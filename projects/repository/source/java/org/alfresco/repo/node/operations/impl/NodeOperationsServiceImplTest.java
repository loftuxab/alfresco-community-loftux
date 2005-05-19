/**
 * Created on May 10, 2005
 */
package org.alfresco.repo.node.operations.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.dictionary.ClassRef;
import org.alfresco.repo.dictionary.bootstrap.DictionaryBootstrap;
import org.alfresco.repo.dictionary.metamodel.M2Aspect;
import org.alfresco.repo.dictionary.metamodel.M2Association;
import org.alfresco.repo.dictionary.metamodel.M2ChildAssociation;
import org.alfresco.repo.dictionary.metamodel.M2Property;
import org.alfresco.repo.dictionary.metamodel.M2Type;
import org.alfresco.repo.dictionary.metamodel.MetaModelDAO;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.node.operations.NodeOperationsService;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.ref.StoreRef;
import org.alfresco.util.BaseSpringTest;
import org.alfresco.util.debug.NodeStoreInspector;

/**
 * Node operations service unit tests
 * 
 * @author Roy Wetherall
 */
public class NodeOperationsServiceImplTest extends BaseSpringTest 
{
	/**
	 * Services used by the tests
	 */
	private NodeService nodeService;
	private NodeOperationsService nodeOperationsService;
	private MetaModelDAO metaModelDAO;
	
	/**
	 * Data used by the tests
	 */
	private StoreRef storeRef;
	private NodeRef sourceNodeRef;	
	private NodeRef rootNodeRef;	
	private NodeRef targetNodeRef;
	private NodeRef nonPrimaryChildNodeRef;
	private NodeRef childNodeRef;
	private NodeRef destinationNodeRef;
	
	/**
	 * Types and properties used by the tests
	 */
	private static final String TEST_TYPE_NAMESPACE = "testTypeNamespaceURI";
	private static final QName TEST_TYPE_QNAME = QName.createQName(TEST_TYPE_NAMESPACE, "testType");
	private static final String PROP1_MANDATORY = "prop1Mandatory";
	private static final QName PROP1_QNAME_MANDATORY = QName.createQName(TEST_TYPE_NAMESPACE, PROP1_MANDATORY);
	private static final String PROP2_OPTIONAL = "prop2Optional";
	private static final QName PROP2_QNAME_OPTIONAL = QName.createQName(TEST_TYPE_NAMESPACE, PROP2_OPTIONAL);
	
	private static final QName TEST_ASPECT_QNAME = QName.createQName(TEST_TYPE_NAMESPACE, "testAspect");
	private static final String PROP3_MANDATORY = "prop3Mandatory";
	private static final QName PROP3_QNAME_MANDATORY = QName.createQName(TEST_TYPE_NAMESPACE, PROP3_MANDATORY);
	private static final String PROP4_OPTIONAL = "prop4Optional";
	private static final QName PROP4_QNAME_OPTIONAL = QName.createQName(TEST_TYPE_NAMESPACE, PROP4_OPTIONAL);
	
	private static final String TEST_VALUE_1 = "testValue1";
	private static final String TEST_VALUE_2 = "testValue2";
	
	private static final String TEST_CHILD_ASSOC_NAME = "testChildAssocName";
	private static final QName TEST_CHILD_ASSOC_QNAME = QName.createQName(TEST_TYPE_NAMESPACE, TEST_CHILD_ASSOC_NAME);
	private static final String TEST_ASSOC_NAME = "testAssocName";
	private static final QName TEST_ASSOC_QNAME = QName.createQName(TEST_TYPE_NAMESPACE, TEST_ASSOC_NAME);
	private static final String TEST_CHILD_ASSOC_NAME2 = "testChildAssocName2";
	private static final QName TEST_CHILD_ASSOC_QNAME2 = QName.createQName(TEST_TYPE_NAMESPACE, TEST_CHILD_ASSOC_NAME2);		
	
