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

import org.alfresco.application.windows.MicorsoftOffice2010;
import org.alfresco.explorer.WindowsExplorer;
import org.alfresco.po.share.site.document.*;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.test.FailedTestListener;
import org.alfresco.utilities.Application;
import org.alfresco.utilities.LdtpUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;
import java.util.Map;

/**
 * @author Sergey Kardash
 */
@Listeners(FailedTestListener.class)
public class AONE6544RepositoryWebDavTests extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(AONE6544RepositoryWebDavTests.class);

    String testName = "AONE6544";
    String testUser = testName + getRandomString(5);
    String siteName = "site" + testName + getRandomString(5);
    String fileName = getFileName(testName + "-1") + getRandomString(3);
    String fileTitle = getFileName(testName + "-1") + "_title";
    String fileDesc = getFileName(testName + "-1") + "_description";
    String folderName = "folder1" + getRandomString(3);
    String folderName2 = "folder2" + getRandomString(3);
    String folderTitle = getFolderName(testName) + "_title";
    String folderDesc = getFolderName(testName) + "_description";

    WindowsExplorer explorer = new WindowsExplorer();
    MicorsoftOffice2010 word = new MicorsoftOffice2010(Application.WORD, "2010");
    String mapConnect;
    String networkDrive;
    String networkPath;
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

        logger.info("[Suite ] : Start Test in: " + "AONE6544RepositoryWebDavTests");
    }

    @BeforeMethod(groups = "setup", timeOut = 150000)
    public void precondition() throws Exception
    {

        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        networkDrive = word.getMapDriver();
        networkPath = word.getMapPath();
        if (networkPath.contains("alfresco\\"))
        {
            networkPath = networkPath.concat("webdav");
        }

        mapConnect = "cmd /c start /WAIT net use" + " " + networkDrive + " " + networkPath + " " + "/user:" + testUser + " " + DEFAULT_PASSWORD;

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
        }

        // Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // Any folder is created, some items are added to folder
        ShareUserSitePage.createFolder(drone, folderName, folderTitle, folderDesc).render();
        // Any folder is created in the space/site's doclib
        ShareUserSitePage.createFolder(drone, folderName2, folderName2, folderName2).render();

        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibrary(drone);

        docLibPage.selectFolder(folderName).render();

        // Any content is created/uploaded to the space/site
        ContentDetails contentDetails = new ContentDetails(fileName, fileTitle, fileDesc, fileName);
        contentDetails.setName(fileName);
        // Any metadata is specified for the folder and for the content (title, description)
        ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.PLAINTEXT, docLibPage);

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");
        docLibPage.selectFolder(folderName).render();

        Assert.assertTrue(docLibPage.isFileVisible(fileName), "File " + fileName + " isn't visible");
    }

    /**
     * Test: AONE-6544:Copying content/folder
     * <ul>
     * <li>Any any site is created via Alfresco Share</li>
     * <li>Any folder is created in the space/site's doclib</li>
     * <li>Any content is created/uploaded to the space/site</li>
     * <li>Any metadata is specified for the folder and for the content (title, description)</li>
     * <li>Alfresco WebDAV connection is established</li>
     * <li>The space / the site's doclib is opened via WebDAV</li>
     * <li>Copy the folder and the content to any other space</li>
     * <li>Verify the original folder and content</li>
     * <li>Verify the folder and content copies</li>
     * </ul>
     *
     * @throws Exception
     */
    @AlfrescoTest(testlink = "AONE-6544")
    @Test(groups = { "WEBDAVWindowsClient", "EnterpriseOnly" }, timeOut = 600000)
    public void AONE_6544() throws Exception
    {

        // Alfresco WebDAV connection is established
        String docLib = "\\documentLibrary";
        String fullPath = networkDrive + sitesPath + siteName.toLowerCase() + docLib + "\\";

        explorer.openWindowsExplorer();
        explorer.openFolder(fullPath);

        // The space / the site's doclib is opened via WebDAV
        String windowName = ldtpUtil.findWindowName("documentLibrary");
        explorer.activateApplicationWindow(windowName);

        // Copy the folder and the content to any other space
        explorer.copyFolderInCurrent(folderName, "documentLibrary", folderName2);
        explorer.activateApplicationWindow(folderName2);
        logger.info("Close window");
        Assert.assertTrue(explorer.getAbstractUtil().getLdtp().getWindowName().contains(folderName2.toLowerCase()), "Expected folder isn't opened 'explorer'");
        explorer.closeExplorer();

        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibrary(drone);

        // Verify the original folder and content
        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        FolderDetailsPage folderDetailsPage = docLibPage.getFileDirectoryInfo(folderName).selectViewFolderDetails().render();

        Map<String, Object> properties = folderDetailsPage.getProperties();
        Assert.assertNotNull(properties);
        Assert.assertEquals(properties.get("Name"), folderName, "Name Property is not equals with folder " + folderName + ".");
        Assert.assertEquals(properties.get("Title"), folderTitle, "Title Property is not present");
        Assert.assertEquals(properties.get("Description"), folderDesc, "Description Property is not present");

        docLibPage = ShareUser.openDocumentLibrary(drone);

        docLibPage.selectFolder(folderName).render();
        Assert.assertTrue(docLibPage.isFileVisible(fileName), "File " + fileName + " isn't visible");

        DocumentDetailsPage detailsPage = docLibPage.selectFile(fileName).render();

        properties = detailsPage.getProperties();
        Assert.assertNotNull(properties);
        Assert.assertEquals(properties.get("Name"), fileName, "Name Property is not equals with file " + fileName + ".");
        Assert.assertEquals(properties.get("Title"), fileTitle, "Title Property is not present");
        Assert.assertEquals(properties.get("Description"), fileDesc, "Description Property is not present");

        // Copies are created successfully. Alfresco-stored metadata is not carried over

        docLibPage = ShareUser.openDocumentLibrary(drone);

        docLibPage.selectFolder(folderName2).render();
        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        folderDetailsPage = docLibPage.getFileDirectoryInfo(folderName).selectViewFolderDetails().render();

        properties = folderDetailsPage.getProperties();
        Assert.assertNotNull(properties);
        Assert.assertEquals(properties.get("Name"), folderName, "Name Property is not equals with folder " + folderName + ".");
        Assert.assertEquals(properties.get("Title"), "(None)", "Title Property is present");
        Assert.assertEquals(properties.get("Description"), "(None)", "Description Property is present");

        docLibPage = ShareUser.openDocumentLibrary(drone);

        docLibPage = docLibPage.selectFolder(folderName2).render();
        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        docLibPage = docLibPage.selectFolder(folderName).render();
        Assert.assertTrue(docLibPage.isFileVisible(fileName), "File " + fileName + " isn't visible");

        detailsPage = docLibPage.selectFile(fileName).render();

        properties = detailsPage.getProperties();
        Assert.assertNotNull(properties);
        Assert.assertEquals(properties.get("Name"), fileName, "Name Property is not equals with file " + fileName + ".");
        Assert.assertEquals(properties.get("Title"), "(None)", "Title Property is present");
        Assert.assertEquals(properties.get("Description"), "(None)", "Description Property is present");

        ShareUser.logout(drone);
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

        logger.info("[Suite ] : End Test in: " + "AONE6544RepositoryWebDavTests");
    }

}
