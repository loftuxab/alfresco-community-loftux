/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.share.workflow;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.task.TaskStatus;
import org.alfresco.po.share.workflow.AssignmentPage;
import org.alfresco.po.share.workflow.CloudTaskOrReviewPage;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
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
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Ranjith Manyam
 * 
 */
@Listeners(FailedTestListener.class)
public class HybridWorkflowTest1 extends AbstractWorkflow
{
    private String testDomain;
    
    /**
     * Class includes: Tests from TestLink in Area: Workflow
     */
    @Override
    @BeforeClass(alwaysRun=true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testDomain = DOMAIN_HYBRID;
    }

    /*public void dataPrep(String testName) throws Exception
    {
        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, testDomain);
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
    }*/

    @Test (groups="Hybrid", enabled = true)
    public void AONE_15593() throws Exception
    {
        // Login as OP user
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Start Simple Cloud Task Workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startCloudReviewTaskWorkFlow(drone);

        //Verify the General section
        Assert.assertTrue(cloudTaskOrReviewPage.isCloudReviewTaskElementsPresent(), "Verifying Cloud Task or Review fields displayed");
        Assert.assertTrue(cloudTaskOrReviewPage.isMessageTextFieldPresent(), "Message text field is not displayed");
        Assert.assertTrue(cloudTaskOrReviewPage.isTypeDropDownPresent(), "Type drop down list is not present");
        Assert.assertTrue(cloudTaskOrReviewPage.isHelpIconPresent(), "Help icon is not present");
        Assert.assertTrue(cloudTaskOrReviewPage.isDueDatePresent(), "Due date field is not present");
        Assert.assertTrue(cloudTaskOrReviewPage.isPriorityDropDownPresent(), "Priority drop down is nor present");

        cloudTaskOrReviewPage.clickHelpIcon();

        Assert.assertEquals(cloudTaskOrReviewPage.getHelpText(), "This field must have between 0 and 250 characters.");
        cloudTaskOrReviewPage.clickHelpIcon();

        cloudTaskOrReviewPage.selectTask(TaskType.SIMPLE_CLOUD_TASK);
        Assert.assertTrue(cloudTaskOrReviewPage.isTaskTypeSelected(TaskType.SIMPLE_CLOUD_TASK));

        cloudTaskOrReviewPage.selectTask(TaskType.CLOUD_REVIEW_TASK);
        Assert.assertTrue(cloudTaskOrReviewPage.isTaskTypeSelected(TaskType.CLOUD_REVIEW_TASK));

        String manualEntryDueDate = new DateTime().plusDays(5).toString("dd/MM/yyyy");

        cloudTaskOrReviewPage.enterDueDateText(manualEntryDueDate);
        Assert.assertEquals(cloudTaskOrReviewPage.getDueDate(), manualEntryDueDate);

        cloudTaskOrReviewPage.selectDateFromCalendar(getDueDateString());
        Assert.assertEquals(cloudTaskOrReviewPage.getDueDate(), getDueDateString());

        List<String> options = cloudTaskOrReviewPage.getPriorityOptions();
        Assert.assertEquals(options.size(), Priority.values().length);
        Assert.assertTrue(options.contains(Priority.HIGH.getPriority()));
        Assert.assertTrue(options.contains(Priority.LOW.getPriority()));
        Assert.assertTrue(options.contains(Priority.MEDIUM.getPriority()));

        cloudTaskOrReviewPage.selectPriorityDropDown(Priority.LOW);
        Assert.assertEquals(cloudTaskOrReviewPage.getSelectedPriorityOption(), Priority.LOW);

        ShareUser.logout(drone);
    }



