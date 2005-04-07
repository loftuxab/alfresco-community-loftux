package com.activiti.repo.node.db;

import com.activiti.repo.node.BaseNodeServiceTest;
import com.activiti.repo.node.NodeService;
import com.activiti.repo.store.StoreService;

/**
 * @see com.activiti.repo.node.db.DbNodeServiceImpl
 * 
 * @author Derek Hulley
 */
public class DbNodeServiceImplTest extends BaseNodeServiceTest
{
    protected StoreService getStoreService()
    {
        return (StoreService) applicationContext.getBean("dbStoreService");
    }

    protected NodeService getNodeService()
    {
        return (NodeService) applicationContext.getBean("dbNodeService");
    }
}
