/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.alfresco.enterprise.repo.sync.SyncNodeException.SyncNodeExceptionType;
import org.alfresco.enterprise.repo.sync.audit.SyncAuditService;
import org.alfresco.enterprise.repo.sync.transport.CloudSyncMemberNodeTransport;
import org.alfresco.enterprise.repo.sync.transport.CloudSyncSetDefinitionTransport;
import org.alfresco.model.ContentModel;
import org.alfresco.model.RenditionModel;
import org.alfresco.repo.mode.ServerMode;
import org.alfresco.repo.mode.ServerModeProvider;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.node.MLPropertyInterceptor;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.MLText;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.alfresco.util.test.junitrules.AlfrescoPerson;
import org.alfresco.util.test.junitrules.ApplicationContextInit;
import org.alfresco.util.test.junitrules.RunAsFullyAuthenticatedRule;
import org.alfresco.util.test.junitrules.TemporaryMockOverride;
import org.alfresco.util.test.junitrules.TemporaryNodes;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestName;
import org.springframework.context.ApplicationContext;

/**
 * Unit Test Class for SyncTrackerComponentTest
 *
 * @author mrogers, janv
 * @since 4.1
 */
public class SyncTrackerComponentTest  extends BaseSyncServiceImplTest
{
    // JUnit Rules
    public static ApplicationContextInit APP_CONTEXT_INIT = new ApplicationContextInit();
    public static AlfrescoPerson         TEST_USER1 = new AlfrescoPerson(APP_CONTEXT_INIT, "UserOne");
    public static TemporaryMockOverride  MOCK_OVERRIDES = new TemporaryMockOverride();
    
    // Tie them together in a static Rule Chain
    @ClassRule public static RuleChain ruleChain = RuleChain.outerRule(APP_CONTEXT_INIT)
                                                            .around(TEST_USER1)
                                                            .around(MOCK_OVERRIDES);
    
    @Rule public RunAsFullyAuthenticatedRule runAsRule = new RunAsFullyAuthenticatedRule(TEST_USER1.getUsername());
    
    @Rule public TemporaryNodes temporaryNodes = new TemporaryNodes(APP_CONTEXT_INIT);
    
    @Rule public TestName testName = new TestName();
    
    private static final Log logger = LogFactory.getLog(SyncTrackerComponentTest.class);
    
    // Various services
    private static NodeService                     nodeService;
    private static RetryingTransactionHelper       retryingTransactionHelper;
    private static SyncAdminService                syncAdminService;
    private static SyncService                     syncService;
    private static SyncAuditService                syncAuditService;
    private static SyncTrackerComponent            syncTrackerComponent;
    private static FileFolderService               fileFolderService;
    private static CheckOutCheckInService          cociService;
    private static CloudSyncSetDefinitionTransport syncSsdTransport;
    private static CloudSyncMemberNodeTransport    syncSsmnTransport;
    private static SyncChangeMonitor               syncChangeMonitor;
    
    private static NodeRef COMPANY_HOME;
    private static String  FAKE_REPO_ID = "FakeRepoId";
    
    final String RUN_ID = ""+System.currentTimeMillis();
    
    @BeforeClass public static void initStaticData() throws Exception
    {
        final ApplicationContext appContext = APP_CONTEXT_INIT.getApplicationContext();
        
        nodeService               = appContext.getBean("NodeService", NodeService.class);
        retryingTransactionHelper = appContext.getBean("retryingTransactionHelper", RetryingTransactionHelper.class);
        syncAuditService          = appContext.getBean("syncAuditService", SyncAuditService.class);
        syncAdminService          = appContext.getBean("syncAdminService", SyncAdminService.class);
        syncService               = appContext.getBean("syncService", SyncService.class);
        syncTrackerComponent      = appContext.getBean("syncTrackerComponent", SyncTrackerComponent.class);
        fileFolderService         = appContext.getBean("FileFolderService", FileFolderService.class);
        cociService               = appContext.getBean("CheckOutCheckInService", CheckOutCheckInService.class);
        syncSsdTransport          = appContext.getBean("cloudSyncSetDefinitionTransport", CloudSyncSetDefinitionTransport.class);
        syncSsmnTransport         = appContext.getBean("cloudSyncMemberNodeTransport", CloudSyncMemberNodeTransport.class);
        syncChangeMonitor         = appContext.getBean("syncChangeMonitor", SyncChangeMonitor.class);
        
        ((SyncAdminServiceImpl)appContext.getBean("syncAdminService")).setCheckLicenseForSyncMode(false);
        
        Repository repositoryHelper = (Repository) appContext.getBean("repositoryHelper");
        COMPANY_HOME = repositoryHelper.getCompanyHome();
        
        // These overridden fields will be automatically reset by the @Rule.
        MOCK_OVERRIDES.setTemporaryField(syncAdminService, "cloudConnectorService", MOCK_CLOUD_CONNECTOR_SERVICE);
        MOCK_OVERRIDES.setTemporaryField(syncSsdTransport, "cloudConnectorService", MOCK_CLOUD_CONNECTOR_SERVICE);
    }
    
    private SyncTrackerComponent createSyncTrackerComponent(SyncTrackerTestFakeTransport fakeTestTransport)
    {
        return createSyncTrackerComponent(fakeTestTransport, 1, 1);
    }
    
    private SyncTrackerComponent createSyncTrackerComponent(SyncTrackerTestFakeTransport fakeTestTransport, int pushThreadCnt, int pullThreadCnt)
    {
        /**
         * Turn off the scheduled sync tracker component - would interfere with these tests
         */
        syncTrackerComponent.setEnabled(false);
        
        ServerModeProvider fakeServerModeProvider = new ServerModeProvider()
        {

			@Override
			public ServerMode getServerMode() {
				return ServerMode.PRODUCTION;
			}
        	
        };
        
        if(syncAdminService instanceof SyncAdminServiceImpl)
        {
        	SyncAdminServiceImpl syncAdminServiceImpl = (SyncAdminServiceImpl)syncAdminService;
        	syncAdminServiceImpl.setServerModeProvider(fakeServerModeProvider);
        }
        
        
        final SyncTrackerComponent c = new SyncTrackerComponent();
        
        c.setCloudSyncMemberNodeTransport(fakeTestTransport);
        c.setCloudSyncSetDefinitionTransport(fakeTestTransport);
        
        c.setJobLockService(syncTrackerComponent.getJobLockService());
        c.setNodeService(syncTrackerComponent.getNodeService());
        c.setCheckOutCheckInService(syncTrackerComponent.getCheckOutCheckInService());
        c.setRetryingTransactionHelper(syncTrackerComponent.getRetryingTransactionHelper());
        c.setSsmnChangeManagement(syncTrackerComponent.getSsmnChangeManagement());
        c.setSyncAdminService(syncTrackerComponent.getSyncAdminService());
        c.setSyncAuditService(syncTrackerComponent.getSyncAuditService());
        c.setSyncService(syncTrackerComponent.getSyncService());
        c.setTransactionService(syncTrackerComponent.getTransactionService());
        c.setPersonService(syncTrackerComponent.getPersonService());
        c.setLockService(syncTrackerComponent.getLockService());
        c.setAttributeService(syncTrackerComponent.getAttributeService());
        c.setPermissionService(syncTrackerComponent.getPermissionService());
        c.setBehaviourFilter(syncTrackerComponent.getBehaviourFilter());
        c.setPushThreadCnt(pushThreadCnt);
        c.setPullThreadCnt(pullThreadCnt);
        c.setServerModeProvider(fakeServerModeProvider);
        
        c.init();
        
        return c;
    }
    
