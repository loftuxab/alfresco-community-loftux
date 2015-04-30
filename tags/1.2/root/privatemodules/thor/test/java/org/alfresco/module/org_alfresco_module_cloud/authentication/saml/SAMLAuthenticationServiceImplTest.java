/*
 * Copyright 2005-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.CloudTestContext;
import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountType;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.AlfrescoSAMLMessageContext;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLBinding;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLCertificateUtil;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLStatusCode;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLTestHelper;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLUtil;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLXMLSignatureSecurityPolicyRule;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.util.ApplicationContextHelper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.LogoutRequest;
import org.opensaml.saml2.core.LogoutResponse;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.metadata.Endpoint;
import org.opensaml.saml2.metadata.SingleLogoutService;
import org.opensaml.saml2.metadata.SingleSignOnService;
import org.springframework.context.ApplicationContext;

/**
 * Test code for {@link SAMLAuthenticationServiceImpl}.
 * 
 * @author jkaabimofrad
 * @since Cloud SAML
 */
public class SAMLAuthenticationServiceImplTest
{
    private static ApplicationContext context;
    private static SAMLAuthenticationService authenticationService;
    private static SAMLBinding samlBinding;
    private static SAMLXMLSignatureSecurityPolicyRule policyRule;
    private static X509Certificate cert;
    private static RetryingTransactionHelper transactionHelper;
    private static AccountService accountService;
    private static CloudTestContext cloudContext;
    private static String testTenant;

    private AlfrescoSAMLMessageContext messageContext;;

    @BeforeClass
    public static void initStaticData() throws Exception
    {
        context = ApplicationContextHelper.getApplicationContext();
        authenticationService = (SAMLAuthenticationService)context.getBean("samlAuthenticationService");
        samlBinding = (SAMLBinding)context.getBean("samlBinding");
        policyRule = (SAMLXMLSignatureSecurityPolicyRule)context.getBean("samlXMLSignatureSecurityPolicyRule");
        cert = SAMLTestHelper.getDefaultCertificate();
        transactionHelper = (RetryingTransactionHelper)context.getBean("retryingTransactionHelper");
        accountService = (AccountService)context.getBean("accountService");

        cloudContext = new CloudTestContext(context);
        testTenant = cloudContext.createTenantName("acme");
        createAccount(testTenant, AccountType.ENTERPRISE_NETWORK_ACCOUNT_TYPE, true);
    }

    @AfterClass
    public static void cleanup() throws Exception
    {
        cloudContext.cleanup();
    }

    @Before
    public void initSecurityPolicyRuleTest() throws Exception
    {
        policyRule.setSamlTrustEngineStore(SAMLTestHelper.buildTestTrustEngine(cert));
        messageContext = new AlfrescoSAMLMessageContext();
    }

    @After
    public void clearCurrentSecurityContext() throws Exception
    {
        AuthenticationUtil.clearCurrentSecurityContext();
    }

