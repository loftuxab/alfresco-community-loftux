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

package org.alfresco.share.sanity;

import org.alfresco.po.share.*;
import org.alfresco.po.share.dashlet.*;
import org.alfresco.po.share.dashlet.sitecontent.DetailedViewInformation;
import org.alfresco.po.share.dashlet.sitecontent.SimpleViewInformation;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CreateSitePage;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.po.share.site.discussions.DiscussionsPage;
import org.alfresco.po.share.site.document.*;
import org.alfresco.po.share.task.EditTaskPage;
import org.alfresco.po.share.task.TaskDetailsPage;
import org.alfresco.po.share.user.*;
import org.alfresco.po.share.workflow.*;
import org.alfresco.po.thirdparty.firefox.RssFeedPage;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static java.util.Arrays.asList;
import static org.alfresco.po.share.dashlet.SiteContentFilter.MY_FAVOURITES;
import static org.alfresco.po.share.dashlet.SiteContentFilter.I_AM_EDITING;
import static org.alfresco.po.share.dashlet.SiteContentFilter.I_HAVE_RECENTLY_MODIFIED;
import static org.alfresco.po.share.dashlet.MyDiscussionsHistoryFilter.*;
import static org.alfresco.po.share.dashlet.MyDiscussionsTopicsFilter.*;
import static org.alfresco.po.share.dashlet.SiteActivitiesHistoryFilter.*;
import static org.alfresco.po.share.dashlet.MyTasksFilter.ACTIVE_TASKS;
import static org.alfresco.po.share.dashlet.MyTasksFilter.COMPLETED_TASKS;
import static org.testng.Assert.*;

/**
 * Created by Olga Lokhach
 */

@Listeners(FailedTestListener.class)

