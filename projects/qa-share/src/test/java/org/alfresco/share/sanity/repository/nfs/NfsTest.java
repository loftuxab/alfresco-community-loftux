/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
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

package org.alfresco.share.sanity.repository.nfs;

import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.share.util.*;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.alfresco.po.share.enums.UserRole.*;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Created by Olga Lokhach
 */
@Test(groups = "EnterpriseOnly" , timeOut = 600000)

public class NfsTest extends NfsUtil
{
    private static Log logger = LogFactory.getLog(NfsTest.class);
    protected static String siteName;
    protected static String fileName;
    protected static String folderName;
    private static String testUser;
    private static String[] folderNames;
    private static UserRole[] userRoles;
    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
    private static final Pattern DOMAIN_PATTERN = Pattern.compile("\\w+(\\.\\w+)*(\\.\\w{2,})");
    private static String pathToNFS = "/tmp/alf/";

    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        testName = this.getClass().getSimpleName();
        siteName = getSiteName(testName);
        fileName = getFileName(testName);
        folderName = getFolderName(testName);
        testUser = testName.toLowerCase();
        folderNames = new String[] { folderName + "_coord", folderName + "_coll", folderName + "_contr", folderName + "_ed", folderName + "_cons" };
        userRoles = new UserRole[] { COORDINATOR, COLLABORATOR, CONTRIBUTOR, EDITOR, CONSUMER };
        logger.info("Start Tests in: " + testName);
        super.setup();


        //creating test data

