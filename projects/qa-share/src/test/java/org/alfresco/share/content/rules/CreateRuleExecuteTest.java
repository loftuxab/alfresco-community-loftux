package org.alfresco.share.content.rules;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.site.contentrule.FolderRulesPage;
import org.alfresco.po.share.site.contentrule.FolderRulesPageWithRules;
import org.alfresco.po.share.site.contentrule.createrules.CreateRulePage;
import org.alfresco.po.share.site.contentrule.createrules.selectors.AbstractIfSelector;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.ActionSelectorEnterpImpl;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.WhenSelectorImpl;
import org.alfresco.po.share.site.document.*;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import static org.testng.Assert.assertEquals;

/**
 * @author Sergey Kardash
 */
@Listeners(FailedTestListener.class)
public class CreateRuleExecuteTest extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(CreateRuleExecuteTest.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        logger.info("[Suite ] : Start Tests in: " + testName);
    }

    /**
     * Test - AONE-14880:Execute rule to set property values in DocLib
     * <ul>
     * <li>Any site is created</li>
     * <li>Any folder is created</li>
     * <li>Any "Set property value" rule is created for the folder</li>
     * <li>Execute the rule</li>
     * <li>Upload the file to the folder (in case of inbound rule)</li>
     * <li>Verify the property value is set according to the rule</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 400000)
    public void AONE_14880() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String folderName = getFolderName(testName);
        String siteName = getSiteName(testName) + getRandomString(5);
        String ruleName = "Rule Name";
        String allFolder = "All";
        String value = "cm:description";
        String toText = testName + "_text";
        String fileName1 = "First_" + getFileName(testName) + ".txt";

        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        ShareUser.logout(drone);

        // Login as user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Create Folder
        ShareUserSitePage.createFolder(drone, folderName, null);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Create the rule for folder
        FolderRulesPage folderRulesPage = docLibPage.getFileDirectoryInfo(folderName).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(folderName), "Rule page isn't correct");

        // Fill "Name" field with correct data
        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField(ruleName);

        // Select any type, e.g. "Items are created or enter this folder" value from "When" drop-down
        WhenSelectorImpl whenSelectorIml = createRulePage.getWhenOptionObj();
        whenSelectorIml.selectInbound();

        // Select 'All items' from "If" drop-down select control
        AbstractIfSelector ifSelector = createRulePage.getIfOptionObj();
        ifSelector.selectIFOption(0);

        // Select "Set property value" from "Perform Action" drop-down select
        ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectSetPropertyValue(allFolder, value);

        // Select any property and click "Create" button
        createRulePage.fillSetValueField(toText);
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

        // Login
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        // Upload any files
        String[] fileInfo = { fileName1, folderName };
        ShareUser.uploadFileInFolder(drone, fileInfo);

        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");

        Assert.assertEquals(docLibPage.getFileDirectoryInfo(fileName1).getDescription(), toText, "Expected description for file " + fileName1
                + " isn't displayed. The property value isn't set according to the rule");
    }

    /**
     * Test - AONE-14881:Execute rule to set property values in My Files
     * <ul>
     * <li>Any folder is created in My Files</li>
     * <li>Any "Set property value" rule is created for the folder</li>
     * <li>Execute the rule</li>
     * <li>Upload the file to the folder (in case of inbound rule)</li>
     * <li>Verify the property value is set according to the rule</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 400000)
    public void AONE_14881() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String folderName = getFolderName(testName) + getRandomString(5);
        String ruleName = "Rule Name";
        String allFolder = "All";
        String value = "cm:description";
        String toText = testName + "_text";
        String fileName1 = "First_" + getFileName(testName) + ".txt";

        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        ShareUser.logout(drone);

        // Login as user
        DashBoardPage dashboard = ShareUser.login(drone, testUserInfo).render();

        // Create Folder
        dashboard.getNav().selectMyFilesPage().render();
        ShareUserSitePage.createFolder(drone, folderName, null);

        MyFilesPage myFilesPage = dashboard.getNav().selectMyFilesPage().render();

        // Create the rule for folder
        FolderRulesPage folderRulesPage = myFilesPage.getFileDirectoryInfo(folderName).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(folderName), "Rule page isn't correct");

        // Fill "Name" field with correct data
        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField(ruleName);

        // Select any type, e.g. "Items are created or enter this folder" value from "When" drop-down
        WhenSelectorImpl whenSelectorIml = createRulePage.getWhenOptionObj();
        whenSelectorIml.selectInbound();

        // Select 'All items' from "If" drop-down select control
        AbstractIfSelector ifSelector = createRulePage.getIfOptionObj();
        ifSelector.selectIFOption(0);

        // Select "Set property value" from "Perform Action" drop-down select
        ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectSetPropertyValue(allFolder, value);

        // Select any property and click "Create" button
        createRulePage.fillSetValueField(toText);
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

        myFilesPage.getNav().selectMyFilesPage().render();

        myFilesPage.selectFolder(folderName);

        File newFileName1 = newFile(DATA_FOLDER + (fileName1), fileName1);
        myFilesPage = ShareUserSitePage.uploadFile(drone, newFileName1).render();
        FileUtils.forceDelete(newFileName1);

        // Verify the property value is set according to the rule
        Assert.assertEquals(myFilesPage.getFileDirectoryInfo(fileName1).getDescription(), toText, "Expected description for file " + fileName1
                + " isn't displayed. The property value isn't set according to the rule");
    }

    /**
     * Test - AONE-14882:Execute inbound (create) rule to set property values for a document
     * <ul>
     * <li>Any site is created</li>
     * <li>Any folder is created</li>
     * <li>Any "Set property value" rule is created for the folder</li>
     * <li>Execute the rule</li>
     * <li>Upload the file to the folder (in case of inbound rule)</li>
     * <li>Verify the property value is set according to the rule</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 400000)
    public void AONE_14882() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String folderName = getFolderName(testName);
        String siteName = getSiteName(testName) + getRandomString(5);
        String ruleName = "Rule Name";
        String allFolder = "All";
        String value = "cm:name";
        String toText = testName + "_text";
        String fileName1 = "First_" + getFileName(testName) + ".txt";

        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        ShareUser.logout(drone);

        // Login as user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Create Folder
        ShareUserSitePage.createFolder(drone, folderName, null);

        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibrary(drone);

        // Create the rule for folder
        FolderRulesPage folderRulesPage = docLibPage.getFileDirectoryInfo(folderName).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(folderName), "Rule page isn't correct");

        // Fill "Name" field with correct data
        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField(ruleName);

        // Select any type, e.g. "Items are created or enter this folder" value from "When" drop-down
        WhenSelectorImpl whenSelectorIml = createRulePage.getWhenOptionObj();
        whenSelectorIml.selectInbound();

        // Select 'All items' from "If" drop-down select control
        AbstractIfSelector ifSelector = createRulePage.getIfOptionObj();
        ifSelector.selectIFOption(0);

        // Select "Set property value" from "Perform Action" drop-down select
        ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectSetPropertyValue(allFolder, value);

        // Select any property and click "Create" button
        createRulePage.fillSetValueField(toText);
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

        // Login
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        // Upload any files
        String[] fileInfo = { fileName1, folderName };
        ShareUser.uploadFileInFolder(drone, fileInfo);

        Assert.assertTrue(docLibPage.isFileVisible(toText), "File " + toText + " isn't visible");
        Assert.assertFalse(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " is visible");

    }

    /**
     * Test - AONE-14883:Execute inbound (create) rule to set property values for a folder
     * <ul>
     * <li>Any site is created</li>
     * <li>Any folder is created</li>
     * <li>Any "Set property value" rule is created for the folder</li>
     * <li>Execute the rule: Create any folder</li>
     * <li>The folder is created</li>
     * <li>The rule is executed</li>
     * <li>The property value is applied</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 400000)
    public void AONE_14883() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String folderName = getFolderName(testName);
        String siteName = getSiteName(testName) + getRandomString(5);
        String ruleName = "Rule Name";
        String allFolder = "All";
        String value = "cm:description";
        String toText = testName + "_text";
        String folderName1 = "First_" + getFolderName(testName);

        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        ShareUser.logout(drone);

        // Login as user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Create Folder
        ShareUserSitePage.createFolder(drone, folderName, null);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Create the rule for folder
        FolderRulesPage folderRulesPage = docLibPage.getFileDirectoryInfo(folderName).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(folderName), "Rule page isn't correct");

        // Fill "Name" field with correct data
        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField(ruleName);

        // Select any type, e.g. "Items are created or enter this folder" value from "When" drop-down
        WhenSelectorImpl whenSelectorIml = createRulePage.getWhenOptionObj();
        whenSelectorIml.selectInbound();

        // Select 'All items' from "If" drop-down select control
        AbstractIfSelector ifSelector = createRulePage.getIfOptionObj();
        ifSelector.selectIFOption(0);

        // Select "Set property value" from "Perform Action" drop-down select
        ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectSetPropertyValue(allFolder, value);

        // Select any property and click "Create" button
        createRulePage.fillSetValueField(toText);
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

        // Login
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        docLibPage.selectFolder(folderName).render();

        // Upload any files
        ShareUserSitePage.createFolder(drone, folderName1, null);

        // The folder is created. The rule is executed. The property value is applied
        Assert.assertTrue(docLibPage.isFileVisible(folderName1), "Folder " + folderName1 + " isn't visible");

        Assert.assertEquals(docLibPage.getFileDirectoryInfo(folderName1).getDescription(), toText, "Expected description for folder " + folderName1
                + " isn't displayed. The property value isn't set according to the rule");
    }

    /**
     * Test - AONE-14884:Execute inbound (move) rule to set property values for a document
     * <ul>
     * <li>Any site is created</li>
     * <li>Two folders are created</li>
     * <li>Any inbound "Set property value" rule is created for the folder1</li>
     * <li>Upload(create) a file to the folder2</li>
     * <li>Execute the rule: Move the file from folder2 to folder1</li>
     * <li>The file is moved</li>
     * <li>The rule is executed</li>
     * <li>The property value is applied</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 400000)
    public void AONE_14884() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String folderName1 = getFolderName(testName) + "-1";
        String folderName2 = getFolderName(testName) + "-2";
        String siteName = getSiteName(testName) + getRandomString(5);
        String ruleName = "Rule Name";
        String allFolder = "All";
        String value = "cm:description";
        String toText = testName + "_text";
        String fileName1 = "First_" + getFileName(testName) + ".txt";

        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        ShareUser.logout(drone);

        // Login as user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Two folders are created
        ShareUserSitePage.createFolder(drone, folderName1, null);
        ShareUserSitePage.createFolder(drone, folderName2, null);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Any inbound "Set property value" rule is created for the folder1
        FolderRulesPage folderRulesPage = docLibPage.getFileDirectoryInfo(folderName1).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(folderName1), "Rule page isn't correct");

        // Fill "Name" field with correct data
        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField(ruleName);

        // Select any type, e.g. "Items are created or enter this folder" value from "When" drop-down
        WhenSelectorImpl whenSelectorIml = createRulePage.getWhenOptionObj();
        whenSelectorIml.selectInbound();

        // Select 'All items' from "If" drop-down select control
        AbstractIfSelector ifSelector = createRulePage.getIfOptionObj();
        ifSelector.selectIFOption(0);

        // Select "Set property value" from "Perform Action" drop-down select
        ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectSetPropertyValue(allFolder, value);

        // Select any property and click "Create" button
        createRulePage.fillSetValueField(toText);
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName1), "Rule page with rule isn't correct");

        // Login
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertTrue(docLibPage.isFileVisible(folderName1), "Folder " + folderName1 + " isn't visible");
        Assert.assertTrue(docLibPage.isFileVisible(folderName2), "Folder " + folderName2 + " isn't visible");

        // Upload(create) a file to the folder2
        String[] fileInfo = { fileName1, folderName2 };
        ShareUser.uploadFileInFolder(drone, fileInfo);

        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " is visible");

        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName1).getDescription().equals(toText), "Expected description for file " + fileName1
                + " is displayed. The property value is set");

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Execute the rule: Move the file from folder2 to folder1
        docLibPage = ShareUserSitePage.copyOrMoveToFolder(drone, siteName, fileName1, new String[] { folderName1 }, false);

        Assert.assertFalse(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " is visible. The file isn't moved");

        // The file is moved. The rule is executed. The property value is applied
        ShareUser.openDocumentLibrary(drone);

        docLibPage.selectFolder(folderName1).render();

        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible. The file isn't moved");

        Assert.assertEquals(docLibPage.getFileDirectoryInfo(fileName1).getDescription(), toText, "Expected description for file " + fileName1
                + " isn't displayed. The property value isn't set according to the rule");

        ShareUser.openDocumentLibrary(drone);

        docLibPage.selectFolder(folderName2).render();

        Assert.assertFalse(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " is visible. The file isn't moved");
    }

    /**
     * Test - AONE-14885:Execute inbound (move) rule to set property values for a folder
     * <ul>
     * <li>Any site is created</li>
     * <li>Two folders are created</li>
     * <li>Any inbound "Set property value" rule is created for the folder1</li>
     * <li>Create a folder in the folder2</li>
     * <li>Execute the rule: Move the folder from folder2 to folder1</li>
     * <li>The folder is moved</li>
     * <li>The rule is executed</li>
     * <li>The property value is applied</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 400000)
    public void AONE_14885() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String folderName1 = getFolderName(testName) + "-1";
        String folderName2 = getFolderName(testName) + "-2";
        String siteName = getSiteName(testName) + getRandomString(5);
        String ruleName = "Rule Name";
        String allFolder = "All";
        String value = "cm:description";
        String toText = testName + "_text";
        String folderName3 = getFolderName(testName) + "-sub";

        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        ShareUser.logout(drone);

        // Login as user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Two folders are created
        ShareUserSitePage.createFolder(drone, folderName1, null);
        ShareUserSitePage.createFolder(drone, folderName2, null);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Any inbound "Set property value" rule is created for the folder1
        FolderRulesPage folderRulesPage = docLibPage.getFileDirectoryInfo(folderName1).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(folderName1), "Rule page isn't correct");

        // Fill "Name" field with correct data
        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField(ruleName);

        // Select any type, e.g. "Items are created or enter this folder" value from "When" drop-down
        WhenSelectorImpl whenSelectorIml = createRulePage.getWhenOptionObj();
        whenSelectorIml.selectInbound();

        // Select 'All items' from "If" drop-down select control
        AbstractIfSelector ifSelector = createRulePage.getIfOptionObj();
        ifSelector.selectIFOption(0);

        // Select "Set property value" from "Perform Action" drop-down select
        ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectSetPropertyValue(allFolder, value);

        // Select any property and click "Create" button
        createRulePage.fillSetValueField(toText);
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName1), "Rule page with rule isn't correct");

        // Login
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertTrue(docLibPage.isFileVisible(folderName1), "Folder " + folderName1 + " isn't visible");
        Assert.assertTrue(docLibPage.isFileVisible(folderName2), "Folder " + folderName2 + " isn't visible");

        docLibPage.selectFolder(folderName2).render();

        // Create a folder in the folder2
        ShareUserSitePage.createFolder(drone, folderName3, null);

        Assert.assertTrue(docLibPage.isFileVisible(folderName3), "Sub folder " + folderName3 + " isn't visible");

        Assert.assertFalse(docLibPage.getFileDirectoryInfo(folderName3).getDescription().equals(toText), "Expected description for folder " + folderName1
                + " is displayed. The property value is set");

        // Execute the rule: Move the folder from folder2 to folder1
        docLibPage = ShareUserSitePage.copyOrMoveToFolder(drone, siteName, folderName3, new String[] { folderName1 }, false);

        Assert.assertFalse(docLibPage.isFileVisible(folderName3), "Folder " + folderName3 + " is visible. The folder isn't moved");

        // The folder is moved. The rule is executed. The property value is applied
        ShareUser.openDocumentLibrary(drone);

        docLibPage.selectFolder(folderName1).render();

        Assert.assertTrue(docLibPage.isFileVisible(folderName3), "Folder " + folderName3 + " isn't visible. The folder isn't moved");

        Assert.assertEquals(docLibPage.getFileDirectoryInfo(folderName3).getDescription(), toText, "Expected description for folder " + folderName3
                + " isn't displayed. The property value isn't set according to the rule");

        ShareUser.openDocumentLibrary(drone);

        docLibPage.selectFolder(folderName2).render();

        Assert.assertFalse(docLibPage.isFileVisible(folderName3), "Folder " + folderName3 + " is visible. The folder isn't moved");
    }

    /**
     * Test - AONE-14886:Execute updated rule to set property values for a document
     * <ul>
     * <li>Any site is created</li>
     * <li>Any folder is created</li>
     * <li>Any "Set property value" rule is created for the folder</li>
     * <li>Upload(create) any file to the folder</li>
     * <li>Update the file</li>
     * <li>The file is updated</li>
     * <li>The rule is executed. The property value is applied</li>
     * <li>Verify the property value is set according to the rule</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 400000)
    public void AONE_14886() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String folderName = getFolderName(testName);
        String siteName = getSiteName(testName) + getRandomString(5);
        String ruleName = "Rule Name";
        String allFolder = "All";
        String value = "cm:description";
        String toText = testName + "_text";
        String fileName1 = "First_" + getFileName(testName) + ".txt";

        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        ShareUser.logout(drone);

        // Login as user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Create Folder
        ShareUserSitePage.createFolder(drone, folderName, null);

        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibrary(drone);

        // Create the rule for folder
        FolderRulesPage folderRulesPage = docLibPage.getFileDirectoryInfo(folderName).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(folderName), "Rule page isn't correct");

        // Fill "Name" field with correct data
        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField(ruleName);

        // Select any type, e.g. "Items are created or enter this folder" value from "When" drop-down
        WhenSelectorImpl whenSelectorIml = createRulePage.getWhenOptionObj();
        whenSelectorIml.selectUpdate();

        // Select 'All items' from "If" drop-down select control
        AbstractIfSelector ifSelector = createRulePage.getIfOptionObj();
        ifSelector.selectIFOption(0);

        // Select "Set property value" from "Perform Action" drop-down select
        ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectSetPropertyValue(allFolder, value);

        // Select any property and click "Create" button
        createRulePage.fillSetValueField(toText);
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

        // Login
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        // Navigating to folder
        ShareUser.openDocumentLibrary(drone);

        docLibPage.selectFolder(folderName).render();

        // Upload(create) any file to the folder
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.PLAINTEXT, docLibPage);

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");
        docLibPage.selectFolder(folderName).render();

        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");

        // Update the file
        docLibPage = ShareUser.editProperties(drone, fileName1, fileName1);
        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");

        // The rule is executed. The property value is applied
        Assert.assertEquals(docLibPage.getFileDirectoryInfo(fileName1).getDescription(), toText, "Expected description for file " + fileName1
                + " isn't displayed. The property value isn't set according to the rule");

    }

    /**
     * Test - AONE-14887:Execute updated rule to set property values for a folder
     * <ul>
     * <li>Any site is created</li>
     * <li>Any folder is created</li>
     * <li>Any "Set property value" rule is created for the folder</li>
     * <li>Create any folder in the folder</li>
     * <li>Update the folder</li>
     * <li>The rule is executed. The property value is applied</li>
     * <li>Verify the property value is set according to the rule</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 400000)
    public void AONE_14887() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String folderName = getFolderName(testName);
        String folderNameSub = getFolderName(testName) + "-sub";
        String siteName = getSiteName(testName) + getRandomString(5);
        String ruleName = "Rule Name";
        String allFolder = "All";
        String value = "cm:description";
        String toText = testName + "_text";

        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        ShareUser.logout(drone);

        // Login as user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Create Folder
        ShareUserSitePage.createFolder(drone, folderName, null);

        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibrary(drone);

        // Create the rule for folder
        FolderRulesPage folderRulesPage = docLibPage.getFileDirectoryInfo(folderName).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(folderName), "Rule page isn't correct");

        // Fill "Name" field with correct data
        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField(ruleName);

        // Select any type, e.g. "Items are created or enter this folder" value from "When" drop-down
        WhenSelectorImpl whenSelectorIml = createRulePage.getWhenOptionObj();
        whenSelectorIml.selectUpdate();

        // Select 'All items' from "If" drop-down select control
        AbstractIfSelector ifSelector = createRulePage.getIfOptionObj();
        ifSelector.selectIFOption(0);

        // Select "Set property value" from "Perform Action" drop-down select
        ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectSetPropertyValue(allFolder, value);

        // Select any property and click "Create" button
        createRulePage.fillSetValueField(toText);
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

        // Login
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        // Navigating to folder
        ShareUser.openDocumentLibrary(drone);

        docLibPage.selectFolder(folderName).render();

        // Create a folder in the folder2
        ShareUserSitePage.createFolder(drone, folderNameSub, null);

        Assert.assertTrue(docLibPage.isFileVisible(folderNameSub), "Sub folder " + folderNameSub + " isn't visible");

        // Update the folder
        docLibPage = ShareUser.editProperties(drone, folderNameSub, folderNameSub);
        Assert.assertTrue(docLibPage.isFileVisible(folderNameSub), "Folder " + folderNameSub + " isn't visible");

        // The rule is executed. The property value is applied
        Assert.assertEquals(docLibPage.getFileDirectoryInfo(folderNameSub).getDescription(), toText, "Expected description for folder " + folderNameSub
                + " isn't displayed. The property value isn't set according to the rule");

    }

    /**
     * Test - AONE-14888:Execute outbound (delete) rule to set property values for a document
     * <ul>
     * <li>Any site is created</li>
     * <li>Any folder is created</li>
     * <li>Any outbound "Set property value" rule is created for the folder</li>
     * <li>Upload(create) a file to the folder</li>
     * <li>Delete the file</li>
     * <li>The file is deleted. No error occurs</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 400000)
    public void AONE_14888() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String folderName = getFolderName(testName);
        String siteName = getSiteName(testName) + getRandomString(5);
        String ruleName = "Rule Name";
        String allFolder = "All";
        String value = "cm:description";
        String toText = testName + "_text";
        String fileName1 = "First_" + getFileName(testName) + ".txt";

        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        ShareUser.logout(drone);

        // Login as user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Create Folder
        ShareUserSitePage.createFolder(drone, folderName, null);

        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibrary(drone);

        // Create the rule for folder
        FolderRulesPage folderRulesPage = docLibPage.getFileDirectoryInfo(folderName).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(folderName), "Rule page isn't correct");

        // Fill "Name" field with correct data
        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField(ruleName);

        // Select any type, e.g. outbound
        WhenSelectorImpl whenSelectorIml = createRulePage.getWhenOptionObj();
        whenSelectorIml.selectOutbound();

        // Select 'All items' from "If" drop-down select control
        AbstractIfSelector ifSelector = createRulePage.getIfOptionObj();
        ifSelector.selectIFOption(0);

        // Select "Set property value" from "Perform Action" drop-down select
        ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectSetPropertyValue(allFolder, value);

        // Select any property and click "Create" button
        createRulePage.fillSetValueField(toText);
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

        // Login
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        // Upload any file
        String[] fileInfo = { fileName1, folderName };
        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");

        // Delete the file
        ShareUser.selectContentCheckBox(drone, fileName1);
        docLibPage = ShareUser.deleteSelectedContent(drone);

        // The file is deleted. No error occurs
        Assert.assertFalse(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " is visible");

        docLibPage = ShareUser.openDocumentLibrary(drone);

        // Navigating to folder
        docLibPage.selectFolder(folderName).render();

        // The file is deleted. No error occurs
        Assert.assertFalse(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " is visible");
    }

    /**
     * Test - AONE-14889:Execute outbound (delete) rule to set property values for a folder
     * <ul>
     * <li>Any site is created</li>
     * <li>Any folder is created</li>
     * <li>Any outbound "Set property value" rule is created for the folder</li>
     * <li>Create a folder in the folder</li>
     * <li>The folder is created</li>
     * <li>Delete the folder</li>
     * <li>The folder is deleted. No error occurs</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 400000)
    public void AONE_14889() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String folderName = getFolderName(testName);
        String siteName = getSiteName(testName) + getRandomString(5);
        String ruleName = "Rule Name";
        String allFolder = "All";
        String value = "cm:description";
        String toText = testName + "_text";
        String folderNameSub = "Sub_" + getFolderName(testName);

        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        ShareUser.logout(drone);

        // Login as user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Create Folder
        ShareUserSitePage.createFolder(drone, folderName, null);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Create the rule for folder
        FolderRulesPage folderRulesPage = docLibPage.getFileDirectoryInfo(folderName).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(folderName), "Rule page isn't correct");

        // Fill "Name" field with correct data
        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField(ruleName);

        // Select any type, e.g. "Items are created or enter this folder" value from "When" drop-down
        WhenSelectorImpl whenSelectorIml = createRulePage.getWhenOptionObj();
        whenSelectorIml.selectOutbound();

        // Select 'All items' from "If" drop-down select control
        AbstractIfSelector ifSelector = createRulePage.getIfOptionObj();
        ifSelector.selectIFOption(0);

        // Select "Set property value" from "Perform Action" drop-down select
        ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectSetPropertyValue(allFolder, value);

        // Select any property and click "Create" button
        createRulePage.fillSetValueField(toText);
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

        // Login
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        docLibPage.selectFolder(folderName).render();

        // Upload any files
        ShareUserSitePage.createFolder(drone, folderNameSub, null);

        // The folder is created.
        Assert.assertTrue(docLibPage.isFileVisible(folderNameSub), "Folder " + folderNameSub + " isn't visible");

        // Delete the folder
        ShareUser.selectContentCheckBox(drone, folderNameSub);
        docLibPage = ShareUser.deleteSelectedContent(drone);

        // The folder is deleted. No error occurs
        Assert.assertFalse(docLibPage.isFileVisible(folderNameSub), "Folder " + folderNameSub + " is visible");

        docLibPage = ShareUser.openDocumentLibrary(drone);

        // Navigating to folder
        docLibPage.selectFolder(folderName).render();

        // The folder is deleted. No error occurs
        Assert.assertFalse(docLibPage.isFileVisible(folderNameSub), "Folder " + folderNameSub + " is visible");
    }

    /**
     * Test - AONE-14890:Execute outbound (move) rule to set property values for a document
     * <ul>
     * <li>Any site is created</li>
     * <li>Two folders are created</li>
     * <li>Any outbound "Set property value" rule is created for the folder2</li>
     * <li>Upload(create) a file to the folder2</li>
     * <li>Move the file from folder2 to folder1</li>
     * <li>The rule is executed</li>
     * <li>The property value is applied</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 400000)
    public void AONE_14890() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String folderName1 = getFolderName(testName) + "-1";
        String folderName2 = getFolderName(testName) + "-2";
        String siteName = getSiteName(testName) + getRandomString(5);
        String ruleName = "Rule Name";
        String allFolder = "All";
        String value = "cm:description";
        String toText = testName + "_text";
        String fileName1 = "First_" + getFileName(testName) + ".txt";

        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        ShareUser.logout(drone);

        // Login as user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Two folders are created
        ShareUserSitePage.createFolder(drone, folderName1, null);
        ShareUserSitePage.createFolder(drone, folderName2, null);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Any inbound "Set property value" rule is created for the folder1
        FolderRulesPage folderRulesPage = docLibPage.getFileDirectoryInfo(folderName2).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(folderName2), "Rule page isn't correct");

        // Fill "Name" field with correct data
        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField(ruleName);

        // Select any type, e.g. "Items are created or enter this folder" value from "When" drop-down
        WhenSelectorImpl whenSelectorIml = createRulePage.getWhenOptionObj();
        whenSelectorIml.selectOutbound();

        // Select 'All items' from "If" drop-down select control
        AbstractIfSelector ifSelector = createRulePage.getIfOptionObj();
        ifSelector.selectIFOption(0);

        // Select "Set property value" from "Perform Action" drop-down select
        ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectSetPropertyValue(allFolder, value);

        // Select any property and click "Create" button
        createRulePage.fillSetValueField(toText);
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName2), "Rule page with rule isn't correct");

        // Login
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertTrue(docLibPage.isFileVisible(folderName1), "Folder " + folderName1 + " isn't visible");
        Assert.assertTrue(docLibPage.isFileVisible(folderName2), "Folder " + folderName2 + " isn't visible");

        // Upload(create) a file to the folder2
        String[] fileInfo = { fileName1, folderName2 };
        ShareUser.uploadFileInFolder(drone, fileInfo);

        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " is visible");

        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName1).getDescription().equals(toText), "Expected description for file " + fileName1
                + " is displayed. The property value is set");

        // Execute the rule: Move the file from folder2 to folder1
        docLibPage = ShareUserSitePage.copyOrMoveToFolder(drone, siteName, fileName1, new String[] { folderName1 }, false);

        Assert.assertFalse(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " is visible. The file isn't moved");

        // The file is moved. The rule is executed. The property value is applied
        ShareUser.openDocumentLibrary(drone);

        docLibPage.selectFolder(folderName1).render();

        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible. The file isn't moved");

        Assert.assertEquals(docLibPage.getFileDirectoryInfo(fileName1).getDescription(), toText, "Expected description for file " + fileName1
                + " isn't displayed. The property value isn't set according to the rule");

        ShareUser.openDocumentLibrary(drone);

        docLibPage.selectFolder(folderName2).render();

        Assert.assertFalse(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " is visible. The file isn't moved");
    }

    /**
     * Test - AONE-14891:Execute outbound (move) rule to set property values for a folder
     * <ul>
     * <li>Any site is created</li>
     * <li>Two folders are created</li>
     * <li>Any outbound "Set property value" rule is created for the folder2</li>
     * <li>Create a folder in the folder2</li>
     * <li>Move the folder from folder2 to folder1</li>
     * <li>The folder is moved</li>
     * <li>The rule is executed</li>
     * <li>The property value is applied</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 400000)
    public void AONE_14891() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String folderName1 = getFolderName(testName) + "-1";
        String folderName2 = getFolderName(testName) + "-2";
        String siteName = getSiteName(testName) + getRandomString(5);
        String ruleName = "Rule Name";
        String allFolder = "All";
        String value = "cm:description";
        String toText = testName + "_text";
        String folderName3 = getFolderName(testName) + "-sub";

        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        ShareUser.logout(drone);

        // Login as user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Two folders are created
        ShareUserSitePage.createFolder(drone, folderName1, null);
        ShareUserSitePage.createFolder(drone, folderName2, null);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Any outbound "Set property value" rule is created for the folder2
        FolderRulesPage folderRulesPage = docLibPage.getFileDirectoryInfo(folderName2).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(folderName2), "Rule page isn't correct");

        // Fill "Name" field with correct data
        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField(ruleName);

        // Select any type, e.g. "Items are created or enter this folder" value from "When" drop-down
        WhenSelectorImpl whenSelectorIml = createRulePage.getWhenOptionObj();
        whenSelectorIml.selectOutbound();

        // Select 'All items' from "If" drop-down select control
        AbstractIfSelector ifSelector = createRulePage.getIfOptionObj();
        ifSelector.selectIFOption(0);

        // Select "Set property value" from "Perform Action" drop-down select
        ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectSetPropertyValue(allFolder, value);

        // Select any property and click "Create" button
        createRulePage.fillSetValueField(toText);
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName2), "Rule page with rule isn't correct");

        // Login
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertTrue(docLibPage.isFileVisible(folderName1), "Folder " + folderName1 + " isn't visible");
        Assert.assertTrue(docLibPage.isFileVisible(folderName2), "Folder " + folderName2 + " isn't visible");

        docLibPage.selectFolder(folderName2).render();

        // Create a folder in the folder2
        ShareUserSitePage.createFolder(drone, folderName3, null);

        Assert.assertTrue(docLibPage.isFileVisible(folderName3), "Sub folder " + folderName3 + " isn't visible");

        Assert.assertFalse(docLibPage.getFileDirectoryInfo(folderName3).getDescription().equals(toText), "Expected description for folder " + folderName1
                + " is displayed. The property value is set");

        // Execute the rule: Move the folder from folder2 to folder1
        docLibPage = ShareUserSitePage.copyOrMoveToFolder(drone, siteName, folderName3, new String[] { folderName1 }, false);

        Assert.assertFalse(docLibPage.isFileVisible(folderName3), "Folder " + folderName3 + " is visible. The folder isn't moved");

        // The folder is moved. The rule is executed. The property value is applied
        ShareUser.openDocumentLibrary(drone);

        docLibPage.selectFolder(folderName1).render();

        Assert.assertTrue(docLibPage.isFileVisible(folderName3), "Folder " + folderName3 + " isn't visible. The folder isn't moved");

        Assert.assertEquals(docLibPage.getFileDirectoryInfo(folderName3).getDescription(), toText, "Expected description for folder " + folderName3
                + " isn't displayed. The property value isn't set according to the rule");

        ShareUser.openDocumentLibrary(drone);

        docLibPage.selectFolder(folderName2).render();

        Assert.assertFalse(docLibPage.isFileVisible(folderName3), "Folder " + folderName3 + " is visible. The folder isn't moved");
    }

    /**
     * Test - AONE-14892:Execute rule to set d:text property values
     * <ul>
     * <li>Any site is created</li>
     * <li>Any folder is created</li>
     * <li>The rule to set d:text (e.g cm:creator) property value is created for the folder</li>
     * <li>Execute the rule, e.g. Upload the file to the folder (in case of inbound rule)</li>
     * <li>Verify the property value is set according to the rule</li>
     * <li>The property value is set according to the rule</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 400000)
    public void AONE_14892() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String folderName = getFolderName(testName) + getRandomString(3);
        String tempFolderName = "temp_" + getFolderName(testName) + getRandomString(3);
        String ruleName = "Rule Name";
        String allFolder = "All";
        String value = "smm:propText";
        String toText = testName + "_text";
        String fileName1 = "temp_" + getFileName(testName) + ".txt";
        String fileNameRule = "RuleFile_" + getFileName(testName) + ".txt";
        String siteNameRule = getSiteName(testName) + getRandomString(3);

        String file1 = "SampleModelNew.xml";
        File modelFile = new File(DATA_FOLDER + "content-rules" + SLASH, file1).getAbsoluteFile();

        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        ShareUser.logout(drone);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Add model file to Company Home > Data Dictionary > Models
        ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        String DATA_DICTIONARY_FOLDER = "Data Dictionary";
        RepositoryPage repositoryPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + DATA_DICTIONARY_FOLDER + SLASH + "Models");

        if (!repositoryPage.isFileVisible(file1))
        {
            ShareUserRepositoryPage.uploadFileInRepository(drone, modelFile);

            RepositoryPage repoPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + DATA_DICTIONARY_FOLDER + SLASH + "Models");
            EditDocumentPropertiesPage editDocPropsPage = repoPage.getFileDirectoryInfo(file1).selectEditProperties().render();
            editDocPropsPage.setModelActive();
            editDocPropsPage.clickSave();
        }

        // Create js file for rule
        String scriptName = "createFile_" + System.currentTimeMillis() + ".js";
        String scriptContent = "var documentLibrary = companyhome.childByNamePath(\"sites/" + siteNameRule + "/documentLibrary\");" + "\r\n"
                + "var file1 = documentLibrary.createNode(\"" + fileNameRule + "\", \"smm:S01\");";
        File jsFile = SiteUtil.newFile(scriptName, scriptContent).getAbsoluteFile();
        jsFile.deleteOnExit();

        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + "Data Dictionary" + SLASH + "Scripts");

        // Add the js file to the Data Diction/Scripts directory
        repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, jsFile);
        Assert.assertTrue(repositoryPage.isFileVisible(jsFile.getName()), "File " + scriptName + " isn't visible");

        ShareUser.logout(drone);

        // Login as user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteNameRule, SITE_VISIBILITY_PUBLIC);

        ShareUser.openSitesDocumentLibrary(drone, siteNameRule);

        // Temp rule for creation file with custom type
        ShareUserSitePage.createFolder(drone, tempFolderName, null);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteNameRule);

        // Create the rule for folder
        FolderRulesPage folderRulesPage = docLibPage.getFileDirectoryInfo(tempFolderName).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(tempFolderName), "Rule page isn't correct");

        // Fill "Name" field with correct data
        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField(ruleName);

        // Select any type, e.g. "Items are created or enter this folder" value from "When" drop-down
        WhenSelectorImpl whenSelectorIml = createRulePage.getWhenOptionObj();
        whenSelectorIml.selectInbound();

        // Select 'All items' from "If" drop-down select control
        AbstractIfSelector ifSelector = createRulePage.getIfOptionObj();
        ifSelector.selectIFOption(0);

        // Select "Execute script" from "Perform Action" drop-down select
        ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectExecuteScript(scriptName);

        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(tempFolderName), "Rule page with rule isn't correct");

        // Upload any files
        String[] fileInfo = { fileName1, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");

        docLibPage = ShareUserSitePage.copyOrMoveToFolder(drone, siteNameRule, fileName1, new String[] { tempFolderName }, true);

        ShareUser.openDocumentLibrary(drone);

        Assert.assertTrue(docLibPage.isFileVisible(fileNameRule), "File " + fileNameRule + " isn't visible");
        Assert.assertTrue(docLibPage.isFileVisible(tempFolderName), "Folder " + tempFolderName + " isn't visible");

        // Create Folder
        ShareUserSitePage.createFolder(drone, folderName, null);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteNameRule);

        // Create the rule for folder
        folderRulesPage = docLibPage.getFileDirectoryInfo(folderName).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(folderName), "Rule page isn't correct");

        // Fill "Name" field with correct data
        createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField(ruleName);

        // Select any type, e.g. "Items are created or enter this folder" value from "When" drop-down
        whenSelectorIml = createRulePage.getWhenOptionObj();
        whenSelectorIml.selectInbound();

        // Select 'All items' from "If" drop-down select control
        ifSelector = createRulePage.getIfOptionObj();
        ifSelector.selectIFOption(0);

        // Select "Set property value" from "Perform Action" drop-down select
        actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectSetPropertyValue(allFolder, value);
        actionSelectorEnterpImpl.selectSetPropertyValue();

        // Select any property and click "Create" button
        createRulePage.fillSetValueField(toText);
        folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

        // Login
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteNameRule);

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        docLibPage = ShareUserSitePage.copyOrMoveToFolder(drone, siteNameRule, fileNameRule, new String[] { folderName }, true);

        Assert.assertTrue(docLibPage.isFileVisible(fileNameRule), "File " + fileNameRule + " isn't visible");

        docLibPage.selectFolder(folderName);

        Assert.assertTrue(docLibPage.isFileVisible(fileNameRule), "File " + fileNameRule + " isn't visible");

        // Verify the property value is set according to the rule
        DocumentDetailsPage detailsPage = docLibPage.selectFile(fileNameRule).render();
        Map<String, Object> props = detailsPage.getProperties();
        assertEquals(props.get("Text1"), toText, "The property value isn't set according to the rule (d:text)");
    }

    /**
     * Test - AONE-14893:Execute rule to set d:int property values
     * <ul>
     * <li>Any site is created</li>
     * <li>Any folder is created</li>
     * <li>The rule to set d:int (e.g. audio:sampleRate) property value is created for the folder</li>
     * <li>Execute the rule, e.g. Upload the file to the folder (in case of inbound rule)</li>
     * <li>Verify the property value is set according to the rule</li>
     * <li>The property value is set according to the rule</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 400000)
    public void AONE_14893() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String folderName = getFolderName(testName) + getRandomString(3);
        String tempFolderName = "temp_" + getFolderName(testName) + getRandomString(3);
        String ruleName = "Rule Name";
        String allFolder = "All";
        String value = "smm:propInt";
        String toText = "123321";
        String fileName1 = "temp_" + getFileName(testName) + ".txt";
        String fileNameRule = "RuleFile_" + getFileName(testName) + ".txt";
        String siteNameRule = getSiteName(testName) + getRandomString(3);

        String file1 = "SampleModelNew.xml";
        File modelFile = new File(DATA_FOLDER + "content-rules" + SLASH, file1).getAbsoluteFile();

        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        ShareUser.logout(drone);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Add model file to Company Home > Data Dictionary > Models
        ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        String DATA_DICTIONARY_FOLDER = "Data Dictionary";
        RepositoryPage repositoryPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + DATA_DICTIONARY_FOLDER + SLASH + "Models");

        if (!repositoryPage.isFileVisible(file1))
        {
            ShareUserRepositoryPage.uploadFileInRepository(drone, modelFile);

            RepositoryPage repoPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + DATA_DICTIONARY_FOLDER + SLASH + "Models");
            EditDocumentPropertiesPage editDocPropsPage = repoPage.getFileDirectoryInfo(file1).selectEditProperties().render();
            editDocPropsPage.setModelActive();
            editDocPropsPage.clickSave();
        }

        // Create js file for rule
        String scriptName = "createFile_" + System.currentTimeMillis() + ".js";
        String scriptContent = "var documentLibrary = companyhome.childByNamePath(\"sites/" + siteNameRule + "/documentLibrary\");" + "\r\n"
                + "var file1 = documentLibrary.createNode(\"" + fileNameRule + "\", \"smm:S01\");";
        File jsFile = SiteUtil.newFile(scriptName, scriptContent).getAbsoluteFile();
        jsFile.deleteOnExit();

        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + "Data Dictionary" + SLASH + "Scripts");

        // Add the js file to the Data Diction/Scripts directory
        repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, jsFile);
        Assert.assertTrue(repositoryPage.isFileVisible(jsFile.getName()), "File " + scriptName + " isn't visible");

        ShareUser.logout(drone);

        // Login as user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteNameRule, SITE_VISIBILITY_PUBLIC);

        ShareUser.openSitesDocumentLibrary(drone, siteNameRule);

        // Temp rule for creation file with custom type
        ShareUserSitePage.createFolder(drone, tempFolderName, null);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteNameRule);

        // Create the rule for folder
        FolderRulesPage folderRulesPage = docLibPage.getFileDirectoryInfo(tempFolderName).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(tempFolderName), "Rule page isn't correct");

        // Fill "Name" field with correct data
        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField(ruleName);

        // Select any type, e.g. "Items are created or enter this folder" value from "When" drop-down
        WhenSelectorImpl whenSelectorIml = createRulePage.getWhenOptionObj();
        whenSelectorIml.selectInbound();

        // Select 'All items' from "If" drop-down select control
        AbstractIfSelector ifSelector = createRulePage.getIfOptionObj();
        ifSelector.selectIFOption(0);

        // Select "Execute script" from "Perform Action" drop-down select
        ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectExecuteScript(scriptName);

        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(tempFolderName), "Rule page with rule isn't correct");

        // Upload any files
        String[] fileInfo = { fileName1, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");

        docLibPage = ShareUserSitePage.copyOrMoveToFolder(drone, siteNameRule, fileName1, new String[] { tempFolderName }, true);

        ShareUser.openDocumentLibrary(drone);

        Assert.assertTrue(docLibPage.isFileVisible(fileNameRule), "File " + fileNameRule + " isn't visible");
        Assert.assertTrue(docLibPage.isFileVisible(tempFolderName), "Folder " + tempFolderName + " isn't visible");

        // Create Folder
        ShareUserSitePage.createFolder(drone, folderName, null);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteNameRule);

        // Create the rule for folder
        folderRulesPage = docLibPage.getFileDirectoryInfo(folderName).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(folderName), "Rule page isn't correct");

        // Fill "Name" field with correct data
        createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField(ruleName);

        // Select any type, e.g. "Items are created or enter this folder" value from "When" drop-down
        whenSelectorIml = createRulePage.getWhenOptionObj();
        whenSelectorIml.selectInbound();

        // Select 'All items' from "If" drop-down select control
        ifSelector = createRulePage.getIfOptionObj();
        ifSelector.selectIFOption(0);

        // Select "Set property value" from "Perform Action" drop-down select
        actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectSetPropertyValue(allFolder, value);
        actionSelectorEnterpImpl.selectSetPropertyValue();

        // Select any property and click "Create" button
        createRulePage.fillSetValueField(toText);
        folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

        // Login
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteNameRule);

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        docLibPage = ShareUserSitePage.copyOrMoveToFolder(drone, siteNameRule, fileNameRule, new String[] { folderName }, true);

        Assert.assertTrue(docLibPage.isFileVisible(fileNameRule), "File " + fileNameRule + " isn't visible");

        docLibPage.selectFolder(folderName);

        Assert.assertTrue(docLibPage.isFileVisible(fileNameRule), "File " + fileNameRule + " isn't visible");

        // Verify the property value is set according to the rule
        DocumentDetailsPage detailsPage = docLibPage.selectFile(fileNameRule).render();
        Map<String, Object> props = detailsPage.getProperties();
        assertEquals(props.get("Int1"), toText, "The property value isn't set according to the rule (d:int)");
    }

    /**
     * Test - AONE-14894:Execute rule to set d:long property values
     * <ul>
     * <li>Any site is created</li>
     * <li>Any folder is created</li>
     * <li>The rule to set d:long (e.g. sys:node-dbid) property value is created for the folder</li>
     * <li>Execute the rule, e.g. Upload the file to the folder (in case of inbound rule)</li>
     * <li>Verify the property value is set according to the rule</li>
     * <li>The property value is set according to the rule</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 400000)
    public void AONE_14894() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String folderName = getFolderName(testName) + getRandomString(3);
        String tempFolderName = "temp_" + getFolderName(testName) + getRandomString(3);
        String ruleName = "Rule Name";
        String allFolder = "All";
        String value = "smm:propLong";
        String toText = "321123";
        String fileName1 = "temp_" + getFileName(testName) + ".txt";
        String fileNameRule = "RuleFile_" + getFileName(testName) + ".txt";
        String siteNameRule = getSiteName(testName) + getRandomString(3);

        String file1 = "SampleModelNew.xml";
        File modelFile = new File(DATA_FOLDER + "content-rules" + SLASH, file1).getAbsoluteFile();

        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        ShareUser.logout(drone);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Add model file to Company Home > Data Dictionary > Models
        ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        String DATA_DICTIONARY_FOLDER = "Data Dictionary";
        RepositoryPage repositoryPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + DATA_DICTIONARY_FOLDER + SLASH + "Models");

        if (!repositoryPage.isFileVisible(file1))
        {
            ShareUserRepositoryPage.uploadFileInRepository(drone, modelFile);

            RepositoryPage repoPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + DATA_DICTIONARY_FOLDER + SLASH + "Models");
            EditDocumentPropertiesPage editDocPropsPage = repoPage.getFileDirectoryInfo(file1).selectEditProperties().render();
            editDocPropsPage.setModelActive();
            editDocPropsPage.clickSave();
        }

        // Create js file for rule
        String scriptName = "createFile_" + System.currentTimeMillis() + ".js";
        String scriptContent = "var documentLibrary = companyhome.childByNamePath(\"sites/" + siteNameRule + "/documentLibrary\");" + "\r\n"
                + "var file1 = documentLibrary.createNode(\"" + fileNameRule + "\", \"smm:S01\");";
        File jsFile = SiteUtil.newFile(scriptName, scriptContent).getAbsoluteFile();
        jsFile.deleteOnExit();

        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + "Data Dictionary" + SLASH + "Scripts");

        // Add the js file to the Data Diction/Scripts directory
        repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, jsFile);
        Assert.assertTrue(repositoryPage.isFileVisible(jsFile.getName()), "File " + scriptName + " isn't visible");

        ShareUser.logout(drone);

        // Login as user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteNameRule, SITE_VISIBILITY_PUBLIC);

        ShareUser.openSitesDocumentLibrary(drone, siteNameRule);

        // Temp rule for creation file with custom type
        ShareUserSitePage.createFolder(drone, tempFolderName, null);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteNameRule);

        // Create the rule for folder
        FolderRulesPage folderRulesPage = docLibPage.getFileDirectoryInfo(tempFolderName).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(tempFolderName), "Rule page isn't correct");

        // Fill "Name" field with correct data
        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField(ruleName);

        // Select any type, e.g. "Items are created or enter this folder" value from "When" drop-down
        WhenSelectorImpl whenSelectorIml = createRulePage.getWhenOptionObj();
        whenSelectorIml.selectInbound();

        // Select 'All items' from "If" drop-down select control
        AbstractIfSelector ifSelector = createRulePage.getIfOptionObj();
        ifSelector.selectIFOption(0);

        // Select "Execute script" from "Perform Action" drop-down select
        ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectExecuteScript(scriptName);

        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(tempFolderName), "Rule page with rule isn't correct");

        // Upload any files
        String[] fileInfo = { fileName1, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");

        docLibPage = ShareUserSitePage.copyOrMoveToFolder(drone, siteNameRule, fileName1, new String[] { tempFolderName }, true);

        ShareUser.openDocumentLibrary(drone);

        Assert.assertTrue(docLibPage.isFileVisible(fileNameRule), "File " + fileNameRule + " isn't visible");
        Assert.assertTrue(docLibPage.isFileVisible(tempFolderName), "Folder " + tempFolderName + " isn't visible");

        // Create Folder
        ShareUserSitePage.createFolder(drone, folderName, null);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteNameRule);

        // Create the rule for folder
        folderRulesPage = docLibPage.getFileDirectoryInfo(folderName).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(folderName), "Rule page isn't correct");

        // Fill "Name" field with correct data
        createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField(ruleName);

        // Select any type, e.g. "Items are created or enter this folder" value from "When" drop-down
        whenSelectorIml = createRulePage.getWhenOptionObj();
        whenSelectorIml.selectInbound();

        // Select 'All items' from "If" drop-down select control
        ifSelector = createRulePage.getIfOptionObj();
        ifSelector.selectIFOption(0);

        // Select "Set property value" from "Perform Action" drop-down select
        actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectSetPropertyValue(allFolder, value);
        actionSelectorEnterpImpl.selectSetPropertyValue();

        // Select any property and click "Create" button
        createRulePage.fillSetValueField(toText);
        folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

        // Login
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteNameRule);

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        docLibPage = ShareUserSitePage.copyOrMoveToFolder(drone, siteNameRule, fileNameRule, new String[] { folderName }, true);

        Assert.assertTrue(docLibPage.isFileVisible(fileNameRule), "File " + fileNameRule + " isn't visible");

        docLibPage.selectFolder(folderName);

        Assert.assertTrue(docLibPage.isFileVisible(fileNameRule), "File " + fileNameRule + " isn't visible");

        // Verify the property value is set according to the rule
        DocumentDetailsPage detailsPage = docLibPage.selectFile(fileNameRule).render();
        Map<String, Object> props = detailsPage.getProperties();
        assertEquals(props.get("Long1"), toText, "The property value isn't set according to the rule (d:long)");
    }

    /**
     * Test - AONE-14897:Execute rule to set d:date property values
     * <ul>
     * <li>Any site is created</li>
     * <li>Any folder is created</li>
     * <li>The rule to set d:date property value is created for the folder (e.g. cm:modified)</li>
     * <li>Execute the rule, e.g. Upload the file to the folder (in case of inbound rule)</li>
     * <li>Verify the property value is set according to the rule</li>
     * <li>The property value is set according to the rule</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 400000)
    public void AONE_14897() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String folderName = getFolderName(testName) + getRandomString(3);
        String tempFolderName = "temp_" + getFolderName(testName) + getRandomString(3);
        String ruleName = "Rule Name";
        String allFolder = "All";
        String value = "smm:propDate";

        Date today = new Date(new java.util.Date().getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        String ruleDate = sdf.format(today);
        SimpleDateFormat sdf1 = new SimpleDateFormat("EEE d MMM yyyy", Locale.ENGLISH);
        String propDate = sdf1.format(today);

        String fileName1 = "temp_" + getFileName(testName) + ".txt";
        String fileNameRule = "RuleFile_" + getFileName(testName) + ".txt";
        String siteNameRule = getSiteName(testName) + getRandomString(3);

        String file1 = "SampleModelNew.xml";
        File modelFile = new File(DATA_FOLDER + "content-rules" + SLASH, file1).getAbsoluteFile();

        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        ShareUser.logout(drone);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Add model file to Company Home > Data Dictionary > Models
        ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        String DATA_DICTIONARY_FOLDER = "Data Dictionary";
        RepositoryPage repositoryPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + DATA_DICTIONARY_FOLDER + SLASH + "Models");

        if (!repositoryPage.isFileVisible(file1))
        {
            ShareUserRepositoryPage.uploadFileInRepository(drone, modelFile);

            RepositoryPage repoPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + DATA_DICTIONARY_FOLDER + SLASH + "Models");
            EditDocumentPropertiesPage editDocPropsPage = repoPage.getFileDirectoryInfo(file1).selectEditProperties().render();
            editDocPropsPage.setModelActive();
            editDocPropsPage.clickSave();
        }

        // Create js file for rule
        String scriptName = "createFile_" + System.currentTimeMillis() + ".js";
        String scriptContent = "var documentLibrary = companyhome.childByNamePath(\"sites/" + siteNameRule + "/documentLibrary\");" + "\r\n"
                + "var file1 = documentLibrary.createNode(\"" + fileNameRule + "\", \"smm:S01\");";
        File jsFile = SiteUtil.newFile(scriptName, scriptContent).getAbsoluteFile();
        jsFile.deleteOnExit();

        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + "Data Dictionary" + SLASH + "Scripts");

        // Add the js file to the Data Diction/Scripts directory
        repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, jsFile);
        Assert.assertTrue(repositoryPage.isFileVisible(jsFile.getName()), "File " + scriptName + " isn't visible");

        ShareUser.logout(drone);

        // Login as user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteNameRule, SITE_VISIBILITY_PUBLIC);

        ShareUser.openDocumentLibrary(drone);

        // Temp rule for creation file with custom type
        ShareUserSitePage.createFolder(drone, tempFolderName, null);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteNameRule);

        // Create the rule for folder
        FolderRulesPage folderRulesPage = docLibPage.getFileDirectoryInfo(tempFolderName).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(tempFolderName), "Rule page isn't correct");

        // Fill "Name" field with correct data
        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField(ruleName);

        // Select any type, e.g. "Items are created or enter this folder" value from "When" drop-down
        WhenSelectorImpl whenSelectorIml = createRulePage.getWhenOptionObj();
        whenSelectorIml.selectInbound();

        // Select 'All items' from "If" drop-down select control
        AbstractIfSelector ifSelector = createRulePage.getIfOptionObj();
        ifSelector.selectIFOption(0);

        // Select "Execute script" from "Perform Action" drop-down select
        ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectExecuteScript(scriptName);

        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(tempFolderName), "Rule page with rule isn't correct");

        // Upload any files
        String[] fileInfo = { fileName1, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");

        docLibPage = ShareUserSitePage.copyOrMoveToFolder(drone, siteNameRule, fileName1, new String[] { tempFolderName }, true);

        ShareUser.openDocumentLibrary(drone);

        Assert.assertTrue(docLibPage.isFileVisible(fileNameRule), "File " + fileNameRule + " isn't visible");
        Assert.assertTrue(docLibPage.isFileVisible(tempFolderName), "Folder " + tempFolderName + " isn't visible");

        // Create Folder
        ShareUserSitePage.createFolder(drone, folderName, null);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteNameRule);

        // Create the rule for folder
        folderRulesPage = docLibPage.getFileDirectoryInfo(folderName).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(folderName), "Rule page isn't correct");

        // Fill "Name" field with correct data
        createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField(ruleName);

        // Select any type, e.g. "Items are created or enter this folder" value from "When" drop-down
        whenSelectorIml = createRulePage.getWhenOptionObj();
        whenSelectorIml.selectInbound();

        // Select 'All items' from "If" drop-down select control
        ifSelector = createRulePage.getIfOptionObj();
        ifSelector.selectIFOption(0);

        // Select "Set property value" from "Perform Action" drop-down select
        actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectSetPropertyValue(allFolder, value);
        actionSelectorEnterpImpl.selectSetPropertyValue();

        // Select any property and click "Create" button
        // auto complete createRulePage.fillSetValueField(ruleDate);
        createRulePage.fillSetValueFieldDate(ruleDate);

        folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

        // Login
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteNameRule);

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        docLibPage = ShareUserSitePage.copyOrMoveToFolder(drone, siteNameRule, fileNameRule, new String[] { folderName }, true);

        Assert.assertTrue(docLibPage.isFileVisible(fileNameRule), "File " + fileNameRule + " isn't visible");

        docLibPage.selectFolder(folderName);

        Assert.assertTrue(docLibPage.isFileVisible(fileNameRule), "File " + fileNameRule + " isn't visible");

        // Verify the property value is set according to the rule
        DocumentDetailsPage detailsPage = docLibPage.selectFile(fileNameRule).render();
        Map<String, Object> props = detailsPage.getProperties();
        assertEquals(props.get("Date1"), propDate, "The property value isn't set according to the rule (d:date)");
    }

    /**
     * Test - AONE-14898:Execute rule to set d:boolean property values
     * <ul>
     * <li>Any site is created</li>
     * <li>Any folder is created</li>
     * <li>The rule to set d:boolean property value is created for the folder (e.g. app:editInline);</li>
     * <li>Execute the rule, e.g. Upload the file to the folder (in case of inbound rule)</li>
     * <li>Verify the property value is set according to the rule</li>
     * <li>The property value is set according to the rule</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 400000)
    public void AONE_14898() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String folderName = getFolderName(testName) + getRandomString(3);
        String tempFolderName = "temp_" + getFolderName(testName) + getRandomString(3);
        String ruleName = "Rule Name";
        String allFolder = "All";
        String value = "smm:propBoolean";
        String fileName1 = "temp_" + getFileName(testName) + ".txt";
        String fileNameRule = "RuleFile_" + getFileName(testName) + ".txt";
        String siteNameRule = getSiteName(testName) + getRandomString(3);

        String file1 = "SampleModelNew.xml";
        File modelFile = new File(DATA_FOLDER + "content-rules" + SLASH, file1).getAbsoluteFile();

        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        ShareUser.logout(drone);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Add model file to Company Home > Data Dictionary > Models
        ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        String DATA_DICTIONARY_FOLDER = "Data Dictionary";
        RepositoryPage repositoryPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + DATA_DICTIONARY_FOLDER + SLASH + "Models");

        if (!repositoryPage.isFileVisible(file1))
        {
            ShareUserRepositoryPage.uploadFileInRepository(drone, modelFile);

            RepositoryPage repoPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + DATA_DICTIONARY_FOLDER + SLASH + "Models");
            EditDocumentPropertiesPage editDocPropsPage = repoPage.getFileDirectoryInfo(file1).selectEditProperties().render();
            editDocPropsPage.setModelActive();
            editDocPropsPage.clickSave();
        }

        // Create js file for rule
        String scriptName = "createFile_" + System.currentTimeMillis() + ".js";
        String scriptContent = "var documentLibrary = companyhome.childByNamePath(\"sites/" + siteNameRule + "/documentLibrary\");" + "\r\n"
                + "var file1 = documentLibrary.createNode(\"" + fileNameRule + "\", \"smm:S01\");";
        File jsFile = SiteUtil.newFile(scriptName, scriptContent).getAbsoluteFile();
        jsFile.deleteOnExit();

        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + "Data Dictionary" + SLASH + "Scripts");

        // Add the js file to the Data Diction/Scripts directory
        repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, jsFile);
        Assert.assertTrue(repositoryPage.isFileVisible(jsFile.getName()), "File " + scriptName + " isn't visible");

        ShareUser.logout(drone);

        // Login as user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteNameRule, SITE_VISIBILITY_PUBLIC);

        ShareUser.openSitesDocumentLibrary(drone, siteNameRule);

        // Temp rule for creation file with custom type
        ShareUserSitePage.createFolder(drone, tempFolderName, null);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteNameRule);

        // Create the rule for folder
        FolderRulesPage folderRulesPage = docLibPage.getFileDirectoryInfo(tempFolderName).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(tempFolderName), "Rule page isn't correct");

        // Fill "Name" field with correct data
        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField(ruleName);

        // Select any type, e.g. "Items are created or enter this folder" value from "When" drop-down
        WhenSelectorImpl whenSelectorIml = createRulePage.getWhenOptionObj();
        whenSelectorIml.selectInbound();

        // Select 'All items' from "If" drop-down select control
        AbstractIfSelector ifSelector = createRulePage.getIfOptionObj();
        ifSelector.selectIFOption(0);

        // Select "Execute script" from "Perform Action" drop-down select
        ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectExecuteScript(scriptName);

        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(tempFolderName), "Rule page with rule isn't correct");

        // Upload any files
        String[] fileInfo = { fileName1, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

        Assert.assertTrue(docLibPage.isFileVisible(fileName1), "File " + fileName1 + " isn't visible");

        docLibPage = ShareUserSitePage.copyOrMoveToFolder(drone, siteNameRule, fileName1, new String[] { tempFolderName }, true);

        ShareUser.openDocumentLibrary(drone);

        Assert.assertTrue(docLibPage.isFileVisible(fileNameRule), "File " + fileNameRule + " isn't visible");
        Assert.assertTrue(docLibPage.isFileVisible(tempFolderName), "Folder " + tempFolderName + " isn't visible");

        // Create Folder
        ShareUserSitePage.createFolder(drone, folderName, null);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteNameRule);

        // Create the rule for folder
        folderRulesPage = docLibPage.getFileDirectoryInfo(folderName).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(folderName), "Rule page isn't correct");

        // Fill "Name" field with correct data
        createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField(ruleName);

        // Select any type, e.g. "Items are created or enter this folder" value from "When" drop-down
        whenSelectorIml = createRulePage.getWhenOptionObj();
        whenSelectorIml.selectInbound();

        // Select 'All items' from "If" drop-down select control
        ifSelector = createRulePage.getIfOptionObj();
        ifSelector.selectIFOption(0);

        // Select "Set property value" from "Perform Action" drop-down select
        actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectSetPropertyValue(allFolder, value);
        actionSelectorEnterpImpl.selectSetPropertyValue();

        // Select any property and click "Create" button
        createRulePage.fillSetValueFieldCheckbox();
        folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

        // Login
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteNameRule);

        Assert.assertTrue(docLibPage.isFileVisible(folderName), "Folder " + folderName + " isn't visible");

        docLibPage = ShareUserSitePage.copyOrMoveToFolder(drone, siteNameRule, fileNameRule, new String[] { folderName }, true);

        Assert.assertTrue(docLibPage.isFileVisible(fileNameRule), "File " + fileNameRule + " isn't visible");
        docLibPage.selectFolder(folderName);
        Assert.assertTrue(docLibPage.isFileVisible(fileNameRule), "File " + fileNameRule + " isn't visible");

        // Verify the property value is set according to the rule
        DocumentDetailsPage detailsPage = docLibPage.selectFile(fileNameRule).render();
        Map<String, Object> props = detailsPage.getProperties();
        assertEquals(props.get("Boolean1"), "Yes", "The property value isn't set according to the rule (d:boolean)");
    }
}
