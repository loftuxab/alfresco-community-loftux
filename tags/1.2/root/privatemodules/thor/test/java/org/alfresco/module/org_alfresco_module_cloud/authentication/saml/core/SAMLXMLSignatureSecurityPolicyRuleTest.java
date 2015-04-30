/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import java.security.cert.X509Certificate;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SAMLObject;
import org.opensaml.ws.security.SecurityPolicyException;
import org.opensaml.xml.ConfigurationException;

/**
 * Test code for {@link SAMLXMLSignatureSecurityPolicyRule}.
 * 
 * @author jkaabimofrad
 * @since Cloud SAML
 */
public class SAMLXMLSignatureSecurityPolicyRuleTest
{

    private static X509Certificate cert;

    private static SAMLXMLSignatureSecurityPolicyRule policyRule;
    private AlfrescoSAMLMessageContext messageContext;

    @BeforeClass
    public static void initXMLObject() throws ConfigurationException
    {
        DefaultBootstrap.bootstrap();

        cert = SAMLTestHelper.getDefaultCertificate();
        policyRule = new SAMLXMLSignatureSecurityPolicyRule();
    }

    @Before
    public void initSecurityPolicyRuleTest() throws Exception
    {
        messageContext = new AlfrescoSAMLMessageContext();        
        policyRule.setSamlTrustEngineStore(SAMLTestHelper.buildTestTrustEngine(cert));
    }

    /**
     * Test valid Response, LogoutRequest and LogoutResponse. Both signature and certificate are valid.
     * 
     * @throws Exception
     */
    @Test
    public void testValidSignature() throws Exception
    {
        // Response to the AuthnRequest
        SAMLObject response = (SAMLObject)SAMLTestHelper.unmarshallElement("response-openam.xml");
        messageContext.setInboundSAMLMessage(response);
        messageContext.setTenantDomain("coolIssuer1");
        assertSuccess("Signature and Certificate are valid.");

        // Logout Request
        SAMLObject logoutRequest = (SAMLObject)SAMLTestHelper.unmarshallElement("logout-request-openam.xml");
        messageContext.setInboundSAMLMessage(logoutRequest);
        messageContext.setTenantDomain("coolIssuer2");
        assertSuccess("Signature and Certificate are valid.");

        // Logout Response
        SAMLObject logoutResponse = (SAMLObject)SAMLTestHelper.unmarshallElement("logout-response-openam.xml");
        messageContext.setInboundSAMLMessage(logoutResponse);
        messageContext.setTenantDomain("coolIssuer3");
        assertSuccess("Signature and Certificate are valid.");
    }

    /**
     * Test Response, LogoutRequest and LogoutRespons which were signed with not-agreed certificate.
     * 
     * @throws Exception
     */
    @Test
    public void testInvalidSignature() throws Exception
    {
        // Response to the AuthnRequest
        SAMLObject response = (SAMLObject)SAMLTestHelper.unmarshallElement("response-pf.xml");
        messageContext.setInboundSAMLMessage(response);
        messageContext.setTenantDomain("coolIssuer1");
        assertFailure("Signature is invalid.");

        // Logout Request
        SAMLObject logoutRequest = (SAMLObject)SAMLTestHelper.unmarshallElement("logout-request-pf.xml");
        messageContext.setInboundSAMLMessage(logoutRequest);
        messageContext.setTenantDomain("coolIssuer2");
        assertFailure("Signature is invalid.");

        // Logout Response
        SAMLObject logoutResponse = (SAMLObject)SAMLTestHelper.unmarshallElement("logout-response-pf.xml");
        messageContext.setInboundSAMLMessage(logoutResponse);
        messageContext.setTenantDomain("coolIssuer3");
        assertFailure("Signature is invalid.");
    }

