package org.alfresco.share.workflow;

import org.alfresco.po.share.site.document.*;
import org.alfresco.po.share.workflow.*;
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
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
    protected String testUser;
    protected String siteName = "";
    DocumentLibraryPage documentLibraryPage;
    protected String testName = "";
    protected String prefixIncomplete = "WFInComplete";
    protected String prefixCompleteWithRemoveSync = "WFCompleteRemoveSync";
    protected String prefixCompleteWithRemoveFile = "WFCompleteRemoveFile";
    private String testDomain = "hybrid.test";

    //
    // tests: 15700, 15702, 15704, 15705, 15706, 15707, 15708, 15709
    // were removed from the suite, because the tests were already added (and reviewed) to CompletedWorkflowNotSyncedTest.java
    // tests: 15710, 15711, 15712, 15713, 15714 have been moved to CompletedWorkflowDocRemoveTests.java
    // because CompletedWorkflowDocRemoveTests were already reviewed
    // please see QA-954

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testDomain = "hybrid.test";
    }

    protected void dataPrepIncomplete(WebDrone drone, WebDrone hybridDrone, String prefix, String sitePrefix) throws Exception
    {
        testName = this.getClass().getSimpleName();
        String user1 = getUserNameForDomain(prefix + testName, testDomain);

        String cloudUser = getUserNameForDomain(prefix + testName, testDomain);

        String opSiteName = getSiteName(prefix + testName) + sitePrefix + "-OP";
        String cloudSiteName = getSiteName(prefix + testName) + sitePrefix + "-CL";

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

    /**
     * AONE-15686 & ALF-208:Create Simple Cloud Task and update when its
     * incomplete.
     * <ul>
     * <li>1) Login to OP user and upload a file.</li>
     * <li>2) Start work flow with the file created on 1. which will sync file with cloud.</li>
     * <li>3) Go back to file uploaded and try to delete the file.
     * <li>4) Login as CL-User, Open CL site and verify no chane happened.
     * </ul>
     */
    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15686() throws Exception
    {
        String testName = getTestName();
        dataPrepIncomplete(drone, hybridDrone, getTestName(), testName);
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
}
