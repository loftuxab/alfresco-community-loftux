/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml;

import java.security.cert.CertificateEncodingException;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.AlfrescoSAMLMessageContext;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLStatusCode;

/**
 * SAML Authentication service (SSO/SLO => login/logout)interface
 * 
 * @author jkaabimofrad, janv
 * @since Cloud SAML
 * 
 */
public interface SAMLAuthenticationService
{
    /**
     * Get Service Provider (SP) entityID.
     * 
     * @param tenantDomain
     * @return a string representation of the SP's entityID
     */
    public String getSpIssuerName(String tenantDomain);

    /**
     * Get Service Provider (SP) SingleSignOn (SSO) URL => a.k.a. Assertion Consumer Service (ACS) URL.
     * 
     * @param tenantDomain
     * @return a string representation of the SP's single-sign-on service URL
     */
    public String getSpSsoURL(String tenantDomain);

    /**
     * Get Service Provider (SP) SingleLogOut (SLO) request service URL.
     * 
     * @param tenantDomain
     * @return a string representation of the SP's single-log-out request service URL
     */
    public String getSpSloRequestURL(String tenantDomain);

    /**
     * Get Service Provider (SP) SingleLogOut (SLO) response service URL.
     * 
     * @param tenantDomain
     * @return a string representation of the SP's single-log-out response service URL
     */
    public String getSpSloResponseURL(String tenantDomain);

    /**
     * Get SAML SSO AuthRequest.
     * 
     * @param idpSSOServiceURL
     * @param tenantDomain
     * @return Map<String, String> (+ ID)
     *         map of form input names, and encoded form input values
     */
    public SAMLResultMap getSamlAuthnRequestParameters(String idpSSOServiceURL, String tenantDomain);

    /**
     * Get SAML SLO LogoutRequest.
     * 
     * @param userId
     * @param idpSessionIndex
     * @param idpSloRequestServiceURL
     * @param tenantDomain
     * @return Map<String, String> (+ ID)
     *         map of form input names, and encoded form input values
     */
    public SAMLResultMap getSamlLogoutRequestParameters(String userId, String idpSessionIndex,
        String idpSloRequestServiceURL, String tenantDomain);

    /**
     * Get SAML SLO LogoutResponse.
     * 
     * @param idpSloResponseServiceURL
     * @param idpSloRequestId
     * @param samlStatusCode
     * @param tenantDomain
     * @return Map<String, String> (+ ID)
     *         map of form input names, and encoded form input values
     */
    public SAMLResultMap getSamlLogoutResponseParameters(String idpSloResponseServiceURL, String idpSloRequestId,
        SAMLStatusCode samlStatusCode, String tenantDomain);

    /**
     * Processes SAML SSO AuthnResponse. The processing includes decoding and signature validation.
     * 
     * Note: both SP-initiated and IdP-initiated
     * 
     * @param samlResponseMessageContext
     *            the message context which contains encoded SAMLResponse and other optional parameters such as
     *            Signature.
     * @return User object (+ ID)
     */
    public SAMLUser processSamlAuthnResponse(AlfrescoSAMLMessageContext samlResponseMessageContext);

    /**
     * Processes SAML SLO LogoutResponse. The processing includes decoding and signature validation.
     * 
     * Note: SP-initiated
     * 
     * @param samlResponseMessageContext
     *            the message context which contains encoded SAMLResponse and other optional parameters such as
     *            Signature.
     * @return User object (+ ID)
     */
    public SAMLUser processSamlLogoutResponse(AlfrescoSAMLMessageContext samlResponseMessageContext);

    /**
     * Processes SAML SLO LogoutReqest. The processing includes decoding and signature validation.
     * 
     * Note: IdP-initiated
     * 
     * @param samlRequestMessageContext
     *            the message context which contains encoded SAMLRequest and other optional parameters such as
     *            Signature.
     * @return User object (+ ID)
     */
    public SAMLUser processSamlLogoutRequest(AlfrescoSAMLMessageContext samlRequestMessageContext);

    /**
     * Get SP public certificate (Base64 encoded)
     * 
     * @return
     */
    public String getSpPublicCertificate() throws CertificateEncodingException;
}
