package org.alfresco.repo.version.lightweight;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.content.ContentService;
import org.alfresco.repo.content.ContentWriter;
import org.alfresco.repo.dictionary.ClassRef;
import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.dictionary.bootstrap.DictionaryBootstrap;
import org.alfresco.repo.dictionary.metamodel.M2Association;
import org.alfresco.repo.dictionary.metamodel.M2ChildAssociation;
import org.alfresco.repo.dictionary.metamodel.M2Property;
import org.alfresco.repo.dictionary.metamodel.M2Type;
import org.alfresco.repo.dictionary.metamodel.MetaModelDAO;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.ref.StoreRef;
import org.alfresco.repo.version.Version;
import org.alfresco.repo.version.VersionService;
import org.alfresco.repo.version.common.counter.VersionCounterDaoService;
import org.alfresco.repo.version.common.versionlabel.SerialVersionLabelPolicy;
import org.alfresco.util.BaseSpringTest;

public class VersionStoreBaseTest extends BaseSpringTest 
{
	/*
     * Services used by the tests
     */
	protected NodeService dbNodeService;
    protected VersionService lightWeightVersionStoreVersionService;
    protected VersionCounterDaoService versionCounterDaoService;
    protected ContentService contentService;
	protected MetaModelDAO metaModelDAO;
	
    /*
     * Data used by tests
     */
    protected StoreRef testStoreRef;
    protected NodeRef rootNodeRef;
    protected Map<String, Serializable> versionProperties;
    protected HashMap<QName, Serializable> nodeProperties;
    
    /**
     * The most recent set of versionable nodes created by createVersionableNode
     */
    protected HashMap<String, NodeRef> versionableNodes;
    
    /*
     * Proprety names and values
     */
	protected static final QName TEST_TYPE_QNAME = QName.createQName(NamespaceService.ALFRESCO_URI, "testType");
	protected static final QName PROP_1 = QName.createQName(NamespaceService.ALFRESCO_URI, "prop1");
	protected static final QName PROP_2 = QName.createQName(NamespaceService.ALFRESCO_URI, "prop2");
	protected static final QName PROP_3 = QName.createQName(NamespaceService.ALFRESCO_URI, "prop3");
	protected static final String VERSION_PROP_1 = "versionProp1";
	protected static final String VERSION_PROP_2 = "versionProp2";
	protected static final String VERSION_PROP_3 = "versionProp3";
	protected static final String VALUE_1 = "value1";
	protected static final String VALUE_2 = "value2";
	protected static final String VALUE_3 = "value3";
	protected static final QName TEST_CHILD_ASSOC_1 = QName.createQName(NamespaceService.ALFRESCO_URI, "childAssoc1");
	protected static final QName TEST_CHILD_ASSOC_2 = QName.createQName(NamespaceService.ALFRESCO_URI, "childAssoc2");
	protected static final QName TEST_ASSOC = QName.createQName(NamespaceService.ALFRESCO_URI, "assoc");	
    
    /**
     * Test content
     */
    protected static final String TEST_CONTENT = "This is the versioned test content.";	
    
	/**
	 * Sets the meta model dao
	 * 
	 * @param metaModelDao  the meta model dao
	 */
	public void setMetaModelDAO(MetaModelDAO metaModelDAO) 
	{
		this.metaModelDAO = metaModelDAO;
	}
	
    /**
     * Called during the transaction setup
     */
    protected void onSetUpInTransaction() throws Exception
    {
        // Get the services by name from the application context
        this.dbNodeService = (NodeService)applicationContext.getBean("dbNodeService");
        this.lightWeightVersionStoreVersionService = (VersionService)applicationContext.getBean("lightWeightVersionStoreVersionService");
        this.versionCounterDaoService = (VersionCounterDaoService)applicationContext.getBean("versionCounterDaoService");
        this.contentService = (ContentService)applicationContext.getBean("contentService");
        
		// Create the test model
		createTestModel();
		
        // Create a bag of properties for later use
        this.versionProperties = new HashMap<String, Serializable>();
        versionProperties.put(VERSION_PROP_1, VALUE_1);
        versionProperties.put(VERSION_PROP_2, VALUE_2);
        versionProperties.put(VERSION_PROP_3, VALUE_3);
        
        // Create the node properties
        this.nodeProperties = new HashMap<QName, Serializable>();
        this.nodeProperties.put(PROP_1, VALUE_1);
        this.nodeProperties.put(PROP_2, VALUE_2);
        this.nodeProperties.put(PROP_3, VALUE_3);
        
        // Create a workspace that contains the 'live' nodes
        this.testStoreRef = this.dbNodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_" + System.currentTimeMillis());
        
        // Get a reference to the root node
        this.rootNodeRef = this.dbNodeService.getRootNode(this.testStoreRef);
    }
	
