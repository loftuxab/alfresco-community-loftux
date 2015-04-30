/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.webscripts;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLResultMap;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLUser;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.AlfrescoSAMLMessageContext;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLStatusCode;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.TicketComponent;
import org.alfresco.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * SAML SLO (SingleLogout) Response
 * 
 * This class is the controller for the "saml-slo-request.post" web script,
 * 
 * @author jkaabimofrad
 * @since Cloud SAML
 */
public class SLORequestPost extends AbstractSAMLAuthWebScript
{
    private static final Log logger = LogFactory.getLog(SLORequestPost.class);

    private TicketComponent ticketComponent;

    public void setTicketComponent(TicketComponent ticketComponent)
    {
        this.ticketComponent = ticketComponent;
    }

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
        
        // retrieve Network-specific "idpSloResponseServiceURL"
        final String idpSloResponseURL = getSAMLConfigSettings(tenantDomain).getIdpSloResponseURL();
        final String spSloRequestURL = samlAuthenticationService.getSpSloRequestURL(tenantDomain);
        
        SAMLStatusCode samlStatusCode = null;
        String relayState = null;
        
        if(logger.isDebugEnabled())
        {
            logger.debug("[SAML:"+tenantDomain+"] [idpSloResponseURL="+idpSloResponseURL+"] SLO logoutRequest - begin logout processing ");
        }
        
        AlfrescoSAMLMessageContext samlRequestMsgCtxt = new AlfrescoSAMLMessageContext();
        samlRequestMsgCtxt.setSpAcsURL(spSloRequestURL);
        samlRequestMsgCtxt.setTenantDomain(tenantDomain);
        
        try
        {
            // receive SAML SLO logout request
            JSONObject json = (JSONObject)JSONValue.parseWithException(req.getContent().getContent());
            // eg. IdP-initiated SLO
            Object samlRequestObj = json.get("SAMLRequest");
            Object signatureObj = json.get("Signature");
            relayState = (String)json.get("RelayState");
            
            samlRequestMsgCtxt.setSamlRequest((samlRequestObj == null) ? null : (String)samlRequestObj);
            samlRequestMsgCtxt.setSignature((signatureObj == null) ? null : (String)signatureObj);
        }
        catch(ParseException pe)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not parse JSON from request ["
                + tenantDomain + "]", pe);
        }
        catch(IOException ioe)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not read content from request ["
                + tenantDomain + "]", ioe);
        }
        
        String userId = null;
        String idpLogoutRequestID = null;
        try
        {
            // process SAML SLO logout request
            SAMLUser samlUser = samlAuthenticationService.processSamlLogoutRequest(samlRequestMsgCtxt);
            userId = (samlUser.getUser() != null ? samlUser.getUser().getId() : null);
            idpLogoutRequestID = samlUser.getSamlID();
        }
        catch (Throwable t)
        {
            // Exception from SAML processing implies failure with "single/global logout" either due to misconfiguration
            // or some other reason (check in server logs on both sides)
            throw new WebScriptException(Status.STATUS_BAD_REQUEST,
                "Failed to process SAML logout request - current user: " + userId, t);
        }
        
        String currentUserId = AuthenticationUtil.getFullyAuthenticatedUser();
        if ((currentUserId != null) && (! currentUserId.equalsIgnoreCase(userId)))
        {
            if (logger.isInfoEnabled())
            {
                logger.info("[SAML:"+tenantDomain+"] ["+userId+"] [idpSloResponseURL="+idpSloResponseURL+"] [logoutRequestId="+idpLogoutRequestID+"] SLO logoutRequest - current user: ");
            }
        }
        
        if (userId != null)
        {
            try
            {
                invalidateCurrentTicket(userId, tenantDomain);
                
                if(logger.isInfoEnabled())
                {
                    logger.info("[SAML:"+tenantDomain+"] ["+userId+"] [idpSloResponseURL="+idpSloResponseURL+"] [logoutRequestId="+idpLogoutRequestID+"] SLO logoutRequest - validated (ticket cleared)");
                }
                
                // the request processed successfully
                samlStatusCode = SAMLStatusCode.SUCCESS_URI;
            }
            catch (Throwable t)
            {
                logger.error("[SAML:"+tenantDomain+"] ["+userId+"] failed to invalidate current ticket: "+t.getMessage());
                
                samlStatusCode = SAMLStatusCode.AUTHN_FAILED_URI;
            }
        }
        else
        {
            samlStatusCode = SAMLStatusCode.UNKNOWN_PRINCIPAL_URI;
        }
        
        Map<String, Object> model = new HashMap<String, Object>();
        
        try
        {
            SAMLResultMap samlResponse = samlAuthenticationService.getSamlLogoutResponseParameters(idpSloResponseURL,
                idpLogoutRequestID, samlStatusCode, tenantDomain);
            
            model.putAll(samlResponse.getResultMap());
            model.put("RelayState", relayState);
            model.put("userId", userId);
            
            // for info logging only - useful for troubleshooting live system integration (CLOUD-1192)
            String spLogoutResponseID = samlResponse.getSamlID();
            
            if(samlStatusCode.equals(SAMLStatusCode.SUCCESS_URI))
            {
                model.put("result", "success");
                
                if (logger.isInfoEnabled())
                {
                    logger.info("[SAML:"+tenantDomain+"] ["+userId+"] [idpLogoutRequestID="+idpLogoutRequestID+"] [spLogoutResponseID="+spLogoutResponseID+"] [idpSloResponseURL="+idpSloResponseURL+"] SLO logoutRequest - validated (ticket cleared) and generated logoutResponse");
                }
            }
            else
            {
                model.put("result", "failure");
                
                logger.error("[SAML:"+tenantDomain+"] ["+userId+"] [idpLogoutRequestID="+idpLogoutRequestID+"] [spLogoutResponseID="+spLogoutResponseID+"] [idpSloResponseURL="+idpSloResponseURL+"] SLO logoutRequest - failed to validate: "+samlStatusCode.getStatusCodeURI());
            }
        }
        catch(Throwable th)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST,
                "Failed to generate SAML LogoutResponse - current user: " + userId, th);
        }
        return model;
    }
    
    private void invalidateCurrentTicket(String userId, String tenantDomain)
    {
        Pair<String, String> userTenant = AuthenticationUtil.getUserTenant(userId);
        final String userName = userTenant.getFirst();
        final String userTenantDomain = userTenant.getSecond();
        
        if (! tenantDomain.equals(userTenantDomain))
        {
            String errorMessage = "Tenant mismatch: " + userName + " (expected: " + tenantDomain + ")";
            logger.error(errorMessage);
            throw new AlfrescoRuntimeException(errorMessage);
        }
        
        String currentTicketId = ticketComponent.getCurrentTicket(userName, false);
        if (currentTicketId != null)
        {
            ticketComponent.invalidateTicketById(currentTicketId);
        }
        else
        {
            if (logger.isInfoEnabled())
            {
                logger.info("[SAML:"+tenantDomain+"] ["+userId+"] SLO logoutRequest - no ticket to clear");
            }
        }
    }
}
