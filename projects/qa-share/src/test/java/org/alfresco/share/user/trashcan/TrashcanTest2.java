package org.alfresco.share.user.trashcan;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.blog.BlogPage;
import org.alfresco.po.share.site.blog.PostViewPage;
import org.alfresco.po.share.site.datalist.DataListPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.SyncInfoPage;
import org.alfresco.po.share.site.links.LinksDetailsPage;
import org.alfresco.po.share.site.links.LinksPage;
import org.alfresco.po.share.site.wiki.WikiPage;
import org.alfresco.po.share.site.wiki.WikiPageList;
import org.alfresco.po.share.user.*;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.alfresco.po.share.enums.DataLists.CONTACT_LIST;
import static org.testng.Assert.*;

/**
 * @author Maryia Zaichanka
 */

@Listeners(FailedTestListener.class)
public class TrashcanTest2 extends AbstractUtils
{
    private static final Logger logger = Logger.getLogger(TrashcanTest2.class);

    private String format = "EEE d MMM YYYY";

    private String getCustomRoleName(String siteName, UserRole role)
    {
        return String.format("site_%s_%s", ShareUser.getSiteShortname(siteName), StringUtils.replace(role.getRoleName().trim(), " ", ""));
    }

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {

        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
    }

    @AfterMethod(groups = { "AlfrescoOne" })
    public void quit() throws Exception
    {
        // Login as created user
        try
        {
            ShareUser.logout(drone);
            logger.info("Trashcan user logged out - drone.");
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14193() throws Exception
    {

        String testName = getTestName();
        String siteName1 = getSiteName(testName);
        String trashcanUser = getUserNameFreeDomain(testName);

        String fileName1 = getFileName(testName);
        String folderName1 = getFolderName(testName);

        for (int i = 1; i <= 2; i++)
        {
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, trashcanUser + i);
        }

        ShareUser.login(drone, trashcanUser + 1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName1);
        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB });
        ShareUserSitePage.createFolder(drone, folderName1, folderName1);
        ShareUser.selectContentCheckBox(drone, fileName1);
        ShareUser.selectContentCheckBox(drone, folderName1);
        ShareUser.deleteSelectedContent(drone);

