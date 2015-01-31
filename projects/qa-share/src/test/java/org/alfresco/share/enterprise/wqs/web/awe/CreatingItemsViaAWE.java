package org.alfresco.share.enterprise.wqs.web.awe;

import org.alfresco.po.share.dashlet.SiteWebQuickStartDashlet;
import org.alfresco.po.share.dashlet.WebQuickStartOptions;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.*;
import org.alfresco.po.share.wqs.*;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by Lucian Tuca on 11/17/2014.
 */
@Listeners(FailedTestListener.class)
public class CreatingItemsViaAWE extends AbstractUtils
{
        private String wqsURL;
        private String ipAddress;
        private String hostName;
        private static final String ALFRESCO_QUICK_START = "Alfresco Quick Start";
        private static final String QUICK_START_EDITORIAL = "Quick Start Editorial";

        public static final String ROOT = "root";

        private static final Logger logger = Logger.getLogger(EditingItemsViaAWE.class);
        private static final int MAX_WAIT_TIME_MINUTES = 4;

        private String testName;
        private String siteName;
        private String testUser;

        @Override
        @BeforeClass(alwaysRun = true)
        public void setup() throws Exception
        {

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

                wqsURL = siteName + ":8080/wcmqs";
                logger.info(" wcmqs url : " + wqsURL);
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

        private void navigateToWcmqsHome(String wcmqsURL)
        {
                // ---- Step 1 ----
                // ---- Step action ----
                // Navigate to param1
                // ---- Expected results ----
                // Sample site is opened

                drone.navigateTo(wcmqsURL);
        }

        private WcmqsBlogPostPage openBlogPost(String blogPost)
        {
                // ---- Step 2 ----
                // ---- Step action ----
                // Open param1 blog post;
                // ---- Expected results ----
                // Blog post is opened;

                WcmqsHomePage homePage = new WcmqsHomePage(drone).render();
                homePage.selectMenu(WcmqsBlogPage.BLOG_MENU);
                WcmqsBlogPage blogPage = new WcmqsBlogPage(drone).render();
                blogPage.openBlogPost(blogPost);

                WcmqsLoginPage loginPage = new WcmqsLoginPage(drone).render();
                loginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
                WcmqsBlogPostPage blogPostPage = new WcmqsBlogPostPage(drone).render();
                Assert.assertNotNull(blogPostPage);
                return blogPostPage;
        }

        private WcmqsEditPage createBlogPost(WcmqsBlogPostPage blogPostPage)
        {
                // ---- Step 3 ----
                // ---- Step action ----
                // Click Create Article button near blog post;
                // ---- Expected results ----
                // Create article window is opened;

                WcmqsEditPage editPage = blogPostPage.createArticle().render();
                Assert.assertNotNull(editPage);
                return editPage;
        }

        private void verifyAllFields(WcmqsEditPage editPage)
        {
                // ---- Step 4 ----
                // ---- Step action ----
                // Verify that all fields are displayed correctly incl. Content field;
                // ---- Expected results ----
                // All fields are displayed correctly, article content is displayed in Content field;

                Assert.assertNotNull(editPage.getArticleDetails());
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

        private void fillInNameAndContentFieldsForBlogPost(WcmqsEditPage editPage, String blogPostName, String blogPostTitle, String blogPostContent)
        {
                // ---- Step 5 ----
                // ---- Step action ----
                // Fill in Name field with "test1.html" and content with "Hello, world!" and save;
                // ---- Expected results ----
                // Information is added successfully, data is saved and displayed correctly;

                String foundTitle;
                String foundContent;

                editPage.editName(blogPostName);
                editPage.editTitle(blogPostTitle);
                editPage.insertTextInContent(blogPostContent);
                editPage.clickSubmitButton();

                WcmqsBlogPage newBlogPage = new WcmqsBlogPage(drone).render();
                waitAndOpenBlogPost(newBlogPage, blogPostTitle, MAX_WAIT_TIME_MINUTES);
                WcmqsBlogPostPage newBlogPost = new WcmqsBlogPostPage(drone).render();

                foundTitle = newBlogPost.getTitle();
                foundContent = newBlogPost.getContent();
                Assert.assertEquals(blogPostTitle, foundTitle);
                Assert.assertEquals(blogPostContent, foundContent);
        }

        private void verifyInDocumentLibraryForBlogPost(String siteName, String blogPostName, String blogPostContent)
        {
                // ---- Step 6 ----
                // ---- Step action ----
                // Go to Share "My Web Site" document library
                // (Alfresco Quick Start/Quick Start Editorial/root/blog) and verify test1.html file;
                // ---- Expected results ----
                // Changes made via AWE are dislpayed correctly;

                String foundTitle;
                String foundContent;

                ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
                documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(ALFRESCO_QUICK_START).render();
                documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(QUICK_START_EDITORIAL).render();
                documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(ROOT).render();
                documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(WcmqsBlogPage.BLOG).render();
                DocumentDetailsPage blogPostDetailsPage = documentLibraryPage.selectFile(blogPostName).render();

                foundTitle = blogPostDetailsPage.getDocumentTitle();
                foundContent = blogPostDetailsPage.getDocumentBody();

                Assert.assertEquals(blogPostName, foundTitle);
                Assert.assertTrue(foundContent.contains(blogPostContent));
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

        @Test(groups = "WQS")
        public void AONE_5631() throws Exception
        {
                String blogPostName = testName + "_" + System.currentTimeMillis() + "_name.html";
                String blogPostTitle = testName + "_" + System.currentTimeMillis() + "_title";
                String blogPostContent = testName + "_" + System.currentTimeMillis() + "_content";
                siteName = getSiteName(testName);

                navigateToWcmqsHome(wqsURL);
                WcmqsBlogPostPage blogPostPage = openBlogPost(WcmqsBlogPage.ETHICAL_FUNDS);
                WcmqsEditPage editPage = createBlogPost(blogPostPage);
                verifyAllFields(editPage);
                fillInNameAndContentFieldsForBlogPost(editPage, blogPostName, blogPostTitle, blogPostContent);
                verifyInDocumentLibraryForBlogPost(siteName, blogPostName, blogPostContent);
        }

        @Test(groups = "WQS")
        public void AONE_5632() throws Exception
        {
                String blogPostName = testName + "_" + System.currentTimeMillis() + "_name.html";
                String blogPostTitle = testName + "_" + System.currentTimeMillis() + "_title";
                String blogPostContent = testName + "_" + System.currentTimeMillis() + "_content";

                navigateToWcmqsHome(wqsURL);
                WcmqsBlogPostPage blogPostPage = openBlogPost(WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP);
                WcmqsEditPage editPage = createBlogPost(blogPostPage);
                verifyAllFields(editPage);
                fillInNameAndContentFieldsForBlogPost(editPage, blogPostName, blogPostTitle, blogPostContent);
                verifyInDocumentLibraryForBlogPost(siteName, blogPostName, blogPostContent);
        }

        @Test(groups = "WQS")
        public void AONE_5633() throws Exception
        {
                String blogPostName = testName + "_" + System.currentTimeMillis() + "_name.html";
                String blogPostTitle = testName + "_" + System.currentTimeMillis() + "_title";
                String blogPostContent = testName + "_" + System.currentTimeMillis() + "_content";
                siteName = getSiteName(testName);

                navigateToWcmqsHome(wqsURL);
                WcmqsBlogPostPage blogPostPage = openBlogPost(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS);
                WcmqsEditPage editPage = createBlogPost(blogPostPage);
                verifyAllFields(editPage);
                fillInNameAndContentFieldsForBlogPost(editPage, blogPostName, blogPostTitle, blogPostContent);
                verifyInDocumentLibraryForBlogPost(siteName, blogPostName, blogPostContent);
        }

        private WcmqsNewsArticleDetails openNewsFromCategory(String newsCategory, String newsTitle)
        {
                // ---- Step 2 ----
                // ---- Step action ----
                //  Open param2 (param1);
                // ---- Expected results ----
                // Article is opened;

                WcmqsHomePage homePage = new WcmqsHomePage(drone).render();
                WcmqsNewsPage newsPage = homePage.openNewsPageFolder(newsCategory);
                newsPage.clickNewsByTitle(newsTitle);

                WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone).render();
                wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);

                WcmqsNewsArticleDetails newsArticleDetails = new WcmqsNewsArticleDetails(drone);
                Assert.assertNotNull(newsArticleDetails);
                return newsArticleDetails;
        }

        private WcmqsEditPage createNewsArticle(WcmqsNewsArticleDetails newsArticleDetails)
        {
                // ---- Step 3 ----
                // ---- Step action ----
                //  Click Create article button near article;
                // ---- Expected results ----
                //  Create article window is opened;

                newsArticleDetails.clickCreateButton();
                WcmqsEditPage editPage = new WcmqsEditPage(drone).render();
                Assert.assertNotNull(editPage);
                return editPage;
        }

        /**
         * Method that waits for the news article to appear on the page for maximum minutesToWait
         * and then opens
         *
         * @param newsPage
         * @param newsArticleTitle
         * @param minutesToWait
         */

        private void waitAndOpenNewsArticle(WcmqsNewsPage newsPage, String newsArticleTitle, int minutesToWait)
        {
                int waitInMilliSeconds = 3000;
                int maxTimeWaitInMilliSeconds = 60000 * minutesToWait;
                boolean newsArticleFound = false;

                while (!newsArticleFound && maxTimeWaitInMilliSeconds > 0)
                {
                        try
                        {
                                newsPage.clickNewsByTitle(newsArticleTitle);
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

        private void fillInNameAndContentForNewsArticle(WcmqsEditPage editPage, String newsArticleName, String newsArticleTitle, String newsArticleContent)
        {
                // ---- Step 5 ----
                // ---- Step action ----
                // Fill in Name field with "file1.html" and content with "Hello, world!" and save;
                // ---- Expected results ----
                // Information is added successfully, data is saved and displayed correctly;

                String foundTitle;
                String foundContent;

                editPage.editName(newsArticleName);
                editPage.editTitle(newsArticleTitle);
                editPage.insertTextInContent(newsArticleContent);
                editPage.clickSubmitButton();

                WcmqsNewsPage newNewsPage = new WcmqsNewsPage(drone).render();
                newNewsPage = newNewsPage.openNewsPageFolder(WcmqsNewsPage.GLOBAL);
                waitAndOpenNewsArticle(newNewsPage, newsArticleTitle, MAX_WAIT_TIME_MINUTES);
                WcmqsNewsArticleDetails newNewsArticleDetails = new WcmqsNewsArticleDetails(drone).render();

                foundTitle = newNewsArticleDetails.getTitleOfNewsArticle();
                foundContent = newNewsArticleDetails.getBodyOfNewsArticle();
                Assert.assertEquals(newsArticleTitle, foundTitle);
                Assert.assertEquals(newsArticleContent, foundContent);
        }

        private void verifyInDocumentLibraryForNewsArticle(String newsArticleName, String newsArticleContent, String category)
        {
                // ---- Step 6 ----
                // ---- Step action ----
                // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/news/global)
                // and verify file1.html file;
                // ---- Expected results ----
                // Changes made via AWE are dislpayed correctly, new file "file1.html" is displayed correctly;

                String foundTitle;
                String foundContent;

                ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
                documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(ALFRESCO_QUICK_START).render();
                documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(QUICK_START_EDITORIAL).render();
                documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(ROOT).render();
                documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(WcmqsNewsPage.NEWS).render();
                documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(category).render();
                DocumentDetailsPage blogPostDetailsPage = documentLibraryPage.selectFile(newsArticleName).render();

                foundTitle = blogPostDetailsPage.getDocumentTitle();
                foundContent = blogPostDetailsPage.getDocumentBody();

                Assert.assertEquals(newsArticleName, foundTitle);
                Assert.assertTrue(foundContent.contains(newsArticleContent));
        }

        @Test(groups = "WQS")
        public void AONE_5634() throws Exception
        {
                String newsArticleName = testName + "_" + System.currentTimeMillis() + "_name.html";
                String newsArticleTitle = testName + "_" + System.currentTimeMillis() + "_title";
                String newsArticleContent = testName + "_" + System.currentTimeMillis() + "_content";
                siteName = getSiteName(testName);

                navigateToWcmqsHome(wqsURL);
                WcmqsNewsArticleDetails newsArticleDetails = openNewsFromCategory(WcmqsNewsPage.GLOBAL, WcmqsNewsPage.EUROPE_DEPT_CONCERNS);
                WcmqsEditPage editPage = createNewsArticle(newsArticleDetails);
                verifyAllFields(editPage);
                fillInNameAndContentForNewsArticle(editPage, newsArticleName, newsArticleTitle, newsArticleContent);
                verifyInDocumentLibraryForNewsArticle(newsArticleName, newsArticleContent, WcmqsNewsPage.GLOBAL);
        }

        @Test(groups = "WQS")
        public void AONE_5635() throws Exception
        {
                String newsArticleName = testName + "_" + System.currentTimeMillis() + "_name.html";
                String newsArticleTitle = testName + "_" + System.currentTimeMillis() + "_title";
                String newsArticleContent = testName + "_" + System.currentTimeMillis() + "_content";
                siteName = getSiteName(testName);

                navigateToWcmqsHome(wqsURL);
                WcmqsNewsArticleDetails newsArticleDetails = openNewsFromCategory(WcmqsNewsPage.GLOBAL, WcmqsNewsPage.FTSE_1000);
                WcmqsEditPage editPage = createNewsArticle(newsArticleDetails);
                verifyAllFields(editPage);
                fillInNameAndContentForNewsArticle(editPage, newsArticleName, newsArticleTitle, newsArticleContent);
                verifyInDocumentLibraryForNewsArticle(newsArticleName, newsArticleContent, WcmqsNewsPage.GLOBAL);
        }

        @Test(groups = "WQS")
        public void AONE_5636() throws Exception
        {
                String newsArticleName = testName + "_" + System.currentTimeMillis() + "_name.html";
                String newsArticleTitle = testName + "_" + System.currentTimeMillis() + "_title";
                String newsArticleContent = testName + "_" + System.currentTimeMillis() + "_content";
                siteName = getSiteName(testName);

                navigateToWcmqsHome(wqsURL);
                WcmqsNewsArticleDetails newsArticleDetails = openNewsFromCategory(WcmqsNewsPage.COMPANIES, WcmqsNewsPage.GLOBAL_CAR_INDUSTRY);
                WcmqsEditPage editPage = createNewsArticle(newsArticleDetails);
                verifyAllFields(editPage);
                fillInNameAndContentForNewsArticle(editPage, newsArticleName, newsArticleTitle, newsArticleContent);
                verifyInDocumentLibraryForNewsArticle(newsArticleName, newsArticleContent, WcmqsNewsPage.COMPANIES);
        }

        @Test(groups = "WQS")
        public void AONE_5637() throws Exception
        {
                String newsArticleName = testName + "_" + System.currentTimeMillis() + "_name.html";
                String newsArticleTitle = testName + "_" + System.currentTimeMillis() + "_title";
                String newsArticleContent = testName + "_" + System.currentTimeMillis() + "_content";
                siteName = getSiteName(testName);

                navigateToWcmqsHome(wqsURL);
                WcmqsNewsArticleDetails newsArticleDetails = openNewsFromCategory(WcmqsNewsPage.COMPANIES, WcmqsNewsPage.FRESH_FLIGHT_TO_SWISS);
                WcmqsEditPage editPage = createNewsArticle(newsArticleDetails);
                verifyAllFields(editPage);
                fillInNameAndContentForNewsArticle(editPage, newsArticleName, newsArticleTitle, newsArticleContent);
                verifyInDocumentLibraryForNewsArticle(newsArticleName, newsArticleContent, WcmqsNewsPage.COMPANIES);
        }

        @Test(groups = "WQS")
        public void AONE_5638() throws Exception
        {
                String newsArticleName = testName + "_" + System.currentTimeMillis() + "_name.html";
                String newsArticleTitle = testName + "_" + System.currentTimeMillis() + "_title";
                String newsArticleContent = testName + "_" + System.currentTimeMillis() + "_content";
                siteName = getSiteName(testName);

                navigateToWcmqsHome(wqsURL);
                WcmqsNewsArticleDetails newsArticleDetails = openNewsFromCategory(WcmqsNewsPage.MARKETS, WcmqsNewsPage.INVESTORS_FEAR);
                WcmqsEditPage editPage = createNewsArticle(newsArticleDetails);
                verifyAllFields(editPage);
                fillInNameAndContentForNewsArticle(editPage, newsArticleName, newsArticleTitle, newsArticleContent);
                verifyInDocumentLibraryForNewsArticle(newsArticleName, newsArticleContent, WcmqsNewsPage.MARKETS);
        }

        @Test(groups = "WQS")
        public void AONE_5639() throws Exception
        {
                String newsArticleName = testName + "_" + System.currentTimeMillis() + "_name.html";
                String newsArticleTitle = testName + "_" + System.currentTimeMillis() + "_title";
                String newsArticleContent = testName + "_" + System.currentTimeMillis() + "_content";
                siteName = getSiteName(testName);

                navigateToWcmqsHome(wqsURL);
                WcmqsNewsArticleDetails newsArticleDetails = openNewsFromCategory(WcmqsNewsPage.MARKETS, WcmqsNewsPage.HOUSE_PRICES);
                WcmqsEditPage editPage = createNewsArticle(newsArticleDetails);
                verifyAllFields(editPage);
                fillInNameAndContentForNewsArticle(editPage, newsArticleName, newsArticleTitle, newsArticleContent);
                verifyInDocumentLibraryForNewsArticle(newsArticleName, newsArticleContent, WcmqsNewsPage.COMPANIES);
        }

        @Test(groups = "WQS")
        public void AONE_5640() throws Exception
        {
                String articleName = testName + System.currentTimeMillis() + "_name.html";
                String articleTitle = testName + System.currentTimeMillis() + "_title";
                String articleDescription = testName + System.currentTimeMillis() + "_description";
                String articleContent = testName + System.currentTimeMillis() + "_content";
                siteName = getSiteName(testName);

                ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

                // ---- Step 1 ----
                // ---- Step Actions ----
                // Create an HTML article in Quick Start Editorial > root > news > global(e.g. article10.html).
                // ---- Expected results ----
                // HTML article is successfully created;
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
                documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(ALFRESCO_QUICK_START).render();
                documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(QUICK_START_EDITORIAL).render();
                documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(ROOT).render();
                documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(WcmqsNewsPage.NEWS).render();
                documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(WcmqsNewsPage.GLOBAL).render();

                ContentDetails contentDetails = new ContentDetails();
                contentDetails.setName(articleName);
                contentDetails.setTitle(articleTitle);
                contentDetails.setDescription(articleDescription);
                contentDetails.setContent(articleContent);
                DocumentLibraryPage articlePage = ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.HTML, documentLibraryPage);
                Assert.assertNotNull(articlePage);

                // ---- Step 2 ----
                // ---- Step actions ----
                // Navigate to Quick Start Editorial > root > news > global > collections > section.articles.
                // ---- Expected results ----
                // Section.articles folder is opened;
                ShareUser.openSiteDashboard(drone, siteName);
                documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
                documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(ALFRESCO_QUICK_START).render();
                documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(QUICK_START_EDITORIAL).render();
                documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(ROOT).render();
                documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(WcmqsNewsPage.NEWS).render();
                documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(WcmqsNewsPage.GLOBAL).render();
                documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(WcmqsNewsPage.COLLECTIONS);
                Assert.assertNotNull(documentLibraryPage);

                // Wait 2 minutes to allow refresh query to execute
                webDriverWait(drone, 1000 * 120);

                // ---- Step 3 ----
                // ---- Step actions ----
                // Click Edit Metadata button;
                // ---- Expected results
                // Edit metadata form is opened;
                FileDirectoryInfo folderInfo = documentLibraryPage.getFileDirectoryInfo(WcmqsNewsPage.SECTION_ARTICLES);
                EditDocumentPropertiesPage editDocumentPropertiesPage = folderInfo.selectEditProperties().render();
                Assert.assertNotNull(editDocumentPropertiesPage);

                // ---- Step 4 ----
                // ---- Step actions ----
                // Verify the presense of arcticle10.html in Web Assets section;
                // ---- Expected results ----
                // Article10.html file is present in Web Assets section;
                List<String> foundAssets = editDocumentPropertiesPage.getWebAssets();
                Assert.assertTrue(foundAssets.contains(articleName));
        }
}