	/**
	 * Sets the meta model DAO
	 * 
	 * @param metaModelDAO  the meta model DAO
	 */
	public void setMetaModelDAO(MetaModelDAO metaModelDAO)
    {
        this.metaModelDAO = metaModelDAO;
    }
	
	/**
	 * On setup in transaction implementation
	 */
	@Override
	protected void onSetUpInTransaction() 
		throws Exception 
	{
		// Set the services
		this.nodeService = (NodeService)this.applicationContext.getBean("dbNodeService");
		this.nodeOperationsService = (NodeOperationsService)this.applicationContext.getBean("nodeOperationsService");
		
		// Create the test model
		createTestModel();
		
		// Create the store and get the root node reference
		this.storeRef = this.nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_" + System.currentTimeMillis());
		this.rootNodeRef = this.nodeService.getRootNode(storeRef);
		
		// Create the node used for copying
		ChildAssocRef childAssocRef = this.nodeService.createNode(
				rootNodeRef,
				null,
				QName.createQName("{test}test"),
				TEST_TYPE_QNAME,
				createTypePropertyBag());
		this.sourceNodeRef = childAssocRef.getChildRef();
		
		// Create another bag of properties
		Map<QName, Serializable> aspectProperties = new HashMap<QName, Serializable>();
		aspectProperties.put(PROP3_QNAME_MANDATORY, TEST_VALUE_1);
		aspectProperties.put(PROP4_QNAME_OPTIONAL, TEST_VALUE_2);
		
		// Apply the test aspect
		this.nodeService.addAspect(
				this.sourceNodeRef, 
				new ClassRef(TEST_ASPECT_QNAME), 
				aspectProperties);
		
		// Add a child
		ChildAssocRef temp3 =this.nodeService.createNode(
				this.sourceNodeRef, 
				null, 
				TEST_CHILD_ASSOC_QNAME, 
				TEST_TYPE_QNAME, 
				createTypePropertyBag());
		this.childNodeRef = temp3.getChildRef();
		
		// Add a child that is primary
		ChildAssocRef temp2 = this.nodeService.createNode(
				rootNodeRef,
				null,
				QName.createQName("{test}testNonPrimaryChild"),
				TEST_TYPE_QNAME,
				createTypePropertyBag());
		this.nonPrimaryChildNodeRef = temp2.getChildRef();
		this.nodeService.addChild(this.sourceNodeRef, this.nonPrimaryChildNodeRef, TEST_CHILD_ASSOC_QNAME2);
		
		// Add a target assoc
		ChildAssocRef temp = this.nodeService.createNode(
				rootNodeRef,
				null,
				QName.createQName("{test}testAssoc"),
				TEST_TYPE_QNAME,
				createTypePropertyBag());
		this.targetNodeRef = temp.getChildRef();
		this.nodeService.createAssociation(this.sourceNodeRef, this.targetNodeRef, TEST_ASSOC_QNAME);
		
		// Create a node we can use as the destination in a copy
		Map<QName, Serializable> destinationProps = new HashMap<QName, Serializable>();
		destinationProps.put(PROP1_QNAME_MANDATORY, TEST_VALUE_1);			
		ChildAssocRef temp5 = this.nodeService.createNode(
				this.rootNodeRef,
				null,
				QName.createQName("{test}testDestinationNode"),
				TEST_TYPE_QNAME,
				destinationProps);
		this.destinationNodeRef = temp5.getChildRef();
	}
	
	/**
	 * Helper method that creates a bag of properties for the test type
	 * 
	 * @return  bag of properties
	 */
	private Map<QName, Serializable> createTypePropertyBag()
	{
		Map<QName, Serializable> result = new HashMap<QName, Serializable>();
		result.put(PROP1_QNAME_MANDATORY, TEST_VALUE_1);
		result.put(PROP2_QNAME_OPTIONAL, TEST_VALUE_2);
		return result;
	}
	
