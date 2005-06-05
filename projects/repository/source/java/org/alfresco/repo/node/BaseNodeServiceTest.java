package org.alfresco.repo.node;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.content.ContentService;
import org.alfresco.repo.dictionary.ClassDefinition;
import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.dictionary.PropertyDefinition;
import org.alfresco.repo.dictionary.PropertyTypeDefinition;
import org.alfresco.repo.dictionary.impl.DictionaryBootstrap;
import org.alfresco.repo.domain.hibernate.NodeImpl;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.DynamicNamespacePrefixResolver;
import org.alfresco.repo.ref.EntityRef;
import org.alfresco.repo.ref.NodeAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.Path;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.ref.StoreRef;
import org.alfresco.repo.search.QueryParameterDefImpl;
import org.alfresco.repo.search.QueryParameterDefinition;
import org.alfresco.util.BaseSpringTest;
import org.alfresco.util.Conversion;
import org.alfresco.util.debug.CodeMonkey;
import org.hibernate.Session;

import com.vladium.utils.timing.ITimer;
import com.vladium.utils.timing.TimerFactory;

/**
 * Provides a base set of tests of the various {@link org.alfresco.repo.node.NodeService}
 * implementations.
 * <p>
 * To test a specific incarnation of the service, the methods {@link #getStoreService()} and
 * {@link #getNodeService()} must be implemented. 
 * 
 * @see #nodeService
 * @see #rootNodeRef
 * @see #buildNodeGraph()
 * 
 * @author Derek Hulley
 */
public abstract class BaseNodeServiceTest extends BaseSpringTest
{
    
    private static ITimer timer = TimerFactory.newTimer();
    
    protected DictionaryService dictionaryService;
    protected NodeService nodeService;
    /** populated during setup */
    protected NodeRef rootNodeRef;

    private ContentService contentService;

    protected void onSetUpInTransaction() throws Exception
    {
        dictionaryService = getDictionaryService();
        nodeService = getNodeService();
        contentService = getContentService();
        
        // create a first store directly
        StoreRef storeRef = nodeService.createStore(
                StoreRef.PROTOCOL_WORKSPACE,
                "Test_" + System.currentTimeMillis());
        rootNodeRef = nodeService.getRootNode(storeRef);
    }
    
    /**
     * Usually just implemented by fetching the bean directly from the bean factory.

     * @return Returns the implementation of <code>DictionaryService</code> to be
     *      used for this test.
     */
    protected abstract DictionaryService getDictionaryService();
    
    /**
     * Usually just implemented by fetching the bean directly from the bean factory,
     * for example:
     * <p>
     * <pre>
     *      return (NodeService) applicationContext.getBean("dbNodeService");
     * </pre>
     * 
     * @return Returns the implementation of <code>NodeService</code> to be
     *      used for this test
     */
    protected abstract NodeService getNodeService();
    
    protected abstract ContentService getContentService();
    
    public void testSetUp() throws Exception
    {
        assertNotNull("StoreService not set", nodeService);
        assertNotNull("NodeService not set", nodeService);
        assertNotNull("rootNodeRef not created", rootNodeRef);
    }
    
