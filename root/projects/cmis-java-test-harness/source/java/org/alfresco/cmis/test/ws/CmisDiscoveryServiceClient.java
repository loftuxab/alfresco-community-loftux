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
import org.alfresco.repo.cmis.ws.DeleteObject;
import org.alfresco.repo.cmis.ws.DiscoveryServicePortBindingStub;
import org.alfresco.repo.cmis.ws.EnumCapabilityQuery;
import org.alfresco.repo.cmis.ws.EnumVersioningState;
import org.alfresco.repo.cmis.ws.GetContentChanges;
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

    private static final String STATEMENT = "SELECT * FROM DOCUMENT";

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
        request.setStatement(STATEMENT);
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
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:wsi-tools-client-context.xml");
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
            documentsIds[i] = createAndAssertDocument(i + System.currentTimeMillis() + "test.txt", getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), null,
                    EnumVersioningState.major);
        }
    }

    @Override
    protected void onTearDown() throws Exception
    {
        super.onTearDown();
        for (int i = 0; i < documentsIds.length; i++)
        {
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentsIds[i], true));
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
            request.setStatement("SELECT * FROM cmis:document");
            try
            {
                QueryResponse queryResponse = getServicesFactory().getDiscoveryService().query(request);
                assertTrue(queryResponse.getObject() != null);
                assertTrue(queryResponse.getObject().length <= 10);
            }
            catch (Exception e)
            {
                fail(e.getMessage());
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
            request.setStatement("SELECT * FROM cmis:document ORDER BY cmis:Name ASC");
            try
            {
                QueryResponse queryResponse = getServicesFactory().getDiscoveryService().query(request);
                assertTrue(queryResponse.getObject() != null);
                assertTrue(queryResponse.getObject().length <= 10);
                String name1 = getStringProperty(queryResponse.getObject()[0].getProperties(), PROP_NAME);
                String name2 = getStringProperty(queryResponse.getObject()[queryResponse.getObject().length - 1].getProperties(), PROP_NAME);
                assertTrue(name1.compareTo(name2) <= 0);
            }
            catch (Exception e)
            {
                fail(e.getMessage());
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
            request.setStatement("SELECT cmis:Name FROM cmis:document");
            try
            {
                QueryResponse queryResponse = getServicesFactory().getDiscoveryService().query(request);
                assertTrue(queryResponse.getObject() != null);
                assertTrue(queryResponse.getObject().length <= 10);
            }
            catch (Exception e)
            {
                fail(e.getMessage());
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
            request.setStatement("SELECT * FROM cmis:document");
            try
            {
                QueryResponse queryResponse = getServicesFactory().getDiscoveryService().query(request);
                objectType1 = queryResponse.getObject(1);

                request.setSkipCount(BigInteger.valueOf(1));
                request.setMaxItems(BigInteger.valueOf(1));
                queryResponse = getServicesFactory().getDiscoveryService().query(request);
                objectType2 = queryResponse.getObject(0);

                assertEquals(objectType1, objectType2);
            }
            catch (Exception e)
            {
                fail(e.getMessage());
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
                    response = getServicesFactory().getObjectService().getProperties(new GetProperties(getAndAssertRepositoryId(), documentsIds[0], null, null, null, null));
                }
                catch (Exception e)
                {
                    fail(e.getMessage());
                }
                name = getStringProperty(response.getObject().getProperties(), PROP_NAME);

                Query request = new Query();
                request.setRepositoryId(getAndAssertRepositoryId());
                request.setStatement("SELECT * FROM cmis:document WHERE cmis:Name='" + name + "'");
                QueryResponse queryResponse = getServicesFactory().getDiscoveryService().query(request);
                String resultId = getIdProperty(queryResponse.getObject()[0].getProperties(), PROP_OBJECT_ID);

                assertEquals(documentsIds[0], resultId);
            }
            catch (Exception e)
            {
                fail(e.getMessage());
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
                String documentId = createAndAssertDocument(System.currentTimeMillis() + "1.txt", null, getAndAssertRootFolderId(), null, EnumVersioningState.major);
                CheckOutResponse checkOutResponse = getServicesFactory().getVersioningService().checkOut(new CheckOut(getAndAssertRepositoryId(), documentId));
                Query request = new Query();
                request.setRepositoryId(getAndAssertRepositoryId());
                request.setStatement("SELECT * FROM cmis:document WHERE cmis:ObjectId='" + checkOutResponse.getDocumentId() + "'");
                QueryResponse queryResponse = null;
                try
                {
                    queryResponse = getServicesFactory().getDiscoveryService().query(request);
                }
                catch (Exception e)
                {
                    fail(e.getMessage());
                }
                finally
                {
                    getServicesFactory().getVersioningService().cancelCheckOut(new CancelCheckOut(getAndAssertRepositoryId(), checkOutResponse.getDocumentId()));
                    getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, true));
                }
                if (getAndAssertCapabilities().isCapabilityPWCSearchable())
                {
                    assertNotNull(queryResponse);
                    assertNotNull(queryResponse.getObject());
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
                String name = System.currentTimeMillis() + ".txt";
                String documentId = createAndAssertDocument(name, null, getAndAssertRootFolderId(), null, EnumVersioningState.major);
                CheckOutResponse checkOutResponse = getServicesFactory().getVersioningService().checkOut(new CheckOut(getAndAssertRepositoryId(), documentId));
                CheckInResponse checkInResponse = getServicesFactory().getVersioningService().checkIn(
                        new CheckIn(getAndAssertRepositoryId(), checkOutResponse.getDocumentId(), true, new CmisPropertiesType(), new CmisContentStreamType(BigInteger.valueOf(0),
                                "text/plain", name, null, "Test content".getBytes(), null), "", null, null, null));
                checkOutResponse = getServicesFactory().getVersioningService().checkOut(new CheckOut(getAndAssertRepositoryId(), checkInResponse.getDocumentId()));
                documentId = getServicesFactory().getVersioningService().checkIn(
                        new CheckIn(getAndAssertRepositoryId(), checkOutResponse.getDocumentId(), true, new CmisPropertiesType(), new CmisContentStreamType(BigInteger.valueOf(0),
                                "text/plain", name, null, "Test content".getBytes(), null), "", null, null, null)).getDocumentId();
                Query request = new Query();
                request.setRepositoryId(getAndAssertRepositoryId());
                request.setStatement("SELECT * FROM cmis:document WHERE cmis:ObjectId='" + checkInResponse.getDocumentId() + "'");
                QueryResponse queryResponse = null;
                try
                {
                    queryResponse = getServicesFactory().getDiscoveryService().query(request);
                }
                catch (Exception e)
                {
                    fail(e.getMessage());
                }
                finally
                {
                    getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, true));
                }
                if (getAndAssertCapabilities().isCapabilityAllVersionsSearchable())
                {
                    assertNotNull(queryResponse);
                    assertNotNull(queryResponse.getObject());
                    assertEquals(getIdProperty(queryResponse.getObject()[0].getProperties(), PROP_OBJECT_ID), checkOutResponse.getDocumentId());
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
                String documentId = createAndAssertDocument(System.currentTimeMillis() + ".txt", null, getAndAssertRootFolderId(), content, EnumVersioningState.major);

                Query request = new Query();
                request.setRepositoryId(getAndAssertRepositoryId());
                request.setStatement("SELECT * FROM cmis:document WHERE CONTAINS('" + content + "')");
                QueryResponse queryResponse = null;
                try
                {
                    queryResponse = getServicesFactory().getDiscoveryService().query(request);
                }
                catch (Exception e)
                {
                    fail(e.getMessage());
                }
                finally
                {
                    getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, true));
                }
                assertNotNull(queryResponse);
                assertNotNull(queryResponse.getObject());
                boolean found = false;
                for (int i = 0; !found && i < queryResponse.getObject().length; i++)
                {
                    found = documentId.equals(getIdProperty(queryResponse.getObject()[0].getProperties(), PROP_OBJECT_ID));
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
        request.setStatement("SELECT * FROM cmis:document");
        try
        {
            QueryResponse queryResponse = getServicesFactory().getDiscoveryService().query(request);
            assertTrue(queryResponse.getObject() != null);
            assertTrue(queryResponse.getObject().length <= 10);
            assertTrue("No allowable actions were returned", queryResponse.getObject()[0].getAllowableActions() != null);
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

}
