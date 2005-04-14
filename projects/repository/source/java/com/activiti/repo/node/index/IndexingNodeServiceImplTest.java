package com.activiti.repo.node.index;

import java.util.Map;

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
import com.activiti.repo.store.StoreExistsException;
import com.activiti.repo.store.StoreService;

/**
 * @see com.activiti.repo.node.index.IndexingNodeServiceImpl
 * 
 * @author Derek Hulley
 */
public class IndexingNodeServiceImplTest extends BaseNodeServiceTest
{
    private Searcher searcher;
    
    protected StoreService getStoreService()
    {
        return (StoreService) applicationContext.getBean("indexingStoreService");
    }

    protected NodeService getNodeService()
    {
        return (NodeService) applicationContext.getBean("indexingNodeService");
    }
    
    protected void onSetUpInTransaction() throws Exception
    {
       super.onSetUpInTransaction();
       searcher = (Searcher) applicationContext.getBean("searcherComponent");
       
       if(myStoreRef == null)
       {
               myStoreRef = storeService.createStore(
               StoreRef.PROTOCOL_WORKSPACE,
               "Test_Persisted" + System.currentTimeMillis());
               myRootNode = storeService.getRootNode(myStoreRef);
       }
    }
    
    private static StoreRef myStoreRef;
    private static NodeRef myRootNode;
    
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
