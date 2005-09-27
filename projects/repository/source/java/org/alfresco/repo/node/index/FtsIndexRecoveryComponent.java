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
import java.util.Collections;
import java.util.List;

import org.alfresco.repo.search.impl.lucene.fts.FullTextSearchIndexer;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Ensures that the FTS indexing picks up on any outstanding documents that
 * require indexing.
 * <p>
 * FTS indexing is a background process.  It is therefore possible that
 * certain documents don't get indexed when the server shuts down.
 * 
 * @author Derek Hulley
 */
public class FtsIndexRecoveryComponent implements IndexRecovery
{
    private static Log logger = LogFactory.getLog(FtsIndexRecoveryComponent.class);
    
    /** provides transactions to atomically index each missed transaction */
    private TransactionService transactionService;
    /** the FTS indexer that we will prompt to pick up on any un-indexed text */
    private FullTextSearchIndexer ftsIndexer;
    /** the component providing searches of the indexed nodes */
    private SearchService searcher;
    /** the component giving direct access to <b>node</b> instances */
    private NodeService nodeService;
    /** the workspaces to reindex */
    private List<StoreRef> storeRefs;
    
    public FtsIndexRecoveryComponent()
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
     * @param ftsIndexer the FTS background indexer
     */
    public void setFtsIndexer(FullTextSearchIndexer ftsIndexer)
    {
        this.ftsIndexer = ftsIndexer;
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
        
        // done
        return Collections.emptyList();
    }
}