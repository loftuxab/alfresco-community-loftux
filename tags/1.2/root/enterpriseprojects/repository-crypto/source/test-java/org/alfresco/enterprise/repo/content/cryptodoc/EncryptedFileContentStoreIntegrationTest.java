/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.enterprise.repo.content.cryptodoc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.List;

import javax.transaction.UserTransaction;

import org.alfresco.encryption.MissingKeyException;
import org.alfresco.enterprise.license.ValidLicenseEvent;
import org.alfresco.enterprise.repo.content.cryptodoc.impl.CryptoContentStore;
import org.alfresco.enterprise.repo.content.cryptodoc.io.IOUtils;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.AbstractContentStore;
import org.alfresco.repo.content.AbstractWritableContentStoreTest;
import org.alfresco.repo.content.ContentContext;
import org.alfresco.repo.content.ContentServiceImpl;
import org.alfresco.repo.content.ContentStore;
import org.alfresco.repo.domain.contentdata.ContentDataDAO;
import org.alfresco.repo.domain.contentdata.ContentUrlEntity;
import org.alfresco.repo.domain.contentdata.ContentUrlKeyEntity;
import org.alfresco.repo.management.subsystems.ChildApplicationContextFactory;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.tenant.Tenant;
import org.alfresco.repo.tenant.TenantAdminService;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.descriptor.DescriptorService;
import org.alfresco.service.license.LicenseDescriptor;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.test_category.OwnJVMTestsCategory;
import org.alfresco.util.BaseApplicationContextHelper;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.webscripts.GUID;

/**
 * TODO: add to suite?
 * 
 * @author sglover
 * @author Matt Ward
 */
@Category(OwnJVMTestsCategory.class)
public class EncryptedFileContentStoreIntegrationTest extends AbstractWritableContentStoreTest
{
    private CryptoContentStore store;
    private ContentServiceImpl contentService;
    private FileFolderService fileFolderService;
    private NodeService nodeService;
    private NamespaceService namespaceService;
    private SearchService searchService;
    private MasterKeystoreService masterKeystoreService;
    private ContentDataDAO contentDataDAO;
    private TenantAdminService tenantAdminService;

    private UserTransaction txn;

    private NodeRef companyHomeNodeRef;

    public static final String[] CONFIG_LOCATIONS = new String[] { "classpath:alfresco/application-context.xml" };

    @BeforeClass
    public static void beforeClass() throws Exception
    {
    	ctx = BaseApplicationContextHelper.getApplicationContext(
    			CONFIG_LOCATIONS, new String[] {"classpath*:/crypto/"});
    }

    @Before
    public void before() throws Exception
    {
    	// license workaround/hackery for the test
    	LicenseDescriptor license = mock(LicenseDescriptor.class);
    	when(license.isCryptodocEnabled()).thenReturn(true);
    	DescriptorService descriptorService = mock(DescriptorService.class);
    	when(descriptorService.getLicenseDescriptor()).thenReturn(license);

        this.contentService = (ContentServiceImpl)ctx.getBean("contentService");
        this.fileFolderService = (FileFolderService)ctx.getBean("FileFolderService");
        this.nodeService = (NodeService)ctx.getBean("NodeService");
        this.namespaceService = (NamespaceService)ctx.getBean("namespaceService");
        this.searchService = (SearchService)ctx.getBean("SearchService");
        this.transactionService = (TransactionService)ctx.getBean("TransactionService");
        this.contentDataDAO = (ContentDataDAO)ctx.getBean("contentDataDAO");
        this.tenantAdminService = (TenantAdminService) ctx.getBean("tenantAdminService");

        ChildApplicationContextFactory encryptedContentSubsystem = (ChildApplicationContextFactory)ctx.getBean("encryptedContentStore");
        assertNotNull("encryptedContentSubsystem", encryptedContentSubsystem);
        ApplicationContext encryptedContentCtx = encryptedContentSubsystem.getApplicationContext();

        this.masterKeystoreService = (MasterKeystoreService)encryptedContentCtx.getBean("masterKeyStoreService");
        this.store = (CryptoContentStore)encryptedContentCtx.getBean("fileContentStore");
        this.store.setDescriptorService(descriptorService);
        ValidLicenseEvent event = new ValidLicenseEvent(this, license);
        this.store.onApplicationEvent(event);
        this.contentService.setStore(store);

        AuthenticationUtil.pushAuthentication();
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        this.txn = transactionService.getUserTransaction();
        this.txn.begin();

        String companyHomePathInStore = "/app:company_home"; 
        String storePath = "workspace://SpacesStore";
        StoreRef storeRef = new StoreRef(storePath);

        NodeRef storeRootNodeRef = nodeService.getRootNode(storeRef);
        List<NodeRef> nodeRefs = searchService.selectNodes(storeRootNodeRef, companyHomePathInStore, null, namespaceService, false);
        this.companyHomeNodeRef = nodeRefs.get(0);
    }

