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
package org.alfresco.module.org_alfresco_module_cloud.accounts.webscripts;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;
import org.alfresco.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the controller for the accounts.get web script.
 * 
 * @author Neil Mc Erlean
 * @author janv
 * @since Cloud
 */
public class AccountsGet extends DeclarativeWebScript
{
    private static final Log logger = LogFactory.getLog(AccountsGet.class);
    
    private static final String TEMPLATE_VAR_TYPE_ID = "typeId";
    private static final String TEMPLATE_VAR_SORT_BY = "sortBy";
    
    @SuppressWarnings("unused")
    private static final String TEMPLATE_VAR_SORT_BY_ASC  = "ASC";  // default - if not DESCending
    private static final String TEMPLATE_VAR_SORT_BY_DESC = "DESC";
    
    private AccountService accountService;
    
    public void setAccountService(AccountService accountService)
    {
        this.accountService = accountService;
    }
    
    @SuppressWarnings("boxing")
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        PagingRequest pagingReq = parsePagingParams(req);
        pagingReq.setRequestTotalCountMax(pagingReq.getSkipCount() + 1000);
        
        Integer filterByAccountTypeId = null;
        String filterByAccountTypeIdStr = req.getParameter(TEMPLATE_VAR_TYPE_ID);
        if (filterByAccountTypeIdStr != null)
        {
            filterByAccountTypeId = new Integer(filterByAccountTypeIdStr);
        }
        
        
        Pair<String, Boolean> sortByPair = null;
        String sortByStr = req.getParameter(TEMPLATE_VAR_SORT_BY);
        if ((sortByStr != null) && (sortByStr.length() > 0))
        {
            String[] parts = sortByStr.split(":");
            String sortByField = parts[0];
            boolean ascending = ((parts.length == 1) || (! parts[1].equals(TEMPLATE_VAR_SORT_BY_DESC)));
            sortByPair = new Pair<String, Boolean>(sortByField, ascending);
        }
        
        // note: ignore sort by typeId if filtering by typeId !
        if ((filterByAccountTypeId != null) && (sortByPair != null) && (AccountService.SORT_BY_ACCOUNT_TYPEID.equals(sortByPair.getFirst())))
        {
            sortByPair = null;
            
            if (logger.isWarnEnabled())
            {
                logger.warn("Skip sort by 'typeId' (since filtering by 'typeId')");
            }
        }
        
        PagingResults<Account> pageOfAccounts = accountService.getAccounts(filterByAccountTypeId, sortByPair,  pagingReq);
        
        Map<String, Object> accountsData = new HashMap<String, Object>();
        
        int total = -1;
        if (pageOfAccounts.getTotalResultCount() != null)
        {
            total = pageOfAccounts.getTotalResultCount().getFirst();
        }
        
        accountsData.put("total", total);
        accountsData.put("pageSize", pagingReq.getMaxItems());
        accountsData.put("startIndex", pagingReq.getSkipCount());
        accountsData.put("itemCount", pageOfAccounts.getPage().size());
        
        accountsData.put("items", pageOfAccounts.getPage());
        
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("data", accountsData);
        
        return model;
    }
    
    private PagingRequest parsePagingParams(WebScriptRequest req)
    {
        String startIndexStr = req.getParameter("startIndex");
        String pageSizeStr = req.getParameter("pageSize");
        
        int startIndex = 0;
        int pageSize = 10;
        
        if (startIndexStr != null)
        {
            startIndex = Integer.parseInt(startIndexStr);
        }
        if (pageSizeStr != null)
        {
            pageSize = Integer.parseInt(pageSizeStr);
        }
        return new PagingRequest(startIndex, pageSize, null);
    }

}
