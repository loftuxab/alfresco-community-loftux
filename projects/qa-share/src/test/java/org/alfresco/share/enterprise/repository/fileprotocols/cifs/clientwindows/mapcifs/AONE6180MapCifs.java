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

import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.site.document.ConfirmDeletePage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.CifsUtil;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserRepositoryPage;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.testng.Assert.assertFalse;

/**
 * @author Sergey Kardash
 */

@Listeners(FailedTestListener.class)
public class AONE6180MapCifs extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(AONE6180MapCifs.class);

    String testName = "AONE6180MapCifs";
    String mapConnect;
    private static String cifsPath = "\\Sites\\";
    private static final String regexUrlIP = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})";
    private static final String regexUrlWithPort = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(:\\d{1,5})?";
    String filename = "x1234567.001.600";
    String filenameWithExt = filename + ".txt";
    File file1;

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

        logger.info("Starting Tests: " + testName);
    }

    /**
     * Precondition Test - AONE-6180:CIFS wildcard pattern on Windows
     * <ul>
     * <li>Mount Alfresco over CIFS onto a Windows 7 client (mapped as a drive, eg. Z:)</li>
     * </ul>
     */
    @BeforeMethod(groups = "setup")
    public void precondition() throws Exception
    {
        Process process = Runtime.getRuntime().exec("cmd /c start /WAIT net use * /d /y");
        process.waitFor();

        mapConnect = "cmd /c start /WAIT net use" + " " + networkDrive + " " + networkPath + " " + "/user:" + ADMIN_USERNAME + " " + ADMIN_PASSWORD;

        logger.info("Try execute command: " + mapConnect);
        process = Runtime.getRuntime().exec(mapConnect);
        process.waitFor();
    }

    /**
     * Test - AONE-6180:CIFS wildcard pattern on Windows
     * <ul>
     * <li>Upload a file x1234567.001.600.txt to Company Home/li>
     * <li>On Win7, start the DOS command line tool, (Z:)</li>
     * <li>Alfresco Desktop Action window is displayed "Run check in/out Action"</li>
     * <li>Type dir *.*.600.txt</li>
     * <li>Windows is able to find a file</li>
     * </ul>
     */
    @AlfrescoTest(testlink = "AONE-6180")
    @Test(groups = { "CIFSWindowsClient", "EnterpriseOnly" }, timeOut = 600000)
    public void AONE_6180() throws Exception
    {

        file1 = newFile(filenameWithExt, filename);
        file1.deleteOnExit();
        if (!CifsUtil.checkDirOrFileExists(10, 200, networkDrive + cifsPath))
        {
            Assert.fail("Mount Alfresco over CIFS onto a Windows 7 client failed");
        }

        // Login
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Upload a file x1234567.001.600.txt to Company Home
        ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        RepositoryPage repositoryPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO);

        if (!repositoryPage.isFileVisible(filenameWithExt))
        {
            ShareUserRepositoryPage.uploadFileInRepository(drone, file1);
        }
        ShareUser.logout(drone);

        // On Win7, start the DOS command line tool, (Z:)
        // Type dir *.*.600.txt
        List<String> list = executeCommandGetConsoleText("cmd /c \"" + networkDrive + " && dir *.*.600.txt\"");

        // Windows is able to find a file
        Assert.assertTrue(checkString(list, filename), "Expected file isn't found");
        Assert.assertTrue(checkString(list, "1 File(s)"), "Expected file isn't found or was found not one file");
        Assert.assertTrue(checkString(list, "0 Dir(s)"), "Expected file isn't found or was found folder with the same name");

    }

    @AfterMethod(groups = "teardown")
    public void endTest() throws Exception
    {

        Runtime.getRuntime().exec("cmd /c start /WAIT net use * /d /y");
        if (CifsUtil.checkDirOrFileNotExists(7, 200, networkDrive + cifsPath))
        {
            logger.info("--------Unmapping succesfull " + ADMIN_USERNAME);
        }
        else
        {
            logger.error("--------Unmapping was not done correctly " + ADMIN_USERNAME);
        }

        // Login
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Upload a file x1234567.001.600.txt to Company Home
        ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        RepositoryPage repositoryPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO);

        if (repositoryPage.isFileVisible(filenameWithExt))
        {
            FileDirectoryInfo deleteFileInfo = repositoryPage.getFileDirectoryInfo(filenameWithExt);
            deleteFileInfo.selectCheckbox();
            ConfirmDeletePage deleteConf = repositoryPage.getNavigation().selectDelete().render();
            repositoryPage = deleteConf.selectAction(ConfirmDeletePage.Action.Delete).render();
        }
        assertFalse(repositoryPage.isFileVisible(filenameWithExt), "File isn't deleted after test");

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

    private boolean checkString(List<String> list, String searchString)
    {
        for (String str : list)
        {
            if (str.trim().contains(searchString))
            {
                logger.info("String: " + str + " contains string " + searchString);
                return true;
            }
        }
        return false;
    }

    private List<String> executeCommandGetConsoleText(String command) throws IOException
    {
        List<String> list = new ArrayList<>();
        BufferedReader in = null;
        try
        {
            Process p = Runtime.getRuntime().exec(command);
            InputStream s = p.getInputStream();
            in = new BufferedReader(new InputStreamReader(s));
            String temp;
            while ((temp = in.readLine()) != null)
            {
                list.add(temp);
            }
        }
        catch (Exception e)
        {
            logger.error("Error during attempt getting message from cmd console: ", e);
        }
        finally
        {
            if (in != null)
                in.close();
        }
        return list;
    }

}
