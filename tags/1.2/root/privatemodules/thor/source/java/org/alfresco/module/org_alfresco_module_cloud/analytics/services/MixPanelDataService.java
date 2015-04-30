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
import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * MixPanel implementation of the Analytics Data Service For full details on
 * MixPanel APIs see: {@link http://mixpanel.com/api/docs/specification}
 * 
 * @author David Gildeh
 */
public class MixPanelDataService extends AnalyticsDataServiceAbstractBase
{

    // Constants
    private static final String BASE_URL = "http://api.mixpanel.com/track/";

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
        String url = BASE_URL + "?data=";

        // Create JSON Data
        JSONObject jsonData = new JSONObject();
        try
        {
            jsonData.put("event", event.toString());
            JSONObject jsonProps = new JSONObject();
            // Setup Token and User ID
            jsonProps.put("token", getApiKey());
            jsonProps.put("distinct_id", uid);

            // Loop through HashMap properties to generate JSON properties
            for (String key : props.getKeys())
            {
                Object value = props.get(key);
                jsonProps.put(key, value);
            }
            jsonData.put("properties", jsonProps);
        } catch (JSONException e)
        {
            // TODO Do nothing for now
        }

        // Base64 Encode JSON String and append to URL
        url += new String(Base64.encodeBase64(jsonData.toString().getBytes()));

        return url;
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_cloud.analytics.services.AnalyticsDataService#setProperty(java.lang.String,
     *      org.alfresco.module.org_alfresco_module_cloud.analytics.AnalyticsProperties)
     */
    @Override
    public void setUserProperty(String uid, AnalyticsProperties props)
    {

        // Not supported so do nothing
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_cloud.analytics.services.AnalyticsDataService#getSetUserPropertyRequestUrl(java.lang.String,
     *      org.alfresco.module.org_alfresco_module_cloud.analytics.AnalyticsProperties)
     */
    @Override
    public String getSetUserPropertyRequestUrl(String uid,
            AnalyticsProperties props)
    {
        return null;
    }
}