	/**
	 * Creates the test model used by the tests
	 */
	private void createTestModel()
	{
		M2Type testType = this.metaModelDAO.createType(TEST_TYPE_QNAME);
		testType.setSuperClass(this.metaModelDAO.getClass(DictionaryBootstrap.TYPE_QNAME_CONTAINER));
		
		M2Property prop1 = testType.createProperty(PROP_1.getLocalName());
		prop1.setMandatory(true);
		prop1.setMultiValued(false);
		
		M2Property prop2 = testType.createProperty(PROP_2.getLocalName());
		prop2.setMandatory(false);
		prop2.setMandatory(false);

		M2Property prop3 = testType.createProperty(PROP_3.getLocalName());
		prop3.setMandatory(false);
		prop3.setMandatory(false);
		
		M2ChildAssociation childAssoc = testType.createChildAssociation(TEST_CHILD_ASSOC_1.getLocalName());
		childAssoc.setMandatory(false);
		
		M2ChildAssociation childAssoc2 = testType.createChildAssociation(TEST_CHILD_ASSOC_2.getLocalName());
		childAssoc2.setMandatory(false);
		
		M2Association assoc = testType.createAssociation(TEST_ASSOC.getLocalName());
		assoc.setMandatory(false);
//		
//		M2Aspect testAspect = this.metaModelDAO.createAspect(TEST_ASPECT_QNAME);
//		
//		M2Property prop3 = testAspect.createProperty(PROP3_MANDATORY);
//		prop3.setMandatory(true);
//		prop3.setMultiValued(false);
//		
//		M2Property prop4 = testAspect.createProperty(PROP4_OPTIONAL);
//		prop4.setMandatory(false);
//		prop4.setMultiValued(false);					
	}
    
    /**
     * Creates a new versionable node
     * 
     * @return  the node reference
     */
    protected NodeRef createNewVersionableNode()
    {
        // Use this map to retrive the versionable nodes in later tests
        this.versionableNodes = new HashMap<String, NodeRef>();
        
        // Get the version aspect class reference
        ClassRef aspectRef = new ClassRef(DictionaryBootstrap.ASPECT_QNAME_VERSION);
        
        // Create node (this node has some content)
        NodeRef nodeRef = this.dbNodeService.createNode(
                rootNodeRef, 
                null, 
                QName.createQName("{test}MyVersionableNode"),
                TEST_TYPE_QNAME,
                this.nodeProperties).getChildRef();
        this.dbNodeService.addAspect(nodeRef, aspectRef, new HashMap<QName, Serializable>());
        
        Map<QName, Serializable> properties = this.dbNodeService.getProperties(nodeRef);
        properties.put(DictionaryBootstrap.PROP_QNAME_MIME_TYPE, "text/plain");
        properties.put(DictionaryBootstrap.PROP_QNAME_ENCODING, "UTF-8");
        this.dbNodeService.addAspect(nodeRef, DictionaryBootstrap.ASPECT_CONTENT, properties);
        
        assertNotNull(nodeRef);
        this.versionableNodes.put(nodeRef.getId(), nodeRef);
        
        // Add the content to the node
        ContentWriter contentWriter = this.contentService.getUpdatingWriter(nodeRef);
        contentWriter.putContent(TEST_CONTENT);
        
        // Add some children to the node
        NodeRef child1 = this.dbNodeService.createNode(
                nodeRef,
                null,
                //QName.createQName("{test}ChildNode1"),
                TEST_CHILD_ASSOC_1,
				TEST_TYPE_QNAME,
                this.nodeProperties).getChildRef();
        this.dbNodeService.addAspect(child1, aspectRef, new HashMap<QName, Serializable>());
        assertNotNull(child1);
        this.versionableNodes.put(child1.getId(), child1);
        NodeRef child2 = this.dbNodeService.createNode(
                nodeRef,
                null,
                //QName.createQName("{test}ChildNode2"),
                TEST_CHILD_ASSOC_2,
				TEST_TYPE_QNAME,
                this.nodeProperties).getChildRef();
        this.dbNodeService.addAspect(child2, aspectRef, new HashMap<QName, Serializable>());
        assertNotNull(child2);
        this.versionableNodes.put(child2.getId(), child2);
        
        // Create a node that can be associated with the root node
        NodeRef assocNode = this.dbNodeService.createNode(
                rootNodeRef,
                null,
                QName.createQName("{test}MyAssocNode"),
				TEST_TYPE_QNAME,
                this.nodeProperties).getChildRef();
        assertNotNull(assocNode);
        this.dbNodeService.createAssociation(nodeRef, assocNode, TEST_ASSOC);
        
        return nodeRef;
    }
    
