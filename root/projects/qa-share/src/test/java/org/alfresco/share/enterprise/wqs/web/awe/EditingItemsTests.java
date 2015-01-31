package org.alfresco.share.enterprise.wqs.web.awe;

import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.dashlet.SiteWebQuickStartDashlet;
import org.alfresco.po.share.dashlet.WebQuickStartOptions;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
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
import java.util.Map;

@Listeners(FailedTestListener.class)
public class EditingItemsTests extends AbstractUtils
{
        private String testName;
        private String wqsURL;
        private String siteName;
        private String ipAddress;
        private String hostName;
        public static final String ALFRESCO_QUICK_START = "Alfresco Quick Start";
        public static final String QUICK_START_EDITORIAL = "Quick Start Editorial";
        public static final String ROOT = "root";
        public static final String BLOG = "blog";
        public static final String BLOG_FILE1 = "blog1.html";
        public static final String BLOG_FILE2 = "blog2.html";
        public static final String BLOG_FILE3 = "blog3.html";
        public static final String NEWS_FILE1 = "article1.html";
        public static final String NEWS_FILE2 = "article2.html";
        public static final String NEWS_FILE3 = "article3.html";
        public static final String NEWS_FILE4 = "article4.html";
        public static final String NEWS_FILE5 = "article5.html";
        public static final String NEWS_FILE6 = "article6.html";
        public static final String SLIDE_FILE1 = "slide1.html";
        public static final String SLIDE_FILE2 = "slide2.html";
        public static final String SLIDE_FILE3 = "slide3.html";
        private static final Logger logger = Logger.getLogger(EditingItemsViaAWE.class);
        private String newTitle = " title edited";
        private String newDescription = " description edited";
        private String newContent = "content edited";

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
                drone.quit();
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
                String setHostAddress = "cmd.exe /c echo. >> %WINDIR%\\System32\\Drivers\\Etc\\hosts && echo " + ipAddress + " " + siteName
                        + " >> %WINDIR%\\System32\\Drivers\\Etc\\hosts";
                Runtime.getRuntime().exec(setHostAddress);

        }

        /**
         * AONE-5619:Editing "Ethical funds" blog post
         */
        @Test(groups = { "WQS", "EnterpriseOnly" })
        public void AONE_5619() throws Exception
        {
                String blogName = "Ethical funds";
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
                // Blog post is opened;
                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                wcmqsHomePage.render();
                wcmqsHomePage.selectMenu("blog");

                WcmqsBlogPage blogsPage = new WcmqsBlogPage(drone);
                blogsPage.render();
                blogsPage.openBlogPost(blogName);

                // Login
                WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
                wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
                WcmqsBlogPostPage blogPostPage = new WcmqsBlogPostPage(drone);
                blogPostPage.render();
                Assert.assertTrue(blogPostPage.getTitle().contains(blogName), "Blog :" + blogName + " was not found.");
                // ---- Step 3 ----
                // ---- Step action ---
                // Click Edit button near blog post;
                // ---- Expected results ----
                // Edit window is opened;
                WcmqsEditPage wcmqsEditPage = blogPostPage.clickEditButton();
                wcmqsEditPage.render();

                // ---- Step 4 ----
                // ---- Step action ---
                // Verify that all fields are displayed correctly incl. Content field;
                // ---- Expected results ----
                // All fields are displayed correctly, article content is displayed in Content field;
                Assert.assertTrue(wcmqsEditPage.isNameFieldDisplayed(), "Name field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isTitleFieldDisplayed(), "Title field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isDescriptionFieldDisplayed(), "Description field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isContentFrameDisplayed(), "Content frame is not displayed");
                Assert.assertTrue(wcmqsEditPage.isTemplateNameDisplayed(), "Template Name field is not displayed");
                String contentText = wcmqsEditPage.getContentTinyMCEEditor().getContent();
                Assert.assertFalse(contentText.isEmpty(), "Content field is empty");
                Assert.assertFalse(contentText.contains(newContent), "Content field contains new added information.");

                // ---- Step 5 ----
                // ---- Step action ---
                // Change information in all fields and save it;
                // ---- Expected results ----
                // Information is changed successfully, data is saved and displayed correctly;
                //        String newTemplateName="template";
                wcmqsEditPage.editTitle(newTitle);
                wcmqsEditPage.editDescription(newDescription);
                wcmqsEditPage.insertTextInContent(newContent);
                //        wcmqsEditPage.editTemplateName(newTemplateName);
                wcmqsEditPage.clickSubmitButton();

                WcmqsBlogPage blogsPage2 = new WcmqsBlogPage(drone);
                blogsPage2.render();
                Assert.assertTrue(blogsPage2.isBlogDisplayed(newTitle), "Title of blog is not edited.");

                // ---- Step 6 ----
                // ---- Step action ---
                // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/blog) and verify blog1.html file;
                // ---- Expected results ----
                // Changes made via AWE are dislpayed correctly
                ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
                documentLibraryPage = navigateToWqsFolderFromRoot(documentLibraryPage, "blog");

                FileDirectoryInfo fileInfo = documentLibraryPage.getFileDirectoryInfo(BLOG_FILE1);
                Assert.assertTrue(fileInfo.getTitle().contains(newTitle), "Title of blog " + BLOG_FILE1 + " was not updated.");
                Assert.assertTrue(fileInfo.getDescription().contains(newDescription), "Description of blog " + BLOG_FILE1 + " was not updated.");

                DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(BLOG_FILE1).render();
                Assert.assertTrue(docDetailsPage.getDocumentBody().contains(newContent), "Content of blog " + BLOG_FILE1 + " was not updated.");
                Map<String, Object> properties = docDetailsPage.getProperties();
                Assert.assertNotNull(properties);
                Assert.assertEquals(properties.get("Name"), BLOG_FILE1, "Name Property is not " + BLOG_FILE1);
                Assert.assertTrue(properties.get("Title").toString().contains(newTitle), "Title of blog " + BLOG_FILE1 + " was not updated.");
                Assert.assertTrue(properties.get("Description").toString().contains(newDescription), "Description of blog " + BLOG_FILE1 + " was not updated.");
                //        Assert.assertTrue(properties.get("Template Name").toString().contains(newTemplateName),"Template name of blog "+BLOG_FILE1+" was not updated.");

        }

        /**
         * AONE-5620:Editing "Company organises workshop" blog post
         */
        @Test(groups = { "WQS", "EnterpriseOnly" })
        public void AONE_5620() throws Exception
        {
                //to be deleted
                String siteName = "Share-55952SiteName";
                String blogName = "Company organises workshop";

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
                // Blog post is opened;
                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                wcmqsHomePage.render();
                wcmqsHomePage.selectMenu("blog");

                WcmqsBlogPage blogsPage = new WcmqsBlogPage(drone);
                blogsPage.render();
                blogsPage.openBlogPost(blogName);

                // Login
                WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
                wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
                WcmqsBlogPostPage blogPostPage = new WcmqsBlogPostPage(drone);
                blogPostPage.render();
                Assert.assertTrue(blogPostPage.getTitle().contains(blogName), "Blog :" + blogName + " was not found.");

                // ---- Step 3 ----
                // ---- Step action ---
                // Click Edit button near blog post;
                // ---- Expected results ----
                // Edit window is opened;
                WcmqsEditPage wcmqsEditPage = blogPostPage.clickEditButton();
                wcmqsEditPage.render();

                // ---- Step 4 ----
                // ---- Step action ---
                // Verify that all fields are displayed correctly incl. Content field;
                // ---- Expected results ----
                // All fields are displayed correctly, article content is displayed in Content field;
                Assert.assertTrue(wcmqsEditPage.isNameFieldDisplayed(), "Name field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isTitleFieldDisplayed(), "Title field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isDescriptionFieldDisplayed(), "Description field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isContentFrameDisplayed(), "Content frame is not displayed");
                Assert.assertTrue(wcmqsEditPage.isTemplateNameDisplayed(), "Template Name field is not displayed");
                String contentText = wcmqsEditPage.getContentTinyMCEEditor().getContent();
                Assert.assertFalse(contentText.isEmpty(), "Content field is empty");
                Assert.assertFalse(contentText.contains(newContent), "Content field contains new added information.");

                // ---- Step 5 ----
                // ---- Step action ---
                // Change information in all fields and save it;
                // ---- Expected results ----
                // Information is changed successfully, data is saved and displayed correctly;
                //        String newTemplateName="template";
                wcmqsEditPage.editTitle(newTitle);
                wcmqsEditPage.editDescription(newDescription);
                wcmqsEditPage.insertTextInContent(newContent);
                //        wcmqsEditPage.editTemplateName(newTemplateName);
                wcmqsEditPage.clickSubmitButton();

                WcmqsBlogPage blogsPage2 = new WcmqsBlogPage(drone);
                blogsPage2.render();
                Assert.assertTrue(blogsPage2.isBlogDisplayed(newTitle), "Title of blog is not edited.");

                // ---- Step 6 ----
                // ---- Step action ---
                // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/blog) and verify blog2.html file;
                // ---- Expected results ----
                // Changes made via AWE are dislpayed correctly
                ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
                DocumentLibraryPage documentLibraryPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);
                documentLibraryPage = navigateToWqsFolderFromRoot(documentLibraryPage, "blog");

                FileDirectoryInfo fileInfo = documentLibraryPage.getFileDirectoryInfo(BLOG_FILE2);
                Assert.assertTrue(fileInfo.getTitle().contains(newTitle), "Title of blog " + BLOG_FILE2 + " was not updated.");
                Assert.assertTrue(fileInfo.getDescription().contains(newDescription), "Description of blog " + BLOG_FILE2 + " was not updated.");

                DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(BLOG_FILE2).render();
                Assert.assertTrue(docDetailsPage.getDocumentBody().contains(newContent), "Content of blog " + BLOG_FILE2 + " was not updated.");
                Map<String, Object> properties = docDetailsPage.getProperties();
                Assert.assertNotNull(properties);
                Assert.assertEquals(properties.get("Name"), BLOG_FILE2, "Name Property is not " + BLOG_FILE2);
                Assert.assertTrue(properties.get("Title").toString().contains(newTitle), "Title of blog " + BLOG_FILE2 + " was not updated.");
                Assert.assertTrue(properties.get("Description").toString().contains(newDescription), "Description of blog " + BLOG_FILE2 + " was not updated.");
                //        Assert.assertTrue(properties.get("TemplateName").toString().contains(newTemplateName),"Template name of blog "+BLOG_FILE2+" was not updated.");

        }

        /**
         * AONE-5621:Editing "Our top analyst's latest..." blog post
         */
        @Test(groups = { "WQS", "EnterpriseOnly" })
        public void AONE_5621() throws Exception
        {
                String blogName = "Our top analyst's latest thoughts";

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
                // Blog post is opened;
                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                wcmqsHomePage.render();
                wcmqsHomePage.selectMenu("blog");

                WcmqsBlogPage blogsPage = new WcmqsBlogPage(drone);
                blogsPage.render();
                blogsPage.openBlogPost(blogName);

                // Login
                WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
                wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
                WcmqsBlogPostPage blogPostPage = new WcmqsBlogPostPage(drone);
                blogPostPage.render();
                Assert.assertTrue(blogPostPage.getTitle().contains(blogName), "Blog :" + blogName + " was not found.");

                // ---- Step 3 ----
                // ---- Step action ---
                // Click Edit button near blog post;
                // ---- Expected results ----
                // Edit window is opened;
                WcmqsEditPage wcmqsEditPage = blogPostPage.clickEditButton();
                wcmqsEditPage.render();

                // ---- Step 4 ----
                // ---- Step action ---
                // Verify that all fields are displayed correctly incl. Content field;
                // ---- Expected results ----
                // All fields are displayed correctly, article content is displayed in Content field;
                Assert.assertTrue(wcmqsEditPage.isNameFieldDisplayed(), "Name field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isTitleFieldDisplayed(), "Title field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isDescriptionFieldDisplayed(), "Description field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isContentFrameDisplayed(), "Content frame is not displayed");
                Assert.assertTrue(wcmqsEditPage.isTemplateNameDisplayed(), "Template Name field is not displayed");
                String contentText = wcmqsEditPage.getContentTinyMCEEditor().getContent();
                Assert.assertFalse(contentText.isEmpty(), "Content field is empty");
                Assert.assertFalse(contentText.contains(newContent), "Content field contains new added information.");

                // ---- Step 5 ----
                // ---- Step action ---
                // Change information in all fields and save it;
                // ---- Expected results ----
                // Information is changed successfully, data is saved and displayed correctly;
                //        String newTemplateName="template";
                wcmqsEditPage.editTitle(newTitle);
                wcmqsEditPage.editDescription(newDescription);
                wcmqsEditPage.insertTextInContent(newContent);
                //        wcmqsEditPage.editTemplateName(newTemplateName);
                wcmqsEditPage.clickSubmitButton();

                WcmqsBlogPage blogsPage2 = new WcmqsBlogPage(drone);
                blogsPage2.render();
                Assert.assertTrue(blogsPage2.isBlogDisplayed(newTitle), "Title of blog is not edited.");

                // ---- Step 6 ----
                // ---- Step action ---
                // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/blog) and verify blog3.html file;
                // ---- Expected results ----
                // Changes made via AWE are dislpayed correctly
                ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
                DocumentLibraryPage documentLibraryPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);
                documentLibraryPage = navigateToWqsFolderFromRoot(documentLibraryPage, "blog");

                FileDirectoryInfo fileInfo = documentLibraryPage.getFileDirectoryInfo(BLOG_FILE3);
                Assert.assertTrue(fileInfo.getTitle().contains(newTitle), "Title of blog " + BLOG_FILE3 + " was not updated.");
                Assert.assertTrue(fileInfo.getDescription().contains(newDescription), "Description of blog " + BLOG_FILE3 + " was not updated.");

                DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(BLOG_FILE3).render();
                Assert.assertTrue(docDetailsPage.getDocumentBody().contains(newContent), "Content of blog " + BLOG_FILE3 + " was not updated.");
                Map<String, Object> properties = docDetailsPage.getProperties();
                Assert.assertNotNull(properties);
                Assert.assertEquals(properties.get("Name"), BLOG_FILE3, "Name Property is not " + BLOG_FILE3);
                Assert.assertTrue(properties.get("Title").toString().contains(newTitle), "Title of blog " + BLOG_FILE3 + " was not updated.");
                Assert.assertTrue(properties.get("Description").toString().contains(newDescription), "Description of blog " + BLOG_FILE3 + " was not updated.");
                //        Assert.assertTrue(properties.get("TemplateName").toString().contains(newTemplateName),"Template name of blog "+BLOG_FILE3+" was not updated.");

        }

        /**
         * AONE-5622:Editing "Europe dept...."article (Global economy)
         */
        @Test(groups = { "WQS", "EnterpriseOnly" })
        public void AONE_5622() throws Exception
        {
                String newsName = "Europe debt concerns ease but bank fears remain";

                // ---- Step 1 ----
                // ---- Step action ---
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened
                drone.navigateTo(wqsURL);

                // ---- Step 2 ----
                // ---- Step action ---
                // Open "Europe dept concerns ease but bank fears remain" article in Global economy (News);
                // ---- Expected results ----
                // Article is opened;
                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                wcmqsHomePage.render();
                WcmqsNewsPage newsPage = wcmqsHomePage.openNewsPageFolder("global").render();

                newsPage.clickNewsByTitle(newsName);

                // Login
                WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
                wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
                WcmqsNewsArticleDetails newsArticlePage = new WcmqsNewsArticleDetails(drone);
                newsArticlePage.render();
                Assert.assertTrue(newsArticlePage.getTitle().contains(newsName), "News article: " + newsName + " was not found.");

                // ---- Step 3 ----
                // ---- Step action ---
                // Click Edit button near article;
                // ---- Expected results ----
                // Edit window is opened;
                WcmqsEditPage wcmqsEditPage = newsArticlePage.clickEditButton();
                wcmqsEditPage.render();

                // ---- Step 4 ----
                // ---- Step action ---
                // Verify that all fields are displayed correctly incl. Content field;
                // ---- Expected results ----
                // All fields are displayed correctly, article content is displayed in Content field;
                Assert.assertTrue(wcmqsEditPage.isNameFieldDisplayed(), "Name field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isTitleFieldDisplayed(), "Title field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isDescriptionFieldDisplayed(), "Description field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isContentFrameDisplayed(), "Content frame is not displayed");
                Assert.assertTrue(wcmqsEditPage.isTemplateNameDisplayed(), "Template Name field is not displayed");
                String contentText = wcmqsEditPage.getContentTinyMCEEditor().getContent();
                Assert.assertFalse(contentText.isEmpty(), "Content field is empty");
                Assert.assertFalse(contentText.contains(newContent), "Content field contains new added information.");

                // ---- Step 5 ----
                // ---- Step action ---
                // Change information in all fields and save it;
                // ---- Expected results ----
                // Information is changed successfully, data is saved and displayed correctly;
                //        String newTemplateName="template";
                wcmqsEditPage.editTitle(newTitle);
                wcmqsEditPage.editDescription(newDescription);
                wcmqsEditPage.insertTextInContent(newContent);
                //        wcmqsEditPage.editTemplateName(newTemplateName);
                wcmqsEditPage.clickSubmitButton();

                WcmqsNewsPage newsPage2 = new WcmqsNewsPage(drone);
                newsPage2.render();
                Assert.assertTrue(newsPage2.checkIfNewsExists(newTitle), "Title of blog is not edited.");

                // ---- Step 6 ----
                // ---- Step action ---
                // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/news/global) and verify article4.html file;
                // ---- Expected results ----
                // Changes made via AWE are dislpayed correctly
                ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
                DocumentLibraryPage documentLibraryPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);
                documentLibraryPage = navigateToWqsFolderFromRoot(documentLibraryPage, "news");
                documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder("global").render();

                FileDirectoryInfo fileInfo = documentLibraryPage.getFileDirectoryInfo(NEWS_FILE4);
                Assert.assertTrue(fileInfo.getTitle().contains(newTitle), "Title of blog " + NEWS_FILE4 + " was not updated.");
                Assert.assertTrue(fileInfo.getDescription().contains(newDescription), "Description of blog " + NEWS_FILE4 + " was not updated.");

                DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(NEWS_FILE4).render();
                Assert.assertTrue(docDetailsPage.getDocumentBody().contains(newContent), "Content of blog " + NEWS_FILE4 + " was not updated.");
                Map<String, Object> properties = docDetailsPage.getProperties();
                Assert.assertNotNull(properties);
                Assert.assertEquals(properties.get("Name"), NEWS_FILE4, "Name Property is not " + NEWS_FILE4);
                Assert.assertTrue(properties.get("Title").toString().contains(newTitle), "Title of blog " + NEWS_FILE4 + " was not updated.");
                Assert.assertTrue(properties.get("Description").toString().contains(newDescription), "Description of blog " + NEWS_FILE4 + " was not updated.");
                //        Assert.assertTrue(properties.get("TemplateName").toString().contains(newTemplateName),"Template name of blog "+NEWS_FILE4+" was not updated.");

        }

        private DocumentLibraryPage navigateToWqsFolderFromRoot(DocumentLibraryPage documentLibraryPage, String folderName)
        {
                documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(ALFRESCO_QUICK_START).render();
                documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(QUICK_START_EDITORIAL).render();
                documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(ROOT).render();
                documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(folderName).render();
                return documentLibraryPage;
        }

        /**
         * AONE-5623:Editing "Media Consult new site...."article (Global economy)
         */
        @Test(groups = { "WQS", "EnterpriseOnly" })
        public void AONE_5623() throws Exception
        {
                String newsName = "FTSE 100 rallies from seven-week low";

                // ---- Step 1 ----
                // ---- Step action ---
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened
                drone.navigateTo(wqsURL);

                // ---- Step 2 ----
                // ---- Step action ---
                // Open "Media Consult new site coming out in September" article in Global economy (News);
                // ---- Expected results ----
                // Article is opened;
                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                wcmqsHomePage.render();
                WcmqsNewsPage newsPage = wcmqsHomePage.openNewsPageFolder("global").render();

                newsPage.clickNewsByTitle(newsName);

                // Login
                WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
                wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
                WcmqsNewsArticleDetails newsArticlePage = new WcmqsNewsArticleDetails(drone);
                newsArticlePage.render();
                Assert.assertTrue(newsArticlePage.getTitle().contains(newsName), "News article: " + newsName + " was not found.");

                // ---- Step 3 ----
                // ---- Step action ---
                // Click Edit button near article;
                // ---- Expected results ----
                // Edit window is opened;
                WcmqsEditPage wcmqsEditPage = newsArticlePage.clickEditButton();
                wcmqsEditPage.render();

                // ---- Step 4 ----
                // ---- Step action ---
                // Verify that all fields are displayed correctly incl. Content field;
                // ---- Expected results ----
                // All fields are displayed correctly, article content is displayed in Content field;
                Assert.assertTrue(wcmqsEditPage.isNameFieldDisplayed(), "Name field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isTitleFieldDisplayed(), "Title field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isDescriptionFieldDisplayed(), "Description field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isContentFrameDisplayed(), "Content frame is not displayed");
                Assert.assertTrue(wcmqsEditPage.isTemplateNameDisplayed(), "Template Name field is not displayed");
                String contentText = wcmqsEditPage.getContentTinyMCEEditor().getContent();
                Assert.assertFalse(contentText.isEmpty(), "Content field is empty");
                Assert.assertFalse(contentText.contains(newContent), "Content field contains new added information.");

                // ---- Step 5 ----
                // ---- Step action ---
                // Change information in all fields and save it;
                // ---- Expected results ----
                // Information is changed successfully, data is saved and displayed correctly;
                //        String newTemplateName="template";
                wcmqsEditPage.editTitle(newTitle);
                wcmqsEditPage.editDescription(newDescription);
                wcmqsEditPage.insertTextInContent(newContent);
                //        wcmqsEditPage.editTemplateName(newTemplateName);
                wcmqsEditPage.clickSubmitButton();

                WcmqsNewsPage newsPage2 = new WcmqsNewsPage(drone);
                newsPage2.render();
                Assert.assertTrue(newsPage2.checkIfNewsExists(newTitle), "Title of blog is not edited.");

                // ---- Step 6 ----
                // ---- Step action ---
                // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/news/global) and verify article3.html file;
                // ---- Expected results ----
                // Changes made via AWE are dislpayed correctly
                ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
                DocumentLibraryPage documentLibraryPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);
                documentLibraryPage = navigateToWqsFolderFromRoot(documentLibraryPage, "news");
                documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder("global").render();

                FileDirectoryInfo fileInfo = documentLibraryPage.getFileDirectoryInfo(NEWS_FILE3);
                Assert.assertTrue(fileInfo.getTitle().contains(newTitle), "Title of blog " + NEWS_FILE3 + " was not updated.");
                Assert.assertTrue(fileInfo.getDescription().contains(newDescription), "Description of blog " + NEWS_FILE3 + " was not updated.");

                DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(NEWS_FILE3).render();
                Assert.assertTrue(docDetailsPage.getDocumentBody().contains(newContent), "Content of blog " + NEWS_FILE3 + " was not updated.");
                Map<String, Object> properties = docDetailsPage.getProperties();
                Assert.assertNotNull(properties);
                Assert.assertEquals(properties.get("Name"), NEWS_FILE3, "Name Property is not " + NEWS_FILE3);
                Assert.assertTrue(properties.get("Title").toString().contains(newTitle), "Title of blog " + NEWS_FILE3 + " was not updated.");
                Assert.assertTrue(properties.get("Description").toString().contains(newDescription), "Description of blog " + NEWS_FILE3 + " was not updated.");
                //        Assert.assertTrue(properties.get("TemplateName").toString().contains(newTemplateName),"Template name of blog "+NEWS_FILE3+" was not updated.");

        }

        /**
         * AONE-5624:Editing "China eyes shake-up...."article (Companies)
         */
        @Test(groups = { "WQS", "EnterpriseOnly" })
        public void AONE_5624() throws Exception
        {
                String newsName = "Global car industry";

                // ---- Step 1 ----
                // ---- Step action ---
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened
                drone.navigateTo(wqsURL);

                // ---- Step 2 ----
                // ---- Step action ---
                // Open "China eyes shake-up of bank holding" article in Company News (News);
                // ---- Expected results ----
                // Article is opened;
                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                wcmqsHomePage.render();
                WcmqsNewsPage newsPage = wcmqsHomePage.openNewsPageFolder("companies").render();

                newsPage.clickNewsByTitle(newsName);

                // Login
                WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
                wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
                WcmqsNewsArticleDetails newsArticlePage = new WcmqsNewsArticleDetails(drone);
                newsArticlePage.render();
                Assert.assertTrue(newsArticlePage.getTitle().contains(newsName), "News article: " + newsName + " was not found.");

                // ---- Step 3 ----
                // ---- Step action ---
                // Click Edit button near article;
                // ---- Expected results ----
                // Edit window is opened;
                WcmqsEditPage wcmqsEditPage = newsArticlePage.clickEditButton();
                wcmqsEditPage.render();

                // ---- Step 4 ----
                // ---- Step action ---
                // Verify that all fields are displayed correctly incl. Content field;
                // ---- Expected results ----
                // All fields are displayed correctly, article content is displayed in Content field;
                Assert.assertTrue(wcmqsEditPage.isNameFieldDisplayed(), "Name field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isTitleFieldDisplayed(), "Title field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isDescriptionFieldDisplayed(), "Description field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isContentFrameDisplayed(), "Content frame is not displayed");
                Assert.assertTrue(wcmqsEditPage.isTemplateNameDisplayed(), "Template Name field is not displayed");
                String contentText = wcmqsEditPage.getContentTinyMCEEditor().getContent();
                Assert.assertFalse(contentText.isEmpty(), "Content field is empty");
                Assert.assertFalse(contentText.contains(newContent), "Content field contains new added information.");

                // ---- Step 5 ----
                // ---- Step action ---
                // Change information in all fields and save it;
                // ---- Expected results ----
                // Information is changed successfully, data is saved and displayed correctly;
                //        String newTemplateName="template";
                wcmqsEditPage.editTitle(newTitle);
                wcmqsEditPage.editDescription(newDescription);
                wcmqsEditPage.insertTextInContent(newContent);
                //        wcmqsEditPage.editTemplateName(newTemplateName);
                wcmqsEditPage.clickSubmitButton();

                WcmqsNewsPage newsPage2 = new WcmqsNewsPage(drone);
                newsPage2.render();
                Assert.assertTrue(newsPage2.checkIfNewsExists(newTitle), "Title of blog is not edited.");

                // ---- Step 6 ----
                // ---- Step action ---
                // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/news/companies) and verify article2.html file;
                // ---- Expected results ----
                // Changes made via AWE are dislpayed correctly
                ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
                DocumentLibraryPage documentLibraryPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);
                documentLibraryPage = navigateToWqsFolderFromRoot(documentLibraryPage, "news");
                documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder("companies").render();

                FileDirectoryInfo fileInfo = documentLibraryPage.getFileDirectoryInfo(NEWS_FILE2);
                Assert.assertTrue(fileInfo.getTitle().contains(newTitle), "Title of blog " + NEWS_FILE2 + " was not updated.");
                Assert.assertTrue(fileInfo.getDescription().contains(newDescription), "Description of blog " + NEWS_FILE2 + " was not updated.");

                DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(NEWS_FILE2).render();
                Assert.assertTrue(docDetailsPage.getDocumentBody().contains(newContent), "Content of blog " + NEWS_FILE2 + " was not updated.");
                Map<String, Object> properties = docDetailsPage.getProperties();
                Assert.assertNotNull(properties);
                Assert.assertEquals(properties.get("Name"), NEWS_FILE2, "Name Property is not " + NEWS_FILE2);
                Assert.assertTrue(properties.get("Title").toString().contains(newTitle), "Title of blog " + NEWS_FILE2 + " was not updated.");
                Assert.assertTrue(properties.get("Description").toString().contains(newDescription), "Description of blog " + NEWS_FILE2 + " was not updated.");
                //        Assert.assertTrue(properties.get("TemplateName").toString().contains(newTemplateName),"Template name of blog "+NEWS_FILE2+" was not updated.");

        }

        /**
         * AONE-5625:Editing "Minicards are now available" article (Companies)
         */
        @Test(groups = { "WQS", "EnterpriseOnly" })
        public void AONE_5625() throws Exception
        {
                String newsName = "Fresh flight to Swiss franc as Europe's bond strains return";

                // ---- Step 1 ----
                // ---- Step action ---
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened
                drone.navigateTo(wqsURL);

                // ---- Step 2 ----
                // ---- Step action ---
                // Open "Minicards are now available" article in Company News (News);
                // ---- Expected results ----
                // Article is opened;
                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                wcmqsHomePage.render();
                WcmqsNewsPage newsPage = wcmqsHomePage.openNewsPageFolder("companies").render();

                newsPage.clickNewsByTitle(newsName);

                // Login
                WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
                wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
                WcmqsNewsArticleDetails newsArticlePage = new WcmqsNewsArticleDetails(drone);
                newsArticlePage.render();
                Assert.assertTrue(newsArticlePage.getTitle().contains(newsName), "News article: " + newsName + " was not found.");

                // ---- Step 3 ----
                // ---- Step action ---
                // Click Edit button near article;
                // ---- Expected results ----
                // Edit window is opened;
                WcmqsEditPage wcmqsEditPage = newsArticlePage.clickEditButton();
                wcmqsEditPage.render();

                // ---- Step 4 ----
                // ---- Step action ---
                // Verify that all fields are displayed correctly incl. Content field;
                // ---- Expected results ----
                // All fields are displayed correctly, article content is displayed in Content field;
                Assert.assertTrue(wcmqsEditPage.isNameFieldDisplayed(), "Name field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isTitleFieldDisplayed(), "Title field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isDescriptionFieldDisplayed(), "Description field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isContentFrameDisplayed(), "Content frame is not displayed");
                Assert.assertTrue(wcmqsEditPage.isTemplateNameDisplayed(), "Template Name field is not displayed");
                String contentText = wcmqsEditPage.getContentTinyMCEEditor().getContent();
                Assert.assertFalse(contentText.isEmpty(), "Content field is empty");
                Assert.assertFalse(contentText.contains(newContent), "Content field contains new added information.");

                // ---- Step 5 ----
                // ---- Step action ---
                // Change information in all fields and save it;
                // ---- Expected results ----
                // Information is changed successfully, data is saved and displayed correctly;
                //        String newTemplateName="template";
                wcmqsEditPage.editTitle(newTitle);
                wcmqsEditPage.editDescription(newDescription);
                wcmqsEditPage.insertTextInContent(newContent);
                //        wcmqsEditPage.editTemplateName(newTemplateName);
                wcmqsEditPage.clickSubmitButton();

                WcmqsNewsPage newsPage2 = new WcmqsNewsPage(drone);
                newsPage2.render();
                Assert.assertTrue(newsPage2.checkIfNewsExists(newTitle), "Title of blog is not edited.");

                // ---- Step 6 ----
                // ---- Step action ---
                // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/news/companies) and verify article1.html file;
                // ---- Expected results ----
                // Changes made via AWE are dislpayed correctly
                ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
                DocumentLibraryPage documentLibraryPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);
                documentLibraryPage = navigateToWqsFolderFromRoot(documentLibraryPage, "news");
                documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder("companies").render();

                FileDirectoryInfo fileInfo = documentLibraryPage.getFileDirectoryInfo(NEWS_FILE1);
                Assert.assertTrue(fileInfo.getTitle().contains(newTitle), "Title of blog " + NEWS_FILE1 + " was not updated.");
                Assert.assertTrue(fileInfo.getDescription().contains(newDescription), "Description of blog " + NEWS_FILE1 + " was not updated.");

                DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(NEWS_FILE1).render();
                Assert.assertTrue(docDetailsPage.getDocumentBody().contains(newContent), "Content of blog " + NEWS_FILE1 + " was not updated.");
                Map<String, Object> properties = docDetailsPage.getProperties();
                Assert.assertNotNull(properties);
                Assert.assertEquals(properties.get("Name"), NEWS_FILE1, "Name Property is not " + NEWS_FILE1);
                Assert.assertTrue(properties.get("Title").toString().contains(newTitle), "Title of blog " + NEWS_FILE1 + " was not updated.");
                Assert.assertTrue(properties.get("Description").toString().contains(newDescription), "Description of blog " + NEWS_FILE1 + " was not updated.");
                //        Assert.assertTrue(properties.get("TemplateName").toString().contains(newTemplateName),"Template name of blog "+NEWS_FILE1+" was not updated.");

        }

        /**
         * AONE-5626:Editing "Investors fear rising risk..." article (Markets)
         */
        @Test(groups = { "WQS", "EnterpriseOnly" })
        public void AONE_5626() throws Exception
        {
                String newsName = "Fresh flight to Swiss franc as Europe's bond strains return";

                // ---- Step 1 ----
                // ---- Step action ---
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened
                drone.navigateTo(wqsURL);

                // ---- Step 2 ----
                // ---- Step action ---
                // Open "Investors fear rising risk of US regional defaults" article in Markets (News);
                // ---- Expected results ----
                // Article is opened;
                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                wcmqsHomePage.render();
                WcmqsNewsPage newsPage = wcmqsHomePage.openNewsPageFolder("markets").render();

                newsPage.clickNewsByTitle(newsName);

                // Login
                WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
                wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
                WcmqsNewsArticleDetails newsArticlePage = new WcmqsNewsArticleDetails(drone);
                newsArticlePage.render();
                Assert.assertTrue(newsArticlePage.getTitle().contains(newsName), "News article: " + newsName + " was not found.");

                // ---- Step 3 ----
                // ---- Step action ---
                // Click Edit button near article;
                // ---- Expected results ----
                // Edit window is opened;
                WcmqsEditPage wcmqsEditPage = newsArticlePage.clickEditButton();
                wcmqsEditPage.render();

                // ---- Step 4 ----
                // ---- Step action ---
                // Verify that all fields are displayed correctly incl. Content field;
                // ---- Expected results ----
                // All fields are displayed correctly, article content is displayed in Content field;
                Assert.assertTrue(wcmqsEditPage.isNameFieldDisplayed(), "Name field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isTitleFieldDisplayed(), "Title field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isDescriptionFieldDisplayed(), "Description field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isContentFrameDisplayed(), "Content frame is not displayed");
                Assert.assertTrue(wcmqsEditPage.isTemplateNameDisplayed(), "Template Name field is not displayed");
                String contentText = wcmqsEditPage.getContentTinyMCEEditor().getContent();
                Assert.assertFalse(contentText.isEmpty(), "Content field is empty");
                Assert.assertFalse(contentText.contains(newContent), "Content field contains new added information.");

                // ---- Step 5 ----
                // ---- Step action ---
                // Change information in all fields and save it;
                // ---- Expected results ----
                // Information is changed successfully, data is saved and displayed correctly;
                //        String newTemplateName="template";
                wcmqsEditPage.editTitle(newTitle);
                wcmqsEditPage.editDescription(newDescription);
                wcmqsEditPage.insertTextInContent(newContent);
                //        wcmqsEditPage.editTemplateName(newTemplateName);
                wcmqsEditPage.clickSubmitButton();

                WcmqsNewsPage newsPage2 = new WcmqsNewsPage(drone);
                newsPage2.render();
                Assert.assertTrue(newsPage2.checkIfNewsExists(newTitle), "Title of blog is not edited.");

                // ---- Step 6 ----
                // ---- Step action ---
                // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/news/markets) and verify article6.html file;
                // ---- Expected results ----
                // Changes made via AWE are dislpayed correctly
                ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
                DocumentLibraryPage documentLibraryPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);
                documentLibraryPage = navigateToWqsFolderFromRoot(documentLibraryPage, "news");
                documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder("markets").render();

                FileDirectoryInfo fileInfo = documentLibraryPage.getFileDirectoryInfo(NEWS_FILE6);
                Assert.assertTrue(fileInfo.getTitle().contains(newTitle), "Title of blog " + NEWS_FILE6 + " was not updated.");
                Assert.assertTrue(fileInfo.getDescription().contains(newDescription), "Description of blog " + NEWS_FILE6 + " was not updated.");

                DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(NEWS_FILE6).render();
                Assert.assertTrue(docDetailsPage.getDocumentBody().contains(newContent), "Content of blog " + NEWS_FILE6 + " was not updated.");
                Map<String, Object> properties = docDetailsPage.getProperties();
                Assert.assertNotNull(properties);
                Assert.assertEquals(properties.get("Name"), NEWS_FILE6, "Name Property is not " + NEWS_FILE6);
                Assert.assertTrue(properties.get("Title").toString().contains(newTitle), "Title of blog " + NEWS_FILE6 + " was not updated.");
                Assert.assertTrue(properties.get("Description").toString().contains(newDescription), "Description of blog " + NEWS_FILE6 + " was not updated.");
                //        Assert.assertTrue(properties.get("TemplateName").toString().contains(newTemplateName),"Template name of blog "+NEWS_FILE6+" was not updated.");

        }

        /**
         * AONE-5627:Editing "Our new brochure is now available" article (Markets)
         */
        @Test(groups = { "WQS", "EnterpriseOnly" })
        public void AONE_5627() throws Exception
        {
                String newsName = "Fresh flight to Swiss franc as Europe's bond strains return";

                // ---- Step 1 ----
                // ---- Step action ---
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened
                drone.navigateTo(wqsURL);

                // ---- Step 2 ----
                // ---- Step action ---
                // Open "Our new brochure is now available" article in Markets (News);
                // ---- Expected results ----
                // Article is opened;
                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                wcmqsHomePage.render();
                WcmqsNewsPage newsPage = wcmqsHomePage.openNewsPageFolder("markets").render();

                newsPage.clickNewsByTitle(newsName);

                // Login
                WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
                wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
                WcmqsNewsArticleDetails newsArticlePage = new WcmqsNewsArticleDetails(drone);
                newsArticlePage.render();
                Assert.assertTrue(newsArticlePage.getTitle().contains(newsName), "News article: " + newsName + " was not found.");

                // ---- Step 3 ----
                // ---- Step action ---
                // Click Edit button near article;
                // ---- Expected results ----
                // Edit window is opened;
                WcmqsEditPage wcmqsEditPage = newsArticlePage.clickEditButton();
                wcmqsEditPage.render();

                // ---- Step 4 ----
                // ---- Step action ---
                // Verify that all fields are displayed correctly incl. Content field;
                // ---- Expected results ----
                // All fields are displayed correctly, article content is displayed in Content field;
                Assert.assertTrue(wcmqsEditPage.isNameFieldDisplayed(), "Name field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isTitleFieldDisplayed(), "Title field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isDescriptionFieldDisplayed(), "Description field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isContentFrameDisplayed(), "Content frame is not displayed");
                Assert.assertTrue(wcmqsEditPage.isTemplateNameDisplayed(), "Template Name field is not displayed");
                String contentText = wcmqsEditPage.getContentTinyMCEEditor().getContent();
                Assert.assertFalse(contentText.isEmpty(), "Content field is empty");
                Assert.assertFalse(contentText.contains(newContent), "Content field contains new added information.");

                // ---- Step 5 ----
                // ---- Step action ---
                // Change information in all fields and save it;
                // ---- Expected results ----
                // Information is changed successfully, data is saved and displayed correctly;
                //        String newTemplateName="template";
                wcmqsEditPage.editTitle(newTitle);
                wcmqsEditPage.editDescription(newDescription);
                wcmqsEditPage.insertTextInContent(newContent);
                //        wcmqsEditPage.editTemplateName(newTemplateName);
                wcmqsEditPage.clickSubmitButton();

                WcmqsNewsPage newsPage2 = new WcmqsNewsPage(drone);
                newsPage2.render();
                Assert.assertTrue(newsPage2.checkIfNewsExists(newTitle), "Title of blog is not edited.");

                // ---- Step 6 ----
                // ---- Step action ---
                // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/news/markets) and verify article5.html file;
                // ---- Expected results ----
                // Changes made via AWE are dislpayed correctly
                ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
                DocumentLibraryPage documentLibraryPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);
                documentLibraryPage = navigateToWqsFolderFromRoot(documentLibraryPage, "news");
                documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder("markets").render();

                FileDirectoryInfo fileInfo = documentLibraryPage.getFileDirectoryInfo(NEWS_FILE5);
                Assert.assertTrue(fileInfo.getTitle().contains(newTitle), "Title of blog " + NEWS_FILE5 + " was not updated.");
                Assert.assertTrue(fileInfo.getDescription().contains(newDescription), "Description of blog " + NEWS_FILE5 + " was not updated.");

                DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(NEWS_FILE5).render();
                Assert.assertTrue(docDetailsPage.getDocumentBody().contains(newContent), "Content of blog " + NEWS_FILE5 + " was not updated.");
                Map<String, Object> properties = docDetailsPage.getProperties();
                Assert.assertNotNull(properties);
                Assert.assertEquals(properties.get("Name"), NEWS_FILE5, "Name Property is not " + NEWS_FILE5);
                Assert.assertTrue(properties.get("Title").toString().contains(newTitle), "Title of blog " + NEWS_FILE5 + " was not updated.");
                Assert.assertTrue(properties.get("Description").toString().contains(newDescription), "Description of blog " + NEWS_FILE5 + " was not updated.");
                //        Assert.assertTrue(properties.get("TemplateName").toString().contains(newTemplateName),"Template name of blog "+NEWS_FILE5+" was not updated.");

        }

        /**
         * AONE-5628:Editing "First slide" article
         */
        @Test(groups = { "WQS", "EnterpriseOnly" })
        public void AONE_5628() throws Exception
        {
                String newsName = "FTSE 100 rallies from seven-week low";

                // ---- Step 1 ----
                // ---- Step action ---
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened
                drone.navigateTo(wqsURL);

                // ---- Step 2 ----
                // ---- Step action ---
                // On main page click "Read more" on the animated banner when "First slide" is displayed;;
                // ---- Expected results ----
                // Article is opened;
                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                wcmqsHomePage.render();
                wcmqsHomePage.waitForAndClickSlideInBanner(SLIDE_FILE1);

                // Login
                WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
                wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
                WcmqsNewsArticleDetails newsArticlePage = new WcmqsNewsArticleDetails(drone);
                newsArticlePage.render();
                Assert.assertTrue(newsArticlePage.getTitle().contains(newsName), "News article: " + newsName + " was not found.");

                // ---- Step 3 ----
                // ---- Step action ---
                // Click Edit button near article;
                // ---- Expected results ----
                // Edit window is opened;
                WcmqsEditPage wcmqsEditPage = newsArticlePage.clickEditButton();
                wcmqsEditPage.render();

                // ---- Step 4 ----
                // ---- Step action ---
                // Verify that all fields are displayed correctly incl. Content field;
                // ---- Expected results ----
                // All fields are displayed correctly, article content is displayed in Content field;
                Assert.assertTrue(wcmqsEditPage.isNameFieldDisplayed(), "Name field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isTitleFieldDisplayed(), "Title field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isDescriptionFieldDisplayed(), "Description field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isContentFrameDisplayed(), "Content frame is not displayed");
                Assert.assertTrue(wcmqsEditPage.isTemplateNameDisplayed(), "Template Name field is not displayed");
                String contentText = wcmqsEditPage.getContentTinyMCEEditor().getContent();
                Assert.assertFalse(contentText.isEmpty(), "Content field is empty");
                Assert.assertFalse(contentText.contains(newContent), "Content field contains new added information.");

                // ---- Step 5 ----
                // ---- Step action ---
                // Change information in all fields and save it;
                // ---- Expected results ----
                // Information is changed successfully, data is saved and displayed correctly;
                //        String newTemplateName="template";
                wcmqsEditPage.editTitle(newTitle);
                wcmqsEditPage.editDescription(newDescription);
                wcmqsEditPage.insertTextInContent(newContent);
                //        wcmqsEditPage.editTemplateName(newTemplateName);
                wcmqsEditPage.clickSubmitButton();

                WcmqsNewsPage newsPage2 = new WcmqsNewsPage(drone);
                newsPage2.render();
                List<ShareLink> rightTitles = newsPage2.getRightHeadlineTitleNews();
                String titles = rightTitles.toString();
                Assert.assertTrue(titles.contains(newsName + newTitle), "Title of blog is not edited.");

                // ---- Step 6 ----
                // ---- Step action ---
                // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/news) and verify slide1.html file;
                // ---- Expected results ----
                // Changes made via AWE are dislpayed correctly
                ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
                DocumentLibraryPage documentLibraryPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);
                documentLibraryPage = navigateToWqsFolderFromRoot(documentLibraryPage, "news");

                FileDirectoryInfo fileInfo = documentLibraryPage.getFileDirectoryInfo(SLIDE_FILE1);
                Assert.assertTrue(fileInfo.getTitle().contains(newTitle), "Title of blog " + SLIDE_FILE1 + " was not updated.");
                Assert.assertTrue(fileInfo.getDescription().contains(newDescription), "Description of blog " + SLIDE_FILE1 + " was not updated.");

                DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(SLIDE_FILE1).render();
                Assert.assertTrue(docDetailsPage.getDocumentBody().contains(newContent), "Content of blog " + SLIDE_FILE1 + " was not updated.");
                Map<String, Object> properties = docDetailsPage.getProperties();
                Assert.assertNotNull(properties);
                Assert.assertEquals(properties.get("Name"), SLIDE_FILE1, "Name Property is not " + SLIDE_FILE1);
                Assert.assertTrue(properties.get("Title").toString().contains(newTitle), "Title of blog " + SLIDE_FILE1 + " was not updated.");
                Assert.assertTrue(properties.get("Description").toString().contains(newDescription),
                        "Description of blog " + SLIDE_FILE1 + " was not updated.");
                //        Assert.assertTrue(properties.get("TemplateName").toString().contains(newTemplateName),"Template name of blog "+SLIDE_FILE1+" was not updated.");

        }

        /**
         * AONE-5629:Editing "Second slide" article
         */
        @Test(groups = { "WQS", "EnterpriseOnly" })
        public void AONE_5629() throws Exception
        {
                String newsName = "Experts Weigh Stocks, the Dollar, and the 'Fiscal Hangover'";

                // ---- Step 1 ----
                // ---- Step action ---
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened
                drone.navigateTo(wqsURL);

                // ---- Step 2 ----
                // ---- Step action ---
                // On main page click "Read more" on the animated banner when "Second slide" is displayed;
                // ---- Expected results ----
                // Article is opened;
                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                wcmqsHomePage.render();
                wcmqsHomePage.waitForAndClickSlideInBanner(SLIDE_FILE2);

                // Login
                WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
                wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
                WcmqsNewsArticleDetails newsArticlePage = new WcmqsNewsArticleDetails(drone);
                newsArticlePage.render();
                Assert.assertTrue(newsArticlePage.getTitle().contains(newsName), "News article: " + newsName + " was not found.");

                // ---- Step 3 ----
                // ---- Step action ---
                // Click Edit button near article;
                // ---- Expected results ----
                // Edit window is opened;
                WcmqsEditPage wcmqsEditPage = newsArticlePage.clickEditButton();
                wcmqsEditPage.render();

                // ---- Step 4 ----
                // ---- Step action ---
                // Verify that all fields are displayed correctly incl. Content field;
                // ---- Expected results ----
                // All fields are displayed correctly, article content is displayed in Content field;
                Assert.assertTrue(wcmqsEditPage.isNameFieldDisplayed(), "Name field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isTitleFieldDisplayed(), "Title field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isDescriptionFieldDisplayed(), "Description field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isContentFrameDisplayed(), "Content frame is not displayed");
                Assert.assertTrue(wcmqsEditPage.isTemplateNameDisplayed(), "Template Name field is not displayed");
                String contentText = wcmqsEditPage.getContentTinyMCEEditor().getContent();
                Assert.assertFalse(contentText.isEmpty(), "Content field is empty");
                Assert.assertFalse(contentText.contains(newContent), "Content field contains new added information.");

                // ---- Step 5 ----
                // ---- Step action ---
                // Change information in all fields and save it;
                // ---- Expected results ----
                // Information is changed successfully, data is saved and displayed correctly;
                //        String newTemplateName="template";
                wcmqsEditPage.editTitle(newTitle);
                wcmqsEditPage.editDescription(newDescription);
                wcmqsEditPage.insertTextInContent(newContent);
                //        wcmqsEditPage.editTemplateName(newTemplateName);
                wcmqsEditPage.clickSubmitButton();

                WcmqsNewsPage newsPage2 = new WcmqsNewsPage(drone);
                newsPage2.render();
                List<ShareLink> rightTitles = newsPage2.getRightHeadlineTitleNews();
                String titles = rightTitles.toString();
                Assert.assertTrue(titles.contains(newsName + newTitle), "Title of blog is not edited.");

                // ---- Step 6 ----
                // ---- Step action ---
                // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/news) and verify slide2.html file;
                // ---- Expected results ----
                // Changes made via AWE are dislpayed correctly
                ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
                DocumentLibraryPage documentLibraryPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);
                documentLibraryPage = navigateToWqsFolderFromRoot(documentLibraryPage, "news");

                FileDirectoryInfo fileInfo = documentLibraryPage.getFileDirectoryInfo(SLIDE_FILE2);
                Assert.assertTrue(fileInfo.getTitle().contains(newTitle), "Title of blog " + SLIDE_FILE2 + " was not updated.");
                Assert.assertTrue(fileInfo.getDescription().contains(newDescription), "Description of blog " + SLIDE_FILE2 + " was not updated.");

                DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(SLIDE_FILE2).render();
                Assert.assertTrue(docDetailsPage.getDocumentBody().contains(newContent), "Content of blog " + SLIDE_FILE2 + " was not updated.");
                Map<String, Object> properties = docDetailsPage.getProperties();
                Assert.assertNotNull(properties);
                Assert.assertEquals(properties.get("Name"), SLIDE_FILE2, "Name Property is not " + SLIDE_FILE2);
                Assert.assertTrue(properties.get("Title").toString().contains(newTitle), "Title of blog " + SLIDE_FILE2 + " was not updated.");
                Assert.assertTrue(properties.get("Description").toString().contains(newDescription),
                        "Description of blog " + SLIDE_FILE2 + " was not updated.");
                //        Assert.assertTrue(properties.get("TemplateName").toString().contains(newTemplateName),"Template name of blog "+SLIDE_FILE1+" was not updated.");

        }

        /**
         * AONE-5630:Editing "Third slide" article
         */
        @Test(groups = { "WQS", "EnterpriseOnly" })
        public void AONE_5630() throws Exception
        {
                String newsName = "Credit card interest rates rise";

                // ---- Step 1 ----
                // ---- Step action ---
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened
                drone.navigateTo(wqsURL);

                // ---- Step 2 ----
                // ---- Step action ---
                // On main page click "Read more" on the animated banner when "Third slide" is displayed;
                // ---- Expected results ----
                // Article is opened;
                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                wcmqsHomePage.render();
                wcmqsHomePage.waitForAndClickSlideInBanner(SLIDE_FILE3);

                // Login
                WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
                wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
                WcmqsNewsArticleDetails newsArticlePage = new WcmqsNewsArticleDetails(drone);
                newsArticlePage.render();
                Assert.assertTrue(newsArticlePage.getTitle().contains(newsName), "News article: " + newsName + " was not found.");

                // ---- Step 3 ----
                // ---- Step action ---
                // Click Edit button near article;
                // ---- Expected results ----
                // Edit window is opened;
                WcmqsEditPage wcmqsEditPage = newsArticlePage.clickEditButton();
                wcmqsEditPage.render();

                // ---- Step 4 ----
                // ---- Step action ---
                // Verify that all fields are displayed correctly incl. Content field;
                // ---- Expected results ----
                // All fields are displayed correctly, article content is displayed in Content field;
                Assert.assertTrue(wcmqsEditPage.isNameFieldDisplayed(), "Name field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isTitleFieldDisplayed(), "Title field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isDescriptionFieldDisplayed(), "Description field is not displayed");
                Assert.assertTrue(wcmqsEditPage.isContentFrameDisplayed(), "Content frame is not displayed");
                Assert.assertTrue(wcmqsEditPage.isTemplateNameDisplayed(), "Template Name field is not displayed");
                String contentText = wcmqsEditPage.getContentTinyMCEEditor().getContent();
                Assert.assertFalse(contentText.isEmpty(), "Content field is empty");
                Assert.assertFalse(contentText.contains(newContent), "Content field contains new added information.");

                // ---- Step 5 ----
                // ---- Step action ---
                // Change information in all fields and save it;
                // ---- Expected results ----
                // Information is changed successfully, data is saved and displayed correctly;

                //        String newTemplateName="template";
                wcmqsEditPage.editTitle(newTitle);
                wcmqsEditPage.editDescription(newDescription);
                wcmqsEditPage.insertTextInContent(newContent);
                //        wcmqsEditPage.editTemplateName(newTemplateName);
                wcmqsEditPage.clickSubmitButton();

                WcmqsNewsPage newsPage2 = new WcmqsNewsPage(drone);
                newsPage2.render();
                List<ShareLink> rightTitles = newsPage2.getRightHeadlineTitleNews();
                String titles = rightTitles.toString();
                Assert.assertTrue(titles.contains(newsName + newTitle), "Title of blog is not edited.");

                // ---- Step 6 ----
                // ---- Step action ---
                // Go to Share "My Web Site" document library (Alfresco Quick Start/Quick Start Editorial/root/news) and verify slide3.html file;
                // ---- Expected results ----
                // Changes made via AWE are dislpayed correctly
                ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
                DocumentLibraryPage documentLibraryPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);
                documentLibraryPage = navigateToWqsFolderFromRoot(documentLibraryPage, "news");

                FileDirectoryInfo fileInfo = documentLibraryPage.getFileDirectoryInfo(SLIDE_FILE3);
                Assert.assertTrue(fileInfo.getTitle().contains(newTitle), "Title of blog " + SLIDE_FILE3 + " was not updated.");
                Assert.assertTrue(fileInfo.getDescription().contains(newDescription), "Description of blog " + SLIDE_FILE3 + " was not updated.");

                DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(SLIDE_FILE3).render();
                Assert.assertTrue(docDetailsPage.getDocumentBody().contains(newContent), "Content of blog " + SLIDE_FILE3 + " was not updated.");
                Map<String, Object> properties = docDetailsPage.getProperties();
                Assert.assertNotNull(properties);
                Assert.assertEquals(properties.get("Name"), SLIDE_FILE3, "Name Property is not " + SLIDE_FILE3);
                Assert.assertTrue(properties.get("Title").toString().contains(newTitle), "Title of blog " + SLIDE_FILE3 + " was not updated.");
                Assert.assertTrue(properties.get("Description").toString().contains(newDescription),
                        "Description of blog " + SLIDE_FILE3 + " was not updated.");
                //        Assert.assertTrue(properties.get("TemplateName").toString().contains(newTemplateName),"Template name of blog "+SLIDE_FILE1+" was not updated.");

        }

}
