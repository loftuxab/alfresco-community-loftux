/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync.audit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.alfresco.enterprise.repo.sync.BaseSyncServiceImplTest;
import org.alfresco.enterprise.repo.sync.SyncAdminService;
import org.alfresco.enterprise.repo.sync.SyncAdminServiceImpl;
import org.alfresco.enterprise.repo.sync.SyncSetDefinition;
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
import org.alfresco.service.cmr.audit.AuditQueryParameters;
import org.alfresco.service.cmr.audit.AuditService;
import org.alfresco.service.cmr.audit.AuditService.AuditQueryCallback;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.alfresco.util.test.junitrules.AlfrescoPerson;
import org.alfresco.util.test.junitrules.ApplicationContextInit;
import org.alfresco.util.test.junitrules.RunAsFullyAuthenticatedRule;
import org.alfresco.util.test.junitrules.TemporaryNodes;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.rules.RuleChain;
import org.springframework.context.ApplicationContext;

/**
 * Integration tests for {@link SyncAdminServiceImpl}. This test class sends specific auditable events into the {@link SyncAuditService}
 * and tests that each works. See {@link SyncAuditServiceIntegrationTest} for a more 'realistic' test.
 * 
 * @author Neil Mc Erlean
 * @since 4.1
 */
public class SyncAuditServiceImplTest extends BaseSyncServiceImplTest
{
    private static final Log log = LogFactory.getLog(SyncAuditServiceImplTest.class);

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
    
    // A rule to allow individual test methods all to be run as "UserOne".
    // Some test methods need to switch user during execution which they are free to do.
    @Rule public RunAsFullyAuthenticatedRule runAsRule = new RunAsFullyAuthenticatedRule(TEST_USER1);
    
    // Various services
    private static ContentService              CONTENT_SERVICE;
    private static NodeService                 NODE_SERVICE;
    private static RetryingTransactionHelper   TRANSACTION_HELPER;
    private static SyncAuditService            SYNC_AUDIT_SERVICE;
    private static SyncAdminService            SYNC_ADMIN_SERVICE;
    private static CloudSyncSetDefinitionTransport SSD_TRANSPORT;
    
    private static NodeRef COMPANY_HOME;
    
    private NodeRef testNodeU1_1, testNodeU1_2;
    private NodeRef testNodeU2_1, testNodeU2_2;
    
    private SyncSetDefinition ssd1, ssd2;
    
    private String RUNID = System.currentTimeMillis()+"";
    
    @BeforeClass public static void initStaticData() throws Exception
    {
        CONTENT_SERVICE            = APP_CONTEXT_INIT.getApplicationContext().getBean("contentService", ContentService.class);
        NODE_SERVICE               = APP_CONTEXT_INIT.getApplicationContext().getBean("nodeService", NodeService.class);
        TRANSACTION_HELPER         = APP_CONTEXT_INIT.getApplicationContext().getBean("retryingTransactionHelper", RetryingTransactionHelper.class);
        SYNC_AUDIT_SERVICE         = APP_CONTEXT_INIT.getApplicationContext().getBean("syncAuditService", SyncAuditService.class);
        SYNC_ADMIN_SERVICE         = APP_CONTEXT_INIT.getApplicationContext().getBean("syncAdminService", SyncAdminService.class);
        SSD_TRANSPORT              = APP_CONTEXT_INIT.getApplicationContext().getBean("cloudSyncSetDefinitionTransport", CloudSyncSetDefinitionTransport.class);
        
        // Wire in our mock/testing cloud connector
        ((SyncAdminServiceImpl)SYNC_ADMIN_SERVICE).setCloudConnectorService(MOCK_CLOUD_CONNECTOR_SERVICE);
        ((CloudSyncSetDefinitionTransportImpl)SSD_TRANSPORT).setCloudConnectorService(MOCK_CLOUD_CONNECTOR_SERVICE);
        
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
        
        Repository repositoryHelper = (Repository) APP_CONTEXT_INIT.getApplicationContext().getBean("repositoryHelper");
        COMPANY_HOME = repositoryHelper.getCompanyHome();
    }
    
