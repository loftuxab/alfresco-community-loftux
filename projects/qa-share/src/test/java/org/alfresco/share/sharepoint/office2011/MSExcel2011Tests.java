package org.alfresco.share.sharepoint.office2011;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.alfresco.application.mac.MicrosoftExcel2011;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.DocumentLibraryUtil;
import org.alfresco.share.util.FileBaseUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Please pay attention that there is an error that sometimes occures on Office2011 application on MAC: issue logged as MNT-12847
 * 
 * @author Paul Brodner
 */
@Listeners(FailedTestListener.class)
public class MSExcel2011Tests extends AbstractUtils
{

    private String testName;
    private String testUser;
    private String testSiteName;
    private String testTmpFolderName = "Documents/alfresco-testdocs";

    private File xls9760TestFile;
    private File xls9761TestFile;
    private File xls9762TestFile;
    private File xls9761DownloadTestFile;
    private File xls9763TestFile;
    private File xls9765TestFile;
    private File xlsCommonFile;

    private ArrayList<File> testFiles = new ArrayList<File>();

    private DocumentLibraryPage documentLibraryPage;
    private static final String SHAREPOINT = "sharepoint";

    private MicrosoftExcel2011 appExcel2011;

    private static final Logger logger = Logger.getLogger(MSExcel2011Tests.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        appExcel2011 = new MicrosoftExcel2011();
        appExcel2011.killProcesses();
        testName = this.getClass().getSimpleName() + "22";
        testUser = getUserNameFreeDomain(testName);
        testSiteName = getSiteName(testName);

        // used from testdata folder
        xls9760TestFile = getTestDataFile(SHAREPOINT, "AONE-9760.xlsx");
        xls9761TestFile = getTestDataFile(SHAREPOINT, "AONE-9761.xlsx");
        xls9762TestFile = getTestDataFile(SHAREPOINT, "AONE-9762.xlsx");
        xls9763TestFile = getTestDataFile(SHAREPOINT, "AONE-9763.xlsx");
        xlsCommonFile = getTestDataFile(SHAREPOINT, "InputOpen.xlsx"); // common excel file used in multiple tests

        xls9761DownloadTestFile = new File(System.getProperty("user.home"), testTmpFolderName + "/tmpxls9761TestFile.xlsx");
        xls9765TestFile = new File(System.getProperty("user.home"), testTmpFolderName + "/AONE-9765.xlsx");

        // this files will be uploaded on dataprep
        testFiles.add(xls9761TestFile);
        testFiles.add(xls9762TestFile);
        testFiles.add(xls9763TestFile);
        testFiles.add(xlsCommonFile);

    }

    @Override
    @AfterClass(alwaysRun = true)
    public void tearDown()
    {
        try
        {
            appExcel2011.handleCrash();
            appExcel2011.getMDC().exitApplication();
        }
        catch (Exception e)
        {
            logger.error("Error on TearDown Office 2011" + e.getMessage());
        }

        super.tearDown();
    }

    @Test(groups = { "DataPrepExcelMac" })
    public void dataPrep_AONE() throws Exception
    {
        // Create normal User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser });

        ShareUtil.logout(drone);
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create public site
        try
        {
            ShareUser.createSite(drone, testSiteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        }
        catch (SkipException e)
        {
            logger.error("Data Prep on Create Site: " + e.getMessage());
        }

        ShareUser.openSiteDocumentLibraryFromSearch(drone, testSiteName);

        // upload all test files
        for (Iterator<File> iterator = testFiles.iterator(); iterator.hasNext();)
        {
            File file = iterator.next();
            logger.info("Setup Data Prep using: " + file.getName());
            ShareUserSitePage.uploadFile(drone, file).render();
        }

        xls9765TestFile.delete();
        xls9761DownloadTestFile.delete();
    }

    /**
     * Login with default test used and open DocumentLibrary of testSiteName
     */
    private void openDocumentLibraryForTest()
    {
        if (documentLibraryPage == null || !documentLibraryPage.isLoggedIn())
        {
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        }

        documentLibraryPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, testSiteName);
    }

