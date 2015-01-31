package org.alfresco.share.cloudsync;

import org.alfresco.po.share.site.DestinationAndAssigneeBean;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.SyncInfoPage;
import org.alfresco.po.share.user.CloudSignInPage;
import org.alfresco.po.share.user.CloudSyncPage;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.share.util.AbstractCloudSyncTest;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.alfresco.po.share.site.document.ContentType.PLAINTEXT;
import static org.alfresco.po.share.site.document.ContentType.XML;
import static org.alfresco.share.util.ShareUser.*;
import static org.testng.Assert.assertTrue;

/**
 * @author Marina.Nenadovets
 */
@Listeners(FailedTestListener.class)
public class CloudSyncSmokeTest extends AbstractCloudSyncTest
{
    private static Log logger = LogFactory.getLog(CloudSyncSmokeTest.class);

    protected static String fileName;
    protected static String fileNamePlain, fileNameXml, editedFileNamePlain, editedContentXml, siteA, siteB;
    protected static String testName;
    protected static String folderName;
    protected static int retryCount;
    protected static long timeToWait;
    protected static String syncLocation;
    protected static DestinationAndAssigneeBean desAndAssBean;
    DocumentLibraryPage doclibPrem;
    DocumentLibraryPage doclibCl;
    String [] subFolders = {"subfolder1", "subfolder2"};
    String [] subFiles = {"subFile1", "subFile2"};
    String [] content = {"content1", "content2"};

    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        siteA = getSiteName(testName);
        siteB = getSiteName(testName) + "SY";
        fileName = getFileName(testName);
        folderName = getFolderName(testName);
        fileNamePlain = fileName + "plainText";
        editedFileNamePlain = fileNamePlain + "edited";
        fileNameXml = fileName + "xml";
        editedContentXml = fileNameXml + "edited";
        folderName = getFolderName(testName);
        retryCount = 5;
        timeToWait = 25000;
        syncLocation = DOMAIN_PREMIUM + ">" + siteB + ">" + DEFAULT_FOLDER_NAME;
        desAndAssBean = new DestinationAndAssigneeBean();
        desAndAssBean.setNetwork(hybridDomainPremium);
        desAndAssBean.setSiteName(siteA);
        desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);
    }

    @Test(groups = { "DataPrepHybrid" })
    public void dataPrep_AONE_15428() throws Exception
    {
        ShareUser.login(hybridDrone, adminUserPrem, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, siteA, SITE_VISIBILITY_PUBLIC);
        ShareUser.login(drone, adminUserPrem);

        //Create any site
        ShareUser.createSite(drone, siteA, SITE_VISIBILITY_PUBLIC);
        doclibPrem = openSitesDocumentLibrary(drone, siteA).render();
        createFolderInFolder(drone, folderName, folderName, DOCLIB);
        ContentDetails contentDetails = new ContentDetails();
        for(int i = 0; i < 2; i++)
        {
            createFolderInFolder(drone, subFolders[i], subFolders[i], folderName);
            doclibPrem.getFileDirectoryInfo(subFolders[i]).clickOnTitle();
            contentDetails.setContent(subFiles[i]);
            contentDetails.setName(subFiles[i]);
            contentDetails.setDescription(subFiles[i]);
            createContentInCurrentFolder(drone, contentDetails, ContentType.PLAINTEXT, doclibPrem).render();
            uploadFileInFolder1(drone, content[i], folderName);
        }
    }

    @Test(groups = "Hybrid")
    public void AONE_15428() throws Exception
    {
        String mixedCaseUserName = "uSeRaDmIn@pReMiErNet.tEsT";

        ShareUser.login(drone, adminUserPrem);
        disconnectCloudSync(drone);
        doclibPrem = openSitesDocumentLibrary(drone, siteA).render().getFileDirectoryInfo(folderName).clickOnTitle().render();

        //Click "Sync to Cloud" option for subfolder1. on Autorization screen type username with mixed cases
        CloudSignInPage cloudSignInPage = doclibPrem.getFileDirectoryInfo(subFolders[0]).selectSyncToCloud().render();
        DestinationAndAssigneePage destinationAndAssigneePage = cloudSignInPage.loginAs(mixedCaseUserName, DEFAULT_PASSWORD).render();
        destinationAndAssigneePage.selectNetwork(DOMAIN_PREMIUM);
        destinationAndAssigneePage.selectSite(siteA);
        destinationAndAssigneePage.selectFolder(DOCLIB);
        destinationAndAssigneePage.selectSubmitButtonToSync();
        assertTrue(checkIfContentIsSynced(drone, subFolders[0]), "Folder " + subFolders[0] + " wasn't synced to Cloud");
        doclibPrem.getFileDirectoryInfo(subFolders[0]).clickOnTitle().render();
        assertTrue(checkIfContentIsSynced(drone, subFiles[0]) && doclibPrem.getFileDirectoryInfo(subFiles[0]).isIndirectlySyncedIconPresent(),
            subFiles[0] + " wasn't synced to Cloud");

        //Subfolder1 and subfile are synced to Cloud.
        ShareUser.login(hybridDrone, adminUserPrem);
        doclibCl = openSitesDocumentLibrary(hybridDrone, siteA).render();
        assertTrue(waitAndCheckIfVisible(hybridDrone, doclibCl, subFolders[0]) && checkIfContentIsSynced(hybridDrone, subFolders[0]), "Folder " + subFolders[0]
            + " wasn't synced to Cloud");
        doclibCl.getFileDirectoryInfo(subFolders[0]).clickOnTitle().render();
        assertTrue(doclibCl.isFileVisible(subFiles[0]) && checkIfContentIsSynced(hybridDrone, subFiles[0]) && doclibCl.getFileDirectoryInfo(subFiles[0])
            .isIndirectlySyncedIconPresent(), "File " + subFiles[0] + " wasn't synced to Cloud");

        //Disconnect cloud account from My Profile > Cloud Sync tab
        disconnectCloudSync(drone).render();

        //Navigate to site's DocLib and click "Sync to Cloud" option for content1. on Autorization screen type username with mixed cases
        doclibPrem = openSitesDocumentLibrary(drone, siteA).render().getFileDirectoryInfo(folderName).clickOnTitle().render();
        cloudSignInPage = doclibPrem.getFileDirectoryInfo(content[0]).selectSyncToCloud().render();
        destinationAndAssigneePage = cloudSignInPage.loginAs(mixedCaseUserName, DEFAULT_PASSWORD).render();
        destinationAndAssigneePage.selectNetwork(DOMAIN_PREMIUM);
        destinationAndAssigneePage.selectSite(siteA);
        destinationAndAssigneePage.selectFolder(DOCLIB);
        destinationAndAssigneePage.selectSubmitButtonToSync();
        assertTrue(checkIfContentIsSynced(drone, content[0]), "File " + content[0] + " wasn't synced to Cloud");
        doclibCl = openDocumentLibrary(hybridDrone).render();
        assertTrue(waitAndCheckIfVisible(hybridDrone, doclibCl, content[0]) && checkIfContentIsSynced(hybridDrone, content[0]), "File " + content[0]
            + " wasn't synced to Cloud");

        //Open My Profile > Cloud Sync tab and edit cloud account to account with mixed case
        CloudSyncPage cloudSyncPage = navigateToCloudSync(drone).selectEditButton().loginAs(mixedCaseUserName, DEFAULT_PASSWORD).render();
        assertTrue(cloudSyncPage.isDisconnectButtonDisplayed(), "Cloud account wasn't connected");

        //Navigate to site's DocLib again and click "Sync to Cloud" option for the content2 and subfolder2
        openSitesDocumentLibrary(drone, siteA).render().getFileDirectoryInfo(folderName).clickOnTitle().render();
        DestinationAndAssigneeBean dessAndAssBean = new DestinationAndAssigneeBean();
        dessAndAssBean.setNetwork(DOMAIN_PREMIUM);
        dessAndAssBean.setSiteName(siteA);
        dessAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);
        syncContentToCloud(drone, subFolders[1], desAndAssBean).render();
        assertTrue(checkIfContentIsSynced(drone, subFolders[1]), "Folder " + subFolders[1] + " wasn't synced to Cloud");
        doclibPrem.getFileDirectoryInfo(subFolders[1]).clickOnTitle().render();
        assertTrue(checkIfContentIsSynced(drone, subFiles[1]) && doclibPrem.getFileDirectoryInfo(subFiles[1]).isIndirectlySyncedIconPresent(),
            subFiles[1] + " wasn't synced to Cloud");
        openDocumentLibrary(drone).render().getFileDirectoryInfo(folderName).clickOnTitle().render();
        syncContentToCloud(drone, content[1], desAndAssBean).render();
        assertTrue(checkIfContentIsSynced(drone, content[1]), "File " + content[1] + " wasn't synced to Cloud");

        doclibCl = openSitesDocumentLibrary(hybridDrone, siteA).render();

        assertTrue(waitAndCheckIfVisible(hybridDrone, doclibCl, subFolders[1]) && checkIfContentIsSynced(hybridDrone, subFolders[1]), "Folder " + subFolders[1]
            + " wasn't synced to Cloud");
        doclibCl.getFileDirectoryInfo(subFolders[1]).clickOnTitle().render();

        assertTrue(waitAndCheckIfVisible(hybridDrone, doclibCl, subFiles[1]) && checkIfContentIsSynced(hybridDrone, subFiles[1])
            && doclibCl.getFileDirectoryInfo(subFiles[1]).isIndirectlySyncedIconPresent(), "File " + subFiles[1] + " wasn't synced to Cloud");

        openDocumentLibrary(hybridDrone).render();
        assertTrue(waitAndCheckIfVisible(hybridDrone, doclibCl, content[1]) && checkIfContentIsSynced(hybridDrone, content[1]), "File " + content[1] +
            " wasn't synced to Cloud");
    }

    /**
     * Sync to Cloud
     */
    @Test(groups = { "DataPrepHybrid" })
    public void dataPrep_AONE_15429() throws Exception
    {
        // Login into cloud and create a site
        ShareUser.login(hybridDrone, adminUserPrem, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, siteB, SITE_VISIBILITY_PUBLIC);

        ShareUser.login(drone, adminUserPrem);
        //Authorize a cloud premium account (User Profile > Cloud Sync);
        signInToAlfrescoInTheCloud(drone, adminUserPrem, DEFAULT_PASSWORD);

        //Create any site
        ShareUser.createSite(drone, siteB, SITE_VISIBILITY_PUBLIC);
        openSitesDocumentLibrary(drone, siteB);

        //Create 3 sync sets 1. file sync, 2. file sync, and 3. folder
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setContent(fileNamePlain);
        contentDetails.setName(fileNamePlain);
        contentDetails.setDescription(fileNamePlain);
        doclibPrem = createContent(drone, contentDetails, PLAINTEXT);
        contentDetails.setName(fileNameXml);
        contentDetails.setDescription(fileNameXml);
        contentDetails.setContent(fileNameXml);
        createContent(drone, contentDetails, XML).render();
        doclibPrem = ShareUserSitePage.createFolder(drone, folderName, folderName).render();
    }

    @Test(groups = { "Hybrid" })
    public void AONE_15429() throws Exception
    {
        desAndAssBean = new DestinationAndAssigneeBean();
        desAndAssBean.setNetwork(hybridDomainPremium);
        desAndAssBean.setSiteName(siteB);
        desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);
        //Sync all three sync sets to Cloud
        //https://issues.alfresco.com/jira/browse/ALF-14513
        //https://issues.alfresco.com/jira/browse/ALF-15265 - cannot sync folder from selected items menu
        ShareUser.login(drone, adminUserPrem);
        doclibPrem = openSitesDocumentLibrary(drone, siteB).render();
        syncContentToCloud(drone, fileNamePlain, desAndAssBean);
        syncContentToCloud(drone, fileNameXml, desAndAssBean);
        syncContentToCloud(drone, folderName, desAndAssBean);

        //All three sets are synced to Cloud. The icon indicating the file/folder is 'synced' in shown for file/folder.
        //'Pending' status is displayed while sync, after sync is completed 'Synced' status is displayed
        assertTrue(checkIfContentIsSynced(drone, fileNameXml) && checkIfContentIsSynced(drone, fileNamePlain)
            && checkIfContentIsSynced(drone, folderName), "Items are not synced On-Prem");

        // Verify CloudSync Info Link is displayed on each document.
        SyncInfoPage sip1 = doclibPrem.getFileDirectoryInfo(fileNamePlain).clickOnViewCloudSyncInfo().render();
        assertTrue(sip1.getCloudSyncLocation().equals(syncLocation) && sip1.isUnsyncButtonPresent() && sip1.isRequestSyncButtonPresent());
        sip1.clickOnCloseButton();
        SyncInfoPage sip2 = doclibPrem.getFileDirectoryInfo(fileNameXml).clickOnViewCloudSyncInfo().render();
        assertTrue(sip2.getCloudSyncLocation().equals(syncLocation) && sip2.isUnsyncButtonPresent() && sip2.isRequestSyncButtonPresent());
        sip2.clickOnCloseButton();
        SyncInfoPage sip3 = doclibPrem.getFileDirectoryInfo(folderName).clickOnViewCloudSyncInfo().render();
        assertTrue(sip3.getCloudSyncLocation().equals(syncLocation + ">" + folderName) && sip3.isUnsyncButtonPresent() && sip3.isRequestSyncButtonPresent());
        sip3.clickOnCloseButton();

        // Login to cloud and verify the synced files appeared
        ShareUser.login(hybridDrone, adminUserPrem, DEFAULT_PASSWORD);
        doclibCl = ShareUser.openSitesDocumentLibrary(hybridDrone, siteB);
        assertTrue(doclibCl.isFileVisible(fileNameXml) && doclibCl.isFileVisible(fileNamePlain) &&
            doclibCl.isFileVisible(folderName), "Items are not available in Cloud");
        assertTrue(checkIfContentIsSynced(hybridDrone, fileNameXml) && checkIfContentIsSynced(hybridDrone, fileNamePlain)
            && checkIfContentIsSynced(hybridDrone, folderName), "Items are not synced on Cloud side");
    }

    private boolean waitAndCheckIfVisible (WebDrone driver, DocumentLibraryPage docLib, String contentName)
    {
        int i = 0;
        boolean isVisible = docLib.isItemVisble(contentName);
        while(!isVisible)
        {
            webDriverWait(driver, timeToWait);
            docLib = refreshDocumentLibrary(driver).render();
            isVisible = docLib.isItemVisble(contentName);
            i++;
            if(i > retryCount)
            {
                break;
            }
        }
        return isVisible;
    }
}
