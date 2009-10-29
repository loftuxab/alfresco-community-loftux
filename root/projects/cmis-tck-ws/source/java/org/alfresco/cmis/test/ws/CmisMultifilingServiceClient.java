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

import org.alfresco.repo.cmis.ws.AddObjectToFolder;
import org.alfresco.repo.cmis.ws.CheckIn;
import org.alfresco.repo.cmis.ws.CheckInResponse;
import org.alfresco.repo.cmis.ws.CheckOut;
import org.alfresco.repo.cmis.ws.CheckOutResponse;
import org.alfresco.repo.cmis.ws.CmisContentStreamType;
import org.alfresco.repo.cmis.ws.CmisFaultType;
import org.alfresco.repo.cmis.ws.CmisPropertiesType;
import org.alfresco.repo.cmis.ws.CmisPropertyId;
import org.alfresco.repo.cmis.ws.EnumPropertiesFolder;
import org.alfresco.repo.cmis.ws.EnumServiceException;
import org.alfresco.repo.cmis.ws.MultiFilingServicePort;
import org.alfresco.repo.cmis.ws.RemoveObjectFromFolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Client for MultiFiling Service
 */
public class CmisMultifilingServiceClient extends AbstractServiceClient
{
    private static Log LOGGER = LogFactory.getLog(CmisMultifilingServiceClient.class);

    private static final String INVALID_REPOSITORY_ID = "Wrong Repository Id";

    private String folderId;

    private String documentId;

    public CmisMultifilingServiceClient(AbstractService abstractService)
    {
        super(abstractService);
    }

    public CmisMultifilingServiceClient()
    {
    }

