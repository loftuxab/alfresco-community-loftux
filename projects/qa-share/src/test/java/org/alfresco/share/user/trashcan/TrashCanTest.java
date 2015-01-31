/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.share.user.trashcan;

import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.DestinationAndAssigneeBean;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.blog.BlogPage;
import org.alfresco.po.share.site.blog.PostViewPage;
import org.alfresco.po.share.site.datalist.DataListPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.links.LinksPage;
import org.alfresco.po.share.site.wiki.WikiPage;
import org.alfresco.po.share.site.wiki.WikiPageList;
import org.alfresco.po.share.user.*;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.alfresco.po.share.enums.DataLists.CONTACT_LIST;
import static org.testng.Assert.*;

/**
 * Class includes: Tests from TrashCan.
 * 
 * @author nshah
 */
@Listeners(FailedTestListener.class)
public class TrashCanTest extends AbstractCloudSyncTest
{

    protected String testUser;
    private String testDomainFree = DOMAIN_FREE;
    private String adminUserFree = ADMIN_USERNAME;
    private String testDomain = DOMAIN_HYBRID;
    private String format = "EEE d MMM YYYY";
    
    /**
     * Class includes: Tests from TestLink in Area: Advanced Search Tests
     * <ul>
     * <li>Test searches using various Properties, content, Folder</li>
     * </ul>
     */
    @Override
    @BeforeClass(alwaysRun=true)
    public void setup() throws Exception
    {
        super.setup();
        testDomain = DOMAIN_HYBRID;
        testDomainFree = DOMAIN_FREE;
        adminUserFree = ADMIN_USERNAME;
        testName = this.getClass().getSimpleName();
    }

