package org.alfresco.share.workflow.cloudReviewTask;

import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.task.TaskDetailsPage;
import org.alfresco.po.share.task.TaskInfo;
import org.alfresco.po.share.task.TaskItem;
import org.alfresco.po.share.task.TaskStatus;
import org.alfresco.po.share.workflow.*;
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

public class CreateCloudReviewTaskTests extends AbstractWorkflow
{

        private String testDomain;
        private String opUser;
        private String cloudUser;
        private String cloudSite;
        private String opSite;
        private String fileName;
        private String folderName;

        @Override
        @BeforeClass(alwaysRun = true)
        public void setup() throws Exception
        {

                super.setup();
                testDomain = DOMAIN_HYBRID;

        }

        @Test(groups = "DataPrepHybrid", timeOut = 300000)
        public void dataPrep_AONE_15617() throws Exception
        {

                testName = getTestName() + "T";
                String cloudSite = getSiteName(testName + "CL");
                String opSite = getSiteName(testName + "OP");
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String fileName = getFileName(testName) + "-15617" + ".txt";
                String[] fileInfo = { fileName, DOCLIB };

                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

                // Create User1 (Cloud)
                CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
                CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

                // Login to User1, set up the cloud sync
                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
                ShareUser.createSite(drone, opSite, SITE_VISIBILITY_PUBLIC);
                ShareUser.uploadFileInFolder(drone, fileInfo).render();
                ShareUser.logout(drone);

                ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(hybridDrone);

        }

        /**
         * AONE-15617:Cloud Review Task - Create
         */
        @Test(groups = "Hybrid", timeOut = 300000)
        public void AONE_15617() throws Exception
        {
                testName = getTestName() + "T";
                String cloudSite = getSiteName(testName + "CL");
                String opSite = getSiteName(testName + "OP");
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String folderName = getFolderName(testName);
                String fileName = getFileName(testName) + "-15617" + ".txt";
                String[] fileInfo = { fileName, DOCLIB };

                String workFlowName = "Cloud Review Task test message" + testName + "-15617CL";
                fileName = getFileName(testName) + "-15617" + ".txt";

                try
                {
                        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

                        ShareUser.openSitesDocumentLibrary(drone, opSite).render();

                        // --- Step 1 ---
                        // --- Step action ---
                        // OPSpecify 'Cloud Review Task' type.
                        // --- Expected results ---
                        // Performed correctly.

                        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName);

                        // --- Step 2 ---
                        // --- Step action ---
                        // Specify any data in other required fields, e.g.
                        // Message: 'Cloud Review Task test message'
                        // Network: 'network.com'
                        // Site: 'user1 user1's Home'
                        // Folder: 'Documents/'
                        // Assignee: 'user1@network.com'
                        // Required Approval Percentage: 100 After completion: any
                        // Lock on-premise content: any
                        // Items: 'test1.txt'
                        // --- Expected results ---
                        // Performed correctly.

                        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

                        List<String> userNames = new ArrayList<String>();
                        userNames.add(cloudUser);

                        formDetails.setMessage(workFlowName);
                        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
                        formDetails.setSiteName(cloudSite);
                        formDetails.setReviewers(userNames);
                        formDetails.setApprovalPercentage(100);
                        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);
                        formDetails.setTaskPriority(Priority.MEDIUM);

                        // --- Step 3 ---
                        // --- Step action ---
                        // Click on Start Workflow button.
                        // --- Expected results ---
                        // Workflow is started successfully. The workflow is located under
                        // Active on Workflows I've Started page.

                        cloudTaskOrReviewPage.startWorkflow(formDetails).render();

