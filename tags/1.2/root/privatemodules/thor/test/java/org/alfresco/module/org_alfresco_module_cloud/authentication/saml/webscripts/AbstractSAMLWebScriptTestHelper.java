/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.webscripts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Map.Entry;
import org.alfresco.module.org_alfresco_module_cloud.CloudTestContext;
import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLAuthenticationService;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLConfigAdminService;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLConfigSettings;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLBinding;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationService;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.web.scripts.BaseWebScriptTest;
import org.springframework.util.FileCopyUtils;

/**
 * Base unit test class for SAML web scripts
 * 
 * @author jkaabimofrad
 * @since Cloud SAML
 */
public abstract class AbstractSAMLWebScriptTestHelper extends BaseWebScriptTest
{
    protected static final String APPLICATION_JSON = "application/json";
    protected static final String MULTIPART_FORM_DATA_BOUNDARY = "multipart/form-data; boundary=";
    private static final String CHARSET = "UTF-8";
    private static final Object CRLF = "\r\n";

    protected RetryingTransactionHelper transactionHelper;
    protected AccountService accountService;
    protected RegistrationService registrationService;
    protected CloudTestContext cloudContext;
    protected SAMLConfigAdminService samlConfigAdminService;
    protected SAMLAuthenticationService authenticationService;
    protected SAMLBinding samlBinding;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        cloudContext = new CloudTestContext(this);

        transactionHelper = (RetryingTransactionHelper)cloudContext.getApplicationContext().getBean(
            "retryingTransactionHelper");
        accountService = (AccountService)cloudContext.getApplicationContext().getBean("accountService");
        registrationService = (RegistrationService)cloudContext.getApplicationContext().getBean("RegistrationService");
        samlConfigAdminService = (SAMLConfigAdminService)cloudContext.getApplicationContext().getBean(
            "samlConfigAdminService");
        authenticationService = (SAMLAuthenticationService)cloudContext.getApplicationContext().getBean(
            "samlAuthenticationService");
        samlBinding = (SAMLBinding)cloudContext.getApplicationContext().getBean("samlBinding");
    }

    public void setSamlConfig(final String network, final boolean ssoEnabled, final String idpSsoURL,
        final String idpSloRequestURL, final String idpSloResponseURL, final X509Certificate certificate)
        throws Exception
    {
        TenantUtil.runAsTenant(new TenantRunAsWork<Object>()
        {
            public Object doWork() throws Exception
            {
                transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
                {
                    @Override
                    public Void execute() throws Throwable
                    {
                        SAMLConfigSettings config = new SAMLConfigSettings.Builder(ssoEnabled).idpSsoURL(idpSsoURL)
                            .idpSloRequestURL(idpSloRequestURL).idpSloResponseURL(idpSloResponseURL)
                            .encodedCertificate((certificate == null) ? null : certificate.getEncoded()).build();

                        samlConfigAdminService.setSamlConfigs(config);

                        config = samlConfigAdminService.getSamlConfigSettings(network);
                        assertNotNull("SAML settings was null", config);

                        return null;
                    }
                });
                return null;
            }
        }, network);
    }

    public Account createAccount(final String domain, final int type, final boolean enabled) throws Exception
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

    public Account createUserAsNetworkAdmin(final String email, final String firstName, final String lastName,
        final String password)
    {
        return transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Account>()
        {
            @Override
            public Account execute() throws Throwable
            {
                Account account = createUser(email, firstName, lastName, password);
                assertNotNull("Account was null.", account);
                registrationService.promoteUserToNetworkAdmin(account.getId(), email);

                return account;
            }
        });
    }

    public Account createUser(final String email, final String firstName, final String lastName, final String password)
    {
        return transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Account>()
        {
            @Override
            public Account execute() throws Throwable
            {
                Account account = registrationService.createUser(email, firstName, lastName, password);
                cloudContext.addUser(email);
                assertNotNull("Account was null.", account);

                return account;
            }
        });
    }

    public byte[] buildMultipartPostBody(String FilefieldName, File file, String boundary, Map<String, String> params)
        throws IOException
    {
        StringBuilder sb = new StringBuilder();

        addPostParams(params, sb, boundary);

        // Send binary file.
        sb.append("--" + boundary).append(CRLF);
        sb.append("Content-Disposition: form-data; name=\"" + FilefieldName + "\"; filename=\"" + file.getName() + "\"")
            .append(CRLF);
        sb.append("Content-Type: application/octet-stream").append(CRLF);
        sb.append("Content-Transfer-Encoding: binary").append(CRLF);
        sb.append(CRLF);

        byte[] stream = FileCopyUtils.copyToByteArray(new FileInputStream(file));

        sb.append(new String(stream, CHARSET));
        sb.append(CRLF); // CRLF is important! It indicates end of binary boundary.

        // End of multipart/form-data.
        sb.append("--" + boundary + "--").append(CRLF);

        return sb.toString().getBytes(CHARSET);
    }

    private void addPostParams(Map<String, String> params, StringBuilder sb, String boundary)
    {
        if(params == null)
            return;

        for(Entry<String, String> param : params.entrySet())
        {
            sb.append("--" + boundary).append(CRLF);
            sb.append("Content-Disposition: form-data; name=\"" + param.getKey() + "\"").append(CRLF);
            sb.append("Content-Type: text/plain; charset=" + CHARSET).append(CRLF);
            sb.append("Content-Transfer-Encoding: 8bit").append(CRLF);
            sb.append(CRLF);
            sb.append(param.getValue()).append(CRLF);
        }
    }

    public void cleanup(final String tenantDomain)
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

                        SAMLConfigSettings settings = samlConfigAdminService.getSamlConfigSettings(tenantDomain);
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

        }, tenantDomain);
    }
}
