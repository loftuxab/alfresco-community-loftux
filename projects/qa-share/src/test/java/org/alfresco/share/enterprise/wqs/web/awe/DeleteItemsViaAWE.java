package org.alfresco.share.enterprise.wqs.web.awe;

import org.alfresco.po.share.dashlet.SiteWebQuickStartDashlet;
import org.alfresco.po.share.dashlet.WebQuickStartOptions;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.wqs.WcmqsBlogPage;
import org.alfresco.po.share.wqs.WcmqsBlogPostPage;
import org.alfresco.po.share.wqs.WcmqsLoginPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.alfresco.share.util.api.CreateUserAPI;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Created by svidrascu on 11/19/2014.
 */
public class DeleteItemsViaAWE extends AbstractUtils
{
        private String testName;
        private String wqsURL;
        private String testUser;
        private String serverIpAddress = "192.168.56.105";

        private final String ETHICAL_FUNDS = "Ethical funds";
        private final String siteName = "DeleteItemsViaAwe";

        private static final Logger logger = Logger.getLogger(DeleteItemsViaAWE.class);

        @Override
        @BeforeClass(alwaysRun = true)
        public void setup() throws Exception
        {
                testName = this.getClass().getSimpleName();
                testUser = getUserNameFreeDomain(testName);
                wqsURL = siteName + ":8080/wcmqs";
                //        serverIpAddress = serverIp;
                logger.info("wcmqs url : " + wqsURL);
                logger.info("Start Tests from: " + testName);
        }

        @BeforeMethod(alwaysRun = true, groups = { "WQS" })
        public void testSetup() throws Exception
        {
                super.setup();
        }

        @AfterMethod(alwaysRun = true)
        public void tearDown()
        {
                super.tearDown();
        }

        @Test(groups = { "DataPrepWQS" })
        public void dataPrep_AONE() throws Exception
        {
                // User login
                String[] testUserInfo = new String[] { testUser };
                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

                // ---- Step 1 ----
                // ---- Step Action -----
                // WCM Quick Start is installed; - is not required to be executed automatically
                ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
                // ---- Step 2 ----
                // ---- Step Action -----
                // Site "My Web Site" is created in Alfresco Share;
                ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

                // ---- Step 3 ----
                // ---- Step Action -----
                // WCM Quick Start Site Data is imported;
                SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlets.WEB_QUICK_START);
                SiteWebQuickStartDashlet wqsDashlet = siteDashBoard.getDashlet(SITE_WEB_QUICK_START_DASHLET).render();
                wqsDashlet.selectWebsiteDataOption(WebQuickStartOptions.FINANCE);
                wqsDashlet.clickImportButtton();
                wqsDashlet.waitForImportMessage();

                //Change property for quick start to sitename
                DocumentLibraryPage documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
                documentLibPage.selectFolder("Alfresco Quick Start");
                EditDocumentPropertiesPage documentPropertiesPage = documentLibPage.getFileDirectoryInfo("Quick Start Editorial").selectEditProperties()
                        .render();
                documentPropertiesPage.setSiteHostname(siteName);
                documentPropertiesPage.clickSave();

                //Change property for quick start live to ip address
                documentLibPage.getFileDirectoryInfo("Quick Start Live").selectEditProperties().render();
                documentPropertiesPage.setSiteHostname(serverIpAddress);
                documentPropertiesPage.clickSave();

                //setup new entry in hosts to be able to access the new wcmqs site
                String setHostAddress = "cmd.exe /c echo " + serverIpAddress + " " + siteName + " >> %WINDIR%\\System32\\Drivers\\Etc\\Hosts";
                Runtime.getRuntime().exec(setHostAddress);

        }

        /**
         * AONE-5641:Deleting "Ethical funds" blog post
         */
        @Test(groups = "WQS")
        public void AONE_5641() throws Exception
        {
                // ---- Step 1 ----
                // ---- Step action ---
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened

                drone.navigateTo(wqsURL);

                // ---- Step 2 ----
                // ---- Step action ---
                // Open "Ethical funds" blog post;
                // ---- Expected results ----
                // 2. Blog post is opened;

                WcmqsBlogPage wcmqsBlogPage = new WcmqsBlogPage(drone);
                wcmqsBlogPage.openBlogPost(ETHICAL_FUNDS);

                WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
                wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);

                // ---- Step 3 ----
                // ---- Step action ---
                // Click Delete button near post;
                // ---- Expected results ----
                // Confirm Delete window is opened;

                WcmqsBlogPostPage wcmqsBlogPostPage = new WcmqsBlogPostPage(drone);
                wcmqsBlogPostPage.deleteArticle();
                Assert.assertTrue(wcmqsBlogPostPage.isDeleteConfirmationWindowDisplayed(), "Delete confirmation window not displayed");

                // ---- Step 4 ----
                // ---- Step action ---
                // Click Cancel button;
                // ---- Expected results ----
                // File is not deleted;

                wcmqsBlogPostPage.cancelArticleDelete();
                wcmqsBlogPage.checkIfBlogExists(ETHICAL_FUNDS);

                // ---- Step 5 ----
                // ---- Step action ---
                // Click Delete button;
                // ---- Expected results ----
                // Confirm Delete window is opened;
                wcmqsBlogPostPage.deleteArticle();
                Assert.assertTrue(wcmqsBlogPostPage.isDeleteConfirmationWindowDisplayed(), "Delete confirmation window not displayed");

                // ---- Step 6 ----
                // ---- Step action ---
                // Click OK button;
                // ---- Expected results ----
                //  File is deleted and no more dislpayed in the list of articles;
                wcmqsBlogPostPage.confirmArticleDelete();
                wcmqsBlogPage.checkIfBlogIsDeleted(ETHICAL_FUNDS);

                // ---- Step 7 ----
                // ---- Step action ---
                // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/blog) and verify blog1.html file;
                // ---- Expected results ----
                // Changes made via AWE are dislpayed correctly, file is removed and not displayed in the folder;
                ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
                DocumentLibraryPage documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
                documentLibPage.selectFolder("Alfresco Quick Start");
                documentLibPage.selectFolder("Quick Start Editorial");
                documentLibPage.selectFolder("root");
                documentLibPage.selectFolder("blog");

                Assert.assertFalse(documentLibPage.isFileVisible("blog1.html"), "Ethical funds page hasn't been deleted correctly");

        }
}