                        MyWorkFlowsPage myWrokFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone).render();
                        assertTrue(myWrokFlowsPage.isWorkFlowPresent(workFlowName), "OP: The workflow is not displayed under Workflow I've started page");
                        ShareUser.logout(drone);

                        // --- Step 4 ---
                        // --- Step action ---
                        // Cloud Login as user1@network.com and verify the workflow.
                        // --- Expected results ---
                        // The workflow is started in Cloud. It is Active on Workflows I've
                        // Started page. A new task is assigned to the specified user.

                        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
                        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).render();
                        assertTrue(myTasksPage.isTaskPresent(workFlowName), "Cloud: The workflow is not displayed under Workflow I've started page");
                        ShareUser.logout(hybridDrone);

                }
                catch (Throwable t)
                {
                        reportError(drone, testName + "-HY", t);
                        reportError(hybridDrone, testName + "-HY", t);
                }

        }

        @Test(groups = "DataPrepHybrid", timeOut = 500000)
        public void dataPrep_AONE_15618() throws Exception
        {

                testName = getTestName() + "T2";
                String cloudSite = getSiteName(testName + "CL");
                String opSite = getSiteName(testName + "OP");
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String folderName = getFolderName(testName);
                String fileName = getFileName(testName) + "-15618" + ".txt";
                String[] fileInfo = { fileName, DOCLIB };

                String workFlowName = "Cloud Review Task test message" + testName + "-15618CL";

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
                ShareUser.logout(hybridDrone);

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                ShareUser.openSiteDashboard(drone, opSite);
                ShareUser.uploadFileInFolder(drone, fileInfo).render();
                ShareUser.openSitesDocumentLibrary(drone, opSite).render();

                CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName).render();

                List<String> userNames = new ArrayList<String>();
                userNames.add(cloudUser);

                WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

                formDetails.setMessage(workFlowName);
                formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
                formDetails.setTaskPriority(Priority.MEDIUM);
                formDetails.setSiteName(cloudSite);
                formDetails.setReviewers(userNames);
                formDetails.setApprovalPercentage(100);
                formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

                // Create Workflow using File1
                cloudTaskOrReviewPage.startWorkflow(formDetails).render();

                ShareUser.logout(drone);
        }

        /**
         * AONE-15619:Cloud Review Task - Workflow Details (Cloud)
         */
        @Test(groups = "Hybrid", timeOut = 300000)
        public void AONE_15619() throws Exception
        {

                testName = getTestName() + "T1";
                String cloudSite = getSiteName(testName + "CL");
                String opSite = getSiteName(testName + "OP");
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);

                String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
                String fileName = getFileName(testName) + "-15619" + ".txt";

                String workFlowName = "Cloud Review Task test message" + testName + "-15619CL";

                try
                {
                        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

                        // --- Step 1 ---
                        // --- Step action ---
                        // Cloud Open Task History page.
                        // --- Expected results ---
                        // Details page is opened. The following title is displayed:
                        // "Details: Cloud Review Task test message (Start Review)"

                        TaskHistoryPage taskHistoryPage = ShareUserWorkFlow.navigateToTaskHistoryPage(hybridDrone, workFlowName).render();

                        String header = "Details: " + workFlowName + " (Review)";

                        assertEquals(taskHistoryPage.getPageHeader(), header);

                        // --- Step 2 ---
                        // --- Step action ---
                        // Verify 'General Info' section.
                        // --- Expected results ---
                        // The following data is displayed:
                        // Title: Hybrid Review
                        // Description: Request document approval from someone on the Cloud
                        // Started by: Administrator admin
                        // Due: (None)
                        // Completed: in progress
                        // Started: Thu 12 Sep 2013 17:17:42
                        // Priority: Medium
                        // Status: Task is in Progress
                        // Message: Cloud Review Task test message

                        assertEquals(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getTitle(), WorkFlowTitle.HYBRID_REVIEW, "Task History Page: Title is not displayed");
                        assertEquals(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getDescription(), WorkFlowDescription.REQUEST_DOCUMENT_APPROVAL, "Task History Page: Description is not the expected");
                        assertEquals(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getStartedBy(), getUserFullName(cloudUser), "Task History Page: Started by is not the expected one");
                        assertEquals(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getDueDateString(), NONE, "Task History Page: The date is not (NONE)");
                        assertEquals(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getCompleted(), "<in progress>", "Task History Page: The Completed status is not in progress");
                        assertTrue(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getStartDate().isBeforeNow(), "Task History Page: The start date is not the expected one");
                        assertEquals(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getPriority(), Priority.MEDIUM, "Task History Page: The priority is different than Medium");
                        assertEquals(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getStatus(), WorkFlowStatus.TASK_IN_PROGRESS, "Task History Page: Task is not in progress");
                        assertEquals(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getMessage(), workFlowName, "Task History Page: The message is not the expected one");

                        // --- Step 3 ---
                        // --- Step action ---
                        // Verify 'More Info' section.
                        // --- Expected results ---
                        // The following data is displayed:
                        // Send Email Notifications: No

                        List<String> userNames = new ArrayList<String>();
                        userNames.add(cloudUser);

                        assertEquals(taskHistoryPage.getWorkFlowDetailsMoreInfo().getNotification(), SendEMailNotifications.YES);

                        // --- Step 4 ---
                        // --- Step action ---
                        // Verify 'Items' section.
                        // --- Expected results ---
                        // The following item is displayed: test1.txt

                        List<WorkFlowDetailsItem> items = taskHistoryPage.getWorkFlowItems();
                        assertEquals(items.size(), 1);
                        assertEquals(items.get(0).getItemName(), fileName);

                        // --- Step 5 ---
                        // --- Step action ---
                        // Verify 'Current Tasks' section.
                        // --- Expected results ---
                        // The following data is displayed:
                        // Review Administrator admin (None) Not Yet Started

                        List<WorkFlowDetailsCurrentTask> currentTaskList = taskHistoryPage.getCurrentTasksList();

                        assertEquals(currentTaskList.size(), 1, "There are more than 1 task in current tasks list");
                        assertEquals(currentTaskList.get(0).getTaskType(), CurrentTaskType.REVIEW, "The task type is not Review ");
                        assertEquals(currentTaskList.get(0).getAssignedTo(), getUserFullName(cloudUser), "The assigned user is not the cloud user");
                        assertEquals(currentTaskList.get(0).getDueDateString(), NONE, "The due date is not NONE");
                        assertEquals(currentTaskList.get(0).getTaskStatus(), TaskStatus.NOTYETSTARTED, "The task status is different thank Not Yet Started");

                        // --- Step 6 ---
                        // --- Step action ---
                        // Verify 'History' section.
                        // --- Expected results ---
                        // The following data is displayed:
                        // Start Review Administrator admin Thu 12 Sep 2013 17:17:42 Task
                        // Done

                        List<WorkFlowDetailsHistory> historyList = taskHistoryPage.getWorkFlowHistoryList();

                        assertEquals(historyList.size(), 1);
                        assertEquals(historyList.get(0).getType(), WorkFlowHistoryType.START_REVIEW, "Workflow History List: The type is different than Start Review");
                        assertEquals(historyList.get(0).getCompletedBy(), getUserFullName(cloudUser), "Workflow History List: The user name from completed by section is not the cloud user");
                        assertEquals(historyList.get(0).getCompletedDate().toLocalDate(), getToDaysLocalDate(), "Workflow History List: The completed date is not the current date");
                        assertEquals(historyList.get(0).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE, "Workflow History List: The task is not marked as DONE");
                        assertEquals(historyList.get(0).getComment(), "", "Workflow History List: The comment is different than empty");

                        ShareUser.logout(hybridDrone);

                }
                catch (Throwable t)
                {
                        reportError(drone, testName + "-HY", t);
                        reportError(hybridDrone, testName + "-HY", t);
                }

        }

        @Test(groups = "DataPrepHybrid", timeOut = 500000)
        public void dataPrep_AONE_15619() throws Exception
        {

                testName = getTestName() + "T1";
                String cloudSite = getSiteName(testName + "CL");
                String opSite = getSiteName(testName + "OP");
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String folderName = getFolderName(testName);
                String fileName = getFileName(testName) + "-15619" + ".txt";
                String[] fileInfo = { fileName, DOCLIB };

                String workFlowName = "Cloud Review Task test message" + testName + "-15619CL";

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
                ShareUser.logout(hybridDrone);

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                ShareUser.openSiteDashboard(drone, opSite);
                ShareUser.uploadFileInFolder(drone, fileInfo).render();
                ShareUser.openSitesDocumentLibrary(drone, opSite).render();

                CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName).render();

                List<String> userNames = new ArrayList<String>();
                userNames.add(cloudUser);

                WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

                formDetails.setMessage(workFlowName);
                formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
                formDetails.setTaskPriority(Priority.MEDIUM);
                formDetails.setSiteName(cloudSite);
                formDetails.setReviewers(userNames);
                formDetails.setApprovalPercentage(100);
                formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

                // Create Workflow using File1
                cloudTaskOrReviewPage.startWorkflow(formDetails).render();

                ShareUser.logout(drone);

        }

        @Test(groups = "DataPrepHybrid", timeOut = 500000)
        public void dataPrep_AONE_15620() throws Exception
        {

                testName = getTestName() + "T";
                String cloudSite = getSiteName(testName + "CL");
                String opSite = getSiteName(testName + "OP");
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String folderName = getFolderName(testName);
                String fileName = getFileName(testName) + "-15620" + ".txt";
                String[] fileInfo = { fileName, DOCLIB };

                String workFlowName = "Cloud Review Task test message" + testName + "-15620CL";

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
                ShareUser.logout(hybridDrone);

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                ShareUser.openSiteDashboard(drone, opSite);
                ShareUser.uploadFileInFolder(drone, fileInfo).render();
                ShareUser.openSitesDocumentLibrary(drone, opSite).render();

                CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName).render();

                List<String> userNames = new ArrayList<String>();
                userNames.add(cloudUser);

                WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

                formDetails.setMessage(workFlowName);
                formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
                formDetails.setTaskPriority(Priority.MEDIUM);
                formDetails.setSiteName(cloudSite);
                formDetails.setReviewers(userNames);
                formDetails.setApprovalPercentage(100);
                formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

                // Create Workflow using File1
                cloudTaskOrReviewPage.startWorkflow(formDetails).render();

                ShareUser.logout(drone);

        }

        /**
         * AONE-15619:Cloud Review Task - Workflow Details (Cloud)
         */
        @Test(groups = "Hybrid", timeOut = 300000)
        public void AONE_15620() throws Exception
        {
                testName = getTestName() + "T";
                String cloudSite = getSiteName(testName + "CL");
                String opSite = getSiteName(testName + "OP");
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);

                String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
                String fileName = getFileName(testName) + "-15620" + ".txt";

                String workFlowName = "Cloud Review Task test message" + testName + "-15620CL";

                try
                {
                        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

                        // --- Step 1 ---
                        // --- Step action ---
                        // Cloud Verify My Tasks page.
                        // --- Expected results ---
                        // A new active task, e.g. "Cloud Review Task test message", is
                        // present.

                        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).render();
                        assertTrue(myTasksPage.isTaskPresent(workFlowName));

                        // --- Step 2 ---
                        // --- Step action ---
                        // Open the task details.
                        // --- Expected results ---
                        // Performed correctly.
                        // Information details are displayed.
                        // The title is "Details: Cloud Review Task test message (Review)".
                        // Edit button is present under the information details section.

                        TaskDetailsPage taskDetailsPage = myTasksPage.selectViewTasks(workFlowName).render();
                        assertTrue(taskDetailsPage.isBrowserTitle("Task Details"), "The Task Details page was not rendered");

                        String header = "Details: " + workFlowName + " (Review)";
                        assertEquals(taskDetailsPage.getTaskDetailsHeader(), header, "The Task Details Page header is not the expected one");

                        assertTrue(taskDetailsPage.isEditButtonPresent(), "The Edit button is not displayed");

                        // --- Step 3 ---
                        // --- Step action ---
                        // Verify 'Info' section.
                        // --- Expected results ---
                        // The following data is displayed:
                        // Message: Cloud Review Task test message
                        // Owner: Administrator admin
                        // Priority: Medium
                        // Due: (None)
                        // Identifier: 101713

                        TaskInfo taskInfo = taskDetailsPage.getTaskDetailsInfo();

                        assertEquals(taskInfo.getMessage(), workFlowName, "Task Details Page: the message is not the expected one " + workFlowName);
                        assertEquals(taskInfo.getOwner(), getUserFullName(cloudUser), "Task Details Page: the task owner is not the cloud user " + getUserFullName(cloudUser));
                        assertEquals(taskInfo.getPriority(), Priority.MEDIUM, "Task Details Page: the priority is not the expected one: MEDIUM");
                        assertEquals(taskInfo.getDueDateString(), NONE, "Task Details Page: The due date is different than NONE");
                        assertNotNull(taskInfo.getIdentifier(), "Task Details Page: The identifier is Null");

                        // --- Step 4 ---
                        // --- Step action ---
                        // Verify 'Progress' section.
                        // --- Expected results ---
                        // The following data is displayed:
                        // Status: Not Yet Started

                        assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.NOTYETSTARTED, "Task Details Page: The status is different than Not Yet Started");

                        // --- Step 5 ---
                        // --- Step action ---
                        // Verify 'Items' section.
                        // --- Expected results ---
                        // The following data is displayed:
                        // The following item is displayed: test1.txt

                        List<TaskItem> items = taskDetailsPage.getTaskItems();
                        assertEquals(items.size(), 1, "There are more than 1 item in Task Details Page");
                        assertEquals(items.get(0).getItemName(), fileName, "The file name is not the expected one " + fileName);

                        // --- Step 6 ---
                        // --- Step action ---
                        // Verify 'Response' section.
                        // --- Expected results ---
                        // The following data is displayed:
                        // Comment: (None)

                        assertEquals(taskDetailsPage.getComment(), NONE, "Task Details Page: The comment is different than NONE");

                        ShareUser.logout(hybridDrone);

                }
                catch (Throwable t)
                {
                        reportError(drone, testName + "-HY", t);
                        reportError(hybridDrone, testName + "-HY", t);
                }
        }

        /**
         * AONE-15618:Cloud Review Task - Workflow Details (OP)
         */
        @Test(groups = "Hybrid", timeOut = 500000)
        public void AONE_15618() throws Exception
        {

                testName = getTestName() + "T2";
                String cloudSite = getSiteName(testName + "CL");
                String opSite = getSiteName(testName + "OP");
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);

                String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
                String fileName = getFileName(testName) + "-15618" + ".txt";

                String workFlowName = "Cloud Review Task test message" + testName + "-15618CL";

                try
                {
                        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

                        // --- Step 1 ---
                        // --- Step action ---
                        // OP Open created workflow details page.
                        // --- Expected results ---
                        // Details page is opened.
                        // The following title is displayed:
                        // "Details: Cloud Review Task test message (Start a task or review on Alfresco Cloud)"

                        WorkFlowDetailsPage workFlowDetailsPage = ShareUserWorkFlow.navigateToWorkFlowDetailsPage(drone, workFlowName).render();
                        String header = "Details: " + workFlowName + " (Start a task or review on Alfresco Cloud)";

                        assertEquals(workFlowDetailsPage.getPageHeader(), header, "The page header is different than expected one " + header);

                        // --- Step 2 ---
                        // --- Step action ---
                        // Verify 'General Info' section.
                        // --- Expected results ---
                        // The following data is displayed:
                        // Title: Cloud Task or Review
                        // Description: Create a task or start a review on Alfresco Cloud
                        // Started by: Administrator
                        // Due: (None)
                        // Completed: in progress
                        // Started: Thu 12 Sep 2013 17:15:07
                        // Priority: Medium
                        // Status: Workflow is in Progress
                        // Message: Cloud Review Task test message

                        assertEquals(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getTitle(), WorkFlowTitle.CLOUD_TASK_OR_REVIEW, "Workflow Details Page: The title is not the expected one");
                        assertEquals(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getDescription(), WorkFlowDescription.CREATE_A_TASK_OR_START_A_REVIEW, "Workflow Details Page: The description is not the expected one");
                        assertEquals(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getStartedBy(), getUserFullName(opUser), "Workflow Details Page: The workflow started by name is not the OP user");
                        assertEquals(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getDueDateString(), NONE, "Workflow Details Page: The date is different than NONE");
                        assertEquals(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getCompleted(), "<in progress>", "Workflow Details Page: The completed status is not in progress");
                        assertTrue(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getStartDate().isBeforeNow(), "Workflow Details Page: The start date is not the expected one");
                        assertEquals(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getPriority(), Priority.MEDIUM, "Workflow Details Page: The priority is not MEDIUM");
                        assertEquals(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getStatus(), WorkFlowStatus.WORKFLOW_IN_PROGRESS, "Workflow Details Page: The workflow status is not in progress");
                        assertEquals(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getMessage(), workFlowName, "Workflow Details Page: The message is not " + workFlowName);

                        // --- Step 3 ---
                        // --- Step action ---
                        // Verify 'More Info' section.
                        // --- Expected results ---
                        // The following data is displayed:
                        // Type: Cloud Review Task
                        // Destination: network.com
                        // After completion: specified value
                        // Lock on-premise content: specified value
                        // Assignment: user1 user1 (user1@network.com)

                        List<String> userNames = new ArrayList<String>();
                        userNames.add(cloudUser);

                        assertEquals(workFlowDetailsPage.getWorkFlowDetailsMoreInfo().getType(), TaskType.CLOUD_REVIEW_TASK, "Workflow Details Page: The task type is not ");
                        assertEquals(workFlowDetailsPage.getWorkFlowDetailsMoreInfo().getDestination(), DOMAIN_HYBRID, "Workflow Details Page: The destination is not the expected one " + DOMAIN_HYBRID);
                        assertEquals(workFlowDetailsPage.getWorkFlowDetailsMoreInfo().getAfterCompletion(), KeepContentStrategy.DELETECONTENT, "Workflow Details Page: After completion is not the expected one " + KeepContentStrategy.DELETECONTENT);
                        assertTrue(!workFlowDetailsPage.getWorkFlowDetailsMoreInfo().isLockOnPremise());
                        assertEquals(workFlowDetailsPage.getWorkFlowDetailsMoreInfo().getAssignmentList().size(), userNames.size());
                        assertTrue(
                                workFlowDetailsPage.getWorkFlowDetailsMoreInfo().getAssignmentList().contains(getUserFullNameWithEmail(cloudUser, cloudUser)), "Workflow Details Page: the assigned user is not the cloud user");

                        // --- Step 4 ---
                        // --- Step action ---
                        // Verify 'Items' section.
                        // --- Expected results ---
                        // The following item is displayed: test1.txt

                        List<WorkFlowDetailsItem> items = workFlowDetailsPage.getWorkFlowItems();
                        assertEquals(items.size(), 1);
                        assertEquals(items.get(0).getItemName(), fileName, "Workflow Details Page: the item name is not the expected one "+ fileName);

                        // --- Step 5 ---
                        // --- Step action ---
                        // Verify 'Current Tasks' section.
                        // --- Expected results ---
                        // The following data is displayed:
                        // No tasks

                        assertTrue(workFlowDetailsPage.isNoTasksMessageDisplayed(), "There are tasks displayed in Current Task section");

                        // --- Step 6 ---
                        // --- Step action ---
                        // Verify 'History' section.
                        // --- Expected results ---
                        // The following data is displayed:
                        // Start a task or review on Alfresco Cloud admin Thu 12 Sep 2013
                        // 17:15:07 Task Done

                        List<WorkFlowDetailsHistory> historyList = workFlowDetailsPage.getWorkFlowHistoryList();

                        assertEquals(historyList.size(), 1);
                        assertEquals(historyList.get(0).getType(), WorkFlowHistoryType.START_TASK_OR_REVIEW_ON_CLOUD, "Workflow History: The history list does not contain the workflow with type Start task or review on cloud");
                        assertEquals(historyList.get(0).getCompletedBy(), getUserFullName(opUser), "Workflow History: The user name is different than OP user");
                        assertEquals(historyList.get(0).getCompletedDate().toLocalDate(), getToDaysLocalDate(), "Workflow History: The completed date is not the current day");
                        assertEquals(historyList.get(0).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE, "Workflow History: The task is not marked as DONE");
                        assertEquals(historyList.get(0).getComment(), "", "Workflow History: The comment is not empty");

                        ShareUser.logout(hybridDrone);

                }
                catch (Throwable t)
                {
                        reportError(drone, testName + "-ENT", t);
                        reportError(hybridDrone, testName + "-ENT", t);
                }

        }

        @Test(groups = "DataPrepHybrid", timeOut = 500000)
        public void dataPrep_AONE_15621() throws Exception
        {

                testName = getTestName() + "T1";
                String cloudSite = getSiteName(testName + "CL");
                String opSite = getSiteName(testName + "OP");
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);
                String[] userInfo1 = new String[] { opUser };
                String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
                String[] cloudUserInfo1 = new String[] { cloudUser };

                String folderName = getFolderName(testName);
                String fileName = getFileName(testName) + "-15621" + ".txt";
                String[] fileInfo = { fileName, DOCLIB };

                String workFlowName = "Cloud Review Task test message" + testName + "-15621CL";

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
                ShareUser.logout(hybridDrone);

                ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
                ShareUser.openSiteDashboard(drone, opSite);
                ShareUser.uploadFileInFolder(drone, fileInfo).render();
                ShareUser.openSitesDocumentLibrary(drone, opSite).render();

                CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName).render();

                List<String> userNames = new ArrayList<String>();
                userNames.add(cloudUser);

                WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

                formDetails.setMessage(workFlowName);
                formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
                formDetails.setTaskPriority(Priority.MEDIUM);
                formDetails.setSiteName(cloudSite);
                formDetails.setReviewers(userNames);
                formDetails.setApprovalPercentage(100);
                formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

                // Create Workflow using File1
                cloudTaskOrReviewPage.startWorkflow(formDetails).render();

                ShareUser.logout(drone);

        }

        /**
         * AONE-15619:Cloud Review Task - Workflow Details (Cloud)
         */
        @Test(groups = "Hybrid", timeOut = 300000)
        public void AONE_15621() throws Exception
        {

                testName = getTestName() + "T1";
                String cloudSite = getSiteName(testName + "CL");
                String opSite = getSiteName(testName + "OP");
                String opUser = getUserNameForDomain(testName + "opUser", testDomain);

                String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
                String fileName = getFileName(testName) + "-15621" + ".txt";
                String workFlowName = "Cloud Review Task test message" + testName + "-15621CL";

                try
                {
                        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

                        // --- Step 1 ---
                        // --- Step action ---
                        // OP Verify the document, which is the part of the workflow.
                        // --- Expected results ---
                        // The document is successfully synced to Cloud. The correct
                        // destination is displayed.

                        DocumentLibraryPage documentLibraryPageOP = SiteUtil.openSiteDocumentLibraryURL(drone, opSite).render();

                        assertTrue(documentLibraryPageOP.isFileVisible(fileName), "Verifying " + fileName + " exists");
                        assertTrue(documentLibraryPageOP.getFileDirectoryInfo(fileName).isCloudSynced(), "Verifying the document is synced");
                        assertTrue(documentLibraryPageOP.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the document is part of workflow");

                        ShareUser.logout(drone);

                        // --- Step 2 ---
                        // --- Step action ---
                        // Cloud Verify the synced document.
                        // --- Expected results ---
                        // The document is successfully synced to Cloud. It is present in
                        // the correct destination.

                        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

                        DocumentLibraryPage documentLibraryPageCL = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();

                        assertTrue(documentLibraryPageCL.isFileVisible(fileName), "Verifying " + fileName + " exists");
                        assertTrue(documentLibraryPageCL.getFileDirectoryInfo(fileName).isCloudSynced(), "Verifying the document is synced");
                        assertTrue(documentLibraryPageCL.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the document is part of workflow");

                        ShareUser.logout(drone);

                }
                catch (Throwable t)
                {
                        reportError(drone, testName + "-ENT", t);
                        reportError(hybridDrone, testName + "-ENT", t);
                }
        }

}
