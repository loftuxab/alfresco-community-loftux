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

import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.directory.DirectoryService;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.EmailAddressService;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationService;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.springframework.extensions.webscripts.DeclarativeWebScript;

/**
 * This class provides some basic functionality reused by the various Cloud Account Self-signup webscripts.
 * @author Neil Mc Erlean
 */
public class AbstractAccountSignupWebscript extends DeclarativeWebScript
{
    protected static final String PARAM_DATA = "data";
    protected static final String PARAM_EMAIL = "email";
    protected static final String PARAM_FIRST_NAME = "firstName";
    protected static final String PARAM_LAST_NAME = "lastName";
    protected static final String PARAM_PASSWORD = "password";
    protected static final String PARAM_ID = "id";
    protected static final String PARAM_KEY = "key";
    
    private RegistrationService registrationService;
    private AccountService accountService;
    private WorkflowService workflowService;
    private DirectoryService directoryService;
    private EmailAddressService emailAddressService;

    public void setRegistrationService(RegistrationService registrationService)
    {
        this.registrationService = registrationService;
    }

    public void setDirectoryService(DirectoryService directoryService)
    {
        this.directoryService = directoryService;
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

    protected DirectoryService getDirectoryService()
    {
        return this.directoryService;
    }

    protected EmailAddressService getEmailAddressService()
    {
        return this.emailAddressService;
    }
    
}