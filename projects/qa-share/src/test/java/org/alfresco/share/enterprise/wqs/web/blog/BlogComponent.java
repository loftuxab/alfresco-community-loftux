package org.alfresco.share.enterprise.wqs.web.blog;

/**
 * Created by P3700473 on 12/2/2014.
 */

import org.alfresco.po.share.dashlet.SiteWebQuickStartDashlet;
import org.alfresco.po.share.dashlet.WebQuickStartOptions;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.wqs.WcmqsBlogPage;
import org.alfresco.po.share.wqs.WcmqsHomePage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

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
                String setHostAddress = "cmd.exe /c echo. >> %WINDIR%\\System32\\Drivers\\Etc\\hosts && echo " + ipAddress + " " + siteName
                        + " >> %WINDIR%\\System32\\Drivers\\Etc\\hosts";
                Runtime.getRuntime().exec(setHostAddress);
        }

        /*
        * AONE-5673 Blogs page
        */
        @Test(groups = { "WQS" })
        public void AONE_5673() throws Exception
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

        public void navigateTo(String url)
        {
                drone.navigateTo(url);
        }

}
