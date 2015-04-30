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
package org.alfresco.module.org_alfresco_module_cloud.users.webscripts;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.networkadmin.NetworkAdmin;
import org.alfresco.module.org_alfresco_module_cloud.networkadmin.NetworkAdmin.NetworkAdminRunAsWork;
import org.alfresco.module.org_alfresco_module_cloud.registration.CannotDemoteLastNetworkAdminException;
import org.alfresco.module.org_alfresco_module_cloud.registration.NoSuchUserException;
import org.alfresco.module.org_alfresco_module_cloud.webscripts.AbstractAccountBasedWebscript;
import org.alfresco.util.ParameterCheck;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the controller for the network-admin.delete web script.
 * 
 * @author Neil Mc Erlean
 * @since Alfresco Cloud Module (Thor)
 */
public class NetworkAdminDelete extends AbstractAccountBasedWebscript
{
    private NetworkAdmin networkAdmin;

    public void setNetworkAdmin(NetworkAdmin networkAdmin)
    {
        this.networkAdmin = networkAdmin;
    }

    private Map<String, Object> networkAdminDelete(WebScriptRequest req, Status status, Cache cache)
    {
        Account account = getAccountFromReq(req);
        
        // Extract the username from the URL. This is the user who is to be demoted.
        Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
        final String username = templateVars.get("username");
        
        if (username == null)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "User not readable from request");
        }
        
        // The username must be a non-empty String.
        ParameterCheck.mandatoryString("username", username);
        
        try
        {
            registrationService.demoteUserFromNetworkAdmin(account.getId(), username);
        }
        catch (CannotDemoteLastNetworkAdminException cdlnae)
        {
            throw new WebScriptException(Status.STATUS_FORBIDDEN, "Cannot demote last remaining NetworkAdmin", cdlnae);
        }
        catch (NoSuchUserException nsue)
        {
            throw new WebScriptException(Status.STATUS_NOT_FOUND, "Unrecognised user");
        }

        
        Map<String, Object> model = new HashMap<String, Object>();
        //TODO
        model.put("success", true);
        
        return model;
    }
    
    @Override
    protected Map<String, Object> executeImpl(final WebScriptRequest req, final Status status, final Cache cache)
    {
        return networkAdmin.runAs(new NetworkAdminRunAsWork<Map<String, Object>>()
        {
        	public Map<String, Object> doWork() throws Exception
        	{
        		return networkAdminDelete(req, status, cache);
        	}
        });
    }
}