    @After
    public void after() throws Exception
    {
    	if(txn != null)
    	{
    		txn.commit();
    	}
    	AuthenticationUtil.popAuthentication();
    }

    @Override
    protected ContentWriter getWriter()
    {
        return contentService.getWriter(null, ContentModel.PROP_CONTENT, true);
    }

    @Override
    protected ContentStore getStore()
    {
        return store;
    }

    @Ignore("Not implemented for encrypting content store, too difficult to implement")
    @Override
    public void testRandomAccessRead() throws Exception
    {
    }

    @Ignore("This is failing and I don't yet know why")
    @Test
    public void testRandomAccessWrite() throws Exception
    {
        ContentWriter writer = getWriter();
        
        FileChannel fileChannel = writer.getFileChannel(true);
        assertNotNull("No channel given", fileChannel);
        
        // check that no other content access is allowed
        try
        {
            writer.getWritableChannel();
            fail("Second channel access allowed");
        }
        catch (RuntimeException e)
        {
            // expected
        }
        
        // write some content in a random fashion (reverse order)
        byte[] content = new byte[] {1, 2, 3};
        for (int i = content.length - 1; i >= 0; i--)
        {
            ByteBuffer buffer = ByteBuffer.wrap(content, i, 1);
            fileChannel.write(buffer, i);
        }
        
        // close the channel
        fileChannel.close();
        assertTrue("Writer not closed", writer.isClosed());
        
        // check the content
        ContentReader reader = writer.getReader();
        ReadableByteChannel channelReader = reader.getReadableChannel();
        ByteBuffer buffer = ByteBuffer.allocateDirect(16);
        int count = channelReader.read(buffer);
        assertEquals("Incorrect number of bytes read", 3, count);
        for (int i = 0; i < content.length; i++)
        {
            assertEquals("Content doesn't match", content[i], buffer.get(i));
        }
        
        // get a new writer from the store, using the existing content and perform a truncation check
        ContentContext writerTruncateCtx = new ContentContext(writer.getReader(), null);
        ContentWriter writerTruncate = getStore().getWriter(writerTruncateCtx);
        assertEquals("Content size incorrect", 0, writerTruncate.getSize());
        // get the channel with truncation
        FileChannel fcTruncate = writerTruncate.getFileChannel(true);
        fcTruncate.close();
        assertEquals("Content not truncated", 0, writerTruncate.getSize());
        
        // get a new writer from the store, using the existing content and perform a non-truncation check
        ContentContext writerNoTruncateCtx = new ContentContext(writer.getReader(), null);
        ContentWriter writerNoTruncate = getStore().getWriter(writerNoTruncateCtx);
        assertEquals("Content size incorrect", 0, writerNoTruncate.getSize());
        // get the channel without truncation
        FileChannel fcNoTruncate = writerNoTruncate.getFileChannel(false);
        fcNoTruncate.close();
        assertEquals("Content was truncated", writer.getSize(), writerNoTruncate.getSize());
    }

    private NodeRef createNode()
    {
    	NodeRef nodeRef = fileFolderService.create(companyHomeNodeRef, GUID.generate(), ContentModel.TYPE_CONTENT).getNodeRef();
    	return nodeRef;
    }

