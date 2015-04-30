/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.webscripts;

import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountType;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLConfigAdminService;
import org.alfresco.module.org_alfresco_module_cloud.networkadmin.NetworkAdmin;
import org.alfresco.module.org_alfresco_module_cloud.networkadmin.NetworkAdmin.NetworkAdminRunAsWork;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * SAML Config Admin - abstract WebScript
 * 
 * @author janv
 * @since Cloud SAML
 */
public abstract class AbstractSAMLConfigAdminWebScript extends DeclarativeWebScript
{
    protected static final String PARAM_SSO_ENABLED = "ssoEnabled";
    protected static final String PARAM_IDP_SSO_URL = "idpSsoURL";
    protected static final String PARAM_IDP_SLO_REQUEST_URL = "idpSloRequestURL";
    protected static final String PARAM_IDP_SLO_RESPONSE_URL = "idpSloResponseURL";
    protected static final String PARAM_AUTO_PROVISION_ENABLED = "autoProvisionEnabled";
    protected static final String PARAM_ALFRESCO_LOGIN_CREDENTIAL_ENABLED = "alfrescoLoginCredentialEnabled";
    protected static final String PARAM_ENTITY_ID = "entityID";
    protected static final String PARAM_B64_ENCODED_CERTIFICATE = "certificate";
    protected static final String PARAM_CERTIFICATE_INFO = "certificateInfo";
    
    protected SAMLConfigAdminService samlConfigAdminService;
    private NetworkAdmin networkAdmin;
    private AccountService accountService;

    public void setSamlConfigAdminService(SAMLConfigAdminService service)
    {
        this.samlConfigAdminService = service;
    }

    public void setNetworkAdmin(NetworkAdmin networkAdmin)
    {
        this.networkAdmin = networkAdmin;
    }

    public void setAccountService(AccountService accountService)
    {
        this.accountService = accountService;
    }
    
    
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
    
    protected void validateAccount(final String tenantDomain)
    {
        Account account = accountService.getAccountByDomain(tenantDomain);
        if (account != null)
        {
            String level = account.getType().getSubscriptionLevel();
            if ((level == null) || (AccountType.SubscriptionLevel.valueOf(level) != AccountType.SubscriptionLevel.Enterprise))
            {
                // Can only SAML-enable Enterprise Networks
                throw new WebScriptException(Status.STATUS_FORBIDDEN, "Cannot SAML-enabled Network that does not have Enterprise subscription level: "
                    + tenantDomain);
            }
        }
    }
    
    abstract protected Map<String, Object> unprotectedExecuteImpl(WebScriptRequest req, Status status, Cache cache);
    
}
