/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.share.workflow;

import static org.alfresco.po.share.task.EditTaskPage.Button.REASSIGN;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.task.EditTaskPage;
import org.alfresco.po.share.task.TaskDetails;
import org.alfresco.po.share.task.TaskDetailsPage;
import org.alfresco.po.share.task.TaskStatus;
import org.alfresco.po.share.workflow.CloudTaskOrReviewPage;
import org.alfresco.po.share.workflow.KeepContentStrategy;
import org.alfresco.po.share.workflow.MyWorkFlowsPage;
import org.alfresco.po.share.workflow.Priority;
import org.alfresco.po.share.workflow.TaskDetailsType;
import org.alfresco.po.share.workflow.TaskHistoryPage;
import org.alfresco.po.share.workflow.TaskType;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.test.FailedTestListener;
import org.alfresco.webdrone.WebDrone;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Dmitry Yukhnovets
 */
@Listeners(FailedTestListener.class)
public class WorkFlowActionsTest1 extends AbstractWorkflow
{
    private static final Logger logger = Logger.getLogger(WorkFlowActionsTest1.class);
    private String testDomain;
    private String opUser;
    private String cloudSite;
    private String opSite;
    private String fileName;
    private String workFlowName;
    private String[] fileInfo;

    /**
     * Class includes: Tests from TestLink in Area: WorkFlowActionTests
     * It was added to separate test class - because WorkFlowActionTests.class uses BeforeClass for creation Test Data
     */
    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
    }

    private void identifyTestData(String testID)
    {
        testName = testID;
        opSite = getSiteName(testName + "OP" + "X14");
        cloudSite = getSiteName(testName + "CL" + "X14");
        testDomain = DOMAIN_HYBRID;
        opUser = getUserNameForDomain(testName + "opUser", testDomain);
        cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
        fileName = getFileName(testName) + "" + "X14" + ".txt";
        workFlowName = "Simple Cloud Task " + testName;
        fileInfo = new String[] { fileName, DOCLIB };
    }

    private void generateCommonTestData(String testName) throws Exception
    {
        opSite = getSiteName(testName + "OP" + "X14");
        cloudSite = getSiteName(testName + "CL" + "X14");
        testDomain = DOMAIN_HYBRID;
        opUser = getUserNameForDomain(testName + "opUser", testDomain);
        cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
        fileName = getFileName(testName) + "" + "X14" + ".txt";
        workFlowName = "Simple Cloud Task " + testName;
        fileInfo = new String[] { fileName, DOCLIB };


        // create users with unique data for each test case
        String[] userInfo1 = new String[] { opUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        String[] cloudUserInfo1 = new String[] { cloudUser };
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to Share, set up the cloud sync
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        // Create Site on Cloud for sync
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15671() throws Exception
    {
        String testName = getTestName() + System.currentTimeMillis();
        generateCommonTestData(testName);

        try
        {

            // Create workflow (it should be executed from test because workflow should be canceled.)
            ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
            ShareUser.openSiteDashboard(drone, opSite);

            ShareUser.uploadFileInFolder(drone, fileInfo).render();
            ShareUser.openSitesDocumentLibrary(drone, opSite).render();
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
            ShareUser.logout(drone);

            ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

            // --- Step 1 ---
            // --- Step action ---
            // OP Perform Cancel Workflow action
            // --- Expected results ---
            // The workflow is canceled

            MyWorkFlowsPage myWorkflowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone).render();
            myWorkflowsPage.cancelWorkFlow(workFlowName).render();

            // --- Step 2 ---
            // --- Step action ---
            // OP Verify the workflow and the task
            // --- Expected results ---
            // The workflow disappeared

            Assert.assertFalse(myWorkflowsPage.isWorkFlowPresent(workFlowName), "OP: Workflow is presented after cancelling.");

            // --- Step 3 ---
            // --- Step action ---
            // Cloud Verify the workflow and the task
            // --- Expected results ---
            // The task disappeared for the assignee Tasks list. The workflow disappeared from Cloud

            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).render();
            Assert.assertFalse(myTasksPage.isTaskPresent(workFlowName), "Cloud: Task is presented after cancelling.");

            // --- Step 4 ---
            // --- Step action ---
            // Cloud Verify the synced document
            // --- Expected results ---
            // Action, which was specified in After Completion drop-down, is performed

            ShareUser.openSiteDashboard(hybridDrone, cloudSite);
            DocumentLibraryPage documentLibPage = ShareUser.openDocumentLibrary(hybridDrone).render();
            Assert.assertTrue(documentLibPage.isItemVisble(fileName), "Cloud: File has been removed during cancelling workflow");

        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
        }

        ShareUser.logout(drone);
    }

    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15673() throws Exception
    {
        String testName = getTestName() + System.currentTimeMillis();
        generateCommonTestData(testName);

        try
        {
            // Create workflow (it should be executed from test because workflow should be canceled.)
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
            formDetails.setApprovalPercentage(20);
            formDetails.setTaskPriority(Priority.MEDIUM);
            formDetails.setSiteName(cloudSite);
            formDetails.setReviewers(userNames);
            formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);
            formDetails.setLockOnPremise(false);

            // Create Workflow using File1
            cloudTaskOrReviewPage.startWorkflow(formDetails).render();
            ShareUser.logout(drone);

            ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

            // --- Step 1 ---
            // --- Step action ---
            // OP Perform Cancel Workflow action
            // --- Expected results ---
            // The workflow is canceled

            MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone).render();
            myWorkFlowsPage.cancelWorkFlow(workFlowName);

            // --- Step 2 ---
            // --- Step action ---
            // OP Verify the workflow and the task
            // --- Expected results ---
            // The workflow disappeared

            drone.waitForPageLoad(5);
            assertFalse(myWorkFlowsPage.isWorkFlowPresent(workFlowName), "OP: Workflow was not canceled.");
            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone).render();
            assertFalse(myTasksPage.isTaskPresent(workFlowName), "OP: Task was not canceled");

            // --- Step 3 ---
            // --- Step action ---
            // Cloud Verify the workflow and the task
            // --- Expected results ---
            // The task disappeared for the assignee Tasks list. The workflow disappeared from Cloud

            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).render();
            Assert.assertFalse(myTasksPage.isTaskPresent(workFlowName), "Cloud: Task was not canceled.");

            // --- Step 4 ---
            // --- Step action ---
            // Cloud Verify the synced document
            // --- Expected results ---
            // Action, which was specified in After Completion drop-down, is performed

            ShareUser.openSiteDashboard(hybridDrone, cloudSite);
            ShareUser.openDocumentLibrary(hybridDrone).render();
            Assert.assertFalse(myTasksPage.isTaskPresent(workFlowName), "Cloud: Content was not removed after cancelling workflow");
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
        }

        ShareUser.logout(drone);
    }

    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15674() throws Exception
    {
        String testName = getTestName() + System.currentTimeMillis();
        generateCommonTestData(testName);

        try
        {
            // Create workflow (it should be executed from test because workflow should be canceled.)
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
            formDetails.setApprovalPercentage(20);
            formDetails.setTaskPriority(Priority.MEDIUM);
            formDetails.setSiteName(cloudSite);
            formDetails.setReviewers(userNames);
            formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);
            formDetails.setLockOnPremise(false);

            // Create Workflow using File1
            cloudTaskOrReviewPage.startWorkflow(formDetails).render();
            ShareUser.openSitesDocumentLibrary(drone, opSite).render();
            waitForSync(drone, fileName, siteName);
            ShareUser.logout(drone);

            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // --- Step 1 ---
            // --- Step action ---
            // Cloud Perform Cancel Workflow action
            // --- Expected results ---
            // The workflow is canceled

            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).render();
            myTasksPage.selectActiveTasks().renderTask(maxWaitTime, workFlowName);

            findTasks(hybridDrone, workFlowName);
            TaskHistoryPage taskHistoryPage = myTasksPage.selectTaskHistory(workFlowName).render();

            taskHistoryPage.selectCancelWorkFlow().render();
            hybridDrone.waitForPageLoad(5);

            // --- Step 2 ---
            // --- Step action ---
            // Cloud Verify the workflow and the task
            // --- Expected results ---
            // The task disappeared for the assignee Tasks list. The workflow disappeared from Cloud

            Assert.assertFalse(myTasksPage.isTaskPresent(workFlowName), " Cloud: Task is not canceled");

            // --- Step 3 ---
            // --- Step action ---
            // OP Verify the workflow and the task
            // --- Expected results ---
            // The workflow is still active. A task with type "WorkFlow cancelled on the cloud" is received

            ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

            myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);

            assertTrue(ShareUser.checkIfTaskIsPresent(drone, workFlowName), "OP: Workflow is canceled");

            TaskDetails taskDetails = myTasksPage.getTaskDetails(workFlowName);

            assertEquals(taskDetails.getType(), TaskDetailsType.WORKFLOW_CANCELLED_ON_THE_CLOUD, "OP: Info about cancelling task on Cloud is not displayed.");

            // --- Step 4 ---
            // --- Step action ---
            // Cloud Verify the synced document
            // --- Expected results ---
            // The document is still synced

            DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSite).render();
            assertTrue(documentLibraryPage.isFileVisible(fileName), "Cloud: Verifying " + fileName + " exists");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Cloud: Verifying the document is synced");

            // --- Step 5 ---
            // --- Step action ---
            // OP Verify the synced document
            // --- Expected results ---
            // The document is still synced

            ShareUser.openSiteDashboard(drone, opSite);
            DocumentLibraryPage documentLibraryPageOP = ShareUser.openSitesDocumentLibrary(drone, opSite).render();
            assertTrue(documentLibraryPageOP.isFileVisible(fileName), "OP: Verifying " + fileName + " exists");
            assertTrue(documentLibraryPageOP.getFileDirectoryInfo(fileName).isCloudSynced(), "OP: Verifying the document is synced");

        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
        }
        ShareUser.logout(drone);
    }

    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15675() throws Exception
    {
        String testName = getTestName() + System.currentTimeMillis();
        generateCommonTestData(testName);

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, opSite);
        ShareUser.uploadFileInFolder(drone, fileInfo).render();
        ShareUser.openSitesDocumentLibrary(drone, opSite).render();
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

        ShareUser.openSitesDocumentLibrary(drone, opSite).render();
        waitForSync(drone, fileName, siteName);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        MyTasksPage myTasksPage11 = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).render();

        findTasks(hybridDrone, workFlowName);
        TaskDetailsPage taskDetailsPage = myTasksPage11.selectViewTasks(workFlowName).render();
        EditTaskPage editTaskPage = taskDetailsPage.selectEditButton().render();

        editTaskPage.selectStatusDropDown(TaskStatus.COMPLETED);
        editTaskPage.selectTaskDoneButton().render();
        ShareUser.logout(hybridDrone);

        try
        {
            ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

            // --- Step 1 ---
            // --- Step action ---
            // OP Reassign the received task to any other user
            // --- Expected results ---
            // Impossible to reassign task. No Reassign action is available

            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone).render();
            checkIfTaskIsPresent(drone,workFlowName , true);
            myTasksPage.selectActiveTasks().renderTask(maxWaitTimeCloudSync, workFlowName);
            myTasksPage.selectViewWorkflow(workFlowName).render();
            editTaskPage = new EditTaskPage(hybridDrone);
            Assert.assertFalse(editTaskPage.isButtonsDisplayed(REASSIGN), "Button REASSIGN don't display on editTaskPage.");
        }

        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
        }

        ShareUser.logout(drone);
    }


    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15676() throws Exception
    {
        String testName = getTestName() + System.currentTimeMillis();
        generateCommonTestData(testName);

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, opSite);
        ShareUser.uploadFileInFolder(drone, fileInfo).render();
        ShareUser.openSitesDocumentLibrary(drone, opSite).render();
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
        ShareUser.openSitesDocumentLibrary(drone, opSite).render();
        waitForSync(drone, fileName, siteName);
        ShareUser.logout(drone);

        try
        {
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            // --- Step 1 ---
            // --- Step action ---
            // Cloud Reassign the received task to any other user
            // --- Expected results ---
            // Impossible to reassign task. No Reassign action is available

            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).render();
            myTasksPage.renderTask(maxWaitTime, workFlowName);
            EditTaskPage editTaskPage = myTasksPage.navigateToEditTaskPage(workFlowName);
            Assert.assertFalse(editTaskPage.isReAssignButtonDisplayed(), "Button REASSIGN is displayed on editTaskPage.");
            // myTasksPage.selectActiveTasks().renderTask(maxWaitTime, workFlowName);
            // myTasksPage.selectViewWorkflow(workFlowName).render();
            // Assert.assertFalse(drone.isElementDisplayed(By.xpath("//*[contains(@class,'task-edit')]")));
            // // EditTaskPage editTaskPage = myTasksPage.navigateToEditTaskPage(newWorkFlow.getMessage());
            // EditTaskPage editTaskPage1 = new EditTaskPage(hybridDrone);
            // Assert.assertFalse(editTaskPage1.isButtonsDisplayed(REASSIGN),
            // "Button REASSIGN don't display on editTaskPage.");
        }

        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
        }

        ShareUser.logout(drone);
    }

    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15677() throws Exception
    {
        String testName = getTestName() + System.currentTimeMillis();
        generateCommonTestData(testName);

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
        formDetails.setApprovalPercentage(20);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setReviewers(userNames);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);
        formDetails.setLockOnPremise(false);

        // Create Workflow using File1
        cloudTaskOrReviewPage.startWorkflow(formDetails).render();
        ShareUser.openSitesDocumentLibrary(drone, opSite).render();
        waitForSync(drone, fileName, siteName);

        ShareUser.logout(drone);

        try
        {
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // --- Step 1 ---
            // --- Step action ---
            // Cloud Reassign the received task to any other user
            // --- Expected results ---
            // Impossible to reassign task. No Reassign action is available
            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).render();
            myTasksPage.renderTask(maxWaitTime, workFlowName);
            EditTaskPage editTaskPage = myTasksPage.navigateToEditTaskPage(workFlowName);
            Assert.assertFalse(editTaskPage.isReAssignButtonDisplayed(), "Button REASSIGN is displayed on editTaskPage.");

        }

        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
        }

        ShareUser.logout(drone);
    }

    private void findTasks(WebDrone driver, String workFlowName)
    {

        assertTrue(driver.findAndWaitWithRefresh(By.xpath(String.format("//a[text()='%s']", workFlowName))).isDisplayed());

    }
}
