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
package org.alfresco.module.org_alfresco_module_cloud.accounts.webscripts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountRegistry;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountType;
import org.alfresco.util.Pair;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the controller for the accounts.get web script.
 * 
 * @author Neil Mc Erlean
 * @since Thor
 */
public class AccountTypesGet extends DeclarativeWebScript
{
    private static final int DEFAULT_PAGE_START_INDEX = 0;
    private static final int DEFAULT_PAGE_SIZE        = 20;
    
    private AccountRegistry accountRegistry;
    
    public void setAccountRegistry(AccountRegistry accountRegistry)
    {
        this.accountRegistry = accountRegistry;
    }
    
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        // We don't support proper paging beneath this layer.
        // However we can limit what goes over the REST API by exposing paging params.
        Pair<Integer, Integer> startIndexAndPageSize = parsePagingParams(req);
        
        List<AccountType> allAccountTypes = accountRegistry.getTypes();
        
        final int requestedEndIndex = startIndexAndPageSize.getFirst() + startIndexAndPageSize.getSecond();
        final int safeEndIndex = Math.min(requestedEndIndex, allAccountTypes.size());
        
        List<AccountType> pageOfAccounts = allAccountTypes.subList(startIndexAndPageSize.getFirst(), safeEndIndex);
        
        Map<String, Object> accountsData = new HashMap<String, Object>();
        
        int total = allAccountTypes.size();
        accountsData.put("total", total);
        accountsData.put("pageSize", startIndexAndPageSize.getSecond());
        accountsData.put("startIndex", startIndexAndPageSize.getFirst());
        accountsData.put("itemCount", pageOfAccounts.size());
        
        accountsData.put("items", pageOfAccounts);
        
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("data", accountsData);
        
        return model;
    }
    
    /**
     * This method parses the paging parameters from the URL query params.
     * @param req the webscript request.
     * @return a Pair<Integer, Integer> with <startIndex, pageSize>
     */
    private Pair<Integer, Integer> parsePagingParams(WebScriptRequest req)
    {
        String startIndexStr = req.getParameter("startIndex");
        String pageSizeStr = req.getParameter("pageSize");
        
        int startIndex = DEFAULT_PAGE_START_INDEX;
        int pageSize = DEFAULT_PAGE_SIZE;
        
        if (startIndexStr != null)
        {
            try
            {
                int parsedInt = Integer.parseInt(startIndexStr);
                // Prevent negative start index.
                startIndex = parsedInt >= 0 ? parsedInt : 0;
            }
            catch (NumberFormatException ignored) { /* Intentionally empty */ }
        }
        if (pageSizeStr != null)
        {
            try
            {
                int parsedInt = Integer.parseInt(pageSizeStr);
                // prevent negative or zero page size
                pageSize = parsedInt > 0 ? parsedInt : DEFAULT_PAGE_SIZE;
            }
            catch (NumberFormatException ignored) { /* Intentionally empty */ }
        }
        return new Pair<Integer, Integer>(startIndex, pageSize);
    }
}
