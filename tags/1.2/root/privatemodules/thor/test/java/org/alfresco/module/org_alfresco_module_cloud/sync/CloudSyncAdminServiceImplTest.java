/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.module.org_alfresco_module_cloud.sync;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.alfresco.enterprise.repo.sync.SyncAdminService;
import org.alfresco.enterprise.repo.sync.SyncAdminServiceException;
import org.alfresco.enterprise.repo.sync.SyncAdminServiceImpl;
import org.alfresco.enterprise.repo.sync.SyncSetDefinition;
import org.alfresco.enterprise.repo.sync.connector.CloudConnectorService;
import org.alfresco.enterprise.repo.sync.transport.CloudSyncSetDefinitionTransport;
import org.alfresco.enterprise.repo.sync.transport.impl.CloudSyncSetDefinitionTransportImpl;
import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_cloud.CloudModel;
import org.alfresco.module.org_alfresco_module_cloud.CloudTestContext;
import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountAdminService;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountType;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationService;
import org.alfresco.module.org_alfresco_module_cloud.users.CloudPersonService;
import org.alfresco.repo.mode.ServerMode;
import org.alfresco.repo.mode.ServerModeProvider;
import org.alfresco.repo.remoteconnector.RemoteConnectorRequestImpl;
import org.alfresco.repo.remotecredentials.PasswordCredentialsInfoImpl;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.remoteconnector.RemoteConnectorRequest;
import org.alfresco.service.cmr.remoteconnector.RemoteConnectorResponse;
import org.alfresco.service.cmr.remotecredentials.PasswordCredentialsInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.util.ApplicationContextHelper;
import org.alfresco.util.GUID;
import org.alfresco.util.test.junitrules.ApplicationContextInit;
import org.alfresco.util.test.junitrules.TemporaryNodes;
import org.apache.commons.httpclient.Header;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

/**
 * Integration tests for {@link CloudSyncAdminServiceImpl}.
 * 
 */
public class CloudSyncAdminServiceImplTest
{
    // Services
    protected static ApplicationContext TEST_CONTEXT;
    public static ApplicationContextInit APP_CONTEXT_INIT = new ApplicationContextInit();
    
    protected static AccountAdminService       ACCOUNT_ADMIN_SERVICE;
    protected static AccountService            ACCOUNT_SERVICE;
    protected static AuthorityService          AUTHORITY_SERVICE;
    protected static CloudPersonService        CLOUD_PERSON_SERVICE;
    protected static PermissionService         PERMISSION_SERVICE;
    protected static NodeService               NODE_SERVICE;
    protected static RegistrationService       REGISTRATION_SERVICE;
    protected static RetryingTransactionHelper TRANSACTION_HELPER;
    protected static SiteService               SITE_SERVICE;
    protected static SyncAdminService          SYNC_ADMIN_SERVICE;
    protected static CloudSyncSetDefinitionTransport SSD_TRANSPORT;
    
    private static CloudTestContext cloudContext;
    
    private static String acmeTenant;
    private static String acmeAdminUser;
    private static String acmeUser1;
    private static String acmeUser2;
    private static String pingTenant;
    private static String pingAdminUser;
    private static String acmeUser1Site;
    private NodeRef acmeUser1ContentNodeRef;
    
    // A rule to manage test nodes use in each test method
    @Rule public TemporaryNodes temporaryNodes = new TemporaryNodes(APP_CONTEXT_INIT);
    
    /** Dummy remote credentials objects for this test only. */
    protected static final PasswordCredentialsInfo REMOTE_CREDENTIALS = new PasswordCredentialsInfoImpl();
    
    protected static CloudConnectorService MOCK_CLOUD_CONNECTOR_SERVICE;
    
    public static class DummyRemoteConnectorResponse implements RemoteConnectorResponse
    {
        @Override public int                    getStatus()                                  { return 200; }
        @Override public Header[]               getResponseHeaders()                         { return null; }
        @Override public String                 getResponseBodyAsString() throws IOException { return null; }
        @Override public InputStream            getResponseBodyAsStream() throws IOException { return null; }
        @Override public byte[]                 getResponseBodyAsBytes() throws IOException  { return null; }
        @Override public RemoteConnectorRequest getRequest()                                 { return null; }
        @Override public String                 getRawContentType()                          { return null; }
        @Override public String                 getContentType()                             { return null; }
        @Override public String                 getCharset()                                 { return null; }
    };
    
