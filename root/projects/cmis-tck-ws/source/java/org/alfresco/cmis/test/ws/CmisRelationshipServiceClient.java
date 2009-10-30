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

import org.alfresco.repo.cmis.ws.CmisObjectType;
import org.alfresco.repo.cmis.ws.CmisTypeDefinitionType;
import org.alfresco.repo.cmis.ws.EnumRelationshipDirection;
import org.alfresco.repo.cmis.ws.EnumServiceException;
import org.alfresco.repo.cmis.ws.GetObjectRelationships;
import org.alfresco.repo.cmis.ws.GetObjectRelationshipsResponse;
import org.alfresco.repo.cmis.ws.RelationshipServicePort;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CmisRelationshipServiceClient extends AbstractServiceClient
{
    private static Log LOGGER = LogFactory.getLog(CmisRelationshipServiceClient.class);

    private String sourceId;
    private String targetId;

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
        CmisTypeDefinitionType targetType = getAndAssertRelationshipTargetType();

        if ((null == sourceType) || (null == targetType))
        {
            throw new Exception("Relationship Service can't be tested because no one Relationship Type with appropriate Source and Target Object Type Ids was found");
        }

        sourceId = createRelationshipParticipants(sourceType, 1)[0];
        targetId = createRelationshipParticipants(targetType, 1)[0];

        createAndAssertRelationship(sourceId, targetId, getAndAssertRelationshipTypeId());
        for (CmisTypeDefinitionType relationshipType : getRelationshipSubTypes())
        {
            createAndAssertRelationship(sourceId, targetId, relationshipType.getId());
        }
    }

    private String[] createRelationshipParticipants(CmisTypeDefinitionType typeDef, int amount) throws Exception
    {
        String[] result = new String[amount];
        boolean folder = BASE_TYPE_FOLDER.equals(typeDef.getBaseId());
        for (int i = 0; i < amount; i++)
        {
            if (folder)
            {
                result[i] = createAndAssertFolder(generateTestFolderName(), typeDef.getId(), getAndAssertRootFolderId(), null);
            }
            else
            {
                result[i] = createAndAssertDocument(generateTestFileName(), typeDef.getId(), getAndAssertRootFolderId(), null, null, null);
            }
        }
        return result;
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

        relationshipService.getObjectRelationships(new GetObjectRelationships(getAndAssertRepositoryId(), sourceId, false, EnumRelationshipDirection.either, "Relationship", "*", false,
                BigInteger.valueOf(0), BigInteger.valueOf(0), null));
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
        initialize();
        super.onSetUp();
    }

    @Override
    protected void onTearDown() throws Exception
    {
        try
        {
            deleteAndAssertObject(sourceId, true);
        }
        catch (Exception e)
        {
            LOGGER.error(e.toString());
        }
        try
        {
            deleteAndAssertObject(targetId, true);
        }
        catch (Exception e)
        {
            LOGGER.error(e.toString());
        }
        super.onTearDown();
    }

    public void testGetRelationships()
    {
        GetObjectRelationshipsResponse relationshipsResponse = getAndAssertObjectRelationships(sourceId, false, null, null, null, false, null, null);
        assertEquals("Invalid relationships amount was returned", relationshipsResponse.getObjects().getObjects().length <= (getRelationshipSubTypes().size() + 1));
    }

    public void testGetRelationshipsSource()
    {
        GetObjectRelationshipsResponse relationshipsResponse = getAndAssertObjectRelationships(sourceId, false, EnumRelationshipDirection.source, null, null, false, null, null);
        assertTrue("Invalid relationships amount was returned", relationshipsResponse.getObjects().getObjects().length <= (getRelationshipSubTypes().size() + 1));
    }

    public void testGetRelationshipsTarget()
    {
        GetObjectRelationshipsResponse relationshipsResponse = getAndAssertObjectRelationships(targetId, false, EnumRelationshipDirection.target, null, null, false, null, null);
        assertTrue("Invalid relationships amount was returned", relationshipsResponse.getObjects().getObjects().length <= (getRelationshipSubTypes().size() + 1));
    }

    public void testGetRelationshipsBoth()
    {
        GetObjectRelationshipsResponse sourceRelationshipsResponse = getAndAssertObjectRelationships(sourceId, false, EnumRelationshipDirection.either, null, null, false, null, null);
        GetObjectRelationshipsResponse targetRelationshipsResponse = getAndAssertObjectRelationships(targetId, false, EnumRelationshipDirection.either, null, null, false, null, null);
        int amount = getRelationshipSubTypes().size() + 1;
        assertTrue(amount >= sourceRelationshipsResponse.getObjects().getObjects().length);
        assertTrue(amount >= targetRelationshipsResponse.getObjects().getObjects().length);
        assertTrue("Relationships collections for Source and Target Objects are not equal", Arrays.equals(sourceRelationshipsResponse.getObjects().getObjects(), targetRelationshipsResponse.getObjects().getObjects()));
    }

    public void testGetRelationshipsFilter() throws Exception
    {
        GetObjectRelationshipsResponse relationshipsResponse = getAndAssertObjectRelationships(sourceId, false, EnumRelationshipDirection.either, null, "cmis:name, cmis:objectId", false, null, null);
        assertTrue("Invalid relationships amount was returned", relationshipsResponse.getObjects().getObjects().length <= (getRelationshipSubTypes().size() + 1));
        for (CmisObjectType objectType : relationshipsResponse.getObjects().getObjects())
        {
            assertNotNull("Some relationships properties were not returned", objectType.getProperties());

            assertNull("Not expected properties were returned", objectType.getProperties().getPropertyBoolean());
            assertNull("Not expected properties were returned", objectType.getProperties().getPropertyDecimal());
            assertNull("Not expected properties were returned", objectType.getProperties().getPropertyHtml());
            assertNull("Not expected properties were returned", objectType.getProperties().getPropertyInteger());
            assertNull("Not expected properties were returned", objectType.getProperties().getPropertyUri());
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

    public void testGetRelationshipsForWrongObjectId() throws Exception
    {
        try
        {
            getAndAssertObjectRelationships("Wrong Object Id", false, EnumRelationshipDirection.either, null, "*", false, null, null);
            fail("Relationships were returned for Invalid Object Id");
        }
        catch (Exception e)
        {
            assertException("Object Relationships Receiving for Invlaid Object Id", e, EnumServiceException.invalidArgument);
        }
    }

    public void testGetRelationshipsWithAllowableActions() throws Exception
    {
        GetObjectRelationshipsResponse objectRelationships = getAndAssertObjectRelationships(sourceId, false, EnumRelationshipDirection.either, null, "*", true, null, null);
        for (CmisObjectType relationship : objectRelationships.getObjects().getObjects())
        {
            assertNotNull("Invalid Relationships collection! No one Relationship Object may be undefined solely", relationship);
            assertNotNull("Allowable Actions for one of the Relationship Object was not returned", relationship.getAllowableActions());
            assertTrue("Allowable Actions define that Relationship Object Properties can't be read by current user", relationship.getAllowableActions().getCanGetProperties());
        }
    }

    private GetObjectRelationshipsResponse getAndAssertObjectRelationships(String objectId, boolean allowSubTypes, EnumRelationshipDirection direction, String typeId,
            String filter, boolean includeAllowableActions, Long maxItems, Long skipCount)
    {
        GetObjectRelationshipsResponse objectRelationships = null;
        try
        {
            LOGGER.info("[RelationshipService->getRelationships]");
            objectRelationships = getServicesFactory().getRelationshipService().getObjectRelationships(
                    new GetObjectRelationships(getAndAssertRepositoryId(), objectId, allowSubTypes, direction, typeId, filter, includeAllowableActions, BigInteger
                            .valueOf(maxItems), BigInteger.valueOf(skipCount), null));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        assertNotNull("Get Object Relationships response is undefined", objectRelationships);
        assertNotNull("Get Object Relationships response is undefined", objectRelationships.getObjects());
        assertNotNull("Get Object Relationships response is empty", objectRelationships.getObjects().getObjects());
        return objectRelationships;
    }

    public void testGetRelationshipsPagination() throws Exception
    {
        GetObjectRelationshipsResponse allRelationships = getAndAssertObjectRelationships(sourceId, true, EnumRelationshipDirection.either, null, "*", false, null, null);
        long minimalPossibleElementsAmount = allRelationships.getObjects().getObjects().length;
        minimalPossibleElementsAmount = (minimalPossibleElementsAmount > 10) ? (10):(minimalPossibleElementsAmount - 1);
        GetObjectRelationshipsResponse objectRelationships = getAndAssertObjectRelationships(sourceId, false, EnumRelationshipDirection.either, null, "*", false, minimalPossibleElementsAmount, 0L);
        assertEquals(minimalPossibleElementsAmount, objectRelationships.getObjects().getObjects().length);
        if (null != objectRelationships.getObjects().getNumItems())
        {
            assertEquals(minimalPossibleElementsAmount, objectRelationships.getObjects().getNumItems().longValue());
        }
        if (0 != minimalPossibleElementsAmount)
        {
            assertTrue("Has More Items must not be equal to 'false'", objectRelationships.getObjects().isHasMoreItems());
        }
        else
        {
            assertFalse("Has More Items must not be equal to 'true'", objectRelationships.getObjects().isHasMoreItems());
        }
    }

    public void testGetRelationshipsAgainstSubTypes() throws Exception
    {
        GetObjectRelationshipsResponse relationships = getAndAssertObjectRelationships(sourceId, false, EnumRelationshipDirection.either, getAndAssertRelationshipTypeId(), "*",
                false, null, null);
        assertEquals(1, relationships.getObjects().getObjects().length);
        GetObjectRelationshipsResponse allRelationships = getAndAssertObjectRelationships(sourceId, false, EnumRelationshipDirection.either, null, "*", false, null, null);
        assertTrue("No one Relationship Object was returned", allRelationships.getObjects().getObjects().length >= 1);
        if (allRelationships.getObjects().getObjects().length > 1)
        {
            CmisTypeDefinitionType[] relationshipTypesArray = getRelationshipSubTypes().toArray(new CmisTypeDefinitionType[getRelationshipSubTypes().size()]);
            String typeId = relationshipTypesArray[relationshipTypesArray.length % 2].getId();
            relationships = getAndAssertObjectRelationships(sourceId, true, EnumRelationshipDirection.either, typeId, "*", false, null, null);
            assertTrue("No one Sub Relationship Object was returned", relationships.getObjects().getObjects().length >= 1);
            assertTrue("Relationships amount for Descendant Type Id is greater than not restricted Relationships amount",
                    relationships.getObjects().getObjects().length < allRelationships.getObjects().getObjects().length);
        }
    }

    public void testWrongRepositoryIdUsing() throws Exception
    {
        try
        {
            LOGGER.info("[RelationshipService->getRelationships]");
            getServicesFactory().getRelationshipService().getObjectRelationships(
                    new GetObjectRelationships(INVALID_REPOSITORY_ID, sourceId, false, null, null, null, false, null, null, null));
            fail("Repository with specified Id was not described with RepositoryService");
        }
        catch (Exception e)
        {
            assertException("Object Relationships Receiving with Invalid Repository Id", e, EnumServiceException.invalidArgument);
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