    private void clearAudit()
    {
        /**
         * Clear up any junk left over from previous tests
         */
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                syncAuditService.clearAudit();
                return null;
            }
        });
    }
    
    private void checkAndGetNewTargetNodes(TestContextMulti testContext, int filesCnt, boolean folderSync)
    {
        checkAndGetNewTargetNodes(testContext, filesCnt, folderSync, 0, 0);
    }
    
    private void checkAndGetNewTargetNodes(final TestContextMulti testContext, int filesCnt, boolean folderSync, int folderCnt, int folderLevels)
    {
        // Validate nodes(s) have been pushed (initial sync and/or subsequent re-create)
        int expectedCnt = testContext.srcNodeRefs.size();
        
        RetryingTransactionCallback<List<NodeRef>> callback = new RetryingTransactionCallback<List<NodeRef>>()
        {
            @Override
            public List<NodeRef> execute() throws Throwable
            {
                return syncAdminService.getMemberNodes(testContext.targetSyncSet);
            }
        };
        List<NodeRef> targetNodes = retryingTransactionHelper.doInTransaction(callback);
        assertEquals(expectedCnt, targetNodes.size());
        
        for (NodeRef srcNodeRef : testContext.srcNodeRefs)
        {
            assertFalse(targetNodes.contains(srcNodeRef));
        }
        
        testContext.tgtNodeRefs.clear();
        testContext.tgtNodeRefs.addAll(targetNodes);
    }
    
    private void checkNotSynced(final List<NodeRef> nodeRefs)
    {
        checkNotSyncedAndOrDeleted(nodeRefs, false);
    }
    
    private void checkDeleted(final List<NodeRef> nodeRefs)
    {
        checkNotSyncedAndOrDeleted(nodeRefs, true);
    }
    
    private void checkNotSyncedAndOrDeleted(final List<NodeRef> nodeRefs, final boolean deleted)
    {
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                for (NodeRef nodeRef : nodeRefs)
                {
                    if (deleted)
                    {
                        assertFalse(nodeService.exists(nodeRef));
                    }
                    else
                    {
                        Set<QName> nodeAspects = nodeService.getAspects(nodeRef);
                        assertFalse(nodeAspects.contains(SyncModel.ASPECT_SYNC_SET_MEMBER_NODE));
                        assertFalse(nodeAspects.contains(SyncModel.ASPECT_SYNCED));
                        assertFalse(nodeAspects.contains(SyncModel.ASPECT_SYNC_FAILED));
                        
                        assertEquals(0, nodeService.getSourceAssocs(nodeRef, SyncModel.ASSOC_SYNC_MEMBERS).size());
                    }
                }
                return null;
            }
        });
    }
    
    private void checkSynced(final List<NodeRef> nodeRefs)
    {
        checkSyncedOrSyncPending(nodeRefs, false);
    }
    
    private void checkSyncPending(final List<NodeRef> nodeRefs)
    {
        checkSyncedOrSyncPending(nodeRefs, true);
    }
    
    private void checkSyncedOrSyncPending(final List<NodeRef> nodeRefs, final boolean pendingSync)
    {
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                for (NodeRef nodeRef : nodeRefs)
                {
                    assertTrue(nodeService.exists(nodeRef));
                    Set<QName> nodeAspects = nodeService.getAspects(nodeRef);
                    assertTrue(nodeAspects.contains(SyncModel.ASPECT_SYNC_SET_MEMBER_NODE));
                    
                    if (pendingSync)
                    {
                        assertNull(nodeService.getProperty(nodeRef, SyncModel.PROP_SYNC_TIME));
                    }
                    else
                    {
                        assertNotNull(nodeService.getProperty(nodeRef, SyncModel.PROP_SYNC_TIME));
                    }
                    
                    assertFalse(nodeAspects.contains(SyncModel.ASPECT_SYNC_FAILED));
                    
                    assertEquals(1, nodeService.getSourceAssocs(nodeRef, SyncModel.ASSOC_SYNC_MEMBERS).size());
                }
                return null;
            }
        });
    }
    
    private void checkSyncFailed(final List<NodeRef> nodeRefs, final SyncNodeExceptionType seType)
    {
        checkSyncFailed(nodeRefs, seType, null);
    }
    
    private void checkSyncFailed(final List<NodeRef> nodeRefs, final SyncNodeExceptionType seType, final String errorDetailsStartsWith)
    {
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                for (NodeRef nodeRef : nodeRefs)
                {
                    Set<QName> nodeAspects = nodeService.getAspects(nodeRef);
                    assertTrue("Sync should have failed: "+nodeRef, nodeAspects.contains(SyncModel.ASPECT_SYNC_FAILED));
                    
                    String seTypeId = (String)nodeService.getProperty(nodeRef, SyncModel.PROP_SYNCED_FAILED_CODE);
                    assertEquals("Expected="+seTypeId+", Actual="+seType.getMessageId(), seType.getMessageId(), seTypeId);
                    
                    if (errorDetailsStartsWith != null)
                    {
                        String seDetails = (String)nodeService.getProperty(nodeRef, SyncModel.PROP_SYNCED_FAILED_DETAILS);
                        assertTrue((seDetails != null) && (seDetails.startsWith(errorDetailsStartsWith)));
                    }
                }
                return null;
            }
        });
    }
    
    private void push(final SyncTrackerComponent c)
    {
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                c.push(); 
                return null;
            }
        });
    }
    
    private void pull(final SyncTrackerComponent c)
    {
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                c.pull(); 
                return null;
            }
        });
    }
    
    /**
     * Test push
     * 
     * 0. push nothing
     * 
     * 1. Push a single node
     * 
     * 2. Push an update
     * 
     * 3. Push a create and update together
     * 
     * @throws Exception
     */
    @Test public void testPush() throws Exception
    {
        final String FAKE_TENNANT_ID="fakeTennantId";
        
        final TestContext testContext = new TestContext();
        
        /**
         * Create a new SyncTrackerComponent, with defaults from the spring instantiated one.
         * 
         * Plug in the test sync set member node transport 
         * Plug in the test sync set definition transport 
         */
        final SyncTrackerTestFakeTransport fakeTestTransport = new SyncTrackerTestFakeTransport(FAKE_REPO_ID, 
                syncService, 
                syncAdminService, 
                syncAuditService,
                syncSsmnTransport); 
        
        final SyncTrackerComponent c = createSyncTrackerComponent(fakeTestTransport);
        
        clearAudit();
        
        /**
         * Test 0: Push nothing
         */
        push(c);
        
        /**
         * Test 1: Push a single node in a single sync set
         */
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                NodeRef testHome = temporaryNodes.createNode(COMPANY_HOME, testName.getMethodName()+"-"+RUN_ID, ContentModel.TYPE_FOLDER, AuthenticationUtil.getFullyAuthenticatedUser());
                
                // create a node with a title and some content
                String sourceFolder = "Push.SyncSetA.source";
                String targetFolder = "Push.SyncSetA.target";
                String fileNameA = "fileA";
                String fileNameB = "fileB";
                
                NodeRef s = fileFolderService.searchSimple(testHome, sourceFolder);
                if( s == null )
                {
                    FileInfo f = fileFolderService.create(testHome, sourceFolder, ContentModel.TYPE_FOLDER);
                    s = f.getNodeRef();
                    temporaryNodes.addNodeRef(s);
                }
                assertNotNull("test error: sourceFolder is null", s);
                NodeRef t = fileFolderService.searchSimple(testHome, targetFolder);
                if( t == null )
                {
                    FileInfo f = fileFolderService.create(testHome, targetFolder, ContentModel.TYPE_FOLDER);
                    t = f.getNodeRef();
                    temporaryNodes.addNodeRef(t);
                }
                assertNotNull("test error: targetFolder is null", t);
                testContext.targetFolderNodeRef=t;
                
                FileInfo f = fileFolderService.create(s, fileNameA, ContentModel.TYPE_CONTENT);
                testContext.fileANodeRef = f.getNodeRef();
                temporaryNodes.addNodeRef(testContext.fileANodeRef);
                
                f = fileFolderService.create(s, fileNameB, ContentModel.TYPE_CONTENT);
                testContext.fileBNodeRef = f.getNodeRef();
                temporaryNodes.addNodeRef(testContext.fileBNodeRef);
                               
                return null;
            }
        });
        
        /**
         * Test 1: Push a single node (FileA) in a single sync set
         */
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {  
                List<NodeRef> syncSetMembers = new ArrayList<NodeRef>();
                syncSetMembers.add(testContext.fileANodeRef);
                testContext.sourceSyncSet = syncAdminService.createSourceSyncSet(syncSetMembers, FAKE_TENNANT_ID, testContext.targetFolderNodeRef.toString(), false, false, true, false);
                
                assertNotNull("sourceSyncSet is null", testContext.sourceSyncSet);
                String sourceSyncSetId = testContext.sourceSyncSet.getId();
                
                final String targetssdId = GUID.generate();
                testContext.targetSyncSet = syncAdminService.createTargetSyncSet(targetssdId, FAKE_REPO_ID, testContext.targetFolderNodeRef, false, true, false);
                assertNotNull("targetSyncSet is null", testContext.targetSyncSet);
                
                // Magic : Wire up the target sync set to the source sync set
                fakeTestTransport.syncSetMap.put(sourceSyncSetId, testContext.targetSyncSet);
                
                return null;
            }
        });
        
        // Should push a single node
        push(c);
        
        /**
         * Test 1:
         * Validate that the new node has been created
         */
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                // Target nodes should contain a single node that we have pushed above
                List<NodeRef> targetNodes = syncAdminService.getMemberNodes(testContext.targetSyncSet);
                assertNotNull("Target nodes is null");
                assertEquals("Target nodes not one", 1, targetNodes.size());
                                      
                return null;
            }
        });
        
        /** 
         * Test 2: Push a single update  - do the push
         */
        final String NODE_A_TITLE="Update by unit test";
        final String NODE_A_DESC_EN="In English";
        final String NODE_A_DESC_FR="En Fran\u00e7ais";
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                // Set a single value for the title
                nodeService.setProperty(testContext.fileANodeRef, ContentModel.PROP_TITLE, NODE_A_TITLE);                         

                // Set a full ml set of values for the description
                MLText desc = new MLText();
                desc.addValue(Locale.FRENCH, NODE_A_DESC_FR);
                desc.addValue(Locale.ENGLISH, NODE_A_DESC_EN);
                nodeService.setProperty(testContext.fileANodeRef, ContentModel.PROP_DESCRIPTION, desc);
                
                return null;
            }
        });
        
        // push the update
        push(c);
        
        /** 
         * Test 2: Push a single update - validate 
         */
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                // Target nodes should contain a single node that we have pushed above
                List<NodeRef> targetNodes = syncAdminService.getMemberNodes(testContext.targetSyncSet);
                assertNotNull("Target nodes is null");
                assertEquals("Target nodes not one", 1, targetNodes.size());
                
                NodeRef destNode = targetNodes.get(0);
                
                // Check the single title came through fine
                assertEquals("title is wrong", NODE_A_TITLE, nodeService.getProperty(destNode, ContentModel.PROP_TITLE));
                
                // Check the description comes as expected in English
                assertEquals("description in english is wrong", NODE_A_DESC_EN, nodeService.getProperty(destNode, ContentModel.PROP_DESCRIPTION));
                
                // Fetch the full description, and check the multi-lingual part was correctly transferred
                MLPropertyInterceptor.setMLAware(true);
                Object desc;
                try
                {
                    desc = nodeService.getProperty(destNode, ContentModel.PROP_DESCRIPTION);
                }
                finally
                {
                    MLPropertyInterceptor.setMLAware(false);
                }
                
                assertEquals("description is wrong", MLText.class, desc.getClass());
                assertEquals("description is wrong in English", NODE_A_DESC_EN, ((MLText)desc).getValue(Locale.ENGLISH));
                assertEquals("description is wrong in French",  NODE_A_DESC_FR, ((MLText)desc).getValue(Locale.FRENCH));
                                              
                return null;
            }
        });
        
        /**
         * Test 3: Push an update and a create (same sync set) - validate 
         */
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {  
                nodeService.setProperty(testContext.fileANodeRef, ContentModel.PROP_DESCRIPTION, "Whazoo!");                         
                syncAdminService.addSyncSetMember(testContext.sourceSyncSet, testContext.fileBNodeRef);
                return null;
            }
        });
        
        // push the update
        push(c);
        
        /**
         * Test 3: Push an update and a create (same sync set) - validate 
         */
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                // Target nodes should contain a single node that we have pushed above
                List<NodeRef> targetNodes = syncAdminService.getMemberNodes(testContext.targetSyncSet);
                assertNotNull("Target nodes is null");
                assertEquals("Target nodes not two", 2, targetNodes.size());                                                   
                return null;
            }
        });
        
    } // test Push
    
    /**
     * Test Pull
     * 
     * Push a node
     * 
     * 0: Pull no change
     * 
     * Update the target node
     * 
     * 1: Pull the change
     * 
     * @throws Exception
     */
    @Test public void testPull() throws Exception
    {
        final String FAKE_REPO_ID="fakeRepoIdId";
        
        final TestContext testContext = new TestContext();
        
        /**
         * Create a new SyncTrackerComponent, with defaults from the spring instantiated one.
         * 
         * Plug in the test sync set member node transport 
         * Plug in the test sync set definition transport 
         */
        final SyncTrackerTestFakeTransport fakeTestTransport = new SyncTrackerTestFakeTransport(FAKE_REPO_ID, 
                syncService, 
                syncAdminService, 
                syncAuditService,
                syncSsmnTransport); 
        
        final SyncTrackerComponent c = createSyncTrackerComponent(fakeTestTransport);
        
        clearAudit();
        
        /**
         * Test 0: Pull nothing
         */
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {    
                c.pull();
                return null;
            }
        });
        
        /**
         * Test Setup Push a single node in a single sync set
         */
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                NodeRef testHome = temporaryNodes.createNode(COMPANY_HOME, testName.getMethodName()+"-"+RUN_ID, ContentModel.TYPE_FOLDER, AuthenticationUtil.getFullyAuthenticatedUser());
                
                // create a node with a title and some content
                String sourceFolder = "Pull.SyncSetA.source";
                String targetFolder = "Pull.SyncSetA.target";
                String fileNameA = "fileA";
                String fileNameB = "fileB";
                
                NodeRef s = fileFolderService.searchSimple(testHome, sourceFolder);
                if( s == null )
                {
                    FileInfo f = fileFolderService.create(testHome, sourceFolder, ContentModel.TYPE_FOLDER);
                    s = f.getNodeRef();
                }
                assertNotNull("test error: sourceFolder is null", s);
                NodeRef t = fileFolderService.searchSimple(testHome, targetFolder);
                if( t == null )
                {
                    FileInfo f = fileFolderService.create(testHome, targetFolder, ContentModel.TYPE_FOLDER);
                    t = f.getNodeRef();
                }
                assertNotNull("test error: targetFolder is null", t);
                testContext.targetFolderNodeRef=t;
                
                FileInfo f = fileFolderService.create(s, fileNameA, ContentModel.TYPE_CONTENT);
                testContext.fileANodeRef = f.getNodeRef();
                
                f = fileFolderService.create(s, fileNameB, ContentModel.TYPE_CONTENT);
                testContext.fileBNodeRef = f.getNodeRef();
                               
                return null;
            }
        });
        
        /**
         * Test Setup : Push a single node (FileA) in a single sync set
         */
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {  
                List<NodeRef> syncSetMembers = new ArrayList<NodeRef>();
                syncSetMembers.add(testContext.fileANodeRef);
                testContext.sourceSyncSet = syncAdminService.createSourceSyncSet(syncSetMembers, "fake tennant id", testContext.targetFolderNodeRef.toString(), false, false, true, false);
                
                assertNotNull("sourceSyncSet is null", testContext.sourceSyncSet);
                String sourceSyncSetId = testContext.sourceSyncSet.getId();
                
                final String targetssdId = GUID.generate();
                testContext.targetSyncSet = syncAdminService.createTargetSyncSet(targetssdId, FAKE_REPO_ID, testContext.targetFolderNodeRef, false, true, false);
                assertNotNull("targetSyncSet is null", testContext.targetSyncSet);
                
                // Magic : Wire up the target sync set to the source sync set
                fakeTestTransport.syncSetMap.put(sourceSyncSetId, testContext.targetSyncSet);
                
                return null;
            }
        });
        
        // Should push a single node
        push(c);
        
        /**
         * Test 0: Pull nothing
         */
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {    
                c.pull();
                return null;
            }
        });
        
        final String UPDATE_DESCRIPTION = "Update from cloud";
        /**
         * Test 1: Update a target node
         */
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                // Target nodes should contain a single node that we have pushed above
                List<NodeRef> targetNodes = syncAdminService.getMemberNodes(testContext.targetSyncSet);
                assertNotNull("Target nodes is null");
                assertEquals("Target nodes not one", 1, targetNodes.size());
                
                NodeRef targetNode = targetNodes.get(0);
                nodeService.setProperty(targetNode, ContentModel.PROP_DESCRIPTION, UPDATE_DESCRIPTION);
                return null;
            }
        });
        
        /**
         * Test 1: Pull a change
         */
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {    
                c.pull();
                return null;
            }
        });
        
        /**
         * Test 1: Validate the source node has been updated
         */
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                assertEquals("description is wrong", UPDATE_DESCRIPTION, nodeService.getProperty(testContext.fileANodeRef, ContentModel.PROP_DESCRIPTION));
                
                return null;
            }
        });
       
    } // test Pull
    
    /**
     * Test Simple Conflict
     * 
     * Push a node
     * 
     * Update the target node
     * Update the source node
     * 
     * 1: push the change
     * 2: Pull the change
     * 
     * @throws Exception
     */
    @Test public void testConflict() throws Exception
    {
        final String FAKE_REPO_ID="fakeRepoIdId";
        
        final TestContext testContext = new TestContext();
        
        /**
         * Create a new SyncTrackerComponent, with defaults from the spring instantiated one.
         * 
         * Plug in the test sync set member node transport 
         * Plug in the test sync set definition transport 
         */
        final SyncTrackerTestFakeTransport fakeTestTransport = new SyncTrackerTestFakeTransport(FAKE_REPO_ID, 
                syncService, 
                syncAdminService, 
                syncAuditService,
                syncSsmnTransport); 
        
        final SyncTrackerComponent c = createSyncTrackerComponent(fakeTestTransport);
        
        clearAudit();
        
        /**
         * Test Setup Push a single node in a single sync set
         */
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                NodeRef testHome = temporaryNodes.createNode(COMPANY_HOME, testName.getMethodName()+"-"+RUN_ID, ContentModel.TYPE_FOLDER, AuthenticationUtil.getFullyAuthenticatedUser());
                
                // create a node with a title and some content
                String sourceFolder = "Conflict.SyncSetA.source";
                String targetFolder = "Conflict.SyncSetA.target";
                String fileNameA = "fileA";
                String fileNameB = "fileB";
                
                NodeRef s = fileFolderService.searchSimple(testHome, sourceFolder);
                if( s == null )
                {
                    FileInfo f = fileFolderService.create(testHome, sourceFolder, ContentModel.TYPE_FOLDER);
                    s = f.getNodeRef();
                }
                assertNotNull("test error: sourceFolder is null", s);
                NodeRef t = fileFolderService.searchSimple(testHome, targetFolder);
                if( t == null )
                {
                    FileInfo f = fileFolderService.create(testHome, targetFolder, ContentModel.TYPE_FOLDER);
                    t = f.getNodeRef();
                }
                assertNotNull("test error: targetFolder is null", t);
                testContext.targetFolderNodeRef=t;
                
                FileInfo f = fileFolderService.create(s, fileNameA, ContentModel.TYPE_CONTENT);
                testContext.fileANodeRef = f.getNodeRef();
                
                f = fileFolderService.create(s, fileNameB, ContentModel.TYPE_CONTENT);
                testContext.fileBNodeRef = f.getNodeRef();
                
                return null;
            }
        });
        
        /**
         * Test Setup : Push a single node (FileA) in a single sync set
         */
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {  
                List<NodeRef> syncSetMembers = new ArrayList<NodeRef>();
                syncSetMembers.add(testContext.fileANodeRef);
                testContext.sourceSyncSet = syncAdminService.createSourceSyncSet(syncSetMembers, "fake tennant id", testContext.targetFolderNodeRef.toString(), false, false, true, false);
                
                assertNotNull("sourceSyncSet is null", testContext.sourceSyncSet);
                String sourceSyncSetId = testContext.sourceSyncSet.getId();
                
                final String targetssdId = GUID.generate();
                testContext.targetSyncSet = syncAdminService.createTargetSyncSet(targetssdId, FAKE_REPO_ID, testContext.targetFolderNodeRef, false, true, false);
                assertNotNull("targetSyncSet is null", testContext.targetSyncSet);
                
                // Magic : Wire up the target sync set to the source sync set
                fakeTestTransport.syncSetMap.put(sourceSyncSetId, testContext.targetSyncSet);
                
                return null;
            }
        });
        
        // Should push a single node
        push(c);
        
        
        final String UPDATE_DESCRIPTION="Description From Cloud";
        /**
         * Update the source and target nodes causing a conflict
         */
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                // Target nodes should contain a single node that we have pushed above
                List<NodeRef> targetNodes = syncAdminService.getMemberNodes(testContext.targetSyncSet);
                assertNotNull("Target nodes is null");
                assertEquals("Target nodes not one", 1, targetNodes.size());
                
                NodeRef targetNode = targetNodes.get(0);
                nodeService.setProperty(targetNode, ContentModel.PROP_DESCRIPTION, UPDATE_DESCRIPTION);
                nodeService.setProperty(testContext.fileANodeRef, ContentModel.PROP_DESCRIPTION, "looser");
                return null;
            }
        });
        
        
        /**
         * Test 1: Pull and push change
         */
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {   
                c.push();
                c.pull();
                return null;
            }
        });
        
        /**
         * Test 1: Validate the source node has been updated
         */
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                assertEquals("description is wrong", UPDATE_DESCRIPTION, nodeService.getProperty(testContext.fileANodeRef, ContentModel.PROP_DESCRIPTION));
                
                return null;
            }
        });
       
    } // testConflict
    
    /**
     * Simple file sync (of metadata => description) ... sync, push update, pull update, unsync
     *
     * author janv
     */
    @Test public void testFileSync_SimplePushPullUnsync_Single() throws Exception
    {
        final int FILES_PER_SSD_CNT = 1;
        final int SSD_CNT = 1;
        
        runSimplePushPullUnsync(SSD_CNT, FILES_PER_SSD_CNT, false);
    }
    
    @Test public void testFileSync_SimplePushPullUnsync_Multiple() throws Exception
    {
        final int FILES_PER_SSD_CNT = 5;
        final int SSD_CNT = 10;
        
        runSimplePushPullUnsync(SSD_CNT, FILES_PER_SSD_CNT, false);
    }
    
    /**
     * Setup either file sync (eg. one or more files) or a simple one level folder sync (eg. with one or more files)
     * 
     * @param ssdCnt     number of SSDs
     * @param filesCnt   number of files
     * @param folderSync true if folder sync, else file sync
     * @return
     */
    private List<TestContextMulti> setupSimpleSyncs(final int ssdCnt, final int filesCnt, final boolean folderSync)
    {
        // for simple folder sync: default to 2 levels - where level 0 is the root (top-level) parent itself
        return setupSimpleSyncs(ssdCnt, filesCnt, folderSync, 0, 1);
    }
    
    /**
     * Setup folder hierarchy - eg. one or more levels
     * 
     * @param ssdCnt        number of SSDs
     * @param filesCnt      number of files per folder
     * @param foldersCnt    number of folders per folder (ie. per level)
     * @param folderLevels  number of levels - where the root folder is level 1
     * @return
     */
    private List<TestContextMulti> setupFolderSyncHierarchy(int ssdCnt, int filesCnt, int foldersCnt, int folderLevels)
    {
        return setupSimpleSyncs(ssdCnt, filesCnt, true, foldersCnt, folderLevels);
    }
    
    
    private void createFoldersFiles(TestContextMulti testContext, NodeRef parentNodeRef, int fileCnt, int folderCnt, int levels)
    {
        if (levels == 0)
        {
            return;
        }
        createFoldersFiles(testContext, parentNodeRef, fileCnt, folderCnt, levels, 2);
    }
    private void createFoldersFiles(TestContextMulti testContext, NodeRef parentNodeRef, int fileCnt, int folderCnt, int levels, int currentLevel)
    {
        // create folders at this level (if folderCnt > 0)
        int nextLevel = currentLevel + 1;
        for (int i = 1; i <= folderCnt; i++)
        {
            NodeRef folderRef = fileFolderService.create(parentNodeRef, "folder-"+i, ContentModel.TYPE_FOLDER).getNodeRef();
            testContext.srcNodeRefs.add(folderRef);
            
            if (currentLevel != levels)
            {
                // create next level
                createFoldersFiles(testContext, folderRef, fileCnt, folderCnt, levels, nextLevel);
            }
        }
        
        // create files at this level (if fileCnt > 0)
        for (int j = 1; j <= fileCnt; j++)
        {
            NodeRef fileRef =  fileFolderService.create(parentNodeRef, "file-"+j, ContentModel.TYPE_CONTENT).getNodeRef();
            testContext.srcNodeRefs.add(fileRef);
        }
    }
    
    // note: FILES_CNT  = files per SSD (in case of file sync) or files per folder (in case of folder sync - one level)
    private List<TestContextMulti> setupSimpleSyncs(final int SSD_CNT, 
                                                    final int FILES_CNT,
                                                    final boolean folderSync,
                                                    final int FOLDERS_CNT,
                                                    final int folderSyncLevels)
    {
        final int PUSH_THREAD_CNT = 3;
        final int PULL_THREAD_CNT = 3;
        
        /**
         * Create a new SyncTrackerComponent, with defaults from the spring instantiated one.
         * 
         * Plug in the test sync set member node transport 
         * Plug in the test sync set definition transport 
         */
        final SyncTrackerTestFakeTransport fakeTestTransport = new SyncTrackerTestFakeTransport(FAKE_REPO_ID, 
                syncService, 
                syncAdminService, 
                syncAuditService,
                syncSsmnTransport); 
        
        final SyncTrackerComponent c = createSyncTrackerComponent(fakeTestTransport, PUSH_THREAD_CNT, PULL_THREAD_CNT);
        
        final String FAKE_TENANT_ID = "fakeTennantId";
        
        final List<TestContextMulti> testContexts = new ArrayList<TestContextMulti>(SSD_CNT);
        for (int i = 0; i < SSD_CNT; i++)
        {
            TestContextMulti testContext = new TestContextMulti();
            testContext.c = c;
            testContexts.add(testContext);
        }
        
        clearAudit();
        
        // push/pull nothing
        push(c);
        pull(c);
        
        // create test file(s)
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                NodeRef testHome = temporaryNodes.createNode(COMPANY_HOME, testName.getMethodName()+"-"+RUN_ID, ContentModel.TYPE_FOLDER, AuthenticationUtil.getFullyAuthenticatedUser());
                
                for (int i = 0; i < SSD_CNT; i++)
                {
                    TestContextMulti testContext = testContexts.get(i);
                    
                    NodeRef tgt = temporaryNodes.createNode(testHome, "Push.SyncSet.target-"+i, ContentModel.TYPE_FOLDER, AuthenticationUtil.getFullyAuthenticatedUser());
                    testContext.tgtFolderNodeRef=tgt;
                    
                    NodeRef src = temporaryNodes.createNode(testHome, "Push.SyncSet.source-"+i, ContentModel.TYPE_FOLDER, AuthenticationUtil.getFullyAuthenticatedUser());
                    testContext.srcFolderNodeRef=src;
                    
                    if (folderSync)
                    {
                        testContext.srcNodeRefs.add(testContext.srcFolderNodeRef);
                        createFoldersFiles(testContext, testContext.srcFolderNodeRef, FILES_CNT, FOLDERS_CNT, folderSyncLevels);
                    }
                    else
                    {
                        createFoldersFiles(testContext, testContext.srcFolderNodeRef, FILES_CNT, 0, 1);
                    }
                    
                    testContext.testHome = testHome;
                }
                
                return null;
            }
        });
        
        for (int i = 0; i < SSD_CNT; i++)
        {
            checkNotSynced(testContexts.get(i).srcNodeRefs);
        }
        
        // create sync(s) - either file syncs or a folder sync (with files as immediate children)
        for (int i = 0; i < SSD_CNT; i++)
        {
            final TestContextMulti testContext = testContexts.get(i);
            
            retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
            {
                public Void execute() throws Throwable
                {
                    List<NodeRef> syncSetMembers = new ArrayList<NodeRef>();
                    
                    if (folderSync)
                    {
                        syncSetMembers.add(testContext.srcFolderNodeRef);
                    }
                    else
                    {
                        syncSetMembers.addAll(testContext.srcNodeRefs);
                    }
                    
                    boolean includeSubFolders =  (folderSync && (folderSyncLevels > 1));
                    testContext.sourceSyncSet = syncAdminService.createSourceSyncSet(syncSetMembers, FAKE_TENANT_ID, testContext.tgtFolderNodeRef.toString(), false, includeSubFolders, true, false);
                    
                    assertNotNull("sourceSyncSet is null", testContext.sourceSyncSet);
                    String sourceSyncSetId = testContext.sourceSyncSet.getId();
                    
                    final String targetssdId = GUID.generate();
                    testContext.targetSyncSet = syncAdminService.createTargetSyncSet(targetssdId, FAKE_REPO_ID, testContext.tgtFolderNodeRef, false, true, false);
                    assertNotNull("targetSyncSet is null", testContext.targetSyncSet);
                    
                    // Magic : Wire up the target sync set to the source sync set
                    fakeTestTransport.syncSetMap.put(sourceSyncSetId, testContext.targetSyncSet);
                    
                    return null;
                }
            });
        }
        
        for (int i = 0; i < SSD_CNT; i++)
        {
            final TestContextMulti testContext = testContexts.get(i);
            
            // validate that the new node(s) have not yet been created on the target
            retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
            {
                public Void execute() throws Throwable
                {
                    List<NodeRef> targetNodes = syncAdminService.getMemberNodes(testContext.targetSyncSet);
                    assertEquals(0, targetNodes.size());
                    return null;
                }
            });
        }
        
        for (int i = 0; i < SSD_CNT; i++)
        {
            final TestContextMulti testContext = testContexts.get(i);
            
            // check sync time before initial sync
            for (NodeRef srcNodeRef : testContext.srcNodeRefs)
            {
                assertNull(nodeService.getProperty(srcNodeRef, SyncModel.PROP_SYNC_TIME));
            }
            
            checkSyncPending(testContext.srcNodeRefs);
        }
        
        return testContexts;
    }
    
    private void runSimplePushPullUnsync(final int SSD_CNT, final int FILES_CNT, final boolean folderSync) throws Exception
    {
        final List<TestContextMulti> testContexts = setupSimpleSyncs(SSD_CNT, FILES_CNT, folderSync);
        SyncTrackerComponent c = testContexts.get(0).c;
        
        push(c);
        
        for (int i = 0; i < SSD_CNT; i++)
        {
            final TestContextMulti testContext = testContexts.get(i);
            
            checkAndGetNewTargetNodes(testContext, FILES_CNT, folderSync);
            
            checkSynced(testContext.srcNodeRefs);
        }
        
        checkPushPull(SSD_CNT, FILES_CNT, folderSync, testContexts, c);
        
        for (int i = 0; i < SSD_CNT; i++)
        {
            final TestContextMulti testContext = testContexts.get(i);
            
            // unsync (src) - without delete of target
            retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
            {
                public Void execute() throws Throwable
                {
                    if (folderSync)
                    {
                        syncAdminService.removeSyncSetMember(testContext.sourceSyncSet, testContext.srcFolderNodeRef, false);
                    }
                    else
                    {
                        for (NodeRef srcNodeRef : testContext.srcNodeRefs)
                        {
                            syncAdminService.removeSyncSetMember(testContext.sourceSyncSet, srcNodeRef, false);
                        }
                    }
                    return null;
                }
            });
        }
        
        for (int i = 0; i < SSD_CNT; i++)
        {
            checkNotSynced(testContexts.get(i).srcNodeRefs);
        }
        
        push(c);
        
        for (int i = 0; i < SSD_CNT; i++)
        {
            final TestContextMulti testContext = testContexts.get(i);
            
            RetryingTransactionCallback<List<NodeRef>> getMemberNodesSrc = new RetryingTransactionCallback<List<NodeRef>>()
            {
                @Override
                public List<NodeRef> execute() throws Throwable
                {
                    return syncAdminService.getMemberNodes(testContext.sourceSyncSet);
                }
            };
            List<NodeRef> srcNodes = retryingTransactionHelper.doInTransaction(getMemberNodesSrc);
            assertEquals(0, srcNodes.size());
            
            RetryingTransactionCallback<List<NodeRef>> getMemberNodesTgt = new RetryingTransactionCallback<List<NodeRef>>()
            {
                @Override
                public List<NodeRef> execute() throws Throwable
                {
                    return syncAdminService.getMemberNodes(testContext.targetSyncSet);
                }
            };
            List<NodeRef> tgtNodes = retryingTransactionHelper.doInTransaction(getMemberNodesTgt);
            assertEquals(0, tgtNodes.size());
            
            for (NodeRef srcNodeRef : testContext.srcNodeRefs)
            {
                assertTrue(nodeService.exists(srcNodeRef));
            }
            
            for (NodeRef tgtNodeRef : testContext.tgtNodeRefs)
            {
                assertTrue(nodeService.exists(tgtNodeRef));
            }
        }
        
        for (int i = 0; i < SSD_CNT; i++)
        {
            checkNotSynced(testContexts.get(i).tgtNodeRefs);
        }
    }
    
    private void checkPushPull(final int SSD_CNT, final int FILES_CNT, final boolean folderSync, final List<TestContextMulti> testContexts,  final SyncTrackerComponent c)
    {
        final String srcDescription = "Whazoo! - "+System.currentTimeMillis();
        
        for (int i = 0; i < SSD_CNT; i++)
        {
            final TestContextMulti testContext = testContexts.get(i);
            
            // update src nodes and then push to tgt
            for (NodeRef srcNodeRef : testContext.srcNodeRefs)
            {
                String name = (String)nodeService.getProperty(srcNodeRef, ContentModel.PROP_NAME);
                nodeService.setProperty(srcNodeRef, ContentModel.PROP_DESCRIPTION, srcDescription+"-"+name);
            }
        }
        
        push(c);
        
        for (int i = 0; i < SSD_CNT; i++)
        {
            final TestContextMulti testContext = testContexts.get(i);
            
            // validate push
            int expectedCnt = FILES_CNT + (folderSync ? 1 : 0);
            
            // Target nodes should contain node(s) that we have pushed above
            RetryingTransactionCallback<List<NodeRef>> callback = new RetryingTransactionCallback<List<NodeRef>>()
            {
                @Override
                public List<NodeRef> execute() throws Throwable
                {
                    return syncAdminService.getMemberNodes(testContext.targetSyncSet);
                }
            };
            List<NodeRef> targetNodes = retryingTransactionHelper.doInTransaction(callback);
            assertEquals(expectedCnt, targetNodes.size());
            
            for (NodeRef tgtNodeRef : targetNodes)
            {
                String tgtDescription = (String)nodeService.getProperty(tgtNodeRef, ContentModel.PROP_DESCRIPTION);
                String name = (String)nodeService.getProperty(tgtNodeRef, ContentModel.PROP_NAME);
                assertEquals("Wrong description", srcDescription+"-"+name, tgtDescription);
            }
        }
        
        final String tgtDescription2 = "Not Wazoo! - "+System.currentTimeMillis();
        
        for (int i = 0; i < SSD_CNT; i++)
        {
            final TestContextMulti testContext = testContexts.get(i);
            
            // update tgt and then pull to src
            for (NodeRef tgtNodeRef : testContext.tgtNodeRefs)
            {
                String name = (String)nodeService.getProperty(tgtNodeRef, ContentModel.PROP_NAME);
                nodeService.setProperty(tgtNodeRef, ContentModel.PROP_DESCRIPTION, tgtDescription2+"-"+name);
            }
        }
        
        pull(c);
        
        for (int i = 0; i < SSD_CNT; i++)
        {
            final TestContextMulti testContext = testContexts.get(i);
            
            // validate pull
            for (NodeRef srcNodeRef : testContext.srcNodeRefs)
            {
                String srcDescription2 = (String)nodeService.getProperty(srcNodeRef, ContentModel.PROP_DESCRIPTION);
                String name = (String)nodeService.getProperty(srcNodeRef, ContentModel.PROP_NAME);
                assertEquals("Wrong description", tgtDescription2+"-"+name, srcDescription2);
            }
        }
    }
    
    @Test public void testSimpleMove_FileSync() throws Exception
    {
        final int FILES_PER_SSD_CNT = 2;
        
        runSimpleMove_DirectSync(FILES_PER_SSD_CNT, false);
    }
    
    @Test public void testSimpleMove_FolderSync() throws Exception
    {
        final int FILES_PER_SSD_CNT = 2;
        
        runSimpleMove_DirectSync(FILES_PER_SSD_CNT, true);
    }
    
    private void runSimpleMove_DirectSync(final int FILES_CNT, final boolean folderSync)
    {
        final int SSD_CNT = 1;
        
        final List<TestContextMulti> testContexts = setupSimpleSyncs(SSD_CNT, FILES_CNT, folderSync);
        assertEquals(SSD_CNT, testContexts.size());
        SyncTrackerComponent c = testContexts.get(0).c;
        
        // single SSD
        final TestContextMulti testContext = testContexts.get(0);
        
        push(c);
        
        checkAndGetNewTargetNodes(testContext, FILES_CNT, folderSync);
        
        // move directly synced source nodes (ie. were not testing "move out" in this test)
        NodeRef newSrcParent = temporaryNodes.createNode(testContext.testHome, testName.getMethodName()+"-NewSrcParent-"+RUN_ID, ContentModel.TYPE_FOLDER, AuthenticationUtil.getFullyAuthenticatedUser());
        
        if (folderSync)
        {
            ChildAssociationRef primaryParent = nodeService.getPrimaryParent(testContext.srcFolderNodeRef);
            nodeService.moveNode(testContext.srcFolderNodeRef, newSrcParent, ContentModel.ASSOC_CONTAINS, primaryParent.getQName());
        }
        else
        {
            for (NodeRef srcNodeRef : testContext.srcNodeRefs)
            {
                ChildAssociationRef primaryParent = nodeService.getPrimaryParent(srcNodeRef);
                nodeService.moveNode(srcNodeRef, newSrcParent, ContentModel.ASSOC_CONTAINS, primaryParent.getQName());
            }
        }
        
        checkPushPull(SSD_CNT, FILES_CNT, folderSync, testContexts, c);
        
        // move directly synced target nodes (ie. were not testing "move out" in this test)
        NodeRef newTgtParent = temporaryNodes.createNode(testContext.testHome, testName.getMethodName()+"-NewTgtParent-"+RUN_ID, ContentModel.TYPE_FOLDER, AuthenticationUtil.getFullyAuthenticatedUser());
        
        if (folderSync)
        {
            NodeRef tgtFolderSync = new NodeRef((String)nodeService.getProperty(testContext.srcFolderNodeRef, SyncModel.PROP_OTHER_NODEREF_STRING));
            ChildAssociationRef primaryParent = nodeService.getPrimaryParent(tgtFolderSync);
            nodeService.moveNode(tgtFolderSync, newTgtParent, ContentModel.ASSOC_CONTAINS, primaryParent.getQName());
        }
        else
        {
            for (NodeRef tgtNodeRef : testContext.tgtNodeRefs)
            {
                ChildAssociationRef primaryParent = nodeService.getPrimaryParent(tgtNodeRef);
                nodeService.moveNode(tgtNodeRef, newTgtParent, ContentModel.ASSOC_CONTAINS, primaryParent.getQName());
            }
        }
        
        checkPushPull(SSD_CNT, FILES_CNT, folderSync, testContexts, c);
        
    } // runSimpleMove
    
    @Test public void testMoveOutIn_FolderSync() throws Exception
    {
        final int SSD_CNT = 1;
        final int FILES_CNT = 3;
        
        final boolean folderSync = true;
        
        final List<TestContextMulti> testContexts = setupSimpleSyncs(SSD_CNT, FILES_CNT, folderSync);
        assertEquals(SSD_CNT, testContexts.size());
        
        // single SSD
        final TestContextMulti testContext = testContexts.get(0);
        SyncTrackerComponent c = testContext.c;
        
        push(c);
        
        checkAndGetNewTargetNodes(testContext, FILES_CNT, folderSync);
        
        checkPushPull(SSD_CNT, FILES_CNT, folderSync, testContexts, c);
        
        // "move out" the indirectly synced source nodes
        NodeRef newSrcParent = temporaryNodes.createNode(testContext.testHome, testName.getMethodName()+"-NewSrcParent-"+RUN_ID, ContentModel.TYPE_FOLDER, AuthenticationUtil.getFullyAuthenticatedUser());
        
        List<NodeRef> movedOutSrcNodeRefs = new ArrayList<NodeRef>();
        List<NodeRef> movedOutTgtNodeRefs = new ArrayList<NodeRef>();
        
        for (NodeRef srcNodeRef : testContext.srcNodeRefs)
        {
            if (! srcNodeRef.equals(testContext.srcFolderNodeRef))
            {
                NodeRef tgtNodeRef = new NodeRef((String)nodeService.getProperty(srcNodeRef, SyncModel.PROP_OTHER_NODEREF_STRING));
                
                ChildAssociationRef primaryParent = nodeService.getPrimaryParent(srcNodeRef);
                nodeService.moveNode(srcNodeRef, newSrcParent, ContentModel.ASSOC_CONTAINS, primaryParent.getQName());
                
                movedOutSrcNodeRefs.add(srcNodeRef);
                movedOutTgtNodeRefs.add(tgtNodeRef);
            }
        }
        
        checkNotSyncedAndOrDeleted(movedOutSrcNodeRefs, false);
        
        push(c);
        
        // check that the target nodes have been deleted (due to a "move out")
        checkNotSyncedAndOrDeleted(movedOutTgtNodeRefs, true);
        
        RetryingTransactionCallback<List<NodeRef>> getMemberNodes = new RetryingTransactionCallback<List<NodeRef>>()
        {
            @Override
            public List<NodeRef> execute() throws Throwable
            {
                return syncAdminService.getMemberNodes(testContext.targetSyncSet);
            }
        };
        List<NodeRef> targetNodes = retryingTransactionHelper.doInTransaction(getMemberNodes);
        assertEquals(1, targetNodes.size());
        
        // "move in" the unsynced nodes
        for (NodeRef srcNodeRef : testContext.srcNodeRefs)
        {
            if (! srcNodeRef.equals(testContext.srcFolderNodeRef))
            {
                ChildAssociationRef primaryParent = nodeService.getPrimaryParent(srcNodeRef);
                nodeService.moveNode(srcNodeRef, testContext.srcFolderNodeRef, ContentModel.ASSOC_CONTAINS, primaryParent.getQName());
            }
        }
        
        push(c);
        
        // check that the target nodes have been created (due to a "move in")
        final int expectedCnt = FILES_CNT + (folderSync ? 1 : 0);
        targetNodes = retryingTransactionHelper.doInTransaction(getMemberNodes);
        assertEquals(expectedCnt, targetNodes.size());
        
        testContext.tgtNodeRefs = targetNodes;
        
        checkPushPull(SSD_CNT, FILES_CNT, folderSync, testContexts, c);
    } // testMoveOutIn_FolderSync
    
    @Test public void testSimpleRename_FileSync() throws Exception
    {
        final int FILES_PER_SSD_CNT = 1;
        
        runSimpleRename(FILES_PER_SSD_CNT, false);
    }
    
    @Test public void testSimpleRename_FolderSync() throws Exception
    {
        final int FILES_PER_SSD_CNT = 1;
        
        runSimpleRename(FILES_PER_SSD_CNT, true);
    }
    
    private void runSimpleRename(final int FILES_CNT, final boolean folderSync)
    {
        int SSD_CNT = 1;
        final List<TestContextMulti> testContexts = setupSimpleSyncs(SSD_CNT, FILES_CNT, folderSync);
        assertEquals(SSD_CNT, testContexts.size());
        
        // single SSD
        final TestContextMulti testContext = testContexts.get(0);
        SyncTrackerComponent c = testContext.c;
        
        push(c);
        
        checkAndGetNewTargetNodes(testContext, FILES_CNT, folderSync);
        
        NodeRef tgtFolderSync = null;
        if (folderSync)
        {
            tgtFolderSync = new NodeRef((String)nodeService.getProperty(testContext.srcFolderNodeRef, SyncModel.PROP_OTHER_NODEREF_STRING));
        }
        
        // check they're files or folders, as expected
        for (NodeRef tgtNodeRef : testContext.tgtNodeRefs)
        {
            if (folderSync && tgtNodeRef.equals(tgtFolderSync))
            {
                // folder
                assertTrue(fileFolderService.getFileInfo(tgtNodeRef).isFolder());
            }
            else
            {
                // file
                assertFalse(fileFolderService.getFileInfo(tgtNodeRef).isFolder());
            }
        }
        
        push(c);
        
        // rename target node(s)
        String suffix = "-renamed-tgt";
        for (NodeRef tgtNodeRef : testContext.tgtNodeRefs)
        {
            String name = (String)nodeService.getProperty(tgtNodeRef, ContentModel.PROP_NAME);
            nodeService.setProperty(tgtNodeRef, ContentModel.PROP_NAME, name+suffix);
        }
        
        pull(c);
        
        // validate source node(s)
        for (NodeRef srcNodeRef : testContext.srcNodeRefs)
        {
            String name = (String)nodeService.getProperty(srcNodeRef, ContentModel.PROP_NAME);
            assertTrue(name.endsWith(suffix));
        }
        
        // rename source node(s)
        suffix = "-renamed-src";
        for (NodeRef srcNodeRef : testContext.srcNodeRefs)
        {
            String name = (String)nodeService.getProperty(srcNodeRef, ContentModel.PROP_NAME);
            nodeService.setProperty(srcNodeRef, ContentModel.PROP_NAME, name+suffix);
        }
        
        push(c);
        
        // validate target node(s)
        for (NodeRef tgtNodeRef : testContext.tgtNodeRefs)
        {
            String name = (String)nodeService.getProperty(tgtNodeRef, ContentModel.PROP_NAME);
            assertTrue(name.endsWith(suffix));
        }
        
        checkPushPull(SSD_CNT, FILES_CNT, folderSync, testContexts, c);
        
    } // runSimpleRename
    
    @Test public void testSimpleCOCI() throws Exception
    {
        final int FILES_CNT = 2;
        final int SSD_CNT = 1;
        
        boolean folderSync = false;
        
        final List<TestContextMulti> testContexts = setupSimpleSyncs(SSD_CNT, FILES_CNT, folderSync);
        assertEquals(SSD_CNT, testContexts.size());
        
        // single SSD
        final TestContextMulti testContext = testContexts.get(0);
        SyncTrackerComponent c = testContext.c;
        
        // checkout source files - before initial sync
        for (NodeRef srcNodeRef : testContext.srcNodeRefs)
        {
            cociService.checkout(srcNodeRef);
        }
        
        push(c);
        
        // check sync time after initial sync
        for (NodeRef srcNodeRef : testContext.srcNodeRefs)
        {
            assertNotNull(nodeService.getProperty(srcNodeRef, SyncModel.PROP_SYNC_TIME));
            
            // ALF-15130 (part of)
            assertNotNull(nodeService.getProperty(cociService.getWorkingCopy(srcNodeRef), SyncModel.PROP_SYNC_TIME));
        }
        
        checkAndGetNewTargetNodes(testContext, FILES_CNT, folderSync);
        
    } // test simpleCOCI
    
    /**
     * Simple folder sync (of metadata => description) including immediate children (files) ... sync, push update, pull update, unsync 
     *
     * author janv
     */
    @Test public void testFolderSync_SimplePushPullUnsync_Single() throws Exception
    {
        final int FILES_PER_FOLDER_CNT = 1;
        final int SSD_CNT = 1;
        
        runSimplePushPullUnsync(SSD_CNT, FILES_PER_FOLDER_CNT, true);
    }
    
    @Test public void testFolderSync_SimplePushPullUnsync_Multiple() throws Exception
    {
        final int FILES_PER_FOLDER_CNT = 5;
        final int SSD_CNT = 5;
        
        runSimplePushPullUnsync(SSD_CNT, FILES_PER_FOLDER_CNT, true);
    }
    
    @Test public void test_ALF_15287() throws Exception
    {
        final int FILES_PER_FOLDER_CNT = 1;
        final int SSD_CNT = 1;
        
        final List<TestContextMulti> testContexts = setupSimpleSyncs(SSD_CNT, FILES_PER_FOLDER_CNT, true);
        assertEquals(SSD_CNT, testContexts.size());
        
        // single SSD
        final TestContextMulti testContext = testContexts.get(0);
        SyncTrackerComponent c = testContext.c;
        
        List<NodeRef> existingTgtNodeRefs = new ArrayList<NodeRef>();
        
        // create target nodes in the way (ie. causing a name clash) - before initial sync
        String name = (String)nodeService.getProperty(testContext.srcFolderNodeRef, ContentModel.PROP_NAME);
        NodeRef tgtNodeRef = temporaryNodes.createNode(testContext.tgtFolderNodeRef, name, ContentModel.TYPE_FOLDER, AuthenticationUtil.getFullyAuthenticatedUser());
        existingTgtNodeRefs.add(tgtNodeRef);
        
        for (NodeRef srcNodeRef : testContext.srcNodeRefs)
        {
            if (! nodeService.getType(srcNodeRef).equals(ContentModel.TYPE_FOLDER))
            {
                name = (String)nodeService.getProperty(srcNodeRef, ContentModel.PROP_NAME);
                FileInfo f = fileFolderService.create(tgtNodeRef, name, ContentModel.TYPE_CONTENT);
                
                existingTgtNodeRefs.add(f.getNodeRef());
            }
        }
        
        push(c);
        
        checkNotSynced(testContext.tgtNodeRefs);
        
        checkSyncFailed(Arrays.asList(testContext.srcFolderNodeRef), SyncNodeExceptionType.TARGET_FOLDER_NAME_CLASH);
        
        List<NodeRef> srcFileNodeRefs = new ArrayList<NodeRef>(testContext.srcNodeRefs);
        srcFileNodeRefs.remove(testContext.srcFolderNodeRef);
        
        // TODO we should have a real error code for this case
        checkSyncFailed(srcFileNodeRefs, SyncNodeExceptionType.UNKNOWN, "Can't do an initial sync without a destination parent NodeRef");
        
    } // test_ALF_15287
    
    // "conflict" ... src unsync with delete on tgt + unpulled change from tgt (in this case a rename)
    @Test public void test_ALF_15380_a() throws Exception
    {
        final int FILES_CNT = 1;
        final int SSD_CNT = 1;
        
        boolean folderSync = false;
        
        final List<TestContextMulti> testContexts = setupSimpleSyncs(SSD_CNT, FILES_CNT, folderSync);
        assertEquals(SSD_CNT, testContexts.size());
        
        // single SSD
        final TestContextMulti testContext = testContexts.get(0);
        SyncTrackerComponent c = testContext.c;
        
        push(c);
        
        checkAndGetNewTargetNodes(testContext, FILES_CNT, folderSync);
        
        push(c);
        
        // rename target file(s)
        for (NodeRef tgtNodeRef : testContext.tgtNodeRefs)
        {
            String name = (String)nodeService.getProperty(tgtNodeRef, ContentModel.PROP_NAME);
            nodeService.setProperty(tgtNodeRef, ContentModel.PROP_NAME, name+"-renamed");
        }
        
        // unsync with remove (on target)
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                for (NodeRef srcNodeRef : testContext.srcNodeRefs)
                {
                    syncAdminService.removeSyncSetMember(testContext.sourceSyncSet, srcNodeRef, true);
                }
                return null;
            }
        });
        
        checkNotSynced(testContext.srcNodeRefs);
        
        push(c);
        
        // check target nodes no longer exist
        checkDeleted(testContext.tgtNodeRefs);
        
    } // test_ALF_15380_a
    
    // "conflict" ... src delete with unpulled change on tgt (in this case, metadata change - eg. description) 
    @Test public void test_ALF_15380_b() throws Exception
    {
        final int FILES_CNT = 1;
        final int SSD_CNT = 1;
        
        boolean folderSync = false;
        
        final List<TestContextMulti> testContexts = setupSimpleSyncs(SSD_CNT, FILES_CNT, folderSync);
        assertEquals(SSD_CNT, testContexts.size());
        
        // single SSD
        final TestContextMulti testContext = testContexts.get(0);
        SyncTrackerComponent c = testContext.c;
        
        push(c);
        
        checkAndGetNewTargetNodes(testContext, FILES_CNT, folderSync);
        
        push(c);
        
        // update target file(s)
        for (NodeRef tgtNodeRef : testContext.tgtNodeRefs)
        {
            String description = (String)nodeService.getProperty(tgtNodeRef, ContentModel.PROP_DESCRIPTION);
            nodeService.setProperty(tgtNodeRef, ContentModel.PROP_DESCRIPTION, description+"-changed-on-tgt");
        }
        
        // delete (on src)
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                for (NodeRef srcNodeRef : testContext.srcNodeRefs)
                {
                    nodeService.deleteNode(srcNodeRef);
                }
                return null;
            }
        });
        
        // check source nodes no longer exist
        checkDeleted(testContext.srcNodeRefs);
        
        push(c);
        
        // check target nodes no longer exist
        checkDeleted(testContext.tgtNodeRefs);
        
    } // test_ALF_15380_b
    
    @Test public void testSimpleForceUnsync_FileSync() throws Exception
    {
        final int FILES_PER_SSD_CNT = 1;
        
        runSimpleForceUnsync(FILES_PER_SSD_CNT, false);
    }
    
    @Test public void testSimpleForceUnsync_FolderSync() throws Exception
    {
        final int FILES_PER_SSD_CNT = 1;
        
        runSimpleForceUnsync(FILES_PER_SSD_CNT, true);
    }
    
    // note: "force unsync" is available on target only (to cloud network admin) - only affects target
    private void runSimpleForceUnsync(final int FILES_CNT, final boolean folderSync)
    {
        int SSD_CNT = 1;
        final List<TestContextMulti> testContexts = setupSimpleSyncs(SSD_CNT, FILES_CNT, folderSync);
        assertEquals(SSD_CNT, testContexts.size());
        
        // single SSD
        final TestContextMulti testContext = testContexts.get(0);
        SyncTrackerComponent c = testContext.c;
        
        push(c);
        
        // Validate nodes(s) have been pushed (initial sync)
        checkAndGetNewTargetNodes(testContext, FILES_CNT, folderSync);
        
        checkSynced(testContext.srcNodeRefs);
        checkSynced(testContext.tgtNodeRefs);
        
        final NodeRef tgtFolderSync = (folderSync ? new NodeRef((String)nodeService.getProperty(testContext.srcFolderNodeRef, SyncModel.PROP_OTHER_NODEREF_STRING)) : null);
        
        // check they're files or folders, as expected
        for (NodeRef tgtNodeRef : testContext.tgtNodeRefs)
        {
            if (folderSync && tgtNodeRef.equals(tgtFolderSync))
            {
                // folder
                assertTrue(fileFolderService.getFileInfo(tgtNodeRef).isFolder());
            }
            else
            {
                // file
                assertFalse(fileFolderService.getFileInfo(tgtNodeRef).isFolder());
            }
        }
        
        // force unsync on target
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                if (folderSync)
                {
                    syncAdminService.removeSyncSetMember(testContext.targetSyncSet, tgtFolderSync, false);
                }
                else
                {
                    for (NodeRef tgtNodeRef : testContext.tgtNodeRefs)
                    {
                        syncAdminService.removeSyncSetMember(testContext.targetSyncSet, tgtNodeRef, false);
                    }
                }
                return null;
            }
        });
        
        checkNotSynced(testContext.tgtNodeRefs);
        checkSynced(testContext.srcNodeRefs);
        
        // this will pull the unsynce/remove but not action it (except it will clear the "otherNodeRef")
        pull(c);
        
        checkNotSynced(testContext.tgtNodeRefs);
        checkSynced(testContext.srcNodeRefs);
        
        // request sync (on src)
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                syncService.requestSync(testContext.srcNodeRefs);
                return null;
            }
        });
        
        push(c);
        
        checkNotSynced(testContext.tgtNodeRefs);
        
        if (folderSync)
        {
            checkSyncFailed(Arrays.asList(testContext.srcFolderNodeRef), SyncNodeExceptionType.TARGET_FOLDER_NAME_CLASH);
            
            List<NodeRef> srcFileNodeRefs = new ArrayList<NodeRef>(testContext.srcNodeRefs);
            srcFileNodeRefs.remove(testContext.srcFolderNodeRef);
            
            // TODO we should have a real error code for this case
            checkSyncFailed(srcFileNodeRefs, SyncNodeExceptionType.UNKNOWN, "Can't do an initial sync without a destination parent NodeRef");
        }
        else
        {
            checkSyncFailed(testContext.srcNodeRefs, SyncNodeExceptionType.TARGET_FOLDER_NAME_CLASH);
        }
        
        // delete the clashing targets
        for (NodeRef nodeRef : testContext.tgtNodeRefs)
        {
            if (nodeService.exists(nodeRef))
            {
                nodeService.deleteNode(nodeRef);
            }
        }
        
        // request sync again (on src)
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                syncService.requestSync(testContext.srcNodeRefs);
                return null;
            }
        });
        
        push(c);
        
        // Validate nodes(s) have been pushed (re-created sync) - note: this will also update the tgtNodeRefs
        checkAndGetNewTargetNodes(testContext, FILES_CNT, folderSync);
        
        checkSynced(testContext.tgtNodeRefs);
        checkSynced(testContext.srcNodeRefs);
        
        checkPushPull(SSD_CNT, FILES_CNT, folderSync, testContexts, c);
        
    } // runSimpleForceUnsync
    
    //
    // create folder hierarchy, sync the folder and then unsync
    //
    // eg. 2 levels (root folder = level 0) with 3 files per folder, 2 folders per folder
    //
    @Test public void test_ALF_16282() throws Exception
    {
        final int ssdCnt = 1;
        final int filesCnt = 3;
        final int folderCnt = 2;
        final int folderLevels = 2;
        
        final List<TestContextMulti> testContexts = setupFolderSyncHierarchy(ssdCnt, filesCnt, folderCnt, folderLevels);
        assertEquals(ssdCnt, testContexts.size());
        
        // single SSD
        final TestContextMulti testContext = testContexts.get(0);
        SyncTrackerComponent c = testContext.c;
        
        push(c);
        
        checkAndGetNewTargetNodes(testContext, filesCnt, true, folderCnt, folderLevels);
        
        checkSynced(testContext.srcNodeRefs);
        checkSynced(testContext.tgtNodeRefs);
        
        // unsync (src) - without delete of target
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                syncAdminService.removeSyncSetMember(testContext.sourceSyncSet, testContext.srcFolderNodeRef, false);
                return null;
            }
        });
        
        checkNotSynced(testContext.srcNodeRefs);
        checkSynced(testContext.tgtNodeRefs);
        
        push(c);
        
        checkNotSynced(testContext.tgtNodeRefs);
    }
    
     /**
     * MNT-11429
     * 
     * 1) Create folder and subfolder
     * 2) sync folder
     * 3) unsync folder
     */
    @Test
    public void test_ASuncUnsync()
    {
        System.out.println("begin");
        final int ssdCnt = 1;
        final int filesCnt = 0;
        final int folderCnt = 1;
        final int folderLevels = 2;
        
        final List<TestContextMulti> testContexts = setupFolderSyncHierarchy(ssdCnt, filesCnt, folderCnt, folderLevels);
        final TestContextMulti testContext = testContexts.get(0);
        SyncTrackerComponent c = testContext.c;
        
        // unsync (src) - without delete of target
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                syncAdminService.removeSyncSetMember(testContext.sourceSyncSet, testContext.srcFolderNodeRef, false);
                return null;
            }
        });
        push(c);
        checkNotSynced(testContext.srcNodeRefs);
    }
    
    
    /**
     * Test ACE-2560
     * 
     * Pull an indirect node with properties and aspects added before transaction commit
     * 
     * 0: Pull no change
     * 
     * Update the target folder with an indirectly synced node 
     * containing custom properties
     * 
     * 1: Pull the create and check the properties
     * 
     * @throws Exception
     */
    @Test public void testPullIndirectNodeWithProperties() throws Exception
    {
        final QName PROP_SUMMARY_WEBSCRIPT = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "summaryWebscript");
        
