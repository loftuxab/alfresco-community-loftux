package org.alfresco.share.enterprise.wqs.web;

/**
 * Created by P3700473 on 12/2/2014.
 */

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

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Lucian Tuca on 12/02/2014.
 */
public class MainPage extends AbstractUtils
{
        private static final Logger logger = Logger.getLogger(MainPage.class);
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
        * AONE-5656 Main page
        */
        @Test(groups = { "WQS" })
        public void AONE_5656() throws Exception
        {

                // ---- Step 1 ----
                // ---- Step action ----
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened;

                navigateTo(wqsURL);

                // ---- Step 2 ----
                // ---- Step action ----
                // Verify Main page;
                // ---- Expected results ----
                // The following items are displayed:

                // Alfresco Web Quick Start logo,
                WcmqsHomePage mainPage = new WcmqsHomePage(drone);
                Assert.assertTrue(mainPage.isAlfrescoLogoDisplay());

                // alfresco.com link in the bottom
                Assert.assertTrue(mainPage.isBottomUrlDisplayed());

                // Navigation links(Home, News, Publications, Blog, Alfresco.com),

                // Search field with Search button;
                Assert.assertTrue(mainPage.isSearchFieldWithButtonDisplay());

                // Contact link
                Assert.assertTrue(mainPage.isContactLinkDisplay());

                // # Slide... banner with Read more button;
                Assert.assertTrue(mainPage.isSlideReadMoreButtonDisplayed());

                // News and Analysis (We bring value, innovation and growth to  your business) section that contains articles (Media  Consult new site coming out in September,China  eyes shake-up of bank holdings, Minicards  are now available) with picture preview, 1 paragraph and Created date;
                Assert.assertTrue(mainPage.isNewsAndAnalysisSectionDisplayed());

                // Featured section with articles names links (Ethical funds, Minicards  are now available, Alfresco  Datasheet - Social Computing)
                Assert.assertTrue(mainPage.isFeaturedSectionDisplayed());

                // Example Feature (Investments and advertising campaigns) section with Read more button;
                Assert.assertTrue(mainPage.isExampleFeatureSectionDisplayed());

                // Latest Blog Articles section with blog posts preview (Ethical  funds,Company  organises workshop,Our top  analyst's latest...)
                Assert.assertTrue(mainPage.isLatestBlogArticlesDisplayed());

                System.out.println("a");
        }

        /*
        * AONE-5657 Verify correct navigation from main page
        */
        @Test(groups = { "WQS" })
        public void AONE_5657() throws Exception
        {

                navigateTo(wqsURL);
                WcmqsHomePage mainPage = new WcmqsHomePage(drone).render();

                // ---- Step 1 ----
                // ---- Step action ----
                // Click News link;
                // ---- Expected results ----
                // User goes to the News page;

                mainPage.selectMenu(WcmqsNewsPage.NEWS_MENU_STR);
                WcmqsNewsPage newsPage = new WcmqsNewsPage(drone).render();
                Assert.assertTrue(newsPage.getTitle().contains("News"));

                // ---- Step 2 ----
                // ---- Step action ----
                // Click Home link;
                // ---- Expected results ----
                // User goes to main page;

                newsPage.selectMenu(WcmqsHomePage.HOME_MENU_STR);
                mainPage = new WcmqsHomePage(drone).render();
                Assert.assertTrue(mainPage.getTitle().contains("Home"));

                // ---- Step 3 ----
                // ---- Step action ----
                // Click Publications link;
                // ---- Expected results ----
                // User goes to the Publications page;

                mainPage.selectMenu(WcmqsAllPublicationsPage.PUBLICATIONS_MENU_STR);
                WcmqsAllPublicationsPage allPublicationsPage = new WcmqsAllPublicationsPage(drone).render();
                Assert.assertTrue(allPublicationsPage.getTitle().contains("Publications"));

                // ---- Step 4 ----
                // ---- Step action ----
                // Click on Alfresco Web Quick Start logo;
                // ---- Expected results ----
                // User goes to main page;

                mainPage = allPublicationsPage.clickWebQuickStartLogo().render();
                Assert.assertTrue(mainPage.getTitle().contains("Home"));

                // ---- Step 5 ----
                // ---- Step action ----
                // Click Blog link;
                // ---- Expected results ----
                // User goes to the Blog page;

                mainPage.selectMenu(WcmqsAbstractPage.BLOG_MENU_STR);
                WcmqsBlogPage blogPage = new WcmqsBlogPage(drone).render();
                Assert.assertTrue(blogPage.getTitle().contains("Blog"));

                // ---- Step 6 ----
                // ---- Step action ----
                // Click Contact link;
                // ---- Expected results ----
                // Contact page is opened;

                blogPage.clickContactLink();
                String pageTitle = drone.getTitle();
                Assert.assertTrue(pageTitle.contains("Contact"));

                // ---- Step 7 ----
                // ---- Step action ----
                // Click Alfresco.com link;
                // ---- Expected results ----
                // User goes to Alfresco main page site(http://www.alfresco.com);

                blogPage.clickAlfrescoLink();
                pageTitle = drone.getTitle();
                Assert.assertTrue(pageTitle.contains("Alfresco"));
        }

        /*
        * AONE-5660 Adding new section in wcmqs site
        */
        @Test(groups = { "WQS" })
        public void AONE_5660() throws Exception
        {

                // ---- Step 1 ----
                // ---- Step action ----
                // Navigate to WCMQS site- Alfresco Quick Start - Quick Start Editorial - root folder;
                // ---- Expected results ----
                // root folder is opened;

                ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
                documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(ALFRESCO_QUICK_START).render();
                documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(QUICK_START_EDITORIAL).render();
                documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(ROOT).render();
                Assert.assertNotNull(documentLibraryPage);

                // ---- Step 2 ----
                // ---- Step action ----
                // Create new folder with name accounting and title Accounting Data;
                // ---- Expected results ----
                // New folder is created under root;

                String root_folder_path = ALFRESCO_QUICK_START + File.separator + QUICK_START_EDITORIAL + File.separator + ROOT;
                ShareUser.createFolderInFolder(drone, ACCOUNTING, ACCOUNTING_DATA, root_folder_path).render();
                documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(ACCOUNTING).render();
                Assert.assertNotNull(documentLibraryPage);

                // ---- Step 3 ----
                // ---- Step action ----
                // Go to http://host:port/wcmqs;
                // ---- Expected results ----
                // Wcmqs is opened;

                navigateTo(wqsURL);
                WcmqsHomePage homePage = new WcmqsHomePage(drone);
                Assert.assertNotNull(homePage);

                // ---- Step 4 ----
                // ---- Step action ----
                // Click on Accounting Data section;
                // ---- Expected results ----
                // No erro is displayed. Corect page with Coming soon... notifications is displayed;

                waitAndOpenNewSection(homePage, ACCOUNTING, 4);
                String pageTitle = drone.getTitle();

                // TODO 4: Add your code here for step 4.

        }

        public void navigateTo(String url)
        {
                drone.navigateTo(url);
        }

        private void waitAndOpenNewSection(WcmqsHomePage homePage, String menuOption, int minutesToWait)
        {
                int waitInMilliSeconds = 3000;
                int maxTimeWaitInMilliSeconds = 60000 * minutesToWait;
                boolean sectionFound = false;

                while (!sectionFound && maxTimeWaitInMilliSeconds > 0)
                {
                        try
                        {
                                homePage.selectMenu(menuOption);
                                sectionFound = true;
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