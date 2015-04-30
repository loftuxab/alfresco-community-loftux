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
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.directory.DirectoryService;
import org.alfresco.module.org_alfresco_module_cloud.networkadmin.NetworkAdmin;
import org.alfresco.module.org_alfresco_module_cloud.networkadmin.NetworkAdmin.NetworkAdminRunAsWork;
import org.alfresco.module.org_alfresco_module_cloud.registration.CannotDemoteLastNetworkAdminException;
import org.alfresco.module.org_alfresco_module_cloud.registration.CannotRemoveUserException;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationService;
import org.alfresco.module.org_alfresco_module_cloud.webscripts.AbstractAccountBasedWebscript;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

//TODO Rename this class and its associated webscript as 'UserDelete' to better reflect its true behaviour.
//TODO I'd do this now, but branching and merging would be made more difficult.
public class RemoveExternalUserDelete extends AbstractAccountBasedWebscript
{
    private static final Log log = LogFactory.getLog(RemoveExternalUserDelete.class);
    
    private static final String USER_ID = "userId";
    private final static String SPLITTED_EMAIL = "^.*@.*[a-fA-F\\d]{8}-([a-fA-F\\d]{4}-){3}[a-fA-F\\d]{12}$";
    private final static Pattern splittedEmailPattern = Pattern.compile(SPLITTED_EMAIL);
    
    private RegistrationService registrationService;
    private DirectoryService directoryService;
    private NetworkAdmin networkAdmin;
    
    public void setNetworkAdmin(NetworkAdmin networkAdmin)
    {
        this.networkAdmin = networkAdmin;
    }
    
    public void setRegistrationService(RegistrationService service)
    {
        this.registrationService = service;
    }
    
    public void setDirectoryService(DirectoryService service)
    {
        this.directoryService = service;
    }
    
    @Override
    protected Map<String, Object> executeImpl(final WebScriptRequest req, final Status status, final Cache cache)
    {
        return networkAdmin.runAs(new NetworkAdminRunAsWork<Map<String, Object>>()
        {
            public Map<String, Object> doWork() throws Exception
            {
                return unprotectedExecuteImpl(req, status, cache);
            }
        });
    }
    
    protected Map<String, Object> unprotectedExecuteImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Account account = getAccountFromReq(req);
        
        // extract non-account arguments
        Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
        final String userId = templateVars.get(USER_ID);
        if (userId == null || userId.length() == 0)
        {
            throw new WebScriptException(Status.STATUS_NOT_FOUND, "User not specified");
        }
        
        removeUserImpl(account.getId(), userId);
        
        Map<String, Object> model = new HashMap<String, Object>();
        
        // If we have just deleted the user from their home account, then they will be gone.
        if (registrationService.isActivatedEmailAddress(userId))
        {
            // I'm reusing a single webscript to perform "user removal from secondary account" and also "user delete" - because the URL is good for both.
            // This has unfortunate consequences here however. It means that in the case of delete, the user will have no account information
            // and yet the FTL layer requires it.
            // FIXME Consider a better fix than this.
            
            // And note that we will not render userExists directly in the REST response.
            model.put("userExists", true);
            
            model.put("defaultAccount", directoryService.getDefaultAccount(userId));
            model.put("homeAccount", registrationService.getHomeAccount(userId));
            model.put("secondaryAccounts", registrationService.getSecondaryAccounts(userId));
        }
        return model;
    }
    
    /**
     * This method performs the user removal, which is different depending on whether the user is internal or external
     * to the specified domain.
     * @param accountId the account from which the user is to be deleted.
     * @param userId    the user id who is to be deleted.
     */
    private void removeUserImpl(long accountId, String userId)
    {
        if (!directoryService.userExists(userId))
        {
            // ACE-1877: Maybe user was splitted
            if (splittedEmailPattern.matcher(userId).matches())
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Removing splitted user " + userId);
                }
                registrationService.deleteSplittedPerson(userId);
                return;
            }
        }
        
        Account homeAccount = registrationService.getHomeAccount(userId);
        
        List<Account> secondaryAccounts = registrationService.getSecondaryAccounts(userId);
        boolean specifiedAccountIsSecondaryAccount = false;
        for (Account acct : secondaryAccounts)
        {
            if (acct.getId() == accountId)
            {
                specifiedAccountIsSecondaryAccount = true;
                break;
            }
        }
        
        try
        {
            if (homeAccount.getId() == accountId)
            {
                // Remove internal user.
                if (log.isDebugEnabled())
                {
                    log.debug("Removing INTERNAL user " + userId + " from account " + accountId);
                }
                
                registrationService.deleteUser(userId);
            }
            else if (specifiedAccountIsSecondaryAccount)
            {
                // Remove external user
                if (log.isDebugEnabled())
                {
                    log.debug("Removing EXTERNAL user " + userId + " from account " + accountId);
                }
                
                registrationService.removeExternalUser(accountId, userId);
            }
            else
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Unable to remove user " + userId + " from account " + accountId + " as they are not a member.");
                }
                
                // Do not return any information that could be used to test if a user exists etc.
                throw new WebScriptException(Status.STATUS_FORBIDDEN, "User cannot be removed.");
            }
        } catch (CannotRemoveUserException crue)
        {
            throw new WebScriptException(Status.STATUS_FORBIDDEN, "User cannot be removed.");
        }
        catch (CannotDemoteLastNetworkAdminException cdlnae)
        {
            throw new WebScriptException(Status.STATUS_FORBIDDEN, "User cannot be removed.");
        }
    }
}
