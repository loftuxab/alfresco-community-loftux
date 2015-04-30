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
package org.alfresco.module.org_alfresco_module_cloud.accounts.webscripts;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the controller for the account.get web script.
 * 
 * @author Neil Mc Erlean
 * @since Alfresco Cloud Module 0.1 (Thor)
 */
public class AccountGet extends DeclarativeWebScript
{
    private static final String TEMPLATE_VAR_ID = "id";
    private static final String TEMPLATE_VAR_DOMAIN_NAME = "domainName";
    
    private AccountService accountService;
    
    public void setAccountService(AccountService accountService)
    {
        this.accountService = accountService;
    }
    
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
        final String idString = templateVars.get(TEMPLATE_VAR_ID);
        final String domainName = templateVars.get(TEMPLATE_VAR_DOMAIN_NAME);
        
        // We can identify an account either by its ID (a long) or by one of the domains it uses (a String e.g. dept.acme.com)
        boolean usingIdToIdentifyAccount = false;
        
        long id = 0L;
        if (idString != null)
        {
            id = parseId(idString);
            usingIdToIdentifyAccount = true;
        }
        
        // Retrieve the account info
        Account accountInfo = null;
        
        if (usingIdToIdentifyAccount)
        {
            accountInfo = accountService.getAccount(id);
        }
        else
        {
            accountInfo = accountService.getAccountByDomain(domainName);
        }
        
        if (accountInfo == null)
        {
            throw new WebScriptException(Status.STATUS_NOT_FOUND, "Account not found");
        }
        
        Map<String, Object> model = new HashMap<String, Object>();
        
        model.put("account", accountInfo);
        model.put("urlContextPath", req.getServiceContextPath());
      
        return model;
    }
    
    private long parseId(String id)
    {
        try
        {
            return Long.parseLong(id);
        }
        catch (NumberFormatException nfx)
        {
            throw new WebScriptException("Malformed account id");
        }
    }
}
