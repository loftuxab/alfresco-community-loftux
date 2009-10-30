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
import java.util.HashSet;
import java.util.Set;

import org.alfresco.repo.cmis.ws.CancelCheckOut;
import org.alfresco.repo.cmis.ws.CheckInResponse;
import org.alfresco.repo.cmis.ws.CheckOutResponse;
import org.alfresco.repo.cmis.ws.CmisObjectType;
import org.alfresco.repo.cmis.ws.CmisPropertiesType;
import org.alfresco.repo.cmis.ws.CmisRepositoryCapabilitiesType;
import org.alfresco.repo.cmis.ws.DiscoveryServicePortBindingStub;
import org.alfresco.repo.cmis.ws.EnumCapabilityChanges;
import org.alfresco.repo.cmis.ws.EnumCapabilityQuery;
import org.alfresco.repo.cmis.ws.EnumIncludeRelationships;
import org.alfresco.repo.cmis.ws.EnumServiceException;
import org.alfresco.repo.cmis.ws.GetContentChanges;
import org.alfresco.repo.cmis.ws.GetContentChangesResponse;
import org.alfresco.repo.cmis.ws.Query;
import org.alfresco.repo.cmis.ws.QueryResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Client for Discovery Service
 * 
 * @author Dmitry Velichkevich
 */
public class CmisDiscoveryServiceClient extends AbstractServiceClient
{
    private static Log LOGGER = LogFactory.getLog(CmisDiscoveryServiceClient.class);

    private String[] documentsIds = new String[11];

    public CmisDiscoveryServiceClient(AbstractService abstractService)
    {
        super(abstractService);
    }

    public CmisDiscoveryServiceClient()
    {
    }

