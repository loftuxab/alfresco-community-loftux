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
package org.alfresco.module.org_alfresco_module_wcmquickstart.webscript;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * Webscript that fixes up webasset properties following model changes
 * 
 * @author Brian Remmington
 */
public class FixWebAssets1 extends DeclarativeWebScript implements WebSiteModel
{
	/** Node Service */
	private NodeService nodeService;
	
	/** Search Service */
	private SearchService searchService;
	/**
	 * @see org.springframework.extensions.webscripts.DeclarativeWebScript#executeImpl(org.springframework.extensions.webscripts.WebScriptRequest, org.springframework.extensions.webscripts.Status, org.springframework.extensions.webscripts.Cache)
	 */
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
	{
	    //Find all nodes with the webasset aspect and populate the publishDate and published properties
	    ResultSet rs = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, SearchService.LANGUAGE_LUCENE, "ASPECT:\"" + ASPECT_WEBASSET + "\"");
	    Date now = new Date();
	    for (ResultSetRow row : rs)
	    {
	        Map<QName,Serializable> props = nodeService.getProperties(row.getNodeRef());
            props.put(PROP_PUBLISHED, Boolean.TRUE);
            props.put(PROP_AVAILABLE_FROM_DATE, now);
            props.put(PROP_PUBLISHED_TIME, now);
            nodeService.setProperties(row.getNodeRef(), props);
            nodeService.addAspect(row.getNodeRef(), ContentModel.ASPECT_TAGGABLE, null);
	    }
        rs = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, SearchService.LANGUAGE_LUCENE, "TYPE:\"" + TYPE_SECTION + "\"");
        for (ResultSetRow row : rs)
        {
            nodeService.addAspect(row.getNodeRef(), ContentModel.ASPECT_TAGSCOPE, null);
        }
	    return new TreeMap<String, Object>();
	}
	
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }
    
}