    @Test
    public void testProcessSamlAuthnResponse() throws Exception
    {
        /*
         * Test valid response
         */
        Response response = SAMLTestHelper.buildValidTestResponse();

        Endpoint endpoint = SAMLUtil.generateEndpoint(SingleSignOnService.DEFAULT_ELEMENT_NAME,
            "http://sp-domain.com/acs", null);
        Map<String, String> encodedResponse = samlBinding.encodeSignSAMLMessage(response, endpoint,
            SAMLTestHelper.getTestSigningCredential(), "veryCoolIssuer");

        // Encoded valid response
        messageContext.setSamlResponse(encodedResponse.get("SAMLResponse"));
        messageContext.setTenantDomain("someIssuer1");
        messageContext.setSpAcsURL("http://sp-domain.com/acs");

        SAMLUser user = authenticationService.processSamlAuthnResponse(messageContext);
        assertEquals(response.getID(), user.getSamlID());
        assertEquals("test1@test.com", user.getUser().getEmail());

        /*
         * Test response without attribute
         */
        response = SAMLTestHelper.buildTestResponseWithoutAttribute();
        encodedResponse = samlBinding.encodeSignSAMLMessage(response, endpoint,
            SAMLTestHelper.getTestSigningCredential(), "veryCoolIssuer");

        // Encoded response without attribute
        messageContext.setSamlResponse(encodedResponse.get("SAMLResponse"));
        assertProcessResponseFailure("Response must have attribute.");

        /*
         * Test response without Email attribute
         */
        response = SAMLTestHelper.buildTestResponseWithoutEmailAttribute();
        encodedResponse = samlBinding.encodeSignSAMLMessage(response, endpoint,
            SAMLTestHelper.getTestSigningCredential(), "veryCoolIssuer");

        // Encoded response without Email attribute
        messageContext.setSamlResponse(encodedResponse.get("SAMLResponse"));
        assertProcessResponseFailure("Response must have Email attribute.");

        /*
         * Test response with invalid Email attribute's value
         */
        response = SAMLTestHelper.buildTestResponseWithInvalidEmailAttribute();
        encodedResponse = samlBinding.encodeSignSAMLMessage(response, endpoint,
            SAMLTestHelper.getTestSigningCredential(), "veryCoolIssuer");

        // Encoded response with invalid Email value
        messageContext.setSamlResponse(encodedResponse.get("SAMLResponse"));
        assertProcessResponseFailure("Response must have a valid Email attribute's value.");

        /*
         * Test response with unsuccessful status code
         */
        response = SAMLTestHelper.buildTestResponseWithUnsuccessfulStatus();
        encodedResponse = samlBinding.encodeSignSAMLMessage(response, endpoint,
            SAMLTestHelper.getTestSigningCredential(), "veryCoolIssuer");

        // Encoded response with unsuccessful status code
        messageContext.setSamlResponse(encodedResponse.get("SAMLResponse"));
        assertProcessResponseFailure("Unsuccessful response. Authentication Failed.");

        /*
         * Test expired response
         */
        messageContext.setSamlResponse(SAMLTestHelper.RESPONSE_OPENAM);
        assertProcessResponseFailure("Response has expired.");

        /*
         * Test tampered response
         */
        messageContext.setSamlResponse(SAMLTestHelper.getResourceAsBase64EncodedString("tampered-response-openam.xml"));
        messageContext.setSpAcsURL("http://localhost:8081/share/openam.test/saml/authnresponse");
        assertProcessResponseFailure("Response is tampered.");

        /*
         * Test unsigned response
         */
        messageContext.setSamlResponse(SAMLTestHelper.getResourceAsBase64EncodedString("unsigned-response-openam.xml"));
        messageContext.setSpAcsURL("http://localhost:8081/share/openam.test/saml/authnresponse");
        assertProcessResponseFailure("Response must be signed.");

        /*
         * Test invalid certificate
         */
        messageContext.setSamlResponse(SAMLTestHelper.getResourceAsBase64EncodedString("response-pf.xml"));
        messageContext.setSpAcsURL("http://localhost:8081/share/ping.test/saml/authnresponse");
        assertProcessResponseFailure("Response cannot be validated. Invalid certificate.");
    }

