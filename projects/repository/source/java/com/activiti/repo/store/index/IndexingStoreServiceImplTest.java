package com.activiti.repo.store.index;

import com.activiti.repo.store.BaseStoreServiceTest;
import com.activiti.repo.store.StoreService;

/**
 * @see com.activiti.repo.store.index.IndexingStoreServiceImpl
 * 
 * @author Derek Hulley
 */
public class IndexingStoreServiceImplTest extends BaseStoreServiceTest
{
    protected StoreService getStoreService()
    {
        return (StoreService) applicationContext.getBean("indexingStoreService");
    }
}
