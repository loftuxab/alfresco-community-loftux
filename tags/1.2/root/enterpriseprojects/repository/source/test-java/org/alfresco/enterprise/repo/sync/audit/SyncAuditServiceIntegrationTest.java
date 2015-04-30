/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync.audit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.enterprise.repo.sync.BaseSyncServiceImplTest;
import org.alfresco.enterprise.repo.sync.SyncAdminService;
import org.alfresco.enterprise.repo.sync.SyncAdminServiceImpl;
import org.alfresco.enterprise.repo.sync.SyncSetDefinition;
import org.alfresco.enterprise.repo.sync.SyncTrackerComponent;
import org.alfresco.enterprise.repo.sync.audit.SyncAuditServiceImplTest.SyncAuditCleaner;
import org.alfresco.enterprise.repo.sync.audit.SyncEventHandler.AuditEventId;
import org.alfresco.enterprise.repo.sync.transport.CloudSyncSetDefinitionTransport;
import org.alfresco.enterprise.repo.sync.transport.impl.CloudSyncSetDefinitionTransportImpl;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.mode.ServerMode;
import org.alfresco.repo.mode.ServerModeProvider;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.test.junitrules.AlfrescoPeople;
import org.alfresco.util.test.junitrules.TemporaryNodes;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;

/**
 * This test class sets up a realistic dataset (nodes, content, sync sets as well as audit events) and then performs various queries
 * on that dataset to ensure all is well in the audit.
 * For finer grained tests of the {@link SyncAuditService}, please see {@link SyncAuditServiceImplTest}.
 * <p/>
 * There are two places in the live system that will be querying the {@link SyncAuditService}:
 * <ol>
 *   <li>The On Premise Quartz Job that scans for local changes.</li>
 *   <li>The REST API implementation in the Cloud that scans for local (Cloud) changes when polled by On Premise</li>
 * </ol>
 * This class performs some typical queries as used by these components.
 * 
 * @author Neil Mc Erlean
 * @since 4.1
 */
public class SyncAuditServiceIntegrationTest extends BaseSyncServiceImplTest
{
    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(SyncAuditServiceIntegrationTest.class);
    
    /** Let's have audit-relevant changes made by multiple users. */
    private static final int NUMBER_OF_TEST_USERS = 3;
    private static final AlfrescoPeople TEST_USERS = new AlfrescoPeople(APP_CONTEXT_INIT, NUMBER_OF_TEST_USERS);
    
    private static final TemporaryNodes TEMPORARY_NODES = new TemporaryNodes(APP_CONTEXT_INIT);
    
    // A rule to ensure the Sync Audit is cleaned
    private static SyncAuditCleaner SYNCAUDIT_CLEANER = new SyncAuditCleaner(APP_CONTEXT_INIT);
    
    // Tie them together in a static Rule Chain
    @ClassRule public static RuleChain ruleChain = RuleChain.outerRule(APP_CONTEXT_INIT)
                                                            .around(TEST_USERS)
                                                            .around(TEMPORARY_NODES)
                                                            .around(SYNCAUDIT_CLEANER);
    
    
    // Various services
    private static ContentService              CONTENT_SERVICE;
    private static NodeService                 NODE_SERVICE;
    private static RetryingTransactionHelper   TRANSACTION_HELPER;
    private static SyncAuditService            SYNC_AUDIT_SERVICE;
    private static SyncAdminService            SYNC_ADMIN_SERVICE;
    private static CloudSyncSetDefinitionTransport SSD_TRANSPORT;
    
    private static NodeRef COMPANY_HOME;
    private static String SRC_REPO_ID;
    
    private static int MAX_RESULTS = 1024;
    
    private static final Map<String, SyncSetDefinition> ssdsByUser = new HashMap<String, SyncSetDefinition>();
    
