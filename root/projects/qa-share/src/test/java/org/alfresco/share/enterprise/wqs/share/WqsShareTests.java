package org.alfresco.share.enterprise.wqs.share;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.dashlet.AdvancedTinyMceEditor;
import org.alfresco.po.share.dashlet.InsertOrEditLinkPage;
import org.alfresco.po.share.dashlet.SiteWebQuickStartDashlet;
import org.alfresco.po.share.dashlet.WebQuickStartOptions;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CreateSitePage;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteType;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.site.document.EditHtmlDocumentPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.document.InlineEditPage;
import org.alfresco.po.share.site.document.MimeType;
import org.alfresco.po.share.wqs.WcmqsAllPublicationsPage;
import org.alfresco.po.share.wqs.WcmqsArticleDetails;
import org.alfresco.po.share.wqs.WcmqsEditPage;
import org.alfresco.po.share.wqs.WcmqsHomePage;
import org.alfresco.po.share.wqs.WcmqsLoginPage;
import org.alfresco.po.share.wqs.WcmqsNewsArticleDetails;
import org.alfresco.po.share.wqs.WcmqsNewsPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.test.FailedTestListener;
import org.apache.log4j.Logger;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class WqsShareTests extends AbstractUtils
{
    private static final Logger logger = Logger.getLogger(WqsShareTests.class);
    String newsName;;
    String wcmqsURL;
    String siteName;
    public static final String ALFRESCO_QUICK_START = "Alfresco Quick Start";
    public static final String QUICK_START_EDITORIAL = "Quick Start Editorial";
    public static final String QUICK_START_LIVE = "Quick Start Live";
    public static final String ROOT = "root";
    public static final String NEWS = "news";
    public static final String INDEX_HTML = "slide1.html";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        newsName = "cont2" + getFileName(testName) + ".html";
        siteName = getSiteName(testName);
        logger.info("Start tests:" + testName);

        wcmqsURL = wcmqs;
        logger.info(" wcmqs url : " + wcmqsURL);
    }

    @BeforeMethod(alwaysRun = true, groups = { "WQS" })
    public void testSetup() throws Exception
    {
        super.setup();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown()
    {
        try
        {
            drone.quit();
        }
        catch (UnreachableBrowserException e)
        {
            logger.error("Browser communication failed.");
        }
    }

    @Test(groups = { "DataPrepWQS", "EnterpriseOnly" })
    public void dataPrep() throws Exception
    {
        // Login
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render();
        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName);

        CustomiseSiteDashboardPage customiseSiteDashboardPage = siteDashboardPage.getSiteNav().selectCustomizeDashboard().render();
        siteDashboardPage = customiseSiteDashboardPage.addDashlet(Dashlets.WEB_QUICK_START, 1).render();

        SiteWebQuickStartDashlet wqsDashlet = siteDashboardPage.getDashlet(SITE_WEB_QUICK_START_DASHLET).render();
        wqsDashlet.selectWebsiteDataOption(WebQuickStartOptions.FINANCE);
        wqsDashlet.clickImportButtton();
        wqsDashlet.waitForImportMessage();

        ShareUser.logout(drone);
    }

    /** AONE-5595:Creating web site in Share */
    @Test(groups = { "WQSShare", "EnterpriseOnly" })
    public void AONE_5595() throws Exception
    {
        // --- Step 1 ---
        // --- Step action ---
        // Click Create site link;
        // --- Expected results ---
        // Create Site window is opened;

        String testName = getTestName();
        String user1 = getUserNameForDomain(testName + "-op1", DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String siteName = testName + "SiteName";
        String siteURL = testName + "SiteURL";

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        SharePage page = ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        CreateSitePage createSitePage = page.getNav().selectCreateSite().render();
        assertTrue(createSitePage.isCreateSiteDialogDisplayed());

        // --- Step 2 ---
        // --- Step action ---
        // Fill in mandatory fields:
        // Name: My Web Site URL Name: MyWebSite Type: Collaboration site Visibility: Public
        // --- Expected results ---
        // Data is entered successfully;

        createSitePage.setSiteName(siteName);
        createSitePage.setSiteURL(siteURL);
        createSitePage.selectSiteType(SiteType.COLLABORATION);
        createSitePage.selectSiteVisibility(false, false);

        Assert.assertEquals(createSitePage.getSiteName(), siteName);
        Assert.assertEquals(createSitePage.getSiteUrl(), siteURL);
        Assert.assertEquals(createSitePage.getSiteType().get(0), "Collaboration Site");

        // --- Step 3 ---
        // --- Step action ---
        // Click OK button;
        // --- Expected results ---
        // Site is created, Site dashboard page is opened;

        SiteDashboardPage siteDashboardPage = createSitePage.selectOk().render();
        assertTrue(siteDashboardPage.isSiteTitle(siteName));

        // --- Step 4 ---
        // --- Step action ---
        // Add "WCM Quick Start" dashlet to site dashboard
        // --- Expected results ---
        // Dashlet is added to dashboard;

        CustomiseSiteDashboardPage customiseSiteDashboardPage = siteDashboardPage.getSiteNav().selectCustomizeDashboard().render();
        siteDashboardPage = customiseSiteDashboardPage.addDashlet(Dashlets.WEB_QUICK_START, 1).render();

        List<String> dashletTitles = siteDashboardPage.getTitlesList();
        Assert.assertTrue(dashletTitles.contains("Web Quick Start"));

        // --- Step 5 ---
        // --- Step action ---
        // Click 'Import Web Site Data' link on WCM Quick Start dashlet
        // --- Expected results ---
        // "Web Site data import successful" notification is dislpayed;

        SiteWebQuickStartDashlet wqsDashlet = siteDashboardPage.getDashlet(SITE_WEB_QUICK_START_DASHLET).render();
        wqsDashlet.selectWebsiteDataOption(WebQuickStartOptions.FINANCE);
        wqsDashlet.clickImportButtton();

        assertTrue(wqsDashlet.isImportMessageDisplayed());

        ShareUser.logout(drone);
    }

    /** AONE-5596:Opening sample site for preview */
    @Test(groups = { "WQSShare", "EnterpriseOnly" })
    public void AONE_5596() throws Exception
    {
        // ---- Step 1 ----
        // ---- Step Action -----
        // Navigate to http://host:8080/wcmqs
        // Expected Result
        // Sample site is opened;
        drone.navigateTo(wcmqsURL);
        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        homePage.render();

        // ---- Step 2 ----
        // ---- Step Action -----
        // Verify sample site;;
        // Expected Result
        // The following items are displayed:
        // * Alfresco logo
        // * Search field with Search button
        // * Contact link
        // * Toolbar with the following components: Home, News (Global Economy, Companies, Markets), Publications (Research Reports, White Papers), Blog
        // * Articles
        Assert.assertTrue(homePage.isSearchFieldWithButtonDisplay(), "Search field with Search button is not displayed");
        Assert.assertTrue(homePage.isAlfrescoLogoDisplay(), "Alfresco logo is not displayed"); 
        Assert.assertTrue(homePage.isContactLinkDisplay(), "Contact link is not displayed");
        Assert.assertTrue(homePage.isPageMenuDisplay(), "Page Menu is not displayed");
    }
    
    /** AONE-5597:Verifying imported items */
    @Test(groups = { "WQSShare", "EnterpriseOnly" })
    public void AONE_5597() throws Exception
    {
        // ---- Step 1 ----
        // ---- Step Action
        // Navigate to any folder, where content is situated(e.g. Alfresco Quick Start > Quick Start Editorial);
        // ---- Expected results ----
        // Folder is opened successfully;
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(ALFRESCO_QUICK_START).render();
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(QUICK_START_EDITORIAL).render();
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(ROOT).render();
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(NEWS).render();

        // ---- Step 2 ----
        // ---- Step Action
        // Click "Preview web asset" for any file;
        // ---- Expected Results ----
        // File opened correctly in new/tab window;
        int initial = drone.getWindowHandles().size();

        FileDirectoryInfo selection = documentLibraryPage.getFileDirectoryInfo(INDEX_HTML);
        selection.selectPreviewWebAsset();

        drone.waitForWindowsCount(initial + 1, SECONDS.convert(drone.getDefaultWaitTime(), MILLISECONDS));
        String newHandle = (String) drone.getWindowHandles().toArray()[initial];
        drone.switchToWindow(newHandle);

        assertTrue(drone.getCurrentUrl().endsWith(INDEX_HTML));
        ShareUser.logout(drone);
    }

    /** AONE-5598:Verify correct work of "Preview web asset" function */
    @Test(groups = { "WQSShare", "EnterpriseOnly" })
    public void AONE_5598() throws Exception
    {

        // ---- Step 1 ----
        // ---- Step Action
        // Open Document library component;
        // ---- Expected results ----
        // Document library is opened;
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // ---- Step 2 ----
        // ---- Step Action
        // Verify imported items;
        // ---- Expected Results ----
        // Folder "Alfresco Quick Start" is displayed, it contains:
        // * Quick Start Editorial folder (contains folder root with inside site folders and files)
        // * Quick Start live folder (empty)
        Assert.assertTrue(documentLibraryPage.isFileVisible(ALFRESCO_QUICK_START), ALFRESCO_QUICK_START +" folder is not displayed.");
        
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(ALFRESCO_QUICK_START).render();
        Assert.assertTrue(documentLibraryPage.isFileVisible(QUICK_START_EDITORIAL), QUICK_START_EDITORIAL +" folder is not displayed.");
        Assert.assertTrue(documentLibraryPage.isFileVisible(QUICK_START_LIVE), QUICK_START_LIVE +" folder is not displayed.");
        
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(QUICK_START_EDITORIAL).render();
        Assert.assertTrue(documentLibraryPage.isFileVisible(ROOT), "Folder "+ROOT+" is not displayed.");
        
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(ROOT).render();
        Assert.assertTrue(documentLibraryPage.isFileVisible("blog"), "Folder blog is not displayed.");
        Assert.assertTrue(documentLibraryPage.isFileVisible("collections"), "Folder collections is not displayed.");
        Assert.assertTrue(documentLibraryPage.isFileVisible("contact"), "Folder contact is not displayed.");
        Assert.assertTrue(documentLibraryPage.isFileVisible("news"), "Folder news is not displayed.");
        Assert.assertTrue(documentLibraryPage.isFileVisible("publications"), "Folder publications is not displayed.");
        Assert.assertTrue(documentLibraryPage.isFileVisible("index.html"), "File index.html is not displayed.");
        Assert.assertTrue(documentLibraryPage.isFileVisible("logo.jpg"), "File logo.jpg is not displayed.");
        
        documentLibraryPage=documentLibraryPage.getNavigation().clickCrumbsParentLinkName().render();
        Assert.assertTrue(documentLibraryPage.isFileVisible(ALFRESCO_QUICK_START), ALFRESCO_QUICK_START +" folder is not displayed.");
        
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(ALFRESCO_QUICK_START).render();  
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(QUICK_START_LIVE).render();
        Assert.assertTrue(documentLibraryPage.isFileUploadInstructionDisplayed(), "File upload instruction is displayed thus there are no files or folder on the page");
        
        ShareUser.logout(drone);

    }

    @Test(dependsOnMethods = "dataPrep", groups = { "DataPrepWQS", "EnterpriseOnly" })
    public void dataPrep_5600() throws Exception
    {
        String expectedSiteConfiguration = "isEditorial=true";

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // ---- Step 1 ----
        // ---- Step Action ----
        // Navigate to Quick Start Editorial folder;
        // ---- Expected Results ----
        // Folder is opened successfully;
        DocumentLibraryPage documentLibraryPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(ALFRESCO_QUICK_START);

        // ---- Step 2 ----
        // ---- Step Action ----
        // Click "Edit Metadata" link;
        // ---- Expected Results ----
        // Edit Metadata window is opened successfully;

        FileDirectoryInfo quickStartFileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(QUICK_START_EDITORIAL);
        EditDocumentPropertiesPage editDocumentPropertiesPage = quickStartFileDirectoryInfo.selectEditProperties().render();
        // ---- Step 3 ----
        // ---- Step Action ----
        // Verify Site Configuration field data;
        // ---- Expected Results ----
        // Site Configuration field contains "isEditorial=true" data;

        String siteConfiguration = editDocumentPropertiesPage.getSiteConfiguration();
        Assert.assertTrue(siteConfiguration.contains(expectedSiteConfiguration));
    }

    /** AONE-5600:Editing Site's Data */
    @Test(groups = { "WQSShare", "EnterpriseOnly" })
    public void AONE_5600() throws Exception
    {
        String testName = getTestName();

        String newsArticleTitle;
        String newsArticleName;
        // ---- Step 4 ----
        // ---- Step Action ----
        // Navigate WCMQS site, edit any item and save changes;
        // ---- Expected Results ----
        // Site is opened successfully, item's changes saved correctly;

        drone.navigateTo(wcmqsURL);
        WcmqsHomePage wqsPage = new WcmqsHomePage(drone).render();

        WcmqsNewsArticleDetails wcmqsNewsArticle = wqsPage.selectFirstArticleFromLeftPanel().render();
        WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone).render();
        wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);

        WcmqsEditPage wcmqsEditPage = wcmqsNewsArticle.clickEditButton();
        WcmqsArticleDetails wcmqsArticleDetails = wcmqsEditPage.getArticleDetails();
        newsArticleName = wcmqsArticleDetails.getName();

        wcmqsEditPage.editTitle(testName);
        wcmqsEditPage.clickSubmitButton();

        WcmqsNewsPage wcmqsNewsPage = new WcmqsNewsPage(drone);
        newsArticleTitle = wcmqsNewsPage.getNewsTitle(newsArticleName);
        Assert.assertTrue(newsArticleTitle.contains(testName));
    }
    
    /** AONE-5601:Adding hyperlinks to content via inline-edit */
    @Test(groups = { "WQSShare", "EnterpriseOnly" })
    public void AONE_5601() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);

        //create site and import finance data in wqs dashlet
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render(); 
        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName);

        CustomiseSiteDashboardPage customiseSiteDashboardPage = siteDashboardPage.getSiteNav().selectCustomizeDashboard().render();
        siteDashboardPage = customiseSiteDashboardPage.addDashlet(Dashlets.WEB_QUICK_START, 1).render();

        SiteWebQuickStartDashlet wqsDashlet = siteDashboardPage.getDashlet(SITE_WEB_QUICK_START_DASHLET).render();
        wqsDashlet.selectWebsiteDataOption(WebQuickStartOptions.FINANCE);
        wqsDashlet.clickImportButtton();
        wqsDashlet.waitForImportMessage();
        
        // --- Step 1 ---
        // --- Step action ---
        // In Share, go to root/news folder;
        // --- Expected results ---
        // Folder is opened;
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(ALFRESCO_QUICK_START).render();        
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(QUICK_START_EDITORIAL).render();     
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(ROOT).render();
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(NEWS).render();

        // --- Step 2 ---
        // --- Step action ---
        // On index.html, click More, then Inline Edit;
        // --- Expected results ---
        // New page/window is opened;
        FileDirectoryInfo quickStartFileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(INDEX_HTML);
        InlineEditPage inlineEditPage = quickStartFileDirectoryInfo.selectInlineEdit().render();


        // --- Step 3 ---
        // --- Step action ---
        // Enter random text; 
        // --- Expected results ---
        // Text is entered;
        String randomText="random text";
        inlineEditPage.insertTextInContent(randomText);
        AdvancedTinyMceEditor tinyMceEditor = inlineEditPage.getContentTinyMCEEditor();
        Assert.assertTrue(tinyMceEditor.getText().contains(randomText));

        // --- Step 4 ---
        // --- Step action ---
        // Select some text, then click the link button, in the popup dialog, enter a valid url and click Insert. Then clieck Save button.
        // --- Expected results ---
        // The text is now link;
        tinyMceEditor.selectTextFromEditor();
        InsertOrEditLinkPage insertOrEditLinkPage = tinyMceEditor.clickInsertOrEditLink();
        
        insertOrEditLinkPage.setLinkUrl("http://www.alfresco.com/");
        insertOrEditLinkPage.setTitle("Aldresco site");
        insertOrEditLinkPage.clickOKButton();
        Assert.assertEquals(randomText, tinyMceEditor.getText());
        Assert.assertTrue(tinyMceEditor.getContent().contains("<a href=\"http://www.alfresco.com/\" data-mce-href=\"http://www.alfresco.com/\">" + randomText + "</a>"), "tinyMceEditor.getContent() is: " + tinyMceEditor.getContent());
              
        EditHtmlDocumentPage editDocPage = (EditHtmlDocumentPage) inlineEditPage.getInlineEditDocumentPage(MimeType.HTML);
        editDocPage.saveText();
        
        // --- Step 5 ---
        // --- Step action ---
        // Come back to news folder, click More, then Inline Edit on index.html;t
        // --- Expected results ---
        // Link for text is displayed;
        documentLibraryPage = drone.getCurrentPage().render();
        FileDirectoryInfo quickStartFileDirectoryInfo2 = documentLibraryPage.getFileDirectoryInfo(INDEX_HTML);
        InlineEditPage inlineEditPage2 = quickStartFileDirectoryInfo2.selectInlineEdit().render();
        
        AdvancedTinyMceEditor tinyMceEditor2 = inlineEditPage2.getContentTinyMCEEditor();
        Assert.assertEquals(randomText, tinyMceEditor2.getText());
        Assert.assertTrue(tinyMceEditor2.getContent().contains("<a href=\"http://www.alfresco.com/\" data-mce-href=\"http://www.alfresco.com/\">" + randomText + "</a>"), "tinyMceEditor.getContent() is: " + tinyMceEditor2.getContent());

        ShareUser.logout(drone);
    }

    @Test(dependsOnMethods = "dataPrep", groups = { "DataPrepWQS", "EnterpriseOnly" })
    public void dataPrep_5602() throws Exception
    {
        // User login.
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // ---- Step 1 ----
        // ---- Step Action -----
        // Create an HTML article in Quick Start Editorial > root > news > global(e.g. article10.html).
        // Expected Result
        // HTML article is successfully created;
        DocumentLibraryPage docLib = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        docLib.selectFolder("Alfresco Quick Start").render();
        docLib.selectFolder("Quick Start Editorial").render();
        docLib.selectFolder("root").render();
        docLib.selectFolder("news").render();
        docLib.selectFolder("global").render();

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(newsName);
        contentDetails.setTitle(newsName);
        contentDetails.setDescription(newsName);
        contentDetails.setContent(newsName);
        ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.HTML, docLib);
    }

    /** AONE-5602:Verifying correct work of date/time function */
    @Test(groups = { "WQSShare", "EnterpriseOnly" })
    public void AONE_5602() throws Exception
    {
        // ---- Step 2 ----
        // ---- Step Action -----
        // On the Quick Start website, navigate to the Global Economy page.
        // Expected Result
        // Global Economy page is opened;
        drone.navigateTo(wcmqsURL);
        drone.refresh();

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        homePage.render();
        WcmqsNewsPage newsPage = homePage.openNewsPageFolder("global").render();

        List<ShareLink> newsTitles = newsPage.getHeadlineTitleNews();
        String strNews = newsTitles.toString();

        Assert.assertTrue(strNews.contains(newsName));

        // ---- Step 3 ----
        // ---- Step Action -----
        // Verify the presense of date/time in arcticle10.html header;
        // Expected Result
        // Date/time is present in the file header;
        Assert.assertTrue(newsPage.isDateTimeNewsPresent(newsName));
        drone.closeTab();

    }

    @Test(dependsOnMethods = "dataPrep", groups = { "DataPrepWQS", "EnterpriseOnly" })
    public void dataPrep_5603() throws Exception
    {
        String fileName1 = "Content_Platform.pdf";
        String fileName2 = "Community_Network.pdf";

        // User login.
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // create folders and files for the test
        DocumentLibraryPage docLib = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        docLib.selectFolder("Alfresco Quick Start").render();
        docLib.selectFolder("Quick Start Editorial").render();
        docLib.selectFolder("root").render();
        docLib.selectFolder("publications").render();

        // ---- Step 1, 2, 3----
        // ---- Step Action -----
        // 1. Put cursor on any pdf publication
        // 2. Click More+ menu
        // 3. Click Edit Offline action;
        // Expected Result
        // 1. The pdf is high lighted;
        // 2. More menu is expanded; Edit Offline menu item is available;
        // 3. Open/Save dialog pos up. User can download the working copy;
        // Locked document with the yellow notification This document is locked by you for offline editing. is displayed;
        docLib.getFileDirectoryInfo(fileName1).selectEditOfflineAndCloseFileWindow().render();
        Assert.assertTrue(docLib.getFileDirectoryInfo(fileName1).isEdited(), "The file is blocked for editing");

        // ---- Step 4 ----
        // ---- Step Action -----
        // Open the details page of any other publication;
        // Expected Result
        // The details page is opened; Edit Offline action is present;
        DocumentDetailsPage detailsPage = docLib.selectFile(fileName2).render();

        // ---- Step 5 ----
        // ---- Step Action -----
        // Click Edit Offline action;
        // Expected Result
        // Open/Save dialog pos up. User can download the working copy;
        // Locked document with the yellow notification This document is locked by you for offline editing. is displayed;
        detailsPage.selectEditOffLine().render();
        Assert.assertTrue(detailsPage.isEditOfflineDisplayed());
    }

    /** AONE-5603:Edit offline a web quick start publication */
    @Test(groups = { "WQSShare", "EnterpriseOnly" })
    public void AONE_5603() throws Exception
    {
        drone.navigateTo(wcmqsURL);

        // ---- Step 6 ----
        // ---- Step Action -----
        // Open wcmqs site (http://host:wqsPort/wcmqs)
        // Expected Result
        // The site is opened without errors;
        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        homePage.render();

        // ---- Step 7 ----
        // ---- Step Action -----
        // Try to open publication section;
        // Expected Result
        // The section is opened without errors;
        homePage.selectMenu("publications");
        Assert.assertTrue(drone.getTitle().contains("Publications"));

        WcmqsAllPublicationsPage publicationsPage = new WcmqsAllPublicationsPage(drone);

        List<ShareLink> publicationTitles = publicationsPage.getAllPublictionsTitles();
        int size = publicationTitles.size();
        Assert.assertTrue(size > 3);
        drone.closeTab();

    }

    @Test(dependsOnMethods = "dataPrep", groups = { "DataPrepWQS", "EnterpriseOnly" })
    public void dataPrep_5604() throws Exception
    {
        String modifiedTitle = testName + "_newTitle777";
        String newsArticle = "article4.html";

        // User login.
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // ---- Step 1 ----
        // ---- Step Action -----
        // Navigate to the WQS Sample Site > Document Library > Documents >Alfresco Quick Start > Quick Start Editorial > root > en > news >
        // global > article create above > and edit properties of the article (change the title).
        // Expected Result
        // Changes are applied.
        DocumentLibraryPage docLib = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        docLib.selectFolder("Alfresco Quick Start").render();
        docLib.selectFolder("Quick Start Editorial").render();
        docLib.selectFolder("root").render();
        docLib.selectFolder("news").render();
        docLib.selectFolder("global").render();

        DocumentDetailsPage detailsPage = docLib.selectFile(newsArticle);

        InlineEditPage inlineEditPage = detailsPage.selectInlineEdit();
        EditHtmlDocumentPage editDocPage = (EditHtmlDocumentPage) inlineEditPage.getInlineEditDocumentPage(MimeType.HTML);
        editDocPage.setTitle(modifiedTitle);
        editDocPage.saveText();

    }

    /** AONE-5604:Editing Web Site. No cache changes. */
    @Test(groups = { "WQSShare", "EnterpriseOnly" })
    public void AONE_5604() throws Exception
    {
        String modifiedTitle = testName + "_newTitle";
        String newsArticle = "article4.html";

        // On the Quick Start website, navigate to the Global Economy page.
        drone.navigateTo(wcmqsURL);

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        homePage.render();

        drone.refresh();
        drone.refresh();

        // ---- Step 2 ----
        // ---- Step Action -----
        // Navigate to the sample editorial wcmqs site at http://localhost:8080/wcmqs/en/news/global/
        // Expected Result
        // The title of the article has changed.
        WcmqsNewsPage newsPage = homePage.openNewsPageFolder("global").render();
        Assert.assertTrue(newsPage.getNewsTitle(newsArticle).equals(modifiedTitle));

        // ---- Step 3 ----
        // ---- Step Action -----
        // Refresh the browser several times.
        // Expected Result
        // The title reflects the new value.
        drone.refresh();
        drone.refresh();
        drone.refresh();
        String newTitle = newsPage.getNewsTitle(newsArticle);
        Assert.assertTrue(newTitle.equals(modifiedTitle));
        drone.closeTab();
    }

    @Test(dependsOnMethods = "dataPrep", groups = { "DataPrepWQS", "EnterpriseOnly" })
    public void dataPrep_5605() throws Exception
    {
        String folder1 = "Folder 1";
        String folder2 = "Folder 2";
        String folder3 = "Folder 3";
        String folder11 = "Folder11";
        String folder111 = "Folder111";
        String folder112 = "Folder112";

        // User login.
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // ---- Step 1 ----
        // ---- Step Action -----
        // Navigate to Documents> Alfresco Quick Start> Quick Start Editorial> root> blog and create new folders 'Submenu1-Item1','Submenu1-Item2'
        // and'Submenu1-Item3'.
        // Expected Result
        // Folders are created.
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        String blogFolder = "Alfresco Quick Start" + File.separator + "Quick Start Editorial" + File.separator + "root" + File.separator + "blog";
        ShareUser.createFolderInFolder(drone, folder1, folder1, blogFolder).render();
        ShareUser.createFolderInFolder(drone, folder2, folder1, blogFolder).render();
        ShareUser.createFolderInFolder(drone, folder3, folder1, blogFolder).render();

        // ---- Step 2 ----
        // ---- Step Action -----
        // Navigate to Documents> Alfresco Quick Start> Quick Start Editorial> root> blog> Submenu1-Item1 and create new folder 'Submenu2'.
        // Expected Result
        // Folders are created.
        blogFolder = blogFolder + File.separator + folder1;
        ShareUser.createFolderInFolder(drone, folder11, folder11, blogFolder).render();

        // ---- Step 3 ----
        // ---- Step Action -----
        // Navigate to Documents> Alfresco Quick Start> Quick Start Editorial> root> blog> Submenu1-Item1> Submenu2 and create folder 'Submenu3-Item1' and
        // 'Submenu3-Item2'.
        // Expected Result
        // Folders are created.
        blogFolder = blogFolder + File.separator + folder11;
        ShareUser.createFolderInFolder(drone, folder111, folder111, blogFolder).render();
        ShareUser.createFolderInFolder(drone, folder112, folder112, blogFolder).render();

    }

    /** AONE-5605:All WQS submenu items are displayed with normal zoom. */
    @Test(groups = { "WQSShare", "EnterpriseOnly" })
    public void AONE_5605() throws Exception
    {
        drone.navigateTo(wcmqsURL);

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        homePage.render();

        // ---- Step 4 ----
        // ---- Step Action -----
        // In the Quick Start website, navigate to the Home page and open the Blog menu down to Submenu2.
        // Expected Result
        // Folders are created.
        drone.refresh();
        homePage.selectMenu("home");
        drone.refresh();

        List<ShareLink> allFolders = homePage.getAllFoldersFromMenu("blog");
        // String lastFolder = allFolders.get(2).getDescription();
        // Assert.assertTrue(lastFolder.contains(folder3));
        // String urlLast = allFolders.get(6).getHref();
        // Assert.assertTrue(urlLast.contains(folder112));
        Assert.assertTrue(allFolders.size() > 5);
        drone.closeTab();
    }

    /** AONE-5606:Verify uploading a file when transformation fails in a folder inside a WQS site */
    @Test(groups = { "WQSShare", "EnterpriseOnly" })
    public void AONE_5606() throws Exception
    {
        String folder1 = "Folder5606";
        String fileName = "AONE-5606.docx";
        String rendConfig = "application/vnd.openxmlformats-officedocument.wordprocessingml.document=ws:swfPreview";

        File file = new File(DATA_FOLDER + SLASH + fileName);

        // User login.
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // ---- Step 1 ----
        // ---- Step Action -----
        // Already created in data prep
        DocumentLibraryPage docLib = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        String blogFolder = "Alfresco Quick Start" + File.separator + "Quick Start Editorial" + File.separator + "root";
        ShareUser.createFolderInFolder(drone, folder1, folder1, blogFolder).render();

        EditDocumentPropertiesPage editPage = ShareUserSitePage.getFileDirectoryInfo(drone, folder1).selectEditProperties();
        editPage.setRenditionConfig(rendConfig);
        editPage.selectSave().render();

        docLib.selectFolder(folder1).render();

        ShareUserSitePage.uploadFile(drone, file).render();

        Assert.assertTrue(docLib.isFileVisible(fileName));

    }
}