    @Before public void createTestContent()
    {
        // Create some test content to sync
        testNodeU1_1 = temporaryNodes.createNode(COMPANY_HOME, "User1 node1"+RUNID, ContentModel.TYPE_CONTENT, TEST_USER1.getUsername());
        testNodeU1_2 = temporaryNodes.createNode(COMPANY_HOME, "User1 node2"+RUNID, ContentModel.TYPE_CONTENT, TEST_USER1.getUsername());
        
        testNodeU2_1 = temporaryNodes.createNode(COMPANY_HOME, "User2 node1"+RUNID, ContentModel.TYPE_CONTENT, TEST_USER2.getUsername());
        testNodeU2_2 = temporaryNodes.createNode(COMPANY_HOME, "User2 node2"+RUNID, ContentModel.TYPE_CONTENT, TEST_USER2.getUsername());
    }
    
    /**
     * This test method will create and delete a Sync Set Definition, which will test the the addition and removal of aspects 
     * (of the member aspect).
     */
    @Test public void auditSyncSetDefinitionCreationAndDeletion() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                ssd1 = SYNC_ADMIN_SERVICE.createSourceSyncSet(Arrays.asList(new NodeRef[]{ testNodeU1_1, testNodeU1_2 }),
                                                              "remoteTenantId", "target://Folder/NodeRef",
                                                              false, true, false);
                temporaryNodes.addNodeRef(ssd1.getNodeRef());
                
                SYNC_ADMIN_SERVICE.deleteSourceSyncSet(ssd1.getId());
                
                // Only users with write-permissions on all member nodes can create SSDs.
                AuthenticationUtil.pushAuthentication();
                AuthenticationUtil.setFullyAuthenticatedUser(TEST_USER2.getUsername());
                try
                {
                    
                    ssd2 = SYNC_ADMIN_SERVICE.createSourceSyncSet(Arrays.asList(new NodeRef[]{ testNodeU2_1, testNodeU2_2 }),
                            "remoteTenantId", "target://Folder/NodeRef",
                            false, true, false);
                    temporaryNodes.addNodeRef(ssd2.getNodeRef());
                    
                    SYNC_ADMIN_SERVICE.deleteSourceSyncSet(ssd2.getId());
                }
                finally
                {
                    AuthenticationUtil.popAuthentication();
                }
                
