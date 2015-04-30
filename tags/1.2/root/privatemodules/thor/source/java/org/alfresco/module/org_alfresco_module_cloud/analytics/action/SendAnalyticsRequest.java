/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_cloud.analytics.action;

import java.util.Iterator;
import java.util.List;

import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.analytics.AnalyticsProperties;
import org.alfresco.module.org_alfresco_module_cloud.analytics.services.AnalyticsDataService;
import org.alfresco.module.org_alfresco_module_cloud.directory.DirectoryService;
import org.alfresco.module.org_alfresco_module_cloud.directory.InvalidEmailAddressException;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;

/**
 * Action that compiles and sends an analytics request to the analytics services
 * we are using
 * 
 * @author David Gildeh
 */
public class SendAnalyticsRequest extends ActionExecuterAbstractBase
{

    /** Initialize Log4J **/
    private static Log logger = LogFactory.getLog(SendAnalyticsRequest.class);

    // Alfresco Services
    private AccountService accountService;
    private DirectoryService directoryService;

    /*
     * List of Analytics Data Services configured for Analytics Service to call
     * for each event
     */
    private static List<AnalyticsDataService> analyticsDataServices;

    /* Have Spring settings been initialized? */
    private static boolean initialized = false;

    /** Action name and parameters */
    public static final String NAME = "send-analytics-request";
    public static final String PARAM_UID = "uid";
    public static final String PARAM_EVENT = "event";
    public static final String PARAM_PROPS = "event_props";
    public static final String PARAM_SET_PROPERTY_REQUEST = "set_property_request";

    /**
     * Set the Cloud Account Service
     * 
     * @param accountService
     *            The Cloud Account Service
     */
    public void setAccountService(AccountService accountService)
    {
        this.accountService = accountService;
    }

    /**
     * Set the Cloud Directory Service
     * 
     * @param directoryService
     *            The Cloud Directory Service
     */
    public void setDirectoryService(DirectoryService directoryService)
    {
        this.directoryService = directoryService;
    }

    /**
     * Set the list of analytics data services available
     * 
     * @param analyticsDataServices
     *            List of Analytics Data Services
     */
    public void setAnalyticsDataServices(
            List<AnalyticsDataService> analyticsDataServices)
    {

        SendAnalyticsRequest.analyticsDataServices = analyticsDataServices;

        // Record Settings in Log at Startup
        Iterator<AnalyticsDataService> iterator = analyticsDataServices
                .iterator();
        while (iterator.hasNext())
        {
            AnalyticsDataService dataService = iterator.next();
            logger.info("Analytics Data Service Initialised: "
                    + dataService.getServiceName() + " isEnabled: "
                    + String.valueOf(dataService.isEnabled()) + " apiKey: "
                    + dataService.getApiKey());
        }

        SendAnalyticsRequest.initialized = true;
    }

    /**
     * Gets the list of Analytic Data Services setup via Spring. Returns null if
     * service hasn't been initialized
     * 
     * @return analyticsDataServices
     */
    public static List<AnalyticsDataService> getAnalyticsDataServices()
    {
        if (SendAnalyticsRequest.initialized)
        {
            return SendAnalyticsRequest.analyticsDataServices;
        } else
        {
            return null;
        }
    }