    @Test
    public void testProcessSamlLogoutRequest() throws Exception
    {
        /*
         * Test valid logout request. Assume this logout request has been generated by an IdP (someIdP).
         */
        LogoutRequest logoutRequest = new SAMLLogoutRequestBuilder.Builder().withIssuer("someIdP")
            .withDestinationURL("http://sp.com/slo").withNameID("test1@test.com").build("9yQvHjwhN6jUG1NoTIXReh");

        Endpoint endpoint = SAMLUtil.generateEndpoint(SingleLogoutService.DEFAULT_ELEMENT_NAME, "http://sp.com/slo",
            null);

        Map<String, String> encodedLogoutRequest = samlBinding.encodeSignSAMLMessage(logoutRequest, endpoint,
            SAMLTestHelper.getTestSigningCredential(), "someIdP");

        messageContext.setSamlRequest(encodedLogoutRequest.get("SAMLRequest"));
        messageContext.setTenantDomain("someIdP");
        messageContext.setSpAcsURL("http://sp.com/slo");

        SAMLUser user = authenticationService.processSamlLogoutRequest(messageContext);
        assertEquals(logoutRequest.getID(), user.getSamlID());
        assertEquals("test1@test.com", user.getUser().getId());

        /*
         * Test logout request without NameID. Assume this logout request has been generated by an IdP (someIdP).
         */
        logoutRequest = new SAMLLogoutRequestBuilder.Builder().withIssuer("someIdP")
            .withDestinationURL("http://sp.com/slo").build("9yQvHjwhN6jUG1NoTIXReh");

        encodedLogoutRequest = samlBinding.encodeSignSAMLMessage(logoutRequest, endpoint,
            SAMLTestHelper.getTestSigningCredential(), "someIdP");

        messageContext.setSamlRequest(encodedLogoutRequest.get("SAMLRequest"));
        assertProcessLogoutRequestFailure("LogoutRequest must have NameID.");

        /*
         * Test logout request with wrong NameID value. Assume this logout request has been generated by an IdP
         * (someIdP).
         */
        logoutRequest = new SAMLLogoutRequestBuilder.Builder().withIssuer("someIdP")
            .withDestinationURL("http://sp.com/slo").withNameID("test1Id").build("9yQvHjwhN6jUG1NoTIXReh");

        encodedLogoutRequest = samlBinding.encodeSignSAMLMessage(logoutRequest, endpoint,
            SAMLTestHelper.getTestSigningCredential(), "someIdP");

        messageContext.setSamlRequest(encodedLogoutRequest.get("SAMLRequest"));
        assertProcessLogoutRequestFailure("LogoutRequest's NameID value must be a valid email.");

        /*
         * Test expired logout request
         */
        messageContext.setSamlRequest(SAMLTestHelper.LOGOUT_REQUEST_OPENAM);
        assertProcessLogoutRequestFailure("LogoutRequest has expired.");

        /*
         * Test tampered logout request
         */
        messageContext.setSamlRequest(SAMLTestHelper
            .getResourceAsBase64EncodedString("tampered-logout-request-openam.xml"));
        messageContext.setSpAcsURL("http://localhost:8081/share/openam.test/saml/logoutrequest");
        assertProcessLogoutRequestFailure("LogoutRequest is tampered.");

        /*
         * Test unsigned logout request
         */
        messageContext.setSamlRequest(SAMLTestHelper
            .getResourceAsBase64EncodedString("unsigned-logout-request-openam.xml"));
        messageContext.setSpAcsURL("http://localhost:8081/share/openam.test/saml/logoutrequest");
        assertProcessLogoutRequestFailure("LogoutRequest must be signed.");

        /*
         * Test invalid certificate
         */
        messageContext.setSamlRequest(SAMLTestHelper.getResourceAsBase64EncodedString("logout-request-pf.xml"));
        messageContext.setSpAcsURL("http://localhost:8081/share/ping.test/saml/logoutrequest");
        assertProcessLogoutRequestFailure("LogoutRequest cannot be validated. Invalid certificate.");
    }

    @Test
    public void testProcessSamlLogoutResponse() throws Exception
    {
        AuthenticationUtil.setFullyAuthenticatedUser("test1@test.com");
        /*
         * Test valid logout response. Assume this logout response has been generated by an IdP (someIdP).
         */
        LogoutResponse logoutResponse = new SAMLLogoutResponseBuilder.Builder().withDestinationURL("http://sp.com/slo")
            .withInResponseTo(SAMLUtil.generateUUID()).withIssuer("someIdP").withStatusCode(SAMLStatusCode.SUCCESS_URI)
            .build();

        Endpoint endpoint = SAMLUtil.generateEndpoint(SingleLogoutService.DEFAULT_ELEMENT_NAME, "http://sp.com/slo",
            null);

        Map<String, String> encodedLogoutResponse = samlBinding.encodeSignSAMLMessage(logoutResponse, endpoint,
            SAMLTestHelper.getTestSigningCredential(), "someIdP");

        messageContext.setSamlResponse(encodedLogoutResponse.get("SAMLResponse"));
        messageContext.setTenantDomain("someIdP");
        messageContext.setSpAcsURL("http://sp.com/slo");

        SAMLUser user = authenticationService.processSamlLogoutResponse(messageContext);
        assertEquals(logoutResponse.getID(), user.getSamlID());
        assertEquals("test1@test.com", user.getUser().getId());

        /*
         * Test unsuccessful logout response. Assume this logout response has been generated by an IdP (someIdP).
         */
        logoutResponse = new SAMLLogoutResponseBuilder.Builder().withDestinationURL("http://sp.com/slo")
            .withInResponseTo(SAMLUtil.generateUUID()).withIssuer("someIdP")
            .withStatusCode(SAMLStatusCode.AUTHN_FAILED_URI).build();

        endpoint = SAMLUtil.generateEndpoint(SingleLogoutService.DEFAULT_ELEMENT_NAME, "http://sp.com/slo", null);

        encodedLogoutResponse = samlBinding.encodeSignSAMLMessage(logoutResponse, endpoint,
            SAMLTestHelper.getTestSigningCredential(), "someIdP");

        messageContext.setSamlResponse(encodedLogoutResponse.get("SAMLResponse"));
        assertProcessLogoutResponseFailure("Unsuccessful logout response.");

        /*
         * Test expired logout response
         */
        messageContext.setSamlResponse(SAMLTestHelper.LOGOUT_RESPONSE_OPENAM);
        assertProcessLogoutResponseFailure("LogoutResponse has expired.");

        /*
         * Test tampered logout response
         */
        messageContext.setSamlResponse(SAMLTestHelper
            .getResourceAsBase64EncodedString("tampered-logout-response-openam.xml"));
        messageContext.setSpAcsURL("http://localhost:8081/share/openam.test/saml/logoutresponse");
        assertProcessLogoutResponseFailure("LogoutResponse is tampered.");

        /*
         * Test unsigned logout response
         */
        messageContext.setSamlResponse(SAMLTestHelper
            .getResourceAsBase64EncodedString("unsigned-logout-response-openam.xml"));
        messageContext.setSpAcsURL("http://localhost:8081/share/openam.test/saml/logoutresponse");
        assertProcessLogoutResponseFailure("LogoutResponse must be signed.");

        /*
         * Test invalid certificate
         */
        messageContext.setSamlResponse(SAMLTestHelper.getResourceAsBase64EncodedString("logout-response-pf.xml"));
        messageContext.setSpAcsURL("http://localhost:8081/share/ping.test/saml/logoutresponse");
        assertProcessLogoutResponseFailure("LogoutResponse cannot be validated. Invalid certificate.");
    }

