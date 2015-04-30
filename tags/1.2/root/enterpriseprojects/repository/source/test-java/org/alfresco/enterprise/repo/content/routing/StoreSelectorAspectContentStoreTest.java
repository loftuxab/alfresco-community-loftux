/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.content.routing;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.alfresco.enterprise.repo.content.routing.StoreSelectorAspectContentStore.StoreSelectorConstraint;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.coci.CheckOutCheckInServiceImpl;
import org.alfresco.repo.content.ContentServiceImpl;
import org.alfresco.repo.content.ContentStore;
import org.alfresco.repo.content.filestore.FileContentStore;
import org.alfresco.repo.dictionary.DictionaryComponent;
import org.alfresco.repo.dictionary.DictionaryDAO;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.node.integrity.IntegrityException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ApplicationContextHelper;
import org.alfresco.util.TempFileProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * Tests {@link StoreSelectorAspectContentStore}
 * 
 * @author Derek Hulley
 * @since 3.2
 */
public class StoreSelectorAspectContentStoreTest extends TestCase
{
    private static final String STORE_ONE = "Store1";
    private static final String STORE_TWO = "Store2";
    private static final String STORE_THREE = "Store3";

    private static ApplicationContext ctx = ApplicationContextHelper.getApplicationContext();

    private TransactionService transactionService;
    private NodeService nodeService;
    private FileFolderService fileFolderService;

    private Map<String, ContentStore> storesByName;
    private FileContentStore fileStore1;
    private FileContentStore fileStore2;
    private FileContentStore fileStore3;
    private StoreSelectorAspectContentStore store;
    private NodeRef contentNodeRef;
    private ContentService contentService;
    private CheckOutCheckInService checkOutCheckInService;
    private VersionService versionService;


    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        ServiceRegistry serviceRegistry = (ServiceRegistry) ctx.getBean(ServiceRegistry.SERVICE_REGISTRY);
        transactionService = serviceRegistry.getTransactionService();
        nodeService = serviceRegistry.getNodeService();
        fileFolderService = serviceRegistry.getFileFolderService();
        versionService = serviceRegistry.getVersionService();
        
        contentService = serviceRegistry.getContentService();
        checkOutCheckInService = serviceRegistry.getCheckOutCheckInService();

        AuthenticationUtil.pushAuthentication();
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        
        fileStore1 = new FileContentStore(
                ctx,
                TempFileProvider.getSystemTempDir() + "/fileStore1");
        fileStore2 = new FileContentStore(
                ctx,
                TempFileProvider.getSystemTempDir() + "/fileStore2");
        fileStore3 = new FileContentStore(
                ctx,
                TempFileProvider.getSystemTempDir() + "/fileStore3");
        
        storesByName = new HashMap<String, ContentStore>(7);
        storesByName.put(STORE_ONE, fileStore1);
        storesByName.put(STORE_TWO, fileStore2);
        storesByName.put(STORE_THREE, fileStore3);

        store = (StoreSelectorAspectContentStore) ctx.getBean("storeSelectorContentStore");
        store.setStoresByName(storesByName);
        store.setDefaultStoreName(STORE_ONE);
        store.afterPropertiesSet();

        // Force the constraint to re-initialize
        StoreSelectorConstraint storeConstraint = (StoreSelectorConstraint) ctx.getBean(
                "storeSelectorContentStore.constraint");
        storeConstraint.initialize();

        // Change the content service's default store
        ContentServiceImpl contentService = (ContentServiceImpl) ctx.getBean("contentService");
        contentService.setStore(store);

        // load test model containing content properties multiple
        ClassLoader cl = StoreSelectorAspectContentStoreTest.class.getClassLoader();
        InputStream modelStream = cl.getResourceAsStream("storeselectortest/MultipleContentTest_Model.xml");
        assertNotNull(modelStream);

        M2Model model = M2Model.createModel(modelStream);
        DictionaryDAO dictionaryDao = (DictionaryDAO) ctx.getBean("dictionaryDAO");
        dictionaryDao.putModel(model);

        DictionaryComponent dictionary = new DictionaryComponent();
        dictionary.setDictionaryDAO(dictionaryDao);

