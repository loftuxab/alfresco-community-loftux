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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.dashlet.MyTasksDashlet;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.steps.SiteActions;
import org.alfresco.po.share.task.EditTaskPage;
import org.alfresco.po.share.task.TaskDetails;
import org.alfresco.po.share.task.TaskDetailsPage;
import org.alfresco.po.share.task.TaskInfo;
import org.alfresco.po.share.task.TaskStatus;
import org.alfresco.po.share.user.Language;
import org.alfresco.po.share.workflow.AssignmentPage;
import org.alfresco.po.share.workflow.CloudTaskOrReviewPage;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.po.share.workflow.KeepContentStrategy;
import org.alfresco.po.share.workflow.MyWorkFlowsPage;
import org.alfresco.po.share.workflow.Priority;
import org.alfresco.po.share.workflow.TaskHistoryPage;
import org.alfresco.po.share.workflow.TaskType;
import org.alfresco.po.share.workflow.ViewWorkflowPage;
import org.alfresco.po.share.workflow.WorkFlowDetailsCurrentTask;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.EditTaskAction;
import org.alfresco.share.util.PropertiesUtil;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.share.workflow.actionsWithDocuments.RecreateWorkflowTests;
import org.alfresco.test.FailedTestListener;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;

@Listeners(FailedTestListener.class)
public class AdvancedScenariosWorkflowTests extends AbstractWorkflow
{
    private String testDomain;
    private String trialDomain1 = "trial1.test";
    private String partnerDomain1 = "partner1.test";
    private String invitedDomain1 = "invited1.test";
    private static Log logger = LogFactory.getLog(RecreateWorkflowTests.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testDomain = DOMAIN_HYBRID;
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15718() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain("u1" + testName, testDomain);
        String cloudUser2 = getUserNameForDomain("u2" + testName, testDomain);
        String cloudSite1Name = getSiteName(testName + "cl");
        String[] userInfo1 = new String[] { opUser1 };
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String[] cloudUserInfo2 = new String[] { cloudUser2 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Create User2 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo2);

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite1Name, SITE_VISIBILITY_PUBLIC);

