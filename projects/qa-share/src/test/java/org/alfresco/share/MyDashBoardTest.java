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
package org.alfresco.share;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.api.CreateUserAPI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Calendar;

public class MyDashBoardTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(MyDashBoardTest.class);

    protected String testUser;
    protected String testUserPass = DEFAULT_PASSWORD;

    protected String siteName = "";

    /**
     * Class includes: Tests from TestLink in Area: My DashBoard Tests
     * <ul>
     * <li>Test User DashBoard UI</li>
     * <li>Test various Entries in default Dashlets</li>
     * </ul>
     */
    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = testUser + "@" + DOMAIN_FREE;
        logger.info("[Suite ] : Start Tests in: " + testName);
    }

    @Test(groups = { "DataPrepMyDashboard" })
    public void dataPrep_MyDashBoard_1506() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    /**
     * Test:
     * <ul>
     * <li>Login</li>
     * <li>Check Logo the Dashboard</li>
     * </ul>
     */
    @Test  (groups = { "CloudOnly" }, enabled = false) //duplicate test (the test is executed via WebDriver project))
    public void AONE_12091()
    {
        try
        {
            /** Start Test */
            testName = getTestName();

            /** Test Data Setup */
            String testUser = getUserNameFreeDomain(testName);

            /** Test Steps */
            // Login
            ShareUser.login(drone, testUser, testUserPass);

            // Open DashBoard
            DashBoardPage myDashPage = ShareUser.openUserDashboard(drone);

            // Check Logo
            Boolean logoPresent = myDashPage.isLogoPresent();
            Assert.assertTrue(logoPresent, testName + " : Fail : Logo Not Found");
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            testCleanup(drone, testName);
        }
    }

    @Test(groups = { "DataPrepMyDashboard" })
    public void dataPrep_MyDashBoard_1507() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, testUserPass);
    }

    /**
     * Test:
     * <ul>
     * <li>Login</li>
     * <li>Check CopyRight Info on the Dashboard</li>
     * </ul>
     */
    @Test (groups = { "CloudOnly" }, enabled = false) //duplicate test (the test is executed via WebDriver project))
    public void AONE_12092()
    {
        try
        {
            /** Start Test */
            testName = getTestName();

            /** Test Data Setup */
            String testUser = getUserNameFreeDomain(testName);

            Calendar now = Calendar.getInstance();
            int toYear = now.get(Calendar.YEAR);

            String entry = "Alfresco Software, Inc. © 2005-" + toYear + " All rights reserved.";

            /** Test Steps */
            // Login
            ShareUser.login(drone, testUser, testUserPass);

            // Open DashBoard
            DashBoardPage myDashPage = ShareUser.openUserDashboard(drone);

            // Check CopyRight Info
            String actualCopyRight = myDashPage.getCopyRight();
            Assert.assertEquals(actualCopyRight, entry, testName + " : Fail : Copyright Info does not match");
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            testCleanup(drone, testName);
        }
    }

    // MyDashBoard Tests
    @Test(groups = { "DataPrepMyDashboard" })
    public void dataPrep_MyDashBoard_236() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    /**
     * Test:
     * <ul>
     * <li>Login</li>
     * <li>Check CopyRight Info on the Dashboard</li>
     * </ul>
     */
    @Test (groups = { "CloudOnly" }, enabled = false) //duplicate test (the test is executed via WebDriver project))
    public void AONE_12017()
    {
        try
        {
            /** Start Test */
            testName = getTestName();

            /** Test Data Setup */
            String testUser = getUserNameFreeDomain(testName);

            /** Test Steps */
            // Login
            ShareUser.login(drone, testUser, testUserPass);

            // Open DashBoard
            DashBoardPage myDashPage = ShareUser.openUserDashboard(drone);

            // Check Default Components: Welcome Widget
            Assert.assertTrue(myDashPage.panelExists(uiWelcomePanel), testName + " : Fail : Welcome Widget not found");

            // Check Default Components: My Sites
            Boolean panelExists = myDashPage.panelExists(uiMySitesDashlet);
            Assert.assertTrue(panelExists, testName + " : Fail : My Sites Dashlet not found");

            // Check Default Components: My Documents
            Assert.assertTrue(myDashPage.panelExists(uiMyDocuments), testName + " : Fail : My Documents Dashlet not found");

            // Check Default Components: My Tasks
            Assert.assertTrue(myDashPage.panelExists(uiMyTasks), testName + " : Fail : My Tasks Dashlet not found");

            // Check Default Components: My Activities
            Assert.assertTrue(myDashPage.panelExists(uiMyActivities), testName + " : Fail : My Activities Dashlet not found");
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            testCleanup(drone, testName);
        }
    }

    @Test(groups = { "DataPrepMyDashboard" })
    public void dataPrep_MyDashBoard_332() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    /**
     * Test:
     * <ul>
     * <li>Login</li>
     * <li>Create Site: Public</li>
     * <li>Open User Dash-board</li>
     * <li>Check that the User Dash-board > My Sites Dashlet shows the new Site</li>
     * </ul>
     */
    @Test (groups = { "CloudOnly" }, enabled = false) //duplicate test (the test is executed via WebDriver project))
    public void AONE_12072()
    {
        try
        {
            /** Start Test */
            testName = getTestName();

            /** Test Data Setup */
            String testUser = getUserNameFreeDomain(testName);
            String siteName = getSiteName(testName) + System.currentTimeMillis();

            String entry = "";

            /** Test Steps */
            // Login
            ShareUser.login(drone, testUser, testUserPass);

            // createSite
            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

            // Search for Site
            ShareUser.openUserDashboard(drone);

            // Check Activity Feed: Site Name
            entry = siteName;
            Boolean entryFound = ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_SITES, entry, true);
            Assert.assertTrue(entryFound, "Activity Entry not found: " + entry);
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            testCleanup(drone, testName);
        }
    }
}
