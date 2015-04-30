/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.module.org_alfresco_module_cloud.analytics;

import org.alfresco.events.types.Event;
import org.alfresco.module.org_alfresco_module_cloud.analytics.action.SendAnalyticsRequest;
import org.alfresco.repo.events.EventPreparator;
import org.alfresco.repo.events.EventPublisher;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.util.FileFilterMode;
import org.alfresco.util.FileFilterMode.Client;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.springframework.beans.factory.InitializingBean;

/**
 * Static class implementation of the Analytics Service which is called by other
 * Java classes to raise analytics events for tracking/reporting usage of the
 * application
 * 
 * Basic Usage:
 * 
 * To record a tracking event use the following example code when tracking the
 * current fully authenticated user:
 * 
 * Analytics.recordEvent(AnalyticsEvent.LOGIN, hashMapProps, true);
 * 
 * @author David Gildeh
 * 
 */
public class Analytics implements InitializingBean
{
    // Log4J Logger
    private static final Log logger = LogFactory.getLog(Analytics.class);

    // Alfresco Services
    private static ActionService actionService;
    private EventPublisher eventPublisher;

    // Have Spring settings been initialized?
    private static boolean initialized = false;
    private static Analytics instance = null;
    // Set whether the service is enabled or not
    // This is useful to switch off on dev/test environments - false by default
    private static boolean isEnabled = false;
    
    // Enumeration to capture an invite response
    public static enum SiteInviteResponse
    {
        ACCEPTED, REJECTED, IGNORED;
    }
    
    /**
     * Set the Alfresco Action Service
     * 
     * @param actionService
     *            Alfresco Action Service
     */
    public void setActionService(ActionService actionService)
    {
        Analytics.actionService = actionService;
    }


    /**
     * Set the event publisher
     * @param eventPublisher
     */
    public void setEventPublisher(EventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        // At this point all services have been setup
        initialized = true;
        logger.info("Alfresco Cloud Analytics Service is Initialized");
        instance = this;
    }

    /**
     * Set whether the Analytics Service is enabled or not. If not all calls to
     * the service to record events/properties will do nothing
     * 
     * @param isEnabled
     *            String boolean so it can be set easily by Spring Config
     */
    public void setIsEnabled(String isEnabled)
    {
        Analytics.isEnabled = Boolean.parseBoolean(isEnabled);

        if (Analytics.isEnabled)
        {
            logger.info("Alfresco Cloud Analytics service is enabled.");
        }
    }

    /**
     * Get whether the Analytics Service is enabled or not. If not all calls to
     * the service to record events/properties will do nothing
     * 
     * @return isEnabled
     */
    public static boolean isEnabled()
    {
        return Analytics.isEnabled;
    }

    /**
     * Records a tracking event for a currently authenticated user along with
     * any related properties
     * 
     * @param event
     *            The event name
     * @param props
     *            A key/value property map to record with event
     * @param runAsynch
     *            True - send request Asynchronously, False - send request
     *            Synchronously
     */
    private static void recordEvent(AnalyticsEvent event,
            AnalyticsProperties props, boolean runAsynch)
    {
        // Ensure null uid's are passed as empty strings so action called 
        // correctly. null value appears to stop action being called
        String uid = AuthenticationUtil.getFullyAuthenticatedUser() != null 
                ? AuthenticationUtil.getFullyAuthenticatedUser() : "";
        
        recordEvent(uid, event, props, runAsynch);
    }

