package org.alfresco.share.calendar.timezone;

/**
 * Tests for Calendar->TimeZone->SiteDashboard
 * 
 * @author Corina.Nechifor
 */

import java.util.Map;

import org.alfresco.application.windows.MicorsoftOffice2010;
import org.alfresco.po.share.dashlet.SiteCalendarDashlet;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.CalendarUtil;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.utilities.Application;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.cobra.ldtp.Ldtp;

@Listeners(FailedTestListener.class)
public class SiteDashboardTests extends AbstractUtils
{

    private static final Logger logger = Logger.getLogger(SiteDashboardTests.class);
    private String testName;
    private String testUser;
    private String siteName;

    private String defaultTZ = "London";
    private String newTZ = "Bucharest";

    MicorsoftOffice2010 outlook = new MicorsoftOffice2010(Application.OUTLOOK, "2010");
    private String sharePointPath;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();

        testName = this.getClass().getSimpleName();
        testUser = getUserNameFreeDomain(testName);
        siteName = getSiteName(testName);

        logger.info("Start Tests in: " + testName);

        Runtime.getRuntime().exec("taskkill /F /IM OUTLOOK.EXE");
        sharePointPath = outlook.getSharePointPath();

        CalendarUtil.changeTimeZone(defaultTZ);
    }

    @AfterMethod(alwaysRun = true)
    public void teardownMEthod() throws Exception
    {
        CalendarUtil.changeTimeZone(defaultTZ);
        Runtime.getRuntime().exec("taskkill /F /IM OUTLOOK.EXE");
        Runtime.getRuntime().exec("taskkill /F /IM CobraWinLDTP.EXE");
    }

    @Test(groups = { "DataPrepCalendar" })
    public void dataPrep_AONE_SiteDashboard() throws Exception
    {

        // Create normal User
        String[] testUser2 = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser2);

        // login with user
        ShareUser.login(drone, testUser);

        // Create public site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Add Calendar dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlets.SITE_CALENDAR);

        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.CALENDER);

    }

    @Test(groups = { "Calendar", "EnterpriseOnly" })
    public void AONE_663() throws Exception
    {
        boolean allDay = false;

        ShareUser.login(drone, testUser);

        // Calendar is opened
        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Calendar
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();

        // get startTime and endTime
        Map<String, String> timeValues = CalendarUtil.setTimeForSingleDay("3:40 AM", "7:30 AM", allDay);
        String expectedStartDate = timeValues.get("startDateValue");
        String expectedEndDate = timeValues.get("endDateValue");

        // set event name
        String event1 = "single_day_event_" + System.currentTimeMillis();

        // Create any single day event, e.g. event1
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.DAY_TAB, event1, event1, event1, timeValues.get("startYear"),
                timeValues.get("startMonth"), timeValues.get("startDay"), timeValues.get("startTime"), timeValues.get("endYear"), timeValues.get("endMonth"),
                timeValues.get("endDay"), timeValues.get("endTime"), null, allDay);

        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        SiteCalendarDashlet siteCalendarDashlet = siteDashBoard.getDashlet("site-calendar").render();

        Assert.assertTrue(siteCalendarDashlet.isEventsDisplayed(event1), "The " + event1 + " isn't correctly displayed on calendar");

        String eventDetail = timeValues.get("startTime") + " - " + timeValues.get("endTime") + " " + event1;
        Assert.assertTrue(siteCalendarDashlet.isEventsWithDetailDisplayed(eventDetail), "The " + eventDetail + " isn't correctly displayed on calendar");

        ShareUser.logout(drone);

        CalendarUtil.changeTimeZone(newTZ);

        ShareUser.login(drone, testUser);

        // Calendar is opened
        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Site Dashboard - Calendar Dashlet
        siteCalendarDashlet = siteDashBoard.getDashlet("site-calendar").render();

        expectedStartDate = CalendarUtil.convertDateToNewTimeZone(expectedStartDate, defaultTZ, newTZ, allDay);
        expectedEndDate = CalendarUtil.convertDateToNewTimeZone(expectedEndDate, defaultTZ, newTZ, allDay);

        String convertedStartTime = CalendarUtil.getTimeFromDate(expectedStartDate);
        String convertedEndTime = CalendarUtil.getTimeFromDate(expectedEndDate);
        eventDetail = convertedStartTime + " - " + convertedEndTime + " " + event1;
        Assert.assertTrue(siteCalendarDashlet.isEventsWithDetailDisplayed(eventDetail), "The " + eventDetail + " isn't correctly displayed on calendar");
    }

    @Test(groups = { "Calendar", "EnterpriseOnly" })
    public void AONE_664() throws Exception
    {
        boolean allDay = true;

        ShareUser.login(drone, testUser);

        // Calendar is opened
        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Calendar
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();

        // get startTime and endTime
        Map<String, String> timeValues = CalendarUtil.setTimeForSingleDay("3:40 AM", "7:30 AM", allDay);

        // set event name
        String event1 = "single_day_allDay_event_" + System.currentTimeMillis();

        // Create any single day event, e.g. event1
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.DAY_TAB, event1, event1, event1, timeValues.get("startYear"),
                timeValues.get("startMonth"), timeValues.get("startDay"), timeValues.get("startTime"), timeValues.get("endYear"), timeValues.get("endMonth"),
                timeValues.get("endDay"), timeValues.get("endTime"), null, allDay);

        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        SiteCalendarDashlet siteCalendarDashlet = siteDashBoard.getDashlet("site-calendar").render();

        Assert.assertTrue(siteCalendarDashlet.isEventsDisplayed(event1), "The " + event1 + " isn't correctly displayed on calendar");

        ShareUser.logout(drone);

        CalendarUtil.changeTimeZone(newTZ);

        ShareUser.login(drone, testUser);

        // Calendar is opened
        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Site Dashboard - Calendar Dashlet
        siteCalendarDashlet = siteDashBoard.getDashlet("site-calendar").render();

        Assert.assertTrue(siteCalendarDashlet.isEventsDisplayed(event1), "The " + event1 + " isn't correctly displayed on calendar");
    }

    @Test(groups = { "Calendar", "EnterpriseOnly" })
    public void AONE_665() throws Exception
    {
        boolean allDay = false;

        ShareUser.login(drone, testUser);

        // Calendar is opened
        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Calendar
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();

        // get startTime and endTime
        Map<String, String> timeValues = CalendarUtil.addValuesToCurrentDate(0, 2, "7:00 AM", "9:00 AM", allDay);
        String expectedStartDate = timeValues.get("startDateValue");
        String expectedEndDate = timeValues.get("endDateValue");

        // set event name
        String event1 = "multiple_days_event_" + System.currentTimeMillis();

        // Create any single day event, e.g. event1
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.DAY_TAB, event1, event1, event1, timeValues.get("startYear"),
                timeValues.get("startMonth"), timeValues.get("startDay"), timeValues.get("startTime"), timeValues.get("endYear"), timeValues.get("endMonth"),
                timeValues.get("endDay"), timeValues.get("endTime"), null, allDay);

        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        SiteCalendarDashlet siteCalendarDashlet = siteDashBoard.getDashlet("site-calendar").render();

        Assert.assertTrue(siteCalendarDashlet.isEventsDisplayed(event1), "The " + event1 + " isn't correctly displayed on calendar");

        String eventDetail = timeValues.get("startTime") + " " + event1 + " (until: " + expectedEndDate + ")";
        Assert.assertTrue(siteCalendarDashlet.isEventsWithDetailDisplayed(eventDetail), "The " + eventDetail + " isn't correctly displayed on calendar");

        ShareUser.logout(drone);

        CalendarUtil.changeTimeZone(newTZ);

        ShareUser.login(drone, testUser);

        // Calendar is opened
        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Site Dashboard - Calendar Dashlet
        siteCalendarDashlet = siteDashBoard.getDashlet("site-calendar").render();

        expectedStartDate = CalendarUtil.convertDateToNewTimeZone(expectedStartDate, defaultTZ, newTZ, allDay);
        expectedEndDate = CalendarUtil.convertDateToNewTimeZone(expectedEndDate, defaultTZ, newTZ, allDay);

        String convertedStartTime = CalendarUtil.getTimeFromDate(expectedStartDate);
        eventDetail = convertedStartTime + " " + event1 + " (until: " + expectedEndDate + ")";
        Assert.assertTrue(siteCalendarDashlet.isEventsWithDetailDisplayed(eventDetail), "The " + eventDetail + " isn't correctly displayed on calendar");

    }

    @Test(groups = { "Calendar", "EnterpriseOnly" })
    public void AONE_666() throws Exception
    {
        boolean allDay = true;

        ShareUser.login(drone, testUser);

        // Calendar is opened
        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Calendar
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();

        // get startTime and endTime
        Map<String, String> timeValues = CalendarUtil.addValuesToCurrentDate(0, 2, "7:00 AM", "9:00 AM", allDay);
        String expectedEndDate = timeValues.get("endDateValue");

        // set event name
        String event1 = "multiple_days_allDay_event_" + System.currentTimeMillis();

        // Create any single day event, e.g. event1
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.DAY_TAB, event1, event1, event1, timeValues.get("startYear"),
                timeValues.get("startMonth"), timeValues.get("startDay"), timeValues.get("startTime"), timeValues.get("endYear"), timeValues.get("endMonth"),
                timeValues.get("endDay"), timeValues.get("endTime"), null, allDay);

        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        SiteCalendarDashlet siteCalendarDashlet = siteDashBoard.getDashlet("site-calendar").render();

        Assert.assertTrue(siteCalendarDashlet.isEventsDisplayed(event1), "The " + event1 + " isn't correctly displayed on calendar");

        String eventDetail = event1 + " (until: " + expectedEndDate + " )";
        Assert.assertTrue(siteCalendarDashlet.isEventsWithDetailDisplayed(eventDetail), "The " + eventDetail + " isn't correctly displayed on calendar");

        ShareUser.logout(drone);

        CalendarUtil.changeTimeZone(newTZ);

        ShareUser.login(drone, testUser);

        // Calendar is opened
        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Site Dashboard - Calendar Dashlet
        siteCalendarDashlet = siteDashBoard.getDashlet("site-calendar").render();

        eventDetail = event1 + " (until: " + expectedEndDate + " )";
        Assert.assertTrue(siteCalendarDashlet.isEventsWithDetailDisplayed(eventDetail), "The " + eventDetail + " isn't correctly displayed on calendar");

    }

    @Test(groups = { "Calendar", "EnterpriseOnly" })
    public void AONE_667() throws Exception
    {
        boolean allDay = false;

        ShareUser.login(drone, testUser);

        // Calendar is opened
        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Calendar
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();

        // get startTime and endTime
        Map<String, String> timeValues = CalendarUtil.addValuesToCurrentDate(0, 14, "7:00 AM", "9:00 AM", allDay);
        String expectedStartDate = timeValues.get("startDateValue");
        String expectedEndDate = timeValues.get("endDateValue");

        // set event name
        String event1 = "multiple_weeks_event_1" + System.currentTimeMillis();

        // Create any single day event, e.g. event1
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.DAY_TAB, event1, event1, event1, timeValues.get("startYear"),
                timeValues.get("startMonth"), timeValues.get("startDay"), timeValues.get("startTime"), timeValues.get("endYear"), timeValues.get("endMonth"),
                timeValues.get("endDay"), timeValues.get("endTime"), null, allDay);

        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        SiteCalendarDashlet siteCalendarDashlet = siteDashBoard.getDashlet("site-calendar").render();

        Assert.assertTrue(siteCalendarDashlet.isEventsDisplayed(event1), "The " + event1 + " isn't correctly displayed on calendar");

        String eventDetail = timeValues.get("startTime") + " " + event1 + " (until: " + expectedEndDate + ")";
        Assert.assertTrue(siteCalendarDashlet.isEventsWithDetailDisplayed(eventDetail), "The " + eventDetail + " isn't correctly displayed on calendar");

        ShareUser.logout(drone);

        CalendarUtil.changeTimeZone(newTZ);

        ShareUser.login(drone, testUser);

        // Calendar is opened
        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Site Dashboard - Calendar Dashlet
        siteCalendarDashlet = siteDashBoard.getDashlet("site-calendar").render();

        expectedStartDate = CalendarUtil.convertDateToNewTimeZone(expectedStartDate, defaultTZ, newTZ, allDay);
        expectedEndDate = CalendarUtil.convertDateToNewTimeZone(expectedEndDate, defaultTZ, newTZ, allDay);

        String convertedStartTime = CalendarUtil.getTimeFromDate(expectedStartDate);
        eventDetail = convertedStartTime + " " + event1 + " (until: " + expectedEndDate + ")";
        Assert.assertTrue(siteCalendarDashlet.isEventsWithDetailDisplayed(eventDetail), "The " + eventDetail + " isn't correctly displayed on calendar");

    }

    @Test(groups = { "Calendar", "EnterpriseOnly" })
    public void AONE_668() throws Exception
    {
        boolean allDay = true;

        ShareUser.login(drone, testUser);

        // Calendar is opened
        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Calendar
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();

        // get startTime and endTime
        Map<String, String> timeValues = CalendarUtil.addValuesToCurrentDate(0, 14, "7:00 AM", "9:00 AM", allDay);
        String expectedEndDate = timeValues.get("endDateValue");

        // set event name
        String event1 = "multiple_weeks_allDay_event_" + System.currentTimeMillis();

        // Create any single day event, e.g. event1
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.DAY_TAB, event1, event1, event1, timeValues.get("startYear"),
                timeValues.get("startMonth"), timeValues.get("startDay"), timeValues.get("startTime"), timeValues.get("endYear"), timeValues.get("endMonth"),
                timeValues.get("endDay"), timeValues.get("endTime"), null, allDay);

        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        SiteCalendarDashlet siteCalendarDashlet = siteDashBoard.getDashlet("site-calendar").render();

        Assert.assertTrue(siteCalendarDashlet.isEventsDisplayed(event1), "The " + event1 + " isn't correctly displayed on calendar");

        String eventDetail = event1 + " (until: " + expectedEndDate + " )";
        Assert.assertTrue(siteCalendarDashlet.isEventsWithDetailDisplayed(eventDetail), "The " + eventDetail + " isn't correctly displayed on calendar");

        ShareUser.logout(drone);

        CalendarUtil.changeTimeZone(newTZ);

        ShareUser.login(drone, testUser);

        // Calendar is opened
        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Site Dashboard - Calendar Dashlet
        siteCalendarDashlet = siteDashBoard.getDashlet("site-calendar").render();

        eventDetail = event1 + " (until: " + expectedEndDate + " )";
        Assert.assertTrue(siteCalendarDashlet.isEventsWithDetailDisplayed(eventDetail), "The " + eventDetail + " isn't correctly displayed on calendar");

    }

    @Test(groups = { "Calendar", "EnterpriseOnly" })
    public void AONE_669() throws Exception
    {
        boolean allDay = false;

        ShareUser.login(drone, testUser);

        // Calendar is opened
        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Calendar
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();

        // get startTime and endTime
        Map<String, String> timeValues = CalendarUtil.addValuesToCurrentDate(2, 2, "7:00 AM", "9:00 AM", allDay);
        String expectedStartDate = timeValues.get("startDateValue");
        String expectedEndDate = timeValues.get("endDateValue");

        // set event name
        String event1 = "multiple_months_event_" + System.currentTimeMillis();

        // Create any single day event, e.g. event1
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.DAY_TAB, event1, event1, event1, timeValues.get("startYear"),
                timeValues.get("startMonth"), timeValues.get("startDay"), timeValues.get("startTime"), timeValues.get("endYear"), timeValues.get("endMonth"),
                timeValues.get("endDay"), timeValues.get("endTime"), null, allDay);

        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        SiteCalendarDashlet siteCalendarDashlet = siteDashBoard.getDashlet("site-calendar").render();

        Assert.assertTrue(siteCalendarDashlet.isEventsDisplayed(event1), "The " + event1 + " isn't correctly displayed on calendar");

        String eventDetail = timeValues.get("startTime") + " " + event1 + " (until: " + expectedEndDate + ")";
        Assert.assertTrue(siteCalendarDashlet.isEventsWithDetailDisplayed(eventDetail), "The " + eventDetail + " isn't correctly displayed on calendar");

        ShareUser.logout(drone);

        CalendarUtil.changeTimeZone(newTZ);

        ShareUser.login(drone, testUser);

        // Calendar is opened
        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Site Dashboard - Calendar Dashlet
        siteCalendarDashlet = siteDashBoard.getDashlet("site-calendar").render();

        expectedStartDate = CalendarUtil.convertDateToNewTimeZone(expectedStartDate, defaultTZ, newTZ, allDay);
        expectedEndDate = CalendarUtil.convertDateToNewTimeZone(expectedEndDate, defaultTZ, newTZ, allDay);

        String convertedStartTime = CalendarUtil.getTimeFromDate(expectedStartDate);
        eventDetail = convertedStartTime + " " + event1 + " (until: " + expectedEndDate + ")";
        Assert.assertTrue(siteCalendarDashlet.isEventsWithDetailDisplayed(eventDetail), "The " + eventDetail + " isn't correctly displayed on calendar");

    }

    @Test(groups = { "Calendar", "EnterpriseOnly" })
    public void AONE_670() throws Exception
    {
        boolean allDay = true;

        ShareUser.login(drone, testUser);

        // Calendar is opened
        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Calendar
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();

        // get startTime and endTime
        Map<String, String> timeValues = CalendarUtil.addValuesToCurrentDate(3, 1, "7:00 AM", "9:00 AM", allDay);
        String expectedEndDate = timeValues.get("endDateValue");

        // set event name
        String event1 = "multiple_months_allDay_event_" + System.currentTimeMillis();

        // Create any single day event, e.g. event1
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.DAY_TAB, event1, event1, event1, timeValues.get("startYear"),
                timeValues.get("startMonth"), timeValues.get("startDay"), timeValues.get("startTime"), timeValues.get("endYear"), timeValues.get("endMonth"),
                timeValues.get("endDay"), timeValues.get("endTime"), null, allDay);

        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        SiteCalendarDashlet siteCalendarDashlet = siteDashBoard.getDashlet("site-calendar").render();

        Assert.assertTrue(siteCalendarDashlet.isEventsDisplayed(event1), "The " + event1 + " isn't correctly displayed on calendar");

        String eventDetail = event1 + " (until: " + expectedEndDate + " )";
        Assert.assertTrue(siteCalendarDashlet.isEventsWithDetailDisplayed(eventDetail), "The " + eventDetail + " isn't correctly displayed on calendar");

        ShareUser.logout(drone);

        CalendarUtil.changeTimeZone(newTZ);

        ShareUser.login(drone, testUser);

        // Calendar is opened
        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Site Dashboard - Calendar Dashlet
        siteCalendarDashlet = siteDashBoard.getDashlet("site-calendar").render();

        eventDetail = event1 + " (until: " + expectedEndDate + " )";
        Assert.assertTrue(siteCalendarDashlet.isEventsWithDetailDisplayed(eventDetail), "The " + eventDetail + " isn't correctly displayed on calendar");

    }

    @Test(groups = { "Calendar", "EnterpriseOnly" })
    public void AONE_671() throws Exception
    {
        boolean allDay = false;

        ShareUser.login(drone, testUser);

        // Calendar is opened
        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Calendar
        CalendarPage calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();

        // get startTime and endTime
        Map<String, String> timeValues = CalendarUtil.addValuesToCurrentDate(0, 1, "2:30 PM", "1:00 AM", allDay);
        String expectedStartDate = timeValues.get("startDateValue");
        String expectedEndDate = timeValues.get("endDateValue");

        // set event name
        String event1 = "multiple_days_specific_event_" + System.currentTimeMillis();

        // Create any single day event, e.g. event1
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.DAY_TAB, event1, event1, event1, timeValues.get("startYear"),
                timeValues.get("startMonth"), timeValues.get("startDay"), timeValues.get("startTime"), timeValues.get("endYear"), timeValues.get("endMonth"),
                timeValues.get("endDay"), timeValues.get("endTime"), null, allDay);

        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        SiteCalendarDashlet siteCalendarDashlet = siteDashBoard.getDashlet("site-calendar").render();

        Assert.assertTrue(siteCalendarDashlet.isEventsDisplayed(event1), "The " + event1 + " isn't correctly displayed on calendar");

        String eventDetail = timeValues.get("startTime") + " " + event1 + " (until: " + expectedEndDate + ")";
        Assert.assertTrue(siteCalendarDashlet.isEventsWithDetailDisplayed(eventDetail), "The " + eventDetail + " isn't correctly displayed on calendar");

        ShareUser.logout(drone);

        CalendarUtil.changeTimeZone(newTZ);

        ShareUser.login(drone, testUser);

        // Calendar is opened
        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // navigate to Site Dashboard - Calendar Dashlet
        siteCalendarDashlet = siteDashBoard.getDashlet("site-calendar").render();

        expectedStartDate = CalendarUtil.convertDateToNewTimeZone(expectedStartDate, defaultTZ, newTZ, allDay);
        expectedEndDate = CalendarUtil.convertDateToNewTimeZone(expectedEndDate, defaultTZ, newTZ, allDay);

        String convertedStartTime = CalendarUtil.getTimeFromDate(expectedStartDate);

        eventDetail = convertedStartTime + " " + event1 + " (until: " + expectedEndDate + ")";
        Assert.assertTrue(siteCalendarDashlet.isEventsWithDetailDisplayed(eventDetail), "The " + eventDetail + " isn't correctly displayed on calendar");

    }

    /** AONE-672:Site Dashboard. Calendar dashlet. Recurrent */
    @Test(groups = { "Calendar", "EnterpriseOnly" })
    public void AONE_672() throws Exception
    {
        boolean allDay = false;
        String location = testName + " - Room";
        String startDate = "2:30 PM";
        String endDate = "5:25 PM";

        // MS Outlook 2010 is opened;
        Ldtp l = outlook.openOfficeApplication();

        // create new meeting workspace
        outlook.operateOnCreateNewMeetingWorkspace(l, sharePointPath, siteName, location, testUser, DEFAULT_PASSWORD, true, false);

        // Step 1
        // Create any recurrent event, e.g.:
        //
        // Name: test-event;
        // Start Date: 28/06/2013 14:30;
        // End Date: 28/06/2013 17:25;
        // Recurrence: Daily, Every 1 day;
        // End after: 3 occurences.
        Ldtp l1 = outlook.getAbstractUtil().setOnWindow(siteName);
        l1.click("chkAlldayevent");
        l1 = outlook.getAbstractUtil().setOnWindow(siteName);
        l1.mouseLeftClick("btnRecurrence");
        // set the recurrence
        outlook.operateOnRecurrenceAppointment(l1, startDate, endDate, "3");

        Ldtp after_rec = outlook.getAbstractUtil().setOnWindow(siteName);
        after_rec.click("btnSave");

        // User login.
        ShareUser.login(drone, testUser);

        // Step 2
        // Open Site Dashboard
        // Site Dashboard is opened
        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // Step 3
        // Verify Calendar dashlet.
        SiteCalendarDashlet siteCalendarDashlet = siteDashBoard.getDashlet("site-calendar").render();

        Map<String, String> timeValues = CalendarUtil.addValuesToCurrentDate(0, 0, startDate, endDate, allDay);
        String eventDetail = siteName + " (Repeating)";
        String eventHeader = timeValues.get("endDayOfWeek") + ", " + timeValues.get("startDay") + " " + timeValues.get("startMonth") + ", "
                + timeValues.get("startYear");

        Assert.assertTrue(siteCalendarDashlet.isEventsDisplayed(siteName), "The " + siteName + " isn't correctly displayed on calendar");
        Assert.assertTrue(siteCalendarDashlet.isEventsWithHeaderDisplayed(eventHeader), "The " + eventDetail + " isn't correctly displayed on calendar");
        Assert.assertTrue(siteCalendarDashlet.isEventsWithDetailDisplayed(eventDetail), "The " + eventDetail + " isn't correctly displayed on calendar");
        Assert.assertTrue(siteCalendarDashlet.isRepeating(siteName), "The " + siteName + " isn't cnot a repeating event");

        ShareUser.logout(drone);

        // Step 4
        // On a server machine, open Site Dashboard - instead of accessing a server machine, the date time of the current machine is changed
        CalendarUtil.changeTimeZone(newTZ);

        ShareUser.login(drone, testUser);
        
        // Verify Site Dashboard is opened
        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render(maxWaitTime);

        // Step 5
        // Verify Calendar dashlet.
        siteCalendarDashlet = siteDashBoard.getDashlet("site-calendar").render();

        Assert.assertTrue(siteCalendarDashlet.isEventsDisplayed(siteName), "The " + siteName + " isn't correctly displayed on calendar");
        Assert.assertTrue(siteCalendarDashlet.isEventsWithHeaderDisplayed(eventHeader), "The " + eventDetail + " isn't correctly displayed on calendar");
        Assert.assertTrue(siteCalendarDashlet.isEventsWithDetailDisplayed(eventDetail), "The " + eventDetail + " isn't correctly displayed on calendar");
        Assert.assertTrue(siteCalendarDashlet.isRepeating(siteName), "The " + siteName + " isn't cnot a repeating event");

    }

}
