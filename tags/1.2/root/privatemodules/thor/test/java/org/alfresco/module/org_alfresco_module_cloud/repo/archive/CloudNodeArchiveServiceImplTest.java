/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */

package org.alfresco.module.org_alfresco_module_cloud.repo.archive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_cloud.CloudTestContext;
import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountService;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountType;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationService;
import org.alfresco.query.PagingResults;
import org.alfresco.repo.node.archive.ArchivedNodesCannedQueryBuilder;
import org.alfresco.repo.node.archive.NodeArchiveService;
import org.alfresco.repo.node.archive.RestoreNodeReport;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.util.ApplicationContextHelper;
import org.alfresco.util.GUID;
import org.alfresco.util.ScriptPagingDetails;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

/**
 * Test for Cloud implementation of the node archive abstraction
 * 
 * @author Jamal Kaabi-Mofrad
 */
public class CloudNodeArchiveServiceImplTest
{
    private static NodeService nodeService;
    private static ApplicationContext context;
    private static RetryingTransactionHelper transactionHelper;
    private static NodeArchiveService nodeArchiveService;
    private static RegistrationService registrationService;
    private static AccountService accountService;
    private static CloudTestContext cloudContext;
    private static SiteService siteService;

    private static String acmeTenant;
    private static String acmeAdminUser;
    private static String acmeUser1;
    private static String acmeUser2;
    private static String pingTenant;
    private static String pingAdminUser;

    private static String acmeAdminSite;
    private static String acmeUser1Site;
    private static String acmeUser2Site;

    private NodeRef acmeUser1ContentNodeRef;
    private NodeRef acmeUser2ContentNodeRef;
    private NodeRef acmeAdminUserContentNodeRef;   


    @BeforeClass
    public static void initStaticData() throws Exception
    {
        context = ApplicationContextHelper.getApplicationContext();
        transactionHelper = (RetryingTransactionHelper) context.getBean("retryingTransactionHelper");
        nodeArchiveService = (NodeArchiveService) context.getBean("nodeArchiveService");
        accountService = (AccountService) context.getBean("accountService");
        registrationService = (RegistrationService) context.getBean("RegistrationService");
        siteService = (SiteService) context.getBean("siteService");
        nodeService = (NodeService) context.getBean("nodeService");
    }

    @Before
    public void initSecurityPolicyRuleTest() throws Exception
    {
        cloudContext = new CloudTestContext(context);
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

        acmeAdminSite = "testSite-" + GUID.generate();
        acmeUser1Site = "testSite-" + GUID.generate();
        acmeUser2Site = "testSite-" + GUID.generate();

        acmeAdminUserContentNodeRef = creatSiteAndAddContent(acmeAdminSite, acmeAdminUser, acmeTenant);
        acmeUser1ContentNodeRef = creatSiteAndAddContent(acmeUser1Site, acmeUser1, acmeTenant);
        acmeUser2ContentNodeRef = creatSiteAndAddContent(acmeUser2Site, acmeUser2, acmeTenant);

        AuthenticationUtil.clearCurrentSecurityContext();
    }

    @After
    public void cleanup()
    {
        cloudContext.cleanup();
        AuthenticationUtil.clearCurrentSecurityContext();
    }

    @Test
    public void testListArchiveNodes() throws Exception
    {
        AuthenticationUtil.setFullyAuthenticatedUser(acmeUser1);

        ScriptPagingDetails paging = new ScriptPagingDetails(20, 0);
        // Create canned query
        ArchivedNodesCannedQueryBuilder queryBuilder = new ArchivedNodesCannedQueryBuilder.Builder(getArchiveStoreRootNodeRef(), paging)
                    .build();
        // Query the DB
        PagingResults<NodeRef> result = runListArchivedNodesAsAdmin(queryBuilder);
        assertEquals(acmeUser1 + " hasn't deleted anything yet.", 0, result.getPage().size());
        // acmeUser1 deletes his content
        deleteNode(acmeUser1ContentNodeRef);
        result = runListArchivedNodesAsAdmin(queryBuilder);
        assertEquals(acmeUser1 + " deleted only 1 item.", 1, result.getPage().size());

        AuthenticationUtil.setFullyAuthenticatedUser(acmeUser2);
        result = runListArchivedNodesAsAdmin(queryBuilder);
        assertEquals(acmeUser2 + " hasn't deleted anything yet and cannot access other users' trashcan.", 0, result.getPage().size());
        // acmeUser2 deletes his content
        deleteNode(acmeUser2ContentNodeRef);

        result = runListArchivedNodesAsAdmin(queryBuilder);
        assertEquals(acmeUser2 + " deleted only 1 item.", 1, result.getPage().size());

        // Set the authentication to acme's Network Admin
        AuthenticationUtil.setFullyAuthenticatedUser(acmeAdminUser);

        result = nodeArchiveService.listArchivedNodes(queryBuilder);
        // Network Admin can retrieve all users' deleted nodes within his network.
        assertEquals("Network Admin can retrieve all users' deleted nodes within his network.", 2, result.getPage().size());
        // Network Admin deletes his own content
        deleteNode(acmeAdminUserContentNodeRef);
        result = nodeArchiveService.listArchivedNodes(queryBuilder);
        // Admin can retrieve all users' deleted nodes within his network
        assertEquals("Network Admin can retrieve his as well as all users' deleted nodes within his network.", 3, result.getPage().size());

        // Clear any security information
        AuthenticationUtil.clearCurrentSecurityContext();
        // Set the authentication to ping's Network Admin
        AuthenticationUtil.setFullyAuthenticatedUser(pingAdminUser);
        // create a new builder as the archive store root noderef for this tenant is different
        queryBuilder = new ArchivedNodesCannedQueryBuilder.Builder(getArchiveStoreRootNodeRef(), paging).build();
        result = nodeArchiveService.listArchivedNodes(queryBuilder);
        assertEquals(pingAdminUser + " hasn't deleted anything yet and cannot access other network users' trashcan.", 0, result.getPage().size());
    }

