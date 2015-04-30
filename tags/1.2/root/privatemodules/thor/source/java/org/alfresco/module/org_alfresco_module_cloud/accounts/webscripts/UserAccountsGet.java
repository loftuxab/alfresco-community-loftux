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

import org.alfresco.module.org_alfresco_module_cloud.directory.DirectoryService;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationService;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the controller for the account.get web script.
 */
public class UserAccountsGet extends DeclarativeWebScript
{
    private static final String USER_ID = "userId";
    
    private RegistrationService registrationService;
    private DirectoryService directoryService;
    
    public void setRegistrationService(RegistrationService service)
    {
        this.registrationService = service;
    }
    
    public void setDirectoryService(DirectoryService service)
    {
        this.directoryService = service;
    }
    
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
        final String userId = templateVars.get(USER_ID);
        if (userId == null || userId.length() == 0)
        {
            throw new WebScriptException(Status.STATUS_NOT_FOUND, "User not specified");
        }
        
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("defaultAccount", directoryService.getDefaultAccount(userId));
        model.put("homeAccount", registrationService.getHomeAccount(userId));
        model.put("secondaryAccounts", registrationService.getSecondaryAccounts(userId));
        return model;
    }
}
