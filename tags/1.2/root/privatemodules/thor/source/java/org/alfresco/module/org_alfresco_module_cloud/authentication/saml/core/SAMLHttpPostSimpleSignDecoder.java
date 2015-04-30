/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.binding.SAMLMessageContext;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.binding.decoding.HTTPPostDecoder;
import org.opensaml.ws.message.decoder.MessageDecodingException;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.util.Base64;
import org.opensaml.xml.util.DatatypeHelper;

/**
 * 
 * @author jkaabimofrad
 * @since Cloud SAML
 */
public class SAMLHttpPostSimpleSignDecoder extends HTTPPostDecoder
{
    private static final Log logger = LogFactory.getLog(SAMLHttpPostSimpleSignDecoder.class);

    public SAMLHttpPostSimpleSignDecoder()
    {
    }

    @Override
    public String getBindingURI()
    {
        return SAMLConstants.SAML2_POST_SIMPLE_SIGN_BINDING_URI;
    }

    public void decode(AlfrescoSAMLMessageContext samlMessageContext) throws MessageDecodingException,
        SecurityException
    {
        if(logger.isDebugEnabled())
            logger.debug("Beginning to decode SAML message.");

        if(samlMessageContext == null)
        {
            throw new MessageDecodingException("SAMLMessageContex is null.");
        }

        doDecode(samlMessageContext);
        logDecodedMessage(samlMessageContext);
        processSecurityPolicy(samlMessageContext);
        checkEndpointURI(samlMessageContext);

        if(logger.isDebugEnabled())
            logger.debug("Successfully decoded message.");

    }

    protected void doDecode(AlfrescoSAMLMessageContext samlMessageContext)
        throws MessageDecodingException
    {

        // TODO do we need a relayState, or is it going to be handled by share
        // samlMsgCtx.setRelayState(relayState);
        InputStream base64DecodedMessage = getBase64DecodedMessage(samlMessageContext);
        SAMLObject inboundMessage = (SAMLObject)unmarshallMessage(base64DecodedMessage);
        samlMessageContext.setInboundMessage(inboundMessage);
        samlMessageContext.setInboundSAMLMessage(inboundMessage);
        populateMessageContext(samlMessageContext);
    }

    protected InputStream getBase64DecodedMessage(AlfrescoSAMLMessageContext samlMessageContext)
        throws MessageDecodingException
    {
        if(logger.isDebugEnabled())
            logger.debug("Getting Base64 encoded message from SAMLMessage.");

        String encodedMessage = samlMessageContext.getSamlResponse();
        if(DatatypeHelper.isEmpty(encodedMessage))
        {
            encodedMessage = samlMessageContext.getSamlRequest();
            if(DatatypeHelper.isEmpty(encodedMessage))
            {
            throw new MessageDecodingException("SAMLMessage did not contain SAMLResponse or SAMLRequest.");
            }
        }
        if(logger.isTraceEnabled())
            logger.trace("Base64 decoding SAML message:" + encodedMessage);

        byte decodedBytes[] = Base64.decode(encodedMessage);
        if(decodedBytes == null)
        {
            throw new MessageDecodingException("Unable to Base64 decode SAML message");
        }
        else
        {
            if(logger.isTraceEnabled())
                logger.trace("Decoded SAML message:" + new String(decodedBytes));

            return new ByteArrayInputStream(decodedBytes);
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected boolean isMessageSigned(SAMLMessageContext samlMsgContext)
    {
        String sigParam = null;
        if(samlMsgContext instanceof AlfrescoSAMLMessageContext)
        {
            sigParam = ((AlfrescoSAMLMessageContext)samlMsgContext).getSignature();
            return !DatatypeHelper.isEmpty(sigParam) || super.isMessageSigned(samlMsgContext);
        }
        return super.isMessageSigned(samlMsgContext);
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected String getActualReceiverEndpointURI(SAMLMessageContext samlMsgContext) throws MessageDecodingException
    {
        if(samlMsgContext instanceof AlfrescoSAMLMessageContext)
        {
            return ((AlfrescoSAMLMessageContext)samlMsgContext).getSpAcsURL();
        }
        return super.getActualReceiverEndpointURI(samlMsgContext);
    }
}
