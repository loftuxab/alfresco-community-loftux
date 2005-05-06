package org.alfresco.repo.node.index;

import java.util.Map;

import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.node.BaseNodeServiceTest;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.ChildAssocRef;
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

    protected DictionaryService getDictionaryService()
    {
        return (DictionaryService) applicationContext.getBean("dictionaryService");
    }

    protected NodeService getNodeService()
    {
        return (NodeService) applicationContext.getBean("indexingNodeService");
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
        ResultSet results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"" + NamespaceService.alfresco_TEST_PREFIX + ":root_p_n1\"", null, null);
        assertEquals(1, results.length());

    }
}
