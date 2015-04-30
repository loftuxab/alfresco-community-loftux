/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.alfresco.enterprise.repo.sync.audit.SyncAuditService;
import org.alfresco.enterprise.repo.sync.audit.SyncAuditServiceImplTest.SyncAuditCleaner;
import org.alfresco.enterprise.repo.sync.transport.CloudSyncSetDefinitionTransport;
import org.alfresco.enterprise.repo.sync.transport.impl.CloudSyncSetDefinitionTransportImpl;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.mode.ServerMode;
import org.alfresco.repo.mode.ServerModeProvider;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.descriptor.DescriptorService;
import org.alfresco.util.test.junitrules.AlfrescoPerson;
import org.alfresco.util.test.junitrules.RunAsFullyAuthenticatedRule;
import org.alfresco.util.test.junitrules.TemporaryNodes;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestName;
import org.springframework.context.ApplicationContext;

/**
 * Integration tests for {@link SyncAdminServiceImpl}.
 * 
 * @author Neil Mc Erlean
 * @since 4.1
 */
public class SyncAdminServiceImplTest extends BaseSyncServiceImplTest
{
    private static final Log log = LogFactory.getLog(SyncAdminServiceImplTest.class);

    /**
     * A SSdId mapping strategy intended for use only in testing.
     */
    public static class DevelopmentTestSsdIdMapping implements SsdIdMappingStrategy
    {
        public static final String CLOUD_PREFIX = "cloud_";
        
        @Override public String getCloudGUID(String onPremiseGuid) { return CLOUD_PREFIX + onPremiseGuid; }
        
        @Override public String getOnPremiseGUID(String cloudGuid) { return cloudGuid.replace(CLOUD_PREFIX, ""); }
    }
    
    // Rules to create 2 test users.
    public static AlfrescoPerson TEST_USER1 = new AlfrescoPerson(APP_CONTEXT_INIT, "UserOne");
    public static AlfrescoPerson TEST_USER2 = new AlfrescoPerson(APP_CONTEXT_INIT, "UserTwo");
    
    // Tie them together in a static Rule Chain
    @ClassRule public static RuleChain ruleChain = RuleChain.outerRule(APP_CONTEXT_INIT)
                                                            .around(TEST_USER1)
                                                            .around(TEST_USER2);
    
    // A rule to manage test nodes use in each test method
    @Rule public TemporaryNodes temporaryNodes = new TemporaryNodes(APP_CONTEXT_INIT);
    
    // A rule to ensure the Sync Audit is cleaned
    @Rule public SyncAuditCleaner syncAuditCleaner = new SyncAuditCleaner(APP_CONTEXT_INIT);
    
    // A rule to provide the currently running test method's name.
    @Rule public TestName testName = new TestName();
    
    // A rule to allow individual test methods all to be run as "UserOne".
    // Some test methods need to switch user during execution which they are free to do.
    @Rule public RunAsFullyAuthenticatedRule runAsRule = new RunAsFullyAuthenticatedRule(TEST_USER1);
    
    // Various services
    private static NodeService                 NODE_SERVICE;
    private static PermissionService           PERMISSION_SERVICE;
    private static RetryingTransactionHelper   TRANSACTION_HELPER;
    private static SyncAdminService            SYNC_ADMIN_SERVICE;
    private static SyncAuditService            SYNC_AUDIT_SERVICE;
    private static CloudSyncSetDefinitionTransport SSD_TRANSPORT;
    private static LockService                 LOCK_SERVICE;
    private static CheckOutCheckInService      CHECK_OUT_CHECK_IN;
    
    private static NodeRef COMPANY_HOME;
    private static String  SRC_REPO_ID;
    
    private NodeRef testNodeU1_1, testNodeU1_2, testNodeU1_3;
    private NodeRef testNodeU2_1, testNodeU2_2;
    
    private NodeRef syncedNode1, syncedNode2, syncedNode3;
    private SyncSetDefinition precreatedSsd;
    
    private NodeRef           syncedFolderNode;
    private List<NodeRef>     syncedFolderDescendants;
    private SyncSetDefinition syncedFolderSSD;
    
    private String RUNID = System.currentTimeMillis()+"";
    
    @BeforeClass public static void initStaticData() throws Exception
    {
        final ApplicationContext springContext = APP_CONTEXT_INIT.getApplicationContext();
        
        NODE_SERVICE              = springContext.getBean("nodeService", NodeService.class);
        PERMISSION_SERVICE        = springContext.getBean("permissionService", PermissionService.class);
        TRANSACTION_HELPER        = springContext.getBean("retryingTransactionHelper", RetryingTransactionHelper.class);
        SYNC_ADMIN_SERVICE        = springContext.getBean("syncAdminService", SyncAdminService.class);
        SYNC_AUDIT_SERVICE        = springContext.getBean("syncAuditService", SyncAuditService.class);
        SSD_TRANSPORT             = springContext.getBean("cloudSyncSetDefinitionTransport", CloudSyncSetDefinitionTransport.class);
        LOCK_SERVICE              = springContext.getBean("lockService", LockService.class);
        CHECK_OUT_CHECK_IN        = springContext.getBean("checkOutCheckInService", CheckOutCheckInService.class);

        
        // Push the "developer" mode switch to stop checking licenses
        ((SyncAdminServiceImpl)springContext.getBean("syncAdminService")).setCheckLicenseForSyncMode(false);
        
        // Wire in our mock/testing cloud connector
        ((SyncAdminServiceImpl)SYNC_ADMIN_SERVICE).setCloudConnectorService(MOCK_CLOUD_CONNECTOR_SERVICE);
        ((CloudSyncSetDefinitionTransportImpl)SSD_TRANSPORT).setCloudConnectorService(MOCK_CLOUD_CONNECTOR_SERVICE);
        
        Repository repositoryHelper = (Repository) springContext.getBean("repositoryHelper");
        COMPANY_HOME = repositoryHelper.getCompanyHome();
        
        DescriptorService descriptorService = (DescriptorService)springContext.getBean("DescriptorService");
        SRC_REPO_ID = descriptorService.getCurrentRepositoryDescriptor().getId();
        
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
        
    }
    
