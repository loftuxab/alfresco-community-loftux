package org.alfresco.share.enterprise.wqs.web.contact;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.dashlet.SiteWebQuickStartDashlet;
import org.alfresco.po.share.dashlet.WebQuickStartOptions;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.wqs.WcmqsContactPage;
import org.alfresco.po.share.wqs.WcmqsHomePage;
import org.alfresco.share.enterprise.wqs.web.search.SearchTests;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Created by Cristina Axinte on 1/7/2015.
 */

@Listeners(FailedTestListener.class)
public class ContactWorkflowTests extends AbstractUtils
{
    private static final Logger logger = Logger.getLogger(SearchTests.class);
    private final String ALFRESCO_QUICK_START = "Alfresco Quick Start";
    private final String QUICK_START_EDITORIAL = "Quick Start Editorial";

    private String testName;
    private String wqsURL;
    private String siteName;
    private String ipAddress;
    private String hostName;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();

        testName = this.getClass().getSimpleName();
        siteName = testName;

        hostName = (shareUrl).replaceAll(".*\\//|\\:.*", "");
        try
        {
            ipAddress = InetAddress.getByName(hostName).toString().replaceAll(".*/", "");
            logger.info("Ip address from Alfresco server was obtained");
        }
        catch (UnknownHostException | SecurityException e)
        {
            logger.error("Ip address from Alfresco server could not be obtained");
        }

