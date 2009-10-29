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
import org.alfresco.repo.cmis.ws.CmisFaultType;
import org.alfresco.repo.cmis.ws.CmisObjectType;
import org.alfresco.repo.cmis.ws.CmisPropertiesType;
import org.alfresco.repo.cmis.ws.DeleteObject;
import org.alfresco.repo.cmis.ws.EnumIncludeRelationships;
import org.alfresco.repo.cmis.ws.EnumServiceException;
import org.alfresco.repo.cmis.ws.EnumVersioningState;
import org.alfresco.repo.cmis.ws.GetAllVersions;
import org.alfresco.repo.cmis.ws.GetPropertiesOfLatestVersion;
import org.alfresco.repo.cmis.ws.GetPropertiesOfLatestVersionResponse;
import org.alfresco.repo.cmis.ws.VersioningServicePort;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Client for Versioning Service
 */
public class CmisVersioningServiceClient extends AbstractServiceClient
{
    private static Log LOGGER = LogFactory.getLog(CmisVersioningServiceClient.class);

    private static final String TEST_CHECK_IN_COMMENT_MESSAGE = "Test Check In Comment";
    private static final String INVALID_REPOSITORY_ID = "Wrong Repository Id";

    private String documentId;

    private String documentIdHolder;

    public CmisVersioningServiceClient()
    {
    }

    public CmisVersioningServiceClient(AbstractService abstractService)
    {
        super(abstractService);
    }

