package org.alfresco.share.cloudsync;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.CreateNewFolderInCloudPage;
import org.alfresco.po.share.site.document.*;
import org.alfresco.po.share.user.CloudSignInPage;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class HybridSyncPositiveTests extends AbstractWorkflow
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
        public void dataPrep_15412() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String opSiteName = getSiteName(testName) + "-OP";

                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(drone);
        }

        /**
         * AONE-15412:Sync to Cloud option. More+ menu
         */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15412() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String opSiteName = getSiteName(testName) + "-OP";
                String fileName = getFileName(testName) + ".txt";
                String[] fileInfo = new String[] { fileName, DOCLIB };

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

                // Document Library page of the created Site is opened
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

                // Any file is created/uploaded into the Document Library
                ShareUser.uploadFileInFolder(drone, fileInfo);

                // --- Step 1 ---
                // --- Step action ---
                // Set cursor to the document and expand More+ menu
                // --- Expected results ---
                // List of actions is appeared for document and 'sync to cloud' option available
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isSyncToCloudLinkPresent());
                ShareUser.logout(drone);
        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_15413() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
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
                ShareUser.logout(drone);
        }

        /**
         * AONE-15413:Sync File(s) to the Cloud. Single network and single site.
         */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15413() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";
                String fileName = getFileName(testName) + ".txt";
                String[] fileInfo = new String[] { fileName, DOCLIB };

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

                // Document Library page of the created Site is opened
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

                // Any file is created/uploaded into the Document Library
                ShareUser.uploadFileInFolder(drone, fileInfo);

                // --- Step 1 ---
                // --- Step action ---
                // Set cursor to the document and expand More+ menu
                // --- Expected results ---
                // List of actions is appeared for document and 'sync to cloud' option available
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isSyncToCloudLinkPresent());

                // --- Step 2 ---
                // --- Step action ---
                // Choose 'Sync to Cloud' option
                // --- Expected results ---
                // Pop-up window to select target cloud location appears
                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(fileName).selectSyncToCloud().render();
                Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + fileName + " to The Cloud"));

                // --- Step 3 ---
                // --- Step action ---
                // Verify default network-->site-->document library is selected/displayed in the window
                // --- Expected results ---
                // Information is displayed correctly
                Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(testDomain));
                Assert.assertTrue(destinationAndAssigneePage.isSiteDisplayed(cloudSiteName));
                Assert.assertTrue(destinationAndAssigneePage.isFolderDisplayed("Documents"));

                ShareUser.logout(drone);

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_15414() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName1 = getSiteName(testName) + "-CL1";
                String cloudSiteName2 = getSiteName(testName) + "-CL2";
                String cloudSiteName3 = getSiteName(testName) + "-CL3";

                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
                CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(drone);

                // Cloud user logins and create site.
                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                ShareUser.createSite(hybridDrone, cloudSiteName1, SITE_VISIBILITY_PUBLIC);
                ShareUser.createSite(hybridDrone, cloudSiteName2, SITE_VISIBILITY_PUBLIC);
                ShareUser.createSite(hybridDrone, cloudSiteName3, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(hybridDrone);

                // Login to User1, set up the cloud sync
                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
                ShareUser.logout(drone);

        }

        /**
         * AONE-15414:Sync File(s) to the Cloud. Single network and two to three sites
         */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15414() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName1 = getSiteName(testName) + "-CL1";
                String cloudSiteName2 = getSiteName(testName) + "-CL2";
                String cloudSiteName3 = getSiteName(testName) + "-CL3";
                String fileName = getFileName(testName) + ".txt";
                String[] fileInfo = new String[] { fileName, DOCLIB };

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

                // Document Library page of the created Site is opened
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

                // Any file is created/uploaded into the Document Library
                ShareUser.uploadFileInFolder(drone, fileInfo);

                // --- Step 1 ---
                // --- Step action ---
                // Set cursor to the document and expand More+ menu
                // --- Expected results ---
                // List of actions is appeared for document and 'sync to cloud' option available
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isSyncToCloudLinkPresent());

                // --- Step 2 ---
                // --- Step action ---
                // Choose 'Sync to Cloud' option
                // --- Expected results ---
                // Pop-up window to select target cloud location appears
                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(fileName).selectSyncToCloud().render();
                Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + fileName + " to The Cloud"));

                // --- Step 3 ---
                // --- Step action ---
                // Verify default network-->sites are displayed in the window
                // --- Expected results ---
                // Information is correct and all available sites are displayed and can be chosen
                Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(testDomain));
                Assert.assertTrue(destinationAndAssigneePage.isSiteDisplayed(cloudSiteName1));
                Assert.assertTrue(destinationAndAssigneePage.isSiteDisplayed(cloudSiteName2));
                Assert.assertTrue(destinationAndAssigneePage.isSiteDisplayed(cloudSiteName3));

                ShareUser.logout(drone);
        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_15415() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser1 = getUserNameForDomain(testName + "cloudUser1", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser1 };
                String cloudUser2 = getUserNameForDomain(testName + "cloudUser2", testDomain);
                String[] cloudUserInfo2 = new String[] { cloudUser2 };

                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";

                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
                CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
                CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo2);

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(drone);

                // Cloud user logins and create site.
                ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
                ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
                CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, cloudUser1, cloudUser2, getSiteShortname(cloudSiteName), "SiteCollaborator", "");
                ShareUser.logout(hybridDrone);

                // Login to User1, set up the cloud sync
                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser2, DEFAULT_PASSWORD);
                ShareUser.logout(drone);
        }

        /**
         * AONE-15415:Sync File(s) to the Cloud. More than one network
         */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15415() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";
                String fileName = getFileName(testName) + ".txt";
                String[] fileInfo = new String[] { fileName, DOCLIB };

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

                // Document Library page of the created Site is opened
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

                // Any file is created/uploaded into the Document Library
                ShareUser.uploadFileInFolder(drone, fileInfo);

                // --- Step 1 ---
                // --- Step action ---
                // Set cursor to the document and expand More+ menu
                // --- Expected results ---
                // List of actions is appeared for document and 'sync to cloud' option available
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isSyncToCloudLinkPresent());

                // --- Step 2 ---
                // --- Step action ---
                // Choose 'Sync to Cloud' option
                // --- Expected results ---
                // Pop-up window to select target cloud location appears
                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(fileName).selectSyncToCloud().render();
                Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + fileName + " to The Cloud"));

                // --- Step 3 ---
                // --- Step action ---
                // Verify information displayed in window
                // --- Expected results ---
                // All available networks are displayed correctly and can be chosen
                Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(testDomain));
                Assert.assertTrue(destinationAndAssigneePage.isSiteDisplayed(cloudSiteName));

                // --- Step 4 ---
                // --- Step action ---
                // Choose any network -->available site-->document library and press OK button
                // --- Expected results ---
                // Notification about file is synced successfully appears
                destinationAndAssigneePage.selectNetwork(testDomain);
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents");
                destinationAndAssigneePage.clickSyncButton();
                Assert.assertTrue(documentLibraryPage.isSyncMessagePresent());
                ShareUser.logout(drone);

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_15416() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String opSiteName = getSiteName(testName) + "-OP";

                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(drone);
        }

        /**
         * AONE-15416:Sync to Cloud option. Details page
         */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15416() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String opSiteName = getSiteName(testName) + "-OP";
                String fileName = getFileName(testName) + ".txt";
                String[] fileInfo = new String[] { fileName, DOCLIB };

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

                // Document Library page of the created Site is opened
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

                // Any file is created/uploaded into the Document Library
                ShareUser.uploadFileInFolder(drone, fileInfo);

                // --- Step 1 ---
                // --- Step action ---
                // Click on created/uploaded file to open its details page
                // --- Expected results ---
                // Details page for created/uploaded document is successfully opened and information is displayed correct
                DocumentDetailsPage documentDetailsPage = documentLibraryPage.getFileDirectoryInfo(fileName).clickOnTitle().render();
                Assert.assertTrue(documentDetailsPage.isDocumentDetailsPage());

                // --- Step 2 ---
                // --- Step action ---
                // Verify list of Document Actions
                // --- Expected results ---
                // 'Sync to Cloud' option is available
                Assert.assertTrue(documentDetailsPage.isSyncToCloudOptionDisplayed());
                ShareUser.logout(drone);
        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_15419() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
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
                ShareUser.logout(drone);
        }

        /**
         * AONE-15419:Sync multiple files to accessible cloud account
         */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15419() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";
                String fileName1 = getTestName() + "1" + ".txt";
                String[] fileInfo1 = new String[] { fileName1, DOCLIB };
                String fileName2 = getTestName() + "2" + ".txt";
                String[] fileInfo2 = new String[] { fileName2, DOCLIB };

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

                // Document Library page of the created Site is opened
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

                // At least two files are created/uploaded into the Document Library
                ShareUser.uploadFileInFolder(drone, fileInfo1);
                ShareUser.uploadFileInFolder(drone, fileInfo2);

                // --- Step 1 ---
                // --- Step action ---
                // Choose All option from Select menu or manually check checkboxes for files you want to be synced
                // --- Expected results ---
                // Necessary files are checked
                DocumentLibraryNavigation documentLibraryNavigation = new DocumentLibraryNavigation(drone);
                documentLibraryNavigation.selectAll().render();

                Assert.assertEquals(documentLibraryPage.getFiles().size(), 2);
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCheckboxSelected());
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName2).isCheckboxSelected());

                // --- Step 2 ---
                // --- Step action ---
                // Choose 'Sync to Cloud' option
                // --- Expected results ---
                // Pop-up window to select target cloud location appears
                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryNavigation.selectSyncToCloud().render();
                Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync selected content to the Cloud"));

                // --- Step 3 ---
                // --- Step action ---
                // Choose target location in the Cloud (network->site->document library) and press OK button
                // --- Expected results ---
                // Notification about successful files' sync appears and all selected files are synced to the Cloud
                destinationAndAssigneePage.selectNetwork(testDomain);
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents");
                destinationAndAssigneePage.clickSyncButton();
                Assert.assertTrue(documentLibraryPage.isSyncMessagePresent());

                ShareUser.logout(drone);
        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_15421() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
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
                ShareUser.logout(drone);
        }

        /**
         * AONE-15421:Sync File(s) after Cloud account authorised
         */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15421() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String opSiteName = getSiteName(testName) + "-OP";
                String fileName = getTestName() + ".txt";
                String[] fileInfo = new String[] { fileName, DOCLIB };

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

                // Document Library page of the created Site is opened
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

                // Any file(s) is(are) created/uploaded into the Document Library
                ShareUser.uploadFileInFolder(drone, fileInfo);

                // --- Step 1 ---
                // --- Step action ---
                // Check one or more files in Document Library in Alfresco Share (On-premise)
                // --- Expected results ---
                // Necessary file or files are checked
                documentLibraryPage.getFileDirectoryInfo(fileName).selectCheckbox();
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCheckboxSelected());

                // --- Step 2 ---
                // --- Step action ---
                // Choose 'Sync to Cloud' option from Selected items.. menu
                // --- Expected results ---
                // Pop-up window to select target cloud location appears. There is no any Cloud login prompt.
                DocumentLibraryNavigation documentLibraryNavigation = new DocumentLibraryNavigation(drone);
                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryNavigation.selectSyncToCloud().render();
                Assert.assertFalse(documentLibraryPage.isSignUpDialogVisible());
                Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync selected content to the Cloud"));

                ShareUser.logout(drone);
        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_15422() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String opSiteName = getSiteName(testName) + "-OP";

                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(drone);
        }

        /**
         * AONE-15422:Sync File(s) without cloud account
         */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15422() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String opSiteName = getSiteName(testName) + "-OP";
                String fileName = testName + ".txt";
                String[] fileInfo = new String[] { fileName, DOCLIB };

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

                // Document Library page of the created Site is opened
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

                // Any file(s) is(are) created/uploaded into the Document Library
                ShareUser.uploadFileInFolder(drone, fileInfo);

                // --- Step 1 ---
                // --- Step action ---
                // Check one or more files in Document Library
                // --- Expected results ---
                // Necessary file or files are checked
                documentLibraryPage.getFileDirectoryInfo(fileName).selectCheckbox();
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCheckboxSelected());

                // --- Step 2 ---
                // --- Step action ---
                // Choose 'Sync to Cloud' option from Selected items.. menu
                // --- Expected results ---
                // Cloud login window prompted with option to create a new cloud account appears
                DocumentLibraryNavigation documentLibraryNavigation = new DocumentLibraryNavigation(drone);
                CloudSignInPage cloudSignInPage = documentLibraryNavigation.selectSyncToCloud().render();

                Assert.assertTrue(documentLibraryPage.isSignUpDialogVisible());
                Assert.assertTrue(cloudSignInPage.isSignUpLinkDisplayed());

                // --- Step 3 ---
                // --- Step action ---
                // Click the new account option
                // --- Expected results ---
                // Cloud create new account window opens
                cloudSignInPage.selectSignUpLink();

                // TODO - Failed with defect --- MNT-12815 - The signup page is not displayed when the 'Sign up for free' option is selected from Document Library ->
                // 'Sync to Cloud' pop-up
                // Uncomment after fix
                //Assert.assertTrue(isWindowWithURLOpened(drone, "Login"));
                ShareUser.logout(drone);

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_15423() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String opSiteName = getSiteName(testName) + "-OP";

                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(drone);
        }

        /**
         * AONE-15423:Sync File(s) and create a new cloud account
         */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15423() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String opSiteName = getSiteName(testName) + "-OP";
                String fileName = getTestName() + ".txt";
                String[] fileInfo = new String[] { fileName, DOCLIB };

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

                // Document Library page of the created Site is opened
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

                // Any file(s) is(are) created/uploaded into the Document Library
                ShareUser.uploadFileInFolder(drone, fileInfo);

                // --- Step 1 ---
                // --- Step action ---
                // Check one or more files in Document Library
                // --- Expected results ---
                // Necessary file or files are checked
                documentLibraryPage.getFileDirectoryInfo(fileName).selectCheckbox();
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCheckboxSelected());

                // --- Step 2 ---
                // --- Step action ---
                // Choose 'Sync to Cloud' option from Selected items.. menu
                // --- Expected results ---
                // Cloud login window prompted with option to create a new cloud account appears
                DocumentLibraryNavigation documentLibraryNavigation = new DocumentLibraryNavigation(drone);
                CloudSignInPage cloudSignInPage = documentLibraryNavigation.selectSyncToCloud().render();

                Assert.assertTrue(documentLibraryPage.isSignUpDialogVisible());
                Assert.assertTrue(cloudSignInPage.isSignUpLinkDisplayed());

                // --- Step 3 ---
                // --- Step action ---
                // Click the new account option 'Singn up for Free Account'
                // --- Expected results ---
                // New window where it's necessary to enter your e-mail for new account registration is opened
                cloudSignInPage.selectSignUpLink();

                // TODO - add defect in JIRA - MNT-12815
                // uncomment after fix
                // Assert.assertTrue(isWindowWithURLOpened(drone, "Login"));
                ShareUser.logout(drone);

                // --- Step 4 ---
                // --- Step action ---
                // Enter correct e-mail into appropriate field and click on sign up buttonsave it
                // --- Expected results ---
                // Information entered successfully.Message to confirm your registartion is sent to your e-mail

                // --- Step 5 ---
                // --- Step action ---
                // Go to your mail service and confirm registration from recieved message
                // --- Expected results ---
                // You are redirected to the page for fill in the form (username, password and so on)

                // --- Step 6 ---
                // --- Step action ---
                // Fill in all appropriate fields and click on OK button
                // --- Expected results ---
                // Registration is successfull, new account is created

                // --- Step 7 ---
                // --- Step action ---
                // Return to the page with file you are going to sync and enter login/password of the created account into appropriate fields of the login prompted
                // window
                // --- Expected results ---
                // Pop-up window to select Cloud target location appears

                // --- Step 8 ---
                // --- Step action ---
                // Choose available location and press OK button
                // --- Expected results ---
                // Notification about successful file(s) sync appears. File(s) are synced to the Cloud.
        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_15425() throws Exception
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

                // Any file is created/uploaded into the Document Library
                DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo).render();
                documentLibraryPage.getFileDirectoryInfo(fileName).selectCheckbox();

                DocumentLibraryNavigation documentLibraryNavigation = new DocumentLibraryNavigation(drone);
                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryNavigation.selectSyncToCloud().render();
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectSubmitButtonToSync().render();

                checkIfContentIsSynced(drone, fileName);

                ShareUser.logout(drone);

        }

        /**
         * AONE-15425:Cloud icon for a synced file
         */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15425() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String opSiteName = getSiteName(testName) + "-OP";
                String fileName = getTestName() + ".txt";

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

                // --- Step 1 ---
                // --- Step action ---
                // Go to the Document Library of created in preconditions site in Alfresco Share (On-premise)
                // --- Expected results ---
                // Cloud icon is displayed for synced file
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced());
                ShareUser.logout(drone);
        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_15426() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };
                String folderName = getFolderName(testName);

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

                ShareUserSitePage.createFolder(drone, folderName, "").render();

                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(folderName).selectSyncToCloud().render();

                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectSubmitButtonToSync().render();

                checkIfContentIsSynced(drone, folderName);

                ShareUser.logout(drone);
        }

        /**
         * AONE-15426:Cloud icon for a synced folder
         */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15426() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String folderName = getFolderName(testName);
                String opSiteName = getSiteName(testName) + "-OP";

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

                // --- Step 1 ---
                // --- Step action ---
                // Go to the Document Library of created in preconditions site in Alfresco Share (On-premise)
                // --- Expected results ---
                // Cloud icon is displayed near the synced folder
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(folderName).isCloudSynced());
                ShareUser.logout(drone);

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_15427() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String folderName1 = getFolderName(testName) + "1";
                String folderName2 = getFolderName(testName) + "2";
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

                // create files and folders
                ShareUserSitePage.createFolder(drone, folderName1, "").render();
                ShareUserSitePage.createFolder(drone, folderName2, "").render();
                ShareUser.uploadFileInFolder(drone, fileInfo1).render();
                ShareUser.uploadFileInFolder(drone, fileInfo2).render();

                documentLibraryPage.getFileDirectoryInfo(fileName1).selectCheckbox();
                documentLibraryPage.getFileDirectoryInfo(fileName2).selectCheckbox();

                DocumentLibraryNavigation documentLibraryNavigation = new DocumentLibraryNavigation(drone);

                // sync files
                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryNavigation.selectSyncToCloud().render();
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectSubmitButtonToSync().render();

                refreshSharePage(drone).render();

                // sync folder1
                destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(folderName1).selectSyncToCloud().render();
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectSubmitButtonToSync().render();
                checkIfContentIsSynced(drone, folderName1);

                // sync folder2
                destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(folderName2).selectSyncToCloud().render();
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectSubmitButtonToSync().render();
                checkIfContentIsSynced(drone, folderName2);

                ShareUser.logout(drone);
        }

        /**
         * AONE-15427:Cloud icon for synced multiple files
         */

        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15427() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String folderName1 = getFolderName(testName) + "1";
                String folderName2 = getFolderName(testName) + "2";
                String fileName1 = getTestName() + "1" + ".txt";
                String fileName2 = getTestName() + "2" + ".txt";

                String opSiteName = getSiteName(testName) + "-OP";

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

                // --- Step 1 ---
                // --- Step action ---
                // Go to the Document Library of created in preconditions site in Alfresco Share (On-premise)
                // --- Expected results ---
                // Cloud icon is displayed near the synced folder
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(folderName1).isCloudSynced());
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(folderName2).isCloudSynced());
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced());
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName2).isCloudSynced());

                ShareUser.logout(drone);

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_15428() throws Exception
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

                ShareUser.uploadFileInFolder(drone, fileInfo).render();

                ShareUser.logout(drone);
        }

        /**
         * AONE-15428:Pending status
         */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15428() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String fileName = getTestName() + ".txt";

                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

                // --- Step 1 ---
                // --- Step action ---
                // Set cursor to the document and expand More+ menu
                // --- Expected results ---
                // List of actions is appeared for document and 'sync to cloud' option available
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isSyncToCloudLinkPresent());

                // --- Step 2 ---
                // --- Step action ---
                // Choose 'Sync to Cloud' option
                // --- Expected results ---
                // Pop-up window to select target cloud location appears
                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(fileName).selectSyncToCloud().render();
                Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + fileName + " to The Cloud"));

                // --- Step 3 ---
                // --- Step action ---
                // Open Document Library for the created in preconditions in Alfresco Share (On-premise) site in another tab
                // --- Expected results ---
                // Document library page is opened succesfully

                // TODO update the test scenario from test link accordingly with steps bellow
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectSubmitButtonToSync().render();

                // --- Step 4 ---
                // --- Step action ---
                // Click Cloud icon to open Sync Status Dialogue and verify status for the file which is syncing now
                // --- Expected results ---
                // Pop up dialogue appears where Pending status is displayed
                SyncInfoPage syncInfoPage = documentLibraryPage.getFileDirectoryInfo(fileName).clickOnViewCloudSyncInfo().render();
                Assert.assertTrue(syncInfoPage.getCloudSyncStatus().contains("Sync Pending"));

                ShareUser.logout(drone);
        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_15429() throws Exception
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
         * AONE-15429:Synced status
         */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15429() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String fileName = getTestName() + ".txt";
                String opSiteName = getSiteName(testName) + "-OP";

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

                // --- Step 1 ---
                // --- Step action ---
                // Go to the Document Library of created in preconditions in Alfresco Share (On-premise) site
                // --- Expected results ---
                // Document Library page is opened
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                Assert.assertTrue(documentLibraryPage.isBrowserTitle("Alfresco » Document Library"));

                // --- Step 2 ---
                // --- Step action ---
                // Click Cloud icon to open Sync Status Dialogue and verify status for the file which is synced to the Cloud
                // --- Expected results ---
                // Pop up dialogue appears where Synced status is displayed
                SyncInfoPage syncInfoPage = documentLibraryPage.getFileDirectoryInfo(fileName).clickOnViewCloudSyncInfo().render();
                Assert.assertTrue(syncInfoPage.getCloudSyncStatus().contains("Synced"));

                // --- Step 3 ---
                // --- Step action ---
                // Verify last sync date and time is displayed in pop up status dialogue
                // --- Expected results ---
                // Correct last sync date and time is displayed in pop up status dialogue
                Assert.assertNotNull(syncInfoPage.getSyncPeriodDetails());

                ShareUser.logout(drone);
        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_15430() throws Exception
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
         * AONE-15429:Synced status
         */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15430() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String fileName = getTestName() + ".txt";
                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";
                String syncLocation = testDomain + ">" + cloudSiteName + ">" + "Documents";

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

                // --- Step 1 ---
                // --- Step action ---
                // Go to the Document Library of created in preconditions in Alfresco Share (On-premise) site
                // --- Expected results ---
                // Document Library page is opened
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                logger.info("Title  --- " + documentLibraryPage.getTitle());
                Assert.assertTrue(documentLibraryPage.isBrowserTitle("Alfresco » Document Library"));

                // --- Step 2 ---
                // --- Step action ---
                // Click Cloud icon to open Sync Status Dialogue and verify status for the file which is synced to the Cloud
                // --- Expected results ---
                // Pop up dialogue with sync info appears. Cloud location info is displayed
                SyncInfoPage syncInfoPage = documentLibraryPage.getFileDirectoryInfo(fileName).clickOnViewCloudSyncInfo().render();
                Assert.assertTrue(syncInfoPage.getCloudSyncStatus().contains("Synced"));
                Assert.assertEquals(syncInfoPage.getCloudSyncLocation(), syncLocation);

                ShareUser.logout(drone);
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

                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFolderName = getFolderName(testName);
                String opSiteName = getSiteName(testName) + "-OP" + "Aa2";
                String cloudSiteName = getSiteName(testName) + "-CL" + "Aa2";
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
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFolderName = getFolderName(testName);
                String opSiteName = getSiteName(testName) + "-OP" + "Aa2";
                String cloudSiteName = getSiteName(testName) + "-CL" + "Aa2";
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

                editDocumentPropertiesPage = documentLibraryPage.getFileDirectoryInfo(fileName1).selectEditProperties().render();
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

                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opSiteName = getSiteName(testName) + "-OP" + "A4";
                String cloudSiteName = getSiteName(testName) + "-CL" + "A4";
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
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opSiteName = getSiteName(testName) + "-OP" + "A4";
                String cloudSiteName = getSiteName(testName) + "-CL" + "A4";
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
                documentLibraryPage = documentLibraryNavigation.selectRequestSync().render();

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

        @Test(groups = "DataPrepHybrid", timeOut = 300000)
        public void dataPrep_AONE_15445() throws Exception
        {

                String uniqueName = "A5";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

                String folderName1 = getFolderName(testName) + uniqueName + "1";
                String folderName2 = getFolderName(testName) + uniqueName + "2";

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
                destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(folderName2).selectSyncToCloud().render();
                destinationAndAssigneePage.isSiteDisplayed(cloudSiteName);
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectSubmitButtonToSync().render(5000);

                ShareUser.logout(drone);
        }

        /**
         * AONE-15445:Request sync. Change some multiple folders
         */
        @Test(groups = "Hybrid", timeOut = 500000)
        public void AONE_15445() throws Exception
        {
                String uniqueName = "A5";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);

                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

                String folderName1 = getFolderName(testName) + uniqueName + "1";
                String folderName2 = getFolderName(testName) + uniqueName + "2";

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

        @Test(groups = "DataPrepHybrid", timeOut = 300000)
        public void dataPrep_AONE_15446() throws Exception
        {

                String uniqueName = "A3";
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
                String uniqueName = "A3";
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

                String uniqueName = "A1";
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
                String uniqueName = "A1";
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

                String uniqueName = "A4";
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
                String uniqueName = "A4";
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

                String uniqueName = "Aq2";
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
                String uniqueName = "Aq2";
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

                String uniqueName = "Aa13";
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
                String uniqueName = "Aa13";
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

                String uniqueName = "A1";
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
                String uniqueName = "A1";
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

                String uniqueName = "Aa4";
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
                String uniqueName = "Aa4";
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

                String uniqueName = "A1";
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
                String uniqueName = "A1";
                String testName = getTestName();
                String opUser1 = getUserNameForDomain(testName + "opUser1", testDomain);
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

                String uniqueName = "A3";
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
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
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
                String uniqueName = "A3";
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

                String uniqueName = "A4";
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
                String uniqueName = "A4";
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
                DocumentDetailsPage documentDetailsPage = opDocumentLibraryPage.getFileDirectoryInfo(fileName).clickOnTitle().render();
                Assert.assertEquals(documentDetailsPage.getCurrentVersionDetails().getVersionNumber(), "1.1",
                        "The version was not changed after Edit on Cloud");

                ShareUser.logout(drone);
        }

        @Test(groups = "DataPrepHybrid", timeOut = 300000)
        public void dataPrep_AONE_15456() throws Exception
        {

                String uniqueName = "A8";
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
                String uniqueName = "A8";
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

                ShareUser.logout(hybridDrone);

                // --- Step 3 ---
                // --- Step action ---
                //  Login as User (OP), verify version  from DocumentDetailsPage;
                // --- Expected results ---
                // Version is updated to "1.1"

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                DocumentLibraryPage opDocumentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                Assert.assertTrue(opDocumentLibraryPage.isFileVisible(fileName), "The synced file is not displayed");
                documentDetailsPage = opDocumentLibraryPage.getFileDirectoryInfo(fileName).clickOnTitle().render();
                Assert.assertEquals(documentDetailsPage.getCurrentVersionDetails().getVersionNumber(), "1.1",
                        "The version was not changed after Edit on Cloud");

                ShareUser.logout(drone);
        }

        @Test(groups = "DataPrepHybrid", timeOut = 300000)
        public void dataPrep_AONE_15457() throws Exception
        {

                String uniqueName = "A2";
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
                String uniqueName = "A2";
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
                // Attempt to edit the locked file;
                // --- Expected results ---
                // The locked file is not editable.

                Assert.assertFalse(opDocumentLibraryPage.getFileDirectoryInfo(fileName).isInlineEditLinkPresent(), "The locked file can be editable");
                ShareUser.logout(drone);
        }

        @Test(groups = "DataPrepHybrid", timeOut = 300000)
        public void dataPrep_AONE_15458() throws Exception
        {

                String uniqueName = "A1";
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
                String uniqueName = "A1";
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

                String uniqueName = "A1";
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
                String uniqueName = "A1";
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

                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFileName = getFolderName(testName) + "OP";
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

                CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1001");

                // Login to User1, set up the cloud sync
                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD).render();

                // create a file
                DocumentLibraryPage docLib = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ContentDetails contentDetails = new ContentDetails();
                contentDetails.setName(opFileName);
                contentDetails.setTitle(opFileName);
                contentDetails.setDescription(opFileName);
                contentDetails.setContent(opFileName);
                ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.HTML, docLib).render();
                ShareUser.logout(drone);
        }

        /*
        * AONE-15460 Creating a new folder in a cloud target selection window
        */
        @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
        public void AONE_15460() throws Exception
        {

                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);

                String opFileName = getFolderName(testName) + "OP";
                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";

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

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15461() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFileName = getFolderName(testName) + "OP";
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

                // create a file
                DocumentLibraryPage docLib = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ContentDetails contentDetails = new ContentDetails();
                contentDetails.setName(opFileName);
                contentDetails.setTitle(opFileName);
                contentDetails.setDescription(opFileName);
                contentDetails.setContent(opFileName);
                ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.HTML, docLib);
                ShareUser.logout(drone);
        }

        /*
        * AONE-15461 Creating a new folder in a cloud target selection window under documents folder
        */
        @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
        public void AONE_15461() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);

                String opFileName = getFolderName(testName) + "OP";
                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";

                String newFolderName = getFolderName(testName) + "-NAME";
                String newFolderTitle = getFolderName(testName) + "-TITLE";
                String newFolderDescription = getFolderName(testName) + "-DESC";

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
                // In section with folders click the available folder;
                // ---- Expected results ----
                // Available documents or subfolders were displayed;

                destinationAndAssigneePage.selectFolder("Documents");

                // ---- Step 5 ----
                // ---- Step action ----
                // Click on the button '+' in section with folders;
                // ---- Expected results ----
                // Pop-up window New Folder appears with labels 'Name' and 'Description';

                CreateNewFolderInCloudPage newFolderPopup = destinationAndAssigneePage.selectCreateNewFolder().render();
                Assert.assertTrue(newFolderPopup.isNameLabelDisplayed(), "Name label is not displayed.");
                Assert.assertTrue(newFolderPopup.isDescriptionLabelDisplayed(), "Description label is not displayed.");

                // ---- Step 6 ----
                // ---- Step action ----
                // Fill the necessary fields and click 'OK';
                // ---- Expected results ----
                // The folder was successfully created under documents folder in Cloud.

                newFolderPopup.createNewFolder(newFolderName, newFolderTitle, newFolderDescription).render();
                destinationAndAssigneePage = new DestinationAndAssigneePage(drone).render();
                Assert.assertTrue(destinationAndAssigneePage.isFolderDisplayed(newFolderName), "Folder is not displayed.");
        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15462() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFileName = getFileName(testName) + "-FILE-OP";
                String opFolderName = getFolderName(testName) + "-FOLDER-OP";
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

                // create a file
                DocumentLibraryPage docLib = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ContentDetails contentDetails = new ContentDetails();
                contentDetails.setName(opFileName);
                contentDetails.setTitle(opFileName);
                contentDetails.setDescription(opFileName);
                contentDetails.setContent(opFileName);
                ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.HTML, docLib).render();

                // create folder in order to test the ability to create a subfolder
                docLib = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                FileDirectoryInfo fileDirectoryInfo = docLib.getFileDirectoryInfo(opFileName);
                DestinationAndAssigneePage destinationAndAssigneePage = fileDirectoryInfo.selectSyncToCloud().render();
                destinationAndAssigneePage.selectFolder("Documents", opFolderName);
                CreateNewFolderInCloudPage newFolderPopup = destinationAndAssigneePage.selectCreateNewFolder().render();
                newFolderPopup.createNewFolder(opFolderName, opFolderName, opFolderName).render();

                ShareUser.logout(drone);
        }

        /*
        * AONE-15462 Creating a new folder in a cloud target selection window under one of the subfolders
        */
        @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
        public void AONE_15462() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);

                String opFileName = getFileName(testName) + "-FILE-OP";
                String opFolderName = getFolderName(testName) + "-FOLDER-OP";
                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";

                String newSubFolderName = "SUB" + getFolderName(testName) + "-NAME";
                String newSubFolderTitle = "SUB" + getFolderName(testName) + "-TITLE";
                String newSubFolderDescription = "SUB" + getFolderName(testName) + "-DESC";

                // ---- Step 1 ----
                // ---- Step action ----
                // Set cursor to the document and expand More+ menu;
                // ---- Expected results ----
                // List of actions is appeared for document and 'sync to cloud' option available;

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                FileDirectoryInfo fileDirectoryInfo = docLibPage.getFileDirectoryInfo(opFileName);
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
                // In section with folders click on the available folder and then select on the subfolder;
                // ---- Expected results ----
                // The subfolder was selected;

                destinationAndAssigneePage.selectFolder("Documents", opFolderName);

                CreateNewFolderInCloudPage newFolderPopup = destinationAndAssigneePage.selectCreateNewFolder().render();
                Assert.assertTrue(newFolderPopup.isNameLabelDisplayed(), "Name label is not displayed.");
                Assert.assertTrue(newFolderPopup.isDescriptionLabelDisplayed(), "Description label is not displayed.");

                // ---- Step 6 ----
                // ---- Step action ----
                // Fill the necessary fields and click 'OK';
                // ---- Expected results ----
                // The folder was successfully created under one of the subfolders.

                newFolderPopup.createNewFolder(newSubFolderName, newSubFolderTitle, newSubFolderDescription).render();
                destinationAndAssigneePage = new DestinationAndAssigneePage(drone).render();
                Assert.assertTrue(destinationAndAssigneePage.isFolderDisplayed(newSubFolderName), "Subfolder is not displayed");
        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15463() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFileName = getFileName(testName) + "OP";
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

                // create a file
                DocumentLibraryPage docLib = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ContentDetails contentDetails = new ContentDetails();
                contentDetails.setName(opFileName);
                contentDetails.setTitle(opFileName);
                contentDetails.setDescription(opFileName);
                contentDetails.setContent(opFileName);
                ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.HTML, docLib);
                ShareUser.logout(drone);
        }

        /*
        * AONE-15463 Creating a new folder in cloud and sync  a file to it
        */
        @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
        public void AONE_15463() throws Exception
        {

                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);

                String opFileName = getFileName(testName) + "OP";
                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";

                String newFolderName = getFolderName(testName) + "-NAME-" + System.currentTimeMillis();
                String newFolderTitle = getFolderName(testName) + "-TITLE-" + System.currentTimeMillis();
                String newFolderDescription = getFolderName(testName) + "-DESC-" + System.currentTimeMillis();

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

                CreateNewFolderInCloudPage newFolderPopup = destinationAndAssigneePage.selectCreateNewFolder().render();
                Assert.assertTrue(newFolderPopup.isNameLabelDisplayed(), "Name label is not displayed.");
                Assert.assertTrue(newFolderPopup.isDescriptionLabelDisplayed(), "Description label is not displayed.");

                // ---- Step 5 ----
                // ---- Step action ----
                // Fill the necessary fields and press OK button;
                // ---- Expected results ----
                // The folder was successfully created in Cloud;

                newFolderPopup.createNewFolder(newFolderName, newFolderTitle, newFolderDescription).render();
                destinationAndAssigneePage = new DestinationAndAssigneePage(drone).render();
                Assert.assertTrue(destinationAndAssigneePage.isFolderDisplayed(newFolderName), "Folder is not displayed.");

                // ---- Step 6 ----
                // ---- Step action ----
                // Choose target location in the Cloud (network-site-document library-created folder) and press OK button;
                // ---- Expected results ----
                // 'Sync created'notification appear.

                destinationAndAssigneePage.clickSyncButton();
                Assert.assertTrue(documentLibraryPage.isSyncMessagePresent(), "Sync message is not present.");

                // ---- Step 7 ----
                // ---- Step action ----
                // Go to Cloud location (network-site-document library-created folder)set in previous step;
                //
                // ---- Expected results ----
                // The file for which sync action was applied is displayed.

                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
                documentLibraryPage = documentLibraryPage.selectFolder(newFolderName).render();
                Assert.assertNotNull(documentLibraryPage, "Could not select folder.");

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15464() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFileName1 = getFileName(testName) + "-FILE-OP_1";
                String opFileName2 = getFileName(testName) + "-FILE-OP_2";
                String opFolderName = getFolderName(testName) + "-FOLDER-OP";
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

                // create 1 folder and 2 files in it
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUserSitePage.createFolder(drone, opFolderName, "").render();
                documentLibraryPage = documentLibraryPage.selectFolder(opFolderName).render();

                ContentDetails contentDetails = new ContentDetails();
                contentDetails.setName(opFileName1);
                contentDetails.setTitle(opFileName1);
                contentDetails.setDescription(opFileName1);
                contentDetails.setContent(opFileName1);
                ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.HTML, documentLibraryPage);
                documentLibraryPage = documentLibraryPage.selectFolder(opFolderName).render();

                contentDetails = new ContentDetails();
                contentDetails.setName(opFileName2);
                contentDetails.setTitle(opFileName2);
                contentDetails.setDescription(opFileName2);
                contentDetails.setContent(opFileName2);
                ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.HTML, documentLibraryPage);

                ShareUser.logout(drone);
        }

        /*
        * AONE-15464 Creating a new folder in a cloud target selection window and sync a folder with some files in it
        */
        @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
        public void AONE_15464() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);

                String opFileName1 = getFileName(testName) + "-FILE-OP_1";
                String opFileName2 = getFileName(testName) + "-FILE-OP_2";
                String opFolderName = getFolderName(testName) + "-FOLDER-OP";
                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";

                String newFolderName = getFolderName(testName) + "-NAME";
                String newFolderTitle = getFolderName(testName) + "-TITLE";
                String newFolderDescription = getFolderName(testName) + "-DESC";

                // ---- Step 1 ----
                // ---- Step action ----
                // Set cursor to the folder and expand More+ menu;
                // ---- Expected results ----
                // List of actions is appeared for folder and 'sync to cloud' option available;

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                FileDirectoryInfo fileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(opFolderName);
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
                // Click on the button '+' in section with folders;
                // ---- Expected results ----
                // Pop-up 'New Folder' window appears with 'Name' and 'Description' labels;

                CreateNewFolderInCloudPage newFolderPopup = destinationAndAssigneePage.selectCreateNewFolder().render();
                Assert.assertTrue(newFolderPopup.isNameLabelDisplayed(), "Name label is not displayed.");
                Assert.assertTrue(newFolderPopup.isDescriptionLabelDisplayed(), "Description label is not displayed.");

                // ---- Step 4 ----
                // ---- Step action ----
                // Fill the necessary fields and press OK button;
                // ---- Expected results ----
                // The folder was successfully created in Cloud;

                newFolderPopup.createNewFolder(newFolderName, newFolderTitle, newFolderDescription).render();
                destinationAndAssigneePage = new DestinationAndAssigneePage(drone).render();
                Assert.assertTrue(destinationAndAssigneePage.isFolderDisplayed(newFolderName), "Folder is not displayed.");

                // ---- Step 5 ----
                // ---- Step action ----
                // Choose target location in the Cloud (network-site-document library-created folder) and press OK button;
                // ---- Expected results ----
                // 'Sync created'notification appear.

                destinationAndAssigneePage.clickSyncButton();
                Assert.assertTrue(documentLibraryPage.isSyncMessagePresent(), "Sync message is not present");

                drone.refresh();
                documentLibraryPage.getFileDirectoryInfo(opFolderName).selectRequestSync().render();
                checkIfContentIsSynced(drone, opFolderName);
                waitForSync(opFolderName, opSiteName, drone);

                // ---- Step 6 ----
                // ---- Step action ----
                // Go to Cloud location (network-site-document library-created folder)set in previous step;
                //
                // ---- Expected results ----
                // Folder with files in it for which sync action was applied is displayed.

                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
                documentLibraryPage = documentLibraryPage.selectFolder(newFolderName).render();
                documentLibraryPage = documentLibraryPage.selectFolder(opFolderName).render();
                Assert.assertTrue(documentLibraryPage.isItemVisble(opFileName1), "File is not visible.");
                Assert.assertTrue(documentLibraryPage.isItemVisble(opFileName2), "File is not visible.");

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15465() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFileName1 = getFileName(testName) + "-FILE-OP_1";
                String opFileName2 = getFileName(testName) + "-FILE-OP_2";
                String opFolderName = getFolderName(testName) + "-FOLDER-OP";
                String opSubFolderName = getFolderName(testName) + "-SUBFOLDER-OP";

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

                // create 1 folder and 2 files in it
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUserSitePage.createFolder(drone, opFolderName, "").render();
                documentLibraryPage.selectFolder(opFolderName);
                ShareUserSitePage.createFolder(drone, opSubFolderName, "").render();

                ContentDetails contentDetails = new ContentDetails();
                contentDetails.setName(opFileName1);
                contentDetails.setTitle(opFileName1);
                contentDetails.setDescription(opFileName1);
                contentDetails.setContent(opFileName1);
                ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.HTML, documentLibraryPage);
                documentLibraryPage = documentLibraryPage.selectFolder(opFolderName).render();

                contentDetails = new ContentDetails();
                contentDetails.setName(opFileName2);
                contentDetails.setTitle(opFileName2);
                contentDetails.setDescription(opFileName2);
                contentDetails.setContent(opFileName2);
                ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.HTML, documentLibraryPage);

                ShareUser.logout(drone);
        }

        /*
        * AONE-15465 Sync a folder with some files and sub folders in it
        */
        @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
        public void AONE_15465() throws Exception
        {

                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);

                String opFileName1 = getFileName(testName) + "-FILE-OP_1";
                String opFileName2 = getFileName(testName) + "-FILE-OP_2";
                String opFolderName = getFolderName(testName) + "-FOLDER-OP";
                String opSubFolderName = getFolderName(testName) + "-SUBFOLDER-OP";

                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";

                // ---- Step 1 ----
                // ---- Step action ----
                // Set cursor to the folder and expand More+ menu;
                // ---- Expected results ----
                // List of actions is appeared for folder and 'sync to cloud' option available;

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                FileDirectoryInfo fileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(opFolderName);
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
                // Choose target location in the Cloud (network-site-document library-folder), choose 'Sync subfolders' option and press OK button;
                // ---- Expected results ----
                // 'Sync created' notification appears;
                //

                if (!destinationAndAssigneePage.isIncludeSubFoldersSelected())
                {
                        destinationAndAssigneePage.unSelectIncludeSubFolders();
                }

                destinationAndAssigneePage.clickSyncButton();
                documentLibraryPage = new DocumentLibraryPage(drone);
                Assert.assertTrue(documentLibraryPage.isSyncMessagePresent(), "Sync message is not present.");

                drone.refresh();
                documentLibraryPage.getFileDirectoryInfo(opFolderName).selectRequestSync().render();
                waitForSync(opFolderName, opSiteName, drone);

                // ---- Step 4 ----
                // ---- Step action ----
                // Go to Cloud location (network-site-document library-folder) set in previous step;
                // ---- Expected results ----
                // Folder with all files and subfolders for which sync action was applied is displayed.

                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
                documentLibraryPage = documentLibraryPage.selectFolder(opFolderName).render();
                Assert.assertTrue(documentLibraryPage.isItemVisble(opSubFolderName), "Folder is not visible.");
                Assert.assertTrue(documentLibraryPage.isItemVisble(opFileName1), "File is not visible.");
                Assert.assertTrue(documentLibraryPage.isItemVisble(opFileName2), "File is not visible.");

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15466() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFileName1 = getFileName(testName) + "-FILE-OP_1";
                String opFileName2 = getFileName(testName) + "-FILE-OP_2";
                String opFolderName = getFolderName(testName) + "-FOLDER-OP";
                String opSubFolderName = getFolderName(testName) + "-SUBFOLDER-OP";

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

                // create 1 folder and 2 files in it
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUserSitePage.createFolder(drone, opFolderName, "").render();
                documentLibraryPage.selectFolder(opFolderName);
                ShareUserSitePage.createFolder(drone, opSubFolderName, "").render();

                ContentDetails contentDetails = new ContentDetails();
                contentDetails.setName(opFileName1);
                contentDetails.setTitle(opFileName1);
                contentDetails.setDescription(opFileName1);
                contentDetails.setContent(opFileName1);
                ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.HTML, documentLibraryPage).render();
                documentLibraryPage = documentLibraryPage.selectFolder(opFolderName).render();

                contentDetails = new ContentDetails();
                contentDetails.setName(opFileName2);
                contentDetails.setTitle(opFileName2);
                contentDetails.setDescription(opFileName2);
                contentDetails.setContent(opFileName2);
                ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.HTML, documentLibraryPage).render();

                ShareUser.logout(drone);
        }

        /*
        * AONE-15466 Sync a folder with some files and sub folders in it. 'Not to sync subfolders' option
        */
        @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
        public void AONE_15466() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);

                String opFileName1 = getFileName(testName) + "-FILE-OP_1";
                String opFileName2 = getFileName(testName) + "-FILE-OP_2";
                String opFolderName = getFolderName(testName) + "-FOLDER-OP";
                String opSubFolderName = getFolderName(testName) + "-SUBFOLDER-OP";

                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";

                // ---- Step 1 ----
                // ---- Step action ----
                // Set cursor to the folder and expand More+ menu;
                // ---- Expected results ----
                // List of actions is appeared for folder and 'sync to cloud' option available;

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                FileDirectoryInfo fileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(opFolderName);
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
                // Choose target location in the Cloud (network-site-document  library-folder-sub folder), choose 'Not to sync subfolders' option and  press OK button;
                // ---- Expected results ----
                // 'Sync created' notification appears;

                if (destinationAndAssigneePage.isIncludeSubFoldersSelected())
                {
                        destinationAndAssigneePage.unSelectIncludeSubFolders();
                }

                destinationAndAssigneePage.clickSyncButton();
                documentLibraryPage = new DocumentLibraryPage(drone);
                Assert.assertTrue(documentLibraryPage.isSyncMessagePresent(), "Sync message is not present.");

                // ---- Step 4 ----
                // ---- Step action ----
                // Go to Cloud location (network-site-document library-folder) set in previous step;
                // ---- Expected results ----
                // Folder with all files for which sync action was applied is displayed but subfolders are not displayed.

                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
                documentLibraryPage = documentLibraryPage.selectFolder(opFolderName).render();
                Assert.assertFalse(documentLibraryPage.isItemVisble(opSubFolderName), "Subfolder is visible");
                Assert.assertTrue(documentLibraryPage.isItemVisble(opFileName1), "File is not visible");
                Assert.assertTrue(documentLibraryPage.isItemVisble(opFileName2), "File is not visible");

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15467() throws Exception
        {
                /*
                1. At least 2 users (user1,user2) are created in Alfresco Share (On-premise);
                 */
                String testName = getTestName();
                String opUser1 = getUserNameForDomain(testName + "opUser-1", testDomain);
                String[] userInfo1 = new String[] { opUser1 };
                String opFileName = getFileName(testName);
                String opUser2 = getUserNameForDomain(testName + "opUser-2", testDomain);
                String[] userInfo2 = new String[] { opUser2 };

                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opSiteName = getSiteName(testName) + "-OP1";
                String cloudSiteName = getSiteName(testName) + "-CL";

                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo2);
                CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

                /*
                2. At least one network and one site created by user1 are setup in Cloud;
                 */
                ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
                ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(drone);

                /*
                3. At least user1 has access to the Cloud account;
                 */

                // Cloud user logins and create site.
                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(hybridDrone);

                // Login to User1, set up the cloud sync
                ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);


                /*
                4. User1 is logged in Alfresco Share (On-premise);
                5. Any site (e.g. Test) is created in Alfresco Share (On-premise) by user1;
                6. Document Library page of the created site (e.g. Test) is opened;
                7. Any files/folders are created into the Document Library and synced to Clout;
                 */
                ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

                ContentDetails contentDetails = new ContentDetails();
                contentDetails.setName(opFileName);
                contentDetails.setTitle(opFileName);
                contentDetails.setDescription(opFileName);
                contentDetails.setContent(opFileName);
                ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.HTML, documentLibraryPage).render();

                DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(opFileName).render();
                DestinationAndAssigneePage destinationAndAssigneePage = documentDetailsPage.selectSyncToCloud().render();
                destinationAndAssigneePage.clickSyncButton();
                ShareUser.openSiteDashboard(drone, opSiteName).render();
                ShareUserMembers.inviteUserToSiteWithRole(drone, opUser1, opUser2, opSiteName, UserRole.CONSUMER);

                ShareUser.logout(drone);
        }

        /*
        * AONE-15467 Checking synced files and the sync status by consumer
        */
        @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
        public void AONE_15467() throws Exception
        {

                String testName = getTestName();
                String opUser1 = getUserNameForDomain(testName + "opUser-1", testDomain);
                String opUser2 = getUserNameForDomain(testName + "opUser-2", testDomain);
                String opFileName = getFileName(testName);
                String opSiteName = getSiteName(testName) + "-OP1";

                // ---- Step 1 ----
                // ---- Step action ----
                // Log in Alfresco Share (On-premise) as user2;
                // ---- Expected results ----
                // User2 is logged in Alfresco Share (On-premise) successfully;

                ShareUser.login(drone, opUser2, DEFAULT_PASSWORD);

                // ---- Step 2 ----
                // ---- Step action ----
                // Go to the Document Library of created in preconditions in Alfresco Share (On-premise) site;
                // ---- Expected results ----
                // Document Library page is opened and all synced files are displayed correctly on it;

                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                Assert.assertNotNull(documentLibraryPage, "Failed to open document library page.");

                // ---- Step 3 ----
                // ---- Step action ----
                // Click on the synced file to open its details page;
                // ---- Expected results ----
                // Details page for synced document is successfully opened and information is displayed correctly;

                DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(opFileName).render();
                Assert.assertNotNull(documentDetailsPage, "Failed to open file.");

                // ---- Step 4 ----
                // ---- Step action ----
                // Check the sync status in section 'Sync';
                // ---- Expected results ----
                // The sync status is displayed correctly.

                String syncStatus = documentDetailsPage.getSyncStatus();

                Assert.assertTrue(syncStatus.contains("Synced"), "Sync status is not displayed properly");
                Assert.assertTrue(syncStatus.contains(opUser1), "Sync status is not displayed properly");

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15468() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFileName = getFileName(testName) + "-FILE-OP";
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

                // create a file
                DocumentLibraryPage docLib = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ContentDetails contentDetails = new ContentDetails();
                contentDetails.setName(opFileName);
                contentDetails.setTitle(opFileName);
                contentDetails.setDescription(opFileName);
                contentDetails.setContent(opFileName);
                ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.HTML, docLib).render();
                ShareUser.logout(drone);
        }

        /*
        * AONE-15468 Checking the status 'Sync Pending' by owner
        */
        @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
        public void AONE_15468() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String opFileName = getFileName(testName) + "-FILE-OP";
                String opSiteName = getSiteName(testName) + "-OP";

                // ---- Step 1 ----
                // ---- Step action ----
                // Set cursor to the file/folder and expand More+ menu;
                // ---- Expected results ----
                // List of actions is appeared for folder and 'sync to cloud' option available;

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
                // Choose target location in the Cloud (network-site-document  library-folder) and  press OK button;
                // ---- Expected results ----
                // 'Sync created' notification appears;

                destinationAndAssigneePage.clickSyncButton();
                documentLibraryPage = new DocumentLibraryPage(drone);
                Assert.assertTrue(documentLibraryPage.isSyncMessagePresent(), "Sync message is not present.");

                // ---- Step 4 ----
                // ---- Step action ----
                // Whilst syncing go to Document Library and click on the syncing file to open its details page;
                // ---- Expected results ----
                // Details page for syncing document is successfully opened and information is displayed correctly;

                // ---- Step 5 ----
                // ---- Step action ----
                // Check the sync status in section 'Sync';
                // ---- Expected results ----
                // The sync status is displayed as 'Sync Pending'.
                SyncInfoPage syncInfoPage = documentLibraryPage.getFileDirectoryInfo(opFileName).clickOnViewCloudSyncInfo().render();
                Assert.assertTrue(syncInfoPage.getCloudSyncStatus().contains("Sync Pending"), "Status doesn't contain 'Sync Pending'.");

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15469() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFileName = getFileName(testName) + "-FILE-OP";
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

                // create a file
                DocumentLibraryPage docLib = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ContentDetails contentDetails = new ContentDetails();
                contentDetails.setName(opFileName);
                contentDetails.setTitle(opFileName);
                contentDetails.setDescription(opFileName);
                contentDetails.setContent(opFileName);
                ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.HTML, docLib).render();
                ShareUser.logout(drone);
        }

        /*
        * AONE-15469 Checking the status 'Sync Successful' by owner
        */
        @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
        public void AONE_15469() throws Exception
        {

                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String opFileName = getFileName(testName) + "-FILE-OP";
                String opSiteName = getSiteName(testName) + "-OP";

                // ---- Step 1 ----
                // ---- Step action ----
                // Set cursor to the file/folder and expand More+ menu;
                // ---- Expected results ----
                // List of actions is appeared for folder and 'sync to cloud' option available;

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
                // Choose target location in the Cloud (network-site-document  library-folder) and  press OK button;
                // ---- Expected results ----
                // 'Sync created' notification appears;

                destinationAndAssigneePage.clickSyncButton();
                documentLibraryPage = new DocumentLibraryPage(drone).render();
                Assert.assertTrue(documentLibraryPage.isSyncMessagePresent(), "Sync message is not present.");

                // ---- Step 4 ----
                // ---- Step action ----
                // After 1 min of sync go to Document Library and click on the synced file to open its details page;
                // ---- Expected results ----
                // Details page for synced document is successfully opened and information is displayed correctly;

                wait(120); // Asked by the scenario. Do not change!
                DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(opFileName).render();
                Assert.assertNotNull(documentDetailsPage, "Failed to open file.");

                // ---- Step 5 ----
                // ---- Step action ----
                // Check the sync status in section 'Sync';
                // ---- Expected results ----
                // The sync status is displayed as 'Sync Successful'.

                String syncStatus = documentDetailsPage.getSyncStatus().toLowerCase();
                logger.info(syncStatus);
                Assert.assertTrue(syncStatus.contains("synced"), "Status is incorrect.");

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15470() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFileName = getFileName(testName) + "-FILE-OP";
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

                // create a file
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ContentDetails contentDetails = new ContentDetails();
                contentDetails.setName(opFileName);
                contentDetails.setTitle(opFileName);
                contentDetails.setDescription(opFileName);
                contentDetails.setContent(opFileName);
                ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.HTML, documentLibraryPage).render();
                FileDirectoryInfo fileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(opFileName);
                DestinationAndAssigneePage destinationAndAssigneePage = fileDirectoryInfo.selectSyncToCloud().render();
                destinationAndAssigneePage.clickSyncButton();
                ShareUser.logout(drone);
        }

        /*
        * AONE-15470 Checking the sync status. Last sync date, time and cloud location
        */
        @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
        public void AONE_15470() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String opFileName = getFileName(testName) + "-FILE-OP";
                String opSiteName = getSiteName(testName) + "-OP";

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                // ---- Step 1 ----
                // ---- Step action ----
                // Go to the Document Library of created in preconditions in Alfresco Share (On-premise) site;
                // ---- Expected results ----
                // Document Library page is opened and all information is displayed correctly on it;

                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                Assert.assertNotNull(documentLibraryPage, "Failed to open document library page.");

                // ---- Step 2 ----
                // ---- Step action ----
                // Click on the synced file to open its details page;
                // ---- Expected results ----
                // Details page for synced document is successfully opened and information is displayed correctly;

                DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(opFileName).render();
                Assert.assertNotNull(documentDetailsPage, "Failed to open file");

                // ---- Step 3 ----
                // ---- Step action ----
                // Check the sync status in sections 'Sync';
                // ---- Expected results ----
                // The sync status displays last sync date, time and cloud location.

                Assert.assertNotNull(documentDetailsPage.getLocationInCloud(), "Failed to get 'Location in cloud'");
                Assert.assertNotNull(documentDetailsPage.getSyncStatus(), "Failed to get 'Sync status'.");
        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15471() throws Exception
        {
                /*
                1. At least one network and one site are setup in Cloud;
                2. User1, User2 are created in (On Premise);
                 */
                String testName = getTestName();
                String opUser1 = getUserNameForDomain(testName + "opUser-1", testDomain);
                String[] userInfo1 = new String[] { opUser1 };
                String opFileName = getFileName(testName);
                String opUser2 = getUserNameForDomain(testName + "opUser-2", testDomain);
                String[] userInfo2 = new String[] { opUser2 };

                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opSiteName = getSiteName(testName) + "-OP1";
                String cloudSiteName = getSiteName(testName) + "-CL";

                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo2);
                CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

                /*
                2. User1 (Owner) have access to the Cloud account;
                3. User is logged in Alfresco Share (On-premise);
                4. Any site is created by User1 in Alfresco Share (On-premise);
                5. User2 is invited to site as Collaborator;
                6. User2 accept the invitation;
                 */
                ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
                ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUserMembers.inviteUserToSiteWithRole(drone, opUser1, opUser2, opSiteName, UserRole.COLLABORATOR);
                ShareUser.logout(drone);

                // Cloud user logins and create site.
                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(hybridDrone);

                // Login to User1, set up the cloud sync
                ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
                ShareUser.logout(drone);

                /*
                7. Document Library page of the created Site is opened by User2;
                8. Any file/folder is created under the Document Library;
                 */
                ShareUser.login(drone, opUser2, DEFAULT_PASSWORD);
                DocumentLibraryPage docLib = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ContentDetails contentDetails = new ContentDetails();
                contentDetails.setName(opFileName);
                contentDetails.setTitle(opFileName);
                contentDetails.setDescription(opFileName);
                contentDetails.setContent(opFileName);
                ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.HTML, docLib).render();
                ShareUser.logout(drone);

                /*
                9. User1 who owns the site sync the file with cloud.
                 */
                ShareUser.login(drone, opUser1);
                docLib = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                DocumentDetailsPage documentDetailsPage = docLib.selectFile(opFileName).render();
                DestinationAndAssigneePage destinationAndAssigneePage = documentDetailsPage.selectSyncToCloud().render();
                destinationAndAssigneePage.clickSyncButton();
                ShareUser.logout(drone);

        }

        /*
         * AONE-15471: Unsync the file from cloud by owner. Select to remove the file
         */
        @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
        public void AONE_15471() throws Exception
        {
                String testName = getTestName();
                String opUser1 = getUserNameForDomain(testName + "opUser-1", testDomain);
                String opUser2 = getUserNameForDomain(testName + "opUser-2", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String opSiteName = getSiteName(testName) + "-OP1";
                String cloudSiteName = getSiteName(testName) + "-CL";
                String opFileName = getFileName(testName);

                // ---- Step 1 ----
                // ---- Step action ----
                // User2 logins and check the sync status of (Its own file).
                // ---- Expected results
                // All information is displayed correctly on it;

                ShareUser.login(drone, opUser2);
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(opFileName).render();
                String syncStatus = documentDetailsPage.getSyncStatus();
                Assert.assertNotNull(syncStatus, "Failed to get 'Sync status'");

                // ---- Step 2 ----
                // ---- Step action ----
                // Click the cloud icon against the file wich is synced;
                // ---- Expected results
                // Popup dialogue with sync info appears;

                documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                FileDirectoryInfo fileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(opFileName);
                SyncInfoPage syncInfoPage = fileDirectoryInfo.clickOnViewCloudSyncInfo().render();
                Assert.assertNotNull(syncInfoPage, "Failed to open popup dialog with sync info.");

                // ---- Step 3 ----
                // ---- Step action ----
                // Verify Unsync button is available on Sync info pop-up window;
                // ---- Expected results
                // Unsync button is available and enabled;

                Assert.assertTrue(syncInfoPage.isUnsyncButtonPresent(), "Unsync button is not present.");
                Assert.assertTrue(syncInfoPage.isUnsyncButtonEnabled(), "Unsync button is not enabled.");

                // ---- Step 4 ----
                // ---- Step action ----
                // Click on the 'Unsync' button and select 'Remove the file from cloud' option on popup window;
                // ---- Expected results
                // File unsynced notification appears;

                syncInfoPage.selectUnsyncRemoveContentFromCloud(true);
                ShareUser.logout(drone);

                // ---- Step 5 ----
                // ---- Step action ----
                // Go to the target cloud location which was set in preconditions for sync;
                // ---- Expected results
                // Target location in Cloud (Network->Site->Document Library page) is opened. Unsynced file removed from Cloud.

                ShareUser.login(hybridDrone, cloudUser);
                documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
                Assert.assertFalse(documentLibraryPage.isFileVisible(opFileName), "File is visible.");

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15472() throws Exception
        {
                String testName = getTestName();
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFileName = getFileName(testName) + "-FILE-OP";
                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";

                CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

                ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
                ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(drone);

                // Cloud user logins and create site.
                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(hybridDrone);

                // Login to User1, set up the cloud sync
                ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

                // create a file
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ContentDetails contentDetails = new ContentDetails();
                contentDetails.setName(opFileName);
                contentDetails.setTitle(opFileName);
                contentDetails.setDescription(opFileName);
                contentDetails.setContent(opFileName);
                ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.HTML, documentLibraryPage).render();
                FileDirectoryInfo fileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(opFileName);
                DestinationAndAssigneePage destinationAndAssigneePage = fileDirectoryInfo.selectSyncToCloud().render();
                destinationAndAssigneePage.clickSyncButton();
                ShareUser.logout(drone);
        }

        /*
        * AONE-15472 Unsync the file from cloud by Admin/site manager. Select to remove the file
        */
        @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
        public void AONE_15472() throws Exception
        {
                String testName = getTestName();
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFileName = getFileName(testName) + "-FILE-OP";
                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";

                // ---- Step 1 ----
                // ---- Step action ----
                // Go to the Document Library of created in preconditions in Alfresco Share (On-premise) site;
                // ---- Expected results ----
                // Document Library page is opened and all information is displayed correctly on it;

                ShareUser.login(drone, ADMIN_USERNAME);
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                Assert.assertNotNull(documentLibraryPage, "Failed to open document library page.");

                // ---- Step 2 ----
                // ---- Step action ----
                // Click the cloud icon against the file wich is synced;
                // ---- Expected results ----
                // Popup dialogue with sync info appears;

                FileDirectoryInfo fileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(opFileName);
                SyncInfoPage syncInfoPage = fileDirectoryInfo.clickOnViewCloudSyncInfo().render();
                Assert.assertNotNull(syncInfoPage, "Failed to open sync info popup dialog.");

                // ---- Step 3 ----
                // ---- Step action ----
                // Verify Unsync button is available on Sync info pop-up window;
                // ---- Expected results ----
                // Unsync button is available and enabled;

                Assert.assertTrue(syncInfoPage.isCloseButtonPresent(), "Unsync button is not present.");
                Assert.assertTrue(syncInfoPage.isUnsyncButtonEnabled(), "Unsync button is not visible.");

                // ---- Step 4 ----
                // ---- Step action ----
                // Click on the 'Unsync' button and select 'Remove the file from cloud' option on popup window;
                // ---- Expected results ----
                // File unsynced notification appears;

                syncInfoPage.selectUnsyncRemoveContentFromCloud(true);

                // ---- Step 5 ----
                // ---- Step action ----
                // Go to the target cloud location which was set in preconditions for sync;
                // ---- Expected results ----
                // Target location in Cloud (Network-Site-Document Library page) is opened. Unsynced file removed from Cloud.

                ShareUser.login(hybridDrone, cloudUser);
                documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();

                int maxRefreshes = 60;
                while (documentLibraryPage.isItemVisble(opFileName) && maxRefreshes > 0)
                {
                        hybridDrone.refresh();
                        maxRefreshes = maxRefreshes - 1;
                        wait(1);
                }

                Assert.assertFalse(documentLibraryPage.isFileVisible(opFileName), "File is still visible on the cloud.");

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15473() throws Exception
        {
                String testName = getTestName();
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFileName = getFileName(testName) + "-FILE-OP";
                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";

                CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

                ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
                ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(drone);

                // Cloud user logins and create site.
                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(hybridDrone);

                // Login to User1, set up the cloud sync
                ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

                // create a file
                DocumentLibraryPage docLib = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ContentDetails contentDetails = new ContentDetails();
                contentDetails.setName(opFileName);
                contentDetails.setTitle(opFileName);
                contentDetails.setDescription(opFileName);
                contentDetails.setContent(opFileName);
                ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.HTML, docLib).render();
                FileDirectoryInfo fileDirectoryInfo = docLib.getFileDirectoryInfo(opFileName);
                DestinationAndAssigneePage destinationAndAssigneePage = fileDirectoryInfo.selectSyncToCloud().render();
                destinationAndAssigneePage.clickSyncButton();
                ShareUser.logout(drone);
        }

        /*
        * AONE-15473 Unsync the file from cloud by owner/site manager. Select 'do not remove the file'
        */
        @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
        public void AONE_15473() throws Exception
        {
                String testName = getTestName();
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFileName = getFileName(testName) + "-FILE-OP";
                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";

                // ---- Step 1 ----
                // ---- Step action ----
                // Go to the Document Library of created in preconditions in Alfresco Share (On-premise) site;
                // ---- Expected results ----
                // Document Library page is opened and all information is displayed correctly on it;

                ShareUser.login(drone, ADMIN_USERNAME);
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                Assert.assertNotNull(documentLibraryPage, "Failed to open document library page.");

                // ---- Step 2 ----
                // ---- Step action ----
                // Click the cloud icon against the file wich is synced;
                // ---- Expected results ----
                // Popup dialogue with sync info appears;

                FileDirectoryInfo fileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(opFileName);
                SyncInfoPage syncInfoPage = fileDirectoryInfo.clickOnViewCloudSyncInfo().render();
                Assert.assertNotNull(syncInfoPage, "Popup dialog with sync info doesn't appear.");

                // ---- Step 3 ----
                // ---- Step action ----
                // Verify Unsync button is available on Sync info pop-up window;
                // ---- Expected results ----
                // Unsync button is available and enabled;

                Assert.assertTrue(syncInfoPage.isCloseButtonPresent(), "Unsync button is not present.");
                Assert.assertTrue(syncInfoPage.isUnsyncButtonEnabled(), "Unsync button is not enabled.");

                // ---- Step 4 ----
                // ---- Step action ----
                // Click on the 'Unsync' button and select 'Do not to remove the file from cloud' option on popup window;
                // ---- Expected results ----
                // File unsynced notification appears;

                syncInfoPage.selectUnsyncRemoveContentFromCloud(false);

                // ---- Step 5 ----
                // ---- Step action ----
                // Go to the target cloud location which was set in preconditions for sync;
                // ---- Expected results ----
                // Target location in Cloud (Network-Site-Document Library page) is opened. Unsynced file is not removed from Cloud;

                ShareUser.login(hybridDrone, cloudUser);
                documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
                Assert.assertTrue(documentLibraryPage.isFileVisible(opFileName), "File is not visible on the cloud.");

                // ---- Step 6 ----
                // ---- Step action ----
                // Go to the Document Library of created in Alfresco Share (On-premise) site and update the file;
                // ---- Expected results ----
                // Document Library page is opened and all information is displayed correctly on it, the file is updated;

                documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                EditDocumentPropertiesPage editDocumentPropertiesPage = documentLibraryPage.getFileDirectoryInfo(opFileName).selectEditProperties().render();
                editDocumentPropertiesPage.setDescription("CHANGED DESCRIPTION");
                editDocumentPropertiesPage.selectSave().render();

                // ---- Step 7 ----
                // ---- Step action ----
                // Go to the target cloud location and make sure the updated file is not syncing the cloud copy.
                // ---- Expected results ----
                // Target location in Cloud is opened, the updated file is not syncing the cloud copy.

                documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
                editDocumentPropertiesPage = documentLibraryPage.getFileDirectoryInfo(opFileName).selectEditProperties().render();
                String cloudDescription = editDocumentPropertiesPage.getDescription();
                Assert.assertFalse(cloudDescription.contains("CHANGED DESCRIPTION"), "File description did not change.");

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15474() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFileName = getFileName(testName) + "-FILE-OP";
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

                // create a file
                DocumentLibraryPage docLib = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ContentDetails contentDetails = new ContentDetails();
                contentDetails.setName(opFileName);
                contentDetails.setTitle(opFileName);
                contentDetails.setDescription(opFileName);
                contentDetails.setContent(opFileName);
                ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.HTML, docLib).render();
                FileDirectoryInfo fileDirectoryInfo = docLib.getFileDirectoryInfo(opFileName);
                DestinationAndAssigneePage destinationAndAssigneePage = fileDirectoryInfo.selectSyncToCloud().render();
                destinationAndAssigneePage.clickSyncButton();
                ShareUser.logout(drone);
        }

        /*
        * AONE-15474 Changes to file in on-premise/cloud
        */
        @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
        public void AONE_15474() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);

                String opFileName = getFileName(testName) + "-FILE-OP";
                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";

                String opChange = "CHANGED DESCRIPTION = OP";
                String cloudChange = "CHANGED DESCRIPTION = CLOUD";
                String newDescription;

                // ---- Step 1 ----
                // ---- Step action ----
                // Make some changes to the synced file in on-premise
                // ---- Expected results ----
                // Changes are made and saved for synced file in on- premise

                ShareUser.login(drone, opUser);
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                EditDocumentPropertiesPage editDocumentPropertiesPage = documentLibraryPage.getFileDirectoryInfo(opFileName).selectEditProperties().render();
                editDocumentPropertiesPage.setDescription(opChange);
                editDocumentPropertiesPage.selectSave().render();

                // ---- Step 2 ----
                // ---- Step action ----
                // Verify the changes in Cloud
                // ---- Expected results ----
                // The changes are applied after a while

                ShareUser.login(hybridDrone, cloudUser);
                documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
                wait(5);
                hybridDrone.refresh();
                editDocumentPropertiesPage = documentLibraryPage.getFileDirectoryInfo(opFileName).selectEditProperties().render();
                newDescription = editDocumentPropertiesPage.getDescription();
                Assert.assertEquals(newDescription, opChange, "File description did not change.");

                // ---- Step 3 ----
                // ---- Step action ----
                // Make some changes to the synced file in Cloud
                // ---- Expected results ----
                // Changes are made and saved in Cloud

                editDocumentPropertiesPage.setDescription(cloudChange);
                editDocumentPropertiesPage.selectSave().render();

                // ---- Step 4 ----
                // ---- Step action ----
                // Verify the changes in on-premise
                // ---- Expected results ----
                // The changes are applied after a bit of time

                ShareUser.openSiteDashboard(drone, opSiteName);
                documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                documentLibraryPage = documentLibraryPage.getFileDirectoryInfo(opFileName).selectRequestSync().render();
                waitForSync(opFileName, opSiteName, drone);
                editDocumentPropertiesPage = documentLibraryPage.getFileDirectoryInfo(opFileName).selectEditProperties().render();
                newDescription = editDocumentPropertiesPage.getDescription();
                Assert.assertEquals(newDescription, cloudChange, "File description did not change.");

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15475() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFolderName = getFolderName(testName) + "-FOLDER-OP";
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

                // create a file
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUserSitePage.createFolder(drone, opFolderName, opFolderName, opFolderName).render();
                FileDirectoryInfo fileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(opFolderName);
                DestinationAndAssigneePage destinationAndAssigneePage = fileDirectoryInfo.selectSyncToCloud().render();
                destinationAndAssigneePage.clickSyncButton();
                ShareUser.logout(drone);
        }

        /*
        * AONE-15475 Changes to folder in on-premise/cloud
        */
        @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
        public void AONE_15475() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);

                String opFolderName = getFolderName(testName) + "-FOLDER-OP";
                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";

                String opChange = "CHANGED DESCRIPTION = OP";
                String cloudChange = "CHANGED DESCRIPTION = CLOUD";
                String newDescription;

                // ---- Step 1 ----
                // ---- Step action ----
                // Make some changes to the synced folder in on-premise
                // ---- Expected results ----
                // Changes are made and saved for synced folder in on- premise

                ShareUser.login(drone, opUser);
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                EditDocumentPropertiesPage editDocumentPropertiesPage = documentLibraryPage.getFileDirectoryInfo(opFolderName).selectEditProperties().render();
                editDocumentPropertiesPage.setDescription(opChange);
                editDocumentPropertiesPage.clickSave();

                drone.refresh();
                documentLibraryPage.getFileDirectoryInfo(opFolderName).selectRequestSync().render();
                waitForSync(opFolderName, opSiteName, drone);

                // ---- Step 2 ----
                // ---- Step action ----
                // Verify the changes in Cloud
                // ---- Expected results ----
                // The changes are applied after a while

                ShareUser.login(hybridDrone, cloudUser);
                wait(5);
                documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
                editDocumentPropertiesPage = documentLibraryPage.getFileDirectoryInfo(opFolderName).selectEditProperties().render();
                newDescription = editDocumentPropertiesPage.getDescription();
                Assert.assertEquals(newDescription, opChange, "File description did not change.");

                // ---- Step 3 ----
                // ---- Step action ----
                // Make some changes to the synced folder in Cloud
                // ---- Expected results ----
                // Changes are made and saved in Cloud

                editDocumentPropertiesPage.setDescription(cloudChange);
                editDocumentPropertiesPage.clickSave();

                // ---- Step 4 ----
                // ---- Step action ----
                // Verify the changes in on-premise
                // ---- Expected results ----
                // The changes are applied after a bit of time

                ShareUser.openSiteDashboard(drone, opSiteName);
                documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                drone.refresh();
                documentLibraryPage = documentLibraryPage.getFileDirectoryInfo(opFolderName).selectRequestSync().render();
                waitForSync(opFolderName, opSiteName, drone);
                editDocumentPropertiesPage = documentLibraryPage.getFileDirectoryInfo(opFolderName).selectEditProperties().render();
                newDescription = editDocumentPropertiesPage.getDescription();
                Assert.assertEquals(newDescription, cloudChange, "File description did not change.");
        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15476() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFolderName = getFolderName(testName) + "-FOLDER-OP";
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

                // create a file
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUserSitePage.createFolder(drone, opFolderName, opFolderName, opFolderName);
                FileDirectoryInfo fileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(opFolderName);
                DestinationAndAssigneePage destinationAndAssigneePage = fileDirectoryInfo.selectSyncToCloud().render();
                destinationAndAssigneePage.clickSyncButton();
                ShareUser.logout(drone);
        }

        /*
        * AONE-15476 Unsync the folder from cloud by owner. Select to remove the folder
        */
        @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
        public void AONE_15476() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);

                String opFolderName = getFolderName(testName) + "-FOLDER-OP";
                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";

                // ---- Step 1 ----
                // ---- Step action ----
                // Go to the Document Library of created in preconditions in Alfresco Share (On-premise) site;
                // ---- Expected results ----
                // Document Library page is opened and all information is displayed correctly on it;

                ShareUser.login(drone, opUser);
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                Assert.assertNotNull(documentLibraryPage, "Failed to open document library page.");

                // ---- Step 2 ----
                // ---- Step action ----
                // Click the cloud icon against the folder wich is synced;
                // ---- Expected results ----
                // Popup dialogue with sync info appears;

                documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                FileDirectoryInfo fileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(opFolderName);
                SyncInfoPage syncInfoPage = fileDirectoryInfo.clickOnViewCloudSyncInfo().render();
                Assert.assertNotNull(syncInfoPage, "Failed to open sync info popup dialog for sync info.");

                // ---- Step 3 ----
                // ---- Step action ----
                // Verify Unsync button is available on Sync info pop-up window;
                // ---- Expected results ----
                // Unsync button is available and enabled;

                Assert.assertTrue(syncInfoPage.isUnsyncButtonPresent(), "Unsync button is not present.");
                Assert.assertTrue(syncInfoPage.isUnsyncButtonEnabled(), "Unsync button is not enabled.");

                // ---- Step 4 ----
                // ---- Step action ----
                // Click on the 'Unsync' button and select 'Remove the folder from cloud' option on popup window;
                // ---- Expected results ----
                // Folder unsynced notification appears;

                syncInfoPage.selectUnsyncRemoveContentFromCloud(true);

                // ---- Step 5 ----
                // ---- Step action ----
                // Go to the target cloud location which was set in preconditions for sync;
                // ---- Expected results ----
                // Target location in Cloud (Network-Site-Document Library page) is opened. Unsynced folder is removed from Cloud.

                ShareUser.login(hybridDrone, cloudUser);
                documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();

                int maxRefreshes = 30;
                while (documentLibraryPage.isItemVisble(opFolderName) && maxRefreshes > 0)
                {
                        hybridDrone.refresh();
                        maxRefreshes = maxRefreshes - 1;
                }

                Assert.assertFalse(documentLibraryPage.isItemVisble(opFolderName), "File is still visible.");

                ShareUser.logout(drone);
                ShareUser.logout(hybridDrone);

        }

        private void wait(int k)
        {
                long time0, time1;
                time0 = System.currentTimeMillis();
                do
                {
                        time1 = System.currentTimeMillis();
                }
                while ((time1 - time0) < k * 1000);
        }

        private void waitForSync(String fileName, String siteName, WebDrone _drone)
        {
                int counter = 1;
                int retryRefreshCount = 4;
                while (counter <= retryRefreshCount)
                {
                        if (checkIfContentIsSynced(_drone, fileName))
                        {
                                break;
                        }
                        else
                        {
                                logger.info("Wait for Sync");

                                _drone.refresh();
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