        // Inviting user2 as consumer to the site.
        CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, cloudUser1, cloudUser2, siteActions.getSiteShortname(cloudSite1Name), "SiteConsumer", "");

        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);
    }

    /**
     * AONE-15718:Reviewer/Assignee has no write permissions to the folder
     */
    @Test(groups = "Hybrid", timeOut = 500000)
    public void AONE_15718() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain("u1" + testName, testDomain);
        String cloudUser2 = getUserNameForDomain("u2" + testName, testDomain);
        String cloudSite1Name = getSiteName(testName + "cl");
        String opSiteName = getSiteName(testName) + System.currentTimeMillis();
        String opFileName = getFileName(testName) + System.currentTimeMillis();
        String workFlowName1 = testName + System.currentTimeMillis();

        List<String> userNames = new ArrayList<String>();
        String[] opFileInfo = new String[] { opFileName };

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC).render();

        // Open Document library, Upload a file
        ShareUser.openDocumentLibrary(drone).render();
        ShareUser.uploadFileInFolder(drone, opFileInfo).render();

        // Start Cloud Task or Review workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, opFileName).render();

        // Adding reviewers
        userNames.add(cloudUser2);
        userNames.add(cloudUser1);
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setMessage(workFlowName1);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite1Name);
        formDetails.setApprovalPercentage(50);
        formDetails.setReviewers(userNames);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails).render();
        ShareUser.logout(drone);

        // TODO: Please update last step in test link accordingly to next assertions
        // TODO: verify ALF-20139
        // TODO: Add step in test link: Cloud user2 login and verifies the task is present or not
        ShareUser.login(hybridDrone, cloudUser2, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName1), "Task is not displayed");
        ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSite1Name);
        DocumentDetailsPage detailsPage = ShareUser.openDocumentDetailPage(hybridDrone, opFileName).render();

        // TODO: Add step: Verify the cloud user is not having the edit options on sync document as he is the consumer on this content.
        Assert.assertFalse(detailsPage.isEditOfflineLinkDisplayed(), "Edit offline is displayed");
        Assert.assertFalse(detailsPage.isUploadNewVersionDisplayed(), "Upload new version is displayed");
        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15720() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName, trialDomain1);
        String cloudSite1Name = getSiteName(testName + "cl1");
        String[] userInfo1 = new String[] { opUser1 };
        String[] cloudUserInfo1 = new String[] { cloudUser1 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, trialDomain1, "1001");

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite1Name, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);

        ShareUser.logout(drone);
    }

    /**
     * AONE-15720:Cloud Trial Standard Network - Start Workflow
     */
    @Test(groups = "Hybrid", timeOut = 500000)
    public void AONE_15720() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName, trialDomain1);
        String cloudSite1Name = getSiteName(testName + "cl1");
        String opSiteName = getSiteName(testName) + System.currentTimeMillis();
        String opFileName = getFileName(testName) + System.currentTimeMillis();
        String workFlowName1 = testName + System.currentTimeMillis();

        List<String> userNames = new ArrayList<String>();
        String[] opFileInfo = new String[] { opFileName };

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        ShareUser.openDocumentLibrary(drone);
        ShareUser.uploadFileInFolder(drone, opFileInfo).render();

        // Start Cloud Task or Review workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startCloudReviewTaskWorkFlow(drone).render();

        // Adding reviewers
        userNames.add(cloudUser1);
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setMessage(workFlowName1);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite1Name);
        formDetails.setReviewers(userNames);
        formDetails.setApprovalPercentage(50);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(opFileName, opSiteName);

        // Fill the form details and start workflow
        MyWorkFlowsPage myWorkFlowsPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();
        Assert.assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName1), "Workflow is not displayed");
        ShareUser.logout(drone);
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15721() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName, partnerDomain1);
        String cloudSite1Name = getSiteName(testName + "cl1");
        String[] userInfo1 = new String[] { opUser1 };
        String[] cloudUserInfo1 = new String[] { cloudUser1 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Any Cloud user with cloud partner network is created and activated, e.g. user1@partnernetwork.com
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, partnerDomain1, "101");

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite1Name, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);
    }

    /**
     * AONE-15721:Partner Cloud account - Start Workflow
     */
    @Test(groups = "Hybrid", timeOut = 500000)
    public void AONE_15721() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName, partnerDomain1);
        String cloudSite1Name = getSiteName(testName + "cl1");
        String opSiteName = getSiteName(testName) + System.currentTimeMillis();
        String opFileName = getFileName(testName) + System.currentTimeMillis();
        String workFlowName1 = testName + System.currentTimeMillis();

        List<String> userNames = new ArrayList<String>();
        String[] opFileInfo = new String[] { opFileName };

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        ShareUser.openDocumentLibrary(drone);
        ShareUser.uploadFileInFolder(drone, opFileInfo).render();

        // Start Cloud Task or Review workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startCloudReviewTaskWorkFlow(drone).render();

        // Adding reviewers
        userNames.add(cloudUser1);
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setMessage(workFlowName1);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite1Name);
        formDetails.setReviewers(userNames);
        formDetails.setApprovalPercentage(50);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(opFileName, opSiteName);

        // Fill the form details and start workflow
        MyWorkFlowsPage myWorkFlowsPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();
        Assert.assertNotNull(myWorkFlowsPage.isWorkFlowPresent(workFlowName1), "Workflow is not displyed");
        ShareUser.logout(drone);
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15722() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { opUser1 };
        String trailDomainName = "trial11.test";
        String cloudUser1 = getUserNameForDomain(testName, trailDomainName);
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String cloudSite1Name = getSiteName(testName + "cl1");
        String opSiteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";
        String workFlowName = testName + "_workflowName";
        String dueDate = getDueDateString();

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Creating trial domain.
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, trailDomainName, "1001");

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD).render();

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite1Name, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        ShareUser.openDocumentLibrary(drone);
        ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });

        // Select "Cloud Task or Review" from select a workflow drop down
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName).render();

        // --- Step 1 ---
        // OP Create another workflow with any data specified, with the same destination chosen and with the same document attached.
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite1Name);
        formDetails.setAssignee(cloudUser1);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);
        formDetails.setMessage(workFlowName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);

        cloudTaskOrReviewPage.startWorkflow(formDetails).render(maxWaitTimeCloudSync);
        isSynced(fileName, opSiteName);
    }

    /**
     * AONE-15722: Downgrade Cloud account - Incomplete Workflow
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 600000)
    public void AONE_15722() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String trailDomainName = "trial11.test";
        String cloudUser1 = getUserNameForDomain(testName, trailDomainName);
        String cloudSite1Name = getSiteName(testName + "cl1");
        String opSiteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";
        String workFlowName = testName + "_workflowName";
        String modifiedTitle = testName + "modifiedBy ";
        String comment = "Cloud_comment" + ShareUser.getRandomStringWithNumders(4);

        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Step 1: OP Perform any changes to the synced document.
        EditDocumentPropertiesPage editDocumentProperties = ShareUserSitePage.getEditPropertiesFromDocLibPage(drone, opSiteName, fileName).render();
        editDocumentProperties.setDocumentTitle(modifiedTitle + opUser1);
        editDocumentProperties.selectSave().render();

        editDocumentProperties = ShareUserSitePage.getEditPropertiesFromDocLibPage(drone, opSiteName, fileName).render(maxWaitTime);
        Assert.assertTrue((modifiedTitle + opUser1).equals(editDocumentProperties.getDocumentTitle()),
                "Document Title modified by OP User is not present for Cloud.");
        editDocumentProperties.selectCancel();
        Assert.assertTrue(isSynced(fileName, opSiteName), "Document is not synced");
        ShareUser.logout(drone);

        // login cloud user
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Step 2: Cloud Perform any changes to the synced document.
        EditDocumentPropertiesPage editDocumentPropertiescl = ShareUserSitePage.getEditPropertiesFromDocLibPage(hybridDrone, cloudSite1Name, fileName);
        editDocumentPropertiescl.setDocumentTitle(modifiedTitle + cloudUser1);
        editDocumentPropertiescl.selectSave().render();

        DocumentLibraryPage cldocumentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSite1Name).render();
        cldocumentLibraryPage.selectFile(fileName).render();
        editDocumentPropertiescl = ShareUserSitePage.getEditPropertiesFromDocLibPage(hybridDrone, cloudSite1Name, fileName);
        Assert.assertTrue((modifiedTitle + cloudUser1).equals(editDocumentPropertiescl.getDocumentTitle()),
                "Document Title modified by CL User is not present for Cloud.");

        // Step 3: Perform any changes against the workflow, e.g. add a comment to the task and save them.s
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).render();
        EditTaskPage editTaskPage = myTasksPage.navigateToEditTaskPage(workFlowName, cloudUser1).render();
        editTaskPage.enterComment(comment);
        editTaskPage.selectSaveButton().render();

        // Step 4: Downgrade Cloud account to free network
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, trailDomainName, "0");

        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Step 5: OP Perform any changes to the synced document.
        editDocumentProperties = ShareUserSitePage.getEditPropertiesFromDocLibPage(drone, opSiteName, fileName).render();
        editDocumentProperties.setDocumentTitle(modifiedTitle + opUser1 + "secondEdit");
        editDocumentProperties.selectSave().render();

        // The changes are applied but cannot be synced.
        Assert.assertTrue(verifySyncFailed(fileName, opSiteName), "Sync is not failed");
        ShareUser.logout(drone);

        // login cloud user
        // Step 6: Cloud Perform any changes to the synced document.
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        editDocumentPropertiescl = ShareUserSitePage.getEditPropertiesFromDocLibPage(hybridDrone, cloudSite1Name, fileName);
        editDocumentPropertiescl.setDocumentTitle(modifiedTitle + cloudUser1 + "edit");
        editDocumentPropertiescl.selectSave().render();

        // The changes are applied
        editDocumentPropertiescl = ShareUserSitePage.getEditPropertiesFromDocLibPage(hybridDrone, cloudSite1Name, fileName).render(maxWaitTime);
        Assert.assertTrue((modifiedTitle + cloudUser1 + "edit").equals(editDocumentPropertiescl.getDocumentTitle()),
                "Document Title modified by OP User is not present for Cloud.");
        editDocumentPropertiescl.selectCancel();

        // but cannot be synced.
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        ShareUser.openSiteDocumentLibraryFromSearch(drone, opSiteName).render();
        Assert.assertTrue(verifySyncFailed(fileName, opSiteName), "Sync is not failed");
        ShareUser.logout(drone);

        // Step 7: Cloud Verify the created in the pre-condition workflow on Tasks I've Started page.
        // TODO: remove this step from Test Link because option: Tasks I've Started page isn't available for this user
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Step 8: Verify the task on My Tasks page
        MyTasksPage myTasks = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(myTasks.isTaskPresent(workFlowName), "Task is missing");

        String weekDay;
        SimpleDateFormat dayFormat = new SimpleDateFormat("d MMM, yyyy", Locale.US);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 2);
        weekDay = dayFormat.format(calendar.getTime());

        // Step 9: Verify the task details page.
        TaskDetailsPage taskDetails = myTasks.selectViewTasks(workFlowName);
        TaskInfo taskDetailsInfo = taskDetails.getTaskDetailsInfo();
        Assert.assertTrue(taskDetailsInfo.getDueDateString().equals(weekDay), "Due date is incorrect");
        Assert.assertTrue(taskDetailsInfo.getPriority().equals(Priority.MEDIUM), "Priority is not set to medium");

        // Step 10: Verify the task in My Tasks dashlet on User Dashboard page
        DashBoardPage dashBoard = ShareUser.openUserDashboard(hybridDrone).render();
        MyTasksDashlet taskDashlet = dashBoard.getDashlet("tasks").render();

        List<ShareLink> tasks = taskDashlet.getTasks();
        String theTask = tasks.get(0).getDescription();
        Assert.assertTrue(theTask.contains(workFlowName), "Task " + workFlowName + " is missing");

        // Step 11: Try to complete workflow in Cloud
        myTasks = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        myTasks.navigateToEditTaskPage(workFlowName).render();
        ShareUserWorkFlow.completeTask(hybridDrone, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE).render();

        // Workflow cannot be completed or another friendly behavior occurs.
        // TODO: BUG ALF-20442
        Assert.assertTrue(myTasks.isTaskPresent(workFlowName), "Task is missing");
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15723() throws Exception
    {
        String testName = getTestName() + "ts14";
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { opUser1 };
        String trailDomainName = "trial15723.test";
        String cloudUser1 = getUserNameForDomain(testName, trailDomainName);
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String cloudSite1Name = getSiteName(testName + "cl1");
        String opSiteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";
        String workFlowName = testName + "_workflowName";
        String dueDate = getDueDateString();

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Creating trial domain.
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, trailDomainName, "1001");

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD).render();

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite1Name, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        ShareUser.openDocumentLibrary(drone);
        ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });

        // Select "Cloud Task or Review" from select a workflow drop down
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName).render();

        // --- Step 1 ---
        // OP Create another workflow with any data specified, with the same destination chosen and with the same document attached.
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite1Name);
        formDetails.setAssignee(cloudUser1);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);
        formDetails.setMessage(workFlowName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        cloudTaskOrReviewPage.startWorkflow(formDetails).render(maxWaitTimeCloudSync);
        isSynced(fileName, opSiteName);
    }

    /**
     * AONE-15723: Upgrade Cloud account - Incomplete Workflow
     */
    @Test(groups = "Hybrid", enabled = true, timeOut = 400000)
    public void AONE_15723() throws Exception
    {
        String testName = getTestName() + "ts14";
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String trailDomainName = "trial15723.test";
        String cloudUser1 = getUserNameForDomain(testName, trailDomainName);
        String opSiteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";
        String workFlowName = testName + "_workflowName";
        String modifiedTitle = testName + "modifiedBy ";

        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Perform any changes to the synced document.
        EditDocumentPropertiesPage editDocumentProperties = ShareUserSitePage.getEditPropertiesFromDocLibPage(drone, opSiteName, fileName).render();
        editDocumentProperties.setDocumentTitle(modifiedTitle + opUser1);
        editDocumentProperties.selectSave().render();

        // The changes are applied and synced successfully.
        editDocumentProperties = ShareUserSitePage.getEditPropertiesFromDocLibPage(drone, opSiteName, fileName).render(maxWaitTime);
        Assert.assertTrue((modifiedTitle + opUser1).equals(editDocumentProperties.getDocumentTitle()),
                "Document Title modified by OP User is not present for Cloud.");
        editDocumentProperties.selectCancel();

        // Step 2: Upgrade Cloud account to enterprise network.
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, trailDomainName, "1000");

        ShareUser.openSiteDocumentLibraryFromSearch(drone, opSiteName).render();
        Assert.assertTrue(isSynced(fileName, opSiteName), "File: " + fileName + " is not synced");

        // Step 3: Perform any changes to the synced document.
        editDocumentProperties = ShareUserSitePage.getEditPropertiesFromDocLibPage(drone, opSiteName, fileName).render();
        editDocumentProperties.setDocumentTitle(modifiedTitle + opUser1 + " second edit");
        editDocumentProperties.selectSave().render();

        // The changes are applied and synced successfully.
        Assert.assertTrue(isSynced(fileName, opSiteName), "File: " + fileName + " is not synced");

        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Step 4: Try to complete workflow in Cloud.
        MyTasksPage myTasks = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(myTasks.isTaskPresent(workFlowName), "Task is not displayed");
        myTasks.navigateToEditTaskPage(workFlowName).render();
        ShareUserWorkFlow.completeTask(hybridDrone, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE).render();
        myTasks.selectCompletedTasks().render();

        // Workflow can be completed.
        Assert.assertTrue(myTasks.isTaskPresent(workFlowName), "Task is not displayed");
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15724() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain("r1" + testName, invitedDomain1);
        String cloudUser2 = getUserNameForDomain("r2" + testName, invitedDomain1);
        String cloudUser3 = getUserNameForDomain("r3" + testName, invitedDomain1);
        String cloudSite1Name = getSiteName(testName + "cl1");
        String[] userInfo1 = new String[] { opUser1 };
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String[] cloudUserInfo2 = new String[] { cloudUser2 };
        String[] cloudUserInfo3 = new String[] { cloudUser3 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, invitedDomain1, "1000");

        // Create User2 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo2);

        // Create User3 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo3);

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite1Name, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);

        ShareUser.logout(drone);
    }

    /**
     * AONE-15724: Check workflow creation with non-members of site
     */
    @Test(groups = "Hybrid", timeOut = 200000)
    public void AONE_15724() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain("r1" + testName, invitedDomain1);
        String cloudUser2 = getUserNameForDomain("r2" + testName, invitedDomain1);
        String cloudSite1Name = getSiteName(testName + "cl1");
        String opSiteName = getSiteName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + ".txt";

        String workFlowName = testName + System.currentTimeMillis();
        String dueDate = getDueDateString();
        String errorMessage = "The folowing user(s) are not part of the site you selected as destination: " + cloudUser2
                + ". Invite them to the site in order to have them participate in the cloud-workflow.";
        List<String> userNames = new ArrayList<String>();

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        ShareUser.openDocumentLibrary(drone);
        ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });

        // Start Cloud Task or Review workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName);

        // Adding reviewers
        userNames.add(cloudUser2);
        userNames.add(cloudUser1);

        // Step 1: OP Create any 'Cloud Task or Review' workflow - 'Cloud Review Task' type, any data specified, reviewers - user1@network.com,
        // user2@network.com
        cloudTaskOrReviewPage.enterMessageText(workFlowName);
        cloudTaskOrReviewPage.selectTask(TaskType.CLOUD_REVIEW_TASK);
        cloudTaskOrReviewPage.enterDueDateText(dueDate);
        cloudTaskOrReviewPage.selectLockOnPremiseCheckbox(true);
        cloudTaskOrReviewPage.selectPriorityDropDown(Priority.MEDIUM);
        cloudTaskOrReviewPage.enterRequiredApprovalPercentage(100);
        DestinationAndAssigneePage destinationAndAssigneePage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();
        destinationAndAssigneePage.selectSite(cloudSite1Name);
        destinationAndAssigneePage.selectSubmitButtonToSync();
        AssignmentPage assignmentPage = cloudTaskOrReviewPage.selectAssignmentPage().render();
        assignmentPage.selectReviewers(userNames);

        // Step 2: Press Start workflow button.
        cloudTaskOrReviewPage.clickStartWorkflow();

        // The workflow can not be started. Following message is displayed: The following user(s) are not part of the site you selected as destination:
        // user2@hello.test. Invite them to the site in order to have them participate in the cloud-workflow.
        String error = cloudTaskOrReviewPage.getWorkFlowCouldNotBeStartedPromptMessage();
        Assert.assertTrue(error.contains(errorMessage), "Error is not displayed");
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15725() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain("r1" + testName, invitedDomain1);
        String cloudUser2 = getUserNameForDomain("r2" + testName, invitedDomain1);
        String cloudUser3 = getUserNameForDomain("r3" + testName, invitedDomain1);
        String cloudSite1Name = getSiteName(testName + "cl1");
        String opSiteName = getSiteName(testName + "OP");
        String[] userInfo1 = new String[] { opUser1 };
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String[] cloudUserInfo2 = new String[] { cloudUser2 };
        String[] cloudUserInfo3 = new String[] { cloudUser3 };
        String opFileName = getFileName(testName);
        List<String> userNames = new ArrayList<String>();
        String[] opFileInfo = new String[] { opFileName };
        String workFlowName1 = testName + "_workflowName";

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Create User2 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo2);

        // Create User3 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo3);

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite1Name, SITE_VISIBILITY_PUBLIC);

        // Inviting user2 as contributor to the site.
        ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, cloudUser1, cloudUser2, cloudSite1Name, UserRole.COLLABORATOR);

        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        ShareUser.openDocumentLibrary(drone);
        ShareUser.uploadFileInFolder(drone, opFileInfo).render();

        // Start Cloud Task or Review workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, opFileName);

        // Adding reviewers
        userNames.add(cloudUser2);
        userNames.add(cloudUser1);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setMessage(workFlowName1);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite1Name);
        formDetails.setApprovalPercentage(100);
        formDetails.setReviewers(userNames);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        // wait for sync
        isSynced(opFileName, opSiteName);
    }

    @Test(groups = "Hybrid", timeOut = 300000)
    public void AONE_15725() throws Exception
    {
        String testName = getTestName();
        String cloudUser1 = getUserNameForDomain("r1" + testName, invitedDomain1);
        String cloudUser2 = getUserNameForDomain("r2" + testName, invitedDomain1);
        String cloudUser3 = getUserNameForDomain("r3" + testName, invitedDomain1);

        String workFlowName1 = testName + "_workflowName";

        // Step 1: Cloud Login as user1@network.
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Step 2: Verify the task presence.
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).render();

        // The task is available for the user as he is a reviewer.
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName1));

        // Step 3: Cloud Login as user2@network..
        ShareUser.login(hybridDrone, cloudUser2, DEFAULT_PASSWORD);

        // Step 4: Verify the task presence.
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).render();

        // The task is available for the user as he is a reviewer.
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName1), "Task is not displayed");

        // Step 4: Cloud Login as user3@network.
        ShareUser.login(hybridDrone, cloudUser3, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

        // The task is unavailable for the user as he is not a reviewer.
        Assert.assertTrue(AbstractWorkflow.checkIfTaskIsPresent(hybridDrone, workFlowName1, false), "Task is available");
        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15726() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain("r1" + testName, invitedDomain1);
        String cloudUser2 = getUserNameForDomain("r2" + testName, invitedDomain1);
        String cloudSite1Name = getSiteName(testName + "cl1");
        String opSiteName = getSiteName(testName + "OP");
        String[] userInfo1 = new String[] { opUser1 };
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String[] cloudUserInfo2 = new String[] { cloudUser2 };
        String opFileName = getFileName(testName);
        List<String> userNames = new ArrayList<String>();
        String[] opFileInfo = new String[] { opFileName };
        String workFlowName1 = testName + "_workflowName";

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Create User2 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo2);

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite1Name, SITE_VISIBILITY_PUBLIC);

        // Inviting user2 as contributor to the site.
        ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, cloudUser1, cloudUser2, cloudSite1Name, UserRole.COLLABORATOR);

        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        ShareUser.openDocumentLibrary(drone);
        ShareUser.uploadFileInFolder(drone, opFileInfo).render();

        // Start Cloud Task or Review workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, opFileName);

        // Adding reviewers
        userNames.add(cloudUser2);
        userNames.add(cloudUser1);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setMessage(workFlowName1);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite1Name);
        formDetails.setApprovalPercentage(100);
        formDetails.setReviewers(userNames);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        // wait for sync
        isSynced(opFileName, opSiteName);
    }

    @Test(groups = "Hybrid", timeOut = 300000)
    public void AONE_15726() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain("r1" + testName, invitedDomain1);
        String cloudUser2 = getUserNameForDomain("r2" + testName, invitedDomain1);

        String workFlowName1 = testName + "_workflowName";
        String user1Comments = "firstUserComments";
        String user2Comments = "secondUserComments";

        // Step 1: Cloud Login as user1@network.
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        MyTasksPage myTasks = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName1), "Task is not available");

        // Step 2: Open view history page.
        TaskHistoryPage taskHistoryPage = myTasks.selectTaskHistory(workFlowName1).render();

        // Step 3: Verify the list of current tasks.
        List<WorkFlowDetailsCurrentTask> tasks = taskHistoryPage.getCurrentTasksList();
        String user1 = tasks.get(0).getAssignedTo();
        String user2 = tasks.get(1).getAssignedTo();

        // Both user1's and user2's tasks are displayed.
        Assert.assertTrue(user1.contains(cloudUser1), cloudUser1 + " is not displayed");
        Assert.assertTrue(user2.contains(cloudUser2), cloudUser2 + " is not displayed");

        // Step 4: Complete (Approve or Reject action) both user1's and user2's tasks.
        EditTaskPage editTaskPage = selectEditLinkOnUserTask(cloudUser1, taskHistoryPage);
        editTaskPage.selectStatusDropDown(TaskStatus.COMPLETED);
        editTaskPage.enterComment(user1Comments);
        editTaskPage.selectApproveButton().render();
        taskHistoryPage.render();

        editTaskPage = selectEditLinkOnUserTask(cloudUser2, taskHistoryPage);
        editTaskPage.selectStatusDropDown(TaskStatus.COMPLETED);
        editTaskPage.enterComment(user2Comments);
        editTaskPage.selectApproveButton().render();
        taskHistoryPage.render();

        ShareUser.logout(hybridDrone);

        // Step 5: OP Verify the appeared task.
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(drone, workFlowName1), "Task is not available");

        TaskDetailsPage taskDetailsPage = myTasksPage.selectViewTasks(workFlowName1).render();

        // The task contains two reviews with comments. Both comments are marked as made by user1.
        Assert.assertTrue(taskDetailsPage.getTaskDetailsHeader().contains(workFlowName1 + " (Document was approved on the cloud)"),
                "Details header is not displyed");
        Assert.assertTrue(taskDetailsPage.getComment().contains(cloudUser1 + " LName: " + user1Comments), "Comment for " + cloudUser1 + " is not displyed");
        Assert.assertTrue(taskDetailsPage.getComment().contains(cloudUser1 + " LName: " + user2Comments), "Comment for " + cloudUser2 + " is not displyed");
        Assert.assertFalse(taskDetailsPage.getComment().contains(cloudUser2), "User " + cloudUser2 + " is displyed");
        Assert.assertTrue(taskDetailsPage.getComment().contains("(Approved)"), "Incorrect comment");
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15730() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain("r1" + testName, invitedDomain1);
        String cloudSite1Name = getSiteName(testName + "cl1");
        String[] userInfo1 = new String[] { opUser1 };
        String[] cloudUserInfo1 = new String[] { cloudUser1 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, invitedDomain1, "1000");

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite1Name, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD).render();
        ShareUser.logout(drone);
    }

    /**
     * AONE-15730:L10N for Simple Cloud Task
     */
    @Test(groups = "Hybrid", timeOut = 990000)
    public void AONE_15730() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain("r1" + testName, invitedDomain1);
        String cloudSite1Name = getSiteName(testName + "cl1");
        String opSiteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";
        String workFlowName = testName + "_workFlow";
        String dueDate = getDueDateString();

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC).render();

        // Open Document library, Upload a file
        ShareUser.openDocumentLibrary(drone);
        ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });
        ShareUser.logout(drone);

        // set language in browser to French
        setCustomDroneWithLanguage(BrowserLanguages.FRENCH);
        ShareUser.login(customDrone, opUser1, DEFAULT_PASSWORD);

        // Start Cloud Task or Review workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startCloudReviewTaskOtherLanguage(customDrone, Language.FRENCH);
        cloudTaskOrReviewPage.selectTask(TaskType.SIMPLE_CLOUD_TASK);
        List<String> labels = cloudTaskOrReviewPage.getAllLabels();

        verifyLabelsFromCloudReviewPage(Language.FRENCH, labels, TaskType.SIMPLE_CLOUD_TASK);
        customDrone.closeWindow();

        // set language in browser to Deutsche
        setCustomDroneWithLanguage(BrowserLanguages.GERMANY);
        ShareUser.login(customDrone, opUser1, DEFAULT_PASSWORD);

        // Start Cloud Task or Review workflow
        cloudTaskOrReviewPage = ShareUserWorkFlow.startCloudReviewTaskOtherLanguage(customDrone, Language.DEUTSCHE);
        cloudTaskOrReviewPage.selectTask(TaskType.SIMPLE_CLOUD_TASK);
        labels = cloudTaskOrReviewPage.getAllLabels();
        verifyLabelsFromCloudReviewPage(Language.DEUTSCHE, labels, TaskType.SIMPLE_CLOUD_TASK);
        customDrone.closeWindow();

        // set language in browser to Italian
        setCustomDroneWithLanguage(BrowserLanguages.ITALIAN);
        ShareUser.login(customDrone, opUser1, DEFAULT_PASSWORD);

        // Start Cloud Task or Review workflow
        cloudTaskOrReviewPage = ShareUserWorkFlow.startCloudReviewTaskOtherLanguage(customDrone, Language.ITALIAN);
        cloudTaskOrReviewPage.selectTask(TaskType.SIMPLE_CLOUD_TASK);
        labels = cloudTaskOrReviewPage.getAllLabels();
        verifyLabelsFromCloudReviewPage(Language.ITALIAN, labels, TaskType.SIMPLE_CLOUD_TASK);
        customDrone.closeWindow();

        // set language in browser to JAPANESE
        setCustomDroneWithLanguage(BrowserLanguages.JAPANESE);
        ShareUser.login(customDrone, opUser1, DEFAULT_PASSWORD);

        // Start Cloud Task or Review workflow
        cloudTaskOrReviewPage = ShareUserWorkFlow.startCloudReviewTaskOtherLanguage(customDrone, Language.JAPANESE);
        cloudTaskOrReviewPage.selectTask(TaskType.SIMPLE_CLOUD_TASK);
        labels = cloudTaskOrReviewPage.getAllLabels();
        verifyLabelsFromCloudReviewPage(Language.JAPANESE, labels, TaskType.SIMPLE_CLOUD_TASK);
        customDrone.closeWindow();

        // set language in browser to SPANISH
        setCustomDroneWithLanguage(BrowserLanguages.SPANISH);
        ShareUser.login(customDrone, opUser1, DEFAULT_PASSWORD);

        // Start Cloud Task or Review workflow
        cloudTaskOrReviewPage = ShareUserWorkFlow.startCloudReviewTaskOtherLanguage(customDrone, Language.SPANISH);
        cloudTaskOrReviewPage.selectTask(TaskType.SIMPLE_CLOUD_TASK);
        labels = cloudTaskOrReviewPage.getAllLabels();
        verifyLabelsFromCloudReviewPage(Language.SPANISH, labels, TaskType.SIMPLE_CLOUD_TASK);
        customDrone.closeWindow();

        // create workflow
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        // Select "Cloud Task or Review" from select a workflow dropdown
        cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName).render();
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite1Name);
        formDetails.setAssignee(cloudUser1);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);
        formDetails.setMessage(workFlowName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        cloudTaskOrReviewPage.startWorkflow(formDetails).render(maxWaitTimeCloudSync);
        isSynced(fileName, opSiteName);

        // verify task is received in French
        ShareUser.loginWithLanguage(hybridDrone, Language.FRENCH, cloudUser1);
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName));

        TaskDetails details = myTasksPage.getTaskLabels(workFlowName);
        List<String> taskLabels = details.getTaskLabels();
        verifyLabelsMyTaskPage(Language.FRENCH, taskLabels);

        TaskDetailsPage detailsPage = myTasksPage.selectViewTasks(workFlowName).render();
        List<String> viewLabels = detailsPage.getAllLabels();
        verifyLabelsViewTaskPage(Language.FRENCH, viewLabels);

        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        EditTaskPage editPage = myTasksPage.navigateToEditTaskPage(workFlowName).render();
        List<String> editLabels = editPage.getAllLabels();
        verifyLabelsEditTaskPage(Language.FRENCH, editLabels);

        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        TaskHistoryPage historyPage = myTasksPage.selectTaskHistory(workFlowName).render();
        List<String> historyLabels = historyPage.getAllLabels();
        verifyLabelsHistoryTaskPage(Language.FRENCH, historyLabels);

        hybridDrone.closeWindow();
        setupHybridDrone();

        // verify task is received in DEUTSCHE
        ShareUser.loginWithLanguage(hybridDrone, Language.DEUTSCHE, cloudUser1).render();

        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName));

        details = myTasksPage.getTaskLabels(workFlowName);
        taskLabels = details.getTaskLabels();
        verifyLabelsMyTaskPage(Language.DEUTSCHE, taskLabels);

        detailsPage = myTasksPage.selectViewTasks(workFlowName).render();
        viewLabels = detailsPage.getAllLabels();
        verifyLabelsViewTaskPage(Language.DEUTSCHE, viewLabels);

        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        editPage = myTasksPage.navigateToEditTaskPage(workFlowName).render();
        editLabels = editPage.getAllLabels();
        verifyLabelsEditTaskPage(Language.DEUTSCHE, editLabels);

        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        historyPage = myTasksPage.selectTaskHistory(workFlowName).render();
        historyLabels = historyPage.getAllLabels();
        verifyLabelsHistoryTaskPage(Language.DEUTSCHE, historyLabels);

        hybridDrone.closeWindow();
        setupHybridDrone();

        // verify task is received in ITALIAN
        ShareUser.loginWithLanguage(hybridDrone, Language.ITALIAN, cloudUser1).render();
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName));

        details = myTasksPage.getTaskLabels(workFlowName);
        taskLabels = details.getTaskLabels();
        verifyLabelsMyTaskPage(Language.ITALIAN, taskLabels);

        detailsPage = myTasksPage.selectViewTasks(workFlowName).render();
        viewLabels = detailsPage.getAllLabels();
        verifyLabelsViewTaskPage(Language.ITALIAN, viewLabels);

        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        editPage = myTasksPage.navigateToEditTaskPage(workFlowName).render();
        editLabels = editPage.getAllLabels();
        verifyLabelsEditTaskPage(Language.ITALIAN, editLabels);

        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        historyPage = myTasksPage.selectTaskHistory(workFlowName).render();
        historyLabels = historyPage.getAllLabels();
        verifyLabelsHistoryTaskPage(Language.ITALIAN, historyLabels);

        hybridDrone.closeWindow();
        setupHybridDrone();

        // verify task is received in SPANISH
        ShareUser.loginWithLanguage(hybridDrone, Language.SPANISH, cloudUser1);
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName));

        details = myTasksPage.getTaskLabels(workFlowName);
        taskLabels = details.getTaskLabels();
        verifyLabelsMyTaskPage(Language.SPANISH, taskLabels);

        detailsPage = myTasksPage.selectViewTasks(workFlowName).render();
        viewLabels = detailsPage.getAllLabels();
        verifyLabelsViewTaskPage(Language.SPANISH, viewLabels);

        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        editPage = myTasksPage.navigateToEditTaskPage(workFlowName).render();
        editLabels = editPage.getAllLabels();
        verifyLabelsEditTaskPage(Language.SPANISH, editLabels);

        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        historyPage = myTasksPage.selectTaskHistory(workFlowName).render();
        historyLabels = historyPage.getAllLabels();
        verifyLabelsHistoryTaskPage(Language.SPANISH, historyLabels);

        hybridDrone.closeWindow();
        setupHybridDrone();

        // verify task is received in JAPANESE
        ShareUser.loginWithLanguage(hybridDrone, Language.JAPANESE, cloudUser1);
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName));

        details = myTasksPage.getTaskLabels(workFlowName);
        taskLabels = details.getTaskLabels();
        verifyLabelsMyTaskPage(Language.JAPANESE, taskLabels);

        detailsPage = myTasksPage.selectViewTasks(workFlowName).render();
        viewLabels = detailsPage.getAllLabels();
        verifyLabelsViewTaskPage(Language.JAPANESE, viewLabels);

        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        editPage = myTasksPage.navigateToEditTaskPage(workFlowName).render();
        editLabels = editPage.getAllLabels();
        verifyLabelsEditTaskPage(Language.JAPANESE, editLabels);

        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        historyPage = myTasksPage.selectTaskHistory(workFlowName).render();
        historyLabels = historyPage.getAllLabels();
        verifyLabelsHistoryTaskPage(Language.JAPANESE, historyLabels);

        hybridDrone.closeWindow();
        setupHybridDrone();

        // navigate to task and Click Task Done button
        ShareUser.loginWithLanguage(hybridDrone, Language.FRENCH, cloudUser1);
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        editPage = myTasksPage.navigateToEditTaskPage(workFlowName).render();
        editPage.selectTaskDoneButton();
        ShareUser.logout(hybridDrone);

        hybridDrone.closeWindow();
        setupHybridDrone();

        // Login as User1 (OP) in FRENCH
        setCustomDroneWithLanguage(BrowserLanguages.FRENCH);
        ShareUser.login(customDrone, opUser1, DEFAULT_PASSWORD);

        MyTasksPage opMyTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(customDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(customDrone, workFlowName));

        TaskDetails opDetails = opMyTasksPage.getTaskLabels(workFlowName);
        List<String> opTaskLabels = opDetails.getTaskLabels();
        verifyLabelsMyTaskPage(Language.FRENCH, opTaskLabels);

        TaskDetailsPage opDetailsPage = opMyTasksPage.selectViewTasks(workFlowName).render();
        List<String> viewLabelsOP = opDetailsPage.getAllLabels();
        verifyLabelsViewTaskPage(Language.FRENCH, viewLabelsOP);

        opMyTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(customDrone);
        EditTaskPage opEditPage = opMyTasksPage.navigateToEditTaskPage(workFlowName).render();
        List<String> opEditLabels = opEditPage.getAllLabels();
        verifyLabelsEditTaskPage(Language.FRENCH, opEditLabels);

        opMyTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(customDrone);
        ViewWorkflowPage opViewWorkflow = opMyTasksPage.selectViewWorkflow(workFlowName).render();
        List<String> opHistoryWorkflowPage = opViewWorkflow.getAllLabels();
        verifyLabelsHistoryTaskPage(Language.FRENCH, opHistoryWorkflowPage);
        customDrone.closeWindow();

        // Login as User1 (OP) in DEUTSCHE
        setCustomDroneWithLanguage(BrowserLanguages.GERMANY);
        ShareUser.login(customDrone, opUser1, DEFAULT_PASSWORD);

        opMyTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(customDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(customDrone, workFlowName));

        opDetails = opMyTasksPage.getTaskLabels(workFlowName);
        opTaskLabels = opDetails.getTaskLabels();
        verifyLabelsMyTaskPage(Language.DEUTSCHE, opTaskLabels);

        opDetailsPage = opMyTasksPage.selectViewTasks(workFlowName).render();
        viewLabelsOP = opDetailsPage.getAllLabels();
        verifyLabelsViewTaskPage(Language.DEUTSCHE, viewLabelsOP);

        opMyTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(customDrone);
        opEditPage = opMyTasksPage.navigateToEditTaskPage(workFlowName).render();
        opEditLabels = opEditPage.getAllLabels();
        verifyLabelsEditTaskPage(Language.DEUTSCHE, opEditLabels);

        opMyTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(customDrone);
        opViewWorkflow = opMyTasksPage.selectViewWorkflow(workFlowName).render();
        opHistoryWorkflowPage = opViewWorkflow.getAllLabels();
        verifyLabelsHistoryTaskPage(Language.DEUTSCHE, opHistoryWorkflowPage);
        customDrone.closeWindow();

        // Login as User1 (OP) in ITALIAN
        setCustomDroneWithLanguage(BrowserLanguages.ITALIAN);
        ShareUser.login(customDrone, opUser1, DEFAULT_PASSWORD);

        opMyTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(customDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(customDrone, workFlowName));

        opDetails = opMyTasksPage.getTaskLabels(workFlowName);
        opTaskLabels = opDetails.getTaskLabels();
        verifyLabelsMyTaskPage(Language.ITALIAN, opTaskLabels);

        opDetailsPage = opMyTasksPage.selectViewTasks(workFlowName).render();
        viewLabelsOP = opDetailsPage.getAllLabels();
        verifyLabelsViewTaskPage(Language.ITALIAN, viewLabelsOP);

        opMyTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(customDrone);
        opEditPage = opMyTasksPage.navigateToEditTaskPage(workFlowName).render();
        opEditLabels = opEditPage.getAllLabels();
        verifyLabelsEditTaskPage(Language.ITALIAN, opEditLabels);

        opMyTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(customDrone);
        opViewWorkflow = opMyTasksPage.selectViewWorkflow(workFlowName).render();
        opHistoryWorkflowPage = opViewWorkflow.getAllLabels();
        verifyLabelsHistoryTaskPage(Language.ITALIAN, opHistoryWorkflowPage);
        customDrone.closeWindow();

        // Login as User1 (OP) in SPANISH
        setCustomDroneWithLanguage(BrowserLanguages.SPANISH);
        ShareUser.login(customDrone, opUser1, DEFAULT_PASSWORD);

        opMyTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(customDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(customDrone, workFlowName));

        opDetails = opMyTasksPage.getTaskLabels(workFlowName);
        opTaskLabels = opDetails.getTaskLabels();
        verifyLabelsMyTaskPage(Language.SPANISH, opTaskLabels);

        opDetailsPage = opMyTasksPage.selectViewTasks(workFlowName).render();
        viewLabelsOP = opDetailsPage.getAllLabels();
        verifyLabelsViewTaskPage(Language.SPANISH, viewLabelsOP);

        opMyTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(customDrone);
        opEditPage = opMyTasksPage.navigateToEditTaskPage(workFlowName).render();
        opEditLabels = opEditPage.getAllLabels();
        verifyLabelsEditTaskPage(Language.SPANISH, opEditLabels);

        opMyTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(customDrone);
        opViewWorkflow = opMyTasksPage.selectViewWorkflow(workFlowName).render();
        opHistoryWorkflowPage = opViewWorkflow.getAllLabels();
        verifyLabelsHistoryTaskPage(Language.SPANISH, opHistoryWorkflowPage);
        customDrone.closeWindow();

        // Login as User1 (OP) in JAPANESE
        setCustomDroneWithLanguage(BrowserLanguages.JAPANESE);
        ShareUser.login(customDrone, opUser1, DEFAULT_PASSWORD);

        opMyTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(customDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(customDrone, workFlowName));

        opDetails = opMyTasksPage.getTaskLabels(workFlowName);
        opTaskLabels = opDetails.getTaskLabels();
        verifyLabelsMyTaskPage(Language.JAPANESE, opTaskLabels);

        opDetailsPage = opMyTasksPage.selectViewTasks(workFlowName).render();
        viewLabelsOP = opDetailsPage.getAllLabels();
        verifyLabelsViewTaskPage(Language.JAPANESE, viewLabelsOP);

        opMyTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(customDrone);
        opEditPage = opMyTasksPage.navigateToEditTaskPage(workFlowName).render();
        opEditLabels = opEditPage.getAllLabels();
        verifyLabelsEditTaskPage(Language.JAPANESE, opEditLabels);

        opMyTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(customDrone);
        opViewWorkflow = opMyTasksPage.selectViewWorkflow(workFlowName).render();
        opHistoryWorkflowPage = opViewWorkflow.getAllLabels();
        verifyLabelsHistoryTaskPage(Language.JAPANESE, opHistoryWorkflowPage);
        customDrone.closeWindow();
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15731() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain("r1" + testName, invitedDomain1);
        String cloudSite1Name = getSiteName(testName + "cl1");
        String[] userInfo1 = new String[] { opUser1 };
        String[] cloudUserInfo1 = new String[] { cloudUser1 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, invitedDomain1, "1000");

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite1Name, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD).render();
        ShareUser.logout(drone);
    }

    /**
     * AONE-15731:L10N for Cloud Review Task
     */
    @Test(groups = "Hybrid", timeOut = 990000)
    public void AONE_15731() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain("r1" + testName, invitedDomain1);
        String cloudSite1Name = getSiteName(testName + "cl1");
        String opSiteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";
        String workFlowName = testName + "_workFlow";
        String dueDate = getDueDateString();
        List<String> userNames = new ArrayList<String>();

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC).render();

        // Open Document library, Upload a file
        ShareUser.openDocumentLibrary(drone);
        ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });
        ShareUser.logout(drone);

        // set language in browser to French
        setCustomDroneWithLanguage(BrowserLanguages.FRENCH);
        ShareUser.login(customDrone, opUser1, DEFAULT_PASSWORD);

        // Start Cloud Task or Review workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startCloudReviewTaskOtherLanguage(customDrone, Language.FRENCH);
        cloudTaskOrReviewPage.selectTask(TaskType.CLOUD_REVIEW_TASK);
        cloudTaskOrReviewPage.render();
        List<String> labels = cloudTaskOrReviewPage.getAllLabels();

        verifyLabelsFromCloudReviewPage(Language.FRENCH, labels, TaskType.CLOUD_REVIEW_TASK);
        customDrone.closeWindow();

        // set language in browser to Deutsche
        setCustomDroneWithLanguage(BrowserLanguages.GERMANY);
        ShareUser.login(customDrone, opUser1, DEFAULT_PASSWORD);

        // Start Cloud Task or Review workflow
        cloudTaskOrReviewPage = ShareUserWorkFlow.startCloudReviewTaskOtherLanguage(customDrone, Language.DEUTSCHE);
        cloudTaskOrReviewPage.selectTask(TaskType.CLOUD_REVIEW_TASK);
        labels = cloudTaskOrReviewPage.getAllLabels();
        verifyLabelsFromCloudReviewPage(Language.DEUTSCHE, labels, TaskType.CLOUD_REVIEW_TASK);
        customDrone.closeWindow();

        // set language in browser to Italian
        setCustomDroneWithLanguage(BrowserLanguages.ITALIAN);
        ShareUser.login(customDrone, opUser1, DEFAULT_PASSWORD);

        // Start Cloud Task or Review workflow
        cloudTaskOrReviewPage = ShareUserWorkFlow.startCloudReviewTaskOtherLanguage(customDrone, Language.ITALIAN);
        cloudTaskOrReviewPage.selectTask(TaskType.CLOUD_REVIEW_TASK);
        labels = cloudTaskOrReviewPage.getAllLabels();
        verifyLabelsFromCloudReviewPage(Language.ITALIAN, labels, TaskType.CLOUD_REVIEW_TASK);
        customDrone.closeWindow();

        // set language in browser to JAPANESE
        setCustomDroneWithLanguage(BrowserLanguages.JAPANESE);
        ShareUser.login(customDrone, opUser1, DEFAULT_PASSWORD);

        // Start Cloud Task or Review workflow
        cloudTaskOrReviewPage = ShareUserWorkFlow.startCloudReviewTaskOtherLanguage(customDrone, Language.JAPANESE);
        cloudTaskOrReviewPage.selectTask(TaskType.CLOUD_REVIEW_TASK);
        labels = cloudTaskOrReviewPage.getAllLabels();
        verifyLabelsFromCloudReviewPage(Language.JAPANESE, labels, TaskType.CLOUD_REVIEW_TASK);

        customDrone.closeWindow();

        // set language in browser to SPANISH
        setCustomDroneWithLanguage(BrowserLanguages.SPANISH);
        ShareUser.login(customDrone, opUser1, DEFAULT_PASSWORD);

        // Start Cloud Task or Review workflow
        cloudTaskOrReviewPage = ShareUserWorkFlow.startCloudReviewTaskOtherLanguage(customDrone, Language.SPANISH);
        cloudTaskOrReviewPage.selectTask(TaskType.CLOUD_REVIEW_TASK);
        labels = cloudTaskOrReviewPage.getAllLabels();
        verifyLabelsFromCloudReviewPage(Language.SPANISH, labels, TaskType.CLOUD_REVIEW_TASK);
        customDrone.closeWindow();

        // create workflow
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName);
        userNames.add(cloudUser1);

        // Select "Cloud Task or Review" from select a workflow dropdown
        CloudTaskOrReviewPage cloudTaskOrReviewPage1 = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName).render();
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite1Name);
        formDetails.setReviewers(userNames);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);
        formDetails.setMessage(workFlowName);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setApprovalPercentage(100);
        cloudTaskOrReviewPage1.startWorkflow(formDetails).render(maxWaitTimeCloudSync);
        isSynced(fileName, opSiteName);

        // verify task is received in French
        ShareUser.loginWithLanguage(hybridDrone, Language.FRENCH, cloudUser1);
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName));

        TaskDetails details = myTasksPage.getTaskLabels(workFlowName);
        List<String> taskLabels = details.getTaskLabels();
        verifyLabelsMyTaskPage(Language.FRENCH, taskLabels);

        TaskDetailsPage detailsPage = myTasksPage.selectViewTasks(workFlowName).render();
        List<String> viewLabels = detailsPage.getAllLabels();
        verifyLabelsViewTaskPage(Language.FRENCH, viewLabels);

        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        EditTaskPage editPage = myTasksPage.navigateToEditTaskPage(workFlowName).render();
        List<String> editLabels = editPage.getAllLabels();
        verifyLabelsEditTaskPage(Language.FRENCH, editLabels);

        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        TaskHistoryPage historyPage = myTasksPage.selectTaskHistory(workFlowName).render();
        List<String> historyLabels = historyPage.getAllLabels();
        verifyLabelsHistoryTaskPage(Language.FRENCH, historyLabels);

        hybridDrone.closeWindow();
        setupHybridDrone();

        // verify task is received in DEUTSCHE
        ShareUser.loginWithLanguage(hybridDrone, Language.DEUTSCHE, cloudUser1).render();

        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName));

        details = myTasksPage.getTaskLabels(workFlowName);
        taskLabels = details.getTaskLabels();
        verifyLabelsMyTaskPage(Language.DEUTSCHE, taskLabels);

        detailsPage = myTasksPage.selectViewTasks(workFlowName).render();
        viewLabels = detailsPage.getAllLabels();
        verifyLabelsViewTaskPage(Language.DEUTSCHE, viewLabels);

        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        editPage = myTasksPage.navigateToEditTaskPage(workFlowName).render();
        editLabels = editPage.getAllLabels();
        verifyLabelsEditTaskPage(Language.DEUTSCHE, editLabels);

        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        historyPage = myTasksPage.selectTaskHistory(workFlowName).render();
        historyLabels = historyPage.getAllLabels();
        verifyLabelsHistoryTaskPage(Language.DEUTSCHE, historyLabels);

        hybridDrone.closeWindow();
        setupHybridDrone();

        // verify task is received in ITALIAN
        ShareUser.loginWithLanguage(hybridDrone, Language.ITALIAN, cloudUser1).render();
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName));

        details = myTasksPage.getTaskLabels(workFlowName);
        taskLabels = details.getTaskLabels();
        verifyLabelsMyTaskPage(Language.ITALIAN, taskLabels);

        detailsPage = myTasksPage.selectViewTasks(workFlowName).render();
        viewLabels = detailsPage.getAllLabels();
        verifyLabelsViewTaskPage(Language.ITALIAN, viewLabels);

        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        editPage = myTasksPage.navigateToEditTaskPage(workFlowName).render();
        editLabels = editPage.getAllLabels();
        verifyLabelsEditTaskPage(Language.ITALIAN, editLabels);

        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        historyPage = myTasksPage.selectTaskHistory(workFlowName).render();
        historyLabels = historyPage.getAllLabels();
        verifyLabelsHistoryTaskPage(Language.ITALIAN, historyLabels);

        hybridDrone.closeWindow();
        setupHybridDrone();

        // verify task is received in SPANISH
        ShareUser.loginWithLanguage(hybridDrone, Language.SPANISH, cloudUser1);
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName));

        details = myTasksPage.getTaskLabels(workFlowName);
        taskLabels = details.getTaskLabels();
        verifyLabelsMyTaskPage(Language.SPANISH, taskLabels);

        detailsPage = myTasksPage.selectViewTasks(workFlowName).render();
        viewLabels = detailsPage.getAllLabels();
        verifyLabelsViewTaskPage(Language.SPANISH, viewLabels);

        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        editPage = myTasksPage.navigateToEditTaskPage(workFlowName).render();
        editLabels = editPage.getAllLabels();
        verifyLabelsEditTaskPage(Language.SPANISH, editLabels);

        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        historyPage = myTasksPage.selectTaskHistory(workFlowName).render();
        historyLabels = historyPage.getAllLabels();
        verifyLabelsHistoryTaskPage(Language.SPANISH, historyLabels);

        hybridDrone.closeWindow();
        setupHybridDrone();

        // verify task is received in JAPANESE
        ShareUser.loginWithLanguage(hybridDrone, Language.JAPANESE, cloudUser1);
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName));

        details = myTasksPage.getTaskLabels(workFlowName);
        taskLabels = details.getTaskLabels();
        verifyLabelsMyTaskPage(Language.JAPANESE, taskLabels);

        detailsPage = myTasksPage.selectViewTasks(workFlowName).render();
        viewLabels = detailsPage.getAllLabels();
        verifyLabelsViewTaskPage(Language.JAPANESE, viewLabels);

        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        editPage = myTasksPage.navigateToEditTaskPage(workFlowName).render();
        editLabels = editPage.getAllLabels();
        verifyLabelsEditTaskPage(Language.JAPANESE, editLabels);

        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        historyPage = myTasksPage.selectTaskHistory(workFlowName).render();
        historyLabels = historyPage.getAllLabels();
        verifyLabelsHistoryTaskPage(Language.JAPANESE, historyLabels);

        hybridDrone.closeWindow();
        setupHybridDrone();

        // navigate to task and Click Task Done button
        ShareUser.loginWithLanguage(hybridDrone, Language.FRENCH, cloudUser1);
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        editPage = myTasksPage.navigateToEditTaskPage(workFlowName).render();
        editPage.selectApproveButton();
        ShareUser.logout(hybridDrone);

        // Login as User1 (OP) in FRENCH
        setCustomDroneWithLanguage(BrowserLanguages.FRENCH);
        ShareUser.login(customDrone, opUser1, DEFAULT_PASSWORD);

        MyTasksPage opMyTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(customDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(customDrone, workFlowName));

        TaskDetails opDetails = opMyTasksPage.getTaskLabels(workFlowName);
        List<String> opTaskLabels = opDetails.getTaskLabels();
        verifyLabelsMyTaskPage(Language.FRENCH, opTaskLabels);

        TaskDetailsPage opDetailsPage = opMyTasksPage.selectViewTasks(workFlowName).render();
        List<String> viewLabelsOP = opDetailsPage.getAllLabels();
        verifyLabelsViewTaskPage(Language.FRENCH, viewLabelsOP);

        opMyTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(customDrone);
        EditTaskPage opEditPage = opMyTasksPage.navigateToEditTaskPage(workFlowName).render();
        List<String> opEditLabels = opEditPage.getAllLabels();
        verifyLabelsEditTaskPage(Language.FRENCH, opEditLabels);

        opMyTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(customDrone);
        ViewWorkflowPage opViewWorkflow = opMyTasksPage.selectViewWorkflow(workFlowName).render();
        List<String> opHistoryWorkflowPage = opViewWorkflow.getAllLabels();
        verifyLabelsHistoryTaskPage(Language.FRENCH, opHistoryWorkflowPage);
        customDrone.closeWindow();

        // Login as User1 (OP) in DEUTSCHE
        setCustomDroneWithLanguage(BrowserLanguages.GERMANY);
        ShareUser.login(customDrone, opUser1, DEFAULT_PASSWORD);

        opMyTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(customDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(customDrone, workFlowName));

        opDetails = opMyTasksPage.getTaskLabels(workFlowName);
        opTaskLabels = opDetails.getTaskLabels();
        verifyLabelsMyTaskPage(Language.DEUTSCHE, opTaskLabels);

        opDetailsPage = opMyTasksPage.selectViewTasks(workFlowName).render();
        viewLabelsOP = opDetailsPage.getAllLabels();
        verifyLabelsViewTaskPage(Language.DEUTSCHE, viewLabelsOP);

        opMyTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(customDrone);
        opEditPage = opMyTasksPage.navigateToEditTaskPage(workFlowName).render();
        opEditLabels = opEditPage.getAllLabels();
        verifyLabelsEditTaskPage(Language.DEUTSCHE, opEditLabels);

        opMyTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(customDrone);
        opViewWorkflow = opMyTasksPage.selectViewWorkflow(workFlowName).render();
        opHistoryWorkflowPage = opViewWorkflow.getAllLabels();
        verifyLabelsHistoryTaskPage(Language.DEUTSCHE, opHistoryWorkflowPage);
        customDrone.closeWindow();

        // Login as User1 (OP) in ITALIAN
        setCustomDroneWithLanguage(BrowserLanguages.ITALIAN);
        ShareUser.login(customDrone, opUser1, DEFAULT_PASSWORD);

        opMyTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(customDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(customDrone, workFlowName));

        opDetails = opMyTasksPage.getTaskLabels(workFlowName);
        opTaskLabels = opDetails.getTaskLabels();
        verifyLabelsMyTaskPage(Language.ITALIAN, opTaskLabels);

        opDetailsPage = opMyTasksPage.selectViewTasks(workFlowName).render();
        viewLabelsOP = opDetailsPage.getAllLabels();
        verifyLabelsViewTaskPage(Language.ITALIAN, viewLabelsOP);

        opMyTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(customDrone);
        opEditPage = opMyTasksPage.navigateToEditTaskPage(workFlowName).render();
        opEditLabels = opEditPage.getAllLabels();
        verifyLabelsEditTaskPage(Language.ITALIAN, opEditLabels);

        opMyTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(customDrone);
        opViewWorkflow = opMyTasksPage.selectViewWorkflow(workFlowName).render();
        opHistoryWorkflowPage = opViewWorkflow.getAllLabels();
        verifyLabelsHistoryTaskPage(Language.ITALIAN, opHistoryWorkflowPage);
        customDrone.closeWindow();

        // Login as User1 (OP) in SPANISH
        setCustomDroneWithLanguage(BrowserLanguages.SPANISH);
        ShareUser.login(customDrone, opUser1, DEFAULT_PASSWORD);

        opMyTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(customDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(customDrone, workFlowName));

        opDetails = opMyTasksPage.getTaskLabels(workFlowName);
        opTaskLabels = opDetails.getTaskLabels();
        verifyLabelsMyTaskPage(Language.SPANISH, opTaskLabels);

        opDetailsPage = opMyTasksPage.selectViewTasks(workFlowName).render();
        viewLabelsOP = opDetailsPage.getAllLabels();
        verifyLabelsViewTaskPage(Language.SPANISH, viewLabelsOP);

        opMyTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(customDrone);
        opEditPage = opMyTasksPage.navigateToEditTaskPage(workFlowName).render();
        opEditLabels = opEditPage.getAllLabels();
        verifyLabelsEditTaskPage(Language.SPANISH, opEditLabels);

        opMyTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(customDrone);
        opViewWorkflow = opMyTasksPage.selectViewWorkflow(workFlowName).render();
        opHistoryWorkflowPage = opViewWorkflow.getAllLabels();
        verifyLabelsHistoryTaskPage(Language.SPANISH, opHistoryWorkflowPage);
        customDrone.closeWindow();

        // Login as User1 (OP) in JAPANESE
        setCustomDroneWithLanguage(BrowserLanguages.JAPANESE);
        ShareUser.login(customDrone, opUser1, DEFAULT_PASSWORD);

        opMyTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(customDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(customDrone, workFlowName));

        opDetails = opMyTasksPage.getTaskLabels(workFlowName);
        opTaskLabels = opDetails.getTaskLabels();
        verifyLabelsMyTaskPage(Language.JAPANESE, opTaskLabels);

        opDetailsPage = opMyTasksPage.selectViewTasks(workFlowName).render();
        viewLabelsOP = opDetailsPage.getAllLabels();
        verifyLabelsViewTaskPage(Language.JAPANESE, viewLabelsOP);

        opMyTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(customDrone);
        opEditPage = opMyTasksPage.navigateToEditTaskPage(workFlowName).render();
        opEditLabels = opEditPage.getAllLabels();
        verifyLabelsEditTaskPage(Language.JAPANESE, opEditLabels);

        opMyTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(customDrone);
        opViewWorkflow = opMyTasksPage.selectViewWorkflow(workFlowName).render();
        opHistoryWorkflowPage = opViewWorkflow.getAllLabels();
        verifyLabelsHistoryTaskPage(Language.JAPANESE, opHistoryWorkflowPage);
        customDrone.closeWindow();
    }

    private boolean isSynced(String fileName, String siteName)
    {
        boolean synced = false;
        int counter = 1;
        int retryRefreshCount = 4;
        while (counter <= retryRefreshCount)
        {
            if (checkIfContentIsSynced(drone, fileName))
            {
                logger.info("Synced successful");
                return synced = true;
            }
            else
            {
                drone.refresh();
                counter++;

                if (counter == 2 || counter == 3)
                {
                    DocumentLibraryPage docLib = ShareUser.openSitesDocumentLibrary(drone, siteName);
                    docLib.getFileDirectoryInfo(fileName).selectRequestSync().render();
                }
            }
        }
        return synced;
    }

    private boolean verifySyncFailed(String fileName, String siteName)
    {
        boolean failed = false;
        int counter = 1;
        int retryRefreshCount = 4;
        while (counter <= retryRefreshCount)
        {
            if (checkIfSyncFailed(drone, fileName))
            {
                logger.info("Synced failed. It's ok");
                return failed = true;
            }
            else
            {
                drone.refresh();
                counter++;

                if (counter == 2 || counter == 3)
                {
                    DocumentLibraryPage docLib = ShareUser.openSitesDocumentLibrary(drone, siteName);
                    docLib.getFileDirectoryInfo(fileName).selectRequestSync().render();
                }
            }
        }

        return failed;
    }

    private String getFileWithWorkflowLabels(Language lng)
    {
        String fileName = lng.getLanguagePropertyFileName();
        return DATA_FOLDER + SLASH + "Workflow-Localization-labels" + SLASH + fileName;
    }

    private void verifyLabelsFromCloudReviewPage(Language language, List<String> cloudReviewTaskLabels, TaskType taskType)
    {
        Assert.assertTrue(cloudReviewTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "cloudTaskReview.message")),
                "Wrong label in cloud review task page for language:" + language);
        Assert.assertTrue(cloudReviewTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "cloudTaskReview.due")),
                "Wrong label in cloud review task page for language:" + language);
        Assert.assertTrue(cloudReviewTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "cloudTaskReview.priority")),
                "Wrong label in cloud review task page for language:" + language);
        if (taskType.equals(TaskType.SIMPLE_CLOUD_TASK))
        {
            Assert.assertTrue(
                    cloudReviewTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "cloudTaskReview.assignament")),
                    "Wrong label in cloud review task page for language:" + language);
        }
        else
        {
            Assert.assertTrue(
                    cloudReviewTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "cloudTaskReview.reviewers")),
                    "Wrong label in cloud review task page for language:" + language);
        }

        Assert.assertTrue(
                cloudReviewTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "cloudTaskReview.afterCompletion")),
                "Wrong label in cloud review task page for language:" + language);
        Assert.assertTrue(cloudReviewTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "cloudTaskReview.lock")),
                "Wrong label in cloud review task page for language:" + language);
        Assert.assertTrue(cloudReviewTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "cloudTaskReview.items")),
                "Wrong label in cloud review task page for language:" + language);
    }

    private void verifyLabelsMyTaskPage(Language language, List<String> myTaskLabels)
    {
        Assert.assertTrue(myTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "myTaskPage.due")),
                "Wrong label for language:" + language);
        Assert.assertTrue(myTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "myTaskPage.started")),
                "Wrong label for language:" + language);
        Assert.assertTrue(myTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "myTaskPage.status")),
                "Wrong label for language:" + language);
        Assert.assertTrue(myTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "myTaskPage.type")),
                "Wrong label for language:" + language);
        Assert.assertTrue(myTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "myTaskPage.description")),
                "Wrong label for language:" + language);
        Assert.assertTrue(myTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "myTaskPage.startedBy")),
                "Wrong label for language:" + language);
    }

    private void verifyLabelsViewTaskPage(Language language, List<String> viewTaskLabels)
    {
        Assert.assertTrue(viewTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "viewTask.message")),
                "Wrong label for language:" + language);
        Assert.assertTrue(viewTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "viewTask.owner")),
                "Wrong label for language:" + language);
        Assert.assertTrue(viewTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "viewTask.priority")),
                "Wrong label for language:" + language);
        Assert.assertTrue(viewTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "viewTask.due")),
                "Wrong label for language:" + language);
        Assert.assertTrue(viewTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "viewTask.identifier")),
                "Wrong label for language:" + language);
        Assert.assertTrue(viewTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "viewTask.status")),
                "Wrong label for language:" + language);
        Assert.assertTrue(viewTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "viewTask.items")),
                "Wrong label for language:" + language);
    }

    private void verifyLabelsEditTaskPage(Language language, List<String> editTaskLabels)
    {
        Assert.assertTrue(editTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "editTask.message")),
                "Wrong label in edit task page for language:" + language);
        Assert.assertTrue(editTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "editTask.owner")),
                "Wrong label in edit task page for language:" + language);
        Assert.assertTrue(editTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "editTask.priority")),
                "Wrong label in edit task page for language:" + language);
        Assert.assertTrue(editTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "editTask.due")),
                "Wrong label in edit task page for language:" + language);
        Assert.assertTrue(editTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "editTask.identifier")),
                "Wrong label in edit task page for language:" + language);
    }

    private void verifyLabelsHistoryTaskPage(Language language, List<String> historyTaskLabels)
    {
        Assert.assertTrue(historyTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "taskHistory.completedOn")),
                "Wrong label in history task page for language:" + language);
        Assert.assertTrue(historyTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "taskHistory.completedBy")),
                "Wrong label in history task page for language:" + language);
        Assert.assertTrue(historyTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "taskHistory.outcome")),
                "Wrong label in history task page for language:" + language);
        Assert.assertTrue(historyTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "taskHistory.title")),
                "Wrong label in history task page for language:" + language);
        Assert.assertTrue(historyTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "taskHistory.description")),
                "Wrong label in history task page for language:" + language);
        Assert.assertTrue(historyTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "taskHistory.startedBy")),
                "Wrong label in history task page for language:" + language);
        Assert.assertTrue(historyTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "taskHistory.due")),
                "Wrong label in history task page for language:" + language);
        Assert.assertTrue(historyTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "taskHistory.completed")),
                "Wrong label in history task page for language:" + language);
        Assert.assertTrue(historyTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "taskHistory.started")),
                "Wrong label in history task page for language:" + language);
        Assert.assertTrue(historyTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "taskHistory.priority")),
                "Wrong label in history task page for language:" + language);
        Assert.assertTrue(historyTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "taskHistory.status")),
                "Wrong label in history task page for language:" + language);
        Assert.assertTrue(historyTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "taskHistory.message")),
                "Wrong label in history task page for language:" + language);
        Assert.assertTrue(historyTaskLabels.contains(PropertiesUtil.getPropertyValue(getFileWithWorkflowLabels(language), "taskHistory.items")),
                "Wrong label in history task page for language:" + language);

    }

    /**
     * @param user
     * @param taskHistoryPage
     */
    private EditTaskPage selectEditLinkOnUserTask(String user, TaskHistoryPage taskHistoryPage)
    {
        for (WorkFlowDetailsCurrentTask currentTask : taskHistoryPage.getCurrentTasksList())
        {
            if (currentTask.getAssignedTo().contains(user))
            {
                return currentTask.getEditTaskLink().click().render();
            }
        }
        throw new PageOperationException("Unable for find the user current task.");
    }

}
