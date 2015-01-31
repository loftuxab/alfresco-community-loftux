package org.alfresco.share.workflow;

import org.alfresco.po.share.SharePopup;
import org.alfresco.po.share.site.document.*;
import org.alfresco.po.share.workflow.CloudTaskOrReviewPage;
import org.alfresco.po.share.workflow.KeepContentStrategy;
import org.alfresco.po.share.workflow.MyWorkFlowsPage;
import org.alfresco.po.share.workflow.Priority;
import org.alfresco.po.share.workflow.TaskType;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneImpl;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Naved Shah
 */
@Listeners(FailedTestListener.class)
public class HybridWorkflowTest2 extends AbstractWorkflow
{
    private static final Logger logger = Logger.getLogger(HybridWorkflowTest2.class);
    protected String testUser;
    protected String siteName = "";
    DocumentLibraryPage documentLibraryPage;
    protected long maxPageLoadingTime = 20000;
    protected String testName = "";
    protected String prefixIncomplete = "WFInComplete";
    protected String prefixComplete = "WFComplete";
    protected String prefixCompleteWithRemoveSync = "WFCompleteRemoveSync";
    protected String prefixCompleteWithRemoveFile = "WFCompltetRemoveFile";
    private String testDomain = "hybrid.test";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testDomain = "hybrid.test";
    }

    protected void dataprep_Incomplete(WebDrone drone, WebDrone hybridDrone, String prefix, String siteprefix) throws Exception
    {
        testName = this.getClass().getSimpleName();
        String user1 = getUserNameForDomain(prefix + testName, testDomain);

        String cloudUser = getUserNameForDomain(prefix + testName, testDomain);

        String opSiteName = getSiteName(prefix + testName) + siteprefix + "-OP";
        String cloudSiteName = getSiteName(prefix + testName) + siteprefix + "-CL";

        String fileName = getFileName(testName) + ".txt";
        String folderName = getFolderName(testName);

        String workFlowName = prefix + testName + "-WF1";
        String dueDate = "12/05/2015";

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
        //ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);
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
        if (prefixCompleteWithRemoveSync.equals(prefix))
        {
            formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENTREMOVESYNC);
        }
        else if (prefixCompleteWithRemoveFile.equals(prefix))
        {
            formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);
        }
        else
        {
            formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);
        }

        // Start Simple Cloud Task workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startSimpleCloudTaskWorkFlow(drone);
        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(fileName, opSiteName);
        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails);

        ShareUser.logout(drone);

    }

    protected void dataprep_Complete(WebDrone drone, WebDrone hybridDrone, String prefix, String siteprefix) throws Exception
    {
        dataprep_Incomplete(drone, hybridDrone, prefix, siteprefix);
        String cloudUser = getUserNameForDomain(prefix + testName, testDomain);
        String user1 = getUserNameForDomain(prefix + testName, testDomain);
        String workFlowName = prefix + testName + "-WF1";

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

    /**
     * AONE-15686 & ALF-208:Create Simple Cloud Task and update when its
     * incomplete.
     * <ul>
     * <li>1) Login to OP user and upload a file.</li>
     * <li>2) Start work flow with the file created on 1. which will sync file with cloud.</li>
     * <li>3) Go back to file uploaded and try to delete the file.
     * <li>4) Login as CL-User, Open CL site and verfiy no chane happened.
     * </ul>
     */
    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15686() throws Exception
    {
        String testName = getTestName();
        dataprep_Incomplete(drone, hybridDrone, getTestName(), testName);
    }

    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15686() throws Exception
    {
        String testNameSitePrefix = getTestName();
        prefixIncomplete = getTestName();
        String user1 = getUserNameForDomain(prefixIncomplete + testName, testDomain);

        String cloudUser = getUserNameForDomain(prefixIncomplete + testName, testDomain);

        String opSiteName = getSiteName(prefixIncomplete + testName) + testNameSitePrefix + "-OP";
        String cloudSiteName = getSiteName(prefixIncomplete + testName) + testNameSitePrefix + "-CL";

        String fileName = getFileName(testName) + ".txt";

        // Try to remove the synced document. Delete Document link is absent in Document Details page.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName).render();
        Assert.assertFalse(detailsPage.isDeleteDocumentLinkDisplayed(), "It is possible to delete document!");

        // Delete link is absent in Selected Items.
        ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        documentLibraryPage.getFileDirectoryInfo(fileName).selectCheckbox();
        DocumentLibraryNavigation docLibNavOption = documentLibraryPage.getNavigation().render();
        docLibNavOption.clickSelectedItemsButton();
        Assert.assertFalse(docLibNavOption.isDeleteActionForIncompleteWorkflowDocumentPresent(), "It is possible to delete document!");
        ShareUser.logout(drone);

        // Cloud Verify the document
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertTrue(documentLibraryPage.isFileVisible(fileName), "File part of workflow is getting delete due to bug alf-20133");
        ShareUser.logout(hybridDrone);
    }


    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15700() throws Exception
    {
        String testName = getTestName();
        dataprep_Complete(drone, hybridDrone, prefixCompleteWithRemoveSync, testName);
    }

    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15700() throws Exception
    {
        String testNameSitePrefix = getTestName();
        String user1 = getUserNameForDomain(prefixCompleteWithRemoveSync + testName, testDomain);
        String cloudUser = getUserNameForDomain(prefixCompleteWithRemoveSync + testName, testDomain);

        String opSiteName = getSiteName(prefixCompleteWithRemoveSync + testName) + testNameSitePrefix + "-OP";
        String cloudSiteName = getSiteName(prefixCompleteWithRemoveSync + testName) + testNameSitePrefix + "-CL";
        String fileName = getFileName(testName) + ".txt";

        String modifiedFileTitle = testName + "modifiedBy " + System.currentTimeMillis();
        String descOfFile = fileName + " modified by " + System.currentTimeMillis();

        // OP Modify the synced document's properties, e.g. change title and description.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        EditDocumentPropertiesPage editDocumentProperties = ShareUserSitePage.getEditPropertiesFromDocLibPage(drone, opSiteName, fileName);
        editDocumentProperties.setDocumentTitle(modifiedFileTitle + user1 + "OP");
        editDocumentProperties.setDescription(descOfFile + user1 + "OP");
        editDocumentProperties.selectSave().render();
        ShareUser.logout(drone);

        //Cloud Verify the document. Changes didn't appear to Cloud.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        editDocumentProperties = ShareUserSitePage.getEditPropertiesFromDocLibPage(hybridDrone, cloudSiteName, fileName);

        Assert.assertFalse((modifiedFileTitle + user1).equals(editDocumentProperties.getDocumentTitle()),
                "Document Title modified by OP User is not present for Cloud.");
        Assert.assertFalse((descOfFile + user1).equals(editDocumentProperties.getDescription()),
                "Document Description modified by OP User is not present for Cloud.");

        editDocumentProperties.setDocumentTitle(modifiedFileTitle + cloudUser + "CL");
        editDocumentProperties.setDescription(descOfFile + cloudUser + "CL");
        ShareUser.logout(hybridDrone);

        //Verify that changes are not applied in the Share
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        editDocumentProperties = ShareUserSitePage.getEditPropertiesFromDocLibPage(drone, opSiteName, fileName);
        Assert.assertFalse((modifiedFileTitle + cloudUser).equals(editDocumentProperties.getDocumentTitle()),
                "Document Title modified by Cloud User is not present for OP.");
        Assert.assertFalse((descOfFile + cloudUser).equals(editDocumentProperties.getDescription()),
                "Document Description modified by CLoud User is not present for OP.");
        ShareUser.logout(drone);

    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15702() throws Exception
    {
        String testName = getTestName();
        dataprep_Complete(drone, hybridDrone, prefixCompleteWithRemoveSync, testName);
    }

    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15702() throws Exception
    {
        String testNameSitePrefix = getTestName();
        String user1 = getUserNameForDomain(prefixCompleteWithRemoveSync + testName, testDomain);

        String cloudUser = getUserNameForDomain(prefixCompleteWithRemoveSync + testName, testDomain);

        String opSiteName = getSiteName(prefixCompleteWithRemoveSync + testName) + testNameSitePrefix + "-OP";
        String cloudSiteName = getSiteName(prefixCompleteWithRemoveSync + testName) + testNameSitePrefix + "-CL";
        String fileName = getFileName(testName) + ".txt";

        String modifiedContentByOnPrem = testName + " modifiedBy: OP User" + System.currentTimeMillis();
        String modifiedContentByCloud = testName + " modifiedBy: Cloud User" + System.currentTimeMillis();

        //OP Modify the synced document's content. The content is changed successfully.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(fileName);

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setContent(modifiedContentByOnPrem);
        contentDetails.setName(fileName);

        EditTextDocumentPage inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        documentDetailsPage = inlineEditPage.save(contentDetails).render();
        ShareUser.logout(drone);

        //Cloud Verify the document. Changes didn't appear to Cloud.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);
        documentDetailsPage = documentLibraryPage.selectFile(fileName);
        inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        Assert.assertFalse(inlineEditPage.getDetails().getContent().contains(modifiedContentByOnPrem));

        contentDetails.setContent(modifiedContentByCloud);
        documentDetailsPage = inlineEditPage.save(contentDetails).render();
        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);
        documentDetailsPage = documentLibraryPage.selectFile(fileName);
        inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        Assert.assertFalse(inlineEditPage.getDetails().getContent().contains(modifiedContentByCloud));
        ShareUser.logout(drone);
    }

    /**
     * AONE-15704 :Create Simple Cloud Task and update when its incomplete.
     * <ul>
     * <li>1) Login to OP user and upload a file.</li>
     * <li>2) Start work flow with the file created on 1. which will sync file with cloud.</li>
     * <li>3) Go back to file uploaded and try to move the file to different folder.
     * <li>4) Login as CL-User, Open CL site and verfiy location of sync is changed.
     * </ul>
     */
    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15704() throws Exception
    {
        String testName = getTestName();
        dataprep_Complete(drone, hybridDrone, prefixCompleteWithRemoveSync, testName);
    }

    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15704() throws Exception
    {
        String testNameSitePrefix = getTestName();
        String user1 = getUserNameForDomain(prefixCompleteWithRemoveSync + testName, testDomain);
        String folderName = getFolderName(testName);
        String cloudUser = getUserNameForDomain(prefixCompleteWithRemoveSync + testName, testDomain);

        String opSiteName = getSiteName(prefixCompleteWithRemoveSync + testName) + testNameSitePrefix + "-OP";
        String cloudSiteName = getSiteName(prefixCompleteWithRemoveSync + testName) + testNameSitePrefix + "-CL";

        String fileName = getFileName(testName) + ".txt";

        //OP Move the synced document to another location. The content is moved successfully.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        CopyOrMoveContentPage moveToPage = documentLibraryPage.getFileDirectoryInfo(fileName).selectMoveTo().render();
        moveToPage.selectPath(folderName).render().selectOkButton().render();
        ShareUser.logout(drone);

        //Cloud Verify the document. The document is not synced.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced());
        ShareUser.logout(hybridDrone);
    }

    /**
     * AONE-15705 :Create Simple Cloud Task and update when its incomplete.
     * <ul>
     * <li>1) Login to CL user and upload a file.</li>
     * <li>2) Start work flow with the file created on 1. which will sync file with cloud.</li>
     * <li>3) Go back to file uploaded and try to move the file to different folder.
     * <li>4) Login as OP-User, Open OP site and verfiy location of sync is changed.
     * </ul>
     */
    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15705() throws Exception
    {
        String testName = getTestName();
        dataprep_Complete(drone, hybridDrone, prefixCompleteWithRemoveSync, testName);
    }

    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15705() throws Exception
    {
        String testNameSitePrefix = getTestName();
        String user1 = getUserNameForDomain(prefixCompleteWithRemoveSync + testName, testDomain);
        String folderName = getFolderName(testName);
        String cloudUser = getUserNameForDomain(prefixCompleteWithRemoveSync + testName, testDomain);

        String opSiteName = getSiteName(prefixCompleteWithRemoveSync + testName) + testNameSitePrefix + "-OP";
        String cloudSiteName = getSiteName(prefixCompleteWithRemoveSync + testName) + testNameSitePrefix + "-CL";

        String fileName = getFileName(testName) + ".txt";

        // Cloud Move the synced document to another location.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        CopyOrMoveContentPage moveToPage = documentLibraryPage.getFileDirectoryInfo(fileName).selectMoveTo().render();
        moveToPage.selectPath(folderName).render().selectOkButton().render();
        ShareUser.logout(hybridDrone);

        // OP Verify the document. The document is not synced.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced());
        ShareUser.logout(hybridDrone);
    }

    /**
     * AONE-15706 : With complete workflow check for delete content location in
     * On-Prem site.
     */
    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15706() throws Exception
    {
        String testName = getTestName();
        dataprep_Complete(drone, hybridDrone, prefixCompleteWithRemoveSync, testName);
    }

    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15706() throws Exception
    {
        String testNameSitePrefix = getTestName();
        String user1 = getUserNameForDomain(prefixCompleteWithRemoveSync + testName, testDomain);
        String cloudUser = getUserNameForDomain(prefixCompleteWithRemoveSync + testName, testDomain);

        String opSiteName = getSiteName(prefixCompleteWithRemoveSync + testName) + testNameSitePrefix + "-OP";
        String cloudSiteName = getSiteName(prefixCompleteWithRemoveSync + testName) + testNameSitePrefix + "-CL";

        String fileName = getFileName(testName) + ".txt";

        //OP Remove the synced document. The content is removed successfully.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        documentLibraryPage.getFileDirectoryInfo(fileName).selectCheckbox();
        documentLibraryPage = ShareUser.deleteSelectedContent(drone).render();
        Assert.assertFalse(documentLibraryPage.isFileVisible(fileName));
        ShareUser.logout(drone);

        //Cloud Verify the document. The document is not synced.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced());
        ShareUser.logout(hybridDrone);
    }

    /**
     * AONE-15707 : With complete workflow check for delete content location in
     * On-Prem site.
     */
    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15707() throws Exception
    {
        String testName = getTestName();
        dataprep_Complete(drone, hybridDrone, prefixCompleteWithRemoveSync, testName);
    }

    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15707() throws Exception
    {
        String testNameSitePrefix = getTestName();
        String user1 = getUserNameForDomain(prefixCompleteWithRemoveSync + testName, testDomain);
        String cloudUser = getUserNameForDomain(prefixCompleteWithRemoveSync + testName, testDomain);

        String opSiteName = getSiteName(prefixCompleteWithRemoveSync + testName) + testNameSitePrefix + "-OP";
        String cloudSiteName = getSiteName(prefixCompleteWithRemoveSync + testName) + testNameSitePrefix + "-CL";

        String fileName = getFileName(testName) + ".txt";

        //Cloud Remove the synced document. The content is removed.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        documentLibraryPage.getFileDirectoryInfo(fileName).selectCheckbox();
        documentLibraryPage = ShareUser.deleteSelectedContent(hybridDrone).render();
        Assert.assertFalse(documentLibraryPage.isFileVisible(fileName));
        ShareUser.logout(hybridDrone);

        //OP Verify the document. The document is not synced.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced());
        ShareUser.logout(drone);
    }

    /**
     * AONE-15708 : With complete workflow check for unsync content in Cloud
     * site.
     */
    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15708() throws Exception
    {
        String testName = getTestName();
        dataprep_Complete(drone, hybridDrone, prefixCompleteWithRemoveSync, testName);
    }

    @Test(groups = { "Hybrid", "IntermittentBugs" }, enabled = true)
    public void AONE_15708() throws Exception
    {
        String testNameSitePrefix = getTestName();
        String user1 = getUserNameForDomain(prefixCompleteWithRemoveSync + testName, testDomain);
        String cloudUser = getUserNameForDomain(prefixCompleteWithRemoveSync + testName, testDomain);
        String opSiteName = getSiteName(prefixCompleteWithRemoveSync + testName) + testNameSitePrefix + "-OP";
        String cloudSiteName = getSiteName(prefixCompleteWithRemoveSync + testName) + testNameSitePrefix + "-CL";

        String fileName = getFileName(testName) + ".txt";

        //OP Unsync the synced document. Unsync action is not available.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isUnSyncFromCloudLinkPresent());
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced());
        ShareUser.logout(drone);

        //Cloud Verify the document. The document is not synced.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced());
        ShareUser.logout(hybridDrone);
    }

    /**
     * AONE-15709 : With complete workflow check for unsync content in on prem
     * site.
     */
    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15709() throws Exception
    {
        String testName = getTestName();
        dataprep_Complete(drone, hybridDrone, prefixCompleteWithRemoveSync, testName);
    }
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15709() throws Exception
    {
        String testNameSitePrefix = getTestName();
        String user1 = getUserNameForDomain(prefixCompleteWithRemoveSync + testName, testDomain);
        String cloudUser = getUserNameForDomain(prefixCompleteWithRemoveSync + testName, testDomain);

        String opSiteName = getSiteName(prefixCompleteWithRemoveSync + testName) + testNameSitePrefix + "-OP";
        String cloudSiteName = getSiteName(prefixCompleteWithRemoveSync + testName) + testNameSitePrefix + "-CL";

        String fileName = getFileName(testName) + ".txt";

        //Cloud Unsync the synced document. Unsync action is not available.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isUnSyncFromCloudLinkPresent());
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced());
        ShareUser.logout(hybridDrone);

        //OP Verify the document. The document is not synced.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced());
        ShareUser.logout(drone);
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15710() throws Exception
    {
        String testName = getTestName();
        dataprep_Complete(drone, hybridDrone, prefixCompleteWithRemoveFile, testName);
    }

    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15710() throws Exception
    {
        String testNameSitePrefix = getTestName();
        String user1 = getUserNameForDomain(prefixCompleteWithRemoveFile + testName, testDomain);
        String cloudUser = getUserNameForDomain(prefixCompleteWithRemoveFile + testName, testDomain);

        String opSiteName = getSiteName(prefixCompleteWithRemoveFile + testName) + testNameSitePrefix + "-OP";
        String cloudSiteName = getSiteName(prefixCompleteWithRemoveFile + testName) + testNameSitePrefix + "-CL";
        String fileName = getFileName(testName) + ".txt";

        String modifiedFileTitle = testName + "modifiedBy " + System.currentTimeMillis();
        String descOfFile = fileName + " modified by " + System.currentTimeMillis();

        //OP Modify the document's properties, e.g. change title and description. The properties are changed successfully.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        EditDocumentPropertiesPage editDocumentProperties = ShareUserSitePage.getEditPropertiesFromDocLibPage(drone, opSiteName, fileName);
        editDocumentProperties.setDocumentTitle(modifiedFileTitle + user1);
        editDocumentProperties.setDescription(descOfFile + user1);
        editDocumentProperties.selectSave().render();

        //Cloud Verify the document. The document is absent.
        ShareUser.logout(drone);
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertFalse(documentLibraryPage.isFileVisible(fileName));
        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15711() throws Exception
    {
        String testName = getTestName();
        dataprep_Complete(drone, hybridDrone, prefixCompleteWithRemoveFile, testName);
    }

    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15711() throws Exception
    {
        String testNameSitePrefix = getTestName();
        String user1 = getUserNameForDomain(prefixCompleteWithRemoveFile + testName, testDomain);
        String cloudUser = getUserNameForDomain(prefixCompleteWithRemoveFile + testName, testDomain);

        String opSiteName = getSiteName(prefixCompleteWithRemoveFile + testName) + testNameSitePrefix + "-OP";
        String cloudSiteName = getSiteName(prefixCompleteWithRemoveFile + testName) + testNameSitePrefix + "-CL";
        String fileName = getFileName(testName) + ".txt";

        String modifiedContentByOnPrem = testName + " modifiedBy " + user1;

        //OP Modify the document's content. The content is changed successfully.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(fileName);

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setContent(modifiedContentByOnPrem);
        contentDetails.setName(fileName);
        // Select Inline Edit and change the content and save
        EditTextDocumentPage inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        documentDetailsPage = inlineEditPage.save(contentDetails).render();
        ShareUser.logout(drone);

        // Cloud Verify the document. The document is absent.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertFalse(documentLibraryPage.isFileVisible(fileName));
        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15714() throws Exception
    {
        String testName = getTestName();
        dataprep_Complete(drone, hybridDrone, prefixCompleteWithRemoveFile, testName);
    }

    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15714() throws Exception
    {
        String testNameSitePrefix = getTestName();
        String user1 = getUserNameForDomain(prefixCompleteWithRemoveFile + testName, testDomain);
        String cloudUser = getUserNameForDomain(prefixCompleteWithRemoveFile + testName, testDomain);

        String opSiteName = getSiteName(prefixCompleteWithRemoveFile + testName) + testNameSitePrefix + "-OP";
        String cloudSiteName = getSiteName(prefixCompleteWithRemoveFile + testName) + testNameSitePrefix + "-CL";

        String fileName = getFileName(testName) + ".txt";

        //OP Unsync the document. Unsync action is not available.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isUnSyncFromCloudLinkPresent());
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced());
        ShareUser.logout(drone);

        //Cloud Verify the document. The document is absent.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertFalse(documentLibraryPage.isFileVisible(fileName));
        ShareUser.logout(hybridDrone);

    }


    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15712() throws Exception
    {
        String testName = getTestName();
        dataprep_Complete(drone, hybridDrone, prefixCompleteWithRemoveFile, testName);
    }

    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15712() throws Exception
    {
        String testNameSitePrefix = getTestName();
        String user1 = getUserNameForDomain(prefixCompleteWithRemoveFile + testName, testDomain);
        String folderName = getFolderName(testName);
        String cloudUser = getUserNameForDomain(prefixCompleteWithRemoveFile + testName, testDomain);

        String opSiteName = getSiteName(prefixCompleteWithRemoveFile + testName) + testNameSitePrefix + "-OP";
        String cloudSiteName = getSiteName(prefixCompleteWithRemoveFile + testName) + testNameSitePrefix + "-CL";

        String fileName = getFileName(testName) + ".txt";

        //OP Move the document to another location. The content is moved successfully.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        CopyOrMoveContentPage moveToPage = documentLibraryPage.getFileDirectoryInfo(fileName).selectMoveTo().render();
        moveToPage.selectPath(folderName).render().selectOkButton().render();
        ShareUser.logout(drone);

        //Cloud Verify the document. The document is absent.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertFalse(documentLibraryPage.isFileVisible(fileName));
        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15713() throws Exception
    {
        String testName = getTestName();
        dataprep_Complete(drone, hybridDrone, prefixCompleteWithRemoveFile, testName);
    }

    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15713() throws Exception
    {
        String testNameSitePrefix = getTestName();
        String user1 = getUserNameForDomain(prefixCompleteWithRemoveFile + testName, testDomain);
        String cloudUser = getUserNameForDomain(prefixCompleteWithRemoveFile + testName, testDomain);

        String opSiteName = getSiteName(prefixCompleteWithRemoveFile + testName) + testNameSitePrefix + "-OP";
        String cloudSiteName = getSiteName(prefixCompleteWithRemoveFile + testName) + testNameSitePrefix + "-CL";

        String fileName = getFileName(testName) + ".txt";

        //OP Remove the document. The document is removed successfully.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        documentLibraryPage.getFileDirectoryInfo(fileName).selectCheckbox();
        documentLibraryPage = ShareUser.deleteSelectedContent(drone).render();
        Assert.assertFalse(documentLibraryPage.isFileVisible(fileName));
        ShareUser.logout(drone);

        //Cloud Verify the document. The document is absent.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertFalse(documentLibraryPage.isFileVisible(fileName));
        ShareUser.logout(hybridDrone);
    }
}