    /**
     * Records a tracking event for a specific user along with any related
     * properties
     * 
     * @param uid
     *            The user's ID (email) to identify the user
     * @param event
     *            The event name
     * @param props
     *            A key/value property map to record with event
     * @param runAsynch
     *            True - send request Asynchronously, False - send request
     *            Synchronously
     */
    private static void recordEvent(final String uid, final AnalyticsEvent event,
            final AnalyticsProperties props, boolean runAsynch)
    {
        
        if (Analytics.isEnabled && Analytics.initialized)
        {   

            // Create Action and set parameters
            Action analyticsAction = actionService
                    .createAction(SendAnalyticsRequest.NAME);
            analyticsAction.setParameterValue(SendAnalyticsRequest.PARAM_UID,
                    uid);
            analyticsAction.setParameterValue(SendAnalyticsRequest.PARAM_EVENT,
                    event.toString());
            analyticsAction.setParameterValue(SendAnalyticsRequest.PARAM_PROPS,
                    props.toString());
            analyticsAction.setParameterValue(
                    SendAnalyticsRequest.PARAM_SET_PROPERTY_REQUEST,
                    new Boolean(false));
            
            //Nasty to get around the use of static methods in a Spring bean instance
            if (instance != null && instance.eventPublisher != null)
            {
                instance.eventPublisher.publishEvent(new EventPreparator(){
                    @Override
                    public Event prepareEvent(String user, String networkId, String transactionId)
                    {
                        return new CloudAnalyticsEvent(CloudAnalyticsEvent.CLOUD_PREFIX+event, uid, networkId, props.toString());
                    }
                });
            }
            // Execute action
            actionService
                    .executeAction(analyticsAction, null, false, runAsynch);
        }
    }

    // These two private methods were commented because they are not used.
    //
    // /**
    // * Sets specific properties for the current authenticated user without
    // * recording a tracking event. These properties can be used to segment
    // * groups of users in reports
    // *
    // * @param props
    // * A key/value property map to record with event
    // * @param runAsynch
    // * True - send request Asynchronously, False - send request
    // * Synchronously
    // */
    // private static void setUserProperty(AnalyticsProperties props,
    // boolean runAsynch)
    // {
    // // Ensure null uid's are passed as empty strings so action called
    // // correctly. null value appears to stop action being called
    // String uid = AuthenticationUtil.getFullyAuthenticatedUser() != null
    // ? AuthenticationUtil.getFullyAuthenticatedUser() : "";
    //
    // setUserProperty(uid, props, runAsynch);
    // }
    //
    // /**
    // * Sets specific properties for a specific user without recording a
    // tracking
    // * event. These properties can be used to segment groups of users in
    // reports
    // *
    // * @param uid
    // * The user's ID (email) to identify the user
    // * @param props
    // * A key/value property map to set against the user
    // * @param runAsynch
    // * True - send request Asynchronously, False - send request
    // * Synchronously
    // */
    // private static void setUserProperty(String uid, AnalyticsProperties
    // props,
    // boolean runAsynch)
    // {
    //
    // if (Analytics.isEnabled && Analytics.initialized)
    // {
    //
    // // Create Action and set parameters
    // Action analyticsAction = actionService
    // .createAction(SendAnalyticsRequest.NAME);
    // analyticsAction.setParameterValue(SendAnalyticsRequest.PARAM_UID,
    // uid);
    // analyticsAction.setParameterValue(SendAnalyticsRequest.PARAM_PROPS,
    // props.toString());
    // analyticsAction.setParameterValue(
    // SendAnalyticsRequest.PARAM_SET_PROPERTY_REQUEST,
    // new Boolean(true));
    //
    // // Execute action
    // actionService
    // .executeAction(analyticsAction, null, false, runAsynch);
    // }
    // }

    /**********************************************************************
     * Use these methods to raise standard events
     */
    
    /**
     * Record a registration (signup) for when a user signs up for a new account using the
     * Cloud landing page.
     * 
     * NOTE: This method is provided for testing purposes and shouldn't be used
     * in production. This event will be generated from the Drupal website instead
     * 
     * @param email
     *            The user's first name
     * @param source
     *            The source of the signup, e.g. mobile app, website, web app etc.
     * @param sourceUrl if source is "website", then a value can be provided for that website's URL e.g. "cloud.alfresco.com"
     * @param ip_address
     *            The IP address of where the user signed up from
     * @param landing_time
     *            The UNIX time (in seconds) the user originally landed on the website
     * @param landing_referrer
     *            The landing referral URL where the user came from
     * @param landing_page
     *            The landing page the user first arrived on the website
     * @param landing_keywords
     *            Comma separated keywords if the user arrived from a search engine
     * @param utm_source
     *            Google 'source' identifier
     * @param utm_medium
     *            Google 'medium' identifier
     * @param utm_term
     *            Google 'term' identifier
     * @param utm_content
     *            Google 'content' identifier
     * @param utm_campaign
     *            Google 'campaign' identifier
     */
    public static void record_Registration(String email, String source, String sourceUrl, String ip_address,
            Long landing_time, String landing_referrer, String landing_page, String landing_keywords, 
            String utm_source, String utm_medium, String utm_term, String utm_content,
            String utm_campaign)
    {

        AnalyticsProperties props = new AnalyticsProperties();
        try
        {
            props.put("mp_name_tag", email);
            props.put("source", source);
            props.put("sourceUrl", sourceUrl);
            props.put("ip", ip_address);
            props.put("landing_time", landing_time);
            props.put("landing_referrer", landing_referrer);
            props.put("landing_page", landing_page);
            props.put("landing_keywords", landing_keywords);
            props.put("utm_source", utm_source);
            props.put("utm_medium", utm_medium);
            props.put("utm_term", utm_term);
            props.put("utm_content", utm_content);
            props.put("utm_campaign", utm_campaign);
        } catch (JSONException je)
        {
            logger.error("JSONException: " + je.getMessage(), je);
        }
        
        recordEvent(email, AnalyticsEvent.SIGNED_UP, props, true);
    }
    
