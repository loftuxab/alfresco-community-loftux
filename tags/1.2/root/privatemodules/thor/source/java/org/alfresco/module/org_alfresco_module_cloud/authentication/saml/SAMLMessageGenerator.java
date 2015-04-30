/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml;

import java.security.cert.CertificateEncodingException;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLStatusCode;

/**
 * SAML SSO/SLO (AuthnRequest/LogoutRequest/LogoutResponse) message generator
 * 
 * @author jkaabimofrad, janv
 * @since Cloud SAML
 */
public interface SAMLMessageGenerator
{
    /**
     * Generates SAML SSO AuthnRequest
     * 
     * @param idpSSOServiceURL
     * @param spAssertionConsumerServiceURL
     * @param spIssuerName
     * @return ID + Map<String, String>
     *         map of form input names, and encoded form input values
     */
    public SAMLResultMap generateSamlAuthnRequestMessage(String idpSSOServiceURL, String spAssertionConsumerServiceURL,
        String spIssuerName);

    /**
     * Generates SAML SLO LogoutRequest
     * 
     * @param userId
     * @param idpSLORequestServiceURL
     * @param spSLOResponseServiceURL
     * @param spIssuerName
     * @return ID + Map<String, String>
     *         map of form input names, and encoded form input values
     */
    public SAMLResultMap generateSamlLogoutRequestMessage(String userId, String idpSessionIndex,
        String idpSLORequestServiceURL, String spSLOResponseServiceURL, String spIssuerName);

    /**
     * Generates SAML SLO LogoutResponse
     * 
     * @param idpSloResponseServiceURL
     * @param idpSloRequestId
     * @param samlStatusCode
     * @param spIssuerName
     * @return ID + Map<String, String>
     *         map of form input names, and encoded form input values
     */
    public SAMLResultMap generateSamlLogoutResponseMessage(String idpSloResponseServiceURL, String idpSloRequestId,
        SAMLStatusCode samlStatusCode, String spIssuerName);

    /**
     * Get SP public certificate (Base64 encoded)
     * 
     * @return
     */
    public String getSpPublicCertificate() throws CertificateEncodingException;
}
