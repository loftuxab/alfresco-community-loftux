/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.webscripts;

import java.io.StringWriter;
import java.util.Map;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountType;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLLogoutRequestBuilder;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLLogoutResponseBuilder;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLStatusCode;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLTestHelper;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.opensaml.saml2.core.LogoutRequest;
import org.opensaml.saml2.core.LogoutResponse;
import org.opensaml.saml2.metadata.Endpoint;
import org.opensaml.saml2.metadata.SingleLogoutService;
import org.springframework.extensions.webscripts.TestWebScriptServer;
import org.springframework.extensions.webscripts.TestWebScriptServer.Response;
import com.google.gdata.util.common.util.Base64;

/**
 * Test code for {@link SLORequestGet}, {@link SLORequestPost} and {@link SLOResponsePost}.
 * 
 * @author jkaabimofrad
 * @since Cloud SAML
 */
public class SLORestApiTest extends AbstractSAMLWebScriptTestHelper
{
    private static final String GET_SLO_REQUEST_PREFIX_URL = "/internal/saml/slo/";
    private static final String POST_SLO_REQUEST_PREFIX_URL = "/internal/saml/slo-request/";
    private static final String POST_SLO_RESPONSE_PREFIX_URL = "/internal/saml/slo-response/";

    private String testTenant;
    private String testUser1;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        testTenant = cloudContext.createTenantName("acme");
        testUser1 = cloudContext.createUserName("testuser1", testTenant);

        // Set the current security context as admin
        AuthenticationUtil.setAdminUserAsFullyAuthenticatedUser();