    @Before public void createTestContent()
    {
        // Create some test content (unsynced initially)
        testNodeU1_1 = temporaryNodes.createNode(COMPANY_HOME, "User1 node1"+RUNID, ContentModel.TYPE_CONTENT, TEST_USER1.getUsername());
        testNodeU1_2 = temporaryNodes.createNode(COMPANY_HOME, "User1 node2"+RUNID, ContentModel.TYPE_CONTENT, TEST_USER1.getUsername());
        testNodeU1_3 = temporaryNodes.createNode(COMPANY_HOME, "User1 node3"+RUNID, ContentModel.TYPE_CONTENT, TEST_USER1.getUsername());
        
        testNodeU2_1 = temporaryNodes.createNode(COMPANY_HOME, "User2 node1"+RUNID, ContentModel.TYPE_CONTENT, TEST_USER2.getUsername());
        testNodeU2_2 = temporaryNodes.createNode(COMPANY_HOME, "User2 node2"+RUNID, ContentModel.TYPE_CONTENT, TEST_USER2.getUsername());
        
        // Create some test content which is already set up for syncing at @Test execution.
        syncedNode1 = temporaryNodes.createNode(COMPANY_HOME, "User1 synced node1"+RUNID, ContentModel.TYPE_CONTENT, TEST_USER1.getUsername());
        syncedNode2 = temporaryNodes.createNode(COMPANY_HOME, "User1 synced node2"+RUNID, ContentModel.TYPE_CONTENT, TEST_USER1.getUsername());
        syncedNode3 = temporaryNodes.createNode(COMPANY_HOME, "User2 synced node3"+RUNID, ContentModel.TYPE_CONTENT, TEST_USER1.getUsername());
        
        final List<NodeRef> syncMembers = Arrays.asList(new NodeRef[]{ syncedNode1, syncedNode2, syncedNode3});
        precreatedSsd = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<SyncSetDefinition>()
        {
            public SyncSetDefinition execute() throws Throwable
            {
                SyncSetDefinition ssd = SYNC_ADMIN_SERVICE.createSourceSyncSet(syncMembers, "remoteTenant", "cloud://node/Ref", false, true, false);
                temporaryNodes.addNodeRef(ssd.getNodeRef());
                
                assertNotNull(ssd);
                assertNotNull(ssd.getId());
                
                // The tests below aren't interested in the audit events generated by the above test data
                SYNC_AUDIT_SERVICE.clearAudit();
                
                return ssd;
            }
        });
        
        
        // Now create a deep folder of content and sync it at the root.
        syncedFolderNode     = temporaryNodes.createNode(COMPANY_HOME, "User1 synced folder"+RUNID, ContentModel.TYPE_FOLDER, TEST_USER1.getUsername());
        NodeRef folderChild1 = temporaryNodes.createNode(syncedFolderNode, "User1 synced child1"+RUNID, ContentModel.TYPE_CONTENT, TEST_USER1.getUsername());
        NodeRef folderChild2 = temporaryNodes.createNode(syncedFolderNode, "User1 synced child2"+RUNID, ContentModel.TYPE_CONTENT, TEST_USER1.getUsername());
        NodeRef subFolder    = temporaryNodes.createNode(syncedFolderNode, "User1 synced subfolder"+RUNID, ContentModel.TYPE_FOLDER, TEST_USER1.getUsername());
        NodeRef subfolderChild1 = temporaryNodes.createNode(subFolder, "User1 synced grandchild1"+RUNID, ContentModel.TYPE_CONTENT, TEST_USER1.getUsername());
        NodeRef subfolderChild2 = temporaryNodes.createNode(subFolder, "User1 synced grandchild2"+RUNID, ContentModel.TYPE_CONTENT, TEST_USER1.getUsername());
        
        syncedFolderDescendants = Arrays.asList(new NodeRef[] {folderChild1, folderChild2, subFolder, subfolderChild1, subfolderChild2});
        
