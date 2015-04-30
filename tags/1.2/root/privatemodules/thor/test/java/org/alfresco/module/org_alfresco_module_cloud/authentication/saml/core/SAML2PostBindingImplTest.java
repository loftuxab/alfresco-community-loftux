/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.security.cert.X509Certificate;
import java.util.Map;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLAuthnRequestBuilder;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLLogoutRequestBuilder;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLLogoutResponseBuilder;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.util.ApplicationContextHelper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.binding.SAMLMessageContext;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.LogoutRequest;
import org.opensaml.saml2.core.LogoutResponse;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.metadata.Endpoint;
import org.opensaml.saml2.metadata.SingleLogoutService;
import org.opensaml.saml2.metadata.SingleSignOnService;
import org.springframework.context.ApplicationContext;

/**
 * Test code for {@link SAML2PostBindingImpl}.
 * 
 * @author jkaabimofrad
 * @since Cloud SAML
 */
public class SAML2PostBindingImplTest
{
    private static ApplicationContext context;
    protected static RetryingTransactionHelper transactionHelper;
    private static SAMLBinding samlBinding;
    private static SAMLXMLSignatureSecurityPolicyRule policyRule;
    private static X509Certificate cert;

    private AlfrescoSAMLMessageContext messageContext;;

    @BeforeClass
    public static void initStaticData() throws Exception
    {
        context = ApplicationContextHelper.getApplicationContext();
        samlBinding = (SAMLBinding)context.getBean("samlBinding");
        transactionHelper = (RetryingTransactionHelper)context.getBean("retryingTransactionHelper");
        policyRule = (SAMLXMLSignatureSecurityPolicyRule)context.getBean("samlXMLSignatureSecurityPolicyRule");
        cert = SAMLTestHelper.getDefaultCertificate();
    }

    @Before
    public void initSecurityPolicyRuleTest() throws Exception
    {
        policyRule.setSamlTrustEngineStore(SAMLTestHelper.buildTestTrustEngine(cert));
        messageContext = new AlfrescoSAMLMessageContext();
    }

    /**
     * Test Response, LogoutRequest and LogoutRespons which their IssueInstant date has already expired.
     * 
     * @throws Exception
     */
    @Test
    public void testDecodeInvalidSamlMessage() throws Exception
    {
        // Response to the AuthnRequest
        messageContext.setSamlResponse(SAMLTestHelper.RESPONSE_OPENAM);
        messageContext.setTenantDomain("someIssuer1");
        messageContext.setSpAcsURL("http://localhost:8081/share/openam.test/saml/authnresponse");
        assertDecodeFailure("issue instant expiration.");

        // Logout Request
        messageContext.setSamlRequest(SAMLTestHelper.LOGOUT_REQUEST_OPENAM);
        messageContext.setTenantDomain("someIssuer2");
        messageContext.setSpAcsURL("http://localhost:8081/share/openam.test/saml/logoutrequest");
        assertDecodeFailure("issue instant expiration.");

        // Logout Response
        messageContext.setSamlResponse(SAMLTestHelper.LOGOUT_RESPONSE_OPENAM);
        messageContext.setTenantDomain("someIssuer2");
        messageContext.setSpAcsURL("http://localhost:8081/share/openam.test/saml/logoutresponse");
        assertDecodeFailure("issue instant expiration.");
    }

    /**
     * Test valid Response.
     * 
     * @throws Exception
     */
    @Test
    public void testDecodeValidSamlResponse() throws Exception
    {
        Response response = SAMLTestHelper.buildValidTestResponse();

        Endpoint endpoint = SAMLUtil.generateEndpoint(SingleSignOnService.DEFAULT_ELEMENT_NAME,
            "http://somedomain.com/acs", null);
        Map<String, String> encodedResponse = samlBinding.encodeSignSAMLMessage(response, endpoint,
            SAMLTestHelper.getTestSigningCredential(), "veryCoolIssuer");

        // Response
        messageContext.setSamlResponse(encodedResponse.get("SAMLResponse"));
        messageContext.setTenantDomain("someIssuer1");
        messageContext.setSpAcsURL("http://somedomain.com/acs");
        assertDecodeSuccess("valid response.");
    }

