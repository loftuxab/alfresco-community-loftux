package org.alfresco.share.enterprise.wqs.web.publications;

import org.alfresco.po.share.dashlet.SiteWebQuickStartDashlet;
import org.alfresco.po.share.dashlet.WebQuickStartOptions;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.wqs.WcmqsAllPublicationsPage;
import org.alfresco.po.share.wqs.WcmqsHomePage;
import org.alfresco.po.share.wqs.WcmqsPublicationPage;
import org.alfresco.po.share.wqs.WcmqsSearchPage;
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
 * Created by svidrascu on 11/19/2014.
 */
public class PublicationActions extends AbstractUtils
{
        private static final Logger logger = Logger.getLogger(PublicationActions.class);
        private final String ALFRESCO_QUICK_START = "Alfresco Quick Start";
        private final String QUICK_START_EDITORIAL = "Quick Start Editorial";
        private final String ROOT_FOLDER = "root";
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
         * AONE-5661:Publications
         */
        @Test(groups = "WQS")
        public void AONE_5661() throws Exception
        {
                // ---- Step 1 ----
                // ---- Step action ---
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened

                drone.navigateTo(wqsURL);

                //verify that the publications dropdown list exists and has research reports and white papers within it
                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                wcmqsHomePage.render();
                wcmqsHomePage.mouseOverMenu("publications");
                Assert.assertTrue(wcmqsHomePage.isResearchReportsDisplayed());
                Assert.assertTrue(wcmqsHomePage.isWhitePapersDisplayed());

                //click on research reports and check if the correct page opened
                wcmqsHomePage.openPublicationsPageFolder("research reports");
                Assert.assertTrue(wcmqsHomePage.getTitle().contains("Research Reports"));

                //click on white papers and check if the correct page opened
                wcmqsHomePage.render();
                wcmqsHomePage.openPublicationsPageFolder("white papers");
                Assert.assertTrue(wcmqsHomePage.getTitle().contains("White Papers"));
        }

        /**
         * AONE-5662:Publications page
         */
        @Test(groups = "WQS")
        public void AONE_5662() throws Exception
        {
                // ---- Step 1 ----
                // ---- Step action ---
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened
                drone.navigateTo(wqsURL);

                //open publications page and check if you reached the correct page, if key publications section is displayed, and that publications are displayed with link and description
                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                wcmqsHomePage.render();
                wcmqsHomePage.selectMenu("publications");
                WcmqsAllPublicationsPage wcmqsAllPublicationsPage = new WcmqsAllPublicationsPage(drone);

                Assert.assertTrue(wcmqsAllPublicationsPage.getTitle().contains("Publications"));
                Assert.assertTrue(wcmqsAllPublicationsPage.getKeyPublicationsSection().isDisplayed());
                Assert.assertTrue(wcmqsAllPublicationsPage.getAllPublictionsTitles().size() > 3);
        }

        /**
         * AONE-5663:Opening Documents from publications page
         */
        @Test(groups = "WQS")
        public void AONE_5663() throws Exception
        {
                // ---- Step 1 ----
                // ---- Step action ---
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened
                drone.navigateTo(wqsURL);

                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                wcmqsHomePage.render();
                wcmqsHomePage.selectMenu("publications");
                WcmqsAllPublicationsPage wcmqsAllPublicationsPage = new WcmqsAllPublicationsPage(drone);

                //open publications page using the publication title and check if you reached the correct page
                for (int i = 0; i < 7; i++)
                {
                        wcmqsAllPublicationsPage.getAllPublictionsTitles().get(i).openLink();
                        WcmqsHomePage wcmqsHomePage1 = new WcmqsHomePage(drone);
                        wcmqsHomePage1.render();
                        Boolean check = false;
                        for (String PageTitle : WcmqsPublicationPage.PUBLICATION_PAGES)
                        {
                                if (wcmqsHomePage1.getTitle().contains(PageTitle))
                                {
                                        check = true;
                                        break;
                                }
                        }
                        Assert.assertTrue(check, "Publication page did not open correctly");
                        wcmqsHomePage1.selectMenu("publications");
                        wcmqsAllPublicationsPage = new WcmqsAllPublicationsPage(drone);
                }

                //open publications page using the publication image and check if you reached the correct page
                for (int i = 0; i < 7; i++)
                {
                        wcmqsAllPublicationsPage.getAllPublictionsImages().get(i).openLink();
                        WcmqsHomePage wcmqsHomePage1 = new WcmqsHomePage(drone);
                        wcmqsHomePage1.render();
                        Boolean check = false;
                        for (String PageTitle : WcmqsPublicationPage.PUBLICATION_PAGES)
                        {
                                if (wcmqsHomePage1.getTitle().contains(PageTitle))
                                {
                                        check = true;
                                        break;
                                }
                        }
                        Assert.assertTrue(check, "Publication page did not open correctly");
                        wcmqsHomePage1.selectMenu("publications");
                        wcmqsAllPublicationsPage = new WcmqsAllPublicationsPage(drone);
                }
        }

