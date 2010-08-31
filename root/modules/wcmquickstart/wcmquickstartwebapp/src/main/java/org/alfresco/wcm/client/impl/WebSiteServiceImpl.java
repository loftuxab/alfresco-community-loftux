/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
package org.alfresco.wcm.client.impl;

import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.AssetFactory;
import org.alfresco.wcm.client.SectionFactory;
import org.alfresco.wcm.client.WebSite;
import org.alfresco.wcm.client.WebSiteService;
import org.alfresco.wcm.client.util.CmisSessionHelper;
import org.alfresco.wcm.client.util.UrlUtils;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorService;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.extensions.webscripts.connector.ResponseStatus;
import org.springframework.extensions.webscripts.processor.BaseProcessorExtension;

/**
 * Web site service implementation
 * 
 * @author Roy Wetherall
 * @author Brian Remmington
 */
public class WebSiteServiceImpl extends BaseProcessorExtension implements WebSiteService
{
    private static final Log log = LogFactory.getLog(WebSiteServiceImpl.class);
    /** Query for all web roots */
    private static final String QUERY_WEB_ROOTS = "select f.cmis:objectId, w.ws:hostName, w.ws:hostPort, t.cm:title, t.cm:description "
            + "from cmis:folder as f "
            + "join ws:website as w on w.cmis:objectId = f.cmis:objectId "
            + "join cm:titled as t on t.cmis:objectId = f.cmis:objectId";

    /** Web site cache */
    private Map<String, WebSite> webSiteCache;
    private long webSiteCacheRefeshedAt;

    /** Cache timeout values (seconds) */
    private int webSiteCacheRefreshAfter = 60;
    private int webSiteSectionCacheRefreshAfter = 60;

    private ConnectorService connectorService;
    private SectionFactory sectionFactory;
    private AssetFactory assetFactory;

    private String logoFilename;
	private UrlUtils urlUtils;

    /**
     * Set the number of seconds after which the web site cache will refresh.
     * 
     * @param webSiteCacheRefreshAfter
     *            seconds
     */
    public void setWebSiteCacheRefreshAfter(int webSiteCacheRefreshAfter)
    {
        this.webSiteCacheRefreshAfter = webSiteCacheRefreshAfter;
    }

    /**
     * Set the number of seconds after which the web site section cache will
     * refresh.
     * 
     * @param webSiteSectionCacheRefreshAfter
     *            seconds
     */
    public void setWebSiteSectionCacheRefreshAfter(int webSiteSectionCacheRefreshAfter)
    {
        this.webSiteSectionCacheRefreshAfter = webSiteSectionCacheRefreshAfter;
    }

    /**
     * @see org.alfresco.wcm.client.WebSiteService#getWebSite(java.lang.String,
     *      int)
     */
    public WebSite getWebSite(String hostName, int hostPort)
    {
        return getWebSiteCache().get(hostName + ":" + hostPort);
    }

    /**
     * @see org.alfresco.wcm.client.WebSiteService#getWebSites()
     */
    public Collection<WebSite> getWebSites()
    {
        return getWebSiteCache().values();
    }