        // Create a content node
        RetryingTransactionCallback<NodeRef> makeNodeCallback = new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {
                StoreRef storeRef = nodeService.createStore(
                        StoreRef.PROTOCOL_TEST, getName() + "_" + System.currentTimeMillis());
                NodeRef rootNodeRef = nodeService.getRootNode(storeRef);
                // Create a folder
                NodeRef folderNodeRef = nodeService.createNode(rootNodeRef, ContentModel.ASSOC_CHILDREN,
                        ContentModel.ASSOC_CHILDREN, ContentModel.TYPE_FOLDER).getChildRef();
                // Add some content
                return fileFolderService.create(
                        folderNodeRef,
                        getName() + ".txt",
                        ContentModel.TYPE_CONTENT).getNodeRef();
            }
        };
        contentNodeRef = transactionService.getRetryingTransactionHelper().doInTransaction(makeNodeCallback);
    }

    @Override
    public void tearDown() throws Exception
    {
        AuthenticationUtil.popAuthentication();
    }

    /**
     * Writes to the file
     * 
     * @return Returns the new content URL
     */
    private String writeToFile()
    {
        RetryingTransactionCallback<String> writeContentCallback = new RetryingTransactionCallback<String>()
        {
            public String execute() throws Throwable
            {
                ContentWriter writer = fileFolderService.getWriter(contentNodeRef);
                writer.putContent("Some test content");
                return writer.getContentUrl();
            }
        };
        return transactionService.getRetryingTransactionHelper().doInTransaction(writeContentCallback);
    }

    /**
     * Set the name of the store that must hold the content
     * 
     * @param storeName
     *            the name of the store
     */
    private void setStoreNameProperty(String storeName)
    {
        // The nodeService is transactional
        nodeService.setProperty(contentNodeRef, ContentModel.PROP_STORE_NAME, storeName);
    }

    /**
     * Ensure that a <tt>null</tt> <b>cm:storeName</b> property is acceptable.
     */
    public void testNullStoreNameProperty() throws Exception
    {
        try
        {
            setStoreNameProperty(null);
        }
        catch (Throwable e)
        {
            throw new Exception("Failed to set store name property to null", e);
        }
    }

    /**
     * Ensure that an invalid <b>cm:storeName</b> property is kicked out.
     */
    public void testInvalidStoreNameProperty() throws Exception
    {
        RetryingTransactionCallback<Object> setInvalidStoreNameCallback = new RetryingTransactionCallback<Object>()
        {
            public Object execute() throws Throwable
            {
                setStoreNameProperty("bogus");
                return null;
            }
        };
        try
        {
            transactionService.getRetryingTransactionHelper().doInTransaction(setInvalidStoreNameCallback, false, true);
            setStoreNameProperty("bogus");
            fail("Expected integrity error for bogus store name");
        }
        catch (IntegrityException e)
        {
            // Expected
        }
    }

    /**
     * Check that the default store is used if the property is not set
     */
    public void testWriteWithoutAspect() throws Exception
    {
        String contentUrl = writeToFile();
        // The content should be in the default store
        assertTrue("Default store does not have content", fileStore1.exists(contentUrl));
        assertFalse("Mapped store should not have content", fileStore2.exists(contentUrl));
        assertFalse("Mapped store should not have content", fileStore3.exists(contentUrl));
    }

    public void testSimpleWritesWithAspect() throws Exception
    {
        for (Map.Entry<String, ContentStore> entry : storesByName.entrySet())
        {
            String storeName = entry.getKey();
            ContentStore store = entry.getValue();
            setStoreNameProperty(storeName);
            String contentUrl = writeToFile();
            assertTrue("Content not in store " + storeName, store.exists(contentUrl));
        }
    }

    public void testPropertyChange() throws Exception
    {
        setStoreNameProperty(STORE_ONE);
        String contentUrl = writeToFile();
        assertTrue("Store1 should have content", storesByName.get(STORE_ONE).exists(contentUrl));
        assertFalse("Store2 should NOT have content", storesByName.get(STORE_TWO).exists(contentUrl));
        assertFalse("Store3 should NOT have content", storesByName.get(STORE_THREE).exists(contentUrl));
        // Change the property
        setStoreNameProperty(STORE_TWO);
        // get back the new content url
        ContentData content = (ContentData) nodeService.getProperty(contentNodeRef, ContentModel.PROP_CONTENT);
        contentUrl = content.getContentUrl();
        // It should have moved
        assertFalse("Store1 should NOT have content", storesByName.get(STORE_ONE).exists(contentUrl));
        assertTrue("Store2 should have content", storesByName.get(STORE_TWO).exists(contentUrl));
        assertFalse("Store3 should NOT have content", storesByName.get(STORE_THREE).exists(contentUrl));
        // Change the property
        setStoreNameProperty(STORE_THREE);
        // get back the new content url
        content = (ContentData) nodeService.getProperty(contentNodeRef, ContentModel.PROP_CONTENT);
        contentUrl = content.getContentUrl();
        // It should have moved
        assertFalse("Store1 should NOT have content", storesByName.get(STORE_ONE).exists(contentUrl));
        assertFalse("Store2 should NOT have content", storesByName.get(STORE_TWO).exists(contentUrl));
        assertTrue("Store3 should NOT have content", storesByName.get(STORE_THREE).exists(contentUrl));
    }

    /**
     * Ensure that the store move does not occur if an invalid
     * <b>cm:storeName</b> property us used.
     */
    public void testPropertyChangeWithIntegrityError() throws Exception
    {
        String contentUrl = writeToFile();
        assertTrue("Store1 should have content", storesByName.get(STORE_ONE).exists(contentUrl));
        assertFalse("Store2 should NOT have content", storesByName.get(STORE_TWO).exists(contentUrl));
        RetryingTransactionCallback<Object> setInvalidStoreNameCallback = new RetryingTransactionCallback<Object>()
        {
            public Object execute() throws Throwable
            {
                setStoreNameProperty("bogus");
                return null;
            }
        };
        try
        {
            transactionService.getRetryingTransactionHelper().doInTransaction(setInvalidStoreNameCallback, false, true);
            fail("Expected integrity error for bogus store name");
        }
        catch (IntegrityException e)
        {
            // Expected
        }
        ContentData content = (ContentData) nodeService.getProperty(contentNodeRef, ContentModel.PROP_CONTENT);
        contentUrl = content.getContentUrl();
        assertTrue("Store1 should have content", storesByName.get(STORE_ONE).exists(contentUrl));
        assertFalse("Store2 should have content", storesByName.get(STORE_TWO).exists(contentUrl));

    }

    /**
     * Writes to the file
     * 
     * @return Returns the new content URL
     */
    private void populateContentProps(NodeRef contentNodeRef)
    {
        final NodeRef finalContentNodeRef = contentNodeRef;
        RetryingTransactionCallback<String> writeContentCallback = new RetryingTransactionCallback<String>()
        {
            public String execute() throws Throwable
            {
                ContentWriter writer = contentService.getWriter(
                        finalContentNodeRef, ContentModel.PROP_CONTENT, true);
                writer.putContent("Some test content 1");
                writer = contentService.getWriter(
                        finalContentNodeRef,
                        QName.createQName("testing.contentrouting", "prop1"),
                        true);
                writer.putContent("Some test content 2");
                writer = contentService.getWriter(
                        finalContentNodeRef,
                        QName.createQName("testing.contentrouting", "prop2"),
                        true);
                writer.putContent("Some test content 3");
                return "";
            }
        };
        transactionService.getRetryingTransactionHelper().doInTransaction(writeContentCallback);
    }

    /*
     * Test with multiple content properties. All the content properties should
     * always be in the same store
     */
    public void testOnMultipleContent() throws Exception
    {
        // Create a content node
        RetryingTransactionCallback<NodeRef> makeNodeCallback = new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {
                StoreRef storeRef = nodeService.createStore(
                        StoreRef.PROTOCOL_WORKSPACE,
                        "TEST_" + System.currentTimeMillis());
                NodeRef rootNodeRef = nodeService.getRootNode(storeRef);
                // Create a folder
                NodeRef folderNodeRef = nodeService.createNode(
                        rootNodeRef,
                        ContentModel.ASSOC_CHILDREN,
                        ContentModel.ASSOC_CHILDREN,
                        ContentModel.TYPE_FOLDER).getChildRef();
                // Add some content
                NodeRef res = fileFolderService.create(
                        folderNodeRef,
                        "TEST2.txt" + System.currentTimeMillis(),
                        ContentModel.TYPE_CONTENT).getNodeRef();
                nodeService.addAspect(
                        res,
                        QName.createQName("testing.contentrouting", "contentroutingtest"),
                        null);
                nodeService.addAspect(res, ContentModel.ASPECT_STORE_SELECTOR, null);
                return res;

            }
        };
        NodeRef localContentNodeRef = transactionService.getRetryingTransactionHelper().doInTransaction(makeNodeCallback);

        // add content to properties
        populateContentProps(localContentNodeRef);

        // check that all the content properties are on the default store
        ContentData contentData = (ContentData) nodeService.getProperty(localContentNodeRef, ContentModel.PROP_CONTENT);
        String contentUrl = contentData.getContentUrl();
        assertTrue("Store1 should have content", storesByName.get(STORE_ONE).exists(contentUrl));
        assertFalse("Store2 should NOT have content", storesByName.get(STORE_TWO).exists(contentUrl));
        assertFalse("Store3 should NOT have content", storesByName.get(STORE_THREE).exists(contentUrl));

        contentData = (ContentData) nodeService.getProperty(localContentNodeRef, QName.createQName("testing.contentrouting",
                "prop1"));
        contentUrl = contentData.getContentUrl();
        assertTrue("Store1 should have content", storesByName.get(STORE_ONE).exists(contentUrl));
        assertFalse("Store2 should NOT have content", storesByName.get(STORE_TWO).exists(contentUrl));
        assertFalse("Store3 should NOT have content", storesByName.get(STORE_THREE).exists(contentUrl));

        contentData = (ContentData) nodeService.getProperty(localContentNodeRef, QName.createQName("testing.contentrouting",
                "prop2"));
        contentUrl = contentData.getContentUrl();
        assertTrue("Store1 should have content", storesByName.get(STORE_ONE).exists(contentUrl));
        assertFalse("Store2 should NOT have content", storesByName.get(STORE_TWO).exists(contentUrl));
        assertFalse("Store3 should NOT have content", storesByName.get(STORE_THREE).exists(contentUrl));

        // move the content
        // Change the property
        nodeService.setProperty(localContentNodeRef, ContentModel.PROP_STORE_NAME, STORE_TWO);

        // check that all the content properties are on the default store
        contentData = (ContentData) nodeService.getProperty(localContentNodeRef, ContentModel.PROP_CONTENT);
        contentUrl = contentData.getContentUrl();
        assertFalse("Store1 should NOT have content", storesByName.get(STORE_ONE).exists(contentUrl));
        assertTrue("Store2 should have content", storesByName.get(STORE_TWO).exists(contentUrl));
        assertFalse("Store3 should NOT have content", storesByName.get(STORE_THREE).exists(contentUrl));

        contentData = (ContentData) nodeService.getProperty(localContentNodeRef, QName.createQName("testing.contentrouting",
                "prop1"));
        contentUrl = contentData.getContentUrl();
        assertFalse("Store1 should NOT have content", storesByName.get(STORE_ONE).exists(contentUrl));
        assertTrue("Store2 should have content", storesByName.get(STORE_TWO).exists(contentUrl));
        assertFalse("Store3 should NOT have content", storesByName.get(STORE_THREE).exists(contentUrl));

        contentData = (ContentData) nodeService.getProperty(
                localContentNodeRef,
                QName.createQName("testing.contentrouting",
                "prop2"));
        contentUrl = contentData.getContentUrl();
        assertFalse("Store1 should NOT have content", storesByName.get(STORE_ONE).exists(contentUrl));
        assertTrue("Store2 should have content", storesByName.get(STORE_TWO).exists(contentUrl));
        assertFalse("Store3 should NOT have content", storesByName.get(STORE_THREE).exists(contentUrl));
        
        
        //remove the aspect and all content proprties should be brought back to default
        nodeService.removeAspect(localContentNodeRef, ContentModel.ASPECT_STORE_SELECTOR);
        contentData = (ContentData) nodeService.getProperty(localContentNodeRef, ContentModel.PROP_CONTENT);
        contentUrl = contentData.getContentUrl();
        assertTrue("Store1 should have content", storesByName.get(STORE_ONE).exists(contentUrl));
        assertFalse("Store2 should NOT have content", storesByName.get(STORE_TWO).exists(contentUrl));
        assertFalse("Store3 should NOT have content", storesByName.get(STORE_THREE).exists(contentUrl));

        contentData = (ContentData) nodeService.getProperty(
                localContentNodeRef,
                QName.createQName("testing.contentrouting", "prop1"));
        contentUrl = contentData.getContentUrl();
        assertTrue("Store1 should have content", storesByName.get(STORE_ONE).exists(contentUrl));
        assertFalse("Store2 should NOT have content", storesByName.get(STORE_TWO).exists(contentUrl));
        assertFalse("Store3 should NOT have content", storesByName.get(STORE_THREE).exists(contentUrl));

        contentData = (ContentData) nodeService.getProperty(
                localContentNodeRef,
                QName.createQName("testing.contentrouting", "prop2"));
        contentUrl = contentData.getContentUrl();
        assertTrue("Store1 should have content", storesByName.get(STORE_ONE).exists(contentUrl));
        assertFalse("Store2 should NOT have content", storesByName.get(STORE_TWO).exists(contentUrl));
        assertFalse("Store3 should NOT have content", storesByName.get(STORE_THREE).exists(contentUrl));
    }

    /**
     * Ensure that after remove aspect the content is brought back to default.
     */
    public void testRemoveAspectContentStore() throws Exception
    {
        setStoreNameProperty(STORE_TWO);
        String contentUrl = writeToFile();
        assertFalse("Store1 should NOT have content", storesByName.get(STORE_ONE).exists(contentUrl));
        assertTrue("Store2 should have content", storesByName.get(STORE_TWO).exists(contentUrl));
        RetryingTransactionCallback<Object> removeAspectCallback = new RetryingTransactionCallback<Object>()
        {
            public Object execute() throws Throwable
            {
                // remove the aspect
                store.beforeRemoveAspect(contentNodeRef, ContentModel.ASPECT_STORE_SELECTOR);
                return null;
            }
        };
        transactionService.getRetryingTransactionHelper().doInTransaction(removeAspectCallback, false, true);
        // remove the aspect
        ContentData content = (ContentData) nodeService.getProperty(contentNodeRef, ContentModel.PROP_CONTENT);
        contentUrl = content.getContentUrl();
        assertTrue("Store1 should have content", storesByName.get(STORE_ONE).exists(contentUrl));
        assertFalse("Store2 should NOT have content", storesByName.get(STORE_TWO).exists(contentUrl));
    }

    /**
     * Test Check out content. Working copy should be in the same store.
     */    
    public void testCheckoutOfContent() throws Exception 
    {
		setStoreNameProperty(STORE_TWO);
		String contentUrl = writeToFile();
		
		NodeRef workingCopy1 = checkOutCheckInService.getWorkingCopy(contentNodeRef);
		assertNull(workingCopy1);
		
		RetryingTransactionCallback<NodeRef> checkOutCallback = new RetryingTransactionCallback<NodeRef>()
		{
			public NodeRef execute() throws Throwable 
			{
				NodeRef workingCopy = checkOutCheckInService.checkout(contentNodeRef);
				return workingCopy;
			}
		};
		NodeRef workingCopy2 = transactionService.getRetryingTransactionHelper().doInTransaction(checkOutCallback);
		assertNotNull(workingCopy2);

		// Ensure that the working copy and copy aspect has been applied
		assertTrue(nodeService.hasAspect(workingCopy2, ContentModel.ASPECT_WORKING_COPY));
		assertTrue(nodeService.hasAspect(workingCopy2, ContentModel.ASPECT_COPIEDFROM));
		
		// Check that the working copy name has been set correctly
		String name = (String)this.nodeService.getProperty(contentNodeRef, ContentModel.PROP_NAME);
        String expectedWorkingCopyLabel = I18NUtil.getMessage("coci_service.working_copy_label");
        String expectedWorkingCopyName = CheckOutCheckInServiceImpl.createWorkingCopyName(name, expectedWorkingCopyLabel);
        String workingCopyName = (String)this.nodeService.getProperty(workingCopy2, ContentModel.PROP_NAME);
        assertEquals(expectedWorkingCopyName, workingCopyName);
        
        // It shouldn't have moved
        ContentData content = (ContentData) nodeService.getProperty(contentNodeRef, ContentModel.PROP_CONTENT);
        contentUrl = content.getContentUrl();
        assertFalse("Store1 should NOT have content", storesByName.get(STORE_ONE).exists(contentUrl));
        assertTrue("Store2 should have content", storesByName.get(STORE_TWO).exists(contentUrl));
        assertFalse("Store3 should NOT have content", storesByName.get(STORE_THREE).exists(contentUrl));
	}
    
    /**
     * Test Update of content.
     */     
    public void testUpdateOfContent() throws Exception
    {
		setStoreNameProperty(STORE_TWO);
		String contentUrl = writeToFile();
		
		//Write updated content
        RetryingTransactionCallback<Object> writeUpdatedContentCallback = new RetryingTransactionCallback<Object>()
        {
            public Object execute() throws Throwable
            {
                ContentWriter writer = fileFolderService.getWriter(contentNodeRef);
                writer.putContent("Updated test content");
                return null;
            }
        };
        transactionService.getRetryingTransactionHelper().doInTransaction(writeUpdatedContentCallback);
                
        // It shouldn't have moved
        ContentData content = (ContentData) nodeService.getProperty(contentNodeRef, ContentModel.PROP_CONTENT);
        contentUrl = content.getContentUrl();
        assertFalse("Store1 should NOT have content", storesByName.get(STORE_ONE).exists(contentUrl));
        assertTrue("Store2 should have content", storesByName.get(STORE_TWO).exists(contentUrl));
        assertFalse("Store3 should NOT have content", storesByName.get(STORE_THREE).exists(contentUrl));        
    }

    /**
     * MNT-9688: test that document version is the same after the storename is changed
     */     
    public void testVersionOnStoreChange() throws Exception
    {
        transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>()
        {
            public Object execute() throws Exception
            {
                if (!nodeService.hasAspect(contentNodeRef, ContentModel.ASPECT_VERSIONABLE))
                {
                    nodeService.addAspect(contentNodeRef, ContentModel.ASPECT_VERSIONABLE, new HashMap<QName, Serializable>());
                }
                return null;
            }
        });
        
        assertEquals("Wrong version label", "1.0", versionService.getCurrentVersion(contentNodeRef).getVersionLabel());
        
        setStoreNameProperty(STORE_TWO);
        
        assertEquals("Version label should remain", "1.0", versionService.getCurrentVersion(contentNodeRef).getVersionLabel());
    }
    
    public void testUpdateProperty() throws Exception
    {

        // Set default store store 1
        store.setDefaultStoreName(STORE_ONE);

        String contentUrl = writeToFile();
        // The content should be in the default store - fielStore1
        assertTrue("Store1 should have content", storesByName.get(STORE_ONE).exists(contentUrl));
        assertFalse("Store2 should NOT have content", storesByName.get(STORE_TWO)
                .exists(contentUrl));
        assertFalse("Store3 should NOT have content",
                storesByName.get(STORE_THREE).exists(contentUrl));

        store.setDefaultStoreName(STORE_TWO);
        ContentData content = (ContentData) nodeService.getProperty(contentNodeRef,
                ContentModel.PROP_CONTENT);
        contentUrl = content.getContentUrl();
        // nothing changed, only default store
        assertTrue("Store1 should have content", storesByName.get(STORE_ONE).exists(contentUrl));
        assertFalse("Store2 should NOT have content", storesByName.get(STORE_TWO)
                .exists(contentUrl));
        assertFalse("Store3 should NOT have content",
                storesByName.get(STORE_THREE).exists(contentUrl));

        // set default store to Store3
        store.setDefaultStoreName(STORE_THREE);
        // move from default to Store2
        setStoreNameProperty(STORE_TWO);
        content = (ContentData) nodeService.getProperty(contentNodeRef, ContentModel.PROP_CONTENT);
        contentUrl = content.getContentUrl();
        assertFalse("Store1 should NOT have content", storesByName.get(STORE_ONE)
                .exists(contentUrl));
        assertTrue("Store2 should have content", storesByName.get(STORE_TWO).exists(contentUrl));
        assertFalse("Store3 should NOT have content",
                storesByName.get(STORE_THREE).exists(contentUrl));

    }
}