    @Test(groups="DataPrepHybrid")
    public void dataPrep_15594() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] {user1};

        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
        String[] cloudUserInfo1 = new String[] {cloudUser};

        String cloudSite = getSiteName(testName) + "-CL";
        String folderName = getFolderName(testName);

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // Login as cloudUser (Cloud) and create a site, a folder within the site
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
        ShareUserSitePage.createFolder(hybridDrone, folderName, folderName);
        ShareUser.logout(hybridDrone);
    }

    @Test (groups="Hybrid", enabled = true)
    public void AONE_15594() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName + "opUser", testDomain);
        String cloudSite = getSiteName(testName) + "-CL";
        String folderName = getFolderName(testName);

        try
        {
            // Login as OP user
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            // Start Simple Cloud Task Workflow
            CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startSimpleCloudTaskWorkFlow(drone);

            Assert.assertTrue(cloudTaskOrReviewPage.isSimpleCloudTaskElementsPresent(), "Verifying Simple Cloud Task fields");

            // Verify Destination Network, Site and Folder default values are "(None)"
            Assert.assertEquals(cloudTaskOrReviewPage.getDestinationNetwork(), NONE, "Verifying Destination Network default value is set to None");
            Assert.assertEquals(cloudTaskOrReviewPage.getDestinationSite(), NONE, "Verifying Destination Site default value is set to None");
            Assert.assertEquals(cloudTaskOrReviewPage.getDestinationFolder(), NONE, "Verifying Destination Folder default value is set to None");

            // Verify the Select Assignee button is disabled when the destination is not chosen and no assignee is displayed
            Assert.assertFalse(cloudTaskOrReviewPage.isSelectAssigneeButtonEnabled(), "Verifying the Select Assignee button is disabled when the destination is not chosen");
            Assert.assertFalse(cloudTaskOrReviewPage.isAssigneePresent(), "Verifying the Assignee is not present");
            
            // Select Destination And Assignee
            DestinationAndAssigneePage destinationAndAssigneePage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();

            // Verify Destination and Assignee title
            Assert.assertEquals(destinationAndAssigneePage.getSyncToCloudTitle(), "Select destination for documents on Cloud");

            // Verify Destination Network, Site, Folder are displayed
            Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(testDomain), "Verifying Cloud Network is displayed");
            destinationAndAssigneePage.selectNetwork(testDomain);

            Assert.assertTrue(destinationAndAssigneePage.isSiteDisplayed(cloudSite), "Verifying Cloud Site is displayed");
            destinationAndAssigneePage.selectSite(cloudSite);
            Assert.assertTrue(destinationAndAssigneePage.isFolderDisplayed(DEFAULT_FOLDER_NAME), "Verifying Default Folder (Documents) is displayed");
            Assert.assertTrue(destinationAndAssigneePage.isFolderDisplayed(folderName), "Verifying Folder created under Documents is displayed");
            destinationAndAssigneePage.selectFolder(folderName);

            // Select Close and verify the Destination is not updated
            destinationAndAssigneePage.selectCloseButton();

            ShareUser.logout(drone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
        }
    }


    @Test(groups="DataPrepHybrid")
    public void dataPrep_15595() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] {user1};

        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
        String[] cloudUserInfo1 = new String[] {cloudUser};

        String cloudSite = getSiteName(testName) + "-CL";
        String folderName = getFolderName(testName);

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // Login as cloudUser (Cloud) and create a site, a folder within the site
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
        ShareUserSitePage.createFolder(hybridDrone, folderName, folderName);
        ShareUser.logout(hybridDrone);
    }

    @Test (groups="Hybrid", enabled = true)
    public void AONE_15595() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName + "opUser", testDomain);
        String cloudSite = getSiteName(testName) + "-CL";
        String folderName = getFolderName(testName);

        try
        {
            // Login as OP user
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            //1. Choose 'Cloud Review Task' in the Type drop-down list.
            CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startCloudReviewTaskWorkFlow(drone);
            Assert.assertTrue(cloudTaskOrReviewPage.isCloudReviewTaskElementsPresent(), "Verifying Cloud Review Task fields");

            //2. Verify the Destination and Assignee section.
            //Network field
            //Site field
            //Folder field
            //Reviewers field
            //Select Reviewers button
            //Required approval percentage field
            //Help icon of the Required approval percentage field
            //3. Verify the Network, Site and Folder fields (none value).
            //5. Verify the Assignment field.
            //6. Verify the Select Reviewers button
            Assert.assertEquals(cloudTaskOrReviewPage.getDestinationNetwork(), NONE, "Verifying Destination Network default value is set to None");
            Assert.assertEquals(cloudTaskOrReviewPage.getDestinationSite(), NONE, "Verifying Destination Site default value is set to None");
            Assert.assertEquals(cloudTaskOrReviewPage.getDestinationFolder(), NONE, "Verifying Destination Folder default value is set to None");

            Assert.assertTrue(cloudTaskOrReviewPage.isRequiredApprovalPercentageFieldPresent(), "Required Approval Percentage Field is not present");
            Assert.assertFalse(cloudTaskOrReviewPage.isSelectReviewersButtonEnabled(), "Verifying the Select Reviewers button is disabled when the destination is not chosen");
            Assert.assertFalse(cloudTaskOrReviewPage.isReviewersPresent(), "Verifying the Reviewers not present");

            Assert.assertEquals(cloudTaskOrReviewPage.getRequiredApprovalPercentageField(), "50", "Incorrect value in Required Approval Percentage Field");
            //7. Click on Help icon of the Required approval percentage field.
            Assert.assertEquals(cloudTaskOrReviewPage.getRequiredApprovalPercentageHelpText(), APPROVAL_PERCENTAGE_HELP_TEXT, "Verify Approval Percentage Help Text");

            //4. Click on Select Destination button.
            DestinationAndAssigneePage destinationAndAssigneePage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();
            // Verify Destination and Assignee title
            Assert.assertEquals(destinationAndAssigneePage.getSyncToCloudTitle(), "Select destination for documents on Cloud");
            // Verify Destination Network, Site, Folder are displayed
            Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(testDomain), "Verifying Cloud Network is displayed");
            destinationAndAssigneePage.selectNetwork(testDomain);
            Assert.assertTrue(destinationAndAssigneePage.isSiteDisplayed(cloudSite), "Verifying Cloud Site is displayed");
            destinationAndAssigneePage.selectSite(cloudSite);
            Assert.assertTrue(destinationAndAssigneePage.isFolderDisplayed(DEFAULT_FOLDER_NAME), "Verifying Default Folder (Documents) is displayed");
            Assert.assertTrue(destinationAndAssigneePage.isFolderDisplayed(folderName), "Verifying Folder created under Documents is displayed");
            destinationAndAssigneePage.selectFolder(folderName);

            // Select Close and verify the Destination is not updated
            destinationAndAssigneePage.selectCloseButton();
            ShareUser.logout(drone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
        }
    }

}