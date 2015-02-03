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
import org.alfresco.po.share.site.contentrule.FolderRulesPage;
import org.alfresco.po.share.site.contentrule.FolderRulesPageWithRules;
import org.alfresco.po.share.site.contentrule.createrules.CreateRulePage;
import org.alfresco.po.share.site.contentrule.createrules.selectors.AbstractIfSelector;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.ActionSelectorEnterpImpl;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.WhenSelectorImpl;
import org.alfresco.po.share.site.document.DetailsPage;
import org.alfresco.po.share.site.document.DocumentAspect;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.SelectAspectsPage;
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

/**
 * @author Sergey Kardash
 */
@Listeners(FailedTestListener.class)
public class AONE6556RepositoryWebDavTests extends AbstractUtils {
    private static Log logger = LogFactory.getLog(AONE6556RepositoryWebDavTests.class);

    String testName = "AONE6556";
    String testUser = testName + getRandomString(5);
    String siteName = "site" + testName + getRandomString(5);

    String fileName = "test" + getRandomString(3);
    String fileNameWithExt = fileName + ".txt";
    String tempFolder = "tempfolder" + getRandomString(3);
    String folderName1 = "folder1" + getRandomString(3);
    String folderName2 = "folder2" + getRandomString(3);

    WindowsExplorer explorer = new WindowsExplorer();
    MicorsoftOffice2010 word = new MicorsoftOffice2010(Application.WORD, "2010");
    String mapConnect;
    String networkDrive;
    String networkPath;
    private static String sitesPath = "\\Sites\\";
    Process removeMappedDrive;

    @Override
    @BeforeClass(groups = "setup", timeOut = 60000)
    public void setup() throws Exception {
        super.setup();

        removeMappedDrive = Runtime.getRuntime().exec("cmd /c start /WAIT net use * /d /y");
        removeMappedDrive.waitFor();

        logger.info("[Suite ] : Start Test in: " + "AONE6556RepositoryWebDavTests");
    }

    @BeforeMethod(groups = "setup", timeOut = 150000)
    public void precondition() throws Exception {
        // Any site is created
        // Create user
        String[] testUserInfo = new String[]{testUser};
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        networkDrive = word.getMapDriver();
        networkPath = word.getMapPath();
        if (networkPath.contains("alfresco\\")) {
            networkPath = networkPath.concat("webdav");
        }

        mapConnect = "cmd /c start /WAIT net use" + " " + networkDrive + " " + networkPath + " " + "/user:" + testUser + " " + DEFAULT_PASSWORD;

        // Runtime.getRuntime().exec(mapConnect);
        Process process = Runtime.getRuntime().exec(mapConnect);
        // waitProcessEnd(process);
        process.waitFor();

        if (CifsUtil.checkDirOrFileExists(10, 200, networkDrive + sitesPath)) {
            logger.info("----------Mapping succesfull " + testUser);
        } else {
            logger.error("----------Mapping was not done " + testUser);
        }

        // Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.openDocumentLibrary(drone).render();

        // At least 2 folders are created within the site
        ShareUserSitePage.createFolder(drone, folderName1, folderName1);
        ShareUserSitePage.createFolder(drone, folderName2, folderName2);

    }