    /**
     * Gets the web site cache
     * 
     * @return Map<String, WebSite> map of web sites by host:port
     */
    private Map<String, WebSite> getWebSiteCache()
    {
        if (webSiteCache == null || webSiteCacheExpired() == true)
        {
            Map<String, WebSite> newCache = new HashMap<String, WebSite>(5);

            Session session = CmisSessionHelper.getSession();

            // Execute query
            if (log.isDebugEnabled())
            {
                log.debug("About to run CMIS query: " + QUERY_WEB_ROOTS);
            }            
            ItemIterable<QueryResult> results = session.query(QUERY_WEB_ROOTS, false);
            for (QueryResult result : results)
            {
                // Get the details of the returned object
                String id = result.getPropertyValueById(PropertyIds.OBJECT_ID);
                String hostName = result.getPropertyValueById(WebSite.PROP_HOSTNAME);
                BigInteger hostPort = result.getPropertyValueById(WebSite.PROP_HOSTPORT);
                String key = hostName + ":" + hostPort.toString();
                String title = result.getPropertyValueById(Asset.PROPERTY_TITLE);
                String description = result.getPropertyValueById(Asset.PROPERTY_DESCRIPTION);

                WebsiteInfo siteInfo = getWebsiteInfo(id);

                WebSiteImpl webSite = new WebSiteImpl(id, hostName, hostPort.intValue(),
                        webSiteSectionCacheRefreshAfter);
                webSite.setRootSectionId(siteInfo.rootSectionId);
                webSite.setTitle(title);
                webSite.setDescription(description);
                webSite.setSectionFactory(sectionFactory);
                UgcServiceCmisImpl ugcService = new UgcServiceCmisImpl(session
                        .createObjectId(siteInfo.feedbackFolderId));
                webSite.setUgcService(ugcService);
                newCache.put(key, webSite);

                // Find the logo asset id
                Asset logo = assetFactory.getSectionAsset(siteInfo.rootSectionId, logoFilename, true);
                webSite.setLogo(logo);
                
                webSite.setUrlUtils(urlUtils);
            }

            webSiteCacheRefeshedAt = System.currentTimeMillis();
            webSiteCache = newCache;
        }
        return webSiteCache;
    }

    private WebsiteInfo getWebsiteInfo(String websiteid)
    {
        String feedbackFolderId = websiteid;
        String rootSectionId = websiteid;
        try
        {
            // Query the named collection under the collections folder
            Connector connector = connectorService.getConnector("alfresco-qs");

            String uri = "/api/websiteinfo?websiteid=" + URLEncoder.encode(websiteid, "UTF-8");
            if (log.isDebugEnabled())
            {
                log.debug("About to call... " + uri);
            }
            Response response = connector.call(uri);
            if ((response != null) && (response.getStatus().getCode() == ResponseStatus.STATUS_OK))
            {
                String jsonString = response.getText();
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONObject data = (JSONObject) jsonObject.get("data");
                feedbackFolderId = data.getString("feedbackfolderid");
                rootSectionId = data.getString("rootsectionid");
            } else
            {
                if (log.isWarnEnabled())
                {
                    log.warn("Received unexpected response when requesting feedback folder for site "
                            + websiteid + ": " + response);
                }
            }
        } catch (Exception ex)
        {
            log.error("Error while attempting to retrieve feedback folder for website " + websiteid, ex);
        }
        return new WebsiteInfo(rootSectionId,feedbackFolderId);
    }

    /**
     * Indicates whether the web site cache has expired.
     * 
     * @return boolean true if expired, false otherwise
     */
    private boolean webSiteCacheExpired()
    {
        boolean result = true;
        long now = System.currentTimeMillis();
        long difference = now - webSiteCacheRefeshedAt;
        long calcValue = webSiteCacheRefreshAfter * 1000;
        if (difference <= calcValue)
        {
            result = false;
        }
        return result;
    }

    /**
     * Set the logo image filename pattern, eg logo.%
     * 
     * @param logo
     */
    public void setLogoFilename(String logoFilename)
    {
        this.logoFilename = logoFilename;
    }

    public void setSectionFactory(SectionFactory sectionFactory)
    {
        this.sectionFactory = sectionFactory;
    }

    public void setAssetFactory(AssetFactory assetFactory)
    {
        this.assetFactory = assetFactory;
    }

    public void setConnectorService(ConnectorService connectorService)
    {
        this.connectorService = connectorService;
    }

    	
	public void setUrlUtils(UrlUtils urlUtils) {
		this.urlUtils = urlUtils;
	}    

    public final static class WebsiteInfo
    {
        public final String rootSectionId;
        public final String feedbackFolderId;

        public WebsiteInfo(String rootSectionId, String feedbackFolderId)
        {
            this.rootSectionId = rootSectionId;
            this.feedbackFolderId = feedbackFolderId;
        }
    }
}
