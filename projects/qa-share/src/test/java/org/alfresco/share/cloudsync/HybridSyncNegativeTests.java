package org.alfresco.share.cloudsync;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.document.*;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.alfresco.share.util.ShareUser.openSitesDocumentLibrary;
import static org.alfresco.share.util.ShareUser.refreshDocumentLibrary;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created by P3700481 on 12/9/2014.
 */
@Listeners(FailedTestListener.class)
public class HybridSyncNegativeTests extends AbstractWorkflow
{
        private static Log logger = LogFactory.getLog(HybridSyncPositiveTests.class);

        protected String testUser;
        private String testDomain;
        protected static long timeToWait;
        protected static int retryCount;

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
                retryCount = 5;
        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15478() throws Exception
        {
                String testName = getTestName();
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

                // Document Library page of the created Site is opened
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

                ShareUser.logout(drone);
        }

        /*
        * AONE-15478 Sync the same file that you synced earlier again to a different location
        */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15478() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";
                ContentDetails contentDetails = new ContentDetails(testName);
                ContentType contentType = ContentType.PLAINTEXT;

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

                // Any file is created/uploaded into the Document Library
                ShareUser.createContent(drone, contentDetails, contentType);
                String fileName = testName;

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
                // Choose any network --available site--document library and press OK button
                // ---- Expected results ----
                // Notification about file is synced successfully appears.

                destinationAndAssigneePage.selectNetwork(testDomain);
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents");
                destinationAndAssigneePage.clickSyncButton();
                Assert.assertTrue(documentLibraryPage.isSyncMessagePresent());

