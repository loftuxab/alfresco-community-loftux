package com.activiti.repo.node.index;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.activiti.repo.dictionary.DictionaryService;
import com.activiti.repo.dictionary.NamespaceService;
import com.activiti.repo.dictionary.bootstrap.DictionaryBootstrap;
import com.activiti.repo.node.BaseNodeServiceTest;
import com.activiti.repo.node.NodeService;
import com.activiti.repo.ref.ChildAssocRef;
import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.QName;
import com.activiti.repo.ref.StoreRef;
import com.activiti.repo.search.ResultSet;
import com.activiti.repo.search.Searcher;

/**
 * @see com.activiti.repo.node.index.IndexingNodeServiceImpl
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
        ResultSet results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"" + NamespaceService.ACTIVITI_TEST_PREFIX + ":root_p_n1\"", null, null);
        assertEquals(1, results.length());

    }

 
}
