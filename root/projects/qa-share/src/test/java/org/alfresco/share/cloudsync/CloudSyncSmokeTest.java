package org.alfresco.share.cloudsync;

import org.alfresco.po.share.site.DestinationAndAssigneeBean;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.user.CloudSignInPage;
import org.alfresco.po.share.user.CloudSyncPage;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.share.util.AbstractCloudSyncTest;
import org.alfresco.share.util.ShareUser;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

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
    protected static String siteName;
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
        siteName = getSiteName(testName) + "17";
        fileName = getFileName(testName);
        folderName = getFolderName(testName);
        retryCount = 5;
        timeToWait = 25000;
        syncLocation = DOMAIN_PREMIUM + ">" + siteName + ">" + DEFAULT_FOLDER_NAME;
        desAndAssBean = new DestinationAndAssigneeBean();
        desAndAssBean.setNetwork(hybridDomainPremium);
        desAndAssBean.setSiteName(siteName);
        desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);
    }

    @Test(groups = { "DataPrepHybrid" })
    public void dataPrep_AONE_15428() throws Exception
    {
        ShareUser.login(hybridDrone, adminUserPrem, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.login(drone, adminUserPrem);

        //Create any site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        doclibPrem = openSitesDocumentLibrary(drone, siteName).render();
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
        doclibPrem = openSitesDocumentLibrary(drone, siteName).render().getFileDirectoryInfo(folderName).clickOnTitle().render();

        //Click "Sync to Cloud" option for subfolder1. on Autorization screen type username with mixed cases
        CloudSignInPage cloudSignInPage = doclibPrem.getFileDirectoryInfo(subFolders[0]).selectSyncToCloud().render();
        DestinationAndAssigneePage destinationAndAssigneePage = cloudSignInPage.loginAs(mixedCaseUserName, DEFAULT_PASSWORD).render();
        destinationAndAssigneePage.selectNetwork(DOMAIN_PREMIUM);
        destinationAndAssigneePage.selectSite(siteName);
        destinationAndAssigneePage.selectFolder(DOCLIB);
        destinationAndAssigneePage.selectSubmitButtonToSync();
        assertTrue(checkIfContentIsSynced(drone, subFolders[0]), "Folder " + subFolders[0] + " wasn't synced to Cloud");
        doclibPrem.getFileDirectoryInfo(subFolders[0]).clickOnTitle().render();
        assertTrue(checkIfContentIsSynced(drone, subFiles[0]) && doclibPrem.getFileDirectoryInfo(subFiles[0]).isIndirectlySyncedIconPresent(),
            subFiles[0] + " wasn't synced to Cloud");

        //Subfolder1 and subfile are synced to Cloud.
        ShareUser.login(hybridDrone, adminUserPrem);
        doclibCl = openSitesDocumentLibrary(hybridDrone, siteName).render();
        boolean isVisible = doclibCl.isItemVisble(subFolders[0]);
        while(!isVisible)
        {
            webDriverWait(hybridDrone, timeToWait);
            doclibCl = refreshDocumentLibrary(hybridDrone).render();
            isVisible = doclibCl.isItemVisble(subFolders[0]);
            int i = 0;
            i++;
            if(i > retryCount)
            {
                break;
            }
        }
        assertTrue(isVisible && checkIfContentIsSynced(hybridDrone, subFolders[0]), "Folder " + subFolders[0] + " wasn't synced to Cloud");
        doclibCl.getFileDirectoryInfo(subFolders[0]).clickOnTitle().render();
        assertTrue(doclibCl.isFileVisible(subFiles[0]) && checkIfContentIsSynced(hybridDrone, subFiles[0]) && doclibCl.getFileDirectoryInfo(subFiles[0])
            .isIndirectlySyncedIconPresent(), "File " + subFiles[0] + " wasn't synced to Cloud");

        //Disconnect cloud account from My Profile > Cloud Sync tab
        disconnectCloudSync(drone).render();

        //Navigate to site's DocLib and click "Sync to Cloud" option for content1. on Autorization screen type username with mixed cases
        doclibPrem = openSitesDocumentLibrary(drone, siteName).render().getFileDirectoryInfo(folderName).clickOnTitle().render();
        cloudSignInPage = doclibPrem.getFileDirectoryInfo(content[0]).selectSyncToCloud().render();
        destinationAndAssigneePage = cloudSignInPage.loginAs(mixedCaseUserName, DEFAULT_PASSWORD).render();
        destinationAndAssigneePage.selectNetwork(DOMAIN_PREMIUM);
        destinationAndAssigneePage.selectSite(siteName);
        destinationAndAssigneePage.selectFolder(DOCLIB);
        destinationAndAssigneePage.selectSubmitButtonToSync();
        assertTrue(checkIfContentIsSynced(drone, content[0]), "File " + content[0] + " wasn't synced to Cloud");

        doclibCl = openDocumentLibrary(hybridDrone).render();
        isVisible = doclibCl.isItemVisble(content[0]);
        while(!isVisible)
        {
            webDriverWait(hybridDrone, timeToWait);
            doclibCl = refreshDocumentLibrary(hybridDrone).render();
            isVisible = doclibCl.isItemVisble(content[0]);
            int i = 0;
            i++;
            if(i > retryCount)
            {
                break;
            }
        }
        assertTrue(isVisible && checkIfContentIsSynced(hybridDrone, content[0]), "File " + content[0] + " wasn't synced to Cloud");

        //Open My Profile > Cloud Sync tab and edit cloud account to account with mixed case
        CloudSyncPage cloudSyncPage = navigateToCloudSync(drone).selectEditButton().loginAs(mixedCaseUserName, DEFAULT_PASSWORD).render();
        assertTrue(cloudSyncPage.isDisconnectButtonDisplayed(), "Cloud account wasn't connected");

        //Navigate to site's DocLib again and click "Sync to Cloud" option for the content2 and subfolder2
        openSitesDocumentLibrary(drone, siteName).render().getFileDirectoryInfo(folderName).clickOnTitle().render();
        DestinationAndAssigneeBean dessAndAssBean = new DestinationAndAssigneeBean();
        dessAndAssBean.setNetwork(DOMAIN_PREMIUM);
        dessAndAssBean.setSiteName(siteName);
        dessAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);
        syncContentToCloud(drone, subFolders[1], desAndAssBean).render();
        assertTrue(checkIfContentIsSynced(drone, subFolders[1]), "Folder " + subFolders[1] + " wasn't synced to Cloud");
        doclibPrem.getFileDirectoryInfo(subFolders[1]).clickOnTitle().render();
        assertTrue(checkIfContentIsSynced(drone, subFiles[1]) && doclibPrem.getFileDirectoryInfo(subFiles[1]).isIndirectlySyncedIconPresent(),
            subFiles[1] + " wasn't synced to Cloud");
        openDocumentLibrary(drone).render().getFileDirectoryInfo(folderName).clickOnTitle().render();
        syncContentToCloud(drone, content[1], desAndAssBean).render();
        assertTrue(checkIfContentIsSynced(drone, content[1]), "File " + content[1] + " wasn't synced to Cloud");

        doclibCl = openSitesDocumentLibrary(hybridDrone, siteName).render();
        isVisible = doclibCl.isItemVisble(subFolders[1]);
        while(!isVisible)
        {
            webDriverWait(hybridDrone, timeToWait);
            doclibCl = refreshDocumentLibrary(hybridDrone).render();
            isVisible = doclibCl.isItemVisble(subFolders[1]);
            int i = 0;
            i++;
            if(i > retryCount)
            {
                break;
            }
        }
        assertTrue(isVisible && checkIfContentIsSynced(hybridDrone, subFolders[0]), "Folder " + subFolders[0] + " wasn't synced to Cloud");
        doclibCl.getFileDirectoryInfo(subFolders[1]).clickOnTitle().render();

        isVisible = doclibCl.isItemVisble(subFiles[1]);
        while(!isVisible)
        {
            webDriverWait(hybridDrone, timeToWait);
            doclibCl = refreshDocumentLibrary(hybridDrone).render();
            isVisible = doclibCl.isItemVisble(subFiles[1]);
            int i = 0;
            i++;
            if(i > retryCount)
            {
                break;
            }
        }
        assertTrue(doclibCl.isFileVisible(subFiles[1]) && checkIfContentIsSynced(hybridDrone, subFiles[1]) && doclibCl.getFileDirectoryInfo(subFiles[1])
            .isIndirectlySyncedIconPresent(), "File " + subFiles[1] + " wasn't synced to Cloud");

        openDocumentLibrary(hybridDrone).render();
        isVisible = doclibCl.isItemVisble(content[1]);
        while(!isVisible)
        {
            webDriverWait(hybridDrone, timeToWait);
            doclibCl = refreshDocumentLibrary(hybridDrone).render();
            isVisible = doclibCl.isItemVisble(content[1]);
            int i = 0;
            i++;
            if(i > retryCount)
            {
                break;
            }
        }
        assertTrue(isVisible && checkIfContentIsSynced(hybridDrone, content[1]), "File " + content[1] + " wasn't synced to Cloud");
    }
}