    private void openCleanMDCtool(String testSiteName, String testUser, String password) throws Exception
    {
        // MDC tool: A sharepoint connection to Alfresco is created
        appExcel2011.getMDC().killProcesses();
        appExcel2011.getMDC().cleanUpHistoryConnectionList();
        appExcel2011.getMDC().openApplication();
        appExcel2011.getMDC().addLocation(getVTIDocumentLibraryPath(testSiteName), testUser, password);
    }

    /**
     * Preconditions
     * <ul>
     * <li>Any site is created in Share;</li>
     * <li>Any MS Excel document is uploaded to the site's document library;</li>
     * <li>MS Document Connection is opened;</li>
     * <li>A sharepoint connection to Alfresco is created;</li>
     * <li>Site Document Library is opened</li>
     * </ul>
     */
    @Test(groups = "Enterprise4.2", description = "Upload the document - Add file action")
    public void AONE_9760() throws Exception
    {
        openCleanMDCtool(testSiteName, testUser, DEFAULT_PASSWORD);

        // ---- Step 1 ----
        // ---- Step action ----
        // Click on Add File Button
        // ---- Expected results ----
        // Upload New Files window is opened

        // ---- Step 2 ----
        // ---- Step action ----
        // Choose the created in the pre-condition document and click on Upload button.
        // ---- Expected results ----
        // The document is uploaded
        appExcel2011.getMDC().addFile(xls9760TestFile);

        // I choose to open DL here so in this time, the document will be uploaded from step2 - no wait required
        openDocumentLibraryForTest();

        // ---- Step 3 ----
        // ---- Step action ----
        // Verify the document library of the site in the Share.
        // ---- Expected results ----
        // The document was uploaded correctly
        Assert.assertTrue(documentLibraryPage.isFileVisible(xls9760TestFile.getName()), "The document was uploaded corectly.");
        // No data was lost (checking the size of the document uploaded)
        Assert.assertEquals(DocumentLibraryUtil.getDocumentProperties(documentLibraryPage, xls9760TestFile.getName()).size(), 10);

        appExcel2011.getMDC().exitApplication();

        ShareUser.logout(drone);
    }

    /**
     * Preconditions
     * <ul>
     * <li>Any site is created in Share;</li>
     * <li>Any MS Excel document is uploaded to the site's document library;</li>
     * <li>MS Document Connection is opened;</li>
     * <li>A sharepoint connection to Alfresco is created;</li>
     * <li>Site Document Library is opened</li>
     * </ul>
     */
    @Test(groups = "Enterprise4.2", description = "Download the document - Save as action")
    public void AONE_9761() throws Exception
    {
        openCleanMDCtool(testSiteName, testUser, DEFAULT_PASSWORD);

        // ---- Step 1----
        // ---- Step action ----
        // Choose the document and choose Save As action from the context menu.
        // ---- Expected results ----
        // Save document_name.docx As window is opened.
        appExcel2011.getMDC().search(xls9761TestFile.getName());

        // ---- Step 2 ----
        // ---- Step action ----
        // Choose any location, e.g. Desktop, specify any name and click on Save button.
        // ---- Expected results ----
        // The document is downloaded.
        appExcel2011.getMDC().saveAsFirstDocumentAs(xls9761DownloadTestFile);

        // ---- Step 3 ----
        // ---- Step action ----
        // Verify the chosen location, e.g. Desktop.
        // ---- Expected results ----
        // The document was downloaded correctly. No data was lost.

        xls9761DownloadTestFile = new File(System.getProperty("user.home"), testTmpFolderName + "/tmpxls9761TestFile.xlsx");
        boolean fileSaved = FileBaseUtils.waitForFile(xls9761DownloadTestFile);
        
        Assert.assertTrue(fileSaved, "File " + xls9761DownloadTestFile.getName() + " was saved localy from MDC.");
        Assert.assertEquals(xls9761DownloadTestFile.length(), 8679, "Data was lost from the file downloaded");
        xls9761DownloadTestFile.delete();
        appExcel2011.getMDC().exitApplication();
    }

