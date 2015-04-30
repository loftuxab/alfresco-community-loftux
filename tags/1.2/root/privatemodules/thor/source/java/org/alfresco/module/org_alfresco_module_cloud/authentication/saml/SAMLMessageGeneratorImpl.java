/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml;

import java.security.cert.CertificateEncodingException;
import java.util.Map;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLBinding;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLCredentialResolver;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLStatusCode;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLUtil;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.LogoutRequest;
import org.opensaml.saml2.core.LogoutResponse;
import org.opensaml.saml2.metadata.Endpoint;
import org.opensaml.saml2.metadata.SingleLogoutService;
import org.opensaml.saml2.metadata.SingleSignOnService;
import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.util.Base64;
import org.springframework.context.ApplicationEvent;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;
import org.springframework.extensions.surf.util.ParameterCheck;

/***
 * @author jkaabimofrad
 * @since Cloud SAML
 */
public class SAMLMessageGeneratorImpl extends AbstractLifecycleBean implements SAMLMessageGenerator
{
    private static final Log logger = LogFactory.getLog(SAMLMessageGeneratorImpl.class);

    private SAMLBinding samlBinding;
    private SAMLCredentialResolver credentialResolver;
    private Credential signingCredential;

    /**
     * Default constructor
     */
    public SAMLMessageGeneratorImpl()
    {
    }

    public void setSamlBinding(SAMLBinding samlBinding)
    {
        this.samlBinding = samlBinding;
    }

    public void setCredentialResolver(SAMLCredentialResolver credentialResolver)
    {
        this.credentialResolver = credentialResolver;
    }

    public void init() throws Exception
    {
        PropertyCheck.mandatory(this, "samlBinding", samlBinding);
    }

    @Override
    protected void onBootstrap(ApplicationEvent event)
    {
        PropertyCheck.mandatory(this, "credentialResolver", credentialResolver);

        try
        {
            this.signingCredential = credentialResolver.resolveSingle();
        }
        catch(SecurityException e)
        {
            throw new AlfrescoRuntimeException("Couldn't resolve the signing Credential. ", e);
        }

        PropertyCheck.mandatory(this, "signingCredential", signingCredential);
    }

    @Override
    protected void onShutdown(ApplicationEvent event)
    {
        // NOOP
    }

    @Override
    public SAMLResultMap generateSamlAuthnRequestMessage(String idpSSOServiceURL, String spAcsURL, String spIssuerName)
    {
        ParameterCheck.mandatoryString("idpSSOServiceURL", idpSSOServiceURL);
        ParameterCheck.mandatoryString("spAcsURL", spAcsURL);
        ParameterCheck.mandatoryString("spIssuerName", spIssuerName);

        if(logger.isTraceEnabled())
        {
            logger.trace("Attempting to generate SAML authn request: idpSSOServiceURL=" + idpSSOServiceURL
                + ",spAcsURL=" + spAcsURL + ",spIssuerName=" + spIssuerName);
        }

        Map<String, String> resultMap = null;
        Endpoint endpoint = SAMLUtil.generateEndpoint(SingleSignOnService.DEFAULT_ELEMENT_NAME, idpSSOServiceURL,
            spAcsURL);

        AuthnRequest authnRequest = new SAMLAuthnRequestBuilder.Builder().withIssuer(spIssuerName)
            .withDestinationURL(idpSSOServiceURL).withSpAcsURL(spAcsURL).withDefaultNameIDPolicy().build();
        
        try
        {
            resultMap = samlBinding.encodeSignSAMLMessage(authnRequest, endpoint, signingCredential, spIssuerName);
            
            if(logger.isDebugEnabled())
            {
                logger.debug("SAML authn request generated successfully: idpSSOServiceURL=" + idpSSOServiceURL
                    + ",spAcsURL=" + spAcsURL + ",spIssuerName=" + spIssuerName);
            }
        }
        catch(MessageEncodingException e)
        {
            throw new AlfrescoRuntimeException("Could not generate an encoded SAML request.", e);
        }
        
        return new SAMLResultMap(authnRequest.getID(), resultMap);
    }