    static {
        ((PasswordCredentialsInfoImpl)REMOTE_CREDENTIALS).setRemoteUsername("remote.user");
        ((PasswordCredentialsInfoImpl)REMOTE_CREDENTIALS).setRemotePassword("remote.password");
        
        // and a mocked CloudConnectorService...
        MOCK_CLOUD_CONNECTOR_SERVICE = mock(CloudConnectorService.class);
        // ...that always uses dummy credentials
        when(MOCK_CLOUD_CONNECTOR_SERVICE.getCloudCredentials())
            .thenReturn(REMOTE_CREDENTIALS);
        // ...and always returns a simple RemoteConnectorRequest object
        when(MOCK_CLOUD_CONNECTOR_SERVICE.buildCloudRequest(any(String.class), any(String.class), any(String.class)))
            .thenReturn(new RemoteConnectorRequestImpl("srcRepoId", "POST"));
        // ... and always returns a dummy response object
        try
        {
            when(MOCK_CLOUD_CONNECTOR_SERVICE.executeCloudRequest(any(RemoteConnectorRequest.class)))
                .thenReturn(new DummyRemoteConnectorResponse());
        } catch (IOException ignored)
        {
            // Intentionally empty.
        }
    }
    
    /**
     * Initialise various services required by the test.
     */
    @BeforeClass public static void initTestsContext() throws Exception
    {
        TEST_CONTEXT = ApplicationContextHelper.getApplicationContext();
        
        ACCOUNT_SERVICE       = (AccountService) TEST_CONTEXT.getBean("accountService");
        AUTHORITY_SERVICE     = (AuthorityService) TEST_CONTEXT.getBean("authorityService");
        CLOUD_PERSON_SERVICE  = (CloudPersonService) TEST_CONTEXT.getBean("cloudPersonService");
        NODE_SERVICE          = (NodeService) TEST_CONTEXT.getBean("nodeService");
        REGISTRATION_SERVICE  = (RegistrationService)TEST_CONTEXT.getBean("registrationService");
        TRANSACTION_HELPER    = (RetryingTransactionHelper)TEST_CONTEXT.getBean("retryingTransactionHelper");
        SITE_SERVICE          = (SiteService) TEST_CONTEXT.getBean("siteService");
        SYNC_ADMIN_SERVICE    = (SyncAdminService) TEST_CONTEXT.getBean("syncAdminService");
        SSD_TRANSPORT         = (CloudSyncSetDefinitionTransport) TEST_CONTEXT.getBean("cloudSyncSetDefinitionTransport");
        PERMISSION_SERVICE    = (PermissionService) TEST_CONTEXT.getBean("permissionService");
        
        // Wire in our mock/testing cloud connector
        ((SyncAdminServiceImpl)SYNC_ADMIN_SERVICE).setCloudConnectorService(MOCK_CLOUD_CONNECTOR_SERVICE);
        ServerModeProvider fakeServerModeProvider = new ServerModeProvider()
        {
            @Override
            public ServerMode getServerMode() {
                return ServerMode.PRODUCTION;
            }
        };
        if(SYNC_ADMIN_SERVICE instanceof SyncAdminServiceImpl)
        {
            SyncAdminServiceImpl syncAdminServiceImpl = (SyncAdminServiceImpl)SYNC_ADMIN_SERVICE;
            syncAdminServiceImpl.setServerModeProvider(fakeServerModeProvider);
        }
        ((CloudSyncSetDefinitionTransportImpl)SSD_TRANSPORT).setCloudConnectorService(MOCK_CLOUD_CONNECTOR_SERVICE);
    }
    