    @Test
    public void testRestoreArchivedNode() throws Exception
    {
        AuthenticationUtil.setFullyAuthenticatedUser(acmeUser1);

        ScriptPagingDetails paging = new ScriptPagingDetails(5, 0);
        // Create canned query
        final ArchivedNodesCannedQueryBuilder queryBuilder = new ArchivedNodesCannedQueryBuilder.Builder(getArchiveStoreRootNodeRef(), paging)
                    .build();

        // acmeUser1 deletes his own content
        deleteNode(acmeUser1ContentNodeRef);

        PagingResults<NodeRef> result = runListArchivedNodesAsAdmin(queryBuilder);
        assertEquals(acmeUser1 + " deleted only 1 item.", 1, result.getPage().size());

        final NodeRef archivedNodeRef = result.getPage().get(0);

        AuthenticationUtil.runAs(new RunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                RestoreNodeReport report = nodeArchiveService.restoreArchivedNode(archivedNodeRef);
                assertEquals(acmeUser1 + " was not able to restore his content.", RestoreNodeReport.RestoreStatus.SUCCESS.toString(), report
                            .getStatus().toString());
                return null;
            }
        }, acmeUser1);

        result = runListArchivedNodesAsAdmin(queryBuilder);
        assertEquals(acmeUser1 + " restored the only item, so the trashcan must be empty.", 0, result.getPage().size());

        // acmeUser1 deletes his own content again
        deleteNode(acmeUser1ContentNodeRef);

        // Set the authentication to acme's user2
        AuthenticationUtil.setFullyAuthenticatedUser(acmeUser2);

        // it is the responsibility of the client to check the permission by
        // invoking the hasFullAccess method.       
        AuthenticationUtil.runAs(new RunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                assertFalse(acmeUser2 + " didn't archive " + archivedNodeRef, nodeArchiveService.hasFullAccess(archivedNodeRef));
                return null;
            }
        }, AuthenticationUtil.getAdminUserName());

        // Set the authentication to acme's Network Admin
        AuthenticationUtil.setFullyAuthenticatedUser(acmeAdminUser);
        result = nodeArchiveService.listArchivedNodes(queryBuilder);

        assertEquals("Network Admin can retrieve all users' deleted nodes within his network.", 1, result.getPage().size());

        final NodeRef user1ArchivedNode = result.getPage().get(0);

        AuthenticationUtil.runAs(new RunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                assertTrue("Network admin can access all the archived nodes within his network.", nodeArchiveService.hasFullAccess(user1ArchivedNode));
                RestoreNodeReport report = nodeArchiveService.restoreArchivedNode(user1ArchivedNode);
                assertEquals("Network admin was not able to restore " + acmeUser1 + "'s content.",
                            RestoreNodeReport.RestoreStatus.SUCCESS.toString(), report.getStatus().toString());
                return null;
            }
        }, AuthenticationUtil.getAdminUserName());

        result = nodeArchiveService.listArchivedNodes(queryBuilder);
        assertEquals("Network Admin restored the only item, so the trashcan must be empty.", 0, result.getPage().size());
    }

    @Test
    public void testPurgeArchivedNode() throws Exception
    {
        AuthenticationUtil.setFullyAuthenticatedUser(acmeUser2);

        ScriptPagingDetails paging = new ScriptPagingDetails(5, 0);
        // Create canned query
        final ArchivedNodesCannedQueryBuilder queryBuilder = new ArchivedNodesCannedQueryBuilder.Builder(getArchiveStoreRootNodeRef(), paging)
                    .build();

        // acmeUser2 deletes his content
        deleteNode(acmeUser2ContentNodeRef);

        PagingResults<NodeRef> result = runListArchivedNodesAsAdmin(queryBuilder);
        assertEquals(acmeUser2 + " deleted only 1 item.", 1, result.getPage().size());

        final NodeRef archivedNodeRef = result.getPage().get(0);

        AuthenticationUtil.runAs(new RunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                nodeArchiveService.purgeArchivedNode(archivedNodeRef);
                return null;
            }
        }, acmeUser2);

        result = runListArchivedNodesAsAdmin(queryBuilder);
        assertEquals(acmeUser2 + " permanently deleted the only item, so the trashcan must be empty.", 0, result.getPage().size());

        AuthenticationUtil.setFullyAuthenticatedUser(acmeUser1);
        // acmeUser1 deletes his own content
        deleteNode(acmeUser1ContentNodeRef);
        result = runListArchivedNodesAsAdmin(queryBuilder);
        assertEquals(acmeUser1 + " deleted only 1 item.", 1, result.getPage().size());

        final NodeRef user1ArchivedNodeRef = result.getPage().get(0);
        
        // Set the authentication to acme's user2
        AuthenticationUtil.setFullyAuthenticatedUser(acmeUser2);
        
        // it is the responsibility of the client to check the permission by
        // invoking the hasFullAccess method.
        AuthenticationUtil.runAs(new RunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                assertFalse(acmeUser2 + " didn't archive " + archivedNodeRef, nodeArchiveService.hasFullAccess(user1ArchivedNodeRef));
                return null;
            }
        }, AuthenticationUtil.getAdminUserName());

        // Set the authentication to acme's Network Admin
        AuthenticationUtil.setFullyAuthenticatedUser(acmeAdminUser);
        result = nodeArchiveService.listArchivedNodes(queryBuilder);

        assertEquals("Network Admin can retrieve all users' deleted nodes within his network.", 1, result.getPage().size());

        final NodeRef user1ArchivedNode = result.getPage().get(0);

        AuthenticationUtil.runAs(new RunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                assertTrue("Network admin can access all the archived nodes within his network.", nodeArchiveService.hasFullAccess(user1ArchivedNode));
                nodeArchiveService.purgeArchivedNode(user1ArchivedNode);
                return null;
            }
        }, AuthenticationUtil.getAdminUserName());

        result = nodeArchiveService.listArchivedNodes(queryBuilder);
        assertEquals("Network Admin permanently deleted the only item, so the trashcan must be empty.", 0, result.getPage().size());
    }

    private NodeRef getArchiveStoreRootNodeRef()
    {
        return nodeService.getStoreArchiveNode(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
    }

    private void deleteNode(final NodeRef nodeRef)
    {
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                nodeService.deleteNode(nodeRef);
                return null;
            }
        });
    }

    private PagingResults<NodeRef> runListArchivedNodesAsAdmin(final ArchivedNodesCannedQueryBuilder queryBuilder)
    {
        return AuthenticationUtil.runAs(new RunAsWork<PagingResults<NodeRef>>()
        {
            @Override
            public PagingResults<NodeRef> doWork() throws Exception
            {
                return nodeArchiveService.listArchivedNodes(queryBuilder);
            }
        }, AuthenticationUtil.getAdminUserName());
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

    public static Account createUser(final String email, final String firstName, final String lastName, final String passwd, final boolean isAdmin)
    {
        return transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Account>()
        {
            @Override
            public Account execute() throws Throwable
            {
                Account account = registrationService.createUser(email, firstName, lastName, passwd);
                cloudContext.addUser(email);
                assertNotNull("Account was null.", account);

                if (isAdmin)
                {
                    registrationService.promoteUserToNetworkAdmin(account.getId(), email);
                }
                return account;
            }
        });
    }

    public NodeRef creatSiteAndAddContent(final String siteShortName, String user, String tenant)
    {
        // Create additional sites
        TenantRunAsWork<NodeRef> createSiteWork = new TenantRunAsWork<NodeRef>()
        {
            @Override
            public NodeRef doWork() throws Exception
            {
                return transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>()
                {
                    @Override
                    public NodeRef execute() throws Throwable
                    {
                        siteService.createSite("site-preset", siteShortName, "site title", "site description", SiteVisibility.PUBLIC);
                        NodeRef docLibNodeRef = siteService.getContainer(siteShortName, SiteService.DOCUMENT_LIBRARY);

                        // Create a test node. It doesn't need a content.
                        NodeRef nodeRef = nodeService.createNode(docLibNodeRef, ContentModel.ASSOC_CONTAINS, ContentModel.ASSOC_CONTAINS,
                                    ContentModel.TYPE_CONTENT).getChildRef();
                        return nodeRef;
                    }
                });
            }
        };
        return TenantUtil.runAsUserTenant(createSiteWork, user, tenant);
    }
}
