/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_cloud.emailaddress.webscripts;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.emailaddress.DomainValidityCheck;
import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;
import org.alfresco.util.ScriptPagingDetails;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This is the webscript controller for the invalid-domains.get webscript.
 * 
 * @author Neil Mc Erlean
 * @since Thor Phase 2 Sprint 1
 */
public class InvalidDomainsGet extends AbstractInvalidDomainWebscript
{
    private static final int DEFAULT_PAGE_SIZE = 128;
    
    @Override protected Map<String, Object> executeImpl(WebScriptRequest req,
            Status status, Cache cache)
    {
        PagingRequest pagingRequest = ScriptPagingDetails.buildPagingRequest(req, DEFAULT_PAGE_SIZE);
        
        PagingResults<DomainValidityCheck> pagingResults = emailAddressService.getInvalidDomains(pagingRequest);
        
        Map<String, Object> domainsData = new HashMap<String, Object>();
        
        int total = -1;
        if (pagingResults.getTotalResultCount() != null)
        {
            total = pagingResults.getTotalResultCount().getFirst();
        }
        
        domainsData.put("total", total);
        domainsData.put("pageSize", pagingRequest.getMaxItems());
        domainsData.put("startIndex", pagingRequest.getSkipCount());
        domainsData.put("itemCount", pagingResults.getPage().size());
        
        domainsData.put("items", pagingResults.getPage());
        
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("data", domainsData);
        
        return model;

    }
}
