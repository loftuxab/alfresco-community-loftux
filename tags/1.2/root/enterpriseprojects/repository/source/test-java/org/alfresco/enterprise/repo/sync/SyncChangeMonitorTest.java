/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.alfresco.enterprise.repo.sync.audit.SyncAuditService;
import org.alfresco.enterprise.repo.sync.audit.SyncAuditServiceImplTest.SyncAuditCleaner;
import org.alfresco.enterprise.repo.sync.transport.CloudSyncSetDefinitionTransport;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.mode.ServerMode;
import org.alfresco.repo.mode.ServerModeProvider;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.repo.version.VersionModel;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.cmr.version.VersionType;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.test.junitrules.AlfrescoPerson;
import org.alfresco.util.test.junitrules.RunAsFullyAuthenticatedRule;
import org.alfresco.util.test.junitrules.TemporaryMockOverride;
import org.alfresco.util.test.junitrules.TemporaryNodes;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.springframework.extensions.webscripts.GUID;

/**
 * Integration tests for {@link SyncChangeMonitor}.
 * 
 * This test class ensures that the correct methods in the {@link SyncAuditService} are called
 * when various sync-relevant changes occur. The {@link SyncAuditService} is mocked out using
 * <a href="http://code.google.com/p/mockito/" >Mockito</a> which 'records' which methods are called during
 * the execution of a test method.
 * 
 * @author Neil Mc Erlean
 * @since CloudSync
 */
public class SyncChangeMonitorTest extends BaseSyncServiceImplTest
{
    // Rule to create a test user.
    public static AlfrescoPerson TEST_USER1 = new AlfrescoPerson(APP_CONTEXT_INIT, "UserOne");
    
    // Tie them together in a static Rule Chain
    @ClassRule public static RuleChain ruleChain = RuleChain.outerRule(APP_CONTEXT_INIT)
                                                            .around(TEST_USER1);
    
    // A rule to manage test nodes use in each test method
    @Rule public TemporaryNodes temporaryNodes = new TemporaryNodes(APP_CONTEXT_INIT);
    
    // A rule to ensure the Sync Audit is cleaned
    @Rule public SyncAuditCleaner syncAuditCleaner = new SyncAuditCleaner(APP_CONTEXT_INIT);
    
    // A rule to allow individual test methods all to be run as "UserOne".
    @Rule public RunAsFullyAuthenticatedRule runAsRule = new RunAsFullyAuthenticatedRule(TEST_USER1);
    
    @ClassRule public static TemporaryMockOverride MOCK_OVERRIDES = new TemporaryMockOverride();
    
    // Various services
    private static ContentService              CONTENT_SERVICE;
    private static DictionaryService           DICTIONARY_SERVICE;
    private static NamespaceService            NAMESPACE_SERVICE;
    private static NodeService                 NODE_SERVICE;
    private static NodeService                 PUBLIC_NODE_SERVICE;
    private static RetryingTransactionHelper   TRANSACTION_HELPER;
    private static SyncAdminService            SYNC_ADMIN_SERVICE;
    private static SyncAdminService            PUBLIC_SYNC_ADMIN_SERVICE;
    private static SyncAuditService            MOCK_SYNC_AUDIT_SERVICE = mock(SyncAuditService.class);
    private static CloudSyncSetDefinitionTransport SSD_TRANSPORT;
    private static SyncAuditService            REAL_SYNC_AUDIT_SERVICE;
    private static SyncChangeMonitor           SYNC_CHANGE_MONITOR;
    private static VersionService              VERSION_SERVICE;			   
    
    private static NodeRef COMPANY_HOME;
    
    private SyncSetDefinition testSsd;
    private NodeRef testNodeU1_1, testNodeU1_2, testNodeU1_3;
    
    // And some test nodes which are in synced folders.
    private SyncSetDefinition testSsd_folder1, testSsd_folder2;
    private NodeRef testFolderA, testFolderB, testDocA, testDocB;
    
    private NodeRef testNodeUnsynced;
    
