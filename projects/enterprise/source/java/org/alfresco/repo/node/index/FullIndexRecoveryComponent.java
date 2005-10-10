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
import java.util.ListIterator;

import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.domain.NodeStatus;
import org.alfresco.repo.domain.hibernate.ChildAssocImpl;
import org.alfresco.repo.domain.hibernate.NodeAssocImpl;
import org.alfresco.repo.domain.hibernate.NodeImpl;
import org.alfresco.repo.search.Indexer;
import org.alfresco.repo.search.impl.lucene.fts.FullTextSearchIndexer;
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
 * require indexing
 * 
 * @author Derek Hulley
 */
public class FullIndexRecoveryComponent extends HibernateDaoSupport implements IndexRecovery
{
    public static final String QUERY_GET_CHANGE_TXN_IDS = "node.GetChangeTxnIds";
    public static final String QUERY_GET_CHANGED_NODE_STATUSES = "node.GetChangedNodeStatuses";
    public static final String QUERY_GET_CHANGED_NODE_STATUSES_COUNT = "node.GetChangedNodeStatusesCount";
    
    private static Log logger = LogFactory.getLog(FullIndexRecoveryComponent.class);
    
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
    
    public FullIndexRecoveryComponent()
    {
        this.storeRefs = new ArrayList<StoreRef>(2);
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
     * Ensure that the index is up to date with the current state of the persistence layer.
     * The full list of unique transaction change IDs is retrieved and used to detect
     * which are not present in the index.  All the node changes and deletions for the
     * remaining transactions are then indexed.
     */
    public List<String> reindex()
    {
        // we want to start with a clean L2 cache
        getSession().setCacheMode(CacheMode.IGNORE);
        // - evict all queries
        getSessionFactory().evictQueries();
        // - evict anything that might affect the indexing
        getSessionFactory().evict(NodeImpl.class);
        getSessionFactory().evict(ChildAssocImpl.class);
        getSessionFactory().evict(NodeAssocImpl.class);
        
        List<String> reindexedTxns = new ArrayList<String>(30);
        
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
            
            List<String> reindexed = reindex(storeRef);
            reindexedTxns.addAll(reindexed);
        }
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Reindexed " + reindexedTxns.size() + " outstanding transactions");
        }
        return reindexedTxns;
    }
    
    private List<String> reindex(StoreRef storeRef)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Reindexing store: " + storeRef);
        }
        
        // prompt FTS to reindex the store
        ftsIndexer.requiresIndex(storeRef);
        
        SearchParameters sp = new SearchParameters();
        sp.addStore(storeRef);

        // get all the transaction IDs
        List<String> changeTxnIds = getAllChangeTxnIds(storeRef);
        
        // remove all IDs that are already present in the indexer
        ListIterator<String> iterator = changeTxnIds.listIterator();
        while (iterator.hasNext())
        {
            String changeTxnId = iterator.next();
            // search for it
            String query = "TX:\"" + changeTxnId + "\"";
            sp.setLanguage(SearchService.LANGUAGE_LUCENE);
            sp.setQuery(query);
            ResultSet results = searcher.query(sp);
            // did the index have any of these changes?
            if (results.length() > 0)
            {
                // we assume that the index is up to date with this transaction
                iterator.remove();
                continue;
            }
            // the index has no record of this
            // were there any changes, or is it all just deletions?
            int changedCount = getChangedNodeStatusesCount(storeRef, changeTxnId);
            if (changedCount == 0)
            {
                // no nodes were changed in the transaction, i.e. they are only deletions
                // the index is quite right not to have any entries for the transaction
                iterator.remove();
                continue;
            }
            int i = 0;
            // need to index the changes - keep them for later
        }
        
        /*
         * At this point, we have a set of missed transactions, i.e. they exist in the
         * database, but not in the index.  In theory, a transaction that only removes
         * a node without making any changes to any other nodes, will be present here
         * as well.  In this case, the transaction will be processed as a set of removals
         * from the index.  We don't have any way of recording deletions in the index -
         * so the quickest is just to process them.
         */
        for (String changeTxnId : changeTxnIds)
        {
            reindex(storeRef, changeTxnId);
        }
        
        return changeTxnIds;
    }
    
    /**
     * Reindexes changes specific to the store and change transaction.
     */
    private void reindex(StoreRef storeRef, String changeTxnId)
    {
        /*
         * This must execute each within its own transaction.
         * The cache size is therefore not an issue.
         */
        UserTransaction txn = transactionService.getNonPropagatingUserTransaction();
        try
        {
            txn.begin();
            
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
            
            // commit the change - it is a commit to the index only
            txn.commit();
            // done
            if (logger.isDebugEnabled())
            {
                logger.debug("Reindex transaction: \n" +
                        "   store: " + storeRef + "\n" +
                        "   txn: " + changeTxnId);
            }
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
                    "   store: " + storeRef + "\n" +
                    "   txn: " + changeTxnId,
                    e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<String> getAllChangeTxnIds(final StoreRef storeRef)
    {
        HibernateCallback callback = new HibernateCallback()
        {
            public Object doInHibernate(Session session)
            {
                Query query = session.getNamedQuery(QUERY_GET_CHANGE_TXN_IDS);
                query.setString("storeProtocol", storeRef.getProtocol())
                     .setString("storeIdentifier", storeRef.getIdentifier())
                     .setReadOnly(true);
                return query.list();
            }
        };
        List<String> queryResults = (List) getHibernateTemplate().execute(callback);
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