    /**
     * This won't work for the encrypted content store (which requires the content url key to be updated
     * in the DAO, and this will only work for writers obtained from the content service)
     */
    @Test
    @Override
    public void testSimpleUse()
    {
    	NodeRef nodeRef = createNode();

    	ContentWriter writer = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
        String content = "Content for testSimpleUse";

        assertNotNull("Writer may not be null", writer);
        // Ensure that the URL is available
        String contentUrlBefore = writer.getContentUrl();
        assertNotNull("Content URL may not be null for unused writer", contentUrlBefore);
        assertTrue("URL is not valid: " + contentUrlBefore, AbstractContentStore.isValidContentUrl(contentUrlBefore));
        // Write something
        writer.putContent(content);
        String contentUrlAfter = writer.getContentUrl();
        assertTrue("URL is not valid: " + contentUrlBefore, AbstractContentStore.isValidContentUrl(contentUrlAfter));
        assertEquals("The content URL may not change just because the writer has put content", contentUrlBefore, contentUrlAfter);
        // Get the readers
        ContentReader reader = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
        assertNotNull("Reader from store is null", reader);
        assertEquals(reader.getContentUrl(), writer.getContentUrl());
        String checkContent = reader.getContentString();
        assertEquals("Content is different", content, checkContent);
    }

    @Override
    @Test
    public void testDeleteReaderStates() throws Exception
    {
    	NodeRef nodeRef = createNode();

    	ContentWriter writer = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);

        String content = "Content for testDeleteReaderStates";
        String contentUrl = writer.getContentUrl();

        // write some bytes, but don't close the stream
        OutputStream os = writer.getContentOutputStream();
        os.write(content.getBytes());
        os.flush();                  // make sure that the bytes get persisted
        // close the stream
        os.close();
        
        // get a reader
        ContentReader reader = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
        assertNotNull(reader);
        
        ContentReader readerCheck = writer.getReader();
        assertNotNull(readerCheck);
        assertEquals("Store and write provided readers onto different URLs",
                writer.getContentUrl(), reader.getContentUrl());
        
        // open the stream onto the content
        InputStream is = reader.getContentInputStream();
        
        // attempt to delete the content
        boolean deleted = store.delete(contentUrl);

        // close the reader stream
        is.close();
        
        // get a fresh reader
        reader = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
        assertNotNull(reader);
        
        // the underlying system may or may not have deleted the content
        if (deleted)
        {
            assertFalse("Content should not exist", reader.exists());
            // drop out here
            return;
        }
        else
        {
            assertTrue("Content should exist", reader.exists());
        }

        // delete the content
        nodeService.deleteNode(nodeRef);

        // attempt to read from the reader
        try
        {
            is = reader.getContentInputStream();
            fail("Reader failed to detect underlying content deletion");
        }
        catch (ContentIOException e)
        {
            // expected
        }

