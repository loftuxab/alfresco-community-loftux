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

import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.alfresco.application.windows.MicorsoftOffice2010;
import org.alfresco.application.windows.NotepadApplications;
import org.alfresco.explorer.MoveAndCopyActions;
import org.alfresco.explorer.WindowsExplorer;
import org.alfresco.po.alfresco.webdav.WebDavPage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.site.contentrule.FolderRulesPage;
import org.alfresco.po.share.site.contentrule.FolderRulesPageWithRules;
import org.alfresco.po.share.site.contentrule.createrules.CreateRulePage;
import org.alfresco.po.share.site.contentrule.createrules.selectors.AbstractIfSelector;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.ActionSelectorEnterpImpl;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.WhenSelectorImpl;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.DetailsPage;
import org.alfresco.po.share.site.document.DocumentAspect;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FolderDetailsPage;
import org.alfresco.po.share.site.document.SelectAspectsPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.CifsUtil;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.alfresco.utilities.Application;
import org.alfresco.utilities.LdtpUtil;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.cobra.ldtp.Ldtp;

/**
 * @author Sergey Kardash
 */
@Listeners(FailedTestListener.class)
public class RepositoryWebDavTests2 extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(RepositoryWebDavTests2.class);

    private String testUser;

    WindowsExplorer explorer = new WindowsExplorer();
    NotepadApplications notePad = new NotepadApplications();
    MicorsoftOffice2010 word = new MicorsoftOffice2010(Application.WORD, "2010");
    String mapConnect;
    String networkDrive;
    String networkPath;
    private static String sitesPath = "\\Sites\\";
    private static String docLib = "\\documentLibrary";
    private static final String regexUrlWithPort = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(:\\d{1,5})?";
    private static final String regexUrlIP = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})";
    LdtpUtil ldtpUtil = new LdtpUtil();

    public static boolean isAlive(Process process)
    {
        try
        {
            process.exitValue();
            return false;
        }
        catch (IllegalThreadStateException e)
        {
            return true;
        }
    }

    public static void waitProcessEnd(Process process)
    {
        long now = System.currentTimeMillis();
        long timeoutInMillis = 1000L * 10;
        long finish = now + timeoutInMillis;
        while (isAlive(process))
        {
            if (System.currentTimeMillis() > finish)
            {
                process.destroy();
            }
        }
    }

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();


        testName = this.getClass().getSimpleName();
        testUser = getUserNameFreeDomain(testName);

        networkDrive = word.getMapDriver();
        networkPath = word.getMapPath();
        // String ip = getAddress(networkPath);
        // networkPath = networkPath.replaceFirst(regexUrlWithPort, ip);
        if (networkPath.contains("alfresco\\"))
        {
            networkPath = networkPath.concat("webdav");
        }

        // Runtime.getRuntime().exec("cmd /c start /WAIT net use * /d /y");

        mapConnect = "cmd /c start /WAIT net use" + " " + networkDrive + " " + networkPath + " " + "/user:" + ADMIN_USERNAME + " " + ADMIN_PASSWORD;

        //Runtime.getRuntime().exec(mapConnect);
        Process process = Runtime.getRuntime().exec(mapConnect);
        waitProcessEnd(process);

        if (CifsUtil.checkDirOrFileExists(10, 200, networkDrive + sitesPath))
        {
            logger.info("----------Mapping succesfull " + testUser);
        }
        else
        {
            logger.error("----------Mapping was not done " + testUser);
        }

        logger.info("[Suite ] : Start Tests in: " + testName);
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() throws IOException
    {

        Runtime.getRuntime().exec("cmd /c start /WAIT net use * /d /y");

        if (CifsUtil.checkDirOrFileNotExists(7, 200, networkDrive + sitesPath))
        {
            logger.info("--------Unmapping succesfull " + testUser);
        }
        else
        {
            logger.error("--------Unmapping was not done correctly " + testUser);
        }

        super.tearDown();

    }

    private static String getAddressWithPort(String shareUrl)
    {
        Pattern p1 = Pattern.compile(regexUrlWithPort);
        Matcher m1 = p1.matcher(shareUrl);
        if (m1.find())
        {
            return m1.group();
        }
        throw new PageException("Can't extract address from URL");
    }


    /**
     * Test: AONE-6545:Verify accessing WebDAV in browser
     * <ul>
     * <li>In your browser open the following link http://servername/alfresco/webdav</li>
     * <li>Information is entered successfully and Company home is opened in view mode</li>
     * <li>Navigate through the folder structure</li>
     * </ul>
     *
     * @throws Exception
     */
    @Test(groups = "EnterpriseOnly", timeOut = 300000)
    public void AONE_6545() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // In your browser open the following link http://servername/alfresco/webdav
        WebDavPage webDavPage = ShareUtil.navigateToWebDav(drone, ADMIN_USERNAME, ADMIN_PASSWORD).render();

        // Information is entered successfully and Company home is opened in view mode
        Assert.assertTrue(webDavPage.getDirectoryText().equals("Directory listing for /"), "Default directory isn't opened");

        // Navigate through the folder structure
        webDavPage.clickDirectory("Sites");

        Assert.assertTrue(webDavPage.getDirectoryText().equals("Directory listing for /Sites"), "Link 'Sites' isn't opened");

        Assert.assertTrue(webDavPage.checkDirectoryDisplayed(siteName.toLowerCase()), "Expected site " + siteName + " isn't displayed");
        Assert.assertTrue(webDavPage.checkUpToLevelDisplayed(), "'Up a level' link isn't displayed");

        // Navigation works correctly
        webDavPage.clickUpToLevel();

        Assert.assertTrue(webDavPage.getDirectoryText().equals("Directory listing for /"), "Default directory isn't opened");

    }

    /**
     * Test: AONE-6544:Copying content/folder
     * <ul>
     * <li>Any any site is created via Alfresco Share</li>
     * <li>Any folder is created in the space/site's doclib</li>
     * <li>Any content is created/uploaded to the space/site</li>
     * <li>Any metadata is specified for the folder and for the content (title, description)</li>
     * <li>Alfresco WebDAV connection is established</li>
     * <li>The space / the site's doclib is opened via WebDAV</li>
     * <li>Copy the folder and the content to any other space</li>
     * <li>Verify the original folder and content</li>
     * <li>Verify the folder and content copies</li>
     * </ul>
     *
     * @throws Exception
     */
    @Test(groups = { "WEBDAVWindowsClient", "EnterpriseOnly" },dependsOnMethods = "AONE_6545" , timeOut = 600000, alwaysRun = true)
    public void AONE_6544() throws Exception
    {
        String testName = getTestName();
        String siteName1 = getSiteName(testName + "-1-") +  getRandomString(3);
        String fileName = getFileName(testName + "-1") +getRandomString(3);
        String fileTitle = getFileName(testName + "-1") + "_title";
        String fileDesc = getFileName(testName + "-1") + "_description";
        String folderName = "folder1" + getRandomString(3);
        String folderName2 = "folder2" + getRandomString(3);
        String folderTitle = getFolderName(testName) + "_title";
        String folderDesc = getFolderName(testName) + "_description";

        // Any site is created
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName1).render();

        // Any folder is created, some items are added to folder
        ShareUserSitePage.createFolder(drone, folderName, folderTitle, folderDesc).render();
        // Any folder is created in the space/site's doclib
        ShareUserSitePage.createFolder(drone, folderName2, folderName2, folderName2).render();

        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibrary(drone);

        docLibPage.selectFolder(folderName).render();

        // Any content is created/uploaded to the space/site
        ContentDetails contentDetails = new ContentDetails(fileName, fileTitle, fileDesc, fileName);
        contentDetails.setName(fileName);
        // Any metadata is specified for the folder and for the content (title, description)
        ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.PLAINTEXT, docLibPage);

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");
        docLibPage.selectFolder(folderName).render();

        Assert.assertTrue(docLibPage.isFileVisible(fileName), "File " + fileName + " isn't visible");

        // Alfresco WebDAV connection is established
        String fullPath = networkDrive + sitesPath + siteName1.toLowerCase() + docLib + "\\";

        explorer.openWindowsExplorer();
        explorer.openFolder(fullPath);

        // The space / the site's doclib is opened via WebDAV
        String windowName = ldtpUtil.findWindowName("documentLibrary");
        explorer.activateApplicationWindow(windowName);

        // Copy the folder and the content to any other space
        explorer.copyFolderInCurrent(folderName, "documentLibrary", folderName2);
        explorer.activateApplicationWindow(folderName2);
        logger.info("Close window");
        explorer.closeExplorer();

        docLibPage = ShareUser.openDocumentLibrary(drone);

        // Verify the original folder and content
        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        FolderDetailsPage folderDetailsPage = docLibPage.getFileDirectoryInfo(folderName).selectViewFolderDetails().render();

        Map<String, Object> properties = folderDetailsPage.getProperties();
        Assert.assertNotNull(properties);
        Assert.assertEquals(properties.get("Name"), folderName, "Name Property is not equals with folder " + folderName + ".");
        Assert.assertEquals(properties.get("Title"), folderTitle, "Title Property is not present");
        Assert.assertEquals(properties.get("Description"), folderDesc, "Description Property is not present");

        docLibPage = ShareUser.openDocumentLibrary(drone);

        docLibPage.selectFolder(folderName).render();
        Assert.assertTrue(docLibPage.isFileVisible(fileName), "File " + fileName + " isn't visible");

        DocumentDetailsPage detailsPage = docLibPage.selectFile(fileName).render();

        properties = detailsPage.getProperties();
        Assert.assertNotNull(properties);
        Assert.assertEquals(properties.get("Name"), fileName, "Name Property is not equals with file " + fileName + ".");
        Assert.assertEquals(properties.get("Title"), fileTitle, "Title Property is not present");
        Assert.assertEquals(properties.get("Description"), fileDesc, "Description Property is not present");

        // Copies are created successfully. Alfresco-stored metadata is not carried over

        docLibPage = ShareUser.openDocumentLibrary(drone);

        docLibPage.selectFolder(folderName2).render();
        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        folderDetailsPage = docLibPage.getFileDirectoryInfo(folderName).selectViewFolderDetails().render();

        properties = folderDetailsPage.getProperties();
        Assert.assertNotNull(properties);
        Assert.assertEquals(properties.get("Name"), folderName, "Name Property is not equals with folder " + folderName + ".");
        Assert.assertEquals(properties.get("Title"), "(None)", "Title Property is present");
        Assert.assertEquals(properties.get("Description"), "(None)", "Description Property is present");

        docLibPage = ShareUser.openDocumentLibrary(drone);

        docLibPage.selectFolder(folderName2).render();
        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        docLibPage.selectFolder(folderName).render();
        Assert.assertTrue(docLibPage.isFileVisible(fileName), "File " + fileName + " isn't visible");

        detailsPage = docLibPage.selectFile(fileName).render();

        properties = detailsPage.getProperties();
        Assert.assertNotNull(properties);
        Assert.assertEquals(properties.get("Name"), fileName, "Name Property is not equals with file " + fileName + ".");
        Assert.assertEquals(properties.get("Title"), "(None)", "Title Property is present");
        Assert.assertEquals(properties.get("Description"), "(None)", "Description Property is present");

        SiteUtil.deleteSite(drone, siteName1);
        ShareUser.logout(drone);
    }

    /**
     * Test: AONE-6554:Hidden files
     * <ul>
     * <li>Map Alfresco Webdav as a network drive</li>
     * <li>Create a folder with the name started with dot (e.g. '.folder new')</li>
     * <li>Create a file with the name started with dot (e.g. '.test.txt')</li>
     * <li>Log into Share</li>
     * <li>Verify the presence of the file and folder</li>
     * <li>The file and folder are not displayed</li>
     * </ul>
     *
     * @throws Exception
     */
    @Test(groups = { "WEBDAVWindowsClient", "EnterpriseOnly" },dependsOnMethods = "AONE_6544" , timeOut = 600000, alwaysRun = true)
    public void AONE_6554() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName + "-1-") + getRandomString(3);
        String fileName = ".test" +getRandomString(3);
        String fileNameCheck = "lst" + fileName.replaceAll("\\W", "");
        String folderName = ".folder1 " + getRandomString(3);
        String folderNameCheck = "lst" + folderName.replaceAll("\\W", "");

        // Any site is created
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // Alfresco WebDAV connection is established
        String fullPath = networkDrive + sitesPath + siteName.toLowerCase() + docLib + "\\";

        explorer.openWindowsExplorer();
        explorer.openFolder(fullPath);

        // The space / the site's doclib is opened via WebDAV
        String windowName = ldtpUtil.findWindowName("documentLibrary");
        explorer.activateApplicationWindow(windowName);

        // Create a folder with the name started with dot (e.g. '.folder new')
        explorer.createNewFolderMenu(folderName);

        // Create a file with the name started with dot (e.g. '.test.txt')
        explorer.rightClickCreate("documentLibrary", fileName, Application.TEXTFILE);

        explorer.activateApplicationWindow(windowName);

        Ldtp newLdtp = new Ldtp(windowName);
        String[] objects = newLdtp.getObjectList();
        Assert.assertTrue(Arrays.asList(objects).contains(fileNameCheck), "Expected file '" + fileName + "' isn't displayed");
        Assert.assertTrue(Arrays.asList(objects).contains(folderNameCheck), "Expected folder '" + folderName + "' isn't displayed");
        logger.info("Close window");
        explorer.closeExplorer();

        // Log into Share
        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibrary(drone);

        int fileCount = docLibPage.getFiles().size();

        // The file and folder are not displayed
        Assert.assertTrue(fileCount == 0, "Some file isn't hidden in Document Library. MNT-13125");

    }

    /**
     * Test: AONE-6556:Verify the rule is applied via WebDAV without errors
     * <ul>
     * <li>Any site is created</li>
     * <li>At least 2 folders are created within the site</li>
     * <li>Map Alfresco Webdav as a network drive</li>
     * <li>Click Manage Rule for one of the folders</li>
     * <li>f1 and create inbound rule performs</li>
     * <li>add taggable aspect</li>
     * <li>move item to another folder, e.g. f2</li>
     * <li>Open folder f1 in WIndows Explorer</li>
     * <li>Drag and drop any file from your computer to the folder f1</li>
     * <li>Open folder f2</li>
     * <li>Log into Share</li>
     * <li>Verify the presence of the file and folder</li>
     * <li>The file and folder are not displayed</li>
     * </ul>
     *
     * @throws Exception
     */
    @Test(groups = { "WEBDAVWindowsClient", "EnterpriseOnly" },dependsOnMethods = "AONE_6554" , timeOut = 600000, alwaysRun = true)
    public void AONE_6556() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName + "-1-") + getRandomString(3);
        String fileName = "test" +getRandomString(3);
        String fileNameWithExt = fileName + ".txt";
        String tempFolder = "tempfolder" + getRandomString(3);
        String folderName1 = "folder1" + getRandomString(3);
        String folderName2 = "folder2" + getRandomString(3);

        File folder = new File(DATA_FOLDER + tempFolder);
        folder.mkdirs();
        folder.deleteOnExit();
        File file = newFile(DATA_FOLDER + tempFolder + SLASH + fileNameWithExt, fileNameWithExt);
        file.deleteOnExit();
        String filePath = file.getParent();

        // Any site is created
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // At least 2 folders are created within the site
        ShareUserSitePage.createFolder(drone, folderName1, folderName1);
        ShareUserSitePage.createFolder(drone, folderName2, folderName2);

        // create the rule for folder
        FolderRulesPage folderRulesPage = ShareUserSitePage.getFileDirectoryInfo(drone, folderName1).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(folderName1), "Rule page isn't correct");

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

        // Select 'add taggable aspect' value in the 'Perform Action' section.
        ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectAddAspect(DocumentAspect.TAGGABLE.getValue());

        // Select 'Move' value in the 'Perform Action' section.
        createRulePage.addOrRemoveOptionsFieldsToBlock(CreateRulePage.Block.ACTION_BLOCK, CreateRulePage.AddRemoveAction.ADD);
        actionSelectorEnterpImpl.selectMove(siteName, "Documents", folderName2);

        // Click "Create" button
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName1), "Rule page with rule isn't correct");

        // Open folder f1 in Windows Explorer
        explorer.openWindowsExplorer();
        String fullPath = networkDrive + sitesPath + siteName.toLowerCase() + docLib + "\\" + folderName1;
        String fullPathNew = networkDrive + sitesPath + siteName.toLowerCase() + docLib + "\\" + folderName2 + "\\" + fileNameWithExt;

        // Drag and drop any file from your computer to the folder f1 (move ope)
        explorer.moveFileToOtherFolder(filePath, fileName, fullPath);
        Assert.assertTrue(explorer.isFilePresent(fullPathNew), "File isn't moved");
        explorer.closeExplorer();

        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone);

        Assert.assertTrue(documentLibraryPage.isFileVisible(folderName1), "Folder " + folderName1 + " isn't visible");
        Assert.assertTrue(documentLibraryPage.isFileVisible(folderName2), "Folder " + folderName2 + " isn't visible");

        // Open folder f2
        documentLibraryPage.selectFolder(folderName2).render();

        Assert.assertTrue(documentLibraryPage.isFileVisible(fileNameWithExt), "File " + fileNameWithExt + " isn't visible");

        // Open folder2 in browser and verify the file is not locked
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileNameWithExt).isLocked(), "File " + fileNameWithExt + " is locked");

        DetailsPage detailsPage = ShareUser.openDocumentDetailPage(drone, fileNameWithExt);

        SelectAspectsPage aspectsPage = detailsPage.selectManageAspects().render();

        // Verify that the taggable aspect is applied
        Assert.assertTrue(aspectsPage.getSelectedAspects().contains(DocumentAspect.TAGGABLE), "'TAGGABLE'' aspect isn't applied");

    }

    /**
     * Test: AONE-6557:Checking version history when saving document via WebDav
     * <ul>
     * <li>Should be tested on Mac OS and Windows 7.</li>
     * <li>Any site is created</li>
     * <li>Any folder is created in the DocLib of the site</li>
     * <li>Version aspect is added to all documents put in this folder</li>
     * <li>Any document is uploaded via Share to this folder</li>
     * <li>Alfresco webdav is mounted</li>
     * <li>Create a new *.docx ot *.txt document on client machine</li>
     * <li>Enter any text and save the *.docx ot *.txt document to the folder created in preconditions (step 2) via webdav</li>
     * <li>Check a version of saved documents in Share</li>
     * <li>Edit the *.docx ot *.txt document via WebDav. Version is incremented</li>
     * <li>Repeat several times</li>
     * <li>Drag and Drop any file from your desktop to the space created in preconditions (step 2) via WebDav</li>
     * <li>Check a version of saved documents in Alfresco</li>
     * <li>Drag and Drop any file from your desktop to the space</li>
     * <li>Verify the presence of the file and folder</li>
     * <li>The file and folder are not displayed</li>
     * </ul>
     *
     * @throws Exception
     */
    @Test(groups = { "WEBDAVWindowsClient", "EnterpriseOnly" },dependsOnMethods = "AONE_6556" , timeOut = 900000, alwaysRun = true)
    public void AONE_6557() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName + "-1-") + getRandomString(3);
        String fileName1 = "test1" +getRandomString(3);
        String fileName1WithExt = fileName1 + ".txt";
        String fileName2 = "test2" +getRandomString(3);
        String fileName2WithExt = fileName2 + ".txt";
        String tempFolder = "tempfolder" + getRandomString(3);
        String folderName1 = "folder1" + getRandomString(3);

        File folder = new File(DATA_FOLDER + tempFolder);
        folder.mkdirs();
        folder.deleteOnExit();
        File file = newFile(DATA_FOLDER + tempFolder + SLASH + fileName2WithExt, fileName2WithExt);
        file.deleteOnExit();
        String filePath = file.getParent();

        // Any site is created
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // Any folder is created in the DocLib of the site
        ShareUserSitePage.createFolder(drone, folderName1, folderName1);

        // create the rule for folder
        FolderRulesPage folderRulesPage = ShareUserSitePage.getFileDirectoryInfo(drone, folderName1).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(folderName1), "Rule page isn't correct");

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

        // Select 'add taggable aspect' value in the 'Perform Action' section.
        ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectAddAspect(DocumentAspect.VERSIONABLE.getValue());

        // Click "Create" button
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName1), "Rule page with rule isn't correct");

        String fullPath = networkDrive + sitesPath + siteName.toLowerCase() + docLib + "\\" + folderName1;
        // Create a new *.docx ot *.txt document on client machine
        notePad.openNotepadApplication();
        notePad.setNotepadWindow("Notepad");
        notePad.saveAsNotpad(fullPath, fileName1);
        // Enter any text and save the *.docx ot *.txt document to the folder created in preconditions (step 2) via webdav
        notePad.getAbstractUtil().waitForWindow(fileName1);
        notePad.editNotepad("first create in client", fileName1);
        notePad.ctrlSSave();
        notePad.getAbstractUtil().getLdtp().waitTime(10);

        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();
        documentLibraryPage.selectFolder(folderName1);
        DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName1WithExt).render();
        // Check a version of saved documents in Share
        assertTrue(detailsPage.getDocumentVersion().equals("1.0"), "Version '1.0' for file " + fileName1WithExt + " isn't presented");

        // Edit the *.docx ot *.txt document via WebDav
        notePad.appendTextToNotepad(" adding abc", fileName1);
        notePad.ctrlSSave();
        notePad.getAbstractUtil().getLdtp().waitTime(10);

        // Check the version in Share
        documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();
        documentLibraryPage.selectFolder(folderName1);
        detailsPage = documentLibraryPage.selectFile(fileName1WithExt).render();
        assertTrue(detailsPage.getDocumentVersion().equals("1.1"), "Version '1.1' for file " + fileName1WithExt + " isn't presented");

        // Repeat several times
        notePad.appendTextToNotepad(" adding abc", fileName1);
        notePad.ctrlSSave();
        notePad.getAbstractUtil().getLdtp().waitTime(10);

        documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();
        documentLibraryPage.selectFolder(folderName1);
        detailsPage = documentLibraryPage.selectFile(fileName1WithExt).render();
        assertTrue(detailsPage.getDocumentVersion().equals("1.2"), "Version '1.2' for file " + fileName1WithExt + " isn't presented");

        notePad.closeNotepad(fileName1);
        explorer.openWindowsExplorer();

        // Drag and Drop any file from your desktop to the space created in preconditions (step 2) via WebDav
        explorer.copyFileToOtherFolder(filePath, fileName2, fullPath);
        Assert.assertTrue(explorer.isFilePresent(fullPath + SLASH + fileName2WithExt), "File " + fileName2WithExt + " isn't moved");

        documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();
        documentLibraryPage.selectFolder(folderName1);
        detailsPage = documentLibraryPage.selectFile(fileName2WithExt).render();
        // Check a version of saved documents in Alfresco
        assertTrue(detailsPage.getDocumentVersion().equals("1.0"), "Version '1.0' for file " + fileName2WithExt + " isn't presented");

        // Drag and Drop any file from your desktop to the space
        explorer.copyFileToOtherFolder(filePath, fileName2, fullPath, MoveAndCopyActions.COPY_AND_REPLACE);

        // Open document's details page via browser and verify version history for the rewrited document
        documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();
        documentLibraryPage.selectFolder(folderName1);
        detailsPage = documentLibraryPage.selectFile(fileName2WithExt).render();
        assertTrue(detailsPage.getDocumentVersion().equals("1.1"), "Version '1.1' for file " + fileName2WithExt + " isn't presented");

        // Drag and Drop any file from your desktop to the space
        explorer.copyFileToOtherFolder(filePath, fileName2, fullPath, MoveAndCopyActions.COPY_AND_REPLACE);

        // Open document's details page via browser and verify version history for the rewrited document
        documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();
        documentLibraryPage.selectFolder(folderName1);
        detailsPage = documentLibraryPage.selectFile(fileName2WithExt).render();
        assertTrue(detailsPage.getDocumentVersion().equals("1.2"), "Version '1.2' for file " + fileName2WithExt + " isn't presented");

        explorer.closeExplorer();

    }

    /**
     * Test: AONE-6547:Verify accessing WebDAV via Windows machine
     * <ul>
     * <li>Any any site is created via Alfresco Share</li>
     * <li>Windows explorer is opened</li>
     * <li>Add the url of the webdav server</li>
     * <li>Fill in the user/password (e.g. admin/admin) and press 'OK' button</li>
     * <li>Alfresco WebDAV connection is established</li>
     * <li>The created connection is opened in a new window. The appropriate space are displayed</li>
     * <li>Open webdav folder and navigate through the folders structure</li>
     * </ul>
     *
     * @throws Exception
     */
    @Test(groups = { "WEBDAVWindowsClient", "EnterpriseOnly" },dependsOnMethods = "AONE_6557" , timeOut = 1000000, alwaysRun = true)
    public void AONE_6547() throws Exception
    {
        String testName = getTestName();
        String siteName1 = getSiteName(testName + "-1-") + getRandomString(3);
        String webdavPath = "alfresco/webdav";

        // Any site is created
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);

        Runtime.getRuntime().exec("cmd /c start /WAIT net use * /d /y");
        logger.info("Remove all mapped drives");

        // Windows explorer is opened
        explorer.openWindowsExplorer();
        logger.info("Windows explorer is opened");

        String drive = explorer.mapNetworkDrive(shareUrl, webdavPath, ADMIN_USERNAME, ADMIN_PASSWORD);
        explorer.getAbstractUtil().waitForWindow("frmwebdav");

        // The created connection is opened in a new window. The appropriate space are displayed
        String windowName = explorer.getAbstractUtil().getAbsoluteWindowName("frmwebdav");
        Assert.assertTrue(windowName.contains(drive), "The created connection is not opened in a new window");

        // Open webdav folder and navigate through the folders structure
        explorer.openFolder(drive + "\\" + "Sites");
        explorer.activateApplicationWindow("frmSites");

        String[] allObjectsWindow = explorer.getAbstractUtil().getLdtp().getObjectList();
        String ip = getAddressWithPort(shareUrl).replace(".", "");
        ip = ip.replace(":", "@");

        Assert.assertTrue(Arrays.asList(allObjectsWindow).contains("lst" + siteName1.toLowerCase()), "Navigation works not correctly. Expected site "
                + siteName1.toLowerCase() + " isn't presented.");
//        Assert.assertTrue(Arrays.asList(allObjectsWindow).contains("tblc" + ip), "The created connection isn't displayed in \"The Internet\" section.");

        String uknwebdav = null;
        for (String objectWindow : allObjectsWindow)
        {
            if (objectWindow.toLowerCase().contains("uknwebdav"))
            {
                uknwebdav = objectWindow;
                break;
            }
        }

        // The created connection is displayed in "The Internet" section
        if (uknwebdav != null)
        {
            Assert.assertTrue(uknwebdav.contains(("DavWWWRoot\\alfresco)(") + drive.substring(0, drive.length() - 1)),
                    "The created connection isn't displayed in \"The Internet\" section.");
        }

        logger.info("Close window");
        explorer.closeExplorer();
        Runtime.getRuntime().exec("cmd /c start /WAIT net use * /d /y");

        SiteUtil.deleteSite(drone, siteName1);
        ShareUser.logout(drone);
    }


}
