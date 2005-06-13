package org.alfresco.repo.node.db;

import org.alfresco.repo.node.BaseNodeServiceTest;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeService;

/**
 * @see org.alfresco.repo.node.db.DbNodeServiceImpl
 * 
 * @author Derek Hulley
 */
public class DbNodeServiceImplTest extends BaseNodeServiceTest
{
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
