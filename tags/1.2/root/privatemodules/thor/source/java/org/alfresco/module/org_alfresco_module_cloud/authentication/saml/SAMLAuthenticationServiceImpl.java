/*
 * Copyright 2005-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml;

import java.security.cert.CertificateEncodingException;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.AlfrescoSAMLMessageContext;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLBinding;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLStatusCode;
import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.util.ParameterCheck;
import org.alfresco.util.PropertyCheck;
import org.alfresco.util.UrlUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.binding.SAMLMessageContext;
import org.opensaml.saml2.core.LogoutRequest;
import org.opensaml.saml2.core.LogoutResponse;
import org.opensaml.saml2.core.Response;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.extensions.surf.exception.AuthenticationException;

/**
 * SAML SSO (Authentication) service implementation
 * 
 * @author jkaabimofrad, janv
 * @since Cloud SAML
 */
public class SAMLAuthenticationServiceImpl implements SAMLAuthenticationService, InitializingBean
{
    private static final Log logger = LogFactory.getLog(SAMLAuthenticationServiceImpl.class);

    private SAMLMessageGenerator samlRequestMessageGenerator;
    private SAMLAssertionConsumer samlAssertionConsumer;
    private SAMLBinding samlBinding;

    private SysAdminParams sysAdminParams;
    private SAMLConfigAdminService samlConfigAdminService; 

    // note: if not supplied then will be default to Share URL, eg. http://localhost:8081/share or
    // https://my.alfresco.com/share or ...)
    private String spURLContext = "";

    private String spIssuerNamePrefix = "my.alfresco.com-";
    private String spSsoURLSuffix = "/saml/authnresponse";
    private String spSloRequestURLSuffix = "/saml/logoutrequest";
    private String spSloResponseURLSuffix = "/saml/logoutresponse";


    public void setSysAdminParams(SysAdminParams sysAdminParams)
    {
        this.sysAdminParams = sysAdminParams;
    }

    public void setSamlConfigAdminService(SAMLConfigAdminService samlConfigAdminService)
    {
        this.samlConfigAdminService = samlConfigAdminService;
    }

    public void setSpURLContext(String spURLContext)
    {
        this.spURLContext = spURLContext;
    }

    public void setSpIssuerNamePrefix(String spIssuerNamePrefix)
    {
        this.spIssuerNamePrefix = spIssuerNamePrefix;
    }
    
    public void setSpSsoURLSuffix(String spSsoURLSuffix)
    {
        this.spSsoURLSuffix = spSsoURLSuffix;
    }

    public void setSpSloRequestURLSuffix(String spSloRequestURLSuffix)
    {
        this.spSloRequestURLSuffix = spSloRequestURLSuffix;
    }

    public void setSpSloResponseURLSuffix(String spSloResponseURLSuffix)
    {
        this.spSloResponseURLSuffix = spSloResponseURLSuffix;
    }

    public void setSmalRequestMessageGenerator(SAMLMessageGenerator smalRequestMessageGenerator)
    {
        this.samlRequestMessageGenerator = smalRequestMessageGenerator;
    }

    public void setSamlAssertionConsumer(SAMLAssertionConsumer samlAssertionConsumer)
    {
        this.samlAssertionConsumer = samlAssertionConsumer;
    }

    public void setSamlBinding(SAMLBinding samlBinding)
    {
        this.samlBinding = samlBinding;
    }

