package com.activiti.repo.node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.activiti.repo.domain.Node;
import com.activiti.repo.domain.Store;
import com.activiti.repo.domain.hibernate.NodeImpl;
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
        String query =
                "select count(node.key.guid)" +
                " from " +
                NodeImpl.class.getName() + " node" +
                " where node.key.guid = ?";
        Session session = getSession();
        List results = session.createQuery(query)
            .setString(0, nodeRef.getId())
            .list();
        Integer count = (Integer) results.get(0);
        return count.intValue();
    }
    
    public void testCreateNodeNoProperties() throws Exception
    {
        // flush to ensure that the pure JDBC query will work
        NodeRef nodeRef = nodeService.createNode(rootNodeRef, "path1", Node.TYPE_CONTAINER);
        // count the nodes with the given id
        int count = countNodesById(nodeRef);
        assertEquals("Unexpected number of nodes present", 1, count);
    }
    
    public void testDelete() throws Exception
    {
        NodeRef nodeRef = nodeService.createNode(rootNodeRef, "path1", Node.TYPE_CONTAINER);
        int countBefore = countNodesById(nodeRef);
        assertEquals("Node not created", 1, countBefore);
        // delete it
        nodeService.deleteNode(nodeRef);
        int countAfter = countNodesById(nodeRef);
        // check
        assertEquals("Node not deleted", 0, countAfter);
    }
    
    private int countChildrenOfNode(NodeRef nodeRef)
    {
        String query =
                "select node.childAssocs" +
                " from " +
                NodeImpl.class.getName() + " node" +
                " where node.key.guid = ?";
        Session session = getSession();
        List results = session.createQuery(query)
            .setString(0, nodeRef.getId())
            .list();
        int count = results.size();
        return count;
    }
    
    /**
     * TODO: Fix test checks
     */
    public void testAddChild() throws Exception
    {
        // create a bogus reference
        NodeRef bogusChildRef = new NodeRef(rootNodeRef.getStoreRef(), "BOGUS");
        try
        {
            nodeService.addChild(rootNodeRef, bogusChildRef, "BOGUS_PATH");
            fail("Failed to detect invalid child node reference");
        }
        catch (InvalidNodeRefException e)
        {
            // expected
        }
        NodeRef nodeRef = nodeService.createNode(rootNodeRef, "pathA", Node.TYPE_CONTAINER);
//        int countBefore = countChildrenOfNode(rootNodeRef);
//        assertEquals("Root children count incorrect", 1, countBefore);
        // associate the two nodes
        nodeService.addChild(rootNodeRef, nodeRef, "pathB");
        // there should now be 2 child assocs on the root
//        int countAfter = countChildrenOfNode(rootNodeRef);
//        assertEquals("Root children count incorrect", 2, countAfter);
    }
    
    public void testRemoveChildByRef() throws Exception
    {
        NodeRef nodeRef = nodeService.createNode(rootNodeRef, "pathA", Node.TYPE_CONTAINER);
        nodeService.addChild(rootNodeRef, nodeRef, "pathB");
        nodeService.addChild(rootNodeRef, nodeRef, "pathC");
        // delete all the associations
        nodeService.removeChild(rootNodeRef, nodeRef);
    }
    
    public void testRemoveChildByName() throws Exception
    {
        NodeRef nodeRef = nodeService.createNode(rootNodeRef, "pathA", Node.TYPE_CONTAINER);
        nodeService.addChild(rootNodeRef, nodeRef, "pathB");
        nodeService.addChild(rootNodeRef, nodeRef, "pathC");
        // delete all the associations
        nodeService.removeChild(rootNodeRef, "pathB");
    }
    
    public void testGetType() throws Exception
    {
        NodeRef nodeRef = nodeService.createNode(rootNodeRef, "pathA", Node.TYPE_CONTAINER);
        // get the type
        String type = nodeService.getType(nodeRef);
        assertEquals("Type mismatch", Node.TYPE_CONTAINER, type);
    }
    
    public void testProperties() throws Exception
    {
        Map properties = new HashMap(5);
        properties.put("PROPERTY1", "VALUE1");
        // add some properties to the root node
        nodeService.setProperties(rootNodeRef, properties);
        
        // force a flush
        getSession().flush();
        getSession().clear();
        
        // now get them back
        Map check = nodeService.getProperties(rootNodeRef);
        assertEquals("Properties were not set/retrieved", properties, check);
    }
}