    /**
     * Record an activation (registration) event for when a user registers after getting their
     * signup email and creates their new account
     * 
     * @param firstName
     *            The user's first name
     * @param lastName
     *            The user's last name
     * @param email
     *            The user's email
     */
    public static void record_Activation(String email)
    {

        AnalyticsProperties props = new AnalyticsProperties();
        try
        {
            // Add MixPanel User Streams & Retention Birth Dates
            props.put("mp_name_tag", email);
        } catch (JSONException je)
        {
            logger.error("JSONException: " + je.getMessage(), je);
        }

        recordEvent(email, AnalyticsEvent.REGISTERED, props, true);
        // Also record new $born event for MixPanel 
        // (http://blog.mixpanel.com/2011/11/09/upcoming-changes-to-retention-on-nov-21)
        props = new AnalyticsProperties();
        recordEvent(email, AnalyticsEvent.BORN, props, true);
    }
    
    /**
     * Record a login event for retention measuring
     * 
     */
    public static void record_login(String username)
    {
        
        AnalyticsProperties props = new AnalyticsProperties();
        try
        {
            props.put("mp_name_tag", username);
        } catch (JSONException je)
        {
            logger.error("JSONException: " + je.getMessage(), je);
        }
        recordEvent(username, AnalyticsEvent.LOGIN, props, true);
    }

    /**
     * Record a create site event
     * 
     * @param siteTemplate 
     *            The site template used to create the site, i.e. collaboration
     * @param visibility
     *            The visibility of the site: PRIVATE, PUBLIC or MODERATED     
     */
    public static void record_createSite(String siteTemplate, SiteVisibility visibility)
    {
        // Convert Site Visibility to String
        String strVisibility = "";
        switch (visibility)
        {
        case PRIVATE:   strVisibility = "PRIVATE";
                        break;
        case PUBLIC:    strVisibility = "PUBLIC";
                        break;
        case MODERATED: strVisibility = "MODERATED";
                        break;
        }
        
        AnalyticsProperties props = new AnalyticsProperties();
        try
        {
            props.put("siteTemplate", siteTemplate);
            props.put("visibility", strVisibility);
        } catch (JSONException je)
        {
            logger.error("JSONException: " + je.getMessage(), je);
        }
        
        recordEvent(AnalyticsEvent.CREATE_SITE, props, true);
    }
    
    /**
     * Record an uploaded document event
     * 
     * @param mimetype
     *            The type of document
     * @param size
     *            The size of the document in Bytes
     * @param isEdit
     *            True - this is a file update, False - This is a file creation
     * @param source
     *            Source type of the event, e.g. "spp" for Sharepoint. If null then the FileFilterMode will
     *            be used (if available) for this value.
     */
    public static void record_UploadDocument(String mimeType, long size,
            boolean isEdit, String source)
    {

        AnalyticsProperties props = new AnalyticsProperties();
        try
        {
            props.put("type", mimeType);
            props.put("size", size);
            props.put("isEdit", isEdit);
            if (source == null)
            {
                source = getUploadSource();
            }
            props.put("source", source);
        } catch (JSONException je)
        {
            logger.error("JSONException: " + je.getMessage(), je);
        }

        recordEvent(AnalyticsEvent.UPLOAD_DOCUMENT, props, true);
    }
    
