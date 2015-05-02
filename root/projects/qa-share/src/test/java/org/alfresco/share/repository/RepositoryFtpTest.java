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

package org.alfresco.share.repository;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.VersionDetails;
import org.alfresco.po.share.util.PageUtils;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.FtpUtil;
import org.alfresco.share.util.RandomUtil;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.ShareUserRepositoryPage;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Created by olga.lokhach
 */
@Listeners(FailedTestListener.class)
@Test(groups = "EnterpriseOnly", timeOut = 400000)

public class RepositoryFtpTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(RepositoryFtpTest.class);
    private static String remotePathToSites = "/" + "Alfresco" + "/" + "Sites";

    @Override
    @BeforeClass
    public void setup() throws Exception
    {
        super.setup();
        logger.info("Starting Tests: " + testName);
        testName = this.getClass().getSimpleName();

        FtpUtil.setCustomFtpPort(drone, ftpPort);

    }

    /**
     * Test: AONE-6433:Creating folder
     */
    @Test
    public void AONE_6433() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String folderName = getFolderName(testName);
        String remotePath = remotePathToSites + "/" + siteName + "/" + "documentLibrary";

        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        //Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        // Create any folder in the Document Library space in the created site by FTP
        assertTrue(FtpUtil.createSpace(shareUrl, testUser, DEFAULT_PASSWORD, folderName, remotePath), "Can't create " + folderName + " folder");

        // Login to Share, check that the folder is displayed
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        DocumentLibraryPage documentLibraryPage = drone.getCurrentPage().render();
        assertTrue(documentLibraryPage.isItemVisble(folderName), folderName + " folder isn't displayed");

    }

    /**
     * AONE-6434:Creating content
     */

    @Test
    public void AONE_6434() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName);
        File file = newFile(fileName, fileName);
        String remotePath = remotePathToSites + "/" + siteName + "/" + "documentLibrary";

        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        //Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        //Create any file in the Document Library space in the created site by FTP;
        assertTrue(FtpUtil.uploadContent(shareUrl, testUser, DEFAULT_PASSWORD, file, remotePath), "Can't create " + file);

        //Login to Share, check that the file is displayed
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        DocumentLibraryPage documentLibraryPage = drone.getCurrentPage().render();
        assertTrue(documentLibraryPage.isItemVisble(fileName), fileName + " file isn't displayed");

    }

    /**
     * AONE-6435:Renaming folder
     */

    @Test
    public void AONE_6435() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String folderName = getFolderName(testName);
        String folderNewName = folderName + "-FTP";
        String remotePath = remotePathToSites + "/" + siteName + "/" + "documentLibrary";

        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        //Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        //Any folder is created
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUser.logout(drone);

        //Rename created folder by FTP
        assertTrue(FtpUtil.renameFile(shareUrl, testUser, DEFAULT_PASSWORD, remotePath, folderName, folderNewName), "Can't rename " + folderName);

        //Login to Share, check that the folder is displayed with renamed name
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        DocumentLibraryPage documentLibraryPage = drone.getCurrentPage().render();
        assertTrue(documentLibraryPage.isItemVisble(folderNewName), "The folder isn't renamed");

    }

    /**
     * AONE-6436:Renaming content
     */

    @Test
    public void AONE_6436() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName);
        File file = newFile(fileName, fileName);
        String fileNewName = fileName + "-FTP";
        String remotePath = remotePathToSites + "/" + siteName + "/" + "documentLibrary";

        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        //Create a site
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        //Upload a file
        ShareUserSitePage.uploadFile(drone, file);
        ShareUser.logout(drone);

        //Rename uploaded content by FTP;
        assertTrue(FtpUtil.renameFile(shareUrl, testUser, DEFAULT_PASSWORD, remotePath, fileName, fileNewName), "Can't rename " + fileName);

        //Login to Share, check that content is displayed with renamed name
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        DocumentLibraryPage documentLibraryPage = drone.getCurrentPage().render();
        assertTrue(documentLibraryPage.isItemVisble(fileNewName), "The content isn't renamed");
    }

    /**
     * AONE-6437:Deleting folder
     */

    @Test
    public void AONE_6437() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String folderName = getFolderName(testName);
        String remotePath = remotePathToSites + "/" + siteName + "/" + "documentLibrary";

        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        //Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        //Any folder is created
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUser.logout(drone);

        //Delete the folder by FTP
        assertTrue(FtpUtil.deleteFolder(shareUrl, testUser, DEFAULT_PASSWORD, folderName, remotePath), "Can't delete " + folderName);

        //Login to Share, check that the folder isn't  displayed in Share client;
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        DocumentLibraryPage documentLibraryPage = drone.getCurrentPage().render();
        assertFalse(documentLibraryPage.isItemVisble(folderName), folderName + " folder is displayed, but should be not");

    }

    /**
     * AONE-6438:Deleting content
     */

    @Test
    public void AONE_6438() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName);
        File file = newFile(fileName, fileName);
        String remotePath = remotePathToSites + "/" + siteName + "/" + "documentLibrary";

        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        //Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        //Upload a file
        ShareUserSitePage.uploadFile(drone, file);
        ShareUser.logout(drone);

        //Delete uploaded file by FTP
        assertTrue(FtpUtil.deleteContentItem(shareUrl, testUser, DEFAULT_PASSWORD, fileName, remotePath), "Can't delete " + fileName);

        //Login to Share, check that the file isn't  displayed in Share client;
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        DocumentLibraryPage documentLibraryPage = drone.getCurrentPage().render();
        assertFalse(documentLibraryPage.isItemVisble(fileName), fileName + " file is displayed, but should be not");
    }

    /**
     * AONE-6439:Editing content
     */

    @Test
    public void AONE_6439() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName + ".txt");
        File file = newFile(fileName, fileName);
        String remotePath = remotePathToSites + "/" + siteName + "/" + "documentLibrary";

        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        //Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        //Upload a file
        ShareUserSitePage.uploadFile(drone, file);
        ShareUser.logout(drone);

        //Edit uploaded file by FTP
        assertTrue(FtpUtil.editContent(shareUrl, testUser, DEFAULT_PASSWORD, fileName, remotePath), "Can't edit " + fileName);
        assertTrue(FtpUtil.getContent(shareUrl, testUser, DEFAULT_PASSWORD, fileName, remotePath).equals(testUser));

        //Login to Share, check that the file is displayed
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        DocumentLibraryPage documentLibraryPage = drone.getCurrentPage().render();
        assertTrue(documentLibraryPage.isItemVisble(fileName), fileName + " file isn't displayed");

        //Check that the changes made by FTP are displayed;
        DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName).render().render();
        assertEquals(detailsPage.getDocumentBody().contains(testUser), true, "The changes are not displayed");
    }

    /**
     * AONE-6440:Editing content. Edit offline
     */

    @Test
    public void AONE_6440() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName + ".txt");
        String editedFileName = getFileName(testName + " (Working Copy).txt");
        File file = newFile(fileName, fileName);
        String remotePath = remotePathToSites + "/" + siteName + "/" + "documentLibrary";

        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        //Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        //Upload a file and click "Edit Ofline"
        ShareUserSitePage.uploadFile(drone, file);
        DocumentLibraryPage documentLibraryPage = drone.getCurrentPage().render();
        documentLibraryPage.getFileDirectoryInfo(fileName).selectEditOfflineAndCloseFileWindow().render();
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isEdited(), "The file is blocked for editing");
        ShareUser.logout(drone);

        //Navigate to editing content by FTP
        assertTrue(FtpUtil.isObjectExists(shareUrl, testUser, DEFAULT_PASSWORD, fileName, remotePath), fileName + " file is not exist.");
        assertTrue(FtpUtil.isObjectExists(shareUrl, testUser, DEFAULT_PASSWORD, editedFileName, remotePath), editedFileName + " file is not exist.");

        //Try to edit editing content
        assertFalse(FtpUtil.editContent(shareUrl, testUser, DEFAULT_PASSWORD, fileName, remotePath), "Can't edit " + fileName);
        assertTrue(FtpUtil.editContent(shareUrl, testUser, DEFAULT_PASSWORD, editedFileName, remotePath), "Can't edit " + fileName);

        //Login to Share, check that the file is displayed
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        documentLibraryPage = drone.getCurrentPage().render();
        assertEquals(ShareUserSitePage.getFileDirectoryInfo(drone, fileName).getContentInfo(), "This document is locked by you for offline editing.");
        assertEquals(ShareUserSitePage.getContentCount(drone), 1, "Incorrect document count: " + ShareUserSitePage.getContentCount(drone));

        //Check that the changes made by FTP are displayed;
        DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName).render();
        assertEquals(detailsPage.getDocumentBody().contains(testUser), true, "The changes are not displayed");

    }

    /**
     * AONE-6445:Consumer. Available actions
     */

    @Test
    public void AONE_6445() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + "-manager");
        String testUser2 = getUserNameFreeDomain(testName + "-consumer");
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName);
        File file = newFile(fileName, fileName);
        String folderName = getFolderName(testName);
        String folderNewName = folderName + "-FTP";
        String fileNewName = fileName + "-FTP";
        File newFile = newFile(fileNewName, fileNewName);
        String remotePath = remotePathToSites + "/" + siteName + "/" + "documentLibrary";
        String remotePathToFolder = remotePathToSites + "/" + siteName + "/" + "documentLibrary" + "/" + folderName;

        // Create users
        String[] testUserInfo1 = new String[] { testUser1 };
        String[] testUserInfo2 = new String[] { testUser2 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

        //Any site is created
        ShareUser.login(drone, testUser1);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        //Any folder is created
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUserSitePage.navigateToFolder(drone, folderName);
        ShareUserSitePage.uploadFile(drone, file);

        //User1 invites the users to the site with Consumer role
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName, UserRole.CONSUMER);

        //Navigate to created folder by FTP as consumer
        assertTrue(FtpUtil.isObjectExists(shareUrl, testUser2, DEFAULT_PASSWORD, folderName, remotePath), folderName + " file is not exist.");

        //Verify the possibility to rename a folder by FTP;
        assertFalse(FtpUtil.renameFile(shareUrl, testUser2, DEFAULT_PASSWORD, remotePath, folderName, folderNewName), "Can rename " + folderName);

        //Verify the possibility to  rename a content in this folder by FTP;
        assertFalse(FtpUtil.renameFile(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileName, fileNewName), "Can rename " + fileName);

        //Verify the possibility to edit a content by FTP;
        assertFalse(FtpUtil.editContent(shareUrl, testUser2, DEFAULT_PASSWORD, fileName, remotePathToFolder), "Can edit " + fileName);

        //Verify the possibility to delete a content by FTP;
        assertFalse(FtpUtil.deleteContentItem(shareUrl, testUser2, DEFAULT_PASSWORD, fileName, remotePathToFolder), "Can delete " + fileName);

        //Verify the possibility to create new folder by FTP;
        assertFalse(FtpUtil.createSpace(shareUrl, testUser2, DEFAULT_PASSWORD, folderNewName, remotePathToFolder), "Can create " + folderNewName + " folder");

        //Verify the possibility to upload new content by FTP;
        assertFalse(FtpUtil.uploadContent(shareUrl, testUser2, DEFAULT_PASSWORD, newFile, remotePathToFolder), "Can upload " + newFile + " content");

        //Verify the possibility to delete a folder by FTP;
        assertFalse(FtpUtil.DeleteSpace(shareUrl, testUser2, DEFAULT_PASSWORD, folderName, remotePath), "Can delete " + folderName + " folder");
    }

    /**
     * AONE-6444:Contributor. Available actions
     */

    @Test
    public void AONE_6444() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + "-manager");
        String testUser2 = getUserNameFreeDomain(testName + "-contributor");
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName);
        File file = newFile(fileName, fileName);
        String folderName = getFolderName(testName);
        String folderNewName = folderName + "-FTP";
        String fileNewName = fileName + "-FTP";
        File newFile = newFile(fileNewName, fileNewName);
        String remotePath = remotePathToSites + "/" + siteName + "/" + "documentLibrary";
        String remotePathToFolder = remotePathToSites + "/" + siteName + "/" + "documentLibrary" + "/" + folderName;

        // Create users
        String[] testUserInfo1 = new String[] { testUser1 };
        String[] testUserInfo2 = new String[] { testUser2 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

        //Any site is created
        ShareUser.login(drone, testUser1);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        //Any folder is created
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUserSitePage.navigateToFolder(drone, folderName);
        ShareUserSitePage.uploadFile(drone, file);

        //User1 invites the users to the site with contributor role
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName, UserRole.CONTRIBUTOR);

        //Navigate to created folder by FTP as contributor
        assertTrue(FtpUtil.isObjectExists(shareUrl, testUser2, DEFAULT_PASSWORD, folderName, remotePath), folderName + " file is not exist.");

        //Verify the possibility to rename a folder by FTP;
        assertFalse(FtpUtil.renameFile(shareUrl, testUser2, DEFAULT_PASSWORD, remotePath, folderName, folderNewName), "Can rename " + folderName);

        //Verify the possibility to  rename a content in this folder by FTP;
        assertFalse(FtpUtil.renameFile(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileName, fileNewName), "Can rename " + fileName);

        //Verify the possibility to edit a content by FTP;
        assertFalse(FtpUtil.editContent(shareUrl, testUser2, DEFAULT_PASSWORD, fileName, remotePathToFolder), "Can edit " + fileName);

        //Verify the possibility to delete a content by FTP;
        assertFalse(FtpUtil.deleteContentItem(shareUrl, testUser2, DEFAULT_PASSWORD, fileName, remotePathToFolder), "Can delete " + fileName);

        //Verify the possibility to create new folder by FTP;
        assertTrue(FtpUtil.createSpace(shareUrl, testUser2, DEFAULT_PASSWORD, folderNewName, remotePathToFolder), "Can't create " + folderNewName + " folder");

        //Verify the possibility to upload new content by FTP;
        assertTrue(FtpUtil.uploadContent(shareUrl, testUser2, DEFAULT_PASSWORD, newFile, remotePathToFolder), "Can't upload " + newFile + " content");

        //Verify the possibility to delete a folder by FTP;
        assertFalse(FtpUtil.DeleteSpace(shareUrl, testUser2, DEFAULT_PASSWORD, folderName, remotePath), "Can delete " + folderName + " folder");
    }

    /**
     * AONE-6443:Collaborator. Available actions
     */

    @Test
    public void AONE_6443() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + "-manager");
        String testUser2 = getUserNameFreeDomain(testName + "-collaborator");
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName);
        File file = newFile(fileName, fileName);
        String folderName = getFolderName(testName);
        String folderNewName = folderName + "-FTP";
        String fileNewName = fileName + "-FTP";
        String remotePath = remotePathToSites + "/" + siteName + "/" + "documentLibrary";
        String remotePathToFolder = remotePathToSites + "/" + siteName + "/" + "documentLibrary" + "/" + folderNewName;

        // Create users
        String[] testUserInfo1 = new String[] { testUser1 };
        String[] testUserInfo2 = new String[] { testUser2 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

        //Any site is created
        ShareUser.login(drone, testUser1);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        //Any folder is created
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUserSitePage.navigateToFolder(drone, folderName);
        ShareUserSitePage.uploadFile(drone, file);

        //User1 invites the users to the site with collaborator role
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName, UserRole.COLLABORATOR);

        //Navigate to created folder by FTP as collaborator
        assertTrue(FtpUtil.isObjectExists(shareUrl, testUser2, DEFAULT_PASSWORD, folderName, remotePath), folderName + " file is not exist.");

        //Verify the possibility to rename a folder by FTP;
        assertTrue(FtpUtil.renameFile(shareUrl, testUser2, DEFAULT_PASSWORD, remotePath, folderName, folderNewName), "Can't rename " + folderName);

        //Verify the possibility to  rename a content in this folder by FTP;
        assertTrue(FtpUtil.renameFile(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileName, fileNewName), "Can't rename " + fileName);

        //Verify the possibility to edit a content by FTP;
        assertTrue(FtpUtil.editContent(shareUrl, testUser2, DEFAULT_PASSWORD, fileNewName, remotePathToFolder), "Can't edit " + fileNewName);
        assertTrue(FtpUtil.getContent(shareUrl, testUser2, DEFAULT_PASSWORD, fileNewName, remotePathToFolder).equals(testUser2));

        //Verify the possibility to delete a content by FTP;
        assertFalse(FtpUtil.deleteContentItem(shareUrl, testUser2, DEFAULT_PASSWORD, fileNewName, remotePathToFolder), "Can delete " + fileNewName);

        //Verify the possibility to create new folder by FTP;
        assertTrue(FtpUtil.createSpace(shareUrl, testUser2, DEFAULT_PASSWORD, folderName, remotePathToFolder), "Can't create " + folderName + " folder");

        //Verify the possibility to upload new content by FTP;
        assertTrue(FtpUtil.uploadContent(shareUrl, testUser2, DEFAULT_PASSWORD, file, remotePathToFolder), "Can't upload " + fileName + " content");

        //Verify the possibility to delete a folder by FTP;
        assertFalse(FtpUtil.DeleteSpace(shareUrl, testUser2, DEFAULT_PASSWORD, folderNewName, remotePath), "Can delete " + folderNewName + " folder");
    }

    /**
     * AONE-6442:Manager. Available actions
     */

    @Test
    public void AONE_6442() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + "-manager-1");
        String testUser2 = getUserNameFreeDomain(testName + "-manager-2");
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName);
        File file = newFile(fileName, fileName);
        String folderName = getFolderName(testName);
        String folderNewName = folderName + "-FTP";
        String fileNewName = fileName + "-FTP";
        String remotePath = remotePathToSites + "/" + siteName + "/" + "documentLibrary";
        String remotePathToFolder = remotePathToSites + "/" + siteName + "/" + "documentLibrary" + "/" + folderNewName;

        // Create users
        String[] testUserInfo1 = new String[] { testUser1 };
        String[] testUserInfo2 = new String[] { testUser2 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

        //Any site is created
        ShareUser.login(drone, testUser1);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        //Any folder is created
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUserSitePage.navigateToFolder(drone, folderName);
        ShareUserSitePage.uploadFile(drone, file);

        //User1 invites the users to the site with manager role
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName, UserRole.MANAGER);

        //Navigate to created folder by FTP as manager
        assertTrue(FtpUtil.isObjectExists(shareUrl, testUser2, DEFAULT_PASSWORD, folderName, remotePath), folderName + " file is not exist.");

        //Verify the possibility to rename a folder by FTP;
        assertTrue(FtpUtil.renameFile(shareUrl, testUser2, DEFAULT_PASSWORD, remotePath, folderName, folderNewName), "Can't rename " + folderName);

        //Verify the possibility to rename a content in this folder by FTP;
        assertTrue(FtpUtil.renameFile(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileName, fileNewName), "Can't rename " + fileName);

        //Verify the possibility to edit a content by FTP;
        assertTrue(FtpUtil.editContent(shareUrl, testUser2, DEFAULT_PASSWORD, fileNewName, remotePathToFolder), "Can't edit " + fileNewName);
        assertTrue(FtpUtil.getContent(shareUrl, testUser2, DEFAULT_PASSWORD, fileNewName, remotePathToFolder).equals(testUser2));

        //Verify the possibility to delete a content by FTP;
        assertTrue(FtpUtil.deleteContentItem(shareUrl, testUser2, DEFAULT_PASSWORD, fileNewName, remotePathToFolder), "Can't delete " + fileNewName);

        //Verify the possibility to create new folder by FTP;
        assertTrue(FtpUtil.createSpace(shareUrl, testUser2, DEFAULT_PASSWORD, folderName, remotePathToFolder), "Can't create " + folderName + " folder");

        //Verify the possibility to upload new content by FTP;
        assertTrue(FtpUtil.uploadContent(shareUrl, testUser2, DEFAULT_PASSWORD, file, remotePathToFolder), "Can't upload " + fileName + " content");

        //Verify the possibility to delete a folder by FTP;
        assertTrue(FtpUtil.DeleteSpace(shareUrl, testUser2, DEFAULT_PASSWORD, folderNewName, remotePath), "Can't delete " + folderNewName + " folder");
    }

    /**
     * AONE-6441:Copy non-empty folder
     */

    @Test
    public void AONE_6441() throws Exception
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
        String remotePath = remotePathToSites + "/" + siteName1 + "/" + "documentLibrary";
        String destination = remotePathToSites + "/" + siteName2 + "/" + "documentLibrary";

        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        //Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, siteName2, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName1).render();

        //Any folder is created, some  items are added to folder
        ShareUserSitePage.createFolder(drone, folderName, folderName).render();
        ShareUserSitePage.navigateToFolder(drone, folderName);
        ShareUserSitePage.uploadFile(drone, file1);
        ShareUserSitePage.uploadFile(drone, file2);

        //Copy folder from the site 1 to the site 2 (to Document Library space of site) by FTP
        assertTrue(FtpUtil.copyFolder(shareUrl, testUser, DEFAULT_PASSWORD, remotePath, folderName, destination), "Can't copy " + folderName + " folder");

        //Log in to Share and navigate to the Document Library of the site 1, check that folder is present here
        ShareUser.login(drone, testUser);
        ShareUser.openSitesDocumentLibrary(drone, siteName1).render();
        DocumentLibraryPage documentLibraryPage = drone.getCurrentPage().render();
        assertTrue(documentLibraryPage.isItemVisble(folderName), folderName + " folder isn't displayed");

        //Navigate to the site2, check that folder is copied here.
        ShareUser.openSitesDocumentLibrary(drone, siteName2).render();
        documentLibraryPage = drone.getCurrentPage().render();
        assertTrue(documentLibraryPage.isItemVisble(folderName), folderName + " folder isn't displayed");

        //Verify that folder has all items;
        ShareUserSitePage.navigateToFolder(drone, folderName);
        assertTrue(documentLibraryPage.isItemVisble(fileName1), fileName1 + " isn't displayed");
        assertTrue(documentLibraryPage.isItemVisble(fileName2), fileName2 + " isn't displayed");
    }

    /**
     * AONE-6446: FTP Change Working Directory
     */

    @Test
    public void AONE_6446() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);
        String testFolder1 = folderName + System.currentTimeMillis();
        String testFolder2 = folderName + "_2";
        String testFolder3 = folderName + "_3";
        String testFolder4 = folderName + "_4";
        String fileName1 = getFileName(testName) + "_1";
        File file1 = newFile(fileName1, fileName1);
        String server = PageUtils.getAddress(shareUrl).replaceAll("(:\\d{1,5})?", "");
        FTPClient ftpClient = new FTPClient();

        String command1 = "/Alfresco" + SLASH + testFolder1 + SLASH + testFolder2 + SLASH + testFolder3 + SLASH + testFolder4 + SLASH;
        String command2 = "/";
        String command3 = "/Alfresco" + SLASH + testFolder1 + SLASH + testFolder2 + SLASH;

        // User login
        ShareUser.login(drone, ADMIN_PASSWORD, ADMIN_USERNAME);

        // Create folders
        ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        ShareUserRepositoryPage.createFolderInRepository(drone, testFolder1, testFolder1);
        ShareUserRepositoryPage.createFolderInFolderInRepository(drone, testFolder2, testFolder2, REPO + SLASH + testFolder1);
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + testFolder1 + SLASH + testFolder2);
        ShareUserRepositoryPage.createFolderInRepository(drone, testFolder3, testFolder3);
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + testFolder1 + SLASH + testFolder2 + SLASH + testFolder3);
        ShareUserRepositoryPage.createFolderInRepository(drone, testFolder4, testFolder4);
        ShareUserRepositoryPage
            .navigateToFolderInRepository(drone, REPO + SLASH + testFolder1 + SLASH + testFolder2 + SLASH + testFolder3 + SLASH + testFolder4);
        ShareUserRepositoryPage.uploadFileInRepository(drone, file1);


        try
        {
            //Connect to FTP
            ftpClient.connect(server, Integer.parseInt(ftpPort));
            ftpClient.enterLocalPassiveMode();
            ftpClient.login(ADMIN_USERNAME, ADMIN_PASSWORD);
            assertTrue(ftpClient.getReplyString().contains("230 User logged in, proceed"));

            // Execute cd /alfresco/testFolder1/testFolder2/testFolder3/testFolder4/ command
            ftpClient.changeWorkingDirectory(command1);
            assertTrue(ftpClient.getReplyString().contains("250 Requested file action OK"));

            // Execute ls command
            String[] files = ftpClient.listNames();
            assertTrue(files[0].contains(fileName1));
            assertTrue(ftpClient.getReplyString().contains("226 Closing data connection"));

            // Execute cd / command
            ftpClient.changeWorkingDirectory(command2);
            assertTrue(ftpClient.getReplyString().contains("250 Requested file action OK"));

            // Execute cd /alfresco/testFolder1/testFolder2/ command
            ftpClient.changeWorkingDirectory(command3);
            assertTrue(ftpClient.getReplyString().contains("250 Requested file action OK"));

        }
        finally
        {
            ftpClient.logout();
            ftpClient.disconnect();
        }

    }

    /**
     * AONE-6447: FTP working with a directory
     */

    @Test
    public void AONE_6447() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);
        String testFolder1 = folderName + System.currentTimeMillis();
        String testFolder2 = folderName + "_2";
        String fileName1 = getFileName(testName) + "_1";
        File file1 = newFile(fileName1, fileName1);
        String server = PageUtils.getAddress(shareUrl).replaceAll("(:\\d{1,5})?", "");
        FTPClient ftpClient = new FTPClient();

        String command1 = "/Alfresco";
        String command2 = "/Alfresco/" + testFolder1;
        String command3 = command2 + SLASH + testFolder2;

        // User login
        ShareUser.login(drone, ADMIN_PASSWORD, ADMIN_USERNAME);

        // Create folders
        ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        ShareUserRepositoryPage.createFolderInRepository(drone, testFolder1, testFolder1);
        ShareUserRepositoryPage.createFolderInFolderInRepository(drone, testFolder2, testFolder2, REPO + SLASH + testFolder1);
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + testFolder1 + SLASH + testFolder2);
        ShareUserRepositoryPage.uploadFileInRepository(drone, file1);

        try
        {
            //Connect to FTP
            ftpClient.connect(server, Integer.parseInt(ftpPort));
            ftpClient.enterLocalPassiveMode();
            ftpClient.login(ADMIN_USERNAME, ADMIN_PASSWORD);
            assertTrue(ftpClient.getReplyString().contains("230 User logged in, proceed"));

            // Execute dir command
            assertTrue(canListDirectory(ftpClient, "Alfresco"));
            assertTrue(ftpClient.getReplyString().contains("226 Closing data connection"));

            // Execute cd /Alfresco command
            ftpClient.changeWorkingDirectory(command1);
            assertTrue(ftpClient.getReplyString().contains("250 Requested file action OK"));

            // Execute dir command
            assertTrue(canListDirectory(ftpClient, testFolder1));
            assertTrue(ftpClient.getReplyString().contains("226 Closing data connection"));

            // Execute cd /testFolder1
            ftpClient.changeWorkingDirectory(testFolder1);
            assertTrue(ftpClient.getReplyString().contains("250 Requested file action OK"));

            // Execute dir command
            assertTrue(canListDirectory(ftpClient, testFolder2));
            assertTrue(ftpClient.getReplyString().contains("226 Closing data connection"));

            // Execute dir /testFolder2
            FTPFile [] files = ftpClient.listDirectories(testFolder2);
            for (FTPFile file : files)
            {
                if (file.getName().equals(fileName1))
                {
                    logger.info (fileName1 + " is found");
                }
                else
                {
                    fail ("Can't list "+ testFolder2);
                }
            }

            assertTrue(ftpClient.getReplyString().contains("226 Closing data connection"));

            // Execute pwd
            ftpClient.printWorkingDirectory();
            assertTrue(ftpClient.getReplyString().contains("/Alfresco/" + testFolder1));

            // Execute dir /Alfresco/testFolder1
            files = ftpClient.listDirectories(command2);
            for (FTPFile file : files)
            {
                if (file.getName().equals(testFolder2))
                {
                    logger.info (testFolder2 + " is found");
                }
                else
                {
                    fail ("Can't list "+ command2);
                }
            }
            assertTrue(ftpClient.getReplyString().contains("226 Closing data connection"));

            // Execute dir /Alfresco/testFolder1/testFolder2
            files = ftpClient.listDirectories(command3);
            for (FTPFile file : files)
            {
                if (file.getName().equals(fileName1))
                {
                    logger.info (fileName1 + " is found");
                }
                else
                {
                    fail ("Can't list "+ command3);
                }
            }
            assertTrue(ftpClient.getReplyString().contains("226 Closing data connection"));
        }
        finally
        {
            ftpClient.logout();
            ftpClient.disconnect();
        }
    }

    /**
     * AONE-6452:Version history behavior for edited files
     */

    @Test
    public void AONE_6452() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + "-1-") + System.currentTimeMillis();
        String fileName = getFileName(testName + "-1");
        File file1 = newFile(fileName, fileName);
        String folderName = getFolderName(testName);
        String remotePath = remotePathToSites + "/" + siteName + "/" + "documentLibrary" + "/" + folderName;

        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // Any folder is created, some  items are added to folder
        ShareUserSitePage.createFolder(drone, folderName, folderName).render();
        ShareUserSitePage.navigateToFolder(drone, folderName);
        ShareUserSitePage.uploadFile(drone, file1);

        //  Open Details page of the uploaded document and verify version history
        DocumentLibraryPage documentLibraryPage = drone.getCurrentPage().render();
        documentLibraryPage.selectFile(fileName).render();
        DocumentDetailsPage detailsPage = drone.getCurrentPage().render();
        assertTrue(detailsPage.isVersionHistoryPanelPresent(), "Version history panel isn't present");
        assertTrue(detailsPage.getDocumentVersion().equals("1.0"));
        ShareUser.logout(drone);

        for (int i = 1; i < 4; i++)
        {
            // Navigate to the uploaded document via FTP and edit the document
            FtpUtil.editDocument(shareUrl, testUser, DEFAULT_PASSWORD, fileName, remotePath, RandomUtil.getRandomString(10));

            // Open Details page of the uploaded document and verify version history
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            ShareUser.openSitesDocumentLibrary(drone, siteName).render();
            ShareUserSitePage.navigateToFolder(drone, folderName);
            documentLibraryPage = drone.getCurrentPage().render();
            documentLibraryPage.selectFile(fileName).render();
            detailsPage = drone.getCurrentPage().render();
            assertTrue(detailsPage.getDocumentVersion().equals("1." + i));
            ShareUser.logout(drone);

        }

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        ShareUserSitePage.navigateToFolder(drone, folderName);
        documentLibraryPage = drone.getCurrentPage().render();
        documentLibraryPage.selectFile(fileName).render();
        detailsPage = drone.getCurrentPage().render();
        List<VersionDetails> olderVersions = detailsPage.getOlderVersionDetails();
        assertEquals(olderVersions.get(0).getVersionNumber(), "1.2");
        assertEquals(olderVersions.get(1).getVersionNumber(), "1.1");
        assertEquals(olderVersions.get(2).getVersionNumber(), "1.0");

    }

    /**
     * AONE-6449:Verify concurrent upload of files using FTP
     */

    @Test
    public void AONE_6449() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName + "-1-") + System.currentTimeMillis();
        String fileName1 = getFileName(testName + "-1");
        String fileName2 = getFileName(testName + "-2");
        String fileName3 = getFileName(testName + "-3");
        File file1 = newFile(fileName1, fileName1);
        File file2 = newFile(fileName2, fileName2);
        File file3 = newFile(fileName3, fileName3);
        ArrayBlockingQueue<File> fileQueue = new ArrayBlockingQueue<File>(3);
        String remotePath = remotePathToSites + "/" + siteName + "/" + "documentLibrary" + "/";

        // Create user
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Concurrent upload 3 files
        fileQueue.put(file1);
        fileQueue.put(file2);
        fileQueue.put(file3);
        FtpUtil.concurrentUpload(shareUrl, testUser, DEFAULT_PASSWORD, fileQueue, remotePath);

        // Login to Share, check that files are displayed
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        DocumentLibraryPage documentLibraryPage = drone.getCurrentPage().render();
        assertTrue(documentLibraryPage.isFileVisible(fileName1));
        assertTrue(documentLibraryPage.isFileVisible(fileName2));
        assertTrue(documentLibraryPage.isFileVisible(fileName3));

    }

    /**
     * AONE-6448: Connect to FTP via Firefox
     */

    @Test
    public void AONE_6448() throws Exception
    {
        String ftpUrl = "ftp://%s:%s@%s";
        String serverIP = PageUtils.getAddress(shareUrl).replaceAll("(:\\d{1,5})?", "") + ":" + ftpPort;
        ftpUrl = String.format(ftpUrl, ADMIN_USERNAME, ADMIN_PASSWORD, serverIP);

        // Navigate to ftp://login:pass@server_ip
        drone.navigateTo(ftpUrl);
        assertTrue(drone.findAndWait(By.cssSelector(".up")).getAttribute("href").contains(ftpUrl));
        assertTrue(getDrone().findAndWait(By.cssSelector(".dir")).getText().contains("Alfresco"));
    }


  private boolean canListDirectory (FTPClient ftpClient, String remoteObject) throws IOException
  {

      FTPFile [] files = ftpClient.listDirectories();
      for (FTPFile file : files)
      {
          if (file.getName().equals(remoteObject))
      {
          return true;
      }
      }
      return false;
  }
}
