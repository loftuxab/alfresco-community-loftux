/*
 * Copyright 2005-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.webscripts;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountType;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLConfigSettings;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLCertificateUtil;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLTestHelper;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.extensions.webscripts.TestWebScriptServer;
import org.springframework.extensions.webscripts.TestWebScriptServer.PostRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.Response;

import com.google.gdata.util.common.util.Base64;

/**
 * Test code for {@link SAMLConfigAdminGet}, {@link SAMLConfigAdminPostPut}, {@link SAMLConfigAdminDelete} and
 * {@link SAMLConfigAdminMultipartPost}.
 * 
 * @author jkaabimofrad
 * @since Cloud SAML
 */
public class SAMLConfigAdminRestApiTest extends AbstractSAMLWebScriptTestHelper
{
    private static final String SAML_CONFIG_URL = "/saml/config";
    private static final String SAML_CONFIG_MULTIPART_URL = "/saml/config/multipart";

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
    }

    @Override
    protected void tearDown() throws Exception
    {
        cleanup(testTenant);
        cloudContext.cleanup();
    }

    public void testPostSamlConfigInNotEnterpriseNetwork() throws Exception
    {
        createAccount(testTenant, AccountType.STANDARD_NETWORK_ACCOUNT_TYPE, true);
        createUserAsNetworkAdmin(testUser1, "John", "Doe", "password");

        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                // The network is not enterprise
                Response resp = postSamlConfig(true, "http://localhost:8081/someIdP/sso",
                    "http://localhost:8081/someIdP/slo", "http://localhost:8081/someIdP/slo",
                    SAMLCertificateUtil.encodeCertificate(SAMLTestHelper.getDefaultCertificate()), 403);
                assertEquals(403, resp.getStatus());

                return null;
            }
        }, testUser1, testTenant);
    }

    public void testPostSamlConfigAsNotNetworkAdmin() throws Exception
    {
        createAccount(testTenant, AccountType.ENTERPRISE_NETWORK_ACCOUNT_TYPE, true);
        createUser(testUser1, "John", "Doe", "password");

        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                // The user is not a network Admin
                Response resp = postSamlConfig(true, "http://localhost:8081/someIdP/sso",
                    "http://localhost:8081/someIdP/slo", "http://localhost:8081/someIdP/slo",
                    SAMLCertificateUtil.encodeCertificate(SAMLTestHelper.getDefaultCertificate()), 401);
                assertEquals(401, resp.getStatus());

                return null;
            }
        }, testUser1, testTenant);
    }

    public void testPostSamlConfig() throws Exception
    {
        createAccount(testTenant, AccountType.ENTERPRISE_NETWORK_ACCOUNT_TYPE, true);
        createUserAsNetworkAdmin(testUser1, "John", "Doe", "password");

        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                Response resp = postSamlConfig(true, "http://localhost:8081/someIdP/sso",
                            "http://localhost:8081/someIdP/slo", "http://localhost:8081/someIdP/slo",
                            SAMLCertificateUtil.encodeCertificate(SAMLTestHelper.getDefaultCertificate()), 200);
                assertEquals(200, resp.getStatus());

                SAMLConfigSettings settings = getSamlConfigSettings(testTenant);
                assertTrue(settings.isSsoEnabled());
                assertEquals("http://localhost:8081/someIdP/sso", settings.getIdpSsoURL());
                assertEquals("http://localhost:8081/someIdP/slo", settings.getIdpSloRequestURL());
                assertEquals("http://localhost:8081/someIdP/slo", settings.getIdpSloResponseURL());
                assertEquals(new DateTime(SAMLTestHelper.getDefaultCertificate().getNotAfter()).toString(), settings
                            .getCertificateInfo().getExpiryDate().toString());

                return null;
            }
        }, testUser1, testTenant);
    }

    public void testPostSamlConfigWithInvalidCertificate() throws Exception
    {
        createAccount(testTenant, AccountType.ENTERPRISE_NETWORK_ACCOUNT_TYPE, true);
        createUserAsNetworkAdmin(testUser1, "John", "Doe", "password");

        // Post expired certificate
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                Response resp = postSamlConfig(true, "http://localhost:8081/someIdP/sso",
                    "http://localhost:8081/someIdP/slo", "http://localhost:8081/someIdP/slo",
                    SAMLCertificateUtil.encodeCertificate(SAMLTestHelper.getExpiredCertificate()), 400);

                assertEquals(400, resp.getStatus());

                return null;
            }
        }, testUser1, testTenant);

        // Post malformed certificate
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                Response resp = postSamlConfig(true, "http://localhost:8081/someIdP/sso",
                    "http://localhost:8081/someIdP/slo", "http://localhost:8081/someIdP/slo",
                    Base64.encode("Malformed certificate file".getBytes()), 400);

                assertEquals(400, resp.getStatus());

                return null;
            }
        }, testUser1, testTenant);
    }

    public void testGetSamlConfig() throws Exception
    {
        createAccount(testTenant, AccountType.ENTERPRISE_NETWORK_ACCOUNT_TYPE, true);
        createUserAsNetworkAdmin(testUser1, "John", "Doe", "password");

        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                postSamlConfig(true, "http://localhost:8081/someIdP/sso", "http://localhost:8081/someIdP/slo",
                    "http://localhost:8081/someIdP/slo",
                    SAMLCertificateUtil.encodeCertificate(SAMLTestHelper.getDefaultCertificate()), 200);

                return null;
            }
        }, testUser1, testTenant);

        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                Response resp = getSamlConfig(200);
                String contentAsString = resp.getContentAsString();

                JSONObject jsonRsp = (JSONObject)JSONValue.parse(contentAsString);
                assertNotNull("Problem reading JSON", jsonRsp);

                boolean ssoEnabled = (Boolean)jsonRsp.get("ssoEnabled");
                String idpSsoURL = (String)jsonRsp.get("idpSsoURL");
                String idpSloRequestURL = (String)jsonRsp.get("idpSloRequestURL");
                String idpSloResponseURL = (String)jsonRsp.get("idpSloResponseURL");
                String entityID = (String)jsonRsp.get("entityID");
                JSONObject certObj = (JSONObject)jsonRsp.get("certificate");
                String status = (String)certObj.get("status");
                String iso8601ExpiryDate = (String)((JSONObject)certObj.get("expiryDate")).get("iso8601");

                assertTrue(ssoEnabled);
                assertEquals("http://localhost:8081/someIdP/sso", idpSsoURL);
                assertEquals("http://localhost:8081/someIdP/slo", idpSloRequestURL);
                assertEquals("http://localhost:8081/someIdP/slo", idpSloResponseURL);
                assertEquals(getIssuerName(testTenant), entityID);
                assertEquals("valid", status);
                assertEquals(new DateTime(SAMLTestHelper.getDefaultCertificate().getNotAfter()).toString(),
                    iso8601ExpiryDate);

                return null;
            }
        }, testUser1, testTenant);
    }

    public void testUpdateSamlConfig() throws Exception
    {
        createAccount(testTenant, AccountType.ENTERPRISE_NETWORK_ACCOUNT_TYPE, true);
        createUserAsNetworkAdmin(testUser1, "John", "Doe", "password");

        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                Response resp = postSamlConfig(true, "http://localhost:8081/someIdP/sso",
                    "http://localhost:8081/someIdP/slo", "http://localhost:8081/someIdP/slo",
                    SAMLCertificateUtil.encodeCertificate(SAMLTestHelper.getDefaultCertificate()), 200);

                assertEquals(200, resp.getStatus());

                return null;
            }
        }, testUser1, testTenant);

        // Update sso url
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                // Update sso url
                Response resp = postSamlConfig(true, "http://localhost:8081/someIdP/ChangedSsoURL", null, null, null, 200);
                assertEquals(200, resp.getStatus());

                SAMLConfigSettings settings = getSamlConfigSettings(testTenant);
                assertTrue(settings.isSsoEnabled());
                assertEquals("http://localhost:8081/someIdP/ChangedSsoURL", settings.getIdpSsoURL());
                assertEquals("http://localhost:8081/someIdP/slo", settings.getIdpSloRequestURL());
                assertEquals("http://localhost:8081/someIdP/slo", settings.getIdpSloResponseURL());
                assertEquals(new DateTime(SAMLTestHelper.getDefaultCertificate().getNotAfter()).toString(), settings
                            .getCertificateInfo().getExpiryDate().toString());

                return null;
            }
        }, testUser1, testTenant);

        // Update certificate
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                // Update certificate
                Response resp = postSamlConfig(true, null, null, null,
                            SAMLCertificateUtil.encodeCertificate(SAMLTestHelper.getUnknownCertificate()), 200);
                assertEquals(200, resp.getStatus());

                SAMLConfigSettings settings = getSamlConfigSettings(testTenant);
                assertTrue(settings.isSsoEnabled());
                assertEquals("http://localhost:8081/someIdP/ChangedSsoURL", settings.getIdpSsoURL());
                assertEquals("http://localhost:8081/someIdP/slo", settings.getIdpSloRequestURL());
                assertEquals("http://localhost:8081/someIdP/slo", settings.getIdpSloResponseURL());
                assertEquals(new DateTime(SAMLTestHelper.getUnknownCertificate().getNotAfter()).toString(), settings
                            .getCertificateInfo().getExpiryDate().toString());

                return null;
            }
        }, testUser1, testTenant);

        // Update idpSloRequestURL via PUT
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @SuppressWarnings("unchecked")
            @Override
            public Void doWork() throws Exception
            {
                JSONObject obj = new JSONObject();
                obj.put("ssoEnabled", true);
                obj.put("idpSloRequestURL", "http://localhost:8081/someIdP/NewSloURL");

                StringWriter stringWriter = new StringWriter();
                obj.writeJSONString(stringWriter);
                String jsonString = stringWriter.toString();

                Response resp = sendRequest(new TestWebScriptServer.PutRequest(SAML_CONFIG_URL, jsonString,
                            APPLICATION_JSON), 200);

                assertEquals(200, resp.getStatus());

                SAMLConfigSettings settings = getSamlConfigSettings(testTenant);
                assertTrue(settings.isSsoEnabled());
                assertEquals("http://localhost:8081/someIdP/ChangedSsoURL", settings.getIdpSsoURL());
                assertEquals("http://localhost:8081/someIdP/NewSloURL", settings.getIdpSloRequestURL());
                assertEquals("http://localhost:8081/someIdP/slo", settings.getIdpSloResponseURL());
                assertEquals(new DateTime(SAMLTestHelper.getUnknownCertificate().getNotAfter()).toString(), settings
                            .getCertificateInfo().getExpiryDate().toString());

                return null;
            }
        }, testUser1, testTenant);
    }

    public void testDeleteSamlConfig() throws Exception
    {
        createAccount(testTenant, AccountType.ENTERPRISE_NETWORK_ACCOUNT_TYPE, true);
        createUserAsNetworkAdmin(testUser1, "John", "Doe", "password");

        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                postSamlConfig(true, "http://localhost:8081/someIdP/sso", "http://localhost:8081/someIdP/slo",
                    "http://localhost:8081/someIdP/slo",
                    SAMLCertificateUtil.encodeCertificate(SAMLTestHelper.getDefaultCertificate()), 200);

                return null;
            }
        }, testUser1, testTenant);

        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                Response resp = sendRequest(new TestWebScriptServer.DeleteRequest(SAML_CONFIG_URL), 200);
                assertEquals(200, resp.getStatus());

                SAMLConfigSettings settings = getSamlConfigSettings(testTenant);
                assertFalse(settings.isSsoEnabled());
                assertNull(settings.getIdpSsoURL());
                assertNull(settings.getIdpSloRequestURL());
                assertNull(settings.getIdpSloResponseURL());
                assertNull(settings.getCertificateInfo());

                return null;
            }
        }, testUser1, testTenant);
    }

    public void testPostSamlConfigViaMultipart() throws Exception
    {
        createAccount(testTenant, AccountType.ENTERPRISE_NETWORK_ACCOUNT_TYPE, true);
        createUserAsNetworkAdmin(testUser1, "John", "Doe", "password");

        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                // request parameters
                Map<String, String> params = new HashMap<String, String>();
                params.put("ssoEnabled", "true");
                params.put("idpSsoURL", "http://localhost:8081/someIdP/sso");
                params.put("idpSloRequestURL", "http://localhost:8081/someIdP/slo");
                params.put("idpSloResponseURL", "http://localhost:8081/someIdP/slo");

                String boundary = Long.toHexString(System.currentTimeMillis());
                String multipartContentType = MULTIPART_FORM_DATA_BOUNDARY + boundary;

                PostRequest postReq = new PostRequest(SAML_CONFIG_MULTIPART_URL, buildMultipartPostBody("certificate",
                    SAMLTestHelper.getResourceFile("test-certificate.cer"), boundary, params), multipartContentType);

                Response resp = sendRequest(postReq, 200);
                assertEquals(200, resp.getStatus());

                return null;
            }
        }, testUser1, testTenant);

        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                SAMLConfigSettings settings = getSamlConfigSettings(testTenant);
                assertTrue(settings.isSsoEnabled());
                assertEquals("http://localhost:8081/someIdP/sso", settings.getIdpSsoURL());
                assertEquals("http://localhost:8081/someIdP/slo", settings.getIdpSloRequestURL());
                assertEquals("http://localhost:8081/someIdP/slo", settings.getIdpSloResponseURL());
                assertEquals(new DateTime(SAMLTestHelper.getDefaultCertificate().getNotAfter()).toString(), settings
                            .getCertificateInfo().getExpiryDate().toString());

                return null;
            }
        }, testUser1, testTenant);
    }

    public void testPostSamlConfigViaMultipartWithInvalidCertificate() throws Exception
    {
        createAccount(testTenant, AccountType.ENTERPRISE_NETWORK_ACCOUNT_TYPE, true);
        createUserAsNetworkAdmin(testUser1, "John", "Doe", "password");

        // Try to save an expired certificate
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                // request parameters
                Map<String, String> params = new HashMap<String, String>();
                params.put("ssoEnabled", "true");
                params.put("idpSsoURL", "http://localhost:8081/someIdP/sso");
                params.put("idpSloRequestURL", "http://localhost:8081/someIdP/slo");
                params.put("idpSloResponseURL", "http://localhost:8081/someIdP/slo");

                String boundary = Long.toHexString(System.currentTimeMillis());
                String multipartContentType = MULTIPART_FORM_DATA_BOUNDARY + boundary;

                PostRequest postReq = new PostRequest(SAML_CONFIG_MULTIPART_URL, buildMultipartPostBody("certificate",
                            SAMLTestHelper.getResourceFile("expired-certificate.cer"), boundary, params),
                            multipartContentType);

                Response resp = sendRequest(postReq, 400);
                assertEquals(400, resp.getStatus());

                // Check that nothing have been saved
                SAMLConfigSettings settings = getSamlConfigSettings(testTenant);
                assertFalse(settings.isSsoEnabled());
                assertNull(settings.getIdpSsoURL());
                assertNull(settings.getIdpSloRequestURL());
                assertNull(settings.getIdpSloResponseURL());
                assertNull(settings.getCertificateInfo());

                return null;
            }
        }, testUser1, testTenant);

        // Try to save an invalid PEM format certificate
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                // request parameters
                Map<String, String> params = new HashMap<String, String>();
                params.put("ssoEnabled", "true");
                params.put("idpSsoURL", "http://localhost:8081/someIdP/sso");
                params.put("idpSloRequestURL", "http://localhost:8081/someIdP/slo");
                params.put("idpSloResponseURL", "http://localhost:8081/someIdP/slo");

                String boundary = Long.toHexString(System.currentTimeMillis());
                String multipartContentType = MULTIPART_FORM_DATA_BOUNDARY + boundary;

                PostRequest postReq = new PostRequest(SAML_CONFIG_MULTIPART_URL, buildMultipartPostBody("certificate",
                            SAMLTestHelper.getResourceFile("invalid-pemFormat-certificate.cer"), boundary, params),
                            multipartContentType);

                Response resp = sendRequest(postReq, 400);
                assertEquals(400, resp.getStatus());

                // Check that nothing have been saved
                SAMLConfigSettings settings = getSamlConfigSettings(testTenant);
                assertFalse(settings.isSsoEnabled());

                return null;
            }
        }, testUser1, testTenant);

        // Try to save a malformed certificate
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                // request parameters
                Map<String, String> params = new HashMap<String, String>();
                params.put("ssoEnabled", "true");
                params.put("idpSsoURL", "http://localhost:8081/someIdP/sso");
                params.put("idpSloRequestURL", "http://localhost:8081/someIdP/slo");
                params.put("idpSloResponseURL", "http://localhost:8081/someIdP/slo");

                String boundary = Long.toHexString(System.currentTimeMillis());
                String multipartContentType = MULTIPART_FORM_DATA_BOUNDARY + boundary;

                PostRequest postReq = new PostRequest(SAML_CONFIG_MULTIPART_URL, buildMultipartPostBody("certificate",
                            SAMLTestHelper.getResourceFile("not-real-certificate.cer"), boundary, params),
                            multipartContentType);

                Response resp = sendRequest(postReq, 400);
                assertEquals(400, resp.getStatus());

                // Check that nothing have been saved
                SAMLConfigSettings settings = getSamlConfigSettings(testTenant);
                assertFalse(settings.isSsoEnabled());

                return null;
            }
        }, testUser1, testTenant);
    }

    public void testUpdateIssuerNameConfig() throws Exception
    {
        final String isserName = "http://my.alfresco.com-" + testTenant + System.currentTimeMillis();
        createAccount(testTenant, AccountType.ENTERPRISE_NETWORK_ACCOUNT_TYPE, true);
        createUserAsNetworkAdmin(testUser1, "John", "Doe", "password");

        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                postSamlConfig(true, "http://localhost:8081/someIdP/sso", "http://localhost:8081/someIdP/slo",
                    "http://localhost:8081/someIdP/slo",
                    SAMLCertificateUtil.encodeCertificate(SAMLTestHelper.getDefaultCertificate()), 200);

                return null;
            }
        }, testUser1, testTenant);

        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                Response resp = getSamlConfig(200);
                String contentAsString = resp.getContentAsString();

                JSONObject jsonRsp = (JSONObject)JSONValue.parse(contentAsString);
                assertNotNull("Problem reading JSON", jsonRsp);

                boolean ssoEnabled = (Boolean)jsonRsp.get("ssoEnabled");
                String entityID = (String)jsonRsp.get("entityID");
                
                assertTrue(ssoEnabled);
                assertEquals(getIssuerName(testTenant), entityID);

                return null;
            }
        }, testUser1, testTenant);

        // Now update the entity id
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @SuppressWarnings("unchecked")
            @Override
            public Void doWork() throws Exception
            {

                JSONObject obj = new JSONObject();
                obj.put("ssoEnabled", true);
                obj.put("entityID", isserName);

                StringWriter stringWriter = new StringWriter();
                obj.writeJSONString(stringWriter);

                sendRequest(new TestWebScriptServer.PutRequest(SAML_CONFIG_URL, stringWriter.toString(), APPLICATION_JSON), 200);
                return null;
            }
        }, testUser1, testTenant);
        
        // Check the rest of configs haven't been changed
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                Response resp = getSamlConfig(200);
                String contentAsString = resp.getContentAsString();

                JSONObject jsonRsp = (JSONObject)JSONValue.parse(contentAsString);
                assertNotNull("Problem reading JSON", jsonRsp);

                boolean ssoEnabled = (Boolean)jsonRsp.get("ssoEnabled");
                String idpSsoURL = (String)jsonRsp.get("idpSsoURL");
                String idpSloRequestURL = (String)jsonRsp.get("idpSloRequestURL");
                String idpSloResponseURL = (String)jsonRsp.get("idpSloResponseURL");
                String entityID = (String)jsonRsp.get("entityID");
                JSONObject certObj = (JSONObject)jsonRsp.get("certificate");
                String status = (String)certObj.get("status");
                String iso8601ExpiryDate = (String)((JSONObject)certObj.get("expiryDate")).get("iso8601");

                assertTrue(ssoEnabled);
                assertEquals("http://localhost:8081/someIdP/sso", idpSsoURL);
                assertEquals("http://localhost:8081/someIdP/slo", idpSloRequestURL);
                assertEquals("http://localhost:8081/someIdP/slo", idpSloResponseURL);
                String modifiedIssuerName = getIssuerName(testTenant);
                assertEquals(modifiedIssuerName, entityID);
                assertEquals(isserName, modifiedIssuerName);
                
                assertEquals("valid", status);
                assertEquals(new DateTime(SAMLTestHelper.getDefaultCertificate().getNotAfter()).toString(),
                    iso8601ExpiryDate);

                return null;
            }
        }, testUser1, testTenant);
    
    }

    public Response postSamlConfig(boolean ssoEnabled, String idpSsoURL, String idpSloRequestURL,
        String idpSloResponseURL, String certificate, int expectedStatus) throws UnsupportedEncodingException,
        IOException
    {

        JSONObject obj = new JSONObject();
        put(obj, "ssoEnabled", ssoEnabled);
        put(obj, "idpSsoURL", idpSsoURL);
        put(obj, "idpSloRequestURL", idpSloRequestURL);
        put(obj, "idpSloResponseURL", idpSloResponseURL);
        put(obj, "certificate", certificate);

        StringWriter stringWriter = new StringWriter();
        obj.writeJSONString(stringWriter);
        String jsonString = stringWriter.toString();

        Response resp = sendRequest(new TestWebScriptServer.PostRequest(SAML_CONFIG_URL,
                    jsonString, APPLICATION_JSON), expectedStatus);
        return resp;
    }

    @SuppressWarnings("unchecked")
    private void put(JSONObject obj, String key, Object value)
    {
        if(value != null)
        {
            obj.put(key, value);
        }
    }

    private Response getSamlConfig(int expectedStatus) throws Exception
    {
        Response resp = sendRequest(new TestWebScriptServer.GetRequest(SAML_CONFIG_URL), expectedStatus);

        return resp;
    }
    
    private SAMLConfigSettings getSamlConfigSettings(final String tenant)
    {
        return transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<SAMLConfigSettings>()
        {
            @Override
            public SAMLConfigSettings execute() throws Throwable
            {
                return samlConfigAdminService.getSamlConfigSettings(tenant);
            }
        });
    }

    private String getIssuerName(final String tenant)
    {
        return transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<String>()
        {
            @Override
            public String execute() throws Throwable
            {
                return authenticationService.getSpIssuerName(tenant);
            }
        });
    }
}
