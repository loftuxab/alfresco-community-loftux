/**
 * Created on May 16, 2005
 */
package org.alfresco.repo.version.operations.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.version.lightweight.VersionStoreConst;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionType;
import org.alfresco.service.namespace.QName;
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
	private CheckOutCheckInService versionOperationsService;
	private ContentService contentService;
	
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
	private static final String CONTENT_1 = "This is some content";
	private static final String CONTENT_2 = "This is the cotent modified.";
	
	/**
	 * On setup in transaction implementation
	 */
	@Override
	protected void onSetUpInTransaction() 
		throws Exception 
	{
		// Set the services
		this.nodeService = (NodeService)this.applicationContext.getBean("dbNodeService");
		this.versionOperationsService = (CheckOutCheckInService)this.applicationContext.getBean("versionOperationsService");
		this.contentService = (ContentService)this.applicationContext.getBean("contentService");
		
		// Create the store and get the root node reference
		this.storeRef = this.nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_" + System.currentTimeMillis());
		this.rootNodeRef = this.nodeService.getRootNode(storeRef);
		
		// Create the node used for tests
        Map<QName, Serializable> bagOfProps = createTypePropertyBag();
        bagOfProps.put(ContentModel.PROP_MIME_TYPE, "text/plain");
        bagOfProps.put(ContentModel.PROP_ENCODING, "UTF-8");
        
		ChildAssociationRef childAssocRef = this.nodeService.createNode(
				rootNodeRef,
				ContentModel.ASSOC_CONTAINS,
				QName.createQName("{test}test"),
				ContentModel.TYPE_CONTENT,
				bagOfProps);
		this.nodeRef = childAssocRef.getChildRef();
		
		// Add the initial content to the node
		//Map<QName, Serializable>contentProperties = new HashMap<QName, Serializable>();
		//this.nodeService.addAspect(this.nodeRef, DictionaryBootstrap.ASPECT_QNAME_CONTENT, contentProperties);
		ContentWriter contentWriter = this.contentService.getUpdatingWriter(this.nodeRef);
		contentWriter.putContent(CONTENT_1);	
		
		// Add the lock and version aspects to the created node
		this.nodeService.addAspect(this.nodeRef, ContentModel.ASPECT_VERSIONABLE, null);
		this.nodeService.addAspect(this.nodeRef, ContentModel.ASPECT_LOCKABLE, null);		
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
	
	/**
	 * Test checkout 
	 */
	public void testCheckOut()
	{
		checkout();
	}
	
	/**
	 * 
	 * @return
	 */
	private NodeRef checkout()
	{
		System.out.println(
				NodeStoreInspector.dumpNodeStore(this.nodeService, this.storeRef));
		
		// Check out the node
		NodeRef workingCopy = this.versionOperationsService.checkout(
				this.nodeRef, 
				this.rootNodeRef, 
				ContentModel.ASSOC_CONTAINS, 
				QName.createQName("{test}workingCopy"));
		assertNotNull(workingCopy);
		
		// Ensure that the working copy and copy aspect has been applied
		assertTrue(this.nodeService.hasAspect(workingCopy, ContentModel.ASPECT_WORKING_COPY));	
		assertTrue(this.nodeService.hasAspect(workingCopy, ContentModel.ASPECT_COPIEDFROM));
		
		// Ensure that the content has been copied correctly
		ContentReader contentReader = this.contentService.getReader(this.nodeRef);
		assertNotNull(contentReader);
		ContentReader contentReader2 = this.contentService.getReader(workingCopy);
		assertNotNull(contentReader2);
		assertEquals(
				"The content string of the working copy should match the origioanl immediatly after checkout.", 
				contentReader.getContentString(), 
				contentReader2.getContentString());
		
		// Dump the store so we can have a look at what is going on
		System.out.println(
				NodeStoreInspector.dumpNodeStore(this.nodeService, this.storeRef));
		
		return workingCopy;
	}
	
	/**
	 * Test checkIn
	 */
	public void testCheckIn()
	{
		NodeRef workingCopy = checkout();
		
		// Test standard check-in
		Map<String, Serializable> versionProperties = new HashMap<String, Serializable>();
		versionProperties.put(Version.PROP_DESCRIPTION, "This is a test version");		
		this.versionOperationsService.checkin(workingCopy, versionProperties);		
		System.out.println(
				NodeStoreInspector.dumpNodeStore(this.nodeService, this.storeRef));
		
		// Test check-in with content
        NodeRef workingCopy3 = checkout();
        ContentWriter tempWriter = this.contentService.getWriter(workingCopy3);
		assertNotNull(tempWriter);
		tempWriter.putContent(CONTENT_2);
		String contentUrl = tempWriter.getContentUrl();
		Map<String, Serializable> versionProperties3 = new HashMap<String, Serializable>();
		versionProperties3.put(VersionStoreConst.PROP_VERSION_TYPE, VersionType.MAJOR);
		NodeRef origNodeRef = this.versionOperationsService.checkin(workingCopy3, versionProperties3, contentUrl, true);
		assertNotNull(origNodeRef);
		ContentReader contentReader = this.contentService.getReader(origNodeRef);
		assertNotNull(contentReader);
		assertEquals(CONTENT_2, contentReader.getContentString());
		System.out.println(
				NodeStoreInspector.dumpNodeStore(this.nodeService, this.storeRef));
		this.versionOperationsService.cancelCheckout(workingCopy3);
		
		// Test keep checked out flag
		NodeRef workingCopy2 = checkout();		
		Map<String, Serializable> versionProperties2 = new HashMap<String, Serializable>();
		versionProperties2.put(Version.PROP_DESCRIPTION, "Another version test");		
		this.versionOperationsService.checkin(workingCopy2, versionProperties2, null, true);
		this.versionOperationsService.checkin(workingCopy2, new HashMap<String, Serializable>(), null, true);
		System.out.println(
				NodeStoreInspector.dumpNodeStore(this.nodeService, this.storeRef));		
	}
	
	/**
	 * Test cancel checkOut
	 */
	public void testCancelCheckOut()
	{
		NodeRef workingCopy = checkout();
		assertNotNull(workingCopy);
		
		NodeRef origNodeRef = this.versionOperationsService.cancelCheckout(workingCopy);
		assertEquals(this.nodeRef, origNodeRef);
		
		System.out.println(
				NodeStoreInspector.dumpNodeStore(this.nodeService, this.storeRef));
	}

}
