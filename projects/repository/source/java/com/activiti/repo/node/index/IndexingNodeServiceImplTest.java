package com.activiti.repo.node.index;

import com.activiti.repo.node.BaseNodeServiceTest;
import com.activiti.repo.node.NodeService;
import com.activiti.repo.store.StoreService;

/**
 * @see com.activiti.repo.node.index.IndexingNodeServiceImpl
 * 
 * @author Derek Hulley
 */
public class IndexingNodeServiceImplTest extends BaseNodeServiceTest
{
    protected StoreService getStoreService()
    {
        return (StoreService) applicationContext.getBean("indexingStoreService");
    }

    protected NodeService getNodeService()
    {
        return (NodeService) applicationContext.getBean("indexingNodeService");
    }
}