    /**
     * Preconditions
     * <ul>
     * <li>Any site is created in Share;</li>
     * <li>Any MS Excel document is uploaded to the site's document library;</li>
     * <li>MS Document Connection is opened;</li>
     * <li>A sharepoint connection to Alfresco is created;</li>
     * <li>Site Document Library is opened</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "Enterprise4.2", description = "Read document")
    public void AONE_9762() throws Exception
    {
        openDocumentLibraryForTest();
        openCleanMDCtool(testSiteName, testUser, DEFAULT_PASSWORD);

        // ---- Step 1 ----
        // ---- Step action ----
        // Choose the document and click on Read button.
        // ---- Expected results ----
        // The document is opened in a read-only mode.
        appExcel2011.getMDC().search(xls9762TestFile.getName());
        appExcel2011.getMDC().readFirstDocument();

        appExcel2011.addCredentials(testUser, DEFAULT_PASSWORD);
        boolean isReadOnly = appExcel2011.isFileInReadOnlyMode(xls9762TestFile.getName());
        Assert.assertTrue(isReadOnly, "File was opened in Read Only mode");
        appExcel2011.getMDC().focus();

        // ---- Step 2 ----
        // ---- Step action ----
        // Verify the document library of the site in the Share.
        // ---- Expected results ----
        // The document is not locked. No changes are made.
        boolean isLocked = DocumentLibraryUtil.isFileLocked(documentLibraryPage, xls9762TestFile.getName());
        Assert.assertFalse(isLocked, "The document is not locked. No changes are made.");

        // ---- Step 3 ----
        // ---- Step action ----
        // Try to enter any changes to the opened document.
        // ---- Expected results ----
        // No changes can be made. The document is read-only.
        // {Paul: nothing to add here}

        // ---- Step 4 ----
        // ---- Step action ----
        // Try to check out the opened document.
        // ---- Expected results ----
        // The document cannot be checked out. "You can't edit this file. You have the file open as read-only. To edit the file, close it, and then try again."
        // dialog message is displayed.
        // {Paul: cannot reproduce this on MAC. In this case the Checkout button is disabled, so I check for this functionality}

        Assert.assertEquals(appExcel2011.getMDC().isBtnCheckOutEnabled(), 0, "The file cannot be Checked out");
        appExcel2011.getMDC().exitApplication();
        appExcel2011.exitApplication();
        ShareUser.logout(drone);
    }

    /**
     * Preconditions
     * <ul>
     * <li>Any site is created in Share;</li>
     * <li>Any MS Excel document is uploaded to the site's document library;</li>
     * <li>MS Document Connection is opened;</li>
     * <li>A sharepoint connection§ to Alfresco is created;</li>
     * <li>Site Document Library is opened</li>
     * </ul>
     */
    @Test(groups = "Enterprise4.2", description = "Edit document")
    public void AONE_9763() throws Exception
    {
        openDocumentLibraryForTest();
        // we need to know first the current version of the document
        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(xls9763TestFile.getName()).render();
        String oldVersion = docDetailsPage.getCurrentVersionDetails().getVersionNumber();

        openCleanMDCtool(testSiteName, testUser, DEFAULT_PASSWORD);

        // ---- Step 1 ----
        // ---- Step action ----
        // Choose the document and click on Edit button.
        // ---- Expected results ----
        // The document is opened in a read-write mode.
        appExcel2011.getMDC().search(xls9763TestFile.getName());
        appExcel2011.getMDC().editFirstDocument();
        appExcel2011.addCredentials(testUser, DEFAULT_PASSWORD);
        appExcel2011.waitForWindow(xls9763TestFile.getName());

        boolean isEditMode = appExcel2011.isFileInEditMode(xls9763TestFile.getName());
        Assert.assertTrue(isEditMode, "The document is opened in a read-write mode.");

        // ---- Step 2 ----
        // ---- Step action ----
        // Verify the document library of the site in the Share.
        // ---- Expected results ----
        // The document is locked. "This document is locked by you." message is displayed.
        ShareUser.openDocumentLibrary(drone);
        boolean isLocked = DocumentLibraryUtil.isFileLockedByYou(documentLibraryPage, xls9763TestFile.getName());
        Assert.assertTrue(isLocked, "The document is locked. \"This document is locked by you.\" message is displayed.");

        // ---- Step 3 ----
        // ---- Step action ----
        // Try to enter any changes to the opened document.
        // ---- Expected results ----
        // The changes can be entered.
        appExcel2011.waitForWindow(xls9763TestFile.getName());
        appExcel2011.setFileName(xls9763TestFile.getName());
        appExcel2011.focus();
        appExcel2011.edit("some test data");

        // ---- Step 4 ----
        // ---- Step action ----
        // Try to check out the opened document.
        // ---- Expected results ----
        // The document's changes are saved automatically. The document is checked out.
        // {Paul: on MDC we cannot Checkout document after we EDIT. On EDIT, the document is auto-checked out. At this point can just SAVE the file}
        appExcel2011.save();

        // ---- Step 5 ----
        // ---- Step action ----
        // Verify the document library of the site in the Share.
        // ---- Expected results ----
        // Previously applied changes are present. A new minor version is created. The document is locked. "This document is locked by you for offline editing."
        // message is displayed.
        // {Paul: the document was not checked out. It was only Edited. This message will apply on checkout operation. We have a distinct test case for this:
        // AONE-9766}

        ShareUser.openDocumentLibrary(drone);
        isLocked = DocumentLibraryUtil.isFileLocked(documentLibraryPage, xls9763TestFile.getName());
        Assert.assertTrue(isLocked, "This document is locked by you.");

        // check we have a new minor version
        String currentVersion = docDetailsPage.getCurrentVersionDetails().getVersionNumber();
        Assert.assertNotEquals(oldVersion, currentVersion);

        appExcel2011.closeFile(xls9763TestFile.getName());
        appExcel2011.getMDC().exitApplication();
        appExcel2011.exitApplication();
        ShareUser.logout(drone);
    }

