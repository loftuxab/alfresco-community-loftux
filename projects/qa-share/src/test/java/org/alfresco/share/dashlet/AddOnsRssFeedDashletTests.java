package org.alfresco.share.dashlet;

import java.util.List;
import java.util.Set;

import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.dashlet.AddOnsRssFeedDashlet;
import org.alfresco.po.share.dashlet.RssFeedUrlBoxPage;
import org.alfresco.po.share.dashlet.RssFeedUrlBoxPage.NrItems;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class AddOnsRssFeedDashletTests extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(AddOnsRssFeedDashletTests.class);
    String headerInfo = "Find, rate, and contribute Alfresco add-ons and extensions. Visit the Alfresco Add-ons Home Page";
    String helpInfo = "This dashlet shows the latest news from Alfresco Add-ons. Click the edit icon on the dashlet to configure the feed.";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();

        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
    }

    @Test(groups = { "DataPrepDashlets" })
    public void dataPrep_2904() throws Exception
    {
        String testName = getTestName() + "6";
        String testUser = getUserNameFreeDomain(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Alfresco Add-ons News Feed dashlet added to My Dashboard
        ShareUserDashboard.addDashlet(drone, Dashlets.ALFRESCO_ADDONS_RSS_FEED).render();

    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_2904() throws Exception
    {
        String testName = getTestName() + "6";
        String testUser = getUserNameFreeDomain(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        AddOnsRssFeedDashlet rssDashlet = ShareUserDashboard.getDashlet(drone, Dashlets.ALFRESCO_ADDONS_RSS_FEED).render();

        String title = "";
        int counter = 1;
        int retryRefreshCount = 5;

        while (counter <= retryRefreshCount)
        {
            title = rssDashlet.getTitle();
            if (title.equals("Newest Add-ons"))
            {
                break;
            }
            else
            {
                logger.info("Wait a few seconds to load the RSS feed");
                rssDashlet.waitUntilLoadingDisappears();
                counter++;
            }
        }

        // ---- Step 1 ----
        // ---- Step action ---
        // Verify info available on dashlet
        // ---- Expected results ----
        // The next info should be available:
        // - Newest Add-ons title
        // - Configure this dashlet icon
        // - Display help for this dashlet icon
        // - Find, rate, and contribute Alfresco add-ons and extensions. Visit the Alfresco Add-ons Home Page description
        Assert.assertTrue(title.equals("Newest Add-ons"));
        Assert.assertTrue(rssDashlet.getHeaderInfo().equals(headerInfo));
        Assert.assertTrue(rssDashlet.isHelpIconDisplayed());
        Assert.assertTrue(rssDashlet.isConfigurePresent());

        // ---- Step 2 ----
        // ---- Step action ---
        // Click Configure this dashlet icon
        // ---- Expected results ----
        // Help pop-up displays:
        // This dashlet shows the latest news from Alfresco Add-ons. Click the edit icon on the dashlet to configure the feed
        RssFeedUrlBoxPage rssFeedUrlBoxPage = rssDashlet.clickConfigure().render();
        Assert.assertNotNull(rssFeedUrlBoxPage);
        rssFeedUrlBoxPage.clickClose();

        // ---- Step 3 ----
        // ---- Step action ---
        // Click Display help for this dashlet icon
        // ---- Expected results ----
        // Configuring dashlet form displays
        rssDashlet.clickOnHelpIcon();
        Assert.assertTrue(rssDashlet.isBalloonDisplayed());
        Assert.assertTrue(rssDashlet.getHelpBalloonMessage().equals(helpInfo));
        rssDashlet.getHelpBalloonMessage();

        // ---- Step 4 ----
        // ---- Step action ---
        // Click X
        // ---- Expected results ----
        // Help pop-up closed
        rssDashlet.closeHelpBallon();

        // ---- Step 5 ----
        // ---- Step action ---
        // Click all available links on the dahslet
        // ---- Expected results ----
        // All links lead to correct we pages
        List<ShareLink> links = rssDashlet.getHeadlineLinksFromDashlet();
        String firstRssDescription = links.get(0).getDescription();
        links.get(0).openLink();
        Assert.assertTrue(drone.getTitle().contains(firstRssDescription));

    }

    @Test(groups = { "DataPrepDashlets" })
    public void dataPrep_2905() throws Exception
    {
        String testName = getTestName() + "18";
        String testUser = getUserNameFreeDomain(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Alfresco Add-ons News Feed dashlet added to My Dashboard
        ShareUserDashboard.addDashlet(drone, Dashlets.ALFRESCO_ADDONS_RSS_FEED).render();
    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_2905() throws Exception
    {
        String testName = getTestName() + "18";
        String testUser = getUserNameFreeDomain(testName);
        String rssUrl = "http://feeds.reuters.com/reuters/businessNews";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        AddOnsRssFeedDashlet rssDashlet = ShareUserDashboard.getDashlet(drone, Dashlets.ALFRESCO_ADDONS_RSS_FEED).render();

        // ---- Step 1 ----
        // ---- Step action ---
        // Click Configure this dashlet icon
        // ---- Expected results ----
        // Enter Feed URL form displays
        RssFeedUrlBoxPage rssFeedUrlBoxPage = rssDashlet.clickConfigure().render();
        Assert.assertNotNull(rssFeedUrlBoxPage);

        // ---- Step 2 ----
        // ---- Step action ---
        // Fill in URL field;
        // ---- Expected results ----
        // Information is entered successfully;
        rssFeedUrlBoxPage.fillURL(rssUrl);

        // ---- Step 3 ----
        // ---- Step action ---
        // In Number of items to display drop-down menu choose any value (e.g. 5);
        // ---- Expected results ----
        // Information is chosen successfully;
        rssFeedUrlBoxPage.selectNrOfItemsToDisplay(NrItems.Five);

        // ---- Step 4 ----
        // ---- Step action ---
        // Activate Open links in new window check-box
        // ---- Expected results ----
        // Check-box is selected;
        rssFeedUrlBoxPage.selectOpenLinkNewWindow();

        // ---- Step 5 ----
        // ---- Step action ---
        // Press OK button;
        // ---- Expected results ----
        // My Dashboard page is displayed, feed information is changed;
        rssFeedUrlBoxPage.clickOk();

        // wait a few seconds to load the RSS Feed
        rssDashlet.waitUntilLoadingDisappears();
        rssDashlet.render();

        // ---- Step 6 ----
        // ---- Step action ---
        // Click on any RSS news;
        // ---- Expected results ----
        // RSS news is opened in new window;
        List<ShareLink> links = rssDashlet.getHeadlineLinksFromDashlet();
        links.get(0).openLink();

        drone.waitForPageLoad(5);
        
        Set<String> windowHandles = drone.getWindowHandles();
        Assert.assertEquals(windowHandles.size(), 2);

    }

    @Test(groups = { "DataPrepDashlets" })
    public void dataPrep_2906() throws Exception
    {
        String testName = getTestName() + "6";
        String testUser = getUserNameFreeDomain(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Alfresco Add-ons News Feed dashlet added to My Dashboard
        ShareUserDashboard.addDashlet(drone, Dashlets.ALFRESCO_ADDONS_RSS_FEED).render();
    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_2906() throws Exception
    {
        String testName = getTestName() + "6";
        String testUser = getUserNameFreeDomain(testName);
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        String rssUrl = "http://feeds.reuters.com/reuters/businessNews";

        AddOnsRssFeedDashlet rssDashlet = ShareUserDashboard.getDashlet(drone, Dashlets.ALFRESCO_ADDONS_RSS_FEED).render();

        // ---- Step 1 ----
        // ---- Step action ---
        // Click Configure this dashlet icon
        // ---- Expected results ----
        // Enter Feed URL form displays
        RssFeedUrlBoxPage rssFeedUrlBoxPage = rssDashlet.clickConfigure().render();
        Assert.assertNotNull(rssFeedUrlBoxPage);

        // ---- Step 2 ----
        // ---- Step action ---
        // Leave URL field empty
        // ---- Expected results ----
        // OK button disabled;
        // For v4.2: OK button is enabled and "The value cannot be empty" message appears near the "URL" button;
        String errorEmpty = rssFeedUrlBoxPage.getValidationMessageFromUrlField("");
        Assert.assertTrue(errorEmpty.equals("The value cannot be empty."));

        // ---- Step 3 ----
        // ---- Step action ---
        // Fill URL field with spaces
        // ---- Expected results ----
        // OK button disabled;
        // For v4.2: OK button is enabled and "The value cannot be empty" message appears near the "URL" button;
        String errorSpaces = rssFeedUrlBoxPage.getValidationMessageFromUrlField("                ");
        Assert.assertTrue(errorSpaces.equals("The value cannot be empty."));

        // ---- Step 4 ----
        // ---- Step action ---
        // Enter into URL filed special characters (~!@#$%^&*()_{}:|"<>?)
        // ---- Expected results ----
        // OK button disabled;
        // For v4.2: OK button is enabled and "Field contains an error." message appears near the "URL" button
        String errorSpecial = rssFeedUrlBoxPage.getValidationMessageFromUrlField("~!@#$%^&*()_{}:");
        Assert.assertTrue(errorSpecial.equals("Field contains an error."));

        // ---- Step 5 ----
        // ---- Step action ---
        // Enter info URL field more than 1024 character (without slashes and with http://)
        // ---- Expected results ----
        // OK button enabled;
        String str = createString(1024);
        rssFeedUrlBoxPage.fillURL("http://" + str);

        // ---- Step 6 ----
        // ---- Step action ---
        // Press OK button;
        // ---- Expected results ----
        // Dashlet displays correctly
        rssFeedUrlBoxPage.clickOk();
        // rssDashlet = ShareUserDashboard.getDashlet(drone, Dashlets.ALFRESCO_ADDONS_RSS_FEED).render();
        rssDashlet.render();
        Assert.assertTrue(rssDashlet.getHeaderInfo().equals(headerInfo));
        Assert.assertTrue(rssDashlet.isHelpIconDisplayed());
        Assert.assertTrue(rssDashlet.isConfigurePresent());

        // ---- Step 7 ----
        // ---- Step action ---
        // Click Configure this dashlet icon again
        // ---- Expected results ----
        // Enter RSS URL for displays
        rssFeedUrlBoxPage = rssDashlet.clickConfigure().render();

        // ---- Step 8 ----
        // ---- Step action ---
        // Enter something into URL field and click Cancel button
        // ---- Expected results ----
        // Dashlet displays. No changes were made.
        rssFeedUrlBoxPage.fillURL(rssUrl);
        rssFeedUrlBoxPage.clickCancel();
        rssDashlet.render();
        Assert.assertTrue(rssDashlet.getHeaderInfo().equals(headerInfo));
        Assert.assertTrue(rssDashlet.isHelpIconDisplayed());
        Assert.assertTrue(rssDashlet.isConfigurePresent());
    }

    private static String createString(int size)
    {
        StringBuilder o = new StringBuilder(size);
        for (int i = 0; i < size; i++)
        {
            o.append("f");
        }
        return o.toString();
    }

}
