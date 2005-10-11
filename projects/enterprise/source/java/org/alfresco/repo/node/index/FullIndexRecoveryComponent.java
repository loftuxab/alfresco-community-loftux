/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.node.index;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.domain.NodeStatus;
import org.alfresco.repo.search.Indexer;
import org.alfresco.repo.search.impl.lucene.fts.FullTextSearchIndexer;
import org.alfresco.repo.transaction.TransactionUtil;
import org.alfresco.repo.transaction.TransactionUtil.TransactionWork;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.CacheMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Ensures that the FTS indexing picks up on any outstanding documents that
 * require indexing.
 * <p>
 * This component must be used as a singleton (one per VM) and may only be
 * called to reindex once.  It will start a thread that processes all available
 * transactions and keeps checking to ensure that the index is up to date with
 * the latest database changes.
 * <p>
 * <b>The following points are important:</b>
 * <ul>
 *   <li>
 *       By default, the Hibernate L2 cache is used during processing.
 *       This can be disabled by either disabling the L2 cache globally
 *       for the server (not recommended) or by setting the
 *       {@link #setUseL2Cache(boolean) useL2Cache} property.  If the
 *       database is static then the L2 cache should be left enabled
 *       for maximum performance.
 *   </li>
 *   <li>
 *       This process should not run continuously on a live
 *       server as it would be performing unecessary work.
 *       If it was left running, however, it would not
 *       lead to data corruption or such-like.  Use the
 *       {@link #setRunContinuously(boolean) runContinuously} property
 *       to change this behaviour.
 *   </li>
 * </ul>
 * 
 * @author Derek Hulley
 */
public class FullIndexRecoveryComponent extends HibernateDaoSupport implements IndexRecovery
{
    public static final String QUERY_GET_NEXT_CHANGE_TXN_IDS = "node.GetNextChangeTxnIds";
    public static final String QUERY_GET_CHANGED_NODE_STATUSES = "node.GetChangedNodeStatuses";
    public static final String QUERY_GET_CHANGED_NODE_STATUSES_COUNT = "node.GetChangedNodeStatusesCount";
    
    private static final String START_TXN_ID = "000";
    
    private static Log logger = LogFactory.getLog(FullIndexRecoveryComponent.class);
    
    /** ensures that this process is kicked off once per VM */
    private static boolean started = false;
    /**The current transaction ID being processed */
    private static String currentTxnId = null;
    
    /** provides transactions to atomically index each missed transaction */
    private TransactionService transactionService;
    /** the component to index the node hierarchy */
    private Indexer indexer;
    /** the FTS indexer that we will prompt to pick up on any un-indexed text */
    private FullTextSearchIndexer ftsIndexer;
    /** the component providing searches of the indexed nodes */
    private SearchService searcher;
    /** the component giving direct access to <b>node</b> instances */
    private NodeService nodeService;
    /** the workspaces to reindex */
    private List<StoreRef> storeRefs;
    /** set this on to keep checking for new transactions and never stop */
    private boolean runContinuously;
    /** controls whether the L2 cache should be used or not */
    private boolean useL2Cache;
    
    /**
     * @return Returns the ID of the current (or last) transaction processed
     */
    public static String getCurrentTransactionId()
    {
        return currentTxnId;
    }

    public FullIndexRecoveryComponent()
    {
        this.storeRefs = new ArrayList<StoreRef>(2);
        
        this.runContinuously = false;
        this.useL2Cache = true;
    }
    
    /**
     * @param transactionService provide transactions to index each missed transaction
     */
    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    /**
     * @param indexer the indexer that will be index
     */
    public void setIndexer(Indexer indexer)
    {
        this.indexer = indexer;
    }
    
    /**
     * @param ftsIndexer the FTS background indexer
     */
    public void setFtsIndexer(FullTextSearchIndexer ftsIndexer)
    {
        this.ftsIndexer = ftsIndexer;
    }

    /**
     * @param searcher component providing index searches
     */
    public void setSearcher(SearchService searcher)
    {
        this.searcher = searcher;
    }

    /**
     * @param nodeService provides information about nodes for indexing
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    /**
     * Set the workspaces that need reindexing
     * 
     * @param storeRefStrings a list of strings representing store references
     */
    public void setStores(List<String> storeRefStrings)
    {
        storeRefs.clear();
        for (String storeRefStr : storeRefStrings)
        {
            StoreRef storeRef = new StoreRef(storeRefStr);
            storeRefs.add(storeRef);
        }
    }

    /**
     * Set this to ensure that the process continously checks for new transactions.
     * If not, it will permanently terminate once it catches up with the current
     * transactions.
     * 
     * @param runContinuously true to never cease looking for new transactions
     */
    public void setRunContinuously(boolean runContinuously)
    {
        this.runContinuously = runContinuously;
    }
    
    /**
     * Set this to false if the server this process is running in is NOT
     * the only processing modifying the underlying data.
     * <p>
     * By default, it is should be <code>true</code>.
     * 
     * @param useL2Cache true to use the L2 cache
     */
    public void setUseL2Cache(boolean useL2Cache)
    {
        this.useL2Cache = useL2Cache;
    }

    /**
     * Ensure that the index is up to date with the current state of the persistence layer.
     * The full list of unique transaction change IDs is retrieved and used to detect
     * which are not present in the index.  All the node changes and deletions for the
     * remaining transactions are then indexed.
     */
    public synchronized void reindex()
    {
        if (FullIndexRecoveryComponent.started)
        {
            throw new AlfrescoRuntimeException
                    ("Only one FullIndexRecoveryComponent may be used per VM and it may only be called once");
        }
        // check that no attempt is made to reuse this component
        // set the state of the reindex
        FullIndexRecoveryComponent.currentTxnId = START_TXN_ID;
        
        // start a stateful thread that will begin processing the reindexing the transactions
        Runnable runnable = new ReindexRunner();
        Thread reindexThread = new Thread(runnable);
        // make it a daemon thread
        reindexThread.setDaemon(true);
        // it should not be a high priority
        reindexThread.setPriority(Thread.MIN_PRIORITY);
        // start it
        reindexThread.start();
        
        // ensure that we mark the txn as started
        FullIndexRecoveryComponent.started = true;
        
        // work to mark the stores for full text reindexing
        TransactionWork<Object> ftsReindexWork = new TransactionWork<Object>()
        {
            public Object doWork()
            {
                // reindex each store
                for (StoreRef storeRef : storeRefs)
                {
                    // check if the store exists
                    if (!nodeService.exists(storeRef))
                    {
                        // store does not exist
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("Skipping reindex of non-existent store: " + storeRef);
                        }
                        continue;
                    }
                    
                    // prompt FTS to reindex the store
                    ftsIndexer.requiresIndex(storeRef);
                }
                // done
                if (logger.isDebugEnabled())
                {
                    logger.debug("Prompted FTS index on stores: " + storeRefs);
                }
                return null;
            }
        };
        TransactionUtil.executeInNonPropagatingUserTransaction(transactionService, ftsReindexWork);
        // all further FTS indexing will be done by individual node index changes
    }
    
    /**
     * Stateful thread runnable that executes reindex calls.
     * 
     * @see FullIndexRecoveryComponent#reindexImpl()
     * 
     * @author Derek Hulley
     */
    private class ReindexRunner implements Runnable
    {
        public void run()
        {
            // keep this thread going permanently
            while (true)
            {
                List<String> txnsIndexed = FullIndexRecoveryComponent.this.reindexImpl();
                // check if the process should terminate
                if (txnsIndexed.size() == 0 && !runContinuously)
                {
                    // the thread has caught up with all the available work and should not
                    // run continuously
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Thread quitting - no more available indexing to do: \n" +
                                "   last txn: " + FullIndexRecoveryComponent.getCurrentTransactionId());
                    }
                    break;
                }
                // brief pause
                try
                {
                    synchronized(FullIndexRecoveryComponent.this)
                    {
                        FullIndexRecoveryComponent.this.wait(1000L);
                    }
                }
                catch (InterruptedException e)
                {
                    // ignore
                }
            }
        }
    }
    
    /**
     * @return Returns the transaction ID just reindexed, i.e. where some work was performed
     */
    private List<String> reindexImpl()
    {
        // get a list of all transactions still requiring a check
        List<String> txnsToCheck = getNextChangeTxnIds(FullIndexRecoveryComponent.currentTxnId);
        
        // loop over each transaction
        for (String changeTxnId : txnsToCheck)
        {
            reindex(changeTxnId);
        }
        // done
        return txnsToCheck;
    }

    /**
     * Reindexes changes specific to the change transaction ID.
     */
    private void reindex(String changeTxnId)
    {
        /*
         * This must execute each within its own transaction.
         * The cache size is therefore not an issue.
         */
        UserTransaction txn = transactionService.getNonPropagatingUserTransaction();
        try
        {
            txn.begin();
            
            // perform the work in a Hibernate callback
            HibernateCallback callback = new ReindexCallback(changeTxnId);
            getHibernateTemplate().execute(callback);
            
            // commit the change - it is a commit to the index only
            txn.commit();
        }
        catch (Throwable e)
        {
            try
            {
                if (txn.getStatus() == Status.STATUS_ACTIVE)
                {
                    txn.rollback();
                }
            }
            catch (Throwable ee)
            {
                ee.printStackTrace();
            }
            logger.error("Transaction reindex failed: \n" +
                    "   txn: " + changeTxnId,
                    e);
        }
        finally
        {
            // Up the current transaction now, in case the process fails at this point.
            // This will prevent the transaction from being processed again.
            // This applies to failures as well, which should be dealt with externally
            //      and having the entire process start again, e.g. such as a system reboot
            currentTxnId = changeTxnId;
        }
    }
    
    /**
     * Stateful inner class that implements a single reindex call for a given store
     * and transaction.
     * <p>
     * It must be called within its own transaction.
     * 
     * @author Derek Hulley
     */
    private class ReindexCallback implements HibernateCallback
    {
        private final String changeTxnId;
        
        public ReindexCallback(String changeTxnId)
        {
            this.changeTxnId = changeTxnId;
        }
        
        /**
         * Switches the L2 cache of, if necessary.
         * 
         * @see #reindex(StoreRef, String)
         */
        public Object doInHibernate(Session session)
        {
            if (!useL2Cache)
            {
                // we want to start with a clean L2 cache
                getSession().setCacheMode(CacheMode.IGNORE);
            }
            
            // reindex each store
            for (StoreRef storeRef : storeRefs)
            {
                if (!nodeService.exists(storeRef))
                {
                    // the store is not present
                    continue;
                }
                // reindex for store
                reindex(storeRef, changeTxnId);
            }
            // done
            return null;
        }
        
        private void reindex(StoreRef storeRef, String changeTxnId)
        {
            // check if we need to perform this operation
            SearchParameters sp = new SearchParameters();
            sp.addStore(storeRef);

            // search for it in the index
            String query = "TX:\"" + changeTxnId + "\"";
            sp.setLanguage(SearchService.LANGUAGE_LUCENE);
            sp.setQuery(query);
            ResultSet results = searcher.query(sp);
            // did the index have any of these changes?
            if (results.length() > 0)
            {
                // the transaction has an entry in the index - assume that it was
                // atomically correct
                if (logger.isDebugEnabled())
                {
                    logger.debug("Transaction present in index - no indexing required: \n" +
                            "   store: " + storeRef + "\n" +
                            "   txn: " + changeTxnId);
                }
                return;
            }
            // the index has no record of this
            // were there any changes, or is it all just deletions?
            int changedCount = getChangedNodeStatusesCount(storeRef, changeTxnId);
            if (changedCount == 0)
            {
                // no nodes were changed in the transaction, i.e. they are only deletions
                // the index is quite right not to have any entries for the transaction
                if (logger.isDebugEnabled())
                {
                    logger.debug("Transaction only has deletions - no indexing required: \n" +
                            "   store: " + storeRef + "\n" +
                            "   txn: " + changeTxnId);
                }
                return;
            }

            // process the deletions relevant to the txn and the store
            List<NodeStatus> deletedNodeStatuses = getDeletedNodeStatuses(storeRef, changeTxnId);
            for (NodeStatus status : deletedNodeStatuses)
            {
                NodeRef nodeRef = new NodeRef(storeRef, status.getKey().getGuid());
                // only the child node ref is relevant
                ChildAssociationRef assocRef = new ChildAssociationRef(
                        ContentModel.ASSOC_CHILDREN,
                        null,
                        null,
                        nodeRef);
                indexer.deleteNode(assocRef);
            }
            
            // process additions
            List<NodeStatus> changedNodeStatuses = getChangedNodeStatuses(storeRef, changeTxnId);
            for (NodeStatus status : changedNodeStatuses)
            {
                NodeRef nodeRef = new NodeRef(storeRef, status.getKey().getGuid());
                // get the primary assoc for the node
                ChildAssociationRef primaryAssocRef = nodeService.getPrimaryParent(nodeRef);
                // reindex
                indexer.createNode(primaryAssocRef);
            }
            
            // done
            if (logger.isDebugEnabled())
            {
                logger.debug("Transaction reindexed: \n" +
                        "   store: " + storeRef + "\n" +
                        "   txn: " + changeTxnId + "\n" +
                        "   deletions: " + deletedNodeStatuses.size() + "\n" +
                        "   modifications: " + changedNodeStatuses.size());
            }
        }
    };

    /**
     * Retrieve all transaction IDs that are greater than the given transaction ID.
     * 
     * @param currentTxnId the transaction ID that must be less than all returned results
     * @return Returns an ordered list of transaction IDs 
     */
    @SuppressWarnings("unchecked")
    public List<String> getNextChangeTxnIds(final String currentTxnId)
    {
        HibernateCallback callback = new HibernateCallback()
        {
            public Object doInHibernate(Session session)
            {
                Query query = session.getNamedQuery(QUERY_GET_NEXT_CHANGE_TXN_IDS);
                query.setString("currentTxnId", currentTxnId)
                     .setReadOnly(true);
                return query.list();
            }
        };
        List<String> queryResults = (List<String>) getHibernateTemplate().execute(callback);
        // done
        return queryResults;
    }

    @SuppressWarnings("unchecked")
    public int getChangedNodeStatusesCount(final StoreRef storeRef, final String changeTxnId)
    {
        HibernateCallback callback = new HibernateCallback()
        {
            public Object doInHibernate(Session session)
            {
                Query query = session.getNamedQuery(QUERY_GET_CHANGED_NODE_STATUSES_COUNT);
                query.setBoolean("deleted", false)
                     .setString("storeProtocol", storeRef.getProtocol())
                     .setString("storeIdentifier", storeRef.getIdentifier())
                     .setString("changeTxnId", changeTxnId)
                     .setReadOnly(true);
                return query.uniqueResult();
            }
        };
        Integer changeCount = (Integer) getHibernateTemplate().execute(callback);
        // done
        return changeCount.intValue();
    }

    @SuppressWarnings("unchecked")
    public List<NodeStatus> getChangedNodeStatuses(final StoreRef storeRef, final String changeTxnId)
    {
        HibernateCallback callback = new HibernateCallback()
        {
            public Object doInHibernate(Session session)
            {
                Query query = session.getNamedQuery(QUERY_GET_CHANGED_NODE_STATUSES);
                query.setBoolean("deleted", false)
                     .setString("storeProtocol", storeRef.getProtocol())
                     .setString("storeIdentifier", storeRef.getIdentifier())
                     .setString("changeTxnId", changeTxnId)
                     .setReadOnly(true);
                return query.list();
            }
        };
        List<NodeStatus> queryResults = (List) getHibernateTemplate().execute(callback);
        // done
        return queryResults;
    }

    @SuppressWarnings("unchecked")
    public List<NodeStatus> getDeletedNodeStatuses(final StoreRef storeRef, final String changeTxnId)
    {
        HibernateCallback callback = new HibernateCallback()
        {
            public Object doInHibernate(Session session)
            {
                Query query = session.getNamedQuery(QUERY_GET_CHANGED_NODE_STATUSES);
                query.setBoolean("deleted", true)
                     .setString("storeProtocol", storeRef.getProtocol())
                     .setString("storeIdentifier", storeRef.getIdentifier())
                     .setString("changeTxnId", changeTxnId)
                     .setReadOnly(true);
                return query.list();
            }
        };
        List<NodeStatus> queryResults = (List) getHibernateTemplate().execute(callback);
        // done
        return queryResults;
    }
}