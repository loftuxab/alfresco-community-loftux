package org.alfresco.share.cloudsync;

import static org.alfresco.share.util.ShareUser.refreshDocumentLibrary;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.CopyOrMoveContentPage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.site.document.ManagePermissionsPage;
import org.alfresco.po.share.site.document.ManagePermissionsPage.ButtonType;
import org.alfresco.po.share.site.document.SyncInfoPage;
import org.alfresco.po.share.site.document.UserProfile;
import org.alfresco.po.share.site.document.VersionDetails;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.WebDroneType;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Created by razvan.dorobantu on 12/9/2014.
 */
@Listeners(FailedTestListener.class)
public class HybridSyncNegativeTests extends AbstractWorkflow
{
    private static Log logger = LogFactory.getLog(HybridSyncPositiveTests.class);

    protected String testUser;
    private String testDomain;
    protected static long timeToWait;
    protected static int retryCount;
    String uniqueRun;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        logger.info("Starting Tests: " + testName);
        testName = this.getClass().getSimpleName();
        testDomain = DOMAIN_HYBRID;
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1001");
        timeToWait = 25000;
        retryCount = 10;
        uniqueRun = "TS6";
        
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15478() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser };
        String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        String opFileName = getFileName(testName);
        String[] opFileInfo = new String[] { opFileName };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC).render();

        // set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.uploadFileInFolder(drone, opFileInfo).render();
        ShareUser.logout(drone);
    }

    /**
     * AONE-15478 Sync the same file that you synced earlier again to a different location
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
    public void AONE_15478() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        String fileName = getFileName(testName);

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

        // ---- Step 1 ----
        // ---- Step action ----
        // Set cursor to the document and expand More+ menu
        // ---- Expected results ----
        // List of actions is appeared for document and 'sync to cloud' option available
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isSyncToCloudLinkPresent(), "Sync to cloud button is not presesnt");

        // ---- Step 2 ----
        // ---- Step action ----
        // Choose 'Sync to Cloud' option
        // ---- Expected results ----
        // Pop-up window to select target cloud location appears
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(fileName).selectSyncToCloud().render();
        Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + fileName + " to The Cloud"), "Title is not displyed correctly");

        // ---- Step 3 ----
        // ---- Step action ----
        // Verify information displayed in window
        // ---- Expected results ----
        // All available networks are displayed correctly and can be chosen
        Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(testDomain), "Network " + testDomain + " is not displayed");
        Assert.assertTrue(destinationAndAssigneePage.isSiteDisplayed(cloudSiteName), "Site " + cloudSiteName + " is not displyed");

        // ---- Step 4 ----
        // ---- Step action ----
        // Choose any network --available site--document library and press OK button
        // ---- Expected results ----
        // Notification about file is synced successfully appears.
        destinationAndAssigneePage.selectNetwork(testDomain);
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectFolder("Documents");
        destinationAndAssigneePage.clickSyncButton();
        Assert.assertTrue(documentLibraryPage.isSyncMessagePresent(), "Sync message is not displyed");

        // ---- Step 5 ----
        // ---- Step action ----
        // Choose Sync to Cloud again.
        // ---- Expected results ----
        // There is no 'Sync to Cloud' option
        documentLibraryPage.render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isSyncToCloudLinkPresent(), "Sync to cloud button is presesnt");
        ShareUser.logout(drone);
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15479() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser };
        String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        String opFileName = getFileName(testName) + ".txt";
        String[] opFileInfo = new String[] { opFileName };
        String folderName = getFolderName(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC).render();

        // set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);
        ShareUser.uploadFileInFolder(drone, opFileInfo).render();
        ShareUser.logout(drone);
    }

    /**
     * AONE-15479 Update the content in on-premise
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 500000)
    public void AONE_15479() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        String fileName = getFileName(testName) + ".txt";
        String contentEdited = fileName + "-edited";
        String folderName = getFolderName(testName);

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

        // ---- Step 1 ----
        // ---- Step action ----
        // Set cursor to the document and expand More+ menu
        // ---- Expected results ----
        // List of actions is appeared for document and 'sync to cloud' option available
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isSyncToCloudLinkPresent(), "Sync to cloud not displayed");

        // ---- Step 2 ----
        // ---- Step action ----
        // Choose 'Sync to Cloud' option
        // ---- Expected results ----
        // Pop-up window to select target cloud location appears
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(fileName).selectSyncToCloud().render();
        Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + fileName + " to The Cloud"), "Title is not displyed");

        // ---- Step 3 ----
        // ---- Step action ----
        // Verify information displayed in window
        // ---- Expected results ----
        // All available networks are displayed correctly and can be chosen
        Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(testDomain), "Network " + testDomain + " is not displayed");
        Assert.assertTrue(destinationAndAssigneePage.isSiteDisplayed(cloudSiteName), "Site " + cloudSiteName + " is not displyed");

        // ---- Step 4 ----
        // ---- Step action ----
        // Choose any network --available site--document library and press OK button
        // ---- Expected results ----
        // Notification about file is synced successfully appears.
        destinationAndAssigneePage.selectNetwork(testDomain);
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectFolder("Documents");
        destinationAndAssigneePage.clickSyncButton();
        Assert.assertTrue(documentLibraryPage.isSyncMessagePresent(), "Sync message not displayed");

        // ---- Step 5 ----
        // ---- Step action ----
        // Move the file to different folder and update the content on Alfresco Enterprise.
        // ---- Expected results ----
        // The file is moved and edited.
        documentLibraryPage.render();
        documentLibraryPage.getFileDirectoryInfo(fileName).selectMoveTo();
        CopyOrMoveContentPage contentPage = new CopyOrMoveContentPage(drone);
        contentPage.selectPath(folderName).selectOkButton().render();
        documentLibraryPage = documentLibraryPage.selectFolder(folderName).render();
        Assert.assertTrue(documentLibraryPage.isFileVisible(fileName), "File is not visible");
        DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName).render();
        ShareUser.editTextDocument(drone, fileName, "", contentEdited);

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        documentLibraryPage = documentLibraryPage.selectFolder(folderName).render();
        ShareUser.refreshSharePage(drone);
        documentLibraryPage.getFileDirectoryInfo(fileName).selectRequestSync().render();
        waitForSync(drone, fileName, opSiteName);

        // ---- Step 6 ----
        // ---- Step action ----
        // Check the file content and version updated both in Cloud and Alfresco Enterprise.
        // ---- Expected results ----
        // The file content and version updated both in Cloud and Alfresco Enterprise.
        ContentDetails contentDetailsOp = ShareUserSitePage.getInLineEditContentDetails(drone, fileName);
        String contentOp = contentDetailsOp.getContent();
        Assert.assertTrue(contentOp.equals(contentEdited), "Content is not modified");
        detailsPage = documentLibraryPage.selectFile(fileName).render();
        VersionDetails versionDetails = detailsPage.getCurrentVersionDetails();
        Assert.assertTrue(versionDetails.getVersionNumber().equals("1.1"), "Version is not updated");
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPageCL = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();

        ContentDetails contentDetailsCl = ShareUserSitePage.getInLineEditContentDetails(hybridDrone, fileName);
        String contentCl = contentDetailsCl.getContent();
        Assert.assertTrue(contentCl.equals(contentEdited), "Content is not modified");
        Assert.assertFalse(documentLibraryPageCL.getFileDirectoryInfo(fileName).getVersionInfo().equals("1.0"), "Version is not 1.1");
        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15480() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser };
        String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        String opFileName = getFileName(testName) + ".txt";
        String[] opFileInfo = new String[] { opFileName };
        String folderName = getFolderName(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        ShareUser.createFolderInFolder(hybridDrone, folderName, folderName, DOCLIB);
        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC).render();
        ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.uploadFileInFolder(drone, opFileInfo).render();
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }

    /**
     * AONE-15480 Update the content in Cloud.
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 600000)
    public void AONE_15480() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        String fileName = getFileName(testName) + ".txt";
        String contentEdited = fileName + "-edited";
        String folderName = getFolderName(testName);

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

        // ---- Step 1 ----
        // ---- Step action ----
        // Set cursor to the document and expand More+ menu
        // ---- Expected results ----
        // List of actions is appeared for document and 'sync to cloud' option available
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isSyncToCloudLinkPresent(), "Sync to cloud not displayed");

        // ---- Step 2 ----
        // ---- Step action ----
        // Choose 'Sync to Cloud' option
        // ---- Expected results ----
        // Pop-up window to select target cloud location appears
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(fileName).selectSyncToCloud().render();
        Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + fileName + " to The Cloud"), "Title is not displyed");

        // ---- Step 3 ----
        // ---- Step action ----
        // Verify information displayed in window
        // ---- Expected results ----
        // All available networks are displayed correctly and can be chosen
        Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(testDomain), "Network " + testDomain + " is not displayed");
        Assert.assertTrue(destinationAndAssigneePage.isSiteDisplayed(cloudSiteName), "Site " + cloudSiteName + " is not displyed");

        // ---- Step 4 ----
        // ---- Step action ----
        // Choose any network --available site--document library and press OK button
        // ---- Expected results ----
        // Notification about file is synced successfully appears.
        destinationAndAssigneePage.selectNetwork(testDomain);
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectFolder("Documents");
        destinationAndAssigneePage.clickSyncButton();
        Assert.assertTrue(documentLibraryPage.isSyncMessagePresent(), "Sync message not displayed");
        Assert.assertTrue(checkIfContentIsSynced(drone, fileName));

        // ---- Step 5 ----
        // ---- Step action ----
        // Move the file to different folder and update the content on Cloud
        // ---- Expected results ----
        // The file is moved and edited.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPageCL = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        documentLibraryPageCL.getFileDirectoryInfo(fileName).selectMoveTo();
        CopyOrMoveContentPage contentPage = new CopyOrMoveContentPage(hybridDrone);
        contentPage.selectPath(folderName).selectOkButton().render();
        documentLibraryPageCL = documentLibraryPageCL.selectFolder(folderName).render();
        Assert.assertTrue(documentLibraryPageCL.isFileVisible(fileName), "File is not visible");
        DocumentDetailsPage detailsPage = documentLibraryPageCL.selectFile(fileName).render();
        detailsPage = ShareUser.editTextDocument(hybridDrone, fileName, "", contentEdited).render();

        // ---- Step 6 ----
        // ---- Step action ----
        // Check the file content and version updated both in Cloud and Alfresco Enterprise.
        // ---- Expected results ----
        // The file content and version updated both in Cloud and Alfresco Enterprise.
        documentLibraryPageCL = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        documentLibraryPageCL = documentLibraryPageCL.selectFolder(folderName).render();
        ContentDetails contentDetailsCl = ShareUserSitePage.getInLineEditContentDetails(hybridDrone, fileName);
        String contentCl = contentDetailsCl.getContent();
        Assert.assertTrue(contentCl.equals(contentEdited), "Content is not modified");
        detailsPage = documentLibraryPageCL.selectFile(fileName).render();
        VersionDetails versionDetails = detailsPage.getCurrentVersionDetails();
        Assert.assertFalse(versionDetails.getVersionNumber().equals("1.0"), "Version is not updated");
        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.refreshSharePage(drone);
        documentLibraryPage.getFileDirectoryInfo(fileName).selectRequestSync().render();
        waitForSync(drone, fileName, opSiteName);

        ContentDetails contentDetailsOp = ShareUserSitePage.getInLineEditContentDetails(drone, fileName);
        String contentOp = contentDetailsOp.getContent();
        Assert.assertTrue(contentOp.equals(contentEdited), "Content is not modified");
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).getVersionInfo().equals("1.0"), "Version is not updated");

    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15482() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser };
        String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };
        String opFileName = getFileName(testName) + ".txt";
        String[] opFileInfo = new String[] { opFileName };
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.uploadFileInFolder(drone, opFileInfo).render();
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }

    /**
     * AONE-15481 Add a document and sync while the cloud network is down
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
    public void AONE_15482() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser", testDomain);
        String opSiteName = getSiteName(testName) + "-OP";
        String opFileName = getFileName(testName) + ".txt";
        String cloudSiteName = getSiteName(testName) + "-CL";
        String cloudUser1 = getUserNameForDomain(testName + "clUser", testDomain);
        WebDrone thisDrone;
        setupCustomDrone(WebDroneType.HybridDrone);
        thisDrone = customDrone;

        // ---- Step 1 ----
        // ---- Step action ----
        // Add a document and sync it to Cloud, whilst syncing close browser.
        // ---- Expected results ----
        // File is synced to Cloud.
        ShareUser.login(thisDrone, opUser1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(thisDrone, opSiteName).render();
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(opFileName).selectSyncToCloud().render();

        destinationAndAssigneePage.selectNetwork(testDomain);
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectFolder("Documents");
        destinationAndAssigneePage.clickSyncButton();
        if (documentLibraryPage.isSyncMessagePresent())
        {
            thisDrone.closeWindow();
        }

        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        waitAndCheckIfVisible(hybridDrone, documentLibraryPage, opFileName);
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(opFileName).isCloudSynced(), "File is not synced");
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15484() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser };
        String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };
        String cloudUser2 = getUserNameForDomain(testName + "clUserInv", testDomain);
        String[] cloudUserInfo2 = new String[] { cloudUser2 };
        String folderName = getFolderName(testName);
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        String opFileName1 = getFileName(testName) + "1.txt";
        String[] opFileInfo1 = new String[] { opFileName1 };
        String opFileName2 = getFileName(testName) + "2.txt";
        String[] opFileInfo2 = new String[] { opFileName2 };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo2);

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUserSitePage.createFolder(hybridDrone, folderName, "").render();

        // One user should be invited to the site as Collaborator or Contributor
        CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, cloudUser, cloudUser2, getSiteShortname(cloudSiteName), "SiteContributor", "");
        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser2, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.uploadFileInFolder(drone, opFileInfo1).render();
        ShareUser.uploadFileInFolder(drone, opFileInfo2).render();
    }

    /**
     * AONE-15484:Remove the write access to Cloud.
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
    public void AONE_15484() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
        String cloudUser2 = getUserNameForDomain(testName + "clUserInv", testDomain);
        String folderName = getFolderName(testName);
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        String opFileName1 = getFileName(testName) + "1.txt";
        String opFileName2 = getFileName(testName) + "2.txt";

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

        // ---- Step 1 ----
        // ---- Step action ----
        // Set cursor to the document and expand More+ menu
        // ---- Expected results ----
        // List of actions is appeared for document and 'sync to cloud' option available
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(opFileName1).isSyncToCloudLinkPresent(), "Sync to cloud not displayed");

        // ---- Step 2 ----
        // ---- Step action ----
        // Choose 'Sync to Cloud' option
        // ---- Expected results ----
        // Pop-up window to select target cloud location appears
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(opFileName1).selectSyncToCloud().render();
        Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + opFileName1 + " to The Cloud"), "Title is not displyed");

        // ---- Step 3 ----
        // ---- Step action ----
        // Verify information displayed in window
        // ---- Expected results ----
        // All available networks are displayed correctly and can be chosen
        Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(testDomain), "Network " + testDomain + " is not displayed");
        Assert.assertTrue(destinationAndAssigneePage.isSiteDisplayed(cloudSiteName), "Site " + cloudSiteName + " is not displyed");

        // ---- Step 4 ----
        // ---- Step action ----
        // Choose any network --available site--document library and press OK button
        // ---- Expected results ----
        // Notification about file is synced successfully appears.
        destinationAndAssigneePage.selectNetwork(testDomain);
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectFolder(folderName);
        destinationAndAssigneePage.clickSyncButton();
        Assert.assertTrue(documentLibraryPage.isSyncMessagePresent(), "Sync message not displayed");

        // ---- Step 5 ----
        // ---- Step action ----
        // Remove the write access to that folder in the Cloud (Manage permissions)
        // ---- Expected results ----
        // The write access is removed successfully.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();

        ManagePermissionsPage mangPermPage = documentLibraryPage.getFileDirectoryInfo(folderName).selectManagePermission().render();
        UserProfile userProfile = new UserProfile();
        userProfile.setUsername(cloudUser2);
        mangPermPage = mangPermPage.selectAddUser().searchAndSelectUser(userProfile).render();
        mangPermPage.setAccessType(cloudUser2, UserRole.CONSUMER);
        mangPermPage = mangPermPage.toggleInheritPermission(false, ButtonType.Yes);
        mangPermPage.selectSave();
        ShareUser.logout(hybridDrone);

        // ---- Step 6 ----
        // ---- Step action ----
        // The user still have write access in Alfresco Enterprise. Try to sync the other document to that folder
        // ---- Expected results ----
        // Sync fails (wait a little bit, status should change from pending to failed).
        // TODO: Modify step 6 in test link accordingly to assertion
        ShareUser.refreshSharePage(drone);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(opFileName2).selectSyncToCloud().render();
        destinationAndAssigneePage.selectNetwork(testDomain);
        destinationAndAssigneePage.selectSite(cloudSiteName);
        Assert.assertFalse(destinationAndAssigneePage.isSyncPermitted(folderName), "Sync is permited");
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15485() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
        String[] userInfo1 = new String[] { opUser1 };
        String opUser2 = getUserNameForDomain(testName + "opUser2", testDomain);
        String[] userInfo2 = new String[] { opUser2 };
        String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
        String[] cloudUserInfo = new String[] { cloudUser };
        ContentDetails contentDetails1 = new ContentDetails(testName);
        ContentDetails contentDetails2 = new ContentDetails(testName + "2");
        ContentType contentType = ContentType.PLAINTEXT;

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo2);

        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

        // Any file is created/uploaded into the Document Library of the site
        ShareUser.createContent(drone, contentDetails1, contentType);
        ShareUser.createContent(drone, contentDetails2, contentType);

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // User2 should be invited to the site as Collaborator
        ShareUserMembers.inviteUserToSiteWithRole(drone, opUser1, opUser2, getSiteShortname(opSiteName), UserRole.COLLABORATOR);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // Login to User2, set up the cloud sync
        ShareUser.login(drone, opUser2, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

        ShareUser.logout(drone);
    }

    /**
     * AONE-15485 Remove the write access in on-premise.
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 400000)
    public void AONE_15485() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
        String opUser2 = getUserNameForDomain(testName + "opUser2", testDomain);
        String cloudSiteName = getSiteName(testName) + "-CL";
        String opSiteName = getSiteName(testName) + "-OP";
        String fileName = testName;
        String fileName2 = testName + "2";

        ShareUser.login(drone, opUser2, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

        // ---- Step 1 ----
        // ---- Step action ----
        // Set cursor to the document and expand More+ menu
        // ---- Expected results ----
        // List of actions is appeared for document and 'sync to cloud' option available
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isSyncToCloudLinkPresent(), "Sync to cloud is not displayed");

        // ---- Step 2 ----
        // ---- Step action ----
        // Choose 'Sync to Cloud' option
        // ---- Expected results ----
        // Pop-up window to select target cloud location appears
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(fileName).selectSyncToCloud().render();
        Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + fileName + " to The Cloud"));

        // ---- Step 3 ----
        // ---- Step action ----
        // Verify information displayed in window
        // ---- Expected results ----
        // All available networks are displayed correctly and can be chosen
        Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(testDomain), "Test domanin " + testDomain + " is not displayed");
        Assert.assertTrue(destinationAndAssigneePage.isSiteDisplayed(cloudSiteName), "Site " + cloudSiteName + " is not displayed");

        // ---- Step 4 ----
        // ---- Step action ----
        // Choose any network --available site--document library and press OK button
        // ---- Expected results ----
        // Notification about file is synced successfully appears.
        destinationAndAssigneePage.selectNetwork(testDomain);
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectFolder("Documents");
        destinationAndAssigneePage.clickSyncButton();
        Assert.assertTrue(documentLibraryPage.isSyncMessagePresent(), "Sync message is not displayed");

        // ---- Step 5 ----
        // ---- Step action ----
        // Uncync the file by User1
        // ---- Expected results ----
        // The file is uncynced
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        documentLibraryPage.getFileDirectoryInfo(fileName).selectUnSyncAndRemoveContentFromCloud(false);

        // ---- Step 6 ----
        // ---- Step action ----
        // Remove the write access to User2 to that file in on-premise (Manage permissions)
        // ---- Expected results ----
        // The write access is removed successfully.
        documentLibraryPage.render();
        ManagePermissionsPage mangPermPage = documentLibraryPage.getFileDirectoryInfo(fileName).selectManagePermission().render();
        UserProfile userProfile = new UserProfile();
        userProfile.setUsername(opUser2);
        mangPermPage = mangPermPage.selectAddUser().searchAndSelectUser(userProfile).render();
        mangPermPage.setAccessType(opUser2, UserRole.SITECONSUMER);
        mangPermPage = mangPermPage.toggleInheritPermission(false, ButtonType.Yes);
        mangPermPage.selectSave();
        ShareUser.logout(drone);

        // ---- Step 7 ----
        // ---- Step action ----
        // Try to sync the file by User2 who now has not write access to the file
        // ---- Expected results ----
        // 'Sync to Cloud' action is absent fo the file
        ShareUser.login(drone, opUser2, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isSyncToCloudLinkPresent(), "Sync to cloud is displayed");

        // ---- Step 8 ----
        // ---- Step action ----
        // Try to sync the other document by User2;
        // ---- Expected results ----
        // Sync pass
        documentLibraryPage.getFileDirectoryInfo(fileName2).selectSyncToCloud().render();
        destinationAndAssigneePage.selectNetwork(testDomain);
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectFolder("Documents");
        destinationAndAssigneePage.clickSyncButton();
        Assert.assertTrue(documentLibraryPage.isSyncMessagePresent(), "Sync message not displayed");
        ShareUser.logout(drone);
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15486() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser };
        String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
    }

    /**
     * AONE-15486 Creation new folder.
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
    public void AONE_15486() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        String cloudFolderName = getFolderName(testName) + "1-CL";
        ContentDetails contentDetails = new ContentDetails(testName + "1");
        ContentType contentType = ContentType.PLAINTEXT;

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

        // Any file is created/uploaded into the Document Library
        ShareUser.createContent(drone, contentDetails, contentType);
        String fileName = testName + "1";

        // ---- Step 1 ----
        // ---- Step action ----
        // Set cursor to the document and expand More+ menu
        // ---- Expected results ----
        // List of actions is appeared for document and 'sync to cloud' option available
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isSyncToCloudLinkPresent());

        // ---- Step 2 ----
        // ---- Step action ----
        // Choose 'Sync to Cloud' option
        // ---- Expected results ----
        // Pop-up window to select target cloud location appears
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(fileName).selectSyncToCloud().render();
        Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + fileName + " to The Cloud"));

        // ---- Step 3 ----
        // ---- Step action ----
        // Verify information displayed in window
        // ---- Expected results ----
        // All available networks are displayed correctly and can be chosen
        Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(testDomain));
        Assert.assertTrue(destinationAndAssigneePage.isSiteDisplayed(cloudSiteName));

        // ---- Step 4 ----
        // ---- Step action ----
        // Choose any network --available site--document library and keep the folder selection window open.
        // ---- Expected results ----
        // Document Library is displayed. The folder selection window is opened.
        destinationAndAssigneePage.selectNetwork(testDomain);
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectFolder("Documents");

        // ---- Step 5 ----
        // ---- Step action ----
        // Create a new folder under document library of exact same site above selected in another tab.
        // ---- Expected results ----
        // The folder is created.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        ShareUserSitePage.createFolder(hybridDrone, cloudFolderName, "").render();
        ShareUser.logout(hybridDrone);

        // ---- Step 6 ----
        // ---- Step action ----
        // Check in the first tab if the folder is displayed immediatily.
        // ---- Expected results ----
        // May not be displayed.
        Assert.assertFalse(destinationAndAssigneePage.isFolderDisplayed(cloudFolderName));

        // ---- Step 7 ----
        // ---- Step action ----
        // Close the popup and reopening it again. Select the newly created folder.
        // ---- Expected results ----
        // It should be displayed now.
        destinationAndAssigneePage.selectCancelButton();
        documentLibraryPage.getFileDirectoryInfo(fileName).selectSyncToCloud().render();
        destinationAndAssigneePage.selectSite(cloudSiteName);
        Assert.assertTrue(destinationAndAssigneePage.isFolderDisplayed(cloudFolderName));
        ShareUser.logout(drone);
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15512() throws Exception
    {
        String testName = getTestName() + uniqueRun + "A1";
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser };
        String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };
        ContentDetails contentDetails = new ContentDetails(testName);
        ContentType contentType = ContentType.PLAINTEXT;
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

        // Document Library page of the created Site is opened
        ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

        // A file is created/uploaded into the Document Library of the site
        ShareUser.createContent(drone, contentDetails, contentType);
    }

    /**
     * AONE-15512 Same filename exists.
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15512() throws Exception
    {
        String testName = getTestName() + uniqueRun + "A1";;
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        String fileName = testName;

        // ---- Step 1 ----
        // ---- Step action ----
        // Sync a file to Cloud.
        // ---- Expected results ----
        // The file is synced successfully.
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(fileName).selectSyncToCloud().render();
        destinationAndAssigneePage.selectNetwork(testDomain);
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectFolder("Documents");
        destinationAndAssigneePage.clickSyncButton();
        Assert.assertTrue(documentLibraryPage.isSyncMessagePresent(), "Sync Message not displayed");
        Assert.assertTrue(checkIfContentIsSynced(drone, fileName), "Sync Failed");

        // ---- Step 2 ----
        // ---- Step action ----
        // Sync this file to the same location.
        // ---- Expected results ----
        // You cannot sync a file to a target location where same file name exists.;
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        documentLibraryPage.getFileDirectoryInfo(fileName).selectUnSyncAndRemoveContentFromCloud(false);
        ShareUser.refreshSharePage(drone);
        documentLibraryPage.render();
        destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(fileName).selectSyncToCloud().render();
        destinationAndAssigneePage.selectNetwork(testDomain);
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectFolder("Documents");
        destinationAndAssigneePage.clickSyncButton();
        documentLibraryPage.isSyncMessagePresent();
        Assert.assertTrue(checkIfSyncFailed(drone, fileName), "Sync is created");
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15513() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser };
        String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };
        ContentDetails contentDetails = new ContentDetails(testName + ".txt");
        ContentDetails contentDetails2 = new ContentDetails(testName + ".html");
        ContentType contentType = ContentType.PLAINTEXT;
        ContentType contentType2 = ContentType.HTML;

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

        // Document Library page of the created Site is opened
        ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        // Two files with same name but different types are created/uploaded into the Document Library of the site
        ShareUser.createContent(drone, contentDetails, contentType);
        ShareUser.createContent(drone, contentDetails2, contentType2);
        ShareUser.logout(drone);
    }

    /**
     * AONE-15513 Same filename exists. Different file types.
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 400000)
    public void AONE_15513() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        String fileName = testName + ".txt";
        String fileName2 = testName + ".html";

        // ---- Step 1 ----
        // ---- Step action ----
        // Sync a file to Cloud.
        // ---- Expected results ----
        // The file is synced successfully.
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(fileName).selectSyncToCloud().render();
        destinationAndAssigneePage.selectNetwork(testDomain);
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectFolder("Documents");
        destinationAndAssigneePage.clickSyncButton();
        Assert.assertTrue(documentLibraryPage.isSyncMessagePresent(), "Sync Message is not displayed");
        Assert.assertTrue(checkIfContentIsSynced(drone, fileName), "Sync failed");

        // ---- Step 2 ----
        // ---- Step action ----
        // Sync a file with the same filename and diffrent file type to the same location.
        // ---- Expected results ----
        // The file is synced to Cloud.
        documentLibraryPage = (DocumentLibraryPage) drone.getCurrentPage().render();
        documentLibraryPage.getFileDirectoryInfo(fileName2).selectSyncToCloud().render();
        destinationAndAssigneePage.selectNetwork(testDomain);
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectFolder("Documents");
        destinationAndAssigneePage.clickSyncButton();
        Assert.assertTrue(documentLibraryPage.isSyncMessagePresent(), "Sync Message is not displayed");
        Assert.assertTrue(checkIfContentIsSynced(drone, fileName), "Sync failed");
        ShareUser.logout(drone);
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPageCL = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, fileName), "File is not visible");
        Assert.assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, fileName2), "File is not visible");
        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15514() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser };
        String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };
        String opFolderName = getFolderName(testName);
        String clFolderName = getFolderName(testName);
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

        // Document Library page of the created Site is opened
        ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        // A folder is created into the Document Library of the site
        ShareUserSitePage.createFolder(drone, opFolderName, "").render();
        ShareUser.logout(drone);

        // A folder is created into the Document Library of the Cloud site
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);
        ShareUserSitePage.createFolder(hybridDrone, clFolderName, "").render();
        ShareUser.logout(hybridDrone);
    }

    /**
     * AONE-15514 Sync folder with the same name as already exists in Cloud
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 400000)
    public void AONE_15514() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String opFolderName = getFolderName(testName);
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        // ---- Step 1 ----
        // ---- Step action ----
        // Set cursor to the folder and expand More+ menu
        // ---- Expected results ----
        // List of actions is appeared for folder and 'sync to cloud' option available
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(opFolderName).isSyncToCloudLinkPresent(), "Sync to cloud not displayed");

        // ---- Step 2 ----
        // ---- Step action ----
        // Choose 'Sync to Cloud' option from More+ menu
        // ---- Expected results ----
        // Pop-up window to select target cloud location appears
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(opFolderName).selectSyncToCloud().render();
        Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + opFolderName + " to The Cloud"));
        Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(testDomain), "Network is not displayed");
        Assert.assertTrue(destinationAndAssigneePage.isSiteDisplayed(cloudSiteName), "Cloud Site not displayed");

        // ---- Step 3 ----
        // ---- Step action ----
        // Choose target location in the Cloud where folder exists and press OK button
        // ---- Expected results ----
        // Sync failed
        destinationAndAssigneePage.selectNetwork(testDomain);
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectFolder("Documents");
        destinationAndAssigneePage.clickSyncButton();
        Assert.assertTrue(documentLibraryPage.isSyncMessagePresent(), "Message not displayed");
        Assert.assertTrue(checkIfSyncFailed(drone, opFolderName), "Sync is created");
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15515() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo = new String[] { opUser };
        String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
        String[] cloudUserInfo = new String[] { cloudUser };
        String opFolderName = getFolderName(testName);
        String opSubFolderName = getFolderName("Sub-" + testName);
        ContentDetails contentDetails = new ContentDetails(testName);
        ContentType contentType = ContentType.PLAINTEXT;

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo);

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

        // Document Library page of the created Site is opened
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        // Folder with some sub folders and files in it is created under the Document Library
        ShareUserSitePage.createFolder(drone, opFolderName, "").render();
        documentLibraryPage.selectFolder(opFolderName);
        ShareUser.createContentInCurrentFolder(drone, contentDetails, contentType, documentLibraryPage);
        ShareUser.createFolderInFolder(drone, opSubFolderName, "", opFolderName);
        ShareUser.logout(drone);
    }

    /**
     * AONE-15515 Sync a folder with files and sub folders in it
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 500000)
    public void AONE_15515() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
        String opFolderName = getFolderName(testName);
        String opSubFolderName = getFolderName("Sub-" + testName);
        String fileName = testName;
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        // ---- Step 1 ----
        // ---- Step action ----
        // Set cursor to the folder and expand More+ menu
        // ---- Expected results ----
        // List of actions is appeared for folder and 'sync to cloud' option available
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(opFolderName).isSyncToCloudLinkPresent(), "Sync to cloud is not displayed");

        // ---- Step 2 ----
        // ---- Step action ----
        // Choose 'Sync to Cloud' option from More+ menu
        // ---- Expected results ----
        // Pop-up window to select target cloud location appears
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(opFolderName).selectSyncToCloud().render();
        Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + opFolderName + " to The Cloud"));
        Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(testDomain), "Domain is not displayed");
        Assert.assertTrue(destinationAndAssigneePage.isSiteDisplayed(cloudSiteName), "Site " + cloudSiteName + " is not displayed");

        // ---- Step 3 ----
        // ---- Step action ----
        // Choose target location in the Cloud (network->site->document library) and press OK button
        // ---- Expected results ----
        // 'Sync created' notification appears
        destinationAndAssigneePage.selectNetwork(testDomain);
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectFolder("Documents");
        destinationAndAssigneePage.clickSyncButton();
        Assert.assertTrue(documentLibraryPage.isSyncMessagePresent(), "Sync message not displayed");
        Assert.assertTrue(checkIfContentIsSynced(drone, opFolderName), "Sync failed");
        ShareUser.logout(drone);

        // ---- Step 4 ----
        // ---- Step action ----
        // Go to Cloud location (network->site->document library) set in previous step
        // ---- Expected results ----
        // Folder for which sync action was applied is displayed.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPageCL = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertTrue(documentLibraryPageCL.isFileVisible(opFolderName));
        documentLibraryPageCL.selectFolder(opFolderName);
        Assert.assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, opSubFolderName), "File is not visible");
        Assert.assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, fileName), "File is not visible");
        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15516() throws Exception
    {
        String testName = getTestName() + uniqueRun + "A1";;
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo = new String[] { opUser };
        String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
        String[] cloudUserInfo = new String[] { cloudUser };
        String opFolderName = getFolderName(testName);
        String opSubFolderName = getFolderName("Sub-" + testName);
        ContentDetails contentDetails = new ContentDetails(testName);
        ContentType contentType = ContentType.PLAINTEXT;
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo);

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

        // Document Library page of the created Site is opened
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        // Folder with some sub folders and files in it is created under the Document Library
        ShareUserSitePage.createFolder(drone, opFolderName, "").render();
        documentLibraryPage.selectFolder(opFolderName);
        ShareUser.createContentInCurrentFolder(drone, contentDetails, contentType, documentLibraryPage);
        ShareUser.createFolderInFolder(drone, opSubFolderName, "", opFolderName);

        // Sync the above created folders and file to the Cloud
        ShareUser.selectMyDashBoard(drone);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(opFolderName).selectSyncToCloud().render();
        destinationAndAssigneePage.selectNetwork(testDomain);
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectFolder("Documents");
        destinationAndAssigneePage.clickSyncButton();
        documentLibraryPage.isSyncMessagePresent();
        checkIfContentIsSynced(drone, opFolderName);
    }

    /**
     * AONE-15516 Rename synced a non-empty folder in cloud. Make some changes in on-premise
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 600000)
    public void AONE_15516() throws Exception
    {
        String testName = getTestName() + uniqueRun + "A1";;
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
        String opFolderName = getFolderName(testName);
        String opSubFolderName = getFolderName("Sub-" + testName);
        String fileName = testName;
        String fileNameEditedCL = testName + "-editedCL";
        String fileNameEditedOP = testName + "-editedOP";
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        // ---- Step 1 ----
        // ---- Step action ----
        // Go to Cloud target location (network-site-document library) where folder was synced in preconditions
        // ---- Expected results ----
        // Document library page of site in Cloud is opened and synced folder is displayed on it
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPageCL = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertTrue(documentLibraryPageCL.isFileVisible(opFolderName), "Folder is not synced");

        // ---- Step 2 ----
        // ---- Step action ----
        // Set cursor to the synced folder and choose Edit properties option
        // ---- Expected results ----
        // Pop up window where it's possible to change some properties including name appears
        EditDocumentPropertiesPage editCL = documentLibraryPageCL.getFileDirectoryInfo(opFolderName).selectEditProperties().render();
        Assert.assertTrue(editCL.isEditPropertiesVisible(), "Edit Properties not displayed");
        Assert.assertEquals(editCL.getName(), opFolderName, "Incorrect folder name");

        // ---- Step 3 ----
        // ---- Step action ----
        // Change name in name field and click on save button
        // ---- Expected results ----
        // Folder is renamed successfully in Cloud
        editCL.setName(testName + "-editedCL");
        editCL.clickSave();
        documentLibraryPageCL.render();
        Assert.assertTrue(documentLibraryPageCL.isFileVisible(fileNameEditedCL), "Folder is not edited");
        ShareUser.logout(hybridDrone);

        // ---- Step 4 ----
        // ---- Step action ----
        // Log in to Alfresco Share (on-premise)
        // ---- Expected results ----
        // User is logged in successfully
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

        // ---- Step 5 ----
        // ---- Step action ----
        // Go to site-document library-synced folder
        // ---- Expected results ----
        // Synced folder is opened, sub folders and files are displayed on the page
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertTrue(waitAndCheckIfVisible(drone, documentLibraryPage, fileNameEditedCL), "File is not visible");
        documentLibraryPage = documentLibraryPage.selectFolder(fileNameEditedCL).render();
        Assert.assertTrue(waitAndCheckIfVisible(drone, documentLibraryPage, opSubFolderName), "File is not visible");
        Assert.assertTrue(waitAndCheckIfVisible(drone, documentLibraryPage, fileName), "File is not visible");

        // ---- Step 6 ----
        // ---- Step action ----
        // Change some files and sub folders
        // ---- Expected results ----
        // Changes are saved successfully in on-premise. Sync is not failed
        EditDocumentPropertiesPage editOP = documentLibraryPage.getFileDirectoryInfo(opSubFolderName).selectEditProperties().render();
        editOP.setName(testName + "-editedOP");
        editOP.clickSave();
        ShareUser.refreshSharePage(drone);
        documentLibraryPage.render();
        Assert.assertTrue(waitAndCheckIfVisible(drone, documentLibraryPage, fileNameEditedOP), "File is not visible");
        SyncInfoPage syncInfoPage = new SyncInfoPage(drone);
        documentLibraryPage.getFileDirectoryInfo(fileNameEditedOP).clickOnViewCloudSyncInfo();
        Assert.assertFalse(syncInfoPage.isFailedInfoDisplayed(), "Sync failed");
        ShareUser.logout(drone);

        // ---- Step 7 ----
        // ---- Step action ----
        // Go to Cloud location (network-site-document library-synced folder) again
        // ---- Expected results ----
        // Synced folder is opened successfully in Cloud. Changes made for files and sub folders in on-premise are applied successfully
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, fileNameEditedCL), "File is not visible");
        documentLibraryPageCL.selectFolder(fileNameEditedCL);
        Assert.assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, fileNameEditedOP), "File is not visible");
        Assert.assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, fileName), "File is not visible");
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15517() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo = new String[] { opUser };
        String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
        String[] cloudUserInfo = new String[] { cloudUser };
        String opFolderName = getFolderName(testName);
        String opSubFolderName = getFolderName("Sub-" + testName);
        ContentDetails contentDetails = new ContentDetails(testName);
        ContentType contentType = ContentType.PLAINTEXT;
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo);

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

        // Document Library page of the created Site is opened
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        // Folder with some sub folders and files in it is created under the Document Library
        ShareUserSitePage.createFolder(drone, opFolderName, "").render();
        documentLibraryPage.selectFolder(opFolderName);
        ShareUser.createContentInCurrentFolder(drone, contentDetails, contentType, documentLibraryPage);
        ShareUser.createFolderInFolder(drone, opSubFolderName, "", opFolderName);

        // Sync the above created folders and file to the Cloud
        ShareUser.selectMyDashBoard(drone);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(opFolderName).selectSyncToCloud().render();
        destinationAndAssigneePage.selectNetwork(testDomain);
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectFolder("Documents");
        destinationAndAssigneePage.clickSyncButton();
        documentLibraryPage.isSyncMessagePresent();
        checkIfContentIsSynced(drone, opFolderName);
    }

    /**
     * AONE-15517 Rename synced a non-empty folder in on-premise. Sync with changes from Cloud
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 500000)
    public void AONE_15517() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
        String opFolderName = getFolderName(testName);
        String opFolderNameEdited = getFolderName(testName + "-edit");
        String opSubFolderName = getFolderName("Sub-" + testName);
        String fileName = testName;
        String fileNameEditedCL = testName + "-editedCL";
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        DashBoardPage dashBoardPage = new DashBoardPage(drone);

        // ---- Step 1 ----
        // ---- Step action ----
        // Log in to Alfresco Share (on-premise)
        // ---- Expected results ----
        // User is logged in successfully
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        Assert.assertTrue(dashBoardPage.isLoggedIn(), "User is not logged in");

        // ---- Step 2 ----
        // ---- Step action ----
        // Go to site-document library
        // ---- Expected results ----
        // Document library page of site in on-premise is opened and synced folder is displayed on it
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);
        Assert.assertTrue(documentLibraryPage.isFileVisible(opFolderName), "Folder is not displayed");

        // ---- Step 3 ----
        // ---- Step action ----
        // Set cursor to the synced folder and choose Edit properties option
        // ---- Expected results ----
        // Pop up window where it's possible to change some properties including name appears
        EditDocumentPropertiesPage editOP = documentLibraryPage.getFileDirectoryInfo(opFolderName).selectEditProperties().render();
        Assert.assertTrue(editOP.isEditPropertiesVisible(), "Edit properties is not visible");
        Assert.assertEquals(editOP.getName(), opFolderName, "Incorrect folder name");

        // ---- Step 4 ----
        // ---- Step action ----
        // Change name in name field and click on save button
        // ---- Expected results ----
        // Folder is renamed successfully in Cloud
        editOP.setName(opFolderNameEdited);
        editOP.clickSave();
        ShareUser.refreshSharePage(drone);
        documentLibraryPage.render();
        Assert.assertTrue(documentLibraryPage.isFileVisible(opFolderNameEdited), "File " + opFolderNameEdited + " is not visible");
        ShareUser.logout(drone);

        // ---- Step 5 ----
        // ---- Step action ----
        // Go to Cloud location (network-site-document library-synced folder)
        // ---- Expected results ----
        // Synced folder is opened successfully in Cloud, ub folders and files are displayed on the page
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPageCL = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, opFolderNameEdited), "File is not visible");
        documentLibraryPageCL.selectFolder(opFolderNameEdited);
        Assert.assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, opSubFolderName), "File is not visible");
        Assert.assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, fileName), "File is not visible");

        // ---- Step 6 ----
        // ---- Step action ----
        // Make changes to some files and sub folders and click on save button
        // ---- Expected results ----
        // Changes are saved successfully in cloud. Sync is passed
        EditDocumentPropertiesPage editCL = documentLibraryPageCL.getFileDirectoryInfo(fileName).selectEditProperties().render();
        editCL.setName(testName + "-editedCL");
        editCL.clickSave();
        Assert.assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, fileNameEditedCL), "File is not visible");
        ShareUser.logout(hybridDrone);

        // ---- Step 7 ----
        // ---- Step action ----
        // Go to location location site-document library-synced folder in on-premise again
        // ---- Expected results ----
        // Synced folder is opened successfully in Cloud. Changes made for files and sub folders in cloud are applied successfully
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertTrue(waitAndCheckIfVisible(drone, documentLibraryPage, opFolderNameEdited), "File is not visible");
        documentLibraryPage = documentLibraryPage.selectFolder(opFolderNameEdited).render();
        Assert.assertTrue(waitAndCheckIfVisible(drone, documentLibraryPage, opSubFolderName), "File is not visible");
        Assert.assertTrue(waitAndCheckIfVisible(drone, documentLibraryPage, fileNameEditedCL), "File is not visible");
        SyncInfoPage syncInfoPageOP = documentLibraryPage.getFileDirectoryInfo(fileNameEditedCL).clickOnViewCloudSyncInfo().render();
        Assert.assertFalse(syncInfoPageOP.isFailedInfoDisplayed(), "Sync failed");
        syncInfoPageOP.clickOnCloseButton();
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15518() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo = new String[] { opUser };
        String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
        String[] cloudUserInfo = new String[] { cloudUser };
        String opFolderName = getFolderName(testName);
        String opSubFolderName = getFolderName("Sub-" + testName);
        ContentDetails contentDetails = new ContentDetails(testName);
        ContentType contentType = ContentType.PLAINTEXT;
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo);

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login to User, set up the cloud sync
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

        // Document Library page of the created Site is opened
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        // Folder with some sub folders and files in it is created under the Document Library
        ShareUserSitePage.createFolder(drone, opFolderName, "").render();
        documentLibraryPage.selectFolder(opFolderName);
        ShareUser.createContentInCurrentFolder(drone, contentDetails, contentType, documentLibraryPage);
        ShareUser.createFolderInFolder(drone, opSubFolderName, "", opFolderName);

        // Sync the above created folders and file to the Cloud
        ShareUser.selectMyDashBoard(drone);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(opFolderName).selectSyncToCloud().render();
        destinationAndAssigneePage.selectNetwork(testDomain);
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectFolder("Documents");
        destinationAndAssigneePage.clickSyncButton();
        documentLibraryPage.isSyncMessagePresent();
        checkIfContentIsSynced(drone, opFolderName);
    }

    /**
     * AONE-15518 Add file/folder to non-empty synced folder in on-premise
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
    public void AONE_15518() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
        String opFolderName = getFolderName(testName);
        String opFolderNameAdd = getFolderName(testName + "Add");
        String opSubFolderName = getFolderName("Sub-" + testName);
        String fileName = testName;
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        DashBoardPage dashBoardPage = new DashBoardPage(drone);

        // ---- Step 1 ----
        // ---- Step action ----
        // Log in to Alfresco Share (on-premise)
        // ---- Expected results ----
        // User is logged in successfully
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        Assert.assertTrue(dashBoardPage.isLoggedIn(), "User is not logged in");

        // ---- Step 2 ----
        // ---- Step action ----
        // Go to site-document library-synced folder
        // ---- Expected results ----
        // Synced folder is opened, sub folders and files are displayed on the page
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertTrue(documentLibraryPage.isFileVisible(opFolderName), opFolderName + " is not displayed");
        documentLibraryPage.selectFolder(opFolderName);
        Assert.assertTrue(documentLibraryPage.isFileVisible(opSubFolderName), opSubFolderName + " is not displayed");
        Assert.assertTrue(documentLibraryPage.isFileVisible(fileName), fileName + " is not displayed");

        // ---- Step 3 ----
        // ---- Step action ----
        // Add any file/folder to opened folder
        // ---- Expected results ----
        // A file/folder is added successfully and displayed in synced folder in on-premise
        ShareUser.createFolderInFolder(drone, opFolderNameAdd, "", opFolderName);
        Assert.assertTrue(documentLibraryPage.isFileVisible(opFolderNameAdd), opFolderNameAdd + " is not displayed");
        ShareUser.logout(drone);

        // ---- Step 4 ----
        // ---- Step action ----
        // Go to Cloud location (network-site-document library-synced folder)
        // ---- Expected results ----
        // Synced folder is opened successfully in Cloud. Added in on-premise file/folder is created in cloud as well
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPageCL = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, opFolderName), "File is not visible");
        documentLibraryPageCL.selectFolder(opFolderName);
        Assert.assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, opSubFolderName), "File is not visible");
        Assert.assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, fileName), "File is not visible");
        Assert.assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, opFolderNameAdd), "File is not visible");
        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15519() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo = new String[] { opUser };
        String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
        String[] cloudUserInfo = new String[] { cloudUser };
        String opFolderName = getFolderName(testName);
        String opSubFolderName = getFolderName("Sub-" + testName);
        ContentDetails contentDetails = new ContentDetails(testName);
        ContentType contentType = ContentType.PLAINTEXT;
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo);

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login to User, set up the cloud sync
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

        // Document Library page of the created Site is opened
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        // Folder with some sub folders and files in it is created under the Document Library
        ShareUserSitePage.createFolder(drone, opFolderName, "").render();
        documentLibraryPage.selectFolder(opFolderName);
        ShareUser.createContentInCurrentFolder(drone, contentDetails, contentType, documentLibraryPage);
        ShareUser.createFolderInFolder(drone, opSubFolderName, "", opFolderName);

        // Sync the above created folders and file to the Cloud
        ShareUser.selectMyDashBoard(drone);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(opFolderName).selectSyncToCloud().render();
        destinationAndAssigneePage.selectNetwork(testDomain);
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectFolder("Documents");
        destinationAndAssigneePage.clickSyncButton();
        documentLibraryPage.isSyncMessagePresent();
        checkIfContentIsSynced(drone, opFolderName);
    }

    /**
     * AONE-15519 Add file/folder to non-empty synced folder in Cloud
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 400000)
    public void AONE_15519() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
        String opFolderName = getFolderName(testName);
        String clFolderNameAdd = getFolderName(testName + "Add");
        String opSubFolderName = getFolderName("Sub-" + testName);
        String fileName = testName;
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        DashBoardPage dashBoardPage = new DashBoardPage(drone);

        // ---- Step 1 ----
        // ---- Step action ----
        // Go to Cloud location (network-site-document library-synced folder)
        // ---- Expected results ----
        // Synced folder is opened successfully in Cloud, sub folders and files are displayed on the page correctly
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPageCL = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertTrue(documentLibraryPageCL.isFileVisible(opFolderName), opFolderName + " is not displayed");
        documentLibraryPageCL = documentLibraryPageCL.selectFolder(opFolderName).render();
        Assert.assertTrue(documentLibraryPageCL.isFileVisible(opSubFolderName), opSubFolderName + " is not displayed");
        Assert.assertTrue(documentLibraryPageCL.isFileVisible(fileName), fileName + " is not displayed");

        // ---- Step 2 ----
        // ---- Step action ----
        // Add any file/folder to opened folder
        // ---- Expected results ----
        // A file/folder is added successfully and displayed in synced folder in Cloud
        ShareUser.createFolderInFolder(hybridDrone, clFolderNameAdd, "", opFolderName);
        Assert.assertTrue(documentLibraryPageCL.isFileVisible(clFolderNameAdd), clFolderNameAdd + " is not displayed");
        ShareUser.logout(hybridDrone);

        // ---- Step 3 ----
        // ---- Step action ----
        // Log in to Alfresco Share (on-premise)
        // ---- Expected results ----
        // User is logged in successfully
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        Assert.assertTrue(dashBoardPage.isLoggedIn(), "OP User is not logged in");

        // ---- Step 4 ----
        // ---- Step action ----
        // Go to site-document library-synced folder
        // ---- Expected results ----
        // Synced folder is opened successfully in on-premise. Added in Cloud file/folder is created in on-premise as well
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);
        Assert.assertTrue(waitAndCheckIfVisible(drone, documentLibraryPage, opFolderName), "File is not visible");
        documentLibraryPage.selectFolder(opFolderName);
        Assert.assertTrue(waitAndCheckIfVisible(drone, documentLibraryPage, opSubFolderName), "File is not visible");
        Assert.assertTrue(waitAndCheckIfVisible(drone, documentLibraryPage, clFolderNameAdd), "File is not visible");
        Assert.assertTrue(waitAndCheckIfVisible(drone, documentLibraryPage, fileName), "File is not visible");
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15520() throws Exception
    {
        String testName = getTestName() + uniqueRun + "A1";;
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo = new String[] { opUser };
        String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
        String[] cloudUserInfo = new String[] { cloudUser };
        String opFolderName = getFolderName(testName);
        String opSubFolderName = getFolderName("Sub-" + testName);
        ContentDetails contentDetails = new ContentDetails(testName);
        ContentType contentType = ContentType.PLAINTEXT;
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo);

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        // Cloud user logs in and creates site.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

        // Document Library page of the created Site is opened
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        // Folder with some sub folders and files in it is created under the Document Library
        ShareUserSitePage.createFolder(drone, opFolderName, "").render();
        documentLibraryPage.selectFolder(opFolderName);
        ShareUser.createContentInCurrentFolder(drone, contentDetails, contentType, documentLibraryPage);
        ShareUser.createFolderInFolder(drone, opSubFolderName, "", opFolderName);

        // Sync the above created folders and file to the Cloud
        ShareUser.selectMyDashBoard(drone);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(opFolderName).selectSyncToCloud().render();
        destinationAndAssigneePage.selectNetwork(testDomain);
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectFolder("Documents");
        destinationAndAssigneePage.clickSyncButton();
        documentLibraryPage.isSyncMessagePresent();
        checkIfContentIsSynced(drone, opFolderName);
    }

    /**
     * AONE-15520 Update child file/folder in parent synced folder in on-premise
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
    public void AONE_15520() throws Exception
    {
        String testName = getTestName() + uniqueRun + "A1";;
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
        String opFolderName = getFolderName(testName);
        String opSubFolderName = getFolderName("Sub-" + testName);
        String fileName = testName;
        String fileNameEditedOP = testName + "-editedOP";
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        DashBoardPage dashBoardPage = new DashBoardPage(drone);

        // ---- Step 1 ----
        // ---- Step action ----
        // Log in to Alfresco Share (on-premise) as sync owner
        // ---- Expected results ----
        // User is logged in successfully
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        Assert.assertTrue(dashBoardPage.isLoggedIn(), "OP User is not logged in");

        // ---- Step 2 ----
        // ---- Step action ----
        // Go to site created in preconditions (step4)-document library
        // ---- Expected results ----
        // Document library page is opened successfully, all information including synced in preconditions (step6) folder is displayed correctly
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertTrue(documentLibraryPage.isFileVisible(opFolderName), opFolderName + " is not displayed");

        // ---- Step 3 ----
        // ---- Step action ----
        // Open synced folder
        // ---- Expected results ----
        // Folder is opened successfully, files and sub folders are displayed on the page
        documentLibraryPage = documentLibraryPage.selectFolder(opFolderName).render();
        Assert.assertTrue(documentLibraryPage.isFileVisible(opSubFolderName), opSubFolderName + " is not displayed");
        Assert.assertTrue(documentLibraryPage.isFileVisible(fileName), fileName + " is not displayed");

        // ---- Step 4 ----
        // ---- Step action ----
        // Update at least one of the files or sub folders in synced folder in on-premise
        // ---- Expected results ----
        // File/sub folders is updated successfully
        EditDocumentPropertiesPage editOP = documentLibraryPage.getFileDirectoryInfo(fileName).selectEditProperties().render();
        Assert.assertTrue(editOP.isEditPropertiesVisible(), "Edit Properties is not displayed");
        Assert.assertEquals(editOP.getName(), fileName, fileName + " is not displayed");
        editOP.setName(testName + "-editedOP");
        editOP.clickSave();
        ShareUser.refreshSharePage(drone);
        documentLibraryPage.render();
        Assert.assertTrue(documentLibraryPage.isFileVisible(fileNameEditedOP));

        // ---- Step 5 ----
        // ---- Step action ----
        // Go to Cloud target location where folder was synced in preconditions and open it to verify changes made to the file/sub folder are applied
        // ---- Expected results ----
        // Updates synced to cloud and changed in previous step file/sub folder is displayed correct
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPageCL = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, opFolderName), "File is not visible");
        documentLibraryPageCL.selectFolder(opFolderName);
        Assert.assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, opSubFolderName), "File is not visible");
        Assert.assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, fileNameEditedOP), "File is not visible");
        ShareUser.logout(hybridDrone);

    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15521() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo = new String[] { opUser };
        String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
        String[] cloudUserInfo = new String[] { cloudUser };
        String opFolderName = getFolderName(testName);
        String opSubFolderName = getFolderName("Sub-" + testName);
        ContentDetails contentDetails = new ContentDetails(testName);
        ContentType contentType = ContentType.PLAINTEXT;
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo);

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        // Cloud user logs in and creates site.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

        // Document Library page of the created Site is opened
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        // Folder with some sub folders and files in it is created under the Document Library
        ShareUserSitePage.createFolder(drone, opFolderName, "").render();
        documentLibraryPage.selectFolder(opFolderName);
        ShareUser.createContentInCurrentFolder(drone, contentDetails, contentType, documentLibraryPage);
        ShareUser.createFolderInFolder(drone, opSubFolderName, "", opFolderName);

        // Sync the above created folders and file to the Cloud
        ShareUser.selectMyDashBoard(drone);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(opFolderName).selectSyncToCloud().render();
        destinationAndAssigneePage.selectNetwork(testDomain);
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectFolder("Documents");
        destinationAndAssigneePage.clickSyncButton();
        documentLibraryPage.isSyncMessagePresent();
        checkIfContentIsSynced(drone, opFolderName);
    }

    /**
     * AONE-15521 Update child file/folder in parent synced folder in cloud
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
    public void AONE_15521() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
        String opFolderName = getFolderName(testName);
        String opSubFolderName = getFolderName("Sub-" + testName);
        String fileName = testName;
        String folderNameEditedCL = testName + "-editedCL";
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        DashBoardPage dashBoardPage = new DashBoardPage(drone);

        // ---- Step 1 ----
        // ---- Step action ----
        // Go to Cloud target location (nertwork-site-document library) where folder was synced in preconditions
        // ---- Expected results ----
        // Document library page of site created in cloud is opened successfully, all information including synced in preconditions (step6) folder is displayed
        // correctly
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPageCL = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertTrue(documentLibraryPageCL.isFileVisible(opFolderName), opFolderName + " is not displayed");

        // ---- Step 2 ----
        // ---- Step action ----
        // Open synced folder
        // ---- Expected results ----
        // Folder is opened successfully, files and sub folders are displayed on the page
        documentLibraryPageCL.selectFolder(opFolderName);
        Assert.assertTrue(documentLibraryPageCL.isFileVisible(opSubFolderName), opSubFolderName + " is not displayed");
        Assert.assertTrue(documentLibraryPageCL.isFileVisible(fileName), fileName + " is not displayed");

        // ---- Step 3 ----
        // ---- Step action ----
        // Update at least one of the files or sub folders in synced folder in cloud
        // ---- Expected results ----
        // File/sub folder is updated successfully
        EditDocumentPropertiesPage editCL = documentLibraryPageCL.getFileDirectoryInfo(opSubFolderName).selectEditProperties().render();
        Assert.assertTrue(editCL.isEditPropertiesVisible(), "Edit Properties is not displayed");
        Assert.assertEquals(editCL.getName(), opSubFolderName, opSubFolderName + " is not displayed");
        editCL.setName(testName + "-editedCL");
        editCL.clickSave();
        documentLibraryPageCL.render();
        Assert.assertTrue(documentLibraryPageCL.isFileVisible(folderNameEditedCL), folderNameEditedCL + " is not displayed");
        ShareUser.logout(hybridDrone);

        // ---- Step 4 ----
        // ---- Step action ----
        // Log in to Alfresco Share (on-premise)
        // ---- Expected results ----
        // User is logged in successfully
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        Assert.assertTrue(dashBoardPage.isLoggedIn(), "OP User is not logged in");

        // ---- Step 5 ----
        // ---- Step action ----
        // Go to site created in preconditions (step4)-document library-synced folder and verify changes made to the file/sub folder in step3 are applied
        // ---- Expected results ----
        // Synced folder is opened. Updates synced to on-premise and changed in step3 file/sub folder is displayed correct
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertTrue(documentLibraryPage.isFileVisible(opFolderName));
        documentLibraryPage.selectFolder(opFolderName);
        Assert.assertTrue(documentLibraryPage.isFileVisible(fileName), fileName + " is not displayed");
        Assert.assertTrue(waitAndCheckIfVisible(drone, documentLibraryPage, folderNameEditedCL), "Edited folder is not visible");
    }

    private boolean waitAndCheckIfVisible(WebDrone driver, DocumentLibraryPage docLib, String contentName)
    {
        int i = 0;
        boolean isVisible = docLib.isItemVisble(contentName);
        while (!isVisible)
        {
            webDriverWait(driver, timeToWait);
            docLib = refreshDocumentLibrary(driver).render();
            isVisible = docLib.isItemVisble(contentName);
            i++;
            if (i > retryCount)
            {
                break;
            }
        }
        return isVisible;
    }
}