        /**
         * AONE-5664:Verifying publications page
         */
        @Test(groups = "WQS")
        public void AONE_5664() throws Exception
        {
                // ---- Step 1 ----
                // ---- Step action ---
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened
                drone.navigateTo(wqsURL);

                //Click publications link
                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                wcmqsHomePage.render();
                wcmqsHomePage.selectMenu("publications");
                WcmqsAllPublicationsPage wcmqsAllPublicationsPage = new WcmqsAllPublicationsPage(drone);

                //Click Alfresco WCM link
                wcmqsAllPublicationsPage.getAllPublictionsTitles().get(0).openLink();

                WcmqsPublicationPage wcmqsPublicationPage = new WcmqsPublicationPage(drone);
                wcmqsPublicationPage.render();

                //Verify Publication page contains: Publication name, Publication date, Publication preview, Tags section, Publication details section
                Assert.assertTrue(wcmqsPublicationPage.isPublicationNameDisplay());
                Assert.assertTrue(wcmqsPublicationPage.isPublicationDateDisplay());
                Assert.assertTrue(wcmqsPublicationPage.isPublicationPreviewDisplay());
                Assert.assertTrue(wcmqsPublicationPage.isPublicationTagsDisplay());
                Assert.assertTrue(wcmqsPublicationPage.isPublicationDetailsDisplay());

        }

        /**
         * AONE-5665:Verifying publications details
         */
        @Test(groups = "WQS")
        public void AONE_5665() throws Exception
        {
                // ---- Step 1 ----
                // ---- Step action ---
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened
                drone.navigateTo(wqsURL);

                //Click publications link
                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                wcmqsHomePage.render();
                wcmqsHomePage.selectMenu("publications");
                WcmqsAllPublicationsPage wcmqsAllPublicationsPage = new WcmqsAllPublicationsPage(drone);

                //Click Alfresco WCM link
                wcmqsAllPublicationsPage.getAllPublictionsTitles().get(0).openLink();

                WcmqsPublicationPage wcmqsPublicationPage = new WcmqsPublicationPage(drone);
                wcmqsPublicationPage.render();

                //Verify Publication page details contains: Publication description, fields(Author, Published, Size, Mime Type, Download)
                Assert.assertTrue(wcmqsPublicationPage.isPublicationDescriptionDisplay());
                Assert.assertTrue(wcmqsPublicationPage.isPublicationAuthorDisplay());
                Assert.assertTrue(wcmqsPublicationPage.isPublicationPublishDateDisplay());
                Assert.assertTrue(wcmqsPublicationPage.isPublicationSizeDisplay());
                Assert.assertTrue(wcmqsPublicationPage.isPublicationMimeDisplay());
                Assert.assertTrue(wcmqsPublicationPage.isPublicationDownloadDisplay());

                //Verify Document Can Be Downloaded correctly
                File testFile = wcmqsPublicationPage.downloadFiles();
                Assert.assertTrue(testFile.length() > 0);

        }

