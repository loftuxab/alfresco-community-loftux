package org.alfresco.share.enterprise.wqs.web.awe;

import static org.alfresco.po.share.site.document.TinyMceEditor.FormatType.BOLD;
import static org.alfresco.po.share.site.document.TinyMceEditor.FormatType.BULLET;
import static org.alfresco.po.share.site.document.TinyMceEditor.FormatType.ITALIC;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.alfresco.WcmqsArticleDetails;
import org.alfresco.po.alfresco.WcmqsEditPage;
import org.alfresco.po.alfresco.WcmqsHomePage;
import org.alfresco.po.alfresco.WcmqsLoginPage;
import org.alfresco.po.alfresco.WcmqsNewsArticleDetails;
import org.alfresco.po.alfresco.WcmqsNewsPage;
import org.alfresco.po.share.dashlet.SiteWebQuickStartDashlet;
import org.alfresco.po.share.dashlet.WebQuickStartOptions;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.enums.TinyMceColourCode;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.TinyMceEditor;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class EditingItemsViaAWE extends AbstractUtils
{
    private String testName;
    private String wqsURL;

    private static final Logger logger = Logger.getLogger(EditingItemsViaAWE.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        wqsURL = "http://localhost:8080/wcmqs";
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
    
    @Test(groups = "WQS")
    public void AONE_5607() throws Exception
    {
        // ---- Step 1 ----
        // ---- Step action ---
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        //  Sample site is opened
    
        drone.navigateTo("http://localhost:8080/wcmqs/");
        
        // ---- Step 2 ----
        // ---- Step action ---
        // Open any blog post/article
        // ---- Expected results ----
        // Blog post/article is opened
        
        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        wcmqsHomePage.selectFirstArticleFromLeftPanel();
        
        // Login
        WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
        wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        WcmqsNewsArticleDetails wcmqsNewsArticleDetails = new WcmqsNewsArticleDetails(drone);
        
        // ---- Step 3 ----
        // ---- Step action ---
        // Click Edit button near blog post/article(login as admin, if required)
        // ---- Expected results ----
        // Edit window is opened
                
        wcmqsNewsArticleDetails.clickEditButton();
        
        // ---- Step 4 ----
        // ---- Step action ---
        // Verify the presence of all fields on the form
        // ---- Expected results ----
        // The form contains of fields: Name(mandatory), Title, Description,
        // Content, Template Name. It also contains Submit and Cancel buttons
        WcmqsArticleDetails wcmqsArticleDetails = new WcmqsArticleDetails();
        wcmqsArticleDetails.getName();
        wcmqsArticleDetails.getTitle();
        wcmqsArticleDetails.getDescription();
        wcmqsArticleDetails.getContent();
        wcmqsArticleDetails.getTemplateName();
    }
    
    @Test(groups = "WQS")
    public void AONE_5609() throws Exception
    {
        drone.navigateTo("http://localhost:8080/wcmqs/");
        
        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        wcmqsHomePage.selectFirstArticleFromLeftPanel();

        // Login
        WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
        wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        WcmqsNewsArticleDetails wcmqsNewsArticleDetails = new WcmqsNewsArticleDetails(drone);
        WcmqsEditPage wcmqsEditPage = wcmqsNewsArticleDetails.clickEditButton();
        
        
        // ---- Step 1 ----
        // ---- Step action ---
        // Fill all mandatory fields with correct information
        // ---- Expected results ----
        // Data is entered successfully
    
        WcmqsArticleDetails wcmqsArticleDetails = new WcmqsArticleDetails();
        
        String name = wcmqsArticleDetails.getName();
        wcmqsArticleDetails.setName(name);
        
        String title= wcmqsArticleDetails.getTitle();
        wcmqsArticleDetails.setTitle(title);
        
        String description = wcmqsArticleDetails.getDescription();
        wcmqsArticleDetails.setDescription(description);
        
        String content = wcmqsArticleDetails.getContent();
        wcmqsArticleDetails.setContent(content);
        
        String template = wcmqsArticleDetails.getTemplateName();
        wcmqsArticleDetails.setTemplateName(template);
        
        // ---- Step 2 ----
        // ---- Step action ---
        // Verify Submit button
        // ---- Expected results ----
        // Submit button is active
        
        
         wcmqsEditPage.clickSubmitButton();
        
        // ---- Step 3 ----
        // ---- Step action ---
        // Leave Name field empty
        // ---- Expected results ----
        // Name field is empty
         wcmqsHomePage.selectFirstArticleFromLeftPanel();
         wcmqsNewsArticleDetails.clickEditButton();
         String name2 = "";
         wcmqsEditPage.editName(name2);
             
        
        // ---- Step 4 ----
        // ---- Step action ---
        // Verify Submit button
        // ---- Expected results ----
        // Submit button isn't active/friendly notification is displayed
        
         wcmqsEditPage.clickSubmitButton();
         wcmqsEditPage.clickSubmitButton();

    }
    
    @Test(groups = "WQS", enabled = true)
    public void AONE_5610() throws Exception
    {
        drone.navigateTo("http://localhost:8080/wcmqs/");
        
        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        wcmqsHomePage.selectFirstArticleFromLeftPanel();
        
        // Login
        
        WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
        wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        WcmqsNewsArticleDetails wcmqsNewsArticleDetails = new WcmqsNewsArticleDetails(drone);
        WcmqsEditPage wcmqsEditPage = wcmqsNewsArticleDetails.clickEditButton();
        
        // ---- Step 1 ----
        // ---- Step action ---
        // Fill all mandatory fields with correct information
        // ---- Expected results ----
        // Data is entered successfully
    
        WcmqsArticleDetails wcmqsArticleDetails = new WcmqsArticleDetails();
        
        String title= wcmqsArticleDetails.getTitle();
        wcmqsEditPage.editTitle(title);
        
        String description = wcmqsArticleDetails.getDescription();
        wcmqsEditPage.editDescription(description);
        
        String template = wcmqsArticleDetails.getTemplateName();
        wcmqsEditPage.editTemplateName(template);
        
        // ---- Step 2 ----
        // ---- Step action ---
        // Verify Submit button
        // ---- Expected results ----
        // Submit button is active
        
        wcmqsEditPage.clickSubmitButton();
        
        // ---- Step 3 ----
        // ---- Step action ---
        // Fill Name field with wildcards
        // ---- Expected results ----
        // Data is entered successfully
        
        drone.navigateTo("http://localhost:8080/wcmqs/");                  
        wcmqsHomePage.selectFirstArticleFromLeftPanel();
        wcmqsNewsArticleDetails.clickEditButton();
        String name2 = " *";
        wcmqsEditPage.editName(name2);
        
        // ---- Step 4 ----
        // ---- Step action ---
        // Verify Submit button
        // ---- Expected results ----
        // Submit button isn't active/friendly notification is displayed
        
        wcmqsEditPage.clickSubmitButton();
        wcmqsEditPage.clickSubmitButton();
        
    }
    
    @Test(groups = "WQS", enabled = true)
    public void AONE_5611() throws Exception
    {
        drone.navigateTo("http://localhost:8080/wcmqs/");
        
        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        wcmqsHomePage.selectFirstArticleFromLeftPanel();
        
        // Login
        
        WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
        wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        WcmqsNewsArticleDetails wcmqsNewsArticleDetails = new WcmqsNewsArticleDetails(drone);
        WcmqsEditPage wcmqsEditPage = wcmqsNewsArticleDetails.clickEditButton();
        
        // ---- Step 1 ----
        // ---- Step action ---
        // Enter some new name in Name field;
        // ---- Expected results ----
        // Data is entered successfully
    
//        WcmqsArticleDetails wcmqsArticleDetails = new WcmqsArticleDetails();
        
//        String name = wcmqsArticleDetails.getName();
//        wcmqsEditPage.editName(name);
        wcmqsEditPage.editName("article2.html");
        
        
        // ---- Step 2 ----
        // ---- Step action ---
        // Click Submit button
        // ---- Expected results ----
        // Edit blog post/article form is closed, data is changed successfully
        
        wcmqsEditPage.clickSubmitButton();

    }
    
    @Test(groups = "WQS", enabled = true)
    public void AONE_5612() throws Exception
    {
        String textBold = "Bold Text";
        String textItalic = "Italic Text";
        String textBullet = "Bullet Text";
        String colorText = "Color Text";
    	
        drone.navigateTo("http://localhost:8080/wcmqs/");
        
        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        WcmqsNewsArticleDetails wcmqsNewsArticleDetails = new WcmqsNewsArticleDetails(drone);
        wcmqsHomePage.selectFirstArticleFromLeftPanel();
        
        // Login
        
        WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
        wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        
        WcmqsEditPage wcmqsEditPage = wcmqsNewsArticleDetails.clickEditButton();
        
        // ---- Step 1 ----
        // ---- Step action ---
        // Leave Content field empty
        // ---- Expected results ----
        // Content field is empty
 
        String content = "";
        wcmqsEditPage.insertTextInContent(content);
        
        // ---- Step 2 ----
        // ---- Step action ---
        // Verify Submit button status
        // ---- Expected results ----
        // Submit button is active
        
        wcmqsEditPage.clickSubmitButton();
        
        // ---- Step 3 ----
        // ---- Step action ---
        // Enter some valid data and apply Bold, Italic and Underline styles for it
        // ---- Expected results ----
        // Styles are applied successfully
                  
        drone.navigateTo("http://localhost:8080/wcmqs/");                  
        wcmqsHomePage.selectFirstArticleFromLeftPanel();
        wcmqsNewsArticleDetails.clickEditButton();
        
        String content2 = "test";
        wcmqsEditPage.insertTextInContent(content2);
        
        TinyMceEditor tinyMceEditor = wcmqsEditPage.getContentTinyMCEEditor();
        tinyMceEditor.setText(textBold);
        tinyMceEditor.clickTextFormatter(BOLD);
        assertEquals(tinyMceEditor.getContent(), String.format("<p><strong>%s</strong></p>", textBold), "The text didn't mark as bold.");
        
        tinyMceEditor.setText(textItalic);
        tinyMceEditor.clickTextFormatter(ITALIC);
        assertEquals(tinyMceEditor.getContent(), String.format("<p><em>%s</em></p>", textItalic), "The text didn't italic.");
               
        // ---- Step 4 ----
        // ---- Step action ---
        // Try to paste any Unodered and Odered list
        // ---- Expected results ----
        // Lists are pasted successfully
        
        tinyMceEditor.setText(textBullet);
        tinyMceEditor.clickTextFormatter(BULLET);
        assertEquals(tinyMceEditor.getContent(), String.format("<ul style=\"\"><li>%s</li></ul>", textBullet), "List didn't display.");

        
        // ---- Step 5 ----
        // ---- Step action ---
        // Change color for some text
        // ---- Expected results ----
        // Color is changed successfully
        
        tinyMceEditor.setText(colorText);
        tinyMceEditor.clickColorCode(TinyMceColourCode.BLUE);
        assertEquals(tinyMceEditor.getContent(), String.format("<p><span style=\"color: rgb(0, 0, 255);\">%s</span></p>", colorText),
                "Text didn't get colored.");
        
        // ---- Step 6 ----
        // ---- Step action ---
        //  Click Undo button
        // ---- Expected results ----
        // Last formatting action is canceled
        
        tinyMceEditor.clickEdit();
        
        tinyMceEditor.clickUndo();
                        
        // ---- Step 7 ----
        // ---- Step action ---
        // Click Redo button
        // ---- Expected results ----
        // Last formatting action is redone
        tinyMceEditor.clickEdit();
        
        tinyMceEditor.clickRedo();
        
        // ---- Step 8 ----
        // ---- Step action ---
        // Highlight some text and click Remove formatting button
        // ---- Expected results ----
        // Formatting is removed
        
        tinyMceEditor.clickFormat();
        tinyMceEditor.clickFormat();
        tinyMceEditor.removeFormatting();
        
        // ---- Step 9 ----
        // ---- Step action ---
        // Click Submit button
        // ---- Expected results ----
        // Edit blog post/article form is closed, changes are saved
        wcmqsEditPage.clickSubmitButton();
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
        String newContent = "content " + getTestName();
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
        String newTitle = "title " + getTestName();
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
        String newTitle = "title " + getTestName();
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
        String newDescription = "new description " + getTestName();
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
        String newDescription = "new description " + getTestName();
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
}
