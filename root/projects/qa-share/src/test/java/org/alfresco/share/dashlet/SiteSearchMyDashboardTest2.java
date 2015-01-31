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
package org.alfresco.share.dashlet;

import org.alfresco.po.share.dashlet.SearchLimit;
import org.alfresco.po.share.dashlet.SiteSearchDashlet;
import org.alfresco.po.share.dashlet.SiteSearchItem;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.DocumentAspect;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.wiki.WikiPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by olga.lokhach
 */
@Listeners(FailedTestListener.class)
public class SiteSearchMyDashboardTest2 extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(SiteSearchMyDashboardTest2.class);
    private String siteDomain = "siteSearch.test";
    private static final String LAST_NAME = "SSLastName";
    private static final String NO_RESULTS_FOUND_MESSAGE = "No results found.";

    @Override
    @BeforeClass(alwaysRun=true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Start Tests in: " + testName);
    }
    @Test(groups = { "DataPrepSearchDashlet" })
    public void dataPrep_AONE_14828() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);

    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14828() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        List<String> searchTermsList = new ArrayList<>();
        searchTermsList.add("modified: today" + " and modifier: " + testUser);
        searchTermsList.add("modified: \"" + getDate("yyyy-MM-dd") + "\"" +  " and modifier: " + testUser);
        searchTermsList.add("modified: \"" + getDate("yyyy-MMM-dd") + "\"" +  " and modifier: " + testUser);
        searchTermsList.add("modified: [" + getDate("yyyy-MM-dd") + " TO TODAY]" +  " and modifier: " + testUser);
        searchTermsList.add("modified: [" + getDate("yyyy-MM-dd") + " TO NOW]" +  " and modifier: " + testUser);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Upload File
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo);
        SiteDashboardPage siteDashboardPage = documentLibraryPage.getSiteNav().selectSiteDashBoard().render();
        Assert.assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_CONTENT_DASHLET, fileName, true, siteName));
        siteDashboardPage.getNav().selectMyDashBoard().render();

        // Search
        List<SiteSearchItem> items;
        for (String searchTerm : searchTermsList)
        {
            logger.info("Search Term: " + searchTerm);
            items = ShareUserDashboard.searchSiteSearchDashlet(drone, searchTerm, SearchLimit.HUNDRED);
            Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));
        }

    }

    @Test(groups = { "DataPrepSearchDashlet" })
    public void dataPrep_AONE_14829() throws Exception
    {
        String testName = getTestName();
        String firstName = testName;
        String userName = firstName + " " + LAST_NAME;
        String[] testUserInfo = new String[] { userName, firstName, LAST_NAME };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);
    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14829() throws Exception
    {
        String testName = getTestName();
        String firstName = testName;
        String userName = firstName + " " + LAST_NAME;
        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";
        String[] fileInfo = { fileName, DOCLIB };


        // User login
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Upload File
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo);
        SiteDashboardPage siteDashboardPage = documentLibraryPage.getSiteNav().selectSiteDashBoard().render();
        Assert.assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_CONTENT_DASHLET, fileName, true, siteName));
        siteDashboardPage.getNav().selectMyDashBoard().render();

        // Search
        List<String> searchTermsList = new ArrayList<>();
        searchTermsList.add("modifier: \"" + userName + "\"");
        searchTermsList.add("modifier: " + firstName);
        searchTermsList.add("modifier: " + LAST_NAME);
        List<SiteSearchItem> items;

        for (String searchTerm : searchTermsList)
        {
            items = ShareUserDashboard.searchSiteSearchDashlet(drone, searchTerm, SearchLimit.HUNDRED);
            Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName), "Search Term: " + searchTerm);
        }
    }

    // AONE-14830:Property search: description

    @Test(groups = { "DataPrepSearchDashlet" })
    public void dataPrep_AONE_14830() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String[] testUserInfo = new String[] { testUser };
        String fileName1 = "file1" + testName;
        String fileName2 = "file2" + testName;

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);

        // Site creation
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create file1
        ContentDetails contentDetails = new ContentDetails(fileName1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Create file2 with file1 as description
        contentDetails = new ContentDetails(fileName2, null, fileName1, null);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
        DocumentLibraryPage documentLibraryPage = drone.getCurrentPage().render();
        documentLibraryPage.getNav().selectMyDashBoard().render();


    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14830() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName1 = "file1" + testName;
        String fileName2 = "file2" + testName;

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Search with "file1"
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, fileName1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));

        // Search with "description: file1"
        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "description: " + fileName1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));

    }

    // AONE-14831:Property search: name

    @Test(groups = { "DataPrepSearchDashlet" })
    public void dataPrep_AONE_14831() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String[] testUserInfo = new String[] { testUser };
        String fileName1 = "file1" + testName;
        String fileName2 = "file2" + testName;
        String fileName3 = testName + "_10.10.txt";

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);

        // Site creation
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create file1
        ContentDetails contentDetails = new ContentDetails(fileName1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Create file2 with file1 as description
        contentDetails = new ContentDetails(fileName2, null, fileName1, null);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Create file3 with "_" and "." in name
        contentDetails = new ContentDetails(fileName3);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
        DocumentLibraryPage documentLibraryPage = drone.getCurrentPage().render();
        documentLibraryPage.getNav().selectMyDashBoard().render();

    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14831() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName1 = "file1" + testName;
        String fileName2 = "file2" + testName;
        String fileName3 = testName + "_10.10.txt";

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Search with "file1"
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, fileName1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));

        // Search with "name: file1"
        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "name: " + fileName1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));

        // Search with "name: file3"
        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "name: " + fileName3);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName3));

    }

    //AONE-14832:Property search: title

    @Test(groups = { "DataPrepSearchDashlet" })
    public void dataPrep_AONE_14832() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = "file1" + testName;
        String fileName2 = "file2" + testName;
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);

        // Site creation
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File1
        ContentDetails contentDetails = new ContentDetails(fileName1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Create File2 with File1 as title
        contentDetails = new ContentDetails(fileName2, fileName1, null, null);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
        Assert.assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_CONTENT_DASHLET, fileName2, true, siteName));
        SiteDashboardPage siteDashboardPage = drone.getCurrentPage().render();
        siteDashboardPage.getNav().selectMyDashBoard().render();

    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14832() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName1 = "file1" + testName;
        String fileName2 = "file2" + testName;

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Search with "file1"
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, fileName1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));

        // Search with "title: file1"
        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "title: " + fileName1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));

    }

    //AONE-14833:Property search: author

    @Test(groups = { "DataPrepSearchDashlet" })
    public void dataPrep_AONE_14833() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = testUser;
        String fileName2 = "file2" + testName;
        String[] fileInfo1 = {fileName1};
        String[] fileInfo2 = {fileName2};
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);

        // Site creation
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create content
        ShareUser.uploadFileInFolder(drone, fileInfo1);
        ShareUser.uploadFileInFolder(drone, fileInfo2);

        // Set author as file1 to file2
        ShareUser.setAuthor(drone, fileName2, testUser);

    }



    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14833() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName1 = testUser;
        String fileName2 = "file2" + testName;

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Search with "file1"
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, fileName1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));

        // Search with "author: testUser"
        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "author: " + testUser);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
    }


    //AONE-14857:Property search: creator

    @Test(groups = { "DataPrepSearchDashlet" })
    public void dataPrep_AONE_14857() throws Exception
    {
        String testName = getTestName();
        String firstName = testName;
        String siteName = getSiteName(testName);
        String userName = firstName + " " + LAST_NAME;
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        String[] testUserInfo = new String[] { userName, firstName, LAST_NAME };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);

        // Site creation
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create content
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14857() throws Exception
    {
        String testName = getTestName();
        String firstName = testName;
        String userName = firstName + " " + LAST_NAME;
        String fileName = getFileName(testName) + ".txt";

        // User login
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);

        // Search
        List<String> searchTerms = Arrays.asList("creator: \"" + userName + "\"", "creator: " + firstName, "creator: " + LAST_NAME);
        List<SiteSearchItem> items;

        for (String searchTerm : searchTerms)
        {
            logger.debug("Search Term: " + searchTerm);
            items = ShareUserDashboard.searchSiteSearchDashlet(drone, searchTerm, SearchLimit.HUNDRED);
            Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName), "SearchTerm: " + searchTerm);
        }

    }

    // AONE-14834:Extended search

    @Test(groups = { "DataPrepSearchDashlet" })
    public void dataPrep_AONE_14834() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = "file" + testName;
        String fileName1 = "Content1" + testName;
        String fileName2 = "Content2" + testName;
        String fileName3 = "Content3" + testName;
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);

        // Site creation
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create file with name fileName
        ContentDetails contentDetails = new ContentDetails(fileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        //Create file with title  fileName
        contentDetails = new ContentDetails(fileName1, fileName, null, null);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        //Create file with description fileName
        contentDetails = new ContentDetails(fileName2, null, fileName, null);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        //Create file with content fileName
        contentDetails = new ContentDetails(fileName3, null, null, fileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14834() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName = "file" + testName;
        String fileName1 = "Content1" + testName;
        String fileName2 = "Content2" + testName;
        String fileName3 = "Content3" + testName;

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Search with fileName
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, fileName);
        Assert.assertEquals(items.size(), 4);
        List<String> fileNames = Arrays.asList(fileName, fileName1, fileName2, fileName3);
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }

    }

    @Test(groups = { "DataPrepSearchDashlet" })
    public void dataPrep_AONE_14835() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);



    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14835() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails(fileName);
        DocumentLibraryPage documentLibraryPage = ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
        SiteDashboardPage siteDashboardPage = documentLibraryPage.getSiteNav().selectSiteDashBoard().render();
        Assert.assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_CONTENT_DASHLET, fileName, true, siteName));
        siteDashboardPage.getNav().selectMyDashBoard().render();

        // Search
        List<String> searchTerms = new ArrayList<>();
        searchTerms.add("created: today" +  " and modifier: " + testUser);
        searchTerms.add("created: \"" + getDate("yyyy-MM-dd") + "\"" +  " and modifier: " + testUser);
        searchTerms.add("created: \"" + getDate("yyyy-MMM-dd") + "\"" +  " and modifier: " + testUser);
        searchTerms.add("created: [" + getDate("yyyy-MM-dd")+ " TO TODAY]" +  " and modifier: " + testUser);
        searchTerms.add("created: [" + getDate("yyyy-MM-dd") + " TO NOW]" +  " and modifier: " + testUser);
        List<SiteSearchItem> items;

        for (String searchTerm : searchTerms)
        {
            logger.info("Search Term: " + searchTerm);
            items = ShareUserDashboard.searchSiteSearchDashlet(drone, searchTerm);
            Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));
        }

    }

    //AONE-14836:Property search: type

    @Test(groups = { "DataPrepSearchDashlet" })
    public void dataPrep_AONE_14836() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);
        String folderName = getFolderName(testName);
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create Folder
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        // Create File
        ContentDetails contentDetails = new ContentDetails(fileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14836() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName = getFileName(testName);
        String folderName = getFolderName(testName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        //Search with TYPE:"cm:cmobject" and modifier: "user"
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "TYPE:\"cm:cmobject\"" + " and modifier: " + testUser);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, folderName));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

    }

    //AONE-14837:Property search: ID

    @Test(groups = { "DataPrepSearchDashlet" })
    public void dataPrep_AONE_14837() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);
        String[] fileInfo = { fileName, DOCLIB };
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        //Upload content
        ShareUser.uploadFileInFolder(drone, fileInfo);
    }


    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14837() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Get the node reference of the document
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        String nodeRef = documentLibraryPage.getFileDirectoryInfo(fileName).getNodeRef();
        documentLibraryPage.getNav().selectMyDashBoard().render();

        // Search with ID: "noderef"
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "ID: \"" + nodeRef + "\"");
        Assert.assertEquals(items.size(), 1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

    }

    //AONE-14838:Property search: aspect

    @Test(groups = { "DataPrepSearchDashlet" })
    public void dataPrep_AONE_14838() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName("File1" + testName);
        String fileName2 = getFileName("File2" + testName);
        String fileName3 = getFileName("File3" + testName);
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create content
        ContentDetails contentDetails = new ContentDetails(fileName1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails(fileName2);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails(fileName3);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add aspect
        ShareUser.addAspects(drone, Arrays.asList(DocumentAspect.VERSIONABLE), fileName1);
        ShareUser.addAspects(drone, Arrays.asList(DocumentAspect.TAGGABLE), fileName2);
        ShareUser.addAspects(drone, Arrays.asList(DocumentAspect.CLASSIFIABLE), fileName3);

    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14838() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName1 = getFileName("File1" + testName);
        String fileName2 = getFileName("File2" + testName);
        String fileName3 = getFileName("File3" + testName);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Search with ASPECT:"cm:versionable" and modifier: user
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "ASPECT: \"cm:" + DocumentAspect.VERSIONABLE.getValue() + "\"" + " and modifier: " + testUser);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName3));

        // Search with ASPECT:"cm:taggable" and modifier: user
        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "ASPECT: \"cm:" + DocumentAspect.TAGGABLE.getValue() + "\"" + " and modifier: " + testUser);
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName3));

        // Search with ASPECT:"cm:generalclassifiable" and modifier: user
        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "ASPECT: \"cm:" + "generalclassifiable" + "\"" + " and modifier: " + testUser);
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName3));

    }

    //AONE-14676:Property search: mimetype

    @Test(groups = { "DataPrepSearchDashlet" })
    public void dataPrep_AONE_14839() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String textFileName = "TextFile"+ testName + ".txt";
        String xmlFileName = "note"+ testName + ".xml";
        String htmlFileName = "heading" + testName + ".html";
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Upload TXT File
        String[] textFileInfo = { textFileName };
        ShareUser.uploadFileInFolder(drone, textFileInfo);

        // Upload XML file.
        String[] xmlFileInfo = { xmlFileName };
        ShareUser.uploadFileInFolder(drone, xmlFileInfo);

        // Upload HTML file.
        String[] htmlFileInfo = { htmlFileName };
        ShareUser.uploadFileInFolder(drone, htmlFileInfo);

    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14839() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String textFileName = "TextFile"+ testName + ".txt";
        String xmlFileName = "note"+ testName + ".xml";
        String htmlFileName = "heading" + testName + ".html";

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Search with query: content.mimetype:text\/xml and modifier: user
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "content.mimetype:text\\/xml" + " and modifier: " + testUser);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, xmlFileName));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, textFileName));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, htmlFileName));

        //Search with query: content.mimetype:text\/plain and modifier: user
        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "content.mimetype:text\\/plain" + " and modifier: " + testUser);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, textFileName));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, htmlFileName));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, xmlFileName));

        // Search with query: content.mimetype:text\/html and modifier: user
        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "content.mimetype:text\\/html" + " and modifier: " + testUser);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, htmlFileName));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, xmlFileName));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, textFileName));

    }

    // AONE-14840:Mixed query

    @Test(groups = { "DataPrepSearchDashlet" })
    public void dataPrep_AONE_14840() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);
        String[] fileInfo = { fileName };
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create Content
        ShareUser.uploadFileInFolder(drone, fileInfo);
    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14840() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName = getFileName(testName);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Search with query: creator:"admin" and name:"Content.txt"
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "creator: \"admin\" and name: \"" + fileName + "\"");
        Assert.assertTrue(items.isEmpty());
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        Assert.assertEquals(searchDashlet.getContent(), NO_RESULTS_FOUND_MESSAGE);

        // Search with query: creator:"TestUser" and name:"Content.txt"
        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "creator: \"" + testUser + "\" and name: \"" + fileName + "\"");
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

        // Search with query: creator:"admin" or name:"Content.txt"
        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "creator: \"admin\" or name: \"" + fileName + "\"");
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

        // Search with query: (creator:"TestUser" or name:"Content.txt") and creator:"admin"
        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "(creator: \"" + testUser + "\" and name: \"" + fileName + "\") and creator: \"admin\"");
        Assert.assertTrue(items.isEmpty());
        searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        Assert.assertEquals(searchDashlet.getContent(), NO_RESULTS_FOUND_MESSAGE);

        // Search with query: (creator:"TestUser" and name:"Content.txt") or creator:"admin"
        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "(creator: \"" + testUser + "\" and name: \"" + fileName + "\") or creator: \"admin\"");
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

        // Search with query: creator:"TestUser" and (name:"Content.txt" or creator:"admin")
        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "creator: \"" + testUser + "\" and (name: \"" + fileName + "\" or creator: \"admin\")");
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

    }

    // AONE-14841:KEYWORDS()

    @Test(groups = { "DataPrepSearchDashlet" })
    public void dataPrep_AONE_14841() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);
        String[] fileInfo = { fileName };
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create Content
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14841() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName = getFileName(testName);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Search with query: KEYWORDS:"filename"
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "KEYWORDS: \"" + fileName +"\"");
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

    }

    // AONE-14843:Fully qualified data type

    @Test(groups = { "DataPrepSearchDashlet" })
    public void dataPrep_AONE_14843() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create content
        ContentDetails contentDetails = new ContentDetails(fileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add aspect
        ShareUser.addAspects(drone, Arrays.asList(DocumentAspect.DUBLIN_CORE), fileName);

    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14843() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName = getFileName(testName);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Search with query: ASPECT:"{http://www.alfresco.org/model/content/1.0}dublincore"
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, " ASPECT:\"{http://www.alfresco.org/model/content/1.0}dublincore\"");
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

    }

    // AONE-14844:Wildcards

    @Test(groups = { "DataPrepSearchDashlet" })
    public void dataPrep_AONE_14844() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = "1234.txt";
        String[] fileInfo = { fileName };
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create content
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14844() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName = "1234.txt";

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Search
        List<String> searchTerms = new ArrayList<String>();
        searchTerms.add("12?4");
        searchTerms.add("*234");
        searchTerms.add("="+fileName);
        searchTerms.add("\"1234\"");
        searchTerms.add("????" + " and modifier: " + testUser);
        searchTerms.add("\"???4\"");

        List<SiteSearchItem> items;

        for (String searchTerm : searchTerms)
        {
            logger.info("Search Term: " + searchTerm);
            items = ShareUserDashboard.searchSiteSearchDashlet(drone, searchTerm);
            Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));
        }

    }

    // AONE-14845:Fully qualified property

    @Test(groups = { "DataPrepSearchDashlet" })
    public void dataPrep_AONE_14845() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName };
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Upload Content
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14845() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName = getFileName(testName) + ".txt";

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Search
        List<String> searchTerms = new ArrayList<String>();
        searchTerms.add("{http://www.alfresco.org/model/content/1.0}name: "+fileName);
        searchTerms.add("@{http://www.alfresco.org/model/content/1.0}name: "+fileName);

        List<SiteSearchItem> items;

        for (String searchTerm : searchTerms)
        {
            logger.info("Search Term: " + searchTerm);
            items = ShareUserDashboard.searchSiteSearchDashlet(drone, searchTerm);
            Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));
        }

    }

    //AONE-14846:CMIS style property

    @Test(groups = { "DataPrepSearchDashlet" })
    public void dataPrep_AONE_14846() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName };
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Upload Content
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14846() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName = getFileName(testName) + ".txt";

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Search with query cm_name: fileName
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "cm_name: " + fileName);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

    }

    //AONE-14847:Complex query

    @Test(groups = { "DataPrepSearchDashlet" })
    public void dataPrep_AONE_14847() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = "test.txt";
        String fileName2 = "tab.txt";
        String fileName3 = "alf.txt";
        String[] fileInfo1 = { fileName1 };
        String[] fileInfo2 = { fileName2 };
        String[] fileInfo3 = { fileName3 };
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Upload 3 Files
        ShareUser.uploadFileInFolder(drone, fileInfo1);
        ShareUser.uploadFileInFolder(drone, fileInfo2);
        ShareUser.uploadFileInFolder(drone, fileInfo3);

    }


    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14847() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName1 = "test.txt";
        String fileName2 = "tab.txt";
        String fileName3 = "alf.txt";

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Search with query: +created:[MIN TO NOW] AND +modifier: (user1) AND !test AND -(tab)
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "+created:[MIN TO NOW] AND +modifier: (" + testUser + ") AND !test AND -(tab)");
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName3));

    }

    //AONE-14848:Finding nodes by boolean property values

    @Test(groups = { "DataPrepSearchDashlet" })
    public void dataPrep_AONE_14848() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName)+".txt";
        String[] fileInfo = { fileName };
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Upload File
        ShareUser.uploadFileInFolder(drone, fileInfo);

        // Create Wiki Page
        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName).render();
        CustomizeSitePage customizeSitePage = siteDashboardPage.getSiteNav().selectCustomizeSite();
        customizeSitePage.addPages(asList(SitePageType.WIKI));

        WikiPage wikiPage = siteDashboardPage.getSiteNav().selectWikiPage().render();
        List<String> txtLines = new ArrayList<>();
        txtLines.add(0, testName + "wiki");
        wikiPage.createWikiPage(testName + "wiki", txtLines).render();

    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14848() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName = getFileName(testName)+".txt";
        String wikiPage = testName+"wiki";

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Search with query: cm:initialVersion:true and modifier:user
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "cm:initialVersion:true" + " AND modifier:" + testUser);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, wikiPage));

    }

    // AONE-14849:Finding nodes by integer or long property values

    @Test(groups = { "DataPrepSearchDashlet" })
    public void dataPrep_AONE_14849() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName)+"-1.txt";
        String fileName2 = getFileName(testName)+"-2.txt";
        String[] fileInfo1 = { fileName1 };
        String[] fileInfo2 = { fileName2 };
        String comment = getComment(fileName1);
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Upload two files
        ShareUser.uploadFileInFolder(drone, fileInfo1);
        ShareUser.uploadFileInFolder(drone, fileInfo2);

        // Add Comment for File1
        ShareUser.addComment(drone, fileName1, comment);

    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14849() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName1 = getFileName(testName)+"-1.txt";
        String fileName2 = getFileName(testName)+"-2.txt";

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Search with query: fm:commentCount: 1
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "fm:commentCount: 1", SearchLimit.HUNDRED);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));

    }

    // AONE-14850:Finding nodes by float and double property values

    @Test(groups = { "DataPrepSearchDashlet" })
    public void dataPrep_AONE_14850() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName)+"-1.txt";
        String fileName2 = getFileName(testName)+"-2.txt";
        String[] fileInfo1 = { fileName1 };
        String[] fileInfo2 = { fileName2 };
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Upload two documents
        ShareUser.uploadFileInFolder(drone, fileInfo1);
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo2);

        // Edit file1 and make some changes (To update the version)
        documentLibraryPage.selectFile(fileName1).render();
        ShareUser.editTextDocument(drone, fileName1, fileName1, fileName1);

    }


    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14850() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName1 = getFileName(testName)+"-1.txt";
        String fileName2 = getFileName(testName)+"-2.txt";

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Verify upon searching with "cm:versionLabel: 1.1", search results show File 1.
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "cm:versionLabel: 1.1" + " and creator: " + testUser);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));

    }

    //AONE-14851:Range Queries

    @Test(groups = { "DataPrepSearchDashlet" })
    public void dataPrep_AONE_14851() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName)+"-1.txt";
        String fileName2 = getFileName(testName)+"-2.txt";
        String fileName3 = getFileName(testName)+"-3.txt";
        String[] fileInfo1 = { fileName1 };
        String[] fileInfo2 = { fileName2 };
        String[] fileInfo3 = { fileName3 };
        String comment = testName+" Comment.";
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Upload 3 Files
        ShareUser.uploadFileInFolder(drone, fileInfo1);
        ShareUser.uploadFileInFolder(drone, fileInfo2);
        ShareUser.uploadFileInFolder(drone, fileInfo3);

        // Add one comment to File2
        ShareUser.addComment(drone, fileName2, comment);

        // Add two comments to File3
        ShareUser.addComment(drone, fileName3, comment);
        ShareUser.addComment(drone, fileName3, comment);

    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14851() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName1 = getFileName(testName)+"-1.txt";
        String fileName2 = getFileName(testName)+"-2.txt";
        String fileName3 = getFileName(testName)+"-3.txt";

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Search with query: fm:commentCount:1..2 and creator: user
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "fm:commentCount:1..2" + " and creator: " + testUser);
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName3));

    }

    //AONE-14852:Proximity

    @Test(groups = { "DataPrepSearchDashlet" })
    public void dataPrep_AONE_14852() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName)+"-1.txt";
        String fileName2 = getFileName(testName)+"-2.txt";
        String file1Content = "big red apple 14852";
        String file2Content = "big red tasty sweet apple 14852";
        String[] fileInfo1 = {fileName1, DOCLIB, file1Content};
        String[] fileInfo2 = {fileName2, DOCLIB, file2Content};
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create 2 Files
        ShareUser.uploadFileInFolder(drone, fileInfo1);
        ShareUser.uploadFileInFolder(drone, fileInfo2);

    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14852() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName1 = getFileName(testName)+"-1.txt";
        String fileName2 = getFileName(testName)+"-2.txt";

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Search with query: "big * apple or TEXT:(big * 14852)"
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "TEXT:(big * 14852)");
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));

        // Search with query: "big *(1) apple or TEXT:(big *(2) 14852)"
        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "TEXT:(big *(2) 14852)");
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));

    }

    //AONE-14853:Boosts

    @Test(groups = { "DataPrepSearchDashlet" })
    public void dataPrep_AONE_14853() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName)+".txt";
        String fileContent = "big red apple 14853";
        String[] fileInfo = {fileName, DOCLIB, fileContent};
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create Content
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14853() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName = getFileName(testName)+".txt";

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Search with query:  "big red apple 14853"^3
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "\"big red apple 14853\"^3");
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));


    }

    //AONE-14854:Explicit spans/positions

    @Test(groups = { "DataPrepSearchDashlet" })
    public void dataPrep_AONE_14854() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName)+"-1.txt";
        String fileName2 = getFileName(testName)+"-2.txt";
        String file1Content = "this is an item 14854";
        String file2Content = "this is the best item 14854";
        String[] fileInfo1 = {fileName1, DOCLIB, file1Content};
        String[] fileInfo2 = {fileName2, DOCLIB, file2Content};
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create 2 Files
        ShareUser.uploadFileInFolder(drone, fileInfo1);
        ShareUser.uploadFileInFolder(drone, fileInfo2);

    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14854() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName1 = getFileName(testName)+"-1.txt";
        String fileName2 = getFileName(testName)+"-2.txt";

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Search with query:  "this[^] 14854[$]"
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "\"this[^] 14854[$]\"");
        Assert.assertEquals(items.size(), 2);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));

    }

    // AONE-14855:Escaping

    @Test(groups = { "DataPrepSearchDashlet" })
    public void dataPrep_AONE_14855() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName)+".txt";
        String fileContent = "<type>d:text14855</type>";
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create Content
        ContentDetails contentDetails = new ContentDetails(fileName, fileName, "" ,fileContent);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14855() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName = getFileName(testName)+".txt";

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Search with query:   "\<type\>d\:text\<\/type\>"
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "\\<type\\>d\\:text14855\\</type\\>");
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

    }

    //AONE-14856:Range Queries

    @Test(groups = { "DataPrepSearchDashlet" })
    public void dataPrep_AONE_14856() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName)+".txt";
        String fileContent = "apple and banana";
        String[] fileInfo = {fileName, DOCLIB, fileContent};
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create Content
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_14856() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Search with query:  TEXT:apple..banana
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "TEXT:apple..banana");
        Assert.assertTrue(items.isEmpty());
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        Assert.assertEquals(searchDashlet.getContent(), NO_RESULTS_FOUND_MESSAGE);

    }



}