    @Before public void init() throws Exception
    {
        cloudContext = new CloudTestContext(TEST_CONTEXT);
        
        // Create two tenants
        acmeTenant = cloudContext.createTenantName("acme");
        createAccount(acmeTenant, AccountType.ENTERPRISE_NETWORK_ACCOUNT_TYPE, true);

        pingTenant = cloudContext.createTenantName("ping");
        createAccount(pingTenant, AccountType.ENTERPRISE_NETWORK_ACCOUNT_TYPE, true);

        // Create users for the created tenants
        acmeAdminUser = cloudContext.createUserName("acmeAdminUser", acmeTenant);
        createUser(acmeAdminUser, "Admin", "Someone", "password", true);

        acmeUser1 = cloudContext.createUserName("acmeUser1", acmeTenant);
        createUser(acmeUser1, "John", "Doe", "password", false);

        acmeUser2 = cloudContext.createUserName("acmeUser2", acmeTenant);
        createUser(acmeUser2, "Jane", "Doe", "password", false);

        pingAdminUser = cloudContext.createUserName("pingUser", pingTenant);
        createUser(pingAdminUser, "Sam", "Noone", "password", true);
        
        acmeUser1Site = "testSite-" + GUID.generate();
        acmeUser1ContentNodeRef = createSiteAndAddContent(acmeUser1Site, acmeUser1, acmeTenant);
    }
    
    /** Delete any temporary accounts created during the previous test */
    @After public void cleanup()
    {
        cloudContext.cleanup();
    }
    
    @Test
    public void deleteSyncSetDefinitionAllowedOnlyForCreatorsAndAdmins()
    {
        // Create the SSD
        final List<NodeRef> syncMembers = Arrays.asList(new NodeRef[]{ acmeUser1ContentNodeRef });
        
        final String remoteTenantId = "remoteTenant";
        final String targetFolderNodeRef = "cloud://node/Ref";
        final boolean lockSourceCopy = false;
        final boolean isDeleteOnCloud = true;
        final boolean isDeleteOnPrem = false;
        
        // create ssd as user1
        AuthenticationUtil.setFullyAuthenticatedUser(acmeUser1);
        final SyncSetDefinition newSSD = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<SyncSetDefinition>()
        {
            public SyncSetDefinition execute() throws Throwable
            {
                SyncSetDefinition ssd = SYNC_ADMIN_SERVICE.createSourceSyncSet(syncMembers, remoteTenantId, targetFolderNodeRef, lockSourceCopy, isDeleteOnCloud, isDeleteOnPrem);
                temporaryNodes.addNodeRef(ssd.getNodeRef());
                assertNotNull(ssd);
                assertNotNull(ssd.getId());
                return ssd;
            }
        });
        
        // add write permissions on node
        PERMISSION_SERVICE.setPermission(acmeUser1ContentNodeRef, acmeAdminUser, PermissionService.ALL_PERMISSIONS, true);
        PERMISSION_SERVICE.setPermission(acmeUser1ContentNodeRef, acmeUser2, PermissionService.ALL_PERMISSIONS, true);
        PERMISSION_SERVICE.setPermission(acmeUser1ContentNodeRef, pingAdminUser, PermissionService.ALL_PERMISSIONS, true);
        
