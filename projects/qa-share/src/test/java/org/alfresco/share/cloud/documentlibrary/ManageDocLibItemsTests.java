package org.alfresco.share.cloud.documentlibrary;

import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.document.TreeMenuNavigation;
import org.alfresco.po.share.workflow.CloudTaskOrReviewPage;
import org.alfresco.po.share.workflow.KeepContentStrategy;
import org.alfresco.po.share.workflow.Priority;
import org.alfresco.po.share.workflow.TaskType;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.share.site.document.DocumentDetailsActionsTest;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author bogdan.bocancea
 */

@Listeners(FailedTestListener.class)
public class ManageDocLibItemsTests extends AbstractWorkflow
{
    private static Log logger = LogFactory.getLog(DocumentDetailsActionsTest.class);
    protected String testUser;
    protected String siteName = "";
    private static DocumentLibraryPage documentLibPage;
    String testDomain;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName() + System.currentTimeMillis();

        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("Start Tests in: " + testName);
        testDomain = DOMAIN_HYBRID;
    }


    public void dataPrep_12567() throws Exception
    {
        String testUser = getUserNameFreeDomain(testName);
        String testUser2 = getUserNameFreeDomain(testName) + "2";
        String siteName = getSiteName(testName);
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "1-CL";
        String dueDate = getDueDateString();
        String simpleTaskWF = testName + System.currentTimeMillis() + "-WF";

        // User 1
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User 2
        String[] testUserInfo2 = new String[] { testUser2 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

        String cloudUser = getUserNameForDomain(testName + "CL", testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, DOMAIN_HYBRID, "1000");

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render(maxWaitTime);

        // invite user2 to site
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUser2, siteName, UserRole.COLLABORATOR);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Upload File
        String fileName1 = getFileName(testName) + "1.txt";
        String[] fileInfo1 = { fileName1, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo1).render();

        String fileName2 = getFileName(testName) + "2.txt";
        String[] fileInfo2 = { fileName2, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo2).render();

        String fileName3 = getFileName(testName) + "3.txt";
        String[] fileInfo3 = { fileName3, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo3).render();

        FileDirectoryInfo fileInfoDir = ShareUserSitePage.getFileDirectoryInfo(drone, fileName1);

        // select favorite for file1
        fileInfoDir.selectFavourite();

        // edit offline first document
        fileInfoDir.selectEditOfflineAndCloseFileWindow().render();

        // select favorite for File2
        fileInfoDir = ShareUserSitePage.getFileDirectoryInfo(drone, fileName2);
        fileInfoDir.selectFavourite();

        ShareUser.logout(drone);

        // Login user 2
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        ShareUser.selectHomeNetwork(drone, testUser);
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // edit offline
        fileInfoDir = ShareUserSitePage.getFileDirectoryInfo(drone, fileName3);
        fileInfoDir.selectEditOfflineAndCloseFileWindow().render();
        fileInfoDir = ShareUserSitePage.getFileDirectoryInfo(drone, fileName2);
        fileInfoDir.selectFavourite();

        ShareUser.logout(drone);
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // sync a document to cloud
        // Select "Cloud Task or Review" from select a workflow drop down
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName2).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);
        formDetails.setMessage(simpleTaskWF);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);

        cloudTaskOrReviewPage.startWorkflow(formDetails).render(maxWaitTimeCloudSync);
        waitForSync(fileName2);
        ShareUser.logout(drone);

    }

    @Test(groups = "Hybrid", enabled = true)
    public void AONE_12567() throws Exception
    {
        String fileName1 = getFileName(testName) + "1.txt";
        String fileName2 = getFileName(testName) + "2.txt";
        String fileName3 = getFileName(testName) + "3.txt";
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        dataPrep_12567();

        // login user 1
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();

        // navigate to document library
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        TreeMenuNavigation treeMenuNavigation = documentLibPage.getLeftMenus().render();

        // 1. Click the All documents view
        treeMenuNavigation.selectDocumentNode(TreeMenuNavigation.DocumentsMenu.ALL_DOCUMENTS).render();

        // 1. All the items in the Document Library are displayed;
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, fileName1, TreeMenuNavigation.DocumentsMenu.ALL_DOCUMENTS, true), fileName1
                + " cannot be found.");
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, fileName2, TreeMenuNavigation.DocumentsMenu.ALL_DOCUMENTS, true), fileName2
                + " cannot be found.");
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, fileName3, TreeMenuNavigation.DocumentsMenu.ALL_DOCUMENTS, true), fileName3
                + " cannot be found.");

        // 2. Click Locate File action from More+ menu for one of documents;
        FileDirectoryInfo fileInfoDir1 = ShareUserSitePage.getFileDirectoryInfo(drone, fileName1);
        fileInfoDir1.selectLocateFile();

        // 2. The folder where the file is located is opened. The file is displayed;
        assertTrue(documentLibPage.isFileVisible(fileName1));

        // 3. Click the I'm Editing view;
        treeMenuNavigation.selectDocumentNode(TreeMenuNavigation.DocumentsMenu.IM_EDITING).render();
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, fileName1, TreeMenuNavigation.DocumentsMenu.IM_EDITING, true), fileName1
                + " cannot be found.");
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, fileName2, TreeMenuNavigation.DocumentsMenu.IM_EDITING, false), fileName2
                + " cannot be found.");

        // 4. Click Locate File action from More+ menu for one of documents;
        fileInfoDir1 = ShareUserSitePage.getFileDirectoryInfo(drone, fileName1);
        fileInfoDir1.selectLocateFile();

        // 4. The folder where the file is located is opened. The file is displayed;
        assertTrue(documentLibPage.isFileVisible(fileName1));

        // 5. Click the Others are Editing view
        treeMenuNavigation.selectDocumentNode(TreeMenuNavigation.DocumentsMenu.OTHERS_EDITING).render();
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, fileName1, TreeMenuNavigation.DocumentsMenu.OTHERS_EDITING, false), fileName1
                + " cannot be found.");
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, fileName3, TreeMenuNavigation.DocumentsMenu.OTHERS_EDITING, true), fileName3
                + " cannot be found.");

        // 6. Click Locate File action from More+ menu for one of documents;
        fileInfoDir1 = ShareUserSitePage.getFileDirectoryInfo(drone, fileName3);
        fileInfoDir1.selectLocateFile();
        documentLibPage.render();
        assertTrue(documentLibPage.isFileVisible(fileName3));

        // 7. Click the Recently Modified view;
        treeMenuNavigation.selectDocumentNode(TreeMenuNavigation.DocumentsMenu.RECENTLY_MODIFIED).render();
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, fileName1, TreeMenuNavigation.DocumentsMenu.RECENTLY_MODIFIED, true), fileName1
                + " cannot be found.");
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, fileName3, TreeMenuNavigation.DocumentsMenu.RECENTLY_MODIFIED, true), fileName3
                + " cannot be found.");

        // 8. Click Locate File action from More+ menu for one of documents;
        fileInfoDir1 = ShareUserSitePage.getFileDirectoryInfo(drone, fileName3);
        fileInfoDir1.selectLocateFile();
        assertTrue(documentLibPage.isFileVisible(fileName3));

        // 9. Click the Recently Added view;
        treeMenuNavigation.selectDocumentNode(TreeMenuNavigation.DocumentsMenu.RECENTLY_ADDED).render();
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, fileName1, TreeMenuNavigation.DocumentsMenu.RECENTLY_ADDED, true), fileName1
                + " cannot be found.");
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, fileName2, TreeMenuNavigation.DocumentsMenu.RECENTLY_ADDED, true), fileName2
                + " cannot be found.");
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, fileName3, TreeMenuNavigation.DocumentsMenu.RECENTLY_ADDED, true), fileName3
                + " cannot be found.");

        // 10. Click Locate File action from More+ menu for one of documents;
        fileInfoDir1 = ShareUserSitePage.getFileDirectoryInfo(drone, fileName3);
        fileInfoDir1.selectLocateFile();
        assertTrue(documentLibPage.isFileVisible(fileName3));

        // 11. Click the My Favorites view;
        treeMenuNavigation.selectDocumentNode(TreeMenuNavigation.DocumentsMenu.MY_FAVORITES).render();
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, fileName1, TreeMenuNavigation.DocumentsMenu.MY_FAVORITES, true), fileName1
                + " cannot be found.");
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, fileName2, TreeMenuNavigation.DocumentsMenu.MY_FAVORITES, true), fileName2
                + " cannot be found.");
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, fileName3, TreeMenuNavigation.DocumentsMenu.MY_FAVORITES, false), fileName3
                + " cannot be found.");

        // 12. Click Locate File action from More+ menu for one of documents;
        fileInfoDir1 = ShareUserSitePage.getFileDirectoryInfo(drone, fileName2);
        fileInfoDir1.selectLocateFile();
        assertTrue(documentLibPage.isFileVisible(fileName2));

        // 13. Click the Synced content view
        treeMenuNavigation.selectDocumentNode(TreeMenuNavigation.DocumentsMenu.SYNCED_CONTENT).render();
        
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, fileName2, TreeMenuNavigation.DocumentsMenu.SYNCED_CONTENT, true), fileName1
                + " cannot be found.");
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, fileName1, TreeMenuNavigation.DocumentsMenu.SYNCED_CONTENT, false), fileName1
                + " cannot be found.");
        
        // 14. Click Locate File action from More+ menu for one of folders.
        fileInfoDir1 = ShareUserSitePage.getFileDirectoryInfo(drone, fileName2);
        fileInfoDir1.selectLocateFile();
        assertTrue(documentLibPage.isFileVisible(fileName2));
    }

    private void waitForSync(String fileName)
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
            }
        }
    }
}