    /**
     * Preconditions
     * <ul>
     * <li>Any site is created in Share;</li>
     * <li>Any MS Excel document is uploaded to the site's document library;</li>
     * <li>MS Document Connection is opened;</li>
     * <li>A sharepoint connection to Alfresco is created;</li>
     * <li>Site Document Library is opened.</li>
     * <li>The document is opened for editing.</li>
     * <li>Any data is entered into the opened document.</li>
     * </ul>
     */
    @Test(groups = "Enterprise4.2", description = "Edit document - Upload changes")
    public void AONE_9764() throws Exception
    {
        openDocumentLibraryForTest();

        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(xlsCommonFile.getName()).render();
        String oldVersion = docDetailsPage.getCurrentVersionDetails().getVersionNumber();

        openCleanMDCtool(testSiteName, testUser, DEFAULT_PASSWORD);

        // ---- Step 1 ----
        // ---- Step action ----
        // Choose the document and click on Upload button.
        // ---- Expected results ----
        // Upload Changes window is displayed.
        appExcel2011.getMDC().search(xlsCommonFile.getName());
        appExcel2011.getMDC().editFirstDocument();
        appExcel2011.addCredentials(testUser, DEFAULT_PASSWORD);

        // {Paul: cannot use Open URL, Appliction will crash}
        // appExcel2011.openURL(getVTIDocumentLibraryFilePath(testSiteName, xlsCommonFile.getName()));

        // ---- Step 2 ----
        // ---- Step action ----
        // Click on Upload Changes button.
        // ---- Expected results ----
        // The changes are uploaded. The file is still opened for editing.
        appExcel2011.waitForWindow(xlsCommonFile.getName());
        appExcel2011.edit("edited from excel");
        appExcel2011.setFileName(xlsCommonFile.getName());
        appExcel2011.save();

        // ---- Step 3 ----
        // ---- Step action ----
        // Verify the document library of the site in the Share.
        // ---- Expected results ----
        // The changes are applied. A new minor version is created. The document is still locked for editing.

        docDetailsPage.getDrone().refresh();
        docDetailsPage.getDrone().getCurrentPage().render();
        boolean isLocked = docDetailsPage.isCheckedOut();
        Assert.assertTrue(isLocked, "File is locked after is was opened localy from Excel");

        String currentVersion = docDetailsPage.getCurrentVersionDetails().getVersionNumber();
        Assert.assertNotEquals(oldVersion, currentVersion);

        appExcel2011.exitApplication();
        ShareUser.logout(drone);
    }