//        final QName PROP_APPLICATION_NAME = QName.createQName("http://www.alfresco.com/model/attachment/1.0", "applicationName");
//        final QName PROP_ATTACHMENT_OF_NAME = QName.createQName("http://www.alfresco.com/model/attachment/1.0", "attachmentOfName");
//        final QName PROP_ATTACHMENT_OF_URI = QName.createQName("http://www.alfresco.com/model/attachment/1.0", "attachmentOfUri");

        final QName ASPECT_REFERENCES_NODE = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "referencesnode");
        final QName ASPECT_PROJECT_SUMMARY = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "projectsummary");

//        final QName ASPECT_ATTACHMENT = QName.createQName("http://www.alfresco.com/model/attachment/1.0", "attachment");
        
        syncChangeMonitor.addCustomPropertyToTrack(ContentModel.PROP_COMPANYPOSTCODE);
        syncChangeMonitor.addCustomPropertyToTrack(ContentModel.PROP_TELEPHONE);
        syncChangeMonitor.addCustomPropertyToTrack(ContentModel.PROP_AUTHOR);
        syncChangeMonitor.addCustomPropertyToTrack(ContentModel.PROP_NODE_REF);
        syncChangeMonitor.addCustomPropertyToTrack(PROP_SUMMARY_WEBSCRIPT);
        syncChangeMonitor.addCustomAspectToTrack(RenditionModel.ASPECT_PREVENT_RENDITIONS);
        syncChangeMonitor.addCustomAspectToTrack(ASPECT_REFERENCES_NODE);
        syncChangeMonitor.addCustomAspectToTrack(ASPECT_PROJECT_SUMMARY);