    @Override
    public void initialize() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Initializing client...");
        }
    }

    /**
     * Invokes all methods in Discovery Service
     */
    @Override
    public void invoke() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Invoking client...");
        }
        DiscoveryServicePortBindingStub discoveryServicePort = getServicesFactory().getDiscoveryService(getProxyUrl() + getService().getPath());
        Query request = new Query();
        request.setRepositoryId(getAndAssertRepositoryId());
        request.setMaxItems(BigInteger.valueOf(10));
        request.setStatement("SELECT * FROM " + BASE_TYPE_DOCUMENT.getValue());
        discoveryServicePort.query(request);
        discoveryServicePort.getContentChanges(new GetContentChanges(getAndAssertRepositoryId(), null, false, "*", false, false, BigInteger.TEN, null));
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
    public static void main(String[] args)
    {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:wsi-context.xml");
        AbstractServiceClient client = (CmisDiscoveryServiceClient) applicationContext.getBean("cmisDiscoveryServiceClient");
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
        for (int i = 0; i < documentsIds.length; i++)
        {
            documentsIds[i] = createAndAssertDocument();
        }
    }

    @Override
    protected void onTearDown() throws Exception
    {
        super.onTearDown();
        for (int i = 0; i < documentsIds.length; i++)
        {
            deleteAndAssertObject(documentsIds[i]);
        }
    }

    public void testQueryAll() throws Exception
    {
        if (EnumCapabilityQuery.none.equals(getAndAssertCapabilities().getCapabilityQuery())
                || EnumCapabilityQuery.fulltextonly.equals(getAndAssertCapabilities().getCapabilityQuery()))
        {
            LOGGER.warn("testQueryAll() was skipped: Metadata query isn't supported");
        }
        else
        {
            String statement = "SELECT * FROM " + BASE_TYPE_DOCUMENT.getValue();
            try
            {
                QueryResponse queryResponse = queryAndAssert(statement, false, false, null, null, 10L, null);
                assertTrue("Query response contains more objects than was expected", queryResponse.getObjects().getObjects().length <= 10);
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
        }
    }

    public void testQueryOrder() throws Exception
    {
        if (EnumCapabilityQuery.none.equals(getAndAssertCapabilities().getCapabilityQuery())
                || EnumCapabilityQuery.fulltextonly.equals(getAndAssertCapabilities().getCapabilityQuery()))
        {
            LOGGER.warn("testQueryOrder() was skipped: Metadata query isn't supported");
        }
        else
        {
            String statement = "SELECT * FROM " + BASE_TYPE_DOCUMENT.getValue() + " ORDER BY " + PROP_NAME + " ASC";
            try
            {
                QueryResponse queryResponse = queryAndAssert(statement, false, false, EnumIncludeRelationships.none, null, 10L, null);
                assertTrue("Query response contains more objects than was expected", queryResponse.getObjects().getObjects().length <= 10);
                String name1 = getStringProperty(queryResponse.getObjects().getObjects()[0].getProperties(), PROP_NAME);
                String name2 = getStringProperty(queryResponse.getObjects().getObjects()[queryResponse.getObjects().getObjects().length - 1].getProperties(), PROP_NAME);
                assertTrue("Query response objects are not ordered", name1.compareTo(name2) <= 0);
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
        }
    }

    private QueryResponse queryAndAssert(String statement, boolean searchAllVersions, boolean includeAllowableActions, EnumIncludeRelationships includeRelationships,
            String renditionFilter, Long maxItems, Long skipCount) throws Exception
    {
        QueryResponse queryResponse = null;
        try
        {
            LOGGER.info("[DiscoveryService->query]");
            queryResponse = getServicesFactory().getDiscoveryService().query(
                    new Query(getAndAssertRepositoryId(), statement, searchAllVersions, includeAllowableActions, includeRelationships, renditionFilter, BigInteger
                            .valueOf(maxItems), BigInteger.valueOf(skipCount), null));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        assertNotNull("Query response is NULL", queryResponse);
        assertNotNull("Query response is NULL", queryResponse.getObjects());
        assertNotNull("Query response is NULL", queryResponse.getObjects().getObjects());
        return queryResponse;
    }

    public void testQueryField() throws Exception
    {
        if (EnumCapabilityQuery.none.equals(getAndAssertCapabilities().getCapabilityQuery())
                || EnumCapabilityQuery.fulltextonly.equals(getAndAssertCapabilities().getCapabilityQuery()))
        {
            LOGGER.warn("testQueryField() was skipped: Metadata query isn't supported");
        }
        else
        {
            String statement = "SELECT " + PROP_NAME + " FROM " + BASE_TYPE_DOCUMENT.getValue();
            QueryResponse queryResponse = queryAndAssert(statement, false, false, null, null, 10L, null);
            assertTrue("Query response contains more objects than was expected", queryResponse.getObjects().getObjects().length <= 10);
            for (CmisObjectType object : queryResponse.getObjects().getObjects())
            {
                assertNotNull("Invalid Query response: one of the result Objects is in 'not set' state solely", object);
                CmisPropertiesType properties = object.getProperties();
                assertNotNull("Object Properties are undefined", properties);
                int amount = properties.getPropertyBoolean().length + properties.getPropertyDateTime().length + properties.getPropertyDecimal().length
                        + properties.getPropertyHtml().length + properties.getPropertyId().length + properties.getPropertyInteger().length + properties.getPropertyString().length
                        + properties.getPropertyUri().length;
                assertEquals(1, amount);
            }
        }
    }

    public void testQueryPagination() throws Exception
    {
        if (EnumCapabilityQuery.none.equals(getAndAssertCapabilities().getCapabilityQuery())
                || EnumCapabilityQuery.fulltextonly.equals(getAndAssertCapabilities().getCapabilityQuery()))
        {
            LOGGER.warn("testQueryOffset() was skipped: Metadata query isn't supported");
        }
        else
        {
            CmisObjectType objectType1 = null;
            CmisObjectType objectType2 = null;
            String statement = "SELECT * FROM " + BASE_TYPE_DOCUMENT.getValue();
            try
            {
                QueryResponse queryResponse = queryAndAssert(statement, false, false, null, null, 2L, null);
                assertTrue("Query response contains more objects than was expected", queryResponse.getObjects().getObjects().length <= 2);
                objectType1 = queryResponse.getObjects().getObjects(1);
                queryResponse = queryAndAssert(statement, false, false, null, null, 1L, 1L);
                assertTrue("Query response contains more objects than was expected", queryResponse.getObjects().getObjects().length <= 1);
                objectType2 = queryResponse.getObjects().getObjects(0);
                assertEquals("Unexpected Object in Query response after Offsetting", objectType1, objectType2);
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
        }
    }

    // TODO: renditionFilter

    public void testQueryWhere() throws Exception
    {
        if (EnumCapabilityQuery.none.equals(getAndAssertCapabilities().getCapabilityQuery())
                || EnumCapabilityQuery.fulltextonly.equals(getAndAssertCapabilities().getCapabilityQuery()))
        {
            LOGGER.warn("testQueryWhere() was skipped: Metadata query isn't supported");
        }
        else
        {
            CmisPropertiesType response = getAndAssertObjectProperties(documentsIds[0], PROP_NAME);
            String name = getStringProperty(response, PROP_NAME);
            String statement = "SELECT * FROM " + BASE_TYPE_DOCUMENT.getValue() + " WHERE " + PROP_NAME + "='" + name + "'";
            QueryResponse queryResponse = queryAndAssert(statement, false, false, null, null, null, null);
            String resultId = getIdProperty(queryResponse.getObjects().getObjects()[0].getProperties(), PROP_OBJECT_ID);
            assertEquals("'WHERE' clause was resulted with invalid Object", documentsIds[0], resultId);
        }
    }

    public void testNonValidQuery()
    {
        if (EnumCapabilityQuery.none.equals(getAndAssertCapabilities().getCapabilityQuery())
                || EnumCapabilityQuery.fulltextonly.equals(getAndAssertCapabilities().getCapabilityQuery()))
        {
            LOGGER.warn("testNonValidQuery() was skipped: Metadata query isn't supported");
        }
        else
        {
            Query request = new Query();
            request.setRepositoryId(getAndAssertRepositoryId());
            request.setStatement("SELECT * FROM InvalidTypeId");
            try
            {
                LOGGER.info("[DiscoveryService->query]");
                getServicesFactory().getDiscoveryService().query(request);
                fail("No Exception was thrown during executing Query with wrong Statement");
            }
            catch (Exception e)
            {
                Set<EnumServiceException> expectedExceptions = new HashSet<EnumServiceException>();
                expectedExceptions.add(EnumServiceException.invalidArgument);
                expectedExceptions.add(EnumServiceException.runtime);
                assertException("Executing Query with wrong Statement", e, expectedExceptions);
            }
        }
    }

    public void testQueryPWCSearchable() throws Exception
    {
        if (EnumCapabilityQuery.none.equals(getAndAssertCapabilities().getCapabilityQuery())
                || EnumCapabilityQuery.fulltextonly.equals(getAndAssertCapabilities().getCapabilityQuery()))
        {
            LOGGER.warn("testQueryPWCSearchable() was skipped: Metadata query isn't supported");
        }
        else
        {
            if (isVersioningAllowed())
            {
                String documentId = createAndAssertDocument();
                CheckOutResponse checkOutResponse = checkOutAndAssert(documentId);
                String statement = "SELECT * FROM " + BASE_TYPE_DOCUMENT.getValue() + " WHERE " + PROP_OBJECT_ID + "='" + checkOutResponse.getDocumentId() + "'";
                QueryResponse queryResponse = null;
                try
                {
                    queryResponse = queryAndAssert(statement, false, false, null, null, null, null);
                }
                finally
                {
                    LOGGER.info("[VersioningService->cancelCheckOut]");
                    getServicesFactory().getVersioningService().cancelCheckOut(new CancelCheckOut(getAndAssertRepositoryId(), checkOutResponse.getDocumentId(), null));
                    deleteAndAssertObject(documentId);
                }
                if (getAndAssertCapabilities().isCapabilityPWCSearchable())
                {
                    assertEquals("PWC was not found", 1, queryResponse.getObjects().getObjects().length);
                }
                else
                {
                    assertTrue("PWC is not searchable but was found", queryResponse.getObjects().getObjects().length < 1);
                }
            }
            else
            {
                LOGGER.warn("testQueryPWCSearchable() was skipped: Versioning isn't supported");
            }
        }
    }

    public void testQueryAllVersionsSearchable() throws Exception
    {
        CmisRepositoryCapabilitiesType capabilities = getAndAssertCapabilities();
        if (EnumCapabilityQuery.none.equals(capabilities.getCapabilityQuery()) || EnumCapabilityQuery.fulltextonly.equals(capabilities.getCapabilityQuery()))
        {
            LOGGER.warn("testQueryAllVersionsSearchable() was skipped: Metadata querying isn't supported");
        }
        else
        {
            if (isVersioningAllowed())
            {
                String documentId = createAndAssertDocument(generateTestFileName(), getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), null, TEST_CONTENT, null);
                CheckInResponse checkInResponse = new CheckInResponse(documentId, null);
                for (int i = 0; i < 2; i++)
                {
                    CheckOutResponse checkOutResponse = checkOutAndAssert(checkInResponse.getDocumentId());
                    checkInResponse = checkInAndAssert(checkOutResponse.getDocumentId(), true, new CmisPropertiesType(), createUniqueContentStream(), "");
                }
                CmisObjectType[] allVersions = getAndAssertAllVersions(checkInResponse.getDocumentId(), "*", false);
                String statement = "SELECT * FROM " + BASE_TYPE_DOCUMENT.getValue() + " WHERE " + PROP_PARENT_ID + "='" + getAndAssertRootFolderId() + "'";
                QueryResponse queryResponse = null;
                try
                {
                    queryResponse = queryAndAssert(statement, true, false, null, null, null, null);
                }
                finally
                {
                    deleteAndAssertObject(documentId, true);
                }
                Set<String> responseIds = new HashSet<String>();
                for (CmisObjectType object : queryResponse.getObjects().getObjects())
                {
                    assertNotNull("Invalid Query response: one of the Objects is in 'not set' state solely", object);
                    responseIds.add(getIdProperty(object.getProperties(), PROP_OBJECT_ID));
                }
                for (CmisObjectType object : allVersions)
                {
                    assertNotNull("Invalid Version Objects collection: one of the Version Objects is in 'not set' state solely", object);
                    if (capabilities.isCapabilityAllVersionsSearchable())
                    {
                        assertTrue("All Versions Searchable capability is supported but Version Objects was not returned", responseIds.contains(getIdProperty(object
                                .getProperties(), PROP_OBJECT_ID)));
                    }
                    else
                    {
                        assertFalse("All Versions Searchable capability is not supported but Version Objects was returned", responseIds.contains(getIdProperty(object
                                .getProperties(), PROP_OBJECT_ID)));
                    }
                }
            }
            else
            {
                LOGGER.warn("testQueryAllVersionsSearchable() was skipped: Versioning isn't supported");
            }
        }
    }

    public void testQueryFullText() throws Exception
    {
        if (EnumCapabilityQuery.none.equals(getAndAssertCapabilities().getCapabilityQuery())
                || EnumCapabilityQuery.metadataonly.equals(getAndAssertCapabilities().getCapabilityQuery()))
        {
            LOGGER.info("testQueryFullText() was skipped: Full Text querying isn't supported");
        }
        else
        {
            if (isContentStreamAllowed())
            {
                String content = createTestContnet();
                String documentId = createAndAssertDocument(generateTestFileName(), getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), null, content, null);
                String statement = "SELECT * FROM " + BASE_TYPE_DOCUMENT + " WHERE CONTAINS('" + content + "')";
                QueryResponse queryResponse = null;
                try
                {
                    queryResponse = queryAndAssert(statement, false, false, null, null, null, null);
                }
                finally
                {
                    deleteAndAssertObject(documentId);
                }
                boolean found = false;
                for (int i = 0; !found && (i < queryResponse.getObjects().getObjects().length); i++)
                {
                    found = documentId.equals(getIdProperty(queryResponse.getObjects().getObjects()[i].getProperties(), PROP_OBJECT_ID));
                }
                assertTrue("Full Text Search is supported but Document was not found by content", found);
            }
            else
            {
                LOGGER.info("testQueryFullText() was skipped: Content stream isn't allowed");
            }
        }
    }

    public void testQueryAllowableActions() throws Exception
    {
        if (EnumCapabilityQuery.none.equals(getAndAssertCapabilities().getCapabilityQuery())
                || EnumCapabilityQuery.fulltextonly.equals(getAndAssertCapabilities().getCapabilityQuery()))
        {
            LOGGER.warn("testQueryAllowableActions() was skipped: Metadata query isn't supported");
        }
        else
        {
            String statement = "SELECT * FROM " + BASE_TYPE_DOCUMENT.getValue();
            QueryResponse queryResponse = queryAndAssert(statement, false, true, null, null, null, null);
            for (CmisObjectType object : queryResponse.getObjects().getObjects())
            {
                assertNotNull("Invalid Query response: one of the Objects is in 'not set' state solely", object);
                assertNotNull("Allowable Actions were not returned in Query response", object.getAllowableActions());
                assertTrue("It is denied Getting Properties for Current User Principals", object.getAllowableActions().getCanGetProperties());
            }
        }
    }

    // TODO: (in the next version of tests) selecting properties from several different tables with incldueAllowableActions equal to 'true' and 'false'

    public void testQueryRelationships() throws Exception
    {
        if (EnumCapabilityQuery.none.equals(getAndAssertCapabilities().getCapabilityQuery())
                || EnumCapabilityQuery.fulltextonly.equals(getAndAssertCapabilities().getCapabilityQuery()))
        {
            LOGGER.warn("testQueryRelationships() was skipped: Metadata query isn't supported");
        }
        else
        {
            String statement = "SELECT * FROM " + BASE_TYPE_DOCUMENT.getValue();
            EnumIncludeRelationships[] relationshipInclusionRules = new EnumIncludeRelationships[] { EnumIncludeRelationships.none, EnumIncludeRelationships.both,
                    EnumIncludeRelationships.source, EnumIncludeRelationships.target };
            for (EnumIncludeRelationships rule : relationshipInclusionRules)
            {
                QueryResponse queryResponse = queryAndAssert(statement, false, false, rule, null, null, null);
                assertRelationships(queryResponse, rule);
            }
        }
    }

    private void assertRelationships(QueryResponse queryResponse, EnumIncludeRelationships includeRelationships) throws Exception
    {
        for (CmisObjectType object : queryResponse.getObjects().getObjects())
        {
            assertNotNull("Invalid Query response: one of the Objects is in 'not set' state solely", object);
            if (EnumIncludeRelationships.none.equals(includeRelationships))
            {
                assertNull("Relationships were returned for one of the Objects with includeRelationships parameter equal to 'NONE'", object.getRelationship());
            }
            else
            {
                assertNotNull("Relationships were not returned for one of the Objects", object.getRelationship());
                String objectId = getIdProperty(object.getProperties(), PROP_OBJECT_ID);
                for (CmisObjectType relationship : object.getRelationship())
                {
                    assertNotNull("Invalid Relationships collection in Query response: one of the Relationship Objects in 'not set' state solely", relationship);
                    String sourceId = getIdProperty(relationship.getProperties(), PROP_SOURCE_ID);
                    String targetId = getIdProperty(relationship.getProperties(), PROP_TARGET_ID);
                    if (EnumIncludeRelationships.source.equals(includeRelationships))
                    {
                        assertEquals(objectId, sourceId);
                    }
                    else
                    {
                        if (EnumIncludeRelationships.target.equals(includeRelationships))
                        {
                            assertEquals(objectId, targetId);
                        }
                        else
                        {
                            assertTrue(("Object with Id='" + objectId + "' MUST be either Source or Target Object of each Relationship Object"), objectId.equals(sourceId)
                                    || objectId.equals(targetId));
                        }
                    }
                }
            }
        }
    }

    public void testGetContentChanges() throws Exception
    {
        if (!EnumCapabilityChanges.none.equals(getAndAssertCapabilities().getCapabilityChanges()))
        {
            try
            {
                LOGGER.info("[DiscoveryService->getContentChanges]");
                // TODO: changeLogToken
                GetContentChangesResponse response = getServicesFactory().getDiscoveryService().getContentChanges(
                        new GetContentChanges(getAndAssertRepositoryId(), null, false, "*", false, false, null, null));
                assertNotNull("Get Content Changes response is undefined", response);
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
        }
        else
        {
            LOGGER.warn("testGetContentChanges() was skipped: Changes capability is not supported");
        }
    }
}
