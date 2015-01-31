package org.alfresco.share.enterprise.repository.fileprotocols.cifs.clientwindows.msoffice2013;

import java.awt.AWTException;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.alfresco.application.windows.MicrosoftOffice2013;
import org.alfresco.explorer.WindowsExplorer;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.CifsUtil;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.utilities.Application;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.cobra.ldtp.Ldtp;

@Listeners(FailedTestListener.class)
public class CifsMSPPoint2013Tests extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(CifsMSPPoint2013Tests.class);

    private String testName;
    private String testUser;
    private String siteName;
    private String pptxFileType = ".pptx";
    String fileName_6295;
    String fileName_6296;
    String fileName_6297;
    String fileName_6298;
    String fileName_6299;
    String fileName_6300;

    int nrFilesBeforeOpen = 0;
    int nrFilesAfterClose = 0;

    String image_1 = DATA_FOLDER + CIFS_LOCATION + SLASH + "CifsPic1.jpg";
    String image_2 = DATA_FOLDER + CIFS_LOCATION + SLASH + "CifsPic2.jpg";
    String image_3 = DATA_FOLDER + CIFS_LOCATION + SLASH + "CifsPic3.jpg";

    private static DocumentLibraryPage documentLibPage;
    private static final String CIFS_LOCATION = "cifs";
    public String officePath;
    WindowsExplorer explorer = new WindowsExplorer();
    MicrosoftOffice2013 power = new MicrosoftOffice2013(Application.POWERPOINT, "2013");
    String mapConnect;
    String networkDrive;
    String networkPath;
    String cifsPath;

    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {

        super.setup();

        testName = this.getClass().getSimpleName();
        testUser = getUserNameFreeDomain(testName);
        cifsPath = power.getCIFSPath();
        networkDrive = power.getMapDriver();
        networkPath = power.getMapPath();

        try
        {
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        }
        catch (SkipException e)
        {
            // create user
            logger.info("Creating user " + testUser);
            String[] testUser1 = new String[] { testUser };
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);
        }

        // power point files
        fileName_6295 = "AONE-6295";
        fileName_6296 = "AONE-6296";
        fileName_6297 = "AONE-6297";
        fileName_6298 = "AONE-6298";
        fileName_6299 = "AONE-6299";
        fileName_6300 = "AONE-6300";

        mapConnect = "cmd /c start /WAIT net use" + " " + networkDrive + " " + networkPath + " " + "/user:admin admin";
        Runtime.getRuntime().exec(mapConnect);
        if (CifsUtil.checkDirOrFileExists(7, 200, networkDrive + cifsPath))
        {
            logger.info("----------Mapping succesfull " + testUser);
        }
        else
        {
            logger.error("----------Mapping was not done " + testUser);
        }

        super.tearDown();
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() throws Exception
    {
        super.setup();

    }

    @AfterMethod(alwaysRun = true)
    public void teardownMethod() throws Exception
    {
        Runtime.getRuntime().exec("taskkill /F /IM CobraWinLDTP.EXE");
        Runtime.getRuntime().exec("taskkill /F /IM POWERPNT.EXE");
        super.tearDown();
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() throws IOException
    {

        Runtime.getRuntime().exec("cmd /c start /WAIT net use * /d /y");

        if (CifsUtil.checkDirOrFileNotExists(7, 200, networkDrive + cifsPath))
        {
            logger.info("--------Unmapping succesfull " + testUser);
        }
        else
        {
            logger.error("--------Unmapping was not done correctly " + testUser);
        }

    }

    @Test(groups = { "DataPrepPowerPoint" })
    public void dataPrep_6295() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        File file = new File(DATA_FOLDER + CIFS_LOCATION + SLASH + fileName_6295 + pptxFileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        DocumentDetailsPage detailsPage = documentLibPage.selectFile(fileName_6295 + pptxFileType);

        // Click "Edit Properties" in Actions section;
        EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
        editPropertiesPage.setDocumentTitle(testName);

        detailsPage = editPropertiesPage.selectSave().render();
        detailsPage.render();
    }

    /** AONE-6295:MS PowerPoint 2013 - uploaded to Share */
    @Test(groups = { "CIFSWindowsClient", "EnterpriseOnly" })
    public void AONE_6295() throws IOException
    {
        String testName = getTestName();
        String siteName = getSiteName(testName).toLowerCase();
        String first_modification = testName + "1";
        String second_modification = testName + "2";
        String last_modification = testName + "3";

        String fullPath = networkDrive + cifsPath + siteName.toLowerCase() + SLASH + "documentLibrary" + SLASH;

        // ---- Step 1 ----
        // ---- Step Action -----
        // Open .pptx document for editing.
        // The document is opened in write mode.

        Ldtp ldtp = power.openFileFromCMD(fullPath, fileName_6295 + pptxFileType, testUser, DEFAULT_PASSWORD, false);
        // ---- Step 2 ----
        // ---- Step Action -----
        // Add any data.
        // Expected Result
        // The data is entered.
        ldtp = power.getAbstractUtil().setOnWindow(fileName_6295);
        ldtp.click("paneSlide");
        power.editOffice(ldtp, " " + first_modification + " ");

        // ---- Step 3 ----
        // ---- Step Action -----
        // Save the document (Ctrl+S, for example) and close it
        // Expected Result
        // The document is saved. No errors occur in UI and in the log. No tmp files are left.
        power.saveOffice(ldtp);

        power.exitOfficeApplication(ldtp, fileName_6295);

        Assert.assertTrue(CifsUtil.checkTemporaryFileDoesntExists(fullPath, pptxFileType, 6));

        // ---- Step 4 ----
        // ---- Step Action -----
        // Verify the document's metadata and version history in the Share.
        // Expected Result
        // The document's metadata and version history are not broken. They are displayed correctly.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(fileName_6295 + pptxFileType);

        EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
        Assert.assertTrue(editPropertiesPage.getName().equals(fileName_6295 + pptxFileType));
        editPropertiesPage.clickCancel();

        // ---- Step 5 ----
        // ---- Step Action -----
        // Verify the document's content.
        // Expected Result
        // All changes are present and displayed correctly.
        String body = detailsPage.getDocumentBody().replaceAll("\\n", "");
        Assert.assertTrue(body.contains(first_modification));

        // ---- Step 6 ----
        // ---- Step Action -----
        // 6. Open the document for editing again.
        // Expected Result
        // 6. The document is opened in write mode.

        ldtp = power.openFileFromCMD(fullPath, fileName_6295 + pptxFileType, testUser, DEFAULT_PASSWORD, false);
        power.getAbstractUtil().waitForWindow(fileName_6295);
        // ---- Step 7 ----
        // ---- Step Action -----
        // Add any data.
        // Expected Result
        // The data is entered.
        ldtp = power.getAbstractUtil().setOnWindow(fileName_6295);
        ldtp.click("paneSlide");
        power.editOffice(ldtp, " " + second_modification + " ");

        // ---- Step 8 ----
        // ---- Step Action -----
        // Verify the document's content.
        // Expected Result
        // All changes are present and displayed correctly
        power.saveOffice(ldtp);

        power.exitOfficeApplication(ldtp, fileName_6295);

        Assert.assertTrue(CifsUtil.checkTemporaryFileDoesntExists(fullPath, pptxFileType, 6));

        // ---- Step 9 ----
        // ---- Step Action -----
        // Verify the document's metadata and version history in the Share.
        // Expected Result
        // The document's metadata and version history are not broken. They are displayed correctly.
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        detailsPage = documentLibPage.selectFile(fileName_6295 + pptxFileType);

        editPropertiesPage = detailsPage.selectEditProperties().render();
        Assert.assertTrue(editPropertiesPage.getName().equals(fileName_6295 + pptxFileType));
        editPropertiesPage.clickCancel();

        // ---- Step 10 ----
        // ---- Step Action -----
        // Verify the document's content.
        // Expected Result
        // All changes are present and displayed correctly.
        detailsPage.render();
        String body2 = detailsPage.getDocumentBody().replaceAll("\\n", "");

        Assert.assertTrue(body2.contains(second_modification));

        // ---- Step 11 ----
        // ---- Step Action -----
        // 6. Open the document for editing again.
        // Expected Result
        // 6. The document is opened in write mode.
        ldtp = power.openFileFromCMD(fullPath, fileName_6295 + pptxFileType, testUser, DEFAULT_PASSWORD, false);
        power.getAbstractUtil().waitForWindow(fileName_6295);
        // ---- Step 12 ----
        // ---- Step Action -----
        // Add any data.
        // Expected Result
        // The data is entered.
        ldtp = power.getAbstractUtil().setOnWindow(fileName_6295);
        ldtp.click("paneSlide");
        power.editOffice(ldtp, " " + last_modification + " ");

        // ---- Step 13 ----
        // ---- Step Action -----
        // Verify the document's content.
        // Expected Result
        // All changes are present and displayed correctly
        power.saveOffice(ldtp);

        power.exitOfficeApplication(ldtp, fileName_6295);

        Assert.assertTrue(CifsUtil.checkTemporaryFileDoesntExists(fullPath, pptxFileType, 6));

        // ---- Step 14 ----
        // ---- Step Action -----
        // Verify the document's metadata and version history in the Share.
        // Expected Result
        // The document's metadata and version history are not broken. They are displayed correctly.
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        detailsPage = documentLibPage.selectFile(fileName_6295 + pptxFileType);

        editPropertiesPage = detailsPage.selectEditProperties().render();
        Assert.assertTrue(editPropertiesPage.getName().equals(fileName_6295 + pptxFileType));
        editPropertiesPage.clickCancel();

        // ---- Step 15 ----
        // ---- Step Action -----
        // Verify the document's content.
        // Expected Result
        // All changes are present and displayed correctly.
        String body3 = detailsPage.getDocumentBody().replaceAll("\\n", "");
        Assert.assertTrue(body3.contains(last_modification));

    }

    @Test(groups = { "DataPrepPowerPoint" })
    public void dataPrep_6296() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        File file = new File(DATA_FOLDER + CIFS_LOCATION + SLASH + fileName_6296 + pptxFileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        DocumentDetailsPage detailsPage = documentLibPage.selectFile(fileName_6296 + pptxFileType);

        // Click "Edit Properties" in Actions section;
        EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
        editPropertiesPage.setDocumentTitle(testName);

        detailsPage = editPropertiesPage.selectSave().render();
        detailsPage.render();
    }

    /**
     * AONE-6296:MS PowerPoint 2013 - uploaded to Share (big)
     * 
     * @throws AWTException
     */
    @Test(groups = { "CIFSWindowsClient", "EnterpriseOnly" })
    public void AONE_6296() throws IOException, AWTException
    {
        String testName = getTestName();
        String siteName = getSiteName(testName).toLowerCase();
        String first_modification = testName + "1";
        String second_modification = testName + "2";
        String last_modification = testName + "3";

        String fullPath = networkDrive + cifsPath + siteName.toLowerCase() + SLASH + "documentLibrary" + SLASH;

        // ---- Step 1 ----
        // ---- Step Action -----
        // Open .pptx document for editing.
        // The document is opened in write mode.
        Ldtp ldtp = power.openFileFromCMD(fullPath, fileName_6296 + pptxFileType, testUser, DEFAULT_PASSWORD, false);
        // ---- Step 2 ----
        // ---- Step Action -----
        // Add any data.doc
        // Expected Result
        // The data is entered.
        ldtp = power.getAbstractUtil().setOnWindow(fileName_6296);
        ldtp.click("paneSlide");
        CifsUtil.uploadImageInOffice(image_1);
        ldtp.waitTime(2);
        ldtp.click("paneSlide");
        power.editOffice(ldtp, " " + first_modification + " ");

        // ---- Step 3 ----
        // ---- Step Action -----
        // Save the document (Ctrl+S, for example) and close it
        // Expected Result
        // The document is saved. No errors occur in UI and in the log. No
        // tmp files are left.
        power.saveOffice(ldtp);
        power.getAbstractUtil().setOnWindow(fileName_6296);
        power.exitOfficeApplication(ldtp, fileName_6296);

        Assert.assertTrue(CifsUtil.checkTemporaryFileDoesntExists(fullPath, pptxFileType, 6));

        // ---- Step 4 ----
        // ---- Step Action -----
        // Verify the document's metadata and version history in the Share.
        // Expected Result
        // The document's metadata and version history are not broken. They
        // are displayed correctly.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(fileName_6296 + pptxFileType);

        EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
        editPropertiesPage.getDocumentTitle();
        Assert.assertTrue(editPropertiesPage.getDocumentTitle().equals(testName));
        editPropertiesPage.clickCancel();

        // ---- Step 5 ----
        // ---- Step Action -----
        // Verify the document's content.
        // Expected Result
        // All changes are present and displayed correctly.
        String body = detailsPage.getDocumentBody().replaceAll("\\n", "");
        Assert.assertTrue(body.contains(first_modification));

        // ---- Step 6 ----
        // ---- Step Action -----
        // 6. Open the document for editing again.
        // Expected Result
        // 6. The document is opened in write mode.
        ldtp = power.openFileFromCMD(fullPath, fileName_6296 + pptxFileType, testUser, DEFAULT_PASSWORD, false);
        // ---- Step 7 ----
        // ---- Step Action -----
        // Add any data.
        // Expected Result
        // The data is entered.
        ldtp = power.getAbstractUtil().setOnWindow(fileName_6296);
        ldtp.click("btnNextSlide");
        ldtp.waitTime(1);
        ldtp.click("paneSlide");
        CifsUtil.uploadImageInOffice(image_2);
        ldtp.waitTime(2);
        ldtp.click("paneSlide");
        power.editOffice(ldtp, " " + second_modification + " ");

        // ---- Step 8 ----
        // ---- Step Action -----
        // Verify the document's content.
        // Expected Result
        // All changes are present and displayed correctly
        power.saveOffice(ldtp);

        power.exitOfficeApplication(ldtp, fileName_6296);

        Assert.assertTrue(CifsUtil.checkTemporaryFileDoesntExists(fullPath, pptxFileType, 6));

        // ---- Step 9 ----
        // ---- Step Action -----
        // Verify the document's metadata and version history in the Share.
        // Expected Result
        // The document's metadata and version history are not broken. They
        // are displayed correctly.
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        detailsPage = documentLibPage.selectFile(fileName_6296 + pptxFileType);

        editPropertiesPage = detailsPage.selectEditProperties().render();
        editPropertiesPage.getDocumentTitle();
        Assert.assertTrue(editPropertiesPage.getDocumentTitle().equals(testName));
        editPropertiesPage.clickCancel();

        // ---- Step 10 ----
        // ---- Step Action -----
        // Verify the document's content.
        // Expected Result
        // All changes are present and displayed correctly.
        body = detailsPage.getDocumentBody().replaceAll("\\n", "");
        Assert.assertTrue(body.contains(second_modification));

        // ---- Step 11 ----
        // ---- Step Action -----
        // 6. Open the document for editing again.
        // Expected Result
        // 6. The document is opened in write mode.
        ldtp = power.openFileFromCMD(fullPath, fileName_6296 + pptxFileType, testUser, DEFAULT_PASSWORD, false);
        // ---- Step 12 ----
        // ---- Step Action -----
        // Add any data.
        // Expected Result
        // The data is entered.
        ldtp = power.getAbstractUtil().setOnWindow(fileName_6296);
        ldtp.click("btnNextSlide");
        ldtp.waitTime(1);
        ldtp.click("btnNextSlide");
        ldtp.waitTime(1);
        ldtp.click("paneSlide");
        CifsUtil.uploadImageInOffice(image_3);
        ldtp.waitTime(2);
        ldtp.click("paneSlide");
        power.editOffice(ldtp, " " + last_modification + " ");

        // ---- Step 13 ----
        // ---- Step Action -----
        // Verify the document's content.
        // Expected Result
        // All changes are present and displayed correctly
        power.saveOffice(ldtp);

        power.exitOfficeApplication(ldtp, fileName_6296);

        Assert.assertTrue(CifsUtil.checkTemporaryFileDoesntExists(fullPath, pptxFileType, 6));

        // ---- Step 14 ----
        // ---- Step Action -----
        // Verify the document's metadata and version history in the Share.
        // Expected Result
        // The document's metadata and version history are not broken. They
        // are displayed correctly.
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        detailsPage = documentLibPage.selectFile(fileName_6296 + pptxFileType);

        editPropertiesPage = detailsPage.selectEditProperties().render();
        Assert.assertTrue(editPropertiesPage.getName().equals(fileName_6296 + pptxFileType));
        editPropertiesPage.clickCancel();

        // ---- Step 15 ----
        // ---- Step Action -----
        // Verify the document's content.
        // Expected Result
        // All changes are present and displayed correctly.
        body = detailsPage.getDocumentBody().replaceAll("\\n", "");
        Assert.assertTrue(body.contains(last_modification));

    }

    @Test(groups = { "DataPrepPowerPoint" })
    public void dataPrep_6297() throws Exception
    {

        testName = getTestName();
        siteName = getSiteName(testName);

        ShareUser.login(drone, testUser);

        // --- Step 2 ---
        // --- Step action ---
        // Any site is created;
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render();

        // --- Step 3 ---
        // --- Step action ---
        // CIFS is mapped as a network drive as a site manager user; -- already
        // performed manually
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

    }

    @Test(groups = { "DataPrepPowerPoint" })
    public void dataPrep_6298() throws Exception
    {

        testName = getTestName();
        siteName = getSiteName(testName);

        ShareUser.login(drone, testUser);

        // --- Step 2 ---
        // --- Step action ---
        // Any site is created;
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render();

        // --- Step 3 ---
        // --- Step action ---
        // CIFS is mapped as a network drive as a site manager user; -- already
        // performed manually
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

    }

    @Test(groups = { "DataPrepPowerPoint" })
    public void dataPrep_6299() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);

        ShareUser.login(drone, testUser);

        // --- Step 2 ---
        // --- Step action ---
        // Any site is created;
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render();

        // --- Step 3 ---
        // --- Step action ---
        // CIFS is mapped as a network drive as a site manager user; -- already
        // performed manually
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

    }

    @Test(groups = { "DataPrepPowerPoint" })
    public void dataPrep_6300() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);

        ShareUser.login(drone, testUser);

        // --- Step 2 ---
        // --- Step action ---
        // Any site is created;
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render();

        // --- Step 3 ---
        // --- Step action ---
        // CIFS is mapped as a network drive as a site manager user; -- already
        // performed manually
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

    }

    /** AONE-6297:MS PowerPoint 2013 - created via context menu */
    @Test(groups = { "CIFSWindowsClient", "EnterpriseOnly" })
    public void AONE_6297() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName).toLowerCase();
        String addText = "First text";
        String edit1 = "New text1";
        String edit2 = "New text2";
        String edit3 = "New text3";
        String fullPath = networkDrive + cifsPath + siteName.toLowerCase() + SLASH + "documentLibrary" + SLASH;

        Ldtp l1;

        // --- Step 1 ---
        // --- Step action ---
        // RBC in the window -> New -> New Microsoft Office Word Document
        // --- Expected results --
        // The document .docx is created

        l1 = power.openOfficeApplication();
        power.editOffice(l1, addText);
        power.saveAsOffice(l1, fullPath + fileName_6297);

        power.getAbstractUtil().waitForWindow(fileName_6297);
        power.exitOfficeApplication(l1, fileName_6297);

        // ---- Step 2 ----
        // ---- Step Action -----
        // Open .docx document for editing.
        // ---- Expected Result -----
        // The document is opened in write mode.
        l1 = power.openFileFromCMD(fullPath, fileName_6297 + pptxFileType, testUser, DEFAULT_PASSWORD, false);
        // ---- Step 3 ----
        // ---- Step Action -----
        // Add any data.
        // ---- Expected Result -----
        // The data is entered.
        l1 = power.getAbstractUtil().setOnWindow(fileName_6297);
        l1.click("btnNewSlide");
        power.editOffice(l1, " " + edit1 + " ");

        // --- Step 4 ---
        // --- Step action ---
        // Save the document (Ctrl+S, for example) and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.
        power.saveOffice(l1);

        power.exitOfficeApplication(l1, fileName_6297);
        Assert.assertTrue(CifsUtil.checkTemporaryFileDoesntExists(fullPath, pptxFileType, 6));

        // ---- Step 5 ----
        // ---- Step Action -----
        // Verify the document's content.
        // Expected Result
        // All changes are present and displayed correctly.
        ShareUser.login(drone, testUser);
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(fileName_6297 + pptxFileType);

        EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
        Assert.assertTrue(editPropertiesPage.getName().equals(fileName_6297 + pptxFileType));
        editPropertiesPage.clickCancel();
        String body = detailsPage.getDocumentBody().replaceAll("\\n", "");
        Assert.assertTrue(body.contains(edit1));

        // --- Step 6 ---
        // --- Step action ---
        // Specify the document's metadata, e.g. title, description and so on,
        // and create some version history (i.e. add several new versions (minor
        // and major))
        // via the Share.
        // --- Expected results --
        // The document's metadata and version history are specified.
        EditDocumentPropertiesPage editDocPropPage = detailsPage.selectEditProperties();
        String documentTitle = "Title " + fileName_6297;
        String documentDescription = "Description for " + fileName_6297;
        editDocPropPage.setDocumentTitle(documentTitle);
        editDocPropPage.setDescription(documentDescription);
        detailsPage = editDocPropPage.selectSave().render();

        String newFileVersion2 = CIFS_LOCATION + SLASH + "SaveCIFSv2.pptx";
        String newFileVersion3 = CIFS_LOCATION + SLASH + "SaveCIFSv3.pptx";
        detailsPage = ShareUser.uploadNewVersionOfDocument(drone, newFileVersion2, "", true).render();
        detailsPage = ShareUser.uploadNewVersionOfDocument(drone, newFileVersion3, "", true).render();

        Map<String, Object> propertiesValues = detailsPage.getProperties();
        Assert.assertEquals(propertiesValues.get("Title").toString(), documentTitle, "Document title is not " + documentTitle);
        Assert.assertEquals(propertiesValues.get("Description").toString(), documentDescription, "Document description is not " + documentDescription);
        Assert.assertTrue(detailsPage.isVersionHistoryPanelPresent());
        Assert.assertTrue(detailsPage.isVersionPresentInVersionHistoryPanel("1.0") && detailsPage.isVersionPresentInVersionHistoryPanel("2.0")
                && detailsPage.isVersionPresentInVersionHistoryPanel("3.0"));

        // ---- Step 7 ----
        // ---- Step Action -----
        // Open .docx document for editing.
        // ---- Expected Result -----
        // The document is opened in write mode.
        l1 = power.openFileFromCMD(fullPath, fileName_6297 + pptxFileType, testUser, DEFAULT_PASSWORD, false);
        // ---- Step 8 ----
        // ---- Step Action -----
        // Add any data.
        // ---- Expected Result -----
        // The data is entered.
        l1 = power.getAbstractUtil().setOnWindow(fileName_6297);
        power.editOffice(l1, " " + edit2 + " ");

        // --- Step 9 ---
        // --- Step action ---
        // Save the document (Ctrl+S, for example) and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.
        power.saveOffice(l1);

        power.exitOfficeApplication(l1, fileName_6297);

        Assert.assertTrue(CifsUtil.checkTemporaryFileDoesntExists(fullPath, pptxFileType, 6));
        // --- Step 10 ---
        // --- Step action ---
        // Specify the document's metadata, e.g. title, description and so on,
        // and create some version history (i.e. add several new versions (minor
        // and major))
        // via the Share.
        // --- Expected results --
        // The document's metadata and version history are specified.
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        detailsPage = ShareUserSitePage.openDetailsPage(drone, fileName_6297 + pptxFileType).render();

        Map<String, Object> propertiesValues2 = detailsPage.getProperties();
        Assert.assertEquals(propertiesValues2.get("Title").toString(), documentTitle, "Document title is not " + documentTitle);
        Assert.assertEquals(propertiesValues2.get("Description").toString(), documentDescription, "Document description is not " + documentDescription);
        Assert.assertTrue(detailsPage.isVersionHistoryPanelPresent());
        Assert.assertTrue(detailsPage.isVersionPresentInVersionHistoryPanel("1.0") && detailsPage.isVersionPresentInVersionHistoryPanel("2.0")
                && detailsPage.isVersionPresentInVersionHistoryPanel("3.0"));

        // ---- Step 11 ----
        // ---- Step Action -----
        // Verify the document's content.
        // Expected Result
        // All changes are present and displayed correctly.
        body = detailsPage.getDocumentBody().replaceAll("\\n", "");
        Assert.assertTrue(body.contains(edit2));

        // ---- Step 12 ----
        // ---- Step Action -----
        // Open .docx document for editing.
        // ---- Expected Result -----
        // The document is opened in write mode.
        l1 = power.openFileFromCMD(fullPath, fileName_6297 + pptxFileType, testUser, DEFAULT_PASSWORD, false);
        // ---- Step 13 ----
        // ---- Step Action -----
        // Add any data.
        // ---- Expected Result -----
        // The data is entered.
        l1 = power.getAbstractUtil().setOnWindow(fileName_6297);
        l1.click("paneSlide");
        power.editOffice(l1, " " + edit3 + " ");

        // --- Step 14 ---
        // --- Step action ---
        // Save the document (Ctrl+S, for example) and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.
        power.saveOffice(l1);

        power.exitOfficeApplication(l1, fileName_6297);
        Assert.assertTrue(CifsUtil.checkTemporaryFileDoesntExists(fullPath, pptxFileType, 6));

        // --- Step 15 ---
        // --- Step action ---
        // Specify the document's metadata, e.g. title, description and so on,
        // and create some version history (i.e. add several new versions (minor
        // and major))
        // via the Share.
        // --- Expected results --
        // The document's metadata and version history are specified.
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        detailsPage = ShareUserSitePage.openDetailsPage(drone, fileName_6297 + pptxFileType).render();

        Map<String, Object> propertiesValues3 = detailsPage.getProperties();
        Assert.assertEquals(propertiesValues3.get("Title").toString(), documentTitle, "Document title is not " + documentTitle);
        Assert.assertEquals(propertiesValues3.get("Description").toString(), documentDescription, "Document description is not " + documentDescription);
        Assert.assertTrue(detailsPage.isVersionHistoryPanelPresent());
        Assert.assertTrue(detailsPage.isVersionPresentInVersionHistoryPanel("1.0") && detailsPage.isVersionPresentInVersionHistoryPanel("2.0")
                && detailsPage.isVersionPresentInVersionHistoryPanel("3.0"));

        // ---- Step 16 ----
        // ---- Step Action -----
        // Verify the document's content.
        // Expected Result
        // All changes are present and displayed correctly.
        body = detailsPage.getDocumentBody().replaceAll("\\n", "");
        Assert.assertTrue(body.contains(edit3));

    }

    /** AONE-6298:MS PowerPoint 2013 - created via context menu (big) */
    @Test(groups = { "CIFSWindowsClient", "EnterpriseOnly" })
    public void AONE_6298() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName).toLowerCase();
        String addText = "First text";
        String edit1 = "New text1";
        String edit2 = "New text2";
        String edit3 = "New text3";
        String fullPath = networkDrive + cifsPath + siteName.toLowerCase() + SLASH + "documentLibrary" + SLASH;

        Ldtp l1;

        // --- Step 1 ---
        // --- Step action ---
        // RBC in the window -> New -> New Microsoft Office Word Document
        // --- Expected results --
        // The document .docx is created

        l1 = power.openOfficeApplication();
        power.editOffice(l1, addText);
        power.saveAsOffice(l1, fullPath + fileName_6298);

        power.getAbstractUtil().waitForWindow(fileName_6298);
        power.exitOfficeApplication(l1, fileName_6298);

        // ---- Step 2 ----
        // ---- Step Action -----
        // Open .docx document for editing.
        // ---- Expected Result -----
        // The document is opened in write mode.
        l1 = power.openFileFromCMD(fullPath, fileName_6298 + pptxFileType, testUser, DEFAULT_PASSWORD, false);
        // ---- Step 3 ----
        // ---- Step Action -----
        // Add any data (5-10 mb).
        // ---- Expected Result -----
        // The data is entered.
        l1 = power.getAbstractUtil().setOnWindow(fileName_6298);
        l1.click("btnNewSlide");
        power.editOffice(l1, " " + edit1 + " ");
        l1.click("btnNewSlide");
        CifsUtil.uploadImageInOffice(image_1);

        // --- Step 4 ---
        // --- Step action ---
        // Save the document (Ctrl+S, for example) and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.
        power.saveOffice(l1);

        power.exitOfficeApplication(l1, fileName_6298);
        Assert.assertTrue(CifsUtil.checkTemporaryFileDoesntExists(fullPath, pptxFileType, 6));

        // ---- Step 5 ----
        // ---- Step Action -----
        // Verify the document's content.
        // Expected Result
        // All changes are present and displayed correctly.
        ShareUser.login(drone, testUser);
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(fileName_6298 + pptxFileType);

        EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
        Assert.assertTrue(editPropertiesPage.getName().equals(fileName_6298 + pptxFileType));
        editPropertiesPage.clickCancel();
        String body = detailsPage.getDocumentBody().replaceAll("\\n", "");
        Assert.assertTrue(body.contains(edit1));

        // --- Step 6 ---
        // --- Step action ---
        // Specify the document's metadata, e.g. title, description and so on,
        // and create some version history (i.e. add several new versions (minor
        // and major))
        // via the Share.
        // --- Expected results --
        // The document's metadata and version history are specified.
        EditDocumentPropertiesPage editDocPropPage = detailsPage.selectEditProperties();
        String documentTitle = "Title " + fileName_6298;
        String documentDescription = "Description for " + fileName_6298;
        editDocPropPage.setDocumentTitle(documentTitle);
        editDocPropPage.setDescription(documentDescription);
        detailsPage = editDocPropPage.selectSave().render();

        String newFileVersion2 = CIFS_LOCATION + SLASH + "SaveCIFSv2.pptx";
        String newFileVersion3 = CIFS_LOCATION + SLASH + "SaveCIFSv3.pptx";
        detailsPage = ShareUser.uploadNewVersionOfDocument(drone, newFileVersion2, "", true).render();
        detailsPage = ShareUser.uploadNewVersionOfDocument(drone, newFileVersion3, "", true).render();

        Map<String, Object> propertiesValues = detailsPage.getProperties();
        Assert.assertEquals(propertiesValues.get("Title").toString(), documentTitle, "Document title is not " + documentTitle);
        Assert.assertEquals(propertiesValues.get("Description").toString(), documentDescription, "Document description is not " + documentDescription);
        Assert.assertTrue(detailsPage.isVersionHistoryPanelPresent());
        Assert.assertTrue(detailsPage.isVersionPresentInVersionHistoryPanel("1.0") && detailsPage.isVersionPresentInVersionHistoryPanel("2.0")
                && detailsPage.isVersionPresentInVersionHistoryPanel("3.0"));

        // ---- Step 7 ----
        // ---- Step Action -----
        // Open .docx document for editing.
        // ---- Expected Result -----
        // The document is opened in write mode.
        l1 = power.openFileFromCMD(fullPath, fileName_6298 + pptxFileType, testUser, DEFAULT_PASSWORD, false);
        l1 = power.getAbstractUtil().setOnWindow(fileName_6298);

        // ---- Step 8 ----
        // ---- Step Action -----
        // Add any data (5-10 mb).
        // ---- Expected Result -----
        // The data is entered.
        l1.click("paneSlide");
        power.editOffice(l1, " " + edit2 + " ");
        l1.click("btnNewSlide");
        CifsUtil.uploadImageInOffice(image_2);

        // --- Step 9 ---
        // --- Step action ---
        // Save the document (Ctrl+S, for example) and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.
        power.saveOffice(l1);

        power.exitOfficeApplication(l1, fileName_6298);
        Assert.assertTrue(CifsUtil.checkTemporaryFileDoesntExists(fullPath, pptxFileType, 6));

        // --- Step 10 ---
        // --- Step action ---
        // Specify the document's metadata, e.g. title, description and so on,
        // and create some version history (i.e. add several new versions (minor
        // and major))
        // via the Share.
        // --- Expected results --
        // The document's metadata and version history are specified.
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        detailsPage = ShareUserSitePage.openDetailsPage(drone, fileName_6298 + pptxFileType).render();

        Map<String, Object> propertiesValues2 = detailsPage.getProperties();
        Assert.assertEquals(propertiesValues2.get("Title").toString(), documentTitle, "Document title is not " + documentTitle);
        Assert.assertEquals(propertiesValues2.get("Description").toString(), documentDescription, "Document description is not " + documentDescription);
        Assert.assertTrue(detailsPage.isVersionHistoryPanelPresent());
        Assert.assertTrue(detailsPage.isVersionPresentInVersionHistoryPanel("1.0") && detailsPage.isVersionPresentInVersionHistoryPanel("2.0")
                && detailsPage.isVersionPresentInVersionHistoryPanel("3.0"));

        // ---- Step 11 ----
        // ---- Step Action -----
        // Verify the document's content.
        // Expected Result
        // All changes are present and displayed correctly.
        body = detailsPage.getDocumentBody().replaceAll("\\n", "");
        Assert.assertTrue(body.contains(edit2));

        // ---- Step 12 ----
        // ---- Step Action -----
        // Open .docx document for editing.
        // ---- Expected Result -----
        // The document is opened in write mode.
        l1 = power.openFileFromCMD(fullPath, fileName_6298 + pptxFileType, testUser, DEFAULT_PASSWORD, false);
        power.getAbstractUtil().waitForWindow(fileName_6298);
        l1 = power.getAbstractUtil().setOnWindow(fileName_6298);

        // ---- Step 13 ----
        // ---- Step Action -----
        // Add any data (5-10 mb).
        // ---- Expected Result -----
        // The data is entered.
        l1.click("btnNextSlide");
        l1.waitTime(2);
        l1.click("paneSlide");
        power.editOffice(l1, " " + edit3 + " ");
        l1.click("btnNewSlide");
        CifsUtil.uploadImageInOffice(image_3);

        // --- Step 14 ---
        // --- Step action ---
        // Save the document (Ctrl+S, for example) and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.
        power.saveOffice(l1);

        power.exitOfficeApplication(l1, fileName_6298);
        Assert.assertTrue(CifsUtil.checkTemporaryFileDoesntExists(fullPath, pptxFileType, 6));

        // --- Step 15 ---
        // --- Step action ---
        // Specify the document's metadata, e.g. title, description and so on,
        // and create some version history (i.e. add several new versions (minor
        // and major))
        // via the Share.
        // --- Expected results --
        // The document's metadata and version history are specified.
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        detailsPage = ShareUserSitePage.openDetailsPage(drone, fileName_6298 + pptxFileType).render();

        Map<String, Object> propertiesValues3 = detailsPage.getProperties();
        Assert.assertEquals(propertiesValues3.get("Title").toString(), documentTitle, "Document title is not " + documentTitle);
        Assert.assertEquals(propertiesValues3.get("Description").toString(), documentDescription, "Document description is not " + documentDescription);
        Assert.assertTrue(detailsPage.isVersionHistoryPanelPresent());
        Assert.assertTrue(detailsPage.isVersionPresentInVersionHistoryPanel("1.0") && detailsPage.isVersionPresentInVersionHistoryPanel("2.0")
                && detailsPage.isVersionPresentInVersionHistoryPanel("3.0"));

        // ---- Step 16 ----
        // ---- Step Action -----
        // Verify the document's content.
        // Expected Result
        // All changes are present and displayed correctly.
        body = detailsPage.getDocumentBody().replaceAll("\\n", "");
        Assert.assertTrue(body.contains(edit3));

    }

    /** AONE-6299:MS PowerPoint 2013 - saved into CIFS */
    @Test(groups = { "CIFSWindowsClient", "EnterpriseOnly" })
    public void AONE_6299() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName).toLowerCase();
        String edit2 = "New text2";
        String edit3 = "New text3";
        String fullPath = networkDrive + cifsPath + siteName.toLowerCase() + SLASH + "documentLibrary" + SLASH;
        String localPath = DATA_FOLDER + CIFS_LOCATION + SLASH;
        Ldtp l1;

        // --- Step 1 ---
        // --- Step action ---
        // Save a document into the CIFS drive (into the Document Library space)
        // and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.

        l1 = power.openFileFromCMD(localPath, fileName_6299 + pptxFileType, testUser, DEFAULT_PASSWORD, false);
        l1 = power.getAbstractUtil().setOnWindow(fileName_6299);
        power.navigateToSaveAsSharePointBrowse(l1);

        power.operateOnSaveAsWithFullPath(l1, fullPath, fileName_6299, testUser, DEFAULT_PASSWORD);
        l1 = power.getAbstractUtil().setOnWindow(fileName_6299);
        // l1.waitTime(4);
        power.exitOfficeApplication(l1, fileName_6299);

        Assert.assertTrue(CifsUtil.checkTemporaryFileDoesntExists(fullPath, pptxFileType, 6));
        // --- Step 2 ---
        // --- Step action ---
        // Verify the document's content.
        // --- Expected results --
        // All changes are present and displayed correctly.
        ShareUser.login(drone, testUser);
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(fileName_6299 + pptxFileType);

        EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
        Assert.assertTrue(editPropertiesPage.getName().equals(fileName_6299 + pptxFileType));
        editPropertiesPage.clickCancel();

        // --- Step 3 ---
        // --- Step action ---
        // Specify the document's metadata, e.g. title, description and so on,
        // and create some version history (i.e. add several new versions (minor
        // and major))
        // via the Share.
        // --- Expected results --
        // The document's metadata and version history are specified.
        EditDocumentPropertiesPage editDocPropPage = detailsPage.selectEditProperties();
        String documentTitle = "Title " + fileName_6299;
        String documentDescription = "Description for " + fileName_6299;
        editDocPropPage.setDocumentTitle(documentTitle);
        editDocPropPage.setDescription(documentDescription);
        detailsPage = editDocPropPage.selectSave().render();

        String newFileVersion2 = CIFS_LOCATION + SLASH + "SaveCIFSv2.pptx";
        String newFileVersion3 = CIFS_LOCATION + SLASH + "SaveCIFSv3.pptx";
        detailsPage = ShareUser.uploadNewVersionOfDocument(drone, newFileVersion2, "", true).render();
        detailsPage = ShareUser.uploadNewVersionOfDocument(drone, newFileVersion3, "", true).render();

        Map<String, Object> propertiesValues = detailsPage.getProperties();
        Assert.assertEquals(propertiesValues.get("Title").toString(), documentTitle, "Document title is not " + documentTitle);
        Assert.assertEquals(propertiesValues.get("Description").toString(), documentDescription, "Document description is not " + documentDescription);
        Assert.assertTrue(detailsPage.isVersionHistoryPanelPresent());
        Assert.assertTrue(detailsPage.isVersionPresentInVersionHistoryPanel("1.0") && detailsPage.isVersionPresentInVersionHistoryPanel("2.0")
                && detailsPage.isVersionPresentInVersionHistoryPanel("3.0"));

        // --- Step 4 ---
        // --- Step action ---
        // Open the document for editing again.
        // --- Expected results --
        // The document is opened in write mode.

        l1 = power.openFileFromCMD(fullPath, fileName_6299 + pptxFileType, testUser, DEFAULT_PASSWORD, false);
        power.getAbstractUtil().waitForWindow(fileName_6299);
        l1 = power.getAbstractUtil().setOnWindow(fileName_6299);
        String actualName = l1.getWindowName();
        Assert.assertTrue(actualName.contains(fileName_6299), "Microsoft Excel - " + fileName_6299 + " window is active.");

        // --- Step 5 ---
        // --- Step action ---
        // Add any data.
        // --- Expected results --
        // The data is entered.
        power.editOffice(l1, " " + edit2 + " ");

        // --- Step 6 ---
        // --- Step action ---
        // Save the document (Ctrl+S, for example) and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.
        power.saveOffice(l1);

        power.exitOfficeApplication(l1, fileName_6299);
        Assert.assertTrue(CifsUtil.checkTemporaryFileDoesntExists(fullPath, pptxFileType, 6));

        // --- Step 7 ---
        // --- Step action ---
        // Verify the document's metadata and version history in the Share.
        // --- Expected results --
        // The document's metadata and version history are not broken. They are
        // displayed correctly..
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        detailsPage = ShareUserSitePage.openDetailsPage(drone, fileName_6299 + pptxFileType).render();

        Map<String, Object> propertiesValues2 = detailsPage.getProperties();
        Assert.assertEquals(propertiesValues2.get("Title").toString(), documentTitle, "Document title is not " + documentTitle);
        Assert.assertEquals(propertiesValues2.get("Description").toString(), documentDescription, "Document description is not " + documentDescription);
        Assert.assertTrue(detailsPage.isVersionHistoryPanelPresent());
        Assert.assertTrue(detailsPage.isVersionPresentInVersionHistoryPanel("1.0") && detailsPage.isVersionPresentInVersionHistoryPanel("2.0")
                && detailsPage.isVersionPresentInVersionHistoryPanel("3.0"));

        // --- Step 8 ---
        // --- Step action ---
        // Verify the document's content.
        // --- Expected results --
        // All changes are present and displayed correctly.
        String body = detailsPage.getDocumentBody().replaceAll("\\n", "");
        Assert.assertTrue(body.contains(edit2));

        // --- Step 9 ---
        // --- Step action ---
        // Open the document for editing again.
        // --- Expected results --
        // The document is opened in write mode.

        l1 = power.openFileFromCMD(fullPath, fileName_6299 + pptxFileType, testUser, DEFAULT_PASSWORD, false);
        power.getAbstractUtil().waitForWindow(fileName_6299);
        l1 = power.getAbstractUtil().setOnWindow(fileName_6299);
        actualName = l1.getWindowName();
        Assert.assertTrue(actualName.contains(fileName_6299), "Microsoft Excel - " + fileName_6299 + " window is active.");

        // --- Step 10 ---
        // --- Step action ---
        // Add any data.
        // --- Expected results --
        // The data is entered.
        l1.click("paneSlide");
        power.editOffice(l1, " " + edit3 + " ");

        // --- Step 11 ---
        // --- Step action ---
        // Save the document (Ctrl+S, for example) and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.
        l1 = power.getAbstractUtil().setOnWindow(fileName_6299);
        power.saveOffice(l1);

        power.exitOfficeApplication(l1, fileName_6299);
        Assert.assertTrue(CifsUtil.checkTemporaryFileDoesntExists(fullPath, pptxFileType, 6));

        // --- Step 12 ---
        // --- Step action ---
        // Verify the document's metadata and version history in the Share.
        // --- Expected results --
        // The document's metadata and version history are not broken. They are
        // displayed correctly..
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        detailsPage = ShareUserSitePage.openDetailsPage(drone, fileName_6299 + pptxFileType).render();

        Map<String, Object> propertiesValues3 = detailsPage.getProperties();
        Assert.assertEquals(propertiesValues3.get("Title").toString(), documentTitle, "Document title is not " + documentTitle);
        Assert.assertEquals(propertiesValues3.get("Description").toString(), documentDescription, "Document description is not " + documentDescription);
        Assert.assertTrue(detailsPage.isVersionHistoryPanelPresent());
        Assert.assertTrue(detailsPage.isVersionPresentInVersionHistoryPanel("1.0") && detailsPage.isVersionPresentInVersionHistoryPanel("2.0")
                && detailsPage.isVersionPresentInVersionHistoryPanel("3.0"));

        // --- Step 13 ---
        // --- Step action ---
        // Verify the document's content.
        // --- Expected results --
        // All changes are present and displayed correctly.
        String body3 = detailsPage.getDocumentBody().replaceAll("\\n", "");
        Assert.assertTrue(body3.contains(edit3));
    }

    /** AONE-6300:MS PowerPoint 2013 - saved into CIFS (big) */
    @Test(groups = { "CIFSWindowsClient", "EnterpriseOnly" })
    public void AONE_6300() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName).toLowerCase();
        String edit2 = "New text2";
        String edit3 = "New text3";
        String fullPath = networkDrive + cifsPath + siteName.toLowerCase() + SLASH + "documentLibrary" + SLASH;
        String localPath = DATA_FOLDER + CIFS_LOCATION + SLASH;
        Ldtp l1;
        // --- Step 1 ---
        // --- Step action ---
        // Save a document into the CIFS drive (into the Document Library space)
        // and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.

        l1 = power.openFileFromCMD(localPath, fileName_6300 + pptxFileType, testUser, DEFAULT_PASSWORD, false);
        power.getAbstractUtil().waitForWindow(fileName_6300);
        l1 = power.getAbstractUtil().setOnWindow(fileName_6300);
        // power.navigateToSaveAsSharePointBrowse(l1);
        //
        // power.operateOnSaveAsWithFullPath(l1, fullPath, fileName_6300, testUser, DEFAULT_PASSWORD);
        // l1 = power.getAbstractUtil().setOnWindow(fileName_6300);
        // // l1.waitTime(4);
        // power.exitOfficeApplication(l1, fileName_6300);

        power.saveAsOffice(l1, fullPath + fileName_6300);

        power.getAbstractUtil().setOnWindow(fileName_6300);
        power.exitOfficeApplication(l1, fileName_6300);

        Assert.assertTrue(CifsUtil.checkTemporaryFileDoesntExists(fullPath, pptxFileType, 6));

        // --- Step 2 ---
        // --- Step action ---
        // Verify the document's content.
        // --- Expected results --
        // All changes are present and displayed correctly.
        ShareUser.login(drone, testUser);
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(fileName_6300 + pptxFileType);

        EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
        Assert.assertTrue(editPropertiesPage.getName().equals(fileName_6300 + pptxFileType));
        editPropertiesPage.clickCancel();

        // --- Step 3 ---
        // --- Step action ---
        // Specify the document's metadata, e.g. title, description and so on,
        // and create some version history (i.e. add several new versions (minor
        // and major))
        // via the Share.
        // --- Expected results --
        // The document's metadata and version history are specified.
        EditDocumentPropertiesPage editDocPropPage = detailsPage.selectEditProperties();
        String documentTitle = "Title " + fileName_6300;
        String documentDescription = "Description for " + fileName_6300;
        editDocPropPage.setDocumentTitle(documentTitle);
        editDocPropPage.setDescription(documentDescription);
        detailsPage = editDocPropPage.selectSave().render();

        String newFileVersion2 = CIFS_LOCATION + SLASH + "SaveCIFSv2.pptx";
        String newFileVersion3 = CIFS_LOCATION + SLASH + "SaveCIFSv3.pptx";
        detailsPage = ShareUser.uploadNewVersionOfDocument(drone, newFileVersion2, "", true).render();
        detailsPage = ShareUser.uploadNewVersionOfDocument(drone, newFileVersion3, "", true).render();

        Map<String, Object> propertiesValues = detailsPage.getProperties();
        Assert.assertEquals(propertiesValues.get("Title").toString(), documentTitle, "Document title is not " + documentTitle);
        Assert.assertEquals(propertiesValues.get("Description").toString(), documentDescription, "Document description is not " + documentDescription);
        Assert.assertTrue(detailsPage.isVersionHistoryPanelPresent());
        Assert.assertTrue(detailsPage.isVersionPresentInVersionHistoryPanel("1.0") && detailsPage.isVersionPresentInVersionHistoryPanel("2.0")
                && detailsPage.isVersionPresentInVersionHistoryPanel("3.0"));

        // --- Step 4 ---
        // --- Step action ---
        // Open the document for editing again.
        // --- Expected results --
        // The document is opened in write mode.

        l1 = power.openFileFromCMD(fullPath, fileName_6300 + pptxFileType, testUser, DEFAULT_PASSWORD, false);
        power.getAbstractUtil().waitForWindow(fileName_6300);
        l1 = power.getAbstractUtil().setOnWindow(fileName_6300);
        String actualName = l1.getWindowName();
        Assert.assertTrue(actualName.contains(fileName_6300), "Microsoft Excel - " + fileName_6300 + " window is active.");

        // --- Step 5 ---
        // --- Step action ---
        // Add any data (5-10 mb).
        // --- Expected results --
        // The data is entered.
        l1.click("paneSlide");
        CifsUtil.uploadImageInOffice(image_1);
        l1.click("paneSlide");
        power.editOffice(l1, " " + edit2 + " ");

        // --- Step 6 ---
        // --- Step action ---
        // Save the document (Ctrl+S, for example) and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.
        power.saveOffice(l1);

        power.exitOfficeApplication(l1, fileName_6300);

        Assert.assertTrue(CifsUtil.checkTemporaryFileDoesntExists(fullPath, pptxFileType, 6));

        // --- Step 7 ---
        // --- Step action ---
        // Verify the document's metadata and version history in the Share.
        // --- Expected results --
        // The document's metadata and version history are not broken. They are
        // displayed correctly..
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        detailsPage = ShareUserSitePage.openDetailsPage(drone, fileName_6300 + pptxFileType).render();

        Map<String, Object> propertiesValues2 = detailsPage.getProperties();
        Assert.assertEquals(propertiesValues2.get("Title").toString(), documentTitle, "Document title is not " + documentTitle);
        Assert.assertEquals(propertiesValues2.get("Description").toString(), documentDescription, "Document description is not " + documentDescription);
        Assert.assertTrue(detailsPage.isVersionHistoryPanelPresent());
        Assert.assertTrue(detailsPage.isVersionPresentInVersionHistoryPanel("1.0") && detailsPage.isVersionPresentInVersionHistoryPanel("2.0")
                && detailsPage.isVersionPresentInVersionHistoryPanel("3.0"));

        // --- Step 8 ---
        // --- Step action ---
        // Verify the document's content.
        // --- Expected results --
        // All changes are present and displayed correctly.
        String body = detailsPage.getDocumentBody().replaceAll("\\n", "");
        Assert.assertTrue(body.contains(edit2));

        // --- Step 9 ---
        // --- Step action ---
        // Open the document for editing again.
        // --- Expected results --
        // The document is opened in write mode.

        l1 = power.openFileFromCMD(fullPath, fileName_6300 + pptxFileType, testUser, DEFAULT_PASSWORD, false);
        l1 = power.getAbstractUtil().setOnWindow(fileName_6300);
        actualName = l1.getWindowName();
        Assert.assertTrue(actualName.contains(fileName_6300), "Microsoft Excel - " + fileName_6300 + " window is active.");

        // --- Step 10 ---
        // --- Step action ---
        // Add any data (5-10 mb).
        // --- Expected results --
        // The data is entered.
        l1.click("btnNextSlide");
        l1.waitTime(2);
        l1.click("paneSlide");
        CifsUtil.uploadImageInOffice(image_2);
        l1.click("paneSlide");
        power.editOffice(l1, " " + edit3 + " ");

        // --- Step 11 ---
        // --- Step action ---
        // Save the document (Ctrl+S, for example) and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.
        power.saveOffice(l1);
        // l1.waitTime(5);
        power.exitOfficeApplication(l1, fileName_6300);

        Assert.assertTrue(CifsUtil.checkTemporaryFileDoesntExists(fullPath, pptxFileType, 6));

        // --- Step 12 ---
        // --- Step action ---
        // Verify the document's metadata and version history in the Share.
        // --- Expected results --
        // The document's metadata and version history are not broken. They are
        // displayed correctly..
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        detailsPage = ShareUserSitePage.openDetailsPage(drone, fileName_6300 + pptxFileType).render();

        Map<String, Object> propertiesValues3 = detailsPage.getProperties();
        Assert.assertEquals(propertiesValues3.get("Title").toString(), documentTitle, "Document title is not " + documentTitle);
        Assert.assertEquals(propertiesValues3.get("Description").toString(), documentDescription, "Document description is not " + documentDescription);
        Assert.assertTrue(detailsPage.isVersionHistoryPanelPresent());
        Assert.assertTrue(detailsPage.isVersionPresentInVersionHistoryPanel("1.0") && detailsPage.isVersionPresentInVersionHistoryPanel("2.0")
                && detailsPage.isVersionPresentInVersionHistoryPanel("3.0"));

        // --- Step 13 ---
        // --- Step action ---
        // Verify the document's content.
        // --- Expected results --
        // All changes are present and displayed correctly.
        String body3 = detailsPage.getDocumentBody().replaceAll("\\n", "");
        Assert.assertTrue(body3.contains(edit3));
    }
}
