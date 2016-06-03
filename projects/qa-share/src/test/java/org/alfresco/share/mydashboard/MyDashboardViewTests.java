/*
 * #%L
 * qa-share
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.alfresco.share.mydashboard;

import java.util.List;

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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class MyDashboardViewTests extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(MyDashboardViewTests.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();

        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
    }

    @Test(groups = { "EnterpriseOnly" }, timeOut = 100000 )
    public void AONE_2819() throws Exception
    {
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        MySitesDashlet mySites = ShareUserDashboard.getDashlet(drone, Dashlets.MY_SITES).render();
        Assert.assertTrue(mySites.isHelpIconDisplayed(), "Help icon missing");

        MyActivitiesDashlet myActivities = ShareUserDashboard.getDashlet(drone, Dashlets.MY_ACTIVITIES).render();
        Assert.assertTrue(myActivities.isHelpIconDisplayed(), "Help icon missing");

        MyTasksDashlet myTasks = ShareUserDashboard.getDashlet(drone, Dashlets.MY_TASKS).render();
        Assert.assertTrue(myTasks.isHelpIconDisplayed(), "Help icon missing");

        MyDocumentsDashlet myDocuments = ShareUserDashboard.getDashlet(drone, Dashlets.MY_DOCUMENTS).render();
        Assert.assertTrue(myDocuments.isHelpIconDisplayed(), "Help icon missing");
    }

    @Test(groups = { "EnterpriseOnly"}, timeOut = 280000  )
    public void AONE_2820() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);     
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.createEnterpriseUserAPI(drone, ADMIN_USERNAME, testUserInfo);
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // ---- Step 1 ----
        // ---- Step action ---
        // Modify that users dashboard somehow, ex. remove a dashlet.
        // ---- Expected results ----
        // The dashbord is modified.
        ShareUserDashboard.removeDashletFromUserDashboard(drone, Dashlets.MY_TASKS);
        List<String> titles = ShareUserDashboard.getAllDashletTitles(drone);
        Assert.assertFalse(titles.contains("My Tasks"));
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
        CreateUserAPI.createEnterpriseUserAPI(drone, ADMIN_USERNAME, testUserInfo);

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
}
