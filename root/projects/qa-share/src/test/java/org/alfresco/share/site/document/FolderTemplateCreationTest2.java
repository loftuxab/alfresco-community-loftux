/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.share.site.document;

import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.rest.api.tests.client.PublicApiClient;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CmisUtils;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.alfresco.share.enums.CMISBinding.ATOMPUB11;
import static org.apache.chemistry.opencmis.commons.enums.VersioningState.MAJOR;
import static org.testng.Assert.assertTrue;

/**
 * @author Maryia Zaichanka
 */
public class FolderTemplateCreationTest2 extends CmisUtils
{
    private static final Logger logger = Logger.getLogger(FolderTemplateCreationTest2.class);
    private static final String DOMAIN = "-default-";

    private String folderName = "Folder" + "template";

    @Override
    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws Exception
    {
        super.beforeClass();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
    }

    @Test(groups = {"EnterpriseOnly"})
    public void AONE_15042() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        PublicApiClient.CmisSession cmisSession = getCmisSession(ATOMPUB11, ADMIN_USERNAME, DOMAIN);
        Folder rootFolder = (Folder) cmisSession.getObjectByPath("/Data Dictionary/Space Templates");

        createCustomFolder(rootFolder, folderName + 6);

        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();

        // Select the Create menu > Create folder from templates
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        documentLibraryPage.createFolderFromTemplateHover(folderName + 6).render();
        drone.getCurrentPage().render();
        assertTrue(documentLibraryPage.isFileVisible(folderName + 6), "Folder isn't created");

    }

    @Test(groups = {"EnterpriseOnly"})
    public void AONE_15043() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String contentName = getTestName() + ".txt";

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        PublicApiClient.CmisSession cmisSession = getCmisSession(ATOMPUB11, ADMIN_USERNAME, DOMAIN);
        Folder rootFolder = (Folder) cmisSession.getObjectByPath("/Data Dictionary/Space Templates");

        Folder folder = createFolderInFolder(rootFolder, folderName + 7);
        createCustomDocument(folder, contentName);

        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();

        // Select the Create menu > Create folder from templates
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        documentLibraryPage.createFolderFromTemplateHover(folderName + 7).render();
        drone.getCurrentPage().render();
        assertTrue(documentLibraryPage.isFileVisible(folderName + 7), "Folder isn't created");

        // Verify the the created folder contains the content such as the template have
        FileDirectoryInfo fileDirectory = ShareUserSitePage.getFileDirectoryInfo(drone, folderName + 7);
        fileDirectory.clickOnTitle();
        assertTrue(documentLibraryPage.isFileVisible(contentName), "Document isn't created");

    }

    public Folder createFolderInFolder(Folder folder, String newFolderName)
    {
        Map<String, String> properties = new HashMap<>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
        properties.put(PropertyIds.NAME, newFolderName);

        return folder.createFolder(properties);
    }

    public Document createCustomDocument(Folder folder, String documentName) throws IOException
    {
        Map<String, Serializable> docProperties = new HashMap<>();
        docProperties.put(PropertyIds.OBJECT_TYPE_ID, "D:cmiscustom:document");
        docProperties.put(PropertyIds.NAME, documentName);
        docProperties.put(PropertyIds.DESCRIPTION, "important document");

        ContentStreamImpl fileContent = new ContentStreamImpl();
        fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
        File tempFile = File.createTempFile("testFile" + System.currentTimeMillis(), ".txt");
        fileContent.setStream(new FileInputStream(tempFile));
        Document document = folder.createDocument(docProperties, fileContent, MAJOR);
        if (tempFile.exists())
        {
            tempFile.delete();
        }
        return document;
    }

    public void createCustomFolder(Folder folder, String name) {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "F:cmiscustom:folder");
        properties.put(PropertyIds.NAME, name);
        folder.createFolder(properties);
    }

    @Override
    @AfterClass(alwaysRun = true)
    public void tearDown()
    {
        super.tearDown();
    }

}