    @Test
    public void testGetSpPublicCertificate() throws Exception
    {
        String base64EncodedCert = authenticationService.getSpPublicCertificate();
        X509Certificate cert = SAMLCertificateUtil.generateCertificate(SAMLCertificateUtil
            .decodeCertificate(base64EncodedCert));

        assertNotNull(cert);
        assertEquals(cert.getNotAfter().toString(), SAMLTestHelper.getDefaultCertificate().getNotAfter().toString());
        assertEquals(cert.getIssuerDN().getName(), SAMLTestHelper.getDefaultCertificate().getIssuerDN().getName());
        assertArrayEquals(cert.getPublicKey().getEncoded(), SAMLTestHelper.getDefaultCertificate().getPublicKey()
            .getEncoded());

        // unknown certificate
        assertFalse(Arrays.equals(cert.getPublicKey().getEncoded(), SAMLTestHelper.getUnknownCertificate()
            .getPublicKey().getEncoded()));
    }

    @Test
    public void testGetSamlAuthnRequestParameters() throws Exception
    {
        SAMLResultMap resultMap = transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<SAMLResultMap>()
        {
            @Override
            public SAMLResultMap execute() throws Throwable
            {
                return authenticationService.getSamlAuthnRequestParameters("http://someIdP.com/sso", testTenant);
            }
        });

        Map<String, String> map = resultMap.getResultMap();
        assertNotNull(map);
        assertEquals(5, map.size());
        AuthnRequest authnRequest = SAMLTestHelper.unmarshallBase64EncodedElement(map.get("SAMLRequest"));
        assertTrue("AuthRequest must be signed.", authnRequest.isSigned());
        assertEquals("http://someIdP.com/sso", authnRequest.getDestination());
        assertEquals("http://www.w3.org/2000/09/xmldsig#rsa-sha1", authnRequest.getSignature().getSignatureAlgorithm());

