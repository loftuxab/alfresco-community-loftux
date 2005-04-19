package com.activiti.repo.node.db;

import com.activiti.repo.dictionary.DictionaryService;
import com.activiti.repo.node.BaseNodeServiceTest;
import com.activiti.repo.node.NodeService;

/**
 * @see com.activiti.repo.node.db.DbNodeServiceImpl
 * 
 * @author Derek Hulley
 */
public class DbNodeServiceImplTest extends BaseNodeServiceTest
{
    protected DictionaryService getDictionaryService()
    {
        return (DictionaryService) applicationContext.getBean("dictionaryService");
    }

    protected NodeService getNodeService()
    {
        return (NodeService) applicationContext.getBean("dbNodeService");
    }
}
