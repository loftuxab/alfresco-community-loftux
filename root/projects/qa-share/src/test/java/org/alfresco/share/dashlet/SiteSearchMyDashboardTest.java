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

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.dashlet.SearchLimit;
import org.alfresco.po.share.dashlet.SiteSearchDashlet;
import org.alfresco.po.share.dashlet.SiteSearchItem;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.blog.BlogPage;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.po.share.site.calendar.EditEventForm;
import org.alfresco.po.share.site.calendar.InformationEventForm;
import org.alfresco.po.share.site.datalist.DataListPage;
import org.alfresco.po.share.site.discussions.DiscussionsPage;
import org.alfresco.po.share.site.document.*;
import org.alfresco.po.share.site.links.LinksPage;
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
import static org.alfresco.po.share.enums.DataLists.CONTACT_LIST;

/**
 * @author Ranjith Manyam
 */
@Listeners(FailedTestListener.class)
public class SiteSearchMyDashboardTest extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(SiteSearchMyDashboardTest.class);
    private String siteDomain = "siteSearchr.test";
    private String expectedHelpBallonMsg = "Use this dashlet to perform a site search and view the results.\nClicking the item name takes you to the details page so you can preview or work with the item.";
    private static final String NO_RESULTS_FOUND_MESSAGE = "No results found.";

    private static final SitePageType[] PAGE_TYPES = {
            SitePageType.BLOG,
            SitePageType.CALENDER,
            SitePageType.DATA_LISTS,
            SitePageType.DISCUSSIONS,
            SitePageType.LINKS,
            SitePageType.WIKI
    };

    @Override
    @BeforeClass(alwaysRun=true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Start Tests in: " + testName);
    }

    @Test(groups={"DataPrepEnterpriseOnly"})
    public void dataPrep_AONE_14816() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        String[] testUserInfo = new String[] {testUser};
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);

        ShareUser.logout(drone);
    }

    @Test(groups = { "EnterpriseOnly"})
    public void AONE_14816() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        DashBoardPage dashBoardPage = (DashBoardPage) ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Verify Site search Dashlet has been added to the Site dashboard
        Assert.assertNotNull(dashBoardPage);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        Assert.assertNotNull(searchDashlet);

        //1. The following items are displayed: "Site Search" name of the dashlet;
        Assert.assertEquals(searchDashlet.getTitle(), Dashlets.SITE_SEARCH.getDashletName());
        //Search field with the "Search" button;
        Assert.assertTrue(searchDashlet.isSearchFieldDisplayed(), "Search field is not found!");
        Assert.assertTrue(searchDashlet.isSearchButtonDisplayed(), "Search button is not found!");
        //Drop down menu with the number of items to be displayed;
        Assert.assertTrue(searchDashlet.isDropDownResultsSizeDisplayed(), "Result size drop down is not found!");

        // Verify Help balloon message has been displayed correctly
        //2. Click on ? icon
        searchDashlet.clickOnHelpIcon();
        Assert.assertTrue(searchDashlet.isBalloonDisplayed(), "Balloon is not displayed!");
        Assert.assertEquals(searchDashlet.getHelpBalloonMessage(), expectedHelpBallonMsg);
        //3. Click on X icon.
        searchDashlet.closeHelpBallon().render();
        Assert.assertFalse(searchDashlet.isBalloonDisplayed());

        //4. Click on drop down menu.
        Assert.assertEquals(searchDashlet.getAvailableResultSizes(), Arrays.asList("10", "25", "50", "100"));

        //5. Click on "Search" button.
        searchDashlet.search("").render();
        Assert.assertEquals(searchDashlet.getContent(), NO_RESULTS_FOUND_MESSAGE);
    }

    @Test(groups={"DataPrepEnterpriseOnly"})
    public void dataPrep_AONE_14817() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        String[] testUserInfo = new String[] {testUser};
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);

        ShareUser.logout(drone);
    }

    @Test(groups = { "EnterpriseOnly", "IntermittentBugs"})
    public void AONE_14817() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        String searchText = "£$%^&";
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, searchText);
        Assert.assertTrue(items.isEmpty());
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        Assert.assertEquals(searchDashlet.getContent(), NO_RESULTS_FOUND_MESSAGE);

        searchText = ShareUser.getRandomStringWithNumders(1400);

        // Search
        searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search(searchText).render();
        Assert.assertEquals(searchDashlet.getSearchText().length(), 1024);
    }

    @Test(groups={"DataPrepEnterpriseOnly"})
    public void dataPrep_AONE_14818() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + "-.txt";
        String[] fileInfo = { fileName, DOCLIB };

        String[] testUserInfo = new String[] {testUser};
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // Upload File
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUser.logout(drone);
    }

    @Test(groups = { "EnterpriseOnly"})
    public void AONE_14818() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName = getFileName(testName) + "-.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, fileName);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));
        DocumentDetailsPage detailsPage = items.get(0).getItemName().click().render();
        Assert.assertTrue(detailsPage != null);

    }

    @Test(groups={"DataPrepEnterpriseOnly"})
    public void dataPrep_AONE_14819() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String content = "content";
        String test = "finish";
        String fileName = content + testName + test + ".doc";
        String[] fileInfo = { fileName, DOCLIB };

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);

        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName).render();
        CustomizeSitePage customizeSitePage = siteDashboardPage.getSiteNav().selectCustomizeSite();
        customizeSitePage.addPages(asList(PAGE_TYPES));

        // Create Wiki Page
        WikiPage wikiPage = siteDashboardPage.getSiteNav().selectWikiPage().render();

        List<String> txtLines = new ArrayList<>();
        txtLines.add(0, content + "wiki" + test);
        wikiPage.createWikiPage(content + "wiki" + test, txtLines).render();

        // Create Blog Post
        BlogPage blogPage = siteDashboardPage.getSiteNav().selectBlogPage().render();
        blogPage.createPostInternally(content + "blog" + test, content + "blog" + test).render();

        // Create any event
        CalendarPage calendarPage = siteDashboardPage.getSiteNav().selectCalendarPage().render();
        calendarPage.createEvent(content + "event" + test, content + "event" + test, content + "event" + test, true).render();

        // Create Data List
        siteDashboardPage.getSiteNav().selectDataListPage();
        DataListPage dataListPage = new DataListPage(drone).render();
        dataListPage.createDataList(CONTACT_LIST, content + "dataList" + test, content + "dataList" + test).render();

        //Create a topic
        DiscussionsPage discussionsPage = siteDashboardPage.getSiteNav().selectDiscussionsPage().render();
        discussionsPage.createTopic(content + "discussion" + test, content + "discussion" + test).render();

        ShareUser.logout(drone);
    }

    @Test(groups = { "EnterpriseOnly"})
    public void AONE_14819() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String content = "content";
        String test = "finish";
        String fileName = content + testName + test + ".doc";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "*" + test, SearchLimit.HUNDRED);

        // Search
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, content + "wiki" + test));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, content + "blog" + test));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, content + "event" + test));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, content + "dataList" + test));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, content + "discussion" + test));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

    }

    @Test(groups={"DataPrepEnterpriseOnly"})
    public void dataPrep_AONE_14820() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String content = "content";
        String test = "finish";
        String fileName = content + testName + test + ".doc";
        String[] fileInfo = { fileName, DOCLIB };

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);

        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName).render();
        CustomizeSitePage customizeSitePage = siteDashboardPage.getSiteNav().selectCustomizeSite();
        customizeSitePage.addPages(asList(PAGE_TYPES));

        // Create Wiki Page
        WikiPage wikiPage = siteDashboardPage.getSiteNav().selectWikiPage().render();

        List<String> txtLines = new ArrayList<>();
        txtLines.add(0, content + "wiki" + test);
        wikiPage.createWikiPage(content + "wiki" + test, txtLines).render();

        // Create Blog Post
        BlogPage blogPage = siteDashboardPage.getSiteNav().selectBlogPage().render();
        blogPage.createPostInternally(content + "blog" + test, content + "blog" + test).render();

        // Create any event
        CalendarPage calendarPage = siteDashboardPage.getSiteNav().selectCalendarPage().render();
        calendarPage.createEvent(content + "event" + test, content + "event" + test, content + "event" + test, true).render();

        // Create Data List
        siteDashboardPage.getSiteNav().selectDataListPage();
        DataListPage dataListPage = new DataListPage(drone).render();
        dataListPage.createDataList(CONTACT_LIST, content + "dataList" + test, content + "dataList" + test).render();

        //Create a topic
        DiscussionsPage discussionsPage = siteDashboardPage.getSiteNav().selectDiscussionsPage().render();
        discussionsPage.createTopic(content + "discussion" + test, content + "discussion" + test).render();

        ShareUser.logout(drone);
    }

    @Test(groups = { "EnterpriseOnly"})
    public void AONE_14820() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String content = "content";
        String test = "finish";
        String part = "tent";
        String fileName = content + testName + test + ".doc";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, content + "*", SearchLimit.HUNDRED);

        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, content + "wiki" + test));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, content + "blog" + test));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, content + "event" + test));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, content + "dataList" + test));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, content + "discussion" + test));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

        // Configure Saved search with "*test"
        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "*" + test, SearchLimit.HUNDRED);

        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, content + "wiki" + test));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, content + "blog" + test));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, content + "event" + test));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, content + "dataList" + test));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, content + "discussion" + test));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

        // Configure Saved search with "*test*"
        //items = ShareUserDashboard.searchSiteSearchDashlet(drone, "*" + test + "*", SearchLimit.HUNDRED);
        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "*" + part + "*", SearchLimit.HUNDRED);

        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, content + "wiki" + test));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, content + "blog" + test));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, content + "event" + test));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, content + "dataList" + test));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, content + "discussion" + test));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

    }

    @Test(groups={"DataPrepEnterpriseOnly"})
    public void dataPrep_AONE_14821() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String description = testName + 1;
        String fileName = testName + ".doc";

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setDescription(description);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName).render();
        CustomizeSitePage customizeSitePage = siteDashboardPage.getSiteNav().selectCustomizeSite();
        customizeSitePage.addPages(asList(PAGE_TYPES));

        // Create Wiki Page
        WikiPage wikiPage = siteDashboardPage.getSiteNav().selectWikiPage().render();

        List<String> txtLines = new ArrayList<>();
        txtLines.add(0, description);
        wikiPage.createWikiPage(testName + "wiki", txtLines).render();

        // Create Blog Post
        BlogPage blogPage = siteDashboardPage.getSiteNav().selectBlogPage().render();
        blogPage.createPostInternally(testName + "blog", description).render();

        // Create any event
        CalendarPage calendarPage = siteDashboardPage.getSiteNav().selectCalendarPage().render();
        calendarPage.createEvent(testName + "event", description, description, true).render();

        // Create Data List
        siteDashboardPage.getSiteNav().selectDataListPage();
        DataListPage dataListPage = new DataListPage(drone).render();
        dataListPage.createDataList(CONTACT_LIST, testName + "dataList", description).render();

        //Create a topic
        DiscussionsPage discussionsPage = siteDashboardPage.getSiteNav().selectDiscussionsPage().render();
        discussionsPage.createTopic(testName + "discussion", description).render();

        //Create a link
        LinksPage linksPage = siteDashboardPage.getSiteNav().selectLinksPage().render();
        String url = getRandomString(7);
        linksPage.createLink(testName + "link", url, description, description).render();

        ShareUser.logout(drone);
    }

    @Test(groups = { "EnterpriseOnly"})
    public void AONE_14821() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String description = testName + 1;
        String fileName = testName + ".doc";


        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Configure Site search with Description
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, description);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, testName + "wiki"));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, testName + "blog"));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, testName + "event"));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, testName + "dataList"));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, testName + "discussion"));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, testName + "link"));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

    }

    @Test(groups={"DataPrepEnterpriseOnly"})
    public void dataPrep_AONE_14822() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String tag = testName + 2;
        String fileName = testName + ".doc";

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        ShareUser.openDocumentLibrary(drone);
        ShareUserSitePage.addTag(drone, fileName, tag);

        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName).render();
        CustomizeSitePage customizeSitePage = siteDashboardPage.getSiteNav().selectCustomizeSite();
        customizeSitePage.addPages(asList(PAGE_TYPES));

        // Create Wiki Page
        WikiPage wikiPage = siteDashboardPage.getSiteNav().selectWikiPage().render();

        List<String> txtLines = new ArrayList<>();
        txtLines.add(0, testName + "wiki");
        List<String> tagLines = new ArrayList<>();
        txtLines.add(0, tag);
        wikiPage.createWikiPage(testName + "wiki", txtLines, tagLines).render();

        // Create Blog Post
        BlogPage blogPage = siteDashboardPage.getSiteNav().selectBlogPage().render();
        blogPage.createPostInternally(testName + "blog", testName + "blog", tag).render();

        // Create any event
        CalendarPage calendarPage = siteDashboardPage.getSiteNav().selectCalendarPage().render();
        calendarPage = (CalendarPage) calendarPage.chooseMonthTab();

        calendarPage = calendarPage.createEvent(testName + "event", testName + "event", testName + "event", false);

        InformationEventForm informationEventForm = calendarPage.clickOnEvent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, testName + "event");
        EditEventForm editEventForm = informationEventForm.clickOnEditEvent();
        editEventForm.setTagsField(tag);
        editEventForm.clickAddTag();
        editEventForm.clickSave();

        //Create a topic
        DiscussionsPage discussionsPage = siteDashboardPage.getSiteNav().selectDiscussionsPage().render();
        discussionsPage.createTopic(testName + "discussion", testName + "discussion", tag).render();

        //Create a link
        LinksPage linksPage = siteDashboardPage.getSiteNav().selectLinksPage().render();
        String url = getRandomString(7);
        linksPage.createLink(testName + "link", url, testName + "link", tag).render();

        ShareUser.logout(drone);
    }

    @Test(groups = { "EnterpriseOnly"})
    public void AONE_14822() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String tag = testName + 2;
        String fileName = testName + ".doc";

        //ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Configure Site search with Tag
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, tag);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, testName + "wiki"));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, testName + "blog"));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, testName + "event"));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, testName + "discussion"));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, testName + "link"));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

    }

    @Test(groups={"DataPrepEnterpriseOnly"})
    public void dataPrep_AONE_14823() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = testName + ".doc";
        String[] fileInfo = { fileName, DOCLIB };

        String[] testUserInfo = new String[] {testUser};
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUser.logout(drone);
    }

    @Test(groups = { "EnterpriseOnly"})
    public void AONE_14823() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName = testName + ".doc";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Configure Site search with file name "test"
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, testName);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));
        // TODO - Used "and modifier: = testUser"
        items = ShareUserDashboard.searchSiteSearchDashlet(drone, ".doc" + " and modifier: " + testUser);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

        ShareUser.logout(drone);
    }

    @Test(groups={"DataPrepEnterpriseOnly"})
    public void dataPrep_AONE_14824() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = testName;
        String[] dots = {".pdf", ".xml", ".html", ".txt", ".eml", ".odp", ".ods", ".odt", ".xls", ".xlsx", ".xsl", ".doc", ".docx", ".ppt",
                ".pptx", ".pot", ".xsd", ".js", ".java", ".css", ".rtf", ".msg"};

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        for (String dot : dots)
        {
            String[] fileInfo = {fileName + dot, DOCLIB};
            ShareUser.uploadFileInFolder(drone, fileInfo);
        }

        ShareUser.logout(drone);
    }

    @Test(groups = { "EnterpriseOnly"})
    public void AONE_14824() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName = testName;
        String[] dots = {".pdf", ".xml", ".html", ".txt", ".eml", ".odp", ".ods", ".odt", ".xls", ".xlsx", ".xsl", ".doc", ".docx", ".ppt",
                ".pptx", ".pot", ".xsd", ".js", ".java", ".css", ".rtf", ".msg"};


        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, fileName, SearchLimit.HUNDRED);

        for (String dot : dots)
        {
            Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName + dot));
        }

    }

    @Test(groups={"DataPrepEnterpriseOnly"})
    public void dataPrep_AONE_14825() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = "12345" + getSiteName(testName);
        String folderName = testName + "-Folder";

        String[] testUserInfo = new String[] {testUser};
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUser.logout(drone);
    }

    @Test(groups = { "EnterpriseOnly"})
    public void AONE_14825() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String folderName = testName + "-Folder";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        //Search with Folder Name
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, folderName);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, folderName));
    }

    @Test(groups={"DataPrepEnterpriseOnly"})
    public void dataPrep_AONE_14826() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);

        String[] testUserInfo = new String[] {testUser};
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);
    }
    // TODO - Used "modifier: testUser" in search criteria
    @Test(groups = { "EnterpriseOnly"})
    public void AONE_14826() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + System.currentTimeMillis() + "_1.1.odt";
        String[] fileInfo = { fileName, DOCLIB };

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        // Create site and upload document
        ShareUser.openSiteDashboard(drone, siteName);
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo);
        documentLibraryPage.getSiteNav().selectSiteDashBoard().render();

        // Wait till index
        Assert.assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_CONTENT_DASHLET, fileName, true, siteName));
        ShareUser.openUserDashboard(drone);

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, ".odt" + " and modifier: " + testUser);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

        items = ShareUserDashboard.searchSiteSearchDashlet(drone, fileName);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

        DocumentDetailsPage detailsPage = (DocumentDetailsPage) ShareUserDashboard.selectItem(items, fileName);
        Assert.assertTrue(detailsPage != null);

        String newFileName = new StringBuffer(fileName).insert(4, '_').toString();
        EditDocumentPropertiesPage editDocumentPropertiesPage = detailsPage.selectEditProperties().render();
        editDocumentPropertiesPage.setName(newFileName);
        editDocumentPropertiesPage.selectSave().render();

        ShareUser.openUserDashboard(drone);

        items = ShareUserDashboard.searchSiteSearchDashlet(drone, ".odt" + " and modifier: " + testUser);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, newFileName));
    }

    @Test(groups={"DataPrepEnterpriseOnly"})
    public void dataPrep_AONE_14827() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB, "Les études de l'admissibilité de la solution dans cette société financière." };

        String[] testUserInfo = new String[] {testUser};
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUserDashboard.addDashlet(drone, Dashlets.SITE_SEARCH);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUser.logout(drone);
    }

    @Test(groups = { "EnterpriseOnly"})
    public void AONE_14827() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName = getFileName(testName) + ".txt";
        String[] searchTerms = {"l'admissibilité", "l'admissibilite", "études", "etudes", "financière", "financiere"};

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        List<SiteSearchItem> items;

        // Loop through the search terms and verify the item is found
        for(String searchTerm: searchTerms)
        {
            items = ShareUserDashboard.searchSiteSearchDashlet(drone, searchTerm);
            Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));
        }
    }


}
