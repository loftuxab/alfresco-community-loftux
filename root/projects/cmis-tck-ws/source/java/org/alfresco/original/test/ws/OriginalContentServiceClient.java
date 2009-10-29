/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.original.test.ws;

import java.io.InputStream;

import javax.xml.rpc.ServiceException;

import org.alfresco.cmis.test.ws.AbstractService;
import org.alfresco.cmis.test.ws.AbstractServiceClient;
import org.alfresco.repo.webservice.content.ContentServiceLocator;
import org.alfresco.repo.webservice.content.ContentServiceSoapBindingStub;
import org.alfresco.repo.webservice.repository.RepositoryServiceLocator;
import org.alfresco.repo.webservice.repository.RepositoryServiceSoapBindingStub;
import org.alfresco.repo.webservice.repository.UpdateResult;
import org.alfresco.repo.webservice.types.CML;
import org.alfresco.repo.webservice.types.CMLCreate;
import org.alfresco.repo.webservice.types.CMLDelete;
import org.alfresco.repo.webservice.types.ContentFormat;
import org.alfresco.repo.webservice.types.NamedValue;
import org.alfresco.repo.webservice.types.Node;
import org.alfresco.repo.webservice.types.ParentReference;
import org.alfresco.repo.webservice.types.Predicate;
import org.alfresco.repo.webservice.types.Reference;
import org.alfresco.repo.webservice.types.Store;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

/**
 * Client for Content Service
 */
public class OriginalContentServiceClient extends AbstractServiceClient
{
    private static Log LOGGER = LogFactory.getLog(OriginalContentServiceClient.class);

    private static final String TEST_FILE_ID = "1";
    private static final String TEST_DOC_ID = "2";

    private static final String ENCODING = "UTF-8";
    private static final String TEST_DOC_NAME = "quick.doc";
    private static final String WORKSPACE_STORE = "workspace";
    private static final String SPACES_STORE = "SpacesStore";

    private static final String TEST_FILE_NAME = "test.txt";
    private static final String TEST_CONTENT = "Test content";
    private static final String MIMETYPE_TEXT_PLAIN = "text/plain";
    public static final String MIMETYPE_IMAGE_JPEG = "image/jpeg";
    public static final String MIMETYPE_APPLICATION_MSWORD = "application/msword";

    private static final String PROP_NAME = "{http://www.alfresco.org/model/content/1.0}name";
    private static final String PROP_CONTENT = "{http://www.alfresco.org/model/content/1.0}content";
    private static final String TYPE_CONTENT = "{http://www.alfresco.org/model/content/1.0}content";
    private static final String ASSOC_CHILDREN = "{http://www.alfresco.org/model/system/1.0}children";
    private static final String CHILD_ASSOCIATION_NAME = "{http://www.alfresco.org/model/content/1.0}test" + System.currentTimeMillis();

    private Store store;

    private Reference contentRef;
    private Reference transformContentRef;

    // private Resource testImageResource;
    private Resource testDocumentResource;

    private AbstractService repositoryService;

    public OriginalContentServiceClient(AbstractService abstractService)
    {
        super(abstractService);
    }

    public void setRepositoryService(AbstractService repositoryService)
    {
        this.repositoryService = repositoryService;
    }

    // public void setTestImageResource(Resource testImageResource)
    // {
    //     this.testImageResource = testImageResource;
    // }

    public void setTestDocumentResource(Resource testDocumentResource)
    {
        this.testDocumentResource = testDocumentResource;
    }

