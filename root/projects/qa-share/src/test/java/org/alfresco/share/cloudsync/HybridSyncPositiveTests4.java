package org.alfresco.share.cloudsync;

import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.CreateNewFolderInCloudPage;
import org.alfresco.po.share.site.document.*;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
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
public class HybridSyncPositiveTests4 extends AbstractWorkflow
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
        public void dataPrep_AONE_15461() throws Exception
        {
                String uniqueName = "_Z7";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFileName = getFolderName(testName) + "OP";
                String[] fileInfo1 = new String[] { opFileName, DOCLIB };
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

                // Login to User1, set up the cloud sync
                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

                // create a file
                DocumentLibraryPage docLib = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUser.uploadFileInFolder(drone, fileInfo1).render();
                ShareUser.logout(drone);
        }

        /*
        * AONE-15461 Creating a new folder in a cloud target selection window under documents folder
        */
        @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
        public void AONE_15461() throws Exception
        {
                String uniqueName = "_Z7";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);

                String opFileName = getFolderName(testName) + "OP";
                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

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

                destinationAndAssigneePage.selectSite(cloudSiteName);
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
                ShareUser.logout(drone);
        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15462() throws Exception
        {
                String uniqueName = "_Z7";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFileName = getFileName(testName) + "-FILE-OP";
                String[] fileInfo1 = new String[] { opFileName, DOCLIB };
                String opFolderName = getFolderName(testName) + "-FOLDER-OP";
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

                // Login to User1, set up the cloud sync
                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

                // create a file
                DocumentLibraryPage docLib = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUser.uploadFileInFolder(drone, fileInfo1).render();

                // create folder in order to test the ability to create a subfolder
                docLib = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                FileDirectoryInfo fileDirectoryInfo = docLib.getFileDirectoryInfo(opFileName);
                DestinationAndAssigneePage destinationAndAssigneePage = fileDirectoryInfo.selectSyncToCloud().render();
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents", opFolderName);
                CreateNewFolderInCloudPage newFolderPopup = destinationAndAssigneePage.selectCreateNewFolder().render(10000);
                newFolderPopup.createNewFolder(opFolderName, opFolderName, opFolderName).render();

                ShareUser.logout(drone);
        }

        /*
        * AONE-15462 Creating a new folder in a cloud target selection window under one of the subfolders
        */
        @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
        public void AONE_15462() throws Exception
        {
                String uniqueName = "_Z7";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);

                String opFileName = getFileName(testName) + "-FILE-OP";
                String opFolderName = getFolderName(testName) + "-FOLDER-OP";
                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

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

                destinationAndAssigneePage.selectSite(cloudSiteName);
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
                String uniqueName = "_Z7";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFileName = getFileName(testName) + "OP";
                String[] fileInfo1 = new String[] { opFileName, DOCLIB };
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

                // Login to User1, set up the cloud sync
                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

                // create a file
                DocumentLibraryPage docLib = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUser.uploadFileInFolder(drone, fileInfo1).render();
                ShareUser.logout(drone);
        }

        /*
        * AONE-15463 Creating a new folder in cloud and sync  a file to it
        */
        @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
        public void AONE_15463() throws Exception
        {

                String uniqueName = "_Z7";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);

                String opFileName = getFileName(testName) + "OP";
                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

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

                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents");
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
                String uniqueName = "_Z7";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFolderName = getFolderName(testName) + "-FOLDER-OP";
                String opFileName1 = getFileName(testName) + "-FILE-OP_1";
                String[] fileInfo1 = new String[] { opFileName1, opFolderName };
                String opFileName2 = getFileName(testName) + "-FILE-OP_2";
                String[] fileInfo2 = new String[] { opFileName2, opFolderName };

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

                // Login to User1, set up the cloud sync
                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

                // create 1 folder and 2 files in it
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUserSitePage.createFolder(drone, opFolderName, "").render();
                documentLibraryPage = documentLibraryPage.selectFolder(opFolderName).render();

                ShareUser.uploadFileInFolder(drone, fileInfo1).render();
                ShareUser.uploadFileInFolder(drone, fileInfo2).render();

                ShareUser.logout(drone);
        }

        /*
        * AONE-15464 Creating a new folder in a cloud target selection window and sync a folder with some files in it
        */
        @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
        public void AONE_15464() throws Exception
        {
                String uniqueName = "_Z7";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);

                String opFolderName = getFolderName(testName) + "-FOLDER-OP";
                String opFileName1 = getFileName(testName) + "-FILE-OP_1";
                String[] fileInfo1 = new String[] { opFileName1, opFolderName };
                String opFileName2 = getFileName(testName) + "-FILE-OP_2";
                String[] fileInfo2 = new String[] { opFileName2, opFolderName };

                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

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

                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder(opFolderName);
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

                documentLibraryPage.getFileDirectoryInfo(opFolderName).selectRequestSync().render();
                checkIfContentIsSynced(drone, opFolderName);
                waitForSync(drone, opFolderName, opSiteName);

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
                String uniqueName = "_Z7f";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFolderName = getFolderName(testName) + "-FOLDER-OP";
                String opSubFolderName = getFolderName(testName) + "-SUBFOLDER-OP";
                String opFileName1 = getFileName(testName) + "-FILE-OP_1";
                String[] fileInfo1 = new String[] { opFileName1, opFolderName };
                String opFileName2 = getFileName(testName) + "-FILE-OP_2";
                String[] fileInfo2 = new String[] { opFileName2, opFolderName };


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

                // Login to User1, set up the cloud sync
                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

                // create 1 folder and 2 files in it
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUserSitePage.createFolder(drone, opFolderName, "").render();
                documentLibraryPage.selectFolder(opFolderName);
                ShareUser.uploadFileInFolder(drone, fileInfo1).render();
                ShareUserSitePage.createFolder(drone, opSubFolderName, "").render();
                //documentLibraryPage.selectFolder(opFolderName);
                documentLibraryPage.selectFolder(opSubFolderName);
                ShareUser.uploadFileInFolder(drone, fileInfo2).render();

                ShareUser.logout(drone);
        }

        /*
        * AONE-15465 Sync a folder with some files and sub folders in it
        */
        @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
        public void AONE_15465() throws Exception
        {

                String uniqueName = "_Z7f";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);

                String opFileName1 = getFileName(testName) + "-FILE-OP_1";
                String opFileName2 = getFileName(testName) + "-FILE-OP_2";
                String opFolderName = getFolderName(testName) + "-FOLDER-OP";
                String opSubFolderName = getFolderName(testName) + "-SUBFOLDER-OP";

                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

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

                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents");
                destinationAndAssigneePage.clickSyncButton();
                Assert.assertTrue(documentLibraryPage.isSyncMessagePresent(), "Sync message is not present.");

                waitForSync(drone, opFolderName, opSiteName);

                ShareUser.logout(drone);

                // ---- Step 4 ----
                // ---- Step action ----
                // Go to Cloud location (network-site-document library-folder) set in previous step;
                // ---- Expected results ----
                // Folder with all files and subfolders for which sync action was applied is displayed.

                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
                documentLibraryPage = documentLibraryPage.selectFolder(opFolderName).render();
                Assert.assertTrue(documentLibraryPage.isItemVisble(opFileName1), "File is not visible.");
                Assert.assertTrue(documentLibraryPage.isItemVisble(opSubFolderName), "Folder is not visible.");
                Assert.assertTrue(documentLibraryPage.isItemVisble(opFileName2), "File is not visible.");

                ShareUser.logout(hybridDrone);
        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15466() throws Exception
        {
                String uniqueName = "_Z7a";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFolderName = getFolderName(testName) + "-FOLDER-OP";
                String opSubFolderName = getFolderName(testName) + "-SUBFOLDER-OP";
                String opFileName1 = getFileName(testName) + "-FILE-OP_1";
                String[] fileInfo1 = new String[] { opFileName1, opFolderName };
                String opFileName2 = getFileName(testName) + "-FILE-OP_2";
                String[] fileInfo2 = new String[] { opFileName2, opFolderName };

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

                // Login to User1, set up the cloud sync
                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

                // create 1 folder and 2 files in it
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();


                ShareUserSitePage.createFolder(drone, opFolderName, "").render();
                documentLibraryPage.selectFolder(opFolderName);
                ShareUser.uploadFileInFolder(drone, fileInfo1).render();
                ShareUserSitePage.createFolder(drone, opSubFolderName, "").render();
                documentLibraryPage.selectFolder(opSubFolderName);
                ShareUser.uploadFileInFolder(drone, fileInfo2).render();

                ShareUser.logout(drone);
        }

        /*
        * AONE-15466 Sync a folder with some files and sub folders in it. 'Not to sync subfolders' option
        */
        @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
        public void AONE_15466() throws Exception
        {
                String uniqueName = "_Z7a";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);

                String opFileName1 = getFileName(testName) + "-FILE-OP_1";
                String opFileName2 = getFileName(testName) + "-FILE-OP_2";
                String opFolderName = getFolderName(testName) + "-FOLDER-OP";
                String opSubFolderName = getFolderName(testName) + "-SUBFOLDER-OP";

                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

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

                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents");
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
                String uniqueName = "_Z7c";
                String testName = getTestName();
                String opUser1 = getUserNameForDomain(testName + "opUser-1", testDomain);
                String[] userInfo1 = new String[] { opUser1 };
                String opFileName = getFileName(testName);
                String[] fileInfo1 = new String[] { opFileName, DOCLIB };

                String opUser2 = getUserNameForDomain(testName + "opUser-2", testDomain);
                String[] userInfo2 = new String[] { opUser2 };

                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opSiteName = getSiteName(testName) + "-OP1" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

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

                ShareUser.uploadFileInFolder(drone, fileInfo1);

                DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(opFileName).selectSyncToCloud().render();
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.clickSyncButton();
                waitForSync(drone, opFileName, opSiteName);

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

                String uniqueName = "_Z7c";
                String testName = getTestName();
                String opUser1 = getUserNameForDomain(testName + "opUser-1", testDomain);
                String opUser2 = getUserNameForDomain(testName + "opUser-2", testDomain);
                String opFileName = getFileName(testName);
                String opSiteName = getSiteName(testName) + "-OP1" + uniqueName;

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
                String uniqueName = "_Z7a";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFileName = getFileName(testName) + "-FILE-OP";
                String[] fileInfo1 = new String[] { opFileName, DOCLIB };
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

                // Login to User1, set up the cloud sync
                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

                // create a file
                DocumentLibraryPage docLib = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUser.uploadFileInFolder(drone, fileInfo1).render();
                ShareUser.logout(drone);
        }

        /*
        * AONE-15468 Checking the status 'Sync Pending' by owner
        */
        @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
        public void AONE_15468() throws Exception
        {
                String uniqueName = "_Z7a";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String opFileName = getFileName(testName) + "-FILE-OP";
                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

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

                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents");
                destinationAndAssigneePage.clickSyncButton();
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

                ShareUser.logout(drone);

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15469() throws Exception
        {
                String uniqueName = "_Z7e";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFileName = getFileName(testName) + "-FILE-OP";
                String[] fileInfo1 = new String[] { opFileName, DOCLIB };
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

                // Login to User1, set up the cloud sync
                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

                // create a file
                DocumentLibraryPage docLib = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUser.uploadFileInFolder(drone, fileInfo1).render();
                ShareUser.logout(drone);
        }

        /*
        * AONE-15469 Checking the status 'Sync Successful' by owner
        */
        @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
        public void AONE_15469() throws Exception
        {

                String uniqueName = "_Z7e";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String opFileName = getFileName(testName) + "-FILE-OP";
                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

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

                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.clickSyncButton();
                Assert.assertTrue(documentLibraryPage.isSyncMessagePresent(), "Sync message is not present.");

                // ---- Step 4 ----
                // ---- Step action ----
                // After 1 min of sync go to Document Library and click on the synced file to open its details page;
                // ---- Expected results ----
                // Details page for synced document is successfully opened and information is displayed correctly;

                checkIfContentIsSynced(drone, opFileName);
                //waitForSync(drone, opFileName, opSiteName);
                //wait(120); // Asked by the scenario. Do not change!
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
                String uniqueName = "_Z7b";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFileName = getFileName(testName) + "-FILE-OP";
                String[] fileInfo1 = new String[] { opFileName, DOCLIB };
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

                // Login to User1, set up the cloud sync
                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

                // create a file
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUser.uploadFileInFolder(drone, fileInfo1).render();
                FileDirectoryInfo fileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(opFileName);
                DestinationAndAssigneePage destinationAndAssigneePage = fileDirectoryInfo.selectSyncToCloud().render();
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents");
                destinationAndAssigneePage.clickSyncButton();
                ShareUser.logout(drone);
        }

        /*
        * AONE-15470 Checking the sync status. Last sync date, time and cloud location
        */
        @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
        public void AONE_15470() throws Exception
        {
                String uniqueName = "_Z7b";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String opFileName = getFileName(testName) + "-FILE-OP";
                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;

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
                String uniqueName = "_Z7a";
                String testName = getTestName();
                String opUser1 = getUserNameForDomain(testName + "opUser-1", testDomain);
                String[] userInfo1 = new String[] { opUser1 };
                String opFileName = getFileName(testName);
                String[] fileInfo1 = new String[] { opFileName, DOCLIB };
                String opUser2 = getUserNameForDomain(testName + "opUser-2", testDomain);
                String[] userInfo2 = new String[] { opUser2 };

                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opSiteName = getSiteName(testName) + "-OP1" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

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
                ShareUser.uploadFileInFolder(drone, fileInfo1).render();
                ShareUser.logout(drone);

                /*
                9. User1 who owns the site sync the file with cloud.
                 */
                ShareUser.login(drone, opUser1);
                docLib = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                DestinationAndAssigneePage destinationAndAssigneePage = docLib.getFileDirectoryInfo(opFileName).selectSyncToCloud().render();
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents");
                destinationAndAssigneePage.clickSyncButton();
                waitForSync(drone, opFileName, opSiteName);
                ShareUser.logout(drone);

        }

        /*
         * AONE-15471: Unsync the file from cloud by owner. Select to remove the file
         */
        @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
        public void AONE_15471() throws Exception
        {
                String uniqueName = "_Z7a";
                String testName = getTestName();
                String opUser2 = getUserNameForDomain(testName + "opUser-2", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String opSiteName = getSiteName(testName) + "-OP1" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;
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
                String uniqueName = "_Z7f";
                String testName = getTestName();
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFileName = getFileName(testName) + "-FILE-OP";
                String[] fileInfo1 = new String[] { opFileName, DOCLIB };
                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

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
                ShareUser.uploadFileInFolder(drone, fileInfo1).render();
                FileDirectoryInfo fileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(opFileName);
                DestinationAndAssigneePage destinationAndAssigneePage = fileDirectoryInfo.selectSyncToCloud().render();
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents");
                destinationAndAssigneePage.clickSyncButton();
                waitForSync(drone, opFileName, opSiteName);
                ShareUser.logout(drone);
        }

        /*
        * AONE-15472 Unsync the file from cloud by Admin/site manager. Select to remove the file
        */
        @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
        public void AONE_15472() throws Exception
        {
                String uniqueName = "_Z7f";
                String testName = getTestName();
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);

                String opFileName = getFileName(testName) + "-FILE-OP";
                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

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
                documentLibraryPage = documentLibraryPage.getFileDirectoryInfo(opFileName).selectUnSyncAndRemoveContentFromCloud(false);
                ShareUser.logout(drone);

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
                        documentLibraryPage = documentLibraryPage.getSiteNav().selectSiteDocumentLibrary().render();
                        maxRefreshes = maxRefreshes - 1;
                }

                Assert.assertFalse(documentLibraryPage.isFileVisible(opFileName), "File is still visible on the cloud.");

                ShareUser.logout(hybridDrone);
        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15473() throws Exception
        {
                String uniqueName = "_Z7a";
                String testName = getTestName();
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFileName = getFileName(testName) + "-FILE-OP";
                String[] fileInfo1 = new String[] { opFileName, DOCLIB };
                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

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
                ShareUser.uploadFileInFolder(drone, fileInfo1).render();
                FileDirectoryInfo fileDirectoryInfo = docLib.getFileDirectoryInfo(opFileName);
                DestinationAndAssigneePage destinationAndAssigneePage = fileDirectoryInfo.selectSyncToCloud().render();
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents");
                destinationAndAssigneePage.clickSyncButton();
                waitForSync(drone, opFileName, opSiteName);
                ShareUser.logout(drone);
        }

        /*
        * AONE-15473 Unsync the file from cloud by owner/site manager. Select 'do not remove the file'
        */
        @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
        public void AONE_15473() throws Exception
        {
                String uniqueName = "_Z7a";
                String testName = getTestName();
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFileName = getFileName(testName) + "-FILE-OP";
                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

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
                String uniqueName = "_Z7a";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFileName = getFileName(testName) + "-FILE-OP";
                String[] fileInfo1 = new String[] { opFileName, DOCLIB };
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

                // Login to User1, set up the cloud sync
                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

                // create a file
                DocumentLibraryPage docLib = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUser.uploadFileInFolder(drone, fileInfo1).render();
                FileDirectoryInfo fileDirectoryInfo = docLib.getFileDirectoryInfo(opFileName);
                DestinationAndAssigneePage destinationAndAssigneePage = fileDirectoryInfo.selectSyncToCloud().render();
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectFolder("Documents");
                destinationAndAssigneePage.clickSyncButton();
                waitForSync(drone, opFileName, opSiteName);
                ShareUser.logout(drone);
        }

        /*
        * AONE-15474 Changes to file in on-premise/cloud
        */
        @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
        public void AONE_15474() throws Exception
        {
                String uniqueName = "_Z7a";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);

                String opFileName = getFileName(testName) + "-FILE-OP";
                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

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
                waitForSync(drone, opFileName, opSiteName);
                editDocumentPropertiesPage = documentLibraryPage.getFileDirectoryInfo(opFileName).selectEditProperties().render();
                newDescription = editDocumentPropertiesPage.getDescription();
                Assert.assertEquals(newDescription, cloudChange, "File description did not change.");

        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15475() throws Exception
        {

                String uniqueName = "_Z7g";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFolderName = getFolderName(testName) + "-FOLDER-OP";
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

                // Login to User1, set up the cloud sync
                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

                // create a file
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUserSitePage.createFolder(drone, opFolderName, opFolderName, opFolderName).render();
                FileDirectoryInfo fileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(opFolderName);
                DestinationAndAssigneePage destinationAndAssigneePage = fileDirectoryInfo.selectSyncToCloud().render();
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.selectSubmitButtonToSync().render();
                ShareUser.logout(drone);
        }

        /*
        * AONE-15475 Changes to folder in on-premise/cloud
        */
        @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
        public void AONE_15475() throws Exception
        {
                String uniqueName = "_Z7g";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);

                String opFolderName = getFolderName(testName) + "-FOLDER-OP";
                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

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
                documentLibraryPage = editDocumentPropertiesPage.selectSave().render();

                //drone.refresh();
                documentLibraryPage.getFileDirectoryInfo(opFolderName).selectRequestSync().render();
                waitForSync(drone, opFolderName, opSiteName);
                ShareUser.logout(drone);

                // ---- Step 2 ----
                // ---- Step action ----
                // Verify the changes in Cloud
                // ---- Expected results ----
                // The changes are applied after a while

                ShareUser.login(hybridDrone, cloudUser);
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
                editDocumentPropertiesPage.selectSave().render();
                ShareUser.logout(hybridDrone);

                // ---- Step 4 ----
                // ---- Step action ----
                // Verify the changes in on-premise
                // ---- Expected results ----
                // The changes are applied after a bit of time

                ShareUser.login(drone, opUser);
                ShareUser.openSiteDashboard(drone, opSiteName);
                documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                waitForSync(drone, opFolderName, opSiteName);
                int maxRefreshes = 10;
                while (documentLibraryPage.getFileDirectoryInfo(opFolderName).getDescription().contains(opChange) && maxRefreshes > 0)
                {

                        documentLibraryPage = documentLibraryPage.getSiteNav().selectSiteDocumentLibrary().render();
                        maxRefreshes = maxRefreshes - 1;

                }


                editDocumentPropertiesPage = documentLibraryPage.getFileDirectoryInfo(opFolderName).selectEditProperties().render();
                newDescription = editDocumentPropertiesPage.getDescription();
                Assert.assertEquals(newDescription, cloudChange, "File description did not change.");

                ShareUser.logout(drone);
        }

        @Test(groups = "DataPrepHybrid")
        public void dataPrep_AONE_15476() throws Exception
        {
                String uniqueName = "_Z7";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String opFolderName = getFolderName(testName) + "-FOLDER-OP";
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

                // Login to User1, set up the cloud sync
                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

                // create a file
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
                ShareUserSitePage.createFolder(drone, opFolderName, opFolderName, opFolderName);
                FileDirectoryInfo fileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(opFolderName);
                DestinationAndAssigneePage destinationAndAssigneePage = fileDirectoryInfo.selectSyncToCloud().render();
                destinationAndAssigneePage.selectSite(cloudSiteName);
                destinationAndAssigneePage.clickSyncButton();
                waitForSync(drone, opFolderName, opSiteName);
                ShareUser.logout(drone);
        }

        /*
        * AONE-15476 Unsync the folder from cloud by owner. Select to remove the folder
        */
        @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
        public void AONE_15476() throws Exception
        {
                String uniqueName = "_Z7";
                String testName = getTestName();
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);

                String opFolderName = getFolderName(testName) + "-FOLDER-OP";
                String opSiteName = getSiteName(testName) + "-OP" + uniqueName;
                String cloudSiteName = getSiteName(testName) + "-CL" + uniqueName;

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
                        documentLibraryPage.getSiteNav().selectSiteDocumentLibrary().render();
                        maxRefreshes = maxRefreshes - 1;
                }

                Assert.assertFalse(documentLibraryPage.isItemVisble(opFolderName), "File is still visible.");

                ShareUser.logout(drone);
                ShareUser.logout(hybridDrone);

        }

}
