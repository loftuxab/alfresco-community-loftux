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
import org.alfresco.application.windows.NotepadApplications;
import org.alfresco.explorer.MoveAndCopyActions;
import org.alfresco.explorer.WindowsExplorer;
import org.alfresco.po.share.site.contentrule.FolderRulesPage;
import org.alfresco.po.share.site.contentrule.FolderRulesPageWithRules;
import org.alfresco.po.share.site.contentrule.createrules.CreateRulePage;
import org.alfresco.po.share.site.contentrule.createrules.selectors.AbstractIfSelector;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.ActionSelectorEnterpImpl;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.WhenSelectorImpl;
import org.alfresco.po.share.site.document.*;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.test.FailedTestListener;
import org.alfresco.utilities.Application;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;

import static org.testng.Assert.assertTrue;

/**
 * @author Sergey Kardash
 */
@Listeners(FailedTestListener.class)
public class AONE6557RepositoryWebDavTests extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(AONE6557RepositoryWebDavTests.class);

    String testName = "AONE6557";
    String testUser = testName + getRandomString(5);
    String siteName = "site" + testName + getRandomString(5);

    String fileName1 = "test1" + getRandomString(3);
    String fileName1WithExt = fileName1 + ".txt";
    String fileName2 = "test2" + getRandomString(3);
    String fileName2WithExt = fileName2 + ".txt";
    String tempFolder = "tempfolder" + getRandomString(3);
    String folderName1 = "folder1" + getRandomString(3);

    WindowsExplorer explorer = new WindowsExplorer();
    MicorsoftOffice2010 word = new MicorsoftOffice2010(Application.WORD, "2010");
    NotepadApplications notePad = new NotepadApplications();

    String mapConnect;
    String networkDrive;
    String networkPath;
    private static String sitesPath = "\\Sites\\";
    Process removeMappedDrive;

    @Override
    @BeforeClass(groups = "setup", timeOut = 60000)
    public void setup() throws Exception
    {
        super.setup();

        removeMappedDrive = Runtime.getRuntime().exec("cmd /c start /WAIT net use * /d /y");
        removeMappedDrive.waitFor();

        logger.info("[Suite ] : Start Test in: " + "AONE6557RepositoryWebDavTests");
    }

    @BeforeMethod(groups = "setup", timeOut = 150000)
    public void precondition() throws Exception
    {
        // Any site is created
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

        ShareUser.openDocumentLibrary(drone).render();

        // Any folder is created in the DocLib of the site
        ShareUserSitePage.createFolder(drone, folderName1, folderName1);

        // create the rule for folder
        FolderRulesPage folderRulesPage = ShareUserSitePage.getFileDirectoryInfo(drone, folderName1).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(folderName1), "Rule page isn't correct");

        // Fill "Name" field with correct data
        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField("Rule Name");
        createRulePage.fillDescriptionField("Rule Description");

        // Select several values in the When section, e.g.
        // 'Items are created or enter this folder'
        WhenSelectorImpl whenSelectorIml = createRulePage.getWhenOptionObj();
        whenSelectorIml.selectInbound();

        // Select 'All items' from "If" drop-down select control
        AbstractIfSelector ifSelector = createRulePage.getIfOptionObj();
        ifSelector.selectIFOption(0);

        // Select 'add taggable aspect' value in the 'Perform Action' section.
        ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectAddAspect(DocumentAspect.VERSIONABLE.getValue());

        // Click "Create" button
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName1), "Rule page with rule isn't correct");

    }

    /**
     * Test: AONE-6557:Checking version history when saving document via WebDav
     * <ul>
     * <li>Should be tested on Mac OS and Windows 7.</li>
     * <li>Any site is created</li>
     * <li>Any folder is created in the DocLib of the site</li>
     * <li>Version aspect is added to all documents put in this folder</li>
     * <li>Any document is uploaded via Share to this folder</li>
     * <li>Alfresco webdav is mounted</li>
     * <li>Create a new *.docx ot *.txt document on client machine</li>
     * <li>Enter any text and save the *.docx ot *.txt document to the folder created in preconditions (step 2) via webdav</li>
     * <li>Check a version of saved documents in Share</li>
     * <li>Edit the *.docx ot *.txt document via WebDav. Version is incremented</li>
     * <li>Repeat several times</li>
     * <li>Drag and Drop any file from your desktop to the space created in preconditions (step 2) via WebDav</li>
     * <li>Check a version of saved documents in Alfresco</li>
     * <li>Drag and Drop any file from your desktop to the space</li>
     * <li>Verify the presence of the file and folder</li>
     * <li>The file and folder are not displayed</li>
     * </ul>
     *
     * @throws Exception
     */
    @AlfrescoTest(testlink = "AONE-6557")
    @Test(groups = { "WEBDAVWindowsClient", "EnterpriseOnly" }, timeOut = 900000)
    public void AONE_6557() throws Exception
    {

        // Alfresco WebDAV connection is established
        String docLib = "\\documentLibrary";

        File folder = new File(DATA_FOLDER + tempFolder);
        boolean folderCreated = folder.mkdir();
        File file = null;
        if (folderCreated)
        {
            folder.deleteOnExit();
            file = newFile(DATA_FOLDER + tempFolder + SLASH + fileName2WithExt, fileName2WithExt);
            file.deleteOnExit();
        }

        String filePath = null;
        if (file != null)
        {
            filePath = file.getParent();
        }

        String fullPath = networkDrive + sitesPath + siteName.toLowerCase() + docLib + "\\" + folderName1;
        // Create a new *.docx ot *.txt document on client machine
        notePad.openNotepadApplication();
        notePad.setNotepadWindow("Notepad");
        notePad.saveAsNotpad(fullPath, fileName1);
        // Enter any text and save the *.docx ot *.txt document to the folder created in preconditions (step 2) via webdav
        notePad.getAbstractUtil().waitForWindow(fileName1);
        notePad.editNotepad("first create in client", fileName1);
        notePad.ctrlSSave();
        notePad.getAbstractUtil().getLdtp().waitTime(10);

        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();
        documentLibraryPage.selectFolder(folderName1);
        DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName1WithExt).render();
        // Check a version of saved documents in Share
        assertTrue(detailsPage.getDocumentVersion().equals("1.0"), "Version '1.0' for file " + fileName1WithExt + " isn't presented");

        // Edit the *.docx ot *.txt document via WebDav
        notePad.appendTextToNotepad(" adding abc", fileName1);
        notePad.ctrlSSave();
        notePad.getAbstractUtil().getLdtp().waitTime(10);

        // Check the version in Share
        documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();
        documentLibraryPage.selectFolder(folderName1);
        detailsPage = documentLibraryPage.selectFile(fileName1WithExt).render();
        assertTrue(detailsPage.getDocumentVersion().equals("1.1"), "Version '1.1' for file " + fileName1WithExt + " isn't presented");

        // Repeat several times
        notePad.appendTextToNotepad(" adding abc", fileName1);
        notePad.ctrlSSave();
        notePad.getAbstractUtil().getLdtp().waitTime(10);

        documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();
        documentLibraryPage.selectFolder(folderName1);
        detailsPage = documentLibraryPage.selectFile(fileName1WithExt).render();
        assertTrue(detailsPage.getDocumentVersion().equals("1.2"), "Version '1.2' for file " + fileName1WithExt + " isn't presented");

        notePad.closeNotepad(fileName1);
        explorer.openWindowsExplorer();

        // Drag and Drop any file from your desktop to the space created in preconditions (step 2) via WebDav
        explorer.copyFileToOtherFolder(filePath, fileName2, fullPath);
        Assert.assertTrue(explorer.isFilePresent(fullPath + SLASH + fileName2WithExt), "File " + fileName2WithExt + " isn't moved");

        documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();
        documentLibraryPage.selectFolder(folderName1);
        detailsPage = documentLibraryPage.selectFile(fileName2WithExt).render();
        // Check a version of saved documents in Alfresco
        assertTrue(detailsPage.getDocumentVersion().equals("1.0"), "Version '1.0' for file " + fileName2WithExt + " isn't presented");

        // Drag and Drop any file from your desktop to the space
        explorer.copyFileToOtherFolder(filePath, fileName2, fullPath, MoveAndCopyActions.COPY_AND_REPLACE);

        // Open document's details page via browser and verify version history for the rewrited document
        documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();
        documentLibraryPage.selectFolder(folderName1);
        detailsPage = documentLibraryPage.selectFile(fileName2WithExt).render();
        assertTrue(detailsPage.getDocumentVersion().equals("1.1"), "Version '1.1' for file " + fileName2WithExt + " isn't presented");

        // Drag and Drop any file from your desktop to the space
        explorer.copyFileToOtherFolder(filePath, fileName2, fullPath, MoveAndCopyActions.COPY_AND_REPLACE);

        // Open document's details page via browser and verify version history for the rewrited document
        documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();
        documentLibraryPage.selectFolder(folderName1);
        detailsPage = documentLibraryPage.selectFile(fileName2WithExt).render();
        assertTrue(detailsPage.getDocumentVersion().equals("1.2"), "Version '1.2' for file " + fileName2WithExt + " isn't presented");

        explorer.closeExplorer();

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

        logger.info("[Suite ] : End Test in: " + "AONE6557RepositoryWebDavTests");
    }

}
