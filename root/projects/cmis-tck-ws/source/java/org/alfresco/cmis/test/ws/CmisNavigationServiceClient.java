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

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.alfresco.repo.cmis.ws.AddObjectToFolder;
import org.alfresco.repo.cmis.ws.CancelCheckOut;
import org.alfresco.repo.cmis.ws.CheckIn;
import org.alfresco.repo.cmis.ws.CheckInResponse;
import org.alfresco.repo.cmis.ws.CheckOut;
import org.alfresco.repo.cmis.ws.CheckOutResponse;
import org.alfresco.repo.cmis.ws.CmisContentStreamType;
import org.alfresco.repo.cmis.ws.CmisFaultType;
import org.alfresco.repo.cmis.ws.CmisObjectType;
import org.alfresco.repo.cmis.ws.CmisPropertiesType;
import org.alfresco.repo.cmis.ws.CmisPropertyDateTime;
import org.alfresco.repo.cmis.ws.CmisPropertyId;
import org.alfresco.repo.cmis.ws.CmisPropertyString;
import org.alfresco.repo.cmis.ws.CmisTypeDefinitionType;
import org.alfresco.repo.cmis.ws.DeleteObject;
import org.alfresco.repo.cmis.ws.DeleteTree;
import org.alfresco.repo.cmis.ws.EnumBaseObjectTypeIds;
import org.alfresco.repo.cmis.ws.EnumIncludeRelationships;
import org.alfresco.repo.cmis.ws.EnumPropertiesBase;
import org.alfresco.repo.cmis.ws.EnumPropertiesRelationship;
import org.alfresco.repo.cmis.ws.EnumServiceException;
import org.alfresco.repo.cmis.ws.EnumUnfileObject;
import org.alfresco.repo.cmis.ws.EnumVersioningState;
import org.alfresco.repo.cmis.ws.GetCheckedOutDocs;
import org.alfresco.repo.cmis.ws.GetCheckedOutDocsResponse;
import org.alfresco.repo.cmis.ws.GetChildren;
import org.alfresco.repo.cmis.ws.GetChildrenResponse;
import org.alfresco.repo.cmis.ws.GetDescendants;
import org.alfresco.repo.cmis.ws.GetFolderParent;
import org.alfresco.repo.cmis.ws.GetFolderParentResponse;
import org.alfresco.repo.cmis.ws.GetFolderTree;
import org.alfresco.repo.cmis.ws.GetObjectParents;
import org.alfresco.repo.cmis.ws.NavigationServicePort;
import org.alfresco.repo.cmis.ws.NavigationServicePortBindingStub;
import org.alfresco.repo.cmis.ws.VersioningServicePortBindingStub;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Client for Navigation Service
 */
public class CmisNavigationServiceClient extends AbstractServiceClient
{
    private static Log LOGGER = LogFactory.getLog(CmisNavigationServiceClient.class);

    private static final int TEST_TREE_DEPTH = 4;

    private static final int PAGING_STEP = 5;
    private static final int PAGING_LIMIT = PAGING_STEP * 2;

    private static final int CHILDREN_TEST_OBJECTS_AMOUNT = 10;

    private static final String INVALID_REPOSITORY_ID = "Wrong Repository Id";
    private static final String INVALID_FILTER = "Name, *eationDa*";

    private static final String OBJECT_IS_NULL_MESSAGE = "Some Object from response is null";
    private static final String INVALID_WRONG_FILTER_PROCESSING_MESSAGE = "Wrong filter was not processed as correct filter";
    private static final String INVALID_OBJECTS_INTEGRITY_MESSAGE = "Not all Objects or odd Objects were returned in Response from ";
    private static final String INVALID_OBJECTS_LAYERS_DESCRIPTION_MESSAGE = "Objects layers description and actual Objects list are not compliant";

    private String folderId;
    private String documentId;

    public CmisNavigationServiceClient()
    {
        super();
    }

    public CmisNavigationServiceClient(AbstractService abstractService)
    {
        super(abstractService);
    }