    @BeforeClass public static void initStaticData() throws Exception
    {
        // Spring services
        CONTENT_SERVICE            = APP_CONTEXT_INIT.getApplicationContext().getBean("contentService", ContentService.class);
        NODE_SERVICE               = APP_CONTEXT_INIT.getApplicationContext().getBean("nodeService", NodeService.class);
        TRANSACTION_HELPER         = APP_CONTEXT_INIT.getApplicationContext().getBean("retryingTransactionHelper", RetryingTransactionHelper.class);
        SYNC_AUDIT_SERVICE         = APP_CONTEXT_INIT.getApplicationContext().getBean("syncAuditService", SyncAuditServiceImpl.class);
        SYNC_ADMIN_SERVICE         = APP_CONTEXT_INIT.getApplicationContext().getBean("syncAdminService", SyncAdminServiceImpl.class);
        SSD_TRANSPORT              = APP_CONTEXT_INIT.getApplicationContext().getBean("cloudSyncSetDefinitionTransport", CloudSyncSetDefinitionTransport.class);
        
        // Disable push/pull jobs for this unit test
        SyncTrackerComponent syncTackerComponent = APP_CONTEXT_INIT.getApplicationContext().getBean("syncTrackerComponent", SyncTrackerComponent.class);
        syncTackerComponent.setEnabled(false);
        
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
        
        // Wire in our mock/testing cloud connector
        ((SyncAdminServiceImpl)SYNC_ADMIN_SERVICE).setCloudConnectorService(MOCK_CLOUD_CONNECTOR_SERVICE);
        ((CloudSyncSetDefinitionTransportImpl)SSD_TRANSPORT).setCloudConnectorService(MOCK_CLOUD_CONNECTOR_SERVICE);
        
        // Company Home
        Repository repositoryHelper = (Repository) APP_CONTEXT_INIT.getApplicationContext().getBean("repositoryHelper");
        COMPANY_HOME = repositoryHelper.getCompanyHome();
        
        SRC_REPO_ID = SYNC_AUDIT_SERVICE.getRepoId();
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                SYNC_AUDIT_SERVICE.clearAudit();
                return null;
            }
        });
        
        // Each user will create a few content items.
        for (final String username : TEST_USERS.getUsernames())
        {
            final NodeRef n1 = TEMPORARY_NODES.createNode(COMPANY_HOME, username + " node1", ContentModel.TYPE_CONTENT, username);
            final NodeRef n2 = TEMPORARY_NODES.createNode(COMPANY_HOME, username + " node2", ContentModel.TYPE_CONTENT, username);
            final NodeRef n3 = TEMPORARY_NODES.createNode(COMPANY_HOME, username + " node3", ContentModel.TYPE_CONTENT, username);
            
            AuthenticationUtil.pushAuthentication();
            
            AuthenticationUtil.setFullyAuthenticatedUser(username);
            
            // They put their content into a sync set.
            TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
            {
                public Void execute() throws Throwable
                {
                    SyncSetDefinition ssd = SYNC_ADMIN_SERVICE.createSourceSyncSet(Arrays.asList(new NodeRef[]{ n1, n2, n3 }),
                                                                  "remoteTenantId", "target://Folder/NodeRef",
                                                                  false, true, false);
                    TEMPORARY_NODES.addNodeRef(ssd.getNodeRef());
                    
                    ssdsByUser.put(username, ssd);
                    return null;
                }
            });
            
            // And they make a few changes that should show up in the sync audit tables.
            // I'm spreading these changes across multiple transactions intentionally.
            TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
            {
                public Void execute() throws Throwable
                {
                    NODE_SERVICE.setProperty(n1, ContentModel.PROP_NAME, "new cmname n1 " + username);
                    return null;
                }
            });
            
            TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
            {
                public Void execute() throws Throwable
                {
                    ContentWriter cw = CONTENT_SERVICE.getWriter(n2, ContentModel.PROP_CONTENT, true);
                    cw.putContent("new content");
                    return null;
                }
            });
            
            TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
            {
                public Void execute() throws Throwable
                {
                    NODE_SERVICE.deleteNode(n3);
                    return null;
                }
            });
            
            AuthenticationUtil.popAuthentication();
            
            // TODO Maybe add some SSD deletions in here.
        }
    }
    
    @Test public void queryForSsdManifestAndDetails() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                List<String> ssdManifest = SYNC_AUDIT_SERVICE.querySsdManifest(SRC_REPO_ID, MAX_RESULTS);
                
                SyncAuditServiceImplTest.SyncAuditServiceUtils.printSyncAudit(System.err, APP_CONTEXT_INIT.getApplicationContext());
                
                assertEquals("Wrong number of ssdIds in manifest", TEST_USERS.getUsernames().size(), ssdManifest.size());
                
                for (String ssdId : ssdManifest)
                {
                    List<SyncChangeEvent> ssdChanges = SYNC_AUDIT_SERVICE.queryBySsdId(ssdId, MAX_RESULTS);
                    
                    // Per user: 
                    // 3 SSMN added, 1 props changed (cm:name), 1 content changed, 1 SSMN deleted => 6
                    assertEquals("Wrong number of ssd-related sync events: "+ssdChanges, 6, ssdChanges.size());
                }
                
                return null;
            }
        });
    }
    
    @Test public void queryForChangesOnSpecificMemberNode() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                // First get a valid SSMN, as we haven't stored them in this class anywhere (not worth it).
                final String firstUser = TEST_USERS.getUsernames().iterator().next();
                SyncSetDefinition mySSD = ssdsByUser.get(firstUser);
                assertNotNull("No SSD found for user " + firstUser, mySSD);
                
                List<NodeRef> memberNodes = SYNC_ADMIN_SERVICE.getMemberNodes(mySSD);
                
                // We'll just use the first one, as we don't care which one we use.
                assertFalse(memberNodes.isEmpty());
                NodeRef memberNode = memberNodes.get(0);
                
                
                SyncAuditServiceImplTest.SyncAuditServiceUtils.printSyncAudit(System.err, APP_CONTEXT_INIT.getApplicationContext());
                
                // Get the changes
                List<SyncChangeEvent> nodeChanges = SYNC_AUDIT_SERVICE.queryByNodeRef(memberNode, MAX_RESULTS);
                
                System.err.println("Retrieved changes for node " + memberNode);
                
                // There should be the addition of the SSMN and a property change and that's all.
                assertEquals(2, nodeChanges.size());
                assertEquals(AuditEventId.SSMN_ADDED, nodeChanges.get(0).getEventId());
                assertEquals(AuditEventId.PROPS_CHANGED, nodeChanges.get(1).getEventId());
                
                return null;
            }
            
        });
    }
}