	/**
	 * Creates the test model used by the tests
	 */
	private void createTestModel()
	{
		M2Type testType = this.metaModelDAO.createType(TEST_TYPE_QNAME);
		testType.setSuperClass(this.metaModelDAO.getClass(DictionaryBootstrap.TYPE_QNAME_CONTAINER));
		
		M2Property prop1 = testType.createProperty(PROP1_MANDATORY);
		prop1.setMandatory(true);
		prop1.setMultiValued(false);
		
		M2Property prop2 = testType.createProperty(PROP2_OPTIONAL);
		prop2.setMandatory(false);
		prop2.setMandatory(false);
		
		M2ChildAssociation childAssoc = testType.createChildAssociation(TEST_CHILD_ASSOC_NAME);
		childAssoc.setMandatory(false);
		
		M2ChildAssociation childAssoc2 = testType.createChildAssociation(TEST_CHILD_ASSOC_NAME2);
		childAssoc2.setMandatory(false);
		
		M2Association assoc = testType.createAssociation(TEST_ASSOC_NAME);
		assoc.setMandatory(false);
		
		M2Aspect testAspect = this.metaModelDAO.createAspect(TEST_ASPECT_QNAME);
		
		M2Property prop3 = testAspect.createProperty(PROP3_MANDATORY);
		prop3.setMandatory(true);
		prop3.setMultiValued(false);
		
		M2Property prop4 = testAspect.createProperty(PROP4_OPTIONAL);
		prop4.setMandatory(false);
		prop4.setMultiValued(false);					
	}
	
	/**
	 * Test copy new node within store	 
	 */
	public void testCopyToNewNode()
	{
		// Copy to new node without copying children
		NodeRef copy = this.nodeOperationsService.copy(
				this.sourceNodeRef,
				this.rootNodeRef,
				null,
				QName.createQName("{test}copyAssoc"));		
		checkCopiedNode(this.sourceNodeRef, copy, true, true, false);
		
		// Copy to new node, copying children
		NodeRef copy2 = this.nodeOperationsService.copy(
				this.sourceNodeRef,
				this.rootNodeRef,
				null,
				QName.createQName("{test}copyAssoc"),
				true);		
		checkCopiedNode(this.sourceNodeRef, copy2, true, true, true);
		
		// Check that a copy of a copy works correctly
		NodeRef copyOfCopy = this.nodeOperationsService.copy(
				copy,
				this.rootNodeRef,
				null,
				QName.createQName("{test}copyOfCopy"));
		checkCopiedNode(copy, copyOfCopy, true, true, false);
		
        // TODO check copying from a versioned copy
		// TODO check copying from a lockable copy
		// TODO check copying from a node with content
		
		// TODO check copying to a different store
		
		System.out.println(
				NodeStoreInspector.dumpNodeStore(this.nodeService, this.storeRef));
	}	
	
	public void testCopyToExistingNode()
	{
		// Copy nodes within the same store
		this.nodeOperationsService.copy(this.sourceNodeRef, this.destinationNodeRef);
		checkCopiedNode(this.sourceNodeRef, this.destinationNodeRef, false, true, false);
		
		// TODO check copying from a copy
		// TODO check copying from a versioned copy
		// TODO check copying from a lockable copy
		// TODO check copying from a node with content
		
		// TODO check copying nodes between stores
		
		System.out.println(
				NodeStoreInspector.dumpNodeStore(this.nodeService, this.storeRef));
	}
	