    /**
     * Builds a graph of child associations as follows:
     * <pre>
     * Level 0:     root
     * Level 1:     root_p_n1   root_p_n2
     * Level 2:     n1_p_n3     n2_p_n4     n1_n4       n2_p_n5
     * Level 3:     n3_p_n6     n4_n6       n5_p_n7
     * Level 4:     n6_p_n8     n7_n8
     * </pre>
     * <p>
     * Apart from the root node having the root aspect, node 6 (<b>n6</b>) also has the
     * root aspect.
     * <p>
     * The namespace URI for all associations is <b>{@link NamespaceService.alfresco_TEST_URI}</b>.
     * <p>
     * The naming convention is:
     * <pre>
     * n2_p_n5
     * n4_n5
     * where
     *      n5 is the node number of the node
     *      n2 is the primary parent node number
     *      n4 is any other non-primary parent
     * </pre>
     * <p>
     * The session is flushed to ensure that persistence occurs correctly.  It is
     * cleared to ensure that fetches against the created data are correct.
     * 
     * @return Returns a map <code>ChildAssocRef</code> instances keyed by qualified assoc name
     */
    protected Map<QName, ChildAssocRef> buildNodeGraph() throws Exception
    {
        String ns = NamespaceService.ALFRESCO_TEST_URI;
        QName qname = null;
        ChildAssocRef assoc = null;
        Map<QName, ChildAssocRef> ret = new HashMap<QName, ChildAssocRef>(13);
        
        
        
        // LEVEL 0

        // LEVEL 1
        qname = QName.createQName(ns, "root_p_n1");
        assoc = nodeService.createNode(rootNodeRef, null, qname, DictionaryBootstrap.TYPE_QNAME_CONTAINER);
        ret.put(qname, assoc);
        NodeRef n1 = assoc.getChildRef();

        qname = QName.createQName(ns, "root_p_n2");
        assoc = nodeService.createNode(rootNodeRef, null, qname, DictionaryBootstrap.TYPE_QNAME_CONTAINER);
        ret.put(qname, assoc);
        NodeRef n2 = assoc.getChildRef();

        // LEVEL 2
        
        Map<QName, Serializable> attributes = new HashMap<QName, Serializable>();
        attributes.put(QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "animal"), "monkey");
        attributes.put(QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "reference"), n2.toString());
        
        qname = QName.createQName(ns, "n1_p_n3");
        assoc = nodeService.createNode(n1, null, qname, DictionaryBootstrap.TYPE_QNAME_CONTAINER, attributes);
        ret.put(qname, assoc);
        NodeRef n3 = assoc.getChildRef();

        qname = QName.createQName(ns, "n2_p_n4");
        assoc = nodeService.createNode(n2, null, qname, DictionaryBootstrap.TYPE_QNAME_CONTAINER);
        ret.put(qname, assoc);
        NodeRef n4 = assoc.getChildRef();

        qname = QName.createQName(ns, "n1_n4");
        assoc = nodeService.addChild(n1, n4, qname);
        ret.put(qname, assoc);

        qname = QName.createQName(ns, "n2_p_n5");
        assoc = nodeService.createNode(n2, null, qname, DictionaryBootstrap.TYPE_QNAME_CONTAINER);
        ret.put(qname, assoc);
        NodeRef n5 = assoc.getChildRef();

        // LEVEL 3
        qname = QName.createQName(ns, "n3_p_n6");
        assoc = nodeService.createNode(n3, null, qname, DictionaryBootstrap.TYPE_QNAME_CONTAINER);
        ret.put(qname, assoc);
        NodeRef n6 = assoc.getChildRef();
        nodeService.addAspect(n6,
                DictionaryBootstrap.ASPECT_QNAME_ROOT,
                Collections.<QName, Serializable>emptyMap());

        qname = QName.createQName(ns, "n4_n6");
        assoc = nodeService.addChild(n4, n6, qname);
        ret.put(qname, assoc);

        qname = QName.createQName(ns, "n5_p_n7");
        assoc = nodeService.createNode(n5, null, qname, DictionaryBootstrap.TYPE_QNAME_CONTAINER);
        ret.put(qname, assoc);
        NodeRef n7 = assoc.getChildRef();

        // LEVEL 4
        qname = QName.createQName(ns, "n6_p_n8");
        assoc = nodeService.createNode(n6, null, qname, DictionaryBootstrap.TYPE_QNAME_CONTAINER);
        ret.put(qname, assoc);
        NodeRef n8 = assoc.getChildRef();

        qname = QName.createQName(ns, "n7_n8");
        assoc = nodeService.addChild(n7, n8, qname);
        ret.put(qname, assoc);

        // flush and clear
        getSession().flush();
        getSession().clear();
        
        // done
        return ret;
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
    
    /**
     * @return Returns a reference to the created store
     */
    private StoreRef createStore() throws Exception
    {
        StoreRef storeRef = nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "my store");
        assertNotNull("No reference returned", storeRef);
        // done
        return storeRef;
    }
    
    public void testCreateStore() throws Exception
    {
        StoreRef storeRef = createStore();
        // get the root node
        NodeRef storeRootNode = nodeService.getRootNode(storeRef);
        // make sure that it has the root aspect
        boolean isRoot = nodeService.hasAspect(storeRootNode, DictionaryBootstrap.ASPECT_QNAME_ROOT);
        assertTrue("Root node of store does not have root aspect", isRoot);
    }
    
    public void testExists() throws Exception
    {
        StoreRef storeRef = createStore();
        boolean exists = nodeService.exists(storeRef);
        assertEquals("Exists failed", true, exists);
        // create bogus ref
        StoreRef bogusRef = new StoreRef("What", "the");
        exists = nodeService.exists(bogusRef);
        assertEquals("Exists failed", false, exists);
    }
    
    public void testGetRootNode() throws Exception
    {
        StoreRef storeRef = createStore();
        // get the root node
        NodeRef rootNodeRef = nodeService.getRootNode(storeRef);
        assertNotNull("No root node reference returned", rootNodeRef);
        // get the root node again
        NodeRef rootNodeRefCheck = nodeService.getRootNode(storeRef);
        assertEquals("Root nodes returned different refs", rootNodeRef, rootNodeRefCheck);
    }

    public void testGetType() throws Exception
    {
        ChildAssocRef assocRef = nodeService.createNode(rootNodeRef,
                null,
                QName.createQName("pathA"), DictionaryBootstrap.TYPE_QNAME_CONTAINER);
        NodeRef nodeRef = assocRef.getChildRef();
        // get the type
        QName type = nodeService.getType(nodeRef);
        assertEquals("Type mismatch", DictionaryBootstrap.TYPE_QNAME_CONTAINER, type);
    }
    
    /**
     * Fills the given property map with some values according to the property definitions on the given class
     */
    protected void fillProperties(QName qname, Map<QName, Serializable> properties)
    {
        ClassDefinition classDef = dictionaryService.getClass(qname);
        if (classDef == null)
        {
            throw new RuntimeException("No such class: " + qname);
        }
        Map<QName,PropertyDefinition> propertyDefs = classDef.getProperties();
        // make up a property value for each property
        for (QName propertyName : propertyDefs.keySet())
        {
            Serializable value = new Long(System.currentTimeMillis());
            // add it
            properties.put(propertyName, value);
        }
    }
    
    /**
     * Checks that aspects can be added, removed and queried.  Failure to detect
     * inadequate properties is also checked.
     */
    public void testAspects() throws Exception
    {
        // create a regular base node
        ChildAssocRef assocRef = nodeService.createNode(
                rootNodeRef,
                null,
                QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "test-content"),
                DictionaryBootstrap.TYPE_QNAME_BASE);
        NodeRef nodeRef = assocRef.getChildRef();
        // add the content aspect to the node, but don't supply any properties
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>(20);
        try
        {
            nodeService.addAspect(nodeRef, DictionaryBootstrap.ASPECT_QNAME_CONTENT, properties);
            fail("Failed to detect inadequate properties for aspect");
        }
        catch (PropertyException e)
        {
            // expected
        }
        // get the properties required for the aspect
        fillProperties(DictionaryBootstrap.ASPECT_QNAME_CONTENT, properties);
        // get the node properties before
        Map<QName, Serializable> propertiesBefore = nodeService.getProperties(nodeRef);
        // add the aspect
        nodeService.addAspect(nodeRef, DictionaryBootstrap.ASPECT_QNAME_CONTENT, properties);
        // get the properties after and check
        Map<QName, Serializable> propertiesAfter = nodeService.getProperties(nodeRef);
        assertEquals("Aspect properties not added",
                propertiesBefore.size() + 3,
                propertiesAfter.size());
        
        // attempt to override node properties with insufficient properties
        propertiesAfter.clear();
        try
        {
            nodeService.setProperties(nodeRef, propertiesAfter);
            fail("Failed to detect that required properties were missing");
        }
        catch (PropertyException e)
        {
            // expected
        }
        
        // check that we know that the aspect is present
        Set<QName> aspects = nodeService.getAspects(nodeRef);
        assertEquals("Incorrect number of aspects", 1, aspects.size());
        assertTrue("Content aspect not present",
                aspects.contains(DictionaryBootstrap.ASPECT_QNAME_CONTENT));
        
        // check that hasAspect works
        boolean hasAspect = nodeService.hasAspect(nodeRef, DictionaryBootstrap.ASPECT_QNAME_CONTENT);
        assertTrue("Aspect not confirmed to be on node", hasAspect);
        
        // remove the aspect
        nodeService.removeAspect(nodeRef, DictionaryBootstrap.ASPECT_QNAME_CONTENT);
        hasAspect = nodeService.hasAspect(nodeRef, DictionaryBootstrap.ASPECT_QNAME_CONTENT);
        assertFalse("Aspect not removed from node", hasAspect);
        
        // check that the associated properties were removed
        propertiesAfter = nodeService.getProperties(nodeRef);
        assertEquals("Aspect properties not removed",
                propertiesBefore.size(),
                propertiesAfter.size());
    }
    
    public void testSpaceAspect() throws Exception
    {
       // create a folder node
        ChildAssocRef assocRef = nodeService.createNode(
                rootNodeRef,
                null,
                QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "test-space"),
                DictionaryBootstrap.TYPE_QNAME_FOLDER);
        NodeRef nodeRef = assocRef.getChildRef();
        
        // define properties required for the space aspect
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>(5);
        
        QName propCreatedDate = QName.createQName(NamespaceService.ALFRESCO_URI, "createddate");
        Date now = new Date();
        properties.put(propCreatedDate, Conversion.dateToXmlDate(now));
        
        QName propModifiedDate = QName.createQName(NamespaceService.ALFRESCO_URI, "modifieddate");
        properties.put(propModifiedDate, Conversion.dateToXmlDate(now));
        
        QName propIcon = QName.createQName(NamespaceService.ALFRESCO_URI, "icon");
        properties.put(propIcon, "space.gif");
        
        QName propDescription = QName.createQName(NamespaceService.ALFRESCO_URI, "description");
        properties.put(propDescription, "Short description");
        
        QName propSpaceType = QName.createQName(NamespaceService.ALFRESCO_URI, "spacetype");
        properties.put(propSpaceType, "container");
        
        // try and add the aspect to the folder node
        nodeService.addAspect(nodeRef, DictionaryBootstrap.ASPECT_QNAME_SPACE, properties);
        
        Set<QName> aspects = nodeService.getAspects(nodeRef);
        assertEquals("There should only be 1 aspect applied", 1, aspects.size());
    }

    public void testCreateNodeNoProperties() throws Exception
    {
        // flush to ensure that the pure JDBC query will work
        ChildAssocRef assocRef = nodeService.createNode(rootNodeRef,
                null,
                QName.createQName("path1"),
                DictionaryBootstrap.TYPE_QNAME_CONTAINER);
        NodeRef nodeRef = assocRef.getChildRef();
        // count the nodes with the given id
        int count = countNodesById(nodeRef);
        assertEquals("Unexpected number of nodes present", 1, count);
    }
    
    /**
     * @see DictionaryBootstrap#ASPECT_CONTENT
     * @see DictionaryBootstrap#TYPE_CONTENT
     */
    public void testCreateNodeWithAspects() throws Exception
    {
        try
        {
            ChildAssocRef assocRef = nodeService.createNode(rootNodeRef,
                    null,
                    QName.createQName("MyContent"),
                    DictionaryBootstrap.TYPE_QNAME_CONTENT);
            fail("Failed to detect missing properties for required aspect");
        }
        catch (PropertyException e)
        {
            // exptected
        }
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>(5);
        // fill properties
        fillProperties(DictionaryBootstrap.ASPECT_QNAME_CONTENT, properties);
        
        // create node for real
        ChildAssocRef assocRef = nodeService.createNode(rootNodeRef,
                null,
                QName.createQName("MyContent"),
                DictionaryBootstrap.TYPE_QNAME_CONTENT,
                properties);
        NodeRef nodeRef = assocRef.getChildRef();
        // check that the content aspect is present
        assertTrue("Content aspect not present",
                nodeService.hasAspect(nodeRef, DictionaryBootstrap.ASPECT_QNAME_CONTENT));
        
        // attempt to remove the aspect
        try
        {
            nodeService.removeAspect(nodeRef, DictionaryBootstrap.ASPECT_QNAME_CONTENT);
            fail("Failed to prevent removal of type-required aspect");
        }
        catch (InvalidAspectException e)
        {
            // expected
        }
    }
    
    public void testDelete() throws Exception
    {
        ChildAssocRef assocRef = nodeService.createNode(rootNodeRef,
                null,
                QName.createQName("path1"),
                DictionaryBootstrap.TYPE_QNAME_CONTAINER);
        NodeRef nodeRef = assocRef.getChildRef();
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
    
    public void testAddChild() throws Exception
    {
        // create a bogus reference
        NodeRef bogusChildRef = new NodeRef(rootNodeRef.getStoreRef(), "BOGUS");
        try
        {
            nodeService.addChild(rootNodeRef, bogusChildRef, QName.createQName("BOGUS_PATH"));
            fail("Failed to detect invalid child node reference");
        }
        catch (InvalidNodeRefException e)
        {
            // expected
        }
        ChildAssocRef assocRef = nodeService.createNode(rootNodeRef,
                null,
                QName.createQName("pathA"),
                DictionaryBootstrap.TYPE_QNAME_CONTAINER);
                CodeMonkey.todo("Fix test checks");
//        int countBefore = countChildrenOfNode(rootNodeRef);
//        assertEquals("Root children count incorrect", 1, countBefore);
        // associate the two nodes
        nodeService.addChild(rootNodeRef, assocRef.getChildRef(), QName.createQName("pathB"));
        // there should now be 2 child assocs on the root
        CodeMonkey.todo("Fix test checks");
//        int countAfter = countChildrenOfNode(rootNodeRef);
//        assertEquals("Root children count incorrect", 2, countAfter);
    }
    
    public void testRemoveChildByRef() throws Exception
    {
        ChildAssocRef pathARef = nodeService.createNode(rootNodeRef,
                null,
                QName.createQName("pathA"),
                DictionaryBootstrap.TYPE_QNAME_CONTAINER);
        NodeRef nodeRef = pathARef.getChildRef();
        ChildAssocRef pathBRef = nodeService.addChild(rootNodeRef, nodeRef, QName.createQName("pathB"));
        ChildAssocRef pathCRef = nodeService.addChild(rootNodeRef, nodeRef, QName.createQName("pathC"));
        // delete all the associations
        Collection<EntityRef> deletedRefs = nodeService.removeChild(rootNodeRef, nodeRef);
        assertTrue("Primary child not deleted", deletedRefs.contains(nodeRef));
        assertTrue("Primary A path not deleted", deletedRefs.contains(pathARef));
        assertTrue("Secondary B path not deleted", deletedRefs.contains(pathBRef));
        assertTrue("Secondary C path not deleted", deletedRefs.contains(pathCRef));
    }
    
    public void testRemoveChildByName() throws Exception
    {
        ChildAssocRef assocRef = nodeService.createNode(rootNodeRef,
                null,
                QName.createQName("nsA", "pathA"),
                DictionaryBootstrap.TYPE_QNAME_CONTAINER);
        NodeRef nodeRef = assocRef.getChildRef();
        nodeService.addChild(rootNodeRef, nodeRef, QName.createQName("nsB1", "pathB"));
        nodeService.addChild(rootNodeRef, nodeRef, QName.createQName("nsB2", "pathB"));
        nodeService.addChild(rootNodeRef, nodeRef, QName.createQName("nsC", "pathC"));
        // delete all the associations
        nodeService.removeChildren(rootNodeRef, QName.createQName("nsB1", "pathB"));
        
        // get the children of the root
        Collection<ChildAssocRef> childAssocRefs = nodeService.getChildAssocs(rootNodeRef);
        assertEquals("Unexpected number of children under root", 3, childAssocRefs.size());
        
        // flush and clear
        flushAndClear();
        
        // get the children again to check that the flushing didn't produce different results
        childAssocRefs = nodeService.getChildAssocs(rootNodeRef);
        assertEquals("Unexpected number of children under root", 3, childAssocRefs.size());
    }
    
    public void testProperties() throws Exception
    {
        QName qnameProperty1 = QName.createQName("PROPERTY1");
        QName qnameProperty2 = QName.createQName("PROPERTY2");
        
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>(5);
        properties.put(qnameProperty1, "VALUE1");
        // add some properties to the root node
        nodeService.setProperties(rootNodeRef, properties);
        // set a single property
        nodeService.setProperty(rootNodeRef, qnameProperty2, "VALUE2");
        
        // force a flush
        getSession().flush();
        getSession().clear();
        
        // now get them back
        Map<QName, Serializable> checkMap = nodeService.getProperties(rootNodeRef);
        assertNotNull("Properties were not set/retrieved", checkMap);
        assertNotNull("Property value not set", checkMap.get(qnameProperty1));
        assertNotNull("Property value not set", checkMap.get(qnameProperty2));
        
        // get a single property direct from the node
        Serializable valueCheck = nodeService.getProperty(rootNodeRef, qnameProperty2);
        assertNotNull("Property value not set", valueCheck);
        assertEquals("Property value incorrect", "VALUE2", valueCheck);
        
        // set the property value to null
        try
        {
            nodeService.setProperty(rootNodeRef, qnameProperty2, null);            
        }
        catch (IllegalArgumentException e)
        {
			fail("Null property values are allowed");
        }
        // try setting null value as part of complete set
        try
        {
            properties = nodeService.getProperties(rootNodeRef);
            properties.put(qnameProperty1, null);
            nodeService.setProperties(rootNodeRef, properties);
        }
        catch (IllegalArgumentException e)
        {
            fail("Null property values are allowed in the map");
        }
    }
    
    public void testGetParentAssocs() throws Exception
    {
        Map<QName, ChildAssocRef> assocRefs = buildNodeGraph();
        ChildAssocRef n3pn6Ref = assocRefs.get(QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "n3_p_n6"));
        ChildAssocRef n5pn7Ref = assocRefs.get(QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "n5_p_n7"));
        ChildAssocRef n6pn8Ref = assocRefs.get(QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "n6_p_n8"));
        ChildAssocRef n7n8Ref = assocRefs.get(QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "n7_n8"));
        // get a child node's parents
        NodeRef n8Ref = assocRefs.get(QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "n6_p_n8")).getChildRef();
        List<ChildAssocRef> parentAssocs = nodeService.getParentAssocs(n8Ref);
        assertEquals("Incorrect number of parents", 2, parentAssocs.size());
        assertTrue("Expected assoc not found", parentAssocs.contains(n6pn8Ref));
        assertTrue("Expected assoc not found", parentAssocs.contains(n7n8Ref));
        
        // check that we can retrieve the primary parent
        ChildAssocRef primaryParentAssocCheck = nodeService.getPrimaryParent(n8Ref);
        assertEquals("Primary parent assoc not retrieved", n6pn8Ref, primaryParentAssocCheck);
        
        // check that the root node returns a null primary parent
        ChildAssocRef rootNodePrimaryAssoc = nodeService.getPrimaryParent(rootNodeRef);
        assertNull("Expected null primary parent for root node", rootNodePrimaryAssoc.getParentRef());
    }
    
    public void testGetChildAssocs() throws Exception
    {
        Map<QName, ChildAssocRef> assocRefs = buildNodeGraph();
        NodeRef n1Ref = assocRefs.get(QName.createQName(NamespaceService.ALFRESCO_TEST_URI,"root_p_n1")).getChildRef();
        
        // get the parent node's children
        Collection<ChildAssocRef> childAssocRefs = nodeService.getChildAssocs(n1Ref);
        assertEquals("Incorrect number of children", 2, childAssocRefs.size());
    }
    
    public void testMoveNode() throws Exception
    {
        Map<QName, ChildAssocRef> assocRefs = buildNodeGraph();
        ChildAssocRef n5pn7Ref = assocRefs.get(QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "n5_p_n7"));
        ChildAssocRef n6pn8Ref = assocRefs.get(QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "n6_p_n8"));
        NodeRef n5Ref = n5pn7Ref.getParentRef();
        NodeRef n6Ref = n6pn8Ref.getParentRef();
        NodeRef n8Ref = n6pn8Ref.getChildRef();
        // move n8 to n5
        ChildAssocRef assocRef = nodeService.moveNode(
                n8Ref,
                n5Ref,
                null,
                QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "n5_p_n8"));
        // check that n6 is no longer the parent
        List<ChildAssocRef> n6ChildRefs = nodeService.getChildAssocs(
                n6Ref,
                QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "n6_p_n8"));
        assertEquals("Primary child assoc is still present", 0, n6ChildRefs.size());
        // check that n5 is the parent
        ChildAssocRef checkRef = nodeService.getPrimaryParent(n8Ref);
        assertEquals("Primary assoc incorrent", assocRef, checkRef);
    }
    
    /**
     * Creates a named association between two nodes
     * 
     * @return Returns an array of [source real NodeRef][target reference NodeRef][assoc name String]
     */
    private NodeAssocRef createAssociation() throws Exception
    {
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>(5);
        fillProperties(DictionaryBootstrap.TYPE_QNAME_REFERENCE, properties);
        
        ChildAssocRef childAssocRef = nodeService.createNode(rootNodeRef,
                null,
                QName.createQName(null, "N1"),
                DictionaryBootstrap.TYPE_QNAME_BASE);
        NodeRef sourceRef = childAssocRef.getChildRef();
        childAssocRef = nodeService.createNode(rootNodeRef,
                null,
                QName.createQName(null, "N2"),
                DictionaryBootstrap.TYPE_QNAME_REFERENCE,
                properties);
        NodeRef targetRef = childAssocRef.getChildRef();
        
        QName qname = QName.createQName("next");
        NodeAssocRef assocRef = nodeService.createAssociation(sourceRef, targetRef, qname);
        // done
        return assocRef;
    }
    
    public void testCreateAssociation() throws Exception
    {
        NodeAssocRef assocRef = createAssociation();
        NodeRef sourceRef = assocRef.getSourceRef();
        NodeRef targetRef = assocRef.getTargetRef();
        QName qname = assocRef.getQName();
        try
        {
            // attempt the association in reverse
            nodeService.createAssociation(sourceRef, targetRef, qname);
            fail("Incorrect node type not detected");
        }
        catch (RuntimeException e)
        {
            // expected
        }
        try
        {
            // attempt repeat
            nodeService.createAssociation(sourceRef, targetRef, qname);
            fail("Duplicate assocation not detected");
        }
        catch (AssociationExistsException e)
        {
            // expected
        }
    }
    
    public void testRemoveAssociation() throws Exception
    {
        NodeAssocRef assocRef = createAssociation();
        NodeRef sourceRef = assocRef.getSourceRef();
        NodeRef targetRef = assocRef.getTargetRef();
        QName qname = assocRef.getQName();
        // remove the association
        nodeService.removeAssociation(sourceRef, targetRef, qname);
        // remake association
        nodeService.createAssociation(sourceRef, targetRef, qname);
    }
    
    public void testGetTargetAssocs() throws Exception
    {
        NodeAssocRef assocRef = createAssociation();
        NodeRef sourceRef = assocRef.getSourceRef();
        NodeRef targetRef = assocRef.getTargetRef();
        QName qname = assocRef.getQName();
        // get the target assocs
        List<NodeAssocRef> targetAssocs = nodeService.getTargetAssocs(sourceRef, qname);
        assertEquals("Incorrect number of targets", 1, targetAssocs.size());
        assertTrue("Target not found", targetAssocs.contains(assocRef));
    }
    
    public void testGetSourceAssocs() throws Exception
    {
        NodeAssocRef assocRef = createAssociation();
        NodeRef sourceRef = assocRef.getSourceRef();
        NodeRef targetRef = assocRef.getTargetRef();
        QName qname = assocRef.getQName();
        // get the source assocs
        List<NodeAssocRef> sourceAssocs = nodeService.getSourceAssocs(targetRef, qname);
        assertEquals("Incorrect number of source assocs", 1, sourceAssocs.size());
        assertTrue("Source not found", sourceAssocs.contains(assocRef));
    }
    
    /**
     * @see #buildNodeGraph() 
     */
    public void testGetPath() throws Exception
    {
        Map<QName, ChildAssocRef> assocRefs = buildNodeGraph();
        NodeRef n8Ref = assocRefs.get(QName.createQName(NamespaceService.ALFRESCO_TEST_URI,"n6_p_n8")).getChildRef();

        // get the primary node path for n8
        Path path = nodeService.getPath(n8Ref);
        assertEquals("Primary path incorrect",
                "/{" + NamespaceService.ALFRESCO_TEST_URI + "}root_p_n1/{" + NamespaceService.ALFRESCO_TEST_URI + "}n1_p_n3/{" + NamespaceService.ALFRESCO_TEST_URI + "}n3_p_n6/{" + NamespaceService.ALFRESCO_TEST_URI + "}n6_p_n8",
                path.toString());
    }
    
    /**
     * @see #buildNodeGraph() 
     */
    public void testGetPaths() throws Exception
    {
        Map<QName, ChildAssocRef> assocRefs = buildNodeGraph();
        NodeRef n6Ref = assocRefs.get(QName.createQName(NamespaceService.ALFRESCO_TEST_URI,"n3_p_n6")).getChildRef();
        NodeRef n8Ref = assocRefs.get(QName.createQName(NamespaceService.ALFRESCO_TEST_URI,"n6_p_n8")).getChildRef();
        
        // get all paths for the root node
        Collection<Path> paths = nodeService.getPaths(rootNodeRef, false);
        assertEquals("Root node must have exactly 1 path", 1, paths.size());
        Path rootPath = paths.toArray(new Path[1])[0];
        assertNotNull("Root node path must have 1 element", rootPath.last());
        assertEquals("Root node path must have 1 element", rootPath.first(), rootPath.last());

        // get all paths for n8
        paths = nodeService.getPaths(n8Ref, false);
        assertEquals("Incorrect path count", 5, paths.size());  // n6 is a root as well
        // check that each path element has parent node ref, qname and child node ref
        for (Path path : paths)
        {
            // get the path elements
            for (Path.Element element : path)
            {
                assertTrue("Path element of incorrect type", element instanceof Path.ChildAssocElement);
                Path.ChildAssocElement childAssocElement = (Path.ChildAssocElement) element;
                ChildAssocRef ref = childAssocElement.getRef();
                if (childAssocElement != path.first())
                {
                    // for all but the first element, the parent and assoc qname must be set
                    assertNotNull("Parent node ref not set", ref.getParentRef());
                    assertNotNull("QName not set", ref.getQName());
                }
                // all associations must have a child ref
                assertNotNull("Child node ref not set", ref.getChildRef());
            }
        }

        // get primary path for n8
        paths = nodeService.getPaths(n8Ref, true);
        assertEquals("Incorrect path count", 1, paths.size());
        
        // check that a cyclic path is detected - make n8_n2
        try
        {
            nodeService.addChild(n8Ref, n6Ref, QName.createQName("n8_n6"));
            nodeService.getPaths(n8Ref, false);
            fail("Cyclic relationship not detected");
        }
        catch (CyclicChildRelationshipException e)
        {
            // expected
        }
        catch (StackOverflowError e)
        {
            fail("Cyclic relationship caused stack overflow");
        }
    }
    
    public void testNodeXPath() throws Exception
    {
        Map<QName, ChildAssocRef> assocRefs = buildNodeGraph();
        QName qname = QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "n2_p_n4");
        
        NodeServiceXPath xpath;
        List list;
        
        DynamicNamespacePrefixResolver namespacePrefixResolver = new DynamicNamespacePrefixResolver(null);
        namespacePrefixResolver.addDynamicNamespace(NamespaceService.ALFRESCO_PREFIX, NamespaceService.ALFRESCO_URI);
        namespacePrefixResolver.addDynamicNamespace(NamespaceService.ALFRESCO_TEST_PREFIX, NamespaceService.ALFRESCO_TEST_URI);
        
        xpath = new NodeServiceXPath("//.[@alftest:animal='monkey']", nodeService, namespacePrefixResolver, null, false);
        xpath.addNamespace(NamespaceService.ALFRESCO_TEST_PREFIX, NamespaceService.ALFRESCO_TEST_URI);
        list = xpath.selectNodes(new ChildAssocRef(null, null, rootNodeRef));
        assertEquals(1, list.size());
      
        
        xpath = new NodeServiceXPath("*", nodeService, namespacePrefixResolver, null, false);
        list = xpath.selectNodes(new ChildAssocRef(null, null, rootNodeRef));
        assertEquals(2, list.size());
        
        xpath = new NodeServiceXPath("*/*", nodeService, namespacePrefixResolver, null, false);
        list = xpath.selectNodes(new ChildAssocRef(null, null, rootNodeRef));
        assertEquals(4, list.size());
        
        xpath = new NodeServiceXPath("*/*/*", nodeService, namespacePrefixResolver, null, false);
        list = xpath.selectNodes(new ChildAssocRef(null, null, rootNodeRef));
        assertEquals(3, list.size());
        
        xpath = new NodeServiceXPath("*/*/*/*", nodeService, namespacePrefixResolver, null, false);
        list = xpath.selectNodes(new ChildAssocRef(null, null, rootNodeRef));
        assertEquals(2, list.size());
        
        xpath = new NodeServiceXPath("*/*/*/*/..", nodeService, namespacePrefixResolver, null, false);
        list = xpath.selectNodes(new ChildAssocRef(null, null, rootNodeRef));
        assertEquals(2, list.size());
        
        xpath = new NodeServiceXPath("*//.", nodeService, namespacePrefixResolver, null, false);
        list = xpath.selectNodes(new ChildAssocRef(null, null, rootNodeRef));
        assertEquals(11, list.size());
        
        xpath = new NodeServiceXPath("alftest:root_p_n1", nodeService, namespacePrefixResolver, null, false);
        xpath.addNamespace(NamespaceService.ALFRESCO_TEST_PREFIX, NamespaceService.ALFRESCO_TEST_URI);
        list = xpath.selectNodes(new ChildAssocRef(null, null, rootNodeRef));
        assertEquals(1, list.size());
        
        xpath = new NodeServiceXPath("*//.[@alftest:animal]", nodeService, namespacePrefixResolver, null, false);
        xpath.addNamespace(NamespaceService.ALFRESCO_TEST_PREFIX, NamespaceService.ALFRESCO_TEST_URI);
        list = xpath.selectNodes(new ChildAssocRef(null, null, rootNodeRef));
        assertEquals(1, list.size());
        
        xpath = new NodeServiceXPath("*//.[@alftest:animal='monkey']", nodeService, namespacePrefixResolver, null, false);
        xpath.addNamespace(NamespaceService.ALFRESCO_TEST_PREFIX, NamespaceService.ALFRESCO_TEST_URI);
        list = xpath.selectNodes(new ChildAssocRef(null, null, rootNodeRef));
        assertEquals(1, list.size());
        
        xpath = new NodeServiceXPath("//.[@alftest:animal='monkey']", nodeService, namespacePrefixResolver, null, false);
        xpath.addNamespace(NamespaceService.ALFRESCO_TEST_PREFIX, NamespaceService.ALFRESCO_TEST_URI);
        list = xpath.selectNodes(new ChildAssocRef(null, null, rootNodeRef));
        assertEquals(1, list.size());
        
        QueryParameterDefImpl paramDef = new QueryParameterDefImpl(QName.createQName("alf:test", namespacePrefixResolver), dictionaryService.getPropertyType(PropertyTypeDefinition.TEXT), true, "monkey");
        xpath = new NodeServiceXPath("//.[@alftest:animal=$alf:test]", nodeService, namespacePrefixResolver, new QueryParameterDefinition[]{paramDef}, false);
        xpath.addNamespace(NamespaceService.ALFRESCO_TEST_PREFIX, NamespaceService.ALFRESCO_TEST_URI);
        xpath.addNamespace(NamespaceService.ALFRESCO_PREFIX, NamespaceService.ALFRESCO_URI);
        list = xpath.selectNodes(new ChildAssocRef(null, null, rootNodeRef));
        assertEquals(1, list.size());
        
        xpath = new NodeServiceXPath(".", nodeService, namespacePrefixResolver, null, false);
        list = xpath.selectNodes(assocRefs.get(qname));
        assertEquals(1, list.size());
        
        xpath = new NodeServiceXPath("..", nodeService, namespacePrefixResolver, null, false);
        list = xpath.selectNodes(assocRefs.get(qname));
        assertEquals(1, list.size());
        
        xpath = new NodeServiceXPath("..", nodeService, namespacePrefixResolver, null, true);
        list = xpath.selectNodes(assocRefs.get(qname));
        assertEquals(2, list.size());
        
        xpath = new NodeServiceXPath("//@alftest:animal", nodeService, namespacePrefixResolver, null, true);
        xpath.addNamespace(NamespaceService.ALFRESCO_TEST_PREFIX, NamespaceService.ALFRESCO_TEST_URI);
        list = xpath.selectNodes(assocRefs.get(qname));
        assertEquals(1, list.size());
        assertTrue(list.get(0) instanceof DocumentNavigator.Property);
        
        xpath = new NodeServiceXPath("//@alftest:reference", nodeService, namespacePrefixResolver, null, true);
        xpath.addNamespace(NamespaceService.ALFRESCO_TEST_PREFIX, NamespaceService.ALFRESCO_TEST_URI);
        list = xpath.selectNodes(assocRefs.get(qname));
        assertEquals(1, list.size());
        
        xpath = new NodeServiceXPath("deref(/alftest:root_p_n1/alftest:n1_p_n3/@alftest:reference, '')", nodeService, namespacePrefixResolver, null, true);
        xpath.addNamespace(NamespaceService.ALFRESCO_TEST_PREFIX, NamespaceService.ALFRESCO_TEST_URI);
        list = xpath.selectNodes(assocRefs.get(qname));
        assertEquals(1, list.size());
        
    }
    
    
    public void testSelectAPI() throws Exception
    {
        Map<QName, ChildAssocRef> assocRefs = buildNodeGraph();
        
        DynamicNamespacePrefixResolver namespacePrefixResolver = new DynamicNamespacePrefixResolver(null);
        namespacePrefixResolver.addDynamicNamespace(NamespaceService.ALFRESCO_PREFIX, NamespaceService.ALFRESCO_URI);
        namespacePrefixResolver.addDynamicNamespace(NamespaceService.ALFRESCO_TEST_PREFIX, NamespaceService.ALFRESCO_TEST_URI);
        
        List<ChildAssocRef> answer =  nodeService.selectNodes(rootNodeRef, "*", null, namespacePrefixResolver, false);
        assertEquals(2, answer.size());
        
        List<Serializable> attributes = nodeService.selectProperties(rootNodeRef, "//@alftest:animal", null, namespacePrefixResolver, false);
        assertEquals(1, attributes.size());
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
    
//  Must move to suite of performance tests
//  and must be actual tests, i.e. with asserts, etc.
//    public void xtestAddPerformance() throws Exception
//    {
//        timer.start();
//        Map<NodeRef, QName> directories = new HashMap<NodeRef, QName>();
//        directories.put(rootNodeRef, QName.createQName("{ } "));
//        createLevels(directories, 1);    
//        setComplete();
//        
//    }
//    
//    public void xtestAddPerformance2() throws Exception
//    {
//        timer.stop();
//        System.out.println("Created in "+timer.getDuration());
//        timer.reset();
//        timer.start();
//        Map<NodeRef, QName> directories = new HashMap<NodeRef, QName>();
//        directories.put(rootNodeRef, QName.createQName("{ } "));
//        createLevels(directories, 2);
//        setComplete();
//    }
//    
//    public void xtestAddPerformance3() throws Exception
//    {
//        timer.stop();
//        System.out.println("Created in "+timer.getDuration());
//        timer.reset();
//        timer.start();
//        Map<NodeRef, QName> directories = new HashMap<NodeRef, QName>();
//        directories.put(rootNodeRef, QName.createQName("{ } "));
//        createLevels(directories, 3);
//        timer.stop();
//        setComplete();
//        System.out.println("Created in "+timer.getDuration());
//    }
//    
//    public void xtestAddPerformance4() throws Exception
//    {
//        timer.stop();
//        System.out.println("Created in "+timer.getDuration());
//        timer.reset();
//    }
//    
//    
//
//    private void createLevels( Map<NodeRef, QName> map, int levels)
//    {
//        for(NodeRef ref: map.keySet())
//        {
//            QName qname = map.get(ref);
//            Map<NodeRef, QName> directories = createLevel(ref, qname.getLocalName(), "file", "directory");
//            if(levels > 0)
//            {
//                createLevels(directories, levels-1);
//            }
//        }
//    }
//    
//    private Map<NodeRef, QName> createLevel(NodeRef container, String root, String filePrefix, String directoryPrefix)
//    {
//        String ns = NamespaceService.ALFRESCO_TEST_URI;
//        QName qname = null;
//        ChildAssocRef assoc = null;
//        Map<NodeRef, QName> ret = new HashMap<NodeRef, QName>(10);
//
//        for (int i = 0; i < 10; i++)
//        {
//            qname = QName.createQName(ns, root +"_ "+ directoryPrefix + "_" + i);
//            assoc = nodeService.createNode(container,
//                    null,
//                    qname,
//                    DictionaryBootstrap.TYPE_QNAME_CONTAINER);
//            NodeRef ref = assoc.getChildRef();
//            ret.put(ref, qname);
//        }
//
//        for (int i = 10; i < 100; i++)
//        {
//            qname = QName.createQName(ns, root + filePrefix + "_" + i);
//            assoc = nodeService.createNode(container,
//                    null,
//                    qname,
//                    DictionaryBootstrap.TYPE_QNAME_CONTAINER);
//            Map<QName, Serializable> properties = nodeService.getProperties(assoc.getChildRef());
//            //properties.put(DictionaryBootstrap.PROP_QNAME_MIME_TYPE, "application/msword");
//            properties.put(DictionaryBootstrap.PROP_QNAME_MIME_TYPE, "text/plain");
//            properties.put(DictionaryBootstrap.PROP_QNAME_ENCODING, "UTF-16");
//            nodeService.addAspect(assoc.getChildRef(), DictionaryBootstrap.ASPECT_QNAME_CONTENT, properties);
//            ContentWriter writer = contentService.getUpdatingWriter(assoc.getChildRef());
//            writer.putContent("I need to make a 50K document somehow. So I will type and type and type until I have something like it …. But then word is so overblown it will probably be that big already!/n"+
//                    "/n" +
//                    "Well hat is 25k and it contains nothing./n"+
//                    "Good work all round there./n"+
//                    "/n"+
//                   "So does this make much difference?/n/n");
//            //InputStream is = this.getClass().getClassLoader().getResourceAsStream("text50.txt");
//            //writer.putContent(is);
//            NodeRef ref = assoc.getChildRef();
//        }
//
//        return ret;
//    }
}
