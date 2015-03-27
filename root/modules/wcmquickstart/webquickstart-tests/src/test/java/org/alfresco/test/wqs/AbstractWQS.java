/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.test.wqs;

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.steps.LoginActions;
import org.alfresco.po.share.steps.SiteActions;
import org.alfresco.po.share.util.ShareTestProperty;
import org.alfresco.test.AlfrescoTests;
import org.alfresco.test.util.BasicAuthPublicApiFactory;
import org.alfresco.test.util.SiteService;
import org.alfresco.test.util.UserService;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneImpl;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.po.wqs.FactoryWqsPage;
import org.alfresco.po.wqs.WcmqsBlogPage;
import org.alfresco.po.wqs.WcmqsBlogPostPage;
import org.alfresco.po.wqs.WcmqsEditPage;
import org.alfresco.po.wqs.WcmqsHomePage;
import org.alfresco.po.wqs.WcmqsLoginPage;
import org.alfresco.po.wqs.WcmqsNewsArticleDetails;
import org.alfresco.po.wqs.WcmqsNewsPage;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Class includes: Abstract class holds all common methods for WQS tests
 *
 * @author Oana Caciuc
 */
public abstract class AbstractWQS implements AlfrescoTests
{
    protected static ApplicationContext ctx;
    protected static String shareUrl;
    protected static String wqsURL;
    protected static AlfrescoVersion alfrescoVersion;
    protected static String ADMIN_USERNAME;
    protected static String ADMIN_PASSWORD;

    protected static Map<WebDrone, ShareTestProperty> dronePropertiesMap = new HashMap<WebDrone, ShareTestProperty>();
    Map<String, WebDrone> droneMap = new HashMap<String, WebDrone>();
    protected static ShareTestProperty testProperties;
    protected SiteService siteService;
    protected UserService userService;
    protected static BasicAuthPublicApiFactory dataPrepProperties;
    private static WqsTestProperty wqsTestProperties;

    protected static String UNIQUE_TESTDATA_STRING = "newdata";
    protected static String DOMAIN_FREE = "freethtnew.test";
    protected static String DOMAIN_HYBRID = "hybridnew.test";

    protected final String ALFRESCO_QUICK_START = "Alfresco Quick Start";
    protected final String QUICK_START_EDITORIAL = "Quick Start Editorial";
    protected final String ROOT = "root";
    protected String testName;
    protected WebDrone drone;
    protected LoginActions loginActions = new LoginActions();
    protected SiteActions siteActions = new SiteActions();

    protected static final String SITE_WEB_QUICK_START_DASHLET = "site-wqs";
    protected static final String QUICK_START_LIVE = "Quick Start Live";
    protected static final String NEWS = "news";
    protected static final String INDEX_HTML = "slide1.html";
    protected static final String ACCOUNTING = "accounting";
    protected static final String ACCOUNTING_DATA = "Accounting";
    protected static final String DEFAULT_PASSWORD = "password";

    public static final String SLASH = File.separator;
    private static final String SRC_ROOT = System.getProperty("user.dir") + SLASH;
    private static String RESULTS_FOLDER = SRC_ROOT + "test-output" + SLASH;
    protected static final String DATA_FOLDER = SRC_ROOT + "testdata" + SLASH;

    protected static final int MAX_WAIT_TIME_MINUTES = 4;
    public static long maxWaitTime = 30000;
    private static Log logger = LogFactory.getLog(AbstractWQS.class);


    @BeforeSuite(alwaysRun = true)
    @Parameters({"contextFileName"})
    public static void setupContext(@Optional("wqs-context.xml") String contextFileName)
    {
        List<String> contextXMLList = new ArrayList<String>();
        contextXMLList.add(contextFileName);
        ctx = new ClassPathXmlApplicationContext(contextXMLList.toArray(new String[contextXMLList.size()]));
        testProperties = (ShareTestProperty) ctx.getBean("shareTestProperties");
        wqsTestProperties = (WqsTestProperty) ctx.getBean("wqsProperties");

        wqsURL = wqsTestProperties.getWcmqs();
        shareUrl = testProperties.getShareUrl();
        ADMIN_USERNAME = testProperties.getUsername();
        ADMIN_PASSWORD = testProperties.getPassword();
        alfrescoVersion = testProperties.getAlfrescoVersion();

    }

