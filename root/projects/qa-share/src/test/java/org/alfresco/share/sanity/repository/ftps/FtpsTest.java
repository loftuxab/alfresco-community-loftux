package org.alfresco.share.sanity.repository.ftps;

import static org.alfresco.po.share.enums.UserRole.COLLABORATOR;
import static org.alfresco.po.share.enums.UserRole.CONSUMER;
import static org.alfresco.po.share.enums.UserRole.CONTRIBUTOR;
import static org.alfresco.po.share.enums.UserRole.COORDINATOR;
import static org.alfresco.po.share.enums.UserRole.EDITOR;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.document.ManagePermissionsPage;
import org.alfresco.po.share.site.document.UserProfile;
import org.alfresco.po.share.systemsummary.AdminConsoleLink;
import org.alfresco.po.share.systemsummary.RepositoryServerClusteringPage;
import org.alfresco.po.share.systemsummary.SystemSummaryPage;
import org.alfresco.po.share.util.PageUtils;
import org.alfresco.share.util.FtpUtil;
import org.alfresco.share.util.FtpsUtil;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserRepositoryPage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * This class contains tests from Enterprise -> Sanity -> FTPS area
 *
 * @author Marina.Nenadovets
 */
@Listeners(FailedTestListener.class)
@Test(groups = "EnterpriseOnly")
public class FtpsTest extends FtpsUtil
{
    private static Log logger = LogFactory.getLog(FtpsTest.class);
    protected static String siteName;
    protected static String fileName;
    protected static String folderName;
    private static String remotePathToRepo = "/" + "Alfresco";
    private static String server;
    private static String testUser;
    private static String[] folderNames;
    private static UserRole[] userRoles;
    private static File newFile;
    private static String newFileName = getRandomString(6);

    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        testName = this.getClass().getSimpleName();
        siteName = getSiteName(testName);
        fileName = getFileName(testName);
        folderName = getFolderName(testName);
        testUser = getUserNameFreeDomain(testName);
        folderNames = new String[] { folderName + "_coord", folderName + "_coll", folderName + "_contr", folderName + "_ed", folderName + "_cons" };
        userRoles = new UserRole[] { COORDINATOR, COLLABORATOR, CONTRIBUTOR, EDITOR, CONSUMER };
        newFile = newFile(DATA_FOLDER + "ftps" + SLASH + newFileName, testUser);
        server = shareUrl;
        logger.info("Start Tests in: " + testName);
        super.setup();

        //creating test data
        try
        {
            SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            RepositoryServerClusteringPage clusteringPage = sysSummaryPage.openConsolePage(AdminConsoleLink.RepositoryServerClustering).render();
            if (clusteringPage.isClusterEnabled())
            {
                server = clusteringPage.getClusterMembers().get(0);
            }
            else
            {
                server = PageUtils.getAddress(shareUrl).replaceAll("(:\\d{1,5})?", "");
            }
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);
            UserProfile profile = new UserProfile();
            profile.setfName(testUser);
            profile.setUsername(testUser);