    @BeforeClass public static void initStaticData() throws Exception
    {
        CONTENT_SERVICE           = APP_CONTEXT_INIT.getApplicationContext().getBean("contentService", ContentService.class);
        DICTIONARY_SERVICE        = APP_CONTEXT_INIT.getApplicationContext().getBean("dictionaryService", DictionaryService.class);
        NAMESPACE_SERVICE         = APP_CONTEXT_INIT.getApplicationContext().getBean("namespaceService", NamespaceService.class);
        NODE_SERVICE              = APP_CONTEXT_INIT.getApplicationContext().getBean("nodeService", NodeService.class);
        PUBLIC_NODE_SERVICE       = APP_CONTEXT_INIT.getApplicationContext().getBean("NodeService", NodeService.class);
        TRANSACTION_HELPER        = APP_CONTEXT_INIT.getApplicationContext().getBean("retryingTransactionHelper", RetryingTransactionHelper.class);
        SYNC_ADMIN_SERVICE        = APP_CONTEXT_INIT.getApplicationContext().getBean("syncAdminService", SyncAdminService.class);
        PUBLIC_SYNC_ADMIN_SERVICE = APP_CONTEXT_INIT.getApplicationContext().getBean("SyncAdminService", SyncAdminService.class);
        SSD_TRANSPORT             = APP_CONTEXT_INIT.getApplicationContext().getBean("cloudSyncSetDefinitionTransport", CloudSyncSetDefinitionTransport.class);
        REAL_SYNC_AUDIT_SERVICE   = APP_CONTEXT_INIT.getApplicationContext().getBean("syncAuditService", SyncAuditService.class);
        SYNC_CHANGE_MONITOR       = APP_CONTEXT_INIT.getApplicationContext().getBean("syncChangeMonitor", SyncChangeMonitor.class);
        VERSION_SERVICE           = APP_CONTEXT_INIT.getApplicationContext().getBean("versionService", VersionService.class);
        
        // Wire in our mock/testing cloud connector
        MOCK_OVERRIDES.setTemporaryField(SYNC_ADMIN_SERVICE, "cloudConnectorService", MOCK_CLOUD_CONNECTOR_SERVICE);
        MOCK_OVERRIDES.setTemporaryField(SSD_TRANSPORT, "cloudConnectorService", MOCK_CLOUD_CONNECTOR_SERVICE);
        
        
        // Override the normal SyncAuditService with a mock
        MOCK_OVERRIDES.setTemporaryField(SYNC_CHANGE_MONITOR, "syncAuditService", MOCK_SYNC_AUDIT_SERVICE);
        
        Repository repositoryHelper = (Repository) APP_CONTEXT_INIT.getApplicationContext().getBean("repositoryHelper");
        COMPANY_HOME = repositoryHelper.getCompanyHome();
    }
    
    @AfterClass public static void restoreBeanStates() throws Exception
    {
        // Put back the original audit, for other tests in our suite
        SYNC_CHANGE_MONITOR.setSyncAuditService(REAL_SYNC_AUDIT_SERVICE);
    }
    
    @Before public void createTestContent()
    {
        String guid = GUID.generate();
        // Create some test content to sync
        testNodeU1_1 = temporaryNodes.createNode(COMPANY_HOME, "User1 node1 " + guid, ContentModel.TYPE_CONTENT, TEST_USER1.getUsername());
        testNodeU1_2 = temporaryNodes.createNodeWithTextContent(COMPANY_HOME, "User1 node2 " + guid, ContentModel.TYPE_CONTENT, TEST_USER1.getUsername(), "Here is some initial test content");
        testNodeU1_3 = temporaryNodes.createNodeWithTextContent(COMPANY_HOME, "User1 node3 " + guid, ContentModel.TYPE_CONTENT, TEST_USER1.getUsername(), "Here is some initial test content");
        testNodeUnsynced = temporaryNodes.createNodeWithTextContent(COMPANY_HOME, "User1 unsynced node " + guid, ContentModel.TYPE_CONTENT, TEST_USER1.getUsername(), "Here is some initial test content");
        
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
        testSsd = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<SyncSetDefinition>()
        {
            public SyncSetDefinition execute() throws Throwable
            {
                // add some of them to a sync set so the various aspects/associations are correctly init'd.
                SyncSetDefinition ssd = SYNC_ADMIN_SERVICE.createSourceSyncSet(
                                                       Arrays.asList(new NodeRef[] {testNodeU1_1, testNodeU1_2, testNodeU1_3}),
                                                       "tenantX", "test://Node/Ref", 
                                                       false, true, false);
                
                temporaryNodes.addNodeRef(ssd.getNodeRef());
                return ssd;
            }
        });
        
