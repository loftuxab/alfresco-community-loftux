package org.alfresco.share.enterprise.wqs.web.awe;

import org.alfresco.po.share.dashlet.SiteWebQuickStartDashlet;
import org.alfresco.po.share.dashlet.WebQuickStartOptions;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.wqs.*;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by svidrascu on 11/19/2014.
 */
public class DeleteItemsViaAWE extends AbstractUtils
{
        private String testName;
        private String wqsURL;
        private String siteName;
        private String ipAddress;
        private String hostName;

        private final String ALFRESCO_QUICK_START = "Alfresco Quick Start";
        private final String QUICK_START_EDITORIAL = "Quick Start Editorial";
        private final String ROOT_FOLDER = "root";

        private static final Logger logger = Logger.getLogger(DeleteItemsViaAWE.class);

        @Override
        @BeforeClass(alwaysRun = true)
        public void setup() throws Exception
        {
                super.setup();

                testName = this.getClass().getSimpleName();
                siteName = testName;
                hostName = (shareUrl).replaceAll(".*\\//|\\:.*", "");
                try
                {
                        ipAddress = InetAddress.getByName(hostName).toString().replaceAll(".*/", "");
                        logger.info("Ip address from Alfresco server was obtained");
                }
                catch (UnknownHostException | SecurityException e)
                {
                        logger.error("Ip address from Alfresco server could not be obtained");
                }

                ;
                wqsURL = siteName + ":8080/wcmqs";
                logger.info(" wcmqs url : " + wqsURL);
                logger.info("Start Tests from: " + testName);
        }

        @AfterClass(alwaysRun = true)
        public void tearDown()
        {
                super.tearDown();
        }

        @Test(groups = { "DataPrepWQS" })
        public void dataPrep_AONE() throws Exception
        {
                // User login
                // ---- Step 1 ----
                // ---- Step Action -----
                // WCM Quick Start is installed; - is not required to be executed automatically
                ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

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
                documentPropertiesPage.setSiteHostname(ipAddress);
                documentPropertiesPage.clickSave();

                //setup new entry in hosts to be able to access the new wcmqs site
                String setHostAddress = "cmd.exe /c echo " + ipAddress + " " + siteName + " >> %WINDIR%\\System32\\Drivers\\Etc\\hosts";
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
                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                wcmqsHomePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);

                WcmqsBlogPage wcmqsBlogPage = new WcmqsBlogPage(drone);
                wcmqsBlogPage.openBlogPost(WcmqsBlogPage.ETHICAL_FUNDS);

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
                Assert.assertTrue(wcmqsBlogPage.checkIfBlogExists(WcmqsBlogPage.ETHICAL_FUNDS));

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
                Assert.assertTrue(wcmqsBlogPage.checkIfBlogIsDeleted(WcmqsBlogPage.ETHICAL_FUNDS));

                // ---- Step 7 ----
                // ---- Step action ---
                // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/blog) and verify blog1.html file;
                // ---- Expected results ----
                // Changes made via AWE are dislpayed correctly, file is removed and not displayed in the folder;
                ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
                DocumentLibraryPage documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
                documentLibPage.selectFolder(ALFRESCO_QUICK_START);
                documentLibPage.selectFolder(QUICK_START_EDITORIAL);
                documentLibPage.selectFolder(ROOT_FOLDER);
                documentLibPage.selectFolder(WcmqsBlogPage.BLOG);