    /**
     * Creates a new version, checking the properties of the version.
     * <p>
     * The default test propreties are assigned to the version.
     * 
     * @param versionableNode    the versionable node
     * @return                   the created (and checked) new version
     */
    protected Version createVersion(NodeRef versionableNode)
    {
        return createVersion(versionableNode, this.versionProperties);
    }
    
    /**
     * Creates a new version, checking the properties of the version.
     * 
     * @param versionableNode    the versionable node
     * @param versionProperties  the version properties
     * @return                   the created (and checked) new version
     */
    protected Version createVersion(NodeRef versionableNode, Map<String, Serializable> versionProperties)
    {
        // Get the next version number
        int nextVersion = peekNextVersionNumber(); 
        String nextVersionLabel = peekNextVersionLabel(versionableNode, nextVersion, versionProperties);
		
        // Snap-shot the date-time
        long beforeVersionTime = System.currentTimeMillis();
        
        // Now lets create a new version for this node
        Version newVersion = lightWeightVersionStoreVersionService.createVersion(versionableNode, this.versionProperties);
        checkNewVersion(beforeVersionTime, nextVersion, nextVersionLabel, newVersion, versionableNode);
        
        // Return the new version
        return newVersion;
    }
	
	/**
	 * Gets the next version label
	 */
	protected String peekNextVersionLabel(NodeRef nodeRef, int versionNumber, Map<String, Serializable> versionProperties)
	{
		Version version = this.lightWeightVersionStoreVersionService.getCurrentVersion(nodeRef);		
		SerialVersionLabelPolicy policy = new SerialVersionLabelPolicy();
		return policy.calculateVersionLabel(DictionaryBootstrap.TYPE_BASE, version, versionNumber, versionProperties);
	}
    
    /**
     * Checkd the validity of a new version
     * 
     * @param beforeVersionTime     the time snap shot before the version was created
     * @param expectedVersionNumber the expected version number
     * @param newVersion            the new version
     * @param versionableNode       the versioned node
     */
    protected void checkNewVersion(long beforeVersionTime, int expectedVersionNumber, String expectedVersionLabel, Version newVersion, NodeRef versionableNode)
    {
        assertNotNull(newVersion);
        
        // Check the version label and version number
        assertEquals(
                "The expected version number was not used.",
                Integer.toString(expectedVersionNumber), 
                newVersion.getVersionProperty(Version.PROP_VERSION_NUMBER));
		assertEquals(
				"The expected version label was not used.",
				expectedVersionLabel,
				newVersion.getVersionLabel());
        
        // Check the created date
        long afterVersionTime = System.currentTimeMillis();
        long createdDate = newVersion.getCreatedDate().getTime();
        if (createdDate < beforeVersionTime || createdDate > afterVersionTime)
        {
            fail("The created date of the version is incorrect.");
        }
        
        // Check the properties of the verison
        Map<String, Serializable> props = newVersion.getVersionProperties();
        assertNotNull("The version properties collection should not be null.", props);
        // TODO sort this out - need to check for the reserved properties too
        //assertEquals(versionProperties.size(), props.size());
        for (String key : versionProperties.keySet())
        {
            assertEquals(
                    versionProperties.get(key), 
                    newVersion.getVersionProperty(key));
        }
        
        // Check that the node reference is correct
        NodeRef nodeRef = newVersion.getNodeRef();
        assertNotNull(nodeRef);
        assertEquals(this.lightWeightVersionStoreVersionService.getVersionStoreReference(), nodeRef.getStoreRef());
        assertNotNull(nodeRef.getId());        
        
        // TODO: How do we check the frozen attributes ??
        
        // Check the node ref for the current version
        String currentVersionLabel = (String)this.dbNodeService.getProperty(
                versionableNode,
                DictionaryBootstrap.PROP_QNAME_CURRENT_VERSION_LABEL);
        assertEquals(newVersion.getVersionLabel(), currentVersionLabel);
    }
    
    /**
     * Returns the next version number without affecting the version counter.
     * 
     * @return  the next version number to be allocated
     */
    protected int peekNextVersionNumber()
    {
        StoreRef lwVersionStoreRef = this.lightWeightVersionStoreVersionService.getVersionStoreReference();
        return this.versionCounterDaoService.currentVersionNumber(lwVersionStoreRef) + 1; 
    }

}
