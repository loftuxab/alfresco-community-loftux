package com.activiti.repo.store.index;

import com.activiti.repo.ref.ChildAssocRef;
import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.StoreRef;
import com.activiti.repo.search.Indexer;
import com.activiti.repo.search.IndexerComponent;
import com.activiti.repo.store.InvalidStoreRefException;
import com.activiti.repo.store.StoreExistsException;
import com.activiti.repo.store.StoreService;

/**
 * A lightweight <code>StoreService</code> that delegates the work to a
 * <i>proper</i> <code>StoreService</code>, but also ensures that the
 * required calls are made to the {@link com.activiti.repo.search.Indexer indexer}.
 * <p>
 * The use of a delegate to perform all the <b>store</b> manipulation means that
 * implementations of the stores can be swapped in and out but still get indexed.
 * 
 * @author Derek Hulley
 */
public class IndexingStoreServiceImpl implements StoreService
{
    private StoreService storeServiceDelegate;
    private Indexer indexer;
    
    /**
     * @param nodeServiceDelegate the <code>NodeService</code> that will do the node work
     */
    public void setStoreServiceDelegate(StoreService storeServiceDelegate)
    {
        this.storeServiceDelegate = storeServiceDelegate;
    }
    
    /**
     * @param indexer the component that performs the indexing operations
     */
    public void setIndexer(Indexer indexer)
    {
        this.indexer = indexer;
    }

    /**
     * Delegates to the assigned {@link #storeServiceDelegate} before using the
     * {@link #indexer} to update the search index.
     * 
     * @see IndexerComponent#createNode(ChildAssocRef)
     */
    public StoreRef createStore(String protocol, String identifier) throws StoreExistsException
    {
        StoreRef storeRef = storeServiceDelegate.createStore(protocol, identifier);
        // get the root node
        NodeRef rootNodeRef = getRootNode(storeRef);
        // index it
        ChildAssocRef rootAssocRef = new ChildAssocRef(null, null, rootNodeRef);
        indexer.createNode(rootAssocRef);
        // done
        return storeRef;
    }

    /**
     * Direct delegation to assigned {@link #storeServiceDelegate}
     */
    public boolean exists(StoreRef storeRef)
    {
        return storeServiceDelegate.exists(storeRef);
    }

    /**
     * Direct delegation to assigned {@link #storeServiceDelegate}
     */
    public NodeRef getRootNode(StoreRef storeRef) throws InvalidStoreRefException
    {
        return storeServiceDelegate.getRootNode(storeRef);
    }
}
