/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core;

import java.util.Map;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.SignableSAMLObject;
import org.opensaml.common.binding.SAMLMessageContext;
import org.opensaml.saml2.metadata.Endpoint;
import org.opensaml.ws.message.decoder.MessageDecodingException;
import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.Credential;

/**
 * 
 * @author jkaabimofrad
 * @since Cloud SAML
 */
public interface SAMLBinding
{
    /**
     * Signs and encodes SAML authentication request.
     * 
     * @param samlMessage
     *            the AuthnRequest
     * @param endpoint
     *            the IdP
     * @param samlMessageSigningCredential
     *            the credential that must be used to sing the request
     * @param spIssuerName
     *            the service provider entityID
     * @return Map<String, String>
     *         map of form input names, and encoded form input values
     * @throws MessageEncodingException
     */
    public Map<String, String> encodeSignSAMLMessage(SignableSAMLObject samlMessage, Endpoint endpoint,
        Credential samlMessageSigningCredential, String spIssuerName) throws MessageEncodingException;

    /**
     * Decodes and validates SAML response.
     * 
     * @param samlMessageContext
     *         the message context which contains encoded SAMLResponse/SAMLRequest and other optional parameters such as Signature.
     * 
     * @return SAMLMessageContext
     *         the decoded SAMLMessageContext
     * @throws MessageDecodingException
     * @throws SecurityException
     */
    public SAMLMessageContext<SAMLObject, SAMLObject, SAMLObject> decodeSignedSAMLMessage(
        AlfrescoSAMLMessageContext samlMessageContext) throws MessageDecodingException, SecurityException;

}
