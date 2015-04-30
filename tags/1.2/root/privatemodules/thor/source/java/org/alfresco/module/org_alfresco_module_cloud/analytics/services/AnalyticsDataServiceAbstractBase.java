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

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract class that all 3rd party services extend to provide standard way of
 * generating events and raising them
 * 
 * @author David Gildeh
 */
public abstract class AnalyticsDataServiceAbstractBase implements
        AnalyticsDataService
{

    // Log4J Logger
    private static final Log logger = LogFactory
            .getLog(AnalyticsDataServiceAbstractBase.class);

    // API Key
    private String apiKey;

    // Service Name
    private String serviceName = "";

    // Controls whether the service is enabled or not
    // This is useful to switch off on dev/test environments - false by default
    private boolean isEnabled = false;

    /**
     * @see org.alfresco.module.org_alfresco_module_cloud.analytics.services.AnalyticsDataService#setApiKey(java.lang.String)
     */
    public void setApiKey(String apiKey)
    {
        this.apiKey = apiKey;
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_cloud.analytics.services.AnalyticsDataService#getApiKey()
     */
    public String getApiKey()
    {
        return apiKey;
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_cloud.analytics.services.AnalyticsDataService#setServiceName(java.lang.String)
     */
    public void setServiceName(String serviceName)
    {
        this.serviceName = serviceName;
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_cloud.analytics.services.AnalyticsDataService#getServiceName()
     */
    public String getServiceName()
    {
        return serviceName;
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_cloud.analytics.services.AnalyticsDataService#setIsEnabled(java.lang.String)
     */
    public void setIsEnabled(String isEnabled)
    {
        this.isEnabled = Boolean.parseBoolean(isEnabled);
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_cloud.analytics.services.AnalyticsDataService#isEnabled()
     */
    public boolean isEnabled()
    {
        return isEnabled;
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_cloud.analytics.services.AnalyticsDataService#sendAnalyicsRequest(java.lang.String)
     */
    @Override
    public GetMethod sendAnalyicsRequest(String url)
    {

        // Create new HTTPClient
        HttpClient client = new HttpClient();
        // Create a method instance.
        GetMethod method = new GetMethod(url);

        int statusCode = 0;

        if (logger.isDebugEnabled())
        {
            logger.debug("GET Request URL: " + url);
        }

        try
        {
            // Execute the method.
            statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK)
            {
                logger.error("GET Method failed: " + method.getStatusLine());
            } else
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("GET Response: "
                            + method.getResponseBodyAsString());
                }
            }

        } catch (HttpException e)
        {
            logger.error("Fatal protocol violation: " + e.getMessage(), e);
        } catch (IOException e)
        {
            logger.error("Fatal transport error: " + e.getMessage(), e);
        } finally
        {
            // Release the connection.
            method.releaseConnection();
        }

        return method;
    }
}
