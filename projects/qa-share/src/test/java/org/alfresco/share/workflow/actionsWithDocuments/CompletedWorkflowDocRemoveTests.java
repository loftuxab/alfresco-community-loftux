package org.alfresco.share.workflow.actionsWithDocuments;

import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.CopyOrMoveContentPage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.site.document.EditTextDocumentPage;
import org.alfresco.po.share.task.TaskStatus;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.workflow.CloudTaskOrReviewPage;
import org.alfresco.po.share.workflow.KeepContentStrategy;
import org.alfresco.po.share.workflow.MyWorkFlowsPage;
import org.alfresco.po.share.workflow.Priority;
import org.alfresco.po.share.workflow.TaskType;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.EditTaskAction;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.api.CreateUserAPI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class CompletedWorkflowDocRemoveTests extends AbstractWorkflow
{
    private String testDomain;
    private String completeWorkflowTests = "complete_workflow_tests_";
    private static Log logger = LogFactory.getLog(CompletedWorkflowDocRemoveTests.class);
    DocumentLibraryPage documentLibraryPage;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testDomain = DOMAIN_HYBRID;
    }

    private void dataPrep(String testName) throws Exception
    {
        String user1 = getUserNameForDomain(completeWorkflowTests + "OP", testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(completeWorkflowTests + "CL", testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, DOMAIN_HYBRID, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
    }

    /**
     * Data preparation for Complete Workflow Tests
     */
    @Test(groups = "DataPrepHybrid")
    public void dataPrep_createUsers() throws Exception
    {
        dataPrep(completeWorkflowTests);
    }

    /**
     * AONE-15710: Modify properties (OP)
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15710() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(completeWorkflowTests + "OP", testDomain);
        String cloudUser = getUserNameForDomain(completeWorkflowTests + "CL", testDomain);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "2-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "2-CL";

        String simpleTaskFile = getFileName(testName) + ".txt";
        String[] fileInfo = { simpleTaskFile, DOCLIB };

        String simpleTaskWF = testName + System.currentTimeMillis() + "-WF";
        String dueDate = getDueDateString();

        String modifiedTitle = testName + "modified";
        String modifiedDescription = simpleTaskFile + " modified";

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Create Site
        SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();

        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        // Select "Cloud Task or Review" from select a workflow drop down
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, simpleTaskFile).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);
        formDetails.setMessage(simpleTaskWF);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        cloudTaskOrReviewPage.startWorkflow(formDetails).render(maxWaitTimeCloudSync);

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        ShareUser.checkIfTaskIsPresent(hybridDrone, simpleTaskWF);
        ShareUserWorkFlow.completeWorkFlow(hybridDrone, cloudUser, simpleTaskWF);
        ShareUser.logout(hybridDrone);

        // ---- Step 1 ----
        // ---- Step action ---
        // OP Modify the document's properties, e.g. change title and description.
        // ---- Expected results ----
        // The properties are changed successfully

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        ShareUser.checkIfTaskIsPresent(drone, simpleTaskWF);
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(drone, simpleTaskWF, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);
        myTasksPage.selectCompletedTasks().render();
        Assert.assertTrue(myTasksPage.isTaskPresent(simpleTaskWF));

        EditDocumentPropertiesPage editDocumentProperties = ShareUserSitePage.getEditPropertiesFromDocLibPage(drone, opSiteName, simpleTaskFile);
        editDocumentProperties.setDocumentTitle(modifiedTitle);
        editDocumentProperties.setDescription(modifiedDescription);
        editDocumentProperties.selectSave().render();

        ShareUser.logout(drone);

        // ---- Step 2 ----
        // ---- Step action ---
        // Check the logs in OP and in Cloud
        // ---- Expected results ----
        // The logs contain no error messages. The changes are not synchronized

        // TODO : Please update 2nd step in TestLink as verifying in logs is not present.

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone).selectCompletedWorkFlows();
        Assert.assertTrue(myTasksPage.isTaskPresent(simpleTaskWF));
        ShareUser.logout(drone);

        // ---- Step 3 ----
        // ---- Step action ---
        // Cloud Verify the document
        // ---- Expected results ----
        // The document is absent

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSite).render();
        Assert.assertFalse(documentLibraryPage.isFileVisible(simpleTaskFile));
        ShareUser.logout(hybridDrone);
    }

    /**
     * AONE-15711: Modify properties (OP)
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15711() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(completeWorkflowTests + "OP", testDomain);
        String cloudUser = getUserNameForDomain(completeWorkflowTests + "CL", testDomain);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "2-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "2-CL";

        String simpleTaskFile = getFileName(testName) + ".txt";
        String[] fileInfo = { simpleTaskFile, DOCLIB };

        String simpleTaskWF = testName + System.currentTimeMillis() + "-WF";
        String dueDate = getDueDateString();

        String modifiedContentByOnPrem = testName + " modifiedBy " + user1;

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Create Site
        SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();

        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        // Select "Cloud Task or Review" from select a workflow drop down
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, simpleTaskFile).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);
        formDetails.setMessage(simpleTaskWF);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        cloudTaskOrReviewPage.startWorkflow(formDetails).render(maxWaitTimeCloudSync);

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        ShareUser.checkIfTaskIsPresent(hybridDrone, simpleTaskWF);
        ShareUserWorkFlow.completeWorkFlow(hybridDrone, cloudUser, simpleTaskWF);
        ShareUser.logout(hybridDrone);

        // ---- Step 1 ----
        // ---- Step action ---
        // OP Modify the document's content
        // ---- Expected results ----
        // The content is changed successfully

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        ShareUser.checkIfTaskIsPresent(drone, simpleTaskWF);
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(drone, simpleTaskWF, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);
        myTasksPage = myTasksPage.selectCompletedTasks().render();
        Assert.assertTrue(myTasksPage.isTaskPresent(simpleTaskWF));

        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(simpleTaskFile);

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setContent(modifiedContentByOnPrem);
        contentDetails.setName(simpleTaskFile);
        // Select Inline Edit and change the content and save
        EditTextDocumentPage inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        documentDetailsPage = inlineEditPage.save(contentDetails).render();

        ShareUser.logout(drone);

        // ---- Step 2 ----
        // ---- Step action ---
        // Check the logs in OP and in Cloud
        // ---- Expected results ----
        // The logs contain no error messages. The changes are not synchronized

        // TODO : Please update 2nd step in TestLink as verifying in logs is not present.

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone).selectCompletedWorkFlows().render();
        Assert.assertTrue(myWorkFlowsPage.isWorkFlowPresent(simpleTaskWF));
        ShareUser.logout(drone);

        // ---- Step 3 ----
        // ---- Step action ---
        // Cloud Verify the document
        // ---- Expected results ----
        // The document is absent

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSite).render();
        Assert.assertFalse(documentLibraryPage.isFileVisible(simpleTaskFile));
        ShareUser.logout(hybridDrone);
    }

    /**
     * AONE-15712: OP Move the document to another location
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15712() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(completeWorkflowTests + "OP", testDomain);
        String cloudUser = getUserNameForDomain(completeWorkflowTests + "CL", testDomain);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "2-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "2-CL";

        String simpleTaskFile = getFileName(testName) + ".txt";
        String[] fileInfo = { simpleTaskFile, DOCLIB };

        String simpleTaskWF = testName + System.currentTimeMillis() + "-WF";
        String dueDate = getDueDateString();
        String folderName = getFolderName(testName);

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Create Site
        SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();

        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        // Select "Cloud Task or Review" from select a workflow drop down
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, simpleTaskFile).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);
        formDetails.setMessage(simpleTaskWF);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        cloudTaskOrReviewPage.startWorkflow(formDetails).render(maxWaitTimeCloudSync);

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        ShareUser.checkIfTaskIsPresent(hybridDrone, simpleTaskWF);
        ShareUserWorkFlow.completeWorkFlow(hybridDrone, cloudUser, simpleTaskWF);
        ShareUser.logout(hybridDrone);

        // ---- Step 1 ----
        // ---- Step action ---
        // OP Move the document to another location
        // ---- Expected results ----
        // The content is moved successfully.

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone).render();
        ShareUser.checkIfTaskIsPresent(drone, simpleTaskWF);
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(drone, simpleTaskWF, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);
        myTasksPage = myTasksPage.selectCompletedTasks().render();
        Assert.assertTrue(myTasksPage.isTaskPresent(simpleTaskWF));

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        CopyOrMoveContentPage moveToPage = documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).selectMoveTo().render();
        moveToPage.selectPath(folderName).render().selectOkButton().render();

        ShareUser.logout(drone);

        // ---- Step 2 ----
        // ---- Step action ---
        // Check the logs in OP and in Cloud
        // ---- Expected results ----
        // The logs contain no error messages. The changes are not synchronized

        // TODO : Please update 2nd step in TestLink as verifying in logs is not present.

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone).selectCompletedWorkFlows();
        Assert.assertTrue(myTasksPage.isTaskPresent(simpleTaskWF));
        ShareUser.logout(drone);

        // ---- Step 3 ----
        // ---- Step action ---
        // Cloud Verify the document
        // ---- Expected results ----
        // The document is absent

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSite).render();
        Assert.assertFalse(documentLibraryPage.isFileVisible(simpleTaskFile));
        ShareUser.logout(hybridDrone);
    }

    /**
     * AONE-15713: OP Remove the document
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15713() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(completeWorkflowTests + "OP", testDomain);
        String cloudUser = getUserNameForDomain(completeWorkflowTests + "CL", testDomain);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "2-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "2-CL";

        String simpleTaskFile = getFileName(testName) + ".txt";
        String[] fileInfo = { simpleTaskFile, DOCLIB };

        String simpleTaskWF = testName + System.currentTimeMillis() + "-WF";
        String dueDate = getDueDateString();

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Create Site
        SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();

        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        // Select "Cloud Task or Review" from select a workflow drop down
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, simpleTaskFile).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);
        formDetails.setMessage(simpleTaskWF);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        cloudTaskOrReviewPage.startWorkflow(formDetails).render(maxWaitTimeCloudSync);

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        ShareUser.checkIfTaskIsPresent(hybridDrone, simpleTaskWF);
        ShareUserWorkFlow.completeWorkFlow(hybridDrone, cloudUser, simpleTaskWF);
        ShareUser.logout(hybridDrone);

        // ---- Step 1 ----
        // ---- Step action ---
        // OP Remove the document
        // ---- Expected results ----
        // The document is removed successfully

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone).render();
        ShareUser.checkIfTaskIsPresent(drone, simpleTaskWF);
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(drone, simpleTaskWF, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);
        myTasksPage = myTasksPage.selectCompletedTasks().render();
        Assert.assertTrue(myTasksPage.isTaskPresent(simpleTaskWF));

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).selectCheckbox();
        documentLibraryPage = ShareUser.deleteSelectedContent(drone).render();
        Assert.assertFalse(documentLibraryPage.isFileVisible(simpleTaskFile));

        ShareUser.logout(drone);

        // ---- Step 2 ----
        // ---- Step action ---
        // Check the logs in OP and in Cloud
        // ---- Expected results ----
        // The logs contain no error messages. The changes are not synchronized

        // TODO : Please update 2nd step in TestLink as verifying in logs is not present.

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone).selectCompletedWorkFlows();
        Assert.assertTrue(myTasksPage.isTaskPresent(simpleTaskWF));
        ShareUser.logout(drone);

        // ---- Step 3 ----
        // ---- Step action ---
        // Cloud Verify the document
        // ---- Expected results ----
        // The document is absent

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSite).render();
        Assert.assertFalse(documentLibraryPage.isFileVisible(simpleTaskFile));
        ShareUser.logout(hybridDrone);
    }
}