        /*
         * Test null parameters
         */
        try
        {
            resultMap = authenticationService.getSamlAuthnRequestParameters(null, testTenant);
            fail("Expected failure. IdpSSOServiceURL cannot be null.");
        }
        catch(Exception e)
        {
            try
            {
                resultMap = authenticationService.getSamlAuthnRequestParameters("http://someIdP.com/sso", null);
                fail("Expected failure. Tenant domain cannot be null.");
            }
            catch(Exception ex)
            {
                // do nothing
            }
        }
    }

    @Test
    public void testGetSamlLogoutRequestParameters() throws Exception
    {
        final String sessionIndex = SAMLUtil.generateUUID();

        SAMLResultMap resultMap = transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<SAMLResultMap>()
        {
            @Override
            public SAMLResultMap execute() throws Throwable
            {
                return authenticationService.getSamlLogoutRequestParameters("user1@test.com", sessionIndex, "http://someIdP.com/slo", testTenant);
            }
        });

        Map<String, String> map = resultMap.getResultMap();
        assertNotNull(map);
        assertEquals(5, map.size());
        LogoutRequest logoutRequest = SAMLTestHelper.unmarshallBase64EncodedElement(map.get("SAMLRequest"));
        assertTrue("LogoutRequest must be signed.", logoutRequest.isSigned());
        assertEquals("http://someIdP.com/slo", logoutRequest.getDestination());
        assertEquals("user1@test.com", logoutRequest.getNameID().getValue());
        assertEquals(sessionIndex, logoutRequest.getSessionIndexes().get(0).getSessionIndex());

        /*
         * Test null parameters
         */
        assertGetSamlLogoutRequestParameters(null, sessionIndex, "http://someIdP.com/slo", testTenant);
        assertGetSamlLogoutRequestParameters("user1@test.com", null, "http://someIdP.com/slo", testTenant);
        assertGetSamlLogoutRequestParameters("user1@test.com", sessionIndex, null, testTenant);
        assertGetSamlLogoutRequestParameters("user1@test.com", sessionIndex, "http://someIdP.com/slo", null);
    }

    @Test
    public void testGetSamlLogoutResponseParameters() throws Exception
    {
        final String logoutRequestId = SAMLUtil.generateUUID();

        SAMLResultMap resultMap = transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<SAMLResultMap>()
        {
            @Override
            public SAMLResultMap execute() throws Throwable
            {
                return authenticationService.getSamlLogoutResponseParameters("http://someIdP.com/slo", logoutRequestId, SAMLStatusCode.SUCCESS_URI, testTenant);
            }
        });

        Map<String, String> map = resultMap.getResultMap();
        assertNotNull(map);
        assertEquals(2, map.size());
        LogoutResponse logoutResponse = SAMLTestHelper.unmarshallBase64EncodedElement(map.get("SAMLResponse"));
        assertTrue("LogoutRequest must be signed.", logoutResponse.isSigned());
        assertEquals("http://someIdP.com/slo", logoutResponse.getDestination());
        assertEquals(logoutRequestId, logoutResponse.getInResponseTo());
        assertEquals(SAMLStatusCode.SUCCESS_URI.getStatusCodeURI(), logoutResponse.getStatus().getStatusCode()
            .getValue());

        /*
         * Test null parameters
         */
        assertGetSamlLogoutResponseParameters(null, logoutRequestId, SAMLStatusCode.SUCCESS_URI, testTenant);
        assertGetSamlLogoutResponseParameters("http://someIdP.com/slo", null, SAMLStatusCode.SUCCESS_URI, testTenant);
        assertGetSamlLogoutResponseParameters("http://someIdP.com/slo", logoutRequestId, null, testTenant);
        assertGetSamlLogoutResponseParameters("http://someIdP.com/slo", logoutRequestId, SAMLStatusCode.SUCCESS_URI, null);
    }

    private void assertProcessResponseFailure(String msg)
    {
        try
        {
            authenticationService.processSamlAuthnResponse(messageContext);
            fail("Expected failure: " + msg);
        }
        catch(Exception e)
        {
            // Do nothing, expected
            return;
        }
    }

    private void assertProcessLogoutResponseFailure(String msg)
    {
        try
        {
            authenticationService.processSamlLogoutResponse(messageContext);
            fail("Expected failure: " + msg);
        }
        catch(Exception e)
        {
            // Do nothing, expected
            return;
        }
    }

    private void assertProcessLogoutRequestFailure(String msg)
    {
        try
        {
            SAMLUser samlUser = authenticationService.processSamlLogoutRequest(messageContext);
            if(samlUser.getUser() != null)
            {
                fail("Expected failure: " + msg);
            }
        }
        catch(Exception e)
        {
            // Do nothing, expected
            return;
        }
    }

    private void assertGetSamlLogoutRequestParameters(String userId, String idpSessionIndex,
        String idpSloRequestServiceURL, String tenantDomain)
    {
        try
        {
            authenticationService.getSamlLogoutRequestParameters(userId, idpSessionIndex, idpSloRequestServiceURL,
                tenantDomain);
            fail("Expected failure. Parameters cannot be null.");
        }
        catch(Exception e)
        {
            // Do nothing, expected
            return;
        }
    }

    private void assertGetSamlLogoutResponseParameters(String idpSloResponseServiceURL, String idpSloRequestId,
        SAMLStatusCode samlStatusCode, String tenantDomain)
    {
        try
        {
            authenticationService.getSamlLogoutResponseParameters(idpSloResponseServiceURL, idpSloRequestId,
                samlStatusCode, tenantDomain);
            fail("Expected failure. Parameters cannot be null.");
        }
        catch(Exception e)
        {
            // Do nothing, expected
            return;
        }
    }

    private static Account createAccount(final String domain, final int type, final boolean enabled) throws Exception
    {
        return transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Account>()
        {
            @Override
            public Account execute() throws Throwable
            {
                Account account = accountService.createAccount(domain, type, enabled);
                cloudContext.addAccount(account);
                return account;
            }
        });
    }
}