    public void setup() throws Exception
    {

        siteService = (SiteService) ctx.getBean("siteService");
        userService = (UserService) ctx.getBean("userService");
        dataPrepProperties = (BasicAuthPublicApiFactory) ctx.getBean("basicAuthPublicApiFactory");
        drone = (WebDrone) ctx.getBean("webDrone");

        droneMap.put("std_drone", drone);
        dronePropertiesMap.put(drone, testProperties);
        drone.maximize();

        maxWaitTime = ((WebDroneImpl) drone).getMaxPageRenderWaitTime();

    }

    /**
     * Helper returns the test / methodname. This needs to be called as the 1st
     * step of the test. Common Test code can later be introduced here.
     *
     * @return String testcaseName
     */
    public String getTestName()
    {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }

    /**
     * Helper to consistently get the userName in the specified domain, in the
     * desired format.
     *
     * @param testID String Name of the test for uniquely identifying / mapping
     *               test data with the test
     * @return String userName
     */
    protected String getUserNameForDomain(String testID, String domainName)
    {
        String userName;
        if (domainName.isEmpty())
        {
            domainName = DOMAIN_FREE;
        }
        // ALF: Workaround needs toLowerCase to be added. to be removed when
        // jira is fixed
        userName = String.format("user%s@%s", testID, domainName).toLowerCase();

        return userName;
    }


    /**
     * Helper to consistently get the filename.
     *
     * @param partFileName String Part Name of the file for uniquely identifying /
     *                     mapping test data with the test
     * @return String fileName
     */
    protected String getFileName(String partFileName)
    {
        String fileName;

        fileName = String.format("File%s-%s", UNIQUE_TESTDATA_STRING, partFileName);

        return fileName;
    }

