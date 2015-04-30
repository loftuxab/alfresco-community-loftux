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
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLUser;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.AlfrescoSAMLMessageContext;
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
 * This class is the controller for the "saml-slo-response.post" web script,
 * for receiving the signed SAML slo response from the Network-specific iDP (if configured).
 * 
 * @author janv, jkaabimofrad
 * @since Cloud SAML
 */
public class SLOResponsePost extends AbstractSAMLAuthWebScript
{
    private static final Log logger = LogFactory.getLog(SLOResponsePost.class);

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
        
        // retrieve Network-specific "spSloURL"
        final String spSloURL = samlAuthenticationService.getSpSloResponseURL(tenantDomain);
        
        if(logger.isDebugEnabled())
        {
            logger.debug("[SAML:"+tenantDomain+"] [spSloURL="+spSloURL+"] SLO logoutResponse - begin logout validation");
        }
        
        AlfrescoSAMLMessageContext samlResponseMsgCtxt = new AlfrescoSAMLMessageContext();
        samlResponseMsgCtxt.setTenantDomain(tenantDomain);
        samlResponseMsgCtxt.setSpAcsURL(spSloURL);
        
        try
        {
            // receive SAML SLO logout response
            JSONObject json = (JSONObject)JSONValue.parseWithException(req.getContent().getContent());
            Object samlResponseObj = json.get("SAMLResponse");
            Object signatureObj = json.get("Signature");
            
            samlResponseMsgCtxt.setSamlResponse((samlResponseObj == null) ? null : (String)samlResponseObj);
            samlResponseMsgCtxt.setSignature((signatureObj == null) ? null : (String)signatureObj);
        }
        catch(ParseException pe)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not parse JSON from request [" + tenantDomain + "]", pe);
        }
        catch(IOException ioe)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not read content from request [" + tenantDomain + "]", ioe);
        }
        
        String userId = null;
        String idpLogoutResponseID = null;
        try
        {
            // process SAML SLO logout response (from IdP)
            SAMLUser result = samlAuthenticationService.processSamlLogoutResponse(samlResponseMsgCtxt);
            
            if (result != null)
            {
                if (result.getUser() != null)
                {
                    userId = result.getUser().getId();
                    
                    // for info logging only - useful for troubleshooting live system integration (CLOUD-1192)
                    idpLogoutResponseID = result.getSamlID();
                }
            }
        }
        catch (Throwable t)
        {
            // Exception from SAML processing implies failure with "single/global logout" either due to misconfiguration
            // or some other reason (check in server logs on both sides) In any case, since we have got this far we should try to "locally logout" the current user anyway - but  throw exception to Share
            userId = AuthenticationUtil.getFullyAuthenticatedUser();
            
            if(userId != null)
            {
                try
                {
                    invalidateCurrentTicket(userId, tenantDomain);
                    
                    if(logger.isInfoEnabled())
                    {
                        logger.info("[SAML:"+tenantDomain+"] ["+userId+"] SLO logoutResponse - invalid but still invalidated current ticket");
                    }
                }
                catch(Throwable t2)
                {
                    logger.error(t2);
                    // drop through
                }
            }
            
            throw new WebScriptException(Status.STATUS_BAD_REQUEST,
                "Failed to process SAML logout response - current user: " + userId, t);
        }
        
        // SAML has been successfully processed - userId should not be null
        
        try
        {
            if (userId != null)
            {
                invalidateCurrentTicket(userId, tenantDomain);
            }
            
            if(logger.isInfoEnabled())
            {
                logger.info("[SAML:"+tenantDomain+"] ["+userId+"] [idpLogoutResponseID="+idpLogoutResponseID+"] SLO logoutResponse - logout validated (ticket cleared)");
            }
            
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("userId", userId);
            
            return model;
        }
        catch(Throwable t)
        {
            throw new WebScriptException(Status.STATUS_NOT_FOUND, "Cannot invalidate current ticket for: " + userId, t);
        }
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
                logger.info("[SAML:"+tenantDomain+"] ["+userId+"] SLO logoutResponse - no ticket to clear");
            }
        }
    }
}
