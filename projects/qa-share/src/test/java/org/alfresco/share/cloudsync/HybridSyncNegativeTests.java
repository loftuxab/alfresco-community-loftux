package org.alfresco.share.cloudsync;

import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.document.*;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

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
        public void dataPrep_15478() throws Exception
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
                //

                Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isSyncToCloudLinkPresent());
        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_15479() throws Exception
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
                assertTrue(documentLibraryPage.isFileVisible(testName));
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
                assertTrue(documentLibraryPageCL.isFileVisible(fileNameEdited));
                assertEquals(documentLibraryPageCL.getFileDirectoryInfo(fileNameEdited).getVersionInfo(),"1.0");

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_15480() throws Exception
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
                EditDocumentPropertiesPage edit = documentLibraryPageCL.getFileDirectoryInfo(fileName).selectEditProperties();
                edit.setName(testName + "-editedCL");
                edit.clickSave();
                documentLibraryPageCL.getFileDirectoryInfo(fileNameEdited).selectMoveTo();
                CopyOrMoveContentPage contentPage = new CopyOrMoveContentPage(hybridDrone);
                contentPage.selectSite(destination).selectOkButton();
                ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteNameMove).render();
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
                assertTrue(documentLibraryPage.isFileVisible(fileNameEdited));
                assertEquals(documentLibraryPage.getFileDirectoryInfo(fileNameEdited).getVersionInfo(),"1.1");

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_15482() throws Exception
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

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_15484() throws Exception
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

        /*
        * AONE-15484 Remove the write access to Cloud.
        */
        @Test(groups = "Hybrid", enabled = true)
        public void AONE_15484() throws Exception
        {

                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String cloudUser2 = getUserNameForDomain(testName + "clUserInv", testDomain);
                String opSiteName = getSiteName(testName) + "-OP";
                String cloudSiteName = getSiteName(testName) + "-CL";
                String opFolderName = getFolderName(testName) + "OP1";
                ContentDetails contentDetails = new ContentDetails(testName);
                ContentType contentType = ContentType.PLAINTEXT;


                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

                // Any file is created/uploaded into the folder
                documentLibraryPage.selectFolder(opFolderName);
//                ShareUserSitePage.navigateToFolder(drone,opFolderName);
                ShareUser.createContentInCurrentFolder(drone, contentDetails, contentType, documentLibraryPage);
                String fileName = testName;

                // ---- Step 1 ----
                // ---- Step action ----
                // Set cursor to the document and expand More+ menu
                // ---- Expected results ----
                // List of actions is appeared for document and 'sync to cloud' option available
                documentLibraryPage.selectFolder(opFolderName);
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
                // Choose any network --available site--document library--folder and press OK button
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
                // Remove the write access to that folder in the Cloud (Manage permissions)
                // ---- Expected results ----
                // The write access is removed successfully.

                // TODO 5: Add your code here for step 5.

                // ---- Step 6 ----
                // ---- Step action ----
                // The user still have write access in Alfresco Enterprise. Try to sync the other document to that folder.
                // ---- Expected results ----
                // Sync fails (wait a little bit, status should change from pending to failed).

                // TODO 6: Add your code here for step 6.

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_15485() throws Exception
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
                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo2);
                CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo);

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
                ShareUser.logout(drone);

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
                ShareUser.openSitesDocumentLibrary(drone, opSiteName).render(); //Share-15485
                documentLibraryPage.getFileDirectoryInfo(fileName).isUnSyncFromCloudLinkPresent();
                DocumentDetailsPage detailsPage = new DocumentDetailsPage(drone);
                detailsPage.selectUnSyncFromCloud();
//                Assert.assertTrue(detailsPage.isFileSyncSetUp());


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

        }


        @Test(groups = "DataPrepHybrid")
        public void dataPrep_15486() throws Exception
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
                String CloudFolderNamePath = "//div[contains(@id,'default-cloud-folder-treeview')]//td/span[contains(text(),'"+cloudFolderName+"'"+")]";


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
                drone.waitUntilElementPresent(By.xpath(CloudFolderNamePath),maxWaitTime);
                Assert.assertTrue(destinationAndAssigneePage.isFolderDisplayed(cloudFolderName));
        }









}
