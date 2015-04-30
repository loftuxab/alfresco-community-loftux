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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.alfresco.module.org_alfresco_module_cloud.analytics.AnalyticsProperties;
import org.alfresco.util.GUID;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;

/**
 * SimpleDB implementation of the Analytics Data Service. Stores events into
 * local data store which we can run reports on outside 3rd party services
 * 
 * @author David Gildeh
 */
public class SimpleDBDataService implements AnalyticsDataService
{
    // Log4J Logger
    private static final Log logger = LogFactory
            .getLog(SimpleDBDataService.class);

    // SimpleDB Domain Name
    private String simpleDBDomain;

    // API Keys
    private String accessKey;
    private String secretKey;

    // Service Name
    private String serviceName = "";

    // Controls whether the service is enabled or not
    // This is useful to switch off on dev/test environments - false by default
    private boolean isEnabled = false;

    // SimpleDB Connector
    private AmazonSimpleDB simpleDB;
    
    /**
     * Set the SimpleDB domain to store events in for the current environment
     * 
     * @param simpleDBDomain
     *          The AWS SimpleDB Domain
     */
    public void setSimpleDBDomain(String simpleDBDomain) 
    {
        this.simpleDBDomain = simpleDBDomain;
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_cloud.analytics.services.AnalyticsDataService#setApiKey(java.lang.String)
     */
    @Override
    public void setApiKey(String apiKey)
    {
        // Expected in format {accessKey}|{secretKey}
        String[] keys = apiKey.split("\\|");
        this.accessKey = keys[0];
        this.secretKey = keys[1];

        // Initialise SimpleDB Connector
        simpleDB = new AmazonSimpleDBClient(new BasicAWSCredentials(accessKey,
                secretKey));
        // Ensure data is US-EAST Region
        simpleDB.setEndpoint("sdb.amazonaws.com");
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_cloud.analytics.services.AnalyticsDataService#getApiKey()
     */
    @Override
    public String getApiKey()
    {
        // Return {accessKey}|{secretKey}
        return accessKey + "|" + secretKey;
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_cloud.analytics.services.AnalyticsDataService#setServiceName(java.lang.String)
     */
    @Override
    public void setServiceName(String serviceName)
    {
        this.serviceName = serviceName;
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_cloud.analytics.services.AnalyticsDataService#getServiceName()
     */
    @Override
    public String getServiceName()
    {
        return serviceName;
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_cloud.analytics.services.AnalyticsDataService#setIsEnabled(java.lang.String)
     */
    @Override
    public void setIsEnabled(String isEnabled)
    {
        this.isEnabled = Boolean.parseBoolean(isEnabled);

    }

    /**
     * @see org.alfresco.module.org_alfresco_module_cloud.analytics.services.AnalyticsDataService#isEnabled()
     */
    @Override
    public boolean isEnabled()
    {
        return isEnabled;
    }

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
            try
            {

                if (logger.isDebugEnabled())
                {
                    logger.debug("Recording event in SimpleDB for " + uid
                            + ": " + event);
                }

                // Create Event Data Item
                List<ReplaceableAttribute> attributes = new ArrayList<ReplaceableAttribute>();
                
                // Get time in Seconds to match PHP Unix time
                long time = new Date().getTime() / 1000;
                
                // Add uid, event and time attributes
                attributes.add(new ReplaceableAttribute("uid", uid, false));
                attributes.add(new ReplaceableAttribute("event", event, false));
                attributes.add(new ReplaceableAttribute("time", String.valueOf(time), false));

                // Loop through HashMap properties to generate attributes
                for (String key : props.getKeys())
                {
                    Object value = props.get(key);
                    attributes.add(new ReplaceableAttribute(key, String.valueOf(value), false));
                }

                // Put it into SimpleDB
                simpleDB.putAttributes(new PutAttributesRequest(simpleDBDomain,
                        GUID.generate(), attributes));

            } catch (AmazonServiceException ase) 
            {
                logger.error("Caught an AmazonServiceException, which means your request made it "
                        + "to Amazon SimpleDB, but was rejected with an error response for some reason.", ase);
            } catch (AmazonClientException ace) 
            {
                logger.error("Caught an AmazonClientException, which means the client encountered "
                        + "a serious internal problem while trying to communicate with SimpleDB, "
                        + "such as not being able to access the network.", ace);
            }
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
        // Not required so return null
        return null;
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
        // Not supported so return null;
        return null;
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_cloud.analytics.services.AnalyticsDataService#sendAnalyicsRequest(java.lang.String)
     */
    @Override
    public GetMethod sendAnalyicsRequest(String url)
    {
        // SimpleDB doesn't use GET Requests so return null
        return null;
    }
}
