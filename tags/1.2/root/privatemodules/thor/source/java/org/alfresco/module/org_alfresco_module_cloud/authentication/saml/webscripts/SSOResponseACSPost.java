/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.webscripts;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLUser;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.AlfrescoSAMLMessageContext;
import org.alfresco.module.org_alfresco_module_cloud.directory.InvalidEmailAddressException;
import org.alfresco.module.org_alfresco_module_cloud.invitation.CloudInvitation;
import org.alfresco.module.org_alfresco_module_cloud.invitation.CloudInvitationService;
import org.alfresco.module.org_alfresco_module_cloud.registration.Registration;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationService;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationServiceImpl;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.TicketComponent;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.service.cmr.security.PersonService;
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
 * SAML SSO (SingleSignOn) Response => Assertion Consumer Service
 * 
 * This class is the controller for the "saml-sso-response-acs.post" web script,
 * for receiving the signed SAML sso auth response from the Network-specific iDP (if configured).
 * 
 * @author janv, jkaabimofrad
 * @since Cloud SAML
 */
public class SSOResponseACSPost extends AbstractSAMLAuthWebScript
{
    private static final Log logger = LogFactory.getLog(SSOResponseACSPost.class);
    
    private TicketComponent ticketComponent;
    private PersonService personService;
    private RegistrationService registrationService;
    private CloudInvitationService cloudInvitationService;
    
    public void setTicketComponent(TicketComponent ticketComponent)
    {
        this.ticketComponent = ticketComponent;
    }
    
    public void setPersonService(PersonService personService)
    {
        this.personService = personService;
    }
    
    // Note: Cloud/Thor specific
    public void setRegistrationService(RegistrationService registrationService)
    {
        this.registrationService = registrationService;
    }
    
