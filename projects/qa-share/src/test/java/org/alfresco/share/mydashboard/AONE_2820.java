package org.alfresco.share.mydashboard;

import org.alfresco.po.share.dashlet.MyActivitiesDashlet;
import org.alfresco.po.share.dashlet.MyDocumentsDashlet;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.dashlet.MyTasksDashlet;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.test.FailedTestListener;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.alfresco.share.util.api.CreateUserAPI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Sergey Kardash
 */

@Listeners(FailedTestListener.class)
//@AlfrescoTest(testlink=“AONE_2820”)
public class AONE_2820 extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(AONE_2820.class);

    private String testUser;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();

        testName = this.getClass().getSimpleName();
        testUser = getUserNameFreeDomain(testName);

        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
    }

    @Test(groups = { "EnterpriseOnly" }, timeOut = 280000)
    public void AONE_2820() throws Exception
    {
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // ---- Step 1 ----
        // ---- Step action ---
        // Modify that users dashboard somehow, ex. remove a dashlet.
        // ---- Expected results ----
        // The dashbord is modified.
        ShareUserDashboard.removeDashletFromUserDashboard(drone, Dashlets.MY_TASKS);
        ShareUser.logout(drone);

        // ---- Step 2 ----
        // ---- Step action ---
        // Login as admin and delete the user "qwerty".
        // ---- Expected results ----
        // The user is removed.
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUser.deleteUser(drone, testUser).render();

        // ---- Step 3 ----
        // ---- Step action ---
        // Create a new user with the same username .
        // ---- Expected results ----
        // New user is created.
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // ---- Step 4 ----
        // ---- Step action ---
        // Verify that dashboards order is set to default.
        // ---- Expected results ----
        // Dashboards are default.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        MySitesDashlet mySites = ShareUserDashboard.getDashlet(drone, Dashlets.MY_SITES).render();
        Assert.assertTrue(mySites.isHelpIconDisplayed(), "Help icon missing");

        MyActivitiesDashlet myActivities = ShareUserDashboard.getDashlet(drone, Dashlets.MY_ACTIVITIES).render();
        Assert.assertTrue(myActivities.isHelpIconDisplayed(), "Help icon missing");

        MyTasksDashlet myTasks = ShareUserDashboard.getDashlet(drone, Dashlets.MY_TASKS).render();
        Assert.assertTrue(myTasks.isHelpIconDisplayed());

        MyDocumentsDashlet myDocuments = ShareUserDashboard.getDashlet(drone, Dashlets.MY_DOCUMENTS).render();
        Assert.assertTrue(myDocuments.isHelpIconDisplayed(), "Help icon missing");
    }

    @AfterClass(alwaysRun = true)
    public void tearDown()
    {
        //super.tearDown();

        //or
        ShareUser.logout(drone);
    }

}
