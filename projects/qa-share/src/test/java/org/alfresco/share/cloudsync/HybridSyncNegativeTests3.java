package org.alfresco.share.cloudsync;

import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.CopyOrMoveContentPage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryNavigation;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.site.document.SyncInfoPage;
import org.alfresco.po.share.site.document.VersionDetails;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author bogdan.bocancea
 */

@Listeners(FailedTestListener.class)
public class HybridSyncNegativeTests3 extends AbstractWorkflow
{
    private static final Logger logger = Logger.getLogger(HybridSyncNegativeTests3.class);

    private String testDomain1;
    private String testDomain;
    private String uniqueRun;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();

        logger.info("[Suite ] : Start Tests in: " + testName);
        testDomain1 = "negative1.test";
        testDomain = DOMAIN_HYBRID;
        uniqueRun = "TS9";
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15501() throws Exception
    {
        String testName = getTestName() + uniqueRun + "S2";
        String opUser1 = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser1 };

        String cloudUser1 = getUserNameForDomain(testName + "cloudUser", testDomain1);
        String[] cloudUserInfo1 = new String[] { cloudUser1 };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL1";
        String opFileName1 = getFileName(testName);
        String[] opFileInfo1 = new String[] { opFileName1, DOCLIB };
        String folderName = getFolderName(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain1, "1001");

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);
        ShareUser.createFolderInFolder(hybridDrone, folderName, folderName, DOCLIB);
        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.uploadFileInFolder(drone, opFileInfo1).render();
    }

    /**
     * AONE-15501:Move to different location in Cloud
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 500000)
    public void AONE_15501() throws Exception
    {
        String testName = getTestName() + uniqueRun + "S2";
        String opUser1 = getUserNameForDomain(testName + "opUser", testDomain);
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain1);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL1";
        String opFileName1 = getFileName(testName);
        String folderName = getFolderName(testName);
        String syncLocation = testDomain1 + ">" + cloudSiteName + ">" + "Documents" + ">" + folderName;

        // ---- Step 1 ----
        // ---- Step action ----
        // Sync files or folders to cloud.
        // ---- Expected results ----
        // Files or folder are synced.
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        documentLibraryPage.getFileDirectoryInfo(opFileName1).selectCheckbox();
        DocumentLibraryNavigation documentLibraryNavigation = new DocumentLibraryNavigation(drone);
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryNavigation.selectSyncToCloud().render();
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectSubmitButtonToSync().render();
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(opFileName1).isCloudSynced(), "Cloud is not synced");
        Assert.assertTrue(checkIfContentIsSynced(drone, opFileName1), "Content not synced");

        // ---- Step 2 ----
        // ---- Step action ----
        // Move the synced files or folders to different location in cloud
        // ---- Expected results ----
        // Files or folders are moved to different location in cloud.
        ShareUser.login(hybridDrone, cloudUser);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        documentLibraryPage.getFileDirectoryInfo(opFileName1).selectCheckbox();

        DocumentLibraryNavigation documentLibraryNavigationCloud = new DocumentLibraryNavigation(hybridDrone);
        CopyOrMoveContentPage moveToFolder = documentLibraryNavigationCloud.selectMoveTo().render();
        moveToFolder.selectPath(folderName).render().selectOkButton().render();
        documentLibraryPage.render();
        ShareUser.logout(hybridDrone);

        // ---- Step 3 ----
        // ---- Step action ----
        // Check the cloud location in on-premise.
        // ---- Expected results ----
        // New cloud location should be displayed and files synced without any errors.
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        SyncInfoPage syncInfoPage = documentLibraryPage.getFileDirectoryInfo(opFileName1).clickOnViewCloudSyncInfo().render();
        Assert.assertEquals(syncInfoPage.getCloudSyncLocation(), syncLocation, "Invalid location");

    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15502() throws Exception
    {
        String testName = getTestName() + uniqueRun + "S1";
        String opUser1 = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser1 };

        String cloudUser1 = getUserNameForDomain(testName + "cloudUser", testDomain1);
        String[] cloudUserInfo1 = new String[] { cloudUser1 };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL1";
        String opFileName1 = getFileName(testName);
        String[] opFileInfo1 = new String[] { opFileName1, DOCLIB };
        String folderName = getFolderName(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain1, "1001");

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);
        ShareUser.uploadFileInFolder(drone, opFileInfo1).render();
    }

    /**
     * AONE-15502:Move to different location in Alfresco Enterprise
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 500000)
    public void AONE_15502() throws Exception
    {
        String testName = getTestName() + uniqueRun + "S1";
        String opUser1 = getUserNameForDomain(testName + "opUser", testDomain);
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain1);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL1";
        String opFileName1 = getFileName(testName);
        String folderName = getFolderName(testName);

        // ---- Step 1 ----
        // ---- Step action ----
        // Sync files or folders to cloud.
        // ---- Expected results ----
        // Files or folder are synced.
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        documentLibraryPage.getFileDirectoryInfo(opFileName1).selectCheckbox();
        DocumentLibraryNavigation documentLibraryNavigation = new DocumentLibraryNavigation(drone);
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryNavigation.selectSyncToCloud().render();
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectSubmitButtonToSync().render();
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(opFileName1).isCloudSynced(), "File is not synced");

        // ---- Step 2 ----
        // ---- Step action ----
        // Move the synced files or folders to different location in cloud
        // ---- Expected results ----
        // Files or folders are moved to different location in cloud.
        documentLibraryPage.render();
        CopyOrMoveContentPage moveToFolder = documentLibraryNavigation.selectMoveTo().render();
        moveToFolder.selectPath(folderName).render().selectOkButton().render();
        documentLibraryPage.render();
        documentLibraryPage = documentLibraryPage.selectFolder(folderName).render();
        documentLibraryPage.getFileDirectoryInfo(opFileName1).selectRequestSync();
        Assert.assertTrue(checkIfContentIsSynced(drone, opFileName1), "File is not synced");

        // ---- Step 3 ----
        // ---- Step action ----
        // Check the cloud location in on-premise.
        // ---- Expected results ----
        // New cloud location should be displayed and files synced without any errors.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        DocumentDetailsPage docDetails = documentLibraryPage.selectFile(opFileName1).render();
        VersionDetails versionDetails = docDetails.getCurrentVersionDetails();
        String location = "'" + opSiteName + "/" + folderName + "/" + opFileName1 + "'";
        Assert.assertTrue(versionDetails.getFullDetails().contains(location), "Location is not displayed");
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15503() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser1 };

        String cloudUser1 = getUserNameForDomain(testName + "cloudUser", testDomain1);
        String[] cloudUserInfo1 = new String[] { cloudUser1 };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL1";
        String opFileName1 = getFileName(testName);
        String[] opFileInfo1 = new String[] { opFileName1, DOCLIB };
        String folderName = getFolderName(testName) + "CL";

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain1, "1001");

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        ShareUser.createFolderInFolder(hybridDrone, folderName, folderName, DOCLIB);
        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.uploadFileInFolder(drone, opFileInfo1).render();
    }

    /**
     * AONE-15503:Delete the target folder. Cloud.
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
    public void AONE_15503() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser", testDomain);
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain1);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL1";
        String opFileName1 = getFileName(testName);
        String folderName = getFolderName(testName) + "CL";

        // ---- Step 1 ----
        // ---- Step action ----
        // Sync files or folders to cloud.
        // ---- Expected results ----
        // Files or folder are synced.
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        documentLibraryPage.getFileDirectoryInfo(opFileName1).selectCheckbox();
        DocumentLibraryNavigation documentLibraryNavigation = new DocumentLibraryNavigation(drone);
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryNavigation.selectSyncToCloud().render();
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectFolder(folderName);
        destinationAndAssigneePage.selectSubmitButtonToSync().render();
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(opFileName1).isCloudSynced(), "File isn't synced");

        // ---- Step 2 ----
        // ---- Step action ----
        // Delete the target folder
        // ---- Expected results ----
        // The target folder is deleted.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        documentLibraryPage = documentLibraryPage.getFileDirectoryInfo(folderName).delete().render();

        // ---- Step 3 ----
        // ---- Step action ----
        // Check the cloud location in on-premise.
        // ---- Expected results ----
        // Target folder shouldn’t be displayed.
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        SyncInfoPage syncInfoPage = documentLibraryPage.getFileDirectoryInfo(opFileName1).clickOnViewCloudSyncInfo().render();
        Assert.assertTrue(syncInfoPage.isUnableToRetrieveLocation(), "Unable to retrieve location isn't displayed");
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15504() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser1 };

        String cloudUser1 = getUserNameForDomain(testName + "cloudUser", testDomain1);
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String cloudSiteName = getSiteName(testName) + "-CL1";

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain1, "1001");

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);
    }

    /**
     * AONE-15504:Change the file or folder property.
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 400000)
    public void AONE_15504() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser", testDomain);
        String opSiteName = getSiteName(testName) + "-OP" + System.currentTimeMillis();
        String opFileName = getFileName(testName) + System.currentTimeMillis();
        String cloudSiteName = getSiteName(testName) + "-CL1";
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain1);
        String cloudTitle = opFileName + "-cloud";
        String premiseTitle = opFileName + "-premise";
        String[] opFileInfo1 = new String[] { opFileName, DOCLIB };

        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

        ShareUser.uploadFileInFolder(drone, opFileInfo1).render();
        DocumentLibraryPage doclib = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        DestinationAndAssigneePage destinationAndAssigneePage = doclib.getFileDirectoryInfo(opFileName).selectSyncToCloud().render();
        destinationAndAssigneePage.selectNetwork(testDomain1);
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectFolder("Documents");
        destinationAndAssigneePage.clickSyncButton();
        Assert.assertTrue(doclib.isSyncMessagePresent(), "Message is not displayed");
        doclib.render();
        Assert.assertTrue(checkIfContentIsSynced(drone, opFileName), "File isn't synced");

        // ---- Step 1 ----
        // ---- Step action ----
        // Change the file or folder property (content name) in cloud and on-premise and sync them
        // ---- Expected results ----
        // The Cloud Version is the primary one and the latest version of that file in on-premise/cloud contains the cloud changes with message that
        // "file has been synced with conflict". On-premise changes will be versioned as well but that on-premise version will be the 2nd one after Cloud
        // version.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        EditDocumentPropertiesPage editDocumentPropertiesPage = documentLibraryPage.getFileDirectoryInfo(opFileName).selectEditProperties().render();
        editDocumentPropertiesPage.setDocumentTitle(cloudTitle);
        documentLibraryPage = editDocumentPropertiesPage.selectSave().render();

        EditDocumentPropertiesPage editDocumentPropertiesPageOP = doclib.getFileDirectoryInfo(opFileName).selectEditProperties().render();
        editDocumentPropertiesPageOP.setDocumentTitle(premiseTitle);
        editDocumentPropertiesPageOP.selectSave();
        drone.refresh();
        drone.refresh();
        doclib.getFileDirectoryInfo(opFileName).selectRequestSync().render();
        waitForSync(opFileName, opSiteName);

        editDocumentPropertiesPageOP = doclib.getFileDirectoryInfo(opFileName).selectEditProperties().render();
        Assert.assertTrue(editDocumentPropertiesPageOP.getDocumentTitle().equals(cloudTitle), "Title wasn't synced");
        editDocumentPropertiesPageOP.selectCancel();

        DocumentDetailsPage detailsPage = doclib.selectFile(opFileName).render();
        String premiseVersion = detailsPage.getDocumentVersion();
        Assert.assertEquals(premiseVersion, "1.2", "Premise file version isn't correct");
        VersionDetails versionDetails = detailsPage.getCurrentVersionDetails();
        Assert.assertTrue(versionDetails.getFullDetails().contains("synced with conflict by '" + opUser1 + "'"), "Document isn't synced with conflict");

        ShareUser.openSiteDashboard(hybridDrone, cloudSiteName);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        detailsPage = documentLibraryPage.selectFile(opFileName).render();
        String cloudVersion = detailsPage.getDocumentVersion();
        Assert.assertEquals(cloudVersion, "1.1", "Cloud file version isn't correct");
        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15505() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser1 };

        String cloudUser1 = getUserNameForDomain(testName + "cloudUser", testDomain1);
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String cloudSiteName = getSiteName(testName) + "-CL1";

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain1, "1000");

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);
    }

    /**
     * AONE-15505:Change name and author.
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 900000)
    public void AONE_15505() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser", testDomain);
        String opSiteName = getSiteName(testName) + "-OP" + System.currentTimeMillis();
        String opFileName = getFileName(testName) + System.currentTimeMillis();
        String cloudSiteName = getSiteName(testName) + "-CL1";
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain1);
        String cloudFileName = opFileName + "-cloud";
        String premiseAuthor = opFileName + "-premise";
        String[] opFileInfo1 = new String[] { opFileName, DOCLIB };

        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

        ShareUser.uploadFileInFolder(drone, opFileInfo1).render();
        DocumentLibraryPage doclib = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        DestinationAndAssigneePage destinationAndAssigneePage = doclib.getFileDirectoryInfo(opFileName).selectSyncToCloud().render();
        destinationAndAssigneePage.selectNetwork(testDomain1);
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectFolder("Documents");
        destinationAndAssigneePage.clickSyncButton();
        Assert.assertTrue(doclib.isSyncMessagePresent());
        doclib.render();
        Assert.assertTrue(checkIfContentIsSynced(drone, opFileName), "File isn't synced");

        // ---- Step 1 ----
        // ---- Step action ----
        // Change the file/folder author in on-premise and change the file/folder name in cloud and sync them
        // ---- Expected results ----
        // The Cloud Version is the primary one and the latest version of that file in on-premise/cloud contains the cloud changes with message that
        // "file has been synced with conflict". On-premise changes will be versioned as well but that on-premise version will be the 2nd one after Cloud
        // version.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        EditDocumentPropertiesPage editDocumentPropertiesPage = documentLibraryPage.getFileDirectoryInfo(opFileName).selectEditProperties().render();
        editDocumentPropertiesPage.setName(cloudFileName);
        documentLibraryPage = editDocumentPropertiesPage.selectSave().render();

        EditDocumentPropertiesPage editDocumentPropertiesPageOP = doclib.getFileDirectoryInfo(opFileName).selectEditProperties().render();
        editDocumentPropertiesPageOP.selectAllProperties();
        editDocumentPropertiesPageOP.setAuthor(premiseAuthor);
        doclib = editDocumentPropertiesPageOP.selectSave().render();

        drone.refresh();
        doclib.render();
        if (doclib.isFileVisible(opFileName))
        {
            doclib.getFileDirectoryInfo(opFileName).selectRequestSync().render();
        }
        else
        {
            doclib.getFileDirectoryInfo(cloudFileName).selectRequestSync().render();
        }

        checkIfFileNameIsUpdated(drone, cloudFileName);

        editDocumentPropertiesPageOP = doclib.getFileDirectoryInfo(cloudFileName).selectEditProperties().render();
        Assert.assertTrue(editDocumentPropertiesPageOP.getName().equals(cloudFileName), "The name isn't correct");
        editDocumentPropertiesPageOP.selectCancel();

        DocumentDetailsPage detailsPage = doclib.selectFile(cloudFileName).render();
        String premiseVersion = detailsPage.getDocumentVersion();
        Assert.assertEquals(premiseVersion, "1.2", "The version for the file from premise isn't 1.2");

        VersionDetails versionDetails = detailsPage.getCurrentVersionDetails();
        Assert.assertTrue(versionDetails.getFullDetails().contains("synced with conflict by '" + opUser1 + "'"), "Document isn't synced with conflict");
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15506() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser1 };

        String cloudUser1 = getUserNameForDomain(testName + "cloudUser", testDomain1);
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String cloudSiteName = getSiteName(testName) + "-CL1";

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain1, "1000");

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);
    }

    /**
     * AONE-15506: Change the file content both.
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 600000)
    public void AONE_15506() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser", testDomain);
        String opSiteName = getSiteName(testName) + "-OP" + System.currentTimeMillis();
        String opFileName = getFileName(testName) + System.currentTimeMillis() + ".txt";
        String cloudSiteName = getSiteName(testName) + "-CL1";
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain1);
        String cloudContent = opFileName + "-cloud content";
        String premiseContent = opFileName + "-premise content";
        String[] opFileInfo1 = new String[] { opFileName, DOCLIB };

        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

        ShareUser.uploadFileInFolder(drone, opFileInfo1).render();
        DocumentLibraryPage doclib = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        DestinationAndAssigneePage destinationAndAssigneePage = doclib.getFileDirectoryInfo(opFileName).selectSyncToCloud().render();
        destinationAndAssigneePage.selectNetwork(testDomain1);
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectFolder("Documents");
        destinationAndAssigneePage.clickSyncButton();
        Assert.assertTrue(doclib.isSyncMessagePresent());

        Assert.assertTrue(checkIfContentIsSynced(drone, opFileName), "Sync failed");
        doclib.render();
        DocumentDetailsPage detailsOp = doclib.selectFile(opFileName).render();

        // ---- Step 1 ----
        // ---- Step action ----
        // Change the file content both in on-premise and cloud and sync them
        // ---- Expected results ----
        // The Cloud Version is the primary one and the latest version of that file in on-premise/cloud contains the cloud changes with message that
        // "file has been synced with conflict". On-premise changes will be versioned as well but that on-premise version will be the 2nd one after Cloud
        // version.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(opFileName).render();
        ShareUser.editTextDocument(hybridDrone, opFileName, "", cloudContent);

        detailsOp = ShareUser.editTextDocument(drone, opFileName, "", premiseContent);
        doclib = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        drone.refresh();
        doclib.getFileDirectoryInfo(opFileName).selectRequestSync().render();
        waitForSync(opFileName, opSiteName);

        ContentDetails contentDetailsOp = ShareUserSitePage.getInLineEditContentDetails(drone, opFileName);
        String contentOp = contentDetailsOp.getContent();
        Assert.assertTrue(contentOp.equals(cloudContent), "Content from premise not equal to cloud content");

        detailsOp = doclib.selectFile(opFileName).render();
        String premiseVersion = detailsOp.getDocumentVersion();
        Assert.assertEquals(premiseVersion, "1.3", "Premise file version not equal to 1.3");
        VersionDetails versionDetails = detailsOp.getCurrentVersionDetails();
        Assert.assertTrue(versionDetails.getFullDetails().contains("synced with conflict by '" + opUser1 + "'"), "Document isn't synced with conflict");

        ShareUser.logout(hybridDrone);
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(hybridDrone, cloudSiteName);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        docDetailsPage = documentLibraryPage.selectFile(opFileName).render();
        String cloudVersion = docDetailsPage.getDocumentVersion();
        Assert.assertEquals(cloudVersion, "1.2", "Cloud file version not equal to 1.2");
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15507() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser1 };

        String cloudUser1 = getUserNameForDomain(testName + "cloudUser", testDomain1);
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String cloudSiteName = getSiteName(testName) + "-CL1";

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain1, "1000");

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);
    }

    /**
     * AONE-15507: Change the file name in cloud and on-premise.
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 900000)
    public void AONE_15507() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser", testDomain);
        String opSiteName = getSiteName(testName) + "-OP" + System.currentTimeMillis();
        String opFileName = getFileName(testName) + System.currentTimeMillis();
        String cloudSiteName = getSiteName(testName) + "-CL1";
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain1);
        String cloudFileName = opFileName + "-cloud";
        String opFileNameEdited = opFileName + "-premise";
        String[] opFileInfo1 = new String[] { opFileName, DOCLIB };

        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

        ShareUser.uploadFileInFolder(drone, opFileInfo1).render();
        DocumentLibraryPage doclib = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        DestinationAndAssigneePage destinationAndAssigneePage = doclib.getFileDirectoryInfo(opFileName).selectSyncToCloud().render();
        destinationAndAssigneePage.selectNetwork(testDomain1);
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectFolder("Documents");
        destinationAndAssigneePage.clickSyncButton();
        doclib.isSyncMessagePresent();
        doclib.render();
        Assert.assertTrue(checkIfContentIsSynced(drone, opFileName), "File isn't synced");

        // ---- Step 1 ----
        // ---- Step action ----
        // Change the file/folder author in on-premise and change the file/folder name in cloud and sync them
        // ---- Expected results ----
        // The Cloud Version is the primary one and the latest version of that file in on-premise/cloud contains the cloud changes with message that
        // "file has been synced with conflict". On-premise changes will be versioned as well but that on-premise version will be the 2nd one after Cloud
        // version.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        EditDocumentPropertiesPage editDocumentPropertiesPage = documentLibraryPage.getFileDirectoryInfo(opFileName).selectEditProperties().render();
        editDocumentPropertiesPage.setName(cloudFileName);
        documentLibraryPage = editDocumentPropertiesPage.selectSave().render();

        // modify the content on premise
        EditDocumentPropertiesPage editDocumentPropertiesPageOP = doclib.getFileDirectoryInfo(opFileName).selectEditProperties().render();
        editDocumentPropertiesPageOP.setName(opFileNameEdited);
        doclib = editDocumentPropertiesPageOP.selectSave().render();
        ShareUser.refreshSharePage(drone);
        doclib.render();
        if (doclib.isFileVisible(opFileNameEdited))
        {
            doclib.getFileDirectoryInfo(opFileNameEdited).selectRequestSync().render();
        }
        else
        {
            doclib.getFileDirectoryInfo(cloudFileName).selectRequestSync().render();
        }

        checkIfFileNameIsUpdated(drone, cloudFileName);

        editDocumentPropertiesPageOP = doclib.getFileDirectoryInfo(cloudFileName).selectEditProperties().render();
        Assert.assertTrue(editDocumentPropertiesPageOP.getName().equals(cloudFileName), "The name isn't correct");
        editDocumentPropertiesPageOP.selectCancel();

        DocumentDetailsPage detailsPage = doclib.selectFile(cloudFileName).render();
        String premiseVersion = detailsPage.getDocumentVersion();
        Assert.assertEquals(premiseVersion, "1.2", "The version for the file from premise isn't 1.2");

        VersionDetails versionDetails = detailsPage.getCurrentVersionDetails();
        Assert.assertTrue(versionDetails.getFullDetails().contains("synced with conflict by '" + opUser1 + "'"), "Document isn't synced with conflict");

        // ---- Step 2 ----
        // ---- Step action ----
        // Now change the file name back to old on in cloud and check if conflict goes away.
        // ---- Expected results ----
        // Conflict can only be resolved manually from on-premise.
        ShareUser.logout(hybridDrone);
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        editDocumentPropertiesPage = documentLibraryPage.getFileDirectoryInfo(cloudFileName).selectEditProperties().render();
        editDocumentPropertiesPage.setName(opFileName);
        documentLibraryPage = editDocumentPropertiesPage.selectSave().render();

        doclib = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.refreshSharePage(drone);
        doclib.getFileDirectoryInfo(cloudFileName).selectRequestSync().render();
        checkIfFileNameIsUpdated(drone, opFileName);

        doclib.render();
        detailsPage = doclib.selectFile(opFileName).render();
        versionDetails = detailsPage.getCurrentVersionDetails();
        Assert.assertTrue(versionDetails.getFullDetails().contains("synced with conflict by '" + opUser1 + "'"), "Document is synced with conflict");

    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15508() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
        String[] userInfo1 = new String[] { opUser1 };
        String cloudUser1 = getUserNameForDomain(testName + "cloudUser1", testDomain1);
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName1 = getSiteName(testName) + "-CL1";
        String opFileName = getFileName(testName);
        String[] opFileInfo = new String[] { opFileName };
        String folderName = getFolderName(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain1, "1001");

        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName1, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName1).render();
        ShareUser.createFolderInFolder(hybridDrone, folderName, folderName, DOCLIB).render();
        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName);
        ShareUser.uploadFileInFolder(drone, opFileInfo).render();
    }

    /**
     * AONE-15508:Unsync a moved file/folder in cloud.
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 500000)
    public void AONE_15508() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
        String cloudUser1 = getUserNameForDomain(testName + "cloudUser1", testDomain1);
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName1 = getSiteName(testName) + "-CL1";
        String opFileName1 = getFileName(testName);
        String folderName = getFolderName(testName);

        // ---- Step 1 ----
        // ---- Step action ----
        // Unsync a moved file/folder in cloud.
        // ---- Expected results ----
        // Unsync should be successful
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        documentLibraryPage.getFileDirectoryInfo(opFileName1).selectCheckbox();

        DocumentLibraryNavigation documentLibraryNavigation = new DocumentLibraryNavigation(drone);
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryNavigation.selectSyncToCloud().render();
        destinationAndAssigneePage.selectSite(cloudSiteName1);
        destinationAndAssigneePage.selectSubmitButtonToSync().render();
        Assert.assertTrue(checkIfContentIsSynced(drone, opFileName1), "File wasn't synced");

        ShareUser.login(hybridDrone, cloudUser1);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName1).render();
        documentLibraryPage.getFileDirectoryInfo(opFileName1).selectCheckbox();
        DocumentLibraryNavigation documentLibraryNavigationCloud = new DocumentLibraryNavigation(hybridDrone);
        CopyOrMoveContentPage moveToFolder = documentLibraryNavigationCloud.selectMoveTo().render();
        moveToFolder.selectPath(folderName).render().selectOkButton().render();

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        documentLibraryPage.getFileDirectoryInfo(opFileName1).selectUnSyncAndRemoveContentFromCloud(false);
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(opFileName1).isCloudSynced(), "The file is synced");
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15509() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
        String[] userInfo1 = new String[] { opUser1 };

        String cloudUser1 = getUserNameForDomain(testName + "cloudUser1", testDomain1);
        String[] cloudUserInfo1 = new String[] { cloudUser1 };

        String cloudUser2 = getUserNameForDomain(testName + "cloudUser2", testDomain1);
        String[] cloudUserInfo2 = new String[] { cloudUser2 };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName1 = getSiteName(testName) + "-CL1";
        String opFileName = getFileName(testName);
        String[] opFileInfo = new String[] { opFileName };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo2);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain1, "1001");

        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName1, SITE_VISIBILITY_PUBLIC);
        CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, cloudUser1, cloudUser2, getSiteShortname(cloudSiteName1), "SiteCollaborator", "");
        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);

        DocumentLibraryPage doclib = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.uploadFileInFolder(drone, opFileInfo).render();
        DestinationAndAssigneePage destinationAndAssigneePage = doclib.getFileDirectoryInfo(opFileName).selectSyncToCloud().render();
        destinationAndAssigneePage.selectNetwork(testDomain1);
        destinationAndAssigneePage.selectSite(cloudSiteName1);
        destinationAndAssigneePage.selectFolder("Documents");
        destinationAndAssigneePage.clickSyncButton();
        doclib.isSyncMessagePresent();
        doclib.render();
        checkIfContentIsSynced(drone, opFileName);
    }

    /**
     * AONE-15509: Unsync a file/folder that is locked for editing.
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
    public void AONE_15509() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
        String cloudUser2 = getUserNameForDomain(testName + "cloudUser2", testDomain1);
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName1 = getSiteName(testName) + "-CL1";
        String opFileName = getFileName(testName);

        // ---- Step 1 ----
        // ---- Step action ----
        // Try to unsync a file/folder that is locked for editing in cloud by another user.
        // ---- Expected results ----
        // Unsync should be successfull.
        ShareUser.login(hybridDrone, cloudUser2, DEFAULT_PASSWORD);
        DocumentLibraryPage docLib = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName1).render();
        docLib.getFileDirectoryInfo(opFileName).selectEditOfflineAndCloseFileWindow();

        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        docLib = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        docLib.getFileDirectoryInfo(opFileName).selectUnSyncAndRemoveContentFromCloud(false);
        Assert.assertFalse(docLib.getFileDirectoryInfo(opFileName).isCloudSynced(), "The file is synced");
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15510() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser1 };

        String cloudUser1 = getUserNameForDomain(testName + "cloudUser", testDomain1);
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String cloudSiteName = getSiteName(testName) + "-CL1";

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain1, "1001");

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);
    }

    /**
     * AONE-15510: Unsync a file/folder that has conflict.
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 500000)
    public void AONE_15510() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser", testDomain);
        String opSiteName = getSiteName(testName) + "-OP" + System.currentTimeMillis();
        String opFileName = getFileName(testName) + System.currentTimeMillis();
        String cloudSiteName = getSiteName(testName) + "-CL1";
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain1);
        String cloudTitle = opFileName + "-cloud";
        String premiseTitle = opFileName + "-premise";
        String[] opFileInfo1 = new String[] { opFileName, DOCLIB };

        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

        ShareUser.uploadFileInFolder(drone, opFileInfo1).render();
        DocumentLibraryPage doclib = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        DestinationAndAssigneePage destinationAndAssigneePage = doclib.getFileDirectoryInfo(opFileName).selectSyncToCloud().render();
        destinationAndAssigneePage.selectNetwork(testDomain1);
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectFolder("Documents");
        destinationAndAssigneePage.clickSyncButton();
        Assert.assertTrue(doclib.isSyncMessagePresent(), "Message is not displayed");
        doclib.render();
        Assert.assertTrue(checkIfContentIsSynced(drone, opFileName), "File isn't synced");

        // ---- Step 1 ----
        // ---- Step action ----
        // Try and unsync a file/folder that has conflict, unsync it without resolving conflict.
        // ---- Expected results ----
        // Unsync should pass..
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        EditDocumentPropertiesPage editDocumentPropertiesPage = documentLibraryPage.getFileDirectoryInfo(opFileName).selectEditProperties().render();
        editDocumentPropertiesPage.setDocumentTitle(cloudTitle);
        documentLibraryPage = editDocumentPropertiesPage.selectSave().render();

        EditDocumentPropertiesPage editDocumentPropertiesPageOP = doclib.getFileDirectoryInfo(opFileName).selectEditProperties().render();
        editDocumentPropertiesPageOP.setDocumentTitle(premiseTitle);
        editDocumentPropertiesPageOP.selectSave();
        ShareUser.refreshSharePage(drone);
        ShareUser.refreshSharePage(drone);
        doclib.getFileDirectoryInfo(opFileName).selectRequestSync().render();
        waitForSync(opFileName, opSiteName);

        DocumentDetailsPage detailsPage = doclib.selectFile(opFileName).render();
        VersionDetails versionDetails = detailsPage.getCurrentVersionDetails();
        Assert.assertTrue(versionDetails.getFullDetails().contains("synced with conflict by '" + opUser1 + "'"), "Document isn't synced with conflict");
        doclib = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

        doclib.getFileDirectoryInfo(opFileName).selectUnSyncAndRemoveContentFromCloud(false);
        Assert.assertFalse(doclib.getFileDirectoryInfo(opFileName).isCloudSynced(), "The file is synced");
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15511() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser1 };
        String cloudUser1 = getUserNameForDomain(testName + "cloudUser", testDomain1);
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String cloudSiteName = getSiteName(testName) + "-CL1";
        String opSiteName = getSiteName(testName) + "-OP";
        String opFileName1 = getFileName(testName) + "1";
        String opFileName2 = getFileName(testName) + "2";
        String[] opFileInfo1 = new String[] { opFileName1, DOCLIB };
        String[] opFileInfo2 = new String[] { opFileName2, DOCLIB };
        String folderName = getFolderName(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain1, "1001");

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB).render();
        ShareUser.uploadFileInFolder(drone, opFileInfo1).render();
        ShareUser.uploadFileInFolder(drone, opFileInfo2).render();
    }

    /**
     * AONE-15511: Unsync multiple files and folder whilst one or two files are locked for editing in cloud.
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 500000)
    public void AONE_15511() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser", testDomain);
        String opSiteName = getSiteName(testName) + "-OP";
        String opFileName1 = getFileName(testName) + "1";
        String opFileName2 = getFileName(testName) + "2";
        String cloudSiteName = getSiteName(testName) + "-CL1";
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain1);
        String folderName = getFolderName(testName);

        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        DocumentLibraryPage doclib = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        doclib.getFileDirectoryInfo(opFileName1).selectCheckbox();
        doclib.getFileDirectoryInfo(opFileName2).selectCheckbox();
        DocumentLibraryNavigation documentLibraryNavigation = new DocumentLibraryNavigation(drone);
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryNavigation.selectSyncToCloud().render();
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectSubmitButtonToSync().render();
        ShareUser.refreshSharePage(drone);
        doclib.render();

        destinationAndAssigneePage = doclib.getFileDirectoryInfo(folderName).selectSyncToCloud().render();
        destinationAndAssigneePage.selectNetwork(testDomain1);
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectFolder("Documents");
        destinationAndAssigneePage.clickSyncButton();
        doclib.isSyncMessagePresent();
        doclib.render();
        Assert.assertTrue(checkIfContentIsSynced(drone, folderName), "Folder is not synced");

        // ---- Step 1 ----
        // ---- Step action ----
        // Try and unsync multiple files and folder whilst one or two files are locked for editing in cloud.
        // ---- Expected results ----
        // Unsync should be successful.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        documentLibraryPage.getFileDirectoryInfo(opFileName1).selectEditOfflineAndCloseFileWindow();
        documentLibraryPage.render();
        documentLibraryPage.getFileDirectoryInfo(opFileName2).selectEditOfflineAndCloseFileWindow();

        doclib = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        doclib.getFileDirectoryInfo(opFileName1).selectUnSyncAndRemoveContentFromCloud(false);
        Assert.assertFalse(doclib.getFileDirectoryInfo(opFileName1).isCloudSynced(), "The file " + opFileName1 + " is synced");

        doclib.getFileDirectoryInfo(opFileName2).selectUnSyncAndRemoveContentFromCloud(false);
        Assert.assertFalse(doclib.getFileDirectoryInfo(opFileName2).isCloudSynced(), "The file " + opFileName2 + " is synced");

        doclib.getFileDirectoryInfo(folderName).selectUnSyncAndRemoveContentFromCloud(false);
        Assert.assertFalse(doclib.getFileDirectoryInfo(folderName).isCloudSynced(), "The folder " + folderName + " is synced");
    }

    private void waitForSync(String fileName, String siteName)
    {
        int counter = 1;
        int retryRefreshCount = 4;
        while (counter <= retryRefreshCount)
        {
            if (checkIfContentIsSynced(drone, fileName))
            {
                break;
            }
            else
            {
                logger.info("Wait for Sync");

                drone.refresh();
                counter++;

                if (counter == 2 || counter == 3)
                {
                    DocumentLibraryPage docLib = ShareUser.openSitesDocumentLibrary(drone, siteName);
                    docLib.getFileDirectoryInfo(fileName).selectRequestSync().render();
                }
            }
        }
    }

}
