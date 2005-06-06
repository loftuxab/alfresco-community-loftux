package org.alfresco.repo.node.index;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.content.ContentService;
import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.node.BaseNodeServiceTest;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.DynamicNamespacePrefixResolver;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.ref.StoreRef;
import org.alfresco.repo.search.ResultSet;
import org.alfresco.repo.search.Searcher;

/**
 * @see org.alfresco.repo.node.index.IndexingNodeServiceImpl
 * 
 * @author Derek Hulley
 */
public class IndexingNodeServiceImplTest extends BaseNodeServiceTest
{
    private Searcher searcher;

    private static StoreRef myStoreRef;

    private static NodeRef myRootNode;

    protected NodeService getNodeService()
    {
        return (NodeService) applicationContext.getBean("indexingNodeService");
    }
    
    @Override
    protected ContentService getContentService()
    {
        return (ContentService) applicationContext.getBean("contentService");
    }

    protected void onSetUpInTransaction() throws Exception
    {
        super.onSetUpInTransaction();
        searcher = (Searcher) applicationContext.getBean("searcherComponent");

        if (myStoreRef == null)
        {
            myStoreRef = nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_Persisted" + System.currentTimeMillis());
            myRootNode = nodeService.getRootNode(myStoreRef);
        }
    }

    public void testCommitQueryData() throws Exception
    {
        rootNodeRef = myRootNode;
        Map<QName, ChildAssocRef> assocRefs = buildNodeGraph();
        setComplete();
    }

    public void testQuery() throws Exception
    {
        rootNodeRef = myRootNode;
        ResultSet results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"" + BaseNodeServiceTest.TEST_PREFIX + ":root_p_n1\"", null, null);
        assertEquals(1, results.length());
    }
    
    public void testLikeAndContains() throws Exception
    {
        rootNodeRef = myRootNode;
        
        DynamicNamespacePrefixResolver namespacePrefixResolver = new DynamicNamespacePrefixResolver(null);
        namespacePrefixResolver.addDynamicNamespace(NamespaceService.ALFRESCO_PREFIX, NamespaceService.ALFRESCO_URI);
        namespacePrefixResolver.addDynamicNamespace(NamespaceService.ALFRESCO_TEST_PREFIX, BaseNodeServiceTest.NAMESPACE);
        
        List<ChildAssocRef> answer =  nodeService.selectNodes(rootNodeRef, "//*[like(@alftest:animal, '*monkey')", null, namespacePrefixResolver, false);
        assertEquals(1, answer.size());
        
        answer =  nodeService.selectNodes(rootNodeRef, "//*[like(@alftest:animal, '%monkey')", null, namespacePrefixResolver, false);
        assertEquals(1, answer.size());
        
        answer =  nodeService.selectNodes(rootNodeRef, "//*[like(@alftest:animal, 'monk*')", null, namespacePrefixResolver, false);
        assertEquals(1, answer.size());
        
        answer =  nodeService.selectNodes(rootNodeRef, "//*[like(@alftest:animal, 'monk%')", null, namespacePrefixResolver, false);
        assertEquals(1, answer.size());
        
        answer =  nodeService.selectNodes(rootNodeRef, "//*[like(@alftest:animal, 'monk\\%')", null, namespacePrefixResolver, false);
        assertEquals(0, answer.size());
        
        answer =  nodeService.selectNodes(rootNodeRef, "//*[contains('monkey')", null, namespacePrefixResolver, false);
        assertEquals(1, answer.size());
        
        List<Serializable> result =  nodeService.selectProperties(rootNodeRef, "//@*[contains('monkey')", null, namespacePrefixResolver, false);
        assertEquals(1, result.size());
        
        answer =  nodeService.selectNodes(rootNodeRef, "//*[contains('mon?ey')", null, namespacePrefixResolver, false);
        assertEquals(1, answer.size());
        
        result =  nodeService.selectProperties(rootNodeRef, "//@*[contains('mon?ey')", null, namespacePrefixResolver, false);
        assertEquals(1, result.size());
        
        answer =  nodeService.selectNodes(rootNodeRef, "//*[contains('m*y')", null, namespacePrefixResolver, false);
        assertEquals(1, answer.size());
        
        result =  nodeService.selectProperties(rootNodeRef, "//@*[contains('mon*')", null, namespacePrefixResolver, false);
        assertEquals(1, result.size());
        
        answer =  nodeService.selectNodes(rootNodeRef, "//*[contains('*nkey')", null, namespacePrefixResolver, false);
        assertEquals(1, answer.size());
        
        result =  nodeService.selectProperties(rootNodeRef, "//@*[contains('?onkey')", null, namespacePrefixResolver, false);
        assertEquals(1, result.size());
    }
}
