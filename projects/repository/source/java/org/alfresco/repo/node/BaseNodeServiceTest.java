package org.alfresco.repo.node;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.content.ContentService;
import org.alfresco.repo.dictionary.ClassDefinition;
import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.dictionary.PropertyDefinition;
import org.alfresco.repo.dictionary.PropertyTypeDefinition;
import org.alfresco.repo.dictionary.impl.DictionaryBootstrap;
import org.alfresco.repo.dictionary.impl.DictionaryComponent;
import org.alfresco.repo.dictionary.impl.DictionaryDAO;
import org.alfresco.repo.dictionary.impl.M2Model;
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
import org.alfresco.util.debug.CodeMonkey;
import org.hibernate.Session;

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
    public static final String NAMESPACE = "http://www.alfresco.org/test/BaseNodeServiceTest";
    public static final String TEST_PREFIX = "test";
    public static final QName TYPE_QNAME_TEST_CONTENT = QName.createQName(NAMESPACE, "content");
    public static final QName ASPECT_QNAME_TEST_TITLED = QName.createQName(NAMESPACE, "titled");
    public static final QName ASSOC_TYPE_QNAME = DictionaryBootstrap.ASSOC_QNAME_CONTAINS;
    
    protected DictionaryService dictionaryService;
    protected NodeService nodeService;
    /** populated during setup */
    protected NodeRef rootNodeRef;

    private ContentService contentService;

    protected void onSetUpInTransaction() throws Exception
    {
        DictionaryDAO dictionaryDao = (DictionaryDAO) applicationContext.getBean("dictionaryDAO");
        // load the system model
        ClassLoader cl = BaseNodeServiceTest.class.getClassLoader();
        InputStream modelStream = cl.getResourceAsStream("org/alfresco/repo/dictionary/impl/content_model.xml");
        assertNotNull(modelStream);
        M2Model model = M2Model.createModel(modelStream);
        dictionaryDao.putModel(model);
        // load the test model
        modelStream = cl.getResourceAsStream("org/alfresco/repo/node/BaseNodeServiceTest_model.xml");
        assertNotNull(modelStream);
        model = M2Model.createModel(modelStream);
        dictionaryDao.putModel(model);
        
        DictionaryComponent dictionary = new DictionaryComponent();
        dictionary.setDictionaryDAO(dictionaryDao);
        dictionaryService = dictionary;
        
        nodeService = getNodeService();
        contentService = getContentService();
        
        // create a first store directly
        StoreRef storeRef = nodeService.createStore(
                StoreRef.PROTOCOL_WORKSPACE,
                "Test_" + System.currentTimeMillis());
        rootNodeRef = nodeService.getRootNode(storeRef);
    }
    
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
     * The namespace URI for all associations is <b>{@link BaseNodeServiceTest#NAMESPACE}</b>.
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
        String ns = BaseNodeServiceTest.NAMESPACE;
        QName qname = null;
        ChildAssocRef assoc = null;
        Map<QName, ChildAssocRef> ret = new HashMap<QName, ChildAssocRef>(13);
        
        // LEVEL 0

        // LEVEL 1
        qname = QName.createQName(ns, "root_p_n1");
        assoc = nodeService.createNode(rootNodeRef, ASSOC_TYPE_QNAME, qname, DictionaryBootstrap.TYPE_QNAME_CONTAINER);
        ret.put(qname, assoc);
        NodeRef n1 = assoc.getChildRef();

        qname = QName.createQName(ns, "root_p_n2");
        assoc = nodeService.createNode(rootNodeRef, ASSOC_TYPE_QNAME, qname, DictionaryBootstrap.TYPE_QNAME_CONTAINER);
        ret.put(qname, assoc);
        NodeRef n2 = assoc.getChildRef();

        // LEVEL 2
        
        Map<QName, Serializable> attributes = new HashMap<QName, Serializable>();
        attributes.put(QName.createQName(ns, "animal"), "monkey");
        attributes.put(QName.createQName(ns, "reference"), n2.toString());
        
        qname = QName.createQName(ns, "n1_p_n3");
        assoc = nodeService.createNode(n1, ASSOC_TYPE_QNAME, qname, DictionaryBootstrap.TYPE_QNAME_CONTAINER, attributes);
        ret.put(qname, assoc);
        NodeRef n3 = assoc.getChildRef();

        qname = QName.createQName(ns, "n2_p_n4");
        assoc = nodeService.createNode(n2, ASSOC_TYPE_QNAME, qname, DictionaryBootstrap.TYPE_QNAME_CONTAINER);
        ret.put(qname, assoc);
        NodeRef n4 = assoc.getChildRef();

        qname = QName.createQName(ns, "n1_n4");
        assoc = nodeService.addChild(n1, n4, ASSOC_TYPE_QNAME, qname);
        ret.put(qname, assoc);

        qname = QName.createQName(ns, "n2_p_n5");
        assoc = nodeService.createNode(n2, ASSOC_TYPE_QNAME, qname, DictionaryBootstrap.TYPE_QNAME_CONTAINER);
        ret.put(qname, assoc);
        NodeRef n5 = assoc.getChildRef();

        // LEVEL 3
        qname = QName.createQName(ns, "n3_p_n6");
        assoc = nodeService.createNode(n3, ASSOC_TYPE_QNAME, qname, DictionaryBootstrap.TYPE_QNAME_CONTAINER);
        ret.put(qname, assoc);
        NodeRef n6 = assoc.getChildRef();
        nodeService.addAspect(n6,
                DictionaryBootstrap.ASPECT_QNAME_ROOT,
                Collections.<QName, Serializable>emptyMap());

        qname = QName.createQName(ns, "n4_n6");
        assoc = nodeService.addChild(n4, n6, ASSOC_TYPE_QNAME, qname);
        ret.put(qname, assoc);

        qname = QName.createQName(ns, "n5_p_n7");
        assoc = nodeService.createNode(n5, ASSOC_TYPE_QNAME, qname, DictionaryBootstrap.TYPE_QNAME_CONTAINER);
        ret.put(qname, assoc);
        NodeRef n7 = assoc.getChildRef();

        // LEVEL 4
        qname = QName.createQName(ns, "n6_p_n8");
        assoc = nodeService.createNode(n6, ASSOC_TYPE_QNAME, qname, DictionaryBootstrap.TYPE_QNAME_CONTAINER);
        ret.put(qname, assoc);
        NodeRef n8 = assoc.getChildRef();

        qname = QName.createQName(ns, "n7_n8");
        assoc = nodeService.addChild(n7, n8, ASSOC_TYPE_QNAME, qname);
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
        // and is of the correct type
        QName rootType = nodeService.getType(storeRootNode);
        assertEquals("Store root node of incorrect type", DictionaryBootstrap.TYPE_QNAME_STOREROOT, rootType);
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
    
    public void testCreateNode() throws Exception
    {
        ChildAssocRef assocRef = nodeService.createNode(rootNodeRef,
                ASSOC_TYPE_QNAME,
                QName.createQName("pathA"),
                DictionaryBootstrap.TYPE_QNAME_CONTAINER);
        assertEquals("Assoc type qname not set", ASSOC_TYPE_QNAME, assocRef.getTypeQName());
        assertEquals("Assoc qname not set", QName.createQName("pathA"), assocRef.getQName());
        NodeRef childRef = assocRef.getChildRef();
        QName checkType = nodeService.getType(childRef);
        assertEquals("Child node type incorrect", DictionaryBootstrap.TYPE_QNAME_CONTAINER, checkType);
    }

    public void testGetType() throws Exception
    {
        ChildAssocRef assocRef = nodeService.createNode(rootNodeRef,
                ASSOC_TYPE_QNAME,
                QName.createQName("pathA"),
                DictionaryBootstrap.TYPE_QNAME_CONTAINER);
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
                ASSOC_TYPE_QNAME,
                QName.createQName(BaseNodeServiceTest.NAMESPACE, "test-container"),
                DictionaryBootstrap.TYPE_QNAME_CONTAINER);
        NodeRef nodeRef = assocRef.getChildRef();
        // add the content aspect to the node, but don't supply any properties
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>(20);
        try
        {
            nodeService.addAspect(nodeRef, BaseNodeServiceTest.ASPECT_QNAME_TEST_TITLED, properties);
            fail("Failed to detect inadequate properties for aspect");
        }
        catch (PropertyException e)
        {
            // expected
        }
        // get the properties required for the aspect
        fillProperties(BaseNodeServiceTest.ASPECT_QNAME_TEST_TITLED, properties);
        // get the node properties before
        Map<QName, Serializable> propertiesBefore = nodeService.getProperties(nodeRef);
        // add the aspect
        nodeService.addAspect(nodeRef, BaseNodeServiceTest.ASPECT_QNAME_TEST_TITLED, properties);
        // get the properties after and check
        Map<QName, Serializable> propertiesAfter = nodeService.getProperties(nodeRef);
        assertEquals("Aspect properties not added",
                propertiesBefore.size() + 2,
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
        assertTrue("Titled aspect not present",
                aspects.contains(BaseNodeServiceTest.ASPECT_QNAME_TEST_TITLED));
        
        // check that hasAspect works
        boolean hasAspect = nodeService.hasAspect(nodeRef, BaseNodeServiceTest.ASPECT_QNAME_TEST_TITLED);
        assertTrue("Aspect not confirmed to be on node", hasAspect);
        
        // remove the aspect
        nodeService.removeAspect(nodeRef, BaseNodeServiceTest.ASPECT_QNAME_TEST_TITLED);
        hasAspect = nodeService.hasAspect(nodeRef, BaseNodeServiceTest.ASPECT_QNAME_TEST_TITLED);
        assertFalse("Aspect not removed from node", hasAspect);
        
        // check that the associated properties were removed
        propertiesAfter = nodeService.getProperties(nodeRef);
        assertEquals("Aspect properties not removed",
                propertiesBefore.size(),
                propertiesAfter.size());
    }
    
    public void testCreateNodeNoProperties() throws Exception
    {
        // flush to ensure that the pure JDBC query will work
        ChildAssocRef assocRef = nodeService.createNode(rootNodeRef,
                ASSOC_TYPE_QNAME,
                QName.createQName("path1"),
                DictionaryBootstrap.TYPE_QNAME_CONTAINER);
        NodeRef nodeRef = assocRef.getChildRef();
        // count the nodes with the given id
        int count = countNodesById(nodeRef);
        assertEquals("Unexpected number of nodes present", 1, count);
    }
    
    /**
     * @see #ASPECT_QNAME_TEST_TITLED
     */
    public void testCreateNodeWithProperties() throws Exception
    {
        try
        {
            ChildAssocRef assocRef = nodeService.createNode(rootNodeRef,
                    ASSOC_TYPE_QNAME,
                    QName.createQName("MyContentNode"),
                    TYPE_QNAME_TEST_CONTENT);
            fail("Failed to detect missing properties for type");
        }
        catch (PropertyException e)
        {
            // exptected
        }
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>(5);
        // fill properties
        fillProperties(TYPE_QNAME_TEST_CONTENT, properties);
        fillProperties(ASPECT_QNAME_TEST_TITLED, properties);
        
        // create node for real
        ChildAssocRef assocRef = nodeService.createNode(rootNodeRef,
                ASSOC_TYPE_QNAME,
                QName.createQName("MyContent"),
                TYPE_QNAME_TEST_CONTENT,
                properties);
        NodeRef nodeRef = assocRef.getChildRef();
        // check that the titled aspect is present
        assertTrue("Titled aspect not present",
                nodeService.hasAspect(nodeRef, ASPECT_QNAME_TEST_TITLED));
        
        // attempt to remove the aspect
        try
        {
            nodeService.removeAspect(nodeRef, ASPECT_QNAME_TEST_TITLED);
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
                ASSOC_TYPE_QNAME,
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
            nodeService.addChild(rootNodeRef, bogusChildRef, ASSOC_TYPE_QNAME, QName.createQName("BOGUS_PATH"));
            fail("Failed to detect invalid child node reference");
        }
        catch (InvalidNodeRefException e)
        {
            // expected
        }
        ChildAssocRef assocRef = nodeService.createNode(rootNodeRef,
                ASSOC_TYPE_QNAME,
                QName.createQName("pathA"),
                DictionaryBootstrap.TYPE_QNAME_CONTAINER);
                CodeMonkey.todo("Fix test checks");
//        int countBefore = countChildrenOfNode(rootNodeRef);
//        assertEquals("Root children count incorrect", 1, countBefore);
        // associate the two nodes
        nodeService.addChild(rootNodeRef, assocRef.getChildRef(), ASSOC_TYPE_QNAME, QName.createQName("pathB"));
        // there should now be 2 child assocs on the root
        CodeMonkey.todo("Fix test checks");
//        int countAfter = countChildrenOfNode(rootNodeRef);
//        assertEquals("Root children count incorrect", 2, countAfter);
    }
    
    public void testRemoveChildByRef() throws Exception
    {
        ChildAssocRef pathARef = nodeService.createNode(rootNodeRef,
                ASSOC_TYPE_QNAME,
                QName.createQName("pathA"),
                DictionaryBootstrap.TYPE_QNAME_CONTAINER);
        NodeRef nodeRef = pathARef.getChildRef();
        ChildAssocRef pathBRef = nodeService.addChild(rootNodeRef, nodeRef, ASSOC_TYPE_QNAME, QName.createQName("pathB"));
        ChildAssocRef pathCRef = nodeService.addChild(rootNodeRef, nodeRef, ASSOC_TYPE_QNAME, QName.createQName("pathC"));
        // delete all the associations
        Collection<EntityRef> deletedRefs = nodeService.removeChild(rootNodeRef, nodeRef);
        assertTrue("Primary child not deleted", deletedRefs.contains(nodeRef));
        assertTrue("Primary A path not deleted", deletedRefs.contains(pathARef));
        assertTrue("Secondary B path not deleted", deletedRefs.contains(pathBRef));
        assertTrue("Secondary C path not deleted", deletedRefs.contains(pathCRef));
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
        ChildAssocRef n3pn6Ref = assocRefs.get(QName.createQName(BaseNodeServiceTest.NAMESPACE, "n3_p_n6"));
        ChildAssocRef n5pn7Ref = assocRefs.get(QName.createQName(BaseNodeServiceTest.NAMESPACE, "n5_p_n7"));
        ChildAssocRef n6pn8Ref = assocRefs.get(QName.createQName(BaseNodeServiceTest.NAMESPACE, "n6_p_n8"));
        ChildAssocRef n7n8Ref = assocRefs.get(QName.createQName(BaseNodeServiceTest.NAMESPACE, "n7_n8"));
        // get a child node's parents
        NodeRef n8Ref = assocRefs.get(QName.createQName(BaseNodeServiceTest.NAMESPACE, "n6_p_n8")).getChildRef();
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
        NodeRef n1Ref = assocRefs.get(QName.createQName(BaseNodeServiceTest.NAMESPACE,"root_p_n1")).getChildRef();
        
        // get the parent node's children
        Collection<ChildAssocRef> childAssocRefs = nodeService.getChildAssocs(n1Ref);
        assertEquals("Incorrect number of children", 2, childAssocRefs.size());
    }
    
    public void testMoveNode() throws Exception
    {
        Map<QName, ChildAssocRef> assocRefs = buildNodeGraph();
        ChildAssocRef n5pn7Ref = assocRefs.get(QName.createQName(BaseNodeServiceTest.NAMESPACE, "n5_p_n7"));
        ChildAssocRef n6pn8Ref = assocRefs.get(QName.createQName(BaseNodeServiceTest.NAMESPACE, "n6_p_n8"));
        NodeRef n5Ref = n5pn7Ref.getParentRef();
        NodeRef n6Ref = n6pn8Ref.getParentRef();
        NodeRef n8Ref = n6pn8Ref.getChildRef();
        // move n8 to n5
        ChildAssocRef assocRef = nodeService.moveNode(
                n8Ref,
                n5Ref,
                ASSOC_TYPE_QNAME,
                QName.createQName(BaseNodeServiceTest.NAMESPACE, "n5_p_n8"));
        // check that n6 is no longer the parent
        List<ChildAssocRef> n6ChildRefs = nodeService.getChildAssocs(
                n6Ref,
                QName.createQName(BaseNodeServiceTest.NAMESPACE, "n6_p_n8"));
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
        fillProperties(TYPE_QNAME_TEST_CONTENT, properties);
        fillProperties(ASPECT_QNAME_TEST_TITLED, properties);
        
        ChildAssocRef childAssocRef = nodeService.createNode(rootNodeRef,
                ASSOC_TYPE_QNAME,
                QName.createQName(null, "N1"),
                DictionaryBootstrap.TYPE_QNAME_CONTAINER);
        NodeRef sourceRef = childAssocRef.getChildRef();
        childAssocRef = nodeService.createNode(rootNodeRef,
                ASSOC_TYPE_QNAME,
                QName.createQName(null, "N2"),
                TYPE_QNAME_TEST_CONTENT,
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
        QName qname = assocRef.getTypeQName();
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
        QName qname = assocRef.getTypeQName();
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
        QName qname = assocRef.getTypeQName();
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
        QName qname = assocRef.getTypeQName();
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
        NodeRef n8Ref = assocRefs.get(QName.createQName(BaseNodeServiceTest.NAMESPACE,"n6_p_n8")).getChildRef();

        // get the primary node path for n8
        Path path = nodeService.getPath(n8Ref);
        assertEquals("Primary path incorrect",
                "/{" + BaseNodeServiceTest.NAMESPACE + "}root_p_n1/{" + BaseNodeServiceTest.NAMESPACE + "}n1_p_n3/{" + BaseNodeServiceTest.NAMESPACE + "}n3_p_n6/{" + BaseNodeServiceTest.NAMESPACE + "}n6_p_n8",
                path.toString());
    }
    
    /**
     * @see #buildNodeGraph() 
     */
    public void testGetPaths() throws Exception
    {
        Map<QName, ChildAssocRef> assocRefs = buildNodeGraph();
        NodeRef n6Ref = assocRefs.get(QName.createQName(BaseNodeServiceTest.NAMESPACE, "n3_p_n6")).getChildRef();
        NodeRef n8Ref = assocRefs.get(QName.createQName(BaseNodeServiceTest.NAMESPACE, "n6_p_n8")).getChildRef();
        
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
            nodeService.addChild(n8Ref, n6Ref, ASSOC_TYPE_QNAME, QName.createQName("n8_n6"));
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
        QName qname = QName.createQName(BaseNodeServiceTest.NAMESPACE, "n2_p_n4");
        
        NodeServiceXPath xpath;
        List list;
        
        DynamicNamespacePrefixResolver namespacePrefixResolver = new DynamicNamespacePrefixResolver(null);
        namespacePrefixResolver.addDynamicNamespace(BaseNodeServiceTest.TEST_PREFIX, BaseNodeServiceTest.NAMESPACE);
        
        xpath = new NodeServiceXPath("//.[@test:animal='monkey']", nodeService, namespacePrefixResolver, null, false);
        xpath.addNamespace(BaseNodeServiceTest.TEST_PREFIX, BaseNodeServiceTest.NAMESPACE);
        list = xpath.selectNodes(new ChildAssocRef(null, null, null, rootNodeRef));
        assertEquals(1, list.size());
      
        
        xpath = new NodeServiceXPath("*", nodeService, namespacePrefixResolver, null, false);
        list = xpath.selectNodes(new ChildAssocRef(null, null, null, rootNodeRef));
        assertEquals(2, list.size());
        
        xpath = new NodeServiceXPath("*/*", nodeService, namespacePrefixResolver, null, false);
        list = xpath.selectNodes(new ChildAssocRef(null, null, null, rootNodeRef));
        assertEquals(4, list.size());
        
        xpath = new NodeServiceXPath("*/*/*", nodeService, namespacePrefixResolver, null, false);
        list = xpath.selectNodes(new ChildAssocRef(null, null, null, rootNodeRef));
        assertEquals(3, list.size());
        
        xpath = new NodeServiceXPath("*/*/*/*", nodeService, namespacePrefixResolver, null, false);
        list = xpath.selectNodes(new ChildAssocRef(null, null, null, rootNodeRef));
        assertEquals(2, list.size());
        
        xpath = new NodeServiceXPath("*/*/*/*/..", nodeService, namespacePrefixResolver, null, false);
        list = xpath.selectNodes(new ChildAssocRef(null, null, null, rootNodeRef));
        assertEquals(2, list.size());
        
        xpath = new NodeServiceXPath("*//.", nodeService, namespacePrefixResolver, null, false);
        list = xpath.selectNodes(new ChildAssocRef(null, null, null, rootNodeRef));
        assertEquals(11, list.size());
        
        xpath = new NodeServiceXPath("test:root_p_n1", nodeService, namespacePrefixResolver, null, false);
        xpath.addNamespace(BaseNodeServiceTest.TEST_PREFIX, BaseNodeServiceTest.NAMESPACE);
        list = xpath.selectNodes(new ChildAssocRef(null, null, null, rootNodeRef));
        assertEquals(1, list.size());
        
        xpath = new NodeServiceXPath("*//.[@test:animal]", nodeService, namespacePrefixResolver, null, false);
        xpath.addNamespace(BaseNodeServiceTest.TEST_PREFIX, BaseNodeServiceTest.NAMESPACE);
        list = xpath.selectNodes(new ChildAssocRef(null, null, null, rootNodeRef));
        assertEquals(1, list.size());
        
        xpath = new NodeServiceXPath("*//.[@test:animal='monkey']", nodeService, namespacePrefixResolver, null, false);
        xpath.addNamespace(BaseNodeServiceTest.TEST_PREFIX, BaseNodeServiceTest.NAMESPACE);
        list = xpath.selectNodes(new ChildAssocRef(null, null, null, rootNodeRef));
        assertEquals(1, list.size());
        
        xpath = new NodeServiceXPath("//.[@test:animal='monkey']", nodeService, namespacePrefixResolver, null, false);
        xpath.addNamespace(BaseNodeServiceTest.TEST_PREFIX, BaseNodeServiceTest.NAMESPACE);
        list = xpath.selectNodes(new ChildAssocRef(null, null, null, rootNodeRef));
        assertEquals(1, list.size());
        
        QueryParameterDefImpl paramDef = new QueryParameterDefImpl(
                QName.createQName("test:test", namespacePrefixResolver),
                dictionaryService.getPropertyType(PropertyTypeDefinition.TEXT), true, "monkey");
        xpath = new NodeServiceXPath("//.[@test:animal=$test:test]", nodeService, namespacePrefixResolver, new QueryParameterDefinition[]{paramDef}, false);
        xpath.addNamespace(BaseNodeServiceTest.TEST_PREFIX,   BaseNodeServiceTest.NAMESPACE);
        list = xpath.selectNodes(new ChildAssocRef(null, null, null, rootNodeRef));
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
        
        xpath = new NodeServiceXPath("//@test:animal", nodeService, namespacePrefixResolver, null, true);
        xpath.addNamespace(BaseNodeServiceTest.TEST_PREFIX, BaseNodeServiceTest.NAMESPACE);
        list = xpath.selectNodes(assocRefs.get(qname));
        assertEquals(1, list.size());
        assertTrue(list.get(0) instanceof DocumentNavigator.Property);
        
        xpath = new NodeServiceXPath("//@test:reference", nodeService, namespacePrefixResolver, null, true);
        xpath.addNamespace(BaseNodeServiceTest.TEST_PREFIX, BaseNodeServiceTest.NAMESPACE);
        list = xpath.selectNodes(assocRefs.get(qname));
        assertEquals(1, list.size());
        
        xpath = new NodeServiceXPath("deref(/test:root_p_n1/test:n1_p_n3/@test:reference, '')", nodeService, namespacePrefixResolver, null, true);
        xpath.addNamespace(BaseNodeServiceTest.TEST_PREFIX, BaseNodeServiceTest.NAMESPACE);
        list = xpath.selectNodes(assocRefs.get(qname));
        assertEquals(1, list.size());
        
    }
    
    
    public void testSelectAPI() throws Exception
    {
        Map<QName, ChildAssocRef> assocRefs = buildNodeGraph();
        
        DynamicNamespacePrefixResolver namespacePrefixResolver = new DynamicNamespacePrefixResolver(null);
        namespacePrefixResolver.addDynamicNamespace(BaseNodeServiceTest.TEST_PREFIX, BaseNodeServiceTest.NAMESPACE);
        
        List<ChildAssocRef> answer =  nodeService.selectNodes(rootNodeRef, "/test:root_p_n1/test:n1_p_n3/*", null, namespacePrefixResolver, false);
        assertEquals(1, answer.size());
        
        //List<ChildAssocRef> 
        answer =  nodeService.selectNodes(rootNodeRef, "*", null, namespacePrefixResolver, false);
        assertEquals(2, answer.size());
        
        List<Serializable> attributes = nodeService.selectProperties(rootNodeRef, "//@test:animal", null, namespacePrefixResolver, false);
        assertEquals(1, attributes.size());
    }
    
    public void testLikeAndContains() throws Exception
    {
        Map<QName, ChildAssocRef> assocRefs = buildNodeGraph();
        
        DynamicNamespacePrefixResolver namespacePrefixResolver = new DynamicNamespacePrefixResolver(null);
        namespacePrefixResolver.addDynamicNamespace(BaseNodeServiceTest.TEST_PREFIX, BaseNodeServiceTest.NAMESPACE);
        
        List<ChildAssocRef> answer =  nodeService.selectNodes(rootNodeRef, "//*[like(@test:animal, 'monkey')", null, namespacePrefixResolver, false);
        assertEquals(0, answer.size());
        
        answer =  nodeService.selectNodes(rootNodeRef, "//*[contains('monkey')", null, namespacePrefixResolver, false);
        assertEquals(0, answer.size());
    }
}
