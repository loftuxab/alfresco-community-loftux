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

import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.VersionDetails;
import org.alfresco.po.share.util.PageUtils;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import static org.testng.Assert.*;

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
        DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName).render();
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
        DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName);
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

        String[] arguments = new String[8];

        for (int i = 0; i < 8; i++)
            arguments[i] = "";

        String command1 = "cd /Alfresco" + SLASH + testFolder1 + SLASH + testFolder2 + SLASH + testFolder3 + SLASH + testFolder4 + SLASH;
        String command2 = "ls";
        String command3 = "cd /";
        String command4 = "cd /Alfresco" + SLASH + testFolder1 + SLASH + testFolder2 + SLASH;

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

        // Execute cd /alfresco/testFolder1/testFolder2/testFolder3/testFolder4/ command
        arguments[0] = command1;
        assertTrue(execFTP(arguments).contains("230 User logged in, proceed"));
        assertTrue(execFTP(arguments).contains(command1 + "250 Requested file action OK"));

        // Execute ls command
        arguments[1] = command2;
        assertTrue(execFTP(arguments).contains(fileName1 + "226 Closing data connection"));

        // Execute cd / command
        arguments[2] = command3;
        assertTrue(execFTP(arguments).contains(command3 + "250 Requested file action OK"));

        // Execute cd /alfresco/testFolder1/testFolder2/ command
        arguments[3] = command4;
        assertTrue(execFTP(arguments).contains(command4 + "250 Requested file action OK"));

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

        String[] arguments = new String[8];

        for (int i = 0; i < 8; i++)
            arguments[i] = "";

        String command1 = "dir ";
        String command2 = "cd /Alfresco";
        String command3 = "cd " + testFolder1;
        String command4 = "pwd";
        String command5 = "dir /Alfresco" + SLASH + testFolder1;
        String command6 = command5 + SLASH + testFolder2;

        // User login
        ShareUser.login(drone, ADMIN_PASSWORD, ADMIN_USERNAME);

        // Create folders
        ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        ShareUserRepositoryPage.createFolderInRepository(drone, testFolder1, testFolder1);
        ShareUserRepositoryPage.createFolderInFolderInRepository(drone, testFolder2, testFolder2, REPO + SLASH + testFolder1);
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + testFolder1 + SLASH + testFolder2);
        ShareUserRepositoryPage.uploadFileInRepository(drone, file1);

        // Execute dir command
        arguments[0] = command1;
        assertTrue(execFTP(arguments).contains("230 User logged in, proceed"));
        assertTrue(execFTP(arguments).contains("Alfresco" + "226 Closing data connection"));

        // Execute cd /Alfresco command
        arguments[1] = command2;
        assertTrue(execFTP(arguments).contains(command2 + "250 Requested file action OK"));

        // Execute dir command
        arguments[2] = command1;
        assertTrue(execFTP(arguments).contains(testFolder1 + "226 Closing data connection"));

        // Execute cd /testFolder1
        arguments[3] = command3;
        assertTrue(execFTP(arguments).contains(command3 + "250 Requested file action OK"));

        // Execute dir command
        arguments[4] = command1;
        assertTrue(execFTP(arguments).contains(testFolder2 + "226 Closing data connection"));

        // Execute cd /testFolder2
        arguments[5] = command1 + testFolder2;
        assertTrue(execFTP(arguments).contains(fileName1 + "226 Closing data connection"));

        // Execute pwd
        arguments[6] = command4;
        assertTrue(execFTP(arguments).contains(command4 + "257 \"" + "/Alfresco/" + testFolder1));

        // Execute dir /Alfresco/testFolder1
        arguments[4] = "";
        arguments[7] = command5;
        assertTrue(execFTP(arguments).contains(testFolder2 + "226 Closing data connection"));

        // Execute dir /Alfresco/testFolder1/testFolder2
        arguments[5] = "";
        arguments[7] = command6;
        assertTrue(execFTP(arguments).contains(fileName1 + "226 Closing data connection"));

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
        documentLibraryPage.selectFile(fileName);
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
            documentLibraryPage.selectFile(fileName);
            detailsPage = drone.getCurrentPage().render();
            assertTrue(detailsPage.getDocumentVersion().equals("1." + i));
            ShareUser.logout(drone);

        }

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        ShareUserSitePage.navigateToFolder(drone, folderName);
        documentLibraryPage = drone.getCurrentPage().render();
        documentLibraryPage.selectFile(fileName);
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
        assertTrue(drone.findAndWait(By.cssSelector(".dir")).getText().contains("Alfresco"));

        //  Navigate to ftp://server_ip
        ftpUrl = "ftp://%s";
        ftpUrl = String.format(ftpUrl, serverIP);
        drone.executeJavaScript("window.location.replace(\"" + ftpUrl + "\"" + ")");
        Thread.sleep(3000);

        //  Fill Login and Pass fields and click 'Ok' button;
        for (String winHandle : drone.getWindowHandles())
        {
            drone.switchToWindow(winHandle);

            try
            {
                Robot robot = new Robot();
                type(ADMIN_USERNAME);
                robot.keyPress(KeyEvent.VK_TAB);
                type(ADMIN_PASSWORD);
                robot.keyPress(KeyEvent.VK_ENTER);
                break;
            }
            catch (AWTException ex)
            {
                logger.error(ex);
            }
        }
        assertTrue(drone.findAndWait(By.cssSelector(".dir")).getText().contains("Alfresco"));

        // Navigate to ftp://login@server_ip
        ftpUrl = "ftp://%s@%s";
        ftpUrl = String.format(ftpUrl, ADMIN_USERNAME, serverIP);
        drone.executeJavaScript("window.location.replace(\"" + ftpUrl + "\"" + ")");
        Thread.sleep(3000);

        // Fill Password field and click 'Ok' button;
        for (String winHandle : drone.getWindowHandles())
        {
            drone.switchToWindow(winHandle);

            try
            {
                Robot robot = new Robot();
                type(ADMIN_PASSWORD);
                robot.keyPress(KeyEvent.VK_ENTER);
                break;
            }
            catch (AWTException ex)
            {
                logger.error(ex);
            }
        }
        assertTrue(drone.findAndWait(By.cssSelector(".dir")).getText().contains("Alfresco"));

    }

    private void writeToClipboard(String s)
    {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable transferable = new StringSelection(s);
        clipboard.setContents(transferable, null);
    }

    private void type(String text)
    {
        writeToClipboard(text);
        pasteClipboard();
    }

    private void pasteClipboard()
    {

        try
        {

            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_CONTROL);

        }
        catch (AWTException ex)
        {
            logger.error(ex);
        }
    }

    private String execFTP(String[] arguments)
    {

        String host = PageUtils.getAddress(shareUrl).replaceAll("(:\\d{1,5})?", "");
        StringBuilder content = new StringBuilder();
        BufferedReader reader;
        String inputLine;
        String errorLine;
        Process process;

        try
        {
            File tmpDir = new File (DATA_FOLDER);
            File file = File.createTempFile("ftp", ".tmp", tmpDir );
            file.deleteOnExit();
            Writer writer = new FileWriter(file);
            if (System.getProperty("os.name").contains("Windows"))
            {
                writer.write(
                    "verbose\n" + "open " + host + " " + ftpPort + "\n" + ADMIN_USERNAME + "\n" + ADMIN_PASSWORD + "\n" + arguments[0] + "\n" + arguments[1]
                        + "\n"
                        + arguments[2] + "\n" + arguments[3] + "\n" + arguments[4] + "\n" + arguments[5] + "\n" + arguments[6] + "\n" + arguments[7] + "\n"
                        + "\nquit"
                );
                process = Runtime.getRuntime().exec("ftp -s:" + file);
            }
            else
            {
                writer.write(
                    "open " + host + " " + ftpPort + "\n" + "user " + ADMIN_USERNAME + ADMIN_PASSWORD + arguments[0] + "\n" + arguments[1] + "\n"
                        + arguments[2] + "\n" + arguments[3] + "\n" + arguments[4] + "\n" + arguments[5] + "\n" + arguments[6] + "\n" + arguments[7] + "\n"
                        + "\nquit"
                );
                process = Runtime.getRuntime().exec("ftp -n <" + file);
            }
            writer.close();

            InputStream inputStream = process.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));

            BufferedReader stdError = new BufferedReader(new
                InputStreamReader(process.getErrorStream()));

            while ((inputLine = reader.readLine()) != null)
            {
                content.append(inputLine);
            }
            reader.close();

            // read any errors
            while ((errorLine = stdError.readLine()) != null)
            {
                logger.error(errorLine);
            }
            stdError.close();
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex.getMessage());
        }

        return content.toString();
    }

}
