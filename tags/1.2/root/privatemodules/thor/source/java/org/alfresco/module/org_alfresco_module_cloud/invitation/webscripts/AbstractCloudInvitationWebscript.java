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
package org.alfresco.module.org_alfresco_module_cloud.invitation.webscripts;

import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.EmailAddressService;
import org.alfresco.module.org_alfresco_module_cloud.invitation.CloudInvitationService;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationService;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class provides some basic functionality reused by the various Cloud Site Invitation webscripts.
 * @author Neil Mc Erlean
 */
public class AbstractCloudInvitationWebscript extends DeclarativeWebScript
{
    protected static final String VAR_INVITE_ID = "invite_id";
    protected static final String PARAM_DATA = "data";
    protected static final String PARAM_KEY = "key";
    protected static final String PAGE_SIZE = "pageSize";
    
    private RegistrationService registrationService;
    private AccountService accountService;
    private WorkflowService workflowService;
    private EmailAddressService emailAddressService;
    private CloudInvitationService cloudInvitationService;

    public void setCloudInvitationService(CloudInvitationService cloudInvitationService)
    {
        this.cloudInvitationService = cloudInvitationService;
    }
    
    protected CloudInvitationService getCloudInvitationService()
    {
        return this.cloudInvitationService;
    }
    
    public void setRegistrationService(RegistrationService registrationService)
    {
        this.registrationService = registrationService;
    }

    public void setAccountService(AccountService accountService)
    {
        this.accountService = accountService;
    }
    
    public void setWorkflowService(WorkflowService workflowService)
    {
        this.workflowService = workflowService;
    }
    
    public void setEmailAddressService(EmailAddressService emailAddressService)
    {
        this.emailAddressService = emailAddressService;
    }
    
    protected AccountService getAccountService()
    {
        return this.accountService;
    }
    
    protected WorkflowService getWorkflowService()
    {
        return this.workflowService;
    }

    protected RegistrationService getRegistrationService()
    {
        return this.registrationService;
    }

    protected EmailAddressService getEmailAddressService()
    {
        return this.emailAddressService;
    }
    
    /**
     * This method gets the optional parameter 'pageSize' from the request URL parameters.
     * @param req the webscript request.
     * @return the requested pageSize if one was provided. Returns -1 if pageSize was not specified on the URL
     *         or if the value was not parseable as an int.
     */
    protected int getRequestedPageSize(WebScriptRequest req)
    {
        // PageSize is not mandatory
        int requestedPageSize = -1;
        
        String pageSizeString = req.getParameter(PAGE_SIZE);
        if (pageSizeString != null)
        {
            try
            {
                requestedPageSize = Integer.parseInt(pageSizeString);
            }
            catch (NumberFormatException ignored)
            {
                // Intentionally empty
            }
        }
        
        return requestedPageSize;
    }
}