        ShareUser.logout(drone);
    }

    @Test(groups = { "AlfrescoOne" })
    public void AONE_14193() throws Exception
    {
        String testName = getTestName();
        String trashcanUser = getUserNameFreeDomain(testName);
        String fileName1 = getFileName(testName);
        String folderName1 = getFolderName(testName);

        // Log in as User2
        ShareUser.login(drone, trashcanUser + 2, DEFAULT_PASSWORD);

        // Navigate to My Profile
        // Click on Trashcan
        TrashCanPage trashCanPage = ShareUserProfile.navigateToTrashCan(drone).render();

        // Verify the trashcan
        Assert.assertFalse(trashCanPage.hasTrashCanItems(), "A trashcan isn't empty");

        // Try to search for deleted items
        List<String> nameOfItems = ShareUserProfile.getTrashCanItems(drone, "");

        Assert.assertFalse(nameOfItems.contains(fileName1), "The deleted item is present in other user's trashcan");
        Assert.assertFalse(nameOfItems.contains(folderName1), "The deleted item is present in other user's trashcan");

        ShareUser.logout(drone);
    }

    @Test(groups = { "AlfrescoOne" })
    public void AONE_14185() throws Exception
    {
        String testName = getTestName();
        String trashcanUser = getUserNameFreeDomain(testName);
        String searchText = getRandomString(5);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, trashcanUser);

        // Log in as created user
        ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        // Trashcan is opened
        TrashCanPage trashCanPage = ShareUserProfile.navigateToTrashCan(drone).render();

        // Fill in 'Search' field with any string
        ShareUserProfile.searchInput(drone).sendKeys(searchText);
        Assert.assertFalse(ShareUserProfile.getInputText(drone).contains(searchText), "Search field isn't filled with search text");

        // Click 'Clear' button
        trashCanPage.clearSearch().render();
        Assert.assertTrue(ShareUserProfile.getInputText(drone).isEmpty(), "Search field isn't cleared");

        // Do no fill in any text and click 'Clear' button
        trashCanPage.clearSearch().render();
        Assert.assertTrue(ShareUserProfile.getInputText(drone).isEmpty(), "Search field isn't cleared");

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_TrashCan_AONE_14173() throws Exception
    {
        String testName = getTestName();

        String trashcanUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { trashcanUser });

        ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        ShareUser.logout(drone);
    }

    @Test(groups = { "AlfrescoOne" })
    public void AONE_14173() throws Exception
    {
        String testName = getTestName();
        String trashcanUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        String fileName1 = getFileName(testName) + "-1" + System.currentTimeMillis();
        String fileName2 = getFileName(testName) + "-2" + System.currentTimeMillis();
        String folderName1 = getFolderName(testName) + "-3" + System.currentTimeMillis();
        String folderName2 = getFolderName(testName) + "-4" + System.currentTimeMillis();

        ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB });

        ShareUser.uploadFileInFolder(drone, new String[] { fileName2, DOCLIB });

        ShareUserSitePage.createFolder(drone, folderName1, folderName1);

        ShareUserSitePage.createFolder(drone, folderName2, folderName2);

        ShareUser.selectContentCheckBox(drone, fileName1);
        ShareUser.selectContentCheckBox(drone, fileName2);
        ShareUser.selectContentCheckBox(drone, folderName1);
        ShareUser.selectContentCheckBox(drone, folderName2);

        DocumentLibraryPage docLibPage = ShareUser.deleteSelectedContent(drone);

        Assert.assertFalse(docLibPage.isFileVisible(fileName1), fileName1 + "is deleted");
        Assert.assertFalse(docLibPage.isFileVisible(fileName2), fileName2 + "is deleted");
        Assert.assertFalse(docLibPage.isFileVisible(folderName1), folderName1 + "is deleted");
        Assert.assertFalse(docLibPage.isFileVisible(folderName2), folderName2 + "is deleted");

        ShareUserProfile.navigateToTrashCan(drone);

        Assert.assertTrue(ShareUserProfile.getTrashCanItems(drone, fileName1).contains(fileName1));
        Assert.assertTrue(ShareUserProfile.getTrashCanItems(drone, fileName2).contains(fileName2));
        Assert.assertTrue(ShareUserProfile.getTrashCanItems(drone, folderName1).contains(folderName1));
        Assert.assertTrue(ShareUserProfile.getTrashCanItems(drone, folderName2).contains(folderName2));

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_TrashCan_AONE_14175() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + System.currentTimeMillis();
        String folderName = getFolderName(testName) + System.currentTimeMillis();

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser });

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        for (int i = 0; i < 4; i++)
        {
            ShareUser.uploadFileInFolder(drone, new String[] { i + "-" + fileName, DOCLIB }).render();
            ShareUserSitePage.createFolder(drone, i + "-" + folderName, i + "-" + folderName);
        }
        for (int i = 0; i < 3; i++)
        {
            ShareUser.createCopyOfAllContent(drone);
        }

        ShareUser.deleteAllContentFromDocumentLibrary(drone);

        ShareUser.logout(drone);
    }

    @Test(groups = { "AlfrescoOne" })
    public void AONE_14175() throws Exception
    {
        String testName = getTestName();
        String trashcanUser = getUserNameFreeDomain(testName);

        ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        TrashCanPage trashCanPage = ShareUserProfile.navigateToTrashCan(drone);

        // TODO: Naved: Missing Step 3: Click on Page 2

        // TODO: Naved: Missing Step 4: Click on Page 1

        Assert.assertTrue(trashCanPage.hasNextPage());

        trashCanPage.selectNextPage();

        Assert.assertTrue(trashCanPage.render().hasPreviousPage());

        trashCanPage.render().selectPreviousPage();

        ShareUser.logout(drone);

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_TrashCan_AONE_14174() throws Exception
    {

        String testName = getTestName();
        String trashcanUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { trashcanUser });

        ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        ShareUser.logout(drone);
    }

    @Test(groups = { "AlfrescoOne" })
    public void AONE_14174() throws Exception
    {
        String testName = getTestName();
        String trashcanUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        String fileName1 = getFileName(testName) + "-1" + System.currentTimeMillis();
        String fileName2 = getFileName(testName) + "-2" + System.currentTimeMillis();

        String folderName1 = getFolderName(testName) + "-1" + System.currentTimeMillis();
        String folderName2 = getFolderName(testName) + "-2" + System.currentTimeMillis();

        ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUserSitePage.createFolder(drone, folderName1, folderName1);
        ShareUserSitePage.createFolder(drone, folderName2, folderName2);

        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB });
        ShareUser.uploadFileInFolder(drone, new String[] { fileName2, DOCLIB });

        ShareUser.selectContentCheckBox(drone, folderName1);
        ShareUser.selectContentCheckBox(drone, folderName2);
        ShareUser.selectContentCheckBox(drone, fileName1);
        ShareUser.selectContentCheckBox(drone, fileName2);

        ShareUser.deleteSelectedContent(drone);
        String dateOfContentDeletion = ShareUser.getDate(format);

        ShareUserProfile.navigateToTrashCan(drone);

        TrashCanItem itemInfo = ShareUserProfile.getTrashCanItem(drone, folderName1);

        Assert.assertEquals(itemInfo.getFileName(), folderName1);
        Assert.assertTrue(itemInfo.getDate().contains(dateOfContentDeletion),
            String.format("Date displayed: %s, Date Expected: %s", itemInfo.getDate(), dateOfContentDeletion));
        Assert.assertTrue(itemInfo.getFolderPath().endsWith(DOCLIB_CONTAINER));
        Assert.assertTrue(itemInfo.getUserFullName().startsWith(trashcanUser));

        itemInfo = ShareUserProfile.getTrashCanItem(drone, folderName2);

        Assert.assertEquals(itemInfo.getFileName(), folderName2);
        Assert.assertTrue(itemInfo.getDate().contains(dateOfContentDeletion));
        Assert.assertTrue(itemInfo.getFolderPath().endsWith(DOCLIB_CONTAINER));
        Assert.assertTrue(itemInfo.getUserFullName().startsWith(trashcanUser));

        itemInfo = ShareUserProfile.getTrashCanItem(drone, fileName1);

        Assert.assertEquals(itemInfo.getFileName(), fileName1);
        Assert.assertTrue(itemInfo.getDate().contains(dateOfContentDeletion));
        Assert.assertTrue(itemInfo.getFolderPath().endsWith(DOCLIB_CONTAINER));
        Assert.assertTrue(itemInfo.getUserFullName().startsWith(trashcanUser));

        itemInfo = ShareUserProfile.getTrashCanItem(drone, fileName2);

        Assert.assertEquals(itemInfo.getFileName(), fileName2);
        Assert.assertTrue(itemInfo.getDate().contains(dateOfContentDeletion));
        Assert.assertTrue(itemInfo.getFolderPath().endsWith(DOCLIB_CONTAINER));
        Assert.assertTrue(itemInfo.getUserFullName().startsWith(trashcanUser));
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_TrashCan_AONE_14176() throws Exception
    {
        String testName = getTestName();
        String trashcanUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { trashcanUser });

        ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        ShareUser.logout(drone);
    }

    /**
     * AONE_14176
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_14176() throws Exception
    {
        String testName = getTestName();

        String testUser = getUserNameFreeDomain(testName);

        String siteName = getSiteName(testName);
        String siteName1 = getSiteName(testName) + "-1" + System.currentTimeMillis();
        String siteName2 = getSiteName(testName) + "-2" + System.currentTimeMillis();

        String fileName1 = getFileName(testName) + "-1" + System.currentTimeMillis();
        String fileName2 = getFileName(testName) + "-2" + System.currentTimeMillis();

        String folderName1 = getFolderName(testName) + "-3" + System.currentTimeMillis();
        String folderName2 = getFolderName(testName) + "-4" + System.currentTimeMillis();

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Sites
        SiteUtil.createSite(drone, siteName1, siteName1, SITE_VISIBILITY_PUBLIC, true);
        SiteUtil.createSite(drone, siteName2, siteName2, SITE_VISIBILITY_PUBLIC, true);

        // Delete Sites
        SiteUtil.deleteSite(drone, siteName1);
        SiteUtil.deleteSite(drone, siteName2);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB });
        ShareUser.uploadFileInFolder(drone, new String[] { fileName2, DOCLIB });

        ShareUserSitePage.createFolder(drone, folderName1, folderName1);
        ShareUserSitePage.createFolder(drone, folderName2, folderName2);

        // Select Files and Folders created above
        ShareUser.selectContentCheckBox(drone, fileName1);
        ShareUser.selectContentCheckBox(drone, fileName2);

        ShareUser.selectContentCheckBox(drone, folderName1);
        DocumentLibraryPage docLibPage = ShareUser.selectContentCheckBox(drone, folderName2);

        ShareUser.deleteSelectedContent(drone);

        ShareUserProfile.navigateToTrashCan(drone);

        // Recover site, file and folder
        ShareUserProfile.recoverTrashCanItem(drone, siteName1.toLowerCase());

        ShareUserProfile.recoverTrashCanItem(drone, fileName1);

        ShareUserProfile.recoverTrashCanItem(drone, folderName1);

        Assert.assertTrue(ShareUser.openSiteDashboard(drone, siteName1).isSite(siteName1));

        // Confirm right files are recovered
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        Assert.assertTrue(docLibPage.isFileVisible(fileName1));
        Assert.assertFalse(docLibPage.isFileVisible(fileName2));

        // Confirm right folders are recovered
        Assert.assertTrue(docLibPage.isFileVisible(folderName1));
        Assert.assertFalse(docLibPage.isFileVisible(folderName2));

        // Confirm certain files and right folders are still in trashcan as expected
        ShareUserProfile.navigateToTrashCan(drone);

        // Additional Step to check that the items not recovered are present in trashcan
        TrashCanItem trashCanItem = ShareUserProfile.getTrashCanItem(drone, siteName2);
        Assert.assertNotNull(trashCanItem);

        trashCanItem = ShareUserProfile.getTrashCanItem(drone, fileName2);
        Assert.assertNotNull(trashCanItem);

        trashCanItem = ShareUserProfile.getTrashCanItem(drone, folderName2);
        Assert.assertNotNull(trashCanItem);
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_TrashCan_AONE_14177() throws Exception
    {
        String testName = getTestName();
        String trashcanUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { trashcanUser });

        ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        ShareUser.logout(drone);
    }

    /**
     * AONE_14177
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_14177() throws Exception
    {
        // dataPrep_TrashCan_AONE_14177(drone);
        String testName = getTestName();

        String testUser = getUserNameFreeDomain(testName);

        String siteName = getSiteName(testName);
        String siteName1 = getSiteName(testName) + System.currentTimeMillis() + "-1";
        String siteName2 = getSiteName(testName) + System.currentTimeMillis() + "-2";

        String fileName1 = getFileName(testName) + "-1" + System.currentTimeMillis();
        String fileName2 = getFileName(testName) + "-2" + System.currentTimeMillis();
        String fileName3 = getFileName(testName) + "-5" + System.currentTimeMillis();

        String folderName1 = getFolderName(testName) + "-3" + System.currentTimeMillis();
        String folderName2 = getFolderName(testName) + "-4" + System.currentTimeMillis();
        String folderName3 = getFolderName(testName) + "-6" + System.currentTimeMillis();

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, siteName2, SITE_VISIBILITY_PUBLIC);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB });
        ShareUser.uploadFileInFolder(drone, new String[] { fileName2, DOCLIB });
        ShareUser.uploadFileInFolder(drone, new String[] { fileName3, DOCLIB });

        ShareUserSitePage.createFolder(drone, folderName1, folderName1);
        ShareUserSitePage.createFolder(drone, folderName2, folderName2);

        DocumentLibraryPage docLibPage = ShareUserSitePage.createFolder(drone, folderName3, folderName3);

        // Select Content
        ShareUser.selectContentCheckBox(drone, fileName1);
        ShareUser.selectContentCheckBox(drone, fileName2);
        ShareUser.selectContentCheckBox(drone, fileName3);
        ShareUser.selectContentCheckBox(drone, folderName1);
        ShareUser.selectContentCheckBox(drone, folderName2);
        ShareUser.selectContentCheckBox(drone, folderName3);

        ShareUser.deleteSelectedContent(drone);
        SiteUtil.deleteSite(drone, siteName1);
        SiteUtil.deleteSite(drone, siteName2);

        ShareUserProfile.navigateToTrashCan(drone);

        ShareUserProfile.deleteTrashCanItem(drone, fileName1);

        ShareUserProfile.deleteTrashCanItem(drone, fileName2);

        ShareUserProfile.deleteTrashCanItem(drone, folderName1);

        ShareUserProfile.deleteTrashCanItem(drone, folderName2);

        ShareUserProfile.deleteTrashCanItem(drone, siteName1);

        ShareUserProfile.deleteTrashCanItem(drone, siteName2);

        Assert.assertFalse(SiteUtil.getSiteFinder(drone).getSiteList().contains(ShareUser.getSiteShortname(siteName1)));
        Assert.assertFalse(SiteUtil.getSiteFinder(drone).getSiteList().contains(ShareUser.getSiteShortname(siteName2)));

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        Assert.assertFalse(docLibPage.isFileVisible(fileName1));
        Assert.assertFalse(docLibPage.isFileVisible(fileName2));

        Assert.assertFalse(docLibPage.isFileVisible(fileName3));

        Assert.assertFalse(docLibPage.isFileVisible(folderName1));
        Assert.assertFalse(docLibPage.isFileVisible(folderName2));

        Assert.assertFalse(docLibPage.isFileVisible(folderName3));

        ShareUserProfile.navigateToTrashCan(drone);

        TrashCanItem trashCanItem = ShareUserProfile.getTrashCanItem(drone, fileName3);
        Assert.assertTrue(trashCanItem.getFileName().contains(fileName3));

        trashCanItem = ShareUserProfile.getTrashCanItem(drone, folderName3);
        Assert.assertTrue(trashCanItem.getFileName().contains(folderName3));

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_TrashCan_AONE_14178() throws Exception
    {

        String testName = getTestName();
        String trashcanUser = getUserNameFreeDomain(testName);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { trashcanUser });

    }

    @Test(groups = { "AlfrescoOne" })
    public void AONE_14178() throws Exception
    {
        // dataPrep_TrashCan_AONE_14178(drone);
        String testName = getTestName();
        String trashcanUser = getUserNameFreeDomain(testName);

        String siteName1 = getSiteName(testName) + "-1" + System.currentTimeMillis();
        String siteName2 = getSiteName(testName) + "-2" + System.currentTimeMillis();

        String fileName1 = getFileName(testName) + "-3" + System.currentTimeMillis();
        String fileName2 = getFileName(testName) + "-4" + System.currentTimeMillis();

        String folderName1 = getFolderName(testName) + "-5" + System.currentTimeMillis();
        String folderName2 = getFolderName(testName) + "-6" + System.currentTimeMillis();

        ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, siteName2, SITE_VISIBILITY_PUBLIC);

        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB });
        ShareUser.uploadFileInFolder(drone, new String[] { fileName2, DOCLIB });

        ShareUserSitePage.createFolder(drone, folderName1, folderName1);
        ShareUserSitePage.createFolder(drone, folderName2, folderName2);

        // Select Files
        ShareUser.selectContentCheckBox(drone, fileName1);
        ShareUser.selectContentCheckBox(drone, fileName2);

        ShareUser.selectContentCheckBox(drone, folderName1);
        ShareUser.selectContentCheckBox(drone, folderName2);

        ShareUser.deleteSelectedContent(drone);

        SiteUtil.deleteSite(drone, siteName1);
        SiteUtil.deleteSite(drone, siteName2);

        TrashCanPage trashCanPage = ShareUserProfile.navigateToTrashCan(drone);

        ShareUserProfile.emptyTrashCan(drone, SyncInfoPage.ButtonType.CANCEL);

        Assert.assertTrue(ShareUserProfile.isTrashCanItemPresent(drone, siteName1));
        Assert.assertTrue(ShareUserProfile.isTrashCanItemPresent(drone, folderName1));
        Assert.assertTrue(ShareUserProfile.isTrashCanItemPresent(drone, fileName1));

        ShareUserProfile.emptyTrashCan(drone, SyncInfoPage.ButtonType.REMOVE);

        Assert.assertFalse(trashCanPage.hasTrashCanItems());

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_TrashCan_AONE_14179() throws Exception
    {
        String testName = getTestName();
        String siteName1 = getSiteName(testName) + "-s1";

        String trashcanUser1 = getUserNameFreeDomain(testName + "1");
        String trashcanUser2 = getUserNameFreeDomain(testName + "2");

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { trashcanUser1 });
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { trashcanUser2 });

        ShareUser.login(drone, trashcanUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

    }

    @Test(groups = { "AlfrescoOne" })
    public void AONE_14179() throws Exception
    {
        // dataPrep_TrashCan_AONE_14179(drone);
        String testName = getTestName();
        String trashcanUser1 = getUserNameFreeDomain(testName + "1");
        String trashcanUser2 = getUserNameFreeDomain(testName + "2");

        String siteName1 = getSiteName(testName) + "-s1";

        String fileName1 = getFileName(testName) + "-fi1" + System.currentTimeMillis();

        String folderName1 = getFolderName(testName) + "-fo2" + System.currentTimeMillis();

        ShareUser.login(drone, trashcanUser1, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB });

        ShareUserSitePage.createFolder(drone, folderName1, folderName1);

        // Select File and Folder
        ShareUser.selectContentCheckBox(drone, fileName1);
        ShareUser.selectContentCheckBox(drone, folderName1);

        ShareUser.deleteSelectedContent(drone);

        SiteUtil.deleteSite(drone, siteName1);

        ShareUser.logout(drone);

        ShareUser.login(drone, trashcanUser2, DEFAULT_PASSWORD);

        TrashCanPage trashCanPage = ShareUserProfile.navigateToTrashCan(drone);

        Assert.assertFalse(trashCanPage.hasTrashCanItems());

        Assert.assertFalse(ShareUserProfile.getTrashCanItems(drone, siteName1).contains(siteName1));

        Assert.assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, fileName1));

        Assert.assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, folderName1));

        ShareUser.logout(drone);

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_TrashCan_AONE_14180() throws Exception
    {
        String testName = getTestName();

        String siteName1 = getSiteName(testName);

        String testUser = getUserNameFreeDomain(testName + "1");

        String fileName1 = "f1-" + getFileName(testName) + ".txt";
        String fileName2 = "f2-" + getFileName(testName) + ".txt";

        String folderName1 = "f3-" + getFolderName(testName);
        String folderName2 = "f4-" + getFolderName(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser });

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);

        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB });
        ShareUser.uploadFileInFolder(drone, new String[] { fileName2, DOCLIB });

        ShareUserSitePage.createFolder(drone, folderName1, folderName1);
        ShareUserSitePage.createFolder(drone, folderName2, folderName2);

        ShareUser.selectContentCheckBox(drone, fileName1);
        ShareUser.selectContentCheckBox(drone, fileName2);
        ShareUser.selectContentCheckBox(drone, folderName1);
        ShareUser.selectContentCheckBox(drone, folderName2);

        ShareUser.deleteSelectedContent(drone);

        ShareUser.logout(drone);
    }

    @Test(groups = { "AlfrescoOne" })
    public void AONE_14180() throws Exception
    {
        String testName = getTestName();

        String testUser = getUserNameFreeDomain(testName + "1");

        String fileName1 = "f1-" + getFileName(testName) + ".txt";
        String fileName2 = "f2-" + getFileName(testName) + ".txt";

        String folderName1 = "f3-" + getFolderName(testName);
        String folderName2 = "f4-" + getFolderName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        TrashCanPage trashCanPage = ShareUserProfile.navigateToTrashCan(drone);

        // Search for file includes file in the results
        Assert.assertTrue(ShareUserProfile.getTrashCanItems(drone, fileName1).contains(fileName1));

        Assert.assertTrue(ShareUserProfile.getTrashCanItems(drone, fileName2).contains(fileName2));
        Assert.assertTrue(trashCanPage.getTrashCanItemForContent(TrashCanValues.FILE, fileName2, "documentLibrary").size() > 0);

        // Search for folder includes folder in the results
        Assert.assertTrue(ShareUserProfile.getTrashCanItems(drone, folderName1).contains(folderName1));

        Assert.assertTrue(ShareUserProfile.getTrashCanItems(drone, folderName2).contains(folderName2));
        Assert.assertTrue(trashCanPage.getTrashCanItemForContent(TrashCanValues.FOLDER, folderName2, "documentLibrary").size() > 0);

        ShareUser.logout(drone);

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_TrashCan_AONE_14181() throws Exception
    {

        String testName = getTestName();
        String siteName1 = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName + "1");

        String fileName1 = "fi1-" + getFileName(testName);
        String fileName2 = "fi2-" + getFileName(testName);

        String folderName1 = "fo3-" + getFolderName(testName);
        String folderName2 = "fo4-" + getFolderName(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser });

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);

        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB });
        ShareUser.uploadFileInFolder(drone, new String[] { fileName2, DOCLIB });

        ShareUserSitePage.createFolder(drone, folderName1, folderName1);
        ShareUserSitePage.createFolder(drone, folderName2, folderName2);

        ShareUser.selectContentCheckBox(drone, fileName1);
        ShareUser.selectContentCheckBox(drone, fileName2);
        ShareUser.selectContentCheckBox(drone, folderName1);
        ShareUser.selectContentCheckBox(drone, folderName2);

        ShareUser.deleteSelectedContent(drone);

        ShareUser.logout(drone);
    }

    @Test(groups = { "AlfrescoOne" })
    public void AONE_14181() throws Exception
    {
        // dataPrep_TrashCan_AONE_14181(drone);
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName + "1");

        String fileName1 = "fi1-" + getFileName(testName);
        String fileName2 = "fi2-" + getFileName(testName);

        String folderName1 = "fo3-" + getFolderName(testName);
        String folderName2 = "fo4-" + getFolderName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUserProfile.navigateToTrashCan(drone);

        // Empty String search
        List<String> nameOfItems = ShareUserProfile.getTrashCanItems(drone, "");

        Assert.assertTrue(nameOfItems.contains(fileName1));
        Assert.assertTrue(nameOfItems.contains(fileName2));
        Assert.assertTrue(nameOfItems.contains(folderName1));
        Assert.assertTrue(nameOfItems.contains(folderName2));

        // Search: ends with common term: *testName
        nameOfItems = ShareUserProfile.getTrashCanItems(drone, "*" + testName);

        Assert.assertTrue(nameOfItems.contains(fileName1));
        Assert.assertTrue(nameOfItems.contains(fileName2));
        Assert.assertTrue(nameOfItems.contains(folderName1));
        Assert.assertTrue(nameOfItems.contains(folderName2));

        // Search: starts with common term: fo*
        nameOfItems = ShareUserProfile.getTrashCanItems(drone, "fo*");
        Assert.assertFalse(nameOfItems.contains(fileName1));
        Assert.assertFalse(nameOfItems.contains(fileName2));
        Assert.assertTrue(nameOfItems.contains(folderName1));
        Assert.assertTrue(nameOfItems.contains(folderName2));

        // Search: starts with common term: fi*
        nameOfItems = ShareUserProfile.getTrashCanItems(drone, "fi*");
        Assert.assertTrue(nameOfItems.contains(fileName1));
        Assert.assertTrue(nameOfItems.contains(fileName2));
        Assert.assertFalse(nameOfItems.contains(folderName1));
        Assert.assertFalse(nameOfItems.contains(folderName2));

        nameOfItems = ShareUserProfile.getTrashCanItems(drone, fileName1 + "*");
        Assert.assertTrue(nameOfItems.contains(fileName1));
        Assert.assertFalse(nameOfItems.contains(fileName2));
        Assert.assertFalse(nameOfItems.contains(folderName1));
        Assert.assertFalse(nameOfItems.contains(folderName2));

        ShareUser.logout(drone);

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_TrashCan_AONE_14182() throws Exception
    {

        String testName = getTestName();
        String siteName1 = getSiteName(testName);

        String testUser = getUserNameFreeDomain(testName + "1");

        String fileName1 = "fi1-" + getFileName(testName);
        String fileName2 = "fi2-" + getFileName(testName);

        String folderName1 = "fo3-" + getFolderName(testName);
        String folderName2 = "fo4-" + getFolderName(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser });
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName1);
        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB });
        ShareUser.uploadFileInFolder(drone, new String[] { fileName2, DOCLIB });
        ShareUserSitePage.createFolder(drone, folderName1, folderName1);
        ShareUserSitePage.createFolder(drone, folderName2, folderName2);

        ShareUser.selectContentCheckBox(drone, fileName1);
        ShareUser.selectContentCheckBox(drone, fileName2);
        ShareUser.selectContentCheckBox(drone, folderName1);
        ShareUser.selectContentCheckBox(drone, folderName2);

        ShareUser.deleteSelectedContent(drone);

        ShareUser.logout(drone);
    }

    @Test(groups = { "AlfrescoOne" })
    public void AONE_14182() throws Exception
    {

        String testName = getTestName();
        String trashcanUser1 = getUserNameFreeDomain(testName + "1");
        String fileName1 = "fi1-" + getFileName(testName);
        String fileName2 = "fi2-" + getFileName(testName);
        String folderName1 = "fo3-" + getFolderName(testName);
        String folderName2 = "fo4-" + getFolderName(testName);

        ShareUser.login(drone, trashcanUser1, DEFAULT_PASSWORD);

        ShareUserProfile.navigateToTrashCan(drone);

        List<String> nameOfItems = ShareUserProfile.getTrashCanItems(drone, "");

        Assert.assertTrue(nameOfItems.contains(fileName1));
        Assert.assertTrue(nameOfItems.contains(fileName2));
        Assert.assertTrue(nameOfItems.contains(folderName1));
        Assert.assertTrue(nameOfItems.contains(folderName2));

        ShareUser.logout(drone);

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_TrashCan_AONE_14184() throws Exception
    {

        String testName = getTestName();
        String siteName1 = getSiteName(testName);
        String trashcanUser1 = getUserNameFreeDomain(testName + "1");
        String trashcanUser2 = getUserNameFreeDomain(testName + "2");

        String fileName1 = getFileName(testName);
        String folderName1 = getFolderName(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { trashcanUser1 });
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { trashcanUser2 });

        ShareUser.login(drone, trashcanUser1, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);

        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB });

        ShareUserSitePage.createFolder(drone, folderName1, folderName1);

        ShareUser.selectContentCheckBox(drone, fileName1);

        ShareUser.selectContentCheckBox(drone, folderName1);

        ShareUser.deleteSelectedContent(drone);

        ShareUser.logout(drone);
    }

    @Test(groups = { "AlfrescoOne" })
    public void AONE_14184() throws Exception
    {
        String testName = getTestName();

        String trashcanUser1 = getUserNameFreeDomain(testName + "1");
        String trashcanUser2 = getUserNameFreeDomain(testName + "2");

        String fileName1 = getFileName(testName);

        String folderName1 = getFolderName(testName);

        ShareUser.login(drone, trashcanUser1, DEFAULT_PASSWORD);

        ShareUserProfile.navigateToTrashCan(drone);

        String url = getShareUrl();
        if (alfrescoVersion.isCloud())
        {
            url = url + "/" + getUserDomain(trashcanUser2);
        }

        drone.navigateTo(url + "/page/user/" + trashcanUser2 + "/user-trashcan");

        ((TrashCanPage) drone.getCurrentPage()).render();
        List<String> nameOfItems = ShareUserProfile.getTrashCanItems(drone, "");
        Assert.assertTrue(nameOfItems.contains(fileName1));

        Assert.assertTrue(nameOfItems.contains(folderName1));

        Assert.assertTrue(drone.getCurrentUrl().contains(trashcanUser2));
        Assert.assertFalse(drone.getCurrentUrl().contains(trashcanUser1));

        ShareUser.logout(drone);
    }

    @Test(groups = { "AlfrescoOne" })
    public void AONE_14171() throws Exception
    {
        String testName = getTestName();
        String trashcanUser = getUserNameFreeDomain(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, trashcanUser);

        // Log in as created user
        SharePage sharePage = ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        //Navigate to My Profile
        MyProfilePage myProfilePage = sharePage.getNav().selectMyProfile().render();
        Assert.assertTrue(myProfilePage.isTrashcanLinkDisplayed(), "Trashcan isn't present");
    }

    @Test(groups = { "AlfrescoOne" })
    public void AONE_14172() throws Exception
    {
        String testName = getTestName();
        String trashcanUser = getUserNameFreeDomain(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, trashcanUser);

        // Log in as created user
        SharePage sharePage = ShareUser.login(drone, trashcanUser, DEFAULT_PASSWORD);

        // Navigate to My Profile
        // Click on Trashcan
        TrashCanPage trashCanPage = ShareUserProfile.navigateToTrashCan(drone).render();

        //Verify Trashcan page
        Assert.assertTrue(trashCanPage.isPageCorrect());

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
        for (int i = 0; i < 8; i++)
        {
            List<String> txtLines = new ArrayList<>();
            txtLines.add(0, testName + "wiki" + i);
            wikiPage.createWikiPage(testName + "wiki" + i, txtLines).render();
        }

        //Navigate to Wiki
        wikiPage = siteDashboardPage.getSiteNav().selectWikiPage().render();
        WikiPageList wikiPageList = wikiPage.clickWikiPageListBtn();

        // Deleting 8 Wiki pages
        for (int i = 0; i < 8; i++)
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
        int j = 1;
        for (int i = 0; i < wikis.length; i++)
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
        Assert.assertTrue(itemInfo.getDate().contains(dateOfContentDeletion),
            String.format("Date displayed: %s, Date Expected: %s", itemInfo.getDate(), dateOfContentDeletion));
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
        assertEquals(trashCanRecoverConfirmation.getNotificationMessage(), "Successfully recovered 2 item(s), 0 failed.");
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
        assertEquals(trashCanConfirmationDeleteDialog.getNotificationMessage(), "Successfully deleted 2 item(s), 0 failed.");
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
        assertTrue(trashCanPage.checkNoItemsMessage());

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
        for (int i = 0; i < posts.length; i++)
        {
            posts[i] = testName + "blog" + i;
        }

        // Log in as User1
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Open Blog page
        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName).render();
        BlogPage blogPage = siteDashboardPage.getSiteNav().selectBlogPage().render();

        // Creating 8 posts
        for (int i = 0; i < postIds.length; i++)
        {
            blogPage.createPostInternally(testName + "blog" + i, testName + "_" + i).render();
            postIds[i] = BlogUtil.getPostId(drone, siteName, testName + "blog" + i);
        }

        //Navigate to Blog page
        blogPage = siteDashboardPage.getSiteNav().selectBlogPage().render();

        // Deleting 8 posts
        for (int i = 0; i < 8; i++)
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
        Assert.assertTrue(itemInfo.getDate().contains(dateOfContentDeletion),
            String.format("Date displayed: %s, Date Expected: %s", itemInfo.getDate(), dateOfContentDeletion));
        Assert.assertTrue(itemInfo.getFolderPath().endsWith("blog"));
        Assert.assertTrue(itemInfo.getUserFullName().toLowerCase().startsWith(
            user1.toLowerCase()), String.format("Username displayed: %s, Username Expected: %s", itemInfo.getUserFullName(), user1));

        // Recover post0
        ShareUserProfile.recoverTrashCanItem(drone, postIds[0]);

        // Verify that the recovered item is not presented
        assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, postIds[0]), posts[0] + " is presented in Trashcan");

        // Delete post1
        ShareUserProfile.deleteTrashCanItem(drone, postIds[1]);

        // Verify that the deleted item is not presented
        assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, postIds[1]), posts[1] + "  is presented in Trashcan");

        // Recover two posts  (post2 and post3)
        TrashCanItem post2 = ShareUserProfile.getTrashCanItem(drone, postIds[2]);
        TrashCanItem post3 = ShareUserProfile.getTrashCanItem(drone, postIds[3]);
        post2.selectTrashCanItemCheckBox();
        post3.selectTrashCanItemCheckBox();
        TrashCanRecoverConfirmDialog trashCanRecoverConfirmation = trashCanPage.selectedRecover().render();
        assertEquals(trashCanRecoverConfirmation.getNotificationMessage(), "Successfully recovered 2 item(s), 0 failed.");
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
        assertEquals(trashCanRecoverConfirmation.getNotificationMessage(), "This will permanently delete the item(s). Are you sure?");
        trashCanConfirmationDeleteDialog.clickOkButton();
        assertEquals(trashCanConfirmationDeleteDialog.getNotificationMessage(), "Successfully deleted 2 item(s), 0 failed.");
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
        assertTrue(trashCanPage.checkNoItemsMessage());

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
        for (int i = 0; i < links.length; i++)
        {
            links[i] = testName + "link" + i;
        }

        // Log in as User1
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Open Link page
        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName).render();

        // Creating 8 links
        for (int i = 0; i < linkIds.length; i++)
        {
            LinksPage linksPage = siteDashboardPage.getSiteNav().selectLinksPage();
            linksPage.createLink(testName + "link" + i, linkUrl).render();
            linkIds[i] = LinkUtil.getLinkId(drone, siteName, testName + "link" + i);
        }

        //Navigate to Link page
        LinksPage linksPage = siteDashboardPage.getSiteNav().selectLinksPage().render();

        // Deleting 8 links
        for (int i = 0; i < 8; i++)
        {
            LinksDetailsPage linksDetailsPage = linksPage.clickLink(testName + "link" + i).render();
            linksPage = linksDetailsPage.deleteLink().render();
        }
        ShareUser.logout(drone);

        // User2 login
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);
        siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName);

        // Creating link
        linksPage = siteDashboardPage.getSiteNav().selectLinksPage().render();
        linksPage.createLink(testName + "link8", linkUrl).render();
        String linkId8 = LinkUtil.getLinkId(drone, siteName, testName + "link8");

        // Navigate to Link page
        linksPage = siteDashboardPage.getSiteNav().selectLinksPage().render();

        // Delete link
        LinksDetailsPage linksDetailsPage = linksPage.clickLink(testName + "link8").render();
        linksDetailsPage.deleteLink().render();
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
        Assert.assertTrue(itemInfo.getDate().contains(dateOfContentDeletion),
            String.format("Date displayed: %s, Date Expected: %s", itemInfo.getDate(), dateOfContentDeletion));
        Assert.assertTrue(itemInfo.getFolderPath().endsWith("links"));
        Assert.assertTrue(itemInfo.getUserFullName().toLowerCase().startsWith(
            user1.toLowerCase()), String.format("Username displayed: %s, Username Expected: %s", itemInfo.getUserFullName(), user1));

        // Recover link0
        ShareUserProfile.recoverTrashCanItem(drone, linkIds[0]);

        // Verify that the recovered item is not presented
        assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, linkIds[0]), links[0] + " is presented in Trashcan");

        // Delete link1
        ShareUserProfile.deleteTrashCanItem(drone, linkIds[1]);

        // Verify that the deleted item is not presented
        assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, linkIds[1]), links[1] + "  is presented in Trashcan");

        // Recover two links (link2 and link3)
        TrashCanItem link2 = ShareUserProfile.getTrashCanItem(drone, linkIds[2]);
        TrashCanItem link3 = ShareUserProfile.getTrashCanItem(drone, linkIds[3]);
        link2.selectTrashCanItemCheckBox();
        link3.selectTrashCanItemCheckBox();
        TrashCanRecoverConfirmDialog trashCanRecoverConfirmation = trashCanPage.selectedRecover().render();
        assertEquals(trashCanRecoverConfirmation.getNotificationMessage(), "Successfully recovered 2 item(s), 0 failed.");
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
        assertEquals(trashCanConfirmationDeleteDialog.getNotificationMessage(), "This will permanently delete the item(s). Are you sure?");
        trashCanConfirmationDeleteDialog.clickOkButton();
        assertEquals(trashCanConfirmationDeleteDialog.getNotificationMessage(), "Successfully deleted 2 item(s), 0 failed.");
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
        assertTrue(trashCanPage.checkNoItemsMessage());

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
        for (int i = 0; i < lists.length; i++)
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
        for (int i = 0; i < listIds.length; i++)
        {
            dataListPage.createDataList(CONTACT_LIST, testName + "dataList" + i, testName);
            listIds[i] = DataListUtil.getListId(drone, siteName, testName + "dataList" + i);
        }

        //Navigate to Data Lists page
        dataListPage = siteDashboardPage.getSiteNav().selectDataListPage().render();

        // Deleting 8 data lists
        for (int i = 0; i < 8; i++)
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
        Assert.assertTrue(itemInfo.getDate().contains(dateOfContentDeletion),
            String.format("Date displayed: %s, Date Expected: %s", itemInfo.getDate(), dateOfContentDeletion));
        Assert.assertTrue(itemInfo.getFolderPath().endsWith("dataLists"));
        Assert.assertTrue(itemInfo.getUserFullName().toLowerCase().startsWith(
            user1.toLowerCase()), String.format("Username displayed: %s, Username Expected: %s", itemInfo.getUserFullName(), user1));

        // Recover data list0
        ShareUserProfile.recoverTrashCanItem(drone, listIds[0]);

        // Verify that the recovered item is not presented
        assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, listIds[0]), lists[0] + " is presented in Trashcan");

        // Delete data list1
        ShareUserProfile.deleteTrashCanItem(drone, listIds[1]);

        // Verify that the deleted item is not presented
        assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, listIds[1]), lists[1] + "  is presented in Trashcan");

        // Recover two data lists (data list2 and data list3)
        TrashCanItem list2 = ShareUserProfile.getTrashCanItem(drone, listIds[2]);
        TrashCanItem list3 = ShareUserProfile.getTrashCanItem(drone, listIds[3]);
        list2.selectTrashCanItemCheckBox();
        list3.selectTrashCanItemCheckBox();
        TrashCanRecoverConfirmDialog trashCanRecoverConfirmation = trashCanPage.selectedRecover().render();
        assertEquals(trashCanRecoverConfirmation.getNotificationMessage(), "Successfully recovered 2 item(s), 0 failed.");
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
        assertEquals(trashCanConfirmationDeleteDialog.getNotificationMessage(), "This will permanently delete the item(s). Are you sure?");
        trashCanConfirmationDeleteDialog.clickOkButton();
        assertEquals(trashCanConfirmationDeleteDialog.getNotificationMessage(), "Successfully deleted 2 item(s), 0 failed.");
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
        assertTrue(trashCanPage.checkNoItemsMessage());

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
