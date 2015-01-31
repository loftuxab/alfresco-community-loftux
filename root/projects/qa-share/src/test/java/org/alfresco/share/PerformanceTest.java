/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.share;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.search.FacetedSearchPage;
import org.alfresco.po.share.site.document.*;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.rest.api.tests.client.PublicApiClient;
import org.alfresco.share.util.FtpUtil;
import org.alfresco.share.util.RandomUtil;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CmisUtils;
import org.alfresco.share.util.httpCore.CoreHelper;
import org.alfresco.share.util.httpCore.HttpCore;
import org.alfresco.share.util.httpCore.Response;
import org.alfresco.webdrone.WebDroneImpl;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.alfresco.po.share.site.SitePageType.DOCUMENT_LIBRARY;
import static org.alfresco.po.share.site.document.ContentType.PLAINTEXT;
import static org.alfresco.po.share.site.document.DocumentAspect.CLASSIFIABLE;
import static org.alfresco.share.enums.CMISBinding.ATOMPUB11;
import static org.alfresco.share.util.ShareUser.*;
import static org.alfresco.share.util.SiteUtil.createSite;
import static org.alfresco.share.util.WebDroneType.DownLoadDrone;
import static org.alfresco.share.util.api.CreateUserAPI.CreateActivateUser;
import static org.apache.chemistry.opencmis.commons.enums.VersioningState.MAJOR;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.testng.Assert.*;

/**
 * @author Aliaksei Boole
 */
