package org.alfresco.share.content.rules;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.site.contentrule.FolderRulesPage;
import org.alfresco.po.share.site.contentrule.FolderRulesPageWithRules;
import org.alfresco.po.share.site.contentrule.createrules.CreateRulePage;
import org.alfresco.po.share.site.contentrule.createrules.SetPropertyValuePage;
import org.alfresco.po.share.site.contentrule.createrules.selectors.AbstractIfSelector;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.ActionSelectorEnterpImpl;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.WhenSelectorImpl;
import org.alfresco.po.share.site.document.*;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;


/**
 * @author Maryia Zaichanka
 */


@Listeners(FailedTestListener.class)
public class CreateRuleSetPropertyTest extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(CreateRuleSetPropertyTest.class);

    protected String testUser;
    protected String siteName = "";
    private String DATA_DICTIONARY_FOLDER = "Data Dictionary";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();

        logger.info("[Suite ] : Start Tests in: " + testName);
    }

    @Test(groups = { "DataPrepEnterpriseOnly" })
    public void dataPrep_AONE_14866() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String folderName = getFolderName(testName);
        String siteName = getSiteName(testName);


        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        ShareUser.logout(drone);

        // Login as user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Create Folder
        ShareUserSitePage.createFolder(drone, folderName, null);
        ShareUser.logout(drone);


    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14866() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String ruleName = "Rule " + getRandomString(5);
        String allFolder = "All";
        String value = "act:actionDescription";
        String toText = getRandomString(5);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
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

        //  Select any property and click "Create" button
        createRulePage.fillSetValueField(toText);
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

    }

    @Test(groups = { "DataPrepEnterpriseOnly" })
    public void dataPrep_AONE_14867() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String folderName = getFolderName(testName);
        String siteName = getSiteName(testName);


        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        ShareUser.logout(drone);

        // Login as user
        DashBoardPage dashboard = ShareUser.login(drone, testUserInfo).render();

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Create Folder
        dashboard.getNav().selectMyFilesPage().render();
        ShareUserSitePage.createFolder(drone, folderName, null);
        ShareUser.logout(drone);


    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14867() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String folderName = getFolderName(testName);
        String ruleName = "Rule " + getRandomString(5);
        String allFolder = "All";
        String value = "act:actionDescription";
        String toText = getRandomString(5);

        // Login
        DashBoardPage dashboard = ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();
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

        //  Select any property and click "Create" button
        createRulePage.fillSetValueField(toText);
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

    }

    @Test(groups = { "DataPrepEnterpriseOnly" })
    public void dataPrep_AONE_14869() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String folderName = getFolderName(testName);
        String siteName = getSiteName(testName);


        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        ShareUser.logout(drone);

        // Login as user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Create Folder
        ShareUserSitePage.createFolder(drone, folderName, null);
        ShareUser.logout(drone);


    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14869() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String ruleName = "Rule " + getRandomString(5);
        String allFolder = "All";
        String value = "download:done";
        String toText = getRandomString(5);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
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

        //  Select any property and click "Create" button
        createRulePage.fillSetValueField(toText);
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

    }

    @Test(groups = { "DataPrepEnterpriseOnly" })
    public void dataPrep_AONE_14870() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String folderName = getFolderName(testName);
        String siteName = getSiteName(testName);


        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        ShareUser.logout(drone);

        // Login as user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Create Folder
        ShareUserSitePage.createFolder(drone, folderName, null);
        ShareUser.logout(drone);


    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14870() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String ruleName = "Rule " + getRandomString(5);
        String allFolder = "All";
        String value = "cm:companyaddress1";
        String toText = getRandomString(5);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Create the rule for folder
        FolderRulesPage folderRulesPage = docLibPage.getFileDirectoryInfo(folderName).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(folderName), "Rule page isn't correct");

        // Fill "Name" field with correct data
        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField(ruleName);

        // Select any type, e.g. "Items are updated" value from "When" drop-down
        WhenSelectorImpl whenSelectorIml = createRulePage.getWhenOptionObj();
        whenSelectorIml.selectUpdate();

        // Select 'All items' from "If" drop-down select control
        AbstractIfSelector ifSelector = createRulePage.getIfOptionObj();
        ifSelector.selectIFOption(0);

        // Select "Set property value" from "Perform Action" drop-down select
        ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectSetPropertyValue(allFolder, value);

        //  Select any property and click "Create" button
        createRulePage.fillSetValueField(toText);
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

    }

    @Test(groups = { "DataPrepEnterpriseOnly" })
    public void dataPrep_AONE_14871() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String folderName = getFolderName(testName);
        String siteName = getSiteName(testName);


        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        ShareUser.logout(drone);

        // Login as user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Create Folder
        ShareUserSitePage.createFolder(drone, folderName, null);
        ShareUser.logout(drone);


    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14871() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String ruleName = "Rule " + getRandomString(5);
        String allFolder = "All";
        String value = "blg:id";
        String toText = getRandomString(5);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Create the rule for folder
        FolderRulesPage folderRulesPage = docLibPage.getFileDirectoryInfo(folderName).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(folderName), "Rule page isn't correct");

        // Fill "Name" field with correct data
        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField(ruleName);

        // Select any type, e.g. "Items are deleted or leave this folder" value from "When" drop-down
        WhenSelectorImpl whenSelectorIml = createRulePage.getWhenOptionObj();
        whenSelectorIml.selectOutbound();

        // Select 'All items' from "If" drop-down select control
        AbstractIfSelector ifSelector = createRulePage.getIfOptionObj();
        ifSelector.selectIFOption(0);

        // Select "Set property value" from "Perform Action" drop-down select
        ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectSetPropertyValue(allFolder, value);

        //  Select any property and click "Create" button
        createRulePage.fillSetValueField(toText);
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

    }

    @Test(groups = { "DataPrepEnterpriseOnly" })
    public void dataPrep_AONE_14872() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String file1 = "exampleModel.xml";
        String file2 = "web-client-config-custom.xml";

        File modelFile = new File(DATA_FOLDER + "content-rules" + SLASH, file1).getAbsoluteFile();
        File configFile = new File(DATA_FOLDER + "content-rules" + SLASH, file2).getAbsoluteFile();

        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Add model file to Company Home > Data Dictionary > Models
        ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        ShareUserRepositoryPage.navigateToFolderInRepository(drone,
                REPO + SLASH + DATA_DICTIONARY_FOLDER + SLASH + "Models");
        ShareUserRepositoryPage.uploadFileInRepository(drone, modelFile);

        // Add config file to Company Home > Data Dictionary > Web Client Extension
        ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        ShareUserRepositoryPage.navigateToFolderInRepository(drone,
                REPO + SLASH + DATA_DICTIONARY_FOLDER + SLASH + "Web Client Extension");
        ShareUserRepositoryPage.uploadFileInRepository(drone, configFile);

        RepositoryPage repoPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + DATA_DICTIONARY_FOLDER + SLASH + "Models");
        EditDocumentPropertiesPage editDocPropsPage = repoPage.getFileDirectoryInfo(file1).selectEditProperties().render();
        editDocPropsPage.setModelActive();
        editDocPropsPage.clickSave();

        ShareUser.logout(drone);

    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14872() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String folderName = getFolderName(testName);
        String ruleName = "Rule " + getRandomString(5);
        String allFolder = "All";
        String value = "cm:description";
        String text = getRandomString(5) + " " + getRandomString(5);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Create Folder
        ShareUserSitePage.createFolder(drone, folderName, null);

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

        // Specify any text value
        createRulePage.fillSetValueField(text);
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

    }

    @Test(groups = { "DataPrepEnterpriseOnly" })
    public void dataPrep_AONE_14873() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);

        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.logout(drone);

    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14873() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String folderName = getFolderName(testName);
        String ruleName = "Rule " + getRandomString(5);
        String allFolder = "All";
        String value = "audio:sampleRate";
        String i = getRandomStringWithNumders(2);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Create Folder
        ShareUserSitePage.createFolder(drone, folderName, null);

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

        // Specify any integer value
        createRulePage.fillSetValueField(i);
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

    }

    @Test(groups = { "DataPrepEnterpriseOnly" })
    public void dataPrep_AONE_14874() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);

        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.logout(drone);

    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14874() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String folderName = getFolderName(testName);
        String ruleName = "Rule " + getRandomString(5);
        String allFolder = "All";
        String value = "sys:node-dbid";
        String l = getRandomStringWithNumders(50);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Create Folder
        ShareUserSitePage.createFolder(drone, folderName, null);

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

        // Specify any long value
        createRulePage.fillSetValueField(l);
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

    }

    @Test(groups = { "DataPrepEnterpriseOnly" })
    public void dataPrep_AONE_14875() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);

        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.logout(drone);

    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14875() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String folderName = getFolderName(testName);
        String ruleName = "Rule " + getRandomString(5);
        String allFolder = "All";
        String value = "my:width";
        String f = "3.46f";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Create Folder
        ShareUserSitePage.createFolder(drone, folderName, null);

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

        // Specify any float value
        createRulePage.fillSetValueField(f);
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

    }

    @Test(groups = { "DataPrepEnterpriseOnly" })
    public void dataPrep_AONE_14876() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);

        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.logout(drone);

    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14876() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String folderName = getFolderName(testName);
        String ruleName = "Rule " + getRandomString(5);
        String allFolder = "All";
        String value = "my:height";
        String d = "3.46e10";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Create Folder
        ShareUserSitePage.createFolder(drone, folderName, null);

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

        // Specify any double value
        createRulePage.fillSetValueField(d);
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

    }

    @Test(groups = { "DataPrepEnterpriseOnly" })
    public void dataPrep_AONE_14877() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);

        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.logout(drone);

    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14877() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String folderName = getFolderName(testName);
        String ruleName = "Rule " + getRandomString(5);
        String allFolder = "All";
        String value = "audio:releaseDate";
        String date = "21";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Create Folder
        ShareUserSitePage.createFolder(drone, folderName, null);

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
        SetPropertyValuePage setPropertyValuePage = new SetPropertyValuePage(drone);

        // Specify a date
        setPropertyValuePage.clickCalendarButton();
        setPropertyValuePage.setDate(date);
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

    }

    @Test(groups = { "DataPrepEnterpriseOnly" })
    public void dataPrep_AONE_14878() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);

        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.logout(drone);

    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14878() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String folderName = getFolderName(testName);
        String ruleName = "Rule " + getRandomString(5);
        String allFolder = "All";
        String value = "app:editInline";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Create Folder
        ShareUserSitePage.createFolder(drone, folderName, null);

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

        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

    }

    @Test(groups = { "DataPrepEnterpriseOnly" })
    public void dataPrep_AONE_14879() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);

        // Create User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.logout(drone);

    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14879() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String folderName = getFolderName(testName);
        String ruleName = "Rule " + getRandomString(5);
        String allFolder = "All";
        String value = "my:resolution";
        String input = "app:company_home";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Create Folder
        ShareUserSitePage.createFolder(drone, folderName, null);

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

        // Specify value
        createRulePage.fillSetValueField(input);
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

    }

}
