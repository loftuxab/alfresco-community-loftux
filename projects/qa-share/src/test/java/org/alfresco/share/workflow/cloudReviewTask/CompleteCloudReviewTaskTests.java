package org.alfresco.share.workflow.cloudReviewTask;

import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.task.*;
import org.alfresco.po.share.task.EditTaskPage.Button;
import org.alfresco.po.share.workflow.*;
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

public class CompleteCloudReviewTaskTests extends AbstractWorkflow
{

    private String testDomain;
    private String fileName;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {

        super.setup();
        testDomain = DOMAIN_HYBRID;

    }

    @Test(groups = "DataPrepHybrid", timeOut = 300000)
    public void dataPrep_AONE_15624() throws Exception
    {

        testName = getTestName() + "A";
        String cloudSite = getSiteName(testName + "CL");
        String opSite = getSiteName(testName + "OP");
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser };
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        fileName = getFileName(testName) + "-15624" + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        ShareUser.logout(drone);
    }

    /**
     * AONE-15624 : Approve action
     */
    @Test(groups = "Hybrid", timeOut=1000000)
    public void AONE_15624() throws Exception
    {

        testName = getTestName() + "A";
        String cloudSite = getSiteName(testName + "CL");
        String opSite = getSiteName(testName + "OP");
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);

        String fileName = getFileName(testName) + "-15624" + ".txt";
        String workFlowName = "Cloud Review Task test message" + testName + "-15624";

        List<String> userNames = new ArrayList<String>();
        userNames.add(cloudUser);

        try
        {

            ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

            // --- Step 1 ---
            // --- Step action ---
            // OP Specify 'Cloud Review Task' type.
            // --- Expected results ---
            // Performed correctly.

            // --- Step 2 ---
            // --- Step action ---
            // Specify any data in other required fields, e.g. Message: 'Cloud Review Task test message' Network: 'network.com' Site: 'user1 user1's Home'
            // Folder: 'Documents/' Assignee: 'user1@network.com' Required Approval Percentage: 50 After completion: any Lock on-premise content: any Items:
            // 'test1.txt'
            // --- Expected results ---
            // Performed correctly.

            ShareUser.openSitesDocumentLibrary(drone, opSite).render();

            CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName).render();

            WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

            formDetails.setMessage(workFlowName);
            formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
            formDetails.setTaskPriority(Priority.MEDIUM);
            formDetails.setSiteName(cloudSite);
            formDetails.setReviewers(userNames);
            formDetails.setApprovalPercentage(50);
            formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

            // --- Step 3 ---
            // --- Step action ---
            // Click on Start Workflow button.
            // --- Expected results ---
            // Workflow is started successfully. The workflow is located under Active on Workflows I've Started page.

            cloudTaskOrReviewPage.startWorkflow(formDetails).render();

            MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone).render();
            assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName), "OP: The workflow was not found in Workflows I Started page");

            // --- Step 4 ---
            // --- Step action ---
            // Cloud Login as user1@network.com and verify the workflow.
            // --- Expected results ---
            // The workflow is started in Cloud. It is Active on Workflows I've Started page. A new task is assigned to the specified user.

            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            assertTrue(ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).render().isTaskPresent(workFlowName), "Cloud: The workflow was not found in My Tasks page");

            // --- Step 5 ---
            // --- Step action ---
            // OP Open created workflow details page.
            // --- Expected results ---
            // Details page is opened. The following title is displayed: "Details: Cloud Review Task test message (Start a task or review on Alfresco Cloud)"

            ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

            WorkFlowDetailsPage workFlowDetailsPage = ShareUserWorkFlow.navigateToWorkFlowDetailsPage(drone, workFlowName).render();
            String header = "Details: " + workFlowName + " (Start a task or review on Alfresco Cloud)";
            assertEquals(workFlowDetailsPage.getPageHeader(), header, "OP: The page header of the created workflow is not " + header);

            // --- Step 6 ---
            // --- Step action ---
            // Verify 'General Info' section.
            // --- Expected results ---
            // The following data is displayed: Title: Cloud Task or Review Description: Create a task or start a review on Alfresco Cloud Started by:
            // Administrator Due: (None) Completed: in progress Started: Thu 12 Sep 2013 17:15:07 Priority: Medium Status: Workflow is in Progress Message:
            // Cloud Review Task test message

            assertEquals(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getTitle(), WorkFlowTitle.CLOUD_TASK_OR_REVIEW, "General Info: The title of the task is not Cloud Task or Review");
            assertEquals(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getDescription(), WorkFlowDescription.CREATE_A_TASK_OR_START_A_REVIEW);
            assertEquals(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getStartedBy(), getUserFullName(opUser), "General Info: Started by is not the opUser " + getUserFullName(opUser));
            assertEquals(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getDueDateString(), NONE, "General Info: The due date of the task is not NONE");
            assertEquals(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getCompleted(), "<in progress>", "General Info: The completed task is not In Progress");
            assertTrue(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getStartDate().isBeforeNow(), "General Info: The started Date is not the expected one");
            assertEquals(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getPriority(), Priority.MEDIUM, "General Info: The task priority is not MEDIUM");
            assertEquals(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getStatus(), WorkFlowStatus.WORKFLOW_IN_PROGRESS, "General Info: The task status is not In Progress");
            assertEquals(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getMessage(), workFlowName, "General Info: The message is not the expected one: " + workFlowName);

            // --- Step 7 ---
            // --- Step action ---
            // Verify 'More Info' section.
            // --- Expected results ---
            // The following data is displayed: Type: Cloud Review Task Destination: network.com After completion: specified value Lock on-premise content:
            // specified value Assignment: user1 user1 (user1@network.com)

            assertEquals(workFlowDetailsPage.getWorkFlowDetailsMoreInfo().getType(), TaskType.CLOUD_REVIEW_TASK, "More Info: The type of the task is not Cloud Task or Review");
            assertEquals(workFlowDetailsPage.getWorkFlowDetailsMoreInfo().getDestination(), DOMAIN_HYBRID, "More Info: The destination is not the expected domain: "+ DOMAIN_HYBRID);
            assertEquals(workFlowDetailsPage.getWorkFlowDetailsMoreInfo().getAfterCompletion(), KeepContentStrategy.DELETECONTENT, "More Info: The after completion action is not the expected one");
            assertTrue(!workFlowDetailsPage.getWorkFlowDetailsMoreInfo().isLockOnPremise(), "More Info: The lock on premise should not be true");
            assertEquals(workFlowDetailsPage.getWorkFlowDetailsMoreInfo().getAssignmentList().size(), userNames.size());
            assertTrue(workFlowDetailsPage.getWorkFlowDetailsMoreInfo().getAssignmentList().contains(getUserFullNameWithEmail(cloudUser, cloudUser)), "More Info: The assignment user is not the cloud user");

            // --- Step 8 ---
            // --- Step action ---
            // Verify 'Items' section.
            // --- Expected results ---
            // The following item is displayed: test1.txt

            List<WorkFlowDetailsItem> items = workFlowDetailsPage.getWorkFlowItems();
            assertEquals(items.size(), 1);
            assertEquals(items.get(0).getItemName(), fileName, "The item displayed should be: " + fileName);

            // --- Step 9 ---
            // --- Step action ---
            // Verify 'Current Tasks' section.
            // --- Expected results ---
            // The following data is displayed: No tasks

            assertTrue(workFlowDetailsPage.isNoTasksMessageDisplayed(), "Current Task section should display no tasks");

            // --- Step 10 ---
            // --- Step action ---
            // Verify 'History' section.
            // --- Expected results ---
            // The following data is displayed: Start a task or review on Alfresco Cloud admin Thu 12 Sep 2013 17:15:07 Task Done

            List<WorkFlowDetailsHistory> historyList = workFlowDetailsPage.getWorkFlowHistoryList();

            assertEquals(historyList.size(), 1);
            assertEquals(historyList.get(0).getType(), WorkFlowHistoryType.START_TASK_OR_REVIEW_ON_CLOUD, "History: The workflow type is not Start a task or review on Alfresco Cloud");
            assertEquals(historyList.get(0).getCompletedBy(), getUserFullName(opUser), "History: The completed by assigned user is not the opUser");
            assertEquals(historyList.get(0).getCompletedDate().toLocalDate(), getToDaysLocalDate(), "History: The day that task was completed is not the expected one");
            assertEquals(historyList.get(0).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE, "History: The task outcome should be Task Done");
            assertEquals(historyList.get(0).getComment(), "", "History: The comment is different than Null");

            // --- Step 11 ---
            // --- Step action ---
            // OP Verify the document, which is the part of the workflow.
            // --- Expected results ---
            // The document is successfully synced to Cloud. The correct destination is displayed.

            // ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

            DocumentLibraryPage documentLibraryPageOP = SiteUtil.openSiteDocumentLibraryURL(drone, opSite).render();

            assertTrue(documentLibraryPageOP.isFileVisible(fileName), "Verifying " + fileName + " exists");
            assertTrue(documentLibraryPageOP.getFileDirectoryInfo(fileName).isCloudSynced(), "Verifying the document is synced");
            assertTrue(documentLibraryPageOP.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the document is part of workflow");
            ShareUser.logout(drone);

            // --- Step 12 ---
            // --- Step action ---
            // Cloud Verify the synced document.
            // --- Expected results ---
            // The document is successfully synced to Cloud. It is present in the correct destination.

            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            DocumentLibraryPage documentLibraryPageCL = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();

            assertTrue(documentLibraryPageCL.isFileVisible(fileName), "Verifying " + fileName + " exists");
            assertTrue(documentLibraryPageCL.getFileDirectoryInfo(fileName).isCloudSynced(), "Cloud: Verifying the document is synced");
            assertTrue(documentLibraryPageCL.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the document is part of workflow");

            // --- Step 13 ---
            // --- Step action ---
            // Cloud Open Task History page.
            // --- Expected results ---
            // Details page is opened. The following title is displayed: "Details: Cloud Review Task test message (Start Review)"

            TaskHistoryPage taskHistoryPage = ShareUserWorkFlow.navigateToTaskHistoryPage(hybridDrone, workFlowName).render();

            header = "Details: " + workFlowName + " (Review)";
            assertEquals(taskHistoryPage.getPageHeader(), header);

            // --- Step 14 ---
            // --- Step action ---
            // Verify 'General Info' section.
            // --- Expected results ---
            // The following data is displayed: Title: Hybrid Review And Approve Process Description: Hybrid Review And Approve Process Started by:
            // Administrator admin Due: (None) Completed: in progress Started: Thu 12 Sep 2013 17:17:42 Priority: Medium Status: Task is in Progress Message:
            // Cloud Review Task test message

            assertEquals(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getTitle(), WorkFlowTitle.HYBRID_REVIEW, "CL: General Info - The workflow title is not the Hybrid Review and Approve Process");
            assertEquals(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getDescription(), WorkFlowDescription.REQUEST_DOCUMENT_APPROVAL);
            assertEquals(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getStartedBy(), getUserFullName(cloudUser), "CL: General Info - The started by user is not the cloud user");
            assertEquals(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getDueDateString(), NONE, "CL: General Info - The due date is different than NONE");
            assertEquals(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getCompleted(), "<in progress>");
            assertTrue(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getStartDate().isBeforeNow());
            assertEquals(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getPriority(), Priority.MEDIUM, "CL: General Info - The workflow priority is different than MEDIUM");
            assertEquals(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getStatus(), WorkFlowStatus.TASK_IN_PROGRESS, "CL: General Info - The status is different than Task is in Progress");
            assertEquals(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getMessage(), workFlowName);

            // --- Step 15 ---
            // --- Step action ---
            // Verify 'More Info' section.
            // --- Expected results ---
            // The following data is displayed: Send Email Notifications: No

            // TODO Send Email Notifications should be YES, Test case should be updated

            assertEquals(taskHistoryPage.getWorkFlowDetailsMoreInfo().getNotification(), SendEMailNotifications.YES, "CL: General Info - Send Email Notification should be YES");

            // --- Step 16 ---
            // --- Step action ---
            // Verify 'Items' section.
            // --- Expected results ---
            // The following data is displayed: Send Email Notifications: No

            items = taskHistoryPage.getWorkFlowItems();
            assertEquals(items.size(), 1);
            assertEquals(items.get(0).getItemName(), fileName, "CL: The item uploaded to workflow is not the expected one " + fileName);

            // --- Step 17 ---
            // --- Step action ---
            // Verify 'Current Tasks' section.
            // --- Expected results ---
            // The following data is displayed: Review Administrator admin (None) Not Yet Started

            List<WorkFlowDetailsCurrentTask> currentTaskList = taskHistoryPage.getCurrentTasksList();

            assertEquals(currentTaskList.size(), 1);
            assertEquals(currentTaskList.get(0).getTaskType(), CurrentTaskType.REVIEW, "CL: Task type is not the expected one: Review");
            assertEquals(currentTaskList.get(0).getAssignedTo(), getUserFullName(cloudUser), "CL: The assigned user is not the cloudUser");
            assertEquals(currentTaskList.get(0).getDueDateString(), NONE);
            assertEquals(currentTaskList.get(0).getTaskStatus(), TaskStatus.NOTYETSTARTED, "CL: The task status is not the expected one: Not Yet Started");

            // --- Step 18 ---
            // --- Step action ---
            // Verify 'History' section.
            // --- Expected results ---
            // The following data is displayed: Start Review Administrator admin Thu 12 Sep 2013 17:17:42 Task Done

            historyList = taskHistoryPage.getWorkFlowHistoryList();

            assertEquals(historyList.size(), 1);
            assertEquals(historyList.get(0).getType(), WorkFlowHistoryType.START_REVIEW, "CL - History: The task type is not the expected one: Start Review");
            assertEquals(historyList.get(0).getCompletedBy(), getUserFullName(cloudUser), "CL - History: The task assignment is not the expected one: cloudUser");
            assertEquals(historyList.get(0).getCompletedDate().toLocalDate(), getToDaysLocalDate());
            assertEquals(historyList.get(0).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE, "CL - History: The task outcome is not the expected one: Task Done");
            assertEquals(historyList.get(0).getComment(), "", "CL - History: The task comment is different than Null");

            // --- Step 19 ---
            // --- Step action ---
            // Cloud Verify My Tasks page.
            // --- Expected results ---
            // A new active task, e.g. "Cloud Review Task test message", is present.

            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).render();
            assertTrue(myTasksPage.isTaskPresent(workFlowName), "CL: The task is not present in My Tasks page " + workFlowName);

            // --- Step 20 ---
            // --- Step action ---
            // Open the task details.
            // --- Expected results ---
            // Performed correctly. Information details are displayed. The title is "Details: Cloud Review Task test message (Review)". Edit button is present
            // under the information details section.

            TaskDetailsPage taskDetailsPage = myTasksPage.selectViewTasks(workFlowName).render();
            assertTrue(taskDetailsPage.isBrowserTitle("Task Details"));

            header = "Details: " + workFlowName + " (Review)";
            assertEquals(taskDetailsPage.getTaskDetailsHeader(), header);
            assertTrue(taskDetailsPage.isEditButtonPresent(), "Cl- Task Details: The Edit button is not present");

            // --- Step 21 ---
            // --- Step action ---
            // Verify 'Info' section.
            // --- Expected results ---
            // The following data is displayed: Message: Cloud Review Task test message Owner: Administrator admin Priority: Medium Due: (None) Identifier:
            // 101713

            TaskInfo taskInfo = taskDetailsPage.getTaskDetailsInfo();

            assertEquals(taskInfo.getMessage(), workFlowName, "CL - Task Details: The task message is not the expected one " + workFlowName);
            assertEquals(taskInfo.getOwner(), getUserFullName(cloudUser), "CL - Task Details: The assigned user is not the cloudUser");
            assertEquals(taskInfo.getPriority(), Priority.MEDIUM);
            assertEquals(taskInfo.getDueDateString(), NONE);
            assertNotNull(taskInfo.getIdentifier());

            // --- Step 22 ---
            // --- Step action ---
            // Verify 'Progress' section.
            // --- Expected results ---
            // The following data is displayed: Status: Not Yet Started

            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.NOTYETSTARTED, "CL - Task Details: The Progress status is different than Not Yet Started");

            // --- Step 23 ---
            // --- Step action ---
            // Verify 'Items' section.
            // --- Expected results ---
            // The following item is displayed: test1.txt

            List<TaskItem> taskItems = taskDetailsPage.getTaskItems();
            assertEquals(taskItems.size(), 1);
            assertEquals(taskItems.get(0).getItemName(), fileName, "CL - Task Details: The file uploaded is not the expected one " + fileName);

            // --- Step 24 ---
            // --- Step action ---
            // Verify 'Response' section.
            // --- Expected results ---
            // The following data is displayed: Comment: (None)
            assertEquals(taskDetailsPage.getComment(), NONE, "CL - Task Details: The comment area is different than Null");

            // --- Step 25 ---
            // --- Step action ---
            // Cloud Click on Edit button.
            // --- Expected results ---
            // The button is pressed. Edit Task page is opened.

            EditTaskPage editTaskPage = taskDetailsPage.selectEditButton().render();
            assertTrue(editTaskPage.getTitle().contains("Edit Task"));

            // --- Step 26 ---
            // --- Step action ---
            // Verify the available controls on Edit Task page.
            // --- Expected results ---
            // The following additional controls are present: Status drop-down list View More Actions button for the document Comment field Approve button
            // Reject button Save and close button Cancel button

            List<TaskStatus> statusOptions = editTaskPage.getStatusOptions();
            assertEquals(statusOptions.size(), TaskStatus.values().length);

            taskItems = editTaskPage.getTaskItem(fileName);
            assertTrue(taskItems.get(0).getViewMoreActionsLink().getDescription().contains("View More Actions"));
            assertTrue(editTaskPage.isCommentTextAreaDisplayed());
            assertTrue(editTaskPage.isButtonsDisplayed(Button.APPROVE), "CL - Edit Task: Approve button is not displayed");
            assertTrue(editTaskPage.isButtonsDisplayed(Button.REJECT), "CL - Edit Task: Reject button is not displayed");
            assertTrue(editTaskPage.isButtonsDisplayed(Button.SAVE_AND_CLOSE), "CL - Edit Task: Save and Close button is not displayed");
            assertTrue(editTaskPage.isButtonsDisplayed(Button.CANCEL), "CL - Edit Task: Cancel button is not displayed");

            // --- Step 27 ---
            // --- Step action ---
            // Verify the Status drop-down list.
            // --- Expected results ---
            // The following values are available: Not yet started (set by default) In Progress On Hold Canceled Completed

            assertTrue(statusOptions.containsAll(getTaskStatusList()), "Not all items are displayed in status list");

            // --- Step 28 ---
            // --- Step action ---
            // Specify any value in the Status drop-down list, e.g. 'In Progress'.
            // --- Expected results ---
            // Performed correctly.

            editTaskPage.selectStatusDropDown(TaskStatus.INPROGRESS);
            assertEquals(editTaskPage.getSelectedStatusFromDropDown(), TaskStatus.INPROGRESS, "In Progress status was not selected");

            // --- Step 29 ---
            // --- Step action ---
            // Add any data into the Comment field, e.g. "test comment".
            // --- Expected results ---
            // Performed correctly.

            editTaskPage.enterComment("test comment");
            assertEquals(editTaskPage.readCommentFromCommentTextArea(), "test comment");

            // --- Step 30 ---
            // --- Step action ---
            // Click on Cancel button.
            // --- Expected results ---
            // Edit Task page is closed. Task Details are displayed. No data was changed. Comment: (None)

            taskDetailsPage = editTaskPage.selectCancelButton().render();
            assertEquals(taskDetailsPage.getComment(), NONE, "CL: Cancel button was not clicked");

            // --- Step 31 ---
            // --- Step action ---
            // Repeat steps 25-29.
            // --- Expected results ---
            // Performed correctly.

            taskDetailsPage.selectEditButton().render();
            editTaskPage.selectStatusDropDown(TaskStatus.INPROGRESS);
            editTaskPage.enterComment("test comment");
            assertEquals(editTaskPage.getSelectedStatusFromDropDown(), TaskStatus.INPROGRESS, "CL - Edit: In Progress status was not selected");
            assertEquals(editTaskPage.readCommentFromCommentTextArea(), "test comment");

            // --- Step 32 ---
            // --- Step action ---
            // Click on Save and Close button.
            // --- Expected results ---
            // Edit Task page is closed. Task Details are displayed. The specified data was changed.

            taskDetailsPage = editTaskPage.selectSaveButton().render();
            assertTrue(taskDetailsPage.isBrowserTitle("Task Details"));
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.INPROGRESS);
            assertEquals(taskDetailsPage.getComment(), "test comment", "CL: The edit was not performed correctly");

            // --- Step 33 ---
            // --- Step action ---
            // Cloud: Select Edit button, select Task Status as Completed, specify any comment, e.g. test comment and click on Approve button
            // --- Expected results ---
            // The task is completed. It moves to the Completed Tasks filter.

            editTaskPage = taskDetailsPage.selectEditButton().render();
            editTaskPage.selectStatusDropDown(TaskStatus.COMPLETED);
            editTaskPage.enterComment("test comment");
            taskDetailsPage = editTaskPage.selectApproveButton().render();
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.COMPLETED, "CL: The Approve action was not performed correctly");

            myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).selectCompletedTasks().render();
            assertTrue(myTasksPage.isTaskPresent(workFlowName), "CL: The task was not displayed in My Tasks page");

            // --- Step 34 ---
            // --- Step action ---
            // Cloud Verify My Tasks page.
            // --- Expected results ---
            // A completed task, e.g. "Cloud Review Task test message", is present in the Completed Tasks filter.

            // same as previous step

            // --- Step 35 ---
            // --- Step action ---
            // Open Task Details page.
            // --- Expected results ---
            // Details page is opened.

            taskDetailsPage = myTasksPage.selectViewTasks(workFlowName).render();
            assertTrue(taskDetailsPage.isBrowserTitle("Task Details"), "CL: The Task Details page was not displayed");

            // --- Step 36 ---
            // --- Step action ---
            // Verify the changed data.
            // --- Expected results ---
            // The following changes are present: Edit button is absent under the information details section. Status: 'Completed' in the Progress section
            // Comment: 'test comment edited (Approved)' in the Response section

            assertTrue(!taskDetailsPage.isEditButtonPresent(), "CL: Edit button is present on approved workflow");
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.COMPLETED, "Cl: The task status on approved task is not Completed");
            // TODO modify in TestLink the step about the comment
            assertEquals(taskDetailsPage.getComment(), "test comment", "CL: The comment is not the saved one");

            // --- Step 37 ---
            // --- Step action ---
            // Cloud Open Task History page.
            // --- Expected results ---
            // Task History Page is opened

            taskHistoryPage = ShareUserWorkFlow.navigateToTaskHistoryPage(hybridDrone, workFlowName).render();
            assertTrue(taskHistoryPage.isBrowserTitle("Task History"), "CL: Task History page was not displayed");

            // --- Step 38 ---
            // --- Step action ---
            // Verify the changed data.
            // --- Expected results ---
            // The following changes are present: Completed: Thu 12 Sep 2013 20:26:03 in the General Info section Status: Task is Complete in the General Info
            // section

            assertTrue(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getCompletedDate().isBeforeNow());
            assertEquals(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getStatus(), WorkFlowStatus.TASK_COMPLETE, "CL: The Workflow Status is not Completed");

            // --- Step 39 ---
            // --- Step action ---
            // Verify 'Current Tasks' section.
            // --- Expected results ---
            // The following data is displayed: No tasks

            currentTaskList = taskHistoryPage.getCurrentTasksList();

            assertEquals(currentTaskList.size(), 0);
            // TODO verify id No Tasks means size == 0
            // assertEquals(currentTaskList.get(0).getTaskType(), CurrentTaskType.TASK);

            // --- Step 40 ---
            // --- Step action ---
            // Verify 'History' section.
            // --- Expected results ---
            // The following data is displayed: Workflow Task Administrator admin Thu 12 Sep 2013 20:26:02 Task Done test comment edited Start Review
            // Administrator admin Thu 12 Sep 2013 17:17:42 Task Done

            // TODO Modify in TestLink the data displayed in History section

            assertEquals(taskHistoryPage.getWorkFlowHistoryList().get(0).getType(), WorkFlowHistoryType.REVIEW, "CL - Workflow History: The Workflow type is not Review - first row");
            assertEquals(taskHistoryPage.getWorkFlowHistoryList().get(0).getCompletedBy(), getUserFullName(cloudUser), "CL - Workflow History: The assigned user is not cloudUser - first row");
            assertTrue(taskHistoryPage.getWorkFlowHistoryList().get(0).getCompletedDate().isBeforeNow());
            assertEquals(taskHistoryPage.getWorkFlowHistoryList().get(0).getOutcome(), WorkFlowHistoryOutCome.APPROVED, "CL - Workflow History: The outcome is not Approved - first row");
            assertEquals(taskHistoryPage.getWorkFlowHistoryList().get(0).getComment(), "test comment");

            assertEquals(taskHistoryPage.getWorkFlowHistoryList().get(1).getType(), WorkFlowHistoryType.START_REVIEW, "CL - Workflow History: The Workflow type is not Start Review - second row");
            assertEquals(taskHistoryPage.getWorkFlowHistoryList().get(1).getCompletedBy(), getUserFullName(cloudUser), "CL - Workflow History: The assigned user is not cloudUser - second row");
            assertTrue(taskHistoryPage.getWorkFlowHistoryList().get(1).getCompletedDate().isBeforeNow());
            assertEquals(taskHistoryPage.getWorkFlowHistoryList().get(1).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE, "CL - Workflow History: The outcome is not Task Done - second row");
            assertEquals(taskHistoryPage.getWorkFlowHistoryList().get(1).getComment(), "");
            ShareUser.logout(hybridDrone);

            // --- Step 41 ---
            // --- Step action ---
            // OP Verify My Tasks page.
            // --- Expected results ---
            // An active task, e.g. "Cloud Review Task test message", is present in the Active Tasks filter.

            ShareUser.login(drone, opUser, DEFAULT_PASSWORD).render();
            myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone).render(4000);
            findTasks(drone, workFlowName);

            assertTrue(myTasksPage.isTaskPresent(workFlowName), "OP: The task is not present on Active Task");

            // --- Step 42 ---
            // --- Step action ---
            // Open the task details.
            // --- Expected results ---
            // Performed correctly. Information details are displayed. The title is
            // "Details: Cloud Review Task test message (Document was approved on the cloud)". Edit button is present under the information details section.

            taskDetailsPage = ShareUserWorkFlow.navigateToTaskDetailsPage(drone, workFlowName);
            header = "Details: " + workFlowName + " (Document was approved on the cloud)";
            assertEquals(taskDetailsPage.getTaskDetailsHeader(), header);
            assertTrue(taskDetailsPage.isEditButtonPresent(), "OP: The Edit button is not displayed");

            // --- Step 43 ---
            // --- Step action ---
            // Verify 'Info' section.
            // --- Expected results ---
            // The following data is displayed: Message: Cloud Review Task test message Owner: Administrator Priority: Medium Due: (None) Identifier: 416

            assertEquals(taskDetailsPage.getTaskDetailsInfo().getMessage(), workFlowName, "OP - Details Page: The message is not the expected one: " + workFlowName);
            assertEquals(taskDetailsPage.getTaskDetailsInfo().getOwner(), getUserFullName(opUser), "OP - Details Page: The owner is not the opUser");
            assertEquals(taskDetailsPage.getTaskDetailsInfo().getPriority(), Priority.MEDIUM, "OP - Details Page: The task priority is not MEDIUM");
            assertEquals(taskDetailsPage.getTaskDetailsInfo().getDueDateString(), NONE);
            assertNotNull(taskDetailsPage.getTaskDetailsInfo().getIdentifier());

            // --- Step 44 ---
            // --- Step action ---
            // Verify 'Progress' section.
            // --- Expected results ---
            // The following data is displayed: Status: Not Yet Started

            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.NOTYETSTARTED, "OP - Details Page: The task status is not Not Yet Started");

            // --- Step 45 ---
            // --- Step action ---
            // Verify 'Items' section.
            // --- Expected results ---
            // The following item is displayed: test1.txt

            taskItems = taskDetailsPage.getTaskItems();
            assertEquals(items.size(), 1);
            assertEquals(items.get(0).getItemName(), fileName, "OP - Details Page: The item uploaded is not the expected one: " + fileName);

            // --- Step 46 ---
            // --- Step action ---
            // Verify 'Response' section.
            // --- Expected results ---
            // The following data is displayed: Required approval percentage: 50 Actual approval percentage: 100 Comments: test comment (Approved)

            assertEquals(taskDetailsPage.getRequiredApprovalPercentage(), 50, "OP - Details Page: The task required approval percentage is not 50");
            assertEquals(taskDetailsPage.getActualApprovalPercentage(), 100, "OP - Details Page: The task actual approval percentage is not 100");
            // TODO update the step from TestLink regarding comment --- add user name
            assertEquals(taskDetailsPage.getComment(), getUserFullName(cloudUser) + ": test comment  (Approved)");

            // --- Step 47 ---
            // --- Step action ---
            // OP Click on Edit button.
            // --- Expected results ---
            // The button is pressed. Edit Task page is opened.

            editTaskPage = taskDetailsPage.selectEditButton().render();
            assertTrue(editTaskPage.isBrowserTitle("Edit Task"), "OP - Edit: The Edit Task page is not displayed");

            // --- Step 48 ---
            // --- Step action ---
            // Verify the available controls on Edit Task page.
            // --- Expected results ---
            // The following additional controls are present: Status drop-down list View More Actions button for the document Task Done button Save and close
            // button Cancel button

            statusOptions = editTaskPage.getStatusOptions();
            assertEquals(statusOptions.size(), TaskStatus.values().length);

            taskItems = editTaskPage.getTaskItem(fileName);
            assertTrue(taskItems.get(0).getViewMoreActionsLink().getDescription().contains("View More Actions"));
            assertTrue(editTaskPage.isButtonsDisplayed(Button.TASK_DONE), "OP - Edit: The Task Done button is not displayed");
            assertTrue(editTaskPage.isButtonsDisplayed(Button.SAVE_AND_CLOSE), "OP - Edit: The Save and Close button is not displayed");
            assertTrue(editTaskPage.isButtonsDisplayed(Button.CANCEL), "OP - Edit: The Cancel button is not displayed");

            // --- Step 49 ---
            // --- Step action ---
            // Verify the Status drop-down list.
            // --- Expected results ---
            // The following values are available: Not yet started (set by default) In Progress On Hold Canceled Completed

            assertTrue(statusOptions.containsAll(getTaskStatusList()), "Not all items are displayed in Status drop-down");

            // --- Step 50 ---
            // --- Step action ---
            // Specify any value in the Status drop-down list, e.g. 'In Progress'.
            // --- Expected results ---
            // Performed correctly.

            editTaskPage.selectStatusDropDown(TaskStatus.INPROGRESS);
            assertEquals(editTaskPage.getSelectedStatusFromDropDown(), TaskStatus.INPROGRESS);

            // --- Step 51 ---
            // --- Step action ---
            // Click on Cancel button.
            // --- Expected results ---
            // Edit Task page is closed. Task Details are displayed. No data was changed. Comment: (None)

            // TODO update test in TestLink --- remove the comment part
            taskDetailsPage = editTaskPage.selectCancelButton().render();

            assertTrue(taskDetailsPage.isTitlePresent("Task Details"), "The Cancel button was not clicked");

            // --- Step 52 ---
            // --- Step action ---
            // Repeat steps 47-50.
            // --- Expected results ---
            // Performed correctly.

            taskDetailsPage.selectEditButton().render();
            editTaskPage.selectStatusDropDown(TaskStatus.INPROGRESS);
            assertEquals(editTaskPage.getSelectedStatusFromDropDown(), TaskStatus.INPROGRESS);

            // --- Step 53 ---
            // --- Step action ---
            // Click on Save and Close button.
            // --- Expected results ---
            // Edit Task page is closed. Task Details are displayed. The specified data was changed.

            taskDetailsPage = editTaskPage.selectSaveButton().render();
            assertTrue(taskDetailsPage.isBrowserTitle("Task Details"));
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.INPROGRESS, "OP: The changes were not saved");

            // --- Step 54 ---
            // --- Step action ---
            // OP Click on Edit button.
            // --- Expected results ---
            // The button is pressed. Edit Task page is opened.

            taskDetailsPage.selectEditButton().render();
            assertTrue(editTaskPage.isBrowserTitle("Edit Task"), "OP: The Edit button was not clicked");

            // --- Step 55 ---
            // --- Step action ---
            // Specify any value in the Status drop-down list, e.g. 'Completed'.
            // --- Expected results ---
            // Performed correctly.

            editTaskPage.selectStatusDropDown(TaskStatus.COMPLETED);
            assertEquals(editTaskPage.getSelectedStatusFromDropDown(), TaskStatus.COMPLETED);

            // --- Step 56 ---
            // --- Step action ---
            // Click on Task Done button.
            // --- Expected results ---
            // Edit Task page is closed. Task Details are displayed. The specified data was changed.

            taskDetailsPage = editTaskPage.selectTaskDoneButton().render();
            assertTrue(taskDetailsPage.isBrowserTitle("Task Details"), "OP: The Task Done button was not clicked");
            // /what data???

            // --- Step 57 ---
            // --- Step action ---
            // Verify My Tasks page.
            // --- Expected results ---
            // A completed task, e.g. "Cloud Review Task test message", is present in the Completed Tasks filter.

            MyTasksPage myTasksPage2 = ShareUserWorkFlow.navigateToMyTasksPage(drone).selectCompletedTasks().render();
            assertTrue(myTasksPage2.isTaskPresent(workFlowName), "OP: The task is not present in Completed Tasks page");

            // --- Step 58 ---
            // --- Step action ---
            // Verify Workflows I've started page.
            // --- Expected results ---
            // A completed workflow, e.g. "Cloud Review Task test message", is present in the Completed Workflows filter.

            myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone).selectCompletedWorkFlows().render();
            assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName), "OP: The workflow is not present in Completed Workflow page");

            // --- Step 59 ---
            // --- Step action ---
            // Verify Workflow's Details page.
            // --- Expected results ---
            // The following changes are present: Completed: Thu 12 Sep 2013 20:26:03 in the General Info section Status: Workflow is Complete in the General
            // Info section Delete Workflow button

            workFlowDetailsPage = ShareUserWorkFlow.navigateToWorkFlowDetailsPage(drone, workFlowName).render();

            assertTrue(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getCompletedDate().isBeforeNow());
            assertEquals(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getStatus(), WorkFlowStatus.WORKFLOW_COMPLETE, "The workflow status is not Completed");
            assertTrue(workFlowDetailsPage.isDeleteWorkFlowButtonDisplayed(), "OP: The Delete button is not present");

            // --- Step 60 ---
            // --- Step action ---
            // Verify 'Current Tasks' section.
            // --- Expected results ---
            // The following data is displayed: No tasks

            assertTrue(workFlowDetailsPage.isNoTasksMessageDisplayed(), "There are tasks displayed in Current Tasks sections");

            // --- Step 61 ---
            // --- Step action ---
            // Verify 'History' section.
            // --- Expected results ---
            // The following data is displayed: Document was approved on the cloud admin Thu 12 Sep 2013 21:02:27 Task Done Start a task or review on Alfresco
            // Cloud admin Thu 12 Sep 2013 17:15:07 Task Done

            historyList = workFlowDetailsPage.getWorkFlowHistoryList();

            assertEquals(historyList.size(), 2);
            assertEquals(historyList.get(0).getType(), WorkFlowHistoryType.DOCUMENT_WAS_APPROVED_ON_CLOUD, "OP - Workflow History: The workflow type is not Approved on the cloud - first row");
            assertEquals(historyList.get(0).getCompletedBy(), getUserFullName(opUser), "OP - Workflow History: The user completed by is not the opUser - first row");
            assertEquals(historyList.get(0).getCompletedDate().toLocalDate(), getToDaysLocalDate());
            assertEquals(historyList.get(0).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE, "OP - Workflow History: The workflow outcome is not Task Done - first row");
            assertEquals(historyList.get(0).getComment(), "", "OP - Workflow History: The workflow comment is not Null - first row");

            assertEquals(historyList.get(1).getType(), WorkFlowHistoryType.START_TASK_OR_REVIEW_ON_CLOUD, "OP - Workflow History: The workflow type is not Start a task or review - second row");
            assertEquals(historyList.get(1).getCompletedBy(), getUserFullName(opUser), "OP - Workflow History: The user completed by is not the opUser - second row");
            assertEquals(historyList.get(1).getCompletedDate().toLocalDate(), getToDaysLocalDate());
            assertEquals(historyList.get(1).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE, "OP - Workflow History: The workflow outcome is not Task Done - second row");
            assertEquals(historyList.get(1).getComment(), "");

            ShareUser.logout(drone);

        }
        catch (Throwable t)
        {
            reportError(hybridDrone, testName + "-HY", t);
            reportError(drone, testName + "-HY", t);
        }

    }

    @Test(groups = "DataPrepHybrid", timeOut = 500000)
    public void dataPrep_AONE_15625() throws Exception
    {

        testName = getTestName() + "A1";
        String cloudSite = getSiteName(testName + "CL");
        String opSite = getSiteName(testName + "OP");
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser };
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        fileName = getFileName(testName) + "-15625" + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        String workFlowName = "Cloud Review Task test message" + testName + "-15625CL";

        List<String> userNames = new ArrayList<String>();
        userNames.add(cloudUser);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setReviewers(userNames);
        formDetails.setApprovalPercentage(50);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

        cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        ShareUser.logout(drone);
    }

    /**
     * AONE-15625:Reject action
     */
    @Test(groups = "Hybrid", timeOut = 300000)
    public void AONE_15625() throws Exception
    {

        testName = getTestName() + "A1";
        String cloudSite = getSiteName(testName + "CL");
        String opSite = getSiteName(testName + "OP");
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
        String fileName = getFileName(testName) + "-15625" + ".txt";

        String workFlowName = "Cloud Review Task test message" + testName + "-15625CL";


        try
        {

            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // --- Step 1 ---
            // --- Step action ---
            // Cloud Specify any comment, e.g. test comment.
            // --- Expected results ---
            // Performed correctly.

            EditTaskPage editTaskPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).navigateToEditTaskPage(workFlowName).render();
            editTaskPage.enterComment("test comment");
            assertEquals(editTaskPage.readCommentFromCommentTextArea(), "test comment", "CL: The test comment was not entered in comment area");

            // --- Step 2 ---
            // --- Step action ---
            // Click on Reject button.
            // --- Expected results ---
            // The task is completed. It moves to the Completed Tasks filter.

            MyTasksPage myTasksPage = editTaskPage.selectRejectButton().render();
            assertTrue(myTasksPage.selectCompletedTasks().render().isTaskPresent(workFlowName), "CL: The Reject button was not clicked");

            ShareUser.logout(hybridDrone);

            // --- Step 3 ---
            // --- Step action ---
            // OP Open the received task details.
            // --- Expected results ---
            // The following data is present:
            // Title: 'Details: Cloud Review Task test message (Document was rejected on the cloud)'
            // Response
            // Required approval percentage: 50
            // Actual approval percentage: 100
            // Comments: test comment (Rejected)
            // TODO modify in TestLink the actual approval percentage to 0, since the task was rejected
            ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

            myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone).render();
            findTasks(drone, workFlowName);

            TaskDetailsPage taskDetailsPage = myTasksPage.selectViewTasks(workFlowName);

            assertEquals(taskDetailsPage.getTaskDetailsHeader(), "Details: " + workFlowName + " (Document was rejected on the cloud)");
            assertEquals(taskDetailsPage.getRequiredApprovalPercentage(), 50, "OP - Details Page: The required approval percentage is not 50");
            assertEquals(taskDetailsPage.getActualApprovalPercentage(), 0, "OP - Details Page: The actual approval percentage is not 0");
            assertEquals(taskDetailsPage.getComment(), getUserFullName(cloudUser) + ": " + "test comment  (Rejected)", "OP - Details Page: the comment is not the expected one");

            ShareUser.logout(drone);

        }
        catch (Throwable t)
        {
            reportError(hybridDrone, testName + "-HY", t);
            reportError(drone, testName + "-HY", t);
        }

    }

    @Test(groups = "DataPrepHybrid", timeOut = 500000)
    public void dataPrep_AONE_15626() throws Exception
    {

        testName = getTestName() + "A";
        String cloudSite = getSiteName(testName + "CL");
        String opSite = getSiteName(testName + "OP");
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser };
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        fileName = getFileName(testName) + "-15626" + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        String workFlowName = "Cloud Review Task test message" + testName + "-15626CL";

        List<String> userNames = new ArrayList<String>();
        userNames.add(cloudUser);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setReviewers(userNames);
        formDetails.setApprovalPercentage(50);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

        DocumentLibraryPage documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();
        documentLibraryPage.getFileDirectoryInfo(fileName).selectRequestSync().render();

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).render();
        findTasks(hybridDrone, workFlowName);
        EditTaskPage editTaskPage = myTasksPage.navigateToEditTaskPage(workFlowName).render();

        editTaskPage.enterComment("test comment");
        editTaskPage.selectSaveButton().render();

        ShareUser.logout(hybridDrone);
    }

    /**
     * AONE-15626:Cloud Review Task - Task Done
     */
    @Test(groups = "Hybrid", timeOut = 300000)
    public void AONE_15626() throws Exception
    {

        testName = getTestName() + "A";
        String cloudSite = getSiteName(testName + "CL");
        String opSite = getSiteName(testName + "OP");
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
        String fileName = getFileName(testName) + "-15626" + ".txt";

        String workFlowName = "Cloud Review Task test message" + testName + "-15626CL";

        try
        {

            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // --- Step 1 ---
            // --- Step action ---
            // Cloud Click on Edit button.
            // --- Expected results ---
            // The button is pressed. Edit Task page is opened.

            EditTaskPage editTaskPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).navigateToEditTaskPage(workFlowName).render();
            assertTrue(editTaskPage.isBrowserTitle("Edit Task"), "CL: The Edit Task page was not displayed");

            // --- Step 2 ---
            // --- Step action ---
            // Add any data into the Comment field, e.g. "test comment edited".
            // --- Expected results ---
            // Performed correctly.

            editTaskPage.enterComment("test comment edited");
            assertEquals(editTaskPage.readCommentFromCommentTextArea(), "test comment edited");

            // --- Step 3 ---
            // --- Step action ---
            // Specify any value in the Status drop-down list, e.g. 'Completed'.
            // --- Expected results ---
            // Performed correctly.

            editTaskPage.selectStatusDropDown(TaskStatus.COMPLETED);
            assertEquals(editTaskPage.getSelectedStatusFromDropDown(), TaskStatus.COMPLETED, "CL: The Completed task was not selecteed");

            // --- Step 4 ---
            // --- Step action ---
            // Click on Approve (or Reject) button.
            // --- Expected results ---
            // Task is closed. It is disappeared from the Active Tasks.

            MyTasksPage myTasksPage = editTaskPage.selectApproveButton().render();

            assertTrue(!myTasksPage.selectActiveTasks().render().isTaskPresent(workFlowName), "CL: The Approve button was not clicked");

            ShareUser.logout(hybridDrone);

        }
        catch (Throwable t)
        {
            reportError(hybridDrone, testName + "-HY", t);
            reportError(drone, testName + "-HY", t);
        }

    }

    @Test(groups = "DataPrepHybrid", timeOut = 500000)
    public void dataPrep_AONE_15627() throws Exception
    {

        testName = getTestName() + "A";
        String cloudSite = getSiteName(testName + "CL");
        String opSite = getSiteName(testName + "OP");
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser };
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        fileName = getFileName(testName) + "-15627" + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        String workFlowName = "Cloud Review Task test message" + testName + "-15627CL";

        List<String> userNames = new ArrayList<String>();
        userNames.add(cloudUser);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setReviewers(userNames);
        formDetails.setApprovalPercentage(100);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

        cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).render();

        findTasks(hybridDrone, workFlowName);

        EditTaskPage editTaskPage = myTasksPage.navigateToEditTaskPage(workFlowName).render();

        editTaskPage.enterComment("test comment");
        editTaskPage.selectStatusDropDown(TaskStatus.INPROGRESS);

        myTasksPage = editTaskPage.selectSaveButton().render();
        TaskDetailsPage taskDetailsPage = myTasksPage.selectViewTasks(workFlowName).render();

        assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.INPROGRESS);
        assertEquals(taskDetailsPage.getComment(), "test comment");

        editTaskPage = taskDetailsPage.selectEditButton().render();
        editTaskPage.enterComment("test comment edited");
        editTaskPage.selectStatusDropDown(TaskStatus.COMPLETED);
        editTaskPage.selectApproveButton().render();

        assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.COMPLETED);
        assertEquals(taskDetailsPage.getComment(), "test comment edited");

        ShareUser.logout(hybridDrone);
    }

    /**
     * AONE-15627:Cloud Review Task - Task Done - Details
     */
    @Test(groups = "Hybrid", timeOut = 500000)
    public void AONE_15627() throws Exception
    {

        testName = getTestName() + "A";
        String cloudSite = getSiteName(testName + "CL");
        String opSite = getSiteName(testName + "OP");
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
        String fileName = getFileName(testName) + "-15627" + ".txt";

        String workFlowName = "Cloud Review Task test message" + testName + "-15627CL";

        try
        {

            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // --- Step 1 ---
            // --- Step action ---
            // Cloud Verify My Tasks page.
            // --- Expected results ---
            // A completed task, e.g. "Cloud Review Task test message", is present in the Completed Tasks filter.

            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).render();
            assertTrue(myTasksPage.selectCompletedTasks().isTaskPresent(workFlowName), "CL: The task is not present in My Tasks page: " + workFlowName);

            // --- Step 2 ---
            // --- Step action ---
            // Open Task Details page.
            // --- Expected results ---
            // Details page is opened.

            TaskDetailsPage taskDetailsPage = myTasksPage.selectViewTasks(workFlowName).render();
            assertTrue(taskDetailsPage.isTitlePresent("Task Details"), "CL: The Task Details page was not displayed");

            // --- Step 3 ---
            // --- Step action ---
            // Verify the changed data.
            // --- Expected results ---
            // The following changes are present:
            // Edit button is absent under the information details section.
            // Status: 'Completed' in the Progress section
            // Comment: 'test comment edited (Approved)' in the Response section

            // TODO update TestLink with comment: "test comment edited"
            assertTrue(!taskDetailsPage.isEditButtonPresent(), "CL: The Edit button is present on Approved task");
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.COMPLETED, "CL: The task status is not Completed for Approved task");
            assertEquals(taskDetailsPage.getComment(), "test comment edited");

            // --- Step 4 ---
            // --- Step action ---
            // Cloud Open Task History page.
            // --- Expected results ---
            // Details page is opened."

            TaskHistoryPage taskHistoryPage = taskDetailsPage.selectTaskHistoryLink().render();
            assertTrue(taskHistoryPage.isBrowserTitle("Task History"), "CL: The Task History page was not displayed");

            // --- Step 5 ---
            // --- Step action ---
            // Verify the changed data.
            // --- Expected results ---
            // The following changes are present:
            // Completed: Thu 12 Sep 2013 20:26:03 in the General Info section
            // Status: Task is Complete in the General Info section

            assertTrue(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getCompletedDate().isBeforeNow());
            assertEquals(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getStatus(), WorkFlowStatus.TASK_COMPLETE, "Cl: The workflow status was not Task is Complete");

            // --- Step 6 ---
            // --- Step action ---
            // Verify 'Current Tasks' section.
            // --- Expected results ---
            // The following data is displayed: No tasks

            assertTrue(taskHistoryPage.isNoTasksMessageDisplayed());

            // --- Step 7 ---
            // --- Step action ---
            // Verify 'History' section.
            // --- Expected results ---
            // The following data is displayed:
            // Workflow Task Administrator admin Thu 12 Sep 2013 20:26:02 Task Done test comment edited
            // Start Review Administrator admin Thu 12 Sep 2013 17:17:42 Task Done

            // TODO update the TestLink with first history entry: the type should be Review, the outcome should be Approved

            List<WorkFlowDetailsHistory> historyList = taskHistoryPage.getWorkFlowHistoryList();

            assertEquals(historyList.size(), 2);
            assertEquals(historyList.get(0).getType(), WorkFlowHistoryType.REVIEW, "CL - History: The workflow type is not Review - first row");
            assertEquals(historyList.get(0).getCompletedBy(), getUserFullName(cloudUser), "CL - History: The assigned user is not cloud user - first row");
            assertEquals(historyList.get(0).getCompletedDate().toLocalDate(), getToDaysLocalDate());
            assertEquals(historyList.get(0).getOutcome(), WorkFlowHistoryOutCome.APPROVED, "CL - History: The workflow outcome is not Approved - first row");
            assertEquals(historyList.get(0).getComment(), "test comment edited");

            assertEquals(historyList.get(1).getType(), WorkFlowHistoryType.START_REVIEW, "CL - History: The workflow type is not Start Review - second row");
            assertEquals(historyList.get(1).getCompletedBy(), getUserFullName(cloudUser), "CL - History: The assigned user is not cloud user - second row");
            assertEquals(historyList.get(1).getCompletedDate().toLocalDate(), getToDaysLocalDate());
            assertEquals(historyList.get(1).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE, "CL - History: The workflow outcome is not Task Done - second row");
            assertEquals(historyList.get(1).getComment(), "");

            ShareUser.logout(hybridDrone);

            // --- Step 8 ---
            // --- Step action ---
            // OPOpen workflow details page.
            // --- Expected results ---
            // Details page is opened."

            ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
            WorkFlowDetailsPage workFlowDetailsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone).render().selectWorkFlow(workFlowName).render();
            assertTrue(workFlowDetailsPage.isTitlePresent("Workflow Details"), "OP: The Workflow Details page was not displayed");

            // --- Step 9 ---
            // --- Step action ---
            // Verify 'Current Tasks' section.
            // --- Expected results ---
            // The following data is displayed:
            // Verify task was completed on the cloud admin (None) Not Yet Started
            // TODO task was approved

            List<WorkFlowDetailsCurrentTask> workFlowDetailsCurrentTask = workFlowDetailsPage.getCurrentTasksList();
            assertEquals(workFlowDetailsCurrentTask.get(0).getTaskType(), CurrentTaskType.DOCUMENT_WAS_APPROVED_ON_CLOUD, "OP- Workflow Details: The Current Task Type was not Approved on Cloud");
            assertEquals(workFlowDetailsCurrentTask.get(0).getAssignedTo(), getUserFullName(opUser), "OP- Workflow Details: The Current Task assigned user is not opUser");
            assertEquals(workFlowDetailsCurrentTask.get(0).getDueDateString(), NONE);
            assertEquals(workFlowDetailsCurrentTask.get(0).getTaskStatus(), TaskStatus.NOTYETSTARTED, "OP- Workflow Details: The Current Task Status is not Not Yet Started");

            // --- Step 10 ---
            // --- Step action ---
            // OP Verify My Tasks page.
            // --- Expected results ---
            // An active task, e.g. "Cloud Review Task test message", is present in the Active Tasks filter.

            myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone).selectActiveTasks().render();
            assertTrue(myTasksPage.isTaskPresent(workFlowName), "The task is not present in My Tasks page: " + workFlowName);

            // --- Step 11 ---
            // --- Step action ---
            // Open the task details.
            // --- Expected results ---
            // Performed correctly. Information details are displayed. The title is
            // "Details: Cloud Review Task test message (Document was approved on the cloud)". Edit button is present under the information details section.

            taskDetailsPage = myTasksPage.selectViewTasks(workFlowName).render();
            assertTrue(taskDetailsPage.isBrowserTitle("Task Details"));

            String header = "Details: " + workFlowName + " (Document was approved on the cloud)";
            assertEquals(taskDetailsPage.getTaskDetailsHeader(), header);
            assertTrue(taskDetailsPage.isEditButtonPresent(), "OP - Task Details: Edit button is not present");

            // --- Step 12 ---
            // --- Step action ---
            // Verify 'Info' section.
            // --- Expected results ---
            // The following data is displayed:
            // Message: Cloud Review Task test message
            // Owner: Administrator
            // Priority: Medium
            // Due: (None)
            // Identifier: 416

            TaskInfo taskInfo = taskDetailsPage.getTaskDetailsInfo();

            assertEquals(taskInfo.getMessage(), workFlowName, "OP-Task Info: The message is not the expected one: " + workFlowName);
            assertEquals(taskInfo.getOwner(), getUserFullName(opUser), "OP-Task Info: The owner is not the opUser");
            assertEquals(taskInfo.getPriority(), Priority.MEDIUM, "OP-Task Info: Task priority is not MEDIUM");
            assertEquals(taskInfo.getDueDateString(), NONE, "OP-Task Info: Due date is not NONE");
            assertNotNull(taskInfo.getIdentifier());

            // --- Step 13 ---
            // --- Step action ---
            // Verify 'Progress' section.
            // --- Expected results ---
            // The following data is displayed:
            // Status: Not Yet Started

            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.NOTYETSTARTED, "OP: The task status is not Not Yet Started");

            // --- Step 14 ---
            // --- Step action ---
            // Verify 'Items' section.
            // --- Expected results ---
            // The following item is displayed: test1.txt

            List<TaskItem> taskItems = taskDetailsPage.getTaskItems();
            assertEquals(taskItems.size(), 1);
            assertEquals(taskItems.get(0).getItemName(), fileName, "OP: The item uploaded is not the expected one: " + fileName);

            // --- Step 15 ---
            // --- Step action ---
            // Verify 'Response' section.
            // --- Expected results ---
            // The following data is displayed:
            // Comment: test comment edited

            assertEquals(taskDetailsPage.getComment(), getUserFullName(cloudUser) + ": " + "test comment edited  (Approved)", "The comment in Response area is not the expected one");

            ShareUser.logout(drone);

        }
        catch (Throwable t)
        {
            reportError(hybridDrone, testName + "-HY", t);
            reportError(drone, testName + "-HY", t);
        }

    }

    @Test(groups = "DataPrepHybrid", timeOut = 500000)
    public void dataPrep_AONE_15628() throws Exception
    {

        testName = getTestName() + "A";
        String cloudSite = getSiteName(testName + "CL");
        String opSite = getSiteName(testName + "OP");
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser };
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        fileName = getFileName(testName) + "-15628" + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        String workFlowName = "Cloud Review Task test message" + testName + "-15628CL";

        List<String> userNames = new ArrayList<String>();
        userNames.add(cloudUser);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setReviewers(userNames);
        formDetails.setApprovalPercentage(100);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

        DocumentLibraryPage documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();
        documentLibraryPage.getFileDirectoryInfo(fileName).selectRequestSync().render();

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // edit task with comment an
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).render();
        findTasks(hybridDrone, workFlowName);

        EditTaskPage editTaskPage = myTasksPage.navigateToEditTaskPage(workFlowName).render();

        editTaskPage.enterComment("test comment");
        editTaskPage.selectStatusDropDown(TaskStatus.INPROGRESS);

        myTasksPage = editTaskPage.selectSaveButton().render();
        findTasks(hybridDrone, workFlowName);
        TaskDetailsPage taskDetailsPage = myTasksPage.selectViewTasks(workFlowName).render();

        assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.INPROGRESS);
        assertEquals(taskDetailsPage.getComment(), "test comment");

        editTaskPage = taskDetailsPage.selectEditButton().render();
        editTaskPage.enterComment("test comment edited");
        editTaskPage.selectStatusDropDown(TaskStatus.COMPLETED);
        editTaskPage.selectApproveButton().render();

        assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.COMPLETED);
        assertEquals(taskDetailsPage.getComment(), "test comment edited");

        ShareUser.logout(hybridDrone);
    }

    /**
     * AONE-15628:Cloud Review Task - Task Done - Edit Task Details (OP)
     */
    @Test(groups = "Hybrid", timeOut = 300000)
    public void AONE_15628() throws Exception
    {

        testName = getTestName() + "A";
        String cloudSite = getSiteName(testName + "CL");
        String opSite = getSiteName(testName + "OP");
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
        String fileName = getFileName(testName) + "-15628" + ".txt";

        String workFlowName = "Cloud Review Task test message" + testName + "-15628CL";

        try
        {

            ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

            // --- Step 1 ---
            // --- Step action ---
            // OP Click on Edit button.
            // --- Expected results ---
            // The button is pressed. Edit Task page is opened.

            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone).render();
            findTasks(drone, workFlowName);
            EditTaskPage editTaskPage = myTasksPage.selectViewTasks(workFlowName).selectEditButton().render();
            assertTrue(editTaskPage.isBrowserTitle("Edit Task"), "The Edit Task page was not displayed");

            // --- Step 2 ---
            // --- Step action ---
            // --- Expected results ---
            // The following additional controls are present:
            // Status drop-down list
            // View More Actions button for the document
            // Task Done button
            // Save and close button
            // Cancel button

            List<TaskStatus> statusOptions = editTaskPage.getStatusOptions();
            assertEquals(statusOptions.size(), TaskStatus.values().length);

            List<TaskItem> taskItems = editTaskPage.getTaskItem(fileName);
            assertTrue(taskItems.get(0).getViewMoreActionsLink().getDescription().contains("View More Actions"), "OP - Edit Task: View More Actions is not present");
            assertTrue(editTaskPage.isButtonsDisplayed(Button.TASK_DONE), "OP - Edit Task: Task Done button is not present");
            assertTrue(editTaskPage.isButtonsDisplayed(Button.SAVE_AND_CLOSE), "OP - Edit Task: Save and Close button is not present");
            assertTrue(editTaskPage.isButtonsDisplayed(Button.CANCEL), "OP - Edit Task: Cancel button is not present");

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

            assertTrue(statusOptions.containsAll(getTaskStatusList()), "OP - Edit Task: Not all items are displayed in Status drop-down list");

            // --- Step 4 ---
            // --- Step action ---
            // Specify any value in the Status drop-down list, e.g. 'In Progress'.
            // --- Expected results ---
            // Performed correctly.

            editTaskPage.selectStatusDropDown(TaskStatus.INPROGRESS);
            assertEquals(editTaskPage.getSelectedStatusFromDropDown(), TaskStatus.INPROGRESS, "OP - Edit Task: In Progress status was not selected");

            // --- Step 5 ---
            // --- Step action ---
            // Click on Cancel button.
            // --- Expected results ---
            // Edit Task page is closed. Task Details are displayed. No data was changed.
            // Comment: (None)

            TaskDetailsPage taskDetailsPage = editTaskPage.selectCancelButton().render();
            assertTrue(taskDetailsPage.isTitlePresent("Task Details"), "OP: Cancel button was not clicked");

            // --- Step 6 ---
            // --- Step action ---
            // Repeat steps 1-4.
            // --- Expected results ---
            // Performed correctly.

            taskDetailsPage.selectEditButton().render();
            editTaskPage.selectStatusDropDown(TaskStatus.INPROGRESS);
            assertEquals(editTaskPage.getSelectedStatusFromDropDown(), TaskStatus.INPROGRESS, "OP - Edit Task: In Progress status was not selected");

            // --- Step 7 ---
            // --- Step action ---
            // Click on Save and Close button.
            // --- Expected results ---
            // Edit Task page is closed. Task Details are displayed. The specified data was changed.

            taskDetailsPage = editTaskPage.selectSaveButton().render();
            assertTrue(taskDetailsPage.isBrowserTitle("Task Details"));
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.INPROGRESS, "OP - Edit Task: Save and Close button was not clicked");

            ShareUser.logout(drone);

        }
        catch (Throwable t)
        {
            reportError(hybridDrone, testName + "-HY", t);
            reportError(drone, testName + "-HY", t);
        }

    }

    @Test(groups = "DataPrepHybrid", timeOut = 500000)
    public void dataPrep_AONE_15629() throws Exception
    {

        testName = getTestName() + "A";
        String cloudSite = getSiteName(testName + "CL");
        String opSite = getSiteName(testName + "OP");
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser };
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        fileName = getFileName(testName) + "-15629" + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        String workFlowName = "Cloud Review Task test message" + testName + "-15629CL";

        List<String> userNames = new ArrayList<String>();
        userNames.add(cloudUser);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setReviewers(userNames);
        formDetails.setApprovalPercentage(100);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

        DocumentLibraryPage documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();
        documentLibraryPage.getFileDirectoryInfo(fileName).selectRequestSync().render();

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // edit task with comment an
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).render();
        findTasks(hybridDrone, workFlowName);
        EditTaskPage editTaskPage = myTasksPage.navigateToEditTaskPage(workFlowName).render();

        editTaskPage.enterComment("test comment");
        editTaskPage.selectStatusDropDown(TaskStatus.INPROGRESS);

        myTasksPage = editTaskPage.selectSaveButton().render();
        TaskDetailsPage taskDetailsPage = myTasksPage.selectViewTasks(workFlowName).render();

        assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.INPROGRESS);
        assertEquals(taskDetailsPage.getComment(), "test comment");

        editTaskPage = taskDetailsPage.selectEditButton().render();
        editTaskPage.enterComment("test comment edited");
        editTaskPage.selectStatusDropDown(TaskStatus.COMPLETED);
        editTaskPage.selectApproveButton().render();

        assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.COMPLETED);
        assertEquals(taskDetailsPage.getComment(), "test comment edited");

        ShareUser.logout(hybridDrone);
    }

    /**
     * AONE-15629:Cloud Review Task - Task Done - Complete (OP)
     */
    @Test(groups = "Hybrid", timeOut = 500000)
    public void AONE_15629() throws Exception
    {

        testName = getTestName() + "A";
        String cloudSite = getSiteName(testName + "CL");
        String opSite = getSiteName(testName + "OP");
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
        String fileName = getFileName(testName) + "-15629" + ".txt";

        String workFlowName = "Cloud Review Task test message" + testName + "-15629CL";

        try
        {

            ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

            // --- Step 1 ---
            // --- Step action ---
            // OP Click on Edit button.
            // --- Expected results ---
            // The button is pressed. Edit Task page is opened.

            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone).render();
            findTasks(drone, workFlowName);
            EditTaskPage editTaskPage = myTasksPage.selectViewTasks(workFlowName).selectEditButton().render();
            assertTrue(editTaskPage.isBrowserTitle("Edit Task"), "OP: Edit Task page was not displayed");

            // --- Step 2 ---
            // --- Step action ---
            // Specify any value in the Status drop-down list, e.g. 'Completed'.
            // --- Expected results ---
            // Performed correctly.

            editTaskPage.selectStatusDropDown(TaskStatus.COMPLETED);
            assertEquals(editTaskPage.getSelectedStatusFromDropDown(), TaskStatus.COMPLETED, "OP - Edit Task: Completed status was not selected");

            // --- Step 3 ---
            // --- Step action ---
            // Click on Task Done button.
            // --- Expected results ---
            // Edit Task page is closed. Task Details are displayed. The specified data was changed.

            TaskDetailsPage taskDetailsPage = editTaskPage.selectTaskDoneButton().render();
            assertTrue(taskDetailsPage.isBrowserTitle("Task Details"), "OP: Task Done button was not clicked");

            // --- Step 4 ---
            // --- Step action ---
            // Verify My Tasks page.
            // --- Expected results ---
            // A completed task, e.g. "Cloud Review Task test message", is present in the Completed Tasks filter.

            myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone).render();
            assertTrue(myTasksPage.selectCompletedTasks().render().isTaskPresent(workFlowName), "OP: The task is not displayed in My Tasks Page: " + workFlowName);

            // --- Step 5 ---
            // --- Step action ---
            // Verify Workflows I've started page.
            // --- Expected results ---
            // A completed workflow, e.g. "Cloud Review Task test message", is present in the Completed Workflows filter.

            MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone).render().selectCompletedWorkFlows().render();
            assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName), "OP: The workflow is not displayed in Completed Workflow page: " + workFlowName);

            // --- Step 6 ---
            // --- Step action ---
            // Verify Workflow's Details page.
            // --- Expected results ---
            // The following changes are present:
            // Completed: Thu 12 Sep 2013 20:26:03 in the General Info section
            // Status: Workflow is Complete in the General Info section
            // Delete Workflow button

            WorkFlowDetailsPage workFlowDetailsPage = ShareUserWorkFlow.navigateToWorkFlowDetailsPage(drone, workFlowName).render();

            assertTrue(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getCompletedDate().isBeforeNow());
            assertEquals(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getStatus(), WorkFlowStatus.WORKFLOW_COMPLETE,
                    "OP - Workflow Details: The workflow status is not Completed");
            assertTrue(workFlowDetailsPage.isDeleteWorkFlowButtonDisplayed(), "OP - Workflow Details: The Delete button is not displayed");

            // --- Step 7 ---
            // --- Step action ---
            // Verify 'Current Tasks' section.
            // --- Expected results ---
            // The following data is displayed: No tasks

            assertTrue(workFlowDetailsPage.isNoTasksMessageDisplayed());

            // --- Step 8 ---
            // --- Step action ---
            // Verify 'History' section.
            // --- Expected results ---
            // The following data is displayed:
            // Document was approved on the cloud admin Thu 12 Sep 2013 21:02:27 Task Done
            // Start a task or review on Alfresco Cloud admin Thu 12 Sep 2013 17:15:07 Task Done

            List<WorkFlowDetailsHistory> historyList = workFlowDetailsPage.getWorkFlowHistoryList();

            assertEquals(historyList.size(), 2);
            assertEquals(historyList.get(0).getType(), WorkFlowHistoryType.DOCUMENT_WAS_APPROVED_ON_CLOUD, "OP-History: The Workflow type is not Document was approved in the cloud - first row");
            assertEquals(historyList.get(0).getCompletedBy(), getUserFullName(opUser), "OP-History: The Workflow assigned user is not the opUser - first row");
            assertEquals(historyList.get(0).getCompletedDate().toLocalDate(), getToDaysLocalDate());
            assertEquals(historyList.get(0).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE, "OP-History: The Workflow outcome is not Task Done - first row");
            assertEquals(historyList.get(0).getComment(), "");

            assertEquals(historyList.get(1).getType(), WorkFlowHistoryType.START_TASK_OR_REVIEW_ON_CLOUD, "OP-History: The Workflow type is not Document was approved in the cloud - second row");
            assertEquals(historyList.get(1).getCompletedBy(), getUserFullName(opUser), "OP-History: The Workflow assigned user is not the opUser - second row");
            assertEquals(historyList.get(1).getCompletedDate().toLocalDate(), getToDaysLocalDate());
            assertEquals(historyList.get(1).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE, "OP-History: The Workflow outcome is not Task Done - second row");
            assertEquals(historyList.get(1).getComment(), "");

            ShareUser.logout(drone);

        }
        catch (Throwable t)
        {
            reportError(hybridDrone, testName + "-HY", t);
            reportError(drone, testName + "-HY", t);
        }

    }

    private void findTasks(WebDrone driver, String workFlowName)
    {

        assertTrue(driver.findAndWaitWithRefresh(By.xpath(String.format("//a[text()='%s']", workFlowName))).isDisplayed());

    }
}
