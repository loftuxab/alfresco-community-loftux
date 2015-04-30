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
package org.alfresco.module.org_alfresco_module_cloud.analytics.services;

import org.alfresco.module.org_alfresco_module_cloud.analytics.AnalyticsProperties;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * Interface for any Analytics data service to implement
 * 
 * @author David Gildeh
 */
public interface AnalyticsDataService
{

    /**
     * Sets whether data service is enabled. If so it will send requests to data
     * service, otherwise do nothing
     * 
     * @param isEnabled
     *            String property so it can be set by Spring config
     */
    public void setIsEnabled(String isEnabled);

    /**
     * Get whether data service is enabled. If so it will send requests to data
     * service, otherwise do nothing
     */
    public boolean isEnabled();

    /**
     * Set the API Key required for the service
     * 
     * @param apiKey
     *            API Key for service
     */
    public void setApiKey(String apiKey);

    /**
     * Get the API required for the service
     */
    public String getApiKey();

    /**
     * Set the name of the service. Useful for logging
     * 
     * @param serviceName
     *            The name of the service. E.g. "KissMetrics"
     */
    public void setServiceName(String serviceName);

    /**
     * Returns the service name. Useful for logging
     * 
     * @return serviceName
     */
    public String getServiceName();

    /**
     * Records a tracking event for a particular user along with any related
     * properties
     * 
     * @param uid
     *            The user's ID (email) to identify the user
     * @param event
     *            The event name
     * @param props
     *            A key/value property map to record with event
     */
    public void recordEvent(String uid, String event, AnalyticsProperties props);

    /**
     * Helper method for Testing purposes. Generates the URL Request to record
     * an event for the service
     * 
     * @param uid
     *            The user's ID (email) to identify the user
     * @param event
     *            The event name
     * @param props
     *            A key/value property map to record with event
     * @return The Request URL
     */
    public String getRecordEventRequestUrl(String uid, String event,
            AnalyticsProperties props);

    /**
     * Sets specific properties for a user without recording a tracking event.
     * These properties can be used to segment groups of users in reports
     * 
     * @param uid
     *            The user's ID (email) to identify the user
     * @param props
     *            A key/value property map to set against the user
     */
    public void setUserProperty(String uid, AnalyticsProperties props);

    /**
     * Helper method for Testing purposes. Generates the URL Request to record
     * an event for the service. Returns NULL if method not supported
     * 
     * @param uid
     *            The user's ID (email) to identify the user
     * @param props
     *            A key/value property map to record with event
     * @return The Request URL
     */
    public String getSetUserPropertyRequestUrl(String uid,
            AnalyticsProperties props);

    /**
     * Make a GET request to a service
     * 
     * @param url
     *            The URL to GET
     */
    public GetMethod sendAnalyicsRequest(String url);
}