    /**
     * Test: AONE-6556:Verify the rule is applied via WebDAV without errors
     * <ul>
     * <li>Any site is created</li>
     * <li>At least 2 folders are created within the site</li>
     * <li>Map Alfresco Webdav as a network drive</li>
     * <li>Click Manage Rule for one of the folders</li>
     * <li>f1 and create inbound rule performs</li>
     * <li>add taggable aspect</li>
     * <li>move item to another folder, e.g. f2</li>
     * <li>Open folder f1 in WIndows Explorer</li>
     * <li>Drag and drop any file from your computer to the folder f1</li>
     * <li>Open folder f2</li>
     * <li>Log into Share</li>
     * <li>Verify the presence of the file and folder</li>
     * <li>The file and folder are not displayed</li>
     * </ul>
     *
     * @throws Exception
     */
    @AlfrescoTest(testlink = "AONE-6556")
    @Test(groups = {"WEBDAVWindowsClient", "EnterpriseOnly"}, timeOut = 600000)
    public void AONE_6556() throws Exception {
        File folder = new File(DATA_FOLDER + tempFolder);
        boolean folderCreated = folder.mkdir();
        File file = null;
        if (folderCreated) {
            folder.deleteOnExit();
            file = newFile(DATA_FOLDER + tempFolder + SLASH + fileNameWithExt, fileNameWithExt);
            file.deleteOnExit();
        }

        String filePath = null;
        if (file != null) {
            filePath = file.getParent();
        }

        // Alfresco WebDAV connection is established
        String docLib = "\\documentLibrary";

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
        actionSelectorEnterpImpl.selectAddAspect(DocumentAspect.TAGGABLE.getValue());

        // Select 'Move' value in the 'Perform Action' section.
        createRulePage.addOrRemoveOptionsFieldsToBlock(CreateRulePage.Block.ACTION_BLOCK, CreateRulePage.AddRemoveAction.ADD);
        actionSelectorEnterpImpl.selectMove(siteName, "Documents", folderName2);

        // Click "Create" button
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName1), "Rule page with rule isn't correct");

        // Open folder f1 in Windows Explorer
        explorer.openWindowsExplorer();
        String fullPath = networkDrive + sitesPath + siteName.toLowerCase() + docLib + "\\" + folderName1;
        String fullPathNew = networkDrive + sitesPath + siteName.toLowerCase() + docLib + "\\" + folderName2 + "\\" + fileNameWithExt;

        // Drag and drop any file from your computer to the folder f1 (move ope)
        explorer.moveFileToOtherFolder(filePath, fileName, fullPath);
        Assert.assertTrue(explorer.isFilePresent(fullPathNew), "File isn't moved");
        explorer.closeExplorer();

        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone);

        Assert.assertTrue(documentLibraryPage.isFileVisible(folderName1), "Folder " + folderName1 + " isn't visible");
        Assert.assertTrue(documentLibraryPage.isFileVisible(folderName2), "Folder " + folderName2 + " isn't visible");

        // Open folder f2
        documentLibraryPage.selectFolder(folderName2).render();

        Assert.assertTrue(documentLibraryPage.isFileVisible(fileNameWithExt), "File " + fileNameWithExt + " isn't visible");

        // Open folder2 in browser and verify the file is not locked
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileNameWithExt).isLocked(), "File " + fileNameWithExt + " is locked");

        DetailsPage detailsPage = ShareUser.openDocumentDetailPage(drone, fileNameWithExt);

        SelectAspectsPage aspectsPage = detailsPage.selectManageAspects().render();

        // Verify that the taggable aspect is applied
        Assert.assertTrue(aspectsPage.getSelectedAspects().contains(DocumentAspect.TAGGABLE), "'TAGGABLE'' aspect isn't applied");

        ShareUser.logout(drone);
    }

    @AfterMethod(groups = "teardown", timeOut = 150000)
    public void endTest() {
        ShareUser.login(drone, testUser);
        SiteUtil.deleteSite(drone, siteName);
        ShareUser.logout(drone);
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUser.deleteUser(drone, testUser).render();
        ShareUser.logout(drone);
    }

    @AfterClass(groups = "teardown", timeOut = 150000)
    public void tearDownClass() {
        try {
            removeMappedDrive = Runtime.getRuntime().exec("cmd /c start /WAIT net use * /d /y");
            removeMappedDrive.waitFor();
        } catch (IOException | InterruptedException e) {
            logger.error("Error occurred during delete mapped drive ", e);
        }

        logger.info("[Suite ] : End Test in: " + "AONE6556RepositoryWebDavTests");
    }

}