    /**
     * Preconditions
     * <ul>
     * <li>Any site is created in Share;</li>
     * <li>Any MS Excel document is uploaded to the site's document library;</li>
     * <li>MS Document Connection is opened;</li>
     * <li>A sharepoint connection to Alfresco is created;</li>
     * <li>Site Document Library is opened.</li>
     * <li>The document is opened for editing.</li>
     * <li>Any data is entered into the opened document.</li>
     * </ul>
     */
    @Test(groups = "Enterprise4.2", description = "Edit document - Upload File")
    public void AONE_9765() throws Exception
    {

        appExcel2011.openApplication();
        appExcel2011.edit("some test data");
        appExcel2011.saveAs(xls9765TestFile);
        appExcel2011.exitApplication();

        openCleanMDCtool(testSiteName, testUser, DEFAULT_PASSWORD);

        // ---- Step 1 ----
        // ---- Step action ----
        // Choose the document and click on Upload button.
        // ---- Expected results ----
        // Upload Changes window is displayed.
        appExcel2011.getMDC().addFile(xls9765TestFile);

        // {Paul: cannot use Open URL, Appliction will crash}
        // appExcel2011.openURL(getVTIDocumentLibraryFilePath(testSiteName, xlsCommonFile.getName()));

        // ---- Step 2 ----
        // ---- Step action ----
        // Click on Upload File button.
        // ---- Expected results ----
        // The changes are uploaded. The file is still opened for editing.
        openDocumentLibraryForTest();

        // ---- Step 3 ----
        // ---- Step action ----
        // Verify the document library of the site in the Share.
        // ---- Expected results ----
        // The changes are applied. A new minor version is created. The document is still locked for editing.
        Assert.assertEquals(DocumentLibraryUtil.getDocumentProperties(documentLibraryPage, xls9765TestFile.getName()).size(), 10);

        ShareUser.logout(drone);
    }

    @Test(groups = "Enterprise4.2", description = "Check Out")
    public void AONE_9766() throws Exception
    {
        openCleanMDCtool(testSiteName, testUser, DEFAULT_PASSWORD);

        // ---- Step 1 ----
        // ---- Step action ----
        // Choose the document and click on Check Out button.
        // ---- Expected results ----
        // The document is checked out and is opened for editing in a write mode in the default MS Excel app.
        
        appExcel2011.getMDC().checkOutFile(xlsCommonFile.getName());
        openDocumentLibraryForTest();
        appExcel2011.waitForWindow(xlsCommonFile.getName());
        appExcel2011.exitApplication();
        
        // ---- Step 2 ----
        // ---- Step action ----
        // Verify the document library of the site in the Share.
        // ---- Expected results ----
        // The document is locked. "The document is locked by you for offline editing" message is displayed.
        Assert.assertEquals(ShareUserSitePage.getFileDirectoryInfo(drone, xlsCommonFile.getName()).getContentInfo(),
                "This document is locked by you for offline editing.");
    }

}
