package org.alfresco.share.enterprise.wqs.web.blog;

/**
 * Created by P3700473 on 12/2/2014.
 */

import org.alfresco.po.share.dashlet.SiteWebQuickStartDashlet;
import org.alfresco.po.share.dashlet.WebQuickStartOptions;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.datalist.DataListPage;
import org.alfresco.po.share.site.datalist.items.VisitorFeedbackRow;
import org.alfresco.po.share.site.datalist.lists.VisitorFeedbackList;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.wqs.*;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Lucian Tuca on 12/02/2014.
 */
public class BlogComponent extends AbstractUtils
{
        private static final Logger logger = Logger.getLogger(BlogComponent.class);
        private final String ALFRESCO_QUICK_START = "Alfresco Quick Start";
        private final String QUICK_START_EDITORIAL = "Quick Start Editorial";
        private final String ROOT = "root";

        private final String ACCOUNTING = "accounting";
        private final String ACCOUNTING_DATA = "Accounting";

        private String testName;
        private String wqsURL;
        private String siteName;
        private String ipAddress;
        private String hostName;

        @Override @BeforeClass(alwaysRun = true) public void setup() throws Exception
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

        @AfterClass(alwaysRun = true) public void tearDown()
        {
                super.tearDown();
        }

        @Test(groups = { "DataPrepWQS" }) public void dataPrep_AONE() throws Exception
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