    /**
     * Test Response, LogoutRequest and LogoutRespons which were tampered with their IssueInstant date.
     * 
     * @throws Exception
     */
    @Test
    public void testTamperedSamlMessage() throws Exception
    {
        // Response to the AuthnRequest. Altered IssueInstant
        SAMLObject response = (SAMLObject)SAMLTestHelper.unmarshallElement("tampered-response-openam.xml");
        messageContext.setInboundSAMLMessage(response);
        messageContext.setTenantDomain("coolIssuer1");
        assertFailure("Altered IssueInstant, signature must be invalid.");

        // Logout Request. Altered IssueInstant
        SAMLObject logoutRequest = (SAMLObject)SAMLTestHelper
            .unmarshallElement("tampered-logout-request-openam.xml");
        messageContext.setInboundSAMLMessage(logoutRequest);
        messageContext.setTenantDomain("coolIssuer2");
        assertFailure("Altered IssueInstant, signature must be invalid.");

        // Logout Response. Altered IssueInstant
        SAMLObject logoutResponse = (SAMLObject)SAMLTestHelper
            .unmarshallElement("tampered-logout-response-openam.xml");
        messageContext.setInboundSAMLMessage(logoutResponse);
        messageContext.setTenantDomain("coolIssuer3");
        assertFailure("Altered IssueInstant, signature must be invalid.");
    }

    /**
     * Test Response, LogoutRequest and LogoutRespons with an invalid certificate.
     * 
     * @throws Exception
     */
    @Test
    public void testInvalidCertificate() throws Exception
    {
        X509Certificate cert = SAMLTestHelper.getUnknownCertificate();
        policyRule.setSamlTrustEngineStore(SAMLTestHelper.buildTestTrustEngine(cert));
        assertNotNull(policyRule);

        // Response to the AuthnRequest
        SAMLObject response = (SAMLObject)SAMLTestHelper.unmarshallElement("response-openam.xml");
        messageContext.setInboundSAMLMessage(response);
        messageContext.setTenantDomain("coolIssuer1");
        assertFailure("Invalid Certificate.");

        // Logout Request
        SAMLObject logoutRequest = (SAMLObject)SAMLTestHelper.unmarshallElement("logout-request-openam.xml");
        messageContext.setInboundSAMLMessage(logoutRequest);
        messageContext.setTenantDomain("coolIssuer2");
        assertFailure("Invalid Certificate.");

        // Logout Response
        SAMLObject logoutResponse = (SAMLObject)SAMLTestHelper.unmarshallElement("logout-response-openam.xml");
        messageContext.setInboundSAMLMessage(logoutResponse);
        messageContext.setTenantDomain("coolIssuer3");
        assertFailure("Invalid Certificate.");
    }

    /**
     * Test Response, LogoutRequest and LogoutRespons which were not signed.
     * 
     * @throws Exception
     */
    @Test
    public void testUnsignedSamlMessage() throws Exception
    {
        // Response to the AuthnRequest
        SAMLObject response = (SAMLObject)SAMLTestHelper.unmarshallElement("unsigned-response-openam.xml");
        messageContext.setInboundSAMLMessage(response);
        messageContext.setTenantDomain("coolIssuer1");
        assertFailure("Response was not signed.");

        // Logout Request
        SAMLObject logoutRequest = (SAMLObject)SAMLTestHelper.unmarshallElement("unsigned-logout-request-openam.xml");
        messageContext.setInboundSAMLMessage(logoutRequest);
        messageContext.setTenantDomain("coolIssuer2");
        assertFailure("Response was not signed.");

        // Logout Response
        SAMLObject logoutResponse = (SAMLObject)SAMLTestHelper.unmarshallElement("unsigned-logout-response-openam.xml");
        messageContext.setInboundSAMLMessage(logoutResponse);
        messageContext.setTenantDomain("coolIssuer3");
        assertFailure("Response was not signed.");
    }

    private void assertSuccess(String msg)
    {
        try
        {
            policyRule.evaluate(messageContext);
        }
        catch(SecurityPolicyException e)
        {
            fail("Security policy rule failed, expected success: " + msg + ": " + e);
        }
    }

    private void assertFailure(String msg)
    {
        try
        {
            policyRule.evaluate(messageContext);
            fail("Security policy rule succeeded, expected failure: " + msg);
        }
        catch(SecurityPolicyException e)
        {
            // Do nothing, expected
            return;
        }
    }
}