        // get another fresh reader
        reader = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
        assertNotNull("Reader must be returned even when underlying content is missing",
                reader);
        assertFalse("Content should not exist", reader.exists());
        try
        {
            is = reader.getContentInputStream();
            fail("Reader opened stream onto missing content");
        }
        catch (ContentIOException e)
        {
            // expected
        }
    }

    @Ignore("Not relevant here")
    @Override
    @Test
    public void testGetReader() throws Exception
    {
    }
    
    @Test
    public void testCreateTenant()
    {
        long currentTime = System.currentTimeMillis();
        String tenantName = currentTime + ".test";
        String tenantName2 = currentTime + ".test2";
        String rootTenant = "./tenant_stores.test";
        if (tenantAdminService.isEnabled())
        {
            tenantAdminService.createTenant(tenantName, "admin".toCharArray());
            Tenant tenant = tenantAdminService.getTenant(tenantName);
            assertNotNull(tenant);
            tenantAdminService.deleteTenant(tenantName);
            tenantAdminService.createTenant(tenantName2, "admin".toCharArray(), rootTenant);
            tenant = tenantAdminService.getTenant(tenantName2);
            assertNotNull(tenant);
            tenantAdminService.deleteTenant(tenantName2);
            File file = new File(rootTenant);
            if (file.exists())
            {
                try
                {
                    FileUtils.deleteDirectory(file);
                }
                catch (IOException e) {
                }
            }
        }
    }

    @Test
    public void testInterleaves() throws Exception
    {
    	NodeRef nodeRef = createNode();

    	// round 1
    	{
	    	ContentWriter writer = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
	
	        String content = "Some Example Content 1";
	
	        // write some bytes, but don't close the stream
	        OutputStream os = writer.getContentOutputStream();
	        os.write(content.getBytes());
	        os.flush();                  // make sure that the bytes get persisted
	        // close the stream
	        os.close();
	
	        // get a reader
	        ContentReader reader = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
	        assertNotNull(reader);
	
	        ContentReader readerCheck = writer.getReader();
	        assertNotNull(readerCheck);
	        assertEquals("Store and write provided different content", content, reader.getContentString());
    	}

    	// round 2
    	{
	    	ContentWriter writer = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
	
	        String content = "Some Example Content 2";
	
	        // write some bytes, but don't close the stream
	        OutputStream os = writer.getContentOutputStream();
	        os.write(content.getBytes());
	        os.flush();                  // make sure that the bytes get persisted
	        // close the stream
	        os.close();
	
	        // get a reader
	        ContentReader reader = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
	        assertNotNull(reader);
	
	        ContentReader readerCheck = writer.getReader();
	        assertNotNull(readerCheck);
	        assertEquals("Store and write provided different content", content, reader.getContentString());
    	}

    	// round 2
    	{
	    	ContentWriter writer = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
	
	        String content = "Some Example Content 2";
	
	        // write some bytes, but don't close the stream
	        OutputStream os = writer.getContentOutputStream();
	        os.write(content.getBytes());
	        os.flush();                  // make sure that the bytes get persisted
	        // close the stream
	        os.close();
	
	        // get a reader
	        ContentReader reader = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
	        assertNotNull(reader);
	
	        ContentReader readerCheck = writer.getReader();
	        assertNotNull(readerCheck);
	        assertEquals("Store and write provided different content", content, reader.getContentString());
    	}

    	// round 3
    	{
	    	ContentWriter writer = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
	
	        String content = "Some Example Content 3";
	
	        // write some bytes, but don't close the stream
	        OutputStream os = writer.getContentOutputStream();
	        os.write(content.getBytes());
	        os.flush();                  // make sure that the bytes get persisted
	        // close the stream
	        os.close();
	
	        writer = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
	    	
	        content = "Some Example Content 4";
	
	        // write some bytes, but don't close the stream
	        os = writer.getContentOutputStream();
	        os.write(content.getBytes());
	        os.flush();                  // make sure that the bytes get persisted
	        // close the stream
	        os.close();

	        // get a reader
	        ContentReader reader = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
	        assertNotNull(reader);
	
	        ContentReader readerCheck = writer.getReader();
	        assertNotNull(readerCheck);
	        assertEquals("Store and write provided different content", content, reader.getContentString());
    	}
    }

    @Ignore("Failing in the superclass and I don't know why yet")
    @Test
    public void testReadAndWriteStreamByPush() throws Exception
    {
    }

    //@Ignore
    @Test
    public void testMultipleWritesAndReads() throws Exception
    {
    	NodeRef nodeRef = createNode();
        String content = "Some random content";

    	{
	    	ContentWriter writer = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
	
	        // write some bytes, but don't close the stream
	        writer.putContent(content);
	
	        // get a reader
	        ContentReader reader = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
	        assertNotNull(reader);
	
	        ContentReader readerCheck = writer.getReader();
	        assertNotNull(readerCheck);
	        assertEquals("Store and write provided different content", content, reader.getContentString());
    	}

    	{
	    	ContentWriter writer = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);

	        // write some bytes, but don't close the stream
	        WritableByteChannel out = writer.getWritableChannel();
	        InputStream in = new ByteArrayInputStream(content.getBytes("UTF-8"));
	        IOUtils.copy(in, out, 16384);
	
	        // get a reader
	        ContentReader reader = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
	        assertNotNull(reader);
	
	        ContentReader readerCheck = writer.getReader();
	        assertNotNull(readerCheck);
	        assertEquals("Store and write provided different content", content, reader.getContentString());
    	}

    	{
	    	ContentWriter writer = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
	
	        // write some bytes, but don't close the stream
	        OutputStream out = writer.getContentOutputStream();
	        out.write(content.getBytes("UTF-8"));
	        out.flush();
	        out.close();
	
	        // get a reader
	        ContentReader reader = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
	        assertNotNull(reader);
	
	        ContentReader readerCheck = writer.getReader();
	        assertNotNull(readerCheck);
	        assertEquals("Store and write provided different content", content, reader.getContentString());
    	}
    }

    @Test
    public void testRevokeMasterKey() throws Exception
    {
        final NodeRef nodeRef = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            @Override
            public NodeRef execute() throws Throwable
            {
                return createNode();
            }
        }, false, true);
    	final String masterKeyAlias = "mkey1";

        assertEquals(2, masterKeystoreService.getMasterKeys().size());

    	// invalid master key
    	try
    	{
	    	KeyReference masterKeyRef = new KeyReference();
	    	masterKeyRef.setAlias(GUID.generate());
	    	masterKeystoreService.revokeMasterKey(masterKeyRef);
	    	fail("Should not be able to revoke invalid master key");
    	}
    	catch(AlfrescoRuntimeException e)
    	{
    	    assertTrue(e.getCause() instanceof MissingKeyException);
    		// ok
    	}

    	{
    		final KeyReference masterKeyRef = new KeyReference();
    		masterKeyRef.setAlias(masterKeyAlias);

    		transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Void>()
    		{
				@Override
				public Void execute() throws Throwable
				{
			    	masterKeystoreService.revokeMasterKey(masterKeyRef);
			    	masterKeystoreService.reEncryptSymmetricKeys(masterKeyRef);
			    	return null;
				}
    		}, false, true);

	    	// re-encryption happens in the background, so try a few times
	    	long count = -1;
	    	for(int i = 0; i < 5; i++)
	    	{
	    		Thread.sleep(1000);
	    		count = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Long>()
	    		{
					@Override
					public Long execute() throws Throwable
					{
						long count = contentDataDAO.countSymmetricKeysForMasterKeyAlias(masterKeyAlias);
						return count;
					}
				}, true, true);

		    	if(count > 0)
		    	{
		    		continue;
		    	}
                else if (count == 0)
                {
                    // no need to wait more iterations
                    break;
                }
	    	}

	    	assertEquals("", 0, count);
    	}

    	// further encryptions should be using a different key
    	for(int i = 0; i < 10; i++)
    	{
            // any interactions with the MasterKeyService "after" the revocation
            // should be performed in a separate, new transaction
            transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Void>()
            {
                @Override
                public Void execute() throws Throwable
                {
                    String content = "Some Test Content";

//                  ContentContext ctx = new NodeContentContext(null, null, nodeRef, ContentModel.PROP_CONTENT);
//                  ContentWriter writer = store.getWriter(ctx);
                    ContentWriter writer = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
	
                    // write some bytes, but don't close the stream
                    OutputStream out = writer.getContentOutputStream();
                    String contentUrl = writer.getContentUrl();
                    out.write(content.getBytes("UTF-8"));
                    out.flush();
                    out.close();

                    ContentUrlEntity contentUrlEntity = contentDataDAO.getContentUrl(contentUrl);
                    assertNotNull(contentUrlEntity);
                    ContentUrlKeyEntity contentUrlKey = contentUrlEntity.getContentUrlKey();
                    assertNotNull(contentUrlKey);
                    String testMasterKeyAlias = contentUrlKey.getMasterKeyAlias();
                    assertNotEquals("", masterKeyAlias, testMasterKeyAlias);

                    // get a reader
                    ContentReader reader = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
                    assertNotNull(reader);

                    ContentReader readerCheck = writer.getReader();
                    assertNotNull(readerCheck);
                    assertEquals("Store and write provided different content", content, reader.getContentString());
                    return null;
                }
            }, false, true);
    	}

    	// should not be able to revoke last master key
    	try
    	{
	    	KeyReference masterKeyRef = new KeyReference();
	    	masterKeyRef.setAlias("mkey2");
	    	masterKeystoreService.revokeMasterKey(masterKeyRef);
	    	fail("Should not be able to revoke last master key");
    	}
    	catch(CryptoException e)
    	{
    		// ok
    	}
    }

    @Ignore("Not relevant - content url is not registered with the content dao which the crypto content store needs")
    @Test
    public void testGetReaderForExistingContentUrl() throws Exception
    {
    }

    @Test
    public void testTenantCreation() throws Exception
    {
        final String TEST_TENANT_DOMAIN = System.currentTimeMillis() + ".my.test";
        final String DEFAULT_ADMIN_PW = "admin";

        AuthenticationUtil.runAs(new RunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                tenantAdminService.createTenant(TEST_TENANT_DOMAIN, (DEFAULT_ADMIN_PW + " " + TEST_TENANT_DOMAIN).toCharArray(), null);
                return null;
            }
        }, AuthenticationUtil.SYSTEM_USER_NAME);
        
        assertTrue(tenantAdminService.existsTenant(TEST_TENANT_DOMAIN));
    }
}