     @Test(groups = { "DataPrepTrashCan", "HybridSync", "Enterprise42" })
    public void dataPrep_TrashCan_AONE_15084() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);

        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(hybridDrone);

        // Login as Enterprise user, create site
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(drone);

    }

    @Test(groups = { "HybridSync", "Enterprise42" }, enabled=false)
    public void AONE_15084() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String file = testName + System.currentTimeMillis() + ".txt";

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        ShareUser.uploadFileInFolder(drone, new String[] { file });

        DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
        desAndAssBean.setNetwork(testDomain);
        desAndAssBean.setSiteName(cloudSiteName);

        AbstractCloudSyncTest.syncContentToCloud(drone, file, desAndAssBean);

        ShareUser.selectContentCheckBox(drone, file);

        ShareUser.deleteSelectedContent(drone);

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);

        Assert.assertFalse(docLibPage.isFileVisible(file));

        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUserProfile.navigateToTrashCan(drone);

        Assert.assertTrue(ShareUserProfile.isTrashCanItemPresent(drone, file));

        ShareUserProfile.recoverTrashCanItem(drone, file);

        Assert.assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, file));

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        docLibPage.isFileVisible(file);

        Assert.assertTrue(docLibPage.getFileDirectoryInfo(file).isViewCloudSyncInfoLinkPresent());
              
        Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, file), "ALF-20445: sync is not happening!!");               

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);        

        Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(hybridDrone, file));
        
        Assert.assertTrue(docLibPage.isFileVisible(file));

        ShareUser.logout(hybridDrone);

    }

     @Test(groups = { "DataPrepTrashCan", "HybridSync", "Enterprise42" })
    public void dataPrep_TrashCan_AONE_15085() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as Enterprise user, create site
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

    }

    @Test(groups = { "HybridSync", "Enterprise42" }, enabled=false)
    public void AONE_15085() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);
        
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        
        String folderName = testName + System.currentTimeMillis();
        String file = getFileName(testName)+ System.currentTimeMillis()+".text";
        
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        ShareUserSitePage.createFolder(drone, folderName, folderName);

        ShareUser.uploadFileInFolder(drone, new String[] { file });

        DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
        desAndAssBean.setNetwork(testDomain);
        desAndAssBean.setSiteName(cloudSiteName);
        AbstractCloudSyncTest.syncContentToCloud(drone, folderName, desAndAssBean);

        ShareUser.selectContentCheckBox(drone, folderName);

        ShareUser.deleteSelectedContent(drone);

        ShareUser.logout(drone);

        // Check the folder is removed on cloud
        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);

        DocumentLibraryPage doclib = ShareUser.openSiteDocumentLibraryFromSearch(hybridDrone, cloudSiteName);

        Assert.assertFalse(doclib.isFileVisible(folderName));

        ShareUser.logout(hybridDrone);

        // Check On-Premise
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUserProfile.navigateToTrashCan(drone);

        Assert.assertTrue(ShareUserProfile.isTrashCanItemPresent(drone, folderName));

        ShareUserProfile.recoverTrashCanItem(drone, folderName);

        Assert.assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, folderName));

        doclib = ShareUser.openSiteDocumentLibraryFromSearch(drone, opSiteName);

        doclib.isFileVisible(folderName);

        Assert.assertTrue(doclib.getFileDirectoryInfo(folderName).isViewCloudSyncInfoLinkPresent());

        Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, file),"ALF-20445: sync is not happening!!");     
        
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);

        doclib = ShareUser.openSiteDocumentLibraryFromSearch(hybridDrone, cloudSiteName);

        Assert.assertTrue(doclib.isFileVisible(folderName));

        Assert.assertTrue(doclib.getFileDirectoryInfo(folderName).isCloudSynced());
        
        // Open Folder
        doclib = doclib.selectFolder(folderName).render();

        Assert.assertTrue(doclib.isFileVisible(file));
        ShareUser.logout(hybridDrone);

    }

     @Test(groups = { "DataPrepTrashCan", "HybridSync", "Enterprise42" })
    public void dataPrep_TrashCan_AONE_15086() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, create site, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
       
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);
    }

    @Test(groups = {"HybridSync", "Enterprise42" }, enabled=false)
    public void AONE_15086() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String file = testName + System.currentTimeMillis() + ".txt";

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        ShareUser.uploadFileInFolder(drone, new String[] { file });

        DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
        desAndAssBean.setNetwork(testDomain);
        desAndAssBean.setSiteName(cloudSiteName);
        AbstractCloudSyncTest.syncContentToCloud(drone, file, desAndAssBean);

        ShareUser.selectContentCheckBox(drone, file);

        ShareUser.deleteSelectedContent(drone);

        ShareUser.logout(drone);

        // Check on Cloud
        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSiteDocumentLibraryFromSearch(hybridDrone, cloudSiteName);

        Assert.assertFalse(docLibPage.isFileVisible(file));

        ShareUser.logout(hybridDrone);

        // Check On Premise
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUserProfile.navigateToTrashCan(drone);

        Assert.assertTrue(ShareUserProfile.isTrashCanItemPresent(drone, file));

        ShareUserProfile.deleteTrashCanItem(drone, file);

        Assert.assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, file));

        docLibPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, opSiteName);

        Assert.assertFalse(docLibPage.isFileVisible(file));

        ShareUser.logout(drone);

        // Check On Cloud

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSiteDocumentLibraryFromSearch(hybridDrone, cloudSiteName);

        Assert.assertFalse(docLibPage.isFileVisible(file));

        ShareUser.logout(hybridDrone);

    }

    @Test(groups = { "DataPrepTrashCan", "HybridSync", "Enterprise42" })
    public void dataPrep_TrashCan_AONE_15087() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, create site, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        signInToAlfrescoInTheCloud(drone, user1, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);
    }

    @Test(groups = { "HybridSync", "Enterprise42" }, enabled=false)
    public void AONE_15087() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        String file = getFileName(testName)+ System.currentTimeMillis()+".text";
        String folderName = testName + System.currentTimeMillis();

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        ShareUserSitePage.createFolder(drone, folderName, folderName);  
        ShareUser.uploadFileInFolder(drone, new String[] { file });

        DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
        desAndAssBean.setNetwork(testDomain);
        desAndAssBean.setSiteName(cloudSiteName);
        AbstractCloudSyncTest.syncContentToCloud(drone, folderName, desAndAssBean);
       
        ShareUser.selectContentCheckBox(drone, folderName);

        ShareUser.deleteSelectedContent(drone);

        ShareUser.logout(drone);

        // Check On Cloud
        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);

        Assert.assertFalse(ShareUser.openSiteDocumentLibraryFromSearch(hybridDrone, cloudSiteName).isFileVisible(folderName));

        ShareUser.logout(hybridDrone);

        // Check On Premise
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUserProfile.navigateToTrashCan(drone);

        Assert.assertTrue(ShareUserProfile.isTrashCanItemPresent(drone, folderName));

        ShareUserProfile.deleteTrashCanItem(drone, folderName);

        Assert.assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, folderName));

        DocumentLibraryPage docLibPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, opSiteName);

        docLibPage.isFileVisible(folderName);

        ShareUser.logout(drone);

        // Check On Cloud

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);

        Assert.assertFalse(ShareUser.openSiteDocumentLibraryFromSearch(hybridDrone, cloudSiteName).isFileVisible(folderName));

        ShareUser.logout(hybridDrone);

    }

     /**
     * AONE-15088: Wiki pages
     */

    @Test(groups = "DataPrepTrashCan")
    public void dataPrep_AONE_15088() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "_1");
        String user2 = getUserNameFreeDomain(testName + "_2");
        String[] userInfo1 = new String[] { user1 };
        String[] userInfo2 = new String[] { user2 };
        String siteName = getSiteName(testName);

        // Create 2 Users
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo2);

        // User1 login
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Invite User2 as collaborator
        ShareUser.openSiteDashboard(drone, siteName);
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, siteName, UserRole.COLLABORATOR);

        // User1 adds Wiki component
        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName).render();
        CustomizeSitePage customizeSitePage = siteDashboardPage.getSiteNav().selectCustomizeSite();
        customizeSitePage.addPages(asList(SitePageType.WIKI)).render();
        WikiPage wikiPage = siteDashboardPage.getSiteNav().selectWikiPage().render();

        // Creating 8 Wiki pages
        for (int i=0; i<8; i++)
        {
            List<String> txtLines = new ArrayList<>();
            txtLines.add(0, testName + "wiki" + i);
            wikiPage.createWikiPage(testName + "wiki" + i, txtLines).render();
        }

        //Navigate to Wiki
        wikiPage = siteDashboardPage.getSiteNav().selectWikiPage().render();
        WikiPageList wikiPageList = wikiPage.clickWikiPageListBtn();

        // Deleting 8 Wiki pages
        for (int i=0; i<8; i++)
        {
           wikiPageList.deleteWikiWithConfirm(testName + "wiki" + i).render();
        }
        ShareUser.logout(drone);

        // User2 login
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);
        siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName);

        // Creating Wiki page
        wikiPage = siteDashboardPage.getSiteNav().selectWikiPage().render();
        List<String> txtLines = new ArrayList<>();
        txtLines.add(0, testName + "wiki8");
        wikiPage.createWikiPage(testName + "wiki8", txtLines).render();

        // Navigate to Wiki
        wikiPage = siteDashboardPage.getSiteNav().selectWikiPage().render();
        wikiPageList = wikiPage.clickWikiPageListBtn();

        // Delete Wiki page
        wikiPageList.deleteWikiWithConfirm(testName + "wiki8").render();

    }

    @Test(groups = "EnterpriseOnly")
    public void AONE_15088() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "_1");
        String siteName = getSiteName(testName);
        String dateOfContentDeletion = ShareUser.getDate(format);
        String[] wikis = new String[9];
        int j=1;
        for (int i = 0; i<wikis.length ; i++)
        {
            wikis[i] = testName + "wiki" + j;
            j++;
        }

        // Log in as User1
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Navigate to My Profile
        // Click on Trashcan
        TrashCanPage trashCanPage = ShareUserProfile.navigateToTrashCan(drone).render();

        // Try to search for deleted items
        List<String> nameOfItems = ShareUserProfile.getTrashCanItems(drone, "");
        Assert.assertFalse(nameOfItems.contains(wikis[8]), wikis[8] + " page is presented in other user's trashcan");
        Assert.assertTrue(nameOfItems.contains(wikis[6]), wikis[6] + " page is not found");

        // Verify the deleted item info
        TrashCanItem itemInfo = ShareUserProfile.getTrashCanItem(drone, wikis[6]);
        Assert.assertEquals(itemInfo.getFileName(), wikis[6]);
        Assert.assertTrue(itemInfo.getDate().contains(dateOfContentDeletion), String.format("Date displayed: %s, Date Expected: %s", itemInfo.getDate(), dateOfContentDeletion));
        Assert.assertTrue(itemInfo.getFolderPath().endsWith("wiki"));
        Assert.assertTrue(itemInfo.getUserFullName().toLowerCase().startsWith(
            user1.toLowerCase()), String.format("Username displayed: %s, Username Expected: %s", itemInfo.getUserFullName(), user1));

        // Recover wiki0
        ShareUserProfile.recoverTrashCanItem(drone, wikis[0]);

        // Verify that the recovered item is not presented
        assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, wikis[0]), wikis[0] + " page is presented in Trashcan");

        // Delete wiki1
        ShareUserProfile.deleteTrashCanItem(drone, wikis[1]);

        // Verify that the deleted item is not presented
        assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, wikis[1]), wikis[1] + " page is presented in Trashcan");

        // Recover two wiki pages (wiki2 and wiki3)
        TrashCanItem wiki2 = ShareUserProfile.getTrashCanItem(drone, wikis[2]);
        TrashCanItem wiki3 = ShareUserProfile.getTrashCanItem(drone, wikis[3]);
        wiki2.selectTrashCanItemCheckBox();
        wiki3.selectTrashCanItemCheckBox();
        TrashCanRecoverConfirmDialog trashCanRecoverConfirmation = trashCanPage.selectedRecover().render();
        assertEquals (trashCanRecoverConfirmation.getNotificationMessage(),"Successfully recovered 2 item(s), 0 failed.");
        trashCanPage = trashCanRecoverConfirmation.clickRecoverOK().render();
        nameOfItems = ShareUserProfile.getTrashCanItems(drone, "");
        Assert.assertFalse(nameOfItems.contains(wikis[2]), wikis[2] + " is presented in Trashcan");
        Assert.assertFalse(nameOfItems.contains(wikis[3]), wikis[3] + " is presented in Trashcan");

        // Delete two wiki pages (wiki4 and wiki5)
        TrashCanItem wiki4 = ShareUserProfile.getTrashCanItem(drone, wikis[4]);
        TrashCanItem wiki5 = ShareUserProfile.getTrashCanItem(drone, wikis[5]);
        wiki4.selectTrashCanItemCheckBox();
        wiki5.selectTrashCanItemCheckBox();
        TrashCanDeleteConfirmationPage trashCanConfirmationDeleteDialog = trashCanPage.selectedDelete().render();
        assertTrue(trashCanConfirmationDeleteDialog.isConfirmationDialogDisplayed());
        assertEquals(trashCanRecoverConfirmation.getNotificationMessage(), "This will permanently delete the item(s). Are you sure?");
        trashCanConfirmationDeleteDialog.clickOkButton();
        assertEquals (trashCanConfirmationDeleteDialog.getNotificationMessage(),"Successfully deleted 2 item(s), 0 failed.");
        trashCanConfirmationDeleteDialog.clickOkButton();
        trashCanPage = getDrone().getCurrentPage().render();
        nameOfItems = ShareUserProfile.getTrashCanItems(drone, "");
        Assert.assertFalse(nameOfItems.contains(wikis[4]), wikis[4] + " is presented in Trashcan");
        Assert.assertFalse(nameOfItems.contains(wikis[5]), wikis[5] + " is presented in Trashcan");

        // Empty TrashCan
        TrashCanEmptyConfirmationPage trashCanEmptyConfirmationPage = trashCanPage.selectEmpty();
        assertTrue(trashCanConfirmationDeleteDialog.isConfirmationDialogDisplayed());
        trashCanPage = trashCanEmptyConfirmationPage.clickOkButton();
        assertFalse(trashCanPage.hasTrashCanItems());
        assertTrue (trashCanPage.checkNoItemsMessage());

        // Verify that the not recovered items aren't present
        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName);
        WikiPage wikiPage = siteDashboardPage.getSiteNav().selectWikiPage().render();
        WikiPageList wikiPageList = wikiPage.clickWikiPageListBtn();
        assertFalse(wikiPageList.isWikiPagePresent(wikis[5]), wikis[5] + " page is presented in Wiki page");
        assertFalse(wikiPageList.isWikiPagePresent(wikis[4]), wikis[4] + " page is presented in Wiki page");
        assertFalse(wikiPageList.isWikiPagePresent(wikis[1]), wikis[1] + " page is presented in Wiki page");
        assertTrue(wikiPageList.isWikiPagePresent(wikis[0]), wikis[0] + " page is not presented in Wiki page");
        assertTrue(wikiPageList.isWikiPagePresent(wikis[2]), wikis[2] + " page is not presented in Wiki page");
        assertTrue(wikiPageList.isWikiPagePresent(wikis[3]), wikis[3] + " page is not presented in Wiki page");

    }

    /**
     * AONE-15089:Blog posts
     */

    @Test(groups = "DataPrepTrashCan")
    public void dataPrep_AONE_15089() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "_1");
        String user2 = getUserNameFreeDomain(testName + "_2");
        String[] userInfo1 = new String[] { user1 };
        String[] userInfo2 = new String[] { user2 };
        String siteName = getSiteName(testName);

        // Create 2 Users
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo2);

        // User1 login
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Invite User2 as collaborator
        ShareUser.openSiteDashboard(drone, siteName);
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, siteName, UserRole.COLLABORATOR);

        // User1 adds the Blog component
        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName).render();
        CustomizeSitePage customizeSitePage = siteDashboardPage.getSiteNav().selectCustomizeSite();
        customizeSitePage.addPages(asList(SitePageType.BLOG)).render();
    }

    @Test(groups = "EnterpriseOnly")
    public void AONE_15089() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "_1");
        String user2 = getUserNameFreeDomain(testName + "_2");
        String siteName = getSiteName(testName);
        String dateOfContentDeletion = ShareUser.getDate(format);
        String[] postIds = new String[8];
        String[] posts = new String[9];
        for (int i = 0; i<posts.length ; i++)
        {
            posts[i] = testName + "blog" + i;
        }

        // Log in as User1
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Open Blog page
        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName).render();
        BlogPage blogPage = siteDashboardPage.getSiteNav().selectBlogPage().render();

        // Creating 8 posts
        for (int i = 0; i<postIds.length ; i++)
        {
            blogPage.createPostInternally(testName + "blog" + i, testName + "_" + i).render();
            postIds[i] = BlogUtil.getPostId(drone, siteName, testName + "blog" + i );
        }

        //Navigate to Blog page
        blogPage = siteDashboardPage.getSiteNav().selectBlogPage().render();

        // Deleting 8 posts
        for (int i=0; i<8; i++)
        {
            PostViewPage postViewPage = blogPage.openBlogPost(testName + "blog" + i);
            blogPage = postViewPage.deleteBlogPostWithConfirm().render();
        }
        ShareUser.logout(drone);

        // User2 login
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);
        siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName);

        // Creating post
        blogPage = siteDashboardPage.getSiteNav().selectBlogPage().render();
        blogPage.createPostInternally(testName + "blog9", testName + "_9").render();
        String postId8 = BlogUtil.getPostId(drone, siteName, testName + "blog9");

        // Navigate to Blog page
        blogPage = siteDashboardPage.getSiteNav().selectBlogPage().render();
        PostViewPage postViewPage = blogPage.openBlogPost(testName + "blog9");

        // Delete post
        postViewPage.deleteBlogPostWithConfirm().render();
        ShareUser.logout(drone);

        // Log in as User1
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Navigate to My Profile
        // Click on Trashcan
        TrashCanPage trashCanPage = ShareUserProfile.navigateToTrashCan(drone).render();

        // Try to search for deleted items
        List<String> nameOfItems = ShareUserProfile.getTrashCanItems(drone, "");
        Assert.assertFalse(nameOfItems.contains(postId8), posts[8] + " is presented in other user's trashcan");
        Assert.assertTrue(nameOfItems.contains(postIds[6]), posts[6] + " is not found");

        // Verify the deleted item info
        TrashCanItem itemInfo = ShareUserProfile.getTrashCanItem(drone, postIds[6]);
        Assert.assertEquals(itemInfo.getFileName(), postIds[6]);
        Assert.assertTrue(itemInfo.getDate().contains(dateOfContentDeletion), String.format("Date displayed: %s, Date Expected: %s", itemInfo.getDate(), dateOfContentDeletion));
        Assert.assertTrue(itemInfo.getFolderPath().endsWith("blog"));
        Assert.assertTrue(itemInfo.getUserFullName().toLowerCase().startsWith(
            user1.toLowerCase()), String.format("Username displayed: %s, Username Expected: %s", itemInfo.getUserFullName(), user1));

        // Recover post0
        ShareUserProfile.recoverTrashCanItem(drone, postIds[0]);

        // Verify that the recovered item is not presented
        assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, postIds[0]), posts[0] + " is presented in Trashcan");

        // Delete post1
        ShareUserProfile.deleteTrashCanItem(drone, postIds [1]);

        // Verify that the deleted item is not presented
        assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, postIds[1]), posts[1] + "  is presented in Trashcan");

        // Recover two posts  (post2 and post3)
        TrashCanItem post2 = ShareUserProfile.getTrashCanItem(drone, postIds[2]);
        TrashCanItem post3 = ShareUserProfile.getTrashCanItem(drone, postIds[3]);
        post2.selectTrashCanItemCheckBox();
        post3.selectTrashCanItemCheckBox();
        TrashCanRecoverConfirmDialog trashCanRecoverConfirmation = trashCanPage.selectedRecover().render();
        assertEquals (trashCanRecoverConfirmation.getNotificationMessage(),"Successfully recovered 2 item(s), 0 failed.");
        trashCanPage = trashCanRecoverConfirmation.clickRecoverOK().render();
        nameOfItems = ShareUserProfile.getTrashCanItems(drone, "");
        Assert.assertFalse(nameOfItems.contains(postIds[2]), posts[2] + " is presented in Trashcan");
        Assert.assertFalse(nameOfItems.contains(postIds[3]), posts[3] + " is presented in Trashcan");

        // Delete two posts (post4 and post5)
        TrashCanItem post4 = ShareUserProfile.getTrashCanItem(drone, postIds[4]);
        TrashCanItem post5 = ShareUserProfile.getTrashCanItem(drone, postIds[5]);
        post4.selectTrashCanItemCheckBox();
        post5.selectTrashCanItemCheckBox();
        TrashCanDeleteConfirmationPage trashCanConfirmationDeleteDialog = trashCanPage.selectedDelete().render();
        assertTrue(trashCanConfirmationDeleteDialog.isConfirmationDialogDisplayed());
        assertEquals (trashCanRecoverConfirmation.getNotificationMessage(),"This will permanently delete the item(s). Are you sure?");
        trashCanConfirmationDeleteDialog.clickOkButton();
        assertEquals (trashCanConfirmationDeleteDialog.getNotificationMessage(),"Successfully deleted 2 item(s), 0 failed.");
        trashCanConfirmationDeleteDialog.clickOkButton();
        trashCanPage = getDrone().getCurrentPage().render();
        nameOfItems = ShareUserProfile.getTrashCanItems(drone, "");
        Assert.assertFalse(nameOfItems.contains(postIds[4]), posts[4] + " is presented in Trashcan");
        Assert.assertFalse(nameOfItems.contains(postIds[5]), posts[5] + " is presented in Trashcan");

        // Empty TrashCan
        TrashCanEmptyConfirmationPage trashCanEmptyConfirmationPage = trashCanPage.selectEmpty();
        assertTrue(trashCanConfirmationDeleteDialog.isConfirmationDialogDisplayed());
        trashCanPage = trashCanEmptyConfirmationPage.clickOkButton();
        assertFalse(trashCanPage.hasTrashCanItems());
        assertTrue (trashCanPage.checkNoItemsMessage());

        // Verify that the not recovered items aren't present
        siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName);
        blogPage = siteDashboardPage.getSiteNav().selectBlogPage().render();

        assertFalse(blogPage.isPostPresented(posts[5]), posts[5] + " is presented in the Blog page");
        assertFalse(blogPage.isPostPresented(posts[4]), posts[4] + " is presented in the Blog page");
        assertFalse(blogPage.isPostPresented(posts[1]), posts[1] + " is presented in the Blog page");
        assertTrue(blogPage.isPostPresented(posts[0]), posts[0] + " is not presented in the Blog page");
        assertTrue(blogPage.isPostPresented(posts[2]), posts[2] + " is not presented in the Blog page");
        assertTrue(blogPage.isPostPresented(posts[3]), posts[3] + " is not presented in the Blog page");
    }

    /**
     * AONE-15090:Links
     */

    @Test(groups = "DataPrepTrashCan")
    public void dataPrep_AONE_15090() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "_1");
        String user2 = getUserNameFreeDomain(testName + "_2");
        String[] userInfo1 = new String[] { user1 };
        String[] userInfo2 = new String[] { user2 };
        String siteName = getSiteName(testName);

        // Create 2 Users
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo2);

        // User1 login
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Invite User2 as collaborator
        ShareUser.openSiteDashboard(drone, siteName);
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, siteName, UserRole.COLLABORATOR);

        // User1 adds the Links component
        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName).render();
        CustomizeSitePage customizeSitePage = siteDashboardPage.getSiteNav().selectCustomizeSite();
        customizeSitePage.addPages(asList(SitePageType.LINKS)).render();
    }

    @Test(groups = "EnterpriseOnly")
    public void AONE_15090() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "_1");
        String user2 = getUserNameFreeDomain(testName + "_2");
        String siteName = getSiteName(testName);
        String dateOfContentDeletion = ShareUser.getDate(format);
        String linkUrl = "http://alfresco.com";
        String[] linkIds = new String[8];
        String[] links = new String[9];
        for (int i = 0; i<links.length ; i++)
        {
            links[i] = testName + "link" + i;
        }

        // Log in as User1
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Open Link page
        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName).render();

        // Creating 8 links
        for (int i = 0; i<linkIds.length ; i++)
        {
            LinksPage linksPage = siteDashboardPage.getSiteNav().selectLinksPage();
            linksPage.createLink(testName + "link" + i, linkUrl).render();
            linkIds[i] = LinkUtil.getLinkId(drone, siteName, testName + "link" + i);
        }

        //Navigate to Link page
        LinksPage linksPage = siteDashboardPage.getSiteNav().selectLinksPage();

        // Deleting 8 links
        for (int i=0; i<8; i++)
        {
            linksPage = linksPage.deleteLinkWithConfirm(testName + "link" + i);
        }
        ShareUser.logout(drone);

        // User2 login
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);
        siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName);

        // Creating link
        linksPage = siteDashboardPage.getSiteNav().selectLinksPage();
        linksPage.createLink(testName + "link8", linkUrl).render();
        String linkId8 = LinkUtil.getLinkId(drone, siteName, testName + "link8");

        // Navigate to Link page
        linksPage = siteDashboardPage.getSiteNav().selectLinksPage();

        // Delete link
        linksPage.deleteLinkWithConfirm (testName + "link8");
        ShareUser.logout(drone);

        // Log in as User1
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Navigate to My Profile
        // Click on Trashcan
        TrashCanPage trashCanPage = ShareUserProfile.navigateToTrashCan(drone).render();

        // Try to search for deleted items
        List<String> nameOfItems = ShareUserProfile.getTrashCanItems(drone, "");
        Assert.assertFalse(nameOfItems.contains(linkId8), links[8] + " is presented in other user's trashcan");
        Assert.assertTrue(nameOfItems.contains(linkIds[6]), links[6] + " is not found");

        // Verify the deleted item info
        TrashCanItem itemInfo = ShareUserProfile.getTrashCanItem(drone, linkIds[6]);
        Assert.assertEquals(itemInfo.getFileName(), linkIds[6]);
        Assert.assertTrue(itemInfo.getDate().contains(dateOfContentDeletion), String.format("Date displayed: %s, Date Expected: %s", itemInfo.getDate(), dateOfContentDeletion));
        Assert.assertTrue(itemInfo.getFolderPath().endsWith("links"));
        Assert.assertTrue(itemInfo.getUserFullName().toLowerCase().startsWith(
            user1.toLowerCase()), String.format("Username displayed: %s, Username Expected: %s", itemInfo.getUserFullName(), user1));

        // Recover link0
        ShareUserProfile.recoverTrashCanItem(drone, linkIds[0]);

        // Verify that the recovered item is not presented
        assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, linkIds[0]), links[0] + " is presented in Trashcan");

        // Delete link1
        ShareUserProfile.deleteTrashCanItem(drone, linkIds [1]);

        // Verify that the deleted item is not presented
        assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, linkIds[1]), links[1] + "  is presented in Trashcan");

        // Recover two links (link2 and link3)
        TrashCanItem link2 = ShareUserProfile.getTrashCanItem(drone, linkIds[2]);
        TrashCanItem link3 = ShareUserProfile.getTrashCanItem(drone, linkIds[3]);
        link2.selectTrashCanItemCheckBox();
        link3.selectTrashCanItemCheckBox();
        TrashCanRecoverConfirmDialog trashCanRecoverConfirmation = trashCanPage.selectedRecover().render();
        assertEquals (trashCanRecoverConfirmation.getNotificationMessage(),"Successfully recovered 2 item(s), 0 failed.");
        trashCanPage = trashCanRecoverConfirmation.clickRecoverOK().render();
        nameOfItems = ShareUserProfile.getTrashCanItems(drone, "");
        Assert.assertFalse(nameOfItems.contains(linkIds[2]), links[2] + " is presented in Trashcan");
        Assert.assertFalse(nameOfItems.contains(linkIds[3]), links[3] + " is presented in Trashcan");

        // Delete two links (link4 and link5)
        TrashCanItem link4 = ShareUserProfile.getTrashCanItem(drone, linkIds[4]);
        TrashCanItem link5 = ShareUserProfile.getTrashCanItem(drone, linkIds[5]);
        link4.selectTrashCanItemCheckBox();
        link5.selectTrashCanItemCheckBox();
        TrashCanDeleteConfirmationPage trashCanConfirmationDeleteDialog = trashCanPage.selectedDelete().render();
        assertTrue(trashCanConfirmationDeleteDialog.isConfirmationDialogDisplayed());
        assertEquals (trashCanConfirmationDeleteDialog.getNotificationMessage(),"This will permanently delete the item(s). Are you sure?");
        trashCanConfirmationDeleteDialog.clickOkButton();
        assertEquals (trashCanConfirmationDeleteDialog.getNotificationMessage(),"Successfully deleted 2 item(s), 0 failed.");
        trashCanConfirmationDeleteDialog.clickOkButton();
        trashCanPage = getDrone().getCurrentPage().render();
        nameOfItems = ShareUserProfile.getTrashCanItems(drone, "");
        Assert.assertFalse(nameOfItems.contains(linkIds), links[4] + " is presented in Trashcan");
        Assert.assertFalse(nameOfItems.contains(linkIds), links[5] + " is presented in Trashcan");

        // Empty TrashCan
        TrashCanEmptyConfirmationPage trashCanEmptyConfirmationPage = trashCanPage.selectEmpty();
        assertTrue(trashCanConfirmationDeleteDialog.isConfirmationDialogDisplayed());
        trashCanPage = trashCanEmptyConfirmationPage.clickOkButton();
        assertFalse(trashCanPage.hasTrashCanItems());
        assertTrue (trashCanPage.checkNoItemsMessage());

        // Verify that the not recovered items aren't present
        siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName);
        linksPage = siteDashboardPage.getSiteNav().selectLinksPage();

        assertFalse(linksPage.isLinkPresented(links[5]), links[5] + " is presented in the Links page");
        assertFalse(linksPage.isLinkPresented(links[4]), links[4] + " is presented in the Links page");
        assertFalse(linksPage.isLinkPresented(links[1]), links[1] + " is presented in the Links page");
        assertTrue(linksPage.isLinkPresented(links[0]), links[0] + " is not presented in the Links page");
        assertTrue(linksPage.isLinkPresented(links[2]), links[2] + " is not presented in the Links page");
        assertTrue(linksPage.isLinkPresented(links[3]), links[3] + " is not presented in the Links page");
    }

    /**
     * AONE-15091:Data Lists
     */

    @Test(groups = "DataPrepTrashCan")
    public void dataPrep_AONE_15091() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "_1");
        String user2 = getUserNameFreeDomain(testName + "_2");
        String[] userInfo1 = new String[] { user1 };
        String[] userInfo2 = new String[] { user2 };
        String siteName = getSiteName(testName);

        // Create 2 Users
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo2);

        // User1 login
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Invite User2 as collaborator
        ShareUser.openSiteDashboard(drone, siteName);
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, siteName, UserRole.COLLABORATOR);

        // User1 adds the Data Lists component
        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName).render();
        CustomizeSitePage customizeSitePage = siteDashboardPage.getSiteNav().selectCustomizeSite();
        customizeSitePage.addPages(asList(SitePageType.DATA_LISTS)).render();
    }

    @Test(groups = "EnterpriseOnly")
    public void AONE_15091() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "_1");
        String user2 = getUserNameFreeDomain(testName + "_2");
        String siteName = getSiteName(testName);
        String dateOfContentDeletion = ShareUser.getDate(format);
        String[] listIds = new String[8];
        String[] lists = new String[9];
        for (int i = 0; i<lists.length ; i++)
        {
            lists[i] = testName + "dataList" + i;
        }

        // Log in as User1
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Open Data Lists page
        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName).render();
        siteDashboardPage.getSiteNav().selectDataListPage().render();
        DataListPage dataListPage = new DataListPage(drone).render();

        // Creating 8 data lists
        for (int i = 0; i<listIds.length ; i++)
        {
            dataListPage.createDataList(CONTACT_LIST, testName + "dataList" + i, testName);
            listIds[i] = DataListUtil.getListId(drone, siteName, testName + "dataList" + i);
        }

        //Navigate to Data Lists page
        dataListPage = siteDashboardPage.getSiteNav().selectDataListPage().render();

        // Deleting 8 data lists
        for (int i=0; i<8; i++)
        {
            dataListPage = dataListPage.deleteDataListWithConfirm(testName + "dataList" + i);
        }
        ShareUser.logout(drone);

        // User2 login
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);
        siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName);
        siteDashboardPage.getSiteNav().selectDataListPage().render();

        // Creating data list
        dataListPage = new DataListPage(drone).render();
        dataListPage.createDataList(CONTACT_LIST, testName + "dataList8", testName).render();
        String listId8 = DataListUtil.getListId(drone, siteName, testName + "dataList8");

        // Navigate to Data Lists
        dataListPage = siteDashboardPage.getSiteNav().selectDataListPage().render();

        // Delete  data list
        dataListPage.deleteDataListWithConfirm(testName + "dataList8");
        ShareUser.logout(drone);

        // Log in as User1
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Navigate to My Profile
        // Click on Trashcan
        TrashCanPage trashCanPage = ShareUserProfile.navigateToTrashCan(drone).render();

        // Try to search for deleted items
        List<String> nameOfItems = ShareUserProfile.getTrashCanItems(drone, "");
        Assert.assertFalse(nameOfItems.contains(listId8), lists[8] + " is presented in other user's trashcan");
        Assert.assertTrue(nameOfItems.contains(listIds[6]), lists[6] + " is not found");

        // Verify the deleted item info
        TrashCanItem itemInfo = ShareUserProfile.getTrashCanItem(drone, listIds[6]);
        Assert.assertEquals(itemInfo.getFileName(), listIds[6]);
        Assert.assertTrue(itemInfo.getDate().contains(dateOfContentDeletion), String.format("Date displayed: %s, Date Expected: %s", itemInfo.getDate(), dateOfContentDeletion));
        Assert.assertTrue(itemInfo.getFolderPath().endsWith("dataLists"));
        Assert.assertTrue(itemInfo.getUserFullName().toLowerCase().startsWith(
            user1.toLowerCase()), String.format("Username displayed: %s, Username Expected: %s", itemInfo.getUserFullName(), user1));

        // Recover data list0
        ShareUserProfile.recoverTrashCanItem(drone, listIds[0]);

        // Verify that the recovered item is not presented
        assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, listIds[0]), lists[0] + " is presented in Trashcan");

        // Delete data list1
        ShareUserProfile.deleteTrashCanItem(drone, listIds [1]);

        // Verify that the deleted item is not presented
        assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, listIds[1]), lists[1] + "  is presented in Trashcan");

        // Recover two data lists (data list2 and data list3)
        TrashCanItem list2 = ShareUserProfile.getTrashCanItem(drone, listIds[2]);
        TrashCanItem list3 = ShareUserProfile.getTrashCanItem(drone, listIds[3]);
        list2.selectTrashCanItemCheckBox();
        list3.selectTrashCanItemCheckBox();
        TrashCanRecoverConfirmDialog trashCanRecoverConfirmation = trashCanPage.selectedRecover().render();
        assertEquals (trashCanRecoverConfirmation.getNotificationMessage(),"Successfully recovered 2 item(s), 0 failed.");
        trashCanPage = trashCanRecoverConfirmation.clickRecoverOK().render();
        nameOfItems = ShareUserProfile.getTrashCanItems(drone, "");
        Assert.assertFalse(nameOfItems.contains(listIds[2]), lists[2] + " is presented in Trashcan");
        Assert.assertFalse(nameOfItems.contains(listIds[3]), lists[3] + " is presented in Trashcan");

        // Delete two data lists (data list4 and data list5)
        TrashCanItem list4 = ShareUserProfile.getTrashCanItem(drone, listIds[4]);
        TrashCanItem list5 = ShareUserProfile.getTrashCanItem(drone, listIds[5]);
        list4.selectTrashCanItemCheckBox();
        list5.selectTrashCanItemCheckBox();
        TrashCanDeleteConfirmationPage trashCanConfirmationDeleteDialog = trashCanPage.selectedDelete().render();
        assertTrue(trashCanConfirmationDeleteDialog.isConfirmationDialogDisplayed());
        assertEquals (trashCanConfirmationDeleteDialog.getNotificationMessage(),"This will permanently delete the item(s). Are you sure?");
        trashCanConfirmationDeleteDialog.clickOkButton();
        assertEquals (trashCanConfirmationDeleteDialog.getNotificationMessage(),"Successfully deleted 2 item(s), 0 failed.");
        trashCanConfirmationDeleteDialog.clickOkButton();
        trashCanPage = getDrone().getCurrentPage().render();
        nameOfItems = ShareUserProfile.getTrashCanItems(drone, "");
        Assert.assertFalse(nameOfItems.contains(listIds), lists[4] + " is presented in Trashcan");
        Assert.assertFalse(nameOfItems.contains(listIds), lists[5] + " is presented in Trashcan");

        // Empty TrashCan
        TrashCanEmptyConfirmationPage trashCanEmptyConfirmationPage = trashCanPage.selectEmpty();
        assertTrue(trashCanConfirmationDeleteDialog.isConfirmationDialogDisplayed());
        trashCanPage = trashCanEmptyConfirmationPage.clickOkButton();
        assertFalse(trashCanPage.hasTrashCanItems());
        assertTrue (trashCanPage.checkNoItemsMessage());

        // Verify that the not recovered items aren't present
        siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName);
        dataListPage = siteDashboardPage.getSiteNav().selectDataListPage().render();
        List<String> dataLists = dataListPage.getLists();
        assertFalse(dataLists.contains(lists[5]), lists[5] + " is presented in the Data Lists page");
        assertFalse(dataLists.contains(lists[4]), lists[4] + " is presented in the Data Lists page");
        assertFalse(dataLists.contains(lists[1]), lists[1] + " is presented in the Data Lists page");
        assertTrue(dataLists.contains(lists[0]), lists[0] + " is not presented in the Data Lists page");
        assertTrue(dataLists.contains(lists[2]), lists[2] + " is not presented in the Data Lists page");
        assertTrue(dataLists.contains(lists[3]), lists[3] + " is not presented in the Data Lists page");
    }

}
