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

import org.alfresco.repo.cmis.ws.AddObjectToFolder;
import org.alfresco.repo.cmis.ws.CheckInResponse;
import org.alfresco.repo.cmis.ws.CheckOutResponse;
import org.alfresco.repo.cmis.ws.CmisPropertiesType;
import org.alfresco.repo.cmis.ws.CmisPropertyId;
import org.alfresco.repo.cmis.ws.CmisRepositoryCapabilitiesType;
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
        multiFilingServicePort.addObjectToFolder(new AddObjectToFolder(getAndAssertRepositoryId(), documentId, folderId, true, null));
        multiFilingServicePort.removeObjectFromFolder(new RemoveObjectFromFolder(getAndAssertRepositoryId(), documentId, folderId, null));
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
        if (objectExists(documentId))
        {
            deleteAndAssertObject(folderId);
        }
        super.onTearDown();
    }

    public void testAddObjectToFolder() throws Exception
    {
        if (getAndAssertCapabilities().isCapabilityMultifiling())
        {
            addAndAssertObjectToFolder(documentId, folderId, false);
        }
        else
        {
            try
            {
                getServicesFactory().getMultiFilingServicePort().addObjectToFolder(new AddObjectToFolder(getAndAssertRepositoryId(), documentId, folderId, false, null));
                fail("Multi-filing is not supported but Object was Added to another Folder");
            }
            catch (Exception e)
            {
                assertException("Adding Object To Folder when Multi-Filling capability is not supported", e, EnumServiceException.notSupported);
            }
        }
    }

    private void addAndAssertObjectToFolder(String objectId, String folderId, boolean allVersions) throws Exception
    {
        try
        {
            LOGGER.info("[MultiFilingService->addObjectToFolder]");
            getServicesFactory().getMultiFilingServicePort().addObjectToFolder(new AddObjectToFolder(getAndAssertRepositoryId(), documentId, folderId, allVersions, null));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        assertTrue("Document was not Added to Folder", isDocumentInFolder(documentId, folderId));
    }

    public void testDeletingOfMultiFilledObject() throws Exception
    {
        if (getAndAssertCapabilities().isCapabilityMultifiling())
        {
            addAndAssertObjectToFolder(documentId, folderId, false);
            deleteAndAssertObject(documentId);
            assertFalse("Multi-Filled Document Object was not Deleted", objectExists(documentId));
        }
        else
        {
            LOGGER.warn("testDeletingOfMultiFilledObject() was skipped: Multi-Filling capability is not supported");
        }
    }

    public void testAddObjectToNotAllowedFolder() throws Exception
    {
        if (getAndAssertCapabilities().isCapabilityMultifiling())
        {
            CmisPropertiesType properties = new CmisPropertiesType();
            properties.setPropertyId(new CmisPropertyId[] { new CmisPropertyId(PROP_ALLOWED_CHILD_OBJECT_TYPE_IDS, null, null, null, new String[] { getAndAssertFolderTypeId() }) });
            String folderWithRestriction = createAndAssertFolder(generateTestFolderName(), getAndAssertFolderTypeId(), getAndAssertRootFolderId(), properties);
            try
            {
                LOGGER.info("[MultiFilingService->addObjectToFolder]");
                getServicesFactory().getMultiFilingServicePort().addObjectToFolder(new AddObjectToFolder(getAndAssertRepositoryId(), documentId, folderWithRestriction, false, null));
                fail("No Exception was thrown during Adding Object to Folder that cantains no Object Type Id in the list of Allowed Child Object Type Ids");
            }
            catch (Exception e)
            {
                assertException("Adding Not Allowed Object to Folder", e, EnumServiceException.constraint);
            }
        }
        else
        {
            LOGGER.warn("testAddObjectToFolderNotAllowed() was skipped: Multi-Filing capability is not supported");
        }

    }

    public void testAddObjectToFolderWithInvalidObjectId() throws Exception
    {
        if (getAndAssertCapabilities().isCapabilityMultifiling())
        {
            try
            {
                LOGGER.info("[MultiFilingService->addObjectToFolder]");
                getServicesFactory().getMultiFilingServicePort().addObjectToFolder(new AddObjectToFolder(getAndAssertRepositoryId(), "Wrong Object Id", folderId, false, null));
                fail("Inexistent Object was Added to Folder");
            }
            catch (Exception e)
            {
                assertException("Adding Inexistent Object to Folder", e, EnumServiceException.invalidArgument);
            }
        }
        else
        {
            LOGGER.warn("testAddObjectToFolderWithInvalidObjectId() was skipped: Multi-Filling capability is not supported");
        }
    }

    public void testAddObjectToInvalidFolder() throws Exception
    {
        if (getAndAssertCapabilities().isCapabilityMultifiling())
        {
            try
            {
                LOGGER.info("[MultiFilingService->addObjectToFolder]");
                getServicesFactory().getMultiFilingServicePort().addObjectToFolder(new AddObjectToFolder(getAndAssertRepositoryId(), documentId, "Wrong Object Id", false, null));
                fail("Object was Added to Inexistent Folder");
            }
            catch (Exception e)
            {
                assertException("Adding Object to Inexistent Folder", e, EnumServiceException.invalidArgument);
            }
        }
        else
        {
            LOGGER.warn("testAddObjectToInvalidFolder() was skipped: Multi-Filling capability is not supported");
        }
    }

    public void testAddObjectToFolderAgainstAllVersionsParameter() throws Exception
    {
        if (isVersioningAllowed())
        {
            CheckInResponse checkInResponse = new CheckInResponse(documentId, null);
            for (int i = 0; i < 2; i++)
            {
                CheckOutResponse checkOutResponse = checkOutAndAssert(checkInResponse.getDocumentId());
                checkInAndAssert(checkOutResponse.getDocumentId(), true, new CmisPropertiesType(), createUniqueContentStream(), "");
            }
            try
            {
                LOGGER.info("[MultiFilingService->addObjectToFolder]");
                getServicesFactory().getMultiFilingServicePort().addObjectToFolder(
                        new AddObjectToFolder(getAndAssertRepositoryId(), checkInResponse.getDocumentId(), folderId, true, null));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            if (getAndAssertCapabilities().isCapabilityVersionSpecificFiling())
            {
                assertTrue("Document was not added to folder", isDocumentInFolder(documentId, folderId));
            }
            else
            {
                assertFalse("Version specific filing is not supported, but document was added to folder", isDocumentInFolder(documentId, folderId));
            }
        }
        else
        {
            LOGGER.warn("testCapabilityVersionSpecificFiling() was skipped: Versioning isn't supported");
        }
    }

    public void testRemoveObjectFromFolder() throws Exception
    {
        if (getAndAssertCapabilities().isCapabilityMultifiling())
        {
            addAndAssertObjectToFolder(documentId, folderId, false);
            try
            {
                LOGGER.info("[MultiFilingService->removeObjectFromFolder]");
                getServicesFactory().getMultiFilingServicePort().removeObjectFromFolder(new RemoveObjectFromFolder(getAndAssertRepositoryId(), documentId, folderId, null));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertFalse("Object was not Removed from Folder", isDocumentInFolder(documentId, folderId));
        }
        else
        {
            LOGGER.warn("testRemoveObjectFromFolder() was skipped: Multi-Filling capability is not supported");
        }
    }

    public void testRemovingObjectFromPrimaryParentFolder() throws Exception
    {
        try
        {
            LOGGER.info("[MultiFilingService->removeObjectFromFolder]");
            getServicesFactory().getMultiFilingServicePort().removeObjectFromFolder(
                    new RemoveObjectFromFolder(getAndAssertRepositoryId(), documentId, getAndAssertRootFolderId(), null));
            if (!getAndAssertCapabilities().isCapabilityUnfiling())
            {
                fail("Unfiling is not supported but an Object was Removed from the Primary Parent Folder");
            }
            else
            {
                assertTrue("Document Object was not Unfilled", isDocumentInFolder(documentId, getAndAssertRootFolderId()));
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
                assertException("Removing Not Multi-Filled Document from Primary Parent Folder", e, EnumServiceException.notSupported);
            }
        }
        assertTrue("Unfilling attempting was resulted with Object Deletion", objectExists(documentId));
    }

    public void testRemoveInvalidObjectFromFolder() throws Exception
    {
        try
        {
            LOGGER.info("[MultiFilingService->removeObjectFromFolder]");
            getServicesFactory().getMultiFilingServicePort().removeObjectFromFolder(new RemoveObjectFromFolder(getAndAssertRepositoryId(), "Wrong Object Id", folderId, null));
            fail("Inexistent Object was Removed from Folder");
        }
        catch (Exception e)
        {
            assertException("Removing Inexistent Object from Folder", e, EnumServiceException.invalidArgument);
        }
    }

    public void testRemoveObjectFromInvalidFolder() throws Exception
    {
        try
        {
            LOGGER.info("[MultiFilingService->removeObjectFromFolder]");
            getServicesFactory().getMultiFilingServicePort().removeObjectFromFolder(new RemoveObjectFromFolder(getAndAssertRepositoryId(), documentId, "Wrong Object Id", null));
            fail("Object was Removed from Inexistent Folder");
        }
        catch (Exception e)
        {
            assertException("Removing Object from Inexistent Folder", e, EnumServiceException.invalidArgument);
        }
    }

    public void testUnfileMultiFilledObject() throws Exception
    {
        CmisRepositoryCapabilitiesType capabilities = getAndAssertCapabilities();
        if (capabilities.isCapabilityMultifiling() && capabilities.isCapabilityUnfiling())
        {
            addAndAssertObjectToFolder(documentId, folderId, false);
            try
            {
                getServicesFactory().getMultiFilingServicePort().removeObjectFromFolder(new RemoveObjectFromFolder(getAndAssertRepositoryId(), documentId, null, null));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertTrue("Unfilling attempting was resulted with Object Deletion", objectExists(documentId));
            assertFalse("Object was not Removed from Root Folder", isDocumentInFolder(documentId, getAndAssertRootFolderId()));
            assertFalse("Object was not Removed from Folder", isDocumentInFolder(documentId, folderId));
        }
        else
        {
            LOGGER.warn("testUnfileMultiFilledObject() was skipped: Multi-Filling or Un-Filling capability(s) is(are) not supported");
        }
    }

    public void testWrongRepositoryIdUsing() throws Exception
    {
        try
        {
            LOGGER.info("[MultiFilingService->addObjectToFolder]");
            getServicesFactory().getMultiFilingServicePort().addObjectToFolder(new AddObjectToFolder(INVALID_REPOSITORY_ID, documentId, folderId, true, null));
            fail("Repository with specified Id was not described with RepositoryService");
        }
        catch (Exception e)
        {
            assertException("Adding Object To Folder with Invalid Repository Id", e, EnumServiceException.invalidArgument);
        }
        try
        {
            LOGGER.info("[MultiFilingService->removeObjectFromFolder]");
            getServicesFactory().getMultiFilingServicePort().removeObjectFromFolder(new RemoveObjectFromFolder(INVALID_REPOSITORY_ID, documentId, folderId, null));
            fail("Repository with specified Id was not described with RepositoryService");
        }
        catch (Exception e)
        {
            assertException("Removing Object From Folder with Invalid Repository Id", e, EnumServiceException.invalidArgument);
        }
    }
}
