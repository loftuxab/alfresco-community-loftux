package org.alfresco.share.cloudsync;

import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryNavigation;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.site.document.RevertToVersionPage;
import org.alfresco.po.share.site.document.SyncInfoPage;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Created by P3700445 on 1/16/2015.
 */

@Listeners(FailedTestListener.class)
public class HybridSyncPositiveTests2 extends AbstractWorkflow
{

        private static Log logger = LogFactory.getLog(HybridSyncPositiveTests.class);

        protected String testUser;
        private String testDomain;

        @Override
        @BeforeClass(alwaysRun = true)
        public void setup() throws Exception
        {
                super.setup();
                logger.info("Starting Tests: " + testName);
                testName = this.getClass().getSimpleName();
                testDomain = DOMAIN_HYBRID;
        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_15431() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };
                String folderName = getFolderName(testName) + getTestName();

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

                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                // sync to the Cloud
                ShareUserSitePage.createFolder(drone, folderName, "").render();
                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(folderName).selectSyncToCloud().render();
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectSubmitButtonToSync().render();

                ShareUser.logout(drone);
        }

        /**
         * AONE-15431:Sync a folder. Cloud location
         */

        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15431() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String folderName = getFolderName(testName) + getTestName();

                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";
                String syncLocation = testDomain + ">" + cloudSiteName + ">" + "Documents" + ">" + folderName;

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

                // --- Step 1 ---
                // --- Step action ---
                // Go to the Document Library of created in preconditions in Alfresco Share (On-premise) site
                // --- Expected results ---
                // Document Library page is opened and all information is displayed correctly on it
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                Assert.assertTrue(documentLibraryPage.isBrowserTitle("Alfresco » Document Library"));

                // --- Step 2 ---
                // --- Step action ---
                // Click cloud icon near the folder which is synced
                // --- Expected results ---
                // Pop up dialogue with sync info appears. Cloud location info is displayed
                SyncInfoPage syncInfoPage = documentLibraryPage.getFileDirectoryInfo(folderName).clickOnViewCloudSyncInfo().render();
                Assert.assertTrue(syncInfoPage.getCloudSyncStatus().contains("Synced"));
                Assert.assertEquals(syncInfoPage.getCloudSyncLocation(), syncLocation);

                ShareUser.logout(drone);
        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_15432() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String fileName1 = getTestName() + "1" + ".txt";
                String[] fileInfo1 = new String[] { fileName1, DOCLIB };
                String fileName2 = getTestName() + "2" + ".txt";
                String[] fileInfo2 = new String[] { fileName2, DOCLIB };

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

                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

                ShareUser.uploadFileInFolder(drone, fileInfo1).render();

                // fileName1 sync to the Cloud
                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(fileName1).selectSyncToCloud().render();
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectSubmitButtonToSync().render(5000);

                // fileName2 sync to the Cloud
                ShareUser.uploadFileInFolder(drone, fileInfo2).render();
                destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(fileName2).selectSyncToCloud().render();
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectSubmitButtonToSync().render();

                ShareUser.logout(drone);
        }

        /**
         * AONE-15432:Sync multiple files. Cloud location
         */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15432() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String fileName1 = getTestName() + "1" + ".txt";
                String fileName2 = getTestName() + "2" + ".txt";

                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";
                String syncLocation = testDomain + ">" + cloudSiteName + ">" + "Documents";

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

                // --- Step 1 ---
                // --- Step action ---
                // Go to the Document Library of created in preconditions in Alfresco Share (On-premise) site
                // --- Expected results ---
                // Document Library page is opened and all information is displayed correctly on it
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                logger.info("Title  --- " + documentLibraryPage.getTitle());
                Assert.assertTrue(documentLibraryPage.isBrowserTitle("Alfresco » Document Library"));

                // --- Step 2 ---
                // --- Step action ---
                // Click on cloud icon for each file and verify cloud location
                // --- Expected results ---
                // Cloud location is displayed correctly for each file
                SyncInfoPage syncInfoPage = documentLibraryPage.getFileDirectoryInfo(fileName1).clickOnViewCloudSyncInfo().render();
                Assert.assertTrue(syncInfoPage.getCloudSyncStatus().contains("Synced"));
                Assert.assertEquals(syncInfoPage.getCloudSyncLocation(), syncLocation);

                syncInfoPage = documentLibraryPage.getFileDirectoryInfo(fileName2).clickOnViewCloudSyncInfo().render();
                Assert.assertTrue(syncInfoPage.getCloudSyncStatus().contains("Synced"));
                Assert.assertEquals(syncInfoPage.getCloudSyncLocation(), syncLocation);

                ShareUser.logout(drone);
        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_15434() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String fileName = getTestName() + ".txt";
                String[] fileInfo = new String[] { fileName, DOCLIB };

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

                ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

                DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo).render();

                // sync to the Cloud
                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(fileName).selectSyncToCloud().render();
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectSubmitButtonToSync().render();
                ShareUser.logout(drone);
        }

        /**
         * AONE-15434:Unsync file
         */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15434() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String fileName = getTestName() + ".txt";
                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

                // --- Step 1 ---
                // --- Step action ---
                // Go to the Document Library of created in preconditions in Alfresco Share (On-premise) site
                // --- Expected results ---
                // Document Library page is opened and all information is displayed correctly on it
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                Assert.assertTrue(documentLibraryPage.isBrowserTitle("Alfresco » Document Library"));

                // --- Step 2 ---
                // --- Step action ---
                // Click cloud icon against the folder which is synced
                // --- Expected results ---
                // Pop up dialogue with sync info appears.
                SyncInfoPage syncInfoPage = documentLibraryPage.getFileDirectoryInfo(fileName).clickOnViewCloudSyncInfo().render();
                Assert.assertTrue(syncInfoPage.getCloudSyncStatus().contains("Synced"));

                // --- Step 3 ---
                // --- Step action ---
                // Verify Unsync button is available on Sync info pop-up window
                // --- Expected results ---
                // Unsync button is available and enabled
                Assert.assertTrue(syncInfoPage.isUnsyncButtonPresent());
                Assert.assertTrue(syncInfoPage.isUnsyncButtonEnabled());

                // --- Step 4 ---
                // --- Step action ---
                // Click on Unsync button
                // --- Expected results ---
                // Folder unsynced notification appears
                syncInfoPage.selectUnsyncRemoveContentFromCloud(true);

                // --- Step 5 ---
                // --- Step action ---
                // Go to the target cloud location which was setin preconditions for sync
                // --- Expected results ---
                // Target location in Cloud (Network->Site->Document Library page) is opened. Unsynced folder removed from Cloud
                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
                Assert.assertFalse(documentLibraryPage.isItemVisble(fileName));

                ShareUser.logout(drone);
        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_15435() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String folderName = getFolderName(testName) + getTestName();

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

                ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

                DocumentLibraryPage documentLibraryPage = ShareUserSitePage.createFolder(drone, folderName, "").render();

                // sync to the Cloud
                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(folderName).selectSyncToCloud().render();
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectSubmitButtonToSync().render();
                ShareUser.logout(drone);
        }

        /**
         * AONE-15435:Unsync folder
         */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15435() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String folderName = getFolderName(testName) + getTestName();
                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

                // --- Step 1 ---
                // --- Step action ---
                // Go to the Document Library of created in preconditions in Alfresco Share (On-premise) site
                // --- Expected results ---
                // Document Library page is opened and all information is displayed correctly on it
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                Assert.assertTrue(documentLibraryPage.isBrowserTitle("Alfresco » Document Library"));

                // --- Step 2 ---
                // --- Step action ---
                // Click cloud icon afainst the file which is synced
                // --- Expected results ---
                // Pop up dialogue with sync info appears.
                SyncInfoPage syncInfoPage = documentLibraryPage.getFileDirectoryInfo(folderName).clickOnViewCloudSyncInfo().render();
                Assert.assertTrue(syncInfoPage.getCloudSyncStatus().contains("Synced"));

                // --- Step 3 ---
                // --- Step action ---
                // Verify Unsync button is available on Sync info pop-up window
                // --- Expected results ---
                // Unsync button is available and enabled
                Assert.assertTrue(syncInfoPage.isUnsyncButtonPresent());
                Assert.assertTrue(syncInfoPage.isUnsyncButtonEnabled());

                // --- Step 4 ---
                // --- Step action ---
                // Click on Unsync button
                // --- Expected results ---
                // File unsynced notification appears
                syncInfoPage.selectUnsyncRemoveContentFromCloud(true);

                // --- Step 5 ---
                // --- Step action ---
                // Go to the target cloud location which was setin preconditions for sync
                // --- Expected results ---
                // Target location in Cloud (Network->Site->Document Library page) is opened. Unsynced file removed from Cloud
                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
                Assert.assertFalse(documentLibraryPage.isItemVisble(folderName));

                ShareUser.logout(drone);
        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_15436() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String fileName = getTestName() + ".txt";
                String[] fileInfo = new String[] { fileName, DOCLIB };

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

                ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

                DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo).render();

                // sync to the Cloud
                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(fileName).selectSyncToCloud().render();
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectSubmitButtonToSync().render();

                ShareUser.logout(drone);

        }

        /**
         * AONE-15436:Sync info icon for synced file in Cloud
         */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15436() throws Exception
        {
                String testName = getTestName();
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String fileName = getTestName() + ".txt";
                String cloudSiteName = getSiteName(testName) + "-CL";

                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

                // --- Step 1 ---
                // --- Step action ---
                // Go to Cloud location where file was synced in step6
                // --- Expected results ---
                // Cloud location (e.g. site->document library-> folder) is opened, synced item with sync info icon against it is displayed on the page
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
                Assert.assertTrue(documentLibraryPage.isBrowserTitle("Alfresco » Document Library"));
                Assert.assertTrue(documentLibraryPage.isItemVisble(fileName));

                // --- Step 2 ---
                // --- Step action ---
                // Navigate to Sync info icon against synced file
                // --- Expected results ---
                // 'Click to view sync info' tooltip appears
                Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(fileName).getSyncInfoToolTip(), "Click to view sync info");

                // --- Step 3 ---
                // --- Step action ---
                // Click on Sync info icon
                // --- Expected results ---
                // Pop up window with information on it appears
                SyncInfoPage syncInfoPage = documentLibraryPage.getFileDirectoryInfo(fileName).clickOnViewCloudSyncInfo().render();
                Assert.assertTrue(syncInfoPage.getCloudSyncStatus().contains("Synced"));

                ShareUser.logout(hybridDrone);
        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_15437() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };
                String folderName = getFolderName(testName) + getTestName();

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

                // create an empty folder
                ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUserSitePage.createFolder(drone, folderName, "").render();

                ShareUser.logout(drone);
        }

        /**
         * AONE-15437:Sync an empty folder to Cloud
         */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15437() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String folderName = getFolderName(testName) + getTestName();

                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

                // --- Step 1 ---
                // --- Step action ---
                // Set cursor to the folder and expand More+ menu
                // --- Expected results ---
                // List of actions is appeared for folder and 'sync to cloud' option available
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(folderName).isSyncToCloudLinkPresent());

                // --- Step 2 ---
                // --- Step action ---
                // Choose 'Sync to Cloud' option from More+ menu or from list of folder actions on details page or click on 'Sync to Cloud' on top panel of opened
                // folder
                // --- Expected results ---
                // Pop-up window to select target cloud location appears.
                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(folderName).selectSyncToCloud().render();
                Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + folderName + " to The Cloud"));

                // --- Step 3 ---
                // --- Step action ---
                // Choose target location in the Cloud (network->site->document library) and press OK button
                // --- Expected results ---
                // 'Sync created' notification appears
                destinationAndAssigneePage.selectNetwork(testDomain);
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents");
                destinationAndAssigneePage.clickSyncButton();
                Assert.assertTrue(documentLibraryPage.isSyncMessagePresent());

                ShareUser.logout(drone);
                // --- Step 4 ---
                // --- Step action ---
                // Go to Cloud location set in previous step
                // --- Expected results ---
                // Folder for which sync action was applied is displayed.
                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
                Assert.assertTrue(documentLibraryPage.isItemVisble(folderName));

                ShareUser.logout(hybridDrone);
        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_15438() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String folderName = getFolderName(testName) + getTestName();
                String fileName1 = getTestName() + "1" + ".txt";
                String[] fileInfo1 = new String[] { fileName1, folderName };
                String fileName2 = getTestName() + "2" + ".txt";
                String[] fileInfo2 = new String[] { fileName2, folderName };

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

                // create an empty folder
                ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                DocumentLibraryPage documentLibraryPage = ShareUserSitePage.createFolder(drone, folderName, "").render();
                documentLibraryPage.selectFolder(folderName).render();
                ShareUser.uploadFileInFolder(drone, fileInfo1).render();
                ShareUser.uploadFileInFolder(drone, fileInfo2).render();

                ShareUser.logout(drone);

        }

        /**
         * AONE-15438:Sync a non-empty folder to Cloud
         */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15438() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String folderName = getFolderName(testName) + getTestName();
                String fileName1 = getTestName() + "1" + ".txt";
                String fileName2 = getTestName() + "2" + ".txt";
                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

                // --- Step 1 ---
                // --- Step action ---
                // Set cursor to the folder and expand More+ menu
                // --- Expected results ---
                // List of actions is appeared for folder and 'sync to cloud' option available
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(folderName).isSyncToCloudLinkPresent());

                // --- Step 2 ---
                // --- Step action ---
                // Choose 'Sync to Cloud' option from More+ menu or from list of folder actions on details page or click on 'Sync to Cloud' on top panel of opened
                // folder
                // --- Expected results ---
                // Pop-up window to select target cloud location appears.
                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(folderName).selectSyncToCloud().render();
                Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + folderName + " to The Cloud"));

                // --- Step 3 ---
                // --- Step action ---
                // Choose target location in the Cloud (network->site->document library) and press OK button
                // --- Expected results ---
                // 'Sync created' notification appears
                destinationAndAssigneePage.selectNetwork(testDomain);
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents");
                destinationAndAssigneePage.clickSyncButton();
                Assert.assertTrue(documentLibraryPage.isSyncMessagePresent());

                ShareUser.logout(drone);

                // --- Step 4 ---
                // --- Step action ---
                // Wait for some time and go to Cloud location set in previous step
                // --- Expected results ---
                // Folder for which sync action was applied is displayed successfully.
                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
                Assert.assertTrue(documentLibraryPage.isItemVisble(folderName));

                // --- Step 5 ---
                // --- Step action ---
                // Open synced folder in Cloud
                // --- Expected results ---
                // All files uploaded in precoditions to folder are displayed on the page
                documentLibraryPage.selectFolder(folderName).render();
                Assert.assertTrue(documentLibraryPage.isItemVisble(fileName1));
                Assert.assertTrue(documentLibraryPage.isItemVisble(fileName2));

                ShareUser.logout(hybridDrone);

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_15439() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFolderName = getFolderName(testName) + "OP";
                String clFolderName = getFolderName(testName) + "CL";

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
                ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
                ShareUserSitePage.createFolder(hybridDrone, clFolderName, "").render();
                ShareUser.logout(hybridDrone);

                // Login to User1, set up the cloud sync
                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

                // create an empty folder
                ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUserSitePage.createFolder(drone, opFolderName, "").render();
                ShareUser.logout(drone);

        }

        /**
         * AONE-15439:Sync folder to target folder in Cloud
         */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15439() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);

                String opFolderName = getFolderName(testName) + "OP";
                String clFolderName = getFolderName(testName) + "CL";

                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

                // --- Step 1 ---
                // --- Step action ---
                // Set cursor to the folder and expand More+ menu
                // --- Expected results ---
                // List of actions is appeared for folder and 'sync to cloud' option available
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(opFolderName).isSyncToCloudLinkPresent());

                // --- Step 2 ---
                // --- Step action ---
                // Choose 'Sync to Cloud' option from More+ menu or from list of folder actions on details page or click on 'Sync to Cloud' on top panel of opened
                // folder
                // --- Expected results ---
                // Pop-up window to select target cloud location appears.
                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(opFolderName).selectSyncToCloud().render();
                Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + opFolderName + " to The Cloud"));

                // --- Step 3 ---
                // --- Step action ---
                // Choose target location in the Cloud (network->site->document library) and press OK button
                // --- Expected results ---
                // 'Sync created' notification appears
                destinationAndAssigneePage.selectNetwork(testDomain);
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder(clFolderName);
                destinationAndAssigneePage.clickSyncButton();
                Assert.assertTrue(documentLibraryPage.isSyncMessagePresent());

                ShareUser.logout(drone);

                // --- Step 4 ---
                // --- Step action ---
                // Go to Cloud location set in previous step
                // --- Expected results ---
                // Folder for which sync action was applied is displayed.
                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
                documentLibraryPage.selectFolder(clFolderName).render();
                Assert.assertTrue(documentLibraryPage.isItemVisble(opFolderName));

                ShareUser.logout(hybridDrone);

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_15440() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFolderName = getFolderName(testName) + "OP";
                String clFolderName1 = getFolderName(testName) + "CL1";
                String clFolderName2 = getFolderName(testName) + "CL2";

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
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
                ShareUserSitePage.createFolder(hybridDrone, clFolderName1, "").render();
                documentLibraryPage.selectFolder(clFolderName1).render();
                ShareUserSitePage.createFolder(hybridDrone, clFolderName2, "").render();

                ShareUser.logout(hybridDrone);

                // Login to User1, set up the cloud sync
                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

                // create an empty folder
                ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUserSitePage.createFolder(drone, opFolderName, "").render();
                ShareUser.logout(drone);
        }

        /**
         * AONE-15440:Sync folder to target sub folder in Cloud
         */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15440() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);

                String opFolderName = getFolderName(testName) + "OP";
                String clFolderName1 = getFolderName(testName) + "CL1";
                String clFolderName2 = getFolderName(testName) + "CL2";

                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

                // --- Step 1 ---
                // --- Step action ---
                // Set cursor to the folder and expand More+ menu
                // --- Expected results ---
                // List of actions is appeared for folder and 'sync to cloud' option available
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(opFolderName).isSyncToCloudLinkPresent());

                // --- Step 2 ---
                // --- Step action ---
                // Choose 'Sync to Cloud' option from More+ menu or from list of folder actions on details page or click on 'Sync to Cloud' on top panel of opened
                // folder
                // --- Expected results ---
                // Pop-up window to select target cloud location appears.
                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(opFolderName).selectSyncToCloud().render();
                Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + opFolderName + " to The Cloud"));

                // --- Step 3 ---
                // --- Step action ---
                // Choose target location in the Cloud (network->site->document library) and press OK button
                // --- Expected results ---
                // 'Sync created' notification appears
                destinationAndAssigneePage.selectNetwork(testDomain);
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents", clFolderName1, clFolderName2);
                destinationAndAssigneePage.clickSyncButton();
                Assert.assertTrue(documentLibraryPage.isSyncMessagePresent());

                ShareUser.logout(drone);

                // --- Step 4 ---
                // --- Step action ---
                // Go to Cloud location set in previous step
                // --- Expected results ---
                // Folder for which sync action was applied is displayed.
                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
                documentLibraryPage.selectFolder(clFolderName1).render();
                documentLibraryPage.selectFolder(clFolderName2).render();
                Assert.assertTrue(documentLibraryPage.isItemVisble(opFolderName));

                ShareUser.logout(hybridDrone);
        }

        @Test(groups = "DataPrepHybrid", timeOut = 300000)
        public void dataPrep_AONE_15443() throws Exception
        {
                String testUniqueTag = "_Z7";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFolderName = getFolderName(testName);
                String opSiteName = getSiteName(testName) + "-OP" + testUniqueTag;
                String cloudSiteName = getSiteName(testName) + "-CL" + testUniqueTag;
                String fileName1 = getTestName() + "1" + ".txt";
                String[] fileInfo1 = new String[] { fileName1, opFolderName };
                String fileName2 = getTestName() + "2" + ".txt";
                String[] fileInfo2 = new String[] { fileName2, opFolderName };

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

                // create an empty folder
                ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                DocumentLibraryPage documentLibraryPage = ShareUserSitePage.createFolder(drone, opFolderName, "").render();

                ShareUser.uploadFileInFolder(drone, fileInfo1).render();
                ShareUser.uploadFileInFolder(drone, fileInfo2).render();

                // folder sync to the Cloud
                documentLibraryPage.selectDocumentLibrary(drone).render();
                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(opFolderName).selectSyncToCloud().render();
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectSubmitButtonToSync().render(5000);
                ShareUser.logout(drone);
        }

        /**
         * AONE-15443:Request sync. Changes to some files in synced folder
         */
        @Test(groups = "Hybrid", timeOut = 500000)
        public void AONE_15443() throws Exception
        {
                String testUniqueTag = "_Z7";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFolderName = getFolderName(testName);
                String opSiteName = getSiteName(testName) + "-OP" + testUniqueTag;
                String cloudSiteName = getSiteName(testName) + "-CL" + testUniqueTag;
                String fileName1 = getTestName() + "1" + ".txt";
                String[] fileInfo1 = new String[] { fileName1, DOCLIB };
                String fileName2 = getTestName() + "2" + ".txt";
                String[] fileInfo2 = new String[] { fileName2, DOCLIB };

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

                // --- Step 1 ---
                // --- Step action ---
                // Make some changes at least to one of the files in synced folder in on-premise
                // --- Expected results ---
                // Changes are made and saved for files in synced folder in on- premise
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).selectFolder(opFolderName).render();
                EditDocumentPropertiesPage editDocumentPropertiesPage = documentLibraryPage.getFileDirectoryInfo(fileName1).selectEditProperties().render();
                editDocumentPropertiesPage.setDocumentTitle(fileName1 + "Edited");
                documentLibraryPage = editDocumentPropertiesPage.selectSave().render();

                Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(fileName1).getTitle(), "(" + fileName1 + "Edited)",
                        "The changes from Edit Properties were not saved");

                // --- Step 2 ---
                // --- Step action ---
                // Choose 'Request sync' option from More+ menu or from Folder actions on Details page for synced root folder where changed files are situated or click on 'Request sync' button on top panel of opened folder
                // folder
                // --- Expected results ---
                // Changes synced immediately to Cloud

                documentLibraryPage.selectDocumentLibrary(drone).getFileDirectoryInfo(opFolderName).selectRequestSync().render();
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(opFolderName).isCloudSynced(), "The folder was not synced to Cloud");

                // --- Step 3 ---
                // --- Step action ---
                // Go to Cloud target location where folder was synced in preconditions and open it to verify applied changes made to the file in step1
                // --- Expected results ---
                // Changes are applied successfully to the file

                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                DocumentLibraryPage documentLibraryPageCL = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();

                documentLibraryPageCL.selectFolder(opFolderName).render();

                Assert.assertEquals(documentLibraryPageCL.getFileDirectoryInfo(fileName1).getTitle(), "(" + fileName1 + "Edited)",
                        "The changes made in OP were not synced to Cloud");

                // --- Step 4 ---
                // --- Step action ---
                //  On Cloud revert to first version
                // --- Expected results ---
                //  Node on cloud shown as synced and changes are reverted

                DocumentDetailsPage documentDetailsPage = documentLibraryPageCL.getFileDirectoryInfo(fileName1).clickOnTitle().render();
                RevertToVersionPage revertToVersionPage = documentDetailsPage.selectRevertToVersion("1.0").render();
                revertToVersionPage.submit().render();
                documentLibraryPageCL = documentDetailsPage.getSiteNav().selectSiteDocumentLibrary().selectFolder(opFolderName).render();

                editDocumentPropertiesPage = documentLibraryPageCL.getFileDirectoryInfo(fileName1).selectEditProperties().render();
                Assert.assertFalse(editDocumentPropertiesPage.isTitlePresent(fileName1 + "Edited"), "The changes were not revert");
                documentLibraryPageCL = editDocumentPropertiesPage.selectCancel().render();
                Assert.assertTrue(documentLibraryPageCL.getFileDirectoryInfo(fileName1).isIndirectlySyncedIconPresent(),
                        "The file is no longer synced after changes are reverted");

                // --- Step 5 ---
                // --- Step action ---
                // Now make changes to the file on the Cloud (content or metadata)
                // --- Expected results ---
                // Changes are synced

                editDocumentPropertiesPage = documentLibraryPageCL.getFileDirectoryInfo(fileName1).selectEditProperties().render();
                editDocumentPropertiesPage.setDocumentTitle(fileName1 + " Cloud Edit");
                editDocumentPropertiesPage.selectSave().render();

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).selectFolder(opFolderName).render();
                Assert.assertEquals(documentLibraryPageCL.getFileDirectoryInfo(fileName1).getTitle(), "(" + fileName1 + " Cloud Edit)",
                        "The changes were not revert");

        }

        @Test(groups = "DataPrepHybrid", timeOut = 300000)
        public void dataPrep_AONE_15444() throws Exception
        {

                String testUniqueTag = "_Z7";

                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opSiteName = getSiteName(testName) + "-OP" + testUniqueTag;
                String cloudSiteName = getSiteName(testName) + "-CL" + testUniqueTag;
                String fileName1 = getTestName() + "1" + ".txt";
                String[] fileInfo1 = new String[] { fileName1, DOCLIB };
                String fileName2 = getTestName() + "2" + ".txt";
                String[] fileInfo2 = new String[] { fileName2, DOCLIB };

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

                // create an empty folder
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

                // fileName1 sync to the Cloud
                ShareUser.uploadFileInFolder(drone, fileInfo1).render();
                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(fileName1).selectSyncToCloud().render();
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectSubmitButtonToSync().render(5000);

                // fileName1 sync to the Cloud
                ShareUser.uploadFileInFolder(drone, fileInfo2).render();
                destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(fileName2).selectSyncToCloud().render();
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectSubmitButtonToSync().render(5000);

                ShareUser.logout(drone);
        }

        /**
         * AONE-15444:Request sync. Change some multiple files
         */
        @Test(groups = "Hybrid", timeOut = 500000)
        public void AONE_15444() throws Exception
        {

                String testUniqueTag = "_Z7";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opSiteName = getSiteName(testName) + "-OP" + testUniqueTag;
                String cloudSiteName = getSiteName(testName) + "-CL" + testUniqueTag;
                String fileName1 = getTestName() + "1" + ".txt";
                String[] fileInfo1 = new String[] { fileName1, DOCLIB };
                String fileName2 = getTestName() + "2" + ".txt";
                String[] fileInfo2 = new String[] { fileName2, DOCLIB };

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

                // --- Step 1 ---
                // --- Step action ---
                // Make some changes to all synced files in on-premise
                // --- Expected results ---
                // Changes are made and saved for synced files in on- premise
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                EditDocumentPropertiesPage editDocumentPropertiesPage = documentLibraryPage.getFileDirectoryInfo(fileName1).selectEditProperties().render();
                editDocumentPropertiesPage.setDocumentTitle(fileName1 + "Edited");
                documentLibraryPage = editDocumentPropertiesPage.selectSave().render();
                Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(fileName1).getTitle(), "(" + fileName1 + "Edited)",
                        "The changes from Edit Properties were not saved");

                editDocumentPropertiesPage = documentLibraryPage.getFileDirectoryInfo(fileName2).selectEditProperties().render();
                editDocumentPropertiesPage.setDocumentTitle(fileName2 + "Edited");
                documentLibraryPage = editDocumentPropertiesPage.selectSave().render();
                Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(fileName2).getTitle(), "(" + fileName2 + "Edited)",
                        "The changes from Edit Properties were not saved");

                // --- Step 2 ---
                // --- Step action ---
                // Select all changed synced files and choose 'Request sync' option from Selected items... drop down menu
                // --- Expected results ---
                // Sync passed

                documentLibraryPage.getFileDirectoryInfo(fileName1).selectCheckbox();
                documentLibraryPage.getFileDirectoryInfo(fileName2).selectCheckbox();
                DocumentLibraryNavigation documentLibraryNavigation = new DocumentLibraryNavigation(drone);
                documentLibraryPage = documentLibraryNavigation.selectRequestSync().render(7000);

                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "The sync was not performed correctly");
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName2).isCloudSynced(), "The sync was not performed correctly");

                ShareUser.logout(drone);

                // --- Step 3 ---
                // --- Step action ---
                // Go to Cloud target location where file was synced in preconditions to verify applied changes
                // --- Expected results ---
                //  Changes are applied successfully to all files

                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                DocumentLibraryPage documentLibraryPageCL = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();

                Assert.assertEquals(documentLibraryPageCL.getFileDirectoryInfo(fileName1).getTitle(), "(" + fileName1 + "Edited)",
                        "The changes made in OP were not synced to Cloud");
                Assert.assertEquals(documentLibraryPageCL.getFileDirectoryInfo(fileName2).getTitle(), "(" + fileName2 + "Edited)",
                        "The changes made in OP were not synced to Cloud");

                ShareUser.logout(hybridDrone);
        }

        @Test(groups = "DataPrepHybrid", timeOut = 500000)
        public void dataPrep_AONE_15445() throws Exception
        {


                String testUniqueTag = "_Z7";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opSiteName = getSiteName(testName) + "-OP" + testUniqueTag;
                String cloudSiteName = getSiteName(testName) + "-CL" + testUniqueTag;

                String folderName1 = getFolderName(testName) + testUniqueTag + "1";
                String folderName2 = getFolderName(testName) + testUniqueTag + "2";

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

                // create an empty folder
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUserSitePage.createFolder(drone, folderName1, "").render();

                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(folderName1).selectSyncToCloud().render();
                destinationAndAssigneePage.isSiteDisplayed(cloudSiteName);
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectSubmitButtonToSync().render(5000);

                // fileName1 sync to the Cloud
                ShareUserSitePage.createFolder(drone, folderName2, "").render();
                DestinationAndAssigneePage destinationAndAssigneePage2 = documentLibraryPage.getFileDirectoryInfo(folderName2).selectSyncToCloud().render(10000);
                destinationAndAssigneePage2.isSiteDisplayed(cloudSiteName);
                destinationAndAssigneePage2.selectSite(cloudSiteName);
                destinationAndAssigneePage2.selectSubmitButtonToSync().render(5000);

                ShareUser.logout(drone);
        }

        /**
         * AONE-15445:Request sync. Change some multiple folders
         */
        @Test(groups = "Hybrid", timeOut = 500000)
        public void AONE_15445() throws Exception
        {
                String testUniqueTag = "_Z7";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);

                String opSiteName = getSiteName(testName) + "-OP" + testUniqueTag;
                String cloudSiteName = getSiteName(testName) + "-CL" + testUniqueTag;

                String folderName1 = getFolderName(testName) + testUniqueTag + "1";
                String folderName2 = getFolderName(testName) + testUniqueTag + "2";

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

                // --- Step 1 ---
                // --- Step action ---
                // Make some changes to all synced folders in on-premise
                // --- Expected results ---
                // Changes are made and saved for synced folders in on- premise

                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                EditDocumentPropertiesPage editDocumentPropertiesPage = documentLibraryPage.getFileDirectoryInfo(folderName1).selectEditProperties().render();
                editDocumentPropertiesPage.setDocumentTitle(folderName1 + "Edited");
                documentLibraryPage = editDocumentPropertiesPage.selectSave().render();
                Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(folderName1).getTitle(), "(" + folderName1 + "Edited)",
                        "The changes from Edit Properties were not saved");

                editDocumentPropertiesPage = documentLibraryPage.getFileDirectoryInfo(folderName2).selectEditProperties().render();
                editDocumentPropertiesPage.setDocumentTitle(folderName2 + "Edited");
                documentLibraryPage = editDocumentPropertiesPage.selectSave().render();
                Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(folderName2).getTitle(), "(" + folderName2 + "Edited)",
                        "The changes from Edit Properties were not saved");

                // --- Step 2 ---
                // --- Step action ---
                // Select all changed synced files and choose 'Request sync' option from Selected items... drop down menu
                // --- Expected results ---
                // Sync passed

                documentLibraryPage.getFileDirectoryInfo(folderName1).selectCheckbox();
                documentLibraryPage.getFileDirectoryInfo(folderName2).selectCheckbox();
                DocumentLibraryNavigation documentLibraryNavigation = new DocumentLibraryNavigation(drone);
                documentLibraryPage = documentLibraryNavigation.selectRequestSync().render();

                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(folderName1).isCloudSynced(), "The sync was not performed correctly");
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(folderName2).isCloudSynced(), "The sync was not performed correctly");

                ShareUser.logout(drone);

                // --- Step 3 ---
                // --- Step action ---
                // Go to Cloud target location where file was synced in preconditions to verify applied changes
                // --- Expected results ---
                //  Changes are applied successfully to all folders and displayed correctly

                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                DocumentLibraryPage documentLibraryPageCL = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();

                Assert.assertEquals(documentLibraryPageCL.getFileDirectoryInfo(folderName1).getTitle(), "(" + folderName1 + "Edited)",
                        "The changes made in OP were not synced to Cloud");
                Assert.assertEquals(documentLibraryPageCL.getFileDirectoryInfo(folderName2).getTitle(), "(" + folderName2 + "Edited)",
                        "The changes made in OP were not synced to Cloud");

                ShareUser.logout(hybridDrone);
        }
}
