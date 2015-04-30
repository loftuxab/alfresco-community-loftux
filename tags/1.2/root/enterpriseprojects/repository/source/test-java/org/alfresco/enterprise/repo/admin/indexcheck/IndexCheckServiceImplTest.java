/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.admin.indexcheck;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.transaction.Status;
import javax.transaction.UserTransaction;

import junit.framework.TestCase;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.domain.node.NodeDAO;
import org.alfresco.repo.jscript.ClasspathScriptLocation;
import org.alfresco.repo.management.subsystems.ChildApplicationContextFactory;
import org.alfresco.repo.node.index.AbstractReindexComponent.InIndex;
import org.alfresco.repo.node.index.IndexTransactionTracker;
import org.alfresco.repo.node.index.NodeIndexer;
import org.alfresco.repo.search.impl.lucene.fts.FullTextSearchIndexer;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.repo.usage.UserUsageTrackingComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.ScriptLocation;
import org.alfresco.service.cmr.repository.ScriptService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ApplicationContextHelper;
import org.springframework.context.ApplicationContext;

/**
 * Alfresco DM Index check service implementation test
 * 
 * @author janv
 */
public class IndexCheckServiceImplTest extends TestCase
{
    private static ApplicationContext ctx = ApplicationContextHelper.getApplicationContext();

    private IndexCheckService admIndexCheckService;

    private NodeIndexer nodeIndexer;

    private NodeService nodeService;

    protected TransactionService transactionService;

    protected RetryingTransactionHelper txnHelper;

    private ScriptService scriptService;

    private FullTextSearchIndexer luceneFTS;    

    private StoreRef storeRef;

    private NodeRef rootNodeRef;

    private String TEST_USER_ADMIN = "admin";

    private static final long SLEEP_PRE_MSECS = 2000;

    private static final long SLEEP_POST_MSECS = 5000;
    
    @SuppressWarnings("unused")
    private static final long TIME_30_MINS_IN_MSECS = (1000 * 30);
    
    private static final long TIME_1_DAY_IN_MSECS   = (1000 * 60 * 60 * 24);
    
    // improves build/test performance when checking all (or most) txns 
    // - either ignore all stores except this test store and/or check from only more recently (eg. 30 mins ago instead of 1 day ago)
    private static boolean ignoreAllStoresExceptTestStore = true;
    private static final long CHECK_BACK_FROM_NOW_IN_MSECS = TIME_1_DAY_IN_MSECS;
    
    UserTransaction tx;
    
    private UserUsageTrackingComponent userUsageTrackingComponent;

    private NodeDAO nodeDAO;

    @Override
    protected void setUp() throws Exception
    {
        ChildApplicationContextFactory luceneSubSystem = (ChildApplicationContextFactory) ctx.getBean("buildonly");
        this.admIndexCheckService = (IndexCheckService)  luceneSubSystem.getApplicationContext().getBean("search.admIndexCheckService");
        this.nodeIndexer = (NodeIndexer) ctx.getBean("nodeIndexer");
        this.nodeService = (NodeService) ctx.getBean("nodeService");
        this.transactionService = (TransactionService) ctx.getBean("transactionComponent");
        this.txnHelper = (RetryingTransactionHelper) ctx.getBean("retryingTransactionHelper");
        this.scriptService = (ScriptService) ctx.getBean("ScriptService");
        this.userUsageTrackingComponent = (UserUsageTrackingComponent) ctx.getBean("userUsageTrackingComponent");
        this.luceneFTS = (FullTextSearchIndexer)ctx.getBean("LuceneFullTextSearchIndexer");
        this.nodeDAO = (NodeDAO)ctx.getBean("nodeDAO");
        
        // If FTS kicks in at the wrong moment, it can skew the test results. Temporarily disable it during the test
        this.luceneFTS.pause();
		
        
        AuthenticationUtil.setFullyAuthenticatedUser(TEST_USER_ADMIN);
        
        tx = this.transactionService.getUserTransaction();
        tx.begin();
        
        userUsageTrackingComponent.setEnabled(false);
        
        // Create the store and get the root node reference
        this.storeRef = this.nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_" + System.currentTimeMillis());
        this.rootNodeRef = this.nodeService.getRootNode(storeRef);
        
        List<StoreRef> storeRefs = nodeService.getStores();
        
        tx.commit();
        
        if (ignoreAllStoresExceptTestStore)
        {
            List<String> storesToIgnore = new ArrayList<String>(storeRefs.size());
            for (StoreRef sr : storeRefs)
            {
                if (! sr.equals(this.storeRef))
                {
                    storesToIgnore.add(sr.toString());
                }
            }
            
            IndexTransactionTracker admIndexTrackerComponent = (IndexTransactionTracker) luceneSubSystem.getApplicationContext().getBean("search.admIndexTrackerComponent");
            admIndexTrackerComponent.setStoresToIgnore(storesToIgnore);
        }
        
        //tx = this.transactionService.getUserTransaction();
        //tx.begin();

    }
    