    /**
     * Initializes Navigation Service client
     */
    @Override
    public void initialize() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Initializing client...");
        }
        folderId = createAndAssertFolder();
        documentId = createAndAssertDocument(generateTestFileName(), getAndAssertDocumentTypeId(), folderId, null, TEST_CONTENT, null);
        getServicesFactory().getVersioningService().checkOut(new CheckOut(getAndAssertRepositoryId(), documentId));
    }

    /**
     * Invokes all methods in Navigation Service
     */
    @Override
    public void invoke() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Invoking client...");
        }
        NavigationServicePort navigationServicePort = getServicesFactory().getNavigationService(getProxyUrl() + getService().getPath());

        navigationServicePort.getDescendants(new GetDescendants(getAndAssertRepositoryId(), getAndAssertRootFolderId(), BigInteger.valueOf(2), "*", Boolean.FALSE,
                EnumIncludeRelationships.both, Boolean.FALSE, null));

        GetFolderParent getFolderParent = new GetFolderParent();
        getFolderParent.setRepositoryId(getAndAssertRepositoryId());
        getFolderParent.setFolderId(folderId);
        getFolderParent.setFilter("*");
        navigationServicePort.getFolderParent(getFolderParent);

        GetObjectParents getObjectParents = new GetObjectParents();
        getObjectParents.setRepositoryId(getAndAssertRepositoryId());
        getObjectParents.setObjectId(documentId);
        getObjectParents.setFilter("*");
        navigationServicePort.getObjectParents(getObjectParents);

        GetCheckedOutDocs getCheckedoutDocs = new GetCheckedOutDocs();
        getCheckedoutDocs.setRepositoryId(getAndAssertRepositoryId());
        getCheckedoutDocs.setFolderId(folderId);
        getCheckedoutDocs.setFilter("*");
        getCheckedoutDocs.setMaxItems(BigInteger.valueOf(0));
        getCheckedoutDocs.setSkipCount(BigInteger.valueOf(0));
        getCheckedoutDocs.setIncludeAllowableActions(true);
        getCheckedoutDocs.setIncludeRelationships(EnumIncludeRelationships.both);
        navigationServicePort.getCheckedOutDocs(getCheckedoutDocs);

        GetChildren getChildren = new GetChildren();
        getChildren.setRepositoryId(getAndAssertRepositoryId());
        getChildren.setFolderId(getAndAssertRootFolderId());
        getChildren.setFilter("*");
        getChildren.setIncludeAllowableActions(false);
        getChildren.setIncludeRelationships(EnumIncludeRelationships.both);
        getChildren.setMaxItems(BigInteger.valueOf(0));
        getChildren.setSkipCount(BigInteger.valueOf(0));
        navigationServicePort.getChildren(getChildren);
    }

    /**
     * Remove initial data
     */
    @Override
    public void release() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Releasing client...");
        }
        getServicesFactory().getObjectService().deleteTree(new DeleteTree(getAndAssertRepositoryId(), folderId, EnumUnfileObject.delete, true));
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
        AbstractServiceClient client = (CmisNavigationServiceClient) applicationContext.getBean("cmisNavigationServiceClient");
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
        folderId = createAndAssertFolder();
        documentId = createAndAssertDocument(generateTestFileName(), getAndAssertDocumentTypeId(), folderId, null, TEST_CONTENT, null);
        getServicesFactory().getVersioningService().checkOut(new CheckOut(getAndAssertRepositoryId(), documentId));
        super.onSetUp();
    }

    @Override
    protected void onTearDown() throws Exception
    {
        getServicesFactory().getObjectService().deleteTree(new DeleteTree(getAndAssertRepositoryId(), folderId, EnumUnfileObject.delete, true));
        super.onTearDown();
    }

    public void testFoldersTreeReceiving() throws Exception
    {
        List<String> expectedTree = createAndAssertObjectsTree(folderId, EnumVersioningState.major, EnumTypesOfFileableObjects.FOLDERS, -1, TEST_TREE_DEPTH, 1, 6);

        NavigationServicePortBindingStub navigationService = getServicesFactory().getNavigationService();
        LOGGER.info("[NavigationService->getFolderTree]");
        CmisObjectType[] foldersTreeResponse = navigationService.getFolderTree(new GetFolderTree(getAndAssertRepositoryId(), folderId, "*", BigInteger.valueOf(-1), false,
                EnumIncludeRelationships.none));

        assertNotNull("Folder tree response is NULL", foldersTreeResponse);
        assertObjectsFromResponse(foldersTreeResponse, 0, expectedTree.size());

        assertObjectsTree(expectedTree, foldersTreeResponse, true, "GetFoldersTree service");
    }

    public void testDepthLimitedFoldersTreeReceiving() throws Exception
    {
        List<String> expectedTree = createAndAssertObjectsTree(folderId, EnumVersioningState.major, EnumTypesOfFileableObjects.FOLDERS, 2, 2, 1, 4);

        NavigationServicePortBindingStub navigationService = getServicesFactory().getNavigationService();
        LOGGER.info("[NavigationService->getFolderTree]");
        CmisObjectType[] foldersTreeResponse = navigationService.getFolderTree(new GetFolderTree(getAndAssertRepositoryId(), folderId, "*", BigInteger.valueOf(2), false,
                EnumIncludeRelationships.none));

        assertNotNull("Folder tree response is NULL", foldersTreeResponse);
        assertObjectsFromResponse(foldersTreeResponse, 0, expectedTree.size());

        assertObjectsTree(expectedTree, foldersTreeResponse, true, "GetFoldersTree service with depth limit");
    }

    public void testFilteredFoldersTreeReceiving() throws Exception
    {
        int objectsAmount = createAndAssertObjectsTree(folderId, EnumVersioningState.major, EnumTypesOfFileableObjects.FOLDERS, -1, 3, 1, 4).size();
        String filter = EnumPropertiesBase._value1 + ", " + EnumPropertiesBase._value2 + ", " + EnumPropertiesBase._value6;
        NavigationServicePortBindingStub navigationService = getServicesFactory().getNavigationService();
        LOGGER.info("[NavigationService->getFolderTree]");
        CmisObjectType[] foldersTreeResponse = navigationService.getFolderTree(new GetFolderTree(getAndAssertRepositoryId(), folderId, filter, BigInteger.valueOf(3), false,
                EnumIncludeRelationships.none));
        assertNotNull("Folder tree response is NULL", foldersTreeResponse);
        assertObjectsFromResponse(foldersTreeResponse, 0, objectsAmount);

        for (CmisObjectType object : foldersTreeResponse)
        {
            CmisPropertiesType properties = object.getProperties();

            assertNull("Not expected properties were returned", properties.getPropertyBoolean());
            assertNull("Not expected properties were returned", properties.getPropertyDecimal());
            assertNull("Not expected properties were returned", properties.getPropertyHtml());
            assertNull("Not expected properties were returned", properties.getPropertyInteger());
            assertNull("Not expected properties were returned", properties.getPropertyUri());
            assertNull("Not expected properties were returned", properties.getPropertyXml());

            assertNotNull("Expected properties were not returned", properties.getPropertyId());
            assertNotNull("Expected properties were not returned", properties.getPropertyDateTime());
            assertNotNull("Expected properties were not returned", properties.getPropertyString());

            assertEquals("Expected property was not returned", 1, properties.getPropertyId().length);
            assertEquals("Expected property was not returned", 1, properties.getPropertyDateTime().length);
            assertEquals("Expected property was not returned", 1, properties.getPropertyString().length);

            getAndAssertIdPropertyValue(properties, EnumPropertiesBase._value2);
            assertNotNull("Expected property was not returned", getStringProperty(properties, EnumPropertiesBase._value1));
            getAndAssertDateTimePropertyValue(properties, EnumPropertiesBase._value6);
        }
    }

    public void testDescendantsReceiving() throws Exception
    {
        List<String> expectedTree = createAndAssertObjectsTree(folderId, EnumVersioningState.major, EnumTypesOfFileableObjects.BOTH, -1, TEST_TREE_DEPTH, 1, 6);
        expectedTree.add(documentId);

        NavigationServicePortBindingStub navigationService = getServicesFactory().getNavigationService();
        LOGGER.info("[NavigationService->getDescendants]");
        CmisObjectType[] descendantsResponse = navigationService.getDescendants(new GetDescendants(getAndAssertRepositoryId(), folderId, BigInteger.valueOf(-1), "*", false,
                EnumIncludeRelationships.none, false, null));

        assertNotNull("GetDescendants response is NULL", descendantsResponse);
        assertObjectsFromResponse(descendantsResponse, 0, expectedTree.size());

        assertObjectsTree(expectedTree, descendantsResponse, true, "GetDescendants service");
    }

    public void testDepthLimitedDescendantsReceiving() throws Exception
    {
        List<String> expectedTree = createAndAssertObjectsTree(folderId, EnumVersioningState.major, EnumTypesOfFileableObjects.BOTH, 2, TEST_TREE_DEPTH, 1, 6);
        expectedTree.add(documentId);

        NavigationServicePortBindingStub navigationService = getServicesFactory().getNavigationService();
        LOGGER.info("[NavigationService->getDescendants]");
        CmisObjectType[] descendantsResponse = navigationService.getDescendants(new GetDescendants(getAndAssertRepositoryId(), folderId, BigInteger.valueOf(2), "*", false,
                EnumIncludeRelationships.none, false, null));

        assertNotNull("GetDescendants response is NULL", descendantsResponse);
        assertObjectsFromResponse(descendantsResponse, 0, expectedTree.size());

        assertObjectsTree(expectedTree, descendantsResponse, true, "GetDescendants service with depth limit");
    }

    public void testFilteredDescendantsReceiving() throws Exception
    {
        int objectsAmount = createAndAssertObjectsTree(folderId, EnumVersioningState.major, EnumTypesOfFileableObjects.BOTH, -1, 3, 1, 4).size();

        NavigationServicePortBindingStub navigationService = getServicesFactory().getNavigationService();
        String filter = EnumPropertiesBase._value1 + ", " + EnumPropertiesBase._value2 + ", " + EnumPropertiesBase._value6;
        LOGGER.info("[NavigationService->getDescendants]");
        CmisObjectType[] descendantsResponse = navigationService.getDescendants(new GetDescendants(getAndAssertRepositoryId(), folderId, BigInteger.valueOf(3), filter, false,
                EnumIncludeRelationships.none, false, null));
        assertNotNull("GetDescendants response is NULL", descendantsResponse);
        assertObjectsFromResponse(descendantsResponse, 0, objectsAmount);

        for (CmisObjectType object : descendantsResponse)
        {
            CmisPropertiesType properties = object.getProperties();

            assertNull("Not expected properties were returned", properties.getPropertyBoolean());
            assertNull("Not expected properties were returned", properties.getPropertyDecimal());
            assertNull("Not expected properties were returned", properties.getPropertyHtml());
            assertNull("Not expected properties were returned", properties.getPropertyInteger());
            assertNull("Not expected properties were returned", properties.getPropertyUri());
            assertNull("Not expected properties were returned", properties.getPropertyXml());

            assertNotNull("Expected properties were not returned", properties.getPropertyId());
            assertNotNull("Expected properties were not returned", properties.getPropertyDateTime());
            assertNotNull("Expected properties were not returned", properties.getPropertyString());

            assertEquals("Expected property was not returned", 1, properties.getPropertyId().length);
            assertEquals("Expected property was not returned", 1, properties.getPropertyDateTime().length);
            assertEquals("Expected property was not returned", 1, properties.getPropertyString().length);

            getAndAssertIdPropertyValue(properties, EnumPropertiesBase._value2);
            assertNotNull("Expected property was not returned", getStringProperty(properties, EnumPropertiesBase._value1));
            getAndAssertDateTimePropertyValue(properties, EnumPropertiesBase._value6);
        }
    }

    public void testGetDescendantsMultifiled() throws Exception
    {
        if (getAndAssertCapabilities().isCapabilityMultifiling())
        {
            CmisObjectType[] descendantsResponse = null;
            String documentId = createAndAssertDocument();
            LOGGER.info("[MultiFilingService->addObjectToFolder]");
            getServicesFactory().getMultiFilingServicePort().addObjectToFolder(new AddObjectToFolder(getAndAssertRepositoryId(), documentId, folderId));

            try
            {
                LOGGER.info("[NavigationService->getDescendants]");
                descendantsResponse = getServicesFactory().getNavigationService().getDescendants(
                        new GetDescendants(getAndAssertRepositoryId(), getAndAssertRootFolderId(), BigInteger.valueOf(-1), "*", false, EnumIncludeRelationships.none, false, null));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertNotNull("GetDescendants response is NULL", descendantsResponse);
            assertTrue("GetDescendants response is empty", descendantsResponse.length > 0);
            int found = 0;
            for (CmisObjectType objectType : descendantsResponse)
            {
                if (documentId.equals(getIdProperty(objectType.getProperties(), EnumPropertiesBase._value2)))
                {
                    found++;
                }
            }
            assertEquals("Multifiled object was not found in response", 2, found);
            LOGGER.info("[ObjectService->deleteObject]");
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, true));
        }
        else
        {
            LOGGER.info("testGetDescendantsMultifiled was skipped: Multifiling isn't supported");
        }
    }

    public void testGetDescendantsVersionSpecificFiling() throws Exception
    {
        if (isVersioningAllowed() && getAndAssertCapabilities().isCapabilityVersionSpecificFiling())
        {
            CmisObjectType[] descendantsResponse = null;
            String documentId = createAndAssertDocument();
            VersioningServicePortBindingStub versioningService = getServicesFactory().getVersioningService();
            LOGGER.info("[VersioningService->checkOut]");
            CheckOutResponse checkOutResponse = versioningService.checkOut(new CheckOut(getAndAssertRepositoryId(), documentId));
            LOGGER.info("[VersioningService->checkIn]");
            CheckInResponse checkInResponse = versioningService.checkIn(new CheckIn(getAndAssertRepositoryId(), checkOutResponse.getDocumentId(), true, new CmisPropertiesType(),
                    new CmisContentStreamType(BigInteger.valueOf(0), "text/plain", generateTestFileName(), null, "Test content".getBytes(), null), "", null, null, null));
            LOGGER.info("[VersioningService->checkOut]");
            checkOutResponse = versioningService.checkOut(new CheckOut(getAndAssertRepositoryId(), documentId));
            LOGGER.info("[VersioningService->checkIn]");
            versioningService.checkIn(new CheckIn(getAndAssertRepositoryId(), checkOutResponse.getDocumentId(), true, new CmisPropertiesType(), new CmisContentStreamType(
                    BigInteger.valueOf(0), "text/plain", generateTestFileName(), null, "Test content".getBytes(), null), "", null, null, null));

            LOGGER.info("[MultiFilingService->addObjectToFolder]");
            getServicesFactory().getMultiFilingServicePort().addObjectToFolder(new AddObjectToFolder(getAndAssertRepositoryId(), checkInResponse.getDocumentId(), folderId));

            try
            {
                LOGGER.info("[NavigationService->getDescendants]");
                descendantsResponse = getServicesFactory().getNavigationService().getDescendants(
                        new GetDescendants(getAndAssertRepositoryId(), folderId, BigInteger.valueOf(-1), "*", false, EnumIncludeRelationships.none, false, null));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertNotNull("GetDescendants response is NULL", descendantsResponse);
            assertTrue("GetDescendants response is empty", descendantsResponse.length > 0);
            boolean found = false;
            for (CmisObjectType objectType : descendantsResponse)
            {
                if (!found && checkInResponse.getDocumentId().equals(getIdProperty(objectType.getProperties(), EnumPropertiesBase._value2)))
                {
                    found = true;
                    break;
                }
            }
            assertTrue("Specific version of object was not found in response", found);
            LOGGER.info("[ObjectService->deleteObject]");
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, true));
        }
        else
        {
            LOGGER.info("testGetDescendantsVersionSpecificFiling was skipped: Versioning or VersionSpecificFiling isn't supported");
        }
    }

    public void testChildrenReceiving() throws Exception
    {
        List<String> expectedObjects = createAndAssertObjectsTree(folderId, EnumVersioningState.major, EnumTypesOfFileableObjects.BOTH, -1, 1, 1, CHILDREN_TEST_OBJECTS_AMOUNT);
        expectedObjects.add(documentId);

        NavigationServicePortBindingStub navigationService = getServicesFactory().getNavigationService();

        LOGGER.info("[NavigationService->getChildren]");
        GetChildrenResponse childrenResponse = navigationService.getChildren(new GetChildren(getAndAssertRepositoryId(), folderId, "*", false, EnumIncludeRelationships.none,
                false, false, BigInteger.valueOf(0), BigInteger.valueOf(0), null));
        assertNotNull("GetChildren response is NULL", childrenResponse);
        assertObjectsFromResponse(childrenResponse.getObject(), 0, expectedObjects.size());

        assertObjectsTree(expectedObjects, childrenResponse.getObject(), true, "GetChildren service");
    }

    public void testGetChildrenPaigingFunctionality() throws Exception
    {
        List<String> expectedObjects = createAndAssertObjectsTree(folderId, EnumVersioningState.major, EnumTypesOfFileableObjects.BOTH, -1, 1, PAGING_LIMIT, PAGING_LIMIT);
        expectedObjects.add(documentId);

        NavigationServicePortBindingStub navigationService = getServicesFactory().getNavigationService();

        Set<CmisObjectType> actualChildrenResponse = new HashSet<CmisObjectType>();
        int skipCount = PAGING_STEP;
        GetChildrenResponse childrenResponse;

        do
        {
            LOGGER.info("[NavigationService->getChildren]");
            childrenResponse = navigationService.getChildren(new GetChildren(getAndAssertRepositoryId(), folderId, "*", false, EnumIncludeRelationships.none, false, false,
                    BigInteger.valueOf(PAGING_STEP), BigInteger.valueOf(skipCount - PAGING_STEP), null));
            assertNotNull("GetChildren Response with Paging is null", childrenResponse);
            assertNotNull("Returned Response Objects are null", childrenResponse.getObject());
            assertObjectsFromResponse(childrenResponse.getObject(), 0, childrenResponse.getObject().length);

            assertTrue("Paging for GetChildren service works wrongly", (childrenResponse.getObject().length == PAGING_STEP) || (childrenResponse.getObject().length == 1));

            actualChildrenResponse.addAll(Arrays.asList(childrenResponse.getObject()));

            assertTrue("Paging for GetChildren service works wrongly", (skipCount < expectedObjects.size()) ? (childrenResponse.isHasMoreItems()) : (!childrenResponse
                    .isHasMoreItems()));

            skipCount += PAGING_STEP;
        } while (childrenResponse.isHasMoreItems());

        assertObjectsTree(expectedObjects, actualChildrenResponse.toArray(new CmisObjectType[actualChildrenResponse.size()]), true, "GetChildren service with paging");
    }

    public void testGetChildrenVersionSpecificFiling() throws Exception
    {
        if (isVersioningAllowed() && getAndAssertCapabilities().isCapabilityVersionSpecificFiling())
        {
            GetChildrenResponse getChildrenResponse = null;
            String documentId = createAndAssertDocument();
            VersioningServicePortBindingStub versioningService = getServicesFactory().getVersioningService();
            LOGGER.info("[VersioningService->checkOut]");
            CheckOutResponse checkOutResponse = versioningService.checkOut(new CheckOut(getAndAssertRepositoryId(), documentId));
            LOGGER.info("[VersioningService->checkIn]");
            CheckInResponse checkInResponse = versioningService.checkIn(new CheckIn(getAndAssertRepositoryId(), checkOutResponse.getDocumentId(), true, new CmisPropertiesType(),
                    new CmisContentStreamType(BigInteger.valueOf(0), "text/plain", generateTestFileName(), null, "Test content".getBytes(), null), "", null, null, null));
            LOGGER.info("[VersioningService->checkOut]");
            checkOutResponse = versioningService.checkOut(new CheckOut(getAndAssertRepositoryId(), documentId));
            LOGGER.info("[VersioningService->checkIn]");
            versioningService.checkIn(new CheckIn(getAndAssertRepositoryId(), checkOutResponse.getDocumentId(), true, new CmisPropertiesType(), new CmisContentStreamType(
                    BigInteger.valueOf(0), "text/plain", generateTestFileName(), null, "Test content".getBytes(), null), "", null, null, null));

            LOGGER.info("[MultiFilingService->addObjectToFolder]");
            getServicesFactory().getMultiFilingServicePort().addObjectToFolder(new AddObjectToFolder(getAndAssertRepositoryId(), checkInResponse.getDocumentId(), folderId));

            try
            {
                LOGGER.info("[NavigationService->getChildren]");
                getChildrenResponse = getServicesFactory().getNavigationService().getChildren(
                        new GetChildren(getAndAssertRepositoryId(), folderId, "*", true, EnumIncludeRelationships.both, false, false, BigInteger.valueOf(0), BigInteger.valueOf(0),
                                null));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertNotNull("GetChildren response is NULL", getChildrenResponse);
            assertNotNull("GetChildren response is empty", getChildrenResponse.getObject());
            assertTrue("GetChildren response is empty", getChildrenResponse.getObject().length > 0);
            boolean found = false;
            for (CmisObjectType objectType : getChildrenResponse.getObject())
            {
                if (!found && checkInResponse.getDocumentId().equals(getIdProperty(objectType.getProperties(), EnumPropertiesBase._value2)))
                {
                    found = true;
                    break;
                }
            }
            assertTrue("Specific version of object was not found in response", found);
            LOGGER.info("[ObjectService->deleteObject]");
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, true));
        }
        else
        {
            LOGGER.info("testGetChildrenVersionSpecificFiling was skipped: Versioning or VersionSpecificFiling isn't supported");
        }
    }

    public void testFolderParentReceiving() throws Exception
    {
        String childFolder = createAndAssertFolder(generateTestFolderName(), getAndAssertFolderTypeId(), folderId, null);

        NavigationServicePortBindingStub navigationService = getServicesFactory().getNavigationService();
        LOGGER.info("[NavigationService->getFolderParent]");
        GetFolderParentResponse parentsResponse = navigationService.getFolderParent(new GetFolderParent(getAndAssertRepositoryId(), childFolder, "*"));
        assertNotNull("GetParents Response is null", parentsResponse);
        assertObjectsFromResponse(new CmisObjectType[] { parentsResponse.getObject() }, 0, 1);
        assertEquals(folderId, getAndAssertIdPropertyValue(parentsResponse.getObject().getProperties(), EnumPropertiesBase._value2));
    }

    public void testGetFolderParentForRootFolder() throws Exception
    {
        GetFolderParentResponse response = null;
        try
        {
            LOGGER.info("[NavigationService->getFolderParent]");
            response = getServicesFactory().getNavigationService().getFolderParent(new GetFolderParent(getAndAssertRepositoryId(), getAndAssertRootFolderId(), "*"));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        assertNotNull("GetFolderParent Response is NULL", response);
        assertNull("GetFolderParent Response is not empty", response.getObject());

    }

    public void testGetObjectParent() throws Exception
    {
        String documentId = createAndAssertDocument(generateTestFileName(), getAndAssertDocumentTypeId(), folderId, null, TEST_CONTENT, EnumVersioningState.major);

        CmisObjectType[] parentsResponse = null;
        try
        {
            LOGGER.info("[NavigationService->getObjectParents]");
            parentsResponse = getServicesFactory().getNavigationService().getObjectParents(new GetObjectParents(getAndAssertRepositoryId(), documentId, "*"));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        assertNotNull("GetObjectParents response is NULL", parentsResponse);
        assertObjectsFromResponse(parentsResponse, 0, 1);
        assertEquals(folderId, getAndAssertIdPropertyValue(parentsResponse[0].getProperties(), EnumPropertiesBase._value2));
        LOGGER.info("[ObjectService->deleteObject]");
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, true));
    }

    public void testGetObjectParentMultifiled() throws Exception
    {
        if (getAndAssertCapabilities().isCapabilityMultifiling())
        {
            String documentId = createAndAssertDocument();
            LOGGER.info("[MultiFilingService->addObjectToFolder]");
            getServicesFactory().getMultiFilingServicePort().addObjectToFolder(new AddObjectToFolder(getAndAssertRepositoryId(), documentId, folderId));
            CmisObjectType[] parentsResponse = null;
            try
            {
                LOGGER.info("[NavigationService->getObjectParents]");
                parentsResponse = getServicesFactory().getNavigationService().getObjectParents(new GetObjectParents(getAndAssertRepositoryId(), documentId, "*"));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertNotNull("GetObjectParents response is NULL", parentsResponse);
            assertObjectsFromResponse(parentsResponse, 0, 2);
            assertTrue((folderId.equals(getAndAssertIdPropertyValue(parentsResponse[0].getProperties(), EnumPropertiesBase._value2)) || (folderId
                    .equals(getAndAssertIdPropertyValue(parentsResponse[1].getProperties(), EnumPropertiesBase._value2))))
                    && ((getAndAssertRootFolderId().equals(getAndAssertIdPropertyValue(parentsResponse[0].getProperties(), EnumPropertiesBase._value2)) || (getAndAssertRootFolderId()
                            .equals(getAndAssertIdPropertyValue(parentsResponse[1].getProperties(), EnumPropertiesBase._value2))))));
            LOGGER.info("[ObjectService->deleteObject]");
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, true));
        }
        else
        {
            LOGGER.info("testGetObjectParentMultifiled was skipped: Multifiling isn't supported");
        }
    }

    public void testGetObjectParentNotFileable() throws Exception
    {
        String notFileableTypeId = searchAndAssertNotFileableType();
        if (notFileableTypeId != null)
        {
            String objectId = null;
            CmisTypeDefinitionType definitionType = getAndAssertTypeDefinition(notFileableTypeId);
            if (definitionType.getBaseTypeId().equals(EnumBaseObjectTypeIds.value3))
            {
                objectId = createAndAssertRelationship();
            }
            else if (definitionType.getBaseTypeId().equals(EnumBaseObjectTypeIds.value4))
            {
                objectId = createAndAssertPolicy();
            }
            try
            {
                LOGGER.info("[NavigationService->getObjectParents]");
                getServicesFactory().getNavigationService().getObjectParents(new GetObjectParents(getAndAssertRepositoryId(), objectId, "*"));
                fail("No Exception was thrown during getting object parents for not fileable object");
            }
            catch (Exception e)
            {
                assertTrue("Invalid exception was thrown during getting object parents for not fileable object", e instanceof CmisFaultType && ((CmisFaultType) e).getType().equals(EnumServiceException.constraint));
            }
            LOGGER.info("[ObjectService->deleteObject]");
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), objectId, null));
        }

    }

    public void testRelationshipsAndAllowableActionsReceiving() throws Exception
    {
        NavigationServicePortBindingStub navigationService = getServicesFactory().getNavigationService();

        LOGGER.info("[NavigationService->getChildren]");
        GetChildrenResponse childrenResponse = navigationService.getChildren(new GetChildren(getAndAssertRepositoryId(), folderId, "*", true, EnumIncludeRelationships.both, false,
                false, BigInteger.valueOf(0), BigInteger.valueOf(0), null));
        LOGGER.info("[NavigationService->getDescendants]");
        CmisObjectType[] descendantsResponse = navigationService.getDescendants(new GetDescendants(getAndAssertRepositoryId(), folderId, BigInteger.valueOf(1), "*", true,
                EnumIncludeRelationships.both, false, null));
        LOGGER.info("[NavigationService->getObjectParents]");
        CmisObjectType[] objectParentsResponse = navigationService.getObjectParents(new GetObjectParents(getAndAssertRepositoryId(), documentId, "*"));
        LOGGER.info("[NavigationService->getCheckedOutDocs]");
        GetCheckedOutDocsResponse checkedoutDocumentsResponse = navigationService.getCheckedOutDocs(new GetCheckedOutDocs(getAndAssertRepositoryId(), folderId, "*", null, true,
                EnumIncludeRelationships.both, BigInteger.valueOf(0), BigInteger.valueOf(0)));

        List<CmisObjectType> objects = new LinkedList<CmisObjectType>();
        assertAndAddObjectsToList(objects, childrenResponse.getObject(), "GetChildren service");
        assertAndAddObjectsToList(objects, descendantsResponse, "GetDescendants service");
        assertAndAddObjectsToList(objects, objectParentsResponse, "GetObjectParents service");
        assertAndAddObjectsToList(objects, checkedoutDocumentsResponse.getObject(), "GetCheckedoutDocs service");

        for (CmisObjectType object : objects)
        {
            assertNotNull(OBJECT_IS_NULL_MESSAGE, object);
            assertNotNull("Some returned Object Properties are null", object.getProperties());
            assertNotNull("Some returned Object String Properties are null", object.getProperties().getPropertyString());

            assertNotNull("Allowable Actions for Object were not returned", object.getAllowableActions());
            assertNotNull("Relationships Objects for Object were not returned", object.getRelationship());
            assertTrue("No one Relationship Object was returned in Response", object.getRelationship().length > 0);

            if ((object == null) || (object.getRelationship() == null))
            {
                break;
            }

            String id = getAndAssertIdPropertyValue(object.getProperties(), EnumPropertiesBase._value2);

            for (CmisObjectType relationshipObject : object.getRelationship())
            {
                assertNotNull("Some returned Relationship Object is null for object ", relationshipObject);

                String sourceObjectId = getAndAssertIdPropertyValue(relationshipObject.getProperties(), EnumPropertiesRelationship._value1);
                String targetObjectId = getAndAssertIdPropertyValue(relationshipObject.getProperties(), EnumPropertiesRelationship._value2);

                assertTrue("Object response has no any connection with its Relationship Object (it is not Target or Source Object)", id.equals(sourceObjectId)
                        || id.equals(targetObjectId));
            }
        }
    }

    public void testFilteringWithWrongFilter() throws Exception
    {
        NavigationServicePortBindingStub navigationService = getServicesFactory().getNavigationService();

        try
        {
            LOGGER.info("[NavigationService->GetChildren]");
            navigationService.getChildren(new GetChildren(getAndAssertRepositoryId(), folderId, INVALID_FILTER, false, EnumIncludeRelationships.none, false, false, BigInteger
                    .valueOf(0), BigInteger.valueOf(0), null));

            fail(INVALID_WRONG_FILTER_PROCESSING_MESSAGE);
        }
        catch (Throwable e)
        {
            assertTrue(e.toString(), (e instanceof CmisFaultType && ((CmisFaultType) e).getType().equals(EnumServiceException.filterNotValid)));
        }

        try
        {
            LOGGER.info("[NavigationService->GetDescendants]");
            navigationService.getDescendants(new GetDescendants(getAndAssertRepositoryId(), folderId, BigInteger.valueOf(-1), INVALID_FILTER, false, EnumIncludeRelationships.none,
                    false, null));

            fail(INVALID_WRONG_FILTER_PROCESSING_MESSAGE);
        }
        catch (Throwable e)
        {
            assertTrue(e.toString(), (e instanceof CmisFaultType && ((CmisFaultType) e).getType().equals(EnumServiceException.filterNotValid)));
        }

        try
        {
            if (isVersioningAllowed())
            {
                LOGGER.info("[NavigationService->getCheckedOutDocs]");
                navigationService.getCheckedOutDocs(new GetCheckedOutDocs(getAndAssertRepositoryId(), folderId, INVALID_FILTER, null, false, EnumIncludeRelationships.none,
                        BigInteger.valueOf(0), BigInteger.valueOf(0)));
                fail(INVALID_WRONG_FILTER_PROCESSING_MESSAGE);
            }
        }
        catch (Throwable e)
        {
            assertTrue(e.toString(), (e instanceof CmisFaultType && ((CmisFaultType) e).getType().equals(EnumServiceException.filterNotValid)));
        }
    }

    public void testWrongRepositoryIdUsing() throws Exception
    {
        NavigationServicePortBindingStub navigationService = getServicesFactory().getNavigationService();

        try
        {
            if (isVersioningAllowed())
            {
                LOGGER.info("[NavigationService->getCheckedOutDocs]");
                navigationService.getCheckedOutDocs(new GetCheckedOutDocs(INVALID_REPOSITORY_ID, folderId, "*", null, false, EnumIncludeRelationships.none, BigInteger.valueOf(0),
                        BigInteger.valueOf(0)));
                fail("Repository with specified Id was not described with RepositoryService");
            }
        }
        catch (Throwable e)
        {
        }

        try
        {
            LOGGER.info("[NavigationService->getChildren]");
            navigationService.getChildren(new GetChildren(INVALID_REPOSITORY_ID, folderId, "*", false, EnumIncludeRelationships.none, false, false, BigInteger.valueOf(0),
                    BigInteger.valueOf(0), null));

            fail("Repository with specified Id was not described with RepositoryService");
        }
        catch (Throwable e)
        {
        }

        try
        {
            LOGGER.info("[NavigationService->GetDescendants]");
            navigationService.getDescendants(new GetDescendants(INVALID_REPOSITORY_ID, folderId, BigInteger.ONE, "*", false, EnumIncludeRelationships.none, false, null));

            fail("Repository with specified Id was not described with RepositoryService");
        }
        catch (Throwable e)
        {
        }

        try
        {
            LOGGER.info("[NavigationService->getFolderParent]");
            navigationService.getFolderParent(new GetFolderParent(INVALID_REPOSITORY_ID, folderId, "*"));

            fail("Repository with specified Id was not described with RepositoryService");
        }
        catch (Throwable e)
        {
        }

        try
        {
            LOGGER.info("[NavigationService->getObjectParents]");
            navigationService.getObjectParents(new GetObjectParents(INVALID_REPOSITORY_ID, folderId, "*"));

            fail("Repository with specified Id was not described with RepositoryService");
        }
        catch (Throwable e)
        {
        }
    }

    public void testGetCheckedoutDocsDefault()
    {
        if (isVersioningAllowed())
        {
            GetCheckedOutDocsResponse response = null;
            try
            {
                LOGGER.info("[NavigationService->getCheckedOutDocs]");
                response = getServicesFactory().getNavigationService().getCheckedOutDocs(
                        new GetCheckedOutDocs(getAndAssertRepositoryId(), null, null, null, null, null, null, null));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertTrue("No checked out documents were returned", response != null && response.getObject() != null && response.getObject().length > 0);
        }
        else
        {
            LOGGER.info("testGetCheckedoutDocsDefault was skipped: Versioning isn't supported");
        }
    }

    public void testGetCheckedoutDocsFolder() throws Exception
    {
        if (isVersioningAllowed())
        {
            GetCheckedOutDocsResponse response = null;
            String document1Id = createAndAssertDocument();
            LOGGER.info("[VersioningService->checkOut]");
            CheckOutResponse checkOutResponse = getServicesFactory().getVersioningService().checkOut(new CheckOut(getAndAssertRepositoryId(), document1Id));
            try
            {
                LOGGER.info("[NavigationService->getCheckedOutDocs]");
                response = getServicesFactory().getNavigationService().getCheckedOutDocs(
                        new GetCheckedOutDocs(getAndAssertRepositoryId(), getAndAssertRootFolderId(), null, null, null, null, null, null));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertTrue("No checked out documents were returned", response != null && response.getObject() != null && response.getObject().length > 0);
            assertTrue("Number of checked out documents is invalid", response.getObject().length >= 1);

            boolean found = false;
            for (int i = 0; !found && i < response.getObject().length; i++)
            {
                found = checkOutResponse.getDocumentId().equals(getIdProperty(response.getObject()[i].getProperties(), EnumPropertiesBase._value2));
            }
            assertTrue("Not all checked out documents were returned", found);
            try
            {
                LOGGER.info("[NavigationService->getCheckedOutDocs]");
                response = getServicesFactory().getNavigationService().getCheckedOutDocs(
                        new GetCheckedOutDocs(getAndAssertRepositoryId(), null, null, null, null, null, null, null));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertTrue("No checked out documents were returned", response != null && response.getObject() != null && response.getObject().length > 0);
            assertTrue("Number of checked out documents is invalid", response.getObject().length >= 2);

            found = false;
            for (int i = 0; !found && i < response.getObject().length; i++)
            {
                found = checkOutResponse.getDocumentId().equals(getIdProperty(response.getObject()[i].getProperties(), EnumPropertiesBase._value2));
            }
            assertTrue("Not all checked out documents were returned", found);
            LOGGER.info("[VersioningService->cancelCheckOut]");
            getServicesFactory().getVersioningService().cancelCheckOut(new CancelCheckOut(getAndAssertRepositoryId(), checkOutResponse.getDocumentId()));
            deleteAndAssertObject(document1Id);
        }
        else
        {
            LOGGER.info("testGetCheckedoutDocsFolder was skipped: Versioning isn't supported");
        }
    }

    private void assertAndAddObjectsToList(List<CmisObjectType> targetList, CmisObjectType[] objects, String responseType)
    {
        assertNotNull("Response Objects are null for " + responseType, objects);
        assertTrue("No one Object was returned in Response from " + responseType, objects.length > 0);

        if ((objects == null) || (objects.length <= 0))
        {
            return;
        }

        for (CmisObjectType object : objects)
        {
            assertNotNull("Some Expected Object was not found in Response", object);
            assertNotNull("Expected Object properties were not found in Response", object.getProperties());

            CmisPropertyString cmisPropertyString = new CmisPropertyString();
            cmisPropertyString.setPdid(responseType);
            cmisPropertyString.setValue(new String[] { responseType });
            object.getProperties().setPropertyString(new CmisPropertyString[] { cmisPropertyString });
            targetList.add(object);
        }
    }

    private void assertObjectsTree(Collection<String> expectedTree, CmisObjectType[] objects, boolean hasNoneFolderObjects, String sourceName) throws Exception
    {
        assertNotNull("Some Expected Object was not found in Response", objects);
        assertTrue("Some Expected Object was not found in Response", objects.length > 0);

        List<CmisObjectType> actualObjectsList = new LinkedList<CmisObjectType>(Arrays.asList(objects));

        for (String objectId : expectedTree)
        {
            CmisObjectType object = searchObjectById(actualObjectsList, objectId);

            assertNotNull("Some Expected Object was not found in Response", object);

            actualObjectsList.remove(object);
        }

        if (getAndAssertCapabilities().isCapabilityVersionSpecificFiling() && hasNoneFolderObjects)
        {
            assertTrue((INVALID_OBJECTS_INTEGRITY_MESSAGE + sourceName), (objects.length >= expectedTree.size()));
        }
        else
        {
            assertEquals((INVALID_OBJECTS_INTEGRITY_MESSAGE + sourceName), expectedTree.size(), objects.length);
        }
    }

    private Date getAndAssertDateTimePropertyValue(CmisPropertiesType properties, String propertyName)
    {
        assertNotNull("Properties are null", properties);
        assertNotNull("DateTime properties are null", properties.getPropertyDateTime());

        if ((propertyName == null) || (properties == null) || (properties.getPropertyId() == null))
        {
            return null;
        }

        for (CmisPropertyDateTime property : properties.getPropertyDateTime())
        {
            assertNotNull("One of the DateTime properties is null", property);
            assertNotNull("Property DateTime Name is null", property.getPdid());

            if (propertyName.equals(property.getPdid()))
            {
                assertNotNull("Property DateTime Value is null", property.getValue());
                return property.getValue(0).getTime();
            }
        }
        return null;
    }

    private void assertObjectsFromResponse(CmisObjectType[] objects, int startIndex, int length) throws Exception
    {
        assertNotNull("Objects from response are null", objects);
        assertTrue("No one Object was returned in response", objects.length > 0);

        int endIndex = startIndex + length;

        assertTrue(INVALID_OBJECTS_LAYERS_DESCRIPTION_MESSAGE, ((startIndex >= 0) && (startIndex < objects.length)));
        assertTrue(INVALID_OBJECTS_LAYERS_DESCRIPTION_MESSAGE, (endIndex <= objects.length));

        if ((startIndex < 0) || (startIndex >= objects.length) || (endIndex > objects.length))
        {
            return;
        }

        for (int i = startIndex; i < endIndex; i++)
        {
            assertNotNull(OBJECT_IS_NULL_MESSAGE, objects[i]);

            if (objects[i] == null)
            {
                return;
            }
        }
    }

    private CmisObjectType searchObjectById(List<CmisObjectType> objects, String objectId)
    {
        if ((objectId == null) || (objects == null))
        {
            return null;
        }

        for (CmisObjectType object : objects)
        {
            assertNotNull(OBJECT_IS_NULL_MESSAGE, object);

            if (object != null)
            {
                String id = getAndAssertIdPropertyValue(object.getProperties(), EnumPropertiesBase._value2);

                if (objectId.equals(id))
                {
                    return object;
                }
            }
        }

        return null;
    }

    private String getAndAssertIdPropertyValue(CmisPropertiesType properties, String propertyName)
    {
        assertNotNull("Properties are null", properties);
        assertNotNull("Id properties are null", properties.getPropertyId());

        if ((propertyName == null) || (properties == null) || (properties.getPropertyId() == null))
        {
            return null;
        }

        for (CmisPropertyId property : properties.getPropertyId())
        {
            assertNotNull("One of the Id properties is null", property);
            assertNotNull("Property Id Name is null", property.getPdid());

            if (propertyName.equals(property.getPdid()))
            {
                assertNotNull("Property Id Value is null", property.getValue());
                return property.getValue(0);
            }
        }
        return null;
    }

}
