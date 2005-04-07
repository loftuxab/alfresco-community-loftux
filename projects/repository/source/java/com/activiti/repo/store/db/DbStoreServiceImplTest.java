package com.activiti.repo.store.db;

import com.activiti.repo.store.BaseStoreServiceTest;
import com.activiti.repo.store.StoreService;

/**
 * @see com.activiti.repo.store.db.DbStoreServiceImpl
 * 
 * @author Derek Hulley
 */
public class DbStoreServiceImplTest extends BaseStoreServiceTest
{
    protected StoreService getStoreService()
    {
        return (StoreService) applicationContext.getBean("dbStoreService");
    }
}