public class PerformanceTest extends CmisUtils
{
    private static final Logger logger = Logger.getLogger(PerformanceTest.class);
    private static final String DOMAIN = "-default-";
    private static final String BIG_DATA_FILE = "DemoRandomAccessFile.out";
    private static final String LONG_FILE_NAME = "Directors report for year ended";
    private static final HttpCore HTTP_CORE = new HttpCore();
    private static final CoreHelper CORE_HELPER = new CoreHelper();

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.beforeClass();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
    }

    @Override
    public void beforeClass()
    {
       // To change the behavior of initialization.
    }

    @Test(groups = { "DataPrepPerformance", "Share", "NonGrid", "EnterpriseOnly" })
    public void dataPrep_AONE_11809() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName1 = getSiteName(testName + "1");
        String folderName1 = getFolderName(testName + "1");
        String siteName2 = getSiteName(testName + "2");
        String folderName2 = getFolderName(testName + "2");

        CreateActivateUser(drone, ADMIN_USERNAME, testUser);
        login(drone, testUser, DEFAULT_PASSWORD);

        createSite(drone, siteName1, siteName1);
        openSitesDocumentLibrary(drone, siteName1);
        ShareUserSitePage.createFolder(drone, folderName1, folderName1);

        createSite(drone, siteName2, siteName2);
        openSitesDocumentLibrary(drone, siteName2);
        ShareUserSitePage.createFolder(drone, folderName2, folderName2);

        PublicApiClient.CmisSession cmisSession = getCmisSession(ATOMPUB11, testUser, DOMAIN);
        Folder rootFolder = (Folder) cmisSession.getObjectByPath("/Sites/" + siteName1 + "/documentLibrary/" + folderName1);

        for (int i = 0; i < 7500; i++)
        {
            String newFolder = RandomUtil.getRandomString(7);
            String documentName = RandomUtil.getRandomString(7);
            Folder folder = createFolderInFolder(rootFolder, newFolder);
            createDocumentInFolder(folder, documentName);
            folder = createFolderInFolder(folder, newFolder + "_deep");
            createDocumentInFolder(folder, documentName + "_deep");
        }
    }

    @Test(groups = { "Performance", "Share", "NonGrid", "EnterpriseOnly" })
    public void AONE_11809() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);

        String siteName1 = getSiteName(testName + "1");
        String folderName1 = getFolderName(testName + "1");
        String siteName2 = getSiteName(testName + "2");
        String folderName2 = getFolderName(testName + "2");

        login(drone, testUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = openSitesDocumentLibrary(drone, siteName1);
        FileDirectoryInfo fileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(folderName1);
        CopyOrMoveContentPage copyOrMoveContentPage = fileDirectoryInfo.selectMoveTo();
        copyOrMoveContentPage.selectSite(siteName2).selectPath(folderName2).selectOkButton();
        webDriverWait(drone, 30000);
        documentLibraryPage = openSitesDocumentLibrary(drone, siteName2);
        documentLibraryPage = documentLibraryPage.selectFolder(folderName2).render();
        documentLibraryPage = documentLibraryPage.selectFolder(folderName1).render();
        PaginationForm paginationForm = documentLibraryPage.getBottomPaginationForm();
        assertTrue(paginationForm.isDisplay(), "Pagination for moved folder don't displayed.");
        assertEquals(paginationForm.getPaginationInfo(), "Showing items 1 - 50 of 1000++", "Wrong information on pagination.");
        assertEquals(documentLibraryPage.getFiles().size(), 50, "Wrong folders count show on page.");
    }

    @Test(groups = { "DataPrepPerformance", "Share", "NonGrid", "EnterpriseOnly" })
    public void dataPrep_AONE_11810() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        File bigDataFile = null;

        try
        {
            FtpUtil.configFtpPort();
            bigDataFile = getFileWithSize(BIG_DATA_FILE, 2047);
            logger.info("2 GB file created! File[" + bigDataFile.getAbsolutePath() + "]=" + ((float) bigDataFile.length() / 1024 / 1024 / 1024) + " gb.");

            CreateActivateUser(drone, ADMIN_USERNAME, testUser);
            login(drone, testUser, DEFAULT_PASSWORD);
            createSite(drone, siteName, siteName);
            assertTrue(FtpUtil.uploadContent(shareUrl, testUser, DEFAULT_PASSWORD, bigDataFile, "Alfresco/Sites/" + siteName + "/documentLibrary/"), " File[" + bigDataFile.getAbsolutePath() + "] don't upload via FTP.");
        }
        finally
        {
            if (bigDataFile != null && bigDataFile.exists())
            {
                bigDataFile.delete();
            }
        }
    }

    @Test(groups = { "Performance", "Share", "NonGrid", "EnterpriseOnly" })
    public void AONE_11810() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        File downloadedBigDataFile = new File(downloadDirectory + BIG_DATA_FILE);

        try
        {
            super.tearDown();
            super.setupCustomDrone(DownLoadDrone);
            login(customDrone, testUser, DEFAULT_PASSWORD);
            DocumentLibraryPage documentLibraryPage = openSitesDocumentLibrary(customDrone, siteName);
            DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(BIG_DATA_FILE);
            documentDetailsPage.clickOnDownloadLinkForUnsupportedDocument();
            documentDetailsPage.waitForFile(300000, downloadDirectory + BIG_DATA_FILE);
            assertTrue(downloadedBigDataFile.exists(), "Big data file don't download");
            assertEquals(downloadedBigDataFile.length(), 1024 * 1024 * 2047, "File does not fully downloaded.");
        }
        finally
        {
            ShareUser.logout(customDrone);
            if (downloadedBigDataFile.exists())
            {
                downloadedBigDataFile.delete();
            }
            super.tearDown();
            super.beforeClass();
        }
    }

    @Test(groups = { "DataPrepPerformance", "Share", "NonGrid", "EnterpriseOnly" })
    public void dataPrep_AONE_11816() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);

        CreateActivateUser(drone, ADMIN_USERNAME, testUser);
        login(drone, testUser, DEFAULT_PASSWORD);
        createSite(drone, siteName, siteName);
        openSitesDocumentLibrary(drone, siteName);
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setContent(fileName);
        contentDetails.setName(fileName);
        contentDetails.setDescription(fileName);
        DocumentLibraryPage documentLibraryPage = createContent(drone, contentDetails, PLAINTEXT);
        DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(fileName).render();
        SelectAspectsPage selectAspectsPage = documentDetailsPage.selectManageAspects().render();
        List<DocumentAspect> documentAspectList = new ArrayList<>();
        documentAspectList.add(CLASSIFIABLE);
        selectAspectsPage.add(documentAspectList);
        selectAspectsPage.clickApplyChanges().render();

        login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        HTTP_CORE.setContext(CORE_HELPER.makeContextFrom(((WebDroneImpl) drone).getDriver()));
        for (int i = 0; i < 120; i++)
        {
            String categoryName = getCategoryName(testName + i);
            createCategory(categoryName);
        }
    }

    @Test(groups = { "Performance", "Share", "NonGrid", "EnterpriseOnly" })
    public void AONE_11816() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);

        login(drone, testUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = openSitesDocumentLibrary(drone, siteName);
        DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(fileName).render();
        EditDocumentPropertiesPage editDocumentPropertiesPage = documentDetailsPage.selectEditProperties().render();
        CategoryPage categoryPage = editDocumentPropertiesPage.getCategory();
        assertTrue(categoryPage.isCategoryPageVisible(), "Category Page don't open.");
        assertTrue(categoryPage.getAddAbleCatgoryList().size() > 120, "Categories don't displayed.");
        List<String> categoryNameList = new ArrayList<>();
        for (int i = 0; i < 120; i++)
        {
            categoryNameList.add(getCategoryName(testName + i));
        }
        categoryPage.addCategories(categoryNameList);
        categoryPage.clickOk();
        editDocumentPropertiesPage = editDocumentPropertiesPage.render();
        assertTrue(editDocumentPropertiesPage.getCategoryList().size() == 120, "Added categories don't display on EditDocumentPage.");
        editDocumentPropertiesPage.clickSave();
        documentDetailsPage = documentDetailsPage.render();
        assertTrue(documentDetailsPage.getCategoriesNames().size() == 120, "Categories don't added to page.");
    }

    @Test(groups = { "DataPrepPerformance", "Share", "NonGrid", "EnterpriseOnly" })
    public void dataPrep_AONE_11817() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);

        CreateActivateUser(drone, ADMIN_USERNAME, testUser);
        login(drone, testUser, DEFAULT_PASSWORD);
        createSite(drone, siteName, siteName);
        openSitesDocumentLibrary(drone, siteName);
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setContent(fileName);
        contentDetails.setName(fileName);
        contentDetails.setDescription(fileName);
        createContent(drone, contentDetails, PLAINTEXT);
        HTTP_CORE.setContext(CORE_HELPER.makeContextFrom(((WebDroneImpl) drone).getDriver()));
        for (int i = 0; i < 120; i++)
        {
            String tagName = getTagName(testName + i);
            createTag(tagName);
        }
    }

    @Test(groups = { "Performance", "Share", "NonGrid", "EnterpriseOnly" })
    public void AONE_11817() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);
        String tagName;

        login(drone, testUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = openSitesDocumentLibrary(drone, siteName);
        DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(fileName).render();
        EditDocumentPropertiesPage editDocumentPropertiesPage = documentDetailsPage.selectEditProperties().render();
        TagPage tagPage = editDocumentPropertiesPage.getTag();
        webDriverWait(drone, 3000);
        int allTagsCount = tagPage.getAllTagsCount();
        logger.info("AONE_11817: tag_count =" + allTagsCount);
        assertTrue(allTagsCount > 119, "More than 120 tags don't created.");
        tagName = getTagName(testName + 999);
        tagPage.enterTagValue(tagName);
        tagName = getTagName(testName + 888);
        tagPage.enterTagValue(tagName);
        tagPage.refreshTags();
        int newAllTagsCount = tagPage.getAllTagsCount();
        assertTrue((allTagsCount + 2) == newAllTagsCount, "New tags don't added.");
        tagPage.waitUntilAlert();
        editDocumentPropertiesPage = tagPage.clickCancelButton().render();
        editDocumentPropertiesPage.waitUntilAlert();
        tagPage = editDocumentPropertiesPage.getTag();
        tagPage.waitUntilAlert();
        assertTrue(newAllTagsCount == tagPage.getAllTagsCount(), "New tags don't displayed.");
        List<String> listTagNames = tagPage.getAllTagsName();
        for (int i = 0; i < 120; i++)
        {
            tagPage.enterTagValue(listTagNames.get(i));
        }
        editDocumentPropertiesPage = tagPage.clickOkButton();
        assertTrue(editDocumentPropertiesPage.hasTags(), "Tags don't added");
        for (int i = 0; i < 120; i++)
        {
            editDocumentPropertiesPage.isTagVisible(listTagNames.get(i));
        }
        editDocumentPropertiesPage.clickSave();
        documentDetailsPage = documentDetailsPage.render();
        List<String> tags = documentDetailsPage.getTagList();
        assertTrue(tags.size() == 120, "Added tags don't displayed in tags block. ACE-3484");
    }

    @Test(groups = { "DataPrepPerformance", "Share", "NonGrid", "EnterpriseOnly" })
    public void dataPrep_AONE_11818() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String siteName2 = getSiteName(testName + "2");

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setContent(LONG_FILE_NAME);
        contentDetails.setName(LONG_FILE_NAME);
        contentDetails.setDescription(LONG_FILE_NAME);

        CreateActivateUser(drone, ADMIN_USERNAME, testUser);
        login(drone, testUser, DEFAULT_PASSWORD);
        createSite(drone, siteName, siteName);
        openSitesDocumentLibrary(drone, siteName);
        createContent(drone, contentDetails, PLAINTEXT);

        createSite(drone, siteName2, siteName2);
        openSitesDocumentLibrary(drone, siteName2);
        createContent(drone, contentDetails, PLAINTEXT);
    }

    @Test(groups = { "Performance", "Share", "NonGrid", "EnterpriseOnly" })
    public void AONE_11818() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        DashBoardPage dashBoardPage = login(drone, testUser).render();
        FacetedSearchPage facetedSearchPage = dashBoardPage.getSearch().search(LONG_FILE_NAME).render();
        assertTrue(facetedSearchPage.isItemPresentInResultsList(DOCUMENT_LIBRARY, LONG_FILE_NAME), "Needed document don't found. MNT-12826");
        assertEquals(facetedSearchPage.getResultCount(), 2, "Not all files found.");
    }

    public Folder createFolderInFolder(Folder folder, String newFolderName)
    {
        Map<String, String> properties = new HashMap<>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
        properties.put(PropertyIds.NAME, newFolderName);

        return folder.createFolder(properties);
    }

    public Document createDocumentInFolder(Folder folder, String documentName) throws IOException
    {
        Map<String, Serializable> docProperties = new HashMap<>();
        docProperties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
        docProperties.put(PropertyIds.NAME, documentName);
        docProperties.put(PropertyIds.DESCRIPTION, "important document");

        ContentStreamImpl fileContent = new ContentStreamImpl();
        fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
        File tempFile = File.createTempFile("testFile" + System.currentTimeMillis(), ".txt");
        fileContent.setStream(new FileInputStream(tempFile));
        Document document = folder.createDocument(docProperties, fileContent, MAJOR);
        if (tempFile.exists())
        {
            tempFile.delete();
        }
        return document;
    }

    public String createTag(String tagName) throws Exception
    {
        URI url = new URI(getShareUrl() + "/proxy/alfresco/api/tag/workspace/SpacesStore");
        JSONObject json = new JSONObject();
        json.put("name", tagName);
        HttpEntity entity = new StringEntity(json.toString(), APPLICATION_JSON);
        Response response = HTTP_CORE.executePostRequest(url, entity);
        return new JSONObject(response.getResponse()).getString("nodeRef");
    }

    public String createCategory(String category) throws Exception
    {
        URI url = new URI(getShareUrl() + "/proxy/alfresco/api/category");
        JSONObject json = new JSONObject();
        json.put("name", category);
        HttpEntity entity = new StringEntity(json.toString(), APPLICATION_JSON);
        Response response = HTTP_CORE.executePostRequest(url, entity);
        return new JSONObject(response.getResponse()).getString("persistedObject");
    }

    @Override
    @AfterClass(alwaysRun = true)
    public void tearDown()
    {
        super.tearDown();
    }

}
