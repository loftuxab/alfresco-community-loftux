package org.alfresco.share.workflow.cloudReviewTask;

import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.task.EditTaskPage;
import org.alfresco.po.share.task.EditTaskPage.Button;
import org.alfresco.po.share.task.TaskDetailsPage;
import org.alfresco.po.share.task.TaskItem;
import org.alfresco.po.share.task.TaskStatus;
import org.alfresco.po.share.workflow.*;
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.api.CreateUserAPI;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class EditCloudReviewTaskTests extends AbstractWorkflow
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

    @Test(groups = "DataPrepHybrid", timeOut = 500000)
    public void dataPrep_AONE_15622() throws Exception
    {
        testName = getTestName() + "A";
        String cloudSite = getSiteName(testName + "CL");
        String opSite = getSiteName(testName + "OP");
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser };
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String fileName = getFileName(testName) + "-15622" + ".txt";
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

        String workFlowName = "Cloud Review Task test message" + testName + "-15622";

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
     * AONE-15622:Cloud Review Task - Edit Task Details (Cloud)
     */
    @Test(groups = "Hybrid", timeOut = 300000)
    public void AONE_15622() throws Exception
    {
        testName = getTestName() + "A";
        String cloudSite = getSiteName(testName + "CL");
        String opSite = getSiteName(testName + "OP");
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);

        String folderName = getFolderName(testName);
        String fileName = getFileName(testName) + "-15622" + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        String workFlowName = "Cloud Review Task test message" + testName + "-15622";
        fileName = getFileName(testName) + "-15622" + ".txt";

        try
        {

            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // --- Step 1 ---
            // --- Step action ---
            // Cloud Click on Edit button.
            // --- Expected results ---
            // The button is pressed. Edit Task page is opened.

            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).render();
            EditTaskPage editTaskPage = myTasksPage.navigateToEditTaskPage(workFlowName).render();
            Assert.assertTrue(editTaskPage.getTitle().contains("Edit Task"), "The Edit Task page was not opened");

            // --- Step 2 ---
            // --- Step action ---
            // Verify the available controls on Edit Task page.
            // --- Expected results ---
            // The following additional controls are present:
            // Status drop-down list
            // View More Actions button for the document
            // Comment field
            // Approve button
            // Reject button
            // Save and close button
            // Cancel button

            List<TaskStatus> statusOptions = editTaskPage.getStatusOptions();
            assertEquals(statusOptions.size(), TaskStatus.values().length);

            List<TaskItem> taskItems = editTaskPage.getTaskItem(fileName);
            assertTrue(taskItems.get(0).getViewMoreActionsLink().getDescription().contains("View More Actions"), "Edit Task Page: The View More Actions button is not displayed ");
            assertTrue(editTaskPage.isCommentTextAreaDisplayed(), "Edit Task Page: The comment area is not displayed");
            assertTrue(editTaskPage.isButtonsDisplayed(Button.APPROVE), "Edit Task Page: The Approve button is not displyed");
            assertTrue(editTaskPage.isButtonsDisplayed(Button.REJECT),  "Edit Task Page: The Reject button is not displyed");
            assertTrue(editTaskPage.isButtonsDisplayed(Button.SAVE_AND_CLOSE),  "Edit Task Page: The Save and close button is not displyed");
            assertTrue(editTaskPage.isButtonsDisplayed(Button.CANCEL),  "Edit Task Page: The Cancel button is not displyed");

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

            assertTrue(statusOptions.containsAll(getTaskStatusList()), "Not all statuses are available in Status drop-down list");

            // --- Step 4 ---
            // --- Step action ---
            // Specify any value in the Status drop-down list, e.g. 'In Progress'.
            // --- Expected results ---
            // Performed correctly.

            editTaskPage.selectStatusDropDown(TaskStatus.INPROGRESS);
            assertEquals(editTaskPage.getSelectedStatusFromDropDown(), TaskStatus.INPROGRESS, "The selected task status is not In Progress");

            // --- Step 5 ---
            // --- Step action ---
            // Add any data into the Comment field, e.g. "test comment".
            // --- Expected results ---
            // Performed correctly.
            editTaskPage.enterComment("test comment");
            assertEquals(editTaskPage.readCommentFromCommentTextArea(), "test comment");

            // --- Step 6 ---
            // --- Step action ---
            // Click on Cancel button.
            // --- Expected results ---
            // Edit Task page is closed. Task Details are displayed. No data was changed.
            // Comment: (None)
            myTasksPage = editTaskPage.selectCancelButton().render();

            TaskDetailsPage taskDetailsPage = myTasksPage.selectViewTasks(workFlowName).render();
            assertEquals(taskDetailsPage.getComment(), NONE, "The updates on Edit Task page were not cancelled");

            // --- Step 7 ---
            // --- Step action ---
            // Repeat steps 1-5.
            // --- Expected results ---
            // Performed correctly.

            taskDetailsPage.selectEditButton().render();
            editTaskPage.selectStatusDropDown(TaskStatus.INPROGRESS);
            editTaskPage.enterComment("test comment");
            assertEquals(editTaskPage.getSelectedStatusFromDropDown(), TaskStatus.INPROGRESS);
            assertEquals(editTaskPage.readCommentFromCommentTextArea(), "test comment");

            // --- Step 7 ---
            // --- Step action ---
            // Click on Save and Close button.
            // --- Expected results ---
            // Edit Task page is closed. Task Details are displayed. The specified data was changed.
            taskDetailsPage = editTaskPage.selectSaveButton().render();
            assertTrue(taskDetailsPage.isBrowserTitle("Task Details"));
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.INPROGRESS, "The status In Progress was not saved");
            assertEquals(taskDetailsPage.getComment(), "test comment", "The comment was not saved");

            ShareUser.logout(hybridDrone);

        }
        catch (Throwable t)
        {
            reportError(hybridDrone, testName + "-HY", t);
        }

    }

    @Test(groups = "DataPrepHybrid", timeOut = 500000)
    public void dataPrep_AONE_15623() throws Exception
    {

        testName = getTestName() + "A";
        String cloudSite = getSiteName(testName + "CL");
        String opSite = getSiteName(testName + "OP");
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser };
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String fileName = getFileName(testName) + "-15623" + ".txt";
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

        folderName = getFolderName(testName);
        fileName = getFileName(testName) + "-15623" + ".txt";
        String workFlowName = "Cloud Review Task test message" + testName + "-15623CL";

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
     * AONE-15623:Cloud Review Task - Edit Task Details (Cloud)
     */
    @Test(groups = "Hybrid", timeOut = 300000)
    public void AONE_15623() throws Exception
    {

        testName = getTestName() + "A";
        String cloudSite = getSiteName(testName + "CL");
        String opSite = getSiteName(testName + "OP");
        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);

        String folderName = getFolderName(testName);
        String fileName = getFileName(testName) + "-15623" + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        String workFlowName = "Cloud Review Task test message" + testName + "-15623CL";
        fileName = getFileName(testName) + "-15623" + ".txt";

        try
        {

            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // --- Step 1 ---
            // --- Step action ---
            // Cloud Click on Edit button.
            // --- Expected results ---
            // The button is pressed. Edit Task page is opened.

            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
            EditTaskPage editTaskPage = myTasksPage.navigateToEditTaskPage(workFlowName).render();
            Assert.assertTrue(editTaskPage.getTitle().contains("Edit Task"), "The Edit Task page was not rendered");

            // --- Step 2 ---
            // --- Step action ---
            // Try to add any item to the existing set.
            // --- Expected results ---
            // It is not possible to add any item to the task.

            Assert.assertFalse(editTaskPage.isButtonsDisplayed(Button.ADD), "The Add button is displayed in Edit Task Page");

            // --- Step 3 ---
            // --- Step action ---
            // Try to remove the existing item from the task.
            // --- Expected results ---
            // It is not possible to remove item from the task.

            Assert.assertFalse(editTaskPage.isButtonsDisplayed(Button.REMOVE_ALL), "The Remove button is displayed in Edit Task Page");

            ShareUser.logout(hybridDrone);

        }
        catch (Throwable t)
        {
            reportError(hybridDrone, testName + "-HY", t);
        }

    }

}
