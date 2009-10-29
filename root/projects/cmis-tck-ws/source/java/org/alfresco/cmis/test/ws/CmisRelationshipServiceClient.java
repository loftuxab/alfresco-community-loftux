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

import org.alfresco.repo.cmis.ws.CmisObjectType;
import org.alfresco.repo.cmis.ws.CmisTypeDefinitionType;
import org.alfresco.repo.cmis.ws.DeleteObject;
import org.alfresco.repo.cmis.ws.EnumBaseObjectTypeIds;
import org.alfresco.repo.cmis.ws.EnumIncludeRelationships;
import org.alfresco.repo.cmis.ws.EnumRelationshipDirection;
import org.alfresco.repo.cmis.ws.GetRelationships;
import org.alfresco.repo.cmis.ws.GetRelationshipsResponse;
import org.alfresco.repo.cmis.ws.RelationshipServicePort;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CmisRelationshipServiceClient extends AbstractServiceClient
{
    private static Log LOGGER = LogFactory.getLog(CmisRelationshipServiceClient.class);

    private static final String INVALID_REPOSITORY_ID = "Wrong Repository Id";

    private String[] sourceIds = new String[3];
    private String[] targetIds = new String[3];

    public CmisRelationshipServiceClient(AbstractService abstractService)
    {
        super(abstractService);
    }

    public CmisRelationshipServiceClient()
    {
    }

    /**
     * Initializes Relationship Service client
     */
    @Override
    public void initialize() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Initializing client...");
        }

        CmisTypeDefinitionType sourceType = getAndAssertRelationshipSourceType();

        if (sourceType != null && EnumBaseObjectTypeIds.value2.equals(sourceType.getBaseTypeId()))
        {
            sourceIds[0] = createAndAssertFolder(generateTestFolderName("_1"), sourceType.getId(), getAndAssertRootFolderId(), null);
            sourceIds[1] = createAndAssertFolder(generateTestFolderName("_2"), sourceType.getId(), getAndAssertRootFolderId(), null);
            sourceIds[2] = createAndAssertFolder(generateTestFolderName("_3"), sourceType.getId(), getAndAssertRootFolderId(), null);
        }
        else
        {
            sourceIds[0] = createAndAssertDocument(generateTestFileName("_1"), sourceType == null ? getAndAssertDocumentTypeId() : sourceType.getId(), getAndAssertRootFolderId(),
                    null, TEST_CONTENT, null);
            sourceIds[1] = createAndAssertDocument(generateTestFileName("_2"), sourceType == null ? getAndAssertDocumentTypeId() : sourceType.getId(), getAndAssertRootFolderId(),
                    null, TEST_CONTENT, null);
            sourceIds[2] = createAndAssertDocument(generateTestFileName("_3"), sourceType == null ? getAndAssertDocumentTypeId() : sourceType.getId(), getAndAssertRootFolderId(),
                    null, TEST_CONTENT, null);
        }

        CmisTypeDefinitionType targetType = getAndAssertRelationshipTargetType();

        if (targetType != null && EnumBaseObjectTypeIds.value2.equals(targetType.getBaseTypeId())
                && (sourceType != null && !EnumBaseObjectTypeIds.value2.equals(sourceType.getBaseTypeId())))
        {
            targetIds[0] = createAndAssertFolder(generateTestFolderName("_1"), targetType.getId(), getAndAssertRootFolderId(), null);
            targetIds[1] = createAndAssertFolder(generateTestFolderName("_2"), targetType.getId(), getAndAssertRootFolderId(), null);
            targetIds[2] = createAndAssertFolder(generateTestFolderName("_3"), targetType.getId(), getAndAssertRootFolderId(), null);
        }
        else
        {
            targetIds = sourceIds;
        }

        createAndAssertRelationship(sourceIds[0], targetIds[0]);
        createAndAssertRelationship(sourceIds[0], targetIds[1]);
        createAndAssertRelationship(sourceIds[0], targetIds[2]);
        createAndAssertRelationship(sourceIds[1], targetIds[2]);
    }

    /**
     * Invokes all methods in Relationship Service
     */
    @Override
    public void invoke() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Invoking client...");
        }
        RelationshipServicePort relationshipService = getServicesFactory().getRelationshipService(getProxyUrl() + getService().getPath());

        relationshipService.getRelationships(new GetRelationships(getAndAssertRepositoryId(), sourceIds[0], EnumRelationshipDirection.either, "Relationship", true, "*", false,
                EnumIncludeRelationships.both, BigInteger.valueOf(0), BigInteger.valueOf(0)));
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
    }

    @Override
    protected void onSetUp() throws Exception
    {
        CmisTypeDefinitionType sourceType = getAndAssertRelationshipSourceType();

        if (sourceType != null && EnumBaseObjectTypeIds.value2.equals(sourceType.getBaseTypeId()))
        {
            sourceIds[0] = createAndAssertFolder(generateTestFolderName("_1"), sourceType.getId(), getAndAssertRootFolderId(), null);
            sourceIds[1] = createAndAssertFolder(generateTestFolderName("_2"), sourceType.getId(), getAndAssertRootFolderId(), null);
            sourceIds[2] = createAndAssertFolder(generateTestFolderName("_3"), sourceType.getId(), getAndAssertRootFolderId(), null);
        }
        else
        {
            sourceIds[0] = createAndAssertDocument(generateTestFileName("_1"), sourceType == null ? getAndAssertDocumentTypeId() : sourceType.getId(), getAndAssertRootFolderId(),
                    null, TEST_CONTENT, null);
            sourceIds[1] = createAndAssertDocument(generateTestFileName("_2"), sourceType == null ? getAndAssertDocumentTypeId() : sourceType.getId(), getAndAssertRootFolderId(),
                    null, TEST_CONTENT, null);
            sourceIds[2] = createAndAssertDocument(generateTestFileName("_3"), sourceType == null ? getAndAssertDocumentTypeId() : sourceType.getId(), getAndAssertRootFolderId(),
                    null, TEST_CONTENT, null);
        }

        CmisTypeDefinitionType targetType = getAndAssertRelationshipTargetType();

        if (targetType != null && EnumBaseObjectTypeIds.value2.equals(targetType.getBaseTypeId())
                && (sourceType != null && !EnumBaseObjectTypeIds.value2.equals(sourceType.getBaseTypeId())))
        {
            targetIds[0] = createAndAssertFolder(generateTestFolderName("_1"), targetType.getId(), getAndAssertRootFolderId(), null);
            targetIds[1] = createAndAssertFolder(generateTestFolderName("_2"), targetType.getId(), getAndAssertRootFolderId(), null);
            targetIds[2] = createAndAssertFolder(generateTestFolderName("_3"), targetType.getId(), getAndAssertRootFolderId(), null);
        }
        else
        {
            targetIds = sourceIds;
        }

        createAndAssertRelationship(sourceIds[0], targetIds[0]);
        createAndAssertRelationship(sourceIds[0], targetIds[1]);
        createAndAssertRelationship(sourceIds[0], targetIds[2]);
        createAndAssertRelationship(sourceIds[1], targetIds[2]);
        super.onSetUp();
    }

    @Override
    protected void onTearDown() throws Exception
    {
        for (int i = 0; i < sourceIds.length; i++)
        {
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), sourceIds[i], true));
        }
        if (!targetIds[0].equals(sourceIds[0]))
        {
            for (int i = 0; i < targetIds.length; i++)
            {
                getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), targetIds[i], true));
            }
        }
        super.onTearDown();
    }

    public void testGetRelationshipsDefault()
    {
        try
        {
            LOGGER.info("[RelationshipService->getRelationships]");
            GetRelationshipsResponse relationshipsResponse = getServicesFactory().getRelationshipService().getRelationships(
                    new GetRelationships(getAndAssertRepositoryId(), sourceIds[0], null, null, null, null, null, null, null, null));
            assertNotNull("GetRelationships response is NULL", relationshipsResponse);
            assertNotNull("GetRelationships response is NULL", relationshipsResponse.getObject());
            assertEquals("Invalid relationships amount was returned", 3, relationshipsResponse.getObject().length);
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
    }

    public void testGetRelationshipsSource()
    {
        try
        {
            LOGGER.info("[RelationshipService->getRelationships]");
            GetRelationshipsResponse relationshipsResponse = getServicesFactory().getRelationshipService().getRelationships(
                    new GetRelationships(getAndAssertRepositoryId(), sourceIds[0], EnumRelationshipDirection.source, null, null, null, null, null, null, null));
            assertNotNull("GetRelationships response is NULL", relationshipsResponse);
            assertNotNull("GetRelationships response is NULL", relationshipsResponse.getObject());
            assertEquals("Invalid relationships amount was returned", 3, relationshipsResponse.getObject().length);
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
    }

    public void testGetRelationshipsTarget()
    {
        try
        {
            LOGGER.info("[RelationshipService->getRelationships]");
            GetRelationshipsResponse relationshipsResponse = getServicesFactory().getRelationshipService().getRelationships(
                    new GetRelationships(getAndAssertRepositoryId(), targetIds[2], EnumRelationshipDirection.target, null, null, null, null, null, null, null));
            assertNotNull("GetRelationships response is NULL", relationshipsResponse);
            assertNotNull("GetRelationships response is NULL", relationshipsResponse.getObject());
            assertEquals("Invalid relationships amount was returned", 2, relationshipsResponse.getObject().length);
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
    }

    public void testGetRelationshipsBoth()
    {
        try
        {
            LOGGER.info("[RelationshipService->getRelationships]");
            GetRelationshipsResponse relationshipsResponse = getServicesFactory().getRelationshipService().getRelationships(
                    new GetRelationships(getAndAssertRepositoryId(), sourceIds[1], EnumRelationshipDirection.either, null, null, null, null, null, null, null));
            assertNotNull("GetRelationships response is NULL", relationshipsResponse);
            assertNotNull("GetRelationships response is NULL", relationshipsResponse.getObject());
            assertTrue("Invalid relationships amount was returned", (2 == relationshipsResponse.getObject().length && sourceIds[1].equals(targetIds[1]))
                    || (1 == relationshipsResponse.getObject().length && !sourceIds[1].equals(targetIds[1])));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
    }

    public void testGetRelationshipsFilter()
    {
        try
        {
            LOGGER.info("[RelationshipService->getRelationships]");
            GetRelationshipsResponse relationshipsResponse = getServicesFactory().getRelationshipService().getRelationships(
                    new GetRelationships(getAndAssertRepositoryId(), sourceIds[0], null, null, null, "Name, ObjectId", null, null, null, null));
            assertNotNull("GetRelationships response is NULL", relationshipsResponse);
            assertNotNull("GetRelationships response is NULL", relationshipsResponse.getObject());
            assertEquals("Invalid relationships amount was returned", 3, relationshipsResponse.getObject().length);
            for (CmisObjectType objectType : relationshipsResponse.getObject())
            {
                assertNotNull("Some relationships properties were not returned", objectType.getProperties());

                assertNull("Not expected properties were returned", objectType.getProperties().getPropertyBoolean());
                assertNull("Not expected properties were returned", objectType.getProperties().getPropertyDecimal());
                assertNull("Not expected properties were returned", objectType.getProperties().getPropertyHtml());
                assertNull("Not expected properties were returned", objectType.getProperties().getPropertyInteger());
                assertNull("Not expected properties were returned", objectType.getProperties().getPropertyUri());
                assertNull("Not expected properties were returned", objectType.getProperties().getPropertyXml());
                assertNull("Not expected properties were returned", objectType.getProperties().getPropertyDateTime());

                assertNotNull("Expected properties were not returned", objectType.getProperties().getPropertyId());
                assertNotNull("Expected properties were not returned", objectType.getProperties().getPropertyString());
                assertNotNull("Expected properties were not returned", objectType.getProperties().getPropertyBoolean());

                assertEquals("Expected property was not returned", 1, objectType.getProperties().getPropertyId().length);
                assertEquals("Expected property was not returned", 1, objectType.getProperties().getPropertyString().length);

                assertNotNull("Expected property was not returned", getIdProperty(objectType.getProperties(), "ObjectId"));
                assertNotNull("Expected property was not returned", getStringProperty(objectType.getProperties(), "Name"));
            }
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
    }

    public void testWrongRepositoryIdUsing() throws Exception
    {
        try
        {
            LOGGER.info("[RelationshipService->getRelationships]");
            getServicesFactory().getRelationshipService().getRelationships(
                    new GetRelationships(INVALID_REPOSITORY_ID, sourceIds[0], null, null, null, null, null, null, null, null));
            fail("Repository with specified Id was not described with RepositoryService");
        }
        catch (Exception e)
        {
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
        AbstractServiceClient client = (CmisRelationshipServiceClient) applicationContext.getBean("cmisRelationshipServiceClient");
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
}
