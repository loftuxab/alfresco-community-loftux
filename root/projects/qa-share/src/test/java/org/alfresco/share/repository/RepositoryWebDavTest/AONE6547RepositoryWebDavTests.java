/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.share.repository.RepositoryWebDavTest;

import org.alfresco.explorer.WindowsExplorer;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author Sergey Kardash
 */
@Listeners(FailedTestListener.class)
public class AONE6547RepositoryWebDavTests extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(AONE6547RepositoryWebDavTests.class);

    String testName = "AONE6547";
    String testUser = testName + getRandomString(5);
    String siteName = "site" + testName + getRandomString(5);
    String webdavPath = "alfresco/webdav";

    WindowsExplorer explorer = new WindowsExplorer();

    Process removeMappedDrive;

    @Override
    @BeforeClass(groups = "setup", timeOut = 60000)
    public void setup() throws Exception
    {
        super.setup();

        removeMappedDrive = Runtime.getRuntime().exec("cmd /c start /WAIT net use * /d /y");
        removeMappedDrive.waitFor();

        logger.info("[Suite ] : Start Test in: " + "AONE6547RepositoryWebDavTests");
    }

    @BeforeMethod(groups = "setup", timeOut = 150000)
    public void precondition() throws Exception
    {
        // Any site is created
        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(drone);
    }

    /**
     * Test: AONE-6547:Verify accessing WebDAV via Windows machine
     * <ul>
     * <li>Any any site is created via Alfresco Share</li>
     * <li>Windows explorer is opened</li>
     * <li>Add the url of the webdav server</li>
     * <li>Fill in the user/password (e.g. admin/admin) and press 'OK' button</li>
     * <li>Alfresco WebDAV connection is established</li>
     * <li>The created connection is opened in a new window. The appropriate space are displayed</li>
     * <li>Open webdav folder and navigate through the folders structure</li>
     * </ul>
     *
     * @throws Exception
     */
    @AlfrescoTest(testlink = "AONE-6547")
    @Test(groups = { "WEBDAVWindowsClient", "EnterpriseOnly" }, timeOut = 900000)
    public void AONE_6547() throws Exception
    {
        // IF ERROR CONNECTION. RESTART WEBCLIENT
        // Process restartWebClient = Runtime.getRuntime().exec("net stop webclient");
        // restartWebClient.waitFor();
        // restartWebClient = Runtime.getRuntime().exec("net start webclient");
        // restartWebClient.waitFor();

        // Windows explorer is opened
        explorer.openWindowsExplorer();
        logger.info("Windows explorer is opened");

        // String drive = explorer.mapNetworkDrive(shareUrl, webdavPath, ADMIN_USERNAME, ADMIN_PASSWORD);
        String drive = explorer.mapNetworkDrive(shareUrl, webdavPath, testUser, DEFAULT_PASSWORD);
        explorer.getAbstractUtil().waitForWindow("frmwebdav");

        // The created connection is opened in a new window. The appropriate space are displayed
        String windowName = explorer.getAbstractUtil().getAbsoluteWindowName("frmwebdav");
        Assert.assertTrue(windowName.contains(drive), "The created connection is not opened in a new window");

        // Open webdav folder and navigate through the folders structure
        explorer.openFolder(drive + "\\" + "Sites");
        explorer.activateApplicationWindow("frmSites");

        String[] allObjectsWindow = explorer.getAbstractUtil().getLdtp().getObjectList();

        Assert.assertTrue(Arrays.asList(allObjectsWindow).contains("lst" + siteName.toLowerCase()),
                "Navigation works not correctly. Expected site " + siteName.toLowerCase() + " isn't presented.");

        String uknwebdav = null;
        for (String objectWindow : allObjectsWindow)
        {
            if (objectWindow.toLowerCase().contains("uknwebdav"))
            {
                uknwebdav = objectWindow;
                break;
            }
        }

        // The created connection is displayed in "The Internet" section
        if (uknwebdav != null)
        {
            Assert.assertTrue(uknwebdav.contains(("DavWWWRoot\\alfresco)(") + drive.substring(0, drive.length() - 1)),
                    "The created connection isn't displayed in \"The Internet\" section.");
        }

        logger.info("Close window");

        explorer.closeExplorer();
    }

    @AfterMethod(groups = "teardown", timeOut = 150000)
    public void endTest()
    {
        ShareUser.login(drone, testUser);
        SiteUtil.deleteSite(drone, siteName);
        ShareUser.logout(drone);
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUser.deleteUser(drone, testUser).render();
        ShareUser.logout(drone);
    }

    @AfterClass(groups = "teardown", timeOut = 150000)
    public void tearDownClass()
    {
        try
        {
            removeMappedDrive = Runtime.getRuntime().exec("cmd /c start /WAIT net use * /d /y");
            removeMappedDrive.waitFor();
        }
        catch (IOException | InterruptedException e)
        {
            logger.error("Error occurred during delete mapped drive ", e);
        }

        logger.info("[Suite ] : End Test in: " + "AONE6547RepositoryWebDavTests");
    }

}
