package org.alfresco.share.cloudsync;

import org.alfresco.po.share.site.DestinationAndAssigneeBean;
import org.alfresco.po.share.site.document.*;
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
import static org.testng.Assert.assertFalse;
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
    protected static String folderName;
    protected static int retryCount;
    protected static long timeToWait;
    protected static String syncLocation;
    protected static DestinationAndAssigneeBean desAndAssBean;
    DocumentLibraryPage doclibPrem;
    DocumentLibraryPage doclibCl;
    String[] subFolders = { "subfolder1", "subfolder2" };
    String[] subFiles = { "subFile1", "subFile2" };
    String[] content = { "content1", "content2" };
    protected static String[] newFiles;

    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        testName = this.getClass().getSimpleName();
        logger.info("Start Tests in " + testName);
        super.setup();
        siteA = getSiteName(testName);
        siteB = getSiteName(testName) + "SY";
        fileName = getFileName(testName);
        folderName = getFolderName(testName);
        fileNamePlain = fileName + "plainText";
        editedFileNamePlain = fileNamePlain + "edited";
        fileNameXml = fileName + "xml";
        editedContentXml = fileNameXml + "edited";
        folderName = getFolderName(testName);
        newFiles = new String[] { "file1", "file2", "file3", "file4" };
        retryCount = 5;
        timeToWait = 25000;
        syncLocation = DOMAIN_PREMIUM + ">" + siteB + ">" + DEFAULT_FOLDER_NAME;
        desAndAssBean = new DestinationAndAssigneeBean();
        desAndAssBean.setNetwork(hybridDomainPremium);
        desAndAssBean.setSiteName(siteA);
        desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);
    }

    /**
     * Sync. Folder with files to Cloud. Mixed case for user
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepHybrid" })
    public void dataPrep_AONE_15582() throws Exception
    {
        ShareUser.login(hybridDrone, adminUserPrem, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, siteA, SITE_VISIBILITY_PUBLIC);
        ShareUser.login(drone, adminUserPrem);

        //Create any site
        ShareUser.createSite(drone, siteA, SITE_VISIBILITY_PUBLIC);
        doclibPrem = openSitesDocumentLibrary(drone, siteA).render();
        createFolderInFolder(drone, folderName, folderName, DOCLIB);
        ContentDetails contentDetails = new ContentDetails();
        for (int i = 0; i < 2; i++)
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

    /**
     * Sync. Folder with files to Cloud. Mixed case for user
     *
     * @throws Exception
     */
    @Test(groups = "Hybrid")
    public void AONE_15582() throws Exception
    {
        String mixedCaseUserName = getUserNameWithMixedCase("admin", hybridDomainPremium);
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
    public void dataPrep_AONE_15583() throws Exception
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
    public void AONE_15583() throws Exception
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

    /**
     * Change the synced items
     *
     * @throws Exception
     */
    @Test(groups = { "Hybrid" })
    public void AONE_15584() throws Exception
    {
        int i;
        //1st sync set: change file properties in On-premise
        ShareUser.login(drone, adminUserPrem);
        EditDocumentPropertiesPage editPagePrem = openSitesDocumentLibrary(drone, siteB).getFileDirectoryInfo(fileNamePlain).selectEditProperties().render();
        editPagePrem.setName(editedFileNamePlain);
        editPagePrem.clickSave();
        getSharePage(drone).render();

        //The changes are synced to Cloud
        ShareUser.login(hybridDrone, adminUserPrem);
        DocumentLibraryPage docLibCl = openSitesDocumentLibrary(hybridDrone, siteB);
        assertTrue(waitAndCheckIfVisible(hybridDrone, docLibCl, editedFileNamePlain), "File properties were not edited in Cloud");

        //1st sync set: change file properties in Cloud
        EditDocumentPropertiesPage editPageCloud = docLibCl.getFileDirectoryInfo(editedFileNamePlain).selectEditProperties().render();
        editPageCloud.setDescription(editedFileNamePlain);
        editPageCloud.selectSave();
        getSharePage(hybridDrone).render();

        //The properties are changed in Cloud and are synced to On-premise
        DocumentLibraryPage docLibPrem = ShareUser.openSitesDocumentLibrary(drone, siteB).render();
        i = 0;
        EditDocumentPropertiesPage doc1Prop = docLibPrem.getFileDirectoryInfo(editedFileNamePlain).selectEditProperties();
        String actualDesc = doc1Prop.getDescription();
        while (!actualDesc.equals(editedFileNamePlain))
        {
            doc1Prop.clickOnCancel();
            webDriverWait(drone, timeToWait);
            openSiteDashboard(drone, siteB).render();
            docLibPrem = openSitesDocumentLibrary(drone, siteB).render();
            actualDesc = docLibPrem.getFileDirectoryInfo(editedFileNamePlain).selectEditProperties().getDescription();
            i++;
            if (i > retryCount)
                break;
        }
        assertTrue(actualDesc.equals(editedFileNamePlain), "File properties were not edited On-Premise");

        //2nd sync set: change file content in On-premise
        DocumentDetailsPage detailsPrem = docLibPrem.getFileDirectoryInfo(fileNameXml).clickOnTitle().render();
        detailsPrem = editTextDocument(drone, fileNameXml, fileNameXml, editedContentXml).render();
        i = 0;
        String actualContent = detailsPrem.selectInlineEdit().getDetails().getContent();
        while (!actualContent.contains(editedContentXml))
        {
            openSiteDashboard(drone, siteB).render();
            detailsPrem = (DocumentDetailsPage) openDocumentLibrary(drone).render().getFileDirectoryInfo(fileNameXml).clickOnTitle();
            actualContent = detailsPrem.selectInlineEdit().getDetails().getContent();
            i++;
            if (i > retryCount)
                break;
        }
        assertTrue(actualContent.contains(editedContentXml), "Document wasn't edited On-Prem");

        //The content is changed in On-premise and the changes are synced to Cloud
        docLibCl = openSitesDocumentLibrary(hybridDrone, siteB).render();
        DocumentDetailsPage detailsCl = docLibCl.selectFile(fileNameXml).render();
        i = 0;
        actualContent = detailsCl.selectInlineEdit().getDetails().getContent();
        while (!actualContent.contains(editedContentXml))
        {
            webDriverWait(hybridDrone, timeToWait);
            openSiteDashboard(hybridDrone, siteB).render();
            openDocumentLibrary(hybridDrone).render();
            detailsCl = docLibCl.selectFile(fileNameXml).render();
            actualContent = detailsCl.selectInlineEdit().getDetails().getContent();
            i++;
            if (i > retryCount)
                break;
        }
        assertTrue(actualContent.contains(editedContentXml), "File content wasn't edited on Cloud");

        //2nd sync set: change file content in Cloud
        openSitesDocumentLibrary(hybridDrone, siteB).getFileDirectoryInfo(fileNameXml).clickOnTitle();
        detailsCl = editTextDocument(hybridDrone, fileNameXml, fileNameXml, editedContentXml + "1").render();
        getSharePage(hybridDrone).render();
        EditTextDocumentPage editPage = detailsCl.selectInlineEdit().render();
        boolean isEdited = editPage.getDetails().getContent().contains(editedContentXml + "1");
        if (!isEdited)
        {
            refreshSharePage(hybridDrone).render();
            isEdited = editPage.getDetails().getContent().contains(editedContentXml + "1");
        }
        assertTrue(isEdited, "File content wasn't edited in Cloud");

        //The content is changed in Cloud and the changes are synced to On-premise
        docLibPrem = openSitesDocumentLibrary(drone, siteB).render();
        detailsPrem = docLibPrem.selectFile(fileNameXml).render();
        i = 0;
        actualContent = detailsPrem.selectInlineEdit().getDetails().getContent();
        while (!actualContent.contains(editedContentXml + "1"))
        {
            webDriverWait(drone, timeToWait);
            openSitesDocumentLibrary(drone, siteB).render();
            detailsPrem = docLibPrem.selectFile(fileNameXml).render();
            actualContent = detailsPrem.selectInlineEdit().getDetails().getContent();
            i++;
            if (i > retryCount)
                break;
        }
        assertTrue(actualContent.contains(editedContentXml + "1"), "Content wasn't changed On-Premise");

        //3rd sync set: add files to the folder in On-premise. Click Request Sync
        uploadFileInFolder1(drone, newFiles[0], folderName);
        uploadFileInFolder1(drone, newFiles[1], folderName);
        openDocumentLibrary(drone).render();
        docLibPrem = requestSyncToCloud(drone, siteB, folderName).render();
        docLibPrem.getFileDirectoryInfo(folderName).clickOnTitle().render();

        //The sync is requested. The new files are added in On-premise and indirectly synced to Cloud.
        // For indirectly synced content/folder the 'synced indirectly' icon is displayed.
        assertTrue(docLibPrem.getFileDirectoryInfo(newFiles[0]).isIndirectlySyncedIconPresent() && docLibPrem.getFileDirectoryInfo(newFiles[1])
            .isIndirectlySyncedIconPresent(), "Indirectly synced icon isn't displayed");
        //boolean isSynced = checkIfContentIsSynced(drone, newFiles[0]);
        SyncInfoPage syncInf1 = docLibPrem.getFileDirectoryInfo(newFiles[0]).clickOnViewCloudSyncInfo().render();
        assertTrue(syncInf1.getCloudSyncLocation().equals(syncLocation + ">" + folderName), "File " +
            "wasn't synced");
        syncInf1.clickOnCloseButton();

        //isSynced = checkIfContentIsSynced(drone, newFiles[1]);
        SyncInfoPage syncInf2 = docLibPrem.getFileDirectoryInfo(newFiles[1]).clickOnViewCloudSyncInfo().render();
        assertTrue(syncInf2.getCloudSyncLocation().equals(syncLocation + ">" + folderName), "File " +
            "wasn't synced");
        syncInf2.clickOnCloseButton();

        docLibCl = openSitesDocumentLibrary(hybridDrone, siteB).render();
        docLibCl.getFileDirectoryInfo(folderName).clickOnTitle().render();
        i = 0;
        while (!(docLibCl.isFileVisible(newFiles[0]) && docLibCl.isFileVisible(newFiles[1])))
        {
            webDriverWait(hybridDrone, timeToWait);
            docLibCl = refreshDocumentLibrary(hybridDrone);
            i++;
            if (i > retryCount)
                break;
        }
        assertTrue(docLibCl.isFileVisible(newFiles[0]) && docLibCl.isFileVisible(newFiles[1]), "Files were not added to Cloud");
        assertTrue(docLibCl.getFileDirectoryInfo(newFiles[0]).isIndirectlySyncedIconPresent() && docLibCl.getFileDirectoryInfo(newFiles[1])
            .isIndirectlySyncedIconPresent(), "Indirectly synced icons are not displayed on Cloud side");
        boolean isSynced = checkIfContentIsSynced(hybridDrone, newFiles[0]);
        SyncInfoPage syncInf3 = docLibCl.getFileDirectoryInfo(newFiles[0]).clickOnViewCloudSyncInfo().render();
        assertTrue(isSynced && syncInf3.getCloudSyncIndirectLocation().equals(folderName), "Incorrect sync info for " +
            newFiles[0]);

        syncInf3.clickOnCloseButton().render();
        isSynced = checkIfContentIsSynced(hybridDrone, newFiles[1]);
        SyncInfoPage syncInf4 = docLibCl.getFileDirectoryInfo(newFiles[1]).clickOnViewCloudSyncInfo().render();
        assertTrue(isSynced && syncInf4.getCloudSyncIndirectLocation().equals(folderName), "Incorrect sync info for " +
            newFiles[1]);
        syncInf4.clickOnCloseButton().render();

        //3rd sync set: add new files to synced Cloud folder
        docLibCl = openSitesDocumentLibrary(hybridDrone, siteB).render();
        String[] fileInfo = { newFiles[2], folderName };
        String[] fileInfo2 = { newFiles[3], folderName };
        uploadFileInFolder(hybridDrone, fileInfo).render();
        uploadFileInFolder(hybridDrone, fileInfo2).render();
        //https://issues.alfresco.com/jira/browse/CLOUD-2229
        //assertTrue(checkIfContentIsSynced(hybridDrone, newFiles[2]) && checkIfContentIsSynced(hybridDrone, newFiles[3]), "Files were not synced");
        syncInf3 = docLibCl.getFileDirectoryInfo(newFiles[2]).clickOnViewCloudSyncInfo().render();
        assertTrue(syncInf3.getCloudSyncIndirectLocation().equals(folderName), "Incorrect sync info for " + newFiles[2]);
        syncInf3.clickOnCloseButton();

        syncInf4 = docLibCl.getFileDirectoryInfo(newFiles[3]).clickOnViewCloudSyncInfo().render();
        assertTrue(syncInf4.getCloudSyncIndirectLocation().equals(folderName), "Incorrect sync info for " + newFiles[3]);
        syncInf4.clickOnCloseButton();

        //The new files are added in Cloud and are indirectly synced to On-premise. For indirectly synced content the 'synced indirectly' icon is displayed.
        ShareUser.login(drone, adminUserPrem).render();
        docLibPrem = openSitesDocumentLibrary(drone, siteB).getFileDirectoryInfo(folderName).clickOnTitle().render();
        i = 0;
        while (!(docLibPrem.isFileVisible(newFiles[2]) && docLibPrem.isFileVisible(newFiles[3])))
        {
            webDriverWait(drone, timeToWait);
            docLibPrem = (DocumentLibraryPage) refreshSharePage(drone);
            i++;
            if (i > retryCount)
                break;
        }

        assertTrue(docLibPrem.isFileVisible(newFiles[2]) && docLibPrem.isFileVisible(newFiles[3]), "Files were not added to On-Prem");
        assertTrue(docLibPrem.getFileDirectoryInfo(newFiles[2]).isIndirectlySyncedIconPresent() && docLibPrem.getFileDirectoryInfo(newFiles[3])
            .isIndirectlySyncedIconPresent(), "Indirectly synced icons are not displayed On-Prem side");
        syncInf1 = docLibPrem.getFileDirectoryInfo(newFiles[2]).clickOnViewCloudSyncInfo().render();
        isSynced = checkIfContentIsSynced(drone, newFiles[2]);
        assertTrue(syncInf1.getCloudSyncIndirectLocation().equals(folderName) && isSynced,
            "Incorrect sync info for " + newFiles[2]);
        syncInf1.clickOnCloseButton();

        isSynced = checkIfContentIsSynced(drone, newFiles[3]);
        syncInf2 = docLibPrem.getFileDirectoryInfo(newFiles[3]).clickOnViewCloudSyncInfo().render();
        assertTrue(syncInf2.getCloudSyncIndirectLocation().equals(folderName) && isSynced,
            "Incorrect sync info for " + newFiles[3]);
        syncInf2.clickOnCloseButton();
    }

    /**
     * Unsync the sets
     *
     * @throws Exception
     */
    @Test
    public void AONE_15585() throws Exception
    {
        //1st sync set: unsync and DO NOT remove the files from Cloud
        ShareUser.login(drone, adminUserPrem);
        DocumentLibraryPage docLibPrem = openSitesDocumentLibrary(drone, siteB).getFileDirectoryInfo(editedFileNamePlain)
            .selectUnSyncAndRemoveContentFromCloud(false).render();

        //The content is unsynced but files are still there and sync icon is removed in Cloud and on-premise
        FileDirectoryInfo fileOnPrem = docLibPrem.getFileDirectoryInfo(editedFileNamePlain);
        assertFalse(fileOnPrem.isCloudSynced() && fileOnPrem.isViewCloudSyncInfoLinkPresent() && fileOnPrem.isUnSyncFromCloudLinkPresent(),
            editedFileNamePlain + " is still Cloud synced.");
        ShareUser.login(hybridDrone, adminUserPrem);
        DocumentLibraryPage doclibCl = openSitesDocumentLibrary(hybridDrone, siteB).render();
        assertTrue(doclibCl.isFileVisible(editedFileNamePlain), "File is removed from Cloud");
        FileDirectoryInfo fileOnCl = docLibPrem.getFileDirectoryInfo(editedFileNamePlain);
        assertFalse(fileOnCl.isCloudSynced() && fileOnCl.isViewCloudSyncInfoLinkPresent(), "Sync icon and/or info are displayed for " + editedFileNamePlain);

        //2nd sync set: unsync and remove the files from Cloud
        ShareUser.login(drone, adminUserPrem);
        docLibPrem = openSitesDocumentLibrary(drone, siteB).getFileDirectoryInfo(fileNameXml)
            .selectUnSyncAndRemoveContentFromCloud(true).render();

        //The content is unsynced and files removed from Cloud and sync icon is removed in On-premise
        fileOnPrem = docLibPrem.getFileDirectoryInfo(fileNameXml);
        assertFalse(fileOnPrem.isCloudSynced() && fileOnPrem.isViewCloudSyncInfoLinkPresent() && fileOnPrem.isUnSyncFromCloudLinkPresent(),
            fileNameXml + " is still Cloud synced.");
        ShareUser.login(hybridDrone, adminUserPrem);
        doclibCl = openSitesDocumentLibrary(hybridDrone, siteB).render();
        assertFalse(doclibCl.isFileVisible(fileNameXml), "File " + fileNameXml + " is still present in Cloud");

        //3rd sync set: unsync the folder and DO NOT remove folder
        docLibPrem = openSitesDocumentLibrary(drone, siteB).getFileDirectoryInfo(folderName).selectUnSyncAndRemoveContentFromCloud(false).render();

        //Files are unsynced but folder and files are still available in Cloud and cloud icon is not available in On-premise and Cloud
        fileOnPrem = docLibPrem.getFileDirectoryInfo(folderName);
        assertFalse(fileOnPrem.isCloudSynced() && fileOnPrem.isViewCloudSyncInfoLinkPresent() && fileOnPrem.isUnSyncFromCloudLinkPresent(),
            editedFileNamePlain + " is still Cloud synced.");
        docLibPrem = docLibPrem.getFileDirectoryInfo(folderName).clickOnTitle().render();
        for (String theFile : newFiles)
        {
            fileOnPrem = docLibPrem.getFileDirectoryInfo(theFile);
            assertFalse(fileOnPrem.isCloudSynced() && fileOnPrem.isViewCloudSyncInfoLinkPresent() && fileOnPrem.isUnSyncFromCloudLinkPresent(),
                theFile + " is still Cloud synced.");
        }

        fileOnCl = openSitesDocumentLibrary(hybridDrone, siteB).getFileDirectoryInfo(folderName);
        boolean isSynced = fileOnCl.isCloudSynced() && fileOnCl.isViewCloudSyncInfoLinkPresent();
        if (isSynced)
        {
            webDriverWait(hybridDrone, 5000);
            fileOnCl = refreshDocumentLibrary(hybridDrone).render().getFileDirectoryInfo(folderName);
            isSynced = fileOnCl.isCloudSynced() && fileOnCl.isViewCloudSyncInfoLinkPresent();
        }
        assertFalse(isSynced, folderName + " is still synced on Cloud");
        doclibCl = fileOnCl.clickOnTitle().render();
        for (String theFile : newFiles)
        {
            fileOnCl = doclibCl.getFileDirectoryInfo(theFile);
            assertFalse(fileOnCl.isCloudSynced() && fileOnCl.isViewCloudSyncInfoLinkPresent(),
                theFile + " is still Cloud synced.");
        }
    }

    private boolean waitAndCheckIfVisible(WebDrone driver, DocumentLibraryPage docLib, String contentName)
    {
        int i = 0;
        boolean isVisible = docLib.isItemVisble(contentName);
        while (!isVisible)
        {
            webDriverWait(driver, timeToWait);
            docLib = refreshDocumentLibrary(driver).render();
            isVisible = docLib.isItemVisble(contentName);
            i++;
            if (i > retryCount)
            {
                break;
            }
        }
        return isVisible;
    }
}