        createAccount(testTenant, AccountType.ENTERPRISE_NETWORK_ACCOUNT_TYPE, true);
        createUserAsNetworkAdmin(testUser1, "John", "Doe", "password");
    }

    @Override
    protected void tearDown() throws Exception
    {
        cleanup(testTenant);
        cloudContext.cleanup();
    }

    public void testSLORequestGetInNotSamlEnabledNetwork() throws Exception
    {
        // Get SLO Request from a network, which isn't SAML-Enabled
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                // The network is not SAML enabled
                Response resp = getSLORequest(401);
                assertEquals(401, resp.getStatus());

                return null;
            }
        }, testUser1, testTenant);
    }

    public void testValidSLORequestGet() throws Exception
    {
        // Get SLO Request
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                // No need to set the certificate here, as we won't validate the IdP's Request/Response.
                setSamlConfig(testTenant, true, "http://localhost:8081/opensso/sso",
                    "http://localhost:8081/opensso/slo", "http://localhost:8081/opensso/slo", null);

                Response resp = getSLORequest(200);

                String contentAsString = resp.getContentAsString();

                JSONObject jsonRsp = (JSONObject)JSONValue.parse(contentAsString);
                assertNotNull("Problem reading JSON", jsonRsp);

                String samlRequest = (String)jsonRsp.get("SAMLRequest");
                String signature = (String)jsonRsp.get("Signature");
                String sigAlg = (String)jsonRsp.get("SigAlg");
                String keyInfo = (String)jsonRsp.get("KeyInfo");
                String action = (String)jsonRsp.get("action");

                assertNotNull(samlRequest);
                assertNotNull(signature);
                assertNotNull(sigAlg);
                assertNotNull(keyInfo);
                assertNotNull(action);

                return null;
            }
        }, testUser1, testTenant);
    }

    public void testSLORequestPostInNotSamlEnabledNetwork() throws Exception
    {
        // Post SLO Request to a network, which isn't SAML-Enabled
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                // The network is not SAML enabled.
                Response resp = postSLORequest(SAMLTestHelper.LOGOUT_REQUEST_OPENAM, null, null, 401);
                assertEquals(401, resp.getStatus());

                return null;
            }
        }, testUser1, testTenant);
    }

    public void testValidSLORequestPost() throws Exception
    {
        // Post SLO Request
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                setSamlConfig(testTenant, true, "http://localhost:8081/someIdP/sso",
                    "http://localhost:8081/someIdP/slo", "http://localhost:8081/someIdP/slo",
                    SAMLTestHelper.getDefaultCertificate());

                String destination = authenticationService.getSpSloRequestURL(testTenant);

                // Assume this logout request has been generated by an IdP (someIdP).
                LogoutRequest logoutRequest = new SAMLLogoutRequestBuilder.Builder().withIssuer("someIdP")
                    .withDestinationURL(destination).withNameID(testUser1).build(SAMLUtil.generateUUID());

                Endpoint endpoint = SAMLUtil.generateEndpoint(SingleLogoutService.DEFAULT_ELEMENT_NAME, destination,
                    null);

                Map<String, String> encodedLogoutRequestMap = samlBinding.encodeSignSAMLMessage(logoutRequest,
                    endpoint, SAMLTestHelper.getTestSigningCredential(), "someIdP");

                String encodedLogoutRequest = encodedLogoutRequestMap.get("SAMLRequest");
                assertNotNull(encodedLogoutRequest);

                // Assume this relayState has been generated by an IdP (someIdP).
                String encodedRelayState = Base64.encode("somePageURL".getBytes());

                // Upon successful validation of the logout request, a logout response will be generated.
                Response resp = postSLORequest(encodedLogoutRequest, null, encodedRelayState, 200);

                String contentAsString = resp.getContentAsString();
                JSONObject jsonRsp = (JSONObject)JSONValue.parse(contentAsString);
                assertNotNull("Problem reading JSON", jsonRsp);
                // Get the generated logout response.
                String logoutResponse = (String)jsonRsp.get("SAMLResponse");
                String relayState = (String)jsonRsp.get("RelayState");
                String userId = (String)jsonRsp.get("userId");
                String result = (String)jsonRsp.get("result");
                String action = (String)jsonRsp.get("action");

                assertNotNull(logoutResponse);
                assertEquals("The RelayState must not be modified.", encodedRelayState, relayState);
                assertEquals(testUser1, userId);
                assertEquals("success", result);
                assertNotNull(action);

                return null;
            }
        }, testUser1, testTenant);
    }

    public void testMismatchEndpointSLORequestPost()
    {
        // Post SLO Request
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                setSamlConfig(testTenant, true, "http://localhost:8081/someIdP/sso",
                    "http://localhost:8081/someIdP/slo", "http://localhost:8081/someIdP/slo",
                    SAMLTestHelper.getDefaultCertificate());

                String destination = authenticationService.getSpSloRequestURL(testTenant);

                // Assume this logout request has been generated by an IdP (someIdP).
                LogoutRequest logoutRequest = new SAMLLogoutRequestBuilder.Builder().withIssuer("someIdP")
                    .withDestinationURL("http://mismatchEndpoint.com").withNameID(testUser1)
                    .build(SAMLUtil.generateUUID());

                Endpoint endpoint = SAMLUtil.generateEndpoint(SingleLogoutService.DEFAULT_ELEMENT_NAME, destination,
                    null);

                Map<String, String> encodedLogoutRequestMap = samlBinding.encodeSignSAMLMessage(logoutRequest,
                    endpoint, SAMLTestHelper.getTestSigningCredential(), "someIdP");

                String encodedLogoutRequest = encodedLogoutRequestMap.get("SAMLRequest");
                assertNotNull(encodedLogoutRequest);

                // SAML message intended destination endpoint will not match the recipient endpoint.
                Response resp = postSLORequest(encodedLogoutRequest, null, "somePageURL", 400);
                assertEquals(400, resp.getStatus());

                return null;
            }
        }, testUser1, testTenant);
    }

    public void testExpiredSLORequestPost()
    {
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                setSamlConfig(testTenant, true, "http://localhost:8081/someIdP/sso",
                    "http://localhost:8081/someIdP/slo", "http://localhost:8081/someIdP/slo",
                    SAMLTestHelper.getDefaultCertificate());

                // Send an expired SLO Request
                Response resp = postSLORequest(SAMLTestHelper.LOGOUT_REQUEST_OPENAM, null, "somePageURL", 400);
                assertEquals(400, resp.getStatus());

                return null;
            }
        }, testUser1, testTenant);
    }

    public void testValidateSLORequestPostWithWrongCertificate()
    {
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                // Set a wrong certificate for this network
                setSamlConfig(testTenant, true, "http://localhost:8081/someIdP/sso",
                    "http://localhost:8081/someIdP/slo", "http://localhost:8081/someIdP/slo",
                    SAMLTestHelper.getUnknownCertificate());

                /*
                 * The "LOGOUT_REQUEST" is expired. But we don't care about that,
                 * as first you need to decode the message in order to validate its expiration.
                 * And as the certificate is wrong,it will fail in decoding.
                 */
                Response resp = postSLORequest(SAMLTestHelper.LOGOUT_REQUEST_OPENAM, null, "somePageURL", 400);
                assertEquals(400, resp.getStatus());

                return null;
            }
        }, testUser1, testTenant);
    }

    public void testSLOResponseInNotSamlEnabledNetwork() throws Exception
    {
        // Post SLO Response to a network, which isn't SAML-Enabled
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                // The network is not SAML enabled.
                Response resp = postSLOResponse(SAMLTestHelper.LOGOUT_RESPONSE_OPENAM, null, 401);
                assertEquals(401, resp.getStatus());

                return null;
            }
        }, testUser1, testTenant);
    }

    public void testValidSLOResponse() throws Exception
    {
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                setSamlConfig(testTenant, true, "http://localhost:8081/someIdP/sso",
                    "http://localhost:8081/someIdP/slo", "http://localhost:8081/someIdP/slo",
                    SAMLTestHelper.getDefaultCertificate());

                // Assume this logout response has been generated by an IdP (someIdP).
                LogoutResponse logoutResponse = new SAMLLogoutResponseBuilder.Builder()
                    .withDestinationURL(authenticationService.getSpSloResponseURL(testTenant))
                    .withInResponseTo(SAMLUtil.generateUUID()).withIssuer("someIdP")
                    .withStatusCode(SAMLStatusCode.SUCCESS_URI).build();

                Endpoint endpoint = SAMLUtil.generateEndpoint(SingleLogoutService.DEFAULT_ELEMENT_NAME,
                    authenticationService.getSpSloResponseURL(testTenant), null);

                Map<String, String> encodedLogoutResponse = samlBinding.encodeSignSAMLMessage(logoutResponse, endpoint,
                    SAMLTestHelper.getTestSigningCredential(), "someIdP");

                Response resp = postSLOResponse(encodedLogoutResponse.get("SAMLResponse"), null, 200);

                String contentAsString = resp.getContentAsString();
                JSONObject jsonRsp = (JSONObject)JSONValue.parse(contentAsString);

                assertNotNull("Problem reading JSON", jsonRsp);
                String userId = (String)jsonRsp.get("userId");
                assertEquals(testUser1, userId);

                return null;
            }
        }, testUser1, testTenant);
    }

    public void testUnsuccessfulSLOResponse() throws Exception
    {
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                setSamlConfig(testTenant, true, "http://localhost:8081/someIdP/sso",
                    "http://localhost:8081/someIdP/slo", "http://localhost:8081/someIdP/slo",
                    SAMLTestHelper.getDefaultCertificate());

                // Assume this logout response has been generated by an IdP (someIdP). The response was Unsuccessful.
                LogoutResponse logoutResponse = new SAMLLogoutResponseBuilder.Builder()
                    .withDestinationURL(authenticationService.getSpSloResponseURL(testTenant))
                    .withInResponseTo(SAMLUtil.generateUUID()).withIssuer("someIdP")
                    .withStatusCode(SAMLStatusCode.UNKNOWN_PRINCIPAL_URI).build();

                Endpoint endpoint = SAMLUtil.generateEndpoint(SingleLogoutService.DEFAULT_ELEMENT_NAME,
                    authenticationService.getSpSloResponseURL(testTenant), null);

                Map<String, String> encodedLogoutResponse = samlBinding.encodeSignSAMLMessage(logoutResponse, endpoint,
                    SAMLTestHelper.getTestSigningCredential(), "someIdP");

                Response resp = postSLOResponse(encodedLogoutResponse.get("SAMLResponse"), null, 400);
                assertEquals(400, resp.getStatus());

                return null;
            }
        }, testUser1, testTenant);
    }

    public void testValidateSLOResponseWithWrongCertificate()
    {
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                // Set a wrong certificate for this network
                setSamlConfig(testTenant, true, "http://localhost:8081/someIdP/sso",
                    "http://localhost:8081/someIdP/slo", "http://localhost:8081/someIdP/slo",
                    SAMLTestHelper.getUnknownCertificate());

                /*
                 * The "LOGOUT_RESPONSE" is expired. But we don't care about that,
                 * as first you need to decode the message in order to validate its expiration.
                 * And as the certificate is wrong,it will fail in decoding.
                 */
                Response resp = postSLOResponse(SAMLTestHelper.LOGOUT_RESPONSE_OPENAM, null, 400);
                assertEquals(400, resp.getStatus());

                return null;
            }
        }, testUser1, testTenant);
    }

    @SuppressWarnings("unchecked")
    private Response postSLORequest(String base64EncodedLogoutRequest, String signature, String relayState,
        int expectedStatus) throws Exception
    {
        JSONObject obj = new JSONObject();
        obj.put("SAMLRequest", base64EncodedLogoutRequest);
        obj.put("Signature", signature);
        obj.put("RelayState", relayState);

        StringWriter stringWriter = new StringWriter();
        obj.writeJSONString(stringWriter);
        String jsonString = stringWriter.toString();

        Response resp = sendRequest(new TestWebScriptServer.PostRequest(POST_SLO_REQUEST_PREFIX_URL + testTenant,
            jsonString, APPLICATION_JSON), expectedStatus);

        return resp;
    }

    @SuppressWarnings("unchecked")
    private Response postSLOResponse(final String base64EncodedLogoutRequest, final String signature,
        final int expectedStatus) throws Exception
    {
        return TenantUtil.runAsUserTenant(new TenantRunAsWork<Response>()
        {
            @Override
            public Response doWork() throws Exception
            {
                JSONObject obj = new JSONObject();
                obj.put("SAMLResponse", base64EncodedLogoutRequest);
                obj.put("Signature", signature);

                StringWriter stringWriter = new StringWriter();
                obj.writeJSONString(stringWriter);
                String jsonString = stringWriter.toString();

                Response resp = sendRequest(new TestWebScriptServer.PostRequest(POST_SLO_RESPONSE_PREFIX_URL
                    + testTenant, jsonString, APPLICATION_JSON), expectedStatus);

                return resp;
            }
        }, testUser1, testTenant);
    }

    private Response getSLORequest(final int expectedStatus) throws Exception
    {
        // Get SLO Request
        return TenantUtil.runAsUserTenant(new TenantRunAsWork<Response>()
        {
            @Override
            public Response doWork() throws Exception
            {
                Response resp = sendRequest(
                    new TestWebScriptServer.GetRequest(GET_SLO_REQUEST_PREFIX_URL + SAMLUtil.generateUUID()),
                    expectedStatus);

                return resp;
            }
        }, testUser1, testTenant);
    }
}