                return null;
            }
        });
        
        
        AuthenticationUtil.pushAuthentication();
        AuthenticationUtil.setFullyAuthenticatedUser(TEST_USER1.getUsername());
        try
        {
            // Query for events for a specific node
            TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
            {
                public Void execute() throws Throwable
                {
                    SyncAuditServiceImplTest.SyncAuditServiceUtils.printSyncAudit(System.err, APP_CONTEXT_INIT.getApplicationContext());
                    
                    List<SyncChangeEvent> events = SYNC_AUDIT_SERVICE.queryByNodeRef(ssd1.getNodeRef(), 100);
                    assertEquals(0, events.size());
                    
                    return null;
                }
            });
            
            // Query for events for a specific ssdId (which will include member creation/deletion).
            TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
            {
                public Void execute() throws Throwable
                {
                    List<SyncChangeEvent> events = SYNC_AUDIT_SERVICE.queryBySsdId(ssd2.getId(), 100);
                    assertEquals(2, events.size());
                    
                    assertEquals(AuditEventId.SSMN_ADDED,    events.get(0).getEventId());
                    assertEquals(testNodeU2_1,              events.get(0).getNodeRef());
                    assertEquals(ContentModel.TYPE_CONTENT, events.get(0).getNodeType());
                    
                    assertEquals(AuditEventId.SSMN_ADDED,    events.get(1).getEventId());
                    assertEquals(testNodeU2_2,              events.get(1).getNodeRef());
                    
                    // And data common across all events.
                    for (SyncChangeEvent event : events)
                    {
                        assertEquals(TEST_USER2.getUsername(),           event.getUser());
                        assertEquals(ssd2.getId(),                       event.getSsdId());
                    }
                    
                    return null;
                }
            });
        }
        finally
        {
            AuthenticationUtil.popAuthentication();
        }
    }
    
    @Test public void auditNonContentPropertyChangeInMemberNode() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                // Set up the test data.
                ssd1 = SYNC_ADMIN_SERVICE.createSourceSyncSet(Arrays.asList(new NodeRef[]{ testNodeU1_1 }),
                                                              "remoteTenantId", "target://Folder/NodeRef",
                                                              false, true, false);
                temporaryNodes.addNodeRef(ssd1.getNodeRef());
                
                // Now we'll change an audited property.
                NODE_SERVICE.setProperty(testNodeU1_1, ContentModel.PROP_NAME, "Aha! New name");
                return null;
            }
        });
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                List<SyncChangeEvent> events = SYNC_AUDIT_SERVICE.queryByNodeRef(testNodeU1_1, 100);
                assertEquals(2, events.size());
                
                // The first event will be the add-aspect due to its membership of the syncset.
                // The second should be the property change we're interested in.
                SyncChangeEvent event = events.get(1);
                
                assertEquals(TEST_USER1.getUsername(),    event.getUser());
                assertEquals(ssd1.getId(),                event.getSsdId());
                assertEquals(testNodeU1_1,                event.getNodeRef());
                
                assertEquals(AuditEventId.PROPS_CHANGED,  event.getEventId());
                HashSet<QName> expectedPropNames = new HashSet<QName>();
                expectedPropNames.add(ContentModel.PROP_NAME);
                assertEquals(expectedPropNames, event.getValues().get(SyncEventHandler.PATH_TO_PROPS_KEY));
                
                return null;
            }
        });
    }
    
    @Test public void auditContentChange() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                // Set up the test data.
                ssd1 = SYNC_ADMIN_SERVICE.createSourceSyncSet(Arrays.asList(new NodeRef[]{ testNodeU1_1 }),
                                                              "remoteTenantId", "target://Folder/NodeRef",
                                                              false, true, false);
                temporaryNodes.addNodeRef(ssd1.getNodeRef());
                
                // Now we'll change the content.
                ContentWriter writer = CONTENT_SERVICE.getWriter(testNodeU1_1, ContentModel.PROP_CONTENT, true);
                writer.putContent("Aha! New content!");
                return null;
            }
        });
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                List<SyncChangeEvent> events = SYNC_AUDIT_SERVICE.queryByNodeRef(testNodeU1_1, 100);
                assertEquals(2, events.size());
                
                // The first event will be the add-aspect due to its membership of the syncset.
                // The second should be the property change we're interested in.
                SyncChangeEvent event = events.get(1);
                
                assertEquals(TEST_USER1.getUsername(),    event.getUser());
                assertEquals(ssd1.getId(),                event.getSsdId());
                assertEquals(testNodeU1_1,                event.getNodeRef());
                
                assertEquals(AuditEventId.CONTENT_CHANGED,  event.getEventId());
                assertNotNull(event.getValues().get(SyncEventHandler.PATH_TO_CONTENT_KEY));
                
                return null;
            }
        });
    }
    
    @Test public void auditEventsShouldBeReturnedInChronologicalOrderAndCorrectlyTruncated() throws Exception
    {
        // Set up some test content and trigger a load of audit events.
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                final List<NodeRef> u1Nodes = Arrays.asList(new NodeRef[]{ testNodeU1_1, testNodeU1_2 });
                ssd1 = SYNC_ADMIN_SERVICE.createSourceSyncSet(u1Nodes,
                                                              "remoteTenantId", "target://Folder/NodeRef",
                                                              false, true, false);
                temporaryNodes.addNodeRef(ssd1.getNodeRef());
                
                for (NodeRef nodeRef : u1Nodes)
                {
                    NODE_SERVICE.setProperty(nodeRef, ContentModel.PROP_NAME,        "new name" + GUID.generate());
                    NODE_SERVICE.setProperty(nodeRef, ContentModel.PROP_DESCRIPTION, "new description");
                    NODE_SERVICE.setProperty(nodeRef, ContentModel.PROP_TITLE,       "new title");
                    ContentWriter writer = CONTENT_SERVICE.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
                    writer.putContent("New content!");
                }
                return null;
            }
        });
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                // Get all the audit events - by NodeRef
                List<SyncChangeEvent> allNodeRefEvents = SYNC_AUDIT_SERVICE.queryByNodeRef(testNodeU1_1, 100);
                assertEventsAreChronological(allNodeRefEvents);
                
                // Get all the audit events - by SSD ID
                List<SyncChangeEvent> allSsdIdEvents = SYNC_AUDIT_SERVICE.queryBySsdId(ssd1.getId(), 100);
                assertEventsAreChronological(allSsdIdEvents);
                
                
                // Now get truncated lists and ensure they are the oldest, not the newest events.
                List<SyncChangeEvent> someNodeRefEvents = SYNC_AUDIT_SERVICE.queryByNodeRef(testNodeU1_1, 2);
                assertEventsAreChronological(someNodeRefEvents);
                assertEquals(allNodeRefEvents.get(0).getTime(), someNodeRefEvents.get(0).getTime());
                
                List<SyncChangeEvent> someSsdIdEvents = SYNC_AUDIT_SERVICE.queryBySsdId(ssd1.getId(), 3);
                assertEventsAreChronological(someSsdIdEvents);
                assertEquals(allSsdIdEvents.get(0).getTime(), someSsdIdEvents.get(0).getTime());
                
                return null;
            }
        });
    }
    
    @Test public void deletionOfAuditEvents() throws Exception
    {
        // Set up some test content and trigger a load of audit events.
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                final List<NodeRef> u1Nodes = Arrays.asList(new NodeRef[]{ testNodeU1_1, testNodeU1_2 });
                ssd1 = SYNC_ADMIN_SERVICE.createSourceSyncSet(u1Nodes,
                                                              "remoteTenantId", "target://Folder/NodeRef",
                                                              false, true, false);
                temporaryNodes.addNodeRef(ssd1.getNodeRef());
                
                for (NodeRef nodeRef : u1Nodes)
                {
                    NODE_SERVICE.setProperty(nodeRef, ContentModel.PROP_NAME,        "new name" + GUID.generate());
                    NODE_SERVICE.setProperty(nodeRef, ContentModel.PROP_DESCRIPTION, "new description");
                    NODE_SERVICE.setProperty(nodeRef, ContentModel.PROP_TITLE,       "new title");
                    ContentWriter writer = CONTENT_SERVICE.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
                    writer.putContent("New content!");
                }
                return null;
            }
        });
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                // Get all the audit events - by SSD ID
                List<SyncChangeEvent> allSsdIdEvents = SYNC_AUDIT_SERVICE.queryBySsdId(ssd1.getId(), 100);
                
                // Now delete them all from the audit tables.
                for (SyncChangeEvent event : allSsdIdEvents)
                {
                    Long auditEntryId = event.getAuditId();
                    SYNC_AUDIT_SERVICE.deleteAuditEntries(new long[] { auditEntryId });
                }
                
                return null;
            }
        });
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                // They should now all be gone from the audit.
                assertTrue("Unexpectedly found some audit entries.", SYNC_AUDIT_SERVICE.queryBySsdId(ssd1.getId(), 100).isEmpty());
                return null;
            }
        });
    }
    
    /**
     * This method goes through the supplied list and asserts that each event occurs after the one before it.
     */
    private void assertEventsAreChronological(List<SyncChangeEvent> allEvents)
    {
        if (allEvents.size() > 1)
        {
            for (int i = 1; i < allEvents.size(); i++)
            {
                SyncChangeEvent event1 = allEvents.get(i - 1);
                SyncChangeEvent event2 = allEvents.get(i);
                
                assertTrue("Audit Event not chronological", event1.getTime() <= event2.getTime());
            }
        }
    }
    
    public static class SyncAuditServiceUtils
    {
        
        public static void printSyncAudit(final PrintStream out, ApplicationContext appContext)
        {
            final AuditService AuditService = appContext.getBean("auditService", AuditService.class);
            final RetryingTransactionHelper transactionHelper = appContext.getBean("retryingTransactionHelper", RetryingTransactionHelper.class);
            
            transactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
            {
                public Void execute() throws Throwable
                {
                    final int maxResults = 1024;
                    final List<SyncChangeEvent> results = new ArrayList<SyncChangeEvent>();
                    
                    AuditQueryParameters params = new AuditQueryParameters();
                    params.setApplicationName(SyncEventHandler.AUDIT_APPLICATION_NAME);
                    
                    AuditService.auditQuery(new AuditQueryCallback()
                    {
                        @Override public boolean valuesRequired() { return true; }
                        
                        @Override public boolean handleAuditEntryError(Long entryId, String errorMsg, Throwable error)
                        {
                            log.warn("Error fetching sync update entry - " + errorMsg, error);
                            return false;
                        }
                        
                        @Override public boolean handleAuditEntry(Long entryId, String applicationName, String user, long time, Map<String, Serializable> values)
                        {
                            results.add(new SyncChangeEventImpl(entryId, user, time, values));
                            return true;
                        }
                    }, params , maxResults);
                    
                    out.println("Audit has " + results.size() + " entries; capped at " + maxResults);
                    out.println("-----");
                    
                    // Table heading
                    out.println(String.format("%-14s", "time") +
                            String.format("%-9s", "username") +
                            String.format("%-9s", "SSD ID") + // actually 36 long
                            String.format("%-13s", "event-id") +
                            String.format("%-61s", "NodeRef") +
                            "Values");
                    for (SyncChangeEvent event : results)
                    {
                        StringBuilder msg = new StringBuilder();
                        msg.append(event.getTime()).append(" ")
                           .append(event.getUser()).append(": ")
                           .append(String.format("%.8s", event.getSsdId())).append(" ")
                           .append(String.format("%-12s", event.getEventId())).append(" ")
                           .append(event.getNodeRef()).append(" ")
                           .append(event.getValues());
                        out.println(msg.toString());
                    }
                    
                    return null;
                }
            }, true, false);
            
        }
    }
    
    /**
     * This JUnit rule can be used to ensure that any Audit entries related to Cloud Sync which are
     * created during the test run are cleared out after execution.
     * <p/>
     * Example usage:
     * <pre>
     * public class YourTestClass
     * {
     *     // Normally we would initialise the spring application context in another rule.
     *     &#64;ClassRule public static final ApplicationContextInit APP_CONTEXT_RULE = new ApplicationContextInit();
     *     
     *     // We pass the rule that creates the spring application context.
     *     &#64;Rule public final SyncAuditCleaner auditCleaner = new SyncAuditCleaner(APP_CONTEXT_RULE);
     *     
     *     &#64;Test public void aTestMethod()
     *     {
     *         // etc
     *     }
     * }
     * </pre>
     * 
     * @author Neil Mc Erlean
     * @since TODO
     */
    public static class SyncAuditCleaner extends ExternalResource
    {
        private final ApplicationContextInit springContextRule;
        
        private long timeThisScopeStartedExecution;
        
        /**
         * Constructs the rule with a reference to a {@link ApplicationContextInit rule} which can be used to retrieve the ApplicationContext.
         * 
         * @param appContextRule a rule which can be used to retrieve the spring app context.
         */
        public SyncAuditCleaner(ApplicationContextInit appContextRule)
        {
            this.springContextRule = appContextRule;
        }
        
        @Override protected void before() throws Throwable
        {
            // Store the start time.
            this.timeThisScopeStartedExecution = System.currentTimeMillis();
        }
        
        @Override protected void after()
        {
            // Now that the test has completed, clear out the relevant audit entries.
            ApplicationContext ctxt = springContextRule.getApplicationContext();
            final AuditService auditService = ctxt.getBean("auditService", AuditService.class);
            RetryingTransactionHelper transactionHelper = ctxt.getBean("retryingTransactionHelper", RetryingTransactionHelper.class);
            
            transactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
            {
                @Override public Void execute() throws Throwable
                {
                    // Delete all audit entries from the time this test started executing to 'now'.
                    int count = auditService.clearAudit(SyncEventHandler.AUDIT_APPLICATION_NAME, timeThisScopeStartedExecution, System.currentTimeMillis());
                    
                    System.err.println(SyncAuditCleaner.class.getSimpleName() + ": Deleted " + count + " sync audit entries");
                    
                    return null;
                }
            });
        }
    }
}
