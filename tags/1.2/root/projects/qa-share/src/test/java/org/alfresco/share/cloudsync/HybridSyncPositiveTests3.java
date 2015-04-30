package org.alfresco.share.cloudsync;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.CreateNewFolderInCloudPage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.site.document.EditTextDocumentPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.document.SyncInfoPage;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
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
public class HybridSyncPositiveTests3 extends AbstractWorkflow
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

        @Test(groups = "DataPrepHybrid", timeOut = 300000)
        public void dataPrep_AONE_15446() throws Exception
        {

                String uniqueName = "_Z7";
                String testName = getTestName();
                String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
                String[] userInfo1 = new String[] { opUser1 };
                String opUser2 = getUserNameForDomain(testName + "opUser2", testDomain);
                String[] userInfo2 = new String[] { opUser2 };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

                String fileName1 = getTestName() + "1" + ".txt";
                String[] fileInfo1 = new String[] { fileName1, DOCLIB };

                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo2);
                CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

                ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
                ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(drone);

                // Cloud user and site.
                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(hybridDrone);

                ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
                //Invite User2 to be a site member with Comsumer role
                ShareUserMembers.inviteUserToSiteWithRole(drone, opUser1, opUser2, opSiteName, UserRole.CONSUMER);
                // Login to User1, set up the cloud sync
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

                // add file into DOCLIB
                ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUser.uploadFileInFolder(drone, fileInfo1).render();

                ShareUser.logout(drone);
        }

        /**
         * AONE-15446:File sync icon.Synced directly
         */
        @Test(groups = "Hybrid", timeOut = 500000)
        public void AONE_15446() throws Exception
        {
                String uniqueName = "_Z7";
                String testName = getTestName();
                String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
                String opUser2 = getUserNameForDomain(testName + "opUser2", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);

                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

                String fileName1 = getTestName() + "1" + ".txt";

                ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

                // --- Step 1 ---
                // --- Step action ---
                // Set cursor to the file and expand More+ menu    or
                // Click on file's name to open its details page
                // --- Expected results ---
                // List of actions is appeared for folder and 'sync to cloud' option available  or
                // Details page for created file is successfully opened and 'sync to cloud' option is available among Document actions

                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                DocumentDetailsPage documentDetailsPage = documentLibraryPage.getFileDirectoryInfo(fileName1).clickOnTitle().render();

                Assert.assertTrue(documentDetailsPage.isSyncToCloudOptionDisplayed(), "The sync to cloud option is not available among Document actions");

                // --- Step 2 ---
                // --- Step action ---
                // Choose 'Sync to Cloud' option from More+ menu or from list of Document actions on details page
                // --- Expected results ---
                // Pop-up window to select target cloud location appears.

                DestinationAndAssigneePage destinationAndAssigneePage = documentDetailsPage.selectSyncToCloud().render();
                Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + fileName1 + " to The Cloud"));

                // --- Step 3 ---
                // --- Step action ---
                // Choose target location in the Cloud (network->site->document library) and press OK button
                // --- Expected results ---
                // 'Sync created' notification appears. File sync icon appears

                destinationAndAssigneePage.selectNetwork(testDomain);
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents");
                destinationAndAssigneePage.clickSyncButton();
                Assert.assertTrue(documentDetailsPage.isSyncMessagePresent());

                // --- Step 4 ---
                // --- Step action ---
                // Verify file sync icon state
                // --- Expected results ---
                // File sync icon has state 'synced directly'

                documentLibraryPage = documentDetailsPage.getSiteNav().selectSiteDocumentLibrary().render();
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "The synced directly icon is not displayed");

                ShareUser.logout(drone);

                // --- Step 5 ---
                // --- Step action ---
                // Log in to Alfresco Share (on-premise) as user2 (site consumer)
                // --- Expected results ---
                // User2 is logged in successfully

                ShareUser.login(drone, opUser2, DEFAULT_PASSWORD);
                DashBoardPage dashBoardPage = new DashBoardPage(drone);
                Assert.assertTrue(dashBoardPage.titlePresent(), "User2 did not logged in successfully");

                // --- Step 6 ---
                // --- Step action ---
                // Go to site Test->Document Library
                // --- Expected results ---
                // Document Library page is opened and all information including synced to the Cloud by user1 file is displayed correctly

                DocumentLibraryPage documentLibraryPage2 = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                Assert.assertTrue(documentLibraryPage2.isBrowserTitle("Document Library"), "The Document Library is not opened");

                // --- Step 7 ---
                // --- Step action ---
                // Verify  file sync icon state for synced  by user1 in step3 file
                // --- Expected results ---
                // File sync icon has state 'synced directly'
                Assert.assertTrue(documentLibraryPage2.getFileDirectoryInfo(fileName1).isCloudSynced(), "The sync information is not visible for User2");
        }

        @Test(groups = "DataPrepHybrid", timeOut = 300000)
        public void dataPrep_AONE_15447() throws Exception
        {

                String uniqueName = "_Z7";
                String testName = getTestName();
                String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
                String[] userInfo1 = new String[] { opUser1 };
                String opUser2 = getUserNameForDomain(testName + "opUser2", testDomain);
                String[] userInfo2 = new String[] { opUser2 };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

                String folderName = getFolderName(testName);

                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo2);
                CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

                ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
                ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(drone);

                // Cloud user and site.
                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(hybridDrone);

                ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
                //Invite User2 to be a site member with Comsumer role
                ShareUserMembers.inviteUserToSiteWithRole(drone, opUser1, opUser2, opSiteName, UserRole.CONSUMER);
                // Login to User1, set up the cloud sync
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

                // add file into DOCLIB
                ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUserSitePage.createFolder(drone, folderName, "").render();

                ShareUser.logout(drone);
        }

        /**
         * AONE-15447:Folder sync icon. Synced directly
         */
        @Test(groups = "Hybrid", timeOut = 500000)
        public void AONE_15447() throws Exception
        {
                String uniqueName = "_Z7";
                String testName = getTestName();
                String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
                String opUser2 = getUserNameForDomain(testName + "opUser2", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);

                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

                String folderName = getFolderName(testName);

                ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

                // --- Step 1 ---
                // --- Step action ---
                // Set cursor to the folder and expand More+ menu         or
                // Set cursor to the folder and click on View Details option to open its details page        or
                //Click on folder name to open folder
                // --- Expected results ---
                // List of actions is appeared for folder and 'sync to cloud' option available              or
                // Details page for created folder is successfully opened and 'sync to cloud' option is available among folder actions         or
                // 'sync to cloud' button is enable on the top panel

                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(folderName).isSyncToCloudLinkPresent(), "The sync to cloud option is not available");

                // --- Step 2 ---
                // --- Step action ---
                // Choose 'Sync to Cloud' option from More+ menu or from list of folder actions on details page or click on 'Sync to Cloud' on top panel of opened folder
                // --- Expected results ---
                // Pop-up window to select target cloud location appears.

                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(folderName).selectSyncToCloud().render();
                Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + folderName + " to The Cloud"));

                // --- Step 3 ---
                // --- Step action ---
                // Choose target location in the Cloud (network->site->document library) and press OK button
                // --- Expected results ---
                // 'Sync created' notification appears. Folder sync icon appears

                destinationAndAssigneePage.selectNetwork(testDomain);
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents");
                destinationAndAssigneePage.clickSyncButton();
                Assert.assertTrue(documentLibraryPage.isSyncMessagePresent());

                // --- Step 4 ---
                // --- Step action ---
                // Verify folder sync icon state
                // --- Expected results ---
                // Folder sync icon has state 'synced directly'

                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(folderName).isCloudSynced(), "The synced directly icon is not displayed");

                ShareUser.logout(drone);

                // --- Step 5 ---
                // --- Step action ---
                // Log in to Alfresco Share (on-premise) as user2 (site consumer)
                // --- Expected results ---
                // User2 is logged in successfully

                ShareUser.login(drone, opUser2, DEFAULT_PASSWORD);
                DashBoardPage dashBoardPage = new DashBoardPage(drone);
                Assert.assertTrue(dashBoardPage.titlePresent(), "User2 did not logged in successfully");

                // --- Step 6 ---
                // --- Step action ---
                // Go to site Test->Document Library
                // --- Expected results ---
                // Document Library page is opened and all information including synced to the Cloud by user1 file is displayed correctly

                DocumentLibraryPage documentLibraryPage2 = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                Assert.assertTrue(documentLibraryPage2.isBrowserTitle("Document Library"), "The Document Library is not opened");

                // --- Step 7 ---
                // --- Step action ---
                // Verify  folder sync icon state for synced  by user1 in step3 file
                // --- Expected results ---
                // Folder sync icon has state 'synced directly'
                Assert.assertTrue(documentLibraryPage2.getFileDirectoryInfo(folderName).isCloudSynced(), "The sync information is not visible for User2");
        }

        @Test(groups = "DataPrepHybrid", timeOut = 300000)
        public void dataPrep_AONE_15448() throws Exception
        {

                String uniqueName = "_Z7";
                String testName = getTestName();
                String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
                String[] userInfo1 = new String[] { opUser1 };
                String opUser2 = getUserNameForDomain(testName + "opUser2", testDomain);
                String[] userInfo2 = new String[] { opUser2 };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

                String folderName = getFolderName(testName);
                String folderName1 = getFolderName(testName) + "sub_folder";

                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo2);
                CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

                ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
                ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(drone);

                // Cloud user and site.
                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(hybridDrone);

                ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
                //Invite User2 to be a site member with Comsumer role
                ShareUserMembers.inviteUserToSiteWithRole(drone, opUser1, opUser2, opSiteName, UserRole.CONSUMER);
                // Login to User1, set up the cloud sync
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

                // add file into DOCLIB
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUserSitePage.createFolder(drone, folderName, "").render();
                documentLibraryPage.getFileDirectoryInfo(folderName).clickOnTitle().render();
                ShareUserSitePage.createFolder(drone, folderName1, "").render();

                ShareUser.logout(drone);
        }

        /**
         * AONE-15448:Sub folder sync icon. Synced indirectly
         */
        @Test(groups = "Hybrid", timeOut = 500000)
        public void AONE_15448() throws Exception
        {
                String uniqueName = "_Z7";
                String testName = getTestName();
                String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
                String opUser2 = getUserNameForDomain(testName + "opUser2", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);

                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

                String folderName = getFolderName(testName);
                String folderName1 = getFolderName(testName) + "sub_folder";

                ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

                // --- Step 1 ---
                // --- Step action ---
                // Set cursor to the folder created in preconditions in step7 and expand More+ menu        or
                // Set cursor to the folder and click on View Details option to open its details page        or
                //Click on folder name to open folder
                // --- Expected results ---
                // List of actions is appeared for folder and 'sync to cloud' option available              or
                // Details page for created folder is successfully opened and 'sync to cloud' option is available among folder actions         or
                // 'sync to cloud' button is enable on the top panel

                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(folderName).isSyncToCloudLinkPresent(), "The sync to cloud option is not available");

                // --- Step 2 ---
                // --- Step action ---
                // Choose 'Sync to Cloud' option from More+ menu or from list of folder actions on details page or click on 'Sync to Cloud' on top panel of opened folder
                // --- Expected results ---
                // Pop-up window to select target cloud location appears.

                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(folderName).selectSyncToCloud().render();
                Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + folderName + " to The Cloud"));

                // --- Step 3 ---
                // --- Step action ---
                // Choose target location in the Cloud (network->site->document library) and press OK button
                // --- Expected results ---
                // 'Sync created' notification appears. Folder sync icon appears

                destinationAndAssigneePage.selectNetwork(testDomain);
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents");
                destinationAndAssigneePage.clickSyncButton();
                Assert.assertTrue(documentLibraryPage.isSyncMessagePresent());

                // --- Step 4 ---
                // --- Step action ---
                // Open synced folder and verify sync icon state for all sub folders in it
                // --- Expected results ---
                // Synced folder is opened successfully, sub folders displayed on the page correctly. Sub folders' sync icons have state 'synced indirectly'

                documentLibraryPage.getFileDirectoryInfo(folderName).clickOnTitle().render();
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(folderName1).isViewCloudSyncInfoDisplayed(),
                        "The synced indirectly icon is not displayed for subfolder");

                ShareUser.logout(drone);

                // --- Step 5 ---
                // --- Step action ---
                // Log in to Alfresco Share (on-premise) as user2 (site consumer)
                // --- Expected results ---
                // User2 is logged in successfully

                ShareUser.login(drone, opUser2, DEFAULT_PASSWORD);
                DashBoardPage dashBoardPage = new DashBoardPage(drone);
                Assert.assertTrue(dashBoardPage.titlePresent(), "User2 did not logged in successfully");

                // --- Step 6 ---
                // --- Step action ---
                // Go to site Test->Document Library->synced folder with sub folders
                // --- Expected results ---
                // Synced folder is opened, all information and created in it sub folders are displayed correctly

                DocumentLibraryPage documentLibraryPage2 = ShareUser.openSitesDocumentLibrary(drone, opSiteName).getFileDirectoryInfo(folderName).clickOnTitle()
                        .render();
                Assert.assertTrue(documentLibraryPage2.isFileVisible(folderName1), "The subfolder is not visible for User2");

                // --- Step 7 ---
                // --- Step action ---
                // Verify  sub folders' sync icons state
                // --- Expected results ---
                // Sub folders' sync icons have state 'synced indirectly'
                Assert.assertTrue(documentLibraryPage2.getFileDirectoryInfo(folderName1).isViewCloudSyncInfoDisplayed(),
                        "The sync information is not visible for User2");
        }

        @Test(groups = "DataPrepHybrid", timeOut = 300000)
        public void dataPrep_AONE_15449() throws Exception
        {

                String uniqueName = "_Z7";
                String testName = getTestName();
                String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
                String[] userInfo1 = new String[] { opUser1 };
                String opUser2 = getUserNameForDomain(testName + "opUser2", testDomain);
                String[] userInfo2 = new String[] { opUser2 };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

                String folderName = getFolderName(testName);
                String fileName = getTestName() + ".txt";
                String[] fileInfo = new String[] { fileName, folderName };

                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo2);
                CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

                ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
                ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(drone);

                // Cloud user and site.
                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(hybridDrone);

                ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
                //Invite User2 to be a site member with Comsumer role
                ShareUserMembers.inviteUserToSiteWithRole(drone, opUser1, opUser2, opSiteName, UserRole.CONSUMER);
                // Login to User1, set up the cloud sync
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

                // upload file in folder
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUserSitePage.createFolder(drone, folderName, "").render();
                ShareUser.uploadFileInFolder(drone, fileInfo);

                ShareUser.logout(drone);
        }

        /**
         * AONE-15449:File sync icon.Synced indirectly
         */
        @Test(groups = "Hybrid", timeOut = 500000)
        public void AONE_15449() throws Exception
        {
                String uniqueName = "_Z7";
                String testName = getTestName();
                String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
                String opUser2 = getUserNameForDomain(testName + "opUser2", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);

                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

                String folderName = getFolderName(testName);
                String fileName = getTestName() + ".txt";

                ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

                // --- Step 1 ---
                // --- Step action ---
                // Set cursor to the folder created in preconditions in step7 and expand More+ menu        or
                // Set cursor to the folder and click on View Details option to open its details page        or
                //Click on folder name to open folder
                // --- Expected results ---
                // List of actions is appeared for folder and 'sync to cloud' option available              or
                // Details page for created folder is successfully opened and 'sync to cloud' option is available among folder actions         or
                // 'sync to cloud' button is enable on the top panel

                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(folderName).isSyncToCloudLinkPresent(), "The sync to cloud option is not available");

                // --- Step 2 ---
                // --- Step action ---
                // Choose 'Sync to Cloud' option from More+ menu or from list of folder actions on details page or click on 'Sync to Cloud' on top panel of opened folder
                // --- Expected results ---
                // Pop-up window to select target cloud location appears.

                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(folderName).selectSyncToCloud().render();
                Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + folderName + " to The Cloud"));

                // --- Step 3 ---
                // --- Step action ---
                // Choose target location in the Cloud (network->site->document library) and press OK button
                // --- Expected results ---
                // 'Sync created' notification appears. Folder sync icon appears

                destinationAndAssigneePage.selectNetwork(testDomain);
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents");
                destinationAndAssigneePage.clickSyncButton();
                Assert.assertTrue(documentLibraryPage.isSyncMessagePresent());

                // --- Step 4 ---
                // --- Step action ---
                // Open synced folder and verify sync icon state for all files in it
                // --- Expected results ---
                // Synced folder is opened successfully, sub folders displayed on the page correctly. Sub folders' sync icons have state 'synced indirectly'

                documentLibraryPage.getFileDirectoryInfo(folderName).clickOnTitle().render();
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isViewCloudSyncInfoDisplayed(),
                        "The synced indirectly icon is not displayed for file");

                ShareUser.logout(drone);

                // --- Step 5 ---
                // --- Step action ---
                // Log in to Alfresco Share (on-premise) as user2 (site consumer)
                // --- Expected results ---
                // User2 is logged in successfully

                ShareUser.login(drone, opUser2, DEFAULT_PASSWORD);
                DashBoardPage dashBoardPage = new DashBoardPage(drone);
                Assert.assertTrue(dashBoardPage.titlePresent(), "User2 did not logged in successfully");

                // --- Step 6 ---
                // --- Step action ---
                // Go to site Test->Document Library->synced folder
                // --- Expected results ---
                // Synced folder is opened, all information and created in it sub folders are displayed correctly

                DocumentLibraryPage documentLibraryPage2 = ShareUser.openSitesDocumentLibrary(drone, opSiteName).getFileDirectoryInfo(folderName).clickOnTitle()
                        .render();
                Assert.assertTrue(documentLibraryPage2.isFileVisible(fileName), "The file is not visible for User2");

                // --- Step 7 ---
                // --- Step action ---
                // Verify  sub folders' sync icons state
                // --- Expected results ---
                // Sub folders' sync icons have state 'synced indirectly'
                Assert.assertTrue(documentLibraryPage2.getFileDirectoryInfo(fileName).isViewCloudSyncInfoDisplayed(),
                        "The sync information is not visible for User2");
        }

        @Test(groups = "DataPrepHybrid", timeOut = 300000)
        public void dataPrep_AONE_15450() throws Exception
        {

                String uniqueName = "_Z7";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

                String clFolderName = getFolderName(testName);
                String fileName = getTestName() + ".txt";
                String[] fileInfo = new String[] { fileName, DOCLIB };

                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo);
                CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(drone);

                // Cloud user and site.
                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUserSitePage.createFolder(hybridDrone, clFolderName, "").render();
                ShareUser.logout(hybridDrone);

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                // Login to User1, set up the cloud sync
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
                // upload file in folder
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUser.uploadFileInFolder(drone, fileInfo);

                //Sync is failed for created file
                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(fileName).selectSyncToCloud().render();
                Assert.assertTrue(destinationAndAssigneePage.isSiteDisplayed(cloudSiteName));
                destinationAndAssigneePage.selectSubmitButtonToSync().render();

                documentLibraryPage = documentLibraryPage.getFileDirectoryInfo(fileName).selectUnSyncAndRemoveContentFromCloud(false);

                documentLibraryPage.getSiteNav().selectSiteDocumentLibrary().render();

                DestinationAndAssigneePage destinationAndAssigneePage1 = documentLibraryPage.getFileDirectoryInfo(fileName).selectSyncToCloud().render();
                Assert.assertTrue(destinationAndAssigneePage1.isSiteDisplayed(cloudSiteName));
                destinationAndAssigneePage1.selectSubmitButtonToSync().render();

                //wait to send the sync request
                documentLibraryPage.render(maxWaitTimeCloudSync);
                documentLibraryPage.getSiteNav().selectSiteDocumentLibrary().render();

                ShareUser.logout(drone);
        }

        /**
         * AONE-15450:File sync failed icon
         */
        @Test(groups = "Hybrid", timeOut = 500000)
        public void AONE_15450() throws Exception
        {
                String uniqueName = "_Z7";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String fileName = getTestName() + ".txt";

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

                // --- Step 1 ---
                // --- Step action ---
                // Go to the Document Library  of the created in on-premise site (step4 in preconditions)
                // --- Expected results ---
                // Document Library page is opened and all information including file failed to sync is displayed correctly

                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                Assert.assertTrue(documentLibraryPage.isFileVisible(fileName), "The file failed to sync is not displayed");

                // --- Step 2 ---
                // --- Step action ---
                // Verify icon against file failed to sync
                // --- Expected results ---
                // Sync failed icon is displayed

                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isSyncFailedIconPresent(maxWaitTime),
                        "The sync failed icon is not displayed");
        }

        @Test(groups = "DataPrepHybrid", timeOut = 300000)
        public void dataPrep_AONE_15451() throws Exception
        {

                String uniqueName = "_Z7";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

                String clFolderName = getFolderName(testName);
                String folderName = getFolderName(testName);

                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo);
                CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(drone);

                // Cloud user and site.
                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUserSitePage.createFolder(hybridDrone, clFolderName, "").render();
                ShareUser.logout(hybridDrone);

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                // Login to User1, set up the cloud sync
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
                // upload file in folder
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUserSitePage.createFolder(drone, folderName, "").render();

                //Sync is failed for created file
                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(folderName).selectSyncToCloud().render();
                Assert.assertTrue(destinationAndAssigneePage.isSiteDisplayed(cloudSiteName));
                destinationAndAssigneePage.selectSubmitButtonToSync().render();

                documentLibraryPage = documentLibraryPage.getFileDirectoryInfo(folderName).selectUnSyncAndRemoveContentFromCloud(false);

                documentLibraryPage.getSiteNav().selectSiteDocumentLibrary().render();

                DestinationAndAssigneePage destinationAndAssigneePage1 = documentLibraryPage.getFileDirectoryInfo(folderName).selectSyncToCloud().render();
                Assert.assertTrue(destinationAndAssigneePage1.isSiteDisplayed(cloudSiteName));
                destinationAndAssigneePage1.selectSubmitButtonToSync().render();

                //wait to send the sync request
                documentLibraryPage.render(maxWaitTimeCloudSync);
                documentLibraryPage.getSiteNav().selectSiteDocumentLibrary().render();

                ShareUser.logout(drone);
        }

        /**
         * AONE-15451:Folder sync failed icon
         */
        @Test(groups = "Hybrid", timeOut = 500000)
        public void AONE_15451() throws Exception
        {
                String uniqueName = "_Z7";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String folderName = getFolderName(testName);

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

                // --- Step 1 ---
                // --- Step action ---
                // Go to the Document Library  of the created in on-premise site (step4 in preconditions)
                // --- Expected results ---
                // Document Library page is opened and all information including folder failed to sync is displayed correctly

                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                Assert.assertTrue(documentLibraryPage.isFileVisible(folderName), "The folder failed to sync is not displayed");

                // --- Step 2 ---
                // --- Step action ---
                // Verify icon against folder failed to sync
                // --- Expected results ---
                // Sync failed icon is displayed

                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(folderName).isSyncFailedIconPresent(maxWaitTime),
                        "The sync failed icon is not displayed");
        }

        @Test(groups = "DataPrepHybrid", timeOut = 300000)
        public void dataPrep_AONE_15452() throws Exception
        {

                String uniqueName = "_Z7";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

                String folderName = getFolderName(testName);
                String subFolderName = getFolderName(testName) + "_sub_folder";
                String fileName = getTestName() + ".txt";
                String[] fileInfo = new String[] { fileName, folderName };

                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo);
                CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(drone);

                // Cloud user and site.
                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(hybridDrone);

                // Login to User1, set up the cloud sync
                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

                // upload file in folder
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUserSitePage.createFolder(drone, folderName, "").render();
                ShareUser.uploadFileInFolder(drone, fileInfo);
                ShareUserSitePage.createFolder(drone, subFolderName, "").render();

                documentLibraryPage.getSiteNav().selectSiteDocumentLibrary().render();

                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(folderName).selectSyncToCloud().render();
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents");
                destinationAndAssigneePage.selectSubmitButtonToSync().render();

                ShareUser.logout(drone);
        }

        /**
         * AONE-15452:Unsync a child file/folder of parent synced folder. Sync owner
         */
        @Test(groups = "Hybrid", timeOut = 500000)
        public void AONE_15452() throws Exception
        {
                String uniqueName = "_Z7";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String folderName = getFolderName(testName);
                String subFolderName = getFolderName(testName) + "_sub_folder";
                String fileName = getTestName() + ".txt";

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

                // --- Step 1 ---
                // --- Step action ---
                // Go  to the Document Library of created in preconditions in Alfresco Share (On-premise) site as sync owner user
                // --- Expected results ---
                // Document Library page is opened and all information including synced in preconditions folder is displayed correctly on it

                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                Assert.assertTrue(documentLibraryPage.isFileVisible(folderName), "The synced folder is not displayed");

                // --- Step 2 ---
                // --- Step action ---
                // Open synced folder
                // --- Expected results ---
                // Synced folder is opened successfully, sub folders and files displayed on the page correctly.

                documentLibraryPage = documentLibraryPage.getFileDirectoryInfo(folderName).clickOnTitle().render();
                Assert.assertTrue(documentLibraryPage.isFileVisible(subFolderName), "The sub folder from synced folder is not visible");
                Assert.assertTrue(documentLibraryPage.isFileVisible(fileName), "The file from synced folder is not visible");

                // --- Step 3 ---
                // --- Step action ---
                // Click sync icon against the file or sub folder in opened synced folder
                // --- Expected results ---
                // Pop up dialogue with sync info appears. Unsync button is absent

                SyncInfoPage syncInfoPage = documentLibraryPage.getFileDirectoryInfo(subFolderName).clickOnViewCloudSyncInfo().render();
                Assert.assertFalse(syncInfoPage.isUnsyncButtonPresent(), "The Unsync button is visible for an indirect synced file");

                // --- Step 4 ---
                // --- Step action ---
                // Verify Unsync option is available from More+ menu or from Document/Folder actions on Details page
                // --- Expected results ---
                // Unsync option is absent

                documentLibraryPage = syncInfoPage.clickOnCloseButton().render();
                Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(subFolderName).isUnSyncFromCloudLinkPresent(),
                        "The Unsync option is available for subfolder synced indirectly");
        }

        @Test(groups = "DataPrepHybrid", timeOut = 300000)
        public void dataPrep_AONE_15453() throws Exception
        {

                String uniqueName = "_Z7";
                String testName = getTestName();
                String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
                String[] userInfo1 = new String[] { opUser1 };
                String opUser2 = getUserNameForDomain(testName + "opUser2", testDomain);
                String[] userInfo2 = new String[] { opUser2 };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

                String folderName = getFolderName(testName);
                String subFolderName = getFolderName(testName) + "_sub_folder";
                String fileName = getTestName() + ".txt";
                String[] fileInfo = new String[] { fileName, folderName };

                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo2);
                CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

                ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
                ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(drone);

                // Cloud user and site.
                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(hybridDrone);

                // Login to User1, set up the cloud sync
                ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
                // Invite User2 to site with Manager role
                ShareUserMembers.inviteUserToSiteWithRole(drone, opUser1, opUser2, opSiteName, UserRole.MANAGER);

                // upload file in folder
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUserSitePage.createFolder(drone, folderName, "").render();
                ShareUser.uploadFileInFolder(drone, fileInfo);
                ShareUserSitePage.createFolder(drone, subFolderName, "").render();

                documentLibraryPage.getSiteNav().selectSiteDocumentLibrary().render();

                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(folderName).selectSyncToCloud().render();
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents");
                destinationAndAssigneePage.selectSubmitButtonToSync().render();

                ShareUser.logout(drone);
        }

        /**
         * AONE-15453:Unsync a child file/folder of parent synced folder. Site manager
         */
        @Test(groups = "Hybrid", timeOut = 500000)
        public void AONE_15453() throws Exception
        {
                String uniqueName = "_Z7";
                String testName = getTestName();
                String opUser2 = getUserNameForDomain(testName + "opUser2", testDomain);
                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String folderName = getFolderName(testName);
                String subFolderName = getFolderName(testName) + "_sub_folder";
                String fileName = getTestName() + ".txt";

                // --- Step 1 ---
                // --- Step action ---
                // Log in to Alfresco Share (on-premise) as user2 (site manager)
                // --- Expected results ---
                // User2 is logged in successfully

                ShareUser.login(drone, opUser2, DEFAULT_PASSWORD);
                DashBoardPage dashBoardPage = new DashBoardPage(drone);
                Assert.assertTrue(dashBoardPage.titlePresent(), "User2 did not logged in successfully");

                // --- Step 2 ---
                // --- Step action ---
                // Go to site Test->Document Library->synced in preconditions folder.
                // --- Expected results ---
                // Document Library page is opened and all information including files and sub folders are displayed correctly

                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                documentLibraryPage = documentLibraryPage.getFileDirectoryInfo(folderName).clickOnTitle().render();

                Assert.assertTrue(documentLibraryPage.isFileVisible(subFolderName), "The sub folder from synced folder is not visible");
                Assert.assertTrue(documentLibraryPage.isFileVisible(fileName), "The file from synced folder is not visible");

                // --- Step 3 ---
                // --- Step action ---
                // Click sync icon against the file or sub folder in opened synced folder
                // --- Expected results ---
                // Pop up dialogue with sync info appears. Unsync button is absent

                SyncInfoPage syncInfoPage = documentLibraryPage.getFileDirectoryInfo(subFolderName).clickOnViewCloudSyncInfo().render();
                Assert.assertFalse(syncInfoPage.isUnsyncButtonPresent(), "The Unsync button is visible for an indirect synced file");

                // --- Step 4 ---
                // --- Step action ---
                // Verify Unsync option is available from More+ menu or from Document/Folder actions on Details page
                // --- Expected results ---
                // Unsync option is absent

                documentLibraryPage = syncInfoPage.clickOnCloseButton().render();
                Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(subFolderName).isUnSyncFromCloudLinkPresent(),
                        "The Unsync option is available for subfolder synced indirectly");
        }

        @Test(groups = "DataPrepHybrid", timeOut = 300000)
        public void dataPrep_AONE_15454() throws Exception
        {

                String uniqueName = "_Z7";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

                String folderName = getFolderName(testName);
                String folderName2 = getFolderName(testName) + "2";
                String fileName = getTestName() + ".txt";
                String[] fileInfo = new String[] { fileName, folderName };

                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo);
                CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(drone);

                // Cloud user and site.
                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(hybridDrone);

                // Login to User1, set up the cloud sync
                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

                // upload file in folder
               ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUserSitePage.createFolder(drone, folderName, "").render();
                ShareUser.uploadFileInFolder(drone, fileInfo);
                ShareUserSitePage.createFolder(drone, folderName2, "").render();

                ShareUser.logout(drone);
        }

        /**
         * AONE-15454:Lock multiple files whilst syncing
         */
        @Test(groups = "Hybrid", timeOut = 500000)
        public void AONE_15454() throws Exception
        {
                String uniqueName = "_Z7";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;
                String folderName = getFolderName(testName);
                String folderName2 = getFolderName(testName) + "2";
                String fileName = getTestName() + ".txt";

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

                // --- Step 1 ---
                // --- Step action ---
                // Go  to the Document Library of created in preconditions in Alfresco Share (On-premise) site;
                // --- Expected results ---
                // Document Library page is opened and all information including synced in preconditions folder is displayed correctly on it

                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                Assert.assertTrue(documentLibraryPage.isFileVisible(folderName), "The expected folder is not displayed");

                // --- Step 2 ---
                // --- Step action ---
                // Choose All option from Select menu or manually check checkboxes for files you want to be synced;
                // --- Expected results ---
                // Necessary files are checked;

                documentLibraryPage.getFileDirectoryInfo(folderName).clickOnTitle().render();
                documentLibraryPage.getNavigation().selectAll().render();
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCheckboxSelected(), "The folder is not selected");
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(folderName2).isCheckboxSelected(), "The sub folder selected");

                // --- Step 3 ---
                // --- Step action ---
                // Choose 'Sync on Cloud' option from Selected items... menu;
                // --- Expected results ---
                // Pop-up window to select target location in the cloud appears;

                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getNavigation().selectSyncToCloudFromNav().render();
                Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + folderName + " to The Cloud"));

                // --- Step 4 ---
                // --- Step action ---
                // Choose target location in the Cloud (network->site->document library) and press OK button;
                // --- Expected results ---
                // Selected files syncing;

                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents");

                // --- Step 5 ---
                // --- Step action ---
                // Whilst syncing select Lock 'on-premise' copy checkbox
                // --- Expected results ---
                // Confirm locked message for all files are displayed;

                destinationAndAssigneePage.selectLockOnPremCopy();
                destinationAndAssigneePage.selectSubmitButtonToSync().render();

                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isLocked(), "The Locked icon is not displayed for fileName");
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(folderName2).isLocked(), "The Locked icon is not displayed for folderName2");
        }

        @Test(groups = "DataPrepHybrid", timeOut = 300000)
        public void dataPrep_AONE_15455() throws Exception
        {

                String uniqueName = "_Z7b";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

                String fileName = getTestName() + ".txt";
                String[] fileInfo = new String[] { fileName, DOCLIB };

                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo);
                CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(drone);

                // Cloud user and site.
                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(hybridDrone);

                // Login to User1, set up the cloud sync
                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

                // upload file in folder and synced to cloud with locked on-premise option selected
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUser.uploadFileInFolder(drone, fileInfo);
                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(fileName).selectSyncToCloud().render();
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectLockOnPremCopy();
                destinationAndAssigneePage.selectSubmitButtonToSync().render();

                ShareUser.logout(drone);
        }

        /**
         * AONE-15455:Sync a file to 'on-premise' version after change the property
         */
        @Test(groups = "Hybrid", timeOut = 500000)
        public void AONE_15455() throws Exception
        {
                String uniqueName = "_Z7b";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;
                String fileName = getTestName() + ".txt";

                // --- Step 1 ---
                // --- Step action ---
                // Go to Cloud location where file was synced and locked in preconditions;
                // --- Expected results ---
                // Cloud location (e.g. site->document library-> folder) is opened, synced and locked item with sync info icon against it is displayed on the page;

                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
                Assert.assertTrue(documentLibraryPage.isFileVisible(fileName), "The synced file is not displayed");
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "The file is not synced to cloud");

                // --- Step 2 ---
                // --- Step action ---
                //  Change the property of the synced file (e.g. description) clicking 'Edit Properties' option on the right menu;
                // --- Expected results ---
                // The property was changed and file will synced back to on-premise version. And the version is still "1.0".

                EditDocumentPropertiesPage editDocumentPropertiesPage = documentLibraryPage.getFileDirectoryInfo(fileName).selectEditProperties().render();
                editDocumentPropertiesPage.setDescription("Edited_Description");
                documentLibraryPage = editDocumentPropertiesPage.selectSave().render();

                Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(fileName).getDescription(), "Edited_Description",
                        "The description was not edited");
                Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(fileName).getVersionInfo(), "1.0", "The version changed after Edit on Cloud");

                ShareUser.logout(hybridDrone);

                // --- Step 3 ---
                // --- Step action ---
                // Login as User (OP), verify Document
                // --- Expected results ---
                // Document description is updated and version is changed to "1.1" from DocumentDetailsPage

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                DocumentLibraryPage opDocumentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                Assert.assertTrue(opDocumentLibraryPage.isFileVisible(fileName), "The synced file is not displayed");
                waitForSync(drone, fileName, opSiteName);
                DocumentDetailsPage documentDetailsPage = opDocumentLibraryPage.getFileDirectoryInfo(fileName).clickOnTitle().render();
                Assert.assertEquals(documentDetailsPage.getCurrentVersionDetails().getVersionNumber(), "1.2",

                        "The version was not changed after Edit on Cloud");

                ShareUser.logout(drone);
        }

        @Test(groups = "DataPrepHybrid", timeOut = 300000)
        public void dataPrep_AONE_15456() throws Exception
        {

                String uniqueName = "_Z7a";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

                String fileName = getTestName() + ".txt";
                String[] fileInfo = new String[] { fileName, DOCLIB };

                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo);
                CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(drone);

                // Cloud user and site.
                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(hybridDrone);

                // Login to User1, set up the cloud sync
                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

                // upload file in folder and synced to cloud with locked on-premise option selected
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUser.uploadFileInFolder(drone, fileInfo);
                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(fileName).selectSyncToCloud().render();
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectLockOnPremCopy();
                destinationAndAssigneePage.selectSubmitButtonToSync().render();

                ShareUser.logout(drone);
        }

        /**
         * AONE-15456:Sync a file to 'on-premise' version after change the content
         */
        @Test(groups = "Hybrid", timeOut = 500000)
        public void AONE_15456() throws Exception
        {
                String uniqueName = "_Z7a";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;
                String fileName = getTestName() + ".txt";

                // --- Step 1 ---
                // --- Step action ---
                // Go to Cloud location where file was synced and locked in preconditions;
                // --- Expected results ---
                // Cloud location (e.g. site->document library-> folder) is opened, synced item with sync info icon against it is displayed on the page;

                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
                Assert.assertTrue(documentLibraryPage.isFileVisible(fileName), "The synced file is not displayed");
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "The file is not synced to cloud");

                // --- Step 2 ---
                // --- Step action ---
                // Change the content of the synced file clicking 'Edit offline/online' option on expand More+ menu;
                // --- Expected results ---
                // The content was changed and file will synced back to on-premise version. The version is changed to "1.1"

                DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(fileName).render();
                ShareUser.editTextDocument(hybridDrone, fileName, "Description", "Content");

                EditTextDocumentPage inlineEditPage = documentDetailsPage.selectInlineEdit().render();

                ContentDetails contentDetails = inlineEditPage.getDetails();
                Assert.assertEquals(contentDetails.getName(), fileName);
                Assert.assertEquals(contentDetails.getDescription(), "Description");
                Assert.assertEquals(contentDetails.getContent(), "Content", "The content was not edited");

                inlineEditPage.selectCancel().render();
                documentLibraryPage.getSiteNav().selectSiteDocumentLibrary().render();
                Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(fileName).getVersionInfo(), "1.1",
                        "The version was not changed after Edit on Cloud");
                waitForSync(hybridDrone, fileName, cloudSiteName);

                ShareUser.logout(hybridDrone);

                // --- Step 3 ---
                // --- Step action ---
                //  Login as User (OP), verify version  from DocumentDetailsPage;
                // --- Expected results ---
                // Version is updated to "1.1"

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                DocumentLibraryPage opDocumentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                waitForSync(drone, fileName, opSiteName);
                Assert.assertTrue(opDocumentLibraryPage.isFileVisible(fileName), "The synced file is not displayed");
                documentDetailsPage = opDocumentLibraryPage.getFileDirectoryInfo(fileName).clickOnTitle().render(7000);

                int maxRefreshes = 10;
                while (documentDetailsPage.getCurrentVersionDetails().getVersionNumber().contains("1.0") && maxRefreshes > 0)
                {

                        documentLibraryPage = documentDetailsPage.getSiteNav().selectSiteDocumentLibrary().render();
                        documentDetailsPage = documentLibraryPage.getFileDirectoryInfo(fileName).clickOnTitle().render();
                        maxRefreshes = maxRefreshes - 1;

                }


                Assert.assertEquals(documentDetailsPage.getCurrentVersionDetails().getVersionNumber(), "1.1",
                        "The version was not changed after Edit on Cloud");

                ShareUser.logout(drone);
        }

        @Test(groups = "DataPrepHybrid", timeOut = 300000)
        public void dataPrep_AONE_15457() throws Exception
        {

                String uniqueName = "_Z7";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

                String fileName = getTestName() + ".txt";
                String[] fileInfo = new String[] { fileName, DOCLIB };

                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo);
                CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(drone);

                // Cloud user and site.
                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(hybridDrone);

                // Login to User1, set up the cloud sync
                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

                // upload file in folder and synced to cloud with locked on-premise option selected
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUser.uploadFileInFolder(drone, fileInfo);
                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(fileName).selectSyncToCloud().render();
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectLockOnPremCopy();
                destinationAndAssigneePage.selectSubmitButtonToSync().render();

                ShareUser.logout(drone);
        }

        /**
         * AONE-15457:Editing of the locked file
         */
        @Test(groups = "Hybrid", timeOut = 500000)
        public void AONE_15457() throws Exception
        {
                String uniqueName = "_Z7";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String fileName = getTestName() + ".txt";

                // --- Step 1 ---
                // --- Step action ---
                // Go  to the Document Library of created in preconditions in Alfresco Share (On-premise) site;
                // --- Expected results ---
                // Document Library page is opened and all information is displayed correctly on it;

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                DocumentLibraryPage opDocumentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                Assert.assertTrue(opDocumentLibraryPage.isFileVisible(fileName), "The synced file is not displayed");
                Assert.assertTrue(opDocumentLibraryPage.getFileDirectoryInfo(fileName).isLocked(), "The synced file is not locked");

                // --- Step 2 ---
                // --- Step action ---
                // Attempt to edit the locked file;
                // --- Expected results ---
                // The locked file is not editable.

                Assert.assertFalse(opDocumentLibraryPage.getFileDirectoryInfo(fileName).isInlineEditLinkPresent(), "The locked file can be editable");
                ShareUser.logout(drone);
        }

        @Test(groups = "DataPrepHybrid", timeOut = 300000)
        public void dataPrep_AONE_15458() throws Exception
        {

                String uniqueName = "_Z7";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

                String fileName = getTestName() + ".txt";
                String[] fileInfo = new String[] { fileName, DOCLIB };

                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo);
                CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(drone);

                // Cloud user and site.
                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(hybridDrone);

                // Login to User1, set up the cloud sync
                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

                // upload file in folder and synced to cloud with locked on-premise option selected
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUser.uploadFileInFolder(drone, fileInfo);
                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(fileName).selectSyncToCloud().render();
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectLockOnPremCopy();
                destinationAndAssigneePage.selectSubmitButtonToSync().render();

                ShareUser.logout(drone);
        }

        /**
         * AONE-15458:Removing the lock by unsyncing the file
         */
        @Test(groups = "Hybrid", timeOut = 500000)
        public void AONE_15458() throws Exception
        {
                String uniqueName = "_Z7";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;
                String fileName = getTestName() + ".txt";

                // --- Step 1 ---
                // --- Step action ---
                // Go  to the Document Library of created in preconditions in Alfresco Share (On-premise) site;
                // --- Expected results ---
                // Document Library page is opened and all information is displayed correctly on it;

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                DocumentLibraryPage opDocumentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                Assert.assertTrue(opDocumentLibraryPage.isFileVisible(fileName), "The synced file is not displayed");
                Assert.assertTrue(opDocumentLibraryPage.getFileDirectoryInfo(fileName).isLocked(), "The synced file is not locked");

                // --- Step 2 ---
                // --- Step action ---
                // Attempt remove the lock of the file by More+ menu or special button;
                // --- Expected results ---
                // Removing the lock is impossible;

                // --- Step 3 ---
                // --- Step action ---
                // Unsync the file by clicking More+ menu and choosing option 'Unsync from Cloud' or clicking on Cloud icon;
                // --- Expected results ---
                // FIle was unsynced and unlocked. Only unsyncing the file may be remove the lock.

                opDocumentLibraryPage = opDocumentLibraryPage.getFileDirectoryInfo(fileName).selectUnSyncAndRemoveContentFromCloud(true);
                Assert.assertFalse(opDocumentLibraryPage.getFileDirectoryInfo(fileName).isLocked(), "The file remained locked after Unsync");

                ShareUser.logout(drone);
        }

        @Test(groups = "DataPrepHybrid", timeOut = 300000)
        public void dataPrep_AONE_15459() throws Exception
        {

                String uniqueName = "_Z7";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

                String fileName = getTestName() + ".txt";
                String[] fileInfo = new String[] { fileName, DOCLIB };

                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo);
                CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(drone);

                // Cloud user and site.
                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(hybridDrone);

                // Login to User1, set up the cloud sync
                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

                // upload file in folder and synced to cloud with locked on-premise option selected
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUser.uploadFileInFolder(drone, fileInfo);
                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(fileName).selectSyncToCloud().render();
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectLockOnPremCopy();
                destinationAndAssigneePage.selectSubmitButtonToSync().render();

                ShareUser.logout(drone);
        }

        /**
         * AONE-15459:Checking the history of the synced file
         */
        @Test(groups = "Hybrid", timeOut = 500000)
        public void AONE_15459() throws Exception
        {
                String uniqueName = "_Z7";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String fileName = getTestName() + ".txt";

                // --- Step 1 ---
                // --- Step action ---
                // Go  to the Document Library of created in preconditions in Alfresco Share (On-premise) site;
                // --- Expected results ---
                // Document Library page is opened and all information is displayed correctly on it;

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                DocumentLibraryPage opDocumentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                Assert.assertTrue(opDocumentLibraryPage.isFileVisible(fileName), "The synced file is not displayed");
                Assert.assertTrue(opDocumentLibraryPage.getFileDirectoryInfo(fileName).isLocked(), "The synced file is not locked");

                // --- Step 2 ---
                // --- Step action ---
                // Click on the synced file to open its details page;
                // --- Expected results ---
                // Details page for synced document is successfully opened and information is displayed correctly;

                DocumentDetailsPage documentDetailsPage = opDocumentLibraryPage.getFileDirectoryInfo(fileName).clickOnTitle().render();
                Assert.assertTrue(documentDetailsPage.isBrowserTitle("Document Details"), "The details page did not displayed correctly");

                // --- Step 3 ---
                // --- Step action ---
                // Check the history in sections Sync and Version Histiry;
                // --- Expected results ---
                // History should show, sync target location, synced from location, last synced time and date, synced by, last failed sync if any, and version history.

                SyncInfoPage syncInfoPage = documentDetailsPage.getSyncInfoPage();

                Assert.assertTrue(syncInfoPage.isSyncLocationPresent(), "Sync location was not found");
                Assert.assertNotNull(syncInfoPage.getSyncPeriodDetails(), "The synced date time is not displayed");
                Assert.assertTrue(documentDetailsPage.isVersionHistoryPanelPresent(), "The Version History Panel is not present");

                ShareUser.logout(drone);
        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15460() throws Exception
        {

                String uniqueName = "_Z7a";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFileName = getFolderName(testName) + "OP";
                String[] fileInfo = new String[] { opFileName, DOCLIB };
                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
                CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(drone);

                // Cloud user logins and create site.
                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(hybridDrone);

                CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1001");

                // Login to User1, set up the cloud sync
                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD).render();

                // create a file
                DocumentLibraryPage docLib = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUser.uploadFileInFolder(drone, fileInfo).render();
                ShareUser.logout(drone);
        }

        /*
        * AONE-15460 Creating a new folder in a cloud target selection window
        */
        @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
        public void AONE_15460() throws Exception
        {

                String uniqueName = "_Z7a";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);

                String opFileName = getFolderName(testName) + "OP";
                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

                String newFolderName = getFolderName(testName) + "-NAME-";
                String newFolderTitle = getFolderName(testName) + "-TITLE-";
                String newFolderDescription = getFolderName(testName) + "-DESC-";

                // ---- Step 1 ----
                // ---- Step action ----
                // Set cursor to the document and expand More+ menu;
                // ---- Expected results ----
                // List of actions is appeared for document and 'sync to cloud' option available;

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                FileDirectoryInfo fileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(opFileName);
                Assert.assertTrue(fileDirectoryInfo.isSyncToCloudLinkPresent(),
                        "List of actions didn't appeared for document and 'sync to cloud' option available;");

                // ---- Step 2 ----
                // ---- Step action ----
                // Choose 'Sync to Cloud' option;
                // ---- Expected results ----
                // Pop-up window to select target cloud location appears;

                DestinationAndAssigneePage destinationAndAssigneePage = fileDirectoryInfo.selectSyncToCloud().render();
                Assert.assertNotNull(destinationAndAssigneePage, "Pop-up window to select target cloud location didn't appear;");

                // ---- Step 3 ----
                // ---- Step action ----
                // Verify default network--site--document library is selected/displayed in the window;
                // ---- Expected results ----
                // Information is displayed correctly;

                Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(testDomain), "Network is not displayed.");
                Assert.assertTrue(destinationAndAssigneePage.isSiteDisplayed(cloudSiteName), "Site is not displayed.");
                Assert.assertTrue(destinationAndAssigneePage.isFolderDisplayed("Documents"), "Documents folder is not displayed.");

                // ---- Step 4 ----
                // ---- Step action ----
                // Click on the button '+' in section with folders;
                // ---- Expected results ----
                // Pop-up window New Folder appears with labels Name and Description;

                destinationAndAssigneePage.selectSite(cloudSiteName);
                CreateNewFolderInCloudPage newFolderPopup = destinationAndAssigneePage.selectCreateNewFolder().render();
                Assert.assertTrue(newFolderPopup.isNameLabelDisplayed(), "Name label is not displayed.");
                Assert.assertTrue(newFolderPopup.isDescriptionLabelDisplayed(), "Description label is not displayed.");

                // ---- Step 5 ----
                // ---- Step action ----
                // Fill the necessary fields and click 'OK';
                // ---- Expected results ----
                // The folder was successfully created in Cloud.

                newFolderPopup.createNewFolder(newFolderName, newFolderTitle, newFolderDescription).render();
                destinationAndAssigneePage = new DestinationAndAssigneePage(drone).render();
                Assert.assertTrue(destinationAndAssigneePage.isFolderDisplayed(newFolderName), "Folder is not displayed.");

        }

}