    /**
     * @see org.alfresco.repo.action.executer.ActionExecuterAbstractBase#executeImpl(org.alfresco.service.cmr.action.Action,
     *      org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef)
    {   
        try
        {

            // Get Action Parameters
            String uid = (String) action.getParameterValue(PARAM_UID);
            String event = (String) action.getParameterValue(PARAM_EVENT);
            AnalyticsProperties props = new AnalyticsProperties(
                    (String) action.getParameterValue(PARAM_PROPS));
            boolean set_usr_prop_req = ((Boolean) action
                    .getParameterValue(PARAM_SET_PROPERTY_REQUEST))
                    .booleanValue();
            
            // Note that in some scenarios the user will not exist.
            // 1. The event is a user signup (registration, not activation). Then the user never exists.
            // 2. The event is taken from the asynchronous action execution queue but the user has already been deleted. (more likely in test code)
            boolean userExists = false;
            
            // Check that the UID is an email address as 'admin' may be uid and throw an InvalidEmailAddressException
            if (uid.indexOf("@") != -1) {
                userExists = directoryService.userExists(uid);
            }
            
            // Set the user's account ID and apply to every event
            // Only do this if aid isn't set already
            if (props.get("aid") == null && userExists) 
            {
                Account account = null;
                
                try
                {
                    Long homeAccount = directoryService.getHomeAccount(uid);
                    account = accountService.getAccount(homeAccount);
                }
                catch (InvalidEmailAddressException noSuchUser)
                {
                    // If the user has been activated and deleted before this action is run asynchronously,
                    // then getHomeAccount() above will throw the exception.
                    //
                    // This is an unlikely, but possible scenario in production.
                    // It is common in test.
                    
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("InvalidEmailAddress " + uid);
                    }
                }
                catch (NullPointerException ne)
                {
                    // Will throw null pointer exception if user (like admin) doesn't have a home network
                    // For these just ignore them
                    if (logger.isDebugEnabled()) {
                        logger.debug("No Home Network found for " + uid);
                    }
                }
                
                if (account != null)
                {
                    props.put("aid", String.valueOf(account.getId()));
                }   
            }
            
            if (logger.isDebugEnabled()) {
                logger.debug("UID: " + uid + " | EVENT: " + event + " | SET_PROP?: " + set_usr_prop_req + " | PROPS: " + props.toString());
            }

            if (set_usr_prop_req)
            {
                setUserProperty(uid, props);
            } else
            {
                recordEvent(uid, event, props);
            }
        } catch (JSONException je)
        {
            logger.error("JSON Exception: " + je.getMessage(), je);
        }
    }

    /**
     * @see org.alfresco.repo.action.ParameterizedItemAbstractBase#addParameterDefinitions(java.util.List)
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {

        // name, value type, isManditory, display label, isMultivalued
        paramList.add(new ParameterDefinitionImpl(PARAM_UID,
                DataTypeDefinition.TEXT, true, "UserName", false));
        paramList.add(new ParameterDefinitionImpl(PARAM_EVENT,
                DataTypeDefinition.TEXT, false, "Event Name", false));
        paramList.add(new ParameterDefinitionImpl(PARAM_PROPS,
                DataTypeDefinition.TEXT, true, "Properties", false));
        paramList.add(new ParameterDefinitionImpl(PARAM_SET_PROPERTY_REQUEST,
                DataTypeDefinition.BOOLEAN, true, "Set User Property Request?",
                false));
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
     */
    private void recordEvent(String uid, String event, AnalyticsProperties props)
    {

        // Loop through available data services
        Iterator<AnalyticsDataService> iterator = analyticsDataServices
                .iterator();
        while (iterator.hasNext())
        {

            AnalyticsDataService dataService = iterator.next();

            // Only send requests for enabled services
            if (dataService.isEnabled())
            {
                dataService.recordEvent(uid, event, props);
            }
        }
    }

    /**
     * Sets specific properties for a user without recording a tracking event.
     * These properties can be used to segment groups of users in reports
     * 
     * @param uid
     *            The user's ID (email) to identify the user
     * @param props
     *            A key/value property map to set against the user
     */
    private void setUserProperty(String uid, AnalyticsProperties props)
    {

        // Loop through available data services
        Iterator<AnalyticsDataService> iterator = analyticsDataServices
                .iterator();
        while (iterator.hasNext())
        {

            AnalyticsDataService dataService = iterator.next();

            // Only send requests for enabled services
            if (dataService.isEnabled())
            {
                dataService.setUserProperty(uid, props);
            }
        }
    }
}
