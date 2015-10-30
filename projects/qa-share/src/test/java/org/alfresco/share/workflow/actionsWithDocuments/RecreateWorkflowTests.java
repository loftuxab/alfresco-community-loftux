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
package org.alfresco.share.workflow.actionsWithDocuments;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.SharePopup;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.SyncInfoPage;
import org.alfresco.po.share.task.TaskStatus;
import org.alfresco.po.share.workflow.CloudTaskOrReviewPage;
import org.alfresco.po.share.workflow.KeepContentStrategy;
import org.alfresco.po.share.workflow.Priority;
import org.alfresco.po.share.workflow.TaskType;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.EditTaskAction;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author bogdan.bocancea
 */

@Listeners(FailedTestListener.class)
public class RecreateWorkflowTests extends AbstractWorkflow
{
    private String testDomain;
    private static Log logger = LogFactory.getLog(RecreateWorkflowTests.class);
    String keepStrategy = "Keep";
    String removeSyncStrategy = "Remove";
    String deleteContentStrategy = "Delete";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testDomain = DOMAIN_HYBRID;
    }

    private void dataPrep_recreate(String testName, String strategy) throws Exception
    {
        logger.info("Start data prep for test: " + testName);

        String user1 = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { user1 };
        String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        String fileName = getFileName(testName) + ".txt";
        String workFlowName = testName + "-WF";
        String dueDate = getDueDateString();

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC).render();
        ShareUser.logout(hybridDrone);

        // Login as Enterprise user, create site and upload a file.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC).render();
        ShareUser.openSitesDocumentLibrary(drone, opSiteName).render().render();
        ShareUser.uploadFileInFolder(drone, new String[]{fileName, DOCLIB});

        // Select "Cloud Task or Review" from select a workflow drop down
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setAssignee(cloudUser);
        formDetails.setMessage(workFlowName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        if (strategy.equals(keepStrategy))
        {
            formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);
        }
        else if (strategy.equals(removeSyncStrategy))
        {
            formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENTREMOVESYNC);
        }
        else if (strategy.equals(deleteContentStrategy))
        {
            formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);
        }

        cloudTaskOrReviewPage.startWorkflow(formDetails).render(maxWaitTimeCloudSync);
        waitForSync(drone, fileName, opSiteName);
        completeWorkflow(workFlowName, user1, cloudUser);
    }

    /**
     * AONE-15715: Recreate Workflow - The document is already synced
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15715() throws Exception
    {
        String testName = getTestName() + System.currentTimeMillis();
        String user1 = getUserNameForDomain(testName + "opUser", testDomain);
        String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        String fileName = getFileName(testName) + ".txt";
        String workFlowName = testName + "-WF";
        String dueDate = getDueDateString();

        dataPrep_recreate(testName, keepStrategy);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

        // Select "Cloud Task or Review" from select a workflow drop down
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName).render();

        // --- Step 1 ---
        // OP Create another workflow with any data specified and with the same document attached
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);
        formDetails.setMessage(workFlowName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);

        // --- Expected Result ---
        // Workflow is not created. Friendly behavior occurs - 'Workflow could not be started' dialog: 08110558 One of the selected documents is already
        // syncronized with the Cloud. You can only use content that is not yet syncronized with the Cloud to start a new Hybrid Workflow.
        cloudTaskOrReviewPage.startWorkflow(formDetails);
        SharePopup errorPopup = new SharePopup(drone);
        Assert.assertTrue(
                errorPopup
                        .getShareMessage()
                        .contains(
                                "One of the selected documents is already syncronized with the Cloud. You can only use content that is not yet syncronized with the Cloud to start a new Hybrid Workflow."),
                "Incorrect message");

        ShareUser.logout(drone);

        // --- Step 3 ---
        // Cloud Verify the workflow and the document.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();

        // --- Expected result ----
        // The document is still synchronized. The document is not a part of any workflow. No workflow is created
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isCloudSynced(), "File is not synced");
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "File is not part of workflow");
        ShareUser.logout(hybridDrone);
    }

    /**
     * AONE-15716:Recreate Workflow - The document exists in Cloud
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15716() throws Exception
    {
        String testName = getTestName() + System.currentTimeMillis();
        String user1 = getUserNameForDomain(testName + "opUser", testDomain);
        String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        String fileName = getFileName(testName) + ".txt";
        String workFlowName = testName + "-WF";
        String dueDate = getDueDateString();

        dataPrep_recreate(testName, removeSyncStrategy);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

        // Select "Cloud Task or Review" from select a workflow drop down
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName).render();

        // --- Step 1 ---
        // OP Create another workflow with any data specified and with the same document attached
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);
        formDetails.setMessage(workFlowName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);

        // --- Expected Result ---
        // Workflow is not created. Friendly behavior occurs - 'Workflow could not be started' dialog: 08110558 The document with the same name already exists
        // in Cloud.
        // TODO: Modify step in Test link. Workflow is created, wait a few seconds, the sync fails with error: The document with the same name already exists
        // in Cloud.
        cloudTaskOrReviewPage.startWorkflow(formDetails).render();
        Assert.assertTrue(checkIfSyncFailed(drone, fileName), "File is synced");
        SyncInfoPage syncInfoPage = docLibPage.getFileDirectoryInfo(fileName).clickOnViewCloudSyncInfo().render();
        syncInfoPage.clickShowDetails();
        Assert.assertTrue(syncInfoPage.getSyncFailedErrorDetail().equals("Content with the same name already exists in the target folder."));
        Assert.assertTrue(syncInfoPage.getTechnicalReport().contains("Content with the same name already exists in the target folder."));
        ShareUser.logout(drone);

        // --- Step 3 ---
        // Cloud Verify the workflow and the document.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        docLibPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();

        // --- Expected result ----
        // The document is not synchronized. The document is not a part of any workflow. No workflow is created.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isCloudSynced(), "File is synced");
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "File is part of workflow");
        ShareUser.logout(hybridDrone);
    }

    /**
     * AONE-15717:Recreate Workflow - The document was removed from Cloud
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15717() throws Exception
    {
        String testName = getTestName() + System.currentTimeMillis();
        String user1 = getUserNameForDomain(testName + "opUser", testDomain);
        String cloudUser = getUserNameForDomain(testName + "clUser", testDomain);
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        String fileName = getFileName(testName) + ".txt";
        String workFlowName = testName + "-WF";
        String dueDate = getDueDateString();

        dataPrep_recreate(testName, deleteContentStrategy);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

        // Select "Cloud Task or Review" from select a workflow drop down
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName).render();

        // --- Step 1 ---
        // OP Create another workflow with any data specified, with the same destination chosen and with the same document attached.
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);
        formDetails.setMessage(workFlowName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        cloudTaskOrReviewPage.startWorkflow(formDetails).render(maxWaitTimeCloudSync);
        waitForSync(drone, fileName, opSiteName);
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isCloudSynced(), "File is not synced");

        ShareUser.logout(drone);

        // --- Step 3 ---
        // Cloud Verify the workflow and the document.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        DocumentLibraryPage docLibPageCloud = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();

        // --- Expected result ----
        // The document is created in Cloud. The document is a part of a newly created workflow. The workflow is created successfully.
        Assert.assertTrue(docLibPageCloud.getFileDirectoryInfo(fileName).isCloudSynced(), "File is not synced");
        Assert.assertTrue(docLibPageCloud.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "File is not part of workflow");
        ShareUser.logout(hybridDrone);
    }

    private void completeWorkflow(String workflowName, String opUser, String clUser)
    {
        // login as cloud user and complete workflow
        SharePage sharePage = ShareUser.login(hybridDrone, clUser, DEFAULT_PASSWORD);
        sharePage.getNav().selectMyTasks().render();
        ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, workflowName, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE).render();
        ShareUser.logout(hybridDrone);

        // Login as OP user and complete workflow
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(drone).render();
        ShareUser.checkIfTaskIsPresent(drone, workflowName);
        ShareUserWorkFlow.completeWorkFlow(drone, opUser, workflowName).render();
        ShareUser.logout(drone);
    }
}
