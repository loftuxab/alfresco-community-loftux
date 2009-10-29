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
package org.alfresco.cmis.test.ws;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.Arrays;

import org.alfresco.repo.cmis.ws.AddObjectToFolder;
import org.alfresco.repo.cmis.ws.CheckOut;
import org.alfresco.repo.cmis.ws.CmisContentStreamType;
import org.alfresco.repo.cmis.ws.CmisFaultType;
import org.alfresco.repo.cmis.ws.CmisPropertiesType;
import org.alfresco.repo.cmis.ws.CmisPropertyId;
import org.alfresco.repo.cmis.ws.CmisPropertyString;
import org.alfresco.repo.cmis.ws.CmisTypeDocumentDefinitionType;
import org.alfresco.repo.cmis.ws.CreateDocument;
import org.alfresco.repo.cmis.ws.CreateFolder;
import org.alfresco.repo.cmis.ws.DeleteContentStream;
import org.alfresco.repo.cmis.ws.DeleteObject;
import org.alfresco.repo.cmis.ws.DeleteTree;
import org.alfresco.repo.cmis.ws.DeleteTreeResponse;
import org.alfresco.repo.cmis.ws.EnumContentStreamAllowed;
import org.alfresco.repo.cmis.ws.EnumIncludeRelationships;
import org.alfresco.repo.cmis.ws.EnumServiceException;
import org.alfresco.repo.cmis.ws.EnumUnfileObject;
import org.alfresco.repo.cmis.ws.EnumVersioningState;
import org.alfresco.repo.cmis.ws.GetAllowableActions;
import org.alfresco.repo.cmis.ws.GetAllowableActionsResponse;
import org.alfresco.repo.cmis.ws.GetContentStream;
import org.alfresco.repo.cmis.ws.GetContentStreamResponse;
import org.alfresco.repo.cmis.ws.GetFolderByPath;
import org.alfresco.repo.cmis.ws.GetFolderByPathResponse;
import org.alfresco.repo.cmis.ws.GetProperties;
import org.alfresco.repo.cmis.ws.GetPropertiesResponse;
import org.alfresco.repo.cmis.ws.GetTypeDefinition;
import org.alfresco.repo.cmis.ws.GetTypeDefinitionResponse;
import org.alfresco.repo.cmis.ws.MoveObject;
import org.alfresco.repo.cmis.ws.ObjectServicePortBindingStub;
import org.alfresco.repo.cmis.ws.SetContentStream;
import org.alfresco.repo.cmis.ws.UpdateProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

/**
 * Client for Object Service
 */
public class CmisObjectServiceClient extends AbstractServiceClient
{
    private static Log LOGGER = LogFactory.getLog(CmisObjectServiceClient.class);

    private static final String UPDATE_FILE_NAME = "UpdatedFileName.txt";

    private static final String TEST_IMAGE_NAME = "TestImage.jpg";
    private static final String MIMETYPE_IMAGE_JPEG = "image/jpeg";

    private Resource imageResource;

    public CmisObjectServiceClient()
    {
    }

    public CmisObjectServiceClient(AbstractService abstractService)
    {
        super(abstractService);
    }

    public void setImageResource(Resource imageResource)
    {
        this.imageResource = imageResource;
    }

