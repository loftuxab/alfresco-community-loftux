/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core;

import org.alfresco.util.PropertyCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.SignableSAMLObject;
import org.opensaml.common.binding.SAMLMessageContext;
import org.opensaml.common.binding.security.SAMLProtocolMessageXMLSignatureSecurityPolicyRule;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.LogoutResponse;
import org.opensaml.saml2.core.RequestAbstractType;
import org.opensaml.saml2.core.Response;
import org.opensaml.ws.message.MessageContext;
import org.opensaml.ws.security.SecurityPolicyException;
import org.opensaml.xml.security.CriteriaSet;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.UsageType;
import org.opensaml.xml.security.criteria.EntityIDCriteria;
import org.opensaml.xml.security.criteria.UsageCriteria;
import org.opensaml.xml.signature.Signature;

/**
 * 
 * @author jkaabimofrad
 * @since Cloud SAML
 */
public class SAMLXMLSignatureSecurityPolicyRule extends SAMLProtocolMessageXMLSignatureSecurityPolicyRule
{
    private static final Log logger = LogFactory.getLog(SAMLXMLSignatureSecurityPolicyRule.class);

    private SAMLTrustEngineStore samlTrustEngineStore;

    public SAMLXMLSignatureSecurityPolicyRule()
    {
        super(null);
    }

    public void setSamlTrustEngineStore(SAMLTrustEngineStore samlTrustEngineStore)
    {
        this.samlTrustEngineStore = samlTrustEngineStore;
    }

    public void init()
    {
        PropertyCheck.mandatory(this, "samlTrustEngineStore", samlTrustEngineStore);
    }

    @Override
    public void evaluate(MessageContext messageContext) throws SecurityPolicyException
    {
        if(logger.isDebugEnabled())
            logger.debug("SAML XML signature validation started.");

        if(!(messageContext instanceof SAMLMessageContext))
        {
            throw new SecurityPolicyException(
                "Invalid message context type, this policy rule only supports SAMLMessageContext.");
        }

        @SuppressWarnings("rawtypes")
        SAMLMessageContext samlMsgCtx = (SAMLMessageContext)messageContext;

        SAMLObject samlMsg = samlMsgCtx.getInboundSAMLMessage();
        if(!(samlMsg instanceof SignableSAMLObject))
        {
            throw new SecurityPolicyException("Extracted SAML message was not a SignableSAMLObject.");
        }
        SignableSAMLObject signableObject = (SignableSAMLObject)samlMsg;
        Signature signature = signableObject.getSignature();

        if(!signableObject.isSigned())
        {
            if(logger.isDebugEnabled())
                logger.debug("Response is not signed. Check if the assertion within the response is signed.");

            if(samlMsg instanceof Response)
            {
                Response response = (Response)samlMsg;
                Assertion assertion = response.getAssertions().get(0);
                // Check if the assertion within the Response is signed.
                // Some IdPs like openAM don't sign the whole SAML AuthnResponse.
                if(assertion == null || !(assertion.isSigned()))
                {
                    throw new SecurityPolicyException("Extracted SAML message was not signed.");
                }
                signature = assertion.getSignature();
            }
            // check if it is a LogoutRequest
            else if(samlMsg instanceof RequestAbstractType)
            {
                RequestAbstractType samlRequest = (RequestAbstractType)samlMsg;
                if(!(samlRequest.isSigned()))
                {
                    throw new SecurityPolicyException("Extracted SAML LogoutRequest message was not signed.");
                }
                signature = samlRequest.getSignature();
            }
            // check if it is a LogoutResponse
            else if(samlMsg instanceof LogoutResponse)
            {
                LogoutResponse logoutResponse = (LogoutResponse)samlMsg;
                if(!(logoutResponse.isSigned()))
                {
                    throw new SecurityPolicyException("Extracted SAML LogoutResponse message was not signed.");
                }
                signature = logoutResponse.getSignature();
            }
            else
            {
                throw new SecurityPolicyException("Extracted SAML message was not signed.");
            }
        }

        performPreValidation(signature);

        if(samlMsgCtx.getPeerEntityRole() == null)
        {
            if(logger.isDebugEnabled())
                logger.debug("Peer entity role info is not present. Evaluating without role info.");
            evaluateWithoutPeerRoleInfo(samlMsgCtx, signature);
        }
        else
        {
            if(logger.isDebugEnabled())
                logger.debug("Found Peer entity role info. Evaluating with role info.");
            super.doEvaluate(signature, signableObject, samlMsgCtx);
        }

        if(logger.isDebugEnabled())
            logger.debug("SAML XML signature validation ended.");
    }

    @SuppressWarnings("rawtypes")
    private void evaluateWithoutPeerRoleInfo(SAMLMessageContext samlMsgCtx, Signature signature)
        throws SecurityPolicyException
    {
        String tenantDomain = null;
        if(samlMsgCtx instanceof AlfrescoSAMLMessageContext)
        {
            tenantDomain = ((AlfrescoSAMLMessageContext)samlMsgCtx).getTenantDomain();
        }

        CriteriaSet criteriaSet = new CriteriaSet();
        criteriaSet.add(new EntityIDCriteria(tenantDomain));
        criteriaSet.add(new UsageCriteria(UsageType.SIGNING));

        try
        {
            if(logger.isDebugEnabled())
                logger.debug("Validating [" + tenantDomain + "] SAMLResponse signature.");

            if(!samlTrustEngineStore.getTrustEngine(tenantDomain).validate(signature, criteriaSet))
            {
                throw new SecurityPolicyException("Signature was either invalid or signing key could not be trusted.");
            }
        }
        catch(SecurityException e)
        {
            throw new SecurityPolicyException("Error evaluating the signature.", e);
        }
    }
}
