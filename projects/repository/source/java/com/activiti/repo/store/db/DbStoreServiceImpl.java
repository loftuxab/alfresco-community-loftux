package com.activiti.repo.store.db;

import com.activiti.repo.domain.Store;
import com.activiti.repo.ref.StoreRef;
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
        Store store = storeDaoService.createStore(protocol, identifier);
        // done
        StoreRef storeRef = store.getStoreRef();
        return storeRef;
    }
}
