package com.activiti.repo.node;

import com.activiti.repo.domain.Node;
import com.activiti.repo.domain.Store;
import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.StoreRef;
import com.activiti.repo.store.db.StoreDaoService;
import com.activiti.util.BaseSpringTest;

/**
 * Tests the default implementation of the {@link com.activiti.repo.service.NodeService}
 * 
 * @author derekh
 */
public class NodeServiceTest extends BaseSpringTest
{
    private StoreDaoService storeDaoService;
    private NodeService nodeService;
    private NodeRef rootNodeRef;

    public void setStoreDaoService(StoreDaoService storeDaoService)
    {
        this.storeDaoService = storeDaoService;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    protected void onSetUpInTransaction() throws Exception
    {
        // create a first store directly
        Store store = storeDaoService.createStore(StoreRef.PROTOCOL_WORKSPACE,
                "Test_" + System.currentTimeMillis());
        StoreRef storeRef = store.getStoreRef();
        Node rootNode = store.getRootNode();
        rootNodeRef = rootNode.getNodeRef();
    }
    
    public void testSetUp() throws Exception
    {
        assertNotNull("Node Service not set", nodeService);
        assertNotNull("rootNodeRef not created", rootNodeRef);
    }
    
    private int countNodesById(NodeRef nodeRef)
    {
        return jdbcTemplate.queryForInt("select count(*) from node where guid = ?", new Object[] {nodeRef.getGuid()});
    }
    
    public void testCreateNodeNoProperties() throws Exception
    {
        NodeRef nodeRef = nodeService.createNode(rootNodeRef, "path1", Node.TYPE_CONTAINER);
        // count the nodes with the given id
        int count = countNodesById(nodeRef);
        assertEquals("Unexpected number of nodes present", 1, count);
    }
}
