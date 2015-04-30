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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.enterprise.repo.sync.audit.SyncAuditService;
import org.alfresco.enterprise.repo.sync.audit.SyncChangeEvent;
import org.alfresco.enterprise.repo.sync.transport.CloudSyncContent;
import org.alfresco.enterprise.repo.sync.transport.SyncNodeChangesInfo;
import org.alfresco.enterprise.repo.sync.transport.impl.CloudSyncContentFileImpl;
import org.alfresco.enterprise.repo.sync.transport.impl.SyncNodeChangesInfoImpl;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.filestore.FileContentReader;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.descriptor.DescriptorService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.TempFileProvider;
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
import org.junit.rules.RuleChain;

/**
 * Integration tests for {@link SyncServiceImpl}.
 * 
 * @since 4.1
 */ 
public class SyncServiceImplTest
{
    // Rule to initialise the default Alfresco spring configuration
    public static ApplicationContextInit APP_CONTEXT_INIT = new ApplicationContextInit();
    
    // Rules to create 2 test users.
    public static AlfrescoPerson TEST_USER1 = new AlfrescoPerson(APP_CONTEXT_INIT, "UserOne");
    public static AlfrescoPerson TEST_USER2 = new AlfrescoPerson(APP_CONTEXT_INIT, "UserTwo");
 
    // Tie them together in a static Rule Chain
    @ClassRule public static RuleChain ruleChain = RuleChain.outerRule(APP_CONTEXT_INIT)
                                                            .around(TEST_USER1)    
                                                            .around(TEST_USER2);
    // A rule to manage test nodes use in each test method
    @Rule public TemporaryNodes temporaryNodes = new TemporaryNodes(APP_CONTEXT_INIT);
    
    private static final Log log = LogFactory.getLog(SyncServiceImplTest.class);
    
    // Various services
    private static ContentService              CONTENT_SERVICE;
    private static NodeService                 NODE_SERVICE;
    private static RetryingTransactionHelper   TRANSACTION_HELPER;
    private static SyncAdminService            SYNC_ADMIN_SERVICE;
    private static SyncService                 SYNC_SERVICE;
    private static SyncAuditService            SYNC_AUDIT_SERVICE;
    
    private static NodeRef COMPANY_HOME;
    private static String  SRC_REPO_ID;

    
    @BeforeClass public static void initStaticData() throws Exception
    {
        CONTENT_SERVICE           = APP_CONTEXT_INIT.getApplicationContext().getBean("contentService", ContentService.class);
        NODE_SERVICE              = APP_CONTEXT_INIT.getApplicationContext().getBean("nodeService", NodeService.class);
        TRANSACTION_HELPER        = APP_CONTEXT_INIT.getApplicationContext().getBean("retryingTransactionHelper", RetryingTransactionHelper.class);
        SYNC_AUDIT_SERVICE        = APP_CONTEXT_INIT.getApplicationContext().getBean("syncAuditService", SyncAuditService.class);
        SYNC_ADMIN_SERVICE        = APP_CONTEXT_INIT.getApplicationContext().getBean("syncAdminService", SyncAdminService.class);
        SYNC_SERVICE              = APP_CONTEXT_INIT.getApplicationContext().getBean("syncService", SyncService.class);
        
        // Push the "developer" mode switch to stop checking licenses
        ((SyncAdminServiceImpl)APP_CONTEXT_INIT.getApplicationContext().getBean("syncAdminService")).setCheckLicenseForSyncMode(false);

        Repository repositoryHelper = (Repository) APP_CONTEXT_INIT.getApplicationContext().getBean("repositoryHelper");
        COMPANY_HOME = repositoryHelper.getCompanyHome();
        
        DescriptorService descriptorService = (DescriptorService)APP_CONTEXT_INIT.getApplicationContext().getBean("DescriptorService");
        SRC_REPO_ID = descriptorService.getCurrentRepositoryDescriptor().getId();
    }
    
    private NodeRef targetFolderNodeRef;
    
    @Before public void createTestContent()
    {
        targetFolderNodeRef = temporaryNodes.createNode(COMPANY_HOME, "SyncServiceImplTest", ContentModel.TYPE_FOLDER, TEST_USER1.getUsername());
    }
    
    @Rule public RunAsFullyAuthenticatedRule runAsRule = new RunAsFullyAuthenticatedRule(TEST_USER1);
    
