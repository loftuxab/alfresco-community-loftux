/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
 */

package org.alfresco.share.repository.subsystems;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.PeopleFinderPage;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.UserProfilePage;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.systemsummary.directorymanagement.DirectoryInfoRow;
import org.alfresco.po.share.systemsummary.directorymanagement.DirectoryManagementPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.JmxUtils;
import org.alfresco.share.util.MailUtil;
import org.alfresco.share.util.RandomUtil;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.SystemSummaryAdminUtil;
import org.alfresco.test.FailedTestListener;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Created by Olga Lokhach
 */

@Listeners(FailedTestListener.class)
public class SynchronizationSubsystemTests extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(SynchronizationSubsystemTests.class);
    private static String syncObject = "Alfresco:Type=Configuration,Category=Synchronization,id1=default";
    private static String syncAutoCreate = "synchronization.autoCreatePeopleOnLogin";
    private static String syncWhenMissing = "synchronization.syncWhenMissingPeopleLogIn";
    private static String syncOnStartup = "synchronization.syncOnStartup";
    private static String[] users;
    private String testUser1;
    private String testUser2;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
        testUser1 = MailUtil.BOT_MAIL_2.split("[@]+")[0];
        testUser2 = MailUtil.BOT_MAIL_3.split("[@]+")[0];
        users = new String [] {testUser1, testUser2};

        try
        {
            // synchronization.autoCreatePeopleOnLogin is enabled
            JmxUtils.setAlfrescoServerProperty(shareUrl, syncObject, syncAutoCreate, true);

            // synchronization.syncWhenMissingPeopleLogIn is disable
            JmxUtils.setAlfrescoServerProperty(shareUrl, syncObject, syncWhenMissing, false);

            // synchronization.synchronization.syncOnStartup is disable
            JmxUtils.setAlfrescoServerProperty(shareUrl, syncObject, syncOnStartup, false);

            // Click operation "Start"
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, syncObject, "stop");
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, syncObject, "start");

            // Delete testUser1 and testUser2 from Alfresco
            ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
            DashBoardPage dashBoardPage = drone.getCurrentPage().render();
            UserSearchPage userSearchPage = dashBoardPage.getNav().getUsersPage().render();

            for (String user : users)
            {
                UserSearchPage results = userSearchPage.searchFor(user).render();
                if (results.hasResults())
                {
                    UserProfilePage userProfile = results.clickOnUser(user).render();
                    userProfile.deleteUser().render();
                }
            }
        }
        catch (Exception e)
        {
            throw new SkipException("Skipping as pre-condition step(s) fail: " + e);
        }

    }

    /**
     * AONE-7289:Disabling "Create People On Login" option
     */

    @Test(groups = "EnterpriseOnly")
    public void AONE_7289() throws Exception
    {
        String password = MailUtil.PASSWORD_OUTBOUND_ALFRESCO;
        String authChainName = RandomUtil.getRandomString(5);

        try
        {
            DirectoryManagementPage directoryManagementPage = SystemSummaryAdminUtil.addOpenLdapAuthChain(drone, authChainName);
            DirectoryInfoRow directoryInfoRow = directoryManagementPage.getDirectoryInfoRowBy(authChainName);
            assertEquals(directoryInfoRow.getType(), "OpenLDAP");

            // Set synchronization.autoCreatePeopleOnLogin='false' via JMX;
            JmxUtils.setAlfrescoServerProperty(shareUrl, syncObject, syncAutoCreate, false);

            // Click operation "Start"
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, syncObject, "stop");
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, syncObject, "start");

            // Verify can't login testUser1 who does not yet exist in Alfresco;
            ShareUser.deleteSiteCookies(drone,shareUrl);
            SharePage resultPage = login(drone, testUser1, password).render();
            assertFalse(resultPage.isLoggedIn(), testUser1 + " can login");

        }
        finally
        {
            // Set default property "synchronization.autoCreatePeopleOnLogin"
            JmxUtils.setAlfrescoServerProperty(shareUrl, syncObject, syncAutoCreate, true);

            // Set synchronization.syncWhenMissingPeopleLogIn is disable
            JmxUtils.setAlfrescoServerProperty(shareUrl, syncObject, syncWhenMissing, true);

            // Click operation "Start"
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, syncObject, "stop");
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, syncObject, "start");

            // Delete OpenLDAP chain
            SystemSummaryAdminUtil.deleteAuthChain(drone, authChainName);
        }
    }

    /**
     * AONE-7290:Disabling "trigger a differential sync when missing people log in" option
     */

    @Test(groups = "EnterpriseOnly")
    public void AONE_7290() throws Exception
    {
        String password = MailUtil.PASSWORD_OUTBOUND_ALFRESCO;
        String authChainName = RandomUtil.getRandomString(5);

        try
        {
            DirectoryManagementPage directoryManagementPage = SystemSummaryAdminUtil.addOpenLdapAuthChain(drone, authChainName);
            DirectoryInfoRow directoryInfoRow = directoryManagementPage.getDirectoryInfoRowBy(authChainName);
            assertEquals(directoryInfoRow.getType(), "OpenLDAP");

            // Set synchronization.syncWhenMissingPeopleLogIn='false' via JMX;
            JmxUtils.setAlfrescoServerProperty(shareUrl, syncObject, syncWhenMissing, false);

            // Click operation "Start"
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, syncObject, "stop");
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, syncObject, "start");

            // Try to log in to Share as testUser1 - OK;
            SharePage sharePage = ShareUser.login(drone, testUser1, password);
            assertTrue(sharePage.isLoggedIn(), testUser1 + " can't login");

            // Verify sync is not run - Can't found testUser2 who does not yet exist in Alfresco
            DashBoardPage dashBoard = ShareUser.openUserDashboard(drone).render();
            PeopleFinderPage peopleFinderPage = dashBoard.getNav().selectPeople().render();
            peopleFinderPage = peopleFinderPage.searchFor(testUser2).render();
            List<ShareLink> names = peopleFinderPage.getResults();
            assertTrue(names.size() == 0, testUser2 + "is found");

        }
        finally
        {
            // Set default property "synchronization.autoCreatePeopleOnLogin"
            JmxUtils.setAlfrescoServerProperty(shareUrl, syncObject, syncWhenMissing, true);

            // Click operation "Start"
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, syncObject, "stop");
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, syncObject, "start");

            // Delete OpenLDAP chain
            SystemSummaryAdminUtil.deleteAuthChain(drone, authChainName);
        }

    }

    private SharePage login(WebDrone drone, String userName, String userPassword)
    {
        SharePage resultPage;

        try
        {
            resultPage = ShareUser.login(drone, userName, userPassword);
        }
        catch (SkipException se)
        {
            resultPage = ShareUser.getSharePage(drone);
        }
        return resultPage;
    }

}
