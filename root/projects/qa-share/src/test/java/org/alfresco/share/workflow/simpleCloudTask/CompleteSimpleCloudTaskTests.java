package org.alfresco.share.workflow.simpleCloudTask;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.task.EditTaskPage;
import org.alfresco.po.share.task.EditTaskPage.Button;
import org.alfresco.po.share.task.TaskDetailsPage;
import org.alfresco.po.share.task.TaskInfo;
import org.alfresco.po.share.task.TaskItem;
import org.alfresco.po.share.task.TaskStatus;
import org.alfresco.po.share.workflow.CloudTaskOrReviewPage;
import org.alfresco.po.share.workflow.KeepContentStrategy;
import org.alfresco.po.share.workflow.MyWorkFlowsPage;
import org.alfresco.po.share.workflow.Priority;
import org.alfresco.po.share.workflow.TaskHistoryPage;
import org.alfresco.po.share.workflow.TaskType;
import org.alfresco.po.share.workflow.WorkFlowDetailsGeneralInfo;
import org.alfresco.po.share.workflow.WorkFlowDetailsHistory;
import org.alfresco.po.share.workflow.WorkFlowDetailsPage;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.po.share.workflow.WorkFlowHistoryOutCome;
import org.alfresco.po.share.workflow.WorkFlowHistoryType;
import org.alfresco.po.share.workflow.WorkFlowStatus;
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.EditTaskAction;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.api.CreateUserAPI;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CompleteSimpleCloudTaskTests extends AbstractWorkflow
{
    private String testDomain;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testDomain = DOMAIN_HYBRID;
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15613() throws Exception
    {
        String testName = getTestName();
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser };
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };
        String folderName = getFolderName(testName);
        String cloudSite = getSiteName(testName + "CL");
        String opSite = getSiteName(testName + "OP");
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        String workFlowName = "Simple Cloud Task " + testName;

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
        ShareUserSitePage.createFolder(hybridDrone, folderName, folderName);
        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, opSite).render();
        ShareUser.uploadFileInFolder(drone, fileInfo).render();
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName).render();
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setMessage(workFlowName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENTREMOVESYNC);
        formDetails.setLockOnPremise(false);

        // Create Workflow using File1
        cloudTaskOrReviewPage.startWorkflow(formDetails).render();
        checkIfContentIsSynced(drone, fileName);
    }

    /**
     * AONE-15613:Simple Cloud Task - Task Done
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
    public void AONE_15613() throws Exception
    {

        String testName = getTestName();
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
        String workFlowName = "Simple Cloud Task " + testName;
        EditTaskPage editTaskPage;

        try
        {
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // --- Step 1 ---
            // --- Step action ---
            // Cloud Click on Edit button.
            // --- Expected results ---
            // The button is pressed. Edit Task page is opened.
            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).render();
            editTaskPage = myTasksPage.navigateToEditTaskPage(workFlowName);
            assertTrue(editTaskPage.isBrowserTitle("Edit Task"), "Wrong page");

            // --- Step 2 ---
            // --- Step action ---
            // Add any data into the Comment field, e.g. "test comment edited".
            // --- Expected results ---
            // Performed correctly.
            editTaskPage.enterComment("test comment edited");
            assertEquals(editTaskPage.readCommentFromCommentTextArea(), "test comment edited", "Wrong comment");

            // --- Step 3 ---
            // --- Step action ---
            // Specify any value in the Status drop-down list, e.g. 'Completed'.
            // --- Expected results ---
            // Performed correctly.
            editTaskPage.selectStatusDropDown(TaskStatus.COMPLETED);
            assertEquals(editTaskPage.getSelectedStatusFromDropDown(), TaskStatus.COMPLETED, "Status not completed");

            // --- Step 4 ---
            // --- Step action ---
            // Click on Task Done button.
            // --- Expected results ---
            // Task is closed. It is disappeared from the Active Tasks.
            myTasksPage = editTaskPage.selectTaskDoneButton().render();
            Assert.assertTrue(myTasksPage.isBrowserTitle("My Tasks"), "Wrong page");
            Assert.assertFalse(myTasksPage.isTaskPresent(workFlowName), "Task is displayed");
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-CL", t);
        }

        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15614() throws Exception
    {
        String testName = getTestName();
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser };
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };
        String cloudSite = getSiteName(testName + "CL");
        String opSite = getSiteName(testName + "OP");
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        String workFlowName = "Simple Cloud Task " + testName;

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD).render();
        ShareUser.createSite(drone, opSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, opSite).render();
        ShareUser.uploadFileInFolder(drone, fileInfo).render();
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName).render();
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setMessage(workFlowName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENTREMOVESYNC);
        formDetails.setLockOnPremise(false);

        // Create Workflow using File1
        cloudTaskOrReviewPage.startWorkflow(formDetails).render();
        checkIfContentIsSynced(drone, fileName);

        // complete task on cloud
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).render();
        ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, workFlowName, TaskStatus.INPROGRESS, testName + "comment", EditTaskAction.TASK_DONE);
        ShareUser.logout(hybridDrone);
    }

    /**
     * AONE-15614: Simple Cloud Task - Task Done - Details
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
    public void AONE_15614() throws Exception
    {
        String testName = getTestName();
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
        String fileName = getFileName(testName) + ".txt";
        String workFlowName = "Simple Cloud Task " + testName;
        TaskDetailsPage taskDetailsPage;

        try
        {
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).render();

            // --- Step 1 ---
            // --- Step action ---
            // Cloud Verify My Tasks page.
            // --- Expected results ---
            // A completed task, e.g. "Simple Cloud Task test message", is present in the Completed Tasks filter.
            myTasksPage.selectCompletedTasks().render();
            Assert.assertTrue(myTasksPage.isTaskPresent(workFlowName), "Task is not displayed");

            // --- Step 2 ---
            // --- Step action ---
            // Open Task Details page.
            // --- Expected results ---
            // Details page is opened.
            taskDetailsPage = myTasksPage.selectViewTasks(workFlowName).render(maxWaitTime);
            assertTrue(taskDetailsPage.isBrowserTitle("Task Details"), "Wrong Page");

            // --- Step 3 ---
            // --- Step action ---
            // Verify the changed data
            // --- Expected results ---
            // The following changes are present:
            // Edit button is absent under the information details section.
            // Status: 'Completed' in the Progress section
            TaskStatus status = taskDetailsPage.getTaskStatus();
            Assert.assertTrue(status.getTaskName().equals("Completed"));
            Assert.assertFalse(taskDetailsPage.isEditButtonPresent());
            Assert.assertEquals(taskDetailsPage.getComment(), testName + "comment");

            // --- Step 4 ---
            // --- Step action ---
            // Cloud Open Task History page.
            // --- Expected results ---
            // Details page is opened."
            myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
            myTasksPage.selectCompletedTasks().render();
            TaskHistoryPage historyPage = myTasksPage.selectTaskHistory(workFlowName).render();
            Assert.assertTrue(historyPage.getTitle().contains("History"));

            // --- Step 5 ---
            // --- Step action ---
            // Verify the changed data.
            // --- Expected results ---
            // The following changes are present:
            // Completed: Thu 12 Sep 2013 20:26:03 in the General Info section
            // Status: Task is Complete in the General Info section
            // Delete Workflow button
            Assert.assertTrue(historyPage.isDeleteWorkFlowButtonDisplayed());
            List<WorkFlowDetailsHistory> historyList = historyPage.getWorkFlowHistoryList();
            assertEquals(historyList.get(0).getCompletedBy(), getUserFullName(cloudUser), "User Name is not correct for WorkFlow Details History List");
            assertEquals(getLocalDate(historyList.get(0).getCompletedDate()), getToDaysLocalDate(),
                    "Today local date is not correct for WorkFlow Details History List");
            WorkFlowDetailsGeneralInfo generalInfo = historyPage.getWorkFlowDetailsGeneralInfo();
            assertTrue(generalInfo.getStatus().equals(WorkFlowStatus.TASK_COMPLETE));

            // --- Step 6 ---
            // --- Step action ---
            // Verify 'Current Tasks' section.
            // --- Expected results ---
            // The following data is displayed: No tasks
            assertTrue(historyPage.isNoTasksMessageDisplayed(), "No tasks message is not displayed");

            // --- Step 7 ---
            // --- Step action ---
            // Verify 'History' section.
            // --- Expected results ---
            // The following data is displayed:
            // Task Administrator admin Thu 12 Sep 2013 20:26:02 Task Done test comment edited
            // Task Administrator admin Thu 12 Sep 2013 17:17:42 Task Done
            assertEquals(historyList.get(0).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE, "Workflow History outcome is not correct");
            assertEquals(historyList.get(0).getComment(), testName + "comment", "Comment not displayed");

            // --- Step 8 ---
            // --- Step action ---
            // OPOpen workflow details page.
            // --- Expected results ---
            // Details page is opened."
            ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
            myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
            taskDetailsPage = myTasksPage.selectViewTasks(workFlowName).render(maxWaitTime);
            assertTrue(taskDetailsPage.isBrowserTitle("Task Details"), "Wrong Page");

            // --- Step 9 ---
            // --- Step action ---
            // Verify 'Current Tasks' section.
            // --- Expected results ---
            // The following data is displayed:
            // Verify task was completed on the cloud admin (None) Not Yet Started
            status = taskDetailsPage.getTaskStatus();
            assertTrue(status.equals(TaskStatus.NOTYETSTARTED));

            // --- Step 10 ---
            // --- Step action ---
            // OP Verify My Tasks page.
            // --- Expected results ---
            // An active task, e.g. "Simple Cloud Task test message", is present in the Active Tasks filter.
            myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
            assertTrue(ShareUser.checkIfTaskIsPresent(drone, workFlowName), "Task is not present");

            // --- Step 11 ---
            // --- Step action ---
            // Open the task details.
            // --- Expected results ---
            // Performed correctly. Information details are displayed. The title is
            // "Details: Simple Cloud Task test message (Verify task was completed on the cloud)". Edit button is present under the information details section.
            taskDetailsPage = myTasksPage.selectViewTasks(workFlowName).render(maxWaitTime);
            assertEquals(taskDetailsPage.getTaskDetailsHeader(), "Details: " + workFlowName + " (Verify task was completed on the cloud)");
            Assert.assertTrue(taskDetailsPage.isEditButtonPresent());

            // --- Step 12 ---
            // --- Step action ---
            // Verify 'Info' section.
            // --- Expected results ---
            // The following data is displayed: Message: Simple Cloud Task test message; Owner: Administrator; Priority: Medium; Due: (None); Identifier: 416
            TaskInfo infoOp = taskDetailsPage.getTaskDetailsInfo();
            Assert.assertTrue(infoOp.getDueDateString().equals("(None)"));
            Assert.assertTrue(infoOp.getMessage().equals(workFlowName));
            Assert.assertTrue(infoOp.getOwner().contains(opUser));
            assertTrue(infoOp.getPriority().equals(Priority.MEDIUM));
            assertFalse(infoOp.getIdentifier().isEmpty());

            // --- Step 13 ---
            // --- Step action ---
            // Verify 'Progress' section.
            // --- Expected results ---
            // The following data is displayed: Status: Not Yet Started
            status = taskDetailsPage.getTaskStatus();
            assertTrue(status.equals(TaskStatus.NOTYETSTARTED));

            // --- Step 14 ---
            // --- Step action ---
            // Verify 'Items' section.
            // --- Expected results ---
            // The following item is displayed: test1.txt
            List<TaskItem> items = taskDetailsPage.getTaskItems();
            assertTrue(items.get(0).getItemName().contains(fileName));

            // --- Step 15 ---
            // --- Step action ---
            // Verify 'Response' section.
            // --- Expected results ---
            // The following data is displayed: Comment: test comment edited
            Assert.assertEquals(taskDetailsPage.getComment(), testName + "comment");
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
        }

        ShareUser.logout(drone);
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15615() throws Exception
    {
        String testName = getTestName();
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser };
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };
        String folderName = getFolderName(testName);
        String cloudSite = getSiteName(testName + "CL");
        String opSite = getSiteName(testName + "OP");
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        String workFlowName = "Simple Cloud Task " + testName;

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
        ShareUserSitePage.createFolder(hybridDrone, folderName, folderName);
        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, opSite).render();
        ShareUser.uploadFileInFolder(drone, fileInfo).render();
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName).render();
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setMessage(workFlowName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENTREMOVESYNC);
        formDetails.setLockOnPremise(false);

        // Create Workflow using File1
        cloudTaskOrReviewPage.startWorkflow(formDetails).render();
        checkIfContentIsSynced(drone, fileName);

        ShareUser.logout(drone);

        SharePage sharePage = ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        MyTasksPage myTasksPage = sharePage.getNav().selectMyTasks().render();
        Assert.assertTrue(myTasksPage.isTaskPresent(workFlowName));
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, workFlowName, TaskStatus.INPROGRESS, EditTaskAction.TASK_DONE);
        ShareUser.logout(hybridDrone);
    }

    /**
     * AONE-15615:Simple Cloud Task - Task Done - Edit Task Details (OP)
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
    public void AONE_15615() throws Exception
    {
        String testName = getTestName();
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String fileName = getFileName(testName) + ".txt";
        String workFlowName = "Simple Cloud Task " + testName;
        TaskDetailsPage taskDetailsPage;
        EditTaskPage editTaskPage;

        try
        {
            ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone).render();

            // --- Step 1 ---
            // --- Step action ---
            // OP Click on Edit button.
            // --- Expected results ---
            // The button is pressed. Edit Task page is opened.
            taskDetailsPage = myTasksPage.selectViewTasks(workFlowName).render(maxWaitTime);
            editTaskPage = taskDetailsPage.selectEditButton().render();
            assertTrue(editTaskPage.isBrowserTitle("Edit Task"), "Wrong page");

            // --- Step 2 ---
            // --- Step action ---
            // Verify the available controls on Edit Task page.
            // --- Expected results ---
            // The following additional controls are present:
            // Status drop-down list
            // View More Actions button for the document
            // Task Done button
            // Save and close button
            // Cancel button
            List<TaskStatus> statusOptions = editTaskPage.getStatusOptions();
            assertEquals(statusOptions.size(), TaskStatus.values().length, "Status not found");

            List<TaskItem> taskItems = editTaskPage.getTaskItem(fileName);
            assertTrue(taskItems.get(0).getViewMoreActionsLink().getDescription().contains("View More Actions"), "Wrong description");
            assertTrue(editTaskPage.isButtonsDisplayed(Button.TASK_DONE), "Task done button not displayed");
            assertTrue(editTaskPage.isButtonsDisplayed(Button.SAVE_AND_CLOSE), "Save and close button not displayed");

            // --- Step 3 ---
            // --- Step action ---
            // Verify the Status drop-down list.
            // --- Expected results ---
            // The following values are available:
            // Not yet started (set by default)
            // In Progress
            // On Hold
            // Canceled
            // Completed
            assertTrue(statusOptions.containsAll(getTaskStatusList()), "Not all statuses found");

            // --- Step 4 ---
            // --- Step action ---
            // Specify any value in the Status drop-down list, e.g. 'In
            // Progress'.
            // --- Expected results ---
            // Performed correctly.
            editTaskPage.selectStatusDropDown(TaskStatus.INPROGRESS);
            assertEquals(editTaskPage.getSelectedStatusFromDropDown(), TaskStatus.INPROGRESS, "Status not in progress");

            // --- Step 5 ---
            // --- Step action ---
            // Click on Cancel button.
            // --- Expected results ---
            // Edit Task page is closed. Task Details are displayed. No data was
            // changed.
            taskDetailsPage = editTaskPage.selectCancelButton().render();
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.NOTYETSTARTED, "Status is not set to: " + TaskStatus.NOTYETSTARTED);

            // --- Step 6 ---
            // --- Step action ---
            // Repeat steps 1-4.
            // --- Expected results ---
            // Performed correctly.
            taskDetailsPage.selectEditButton().render();
            editTaskPage.selectStatusDropDown(TaskStatus.INPROGRESS);
            assertEquals(editTaskPage.getSelectedStatusFromDropDown(), TaskStatus.INPROGRESS, "Status not in progress");

            // --- Step 7 ---
            // --- Step action ---
            // Click on Save and Close button.
            // --- Expected results ---
            // Edit Task page is closed. Task Details are displayed. The specified data was changed.
            taskDetailsPage = editTaskPage.selectSaveButton().render();
            assertTrue(taskDetailsPage.isBrowserTitle("Task Details"), "Wrong page");
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.INPROGRESS, "Status not in progress");
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
        }

        ShareUser.logout(drone);
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15616() throws Exception
    {
        String testName = getTestName();
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser };
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };
        String folderName = getFolderName(testName);
        String cloudSite = getSiteName(testName + "CL");
        String opSite = getSiteName(testName + "OP");
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        String workFlowName = "Simple Cloud Task " + testName;

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
        ShareUserSitePage.createFolder(hybridDrone, folderName, folderName);
        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, opSite).render();
        ShareUser.uploadFileInFolder(drone, fileInfo).render();
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName).render();
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setMessage(workFlowName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENTREMOVESYNC);
        formDetails.setLockOnPremise(false);

        // Create Workflow using File1
        cloudTaskOrReviewPage.startWorkflow(formDetails).render();
        checkIfContentIsSynced(drone, fileName);
        ShareUser.logout(drone);

        SharePage sharePage = ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        MyTasksPage myTasksPage = sharePage.getNav().selectMyTasks().render();
        Assert.assertTrue(myTasksPage.isTaskPresent(workFlowName));
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, workFlowName, TaskStatus.INPROGRESS, EditTaskAction.TASK_DONE);
        ShareUser.logout(hybridDrone);
    }

    /**
     * AONE-15616:Simple Cloud Task - Task Done - Complete (OP)
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 300000)
    public void AONE_15616() throws Exception
    {
        String testName = getTestName();
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String workFlowName = "Simple Cloud Task " + testName;
        TaskDetailsPage taskDetailsPage;
        WorkFlowDetailsPage workflowDetailsPage;
        EditTaskPage editTaskPage;
        MyWorkFlowsPage myWorkfFlowsPage;

        try
        {
            ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone).render();

            // --- Step 1 ---
            // --- Step action ---
            // OP Click on Edit button.
            // --- Expected results ---
            // The button is pressed. Edit Task page is opened.
            taskDetailsPage = myTasksPage.selectViewTasks(workFlowName).render(maxWaitTime);
            editTaskPage = taskDetailsPage.selectEditButton().render();
            assertTrue(editTaskPage.isBrowserTitle("Edit Task"), "Wrong Page");

            // --- Step 2 ---
            // --- Step action ---
            // Specify any value in the Status drop-down list, e.g. 'Completed'.
            // --- Expected results ---
            // Performed correctly.
            editTaskPage.selectStatusDropDown(TaskStatus.COMPLETED);
            assertEquals(editTaskPage.getSelectedStatusFromDropDown(), TaskStatus.COMPLETED, "Status not completed");

            // --- Step 3 ---
            // --- Step action ---
            // Click on Task Done button.
            // --- Expected results ---
            // Edit Task page is closed. Task Details are displayed. The
            // specified data was changed.
            taskDetailsPage = editTaskPage.selectTaskDoneButton().render();
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.COMPLETED, "Status not completed");

            // --- Step 4 ---
            // --- Step action ---
            // Verify My Tasks page.
            // --- Expected results ---
            // A completed task, e.g. "Simple Cloud Task test message", is
            // present in the Completed Tasks filter.
            ShareUserWorkFlow.navigateToMyTasksPage(drone).render();
            myTasksPage.selectCompletedTasks().render();
            assertTrue(myTasksPage.isTaskPresent(workFlowName), "Task not displayed");

            // --- Step 5 ---
            // --- Step action ---
            // Verify Workflows I've started page.
            // --- Expected results ---
            // A completed workflow, e.g. "Simple Cloud Task test message", is
            // present in the Completed Workflows filter.
            myWorkfFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone).render();
            myWorkfFlowsPage.selectCompletedWorkFlows().render();
            assertTrue(myWorkfFlowsPage.isWorkFlowPresent(workFlowName), "Workflow not displyed");

            // --- Step 6 ---
            // --- Step action ---
            // Verify Workflow's Details page.
            // --- Expected results ---
            // The following changes are present:
            // Completed: Thu 12 Sep 2013 20:26:03 in the General Info section
            // Status: Workflow is Complete in the General Info section
            // Delete Workflow button
            workflowDetailsPage = myWorkfFlowsPage.selectWorkFlow(workFlowName).render();
            assertTrue(workflowDetailsPage.getWorkFlowDetailsGeneralInfo().getCompletedDate().isBeforeNow(), "Wrong completed date");
            assertEquals(workflowDetailsPage.getWorkFlowStatus(), "Workflow is Complete");
            assertTrue(workflowDetailsPage.isDeleteWorkFlowButtonDisplayed(), "Delete workflow button disabled");

            // --- Step 7 ---
            // --- Step action ---
            // Verify 'Current Tasks' section.
            // --- Expected results ---
            // The following data is displayed:
            // No tasks
            assertTrue(workflowDetailsPage.isNoTasksMessageDisplayed(), "No tasks message is not displayed");

            // --- Step 8 ---
            // --- Step action ---
            // Verify 'History' section.
            // --- Expected results ---
            // The following data is displayed:
            // Verify task was completed on the cloud admin Thu 12 Sep 2013
            // 21:02:27 Task Done
            // Start a task or review on Alfresco Cloud admin Thu 12 Sep 2013
            // 17:15:07 Task Done
            assertEquals(workflowDetailsPage.getWorkFlowHistoryList().get(0).getType(), WorkFlowHistoryType.VERIFY_TASK_COMPLETED_ON_CLOUD,
                    "Wrong workflow type");
            assertEquals(workflowDetailsPage.getWorkFlowHistoryList().get(0).getCompletedBy(), getUserFullName(opUser), "Wrong completed by user");
            assertEquals(workflowDetailsPage.getWorkFlowHistoryList().get(0).getCompletedDate().toLocalDate(), getToDaysLocalDate(), "Wrong completed date");
            assertEquals(workflowDetailsPage.getWorkFlowHistoryList().get(0).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE, "Task is not done");
            assertEquals(workflowDetailsPage.getWorkFlowHistoryList().get(1).getType(), WorkFlowHistoryType.START_TASK_OR_REVIEW_ON_CLOUD,
                    "Wrong workflow history type");
            assertEquals(workflowDetailsPage.getWorkFlowHistoryList().get(0).getCompletedBy(), getUserFullName(opUser), "Wrong completed by user");
            assertEquals(workflowDetailsPage.getWorkFlowHistoryList().get(0).getCompletedDate().toLocalDate(), getToDaysLocalDate(), "Wrong completed date");
            assertEquals(workflowDetailsPage.getWorkFlowHistoryList().get(1).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE, "Task is not done");
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
        }

        ShareUser.logout(drone);
    }

}
