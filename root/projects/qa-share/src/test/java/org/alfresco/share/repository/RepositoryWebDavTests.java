/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.share.repository;

import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.*;

/**
 * @author Sergey Kardash
 */
@Listeners(FailedTestListener.class)
@Test(groups = "EnterpriseOnly", timeOut = 400000)
public class RepositoryWebDavTests extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(RepositoryWebDavTests.class);

    @Override
    @BeforeClass
    public void setup() throws Exception
    {
        super.setup();
        logger.info("Starting Tests: " + testName);
    }

    /**
     * Test: AONE-6530:Verify creating a folder
     * <ul>
     * <li>Any user is created, e.g. user1</li>
     * <li>Any site is created via Share client</li>
     * <li>Create any folder in the Document Library space in the created site via WebDav</li>
     * <li>Login to Share, check that the folder is displayed</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6530() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName) + System.currentTimeMillis();
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String folderName = getFolderName(testName);
        String remotePath = "alfresco/webdav/Sites/" + siteName + "/" + "documentLibrary/";
        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        // Create any folder in the Document Library space in the created site via WebDav
        assertTrue(WebDavUtil.createFolder(shareUrl, testUser, DEFAULT_PASSWORD, folderName, remotePath), "Can't create " + folderName + " folder");

        // Login to Share, check that the folder is displayed
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        assertTrue(documentLibraryPage.isItemVisble(folderName), folderName + " folder isn't displayed");

    }

    /**
     * Test: AONE-6531:Verify adding a content item
     * <ul>
     * <li>Any user is created, e.g. user1</li>
     * <li>Any site is created via Share client</li>
     * <li>Copy any file from to the Document Library space in the created site via WebDav</li>
     * <li>Log in to Share client and navigate to the Document Library</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6531() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName) + System.currentTimeMillis();
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";
        File file = newFile(fileName, fileName);
        file.deleteOnExit();

        String remotePath = "alfresco/webdav/Sites/" + siteName + "/" + "documentLibrary/";
        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        // Copy any file from to the Document Library space in the created site via WebDav
        assertTrue(WebDavUtil.uploadContent(shareUrl, testUser, DEFAULT_PASSWORD, file, remotePath), "Can't create " + fileName + " content");

        assertTrue(WebDavUtil.isObjectExists(shareUrl, testUser, DEFAULT_PASSWORD, fileName, remotePath), fileName + " content is not exist.");

        // Login to Share, check that the file is displayed
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        assertTrue(documentLibraryPage.isItemVisble(fileName), fileName + " file isn't displayed");

    }

    /**
     * Test: AONE-6532:Verify renaming a folder
     * <ul>
     * <li>Any user is created, e.g. user1</li>
     * <li>Any site is created via Share client</li>
     * <li>Any folder is created in the Document Library in the created site via Share client</li>
     * <li>Rename created folder via WebDav</li>
     * <li>Log in to Share client and navigate to the Document Library</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6532() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName) + System.currentTimeMillis();
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String folderName = getFolderName(testName);
        String folderNewName = folderName + "WebDav";
        String remotePath = "alfresco/webdav/Sites/" + siteName + "/" + "documentLibrary/" + folderName;
        String remoteNewPath = "alfresco/webdav/Sites/" + siteName + "/" + "documentLibrary/" + folderNewName;
        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // Any folder is created
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        assertTrue(documentLibraryPage.isItemVisble(folderName), "Folder " + folderName + " isn't created");
        ShareUser.logout(drone);

        // Rename created folder via WebDav
        assertTrue(WebDavUtil.renameItem(shareUrl, testUser, DEFAULT_PASSWORD, remotePath, remoteNewPath), "Can't rename " + folderName + " folder");

        // The folder is displayed with renamed name in Share client
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        assertTrue(documentLibraryPage.isItemVisble(folderNewName), folderNewName + " folder with new name isn't displayed");
        assertFalse(documentLibraryPage.isItemVisble(folderName), folderName + " folder with old name is displayed");
    }

    /**
     * Test: AONE-6533:Verify renaming a content
     * <ul>
     * <li>Any user is created, e.g. user1</li>
     * <li>Any site is created via Share client</li>
     * <li>Any content is uploaded in the Document Library in the created site via Share client</li>
     * <li>Rename uploaded content via WebDav</li>
     * <li>Log in to Share client and navigate to the Document Library</li>
     * <li>The content is displayed with renamed name in Share client</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6533() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName);
        String fileNewName = fileName + "-WebDav";
        String remotePath = "alfresco/webdav/Sites/" + siteName + "/" + "documentLibrary/" + fileName;
        String remoteNewPath = "alfresco/webdav/Sites/" + siteName + "/" + "documentLibrary/" + fileNewName;
        File file = newFile(fileName, fileName);
        file.deleteOnExit();

        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // Upload a file
        ShareUserSitePage.uploadFile(drone, file);
        assertTrue(documentLibraryPage.isItemVisble(fileName), "The content isn't created");
        ShareUser.logout(drone);

        // Rename uploaded content by webDav;
        assertTrue(WebDavUtil.renameItem(shareUrl, testUser, DEFAULT_PASSWORD, remotePath, remoteNewPath), "Can't create " + fileName + " file");

        // Login to Share, check that content is displayed with renamed name
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // The content is displayed with renamed name in Share client
        assertTrue(documentLibraryPage.isItemVisble(fileNewName), "The content isn't renamed");
        assertFalse(documentLibraryPage.isItemVisble(fileName), "The content isn't renamed");
    }

    /**
     * Test: AONE-6534:Verify deleting a folder
     * <ul>
     * <li>Any user is created, e.g. user1</li>
     * <li>Any site is created via Share client</li>
     * <li>Any folder is created in the Document Library in the created site via Share client</li>
     * <li>Delete any folder via WebDab</li>
     * <li>Log in to Share client and navigate to the Document Library</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6534() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String folderName = getFolderName(testName);
        String remotePath = "alfresco/webdav/Sites/" + siteName + "/" + "documentLibrary/";
        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // Any folder is created
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        assertTrue(documentLibraryPage.isItemVisble(folderName), "Folder " + folderName + " isn't created");
        ShareUser.logout(drone);

        // Delete any folder via WebDab
        assertTrue(WebDavUtil.deleteItem(shareUrl, testUser, DEFAULT_PASSWORD, folderName, remotePath), "Can't delete " + folderName);

        // Login to Share, check that the folder isn't displayed in Share client;
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        assertFalse(documentLibraryPage.isItemVisble(folderName), folderName + " folder isn't deleted");

    }

    /**
     * Test: AONE-6535:Verify deleting a content item
     * <ul>
     * <li>Any user is created, e.g. user1</li>
     * <li>Any site is created via Share client</li>
     * <li>Any content is uploaded in the Document Library in the created site via Share client</li>
     * <li>Delete uploaded content via WebDav</li>
     * <li>Log in to Share client and navigate to the Document Library</li>
     * <li>The content isn't displayed in Share client</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6535() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName);
        String remotePath = "alfresco/webdav/Sites/" + siteName + "/" + "documentLibrary/";
        File file = newFile(fileName, fileName);
        file.deleteOnExit();

        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // Upload a file
        ShareUserSitePage.uploadFile(drone, file);
        assertTrue(documentLibraryPage.isItemVisble(fileName), "The content isn't created");
        ShareUser.logout(drone);

        // Delete uploaded content via WebDav
        assertTrue(WebDavUtil.deleteItem(shareUrl, testUser, DEFAULT_PASSWORD, fileName, remotePath), "Can't delete " + fileName);

        // Login to Share, check that content is displayed with renamed name
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // The content isn't displayed in Share client
        assertFalse(documentLibraryPage.isItemVisble(fileName), "The content isn't deleted");
    }

    /**
     * Test: AONE-6536:Verify editing a content
     * <ul>
     * <li>Any user is created, e.g. user1</li>
     * <li>Any site is created via Share client</li>
     * <li>Any content is uploaded in the Document Library in the created site via Share client</li>
     * <li>Edit uploaded content via WebDav</li>
     * <li>Log in to Share client and navigate to the Document Library</li>
     * <li>The changes made via WebDav are displayed</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6536() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName + ".txt");
        String remotePath = "alfresco/webdav/Sites/" + siteName + "/" + "documentLibrary/";
        File file = newFile(fileName, fileName);
        file.deleteOnExit();

        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // Upload a file
        ShareUserSitePage.uploadFile(drone, file);
        assertTrue(documentLibraryPage.isItemVisble(fileName), "The content isn't created");
        ShareUser.logout(drone);

        // Edit uploaded content via WebDav
        assertTrue(WebDavUtil.editContent(shareUrl, testUser, DEFAULT_PASSWORD, fileName, remotePath), "Content isn't edited");
        assertTrue(WebDavUtil.getContent(shareUrl, testUser, DEFAULT_PASSWORD, fileName, remotePath).equals(testUser), "Changes aren't applied");

        // Login to Share, check that the file is displayed
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();
        // navigate to the Document Library
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        assertTrue(documentLibraryPage.isItemVisble(fileName), fileName + " file isn't displayed");

        // The changes made via WebDav are displayed
        DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName).render();
        assertEquals(detailsPage.getDocumentBody().contains(testUser), true, "The changes are not displayed");
    }

    /**
     * Test: AONE-6537:Verify editing a content. Edit offline
     * <ul>
     * <li>Any user is created, e.g. user1</li>
     * <li>Any site is created via Share client</li>
     * <li>Any content is uploaded in the Document Library in the created site via Share client</li>
     * <li>Uploaded content is editing offline via Share client</li>
     * <li>Try to edit editing content via WebDav</li>
     * <li>Try to edit Working Copy of editing content via WebDav</li>
     * <li>Log in to Share client and navigate to the Document Library</li>
     * <li>The changes made via WebDav are displayed</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6537() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName + ".txt");
        String editedFileName = getFileName(testName + " (Working Copy).txt");
        File file = newFile(fileName, fileName);
        file.deleteOnExit();
        String remotePath = "alfresco/webdav/Sites/" + siteName + "/" + "documentLibrary/";

        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // Upload a file and click "Edit Ofline"
        ShareUserSitePage.uploadFile(drone, file);
        DocumentLibraryPage documentLibraryPage = drone.getCurrentPage().render();
        documentLibraryPage.getFileDirectoryInfo(fileName).selectEditOffline().render();
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isEdited(), "The file is blocked for editing");
        ShareUser.logout(drone);

        // Navigate to editing content via WebDav
        assertTrue(WebDavUtil.isObjectExists(shareUrl, testUser, DEFAULT_PASSWORD, fileName, remotePath), fileName + " file is not exist.");
        assertTrue(WebDavUtil.isObjectExists(shareUrl, testUser, DEFAULT_PASSWORD, editedFileName, remotePath), editedFileName + " file is not exist.");

        // Try to edit editing content
        assertFalse(WebDavUtil.editContent(shareUrl, testUser, DEFAULT_PASSWORD, fileName, remotePath), "Can edit file " + fileName + ". (Blocked content))");
        // Try to edit Working Copy of editing content via WebDav
        assertTrue(WebDavUtil.editContent(shareUrl, testUser, DEFAULT_PASSWORD, editedFileName, remotePath), "Can't edit file " + editedFileName
                + ". (Working copy))");

        // Login to Share, check that the file is displayed
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        documentLibraryPage = drone.getCurrentPage().render();
        assertEquals(ShareUserSitePage.getFileDirectoryInfo(drone, fileName).getContentInfo(), "This document is locked by you for offline editing.");
        assertEquals(ShareUserSitePage.getContentCount(drone), 1, "Incorrect document count: " + ShareUserSitePage.getContentCount(drone));

        // The changes, made via WebDav for Working Copy, are displayed
        DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName);
        assertEquals(detailsPage.getDocumentBody().contains(testUser), true, "The changes are not displayed");

    }

    /**
     * Test: AONE-6538:Verify copying a non-empty folder
     * <ul>
     * <li>Any user is created, e.g. user1</li>
     * <li>Any site is created via Share client</li>
     * <li>Any folder is created in the Document Library in the created site via Share client</li>
     * <li>Some items are added to folder via Share client</li>
     * <li>Copy folder from the site, where it is located, to the other site (to Document Library space of site) via WebDav</li>
     * <li>Try to edit Working Copy of editing content via WebDav</li>
     * <li>Log in to Share client and navigate to the Document Library</li>
     * <li>The changes made via WebDav are displayed</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6538() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName1 = getSiteName(testName + "-1-") + System.currentTimeMillis();
        String siteName2 = getSiteName(testName + "-2-") + System.currentTimeMillis();
        String fileName1 = getFileName(testName + "-1");
        String fileName2 = getFileName(testName + "-2");
        File file1 = newFile(fileName1, fileName1);
        File file2 = newFile(fileName2, fileName2);
        String folderName = getFolderName(testName);
        file1.deleteOnExit();
        file2.deleteOnExit();

        String remotePath = "alfresco/webdav/Sites/" + siteName1 + "/" + "documentLibrary/" + folderName;
        String destination = "alfresco/webdav/Sites/" + siteName2 + "/" + "documentLibrary/" + folderName;

        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, siteName2, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName1).render();

        // Any folder is created, some items are added to folder
        ShareUserSitePage.createFolder(drone, folderName, folderName).render();
        ShareUserSitePage.navigateToFolder(drone, folderName);
        ShareUserSitePage.uploadFile(drone, file1);
        ShareUserSitePage.uploadFile(drone, file2);

        // Copy folder from the site 1 to the site 2 (to Document Library space of site) by WebDav
        assertTrue(WebDavUtil.copyContent(shareUrl, testUser, DEFAULT_PASSWORD, remotePath, destination, false), "Can't copy " + folderName
                + " folder (via WebDav)");

        // Log in to Share and navigate to the Document Library of the site 1, check that folder is present here
        ShareUser.login(drone, testUser);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName1).render();
        assertTrue(documentLibraryPage.isItemVisble(folderName), folderName + " folder isn't displayed (first site)");

        // Navigate to the site2, check that folder is copied here.
        ShareUser.openSitesDocumentLibrary(drone, siteName2).render();
        assertTrue(documentLibraryPage.isItemVisble(folderName), folderName + " folder isn't displayed (second site)");

        // Verify that folder has all items;
        ShareUserSitePage.navigateToFolder(drone, folderName);
        assertTrue(documentLibraryPage.isItemVisble(fileName1), fileName1 + " isn't displayed (first file)");
        assertTrue(documentLibraryPage.isItemVisble(fileName2), fileName2 + " isn't displayed (second file)");
    }

    /**
     * Test: AONE-6539:Coordinator. Available actions
     * <ul>
     * <li>Any site is created via Share client</li>
     * <li>At least one folder is created by admin user in Document Library space via Share client</li>
     * <li>Any content is added by admin user in Document Library space via Share client</li>
     * <li>user with Coordinator role for the created folder</li>
     * <li>Navigate to created folder via WebDav</li>
     * <li>Try to rename folder</li>
     * <li>Try to rename content in this folder</li>
     * <li>Try to edit content</li>
     * <li>Try to delete content</li>
     * <li>Try to create new folder</li>
     * <li>Try to upload new content</li>
     * <li>Try to delete folder</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6539() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + "-manager-1");
        String testUser2 = getUserNameFreeDomain(testName + "-manager-2");
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName);
        File file = newFile(fileName, fileName);
        file.deleteOnExit();
        String folderName = getFolderName(testName);
        String folderNewName = folderName + "-WebDav";
        String fileNewName = fileName + "-WebDav";

        String remotePath = "alfresco/webdav/Sites/" + siteName + "/" + "documentLibrary/";
        String remotePathToFolder = "alfresco/webdav/Sites/" + siteName + "/" + "documentLibrary/" + folderNewName;

        // Create users
        String[] testUserInfo1 = new String[] { testUser1 };
        String[] testUserInfo2 = new String[] { testUser2 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

        // Any site is created
        ShareUser.login(drone, testUser1);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // Any folder is created
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUserSitePage.navigateToFolder(drone, folderName);
        ShareUserSitePage.uploadFile(drone, file);

        // User1 invites the users to the site with manager role
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName, UserRole.MANAGER);

        // Navigate to created folder by WebDav as manager
        assertTrue(WebDavUtil.isObjectExists(shareUrl, testUser2, DEFAULT_PASSWORD, folderName + "/", remotePath), folderName + " file is not exist.");

        // Verify the possibility to rename a folder by WebDav;
        assertTrue(WebDavUtil.renameItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePath + folderName, remotePathToFolder), "Can't rename " + folderName);

        // Verify the possibility to rename a content in this folder by WebDav;
        assertTrue(WebDavUtil.renameItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder + "/" + fileName, remotePathToFolder + "/" + fileNewName),
                "Can't rename " + fileName);

        // Verify the possibility to edit a content by WebDav;
        assertTrue(WebDavUtil.editContent(shareUrl, testUser2, DEFAULT_PASSWORD, fileNewName, remotePathToFolder + "/"), "Can't edit " + fileNewName);
        assertTrue(WebDavUtil.getContent(shareUrl, testUser2, DEFAULT_PASSWORD, fileNewName, remotePathToFolder + "/").equals(testUser2));

        // Verify the possibility to delete a content by WebDav;
        assertTrue(WebDavUtil.deleteItem(shareUrl, testUser2, DEFAULT_PASSWORD, fileNewName, remotePathToFolder + "/"), "Can't delete " + fileNewName);

        // Verify the possibility to create new folder by WebDav;
        assertTrue(WebDavUtil.createFolder(shareUrl, testUser2, DEFAULT_PASSWORD, folderName, remotePathToFolder + "/"), "Can't create " + folderName
                + " folder");

        // Verify the possibility to upload new content by WebDav;
        assertTrue(WebDavUtil.uploadContent(shareUrl, testUser2, DEFAULT_PASSWORD, file, remotePathToFolder + "/"), "Can't upload " + fileName + " content");

        // Verify the possibility to delete a folder by WebDav;
        assertTrue(WebDavUtil.deleteItem(shareUrl, testUser2, DEFAULT_PASSWORD, folderNewName, remotePath), "Can't delete " + folderNewName + " folder");
    }

    /**
     * Test: AONE-6540:Collaborator. Available actions
     * <ul>
     * <li>Any site is created via Share client</li>
     * <li>At least one folder is created by admin user in Document Library space via Share client</li>
     * <li>Any content is added by admin user in Document Library space via Share client</li>
     * <li>user with Collaborator role for the created folder</li>
     * <li>Navigate to created folder via WebDav</li>
     * <li>Try to rename folder</li>
     * <li>Try to rename content in this folder</li>
     * <li>Try to edit content</li>
     * <li>Try to delete content</li>
     * <li>Try to create new folder</li>
     * <li>Try to upload new content</li>
     * <li>Try to delete folder</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6540() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + "-manager");
        String testUser2 = getUserNameFreeDomain(testName + "-collaborator");
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName);
        File file = newFile(fileName, fileName);
        file.deleteOnExit();
        String folderName = getFolderName(testName);
        String folderNewName = folderName + "-WebDav";
        String fileNewName = fileName + "-WebDav";

        String remotePath = "alfresco/webdav/Sites/" + siteName + "/" + "documentLibrary/";
        String remotePathToFolder = "alfresco/webdav/Sites/" + siteName + "/" + "documentLibrary/" + folderNewName;

        // Create users
        String[] testUserInfo1 = new String[] { testUser1 };
        String[] testUserInfo2 = new String[] { testUser2 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

        // Any site is created
        ShareUser.login(drone, testUser1);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // Any folder is created
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUserSitePage.navigateToFolder(drone, folderName);
        ShareUserSitePage.uploadFile(drone, file);

        // User1 invites the users to the site with collaborator role
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName, UserRole.COLLABORATOR);

        // Navigate to created folder by WebDav as collaborator
        assertTrue(WebDavUtil.isObjectExists(shareUrl, testUser2, DEFAULT_PASSWORD, folderName + "/", remotePath), folderName + " file is not exist.");

        // Verify the possibility to rename a folder by WebDav;
        assertTrue(WebDavUtil.renameItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePath + folderName, remotePathToFolder), "Can't rename " + folderName);

        // Verify the possibility to rename a content in this folder by WebDav;
        assertTrue(WebDavUtil.renameItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder + "/" + fileName, remotePathToFolder + "/" + fileNewName),
                "Can't rename " + fileName);

        // Verify the possibility to edit a content by WebDav;
        assertTrue(WebDavUtil.editContent(shareUrl, testUser2, DEFAULT_PASSWORD, fileNewName, remotePathToFolder + "/"), "Can't edit " + fileNewName);
        assertTrue(WebDavUtil.getContent(shareUrl, testUser2, DEFAULT_PASSWORD, fileNewName, remotePathToFolder + "/").equals(testUser2));

        // Verify the possibility to delete a content by WebDav;
        assertFalse(WebDavUtil.deleteItem(shareUrl, testUser2, DEFAULT_PASSWORD, fileNewName, remotePathToFolder + "/"), "Can delete " + fileNewName);

        // Verify the possibility to create new folder by WebDav;
        assertTrue(WebDavUtil.createFolder(shareUrl, testUser2, DEFAULT_PASSWORD, folderName, remotePathToFolder + "/"), "Can't create " + folderName
                + " folder");

        // Verify the possibility to upload new content by WebDav;
        assertTrue(WebDavUtil.uploadContent(shareUrl, testUser2, DEFAULT_PASSWORD, file, remotePathToFolder + "/"), "Can't upload " + fileName + " content");

        // Verify the possibility to delete a folder by WebDav;
        assertFalse(WebDavUtil.deleteItem(shareUrl, testUser2, DEFAULT_PASSWORD, folderNewName, remotePath), "Can delete " + folderNewName + " folder");
        assertTrue(WebDavUtil.deleteItem(shareUrl, testUser2, DEFAULT_PASSWORD, folderName, remotePathToFolder + "/"), "Can't delete " + folderName + " folder");
        assertTrue(WebDavUtil.deleteItem(shareUrl, testUser2, DEFAULT_PASSWORD, fileName, remotePathToFolder + "/"), "Can't delete " + file + " folder");
    }

    /**
     * Test: AONE-6541:Contributor. Available actions
     * <ul>
     * <li>Any site is created via Share client</li>
     * <li>At least one folder is created by admin user in Document Library space via Share client</li>
     * <li>Any content is added by admin user in Document Library space via Share client</li>
     * <li>user with Contributor role for the created folder</li>
     * <li>Navigate to created folder via WebDav</li>
     * <li>Try to rename folder</li>
     * <li>Try to rename content in this folder</li>
     * <li>Try to edit content</li>
     * <li>Try to delete content</li>
     * <li>Try to create new folder</li>
     * <li>Try to upload new content</li>
     * <li>Try to delete folder</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6541() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + "-manager");
        String testUser2 = getUserNameFreeDomain(testName + "-contributor");
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName);
        File file = newFile(fileName, fileName);
        file.deleteOnExit();
        String folderName = getFolderName(testName);
        String folderNewName = folderName + "-WebDav";
        String fileNewName = fileName + "-WebDav";
        File newFile = newFile(fileNewName, fileNewName);
        newFile.deleteOnExit();

        String remotePath = "alfresco/webdav/Sites/" + siteName + "/" + "documentLibrary/";
        String remotePathToFolder = "alfresco/webdav/Sites/" + siteName + "/" + "documentLibrary/" + folderName;

        // Create users
        String[] testUserInfo1 = new String[] { testUser1 };
        String[] testUserInfo2 = new String[] { testUser2 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

        // Any site is created
        ShareUser.login(drone, testUser1);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // Any folder is created
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUserSitePage.navigateToFolder(drone, folderName);
        ShareUserSitePage.uploadFile(drone, file);

        // User1 invites the users to the site with contributor role
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName, UserRole.CONTRIBUTOR);

        // Navigate to created folder by WebDav as contributor
        assertTrue(WebDavUtil.isObjectExists(shareUrl, testUser2, DEFAULT_PASSWORD, folderName + "/", remotePath), folderName + " file is not exist.");

        // Verify the possibility to rename a folder by WebDav;
        assertFalse(WebDavUtil.renameItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePath + folderName, remotePath + folderNewName), "Can rename "
                + folderName);

        // Verify the possibility to rename a content in this folder by WebDav;
        assertFalse(WebDavUtil.renameItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder + "/" + fileName, remotePathToFolder + "/" + fileNewName),
                "Can rename " + fileName);

        // Verify the possibility to edit a content by WebDav;
        assertFalse(WebDavUtil.editContent(shareUrl, testUser2, DEFAULT_PASSWORD, fileName, remotePathToFolder + "/"), "Can edit " + fileName);
        assertTrue(WebDavUtil.getContent(shareUrl, testUser2, DEFAULT_PASSWORD, fileName, remotePathToFolder + "/").equals(fileName), "Can edit " + fileName);

        // Verify the possibility to delete a content by WebDav;
        assertFalse(WebDavUtil.deleteItem(shareUrl, testUser2, DEFAULT_PASSWORD, fileName, remotePathToFolder + "/"), "Can delete " + fileName);

        // Verify the possibility to create new folder by WebDav;
        assertTrue(WebDavUtil.createFolder(shareUrl, testUser2, DEFAULT_PASSWORD, folderNewName, remotePathToFolder + "/"), "Can't create " + folderNewName
                + " folder");

        // Verify the possibility to upload new content by WebDav;
        assertTrue(WebDavUtil.uploadContent(shareUrl, testUser2, DEFAULT_PASSWORD, newFile, remotePathToFolder + "/"), "Can't upload " + fileNewName + " content");

        // Verify the possibility to delete a folder by WebDav;
        assertFalse(WebDavUtil.deleteItem(shareUrl, testUser2, DEFAULT_PASSWORD, folderName, remotePath), "Can delete " + folderName + " folder");
        assertTrue(WebDavUtil.deleteItem(shareUrl, testUser2, DEFAULT_PASSWORD, folderNewName, remotePathToFolder + "/"), "Can't delete " + folderNewName
                + " folder");
        assertTrue(WebDavUtil.deleteItem(shareUrl, testUser2, DEFAULT_PASSWORD, fileNewName, remotePathToFolder + "/"), "Can't delete " + fileNewName
                + " folder");
    }

    /**
     * Test: AONE-6542:Consumer. Available actions
     * <ul>
     * <li>Any site is created via Share client</li>
     * <li>At least one folder is created by admin user in Document Library space via Share client</li>
     * <li>Any content is added by admin user in Document Library space via Share client</li>
     * <li>user with Consumer role for the created folder</li>
     * <li>Navigate to created folder via WebDav</li>
     * <li>Try to rename folder</li>
     * <li>Try to rename content in this folder</li>
     * <li>Try to edit content</li>
     * <li>Try to delete content</li>
     * <li>Try to create new folder</li>
     * <li>Try to upload new content</li>
     * <li>Try to delete folder</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6542() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + "-manager");
        String testUser2 = getUserNameFreeDomain(testName + "-consumer");
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName);
        File file = newFile(fileName, fileName);
        String folderName = getFolderName(testName);
        String folderNewName = folderName + "-WebDav";
        String fileNewName = fileName + "-WebDav";
        File newFile = newFile(fileNewName, fileNewName);
        newFile.deleteOnExit();

        String remotePath = "alfresco/webdav/Sites/" + siteName + "/" + "documentLibrary/";
        String remotePathToFolder = "alfresco/webdav/Sites/" + siteName + "/" + "documentLibrary/" + folderName;

        // Create users
        String[] testUserInfo1 = new String[] { testUser1 };
        String[] testUserInfo2 = new String[] { testUser2 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

        // Any site is created
        ShareUser.login(drone, testUser1);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // Any folder is created
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUserSitePage.navigateToFolder(drone, folderName);
        ShareUserSitePage.uploadFile(drone, file);

        // User1 invites the users to the site with Consumer role
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName, UserRole.CONSUMER);

        // Navigate to created folder by WebDav as consumer
        assertTrue(WebDavUtil.isObjectExists(shareUrl, testUser2, DEFAULT_PASSWORD, folderName + "/", remotePath), folderName + " file is not exist.");

        // Verify the possibility to rename a folder by WebDav;
        assertFalse(WebDavUtil.renameItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePath + folderName, remotePath + folderNewName), "Can rename "
                + folderName);

        // Verify the possibility to rename a content in this folder by WebDav;
        assertFalse(WebDavUtil.renameItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder + "/" + fileName, remotePathToFolder + "/" + fileNewName),
                "Can rename " + fileName);

        // Verify the possibility to edit a content by WebDav;
        assertFalse(WebDavUtil.editContent(shareUrl, testUser2, DEFAULT_PASSWORD, fileName, remotePathToFolder + "/"), "Can edit " + fileName);
        assertTrue(WebDavUtil.getContent(shareUrl, testUser2, DEFAULT_PASSWORD, fileName, remotePathToFolder + "/").equals(fileName), "Can edit " + fileName);

        // Verify the possibility to delete a content by WebDav;
        assertFalse(WebDavUtil.deleteItem(shareUrl, testUser2, DEFAULT_PASSWORD, fileName, remotePathToFolder + "/"), "Can delete " + fileName);

        // Verify the possibility to create new folder by WebDav;
        assertFalse(WebDavUtil.createFolder(shareUrl, testUser2, DEFAULT_PASSWORD, folderNewName, remotePathToFolder + "/"), "Can create " + folderNewName
                + " folder");

        // Verify the possibility to upload new content by WebDav;
        assertFalse(WebDavUtil.uploadContent(shareUrl, testUser2, DEFAULT_PASSWORD, newFile, remotePathToFolder + "/"), "Can upload " + fileNewName + " content");

        // Verify the possibility to delete a folder by WebDav;
        assertFalse(WebDavUtil.deleteItem(shareUrl, testUser2, DEFAULT_PASSWORD, folderName, remotePath), "Can delete " + folderName + " folder");

    }
}
