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

/**
 * Enumeration to ensure only specific events can be created using the Analytics
 * Service. This prevents developers accidentally mistyping event names so
 * reports aggregate metrics consistently
 * 
 * Different events should have different properties send with them as required
 * so events can be segmented and analyzed across the users. For example an
 * UPLOAD_DOCUMENT should include file type and size so we can analyze most
 * common types of files and average size across application
 * 
 * Please check events below and ONLY add new events as required to this list to
 * capture required analytics across the application
 * 
 * @author David Gildeh
 * 
 */
public enum AnalyticsEvent
{

    // Standard SaaS events based on KissMetrics documentation
    // http://support.kissmetrics.com/apis/saas_events

    /* sent every time a person signs up for your service */
    SIGNED_UP("Signed Up"),
    /* sent every time a person upgrades to a higher priced plan */
    UPGRADED("Upgraded"),
    /* sent every time a person downgrades to a lower priced plan */
    DOWNGRADED("Downgraded"),
    /* sent every time a person cancels their service with you */
    CANCELED("Canceled"),
    /* sent every time you charge a person money */
    BILLED("Billed"),
    /* sent every time a person starts a new session with your site */
    VISITED_SITE("Visited Site"),
    
    // MixPanel specific events
    
    /* This is set to do cohort retention analysis */
    BORN("$born"),

    // Alfresco Specific Events - add to this list to record
    // new events as required

    /* sent every time a person registers for service after getting signup email */
    REGISTERED("Registered"),
    /* sent every time a user logs into the application */
    LOGIN("Login"),
    /* sent every time a user sends a site invite to another user */
    SEND_SITE_INVITE("Send Invite"),
    /* sent every time an invite is responded to by user or ignored (workflow expires) */
    SITE_INVITE_RESPONSE("Site Invite Response"),
    /* sent every time a user creates a new site */
    CREATE_SITE("Create Site"),
    /* sent every time a user uploads a document */
    UPLOAD_DOCUMENT("Upload Document"),
    
    /* SAML-enabled for a Network */
    SAML_ENABLED("enableSAMLSSO"),
    /* SAML-disabled for a Network */
    SAML_DISABLED("disableSAMLSSO"),
    
    /* New session negotiated with client */
    SHAREPOINT_SESSION_START("Sharepoint Session Start");
    
    // Holds String name of enumeration to send to analytics data services
    private String eventName = "";

    /**
     * Protected constructor for enumeration
     * 
     * @param eventName
     *            String event name to send to analytics data services
     */
    AnalyticsEvent(String eventName)
    {
        this.eventName = eventName;
    }

    /**
     * Override toString method
     */
    @Override
    public String toString()
    {
        return this.eventName;
    }
}
