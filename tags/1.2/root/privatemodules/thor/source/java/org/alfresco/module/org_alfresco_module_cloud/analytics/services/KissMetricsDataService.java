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

/**
 * KissMetrics implementation of the Analytics Data Service For full details on
 * KissMetrics APIs see: {@link http
 * ://support.kissmetrics.com/apis/specifications}
 * 
 * @author David Gildeh
 */
public class KissMetricsDataService extends AnalyticsDataServiceAbstractBase
{

    // Constants
    private static final String BASE_URL = "http://trk.kissmetrics.com/";
    private static final String EVENT_URI = "e";
    private static final String PROPS_URI = "s";

    /**
     * @see org.alfresco.module.org_alfresco_module_cloud.analytics.services.AnalyticsDataService#recordEvent(java.lang.String,
     *      java.lang.String,
     *      org.alfresco.module.org_alfresco_module_cloud.analytics.AnalyticsProperties)
     */
    @Override
    public void recordEvent(String uid, String event, AnalyticsProperties props)
    {

        // Only run if service is enabled
        if (isEnabled())
        {

            // Generate URL
            String url = getRecordEventRequestUrl(uid, event, props);
            // Finally send request
            sendAnalyicsRequest(url);
        }
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_cloud.analytics.services.AnalyticsDataService#getRecordEventRequestUrl(java.lang.String,
     *      java.lang.String,
     *      org.alfresco.module.org_alfresco_module_cloud.analytics.AnalyticsProperties)
     */
    @Override
    public String getRecordEventRequestUrl(String uid, String event,
            AnalyticsProperties props)
    {

        // Generate URL in format
        String url = BASE_URL + EVENT_URI + "?_k=" + getApiKey();
        url += "&_n=" + event.toString();
        url += "&_p=" + uid;
        url += getPropsUrl(props);

        return url;
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_cloud.analytics.services.AnalyticsDataService#setProperty(java.lang.String,
     *      org.alfresco.module.org_alfresco_module_cloud.analytics.AnalyticsProperties)
     */
    @Override
    public void setUserProperty(String uid, AnalyticsProperties props)
    {

        // Only run if service is enabled
        if (isEnabled())
        {

            // Generate URL
            String url = getSetUserPropertyRequestUrl(uid, props);
            // Finally send request
            sendAnalyicsRequest(url);
        }
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_cloud.analytics.services.AnalyticsDataService#getSetUserPropertyRequestUrl(java.lang.String,
     *      org.alfresco.module.org_alfresco_module_cloud.analytics.AnalyticsProperties)
     */
    @Override
    public String getSetUserPropertyRequestUrl(String uid,
            AnalyticsProperties props)
    {

        // Generate URL in format
        String url = BASE_URL + PROPS_URI + "?_k=" + getApiKey();
        url += "&_p=" + uid;
        url += getPropsUrl(props);

        return url;
    }

    /**
     * Generates a URL String for all the properties sent in properties HashMap
     * 
     * @param props
     *            The Properties
     * @return A URL encoded String
     */
    private String getPropsUrl(AnalyticsProperties props)
    {
        if (props != null)
        {
            StringBuilder sb = new StringBuilder();

            // Loop through HashMap properties to generate URL
            for (String key : props.getKeys())
            {
                String value = String.valueOf(props.get(key));
                // TODO URL encode key/value
                sb.append('&').append(key).append('=').append(value);
            }
            return sb.toString();
        }
        else
        {
            return "";
        }
    }
}