	/**
	 * Check that the copied node contains the state we are expecting
	 * 
	 * @param sourceNodeRef       the source node reference
	 * @param destinationNodeRef  the destination node reference
	 */
	private void checkCopiedNode(NodeRef sourceNodeRef, NodeRef destinationNodeRef, boolean newCopy, boolean sameStore, boolean copyChildren)
	{
		if (newCopy == true)
		{
			if (sameStore == true)
			{
				// Check that the copy aspect has been applied to the copy
				boolean hasCopyAspect = this.nodeService.hasAspect(destinationNodeRef, DictionaryBootstrap.ASPECT_COPY);
				assertTrue(hasCopyAspect);
				NodeRef copyNodeRef = (NodeRef)this.nodeService.getProperty(destinationNodeRef, DictionaryBootstrap.PROP_QNAME_COPY_REFERENCE);
				assertNotNull(copyNodeRef);
				assertEquals(sourceNodeRef, copyNodeRef);
			}
			else
			{
				// Check that destiantion has the same id as the source
				assertEquals(sourceNodeRef.getId(), destinationNodeRef.getId());
		}
		}
		
		// Check that all the appropriate aspects have been applied to the desitation node
		Set<ClassRef> destinationAspects = this.nodeService.getAspects(destinationNodeRef);
		if (sameStore == true && newCopy == true)
		{
			assertEquals(2, destinationAspects.size());
		}
		else
		{
			assertEquals(1, destinationAspects.size());
		}
		boolean hasTestAspect = this.nodeService.hasAspect(destinationNodeRef, new ClassRef(TEST_ASPECT_QNAME));
		assertTrue(hasTestAspect);
		
		// Check that all the correct properties have been copied
		Map<QName, Serializable> destinationProperties = this.nodeService.getProperties(destinationNodeRef);
		assertNotNull(destinationProperties);
		if (sameStore == true && newCopy == true)
		{
			assertEquals(5, destinationProperties.size());
		}
		else
		{
			assertEquals(4, destinationProperties.size());
		}
		String value1 = (String)destinationProperties.get(PROP1_QNAME_MANDATORY);
		assertNotNull(value1);
		assertEquals(TEST_VALUE_1, value1);
		String value2 = (String)destinationProperties.get(PROP2_QNAME_OPTIONAL);
		assertNotNull(value2);
		assertEquals(TEST_VALUE_2, value2);
		String value3 = (String)destinationProperties.get(PROP3_QNAME_MANDATORY);
		assertNotNull(value3);
		assertEquals(TEST_VALUE_1, value3);
		String value4 = (String)destinationProperties.get(PROP4_QNAME_OPTIONAL);
		assertNotNull(value4);
		assertEquals(TEST_VALUE_2, value4);
		
		// Check all the target associations have been copied
		List<NodeAssocRef> destinationTargets = this.nodeService.getTargetAssocs(destinationNodeRef, TEST_ASSOC_QNAME);
		assertNotNull(destinationTargets);
		assertEquals(1, destinationTargets.size());
		NodeAssocRef nodeAssocRef = destinationTargets.get(0);
		assertNotNull(nodeAssocRef);
		assertEquals(this.targetNodeRef, nodeAssocRef.getTargetRef());
		
		// Check all the child associations have been copied
		List<ChildAssocRef> childAssocRefs = this.nodeService.getChildAssocs(destinationNodeRef);
		assertNotNull(childAssocRefs);
		assertEquals(2, childAssocRefs.size());
		for (ChildAssocRef ref : childAssocRefs) 
		{
			if (ref.getQName().equals(TEST_CHILD_ASSOC_NAME2) == true)
			{
				// Since this child is non-primary in the source it will always be non-primary in the destination
				assertFalse(ref.isPrimary());
				assertEquals(this.nonPrimaryChildNodeRef, ref.getChildRef());
			}
			else
			{
				if (copyChildren == false)
				{
					assertFalse(ref.isPrimary());
					//assertEquals(this.childNodeRef, ref.getChildRef());
				}
				else
				{
					//assertTrue(ref.isPrimary());
					assertTrue(this.childNodeRef.equals(ref.getChildRef()) == false);
					
					// TODO need to check that the copied child has all the correct details ..
				}
			}	
		}
	}
}
