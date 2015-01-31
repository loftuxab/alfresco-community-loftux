package org.alfresco.share.mydashboard;

import java.util.Set;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.dashlet.ConfigureWebViewDashletBoxPage;
import org.alfresco.po.share.dashlet.WebViewDashlet;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.alfresco.share.util.api.CreateUserAPI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class MyDashboardWebViewDashletTests extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(MyDashboardWebViewDashletTests.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();

        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
    }

    @Test(groups = { "DataPrepDashlets" })
    public void dataPrep_2898() throws Exception
    {
        String testName = getTestName() + "2";
        String testUser = getUserNameFreeDomain(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Alfresco Add-ons News Feed dashlet added to My Dashboard
        ShareUserDashboard.addDashlet(drone, Dashlets.WEB_VIEW).render();
    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_2898() throws Exception
    {
        String testName = getTestName() + "2";
        String testUser = getUserNameFreeDomain(testName);
        String url = "http://www.google.com/";
        String linkTitle = "Google";

        DashBoardPage dashBoard = ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();

        // ---- Step 1 ----
        // ---- Step action ---
        // Click Configure this dashlet icon
        // ---- Expected results ----
        // Enter Feed URL form displays        
        //WebViewDashlet webDashlet = ShareUserDashboard.getDashlet(drone, Dashlets.WEB_VIEW).render();
        WebViewDashlet webDashlet = dashBoard.getDashlet("web-view").render();
        ConfigureWebViewDashletBoxPage configure = webDashlet.clickConfigure();

        // ---- Step 2 ----
        // ---- Step action ---
        // Enter Link Title;
        // ---- Expected results ----
        // Link title is entered;
        configure.fillLinkTitle(linkTitle);

        // ---- Step 3 ----
        // ---- Step action ---
        // Enter URL;
        // ---- Expected results ----
        // URL is entered;
        configure.fillUrlField(url);

        // ---- Step 4 ----
        // ---- Step action ---
        // Click OK button;
        // ---- Expected results ----
        // Configure Web View dashlet form is opened;
        configure.clickOkButton();

        // ---- Step 5 ----
        // ---- Step action ---
        // Verify dashlet title is displayed as title link;
        // ---- Expected results ----
        // Dashlet title is displayed as title link;
        for (int i = 1; i < 1000; i++)
        {
            if (!webDashlet.getWebViewDashletTitle().isEmpty())
            {
                break;
            }
            
            logger.info("Wait for the title to be updated");
            i++;
        }

        String newTitle = webDashlet.getWebViewDashletTitle();
        Assert.assertTrue(newTitle.equals(linkTitle));

        // ---- Step 6 ----
        // ---- Step action ---
        // Click the dashlet title;
        // ---- Expected results ----
        // The website is opened in a separate window.
        webDashlet.clickTitle();
        Set<String> windowHandles = drone.getWindowHandles();
        Assert.assertEquals(2, windowHandles.size());
    }
}
