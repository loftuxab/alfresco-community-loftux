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

import org.alfresco.po.alfresco.webdav.WebDavPage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.*;

/**
 * @author Sergey Kardash
 */
@Listeners(FailedTestListener.class)
public class AONE6545RepositoryWebDavTests extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(AONE6545RepositoryWebDavTests.class);
    String testUser = "AONE6545" + getRandomString(5);
    String siteName = "siteAONE6545" + "-" + getRandomString(5);

    @Override
    @BeforeClass(groups = "setup")
    public void setup() throws Exception
    {
        super.setup();

        logger.info("[Suite ] : Start Test in: " + "AONE6545RepositoryWebDavTests");
    }

    @BeforeMethod(groups = "setup", timeOut = 150000)
    public void precondition() throws Exception
    {

        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

    }

    /**
     * Test: AONE-6545:Verify accessing WebDAV in browser
     * <ul>
     * <li>In your browser open the following link http://servername/alfresco/webdav</li>
     * <li>Information is entered successfully and Company home is opened in view mode</li>
     * <li>Navigate through the folder structure</li>
     * </ul>
     *
     * @throws Exception
     */
    @AlfrescoTest(testlink = "AONE-6545")
    @Test(groups = "EnterpriseOnly", timeOut = 300000)
    public void AONE_6545() throws Exception
    {

        // In your browser open the following link http://servername/alfresco/webdav
        WebDavPage webDavPage = ShareUtil.navigateToWebDav(drone, testUser, DEFAULT_PASSWORD).render();

        // Information is entered successfully and Company home is opened in view mode
        Assert.assertTrue(webDavPage.getDirectoryText().equals("Directory listing for /"), "Default directory isn't opened");

        // Navigate through the folder structure
        webDavPage.clickDirectory("Sites");

        Assert.assertTrue(webDavPage.getDirectoryText().equals("Directory listing for /Sites"), "Link 'Sites' isn't opened");

        Assert.assertTrue(webDavPage.checkDirectoryDisplayed(siteName.toLowerCase()), "Expected site " + siteName + " isn't displayed");
        Assert.assertTrue(webDavPage.checkUpToLevelDisplayed(), "'Up a level' link isn't displayed");

        // Navigation works correctly
        webDavPage.clickUpToLevel();

        Assert.assertTrue(webDavPage.getDirectoryText().equals("Directory listing for /"), "Default directory isn't opened");

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

}
