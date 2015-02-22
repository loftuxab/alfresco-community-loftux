package org.alfresco.share.cloudsync;

import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryNavigation;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.SyncInfoPage;
import org.alfresco.po.share.user.CloudSignInPage;
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

/*

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
*/

}