    /**
     * Initializes MultiFiling Service client
     */
    @Override
    public void initialize() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Initializing client...");
        }

        folderId = createAndAssertFolder();
        documentId = createAndAssertDocument();
    }

    /**
     * Invokes all methods in MultiFiling Service
     */
    @Override
    public void invoke() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Invoking client...");
        }
        MultiFilingServicePort multiFilingServicePort = getServicesFactory().getMultiFilingServicePort(getProxyUrl() + getService().getPath());

        multiFilingServicePort.addObjectToFolder(new AddObjectToFolder(getAndAssertRepositoryId(), documentId, folderId));

        multiFilingServicePort.removeObjectFromFolder(new RemoveObjectFromFolder(getAndAssertRepositoryId(), documentId, folderId));
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

        deleteAndAssertObject(documentId);
        deleteAndAssertObject(folderId);
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
        AbstractServiceClient client = (CmisMultifilingServiceClient) applicationContext.getBean("cmisMultiFilingServiceClient");
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
        assertNotNull("Root Folder Id is NULL", getAndAssertRootFolderId());
        folderId = createAndAssertFolder();
        documentId = createAndAssertDocument();
        super.onSetUp();
    }

    @Override
    protected void onTearDown() throws Exception
    {
        deleteAndAssertObject(documentId);
        deleteAndAssertObject(folderId);
        super.onTearDown();
    }

    public void testAddObjectToFolder() throws Exception
    {
        try
        {
            LOGGER.info("[MultiFilingService->addObjectToFolder]");
            getServicesFactory().getMultiFilingServicePort().addObjectToFolder(new AddObjectToFolder(getAndAssertRepositoryId(), documentId, folderId));
            if (!getAndAssertCapabilities().isCapabilityMultifiling())
            {
                fail("Object is already in another folder, and multi-filing is not supported");
            }
        }
        catch (Exception e)
        {
            if (getAndAssertCapabilities().isCapabilityMultifiling())
            {
                fail(e.toString());
            }
            else
            {
                assertTrue("Invalid exception was thrown during adding object to folder while capability multi-filing is not supported", e instanceof CmisFaultType && ((CmisFaultType) e).getType().equals(EnumServiceException.constraint));
            }
        }
        assertTrue("Document was not added to folder", isDocumentInFolder(documentId, folderId));
    }

    public void testAddObjectToFolderNotAllowed() throws Exception
    {
        if (getAndAssertCapabilities().isCapabilityMultifiling())
        {
            CmisPropertiesType properties = new CmisPropertiesType();
            properties.setPropertyId(new CmisPropertyId[] { new CmisPropertyId(EnumPropertiesFolder._value2, null, null, null, new String[] { getAndAssertFolderTypeId() }) });
            String folderId1 = createAndAssertFolder(generateTestFolderName(), getAndAssertFolderTypeId(), getAndAssertRootFolderId(), properties);
            try
            {
                LOGGER.info("[MultiFilingService->addObjectToFolder]");
                getServicesFactory().getMultiFilingServicePort().addObjectToFolder(new AddObjectToFolder(getAndAssertRepositoryId(), documentId, folderId1));
                fail("No Exception was thrown during adding object to folder with typeId is NOT in the list of AllowedChildObjectTypeIds of the parent-folder specified by folderId");
            }
            catch (Exception e)
            {
                assertTrue("Invalid exception was thrown during adding object to folder with typeId is NOT in the list of AllowedChildObjectTypeIds of the parent-folder specified by folderId",
                        e instanceof CmisFaultType && ((CmisFaultType) e).getType().equals(EnumServiceException.constraint));
            }
        }
        else
        {
            LOGGER.info("testAddObjectToFolderNotAllowed was skipped: Capability multi-filing is not supported");
        }

    }

    public void testRemoveObjectFromFolder() throws Exception
    {
        try
        {
            LOGGER.info("[MultiFilingService->removeObjectFromFolder]");
            getServicesFactory().getMultiFilingServicePort().removeObjectFromFolder(new RemoveObjectFromFolder(getAndAssertRepositoryId(), documentId, getAndAssertRootFolderId()));
            if (!getAndAssertCapabilities().isCapabilityUnfiling())
            {
                fail("Unfiling is not supported, but an object was removed from the last folder");
            }
        }
        catch (Exception e)
        {
            if (getAndAssertCapabilities().isCapabilityUnfiling())
            {
                fail(e.toString());
            }
            else
            {
                assertTrue("Invalid exception was thrown during removing object from last folder while capability unfiling is not supported", e instanceof CmisFaultType && ((CmisFaultType) e).getType().equals(EnumServiceException.notSupported));
            }
        }
        boolean found = isDocumentInFolder(documentId, getAndAssertRootFolderId());
        assertTrue((found && !getAndAssertCapabilities().isCapabilityUnfiling()) || (!found && getAndAssertCapabilities().isCapabilityUnfiling()));

        if (getAndAssertCapabilities().isCapabilityMultifiling())
        {
            if (!found)
            {
                LOGGER.info("[MultiFilingService->addObjectToFolder]");
                getServicesFactory().getMultiFilingServicePort().addObjectToFolder(new AddObjectToFolder(getAndAssertRepositoryId(), documentId, getAndAssertRootFolderId()));
            }
            LOGGER.info("[MultiFilingService->addObjectToFolder]");
            getServicesFactory().getMultiFilingServicePort().addObjectToFolder(new AddObjectToFolder(getAndAssertRepositoryId(), documentId, folderId));

            try
            {
                LOGGER.info("[MultiFilingService->removeObjectFromFolder]");
                getServicesFactory().getMultiFilingServicePort().removeObjectFromFolder(new RemoveObjectFromFolder(getAndAssertRepositoryId(), documentId, folderId));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertFalse("Object was not removed from folder", isDocumentInFolder(documentId, folderId));
        }
    }

    public void testCapabilityVersionSpecificFiling() throws Exception
    {
        if (isVersioningAllowed())
        {
            String name = System.currentTimeMillis() + ".txt";
            LOGGER.info("[VersioningService->checkOut]");
            CheckOutResponse checkOutResponse = getServicesFactory().getVersioningService().checkOut(new CheckOut(getAndAssertRepositoryId(), documentId));
            LOGGER.info("[VersioningService->checkIn]");
            CheckInResponse checkInResponse = getServicesFactory().getVersioningService().checkIn(
                    new CheckIn(getAndAssertRepositoryId(), checkOutResponse.getDocumentId(), true, new CmisPropertiesType(), new CmisContentStreamType(BigInteger.valueOf(0),
                            "text/plain", name, null, "Test content 1".getBytes(), null), "", null, null, null));
            checkOutResponse = getServicesFactory().getVersioningService().checkOut(new CheckOut(getAndAssertRepositoryId(), checkInResponse.getDocumentId()));
            LOGGER.info("[VersioningService->checkIn]");
            documentId = getServicesFactory().getVersioningService().checkIn(
                    new CheckIn(getAndAssertRepositoryId(), checkOutResponse.getDocumentId(), true, new CmisPropertiesType(), new CmisContentStreamType(BigInteger.valueOf(0),
                            "text/plain", name, null, "Test content 2".getBytes(), null), "", null, null, null)).getDocumentId();
            try
            {
                LOGGER.info("[MultiFilingService->addObjectToFolder]");
                getServicesFactory().getMultiFilingServicePort().addObjectToFolder(new AddObjectToFolder(getAndAssertRepositoryId(), checkInResponse.getDocumentId(), folderId));
            }
            catch (Exception e)
            {
                if (getAndAssertCapabilities().isCapabilityVersionSpecificFiling())
                {
                    fail(e.toString());
                }
            }
            if (getAndAssertCapabilities().isCapabilityVersionSpecificFiling())
            {
                assertTrue("Document was not added to folder", isDocumentInFolder(checkOutResponse.getDocumentId(), folderId));
            }
            else
            {
                assertFalse("Version specific filing is not supported, but document was added to folder", isDocumentInFolder(checkOutResponse.getDocumentId(), folderId));
            }
        }
        else
        {
            LOGGER.info("testCapabilityVersionSpecificFiling was skipped: Versioning isn't supported");
        }
    }

    public void testWrongRepositoryIdUsing() throws Exception
    {
        try
        {
            LOGGER.info("[MultiFilingService->addObjectToFolder]");
            getServicesFactory().getMultiFilingServicePort().addObjectToFolder(new AddObjectToFolder(INVALID_REPOSITORY_ID, documentId, folderId));
            fail("Repository with specified Id was not described with RepositoryService");
        }
        catch (Exception e)
        {
        }
        try
        {
            LOGGER.info("[MultiFilingService->removeObjectFromFolder]");
            getServicesFactory().getMultiFilingServicePort().removeObjectFromFolder(new RemoveObjectFromFolder(INVALID_REPOSITORY_ID, documentId, folderId));
            fail("Repository with specified Id was not described with RepositoryService");
        }
        catch (Exception e)
        {
        }
    }
}
