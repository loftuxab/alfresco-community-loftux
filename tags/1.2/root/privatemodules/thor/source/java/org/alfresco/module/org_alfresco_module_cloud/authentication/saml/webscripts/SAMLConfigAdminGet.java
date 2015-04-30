/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.webscripts;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLAuthenticationService;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLConfigSettings;
import org.alfresco.repo.tenant.TenantUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * SAML Config Admin GET
 * 
 * This class is the controller for the "saml-config-admin.get" web script.
 * 
 * @author janv, jkaabimofrad
 * @since Cloud SAML
 */
public class SAMLConfigAdminGet extends AbstractSAMLConfigAdminWebScript
{
    private static final Log logger = LogFactory.getLog(SAMLConfigAdminGet.class);
    
    protected SAMLAuthenticationService samlAuthenticationService;
    
    public void setSamlAuthenticationService(SAMLAuthenticationService samlAuthenticationService)
    {
        this.samlAuthenticationService = samlAuthenticationService;
    }
    
    @Override
    protected Map<String, Object> unprotectedExecuteImpl(WebScriptRequest req, Status status, Cache cache)
    {
        String tenantDomain = TenantUtil.getCurrentDomain();
        SAMLConfigSettings samlConfigSettings = samlConfigAdminService.getSamlConfigSettings(tenantDomain);

        Map<String, Object> model = new HashMap<String, Object>();

        model.put(PARAM_SSO_ENABLED, samlConfigSettings.isSsoEnabled());
        model.put(PARAM_IDP_SSO_URL, samlConfigSettings.getIdpSsoURL());
        model.put(PARAM_IDP_SLO_REQUEST_URL, samlConfigSettings.getIdpSloRequestURL());
        model.put(PARAM_IDP_SLO_RESPONSE_URL, samlConfigSettings.getIdpSloResponseURL());
        model.put(PARAM_AUTO_PROVISION_ENABLED, samlConfigSettings.isAutoProvisionEnabled());
        model.put(PARAM_ALFRESCO_LOGIN_CREDENTIAL_ENABLED, samlConfigSettings.isAlfrescoLoginCredentialEnabled());
        model.put(PARAM_ENTITY_ID, samlAuthenticationService.getSpIssuerName(tenantDomain));
        model.put(PARAM_CERTIFICATE_INFO, samlConfigSettings.getCertificateInfo());

        if(logger.isDebugEnabled())
        {
            logger.debug("SAMLConfigAdminGet: " + model);
        }

        return model;
    }
}