        try
        {

            ShareUser.createEnterpriseUser(drone, ADMIN_USERNAME, testUser, testUser, DEFAULT_LASTNAME, DEFAULT_PASSWORD);

            for (String folder : folderNames)
            {
                String folderPath = REPO + SLASH + folder;
                String[] fileInfo = { fileName, folderPath, testUser };
                ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
                ShareUserRepositoryPage.openRepositoryDetailedView(drone).render();
                ShareUserRepositoryPage.createFolderInRepository(drone, folder, folder);
                ShareUserRepositoryPage.uploadFileInFolderInRepository(drone, fileInfo);
                ShareUserRepositoryPage.openRepository(drone).render();
                ShareUserMembers.managePermissionsOnContent(drone, testUser, folder, userRoles[Arrays.asList(folderNames).indexOf(folder)], true);
            }


            NfsUtil.configNfsServer(shareUrl, ADMIN_PASSWORD, testUser);
            NfsUtil.configNfsUser(shareUrl, testUser);

            // Mount NFS
            setSshHost(mountPointHost);
            RemoteUtil.mountNfs(shareUrl, nfsPort, nfsMountPort);

            // Create user on server
            RemoteUtil.createUserOnServer(testUser, DEFAULT_PASSWORD);
        }
        catch (Exception e)
        {
            throw new SkipException("Skipping as test data wasn't generated: " + e.getMessage());
        }

    }

    @AfterClass(alwaysRun = true)
    private void disableNFS() throws Exception
    {
        RemoteUtil.unmountNfs(pathToNFS);
        RemoteUtil.deleteUserOnServer(testUser);
    }

    /**
     * Coordinator. Available actions
     *
     * @throws Exception
     */

    @Test
    public void AONE_8043() throws Exception
    {
        String folderName = folderNames[0];
        String folderNewName = folderName + "-NFS";
        String newSubFolder = folderName + "-sub";
        String fileNewName = fileName + "-NFS";
        File file = newFile(fileName, fileName);

        // Navigate to created space by NFS
        assertTrue(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS, folderName),"Folder isn't available");

        // Try to rename space - OK
        assertTrue(NfsUtil.renameFileOrFolder(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS, folderName, folderNewName));
        assertTrue(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS, folderNewName), "Folder " + folderName + " wasn't renamed");

        // Try to rename content in this space - OK
        assertTrue(NfsUtil.renameFileOrFolder(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderNewName, fileName, fileNewName));
        assertTrue(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderNewName, fileNewName), "File " + fileName + " wasn't renamed");

        // Try to edit content - OK
        assertTrue(NfsUtil.editContent(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderNewName, fileNewName, testUser));
        assertTrue(NfsUtil.getContent(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderNewName, fileNewName).contains(testUser), "Content wasn't edited");

        // Try to delete content - OK
        assertTrue(NfsUtil.deleteContentItem(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderNewName, fileNewName));
        assertFalse(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS, fileNewName), fileNewName + " wasn't deleted");

        // Try to create new space - OK
        assertTrue(NfsUtil.createSpace(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderNewName, newSubFolder));
        assertTrue(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderNewName, newSubFolder),
            "New folder " + newSubFolder + " wasn't created");

        // Try to create new content -OK
        assertTrue(NfsUtil.uploadContent(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderNewName, file));
        assertTrue(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderNewName, fileName),
            "New content " + fileName + " wasn't created");

        // Try to delete space -OK
        assertTrue(NfsUtil.deleteFolder(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS, folderNewName));
        assertFalse(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS, folderNewName), folderNewName + " wasn't deleted");

    }

    /**
     * Collaborator. Available actions
     *
     * @throws Exception
     */

    @Test
    public void AONE_8044() throws Exception
    {
        String folderName = folderNames[1];
        String folderNewName = folderName + "-NFS";
        String newSubFolder = folderName + "-sub";
        String fileNewName = fileName + "-NFS";
        File file = newFile(fileName, fileName);

        // Navigate to created space by NFS
        assertTrue(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS, folderName),"Folder isn't available");

        // Try to rename space - OK
        assertTrue(NfsUtil.renameFileOrFolder(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS, folderName, folderNewName));
        assertTrue(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS, folderNewName), "Folder " + folderName + " wasn't renamed");

        // Try to rename content in this space - OK
        assertTrue(NfsUtil.renameFileOrFolder(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderNewName, fileName, fileNewName));
        assertTrue(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderNewName, fileNewName), "File " + fileName + " wasn't renamed");

        // Try to edit content - OK
        assertTrue(NfsUtil.editContent(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderNewName, fileNewName, testUser));
        assertTrue(NfsUtil.getContent(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderNewName, fileNewName).contains(testUser), "Content wasn't edited");

        // Try to delete content - user isn't able to delete content
        assertFalse(NfsUtil.deleteContentItem(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderNewName, fileNewName));
        assertTrue(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderNewName, fileNewName), fileNewName + " was deleted");

        // Try to create new space - OK
        assertTrue(NfsUtil.createSpace(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderNewName, newSubFolder));
        assertTrue(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderNewName, newSubFolder),
            "New folder " + newSubFolder + " wasn't created");

        // Try to create new content - OK
        assertTrue(NfsUtil.uploadContent(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderNewName, file));
        assertTrue(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderNewName, fileName),
            "New content " + fileName + " wasn't created");

        // Try to delete space - user isn't able to delete folder
        assertFalse(NfsUtil.deleteFolder(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS, folderNewName));
        assertTrue(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS, folderNewName), folderNewName + " was deleted");

    }

    /**
     * Contributor. Available actions
     *
     * @throws Exception
     */

    @Test
    public void AONE_8045() throws Exception
    {
        String folderName = folderNames[2];
        String folderNewName = folderName + "-NFS";
        String newSubFolder = folderName + "-sub";
        String fileNewName = fileName + "-NFS";
        File file = newFile(fileNewName, fileName);

        // Navigate to created space by NFS
        assertTrue(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS, folderName),"Folder isn't available");

        // Try to rename space - user isn't able to rename space
        assertFalse(NfsUtil.renameFileOrFolder(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS, folderName, folderNewName));
        assertFalse(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS, folderNewName), "Folder " + folderName + " was renamed");

        // Try to rename content in this space - user isn't able to rename content
        assertFalse(NfsUtil.renameFileOrFolder(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderName, fileName, fileNewName));
        assertFalse(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderName, fileNewName), "File " + fileName + " was renamed");

        // Try to edit content - user isn't able to edit content
        assertFalse(NfsUtil.editContent(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderName, fileName, testUser));
        assertFalse(NfsUtil.getContent(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderName, fileName).contains(testUser), "Content was edited");

        // Try to delete content - user isn't able to delete content
        assertFalse(NfsUtil.deleteContentItem(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderName, fileName));
        assertTrue(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderName, fileName), fileName + " was deleted");

        // Try to create new space - OK
        assertTrue(NfsUtil.createSpace(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderName, newSubFolder));
        assertTrue(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderName, newSubFolder),
            "New folder " + newSubFolder + " wasn't created");

        // Try to create new content - OK
        assertTrue(NfsUtil.uploadContent(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderName, file));
        assertTrue(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderName, fileNewName),
            "New content " + fileNewName + " wasn't created");

        // Try to delete space - user isn't able to delete folder
        assertFalse(NfsUtil.deleteFolder(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS, folderName));
        assertTrue(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS, folderName), folderName + " was deleted");

    }

    /**
     * Editor. Available actions
     *
     * @throws Exception
     */

    @Test
    public void AONE_8046() throws Exception
    {
        String folderName = folderNames[3];
        String folderNewName = folderName + "-NFS";
        String newSubFolder = folderName + "-sub";
        String fileNewName = fileName + "-NFS";
        File file = newFile(fileName, fileName);

        // Navigate to created space by NFS
        assertTrue(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS, folderName), "Folder isn't available");

        // Try to rename space - OK
        assertTrue(NfsUtil.renameFileOrFolder(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS, folderName, folderNewName));
        assertTrue(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS, folderNewName), "Folder " + folderName + " wasn't renamed");

        // Try to rename content in this space - OK
        assertTrue(NfsUtil.renameFileOrFolder(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderNewName, fileName, fileNewName));
        assertTrue(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderNewName, fileNewName), "File " + fileName + " wasn't renamed");

        // Try to edit content - OK
        assertTrue(NfsUtil.editContent(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderNewName, fileNewName, testUser));
        assertTrue(NfsUtil.getContent(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderNewName, fileNewName).contains(testUser), "Content wasn't edited");

        // Try to delete content - user isn't able to delete content
        assertFalse(NfsUtil.deleteContentItem(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderNewName, fileNewName));
        assertTrue(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderNewName, fileNewName), fileNewName + " was deleted");

        // Try to create new space - user isn't able to create space
        assertFalse(NfsUtil.createSpace(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderNewName, newSubFolder));
        assertFalse(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderNewName, newSubFolder),
            "New folder " + newSubFolder + " was created");

        // Try to create new content - user isn't able to create content
        assertFalse(NfsUtil.uploadContent(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderNewName, file));
        assertFalse(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderNewName, fileName),
            "New content " + fileName + " was created");

        // Try to delete space - user isn't able to delete folder
        assertFalse(NfsUtil.deleteFolder(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS, folderNewName));
        assertTrue(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS, folderNewName), folderNewName + " was deleted");

    }

    /**
     * Consumer. Available actions
     *
     */
    @Test
    public void AONE_8047() throws Exception
    {
        String folderName = folderNames[4];
        String folderNewName = folderName + "-NFS";
        String newSubFolder = folderName + "-sub";
        String fileNewName = fileName + "-NFS";
        File file = newFile(fileNewName, fileName);

        // Navigate to created space by NFS
        assertTrue(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS, folderName),"Folder isn't available");

        // Try to rename space - user isn't able to rename space
        assertFalse(NfsUtil.renameFileOrFolder(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS, folderName, folderNewName));
        assertFalse(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS, folderNewName), "Folder " + folderName + " was renamed");

        // Try to rename content in this space - user isn't able to rename content
        assertFalse(NfsUtil.renameFileOrFolder(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderName, fileName, fileNewName));
        assertFalse(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderName, fileNewName), "File " + fileName + " was renamed");

        // Try to edit content - user isn't able to edit content
        assertFalse(NfsUtil.editContent(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderName, fileName, testUser));
        assertFalse(NfsUtil.getContent(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderName, fileName).contains(testUser), "Content was edited");

        // Try to delete content - user isn't able to delete content
        assertFalse(NfsUtil.deleteContentItem(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderName, fileName));
        assertTrue(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderName, fileName), fileName + " was deleted");

        // Try to create new space - user isn't able to create space
        assertFalse(NfsUtil.createSpace(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderName, newSubFolder));
        assertFalse(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderName, newSubFolder),
            "New folder " + newSubFolder + " was created");

        // Try to create new content - user isn't able to create content
        assertFalse(NfsUtil.uploadContent(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderName, file));
        assertFalse(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS + folderName, fileNewName),
            "New content " + fileNewName + " was created");

        // Try to delete space - user isn't able to delete folder
        assertFalse(NfsUtil.deleteFolder(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS, folderName));
        assertTrue(NfsUtil.isObjectExists(mountPointHost, testUser, DEFAULT_PASSWORD, pathToNFS, folderName), folderName + " was deleted");

    }

    /**
     * Copy and Move non-empty folder
     */

    @Test
    public void AONE_8048() throws Exception
    {
        String folder1 = folderName + "_1";
        String folder2 = folderName + "_2";
        String subFolder1 = folderName + "_sub_1";
        String subFolder2 = folderName + "_sub_2";
        String[] fileInfo1 = { testName, REPO + SLASH + folder1 + SLASH + subFolder1 };
        String[] fileInfo2 = { testName, REPO + SLASH + folder1 + SLASH + subFolder2 };
        String targetPath = pathToNFS + folder1 + "/";
        String destinationPath = pathToNFS + folder2 +"/";

        // Share login
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Create two folders.
        ShareUserRepositoryPage.openRepositoryDetailedView(drone).render();
        ShareUserRepositoryPage.createFolderInRepository(drone, folder1, folder1);
        ShareUserMembers.managePermissionsOnContent(drone, testUser, folder1, UserRole.COORDINATOR, true);
        ShareUserRepositoryPage.createFolderInRepository(drone, folder2, folder2);
        ShareUserMembers.managePermissionsOnContent(drone, testUser, folder2, UserRole.COORDINATOR, true);

        // Create two sub-folders in folder1 and add some items to it.
        ShareUserRepositoryPage.openRepositoryDetailedView(drone).render();
        ShareUserRepositoryPage.createFolderInFolderInRepository(drone, subFolder1, subFolder1, REPO + SLASH + folder1);
        ShareUserRepositoryPage.openRepository(drone).render();
        ShareUserRepositoryPage.createFolderInFolderInRepository(drone, subFolder2, subFolder2, REPO + SLASH + folder1);
        ShareUserRepositoryPage.uploadFileInFolderInRepository(drone, fileInfo1);
        ShareUserRepositoryPage.openRepository(drone).render();
        ShareUserRepositoryPage.uploadFileInFolderInRepository(drone, fileInfo2);

        //Move Folder1 from the space, where it is located, to other space by FTPS
        assertTrue(NfsUtil.moveFolder(mountPointHost, testUser, DEFAULT_PASSWORD, targetPath, subFolder1, destinationPath), "Can't move a folder");

        //Log in to Alfresco client and navigate to the space, were moved by FTPS Folder1 was located
        RepositoryPage repoPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folder1).render();
        assertFalse(repoPage.isItemVisble(subFolder1), "Folder wasn't moved and still visible");
        repoPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folder2).render();
        assertTrue(repoPage.isItemVisble(subFolder1), "Folder wasn't moved and invisible in destination folder");
        repoPage = (RepositoryPage) repoPage.getFileDirectoryInfo(subFolder1).clickOnTitle().render();
        assertTrue(repoPage.isItemVisble(testName), "File wasn't moved");

        //Copy Folder2 from the space, where it is located, to other space by FTPS
        assertTrue(NfsUtil.copyFolder(mountPointHost, testUser, DEFAULT_PASSWORD, targetPath, subFolder2, destinationPath), "Can't copy a folder");

        //Navigate to the space, were Folder2 was copied - it has all added items
        repoPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folder1).render();
        assertTrue(repoPage.isItemVisble(subFolder2), "Folder is invisible");
        repoPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folder2).render();
        assertTrue(repoPage.isItemVisble(subFolder2), "Folder wasn't visible in destination folder");
        repoPage = (RepositoryPage) repoPage.getFileDirectoryInfo(subFolder2).clickOnTitle().render();
        assertTrue(repoPage.isItemVisble(testName), "File wasn't visible in destination folder");
    }




    private void setSshHost(String sshHostUrl)
    {
        sshHost = getAddress(sshHostUrl);
    }

    private static String getAddress(String url)
    {
        Matcher m = IP_PATTERN.matcher(url);
        if (m.find())
        {
            return m.group();
        }
        else
        {
            m = DOMAIN_PATTERN.matcher(url);
            if (m.find())
            {
                return m.group();
            }
        }
        throw new PageOperationException(String.format("Can't parse address from url[%s]", url));
    }


}