//        List<String> aspects = new ArrayList<String>() ;
//        aspects.add("attach:attachment");
//        syncChangeMonitor.setAspectsToTrack(aspects);
//        List<String> properties = new ArrayList<String>() ;
//        properties.add("attach:attachment.*");
//        syncChangeMonitor.setPropertiesToTrack(properties);
        
        final List<TestContextMulti> testContexts = setupSimpleSyncs(1, 0, true);
        assertEquals(1, testContexts.size());
        final SyncTrackerComponent c = testContexts.get(0).c;
        
        final ChildAssociationRef srcFolderRef = retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<ChildAssociationRef>()
        {
            public ChildAssociationRef execute() throws Throwable
            {      
                ChildAssociationRef childRef = nodeService.createNode(testContexts.get(0).srcFolderNodeRef, 
                        ContentModel.ASSOC_CONTAINS, 
                        QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "Test Folder"), 
                        ContentModel.TYPE_FOLDER);
                return childRef;
            }
        });
        
        /**
         * Test 0: Push and Pull the test folder
         */
        logger.debug("push the test folder node localNodeRef:" + srcFolderRef.getChildRef());
        push(c);
        
        final String TEST_NAME="testPullIndirectNodeWithProperties"; 
        
        /**
         * Add a test node to the target folder
         */
        final NodeRef targetNodeRef = testContexts.get(0).tgtFolderNodeRef;
    
        
        final String TEST_POSTCODE = "PC1 1PC";
        final String TEST_TELEPHONE = "000 999";
        final String TEST_AUTHOR = "JRR Tolkein";  // for ASPECT_AUTHOR
        final String TEST_NODE_NAME = "MyTestNode";
        final String TEST_TITLE = "Farmer Giles of Ham";
        final String TEST_DESCRIPTION = "A farmer reluctantly sets off to kill a dragon";       
        final NodeRef TEST_NODEREF = new NodeRef(StoreRef.PROTOCOL_TEST.toString(), "foo", "bar");
        final String TEST_SUMMARY_WEBSCRIPT = "summary webscript";
        
        final NodeRef testSourceNode = retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {   
                List<ChildAssociationRef> folders = nodeService.getChildAssocs(targetNodeRef);  
                assertTrue("folder size wrong", folders.size() == 1);
                final NodeRef myTarget = folders.get(0).getChildRef();
                
                assertNotNull("myTarget is null", myTarget);             
                
                final Map<QName, Serializable>props = new HashMap<QName, Serializable>();
                props.put(ContentModel.PROP_COMPANYPOSTCODE, TEST_POSTCODE);
                props.put(ContentModel.PROP_AUTHOR, TEST_AUTHOR);
                props.put(ContentModel.PROP_NODE_REF, TEST_NODEREF);  // Mandatory aspect property 
                props.put(PROP_SUMMARY_WEBSCRIPT, TEST_SUMMARY_WEBSCRIPT); // Non mandatory aspect property

                Set<QName> targetAspects = nodeService.getAspects(myTarget);
                Map<QName, Serializable> targetProps = nodeService.getProperties(myTarget);
                logger.debug("test folder targetAspects: " + targetAspects);
                logger.debug("test folder targetProps: " + targetProps.keySet());
                
                // non marker aspect
                final Map<QName, Serializable>titleProps = new HashMap<QName, Serializable>();
                titleProps.put(ContentModel.PROP_TITLE, TEST_TITLE);
                titleProps.put(ContentModel.PROP_DESCRIPTION, TEST_DESCRIPTION);
                props.putAll(titleProps);
                
//              // Jared's aspect
//              final Map<QName, Serializable>attachmentProps = new HashMap<QName, Serializable>();
//              attachmentProps.put(PROP_APPLICATION_NAME, "Jared app name");
//              attachmentProps.put(PROP_ATTACHMENT_OF_NAME, "Jared of name");
//              attachmentProps.put(PROP_ATTACHMENT_OF_URI, "Jared of uri");
                //nodeService.addAspect(childRef.getChildRef(), ASPECT_ATTACHMENT, attachmentProps);

                logger.debug("adding new test node");
                ChildAssociationRef childRef = nodeService.createNode(myTarget, ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, TEST_NODE_NAME), ContentModel.TYPE_CONTENT, props);
                logger.debug(" new test node added :" + childRef);

                assertFalse("check new node does not yet have sync set member node",
                   nodeService.hasAspect(childRef.getChildRef(), SyncModel.ASPECT_SYNC_SET_MEMBER_NODE));

                logger.debug("set property");
                // property after create
                nodeService.setProperty(childRef.getChildRef(), ContentModel.PROP_TELEPHONE , TEST_TELEPHONE);
                nodeService.setProperty(childRef.getChildRef(), ContentModel.PROP_NAME , TEST_NODE_NAME);
                
                logger.debug("add marker aspect after create");
                // marker aspect
                nodeService.addAspect(childRef.getChildRef(), RenditionModel.ASPECT_PREVENT_RENDITIONS, null);
                
                logger.debug("all properties set");
                
                return childRef.getChildRef();
            }
        }, false, true);
        
        logger.debug("pull the new node which has PROP_TELEPHONE and PROP_COMPANYPOSTCODE");
        pull(c);
        
        logger.debug("now validate the pulled node");
     
        // Validate
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {   
                NodeRef source = testContexts.get(0).srcFolderNodeRef;
                
                List<ChildAssociationRef> children = nodeService.getChildAssocs(source);
                
                boolean found = false;
                
                /*
                 * Note that this pulled file is written to the the wrong place, however 
                 * that's likely to be a test harness error.  Rather than a real problem.
                 */
                for(ChildAssociationRef child : children)
                {      
                    logger.debug(child);
                    
                    if(child.getQName().getLocalName().equalsIgnoreCase(TEST_NODE_NAME))
                    {
                        NodeRef testNode = child.getChildRef();
                        
                        assertTrue("test error, checking wrong node", !testNode.equals(testSourceNode));
                        
                        logger.debug(nodeService.getType(testNode));
                        Map<QName, Serializable> targetProps = nodeService.getProperties(testNode);
                        logger.debug("targetProps: " + targetProps);
                        assertEquals("telephone number wrong", TEST_TELEPHONE, nodeService.getProperty(testNode, ContentModel.PROP_TELEPHONE));
                        assertEquals("Postcode wrong", TEST_POSTCODE, nodeService.getProperty(testNode, ContentModel.PROP_COMPANYPOSTCODE));
                        assertEquals("Title wrong", TEST_TITLE, nodeService.getProperty(testNode, ContentModel.PROP_TITLE));
                        assertTrue("Test node does not have author aspect", nodeService.hasAspect(testNode, ContentModel.ASPECT_AUTHOR));
                        assertTrue("Test node does not have marker aspect (ASPECT_PREVENT_RENDITIONS)", nodeService.hasAspect(testNode, RenditionModel.ASPECT_PREVENT_RENDITIONS)); 
                        assertTrue("Test node does not have aspect (ASPECT_TITLED)", nodeService.hasAspect(testNode, ContentModel.ASPECT_TITLED)); 
                        assertEquals("PROP_SUMMARY_WEBSCRIPT wrong", TEST_SUMMARY_WEBSCRIPT, nodeService.getProperty(testNode, PROP_SUMMARY_WEBSCRIPT));
 //                       assertEquals("node ref wrong", TEST_NODEREF, nodeService.getProperty(testNode, ContentModel.PROP_NODE_REF));
                        
                        found = true;
                    }
                }
                
                assertTrue("testNode not found", found);
                                
                return null;
            }
        }, false, true);
       
    } // testPullIndirectNodeWithProperties 

    
    private static class TestContext
    {
        NodeRef fileANodeRef;
        NodeRef fileBNodeRef;
        
        NodeRef targetFolderNodeRef;
        
        SyncSetDefinition sourceSyncSet;
        SyncSetDefinition targetSyncSet;
    };
    
    private static class TestContextMulti
    {
        // note: files + folder (in case of simple folder sync)
        List<NodeRef> srcNodeRefs = new ArrayList<NodeRef>();
        List<NodeRef> tgtNodeRefs = new ArrayList<NodeRef>();
        
        NodeRef srcFolderNodeRef;
        NodeRef tgtFolderNodeRef;
        
        SyncSetDefinition sourceSyncSet;
        SyncSetDefinition targetSyncSet;
        
        NodeRef testHome;
        
        SyncTrackerComponent c;
    };
}
