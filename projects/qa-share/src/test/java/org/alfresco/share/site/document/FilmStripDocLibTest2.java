package org.alfresco.share.site.document;

import org.alfresco.po.share.enums.ViewType;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

import static org.testng.Assert.*;

/**
 * Created by olga.lokhach
 */

@Listeners(FailedTestListener.class)
public class FilmStripDocLibTest2 extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(FilmStripDocLibTest2.class);

    protected String testUser;
    protected String siteName = "";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("Starting Tests: " + testName);
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14052() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

    }

    //AONE-14052:Check renaming folder from Info panel

    @Test(groups = "AlfrescoOne")
    public void AONE_14052() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName+ "-")+System.currentTimeMillis();
        String folderName = getFolderName(testName+ "-") + System.currentTimeMillis();
        String newFolderName = folderName + "-Updated";

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Create folder
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB_CONTAINER);

        // Document Library page is opened in Filmstrip view
        DocumentLibraryPage docLibPage = ShareUserSitePage.selectView(drone, ViewType.FILMSTRIP_VIEW);
        assertTrue(docLibPage.getFilmstripActions().isFilmStripViewDisplayed());

        // Info Panel for the document is displayed
        FileDirectoryInfo folderInfo = docLibPage.getFileDirectoryInfo(folderName);
        folderInfo.clickInfoIcon();
        assertTrue(folderInfo.isInfoPopUpDisplayed(), "Info panel isn't shown");

        // - Mouse-over on the document's name;
        // Click the "Edit" icon;
        folderInfo.contentNameEnableEdit();

        // Type any new name of the document
        folderInfo.contentNameEnter(newFolderName);
        assertTrue(folderInfo.isSaveLinkVisible());
        assertTrue(folderInfo.isCancelLinkVisible());

        // and click "Cancel" link;
        folderInfo.contentNameClickCancel();
        assertFalse(folderInfo.isSaveLinkVisible());
        assertFalse(folderInfo.isCancelLinkVisible());
        docLibPage = drone.getCurrentPage().render();
        assertTrue(docLibPage.isFileVisible(folderName), folderName + " folder is changed");

        // - Mouse-over on the document's name, click 'Edit" icon,
        // type any new name and click "Save" button;
        docLibPage = ShareUserSitePage.selectView(drone, ViewType.FILMSTRIP_VIEW);
        folderInfo = docLibPage.getFileDirectoryInfo(folderName);
        folderInfo.clickInfoIcon();
        folderInfo.renameContent(newFolderName);

        docLibPage = drone.getCurrentPage().render();
        assertTrue(docLibPage.isFileVisible(newFolderName), newFolderName + " folder isn't displayed");
        ShareUserSitePage.selectView(drone, ViewType.DETAILED_VIEW);

    }

    //AONE-14054:Check adding tags to the document from Info panel

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14054() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14054() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + "-")+System.currentTimeMillis();
        String fileName = getFileName(testName);
        File file = newFile(fileName, fileName);
        String tagName0 = "tag1";
        String tagName1 = "tag2";
        String tagName2 = "tag3";

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Create content
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUserSitePage.uploadFile(drone, file);
        ShareUserSitePage.getFileDirectoryInfo(drone, fileName);

        //Add two tags
        ShareUserSitePage.addTag(drone,fileName,tagName0);
        ShareUserSitePage.addTag(drone,fileName,tagName1);
        assertEquals(ShareUserSitePage.getFileDirectoryInfo(drone, fileName).getTags().size(), 2, "Tag added above isn't displayed");

        // Expand the "Options" menu and click the "Filmstrip View" button;
        DocumentLibraryPage docLibPage = ShareUserSitePage.selectView(drone, ViewType.FILMSTRIP_VIEW);

        // The view is changed to Filmstrip mode;
        assertTrue(docLibPage.getFilmstripActions().isFilmStripViewDisplayed());

        // Info Panel for the document is displayed
        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, fileName);
        fileInfo.clickInfoIcon();
        assertTrue(fileInfo.isInfoPopUpDisplayed(), "Info panel isn't shown");

        // Click the "Edit" icon.
        fileInfo.clickOnAddTag();
        assertTrue(fileInfo.isSaveLinkVisible());
        assertTrue(fileInfo.isCancelLinkVisible());
        assertTrue(fileInfo.removeTagButtonIsDisplayed(tagName0));

        // Click "x" icon for the Tag1;
        fileInfo.clickOnTagRemoveButton(tagName0);
        List<String> inTags = fileInfo.getInlineTagsList();
        assertFalse(inTags.contains(tagName0), "The tags list contains " + tagName0);

        // Type any new tag and press Enter;
        fileInfo.enterTagString(tagName2);
        inTags = fileInfo.getInlineTagsList();
        assertTrue(inTags.contains(tagName2), "The tags list is not contains " + tagName2);

        // Click "Cancel" button;
        fileInfo.clickOnTagCancelButton();
        assertFalse(fileInfo.isSaveLinkVisible());
        assertFalse(fileInfo.isCancelLinkVisible());
        assertFalse(fileInfo.removeTagButtonIsDisplayed(tagName0));

        List<String> tags = fileInfo.getTags();
        assertFalse(tags.contains(tagName2), "The tags list contains " + tagName2);
        assertTrue(tags.contains(tagName0), "The tags list is not contains " + tagName0);
        assertTrue(tags.contains(tagName1), "The tags list is not contains " + tagName1);

        // Click "Edit" icon again, remove Tag1, add Tag3 press Enter and click "Save" button;
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, fileName);
        fileInfo.clickOnAddTag();
        fileInfo.clickOnTagRemoveButton(tagName0);
        fileInfo.enterTagString(tagName2);
        fileInfo.clickOnTagSaveButton();
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, fileName);
        tags = fileInfo.getTags();
        assertFalse(tags.contains(tagName0), "The tags list contains " + tagName0);
        assertTrue(tags.contains(tagName2), "The tags list is not contains " + tagName2);
        assertTrue(tags.contains(tagName1), "The tags list is not contains " + tagName1);
        ShareUserSitePage.selectView(drone, ViewType.DETAILED_VIEW);


    }

    //AONE-14060:Switch between the 7 view modes

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14060() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);


    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14060() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName+ "-")+System.currentTimeMillis();
        String fileName1 = getFileName(testName+ "-1");
        String fileName2 = getFileName(testName+ "-2");
        File file1 = newFile(fileName1, fileName1);
        File file2 = newFile(fileName2, fileName2);

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Create content
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUserSitePage.uploadFile(drone, file1);
        ShareUserSitePage.uploadFile(drone, file2);

        // Expand the "Options" menu and click the "Filmstrip View" button;
        DocumentLibraryPage docLibPage = ShareUserSitePage.selectView(drone, ViewType.FILMSTRIP_VIEW);

        // The view is changed to Filmstrip mode;
        assertTrue(docLibPage.getFilmstripActions().isFilmStripViewDisplayed());
        docLibPage = drone.getCurrentPage().render();
        assertTrue(docLibPage.isItemVisble(fileName1), "The content " + fileName1 + " isn't displayed");
        assertTrue(docLibPage.isItemVisble(fileName2), "The content " + fileName2 + " isn't displayed");

        // The view is changed to simple view;
        docLibPage = docLibPage.getNavigation().selectSimpleView().render();
        assertEquals(docLibPage.getViewType(), ViewType.SIMPLE_VIEW);
        docLibPage = drone.getCurrentPage().render();
        assertTrue(docLibPage.isItemVisble(fileName1), "The content " + fileName1 + " isn't displayed");
        assertTrue(docLibPage.isItemVisble(fileName2), "The content " + fileName2 + " isn't displayed");

        // The view is changed to gallery view;
        docLibPage = docLibPage.getNavigation().selectGalleryView().render();
        assertEquals(docLibPage.getViewType(), ViewType.GALLERY_VIEW);
        docLibPage = drone.getCurrentPage().render();
        assertTrue(docLibPage.isItemVisble(fileName1), "The content " + fileName1 + " isn't displayed");
        assertTrue(docLibPage.isItemVisble(fileName2), "The content " + fileName2 + " isn't displayed");

        // The view is changed to table view;
        docLibPage = docLibPage.getNavigation().selectTableView().render();
        assertEquals(docLibPage.getViewType(), ViewType.TABLE_VIEW);
        docLibPage = drone.getCurrentPage().render();
        assertTrue(docLibPage.isItemVisble(fileName1), "The content " + fileName1 + " isn't displayed");
        assertTrue(docLibPage.isItemVisble(fileName2), "The content " + fileName2 + " isn't displayed");

        // The view is changed to detailed view;
        docLibPage = docLibPage.getNavigation().selectDetailedView().render();
        assertEquals(docLibPage.getViewType(), ViewType.DETAILED_VIEW);
        docLibPage = drone.getCurrentPage().render();
        assertTrue(docLibPage.isItemVisble(fileName1), "The content " + fileName1 + " isn't displayed");
        assertTrue(docLibPage.isItemVisble(fileName2), "The content " + fileName2 + " isn't displayed");

        // The view is changed to audio view;
        docLibPage = docLibPage.getNavigation().selectAudioView().render();
        assertEquals(docLibPage.getViewType(), ViewType.AUDIO_VIEW);
        docLibPage = drone.getCurrentPage().render();
        assertTrue(docLibPage.isFilesVisible(),"The content isn't displayed");

        // The view is changed to media view;
        docLibPage = docLibPage.getNavigation().selectMediaView().render();
        assertEquals(docLibPage.getViewType(), ViewType.MEDIA_VIEW);
        docLibPage = drone.getCurrentPage().render();
        assertTrue(docLibPage.isFilesVisible(),"The content isn't displayed");
        ShareUserSitePage.selectView(drone, ViewType.DETAILED_VIEW);
    }


}