        // And some more test content this time within synced folders.
        testFolderA = temporaryNodes.createFolder(COMPANY_HOME, "Folder A " + guid, TEST_USER1.getUsername());
        testFolderB = temporaryNodes.createFolder(COMPANY_HOME, "Folder B " + guid, TEST_USER1.getUsername());
        testDocA = temporaryNodes.createNode(testFolderA, "User1 doc A " + guid, ContentModel.TYPE_CONTENT, TEST_USER1.getUsername());
        testDocB = temporaryNodes.createNode(testFolderB, "User1 doc B " + guid, ContentModel.TYPE_CONTENT, TEST_USER1.getUsername());
        
        testSsd_folder1 = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<SyncSetDefinition>()
        {
            public SyncSetDefinition execute() throws Throwable
            {
                SyncSetDefinition ssd = SYNC_ADMIN_SERVICE.createSourceSyncSet(
                                                       Arrays.asList(new NodeRef[] {testFolderA}),
                                                       "tenantX", "test://Node/Ref", false, true, false);
                
                temporaryNodes.addNodeRef(ssd.getNodeRef());
                return ssd;
            }
        });
        testSsd_folder2 = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<SyncSetDefinition>()
        {
            public SyncSetDefinition execute() throws Throwable
            {
                SyncSetDefinition ssd = SYNC_ADMIN_SERVICE.createSourceSyncSet(
                                                       Arrays.asList(new NodeRef[] {testFolderB}),
                                                       "tenantX", "test://Node/Ref", false, true, false);
                
                temporaryNodes.addNodeRef(ssd.getNodeRef());
                return ssd;
            }
        });
        
        
        // At this point the mocked SyncAuditService will have stored various methods which were called on it.
        // These are caused by the setup of the SyncSetDefinition and its members and are the same for all tests.
        // We are not interested in these calls and so we should remove them.
        // Mockito does not support the removal of this history, so we'll create a new mock instance instead:
        MOCK_SYNC_AUDIT_SERVICE = mock(SyncAuditService.class);
        MOCK_OVERRIDES.setTemporaryField(APP_CONTEXT_INIT.getApplicationContext().getBean("syncChangeMonitor", SyncChangeMonitor.class), "syncAuditService", MOCK_SYNC_AUDIT_SERVICE);
    }
    
    // There are no aspects on the SyncSetDefinition which are to be synced.
    
    @Test public void addRemoveIrrelevantAspectToFromSsd() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                NODE_SERVICE.addAspect(testSsd.getNodeRef(), ContentModel.ASPECT_COUNTABLE, null);
                verifyZeroInteractions(MOCK_SYNC_AUDIT_SERVICE);
                
                NODE_SERVICE.removeAspect(testSsd.getNodeRef(), ContentModel.ASPECT_COUNTABLE);
                verifyZeroInteractions(MOCK_SYNC_AUDIT_SERVICE);
                
                return null;
            }
        });
    }
    
    // There are currently no properties on the SyncSetDefinition which can be changed & synced.
    
    // SSDs have no content to change.
    
    
    
    
    // SSMNs = Sync Set Member Nodes.
    /**
     * SSD membership is defined by the association between an {@link SyncModel#TYPE_SYNC_SET_DEFINITION}
     * and its various {@link SyncModel#ASPECT_SYNC_SET_MEMBER_NODE} peers.
     * If we add or remove these associations, we are linking new members to a set.
     */
    @Test public void addRemoveMembershipAssocSsmn() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                assertFalse(NODE_SERVICE.hasAspect(testNodeUnsynced, SyncModel.ASPECT_SYNC_SET_MEMBER_NODE));
                
                NODE_SERVICE.createAssociation(testSsd.getNodeRef(), testNodeUnsynced, SyncModel.ASSOC_SYNC_MEMBERS);
                verify(MOCK_SYNC_AUDIT_SERVICE, times(1)).recordSsmnAdded(eq(testNodeUnsynced));
                
                NODE_SERVICE.removeAssociation(testSsd.getNodeRef(), testNodeUnsynced, SyncModel.ASSOC_SYNC_MEMBERS);
                verify(MOCK_SYNC_AUDIT_SERVICE, times(1)).recordSsmnRemoved(eq(testSsd), eq(testNodeUnsynced));
                
                return null;
            }
        });
    }
    
    @Test public void deleteSsmn() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                assertTrue(NODE_SERVICE.hasAspect(testNodeU1_1, SyncModel.ASPECT_SYNC_SET_MEMBER_NODE));
                NODE_SERVICE.deleteNode(testNodeU1_1);
                
                verify(MOCK_SYNC_AUDIT_SERVICE, times(1)).recordSsmnDeleted(eq(testSsd), eq(testNodeU1_1));
                return null;
            }
        });
    }
    
    @Test public void addRemoveRelevantAspectToFromSsmn() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                assertFalse("Test node already had the aspect.", NODE_SERVICE.hasAspect(testNodeU1_2, ContentModel.ASPECT_TITLED));
                
                // add
                NODE_SERVICE.addAspect(testNodeU1_2, ContentModel.ASPECT_TITLED, null);
                verify(MOCK_SYNC_AUDIT_SERVICE).recordAspectAdded(eq(testNodeU1_2), eq(ContentModel.ASPECT_TITLED));
                
                // remove
                NODE_SERVICE.removeAspect(testNodeU1_2, ContentModel.ASPECT_TITLED);
                verify(MOCK_SYNC_AUDIT_SERVICE).recordAspectRemoved(eq(testNodeU1_2), eq(ContentModel.ASPECT_TITLED));
                
                return null;
            }
        });
    }
    
    @Test public void addRemoveIrrelevantAspectToFromSsmn() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                assertFalse("Test node already had the aspect.", NODE_SERVICE.hasAspect(testNodeU1_2, ContentModel.ASPECT_COUNTABLE));
                
                // add
                NODE_SERVICE.addAspect(testNodeU1_2, ContentModel.ASPECT_COUNTABLE, null);
                verifyZeroInteractions(MOCK_SYNC_AUDIT_SERVICE);
                
                // remove
                NODE_SERVICE.removeAspect(testNodeU1_2, ContentModel.ASPECT_COUNTABLE);
                verifyZeroInteractions(MOCK_SYNC_AUDIT_SERVICE);
                
                return null;
            }
        });
    }
    
    @Test public void changeRelevantNonContentPropertyOnSsmn() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            @SuppressWarnings("unchecked")
            public Void execute() throws Throwable
            {
                // Put the titled aspect on so that when we add the cm:title property below, the aspect isn't auto-added.
                NODE_SERVICE.addAspect(testNodeU1_1, ContentModel.ASPECT_TITLED, null);
                
                // add property
                NODE_SERVICE.setProperty(testNodeU1_1, ContentModel.PROP_TITLE, "new title");
                
                verify(MOCK_SYNC_AUDIT_SERVICE, times(1)).recordNonContentPropertiesUpdate(eq(testNodeU1_1), anyMap(), anyMap());
                
                
                // change property
                NODE_SERVICE.setProperty(testNodeU1_2, ContentModel.PROP_NAME, "new name");
                
                verify(MOCK_SYNC_AUDIT_SERVICE, times(1)).recordNonContentPropertiesUpdate(eq(testNodeU1_1), anyMap(), anyMap());
                
                // FIXME Property removal triggers an event, but we don't get before and after values and so can't actually tell what property got removed.
                // NODE_SERVICE.removeProperty(testNodeU1_3, ContentModel.PROP_TITLE);
                
                return null;
            }
        });
    }
    
    @Test public void changeIrrelevantNonContentPropertyOnSsmn() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                NODE_SERVICE.setProperty(testNodeU1_1, ContentModel.PROP_COUNTER, new Integer(42));
                
                // No updates should have been made to the Audit.
                verifyZeroInteractions(MOCK_SYNC_AUDIT_SERVICE);
                return null;
            }
        });
    }
    
    @Test public void changeContentOnSsmn() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                // change content
                ContentWriter writer = CONTENT_SERVICE.getWriter(testNodeU1_1, ContentModel.PROP_CONTENT, true);
                writer.setMimetype("silly/mimetype");
                writer.setEncoding("UTF-8");
                writer.putContent("updated");
                
                verify(MOCK_SYNC_AUDIT_SERVICE).recordContentPropertyUpdate(eq(testNodeU1_1), any(ContentData.class), any(ContentData.class));
                return null;
            }
        });
    }
    
    
    
    // Alfresco nodes which are not related to Syncing at all.
    @Test public void createDeleteIrrelevantNode() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                NodeRef newNode = temporaryNodes.createNode(COMPANY_HOME, "Test node", ContentModel.TYPE_CONTENT, AuthenticationUtil.getFullyAuthenticatedUser());
                verifyZeroInteractions(MOCK_SYNC_AUDIT_SERVICE);
                
                // change property
                NODE_SERVICE.deleteNode(newNode);
                verifyZeroInteractions(MOCK_SYNC_AUDIT_SERVICE);
                
                return null;
            }
        });
    }
    
    // eg. CLOUD-950 - GoogleDoc created (in synced folder)
    @Test public void createTemporaryContentInSyncedFolder() throws Exception
    {
        final NodeRef newFolderNode = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {
                NodeRef newFolderNode = temporaryNodes.createNode(COMPANY_HOME, "Test folder node", ContentModel.TYPE_FOLDER, AuthenticationUtil.getFullyAuthenticatedUser());
                verifyZeroInteractions(MOCK_SYNC_AUDIT_SERVICE);
                
                @SuppressWarnings("unused")
                SyncSetDefinition ssd = SYNC_ADMIN_SERVICE.createSourceSyncSet(
                        Arrays.asList(new NodeRef[] {newFolderNode}),
                        "tenantX", "test://Node/Ref", false, true, false);
                
                return newFolderNode;
            }
        });
        
        verify(MOCK_SYNC_AUDIT_SERVICE, times(1)).recordSsmnAdded(eq(newFolderNode));
        
        final NodeRef newFileNode = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {
                NodeRef newFileNode = temporaryNodes.createNode(newFolderNode, "Test node", ContentModel.TYPE_CONTENT, AuthenticationUtil.getFullyAuthenticatedUser());
                NODE_SERVICE.addAspect(newFileNode, ContentModel.ASPECT_TEMPORARY, null);
                
                return newFileNode;
            }
        });
        
        // irrelevant when temporary
        verifyZeroInteractions(MOCK_SYNC_AUDIT_SERVICE);
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                NODE_SERVICE.removeAspect(newFileNode, ContentModel.ASPECT_TEMPORARY);
                
                return null;
            }
        });
        
        // relevant when no longer temporary (not not deleted)
        verify(MOCK_SYNC_AUDIT_SERVICE, times(1)).recordSsmnAdded(eq(newFileNode));
    }
    
    @Test public void addRemoveRelevantAspectToFromIrrelevantNode() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                assertFalse("Test node already had the aspect.", NODE_SERVICE.hasAspect(testNodeUnsynced, ContentModel.ASPECT_TITLED));
                
                // add
                NODE_SERVICE.addAspect(testNodeUnsynced, ContentModel.ASPECT_TITLED, null);
                verifyZeroInteractions(MOCK_SYNC_AUDIT_SERVICE);
                
                // remove
                NODE_SERVICE.removeAspect(testNodeUnsynced, ContentModel.ASPECT_TITLED);
                verifyZeroInteractions(MOCK_SYNC_AUDIT_SERVICE);
                
                return null;
            }
        });
    }
    
    @Test public void changeRelevantNonContentPropertyOnIrrelevantNode() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                NODE_SERVICE.setProperty(testNodeUnsynced, ContentModel.PROP_NAME, "new name");
                
                verifyZeroInteractions(MOCK_SYNC_AUDIT_SERVICE);
                return null;
            }
        });
    }
    
    @Test public void changeIrrelevantNonContentPropertyOnIrrelevantNode() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                NODE_SERVICE.setProperty(testNodeUnsynced, ContentModel.PROP_COUNTER, new Integer(42));
                
                // No updates should have been made to the Audit.
                verifyZeroInteractions(MOCK_SYNC_AUDIT_SERVICE);
                return null;
            }
        });
    }
    
    @Test public void changeContentOnIrrelevantNode() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                // change content
                ContentWriter writer = CONTENT_SERVICE.getWriter(testNodeUnsynced, ContentModel.PROP_CONTENT, true);
                writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
                writer.setEncoding("UTF-8");
                writer.putContent("updated");
                return null;
            }
        });
        verifyZeroInteractions(MOCK_SYNC_AUDIT_SERVICE);
    }
    
    @Test public void moveNodeFromOneSyncedFolderIntoAnotherSyncedFolder() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                ChildAssociationRef oldChAssRef = NODE_SERVICE.getPrimaryParent(testDocA);
                NODE_SERVICE.moveNode(testDocA, testFolderB, oldChAssRef.getTypeQName(), oldChAssRef.getQName());
                return null;
            }
        });
        // Moves are currently recorded in the audit as a removal from one sync set and an addition to the other sync set.
        verify(MOCK_SYNC_AUDIT_SERVICE).recordSsmnDeleted(testSsd_folder1, testDocA);
        verify(MOCK_SYNC_AUDIT_SERVICE).recordSsmnAdded(testDocA);
    }
    
    @Test public void injectTrackablePropertiesByName()
    {
        SyncChangeMonitor myMonitor = new SyncChangeMonitor();
        myMonitor.setDictionaryService(DICTIONARY_SERVICE);
        myMonitor.setNamespaceService(NAMESPACE_SERVICE);
        
        List<String> props = Arrays.asList(new String[] {"cm:name", "cm:title"});
        List<QName> propsQName = Arrays.asList(new QName[] {ContentModel.PROP_NAME, ContentModel.PROP_TITLE});
        
        myMonitor.setPropertiesToTrack(props);
        
        assertEquals(propsQName, myMonitor.getPropertiesToTrack());
    }
    
    @Test public void injectTrackablePropertiesByAspectWithWildcard()
    {
        SyncChangeMonitor myMonitor = new SyncChangeMonitor();
        myMonitor.setDictionaryService(DICTIONARY_SERVICE);
        myMonitor.setNamespaceService(NAMESPACE_SERVICE);
        
        List<String> props = Arrays.asList(new String[] {"cm:name", "cm:titled.*"}); // Note the "titled" with a 'd' is intentional.
        List<QName> propsQName = Arrays.asList(new QName[] {ContentModel.PROP_NAME, ContentModel.PROP_TITLE, ContentModel.PROP_DESCRIPTION});
        
        myMonitor.setPropertiesToTrack(props);
        
        assertEquals(propsQName, myMonitor.getPropertiesToTrack());
    }
    
    @Test public void revertOfSyncSetMember()
    {
    	if(!PUBLIC_NODE_SERVICE.hasAspect(testNodeU1_1, SyncModel.ASPECT_SYNC_SET_MEMBER_NODE))
    	{
            TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
            {
                    public Void execute() throws Throwable
                    {
                    	SYNC_ADMIN_SERVICE.addSyncSetMember(testSsd, testNodeU1_1);
                        return null;
                    }
            });
    	}

    	// pre-test assertions
    	assertTrue(PUBLIC_NODE_SERVICE.hasAspect(testNodeU1_1, SyncModel.ASPECT_SYNC_SET_MEMBER_NODE));
    	
    	// Make sure there is at least 1 version
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                 // Set a property value so we can check revert is doing stuff
                NODE_SERVICE.setProperty(testNodeU1_1, ContentModel.PROP_LASTNAME, "Fred");
                // A property in the sync set member node that must not be reverted
                NODE_SERVICE.setProperty(testNodeU1_1, SyncModel.PROP_OTHER_NODEREF_STRING, "a test value");
                return null;
            }
        });
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                if(!NODE_SERVICE.hasAspect(testNodeU1_1, SyncModel.ASPECT_SYNC_SET_MEMBER_NODE))
                {
                    NODE_SERVICE.addAspect(testNodeU1_1, ContentModel.ASPECT_VERSIONABLE, null);
                }
                Version version = VERSION_SERVICE.createVersion(testNodeU1_1, Collections.<String,Serializable>singletonMap(VersionModel.PROP_VERSION_TYPE, VersionType.MAJOR));
                return null;
            }
        }, false, true);
    	
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
             public Void execute() throws Throwable
             {
            	NODE_SERVICE.setProperty(testNodeU1_1, ContentModel.PROP_LASTNAME, "Bill"); 
            	VERSION_SERVICE.revert(testNodeU1_1) ;
                return null;
             }
        }, false, true);
        
    	// Make sure that after revert the node is still a ssmn and in testSsd
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
            	assertTrue(NODE_SERVICE.hasAspect(testNodeU1_1, SyncModel.ASPECT_SYNC_SET_MEMBER_NODE));
            	assertEquals("reverted node not in testSsd", testSsd.getId(),  SYNC_ADMIN_SERVICE.getSyncSetDefinition(testNodeU1_1).getId());
            	assertNotNull("reverted node does not have a OTHER_NODEREF_STRING",  NODE_SERVICE.getProperty(testNodeU1_1,  SyncModel.PROP_OTHER_NODEREF_STRING));
                return null;
            }
        }, true, true);
    	
    	// Make sure that after revert the node is still a ssmn and in testSsd
    	
    	/* 
    	 *  Now for the next part of the test - move the node to another sync set before reverting it
    	 *  node should remain in the new sync set
    	 */
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
            	SYNC_ADMIN_SERVICE.removeSyncSetMember(testSsd, testNodeU1_1);
                // add some of them to a sync set so the various aspects/associations are correctly init'd.
                SyncSetDefinition ssd2 = SYNC_ADMIN_SERVICE.createSourceSyncSet(
                                                       Arrays.asList(new NodeRef[] {testNodeU1_1}),
                                                       "tenantX", "test://Node/Ref2", false, true, false);
            	temporaryNodes.addNodeRef(ssd2.getNodeRef());              	
                return null;
            }
        });
            	
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                    	VERSION_SERVICE.revert(testNodeU1_1) ;
                        return null;
            }
        });
        
    	assertTrue(PUBLIC_NODE_SERVICE.hasAspect(testNodeU1_1, SyncModel.ASPECT_SYNC_SET_MEMBER_NODE));
    	assertNotEquals("reverted node has reverted to old ssd, rather than remaining in the new one", 
    			testSsd.getId(),  
    			PUBLIC_SYNC_ADMIN_SERVICE.getSyncSetDefinition(testNodeU1_1).getId());
    	
    	/**
    	 * And the final part revert a node that has been removed from a sync set, node should remain 
    	 * unsynced
    	 */
    	TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
    	{
             public Void execute() throws Throwable
    		 {
            	 SyncSetDefinition ssd = SYNC_ADMIN_SERVICE.getSyncSetDefinition(testNodeU1_1);
    		     SYNC_ADMIN_SERVICE.removeSyncSetMember(ssd, testNodeU1_1);
    		     return null;
    		 }
    	});
    		            	
    	TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
    	{
    		 public Void execute() throws Throwable
    		 {
    		   	VERSION_SERVICE.revert(testNodeU1_1) ;
    		    return null;
    		 }
        });	        
    	assertFalse(
    	        "node has reverted into an old sync set",
    	        PUBLIC_NODE_SERVICE.hasAspect(testNodeU1_1, SyncModel.ASPECT_SYNC_SET_MEMBER_NODE));   		   

    }
}