        @Test(groups = "DataPrepWQS")
        public void dataPrep_AONE_5666() throws Exception
        {
                // ---- Data prep ----
                ShareUser.openSiteDashboard(drone, siteName);
                DocumentLibraryPage documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
                documentLibPage.selectFolder("Alfresco Quick Start");
                documentLibPage.selectFolder("Quick Start Editorial");
                documentLibPage.selectFolder("root");
                documentLibPage.selectFolder("publications");
                documentLibPage.getFileDirectoryInfo("WCM.pdf").addTag("tag2");
                documentLibPage.selectFolder("white-papers");
                documentLibPage.getFileDirectoryInfo("Datasheet_OEM.pdf").addTag("tag1");
                documentLibPage.getNavigation().clickFolderUp();
                documentLibPage = new DocumentLibraryPage(drone);
                documentLibPage.selectFolder("research-reports");
                documentLibPage.getFileDirectoryInfo("Enterprise_Network_0410.pdf").addTag("tag2");
        }

        /**
         * AONE-5666:Tags
         */
        @Test(groups = "WQS")
        public void AONE_5666() throws Exception
        {

                // ---- Step 1 ----
                // ---- Step action ----
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                //  Sample site is opened;

                drone.navigateTo(wqsURL);

                // ---- Step 2 ----
                // ---- Step action ----
                // Open White papers component in Publications;
                // ---- Expected results ----
                //  White papers component is opened;

                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                wcmqsHomePage.render();
                wcmqsHomePage.mouseOverMenu("publications");
                wcmqsHomePage.openPublicationsPageFolder("white papers");

                // ---- Step 3 ----
                // ---- Step action ----
                // Click tag1 for Microsoft  Word - OEM 0510 v2 publication;
                // ---- Expected results ----
                //  Search page is opened, publication Microsoft  Word - OEM 0510 v2 is displayed;

                WcmqsPublicationPage wcmqsPublicationPage = new WcmqsPublicationPage(drone);
                wcmqsPublicationPage.clickDocumentTag("tag1");
                WcmqsSearchPage wcmqsSearchPage = new WcmqsSearchPage(drone);
                wcmqsSearchPage.render();
                Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains("Microsoft Word - OEM 0510 v2"),
                        "Tag search did not return Microsoft  Word - OEM 0510 v2");

                // ---- Step 4 ----
                // ---- Step action ----
                // Return to White papers page;
                // ---- Expected results ----
                //  White papers component is opened;

                wcmqsHomePage.mouseOverMenu("publications");
                wcmqsHomePage.openPublicationsPageFolder("white papers");

                // ---- Step 5 ----
                // ---- Step action ----
                // Open Microsoft  Word - OEM 0510 v2 publication;
                // ---- Expected results ----
                //  Microsoft  Word - OEM 0510 v2 details page is opened;

                WcmqsAllPublicationsPage wcmqsAllPublicationsPage = new WcmqsAllPublicationsPage(drone);
                wcmqsAllPublicationsPage.clickDocumentByTitle("Microsoft Word - OEM 0510 v2");

                // ---- Step 6 ----
                // ---- Step action ----
                // Click tag1 link;
                // ---- Expected results ----
                //  Search page is opened, publication Microsoft  Word - OEM 0510 v2 is displayed;

