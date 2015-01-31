package org.alfresco.share.enterprise.wqs.web.awe;

import org.alfresco.po.share.dashlet.SiteWebQuickStartDashlet;
import org.alfresco.po.share.dashlet.WebQuickStartOptions;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.wqs.*;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.*;

/**
 * Created by Lucian Tuca on 11/17/2014.
 */
@Listeners(FailedTestListener.class)
public class CreatingItemsViaAWE extends AbstractUtils
{
        public static final String WCMQS_URL = "http://lucian:8080/wcmqs/";
        public static final String ALFRESCO_QUICK_START = "Alfresco Quick Start";
        public static final String QUICK_START_EDITORIAL = "Quick Start Editorial";
        public static final String ROOT = "root";

        private static final Logger logger = Logger.getLogger(EditingItemsViaAWE.class);

        private String testName;
        private String siteName;
        private String testUser;

        @Override
        @BeforeClass(alwaysRun = true)
        public void setup() throws Exception
        {
                super.setup();
                testName = this.getClass().getSimpleName();
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
                drone.quit();
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
                // Wait a lot here (more than 4 minutes)
                newBlogPage.openBlogPost(blogPostTitle);
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
                testUser = getUserNameForDomain(testName, DOMAIN_FREE);
                siteName = getSiteName(testName);

                // ---- Step 1 ----
                // ---- Step Action -----
                // WCM Quick Start is installed; - is not required to be executed automatically

                // ---- Step 2 ----
                // ---- Step Action -----
                // Site "My Web Site" is created in Alfresco Share;
                ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
                ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

                // ---- Step 3 ----
                // ---- Step Action -----
                // WCM Quick Start Site Data is imported;
                SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlets.WEB_QUICK_START);

                SiteWebQuickStartDashlet wqsDashlet = siteDashBoard.getDashlet(SITE_WEB_QUICK_START_DASHLET).render();
                wqsDashlet.selectWebsiteDataOption(WebQuickStartOptions.FINANCE);
                wqsDashlet.clickImportButtton();
                wqsDashlet.waitForImportMessage();
        }

        @Test(groups = "WQS")
        public void AONE_5631() throws Exception
        {
                String blogPostName = testName + "_" + System.currentTimeMillis() + "_name.html";
                String blogPostTitle = testName + "_" + System.currentTimeMillis() + "_title";
                String blogPostContent = testName + "_" + System.currentTimeMillis() + "_content";
                siteName = getSiteName(testName);

                navigateToWcmqsHome(WCMQS_URL);
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
                siteName = getSiteName(testName);

                navigateToWcmqsHome(WCMQS_URL);
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

                navigateToWcmqsHome(WCMQS_URL);
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
                // Wait a lot here (more than 4 minutes)
                newNewsPage.clickNewsByTitle(newsArticleTitle);
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

                navigateToWcmqsHome(WCMQS_URL);
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

                navigateToWcmqsHome(WCMQS_URL);
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

                navigateToWcmqsHome(WCMQS_URL);
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

                navigateToWcmqsHome(WCMQS_URL);
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

                navigateToWcmqsHome(WCMQS_URL);
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

                navigateToWcmqsHome(WCMQS_URL);
                WcmqsNewsArticleDetails newsArticleDetails = openNewsFromCategory(WcmqsNewsPage.MARKETS, WcmqsNewsPage.HOUSE_PRICES);
                WcmqsEditPage editPage = createNewsArticle(newsArticleDetails);
                verifyAllFields(editPage);
                fillInNameAndContentForNewsArticle(editPage, newsArticleName, newsArticleTitle, newsArticleContent);
                verifyInDocumentLibraryForNewsArticle(newsArticleName, newsArticleContent, WcmqsNewsPage.COMPANIES);
        }

}

