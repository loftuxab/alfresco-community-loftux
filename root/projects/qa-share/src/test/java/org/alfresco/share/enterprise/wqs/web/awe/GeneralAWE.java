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
 * Created by Lucian Tuca on 12/02/2014.
 */
public class GeneralAWE extends AbstractUtils
{
        private static final Logger logger = Logger.getLogger(GeneralAWE.class);
        private final String ALFRESCO_QUICK_START = "Alfresco Quick Start";
        private final String QUICK_START_EDITORIAL = "Quick Start Editorial";
        private final String ROOT_FOLDER = "root";
        private String testName;
        private String wqsURL;
        private String siteName;
        private String ipAddress;
        private String hostName;

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

        /*
        * AONE-5650 Toggle Edit Markers
        */
        @Test(groups = { "WQS" })
        public void AONE_5650() throws Exception
        {

                // ---- Step 1 ----
                // ---- Step action ----
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened;

                navigateTo(wqsURL);

                // ---- Step 2 ----
                // ---- Step action ----
                // Open any blog post;
                // ---- Expected results ----
                // Blog post is opened;

                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                wcmqsHomePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);

                WcmqsBlogPage wcmqsBlogPage = new WcmqsBlogPage(drone);
                wcmqsBlogPage.openBlogPost(WcmqsBlogPage.ETHICAL_FUNDS);

                WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
                wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);

                WcmqsBlogPostPage blogPostPage = new WcmqsBlogPostPage(drone).render();
                Assert.assertNotNull(blogPostPage);

                // ---- Step 3 ----
                // ---- Step action ----
                // Click Toggle Edit Markers button on the AWE pannel;
                // ---- Expected results ----
                // Edit Markers are not displayed;

                blogPostPage.clickToggleEditMarkers();
                Assert.assertFalse(blogPostPage.isEditMarkersDisplayed());

                // ---- Step 4 ----
                // ---- Step action ----
                // Click Toggle Edit Markers button on the AWE pannel once more;
                // ---- Expected results ----
                // Edit Markers are displayed;

                blogPostPage.clickToggleEditMarkers();
                Assert.assertTrue(blogPostPage.isEditMarkersDisplayed());
        }

        /*
        * AONE-5651 Orientation
        */
        @Test(groups = { "WQS" })
        public void AONE_5651() throws Exception
        {

                // ---- Step 1 ----
                // ---- Step action ----
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened;

                navigateTo(wqsURL);

                // ---- Step 2 ----
                // ---- Step action ----
                // Open any blog post;
                // ---- Expected results ----
                // Blog post is opened;

                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                wcmqsHomePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);

                WcmqsBlogPage wcmqsBlogPage = new WcmqsBlogPage(drone);
                wcmqsBlogPage.openBlogPost(WcmqsBlogPage.ETHICAL_FUNDS);

                WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
                wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);

                WcmqsBlogPostPage blogPostPage = new WcmqsBlogPostPage(drone).render();
                Assert.assertNotNull(blogPostPage);

                // ---- Step 3 ----
                // ---- Step action ----
                // In Orientation menu select Left;
                // ---- Expected results ----
                // AWE pannel is displayed on the left side;

                blogPostPage.changeOrientationLeft();
                Assert.assertTrue(blogPostPage.isAWEOrientedLeft());

                // ---- Step 4 ----
                // ---- Step action ----
                // In Orientation menu select Right;
                // ---- Expected results ----
                // AWE pannel is displayed on the right side;

                blogPostPage.changeOrientationRight();
                Assert.assertTrue(blogPostPage.isAWEOrientedRight());

                // ---- Step 5 ----
                // ---- Step action ----
                // In Orientation menu select Top;
                // ---- Expected results ----
                // AWE pannel is displayed on the top;

                blogPostPage.changeOrientationTop();
                Assert.assertTrue(blogPostPage.isAWEOrientedTop());
        }

        /*
        * AONE-5652 Creating any article via AWE
        */
        @Test(groups = { "WQS" })
        public void AONE_5652() throws Exception
        {

                // ---- Step 1 ----
                // ---- Step action ----
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened;

                navigateTo(wqsURL);

                // ---- Step 2 ----
                // ---- Step action ----
                // Open any article;
                // ---- Expected results ----
                // Article is opened;

                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                wcmqsHomePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);

                WcmqsBlogPage wcmqsBlogPage = new WcmqsBlogPage(drone);
                wcmqsBlogPage.openBlogPost(WcmqsBlogPage.ETHICAL_FUNDS);

                // ---- Step 3 ----
                // ---- Step action ----
                // Specify Username/password (e.g. admin/admin) and log in;
                // ---- Expected results ----
                // User credentials are specified successfully, user is logged in;

                WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
                wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);

                WcmqsBlogPostPage blogPostPage = new WcmqsBlogPostPage(drone).render();
                Assert.assertNotNull(blogPostPage);

                // ---- Step 4 ----
                // ---- Step action ----
                // Click Create Article button on the AWE pannel;
                // ---- Expected results ----
                // Create article window is opened;

                WcmqsEditPage editPage = blogPostPage.clickAWECreateArticle();
                Assert.assertNotNull(editPage);

                // ---- Step 5 ----
                // ---- Step action ----
                // Fill in Name field with test_article.html and content with Hello, world! and save;
                // ---- Expected results ----
                // Information is added successfully, data is saved and displayed correctly, new item is displayed in the list of articles for selected component;

                String articleName = testName + "name.html";
                String articleTitle = testName + "title";
                String articleDescription = testName + "description";
                String articleContent = testName + "content";

                editPage.editName(articleName);
                editPage.editTitle(articleTitle);
                editPage.editDescription(articleDescription);
                editPage.insertTextInContent(articleContent);
                editPage.clickSubmitButton();

                waitAndOpenBlogPost(wcmqsBlogPage, articleTitle, 4);
                WcmqsBlogPostPage newBlogPostPage = new WcmqsBlogPostPage(drone);

                String actualTitle = newBlogPostPage.getTitle();
                String actualContent = newBlogPostPage.getContent();

                Assert.assertEquals(actualTitle, articleTitle);
                Assert.assertEquals(actualContent, articleContent);
        }

        /*
        * AONE-5653 Creating any article via AWE - Cancel
        */
        @Test(groups = { "WQS" })
        public void AONE_5653() throws Exception
        {

                // ---- Step 1 ----
                // ---- Step action ----
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened;

                navigateTo(wqsURL);

                // ---- Step 2 ----
                // ---- Step action ----
                // Open any article;
                // ---- Expected results ----
                // Article is opened;

                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                wcmqsHomePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);

                WcmqsBlogPage wcmqsBlogPage = new WcmqsBlogPage(drone);
                wcmqsBlogPage.openBlogPost(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS);

                // ---- Step 3 ----
                // ---- Step action ----
                // Specify Username/password (e.g. admin/admin) and log in;
                // ---- Expected results ----
                // User credentials are specified successfully, user is logged in;

                WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
                wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);

                WcmqsBlogPostPage blogPostPage = new WcmqsBlogPostPage(drone).render();
                Assert.assertNotNull(blogPostPage);

                // ---- Step 4 ----
                // ---- Step action ----
                // Click Create Article button on the AWE pannel;
                // ---- Expected results ----
                // Create article window is opened;

                WcmqsEditPage editPage = blogPostPage.clickAWECreateArticle();
                Assert.assertNotNull(editPage);

                // ---- Step 5 ----
                // ---- Step action ----
                // Fill in Name field with test_article.html and content with Hello, world!;
                // ---- Expected results ----
                // Information is added successfully;

                String articleName = testName + "name.html";
                String articleTitle = testName + "title";
                String articleDescription = testName + "description";
                String articleContent = testName + "content";

                editPage.editName(articleName);
                editPage.editTitle(articleTitle);
                editPage.editDescription(articleDescription);
                editPage.insertTextInContent(articleContent);

                // ---- Step 6 ----
                // ---- Step action ----
                // Click Cancel button;
                // ---- Expected results ----
                // New item creation is canceled, item is not created and not displayed in the list of items;

                editPage.clickCancelButton();
                WcmqsBlogPostPage newBlogPostPage = new WcmqsBlogPostPage(drone);
                String pageTitle = newBlogPostPage.getTitle();
                Assert.assertTrue(pageTitle.contains(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS));
        }

        /*
        * AONE-5654 Editing any article via AWE
        */
        @Test(groups = { "WQS" })
        public void AONE_5654() throws Exception
        {

                // ---- Step 1 ----
                // ---- Step action ----
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened;

                navigateTo(wqsURL);

                // ---- Step 2 ----
                // ---- Step action ----
                // Open any article;
                // ---- Expected results ----
                // Article is opened;

                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                wcmqsHomePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);

                WcmqsBlogPage wcmqsBlogPage = new WcmqsBlogPage(drone);
                wcmqsBlogPage.openBlogPost(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS);

                WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
                wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);

                WcmqsBlogPostPage blogPostPage = new WcmqsBlogPostPage(drone).render();
                Assert.assertNotNull(blogPostPage);

                // ---- Step 3 ----
                // ---- Step action ----
                // Click Edit button on the AWE pannel;
                // ---- Expected results ----
                // Edit window is opened;

                WcmqsEditPage editPage = blogPostPage.clickAWEEditArticle();
                Assert.assertNotNull(editPage);

                // ---- Step 4 ----
                // ---- Step action ----
                // Change information in all fields and save it;
                // ---- Expected results ----
                // Information is changed successfully, data is saved and displayed correctly;

                String articleName = testName + "name.html";
                String articleTitle = testName + "title";
                String articleDescription = testName + "description";
                String articleContent = testName + "content";

                editPage.editName(articleName);
                editPage.editTitle(articleTitle);
                editPage.editDescription(articleDescription);
                editPage.insertTextInContent(articleContent);

                waitAndOpenBlogPost(wcmqsBlogPage, articleTitle, 4);
                WcmqsBlogPostPage newBlogPostPage = new WcmqsBlogPostPage(drone);

                String actualTitle = newBlogPostPage.getTitle();
                String actualContent = newBlogPostPage.getContent();

                Assert.assertEquals(actualTitle, articleTitle);
                Assert.assertEquals(actualContent, articleContent);

        }

        /*
        * AONE-5655 Editing any article via AWE - Cancel
        */
        @Test(groups = { "WQS" })
        public void AONE_5655() throws Exception
        {

                // ---- Step 1 ----
                // ---- Step action ----
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened;

                navigateTo(wqsURL);

                // ---- Step 2 ----
                // ---- Step action ----
                // Open any article;
                // ---- Expected results ----
                // Article is opened;

                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                wcmqsHomePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);

                WcmqsBlogPage wcmqsBlogPage = new WcmqsBlogPage(drone);
                wcmqsBlogPage.openBlogPost(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS);

                WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
                wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);

                WcmqsBlogPostPage blogPostPage = new WcmqsBlogPostPage(drone).render();
                Assert.assertNotNull(blogPostPage);

                // ---- Step 3 ----
                // ---- Step action ----
                // Click Edit button on the AWE pannel;
                // ---- Expected results ----
                // Edit window is opened;

                WcmqsEditPage editPage = blogPostPage.clickAWECreateArticle();
                Assert.assertNotNull(editPage);

                // ---- Step 4 ----
                // ---- Step action ----
                // Change information in all fields;
                // ---- Expected results ----
                // Information is changed successfully;

                String articleName = testName + "name.html";
                String articleTitle = testName + "title";
                String articleDescription = testName + "description";
                String articleContent = testName + "content";

                editPage.editName(articleName);
                editPage.editTitle(articleTitle);
                editPage.editDescription(articleDescription);
                editPage.insertTextInContent(articleContent);

                // ---- Step 5 ----
                // ---- Step action ----
                // Click Cancel button;
                // ---- Expected results ----
                // Article editing is canceled, changes are not saved;

                editPage.clickCancelButton();
                WcmqsBlogPostPage newBlogPostPage = new WcmqsBlogPostPage(drone);
                String pageTitle = newBlogPostPage.getTitle();
                Assert.assertTrue(pageTitle.contains(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS));
        }

        public void navigateTo(String url)
        {
                drone.navigateTo(url);
        }

        /**
         * Method that waits for the blog post to appear on the page for maximum minutesToWait
         * and then opens it.
         *
         * @param blogPage
         * @param blogPostTitle
         * @param minutesToWait
         */

        private void waitAndOpenBlogPost(WcmqsBlogPage blogPage, String blogPostTitle, int minutesToWait)
        {
                int waitInMilliSeconds = 3000;
                int maxTimeWaitInMilliSeconds = 60000 * minutesToWait;
                boolean newsArticleFound = false;

                while (!newsArticleFound && maxTimeWaitInMilliSeconds > 0)
                {
                        try
                        {
                                blogPage.openBlogPost(blogPostTitle);
                                newsArticleFound = true;
                        }
                        catch (Exception e)
                        {
                                synchronized (this)
                                {
                                        try
                                        {
                                                this.wait(waitInMilliSeconds);
                                        }
                                        catch (InterruptedException ex)
                                        {
                                        }
                                }
                                drone.refresh();
                                maxTimeWaitInMilliSeconds = maxTimeWaitInMilliSeconds - waitInMilliSeconds;
                        }

                }

        }

}