        // Try to delete the SSD as User2
        AuthenticationUtil.setFullyAuthenticatedUser(acmeUser2);
        try
        {
            TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
            {
                public Void execute() throws Throwable
                {
                    SYNC_ADMIN_SERVICE.deleteTargetSyncSet(newSSD.getId());
                    return null;
                }
            });
            fail("User2 should not have permissions to delete ssd that was created by User1");
        }
        catch (SyncAdminServiceException e)
        {
            assertTrue(e.getMessage().contains("Not enough permissions to process SSD"));
        }

        // Admin should be able to delete ssd that was created by User1
        AuthenticationUtil.setAdminUserAsFullyAuthenticatedUser();
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                SYNC_ADMIN_SERVICE.deleteTargetSyncSet(newSSD.getId());
                return null;
            }
        });

        // create one more ssd as User1
        AuthenticationUtil.setFullyAuthenticatedUser(acmeUser1);
        final SyncSetDefinition newSSD2 = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<SyncSetDefinition>()
        {
            public SyncSetDefinition execute() throws Throwable
            {
                SyncSetDefinition ssd = SYNC_ADMIN_SERVICE.createSourceSyncSet(syncMembers, remoteTenantId, targetFolderNodeRef, lockSourceCopy, isDeleteOnCloud, isDeleteOnPrem);
                temporaryNodes.addNodeRef(ssd.getNodeRef());
                assertNotNull(ssd);
                assertNotNull(ssd.getId());
                return ssd;
            }
        });

        // delete ssd as User1
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                assertFalse(isNetworkAdmin(acmeUser1));
                SYNC_ADMIN_SERVICE.deleteTargetSyncSet(newSSD2.getId());
                return null;
            }
        });
        
        // create one more ssd as User1
        AuthenticationUtil.setFullyAuthenticatedUser(acmeUser1);
        final SyncSetDefinition newSSD3 = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<SyncSetDefinition>()
        {
            public SyncSetDefinition execute() throws Throwable
            {
                SyncSetDefinition ssd = SYNC_ADMIN_SERVICE.createSourceSyncSet(syncMembers, remoteTenantId, targetFolderNodeRef, lockSourceCopy, isDeleteOnCloud, isDeleteOnPrem);
                temporaryNodes.addNodeRef(ssd.getNodeRef());
                assertNotNull(ssd);
                assertNotNull(ssd.getId());
                return ssd;
            }
        });
        
        // Clear any security information
        AuthenticationUtil.clearCurrentSecurityContext();
        // try to delete ssd as ping network admin
        AuthenticationUtil.setFullyAuthenticatedUser(pingAdminUser);
        try
        {
            TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
            {
                public Void execute() throws Throwable
                {
                    assertTrue(isNetworkAdmin(pingAdminUser));
                    SYNC_ADMIN_SERVICE.deleteTargetSyncSet(newSSD3.getId());
                    return null;
                }
            });
            fail("Network Admin from another network should not have permissions to delete ssd that was created by User1");
        }
        catch (SyncAdminServiceException e)
        {
            assertTrue(e.getMessage().contains("No such SSD"));
        }
        
        // Clear any security information
        AuthenticationUtil.clearCurrentSecurityContext();
        // delete ssd as acme network admin
        AuthenticationUtil.setFullyAuthenticatedUser(acmeAdminUser);
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                assertTrue(isNetworkAdmin(acmeAdminUser));
                SYNC_ADMIN_SERVICE.deleteTargetSyncSet(newSSD3.getId());
                return null;
            }
        });
        

    }
    
    private boolean isNetworkAdmin(String email)
    {
        final boolean userIsAdmin = AUTHORITY_SERVICE.getContainedAuthorities(AuthorityType.USER, "GROUP_NETWORK_ADMINS", true).contains(email);
        if (userIsAdmin)
        {
            return true;
        }
        
        final NodeRef personNode = CLOUD_PERSON_SERVICE.getPerson(email, false);
        
        return NODE_SERVICE.hasAspect(personNode, CloudModel.ASPECT_NETWORK_ADMIN);
   }


    private static Account createAccount(final String domain, final int type, final boolean enabled) throws Exception
    {
        return TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Account>()
        {
            @Override
            public Account execute() throws Throwable
            {
                Account account = ACCOUNT_SERVICE.createAccount(domain, type, enabled);
                cloudContext.addAccount(account);
                return account;
            }
        });
    }
    
    public static Account createUser(final String email, final String firstName, final String lastName, final String passwd, final boolean isAdmin)
    {
        return TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Account>()
        {
            @Override
            public Account execute() throws Throwable
            {
                Account account = REGISTRATION_SERVICE.createUser(email, firstName, lastName, passwd);
                cloudContext.addUser(email);
                assertNotNull("Account was null.", account);

                if (isAdmin)
                {
                    REGISTRATION_SERVICE.promoteUserToNetworkAdmin(account.getId(), email);
                }
                return account;
            }
        });
    }
    
    public NodeRef createSiteAndAddContent(final String siteShortName, String user, String tenant)
    {
        // Create additional sites
        TenantRunAsWork<NodeRef> createSiteWork = new TenantRunAsWork<NodeRef>()
        {
            @Override
            public NodeRef doWork() throws Exception
            {
                return TRANSACTION_HELPER.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>()
                {
                    @Override
                    public NodeRef execute() throws Throwable
                    {
                        SITE_SERVICE.createSite("site-preset", siteShortName, "site title", "site description", SiteVisibility.PUBLIC);
                        NodeRef docLibNodeRef = SITE_SERVICE.getContainer(siteShortName, SiteService.DOCUMENT_LIBRARY);

                        // Create a test node. It doesn't need a content.
                        NodeRef nodeRef = NODE_SERVICE.createNode(docLibNodeRef, ContentModel.ASSOC_CONTAINS, ContentModel.ASSOC_CONTAINS,
                                    ContentModel.TYPE_CONTENT).getChildRef();
                        return nodeRef;
                    }
                });
            }
        };
        return TenantUtil.runAsUserTenant(createSiteWork, user, tenant);
    }
    
}