    @Override
    protected void tearDown() throws Exception
    {
        userUsageTrackingComponent.setEnabled(true);
        if (tx.getStatus() == Status.STATUS_ACTIVE)
        {
            tx.rollback();
        }
        AuthenticationUtil.clearCurrentSecurityContext();
        // Resume FTS as normal
        this.luceneFTS.resume();
        super.tearDown();
    }

    public void testForceReindex() throws Exception
    {
        // fix initial state

        reindexFromTxn(1);

        // wait for reindex to complete

        Thread.sleep(20000);
        System.out.println(admIndexCheckService.getReindexProgress());
        while (!admIndexCheckService.getReindexProgress().equals("No reindex in progress"))
        {
            System.out.println(admIndexCheckService.getReindexProgress());
            Thread.sleep(20000);
        }
    }

    /**
     * Test
     */
    public void testCheckLastTxn() throws Exception
    {
        final NodeRef.Status status1 = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<NodeRef.Status>()
        {
            public NodeRef.Status execute() throws Exception
            {
                // create new node
                NodeRef n1 = nodeService.createNode(rootNodeRef, ContentModel.ASSOC_CHILDREN, QName.createQName("{namespace}one"), ContentModel.TYPE_FOLDER).getChildRef();
                assertNotNull(n1);

                return nodeService.getNodeStatus(n1);
            }
        });

        checkTxn(status1.getDbTxnId(), true); // in-sync