                ShareUser.openSiteDashboard(drone, siteName);
                // Data Lists component is added to the site
                ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.DATA_LISTS);

                // Site Dashboard is rendered with Data List link
                ShareUser.openSiteDashboard(drone, siteName).render();

                //setup new entry in hosts to be able to access the new wcmqs site
                String setHostAddress = "cmd.exe /c echo. >> %WINDIR%\\System32\\Drivers\\Etc\\hosts && echo " + ipAddress + " " + siteName
                        + " >> %WINDIR%\\System32\\Drivers\\Etc\\hosts";
                Runtime.getRuntime().exec(setHostAddress);
        }

        /*
        * AONE-5673 Blogs page
        */
        @Test(groups = { "WQS" }) public void AONE_5673() throws Exception
        {

                // ---- Step 1 ----
                // ---- Step action ----
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened;

                navigateTo(wqsURL);

                // ---- Step 2 ----
                // ---- Step action ----
                // Verify Blog page;
                // ---- Expected results ----

                // Pagination

                WcmqsHomePage homePage = new WcmqsHomePage(drone);
                homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);

                WcmqsBlogPage blogPage = new WcmqsBlogPage(drone);
                Assert.assertEquals(blogPage.getBlogPosts(), 3);

                // The following items are displayed:
                // Sample blog posts (Post title link, Creation date, Creator name, #comments link, Read more button)
                // Section Tags (list of tags with number of tags in brackets)

                Assert.assertTrue(blogPage.isBlogDisplayed(WcmqsBlogPage.ETHICAL_FUNDS));
                Assert.assertTrue(blogPage.isBlogPostDateDisplayed());
                Assert.assertTrue(blogPage.isBlogPostCreatorDisplayed());
                Assert.assertTrue(blogPage.isBlogPostCommentsLinkDisplayed());
        }

        /*
        * AONE-5674 Opening blog post
        */
        @Test(groups = { "WQS" }) public void AONE_5674() throws Exception
        {

                // ---- Step 1 ----
                // ---- Step action ----
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened;

                navigateTo(wqsURL);

                // ---- Step 2 ----
                // ---- Step action ----
                // Open Blogs page;
                // ---- Expected results ----
                // Blogs page is opened;

                WcmqsHomePage homePage = new WcmqsHomePage(drone);
                homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
                WcmqsBlogPage blogPage = new WcmqsBlogPage(drone);

                // ---- Step 3 ----
                // ---- Step action ----
                // Click 'Ethical  funds' blog name link;
                // ---- Expected results ----
                // Blog post is opened successfully and displayed correctly;

                blogPage.clickLinkByTitle(WcmqsBlogPage.ETHICAL_FUNDS);
                WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
                wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
                assertThat("Verify if the correct page opened ", blogPage.getTitle(), containsString(WcmqsBlogPage.ETHICAL_FUNDS));

                // ---- Step 4 ----
                // ---- Step action ----
                // Return to Blogs page and click Company   organises workshop blog name link;
                // ---- Expected results ----
                // Blog post is opened successfully and displayed correctly;

                homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
                blogPage.render();
                blogPage.clickLinkByTitle(WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP);
                assertThat("Verify if the correct page opened ", blogPage.getTitle(), containsString(WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP));

                // ---- Step 5 ----
                // ---- Step action ----
                // Return to Blogs page and click Our top  analyst's latest... blog name link;
                // ---- Expected results ----
                // Blog post is opened successfully and displayed correctly;

                homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
                blogPage.render();
                blogPage.clickLinkByTitle(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS);
                assertThat("Verify if the correct page opened ", blogPage.getTitle(), containsString(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS));

                // ---- Step 6 ----
                // ---- Step action ----
                // Return to Blogs page and click Read more button for 'Ethical  funds' for blog;
                // ---- Expected results ----
                // Blog post is opened successfully and displayed correctly;

                homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
                blogPage.render();
                blogPage.clickReadMoreByBlog(WcmqsBlogPage.ETHICAL_FUNDS);
                assertThat("Verify if the correct page opened ", blogPage.getTitle(), containsString(WcmqsBlogPage.ETHICAL_FUNDS));

                // ---- Step 7 ----
                // ---- Step action ----
                // Return to Blogs page and click Read more button for 'Company   organises workshop for blog;
                // ---- Expected results ----
                // Blog post is opened successfully and displayed correctly;

                homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
                blogPage.render();
                blogPage.clickReadMoreByBlog(WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP);
                assertThat("Verify if the correct page opened ", blogPage.getTitle(), containsString(WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP));

                // ---- Step 8 ----
                // ---- Step action ----
                // Return to Blogs page and click Read more button for Our top  analyst's latest... for blog;
                // ---- Expected results ----
                // Blog post is opened successfully and displayed correctly;

                homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
                blogPage.render();
                blogPage.clickReadMoreByBlog(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS);
                assertThat("Verify if the correct page opened ", blogPage.getTitle(), containsString(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS));

        }

        /*
        * AONE-5675 Pagination
        */
        @Test(groups = { "WQS" }) public void AONE_5675() throws Exception
        {

                // ---- Step 1 ----
                // ---- Step action ----
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened;

                navigateTo(wqsURL);

                // ---- Step 2 ----
                // ---- Step action ----
                // Go to Blogs page;
                // ---- Expected results ----
                // Blogs page is opened, created blogs are dislpayed correctly;

                WcmqsHomePage homePage = new WcmqsHomePage(drone);
                homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
                WcmqsBlogPage blogPage = new WcmqsBlogPage(drone);

                // ---- Step 3 ----
                // ---- Step action ----
                // Check the number of displayed items;
                // ---- Expected results ----
                // Only three latest items are displayed, Sectionpage2 field is limited to 3 latest items;

                assertThat("Verify if the correct number of blog pages is displayed ", blogPage.getBlogPosts(), is(equalTo(3)));

        }

        /*
        * AONE-5676 Commenting a blog post
        */
        @Test(groups = { "WQS" }) public void AONE_5676() throws Exception
        {

                String visitorName = "name " + getTestName();
                String visitorEmail = getTestName() + "@" + DOMAIN_FREE;
                String visitorWebsite = "website " + getTestName();
                String visitorComment = "Comment by " + visitorName;

                // ---- Step 1 ----
                // ---- Step action ----
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened;

                navigateTo(wqsURL);

                // ---- Step 2 ----
                // ---- Step action ----
                // Open any blog post (e.g. blog1.html);
                // ---- Expected results ----
                // Blog post is opened;

                WcmqsHomePage homePage = new WcmqsHomePage(drone);
                homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
                WcmqsBlogPage blogPage = new WcmqsBlogPage(drone);
                blogPage.clickLinkByTitle(WcmqsBlogPage.ETHICAL_FUNDS);
                WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
                wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
                assertThat("Verify if the correct page opened ", blogPage.getTitle(), containsString(WcmqsBlogPage.ETHICAL_FUNDS));

                // ---- Step 3 ----
                // ---- Step action ----
                // Specify mandatory information on Comment form and save comment;
                // ---- Expected results ----
                // Information is saved successfully and displayed correctly;

                WcmqsBlogPostPage wcmqsBlogPostPage = new WcmqsBlogPostPage(drone);
                wcmqsBlogPostPage.setVisitorName(visitorName);
                wcmqsBlogPostPage.setVisitorEmail(visitorEmail);
                wcmqsBlogPostPage.setVisitorWebsite(visitorWebsite);
                wcmqsBlogPostPage.setVisitorComment(visitorComment);
                wcmqsBlogPostPage.clickPostButton();

                assertThat("Posting was succesfull", wcmqsBlogPostPage.isAddCommentMessageDisplay());

                wcmqsBlogPostPage.clickWebQuickStartLogo().render();
                wcmqsBlogPostPage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
                wcmqsBlogPostPage.clickLinkByTitle(WcmqsBlogPage.ETHICAL_FUNDS);
                WcmqsComment wcmqsComment = new WcmqsComment(drone);
                assertThat(wcmqsComment.getNameFromContent(), is(equalTo(visitorName)));
                assertThat(wcmqsComment.getCommentFromContent(), is(equalTo(visitorComment)));

                // ---- Step 4 ----
                // ---- Step action ----
                // Open My Web Site via Alfresco Share;
                // ---- Expected results ----
                // Site is opened successfully;

                ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
                DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

                // ---- Step 5 ----
                // ---- Step action ----
                // Go to Data Lists component;
                // ---- Expected results ----
                // Data lists component is opened, Visitor Feedback  (Alfresco WCM Quick Start) data list is displayed by default;

                DataListPage dataListPage = docLibPage.getSiteNav().selectDataListPage().render();

                // ---- Step 6 ----
                // ---- Step action ----
                // Open Visitor Feedback  (Alfresco WCM Quick Start) data list;
                // ---- Expected results ----
                // New feedback item is displayed, it consists information entered on step 4.

                dataListPage.selectDataList("Visitor Feedback (Quick Start Editorial)");
                VisitorFeedbackList feedbackList = new VisitorFeedbackList(drone);
                feedbackList.render();
                VisitorFeedbackRow newFeedback = feedbackList.getRowForSpecificValues(visitorEmail, visitorComment, visitorName, visitorWebsite);
                assertThat(newFeedback.getVisitorName(), is(equalTo(visitorName)));
                assertThat(newFeedback.getVisitorEmail(), is(equalTo(visitorEmail)));
                assertThat(newFeedback.getVisitorComment(), is(equalTo(visitorComment)));
                assertThat(newFeedback.getVisitorWebsite(), is(equalTo(visitorWebsite)));

        }

        /*
        * AONE-5677 Verify correct work of comments number value
        */
        @Test(dependsOnMethods = { "AONE_5676" }, groups = { "WQS" }) public void AONE_5677() throws Exception
        {

                // ---- Step 1 ----
                // ---- Step action ----
                // Open My Web Site via Alfresco Share;
                // ---- Expected results ----
                // Site is opened successfully;

                ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
                DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

                // ---- Step 2 ----
                // ---- Step action ----
                // Go to Data Lists component;
                // ---- Expected results ----
                // Data lists component is opened, Visitor Feedback  (Alfresco WCM Quick Start) data list is displayed by default;

                DataListPage dataListPage = docLibPage.getSiteNav().selectDataListPage().render();

                // ---- Step 3 ----
                // ---- Step action ----
                // Open Visitor Feedback (Alfresco WCM Quick Start) data list;
                // ---- Expected results ----
                // Visitor Feedback  data list is opened;

                dataListPage.selectDataList("Visitor Feedback (Quick Start Editorial)");
                VisitorFeedbackList feedbackList = new VisitorFeedbackList(drone);
                feedbackList.render();

                // ---- Step 4 ----
                // ---- Step action ----
                // Click Duplicate button for prevoiusly created(in WCMQS-12) comment;
                // ---- Expected results ----
                // Item duplicated message is shown;

                VisitorFeedbackRow testrow = feedbackList.getRowForVisitorEmail("Share-5676" + "@" + DOMAIN_FREE);
                testrow.clickDuplicateOnRow();
                assertThat("Check if the duplicate message appears!", testrow.isDuplicateMessageDisplayed());

                // ---- Step 5 ----
                // ---- Step action ----
                // Navigate to blog1 article page;
                // ---- Expected results ----
                // Blog1 is opened;

                navigateTo(wqsURL);
                WcmqsHomePage homePage = new WcmqsHomePage(drone);
                homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
                WcmqsBlogPage blogPage = new WcmqsBlogPage(drone);
                blogPage.clickLinkByTitle(WcmqsBlogPage.ETHICAL_FUNDS);
                WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
                wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
                assertThat("Verify if the correct page opened ", blogPage.getTitle(), containsString(WcmqsBlogPage.ETHICAL_FUNDS));

                // ---- Step 6 ----
                // ---- Step action ----
                // Verify number of comments;
                // ---- Expected results ----
                // Number of comments increased;

                WcmqsComment wcmqsComment = new WcmqsComment(drone).render();
                assertThat(wcmqsComment.getNumberOfCommentsOnPage(), is(equalTo(2)));

        }

        /*
        * AONE-5678 Creating comment with wildcards in blog
        */
        @Test(groups = { "WQS" }) public void AONE_5678() throws Exception
        {
                String visitorName = "name" + getTestName();
                String visitorEmail = getTestName() + "@" + DOMAIN_FREE;
                String visitorWebsite = "website " + getTestName();
                String visitorComment = "Comment by " + visitorName;

                // ---- Step 1 ----
                // ---- Step action ----
                // Enter some data, containing wildcards in the Name field;
                // ---- Expected results ----
                // Data entered successfully;

                navigateTo(wqsURL);
                WcmqsHomePage homePage = new WcmqsHomePage(drone);
                homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
                WcmqsBlogPage blogPage = new WcmqsBlogPage(drone);
                blogPage.clickLinkByTitle(WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP);
                WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
                wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
                WcmqsBlogPostPage wcmqsBlogPostPage = new WcmqsBlogPostPage(drone);
                wcmqsBlogPostPage.setVisitorName(visitorName + "!@#$%^&*");

                // ---- Step 2 ----
                // ---- Step action ----
                // Fill other fields with correct data and click Post button;
                // ---- Expected results ----
                // Please fix the problems indicated below. Thank you. the name you entered contains invalid  characters message is shown;

                wcmqsBlogPostPage.setVisitorEmail(visitorEmail);
                wcmqsBlogPostPage.setVisitorWebsite(visitorWebsite);
                wcmqsBlogPostPage.setVisitorComment(visitorComment);
                wcmqsBlogPostPage.clickPostButton();

                assertThat("Main form error message is displayed! ", wcmqsBlogPostPage.isFormProblemsMessageDisplay());
                assertThat("Verify name field error is displayed", wcmqsBlogPostPage.getFormErrorMessages(),
                        hasItem(equalTo("the name you entered contains invalid characters")));

                // ---- Step 3 ----
                // ---- Step action ----
                // Fill name field with correct data and click Post button again;
                // ---- Expected results ----
                // Comment is saved successfully;

                wcmqsBlogPostPage.setVisitorName(visitorName);
                wcmqsBlogPostPage.clickPostButton();
                assertThat("Posting was succesfull", wcmqsBlogPostPage.isAddCommentMessageDisplay());

                // ---- Step 4 ----
                // ---- Step action ----
                // Open any blog post again;
                // ---- Expected results ----
                // Blog post is opened;

                homePage = new WcmqsHomePage(drone);
                homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
                blogPage = new WcmqsBlogPage(drone);
                blogPage.clickLinkByTitle(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS);

                assertThat("Verify if the correct page opened ", blogPage.getTitle(), containsString(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS));

                // ---- Step 5 ----
                // ---- Step action ----
                // Enter some data, containing wildcards in the Email field;
                // ---- Expected results ----
                // Data entered successfully

                wcmqsBlogPostPage = new WcmqsBlogPostPage(drone);
                wcmqsBlogPostPage.setVisitorEmail(visitorEmail + "!@#$%^&*");

                // ---- Step 6 ----
                // ---- Step action ----
                // Fill other fields with correct data and click Post button;
                // ---- Expected results ----
                // Please fix the problems indicated below. Thank you.the email address is not valid  message is shown;

                wcmqsBlogPostPage.setVisitorName(visitorName);
                wcmqsBlogPostPage.setVisitorWebsite(visitorWebsite);
                wcmqsBlogPostPage.setVisitorComment(visitorComment);
                wcmqsBlogPostPage.clickPostButton();

                assertThat("Main form error message is displayed! ", wcmqsBlogPostPage.isFormProblemsMessageDisplay());
                assertThat("Verify name field error is displayed", wcmqsBlogPostPage.getFormErrorMessages(),
                        hasItem(equalTo("the email address is not valid")));

                // ---- Step 7 ----
                // ---- Step action ----
                // Fill mail field with correct data and click Post button again;
                // ---- Expected results ----
                // Comment is saved successfully;

                wcmqsBlogPostPage.setVisitorEmail(visitorEmail);
                wcmqsBlogPostPage.clickPostButton();
                assertThat("Posting was succesfull", wcmqsBlogPostPage.isAddCommentMessageDisplay());

                // ---- Step 8 ----
                // ---- Step action ----
                // Open any blog post again;
                // ---- Expected results ----
                // Blog post is opened;

                homePage = new WcmqsHomePage(drone);
                homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
                blogPage = new WcmqsBlogPage(drone);
                blogPage.clickLinkByTitle(WcmqsBlogPage.ETHICAL_FUNDS);

                assertThat("Verify if the correct page opened ", blogPage.getTitle(), containsString(WcmqsBlogPage.ETHICAL_FUNDS));

                // ---- Step 9 ----
                // ---- Step action ----
                // Enter some data, containing wildcards in the Website field;
                // ---- Expected results ----
                // Data entered successfully;

                wcmqsBlogPostPage = new WcmqsBlogPostPage(drone);
                wcmqsBlogPostPage.setVisitorWebsite(visitorWebsite + "!@#$%^&*");

                // ---- Step 10 ----
                // ---- Step action ----
                // Fill other fields with correct data and click Post button;
                // ---- Expected results ----
                // Comment is saved successfully;

                wcmqsBlogPostPage.setVisitorName(visitorName);
                wcmqsBlogPostPage.setVisitorEmail(visitorEmail);
                wcmqsBlogPostPage.setVisitorComment(visitorComment);
                wcmqsBlogPostPage.clickPostButton();
                assertThat("Posting was succesfull", wcmqsBlogPostPage.isAddCommentMessageDisplay());

        }

        /*
        * AONE-5679 Adding blog post comment with too long data
        */
        @Test(groups = { "WQS" }) public void AONE_5679() throws Exception
        {

                String visitorName = "name" + getTestName();
                String visitorEmail = getTestName() + "@" + DOMAIN_FREE;
                String visitorWebsite = "website " + getTestName();
                String visitorComment = "Comment by " + visitorName;

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

                WcmqsHomePage homePage = new WcmqsHomePage(drone);
                homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
                WcmqsBlogPage blogPage = new WcmqsBlogPage(drone);
                blogPage.clickLinkByTitle(WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP);
                WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
                wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
                WcmqsBlogPostPage wcmqsBlogPostPage = new WcmqsBlogPostPage(drone);

                // ---- Step 3 ----
                // ---- Step action ----
                // Enter too long data(more 1024 characters) in Name field;
                // ---- Expected results ----
                // Data successfully entered;

                // TODO : Test CASE Steps needs to be updated since the data you enter is automatically truncated to 70 chars
                wcmqsBlogPostPage.setVisitorName(visitorName + StringUtils.leftPad("test", 1100, 'a'));
                assertThat("Check if the number of entered chars is 70", wcmqsBlogPostPage.getVisitorName().length(), is(equalTo(70)));

                // ---- Step 4 ----
                // ---- Step action ----
                // Fill other fields with correct data and click Post button;
                // ---- Expected results ----
                // You should be able to post succesfully;

                wcmqsBlogPostPage.setVisitorEmail(visitorEmail);
                wcmqsBlogPostPage.setVisitorWebsite(visitorWebsite);
                wcmqsBlogPostPage.setVisitorComment(visitorComment);
                wcmqsBlogPostPage.clickPostButton();
                assertThat("Posting was succesfull", wcmqsBlogPostPage.isAddCommentMessageDisplay());

                // ---- Step 5 ----
                // ---- Step action ----
                // Open any blog post again;
                // ---- Expected results ----
                // Blog post is opened;

                homePage = new WcmqsHomePage(drone);
                homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
                blogPage = new WcmqsBlogPage(drone);
                blogPage.clickLinkByTitle(WcmqsBlogPage.ETHICAL_FUNDS);

                assertThat("Verify if the correct page opened ", blogPage.getTitle(), containsString(WcmqsBlogPage.ETHICAL_FUNDS));

                // ---- Step 6 ----
                // ---- Step action ----
                // Enter too long data(more 1024 characters) in Email field;
                // ---- Expected results ----
                // Data successfully entered;

                // TODO : Test CASE Steps needs to be updated since the data you enter is automatically truncated to 100 chars
                wcmqsBlogPostPage.setVisitorEmail(visitorEmail + StringUtils.leftPad("test", 1100, 'a'));
                assertThat("Check if the number of entered chars is 100", wcmqsBlogPostPage.getVisitorEmail().length(), is(equalTo(100)));

                // ---- Step 7 ----
                // ---- Step action ----
                // Fill other fields with correct data and click Post button;
                // ---- Expected results ----
                // Comment is displayed, Email's field data is restricted to 101 symbols;

                wcmqsBlogPostPage.setVisitorName(visitorName);
                wcmqsBlogPostPage.setVisitorWebsite(visitorWebsite);
                wcmqsBlogPostPage.setVisitorComment(visitorComment);
                wcmqsBlogPostPage.clickPostButton();
                assertThat("Posting was succesfull", wcmqsBlogPostPage.isAddCommentMessageDisplay());

                // ---- Step 8 ----
                // ---- Step action ----
                // Open any blog post again;
                // ---- Expected results ----
                // Blog post is opened;

                homePage = new WcmqsHomePage(drone);
                homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
                blogPage = new WcmqsBlogPage(drone);
                blogPage.clickLinkByTitle(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS);

                assertThat("Verify if the correct page opened ", blogPage.getTitle(), containsString(WcmqsBlogPage.ANALYSTS_LATEST_THOUGHTS));

                // ---- Step 9 ----
                // ---- Step action ----
                // Enter too long data(more 1024 characters) in Website field;
                // ---- Expected results ----
                // Data successfully entered;

                // TODO : Test CASE Steps needs to be updated since the data you enter is automatically truncated to 101 chars
                wcmqsBlogPostPage.setVisitorWebsite(visitorWebsite + StringUtils.leftPad("test", 1100, 'a'));
                assertThat("Check if the number of entered chars is 100", wcmqsBlogPostPage.getVisitorWebsite().length(), is(equalTo(100)));

                // ---- Step 10 ----
                // ---- Step action ----
                // Fill other fields with correct data and click Post button;
                // ---- Expected results ----
                // Comment is displayed, Website's field data is restricted to 101 symbols;

                wcmqsBlogPostPage.setVisitorName(visitorName);
                wcmqsBlogPostPage.setVisitorEmail(visitorEmail);
                wcmqsBlogPostPage.setVisitorComment(visitorComment);
                wcmqsBlogPostPage.clickPostButton();
                assertThat("Posting was succesfull", wcmqsBlogPostPage.isAddCommentMessageDisplay());

        }

        /*
        * AONE-5680 Verifying correct work of name field on comment form
        */
        @Test(groups = { "WQS" }) public void AONE_5680() throws Exception
        {
                String visitorName = "name" + getTestName();
                String visitorEmail = getTestName() + "@" + DOMAIN_FREE;
                String visitorWebsite = "website " + getTestName();
                String visitorComment = "Comment by " + visitorName;

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

                WcmqsHomePage homePage = new WcmqsHomePage(drone);
                homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR);
                WcmqsBlogPage blogPage = new WcmqsBlogPage(drone);
                blogPage.clickLinkByTitle(WcmqsBlogPage.COMPANY_ORGANISES_WORKSHOP);
                WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
                wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
                WcmqsBlogPostPage wcmqsBlogPostPage = new WcmqsBlogPostPage(drone);

                // ---- Step 3 ----
                // ---- Step action ----
                // Enter data in Name field ended with space;
                // ---- Expected results ----
                // Data entered successfully;

                wcmqsBlogPostPage.setVisitorName(visitorName);

                // ---- Step 4 ----
                // ---- Step action ----
                // Fill other fields with correct data and click Post button;
                // ---- Expected results ----
                // Data processed correctly, Your comment has been sent message is displayed;

                wcmqsBlogPostPage.setVisitorEmail(visitorEmail);
                wcmqsBlogPostPage.setVisitorWebsite(visitorWebsite);
                wcmqsBlogPostPage.setVisitorComment(visitorComment);
                wcmqsBlogPostPage.clickPostButton();
                assertThat("Posting was succesfull", wcmqsBlogPostPage.isAddCommentMessageDisplay());

        }

        public void navigateTo(String url)
        {
                drone.navigateTo(url);
        }

}
