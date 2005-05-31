package org.alfresco.repo.node.db;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.content.ContentService;
import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.node.BaseNodeServiceTest;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.DynamicNamespacePrefixResolver;
import org.alfresco.repo.ref.QName;

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
    
    
    public void testLikeAndContains() throws Exception
    {
        Map<QName, ChildAssocRef> assocRefs = buildNodeGraph();
        
        DynamicNamespacePrefixResolver namespacePrefixResolver = new DynamicNamespacePrefixResolver(null);
        namespacePrefixResolver.addDynamicNamespace(NamespaceService.ALFRESCO_PREFIX, NamespaceService.ALFRESCO_URI);
        namespacePrefixResolver.addDynamicNamespace(NamespaceService.ALFRESCO_TEST_PREFIX, NamespaceService.ALFRESCO_TEST_URI);
        
        List<ChildAssocRef> answer =  nodeService.selectNodes(rootNodeRef, "//*[like(@alftest:animal, 'monkey')", null, namespacePrefixResolver, false);
        assertEquals(0, answer.size());
        
        answer =  nodeService.selectNodes(rootNodeRef, "//*[contains('monkey')", null, namespacePrefixResolver, false);
        assertEquals(0, answer.size());
    }
}
