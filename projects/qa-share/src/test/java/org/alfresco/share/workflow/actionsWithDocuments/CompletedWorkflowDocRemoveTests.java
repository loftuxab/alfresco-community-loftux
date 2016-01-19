/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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

import org.alfresco.po.share.site.document.*;
import org.alfresco.test.FailedTestListener;
import org.alfresco.po.share.workflow.*;
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.WebDrone;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class CompletedWorkflowDocRemoveTests extends AbstractWorkflow
{
    protected String testUser;
    protected String siteName = "";
    DocumentLibraryPage documentLibraryPage;
    protected String testName = "";
    protected String prefixCompleteWithRemoveSync = "WFCompleteRemoveSync";
    protected String prefixCompleteWithRemoveFile = "WFCompleteRemoveFile";
    private String testDomain = "hybrid.test";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testDomain = "hybrid.test";
    }

    protected void dataPrepIncomplete(WebDrone drone, WebDrone hybridDrone, String testName) throws Exception
    {
        String user1 = getUserNameForDomain(testName, testDomain);

        String cloudUser = getUserNameForDomain(testName, testDomain);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String fileName = getFileName(testName) + ".txt";
        String folderName = getFolderName(testName);

        String workFlowName = testName + "-WF1";
        String dueDate = "12/05/2019";

        String[] userInfo1 = new String[] { user1 };
        String[] cloudUserInfo1 = new String[] { cloudUser };
        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUserSitePage.createFolder(hybridDrone, folderName, folderName);
        // ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);
        ShareUser.logout(hybridDrone);

        // User1 starts Workflow
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

        // Start Simple Cloud Task workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startSimpleCloudTaskWorkFlow(drone);
        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(fileName, opSiteName);
        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails);

        ShareUser.logout(drone);

    }

    protected void dataPrepComplete(WebDrone drone, WebDrone hybridDrone, String testName) throws Exception
    {
        dataPrepIncomplete(drone, hybridDrone, testName);
        String cloudUser = getUserNameForDomain(testName, testDomain);
        String user1 = getUserNameForDomain(testName, testDomain);
        String workFlowName = testName + "-WF1";

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(hybridDrone, user1, workFlowName);
        ShareUser.logout(hybridDrone);

        // Login as OP user
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(drone);
        ShareUser.checkIfTaskIsPresent(drone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(drone, user1, workFlowName).render();
        ShareUser.logout(hybridDrone);

    }

/*    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15710() throws Exception
    {
        String testName = getTestName();
        dataPrepComplete(drone, hybridDrone, prefixCompleteWithRemoveFile, testName);
    }*/

    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15710() throws Exception
    {
        String testName = getTestName() + System.currentTimeMillis();
        String user1 = getUserNameForDomain(testName, testDomain);
        String cloudUser = getUserNameForDomain(testName, testDomain);

        String opSiteName = getSiteName(testName) +  "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        String fileName = getFileName(testName) + ".txt";

        String modifiedFileTitle = testName + "modifiedBy " + System.currentTimeMillis();
        String descOfFile = fileName + " modified by " + System.currentTimeMillis();

        dataPrepComplete(drone, hybridDrone, testName);

        // OP Modify the document's properties, e.g. change title and description. The properties are changed successfully.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        EditDocumentPropertiesPage editDocumentProperties = ShareUserSitePage.getEditPropertiesFromDocLibPage(drone, opSiteName, fileName);
        editDocumentProperties.setDocumentTitle(modifiedFileTitle + user1);
        editDocumentProperties.setDescription(descOfFile + user1);
        editDocumentProperties.selectSave().render();

        // Cloud Verify the document. The document is absent.
        ShareUser.logout(drone);
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertFalse(documentLibraryPage.isFileVisible(fileName));
        ShareUser.logout(hybridDrone);
    }

/*    @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15711() throws Exception
    {
        String testName = getTestName();
        dataPrepComplete(drone, hybridDrone, prefixCompleteWithRemoveFile, testName);
    }*/

    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15711() throws Exception
    {
        String testName = getTestName()+System.currentTimeMillis();
        String user1 = getUserNameForDomain(testName, testDomain);
        String cloudUser = getUserNameForDomain(testName, testDomain);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        String fileName = getFileName(testName) + ".txt";

        String modifiedContentByOnPrem = testName + " modifiedBy " + user1;

        dataPrepComplete(drone, hybridDrone, testName);

        // OP Modify the document's content. The content is changed successfully.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(fileName).render();

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setContent(modifiedContentByOnPrem);
        contentDetails.setName(fileName);
        // Select Inline Edit and change the content and save
        EditTextDocumentPage inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        inlineEditPage.save(contentDetails).render();
        ShareUser.logout(drone);

        // Cloud Verify the document. The document is absent.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertFalse(documentLibraryPage.isFileVisible(fileName));
        ShareUser.logout(hybridDrone);
    }

 /*   @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15714() throws Exception
    {
        String testName = getTestName();
        dataPrepComplete(drone, hybridDrone, prefixCompleteWithRemoveFile, testName);
    }*/

    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15714() throws Exception
    {
        String testName = getTestName() + System.currentTimeMillis();
        String user1 = getUserNameForDomain(testName, testDomain);
        String cloudUser = getUserNameForDomain(testName, testDomain);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String fileName = getFileName(testName) + ".txt";

        dataPrepComplete(drone, hybridDrone, testName);

        // OP UnSync the document. UnSync action is not available.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isUnSyncFromCloudLinkPresent());
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced());
        ShareUser.logout(drone);

        // Cloud Verify the document. The document is absent.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertFalse(documentLibraryPage.isFileVisible(fileName));
        ShareUser.logout(hybridDrone);

    }

    /*@Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15712() throws Exception
    {
        String testName = getTestName();
        dataPrepComplete(drone, hybridDrone, prefixCompleteWithRemoveFile, testName);
    }*/

    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15712() throws Exception
    {
        String testName = getTestName() + System.currentTimeMillis();
        String user1 = getUserNameForDomain(testName, testDomain);
        String folderName = getFolderName(testName);
        String cloudUser = getUserNameForDomain( testName, testDomain);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String fileName = getFileName(testName) + ".txt";

        dataPrepComplete(drone, hybridDrone, testName);

        // OP Move the document to another location. The content is moved successfully.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        CopyOrMoveContentPage moveToPage = documentLibraryPage.getFileDirectoryInfo(fileName).selectMoveTo().render();
        moveToPage.selectPath(folderName).render().selectOkButton().render();
        ShareUser.logout(drone);

        // Cloud Verify the document. The document is absent.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertFalse(documentLibraryPage.isFileVisible(fileName));
        ShareUser.logout(hybridDrone);
    }

 /*   @Test(groups = "DataPrepHybrid")
    public void dataPrep_AONE_15713() throws Exception
    {
        String testName = getTestName();

    }*/

    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15713() throws Exception
    {
        String testName = getTestName() + System.currentTimeMillis();
        String user1 = getUserNameForDomain(testName, testDomain);
        String cloudUser = getUserNameForDomain(testName, testDomain);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String fileName = getFileName(testName) + ".txt";

        dataPrepComplete(drone, hybridDrone, testName);

        // OP Remove the document. The document is removed successfully.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        documentLibraryPage.getFileDirectoryInfo(fileName).selectCheckbox();
        documentLibraryPage = ShareUser.deleteSelectedContent(drone).render();
        Assert.assertFalse(documentLibraryPage.isFileVisible(fileName));
        ShareUser.logout(drone);

        // Cloud Verify the document. The document is absent.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertFalse(documentLibraryPage.isFileVisible(fileName));
        ShareUser.logout(hybridDrone);
    }
}
