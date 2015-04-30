/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.webscripts;

import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLAuthenticationService;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLConfigAdminService;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLConfigSettings;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;

/**
 * SAML Auth (SSO) - abstract WebScript
 * 
 * @author janv, jkaabimofrad
 * @since Cloud SAML
 */
public abstract class AbstractSAMLAuthWebScript extends DeclarativeWebScript
{
    private final Log logger = LogFactory.getLog(this.getClass());
    
    protected static final String PARAM_NETWORK_TENANT_DOMAIN  = "network_domain";

    
    protected SAMLConfigAdminService samlConfigAdminService;
    protected SAMLAuthenticationService samlAuthenticationService;
    
    public void setSamlAuthenticationService(SAMLAuthenticationService samlAuthenticationService)
    {
        this.samlAuthenticationService = samlAuthenticationService;
    }
    
    public void setSamlConfigAdminService(SAMLConfigAdminService service)
    {
        this.samlConfigAdminService = service;
    }
    
    protected boolean isTenantSamlEnabled(String tenantDomain)
    {
        if (tenantDomain == null || tenantDomain.length() == 0)
        {
            logger.warn("Network/Tenant domain not specified");
            return false;
        }
        
        // check that the Network is enabled/configured for SAML (iDP-control)
        if (! samlConfigAdminService.isEnabled(tenantDomain))
        {
            logger.warn("Network/Tenant domain not SAML-enabled: "+tenantDomain);
            return false;
        }
        
        return true;
    }
    
    protected SAMLConfigSettings getSAMLConfigSettings(String tenantDomain)
    {
        // check that the Network is configured for SAML (iDP-control) else throw 401
        SAMLConfigSettings samlConfigSettings = samlConfigAdminService.getSamlConfigSettings(tenantDomain);
        
        if (samlConfigSettings == null || !(samlConfigSettings.isSsoEnabled()))
        {
            throw new WebScriptException(Status.STATUS_UNAUTHORIZED, "Network/Tenant domain config setting not SAML-enabled: "
                + tenantDomain);
        }

        return samlConfigSettings;
    }
}