    /**
     * Checks that all necessary properties have been provided.
     */
    public void afterPropertiesSet()
    {
        PropertyCheck.mandatory(this, "samlRequestMessageGenerator", samlRequestMessageGenerator);
        PropertyCheck.mandatory(this, "samlAssertionConsumer", samlAssertionConsumer);
        PropertyCheck.mandatory(this, "samlBinding", samlBinding);
        PropertyCheck.mandatory(this, "sysAdminParams", sysAdminParams);
        PropertyCheck.mandatory(this, "samlConfigAdminService", samlConfigAdminService);

        if((spURLContext == null) || (spURLContext.isEmpty()))
        {
            spURLContext = UrlUtil.getShareUrl(sysAdminParams);

            if(logger.isDebugEnabled())
            {
                logger.debug("Share URL configured as: " + spURLContext);
            }
        }

        if(!(spIssuerNamePrefix.endsWith("-")))
        {
            spIssuerNamePrefix += "-";
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getSpIssuerName(String tenantDomain)
    {
        ParameterCheck.mandatoryString("tenantDomain", tenantDomain);

        SAMLConfigSettings samlConfigSettings = samlConfigAdminService.getSamlConfigSettings(tenantDomain);
        String issuer = samlConfigSettings.getIssuer();
        if (StringUtils.isNotBlank(issuer))
        {
            return issuer;
        }
        else
        {
            return new StringBuilder(spIssuerNamePrefix).append(tenantDomain).toString();
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getSpSsoURL(String tenantDomain)
    {
        ParameterCheck.mandatoryString("tenantDomain", tenantDomain);
        // get SP SingleSignOn (AssertionConsumerService) URL
        // normally pass-through via Share eg. https://my.alfresco.com/share/{network}/page/saml-authnresponse
        return new StringBuilder(spURLContext).append("/").append(tenantDomain).append(spSsoURLSuffix).toString();
    }

    /**
     * {@inheritDoc}
     */
    public String getSpSloRequestURL(String tenantDomain)
    {
        ParameterCheck.mandatoryString("tenantDomain", tenantDomain);
        // get SP SingleLogOut request URL
        // normally pass-through via Share eg. https://my.alfresco.com/share/{network}/page/saml-logoutresponse
        return new StringBuilder(spURLContext).append("/").append(tenantDomain).append(spSloRequestURLSuffix).toString();
    }

    /**
     * {@inheritDoc}
     */
    public String getSpSloResponseURL(String tenantDomain)
    {
        ParameterCheck.mandatoryString("tenantDomain", tenantDomain);
        // get SP SingleLogOut response URL
        // normally pass-through via Share eg. https://my.alfresco.com/share/{network}/page/saml-logoutresponse
        return new StringBuilder(spURLContext).append("/").append(tenantDomain).append(spSloResponseURLSuffix).toString();
    }

    /**
     * {@inheritDoc}
     */
    public SAMLResultMap getSamlAuthnRequestParameters(String idpSSOServiceURL, String tenantDomain)
    {
        try
        {
            return samlRequestMessageGenerator.generateSamlAuthnRequestMessage(idpSSOServiceURL,
                getSpSsoURL(tenantDomain), getSpIssuerName(tenantDomain));
        }
        catch(Exception e)
        {
            throw new AlfrescoRuntimeException("Unable to generate auth request [" + tenantDomain + "]", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public SAMLResultMap getSamlLogoutRequestParameters(String userId, String idpSessionIndex,
        String idpSloRequestServiceURL, String tenantDomain)
    {
        try
        {
            return samlRequestMessageGenerator.generateSamlLogoutRequestMessage(userId, idpSessionIndex,
                idpSloRequestServiceURL, getSpSloResponseURL(tenantDomain), getSpIssuerName(tenantDomain));
        }
        catch(Exception e)
        {
            throw new AlfrescoRuntimeException("Unable to generate logout request [" + userId + "]", e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public SAMLResultMap getSamlLogoutResponseParameters(String idpSloResponseServiceURL, String idpSloRequestId,
        SAMLStatusCode samlStatusCode, String tenantDomain)
    {
        try
        {
            return samlRequestMessageGenerator.generateSamlLogoutResponseMessage(idpSloResponseServiceURL,
                idpSloRequestId, samlStatusCode, getSpIssuerName(tenantDomain));
        }
        catch(Exception e)
        {
            throw new AlfrescoRuntimeException("Unable to generate logout response for [" + idpSloResponseServiceURL + "]", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public SAMLUser processSamlAuthnResponse(AlfrescoSAMLMessageContext samlResponseMessageContext)
    {
        String tenantDomain = samlResponseMessageContext.getTenantDomain();
        
        SAMLMessageContext<? extends SAMLObject, ? extends SAMLObject, ? extends SAMLObject> messageContext = null;
        try
        {
            messageContext = this.samlBinding.decodeSignedSAMLMessage(samlResponseMessageContext);
        }
        catch(Exception e)
        {
            throw new AlfrescoRuntimeException("Couldn't decode auth response [" + tenantDomain + "]", e);
        }
        
        if(!(messageContext.getInboundSAMLMessage() instanceof Response))
        {
            throw new AlfrescoRuntimeException("Inbound Message was not an auth response [" + tenantDomain + "]");
        }
        
        final Response samlResponse = (Response)messageContext.getInboundSAMLMessage();
        try
        {
            // for info logging only - useful for troubleshooting live system integration (CLOUD-1192)
            String assertionID = samlResponse.getID();
            
            return new SAMLUser(assertionID, samlAssertionConsumer.getUserFromAuthnResponse(samlResponse));
        }
        catch(AuthenticationException e)
        {
            throw new AlfrescoRuntimeException("Unable to get user from auth response [" + tenantDomain
                + "] - check that IdP email attribute is mapped and that the IdP user has email address", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public SAMLUser processSamlLogoutResponse(AlfrescoSAMLMessageContext samlResponseMessageContext)
    {
        String tenantDomain = samlResponseMessageContext.getTenantDomain();
        
        SAMLMessageContext<? extends SAMLObject, ? extends SAMLObject, ? extends SAMLObject> messageContext = null;
        try
        {
            messageContext = this.samlBinding.decodeSignedSAMLMessage(samlResponseMessageContext);
        }
        catch(Exception e)
        {
            throw new AlfrescoRuntimeException("Couldn't decode logout response [" + tenantDomain + "]", e);
        }
        
        if(!(messageContext.getInboundSAMLMessage() instanceof LogoutResponse))
        {
            throw new AlfrescoRuntimeException("Inbound Message was not a Logout response [" + tenantDomain + "]");
        }
        
        final LogoutResponse samlResponse = (LogoutResponse)messageContext.getInboundSAMLMessage();
        try
        {
            return new SAMLUser(samlResponse.getID(), samlAssertionConsumer.getUserFromLogoutResponse(samlResponse));
        }
        catch(AuthenticationException e)
        {
            throw new AlfrescoRuntimeException("Unable to get user from logout response [" + tenantDomain
                + "] - check that IdP email attribute is mapped and that the IdP user has email address", e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public SAMLUser processSamlLogoutRequest(AlfrescoSAMLMessageContext samlRequestMessageContext)
    {
        String tenantDomain = samlRequestMessageContext.getTenantDomain();
        
        SAMLMessageContext<? extends SAMLObject, ? extends SAMLObject, ? extends SAMLObject> messageContext = null;
        try
        {
            messageContext = this.samlBinding.decodeSignedSAMLMessage(samlRequestMessageContext);
        }
        catch(Exception e)
        {
            throw new AlfrescoRuntimeException("Couldn't decode logout response [" + tenantDomain + "]", e);
        }
        
        if(!(messageContext.getInboundSAMLMessage() instanceof LogoutRequest))
        {
            throw new AlfrescoRuntimeException("Inbound Message was not a Logout request [" + tenantDomain + "]");
        }
        
        final LogoutRequest logoutRequest = (LogoutRequest)messageContext.getInboundSAMLMessage();
        try
        {
            return new SAMLUser(logoutRequest.getID(), samlAssertionConsumer.getUserFromLogoutRequest(logoutRequest));
        }
        catch(AuthenticationException e)
        {
            logger.error("Unable to get user from logout request [" + tenantDomain
                + "] - check that IdP email attribute is mapped and that the IdP user has email address", e);
            
            return new SAMLUser(logoutRequest.getID(), null);
        }
    }
    
    public String getSpPublicCertificate() throws CertificateEncodingException
    {
        return samlRequestMessageGenerator.getSpPublicCertificate();
    }
}