    /**
     * Test Response, LogoutRequest and LogoutRespons are encoded and signed.
     * 
     * @throws Exception
     */
    @Test
    public void testEncode() throws Exception
    {
        String issuerName = "coolIssuer";
        String idpSso = "http://idpdomain.com/sso";
        String idpSlo = "http://idpdomain.com/slo";

        /*
         * Create AuthnRequest
         */
        Endpoint endpoint = SAMLUtil.generateEndpoint(SingleSignOnService.DEFAULT_ELEMENT_NAME, idpSso, null);
        AuthnRequest authnRequest = new SAMLAuthnRequestBuilder.Builder().withIssuer(issuerName)
            .withDestinationURL(idpSso).withSpAcsURL("http://coolIssuer.com/acs").withDefaultNameIDPolicy().build();

        assertFalse("AuthRequest is not signed yet.", authnRequest.isSigned());

        Map<String, String> resultMap = samlBinding.encodeSignSAMLMessage(authnRequest, endpoint,
            SAMLTestHelper.getTestSigningCredential(), issuerName);

        assertNotNull(resultMap);
        assertEquals(5, resultMap.size());
        assertTrue("AuthRequest must be signed.", authnRequest.isSigned());

        /*
         * Create LogoutRequest
         */
        Endpoint logoutRequestEndpoint = SAMLUtil.generateEndpoint(SingleLogoutService.DEFAULT_ELEMENT_NAME, idpSlo,
            "http://coolIssuer.com/slo");
        LogoutRequest logoutRequest = new SAMLLogoutRequestBuilder.Builder().withIssuer(issuerName)
            .withDestinationURL(idpSlo).withNameID("testUser@test.com").build("9yQvHjwhN6jUG1NoTIXReh");

        assertFalse("LogoutRequest is not signed yet.", logoutRequest.isSigned());

        resultMap = samlBinding.encodeSignSAMLMessage(logoutRequest, logoutRequestEndpoint,
            SAMLTestHelper.getTestSigningCredential(), issuerName);

        assertNotNull(resultMap);
        assertEquals(5, resultMap.size());
        assertTrue("LogoutRequest must be signed.", logoutRequest.isSigned());

        /*
         * Create LogoutResponse
         */
        Endpoint logoutResponseEndpoint = SAMLUtil.generateEndpoint(SingleLogoutService.DEFAULT_ELEMENT_NAME, idpSlo,
            null);
        LogoutResponse logoutResponse = new SAMLLogoutResponseBuilder.Builder().withDestinationURL(idpSlo)
            .withInResponseTo(SAMLUtil.generateUUID()).withIssuer(issuerName)
            .withStatusCode(SAMLStatusCode.SUCCESS_URI).build();

        assertFalse("LogoutResponse is not signed yet.", logoutResponse.isSigned());

        resultMap = samlBinding.encodeSignSAMLMessage(logoutResponse, logoutResponseEndpoint,
            SAMLTestHelper.getTestSigningCredential(), issuerName);

        assertNotNull(resultMap);
        assertEquals(2, resultMap.size());
        assertTrue("LogoutResponse must be signed.", logoutResponse.isSigned());
    }

    private SAMLMessageContext<SAMLObject, SAMLObject, SAMLObject> decodeInTransaction() throws Exception
    {
        return transactionHelper
            .doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<SAMLMessageContext<SAMLObject, SAMLObject, SAMLObject>>()
            {
                @Override
                public SAMLMessageContext<SAMLObject, SAMLObject, SAMLObject> execute() throws Throwable
                {
                    return samlBinding.decodeSignedSAMLMessage(messageContext);
                }
            });
    }

    private void assertDecodeFailure(String msg)
    {
        try
        {
            decodeInTransaction();
            fail("Security policy rule succeeded, expected failure: " + msg);
        }
        catch(Exception e)
        {
            // Do nothing, expected
            return;
        }
    }

    private void assertDecodeSuccess(String msg)
    {
        try
        {
            decodeInTransaction();
        }
        catch(Exception e)
        {
            fail("Security policy rule failed, expected success: " + msg + ": " + e);
        }
    }
}
