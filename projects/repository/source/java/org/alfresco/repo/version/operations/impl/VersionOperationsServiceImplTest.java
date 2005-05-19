/**
 * Created on May 16, 2005
 */
package org.alfresco.repo.version.operations.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.dictionary.bootstrap.DictionaryBootstrap;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.ref.StoreRef;
import org.alfresco.repo.version.Version;
import org.alfresco.repo.version.operations.VersionOperationsService;
import org.alfresco.util.BaseSpringTest;
import org.alfresco.util.debug.NodeStoreInspector;

/**
 * Version operations service implementation unit tests
 * 
 * @author Roy Wetherall
 */
public class VersionOperationsServiceImplTest extends BaseSpringTest 
{
	/**
	 * Services used by the tests
	 */
	private NodeService nodeService;
	private VersionOperationsService versionOperationsService;
	
	/**
	 * Data used by the tests
	 */
	private StoreRef storeRef;
	private NodeRef rootNodeRef;	
	private NodeRef nodeRef;
	
	/**
	 * Types and properties used by the tests
	 */
	private static final String TEST_VALUE_1 = "testValue1";
	private static final String TEST_VALUE_2 = "testValue2";
	private static final QName PROP1_QNAME = QName.createQName("{test}prop1");
	private static final QName PROP2_QNAME = QName.createQName("{test}prop2");
	
	/**
	 * On setup in transaction implementation
	 */
	@Override
	protected void onSetUpInTransaction() 
		throws Exception 
	{
		// Set the services
		this.nodeService = (NodeService)this.applicationContext.getBean("dbNodeService");
		this.versionOperationsService = (VersionOperationsService)this.applicationContext.getBean("versionOperationsService");
		
		// Create the store and get the root node reference
		this.storeRef = this.nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_" + System.currentTimeMillis());
		this.rootNodeRef = this.nodeService.getRootNode(storeRef);
		
		// Create the node used for tests
		ChildAssocRef childAssocRef = this.nodeService.createNode(
				rootNodeRef,
				null,
				QName.createQName("{test}test"),
				DictionaryBootstrap.TYPE_QNAME_CONTAINER,
				createTypePropertyBag());
		this.nodeRef = childAssocRef.getChildRef();
		
		// Add the lock and version aspects to the created node
		this.nodeService.addAspect(this.nodeRef, DictionaryBootstrap.ASPECT_CLASS_REF_VERSION, null);
		this.nodeService.addAspect(this.nodeRef, DictionaryBootstrap.ASPECT_CLASS_REF_LOCK, null);		
	}
	
	/**
	 * Helper method that creates a bag of properties for the test type
	 * 
	 * @return  bag of properties
	 */
	private Map<QName, Serializable> createTypePropertyBag()
	{
		Map<QName, Serializable> result = new HashMap<QName, Serializable>();
		result.put(PROP1_QNAME, TEST_VALUE_1);
		result.put(PROP2_QNAME, TEST_VALUE_2);
		return result;
	}
	
	public void testCheckOut()
	{
		checkout();
	}
	
	private NodeRef checkout()
	{
		System.out.println(
				NodeStoreInspector.dumpNodeStore(this.nodeService, this.storeRef));
		
		// Check out the node
		NodeRef workingCopy = this.versionOperationsService.checkout(this.nodeRef, this.rootNodeRef, null, QName.createQName("{test}workingCopy"));
		assertNotNull(workingCopy);
		
		// Ensure that the working copy aspect has been applied
		assertTrue(this.nodeService.hasAspect(workingCopy, DictionaryBootstrap.ASPECT_WORKING_COPY));
			
		
		// Dump the store so we can have a look at what is going on
		System.out.println(
				NodeStoreInspector.dumpNodeStore(this.nodeService, this.storeRef));
		
		return workingCopy;
	}
	
	public void testCheckIn()
	{
		NodeRef workingCopy = checkout();
		
		Map<String, Serializable> versionProperties = new HashMap<String, Serializable>();
		versionProperties.put(Version.PROP_DESCRIPTION, "This is a test version");
		
		this.versionOperationsService.checkin(workingCopy, versionProperties);
		
		System.out.println(
				NodeStoreInspector.dumpNodeStore(this.nodeService, this.storeRef));
		
	}
	
	public void testCancelCheckOut()
	{
	}

}