public class SolrTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(SolrTest.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
    }

    /**
     * AONE-8266:My Dashboard
     */

    @Test(groups = "DataPrepSanity", timeOut = 600000)
    public void dataPrep_AONE_8266() throws Exception
    {

        String testName = getTestName();
        String user = getUserNameFreeDomain(testName);
        String[] userInfo = new String[] { user };
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String fileName1 = getFileName(testName) + "-1";
        String fileName2 = getFileName(testName) + "-2";
        String event1 = "single_day";
        String event2 = "all_day";
        String event3 = "mul_day";
        String workFlowName = testName;
        String dueDate = new DateTime().plusDays(2).toString("dd/MM/yyyy");

        // Create User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo);

        // User login
        ShareUser.login(drone, user, DEFAULT_PASSWORD);

        // Add all dashlets to the userdashboard
        DashBoardPage dashBoardPage = ShareUser.openUserDashboard(drone).render();
        dashBoardPage.getNav().selectCustomizeUserDashboard().render();
        CustomiseUserDashboardPage customiseUserDashboardPage = drone.getCurrentPage().render();
        customiseUserDashboardPage.removeAllDashlets();
        dashBoardPage.getNav().selectCustomizeUserDashboard().render();
        customiseUserDashboardPage = drone.getCurrentPage().render();
        customiseUserDashboardPage.selectChangeLayou().selectNewLayout(3);
        customiseUserDashboardPage.addAllDashlets().render();

        //Two sites are created
        //Several events are created: at least one event is passed, three events are upcoming - simple one day event, all day event, multy-day event.
        //The events are created in both sites
        //At least one discussion is created on each site
        //At least one item is being edited on each sites
        //At least one content and folder is marked as favourite on each site
        //At least two tasks are assigned to the user - one active and one completed

        for (int i = 1; i < 3; i++)
        {
            ShareUser.createSite(drone, siteName + i, SITE_VISIBILITY_PUBLIC);
        }

        for (int i = 1; i < 3; i++)
        {
            SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName + i).render();
            CustomizeSitePage customizeSitePage = siteDashboardPage.getSiteNav().selectCustomizeSite();
            customizeSitePage.addPages(asList(SitePageType.CALENDER, SitePageType.DISCUSSIONS)).render();
            siteDashboardPage = drone.getCurrentPage().render();
            CalendarPage calendarPage = siteDashboardPage.getSiteNav().selectCalendarPage();
            Calendar calendar = Calendar.getInstance();
            int lastDate = calendar.getActualMaximum(Calendar.DATE);
            int todayDate = calendar.get(Calendar.DATE);
            int anotherDate;

            // Create any single day event (passed), e.g. event1
            calendarPage = calendarPage
                .createEvent(CalendarPage.ActionEventVia.DAY_TAB, siteName + i + event1 + "passed", event1, event1, String.valueOf(todayDate - 2), null,
                    String.valueOf(todayDate - 2), null, null, false);

            // Create any single day event, e.g. event1
            calendarPage = calendarPage
                .createEvent(CalendarPage.ActionEventVia.DAY_TAB, siteName + i + event1, event1, event1, null, null, null, null, null, false);

            // Create any all day event, e.g. event2
            calendarPage = calendarPage
                .createEvent(CalendarPage.ActionEventVia.DAY_TAB, siteName + i + event2, event2, event2, null, null, null, null, null, true);

            // Create any multiply day event, e.g. event3
            if (lastDate == todayDate)
            {
                anotherDate = todayDate - 1;
                calendarPage.createEvent(CalendarPage.ActionEventVia.DAY_TAB, siteName + i + event3, event3, event3, String.valueOf(anotherDate), null,
                    String.valueOf(todayDate), null, null, false);
            }
            else
            {
                anotherDate = todayDate + 1;
                calendarPage.createEvent(CalendarPage.ActionEventVia.DAY_TAB, siteName + i + event3, event3, event3, String.valueOf(todayDate), null,
                    String.valueOf(anotherDate), null, null, false);
            }

            siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName + i).render();
            DiscussionsPage discussionsPage = siteDashboardPage.getSiteNav().selectDiscussionsPage();
            discussionsPage.createTopic(siteName + i + "topic", "topic");

            ShareUser.openSitesDocumentLibrary(drone, siteName + i).render();
            DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, new String[] { fileName1 + i, DOCLIB }).render();
            FileDirectoryInfo fileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(fileName1 + i);
            fileDirectoryInfo.selectEditOfflineAndCloseFileWindow();
            documentLibraryPage = ShareUser.uploadFileInFolder(drone, new String[] { fileName2 + i, DOCLIB }).render();
            fileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(fileName2 + i);
            fileDirectoryInfo.selectFavourite();
            documentLibraryPage = ShareUser.createFolderInFolder(drone, folderName + i, folderName, DOCLIB_CONTAINER).render();
            fileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(folderName + i);
            fileDirectoryInfo.selectFavourite();

            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone).render();
            StartWorkFlowPage startWorkFlowPage = myTasksPage.selectStartWorkflowButton();
            NewWorkflowPage workFlow = (NewWorkflowPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW).render();
            WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
            formDetails.setMessage(workFlowName + i);
            formDetails.setDueDate(dueDate);
            formDetails.setReviewers(Arrays.asList(user));
            formDetails.setTaskPriority(Priority.MEDIUM);
            workFlow.startWorkflow(formDetails).render();

        }

        // User login
        ShareUser.login(drone, user, DEFAULT_PASSWORD);

        // Complete task2
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        EditTaskPage editTaskPage = myTasksPage.navigateToEditTaskPage(workFlowName + "2");
        editTaskPage.selectTaskDoneButton().render();
    }

    @Test(groups = "Sanity")
    public void AONE_8266() throws Exception
    {

        String testName = getTestName();
        String user = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String fileName1 = getFileName(testName) + "-1";
        String fileName2 = getFileName(testName) + "-2";
        String event1 = "single_day";
        String event2 = "all_day";
        String event3 = "mul_day";
        String workFlowName = testName;
        String rssUrl = "http://feeds.reuters.com/reuters/businessNews";
        String rssTitle = "Reuters: Business News";
        String extSiteUrl = "http://electrictower.ru/";
        String searchTitle = "MyDocs";
        String headerInfo = "Find, rate, and contribute Alfresco add-ons and extensions. Visit the Alfresco Add-ons Home Page";

        // Log in as User
        ShareUser.login(drone, user, DEFAULT_PASSWORD);

        // Verify My Activities Dashlet
        ShareUserDashboard.addDashlet(drone, Dashlets.MY_ACTIVITIES).render();
        MyActivitiesDashlet myActivitiesDashlet = ShareUserDashboard.getDashlet(drone, Dashlets.MY_ACTIVITIES).render();
        assertEquals(myActivitiesDashlet.getTitle(), Dashlets.MY_ACTIVITIES.getDashletName());
        assertTrue(myActivitiesDashlet.isHelpIconDisplayed(), "Help icon isn't displayed");
        myActivitiesDashlet.clickOnHelpIcon();
        assertTrue(myActivitiesDashlet.isBalloonDisplayed(), "Baloon popup isn't displayed");
        myActivitiesDashlet.closeHelpBallon();
        assertFalse(myActivitiesDashlet.isBalloonDisplayed(), "Baloon popup is displayed");

        myActivitiesDashlet.selectOptionFromUserActivities("My activities").render();
        myActivitiesDashlet.selectOptionFromHistoryFilter(TODAY).render();
        String activityEntry = String.format("%s %s added document %s in %s", user, DEFAULT_LASTNAME, fileName2 + 2, siteName + 2);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true), "Activity: " + activityEntry + " is not displayed");
        activityEntry = String.format("%s %s added document %s in %s", user, DEFAULT_LASTNAME, fileName2 + 1, siteName + 1);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true), "Activity: " + activityEntry + " is not displayed");
        activityEntry = String.format("%s %s added folder %s in %s", user, DEFAULT_LASTNAME, folderName + 2, siteName + 2);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true), "Activity: " + activityEntry + " is not displayed");
        activityEntry = String.format("%s %s added folder %s in %s", user, DEFAULT_LASTNAME, folderName + 1, siteName + 1);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true), "Activity: " + activityEntry + " is not displayed");
        activityEntry = String.format("%s %s started discussion %s in %s", user, DEFAULT_LASTNAME, siteName + 2 + "topic", siteName + 2);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true), "Activity: " + activityEntry + " is not displayed");
        activityEntry = String.format("%s %s started discussion %s in %s", user, DEFAULT_LASTNAME, siteName + 1 + "topic", siteName + 1);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true), "Activity: " + activityEntry + " is not displayed");
        activityEntry = String.format("%s %s created calendar event %s in %s", user, DEFAULT_LASTNAME, siteName + 2 + event3, siteName + 2);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true), "Activity: " + activityEntry + " is not displayed");
        activityEntry = String.format("%s %s created calendar event %s in %s", user, DEFAULT_LASTNAME, siteName + 1 + event3, siteName + 1);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true), "Activity: " + activityEntry + " is not displayed");

        myActivitiesDashlet.selectOptionFromHistoryFilter(SEVEN_DAYS).render();
        List<ActivityShareLink> activities = myActivitiesDashlet.getActivities();
        assertTrue(activities.size() != 0, "Information about activities don't reflect in dashlet.");

        myActivitiesDashlet.selectOptionFromHistoryFilter(FOURTEEN_DAYS).render();
        activities = myActivitiesDashlet.getActivities();
        assertTrue(activities.size() != 0, "Information about activities don't reflect in dashlet.");

        myActivitiesDashlet.selectOptionFromHistoryFilter(TWENTY_EIGHT_DAYS).render();
        activities = myActivitiesDashlet.getActivities();
        assertTrue(activities.size() != 0, "Information about activities don't reflect in dashlet.");

        myActivitiesDashlet.selectOptionFromUserActivities("Everyone\'s activities").render();
        activities = myActivitiesDashlet.getActivities();
        assertTrue(activities.size() != 0, "Information about activities don't reflect in dashlet.");

        // Subcribe to RSS and open any page from RSS list
        RssFeedPage rssFeedPage = myActivitiesDashlet.selectRssFeedPage(user, DEFAULT_PASSWORD);
        DocumentDetailsPage documentDetailsPage = rssFeedPage.clickOnFeedContent(fileName2 + 2).render();
        assertNotNull(documentDetailsPage);
        ShareUser.openUserDashboard(drone);

        //Verify My Tasks
        MyTasksDashlet myTasksDashlet = ShareUserDashboard.getDashlet(drone, Dashlets.MY_TASKS).render();
        assertEquals(myTasksDashlet.getTitle(), Dashlets.MY_TASKS.getDashletName());
        assertTrue(myTasksDashlet.isHelpIconDisplayed(), "Help icon isn't displayed");
        myTasksDashlet.clickOnHelpIcon();
        assertTrue(myTasksDashlet.isBalloonDisplayed(), "Baloon popup isn't displayed");
        myTasksDashlet.closeHelpBallon();
        assertFalse(myTasksDashlet.isBalloonDisplayed(), "Baloon popup is displayed");

        // Verify My Tasks : Active Tasks
        ShareUser.openUserDashboard(drone);
        myTasksDashlet = ShareUserDashboard.getDashlet(drone, Dashlets.MY_TASKS).render();
        myTasksDashlet.selectTasksFilter(ACTIVE_TASKS).render();
        assertTrue(myTasksDashlet.isTaskPresent(workFlowName + 1), workFlowName + 1 + " is not found");
        assertTrue(myTasksDashlet.isTaskEditButtonEnabled(workFlowName + 1), "Edit Task button is disabled");
        assertTrue (myTasksDashlet.isTaskViewButtonEnabled(workFlowName + 1), "View Task button is disabled");

        // Click on Edit Task button
        EditTaskPage editTaskPage = myTasksDashlet.selectEditTask(workFlowName + 1).render();
        assertNotNull(editTaskPage);

        // Click on View Task button
        ShareUser.openUserDashboard(drone);
        myTasksDashlet = ShareUserDashboard.getDashlet(drone, Dashlets.MY_TASKS).render();
        TaskDetailsPage taskDetailsPage = myTasksDashlet.selectViewTask(workFlowName + 1).render();
        assertNotNull(taskDetailsPage);

        // Verify My Tasks : Complete Tasks
        ShareUser.openUserDashboard(drone);
        myTasksDashlet = ShareUserDashboard.getDashlet(drone, Dashlets.MY_TASKS).render();
        myTasksDashlet.selectTasksFilter(COMPLETED_TASKS).render();
        assertTrue(myTasksDashlet.isTaskPresent(workFlowName + 2), workFlowName + 2 + " is not found" );
        assertFalse(myTasksDashlet.isTaskEditButtonEnabled(workFlowName + 2), "Edit Task button is enabled");
        assertTrue (myTasksDashlet.isTaskViewButtonEnabled(workFlowName + 2), "View Task button is disabled");

        // Click on View Task button
        taskDetailsPage = myTasksDashlet.selectViewTask(workFlowName + 2).render();
        assertNotNull(taskDetailsPage);

        // Click on Start Workflow button
        ShareUser.openUserDashboard(drone);
        myTasksDashlet = ShareUserDashboard.getDashlet(drone, Dashlets.MY_TASKS).render();
        StartWorkFlowPage startWorkFlowPage = myTasksDashlet.selectStartWorkFlow().render();
        assertNotNull(startWorkFlowPage);

        // Click on Active task button
        ShareUser.openUserDashboard(drone);
        myTasksDashlet = ShareUserDashboard.getDashlet(drone, Dashlets.MY_TASKS).render();
        MyTasksPage myTasksPage = myTasksDashlet.selectActive().render();
        assertTrue (myTasksPage.isFilterTitle("Active Tasks"), "Active tasks page don't open");

        // Click on Complete task button
        ShareUser.openUserDashboard(drone);
        myTasksDashlet = ShareUserDashboard.getDashlet(drone, Dashlets.MY_TASKS).render();
        myTasksPage = myTasksDashlet.selectComplete();
        assertTrue (myTasksPage.isFilterTitle("Completed Tasks"), "Completed Tasks page don't open");

        // Verify Alfresco Add-Ons RSS Feed dashlet
        DashBoardPage dashBoardPage = ShareUser.openUserDashboard(drone).render();
        dashBoardPage.getNav().selectCustomizeUserDashboard().render();
        CustomiseUserDashboardPage customiseUserDashboardPage = drone.getCurrentPage().render();
        customiseUserDashboardPage.removeDashlet(Dashlets.RSS_FEED).render();
        AddOnsRssFeedDashlet rssDashlet = ShareUserDashboard.getAddOnsRssFeedDashlet(drone, "addOns-rss").render();
        assertTrue(rssDashlet.isHelpIconDisplayed(), "Help icon isn't displayed");
        rssDashlet.clickOnHelpIcon();
        assertTrue(rssDashlet.isBalloonDisplayed(), "Baloon popup isn't displayed");
        rssDashlet.closeHelpBallon();
        assertFalse(rssDashlet.isBalloonDisplayed(), "Baloon popup is displayed");
        for (int i = 0; i < 5000; i++)
        {
            if (rssDashlet.getTitle().equals("Newest Add-ons"))
            {
                break;
            }
        }
        assertTrue(rssDashlet.getTitle().equals("Newest Add-ons"));
        assertTrue(rssDashlet.getHeaderInfo().equals(headerInfo));
        assertTrue(rssDashlet.isConfigureIconDisplayed(), "Configure icon isn't available");

        // Configure Alfresco Add-Ons RSS Feed dashlet
        RssFeedUrlBoxPage rssFeedUrlBoxPage = rssDashlet.clickConfigure().render();
        rssFeedUrlBoxPage.fillURL(rssUrl);
        rssFeedUrlBoxPage.selectNrOfItemsToDisplay(RssFeedUrlBoxPage.NrItems.Five);
        rssFeedUrlBoxPage.clickOk();
        rssFeedUrlBoxPage.waitUntilCheckDisapperers();
        for (int i = 0; i < 1000; i++)
        {
           if (rssDashlet.getTitle().equals(rssTitle))
            {
                break;
            }
        }
        assertTrue(rssDashlet.getTitle().equals(rssTitle));
        List<ShareLink> links = rssDashlet.getHeadlineLinksFromDashlet();
        assertEquals(links.size(), 5);

        // Verify Saved Search dashlet
        SavedSearchDashlet savedSearchDashlet = ShareUserDashboard.getDashlet(drone, Dashlets.SAVED_SEARCH).render();
        assertEquals(savedSearchDashlet.getTitle(), Dashlets.SAVED_SEARCH.getDashletName());
        assertEquals(savedSearchDashlet.getContent(), "No results found.");
        assertTrue(savedSearchDashlet.isHelpIconDisplayed(), "Help icon isn't displayed");
        savedSearchDashlet.clickOnHelpIcon();
        assertTrue(savedSearchDashlet.isBalloonDisplayed(), "Baloon popup isn't displayed");
        savedSearchDashlet.closeHelpBallon();
        assertFalse(savedSearchDashlet.isBalloonDisplayed(), "Baloon popup is displayed");
        assertTrue(savedSearchDashlet.isConfigIconDisplayed(), "Configure icon isn't available");

        // Configure Saved Search dashlet
        ShareUserDashboard.configureSavedSearch(drone, "*" + testName + "*", searchTitle, SearchLimit.TEN);
        List<SiteSearchItem> searchResults = savedSearchDashlet.getSearchItems();
        for (SiteSearchItem result : searchResults)
        {
            assertTrue(result.getItemName().getDescription().contains(testName));
        }
        assertEquals(savedSearchDashlet.getTitle(), searchTitle);
        assertEquals(searchResults.size(), 10);

        // Verify Content I'm editing dashlet
        EditingContentDashlet editingContentDashlet = ShareUserDashboard.getDashlet(drone, Dashlets.CONTENT_I_AM_EDITING).render();
        assertEquals(editingContentDashlet.getTitle(), Dashlets.CONTENT_I_AM_EDITING.getDashletName());
        assertTrue(editingContentDashlet.isHelpIconDisplayed(), "Help icon isn't displayed");
        editingContentDashlet.clickOnHelpIcon();
        assertTrue(editingContentDashlet.isBalloonDisplayed(), "Baloon popup isn't displayed");
        editingContentDashlet.closeHelpBallon();
        assertFalse(editingContentDashlet.isBalloonDisplayed(), "Baloon popup is displayed");
        assertTrue(editingContentDashlet.isItemWithDetailDisplayed(fileName1 + 2, siteName + 2));
        assertTrue(editingContentDashlet.isItemWithDetailDisplayed(fileName1 + 1, siteName + 1));
        assertTrue(editingContentDashlet.isItemWithDetailDisplayed(siteName + 1 + "topic", siteName + 1));
        assertTrue(editingContentDashlet.isItemWithDetailDisplayed(siteName + 2 + "topic", siteName + 2));

        // Verify My Profile dashlet
        MyProfileDashlet myProfileDashlet = ShareUserDashboard.getDashlet(drone, Dashlets.MY_PROFILE).render();
        assertEquals(myProfileDashlet.getTitle(), Dashlets.MY_PROFILE.getDashletName());
        assertTrue(myProfileDashlet.isHelpIconPresent(), "Help icon isn't displayed");
        myProfileDashlet.clickOnHelpIcon();
        assertTrue(myProfileDashlet.isBalloonDisplayed(), "Baloon popup isn't displayed");
        myProfileDashlet.closeHelpBallon();
        assertFalse(myProfileDashlet.isBalloonDisplayed(), "Baloon popup is displayed");
        assertTrue(myProfileDashlet.isViewFullProfileDisplayed(), "View Full Profile is absent");
        assertTrue(myProfileDashlet.isAvatarDisplayed(), "Avatar isn't displayed");
        assertTrue(myProfileDashlet.getUserName().contains(user), "User name isn't presented");
        assertTrue(myProfileDashlet.getUserName().endsWith(DEFAULT_LASTNAME), "Last name isn't presented");
        assertEquals(myProfileDashlet.getEmailName(), user);

        // Click View Full Profile button or the user name
        MyProfilePage myProfilePage = myProfileDashlet.clickViewFullProfileButton().render();
        assertNotNull(myProfilePage);
        ShareUser.openUserDashboard(drone).render();
        myProfileDashlet = ShareUserDashboard.getDashlet(drone, Dashlets.MY_PROFILE).render();
        myProfilePage = myProfileDashlet.clickOnUserName().render();
        assertNotNull(myProfilePage);

        // Verify My Discussions dashlet
        ShareUser.openUserDashboard(drone).render();
        MyDiscussionsDashlet myDiscussionsDashlet = ShareUserDashboard.getDashlet(drone, Dashlets.MY_DISCUSSIONS).render();
        assertEquals(myDiscussionsDashlet.getTitle(), Dashlets.MY_DISCUSSIONS.getDashletName());
        assertTrue(myDiscussionsDashlet.isHelpIconDisplayed(), "Help icon isn't displayed");
        myDiscussionsDashlet.clickOnHelpIcon();
        assertTrue(myDiscussionsDashlet.isBalloonDisplayed(), "Baloon popup isn't displayed");
        myDiscussionsDashlet.closeHelpBallon();
        assertFalse(myDiscussionsDashlet.isBalloonDisplayed(), "Baloon popup is displayed");

        myDiscussionsDashlet.selectTopicsFilter(MY_TOPICS).render();
        List<ShareLink> topicsTitles = myDiscussionsDashlet.getTopics(MyDiscussionsDashlet.LinkType.Topic);
        assertEquals(topicsTitles.size(), 2, "Expected topics aren't displayed");
        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { siteName + 1 + "topic" }, true);
        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { siteName + 2 + "topic" }, true);

        myDiscussionsDashlet.selectTopicsFilter(ALL_TOPICS).render();
        topicsTitles = myDiscussionsDashlet.getTopics(MyDiscussionsDashlet.LinkType.Topic);
        assertEquals(topicsTitles.size(), 2, "Expected topics aren't displayed");
        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { siteName + 1 + "topic" }, true);
        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { siteName + 2 + "topic" }, true);

        myDiscussionsDashlet.selectTopicsHistoryFilter(LAST_DAY_TOPICS);
        topicsTitles = myDiscussionsDashlet.getTopics(MyDiscussionsDashlet.LinkType.Topic);
        assertEquals(topicsTitles.size(), 2, "Expected topics aren't displayed");
        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { siteName + 1 + "topic" }, true);
        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { siteName + 2 + "topic" }, true);

        myDiscussionsDashlet.selectTopicsHistoryFilter(SEVEN_DAYS_TOPICS);
        topicsTitles = myDiscussionsDashlet.getTopics(MyDiscussionsDashlet.LinkType.Topic);
        assertEquals(topicsTitles.size(), 2, "Expected topics aren't displayed");
        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { siteName + 1 + "topic" }, true);
        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { siteName + 2 + "topic" }, true);

        myDiscussionsDashlet.selectTopicsHistoryFilter(FOURTEEN_DAYS_TOPICS);
        topicsTitles = myDiscussionsDashlet.getTopics(MyDiscussionsDashlet.LinkType.Topic);
        assertEquals(topicsTitles.size(), 2, "Expected topics isn't displayed");
        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { siteName + 1 + "topic" }, true);
        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { siteName + 2 + "topic" }, true);

        myDiscussionsDashlet.selectTopicsHistoryFilter(TWENTY_EIGHT_DAYS_TOPICS);
        topicsTitles = myDiscussionsDashlet.getTopics(MyDiscussionsDashlet.LinkType.Topic);
        assertEquals(topicsTitles.size(), 2, "Expected topics aren't displayed");
        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { siteName + 1 + "topic" }, true);
        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { siteName + 2 + "topic" }, true);

        // Verify Site Search dashlet. Search for any item
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getDashlet(drone, Dashlets.SITE_SEARCH).render();
        assertEquals(searchDashlet.getTitle(), Dashlets.SITE_SEARCH.getDashletName());
        assertTrue(searchDashlet.isHelpIconDisplayed(), "Help icon isn't displayed");
        searchDashlet.clickOnHelpIcon();
        assertTrue(searchDashlet.isBalloonDisplayed(), "Baloon popup isn't displayed");
        searchDashlet.closeHelpBallon();
        assertFalse(searchDashlet.isBalloonDisplayed(), "Baloon popup is displayed");
        assertTrue(searchDashlet.isSearchFieldDisplayed(), "Search field is not found!");
        assertTrue(searchDashlet.isSearchButtonDisplayed(), "Search button is not found!");
        assertTrue(searchDashlet.isDropDownResultsSizeDisplayed(), "Result size drop down is not found!");
        assertEquals(searchDashlet.getAvailableResultSizes(), Arrays.asList("10", "25", "50", "100"));

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "*" + testName + "*", SearchLimit.TEN);
        for (SiteSearchItem result : items)
        {
            assertTrue(result.getItemName().getDescription().contains(testName));
        }
        assertEquals(items.size(), 10);

        // Verify My Documents dashlet
        MyDocumentsDashlet myDocuments = ShareUserDashboard.getDashlet(drone, Dashlets.MY_DOCUMENTS).render();
        assertEquals(myDocuments.getTitle(), Dashlets.MY_DOCUMENTS.getDashletName());
        assertTrue(myDocuments.isHelpIconDisplayed(), "Help icon isn't displayed");
        myDocuments.clickOnHelpIcon();
        assertTrue(myDocuments.isBalloonDisplayed(), "Baloon popup isn't displayed");
        myDocuments.closeHelpBallon();
        assertFalse(myDocuments.isBalloonDisplayed(), "Baloon popup is displayed");
        assertTrue(myDocuments.isDetailButtonDisplayed(), "Detailed View isn't displayed");
        assertTrue(myDocuments.isSimpleButtonDisplayed(), "Simple View isn't displayed");

        // Verify My Documents: I've Recently Modified
        myDocuments.selectFilter(I_HAVE_RECENTLY_MODIFIED);
        List<ShareLink> listDoc = myDocuments.getDocuments();
        assertEquals(listDoc.size(), 4, "Expected documents aren't displayed");
        List<String> modifiedDoc = new ArrayList<String>(4);
        for (ShareLink result : listDoc)
        {
            modifiedDoc.add(result.getDescription());
        }
        assertTrue(modifiedDoc.contains(fileName1 + 1), fileName1 + 1 + " is not found ");
        assertTrue(modifiedDoc.contains(fileName1 + 2), fileName1 + 2 + " is not found ");
        assertTrue(modifiedDoc.contains(fileName2 + 1), fileName2 + 2 + " is not found ");
        assertTrue(modifiedDoc.contains(fileName2 + 2), fileName2 + 2 + " is not found ");

        // Verify My Documents: I'm Editing
        myDocuments.selectFilter(I_AM_EDITING);
        listDoc = myDocuments.getDocuments();
        assertEquals(listDoc.size(), 2, "Expected documents aren't displayed");
        List<String> editedDoc = new ArrayList<String>(2);
        for (ShareLink result : listDoc)
        {
            editedDoc.add(result.getDescription());
        }
        assertTrue(editedDoc.contains(fileName1 + 1), fileName1 + 1 + " is not found ");
        assertTrue(editedDoc.contains(fileName1 + 2), fileName1 + 2 + " is not found ");

        // Verify My Documents: My Favourites
        myDocuments.selectFilter(MY_FAVOURITES);
        listDoc = myDocuments.getDocuments();
        assertEquals(listDoc.size(), 2, "Expected documents aren't displayed");
        List<String> favouritesDoc = new ArrayList<String>(2);
        for (ShareLink result : listDoc)
        {
            favouritesDoc.add(result.getDescription());
        }
        assertTrue(favouritesDoc.contains(fileName2 + 1), fileName2 + 1 + " is not found ");
        assertTrue(favouritesDoc.contains(fileName2 + 2), fileName2 + 2 + " is not found ");

        // Verify Simple View
        myDocuments.clickSimpleView();
        List<SimpleViewInformation> informations = myDocuments.getSimpleViewInformation();
        assertNotNull(informations);
        assertEquals(informations.size(), 2);

        for (SimpleViewInformation simpleViewInformation : informations)
        {
            assertTrue(simpleViewInformation.getContentStatus().contains("Created"));
            assertNotNull(simpleViewInformation.getContentDetail());
            assertNotNull(simpleViewInformation.getThumbnail());
            assertNotNull(simpleViewInformation.getUser());
        }

        // Verify Detailed View
        myDocuments.clickDetailView();
        List<DetailedViewInformation> detailedInf = myDocuments.getDetailedViewInformation();
        assertNotNull(detailedInf);
        assertEquals(detailedInf.size(), 2);

        for (DetailedViewInformation detailedViewInformation : detailedInf)
        {
            assertTrue(detailedViewInformation.getContentStatus().contains("Created"));
            assertFalse(detailedViewInformation.isPreviewDisplayed());
            assertNotNull(detailedViewInformation.getContentDetail());
            assertNotNull(detailedViewInformation.getThumbnail());
            assertNotNull(detailedViewInformation.getUser());
            assertEquals(detailedViewInformation.getDescription(), "No Description");
            assertEquals(detailedViewInformation.getVersion(), 1.0);
            assertEquals(detailedViewInformation.getLikecount(), 0);
            assertEquals(detailedViewInformation.getFileSize(), "60 bytes");
            assertNotNull(detailedViewInformation.getLike());
            assertNotNull(detailedViewInformation.getFavorite());
            assertNotNull(detailedViewInformation.getComment());
        }

        // Verify My Sites dashlet.
        MySitesDashlet mySitesDashlet = ShareUserDashboard.getDashlet(drone, Dashlets.MY_SITES).render();
        assertEquals(mySitesDashlet.getTitle(), Dashlets.MY_SITES.getDashletName());
        assertTrue(mySitesDashlet.isHelpIconDisplayed(), "Help icon isn't displayed");
        mySitesDashlet.clickOnHelpIcon();
        assertTrue(mySitesDashlet.isBalloonDisplayed(), "Baloon popup isn't displayed");
        mySitesDashlet.closeHelpBallon();
        assertFalse(mySitesDashlet.isBalloonDisplayed(), "Baloon popup is displayed");
        assertTrue(mySitesDashlet.isCreateSiteButtonDisplayed(), "Create Site button isn't displayed");

        // Create a site through My Sites dashlet.
        CreateSitePage createSitePage = mySitesDashlet.clickCreateSiteButton().render();
        SiteDashboardPage siteDashboardPage = createSitePage.createNewSite(siteName + 3).render();
        assertTrue(siteDashboardPage.isSiteTitle(siteName + 3), "Site Dashboard page for created site " + siteName + 3 + " isn't opened");

        // Verify My Sites dashlet :  Recent
        ShareUser.openUserDashboard(drone).render();
        mySitesDashlet = ShareUserDashboard.getDashlet(drone, Dashlets.MY_SITES).render();
        mySitesDashlet.selectMyFavourites(MySitesDashlet.FavouriteType.Recent).render();
        List<ShareLink> searchLinks = mySitesDashlet.getSites();
        assertEquals(searchLinks.size(), 3, "Expected sites aren't displayed");
        List<String> recentSites = new ArrayList<String>(3);
        for (ShareLink result : searchLinks)
        {
            recentSites.add(result.getDescription());
        }
        assertTrue(recentSites.contains(siteName + 1), siteName + 1 + " is not found ");
        assertTrue(recentSites.contains(siteName + 2), siteName + 2 + " is not found ");
        assertTrue(recentSites.contains(siteName + 3), siteName + 3 + " is not found ");

        // Remove site3 from favourite
        mySitesDashlet.selectFavorite(siteName + 3);
        assertFalse(mySitesDashlet.isSiteFavourite(siteName + 3), siteName + 3 + " is favourite site");

        // Verify My Sites dashlet : My Favorites
        mySitesDashlet.selectMyFavourites(MySitesDashlet.FavouriteType.MyFavorites).render();
        searchLinks = mySitesDashlet.getSites();
        assertEquals(searchLinks.size(), 2, "Expected sites aren't displayed");
        List<String> favouriteSites = new ArrayList<String>(2);
        for (ShareLink result : searchLinks)
        {
            favouriteSites.add(result.getDescription());
        }
        assertTrue(favouriteSites.contains(siteName + 1), siteName + 1 + " is not found ");
        assertTrue(favouriteSites.contains(siteName + 2), siteName + 2 + " is not found ");

        // Verify My Sites dashlet : All sites
        mySitesDashlet.selectMyFavourites(MySitesDashlet.FavouriteType.ALL).render();
        searchLinks = mySitesDashlet.getSites();
        assertEquals(searchLinks.size(), 3, "Expected sites aren't displayed");
        List<String> allSites = new ArrayList<String>(3);
        for (ShareLink result : searchLinks)
        {
            allSites.add(result.getDescription());
        }
        assertTrue(allSites.contains(siteName + 1), siteName + 1 + " is not found ");
        assertTrue(allSites.contains(siteName + 2), siteName + 2 + " is not found ");
        assertTrue(allSites.contains(siteName + 3), siteName + 2 + " is not found ");

        // Mark site3 as favourite
        mySitesDashlet.selectFavorite(siteName + 3);
        assertTrue(mySitesDashlet.isSiteFavourite(siteName + 3), siteName + 3 + " isn't favourite site");

        // Verify My Sites dashlet : My Favorites
        mySitesDashlet.selectMyFavourites(MySitesDashlet.FavouriteType.MyFavorites).render();
        assertTrue(mySitesDashlet.isSitePresent(siteName + 3), siteName + 3 + " is not found ");

        // Delete site through My Sites dashlet
        mySitesDashlet.deleteSite(siteName + 3).render();
        ShareUser.openUserDashboard(drone).render();
        mySitesDashlet = ShareUserDashboard.getDashlet(drone, Dashlets.MY_SITES).render();

        // Verify My Sites dashlet : All sites
        mySitesDashlet.selectMyFavourites(MySitesDashlet.FavouriteType.ALL).render();
        assertFalse(mySitesDashlet.isSitePresent(siteName + 3), siteName + 3 + " is found ");

        // Verify My Calendar
        MyCalendarDashlet myCalendarDashlet = ShareUserDashboard.getDashlet(drone, Dashlets.MY_CALENDAR).render();
        assertEquals(myCalendarDashlet.getTitle(), Dashlets.MY_CALENDAR.getDashletName());
        assertTrue(myCalendarDashlet.isHelpIconDisplayed(), "Help icon isn't displayed");
        myCalendarDashlet.clickOnHelpIcon();
        assertTrue(myCalendarDashlet.isBalloonDisplayed(), "Baloon popup isn't displayed");
        myCalendarDashlet.closeHelpBallon();
        assertFalse(myCalendarDashlet.isBalloonDisplayed(), "Baloon popup is displayed");
        assertEquals(myCalendarDashlet.getEventsCount(), 6, "Wrong events count in Calendar Dashlet Displayed.");
        assertFalse(myCalendarDashlet.isEventsDisplayed(siteName + 1 + event1 + "passed"), "The passed event is displayed in dashlet");
        assertFalse(myCalendarDashlet.isEventsDisplayed(siteName + 2 + event1 + "passed"), "The passed event is displayed in dashlet");
        assertTrue(myCalendarDashlet.isEventsDisplayed(siteName + 1 + event2), siteName + 1 + event2 + "  event didn't displayed in dashlet.");
        assertTrue(myCalendarDashlet.isEventsDisplayed(siteName + 2 + event2), siteName + 2 + event2 + " event didn't displayed in dashlet.");
        assertTrue(myCalendarDashlet.isEventsDisplayed(siteName + 1 + event1), siteName + 1 + event1 + " event didn't displayed in dashlet.");
        assertTrue(myCalendarDashlet.isEventsDisplayed(siteName + 2 + event1), siteName + 2 + event1 + " event didn't displayed in dashlet.");
        assertTrue(myCalendarDashlet.isEventsDisplayed(siteName + 1 + event3), siteName + 1 + event1 + " event didn't displayed in dashlet.");
        assertTrue(myCalendarDashlet.isEventsDisplayed(siteName + 2 + event3), siteName + 2 + event1 + " event didn't displayed in dashlet.");

        // Verify RSS Feed.
        dashBoardPage = ShareUser.openUserDashboard(drone).render();
        dashBoardPage.getNav().selectCustomizeUserDashboard().render();
        customiseUserDashboardPage = drone.getCurrentPage().render();
        dashBoardPage = customiseUserDashboardPage.removeDashlet(Dashlets.ALFRESCO_ADDONS_RSS_FEED).render();
        dashBoardPage.getNav().selectCustomizeUserDashboard().render();
        customiseUserDashboardPage = drone.getCurrentPage().render();
        customiseUserDashboardPage.addDashlet(Dashlets.RSS_FEED, 2).render();
        RssFeedDashlet rssFeedDashlet = ShareUserDashboard.getDashlet(drone, Dashlets.RSS_FEED).render();
        for (int i = 0; i < 1000; i++)
        {
            if (!"Rss Feed".equals(rssFeedDashlet.getTitle()))
            {
                break;
            }
        }
        String defaultTitle = rssFeedDashlet.getTitle();
        assertEquals(defaultTitle, "Alfresco Blog", "Rss dashlet doesn't show Alfresco rss by default.");
        assertTrue(rssFeedDashlet.isHelpIconDisplayed(), "Help icon isn't displayed");
        rssFeedDashlet.clickOnHelpIcon();
        assertTrue(rssFeedDashlet.isBalloonDisplayed(), "Baloon popup isn't displayed");
        rssFeedDashlet.closeHelpBallon();
        assertFalse(rssFeedDashlet.isBalloonDisplayed(), "Baloon popup is displayed");
        assertTrue(rssFeedDashlet.isConfigureIconDisplayed(), "Configure icon isn't available");

        // Configure RSS Feed dashlet
        rssFeedUrlBoxPage = rssFeedDashlet.clickConfigure();
        rssFeedUrlBoxPage.fillURL(rssUrl);
        rssFeedUrlBoxPage.selectNrOfItemsToDisplay(RssFeedUrlBoxPage.NrItems.Five);
        rssFeedUrlBoxPage.clickOk();
        rssFeedUrlBoxPage.waitUntilCheckDisapperers();
        for (int i = 0; i < 1000; i++)
        {
            if (rssFeedDashlet.getTitle().equals(rssTitle))
            {
                break;
            }
        }
        assertTrue(rssFeedDashlet.getTitle().equals(rssTitle), "Rss Feed don't reflect are changes.");
        links = rssFeedDashlet.getHeadlineLinksFromDashlet();
        assertEquals(links.size(), 5);

        // Verify Web View
        WebViewDashlet webViewDashlet = ShareUserDashboard.getDashlet(drone, Dashlets.WEB_VIEW).render();
        assertEquals(webViewDashlet.getTitle(), Dashlets.WEB_VIEW.getDashletName());
        assertTrue(webViewDashlet.isHelpIconDisplayed(), "Help icon isn't displayed");
        webViewDashlet.clickOnHelpIcon();
        assertTrue(webViewDashlet.isBalloonDisplayed(), "Baloon popup isn't displayed");
        webViewDashlet.closeHelpBallon();
        assertFalse(webViewDashlet.isBalloonDisplayed(), "Baloon popup is displayed");
        assertTrue(webViewDashlet.isConfigureIconDisplayed(), "Configure icon isn't available");
        assertEquals(webViewDashlet.getDefaultMessage(), "No web page to display.");

        // Configure Web View
        ConfigureWebViewDashletBoxPage configureWebViewDashletBoxPage = webViewDashlet.clickConfigure();
        assertNotNull(configureWebViewDashletBoxPage);
        configureWebViewDashletBoxPage.config(extSiteUrl, extSiteUrl);
        assertEquals(webViewDashlet.getWebViewDashletTitle(), extSiteUrl);
        assertTrue(webViewDashlet.isFrameShow(extSiteUrl), extSiteUrl + " isn't displayed");

    }

    /**
     * AONE-8280:People finder
     */

    @Test(groups = "Sanity", timeOut = 600000)
    public void AONE_8280() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "_1");
        String user2 = getUserNameFreeDomain(testName + "_2");
        String[] userInfo1 = new String[] { user1 };
        String[] userInfo2 = new String[] { user2 };
        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName);
        String[] fileInfo = new String[] { fileName, DOCLIB };
        String folderName = getFolderName(testName);
        String activityEntry;

        // Create 2 Users
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo2);

        // User1 login
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Navigate to People Finder
        DashBoardPage dashBoard = ShareUser.openUserDashboard(drone).render();
        PeopleFinderPage peopleFinderPage = dashBoard.getNav().selectPeople().render();

        // Search yourself
        // Click on user's name link
        peopleFinderPage = peopleFinderPage.searchFor(user1).render();
        List<ShareLink> searchLinks = peopleFinderPage.getResults();
        if (!searchLinks.isEmpty())
        {
            for (ShareLink result : searchLinks)
            {
                if (result.getDescription().contains(user1))
                {
                    result.click();
                }
            }
        }
        else
        {
            fail(user1 + " is not found");
        }

        MyProfilePage myProfilePage = drone.getCurrentPage().render();
        assertNotNull(myProfilePage);

        // Search user2
        // Click Follow button for the user2
        dashBoard = ShareUser.openUserDashboard(drone).render();
        peopleFinderPage = dashBoard.getNav().selectPeople().render();
        peopleFinderPage = peopleFinderPage.searchFor(user2).render();
        searchLinks = peopleFinderPage.getResults();
        if (!searchLinks.isEmpty())
        {
            for (ShareLink result : searchLinks)
            {
                if (result.getDescription().contains(user2))
                {
                    peopleFinderPage.selectFollowForUser(user2);
                }
            }
        }
        else
        {
            fail(user2 + " is not found");
        }
        assertEquals(peopleFinderPage.getTextForFollowButton(user2), "Unfollow");

        // Verify I'm Following link on My Profile
        dashBoard = ShareUser.openUserDashboard(drone).render();
        myProfilePage = dashBoard.getNav().selectMyProfile().render();
        FollowingPage followingPage = myProfilePage.getProfileNav().selectFollowing().render();
        assertEquals(followingPage.getFollowingCount(), "1");
        assertTrue(followingPage.isUserLinkPresent(user2), "Can't find " + user2);
        ShareUser.logout(drone);

        // User2 login
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);

        // Site creating
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Upload any document
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        // Create any folder
        ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB_CONTAINER).render();

        // Navigate to People Finder
        dashBoard = ShareUser.openUserDashboard(drone).render();
        peopleFinderPage = dashBoard.getNav().selectPeople().render();

        // Search user1
        // Click Follow button for the user1
        peopleFinderPage = peopleFinderPage.searchFor(user1).render();
        searchLinks = peopleFinderPage.getResults();
        if (!searchLinks.isEmpty())
        {
            for (ShareLink result : searchLinks)
            {
                if (result.getDescription().contains(user1))
                {
                    peopleFinderPage.selectFollowForUser(user1);
                }
            }
        }
        else
        {
            fail(user2 + " is not found");
        }
        assertEquals(peopleFinderPage.getTextForFollowButton(user1), "Unfollow");
        ShareUser.logout(drone);

        // User1 login
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Verify My Activities dashlet ->I'm following filter
        MyActivitiesDashlet myActivitiesDashlet = ShareUserDashboard.getDashlet(drone, Dashlets.MY_ACTIVITIES).render();
        myActivitiesDashlet.selectOptionFromUserActivities("I\'m following").render();
        assertTrue(myActivitiesDashlet.isOptionSelected("I\'m following"));
        activityEntry = String.format("%s %s added document %s in %s", user2, DEFAULT_LASTNAME, fileName, siteName);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true), "Activity: " + activityEntry + " is not displayed");
        activityEntry = String.format("%s %s added folder %s in %s", user2, DEFAULT_LASTNAME, folderName, siteName);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true), "Activity: " + activityEntry + " is not displayed");
        activityEntry = String.format("%s %s is now following %s %s", user2, DEFAULT_LASTNAME, user1, DEFAULT_LASTNAME);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true), "Activity: " + activityEntry + " is not displayed");

        // Verify Following Me link on My Profile page
        dashBoard = ShareUser.openUserDashboard(drone).render();
        myProfilePage = dashBoard.getNav().selectMyProfile().render();
        FollowersPage followersPage = myProfilePage.getProfileNav().selectFollowers().render();
        assertEquals(followersPage.getFollowersCount(), "1");
        assertTrue(followersPage.isUserLinkPresent(user2), "Can't find " + user2);
    }

    /**
     * AONE-8281:My Profile
     */

    @Test(groups = "DataPrepSanity", timeOut = 600000)
    public void dataPrep_AONE_8281() throws Exception
    {
        String testName = getTestName();
        String user1 = MailUtil.BASE_BOT_MAIL;
        String user2 = getUserNameFreeDomain(testName + "_2");
        String[] userInfo1 = new String[] { user1 };
        String[] userInfo2 = new String[] { user2 };
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName);


        //Config email
        MailUtil.configOutBoundEmail();

        // Create two Users
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo2);

        // User login
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Two sites are created
        for (int i = 1; i < 3; i++)
        {
            ShareUser.createSite(drone, siteName + i, SITE_VISIBILITY_PUBLIC);
        }

        // The user adds several contents
        ShareUser.openSitesDocumentLibrary(drone, siteName + 1).render();
        for (int i = 1; i < 8; i++)
        {
            ShareUser.uploadFileInFolder(drone, new String[] { fileName + i, DOCLIB }).render();
        }
        ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB_CONTAINER).render();

        // Delete several contents
        for (int i = 1; i < 7; i++)
        {
            ShareUser.selectContentCheckBox(drone, fileName + i);
            ShareUser.deleteSelectedContent(drone);
        }

        // Delete folder
        ShareUser.selectContentCheckBox(drone, folderName);
        ShareUser.deleteSelectedContent(drone);

        // Navigate to People Finder
        DashBoardPage dashBoard = ShareUser.openUserDashboard(drone).render();
        PeopleFinderPage peopleFinderPage = dashBoard.getNav().selectPeople().render();

        // Search user2
        // Click Follow button for the user2
        peopleFinderPage = peopleFinderPage.searchFor(user2).render();
        List<ShareLink> searchLinks = peopleFinderPage.getResults();
        if (!searchLinks.isEmpty())
        {
            for (ShareLink result : searchLinks)
            {
                if (result.getDescription().contains(user2))
                {
                    peopleFinderPage.selectFollowForUser(user2);
                }
            }
        }
        else
        {
            fail(user2 + " is not found");
        }
        assertEquals(peopleFinderPage.getTextForFollowButton(user2), "Unfollow");
        ShareUser.logout(drone);

        // User2 login
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);

        // Navigate to People Finder
        dashBoard = ShareUser.openUserDashboard(drone).render();
        peopleFinderPage = dashBoard.getNav().selectPeople().render();

        // Search user1
        // Click Follow button for the user1
        peopleFinderPage = peopleFinderPage.searchFor(user1).render();
        searchLinks = peopleFinderPage.getResults();
        if (!searchLinks.isEmpty())
        {
            for (ShareLink result : searchLinks)
            {
                if (result.getDescription().contains(user1))
                {
                    peopleFinderPage.selectFollowForUser(user1);
                }
            }
        }
        else
        {
            fail(user2 + " is not found");
        }
        assertEquals(peopleFinderPage.getTextForFollowButton(user1), "Unfollow");
    }

    @Test(groups = "Sanity", timeOut = 600000)
    public void AONE_8281() throws Exception
    {
        String testName = getTestName();
        String user1 = MailUtil.BASE_BOT_MAIL;
        String user2 = getUserNameFreeDomain(testName + "_2");
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName);
        String newPassword = DEFAULT_PASSWORD + "123";
        String modifiedLName = "edited";
        String errorNotification = "Your authentication details have not been recognized";

        // User1 login
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Navigate to My Profile
        DashBoardPage dashBoard = ShareUser.openUserDashboard(drone).render();
        MyProfilePage myProfilePage = dashBoard.getNav().selectMyProfile().render();
        assertTrue(myProfilePage.titlePresent(), "My profile page isn't displayed");
        assertTrue(myProfilePage.getUserName().contains(user1), "User name isn't presented");
        assertTrue(myProfilePage.getUserName().endsWith(DEFAULT_LASTNAME), "Last name isn't presented");
        assertEquals(myProfilePage.getEmailName(), user1);

        // Edit the Profile
        EditProfilePage editProfilePage = myProfilePage.openEditProfilePage();
        myProfilePage = editProfilePage.editLastName("edited");
        assertTrue(myProfilePage.getUserName().endsWith("edited"), "New last name isn't displayed");

        // Verify Content on My Profile
        UserContentPage userContentPage = myProfilePage.getProfileNav().selectContent();
        List<UserContentItems> userContentItems = userContentPage.getContentAdded();
        List<String> addedItems = new ArrayList<String>(1);
        for (UserContentItems result : userContentItems)
        {
            addedItems.add(result.getContentName());
        }
        assertTrue(addedItems.contains(fileName + 7), fileName + 7 + " is not found ");

        List<UserContentItems> userModifiedItems = userContentPage.getContentModified();
        List<String> modifiedItems = new ArrayList<String>(1);
        for (UserContentItems result : userModifiedItems)
        {
            modifiedItems.add(result.getContentName());
        }
        assertTrue(modifiedItems.contains(fileName + 7), fileName + 7 + " is not found ");

        // Verify Sites on My Profile
        UserSitesPage userSitesPage = userContentPage.getProfileNav().selectSites().render();
        List<UserSiteItem> userSiteItems = userSitesPage.getSites();
        List<String> siteNames = new ArrayList<String>(2);
        for (UserSiteItem result : userSiteItems)
        {
            siteNames.add(result.getSiteName());
        }
        assertTrue(siteNames.contains(siteName + 1), siteName + 1 + " is not found ");
        assertTrue(siteNames.contains(siteName + 2), siteName + 2 + " is not found ");
        UserSiteItem userSiteItem = userSitesPage.getSite(siteName + 1);
        assertTrue(userSiteItem.getActivityFeedButtonLabel().contains("Disable Activity Feeds"), "Disable Activity Feeds is absent");
        userSiteItem = userSitesPage.getSite(siteName + 2);
        assertTrue(userSiteItem.getActivityFeedButtonLabel().contains("Disable Activity Feeds"), "Disable Activity Feeds is absent");

        // Disable Activity Feeds for site2
        userSiteItem.toggleActivityFeed(false).render();
        assertFalse(userSiteItem.isActivityFeedEnabled(), "Activity Feeds is enabled");

        // Make some activity in site2
        ShareUser.openSitesDocumentLibrary(drone, siteName + 2);
        ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB }).render();
        DetailsPage detailsPage = ShareUserSitePage.getContentDetailsPage(drone, fileName);
        detailsPage.selectLike();

        // Verify Site Activities Dashlet
        ShareUser.openSiteDashboard(drone, siteName + 2);
        String activity1 = String.format("%s %s added document %s", user1, modifiedLName, fileName);
        String activity2 = String.format("%s %s liked document %s", user1, modifiedLName, fileName);

        assertFalse(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity1, true, siteName + 2, ActivityType.DESCRIPTION),
            "Activity should be disabled for site. Found: " + activity1);
        assertFalse(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity2, true, siteName + 2, ActivityType.DESCRIPTION),
            "Activity should be disabled for site. Found: " + activity2);

        // Verify the mail
        Thread.sleep(10000); //solr wait
        JmxUtils.invokeAlfrescoServerProperty("Alfresco:Name=Schedule,Group=DEFAULT,Type=MonitoredCronTrigger,Trigger=feedNotifierTrigger", "executeNow");
        assertFalse(MailUtil.isMailPresent(user1, "Alfresco Share: Recent Activities"), "User get mail about Recent Activities.");

        // Verify Notifications on My Profile.
        // Uncheck Email Notification Feed
        dashBoard = ShareUser.openUserDashboard(drone).render();
        myProfilePage = dashBoard.getNav().selectMyProfile().render();
        NotificationPage notificationPage = myProfilePage.getProfileNav().selectNotification().render();
        notificationPage.toggleNotificationFeed(false);
        myProfilePage = notificationPage.selectOk().render();
        notificationPage = myProfilePage.getProfileNav().selectNotification().render();
        assertFalse(notificationPage.isNotificationFeedChecked(), "Notification Feed emails is enabled.");

        // Make some activity in site1
        ShareUser.openSitesDocumentLibrary(drone, siteName + 1);
        ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB }).render();
        detailsPage = ShareUserSitePage.getContentDetailsPage(drone, fileName);
        detailsPage.selectLike();

        // Verify Site Activities Dashlet
        ShareUser.openSiteDashboard(drone, siteName + 1);
        activity1 = String.format("%s %s added document %s", user1, modifiedLName, fileName);
        activity2 = String.format("%s %s liked document %s", user1, modifiedLName, fileName);

        assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity1, true, siteName + 1, ActivityType.DESCRIPTION),
            "Activity is disabled for site");
        assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity2, true, siteName + 1, ActivityType.DESCRIPTION),
            "Activity is disabled for site");

        // Verify the mail
        Thread.sleep(10000); //solr wait
        JmxUtils.invokeAlfrescoServerProperty("Alfresco:Name=Schedule,Group=DEFAULT,Type=MonitoredCronTrigger,Trigger=feedNotifierTrigger", "executeNow");
        assertFalse(MailUtil.isMailPresent(user1, "Alfresco Share: Recent Activities"), "User get mail about Recent Activities.");

        // Check Email Notifications Feed.
        dashBoard = ShareUser.openUserDashboard(drone).render();
        myProfilePage = dashBoard.getNav().selectMyProfile().render();
        notificationPage = myProfilePage.getProfileNav().selectNotification().render();
        notificationPage.toggleNotificationFeed(true);
        myProfilePage = notificationPage.selectOk().render();
        notificationPage = myProfilePage.getProfileNav().selectNotification().render();
        assertTrue(notificationPage.isNotificationFeedChecked(), "Notification Feed emails is disabled");

        // Make some activity in site1
        ShareUser.openSitesDocumentLibrary(drone, siteName + 1);
        detailsPage = ShareUserSitePage.getContentDetailsPage(drone, fileName);
        detailsPage.addComment("Comment 1");
        detailsPage.editComment("Comment 1", "Updated comment 1");
        detailsPage.saveEditComments();

        // Verify Site Activities Dashlet
        ShareUser.openSiteDashboard(drone, siteName + 1);
        activity1 = String.format("%s %s commented on %s", user1, modifiedLName, fileName);
        activity2 = String.format("%s %s updated comment on %s", user1, modifiedLName, fileName);

        assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity1, true, siteName + 1, ActivityType.DESCRIPTION),
            "Activity is disabled for site");
        assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity2, true, siteName + 1, ActivityType.DESCRIPTION),
            "Activity is disabled for site");

        // Verify the mail
        Thread.sleep(10000); //solr wait
        JmxUtils.invokeAlfrescoServerProperty("Alfresco:Name=Schedule,Group=DEFAULT,Type=MonitoredCronTrigger,Trigger=feedNotifierTrigger", "executeNow");

        String emailMsg = MailUtil.getMailAsString(user1, "Alfresco Share: Recent Activities");
        if (emailMsg != null && !emailMsg.isEmpty())
        {
            emailMsg = Jsoup.parse(emailMsg).text();
            assertTrue(emailMsg.contains(activity2), "Could not find activity in mail: " + activity2);
            assertTrue(emailMsg.contains(activity1), "Could not find activity in mail: " + activity1);
        }
        else
        {
            fail("User[" + user1 + "] don't got a mail about Recent Activites.");
        }

        // Verify I'm Following on My Profile
        dashBoard = ShareUser.openUserDashboard(drone).render();
        myProfilePage = dashBoard.getNav().selectMyProfile().render();
        FollowingPage followingPage = myProfilePage.getProfileNav().selectFollowing().render();
        assertEquals(followingPage.getFollowingCount(), "1");
        assertTrue(followingPage.isUserLinkPresent(user2), "Can't find " + user2);

        // Verify Private checkbox
        followingPage.togglePrivate(true);
        ShareUser.logout(drone);

        // User2 login
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);

        // Navigate to People Finder
        dashBoard = ShareUser.openUserDashboard(drone).render();
        PeopleFinderPage peopleFinderPage = dashBoard.getNav().selectPeople().render();

        // Search user1
        // Click on user's name link
        // Verify Following
        peopleFinderPage = peopleFinderPage.searchFor(user1).render();
        List<ShareLink> searchLinks = peopleFinderPage.getResults();
        if (!searchLinks.isEmpty())
        {
            for (ShareLink result : searchLinks)
            {
                if (result.getDescription().contains(user1))
                {
                    result.click();
                }
            }
        }
        else
        {
            fail(user1 + " is not found");
        }

        MyProfilePage userProfile = drone.getCurrentPage().render();
        assertFalse(userProfile.isFollowingLinkDisplayed(), "Following Link is displayed");
        ShareUser.logout(drone);

        // User1 login
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Navigate to My Profile
        dashBoard = ShareUser.openUserDashboard(drone).render();
        myProfilePage = dashBoard.getNav().selectMyProfile().render();

        // Open I'm Following on My Profile
        // Click Unfollow for user2
        followingPage = myProfilePage.getProfileNav().selectFollowing().render();
        followingPage.selectUnfollowForUser(user2);
        assertTrue(followingPage.isNotFollowingMessagePresent(), "Not Following message isn't displayed");
        assertEquals(followingPage.getFollowingCount(), "0");

        // Verify Trashcan on My Profile
        // The deleted by user items are displayed
        dashBoard = ShareUser.openUserDashboard(drone).render();
        myProfilePage = dashBoard.getNav().selectMyProfile().render();
        TrashCanPage trashCanPage = myProfilePage.getProfileNav().selectTrashCan();
        List<String> nameOfItems = ShareUserProfile.getTrashCanItems(drone, "");
        assertTrue(nameOfItems.size() > 0, "A trashcan is empty");

        // Recover any item
        ShareUserProfile.recoverTrashCanItem(drone, fileName + 1);

        // Verify that the recovered item is not presented
        assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, fileName + 1), fileName + 1 + " is presented in Trashcan");

        // Delete any item
        ShareUserProfile.deleteTrashCanItem(drone, fileName + 2);

        // Verify that the deleted item is not presented
        assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, fileName + 2), fileName + 2 + "  is presented in Trashcan");

        // Search for any item in Trashcan
        trashCanPage = trashCanPage.itemSearch(fileName + 3).render();
        List<TrashCanItem> trashCanItem = trashCanPage.getTrashCanItems();
        assertFalse(trashCanItem.isEmpty(), "A trashcan is empty");
        for (TrashCanItem searchTerm : trashCanItem)
        {
            assertTrue(searchTerm.getFileName().contains(fileName + 3), fileName + 3 + " is not found");
        }

        // Click Clear button in Trashcan
        trashCanPage = trashCanPage.clearSearch().render();
        trashCanItem = trashCanPage.getTrashCanItems();
        assertTrue(ShareUserProfile.getInputText(drone).isEmpty(), "Search field isn't cleared");
        assertTrue(trashCanItem.size() > 1, "Search isn't cleared");

        // Select several items and recover them via Selected Items
        TrashCanItem list1 = ShareUserProfile.getTrashCanItem(drone, fileName + 6);
        TrashCanItem list2 = ShareUserProfile.getTrashCanItem(drone, folderName);
        list1.selectTrashCanItemCheckBox();
        list2.selectTrashCanItemCheckBox();
        TrashCanRecoverConfirmDialog trashCanRecoverConfirmation = trashCanPage.selectedRecover().render();
        assertEquals(trashCanRecoverConfirmation.getNotificationMessage(), "Successfully recovered 2 item(s), 0 failed.");
        trashCanPage = trashCanRecoverConfirmation.clickRecoverOK().render();
        nameOfItems = ShareUserProfile.getTrashCanItems(drone, "");
        assertFalse(nameOfItems.contains(fileName + 6), fileName + 6 + " is presented in Trashcan");
        assertFalse(nameOfItems.contains(folderName), folderName + " is presented in Trashcan");

        // Select several items and delete them via Selected Items
        TrashCanItem list3 = ShareUserProfile.getTrashCanItem(drone, fileName + 4);
        TrashCanItem list4 = ShareUserProfile.getTrashCanItem(drone, fileName + 5);
        list3.selectTrashCanItemCheckBox();
        list4.selectTrashCanItemCheckBox();
        TrashCanDeleteConfirmationPage trashCanConfirmationDeleteDialog = trashCanPage.selectedDelete().render();
        assertTrue(trashCanConfirmationDeleteDialog.isConfirmationDialogDisplayed(), "Confirmation Dialog isn't displayed");
        assertEquals(trashCanConfirmationDeleteDialog.getNotificationMessage(), "This will permanently delete the item(s). Are you sure?");
        trashCanConfirmationDeleteDialog.clickOkButton();
        assertEquals(trashCanConfirmationDeleteDialog.getNotificationMessage(), "Successfully deleted 2 item(s), 0 failed.");
        trashCanConfirmationDeleteDialog.clickOkButton();
        trashCanPage = getDrone().getCurrentPage().render();
        nameOfItems = ShareUserProfile.getTrashCanItems(drone, "");
        assertFalse(nameOfItems.contains(fileName + 4), fileName + 4 + " is presented in Trashcan");
        assertFalse(nameOfItems.contains(fileName + 5), fileName + 5 + " is presented in Trashcan");

        // Empty TrashCan
        TrashCanEmptyConfirmationPage trashCanEmptyConfirmationPage = trashCanPage.selectEmpty();
        assertTrue(trashCanConfirmationDeleteDialog.isConfirmationDialogDisplayed(), "Confirmation Dialog isn't displayed");
        trashCanPage = trashCanEmptyConfirmationPage.clickOkButton();
        assertFalse(trashCanPage.hasTrashCanItems(), "A trashcan isn't empty");
        assertTrue(trashCanPage.checkNoItemsMessage(), "No items exist message isn't displayed");

        // Verify Change Password on My Profile
        // Change the password.
        ChangePasswordPage changePasswordPage = trashCanPage.getProfileNav().selectChangePassword().render();
        changePasswordPage.changePassword(DEFAULT_PASSWORD, newPassword);
        ShareUser.logout(drone);

        // User1 login with old password
        SharePage resultPage = login(drone, user1, DEFAULT_PASSWORD).render();
        assertFalse(resultPage.isLoggedIn(), user1 + " can login with old password");

        //  Check Page titles
        resultPage = drone.getCurrentPage().render();
        assertTrue(resultPage.isBrowserTitle(PAGE_TITLE_LOGIN), "Login page isn't displayed");
        assertFalse(resultPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD), "User Dashboard is displayed");

        // Check Error Message
        LoginPage loginPage = (LoginPage) resultPage.render();
        assertTrue(loginPage.hasErrorMessage(), "Error message isn't displayed");
        logger.info(loginPage.getErrorMessage());
        assertTrue(loginPage.getErrorMessage().contains(errorNotification), errorNotification + "isn't presented");

        // User1 login with new password
        resultPage = ShareUser.login(drone, user1, newPassword).render();
        assertTrue(resultPage.isLoggedIn(), user1 + " can't login with new password");
        assertTrue(resultPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD), "User Dashboard isn't displayed");
    }

    private SharePage login(WebDrone drone, String userName, String userPassword)
    {
        SharePage resultPage = null;

        try
        {
            resultPage = ShareUser.login(drone, userName, userPassword);
        }
        catch (SkipException se)
        {
            resultPage = ShareUser.getSharePage(drone);
        }
        return resultPage;
    }

}
