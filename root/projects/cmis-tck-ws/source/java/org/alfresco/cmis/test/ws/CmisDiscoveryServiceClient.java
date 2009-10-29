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

import org.alfresco.repo.cmis.ws.CancelCheckOut;
import org.alfresco.repo.cmis.ws.CheckIn;
import org.alfresco.repo.cmis.ws.CheckInResponse;
import org.alfresco.repo.cmis.ws.CheckOut;
import org.alfresco.repo.cmis.ws.CheckOutResponse;
import org.alfresco.repo.cmis.ws.CmisContentStreamType;
import org.alfresco.repo.cmis.ws.CmisObjectType;
import org.alfresco.repo.cmis.ws.CmisPropertiesType;
import org.alfresco.repo.cmis.ws.DiscoveryServicePortBindingStub;
import org.alfresco.repo.cmis.ws.EnumBaseObjectTypeIds;
import org.alfresco.repo.cmis.ws.EnumCapabilityChanges;
import org.alfresco.repo.cmis.ws.EnumCapabilityQuery;
import org.alfresco.repo.cmis.ws.EnumPropertiesBase;
import org.alfresco.repo.cmis.ws.GetContentChanges;
import org.alfresco.repo.cmis.ws.GetContentChangesResponse;
import org.alfresco.repo.cmis.ws.GetProperties;
import org.alfresco.repo.cmis.ws.GetPropertiesResponse;
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
        request.setStatement("SELECT * FROM " + EnumBaseObjectTypeIds._value1);
        discoveryServicePort.query(request);
        discoveryServicePort.getContentChanges(new GetContentChanges(getAndAssertRepositoryId(), null, BigInteger.TEN, true, true, "*"));
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

    public void testQueryAll()
    {
        if (EnumCapabilityQuery.none.equals(getAndAssertCapabilities().getCapabilityQuery())
                || EnumCapabilityQuery.fulltextonly.equals(getAndAssertCapabilities().getCapabilityQuery()))
        {
            LOGGER.info("testQueryAll was skipped: Metadata query isn't supported");
        }
        else
        {
            Query request = new Query();
            request.setRepositoryId(getAndAssertRepositoryId());
            request.setMaxItems(BigInteger.valueOf(10));
            request.setStatement("SELECT * FROM " + EnumBaseObjectTypeIds._value1);
            try
            {
                LOGGER.info("[DiscoveryService->query]");
                QueryResponse queryResponse = getServicesFactory().getDiscoveryService().query(request);
                assertNotNull("Query response is NULL", queryResponse);
                assertNotNull("Query response is NULL", queryResponse.getObject());
                assertTrue("Query response contains more objects than was expected", queryResponse.getObject().length <= 10);
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
        }
    }

    public void testQueryOrder()
    {
        if (EnumCapabilityQuery.none.equals(getAndAssertCapabilities().getCapabilityQuery())
                || EnumCapabilityQuery.fulltextonly.equals(getAndAssertCapabilities().getCapabilityQuery()))
        {
            LOGGER.info("testQueryOrder was skipped: Metadata query isn't supported");
        }
        else
        {
            Query request = new Query();
            request.setRepositoryId(getAndAssertRepositoryId());
            request.setMaxItems(BigInteger.valueOf(10));
            request.setStatement("SELECT * FROM " + EnumBaseObjectTypeIds._value1 + " ORDER BY " + EnumPropertiesBase._value1 + " ASC");
            try
            {
                LOGGER.info("[DiscoveryService->query]");
                QueryResponse queryResponse = getServicesFactory().getDiscoveryService().query(request);
                assertNotNull("Query response is NULL", queryResponse);
                assertNotNull("Query response is NULL", queryResponse.getObject());
                assertTrue("Query response contains more objects than was expected", queryResponse.getObject().length <= 10);
                String name1 = getStringProperty(queryResponse.getObject()[0].getProperties(), EnumPropertiesBase._value1);
                String name2 = getStringProperty(queryResponse.getObject()[queryResponse.getObject().length - 1].getProperties(), EnumPropertiesBase._value1);
                assertTrue("Query response objects are not ordered", name1.compareTo(name2) <= 0);
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
        }
    }

    public void testQueryField()
    {
        if (EnumCapabilityQuery.none.equals(getAndAssertCapabilities().getCapabilityQuery())
                || EnumCapabilityQuery.fulltextonly.equals(getAndAssertCapabilities().getCapabilityQuery()))
        {
            LOGGER.info("testQueryField was skipped: Metadata query isn't supported");
        }
        else
        {
            Query request = new Query();
            request.setRepositoryId(getAndAssertRepositoryId());
            request.setMaxItems(BigInteger.valueOf(10));
            request.setStatement("SELECT " + EnumPropertiesBase._value1 + " FROM " + EnumBaseObjectTypeIds._value1);
            try
            {
                LOGGER.info("[DiscoveryService->query]");
                QueryResponse queryResponse = getServicesFactory().getDiscoveryService().query(request);
                assertNotNull("Query response is NULL", queryResponse);
                assertNotNull("Query response is NULL", queryResponse.getObject());
                assertTrue("Query response contains more objects than was expected", queryResponse.getObject().length <= 10);
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
        }
    }

    public void testQueryOffset()
    {
        if (EnumCapabilityQuery.none.equals(getAndAssertCapabilities().getCapabilityQuery())
                || EnumCapabilityQuery.fulltextonly.equals(getAndAssertCapabilities().getCapabilityQuery()))
        {
            LOGGER.info("testQueryOffset was skipped: Metadata query isn't supported");
        }
        else
        {
            CmisObjectType objectType1 = null;
            CmisObjectType objectType2 = null;
            Query request = new Query();
            request.setRepositoryId(getAndAssertRepositoryId());
            request.setMaxItems(BigInteger.valueOf(2));
            request.setStatement("SELECT * FROM " + EnumBaseObjectTypeIds._value1);
            try
            {
                LOGGER.info("[DiscoveryService->query]");
                QueryResponse queryResponse = getServicesFactory().getDiscoveryService().query(request);
                assertNotNull("Query response is NULL", queryResponse);
                assertNotNull("Query response is NULL", queryResponse.getObject());
                assertTrue("Query response contains more objects than was expected", queryResponse.getObject().length <= 2);
                objectType1 = queryResponse.getObject(1);

                request.setSkipCount(BigInteger.valueOf(1));
                request.setMaxItems(BigInteger.valueOf(1));
                LOGGER.info("[DiscoveryService->query]");
                queryResponse = getServicesFactory().getDiscoveryService().query(request);
                assertNotNull("Query response is NULL", queryResponse);
                assertNotNull("Query response is NULL", queryResponse.getObject());
                assertTrue("Query response contains more objects than was expected", queryResponse.getObject().length <= 1);
                objectType2 = queryResponse.getObject(0);

                assertEquals("Offset value changes doesn't affect query result", objectType1, objectType2);
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
        }
    }

    public void testQueryWhere()
    {
        if (EnumCapabilityQuery.none.equals(getAndAssertCapabilities().getCapabilityQuery())
                || EnumCapabilityQuery.fulltextonly.equals(getAndAssertCapabilities().getCapabilityQuery()))
        {
            LOGGER.info("testQueryWhere was skipped: Metadata query isn't supported");
        }
        else
        {
            String name = null;
            try
            {
                GetPropertiesResponse response = null;
                try
                {
                    LOGGER.info("[ObjectService->getProperties]");
                    response = getServicesFactory().getObjectService().getProperties(new GetProperties(getAndAssertRepositoryId(), documentsIds[0], null, null, null, null));
                }
                catch (Exception e)
                {
                    fail(e.toString());
                }
                name = getStringProperty(response.getObject().getProperties(), EnumPropertiesBase._value1);

                Query request = new Query();
                request.setRepositoryId(getAndAssertRepositoryId());
                request.setStatement("SELECT * FROM " + EnumBaseObjectTypeIds._value1 + " WHERE " + EnumPropertiesBase._value1 + "='" + name + "'");
                LOGGER.info("[DiscoveryService->query]");
                QueryResponse queryResponse = getServicesFactory().getDiscoveryService().query(request);
                assertNotNull("Query response is NULL", queryResponse);
                assertNotNull("Query response is NULL", queryResponse.getObject());
                String resultId = getIdProperty(queryResponse.getObject()[0].getProperties(), EnumPropertiesBase._value2);

                assertEquals("'WHERE' clause doesn't affect query result", documentsIds[0], resultId);
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
        }
    }

    public void testNonValidQuery()
    {
        if (EnumCapabilityQuery.none.equals(getAndAssertCapabilities().getCapabilityQuery())
                || EnumCapabilityQuery.fulltextonly.equals(getAndAssertCapabilities().getCapabilityQuery()))
        {
            LOGGER.info("testNonValidQuery was skipped: Metadata query isn't supported");
        }
        else
        {
            Query request = new Query();
            request.setRepositoryId(getAndAssertRepositoryId());
            request.setStatement("SELECT * FROM NonValidType");
            try
            {
                LOGGER.info("[DiscoveryService->query]");
                getServicesFactory().getDiscoveryService().query(request);
                fail("No Exception was thrown");
            }
            catch (Exception e)
            {
                // expected
            }
        }
    }

    public void testQueryPWCSearchable() throws Exception
    {
        if (EnumCapabilityQuery.none.equals(getAndAssertCapabilities().getCapabilityQuery())
                || EnumCapabilityQuery.fulltextonly.equals(getAndAssertCapabilities().getCapabilityQuery()))
        {
            LOGGER.info("testQueryPWCSearchable was skipped: Metadata query isn't supported");
        }
        else
        {
            if (isVersioningAllowed())
            {
                String documentId = createAndAssertDocument();
                LOGGER.info("[VersioningService->checkOut]");
                CheckOutResponse checkOutResponse = getServicesFactory().getVersioningService().checkOut(new CheckOut(getAndAssertRepositoryId(), documentId));
                Query request = new Query();
                request.setRepositoryId(getAndAssertRepositoryId());
                request.setStatement("SELECT * FROM " + EnumBaseObjectTypeIds._value1 + " WHERE " + EnumPropertiesBase._value2 + "='" + checkOutResponse.getDocumentId() + "'");
                QueryResponse queryResponse = null;
                try
                {
                    LOGGER.info("[DiscoveryService->query]");
                    queryResponse = getServicesFactory().getDiscoveryService().query(request);
                }
                catch (Exception e)
                {
                    fail(e.toString());
                }
                finally
                {
                    LOGGER.info("[VersioningService->cancelCheckOut]");
                    getServicesFactory().getVersioningService().cancelCheckOut(new CancelCheckOut(getAndAssertRepositoryId(), checkOutResponse.getDocumentId()));
                    deleteAndAssertObject(documentId);
                }
                if (getAndAssertCapabilities().isCapabilityPWCSearchable())
                {
                    assertNotNull("Query response is NULL", queryResponse);
                    assertNotNull("Query response is NULL", queryResponse.getObject());
                    assertTrue("PWC was not found", queryResponse.getObject().length == 1);
                }
                else
                {
                    assertTrue("PWC is not searchable, but was found", queryResponse == null || queryResponse.getObject() == null || queryResponse.getObject().length == 0);
                }
            }
            else
            {
                LOGGER.info("testQueryPWCSearchable was skipped: Versioning isn't supported");
            }
        }
    }

    public void testQueryAllVersionsSearchable() throws Exception
    {
        if (EnumCapabilityQuery.none.equals(getAndAssertCapabilities().getCapabilityQuery())
                || EnumCapabilityQuery.fulltextonly.equals(getAndAssertCapabilities().getCapabilityQuery()))
        {
            LOGGER.info("testQueryAllVersionsSearchable was skipped: Metadata query isn't supported");
        }
        else
        {
            if (isVersioningAllowed())
            {
                String name = generateTestFileName();
                String documentId = createAndAssertDocument(name, getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), null, TEST_CONTENT, null);
                LOGGER.info("[VersioningService->checkOut]");
                CheckOutResponse checkOutResponse = getServicesFactory().getVersioningService().checkOut(new CheckOut(getAndAssertRepositoryId(), documentId));
                LOGGER.info("[VersioningService->checkIn]");
                CheckInResponse checkInResponse = getServicesFactory().getVersioningService().checkIn(
                        new CheckIn(getAndAssertRepositoryId(), checkOutResponse.getDocumentId(), true, new CmisPropertiesType(), new CmisContentStreamType(BigInteger.valueOf(0),
                                "text/plain", name, null, "Test content".getBytes(), null), "", null, null, null));
                LOGGER.info("[VersioningService->checkOut]");
                checkOutResponse = getServicesFactory().getVersioningService().checkOut(new CheckOut(getAndAssertRepositoryId(), checkInResponse.getDocumentId()));
                LOGGER.info("[VersioningService->checkIn]");
                documentId = getServicesFactory().getVersioningService().checkIn(
                        new CheckIn(getAndAssertRepositoryId(), checkOutResponse.getDocumentId(), true, new CmisPropertiesType(), new CmisContentStreamType(BigInteger.valueOf(0),
                                "text/plain", name, null, "Test content".getBytes(), null), "", null, null, null)).getDocumentId();
                Query request = new Query();
                request.setRepositoryId(getAndAssertRepositoryId());
                request.setStatement("SELECT * FROM " + EnumBaseObjectTypeIds._value1 + " WHERE " + EnumPropertiesBase._value2 + "='" + checkInResponse.getDocumentId() + "'");
                request.setSearchAllVersions(true);
                QueryResponse queryResponse = null;
                try
                {
                    LOGGER.info("[DiscoveryService->query]");
                    queryResponse = getServicesFactory().getDiscoveryService().query(request);
                }
                catch (Exception e)
                {
                    fail(e.toString());
                }
                finally
                {
                    deleteAndAssertObject(documentId);
                }
                if (getAndAssertCapabilities().isCapabilityAllVersionsSearchable())
                {
                    assertNotNull("Query response is NULL", queryResponse);
                    assertNotNull("Query response is NULL", queryResponse.getObject());
                    assertEquals("Particular version of the document was not found", getIdProperty(queryResponse.getObject()[0].getProperties(), EnumPropertiesBase._value2),
                            checkOutResponse.getDocumentId());
                }
                else
                {
                    assertTrue("All versions is not searchable, but not latest version of document was found", queryResponse == null || queryResponse.getObject() == null
                            || queryResponse.getObject().length == 0);
                }
            }
            else
            {
                LOGGER.info("testQueryAllVersionsSearchable was skipped: Versioning isn't supported");
            }
        }
    }

    public void testQueryFullText() throws Exception
    {
        if (EnumCapabilityQuery.none.equals(getAndAssertCapabilities().getCapabilityQuery())
                || EnumCapabilityQuery.metadataonly.equals(getAndAssertCapabilities().getCapabilityQuery()))
        {
            LOGGER.info("testQueryFullText was skipped: Full text query isn't supported");
        }
        else
        {
            if (isContentStreamAllowed())
            {
                String content = "Test cnt" + System.currentTimeMillis();
                String documentId = createAndAssertDocument(generateTestFileName(), getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), null, content, null);

                Query request = new Query();
                request.setRepositoryId(getAndAssertRepositoryId());
                request.setStatement("SELECT * FROM " + EnumBaseObjectTypeIds._value1 + " WHERE CONTAINS('" + content + "')");
                QueryResponse queryResponse = null;
                try
                {
                    LOGGER.info("[DiscoveryService->query]");
                    queryResponse = getServicesFactory().getDiscoveryService().query(request);
                }
                catch (Exception e)
                {
                    fail(e.toString());
                }
                finally
                {
                    deleteAndAssertObject(documentId);
                }
                assertNotNull("Query response is NULL", queryResponse);
                assertNotNull("Query response is NULL", queryResponse.getObject());
                boolean found = false;
                for (int i = 0; !found && i < queryResponse.getObject().length; i++)
                {
                    found = documentId.equals(getIdProperty(queryResponse.getObject()[0].getProperties(), EnumPropertiesBase._value2));
                }
                assertTrue("Fulltext search is supported, but document was not found", found);
            }
            else
            {
                LOGGER.info("testQueryFullText was skipped: Content stream isn't allowed");
            }
        }
    }

    public void testQueryAllowableActions()
    {
        if (EnumCapabilityQuery.none.equals(getAndAssertCapabilities().getCapabilityQuery())
                || EnumCapabilityQuery.fulltextonly.equals(getAndAssertCapabilities().getCapabilityQuery()))
        {
            LOGGER.info("testQueryAllowableActions was skipped: Metadata query isn't supported");
        }
        Query request = new Query();
        request.setRepositoryId(getAndAssertRepositoryId());
        request.setMaxItems(BigInteger.valueOf(10));
        request.setIncludeAllowableActions(true);
        request.setStatement("SELECT * FROM " + EnumBaseObjectTypeIds._value1);
        try
        {
            LOGGER.info("[DiscoveryService->query]");
            QueryResponse queryResponse = getServicesFactory().getDiscoveryService().query(request);
            assertNotNull("Query response is NULL", queryResponse);
            assertNotNull("Query response is NULL", queryResponse.getObject());
            assertTrue("Query response contains more objects than was expected", queryResponse.getObject().length <= 10);
            assertTrue("No allowable actions were returned", queryResponse.getObject()[0].getAllowableActions() != null);
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
    }

    public void testGetContentChanges()
    {
        if (!getAndAssertCapabilities().getCapabilityChanges().equals(EnumCapabilityChanges.none))
        {
            try
            {
                LOGGER.info("[DiscoveryService->getContentChanges]");
                GetContentChangesResponse response = getServicesFactory().getDiscoveryService().getContentChanges(
                        new GetContentChanges(getAndAssertRepositoryId(), null, null, null, null, null));
                assertNotNull("getContentChanges response is NULL", response);
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
        }
        else
        {
            LOGGER.info("testGetContentChanges was skipped: Capability changes not supported");
        }
    }
}
