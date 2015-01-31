package org.alfresco.share.wqs;

import static org.testng.Assert.assertTrue;

import java.util.List;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.Navigation;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.dashlet.MyCalendarDashlet;
import org.alfresco.po.share.dashlet.SiteWebQuickStartDashlet;
import org.alfresco.po.share.dashlet.WebQuickStartOptions;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CreateSitePage;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteNavigation;
import org.alfresco.po.share.site.SiteType;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.share.clustering.SiteDashboardClusterTests;
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
public class WqsShareTests extends AbstractUtils
{
    private SharePage page;
    private String testName;

    private static final Logger logger = Logger.getLogger(ShareRefreshCopyToSites.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
    }


    
    @Test(groups = "DataPrepWQS")
    public void dataPrep_AONE_5593() throws Exception
    {

        String testName = getTestName();
        String user1 = getUserNameForDomain(testName + "-op", DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };
        
        String siteName = testName + "SiteName";
        String siteURL = testName + "SiteURL";

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        ShareUser.createSite(drone, siteName, "Public");
        
    }
     
    
    
    
    /**
     * AONE-5595:Creating web site in Share
     */

    @Test(groups = "WQS")
    public void AONE_5595() throws Exception
    {
        // --- Step 1 ---
        // --- Step action ---
        // Click Create site link;
        // --- Expected results ---
        // Create Site window is opened;

        String testName = getTestName();
        String user1 = getUserNameForDomain(testName + "-op", DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String siteName = testName + "SiteName";
        String siteURL = testName + "SiteURL";

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        page = ShareUser.login(drone, user1, DEFAULT_PASSWORD);

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

        assertTrue(wqsDashlet.isImportMessage());

    }

}