    /**
     * Record an uploaded document event
     * 
     * @param mimetype
     *            The type of document
     * @param size
     *            The size of the document in Bytes
     * @param isEdit
     *            True - this is a file update, False - This is a file creation
     */
    public static void record_UploadDocument(String mimeType, long size,
            boolean isEdit)
    {
        record_UploadDocument(mimeType, size, isEdit, null);
    }
    
    /**
     * Retrieve the source of an an upload event, e.g. WebDAV or CMIS. The source
     * is one of the clients defined by {@link Client}, or null if not set.
     * 
     * @return Client name or null.
     */
    private static String getUploadSource()
    {
        Client source = FileFilterMode.getClient();
        if (source != null)
        {
            return source.toString();
        }
        return null;
    }

    /**
     * Record a send site invite event
     * 
     * @param isNewUser
     *            Is the invitee a new user that doesn't exist on the system yet?
     *            true - yes, false - no
     * @param isExternal
     *            Is the invitee an external user to the inviter's account?
     *            true - yes, false - no
     */
    public static void record_SiteInvite(boolean isNewUser, boolean isExternal)
    {

        AnalyticsProperties props = new AnalyticsProperties();
        try
        {
            props.put("isNewUser", isNewUser);
            props.put("isExternal", isExternal);
        } catch (JSONException je)
        {
            logger.error("JSONException: " + je.getMessage(), je);
        }

        recordEvent(AnalyticsEvent.SEND_SITE_INVITE, props, true);
    }
    
    /**
     * Record a response event for a site invite
     * 
     * @param inviter
     *            The username (email) of the user sending the invite
     * @param response
     *            The response to the invite: "ACCEPTED", "REJECTED", "IGNORED"
     *            Ignored is used when workflow expires
     * @param isNewUser
     *            Is the invitee a new user that doesn't exist on the system yet?
     *            true - yes, false - no
     * @param isExternal
     *            Is the invitee an external user to the inviter's account?
     *            true - yes, false - no
     */
    public static void record_SiteInviteResponse(String inviter, SiteInviteResponse response, 
            boolean isNewUser, boolean isExternal)
    {
        // Convert response to String
        String strResponse = "";
        switch (response)
        {
        case ACCEPTED: strResponse = "ACCEPTED";
                       break;
        case REJECTED: strResponse = "REJECTED";
                       break;
        case IGNORED:  strResponse = "IGNORED";
                       break;
        }
        
        AnalyticsProperties props = new AnalyticsProperties();
        try
        {
            props.put("inviter", inviter);
            props.put("response", strResponse);
            props.put("isNewUser", isNewUser);
            props.put("isExternal", isExternal);
        } catch (JSONException je)
        {
            logger.error("JSONException: " + je.getMessage(), je);
        }

        recordEvent(inviter, AnalyticsEvent.SITE_INVITE_RESPONSE, props, false);
    } 
    
    public static void record_SAML_OnOff(boolean isEnabled)
    {
        // CLOUD-1198 - add analytics [...] for enable/disable SAML SSO
        // Event: 'enableSAMLSSO', properties: none outside standard uid/aid captured for all events, called when SAML SSO is enabled for a Network 
        // Event: 'disableSAMLSSO', properties: none outside standard uid/aid captured for all events, called when SAML SSO is disabled for a Network
        
        AnalyticsProperties props = new AnalyticsProperties();
        
        recordEvent(isEnabled ? AnalyticsEvent.SAML_ENABLED : AnalyticsEvent.SAML_DISABLED, props, true);
    } 

    /**
     * Record a send site invite event
     * 
     * @param isNewUser
     *            Is the invitee a new user that doesn't exist on the system yet?
     *            true - yes, false - no
     * @param isExternal
     *            Is the invitee an external user to the inviter's account?
     *            true - yes, false - no
     */
    public static void record_SharepointSessionStart(String vtiVersion, String userAgent)
    {

        AnalyticsProperties props = new AnalyticsProperties();
        try
        {
            props.put("vtiVersion", vtiVersion);
            props.put("userAgent", userAgent);
        } catch (JSONException je)
        {
            logger.error("JSONException: " + je.getMessage(), je);
        }

        recordEvent(AnalyticsEvent.SHAREPOINT_SESSION_START, props, true);
    }
}
