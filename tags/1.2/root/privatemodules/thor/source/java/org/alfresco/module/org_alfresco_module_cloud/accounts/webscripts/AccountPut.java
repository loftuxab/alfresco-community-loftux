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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountAdminService;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountImpl;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountRegistry;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountUsages;
import org.alfresco.module.org_alfresco_module_cloud.accounts.domain.AccountEntity;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the controller for the account.put web script.
 * There are two types of Account Updates and TODO at some point we may separate these into two distinct REST APIs.
 * <ol>
 *   <li>A self-contained change to e.g. an account quota. These are implemented via the {@link AccountService}.</li>
 *   <li>A change to Account Type, which can have significant side-effects. These are implemented via the {@link AccountAdminService}.</li>
 * </ol>
 * 
 * @author janv
 * @author Neil Mc Erlean
 * @since Thor
 */
public class AccountPut extends DeclarativeWebScript
{
    private static final String TEMPLATE_VAR_ID = "id";
    private static final String TEMPLATE_VAR_DOMAIN_NAME = "domainName";
    
    private AccountRegistry     accountRegistry;
    private AccountService      accountService;
    private AccountAdminService accountAdminService;
    
    public void setAccountRegistry(AccountRegistry registry)
    {
        this.accountRegistry = registry;
    }
    
    public void setAccountService(AccountService service)
    {
        this.accountService = service;
    }
    
    public void setAccountAdminService(AccountAdminService service)
    {
        this.accountAdminService = service;
    }
    
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        // find account
        Account account = null;
        Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
        final String idString = templateVars.get(TEMPLATE_VAR_ID);
        if (idString != null)
        {
            long id = parseId(idString);
            account = accountService.getAccount(id);
        }
        else
        {
            final String domainName = templateVars.get(TEMPLATE_VAR_DOMAIN_NAME);
            if (domainName != null)
            {
                account = accountService.getAccountByDomain(domainName);
            }
        }
        if (account == null)
        {
            throw new WebScriptException(Status.STATUS_NOT_FOUND, "Account not found");
        }
        
        // As this webscript currently supports both types of update (see javadoc above), we must identify which update type we're performing
        // and route the call to the correct foundation service.
        
        // create account entity representing data to update
        AccountUpdateEntity entity = new AccountUpdateEntity(account.getTenantId(), account.getType().getId(), account.isEnabled());
        entity.setId(account.getId());
        AccountUsages usages = new AccountUsages();
        JSONObject json = null;
        boolean updateRequiresAccountTypeChange = false;
        try
        {
            json = new JSONObject(new JSONTokener(req.getContent().getContent()));
            updateRequiresAccountTypeChange = updateAccountFromJSON(json, entity, usages);
        }
        catch (IOException iox)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not read content from request.", iox);
        }
        catch (JSONException je)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not parse JSON from request.", je);
        }
        catch (NumberFormatException nfe)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not parse data from request.", nfe);
        }
        
        // save any account type change
        if (updateRequiresAccountTypeChange)
        {
            accountAdminService.changeAccountType(account, entity.getType());
        }
        
        // save other changes
        Account accountUpdate = new AccountImpl(accountRegistry, entity, usages, entity.isEnabled());
        Account updatedAccount = accountService.updateAccount(accountUpdate);
        
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("account", updatedAccount);
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
    
    /**
     * @return <tt>true</tt> if the ongoing update requires an Account Type change.
     */
    protected boolean updateAccountFromJSON(JSONObject json, AccountUpdateEntity entity, AccountUsages usages)
            throws JSONException, NumberFormatException
    {
        boolean updateRequiresAccountTypeChange = false;
        
        if (json.has("fileUploadQuota"))
        {
            usages.setFileUploadQuota(new Long(json.getString("fileUploadQuota")));
        }
        
        if (json.has("fileQuota"))
        {
            usages.setFileQuota(new Long(json.getString("fileQuota")));
        }
        
        if (json.has("siteCountQuota"))
        {
            usages.setSiteCountQuota(new Integer(json.getString("siteCountQuota")));
        }
        
        if (json.has("personCountQuota"))
        {
            usages.setPersonCountQuota(new Integer(json.getString("personCountQuota")));
        }
        
        if (json.has("personIntOnlyCountQuota"))
        {
            usages.setPersonIntOnlyCountQuota(new Integer(json.getString("personIntOnlyCountQuota")));
        }
        
        if (json.has("enabled"))
        {
            boolean accountEnabled = json.getBoolean("enabled");
            entity.setEnabled(accountEnabled);
        }
        
        if (json.has("accountTypeId"))
        {
            int requestedAccountTypeId = json.getInt("accountTypeId");
            if (accountRegistry.getType(requestedAccountTypeId) == null)
            {
                throw new WebScriptException(Status.STATUS_BAD_REQUEST, "No such account type: " + requestedAccountTypeId);
            }
            entity.setType(requestedAccountTypeId);
            
            updateRequiresAccountTypeChange = true;
        }
        
        return updateRequiresAccountTypeChange;
    }
 
    private class AccountUpdateEntity extends AccountEntity
    {
        boolean isEnabled;
        
        public AccountUpdateEntity(String domain, int type, boolean isEnabled)
        {
            super(domain, type);
            this.isEnabled = isEnabled;
        }
        
        public boolean isEnabled()
        {
            return isEnabled;
        }
        
        public void setEnabled(boolean enabled)
        {
            this.isEnabled = enabled;
        }
    }
}
