package org.alfresco.share.enterprise.wqs.web.awe;

import static org.testng.Assert.assertTrue;

import org.alfresco.po.alfresco.WcmqsArticleDetails;
import org.alfresco.po.alfresco.WcmqsEditPage;
import org.alfresco.po.alfresco.WcmqsHomePage;
import org.alfresco.po.alfresco.WcmqsLoginPage;
import org.alfresco.po.alfresco.WcmqsNewsArticleDetails;
import org.alfresco.po.alfresco.WcmqsNewsPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.dashlet.SiteWebQuickStartDashlet;
import org.alfresco.po.share.dashlet.WebQuickStartOptions;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.site.document.ShareRefreshCopyToSites;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class EditingItemsViaAWE extends AbstractUtils
{
    private SharePage page;
    private String testName;
    private String wqsURL;

    private static final Logger logger = Logger.getLogger(ShareRefreshCopyToSites.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        wqsURL = "http://localhost:8080/wcmqs";
        testName = this.getClass().getSimpleName();
        logger.info("Start Tests from: " + testName);
    }

    @Test(groups = { "DataPrepWQS" })
    public void dataPrep_AONE() throws Exception
    {
        String testUser = getUserNameForDomain(testName, DOMAIN_FREE);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

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
        assertTrue(wqsDashlet.isImportMessage());

    }

    /** AONE-5618:Description field(submit editing) */
    @Test(groups = { "WQS" })
    public void AONE_5618() throws Exception
    {
        // ---- Step 4 ----
        // ---- Step Action -----
        // Edit blog post/article form is opened;
        drone.navigateTo(wqsURL);
        WcmqsHomePage wqsPage = new WcmqsHomePage(drone);
        wqsPage.render();
        WcmqsNewsArticleDetails article = wqsPage.selectFirstArticleFromLeftPanel().render();
        WcmqsLoginPage loginPage = new WcmqsLoginPage(drone);
        loginPage.render();
        loginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        article = WcmqsNewsArticleDetails.getCurrentNewsArticlePage(drone);

        // ---- Step 5 ----
        // ---- Step Action -----
        // All mandatory fields are filled with correct information;
        WcmqsEditPage editPage = article.clickEditButton().render();
        WcmqsArticleDetails articleDeatils = editPage.getArticleDetails();
        String shareName = "article3.html";
        Assert.assertEquals(articleDeatils.getName(), shareName);

        // ---- Step 1 ----
        // ---- Step Action -----
        // Change data in Description field;
        // Data is entered successfully;
        String newDescription = "new description-5618";
        editPage.editDescription(newDescription);
        // Assert.assertTrue(editPage.getArticleDetails().getDescription().contains(newDescription));

        // ---- Step 2 ----
        // ---- Step Action -----
        // Click Submit button;
        // Edit blog post/article form is closed, description data is changed;
        editPage.clickSubmitButton();
        WcmqsNewsPage newsPage = new WcmqsNewsPage(drone).render();
        Assert.assertTrue(newsPage.getNewsDescrition(shareName).contains(newDescription));

    }

    /** AONE-5617:Description field(wildcards) */
    @Test(groups = { "WQS" })
    public void AONE_5617() throws Exception
    {
        // ---- Step 4 ----
        // ---- Step Action -----
        // Edit blog post/article form is opened;
        drone.navigateTo(wqsURL);
        WcmqsHomePage wqsPage = new WcmqsHomePage(drone);
        wqsPage.render();
        WcmqsNewsArticleDetails article = wqsPage.selectFirstArticleFromLeftPanel().render();
        WcmqsLoginPage loginPage = new WcmqsLoginPage(drone);
        loginPage.render();
        loginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        article = WcmqsNewsArticleDetails.getCurrentNewsArticlePage(drone);

        // ---- Step 5 ----
        // ---- Step Action -----
        // All mandatory fields are filled with correct information;
        WcmqsEditPage editPage = article.clickEditButton().render();
        WcmqsArticleDetails articleDeatils = editPage.getArticleDetails();
        String shareName = "article3.html";
        Assert.assertEquals(articleDeatils.getName(), shareName);

        // ---- Step 1 ----
        // ---- Step Action -----
        // Change data in Description field;
        // Data is entered successfully;
        String newDescription = "new description~!@#$%^&*(a)";
        editPage.editDescription(newDescription);
        // Assert.assertTrue(editPage.getArticleDetails().getDescription().contains(newDescription));

        // ---- Step 2 ----
        // ---- Step Action -----
        // Click Submit button;
        // Edit blog post/article form is closed, description data is changed;
        editPage.clickSubmitButton();
        WcmqsNewsPage newsPage = new WcmqsNewsPage(drone).render();
        Assert.assertTrue(newsPage.getNewsDescrition(shareName).contains(newDescription));

    }

    /** AONE-5616:Description field(cancel editing) */
    @Test(groups = { "WQS" })
    public void AONE_5616() throws Exception
    {
        // ---- Step 4 ----
        // ---- Step Action -----
        // Edit blog post/article form is opened;
        drone.navigateTo(wqsURL);
        WcmqsHomePage wqsPage = new WcmqsHomePage(drone);
        wqsPage.render();
        WcmqsNewsArticleDetails article = wqsPage.selectFirstArticleFromLeftPanel().render();
        WcmqsLoginPage loginPage = new WcmqsLoginPage(drone);
        loginPage.render();
        loginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        article = WcmqsNewsArticleDetails.getCurrentNewsArticlePage(drone);

        // ---- Step 5 ----
        // ---- Step Action -----
        // All mandatory fields are filled with correct information;
        WcmqsEditPage editPage = article.clickEditButton().render();
        WcmqsArticleDetails articleDeatils = editPage.getArticleDetails();
        String expectedName = "article3.html";
        Assert.assertEquals(articleDeatils.getName(), expectedName);
        // ---- Step 1 ----
        // ---- Step Action -----
        // Change data in Description field;
        // Data is entered successfully;
        String newDescription = "new description-5616";
        editPage.editDescription(newDescription);
        // Assert.assertTrue(editPage.getArticleDetails().getDescription().contains(newDescription));

        // ---- Step 2 ----
        // ---- Step Action -----
        // Click Cancel button;
        // Edit blog post/article form is closed, description data isn't changed;
        editPage.clickCancelButton();
        article = WcmqsNewsArticleDetails.getCurrentNewsArticlePage(drone);
        // the user is returned in the article page. To check if the changed are not present, edit again the blog post/article
        editPage = article.clickEditButton().render();
        Assert.assertFalse(editPage.getArticleDetails().getDescription().contains(newDescription));

    }

    /** AONE-5615:Editing Title field(positive test) */
    @Test(groups = { "WQS" })
    public void AONE_5615() throws Exception
    {
        // ---- Step 4 ----
        // ---- Step Action -----
        // Edit blog post/article form is opened;
        drone.navigateTo(wqsURL);
        WcmqsHomePage wqsPage = new WcmqsHomePage(drone);
        wqsPage.render();
        WcmqsNewsArticleDetails article = wqsPage.selectFirstArticleFromLeftPanel().render();
        WcmqsLoginPage loginPage = new WcmqsLoginPage(drone);
        loginPage.render();
        loginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        article = WcmqsNewsArticleDetails.getCurrentNewsArticlePage(drone);

        // ---- Step 5 ----
        // ---- Step Action -----
        // All mandatory fields are filled with correct information;
        WcmqsEditPage editPage = article.clickEditButton().render();
        WcmqsArticleDetails articleDeatils = editPage.getArticleDetails();
        String shareName = "article3.html";
        Assert.assertEquals(articleDeatils.getName(), shareName);

        // ---- Step 1 ----
        // ---- Step Action -----
        // Change some data in Title field;
        // Data is changed successfully;
        String newTitle = "title-edited";
        editPage.editTitle(newTitle);
        Assert.assertTrue(editPage.getArticleDetails().getTitle().contains(newTitle));

        // ---- Step 2 ----
        // ---- Step Action -----
        // Click Sumbit button;
        // Edit blog post/article form is closed, changes are saved;
        editPage.clickSubmitButton();
        WcmqsNewsPage newsPage = new WcmqsNewsPage(drone);
        Assert.assertTrue(newsPage.getNewsTitle(shareName).contains(newTitle));

    }

    /** AONE-5614:Editing Title field(negative test) */
    @Test(groups = { "WQS" })
    public void AONE_5614() throws Exception
    {
        // ---- Step 4 ----
        // ---- Step Action -----
        // Edit blog post/article form is opened;
        drone.navigateTo(wqsURL);
        WcmqsHomePage wqsPage = new WcmqsHomePage(drone);
        wqsPage.render();
        WcmqsNewsArticleDetails article = wqsPage.selectFirstArticleFromLeftPanel().render();
        WcmqsLoginPage loginPage = new WcmqsLoginPage(drone);
        loginPage.render();
        loginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        article = WcmqsNewsArticleDetails.getCurrentNewsArticlePage(drone);

        // ---- Step 5 ----
        // ---- Step Action -----
        // All mandatory fields are filled with correct information;
        WcmqsEditPage editPage = article.clickEditButton().render();
        WcmqsArticleDetails articleDeatils = editPage.getArticleDetails();
        String expectedName = "article3.html";
        Assert.assertEquals(articleDeatils.getName(), expectedName);

        // ---- Step 1 ----
        // ---- Step Action -----
        // Change some data in Title field;
        // Data is changed successfully;
        String newTitle = "title-edited";
        editPage.editTitle(newTitle);
        Assert.assertTrue(editPage.getArticleDetails().getTitle().contains(newTitle));

        // ---- Step 2 ----
        // ---- Step Action -----
        // Click Cancel button;
        // Edit blog post/article form is closed, changes are not saved;
        editPage.clickCancelButton();
        article = WcmqsNewsArticleDetails.getCurrentNewsArticlePage(drone);
        Assert.assertFalse(article.getTitleOfNewsArticle().contains(newTitle));

    }

    /** AONE-5613:Editing Content field(negative test) */
    @Test(groups = { "WQS" })
    public void AONE_5613() throws Exception
    {

        // ---- Step 4 ----
        // ---- Step Action -----
        // Edit blog post/article form is opened;
        drone.navigateTo(wqsURL);
        WcmqsHomePage wqsPage = new WcmqsHomePage(drone);
        wqsPage.render();
        WcmqsNewsArticleDetails article = wqsPage.selectFirstArticleFromLeftPanel().render();
        WcmqsLoginPage loginPage = new WcmqsLoginPage(drone);
        loginPage.render();
        loginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        article = WcmqsNewsArticleDetails.getCurrentNewsArticlePage(drone);

        // ---- Step 5 ----
        // ---- Step Action -----
        // All mandatory fields are filled with correct information;
        WcmqsEditPage editPage = article.clickEditButton().render();
        WcmqsArticleDetails articleDeatils = editPage.getArticleDetails();
        String expectedName = "article3.html";
        Assert.assertEquals(articleDeatils.getName(), expectedName);

        // ---- Step 1 ----
        // ---- Step Action -----
        // Change some data in Content field;
        // Data is changed successfully;
        String newContent = "content-edited";
        editPage.insertTextInContent(newContent);
        Assert.assertTrue(editPage.getContentTinyMCEEditor().getText().contains(newContent));

        // ---- Step 2 ----
        // ---- Step Action -----
        // Click Cancel button;
        // Edit blog post/article form is closed, changes are not saved;
        editPage.clickCancelButton();
        article = WcmqsNewsArticleDetails.getCurrentNewsArticlePage(drone);
        Assert.assertFalse(article.getBodyOfNewsArticle().contains(newContent));

    }
}
