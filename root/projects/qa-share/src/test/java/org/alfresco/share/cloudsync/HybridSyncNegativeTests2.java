package org.alfresco.share.cloudsync;

import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.document.CopyOrMoveContentPage;
import org.alfresco.po.share.site.document.DocumentLibraryNavigation;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.user.CloudSignInPage;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.WebDroneType;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.test.FailedTestListener;
import org.alfresco.webdrone.WebDrone;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author bogdan.bocancea
 */

@Listeners(FailedTestListener.class)
public class HybridSyncNegativeTests2 extends AbstractWorkflow
{
    private static final Logger logger = Logger.getLogger(HybridSyncNegativeTests2.class);

    private String testDomain1;
    private String testDomain2;
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
        testDomain2 = "negative2.test";
        testDomain = DOMAIN_HYBRID;
        uniqueRun = "TS23";
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15487() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
        String[] userInfo1 = new String[] { opUser1 };

        String cloudUser1 = getUserNameForDomain(testName + "cloudUser1", testDomain1);
        String[] cloudUserInfo1 = new String[] { cloudUser1 };

        String cloudUser2 = getUserNameForDomain(testName + "cloudUser2", testDomain2);
        String[] cloudUserInfo2 = new String[] { cloudUser2 };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName1 = getSiteName(testName) + "-CL1";
        String cloudSiteName2 = getSiteName(testName) + "-CL2";
        String opFileName = getFileName(testName);
        String[] opFileInfo = new String[] { opFileName };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo2);

        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain1, "1001");
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain2, "1001");

        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName1, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(hybridDrone, cloudSiteName2, SITE_VISIBILITY_PUBLIC);
        CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, cloudUser1, cloudUser2, siteActions.getSiteShortname(cloudSiteName1), "SiteContributor", "");

        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser2, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, opSiteName);
        ShareUser.uploadFileInFolder(drone, opFileInfo).render();
        ShareUser.logout(drone);
    }

    /**
     * AONE-15487: Sync to the different networks.
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
    public void AONE_15487() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
        String opSiteName = getSiteName(testName) + "-OP";
        String opFileName = getFileName(testName);
        String cloudSiteName1 = getSiteName(testName) + "-CL1";

        // ---- Step 1 ----
        // ---- Step action ----
        // Set cursor to the document and expand More+ menu
        // ---- Expected results ----
        // List of actions is appeared for document and 'sync to cloud' option available
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(opFileName).isSyncToCloudLinkPresent(), "Sync to cloud is not displayed");

        // ---- Step 2 ----
        // ---- Step action ----
        // Choose 'Sync to Cloud' option
        // ---- Expected results ----
        // Pop-up window to select target cloud location appears
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(opFileName).selectSyncToCloud().render();
        Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + opFileName + " to The Cloud"), "Title is not present");

        // ---- Step 3 ----
        // ---- Step action ----
        // Verify information displayed in window
        // ---- Expected results ----
        // All available networks are displayed correctly and can be chosen
        Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(testDomain1), "Network " + testDomain1 + " is not displayed");
        Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(testDomain2), "Network " + testDomain2 + " is not displayed");

        // ---- Step 4 ----
        // ---- Step action ----
        // Choose any network --available site--document library and press OK button
        // ---- Expected results ----
        // Notification about file is synced successfully appears.
        destinationAndAssigneePage.selectNetwork(testDomain1);
        destinationAndAssigneePage.selectSite(cloudSiteName1);
        destinationAndAssigneePage.selectFolder("Documents");
        destinationAndAssigneePage.clickSyncButton();
        Assert.assertTrue(documentLibraryPage.isSyncMessagePresent(), "Message is not displayed");

        // ---- Step 5 ----
        // ---- Step action ----
        // Try to sync the same file to another network.
        // ---- Expected results ----
        // Should be not allowed.
        documentLibraryPage.render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(opFileName).isSyncToCloudLinkPresent(), "Sync to cloud is not present");

    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15488() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
        String[] userInfo1 = new String[] { opUser1 };

        String opUser2 = getUserNameForDomain(testName + "opUser2", testDomain);
        String[] userInfo2 = new String[] { opUser2 };

        String cloudUser1 = getUserNameForDomain(testName + "cloudUser1", testDomain1);
        String[] cloudUserInfo1 = new String[] { cloudUser1 };

        String cloudUser2 = getUserNameForDomain(testName + "cloudUser2", testDomain2);
        String[] cloudUserInfo2 = new String[] { cloudUser2 };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName1 = getSiteName(testName) + "-CL1";
        String cloudSiteName2 = getSiteName(testName) + "-CL2";
        String opFileName = getFileName(testName);
        String[] opFileInfo = new String[] { opFileName };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo2);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo2);

        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain1, "1001");
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain2, "1001");

        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName1, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(hybridDrone, cloudSiteName2, SITE_VISIBILITY_PUBLIC);

        CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, cloudUser1, cloudUser2, siteActions.getSiteShortname(cloudSiteName1), "SiteContributor", "");

        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser2, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, opSiteName);
        ShareUser.uploadFileInFolder(drone, opFileInfo).render();
        ShareUserMembers.inviteUserToSiteWithRole(drone, opUser1, opUser2, opSiteName, UserRole.CONTRIBUTOR);
        ShareUser.logout(drone);

        ShareUser.login(drone, opUser2, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser2, DEFAULT_PASSWORD);
    }

    /**
     * AONE-15488: Sync to the different networks by different users.
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
    public void AONE_15488() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
        String opUser2 = getUserNameForDomain(testName + "opUser2", testDomain);
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName1 = getSiteName(testName) + "-CL1";
        String opFileName = getFileName(testName);

        // ---- Step 1 ----
        // ---- Step action ----
        // Set cursor to the document and expand More+ menu
        // ---- Expected results ----
        // List of actions is appeared for document and 'sync to cloud' option available
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(opFileName).isSyncToCloudLinkPresent(), "Sync to cloud is not present");

        // ---- Step 2 ----
        // ---- Step action ----
        // Choose 'Sync to Cloud' option
        // ---- Expected results ----
        // Pop-up window to select target cloud location appears
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(opFileName).selectSyncToCloud().render();
        Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + opFileName + " to The Cloud"), "Title isn't good");

        // ---- Step 3 ----
        // ---- Step action ----
        // Verify information displayed in window
        // ---- Expected results ----
        // All available networks are displayed correctly and can be chosen
        Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(testDomain1), "Network " + testDomain1 + " is not displayed");
        Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(testDomain2), "Network " + testDomain2 + " is not displayed");

        // ---- Step 4 ----
        // ---- Step action ----
        // Choose any network --available site--document library and press OK button
        // ---- Expected results ----
        // Notification about file is synced successfully appears.
        destinationAndAssigneePage.selectNetwork(testDomain1);
        destinationAndAssigneePage.selectSite(cloudSiteName1);
        destinationAndAssigneePage.selectFolder("Documents");
        destinationAndAssigneePage.clickSyncButton();
        Assert.assertTrue(documentLibraryPage.isSyncMessagePresent(), "Sync message isn't displayed");

        // ---- Step 5 ----
        // ---- Step action ----
        // Set cursor to the document and expand More+ menu
        // ---- Expected results ----
        // List of actions is appeared for document and 'sync to cloud' option available
        ShareUser.login(drone, opUser2, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(opFileName).isSyncToCloudLinkPresent(), "Sync to cloud link not displayed");
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15489() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
        String[] userInfo1 = new String[] { opUser1 };

        String cloudUser1 = getUserNameForDomain(testName + "cloudUser1", testDomain1);
        String[] cloudUserInfo1 = new String[] { cloudUser1 };

        String cloudUser2 = getUserNameForDomain(testName + "cloudUser2", testDomain2);
        String[] cloudUserInfo2 = new String[] { cloudUser2 };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName1 = getSiteName(testName) + "-CL1";
        String cloudSiteName2 = getSiteName(testName) + "-CL2";
        String opFileName = getFileName(testName);
        String[] opFileInfo = new String[] { opFileName };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo2);

        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain1, "1001");
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain2, "1001");

        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC).render();
        ShareUser.logout(drone);

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName1, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(hybridDrone, cloudSiteName2, SITE_VISIBILITY_PUBLIC);

        CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, cloudUser1, cloudUser2, siteActions.getSiteShortname(cloudSiteName1), "SiteContributor", "");
        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser2, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName);
        ShareUser.uploadFileInFolder(drone, opFileInfo).render();
    }

    /**
     * AONE-15487: Sync to the different networks.
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
    public void AONE_15489() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
        String opSiteName = getSiteName(testName) + "-OP";
        String opFileName = getFileName(testName);
        String cloudSiteName1 = getSiteName(testName) + "-CL1";

        // ---- Step 1 ----
        // ---- Step action ----
        // Set cursor to the document and expand More+ menu
        // ---- Expected results ----
        // List of actions is appeared for document and 'sync to cloud' option available
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(opFileName).isSyncToCloudLinkPresent(), "Sync to cloud is not displayed");

        // ---- Step 2 ----
        // ---- Step action ----
        // Choose 'Sync to Cloud' option
        // ---- Expected results ----
        // Pop-up window to select target cloud location appears
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(opFileName).selectSyncToCloud().render();
        Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + opFileName + " to The Cloud"), "Title is not present");

        // ---- Step 3 ----
        // ---- Step action ----
        // Verify information displayed in window
        // ---- Expected results ----
        // All available networks are displayed correctly and can be chosen
        Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(testDomain1), "Network " + testDomain1 + " is not displayed");
        Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(testDomain2), "Network " + testDomain2 + " is not displayed");

        // ---- Step 4 ----
        // ---- Step action ----
        // Choose any network --available site--document library and press OK button
        // ---- Expected results ----
        // Notification about file is synced successfully appears.
        destinationAndAssigneePage.selectNetwork(testDomain1);
        destinationAndAssigneePage.selectSite(cloudSiteName1);
        destinationAndAssigneePage.selectFolder("Documents");
        destinationAndAssigneePage.clickSyncButton();
        Assert.assertTrue(documentLibraryPage.isSyncMessagePresent(), "Sync message isn't displayed");

        // ---- Step 5 ----
        // ---- Step action ----
        // Unsync the file.
        // ---- Expected results ----
        // Notification about file is unsynced successfully appears.
        documentLibraryPage.render();
        documentLibraryPage.getFileDirectoryInfo(opFileName).selectUnSyncAndRemoveContentFromCloud(false);
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(opFileName).isUnSyncFromCloudLinkPresent(), "Unsync from cloud option is displayed");

        // ---- Step 6 ----
        // ---- Step action ----
        // Sync the same file to network2.
        // ---- Expected results ----
        // Should be allowed.
        drone.refresh();
        documentLibraryPage.render();
        destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(opFileName).selectSyncToCloud().render();
        destinationAndAssigneePage.selectNetwork(testDomain2);
        destinationAndAssigneePage.selectFolder("Documents");
        destinationAndAssigneePage.clickSyncButton();
        Assert.assertTrue(documentLibraryPage.isSyncMessagePresent(), "Sync message isn't displayed");
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15490() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
        String[] userInfo1 = new String[] { opUser1 };

        String cloudUser1 = getUserNameForDomain(testName + "cloudUser1", testDomain1);
        String[] cloudUserInfo1 = new String[] { cloudUser1 };

        String cloudUser2 = getUserNameForDomain(testName + "cloudUser2", testDomain2);
        String[] cloudUserInfo2 = new String[] { cloudUser2 };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName1 = getSiteName(testName) + "-CL1";
        String cloudSiteName2 = getSiteName(testName) + "-CL2";
        String opFileName = getFileName(testName);
        String[] opFileInfo = new String[] { opFileName };
        String folderName = getFolderName(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo2);

        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain1, "1001");
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain2, "1001");

        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName1, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(hybridDrone, cloudSiteName2, SITE_VISIBILITY_PUBLIC);

        CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, cloudUser1, cloudUser2, siteActions.getSiteShortname(cloudSiteName1), "SiteContributor", "");
        ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName1);
        for (int i = 1; i < 11; i++)
        {
            ShareUser.createFolderInFolder(hybridDrone, folderName + i, folderName + i, DOCLIB);
        }

        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser2, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName);
        ShareUser.uploadFileInFolder(drone, opFileInfo).render();
        ShareUser.logout(drone);
    }

    /**
     * AONE-15490:Sync a file to a sub-folder
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
    public void AONE_15490() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
        String opSiteName = getSiteName(testName) + "-OP";
        String opFileName = getFileName(testName);
        String cloudSiteName1 = getSiteName(testName) + "-CL1";
        String folderName = getFolderName(testName) + "10";

        // ---- Step 1 ----
        // ---- Step action ----
        // Set cursor to the document and expand More+ menu
        // ---- Expected results ----
        // List of actions is appeared for document and 'sync to cloud' option available
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(opFileName).isSyncToCloudLinkPresent(), "Sync to cloud option is not displayed");

        // ---- Step 2 ----
        // ---- Step action ----
        // Choose 'Sync to Cloud' option
        // ---- Expected results ----
        // Pop-up window to select target cloud location appears
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(opFileName).selectSyncToCloud().render();
        Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + opFileName + " to The Cloud"), "Title is not displyed");

        // ---- Step 3 ----
        // ---- Step action ----
        // Verify information displayed in window
        // ---- Expected results ----
        // All available networks are displayed correctly and can be chosen
        Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(testDomain1), "Network " + testDomain1 + " is not displayed");
        Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(testDomain2), "Network " + testDomain2 + " is not displayed");

        // ---- Step 4 ----
        // ---- Step action ----
        // Choose any network --available site--document library and press OK button
        // ---- Expected results ----
        // Notification about file is synced successfully appears.
        destinationAndAssigneePage.selectNetwork(testDomain1);
        destinationAndAssigneePage.selectSite(cloudSiteName1);
        destinationAndAssigneePage.selectFolder(folderName);
        destinationAndAssigneePage.clickSyncButton();
        Assert.assertTrue(documentLibraryPage.isSyncMessagePresent(), "Sync message isn't displayed");

    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15491() throws Exception
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

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain1, "1001");

        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName1, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName);
        ShareUser.uploadFileInFolder(drone, opFileInfo).render();
    }

    /**
     * AONE-15491:Execute "Sync to Cloud" action.
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
    public void AONE_15491() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
        String opSiteName = getSiteName(testName) + "-OP";
        String opFileName = getFileName(testName);

        // ---- Step 1 ----
        // ---- Step action ----
        // Set cursor to the document and expand More+ menu
        // ---- Expected results ----
        // List of actions is appeared for document and 'sync to cloud' option available
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(opFileName).isSyncToCloudLinkPresent(), "Synced to cloud not displayed");

        // ---- Step 2 ----
        // ---- Step action ----
        // Choose 'Sync to Cloud' option
        // ---- Expected results ----
        // Pop-up window to select target cloud location appears
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(opFileName).selectSyncToCloud().render();
        Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + opFileName + " to The Cloud"), "Title is not displayed");

        // ---- Step 3 ----
        // ---- Step action ----
        // Verify information displayed in window
        // ---- Expected results ----
        // All available networks are displayed correctly and can be chosen
        Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(testDomain1), "Network " + testDomain1 + " is not displayed");

        // ---- Step 4 ----
        // ---- Step action ----
        // Choose any network -->available site-->document library and press Close button
        // ---- Expected results ----
        // The popup window is closed.
        documentLibraryPage = destinationAndAssigneePage.selectCloseButton().render();
        Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().isEmpty(), "Popup is displayed");

        // ---- Step 5 ----
        // ---- Step action ----
        // Choose any network -->available site-->document library and press Cancel button
        // ---- Expected results ----
        // The popup window is closed.
        drone.refresh();
        documentLibraryPage.render();
        destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(opFileName).selectSyncToCloud().render();
        documentLibraryPage = destinationAndAssigneePage.selectCancelButton().render();
        Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().isEmpty(), "Popup is closed");
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15492() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
        String[] userInfo1 = new String[] { opUser1 };

        String cloudUser1 = getUserNameForDomain(testName + "cloudUser1", testDomain1);
        String[] cloudUserInfo1 = new String[] { cloudUser1 };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName1 = getSiteName(testName) + "-CL1";
        String opFileName1 = getFileName(testName) + "1";
        String[] opFileInfo1 = new String[] { opFileName1 };

        String opFileName2 = getFileName(testName) + "2";
        String[] opFileInfo2 = new String[] { opFileName2 };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain1, "1001");

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName1, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.uploadFileInFolder(drone, opFileInfo1).render();
        ShareUser.uploadFileInFolder(drone, opFileInfo2).render();

        documentLibraryPage = documentLibraryPage.getFileDirectoryInfo(opFileName1).selectEditOfflineAndCloseFileWindow().render();
        ShareUser.logout(drone);
    }

    /**
     * AONE-15492: Locked for editing files.
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
    public void AONE_15492() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
        String opSiteName = getSiteName(testName) + "-OP";
        String opFileName1 = getFileName(testName) + "1";
        String opFileName2 = getFileName(testName) + "2";
        String cloudSiteName = getSiteName(testName) + "-CL1";

        // ---- Step 1 ----
        // ---- Step action ----
        // Select multiple files with "locked for editing" file and sync all files.
        // ---- Expected results ----
        // Locked file should be skipped.
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        documentLibraryPage.getFileDirectoryInfo(opFileName1).selectCheckbox();
        documentLibraryPage.getFileDirectoryInfo(opFileName2).selectCheckbox();

        DocumentLibraryNavigation documentLibraryNavigation = new DocumentLibraryNavigation(drone);
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryNavigation.selectSyncToCloud().render();
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectSubmitButtonToSync().render();
        refreshSharePage(drone).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(opFileName1).isCloudSynced(), "File is synced");
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(opFileName2).isCloudSynced(), "File is not synced");
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15493() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
        String[] userInfo1 = new String[] { opUser1 };
        String cloudUser1 = getUserNameForDomain(testName + "cloudUser1", testDomain1);
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String folderName = getFolderName(testName);
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL1";
        String opFileName1 = getFileName(testName) + "1";
        String[] opFileInfo1 = new String[] { opFileName1, folderName };
        String opFileName2 = getFileName(testName) + "2";
        String[] opFileInfo2 = new String[] { opFileName2, folderName };
        String opFileName3 = getFileName(testName) + "3";
        String[] opFileInfo3 = new String[] { opFileName3, folderName };

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

        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);
        documentLibraryPage = documentLibraryPage.selectFolder(folderName).render();
        ShareUser.uploadFileInFolder(drone, opFileInfo1).render();
        ShareUser.uploadFileInFolder(drone, opFileInfo2).render();
        ShareUser.uploadFileInFolder(drone, opFileInfo3).render();

        documentLibraryPage.getFileDirectoryInfo(opFileName1).selectCheckbox();
        documentLibraryPage.getFileDirectoryInfo(opFileName2).selectCheckbox();

        DocumentLibraryNavigation documentLibraryNavigation = new DocumentLibraryNavigation(drone);
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryNavigation.selectSyncToCloud().render();
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectSubmitButtonToSync().render();
    }

    /**
     * AONE-15493:Already synced files.
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
    public void AONE_15493() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
        String opSiteName = getSiteName(testName) + "-OP";
        String opFileName1 = getFileName(testName) + "1";
        String opFileName2 = getFileName(testName) + "2";
        String opFileName3 = getFileName(testName) + "3";
        String cloudSiteName = getSiteName(testName) + "-CL1";
        String folderName = getFolderName(testName);

        // ---- Step 1 ----
        // ---- Step action ----
        // Click Sync to Cloud for the folder..
        // ---- Expected results ----
        // Already synced files should be skipped.
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

        DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(folderName).selectSyncToCloud().render();
        destinationAndAssigneePage.selectNetwork(testDomain1);
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectFolder(folderName);
        destinationAndAssigneePage.clickSyncButton();
        Assert.assertTrue(documentLibraryPage.isSyncMessagePresent(), "Message is not displayed");
        documentLibraryPage.render();
        documentLibraryPage.selectFolder(folderName);
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(opFileName1).isCloudSynced(), opFileName1 + " is not synced");
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(opFileName2).isCloudSynced(), opFileName2 + " is not synced");
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(opFileName3).isIndirectlySyncedIconPresent(), opFileName2 + " is not synced");
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15494() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
        String[] userInfo1 = new String[] { opUser1 };
        String cloudUser1 = getUserNameForDomain(testName + "cloudUser1", testDomain1);
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String folderName = getFolderName(testName);
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL1";
        String opFileName1 = getFileName(testName) + "1";
        String[] opFileInfo1 = new String[] { opFileName1, DOCLIB };
        String opFileName2 = getFileName(testName) + "2";
        String[] opFileInfo2 = new String[] { opFileName2, DOCLIB };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain1, "1001");

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC).render();
        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);
        ShareUser.uploadFileInFolder(drone, opFileInfo1).render();
        ShareUser.uploadFileInFolder(drone, opFileInfo2).render();
    }

    /**
     * AONE-15494:Update multiple files in Alfresco Enterprise.
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 700000)
    public void AONE_15494() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
        String opSiteName = getSiteName(testName) + "-OP";
        String opFileName1 = getFileName(testName) + "1";
        String opFileName2 = getFileName(testName) + "2";
        String cloudSiteName = getSiteName(testName) + "-CL1";
        String folderName = getFolderName(testName);
        String cloudUser1 = getUserNameForDomain(testName + "cloudUser1", testDomain1);

        // ---- Step 1 ----
        // ---- Step action ----
        // Select multiple files and sync all files..
        // ---- Expected results ----
        // All files are synced.
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        documentLibraryPage.getFileDirectoryInfo(opFileName1).selectCheckbox();
        documentLibraryPage.getFileDirectoryInfo(opFileName2).selectCheckbox();

        DocumentLibraryNavigation documentLibraryNavigation = new DocumentLibraryNavigation(drone);
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryNavigation.selectSyncToCloud().render();
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectSubmitButtonToSync().render();
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(opFileName1).isCloudSynced(), opFileName1 + " is not synced");
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(opFileName2).isCloudSynced(), opFileName2 + " is not synced");
        Assert.assertTrue(checkIfContentIsSynced(drone, opFileName1), "Sync failed");

        // ---- Step 2 ----
        // ---- Step action ----
        // Move in Alfresco Enterprise all these files to a different folder and update the content.
        // ---- Expected results ----
        // The files are moved and edited.
        documentLibraryPage.render();
        if (!documentLibraryPage.getFileDirectoryInfo(opFileName1).isCheckboxSelected())
        {
            documentLibraryPage.getFileDirectoryInfo(opFileName1).selectCheckbox();
            documentLibraryPage.getFileDirectoryInfo(opFileName2).selectCheckbox();
        }
        CopyOrMoveContentPage moveToFolder = documentLibraryNavigation.selectMoveTo().render();
        moveToFolder.selectPath(folderName).render().selectOkButton().render();

        documentLibraryPage.render();
        documentLibraryPage = documentLibraryPage.selectFolder(folderName).render();
        EditDocumentPropertiesPage editDocumentPropertiesPage = documentLibraryPage.getFileDirectoryInfo(opFileName1).selectEditProperties().render();
        editDocumentPropertiesPage.setDocumentTitle(opFileName1 + testName);
        documentLibraryPage = editDocumentPropertiesPage.selectSave().render();
        ShareUser.refreshSharePage(drone);
        documentLibraryPage = documentLibraryPage.getFileDirectoryInfo(opFileName1).selectRequestSync().render();
        waitForSync(drone, opFileName1, opSiteName);
        documentLibraryPage.render();
        editDocumentPropertiesPage = documentLibraryPage.getFileDirectoryInfo(opFileName2).selectEditProperties().render();
        editDocumentPropertiesPage.setDocumentTitle(opFileName2 + testName);
        documentLibraryPage = editDocumentPropertiesPage.selectSave().render();
        ShareUser.logout(drone);

        // ---- Step 3 ----
        // ---- Step action ----
        // Check and confirm the content is synced and updated in Cloud.
        // ---- Expected results ----
        // The content is synced and updated in Cloud.
        ShareUser.login(hybridDrone, cloudUser1);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(opFileName1).isCloudSynced(), opFileName1 + " is not synced");
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(opFileName2).isCloudSynced(), opFileName2 + " is not synced");
        documentLibraryPage.render();
        editDocumentPropertiesPage = documentLibraryPage.getFileDirectoryInfo(opFileName1).selectEditProperties().render();
        Assert.assertTrue(editDocumentPropertiesPage.getDocumentTitle().equals(opFileName1 + testName), "Title isn't updated");
        editDocumentPropertiesPage.selectCancel();

    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15495() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
        String[] userInfo1 = new String[] { opUser1 };

        String cloudUser1 = getUserNameForDomain(testName + "cloudUser1", testDomain1);
        String[] cloudUserInfo1 = new String[] { cloudUser1 };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL1";
        String opFileName1 = getFileName(testName) + "1";
        String[] opFileInfo1 = new String[] { opFileName1, DOCLIB };
        String opFileName2 = getFileName(testName) + "2";
        String[] opFileInfo2 = new String[] { opFileName2, DOCLIB };
        String folderName = getFolderName(testName);

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
        ShareUser.uploadFileInFolder(drone, opFileInfo2).render();
    }

    /**
     * AONE-15495:Update multiple files in Cloud.
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 700000)
    public void AONE_15495() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
        String opSiteName = getSiteName(testName) + "-OP";
        String opFileName1 = getFileName(testName) + "1";
        String opFileName2 = getFileName(testName) + "2";
        String cloudSiteName = getSiteName(testName) + "-CL1";
        String folderName = getFolderName(testName);
        String cloudUser1 = getUserNameForDomain(testName + "cloudUser1", testDomain1);

        // ---- Step 1 ----
        // ---- Step action ----
        // Select multiple files and sync all files..
        // ---- Expected results ----
        // All files are synced.
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        documentLibraryPage.getFileDirectoryInfo(opFileName1).selectCheckbox();
        documentLibraryPage.getFileDirectoryInfo(opFileName2).selectCheckbox();

        DocumentLibraryNavigation documentLibraryNavigation = new DocumentLibraryNavigation(drone);
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryNavigation.selectSyncToCloud().render();
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectSubmitButtonToSync().render();
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(opFileName1).isCloudSynced(), opFileName1 + " is not synced");
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(opFileName2).isCloudSynced(), opFileName2 + " is not synced");
        Assert.assertTrue(checkIfContentIsSynced(drone, opFileName1), "File didn't sync");

        // ---- Step 2 ----
        // ---- Step action ----
        // Move in Cloud all these files to a different folder and update the content.
        // ---- Expected results ----
        // The files are moved and edited.
        ShareUser.login(hybridDrone, cloudUser1);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        documentLibraryPage.getFileDirectoryInfo(opFileName1).selectCheckbox();
        documentLibraryPage.getFileDirectoryInfo(opFileName2).selectCheckbox();

        DocumentLibraryNavigation documentLibraryNavigationCloud = new DocumentLibraryNavigation(hybridDrone);
        CopyOrMoveContentPage moveToFolder = documentLibraryNavigationCloud.selectMoveTo().render();
        moveToFolder.selectPath(folderName).render().selectOkButton().render();

        documentLibraryPage.render();
        documentLibraryPage = documentLibraryPage.selectFolder(folderName).render();
        EditDocumentPropertiesPage editDocumentPropertiesPage = documentLibraryPage.getFileDirectoryInfo(opFileName1).selectEditProperties().render();
        editDocumentPropertiesPage.setDocumentTitle(opFileName1 + testName);
        documentLibraryPage = editDocumentPropertiesPage.selectSave().render();

        editDocumentPropertiesPage = documentLibraryPage.getFileDirectoryInfo(opFileName2).selectEditProperties().render();
        editDocumentPropertiesPage.setDocumentTitle(opFileName2 + testName);
        documentLibraryPage = editDocumentPropertiesPage.selectSave().render();

        // ---- Step 3 ----
        // ---- Step action ----
        // Check and confirm the content is synced and updated in Alfresco Enterprise.
        // ---- Expected results ----
        // The content is synced and updated in Alfresco Enterprise
        ShareUser.login(drone, opUser1);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(opFileName1).isCloudSynced());
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(opFileName2).isCloudSynced());
        ShareUser.refreshSharePage(drone);
        documentLibraryPage.getFileDirectoryInfo(opFileName1).selectRequestSync();
        waitForSync(drone, opFileName1, opSiteName);

        documentLibraryPage.render();
        editDocumentPropertiesPage = documentLibraryPage.getFileDirectoryInfo(opFileName1).selectEditProperties().render();
        Assert.assertTrue(editDocumentPropertiesPage.getDocumentTitle().equals(opFileName1 + testName), "Title is not updated");
        editDocumentPropertiesPage.selectCancel();
        documentLibraryPage.render();
        editDocumentPropertiesPage = documentLibraryPage.getFileDirectoryInfo(opFileName2).selectEditProperties().render();
        Assert.assertTrue(editDocumentPropertiesPage.getDocumentTitle().equals(opFileName2 + testName), "Title is not updated");
        editDocumentPropertiesPage.selectCancel();
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15497() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser1 };
        String cloudUser1 = getUserNameForDomain(testName + "cloudUser", testDomain1);
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL1";
        String opFileName1 = getFileName(testName) + "1.txt";
        String[] opFileInfo1 = new String[] { opFileName1, DOCLIB };
        String opFileName2 = getFileName(testName) + "2.txt";
        String[] opFileInfo2 = new String[] { opFileName2, DOCLIB };

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
        ShareUser.uploadFileInFolder(drone, opFileInfo1).render();
        ShareUser.uploadFileInFolder(drone, opFileInfo2).render();
    }

    /**
     * AONE-15497:Closing the browser. Multiple files.
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 500000)
    public void AONE_15497() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser", testDomain);
        String opSiteName = getSiteName(testName) + "-OP";
        String opFileName1 = getFileName(testName) + "1.txt";
        String opFileName2 = getFileName(testName) + "2.txt";
        String cloudSiteName = getSiteName(testName) + "-CL1";
        String cloudUser1 = getUserNameForDomain(testName + "cloudUser", testDomain1);
        WebDrone thisDrone;
        setupCustomDrone(WebDroneType.HybridDrone);
        thisDrone = customDrone;

        // ---- Step 1 ----
        // ---- Step action ----
        // Select multiple files and sync them to cloud, whilst syncing close the browse.
        // ---- Expected results ----
        // Files are synced to Cloud.
        ShareUser.login(thisDrone, opUser1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(thisDrone, opSiteName).render();
        documentLibraryPage.getFileDirectoryInfo(opFileName1).selectCheckbox();
        documentLibraryPage.getFileDirectoryInfo(opFileName2).selectCheckbox();

        DocumentLibraryNavigation documentLibraryNavigation = new DocumentLibraryNavigation(thisDrone);
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryNavigation.selectSyncToCloud().render();
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectSubmitButtonToSync().render();
        if (documentLibraryPage.isSyncMessagePresent())
        {
            thisDrone.closeWindow();
        }

        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        waitForDocument(opFileName1, documentLibraryPage);
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(opFileName1).isCloudSynced(), "File is not synced");

        waitForDocument(opFileName2, documentLibraryPage);
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(opFileName2).isCloudSynced(), "File is not synced");
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15498() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser1 };

        String opSiteName = getSiteName(testName) + "-OP";
        String opFileName1 = getFileName(testName);
        String[] opFileInfo1 = new String[] { opFileName1, DOCLIB };

        // TODO: remove Step 1 and 2 from Preconditions from TestLink (user should not be connected to cloud)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.uploadFileInFolder(drone, opFileInfo1).render();
    }

    /**
     * AONE-15498: Invalid username and password.
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
    public void AONE_15498() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser", testDomain);
        String opSiteName = getSiteName(testName) + "-OP";
        String opFileName = getFileName(testName);
        String cloudUserFake = getUserNameForDomain(testName + "clUser777", testDomain1);

        // ---- Step 1 ----
        // ---- Step action ----
        // Select multiple files and sync them to cloud, whilst syncing close the browse.
        // ---- Expected results ----
        // Files are synced to Cloud.
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        documentLibraryPage.getFileDirectoryInfo(opFileName).selectCheckbox();
        DocumentLibraryNavigation documentLibraryNavigation = new DocumentLibraryNavigation(drone);
        CloudSignInPage cloudSignInPage = documentLibraryNavigation.selectSyncToCloud().render();
        cloudSignInPage.loginAs(cloudUserFake, DEFAULT_PASSWORD);
        Assert.assertTrue(cloudSignInPage.isAccountNotRecognised());
        Assert.assertEquals(cloudSignInPage.getAccountNotRecognisedError(), "Email or password not recognised");
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15500() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
        String[] userInfo1 = new String[] { opUser1 };

        String cloudUser1 = getUserNameForDomain(testName + "cloudUser1", testDomain1);
        String[] cloudUserInfo1 = new String[] { cloudUser1 };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL1";
        String opFileName1 = getFileName(testName) + "1";
        String[] opFileInfo1 = new String[] { opFileName1, DOCLIB };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        DocumentLibraryPage doclib = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.uploadFileInFolder(drone, opFileInfo1).render();

        DestinationAndAssigneePage destinationAndAssigneePage = doclib.getFileDirectoryInfo(opFileName1).selectSyncToCloud().render();
        destinationAndAssigneePage.selectNetwork(testDomain1);
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectFolder("Documents");
        destinationAndAssigneePage.clickSyncButton();
        doclib.isSyncMessagePresent();
        doclib.render();
        checkIfContentIsSynced(drone, opFileName1);
    }

    /**
     * AONE-15500: Sync fail. Check status.
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 600000)
    public void AONE_15500() throws Exception
    {
        String testName = getTestName() + uniqueRun;
        String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
        String opSiteName = getSiteName(testName) + "-OP";
        String opFileName = getFileName(testName) + "1";
        String cloudSiteName = getSiteName(testName) + "-CL1";

        // ---- Step 1 ----
        // ---- Step action ----
        // Sync a file that is bound to fail, check the sync status.
        // ---- Expected results ----
        // “Sync failed” status should be displayed.
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        documentLibraryPage.getFileDirectoryInfo(opFileName).selectUnSyncAndRemoveContentFromCloud(false);
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(opFileName).selectSyncToCloud().render();
        destinationAndAssigneePage.selectNetwork(testDomain1);
        destinationAndAssigneePage.selectSite(cloudSiteName);
        destinationAndAssigneePage.selectFolder("Documents");
        destinationAndAssigneePage.clickSyncButton();
        documentLibraryPage.isSyncMessagePresent();
        Assert.assertTrue(checkIfSyncFailed(drone, opFileName), "Sync is created");
    }

    private void waitForDocument(String fileName, DocumentLibraryPage docLib)
    {
        int counter = 1;
        int retryRefreshCount = 15;
        while (counter <= retryRefreshCount)
        {
            if (docLib.isFileVisible(fileName))
            {
                break;
            }
            else
            {
                logger.info("Wait for document to sync");
                drone.refresh();
                counter++;
            }
        }
    }

}
