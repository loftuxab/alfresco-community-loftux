package org.alfresco.share.sanity;

import org.alfresco.po.share.site.DestinationAndAssigneeBean;
import org.alfresco.po.share.site.document.*;
import org.alfresco.share.util.AbstractCloudSyncTest;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Locale;

import static org.alfresco.po.share.enums.CloudSyncStatus.*;
import static org.alfresco.po.share.site.document.ContentType.PLAINTEXT;
import static org.alfresco.po.share.site.document.ContentType.XML;
import static org.alfresco.share.util.ShareUser.*;
import static org.testng.Assert.*;

/**
 * @author Marina.Nenadovets
 */
@Listeners(FailedTestListener.class)
@Test(groups = "Hybrid")
public class CloudSyncSanityTest extends AbstractCloudSyncTest
{
    private static Log logger = LogFactory.getLog(CloudSyncSanityTest.class);
    protected static String siteName;
    protected static String fileName;
    protected static String folderName;
    protected static String fileNamePlain;
    protected static String fileNameXml;
    protected static int retryCount;
    protected static int timeToWait;
    protected static String [] newFiles;
    protected static String editedFileNamePlain;
    protected  static  String editedContentXml;
    String syncLocation;
    String SYNCED_STATUS;
    protected DestinationAndAssigneeBean desAndAssBean;

    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        siteName = getSiteName(testName);
        fileName = getFileName(testName);
        fileNamePlain = fileName + "plainText";
        editedFileNamePlain = fileNamePlain + "edited";
        fileNameXml = fileName + "xml";
        editedContentXml = fileNameXml + "edited";
        folderName = getFolderName(testName);
        retryCount = 5;
        timeToWait = 30000;
        newFiles = new String[] { "file1", "file2", "file3", "file4"};
        syncLocation = DOMAIN_PREMIUM + ">" + siteName + ">" + DEFAULT_FOLDER_NAME;
        SYNCED_STATUS = SYNCED.getValue();
        desAndAssBean = new DestinationAndAssigneeBean();
        desAndAssBean.setNetwork(hybridDomainPremium);
        desAndAssBean.setSiteName(siteName);
        desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);
    }

    /**
     * Sync to Cloud
     */
    @Test
    public void AONE_8217() throws Exception
    {
        // Login into cloud and create a site
        ShareUser.login(hybridDrone, adminUserPrem, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, siteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.login(drone, adminUserPrem);
        //Authorize a cloud premium account (User Profile > Cloud Sync);
        signInToAlfrescoInTheCloud(drone, adminUserPrem, DEFAULT_PASSWORD);

        //Create any site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        openSitesDocumentLibrary(drone, siteName);

        //Create 3 sync sets 1. file sync, 2. file sync, and 3. folder
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setContent(fileNamePlain);
        contentDetails.setName(fileNamePlain);
        contentDetails.setDescription(fileNamePlain);
        DocumentLibraryPage documentLibraryPage = createContent(drone, contentDetails, PLAINTEXT);
        contentDetails.setName(fileNameXml);
        contentDetails.setDescription(fileNameXml);
        contentDetails.setContent(fileNameXml);
        createContent(drone, contentDetails, XML).render();
        documentLibraryPage = ShareUserSitePage.createFolder(drone, folderName, folderName).render();

        //Sync all three sync sets to Cloud
        //https://issues.alfresco.com/jira/browse/ALF-14513
        //https://issues.alfresco.com/jira/browse/ALF-15265 - cannot sync folder from selected items menu
        syncContentToCloud(drone, fileNamePlain, desAndAssBean);
        syncContentToCloud(drone, fileNameXml, desAndAssBean);
        syncContentToCloud(drone, folderName, desAndAssBean);

        //All three sets are synced to Cloud. The icon indicating the file/folder is 'synced' in shown for file/folder.
        //'Pending' status is displayed while sync, after sync is completed 'Synced' status is displayed
        assertTrue(checkIfContentIsSynced(drone, fileNameXml) && checkIfContentIsSynced(drone, fileNamePlain)
            && checkIfContentIsSynced(drone, folderName), "Items are not synced On-Prem");

        // Verify CloudSync Info Link is displayed on each document.
        SyncInfoPage sip1 = documentLibraryPage.getFileDirectoryInfo(fileNamePlain).clickOnViewCloudSyncInfo().render();
        assertTrue(sip1.getCloudSyncLocation().equals(syncLocation) && sip1.isUnsyncButtonPresent() && sip1.isRequestSyncButtonPresent());
        sip1.clickOnCloseButton();
        SyncInfoPage sip2 = documentLibraryPage.getFileDirectoryInfo(fileNameXml).clickOnViewCloudSyncInfo().render();
        assertTrue(sip2.getCloudSyncLocation().equals(syncLocation) && sip2.isUnsyncButtonPresent() && sip2.isRequestSyncButtonPresent());
        sip2.clickOnCloseButton();
        SyncInfoPage sip3 = documentLibraryPage.getFileDirectoryInfo(folderName).clickOnViewCloudSyncInfo().render();
        assertTrue(sip3.getCloudSyncLocation().equals(syncLocation + ">" + folderName) && sip3.isUnsyncButtonPresent() && sip3.isRequestSyncButtonPresent());
        sip3.clickOnCloseButton();

        // Login to cloud and verify the synced files appeared
        ShareUser.login(hybridDrone, adminUserPrem, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, siteName);
        assertTrue(documentLibraryPage.isFileVisible(fileNameXml) && documentLibraryPage.isFileVisible(fileNamePlain) &&
            documentLibraryPage.isFileVisible(folderName), "Items are not available in Cloud");
        assertTrue(checkIfContentIsSynced(hybridDrone, fileNameXml) && checkIfContentIsSynced(hybridDrone, fileNamePlain)
            && checkIfContentIsSynced(hybridDrone, folderName), "Items are not synced on Cloud side");
    }

    /**
     * Change the synced items
     *
     * @throws Exception
     */
    @Test
    public void AONE_8218() throws Exception
    {
        int i = 0;
        //1st sync set: change file properties in On-premise
        ShareUser.login(drone, adminUserPrem);
        EditDocumentPropertiesPage editPagePrem = openSitesDocumentLibrary(drone, siteName).getFileDirectoryInfo(fileNamePlain).selectEditProperties().render();
        editPagePrem.setName(editedFileNamePlain);
        editPagePrem.clickSave();
        getSharePage(drone).render();

        //The changes are synced to Cloud
        ShareUser.login(hybridDrone, adminUserPrem);
        DocumentLibraryPage docLibCl = openSitesDocumentLibrary(hybridDrone, siteName);
        boolean isVisible = docLibCl.isFileVisible(editedFileNamePlain);
        while (!isVisible)
        {
            webDriverWait(hybridDrone, timeToWait);
            ShareUser.openSiteDashboard(hybridDrone, siteName).render();
            docLibCl = openSitesDocumentLibrary(hybridDrone, siteName).render();
            isVisible = docLibCl.isFileVisible(editedFileNamePlain);
            i++;
            if(i > retryCount)
                break;
        }
        assertTrue(isVisible, "File properties were not edited in Cloud");

        //1st sync set: change file properties in Cloud
        EditDocumentPropertiesPage editPageCloud = docLibCl.getFileDirectoryInfo(editedFileNamePlain).selectEditProperties().render();
        editPageCloud.setDescription(editedFileNamePlain);
        editPageCloud.selectSave();
        getSharePage(hybridDrone).render();

        //The properties are changed in Cloud and are synced to On-premise
        DocumentLibraryPage docLibPrem = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        i = 0;
        EditDocumentPropertiesPage doc1Prop = docLibPrem.getFileDirectoryInfo(editedFileNamePlain).selectEditProperties();
        String actualDesc = doc1Prop.getDescription();
        while (!actualDesc.equals(editedFileNamePlain))
        {
            doc1Prop.clickOnCancel();
            webDriverWait(drone, timeToWait);
            openSiteDashboard(drone, siteName).render();
            docLibPrem = openSitesDocumentLibrary(drone, siteName).render();
            actualDesc = docLibPrem.getFileDirectoryInfo(editedFileNamePlain).selectEditProperties().getDescription();
            i++;
            if(i > retryCount)
                break;
        }
        assertTrue(actualDesc.equals(editedFileNamePlain), "File properties were not edited On-Premise");

        //2nd sync set: change file content in On-premise
        DocumentDetailsPage detailsPrem = docLibPrem.getFileDirectoryInfo(fileNameXml).clickOnTitle().render();
        detailsPrem = editTextDocument(drone, fileNameXml, fileNameXml, editedContentXml).render();
        i = 0;
        String actualContent = detailsPrem.selectInlineEdit().getDetails().getContent();
        while(!actualContent.contains(editedContentXml))
        {
            openSiteDashboard(drone, siteName).render();
            detailsPrem = (DocumentDetailsPage)openDocumentLibrary(drone).render().getFileDirectoryInfo(fileNameXml).clickOnTitle();
            actualContent = detailsPrem.selectInlineEdit().getDetails().getContent();
            i++;
            if(i > retryCount)
                break;
        }
        assertTrue(actualContent.contains(editedContentXml), "Document wasn't edited On-Prem");

        //The content is changed in On-premise and the changes are synced to Cloud
        docLibCl = openSitesDocumentLibrary(hybridDrone, siteName).render();
        DocumentDetailsPage detailsCl = docLibCl.selectFile(fileNameXml).render();
        i = 0;
        actualContent = detailsCl.selectInlineEdit().getDetails().getContent();
        while (!actualContent.contains(editedContentXml))
        {
            webDriverWait(hybridDrone, timeToWait);
            openSiteDashboard(hybridDrone, siteName).render();
            openDocumentLibrary(hybridDrone).render();
            detailsCl = docLibCl.selectFile(fileNameXml).render();
            actualContent = detailsCl.selectInlineEdit().getDetails().getContent();
            i++;
            if(i > retryCount)
                break;
        }
        assertTrue(actualContent.contains(editedContentXml), "File content wasn't edited on Cloud");

        //2nd sync set: change file content in Cloud
        openSitesDocumentLibrary(hybridDrone, siteName).getFileDirectoryInfo(fileNameXml).clickOnTitle();
        detailsCl = editTextDocument(hybridDrone, fileNameXml, fileNameXml, editedContentXml + "1").render();
        getSharePage(hybridDrone).render();
        EditTextDocumentPage editPage = detailsCl.selectInlineEdit().render();
        boolean isEdited = editPage.getDetails().getContent().contains(editedContentXml + "1");
        if(!isEdited)
        {
            refreshSharePage(hybridDrone).render();
            isEdited = editPage.getDetails().getContent().contains(editedContentXml + "1");
        }
        assertTrue(isEdited, "File content wasn't edited in Cloud");

        //The content is changed in Cloud and the changes are synced to On-premise
        docLibPrem = openSitesDocumentLibrary(drone, siteName).render();
        detailsPrem = docLibPrem.selectFile(fileNameXml).render();
        i = 0;
        actualContent = detailsPrem.selectInlineEdit().getDetails().getContent();
        while (!actualContent.contains(editedContentXml + "1"))
        {
            webDriverWait(drone, timeToWait);
            openSitesDocumentLibrary(drone, siteName).render();
            detailsPrem = docLibPrem.selectFile(fileNameXml).render();
            actualContent = detailsPrem.selectInlineEdit().getDetails().getContent();
            i++;
            if(i > retryCount)
                break;
        }
        assertTrue(actualContent.contains(editedContentXml + "1"), "Content wasn't changed On-Premise");

        //3rd sync set: add files to the folder in On-premise. Click Request Sync
        uploadFileInFolder1(drone, newFiles[0], folderName);
        uploadFileInFolder1(drone, newFiles[1], folderName);
        openDocumentLibrary(drone).render();
        docLibPrem = requestSyncToCloud(drone, siteName, folderName).render();
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

        docLibCl = openSitesDocumentLibrary(hybridDrone, siteName).render();
        docLibCl.getFileDirectoryInfo(folderName).clickOnTitle().render();
        i = 0;
        while(!(docLibCl.isFileVisible(newFiles[0]) && docLibCl.isFileVisible(newFiles[1])))
        {
            webDriverWait(hybridDrone, timeToWait);
            docLibCl = refreshDocumentLibrary(hybridDrone);
            i++;
            if(i > retryCount)
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
        docLibCl = openSitesDocumentLibrary(hybridDrone, siteName).render();
        String [] fileInfo = { newFiles[2], folderName};
        String [] fileInfo2 = { newFiles[3], folderName};
        uploadFileInFolder(hybridDrone, fileInfo);
        uploadFileInFolder(hybridDrone, fileInfo2);
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
        docLibPrem = openSitesDocumentLibrary(drone, siteName).getFileDirectoryInfo(folderName).clickOnTitle().render();
        i = 0;
        while(!(docLibPrem.isFileVisible(newFiles[2]) && docLibPrem.isFileVisible(newFiles[3])))
        {
            webDriverWait(drone, timeToWait);
            docLibPrem = (DocumentLibraryPage) refreshSharePage(drone);
            i++;
            if(i > retryCount)
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
    public void AONE_8219() throws Exception
    {
        //1st sync set: unsync and DO NOT remove the files from Cloud
        ShareUser.login(drone, adminUserPrem);
        DocumentLibraryPage docLibPrem = openSitesDocumentLibrary(drone, siteName).getFileDirectoryInfo(editedFileNamePlain)
            .selectUnSyncAndRemoveContentFromCloud(false).render();

        //The content is unsynced but files are still there and sync icon is removed in Cloud and on-premise
        FileDirectoryInfo fileOnPrem = docLibPrem.getFileDirectoryInfo(editedFileNamePlain);
        assertFalse(fileOnPrem.isCloudSynced() && fileOnPrem.isViewCloudSyncInfoLinkPresent() && fileOnPrem.isUnSyncFromCloudLinkPresent(),
            editedFileNamePlain + " is still Cloud synced.");
        ShareUser.login(hybridDrone, adminUserPrem);
        DocumentLibraryPage doclibCl = openSitesDocumentLibrary(hybridDrone, siteName).render();
        assertTrue(doclibCl.isFileVisible(editedFileNamePlain), "File is removed from Cloud");
        FileDirectoryInfo fileOnCl = docLibPrem.getFileDirectoryInfo(editedFileNamePlain);
        assertFalse(fileOnCl.isCloudSynced() && fileOnCl.isViewCloudSyncInfoLinkPresent(), "Sync icon and/or info are displayed for " + editedFileNamePlain);

        //2nd sync set: unsync and remove the files from Cloud
        ShareUser.login(drone, adminUserPrem);
        docLibPrem = openSitesDocumentLibrary(drone, siteName).getFileDirectoryInfo(fileNameXml)
            .selectUnSyncAndRemoveContentFromCloud(true).render();

        //The content is unsynced and files removed from Cloud and sync icon is removed in On-premise
        fileOnPrem = docLibPrem.getFileDirectoryInfo(fileNameXml);
        assertFalse(fileOnPrem.isCloudSynced() && fileOnPrem.isViewCloudSyncInfoLinkPresent() && fileOnPrem.isUnSyncFromCloudLinkPresent(),
            fileNameXml + " is still Cloud synced.");
        ShareUser.login(hybridDrone, adminUserPrem);
        doclibCl = openSitesDocumentLibrary(hybridDrone, siteName).render();
        assertFalse(doclibCl.isFileVisible(fileNameXml), "File " + fileNameXml + " is still present in Cloud");

        //3rd sync set: unsync the folder and DO NOT remove folder
        docLibPrem = openSitesDocumentLibrary(drone, siteName).getFileDirectoryInfo(folderName).selectUnSyncAndRemoveContentFromCloud(false).render();

        //Files are unsynced but folder and files are still available in Cloud and cloud icon is not available in On-premise and Cloud
        fileOnPrem = docLibPrem.getFileDirectoryInfo(folderName);
        assertFalse(fileOnPrem.isCloudSynced() && fileOnPrem.isViewCloudSyncInfoLinkPresent() && fileOnPrem.isUnSyncFromCloudLinkPresent(),
            editedFileNamePlain + " is still Cloud synced.");
        docLibPrem = docLibPrem.getFileDirectoryInfo(folderName).clickOnTitle().render();
        for(String theFile : newFiles)
        {
            fileOnPrem = docLibPrem.getFileDirectoryInfo(theFile);
            assertFalse(fileOnPrem.isCloudSynced() && fileOnPrem.isViewCloudSyncInfoLinkPresent() && fileOnPrem.isUnSyncFromCloudLinkPresent(),
                theFile + " is still Cloud synced.");
        }

        fileOnCl = openSitesDocumentLibrary(hybridDrone, siteName).getFileDirectoryInfo(folderName);
        boolean isSynced = fileOnCl.isCloudSynced() && fileOnCl.isViewCloudSyncInfoLinkPresent();
        if(isSynced)
        {
            webDriverWait(hybridDrone, 5000);
            fileOnCl = refreshDocumentLibrary(hybridDrone).render().getFileDirectoryInfo(folderName);
            isSynced = fileOnCl.isCloudSynced() && fileOnCl.isViewCloudSyncInfoLinkPresent();
        }
        assertFalse(isSynced, folderName + " is still synced on Cloud");
        doclibCl = fileOnCl.clickOnTitle().render();
        for(String theFile : newFiles)
        {
            fileOnCl = doclibCl.getFileDirectoryInfo(theFile);
            assertFalse(fileOnCl.isCloudSynced() && fileOnCl.isViewCloudSyncInfoLinkPresent(),
                theFile + " is still Cloud synced.");
        }
    }

    /**
     * Resync the sets
     *
     * @throws Exception
     */
    @Test
    public void AONE_8220() throws Exception
    {
        String newFolderInCloud = "new_fol";
        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMM yyyy HH:mm", Locale.ENGLISH);
        //1st sync set: sync to Cloud to the same location
        ShareUser.login(drone, adminUserPrem);
        DocumentLibraryPage doclibPrem = openSitesDocumentLibrary(drone, siteName).render();
        doclibPrem = syncContentToCloud(drone, editedFileNamePlain, desAndAssBean).render();

        //The sync is failed. 'Sync failed' icon is displayed
        SyncInfoPage syncInf1 = doclibPrem.getFileDirectoryInfo(editedFileNamePlain).clickOnViewCloudSyncInfo().render();
        assertTrue(getCloudSyncStatus(drone, editedFileNamePlain).equals(ATTEMPTED), "Sync didn't fail");
        boolean isFailedInfDisplayed = syncInf1.isFailedInfoDisplayed();
        while(!isFailedInfDisplayed)
        {
            syncInf1.clickOnCloseButton();
            syncInf1 = doclibPrem.getFileDirectoryInfo(editedFileNamePlain).clickOnViewCloudSyncInfo().render();
            isFailedInfDisplayed = syncInf1.isFailedInfoDisplayed();
            int i = 0;
            i++;
            if(i > retryCount)
                break;
        }
        assertTrue(isFailedInfDisplayed, "Failed info isn't displayed");
        syncInf1.clickOnCloseButton();
        assertTrue(doclibPrem.getFileDirectoryInfo(editedFileNamePlain).isSyncFailedIconPresent(maxWaitTime), "Sync didn't fail");

        //2nd sync set: sync to Cloud to the same location, select 'Lock on-premise' copy checkbox
        desAndAssBean.setLockOnPrem(true);
        doclibPrem = syncContentToCloud(drone, fileNameXml, desAndAssBean).render();
        refreshDocumentLibrary(drone).render();

        //The content is synced. The content is locked in on-premise. It's not possible to edit it
        assertTrue(doclibPrem.getFileDirectoryInfo(fileNameXml).isLocked() && isCloudSynced(drone, fileNameXml), "Document wasn't locked");
        assertFalse(doclibPrem.getFileDirectoryInfo(fileNameXml).isInlineEditLinkPresent() && doclibPrem.getFileDirectoryInfo(fileNameXml).isEditOfflineLinkPresent()
            && doclibPrem.getFileDirectoryInfo(fileNameXml).isEditInGoogleDocsPresent(), "It's possible to edit the content");

        //Login to Cloud and check the content is synced
        ShareUser.login(hybridDrone, adminUserPrem, DEFAULT_PASSWORD);
        DocumentLibraryPage doclibCl = ShareUser.openSitesDocumentLibrary(hybridDrone, siteName);
        assertTrue(doclibCl.isFileVisible(fileNameXml), "Items are not available in Cloud");
        assertTrue(checkIfContentIsSynced(hybridDrone, fileNameXml), "Items are not synced on Cloud side");

        //3rd sync set: sync to Cloud: Create a new folder in cloud target selection window and sync to that folder
        desAndAssBean.setLockOnPrem(false);

        //Date syncDate2 = sdf.parse(now.toString());
        createNewFolderAndSyncContent(drone, folderName, desAndAssBean, newFolderInCloud).render();

        //The folder is created. The folder with content is synced to the new location
        SyncInfoPage sip1 = doclibPrem.getFileDirectoryInfo(folderName).clickOnViewCloudSyncInfo().render();
        String theLocation = sip1.getCloudSyncLocation();
        String status = sip1.getCloudSyncStatus();
        while(status.contains(PENDING.getValue()))
        {
            int i = 0;
            refreshDocumentLibrary(drone);
            sip1 = doclibPrem.getFileDirectoryInfo(folderName).clickOnViewCloudSyncInfo().render();
            theLocation = sip1.getCloudSyncLocation();
            status = sip1.getCloudSyncStatus();
            i++;
            if(i > retryCount)
                break;
        }
        assertTrue(theLocation.equals(syncLocation + ">" + newFolderInCloud + ">" + folderName), "Incorrect sync location");
        assertTrue(sip1.isUnsyncButtonPresent() && sip1.isRequestSyncButtonPresent(), "Buttons are not available");
        sip1.clickOnCloseButton();

        //Login to Cloud and check the content is synced
        ShareUser.login(hybridDrone, adminUserPrem).render();
        doclibCl = ShareUser.openSitesDocumentLibrary(hybridDrone, siteName);
        assertTrue(doclibCl.isFileVisible(newFolderInCloud), "Items are not available in Cloud");
        doclibCl.getFileDirectoryInfo(newFolderInCloud).clickOnTitle().render();
        assertTrue(checkIfContentIsSynced(hybridDrone, folderName), "Items are not synced on Cloud side");

        //History should show, sync target location, synced from location, last synced time and date, synced by, last failed sync if any, and version history
        DocumentDetailsPage detailsPage1 = ShareUser.openDocumentLibrary(drone).render().getFileDirectoryInfo(editedFileNamePlain)
            .clickOnTitle().render();
        syncInf1 = detailsPage1.getSyncInfoPage();
        assertTrue(syncInf1.getCloudSyncLocation().equals(syncLocation) && syncInf1.getCloudSyncStatus().contains(ATTEMPTED.getValue()) && syncInf1.
            isFailedInfoDisplayed() && detailsPage1.getCurrentVersionDetails().getUserName().toString().contains(adminUserPrem)
            && detailsPage1.getCurrentVersionDetails().getVersionNumber().equals("1.1"),
            "Incorrect sync history for " + editedFileNamePlain);

        DocumentDetailsPage detailsPage2 = ShareUser.openDocumentLibrary(drone).render().getFileDirectoryInfo(fileNameXml)
            .clickOnTitle().render();
        SyncInfoPage syncInf2 = detailsPage2.getSyncInfoPage();
        assertTrue(syncInf2.getCloudSyncLocation().equals(syncLocation) && syncInf2.getCloudSyncStatus().contains(SYNCED.getValue())
            && detailsPage2.getCurrentVersionDetails().getVersionNumber().equals("1.1") && detailsPage2.getCurrentVersionDetails().getUserName().toString()
            .contains(adminUserPrem));
        assertNotNull(syncInf2.getSyncPeriodDetails());

        FolderDetailsPage fldDetails = ShareUser.openDocumentLibrary(drone).render().getFileDirectoryInfo(folderName).selectViewFolderDetails()
            .render();
        SyncInfoPage syncInf3 = fldDetails.getSyncInfoPage();
        assertTrue(syncInf3.getCloudSyncLocation().equals(syncLocation + ">" + newFolderInCloud + ">" + folderName) && syncInf3.getCloudSyncStatus().contains(SYNCED
        .getValue()));
        assertNotNull(syncInf3.getSyncPeriodDetails());

        doclibPrem = ShareUser.openDocumentLibrary(drone).getFileDirectoryInfo(folderName).clickOnTitle().render();
        for(String theFile: newFiles)
        {
            DocumentDetailsPage fileDtls = (DocumentDetailsPage)doclibPrem.getFileDirectoryInfo(theFile).clickOnTitle();
            syncInf1 = fileDtls.getSyncInfoPage();
            assertTrue(syncInf1.getCloudSyncLocation().equals(syncLocation + ">" + newFolderInCloud + ">" + folderName) && syncInf1.getCloudSyncStatus()
                .contains(SYNCED.getValue()));
            assertNotNull(syncInf1.getSyncPeriodDetails());
            doclibPrem = fileDtls.getSiteNav().selectSiteDocumentLibrary().render().getFileDirectoryInfo(folderName).clickOnTitle().render();
        }

        //Check history on cloud
        detailsPage1 = ShareUser.openDocumentLibrary(hybridDrone).render().getFileDirectoryInfo(editedFileNamePlain)
            .clickOnTitle().render();
        VersionDetails verdetails1 = detailsPage1.getCurrentVersionDetails();
        VersionDetails verdetails2 = detailsPage1.getOlderVersionDetails().get(0);
        String comments2 = detailsPage1.getCommentsOfLastCommit();
        assertTrue(verdetails1.getVersionNumber().equals("1.1") && verdetails1.getUserName().toString().contains(adminUserPrem) && verdetails1.getFileName()
            .equals(editedFileNamePlain) && verdetails2.getFileName().equals(fileNamePlain) && comments2.contains(siteName + "/" + editedFileNamePlain),
            "Incorrect sync history for " + editedFileNamePlain + " in Cloud");

        detailsPage2 = ShareUser.openDocumentLibrary(hybridDrone).render().getFileDirectoryInfo(fileNameXml).clickOnTitle().render();
        verdetails1 = detailsPage2.getCurrentVersionDetails();
        comments2 = detailsPage2.getCommentsOfLastCommit();
        assertTrue(verdetails1.getVersionNumber().equals("1.0") && verdetails1.getUserName().toString().contains(adminUserPrem) && verdetails1.getFileName()
            .equals(fileNameXml) && comments2.contains(siteName + "/" + fileNameXml),
            "Incorrect sync history for " + editedFileNamePlain + " in Cloud");

        ShareUser.openDocumentLibrary(hybridDrone).getFileDirectoryInfo(newFolderInCloud).clickOnTitle().render();
        fldDetails = ShareUser.openFolderDetailPage(hybridDrone, folderName).render();
        syncInf3 = fldDetails.getSyncInfoPage();
        assertTrue(syncInf3.getCloudSyncStatus().contains(SYNCED.getValue()));
        assertNotNull(syncInf3.getSyncPeriodDetails());

        doclibCl = ShareUser.openDocumentLibrary(hybridDrone).getFileDirectoryInfo(newFolderInCloud).clickOnTitle().render();
        doclibCl.getFileDirectoryInfo(folderName).clickOnTitle().render();
        for(String theFile: newFiles)
        {
            DocumentDetailsPage fileDtls = (DocumentDetailsPage)doclibCl.getFileDirectoryInfo(theFile).clickOnTitle();
            syncInf1 = fileDtls.getSyncInfoPage();
            VersionDetails verdetails = fileDtls.getCurrentVersionDetails();
            assertTrue(syncInf1.getCloudSyncDocumentName().equals(folderName) && syncInf1.getCloudSyncStatus().contains(SYNCED.getValue())
            && verdetails.getFileName().equals(theFile) && verdetails.getUserName().toString().contains(adminUserPrem)
            && verdetails.getComment().contains(siteName + "/" + folderName + "/" + theFile));
            assertNotNull(syncInf1.getSyncPeriodDetails());
            doclibCl = ShareUser.openDocumentLibrary(hybridDrone).render().getFileDirectoryInfo(newFolderInCloud).clickOnTitle().render();
            doclibCl.getFileDirectoryInfo(folderName).clickOnTitle().render();
        }
    }
}
