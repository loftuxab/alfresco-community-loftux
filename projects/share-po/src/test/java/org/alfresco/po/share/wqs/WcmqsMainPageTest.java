//package org.alfresco.po.share.wqs;
//
//import org.alfresco.po.share.AbstractTest;
//import org.alfresco.po.share.dashlet.SiteWebQuickStartDashlet;
//import org.alfresco.po.share.dashlet.WebQuickStartOptions;
//import org.alfresco.po.share.enums.Dashlets;
//import org.alfresco.po.share.site.SiteDashboardPage;
//import org.alfresco.po.share.site.SitePageType;
//import org.alfresco.po.share.site.document.DocumentLibraryPage;
//import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
//import org.alfresco.share.util.AbstractUtils;
//import org.alfresco.share.util.ShareUser;
//import org.alfresco.share.util.ShareUserDashboard;
//import org.apache.log4j.Logger;
//import org.testng.Assert;
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.Test;
//
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//
///**
// * Created by P3700481 on 12/5/2014.
// */
//public class WcmqsMainPageTest extends AbstractTest
//{
//        private static final Logger logger = Logger.getLogger(WcmqsMainPageTest.class);
//        private String hostName;
//        private String wqsURL;
//        private String siteName;
//        private String ipAddress;
//
//
//        @BeforeClass(alwaysRun = true)
//        public void setup() throws Exception
//        {
//                String testName = this.getClass().getSimpleName();
//                siteName = testName;
//
//                String hostName = (shareUrl).replaceAll(".*\\//|\\:.*", "");
//                try
//                {
//                        ipAddress = InetAddress.getByName(hostName).toString().replaceAll(".*/", "");
//                        logger.info("Ip address from Alfresco server was obtained");
//                }
//                catch (UnknownHostException | SecurityException e)
//                {
//                        logger.error("Ip address from Alfresco server could not be obtained");
//                }
//
//                ;
//                wqsURL = siteName + ":8080/wcmqs";
//                logger.info(" wcmqs url : " + wqsURL);
//                logger.info("Start Tests from: " + testName);
//
//        }
//
//        @Test(groups = { "DataPrepWQS" })
//        public void dataPrep_MainPageTest() throws Exception
//        {
//                // User login
//                // ---- Step 1 ----
//                // ---- Step Action -----
//                // WCM Quick Start is installed; - is not required to be executed automatically
//                ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
//
//                // ---- Step 2 ----
//                // ---- Step Action -----
//                // Site "My Web Site" is created in Alfresco Share;
//                ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
//
//                // ---- Step 3 ----
//                // ---- Step Action -----
//                // WCM Quick Start Site Data is imported;
//                SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlets.WEB_QUICK_START);
//                SiteWebQuickStartDashlet wqsDashlet = siteDashBoard.getDashlet(SITE_WEB_QUICK_START_DASHLET).render();
//                wqsDashlet.selectWebsiteDataOption(WebQuickStartOptions.FINANCE);
//                wqsDashlet.clickImportButtton();
//
//                // Change property for quick start to sitename
//                DocumentLibraryPage documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
//                documentLibPage.selectFolder("Alfresco Quick Start");
//                EditDocumentPropertiesPage documentPropertiesPage = documentLibPage.getFileDirectoryInfo("Quick Start Editorial").selectEditProperties().render();
//                documentPropertiesPage.setSiteHostname(siteName);
//                documentPropertiesPage.clickSave();
//
//                // Change property for quick start live to ip address
//                documentLibPage.getFileDirectoryInfo("Quick Start Live").selectEditProperties().render();
//                documentPropertiesPage.setSiteHostname(ipAddress);
//                documentPropertiesPage.clickSave();
//
//                ShareUser.openSiteDashboard(drone, siteName);
//                // Data Lists component is added to the site
//                ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.DATA_LISTS);
//
//                // Site Dashboard is rendered with Data List link
//                ShareUser.openSiteDashboard(drone, siteName).render();
//
//
//                // setup new entry in hosts to be able to access the new wcmqs site
//                String setHostAddress = "cmd.exe /c echo. >> %WINDIR%\\System32\\Drivers\\Etc\\hosts && echo " + ipAddress + " " + siteName
//                        + " >> %WINDIR%\\System32\\Drivers\\Etc\\hosts";
//                Runtime.getRuntime().exec(setHostAddress);
//        }
//
//        @Test
//        public void testIsAlfrescoLogoDisplay() throws Exception{
//                WcmqsHomePage wqsPage = new WcmqsHomePage(drone);
//                Assert.assertTrue(wqsPage.isAlfrescoLogoDisplay());
//
//        }
//
//        @Test
//        public void testIsBottomUrlDisplayed() throws Exception{
//                WcmqsHomePage wqsPage = new WcmqsHomePage(drone);
//                Assert.assertNotNull(wqsPage.isBottomUrlDisplayed());
//        }
//
//        @Test
//        public void testIsSearchFieldWithButtonDisplay() throws Exception{
//                WcmqsHomePage wqsPage = new WcmqsHomePage(drone);
//                Assert.assertNotNull(wqsPage.isSearchFieldWithButtonDisplay());
//        }
//
//        @Test
//        public void testIsContactLinkDisplay() throws Exception{
//                WcmqsHomePage wqsPage = new WcmqsHomePage(drone);
//                Assert.assertNotNull(wqsPage.isContactLinkDisplay());
//        }
//
//        @Test
//        public void testIsSlideReadMoreButtonDisplayed(){
//                WcmqsHomePage wqsPage = new WcmqsHomePage(drone);
//                Assert.assertTrue(wqsPage.isSlideReadMoreButtonDisplayed());
//        }
//
//        @Test
//        public void testIsNewsAndAnalysisSectionDisplayed(){
//                WcmqsHomePage wqsPage = new WcmqsHomePage(drone);
//                Assert.assertNotNull(wqsPage.isNewsAndAnalysisSectionDisplayed());
//        }
//
//        @Test
//        public void testIsFeaturedSectionDisplayed(){
//                WcmqsHomePage wqsPage = new WcmqsHomePage(drone);
//                Assert.assertNotNull(wqsPage.isFeaturedSectionDisplayed());
//        }
//
//        @Test
//        public void testIsExampleFeatureSectionDisplayed(){
//                WcmqsHomePage wqsPage = new WcmqsHomePage(drone);
//                Assert.assertNotNull(wqsPage.isExampleFeatureSectionDisplayed());
//        }
//
//        @Test
//        public void testIsLatestBlogArticlesDisplayed(){
//                WcmqsHomePage wqsPage = new WcmqsHomePage(drone);
//                Assert.assertNotNull(wqsPage.isLatestBlogArticlesDisplayed());
//        }
//
//        @Test
//        public void testClickAlfrescoLink(){
//                WcmqsHomePage wqsPage = new WcmqsHomePage(drone);
//                wqsPage.clickAlfrescoLink();
//                String pageTitle = drone.getTitle();
//                Assert.assertTrue(pageTitle.contains("Alfresco"));
//
//        }
//
//        @Test
//        public void testClickContactLink(){
//                WcmqsHomePage wqsPage = new WcmqsHomePage(drone);
//                wqsPage.clickContactLink();
//                String pageTitle = drone.getTitle();
//                Assert.assertTrue(pageTitle.contains("Contact"));
//        }
//
//        @Test
//        public void testClickWebQuickStartLogo(){
//                WcmqsHomePage wqsPage = new WcmqsHomePage(drone);
//                wqsPage.clickWebQuickStartLogo();
//                String pageTitle = drone.getTitle();
//                Assert.assertTrue(pageTitle.contains("Home"));
//        }
//}