    @AfterClass(alwaysRun = true)
    public void tearDown()
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("shutting web drone");
        }
        // Close the browser
        for (Map.Entry<String, WebDrone> entry : droneMap.entrySet())
        {
            try
            {
                if (entry.getValue() != null)
                {
                    try
                    {
                        entry.getValue().quit();
                    }
                    catch (Exception e)
                    {
                        logger.error("If it's tests associated with admin-console-summary-page. it's normal. If not - we have a problem.");
                    }

                    logger.info(entry.getKey() + " closed");
                    logger.info("[Suite ] : End of Tests in: " + this.getClass().getSimpleName());
                }
            }
            catch (Exception e)
            {
                logger.error("Failed to close previous instance of brower:" + entry.getKey(), e);
            }
        }
    }

    public void savePageSource(String methodName) throws IOException
    {
        for (Map.Entry<String, WebDrone> entry : droneMap.entrySet())
        {
            if (entry.getValue() != null)
            {
                String htmlSource = ((WebDroneImpl) entry.getValue()).getDriver().getPageSource();
                File file = new File(RESULTS_FOLDER + methodName + "_" + entry.getKey() + "_Source.html");
                FileUtils.writeStringToFile(file, htmlSource);
            }
        }
    }

    /**
     * Helper to Take a ScreenShot. Saves a screenshot in target folder
     * <RESULTS_FOLDER>
     *
     * @param methodName String This is the Test Name / ID
     * @throws IOException if error
     */
    public void saveScreenShot(String methodName) throws IOException
    {
        for (Map.Entry<String, WebDrone> entry : droneMap.entrySet())
        {
            if (entry.getValue() != null)
            {
                File file = entry.getValue().getScreenShot();
                File tmp = new File(RESULTS_FOLDER + methodName + "_" + entry.getKey() + ".png");
                FileUtils.copyFile(file, tmp);
            }
        }

    }

    @BeforeMethod
    protected String getMethodName(Method method)
    {
        String methodName = method.getName();
        logger.info("[Test: " + methodName + " ]: START");
        return methodName;
    }

    @AfterMethod
    public void logTestResult(ITestResult result)

    {
        logger.info("[Test: " + result.getMethod().getMethodName() + " ]: " + result.toString().toUpperCase());
    }

    /**
     * Return the {@link WebDrone} Configured starting of test.
     *
     * @return {@link WebDrone}
     */
    public WebDrone getDrone()
    {
        return drone;
    }

    public String getShareUrl()
    {
        return testProperties.getShareUrl();
    }

    protected void waitForCommentPresent(MyTasksPage myTasksPage, String taskName) throws InterruptedException
    {
        int count = 1;
        while (!myTasksPage.isTaskPresent(taskName) && count <= 10)
        {
            siteActions.getSharePage(drone).getNav().selectMyDashBoard().render();
            siteActions.getSharePage(drone).getNav().selectWorkFlowsIHaveStarted().render();
            wait(5000);
            count++;
        }
    }

    protected DocumentLibraryPage navigateToFolderAndCreateContent(DocumentLibraryPage documentLibPage, String folderName, String fileName, String fileContent, String fileTitle)
            throws Exception
    {
        documentLibPage = navigateToWqsFolder(documentLibPage, folderName);
        ContentDetails contentDetails1 = new ContentDetails();
        contentDetails1.setName(fileName);
        contentDetails1.setTitle(fileTitle);
        contentDetails1.setContent(fileContent);
        documentLibPage = siteActions.createContent(drone, contentDetails1, ContentType.HTML);
        return documentLibPage;
    }

    protected DocumentLibraryPage navigateToWqsFolder(DocumentLibraryPage documentLibPage, String folderName)
    {
        documentLibPage = documentLibPage.selectFolder(ALFRESCO_QUICK_START).render();
        documentLibPage = documentLibPage.selectFolder(QUICK_START_EDITORIAL).render();
        documentLibPage = documentLibPage.selectFolder(ROOT).render();
        documentLibPage = documentLibPage.selectFolder(folderName).render();
        return documentLibPage;
    }

    protected String generateRandomStringOfLength(int length)
    {
        char[] chars = "abc defghijkl mnopqrs tu vwxyz".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++)
        {
            char c = chars[random.nextInt(chars.length)];
            stringBuilder.append(c);
        }

        return stringBuilder.toString();
    }

    public WcmqsNewsArticleDetails openNewsFromCategory(String newsCategory, String newsTitle)
    {

        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        WcmqsNewsPage newsPage = homePage.openNewsPageFolder(newsCategory).render();

        try
        {
            return newsPage.clickLinkByTitle(newsTitle).render();
        }
        catch (PageException e)
        {
            logger.error("The Article details page did not opened ", e);
            return null;
        }
    }

    public WcmqsEditPage createNewsArticle(WcmqsNewsArticleDetails newsArticleDetails)
    {
        newsArticleDetails.clickCreateButton();
        WcmqsEditPage editPage = new WcmqsEditPage(drone).render();
        Assert.assertNotNull(editPage);
        return editPage;
    }

    /**
     * Method that waits for the news article to appear on the page for maximum minutesToWait
     * and then opens
     *
     * @param newsPage
     * @param newsArticleTitle
     * @param minutesToWait
     */

    public void waitAndOpenNewsArticle(WcmqsNewsPage newsPage, String newsArticleTitle, int minutesToWait)
    {
        int waitInMilliSeconds = 3000;
        int maxTimeWaitInMilliSeconds = 60000 * minutesToWait;
        boolean newsArticleFound = false;

        while (!newsArticleFound && maxTimeWaitInMilliSeconds > 0)
        {
            try
            {
                newsPage.clickLinkByTitle(newsArticleTitle);
                newsArticleFound = true;
            }
            catch (Exception e)
            {
                synchronized (this)
                {
                    try
                    {
                        this.wait(waitInMilliSeconds);
                    }
                    catch (InterruptedException ex)
                    {
                    }
                }
                drone.refresh();
                maxTimeWaitInMilliSeconds = maxTimeWaitInMilliSeconds - waitInMilliSeconds;
            }

        }

    }

    public void fillInNameAndContentForNewsArticle(WcmqsEditPage editPage, String newsArticleName, String newsArticleTitle, String newsArticleContent)
    {
        String foundTitle;
        String foundContent;

        editPage.editName(newsArticleName);
        editPage.editTitle(newsArticleTitle);
        editPage.insertTextInContent(newsArticleContent);
        editPage.clickSubmitButton();

        WcmqsNewsPage newNewsPage = new WcmqsNewsPage(drone).render();
        newNewsPage = newNewsPage.openNewsPageFolder(WcmqsNewsPage.GLOBAL);
        waitAndOpenNewsArticle(newNewsPage, newsArticleTitle, MAX_WAIT_TIME_MINUTES);
        WcmqsNewsArticleDetails newNewsArticleDetails = new WcmqsNewsArticleDetails(drone).render();

        foundTitle = newNewsArticleDetails.getTitleOfNewsArticle();
        foundContent = newNewsArticleDetails.getBodyOfNewsArticle();
        Assert.assertEquals(newsArticleTitle, foundTitle);
        Assert.assertEquals(newsArticleContent, foundContent);
    }

    /*
     * The user most login in Share
     */

    public void verifyInDocumentLibraryForNewsArticle(String newsArticleName, String newsArticleContent, String category, String siteName)
    {

        String foundTitle;
        String foundContent;

        DocumentLibraryPage documentLibraryPage = siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(ALFRESCO_QUICK_START).render();
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(QUICK_START_EDITORIAL).render();
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(ROOT).render();
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(WcmqsNewsPage.NEWS).render();
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(category).render();
        DocumentDetailsPage blogPostDetailsPage = documentLibraryPage.selectFile(newsArticleName).render();

        foundTitle = blogPostDetailsPage.getDocumentTitle();
        foundContent = blogPostDetailsPage.getDocumentBody();

        Assert.assertEquals(newsArticleName, foundTitle);
        Assert.assertTrue(foundContent.contains(newsArticleContent));
    }

    public void navigateTo(String url)
    {
        drone.navigateTo(url);
    }

    public WcmqsBlogPostPage openBlogPost(String blogPost)
    {

        WcmqsHomePage homePage = FactoryWqsPage.resolveWqsPage(drone).render();
        WcmqsBlogPage blogPage = homePage.selectMenu(WcmqsBlogPage.BLOG_MENU_STR).render();
        blogPage.openBlogPost(blogPost);

        WcmqsBlogPostPage blogPostPage = new WcmqsBlogPostPage(drone);
        blogPostPage.render();
        Assert.assertNotNull(blogPostPage);
        return blogPostPage;
    }

    public WcmqsEditPage createBlogPost(WcmqsBlogPostPage blogPostPage)
    {
        WcmqsEditPage editPage = blogPostPage.createArticle().render();
        Assert.assertNotNull(editPage);
        return editPage;
    }

    public void verifyAllFields(WcmqsEditPage editPage)
    {
        Assert.assertNotNull(editPage.getArticleDetails());
    }

    /**
     * Method that waits for the blog post to appear on the page for maximum minutesToWait
     * and then opens it.
     *
     * @param blogPage
     * @param blogPostTitle
     * @param minutesToWait
     */

    public WcmqsBlogPostPage waitAndOpenBlogPost(WcmqsBlogPage blogPage, String blogPostTitle, int minutesToWait)
    {
        int waitInMilliSeconds = 3000;
        int maxTimeWaitInMilliSeconds = 60000 * minutesToWait;
        boolean newsArticleFound = false;
        WcmqsBlogPostPage blogPost = null;

        while (!newsArticleFound && maxTimeWaitInMilliSeconds > 0)
        {
            try
            {
                blogPage.openBlogPost(blogPostTitle);
                blogPost = FactoryWqsPage.resolveWqsPage(drone).render();
                blogPost.render();
                newsArticleFound = true;
            }
            catch (Exception e)
            {
                synchronized (this)
                {
                    try
                    {
                        logger.info("Waiting for edited name of blog post...");
                        this.wait(waitInMilliSeconds);
                    }
                    catch (InterruptedException ex)
                    {
                    }
                }
                drone.refresh();
                maxTimeWaitInMilliSeconds = maxTimeWaitInMilliSeconds - waitInMilliSeconds;
            }

        }

        if (blogPost == null)
        {
            throw new PageException(" Blog Post Page was not found.");
        }

        return blogPost;

    }

    public void fillInNameAndContentFieldsForBlogPost(WcmqsEditPage editPage, String blogPostName, String blogPostTitle, String blogPostContent)
    {
        String foundTitle;
        String foundContent;

        editPage.editName(blogPostName);
        editPage.editTitle(blogPostTitle);
        editPage.insertTextInContent(blogPostContent);
        editPage.clickSubmitButton();

        WcmqsBlogPage newBlogPage = new WcmqsBlogPage(drone).render();
        waitAndOpenBlogPost(newBlogPage, blogPostTitle, MAX_WAIT_TIME_MINUTES);
        WcmqsBlogPostPage newBlogPost = new WcmqsBlogPostPage(drone).render();

        foundTitle = newBlogPost.getTitle();
        foundContent = newBlogPost.getContent();
        Assert.assertEquals(blogPostTitle, foundTitle);
        Assert.assertEquals(blogPostContent, foundContent);
    }

    /*
     * The user most login in Share
     */

    public void verifyInDocumentLibraryForBlogPost(String siteName, String blogPostName, String blogPostContent)
    {

        String foundTitle;
        String foundContent;

        DocumentLibraryPage documentLibraryPage = siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(ALFRESCO_QUICK_START).render();
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(QUICK_START_EDITORIAL).render();
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(ROOT).render();
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(WcmqsBlogPage.BLOG).render();
        DocumentDetailsPage blogPostDetailsPage = documentLibraryPage.selectFile(blogPostName).render();

        foundTitle = blogPostDetailsPage.getDocumentTitle();
        foundContent = blogPostDetailsPage.getDocumentBody();
        Assert.assertEquals(blogPostName, foundTitle);
        Assert.assertTrue(foundContent.contains(blogPostContent));
    }

    public DocumentLibraryPage navigateToWqsFolderFromRoot(DocumentLibraryPage documentLibraryPage, String folderName)
    {
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(ALFRESCO_QUICK_START).render();
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(QUICK_START_EDITORIAL).render();
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(ROOT).render();
        documentLibraryPage = (DocumentLibraryPage) documentLibraryPage.selectFolder(folderName).render();
        return documentLibraryPage;
    }

    public void waitAndOpenNewSection(WcmqsHomePage homePage, String menuOption, int minutesToWait)
    {
        int waitInMilliSeconds = 3000;
        int maxTimeWaitInMilliSeconds = 60000 * minutesToWait;
        boolean sectionFound = false;

        while (!sectionFound && maxTimeWaitInMilliSeconds > 0)
        {
            try
            {
                homePage.selectMenu(menuOption);
                sectionFound = true;
            }
            catch (Exception e)
            {
                synchronized (this)
                {
                    try
                    {
                        this.wait(waitInMilliSeconds);
                    }
                    catch (InterruptedException ex)
                    {
                    }
                }
                drone.refresh();
                maxTimeWaitInMilliSeconds = maxTimeWaitInMilliSeconds - waitInMilliSeconds;
            }

        }
    }

    public String getIpAddress()
    {
        String hostName = (shareUrl).replaceAll(".*\\//|\\:.*", "");
        String ipAddress = "";
        try
        {
            ipAddress = InetAddress.getByName(hostName).toString().replaceAll(".*/", "");
            logger.info("Ip address from Alfresco server was obtained");
        }
        catch (IOException | SecurityException e)
        {
            logger.error("Ip address from Alfresco server could not be obtained");
        }

        return ipAddress;
    }


    /*
     * This method makes sure the admin is logged in WQS for edit mode.
     * When open an article for the first time, the Login dialog is displayed.
     */

    public void loginToWqs()
    {

       if(drone.isElementDisplayed(By.cssSelector("div[id='awe-login']")))
        {
            WcmqsLoginPage wcmqsLoginPage = new WcmqsLoginPage(drone);
            wcmqsLoginPage.render();
            wcmqsLoginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        }
        else
        {
            synchronized (this)
            {
                try
                {
                    wait(1000);
                    loginToWqs();
                }
                catch (InterruptedException e)
                {
                }
            }
        }
    }


    public void loginToWqsFromHomePage()
    {
        try
        {
            WcmqsHomePage wcmqsHomePage = new WcmqsHomePage(drone);
            wcmqsHomePage.render();
            wcmqsHomePage.selectFirstArticleFromLeftPanel().render();
        }
        catch (PageRenderTimeException e)
        {
            logger.error("The articles details were not opened ", e);
        }
        loginToWqs();
    }


    public void waitForWcmqsToLoad()
    {
        boolean wqsLoad = false;
        WcmqsHomePage homePage = new WcmqsHomePage(drone);
        long waitInMilliSeconds = 3000;
        long maxTimeWaitInMilliSeconds = maxWaitTime;

        while (!wqsLoad && maxTimeWaitInMilliSeconds > 0)
        {
            navigateTo(wqsURL);
            if (homePage.isAlfrescoLogoDisplay())
            {
                wqsLoad = true;
            }
            else
            {
                synchronized (this)
                {
                    try
                    {
                        logger.info("Waiting for WQS to load.");
                        wait(waitInMilliSeconds);
                    }
                    catch (InterruptedException ex)
                    {
                    }
                }
                drone.refresh();
                maxTimeWaitInMilliSeconds = maxTimeWaitInMilliSeconds - waitInMilliSeconds;
            }

        }
    }
}