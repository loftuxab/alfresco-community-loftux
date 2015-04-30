/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core;

import java.util.Map;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.SignableSAMLObject;
import org.opensaml.common.binding.BasicSAMLMessageContext;
import org.opensaml.common.binding.SAMLMessageContext;
import org.opensaml.saml2.metadata.Endpoint;
import org.opensaml.ws.message.decoder.MessageDecodingException;
import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.opensaml.ws.security.SecurityPolicyResolver;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.Credential;

/**
 * 
 * @author jkaabimofrad
 * @since Cloud SAML
 */
public class SAML2PostBindingImpl implements SAMLBinding
{
    private static final Log logger = LogFactory.getLog(SAML2PostBindingImpl.class);

    private SAMLHttpPostBodyDataGenerator httpPostBodyDataGenerator;
    private SAMLHttpPostSimpleSignDecoder samlDecoder;
    private SecurityPolicyResolver resolver;

    public SAML2PostBindingImpl()
    {
    }

    public void init()
    {
        PropertyCheck.mandatory(this, "httpPostBodyDataGenerator", httpPostBodyDataGenerator);
        PropertyCheck.mandatory(this, "samlDecoder", samlDecoder);
        PropertyCheck.mandatory(this, "resolver", resolver);
    }

    public void setHttpPostBodyDataGenerator(SAMLHttpPostBodyDataGenerator httpPostBodyDataGenerator)
    {
        this.httpPostBodyDataGenerator = httpPostBodyDataGenerator;
    }

    public void setSamlDecoder(SAMLHttpPostSimpleSignDecoder samlDecoder)
    {
        this.samlDecoder = samlDecoder;
    }

    public void setResolver(SecurityPolicyResolver resolver)
    {
        this.resolver = resolver;
    }

    @Override
    public Map<String, String> encodeSignSAMLMessage(SignableSAMLObject samlMessage, Endpoint endpoint,
        Credential samlMessageSigningCredential, String spIssuerName) throws MessageEncodingException
    {
        if(logger.isTraceEnabled())
        {
            logger.trace("Attempting to encode SAML message.");
        }

        BasicSAMLMessageContext<SAMLObject, SAMLObject, SAMLObject> messageContext = new BasicSAMLMessageContext<SAMLObject, SAMLObject, SAMLObject>();

        messageContext.setOutboundMessageIssuer(spIssuerName);
        messageContext.setOutboundSAMLMessage(samlMessage);
        messageContext.setPeerEntityEndpoint(endpoint);
        messageContext.setOutboundSAMLMessageSigningCredential(samlMessageSigningCredential);

        Map<String, String> resultMap = this.httpPostBodyDataGenerator.getEncodedFormInputsData(messageContext);

        if(logger.isDebugEnabled())
        {
            logger.debug("SAML message encoded Successfully: " + resultMap);
        }

        return resultMap;
    }

    @Override
    public SAMLMessageContext<SAMLObject, SAMLObject, SAMLObject> decodeSignedSAMLMessage(
        AlfrescoSAMLMessageContext samlMessageContext) throws MessageDecodingException, SecurityException
    {
        if(logger.isTraceEnabled())
        {
            logger.trace("Attempting to decode SAML message.");
        }

        samlMessageContext.setSecurityPolicyResolver(resolver);
        this.samlDecoder.decode(samlMessageContext);

        if(logger.isDebugEnabled())
        {
            logger.debug("SAML message decoded Successfully.");
        }

        return samlMessageContext;
    }
}