                Assert.assertFalse(documentLibPage.isFileVisible(WcmqsBlogPage.BLOG_1), "Ethical funds page hasn't been deleted correctly");

        }

        /**
         * AONE-5642:Deleting "Company organises workshop" blog post
         */
        @Test(groups = "WQS")
        public void AONE_5642() throws Exception
        {
                // ---- Step 1 ----
                // ---- Step action ---
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened

                drone.navigateTo(wqsURL);

                // ---- Step 2 ----
                // ---- Step action ---
                // Open "Company organises workshop" blog post;
                // ---- Expected results ----
                // 2. Blog post is opened;
                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                wcmqsHomePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);

                WcmqsBlogPage wcmqsBlogPage = new WcmqsBlogPage(drone);
                wcmqsBlogPage.openBlogPost(WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP);

                //                WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
                //                wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);

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
                Assert.assertTrue(wcmqsBlogPage.checkIfBlogExists(WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP));

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
                Assert.assertTrue(wcmqsBlogPage.checkIfBlogIsDeleted(WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP), "Article was not deleted!");

                // ---- Step 7 ----
                // ---- Step action ---
                // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/blog) and verify blog2.html file;
                // ---- Expected results ----
                // Changes made via AWE are dislpayed correctly, file is removed and not displayed in the folder;
                drone.navigateTo(shareUrl);
                DocumentLibraryPage documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
                documentLibPage.selectFolder(ALFRESCO_QUICK_START);
                documentLibPage.selectFolder(QUICK_START_EDITORIAL);
                documentLibPage.selectFolder(ROOT_FOLDER);
                documentLibPage.selectFolder(WcmqsBlogPage.BLOG);
                Assert.assertFalse(documentLibPage.isFileVisible(WcmqsBlogPage.BLOG_2), "Company organizes workshop page hasn't been deleted correctly");

        }

        /**
         * AONE-5643:Deleting "Our Analyst's thoughts" blog post
         */
        @Test(groups = "WQS")
        public void AONE_5643() throws Exception
        {
                // ---- Step 1 ----
                // ---- Step action ---
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened

                drone.navigateTo(wqsURL);

                // ---- Step 2 ----
                // ---- Step action ---
                // Open "Our top analyst's latest..." blog post;
                // ---- Expected results ----
                // 2. Blog post is opened;

                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                wcmqsHomePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);

                WcmqsBlogPage wcmqsBlogPage = new WcmqsBlogPage(drone);
                wcmqsBlogPage.openBlogPost(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS);

                //                WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
                //                wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);

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
                Assert.assertTrue(wcmqsBlogPage.checkIfBlogExists(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS));

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
                Assert.assertTrue(wcmqsBlogPage.checkIfBlogIsDeleted(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS));

                // ---- Step 7 ----
                // ---- Step action ---
                // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/blog) and verify blog3.html file;
                // ---- Expected results ----
                // Changes made via AWE are dislpayed correctly, file is removed and not displayed in the folder;
                drone.navigateTo(shareUrl);
                DocumentLibraryPage documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
                documentLibPage.selectFolder(ALFRESCO_QUICK_START);
                documentLibPage.selectFolder(QUICK_START_EDITORIAL);
                documentLibPage.selectFolder(ROOT_FOLDER);
                documentLibPage.selectFolder(WcmqsBlogPage.BLOG);

                Assert.assertFalse(documentLibPage.isFileVisible(WcmqsBlogPage.BLOG_3), "Our top analyst's latest thoughts page hasn't been deleted correctly");

        }

        /**
         * AONE-5644:Deleting "Europe dept...."article (Global economy)
         */
        @Test(groups = "WQS")
        public void AONE_5644() throws Exception
        {
                // ---- Step 1 ----
                // ---- Step action ---
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened

                drone.navigateTo(wqsURL);

                // ---- Step 2 ----
                // ---- Step action ---
                //  Open "Europe dept concerns ease but bank fears remain" article in Global economy (News);
                // ---- Expected results ----
                // 2. Blog post is opened;
                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                WcmqsNewsPage wcmqsNewsPage = wcmqsHomePage.openNewsPageFolder(WcmqsNewsPage.GLOBAL);

                wcmqsNewsPage.clickNewsByTitle(WcmqsNewsPage.EUROPE_DEPT_CONCERNS);
                //                WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
                //                wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);

                // ---- Step 3 ----
                // ---- Step action ---
                // Click Delete button near post;
                // ---- Expected results ----
                // Confirm Delete window is opened;

                wcmqsNewsPage.deleteArticle();
                Assert.assertTrue(wcmqsNewsPage.isDeleteConfirmationWindowDisplayed(), "Delete confirmation window not displayed");

                // ---- Step 4 ----
                // ---- Step action ---
                // Click Cancel button;
                // ---- Expected results ----
                // File is not deleted;

                wcmqsNewsPage.cancelArticleDelete();
                Assert.assertTrue(wcmqsNewsPage.checkIfNewsExists(WcmqsNewsPage.EUROPE_DEPT_CONCERNS));

                // ---- Step 5 ----
                // ---- Step action ---
                // Click Delete button;
                // ---- Expected results ----
                // Confirm Delete window is opened;
                wcmqsNewsPage.deleteArticle();
                Assert.assertTrue(wcmqsNewsPage.isDeleteConfirmationWindowDisplayed(), "Delete confirmation window not displayed");

                // ---- Step 6 ----
                // ---- Step action ---
                // Click OK button;
                // ---- Expected results ----
                //  File is deleted and no more dislpayed in the list of articles;
                wcmqsNewsPage.confirmArticleDelete();
                Assert.assertTrue(wcmqsNewsPage.checkIfBlogIsDeleted(WcmqsNewsPage.EUROPE_DEPT_CONCERNS));

                // ---- Step 7 ----
                // ---- Step action ---
                // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/blog) and verify blog1.html file;
                // ---- Expected results ----
                // Changes made via AWE are dislpayed correctly, file is removed and not displayed in the folder;
                drone.navigateTo(shareUrl);

                DocumentLibraryPage documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
                documentLibPage.selectFolder(ALFRESCO_QUICK_START);
                documentLibPage.selectFolder(QUICK_START_EDITORIAL);
                documentLibPage.selectFolder(ROOT_FOLDER);
                documentLibPage.selectFolder(WcmqsNewsPage.NEWS);
                documentLibPage.selectFolder(WcmqsNewsPage.GLOBAL);

                Assert.assertFalse(documentLibPage.isFileVisible(WcmqsNewsPage.ARTICLE_4),
                        "Europe dept concerns ease but bank fears remain page hasn't been deleted correctly");

        }

        /**
         * AONE-5645:Deleting "FTSE 100 rallies from seven-week low" (Global economy)
         */
        @Test(groups = "WQS")
        public void AONE_5645() throws Exception
        {
                // ---- Step 1 ----
                // ---- Step action ---
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened

                drone.navigateTo(wqsURL);

                // ---- Step 2 ----
                // ---- Step action ---
                //  Open "Europe dept concerns ease but bank fears remain" article in Global economy (News);
                // ---- Expected results ----
                // 2. Blog post is opened;
                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                WcmqsNewsPage wcmqsNewsPage = wcmqsHomePage.openNewsPageFolder(WcmqsNewsPage.GLOBAL);

                wcmqsNewsPage.clickNewsByTitle(WcmqsNewsPage.FTSE_1000);
                //                WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
                //                wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);

                // ---- Step 3 ----
                // ---- Step action ---
                // Click Delete button near post;
                // ---- Expected results ----
                // Confirm Delete window is opened;

                wcmqsNewsPage.deleteArticle();
                Assert.assertTrue(wcmqsNewsPage.isDeleteConfirmationWindowDisplayed(), "Delete confirmation window not displayed");

                // ---- Step 4 ----
                // ---- Step action ---
                // Click Cancel button;
                // ---- Expected results ----
                // File is not deleted;

                wcmqsNewsPage.cancelArticleDelete();
                Assert.assertTrue(wcmqsNewsPage.checkIfNewsExists(WcmqsNewsPage.FTSE_1000));

                // ---- Step 5 ----
                // ---- Step action ---
                // Click Delete button;
                // ---- Expected results ----
                // Confirm Delete window is opened;
                wcmqsNewsPage.deleteArticle();
                Assert.assertTrue(wcmqsNewsPage.isDeleteConfirmationWindowDisplayed(), "Delete confirmation window not displayed");

                // ---- Step 6 ----
                // ---- Step action ---
                // Click OK button;
                // ---- Expected results ----
                //  File is deleted and no more dislpayed in the list of articles;
                wcmqsNewsPage.confirmArticleDelete();
                Assert.assertTrue(wcmqsNewsPage.checkIfBlogIsDeleted(WcmqsNewsPage.FTSE_1000));

                // ---- Step 7 ----
                // ---- Step action ---
                // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/blog) and verify blog1.html file;
                // ---- Expected results ----
                // Changes made via AWE are dislpayed correctly, file is removed and not displayed in the folder;
                drone.navigateTo(shareUrl);

                DocumentLibraryPage documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
                documentLibPage.selectFolder(ALFRESCO_QUICK_START);
                documentLibPage.selectFolder(QUICK_START_EDITORIAL);
                documentLibPage.selectFolder(ROOT_FOLDER);
                documentLibPage.selectFolder(WcmqsNewsPage.NEWS);
                documentLibPage.selectFolder(WcmqsNewsPage.GLOBAL);

                Assert.assertFalse(documentLibPage.isFileVisible(WcmqsNewsPage.ARTICLE_3),
                        "FTSE 100 rallies from seven-week low page hasn't been deleted correctly");

        }

        /**
         * AONE-5645:Deleting "Global car industry" (Global economy)
         */
        @Test(groups = "WQS")
        public void AONE_5646() throws Exception
        {
                // ---- Step 1 ----
                // ---- Step action ---
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened

                drone.navigateTo(wqsURL);

                // ---- Step 2 ----
                // ---- Step action ---
                //  Open "Global car industry" article in Companies (News);
                // ---- Expected results ----
                // 2. Blog post is opened;
                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                WcmqsNewsPage wcmqsNewsPage = wcmqsHomePage.openNewsPageFolder(WcmqsNewsPage.COMPANIES);

                wcmqsNewsPage.clickNewsByTitle(WcmqsNewsPage.GLOBAL_CAR_INDUSTRY);
                //                WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
                //                wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);

                // ---- Step 3 ----
                // ---- Step action ---
                // Click Delete button near post;
                // ---- Expected results ----
                // Confirm Delete window is opened;

                wcmqsNewsPage.deleteArticle();
                Assert.assertTrue(wcmqsNewsPage.isDeleteConfirmationWindowDisplayed(), "Delete confirmation window not displayed");

                // ---- Step 4 ----
                // ---- Step action ---
                // Click Cancel button;
                // ---- Expected results ----
                // File is not deleted;

                wcmqsNewsPage.cancelArticleDelete();
                Assert.assertTrue(wcmqsNewsPage.checkIfNewsExists(WcmqsNewsPage.GLOBAL_CAR_INDUSTRY));

                // ---- Step 5 ----
                // ---- Step action ---
                // Click Delete button;
                // ---- Expected results ----
                // Confirm Delete window is opened;
                wcmqsNewsPage.deleteArticle();
                Assert.assertTrue(wcmqsNewsPage.isDeleteConfirmationWindowDisplayed(), "Delete confirmation window not displayed");

                // ---- Step 6 ----
                // ---- Step action ---
                // Click OK button;
                // ---- Expected results ----
                //  File is deleted and no more dislpayed in the list of articles;
                wcmqsNewsPage.confirmArticleDelete();
                Assert.assertTrue(wcmqsNewsPage.checkIfBlogIsDeleted(WcmqsNewsPage.GLOBAL_CAR_INDUSTRY));

                // ---- Step 7 ----
                // ---- Step action ---
                // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/blog) and verify blog1.html file;
                // ---- Expected results ----
                // Changes made via AWE are dislpayed correctly, file is removed and not displayed in the folder;
                drone.navigateTo(shareUrl);

                DocumentLibraryPage documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
                documentLibPage.selectFolder(ALFRESCO_QUICK_START);
                documentLibPage.selectFolder(QUICK_START_EDITORIAL);
                documentLibPage.selectFolder(ROOT_FOLDER);
                documentLibPage.selectFolder(WcmqsNewsPage.NEWS);
                documentLibPage.selectFolder(WcmqsNewsPage.COMPANIES);

                Assert.assertFalse(documentLibPage.isFileVisible(WcmqsNewsPage.ARTICLE_2), "Global car industry page hasn't been deleted correctly");

        }

        /**
         * AONE-5647:Deleting "Fresh flight to Swiss franc as Europe's bond strains return" (Global economy)
         */
        @Test(groups = "WQS")
        public void AONE_5647() throws Exception
        {
                // ---- Step 1 ----
                // ---- Step action ---
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened

                drone.navigateTo(wqsURL);

                // ---- Step 2 ----
                // ---- Step action ---
                //  Open "Fresh flight to Swiss franc as Europe's bond strains return" article in Companies (News);
                // ---- Expected results ----
                // 2. Blog post is opened;
                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                WcmqsNewsPage wcmqsNewsPage = wcmqsHomePage.openNewsPageFolder(WcmqsNewsPage.COMPANIES);

                wcmqsNewsPage.clickNewsByTitle(WcmqsNewsPage.FRESH_FLIGHT_TO_SWISS);
                //                WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
                //                wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);

                // ---- Step 3 ----
                // ---- Step action ---
                // Click Delete button near post;
                // ---- Expected results ----
                // Confirm Delete window is opened;

                wcmqsNewsPage.deleteArticle();
                Assert.assertTrue(wcmqsNewsPage.isDeleteConfirmationWindowDisplayed(), "Delete confirmation window not displayed");

                // ---- Step 4 ----
                // ---- Step action ---
                // Click Cancel button;
                // ---- Expected results ----
                // File is not deleted;

                wcmqsNewsPage.cancelArticleDelete();
                Assert.assertTrue(wcmqsNewsPage.checkIfNewsExists(WcmqsNewsPage.FRESH_FLIGHT_TO_SWISS));

                // ---- Step 5 ----
                // ---- Step action ---
                // Click Delete button;
                // ---- Expected results ----
                // Confirm Delete window is opened;
                wcmqsNewsPage.deleteArticle();
                Assert.assertTrue(wcmqsNewsPage.isDeleteConfirmationWindowDisplayed(), "Delete confirmation window not displayed");

                // ---- Step 6 ----
                // ---- Step action ---
                // Click OK button;
                // ---- Expected results ----
                //  File is deleted and no more dislpayed in the list of articles;
                wcmqsNewsPage.confirmArticleDelete();
                Assert.assertTrue(wcmqsNewsPage.checkIfBlogIsDeleted(WcmqsNewsPage.FRESH_FLIGHT_TO_SWISS));

                // ---- Step 7 ----
                // ---- Step action ---
                // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/blog) and verify blog1.html file;
                // ---- Expected results ----
                // Changes made via AWE are dislpayed correctly, file is removed and not displayed in the folder;
                drone.navigateTo(shareUrl);

                DocumentLibraryPage documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
                documentLibPage.selectFolder(ALFRESCO_QUICK_START);
                documentLibPage.selectFolder(QUICK_START_EDITORIAL);
                documentLibPage.selectFolder(ROOT_FOLDER);
                documentLibPage.selectFolder(WcmqsNewsPage.NEWS);
                documentLibPage.selectFolder(WcmqsNewsPage.COMPANIES);

                Assert.assertFalse(documentLibPage.isFileVisible(WcmqsNewsPage.ARTICLE_1),
                        "Fresh flight to Swiss franc as Europe's bond strains return page hasn't been deleted correctly");

        }

        /**
         * AONE-5648:Deleting Investors fear rising risk of US regional defaults (Global economy)
         */
        @Test(groups = "WQS")
        public void AONE_5648() throws Exception
        {
                // ---- Step 1 ----
                // ---- Step action ---
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened

                drone.navigateTo(wqsURL);

                // ---- Step 2 ----
                // ---- Step action ---
                //  Open Investors fear rising risk of US regional defaults article in markets (News);
                // ---- Expected results ----
                // 2. Blog post is opened;
                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                WcmqsNewsPage wcmqsNewsPage = wcmqsHomePage.openNewsPageFolder(WcmqsNewsPage.MARKETS);

                wcmqsNewsPage.clickNewsByTitle(WcmqsNewsPage.INVESTORS_FEAR);
                //                WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
                //                wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);

                // ---- Step 3 ----
                // ---- Step action ---
                // Click Delete button near post;
                // ---- Expected results ----
                // Confirm Delete window is opened;

                wcmqsNewsPage.deleteArticle();
                Assert.assertTrue(wcmqsNewsPage.isDeleteConfirmationWindowDisplayed(), "Delete confirmation window not displayed");

                // ---- Step 4 ----
                // ---- Step action ---
                // Click Cancel button;
                // ---- Expected results ----
                // File is not deleted;

                wcmqsNewsPage.cancelArticleDelete();
                Assert.assertTrue(wcmqsNewsPage.checkIfNewsExists(WcmqsNewsPage.INVESTORS_FEAR));

                // ---- Step 5 ----
                // ---- Step action ---
                // Click Delete button;
                // ---- Expected results ----
                // Confirm Delete window is opened;
                wcmqsNewsPage.deleteArticle();
                Assert.assertTrue(wcmqsNewsPage.isDeleteConfirmationWindowDisplayed(), "Delete confirmation window not displayed");

                // ---- Step 6 ----
                // ---- Step action ---
                // Click OK button;
                // ---- Expected results ----
                //  File is deleted and no more dislpayed in the list of articles;
                wcmqsNewsPage.confirmArticleDelete();
                Assert.assertTrue(wcmqsNewsPage.checkIfBlogIsDeleted(WcmqsNewsPage.INVESTORS_FEAR));

                // ---- Step 7 ----
                // ---- Step action ---
                // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/blog) and verify blog1.html file;
                // ---- Expected results ----
                // Changes made via AWE are dislpayed correctly, file is removed and not displayed in the folder;
                drone.navigateTo(shareUrl);

                DocumentLibraryPage documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
                documentLibPage.selectFolder(ALFRESCO_QUICK_START);
                documentLibPage.selectFolder(QUICK_START_EDITORIAL);
                documentLibPage.selectFolder(ROOT_FOLDER);
                documentLibPage.selectFolder(WcmqsNewsPage.NEWS);
                documentLibPage.selectFolder(WcmqsNewsPage.MARKETS);

                Assert.assertFalse(documentLibPage.isFileVisible(WcmqsNewsPage.ARTICLE_6),
                        "Investors fear rising risk of US regional defaults page hasn't been deleted correctly");

        }

        /**
         * AONE-5649:Deleting House prices face rollercoaster ride (markets)
         */
        @Test(groups = "WQS")
        public void AONE_5649() throws Exception
        {
                // ---- Step 1 ----
                // ---- Step action ---
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened

                drone.navigateTo(wqsURL);

                // ---- Step 2 ----
                // ---- Step action ---
                //  Open House prices face rollercoaster ride article in markets (News);
                // ---- Expected results ----
                // 2. Blog post is opened;
                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                WcmqsNewsPage wcmqsNewsPage = wcmqsHomePage.openNewsPageFolder(WcmqsNewsPage.MARKETS);

                wcmqsNewsPage.clickNewsByTitle(WcmqsNewsPage.HOUSE_PRICES);
                //                WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
                //                wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);

                // ---- Step 3 ----
                // ---- Step action ---
                // Click Delete button near post;
                // ---- Expected results ----
                // Confirm Delete window is opened;

                wcmqsNewsPage.deleteArticle();
                Assert.assertTrue(wcmqsNewsPage.isDeleteConfirmationWindowDisplayed(), "Delete confirmation window not displayed");

                // ---- Step 4 ----
                // ---- Step action ---
                // Click Cancel button;
                // ---- Expected results ----
                // File is not deleted;

                wcmqsNewsPage.cancelArticleDelete();
                Assert.assertTrue(wcmqsNewsPage.checkIfNewsExists(WcmqsNewsPage.HOUSE_PRICES));

                // ---- Step 5 ----
                // ---- Step action ---
                // Click Delete button;
                // ---- Expected results ----
                // Confirm Delete window is opened;
                wcmqsNewsPage.deleteArticle();
                Assert.assertTrue(wcmqsNewsPage.isDeleteConfirmationWindowDisplayed(), "Delete confirmation window not displayed");

                // ---- Step 6 ----
                // ---- Step action ---
                // Click OK button;
                // ---- Expected results ----
                //  File is deleted and no more dislpayed in the list of articles;
                wcmqsNewsPage.confirmArticleDelete();
                Assert.assertTrue(wcmqsNewsPage.checkIfBlogIsDeleted(WcmqsNewsPage.HOUSE_PRICES));

                // ---- Step 7 ----
                // ---- Step action ---
                // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/blog) and verify blog1.html file;
                // ---- Expected results ----
                // Changes made via AWE are dislpayed correctly, file is removed and not displayed in the folder;
                drone.navigateTo(shareUrl);

                DocumentLibraryPage documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
                documentLibPage.selectFolder(ALFRESCO_QUICK_START);
                documentLibPage.selectFolder(QUICK_START_EDITORIAL);
                documentLibPage.selectFolder(ROOT_FOLDER);
                documentLibPage.selectFolder(WcmqsNewsPage.NEWS);
                documentLibPage.selectFolder(WcmqsNewsPage.MARKETS);

                Assert.assertFalse(documentLibPage.isFileVisible(WcmqsNewsPage.ARTICLE_5),
                        "House prices face rollercoaster ride page hasn't been deleted correctly");

        }

}