    /**
     * Initializes Versioning Service client
     */
    @Override
    public void initialize() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Initializing client...");
        }
        documentId = createAndAssertDocument(System.currentTimeMillis() + TEST_FILE_NAME, null, getAndAssertRootFolderId(), null, EnumVersioningState.major);
        documentIdHolder = getServicesFactory().getVersioningService().checkOut(new CheckOut(getAndAssertRepositoryId(), documentId)).getDocumentId();
    }

    /**
     * Invokes all methods in Versioning Service
     */
    @Override
    public void invoke() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Invoking client...");
        }
        VersioningServicePort versioningService = getServicesFactory().getVersioningService(getProxyUrl() + getService().getPath());

        versioningService.cancelCheckOut(new CancelCheckOut(getAndAssertRepositoryId(), documentIdHolder));

        documentIdHolder = versioningService.checkOut(new CheckOut(getAndAssertRepositoryId(), documentId)).getDocumentId();

        versioningService.checkIn(new CheckIn(getAndAssertRepositoryId(), documentIdHolder, true, new CmisPropertiesType(), new CmisContentStreamType(BigInteger.valueOf(0),
                MIMETYPE_TEXT_PLAIN, TEST_FILE_NAME, null, new byte[0], null), TEST_CHECK_IN_COMMENT_MESSAGE, null, null, null));

        GetAllVersions getAllVersions = new GetAllVersions();
        getAllVersions.setRepositoryId(getAndAssertRepositoryId());
        getAllVersions.setVersionSeriesId(documentId);
        getAllVersions.setFilter("*");
        getAllVersions.setIncludeAllowableActions(false);
        getAllVersions.setIncludeRelationships(EnumIncludeRelationships.none);
        versioningService.getAllVersions(getAllVersions);

        GetPropertiesOfLatestVersion getPropertiesOfLatestVersion = new GetPropertiesOfLatestVersion();
        getPropertiesOfLatestVersion.setRepositoryId(getAndAssertRepositoryId());
        getPropertiesOfLatestVersion.setVersionSeriesId(documentId);
        getPropertiesOfLatestVersion.setFilter("*");
        versioningService.getPropertiesOfLatestVersion(getPropertiesOfLatestVersion);

        // FIXME: uncomment this when schema will be corrected
        // versioningService.deleteAllVersions(getAndAssertRepositoryId(), documentId);
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
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, true));
    }

    /**
     * Main method to start client
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:wsi-tools-client-context.xml");
        AbstractServiceClient client = (CmisVersioningServiceClient) applicationContext.getBean("cmisVersioningServiceClient");
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
        documentId = createAndAssertDocument(System.currentTimeMillis() + TEST_FILE_NAME, null, getAndAssertRootFolderId(), null, EnumVersioningState.major);
        super.onSetUp();
    }

    @Override
    protected void onTearDown() throws Exception
    {
        try
        {
            if (documentIdHolder != null)
            {
                getServicesFactory().getVersioningService().cancelCheckOut(new CancelCheckOut(getAndAssertRepositoryId(), documentIdHolder));
            }
        }
        catch (Exception e)
        {
        }
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, true));
        super.onTearDown();
    }

    public void testCheckOut() throws Exception
    {
        CheckOutResponse response = null;
        try
        {
            response = getServicesFactory().getVersioningService().checkOut(new CheckOut(getAndAssertRepositoryId(), documentId));
            assertNotNull("Checkout response is NULL", response);
            documentIdHolder = response.getDocumentId();
            assertNotNull(documentIdHolder);
            if (!isVersioningAllowed())
            {
                fail("No Exception was thrown");
            }
        }
        catch (Exception e)
        {
            if (!isVersioningAllowed())
            {
                assertTrue(e instanceof CmisFaultType && ((CmisFaultType) e).getType().equals(EnumServiceException.constraint));
            }
            else
            {
                fail(e.getMessage());
            }
        }
    }

    public void testCheckOutCheckInDefault()
    {
        try
        {
            CheckOutResponse checkOutResponse = getServicesFactory().getVersioningService().checkOut(new CheckOut(getAndAssertRepositoryId(), documentId));
            assertNotNull("Checkout response is NULL", checkOutResponse);
            documentIdHolder = checkOutResponse.getDocumentId();
            assertTrue(checkOutResponse.isContentCopied());
            assertFalse(documentId.equals(documentIdHolder));
            if (!isVersioningAllowed())
            {
                fail("No Exception was thrown");
            }

            CheckInResponse checkInResponse = getServicesFactory().getVersioningService().checkIn(
                    new CheckIn(getAndAssertRepositoryId(), documentIdHolder, null, null, null, null, null, null, null));
            assertNotNull("checkin response is NULL", checkInResponse);
            documentId = checkInResponse.getDocumentId();
            if (!isVersioningAllowed())
            {
                fail("No Exception was thrown");
            }
        }
        catch (Exception e)
        {
            if (!isVersioningAllowed())
            {
                assertTrue(e instanceof CmisFaultType && ((CmisFaultType) e).getType().equals(EnumServiceException.constraint));
            }
            else
            {
                fail(e.getMessage());
            }
        }
    }

    public void testCheckOutCancelCheckOut()
    {
        try
        {
            CheckOutResponse checkOutResponse = getServicesFactory().getVersioningService().checkOut(new CheckOut(getAndAssertRepositoryId(), documentId));
            assertNotNull("Checkout response is NULL", checkOutResponse);
            documentIdHolder = checkOutResponse.getDocumentId();
            if (!isVersioningAllowed())
            {
                fail("No Exception was thrown");
            }
            assertTrue(checkOutResponse.isContentCopied());
            assertFalse(documentId.equals(documentIdHolder));

            getServicesFactory().getVersioningService().cancelCheckOut(new CancelCheckOut(getAndAssertRepositoryId(), documentIdHolder));
            if (!isVersioningAllowed())
            {
                fail("No Exception was thrown");
            }
            assertFalse(getBooleanProperty(documentId, "cmis:IsVersionSeriesCheckedOut"));
        }
        catch (Exception e)
        {
            if (!isVersioningAllowed())
            {
                assertTrue(e instanceof CmisFaultType && ((CmisFaultType) e).getType().equals(EnumServiceException.constraint));
            }
            else
            {
                fail(e.getMessage());
            }
        }
    }

    public void testCheckinNoExistsCheckOut() throws Exception
    {
        try
        {
            getServicesFactory().getVersioningService().checkIn(
                    new CheckIn(getAndAssertRepositoryId(), documentId, true, new CmisPropertiesType(), new CmisContentStreamType(BigInteger.valueOf(0), MIMETYPE_TEXT_PLAIN,
                            TEST_FILE_NAME, null, TEST_CONTENT.getBytes(), null), TEST_CHECK_IN_COMMENT_MESSAGE, null, null, null));
            fail("No Exception was thrown");

        }
        catch (Exception e)
        {
            assertTrue(e instanceof CmisFaultType);
        }
    }

    public void testCancelNotExistsCheckOut() throws Exception
    {
        try
        {
            getServicesFactory().getVersioningService().cancelCheckOut(new CancelCheckOut(getAndAssertRepositoryId(), documentId));
            fail("Expects exception");

        }
        catch (Exception e)
        {
            assertTrue(e instanceof CmisFaultType);
        }
    }

    public void testGetPropertiesOfLatestVersionDefault() throws Exception
    {
        if (isVersioningAllowed())
        {
            GetPropertiesOfLatestVersionResponse response = null;
            String versionSeriesId = getIdProperty(documentId, "cmis:VersionSeriesId");
            assertNotNull("'VersionSeriesId' property is NULL", versionSeriesId);
            try
            {
                response = getServicesFactory().getVersioningService().getPropertiesOfLatestVersion(
                        new GetPropertiesOfLatestVersion(getAndAssertRepositoryId(), versionSeriesId, true, null, false));
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
            assertNotNull(response);
            assertNotNull(response.getObject());
            CmisObjectType objectType = response.getObject();
            assertNotNull(objectType.getProperties());
            assertTrue((Boolean) getBooleanProperty(objectType.getProperties(), "cmis:IsLatestVersion"));
        }
        else
        {
            LOGGER.info("testGetPropertiesOfLatestVersionDefault was skipped: Versioning isn't supported");
        }
    }

    public void testGetPropertiesOfLatestVersionFiltered() throws Exception
    {
        if (isVersioningAllowed())
        {
            GetPropertiesOfLatestVersionResponse response = null;
            String versionSeriesId = getIdProperty(documentId, "cmis:VersionSeriesId");
            assertNotNull("'VersionSeriesId' property is NULL", versionSeriesId);
            try
            {
                response = getServicesFactory().getVersioningService().getPropertiesOfLatestVersion(
                        new GetPropertiesOfLatestVersion(getAndAssertRepositoryId(), versionSeriesId, true, "cmis:Name, cmis:ObjectId, cmis:IsLatestVersion", false));
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
            assertNotNull(response);
            assertNotNull(response.getObject());
            CmisObjectType objectType = response.getObject();
            assertNotNull(objectType.getProperties());

            assertNull(objectType.getProperties().getPropertyDecimal());
            assertNull(objectType.getProperties().getPropertyHtml());
            assertNull(objectType.getProperties().getPropertyInteger());
            assertNull(objectType.getProperties().getPropertyUri());
            assertNull(objectType.getProperties().getPropertyXml());
            assertNull(objectType.getProperties().getPropertyDateTime());

            assertNotNull(objectType.getProperties().getPropertyId());
            assertNotNull(objectType.getProperties().getPropertyString());
            assertNotNull(objectType.getProperties().getPropertyBoolean());

            assertEquals(1, objectType.getProperties().getPropertyId().length);
            assertEquals(1, objectType.getProperties().getPropertyString().length);
            assertEquals(1, objectType.getProperties().getPropertyBoolean().length);

            assertNotNull(getIdProperty(objectType.getProperties(), PROP_OBJECT_ID));
            assertNotNull(getStringProperty(objectType.getProperties(), PROP_NAME));
            assertNotNull(getBooleanProperty(objectType.getProperties(), "cmis:IsLatestVersion"));

            assertTrue((Boolean) getBooleanProperty(objectType.getProperties(), "cmis:IsLatestVersion"));
        }
        else
        {
            LOGGER.info("testGetPropertiesOfLatestVersionFiltered was skipped: Versioning isn't supported");
        }
    }

    public void testGetAllVersionsDefault() throws Exception
    {
        if (isVersioningAllowed())
        {
            String checkinComment = "Test checkin" + System.currentTimeMillis();

            CheckOutResponse checkOutResponse = getServicesFactory().getVersioningService().checkOut(new CheckOut(getAndAssertRepositoryId(), documentId));
            assertNotNull("Checkout response is NULL", checkOutResponse);
            documentIdHolder = checkOutResponse.getDocumentId();
            CheckInResponse checkInResponse = getServicesFactory().getVersioningService().checkIn(
                    new CheckIn(getAndAssertRepositoryId(), documentIdHolder, true, new CmisPropertiesType(), new CmisContentStreamType(BigInteger.valueOf(0), MIMETYPE_TEXT_PLAIN,
                            TEST_FILE_NAME, null, TEST_CONTENT.getBytes(), null), checkinComment, null, null, null));
            assertNotNull("Checkin response is NULL", checkInResponse);
            documentId = checkInResponse.getDocumentId();

            CmisObjectType[] response = null;
            String versionSeriesId = getIdProperty(documentId, "cmis:VersionSeriesId");
            assertNotNull("'VersionSeriesId' property is NULL", versionSeriesId);
            try
            {
                response = getServicesFactory().getVersioningService().getAllVersions(new GetAllVersions(getAndAssertRepositoryId(), versionSeriesId, null, null, null));
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
            assertNotNull(response);
            assertTrue(response.length > 0);
            assertNotNull(response[0]);
            assertEquals(checkinComment, getStringProperty(response[0].getProperties(), "cmis:CheckinComment"));
        }
        else
        {
            LOGGER.info("testGetAllVersionsDefault was skipped: Versioning isn't supported");
        }
    }

    public void testGetAllVersionsFiltered() throws Exception
    {
        if (isVersioningAllowed())
        {
            String checkinComment = "Test checkin" + System.currentTimeMillis();

            CheckOutResponse checkOutResponse = getServicesFactory().getVersioningService().checkOut(new CheckOut(getAndAssertRepositoryId(), documentId));
            assertNotNull("Checkout response is NULL", checkOutResponse);
            documentIdHolder = checkOutResponse.getDocumentId();
            CheckInResponse checkInResponse = getServicesFactory().getVersioningService().checkIn(
                    new CheckIn(getAndAssertRepositoryId(), documentIdHolder, true, new CmisPropertiesType(), new CmisContentStreamType(BigInteger.valueOf(0), MIMETYPE_TEXT_PLAIN,
                            TEST_FILE_NAME, null, TEST_CONTENT.getBytes(), null), checkinComment, null, null, null));
            assertNotNull("Checkin response is NULL", checkInResponse);
            documentId = checkInResponse.getDocumentId();

            CmisObjectType[] response = null;
            String versionSeriesId = getIdProperty(documentId, "cmis:VersionSeriesId");
            assertNotNull("'VersionSeriesId' property is NULL", versionSeriesId);
            try
            {
                response = getServicesFactory().getVersioningService().getAllVersions(
                        new GetAllVersions(getAndAssertRepositoryId(), versionSeriesId, "cmis:Name, cmis:ObjectId, cmis:CheckinComment", null, null));
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
            assertNotNull(response);
            assertTrue(response.length > 0);
            assertNotNull(response[0]);

            for (CmisObjectType object : response)
            {
                CmisPropertiesType properties = object.getProperties();

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
                assertTrue(2 >= properties.getPropertyString().length);

                assertNotNull(getIdProperty(properties, PROP_OBJECT_ID));
                assertNotNull(getStringProperty(properties, PROP_NAME));
            }
        }
        else
        {
            LOGGER.info("testGetAllVersionsFiltered was skipped: Versioning isn't supported");
        }
    }

    public void testGetAllVersionsForNoVersionHistory() throws Exception
    {
        if (isVersioningAllowed())
        {
            CmisObjectType[] response = null;
            String versionSeriesId = getIdProperty(documentId, "cmis:VersionSeriesId");
            assertNotNull("'VersionSeriesId' property is NULL", versionSeriesId);
            try
            {
                response = getServicesFactory().getVersioningService().getAllVersions(new GetAllVersions(getAndAssertRepositoryId(), versionSeriesId, "*", null, null));
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
            assertNotNull(response);
            assertTrue(response.length > 0);
            assertNotNull(response[0]);
        }
        else
        {
            LOGGER.info("testGetAllVersionsForNoVersionHistory was skipped: Versioning isn't supported");
        }
    }

    public void testGetAllVersionsPWC() throws Exception
    {
        if (isVersioningAllowed())
        {
            CheckOutResponse checkOutResponse = getServicesFactory().getVersioningService().checkOut(new CheckOut(getAndAssertRepositoryId(), documentId));
            assertNotNull("Checkout response is NULL", checkOutResponse);
            documentIdHolder = checkOutResponse.getDocumentId();

            CmisObjectType[] response = null;
            String versionSeriesId = getIdProperty(documentId, "cmis:VersionSeriesId");
            assertNotNull("'VersionSeriesId' property is NULL", versionSeriesId);
            try
            {
                response = getServicesFactory().getVersioningService().getAllVersions(new GetAllVersions(getAndAssertRepositoryId(), versionSeriesId, "*", null, null));
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
            assertNotNull(response);
            assertTrue(response.length > 0);
            assertNotNull(response[0]);

            // TODO uncomment when PWC Id will be corrected
            // boolean pwcFound = false;
            // for (CmisObjectType cmisObjectType : response.getObject())
            // {
            // if (!pwcFound)
            // {
            // pwcFound = getIdProperty(cmisObjectType.getProperties(), "ObjectId").equals(documentIdHolder);
            // }
            // }
            // assertTrue("No private working copy version found", pwcFound);
        }
        else
        {
            LOGGER.info("testGetAllVersionsPWC was skipped: Versioning isn't supported");
        }
    }

    public void testDeleteAllVersions() throws Exception
    {
        if (isVersioningAllowed())
        {
            String versionSeriesId = getIdProperty(documentId, "cmis:VersionSeriesId");
            assertNotNull("'VersionSeriesId' property is NULL", versionSeriesId);
            try
            {
                // FIXME: uncomment this when schema will be corrected
                // getServicesFactory().getVersioningService().deleteAllVersions(new DeleteAllVersions(getAndAssertRepositoryId(), versionSeriesId));
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
        }
        else
        {
            LOGGER.info("testDeleteAllVersions was skipped: Versioning isn't supported");
        }
    }

    public void testWrongRepositoryIdUsing() throws Exception
    {
        if (isVersioningAllowed())
        {
            try
            {
                getServicesFactory().getVersioningService().cancelCheckOut(new CancelCheckOut(INVALID_REPOSITORY_ID, documentId));
                fail("Repository with specified Id was not described with RepositoryService");
            }
            catch (Exception e)
            {
            }
            try
            {
                documentIdHolder = getServicesFactory().getVersioningService().checkOut(new CheckOut(INVALID_REPOSITORY_ID, documentId)).getDocumentId();
                fail("Repository with specified Id was not described with RepositoryService");
            }
            catch (Exception e)
            {
            }
            try
            {
                getServicesFactory().getVersioningService().checkIn(
                        new CheckIn(INVALID_REPOSITORY_ID, documentId, true, new CmisPropertiesType(), new CmisContentStreamType(BigInteger.valueOf(0), MIMETYPE_TEXT_PLAIN,
                                TEST_FILE_NAME, null, new byte[0], null), TEST_CHECK_IN_COMMENT_MESSAGE, null, null, null));
                fail("Repository with specified Id was not described with RepositoryService");
            }
            catch (Exception e)
            {
            }
            String versionSeriesId = getIdProperty(documentId, "cmis:VersionSeriesId");
            assertNotNull("'VersionSeriesId' property is NULL", versionSeriesId);
            try
            {
                getServicesFactory().getVersioningService().getAllVersions(new GetAllVersions(INVALID_REPOSITORY_ID, versionSeriesId, "*", null, null));
                fail("Repository with specified Id was not described with RepositoryService");
            }
            catch (Exception e)
            {
            }
            try
            {
                getServicesFactory().getVersioningService().getPropertiesOfLatestVersion(
                        new GetPropertiesOfLatestVersion(INVALID_REPOSITORY_ID, versionSeriesId, true, null, false));
                fail("Repository with specified Id was not described with RepositoryService");
            }
            catch (Exception e)
            {
            }
            try
            {
                // FIXME: uncomment this when schema will be corrected
                // getServicesFactory().getVersioningService().deleteAllVersions(new DeleteAllVersions(INVALID_REPOSITORY_ID, versionSeriesId));
                // fail("Repository with specified Id was not described with RepositoryService");
            }
            catch (Exception e)
            {
            }
        }
        else
        {
            LOGGER.info("testWrongRepositoryIdUsing was skipped: Versioning isn't supported");
        }
    }
}