        final NodeRef.Status lastcreate = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<NodeRef.Status>()
        {
            public NodeRef.Status execute() throws Exception
            {
                nodeIndexer.setDisabled(true);

                // create another new node
                NodeRef n2 = nodeService.createNode(rootNodeRef, ContentModel.ASSOC_CHILDREN, QName.createQName("{namespace}one"), ContentModel.TYPE_FOLDER).getChildRef();
                assertNotNull(n2);

                nodeIndexer.setDisabled(false);

                return nodeService.getNodeStatus(n2);
            }
        });

        IndexTxnInfo indexTxnInfo = checkTxn(lastcreate.getDbTxnId(), false); // as expected, out-of-sync !

        reindexFromTxn(indexTxnInfo.getLastMissingTxn().getId());

        checkTxn(lastcreate.getDbTxnId(), true); // in-sync again

        final NodeRef.Status lastupdate = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<NodeRef.Status>()
        {
            public NodeRef.Status execute() throws Exception
            {
                nodeIndexer.setDisabled(true);

                nodeService.addAspect(lastcreate.getNodeRef(), ContentModel.ASPECT_AUTHOR, null);

                nodeIndexer.setDisabled(false);

                
                return nodeService.getNodeStatus(lastcreate.getNodeRef());
            }
        });

        indexTxnInfo = checkTxn(lastupdate.getDbTxnId(), false); // as expected, out-of-sync !

        reindexFromTxn(indexTxnInfo.getLastMissingTxn().getId());

        checkTxn(lastupdate.getDbTxnId(), true); // in-sync again

        final NodeRef.Status lastdeleted = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<NodeRef.Status>()
        {
            public NodeRef.Status execute() throws Exception
            {
                nodeIndexer.setDisabled(true);

                nodeService.deleteNode(lastupdate.getNodeRef());

                nodeIndexer.setDisabled(false);

                return nodeService.getNodeStatus(lastupdate.getNodeRef());
            }
        });

        indexTxnInfo = checkTxn(lastdeleted.getDbTxnId(), false); // as expected, out-of-sync !

        reindexFromTxn(indexTxnInfo.getLastMissingTxn().getId());

        checkTxn(lastdeleted.getDbTxnId(), true); // in-sync again
    }

    public void testCheckAllTxns() throws Exception
    {
        // start with clean slate

        // note: commented-out this reindex since it will/may slow the build (if it has a lot of txns)
        // long dayInPast = System.currentTimeMillis() - (1000 * 60 * 60 * 24);
        // reindexFromTime(dayInPast);

        checkAllTxns(true);
    }

    public void testReindexFromTxn1() throws Exception
    {
        final NodeRef.Status status1 = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<NodeRef.Status>()
        {
            public NodeRef.Status execute() throws Exception
            {
                // create node
                NodeRef n1 = nodeService.createNode(rootNodeRef, ContentModel.ASSOC_CHILDREN, QName.createQName("{namespace}one"), ContentModel.TYPE_FOLDER).getChildRef();
                assertNotNull(n1);

                return nodeService.getNodeStatus(n1);
            }
        });

        checkTxn(status1.getDbTxnId(), true); // in-sync

        final NodeRef.Status created = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<NodeRef.Status>()
        {
            public NodeRef.Status execute() throws Exception
            {
                nodeIndexer.setDisabled(true);

                // create another node
                NodeRef n2 = nodeService.createNode(rootNodeRef, ContentModel.ASSOC_CHILDREN, QName.createQName("{namespace}one"), ContentModel.TYPE_FOLDER).getChildRef();
                assertNotNull(n2);

                nodeIndexer.setDisabled(false);

                return nodeService.getNodeStatus(n2);
            }
        });

        final IndexTxnInfo indexTxnInfo = checkTxn(created.getDbTxnId(), false); // as expected, out-of-sync !

        reindexFromTxn(indexTxnInfo.getLastMissingTxn().getId());

        checkTxn(created.getDbTxnId(), true); // in-sync again
    }

    public void testReindexFromTxn2() throws Exception
    {
        long startTime = System.currentTimeMillis();

        final int TXN_COUNT = 20;

        ArrayList<NodeRef.Status> firstBatch = new ArrayList<NodeRef.Status>();
        for (int i = 1; i < TXN_COUNT; i++)
        {
            NodeRef.Status created = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<NodeRef.Status>()
            {
                public NodeRef.Status execute() throws Exception
                {
                    // create new node
                    NodeRef n1 = nodeService.createNode(rootNodeRef, ContentModel.ASSOC_CHILDREN, QName.createQName("{namespace}one"), ContentModel.TYPE_FOLDER).getChildRef();
                    assertNotNull(n1);

                    return nodeService.getNodeStatus(n1);
                }
            });
            firstBatch.add(created);
        }

        for(NodeRef.Status ns : firstBatch)
        {
            checkTxn(ns.getDbTxnId(), true);
        }
        

        nodeIndexer.setDisabled(true);

        ArrayList<NodeRef.Status> secondBatch = new ArrayList<NodeRef.Status>();
        for (int i = 1; i < TXN_COUNT; i++)
        {
            NodeRef.Status created = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<NodeRef.Status>()
            {
                public NodeRef.Status execute() throws Exception
                {
                    // create another node
                    NodeRef n2 = nodeService.createNode(rootNodeRef, ContentModel.ASSOC_CHILDREN, QName.createQName("{namespace}two"), ContentModel.TYPE_FOLDER).getChildRef();
                    assertNotNull(n2);

                    return nodeService.getNodeStatus(n2);
                }
            });
            secondBatch.add(created);
        }

        nodeIndexer.setDisabled(false);

        for(NodeRef.Status ns : secondBatch)
        {
            checkTxn(ns.getDbTxnId(), false);
        }

        long splitTime = System.currentTimeMillis();

        ArrayList<NodeRef.Status> thirdBatch = new ArrayList<NodeRef.Status>();
        for (int i = 1; i < TXN_COUNT; i++)
        {
            NodeRef.Status created = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<NodeRef.Status>()
            {
                public NodeRef.Status execute() throws Exception
                {
                    // create another node
                    NodeRef n3 = nodeService.createNode(rootNodeRef, ContentModel.ASSOC_CHILDREN, QName.createQName("{namespace}three"), ContentModel.TYPE_FOLDER).getChildRef();
                    assertNotNull(n3);

                    return nodeService.getNodeStatus(n3);
                }
            });
            thirdBatch.add(created);
        }

        for(NodeRef.Status ns : thirdBatch)
        {
            checkTxn(ns.getDbTxnId(), true);
        }
        
       
        final IndexTxnInfo indexTxnInfo = checkTxnFrom(false, startTime); // as expected, out-of-sync !

        reindexFromTime(indexTxnInfo.getFirstMissingTxn().getCommitTimeMs());

        for(NodeRef.Status ns : firstBatch)
        {
            checkTxn(ns.getDbTxnId(), true);
        }
        for(NodeRef.Status ns : secondBatch)
        {
            checkTxn(ns.getDbTxnId(), true);
        }
        
        for(NodeRef.Status ns : thirdBatch)
        {
            checkTxn(ns.getDbTxnId(), true);
        }
    }

    public void testCheckTxnsFrom()
    {
        long now = System.currentTimeMillis();
        checkTxnFrom(true, now); // now - nothing to check (if this is beyond the maxTxnTime)
        
        long from = now - CHECK_BACK_FROM_NOW_IN_MSECS;
        checkTxnFrom(true, from);
        
        long dayInFuture = now + TIME_1_DAY_IN_MSECS;
        checkTxnFrom(true, dayInFuture); // future - nothing to check, since this is beyond the maxTxnTime
    }

    public void testCheckTxnsFromTo()
    {
        long now = System.currentTimeMillis();
        
        long from = now - CHECK_BACK_FROM_NOW_IN_MSECS;
        checkTxnFromTo(true, from, now);
    }

    public void testCheckNodeStatus() throws Exception
    {
        long startTime = System.currentTimeMillis();

        final NodeRef.Status status1 = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<NodeRef.Status>()
        {
            public NodeRef.Status execute() throws Exception
            {
                // create new node
                NodeRef n1 = nodeService.createNode(rootNodeRef, ContentModel.ASSOC_CHILDREN, QName.createQName("{namespace}one"), ContentModel.TYPE_FOLDER).getChildRef();
                assertNotNull(n1);

                // get node status txn info
                IndexTxnInfo indexTxnInfo = admIndexCheckService.getStatusForNode(n1);
                assertNotNull(indexTxnInfo);
                assertEquals(indexTxnInfo.toString(), 0, indexTxnInfo.getMissingCount()); // txn not missing -> in
                // index
                System.out.println(indexTxnInfo);

                assertNotNull(indexTxnInfo.getNodeList());
                assertEquals(indexTxnInfo.toString(), 1, indexTxnInfo.getNodeList().size());

                assertEquals(indexTxnInfo.getNodeList().get(0).toString(), InIndex.YES, indexTxnInfo.getNodeList().get(0).getInIndex()); // nodeRef
                // present
                // in
                // index
                System.out.println(indexTxnInfo.getNodeList().get(0));

                return nodeService.getNodeStatus(n1);
            }
        });

        IndexTxnInfo indexTxnInfo = checkTxn(status1.getDbTxnId(), true); // in-sync

        final NodeRef.Status status2 = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<NodeRef.Status>()
        {
            public NodeRef.Status execute() throws Exception
            {
                nodeIndexer.setDisabled(true);

                // create another new node
                NodeRef n2 = nodeService.createNode(rootNodeRef, ContentModel.ASSOC_CHILDREN, QName.createQName("{namespace}one"), ContentModel.TYPE_FOLDER).getChildRef();

                // get node status txn info
                IndexTxnInfo indexTxnInfo = admIndexCheckService.getStatusForNode(n2);
                assertNotNull(indexTxnInfo);
                assertFalse(indexTxnInfo.toString(), indexTxnInfo.getMissingCount() == 0); // txn missing -> not in
                // index
                System.out.println(indexTxnInfo);

                assertNotNull(indexTxnInfo.getNodeList());
                assertEquals(indexTxnInfo.toString(), 1, indexTxnInfo.getNodeList().size());

                assertEquals(indexTxnInfo.getNodeList().get(0).toString(), InIndex.NO, indexTxnInfo.getNodeList().get(0).getInIndex()); // nodeRef
                // not
                // present
                // in
                // index
                System.out.println(indexTxnInfo.getNodeList().get(0));

                nodeIndexer.setDisabled(false);

                return nodeService.getNodeStatus(n2);
            }
        });

        indexTxnInfo = checkTxn(status2.getDbTxnId(), false); // as expected, out-of-sync !
        assertTrue(indexTxnInfo.toString(), indexTxnInfo.getLastMissingTxn().getId().equals(status2.getDbTxnId()));

        reindexFromTime(indexTxnInfo.getFirstMissingTxn().getCommitTimeMs());

        indexTxnInfo = checkTxn(status2.getDbTxnId(), true); // as expected, added!
    }

    public void testCheckNodeStatusForTxn()
    {
        
        final IndexTxnInfo indexTxnInfo1 = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<IndexTxnInfo>()
        {
            public IndexTxnInfo execute() throws Exception
            {
                // create new node
                NodeRef n1 = nodeService.createNode(rootNodeRef, ContentModel.ASSOC_CHILDREN, QName.createQName("{namespace}one"), ContentModel.TYPE_FOLDER).getChildRef();
                assertNotNull(n1);

                // create another node
                NodeRef n2 = nodeService.createNode(rootNodeRef, ContentModel.ASSOC_CHILDREN, QName.createQName("{namespace}two"), ContentModel.TYPE_FOLDER).getChildRef();
                assertNotNull(n2);

                // get node status txn info
                IndexTxnInfo indexTxnInfo = admIndexCheckService.getStatusForNode(n1);
                assertNotNull(indexTxnInfo);
                assertEquals(indexTxnInfo.toString(), 0, indexTxnInfo.getMissingCount()); // txn not missing -> in
                // index
                System.out.println(indexTxnInfo);

                return indexTxnInfo;
            }
        });

        IndexTxnInfo indexTxnInfo = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<IndexTxnInfo>()
        {
            public IndexTxnInfo execute() throws Exception
            {
                return admIndexCheckService.getStatusForTxnNodes(indexTxnInfo1.getLastProcessedTxn().getId());
            }
        });

        System.out.println(indexTxnInfo);

        assertNotNull(indexTxnInfo.getNodeList());
        assertEquals(indexTxnInfo.toString(), 2, indexTxnInfo.getNodeList().size());

        if (indexTxnInfo.getNodeList() != null)
        {
            for (IndexNodeInfo indexNodeInfo : indexTxnInfo.getNodeList())
            {
                System.out.println(indexNodeInfo);
                assertEquals(indexNodeInfo.toString(), InIndex.YES, indexNodeInfo.getInIndex()); // nodeRef present
                // in index
            }
        }
    }

    private IndexTxnInfo checkTxn(final Long txnId, final boolean inIndex)
    {
        return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<IndexTxnInfo>()
        {
            public IndexTxnInfo execute() throws Exception
            {
                IndexTxnInfo indexTxnInfo;
                if(txnId == null)
                {
                    indexTxnInfo = admIndexCheckService.checkLastTxn();
                }
                else
                {
                    indexTxnInfo = admIndexCheckService.checkTxn(txnId);
                }
                 
                // If the transaction is empty there is nothing we can do
                if(nodeDAO.getTxnChanges(txnId).size() > 0)
                {
                    System.out.println("checked last txn info: " + indexTxnInfo);
                    assertEquals(indexTxnInfo.toString(), inIndex, (indexTxnInfo.getMissingCount() == 0));
                }

                return indexTxnInfo;
            }
        }, false, true);
    }

    private IndexTxnInfo checkAllTxns(final boolean inIndex)
    {
        return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<IndexTxnInfo>()
        {
            public IndexTxnInfo execute() throws Exception
            {
                IndexTxnInfo indexTxnInfo = admIndexCheckService.checkAllTxns();
                System.out.println("checked all txns info: " + indexTxnInfo);

                assertEquals(indexTxnInfo.toString(), inIndex, (indexTxnInfo.getMissingCount() == 0));

                return indexTxnInfo;
            }
        }, false, true);
    }

    private IndexTxnInfo checkTxnFrom(final boolean inIndex, final long fromTime)
    {
        return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<IndexTxnInfo>()
        {
            public IndexTxnInfo execute() throws Exception
            {
                IndexTxnInfo indexTxnInfo = admIndexCheckService.checkTxnsFromTime(new Date(fromTime));
                System.out.println("checked txn from info: " + indexTxnInfo);

                if (indexTxnInfo == null)
                {
                    assertTrue("indexTxnInfo=null", inIndex); // out-of-range is effectively in-sync (ie. nothing to
                    // check)
                }
                else
                {
                    assertEquals(indexTxnInfo.toString(), inIndex, (indexTxnInfo.getMissingCount() == 0));
                }

                return indexTxnInfo;
            }
        }, false, true);
    }

    private IndexTxnInfo checkTxnFromTo(final boolean inIndex, final long fromTime, final long toTime)
    {
        return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<IndexTxnInfo>()
        {
            public IndexTxnInfo execute() throws Exception
            {
                IndexTxnInfo indexTxnInfo = admIndexCheckService.checkTxnsFromToTime(new Date(fromTime), new Date(toTime));
                System.out.println("checked txn from/to info: " + indexTxnInfo);

                if (indexTxnInfo == null)
                {
                    assertTrue("indexTxnInfo=null", inIndex); // out-of-range is effectively in-sync (ie. nothing to
                    // check)
                }
                else
                {
                    assertEquals(indexTxnInfo.toString(), inIndex, (indexTxnInfo.getMissingCount() == 0));
                }

                return indexTxnInfo;
            }
        }, false, true);
    }

    private void reindexFromTxn(final long txnId) throws Exception
    {
        // Allow for default ReIndexLag of 1 sec (1000 ms)
        Thread.sleep(SLEEP_PRE_MSECS);

        transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>()
        {
            public Object execute() throws Exception
            {
                admIndexCheckService.reindexFromTxn(txnId).join();

                return null;
            }
        });
    }

    private void reindexFromTime(final long fromTime) throws Exception
    {
        // Allow for default ReIndexLag of 1 sec (1000 ms)
        Thread.sleep(SLEEP_PRE_MSECS);

        transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>()
        {
            public Object execute() throws Exception
            {
                admIndexCheckService.reindexFromTime(new Date(fromTime)).join();

                return null;
            }
        });

    }

    // == Test the JavaScript API ==

    public void testJSAPI() throws Exception
    {
        // Execute the unit test script
        ScriptLocation location = new ClasspathScriptLocation("org/alfresco/enterprise/repo/admin/indexcheck/script/test_indexCheckService.js");

        String result = (String) scriptService.executeScript(location, new HashMap<String, Object>(0));

        // Check the result and fail if message returned
        if (result != null && result.length() != 0)
        {
            fail("The activity service test JS script failed: " + result);
        }
    }
}
