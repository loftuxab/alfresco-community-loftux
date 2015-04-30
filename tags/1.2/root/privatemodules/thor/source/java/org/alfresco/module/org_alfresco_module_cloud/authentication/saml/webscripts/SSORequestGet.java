/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.webscripts;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLResultMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * SAML SSO (SingleSignOn) Request - Entry Point 
 * 
 * This class is the controller for the "saml-sso-request.get" web script,
 * for sending SAML authentication request to the Network-specific iDP (if configured, for Enterprise Network)
 * 
 * @author janv, jkaabimofrad
 * @since Cloud SAML
 */
public class SSORequestGet extends AbstractSAMLAuthWebScript
{
    private static final Log logger = LogFactory.getLog(SSORequestGet.class);

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        // get Network/Tenant and check that is enabled for SAML (else return 401)
        Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
        final String tenantDomain = templateVars.get(PARAM_NETWORK_TENANT_DOMAIN);
        if (! isTenantSamlEnabled(tenantDomain))
        {
            status.setCode(Status.STATUS_UNAUTHORIZED);
            status.setRedirect(true);
            return null; // null is OK - note: Collections.emptyMap() will fail (with UnsupportedOperationException->AbstractMap.put->DeclarativeWebScript.java:69)
        }
        
        // retrieve Network-specific "idpSsoURL"
        final String idpSsoURL = getSAMLConfigSettings(tenantDomain).getIdpSsoURL();
        
        if (logger.isDebugEnabled())
        {
            logger.debug("[SAML:"+tenantDomain+"] [idpSsoURL="+idpSsoURL+"] SSO authnRequest - begin generation");
        }
        
        // generate and return SAML SSO authentication request
        SAMLResultMap samlRequest = samlAuthenticationService.getSamlAuthnRequestParameters(idpSsoURL, tenantDomain);
        
        // for info logging only - useful for troubleshooting live system integration (CLOUD-1192)
        String spAuthnRequestID = samlRequest.getSamlID();
        
        Map<String, Object> model = new HashMap<String, Object>();
        model.putAll(samlRequest.getResultMap());
        
        if (logger.isInfoEnabled())
        {
            logger.info("[SAML:"+tenantDomain+"] [spAuthnRequestId="+spAuthnRequestID+"] [idpSsoURL="+idpSsoURL+"] SSO authnRequest - generated");
        }
        
        return model;
    }
}