    /**
     * Starts session and initializes Content Service client
     */
    public void initialize() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Initializing client...");
        }
        startSession();

        RepositoryServiceSoapBindingStub repositoryService = getRepositoryService(getServerUrl() + this.repositoryService.getPath());

        for (Store cStore : repositoryService.getStores())
        {
            if (WORKSPACE_STORE.equals(cStore.getScheme()) && SPACES_STORE.equals(cStore.getAddress()))
            {
                store = cStore;
                break;
            }
        }
        Predicate predicate = new Predicate(null, store, null);
        Node[] nodes = repositoryService.get(predicate);
        Reference rootReference = nodes[0].getReference();

        ParentReference parentRef = new ParentReference();
        parentRef.setStore(store);
        parentRef.setUuid(rootReference.getUuid());
        parentRef.setAssociationType(ASSOC_CHILDREN);
        parentRef.setChildName(CHILD_ASSOCIATION_NAME);

        NamedValue[] properties = new NamedValue[] { new NamedValue(PROP_NAME, false, System.currentTimeMillis() + TEST_FILE_NAME, null) };
        NamedValue[] properties1 = new NamedValue[] { new NamedValue(PROP_NAME, false, TEST_DOC_NAME, null) };
        CMLCreate create = new CMLCreate(TEST_FILE_ID, parentRef, null, null, null, TYPE_CONTENT, properties);
        CMLCreate create1 = new CMLCreate(TEST_DOC_ID, parentRef, null, null, null, TYPE_CONTENT, properties1);
        CML cml = new CML();
        cml.setCreate(new CMLCreate[] { create, create1 });
        UpdateResult[] result = repositoryService.update(cml);
        contentRef = result[0].getDestination();
        transformContentRef = result[1].getDestination();

        ContentFormat format = new ContentFormat(MIMETYPE_APPLICATION_MSWORD, ENCODING);
        InputStream viewStream = testDocumentResource.getInputStream();
        byte[] bytes = convertToByteArray(viewStream);
        getContentService(getServerUrl() + getService().getPath()).write(transformContentRef, PROP_CONTENT, bytes, format);
    }

    /**
     * Invokes all methods in Content Service
     */
    public void invoke() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Invoking client...");
        }
        ContentServiceSoapBindingStub contentService = getContentService(getProxyUrl() + getService().getPath());

        contentService.write(contentRef, PROP_CONTENT, TEST_CONTENT.getBytes(), new ContentFormat(MIMETYPE_TEXT_PLAIN, ENCODING));

        contentService.read(new Predicate(new Reference[] { contentRef }, null, null), PROP_CONTENT);

        // no such method in v2.1
        // ContentFormat format = new ContentFormat(MIMETYPE_IMAGE_JPEG, ENCODING);
        // InputStream viewStream = testImageResource.getInputStream();
        // File testFile = File.createTempFile("testImage", ".jpg");
        // FileOutputStream fos = new FileOutputStream(testFile);
        // copy(viewStream, fos);
        // DataHandler attachmentFile = new DataHandler(new FileDataSource(testFile));
        // contentService._setProperty(Call.ATTACHMENT_ENCAPSULATION_FORMAT, Call.ATTACHMENT_ENCAPSULATION_FORMAT_DIME);
        // contentService.addAttachment(attachmentFile);
        // contentService.writeWithAttachment(contentRef, PROP_CONTENT, format);

        // no such method in v2.1
        contentService.transform(transformContentRef, PROP_CONTENT, contentRef, PROP_CONTENT, new ContentFormat(MIMETYPE_TEXT_PLAIN, ENCODING));

        contentService.clear(new Predicate(new Reference[] { contentRef }, null, null), PROP_CONTENT);
    }

    /**
     * Ends session for Content Service client and remove initial data
     */
    public void release() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Releasing client...");
        }
        CML cml = new CML();
        cml.setDelete(new CMLDelete[] { new CMLDelete(new Predicate(new Reference[] { contentRef }, store, null)),
                new CMLDelete(new Predicate(new Reference[] { transformContentRef }, store, null)) });
        getRepositoryService(getServerUrl() + repositoryService.getPath()).update(cml);

        endSession();
    }

    /**
     * Main method to start client
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args)
    {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:wsi-context.xml");
        AbstractServiceClient client = (OriginalContentServiceClient) applicationContext.getBean("originalContentServiceClient");
        try
        {
            client.initialize();
            client.invoke();
            client.release();
        }
        catch (Exception e)
        {
            LOGGER.error("Some error occured during client running. Exception message: " + e.getMessage());
        }
    }

    /**
     * Gets stub for Content Service
     * 
     * @param address address where service resides
     * @return ContentServiceSoapBindingStub
     * @throws ServiceException
     */
    private ContentServiceSoapBindingStub getContentService(String address) throws ServiceException
    {
        ContentServiceSoapBindingStub contentService = null;
        ContentServiceLocator locator = new ContentServiceLocator(getEngineConfiguration());
        locator.setContentServiceEndpointAddress(address);
        contentService = (ContentServiceSoapBindingStub) locator.getContentService();
        contentService.setMaintainSession(true);
        contentService.setTimeout(TIMEOUT);
        return contentService;
    }

    /**
     * Gets stub for Repository Service
     * 
     * @param address address where service resides
     * @return RepositoryServiceSoapBindingStub
     * @throws ServiceException
     */
    private RepositoryServiceSoapBindingStub getRepositoryService(String address) throws ServiceException
    {
        RepositoryServiceSoapBindingStub repositoryService = null;
        RepositoryServiceLocator locator = new RepositoryServiceLocator(getEngineConfiguration());
        locator.setRepositoryServiceEndpointAddress(address);
        repositoryService = (RepositoryServiceSoapBindingStub) locator.getRepositoryService();
        repositoryService.setMaintainSession(true);
        repositoryService.setTimeout(TIMEOUT);
        return repositoryService;
    }

    // /**
    //  * Copies data from InputStream to OutputStream
    //  * 
    //  * @param in InputStream to copy from
    //  * @param out OutputStream to copy to
    //  * @return bytes copied
    //  * @throws IOException
    //  */
    // private int copy(InputStream in, OutputStream out) throws IOException
    // {
    //     try
    //     {
    //         int byteCount = 0;
    //         byte[] buffer = new byte[4096];
    //         int bytesRead = -1;
    //         while ((bytesRead = in.read(buffer)) != -1)
    //         {
    //             out.write(buffer, 0, bytesRead);
    //             byteCount += bytesRead;
    //         }
    //         out.flush();
    //         return byteCount;
    //     }
    //     finally
    //     {
    //         try
    //         {
    //             in.close();
    //         }
    //         catch (IOException ex)
    //         {
    //             // Could not close input stream
    //         }
    //         try
    //         {
    //             out.close();
    //         }
    //         catch (IOException ex)
    //         {
    //             // Could not close output stream
    //         }
    //     }
    // }

    /**
     * Converts InputStream to array of byte
     * 
     * @param inputStream InputStream to convert
     * @return array of byte
     * @throws Exception
     */
    private byte[] convertToByteArray(InputStream inputStream) throws Exception
    {
        byte[] result = null;

        if (inputStream.available() > 0)
        {
            result = new byte[inputStream.available()];
            inputStream.read(result);
        }

        return result;
    }
}
