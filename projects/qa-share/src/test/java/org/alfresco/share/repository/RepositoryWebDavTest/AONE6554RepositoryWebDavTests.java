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

import com.cobra.ldtp.Ldtp;
import org.alfresco.explorer.WindowsExplorer;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.CifsUtil;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.test.FailedTestListener;
import org.alfresco.utilities.Application;
import org.alfresco.utilities.LdtpUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
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
public class AONE6554RepositoryWebDavTests extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(AONE6554RepositoryWebDavTests.class);

    String testName = "AONE6554";
    String testUser = testName + getRandomString(5);
    String siteName = "site" + testName + getRandomString(5);
    String fileName = ".test" + getRandomString(3);
    String fileNameCheck = "lst" + fileName.replaceAll("\\W", "");
    String folderName = ".folder1 " + getRandomString(3);
    String folderNameCheck = "lst" + folderName.replaceAll("\\W", "");

    WindowsExplorer explorer = new WindowsExplorer();
    String mapConnect;
    private static String sitesPath = "\\Sites\\";
    LdtpUtil ldtpUtil = new LdtpUtil();
    Process removeMappedDrive;

    @Override
    @BeforeClass(groups = "setup", timeOut = 60000)
    public void setup() throws Exception
    {
        super.setup();
        removeMappedDrive = Runtime.getRuntime().exec("cmd /c start /WAIT net use * /d /y");
        removeMappedDrive.waitFor();
        Process process = Runtime.getRuntime().exec("net stop webclient");
        process.waitFor();
        process = Runtime.getRuntime().exec("net start webclient");
        process.waitFor();
    }

    @BeforeMethod(groups = "setup", timeOut = 150000)
    public void precondition() throws Exception
    {

        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.logout(drone);

        String networkPathNew = networkPath.concat("webdav");

        mapConnect = "cmd /c start /WAIT net use" + " " + networkDrive + " " + networkPathNew + " " + "/user:" + testUser + " " + DEFAULT_PASSWORD;

        // Runtime.getRuntime().exec(mapConnect);
        Process process = Runtime.getRuntime().exec(mapConnect);
        // waitProcessEnd(process);
        process.waitFor();

        if (CifsUtil.checkDirOrFileExists(10, 200, networkDrive + sitesPath))
        {
            logger.info("----------Mapping succesfull " + testUser);
        }
        else
        {
            logger.error("----------Mapping was not done " + testUser);
            Assert.fail("Mapping was not done " + testUser);
        }

        // Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

    }

    /**
     * Test: AONE-6554:Hidden files
     * <ul>
     * <li>Map Alfresco Webdav as a network drive</li>
     * <li>Create a folder with the name started with dot (e.g. '.folder new')</li>
     * <li>Create a file with the name started with dot (e.g. '.test.txt')</li>
     * <li>Log into Share</li>
     * <li>Verify the presence of the file and folder</li>
     * <li>The file and folder are not displayed</li>
     * </ul>
     *
     * @throws Exception
     */
    @AlfrescoTest(testlink = "AONE-6554")
    @Test(groups = { "WEBDAVWindowsClient", "EnterpriseOnly" }, timeOut = 600000)
    public void AONE_6554() throws Exception
    {

        if (!CifsUtil.checkDirOrFileExists(10, 200, networkDrive + sitesPath))
        {
            Assert.fail("Mapping was not done " + testUser);
        }
        // Alfresco WebDAV connection is established
        String docLib = "\\documentLibrary";
        // Alfresco WebDAV connection is established
        String fullPath = networkDrive + sitesPath + siteName.toLowerCase() + docLib + "\\";

        explorer.openWindowsExplorer();
        explorer.openFolder(fullPath);

        // The space / the site's doclib is opened via WebDAV
        String windowName = ldtpUtil.findWindowName("documentLibrary");
        explorer.activateApplicationWindow(windowName);

        // Create a folder with the name started with dot (e.g. '.folder new')
        explorer.createNewFolderMenu(folderName);

        // Create a file with the name started with dot (e.g. '.test.txt')
        explorer.rightClickCreate("documentLibrary", fileName, Application.TEXTFILE);

        explorer.activateApplicationWindow(windowName);

        Ldtp newLdtp = new Ldtp(windowName);
        String[] objects = newLdtp.getObjectList();
        Assert.assertTrue(Arrays.asList(objects).contains(fileNameCheck), "Expected file '" + fileName + "' isn't displayed");
        Assert.assertTrue(Arrays.asList(objects).contains(folderNameCheck), "Expected folder '" + folderName + "' isn't displayed");
        logger.info("Close window");
        explorer.closeExplorer();

        // Log into Share
        ShareUser.login(drone, testUser);
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        int fileCount = docLibPage.getFiles().size();

        ShareUser.logout(drone);

        // The file and folder are not displayed
        Assert.assertTrue(fileCount == 0, "Some file isn't hidden in Document Library. MNT-13125 and MNT-8116");

    }

    @AfterMethod(groups = "teardown", timeOut = 150000)
    public void endTest()
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

        ShareUser.login(drone, testUser);
        SiteUtil.deleteSite(drone, siteName);
        ShareUser.logout(drone);
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUser.deleteUser(drone, testUser).render();
    }

}
