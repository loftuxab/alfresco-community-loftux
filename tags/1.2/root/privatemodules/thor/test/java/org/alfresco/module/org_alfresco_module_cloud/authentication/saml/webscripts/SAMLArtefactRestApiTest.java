/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.webscripts;

import static org.junit.Assert.assertArrayEquals;
import java.io.ByteArrayInputStream;
import java.security.cert.X509Certificate;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountType;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLCertificateUtil;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLTestHelper;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml2.metadata.AttributeConsumingService;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml2.metadata.RequestedAttribute;
import org.opensaml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml2.metadata.SingleLogoutService;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.signature.X509Data;
import org.springframework.extensions.webscripts.TestWebScriptServer;
import org.springframework.extensions.webscripts.TestWebScriptServer.Response;

/**
 * Test code for {@link SAMLSpPublicCertGet}, {@link SAMLIdpPublicCertGet} and {@link SAMLSpMetadataGet}.
 * 
 * @author jkaabimofrad
 * @since Cloud SAML
 */
public class SAMLArtefactRestApiTest extends AbstractSAMLWebScriptTestHelper
{
    private static final String GET_SP_CERTIFICATE_URL = "/saml/sp/pubcert";
    private static final String GET_IDP_CERTIFICATE_URL = "/saml/idp/pubcert";
    private static final String GET_SP_METADATA_URL = "/saml/sp/metadata";

    private String testTenant;
    private String testUser1;

    @Override
    protected void setUp() throws Exception
    {
        try
        {
            super.setUp();

            testTenant = cloudContext.createTenantName("acme");
            testUser1 = cloudContext.createUserName("testuser1", testTenant);

            // Set the current security context as admin
            AuthenticationUtil.setAdminUserAsFullyAuthenticatedUser();
        }
        catch(Exception e)
        {
            // tearDown() is not run automatically if setUp threw an Exception
            try
            {
                tearDown();
            }
            catch(Exception tdes)
            {
                ;
            }
            throw e;
        }
    }

    @Override
    protected void tearDown() throws Exception
    {
        try
        {
            if(testTenant != null)
            {
                cleanup(testTenant);
            }
        }
        finally
        {
            cloudContext.cleanup();
        }
    }

    public void testSpPublicCertGet() throws Exception
    {
        createAccount(testTenant, AccountType.ENTERPRISE_NETWORK_ACCOUNT_TYPE, true);
        createUserAsNetworkAdmin(testUser1, "John", "Doe", "password");

        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                // Get SP certificate
                Response resp = sendRequest(new TestWebScriptServer.GetRequest(GET_SP_CERTIFICATE_URL), 200);
                assertEquals(200, resp.getStatus());

                byte[] cert = resp.getContentAsByteArray();
                assertNotNull(cert);

                String expectedCert = authenticationService.getSpPublicCertificate();
                assertArrayEquals(
                    SAMLCertificateUtil.generateCertificate(SAMLCertificateUtil.decodeCertificate(expectedCert))
                        .getEncoded(), SAMLCertificateUtil.generateCertificate(cert).getEncoded());

                return null;
            }
        }, testUser1, testTenant);
    }

    public void testIdpPublicCertGet() throws Exception
    {
        createAccount(testTenant, AccountType.ENTERPRISE_NETWORK_ACCOUNT_TYPE, true);
        createUserAsNetworkAdmin(testUser1, "John", "Doe", "password");

        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                // Set Config
                setSamlConfig(testTenant, true, "http://localhost:8081/someIdP/sso",
                    "http://localhost:8081/someIdP/slo", "http://localhost:8081/someIdP/slo",
                    SAMLTestHelper.getDefaultCertificate());

                // Get IdP certificate
                Response resp = sendRequest(new TestWebScriptServer.GetRequest(GET_IDP_CERTIFICATE_URL), 200);
                assertEquals(200, resp.getStatus());

                byte[] cert = resp.getContentAsByteArray();
                assertNotNull(cert);

                assertArrayEquals(SAMLTestHelper.getDefaultCertificate().getEncoded(), SAMLCertificateUtil
                    .generateCertificate(cert).getEncoded());

                return null;
            }
        }, testUser1, testTenant);
    }

    public void testSpMetadataGet() throws Exception
    {
        createAccount(testTenant, AccountType.ENTERPRISE_NETWORK_ACCOUNT_TYPE, true);
        createUserAsNetworkAdmin(testUser1, "John", "Doe", "password");

        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                // Get IdP certificate
                Response resp = sendRequest(new TestWebScriptServer.GetRequest(GET_SP_METADATA_URL), 200);
                assertEquals(200, resp.getStatus());

                byte[] metadata = resp.getContentAsByteArray();
                assertNotNull(metadata);

                // Unmarshall SP metadata
                XMLObject xmlObject = SAMLTestHelper.unmarshallElement(new ByteArrayInputStream(metadata));
                assertTrue(xmlObject instanceof EntityDescriptor);
                EntityDescriptor ed = (EntityDescriptor)xmlObject;
                assertEquals(authenticationService.getSpIssuerName(testTenant), ed.getEntityID());

                SPSSODescriptor spDesc = ed.getSPSSODescriptor(SAMLConstants.SAML20P_NS);
                // SLO service
                SingleLogoutService sloService = spDesc.getSingleLogoutServices().get(0);
                assertNotNull(sloService);
                // We only support POST for now
                assertEquals(SAMLConstants.SAML2_POST_BINDING_URI, sloService.getBinding());
                assertEquals(authenticationService.getSpSloRequestURL(testTenant), sloService.getLocation());
                assertEquals(authenticationService.getSpSloResponseURL(testTenant), sloService.getResponseLocation());

                // SSO service
                AssertionConsumerService ssoService = spDesc.getAssertionConsumerServices().get(0);
                assertNotNull(ssoService);
                // We only support POST for now
                assertEquals(SAMLConstants.SAML2_POST_BINDING_URI, ssoService.getBinding());
                assertEquals(authenticationService.getSpSsoURL(testTenant), ssoService.getLocation());

                // AttributeConsuming service
                AttributeConsumingService attConsService = spDesc.getAttributeConsumingServices().get(0);
                assertNotNull(attConsService);
                RequestedAttribute att = attConsService.getRequestAttributes().get(0);
                assertNotNull(att);
                assertEquals("Email", att.getName());

                // Get Certificate
                KeyDescriptor keyDescriptor = spDesc.getKeyDescriptors().get(0);
                assertNotNull(keyDescriptor);
                KeyInfo keyInfo = keyDescriptor.getKeyInfo();
                assertNotNull(keyInfo);
                X509Data x509Data = keyInfo.getX509Datas().get(0);
                assertNotNull(x509Data);

                org.opensaml.xml.signature.X509Certificate openSamlXmlCert = x509Data.getX509Certificates().get(0);
                assertNotNull(openSamlXmlCert);

                X509Certificate certificate = SAMLCertificateUtil.generateCertificate(SAMLCertificateUtil
                    .decodeCertificate(openSamlXmlCert.getValue()));

                String expectedCert = authenticationService.getSpPublicCertificate();
                assertArrayEquals(
                    SAMLCertificateUtil.generateCertificate(SAMLCertificateUtil.decodeCertificate(expectedCert))
                        .getEncoded(), certificate.getEncoded());

                return null;
            }
        }, testUser1, testTenant);
    }

}
