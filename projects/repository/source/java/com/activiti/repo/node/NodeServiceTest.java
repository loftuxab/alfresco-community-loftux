package com.activiti.repo.node;

import com.activiti.repo.domain.Node;
import com.activiti.repo.domain.Store;
import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.StoreRef;
import com.activiti.repo.workspace.DbStoreService;
import com.activiti.util.BaseSpringTest;

/**
 * Tests the default implementation of the {@link com.activiti.repo.service.NodeService}
 * 
 * @author derekh
 */
public class NodeServiceTest extends BaseSpringTest
{
    private StoreDaoService typedWorkspaceService;
    private NodeService nodeService;
    private NodeRef rootNodeRef;

    public void setTypedWorkspaceService(StoreDaoService typedWorkspaceService)
    {
        this.typedWorkspaceService = typedWorkspaceService;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    protected void onSetUpInTransaction() throws Exception
    {
        // create a first workspace directly
        Store workspace = typedWorkspaceService.createStore(StoreRef.PROTOCOL_WORKSPACE,
                "Test_" + System.currentTimeMillis());
        StoreRef storeRef = workspace.getStoreRef();
        Node rootNode = workspace.getRootNode();
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