    /**
     * Initializes Object Service client
     */
    public void initialize() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Initializing client...");
        }
    }
    
    /**
     * Invokes all methods in Object Service
     */
    public void invoke() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Invoking client...");
        }

        ObjectServicePortBindingStub objectServicePort = getServicesFactory().getObjectService(getProxyUrl() + getService().getPath());

        CmisPropertiesType properties = new CmisPropertiesType();
        CmisPropertyString cmisPropertyName = new CmisPropertyString();
        cmisPropertyName.setPdid(PROP_NAME);
        cmisPropertyName.setValue(new String[] { System.currentTimeMillis() + TEST_FILE_NAME });
        CmisPropertyId idProperty = new CmisPropertyId();
        idProperty.setPdid(PROP_OBJECT_TYPE_ID);
        idProperty.setValue(new String[] {getAndAssertDocumentTypeId()});
        properties.setPropertyString(new CmisPropertyString[] { cmisPropertyName });
        properties.setPropertyId(new CmisPropertyId[] { idProperty });
        CmisContentStreamType cmisStream = new CmisContentStreamType();
        cmisStream.setFilename(System.currentTimeMillis() + TEST_FILE_NAME);
        cmisStream.setMimeType(MIMETYPE_TEXT_PLAIN);
        cmisStream.setStream(TEST_CONTENT.getBytes(ENCODING));
        CreateDocument createDocumentParameters = new CreateDocument(getAndAssertRepositoryId(), properties, getAndAssertRootFolderId(), cmisStream, EnumVersioningState
                .fromString(EnumVersioningState._major), null, null, null);
        String documentId = objectServicePort.createDocument(createDocumentParameters).getObjectId();

        properties = new CmisPropertiesType();
        cmisPropertyName = new CmisPropertyString();
        cmisPropertyName.setPdid(PROP_NAME);
        cmisPropertyName.setValue(new String[] { System.currentTimeMillis() + TEST_FOLDER_NAME });
        idProperty = new CmisPropertyId();
        idProperty.setPdid(PROP_OBJECT_TYPE_ID);
        idProperty.setValue(new String[] {getAndAssertFolderTypeId()});
        properties.setPropertyString(new CmisPropertyString[] { cmisPropertyName });
        properties.setPropertyId(new CmisPropertyId[] { idProperty });
        CreateFolder createFolderParameters = new CreateFolder(getAndAssertRepositoryId(), properties, getAndAssertRootFolderId(), null, null, null);
        String folderId = objectServicePort.createFolder(createFolderParameters).getObjectId();

        GetAllowableActions getAllowableActionsParameters = new GetAllowableActions(getAndAssertRepositoryId(), folderId);
        objectServicePort.getAllowableActions(getAllowableActionsParameters);

        objectServicePort.getProperties(new GetProperties(getAndAssertRepositoryId(), documentId, null, null, null, null));

        properties = new CmisPropertiesType();
        cmisPropertyName = new CmisPropertyString();
        cmisPropertyName.setPdid(PROP_NAME);
        cmisPropertyName.setValue(new String[] { UPDATE_FILE_NAME });
        properties.setPropertyString(new CmisPropertyString[] { cmisPropertyName });
        UpdateProperties updatePropertiesParameters = new UpdateProperties(getAndAssertRepositoryId(), documentId, "", properties);
        documentId = objectServicePort.updateProperties(updatePropertiesParameters).getObjectId();

        objectServicePort.getContentStream(new GetContentStream(getAndAssertRepositoryId(), documentId, ""));

        MoveObject moveObjectParameters = new MoveObject(getAndAssertRepositoryId(), documentId, folderId, getAndAssertRootFolderId());
        objectServicePort.moveObject(moveObjectParameters);

        CmisContentStreamType contentStream = new CmisContentStreamType();
        contentStream.setFilename(TEST_IMAGE_NAME);
        contentStream.setMimeType(MIMETYPE_IMAGE_JPEG);
        InputStream viewStream = imageResource.getInputStream();
        byte[] streamBytes = new byte[viewStream.available()];
        viewStream.read(streamBytes);
        contentStream.setStream(streamBytes);
        SetContentStream setContentStreamParameters = new SetContentStream(getAndAssertRepositoryId(), documentId, true, "", contentStream);
        documentId = objectServicePort.setContentStream(setContentStreamParameters).getDocumentId();

        // TODO WSDL does not correspond to specification
        // objectServicePort.deleteContentStream(new DeleteContentStream(getAndAssertRepositoryId(), documentId));

        objectServicePort.deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, null));

        objectServicePort.deleteTree(new DeleteTree(getAndAssertRepositoryId(), folderId, EnumUnfileObject.fromString(EnumUnfileObject._delete), true));
    }

    @Override
    public void release() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Releasing client...");
        }
    }

    /**
     * Main method to start client
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:cmis-context.xml");
        AbstractServiceClient client = (CmisObjectServiceClient) applicationContext.getBean("cmisObjectServiceClient");
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

    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();
    }

    @Override
    protected void onTearDown() throws Exception
    {
        super.onTearDown();
    }

    public void testCreateDocumentDefault() throws Exception
    {

        String documentName = System.currentTimeMillis() + TEST_FILE_NAME;
        String documentId = null;
        CmisPropertiesType properties = fillProperties(documentName, getAndAssertDocumentTypeId());
        GetTypeDefinitionResponse getTypeDefinitionResponse = getServicesFactory().getRepositoryService().getTypeDefinition(
                new GetTypeDefinition(getAndAssertRepositoryId(), getAndAssertDocumentTypeId()));
        EnumContentStreamAllowed contentStreamAllowed = ((CmisTypeDocumentDefinitionType) getTypeDefinitionResponse.getType()).getContentStreamAllowed();
        try
        {
            if (getAndAssertCapabilities().isCapabilityUnfiling())
            {
                if (EnumContentStreamAllowed.notallowed.equals(contentStreamAllowed))
                {
                    documentId = getServicesFactory().getObjectService().createDocument(
                            new CreateDocument(getAndAssertRepositoryId(), properties, null, null, null, null, null, null)).getObjectId();
                }
                else
                {
                    documentId = getServicesFactory().getObjectService().createDocument(
                            new CreateDocument(getAndAssertRepositoryId(), properties, null, new CmisContentStreamType(BigInteger.valueOf(TEST_CONTENT.length()),
                                    MIMETYPE_TEXT_PLAIN, documentName, null, TEST_CONTENT.getBytes(ENCODING), null), null, null, null, null)).getObjectId();
                }

            }
            else
            {
                if (EnumContentStreamAllowed.notallowed.equals(contentStreamAllowed))
                {
                    documentId = getServicesFactory().getObjectService().createDocument(
                            new CreateDocument(getAndAssertRepositoryId(), properties, getAndAssertRootFolderId(), null, null, null, null, null)).getObjectId();
                }
                else
                {
                    documentId = getServicesFactory().getObjectService().createDocument(
                            new CreateDocument(getAndAssertRepositoryId(), properties, getAndAssertRootFolderId(), new CmisContentStreamType(BigInteger.valueOf(TEST_CONTENT
                                    .length()), MIMETYPE_TEXT_PLAIN, documentName, null, TEST_CONTENT.getBytes(ENCODING), null), null, null, null, null)).getObjectId();
                }
            }
            assertNotNull("Document was not created", documentId);
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
        finally
        {
            if (documentId != null)
            {
                getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, null));
            }
        }
    }

    public void testCreateDocumentVersioningState() throws Exception
    {
        String documentId = null;
        try
        {
            // TODO works not correct in Alfresco
            documentId = createAndAssertDocument(System.currentTimeMillis() + TEST_FILE_NAME, getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), null,
                    EnumVersioningState.checkedout);
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }

        assertNotNull("Document was not created", documentId);
        assertTrue("Document is not checked out", getBooleanProperty(documentId, "cmis:IsVersionSeriesCheckedOut"));

        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, null));

        try
        {
            documentId = createAndAssertDocument(System.currentTimeMillis() + TEST_FILE_NAME, getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), null,
                    EnumVersioningState.major);
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
        assertNotNull("Document was not created", documentId);
        assertTrue("Document version is not Major", getBooleanProperty(documentId, "cmis:IsMajorVersion"));

        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, null));

        try
        {
            documentId = createAndAssertDocument(System.currentTimeMillis() + TEST_FILE_NAME, getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), null,
                    EnumVersioningState.minor);
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
        assertNotNull("Document was not created", documentId);
        assertFalse("Document version is not Minor", getBooleanProperty(documentId, "cmis:IsMajorVersion"));

        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, null));
    }

    public void testCreateFolder() throws Exception
    {
        String folderId = null;
        CmisPropertiesType properties = fillProperties(System.currentTimeMillis() + TEST_FOLDER_NAME, getAndAssertFolderTypeId());
        try
        {
            folderId = getServicesFactory().getObjectService().createFolder(new CreateFolder(getAndAssertRepositoryId(), properties, getAndAssertRootFolderId(), null, null, null))
                    .getObjectId();
            assertNotNull("Folder was not created", folderId);
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), folderId, null));
    }

    public void testCreateRelationship() throws Exception
    {
        String relationshipId = null;
        try
        {
            relationshipId = createAndAssertRelationship(null, null);
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), relationshipId, null));
    }

    public void testGetAllowableActions() throws Exception
    {
        GetAllowableActionsResponse response = null;
        try
        {
            response = getServicesFactory().getObjectService().getAllowableActions(new GetAllowableActions(getAndAssertRepositoryId(), getAndAssertRootFolderId()));
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
        assertNotNull("No allowable actions were returned", response);
        assertNotNull("Action 'getProperties' not defined for an object", response.getAllowableActions().getCanGetProperties());
    }

    public void testGetPropertiesDefault() throws Exception
    {
        GetPropertiesResponse response = null;
        try
        {
            response = getServicesFactory().getObjectService().getProperties(new GetProperties(getAndAssertRepositoryId(), getAndAssertRootFolderId(), null, null, null, null));
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
        assertTrue("No properties were returned", response != null && response.getObject() != null && response.getObject().getProperties() != null);
        assertNotNull("No 'Name' property was returned", getStringProperty(response.getObject().getProperties(), PROP_NAME));
    }
    
    public void testGetPropertiesFiltered() throws Exception
    {
        GetPropertiesResponse response = null;
        try
        {
            String filter = PROP_NAME + ", " + PROP_OBJECT_ID;
            response = getServicesFactory().getObjectService().getProperties(new GetProperties(getAndAssertRepositoryId(), getAndAssertRootFolderId(), filter, null, null, null));
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
        assertTrue("No properties were returned", response != null && response.getObject() != null && response.getObject().getProperties() != null);

        CmisPropertiesType properties = response.getObject().getProperties();

        assertNull(properties.getPropertyBoolean());
        assertNull(properties.getPropertyDecimal());
        assertNull(properties.getPropertyHtml());
        assertNull(properties.getPropertyInteger());
        assertNull(properties.getPropertyUri());
        assertNull(properties.getPropertyXml());
        assertNull(properties.getPropertyDateTime());

        assertNotNull(properties.getPropertyId());
        assertNotNull(properties.getPropertyString());

        assertEquals(1, properties.getPropertyId().length);
        assertEquals(1, properties.getPropertyString().length);

        assertNotNull(getIdProperty(properties, PROP_OBJECT_ID));
        assertNotNull(getStringProperty(properties, PROP_NAME));
    }
    
    public void testGetPropertiesIncludeAllowableActionsAndRelationships() throws Exception
    {
        GetPropertiesResponse response = null;
        
        try
        {
            response = getServicesFactory().getObjectService().getProperties(new GetProperties(getAndAssertRepositoryId(), getAndAssertRootFolderId(), null, true, null, null));
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
        assertTrue("No properties were returned", response != null && response.getObject() != null && response.getObject().getProperties() != null);
        assertNotNull("No allowable actions were returned", response.getObject().getAllowableActions());
        assertNotNull("No action 'getProperties' was returned", response.getObject().getAllowableActions().getCanGetProperties());
        
        String relationshipId = createAndAssertRelationship(null, null);
        try
        {
            response = getServicesFactory().getObjectService().getProperties(
                    new GetProperties(getAndAssertRepositoryId(), getAndAssertRootFolderId(), null, null, EnumIncludeRelationships.both, null));
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
        assertTrue("No properties were returned", response != null && response.getObject() != null && response.getObject().getProperties() != null);
        assertTrue("No Relationships were returned", response.getObject().getRelationship() != null && response.getObject().getRelationship().length >= 1);
     
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), relationshipId, null));
    }
    
    public void testGetFolderByPath() throws Exception
    {
        String folder1Id = null;
        String folder2Id = null;
        try
        {
            String folder1Name = System.currentTimeMillis() + TEST_FOLDER_NAME + "_1";
            String folder2Name = System.currentTimeMillis() + TEST_FOLDER_NAME + "_2";
            folder1Id = createAndAssertFolder(folder1Name, getAndAssertFolderTypeId(), getAndAssertRootFolderId());
            folder2Id = createAndAssertFolder(folder2Name, getAndAssertFolderTypeId(), folder1Id);
            assertNotNull("Folder was not created", folder1Id);
            assertNotNull("Folder was not created", folder2Id);
            
            String pathToFolder1 = "/" + folder1Name;
            String pathToFolder2 = "/" + folder1Name + "/" + folder2Name;
            
            GetFolderByPathResponse response = getServicesFactory().getObjectService().getFolderByPath(
                    new GetFolderByPath(getAndAssertRepositoryId(), pathToFolder1, "*", false, null, false));
            assertTrue("Folder was not found", response != null && response.getObject() != null && response.getObject().getProperties() != null);
            assertEquals(folder1Id, getIdProperty(response.getObject().getProperties(), PROP_OBJECT_ID));
            
            response = getServicesFactory().getObjectService().getFolderByPath(
                    new GetFolderByPath(getAndAssertRepositoryId(), pathToFolder2, "*", false, null, false));
            assertTrue("Folder was not found", response != null && response.getObject() != null && response.getObject().getProperties() != null);
            assertEquals(folder2Id, getIdProperty(response.getObject().getProperties(), PROP_OBJECT_ID));
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), folder2Id, null));
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), folder1Id, null));
    }

    public void testGetContentStream() throws Exception
    {
        if (isContentStreamAllowed())
        {
            GetContentStreamResponse response = null;
            String documentId = createAndAssertDocument(System.currentTimeMillis() + TEST_FILE_NAME, getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), TEST_CONTENT,
                    EnumVersioningState.major);
            try
            {
                response = getServicesFactory().getObjectService().getContentStream(new GetContentStream(getAndAssertRepositoryId(), documentId, ""));
            }
            catch (Exception e)
            {
                getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, null));
                fail(e.getMessage());
            }
            assertTrue("No content stream was returned", response != null && response.getContentStream() != null);
            assertTrue("Invalid content stream was returned", Arrays.equals(TEST_CONTENT.getBytes(), response.getContentStream().getStream()));
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, null));
        }
        else
        {
            LOGGER.info("testGetContentStream was skipped: Content stream isn't allowed");
        }
    }

    public void testUpdatePropertiesDefault() throws Exception
    {
        String documentName = System.currentTimeMillis() + TEST_FILE_NAME;
        String documentId = createAndAssertDocument(documentName, getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), null, null);

        String documentNameNew = System.currentTimeMillis() + TEST_FILE_NAME + "_new";
        try
        {
            CmisPropertiesType properties = fillProperties(documentNameNew, null);
            documentId = getServicesFactory().getObjectService().updateProperties(new UpdateProperties(getAndAssertRepositoryId(), documentId, null, properties)).getObjectId();
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }

        assertEquals("Properties was not updated", documentNameNew, getStringProperty(documentId, PROP_NAME));

        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, null));
    }

    public void testMoveObjectDefault() throws Exception
    {
        String documentName = System.currentTimeMillis() + TEST_FILE_NAME;
        String documentId = createAndAssertDocument(documentName, getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), null, EnumVersioningState.major);
        String folderId = createAndAssertFolder(System.currentTimeMillis() + TEST_FOLDER_NAME, getAndAssertFolderTypeId(), getAndAssertRootFolderId());
        try
        {
            getServicesFactory().getObjectService().moveObject(new MoveObject(getAndAssertRepositoryId(), documentId, folderId, getAndAssertRootFolderId()));
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }

        assertFalse("Object was not removed from source folder", isDocumentInFolder(documentId, getAndAssertRootFolderId()));
        assertTrue("Object was not added to target folder", isDocumentInFolder(documentId, folderId));

        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, null));
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), folderId, null));
    }

    public void testMoveObjectUnfiled() throws Exception
    {
        String folderId = createAndAssertFolder(System.currentTimeMillis() + TEST_FOLDER_NAME, getAndAssertFolderTypeId(), getAndAssertRootFolderId());
        if (getAndAssertCapabilities().isCapabilityUnfiling())
        {
            String documentId = createAndAssertDocument(System.currentTimeMillis() + TEST_FILE_NAME, getAndAssertDocumentTypeId(), null, null, EnumVersioningState.major);
            try
            {
                getServicesFactory().getObjectService().moveObject(new MoveObject(getAndAssertRepositoryId(), documentId, folderId, null));
                fail("No Exception was thrown");
            }
            catch (Exception e)
            {
                assertTrue(e instanceof CmisFaultType && ((CmisFaultType) e).getType().equals(EnumServiceException.notSupported));
            }
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, null));
        }
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), folderId, null));
    }

    public void testMoveObjectMultiFiled() throws Exception
    {
        if (getAndAssertCapabilities().isCapabilityMultifiling())
        {
            String documentId = createAndAssertDocument(System.currentTimeMillis() + TEST_FILE_NAME, getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), null,
                    EnumVersioningState.major);
            String folderId = createAndAssertFolder(System.currentTimeMillis() + TEST_FOLDER_NAME, getAndAssertFolderTypeId(), getAndAssertRootFolderId());
            String folder2Id = createAndAssertFolder(System.currentTimeMillis() + TEST_FOLDER_NAME, null, folderId);
            getServicesFactory().getMultiFilingServicePort().addObjectToFolder(new AddObjectToFolder(getAndAssertRepositoryId(), documentId, folderId));

            try
            {
                getServicesFactory().getObjectService().moveObject(new MoveObject(getAndAssertRepositoryId(), documentId, folder2Id, folderId));
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }

            assertFalse("Object was not removed from source folder", isDocumentInFolder(documentId, folderId));
            assertTrue("Object was removed from not source folder", isDocumentInFolder(documentId, getAndAssertRootFolderId()));
            assertTrue("Object was not added to target folder", isDocumentInFolder(documentId, folder2Id));

            getServicesFactory().getObjectService().deleteTree(new DeleteTree(getAndAssertRepositoryId(), folderId, EnumUnfileObject.delete, true));
        }
    }

    public void testDeleteObject() throws Exception
    {
        String documentId = createAndAssertDocument(System.currentTimeMillis() + TEST_FILE_NAME, getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), null,
                EnumVersioningState.major);
        try
        {
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, true));
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }

        assertFalse("Object was not removed", isDocumentInFolder(documentId, getAndAssertRootFolderId()));
    }

    public void testDeleteFolderWithChild() throws Exception
    {
        String folderId = createAndAssertFolder(System.currentTimeMillis() + TEST_FOLDER_NAME, getAndAssertFolderTypeId(), getAndAssertRootFolderId());
        String folder2Id = createAndAssertFolder(System.currentTimeMillis() + TEST_FOLDER_NAME + "1", getAndAssertFolderTypeId(), folderId);
        try
        {
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), folderId, null));
            fail("No Exception was thrown");
        }
        catch (Exception e)
        {
            assertTrue(e instanceof CmisFaultType && ((CmisFaultType) e).getType().equals(EnumServiceException.constraint));
        }
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), folder2Id, null));
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), folderId, null));
    }

    public void testDeleteMultiFiledObject() throws Exception
    {
        if (getAndAssertCapabilities().isCapabilityMultifiling())
        {
            String documentId = createAndAssertDocument(System.currentTimeMillis() + TEST_FILE_NAME, getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), null,
                    EnumVersioningState.major);
            String folderId = createAndAssertFolder(System.currentTimeMillis() + TEST_FOLDER_NAME, getAndAssertFolderTypeId(), getAndAssertRootFolderId());
            getServicesFactory().getMultiFilingServicePort().addObjectToFolder(new AddObjectToFolder(getAndAssertRepositoryId(), documentId, folderId));
            try
            {
                // TODO works not correct in Alfresco
                getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, null));
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
            assertFalse("Object was not removed", isDocumentInFolder(documentId, getAndAssertRootFolderId()));
            assertFalse("Object was not removed", isDocumentInFolder(documentId, folderId));

            getServicesFactory().getObjectService().deleteTree(new DeleteTree(getAndAssertRepositoryId(), folderId, EnumUnfileObject.delete, true));
        }
    }

    public void testDeletePWC() throws Exception
    {
        if (isVersioningAllowed())
        {
            String documentId = createAndAssertDocument(System.currentTimeMillis() + TEST_FILE_NAME, getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), null,
                    EnumVersioningState.major);
            String checkedOutId = getServicesFactory().getVersioningService().checkOut(new CheckOut(getAndAssertRepositoryId(), documentId)).getDocumentId();
            try
            {
                getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), checkedOutId, null));
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
            assertFalse("Private working copy was not deleted", getBooleanProperty(documentId, "cmis:IsVersionSeriesCheckedOut"));
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, null));
        }
        else
        {
            LOGGER.info("testDeletePWC was skipped: Versioning isn't supported");
        }
    }

    public void testDeleteTreeDefault() throws Exception
    {
        DeleteTreeResponse response = null;
        String folderId = createAndAssertFolder(System.currentTimeMillis() + TEST_FOLDER_NAME, getAndAssertFolderTypeId(), getAndAssertRootFolderId());
        createAndAssertDocument(System.currentTimeMillis() + TEST_FILE_NAME, getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), null, EnumVersioningState.major);
        createAndAssertFolder(System.currentTimeMillis() + TEST_FOLDER_NAME, getAndAssertFolderTypeId(), folderId);
        try
        {
            response = getServicesFactory().getObjectService().deleteTree(new DeleteTree(getAndAssertRepositoryId(), folderId, EnumUnfileObject.delete, false));
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }

        assertTrue("Objects tree was not deleted", response == null || response.getFailedToDelete() == null 
                || response.getFailedToDelete().length == 0);
    }

    public void testDeleteTreeUnfileNonfolderObjects() throws Exception
    {
        if (getAndAssertCapabilities().isCapabilityMultifiling())
        {
            DeleteTreeResponse response = null;
            String folderId = createAndAssertFolder(System.currentTimeMillis() + TEST_FOLDER_NAME, getAndAssertFolderTypeId(), getAndAssertRootFolderId());
            String documentId = createAndAssertDocument(System.currentTimeMillis() + TEST_FILE_NAME, getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), null,
                    EnumVersioningState.major);
            String folder2Id = createAndAssertFolder(System.currentTimeMillis() + TEST_FOLDER_NAME, getAndAssertFolderTypeId(), folderId);
            String document2Id = createAndAssertDocument(System.currentTimeMillis() + TEST_FILE_NAME, getAndAssertDocumentTypeId(), folderId, null, EnumVersioningState.major);
            getServicesFactory().getMultiFilingServicePort().addObjectToFolder(new AddObjectToFolder(getAndAssertRepositoryId(), documentId, folderId));
            getServicesFactory().getMultiFilingServicePort().addObjectToFolder(new AddObjectToFolder(getAndAssertRepositoryId(), document2Id, folder2Id));
            try
            {
                response = getServicesFactory().getObjectService().deleteTree(new DeleteTree(getAndAssertRepositoryId(), folderId, EnumUnfileObject.delete, false));
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
            assertTrue("Objects tree was not deleted", response == null || response.getFailedToDelete() == null || response.getFailedToDelete().length == 0);
            assertFalse("Multifiled document was not removed", isDocumentInFolder(documentId, getAndAssertRootFolderId()));
        }
    }

    public void testDeleteTreeRootFolder()
    {
        try
        {
            getServicesFactory().getObjectService().deleteTree(new DeleteTree(getAndAssertRepositoryId(), getAndAssertRootFolderId(), EnumUnfileObject.delete, true));
            fail("No Exception was thrown");
        }
        catch (Exception e)
        {
            assertTrue(e instanceof CmisFaultType && ((CmisFaultType) e).getType().equals(EnumServiceException.notSupported));
        }
    }

    public void testSetContentStream() throws Exception
    {
        if (isContentStreamAllowed())
        {
            String documentName = System.currentTimeMillis() + TEST_FILE_NAME;
            String documentId = createAndAssertDocument(documentName, getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), TEST_CONTENT, EnumVersioningState.major);
            String newTestCOntent = TEST_CONTENT + System.currentTimeMillis();
            try
            {
                documentId = getServicesFactory().getObjectService().setContentStream(
                        new SetContentStream(getAndAssertRepositoryId(), documentId, true, null, new CmisContentStreamType(BigInteger.valueOf(newTestCOntent.length()),
                                MIMETYPE_TEXT_PLAIN, documentName, null, newTestCOntent.getBytes(ENCODING), null))).getDocumentId();
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
            // TODO uncomment
            // assertTrue("Content stream was not updated", Arrays.equals(newTestCOntent.getBytes(), getServicesFactory().getObjectService().getContentStream(
            // new GetContentStream(getAndAssertRepositoryId(), documentId)).getContentStream().getStream()));
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, null));
        }
        else
        {
            LOGGER.info("testSetContentStream was skipped: Content stream isn't allowed");
        }
    }

    public void testSetContentStreamOverwriteFlag() throws Exception
    {
        if (isContentStreamAllowed())
        {
            String documentName = System.currentTimeMillis() + TEST_FILE_NAME;
            String documentId = createAndAssertDocument(System.currentTimeMillis() + TEST_FILE_NAME, getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), TEST_CONTENT,
                    EnumVersioningState.major);
            String newTestCOntent = TEST_CONTENT + System.currentTimeMillis();
            try
            {
                documentId = getServicesFactory().getObjectService().setContentStream(
                        new SetContentStream(getAndAssertRepositoryId(), documentId, false, null, new CmisContentStreamType(BigInteger.valueOf(newTestCOntent.length()),
                                MIMETYPE_TEXT_PLAIN, documentName, null, newTestCOntent.getBytes(ENCODING), null))).getDocumentId();
                fail("No Exception was thrown");
            }
            catch (Exception e)
            {
                assertTrue(e instanceof CmisFaultType && ((CmisFaultType) e).getType().equals(EnumServiceException.contentAlreadyExists));
            }
            try
            {
                documentId = getServicesFactory().getObjectService().setContentStream(
                        new SetContentStream(getAndAssertRepositoryId(), documentId, true, null, new CmisContentStreamType(BigInteger.valueOf(newTestCOntent.length()),
                                MIMETYPE_TEXT_PLAIN, documentName, null, newTestCOntent.getBytes(ENCODING), null))).getDocumentId();
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }

            // TODO uncomment
            // assertTrue("Content stream was not updated", Arrays.equals(newTestCOntent.getBytes(), getServicesFactory().getObjectService().getContentStream(
            // new GetContentStream(getAndAssertRepositoryId(), documentId)).getContentStream().getStream()));
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, null));
        }
        else
        {
            LOGGER.info("testSetContentStreamOverwriteFlag was skipped: Content stream isn't allowed");
        }
    }

    public void testDeleteContentStream() throws Exception
    {
        if (isContentStreamAllowed())
        {
            String documentId = createAndAssertDocument(System.currentTimeMillis() + TEST_FILE_NAME, getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), TEST_CONTENT, null);
            try
            {
                getServicesFactory().getObjectService().deleteContentStream(new DeleteContentStream(getAndAssertRepositoryId(), documentId, null));
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
            try
            {
                // GetContentStreamResponse contentStreamResponse = getServicesFactory().getObjectService().getContentStream(
                // new GetContentStream(getAndAssertRepositoryId(), documentId));
                // TODO uncomment
                // assertTrue("Content stream was not deleted", contentStreamResponse == null || contentStreamResponse.getContentStream() == null
                // || contentStreamResponse.getContentStream().getStream() == null);
            }
            catch (Exception e)
            {
                assertTrue(e instanceof CmisFaultType);
            }
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, null));
        }
        else
        {
            LOGGER.info("testDeleteContentStream was skipped: Content stream isn't allowed");
        }
    }
}
