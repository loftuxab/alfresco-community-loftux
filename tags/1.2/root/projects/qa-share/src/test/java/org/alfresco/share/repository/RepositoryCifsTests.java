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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;

import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.contentrule.FolderRulesPage;
import org.alfresco.po.share.site.contentrule.FolderRulesPageWithRules;
import org.alfresco.po.share.site.contentrule.createrules.CreateRulePage;
import org.alfresco.po.share.site.contentrule.createrules.selectors.AbstractIfSelector;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.ActionSelectorEnterpImpl;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.WhenSelectorImpl;
import org.alfresco.po.share.site.document.DocumentAspect;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.PaginationForm;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.CifsUtil;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Sergey Kardash
 */
@Listeners(FailedTestListener.class)
@Test(groups = "EnterpriseOnly", timeOut = 400000)
public class RepositoryCifsTests extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(RepositoryCifsTests.class);

    @Override
    @BeforeClass
    public void setup() throws Exception
    {
        super.setup();
        logger.info("Starting Tests: " + testName);
    }

    /**
     * Test: AONE-6146:Verify Creating folder option
     * <ul>
     * <li>CIFS is adjusted and opened via SMB client</li>
     * <li>Any site is created via Share client</li>
     * <li>Create any folder in the Document Library space in the created site via CIFS</li>
     * <li>Login to Share, check that the folder is displayed</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6146() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName) + System.currentTimeMillis();
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String folderName = getFolderName(testName);
        String remotePath = "Alfresco" + "/" + "Sites" + "/" + siteName + "/" + "documentLibrary" + "/";
        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        // Create any folder in the Document Library space in the created site via CIFS
        assertTrue(CifsUtil.addSpace(shareUrl, testUser, DEFAULT_PASSWORD, remotePath, folderName), "Can't create " + folderName + " folder via CIFS");

        // Login to Share, check that the folder is displayed
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        assertTrue(documentLibraryPage.isItemVisble(folderName), folderName + " folder isn't displayed");

    }

    /**
     * Test: AONE-6147:Verify Creating content option
     * <ul>
     * <li>Any user is created, e.g. user1</li>
     * <li>Any site is created via Share client</li>
     * <li>Create any file in the Document Library space in the created site via CIFS</li>
     * <li>Log in to Share client and navigate to the Document Library</li>
     * <li>File Word 2003. Save file over Cifs mapped drive logged</li>
     * <li>File Word 2007. Save file over Cifs mapped drive logged</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6147() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName) + System.currentTimeMillis();
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";
        String FILE_DOCX = "docx.docx";
        String FILE_DOC = "doc.doc";
        File TEST_FILE_DOCX = new File(DATA_FOLDER + SLASH + FILE_DOCX);
        File TEST_FILE_DOC = new File(DATA_FOLDER + SLASH + FILE_DOC);

        String remotePath = "Alfresco" + "/" + "Sites" + "/" + siteName + "/" + "documentLibrary" + "/";
        String remotePathUserHome = "Alfresco" + "/" + "User Homes" + "/";

        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        // Create any file in the Document Library space in the created site via CIFS
        assertTrue(CifsUtil.addContent(shareUrl, testUser, DEFAULT_PASSWORD, remotePath, fileName, fileName), "Can't create " + fileName + " content");

        // File Word 2003. Save file over Cifs mapped drive logged
        assertTrue(CifsUtil.uploadContent(shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD, remotePathUserHome, TEST_FILE_DOCX), "Can't upload " + FILE_DOCX
                + " content (Word 2003)");

        // File Word 2007. Save file over Cifs mapped drive logged
        assertTrue(CifsUtil.uploadContent(shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD, remotePathUserHome, TEST_FILE_DOC), "Can't upload " + FILE_DOCX
                + " content (Word 2007)");

        // Login to Share, check that the file is displayed
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        assertTrue(documentLibraryPage.isItemVisble(fileName), fileName + " file isn't displayed");

        assertTrue(CifsUtil.deleteContent(shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD, remotePathUserHome, FILE_DOCX), "Content (Word 2003) isn't deleted");
        assertTrue(CifsUtil.deleteContent(shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD, remotePathUserHome, FILE_DOC), "Content (Word 2007) isn't deleted");

    }

    /**
     * Test: AONE-6148:Verify Renaming folder option
     * <ul>
     * <li>Any user is created, e.g. user1</li>
     * <li>Any site is created via Share client</li>
     * <li>Any folder is created in the Document Library in the created site via Share client</li>
     * <li>Rename created folder via CIFS</li>
     * <li>Log in to Share client and navigate to the Document Library</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6148() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName) + System.currentTimeMillis();
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String folderName = getFolderName(testName);
        String folderNewName = folderName + "CIFS";
        String remotePath = "Alfresco" + "/" + "Sites" + "/" + siteName + "/" + "documentLibrary" + "/";
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

        // Rename created folder via CIFS
        assertTrue(CifsUtil.renameItem(shareUrl, testUser, DEFAULT_PASSWORD, remotePath, folderName, folderNewName), "Can't rename " + folderName + " folder");

        // The folder is displayed with new name in Share client
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        assertTrue(documentLibraryPage.isItemVisble(folderNewName), folderNewName + " folder with new name isn't displayed");
        assertFalse(documentLibraryPage.isItemVisble(folderName), folderName + " folder with old name is displayed");
    }

    /**
     * Test: AONE-6149:Verify Renaming content option
     * <ul>
     * <li>Any user is created, e.g. user1</li>
     * <li>Any site is created via Share client</li>
     * <li>Any content is uploaded in the Document Library in the created site via Share client</li>
     * <li>Rename uploaded content via CIFS</li>
     * <li>Log in to Share client and navigate to the Document Library</li>
     * <li>The content is displayed with renamed name in Share client</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6149() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName);
        String fileNewName = fileName + "-CIFS";
        String remotePath = "Alfresco" + "/" + "Sites" + "/" + siteName + "/" + "documentLibrary" + "/";
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

        // Rename uploaded content by CIFS;
        assertTrue(CifsUtil.renameItem(shareUrl, testUser, DEFAULT_PASSWORD, remotePath, fileName, fileNewName), "Can't rename " + fileName + " file");

        // Login to Share, check that content is displayed with renamed name
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // TThe content is displayed with new name in Share client
        assertTrue(documentLibraryPage.isItemVisble(fileNewName), "The content isn't renamed");
        assertFalse(documentLibraryPage.isItemVisble(fileName), "The content isn't renamed");
    }

    /**
     * Test: AONE-6150:Verify Deleting folder option
     * <ul>
     * <li>Any user is created, e.g. user1</li>
     * <li>Any site is created via Share client</li>
     * <li>Any folder is created in the Document Library in the created site via Share client</li>
     * <li>Delete created folder via CIFS</li>
     * <li>Log in to Share client and navigate to the Document Library</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6150() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String folderName = getFolderName(testName);
        String remotePath = "Alfresco" + "/" + "Sites" + "/" + siteName + "/" + "documentLibrary" + "/";

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

        // Delete created folder via CIFS
        assertTrue(CifsUtil.deleteContent(shareUrl, testUser, DEFAULT_PASSWORD, remotePath, folderName + "/"), "Can't delete " + folderName);

        // Login to Share, check that the folder isn't displayed in Share client;
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        assertFalse(documentLibraryPage.isItemVisble(folderName), folderName + " folder isn't deleted");

    }

    /**
     * Test: AONE-6151:Verify Deleting content option
     * <ul>
     * <li>Any user is created, e.g. user1</li>
     * <li>Any site is created via Share client</li>
     * <li>Any content is uploaded in the Document Library in the created site via Share client</li>
     * <li>Delete uploaded content via CIFS</li>
     * <li>Log in to Share client and navigate to the Document Library</li>
     * <li>The content isn't displayed in Share client</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6151() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName);
        String remotePath = "Alfresco" + "/" + "Sites" + "/" + siteName + "/" + "documentLibrary" + "/";
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

        // Delete uploaded content via CIFS
        assertTrue(CifsUtil.deleteContent(shareUrl, testUser, DEFAULT_PASSWORD, remotePath, fileName), "Can't delete " + fileName);

        // Login to Share, check that content is displayed with renamed name
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // The content isn't displayed in Share client
        assertFalse(documentLibraryPage.isItemVisble(fileName), "The content isn't deleted");
    }

    /**
     * Test: AONE-6152:Verify Editing content option
     * <ul>
     * <li>Any user is created, e.g. user1</li>
     * <li>Any site is created via Share client</li>
     * <li>Any content is uploaded in the Document Library in the created site via Share client</li>
     * <li>Edit uploaded content via CIFS</li>
     * <li>Log in to Share client and navigate to the Document Library</li>
     * <li>Press the name of content item</li>
     * <li>Changes made via CIFS are displayed correctly</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6152() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName + ".txt");
        String remotePath = "Alfresco" + "/" + "Sites" + "/" + siteName + "/" + "documentLibrary" + "/";
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

        // Edit uploaded content via CIFS
        assertTrue(CifsUtil.editContent(shareUrl, testUser, DEFAULT_PASSWORD, remotePath, fileName, testUser), "Content isn't edited");

        // Login to Share, check that the file is displayed
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();
        // navigate to the Document Library
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        assertTrue(documentLibraryPage.isItemVisble(fileName), fileName + " file isn't displayed");

        // Changes made via CIFS are displayed correctly
        DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName).render();
        assertEquals(detailsPage.getDocumentBody().contains(testUser), true, "The changes are not displayed");
    }

    /**
     * Test: AONE-6153:Verify Edit offline option
     * <ul>
     * <li>Any user is created, e.g. user1</li>
     * <li>Any site is created via Share client</li>
     * <li>Any content is uploaded in the Document Library in the created site via Share client</li>
     * <li>Uploaded content is editing offline via Share client</li>
     * <li>Try to edit editing content via CIFS</li>
     * <li>Try to edit Working Copy of editing content via CIFS</li>
     * <li>Log in to Share client and navigate to the Document Library</li>
     * <li>The changes made via CIFS are displayed</li>
     * <li>Press 'View Original Document'</li>
     * <li>Content item page is opened. No changes are saved</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6153() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName + ".txt");
        String editedFileName = getFileName(testName + " (Working Copy).txt");
        File file = newFile(fileName, fileName);
        file.deleteOnExit();
        String remotePath = "Alfresco" + "/" + "Sites" + "/" + siteName + "/" + "documentLibrary" + "/";

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

        // Try to edit editing content
        assertFalse(CifsUtil.editContent(shareUrl, testUser, DEFAULT_PASSWORD, remotePath, fileName, testUser), "Can edit file " + fileName
                + ". (Blocked content))");
        // Try to edit Working Copy of editing content via CIFS
        assertTrue(CifsUtil.editContent(shareUrl, testUser, DEFAULT_PASSWORD, remotePath, editedFileName, testUser), "Can't edit file " + editedFileName
                + ". (Working copy))");

        // Login to Share, check that the file is displayed
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        assertEquals(ShareUserSitePage.getFileDirectoryInfo(drone, fileName).getContentInfo(), "This document is locked by you for offline editing.");
        assertEquals(ShareUserSitePage.getContentCount(drone), 1, "Incorrect document count: " + ShareUserSitePage.getContentCount(drone));

        // The changes, made via CIFS for Working Copy, are displayed
        DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName);
        assertEquals(detailsPage.getDocumentBody().contains(testUser), true, "The changes are not displayed");

        // Press 'View Original Document'
        detailsPage.selectViewOriginalDocument().render(maxWaitTime);
        assertTrue(detailsPage.isLockedByYou(), "Content item page isn't opened");
        assertTrue(detailsPage.isViewWorkingCopyDisplayed(), "Content item page isn't opened");
        assertFalse(detailsPage.getDocumentBody().contains(testUser), "The changes are not displayed");
        // Content item page is opened. No changes are saved
        assertTrue(detailsPage.getDocumentBody().contains(fileName), "Changes are saved");
    }

    /**
     * Test: AONE-6154:Verify Copying of non-empty folder option
     * <ul>
     * <li>Any user is created, e.g. user1</li>
     * <li>Any site is created via Share client</li>
     * <li>Any folder is created in the Document Library in the created site via Share client</li>
     * <li>Some items are added to folder via Share client</li>
     * <li>Copy folder from the site, where it is located, to the other site (to Document Library space of site) via CIFS</li>
     * <li>Try to edit Working Copy of editing content via CIFS</li>
     * <li>Log in to Share client and navigate to the Document Library</li>
     * <li>The changes made via CIFS are displayed</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6154() throws Exception
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

        String remotePath = "Alfresco" + "/" + "Sites" + "/" + siteName1 + "/" + "documentLibrary" + "/";
        String destination = "Alfresco" + "/" + "Sites" + "/" + siteName2 + "/" + "documentLibrary" + "/";

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

        // Copy folder from the site 1 to the site 2 (to Document Library space of site) by CIFS
        assertTrue(CifsUtil.copyFolder(shareUrl, testUser, DEFAULT_PASSWORD, remotePath + folderName + "/", destination + folderName + "/"), "Can't copy "
                + folderName + " folder (via CIFS)");

        // Log in to Share and navigate to the Document Library of the site 1, check that folder is present here
        ShareUser.login(drone, testUser);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName1).render();
        assertTrue(documentLibraryPage.isItemVisble(folderName), folderName + " folder isn't displayed (first site)");

        // Navigate to the site2, check that folder is copied here
        ShareUser.openSitesDocumentLibrary(drone, siteName2).render();
        assertTrue(documentLibraryPage.isItemVisble(folderName), folderName + " folder isn't displayed (second site)");

        // Verify that folder has all items
        ShareUserSitePage.navigateToFolder(drone, folderName);
        assertTrue(documentLibraryPage.isItemVisble(fileName1), fileName1 + " isn't displayed (first file)");
        assertTrue(documentLibraryPage.isItemVisble(fileName2), fileName2 + " isn't displayed (second file)");
    }

    /**
     * Test: AONE-6155:Verify all available to Manager actions
     * <ul>
     * <li>Any site is created via Share client</li>
     * <li>At least one folder is created by admin user in Document Library space via Share client</li>
     * <li>Any content is added by admin user in Document Library space via Share client</li>
     * <li>user with Coordinator role for the created folder</li>
     * <li>Navigate to created folder via CIFS</li>
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
    public void AONE_6155() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + "-manager-1");
        String testUser2 = getUserNameFreeDomain(testName + "-manager-2");
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName);
        File file = newFile(fileName, fileName);
        file.deleteOnExit();
        String folderName = getFolderName(testName);
        String folderNewName = folderName + "-CIFS";
        String fileNewName = fileName + "-CIFS";

        String remotePath = "Alfresco" + "/" + "Sites" + "/" + siteName + "/" + "documentLibrary" + "/";
        String remotePathToFolder = remotePath + folderNewName + "/";

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

        // Navigate to created folder by CIFS as manager
        assertTrue(CifsUtil.checkItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePath, folderName), folderName + " file is not exist.");

        // Verify the possibility to rename a folder by CIFS;
        assertTrue(CifsUtil.renameItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePath, folderName, folderNewName), "Can't rename " + folderName);
        assertTrue(CifsUtil.checkItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePath, folderNewName), "Can't rename " + folderName);

        // Verify the possibility to rename a content in this folder by CIFS;
        assertTrue(CifsUtil.renameItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileName, fileNewName), "Can't rename " + fileName);
        assertTrue(CifsUtil.checkItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileNewName), "Can't rename " + fileName);

        // Verify the possibility to edit a content by CIFS;
        assertTrue(CifsUtil.editContent(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileNewName, testUser2), "Can't edit " + fileNewName);
        assertTrue(CifsUtil.checkContent(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileNewName, testUser2), "Can't edit " + fileNewName);

        // Verify the possibility to delete a content by CIFS;
        assertTrue(CifsUtil.deleteContent(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileNewName), "Can't delete " + fileNewName);
        assertFalse(CifsUtil.checkItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileNewName), "Can't delete " + fileNewName);

        // Verify the possibility to create new folder by CIFS;
        assertTrue(CifsUtil.addSpace(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, folderName), "Can't create " + folderName + " folder");
        assertTrue(CifsUtil.checkItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, folderName), "Can't create " + folderName);

        // Verify the possibility to upload new content by CIFS;
        assertTrue(CifsUtil.uploadContent(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, file), "Can't upload " + fileName + " content");
        assertTrue(CifsUtil.checkItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileName), "Can't upload " + fileName);

        // Verify the possibility to delete a folder by CIFS;
        assertTrue(CifsUtil.deleteContent(shareUrl, testUser2, DEFAULT_PASSWORD, remotePath, folderNewName + "/"), "Can't delete " + folderNewName + " folder");
        assertFalse(CifsUtil.checkItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePath, folderNewName), "Can't delete " + folderNewName);
    }

    /**
     * Test: AONE-6156:Verify all available to Collaborator actions
     * <ul>
     * <li>Any site is created via Share client</li>
     * <li>At least one folder is created by admin user in Document Library space via Share client</li>
     * <li>Any content is added by admin user in Document Library space via Share client</li>
     * <li>user with Collaborator role for the created folder</li>
     * <li>Navigate to created folder via CIFS</li>
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
    public void AONE_6156() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + "-manager");
        String testUser2 = getUserNameFreeDomain(testName + "-collaborator");
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName);
        File file = newFile(fileName, fileName);
        file.deleteOnExit();
        String folderName = getFolderName(testName);
        String folderNewName = folderName + "-CIFS";
        String fileNewName = fileName + "-CIFS";

        String remotePath = "Alfresco" + "/" + "Sites" + "/" + siteName + "/" + "documentLibrary" + "/";
        String remotePathToFolder = remotePath + folderNewName + "/";

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

        // Navigate to created folder by CIFS as collaborator
        assertTrue(CifsUtil.checkItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePath, folderName), folderName + " file is not exist.");

        // Verify the possibility to rename a folder by CIFS;
        assertTrue(CifsUtil.renameItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePath, folderName, folderNewName), "Can't rename " + folderName);
        assertTrue(CifsUtil.checkItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePath, folderNewName), "Can't rename " + folderName);

        // Verify the possibility to rename a content in this folder by CIFS;
        assertTrue(CifsUtil.renameItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileName, fileNewName), "Can't rename " + fileName);
        assertTrue(CifsUtil.checkItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileNewName), "Can't rename " + fileName);

        // Verify the possibility to edit a content by CIFS;
        assertTrue(CifsUtil.editContent(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileNewName, testUser2), "Can't edit " + fileNewName);
        assertTrue(CifsUtil.checkContent(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileNewName, testUser2), "Can't edit " + fileNewName);

        // Verify the possibility to delete a content by CIFS;
        assertFalse(CifsUtil.deleteContent(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileNewName), "Can delete " + fileNewName);
        assertTrue(CifsUtil.checkItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileNewName), "Can delete " + fileNewName);

        // Verify the possibility to create new folder by CIFS
        assertTrue(CifsUtil.addSpace(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, folderName), "Can't create " + folderName + " folder");
        assertTrue(CifsUtil.checkItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, folderName), "Can't create " + folderName + " folder");

        // Verify the possibility to upload new content by CIFS;
        assertTrue(CifsUtil.uploadContent(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, file), "Can't upload " + fileName + " content");
        assertTrue(CifsUtil.checkItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileName), "Can't upload " + fileName + " content");

        // Verify the possibility to delete a folder by CIFS;
        assertFalse(CifsUtil.deleteContent(shareUrl, testUser2, DEFAULT_PASSWORD, remotePath, folderNewName + "/"), "Can delete " + folderNewName + " folder");
        assertTrue(CifsUtil.checkItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePath, folderNewName), "Can delete " + folderNewName + " folder");

        assertTrue(CifsUtil.deleteContent(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, folderName + "/"), "Can't delete " + folderName
                + " folder");
        assertFalse(CifsUtil.checkItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, folderName), "Can't delete " + folderName + " folder");

        assertTrue(CifsUtil.deleteContent(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileName), "Can't delete " + fileName + " file");
        assertFalse(CifsUtil.checkItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileName), "Can't delete " + fileName + " file");

    }

    /**
     * Test: AONE-6157:Verify all available to Contributor actions
     * <ul>
     * <li>Any site is created via Share client</li>
     * <li>At least one folder is created by admin user in Document Library space via Share client</li>
     * <li>Any content is added by admin user in Document Library space via Share client</li>
     * <li>user with Contributor role for the created folder</li>
     * <li>Navigate to created folder via CIFS</li>
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
    public void AONE_6157() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + "-manager");
        String testUser2 = getUserNameFreeDomain(testName + "-contributor");
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName);
        File file = newFile(fileName, fileName);
        file.deleteOnExit();
        String folderName = getFolderName(testName);
        String folderNewName = folderName + "-CIFS";
        String fileNewName = fileName + "-CIFS";
        File newFile = newFile(fileNewName, fileNewName);
        newFile.deleteOnExit();

        String remotePath = "Alfresco" + "/" + "Sites" + "/" + siteName + "/" + "documentLibrary" + "/";
        String remotePathToFolder = remotePath + folderName + "/";

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

        // Navigate to created folder by CIFS as contributor
        assertTrue(CifsUtil.checkItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePath, folderName), folderName + " file is not exist.");

        // Verify the possibility to rename a folder by CIFS;
        assertFalse(CifsUtil.renameItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePath, folderName, folderNewName), "Can rename " + folderName);
        assertTrue(CifsUtil.checkItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePath, folderName), "Can rename " + folderName + " folder");

        // Verify the possibility to rename a content in this folder by CIFS;
        assertFalse(CifsUtil.renameItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileName, fileNewName), "Can rename " + fileName);
        assertTrue(CifsUtil.checkItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileName), "Can rename " + fileName);

        // Verify the possibility to edit a content by CIFS;
        assertFalse(CifsUtil.editContent(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileName, testUser2), "Can edit " + fileName);
        assertTrue(CifsUtil.checkContent(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileName, fileName), "Can edit " + fileName);

        // Verify the possibility to delete a content by CIFS;
        assertFalse(CifsUtil.deleteContent(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileName), "Can delete " + fileName);
        assertTrue(CifsUtil.checkItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileName), "Can delete " + fileName);

        // Verify the possibility to create new folder by CIFS;
        assertTrue(CifsUtil.addSpace(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, folderNewName), "Can't create " + folderNewName + " folder");
        assertTrue(CifsUtil.checkItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, folderNewName), "Can't create " + folderNewName + " folder");

        // Verify the possibility to upload new content by CIFS;
        assertTrue(CifsUtil.uploadContent(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, newFile), "Can't upload " + fileNewName + " content");
        assertTrue(CifsUtil.checkItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileNewName), "Can't upload " + fileNewName + " content");

        // Verify the possibility to delete a folder by CIFS;
        assertFalse(CifsUtil.deleteContent(shareUrl, testUser2, DEFAULT_PASSWORD, remotePath, folderName + "/"), "Can delete " + folderName + " folder");
        assertTrue(CifsUtil.checkItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePath, folderName), "Can delete " + folderName + " folder");

        assertTrue(CifsUtil.deleteContent(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, folderNewName + "/"), "Can't delete " + folderNewName
                + " folder");
        assertFalse(CifsUtil.checkItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, folderNewName), "Can delete " + folderNewName + " folder");

        assertTrue(CifsUtil.deleteContent(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileNewName), "Can't delete " + fileNewName + " file");
        assertFalse(CifsUtil.checkItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileNewName), "Can delete " + fileNewName + " file");
    }

    /**
     * Test: AONE-6158:Verify all available to Consumer actions
     * <ul>
     * <li>Any site is created via Share client</li>
     * <li>At least one folder is created by admin user in Document Library space via Share client</li>
     * <li>Any content is added by admin user in Document Library space via Share client</li>
     * <li>user with Consumer role for the created folder</li>
     * <li>Navigate to created folder via CIFS</li>
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
    public void AONE_6158() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + "-manager");
        String testUser2 = getUserNameFreeDomain(testName + "-consumer");
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName);
        File file = newFile(fileName, fileName);
        file.deleteOnExit();
        String folderName = getFolderName(testName);
        String folderNewName = folderName + "-CIFS";
        String fileNewName = fileName + "-CIFS";
        File newFile = newFile(fileNewName, fileNewName);
        newFile.deleteOnExit();

        String remotePath = "Alfresco" + "/" + "Sites" + "/" + siteName + "/" + "documentLibrary" + "/";
        String remotePathToFolder = remotePath + folderName + "/";

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

        // Navigate to created folder by CIFS as consumer
        assertTrue(CifsUtil.checkItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePath, folderName), folderName + " file is not exist.");

        // Verify the possibility to rename a folder by CIFS;
        assertFalse(CifsUtil.renameItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePath, folderName, folderNewName), "Can rename " + folderName);
        assertTrue(CifsUtil.checkItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePath, folderName), "Can rename " + folderName);

        // Verify the possibility to rename a content in this folder by CIFS;
        assertFalse(CifsUtil.renameItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileName, fileNewName), "Can rename " + fileName);
        assertTrue(CifsUtil.checkItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileName), "Can rename " + fileName);

        // Verify the possibility to edit a content by CIFS;
        assertFalse(CifsUtil.editContent(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileName, testUser2), "Can edit " + fileName);
        assertTrue(CifsUtil.checkContent(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileName, fileName), "Can edit " + fileName);

        // Verify the possibility to delete a content by CIFS;
        assertFalse(CifsUtil.deleteContent(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileName), "Can delete " + fileName);
        assertTrue(CifsUtil.checkItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileName), "Can delete " + fileName);

        // Verify the possibility to create new folder by CIFS;
        assertFalse(CifsUtil.addSpace(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, folderNewName), "Can create " + folderNewName + " folder");
        assertFalse(CifsUtil.checkItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, folderNewName), "Can create " + folderNewName + " folder");

        // Verify the possibility to upload new content by CIFS;
        assertFalse(CifsUtil.uploadContent(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, newFile), "Can upload " + fileNewName + " content");
        assertFalse(CifsUtil.checkItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileNewName), "Can upload " + fileNewName + " content");

        // Verify the possibility to delete a folder by CIFS;
        assertFalse(CifsUtil.deleteContent(shareUrl, testUser2, DEFAULT_PASSWORD, remotePath, folderName + "/"), "Can delete " + folderName + " folder");
        assertTrue(CifsUtil.checkItem(shareUrl, testUser2, DEFAULT_PASSWORD, remotePath, folderName), "Can delete " + folderName + " folder");
    }

    /**
     * Test: AONE-6160:Version history behavior for edited files
     * <ul>
     * <li>Any site is created</li>
     * <li>Any folder is created in the DocLib of the site</li>
     * <li>Version aspect is added to all documents put in this folder</li>
     * <li>Any document is uploaded via Share to this folder</li>
     * <li>Open Details page of the uploaded document and verify version history</li>
     * <li>Navigate to the uploaded document via CIFS and edit the document</li>
     * <li>Open Details page of the uploaded document and verify version history</li>
     * <li>Navigate to the uploaded document via CIFS and edit the document</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6160() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName) + ".txt";
        File file = newFile(fileName, fileName);
        file.deleteOnExit();
        String folderName = getFolderName(testName);

        String remotePath = "Alfresco" + "/" + "Sites" + "/" + siteName + "/" + "documentLibrary" + "/";
        String remotePathToFolder = remotePath + folderName + "/";

        // Create users
        String[] testUserInfo1 = new String[] { testUser1 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo1);

        // Any site is created
        ShareUser.login(drone, testUser1);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // Any folder is created
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        // Version aspect is added to all documents put in this folder

        // create the rule for folder
        FolderRulesPage folderRulesPage = ShareUserSitePage.getFileDirectoryInfo(drone, folderName).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(folderName), "Rule page isn't correct");

        // Fill "Name" field with correct data
        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField("Rule Name");
        createRulePage.fillDescriptionField("Rule Description");

        // Select several values in the When section, e.g.
        // 'Items are created or enter this folder'
        WhenSelectorImpl whenSelectorIml = createRulePage.getWhenOptionObj();
        whenSelectorIml.selectInbound();

        // Select 'All items' from "If" drop-down select control
        AbstractIfSelector ifSelector = createRulePage.getIfOptionObj();
        ifSelector.selectIFOption(0);

        // Select 'Copy' value in the 'Perform Action' section.
        ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectAddAspect(DocumentAspect.VERSIONABLE.getValue());

        // Click "Create" button
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

        DocumentLibraryPage documentLibraryPage = ShareUserSitePage.navigateToFolder(drone, folderName);

        // Any document is uploaded via Share to this folder
        ShareUserSitePage.uploadFile(drone, file);

        assertTrue(documentLibraryPage.isFileVisible(fileName), "File isn't visible " + fileName);

        // Open Details page of the uploaded document and verify version history
        DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName).render();

        assertTrue(detailsPage.getDocumentVersion().equals("1.0"), "Version isn't changed");
        assertTrue(detailsPage.isVersionHistoryPanelPresent(), "Version history panel isn't present, ACE-1628");

        for (int i = 1; i < 4; i++)
        {
            // Navigate to the uploaded document via CIFS and edit the document
            assertTrue(CifsUtil.editContent(shareUrl, testUser1, DEFAULT_PASSWORD, remotePathToFolder, fileName, testUser1 + i), "Can edit " + fileName);
            assertTrue(CifsUtil.checkContent(shareUrl, testUser1, DEFAULT_PASSWORD, remotePathToFolder, fileName, testUser1 + i), "Can edit " + fileName);

            ShareUserSitePage.navigateToFolder(drone, folderName);

            detailsPage = documentLibraryPage.selectFile(fileName).render();

            // Open Details page of the uploaded document and verify version history
            assertTrue(detailsPage.getDocumentVersion().equals("1." + i), "Version isn't changed");
            assertTrue(detailsPage.isVersionHistoryPanelPresent(), "Version history panel isn't present, ACE-1628");
        }

    }

    /**
     * Test: AONE-6161:Renaming a site via cifs
     * <ul>
     * <li>Any user is created, e.g. user1</li>
     * <li>Any site is created via Share client</li>
     * <li>Alfresco is opened via cifs</li>
     * <li>Try to rename created site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6161() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName) + System.currentTimeMillis();
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String siteNameNew = getSiteName(testName + "-New") + System.currentTimeMillis();
        String remotePath = "Alfresco" + "/" + "Sites" + "/";

        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(drone);

        // Try to rename created site
        assertFalse(CifsUtil.renameItem(shareUrl, testUser, DEFAULT_PASSWORD, remotePath, siteName, siteNameNew), "Can rename " + siteName + " site");
    }

    /**
     * Test: AONE-6162:Uploading large number of files to document library via CIFS
     * <ul>
     * <li>Any user is created, e.g. user1</li>
     * <li>Any site is created via Share client</li>
     * <li>Upload large amount of files to document library</li>
     * <li>Via UI try to open document library</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6162() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName) + System.currentTimeMillis();
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String remotePath = "Alfresco" + "/" + "Sites" + "/" + siteName + "/" + "documentLibrary" + "/";
        String temp_folder = "temp_remove";
        String dataPath = DATA_FOLDER + temp_folder;
        String fileName = "temp%s.txt";
        int i;

        File fileForBulkImport = new File(dataPath);
        fileForBulkImport.deleteOnExit();
        boolean success = fileForBulkImport.mkdir();
        if (success)
        {
            for (i = 1; i < 201; i++)
            {
                File file = new File(dataPath, String.format(fileName, i));
                new FileWriter(file);
                file.deleteOnExit();
            }
        }

        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Upload large amount of files to document library
        for (i = 1; i < 201; i++)
        {
            assertTrue(CifsUtil.uploadContent(shareUrl, testUser, DEFAULT_PASSWORD, remotePath, new File(dataPath + SLASH + String.format(fileName, i))),
                    "Can upload " + String.format(fileName, i) + " site");
        }

        // Via UI try to open document library
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        assertTrue(documentLibraryPage.isFileVisible(String.format(fileName, 1)), "Expected file isn't visible");
        PaginationForm paginationForm = documentLibraryPage.getBottomPaginationForm();

        assertEquals(paginationForm.getPaginationInfo(), "1 - 50 of 200", "Wrong info about pagination items.");

        ShareUser.logout(drone);
    }

    /**
     * Test: AONE-6163:Delete non-empty folder
     * <ul>
     * <li>CIFS is adjusted and opened via SMB client</li>
     * <li>Any site is created via Share client</li>
     * <li>Any folder is created</li>
     * <li>Any subfolder and document is added to the folder</li>
     * <li>Delete the folder</li>
     * <li>The folder is deleted with all it's content</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6163() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName) + System.currentTimeMillis();
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String folderName = getFolderName(testName);
        String subFolderName = "sub_" + folderName;
        String subFileName = "sub_" + getFileName(testName) + ".txt";
        String remotePath = "Alfresco" + "/" + "Sites" + "/" + siteName + "/" + "documentLibrary" + "/";

        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser);

        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Any folder is created
        ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);

        // Any subfolder and document is added to the folder
        ShareUser.uploadFileInFolder(drone, new String[] { subFileName, folderName });
        ShareUser.createFolderInFolder(drone, subFolderName, subFolderName, folderName);

        // Delete the folder
        assertTrue(CifsUtil.deleteContent(shareUrl, testUser, DEFAULT_PASSWORD, remotePath, folderName + "/"), "Can't delete " + folderName
                + " folder (non-empty folder)");

        // The folder is deleted with all it's content
        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone);
        assertFalse(documentLibraryPage.isItemVisble(folderName), folderName + " folder is displayed");
        assertFalse(documentLibraryPage.isItemVisble(subFolderName), subFolderName + " subfolder is displayed");
        assertFalse(documentLibraryPage.isItemVisble(subFileName), subFileName + " file is displayed");

    }
}