    // Note: Cloud/Thor specific
    public void setCloudInvitationService(CloudInvitationService cloudInvitationService)
    {
        this.cloudInvitationService = cloudInvitationService;
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
        
        // retrieve Network-specific "spSsoURL"
        final String spSsoURL = samlAuthenticationService.getSpSsoURL(tenantDomain);
        
        if (logger.isDebugEnabled())
        {
            logger.debug("[SAML:"+tenantDomain+"] [spSsoURL="+spSsoURL+"] SSO authnResponse - begin login validation");
        }
        
        AlfrescoSAMLMessageContext samlResponseMsgCtxt = new AlfrescoSAMLMessageContext();
        samlResponseMsgCtxt.setTenantDomain(tenantDomain);
        samlResponseMsgCtxt.setSpAcsURL(spSsoURL);
        
        final boolean isInviteOrActivate;
        try
        {
            // receive SAML SSO authentication response
            JSONObject json = (JSONObject)JSONValue.parseWithException(req.getContent().getContent());
            Object samlResponseObj = json.get("SAMLResponse");
            Object signatureObj = json.get("Signature");
            Object isInviteOrActivateObj  = json.get("isInviteOrActivate");
            
            samlResponseMsgCtxt.setSamlResponse((samlResponseObj == null) ? null : (String)samlResponseObj);
            samlResponseMsgCtxt.setSignature((signatureObj == null) ? null : (String)signatureObj);
            
            // CLOUD-1358 - note: boolean is currently passed as String (as per SAMLAlfrescoAuthenticator.authenticate)
            isInviteOrActivate = ((isInviteOrActivateObj == null) ? false : new Boolean((String)isInviteOrActivateObj));
        }
        catch (ParseException pe)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not parse JSON from request ["+tenantDomain+"]", pe);
        }
        catch (IOException ioe)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not read content from request ["+tenantDomain+"]", ioe);
        }

        // process SAML SSO authentication response
        SAMLUser result = null;

        try
        {
            result = samlAuthenticationService.processSamlAuthnResponse(samlResponseMsgCtxt);
        }
        catch(Throwable t)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not process SSO response [" + tenantDomain + "]", t);
        }

        if((result != null) && (result.getUser() != null))
        {
            String userId = result.getUser().getEmail();
            
            // Note: we're currently returning the idpSessionIndex in the User.id field
            //       it should be noted that although for PingFederate and OpenAM 
            //       the Assertion (Response) ID and Assertion Session Index are the same
            //       that may not necessarily be true for future/supported types of IdP (TBC)
            final String idpSessionIndex = result.getUser().getId();
            
            // for info logging only - useful for troubleshooting live system integration (CLOUD-1192)
            final String idpAssertionID = result.getSamlID();
            
            try
            {
                Pair<String, String> userTenant = AuthenticationUtil.getUserTenant(userId);
                final String userName = userTenant.getFirst();
                final String userTenantDomain = userTenant.getSecond();
                
                if (! tenantDomain.equals(userTenantDomain))
                {
                    throw new AuthenticationException("Tenant mismatch: " + userName + " (expected: "+tenantDomain+")");
                }
                
                return TenantUtil.runAsSystemTenant(new TenantRunAsWork< Map<String, Object>>()
                {
                    public Map<String, Object> doWork() throws Exception
                    {
                        String ticket = null;
                        Registration registration = null;
                        String registrationType = null;
                        
                        try
                        {
                            if (! personService.personExists(userName))
                            {
                                if (logger.isTraceEnabled())
                                {
                                    logger.trace("SSOResponseACSPost: ["+userName+"] user does not exist");
                                }
                                
                                if (! isInviteOrActivate)
                                {
                                    // CLOUD-1159 - special case (new user login direct to profile page)
                                    
                                    // check if existing signup and/or invite to network
                                    try
                                    {
                                        registration = registrationService.getRegistration(userName);
                                        if (registration != null)
                                        {
                                            registrationType = "signup";
                                        }
                                    }
                                    catch (InvalidEmailAddressException iae)
                                    {
                                        throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Invalid userId: "+userName, iae);
                                    }
                                    
                                    if (registration == null)
                                    {
                                        // check if existing invite to site
                                        List<CloudInvitation> invites = cloudInvitationService.listPendingInvitationsForInvitee(userName, 1);
                                        if (invites.size() > 0)
                                        {
                                            registrationType = "invite";
                                            final CloudInvitation invite = invites.get(0);
                                            registration = new Registration()
                                            {
                                                @Override
                                                public String getEmailAddress()
                                                {
                                                    return userName;
                                                }
                                                
                                                @Override
                                                public Date getRegistrationDate()
                                                {
                                                    return invite.getStartDate();
                                                }
                                                
                                                @Override
                                                public String getId()
                                                {
                                                    return invite.getId();
                                                }
                                                
                                                @Override
                                                public String getKey()
                                                {
                                                    return invite.getKey();
                                                }
                                                
                                                @Override
                                                public String getInitiatorEmailAddress()
                                                {
                                                    return null;
                                                }
                                                
                                                @Override
                                                public String getInitiatorFirstName()
                                                {
                                                    return null;
                                                }
                                                
                                                @Override
                                                public String getInitiatorLastName()
                                                {
                                                    return null;
                                                }
                                            };
                                        }
                                        else
                                        {
                                            registration = registrationService.registerEmail(userName, RegistrationServiceImpl.SAML_DIRECT_SIGNUP, null, null);
                                            registrationType = "signup";
                                        }
                                        
                                        if (logger.isDebugEnabled())
                                        {
                                            logger.debug("SSOResponseACSPost: ["+userName+"] user does not exist [key="+registration.getKey()+",id="+registration.getId()+"]");
                                        }
                                    }
                                }
                                
                                // Note: Issue "internal" ticket (CLOUD-1079) which will be checked during Registration.createUser (to track that valid SSO login)
                                ticketComponent.getCurrentTicket(userName, true); // ignore result - but ticket is cached ...
                                
                                if (logger.isInfoEnabled())
                                {
                                    logger.info("[SAML:"+tenantDomain+"] ["+userName+"] [idpAssertionID="+idpAssertionID+"] [idpSessionIndex="+idpSessionIndex+"] SSO authnResponse - login validated (user does not exist)");
                                }
                            }
                            else
                            {
                                // SSO authnResponse has been validated - issue a new ticket
                                ticket = ticketComponent.getCurrentTicket(userName, true);
                                ticketComponent.validateTicket(ticket);
                                
                                if (logger.isInfoEnabled())
                                {
                                    logger.info("[SAML:"+tenantDomain+"] ["+userName+"] [idpAssertionID="+idpAssertionID+"] [idpSessionIndex="+idpSessionIndex+"] SSO authnResponse - login validated (ticket issued)");
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            throw new AuthenticationException("Cannot login: "+userName, e);
                        }
                        
                        Map<String, Object> model = new HashMap<String, Object>();
                        
                        model.put("ticket", ticket);
                        model.put("userId", userName);
                        model.put("idpSessionIndex", idpSessionIndex);
                        
                        if (registration != null)
                        {
                            // new user
                            if ((registrationType == null) || (ticket != null))
                            {
                                // belts-and-braces
                                throw new AlfrescoRuntimeException("Error for new user signup/invite: "+userName+" [idpSsessionIndex="+idpSessionIndex+",registrationType="+registrationType+"]");
                            }
                            
                            model.put("registration", registration);
                            model.put("registrationType", registrationType);
                        }
                        
                        return model;
                    }
                }, userTenantDomain);
            }
            catch (Throwable t)
            {
                throw new WebScriptException(Status.STATUS_UNAUTHORIZED, "Cannot authenticate: "+userId, t);
            }
        }
        
        throw new WebScriptException(Status.STATUS_NOT_FOUND, "Cannot authenticate - no userId: "+tenantDomain);
    }
}
