package com.activiti.repo.store.db;

import com.activiti.repo.domain.Store;
import com.activiti.repo.ref.StoreRef;
import com.activiti.repo.store.StoreService;

public class DbStoreServiceImpl implements StoreService
{
    private DbStoreService dbStoreService;
    
    public void setDbStoreService(DbStoreService dbStoreService)
    {
        this.dbStoreService = dbStoreService;
    }

    /**
     * Defers to the typed service
     * @see DbStoreService#createWorkspace(String)
     */
    public StoreRef createStore(String protocol, String identifier)
    {
        Store workspace = dbStoreService.createWorkspace(protocol, identifier);
        // done
        StoreRef storeRef = workspace.getStoreRef();
        return storeRef;
    }
}
