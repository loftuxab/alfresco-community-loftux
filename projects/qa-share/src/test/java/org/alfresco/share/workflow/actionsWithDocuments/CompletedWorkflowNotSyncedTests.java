package org.alfresco.share.workflow.actionsWithDocuments;

import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.site.document.*;
import org.alfresco.po.share.task.TaskStatus;
import org.alfresco.po.share.workflow.*;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class CompletedWorkflowNotSyncedTests extends AbstractWorkflow
{
    protected String testUser;
    private String testDomain;
    private String opUser;
    private String cloudUser;
    protected String siteName = "";
    DocumentLibraryPage documentLibraryPage;
    protected String testName = "";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testDomain = DOMAIN_HYBRID;

        opUser = getUserNameForDomain(testName + "opUser", testDomain);
        cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);

    }

    @BeforeClass(groups = "DataPrepHybridWorkflow", dependsOnMethods = "setup")
    public void dataPrep_createUsers() throws Exception
    {
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser };
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

    }

    private void createCompletedWorkflow(String prefix) throws Exception
    {
        testName = this.getClass().getSimpleName();

        List<String> userNames = new ArrayList<String>();
        userNames.add(cloudUser);

        String opSiteName = getSiteName(prefix + testName) + "-OP";
        String cloudSiteName = getSiteName(prefix + testName) + "-CL";

        String fileName = getFileName(prefix + testName) + ".txt";
        String folderName = getFolderName(prefix + testName);

        String workFlowName = prefix + testName + "-WF";

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUserSitePage.createFolder(hybridDrone, folderName, folderName);
        ShareUser.logout(hybridDrone);

        // User1 starts Workflow
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setMessage(workFlowName);

        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setReviewers(userNames);
        formDetails.setApprovalPercentage(100);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENTREMOVESYNC);

        // Start Cloud Task or Review workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startCloudReviewTaskWorkFlow(drone);

        cloudTaskOrReviewPage.selectItem(fileName, opSiteName);
        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails);

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).render();
        ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName);
        myTasksPage.navigateToEditTaskPage(workFlowName).render();
        ShareUserWorkFlow.completeTask(hybridDrone, TaskStatus.COMPLETED, EditTaskAction.APPROVE).render();
        ShareUser.logout(hybridDrone);

        // Login as OP user
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone).render();
        ShareUser.checkIfTaskIsPresent(drone, workFlowName);
        myTasksPage.navigateToEditTaskPage(workFlowName).render();
        ShareUserWorkFlow.completeTask(drone, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE).render();
        ShareUser.logout(drone);

    }

    @Test(groups = "DataPrepHybrid", timeOut = 600000)
    public void dataPrep_AONE_15700() throws Exception
    {

        createCompletedWorkflow("15700" + "A4");
    }

    /**
     * AONE-15700:Modify properties (OP)
     */

    @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
    public void AONE_15700() throws Exception
    {

        String prefix = "15700" + "A4";
        String opSiteName = getSiteName(prefix + testName) + "-OP";
        String cloudSiteName = getSiteName(prefix + testName) + "-CL";

        String fileName = getFileName(prefix + testName) + ".txt";

        String modifiedFileTitle = testName + "modifiedBy ";
        String descOfFile = fileName + " modified by ";

        // --- Step 1 ---
        // --- Step action ---
        // OP Modify the synced document's properties, e.g. change title and description.
        // --- Expected results ---
        // The properties are changed successfully..

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        EditDocumentPropertiesPage editDocumentProperties = ShareUserSitePage.getEditPropertiesFromDocLibPage(drone, opSiteName, fileName);
        editDocumentProperties.setDocumentTitle(modifiedFileTitle + opUser);
        editDocumentProperties.setDescription(descOfFile + opUser);
        editDocumentProperties.selectSave().render();

        ShareUser.logout(drone);

        // --- Step 3 ---
        // --- Step action ---
        // Cloud Verify the document.
        // --- Expected results ---
        // Changes didn't appear to Cloud.

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        editDocumentProperties = ShareUserSitePage.getEditPropertiesFromDocLibPage(hybridDrone, cloudSiteName, fileName);

        Assert.assertFalse((modifiedFileTitle + opUser).equals(editDocumentProperties.getDocumentTitle()),
                "Document Title modified by OP User is not present for Cloud.");
        Assert.assertFalse((descOfFile + opUser).equals(editDocumentProperties.getDescription()),
                "Document Description modified by OP User is not present for Cloud.");

        ShareUser.logout(hybridDrone);

    }

    @Test(groups = "DataPrepHybrid", timeOut = 600000)
    public void dataPrep_AONE_15701() throws Exception
    {

        createCompletedWorkflow("15701" + "A4");
    }

    /**
     * AONE-15701:Modify properties (Cloud)
     */

    @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
    public void AONE_15701() throws Exception
    {

        String prefix = "15701" + "A4";
        String opSiteName = getSiteName(prefix + testName) + "-OP";
        String cloudSiteName = getSiteName(prefix + testName) + "-CL";
        String fileName = getFileName(prefix + testName) + ".txt";

        String modifiedFileTitle = testName + "modifiedBy ";
        String descOfFile = fileName + " modified by ";

        // --- Step 1 ---
        // --- Step action ---
        // Cloud Modify the synced document's properties, e.g. change title and description.
        // --- Expected results ---
        // The properties are changed successfully..

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        EditDocumentPropertiesPage editDocumentProperties = ShareUserSitePage.getEditPropertiesFromDocLibPage(hybridDrone, cloudSiteName, fileName);
        editDocumentProperties.setDocumentTitle(modifiedFileTitle + cloudUser);
        editDocumentProperties.setDescription(descOfFile + cloudUser);
        editDocumentProperties.selectSave().render();

        ShareUser.logout(hybridDrone);
        // --- Step 3 ---
        // --- Step action ---
        // Cloud Verify the document.
        // --- Expected results ---
        // Changes didn't appear to Cloud.

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

        editDocumentProperties = ShareUserSitePage.getEditPropertiesFromDocLibPage(drone, opSiteName, fileName);

        Assert.assertFalse((modifiedFileTitle + cloudUser).equals(editDocumentProperties.getDocumentTitle()),
                "Document Title modified by Cloud User is not present for OP.");
        Assert.assertFalse((descOfFile + cloudUser).equals(editDocumentProperties.getDescription()),
                "Document Description modified by CLoud User is not present for OP.");

        ShareUser.logout(drone);

    }

    @Test(groups = "DataPrepHybrid", timeOut = 600000)
    public void dataPrep_AONE_15702() throws Exception
    {

        createCompletedWorkflow("15702" + "A4");
    }

    /**
     * AONE-15702:Modify content (OP)
     */

    @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
    public void AONE_15702() throws Exception
    {

        String prefix = "15702" + "A4";
        String opSiteName = getSiteName(prefix + testName) + "-OP";
        String cloudSiteName = getSiteName(prefix + testName) + "-CL";

        String fileName = getFileName(prefix + testName) + ".txt";

        String modifiedContentByOnPrem = testName + " modifiedBy: OP User" + "A4";

        // --- Step 1 ---
        // --- Step action ---
        // OP Modify the synced document's content.
        // --- Expected results ---
        // The content is changed successfully.

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(fileName);

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setContent(modifiedContentByOnPrem);
        contentDetails.setName(fileName);

        // Select Inline Edit and change the content and save
        EditTextDocumentPage inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        inlineEditPage.save(contentDetails).render();

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // --- Step 3 ---
        // --- Step action ---
        // Cloud Verify the document.
        // --- Expected results ---
        // Changes didn't appear to Cloud.

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);

        documentDetailsPage = documentLibraryPage.selectFile(fileName);
        inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        Assert.assertFalse(inlineEditPage.getDetails().getContent().contains(modifiedContentByOnPrem), "Content has been changed on Cloud but sync is removed");
        ShareUser.logout(hybridDrone);

    }

    @Test(groups = "DataPrepHybrid", timeOut = 600000)
    public void dataPrep_AONE_15703() throws Exception
    {

        createCompletedWorkflow("15703" + "A4");
    }

    /**
     * AONE-15703:Modify content (Cloud)
     */

    @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
    public void AONE_15703() throws Exception
    {

        String prefix = "15703" + "A4";
        String opSiteName = getSiteName(prefix + testName) + "-OP";
        String cloudSiteName = getSiteName(prefix + testName) + "-CL";

        String fileName = getFileName(prefix + testName) + ".txt";

        String modifiedContentByCloud = testName + " modifiedBy: Cloud User";

        // --- Step 1 ---
        // --- Step action ---
        // Cloud Modify the synced document's content.
        // --- Expected results ---
        // The content is changed successfully.

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);

        DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(fileName);
        EditTextDocumentPage inlineEditPage = documentDetailsPage.selectInlineEdit().render();

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setContent(modifiedContentByCloud);
        contentDetails.setName(fileName);

        inlineEditPage.save(contentDetails).render();
        ShareUser.logout(hybridDrone);

        // --- Step 2 ---
        // --- Step action ---
        // Check the logs in OP and in Cloud.
        // --- Expected results ---
        // The logs contain no error messages. The changes are not synchronized.

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

        // --- Step 3 ---
        // --- Step action ---
        // OP Verify the document.
        // --- Expected results ---
        // Changes didn't appear to OP.

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);
        documentDetailsPage = documentLibraryPage.selectFile(fileName);
        inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        Assert.assertFalse(inlineEditPage.getDetails().getContent().contains(modifiedContentByCloud), "Content has been changed on OP but sync is removed");
        ShareUser.logout(drone);

    }

    @Test(groups = "DataPrepHybrid", timeOut = 600000)
    public void dataPrep_AONE_15704() throws Exception
    {

        createCompletedWorkflow("15704" + "A4");
    }

    /**
     * AONE-15704:Move (OP)
     */

    @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
    public void AONE_15704() throws Exception
    {

        String prefix = "15704" + "A4";
        String opSiteName = getSiteName(prefix + testName) + "-OP";
        String cloudSiteName = getSiteName(prefix + testName) + "-CL";

        String fileName = getFileName(prefix + testName) + ".txt";
        String folderName = getFolderName(prefix + testName);

        // --- Step 1 ---
        // --- Step action ---
        // OP Move the synced document to another location.
        // --- Expected results ---
        // The content is moved successfully.

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(drone);

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        CopyOrMoveContentPage moveToPage = documentLibraryPage.getFileDirectoryInfo(fileName).selectMoveTo().render();
        moveToPage.selectPath(folderName).render().selectOkButton().render();
        ShareUser.logout(drone);
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // --- Step 3 ---
        // --- Step action ---
        // Cloud Verify the document.
        // --- Expected results ---
        // The document is not synced.

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Content has been synced to Cloud during moving on OP");
        ShareUser.logout(hybridDrone);

    }

    @Test(groups = "DataPrepHybrid", timeOut = 600000)
    public void dataPrep_AONE_15705() throws Exception
    {

        createCompletedWorkflow("15705" + "A4");
    }

    /**
     * AONE-15705:Move (Cloud)
     */

    @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
    public void AONE_15705() throws Exception
    {

        String prefix = "15705" + "A4";
        String opSiteName = getSiteName(prefix + testName) + "-OP";
        String cloudSiteName = getSiteName(prefix + testName) + "-CL";

        String fileName = getFileName(prefix + testName) + ".txt";
        String folderName = getFolderName(prefix + testName);

        // --- Step 1 ---
        // --- Step action ---
        // Cloud Move the synced document to another location.
        // --- Expected results ---
        // The content is moved successfully.

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        CopyOrMoveContentPage moveToPage = documentLibraryPage.getFileDirectoryInfo(fileName).selectMoveTo().render();
        moveToPage.selectPath(folderName).render().selectOkButton().render();
        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

        // --- Step 3 ---
        // --- Step action ---
        // OP Verify the document.
        // --- Expected results ---
        // The document is not synced.

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Content has been changed on OP during moving on Cloud");
        ShareUser.logout(drone);

    }

    @Test(groups = "DataPrepHybrid", timeOut = 600000)
    public void dataPrep_AONE_15706() throws Exception
    {

        createCompletedWorkflow("15706" + "A4");
    }

    /**
     * AONE-15706:Remove (OP)
     */

    @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
    public void AONE_15706() throws Exception
    {

        String prefix = "15706" + "A4";
        String opSiteName = getSiteName(prefix + testName) + "-OP";
        String cloudSiteName = getSiteName(prefix + testName) + "-CL";
        String fileName = getFileName(prefix + testName) + ".txt";

        // --- Step 1 ---
        // --- Step action ---
        // OP Remove the synced document.
        // --- Expected results ---
        // The content is removed successfully.

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        documentLibraryPage.getFileDirectoryInfo(fileName).selectCheckbox();
        documentLibraryPage = ShareUser.deleteSelectedContent(drone).render();
        Assert.assertFalse(documentLibraryPage.isFileVisible(fileName));
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // --- Step 3 ---
        // --- Step action ---
        // Cloud Verify the document.
        // --- Expected results ---
        // The document is not synced.

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Content has been synced to Cloud removing on OP");
        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "DataPrepHybrid", timeOut = 600000)
    public void dataPrep_AONE_15707() throws Exception
    {

        createCompletedWorkflow("15707" + "A4");

    }

    /**
     * AONE-15707:Remove (Cloud)
     */

    @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
    public void AONE_15707() throws Exception
    {

        String prefix = "15707" + "A4";
        String opSiteName = getSiteName(prefix + testName) + "-OP";
        String cloudSiteName = getSiteName(prefix + testName) + "-CL";
        String fileName = getFileName(prefix + testName) + ".txt";

        // --- Step 1 ---
        // --- Step action ---
        // Cloud Remove the synced document.
        // --- Expected results ---
        // The content is removed.

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        documentLibraryPage.getFileDirectoryInfo(fileName).selectCheckbox();
        documentLibraryPage = ShareUser.deleteSelectedContent(drone).render();
        Assert.assertFalse(documentLibraryPage.isFileVisible(fileName));
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // --- Step 3 ---
        // --- Step action ---
        // OP Verify the document.
        // --- Expected results ---
        // The document is not synced.

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Content has been synced to Cloud during removing on OP");
        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "DataPrepHybrid", timeOut = 600000)
    public void dataPrep_AONE_15708() throws Exception
    {

        createCompletedWorkflow("15708" + "A4");
    }

    /**
     * AONE-15708:UnSync (OP)
     */

    @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
    public void AONE_15708() throws Exception
    {

        String prefix = "15708" + "A4";
        String opSiteName = getSiteName(prefix + testName) + "-OP";
        String cloudSiteName = getSiteName(prefix + testName) + "-CL";
        String fileName = getFileName(prefix + testName) + ".txt";

        // --- Step 1 ---
        // --- Step action ---
        // OP UnSync the synced document.
        // --- Expected results ---
        // UnSync action is not available.

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isUnSyncFromCloudLinkPresent());
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced());
        ShareUser.logout(drone);

        // --- Step 2 ---
        // --- Step action ---
        // Cloud Verify the document.
        // --- Expected results ---
        // The document is not synced.

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Content is synced for cloud");
        ShareUser.logout(hybridDrone);

    }

    @Test(groups = "DataPrepHybrid", timeOut = 600000)
    public void dataPrep_AONE_15709() throws Exception
    {

        createCompletedWorkflow("15709" + "A4");
    }

    /**
     * AONE-15709:UnSync (Cloud)
     */

    @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
    public void AONE_15709() throws Exception
    {

        String prefix = "15709" + "A4";
        String opSiteName = getSiteName(prefix + testName) + "-OP";
        String cloudSiteName = getSiteName(prefix + testName) + "-CL";
        String fileName = getFileName(prefix + testName) + ".txt";

        // --- Step 1 ---
        // --- Step action ---
        // Cloud UnSync the synced document.
        // --- Expected results ---
        // UnSync action is not available.

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isUnSyncFromCloudLinkPresent());
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced());
        ShareUser.logout(hybridDrone);

        // --- Step 2 ---
        // --- Step action ---
        // OP Verify the document.
        // --- Expected results ---
        // The document is not synced.

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Content is synced for OP");
        ShareUser.logout(drone);

    }

}