                wcmqsPublicationPage = new WcmqsPublicationPage(drone);
                wcmqsPublicationPage.clickDocumentTag("tag1");
                wcmqsSearchPage = new WcmqsSearchPage(drone);
                wcmqsSearchPage.render();
                Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains("0510"), "Tag search did not return Microsoft  Word - OEM 0510 v2");

                // ---- Step 7 ----
                // ---- Step action ----
                // Open Research reports component in Publications;
                // ---- Expected results ----
                //  Research reports component is opened;

                wcmqsHomePage.mouseOverMenu("publications");
                wcmqsHomePage.openPublicationsPageFolder("research reports");

                // ---- Step 8 ----
                // ---- Step action ----
                // Click tag2 for Enterprise  Network publication;
                // ---- Expected results ----
                //  Search page is opened, publication Enterprise  Network and Alfresco WCM publications are displayed;

                wcmqsPublicationPage = new WcmqsPublicationPage(drone);
                wcmqsPublicationPage.clickDocumentTag("tag2");
                wcmqsSearchPage = new WcmqsSearchPage(drone);
                wcmqsSearchPage.render();
                Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains("Enterprise Network"),
                        "Tag search did not return Enterprise Network");
                Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains("Alfresco WCM"), "Tag search did not return Alfresco WCM");

                // ---- Step 9 ----
                // ---- Step action ----
                // Return to Research reports page;
                // ---- Expected results ----
                //  Research reports component is opened;

                wcmqsHomePage.mouseOverMenu("publications");
                wcmqsHomePage.openPublicationsPageFolder("research reports");

                // ---- Step 10 ----
                // ---- Step action ----
                // Open Enterprise  Network publication;
                // ---- Expected results ----
                //  Enterprise  Network details page is opened;

                wcmqsAllPublicationsPage = new WcmqsAllPublicationsPage(drone);
                wcmqsAllPublicationsPage.clickDocumentByTitle("Enterprise Network");

                // ---- Step 11 ----
                // ---- Step action ----
                // Click tag2 link;
                // ---- Expected results ----
                //  Search page is opened, publication Enterprise  Network and Alfresco WCM  publications are displayed;

                wcmqsPublicationPage = new WcmqsPublicationPage(drone);
                wcmqsPublicationPage.clickDocumentTag("tag2");
                wcmqsSearchPage = new WcmqsSearchPage(drone);
                wcmqsSearchPage.render();
                Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains("Enterprise Network"),
                        "Tag search did not return Enterprise Network");
                Assert.assertTrue(wcmqsSearchPage.getTagSearchResults().toString().contains("Alfresco WCM"), "Tag search did not return Alfresco WCM");

        }

        /**
         * AONE-5667:Publications page
         */
        @Test(groups = "WQS")
        public void AONE_5667() throws Exception
        {
                // ---- Step 1 ----
                // ---- Step action ---
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened
                drone.navigateTo(wqsURL);

                // ---- Step 2 ----
                // ---- Step action ----
                // Verify Research reports page;
                // ---- Expected results ----
                // The following items are displayed: *Research reports section(some text in it)
                // Publications section (Publication's name link, Publication's preview, Publications date and author, Publication's description);
                // Tags section;
                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                wcmqsHomePage.render();
                wcmqsHomePage.mouseOverMenu("publications");
                wcmqsHomePage.openPublicationsPageFolder("research reports");
                WcmqsAllPublicationsPage wcmqsAllPublicationsPage = new WcmqsAllPublicationsPage(drone);
                wcmqsAllPublicationsPage.render();
                Assert.assertTrue(wcmqsAllPublicationsPage.isPublicationDescriptionDisplay());
                Assert.assertTrue(wcmqsAllPublicationsPage.isPublicationPreviewDisplay());
                Assert.assertTrue(wcmqsAllPublicationsPage.isPublicationDateAndAuthorDisplay());
                Assert.assertTrue(wcmqsAllPublicationsPage.isPublicationTagDisplay());
                Assert.assertTrue(wcmqsAllPublicationsPage.isPublicationTitleDisplay());
        }

        /**
         * AONE-5668:Publications page
         */
        @Test(groups = "WQS")
        public void AONE_5668() throws Exception
        {
                drone.navigateTo(wqsURL);
                // ---- Step 1 ----
                // ---- Step action ----
                // Click Enterprise network publication link;
                // ---- Expected results ----
                // Publication is opened successfully, the following items are displayed: Publication name, Publication date, Publication preview, Tags section, Publication details section;
                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                wcmqsHomePage.render();
                wcmqsHomePage.mouseOverMenu("publications");
                wcmqsHomePage.openPublicationsPageFolder("research reports");
                WcmqsAllPublicationsPage wcmqsAllPublicationsPage = new WcmqsAllPublicationsPage(drone);

                wcmqsAllPublicationsPage.clickDocumentByTitle("Enterprise Network");

                WcmqsPublicationPage wcmqsPublicationPage = new WcmqsPublicationPage(drone);
                wcmqsPublicationPage.render();

                Assert.assertTrue(wcmqsPublicationPage.isPublicationNameDisplay());
                Assert.assertTrue(wcmqsPublicationPage.isPublicationPreviewDisplay());
                Assert.assertTrue(wcmqsPublicationPage.isPublicationTagsDisplay());
                Assert.assertTrue(wcmqsPublicationPage.isPublicationPublishDateDisplay());
                Assert.assertTrue(wcmqsPublicationPage.isPublicationDetailsDisplay());
                // ---- Step 2 ----
                // ---- Step action ----
                // Return to Research reports page and click Enterprise network publication preview;
                // ---- Expected results ----
                // Publication is opened successfully;
                wcmqsHomePage.mouseOverMenu("publications");
                wcmqsHomePage.openPublicationsPageFolder("research reports");
                wcmqsAllPublicationsPage = new WcmqsAllPublicationsPage(drone);

                wcmqsAllPublicationsPage.clickDocumentImage("Enterprise Network");
                wcmqsPublicationPage = new WcmqsPublicationPage(drone);
                wcmqsPublicationPage.render();

                Assert.assertTrue(wcmqsPublicationPage.isPublicationNameDisplay());

        }

        /**
         * AONE-5669:Publications page
         */
        @Test(groups = "WQS")
        public void AONE_5669() throws Exception
        {
                drone.navigateTo(wqsURL);
                // ---- Step 1 ----
                // ---- Step action ----
                // Click Enterprise network publication link;
                // ---- Expected results ----
                // Publications page is opened;

                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                wcmqsHomePage.render();
                wcmqsHomePage.mouseOverMenu("publications");
                wcmqsHomePage.openPublicationsPageFolder("research reports");
                WcmqsAllPublicationsPage wcmqsAllPublicationsPage = new WcmqsAllPublicationsPage(drone);

                wcmqsAllPublicationsPage.clickDocumentByTitle("Enterprise Network");
                // ---- Step 2 ----
                // ---- Step action ----
                // Verify publication details;
                // ---- Expected results ----
                // Publication page details contains: Publication description, fields(Author, Published, Size, Mime Type, Download);

                WcmqsPublicationPage wcmqsPublicationPage = new WcmqsPublicationPage(drone);
                wcmqsPublicationPage.render();

                //Verify Publication page details contains: Publication description, fields(Author, Published, Size, Mime Type, Download)
                Assert.assertTrue(wcmqsPublicationPage.isPublicationDescriptionDisplay());
                Assert.assertTrue(wcmqsPublicationPage.isPublicationAuthorDisplay());
                Assert.assertTrue(wcmqsPublicationPage.isPublicationPublishDateDisplay());
                Assert.assertTrue(wcmqsPublicationPage.isPublicationSizeDisplay());
                Assert.assertTrue(wcmqsPublicationPage.isPublicationMimeDisplay());
                Assert.assertTrue(wcmqsPublicationPage.isPublicationDownloadDisplay());

                // ---- Step 3 ----
                // ---- Step action ----
                // Click link in Download field;
                // ---- Expected results ----
                // Publication is downloaded;

                File testFile = wcmqsPublicationPage.downloadFiles();
                Assert.assertTrue(testFile.length() > 0);
        }

        /**
         * AONE-5670:Publications - white papers
         */
        @Test(groups = "WQS")
        public void AONE_5670() throws Exception
        {
                // ---- Step 1 ----
                // ---- Step action ---
                // Navigate to http://host:8080/wcmqs
                // ---- Expected results ----
                // Sample site is opened
                drone.navigateTo(wqsURL);

                // ---- Step 2 ----
                // ---- Step action ----
                // Verify Research reports page;
                // ---- Expected results ----
                // The following items are displayed: *white papers section(some text in it)
                // Publications section (Publication's name link, Publication's preview, Publications date and author, Publication's description);
                // Tags section;
                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);

                wcmqsHomePage.mouseOverMenu("publications");
                wcmqsHomePage.openPublicationsPageFolder("white papers");
                WcmqsAllPublicationsPage wcmqsAllPublicationsPage = new WcmqsAllPublicationsPage(drone);
                wcmqsAllPublicationsPage.render();
                Assert.assertTrue(wcmqsAllPublicationsPage.isPublicationDescriptionDisplay());
                Assert.assertTrue(wcmqsAllPublicationsPage.isPublicationPreviewDisplay());
                Assert.assertTrue(wcmqsAllPublicationsPage.isPublicationDateAndAuthorDisplay());
                Assert.assertTrue(wcmqsAllPublicationsPage.isPublicationTagDisplay());
                Assert.assertTrue(wcmqsAllPublicationsPage.isPublicationTitleDisplay());
        }

        /**
         * AONE-5671:Publications - white papers publications
         */
        @Test(groups = "WQS")
        public void AONE_5671() throws Exception
        {
                drone.navigateTo(wqsURL);
                // ---- Step 1 ----
                // ---- Step action ----
                // Click Enterprise network publication link;
                // ---- Expected results ----
                // Publication is opened successfully, the following items are displayed: Publication name, Publication date, Publication preview, Tags section, Publication details section;
                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                wcmqsHomePage.render();
                wcmqsHomePage.mouseOverMenu("publications");
                wcmqsHomePage.openPublicationsPageFolder("white papers");
                WcmqsAllPublicationsPage wcmqsAllPublicationsPage = new WcmqsAllPublicationsPage(drone);

                wcmqsAllPublicationsPage.clickDocumentByTitle("Records Management Datasheet");

                WcmqsPublicationPage wcmqsPublicationPage = new WcmqsPublicationPage(drone);
                wcmqsPublicationPage.render();

                Assert.assertTrue(wcmqsPublicationPage.isPublicationNameDisplay());
                Assert.assertTrue(wcmqsPublicationPage.isPublicationPreviewDisplay());
                Assert.assertTrue(wcmqsPublicationPage.isPublicationTagsDisplay());
                Assert.assertTrue(wcmqsPublicationPage.isPublicationPublishDateDisplay());
                Assert.assertTrue(wcmqsPublicationPage.isPublicationDetailsDisplay());
                // ---- Step 2 ----
                // ---- Step action ----
                // Return to Research reports page and click Enterprise network publication preview;
                // ---- Expected results ----
                // Publication is opened successfully;
                wcmqsHomePage.mouseOverMenu("publications");
                wcmqsHomePage.openPublicationsPageFolder("white papers");
                wcmqsAllPublicationsPage = new WcmqsAllPublicationsPage(drone);

                wcmqsAllPublicationsPage.clickDocumentImage("Records Management Datasheet");
                wcmqsPublicationPage = new WcmqsPublicationPage(drone);
                wcmqsPublicationPage.render();

                Assert.assertTrue(wcmqsPublicationPage.isPublicationNameDisplay());

        }

        /**
         * AONE-5672:Publications - white papers publications details
         */
        @Test(groups = "WQS")
        public void AONE_5672() throws Exception
        {
                drone.navigateTo(wqsURL);
                // ---- Step 1 ----
                // ---- Step action ----
                // Click Enterprise network publication link;
                // ---- Expected results ----
                // Publications page is opened;

                WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
                wcmqsHomePage.render();
                wcmqsHomePage.mouseOverMenu("publications");
                wcmqsHomePage.openPublicationsPageFolder("white papers");
                WcmqsAllPublicationsPage wcmqsAllPublicationsPage = new WcmqsAllPublicationsPage(drone);

                wcmqsAllPublicationsPage.clickDocumentByTitle("Records Management Datasheet");
                // ---- Step 2 ----
                // ---- Step action ----
                // Verify publication details;
                // ---- Expected results ----
                // Publication page details contains: Publication description, fields(Author, Published, Size, Mime Type, Download);

                WcmqsPublicationPage wcmqsPublicationPage = new WcmqsPublicationPage(drone);
                wcmqsPublicationPage.render();

                //Verify Publication page details contains: Publication description, fields(Author, Published, Size, Mime Type, Download)
                Assert.assertTrue(wcmqsPublicationPage.isPublicationDescriptionDisplay());
                Assert.assertTrue(wcmqsPublicationPage.isPublicationAuthorDisplay());
                Assert.assertTrue(wcmqsPublicationPage.isPublicationPublishDateDisplay());
                Assert.assertTrue(wcmqsPublicationPage.isPublicationSizeDisplay());
                Assert.assertTrue(wcmqsPublicationPage.isPublicationMimeDisplay());
                Assert.assertTrue(wcmqsPublicationPage.isPublicationDownloadDisplay());

                // ---- Step 3 ----
                // ---- Step action ----
                // Click link in Download field;
                // ---- Expected results ----
                // Publication is downloaded;

                File testFile = wcmqsPublicationPage.downloadFiles();
                Assert.assertTrue(testFile.length() > 0);
        }
}
