/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.security.cert.X509Certificate;

import org.alfresco.module.org_alfresco_module_cloud.CloudTestContext;
import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountType;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLTestHelper;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.util.ApplicationContextHelper;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

/**
 * Test code for {@link SAMLConfigAdminServiceImpl}.
 * 
 * @author jkaabimofrad
 * @since Cloud SAML
 */
public class SAMLConfigAdminServiceImplTest
{
    private static ApplicationContext context;
    protected static RetryingTransactionHelper transactionHelper;
    private static SAMLConfigAdminService samlConfigAdminService;
    private static AccountService accountService;
    private CloudTestContext cloudContext;
    private String testTenant;

    @BeforeClass
    public static void initStaticData() throws Exception
    {
        context = ApplicationContextHelper.getApplicationContext();
        transactionHelper = (RetryingTransactionHelper)context.getBean("retryingTransactionHelper");
        samlConfigAdminService = (SAMLConfigAdminService)context.getBean("samlConfigAdminService");
        accountService = (AccountService)context.getBean("accountService");
    }

    @Before
    public void initSecurityPolicyRuleTest() throws Exception
    {
        cloudContext = new CloudTestContext(context);
        testTenant = cloudContext.createTenantName("acme");

        // Set the current security context as admin
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
    }

    @After
    public void cleanup()
    {
        TenantUtil.runAsTenant(new TenantRunAsWork<Object>()
        {
            public Object doWork()
            {
                transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @Override
                    public Void execute() throws Throwable
                    {
                        samlConfigAdminService.deleteSamlConfigs();

                        SAMLConfigSettings settings = samlConfigAdminService.getSamlConfigSettings(testTenant);
                        assertFalse(settings.isSsoEnabled());
                        assertNull(settings.getIdpSsoURL());
                        assertNull(settings.getIdpSloRequestURL());
                        assertNull(settings.getIdpSloResponseURL());
                        assertNull(settings.getCertificateInfo());
                        assertEquals(0, settings.getEncodedCertificate().length);

                        return null;
                    }
                });

                return null;
            }
        }, testTenant);

        cloudContext.cleanup();
    }

    @Test
    public void testSamlEnabled() throws Exception
    {

        createAccount(testTenant, AccountType.ENTERPRISE_NETWORK_ACCOUNT_TYPE, true);

        TenantUtil.runAsTenant(new TenantRunAsWork<Object>()
        {
            public Object doWork()
            {
                assertFalse(samlConfigAdminService.isEnabled());
                samlConfigAdminService.setEnabled(true);
                assertTrue(samlConfigAdminService.isEnabled());

                return null;
            }
        }, testTenant);
        assertTrue(samlConfigAdminService.isEnabled(testTenant));

        TenantUtil.runAsTenant(new TenantRunAsWork<Object>()
        {
            public Object doWork()
            {

                samlConfigAdminService.setEnabled(false);
                assertFalse(samlConfigAdminService.isEnabled());

                return null;
            }
        }, testTenant);
        assertFalse(samlConfigAdminService.isEnabled(testTenant));

    }

    @Test
    public void testSamlConfigs() throws Exception
    {
        createAccount(testTenant, AccountType.ENTERPRISE_NETWORK_ACCOUNT_TYPE, true);

        TenantUtil.runAsTenant(new TenantRunAsWork<Object>()
        {
            SAMLConfigSettings config = new SAMLConfigSettings.Builder(true).idpSsoURL("http://someIdP/sso")
                .idpSloRequestURL("http://someIdP/slorequest").idpSloResponseURL("http://someIdP/sloresponse")
                .encodedCertificate(SAMLTestHelper.getDefaultCertificate().getEncoded()).build();

            public Object doWork() throws Exception
            {
                transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @Override
                    public Void execute() throws Throwable
                    {
                        SAMLConfigSettings settings = samlConfigAdminService.getSamlConfigSettings(testTenant);
                        assertFalse(settings.isSsoEnabled());
                        assertNull(settings.getIdpSsoURL());
                        assertNull(settings.getIdpSloRequestURL());
                        assertNull(settings.getIdpSloResponseURL());
                        assertNull(settings.getCertificateInfo());
                        assertEquals(0, settings.getEncodedCertificate().length);

                        samlConfigAdminService.setSamlConfigs(config);

                        settings = samlConfigAdminService.getSamlConfigSettings(testTenant);

                        assertTrue(settings.isSsoEnabled());
                        assertEquals("http://someIdP/sso", settings.getIdpSsoURL());
                        assertEquals("http://someIdP/slorequest", settings.getIdpSloRequestURL());
                        assertEquals("http://someIdP/sloresponse", settings.getIdpSloResponseURL());
                        assertEquals(new DateTime(SAMLTestHelper.getDefaultCertificate().getNotAfter()).toString(),
                            settings.getCertificateInfo().getExpiryDate().toString());

                        // Update SSO URL
                        config = new SAMLConfigSettings.Builder(true).idpSsoURL("http://someIdP/ssoupdated").build();
                        samlConfigAdminService.setSamlConfigs(config);

                        settings = samlConfigAdminService.getSamlConfigSettings(testTenant);

                        assertTrue(settings.isSsoEnabled());
                        assertEquals("http://someIdP/ssoupdated", settings.getIdpSsoURL());
                        assertEquals("http://someIdP/slorequest", settings.getIdpSloRequestURL());
                        assertEquals("http://someIdP/sloresponse", settings.getIdpSloResponseURL());
                        assertEquals(new DateTime(SAMLTestHelper.getDefaultCertificate().getNotAfter()).toString(),
                            settings.getCertificateInfo().getExpiryDate().toString());

                        // Update Certificate
                        config = new SAMLConfigSettings.Builder(true).idpSsoURL("http://someIdP/sso")
                            .idpSloResponseURL(null)
                            .encodedCertificate(SAMLTestHelper.getUnknownCertificate().getEncoded()).build();
                        samlConfigAdminService.setSamlConfigs(config);

                        settings = samlConfigAdminService.getSamlConfigSettings(testTenant);

                        assertTrue(settings.isSsoEnabled());
                        assertEquals("http://someIdP/sso", settings.getIdpSsoURL());
                        assertEquals("http://someIdP/slorequest", settings.getIdpSloRequestURL());
                        assertEquals("http://someIdP/sloresponse", settings.getIdpSloResponseURL());
                        assertEquals(new DateTime(SAMLTestHelper.getUnknownCertificate().getNotAfter()).toString(),
                            settings.getCertificateInfo().getExpiryDate().toString());

                        return null;
                    }
                });

                return null;
            }
        }, testTenant);
    }

    @Test
    public void testSamlCertificate() throws Exception
    {
        createAccount(testTenant, AccountType.ENTERPRISE_NETWORK_ACCOUNT_TYPE, true);

        TenantUtil.runAsTenant(new TenantRunAsWork<Object>()
        {
            public Object doWork() throws Exception
            {
                transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @Override
                    public Void execute() throws Throwable
                    {
                        X509Certificate cert = null;
                        try
                        {
                            cert = samlConfigAdminService.getCertificate();
                            fail("The certificate doesn't exist yet.");
                        }
                        catch(Exception e)
                        {
                            // expected
                        }

                        byte[] defaultCert = SAMLTestHelper.getDefaultCertificate().getEncoded();
                        samlConfigAdminService.setCertificate(defaultCert);

                        cert = samlConfigAdminService.getCertificate();
                        assertArrayEquals(defaultCert, cert.getEncoded());

                        try
                        {
                            samlConfigAdminService.setCertificate("Malformed certificate".getBytes());
                            fail("The certificate is malformed.");
                        }
                        catch(Exception ex)
                        {
                            // expected
                        }
                        cert = samlConfigAdminService.getCertificate();
                        assertArrayEquals(defaultCert, cert.getEncoded());

                        try
                        {
                            samlConfigAdminService.setCertificate(SAMLTestHelper.getExpiredCertificate().getEncoded());
                            fail("The certificate is expired.");
                        }
                        catch(Exception ex)
                        {
                            // expected
                        }
                        cert = samlConfigAdminService.getCertificate(testTenant);
                        assertArrayEquals(defaultCert, cert.getEncoded());

                        return null;
                    }
                });

                return null;
            }
        }, testTenant);
    }

    private Account createAccount(final String domain, final int type, final boolean enabled) throws Exception
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
