package org.alfresco.repo.node.db;

import org.alfresco.repo.content.ContentService;
import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.node.BaseNodeServiceTest;
import org.alfresco.repo.node.NodeService;

/**
 * @see org.alfresco.repo.node.db.DbNodeServiceImpl
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

    @Override
    protected ContentService getContentService()
    {
        return (ContentService) applicationContext.getBean("contentService");
    }
}