    @Test public void testCreate() throws Exception
    {
        final String NODE_NAME = "testCreate";
        final String ssdId = targetFolderNodeRef.getId();
        final String remoteVersionLabel = "Beta";
        
        Calendar cal = GregorianCalendar.getInstance();
        cal.add(cal.MONTH, -1);
        final Date lastMonth = cal.getTime();
        
        class TestContext
        {
            NodeRef nodeRef;
        };

        final TestContext testContext = new TestContext(); 
        
        final String TEST_CONTENT = "Cerium ËˆsÉªÉ™riÉ™m is a chemical element with the symbol Ce and atomic number 58. It is a soft, silvery, ductile metal which easily oxidizes in air. ";
        final File testFile = TempFileProvider.createTempFile("test", "sync");
        FileOutputStream fos = new FileOutputStream(testFile);
        OutputStreamWriter w = new OutputStreamWriter(fos, "UTF-8");
        w.write(TEST_CONTENT);
        w.close();

        
        /**
         * Positive test - create a sync'd node in a target sync set.
         */
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {
                QName type = ContentModel.TYPE_CONTENT;
                String TEST_MIMETYPE = "text/rhubarb";
       
                String ssdid = targetFolderNodeRef.getId();

                SyncSetDefinition ssd = SYNC_ADMIN_SERVICE.createTargetSyncSet(ssdid, SRC_REPO_ID, targetFolderNodeRef, false, true, false);
                assertNotNull("syncSetDefinition is null", ssd);
                
                NodeRef remoteNodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, "Rhubarb");
                
                SyncNodeChangesInfoImpl newNode = new SyncNodeChangesInfoImpl(
                        null, 
                        remoteNodeRef, 
                        ssd.getId(), 
                        type);
                newNode.setLocalParentNodeRef(targetFolderNodeRef);
                newNode.setRemoteVersionLabel(remoteVersionLabel);
                
                Map<QName, Serializable> propertyUpdates = new HashMap<QName, Serializable> ();
                propertyUpdates.put(ContentModel.PROP_NAME, NODE_NAME);
                newNode.setPropertyUpdates(propertyUpdates);
                
                Map<QName, CloudSyncContent> content = new HashMap<QName, CloudSyncContent>(); 
                CloudSyncContentFileImpl fc = new CloudSyncContentFileImpl(ContentModel.PROP_CONTENT, TEST_MIMETYPE, "UTF-8", testFile);

                content.put(ContentModel.PROP_CONTENT, fc);
                
                
                newNode.setContentUpdates(content);
                newNode.setRemoteModifiedAt(lastMonth);
                
                
                NodeRef newNodeRef = SYNC_SERVICE.create(newNode, false);
                testContext.nodeRef = newNodeRef;
                
                /**
                 * Now validate the newly created node.
                 */
                assertNotNull("newNodeRef is null", newNodeRef);
                
                assertTrue("New node is not a member of the sync set", 
                        NODE_SERVICE.hasAspect(newNodeRef, SyncModel.ASPECT_SYNC_SET_MEMBER_NODE));
                
                
                ContentReader reader = CONTENT_SERVICE.getReader(newNodeRef, ContentModel.PROP_CONTENT);              
                assertNotNull("New node missing content", reader);
                assertEquals("mimetype is wrong", TEST_MIMETYPE, reader.getMimetype());
                String readContent = reader.getContentString();
                assertEquals("size is wrong", readContent.length(), TEST_CONTENT.length());
                assertEquals("content is wrong", readContent, TEST_CONTENT);
           
                Date modified = (Date)NODE_SERVICE.getProperty(newNodeRef, ContentModel.PROP_MODIFIED);
                
                log.debug("modified is : " + modified);
                // Don't attempt to do an exact date comparison.   
                // Test validates modified is not in the last 10 seconds.  Alf should be 
                // accurate to about a second if this is going to fail. 
                assertTrue("modified is too recent", (modified.getTime() + 10000) < (new Date().getTime()));
                
                assertEquals("other node ref not set", NODE_SERVICE.getProperty(newNodeRef, SyncModel.PROP_OTHER_NODEREF_STRING), remoteNodeRef.toString());
                
                // Need to test initial version label.
                
                assertTrue("new node is not versionable", NODE_SERVICE.hasAspect(newNodeRef, ContentModel.ASPECT_VERSIONABLE));
                
                /**
                 * Now retry the call - should still succeed because the node is still in the same syncset.
                 */
                NodeRef retryNodeRef = SYNC_SERVICE.create(newNode, false);
                
                assertEquals("retry node ref and new node refs not equal", retryNodeRef, newNodeRef);
                                
                return null;
            }
        });
        
        /**
         * Check the audit history has been turned off.   Otherwise we end up in an infinite loop.
         */
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {
                NodeRef nodeRef = testContext.nodeRef;
                
                /**
                 * Check audit history has been suppressed
                 */
                {
                    List<SyncChangeEvent> nodeChanges = SYNC_AUDIT_SERVICE.queryByNodeRef(nodeRef, 4);
                
                    for(SyncChangeEvent event : nodeChanges)
                    {
                        System.out.println("Query by nodeRef: " + event);
                    }
                
                    assertTrue("sync set changes have not been suppressed by node.", nodeChanges.size() == 0);
                }

                {
                    List<SyncChangeEvent> syncSetChanges = SYNC_AUDIT_SERVICE.queryBySsdId(ssdId, 4);
                
                    for(SyncChangeEvent event : syncSetChanges)
                    {
                        System.out.println("Query By SsdId: "  + event);
                    }
                
                    assertTrue("sync set changes have not been suppressed by ssdId.", syncSetChanges.size() == 0);
                }
                                
                /**
                 * Check sync set member node aspect
                 */
                assertTrue("node not in sync set member node", NODE_SERVICE.hasAspect(nodeRef, SyncModel.ASPECT_SYNC_SET_MEMBER_NODE));

                return null;
            }
        });
        
        /**
         * Negative test - no ssd
         */
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {  
                try
                {
                    SyncNodeChangesInfo newNode = new SyncNodeChangesInfoImpl(
                        null, 
                        null, 
                        null, 
                        null);
                    SYNC_SERVICE.create(newNode, false);
                   
                    throw new RuntimeException("missing ssd is not an error");
                }
                catch (IllegalArgumentException e)
                {
                    // expect to go here
                }
                return null;
            }
        });

        /**
         * Negative test - a node already exists for a different ssd
         * Should throw a SyncNodeException
         */
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {  
                QName type = ContentModel.TYPE_CONTENT;
                String TEST_MIMETYPE = "text/rhubarb";
       
                // This is a different SSID to above
                String ssdid = targetFolderNodeRef.getId() + 1;

                SyncSetDefinition ssd = SYNC_ADMIN_SERVICE.createTargetSyncSet(ssdid, SRC_REPO_ID, targetFolderNodeRef, false, true, false);
                assertNotNull("syncSetDefinition is null", ssd);
                
                NodeRef remoteNodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, "Custard");
                
                SyncNodeChangesInfoImpl newNode = new SyncNodeChangesInfoImpl(
                        null, 
                        remoteNodeRef, 
                        ssd.getId(), 
                        type);
                newNode.setLocalParentNodeRef(targetFolderNodeRef);
                newNode.setRemoteVersionLabel(remoteVersionLabel);
                
                Map<QName, Serializable> propertyUpdates = new HashMap<QName, Serializable> ();
                propertyUpdates.put(ContentModel.PROP_NAME, NODE_NAME);
                newNode.setPropertyUpdates(propertyUpdates);
                
                Map<QName, CloudSyncContent> content = new HashMap<QName, CloudSyncContent>();            
                CloudSyncContent fcr = new CloudSyncContentFileImpl(ContentModel.PROP_CONTENT, TEST_MIMETYPE, "UTF-8", testFile);
            
                content.put(ContentModel.PROP_CONTENT, fcr);
                newNode.setContentUpdates(content);
                newNode.setRemoteModifiedAt(lastMonth);
                
 
                NodeRef newNodeRef = testContext.nodeRef;
                assertNotNull("newNodeRef is null", newNodeRef);
                
                try
                {
                    SYNC_SERVICE.create(newNode, false);
                    fail("expect to throw SyncNodeException");
                }
                catch (SyncNodeException se)
                {
                    // expect to go here
                }
            
                return null;
            }
        });
        
        
    }
    
    @Test public void testDelete() throws Exception
    {
        final String NODE_NAMEA = "testDeleteA";
        final String NODE_NAMEB = "testDeleteB";
        final String ssdId = targetFolderNodeRef.getId();
        final String remoteVersionLabel = "Beta";
        
        class TestContext
        {
            SyncSetDefinition ssd;
            NodeRef nodeRefA;
            NodeRef nodeRefB;
        };

        final TestContext testContext = new TestContext(); 
        
        /**
         * Do some setup - create three nodes,  nodeA, nodeB and nodeC
         */
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
                    public NodeRef execute() throws Throwable
                    {
                   QName type = ContentModel.TYPE_CONTENT;
                        
                        String ssdid = targetFolderNodeRef.getId();

                        testContext.ssd = SYNC_ADMIN_SERVICE.createTargetSyncSet(ssdid, SRC_REPO_ID, targetFolderNodeRef, false, true, false);
                        assertNotNull("syncSetDefinition is null", testContext.ssd);
                        
                        SyncNodeChangesInfoImpl newNode = new SyncNodeChangesInfoImpl(
                                null, 
                                null, 
                                testContext.ssd.getId(), 
                                type);
                        newNode.setLocalParentNodeRef(targetFolderNodeRef);
                        newNode.setRemoteVersionLabel(remoteVersionLabel);
                        
                        Map<QName, Serializable> propertyUpdates = new HashMap<QName, Serializable> ();
                        propertyUpdates.put(ContentModel.PROP_NAME, NODE_NAMEA);
                        newNode.setPropertyUpdates(propertyUpdates);     
                        testContext.nodeRefA =SYNC_SERVICE.create(newNode, false);
                        
                        propertyUpdates.put(ContentModel.PROP_NAME, NODE_NAMEB);
                        newNode.setPropertyUpdates(propertyUpdates);
                        testContext.nodeRefB = SYNC_SERVICE.create(newNode, false);
                        
                        
                        /**
                         * Now validate the newly created node.
                         */
                        assertNotNull("newNodeRef is null",  testContext.nodeRefA);
                        assertNotNull("newNodeRef is null",  testContext.nodeRefB);
                              
                        return null;
             }
        });

        
        /**
         * Positive test, delete a node A that was in a sync set
         */
        log.debug("test delete - step A - delete node");
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {
                QName type = ContentModel.TYPE_CONTENT;
                
                /**
                 * Now delete node A.
                 */
                assertNotNull("nodeRefA is null", testContext.nodeRefA);
                
                SyncNodeChangesInfoImpl toDelete = new SyncNodeChangesInfoImpl(
                        testContext.nodeRefA, 
                        null, 
                        testContext.ssd.getId(), 
                        type);
                
                /**
                 * This is where we call delete
                 */
                SYNC_SERVICE.delete(toDelete, false);
                
                assertFalse("deleted node A still exists", NODE_SERVICE.exists( testContext.nodeRefA ));
                  
                return null;
            }
        });
        
        /**
         * Force a conflict on node B
         */
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {   
                NODE_SERVICE.setProperty(testContext.nodeRefB, ContentModel.PROP_DESCRIPTION, "Update"); 
                
                assertFalse("deleted node still exists", NODE_SERVICE.exists(testContext.nodeRefA)); 
                return null;
            }
        });
        
        /**
         * Negative test - try to delete a node with un sync'd changes
         */
        log.debug("test delete - step B - delete conflicted node");
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {
                QName type = ContentModel.TYPE_CONTENT;
                
                /**
                 * Now delete node B - should not proceed
                 */
                assertNotNull("nodeRefB is null", testContext.nodeRefB);
                
                SyncNodeChangesInfoImpl toDelete = new SyncNodeChangesInfoImpl(
                        testContext.nodeRefB, 
                        null, 
                        testContext.ssd.getId(), 
                        type);
                
                try
                {
                    /**
                     * This is where we call delete
                     */
                    SYNC_SERVICE.delete(toDelete, false);
                    fail("concurrent modification not detected");
                }
                catch (ConcurrentModificationException ce)
                {
                    // Expect to go here
                    log.debug("expected concurrent modifcation exception - caught");
                }

                return null;
            }
        });
        
        /**
         * Force delete a node with un sync'd changes
         */
        log.debug("test delete - step C - force delete of node B");
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {
                QName type = ContentModel.TYPE_CONTENT;
                
                /**
                 * Now delete node B - should not proceed
                 */
                assertNotNull("nodeRefB is null", testContext.nodeRefB);
                
                SyncNodeChangesInfoImpl toDelete = new SyncNodeChangesInfoImpl(
                        testContext.nodeRefB, 
                        null, 
                        testContext.ssd.getId(), 
                        type);
                
                 /**
                  * This is where we call delete
                  */
                 SYNC_SERVICE.delete(toDelete, true);
                 
   
                return null;
              
            }
        });
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {  
                assertFalse("deleted node still exists", NODE_SERVICE.exists(testContext.nodeRefB)); 
                {
                    List<SyncChangeEvent> nodeChanges = SYNC_AUDIT_SERVICE.queryByNodeRef(testContext.nodeRefB, 4);
                        
                    for(SyncChangeEvent event : nodeChanges)
                    {
                        System.out.println("Query by nodeRef: " + event);
                    }
                        
                    assertTrue("sync set changes have not been suppressed by node.", nodeChanges.size() == 0);
                }
                        
                return null;
            }
        });
    }
    
    @Test public void testRemoveFromSyncSet() throws Exception
    {
        final String NODE_NAMEA = "testRemoveFromSyncSetA";
        final String NODE_NAMEB = "testRemoveFromSyncSetB";
        final String ssdId = targetFolderNodeRef.getId();
        final String remoteVersionLabel = "Beta";
        
        class TestContext
        {
            SyncSetDefinition ssd;
            NodeRef nodeRefA;
            NodeRef nodeRefB;
        };

        final TestContext testContext = new TestContext(); 
        
        /**
         * Do some setup - create three nodes,  nodeA, nodeB and nodeC
         */
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
                    public NodeRef execute() throws Throwable
                    {
                   QName type = ContentModel.TYPE_CONTENT;
                        
                        String ssdid = targetFolderNodeRef.getId();

                        testContext.ssd = SYNC_ADMIN_SERVICE.createTargetSyncSet(ssdid, SRC_REPO_ID, targetFolderNodeRef, false, true, false);
                        assertNotNull("syncSetDefinition is null", testContext.ssd);
                        
                        SyncNodeChangesInfoImpl newNode = new SyncNodeChangesInfoImpl(
                                null, 
                                null, 
                                testContext.ssd.getId(), 
                                type);
                        newNode.setLocalParentNodeRef(targetFolderNodeRef);
                        newNode.setRemoteVersionLabel(remoteVersionLabel);
                        
                        Map<QName, Serializable> propertyUpdates = new HashMap<QName, Serializable> ();
                        propertyUpdates.put(ContentModel.PROP_NAME, NODE_NAMEA);
                        newNode.setPropertyUpdates(propertyUpdates);     
                        testContext.nodeRefA =SYNC_SERVICE.create(newNode, false);
                        
                        propertyUpdates.put(ContentModel.PROP_NAME, NODE_NAMEB);
                        newNode.setPropertyUpdates(propertyUpdates);
                        testContext.nodeRefB = SYNC_SERVICE.create(newNode, false);
                       
                        /**
                         * Now validate the newly created nodes.
                         */
                        assertNotNull("newNodeRefA is null",  testContext.nodeRefA);
                        assertNotNull("newNodeRefB is null",  testContext.nodeRefB);
                              
                        return null;
             }
        });

        
        /**
         * Positive test, remove node A from the sync set
         */
        log.debug("test removeFromSyncSet - step A - remove - no conflict");
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {
                QName type = ContentModel.TYPE_CONTENT;
                
                /**
                 * Now delete node A.
                 */
                assertNotNull("nodeRefA is null", testContext.nodeRefA);
                
                SyncNodeChangesInfoImpl toRemove = new SyncNodeChangesInfoImpl(
                        testContext.nodeRefA, 
                        null, 
                        testContext.ssd.getId(), 
                        type);
                
                /**
                 * This is where we call remove from sync set
                 */
                SYNC_SERVICE.removeFromSyncSet(toRemove, false);
                
                assertTrue("removed node A should still exist", NODE_SERVICE.exists( testContext.nodeRefA ));
                
                // More test here
                
                
                return null;
            }
        });
        
        /**
         * Force a conflict on node B
         */
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {   
                NODE_SERVICE.setProperty(testContext.nodeRefB, ContentModel.PROP_DESCRIPTION, "Update"); 
                return null;
            }
        });
        
        /**
         * Negative test - try to remove a node with un sync'd changes
         */
        log.debug("test remove from sync set - step B - remove conflicted node");
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {
                QName type = ContentModel.TYPE_CONTENT;
                
                /**
                 * Now delete node B - should not proceed
                 */
                assertNotNull("nodeRefB is null", testContext.nodeRefB);
                
                SyncNodeChangesInfoImpl toRemove = new SyncNodeChangesInfoImpl(
                        testContext.nodeRefB, 
                        null, 
                        testContext.ssd.getId(), 
                        type);
                
                try
                {
                    /**
                     * This is where we call delete
                     */
                    SYNC_SERVICE.removeFromSyncSet(toRemove, false);
                    fail("concurrent modification not detected");
                }
                catch (ConcurrentModificationException ce)
                {
                    // Expect to go here
                    log.debug("expected concurrent modifcation exception - caught");
                }

                return null;
            }
        });
        
        /**
         * Force remove a node with un sync'd changes
         */
        log.debug("test remove node - step C - force remove of node B");
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {
                QName type = ContentModel.TYPE_CONTENT;
                
                /**
                 * Now delete node B - should not proceed
                 */
                assertNotNull("nodeRefB is null", testContext.nodeRefB);
                
                SyncNodeChangesInfoImpl toRemove = new SyncNodeChangesInfoImpl(
                        testContext.nodeRefB, 
                        null, 
                        testContext.ssd.getId(), 
                        type);
                
                 /**
                  * This is where we call delete
                  */
                 SYNC_SERVICE.removeFromSyncSet(toRemove, true);
                 
   
                return null;
              
            }
        });
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {  
                assertTrue("removed node should still exist", NODE_SERVICE.exists(testContext.nodeRefB)); 
                {
                    List<SyncChangeEvent> nodeChanges = SYNC_AUDIT_SERVICE.queryByNodeRef(testContext.nodeRefB, 4);
                        
                    for(SyncChangeEvent event : nodeChanges)
                    {
                        System.out.println("Query by nodeRef: " + event);
                    }
                        
                    assertTrue("sync set changes have not been suppressed by node.", nodeChanges.size() == 0);
                }
                        
                return null;
            }
        });

    }
    
    @Test public void testUpdate() throws Exception
    {
        final String NODE_NAME = "testUpdate";
        final String ssdId = targetFolderNodeRef.getId();
        final String remoteVersionLabel = "Beta";
        
        class TestContext
        {
            NodeRef nodeRef;
        };
        
        final String TEST_CONTENT = "Praseodymium ( /ËŒpreÉªziË�ÉµËˆdÉªmiÉ™m/ pray-zee-o-dim-ee-É™m[2]) is a chemical element that has the symbol Pr and atomic number 59. Praseodymium is a soft, silvery, malleable and ductile metal in the lanthanide group. It is too reactive to be found in native form, and when artificially prepared, it slowly develops a green oxide coating.";
        final File testFile = TempFileProvider.createTempFile("test", "sync");
        FileOutputStream fos = new FileOutputStream(testFile);
        OutputStreamWriter w = new OutputStreamWriter(fos, "UTF-8");
        w.write(TEST_CONTENT);
        w.close();
        
        Calendar cal = GregorianCalendar.getInstance();
      
        cal.add(cal.MONTH, -1);
        final Date lastMonth = cal.getTime();

        final TestContext testContext = new TestContext(); 
        
        log.debug("testUpdate setup");
        
        /**
         * test update : setup
         */
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {
                {
                    QName type = ContentModel.TYPE_CONTENT;
                    
                    String ssdid = targetFolderNodeRef.getId();

                    SyncSetDefinition ssd = SYNC_ADMIN_SERVICE.createTargetSyncSet(ssdid, SRC_REPO_ID, targetFolderNodeRef, false, true, false);
                    assertNotNull("syncSetDefinition is null", ssd);
                    
                    SyncNodeChangesInfoImpl newNode = new SyncNodeChangesInfoImpl(
                            null, 
                            null, 
                            ssd.getId(), 
                            type);
                    newNode.setLocalParentNodeRef(targetFolderNodeRef);
                    newNode.setRemoteVersionLabel(remoteVersionLabel);
                    
                    Map<QName, Serializable> propertyUpdates = new HashMap<QName, Serializable> ();
                    propertyUpdates.put(ContentModel.PROP_NAME, NODE_NAME);
                    newNode.setPropertyUpdates(propertyUpdates);
                  
                    NodeRef newNodeRef = SYNC_SERVICE.create(newNode, false);
                    testContext.nodeRef = newNodeRef;
                    
                    /**
                     * Now validate the newly created node.
                     */
                    assertNotNull("newNodeRef is null", newNodeRef);
                    
                    return null;
                }
            }
        });
        
        /**
         * Update 1, add a couple of properties and a couple of aspects
         * update the content.
         */
        log.debug("update 1");
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {
                QName type = ContentModel.TYPE_CONTENT;
                String ssdid = targetFolderNodeRef.getId();
                
                SyncNodeChangesInfoImpl updates = new SyncNodeChangesInfoImpl(
                        testContext.nodeRef, 
                        null, 
                        ssdid, 
                        type);
                updates.setLocalParentNodeRef(targetFolderNodeRef);
                updates.setRemoteVersionLabel(remoteVersionLabel);
                
                Map<QName, Serializable> propertyUpdates = new HashMap<QName, Serializable> ();
                propertyUpdates.put(ContentModel.PROP_LATITUDE, "123");
                propertyUpdates.put(ContentModel.PROP_ENABLED, true);
                updates.setPropertyUpdates(propertyUpdates);
                
                Set<QName> aspectsAdded = new HashSet<QName>();
                aspectsAdded.add(ContentModel.ASPECT_TEMPORARY);
                aspectsAdded.add(ContentModel.ASPECT_CLASSIFIABLE);
                updates.setAspectsAdded(aspectsAdded);
                
                updates.setRemoteModifiedAt(lastMonth);
                
                Map<QName, CloudSyncContent> content = new HashMap<QName, CloudSyncContent>();
                CloudSyncContentFileImpl fcr = new CloudSyncContentFileImpl(ContentModel.PROP_CONTENT, "text", "UTF-8", testFile );

                content.put(ContentModel.PROP_CONTENT, fcr);
                updates.setContentUpdates(content);
                
                SYNC_SERVICE.update(updates);
                                          
                return null;
                        
            }
        });
        
        /**
         * Validate Update 1
         */
        log.debug("validate update 1");
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {
                assertTrue("temorary aspect not added", NODE_SERVICE.hasAspect(testContext.nodeRef, ContentModel.ASPECT_TEMPORARY));
                assertTrue("classifiable aspect not added", NODE_SERVICE.hasAspect(testContext.nodeRef, ContentModel.ASPECT_CLASSIFIABLE));
                
                Map<QName, Serializable> props = NODE_SERVICE.getProperties(testContext.nodeRef);
       
                assertTrue("enabled property not added", props.containsKey(ContentModel.PROP_ENABLED));
                assertTrue("lattitude property not added", props.containsKey(ContentModel.PROP_LATITUDE));

//                Date modified = (Date)NODE_SERVICE.getProperty(testContext.nodeRef, ContentModel.PROP_MODIFIED);
                
                Date modified = (Date)NODE_SERVICE.getProperty(testContext.nodeRef, SyncModel.PROP_REMOTE_MODIFIED);
                
                System.out.println("remote modified is : " + modified);
                // Don't attempt to do an exact date comparison.   
                // Test validates modified is not in the last 10 seconds.  Alf should be 
                // accurate to about a second if this is going to fail. 
                assertTrue("remote modified is too recent", (modified.getTime() + 10000) < (new Date().getTime()));
      
                ContentReader reader = CONTENT_SERVICE.getReader(testContext.nodeRef, ContentModel.PROP_CONTENT);              
                assertNotNull("New node missing content", reader);
                //assertEquals("mimetype is wrong", "test", reader.getMimetype());
                String readContent = reader.getContentString();
                assertEquals("size is wrong", readContent.length(), TEST_CONTENT.length());
                assertEquals("content is wrong", readContent, TEST_CONTENT);

                   
                return null;
                        
            }
        });
        
        /**
         * Update 2, add a new property, delete a property, delete an aspect.   
         * 
         * Leave modified as is.
         */
        log.debug("update 2");
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {
                QName type = ContentModel.TYPE_CONTENT;
                String ssdid = targetFolderNodeRef.getId();
                
                SyncNodeChangesInfoImpl updates = new SyncNodeChangesInfoImpl(
                        testContext.nodeRef, 
                        null, 
                        ssdid, 
                        type);
                updates.setLocalParentNodeRef(targetFolderNodeRef);
                updates.setRemoteVersionLabel(remoteVersionLabel);
                
                Map<QName, Serializable> propertyUpdates = new HashMap<QName, Serializable> ();
                propertyUpdates.put(ContentModel.PROP_ADDRESSEE, "Mr blobby");
                propertyUpdates.put(ContentModel.PROP_LATITUDE, null);
                updates.setPropertyUpdates(propertyUpdates);
                
                Set<QName> aspectsRemoved = new HashSet<QName>();
                aspectsRemoved.add(ContentModel.ASPECT_TEMPORARY);
                updates.setAspectsRemoved(aspectsRemoved);
                
                Set<QName> aspectsAdded = new HashSet<QName>();
                aspectsAdded.add(ContentModel.ASPECT_EMAILED);
                updates.setAspectsAdded(aspectsAdded);                
                
                SYNC_SERVICE.update(updates);
                                          
                return null;
                        
            }
        });
        
        /**
         * Validate Update 2
         */
        log.debug("validate update 2");
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {
                assertFalse("temorary aspect not removed", NODE_SERVICE.hasAspect(testContext.nodeRef, ContentModel.ASPECT_TEMPORARY));
                assertTrue("classifiable aspect not left alone", NODE_SERVICE.hasAspect(testContext.nodeRef, ContentModel.ASPECT_CLASSIFIABLE));
                assertTrue("emailed aspect not added", NODE_SERVICE.hasAspect(testContext.nodeRef, ContentModel.ASPECT_EMAILED));
                
                Map<QName, Serializable> props = NODE_SERVICE.getProperties(testContext.nodeRef);
                assertTrue("addressee property not added", props.containsKey(ContentModel.PROP_ADDRESSEE));
                assertFalse("lattitude property not removed", props.containsKey(ContentModel.PROP_LATITUDE));

                Date modified = (Date)NODE_SERVICE.getProperty(testContext.nodeRef, ContentModel.PROP_MODIFIED);
                
                assertNotNull("modified is null", modified);
                System.out.println("modified is : " + modified);
                // Don't attempt to do an exact date comparison.   
                // Alf should be accurate to about a second.  
                Date now = new Date();
                assertTrue("modified is not recent enought", modified.getTime() + 3000 > now.getTime() );
      
                   
                return null;
                        
            }
        });
        
        /**
         * Force a conflict on the test node node 
         */
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {   
                NODE_SERVICE.setProperty(testContext.nodeRef, ContentModel.PROP_DESCRIPTION, "Update"); 
                return null;
            }
        });
        
        log.debug("update 1");
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {
                QName type = ContentModel.TYPE_CONTENT;
                String ssdid = targetFolderNodeRef.getId();
                
                SyncNodeChangesInfoImpl updates = new SyncNodeChangesInfoImpl(
                        testContext.nodeRef, 
                        null, 
                        ssdid, 
                        type);
                updates.setLocalParentNodeRef(targetFolderNodeRef);
                updates.setRemoteVersionLabel(remoteVersionLabel);
                
                Map<QName, Serializable> propertyUpdates = new HashMap<QName, Serializable> ();
                propertyUpdates.put(ContentModel.PROP_LATITUDE, "321");
                updates.setPropertyUpdates(propertyUpdates);
                try
                {
                    
                    SYNC_SERVICE.update(updates);
                    fail ("Expected a concurrency failure");
                } 
                catch(ConcurrentModificationException  ce)
                {
                    // expect to go here
                }
                                          
                return null;
                        
            }
        });
        
        log.debug("testUpdate: clean up");
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {
                QName type = ContentModel.TYPE_CONTENT;
                String ssdid = targetFolderNodeRef.getId();
                
                SyncNodeChangesInfoImpl delete = new SyncNodeChangesInfoImpl(
                        testContext.nodeRef, 
                        null, 
                        ssdid, 
                        type);
                SYNC_SERVICE.delete(delete, true); 
                return null;
            }
         });
    }
    
    @Test public void testDealWithConflictInAppropriteManner() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {
//                SyncNodeChangesInfo conflict = new SyncNodeChangesInfoImpl(null, null, null, null);
//                ConflictResponse response = SYNC_SERVICE.dealWithConflictInAppropriateManner(conflict); 
                return null;
            }
        });
    }
    


}