        // Now sync them.
        syncedFolderSSD = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<SyncSetDefinition>()
        {
            public SyncSetDefinition execute() throws Throwable
            {
                SyncSetDefinition ssd = SYNC_ADMIN_SERVICE.createSourceSyncSet(Arrays.asList(new NodeRef[]{syncedFolderNode}),
                                                                               "remoteTenant", "cloud://node/Ref",
                                                                               false, true, true, false);
                temporaryNodes.addNodeRef(ssd.getNodeRef());
                
                // The tests below aren't interested in the audit events generated by the above test data
                SYNC_AUDIT_SERVICE.clearAudit();
                
                return ssd;
            }
        });
    }
    
    @Test public void ensureSyncSetDefinitionsFolderExists() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                assertNotNull("The SyncSetDefinitions folder is null.", SYNC_ADMIN_SERVICE.getSyncSetDefinitionsFolder());
                
                return null;
            }
        });
    }
    
    private int getSSDCount()
    {
        return TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Integer>()
        {
            public Integer execute() throws Throwable
            {
                return SYNC_ADMIN_SERVICE.getSyncSetDefinitions().size();
            }
        });
    }
    
    @Test public void createGetAndDeleteSyncSetDefinition() throws Exception
    {
        // Create the SSD
        final List<NodeRef> syncMembers = Arrays.asList(new NodeRef[]{ testNodeU1_1, testNodeU1_2, testNodeU1_3});
        
        final String remoteTenantId = "remoteTenant";
        final String targetFolderNodeRef = "cloud://node/Ref";
        final boolean lockSourceCopy = false;
        
        final int ssdCountBefore = getSSDCount();
        
        final SyncSetDefinition newSSD = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<SyncSetDefinition>()
        {
            public SyncSetDefinition execute() throws Throwable
            {
                SyncSetDefinition ssd = SYNC_ADMIN_SERVICE.createSourceSyncSet(syncMembers, remoteTenantId, targetFolderNodeRef, lockSourceCopy, true, false);
                temporaryNodes.addNodeRef(ssd.getNodeRef());
                
                assertNotNull(ssd);
                assertNotNull(ssd.getId());
                return ssd;
            }
        });
        
        // Retrieve the SSD
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                SyncSetDefinition ssd = SYNC_ADMIN_SERVICE.getSyncSetDefinition(newSSD.getId());
                
                assertNotNull(ssd);
                assertEquals(newSSD.getId(), ssd.getId());
                assertEquals(remoteTenantId, newSSD.getRemoteTenantId());
                assertEquals(targetFolderNodeRef, newSSD.getTargetFolderNodeRef());
                assertEquals(lockSourceCopy, newSSD.getLockSourceCopy());
                
                return null;
            }
        });
        
        // Retrieve all SSDs
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                List<SyncSetDefinition> ssds = SYNC_ADMIN_SERVICE.getSyncSetDefinitions();
                
                assertNotNull(ssds);
                
                assertEquals("Wrong number of SSDs", ssdCountBefore+1, ssds.size());
                
                return null;
            }
        });
        
        // System should now have SSDs
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                assertTrue("System should have SSDs", SYNC_ADMIN_SERVICE.hasSyncSetDefintions());
                return null;
            }
        });
        
        // Delete the SSD
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                SYNC_ADMIN_SERVICE.deleteSourceSyncSet(newSSD.getId());
                return null;
            }
        });
        
        // Ensure members do not have the member aspect
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                for (NodeRef exMember : syncMembers)
                {
                    assertFalse("Former SSMN still had sync aspect.", NODE_SERVICE.hasAspect(exMember, SyncModel.ASPECT_SYNC_SET_MEMBER_NODE));
                }
                
                return null;
            }
        });
    }
    
    //FIXME
    @Ignore("Not currently working") @Test public void deletingLastSsmnShouldTriggerDeletionOfSsd() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                // Our test SSD must exist and must have at least one member node.
                assertNotNull("SSD did not exist", precreatedSsd);
                final List<NodeRef> memberNodes = SYNC_ADMIN_SERVICE.getMemberNodes(precreatedSsd);
                assertFalse("SSD had no members", memberNodes.isEmpty());
                
                // Now remove each of the SSMNs - but do not explicitly delete the SSD itself.
                for (NodeRef ssmn : memberNodes)
                {
                    SYNC_ADMIN_SERVICE.removeSyncSetMember(precreatedSsd, ssmn);
                }
                
                // Ensure that the SSD itself is removed.
                assertNull("SSD was not auto-deleted as it should have been.", SYNC_ADMIN_SERVICE.getSyncSetDefinition(precreatedSsd.getId()));
                
                return null;
            }
        });
    }
    
    @Test public void getSyncSetDefinitionFromRawNodeRefs() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                assertEquals("Failed to retrieve SSD from the SSD NodeRef",  precreatedSsd, SYNC_ADMIN_SERVICE.getSyncSetDefinition(precreatedSsd.getNodeRef()));
                assertEquals("Failed to retrieve SSD from the SSMN NodeRef", precreatedSsd, SYNC_ADMIN_SERVICE.getSyncSetDefinition(syncedNode1));
                
                assertNull("Problem retrieving SSD from unrelated NodeRef", SYNC_ADMIN_SERVICE.getSyncSetDefinition(testNodeU1_1));
                
                return null;
            }
        });
    }
    
    @Test public void addAndRemoveSsmnsToFromSsd() throws Exception
    {
        final List<NodeRef> expectedMembers = new ArrayList<NodeRef>();
        expectedMembers.add(syncedNode1);
        expectedMembers.add(syncedNode2);
        expectedMembers.add(syncedNode3);
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                // Is the SSD correctly configured at the start?
                assertEquals("Incorrect SSD membership",  expectedMembers, SYNC_ADMIN_SERVICE.getMemberNodes(precreatedSsd));
                
                
                // Some negative tests first.
                
                boolean expectedExceptionThrown = false;
                assertTrue(SYNC_ADMIN_SERVICE.isSyncSetMemberNode(syncedNode1, precreatedSsd));
                
                // Can't remove a node from an SSD when it isn't a member.
                assertFalse(SYNC_ADMIN_SERVICE.isSyncSetMemberNode(testNodeU1_1, precreatedSsd));
                expectedExceptionThrown = false;
                try
                {
                    SYNC_ADMIN_SERVICE.removeSyncSetMember(precreatedSsd, testNodeU1_1);
                }
                catch (NodeNotSyncRelatedException expected)
                {
                    expectedExceptionThrown = true;
                }
                assertTrue("Expected exception was not thrown.", expectedExceptionThrown);
                
                // Now the positive test cases.
                // Add a new member to an SSD.
                SYNC_ADMIN_SERVICE.addSyncSetMember(precreatedSsd, testNodeU1_1);
                expectedMembers.add(testNodeU1_1);
                assertEquals("Incorrect SSD membership",  expectedMembers, SYNC_ADMIN_SERVICE.getMemberNodes(precreatedSsd));
                assertTrue(SYNC_ADMIN_SERVICE.isSyncSetMemberNode(testNodeU1_1, precreatedSsd));
                
                // Can add a node to the an SSD twice - effectively a NOOP (info message logged as a dev reminder)
                SYNC_ADMIN_SERVICE.addSyncSetMember(precreatedSsd, syncedNode1);
                
                // Now remove a member
                SYNC_ADMIN_SERVICE.removeSyncSetMember(precreatedSsd, syncedNode3);
                expectedMembers.remove(syncedNode3);
                assertEquals("Incorrect SSD membership",  expectedMembers, SYNC_ADMIN_SERVICE.getMemberNodes(precreatedSsd));
                assertFalse(SYNC_ADMIN_SERVICE.isSyncSetMemberNode(syncedNode3, precreatedSsd));
                
                return null;
            }
        });
    }
    
    @Test public void getMemberNodes() throws Exception
    {
        // Create the SSD
        final List<NodeRef> syncMembers = Arrays.asList(new NodeRef[]{ testNodeU1_1, testNodeU1_2, testNodeU1_3});
        
        final String remoteTenantId = "remoteTenant";
        final String targetFolderNodeRef = "cloud://node/Ref";
        final boolean lockSourceCopy = false;
        
        final SyncSetDefinition newSSD = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<SyncSetDefinition>()
        {
            public SyncSetDefinition execute() throws Throwable
            {
                SyncSetDefinition ssd = SYNC_ADMIN_SERVICE.createSourceSyncSet(syncMembers, remoteTenantId, targetFolderNodeRef, lockSourceCopy, true, false);
                temporaryNodes.addNodeRef(ssd.getNodeRef());
                
                assertNotNull(ssd);
                assertNotNull(ssd.getId());
                return ssd;
            }
        });
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                assertEquals(syncMembers, SYNC_ADMIN_SERVICE.getMemberNodes(newSSD));
                
                return null;
            }
        });
    }
    
    /**
     * It should not be possible to create a sync set any of whose members are already in another sync set.
     */
    @Test public void cannotCreateTwoSyncSetDefinitionsWithTheSameId() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                final List<NodeRef> syncMembers1 = Arrays.asList(new NodeRef[]{ testNodeU1_1});
                SyncSetDefinition ssd1 = SYNC_ADMIN_SERVICE.createSourceSyncSet(syncMembers1, "remoteTenantId", "remote://node/ref", false, true, false);
                temporaryNodes.addNodeRef(ssd1.getNodeRef());
                
                boolean exceptionThrown = false;
                try
                {
                    SYNC_ADMIN_SERVICE.createTargetSyncSet(ssd1.getId(), SRC_REPO_ID, null, false, true, false);
                }
                catch (SyncAdminServiceException expected)
                {
                    exceptionThrown = true;
                }
                assertTrue("Expected exception not thrown", exceptionThrown);
                
                return null;
            }
        });
    }
    
    @Test public void creatingTwoSyncSetDefinitionsWithOverlappingLocalMembersShouldSilentlyDropTheOverlaps() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                final List<NodeRef> syncMembers1 = Arrays.asList(new NodeRef[]{ testNodeU1_1, testNodeU1_2});
                SyncSetDefinition ssd1 = SYNC_ADMIN_SERVICE.createSourceSyncSet(syncMembers1, "remoteTenantId", "remote://node/ref", false, true, false);
                temporaryNodes.addNodeRef(ssd1.getNodeRef());
                
                final List<NodeRef> syncMembers2 = Arrays.asList(new NodeRef[]{ testNodeU1_2, testNodeU1_3});
                
                SyncSetDefinition ssd2 = SYNC_ADMIN_SERVICE.createSourceSyncSet(syncMembers2, "remoteTenantId2", "remote://node/ref2", false, true, false);
                assertEquals("Wrong nodes synced", Arrays.asList(new NodeRef[] {testNodeU1_3}),
                                                        SYNC_ADMIN_SERVICE.getMemberNodes(ssd2));
                
                return null;
            }
        });
    }
    
    @Test public void fileBasedSyncToCloudShouldSilentlySkipInvalidMemberNodes() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                final List<NodeRef> syncMembers = Arrays.asList(new NodeRef[]{ testNodeU1_1, testNodeU1_2, testNodeU2_1, testNodeU2_2});
                // Note that this user doesn't have write access to user two's nodes
                assertEquals(AccessStatus.DENIED, PERMISSION_SERVICE.hasPermission(testNodeU2_1, PermissionService.WRITE_CONTENT));
                assertEquals(AccessStatus.DENIED, PERMISSION_SERVICE.hasPermission(testNodeU2_1, PermissionService.WRITE_PROPERTIES));
                
                SyncSetDefinition ssd = SYNC_ADMIN_SERVICE.createSourceSyncSet(syncMembers, "remoteTenantId", "remote://node/ref", false, true, false);
                assertEquals(Arrays.asList(new NodeRef[]{ testNodeU1_1, testNodeU1_2}), SYNC_ADMIN_SERVICE.getMemberNodes(ssd));
                
                return null;
            }
        });
    }
    
    @Test public void fileBasedSyncToCloudWithOnlyInvalidMemberNodesShouldThrowException() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                final List<NodeRef> syncMembers = Arrays.asList(new NodeRef[]{ testNodeU2_1, testNodeU2_2});
                // Note that this user doesn't have write access to user two's nodes
                assertEquals(AccessStatus.DENIED, PERMISSION_SERVICE.hasPermission(testNodeU2_1, PermissionService.WRITE_CONTENT));
                assertEquals(AccessStatus.DENIED, PERMISSION_SERVICE.hasPermission(testNodeU2_1, PermissionService.WRITE_PROPERTIES));
                
                boolean exceptionThrown = false;
                try
                {
                    SYNC_ADMIN_SERVICE.createSourceSyncSet(syncMembers, "remoteTenantId", "remote://node/ref", false, true, false);
                }
                catch (SyncSetCreationConflictException expected)
                {
                    exceptionThrown = true;
                    assertEquals("Wrong nodes reported", Arrays.asList(new NodeRef[] {testNodeU2_1, testNodeU2_2}),
                                                            expected.getIllegalNodes());
                }
                assertTrue("Expected exception not thrown", exceptionThrown);
                
                return null;
            }
        });
    }
    
    /** See ALF-14950 */
    @Test public void addingFileAsSecondaryChildToSyncedFolderShouldNotAddChildToSyncSet() throws Exception
    {
        // Create a standalone, separate node.
        final NodeRef secondaryChildNode = temporaryNodes.createNode(COMPANY_HOME, testName + "_secondaryChild",
                                                                     ContentModel.TYPE_CONTENT,
                                                                     AuthenticationUtil.getFullyAuthenticatedUser());
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                // Now add that node as a *secondary* child of an already synced folder.
                ChildAssociationRef chAssRef = NODE_SERVICE.addChild(syncedFolderNode, secondaryChildNode, ContentModel.ASSOC_CONTAINS, ContentModel.ASSOC_CONTAINS);
                assertFalse(chAssRef.isPrimary());
                
                return null;
            }
        });
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                assertFalse("Nodes added as secondary children to synced folders should not be added to sync set.",
                            SYNC_ADMIN_SERVICE.getMemberNodes(syncedFolderSSD).contains(secondaryChildNode));
                // And just to make sure we've got the correct SSD:
                assertTrue(SYNC_ADMIN_SERVICE.isSyncSetMemberNode(syncedFolderNode, syncedFolderSSD));
                
                return null;
            }
        });
    }
    
    @Test public void folderBasedSyncToCloudShouldSilentlySkipAlreadySyncedButNotUnwritableIndirectMemberNodes() throws Exception
    {
        // This test case is a bit of a special case. We'll create test nodes especially for this test case.
        // The code that handles direct and indirect sync set members is slightly different. This method
        // is providing coverage for the indirect member's membership validation.
        // We shouldn't need to provide a deep folder. Immediate children should be enough.
        final String folderName = SyncAdminServiceImplTest.class.getSimpleName() + "." + testName;
        
        // Create a folder that we'll sync below.
        final NodeRef folderNode = temporaryNodes.createNode(COMPANY_HOME, folderName, ContentModel.TYPE_FOLDER, AuthenticationUtil.getFullyAuthenticatedUser());
        
        // Put a number of immediate child nodes into that folder.
        final Set<NodeRef> normalChildNodes = new HashSet<NodeRef>();
        
        final int immediateChildCount = 15;
        for (int i = 0; i < immediateChildCount; i++)
        {
            // We don't need to add any content.
            normalChildNodes.add(temporaryNodes.createNode(folderNode, folderName + "tosync" + i, ContentModel.TYPE_CONTENT, AuthenticationUtil.getFullyAuthenticatedUser()));
        }
        
        // Create some more immediate children - BUT AS A DIFFERENT USER - these should not be syncable by the current user.
        assertFalse(TEST_USER2.getUsername().equals(AuthenticationUtil.getFullyAuthenticatedUser()));
        
        final Set<NodeRef> unwritableNodes = new HashSet<NodeRef>();
        for (int i= 0; i < 8; i++)
        {
            unwritableNodes.add(temporaryNodes.createNode(folderNode, folderName + "unwritable" + i, ContentModel.TYPE_CONTENT, TEST_USER2.getUsername()));
        }
        
        // Create some more immediate children...
        final Set<NodeRef> alreadySyncedNodes = new HashSet<NodeRef>();
        for (int i= 0; i < 8; i++)
        {
            alreadySyncedNodes.add(temporaryNodes.createNode(folderNode, folderName + "alreadySynced" + i, ContentModel.TYPE_CONTENT, AuthenticationUtil.getFullyAuthenticatedUser()));
        }
        
        // ... and put them in another syncset - these should not then by syncable again.
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                // We need to convert the Set<NodeRef> to a List<NodeRef>.
                List<NodeRef> alreadySyncedNodesList = new ArrayList<NodeRef>(alreadySyncedNodes.size());
                alreadySyncedNodesList.addAll(alreadySyncedNodes);
                SYNC_ADMIN_SERVICE.createSourceSyncSet(alreadySyncedNodesList, "remoteTenant", "test://test/testFolder", false, true, false);
                return null;
            }
        });
        
        
        final SyncSetDefinition ssd = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<SyncSetDefinition>()
        {
            public SyncSetDefinition execute() throws Throwable
            {
                // Try to sync all these nodes -  no exceptions should be thrown here.
                final SyncSetDefinition ssd = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<SyncSetDefinition>()
                {
                    public SyncSetDefinition execute() throws Throwable
                    {
                        return SYNC_ADMIN_SERVICE.createSourceSyncSet(Arrays.asList(new NodeRef[]{ folderNode }), "remoteTenant", "test://test/testFolder", false, true, false);
                    }
                });
                return ssd;
            }
        });
        
        // Ensure that the sync set members are only the valid nodes from above.
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                List<NodeRef> members = SYNC_ADMIN_SERVICE.getMemberNodes(ssd);
                Set<NodeRef> memberSet = new HashSet<NodeRef>();
                memberSet.addAll(members);
                
                assertTrue("The synced folder should have been a member.", members.contains(folderNode));
                assertTrue("The normal children should all be members", members.containsAll(normalChildNodes));
                assertTrue("The unwritable children should all be members", members.containsAll(unwritableNodes));
                assertFalse("None of the already-synced children should be members", org.apache.commons.collections.CollectionUtils.containsAny(members, alreadySyncedNodes));
                return null;
            }
        });
    }
    
    /** See ALF-15178 for details. */
    @Ignore("Issue not reproduced.") @Test public void creatingLargeSyncSetsShouldNotExceedThePropValTxCache() throws Exception
    {
        final String folderName = SyncAdminServiceImplTest.class.getSimpleName() + "." + testName;
        
        // Create a folder that we'll sync below.
        final NodeRef folderNode = temporaryNodes.createNode(COMPANY_HOME, folderName, ContentModel.TYPE_FOLDER, AuthenticationUtil.getFullyAuthenticatedUser());
        
        // Put a large number of child nodes into that folder.
        final Set<NodeRef> normalChildNodes = new HashSet<NodeRef>();
        
        final int immediateChildCount = 3000;
        for (int i = 0; i < immediateChildCount; i++)
        {
            normalChildNodes.add(temporaryNodes.createNode(folderNode, folderName + "tosync" + i, ContentModel.TYPE_FOLDER, AuthenticationUtil.getFullyAuthenticatedUser()));
        }
        
        // Now try to sync the folder and its many children.
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                SYNC_ADMIN_SERVICE.createSourceSyncSet(Arrays.asList(new NodeRef[] {folderNode}), "remoteTenant", "test://test/testFolder", false, true, false);
                return null;
            }
        });
    }
    
    @Test public void syncMembersShouldHaveSyncOwnerProperty() throws Exception
    {
        // Directly synced file.
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                assertEquals("The SyncOwner is wrong.", TEST_USER1.getUsername(),
                                                        NODE_SERVICE.getProperty(syncedNode1, SyncModel.PROP_SYNC_OWNER));
                return null;
            }
        });
        
        // Indirectly synced file.
        final NodeRef folder = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {
                NodeRef folder = temporaryNodes.createNode(COMPANY_HOME, "Test folder", ContentModel.TYPE_FOLDER, TEST_USER1.getUsername());
                NodeRef singleFolderContent = temporaryNodes.createNode(folder, "Test file", ContentModel.TYPE_CONTENT, TEST_USER1.getUsername());
                
                final List<NodeRef> syncMembers = Arrays.asList(new NodeRef[]{ folder});
                SyncSetDefinition ssd = SYNC_ADMIN_SERVICE.createSourceSyncSet(syncMembers, "remoteTenant", "cloud://node/Ref", false, true, false);
                temporaryNodes.addNodeRef(ssd.getNodeRef());
                
                assertEquals("The SyncOwner is wrong.", TEST_USER1.getUsername(),
                                                        NODE_SERVICE.getProperty(singleFolderContent, SyncModel.PROP_SYNC_OWNER));
                
                // The tests below aren't interested in the audit events generated by the above test data
                SYNC_AUDIT_SERVICE.clearAudit();
                
                return folder;
            }
        });
        
        // Adding a file to a synced folder after SSD creation.
        final NodeRef newSsmn = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {
                NodeRef additionalFolderContent = temporaryNodes.createNode(folder, "Additional Test file", ContentModel.TYPE_CONTENT, TEST_USER1.getUsername());
                temporaryNodes.addNodeRef(additionalFolderContent);
                return additionalFolderContent;
            }
        });
        
        // note: check in separate txn (since sync is currently created on commit - to avoid sync'ing working copies)
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                assertEquals("The SyncOwner is wrong.", TEST_USER1.getUsername(),
                                                        NODE_SERVICE.getProperty(newSsmn, SyncModel.PROP_SYNC_OWNER));
                return null;
            }
        });
        
        // Removing the file from the SyncSet should remove the property.
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {
                SYNC_ADMIN_SERVICE.removeSyncSetMember(SYNC_ADMIN_SERVICE.getSyncSetDefinition(folder), folder);
                assertNull("The SyncOwner is wrong.", NODE_SERVICE.getProperty(newSsmn, SyncModel.PROP_SYNC_OWNER));
                
                return folder;
            }
        });
    }
    
    @Test public void syncedNodesShouldReturnCorrectLocalRootNode() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                assertNull("Wrong root node for unsynced node", SYNC_ADMIN_SERVICE.getRootNodeRef(testNodeU1_1));
                
                assertEquals("Wrong root node for root node", syncedFolderNode, SYNC_ADMIN_SERVICE.getRootNodeRef(syncedFolderNode));
                for (NodeRef node : syncedFolderDescendants)
                {
                    System.err.println("Checking descendant node " + node);
                    
                    assertEquals("Wrong root node for descendant node", syncedFolderNode, SYNC_ADMIN_SERVICE.getRootNodeRef(node));
                }
                
                return null;
            }
        });
    }
    
    @Test public void syncingAFolderContainingSysHiddenNodesShouldExcludeTheSysHiddenNodes() throws Exception
    {
        final String folderName = SyncAdminServiceImplTest.class.getSimpleName() + "." + testName;
        
        // Create a folder that we'll sync below.
        final NodeRef folderNode = temporaryNodes.createNode(COMPANY_HOME, folderName, ContentModel.TYPE_FOLDER, AuthenticationUtil.getFullyAuthenticatedUser());
        
        // Put a number of immediate child nodes into that folder.
        final Set<NodeRef> normalChildNodes = new HashSet<NodeRef>();
        
        final int immediateChildCount = 15;
        for (int i = 0; i < immediateChildCount; i++)
        {
            // We don't need to add any content.
            normalChildNodes.add(temporaryNodes.createNode(folderNode, folderName + "tosync" + i, ContentModel.TYPE_CONTENT, AuthenticationUtil.getFullyAuthenticatedUser()));
        }
        
        // Make 1/3 of the children hidden
        final Set<NodeRef> hiddenChildren = new HashSet<NodeRef>();
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                int i = 0;
                for (Iterator<NodeRef> iter = hiddenChildren.iterator(); iter.hasNext(); i++)
                {
                    NodeRef nr = iter.next();
                    if (i % 3 == 0)
                    {
                        hiddenChildren.add(nr);
                    }
                    NODE_SERVICE.addAspect(nr, ContentModel.ASPECT_HIDDEN, null);
                }
                return null;
            }
        });
        
        // Now sync the folder.
        final SyncSetDefinition ssd = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<SyncSetDefinition>()
        {
            public SyncSetDefinition execute() throws Throwable
            {
                return SYNC_ADMIN_SERVICE.createSourceSyncSet(Arrays.asList(new NodeRef[] { folderNode }), "remoteTenant", "test://test/testFolder", false, true, false);
            }
        });
        
        // Ensure that the sync set members are only the valid nodes from above.
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                List<NodeRef> members = SYNC_ADMIN_SERVICE.getMemberNodes(ssd);
                Set<NodeRef> memberSet = new HashSet<NodeRef>();
                memberSet.addAll(members);
                
                assertTrue("The synced folder should have been a member.", members.contains(folderNode));
                assertTrue("The normal children should all be members", members.containsAll(normalChildNodes));
                assertFalse("None of the hidden children should be members", org.apache.commons.collections.CollectionUtils.containsAny(members, hiddenChildren));
                return null;
            }
        });
    }
    
    @Test public void syncingAFolderAndThenSeparatelyAddingSysHiddenNodesShouldExcludeTheSysHiddenNodes() throws Exception
    {
        final String folderName = SyncAdminServiceImplTest.class.getSimpleName() + "." + testName;
        
        // Create a folder that we'll sync below.
        final NodeRef folderNode = temporaryNodes.createNode(COMPANY_HOME, folderName, ContentModel.TYPE_FOLDER, AuthenticationUtil.getFullyAuthenticatedUser());
        
        // Put a number of immediate child nodes into that folder.
        final Set<NodeRef> normalChildNodes = new HashSet<NodeRef>();
        
        final int immediateChildCount = 15;
        for (int i = 0; i < immediateChildCount; i++)
        {
            // We don't need to add any content.
            normalChildNodes.add(temporaryNodes.createNode(folderNode, folderName + "tosync" + i, ContentModel.TYPE_CONTENT, AuthenticationUtil.getFullyAuthenticatedUser()));
        }
        
        // Now sync the folder.
        final SyncSetDefinition ssd = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<SyncSetDefinition>()
        {
            public SyncSetDefinition execute() throws Throwable
            {
                final SyncSetDefinition sourceSyncSet = SYNC_ADMIN_SERVICE.createSourceSyncSet(Arrays.asList(new NodeRef[] { folderNode }), "remoteTenant", "test://test/testFolder", false, true, false);
                temporaryNodes.addNodeRef(sourceSyncSet.getNodeRef());
                return sourceSyncSet;
            }
        });
        
        // Create some new children and these ones are hidden.
        final Set<NodeRef> hiddenChildren = new HashSet<NodeRef>();
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                NodeRef newNode = NODE_SERVICE.createNode(folderNode, ContentModel.ASSOC_CONTAINS, ContentModel.ASSOC_CONTAINS, ContentModel.TYPE_CONTENT).getChildRef();
                NODE_SERVICE.addAspect(newNode, ContentModel.ASPECT_HIDDEN, null);
                hiddenChildren.add(newNode);
                temporaryNodes.addNodeRef(newNode);
                return null;
            }
        });
        
        // Ensure that the sync set members are only the valid nodes from above.
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                List<NodeRef> members = SYNC_ADMIN_SERVICE.getMemberNodes(ssd);
                Set<NodeRef> memberSet = new HashSet<NodeRef>();
                memberSet.addAll(members);
                
                assertTrue("The synced folder should have been a member.", members.contains(folderNode));
                assertTrue("The normal children should all be members", members.containsAll(normalChildNodes));
                assertFalse("None of the hidden children should be members", org.apache.commons.collections.CollectionUtils.containsAny(members, hiddenChildren));
                return null;
            }
        });
    }
    
    @Test public void whenSysHiddenNodesDoGetAddedToSyncSetsTheyMustBeTransportedWithTheSysHiddenAspect() throws Exception
    {
        SyncChangeMonitor scm = APP_CONTEXT_INIT.getApplicationContext().getBean("syncChangeMonitor", SyncChangeMonitor.class);
        assertTrue(scm.getAspectsToTrack().contains(ContentModel.ASPECT_HIDDEN));
        assertTrue(scm.getPropertiesToTrack().contains(ContentModel.PROP_VISIBILITY_MASK));
    }
    
    /**
     * Create a sync set with the "lock on premise" option turned on.
     * 
     * Add a few nodes
     * 
     * Remove all nodes from sync set
     * 
     * @throws Exception
     */
    @Test public void lockSSD() throws Exception
    {
        // Create the SSD
        final List<NodeRef> syncMembers = Arrays.asList(new NodeRef[]{ testNodeU1_1, testNodeU1_2, testNodeU1_3});
        
        final String remoteTenantId = "remoteTenant";
        final String targetFolderNodeRef = "cloud://node/Ref";
        final boolean lockSourceCopy = true;
        
        final SyncSetDefinition newSSD = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<SyncSetDefinition>()
        {
            public SyncSetDefinition execute() throws Throwable
            {
                SyncSetDefinition ssd = SYNC_ADMIN_SERVICE.createSourceSyncSet(syncMembers, remoteTenantId, targetFolderNodeRef, lockSourceCopy, true, false);
                temporaryNodes.addNodeRef(ssd.getNodeRef());
                
                assertEquals(lockSourceCopy, ssd.getLockSourceCopy());
                assertNotNull(ssd);
                assertNotNull(ssd.getId());
                return ssd;
            }
        });
 
        final List<NodeRef> formerMembers = 
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<List<NodeRef>>()
        {
            public List<NodeRef> execute() throws Throwable
            {
                assertEquals(syncMembers, SYNC_ADMIN_SERVICE.getMemberNodes(newSSD));
                
                for(NodeRef nodeRef : syncMembers)
                {
                    assertTrue("node not locked", NODE_SERVICE.hasAspect(nodeRef, ContentModel.ASPECT_LOCKABLE));
                }
                return syncMembers;
            }
        });
        
        // cleanup callback
        final RetryingTransactionCallback<Void> cleanup = new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                LOCK_SERVICE.suspendLocks();
                for(NodeRef nodeRef : syncMembers)
                {
                    NODE_SERVICE.removeAspect(nodeRef, ContentModel.ASPECT_LOCKABLE);
                }
                return null;
            }
        };
        
        /**
         * Now remove the nodes from the sync set which will result in nodes
         * being unlocked.
         */
        try
        {
            TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
            {
                public Void execute() throws Throwable
                {
                    assertEquals(syncMembers, SYNC_ADMIN_SERVICE.getMemberNodes(newSSD));
                        
                    for(NodeRef nodeRef : formerMembers)
                    {
                            SYNC_ADMIN_SERVICE.removeSyncSetMember(newSSD, nodeRef);
                    }
                    
                    return null;
                }
            });
        }
        catch (Exception t)
        {
         // In the event of a failure clean up
            TRANSACTION_HELPER.doInTransaction(cleanup, false, true);
        
            throw t;
        }

        try
        {
            // Check lock has gone
            TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
            {
                public Void execute() throws Throwable
                {
                    for(NodeRef nodeRef : syncMembers)
                    {
                        assertFalse("node still locked", NODE_SERVICE.hasAspect(nodeRef, ContentModel.ASPECT_LOCKABLE));  
                        assertFalse("Node still has sync set member node", NODE_SERVICE.hasAspect(nodeRef, SyncModel.ASPECT_SYNC_SET_MEMBER_NODE));
                    }
                    return null;
                }
            });
        }
        catch (Exception t)
        {
         // In the event of a failure clean up
            TRANSACTION_HELPER.doInTransaction(cleanup, false, true);
        
            throw t;
        }
        
        
    }
    
    
    /**
     * Create a locked sync set, include a working copy in the sync set.
     * 
     * Move a node into a locked sync set.
     * 
     * Remove the sync set.
     * 
     * @throws Exception
     */
    @Test public void lockSSDWithCheckedOutNode() throws Exception
    {
        // Create the SSD
        // testNodeU1_1 is a normal node
        // testNodeU1_2 is a locked working copy node
        // testNodeU1_3 is a node moved into the sync set
        // testNodeU1_4 is a node added into a locked sync set
        
        final List<NodeRef> initialSyncMembers = Collections.unmodifiableList(Arrays.asList(new NodeRef[]{ testNodeU1_1, testNodeU1_2}));
        log.debug("Selecting initial sync members: " + initialSyncMembers);
        
        final String remoteTenantId = "remoteTenant";
        final String targetFolderNodeRef = "cloud://node/Ref";
        final boolean lockSourceCopy = true;
        
        // Check Out node U1_2
        final NodeRef workingCopyOfNodeU1_2 = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {
                log.debug("Checking out node " + testNodeU1_2);
                
                NodeRef wc = CHECK_OUT_CHECK_IN.checkout(testNodeU1_2);
                log.debug("Node " + wc + " is the Working Copy");
                return wc;
            }
        });
        temporaryNodes.addNodeRef(workingCopyOfNodeU1_2);
        
        final SyncSetDefinition newSSD = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<SyncSetDefinition>()
        {
            public SyncSetDefinition execute() throws Throwable
            {
                SyncSetDefinition ssd = SYNC_ADMIN_SERVICE.createSourceSyncSet(initialSyncMembers, remoteTenantId, targetFolderNodeRef, lockSourceCopy, true, false);
                log.debug("Created sync set");
                temporaryNodes.addNodeRef(ssd.getNodeRef());
                
                assertEquals(lockSourceCopy, ssd.getLockSourceCopy());
                assertNotNull(ssd);
                assertNotNull(ssd.getId());
                return ssd;
            }
        });
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                assertEquals(initialSyncMembers, SYNC_ADMIN_SERVICE.getMemberNodes(newSSD));
                
                for(NodeRef nodeRef : initialSyncMembers)
                {
                    assertTrue("node not locked", NODE_SERVICE.hasAspect(nodeRef, ContentModel.ASPECT_LOCKABLE));
                }
                return null;
            }
        });
        
        /**
         * Now remove the nodes from the sync set which will result in nodes
         * being unlocked.   Except for nodes locked prior to creation of the sync set.
         */
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                assertEquals(initialSyncMembers, SYNC_ADMIN_SERVICE.getMemberNodes(newSSD));
                    
                for(NodeRef nodeRef : initialSyncMembers)
                {
                        SYNC_ADMIN_SERVICE.removeSyncSetMember(newSSD, nodeRef);
                }
                
                return null;
            }
        });

        // Check lock has gone
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                for(NodeRef nodeRef : initialSyncMembers)
                {
                    if(nodeRef.equals(testNodeU1_2))
                    {
                        assertTrue("node unlocked by mistake", NODE_SERVICE.hasAspect(nodeRef, ContentModel.ASPECT_LOCKABLE));  
                        assertFalse("Node still has sync set member node", NODE_SERVICE.hasAspect(nodeRef, SyncModel.ASPECT_SYNC_SET_MEMBER_NODE));
                    }
                    else
                    {
                        assertFalse("node still locked", NODE_SERVICE.hasAspect(nodeRef, ContentModel.ASPECT_LOCKABLE));  
                        assertFalse("Node still has sync set member node", NODE_SERVICE.hasAspect(nodeRef, SyncModel.ASPECT_SYNC_SET_MEMBER_NODE));
                    }
                }
                return null;
            }
        });
    }

    /**
     * ALF-19122 - Adding a locked node to a sync set
     */
    @Test public void syncingAFolderAndThenSeparatelyAddingCheckedOutNode() throws Exception
    {
        final String folderName = SyncAdminServiceImplTest.class.getSimpleName() + "." + testName;
        
        // Create a folder that we'll sync below.
        final NodeRef folderNode = temporaryNodes.createNode(COMPANY_HOME, folderName, ContentModel.TYPE_FOLDER, AuthenticationUtil.getFullyAuthenticatedUser());
        
        // Put a number of immediate child nodes into that folder.
        final Set<NodeRef> normalChildNodes = new HashSet<NodeRef>();
        
        final int immediateChildCount = 15;
        for (int i = 0; i < immediateChildCount; i++)
        {
            // We don't need to add any content.
            normalChildNodes.add(temporaryNodes.createNode(folderNode, folderName + "tosync" + i, ContentModel.TYPE_CONTENT, AuthenticationUtil.getFullyAuthenticatedUser()));
        }
        
        // Now sync the folder.
        final SyncSetDefinition ssd = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<SyncSetDefinition>()
        {
            public SyncSetDefinition execute() throws Throwable
            {
                return SYNC_ADMIN_SERVICE.createSourceSyncSet(Arrays.asList(new NodeRef[] { folderNode }), "remoteTenant", "test://test/testFolder", false, true, false);
            }
        });
        
        // Create some new children and these ones are hidden.
        final Set<NodeRef> lockedChildren = new HashSet<NodeRef>();
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                NodeRef newNode = NODE_SERVICE.createNode(folderNode, ContentModel.ASSOC_CONTAINS, ContentModel.ASSOC_CONTAINS, ContentModel.TYPE_CONTENT).getChildRef();
                CHECK_OUT_CHECK_IN.checkout(newNode);
                lockedChildren.add(newNode);
                return null;
            }
        });
        
        // Ensure that the sync set members are only the valid nodes from above.
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                List<NodeRef> members = SYNC_ADMIN_SERVICE.getMemberNodes(ssd);
                Set<NodeRef> memberSet = new HashSet<NodeRef>();
                memberSet.addAll(members);
                
                assertTrue("The synced folder should have been a member.", members.contains(folderNode));
                assertTrue("The normal children should all be members", members.containsAll(normalChildNodes));
                assertTrue("All of the locked children should be members", org.apache.commons.collections.CollectionUtils.containsAny(members, lockedChildren));
                return null;
            }
        });
    }

    
    
}
