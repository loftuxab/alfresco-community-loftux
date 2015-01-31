package org.alfresco.share.workflow;

import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.CopyOrMoveContentPage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.site.document.EditTextDocumentPage;
import org.alfresco.po.share.task.TaskStatus;
import org.alfresco.po.share.workflow.CloudTaskOrReviewPage;
import org.alfresco.po.share.workflow.KeepContentStrategy;
import org.alfresco.po.share.workflow.Priority;
import org.alfresco.po.share.workflow.TaskType;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.EditTaskAction;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class CompletedWorkflowSyncedTests extends AbstractWorkflow
{
    private String testDomain;
    private String completeWorkflow = "complete_workflow";
    private static Log logger = LogFactory.getLog(CompletedWorkflowSyncedTests.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testDomain = DOMAIN_HYBRID;
    }

    private void dataPrep(String testName) throws Exception
    {
        String user1 = getUserNameForDomain(completeWorkflow + "OP", testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(completeWorkflow + "CL", testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, DOMAIN_HYBRID, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
    }

    /**
     * Data preparation for Complete Workflow Synced tests
     */
    @Test(groups = "DataPrepHybrid")
    public void dataPrep_createUsers() throws Exception
    {
        dataPrep(completeWorkflow);
    }

    /**
     * AONE-15690: Modify properties (OP)
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15690() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(completeWorkflow + "OP", testDomain);
        String cloudUser = getUserNameForDomain(completeWorkflow + "CL", testDomain);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "1-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "1-CL";

        String simpleTaskFile = getFileName(testName) + ".txt";
        String[] fileInfo = { simpleTaskFile, DOCLIB };

        String simpleTaskWF = testName + System.currentTimeMillis() + "-WF";
        String dueDate = getDueDateString();

        String modifiedTitle = testName + "modified";
        String modifiedDescription = simpleTaskFile + " modified";

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Create Site
        SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();

        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        // Select "Cloud Task or Review" from select a workflow drop down
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, simpleTaskFile).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);
        formDetails.setMessage(simpleTaskWF);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);

        cloudTaskOrReviewPage.startWorkflow(formDetails).render(maxWaitTimeCloudSync);
        waitForSync(simpleTaskFile, opSiteName);

        completeWorkflow( simpleTaskWF, user1, cloudUser);

        // ---- Step 1 ----
        // ---- Step action ---
        // OP Modify the synced document's properties, e.g. change title and description.
        // ---- Expected results ----
        // The properties are changed successfully.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        EditDocumentPropertiesPage editDocumentProperties = ShareUserSitePage.getEditPropertiesFromDocLibPage(drone, opSiteName, simpleTaskFile);
        editDocumentProperties.setDocumentTitle(modifiedTitle + user1);
        editDocumentProperties.setDescription(modifiedDescription + user1);
        editDocumentProperties.selectSave().render();

        // DocumentLibraryPage docLib = ShareUser.openSitesDocumentLibrary(drone, opSiteName);
        ShareUser.openSiteDashboard(drone, opSiteName).render();
        DocumentLibraryPage docLib = ShareUser.openDocumentLibrary(drone).render();
        docLib.getFileDirectoryInfo(simpleTaskFile).selectRequestSync().render();
        waitForSync(simpleTaskFile, opSiteName);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // ---- Step 3 ----
        // ---- Step action ---
        // Cloud Verify the document.
        // ---- Expected results ----
        // Changes appeared to Cloud. The changed content is correctly displayed.
        // ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSite).render();
        int counter = 1;
        int retryRefreshCount = 4;
        EditDocumentPropertiesPage editDocumentPropertiescl = new EditDocumentPropertiesPage(hybridDrone);
        while (counter <= retryRefreshCount)
        {
            editDocumentPropertiescl = ShareUserSitePage.getEditPropertiesFromDocLibPage(hybridDrone, cloudSite, simpleTaskFile).render(maxWaitTime);
            if (!editDocumentPropertiescl.getDocumentTitle().isEmpty())
            {
                break;
            }
            else
            {
                logger.info("Wait a few seconds for the data to be synced into OP");
                Thread.sleep(10000);
                counter++;
            }
        }

        Assert.assertTrue((modifiedTitle + user1).equals(editDocumentPropertiescl.getDocumentTitle()),
                "Document Title modified by OP User is not present for Cloud.");
        Assert.assertTrue((modifiedDescription + user1).equals(editDocumentPropertiescl.getDescription()),
                "Document Description modified by OP User is not present for Cloud.");

    }

    /**
     * AONE-15691: Modify properties (Cloud)
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15691() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(completeWorkflow + "OP", testDomain);
        String cloudUser = getUserNameForDomain(completeWorkflow + "CL", testDomain);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "1-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "1-CL";

        String simpleTaskFile = getFileName(testName) + ".txt";
        String[] fileInfo = { simpleTaskFile, DOCLIB };

        String simpleTaskWF = testName + System.currentTimeMillis() + "-WF";
        String dueDate = getDueDateString();

        String modifiedTitle = testName + "modified";
        String modifiedDescription = simpleTaskFile + " modified";

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Create Site
        SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC).render();

        // Open Document library, Upload a file
        siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();

        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        // Select "Cloud Task or Review" from select a workflow drop down
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, simpleTaskFile).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);
        formDetails.setMessage(simpleTaskWF);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);

        cloudTaskOrReviewPage.startWorkflow(formDetails).render(maxWaitTimeCloudSync);
        waitForSync(simpleTaskFile, opSiteName);

        ShareUser.logout(drone);

        completeWorkflow( simpleTaskWF, user1, cloudUser);

        // ---- Step 1 ----
        // ---- Step action ---
        // Cloud Modify the synced document's properties, e.g. change title and description.
        // ---- Expected results ----
        // The properties are changed successfully.
        EditDocumentPropertiesPage editDocumentProperties = ShareUserSitePage.getEditPropertiesFromDocLibPage(hybridDrone, cloudSite, simpleTaskFile);
        editDocumentProperties.setDocumentTitle(modifiedTitle + cloudUser);
        editDocumentProperties.setDescription(modifiedDescription + cloudUser);
        editDocumentProperties.selectSave().render();

        DocumentLibraryPage cldocumentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSite);
        cldocumentLibraryPage.selectFile(simpleTaskFile);
        editDocumentProperties = ShareUserSitePage.getEditPropertiesFromDocLibPage(hybridDrone, cloudSite, simpleTaskFile);
        Assert.assertTrue((modifiedTitle + cloudUser).equals(editDocumentProperties.getDocumentTitle()),
                "Document Title modified by CL User is not present for Cloud.");

        ShareUser.logout(hybridDrone);
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // ---- Step 3 ----
        // ---- Step action ---
        // Cloud Verify the document.
        // ---- Expected results ----
        // Changes appeared to OP. The changed content is correctly displayed.
        ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

        int counter = 1;
        int retryRefreshCount = 4;

        while (counter <= retryRefreshCount)
        {
            editDocumentProperties = ShareUserSitePage.getEditPropertiesFromDocLibPage(drone, opSiteName, simpleTaskFile).render();
            if (!editDocumentProperties.getDocumentTitle().isEmpty())
            {
                break;
            }
            else
            {
                logger.info("Wait a few seconds for the data to be synced into OP");
                Thread.sleep(10000);
                counter++;
            }
        }

        Assert.assertTrue((modifiedTitle + cloudUser).equals(editDocumentProperties.getDocumentTitle()),
                "Document Title modified by Cloud User is not present for OP.");
        Assert.assertTrue((modifiedDescription + cloudUser).equals(editDocumentProperties.getDescription()),
                "Document Description modified by CLoud User is not present for OP.");

    }

    /**
     * AONE-15692: Modify content (OP)
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15692() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(completeWorkflow + "OP", testDomain);
        String cloudUser = getUserNameForDomain(completeWorkflow + "CL", testDomain);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "1-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "1-CL";

        String simpleTaskFile = getFileName(testName) + ".txt";
        String[] fileInfo = { simpleTaskFile, DOCLIB };

        String simpleTaskWF = testName + System.currentTimeMillis() + "-WF";
        String dueDate = getDueDateString();

        String modifiedContent = testName + " modified content in OP";

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Create Site
        SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC).render();

        // Open Document library, Upload a file
        siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();

        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo).render();

        // Select "Cloud Task or Review" from select a workflow drop down
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, simpleTaskFile).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);
        formDetails.setMessage(simpleTaskWF);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render(maxWaitTimeCloudSync);
        waitForSync(simpleTaskFile, opSiteName);
        completeWorkflow( simpleTaskWF, user1, cloudUser);

        // ---- Step 1 ----
        // ---- Step action ---
        // OP Modify the synced document's content
        // Cloud Modify the synced document's properties, e.g. change title and description.
        // ---- Expected results ----
        // The content is changed successfully.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(simpleTaskFile);
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setContent(modifiedContent);
        contentDetails.setName(simpleTaskFile);

        EditTextDocumentPage inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        documentDetailsPage = inlineEditPage.save(contentDetails).render();

        ShareUser.openSiteDashboard(drone, opSiteName).render();
        DocumentLibraryPage docLib = ShareUser.openDocumentLibrary(drone).render();
        docLib.getFileDirectoryInfo(simpleTaskFile).selectRequestSync().render();
        waitForSync(simpleTaskFile, opSiteName);

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // ---- Step 3 ----
        // ---- Step action ---
        // Cloud Verify the document.
        // ---- Expected results ----
        // Changes appeared to Cloud. The changed content is correctly displayed.
        DocumentLibraryPage documentLibraryPageCL;
        DocumentDetailsPage documentDetailsPageCL;
        EditTextDocumentPage inlineEditPageCL = new EditTextDocumentPage(hybridDrone);

        int counter = 1;
        int retryRefreshCount = 4;
        while (counter <= retryRefreshCount)
        {
            documentLibraryPageCL = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSite).render();
            documentDetailsPageCL = documentLibraryPageCL.selectFile(simpleTaskFile);
            inlineEditPageCL = documentDetailsPageCL.selectInlineEdit().render();
            if (inlineEditPageCL.getDetails().getContent().contains(modifiedContent))
            {
                break;
            }
            else
            {
                logger.info("Wait a few seconds for the data to be synced into Cloud");
                Thread.sleep(10000);
                counter++;
            }
        }

        Assert.assertTrue(inlineEditPageCL.getDetails().getContent().contains(modifiedContent));
    }

    /**
     * AONE-15683:Incomplete workflow - modify content (Cloud)
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15693() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(completeWorkflow + "OP", testDomain);
        String cloudUser = getUserNameForDomain(completeWorkflow + "CL", testDomain);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "1-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "1-CL";

        String simpleTaskFile = getFileName(testName) + ".txt";
        String[] fileInfo = { simpleTaskFile, DOCLIB };

        String simpleTaskWF = testName + System.currentTimeMillis() + "-WF";
        String dueDate = getDueDateString();

        String modifiedContentOnCloud = testName + "modified content in CLOUD";

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite, AbstractUtils.SITE_VISIBILITY_PUBLIC).render();
        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Create Site
        SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC).render();

        // Open Document library, Upload a file
        siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();

        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo).render();

        // Select "Cloud Task or Review" from select a workflow drop down
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, simpleTaskFile).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);
        formDetails.setMessage(simpleTaskWF);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render(maxWaitTimeCloudSync);

        waitForSync(simpleTaskFile, opSiteName);

        completeWorkflow( simpleTaskWF, user1, cloudUser);

        // ---- Step 1 ----
        // ---- Step action ---
        // Cloud Modify the synced document's content.
        // ---- Expected results ----
        // The content is changed successfully.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSite);
        DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(simpleTaskFile);
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setContent(modifiedContentOnCloud);
        contentDetails.setName(simpleTaskFile);

        EditTextDocumentPage inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        documentDetailsPage = inlineEditPage.save(contentDetails).render();
        ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSite);
        documentLibraryPage.selectFile(simpleTaskFile);
        inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        Assert.assertTrue(inlineEditPage.getDetails().getContent().contains(modifiedContentOnCloud));

        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // ---- Step 3 ----
        // ---- Step action ---
        // Changes appeared to OP. The changed content is correctly displayed.
        // ---- Expected results ----
        // OP Verify the document.
        int counter = 1;
        int retryRefreshCount = 4;

        while (counter <= retryRefreshCount)
        {
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
            documentDetailsPage = documentLibraryPage.selectFile(simpleTaskFile);
            inlineEditPage = documentDetailsPage.selectInlineEdit().render();
            if (inlineEditPage.getDetails().getContent().contains(modifiedContentOnCloud))
            {
                break;
            }
            else
            {
                logger.info("Wait a few seconds for the data to be synced into OP");
                Thread.sleep(10000);
                counter++;
            }
        }

        Assert.assertTrue(inlineEditPage.getDetails().getContent().contains(modifiedContentOnCloud));
    }

    /**
     * AONE-15694: Move (OP)
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15694() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(completeWorkflow + "OP", testDomain);
        String cloudUser = getUserNameForDomain(completeWorkflow + "CL", testDomain);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "1-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "1-CL";
        String folderName = getFolderName(testName);

        String simpleTaskFile = getFileName(testName) + ".txt";
        String[] fileInfo = { simpleTaskFile, DOCLIB };

        String simpleTaskWF = testName + System.currentTimeMillis() + "-WF";
        String dueDate = getDueDateString();

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Create Site
        SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC).render();
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        // Open Document library, Upload a file
        siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();

        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo).render();

        // Select "Cloud Task or Review" from select a workflow drop down
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, simpleTaskFile).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);
        formDetails.setMessage(simpleTaskWF);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render(maxWaitTimeCloudSync);

        waitForSync(simpleTaskFile, opSiteName);

        completeWorkflow( simpleTaskWF, user1, cloudUser);

        // ---- Step 1, 2 ----
        // ---- Step action ---
        // OP Move the synced document to another location.
        // ---- Expected results ----
        // The content is moved successfully.
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        CopyOrMoveContentPage moveToPage = documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).selectMoveTo().render();
        moveToPage.selectPath(folderName).render().selectOkButton().render();
        ShareUser.logout(drone);

        // ---- Step 3 ----
        // ---- Step action ---
        // Cloud Verify the document
        // ---- Expected results ----
        // The document is still synced.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSite).render();
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isCloudSynced());
        ShareUser.logout(hybridDrone);
    }

    /**
     * AONE-15695:Move (Cloud)
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15695() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(completeWorkflow + "OP", testDomain);
        String cloudUser = getUserNameForDomain(completeWorkflow + "CL", testDomain);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "1-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "1-CL";
        String folderName = getFolderName(testName);

        String simpleTaskFile = getFileName(testName) + ".txt";
        String[] fileInfo = { simpleTaskFile, DOCLIB };

        String simpleTaskWF = testName + System.currentTimeMillis() + "-WF";
        String dueDate = getDueDateString();

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUserSitePage.createFolder(hybridDrone, folderName, folderName);
        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Create Site
        SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC).render();

        // Open Document library, Upload a file
        siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();

        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo).render();

        // Select "Cloud Task or Review" from select a workflow drop down
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, simpleTaskFile).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);
        formDetails.setMessage(simpleTaskWF);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render(maxWaitTimeCloudSync);

        waitForSync(simpleTaskFile, opSiteName);
        completeWorkflow( simpleTaskWF, user1, cloudUser);

        // ---- Step 1, 2 ----
        // ---- Step action ---
        // Cloud Move the synced document to another location
        // ---- Expected results ----
        // The content is moved successfully.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSite).render();
        CopyOrMoveContentPage moveToPage = documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).selectMoveTo().render();
        moveToPage.selectPath(folderName).render().selectOkButton().render();
        ShareUser.logout(hybridDrone);

        // ---- Step 3 ----
        // ---- Step action ---
        // OP Verify the document.
        // ---- Expected results ----
        // The document is still synced. Another location is displayed in the Sync details section.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isCloudSynced());
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).clickOnViewCloudSyncInfo().render().getCloudSyncLocation()
                .contains(folderName));
        ShareUser.logout(hybridDrone);
    }

    /**
     * AONE-15696: Remove (OP)
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15696() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(completeWorkflow + "OP", testDomain);
        String cloudUser = getUserNameForDomain(completeWorkflow + "CL", testDomain);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "1-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "1-CL";

        String simpleTaskFile = getFileName(testName) + ".txt";
        String[] fileInfo = { simpleTaskFile, DOCLIB };

        String simpleTaskWF = testName + System.currentTimeMillis() + "-WF";
        String dueDate = getDueDateString();

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Create Site
        SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();

        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        // Select "Cloud Task or Review" from select a workflow drop down
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, simpleTaskFile).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);
        formDetails.setMessage(simpleTaskWF);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        cloudTaskOrReviewPage.startWorkflow(formDetails).render(maxWaitTimeCloudSync);
        waitForSync(simpleTaskFile, opSiteName);
        completeWorkflow( simpleTaskWF, user1, cloudUser);

        // ---- Step 1 ----
        // ---- Step action ---
        // OP Remove the synced document.
        // ---- Expected results ----
        // The content is not removed. It cannot be removed because it is the part of workflow. Friendly behavior occurs (a message).
        // TODO: Modify step 1 in TestLink accordingly to the Assert !
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isDeletePresent());

        ShareUser.logout(drone);
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSite).render();

        // ---- Step 3 ----
        // ---- Step action ---
        // Cloud Verify the document.
        // ---- Expected results ----
        // The document is still synced.
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isCloudSynced());
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isUnSyncFromCloudLinkPresent());
        ShareUser.logout(hybridDrone);
    }

    /**
     * AONE-15697:Remove (Cloud)
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15697() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(completeWorkflow + "OP", testDomain);
        String cloudUser = getUserNameForDomain(completeWorkflow + "CL", testDomain);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "1-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "1-CL";

        String simpleTaskFile = getFileName(testName) + ".txt";
        String[] fileInfo = { simpleTaskFile, DOCLIB };

        String simpleTaskWF = testName + System.currentTimeMillis() + "-WF";
        String dueDate = getDueDateString();

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Create Site
        SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();

        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        // Select "Cloud Task or Review" from select a workflow drop down
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, simpleTaskFile).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);
        formDetails.setMessage(simpleTaskWF);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        cloudTaskOrReviewPage.startWorkflow(formDetails).render(maxWaitTimeCloudSync);
        waitForSync(simpleTaskFile, opSiteName);
        completeWorkflow( simpleTaskWF, user1, cloudUser);

        // ---- Step 1 ----
        // ---- Step action ---
        // Cloud Remove the synced document.
        // ---- Expected results ----
        // The content is not removed. It cannot be removed because it is the part of workflow. Friendly behavior occurs (a message).
        // TODO: Modify step 1 in TestLink accordingly to the Assert !
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSite).render();
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isCloudSynced());
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isDeletePresent());

        ShareUser.logout(hybridDrone);

        // ---- Step 2 ----
        // ---- Step action ---
        // OP Verify the document.
        // ---- Expected results ----
        // The document is still synced. The correct location is displayed in the Sync details section.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        DocumentLibraryPage opDocumentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertTrue(opDocumentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isCloudSynced());
        Assert.assertTrue(opDocumentLibraryPage.getFileDirectoryInfo(simpleTaskFile).clickOnViewCloudSyncInfo().render().getCloudSyncLocation()
                .contains(cloudSite));
    }

    /**
     * AONE-15698:Unsync (OP)
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15698() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(completeWorkflow + "OP", testDomain);
        String cloudUser = getUserNameForDomain(completeWorkflow + "CL", testDomain);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "1-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "1-CL";

        String simpleTaskFile = getFileName(testName) + ".txt";
        String[] fileInfo = { simpleTaskFile, DOCLIB };

        String simpleTaskWF = testName + System.currentTimeMillis() + "-WF";
        String dueDate = getDueDateString();

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Create Site
        SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();

        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        // Select "Cloud Task or Review" from select a workflow drop down
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, simpleTaskFile).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);
        formDetails.setMessage(simpleTaskWF);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        cloudTaskOrReviewPage.startWorkflow(formDetails).render(maxWaitTimeCloudSync);
        waitForSync(simpleTaskFile, opSiteName);
        completeWorkflow( simpleTaskWF, user1, cloudUser);

        // ---- Step 1 ----
        // ---- Step action ---
        // OP Unsync the synced document.
        // ---- Expected results ----
        // The content is not unsynced. Unsync option is absent. It is impossible to unsync the document which is the part of the incomplete workflow.
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isUnSyncFromCloudLinkPresent());

        ShareUser.logout(drone);
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSite).render();

        // ---- Step 3 ----
        // ---- Step action ---
        // Cloud Verify the document.
        // ---- Expected results ----
        // The document is still synced.
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isCloudSynced());
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isUnSyncFromCloudLinkPresent());
        ShareUser.logout(hybridDrone);
    }

    /**
     * AONE-15699:Unsync (Cloud)
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15699() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(completeWorkflow + "OP", testDomain);
        String cloudUser = getUserNameForDomain(completeWorkflow + "CL", testDomain);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "1-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "1-CL";

        String simpleTaskFile = getFileName(testName) + ".txt";
        String[] fileInfo = { simpleTaskFile, DOCLIB };

        String simpleTaskWF = testName + System.currentTimeMillis() + "-WF";
        String dueDate = getDueDateString();

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Create Site
        SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();

        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        // Select "Cloud Task or Review" from select a workflow drop down
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, simpleTaskFile).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);
        formDetails.setMessage(simpleTaskWF);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        cloudTaskOrReviewPage.startWorkflow(formDetails).render(maxWaitTimeCloudSync);
        waitForSync(simpleTaskFile, opSiteName);
        completeWorkflow( simpleTaskWF, user1, cloudUser);

        // ---- Step 1 ----
        // ---- Step action ---
        // Cloud Unsync the synced document
        // ---- Expected results ----
        // The content is not unsynced. Unsync option is absent. It is impossible to unsync the document which is the part of the incomplete workflow
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSite).render();

        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isCloudSynced());
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isUnSyncFromCloudLinkPresent());

        ShareUser.logout(hybridDrone);

        // ---- Step 2 ----
        // ---- Step action ---
        // OP Verify the document.
        // ---- Expected results ----
        // The document is still synced. The correct location is displayed in the Sync details section.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        DocumentLibraryPage opDocumentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertTrue(opDocumentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isCloudSynced());
        Assert.assertTrue(opDocumentLibraryPage.getFileDirectoryInfo(simpleTaskFile).clickOnViewCloudSyncInfo().render().getCloudSyncLocation()
                .contains(cloudSite));
    }

    private void waitForSync(String fileName, String siteName)
    {
        int counter = 1;
        int retryRefreshCount = 4;
        while (counter <= retryRefreshCount)
        {
            if (checkIfContentIsSynced(drone, fileName))
            {
                break;
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
    }

    private void completeWorkflow(String workflowName, String opUser, String clUser)
    {
        // login as cloud user and complete workflow
        SharePage sharePage = ShareUser.login(hybridDrone, clUser, DEFAULT_PASSWORD);
        MyTasksPage myTasksPage = sharePage.getNav().selectMyTasks().render();
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, workflowName, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE).render();
        Assert.assertFalse(myTasksPage.isTaskPresent(workflowName));
        myTasksPage = myTasksPage.selectCompletedTasks().render();
        Assert.assertTrue(myTasksPage.isTaskPresent(workflowName));
        ShareUser.logout(hybridDrone);
        
        // Login as OP user and complete workflow
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(drone).render();
        ShareUser.checkIfTaskIsPresent(drone, workflowName);
        ShareUserWorkFlow.completeWorkFlow(drone, opUser, workflowName).render();
        ShareUser.logout(drone);
        
    }

}
