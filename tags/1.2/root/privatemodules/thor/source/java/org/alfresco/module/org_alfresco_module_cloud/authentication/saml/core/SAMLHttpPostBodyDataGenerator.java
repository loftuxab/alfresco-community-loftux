/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.binding.SAMLMessageContext;
import org.opensaml.saml2.binding.encoding.HTTPPostSimpleSignEncoder;
import org.opensaml.saml2.core.StatusResponseType;
import org.opensaml.ws.message.encoder.MessageEncodingException;

/**
 * 
 * @author jkaabimofrad
 * @since Cloud SAML
 */
public class SAMLHttpPostBodyDataGenerator extends HTTPPostSimpleSignEncoder
{
    private static final Log logger = LogFactory.getLog(SAMLHttpPostBodyDataGenerator.class);

    public static final String SAML_REQUEST_INPUT_NAME = "SAMLRequest";
    public static final String SAML_RESPONSE_INPUT_NAME = "SAMLResponse";
    public static final String SIGNATURE_INPUT_NAME = "Signature";
    public static final String SIG_ALG_INPUT_NAME = "SigAlg";
    public static final String KEY_INFO_INPUT_NAME = "KeyInfo";
    public static final String FORM_ACTION = "action";

    public SAMLHttpPostBodyDataGenerator()
    {
        super(null, null, true);
    }

    public SAMLHttpPostBodyDataGenerator(boolean signXMLProtocolMessage)
    {
        super(null, null, signXMLProtocolMessage);
    }

    public Map<String, String> getEncodedFormInputsData(
        SAMLMessageContext<SAMLObject, SAMLObject, SAMLObject> samlMsgCtx) throws MessageEncodingException
    {
        SAMLObject outboundMessage = samlMsgCtx.getOutboundSAMLMessage();
        if(outboundMessage == null)
        {
            throw new MessageEncodingException("No outbound SAML message contained in message context");
        }
        String endpointURL = getEndpointURL(samlMsgCtx).buildURL();
        if(samlMsgCtx.getOutboundSAMLMessage() instanceof StatusResponseType)
        {
            ((StatusResponseType)samlMsgCtx.getOutboundSAMLMessage()).setDestination(endpointURL);
        }
        // Sign AuthnRequest XML message
        signMessage(samlMsgCtx);
        samlMsgCtx.setOutboundMessage(outboundMessage);

        return getFormInputsData(samlMsgCtx, endpointURL);
    }

    protected Map<String, String> getFormInputsData(SAMLMessageContext<SAMLObject, SAMLObject, SAMLObject> samlMsgCtx,
        String endpointURL) throws MessageEncodingException
    {
        if(logger.isDebugEnabled())
            logger.debug("Creating form controls data for the POST body.");

        Map<String, String> map = new HashMap<String, String>(5);

        VelocityContext context = new VelocityContext();
        // Note: SAMLRequest, which includes a signed AuthnRequest and optionally, a RelayState, will be singed after
        // completion of this method.
        populateVelocityContext(context, samlMsgCtx, endpointURL);

        String samlRequest = (String)context.get(SAML_REQUEST_INPUT_NAME);
        // If SAMLRequest element is null then, it MUST be SAMLResponse.
        if(samlRequest != null)
        {
            map.put(SAML_REQUEST_INPUT_NAME, samlRequest);
            // Rest of the elements are optional
            map.put(SIGNATURE_INPUT_NAME, (String)context.get(SIGNATURE_INPUT_NAME));
            map.put(SIG_ALG_INPUT_NAME, (String)context.get(SIG_ALG_INPUT_NAME));
            map.put(KEY_INFO_INPUT_NAME, (String)context.get(KEY_INFO_INPUT_NAME));
        }
        else
        {
            // No need to include the signature for the SAMLResponse. SAMLResponse is used only to return LogoutResponse
            map.put(SAML_RESPONSE_INPUT_NAME, (String)context.get(SAML_RESPONSE_INPUT_NAME));
        }
        map.put(FORM_ACTION, (String)context.get(FORM_ACTION));

        if(logger.isTraceEnabled())
            logger.trace("Created form controls data:" + map);

        return Collections.unmodifiableMap(map);
    }
}
