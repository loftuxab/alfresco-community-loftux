package org.alfresco.share.enterprise.wqs.web.awe;

import static org.alfresco.po.share.site.document.TinyMceEditor.FormatType.BOLD;
import static org.alfresco.po.share.site.document.TinyMceEditor.FormatType.BULLET;
import static org.alfresco.po.share.site.document.TinyMceEditor.FormatType.ITALIC;
import static org.testng.Assert.assertEquals;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.dashlet.SiteWebQuickStartDashlet;
import org.alfresco.po.share.dashlet.WebQuickStartOptions;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.enums.TinyMceColourCode;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.site.document.TinyMceEditor;
import org.alfresco.po.share.site.document.TinyMceEditor.FormatType;
import org.alfresco.po.share.wqs.WcmqsArticleDetails;
import org.alfresco.po.share.wqs.WcmqsEditPage;
import org.alfresco.po.share.wqs.WcmqsHomePage;
import org.alfresco.po.share.wqs.WcmqsLoginPage;
import org.alfresco.po.share.wqs.WcmqsNewsArticleDetails;
import org.alfresco.po.share.wqs.WcmqsNewsPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.alfresco.test.FailedTestListener;
import org.apache.log4j.Logger;
import org.openqa.selenium.Keys;
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
    private String siteName;
    private String ipAddress;
    private String hostName;

    private static final Logger logger = Logger.getLogger(EditingItemsViaAWE.class);

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

        ;
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

    /*
     * This dataPrep creates the precondition for all tests in this class: from AONE_5607 to AONE_5618
     */
    @Test(groups = { "DataPrepWQS" })
    public void dataPrep_AONE_5607() throws Exception
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

        // Change property for quick start to sitename
        DocumentLibraryPage documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        documentLibPage.selectFolder("Alfresco Quick Start");
        EditDocumentPropertiesPage documentPropertiesPage = documentLibPage.getFileDirectoryInfo("Quick Start Editorial").selectEditProperties().render();
        documentPropertiesPage.setSiteHostname(siteName);
        documentPropertiesPage.clickSave();

        // Change property for quick start live to ip address
        documentLibPage.getFileDirectoryInfo("Quick Start Live").selectEditProperties().render();
        documentPropertiesPage.setSiteHostname(ipAddress);
        documentPropertiesPage.clickSave();

        // setup new entry in hosts to be able to access the new wcmqs site
        String setHostAddress = "cmd.exe /c echo. >> %WINDIR%\\System32\\Drivers\\Etc\\hosts && echo " + ipAddress + " " + siteName
                + " >> %WINDIR%\\System32\\Drivers\\Etc\\hosts";
        Runtime.getRuntime().exec(setHostAddress);
    }

    /**
     * AONE-5607:Verify correct displaying of Edit blog post/article page
     */
    @Test(groups = "WQS")
    public void AONE_5607() throws Exception
    {
        // ---- Step 1 ----
        // ---- Step action ---
        // Navigate to http://host:8080/wcmqs
        // ---- Expected results ----
        // Sample site is opened
        drone.navigateTo(wqsURL);

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

        WcmqsEditPage wcmqsEditPage = wcmqsNewsArticleDetails.clickEditButton();
        wcmqsEditPage.render();

        // ---- Step 4 ----
        // ---- Step action ---
        // Verify the presence of all fields on the form
        // ---- Expected results ----
        // The form contains of fields: Name(mandatory), Title, Description,
        // Content, Template Name. It also contains Submit and Cancel buttons
        Assert.assertTrue(wcmqsEditPage.isNameFieldDisplayed(), "Name field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isNameFieldMandatory(), "Name field is not mandatory");
        Assert.assertTrue(wcmqsEditPage.isTitleFieldDisplayed(), "Title field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isDescriptionFieldDisplayed(), "Description field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isContentFrameDisplayed(), "Content frame is not displayed");
        Assert.assertTrue(wcmqsEditPage.isTemplateNameDisplayed(), "Template Name field is not displayed");
        Assert.assertTrue(wcmqsEditPage.isSubmitButtonDisplayed(), "Submit button is not displayed");
        Assert.assertTrue(wcmqsEditPage.isCancelButtonDisplayed(), "Cancel button is not displayed");
    }

    /**
     * AONE-5608:Editing blog post/article, Name(negative test with spaces)
     */
    @Test(groups = "WQS")
    public void AONE_5608() throws Exception
    {
        drone.navigateTo(wqsURL);
        drone.maximize();

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
        String name = wcmqsEditPage.getArticleDetails().getName();
        wcmqsEditPage.editName(name);

        // ---- Step 2 ----
        // ---- Step action ---
        // Verify Submit button
        // ---- Expected results ----
        // Submit button is active
        Assert.assertTrue(wcmqsEditPage.isSubmitButtonDisplayed(), "Submit button is not displayed.");

        // ---- Step 3 ----
        // ---- Step action ---
        // Fill Name field with spaces;
        // ---- Expected results ----
        // Data is entered successfully;
        String newName = "ar tic le" + getTestName() + ".html";
        wcmqsEditPage.editName(newName);
        wcmqsEditPage.moveFocusToTitle();
        Assert.assertTrue(wcmqsEditPage.getArticleDetails().getName().contains(newName), "The value inserted does not have spaces.");

        // ---- Step 4 ----
        // ---- Step action ---
        // Verify Submit button
        // ---- Expected results ----
        // Submit button isn't active/friendly notification is displayed;
        Assert.assertEquals(wcmqsEditPage.getNotificationMessage(), "The value cannot have spaces.");

    }

    /**
     * AONE-5609:Editing blog post/article, Name(negative test, empty field)
     */
    @Test(groups = "WQS")
    public void AONE_5609() throws Exception
    {
        drone.navigateTo(wqsURL);
        drone.maximize();

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
        wcmqsEditPage.render();
        String name = wcmqsEditPage.getArticleDetails().getName();
        wcmqsEditPage.editName(name);

        // ---- Step 2 ----
        // ---- Step action ---
        // Verify Submit button
        // ---- Expected results ----
        // Submit button is active
        Assert.assertTrue(wcmqsEditPage.isSubmitButtonDisplayed(), "Submit button is not displayed.");

        // ---- Step 3 ----
        // ---- Step action ---
        // Leave Name field empty
        // ---- Expected results ----
        // Name field is empty
        wcmqsEditPage.sendKeyOnName(Keys.RETURN);

        // ---- Step 4 ----
        // ---- Step action ---
        // Verify Submit button
        // ---- Expected results ----
        // Submit button isn't active/friendly notification is displayed
        // wcmqsEditPage.clickSubmitButton();
        Assert.assertEquals(wcmqsEditPage.getNotificationMessage(), "The value cannot be empty.");

    }

    /**
     * AONE-5610:Editing blog post/article, Name(negative test with wildcards)
     */
    @Test(groups = "WQS", enabled = true)
    public void AONE_5610() throws Exception
    {
        drone.navigateTo(wqsURL);

        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        wcmqsHomePage.selectFirstArticleFromLeftPanel();

        // Login

        WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
        wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        WcmqsNewsArticleDetails wcmqsNewsArticleDetails = new WcmqsNewsArticleDetails(drone);
        WcmqsEditPage wcmqsEditPage = wcmqsNewsArticleDetails.clickEditButton().render();

        // ---- Step 1 ----
        // ---- Step action ---
        // Fill all mandatory fields with correct information
        // ---- Expected results ----
        // Data is entered successfully
        String name = wcmqsEditPage.getArticleDetails().getName();
        wcmqsEditPage.editName(name);

        // ---- Step 2 ----
        // ---- Step action ---
        // Verify Submit button
        // ---- Expected results ----
        // Submit button is active
        Assert.assertTrue(wcmqsEditPage.isSubmitButtonDisplayed(), "Submit button is not displayed.");

        // ---- Step 3 ----
        // ---- Step action ---
        // Fill Name field with wildcards
        // ---- Expected results ----
        // Data is entered successfully
        String name2 = "a *";
        wcmqsEditPage.editName(name2);

        // ---- Step 4 ----
        // ---- Step action ---
        // Verify Submit button
        // ---- Expected results ----
        // Submit button isn't active/friendly notification is displayed
        Assert.assertEquals(wcmqsEditPage.getNotificationMessage(), "Value contains illegal characters.");

    }

    /**
     * AONE-5611:Editing blog post/article, Name(positive test)
     */
    @Test(groups = "WQS", enabled = true)
    public void AONE_5611() throws Exception
    {
        drone.navigateTo(wqsURL);

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
        String newName = "article3.html";
        wcmqsEditPage.editName(newName);
        Assert.assertTrue(wcmqsEditPage.getArticleDetails().getName().contains(newName), "The input data is not: " + newName);

        // ---- Step 2 ----
        // ---- Step action ---
        // Click Submit button
        // ---- Expected results ----
        // Edit blog post/article form is closed, data is changed successfully
        wcmqsEditPage.clickSubmitButton();
        WcmqsNewsPage newsPage = new WcmqsNewsPage(drone);
        Assert.assertNotNull(newsPage.render(), "News Page is not redered.");

    }

    /**
     * AONE-5612:Editing Content field
     */
    @Test(groups = "WQS", enabled = true)
    public void AONE_5612() throws Exception
    {
        String textBold = "Bold Text";
        String textItalic = "Italic Text";
        String textUnderlined = "Underlined Text";
        String textBullet = "Bullet Text";
        String colorText = "Color Text";

        drone.navigateTo(wqsURL);

        WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
        WcmqsNewsArticleDetails wcmqsNewsArticleDetails = new WcmqsNewsArticleDetails(drone);
        wcmqsHomePage.selectFirstArticleFromLeftPanel();

        // Login

        WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
        wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);

        WcmqsEditPage wcmqsEditPage = wcmqsNewsArticleDetails.clickEditButton();
        wcmqsEditPage.render();

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
        Assert.assertTrue(wcmqsEditPage.isSubmitButtonDisplayed(), "Submit button is not displayed.");

        if (!AlfrescoVersion.Enterprise42.equals(alfrescoVersion) && !AlfrescoVersion.Enterprise43.equals(alfrescoVersion))
        {
            // ---- Step 3 ----
            // ---- Step action ---
            // Enter some valid data and apply Bold, Italic and Underline styles for it
            // ---- Expected results ----
            // Styles are applied successfully
            String content2 = "test";
            wcmqsEditPage.insertTextInContent(content2);

            TinyMceEditor tinyMceEditor = wcmqsEditPage.getContentTinyMCEEditor();
            tinyMceEditor.setText(textBold);
            tinyMceEditor.clickTextFormatter(BOLD);
            assertEquals(tinyMceEditor.getContent(), String.format("<p><strong>%s</strong></p>", textBold), "The text didn't mark as bold.");

            tinyMceEditor.setText(textItalic);
            tinyMceEditor.clickTextFormatter(ITALIC);
            assertEquals(tinyMceEditor.getContent(), String.format("<p><em>%s</em></p>", textItalic), "The text didn't italic.");

            tinyMceEditor.setText(textUnderlined);
            tinyMceEditor.clickFormat();
            tinyMceEditor.clickTextFormatter(FormatType.UNDERLINED);
            assertEquals(tinyMceEditor.getContent(), String.format("<p><span style=\"text-decoration: underline;\">%s</span></p>", textUnderlined),
                    "The text didn't underlined.");

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
            // Click Undo button (In order to click Undo button, Edit button should be clicked first)
            // ---- Expected results ----
            // Last formatting action is canceled
            String editedText = "text to undo";
            tinyMceEditor.setText(editedText);
            tinyMceEditor.clickEdit();
            tinyMceEditor.clickUndo();
            Assert.assertFalse(tinyMceEditor.getContent().contains(editedText), "The changes are not undo. The text: " + editedText + " is still present.");

            // ---- Step 7 ----
            // ---- Step action ---
            // Click Redo button (In order to click Undo button, Edit button should be clicked first)
            // ---- Expected results ----
            // Last formatting action is redone
            tinyMceEditor.clickEdit();
            tinyMceEditor.clickRedo();
            Assert.assertTrue(tinyMceEditor.getContent().contains(editedText), "The changes are not redo. The text: " + editedText + " is not present.");

            // ---- Step 8 ----
            // ---- Step action ---
            // Highlight some text and click Remove formatting button (Edit menu is already expanded, Format menu should be clicked twice)
            // ---- Expected results ----
            // Formatting is removed
            tinyMceEditor.selectTextFromEditor();
            tinyMceEditor.clickFormat();
            tinyMceEditor.clickFormat();
            tinyMceEditor.removeFormatting();
            Assert.assertNotEquals(tinyMceEditor.getContent(), String.format("<p><span style=\"color: rgb(0, 0, 255);\">%s</span></p>", colorText),
                    "Text didn't get colored.");

        }

        // ---- Step 9 ----
        // ---- Step action ---
        // Click Submit button
        // ---- Expected results ----
        // Edit blog post/article form is closed, changes are saved
        wcmqsEditPage.clickSubmitButton();
        WcmqsNewsPage newsPage = new WcmqsNewsPage(drone);
        Assert.assertNotNull(newsPage.render(), "News Page is not redered.");

    }

    /**
     * AONE-5613:Editing Content field(negative test)
     */
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
        WcmqsArticleDetails articleDetils = editPage.getArticleDetails();
        String expectedName = "article3.html";
        Assert.assertEquals(articleDetils.getName(), expectedName, "The article name is not: " + expectedName);

        // ---- Step 1 ----
        // ---- Step Action -----
        // Change some data in Content field;
        // ---- Expected results ----
        // Data is changed successfully;
        String newContent = "content " + getTestName();
        editPage.insertTextInContent(newContent);
        if (AlfrescoVersion.Enterprise42.equals(alfrescoVersion) || AlfrescoVersion.Enterprise43.equals(alfrescoVersion))
        {
            Assert.assertTrue(editPage.getContentTextarea().contains(newContent), "The article content does not contain: " + newContent);
        }
        else
        {
            Assert.assertTrue(editPage.getContentTinyMCEEditor().getText().contains(newContent), "The article content does not contain: " + newContent);
        }


        // ---- Step 2 ----
        // ---- Step Action -----
        // Click Cancel button;
        // ---- Expected results ----
        // Edit blog post/article form is closed, changes are not saved;
        editPage.clickCancelButton();
        article = WcmqsNewsArticleDetails.getCurrentNewsArticlePage(drone);
        Assert.assertFalse(article.getBodyOfNewsArticle().contains(newContent), "The edit article was not cancelled. The article content contains: "
                + newContent);
    }

    /**
     * AONE-5614:Editing Title field(negative test)
     */
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
        Assert.assertEquals(articleDeatils.getName(), expectedName, "The article name is not: " + expectedName);

        // ---- Step 1 ----
        // ---- Step Action -----
        // Change some data in Title field;
        // ---- Expected results ----
        // Data is changed successfully;
        String newTitle = "title " + getTestName();
        editPage.editTitle(newTitle);
        Assert.assertTrue(editPage.getArticleDetails().getTitle().contains(newTitle), "The article title does not contain: " + newTitle);

        // ---- Step 2 ----
        // ---- Step Action -----
        // Click Cancel button;
        // ---- Expected results ----
        // Edit blog post/article form is closed, changes are not saved;
        editPage.clickCancelButton();
        article = WcmqsNewsArticleDetails.getCurrentNewsArticlePage(drone);
        Assert.assertFalse(article.getTitleOfNewsArticle().contains(newTitle), "The edit article was not cancelled. The article title contains: " + newTitle);
    }

    /**
     * AONE-5615:Editing Title field(positive test)
     */
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
        Assert.assertEquals(articleDeatils.getName(), shareName, "The article name is not: " + shareName);

        // ---- Step 1 ----
        // ---- Step Action -----
        // Change some data in Title field;
        // ---- Expected results ----
        // Data is changed successfully;
        String newTitle = "title " + getTestName();
        editPage.editTitle(newTitle);
        Assert.assertTrue(editPage.getArticleDetails().getTitle().contains(newTitle), "The article title does not contain: " + newTitle);

        // ---- Step 2 ----
        // ---- Step Action -----
        // Click Sumbit button;
        // ---- Expected results ----
        // Edit blog post/article form is closed, changes are saved;
        editPage.clickSubmitButton();
        WcmqsNewsPage newsPage = new WcmqsNewsPage(drone);
        newsPage.render();
        Assert.assertTrue(newsPage.getNewsTitle(shareName).contains(newTitle), "The edit article was not saved. The article title does not contain: "
                + newTitle);

    }

    /**
     * AONE-5616:Description field(cancel editing)
     */
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
        Assert.assertEquals(articleDeatils.getName(), expectedName, "The article name is not: " + expectedName);

        // ---- Step 1 ----
        // ---- Step Action -----
        // Change data in Description field;
        // ---- Expected results ----
        // Data is entered successfully;
        String newDescription = "new description " + getTestName();
        editPage.editDescription(newDescription);        
        Assert.assertTrue(editPage.getArticleDetails().getDescription().contains(newDescription), "The article description does not contain: " + newDescription);

        // ---- Step 2 ----
        // ---- Step Action -----
        // Click Cancel button;
        // ---- Expected results ----
        // Edit blog post/article form is closed, description data isn't changed;
        editPage.clickCancelButton();
        article = WcmqsNewsArticleDetails.getCurrentNewsArticlePage(drone);
        // the user is returned in the article page. To check if the changed are not present, edit again the blog post/article
        editPage = article.clickEditButton().render();
        Assert.assertFalse(editPage.getArticleDetails().getDescription().contains(newDescription),
                "The edit article was not changed. The article description contains: " + newDescription);

    }

    /**
     * AONE-5617:Description field(wildcards)
     */
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
        Assert.assertEquals(articleDeatils.getName(), shareName, "The article name is not: " + shareName);

        // ---- Step 1 ----
        // ---- Step Action -----
        // Change data in Description field;
        // ---- Expected results ----
        // Data is entered successfully;
        String newDescription = "new description~!@#$%^&*(a)";
        editPage.editDescription(newDescription);
        Assert.assertTrue(editPage.getArticleDetails().getDescription().contains(newDescription), "The article description does not contain: " + newDescription);

        // ---- Step 2 ----
        // ---- Step Action -----
        // Click Submit button;
        // ---- Expected results ----
        // Edit blog post/article form is closed, description data is changed;
        editPage.clickSubmitButton();
        WcmqsNewsPage newsPage = new WcmqsNewsPage(drone).render();
        Assert.assertTrue(newsPage.getNewsDescrition(shareName).contains(newDescription),
                "The edit article was not saved. The article description deos not contain: " + newDescription);

    }

    /**
     * AONE-5618:Description field(submit editing)
     */
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
        Assert.assertEquals(articleDeatils.getName(), shareName, "The article name is not: " + shareName);

        // ---- Step 1 ----
        // ---- Step Action -----
        // Change data in Description field;
        // ---- Expected results ----
        // Data is entered successfully;
        String newDescription = "new description " + getTestName();
        editPage.editDescription(newDescription);
        Assert.assertTrue(editPage.getArticleDetails().getDescription().contains(newDescription), "The article description does not contain: " + newDescription);

        // ---- Step 2 ----
        // ---- Step Action -----
        // Click Submit button;
        // ---- Expected results ----
        // Edit blog post/article form is closed, description data is changed;
        editPage.clickSubmitButton();
        WcmqsNewsPage newsPage = new WcmqsNewsPage(drone).render();
        Assert.assertTrue(newsPage.getNewsDescrition(shareName).contains(newDescription),
                "The edit article was not saved. The article description deos not contain: " + newDescription);

    }
}