            for (String theFolder : folderNames)
            {
                String folderPath = REPO + SLASH + theFolder;
                String[] fileInfo = { fileName, folderPath, testUser };
                ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
                ShareUserRepositoryPage.openRepositoryDetailedView(drone).render();
                ShareUserRepositoryPage.createFolderInRepository(drone, theFolder, theFolder);
                ShareUserRepositoryPage.uploadFileInFolderInRepository(drone, fileInfo);
                ShareUserRepositoryPage.openRepository(drone).render();
                ManagePermissionsPage managePermissionsPage = ShareUser.returnManagePermissionPage(drone, theFolder).render();
                ManagePermissionsPage.UserSearchPage userSearchPage = managePermissionsPage.selectAddUser().render();
                managePermissionsPage = userSearchPage.searchAndSelectUser(profile).render();
                managePermissionsPage.setAccessType(profile, userRoles[Arrays.asList(folderNames).indexOf(theFolder)]);
                managePermissionsPage.selectSave();
            }
            FtpsUtil.setCustomFtpPort(drone, ftpPort);
            if (!keystorePath.isEmpty())
            {
                FtpsUtil.enableFtps(server, keystorePath, truststorePath);
            }
            else
            {
                File keyStore = FtpsUtil.generateKeyStore(getRandomString(6));
                FtpsUtil.enableFtps(server, keyStore, null);
            }
        }
        catch (Exception e)
        {
            throw new SkipException("Skipping as test data wasn't generated: " + e.getMessage());
        }
    }

    /**
     * Coordinator. Available actions
     *
     * @throws Exception
     */

    @Test
    public void AONE_8021() throws Exception
    {
        String folderName = folderNames[0];
        String folderNewName = folderName + "-FTPS";
        String folderPath = remotePathToRepo + "/" + folderNewName;
        String newSubFolder = folderName + "-sub";
        String fileNewName = fileName + "-FTPS";
        boolean isSuccess;

        //Created space is displayed
        isSuccess = FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, folderName, remotePathToRepo);
        assertTrue(isSuccess, "Folder isn't available");

        //Try to rename space - OK
        isSuccess = FtpsUtil.renameFile(server, testUser, DEFAULT_PASSWORD, remotePathToRepo, folderName, folderNewName);
        assertTrue(FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, folderNewName, remotePathToRepo));
        assertTrue(isSuccess, "Folder " + folderName + " wasn't renamed");

        //Try to rename content in this space - OK
        isSuccess = FtpsUtil.renameFile(server, testUser, DEFAULT_PASSWORD, folderPath, fileName, fileNewName);
        assertTrue(FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, fileNewName, folderPath));
        assertTrue(isSuccess, "File " + fileName + " wasn't renamed");

        //Try to edit content - OK
        isSuccess = FtpsUtil.editContent(server, testUser, DEFAULT_PASSWORD, fileNewName, folderPath);
        assertTrue(isSuccess && FtpsUtil.getContent(server, testUser, DEFAULT_PASSWORD, fileNewName, folderPath).equals(testUser));

        //Try to delete content - OK
        isSuccess = FtpsUtil.deleteContentItem(server, testUser, DEFAULT_PASSWORD, fileNewName, folderPath);
        assertTrue(isSuccess, fileNewName + " wasn't deleted");
        assertFalse(FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, fileNewName, folderPath), fileNewName + " wasn't deleted");

        //Try to create new space - OK
        isSuccess = FtpsUtil.createSpace(server, testUser, DEFAULT_PASSWORD, newSubFolder, folderPath);
        assertTrue(isSuccess, "New folder " + newSubFolder + " wasn't created");
        assertTrue(FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, newSubFolder, folderPath), "New folder " + newSubFolder + " wasn't created");

        //Try to create new content
        isSuccess = FtpsUtil.uploadContent(server, testUser, DEFAULT_PASSWORD, newFile, folderPath);
        assertTrue(isSuccess, "New content " + fileNewName + " wasn't created");
        assertTrue(FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, newFileName, folderPath), "New content " + fileNewName + " wasn't created");

        //Try to delete space - OK
        isSuccess = FtpsUtil.deleteSpace(server, testUser, DEFAULT_PASSWORD, folderNewName, remotePathToRepo);
        assertTrue(isSuccess, folderNewName + " wasn't deleted");
        assertFalse(FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, folderNewName, remotePathToRepo), folderNewName + " wasn't deleted");
    }

    /**
     * Collaborator. Available actions
     *
     * @throws Exception
     */

    @Test
    public void AONE_8022() throws Exception
    {
        String folderName = folderNames[1];
        String folderNewName = folderName + "-FTPS";
        String folderPath = remotePathToRepo + "/" + folderNewName;
        String newSubFolder = folderName + "-sub";
        String fileNewName = fileName + "-FTPS";
        boolean isSuccess;

        //Created space is displayed
        isSuccess = FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, folderName, remotePathToRepo);
        assertTrue(isSuccess, "Folder isn't available");

        //Try to rename space - OK
        isSuccess = FtpsUtil.renameFile(server, testUser, DEFAULT_PASSWORD, remotePathToRepo, folderName, folderNewName);
        assertTrue(isSuccess && FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, folderNewName, remotePathToRepo), "Folder " + folderName +
            " wasn't renamed");

        //Try to rename content in this space - OK
        isSuccess = FtpsUtil.renameFile(server, testUser, DEFAULT_PASSWORD, folderPath, fileName, fileNewName);
        assertTrue(isSuccess && FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, fileNewName, folderPath), "File " + fileName + " wasn't renamed");

        //Try to edit content - OK
        isSuccess = FtpsUtil.editContent(server, testUser, DEFAULT_PASSWORD, fileNewName, folderPath);
        assertTrue(isSuccess && FtpsUtil.getContent(server, testUser, DEFAULT_PASSWORD, fileNewName, folderPath).equals(testUser), "Content of " + fileNewName +
        " wasn't edited");

        //Try to delete content - code 450
        assertEquals(FtpsUtil.deleteContentWithoutRights(server, testUser, DEFAULT_PASSWORD, fileNewName, folderPath), 450, "Incorrect reply code was received");
        assertTrue(FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, fileNewName, folderPath), fileNewName + " was deleted");

        //Try to create new space - OK
        isSuccess = FtpsUtil.createSpace(server, testUser, DEFAULT_PASSWORD, newSubFolder, folderPath);
        assertTrue(isSuccess, "New folder " + newSubFolder + " wasn't created");
        assertTrue(FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, newSubFolder, folderPath), "New folder " + newSubFolder + " wasn't created");

        //Try to create new content
        isSuccess = FtpsUtil.uploadContent(server, testUser, DEFAULT_PASSWORD, newFile, folderPath);
        assertTrue(isSuccess, "New content " + newFile + " wasn't created");
        assertTrue(FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, newFileName, folderPath), "New content " + fileNewName + " wasn't created");

        //Try to delete space - code 550
        assertEquals(FtpsUtil.deleteSpaceWithoutRights(server, testUser, DEFAULT_PASSWORD, folderNewName, remotePathToRepo), 550, "Incorrect reply code");
        assertTrue(FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, folderNewName, remotePathToRepo), fileNewName + " was deleted");
    }

    /**
     * Contributor. Available actions
     *
     * @throws Exception
     */

    @Test
    public void AONE_8023() throws Exception
    {
        String folderName = folderNames[2];
        String folderNewName = folderName + "-FTPS";
        String folderPath = remotePathToRepo + "/" + folderName;
        String newSubFolder = folderName + "-sub";
        String fileNewName = fileName + "-FTPS";
        boolean isSuccess;

        //Created space is displayed
        isSuccess = FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, folderName, remotePathToRepo);
        assertTrue(isSuccess, "Folder isn't available");

        //Try to rename space - code 450
        assertEquals(FtpsUtil.renameFileWithoutRights(server, testUser, DEFAULT_PASSWORD, remotePathToRepo, folderName, folderNewName), 450, "Incorrect reply code");
        assertFalse(FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, remotePathToRepo, folderNewName), folderName + " was renamed to " + folderNewName);

        //Try to rename content in this space - 450
        assertEquals(FtpsUtil.renameFileWithoutRights(server, testUser, DEFAULT_PASSWORD, folderPath, fileName, fileNewName), 450, "Incorrect reply code");
        assertFalse(FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, folderPath, fileName), folderName + " was renamed to " + folderNewName);

        //Try to edit content - code 451
        assertEquals(FtpsUtil.editContentWithoutRights(server, testUser, DEFAULT_PASSWORD, fileName, folderPath, fileName), 451, "Incorrect reply code");
        assertEquals(FtpsUtil.getContent(server, testUser, DEFAULT_PASSWORD, fileName, folderPath), testUser, "Content was edited");

        //Try to delete content - 450
        assertEquals(FtpsUtil.deleteContentWithoutRights(server, testUser, DEFAULT_PASSWORD, fileName, folderPath), 450, "Incorrect reply code");
        assertTrue(FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, fileName, folderPath), fileName + " was deleted");

        //Try to create new space - OK
        isSuccess = FtpsUtil.createSpace(server, testUser, DEFAULT_PASSWORD, newSubFolder, folderPath);
        assertTrue(isSuccess && FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, newSubFolder, folderPath), "New folder " + newSubFolder +
            " wasn't created");

        //Try to create new content - OK
        isSuccess = FtpsUtil.uploadContent(server, testUser, DEFAULT_PASSWORD, newFile, folderPath);
        assertTrue(isSuccess && FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, newFileName, folderPath), "New content " + fileNewName +
            " wasn't created");

        //Try to delete space - code 550
        assertEquals(FtpsUtil.deleteSpaceWithoutRights(server, testUser, DEFAULT_PASSWORD, folderName, remotePathToRepo), 550, "Incorrect reply code");
        assertTrue(FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, folderName, remotePathToRepo), fileName + " was deleted");
    }

    /**
     * Editor. Available actions
     *
     * @throws Exception
     */

    @Test
    public void AONE_8024() throws Exception
    {
        String folderName = folderNames[3];
        String folderNewName = folderName + "-FTPS";
        String folderPath = remotePathToRepo + "/" + folderNewName + "/";
        String newSubFolder = folderName + "-sub";
        String fileNewName = fileName + "-FTPS";
        boolean isSuccess;

        //Created space is displayed
        isSuccess = FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, folderName, remotePathToRepo);
        assertTrue(isSuccess, "Folder isn't available");

        //Try to rename space - OK
        isSuccess = FtpsUtil.renameFile(server, testUser, DEFAULT_PASSWORD, remotePathToRepo, folderName, folderNewName);
        assertTrue(isSuccess && FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, folderNewName, remotePathToRepo), "Folder " + folderName +
            " wasn't renamed");

        //Try to rename content in this space - OK
        isSuccess = FtpsUtil.renameFile(server, testUser, DEFAULT_PASSWORD, folderPath, fileName, fileNewName);
        assertTrue(isSuccess && FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, fileNewName, folderPath), "File " + fileName + " wasn't renamed");

        //Try to edit content - OK
        isSuccess = FtpsUtil.editContent(server, testUser, DEFAULT_PASSWORD, fileNewName, folderPath);
        assertTrue(isSuccess && FtpsUtil.getContent(server, testUser, DEFAULT_PASSWORD, fileNewName, folderPath).equals(testUser), "Content of " + fileNewName
            + " wasn't edited");

        //Try to delete content - code 450
        assertEquals(FtpsUtil.deleteContentWithoutRights(server, testUser, DEFAULT_PASSWORD, fileNewName, folderPath), 450, "Incorrect reply code received");
        assertFalse(FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, fileName, folderPath), fileName + " was deleted");

        //Try to create new space - code 450
        assertEquals(FtpsUtil.createSpaceWithoutRights(server, testUser, DEFAULT_PASSWORD, newSubFolder, folderPath), 450, "Incorrect reply code received");
        assertFalse(FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, newSubFolder, folderPath), "Folder " + newSubFolder + " was created");

        //Try to create new content - code 451
        assertEquals(FtpsUtil.uploadContentWithoutRights(server, testUser, DEFAULT_PASSWORD, newFile, folderPath), 451, "Incorrect reply code received");
        assertFalse(FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, newFileName, folderPath), "New content " + fileNewName + " was created");

        //Try to delete space - code 550
        assertEquals(FtpsUtil.deleteSpaceWithoutRights(server, testUser, DEFAULT_PASSWORD, folderNewName, remotePathToRepo), 550, "Incorrect reply code received");
        assertTrue(FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, folderNewName, remotePathToRepo), folderName + " was deleted");
    }

    /**
     * Consumer. Available actions
     *
     * @throws Exception
     */

    @Test
    public void AONE_8025() throws Exception
    {
        String folderName = folderNames[4];
        String folderNewName = folderName + "-FTPS";
        String folderPath = remotePathToRepo + "/" + folderName + "/";
        String newSubFolder = folderName + "-sub";
        String fileNewName = fileName + "-FTPS";
        boolean isSuccess;

        //Created space is displayed
        isSuccess = FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, folderName, remotePathToRepo);
        assertTrue(isSuccess, "Folder isn't available");

        //Try to rename space - code 450
        assertEquals(FtpsUtil.renameFileWithoutRights(server, testUser, DEFAULT_PASSWORD, remotePathToRepo, folderName, folderNewName), 450, "Incorrect reply code");
        assertFalse(FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, remotePathToRepo, folderNewName), folderName + " was renamed to " + folderNewName);

        //Try to rename content in this space - 450
        assertEquals(FtpsUtil.renameFileWithoutRights(server, testUser, DEFAULT_PASSWORD, folderPath, fileName, fileNewName), 450, "Incorrect reply code");
        assertFalse(FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, folderPath, fileName), folderName + " was renamed to " + folderNewName);

        //Try to edit content - code 451
        assertEquals(FtpsUtil.editContentWithoutRights(server, testUser, DEFAULT_PASSWORD, fileName, folderPath, fileName), 451, "Incorrect reply code");
        assertEquals(FtpsUtil.getContent(server, testUser, DEFAULT_PASSWORD, fileName, folderPath), testUser, "Content was edited");

        //Try to delete content - 450
        assertEquals(FtpsUtil.deleteContentWithoutRights(server, testUser, DEFAULT_PASSWORD, fileName, folderPath), 450, "Incorrect reply code");
        assertTrue(FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, fileName, folderPath), fileName + " was deleted");

        //Try to create new space - code 450
        assertEquals(FtpsUtil.createSpaceWithoutRights(server, testUser, DEFAULT_PASSWORD, newSubFolder, folderPath), 450, "Incorrect reply code received");
        assertFalse(FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, newSubFolder, folderPath), "Folder " + newSubFolder + " was created");

        //Try to create new content - code 451
        assertEquals(FtpsUtil.uploadContentWithoutRights(server, testUser, DEFAULT_PASSWORD, newFile, folderPath), 451, "Incorrect reply code received");
        assertFalse(FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, newFileName, folderPath), "New content " + fileNewName + " was created");

        //Try to delete space - code 550
        assertEquals(FtpsUtil.deleteSpaceWithoutRights(server, testUser, DEFAULT_PASSWORD, folderPath, remotePathToRepo), 550, "Incorrect reply code received");
        assertTrue(FtpsUtil.isObjectExists(server, testUser, DEFAULT_PASSWORD, folderName, remotePathToRepo), folderName + " was deleted");
    }

    /**
     * Copy and Move non-empty folder
     *
     * @throws Exception
     */

    @Test
    public void AONE_8026() throws Exception
    {
        String folder1 = getFolderName(getRandomString(5));
        String folder2 = getFolderName(getRandomString(5));
        String subFolder1 = getFolderName(getRandomString(5));
        String subFolder2 = getFolderName(getRandomString(5));
        String [] fileInfo1 = {testName, REPO + SLASH + folder1 + SLASH + subFolder1};
        String [] fileInfo2 = {testName, REPO + SLASH + folder1 + SLASH + subFolder2};
        String targetPath = remotePathToRepo + "/" + folder1;
        String destinationPath = remotePathToRepo + "/" + folder2;

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUserRepositoryPage.openRepositoryDetailedView(drone).render();
        ShareUserRepositoryPage.createFolderInRepository(drone, folder1, folder1);
        ShareUserRepositoryPage.createFolderInRepository(drone, folder2, folder2);
        ShareUserRepositoryPage.createFolderInFolderInRepository(drone, subFolder1, subFolder1, REPO + SLASH + folder1);
        ShareUserRepositoryPage.openRepository(drone).render();
        ShareUserRepositoryPage.createFolderInFolderInRepository(drone, subFolder2, subFolder2, REPO + SLASH + folder1);
        ShareUserRepositoryPage.uploadFileInFolderInRepository(drone, fileInfo1);
        ShareUserRepositoryPage.openRepository(drone).render();
        ShareUserRepositoryPage.uploadFileInFolderInRepository(drone, fileInfo2);

        //Move Folder1 from the space, where it is located, to other space by FTPS
        FtpsUtil.moveFolder(server, ADMIN_USERNAME, ADMIN_PASSWORD, targetPath , subFolder1, destinationPath);

        //Log in to Alfresco client and navigate to the space, were moved by FTPS Folder1 was located
        RepositoryPage repoPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folder1).render();
        assertFalse(repoPage.isItemVisble(subFolder1), "Folder wasn't moved and still visible");
        repoPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folder2).render();
        assertTrue(repoPage.isItemVisble(subFolder1), "Folder wasn't moved and invisible in destination folder");
        repoPage = (RepositoryPage) repoPage.getFileDirectoryInfo(subFolder1).clickOnTitle();
        assertTrue(repoPage.isItemVisble(testName), "File wasn't moved");

        //Copy Folder2 from the space, where it is located, to other space by FTPS
        FtpsUtil.copyFolder(server, ADMIN_USERNAME, ADMIN_PASSWORD, targetPath, subFolder2, destinationPath);

        //Navigate to the space, were Folder2 was copied - it has all added items
        repoPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folder1).render();
        assertTrue(repoPage.isItemVisble(subFolder2), "Folder is invisible");
        repoPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folder2).render();
        assertTrue(repoPage.isItemVisble(subFolder2), "Folder wasn't moved and invisible in destination folder");
        repoPage = (RepositoryPage) repoPage.getFileDirectoryInfo(subFolder2).clickOnTitle();
        assertTrue(repoPage.isItemVisble(testName), "File wasn't moved");
    }

    @AfterClass(alwaysRun = true)
    private void deleteFolders()
    {
            boolean isRemoved;

            for (String theFolder : folderNames)
            {
                try
                {
                    isRemoved = FtpsUtil.deleteSpace(server, ADMIN_USERNAME, ADMIN_PASSWORD, theFolder, remotePathToRepo);
                    if(!isRemoved)
                        FtpsUtil.deleteSpace(server, ADMIN_USERNAME, ADMIN_PASSWORD, theFolder + "-FTPS", remotePathToRepo);
                }
                catch (IOException ex)
                {
                    isRemoved = FtpUtil.DeleteSpace(server, ADMIN_USERNAME, ADMIN_PASSWORD, theFolder, remotePathToRepo);
                    if(!isRemoved)
                        FtpUtil.DeleteSpace(server, ADMIN_USERNAME, ADMIN_PASSWORD, theFolder + "-FTPS", remotePathToRepo);
                }
            }
    }

    @AfterClass(alwaysRun = true)
    private void disableFTPS()
    {
        FtpsUtil.disableFtps(server);
        deleteKeyStores();
    }
}