                // ---- Step 5 ----
                // ---- Step action ----
                // Choose Sync to Cloud again.
                // ---- Expected results ----
                // There is no 'Sync to Cloud' option

                Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isSyncToCloudLinkPresent());
                ShareUser.logout(drone);
        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15479() throws Exception
        {
                String testName = getTestName();
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

                // Document Library page of the created Site is opened
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

                ShareUser.logout(drone);
        }

        /*
        * AONE-15479 Update the content in on-premise
        */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15479() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";
                ContentDetails contentDetails = new ContentDetails(testName);
                ContentType contentType = ContentType.PLAINTEXT;
                String destination = "My Files";
                String fileNameEdited = testName + "-edited";

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

                // Any file is created/uploaded into the Document Library
                ShareUser.createContent(drone, contentDetails, contentType);
                String fileName = testName;

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
                // Choose any network --available site--document library and press OK button
                // ---- Expected results ----
                // Notification about file is synced successfully appears.

                destinationAndAssigneePage.selectNetwork(testDomain);
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents");
                destinationAndAssigneePage.clickSyncButton();
                Assert.assertTrue(documentLibraryPage.isSyncMessagePresent());

                // ---- Step 5 ----
                // ---- Step action ----
                // Move the file to different folder and update the content on Alfresco Enterprise.
                // ---- Expected results ----
                // The file is moved and edited.

                documentLibraryPage.getFileDirectoryInfo(fileName).selectMoveTo();
                CopyOrMoveContentPage contentPage = new CopyOrMoveContentPage(drone);
                contentPage.selectDestination(destination).selectOkButton();
                documentLibraryPage.getNav().selectMyFilesPage().render();
                assertTrue(documentLibraryPage.isFileVisible(fileName));
                EditDocumentPropertiesPage edit = documentLibraryPage.getFileDirectoryInfo(fileName).selectEditProperties();
                edit.setName(testName + "-edited");
                edit.clickSave();

                // ---- Step 6 ----
                // ---- Step action ----
                // Check the file content and version updated both in Cloud and Alfresco Enterprise.
                // ---- Expected results ----
                // The file content and version updated both in Cloud and Alfresco Enterprise.

                assertTrue(documentLibraryPage.isFileVisible(fileNameEdited));
                ShareUser.logout(drone);
                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                DocumentLibraryPage documentLibraryPageCL = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
                assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, fileNameEdited), "File is not visible");
                assertEquals(documentLibraryPageCL.getFileDirectoryInfo(fileNameEdited).getVersionInfo(), "1.1", "Version is not 1.1");
                ShareUser.logout(hybridDrone);

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15480() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";
                String cloudSiteNameMove = getSiteName(testName) + "-Move";

                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
                CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(drone);

                // Cloud user logins and create site.
                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.createSite(hybridDrone, cloudSiteNameMove, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(hybridDrone);

                // Login to User1, set up the cloud sync
                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

                // Document Library page of the created Site is opened
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

                ShareUser.logout(drone);
        }

        /*
        * AONE-15480 Update the content in Cloud.
        */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15480() throws Exception
        {

                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";
                String cloudSiteNameMove = getSiteName(testName) + "-Move";
                ContentDetails contentDetails = new ContentDetails(testName);
                ContentType contentType = ContentType.PLAINTEXT;
                String destination = cloudSiteNameMove;
                String fileNameEdited = testName + "-editedCL";

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

                // Any file is created/uploaded into the Document Library
                ShareUser.createContent(drone, contentDetails, contentType);
                String fileName = testName;

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
                // Choose any network --available site--document library and press OK button
                // ---- Expected results ----
                // Notification about file is synced successfully appears.

                destinationAndAssigneePage.selectNetwork(testDomain);
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents");
                destinationAndAssigneePage.clickSyncButton();
                Assert.assertTrue(documentLibraryPage.isSyncMessagePresent());
                ShareUser.logout(drone);

                // ---- Step 5 ----
                // ---- Step action ----
                // Move the file to different folder and update the content on Cloud.
                // ---- Expected results ----
                // The file is moved and edited.

                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                DocumentLibraryPage documentLibraryPageCL = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
                documentLibraryPageCL.getFileDirectoryInfo(fileName).selectMoveTo();
                CopyOrMoveContentPage contentPage = new CopyOrMoveContentPage(hybridDrone);
                contentPage.selectSite(destination).selectOkButton();
                ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteNameMove).render();
                EditDocumentPropertiesPage edit = documentLibraryPageCL.getFileDirectoryInfo(fileName).selectEditProperties();
                edit.setName(testName + "-editedCL");
                edit.clickSave();
                assertTrue(documentLibraryPageCL.isFileVisible(fileNameEdited));

                // ---- Step 6 ----
                // ---- Step action ----
                // Check the file content and version updated both in Cloud and Alfresco Enterprise.
                // ---- Expected results ----
                // The file content and version updated both in Cloud and Alfresco Enterprise.

                assertTrue(documentLibraryPageCL.isFileVisible(fileNameEdited));
                ShareUser.logout(hybridDrone);
                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                assertTrue(waitAndCheckIfVisible(drone, documentLibraryPage, fileNameEdited));
                assertEquals(documentLibraryPage.getFileDirectoryInfo(fileNameEdited).getVersionInfo(), "1.1");
                ShareUser.logout(drone);

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15482() throws Exception
        {
                String testName = getTestName();
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

                // Document Library page of the created Site is opened
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

                ShareUser.logout(drone);
        }

        /*
        * AONE-15481 Add a document and sync while the cloud network is down
        */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15482() throws Exception
        {
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";
                ContentDetails contentDetails = new ContentDetails(testName);
                ContentType contentType = ContentType.PLAINTEXT;

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

                // Any file is created/uploaded into the Document Library
                ShareUser.createContent(drone, contentDetails, contentType);
                String fileName = testName;

                // ---- Step 1 ----
                // ---- Step action ----
                // Add a document and sync it to Cloud, whilst syncing close browser.
                // ---- Expected results ----
                // File is synced to Cloud.

                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isSyncToCloudLinkPresent());
                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(fileName).selectSyncToCloud().render();
                Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + fileName + " to The Cloud"));
                destinationAndAssigneePage.clickSyncButton();
                drone.closeWindow();
                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                DocumentLibraryPage documentLibraryPageCL = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
                assertTrue(documentLibraryPageCL.isFileVisible(fileName));
                ShareUser.logout(hybridDrone);

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15484() throws Exception
        {

                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };
                String cloudUser2 = getUserNameForDomain(testName + "clUserInv", testDomain);
                String[] cloudUserInfo2 = new String[] { cloudUser2 };
                String opFolderName = getFolderName(testName) + "OP1";

                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";

                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
                CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
                CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo2);

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

                ShareUser.logout(drone);

                // Cloud user logins and create site.
                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);

                // One user should be invited to the site as Collaborator or Contributor

                CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, cloudUser, cloudUser2, getSiteShortname(cloudSiteName), "SiteContributor", "");
                ShareUser.logout(hybridDrone);

                // Login to User1, set up the cloud sync
                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser2, DEFAULT_PASSWORD);

                // Document Library page of the created Site is opened
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

                //Any folder is created
                ShareUserSitePage.createFolder(drone, opFolderName, "").render();

                ShareUser.logout(drone);
        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15485() throws Exception
        {

                String testName = getTestName();
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
                ShareUserMembers.inviteUserToSiteWithRole(drone,opUser1,opUser2,getSiteShortname(opSiteName), UserRole.COLLABORATOR);
//                ShareUser.logout(drone);

                // Login to User1, set up the cloud sync
                ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
                ShareUser.logout(drone);

                // Login to User2, set up the cloud sync
                ShareUser.login(drone, opUser2, DEFAULT_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

                ShareUser.logout(drone);
        }

        /*
        * AONE-15485 Remove the write access in on-premise.
        */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15485() throws Exception
        {

                String testName = getTestName();
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
                // Choose any network --available site--document library and press OK button
                // ---- Expected results ----
                // Notification about file is synced successfully appears.

                destinationAndAssigneePage.selectNetwork(testDomain);
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents");
                destinationAndAssigneePage.clickSyncButton();
                Assert.assertTrue(documentLibraryPage.isSyncMessagePresent());

                // ---- Step 5 ----
                // ---- Step action ----
                // Uncync the file by User1
                // ---- Expected results ----
                // The file is uncynced

                ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
                ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                documentLibraryPage.getFileDirectoryInfo(fileName).isUnSyncFromCloudLinkPresent();
                DocumentDetailsPage detailsPage = new DocumentDetailsPage(drone);
                detailsPage.selectUnSyncFromCloud();

                // ---- Step 6 ----
                // ---- Step action ----
                // Remove the write access to User2 to that file in on-premise (Manage permissions)
                // ---- Expected results ----
                // The write access is removed successfully.

                ShareUser.returnManagePermissionPage(drone, fileName);
                ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, opUser2, true, UserRole.CONSUMER, false);
                ShareUser.logout(drone);

                // ---- Step 7 ----
                // ---- Step action ----
                // Try to sync the file by User2 who now has not write access to the file
                // ---- Expected results ----
                // 'Sync to Cloud' action is absent fo the file

                ShareUser.login(drone, opUser2, DEFAULT_PASSWORD);
                ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isSyncToCloudLinkPresent());

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
                Assert.assertTrue(documentLibraryPage.isSyncMessagePresent());
                ShareUser.logout(drone);

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15486() throws Exception
        {
                String testName = getTestName();
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

                // Document Library page of the created Site is opened
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

                ShareUser.logout(drone);
        }

        /*
        * AONE-15486 Creation new folder.
        */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15486() throws Exception
        {

                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";
                String cloudFolderName = getFolderName(testName) + "-CL";
                ContentDetails contentDetails = new ContentDetails(testName);
                ContentType contentType = ContentType.PLAINTEXT;
                String CloudFolderNamePath = "//div[contains(@id,'default-cloud-folder-treeview')]//td/span[contains(text(),'" + cloudFolderName + "'" + ")]";

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

                // Any file is created/uploaded into the Document Library
                ShareUser.createContent(drone, contentDetails, contentType);
                String fileName = testName;

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
                drone.waitUntilElementPresent(By.xpath(CloudFolderNamePath), maxWaitTime);
                Assert.assertTrue(destinationAndAssigneePage.isFolderDisplayed(cloudFolderName));
                ShareUser.logout(drone);
        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15512() throws Exception
        {
                String testName = getTestName();
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
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

                // A file is created/uploaded into the Document Library of the site
                ShareUser.createContent(drone, contentDetails, contentType);

                ShareUser.logout(drone);
        }

        /*
        * AONE-15512 Same filename exists.
        */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15512() throws Exception
        {

                String testName = getTestName();
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
                Assert.assertTrue(documentLibraryPage.isSyncMessagePresent());
                ShareUser.logout(drone);

                // ---- Step 2 ----
                // ---- Step action ----
                // Sync this file to the same location.
                // First the file has to be unsynced and then synced again.
                // ---- Expected results ----
                // You cannot sync a file to a target location where same file name exists.

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                FileDirectoryInfo fileDirInfo = documentLibraryPage.getFileDirectoryInfo(fileName);
                SyncInfoPage syncInfoPage = fileDirInfo.clickOnViewCloudSyncInfo();
                Assert.assertTrue(syncInfoPage.isUnsyncButtonPresent());
                syncInfoPage.selectUnsyncRemoveContentFromCloud(false);
                documentLibraryPage = (DocumentLibraryPage) drone.getCurrentPage().render();
                documentLibraryPage.getFileDirectoryInfo(fileName).selectSyncToCloud().render();
                destinationAndAssigneePage.selectFolder("Documents");
                destinationAndAssigneePage.clickSyncButton();
                ShareUser.logout(drone);
                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                documentLibraryPage.getFileDirectoryInfo(fileName).selectRequestSync().render();
                waitForSync(fileName, opSiteName);
                documentLibraryPage.getFileDirectoryInfo(fileName).clickOnViewCloudSyncInfo();
                Assert.assertTrue(syncInfoPage.isFailedInfoDisplayed());
                ShareUser.logout(drone);

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15513() throws Exception
        {
                String testName = getTestName();
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
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

                // Two files with same name but different types are created/uploaded into the Document Library of the site
                ShareUser.createContent(drone, contentDetails, contentType);
                ShareUser.createContent(drone, contentDetails2, contentType2);

                ShareUser.logout(drone);
        }

        /*
        * AONE-15513 Same filename exists. Different file types.
        */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15513() throws Exception
        {

                String testName = getTestName();
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
                Assert.assertTrue(documentLibraryPage.isSyncMessagePresent());

                // ---- Step 2 ----
                // ---- Step action ----
                // Sync a file with the same filename and diffrent file type to the same location.
                // ---- Expected results ----
                // The file is synced to Cloud.
                //

                documentLibraryPage = (DocumentLibraryPage) drone.getCurrentPage().render();
                documentLibraryPage.getFileDirectoryInfo(fileName2).selectSyncToCloud().render();
                destinationAndAssigneePage.selectNetwork(testDomain);
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents");
                destinationAndAssigneePage.clickSyncButton();
                Assert.assertTrue(documentLibraryPage.isSyncMessagePresent());
                ShareUser.logout(drone);
                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                DocumentLibraryPage documentLibraryPageCL = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
                assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, fileName), "File is not visible");
                assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, fileName2), "File is not visible");
                ShareUser.logout(hybridDrone);

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15514() throws Exception
        {
                String testName = getTestName();
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
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

                // A folder is created into the Document Library of the site
                ShareUserSitePage.createFolder(drone, opFolderName, "").render();
                ShareUser.logout(drone);

                // A folder is created into the Document Library of the Cloud site
                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);
                ShareUserSitePage.createFolder(hybridDrone, clFolderName, "").render();
                ShareUser.logout(hybridDrone);
        }

        /*
        * AONE-15514 Sync folder with the same name as already exists in Cloud
         */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15514() throws Exception
        {
                String testName = getTestName();
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
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(opFolderName).isSyncToCloudLinkPresent());

                // ---- Step 2 ----
                // ---- Step action ----
                // Choose 'Sync to Cloud' option from More+ menu
                // ---- Expected results ----
                // Pop-up window to select target cloud location appears

                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(opFolderName).selectSyncToCloud().render();
                Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + opFolderName + " to The Cloud"));
                Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(testDomain));
                Assert.assertTrue(destinationAndAssigneePage.isSiteDisplayed(cloudSiteName));

                // ---- Step 3 ----
                // ---- Step action ----
                // Choose target location in the Cloud where folder exists and press OK button
                // ---- Expected results ----
                // Sync failed

                destinationAndAssigneePage.selectNetwork(testDomain);
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents");
                destinationAndAssigneePage.clickSyncButton();
                Assert.assertTrue(documentLibraryPage.isSyncMessagePresent());
                ShareUser.logout(drone);
                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                SyncInfoPage syncInfoPage = new SyncInfoPage(drone);
                documentLibraryPage.getFileDirectoryInfo(opFolderName).clickOnViewCloudSyncInfo();
                Assert.assertTrue(syncInfoPage.isFailedInfoDisplayed());
                ShareUser.logout(drone);

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15515() throws Exception
        {
                String testName = getTestName();
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

        /*
       * AONE-15515 Sync a folder with files and sub folders in it
        */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15515() throws Exception
        {
                String testName = getTestName();
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
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(opFolderName).isSyncToCloudLinkPresent());

                // ---- Step 2 ----
                // ---- Step action ----
                // Choose 'Sync to Cloud' option from More+ menu
                // ---- Expected results ----
                // Pop-up window to select target cloud location appears

                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(opFolderName).selectSyncToCloud().render();
                Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + opFolderName + " to The Cloud"));
                Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(testDomain));
                Assert.assertTrue(destinationAndAssigneePage.isSiteDisplayed(cloudSiteName));

                // ---- Step 3 ----
                // ---- Step action ----
                // Choose target location in the Cloud (network->site->document library) and press OK button
                // ---- Expected results ----
                // 'Sync created' notification appears

                destinationAndAssigneePage.selectNetwork(testDomain);
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents");
                destinationAndAssigneePage.clickSyncButton();
                Assert.assertTrue(documentLibraryPage.isSyncMessagePresent());
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
                assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, opSubFolderName), "File is not visible");
                assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, fileName), "File is not visible");
                ShareUser.logout(hybridDrone);

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15516() throws Exception
        {
                String testName = getTestName();
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

                //  Sync the above created folders and file to the Cloud
                ShareUser.selectMyDashBoard(drone);
                ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(opFolderName).selectSyncToCloud().render();
                Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + opFolderName + " to The Cloud"));
                destinationAndAssigneePage.selectNetwork(testDomain);
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents");
                destinationAndAssigneePage.clickSyncButton();
                Assert.assertTrue(documentLibraryPage.isSyncMessagePresent());

                ShareUser.logout(drone);

        }

        /*
        * AONE-15516 Rename synced a non-empty folder in cloud. Make some changes in on-premise
        */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15516() throws Exception
        {

                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String opFolderName = getFolderName(testName);
                String opSubFolderName = getFolderName("Sub-" + testName);
                String fileName = testName;
                String fileNameEditedCL = testName + "-editedCL";
                String fileNameEditedOP = testName + "-editedOP";

                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";
                DashBoardPage dashBoardPage = new DashBoardPage(drone);

                // ---- Step 1 ----
                // ---- Step action ----
                // Go to Cloud target location (network-site-document library) where folder was synced in preconditions
                // ---- Expected results ----
                // Document library page of site in Cloud is opened and synced folder is displayed on it

                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                DocumentLibraryPage documentLibraryPageCL = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
                Assert.assertTrue(documentLibraryPageCL.isFileVisible(opFolderName));

                // ---- Step 2 ----
                // ---- Step action ----
                // Set cursor to the synced folder and choose Edit properties option
                // ---- Expected results ----
                // Pop up window where it's possible to change some properties including name appears

                EditDocumentPropertiesPage editCL = documentLibraryPageCL.getFileDirectoryInfo(opFolderName).selectEditProperties().render();
                Assert.assertTrue(editCL.isEditPropertiesVisible());
                Assert.assertEquals(editCL.getName(), opFolderName);

                // ---- Step 3 ----
                // ---- Step action ----
                // Change name in name field and click on save button
                // ---- Expected results ----
                // Folder is renamed successfully in Cloud

                editCL.setName(testName + "-editedCL");
                editCL.clickSave();
                Assert.assertTrue(documentLibraryPageCL.isFileVisible(fileNameEditedCL));
                ShareUser.logout(hybridDrone);

                // ---- Step 4 ----
                // ---- Step action ----
                // Log in to Alfresco Share (on-premise)
                // ---- Expected results ----
                // User is logged in successfully

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                Assert.assertTrue(dashBoardPage.isLoggedIn());

                // ---- Step 5 ----
                // ---- Step action ----
                // Go to site-document library-synced folder
                // ---- Expected results ----
                // Synced folder is opened, sub folders and files are displayed on the page

                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);
                documentLibraryPage.selectFolder(opFolderName);
                assertTrue(waitAndCheckIfVisible(drone, documentLibraryPage, opSubFolderName), "File is not visible");
                assertTrue(waitAndCheckIfVisible(drone, documentLibraryPage, fileName), "File is not visible");

                // ---- Step 6 ----
                // ---- Step action ----
                // Change some files and sub folders
                // ---- Expected results ----
                // Changes are saved successfully in on-premise. Sync is not failed

                EditDocumentPropertiesPage editOP = documentLibraryPage.getFileDirectoryInfo(opSubFolderName).selectEditProperties();
                editOP.setName(testName + "-editedOP");
                editOP.clickSave();
                documentLibraryPage = (DocumentLibraryPage) drone.getCurrentPage().render();
                assertTrue(waitAndCheckIfVisible(drone, documentLibraryPage, fileNameEditedOP), "File is not visible");
                SyncInfoPage syncInfoPage = new SyncInfoPage(drone);
                documentLibraryPage.getFileDirectoryInfo(fileNameEditedOP).clickOnViewCloudSyncInfo();
                Assert.assertFalse(syncInfoPage.isFailedInfoDisplayed());
                ShareUser.logout(drone);

                // ---- Step 7 ----
                // ---- Step action ----
                // Go to Cloud location (network-site-document library-synced folder) again
                // ---- Expected results ----
                // Synced folder is opened successfully in Cloud. Changes made for files and sub folders in on-premise are applied successfully

                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
                assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, fileNameEditedCL), "File is not visible");
                documentLibraryPageCL.selectFolder(fileNameEditedCL);
                assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, fileNameEditedOP), "File is not visible");
                assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, fileName), "File is not visible");

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15517() throws Exception
        {
                String testName = getTestName();
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
                Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + opFolderName + " to The Cloud"));
                destinationAndAssigneePage.selectNetwork(testDomain);
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents");
                destinationAndAssigneePage.clickSyncButton();
                Assert.assertTrue(documentLibraryPage.isSyncMessagePresent());

                ShareUser.logout(drone);

        }

        /*
        * AONE-15517 Rename synced a non-empty folder in on-premise. Sync with changes from Cloud
        */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15517() throws Exception
        {

                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String opFolderName = getFolderName(testName);
                String opSubFolderName = getFolderName("Sub-" + testName);
                String fileName = testName;
                String fileNameEditedCL = testName + "-editedCL";
                String fileNameEditedOP = testName + "-editedOP";

                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";
                DashBoardPage dashBoardPage = new DashBoardPage(drone);

                // ---- Step 1 ----
                // ---- Step action ----
                // Log in to Alfresco Share (on-premise)
                // ---- Expected results ----
                // User is logged in successfully

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                Assert.assertTrue(dashBoardPage.isLoggedIn());

                // ---- Step 2 ----
                // ---- Step action ----
                // Go to site-document library
                // ---- Expected results ----
                // Document library page of site in on-premise is opened and synced folder is displayed on it

                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);
                Assert.assertTrue(documentLibraryPage.isFileVisible(opFolderName));

                // ---- Step 3 ----
                // ---- Step action ----
                // Set cursor to the synced folder and choose Edit properties option
                // ---- Expected results ----
                // Pop up window where it's possible to change some properties including name appears

                EditDocumentPropertiesPage editOP = documentLibraryPage.getFileDirectoryInfo(opFolderName).selectEditProperties().render();
                Assert.assertTrue(editOP.isEditPropertiesVisible());
                Assert.assertEquals(editOP.getName(), opFolderName);

                // ---- Step 4 ----
                // ---- Step action ----
                // Change name in name field and click on save button
                // ---- Expected results ----
                // Folder is renamed successfully in Cloud

                editOP.setName(testName + "-editedOP");
                editOP.clickSave();
                documentLibraryPage = (DocumentLibraryPage) drone.getCurrentPage().render();
                Assert.assertTrue(documentLibraryPage.isFileVisible(fileNameEditedOP));
                ShareUser.logout(drone);

                // ---- Step 5 ----
                // ---- Step action ----
                // Go to Cloud location (network-site-document library-synced folder)
                // ---- Expected results ----
                // Synced folder is opened successfully in Cloud, ub folders and files are displayed on the page

                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                DocumentLibraryPage documentLibraryPageCL = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
                assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, fileNameEditedOP), "File is not visible");
                documentLibraryPageCL.selectFolder(fileNameEditedOP);
                assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, opSubFolderName), "File is not visible");
                assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, fileName), "File is not visible");

                // ---- Step 6 ----
                // ---- Step action ----
                // Make changes to some files and sub folders and click on save button
                // ---- Expected results ----
                // Changes are saved successfully in cloud. Sync is passed

                EditDocumentPropertiesPage editCL = documentLibraryPageCL.getFileDirectoryInfo(fileName).selectEditProperties().render();
                editCL.setName(testName + "-editedCL");
                editCL.clickSave();
                assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, fileNameEditedCL), "File is not visible");
                ShareUser.logout(hybridDrone);

                // ---- Step 7 ----
                // ---- Step action ----
                // Go to location location site-document library-synced folder in on-premise again
                // ---- Expected results ----
                // Synced folder is opened successfully in Cloud. Changes made for files and sub folders in cloud are applied successfully

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                ShareUser.openSitesDocumentLibrary(drone, opSiteName);
                assertTrue(waitAndCheckIfVisible(drone, documentLibraryPage, fileNameEditedOP), "File is not visible");
                documentLibraryPage.selectFolder(fileNameEditedOP);
                SyncInfoPage syncInfoPageOP = documentLibraryPage.getFileDirectoryInfo(fileNameEditedCL).clickOnViewCloudSyncInfo();
                Assert.assertFalse(syncInfoPageOP.isFailedInfoDisplayed());
                syncInfoPageOP.clickOnCloseButton();
                assertTrue(waitAndCheckIfVisible(drone, documentLibraryPage, opSubFolderName), "File is not visible");
                assertTrue(waitAndCheckIfVisible(drone, documentLibraryPage, fileNameEditedCL), "File is not visible");

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15518() throws Exception
        {
                String testName = getTestName();
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

                //  Sync the above created folders and file to the Cloud
                ShareUser.selectMyDashBoard(drone);
                ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(opFolderName).selectSyncToCloud().render();
                Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + opFolderName + " to The Cloud"));
                destinationAndAssigneePage.selectNetwork(testDomain);
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents");
                destinationAndAssigneePage.clickSyncButton();
                Assert.assertTrue(documentLibraryPage.isSyncMessagePresent());

                ShareUser.logout(drone);
        }

        /*
        * AONE-15518 Add file/folder to non-empty synced folder in on-premise
        */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15518() throws Exception
        {
                String testName = getTestName();
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
                Assert.assertTrue(dashBoardPage.isLoggedIn());

                // ---- Step 2 ----
                // ---- Step action ----
                // Go to site-document library-synced folder
                // ---- Expected results ----
                // Synced folder is opened, sub folders and files are displayed on the page

                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);
                Assert.assertTrue(documentLibraryPage.isFileVisible(opFolderName));
                documentLibraryPage.selectFolder(opFolderName);
                Assert.assertTrue(documentLibraryPage.isFileVisible(opSubFolderName));
                Assert.assertTrue(documentLibraryPage.isFileVisible(fileName));

                // ---- Step 3 ----
                // ---- Step action ----
                // Add any file/folder to opened folder
                // ---- Expected results ----
                // A file/folder is added successfully and displayed in synced folder in on-premise

                ShareUser.createFolderInFolder(drone, opFolderNameAdd, "", opFolderName);
                Assert.assertTrue(documentLibraryPage.isFileVisible(opFolderNameAdd));
                ShareUser.logout(drone);

                // ---- Step 4 ----
                // ---- Step action ----
                // Go to Cloud location (network-site-document library-synced folder)
                // ---- Expected results ----
                // Synced folder is opened successfully in Cloud. Added in on-premise file/folder is created in cloud as well

                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                DocumentLibraryPage documentLibraryPageCL = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
                assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, opFolderName), "File is not visible");
                documentLibraryPageCL.selectFolder(opFolderName);
                assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, opSubFolderName), "File is not visible");
                assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, fileName), "File is not visible");
                assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, opFolderNameAdd), "File is not visible");
                ShareUser.logout(hybridDrone);

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15519() throws Exception
        {
                String testName = getTestName();
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
                Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + opFolderName + " to The Cloud"));
                destinationAndAssigneePage.selectNetwork(testDomain);
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents");
                destinationAndAssigneePage.clickSyncButton();
                Assert.assertTrue(documentLibraryPage.isSyncMessagePresent());

                ShareUser.logout(drone);
        }

        /*
        * AONE-15519 Add file/folder to non-empty synced folder in Cloud
        */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15519() throws Exception
        {

                String testName = getTestName();
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
                Assert.assertTrue(documentLibraryPageCL.isFileVisible(opFolderName));
                documentLibraryPageCL.selectFolder(opFolderName);
                Assert.assertTrue(documentLibraryPageCL.isFileVisible(opSubFolderName));
                Assert.assertTrue(documentLibraryPageCL.isFileVisible(fileName));

                // ---- Step 2 ----
                // ---- Step action ----
                // Add any file/folder to opened folder
                // ---- Expected results ----
                // A file/folder is added successfully and displayed in synced folder in Cloud

                ShareUser.createFolderInFolder(hybridDrone, clFolderNameAdd, "", opFolderName);
                Assert.assertTrue(documentLibraryPageCL.isFileVisible(clFolderNameAdd));
                ShareUser.logout(hybridDrone);

                // ---- Step 3 ----
                // ---- Step action ----
                // Log in to Alfresco Share (on-premise)
                // ---- Expected results ----
                // User is logged in successfully

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                Assert.assertTrue(dashBoardPage.isLoggedIn());

                // ---- Step 4 ----
                // ---- Step action ----
                // Go to site-document library-synced folder
                // ---- Expected results ----
                // Synced folder is opened successfully in on-premise. Added in Cloud file/folder is created in on-premise as well

                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);
                assertTrue(waitAndCheckIfVisible(drone, documentLibraryPage, opFolderName), "File is not visible");
                documentLibraryPage.selectFolder(opFolderName);
                assertTrue(waitAndCheckIfVisible(drone, documentLibraryPage, opSubFolderName), "File is not visible");
                assertTrue(waitAndCheckIfVisible(drone, documentLibraryPage, clFolderNameAdd), "File is not visible");
                assertTrue(waitAndCheckIfVisible(drone, documentLibraryPage, fileName), "File is not visible");

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15520() throws Exception
        {
                String testName = getTestName() + "37";
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

                //  Sync the above created folders and file to the Cloud
                ShareUser.selectMyDashBoard(drone);
                ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(opFolderName).selectSyncToCloud().render();
                Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + opFolderName + " to The Cloud"));
                destinationAndAssigneePage.selectNetwork(testDomain);
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents");
                destinationAndAssigneePage.clickSyncButton();
                Assert.assertTrue(documentLibraryPage.isSyncMessagePresent());

                ShareUser.logout(drone);
        }

        /*
        * AONE-15520 Update child file/folder in parent synced folder in on-premise
        */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15520() throws Exception
        {
                String testName = getTestName() + "37";
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
                Assert.assertTrue(dashBoardPage.isLoggedIn());

                // ---- Step 2 ----
                // ---- Step action ----
                // Go to site created in preconditions (step4)-document library
                // ---- Expected results ----
                // Document library page is opened successfully, all information including synced in preconditions (step6) folder is displayed correctly

                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);
                Assert.assertTrue(documentLibraryPage.isFileVisible(opFolderName));

                // ---- Step 3 ----
                // ---- Step action ----
                // Open synced folder
                // ---- Expected results ----
                // Folder is opened successfully, files and sub folders are displayed on the page

                documentLibraryPage.selectFolder(opFolderName);
                Assert.assertTrue(documentLibraryPage.isFileVisible(opSubFolderName));
                Assert.assertTrue(documentLibraryPage.isFileVisible(fileName));

                // ---- Step 4 ----
                // ---- Step action ----
                // Update at least one of the files or sub folders in synced folder in on-premise
                // ---- Expected results ----
                // File/sub folders is updated successfully

                EditDocumentPropertiesPage editOP = documentLibraryPage.getFileDirectoryInfo(fileName).selectEditProperties().render();
                Assert.assertTrue(editOP.isEditPropertiesVisible());
                Assert.assertEquals(editOP.getName(), fileName);
                editOP.setName(testName + "-editedOP");
                editOP.clickSave();
                documentLibraryPage = (DocumentLibraryPage) drone.getCurrentPage().render();
                Assert.assertTrue(documentLibraryPage.isFileVisible(fileNameEditedOP));
                ShareUser.logout(drone);

                // ---- Step 5 ----
                // ---- Step action ----
                // Go to Cloud target location where folder was synced in preconditions and open it to verify changes made to the file/sub folder are applied
                // ---- Expected results ----
                // Updates synced to cloud and changed in previous step file/sub folder is displayed correct

                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                DocumentLibraryPage documentLibraryPageCL = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
                assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, opFolderName), "File is not visible");
                documentLibraryPageCL.selectFolder(opFolderName);
                assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, opSubFolderName), "File is not visible");
                assertTrue(waitAndCheckIfVisible(hybridDrone, documentLibraryPageCL, fileNameEditedOP), "File is not visible");
                ShareUser.logout(hybridDrone);

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15521() throws Exception
        {
                String testName = getTestName();
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

                //  Sync the above created folders and file to the Cloud
                ShareUser.selectMyDashBoard(drone);
                ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(opFolderName).selectSyncToCloud().render();
                Assert.assertTrue(destinationAndAssigneePage.getSyncToCloudTitle().contains("Sync " + opFolderName + " to The Cloud"));
                destinationAndAssigneePage.selectNetwork(testDomain);
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents");
                destinationAndAssigneePage.clickSyncButton();
                Assert.assertTrue(documentLibraryPage.isSyncMessagePresent());

                ShareUser.logout(drone);
        }

        /*
        * AONE-15521 Update child file/folder in parent synced folder in cloud
        */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15521() throws Exception
        {

                String testName = getTestName();
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
                // Document library page of site created in cloud is opened successfully, all information including synced in preconditions (step6) folder is displayed correctly

                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                DocumentLibraryPage documentLibraryPageCL = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
                Assert.assertTrue(documentLibraryPageCL.isFileVisible(opFolderName));

                // ---- Step 2 ----
                // ---- Step action ----
                // Open synced folder
                // ---- Expected results ----
                // Folder is opened successfully, files and sub folders are displayed on the page

                documentLibraryPageCL.selectFolder(opFolderName);
                Assert.assertTrue(documentLibraryPageCL.isFileVisible(opSubFolderName));
                Assert.assertTrue(documentLibraryPageCL.isFileVisible(fileName));

                // ---- Step 3 ----
                // ---- Step action ----
                // Update at least one of the files or sub folders in synced folder in cloud
                // ---- Expected results ----
                // File/sub folder is updated successfully

                EditDocumentPropertiesPage editCL = documentLibraryPageCL.getFileDirectoryInfo(opSubFolderName).selectEditProperties().render();
                Assert.assertTrue(editCL.isEditPropertiesVisible());
                Assert.assertEquals(editCL.getName(), opSubFolderName);
                editCL.setName(testName + "-editedCL");
                editCL.clickSave();
                Assert.assertTrue(documentLibraryPageCL.isFileVisible(folderNameEditedCL));
                ShareUser.logout(hybridDrone);


                // ---- Step 4 ----
                // ---- Step action ----
                // Log in to Alfresco Share (on-premise)
                // ---- Expected results ----
                // User is logged in successfully

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                Assert.assertTrue(dashBoardPage.isLoggedIn());

                // ---- Step 5 ----
                // ---- Step action ----
                // Go to site created in preconditions (step4)-document library-synced folder and verify changes made to the file/sub folder in step3 are applied
                // ---- Expected results ----
                // Synced folder is opened. Updates synced to on-premise and changed in step3 file/sub folder is displayed correct

                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);
                Assert.assertTrue(documentLibraryPage.isFileVisible(opFolderName));
                documentLibraryPage.selectFolder(opFolderName);
                Assert.assertTrue(documentLibraryPage.isFileVisible(fileName));
                assertTrue(waitAndCheckIfVisible(drone, documentLibraryPage, folderNameEditedCL), "File is not visible");

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
