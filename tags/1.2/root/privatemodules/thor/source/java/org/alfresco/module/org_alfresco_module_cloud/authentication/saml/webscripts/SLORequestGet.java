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
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.tenant.TenantService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * SAML SLO (SingleLogout) Request - SP-initiated
 * 
 * To generate SP-initiated single logout request (note: the "SAML logout request" is POST'ed to the IdP)
 * 
 * This class is the controller for the "saml-slo-request.get" web script,
 * for sending SAML single logout request to the Network-specific iDP (if configured, for Enterprise Network)
 * 
 * @author janv
 * @since Cloud SAML
 */
public class SLORequestGet extends AbstractSAMLAuthWebScript
{
    private static final Log logger = LogFactory.getLog(SLORequestGet.class);
    
    private static final String PARAM_IDP_SESSION_INDEX = "idpSessionIndex";
    
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
        
        String idpSessionIndex = templateVars.get(PARAM_IDP_SESSION_INDEX);
        if (idpSessionIndex == null || idpSessionIndex.length() == 0)
        {
            throw new WebScriptException(Status.STATUS_NOT_FOUND, "IdP Session Index not specified");
        }
        
        String currentUserId = AuthenticationUtil.getFullyAuthenticatedUser();
        
        String tenantDomain = null;
        
        // get user's implied primary network (whether user exists or not)
        int idx = currentUserId.indexOf(TenantService.SEPARATOR);
        if ((idx >= 0) && (idx < (currentUserId.length()-1)))
        {
            tenantDomain = currentUserId.substring(idx+1);
        }
        else
        {
            tenantDomain = currentUserId;
        }
        
        
        if (! isTenantSamlEnabled(tenantDomain))
        {
            status.setCode(Status.STATUS_UNAUTHORIZED);
            status.setRedirect(true);
            return null; // null is OK - note: Collections.emptyMap() will fail (with UnsupportedOperationException->AbstractMap.put->DeclarativeWebScript.java:69)
        }
        
        // retrieve Network-specific "idpSLORequestServiceURL"
        final String idpSloRequestUrl = getSAMLConfigSettings(tenantDomain).getIdpSloRequestURL();
        
        if (logger.isDebugEnabled())
        {
            logger.debug("[SAML:"+tenantDomain+"] [idpSessionIndex="+idpSessionIndex+"] [idpSloRequestUrl="+idpSloRequestUrl+"] SLO logoutRequest - begin generation");
        }
        
        // generate and return SAML SLO logout request
        SAMLResultMap samlRequest = samlAuthenticationService.getSamlLogoutRequestParameters(currentUserId, idpSessionIndex, idpSloRequestUrl, tenantDomain);
        
        // for info logging only - useful for troubleshooting live system integration (CLOUD-1192)
        String spLogoutRequestID = samlRequest.getSamlID();
        
        Map<String, Object> model = new HashMap<String, Object>();
        model.putAll(samlRequest.getResultMap());
        
        if (logger.isInfoEnabled())
        {
            logger.info("[SAML:"+tenantDomain+"] ["+currentUserId+"] [spLogoutRequestID="+spLogoutRequestID+"] [idpSessionIndex="+idpSessionIndex+"] [idpSloRequestUrl="+idpSloRequestUrl+"] SLO logoutRequest - generated");
        }
        
        return model;
    }
}