    @Override
    public SAMLResultMap generateSamlLogoutRequestMessage(String userId, String idpSessionIndex,
        String idpSLORequestServiceURL, String spSLOResponseServiceURL, String spIssuerName)
    {
        ParameterCheck.mandatoryString("userId", userId);
        ParameterCheck.mandatoryString("idpSessionIndex", idpSessionIndex);
        ParameterCheck.mandatoryString("idpSLORequestServiceURL", idpSLORequestServiceURL);
        ParameterCheck.mandatoryString("spSLOResponseServiceURL", spSLOResponseServiceURL);
        ParameterCheck.mandatoryString("spIssuerName", spIssuerName);

        if(logger.isTraceEnabled())
        {
            logger.trace("Attempting to generate SAML logout request: userId=" + userId + ",idpSLORequestServiceURL="
                + idpSLORequestServiceURL + ",spSLOResponseServiceURL=" + spSLOResponseServiceURL + ",spIssuerName=" + spIssuerName);
        }

        userId = userId.toLowerCase();

        Map<String, String> resultMap = null;
        Endpoint endpoint = SAMLUtil.generateEndpoint(SingleLogoutService.DEFAULT_ELEMENT_NAME,
            idpSLORequestServiceURL, spSLOResponseServiceURL);

        LogoutRequest logoutRequest = new SAMLLogoutRequestBuilder.Builder().withIssuer(spIssuerName)
            .withDestinationURL(idpSLORequestServiceURL).withNameID(userId).build(idpSessionIndex);

        try
        {
            resultMap = samlBinding.encodeSignSAMLMessage(logoutRequest, endpoint, signingCredential, spIssuerName);
            
            if(logger.isDebugEnabled())
            {
                logger.debug("SAML logout request generated successfully: userId=" + userId
                    + ",idpSLORequestServiceURL=" + idpSLORequestServiceURL + ",spIssuerName=" + spIssuerName);
            }
        }
        catch(MessageEncodingException e)
        {
            throw new AlfrescoRuntimeException("Could not generate an encoded SAML request.", e);
        }
        
        return new SAMLResultMap(logoutRequest.getID(), resultMap);
    }

    @Override
    public SAMLResultMap generateSamlLogoutResponseMessage(String idpSloResponseServiceURL, String idpSloRequestId,
        SAMLStatusCode samlStatusCode, String spIssuerName)
    {
        ParameterCheck.mandatoryString("idpSloResponseServiceURL", idpSloResponseServiceURL);
        ParameterCheck.mandatoryString("idpSloRequestId", idpSloRequestId);
        ParameterCheck.mandatory("samlStatusCode", samlStatusCode);
        ParameterCheck.mandatoryString("spIssuerName", spIssuerName);

        if(logger.isTraceEnabled())
        {
            logger.trace("Attempting to generate SAML logout response: idpSloResponseServiceURL="
                + idpSloResponseServiceURL + ", InResponseTo=" + idpSloRequestId + ", StatusCode="
                + samlStatusCode.getStatusCodeURI() + ",spIssuerName=" + spIssuerName);
        }

        Map<String, String> resultMap = null;
        Endpoint endpoint = SAMLUtil.generateEndpoint(SingleLogoutService.DEFAULT_ELEMENT_NAME,
            idpSloResponseServiceURL, null);

        LogoutResponse logoutResponse = new SAMLLogoutResponseBuilder.Builder()
            .withDestinationURL(idpSloResponseServiceURL).withInResponseTo(idpSloRequestId).withIssuer(spIssuerName)
            .withStatusCode(samlStatusCode).build();

        try
        {
            resultMap = samlBinding.encodeSignSAMLMessage(logoutResponse, endpoint, signingCredential, spIssuerName);

            if(logger.isDebugEnabled())
            {
                logger.debug("SAML logout response generated successfully: idpSloResponseServiceURL="
                    + idpSloResponseServiceURL + ", InResponseTo=" + idpSloRequestId + ", StatusCode="
                    + samlStatusCode.getStatusCodeURI() + ",spIssuerName=" + spIssuerName);
            }
        }
        catch(MessageEncodingException e)
        {
            throw new AlfrescoRuntimeException("Could not generate an encoded SAML response.", e);
        }

        return new SAMLResultMap(logoutResponse.getID(), resultMap);
    }
    
    @Override
    public String getSpPublicCertificate() throws CertificateEncodingException
    {
        if(signingCredential instanceof BasicX509Credential)
        {
            return new String(Base64.encodeBytes(((BasicX509Credential)signingCredential).getEntityCertificate()
                .getEncoded()));
        }
        else
        {
            throw new AlfrescoRuntimeException("Not supported: " + signingCredential.getClass().getName()
                + "[expected: BasicX509Credential]");
        }
    }
}
