package org.alfresco.share.enterprise.repository.fileprotocols.cifs.clientwindows.msoffice2013;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;

import org.alfresco.application.util.Application;
import org.alfresco.explorer.WindowsExplorer;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.CifsUtil;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.alfresco.windows.application.MicrosoftOffice2013;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.cobra.ldtp.Ldtp;

@Listeners(FailedTestListener.class)
public class CifsMSWord2013Tests extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(CifsMSWord2013Tests.class);

    private String testName;
    private String testUser;
    private String siteName;
    private String docxFileType = ".docx";
    String docFileName_6283;
    String docFileName_6284;
    String docxFileName_6285;
    String docxFileName_6286;
    String docxFileName_6287;
    String docxFileName_6288;

    String image_1 = DATA_FOLDER + CIFS_LOCATION + SLASH + "CifsPic1.jpg";
    String image_2 = DATA_FOLDER + CIFS_LOCATION + SLASH + "CifsPic2.jpg";
    String image_3 = DATA_FOLDER + CIFS_LOCATION + SLASH + "CifsPic3.jpg";

    private static DocumentLibraryPage documentLibPage;
    private static final String CIFS_LOCATION = "cifs";
    public String officePath;
    WindowsExplorer explorer = new WindowsExplorer();
    MicrosoftOffice2013 word = new MicrosoftOffice2013(Application.WORD, "2013");
    String mapConnect;
    String networkDrive;
    String networkPath;
    String cifsPath;

    @BeforeClass(alwaysRun = true)
    public void createUser() throws Exception
    {
        super.setup();

        testName = this.getClass().getSimpleName();
        testUser = getUserNameFreeDomain(testName) + 1;
        cifsPath = word.getCIFSPath();
        networkDrive = word.getMapDriver();
        networkPath = word.getMapPath();

        try
        {
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        }
        catch (Exception e)
        {

            // create user
            String[] testUser1 = new String[] { testUser };
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);
        }

        // word files
        docFileName_6283 = "AONE-6283";
        docFileName_6284 = "AONE-6284";
        docxFileName_6285 = "AONE-6285";
        docxFileName_6286 = "AONE-6286";
        docxFileName_6287 = "AONE-6287";
        docxFileName_6288 = "AONE-6288";

        mapConnect = "cmd /c start /WAIT net use" + " " + networkDrive + " " + networkPath + " " + "/user:" + testUser + " " + DEFAULT_PASSWORD;

        Runtime.getRuntime().exec(mapConnect);
        if (checkDirOrFileExists(7, 200, networkDrive + cifsPath))
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
        Runtime.getRuntime().exec("taskkill /F /IM WINWORD.EXE");

        super.tearDown();

    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() throws IOException
    {

        Runtime.getRuntime().exec("cmd /c start /WAIT net use * /d /y");

        if (checkDirOrFileNotExists(7, 200, networkDrive + cifsPath))
        {
            logger.info("--------Unmapping succesfull " + testUser);
        }
        else
        {
            logger.error("--------Unmapping was not done correctly " + testUser);
        }

    }

    @Test(groups = { "DataPrepWord" })
    public void dataPrep_6285() throws Exception
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

    @Test(groups = { "DataPrepWord" })
    public void dataPrep_6286() throws Exception
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

    @Test(groups = { "DataPrepWord" })
    public void dataPrep_6287() throws Exception
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

    @Test(groups = { "DataPrepWord" })
    public void dataPrep_6288() throws Exception
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

    @Test(groups = { "DataPrepWord" })
    public void dataPrep_6283() throws Exception
    {

        String testName = getTestName();
        String siteName = getSiteName(testName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        File file = new File(DATA_FOLDER + CIFS_LOCATION + SLASH + docFileName_6283 + docxFileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        DocumentDetailsPage detailsPage = documentLibPage.selectFile(docFileName_6283 + docxFileType);

        // Click "Edit Properties" in Actions section;
        EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
        editPropertiesPage.setDocumentTitle(testName);

        detailsPage = editPropertiesPage.selectSave().render();
        detailsPage.render();
    }

    /** AONE-6283:MS Word 2013 - uploaded to Share */
    @Test(groups = { "CIFSWindowsClient", "EnterpriseOnly" })
    public void AONE_6283() throws IOException
    {
        String testName = getTestName();
        String siteName = getSiteName(testName).toLowerCase();
        String first_modification = testName + "1";
        String second_modification = testName + "2";
        String last_modification = testName + "3";

        String fullPath = networkDrive + cifsPath + siteName.toLowerCase() + SLASH + "documentLibrary" + SLASH;

        // ---- Step 1 ----
        // ---- Step Action -----
        // Open .docx document for editing.
        // The document is opened in write mode.
        Ldtp ldtp = word.openFileFromCMD(fullPath, docFileName_6283 + docxFileType, testUser, DEFAULT_PASSWORD, false);
        word.getAbstractUtil().waitForWindow(docFileName_6283);

        // ---- Step 2 ----
        // ---- Step Action -----
        // Add any data.
        // Expected Result
        // The data is entered.
        word.editOffice(ldtp, " " + first_modification + " ");

        // ---- Step 3 ----
        // ---- Step Action -----
        // Save the document (Ctrl+S, for example) and close it
        // Expected Result
        // The document is saved. No errors occur in UI and in the log. No
        // tmp files are left.
        word.saveOffice(ldtp);
        ldtp.waitTime(3);
        word.exitOfficeApplication(ldtp, docFileName_6283);
        ldtp.waitTime(2);
        Assert.assertTrue(checkTemporaryFileDoesntExists(fullPath, docxFileType, 6));
        // ---- Step 4 ----
        // ---- Step Action -----
        // Verify the document's metadata and version history in the Share.
        // Expected Result
        // The document's metadata and version history are not broken. They
        // are displayed correctly.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(docFileName_6283 + docxFileType);

        EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
        editPropertiesPage.getDocumentTitle();
        Assert.assertTrue(editPropertiesPage.getDocumentTitle().equals(testName));
        editPropertiesPage.clickCancel();

        // ---- Step 5 ----
        // ---- Step Action -----
        // Verify the document's content.
        // Expected Result
        // All changes are present and displayed correctly.
        String body = detailsPage.getDocumentBody();
        Assert.assertTrue(body.contains(first_modification));

        // ---- Step 6 ----
        // ---- Step Action -----
        // 6. Open the document for editing again.
        // Expected Result
        // 6. The document is opened in write mode.
        ldtp = word.openFileFromCMD(fullPath, docFileName_6283 + docxFileType, testUser, DEFAULT_PASSWORD, false);
        word.getAbstractUtil().waitForWindow(docFileName_6283);
        // ---- Step 7 ----
        // ---- Step Action -----
        // Add any data.
        // Expected Result
        // The data is entered.
        word.editOffice(ldtp, " " + second_modification + " ");

        // ---- Step 8 ----
        // ---- Step Action -----
        // Verify the document's content.
        // Expected Result
        // All changes are present and displayed correctly
        word.saveOffice(ldtp);
        ldtp.waitTime(3);
        word.exitOfficeApplication(ldtp, docFileName_6283);
        ldtp.waitTime(2);
        Assert.assertTrue(checkTemporaryFileDoesntExists(fullPath, docxFileType, 6));

        // ---- Step 9 ----
        // ---- Step Action -----
        // Verify the document's metadata and version history in the Share.
        // Expected Result
        // The document's metadata and version history are not broken. They
        // are displayed correctly.
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        detailsPage = documentLibPage.selectFile(docFileName_6283 + docxFileType);

        editPropertiesPage = detailsPage.selectEditProperties().render();
        editPropertiesPage.getDocumentTitle();
        Assert.assertTrue(editPropertiesPage.getDocumentTitle().equals(testName));
        editPropertiesPage.clickCancel();

        // ---- Step 10 ----
        // ---- Step Action -----
        // Verify the document's content.
        // Expected Result
        // All changes are present and displayed correctly.
        body = detailsPage.getDocumentBody();
        Assert.assertTrue(body.contains(second_modification));

        // ---- Step 11 ----
        // ---- Step Action -----
        // 6. Open the document for editing again.
        // Expected Result
        // 6. The document is opened in write mode.
        ldtp = word.openFileFromCMD(fullPath, docFileName_6283 + docxFileType, testUser, DEFAULT_PASSWORD, false);
        word.getAbstractUtil().waitForWindow(docFileName_6283);
        // ---- Step 12 ----
        // ---- Step Action -----
        // Add any data.
        // Expected Result
        // The data is entered.
        word.editOffice(ldtp, " " + last_modification + " ");

        // ---- Step 13 ----
        // ---- Step Action -----
        // Verify the document's content.
        // Expected Result
        // All changes are present and displayed correctly
        word.saveOffice(ldtp);
        ldtp.waitTime(3);
        word.exitOfficeApplication(ldtp, docFileName_6283);
        ldtp.waitTime(2);
        Assert.assertTrue(checkTemporaryFileDoesntExists(fullPath, docxFileType, 6));

        // ---- Step 14 ----
        // ---- Step Action -----
        // Verify the document's metadata and version history in the Share.
        // Expected Result
        // The document's metadata and version history are not broken. They
        // are displayed correctly.
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        detailsPage = documentLibPage.selectFile(docFileName_6283 + docxFileType);

        editPropertiesPage = detailsPage.selectEditProperties().render();
        editPropertiesPage.getDocumentTitle();
        Assert.assertTrue(editPropertiesPage.getDocumentTitle().equals(testName));
        editPropertiesPage.clickCancel();

        // ---- Step 15 ----
        // ---- Step Action -----
        // Verify the document's content.
        // Expected Result
        // All changes are present and displayed correctly.
        body = detailsPage.getDocumentBody();
        Assert.assertTrue(body.contains(last_modification));

    }

    @Test(groups = { "DataPrepWord" })
    public void dataPrep_6284() throws Exception
    {

        String testName = getTestName();
        String siteName = getSiteName(testName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        File file = new File(DATA_FOLDER + CIFS_LOCATION + SLASH + docFileName_6284 + docxFileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        DocumentDetailsPage detailsPage = documentLibPage.selectFile(docFileName_6284 + docxFileType);

        // Click "Edit Properties" in Actions section;
        EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
        editPropertiesPage.setDocumentTitle(testName);

        detailsPage = editPropertiesPage.selectSave().render();
        detailsPage.render();
    }

    /** AONE-6284:MS Word 2013 - uploaded to Share (big) */
    @Test(groups = { "CIFSWindowsClient", "EnterpriseOnly" })
    public void AONE_6284() throws IOException, AWTException
    {
        String testName = getTestName();
        String siteName = getSiteName(testName).toLowerCase();
        String first_modification = testName + "1";
        String second_modification = testName + "2";
        String last_modification = testName + "3";

        String fullPath = networkDrive + cifsPath + siteName.toLowerCase() + SLASH + "documentLibrary" + SLASH;

        // ---- Step 1 ----
        // ---- Step Action -----
        // Open .docx document for editing.
        // The document is opened in write mode.
        Ldtp ldtp = word.openFileFromCMD(fullPath, docFileName_6284 + docxFileType, testUser, DEFAULT_PASSWORD, false);
        word.getAbstractUtil().waitForWindow(docFileName_6284);
        // ---- Step 2 ----
        // ---- Step Action -----
        // Add any data.
        // Expected Result
        // The data is entered.
        word.getAbstractUtil().waitForWindow(docFileName_6284);
        uploadImageInOffice(image_1);
        word.editOffice(ldtp, " " + first_modification + " ");

        // ---- Step 3 ----
        // ---- Step Action -----
        // Save the document (Ctrl+S, for example) and close it
        // Expected Result
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.
        word.saveOffice(ldtp);
        ldtp.waitTime(3);
        word.exitOfficeApplication(ldtp, docFileName_6284);
        ldtp.waitTime(2);

        Assert.assertTrue(checkTemporaryFileDoesntExists(fullPath, docxFileType, 6));

        // ---- Step 4 ----
        // ---- Step Action -----
        // Verify the document's metadata and version history in the Share.
        // Expected Result
        // The document's metadata and version history are not broken. They are
        // displayed correctly.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(docFileName_6284 + docxFileType);

        EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
        editPropertiesPage.getDocumentTitle();
        Assert.assertTrue(editPropertiesPage.getDocumentTitle().equals(testName));
        editPropertiesPage.clickCancel();

        // ---- Step 5 ----
        // ---- Step Action -----
        // Verify the document's content.
        // Expected Result
        // All changes are present and displayed correctly.
        String body = detailsPage.getDocumentBody();
        Assert.assertTrue(body.contains(first_modification));

        // ---- Step 6 ----
        // ---- Step Action -----
        // 6. Open the document for editing again.
        // Expected Result
        // 6. The document is opened in write mode.
        ldtp = word.openFileFromCMD(fullPath, docFileName_6284 + docxFileType, testUser, DEFAULT_PASSWORD, false);
        word.getAbstractUtil().waitForWindow(docFileName_6284);
        // ---- Step 7 ----
        // ---- Step Action -----
        // Add any data.
        // Expected Result
        // The data is entered.
        uploadImageInOffice(image_2);
        word.editOffice(ldtp, " " + second_modification + " ");

        // ---- Step 8 ----
        // ---- Step Action -----
        // Verify the document's content.
        // Expected Result
        // All changes are present and displayed correctly
        word.saveOffice(ldtp);
        ldtp.waitTime(3);
        word.exitOfficeApplication(ldtp, docFileName_6284);
        ldtp.waitTime(2);

        Assert.assertTrue(checkTemporaryFileDoesntExists(fullPath, docxFileType, 6));

        // ---- Step 9 ----
        // ---- Step Action -----
        // Verify the document's metadata and version history in the Share.
        // Expected Result
        // The document's metadata and version history are not broken. They are
        // displayed correctly.
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        detailsPage = documentLibPage.selectFile(docFileName_6284 + docxFileType);

        editPropertiesPage = detailsPage.selectEditProperties().render();
        editPropertiesPage.getDocumentTitle();
        Assert.assertTrue(editPropertiesPage.getDocumentTitle().equals(testName));
        editPropertiesPage.clickCancel();

        // ---- Step 10 ----
        // ---- Step Action -----
        // Verify the document's content.
        // Expected Result
        // All changes are present and displayed correctly.
        body = detailsPage.getDocumentBody();
        Assert.assertTrue(body.contains(second_modification));

        // ---- Step 11 ----
        // ---- Step Action -----
        // 6. Open the document for editing again.
        // Expected Result
        // 6. The document is opened in write mode.
        ldtp = word.openFileFromCMD(fullPath, docFileName_6284 + docxFileType, testUser, DEFAULT_PASSWORD, false);
        word.getAbstractUtil().waitForWindow(docFileName_6284);
        // ---- Step 12 ----
        // ---- Step Action -----
        // Add any data.
        // Expected Result
        // The data is entered.
        uploadImageInOffice(image_3);
        word.editOffice(ldtp, " " + last_modification + " ");

        // ---- Step 13 ----
        // ---- Step Action -----
        // Verify the document's content.
        // Expected Result
        // All changes are present and displayed correctly
        word.saveOffice(ldtp);
        ldtp.waitTime(3);
        word.exitOfficeApplication(ldtp, docFileName_6284);
        Assert.assertTrue(checkTemporaryFileDoesntExists(fullPath, docxFileType, 6));

        // ---- Step 14 ----
        // ---- Step Action -----
        // Verify the document's metadata and version history in the Share.
        // Expected Result
        // The document's metadata and version history are not broken. They are
        // displayed correctly.
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        detailsPage = documentLibPage.selectFile(docFileName_6284 + docxFileType);

        editPropertiesPage = detailsPage.selectEditProperties().render();
        editPropertiesPage.getDocumentTitle();
        Assert.assertTrue(editPropertiesPage.getDocumentTitle().equals(testName));
        editPropertiesPage.clickCancel();

        // ---- Step 15 ----
        // ---- Step Action -----
        // Verify the document's content.
        // Expected Result
        // All changes are present and displayed correctly.
        body = detailsPage.getDocumentBody();
        Assert.assertTrue(body.contains(last_modification));

    }

    /** AONE-6287:MS Word 2013 - saved into CIFS */
    @Test(groups = { "CIFSWindowsClient", "EnterpriseOnly" })
    public void AONE_6287() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName).toLowerCase();
        String edit2 = "New text2";
        String edit3 = "New text3";
        String fullPath = networkDrive + cifsPath + siteName.toLowerCase() + SLASH + "documentLibrary" + SLASH;
        String localPath = DATA_FOLDER + CIFS_LOCATION + SLASH;
        Ldtp security = new Ldtp("Windows Security");
        Ldtp l1;

        // --- Step 1 ---
        // --- Step action ---
        // Save a document into the CIFS drive (into the Document Library space)
        // and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.

        int noOfFilesBeforeSave = 0;
        l1 = word.openFileFromCMD(localPath, docxFileName_6287 + docxFileType, testUser, DEFAULT_PASSWORD, false);
        word.getAbstractUtil().waitForWindow(docxFileName_6287);
        l1 = word.getAbstractUtil().setOnWindow(docxFileName_6287);

        word.saveAsOffice(l1, fullPath + docxFileName_6287);
        word.operateOnSecurityAndWait(security, testUser, DEFAULT_PASSWORD);
        word.getAbstractUtil().waitForWindow(docxFileName_6287);
        word.exitOfficeApplication(l1, docxFileName_6287);
        l1.waitTime(2);

        Assert.assertTrue(checkTemporaryFileDoesntExists(fullPath, docxFileType, 6));

        // --- Step 2 ---
        // --- Step action ---
        // Verify the document's content.
        // --- Expected results --
        // All changes are present and displayed correctly.
        ShareUser.login(drone, testUser);
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(docxFileName_6287 + docxFileType);

        EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
        Assert.assertTrue(editPropertiesPage.getName().equals(docxFileName_6287 + docxFileType));
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
        String documentTitle = "Title " + docxFileName_6287;
        String documentDescription = "Description for " + docxFileName_6287;
        editDocPropPage.setDocumentTitle(documentTitle);
        editDocPropPage.setDescription(documentDescription);
        detailsPage = editDocPropPage.selectSave().render();

        String newFileVersion2 = CIFS_LOCATION + SLASH + "SaveCIFSv2.docx";
        String newFileVersion3 = CIFS_LOCATION + SLASH + "SaveCIFSv3.docx";
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
        l1 = word.openFileFromCMD(fullPath, docxFileName_6287 + docxFileType, testUser, DEFAULT_PASSWORD, false);
        l1 = word.getAbstractUtil().setOnWindow(docxFileName_6287);
        String actualName = l1.getWindowName();
        Assert.assertTrue(actualName.contains(docxFileName_6287), "Microsoft Excel - " + docxFileName_6287 + " window is not active.");

        // --- Step 5 ---
        // --- Step action ---
        // Add any data.
        // --- Expected results --
        // The data is entered.
        word.editOffice(l1, " " + edit2 + " ");

        // --- Step 6 ---
        // --- Step action ---
        // Save the document (Ctrl+S, for example) and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.
        word.saveOffice(l1);
        l1.waitTime(3);
        word.exitOfficeApplication(l1, docxFileName_6287);
        l1.waitTime(2);
        Assert.assertTrue(checkTemporaryFileDoesntExists(fullPath, docxFileType, 6));

        // --- Step 7 ---
        // --- Step action ---
        // Verify the document's metadata and version history in the Share.
        // --- Expected results --
        // The document's metadata and version history are not broken. They are
        // displayed correctly..
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        detailsPage = ShareUserSitePage.openDetailsPage(drone, docxFileName_6287 + docxFileType).render();

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
        String body = detailsPage.getDocumentBody();
        Assert.assertTrue(body.contains(edit2));

        // --- Step 9 ---
        // --- Step action ---
        // Open the document for editing again.
        // --- Expected results --
        // The document is opened in write mode.
        l1 = word.openFileFromCMD(fullPath, docxFileName_6287 + docxFileType, testUser, DEFAULT_PASSWORD, false);
        l1 = word.getAbstractUtil().setOnWindow(docxFileName_6287);
        actualName = l1.getWindowName();
        Assert.assertTrue(actualName.contains(docxFileName_6287), "Microsoft Excel - " + docxFileName_6287 + " window is not active.");

        // --- Step 10 ---
        // --- Step action ---
        // Add any data.
        // --- Expected results --
        // The data is entered.
        word.editOffice(l1, " " + edit3 + " ");

        // --- Step 11 ---
        // --- Step action ---
        // Save the document (Ctrl+S, for example) and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.
        l1 = word.getAbstractUtil().setOnWindow(docxFileName_6287);
        word.saveOffice(l1);
        l1.waitTime(3);
        word.exitOfficeApplication(l1, docxFileName_6287);
        l1.waitTime(2);
        Assert.assertTrue(checkTemporaryFileDoesntExists(fullPath, docxFileType, 6));

        // --- Step 12 ---
        // --- Step action ---
        // Verify the document's metadata and version history in the Share.
        // --- Expected results --
        // The document's metadata and version history are not broken. They are
        // displayed correctly..
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        detailsPage = ShareUserSitePage.openDetailsPage(drone, docxFileName_6287 + docxFileType).render();

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
        String body3 = detailsPage.getDocumentBody();
        Assert.assertTrue(body3.contains(edit3));
    }

    /** AONE-6288:MS Word 2013 - saved into CIFS (big) */
    @Test(groups = { "CIFSWindowsClient", "EnterpriseOnly" })
    public void AONE_6288() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName).toLowerCase();
        String edit2 = "New text2";
        String edit3 = "New text3";
        String fullPath = networkDrive + cifsPath + siteName.toLowerCase() + SLASH + "documentLibrary" + SLASH;
        String localPath = DATA_FOLDER + CIFS_LOCATION + SLASH;
        Ldtp security = new Ldtp("Windows Security");
        Ldtp l1;

        // --- Step 1 ---
        // --- Step action ---
        // Save a document into the CIFS drive (into the Document Library space)
        // and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.

        int noOfFilesBeforeSave = 0;
        l1 = word.openFileFromCMD(localPath, docxFileName_6288 + docxFileType, testUser, DEFAULT_PASSWORD, false);
        word.getAbstractUtil().waitForWindow(docxFileName_6288);
        l1 = word.getAbstractUtil().setOnWindow(docxFileName_6288);

        word.saveAsOffice(l1, fullPath + docxFileName_6288);
        word.operateOnSecurityAndWait(security, testUser, DEFAULT_PASSWORD);
        word.getAbstractUtil().waitForWindow(docxFileName_6288);
        word.exitOfficeApplication(l1, docxFileName_6288);
        l1.waitTillGuiNotExist(docxFileName_6288, 3);

        Assert.assertTrue(checkTemporaryFileDoesntExists(fullPath, docxFileType, 6));

        // --- Step 2 ---
        // --- Step action ---
        // Verify the document's content.
        // --- Expected results --
        // All changes are present and displayed correctly.
        ShareUser.login(drone, testUser);
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(docxFileName_6288 + docxFileType);

        EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
        Assert.assertTrue(editPropertiesPage.getName().equals(docxFileName_6288 + docxFileType));
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
        String documentTitle = "Title " + docxFileName_6288;
        String documentDescription = "Description for " + docxFileName_6288;
        editDocPropPage.setDocumentTitle(documentTitle);
        editDocPropPage.setDescription(documentDescription);
        detailsPage = editDocPropPage.selectSave().render();

        String newFileVersion2 = CIFS_LOCATION + SLASH + "SaveCIFSv2.docx";
        String newFileVersion3 = CIFS_LOCATION + SLASH + "SaveCIFSv3.docx";
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
        l1 = word.openFileFromCMD(fullPath, docxFileName_6288 + docxFileType, testUser, DEFAULT_PASSWORD, false);
        l1 = word.getAbstractUtil().setOnWindow(docxFileName_6288);
        String actualName = l1.getWindowName();
        Assert.assertTrue(actualName.contains(docxFileName_6288), "Microsoft Excel - " + docxFileName_6288 + " window is active.");

        // --- Step 5 ---
        // --- Step action ---
        // Add any data (5-10 mb).
        // --- Expected results --
        // The data is entered.
        uploadImageInOffice(image_1);
        word.editOffice(l1, " " + edit2 + " ");

        // --- Step 6 ---
        // --- Step action ---
        // Save the document (Ctrl+S, for example) and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.
        word.saveOffice(l1);
        l1.waitTime(3);
        word.exitOfficeApplication(l1, docxFileName_6288);
        Assert.assertTrue(checkTemporaryFileDoesntExists(fullPath, docxFileType, 6));

        // --- Step 7 ---
        // --- Step action ---
        // Verify the document's metadata and version history in the Share.
        // --- Expected results --
        // The document's metadata and version history are not broken. They are
        // displayed correctly..
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        detailsPage = ShareUserSitePage.openDetailsPage(drone, docxFileName_6288 + docxFileType).render();

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
        String body = detailsPage.getDocumentBody();
        Assert.assertTrue(body.contains(edit2));

        // --- Step 9 ---
        // --- Step action ---
        // Open the document for editing again.
        // --- Expected results --
        // The document is opened in write mode.
        l1 = word.openFileFromCMD(fullPath, docxFileName_6288 + docxFileType, testUser, DEFAULT_PASSWORD, false);
        l1 = word.getAbstractUtil().setOnWindow(docxFileName_6288);
        actualName = l1.getWindowName();
        Assert.assertTrue(actualName.contains(docxFileName_6288), "Microsoft Excel - " + docxFileName_6288 + " window is active.");

        // --- Step 10 ---
        // --- Step action ---
        // Add any data (5-10 mb).
        // --- Expected results --
        // The data is entered.
        uploadImageInOffice(image_2);
        word.editOffice(l1, " " + edit3 + " ");

        // --- Step 11 ---
        // --- Step action ---
        // Save the document (Ctrl+S, for example) and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.
        l1 = word.getAbstractUtil().setOnWindow(docxFileName_6288);
        word.saveOffice(l1);
        l1.waitTime(3);
        word.exitOfficeApplication(l1, docxFileName_6288);
        Assert.assertTrue(checkTemporaryFileDoesntExists(fullPath, docxFileType, 6));
        // --- Step 12 ---
        // --- Step action ---
        // Verify the document's metadata and version history in the Share.
        // --- Expected results --
        // The document's metadata and version history are not broken. They are
        // displayed correctly..
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        detailsPage = ShareUserSitePage.openDetailsPage(drone, docxFileName_6288 + docxFileType).render();

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
        String body3 = detailsPage.getDocumentBody();
        Assert.assertTrue(body3.contains(edit3));
    }

    /** AONE-6285:MS Word 2013 - created via context menu */
    @Test(groups = { "CIFSWindowsClient", "EnterpriseOnly" })
    public void AONE_6285() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName).toLowerCase();
        String addText = "First text";
        String edit1 = "New text1";
        String edit2 = "New text2";
        String edit3 = "New text3";
        String fullPath = networkDrive + cifsPath + siteName.toLowerCase() + SLASH + "documentLibrary" + SLASH;

        Ldtp security = new Ldtp("Windows Security");
        Ldtp l1;

        // --- Step 1 ---
        // --- Step action ---
        // RBC in the window -> New -> New Microsoft Office Word Document
        // --- Expected results --
        // The document .docx is created

        l1 = word.openOfficeApplication();
        word.editOffice(l1, addText);
        word.saveAsOffice(l1, fullPath + docxFileName_6285);
        word.operateOnSecurityAndWait(security, testUser, DEFAULT_PASSWORD);
        word.getAbstractUtil().waitForWindow(docxFileName_6285);
        word.exitOfficeApplication(l1, docxFileName_6285);
        l1.waitTime(2);

        // ---- Step 2 ----
        // ---- Step Action -----
        // Open .docx document for editing.
        // ---- Expected Result -----
        // The document is opened in write mode.
        l1 = word.openFileFromCMD(fullPath, docxFileName_6285 + docxFileType, testUser, DEFAULT_PASSWORD, false);
        word.getAbstractUtil().waitForWindow(docxFileName_6285);
        l1 = word.getAbstractUtil().setOnWindow(docxFileName_6285);

        // ---- Step 3 ----
        // ---- Step Action -----
        // Add any data.
        // ---- Expected Result -----
        // The data is entered.
        word.editOffice(l1, " " + edit1 + " ");

        // --- Step 4 ---
        // --- Step action ---
        // Save the document (Ctrl+S, for example) and close it. New text1
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.
        word.saveOffice(l1);
        l1.waitTime(3);
        word.exitOfficeApplication(l1, docxFileName_6285);
        l1.waitTime(2);
        Assert.assertTrue(checkTemporaryFileDoesntExists(fullPath, docxFileType, 6));

        // ---- Step 5 ----
        // ---- Step Action -----
        // Verify the document's content.
        // Expected Result
        // All changes are present and displayed correctly.
        ShareUser.login(drone, testUser);
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(docxFileName_6285 + docxFileType);

        EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
        Assert.assertTrue(editPropertiesPage.getName().equals(docxFileName_6285 + docxFileType));
        editPropertiesPage.clickCancel();
        String body = detailsPage.getDocumentBody();
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
        String documentTitle = "Title " + docxFileName_6285;
        String documentDescription = "Description for " + docxFileName_6285;
        editDocPropPage.setDocumentTitle(documentTitle);
        editDocPropPage.setDescription(documentDescription);
        detailsPage = editDocPropPage.selectSave().render();

        String newFileVersion2 = CIFS_LOCATION + SLASH + "SaveCIFSv2.docx";
        String newFileVersion3 = CIFS_LOCATION + SLASH + "SaveCIFSv3.docx";
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
        l1 = word.openFileFromCMD(fullPath, docxFileName_6285 + docxFileType, testUser, DEFAULT_PASSWORD, false);
        word.getAbstractUtil().waitForWindow(docxFileName_6285);
        // ---- Step 8 ----
        // ---- Step Action -----
        // Add any data.
        // ---- Expected Result -----
        // The data is entered.
        word.editOffice(l1, " " + edit2 + " ");

        // --- Step 9 ---
        // --- Step action ---
        // Save the document (Ctrl+S, for example) and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.
        word.saveOffice(l1);
        l1.waitTime(3);
        word.exitOfficeApplication(l1, docxFileName_6285);
        l1.waitTime(2);
        Assert.assertTrue(checkTemporaryFileDoesntExists(fullPath, docxFileType, 6));

        // --- Step 10 ---
        // --- Step action ---
        // Specify the document's metadata, e.g. title, description and so on,
        // and create some version history (i.e. add several new versions (minor
        // and major))
        // via the Share.
        // --- Expected results --
        // The document's metadata and version history are specified.
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        detailsPage = ShareUserSitePage.openDetailsPage(drone, docxFileName_6285 + docxFileType).render();

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
        body = detailsPage.getDocumentBody();
        Assert.assertTrue(body.contains(edit2));

        // ---- Step 12 ----
        // ---- Step Action -----
        // Open .docx document for editing.
        // ---- Expected Result -----
        // The document is opened in write mode.
        l1 = word.openFileFromCMD(fullPath, docxFileName_6285 + docxFileType, testUser, DEFAULT_PASSWORD, false);
        word.getAbstractUtil().waitForWindow(docxFileName_6285);
        // ---- Step 13 ----
        // ---- Step Action -----
        // Add any data.
        // ---- Expected Result -----
        // The data is entered.
        word.editOffice(l1, " " + edit3 + " ");

        // --- Step 14 ---
        // --- Step action ---
        // Save the document (Ctrl+S, for example) and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.
        word.saveOffice(l1);
        l1.waitTime(3);
        word.exitOfficeApplication(l1, docxFileName_6285);
        l1.waitTime(2);
        Assert.assertTrue(checkTemporaryFileDoesntExists(fullPath, docxFileType, 6));

        // --- Step 15 ---
        // --- Step action ---
        // Specify the document's metadata, e.g. title, description and so on,
        // and create some version history (i.e. add several new versions (minor
        // and major))
        // via the Share.
        // --- Expected results --
        // The document's metadata and version history are specified.
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        detailsPage = ShareUserSitePage.openDetailsPage(drone, docxFileName_6285 + docxFileType).render();

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
        body = detailsPage.getDocumentBody();
        Assert.assertTrue(body.contains(edit3));

    }

    /** AONE-6286:MS Word 2013 - created via context menu (big) */
    @Test(groups = { "CIFSWindowsClient", "EnterpriseOnly" })
    public void AONE_6286() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName).toLowerCase();
        String addText = "First text";
        String edit1 = "New text1";
        String edit2 = "New text2";
        String edit3 = "New text3";
        String fullPath = networkDrive + cifsPath + siteName.toLowerCase() + SLASH + "documentLibrary" + SLASH;

        Ldtp security = new Ldtp("Windows Security");
        Ldtp l2;

        // --- Step 1 ---
        // --- Step action ---
        // RBC in the window -> New -> New Microsoft Office Word Document
        // --- Expected results --
        // The document .docx is created

        l2 = word.openOfficeApplication();
        word.editOffice(l2, addText);
        word.saveAsOffice(l2, fullPath + docxFileName_6286);
        word.operateOnSecurityAndWait(security, testUser, DEFAULT_PASSWORD);
        word.getAbstractUtil().waitForWindow(docxFileName_6286);
        l2 = word.getAbstractUtil().setOnWindow(docxFileName_6286);
        word.exitOfficeApplication(l2, docxFileName_6286);
        l2.waitTime(2);

        // ---- Step 2 ----
        // ---- Step Action -----
        // Open .docx document for editing.
        // ---- Expected Result -----
        // The document is opened in write mode.
        l2 = word.openFileFromCMD(fullPath, docxFileName_6286 + docxFileType, testUser, DEFAULT_PASSWORD, false);
        word.getAbstractUtil().waitForWindow(docxFileName_6286);
        l2 = word.getAbstractUtil().setOnWindow(docxFileName_6286);

        // ---- Step 3 ----
        // ---- Step Action -----
        // Add any data (5-10 mb).
        // ---- Expected Result -----
        // The data is entered.
        uploadImageInOffice(image_1);
        word.editOffice(l2, " " + edit1 + " ");

        // --- Step 4 ---
        // --- Step action ---
        // Save the document (Ctrl+S, for example) and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.
        word.saveOffice(l2);
        l2.waitTime(3);
        word.exitOfficeApplication(l2, docxFileName_6286);
        Assert.assertTrue(checkTemporaryFileDoesntExists(fullPath, docxFileType, 6));

        // ---- Step 5 ----
        // ---- Step Action -----
        // Verify the document's content.
        // Expected Result
        // All changes are present and displayed correctly.
        ShareUser.login(drone, testUser);
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(docxFileName_6286 + docxFileType);
        EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
        Assert.assertTrue(editPropertiesPage.getName().equals(docxFileName_6286 + docxFileType));
        editPropertiesPage.clickCancel();
        String body = detailsPage.getDocumentBody();
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
        String documentTitle = "Title " + docxFileName_6286;
        String documentDescription = "Description for " + docxFileName_6286;
        editDocPropPage.setDocumentTitle(documentTitle);
        editDocPropPage.setDescription(documentDescription);
        detailsPage = editDocPropPage.selectSave().render();

        String newFileVersion2 = CIFS_LOCATION + SLASH + "SaveCIFSv2.docx";
        String newFileVersion3 = CIFS_LOCATION + SLASH + "SaveCIFSv3.docx";
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
        l2 = word.openFileFromCMD(fullPath, docxFileName_6286 + docxFileType, testUser, DEFAULT_PASSWORD, false);
        word.getAbstractUtil().waitForWindow(docxFileName_6286);
        // ---- Step 8 ----
        // ---- Step Action -----
        // Add any data (5-10 mb).
        // ---- Expected Result -----
        // The data is entered.
        uploadImageInOffice(image_2);
        word.editOffice(l2, " " + edit2 + " ");

        // --- Step 9 ---
        // --- Step action ---
        // Save the document (Ctrl+S, for example) and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.
        word.saveOffice(l2);
        l2.waitTime(3);
        word.exitOfficeApplication(l2, docxFileName_6286);
        Assert.assertTrue(checkTemporaryFileDoesntExists(fullPath, docxFileType, 6));

        // --- Step 10 ---
        // --- Step action ---
        // Specify the document's metadata, e.g. title, description and so on,
        // and create some version history (i.e. add several new versions (minor
        // and major))
        // via the Share.
        // --- Expected results --
        // The document's metadata and version history are specified.
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        detailsPage = ShareUserSitePage.openDetailsPage(drone, docxFileName_6286 + docxFileType).render();

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
        body = detailsPage.getDocumentBody();
        Assert.assertTrue(body.contains(edit2));

        // ---- Step 12 ----
        // ---- Step Action -----
        // Open .docx document for editing.
        // ---- Expected Result -----
        // The document is opened in write mode.
        l2 = word.openFileFromCMD(fullPath, docxFileName_6286 + docxFileType, testUser, DEFAULT_PASSWORD, false);
        word.getAbstractUtil().waitForWindow(docxFileName_6286);
        // ---- Step 13 ----
        // ---- Step Action -----
        // Add any data (5-10 mb).
        // ---- Expected Result -----
        // The data is entered.
        uploadImageInOffice(image_3);
        word.editOffice(l2, " " + edit3 + " ");

        // --- Step 14 ---
        // --- Step action ---
        // Save the document (Ctrl+S, for example) and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.
        word.saveOffice(l2);
        l2.waitTime(3);
        word.exitOfficeApplication(l2, docxFileName_6286);
        Assert.assertTrue(checkTemporaryFileDoesntExists(fullPath, docxFileType, 6));

        // --- Step 15 ---
        // --- Step action ---
        // Specify the document's metadata, e.g. title, description and so on,
        // and create some version history (i.e. add several new versions (minor
        // and major))
        // via the Share.
        // --- Expected results --
        // The document's metadata and version history are specified.
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        detailsPage = ShareUserSitePage.openDetailsPage(drone, docxFileName_6286 + docxFileType).render();

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
        body = detailsPage.getDocumentBody();
        Assert.assertTrue(body.contains(edit3));

    }

    private void uploadImageInOffice(String image) throws AWTException
    {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        ImageIcon icon = new ImageIcon(image);
        CifsUtil clipboardImage = new CifsUtil(icon.getImage());
        clipboard.setContents(clipboardImage, clipboardImage);

        Robot r = new Robot();
        r.keyPress(KeyEvent.VK_CONTROL);
        r.keyPress(KeyEvent.VK_V);
        r.keyRelease(KeyEvent.VK_CONTROL);
        r.keyRelease(KeyEvent.VK_V);
    }

    private Boolean checkDirOrFileExists(int timeoutSECONDS, int pollingTimeMILISECONDS, String path)
    {
        long counter = 0;
        boolean existence = false;
        while (counter < TimeUnit.SECONDS.toMillis(timeoutSECONDS))
        {
            File test = new File(path);
            if (test.exists())
            {
                existence = true;
                break;
            }
            else
            {
                try
                {
                    TimeUnit.MILLISECONDS.sleep(pollingTimeMILISECONDS);
                }
                catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                counter = counter + pollingTimeMILISECONDS;
            }
        }
        return existence;
    }

    private Boolean checkDirOrFileNotExists(int timeoutSECONDS, int pollingTimeMILISECONDS, String path)
    {
        long counter = 0;
        boolean existence = false;
        while (counter < TimeUnit.SECONDS.toMillis(timeoutSECONDS))
        {
            File test = new File(path);
            if (test.exists())
            {
                try
                {
                    TimeUnit.MILLISECONDS.sleep(pollingTimeMILISECONDS);
                }
                catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                counter = counter + pollingTimeMILISECONDS;

            }
            else
            {
                existence = true;
                break;
            }
        }
        return existence;
    }

    private Boolean checkTemporaryFileDoesntExists(String path, String extension, int timeout)
    {
        long counter = 0;
        boolean check = false;
        boolean existence = true;
        while (counter < TimeUnit.SECONDS.toMillis(timeout))
        {
            File test = new File(path);
            for (File element : test.listFiles())
            {
                if (element.isHidden() && element.getName().contains(extension))
                {
                    existence = false;
                    break;
                }
            }
            if (existence)
            {
                check = true;
                break;
            }
            else
            {
                try
                {
                    TimeUnit.MILLISECONDS.sleep(200);
                }
                catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                counter = counter + 200;
                existence = true;
            }
        }
        return check;
    }
}
