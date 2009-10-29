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
import org.alfresco.repo.cmis.ws.CmisPropertyStringDefinitionType;
import org.alfresco.repo.cmis.ws.CmisRenditionType;
import org.alfresco.repo.cmis.ws.CmisRepositoryCapabilitiesType;
import org.alfresco.repo.cmis.ws.CmisTypeDefinitionType;
import org.alfresco.repo.cmis.ws.CreateDocument;
import org.alfresco.repo.cmis.ws.CreateFolder;
import org.alfresco.repo.cmis.ws.DeleteContentStream;
import org.alfresco.repo.cmis.ws.DeleteObject;
import org.alfresco.repo.cmis.ws.DeleteTree;
import org.alfresco.repo.cmis.ws.DeleteTreeResponse;
import org.alfresco.repo.cmis.ws.EnumCapabilityRendition;
import org.alfresco.repo.cmis.ws.EnumIncludeRelationships;
import org.alfresco.repo.cmis.ws.EnumPropertiesBase;
import org.alfresco.repo.cmis.ws.EnumPropertiesDocument;
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
import org.alfresco.repo.cmis.ws.GetRenditions;
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

    private static final String PROPERTIES_NOT_RETURNED_MESSAGE = "Properties were not returned";
    private static final String CHECKEDOUT_WITHOUT_REQUEST_MESSAGE = "Document was Checked Out without appropriate 'CHECKEDOUT' Versioning State attribute";

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
        cmisPropertyName.setPdid(EnumPropertiesBase._value1);
        cmisPropertyName.setValue(new String[] { generateTestFileName() });
        CmisPropertyId idProperty = new CmisPropertyId();
        idProperty.setPdid(EnumPropertiesBase._value3);
        idProperty.setValue(new String[] { getAndAssertDocumentTypeId() });
        properties.setPropertyString(new CmisPropertyString[] { cmisPropertyName });
        properties.setPropertyId(new CmisPropertyId[] { idProperty });
        CmisContentStreamType cmisStream = new CmisContentStreamType();
        cmisStream.setFilename(generateTestFileName());
        cmisStream.setMimeType(MIMETYPE_TEXT_PLAIN);
        cmisStream.setStream(TEST_CONTENT.getBytes(ENCODING));
        CreateDocument createDocumentParameters = new CreateDocument(getAndAssertRepositoryId(), properties, getAndAssertRootFolderId(), cmisStream, EnumVersioningState
                .fromString(EnumVersioningState._major), null, null, null);
        String documentId = objectServicePort.createDocument(createDocumentParameters).getObjectId();

        properties = new CmisPropertiesType();
        cmisPropertyName = new CmisPropertyString();
        cmisPropertyName.setPdid(EnumPropertiesBase._value1);
        cmisPropertyName.setValue(new String[] { generateTestFolderName() });
        idProperty = new CmisPropertyId();
        idProperty.setPdid(EnumPropertiesBase._value3);
        idProperty.setValue(new String[] { getAndAssertFolderTypeId() });
        properties.setPropertyString(new CmisPropertyString[] { cmisPropertyName });
        properties.setPropertyId(new CmisPropertyId[] { idProperty });
        CreateFolder createFolderParameters = new CreateFolder(getAndAssertRepositoryId(), properties, getAndAssertRootFolderId(), null, null, null);
        String folderId = objectServicePort.createFolder(createFolderParameters).getObjectId();

        GetAllowableActions getAllowableActionsParameters = new GetAllowableActions(getAndAssertRepositoryId(), folderId);
        objectServicePort.getAllowableActions(getAllowableActionsParameters);

        objectServicePort.getProperties(new GetProperties(getAndAssertRepositoryId(), documentId, null, null, null, null));

        properties = new CmisPropertiesType();
        cmisPropertyName = new CmisPropertyString();
        cmisPropertyName.setPdid(EnumPropertiesBase._value1);
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

        deleteAndAssertObject(documentId);

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
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:wsi-context.xml");
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

    public void testDocumentCreation() throws Exception
    {
        String documentId = createAndAssertDocument();
        deleteAndAssertObject(documentId);
    }

    public void testDocumentCreationConstrainsObservance() throws Exception
    {
        String rootFolderId = getAndAssertRootFolderId();
        String documentTypeId = getAndAssertDocumentTypeId();
        if (!isContentStreamAllowed())
        {
            assertDocumentConstraitException("Creating Document with Content Stream when Content Stream is 'not allowed'", EnumServiceException.streamNotSupported,
                    generateTestFileName(), documentTypeId, rootFolderId, TEST_CONTENT, null, true);
        }
        assertDocumentConstraitException("Creating Document with 'none document' Type Id", generateTestFileName(), getAndAssertFolderTypeId(), rootFolderId, TEST_CONTENT, null);
        assertNotAllowedObjectException(rootFolderId, true);
        if (isContentStreamRequired())
        {
            assertDocumentConstraitException("Creating Document with 'required' Content Stream Type without Content Stream input parameter", generateTestFileName(),
                    documentTypeId, rootFolderId, null, null);
        }
        String constrainedDocumentTypeId = searchAndAssertNotVersionableDocumentType();
        if (null != constrainedDocumentTypeId)
        {
            assertDocumentConstraitException("Creating not 'versionalbe' Document with Version Type input parameter equal to 'MAJOR'", generateTestFileName(),
                    constrainedDocumentTypeId, getAndAssertFolderTypeId(), TEST_CONTENT, EnumVersioningState.major);
            assertDocumentConstraitException("Creating not 'versionalbe' Document with Version Type input parameter equal to 'CHECKEDOUT'", generateTestFileName(),
                    constrainedDocumentTypeId, getAndAssertFolderTypeId(), TEST_CONTENT, EnumVersioningState.checkedout);
        }
        CmisTypeDefinitionType typeDef = getAndAssertTypeDefinition(documentTypeId);
        CmisPropertyStringDefinitionType propertyDefinition = null;
        for (CmisPropertyStringDefinitionType propDef : typeDef.getPropertyStringDefinition())
        {
            if ((null != propDef.getMaxLength()) && (BigInteger.ZERO.compareTo(propDef.getMaxLength()) < 0))
            {
                propertyDefinition = propDef;
                break;
            }
        }
        if (null != propertyDefinition)
        {
            StringBuilder largeAppender = new StringBuilder("");
            long boundary = propertyDefinition.getMaxLength().longValue();
            for (long i = 0; i <= (boundary + 5); i++)
            {
                largeAppender.append("A");
            }
            CmisPropertiesType properties = new CmisPropertiesType();
            properties.setPropertyString(new CmisPropertyString[] { new CmisPropertyString(propertyDefinition.getId(), null, null, new String[] { largeAppender.toString() }) });
            assertDocumentConstraitException(("Creating Document with outing from bounds Max Length of '" + propertyDefinition.getId() + "' property"),
                    EnumServiceException.constraint, generateTestFileName(), documentTypeId, getAndAssertRootFolderId(), properties, TEST_CONTENT, null);
        }

        // TODO: “controllablePolicy” is set to FALSE and at least one policy is provided
        // TODO: “controllableACL” is set to FALSE and at least one ACE is provided
        // TODO: at least one of the permissions is used in an ACE provided which is not supported by the repository
    }

    private void assertNotAllowedObjectException(String rootFolderId, boolean document) throws Exception
    {
        String constrainedTypeId = searchAndAssertNotAllowedForFolderObjectTypeId(rootFolderId, document);
        String folderId = null;
        if (null == constrainedTypeId)
        {
            String customFolderTypeId = searchAndAssertFolderFromNotBaseType();
            folderId = createAndAssertFolder(generateTestFolderName(), customFolderTypeId, getAndAssertRootFolderId(), null);
            constrainedTypeId = searchAndAssertNotAllowedForFolderObjectTypeId(folderId, document);
        }
        if (null != constrainedTypeId)
        {
            rootFolderId = (null != folderId) ? (folderId) : (rootFolderId);
            if (document)
            {
                assertDocumentConstraitException("Creating Document with 'not allowable object type' for Parent Folder", generateTestFileName(), constrainedTypeId, rootFolderId,
                        TEST_CONTENT, null);
            }
            else
            {
                assertFolderConstraitException("Creating Folder with 'not allowable object type' for Parent Folder", EnumServiceException.constraint, generateTestFolderName(),
                        constrainedTypeId, rootFolderId, null);
            }
        }
        if (null != folderId)
        {
            deleteAndAssertObject(folderId);
        }
    }

    private void assertDocumentConstraitException(String constraintCase, String documentName, String documentTypeId, String folderId, String content,
            EnumVersioningState initialVersion) throws Exception
    {
        assertDocumentConstraitException(constraintCase, EnumServiceException.constraint, documentName, documentTypeId, folderId, content, initialVersion, false);
    }

    private void assertDocumentConstraitException(String constraintCase, EnumServiceException expectedException, String documentName, String documentTypeId, String folderId,
            String content, EnumVersioningState initialVersion, boolean setContentStreamForcibly) throws Exception
    {
        assertDocumentConstraitException(constraintCase, expectedException, documentName, documentTypeId, folderId, null, content, initialVersion);
    }

    private void assertDocumentConstraitException(String constraintCase, EnumServiceException expectedException, String documentName, String documentTypeId, String folderId,
            CmisPropertiesType properties, String content, EnumVersioningState initialVersion) throws Exception
    {
        try
        {
            String documentId = createAndAssertDocument(documentName, documentTypeId, folderId, properties, content, initialVersion);
            deleteAndAssertObject(documentId);
            fail("Either expected '" + expectedException.getValue() + "' Exception nor any Exception at all was thrown during " + constraintCase);
        }
        catch (Exception e)
        {
            assertException(constraintCase, e, expectedException);
        }
    }

    public void testDocumentCreationWithoutProperties() throws Exception
    {
        assertDocumentConstraitException("Creating Document without mandatory input parameter 'properties'", EnumServiceException.invalidArgument, null, null,
                getAndAssertRootFolderId(), null, TEST_CONTENT, null);
    }

    public void testDocumentCreatingAndUnfilingCapabilitySupporting() throws Exception
    {
        CmisRepositoryCapabilitiesType capabilities = getAndAssertCapabilities();
        if (capabilities.isCapabilityUnfiling())
        {
            String documentId = createAndAssertDocument(generateTestFileName(), getAndAssertDocumentTypeId(), null, null, TEST_CONTENT, null);
            deleteAndAssertObject(documentId);
        }
        else
        {
            assertDocumentConstraitException("Creating Document without Parent Folder Id input parameter when Unfiling Capability is not supported", generateTestFileName(),
                    getAndAssertDocumentTypeId(), null, TEST_CONTENT, null);
        }
    }

    public void testDocumentCreationAccordingToVersioningAttribute() throws Exception
    {
        if (!isVersioningAllowed())
        {
            logger.info("No one Document Object Type with 'versionable = true' attribute was found. Test will be skipped...");
            return;
        }

        String documentId = createAndAssertVersionedDocument(EnumVersioningState.minor);
        deleteAndAssertObject(documentId);
        documentId = createAndAssertVersionedDocument(EnumVersioningState.major);
        deleteAndAssertObject(documentId);
        documentId = createAndAssertVersionedDocument(EnumVersioningState.checkedout);
        documentId = cancelCheckOutAndAssert(documentId);
        deleteAndAssertObject(documentId);
    }

    private String createAndAssertVersionedDocument(EnumVersioningState versioningState) throws Exception
    {
        String documentId = null;
        try
        {
            documentId = createAndAssertDocument(generateTestFileName(), getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), null, TEST_CONTENT, versioningState);
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        CmisPropertiesType properties = getAndAssertObjectProperties(documentId, "*", false, null, false);
        boolean majorVersion = getBooleanProperty(properties, EnumPropertiesDocument._value3);
        boolean checkedOut = getBooleanProperty(properties, EnumPropertiesDocument._value7);
        if (EnumVersioningState._major.equals(versioningState.getValue()))
        {
            assertTrue("Create Document service call was performed with 'MAJOR' Versioning State but it has no 'MAJOR' Versioning State", majorVersion);
            assertFalse(CHECKEDOUT_WITHOUT_REQUEST_MESSAGE, checkedOut);
        }
        else
        {
            if (EnumVersioningState._checkedout.equals(versioningState.getValue()))
            {
                assertTrue("Create Document service call was performed with 'CHECKEDOUT' Versioning State but it has no 'CHECKEDOUT' State", checkedOut);
            }
            else
            {
                assertFalse("Create Document service call was performed with 'MINOR' Versioning State but it has 'MAJOR' Versioning State", majorVersion);
                assertFalse(CHECKEDOUT_WITHOUT_REQUEST_MESSAGE, checkedOut);
            }
        }
        return documentId;
    }

    // TODO: <Array> policies parameter for createDocument testing
    // TODO: <Array> ACE addACEs parameter for createDocument testing
    // TODO: <Array> ACE removeACEs parameter for createDocument testing

    public void testFolderCreation() throws Exception
    {
        String folderId = createAndAssertFolder();
        deleteAndAssertObject(folderId);
    }

    public void testFolderCreationWithoutProperties() throws Exception
    {
        assertFolderConstraitException("Folder Creation without mandatory 'properties' input parameter", EnumServiceException.invalidArgument, null, null,
                getAndAssertRootFolderId(), null);
    }

    public void testFolderCreationWithDifferentlyInvalidParentId() throws Exception
    {
        String folderTypeId = getAndAssertFolderTypeId();
        assertFolderConstraitException("Folder Creation with undefined 'parent folder id' input parameter", EnumServiceException.invalidArgument, generateTestFolderName(),
                folderTypeId, null, null);
        String documentId = createAndAssertDocument();
        assertFolderConstraitException("Folder Creation with none Folder 'parent folder id' input parameter", EnumServiceException.invalidArgument, generateTestFolderName(),
                folderTypeId, documentId, null);
        deleteAndAssertObject(documentId);
        assertFolderConstraitException("Folder Creation with invalid 'parent folder id' input parameter", EnumServiceException.invalidArgument, generateTestFolderName(),
                folderTypeId, "Invalid Parent Folder Id", null);
    }

    public void testFolderCreationConstraintsObservance() throws Exception
    {
        String rootFolderId = getAndAssertRootFolderId();
        assertFolderConstraitException("Folder Creation with none Folder 'type id' input parameter", EnumServiceException.constraint, generateTestFolderName(),
                getAndAssertDocumentTypeId(), rootFolderId, null);
        assertNotAllowedObjectException(rootFolderId, false);

        // TODO: “controllablePolicy” is set to FALSE and at least one policy is provided
        // TODO: “controllableACL” is set to FALSE and at least one ACE is provided
        // TODO: at least one of the permissions is used in an ACE provided which is not supported by the repository
    }

    private void assertFolderConstraitException(String constraintCase, EnumServiceException expectedException, String folderName, String folderTypeId, String parentFolderId,
            CmisPropertiesType properties) throws Exception
    {
        try
        {
            String folderId = createAndAssertFolder(folderName, folderTypeId, parentFolderId, properties);
            deleteAndAssertObject(folderId);
            fail("Either expected '" + expectedException.getValue() + "' Exception nor any Exception at all was thrown during " + constraintCase);
        }
        catch (Exception e)
        {
            assertException(constraintCase, e, expectedException);
        }
    }

    // TODO: <Array> policies parameter for createFolder testing
    // TODO: <Array> ACE addACEs parameter for createFolder testing
    // TODO: <Array> ACE removeACEs parameter for createFolder testing

    public void testCreateRelationship() throws Exception
    {
        String relationshipId = null;
        try
        {
            relationshipId = createAndAssertRelationship();
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        LOGGER.info("[ObjectService->deleteObject]");
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), relationshipId, null));
    }

    private void assertRelationshipConstraitException(String constraintCase, EnumServiceException expectedException, String sourceId, String targetId, String relationshipTypeId)
            throws Exception
    {
        try
        {
            String relationshipId = createAndAssertRelationship(sourceId, targetId, relationshipTypeId);
            deleteAndAssertObject(relationshipId);
            fail("Either expected '" + expectedException.getValue() + "' Exception nor any Exception at all was thrown during " + constraintCase);
        }
        catch (Exception e)
        {
            assertException(constraintCase, e, expectedException);
        }
    }

    public void testRelationshipCreationConstraintsObservance() throws Exception
    {
        assertRelationshipConstraitException("Relationship Creation with typeId is not an Object-Type whose baseType is Relationship", EnumServiceException.constraint, null, null,
                getAndAssertFolderTypeId());

        String relationshipTypeId = searchAndAssertRelationshipTypeWithAllowedSourceTypes();
        if (relationshipTypeId != null)
        {
            String notAllowdSourceTypeId = searchAndAssertNotAllowedSourceForRelationshipTypeId(relationshipTypeId);
            assertRelationshipConstraitException(
                    "Relationship Creation with the sourceObjectId’s ObjectType is not in the list of “allowedSourceTypes” specified by the Object-Type definition specified by typeId",
                    EnumServiceException.constraint, notAllowdSourceTypeId, null, relationshipTypeId);
        }

        relationshipTypeId = searchAndAssertRelationshipTypeWithAllowedTargetTypes();
        if (relationshipTypeId != null)
        {
            String notAllowdTargetTypeId = searchAndAssertNotAllowedSourceForRelationshipTypeId(relationshipTypeId);
            assertRelationshipConstraitException(
                    "Relationship Creation with the sourceObjectId’s ObjectType is not in the list of “allowedTargetTypes” specified by the Object-Type definition specified by typeId",
                    EnumServiceException.constraint, null, notAllowdTargetTypeId, relationshipTypeId);
        }

        // TODO: “controllablePolicy” is set to FALSE and at least one policy is provided
        // TODO: “controllableACL” is set to FALSE and at least one ACE is provided
        // TODO: at least one of the permissions is used in an ACE provided which is not supported by the repository
    }

    public void testCreatePolicy() throws Exception
    {
        String policyId = null;
        try
        {
            policyId = createAndAssertPolicy();
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        LOGGER.info("[ObjectService->deleteObject]");
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), policyId, null));
    }

    public void testGetAllowableActions() throws Exception
    {
        GetAllowableActionsResponse response = null;
        try
        {
            LOGGER.info("[ObjectService->getAllowableActions]");
            response = getServicesFactory().getObjectService().getAllowableActions(new GetAllowableActions(getAndAssertRepositoryId(), getAndAssertRootFolderId()));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        assertNotNull("No allowable actions were returned", response);
        assertNotNull("Action 'getProperties' not defined for an object", response.getAllowableActions().getCanGetProperties());
    }

    public void testGetPropertiesDefault() throws Exception
    {
        GetPropertiesResponse response = null;
        try
        {
            LOGGER.info("[ObjectService->getProperties]");
            response = getServicesFactory().getObjectService().getProperties(new GetProperties(getAndAssertRepositoryId(), getAndAssertRootFolderId(), null, null, null, null));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        assertTrue("No properties were returned", response != null && response.getObject() != null && response.getObject().getProperties() != null);
        assertNotNull("No 'Name' property was returned", getStringProperty(response.getObject().getProperties(), EnumPropertiesBase._value1));
    }

    public void testGetPropertiesFiltered() throws Exception
    {
        GetPropertiesResponse response = null;
        try
        {
            String filter = EnumPropertiesBase._value1 + ", " + EnumPropertiesBase._value2;
            LOGGER.info("[ObjectService->getProperties]");
            response = getServicesFactory().getObjectService().getProperties(new GetProperties(getAndAssertRepositoryId(), getAndAssertRootFolderId(), filter, null, null, null));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        assertTrue("No properties were returned", response != null && response.getObject() != null && response.getObject().getProperties() != null);

        CmisPropertiesType properties = response.getObject().getProperties();

        assertNull("Not expected properties were returned", properties.getPropertyBoolean());
        assertNull("Not expected properties were returned", properties.getPropertyDecimal());
        assertNull("Not expected properties were returned", properties.getPropertyHtml());
        assertNull("Not expected properties were returned", properties.getPropertyInteger());
        assertNull("Not expected properties were returned", properties.getPropertyUri());
        assertNull("Not expected properties were returned", properties.getPropertyXml());
        assertNull("Not expected properties were returned", properties.getPropertyDateTime());

        assertNotNull("Expected properties were not returned", properties.getPropertyId());
        assertNotNull("Expected properties were not returned", properties.getPropertyString());

        assertEquals("Expected properties were not returned", 1, properties.getPropertyId().length);
        assertEquals("Expected properties were not returned", 1, properties.getPropertyString().length);

        assertNotNull("Expected property was not returned", getIdProperty(properties, EnumPropertiesBase._value2));
        assertNotNull("Expected property was not returned", getStringProperty(properties, EnumPropertiesBase._value1));
    }

    public void testGetPropertiesIncludeAllowableActionsAndRelationships() throws Exception
    {
        GetPropertiesResponse response = null;

        try
        {
            LOGGER.info("[ObjectService->getProperties]");
            response = getServicesFactory().getObjectService().getProperties(new GetProperties(getAndAssertRepositoryId(), getAndAssertRootFolderId(), null, true, null, null));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        assertTrue(PROPERTIES_NOT_RETURNED_MESSAGE, response != null && response.getObject() != null && response.getObject().getProperties() != null);
        assertNotNull("Allowable actions were not returned", response.getObject().getAllowableActions());
        assertNotNull("No action 'getProperties' was returned", response.getObject().getAllowableActions().getCanGetProperties());

        String relationshipId = createAndAssertRelationship();
        try
        {
            LOGGER.info("[ObjectService->getProperties]");
            response = getServicesFactory().getObjectService().getProperties(
                    new GetProperties(getAndAssertRepositoryId(), getAndAssertRootFolderId(), null, null, EnumIncludeRelationships.both, null));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        assertTrue(PROPERTIES_NOT_RETURNED_MESSAGE, response != null && response.getObject() != null && response.getObject().getProperties() != null);
        assertTrue("Relationships were not returned", response.getObject().getRelationship() != null && response.getObject().getRelationship().length >= 1);

        LOGGER.info("[ObjectService->deleteObject]");
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), relationshipId, null));
    }

    public void testGetFolderByPath() throws Exception
    {
        String folder1Id = null;
        String folder2Id = null;
        try
        {
            String folder1Name = generateTestFolderName("_1");
            String folder2Name = generateTestFolderName("_2");
            folder1Id = createAndAssertFolder(folder1Name, getAndAssertFolderTypeId(), getAndAssertRootFolderId(), null);
            folder2Id = createAndAssertFolder(folder2Name, getAndAssertFolderTypeId(), folder1Id, null);
            assertNotNull("Folder was not created", folder1Id);
            assertNotNull("Folder was not created", folder2Id);

            String pathToFolder1 = "/" + folder1Name;
            String pathToFolder2 = "/" + folder1Name + "/" + folder2Name;

            LOGGER.info("[ObjectService->getFolderByPath]");
            GetFolderByPathResponse response = getServicesFactory().getObjectService().getFolderByPath(
                    new GetFolderByPath(getAndAssertRepositoryId(), pathToFolder1, "*", false, null, false));
            assertTrue("Folder was not found", response != null && response.getObject() != null && response.getObject().getProperties() != null);
            assertEquals("Wrong folder was found", folder1Id, getIdProperty(response.getObject().getProperties(), EnumPropertiesBase._value2));

            LOGGER.info("[ObjectService->getFolderByPath]");
            response = getServicesFactory().getObjectService().getFolderByPath(new GetFolderByPath(getAndAssertRepositoryId(), pathToFolder2, "*", false, null, false));
            assertTrue("Folder was not found", response != null && response.getObject() != null && response.getObject().getProperties() != null);
            assertEquals("Wrong folder was found", folder2Id, getIdProperty(response.getObject().getProperties(), EnumPropertiesBase._value2));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        LOGGER.info("[ObjectService->deleteObject]");
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), folder2Id, null));
        LOGGER.info("[ObjectService->deleteObject]");
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), folder1Id, null));
    }

    public void testGetContentStream() throws Exception
    {
        if (isContentStreamAllowed())
        {
            GetContentStreamResponse response = null;
            String documentId = createAndAssertDocument();
            try
            {
                LOGGER.info("[ObjectService->getContentStream]");
                response = getServicesFactory().getObjectService().getContentStream(new GetContentStream(getAndAssertRepositoryId(), documentId, ""));
            }
            catch (Exception e)
            {
                LOGGER.info("[ObjectService->deleteObject]");
                getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, null));
                fail(e.toString());
            }
            assertTrue("No content stream was returned", response != null && response.getContentStream() != null);
            assertTrue("Invalid content stream was returned", Arrays.equals(TEST_CONTENT.getBytes(), response.getContentStream().getStream()));
            LOGGER.info("[ObjectService->deleteObject]");
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, null));
        }
        else
        {
            LOGGER.info("testGetContentStream was skipped: Content stream isn't allowed");
        }
    }

    public void testGetContentStreamConstraintsObservance() throws Exception
    {
        String docTypeWithNoContentAllowed = searchAndAssertDocumentTypeWithNoContentAlowed();
        if (docTypeWithNoContentAllowed != null && !docTypeWithNoContentAllowed.equals(""))
        {
            String documentId = createAndAssertDocument(generateTestFileName(), docTypeWithNoContentAllowed, getAndAssertRootFolderId(), null, null, null);
            try
            {
                LOGGER.info("[ObjectService->getContentStream]");
                getServicesFactory().getObjectService().getContentStream(new GetContentStream(getAndAssertRepositoryId(), documentId, ""));
                fail("Either expected 'constraint' Exception, or no Exception at all was thrown during getting content stream for object which does NOT have a content stream");
            }
            catch (Exception e)
            {
                LOGGER.info("[ObjectService->deleteObject]");
                getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, null));
                assertException("Trying to get content stream for object which does NOT have a content stream", e, EnumServiceException.constraint);
            }
            LOGGER.info("[ObjectService->deleteObject]");
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, null));
        }
    }

    public void testUpdatePropertiesDefault() throws Exception
    {
        String documentName = generateTestFileName();
        String documentId = createAndAssertDocument(documentName, getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), null, TEST_CONTENT, null);

        String documentNameNew = generateTestFileName("_new");
        try
        {
            CmisPropertiesType properties = fillProperties(documentNameNew, null);
            LOGGER.info("[ObjectService->updateProperties]");
            documentId = getServicesFactory().getObjectService().updateProperties(new UpdateProperties(getAndAssertRepositoryId(), documentId, null, properties)).getObjectId();
        }
        catch (Exception e)
        {
            fail(e.toString());
        }

        assertEquals("Properties was not updated", documentNameNew, getStringProperty(documentId, EnumPropertiesBase._value1));

        LOGGER.info("[ObjectService->deleteObject]");
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, null));
    }

    public void testMoveObjectDefault() throws Exception
    {
        String documentId = createAndAssertDocument();
        String folderId = createAndAssertFolder();
        try
        {
            LOGGER.info("[ObjectService->moveObject]");
            getServicesFactory().getObjectService().moveObject(new MoveObject(getAndAssertRepositoryId(), documentId, folderId, getAndAssertRootFolderId()));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }

        assertFalse("Object was not removed from source folder", isDocumentInFolder(documentId, getAndAssertRootFolderId()));
        assertTrue("Object was not added to target folder", isDocumentInFolder(documentId, folderId));

        LOGGER.info("[ObjectService->deleteObject]");
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, null));
        LOGGER.info("[ObjectService->deleteObject]");
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), folderId, null));
    }

    public void testMoveObjectUnfiled() throws Exception
    {
        if (getAndAssertCapabilities().isCapabilityUnfiling())
        {
            String folderId = createAndAssertFolder();
            String documentId = createAndAssertDocument(generateTestFileName(), getAndAssertDocumentTypeId(), null, null, null, null);
            try
            {
                LOGGER.info("[ObjectService->moveObject]");
                getServicesFactory().getObjectService().moveObject(new MoveObject(getAndAssertRepositoryId(), documentId, folderId, null));
                fail("No Exception was thrown");
            }
            catch (Exception e)
            {
                assertTrue("Invalid exception was thrown", e instanceof CmisFaultType && ((CmisFaultType) e).getType().equals(EnumServiceException.notSupported));
            }
            LOGGER.info("[ObjectService->deleteObject]");
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, null));
            LOGGER.info("[ObjectService->deleteObject]");
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), folderId, null));
        }
        else
        {
            LOGGER.info("testMoveObjectUnfiled was skipped: Unfiling isn't supported");
        }
    }

    public void testMoveObjectMultiFiled() throws Exception
    {
        if (getAndAssertCapabilities().isCapabilityMultifiling())
        {
            String documentId = createAndAssertDocument();
            String folderId = createAndAssertFolder();
            String folder2Id = createAndAssertFolder(generateTestFolderName(), getAndAssertFolderTypeId(), folderId, null);
            LOGGER.info("[MultiFilingService->addObjectToFolder]");
            getServicesFactory().getMultiFilingServicePort().addObjectToFolder(new AddObjectToFolder(getAndAssertRepositoryId(), documentId, folderId));

            try
            {
                LOGGER.info("[ObjectService->moveObject]");
                getServicesFactory().getObjectService().moveObject(new MoveObject(getAndAssertRepositoryId(), documentId, folder2Id, folderId));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }

            assertFalse("Object was not removed from source folder", isDocumentInFolder(documentId, folderId));
            assertTrue("Object was removed from not source folder", isDocumentInFolder(documentId, getAndAssertRootFolderId()));
            assertTrue("Object was not added to target folder", isDocumentInFolder(documentId, folder2Id));

            LOGGER.info("[ObjectService->deleteTree]");
            getServicesFactory().getObjectService().deleteTree(new DeleteTree(getAndAssertRepositoryId(), folderId, EnumUnfileObject.delete, true));
        }
    }

    // FIXME: It is NECESSARY test 'versionSeries' existent and test tries for requesting properties etc of some object from 'versionSeries'
    // FIXME: It is NECESSARY test 'getPropertiesOfLatestVersion' service's method call after deleting current document
    public void testDeleteObject() throws Exception
    {
        String documentId = createAndAssertDocument();
        try
        {
            LOGGER.info("[ObjectService->deleteObject]");
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, true));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }

        assertFalse("Object was not removed", isDocumentInFolder(documentId, getAndAssertRootFolderId()));
    }

    public void testDeleteFolderWithChild() throws Exception
    {
        String folderId = createAndAssertFolder();
        String folder2Id = createAndAssertFolder(generateTestFolderName("1"), getAndAssertFolderTypeId(), folderId, null);
        try
        {
            LOGGER.info("[ObjectService->deleteObject]");
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), folderId, null));
            fail("No Exception was thrown");
        }
        catch (Exception e)
        {
            assertTrue("Invalid exception was thrown", e instanceof CmisFaultType && ((CmisFaultType) e).getType().equals(EnumServiceException.constraint));
        }
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), folder2Id, null));
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), folderId, null));
    }

    public void testDeleteMultiFiledObject() throws Exception
    {
        if (getAndAssertCapabilities().isCapabilityMultifiling())
        {
            String documentId = createAndAssertDocument();
            String folderId = createAndAssertFolder();
            LOGGER.info("[MultiFilingService->addObjectToFolder]");
            getServicesFactory().getMultiFilingServicePort().addObjectToFolder(new AddObjectToFolder(getAndAssertRepositoryId(), documentId, folderId));
            try
            {
                // TODO works not correct in Alfresco
                LOGGER.info("[ObjectService->deleteObject]");
                getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, null));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertFalse("Object was not removed", isDocumentInFolder(documentId, getAndAssertRootFolderId()));
            assertFalse("Object was not removed", isDocumentInFolder(documentId, folderId));

            LOGGER.info("[ObjectService->deleteTree]");
            getServicesFactory().getObjectService().deleteTree(new DeleteTree(getAndAssertRepositoryId(), folderId, EnumUnfileObject.delete, true));
        }
    }

    public void testDeletePWC() throws Exception
    {
        if (isVersioningAllowed())
        {
            String documentId = createAndAssertDocument();
            LOGGER.info("[VersioningService->checkOut]");
            String checkedOutId = getServicesFactory().getVersioningService().checkOut(new CheckOut(getAndAssertRepositoryId(), documentId)).getDocumentId();
            try
            {
                LOGGER.info("[ObjectService->deleteObject]");
                getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), checkedOutId, null));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertFalse("Private working copy was not deleted", getBooleanProperty(documentId, EnumPropertiesDocument._value7));
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
        String folderId = createAndAssertFolder();
        createAndAssertDocument();
        createAndAssertFolder(generateTestFolderName(), getAndAssertFolderTypeId(), folderId, null);
        try
        {
            LOGGER.info("[ObjectService->deleteTree]");
            response = getServicesFactory().getObjectService().deleteTree(new DeleteTree(getAndAssertRepositoryId(), folderId, EnumUnfileObject.delete, false));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }

        assertTrue("Objects tree was not deleted", response == null || response.getFailedToDelete() == null || response.getFailedToDelete().length == 0);
    }

    public void testDeleteTreeUnfileNonfolderObjects() throws Exception
    {
        if (getAndAssertCapabilities().isCapabilityMultifiling())
        {
            DeleteTreeResponse response = null;
            String folderId = createAndAssertFolder();
            String documentId = createAndAssertDocument();
            String folder2Id = createAndAssertFolder(generateTestFolderName(), getAndAssertFolderTypeId(), folderId, null);
            String document2Id = createAndAssertDocument(generateTestFileName(), getAndAssertDocumentTypeId(), folderId, null, TEST_CONTENT, EnumVersioningState.major);
            LOGGER.info("[MultiFilingService->addObjectToFolder]");
            getServicesFactory().getMultiFilingServicePort().addObjectToFolder(new AddObjectToFolder(getAndAssertRepositoryId(), documentId, folderId));
            LOGGER.info("[MultiFilingService->addObjectToFolder]");
            getServicesFactory().getMultiFilingServicePort().addObjectToFolder(new AddObjectToFolder(getAndAssertRepositoryId(), document2Id, folder2Id));
            try
            {
                LOGGER.info("[ObjectService->deleteTree]");
                response = getServicesFactory().getObjectService().deleteTree(new DeleteTree(getAndAssertRepositoryId(), folderId, EnumUnfileObject.delete, false));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertTrue("Objects tree was not deleted", response == null || response.getFailedToDelete() == null || response.getFailedToDelete().length == 0);
            assertFalse("Multifiled document was not removed", isDocumentInFolder(documentId, getAndAssertRootFolderId()));
        }
    }

    public void testDeleteTreeRootFolder()
    {
        try
        {
            LOGGER.info("[ObjectService->deleteTree]");
            getServicesFactory().getObjectService().deleteTree(new DeleteTree(getAndAssertRepositoryId(), getAndAssertRootFolderId(), EnumUnfileObject.delete, true));
            fail("No Exception was thrown");
        }
        catch (Exception e)
        {
            assertTrue("Invalid exception was thrown", e instanceof CmisFaultType && ((CmisFaultType) e).getType().equals(EnumServiceException.notSupported));
        }
    }

    public void testSetContentStream() throws Exception
    {
        if (isContentStreamAllowed())
        {
            String documentName = generateTestFileName();
            String documentId = createAndAssertDocument(documentName, getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), null, TEST_CONTENT, null);
            String newTestCOntent = TEST_CONTENT + System.currentTimeMillis();
            try
            {
                LOGGER.info("[ObjectService->setContentStream]");
                documentId = getServicesFactory().getObjectService().setContentStream(
                        new SetContentStream(getAndAssertRepositoryId(), documentId, true, null, new CmisContentStreamType(BigInteger.valueOf(newTestCOntent.length()),
                                MIMETYPE_TEXT_PLAIN, documentName, null, newTestCOntent.getBytes(ENCODING), null))).getDocumentId();
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            // TODO uncomment
            // assertTrue("Content stream was not updated", Arrays.equals(newTestCOntent.getBytes(), getServicesFactory().getObjectService().getContentStream(
            // new GetContentStream(getAndAssertRepositoryId(), documentId)).getContentStream().getStream()));
            LOGGER.info("[ObjectService->deleteObject]");
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
            String documentName = generateTestFileName();
            String documentId = createAndAssertDocument();
            String newTestCOntent = TEST_CONTENT + System.currentTimeMillis();
            try
            {
                LOGGER.info("[ObjectService->setContentStream]");
                documentId = getServicesFactory().getObjectService().setContentStream(
                        new SetContentStream(getAndAssertRepositoryId(), documentId, false, null, new CmisContentStreamType(BigInteger.valueOf(newTestCOntent.length()),
                                MIMETYPE_TEXT_PLAIN, documentName, null, newTestCOntent.getBytes(ENCODING), null))).getDocumentId();
                fail("No Exception was thrown");
            }
            catch (Exception e)
            {
                assertTrue("Invalid exception was thrown", e instanceof CmisFaultType && ((CmisFaultType) e).getType().equals(EnumServiceException.contentAlreadyExists));
            }
            try
            {
                LOGGER.info("[ObjectService->setContentStream]");
                documentId = getServicesFactory().getObjectService().setContentStream(
                        new SetContentStream(getAndAssertRepositoryId(), documentId, true, null, new CmisContentStreamType(BigInteger.valueOf(newTestCOntent.length()),
                                MIMETYPE_TEXT_PLAIN, documentName, null, newTestCOntent.getBytes(ENCODING), null))).getDocumentId();
            }
            catch (Exception e)
            {
                fail(e.toString());
            }

            // TODO uncomment
            // assertTrue("Content stream was not updated", Arrays.equals(newTestCOntent.getBytes(), getServicesFactory().getObjectService().getContentStream(
            // new GetContentStream(getAndAssertRepositoryId(), documentId)).getContentStream().getStream()));
            LOGGER.info("[ObjectService->deleteObject]");
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
            String documentId = createAndAssertDocument();
            try
            {
                LOGGER.info("[ObjectService->deleteContentStream]");
                getServicesFactory().getObjectService().deleteContentStream(new DeleteContentStream(getAndAssertRepositoryId(), documentId, null));
            }
            catch (Exception e)
            {
                fail(e.toString());
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
                assertTrue("Invalid exception was thrown", e instanceof CmisFaultType);
            }
            LOGGER.info("[ObjectService->deleteObject]");
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, null));
        }
        else
        {
            LOGGER.info("testDeleteContentStream was skipped: Content stream isn't allowed");
        }
    }

    public void testGetRenditions() throws Exception
    {
        if (getAndAssertCapabilities().getCapabilityRenditions().equals(EnumCapabilityRendition.read))
        {
            String documentId = createAndAssertDocument();
            CmisRenditionType[] renditionTypes = null;
            try
            {
                LOGGER.info("[ObjectService->getRenditions]");
                renditionTypes = getServicesFactory().getObjectService().getRenditions(
                        new GetRenditions(getAndAssertRepositoryId(), documentId, "*", BigInteger.valueOf(0), BigInteger.valueOf(0)));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertNotNull("No Renditions were returned", renditionTypes);
        }
        else
        {
            LOGGER.info("testGetRenditions was skipped: Renditions aren't supported");
        }
    }
}
