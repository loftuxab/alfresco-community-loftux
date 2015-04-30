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
package org.alfresco.module.org_alfresco_module_cloud.webscripts;

import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationService;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * @author Neil Mc Erlean
 * @since Thor Phase 2 Sprint 1
 */
public class AbstractAccountBasedWebscript extends DeclarativeWebScript
{
    protected AccountService accountService;
    protected RegistrationService registrationService;
    
    public void setAccountService(AccountService service)
    {
        this.accountService = service;
    }
    
    public void setRegistrationService(RegistrationService service)
    {
        this.registrationService = service;
    }
    
    protected Account getAccountFromReq(WebScriptRequest req)
    {
        // Extract the account identifier from the URL.
        Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
        final String idString = templateVars.get("id");
        final String domainName = templateVars.get("domainName");
        
        // We can identify an account either by its ID (a long) or by one of the domains it uses (a String e.g. dept.acme.com)
        boolean usingIdToIdentifyAccount = false;
        
        long id = 0L;
        if (idString != null)
        {
            try
            {
                id = Long.parseLong(idString);
            }
            catch (NumberFormatException nfx)
            {
                throw new WebScriptException(Status.STATUS_NOT_FOUND, "Account not found");
            }
            usingIdToIdentifyAccount = true;
        }
        
        // Retrieve the account info
        Account account = null;
        
        if (usingIdToIdentifyAccount)
        {
            account = accountService.getAccount(id);
        }
        else
        {
            account = accountService.getAccountByDomain(domainName);
        }
        
        if (account == null)
        {
            throw new WebScriptException(Status.STATUS_NOT_FOUND, "Account not found");
        }
        return account;
    }
}