        wqsURL = siteName + ":8080/wcmqs";
        logger.info(" wcmqs url : " + wqsURL);
        logger.info("Start Tests from: " + testName);
    }

    @AfterClass(alwaysRun = true)
    public void tearDown()
    {
        super.tearDown();
    }

    @Test(groups = { "DataPrepWQS" })
    public void dataPrep_AONE() throws Exception
    {
        // User login
        // ---- Step 1 ----
        // ---- Step Action -----
        // WCM Quick Start is installed; - is not required to be executed automatically
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // ---- Step 2 ----
        // ---- Step Action -----
        // Site "My Web Site" is created in Alfresco Share;
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // ---- Step 3 ----
        // ---- Step Action -----
        // WCM Quick Start Site Data is imported;
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlets.WEB_QUICK_START);
        SiteWebQuickStartDashlet wqsDashlet = siteDashBoard.getDashlet(SITE_WEB_QUICK_START_DASHLET).render();
        wqsDashlet.selectWebsiteDataOption(WebQuickStartOptions.FINANCE);
        wqsDashlet.clickImportButtton();

        // Change property for quick start to sitename
        DocumentLibraryPage documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        documentLibPage.selectFolder(ALFRESCO_QUICK_START);
        EditDocumentPropertiesPage documentPropertiesPage = documentLibPage.getFileDirectoryInfo(QUICK_START_EDITORIAL).selectEditProperties().render();
        documentPropertiesPage.setSiteHostname(siteName);
        documentPropertiesPage.clickSave();

        // Change property for quick start live to ip address
        documentLibPage.getFileDirectoryInfo("Quick Start Live").selectEditProperties().render();
        documentPropertiesPage.setSiteHostname(ipAddress);
        documentPropertiesPage.clickSave();

        documentLibPage.render();

        // setup new entry in hosts to be able to access the new wcmqs site
        String setHostAddress = "cmd.exe /c echo. >> %WINDIR%\\System32\\Drivers\\Etc\\hosts && echo " + ipAddress + " " + siteName
                + " >> %WINDIR%\\System32\\Drivers\\Etc\\hosts";
        Runtime.getRuntime().exec(setHostAddress);
    }

    /*
     * AONE-5717 Verify the presence of Contact request on My Tasks dashboard
     */
    @Test(groups = { "WQS", "EnterpriseOnly" })
    public void AONE_5717() throws Exception
    {
        String visitorName = "name " + getTestName();
        String visitorEmail = getTestName() + "@" + DOMAIN_FREE;
        ;
        String visitorComment = "Comment by " + visitorName;

        // ---- Step 1 ----
        // ---- Step action ----
        // Fill all mandatory fields with valid data;
        // ---- Expected results ----
        // Data is entered successfully;

        drone.navigateTo(wqsURL);
        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        homePage.render();
        homePage.clickContactLink();
        WcmqsContactPage contactPage = new WcmqsContactPage(drone);
        contactPage.render();
        contactPage.setVisitorName(visitorName);
        contactPage.setVisitorEmail(visitorEmail);
        contactPage.setVisitorComment(visitorComment);

        // ---- Step 2 ----
        // ---- Step action ----
        // Click Post button;
        // ---- Expected results ----
        // Your message has been sent! message is shown;

        contactPage.clickPostButton().render();
        Assert.assertTrue(contactPage.isAddCommentMessageDisplay(), "Comment was not posted.");
        String expectedMessage = "Your message has been sent!";
        Assert.assertTrue(contactPage.getAddCommentSuccessfulMessage().contains(expectedMessage), "Message: " + expectedMessage + " is not present.");

        // ---- Step 3 ----
        // ---- Step action ----
        // Log in Alfresco Share as admin;
        // ---- Expected results ----
        // Admin is logged in Alfresco Share;

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // ---- Step 4 ----
        // ---- Step action ----
        // Verify the presence of Contact request on My Tasks dashboard;
        // ---- Expected results ----
        // Contact request from %Name% task is present on MyTasks dashboard;

        String taskName = "Contact request from " + visitorName;
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone).render();
        waitForCommentPresent(myTasksPage, taskName);
        Assert.assertTrue(myTasksPage.isTaskPresent(taskName), "Task: " + taskName + " is not present.");

    }

    /*
     * AONE-5718 Verify the available actions for Contact request(v 3.4)
     */
    @Test(groups = { "WQS", "EnterpriseOnly" })
    public void AONE_5718() throws Exception
    {
        String visitorName = "name " + getTestName();
        String visitorEmail = getTestName() + "@" + DOMAIN_FREE;
        String visitorComment = "Comment by " + visitorName;

        // ---- Step 4 ----
        // ---- Step action ---
        // Contact request with valid data is sent;
        drone.navigateTo(wqsURL);
        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        homePage.render();
        homePage.clickContactLink();
        WcmqsContactPage contactPage = new WcmqsContactPage(drone);
        contactPage.render();
        contactPage.setVisitorName(visitorName);
        contactPage.setVisitorEmail(visitorEmail);
        contactPage.setVisitorComment(visitorComment);
        contactPage.clickPostButton().render();
        Assert.assertTrue(contactPage.isAddCommentMessageDisplay(), "Comment was not posted.");

        // ---- Step 1 ----
        // ---- Step action ----
        // Login as admin in Alfresco Share;
        // ---- Expected results ----
        // Admin is logged in Alfresco Share;

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // ---- Step 2 ----
        // ---- Step action ----
        // Verify actions for Contact request on MyTasks dashboard;
        // ---- Expected results ----
        // The are Edit task and View task buttons, Task link, Priority icon;

        String taskName = "Contact request from " + visitorName;
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone).render();
        waitForCommentPresent(myTasksPage, taskName);
        Assert.assertTrue(myTasksPage.isTaskPresent(taskName), "Task: " + taskName + " is not present.");
        Assert.assertTrue(myTasksPage.isTaskEditButtonEnabled(taskName), "Edit task button is not present for task: " + taskName);
        Assert.assertTrue(myTasksPage.isTaskViewButtonEnabled(taskName), "View task button is not present for task: " + taskName);
        Assert.assertTrue(myTasksPage.isTaskPriorityIconEnabled(taskName), "Priority icon button is not present for task: " + taskName);

    }

    private void waitForCommentPresent(MyTasksPage myTasksPage, String taskName) throws InterruptedException
    {
        int count = 1;
        while (!myTasksPage.isTaskPresent(taskName) && count <= 10)
        {
            ShareUser.openUserDashboard(drone);
            ShareUserWorkFlow.navigateToMyTasksPage(drone);
            Thread.sleep(5000);
            count++;
        }
    }

}
