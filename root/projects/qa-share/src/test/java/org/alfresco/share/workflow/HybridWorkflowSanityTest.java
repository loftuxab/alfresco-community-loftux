package org.alfresco.share.workflow;

import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.SyncInfoPage;
import org.alfresco.po.share.task.*;
import org.alfresco.po.share.workflow.*;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.alfresco.share.util.ShareUser.refreshDocumentLibrary;
import static org.testng.Assert.*;

/**
 * @author Dmitry Yukhnovets on 10.12.2014.
 */
@Listeners(FailedTestListener.class)
public class HybridWorkflowSanityTest extends AbstractWorkflow
{
    private static final Logger logger = Logger.getLogger(HybridWorkflowSanityTest.class);
    private long timeToWait;
    private int retryCount;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        timeToWait = 25000;
        retryCount = 5;
        logger.info("[Suite ] : Start Tests in: " + testName);
    }

    /**
     * AONE-15732:Enable Hybrid Workflow functionality
     */
    @Test(groups = "Hybrid")
    public void AONE_15732() throws Exception
    {
        try
        {
            ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
            StartWorkFlowPage startWorkFlowPage = ShareUserWorkFlow.selectStartWorkFlowFromMyTasksPage(drone);
            assertTrue(startWorkFlowPage.isWorkflowTypePresent(WorkFlowType.CLOUD_TASK_OR_REVIEW),
                    "Verifying the \"Cloud Task or Review\" workflow is displayed in the dropdown");

            ShareUser.logout(drone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
        }
    }

    /**
     * AONE-15733:Create Simple Cloud Task
     */
    @Test(groups = "Hybrid")
    public void AONE_15733() throws Exception
    {

        String testName = getTestName();
        String uniqueData = String.valueOf(System.currentTimeMillis());
        String user1 = getUserNameForDomain(testName + uniqueData, DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName + uniqueData, DOMAIN_HYBRID);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, DOMAIN_HYBRID, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        String opSiteName = getSiteName(testName) + uniqueData + "-OP";
        String cloudSiteName = getSiteName(testName) + uniqueData + "-CL";
        String fileName = getFileName(testName) + uniqueData + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        String workFlowName = testName + uniqueData + "-WF";
        String due = getDueDateString();
        DateTime dueDate = getDueDate(due);

        String cloudCommentInProgress = testName + uniqueData + "-Cloud Comment InProgress";
        String cloudComment = testName + uniqueData + "-Cloud Comment";

        // Login as User1 (Cloud) and Create a Site
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Create Site and Upload a document
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        // Start Simple Cloud Task workflow
        MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);
        StartWorkFlowPage startWorkFlowPage = (StartWorkFlowPage) myWorkFlowsPage.selectStartWorkflowButton();
        CloudTaskOrReviewPage cloudTaskOrReviewPage = (CloudTaskOrReviewPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW);
        cloudTaskOrReviewPage.selectTask(TaskType.SIMPLE_CLOUD_TASK);
        cloudTaskOrReviewPage.selectItem(fileName, opSiteName);
        // Verify "Simple Cloud Task" is selected
        assertTrue(cloudTaskOrReviewPage.isTaskTypeSelected(TaskType.SIMPLE_CLOUD_TASK), "Simple Cloud Task is not selected");

        // Fill up Task Details
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(due);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

        Assert.assertEquals(formDetails.getMessage(), workFlowName, "Workflow message was entered incorrectly");
        Assert.assertEquals(formDetails.getDueDate(), due, "Workflow Due date was entered incorrectly");
        Assert.assertEquals(formDetails.getTaskPriority(), Priority.MEDIUM, "Workflow Priority was entered incorrectly");
        Assert.assertEquals(formDetails.getSiteName(), cloudSiteName, "Workflow Site was entered incorrectly");
        Assert.assertEquals(formDetails.getAssignee(), cloudUser, "Workflow Assignee was entered incorrectly");
        Assert.assertEquals(formDetails.getContentStrategy(), KeepContentStrategy.DELETECONTENT, "Workflow content strategy was entered incorrectly");

        // Fill the form details and start workflow
        myWorkFlowsPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        // Verify the details of the newly created workflow.
        List<WorkFlowDetails> workFlowDetails = myWorkFlowsPage.getWorkFlowDetails(workFlowName);

        assertEquals(workFlowDetails.size(), 1, "Some workflows were created");
        assertEquals(workFlowDetails.get(0).getWorkFlowName(), formDetails.getMessage(), "Verifying workflow name after creation workflow");
        assertEquals(workFlowDetails.get(0).getDue(), getDueDate(formDetails.getDueDate()), "Due date is not correct after creation workflow");
        assertEquals(getLocalDate(workFlowDetails.get(0).getStartDate()), getToDaysLocalDate(), "Start date is not correct after creation workflow");
        assertEquals(workFlowDetails.get(0).getType(), WorkFlowType.CLOUD_TASK_OR_REVIEW, "Type of task is not correct after creation workflow");
        assertEquals(workFlowDetails.get(0).getDescription(), WorkFlowDescription.CREATE_A_TASK_OR_START_A_REVIEW,
                "Description is not correct after creation workflow");

        // Select the workflow to view WorkFlow Details
        WorkFlowDetailsPage workFlowDetailsPage = myWorkFlowsPage.selectWorkFlow(workFlowName).render();

        // Verify WorkFlow Details page The following title is displayed:
        // "Details: Simple Cloud Task test message (Start a task or review on Alfresco Cloud)"
        assertEquals(workFlowDetailsPage.getPageHeader(), getWorkFlowDetailsHeader(workFlowName), "Page Title is not correct");

        // Verify WorkFlow Details General Info section
        WorkFlowDetailsGeneralInfo generalInfo = workFlowDetailsPage.getWorkFlowDetailsGeneralInfo();

        assertEquals(generalInfo.getTitle(), WorkFlowTitle.CLOUD_TASK_OR_REVIEW, "Title is not correct on workflow details page");
        assertEquals(generalInfo.getDescription(), WorkFlowDescription.CREATE_A_TASK_OR_START_A_REVIEW, "Description is not correct on workflow details page");
        assertEquals(generalInfo.getStartedBy(), getUserFullName(user1), "Started by value is not correct on workflow details page");
        assertEquals(getLocalDate(generalInfo.getDueDate()), getLocalDate(dueDate), "Due date is not correct on workflow details page");
        assertEquals(generalInfo.getCompleted(), "<in progress>", "Completion status is not correct on workflow details page");
        assertEquals(getLocalDate(generalInfo.getStartDate()), getToDaysLocalDate(), "Start date is not correct on workflow details page");
        assertEquals(generalInfo.getPriority(), formDetails.getTaskPriority(), "Task Priority is not correct on workflow details page");
        assertEquals(generalInfo.getStatus(), WorkFlowStatus.WORKFLOW_IN_PROGRESS, "Status is not correct on workflow details page");
        assertEquals(generalInfo.getMessage(), workFlowName, "Workflow message is not correct on workflow details page");

        // Verify WorkFlow Details More Info section
        WorkFlowDetailsMoreInfo moreInfo = workFlowDetailsPage.getWorkFlowDetailsMoreInfo();

        assertEquals(moreInfo.getType(), formDetails.getTaskType(), "Task type is not correct for WorkFlow Details More Info section");
        assertEquals(moreInfo.getDestination(), DOMAIN_HYBRID, "Destination is not correct for WorkFlow Details More Info section");
        assertEquals(moreInfo.getAfterCompletion(), formDetails.getContentStrategy(), "Content strategy is not correct for WorkFlow Details More Info section");
        assertFalse(moreInfo.isLockOnPremise(), "Incorrect value for locking on WorkFlow Details More Info section");
        assertEquals(moreInfo.getAssignmentList().size(), 1, "Count of users is not correct for WorkFlow Details More Info section");
        assertEquals(moreInfo.getAssignmentList().get(0), getUserFullNameWithEmail(cloudUser, cloudUser),
                "Assignee user is not correct for WorkFlow Details More Info section");

        // Verify WorkFlow Details Item section
        List<WorkFlowDetailsItem> items = workFlowDetailsPage.getWorkFlowItems();
        assertEquals(items.size(), 1, "Count of added files is not correct.");
        assertEquals(items.get(0).getItemName(), fileName, "File name is not correct");
        assertEquals(items.get(0).getDescription(), NONE, "File description is not correct");
        assertEquals(getLocalDate(items.get(0).getDateModified()), getToDaysLocalDate(), "File date is not correct");

        // Verify WorkFlow Details Current Tasks table displays "No Tasks"
        assertTrue(workFlowDetailsPage.isNoTasksMessageDisplayed(), "No Tasks message is not displayed for WorkFlow Details Current Tasks table");

        // Verify WorkFlow Details History List
        List<WorkFlowDetailsHistory> historyList = workFlowDetailsPage.getWorkFlowHistoryList();

        assertEquals(historyList.size(), 1, "Count of History rows is not equals 1");
        assertEquals(historyList.get(0).getType(), WorkFlowHistoryType.START_TASK_OR_REVIEW_ON_CLOUD,
                "Task type is not correct for WorkFlow Details History List");
        assertEquals(historyList.get(0).getCompletedBy(), getUserFullName(user1), "User Name is not correct for WorkFlow Details History List");
        assertEquals(getLocalDate(historyList.get(0).getCompletedDate()), getToDaysLocalDate(),
                "Today local date is not correct for WorkFlow Details History List");
        assertEquals(historyList.get(0).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE, "Workflow History outcome is not correct");
        assertEquals(historyList.get(0).getComment(), "", "Any comment was added.");

        // Verify the chosen content item.
        DocumentDetailsPage documentDetailsPage = (DocumentDetailsPage) items.get(0).getItemNameLink().click();
        // Content item is part of workflow
        assertTrue(documentDetailsPage.isPartOfWorkflow(), "Content is not part of workflows");
        // The content item is synced to Cloud.
        assertTrue(documentDetailsPage.isRequestSyncIconDisplayed(), "Content was not synced");
        // Sync Location is displayed correctly.
        SyncInfoPage syncInf2 = documentDetailsPage.getSyncInfoPage();
        assertEquals(syncInf2.getCloudSyncLocation(), DOMAIN_HYBRID + ">" + cloudSiteName + ">" + DEFAULT_FOLDER_NAME, "Failed to displayed sync info");

        // Open Site Document Library, verify the document is part of the workflow, document is synced and verify Sync Status
        SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName);
        // Verify the sync status for the Content
        assertTrue(ShareUser.checkIfContentIsSynced(drone, fileName), "Verifying the Sync Status is \"Synced\"");
        ShareUser.logout(drone);

        // Login as Cloud User,
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Open Site document library and verify the file is a part of workflow
        DocumentLibraryPage documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSiteName);
        waitAndCheckIfVisible(hybridDrone, documentLibraryPage, fileName);
        assertTrue(documentLibraryPage.isItemVisble(fileName), "Cloud: File was not synced.");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Workflow was not created.");

        // Navigate to MyTasks page
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        // Verify Task Details are displayed correctly
        assertTrue(AbstractWorkflow.checkIfTaskIsPresent(hybridDrone, workFlowName, true), "Cloud: task is not displayed");
        TaskDetails taskDetails = myTasksPage.getTaskDetails(workFlowName);

        assertEquals(taskDetails.getTaskName(), workFlowName, "Verifying workflow name");
        assertEquals(taskDetails.getDue(), ShareUserWorkFlow.getDueDateOnMyTaskPage(due), "Verifying workflow due date");
        assertEquals(getLocalDate(taskDetails.getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertEquals(taskDetails.getStatus(), TaskStatus.NOTYETSTARTED.getTaskName(), "Verifying status");
        assertEquals(taskDetails.getType(), TaskDetailsType.TASK, "Verifying Task type");
        assertEquals(taskDetails.getDescription(), "Task", "Verifying Workflow Description");
        assertEquals(taskDetails.getStartedBy(), cloudUser, "Verifying Started by user");

        // Task History Verifications
        TaskHistoryPage taskHistoryPage = ShareUserWorkFlow.navigateToTaskHistoryPage(hybridDrone, workFlowName);

        // Verify Task History Page Header
        assertEquals(taskHistoryPage.getPageHeader(), getSimpleCloudTaskDetailsHeader(workFlowName), "Cloud: Task details is not correct on Task History Page");

        // Verify Task History Page General Info Section
        generalInfo = taskHistoryPage.getWorkFlowDetailsGeneralInfo();

        assertEquals(generalInfo.getTitle(), WorkFlowTitle.HYBRID_TASK, "Cloud: Title is not correct on Task History Page");
        assertEquals(generalInfo.getDescription(), WorkFlowDescription.ASSIGN_NEW_TASK_TO_SOMEONE_ON_THE_CLOUD,
                "Cloud: Description is not correct on Task History Page");
        assertEquals(generalInfo.getStartedBy(), getUserFullName(cloudUser), "Cloud: 'Started by' info is not correct on Task History Page");
        assertEquals(getLocalDate(generalInfo.getDueDate()), getLocalDate(dueDate), "Cloud: Due date is not correct on Task History Page");
        assertEquals(generalInfo.getCompleted(), "<in progress>", "Cloud: Status is not correct on Task History Page");
        assertEquals(getLocalDate(generalInfo.getStartDate()), getToDaysLocalDate(), "Cloud: Local Date is not correct on Task History Page");
        assertEquals(generalInfo.getPriority(), formDetails.getTaskPriority(), "Cloud: Priority is not correct on Task History Page");
        assertEquals(generalInfo.getStatus(), WorkFlowStatus.TASK_IN_PROGRESS, "Cloud: Status is not correct on Task History Page");
        assertEquals(generalInfo.getMessage(), workFlowName, "Cloud: Task message is not correct on Task History Page");

        // Verify Task History Page More Info Section
        moreInfo = taskHistoryPage.getWorkFlowDetailsMoreInfo();
        assertEquals(moreInfo.getNotification(), SendEMailNotifications.YES, "Send email option is not correct");

        // Verify Task History Page Item Details
        items = taskHistoryPage.getWorkFlowItems();
        assertEquals(items.size(), 1, "Count of files is not equal 1");
        assertEquals(items.get(0).getItemName(), fileName, "FileName is displayed incorrectly");
        assertEquals(items.get(0).getDescription(), NONE, "Description is displayed incorrectly");
        assertEquals(getLocalDate(items.get(0).getDateModified()), getToDaysLocalDate(), "Modified Date is displayed incorrectly");

        // Verify Task History Page Current Tasks List
        List<WorkFlowDetailsCurrentTask> currentTaskList = taskHistoryPage.getCurrentTasksList();

        assertEquals(currentTaskList.size(), 1, "Count of tasks is not equals 1");
        assertEquals(currentTaskList.get(0).getTaskType(), CurrentTaskType.TASK, "Task is displayed incorrectly");
        assertEquals(currentTaskList.get(0).getAssignedTo(), getUserFullName(cloudUser), "Assignee User is displayed incorrectly");
        assertEquals(currentTaskList.get(0).getDueDate().toLocalDate(), dueDate.toLocalDate(), "Due Date is displayed incorrectly");
        assertEquals(currentTaskList.get(0).getTaskStatus(), TaskStatus.NOTYETSTARTED, "Task status is displayed incorrectly");

        // Verify Task History Page History List
        historyList = taskHistoryPage.getWorkFlowHistoryList();

        assertEquals(historyList.size(), 1, "Workflow History list is not equals 1");
        assertEquals(historyList.get(0).getType(), WorkFlowHistoryType.TASK, "Workflow History list contains incorrect incorrect History Type");
        assertEquals(historyList.get(0).getCompletedBy(), getUserFullName(cloudUser), "History List doesn't contain assignee user");
        assertEquals(getLocalDate(historyList.get(0).getCompletedDate()), getToDaysLocalDate(), "Completed Date is displayed incorrectly");
        assertEquals(historyList.get(0).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE, "OutCome status is displayed incorrectly");
        assertEquals(historyList.get(0).getComment(), "", "Any comment is displayed, but it was not added");

        // Navigate to Task Details Page
        TaskDetailsPage taskDetailsPage = ShareUserWorkFlow.navigateToTaskDetailsPage(hybridDrone, workFlowName);

        // Verify Task Details Page Header
        assertEquals(taskDetailsPage.getTaskDetailsHeader(), getSimpleCloudTaskDetailsHeader(workFlowName));

        // Verify Task Info Task Details Page
        TaskInfo taskInfo = taskDetailsPage.getTaskDetailsInfo();

        assertEquals(taskInfo.getMessage(), workFlowName, "Message is displayed incorrectly on Task Details Page");
        assertEquals(taskInfo.getOwner(), getUserFullName(user1), "Owner is displayed incorrectly on Task Details Page");
        assertEquals(taskInfo.getPriority(), Priority.MEDIUM, "Priority is displayed incorrectly on Task Details Page");
        assertEquals(getLocalDate(taskInfo.getDueDate()), getLocalDate(dueDate), "Due Date is displayed incorrectly on Task Details Page");
        assertEquals(taskInfo.getDueDateString(), dueDate.toString("dd MMM, yyyy"));
        assertFalse(StringUtils.isEmpty(taskInfo.getIdentifier()), "Identified is empty on Task Details Page");

        // Verify 'Progress' section (Status: Not Yet Started)
        assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.NOTYETSTARTED, "Progress section is not correct on Task Details Page");

        // Verify Item Details
        List<TaskItem> taskItems = taskDetailsPage.getTaskItems();
        assertEquals(taskItems.size(), 1, "Count of items is not equal 1 on Task Details Page");
        assertEquals(taskItems.get(0).getItemName(), fileName, "Incorrect file is displayed on Task Details Page");
        assertEquals(taskItems.get(0).getDescription(), NONE, "Incorrect description is displayed on Task Details Page");
        assertEquals(getLocalDate(taskItems.get(0).getDateModified()), getToDaysLocalDate(), "Modified date is incorrect on Task Details Page ");

        assertEquals(taskDetailsPage.getComment(), NONE, "Any comment has been added.");

        // Navigate to Edit Task Page
        EditTaskPage editTaskPage = taskDetailsPage.selectEditButton().render();

        // Edit Task Page - Verify Task Info section
        taskInfo = editTaskPage.getTaskDetailsInfo();

        assertEquals(taskInfo.getMessage(), workFlowName, "Workflow name is displayed incorrectly on Edit Task Page");
        assertEquals(taskInfo.getOwner(), getUserFullName(user1), "Owner is displayed incorrectly on Edit Task Page");
        assertEquals(taskInfo.getPriority(), Priority.MEDIUM, "Priority is displayed incorrectly on Edit Task Page");
        assertEquals(getLocalDate(taskInfo.getDueDate()), getLocalDate(dueDate), "Due date is not correct on Edit Task Page");
        assertFalse(StringUtils.isEmpty(taskInfo.getIdentifier()), "Identifier is not correct on Edit Task Page");

        // Edit Task Page - Verify Task Item
        taskItems = taskDetailsPage.getTaskItems();

        assertEquals(taskItems.size(), 1, "Count of items is not equals 1 on Edit Task Page");
        assertEquals(taskItems.get(0).getItemName(), fileName, "File name is displayed on Edit Task Page");
        assertEquals(taskItems.get(0).getDescription(), NONE, "Incorrect description is displayed on Edit Task Page");
        assertEquals(getLocalDate(taskItems.get(0).getDateModified()), getToDaysLocalDate(), "Modified date is not correct on Edit Task Page");

        // Verify Status Drop down options
        List<TaskStatus> statusOptions = editTaskPage.getStatusOptions();

        assertEquals(statusOptions.size(), TaskStatus.values().length, "Some Status options are not displayed");
        assertTrue(statusOptions.containsAll(getTaskStatusList()), "Some Status options are not displayed");

        assertTrue(editTaskPage.isButtonsDisplayed(EditTaskPage.Button.TASK_DONE), "Task Done button is not displayed on Edit Task Page");
        assertTrue(editTaskPage.isButtonsDisplayed(EditTaskPage.Button.SAVE_AND_CLOSE), "Save and Close button is not displayed on Edit Task Page");
        assertTrue(editTaskPage.isButtonsDisplayed(EditTaskPage.Button.CANCEL), "Cancel button is not displayed on Edit Task Page");

        // Select Task Status as "In-Progress", enter a comment, select Save and verify
        // the Task Status and comment are saved in Task Details Page
        taskDetailsPage = ShareUserWorkFlow.completeTask(hybridDrone, TaskStatus.INPROGRESS, cloudCommentInProgress, EditTaskAction.SAVE).render();
        assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.INPROGRESS, "Task status has not been changed to In Progress");
        assertEquals(taskDetailsPage.getComment(), cloudCommentInProgress, "Comment has not been added");

        // Go to Edit Task page, select Task Status as "On-Hold", change the comment and click on Cancel
        taskDetailsPage = ShareUserWorkFlow.completeTaskFromTaskDetailsPage(hybridDrone, TaskStatus.ONHOLD, cloudComment, EditTaskAction.CANCEL).render();
        // Verify the changes are not reflected in Task Details Page
        assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.INPROGRESS, "Updates have not been canceled. New status has been applied");
        assertEquals(taskDetailsPage.getComment(), cloudCommentInProgress, "Updates have not been canceled. New comment has been applied");

        // Complete the task and verify task is completed (with the comment)
        taskDetailsPage = ShareUserWorkFlow.completeTaskFromTaskDetailsPage(hybridDrone, TaskStatus.COMPLETED, cloudComment, EditTaskAction.TASK_DONE).render();

        assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.COMPLETED, "Task status has not been changed to Completed");
        assertEquals(taskDetailsPage.getComment(), cloudComment, "Comment has not been edited");

        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

        // Verify task is not displayed in Active Tasks list
        assertFalse(myTasksPage.isTaskPresent(workFlowName), "The task is still displayed for Active task, but task was completed");

        // Select Completed Tasks and verify Task is displayed and the task details are correct.
        myTasksPage = myTasksPage.selectCompletedTasks().render();

        assertTrue(myTasksPage.isTaskPresent(workFlowName), "Completed task is not displayed for Completed filter");

        // Verify Task Details in MyTasks Page (Completed Tasks)
        taskDetails = myTasksPage.getTaskDetails(workFlowName);
        assertEquals(taskDetails.getTaskName(), workFlowName, "Task name is not correct on Completed Task page");
        assertEquals(taskDetails.getDue(), ShareUserWorkFlow.getDueDateOnMyTaskPage(due), "Due Date is not correct on Completed Task page");
        assertEquals(getLocalDate(taskDetails.getStartDate()), getToDaysLocalDate(), "Start Date is not correct on Completed Task page");
        assertEquals(getLocalDate(taskDetails.getEndDate()), getToDaysLocalDate(), "End Date is not correct on Completed Task page");
        assertEquals(taskDetails.getStatus(), TaskStatus.COMPLETED.getTaskName(), "Status is not updated on Completed Task page");
        assertEquals(taskDetails.getType(), TaskDetailsType.TASK, "Task type is not correct on Completed Task page");
        assertEquals(taskDetails.getDescription(), "Task", "Description is not correct on Completed Task page");
        assertEquals(taskDetails.getStartedBy(), user1, "Started by value is not correct on Completed Task page");

        // Navigate to Task History Page
        taskHistoryPage = ShareUserWorkFlow.navigateToTaskHistoryPage(hybridDrone, workFlowName);

        // Verify General Info section
        generalInfo = taskHistoryPage.getWorkFlowDetailsGeneralInfo();

        assertEquals(generalInfo.getTitle(), WorkFlowTitle.HYBRID_TASK, "Title is not correct on Task History Page");
        assertEquals(generalInfo.getDescription(), WorkFlowDescription.ASSIGN_NEW_TASK_TO_SOMEONE_ON_THE_CLOUD,
                "Description is not correct on Task History Page");
        assertEquals(generalInfo.getStartedBy(), getUserFullName(user1), "Started By value is not correct on Task History Page");
        assertEquals(getLocalDate(generalInfo.getDueDate()), getLocalDate(dueDate), "Due date is not correct on Task History Page");
        assertEquals(getLocalDate(generalInfo.getCompletedDate()), getToDaysLocalDate(), "Completed Date is not correct on Task History Page");
        assertEquals(getLocalDate(generalInfo.getStartDate()), getToDaysLocalDate(), "Started Date is not correct on Task History Page");
        assertEquals(generalInfo.getPriority(), formDetails.getTaskPriority(), "Task priority is not correct on Task History Page");
        assertEquals(generalInfo.getStatus(), WorkFlowStatus.TASK_COMPLETE, "Status is not correct on Task History Page");
        assertEquals(generalInfo.getMessage(), workFlowName, "Message is not correct on Task History Page");

        // Verify More Info section
        moreInfo = taskHistoryPage.getWorkFlowDetailsMoreInfo();

        assertEquals(moreInfo.getNotification(), SendEMailNotifications.YES, "Email notification is not correct ");

        // Verify Item details
        items = taskHistoryPage.getWorkFlowItems();
        assertEquals(items.size(), 1, "Count of files is not equals 1 on History page");
        assertEquals(items.get(0).getItemName(), fileName, "File name is displayed incorrectly on History page");
        assertEquals(items.get(0).getDescription(), NONE, "Any File description is displayed on History page");
        assertEquals(getLocalDate(items.get(0).getDateModified()), getToDaysLocalDate(), "Modified date is not displayed correctly on History page");
        assertTrue(taskHistoryPage.isDeleteWorkFlowButtonDisplayed(), "Delete Workflow button is not displayed on History Page");

        // 44. Verify "Current Tasks" section (Displays "No Tasks")
        assertTrue(taskHistoryPage.isNoTasksMessageDisplayed(), "'No tasks' message is not displayed on History page for current task section");

        // Verify "History" section
        historyList = taskHistoryPage.getWorkFlowHistoryList();

        assertEquals(historyList.size(), 2, "Some actions from history list are incorrect.");
        assertEquals(historyList.get(0).getType(), WorkFlowHistoryType.TASK, "First Task Type is not correct for History List");
        assertEquals(historyList.get(0).getCompletedBy(), getUserFullName(cloudUser), "'Completed by' value is not correct for first task on History List");
        assertEquals(getLocalDate(historyList.get(0).getCompletedDate()), getToDaysLocalDate(),
                "'Completed Date' value is not correct for first task on History List");
        assertEquals(historyList.get(0).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE, "'OutCome' value is not correct for first task on History List");
        assertEquals(historyList.get(0).getComment(), cloudComment, "Comment is not correct for first task on History List");

        assertEquals(historyList.get(1).getType(), WorkFlowHistoryType.TASK, "Second Task Type is not correct for History List");
        assertEquals(historyList.get(1).getCompletedBy(), getUserFullName(cloudUser), "'Completed by' value is not correct for second task on History List");
        assertEquals(getLocalDate(historyList.get(1).getCompletedDate()), getToDaysLocalDate(),
                "'Completed Date' value is not correct for second task on History List");
        assertEquals(historyList.get(1).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE, "'OutCome' value is not correct for second task on History List");
        assertEquals(historyList.get(1).getComment(), "", "Comment is not correct for first task on History List");

        ShareUser.logout(hybridDrone);

        // Login as OP user
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // 46 Open My Tasks page
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);

        // Verify a new task is displayed for OP user
        assertTrue(ShareUser.checkIfTaskIsPresent(drone, workFlowName), "Completed task (on Cloud) is not displayed for OP.");

        // Verify the task details
        taskDetails = myTasksPage.getTaskDetails(workFlowName);

        assertEquals(taskDetails.getTaskName(), workFlowName, "OP: Task name is not displayed on My Task page (task was completed on Cloud)");
        assertEquals(taskDetails.getDue(), ShareUserWorkFlow.getDueDateOnMyTaskPage(due),
                "OP: Due date  is not correct on My Task page (task was completed on Cloud)");
        assertEquals(getLocalDate(taskDetails.getStartDate()), getToDaysLocalDate(),
                "OP: Start date is not correct on My Task page (task was completed on Cloud)");
        assertNull(taskDetails.getEndDate(), "OP: End date is not correct on My Task page (task was completed on Cloud)");
        assertEquals(taskDetails.getStatus(), TaskStatus.NOTYETSTARTED.getTaskName(),
                "OP: Task Status is not correct on My Task page (task was completed on Cloud)");
        assertEquals(taskDetails.getType(), TaskDetailsType.VERIFY_TASK_WAS_COMPLETED_ON_THE_CLOUD,
                "OP: Task type is not correct on My Task page (task was completed on Cloud)");
        assertEquals(taskDetails.getDescription(), workFlowName,
                "OP: Description is not correct on My Task page (task was completed on Cloud)Verifying Workflow Description");
        assertEquals(taskDetails.getStartedBy(), user1, "OP: 'Started by' value is not correct on My Task page (task was completed on Cloud)");

        // 47 Navigate to Workflows I've started
        myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);
        workFlowDetailsPage = myWorkFlowsPage.selectWorkFlow(workFlowName).render();

        currentTaskList = workFlowDetailsPage.getCurrentTasksList();
        assertEquals(currentTaskList.size(), 1, "OP: Current task List: Count of tasks is not equals 1. when task is completed on Cloud");
        assertEquals(currentTaskList.get(0).getTaskType(), CurrentTaskType.VERIFY_TASK_WAS_COMPLETED_ON_THE_CLOUD,
                "OP: Current task List: Task Type is not correct");
        assertEquals(currentTaskList.get(0).getAssignedTo(), getUserFullName(cloudUser), "OP: Current task List: 'Assigned to' value is not correct");
        assertEquals(currentTaskList.get(0).getDueDate().toLocalDate(), dueDate.toLocalDate(), "OP: Current task List: 'Due Date' value is not correct");
        assertEquals(currentTaskList.get(0).getTaskStatus(), TaskStatus.NOTYETSTARTED, "OP: Current task List: Task Status is not correct");

        // Navigate to Task Details
        taskDetailsPage = ShareUserWorkFlow.navigateToTaskDetailsPage(drone, workFlowName);

        taskInfo = taskDetailsPage.getTaskDetailsInfo();

        assertEquals(taskInfo.getMessage(), workFlowName, "OP: Task Details: task name is not correct");
        assertEquals(taskInfo.getOwner(), getUserFullName(user1), "OP: Task Details: Owner is not correct");
        assertEquals(taskInfo.getPriority(), formDetails.getTaskPriority(), "OP: Task Details: Priority is not correct");
        assertEquals(getLocalDate(taskInfo.getDueDate()), getLocalDate(dueDate), "OP: Task Details: 'Due Date' value is not correct");

        assertTrue(taskInfo.getDueDateString().equals(dueDate.toString("dd MMM, yyyy")), "OP: Format for due Date is not correct.on Task Details Page");
        assertFalse(StringUtils.isEmpty(taskInfo.getIdentifier()), "OP: Task Details: Identifier value is empty");

        assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.NOTYETSTARTED, "OP: Task Details: Task status is not correct.");

        taskItems = taskDetailsPage.getTaskItems();
        assertEquals(taskItems.size(), 1, "OP: Task Details: Count of files is not 1");
        assertEquals(taskItems.get(0).getItemName(), fileName, "OP: Task Details: File name is displayed incorrectly");
        assertEquals(taskItems.get(0).getDescription(), NONE, "OP: Task Details: Some description has been applied");
        assertEquals(getLocalDate(taskItems.get(0).getDateModified()), getToDaysLocalDate(), "OP: Task Details: Modified date is not correct.");

        assertEquals(taskDetailsPage.getComment(), cloudComment, "OP: Task Details: Comment is not displayed correctly.");

        // Edit task and complete the task
        editTaskPage = taskDetailsPage.selectEditButton().render();

        taskInfo = editTaskPage.getTaskDetailsInfo();

        assertEquals(taskInfo.getMessage(), workFlowName, "OP: Edit Task: Task name is not correct");
        assertEquals(taskInfo.getOwner(), getUserFullName(user1), "OP: Edit Task: Owner is not correct");
        assertEquals(taskInfo.getPriority(), Priority.MEDIUM, "OP: Edit Task: Priority is not correct");
        assertEquals(getLocalDate(taskInfo.getDueDate()), getLocalDate(dueDate), "OP: Edit Task: Due Date is not correct");
        assertTrue(taskInfo.getDueDateString().equals(dueDate.toString("dd MMM, yyyy")), "OP: Edit Task: Format for 'Due date' is not correct");
        assertFalse(StringUtils.isEmpty(taskInfo.getIdentifier()), "OP: Edit Task: Identifier is not displayed");

        taskItems = taskDetailsPage.getTaskItems();
        assertEquals(taskItems.size(), 1, "OP: Edit Task: Count of files is not equals 1");
        assertEquals(taskItems.get(0).getItemName(), fileName, "OP: Edit Task: FileName is not correct");
        assertEquals(taskItems.get(0).getDescription(), NONE, "OP: Edit Task: Some description  has been applied");
        assertEquals(getLocalDate(taskItems.get(0).getDateModified()), getToDaysLocalDate(), "OP: Edit Task: Modified date is not correct");
        assertTrue(editTaskPage.isButtonsDisplayed(EditTaskPage.Button.TASK_DONE), "OP: Edit Task: Task Done button is not displayed");
        assertTrue(editTaskPage.isButtonsDisplayed(EditTaskPage.Button.CANCEL), "OP: Edit Task: Cancel button is not displayed");
        assertTrue(editTaskPage.isButtonsDisplayed(EditTaskPage.Button.SAVE_AND_CLOSE), "OP: Edit Task: Save and Close button is not displayed");

        // Verify Status Drop down options
        statusOptions = editTaskPage.getStatusOptions();

        assertEquals(statusOptions.size(), TaskStatus.values().length, "OP: Edit Task: Some Status options are not displayed");
        assertTrue(statusOptions.containsAll(getTaskStatusList()), "OP: Edit Task: Some Status options are not displayed");

        assertFalse(editTaskPage.isReAssignButtonDisplayed(), "Verifying ReAssign button is not displayed");

        // 57-58 Specify any value in the Status drop-down list, e.g. 'On Hold'.
        taskDetailsPage = ShareUserWorkFlow.completeTask(drone, TaskStatus.ONHOLD, EditTaskAction.CANCEL).render();

        assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.NOTYETSTARTED, "OP: Edit Task: Task Status has been changed but cancel button is used");

        // 59 Select Status as "In Progress" and click on Save
        taskDetailsPage = ShareUserWorkFlow.completeTaskFromTaskDetailsPage(drone, TaskStatus.INPROGRESS, EditTaskAction.SAVE);
        assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.INPROGRESS, "OP: Edit Task: Task Status has not been changed when save button is used");

        // 61 Specify any value in the Status drop-down list, e.g. 'Completed' and click on Task Done button
        ShareUserWorkFlow.completeTaskFromTaskDetailsPage(drone, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);

        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);

        // Verify the task is disappeared from Active Tasks list
        assertFalse(myTasksPage.isTaskPresent(workFlowName), "OP: My Task Page: task is displayed but the task has been completed.");

        // Select Completed tasks and verify the task is displayed and the details are accurate
        myTasksPage = myTasksPage.selectCompletedTasks().render();

        assertTrue(myTasksPage.isTaskPresent(workFlowName), "OP: Completed tasks: task is not displayed for Completed Filter.");

        taskDetails = myTasksPage.getTaskDetails(workFlowName);

        assertEquals(taskDetails.getTaskName(), workFlowName, "OP: Completed tasks: task name is not displayed");
        assertEquals(taskDetails.getDue(), ShareUserWorkFlow.getDueDateOnMyTaskPage(due), "OP: Completed tasks: due date is not correct");
        assertEquals(getLocalDate(taskDetails.getStartDate()), getToDaysLocalDate(), "OP: Completed tasks: Start date is not correct");
        assertEquals(getLocalDate(taskDetails.getEndDate()), getToDaysLocalDate(), "OP: Completed tasks: End date is not correct");
        assertEquals(taskDetails.getStatus(), TaskStatus.COMPLETED.getTaskName(), "OP: Completed tasks: Task Status is not correct");
        assertEquals(taskDetails.getType(), TaskDetailsType.VERIFY_TASK_WAS_COMPLETED_ON_THE_CLOUD, "OP: Completed tasks: Task Type is not correct");
        assertEquals(taskDetails.getDescription(), workFlowName, "OP: Completed tasks: Task Description");
        assertEquals(taskDetails.getStartedBy(), user1, "OP: Completed tasks: 'Started by' value is not correct");

        // 63 Navigate to Workflows I've started
        myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

        // Verify the workflow is not displayed anymore in Active WorkFlows
        assertFalse(myWorkFlowsPage.isWorkFlowPresent(workFlowName), "OP: WorkFlow I've started: workflow is displayed but it was completed");

        // Select Completed workflows and verify workflow is displayed
        myWorkFlowsPage = myWorkFlowsPage.selectCompletedWorkFlows().render();

        assertEquals(myWorkFlowsPage.getSubTitle(), "Completed Workflows", "Completed Workflow page is not opened");
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName), "OP: WorkFlow I've started: Completed Filter: Workflow name is not displayed");

        // Verify the completed workflow details are displayed correctly.
        workFlowDetails = myWorkFlowsPage.getWorkFlowDetails(workFlowName);

        assertEquals(workFlowDetails.size(), 1);
        assertEquals(workFlowDetails.get(0).getWorkFlowName(), workFlowName, "OP: WorkFlow I've started: Completed Filter: Workflow name is not displayed");
        assertEquals(workFlowDetails.get(0).getDue(), dueDate, "OP: WorkFlow I've started: Completed Filter: Due Date is not correct");
        assertEquals(getLocalDate(workFlowDetails.get(0).getStartDate()), getToDaysLocalDate(),
                "OP: WorkFlow I've started: Completed Filter: Start Date is not correct");
        assertEquals(getLocalDate(workFlowDetails.get(0).getEndDate()), getToDaysLocalDate(),
                "OP: WorkFlow I've started: Completed Filter: End Date is not correct");
        assertEquals(workFlowDetails.get(0).getType(), WorkFlowType.CLOUD_TASK_OR_REVIEW,
                "OP: WorkFlow I've started: Completed Filter: Workflow Type is not correct");
        assertEquals(workFlowDetails.get(0).getDescription(), WorkFlowDescription.CREATE_A_TASK_OR_START_A_REVIEW,
                "OP: WorkFlow I've started: Completed Filter: Workflow Description is not correct");

        workFlowDetailsPage = myWorkFlowsPage.selectWorkFlow(workFlowName).render();

        generalInfo = workFlowDetailsPage.getWorkFlowDetailsGeneralInfo();

        assertEquals(getLocalDate(generalInfo.getCompletedDate()), getToDaysLocalDate(), "OP: Workflow Details Page: Completed date is not correct");
        assertEquals(generalInfo.getStatus(), WorkFlowStatus.WORKFLOW_COMPLETE, "OP: Workflow Details Page: Status is not correct");

        assertTrue(workFlowDetailsPage.isNoTasksMessageDisplayed(), "OP: Workflow Details Page: some tasks for the workflow are displayed.");

        // 66 Verify 'History' section
        historyList = workFlowDetailsPage.getWorkFlowHistoryList();

        assertEquals(historyList.size(), 2, "OP: Workflow Details Page: History Section: count of tasks is not correct");
        assertEquals(historyList.get(0).getType(), WorkFlowHistoryType.VERIFY_TASK_COMPLETED_ON_CLOUD,
                "OP: Workflow Details Page: History Section: Type of first task is not correct");
        assertEquals(historyList.get(0).getCompletedBy(), getUserFullName(user1),
                "OP: Workflow Details Page: History Section: 'Completed bBlue for first task is not correct");
        assertEquals(historyList.get(0).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE,
                "OP: Workflow Details Page: History Section: 'OutCome' value for first task is not correct");
        assertEquals(historyList.get(0).getComment(), "", "OP: Workflow Details Page: History Section: comment has been added to first task");

        assertEquals(historyList.get(1).getType(), WorkFlowHistoryType.START_TASK_OR_REVIEW_ON_CLOUD,
                "OP: Workflow Details Page: History Section: Type of second task is not correct");
        assertEquals(historyList.get(1).getCompletedBy(), getUserFullName(cloudUser),
                "OP: Workflow Details Page: History Section: 'Completed By' value for second task is not correct");
        assertEquals(getLocalDate(historyList.get(1).getCompletedDate()), getToDaysLocalDate(),
                "OP: Workflow Details Page: History Section: 'Completed Date' value for second task is not correct");
        assertEquals(historyList.get(1).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE,
                "OP: Workflow Details Page: History Section: 'OutCome' value for second task is not correct");
        assertEquals(historyList.get(1).getComment(), "", "OP: Workflow Details Page: History Section: comment has been added to second task");

        ShareUser.logout(drone);
    }

    @Test(groups = "Hybrid")
    public void AONE_15734() throws Exception
    {
        String testName = getTestName();
        String uniqueData = String.valueOf(System.currentTimeMillis());
        String opUser = getUserNameForDomain(testName + uniqueData, DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { opUser };

        String cloudUser = getUserNameForDomain(testName + uniqueData, DOMAIN_HYBRID);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + uniqueData + "-OP";
        String cloudSiteName = getSiteName(testName) + uniqueData + "-CL";
        String fileName = getFileName(testName) + uniqueData + ".txt";
        String workFlowName = uniqueData + "Workflow";
        String comment = uniqueData + "Comment";
        String[] fileInfo = { fileName, DOCLIB };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, DOMAIN_HYBRID, "1000");

        // Login as CloudUser1 and create a site
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login to OP as User1, Create site and upload file
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName);
        // Upload a document
        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        // Start Simple Cloud Task Workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName);
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setMessage(workFlowName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);
        formDetails.setLockOnPremise(false);
        // Create Workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails).render();
        ShareUser.logout(drone);

        // Login as CloudUser User
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).render();
        // 1. In Cloud open the Simple Cloud Task, which was created in the pre-conditions.
        findTasks(hybridDrone, workFlowName);
        TaskDetailsPage taskDetailsPage = myTasksPage.selectViewTasks(workFlowName).render();
        EditTaskPage editTaskPage = taskDetailsPage.selectEditButton().render();
        // 2.Add any comment.
        editTaskPage.enterComment(comment);
        editTaskPage.selectStatusDropDown(TaskStatus.COMPLETED);
        // 3.Click Task Done button.
        editTaskPage.selectTaskDoneButton().render();
        // Verifying the workflow disappears from the user's Tasks list.
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).render();
        assertFalse(myTasksPage.isTaskPresent(workFlowName), "Completed task is displayed for Active filter");
        ShareUser.logout(hybridDrone);

        // 4. On-premise verify the Tasks list.
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);

        // Verify a new task is displayed for OP user
        assertTrue(ShareUser.checkIfTaskIsPresent(drone, workFlowName), "Completed task (on Cloud) is not displayed for OP.");

        // Verify the task details
        TaskDetails taskDetails = myTasksPage.getTaskDetails(workFlowName);

        assertEquals(taskDetails.getTaskName(), workFlowName, "OP: Task name is not displayed on My Task page (task was completed on Cloud)");
        assertEquals(getLocalDate(taskDetails.getStartDate()), getToDaysLocalDate(),
                "OP: Start date is not correct on My Task page (task was completed on Cloud)");
        assertNull(taskDetails.getEndDate(), "OP: End date is not correct on My Task page (task was completed on Cloud)");
        assertEquals(taskDetails.getStatus(), TaskStatus.NOTYETSTARTED.getTaskName(),
                "OP: Task Status is not correct on My Task page (task was completed on Cloud)");
        assertEquals(taskDetails.getType(), TaskDetailsType.VERIFY_TASK_WAS_COMPLETED_ON_THE_CLOUD,
                "OP: Task type is not correct on My Task page (task was completed on Cloud)");
        assertEquals(taskDetails.getDescription(), workFlowName,
                "OP: Description is not correct on My Task page (task was completed on Cloud)Verifying Workflow Description");
        assertEquals(taskDetails.getStartedBy(), opUser, "OP: 'Started by' value is not correct on My Task page (task was completed on Cloud)");

        findTasks(drone, workFlowName);
        taskDetailsPage = myTasksPage.selectViewTasks(workFlowName).render();
        editTaskPage = taskDetailsPage.selectEditButton().render();
        editTaskPage.selectTaskDoneButton().render();
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        assertFalse(myTasksPage.isTaskPresent(workFlowName), "Completed Task is still displayed.");
        ShareUser.logout(drone);
    }

    /**
     * AONE-15735:Create Cloud Review
     */
    @Test(groups = "Hybrid")
    public void AONE_15735() throws Exception
    {
        String testName = getTestName();
        String uniqueData = String.valueOf(System.currentTimeMillis());
        String user1 = getUserNameForDomain(testName + uniqueData + "-op", DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName + uniqueData + "-cl", DOMAIN_HYBRID);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String reviewer1 = getUserNameForDomain(testName + uniqueData + "-1", DOMAIN_HYBRID).replace("user", "reviewer");
        String[] reviewerInfo1 = new String[] { reviewer1 };

        String reviewer2 = getUserNameForDomain(testName + uniqueData + "-2", DOMAIN_HYBRID).replace("user", "reviewer");
        String[] reviewerInfo2 = new String[] { reviewer2 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, reviewerInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, reviewerInfo2);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, DOMAIN_HYBRID, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        String opUser = getUserNameForDomain(testName + uniqueData + "-op", DOMAIN_HYBRID);
        String opSiteName = getSiteName(testName) + uniqueData + "-OP";
        String cloudSite = getSiteName(testName) + uniqueData + "-CL";
        String fileName = getFileName(testName) + uniqueData + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        String workFlowName = testName + uniqueData + "-WorkFlow";
        String due = getDueDateString();
        DateTime dueDate = getDueDate(due);
        int requiredApprovalPercentage = 50;

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(hybridDrone);

        CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, cloudUser, reviewer1, getSiteShortname(cloudSite), "SiteContributor", "");
        CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, cloudUser, reviewer2, getSiteShortname(cloudSite), "SiteContributor", "");

        // Login as User1 (OP)
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        // Select "Cloud Task or Review" from select a workflow dropdown
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startCloudReviewTaskWorkFlow(drone);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        List<String> userNames = new ArrayList<String>();
        userNames.add(reviewer1);
        userNames.add(reviewer2);
        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(due);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setApprovalPercentage(requiredApprovalPercentage);
        formDetails.setSiteName(cloudSite);
        formDetails.setReviewers(userNames);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(fileName, opSiteName);

        // Fill the form details and start workflow
        MyWorkFlowsPage myWorkFlowsPage = ((MyWorkFlowsPage) cloudTaskOrReviewPage.startWorkflow(formDetails)).render();

        List<WorkFlowDetails> workFlowDetails = myWorkFlowsPage.getWorkFlowDetails(workFlowName);

        assertEquals(workFlowDetails.size(), 1);
        assertEquals(workFlowDetails.get(0).getWorkFlowName(), formDetails.getMessage(), "Verifying workflow name");
        assertEquals(workFlowDetails.get(0).getDue(), dueDate, "Verifying workflow due date");
        assertEquals(getLocalDate(workFlowDetails.get(0).getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertNull(workFlowDetails.get(0).getEndDate(), "Verify Workflow End date is NULL as the workflow is still active");
        assertEquals(workFlowDetails.get(0).getType(), WorkFlowType.CLOUD_TASK_OR_REVIEW, "Verifying Workflow type");
        assertEquals(workFlowDetails.get(0).getDescription(), WorkFlowDescription.CREATE_A_TASK_OR_START_A_REVIEW, "Verifying Workflow Description");

        // Open Site Document Library, verify the document is part of the workflow, document is synced and verify Sync Status
        DocumentLibraryPage documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName);

        // Verify Content is part of workflow and shows CloudSync icon
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the document is part of a workflow");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Verifying the document is synced");

        // Verify the sync status for the Content
        assertTrue(ShareUser.checkIfContentIsSynced(drone, fileName), "Verifying the Sync Status is \"Synced\"");

        ShareUser.logout(drone);

        // Login as reviewer1 User,
        ShareUser.login(hybridDrone, reviewer1, DEFAULT_PASSWORD);

        documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite);

        assertTrue(documentLibraryPage.isFileVisible(fileName), "Verifying the document is displayed");

        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the document is part of Workflow");

        // Navigate to MyTasks page
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        assertTrue(AbstractWorkflow.checkIfTaskIsPresent(hybridDrone, workFlowName, true), "Cloud: task is not displayed");
        // Verify Task Details are displayed correctly
        TaskDetails taskDetails = myTasksPage.getTaskDetails(workFlowName);

        assertEquals(taskDetails.getTaskName(), workFlowName, "Verifying workflow name");
        assertEquals(taskDetails.getDue(), ShareUserWorkFlow.getDueDateOnMyTaskPage(due), "Verifying workflow due date");
        assertEquals(getLocalDate(taskDetails.getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertEquals(taskDetails.getStatus(), TaskStatus.NOTYETSTARTED.getTaskName(), "Verifying status");
        assertEquals(taskDetails.getType(), TaskDetailsType.REVIEW, "Verifying Task type");
        assertEquals(taskDetails.getDescription(), "Review", "Verifying Workflow Description");
        assertEquals(taskDetails.getStartedBy(), cloudUser, "Verifying Started by user");

        ShareUser.logout(hybridDrone);

        // Login as reviewer2 User,
        ShareUser.login(hybridDrone, reviewer2, DEFAULT_PASSWORD);

        documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite);

        assertTrue(documentLibraryPage.isFileVisible(fileName), "Verifying the document is displayed");

        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the document is part of Workflow");

        // Navigate to MyTasks page
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

        // Verify Task Details are displayed correctly
        taskDetails = myTasksPage.getTaskDetails(workFlowName);

        assertEquals(taskDetails.getTaskName(), workFlowName, "Verifying workflow name");
        assertEquals(taskDetails.getDue(), ShareUserWorkFlow.getDueDateOnMyTaskPage(due), "Verifying workflow due date");
        assertEquals(getLocalDate(taskDetails.getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertEquals(taskDetails.getStatus(), TaskStatus.NOTYETSTARTED.getTaskName(), "Verifying status");
        assertEquals(taskDetails.getType(), TaskDetailsType.REVIEW, "Verifying Task type");
        assertEquals(taskDetails.getDescription(), "Review", "Verifying Workflow Description");
        assertEquals(taskDetails.getStartedBy(), cloudUser, "Verifying Started by user");

        // Edit task and mark it as complete
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, workFlowName, TaskStatus.COMPLETED, EditTaskAction.APPROVE);

        // Verify task is not displayed in Active Tasks list
        assertFalse(myTasksPage.isTaskPresent(workFlowName));

        // Select Completed Tasks and verify Task is displayed and the task details are correct.
        myTasksPage = myTasksPage.selectCompletedTasks().render();

        assertTrue(myTasksPage.isTaskPresent(workFlowName));

        taskDetails = myTasksPage.getTaskDetails(workFlowName);

        assertEquals(taskDetails.getTaskName(), workFlowName, "Verifying workflow name");
        assertEquals(taskDetails.getDue(), ShareUserWorkFlow.getDueDateOnMyTaskPage(due), "Verifying workflow due date");
        assertEquals(getLocalDate(taskDetails.getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertEquals(getLocalDate(taskDetails.getEndDate()), getToDaysLocalDate(), "Verify Workflow End date");
        assertEquals(taskDetails.getStatus(), TaskStatus.COMPLETED.getTaskName(), "Verifying status");
        assertEquals(taskDetails.getType(), TaskDetailsType.REVIEW, "Verifying Task type");
        assertEquals(taskDetails.getDescription(), "Review", "Verifying Workflow Description");
        assertEquals(taskDetails.getStartedBy(), cloudUser, "Verifying Started by user");
        ShareUser.logout(hybridDrone);

        // Login as reviewer1 User,
        ShareUser.login(hybridDrone, reviewer1, DEFAULT_PASSWORD);

        // Navigate to MyTasks page
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

        // Verify the task is disappeared from tasks list as it met 50% approval rate
        assertFalse(myTasksPage.isTaskPresent(workFlowName));

        myTasksPage = myTasksPage.selectCompletedTasks().render();
        assertTrue(myTasksPage.isTaskPresent(workFlowName));

        ShareUser.logout(hybridDrone);

        // Login as OP user
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

        // Open My Tasks page
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);

        // Verify a new task is displayed for OP user
        assertTrue(ShareUser.checkIfTaskIsPresent(drone, workFlowName));

        // Verify the task details
        taskDetails = myTasksPage.getTaskDetails(workFlowName);

        assertEquals(taskDetails.getTaskName(), workFlowName, "Verifying workflow name");
        assertEquals(taskDetails.getDue(), ShareUserWorkFlow.getDueDateOnMyTaskPage(due), "Verifying workflow due date");
        assertEquals(getLocalDate(taskDetails.getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertNull(taskDetails.getEndDate(), "Verify Workflow End date is NULL as the task is still active");
        assertEquals(taskDetails.getStatus(), TaskStatus.NOTYETSTARTED.getTaskName(), "Verifying status");
        assertEquals(taskDetails.getType(), TaskDetailsType.DOCUMENT_WAS_APPROVED_ON_THE_CLOUD, "Verifying Task type");
        assertEquals(taskDetails.getDescription(), workFlowName, "Verifying Workflow Description");
        assertEquals(taskDetails.getStartedBy(), opUser, "Verifying Started by user");

        // Edit the task and complete the task
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(drone, workFlowName, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);

        // Verify the task is disappeared from Active Tasks list
        assertFalse(myTasksPage.isTaskPresent(workFlowName));

        // Select Completed tasks and verify the task is displayed and the details are accurate
        myTasksPage = myTasksPage.selectCompletedTasks().render();

        assertTrue(myTasksPage.isTaskPresent(workFlowName));

        taskDetails = myTasksPage.getTaskDetails(workFlowName);

        assertEquals(taskDetails.getTaskName(), workFlowName, "Verifying workflow name");
        assertEquals(taskDetails.getDue(), ShareUserWorkFlow.getDueDateOnMyTaskPage(due), "Verifying workflow due date");
        assertEquals(getLocalDate(taskDetails.getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertEquals(getLocalDate(taskDetails.getEndDate()), getToDaysLocalDate(), "Verify Workflow End date");
        assertEquals(taskDetails.getStatus(), TaskStatus.COMPLETED.getTaskName(), "Verifying status");
        assertEquals(taskDetails.getType(), TaskDetailsType.DOCUMENT_WAS_APPROVED_ON_THE_CLOUD, "Verifying Task type");
        assertEquals(taskDetails.getDescription(), workFlowName, "Verifying Workflow Description");
        assertEquals(taskDetails.getStartedBy(), opUser, "Verifying Started by user");

        // Navigate to Workflows I've started
        myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

        // Verify the workflow is not displayed anymore in Active WorkFlows
        assertFalse(myWorkFlowsPage.isWorkFlowPresent(workFlowName));

        // Select Completed workflows and verify workflow is displayed
        myWorkFlowsPage = myWorkFlowsPage.selectCompletedWorkFlows().render();

        assertEquals(myWorkFlowsPage.getSubTitle(), "Completed Workflows");
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName));

        // Verify the completed workflow details are displayed correctly.
        workFlowDetails = myWorkFlowsPage.getWorkFlowDetails(workFlowName);

        assertEquals(workFlowDetails.size(), 1);
        assertEquals(workFlowDetails.get(0).getWorkFlowName(), workFlowName, "Verifying workflow name");
        assertEquals(workFlowDetails.get(0).getDue(), dueDate, "Verifying workflow due date");
        assertEquals(getLocalDate(workFlowDetails.get(0).getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertEquals(getLocalDate(workFlowDetails.get(0).getEndDate()), getToDaysLocalDate(), "Verify Workflow End date");
        assertEquals(workFlowDetails.get(0).getType(), WorkFlowType.CLOUD_TASK_OR_REVIEW, "Verifying Workflow type");
        assertEquals(workFlowDetails.get(0).getDescription(), WorkFlowDescription.CREATE_A_TASK_OR_START_A_REVIEW, "Verifying Workflow Description");

        ShareUser.logout(drone);
    }

    @Test(groups = "Hybrid")
    public void AONE_15736() throws Exception
    {
        String testName = getTestName();
        String uniqueData = String.valueOf(System.currentTimeMillis());
        String user1 = getUserNameForDomain(testName + uniqueData + "-op", DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName + uniqueData + "-cl", DOMAIN_HYBRID);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String reviewer1 = getUserNameForDomain(testName + uniqueData + "-1", DOMAIN_HYBRID).replace("user", "reviewer");
        String[] reviewerInfo1 = new String[] { reviewer1 };

        String reviewer2 = getUserNameForDomain(testName + uniqueData + "-2", DOMAIN_HYBRID).replace("user", "reviewer");
        String[] reviewerInfo2 = new String[] { reviewer2 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, reviewerInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, reviewerInfo2);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, DOMAIN_HYBRID, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        String opUser = getUserNameForDomain(testName + uniqueData + "-op", DOMAIN_HYBRID);
        String opSiteName = getSiteName(testName) + uniqueData + "-OP";
        String cloudSite = getSiteName(testName) + uniqueData + "-CL";
        String fileName = getFileName(testName) + uniqueData + ".txt";
        String comment = uniqueData + "comment";
        String[] fileInfo = { fileName, DOCLIB };

        String workFlowName = testName + uniqueData + "-WorkFlow";
        String due = getDueDateString();
        DateTime dueDate = getDueDate(due);
        int requiredApprovalPercentage = 100;

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(hybridDrone);

        CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, cloudUser, reviewer1, getSiteShortname(cloudSite), "SiteContributor", "");
        CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, cloudUser, reviewer2, getSiteShortname(cloudSite), "SiteContributor", "");

        // Login as User1 (OP)
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        // Select "Cloud Task or Review" from select a workflow dropdown
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startCloudReviewTaskWorkFlow(drone);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        List<String> userNames = new ArrayList<String>();
        userNames.add(reviewer1);
        userNames.add(reviewer2);
        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(due);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setApprovalPercentage(requiredApprovalPercentage);
        formDetails.setSiteName(cloudSite);
        formDetails.setReviewers(userNames);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(fileName, opSiteName);

        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        // Open Site Document Library, verify the document is part of the workflow, document is synced and verify Sync Status
        SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName);
        // Verify the sync status for the Content
        assertTrue(ShareUser.checkIfContentIsSynced(drone, fileName), "Verifying the Sync Status is \"Synced\"");
        ShareUser.logout(drone);

        // Login as reviewer1 User,
        ShareUser.login(hybridDrone, reviewer1, DEFAULT_PASSWORD);
        // Navigate to MyTasks page
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        assertTrue(AbstractWorkflow.checkIfTaskIsPresent(hybridDrone, workFlowName, true), "Cloud: task is not displayed");
        // Verify Task Details are displayed correctly
        TaskDetails taskDetails = myTasksPage.getTaskDetails(workFlowName);

        assertEquals(taskDetails.getTaskName(), workFlowName, "Verifying workflow name");
        assertEquals(taskDetails.getDue(), ShareUserWorkFlow.getDueDateOnMyTaskPage(due), "Verifying workflow due date");
        assertEquals(getLocalDate(taskDetails.getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertEquals(taskDetails.getStatus(), TaskStatus.NOTYETSTARTED.getTaskName(), "Verifying status");
        assertEquals(taskDetails.getType(), TaskDetailsType.REVIEW, "Verifying Task type");
        assertEquals(taskDetails.getDescription(), "Review", "Verifying Workflow Description");
        assertEquals(taskDetails.getStartedBy(), cloudUser, "Verifying Started by user");
        // Edit task and mark it as complete
        // 3. Click Approve button.
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, workFlowName, TaskStatus.COMPLETED, EditTaskAction.APPROVE);

        // Verify task is not displayed in Active Tasks list
        assertFalse(myTasksPage.isTaskPresent(workFlowName), "Completed task is still displayed");
        ShareUser.logout(hybridDrone);

        // 4. On-premise verify the Tasks list.
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        // Open My Tasks page
        ShareUserWorkFlow.navigateToMyTasksPage(drone);
        // The Cloud Review Task is not present in the list.
        assertFalse(ShareUser.checkIfTaskIsPresent(drone, workFlowName), "OP: Task is displayed but it was not fully completed");
        ShareUser.logout(drone);

        // 5. In Cloud open the Cloud Review Task, which was created in the pre-conditions, as another user.
        ShareUser.login(hybridDrone, reviewer2, DEFAULT_PASSWORD);
        // Navigate to MyTasks page
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        assertTrue(AbstractWorkflow.checkIfTaskIsPresent(hybridDrone, workFlowName, true), "Cloud: task is not displayed");
        // Verify Task Details are displayed correctly
        taskDetails = myTasksPage.getTaskDetails(workFlowName);

        assertEquals(taskDetails.getTaskName(), workFlowName, "Verifying workflow name");
        assertEquals(taskDetails.getDue(), ShareUserWorkFlow.getDueDateOnMyTaskPage(due), "Verifying workflow due date");
        assertEquals(getLocalDate(taskDetails.getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertEquals(taskDetails.getStatus(), TaskStatus.NOTYETSTARTED.getTaskName(), "Verifying status");
        assertEquals(taskDetails.getType(), TaskDetailsType.REVIEW, "Verifying Task type");
        assertEquals(taskDetails.getDescription(), "Review", "Verifying Workflow Description");
        assertEquals(taskDetails.getStartedBy(), cloudUser, "Verifying Started by user");

        // 6-7. Add any comment. and Click Approve button.
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, workFlowName, TaskStatus.COMPLETED, comment, EditTaskAction.APPROVE);

        // Verify task is not displayed in Active Tasks list
        assertFalse(myTasksPage.isTaskPresent(workFlowName), "Completed task is still displayed for User's Task List");
        ShareUser.logout(hybridDrone);

        // 8 On-premise verify the Tasks list.
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

        // Open My Tasks page
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);

        // Verify a new task is displayed for OP user
        assertTrue(ShareUser.checkIfTaskIsPresent(drone, workFlowName), "OP: Completed (on Cloud) task is not displayed");

        // Verify the task details
        taskDetails = myTasksPage.getTaskDetails(workFlowName);

        assertEquals(taskDetails.getTaskName(), workFlowName, "Verifying workflow name");
        assertEquals(taskDetails.getDue(), ShareUserWorkFlow.getDueDateOnMyTaskPage(due), "Verifying workflow due date");
        assertEquals(getLocalDate(taskDetails.getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertNull(taskDetails.getEndDate(), "Verify Workflow End date is NULL as the task is still active");
        assertEquals(taskDetails.getStatus(), TaskStatus.NOTYETSTARTED.getTaskName(), "Verifying status");
        assertEquals(taskDetails.getType(), TaskDetailsType.DOCUMENT_WAS_APPROVED_ON_THE_CLOUD, "Verifying Task type");
        assertEquals(taskDetails.getDescription(), workFlowName, "Verifying Workflow Description");
        assertEquals(taskDetails.getStartedBy(), opUser, "Verifying Started by user");

        // Edit the task and complete the task
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(drone, workFlowName, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);

        // Verify the task is disappeared from Active Tasks list
        assertFalse(myTasksPage.isTaskPresent(workFlowName));

        // Select Completed tasks and verify the task is displayed and the details are accurate
        myTasksPage = myTasksPage.selectCompletedTasks().render();

        assertTrue(myTasksPage.isTaskPresent(workFlowName));

        taskDetails = myTasksPage.getTaskDetails(workFlowName);

        assertEquals(taskDetails.getTaskName(), workFlowName, "Verifying workflow name");
        assertEquals(taskDetails.getDue(), ShareUserWorkFlow.getDueDateOnMyTaskPage(due), "Verifying workflow due date");
        assertEquals(getLocalDate(taskDetails.getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertEquals(getLocalDate(taskDetails.getEndDate()), getToDaysLocalDate(), "Verify Workflow End date");
        assertEquals(taskDetails.getStatus(), TaskStatus.COMPLETED.getTaskName(), "Verifying status");
        assertEquals(taskDetails.getType(), TaskDetailsType.DOCUMENT_WAS_APPROVED_ON_THE_CLOUD, "Verifying Task type");
        assertEquals(taskDetails.getDescription(), workFlowName, "Verifying Workflow Description");
        assertEquals(taskDetails.getStartedBy(), opUser, "Verifying Started by user");
        ShareUser.logout(drone);
    }

    /**
     * AONE-15737: Reject Cloud Review Task
     */
    @Test(groups = "Hybrid")
    public void AONE_15737() throws Exception
    {
        String testName = getTestName();
        String uniqueData = String.valueOf(System.currentTimeMillis());
        String user1 = getUserNameForDomain(testName + uniqueData, DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName + uniqueData, DOMAIN_HYBRID);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String reviewer1 = getUserNameForDomain(testName + uniqueData, DOMAIN_HYBRID).replace("user", "reviewer-1");
        String[] reviewerInfo1 = new String[] { reviewer1 };

        String reviewer2 = getUserNameForDomain(testName + uniqueData, DOMAIN_HYBRID).replace("user", "reviewer-2");
        String[] reviewerInfo2 = new String[] { reviewer2 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, reviewerInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, reviewerInfo2);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, DOMAIN_HYBRID, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        String opSiteName = getSiteName(testName) + uniqueData + "-OP";
        String cloudSite = getSiteName(testName) + uniqueData + "-CL";
        String fileName = getFileName(testName) + uniqueData + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        String workFlowName = testName + uniqueData + "-WorkFlow";
        String due = getDueDateString();
        DateTime dueDate = getDueDate(due);
        int requiredApprovalPercentage = 50;
        String cloudComment = testName + uniqueData + "-Cloud Comment";

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(hybridDrone);

        CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, cloudUser, reviewer1, getSiteShortname(cloudSite), "SiteContributor", "");
        CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, cloudUser, reviewer2, getSiteShortname(cloudSite), "SiteContributor", "");

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo).render();

        // Select "Cloud Task or Review" from select a workflow dropdown
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        List<String> userNames = new ArrayList<String>();
        userNames.add(reviewer1);
        userNames.add(reviewer2);
        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(due);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setApprovalPercentage(requiredApprovalPercentage);
        formDetails.setSiteName(cloudSite);
        formDetails.setReviewers(userNames);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

        // Fill the form details and start workflow
        documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        // Verify the content is part of Workflow and cloudSync icon appears
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the document is part of a workflow");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Verifying the document is synced");

        // Verify the cloud sync status for the content
        assertTrue(ShareUser.checkIfContentIsSynced(drone, fileName), "Verifying the Sync Status is \"Synced\"");

        ShareUser.logout(drone);

        // Login as reviewer1 User,
        ShareUser.login(hybridDrone, reviewer1, DEFAULT_PASSWORD);

        documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite);

        // Verify Synced doc is displayed in Site Document Library
        assertTrue(documentLibraryPage.isFileVisible(fileName), "Verifying the document is displayed");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the document is part of Workflow");

        // Navigate to MyTasks page
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

        // Verify Task is displayed
        assertTrue(AbstractWorkflow.checkIfTaskIsPresent(hybridDrone, workFlowName, true), "Cloud: task is not displayed");

        // Edit task and Reject
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, workFlowName, TaskStatus.COMPLETED, cloudComment, EditTaskAction.REJECT);

        // Verify task is not displayed in Active Tasks list
        assertFalse(myTasksPage.isTaskPresent(workFlowName));

        // Select Completed Tasks and verify Task is displayed and the task details are correct.
        myTasksPage = myTasksPage.selectCompletedTasks().render();

        assertTrue(myTasksPage.isTaskPresent(workFlowName));

        ShareUser.logout(hybridDrone);

        // Login as OP user
        sharePage = ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Open My Tasks page
        ShareUserWorkFlow.navigateToMyTasksPage(drone);

        // Verify a new task is displayed for OP user
        assertFalse(ShareUser.checkIfTaskIsPresent(drone, workFlowName));

        ShareUser.logout(drone);

        // Login as reviewer2 User,
        ShareUser.login(hybridDrone, reviewer2, DEFAULT_PASSWORD);

        documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite);

        // Verify Synced doc is displayed in Site Document Library
        assertTrue(documentLibraryPage.isFileVisible(fileName), "Verifying the document is displayed");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the document is part of Workflow");

        // Navigate to MyTasks page
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

        // Verify Task is displayed
        assertTrue(myTasksPage.isTaskPresent(workFlowName));

        // Edit task and Reject
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, workFlowName, TaskStatus.COMPLETED, cloudComment, EditTaskAction.REJECT);

        // Verify task is not displayed in Active Tasks list
        assertFalse(myTasksPage.isTaskPresent(workFlowName));

        // Select Completed Tasks and verify Task is displayed and the task details are correct.
        myTasksPage = myTasksPage.selectCompletedTasks().render();

        assertTrue(myTasksPage.isTaskPresent(workFlowName));

        ShareUser.logout(hybridDrone);

        // Login as OP user
        sharePage = ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Open My Tasks page
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);

        // Verify a new task is displayed for OP user
        assertTrue(ShareUser.checkIfTaskIsPresent(drone, workFlowName));

        // Verify the task details
        TaskDetails taskDetails = myTasksPage.getTaskDetails(workFlowName);

        assertEquals(taskDetails.getTaskName(), workFlowName, "Verifying workflow name");
        assertEquals(taskDetails.getDue(), ShareUserWorkFlow.getDueDateOnMyTaskPage(due), "Verifying workflow due date");
        assertEquals(getLocalDate(taskDetails.getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertNull(taskDetails.getEndDate(), "Verify Workflow End date is NULL as the task is still active");
        assertEquals(taskDetails.getStatus(), TaskStatus.NOTYETSTARTED.getTaskName(), "Verifying status");
        assertEquals(taskDetails.getType(), TaskDetailsType.DOCUMENT_WAS_REJECTED_ON_THE_CLOUD, "Verifying Task type");
        assertEquals(taskDetails.getDescription(), workFlowName, "Verifying Workflow Description");
        assertEquals(taskDetails.getStartedBy(), user1, "Verifying Started by user");

        // Edit tha task and complete the task
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(drone, workFlowName, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);

        // Verify the task is disappeared from Active Tasks list
        myTasksPage = myTasksPage.selectActiveTasks().render();

        assertFalse(myTasksPage.isTaskPresent(workFlowName));

        // Select Completed tasks and verify the task is displayed and the details are accurate
        myTasksPage = myTasksPage.selectCompletedTasks().render();

        assertTrue(myTasksPage.isTaskPresent(workFlowName));

        taskDetails = myTasksPage.getTaskDetails(workFlowName);

        assertEquals(taskDetails.getTaskName(), workFlowName, "Verifying workflow name");
        assertEquals(taskDetails.getDue(), ShareUserWorkFlow.getDueDateOnMyTaskPage(due), "Verifying workflow due date");
        assertEquals(getLocalDate(taskDetails.getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertEquals(getLocalDate(taskDetails.getEndDate()), getToDaysLocalDate(), "Verify Workflow End date");
        assertEquals(taskDetails.getStatus(), TaskStatus.COMPLETED.getTaskName(), "Verifying status");
        assertEquals(taskDetails.getType(), TaskDetailsType.DOCUMENT_WAS_REJECTED_ON_THE_CLOUD, "Verifying Task type");
        assertEquals(taskDetails.getDescription(), workFlowName, "Verifying Workflow Description");
        assertEquals(taskDetails.getStartedBy(), user1, "Verifying Started by user");

        // Navigate to Workflows I've started
        MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

        // Verify the workflow is not displayed anymore in Active WorkFlows
        myWorkFlowsPage = myWorkFlowsPage.selectActiveWorkFlows().render();

        assertFalse(myWorkFlowsPage.isWorkFlowPresent(workFlowName));

        // Select Completed workflows and verify workflow is displayed
        myWorkFlowsPage = myWorkFlowsPage.selectCompletedWorkFlows().render();

        assertEquals(myWorkFlowsPage.getSubTitle(), "Completed Workflows");
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName));

        // Verify the completed workflow details are displayed correctly.
        List<WorkFlowDetails> workFlowDetails = myWorkFlowsPage.getWorkFlowDetails(workFlowName);

        assertEquals(workFlowDetails.size(), 1);
        assertEquals(workFlowDetails.get(0).getWorkFlowName(), workFlowName, "Verifying workflow name");
        assertEquals(workFlowDetails.get(0).getDue(), dueDate, "Verifying workflow due date");
        assertEquals(getLocalDate(workFlowDetails.get(0).getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertEquals(getLocalDate(workFlowDetails.get(0).getEndDate()), getToDaysLocalDate(), "Verify Workflow End date");
        assertEquals(workFlowDetails.get(0).getType(), WorkFlowType.CLOUD_TASK_OR_REVIEW, "Verifying Workflow type");
        assertEquals(workFlowDetails.get(0).getDescription(), WorkFlowDescription.CREATE_A_TASK_OR_START_A_REVIEW, "Verifying Workflow Description");

        ShareUser.logout(drone);
    }

    /**
     * AONE-15738: Cancel Workflow
     */
    @Test(groups = { "Hybrid" })
    public void AONE_15738() throws Exception
    {
        String testName = getTestName();
        String uniqueData = String.valueOf(System.currentTimeMillis());
        String user1 = getUserNameForDomain(testName + uniqueData, DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName + uniqueData, DOMAIN_HYBRID);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String reviewer1 = getUserNameForDomain(testName + uniqueData, DOMAIN_HYBRID).replace("user", "reviewer");
        String[] reviewerInfo1 = new String[] { reviewer1 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, reviewerInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, DOMAIN_HYBRID, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        String opSiteName = getSiteName(testName) + uniqueData + "-OP";
        String cloudSite = getSiteName(testName) + uniqueData + "-CL";
        String fileName = getFileName(testName) + uniqueData + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        String workFlowName = testName + uniqueData + "-WorkFlow";
        String due = getDueDateString();
        int requiredApprovalPercentage = 50;

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(hybridDrone);

        CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, cloudUser, reviewer1, getSiteShortname(cloudSite), "SiteContributor", "");

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo).render();

        // Select "Cloud Task or Review" from select a workflow dropdown
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName);

        // Select Simple Cloud Task
        cloudTaskOrReviewPage.selectTask(TaskType.CLOUD_REVIEW_TASK);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        List<String> userNames = new ArrayList<String>();
        userNames.add(reviewer1);
        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(due);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setApprovalPercentage(requiredApprovalPercentage);
        formDetails.setSiteName(cloudSite);
        formDetails.setReviewers(userNames);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // Fill the form details and start workflow

        documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        // Verify the document is prat of the workflow, document is synced and verify Sync Status

        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the document is part of a workflow");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Verifying the document is synced");

        assertTrue(ShareUser.checkIfContentIsSynced(drone, fileName), "Verifying the Sync Status is \"Synced\"");

        MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

        assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName));

        ShareUser.logout(drone);

        // Login as reviewer1 User,
        ShareUser.login(hybridDrone, reviewer1, DEFAULT_PASSWORD);

        documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite);

        // Verify Synced doc is displayed in Site Document Library
        assertTrue(documentLibraryPage.isFileVisible(fileName), "Verifying the document is displayed");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the document is part of Workflow");

        // Navigate to MyTasks page
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        assertTrue(AbstractWorkflow.checkIfTaskIsPresent(hybridDrone, workFlowName, true), "Verifying the task " + workFlowName + " is displayed");

        // Verify Task Details are displayed correctly
        TaskDetails taskDetails = myTasksPage.getTaskDetails(workFlowName);

        assertEquals(taskDetails.getTaskName(), workFlowName, "Verifying workflow name");
        assertEquals(taskDetails.getDue(), ShareUserWorkFlow.getDueDateOnMyTaskPage(due), "Verifying workflow due date");
        assertEquals(getLocalDate(taskDetails.getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertEquals(taskDetails.getStatus(), TaskStatus.NOTYETSTARTED.getTaskName(), "Verifying status");
        assertEquals(taskDetails.getType(), TaskDetailsType.REVIEW, "Verifying Task type");
        assertEquals(taskDetails.getDescription(), "Review", "Verifying Workflow Description");
        assertEquals(taskDetails.getStartedBy(), user1, "Verifying Started by user");

        ShareUser.logout(hybridDrone);

        // Login as OP user
        sharePage = ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

        assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName), "Verifying the workflow is present");

        WorkFlowDetailsPage workFlowDetailsPage = myWorkFlowsPage.selectWorkFlow(workFlowName).render();

        workFlowDetailsPage.selectCancelWorkFlow().render();

        myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

        assertFalse(myWorkFlowsPage.isWorkFlowPresent(workFlowName), "Verifying the Workflow is not listed in the Active WorkFlows");

        myWorkFlowsPage = myWorkFlowsPage.selectCompletedWorkFlows().render();

        assertFalse(myWorkFlowsPage.isWorkFlowPresent(workFlowName), "Verifying the Workflow is not listed in the Completed WorkFlows");

        ShareUser.logout(drone);

        // Login as reviewer1 User,
        ShareUser.login(hybridDrone, reviewer1, DEFAULT_PASSWORD);

        // Navigate to MyTasks page
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

        // Verify Task Details are displayed correctly
        assertFalse(AbstractWorkflow.checkIfTaskIsPresent(hybridDrone, workFlowName, false),
                "Verifying the task is Removed from tasks list");

        ShareUser.logout(hybridDrone);
    }

    /**
     * AONE-15739: Required approval percentage
     */
    @Test(groups = "Hybrid")
    public void AONE_15739() throws Exception
    {
        String testName = getTestName();
        String uniqueData = String.valueOf(System.currentTimeMillis());
        String user1 = getUserNameForDomain(testName + uniqueData, DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName + uniqueData, DOMAIN_HYBRID);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String reviewer1 = getUserNameForDomain(testName + uniqueData + "-1", DOMAIN_HYBRID).replace("user", "reviewer");
        String[] reviewerInfo1 = new String[] { reviewer1 };

        String reviewer2 = getUserNameForDomain(testName + uniqueData + "-2", DOMAIN_HYBRID).replace("user", "reviewer");
        String[] reviewerInfo2 = new String[] { reviewer2 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, reviewerInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, reviewerInfo2);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, DOMAIN_HYBRID, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        String opSiteName = getSiteName(testName) + uniqueData + "-OP";
        String cloudSite = getSiteName(testName) + uniqueData + "-CL";
        String fileName = getFileName(testName) + uniqueData + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        String workFlowName = testName + uniqueData + "-WorkFlow";
        String due = getDueDateString();
        int requiredApprovalPercentage = 50;

        String cloudComment = testName + uniqueData + "-Cloud Comment";

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(hybridDrone);

        // Invite Reviewer1 and Reviewer 2 to the site as Contributors
        CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, cloudUser, reviewer1, getSiteShortname(cloudSite), "SiteContributor", "");
        CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, cloudUser, reviewer2, getSiteShortname(cloudSite), "SiteContributor", "");

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo).render();

        // Select "Cloud Task or Review" from select a workflow dropdown
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName);

        // Select Cloud Review Task
        cloudTaskOrReviewPage.selectTask(TaskType.CLOUD_REVIEW_TASK);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        List<String> userNames = new ArrayList<String>();
        userNames.add(reviewer1);
        userNames.add(reviewer2);
        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(due);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setApprovalPercentage(requiredApprovalPercentage);
        formDetails.setSiteName(cloudSite);
        formDetails.setReviewers(userNames);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

        // Fill the form details and start workflow
        documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        // Verify the document is prat of the workflow, document is synced and verify Sync Status
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the document is part of a workflow");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Verifying the document is synced");

        assertTrue(ShareUser.checkIfContentIsSynced(drone, fileName), "Verifying the Sync Status is \"Synced\"");

        MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

        assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName), "Verifying workflow exists");

        ShareUser.logout(drone);

        // Login as reviewer1 User,
        ShareUser.login(hybridDrone, reviewer1, DEFAULT_PASSWORD);

        // Navigate to MyTasks page
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

        // Verify task is not displayed in Active Tasks list
        assertTrue(AbstractWorkflow.checkIfTaskIsPresent(hybridDrone, workFlowName, true), "Cloud: task is not displayed");

        ShareUser.logout(hybridDrone);

        // Login as reviewer1 User,
        ShareUser.login(hybridDrone, reviewer2, DEFAULT_PASSWORD);

        // Navigate to MyTasks page
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

        // Verify task is displayed in Active Tasks list
        assertTrue(myTasksPage.isTaskPresent(workFlowName));

        ShareUser.logout(hybridDrone);

        // Login as reviewer1 User,
        ShareUser.login(hybridDrone, reviewer1, DEFAULT_PASSWORD);

        // Navigate to MyTasks page
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, workFlowName, TaskStatus.COMPLETED, cloudComment, EditTaskAction.APPROVE);

        // Verify task is not displayed in Active Tasks list
        assertFalse(myTasksPage.isTaskPresent(workFlowName));
        // Select Completed Tasks and verify the task is present
        myTasksPage = myTasksPage.selectCompletedTasks().render();
        assertTrue(myTasksPage.isTaskPresent(workFlowName));

        ShareUser.logout(hybridDrone);

        // Login as Reviewer2
        sharePage = ShareUser.login(hybridDrone, reviewer2, DEFAULT_PASSWORD);

        // Navigate to MyTasks page
        myTasksPage = sharePage.getNav().selectMyTasks().render();

        // Verify task is not displayed in Active Tasks list
        assertFalse(myTasksPage.isTaskPresent(workFlowName));
        // Select Completed Tasks and verify the task is NOT present
        myTasksPage = myTasksPage.selectCompletedTasks().render();
        assertTrue(myTasksPage.isTaskPresent(workFlowName));

        ShareUser.logout(hybridDrone);

        // Login as OP user
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Open My Tasks page
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);

        myTasksPage = myTasksPage.selectActiveTasks().render();

        // Verify a new task is displayed for OP user
        assertTrue(ShareUser.checkIfTaskIsPresent(drone, workFlowName));

        // Verify the task details
        TaskDetails taskDetails = myTasksPage.getTaskDetails(workFlowName);

        assertEquals(taskDetails.getTaskName(), workFlowName, "Verifying workflow name");
        assertEquals(taskDetails.getDue(), ShareUserWorkFlow.getDueDateOnMyTaskPage(due), "Verifying workflow due date");
        assertEquals(getLocalDate(taskDetails.getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertNull(taskDetails.getEndDate(), "Verify Workflow End date is NULL as the task is still active");
        assertEquals(taskDetails.getStatus(), TaskStatus.NOTYETSTARTED.getTaskName(), "Verifying status");
        assertEquals(taskDetails.getType(), TaskDetailsType.DOCUMENT_WAS_APPROVED_ON_THE_CLOUD, "Verifying Task type");
        assertEquals(taskDetails.getDescription(), workFlowName, "Verifying Workflow Description");
        assertEquals(taskDetails.getStartedBy(), user1, "Verifying Started by user");

        TaskDetailsPage taskDetailsPage = myTasksPage.selectViewTasks(workFlowName).render();

        assertTrue(taskDetailsPage.getComment().contains(cloudComment));

        ShareUser.logout(drone);
    }

    @Test(groups = "Hybrid")
    public void AONE_15740() throws Exception
    {

        String testName = getTestName();
        String uniqueData = String.valueOf(System.currentTimeMillis());
        String user1 = getUserNameForDomain(testName + uniqueData, DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName + uniqueData, DOMAIN_HYBRID);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, DOMAIN_HYBRID, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        String opSiteName = getSiteName(testName) + uniqueData + "-OP";
        String cloudSite = getSiteName(testName) + uniqueData + "-CL";

        String fileName1 = getFileName(testName) + uniqueData + "-1.txt";
        String[] fileInfo1 = { fileName1, DOCLIB };

        String fileName2 = getFileName(testName) + uniqueData + "-2.txt";
        String[] fileInfo2 = { fileName2, DOCLIB };

        String fileName3 = getFileName(testName) + uniqueData + "-3.txt";
        String[] fileInfo3 = { fileName3, DOCLIB };

        String workFlowName1 = testName + uniqueData + "-1-WF";
        String workFlowName2 = testName + uniqueData + "-2-WF";
        String workFlowName3 = testName + uniqueData + "-3-WF";
        String dueDate = getDueDateString();

        String cloudComment1 = testName + uniqueData + "-1-Cloud Comment";
        String cloudComment2 = testName + uniqueData + "-2-Cloud Comment";
        String cloudComment3 = testName + uniqueData + "-3-Cloud Comment";

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        // Create Site
        SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        // Open Document library, Upload 3 files
        siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();

        ShareUser.uploadFileInFolder(drone, fileInfo1).render();
        ShareUser.uploadFileInFolder(drone, fileInfo2).render();
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo3).render();

        // Select "Cloud Task or Review" from select a workflow dropdown
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName1);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setDueDate(dueDate);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setAssignee(cloudUser);

        // Create Workflow1 using File1 (After Completion: Keep content synced on cloud)
        formDetails.setMessage(workFlowName1);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        cloudTaskOrReviewPage.selectTask(TaskType.SIMPLE_CLOUD_TASK);
        documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        // Create Workflow2 using File2 (After Completion: Keep content on cloud and remove sync)
        formDetails.setMessage(workFlowName2);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENTREMOVESYNC);

        documentLibraryPage.renderItem(maxWaitTime, fileName2);
        // Select "Cloud Task or Review" from select a workflow dropdown
        cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName2);

        cloudTaskOrReviewPage.selectTask(TaskType.SIMPLE_CLOUD_TASK);
        documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        // Create Workflow3 using File3 (After Completion: Delete content on cloud and remove sync)
        formDetails.setMessage(workFlowName3);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

        documentLibraryPage.renderItem(maxWaitTime, fileName3);
        cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName3);
        cloudTaskOrReviewPage.selectTask(TaskType.SIMPLE_CLOUD_TASK);
        documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        // Verify all files are part of the workflow, and cloud synced
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the File1 is synced");
        assertTrue(ShareUser.checkIfContentIsSynced(drone, fileName1), "Verifying the Sync Status is \"Synced\"");

        drone.refresh();
        documentLibraryPage.render();

        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName2).isPartOfWorkflow(), "Verifying the File2 is part of a workflow");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName2).isCloudSynced(), "Verifying the File2 is synced");
        assertTrue(ShareUser.checkIfContentIsSynced(drone, fileName2), "Verifying the Sync Status is \"Synced\"");

        drone.refresh();
        documentLibraryPage.render();

        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName3).isPartOfWorkflow(), "Verifying the File3 is part of a workflow");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName3).isCloudSynced(), "Verifying the File3 is synced");
        assertTrue(ShareUser.checkIfContentIsSynced(drone, fileName3), "Verifying the Sync Status is \"Synced\"");

        MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);
        // Verify Workflows are created successfully
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName1), "Verifying workflow1 exists");
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName2), "Verifying workflow2 exists");
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName3), "Verifying workflow3 exists");
        ShareUser.logout(drone);

        // Login as CloudUser User
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Open Site Document Library, verify all files are part of the workflow, and synced
        documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();

        assertTrue(documentLibraryPage.isFileVisible(fileName1), "Verifying File1 exists");
        assertTrue(documentLibraryPage.isFileVisible(fileName2), "Verifying File2 exists");
        assertTrue(documentLibraryPage.isFileVisible(fileName3), "Verifying File3 exists");

        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the File1 is synced");

        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName2).isPartOfWorkflow(), "Verifying the File2 is part of a workflow");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName2).isCloudSynced(), "Verifying the File2 is synced");

        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName3).isPartOfWorkflow(), "Verifying the File3 is part of a workflow");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName3).isCloudSynced(), "Verifying the File3 is synced");

        // Navigate to MyTasks page
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

        // Verify tasks are displayed in Active Tasks list
        assertTrue(AbstractWorkflow.checkIfTaskIsPresent(hybridDrone, workFlowName1, true), "Cloud: task is not displayed");
        assertTrue(AbstractWorkflow.checkIfTaskIsPresent(hybridDrone, workFlowName2, true), "Cloud: task is not displayed");
        assertTrue(AbstractWorkflow.checkIfTaskIsPresent(hybridDrone, workFlowName3, true), "Cloud: task is not displayed");

        // Edit each task and mark them as completed
        ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, workFlowName1, TaskStatus.COMPLETED, cloudComment1, EditTaskAction.TASK_DONE);
        ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, workFlowName2, TaskStatus.COMPLETED, cloudComment2, EditTaskAction.TASK_DONE);
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, workFlowName3, TaskStatus.COMPLETED, cloudComment3, EditTaskAction.TASK_DONE);

        // Verify tasks are NOT displayed in Active Tasks list any more
        assertFalse(myTasksPage.isTaskPresent(workFlowName1));
        assertFalse(myTasksPage.isTaskPresent(workFlowName2));
        assertFalse(myTasksPage.isTaskPresent(workFlowName3));

        // Verify tasks are displayed in Completed Tasks list
        myTasksPage = myTasksPage.selectCompletedTasks().render();
        assertTrue(myTasksPage.isTaskPresent(workFlowName1));
        assertTrue(myTasksPage.isTaskPresent(workFlowName2));
        assertTrue(myTasksPage.isTaskPresent(workFlowName3));

        ShareUser.logout(hybridDrone);

        // Login as OP user
        sharePage = ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Open My Tasks page
        sharePage.getNav().selectMyTasks().render();

        // Verify a new tasks are displayed for OP user in Active Tasks List
        assertTrue(ShareUser.checkIfTaskIsPresent(drone, workFlowName1));
        assertTrue(ShareUser.checkIfTaskIsPresent(drone, workFlowName2));
        assertTrue(ShareUser.checkIfTaskIsPresent(drone, workFlowName3));

        // Edit each task and mark them as completed
        ShareUserWorkFlow.completeTaskFromMyTasksPage(drone, workFlowName1, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);
        ShareUserWorkFlow.completeTaskFromMyTasksPage(drone, workFlowName2, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(drone, workFlowName3, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);

        // Verify the tasks are disappeared from Active Tasks list
        assertFalse(myTasksPage.isTaskPresent(workFlowName1));
        assertFalse(myTasksPage.isTaskPresent(workFlowName2));
        assertFalse(myTasksPage.isTaskPresent(workFlowName3));

        // Select Completed tasks and verify the tasks are displayed
        myTasksPage = myTasksPage.selectCompletedTasks().render();

        assertTrue(myTasksPage.isTaskPresent(workFlowName1));
        assertTrue(myTasksPage.isTaskPresent(workFlowName2));
        assertTrue(myTasksPage.isTaskPresent(workFlowName3));

        // Navigate to Workflows I've Started page and verify tasks are not displayed under Active Workflows page
        myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

        assertFalse(myWorkFlowsPage.isWorkFlowPresent(workFlowName1));
        assertFalse(myWorkFlowsPage.isWorkFlowPresent(workFlowName2));
        assertFalse(myWorkFlowsPage.isWorkFlowPresent(workFlowName3));

        // Select Completed Workflows and verify workflows are displayed
        myWorkFlowsPage = myWorkFlowsPage.selectCompletedWorkFlows().render();

        assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName1));
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName2));
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName3));

        // Open Site Document Library
        documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName).render();

        // Verify File1 is still Synced and not part of a workflow any more
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the document is synced");
        assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the document is part of a workflow");

        // Verify File2 is NOT Synced and NOT part of a workflow
        assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName2).isCloudSynced(), "Verifying the document is synced");
        assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName2).isPartOfWorkflow(), "Verifying the document is part of a workflow");

        // Verify File3 is NOT Synced and NOT part of a workflow
        assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName3).isCloudSynced(), "Verifying the document is synced");
        assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName3).isPartOfWorkflow(), "Verifying the document is part of a workflow");

        ShareUser.logout(drone);

        // Login as CloudUser User
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Open Site Document Library
        documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();

        // Verify File1 exists in Site Document Library and still Synced
        assertTrue(documentLibraryPage.isFileVisible(fileName1), "Verifying File1 exists");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the document is synced");

        // Verify File2 exists in Site Document library and it is not Synced
        assertTrue(documentLibraryPage.isFileVisible(fileName2), "Verifying File2 exists");
        assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName2).isCloudSynced(), "Verifying the document is NOT synced");

        // Verify File3 doesn't exist
        assertFalse(documentLibraryPage.isFileVisible(fileName3), "Verifying File3 does NOT exist");

        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "Hybrid")
    public void AONE_15741() throws Exception
    {
        String testName = getTestName();
        String uniqueData = String.valueOf(System.currentTimeMillis());
        String user1 = getUserNameForDomain(testName + uniqueData, DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName + uniqueData, DOMAIN_HYBRID);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, DOMAIN_HYBRID, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        String opSiteName = getSiteName(testName) + uniqueData + "-OP";
        String cloudSite = getSiteName(testName) + uniqueData + "-CL";

        String fileName1 = getFileName(testName) + uniqueData + "-1.txt";
        String[] fileInfo1 = { fileName1, DOCLIB };

        String fileName2 = getFileName(testName) + uniqueData + "-2.txt";
        String[] fileInfo2 = { fileName2, DOCLIB };

        String workFlowName1 = testName + uniqueData + "-1-WF";
        String workFlowName2 = testName + uniqueData + "-2-WF";
        String dueDate = getDueDateString();

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload 2 files
        ShareUser.uploadFileInFolder(drone, fileInfo1).render();
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo2).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setDueDate(dueDate);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

        // Select "Cloud Task or Review" from select a workflow dropdown
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName1);

        // Create Workflow1 using File1 (Lock On Premise : True)
        formDetails.setMessage(workFlowName1);
        formDetails.setLockOnPremise(true);

        documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        // Create Workflow2 using File2 (Lock On Premise : False)
        formDetails.setMessage(workFlowName2);
        formDetails.setLockOnPremise(false);

        documentLibraryPage.renderItem(maxWaitTime, fileName2);
        cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName2);
        documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        // Verify File1 is Cloud Synced, part of workflow and it is Locked
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the File1 is synced");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isLocked(), "Verifying the File1 is Locked");
        assertEquals(documentLibraryPage.getFileDirectoryInfo(fileName1).getContentInfo(), "This document is locked by you.", "Verifying Locked message");
        assertTrue(ShareUser.checkIfContentIsSynced(drone, fileName1), "Verifying the Sync Status is \"Synced\"");

        drone.refresh();
        documentLibraryPage.render();
        // Verify File2 is Cloud Synced, part of workflow and it is NOT Locked
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName2).isPartOfWorkflow(), "Verifying the File2 is part of a workflow");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName2).isCloudSynced(), "Verifying the File2 is synced");
        assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName2).isLocked(), "Verifying the File2 is NOT Locked");
        assertTrue(ShareUser.checkIfContentIsSynced(drone, fileName2), "Verifying the Sync Status is \"Synced\"");

        MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);
        // Verify Workflows are created successfully
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName1), "Verifying workflow1 exists");
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName2), "Verifying workflow2 exists");

        ShareUser.logout(drone);

        // Login as CloudUser User
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Open Site Document Library
        documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();

        // Verify File1 exists in Site Document Library, it is Synced and part of workflow.
        assertTrue(documentLibraryPage.isFileVisible(fileName1), "Verifying File1 exists");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the document is synced");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the document is part of workflow");
        assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName1).isLocked(), "Verifying the File1 is NOT Locked");

        // Verify File2 exists in Site Document library, it is Synced and part of workflow.
        assertTrue(documentLibraryPage.isFileVisible(fileName2), "Verifying File2 exists");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName2).isCloudSynced(), "Verifying the document is NOT synced");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName2).isPartOfWorkflow(), "Verifying the document is NOT synced");
        assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName2).isLocked(), "Verifying the File2 is NOT Locked");

        // Navigate to MyTasks page and verify both tasks are present
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        assertTrue(AbstractWorkflow.checkIfTaskIsPresent(hybridDrone, workFlowName1, true), "Cloud: task is not displayed");
        assertTrue(AbstractWorkflow.checkIfTaskIsPresent(hybridDrone, workFlowName2, true), "Cloud: task is not displayed");

        ShareUser.logout(hybridDrone);
    }

    @Test(groups = { "Hybrid" })
    public void AONE_15742() throws Exception
    {

        String testName = getTestName();
        String uniqueData = String.valueOf(System.currentTimeMillis());
        String opSite = getSiteName(testName) + uniqueData + "-OP2";
        String cloudSite1 = getSiteName(testName) + uniqueData + "-CL2-1";
        String cloudSite2 = getSiteName(testName) + uniqueData + "-CL2-2";
        String cloudSite3 = getSiteName(testName) + uniqueData + "-CL2-3";
        String cloudSite4 = getSiteName(testName) + uniqueData + "-CL2-4";
        String cloudSite5 = getSiteName(testName) + uniqueData + "-CL2-5";
        String testDomain1 = "domain1" + uniqueData.substring(7, 12) + ".webd";
        String testDomain2 = "domain2" + uniqueData.substring(7, 12) + ".webd";

        String user1 = getUserNameForDomain(testName + uniqueData + "-1", testDomain1);
        String[] userInfo1 = new String[] { user1 };

        String opTestUser1 = getUserNameForDomain(testName + uniqueData + "-op-1", testDomain1);
        String[] opTestUserInfo1 = new String[] { opTestUser1 };

        String cloudUser1 = getUserNameForDomain(testName + uniqueData + "-1", testDomain1);
        String[] cloudUserInfo1 = new String[] { cloudUser1 };

        String cloudUser2 = getUserNameForDomain(testName + uniqueData + "-2", testDomain1);
        String[] cloudUserInfo2 = new String[] { cloudUser2 };

        String cloudUser3 = getUserNameForDomain(testName + uniqueData + "-1", testDomain2);
        String[] cloudUserInfo3 = new String[] { cloudUser3 };

        String cloudUser4 = getUserNameForDomain(testName + uniqueData + "-2", testDomain2);
        String[] cloudUserInfo4 = new String[] { cloudUser4 };

        // Create OP Users (User1, testUser1, testUser2)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, opTestUserInfo1);
        // CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, opTestUserInfo2);

        // Create Cloud users (cloudUser1, cloudUser2, cloudUser3 & cloudUser4)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo2);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo3);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo4);

        // Upgrade both Cloud networks
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain1, "1000");
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain2, "1000");

        // Login to User1, set up the cloud sync, Create a site
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        // Login as CloudUser1 and create a site
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSite1, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as CloudUser2 and create a site
        ShareUser.login(hybridDrone, cloudUser2, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSite2, SITE_VISIBILITY_PRIVATE);
        ShareUser.createSite(hybridDrone, cloudSite5, SITE_VISIBILITY_PRIVATE);
        ShareUser.logout(hybridDrone);

        // CloudUser2 invites CloudUser1 to join the site as Consumer
        CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, cloudUser2, cloudUser1, getSiteShortname(cloudSite5), "SiteConsumer", "");

        // Login as CloudUser3 and create a site
        ShareUser.login(hybridDrone, cloudUser3, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSite3, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as CloudUser4 and create a site
        ShareUser.login(hybridDrone, cloudUser4, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSite4, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        String file = getFileName(testName) + System.currentTimeMillis() + ".txt";
        String[] fileInfo = { file, DOCLIB };

        String dueDate = getDueDateString();
        String workFlowName = testName + System.currentTimeMillis() + "-WF";

        // Login to OP as User1
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        SiteUtil.openSiteDocumentLibraryURL(drone, opSite);
        ShareUser.uploadFileInFolder(drone, fileInfo).render();
        // Start Simple Cloud Task Workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, file);

        // Fill in Message (Workflow Name), due date, After Completion
        cloudTaskOrReviewPage.enterMessageText(workFlowName);
        cloudTaskOrReviewPage.enterDueDateText(dueDate);
        cloudTaskOrReviewPage.selectAfterCompleteDropDown(KeepContentStrategy.DELETECONTENT);

        // Verify Assignee button is not enabled
        assertFalse(cloudTaskOrReviewPage.isSelectAssigneeButtonEnabled());

        // Select Destination And Assignee
        DestinationAndAssigneePage destinationAndAssigneePage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();
        // Verify User can only see Domain1
        assertTrue(destinationAndAssigneePage.isNetworkDisplayed(testDomain1), "Default User's Network is not displayed");
        assertFalse(destinationAndAssigneePage.isNetworkDisplayed(testDomain2), "External network is displayed");

        // Select network, select CloudSite2 (CloudUser1 is consumer for that site)
        destinationAndAssigneePage.selectNetwork(testDomain1);
        assertTrue(destinationAndAssigneePage.isSiteDisplayed(cloudSite1), "Public site is NOT displayed: " + cloudSite1 + " from the default network");
        assertFalse(destinationAndAssigneePage.isSiteDisplayed(cloudSite2), "Private site is displayed: " + cloudSite2 + " from the default network");
        assertFalse(destinationAndAssigneePage.isSiteDisplayed(cloudSite3), "Site is displayed: " + cloudSite3 + " from external network");
        assertFalse(destinationAndAssigneePage.isSiteDisplayed(cloudSite4), "Site is displayed: " + cloudSite4 + " from external network");
        assertTrue(destinationAndAssigneePage.isSiteDisplayed(cloudSite5), "Private site is NOT displayed: " + cloudSite5 + " where user is consumer");

        // i.e. testSite1 and testSite2.
        destinationAndAssigneePage.selectSite(cloudSite5);
        assertFalse(destinationAndAssigneePage.isSyncButtonEnabled(), "Verifying the Sync button is disabled");
        assertFalse(destinationAndAssigneePage.isSyncPermitted(DEFAULT_FOLDER_NAME), "Verifying User doesn't have permissions to the folder");

        // Select CloudSite1, verify user has permission to the folder
        destinationAndAssigneePage.selectSite(cloudSite1);
        assertTrue(destinationAndAssigneePage.isSyncPermitted(DEFAULT_FOLDER_NAME), "Verifying User has permissions to the folder");
        // Select Default folder and select Sync button
        destinationAndAssigneePage.selectFolder(DEFAULT_FOLDER_NAME);
        destinationAndAssigneePage.selectSubmitButtonToSync();
        cloudTaskOrReviewPage.render();

        // Verify Destination details (Network, Site and Folder)
        assertEquals(cloudTaskOrReviewPage.getDestinationNetwork(), testDomain1, "Verify Destination Network");
        assertEquals(cloudTaskOrReviewPage.getDestinationSite(), cloudSite1, "Verify Destination Site");
        assertEquals(cloudTaskOrReviewPage.getDestinationFolder(), DEFAULT_FOLDER_NAME + "/", "Verify Destination Folder Name");
        ShareUser.logout(drone);

    }

    @Test(groups = "Hybrid")
    public void AONE_15743() throws Exception
    {
        String testName = getTestName();
        String uniqueData = String.valueOf(System.currentTimeMillis());

        String testDomain1 = "domain1" + uniqueData.substring(7, 12) + ".webd";
        String testDomain2 = "domain2" + uniqueData.substring(7, 12) + ".webd";
        // 3. with several users in each of them
        String cloudUser1 = getUserNameForDomain(testName + uniqueData + "-1", testDomain1);
        String cloudUser2 = getUserNameForDomain(testName + uniqueData + "-2", testDomain1);
        String cloudUser3 = getUserNameForDomain(testName + uniqueData + "-1", testDomain2);
        String cloudUser4 = getUserNameForDomain(testName + uniqueData + "-2", testDomain2);
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String[] cloudUserInfo2 = new String[] { cloudUser2 };
        String[] cloudUserInfo3 = new String[] { cloudUser3 };
        String[] cloudUserInfo4 = new String[] { cloudUser4 };

        // 4. Any site is created in Cloud by any user
        String cloudSite1 = getSiteName(testName) + uniqueData + "-CL2-1";
        String opSite = getSiteName(testName) + uniqueData + "-OP2";

        // 5. Several users are created on-premise
        String user1 = getUserNameForDomain(testName + uniqueData + "-1-op", testDomain1);
        String user2 = getUserNameForDomain(testName + uniqueData + "-2-op", testDomain1);

        String[] userInfo1 = new String[] { user1 };
        String[] userInfo2 = new String[] { user2 };

        String file = getFileName(testName) + uniqueData + ".txt";
        String[] fileInfo = { file, DOCLIB };

        String dueDate = getDueDateString();
        String workFlowName = testName + uniqueData + "-WF";

        // Several users are created on-premise, e.g. testUser1, testUser2
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo2);

        // Create Cloud users (cloudUser1, cloudUser2, cloudUser3 & cloudUser4)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo2);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo3);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo4);
        // Upgrade both Cloud networks
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain1, "1000");
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain2, "1000");

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSite1, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login to OP as User1
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSite, SITE_VISIBILITY_PUBLIC);
        SiteUtil.openSiteDocumentLibraryURL(drone, opSite);
        ShareUser.uploadFileInFolder(drone, fileInfo).render();
        // Start Simple Cloud Task Workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, file);

        // Fill in Message (Workflow Name), due date, After Completion
        cloudTaskOrReviewPage.enterMessageText(workFlowName);
        cloudTaskOrReviewPage.enterDueDateText(dueDate);
        cloudTaskOrReviewPage.selectAfterCompleteDropDown(KeepContentStrategy.DELETECONTENT);

        // Select Destination
        DestinationAndAssigneePage destinationAndAssigneePage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();
        destinationAndAssigneePage.selectNetwork(testDomain1);
        destinationAndAssigneePage.selectSite(cloudSite1);
        destinationAndAssigneePage.selectFolder(DEFAULT_FOLDER_NAME);
        destinationAndAssigneePage.selectSubmitButtonToSync();

        // Select Assignee button and verify "No items found" for OP TestUser1, OP TestUser2, CloudUser3, CloudUser4
        AssignmentPage assignmentPage = cloudTaskOrReviewPage.selectAssignmentPage().render();
        assertTrue(assignmentPage.isNoItemsFoundMessageDisplayed(user1), "On-premise user1 is found on cloud");
        assertTrue(assignmentPage.isNoItemsFoundMessageDisplayed(user2), "On-premise user2 is found on cloud");
        assertTrue(assignmentPage.isNoItemsFoundMessageDisplayed(cloudUser3), "User3 from other network is found");
        assertTrue(assignmentPage.isNoItemsFoundMessageDisplayed(cloudUser4), "User4 from other network is found");

        // Verify CloudUser1 and CloudUser2 can be found from search
        assertTrue(assignmentPage.isUserFound(cloudUser1), "User1 from the network is NOT found");
        assertTrue(assignmentPage.isUserFound(cloudUser2), "User2 from the network is NOT found");

        // Select CloudUser1
        assignmentPage.selectAssignee(cloudUser1);

        cloudTaskOrReviewPage.render();

        assertTrue(cloudTaskOrReviewPage.isAssigneePresent(), "Assignee user is not filled");
        assertTrue(cloudTaskOrReviewPage.getAssignee().contains(cloudUser1), "User1 is not added as assignee");

        // Select the file from the site and start workflow
        DocumentLibraryPage documentLibraryPage = cloudTaskOrReviewPage.selectStartWorkflow().render();

        assertTrue(checkIfContentIsSynced(drone, file), "Content was not synced to cloud");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(file).isPartOfWorkflow(), "Workflow icon for content is not displayed");
        ShareUser.logout(drone);

        // Login to Cloud as CloudUser1 and verify the task is present
        sharePage = ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

        assertTrue(AbstractWorkflow.checkIfTaskIsPresent(hybridDrone, workFlowName, true), "Cloud: task is not displayed");

        ShareUser.logout(hybridDrone);
    }

    private void findTasks(WebDrone driver, String workFlowName)
    {

        assertTrue(driver.findAndWaitWithRefresh(By.xpath(String.format("//a[text()='%s']", workFlowName))).isDisplayed());

    }

    private boolean waitAndCheckIfVisible(WebDrone driver, DocumentLibraryPage docLib, String contentName)
    {
        int i = 0;
        boolean isVisible = docLib.isItemVisble(contentName);
        while (!isVisible)
        {
            webDriverWait(driver, timeToWait);
            docLib = refreshDocumentLibrary(driver).render();
            isVisible = docLib.isItemVisble(contentName);
            i++;
            if (i > retryCount)
            {
                break;
            }
        }
        return isVisible;
    }
}
