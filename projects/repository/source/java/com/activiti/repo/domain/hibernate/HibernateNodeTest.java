package com.activiti.repo.domain.hibernate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.activiti.repo.domain.ChildAssoc;
import com.activiti.repo.domain.ContainerNode;
import com.activiti.repo.domain.ContentNode;
import com.activiti.repo.domain.Node;
import com.activiti.repo.domain.ReferenceNode;

/**
 * Test persistence and retrieval of Hibernate-specific implementations of the
 * {@link com.activiti.repo.domain.Node} interface
 * 
 * @author derekh
 */
public class HibernateNodeTest extends BaseHibernateTest
{
    public HibernateNodeTest()
    {
    }

    public void testMap() throws Exception
    {
        // create a new Node
        Node node = new NodeImpl();
        // give it a property map
        Map propertyMap = new HashMap(5);
        propertyMap.put("A", "AAA");
        node.setProperties(propertyMap);
        // persist it
        Serializable id = getSession().save(node);

        // throw the reference away and get the a new one for the id
        node = (Node) getSession().load(NodeImpl.class, id);
        assertNotNull("Node not found", node);
        // extract the Map
        propertyMap = node.getProperties();
        assertNotNull("Map not persisted", propertyMap);
        // ensure that the value is present
        assertNotNull("Property value not present in map", propertyMap.get("A"));
    }

    public void testSubclassing() throws Exception
    {
        // persist a subclass of Node
        Node node = new ContentNodeImpl();
        Serializable id = getSession().save(node);
        // get the node back
        node = (Node) getSession().get(NodeImpl.class, id);
        // check
        assertNotNull("Persisted node not found", id);
        assertTrue("Subtype not retrieved", node instanceof ContentNode);
    }

    public void testReferenceNode() throws Exception
    {
        // make a reference node
        ReferenceNode refNode = new ReferenceNodeImpl();
        refNode.setReferencedPath("/somepath/to/some/node[1]");
        Serializable refNodeId = getSession().save(refNode);

        // get the ref node back by ID
        refNode = (ReferenceNode) getSession().get(NodeImpl.class, refNodeId);
    }

    public void testChildAssoc() throws Exception
    {
        // make a content node
        ContentNode contentNode = new ContentNodeImpl();
        Serializable contentNodeId = getSession().save(contentNode);

        // make a container node
        ContainerNode containerNode = new ContainerNodeImpl();
        Serializable containerNodeId = getSession().save(containerNode);
        // create an association to the content
        ChildAssoc assoc = new ChildAssocImpl();
        assoc.setIsPrimary(true);
        assoc.setName("number1");
        assoc.buildAssociation(containerNode, contentNode);
        getSession().save(assoc);

        // make another association between the same two parent and child nodes
        assoc = new ChildAssocImpl();
        assoc.setIsPrimary(true);
        assoc.setName("number2");
        assoc.buildAssociation(containerNode, contentNode);
        getSession().save(assoc);

        getSession().flush();
        getSession().clear();

        // reload the container
        containerNode = (ContainerNode) getSession().get(
                ContainerNodeImpl.class, containerNodeId);
        assertNotNull("Node not found", containerNode);
        // check
        assertEquals("Expected exactly 2 children", 2, containerNode
                .getChildAssocs().size());
        for (Iterator iterator = containerNode.getChildAssocs().iterator(); iterator
                .hasNext(); /**/)
        {
            assoc = (ChildAssoc) iterator.next();
            // the node id must be known
            assertNotNull("Node not populated on assoc", assoc.getChild());
            assertEquals("Node ID on child assoc is incorrect", contentNodeId,
                    assoc.getChild().getId());
        }

        // check that we can traverse the association from the child
        Set parentAssocs = contentNode.getParentAssocs();
        assertEquals("Expected exactly 2 parents", 2, parentAssocs.size());
    }
}
