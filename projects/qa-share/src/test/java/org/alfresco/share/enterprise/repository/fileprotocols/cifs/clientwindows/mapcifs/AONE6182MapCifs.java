/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
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

package org.alfresco.share.enterprise.repository.fileprotocols.cifs.clientwindows.mapcifs;

import com.cobra.ldtp.Ldtp;
import org.alfresco.explorer.WindowsExplorer;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.EditUserPage;
import org.alfresco.po.share.UserProfilePage;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.CifsUtil;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.test.FailedTestListener;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sergey Kardash
 */
@Listeners(FailedTestListener.class)
public class AONE6182MapCifs extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(AONE6182MapCifs.class);

    String testName = "AONE6182";
    String testUser = testName + getRandomString(5);
    WindowsExplorer explorer = new WindowsExplorer();
    String mapConnect;
    private static final String regexUrlIP = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})";
    private static final String regexUrlWithPort = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(:\\d{1,5})?";

    @Override
    @BeforeClass(groups = "setup")
    public void setup() throws Exception
    {
        super.setup();

        String ip = getAddress(networkPath);
        networkPath = networkPath.replaceFirst(regexUrlWithPort, ip);
        if (networkPath.contains("alfresco\\"))
        {
            networkPath = networkPath.replace("alfresco\\", "alfresco");
        }
    }

    /**
     * Precondition Test - AONE-6182:Map CIFS by a disabled user
     * <ul>
     * <li>Create any user, e.g. 'dwrench'</li>
     * <li>Ensure that the user can mount a CIFS drive and log in to Alfresco/Share</li>
     * <li>Create any user, e.g. 'dwrench'</li>
     * </ul>
     */
    @BeforeMethod(groups = "setup")
    public void precondition() throws Exception
    {
        Process process = Runtime.getRuntime().exec("cmd /c start /WAIT net use * /d /y");
        process.waitFor();
        process = Runtime.getRuntime().exec("net stop webclient");
        process.waitFor();
        process = Runtime.getRuntime().exec("net start webclient");
        process.waitFor();

        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        logger.info("Create user: " + testUser);
        ShareUser.logout(drone);

        mapConnect = "cmd /c start /WAIT net use" + " " + networkDrive + " " + networkPath + " " + "/user:" + testUser + " " + DEFAULT_PASSWORD;

        logger.info("Try execute command: " + mapConnect);
        process = Runtime.getRuntime().exec(mapConnect);
        process.waitFor();

        // Ensure that the user can mount a CIFS drive and log in to Alfresco/Share
        String cifsPath = "\\Sites\\";
        if (CifsUtil.checkDirOrFileExists(10, 200, networkDrive + cifsPath))
        {
            logger.info("----------Mapping succesfull " + testUser);
        }
        else
        {
            logger.error("----------Mapping was not done " + testUser);
            Assert.fail("User can't mount a CIFS drive");
        }

        process = Runtime.getRuntime().exec("cmd /c start /WAIT net use * /d /y");
        process.waitFor();

        // Disable the 'dwrench' account through share
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Search for a created user
        DashBoardPage dashBoardPage = drone.getCurrentPage().render();
        UserSearchPage searchPage = dashBoardPage.getNav().getUsersPage().render();
        searchPage = searchPage.searchFor(testUser).render();
        UserProfilePage userProfilePage = searchPage.clickOnUser(testUser).render();
        EditUserPage editUserPage = userProfilePage.selectEditUser().render();

        // Disable user
        editUserPage.selectDisableAccount();

        // Click "Save Changes" button
        editUserPage.saveChanges().render();
        ShareUser.logout(drone);

    }

    /**
     * Test - AONE-6182:Map CIFS by a disabled user
     * <ul>
     * <li>Mount a CIFS drive as user 'dwrench' with 'net use' command</li>
     * <li>The specified network password is not correct</li>
     * <li>Mount a CIFS drive as user 'dwrench' with 'Map Network Drive' Windows option</li>
     * <li>It is possible to mount a CIFS drive, but it is impossible to enter it.</li>
     * </ul>
     */
    @AlfrescoTest(testlink = "AONE-6182")
    @Test(groups = { "CIFSWindowsClient", "EnterpriseOnly" }, timeOut = 600000)
    public void AONE_6182() throws Exception
    {
        Process process = Runtime.getRuntime().exec("cmd /c start /WAIT net use * /d /y");
        process.waitFor();
        process = Runtime.getRuntime().exec("net stop webclient");
        process.waitFor();
        process = Runtime.getRuntime().exec("net start webclient");
        process.waitFor();
        logger.info("Restart webclient service");

        String expectedError = executeCommandGetErrorText("net use " + networkDrive + " " + networkPath + " /user:" + testUser + " " + DEFAULT_PASSWORD);

        logger.info("Expected error: " + expectedError);

        // The specified network password is not correct
        Assert.assertTrue(expectedError.contains("System error 86 has occurred.") || expectedError.contains("System error 1326 has occurred"),
                "Expected error wasn't occurred");
        Assert.assertTrue(expectedError.contains("The specified network password is not correct.") || expectedError.contains("Logon failure"),
                "Expected error wasn't occurred");

        explorer.openWindowsExplorer();
        logger.info("Windows explorer is opened");

        // Mount a CIFS drive as user 'dwrench' with 'Map Network Drive' Windows option
        explorer.activateApplicationWindow("Libraries");
        explorer.openFolderFromCurrent("Computer");
        explorer.activateApplicationWindow("Computer");
        explorer.clickButton("Computer", "Mapnetworkdrive");
        explorer.activateApplicationWindow("Map Network Drive");
        Ldtp newLdtp = new Ldtp("Map Network Drive");
        newLdtp.enterString("txtFolder", networkPath);
        newLdtp.check("chkConnectusingdifferentcredentials");
        newLdtp.click("Finish");
        logger.info("map network driver via windows explorer");
        explorer.activateApplicationWindow("Windows Security");
        Ldtp security = explorer.getAbstractUtil().getLdtp();
        security.waitTime(5);
        explorer.operateOnSecurity(security, testUser, DEFAULT_PASSWORD);
        String windowName = explorer.getAbstractUtil().waitForWindow("dlgWindows Security");
        explorer.activateApplicationWindow(windowName);
        String[] allObjectsWindow = explorer.getAbstractUtil().getLdtp().getObjectList();

        // It is possible to mount a CIFS drive, but it is impossible to enter it.
        Assert.assertTrue(
                Arrays.asList(allObjectsWindow).contains("lblThespecifiednetworkpasswordisnotcorrect")
                        || Arrays.asList(allObjectsWindow).contains("lblLogonfailureunknownusernameorbadpassword"), "Expected error message isn't found");
        explorer.closeExplorer();
        explorer.activateApplicationWindow("Map Network Drive");
        explorer.closeExplorer();
        explorer.activateApplicationWindow("Computer");
        explorer.closeExplorer();
        logger.info("Close windows explorer");
    }

    @AfterMethod(groups = "teardown", timeOut = 150000)
    public void endTest()
    {
        try
        {
            Process removeMappedDrive = Runtime.getRuntime().exec("cmd /c start /WAIT net use * /d /y");
            removeMappedDrive.waitFor();
        }
        catch (IOException | InterruptedException e)
        {
            logger.error("Error occurred during delete mapped drive ", e);
        }

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUser.deleteUser(drone, testUser).render();
    }

    private static String getAddress(String shareUrl)
    {
        Pattern p1 = Pattern.compile(regexUrlIP);
        Matcher m1 = p1.matcher(shareUrl);
        if (m1.find())
        {
            return m1.group();
        }
        throw new PageException("Can't extract address from URL");
    }

    private String executeCommandGetErrorText(String command) throws IOException, InterruptedException
    {
        String line;
        StringBuilder sb = new StringBuilder();
        InputStream stderr;

        // execute command via cmd and grab stderr
        Process process = Runtime.getRuntime().exec(String.format("cmd /c %s", command));
        stderr = process.getErrorStream();
        process.waitFor();

        BufferedReader brCleanUp;

        // clean up if any output in stderr
        brCleanUp = new BufferedReader(new InputStreamReader(stderr));
        while ((line = brCleanUp.readLine()) != null)
        {
            sb.append(line).append("\n");
        }
        brCleanUp.close();
        return sb.toString();
    }

}
