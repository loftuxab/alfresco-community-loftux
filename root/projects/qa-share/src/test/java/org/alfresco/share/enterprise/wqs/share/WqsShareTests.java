package org.alfresco.share.enterprise.wqs.share;

import java.io.File;
import java.util.List;

import org.alfresco.po.alfresco.WcmqsHomePage;
import org.alfresco.po.alfresco.WcmqsNewsPage;
import org.alfresco.po.alfresco.WcmqsAllPublicationsPage;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.dashlet.SiteWebQuickStartDashlet;
import org.alfresco.po.share.dashlet.WebQuickStartOptions;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.site.document.EditHtmlDocumentPage;
import org.alfresco.po.share.site.document.InlineEditPage;
import org.alfresco.po.share.site.document.MimeType;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class WqsShareTests extends AbstractUtils
{
    private static final Logger logger = Logger.getLogger(WqsShareTests.class);
    String newsName;;
    String wcmqsURL = "http://localhost:8080/wcmqs";
    String siteName;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        newsName = "cont2" + getFileName(testName) + ".html";
        siteName = getSiteName(testName) + "5";
        logger.info("Start tests:" + testName);
    }

    @Test(groups = "DataPrepWQS")
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
    }

    @Test(groups = "EnterpriseOnly")
    public void AONE_5602() throws Exception
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

        // On the Quick Start website, navigate to the Global Economy page.
        drone.createNewTab();
        drone.navigateTo(wcmqsURL);
        drone.deleteCookies();
        drone.refresh();

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        homePage.render();

        // ---- Step 2 ----
        // ---- Step Action -----
        // On the Quick Start website, navigate to the Global Economy page.
        // Expected Result
        // Global Economy page is opened;
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

    @Test(groups = "EnterpriseOnly")
    public void AONE_5603() throws Exception
    {
        // String testName = getTestName() + "6";
        // String siteName = getSiteName(testName);
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

        drone.createNewTab();
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

    @Test(groups = "EnterpriseOnly")
    public void AONE_5604() throws Exception
    {
        // String siteName = getSiteName(testName);
        String modifiedTitle = testName + "_newTitle777";

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

        DocumentDetailsPage detailsPage = docLib.selectFile(newsName).render();

        InlineEditPage inlineEditPage = detailsPage.selectInlineEdit();
        EditHtmlDocumentPage editDocPage = (EditHtmlDocumentPage) inlineEditPage.getInlineEditDocumentPage(MimeType.HTML);
        editDocPage.setTitle(modifiedTitle);
        editDocPage.saveText();

        // On the Quick Start website, navigate to the Global Economy page.
        drone.createNewTab();
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
        Assert.assertTrue(newsPage.getNewsTitle(newsName).equals(modifiedTitle));

        // ---- Step 3 ----
        // ---- Step Action -----
        // Refresh the browser several times.
        // Expected Result
        // The title reflects the new value.
        drone.refresh();
        drone.refresh();
        drone.refresh();
        String newTitle = newsPage.getNewsTitle(newsName);
        Assert.assertTrue(newTitle.equals(modifiedTitle));
        drone.closeTab();
    }

    @Test(groups = "EnterpriseOnly")
    public void AONE_5605() throws Exception
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

        drone.createNewTab();
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
        String lastFolder = allFolders.get(2).getDescription();
        Assert.assertTrue(lastFolder.contains(folder3));
        String urlLast = allFolders.get(6).getHref();
        Assert.assertTrue(urlLast.contains(folder112));
        drone.closeTab();
    }

    @Test(groups = "EnterpriseOnly")
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
        editPage.selectSave();

        docLib.selectFolder(folder1).render();

        ShareUserSitePage.uploadFile(drone, file).render();

        Assert.assertTrue(docLib.isFileVisible(fileName));

    }
}
