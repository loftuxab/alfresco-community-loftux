package com.activiti.repo.store.db;

import com.activiti.repo.domain.Node;
import com.activiti.repo.domain.Store;
import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.StoreRef;
import com.activiti.repo.store.InvalidStoreRefException;
import com.activiti.repo.store.StoreExistsException;
import com.activiti.repo.store.StoreService;

public class DbStoreServiceImpl implements StoreService
{
    private StoreDaoService storeDaoService;
    
    public void setStoreDaoService(StoreDaoService storeDaoService)
    {
        this.storeDaoService = storeDaoService;
    }

    /**
     * Defers to the typed service
     * @see StoreDaoService#createWorkspace(String)
     */
    public StoreRef createStore(String protocol, String identifier)
    {
        // check that the store does not already exist
        Store store = storeDaoService.getStore(protocol, identifier);
        if (store != null)
        {
            throw new StoreExistsException("Unable to create a store that already exists",
                    new StoreRef(protocol, identifier));
        }
        // create a new one
        store = storeDaoService.createStore(protocol, identifier);
        // done
        StoreRef storeRef = store.getStoreRef();
        return storeRef;
    }

    public boolean exists(StoreRef storeRef)
    {
        Store store = storeDaoService.getStore(storeRef.getProtocol(), storeRef.getIdentifier());
        boolean exists = (store != null);
        // done
        return exists;
    }
    
    /**
     * @param storeRef store to search for
     * @return Returns a non-null <code>Store</code> instance
     * @throws InvalidStoreRefException if the reference is to a store that doesn't exist
     */
    private Store getStoreNotNull(StoreRef storeRef) throws InvalidStoreRefException
    {
        Store store = storeDaoService.getStore(storeRef.getProtocol(), storeRef.getIdentifier());
        if (store == null)
        {
            throw new InvalidStoreRefException(storeRef);
        }
        return store;
    }

    public NodeRef getRootNode(StoreRef storeRef) throws InvalidStoreRefException
    {
        Store store = storeDaoService.getStore(storeRef.getProtocol(), storeRef.getIdentifier());
        // get the root
        Node node = store.getRootNode();
        NodeRef nodeRef = node.getNodeRef();
        // done
        return nodeRef;
    }
}
