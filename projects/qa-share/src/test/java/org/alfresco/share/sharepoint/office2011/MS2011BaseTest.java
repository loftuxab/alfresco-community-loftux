package org.alfresco.share.sharepoint.office2011;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.alfresco.application.mac.MicrosoftDocumentConnection;
import org.alfresco.application.mac.MicrosoftExcel2011;
import org.alfresco.application.mac.MicrosoftWord2011;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.apache.log4j.Logger;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

/**
 * This is the main class that can be inherited by each Office2011 tests for MAC
 * The class contains generic methods and variables used in all Office 2011 tests
 * 
 * @author Paul Brodner
 */
public class MS2011BaseTest extends AbstractUtils
{
    protected DocumentLibraryPage documentLibraryPage;
    protected static final String SHAREPOINT = "sharepoint";
    protected MicrosoftExcel2011 appExcel2011;
    protected MicrosoftWord2011 appWord2011;
    protected MicrosoftDocumentConnection mdc;
    protected ArrayList<File> testFiles = new ArrayList<File>();

    protected static final Logger logger = onThisClass(); // this is a standard method that will use the inherited class

    protected String testName;
    protected String testUser;
    protected String testSiteName;

    @BeforeClass(alwaysRun = true)
    @Parameters({ "TESTID" })
    public void setup(@Optional String TESTID) throws Exception
    {
        super.setup();
        if (TESTID == null || TESTID.contains("testid"))
            TESTID = "1";

        appExcel2011 = new MicrosoftExcel2011();
        mdc = new MicrosoftDocumentConnection("2011");
        testName = this.getClass().getSimpleName() + TESTID;
        testUser = getUserNameFreeDomain(testName);
        testSiteName = getSiteName(testName);
    }

    public MicrosoftDocumentConnection getMDC()
    {
        return mdc;
    }

    @Override
    @AfterClass(alwaysRun = true)
    public void tearDown()
    {
        try
        {
            appExcel2011.handleCrash();
            getMDC().exitApplication();

            appExcel2011.exitApplication();
        }
        catch (Exception e)
        {
            logger.error("Error on TearDown Office 2011" + e.getMessage());
        }

        deleteDuplicatedFiles();
        super.tearDown();
    }

    /**
     * Initialize dada prep
     * This methid will be used accrod Office 2011 tests
     * 
     * @throws Exception
     */
    protected void initializeDataPrep() throws Exception
    {
        // Create normal User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser });

        ShareUtil.logout(drone);
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create public site
        try
        {
            ShareUser.createSite(drone, testSiteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        }
        catch (SkipException e)
        {
            logger.error("Data Prep on Create Site: " + e.getMessage());
        }

        ShareUser.openSiteDocumentLibraryFromSearch(drone, testSiteName);

        // upload all test files
        for (Iterator<File> iterator = testFiles.iterator(); iterator.hasNext();)
        {
            File file = iterator.next();
            logger.info("Setup Data Prep using: " + file.getName());
            ShareUserSitePage.uploadFile(drone, file).render();
        }
    }

    /**
     * Login with default test used and open DocumentLibrary of testSiteName
     */
    protected void openDocumentLibraryForTest()
    {
        if (documentLibraryPage == null || !documentLibraryPage.isLoggedIn())
        {
            ShareUser.logout(drone);
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        }

        getDrone().navigateTo(getDrone().getCurrentUrl().split("share")[0] + "share/page/site/" + testSiteName + "/documentlibrary");
        documentLibraryPage = (DocumentLibraryPage) getCurrentPage(drone).render();
    }

    /**
     * Open the MDC tool
     * 
     * @param testSiteName
     * @param testUser
     * @param password
     * @throws Exception
     */
    protected void openCleanMDCtool(String testSiteName, String testUser, String password) throws Exception
    {
        // even we have a crash on Word, appExcel2011.handleCrash() can cleanup any hanged processes.
        appExcel2011.handleCrash();
        getMDC().killProcesses();
        getMDC().cleanUpHistoryConnectionList();
        getMDC().openApplication();
        getMDC().addLocation(getVTIDocumentLibraryPath(testSiteName), testUser, password);
    }

    /**
     * This will return the logger of the caller class
     * 
     * @return logger of the class name
     */
    private static Logger onThisClass()
    {
        StackTraceElement thisCaller = Thread.currentThread().getStackTrace()[2];
        return Logger.getLogger(thisCaller.getClassName());
    }

    /**
     * Add one extension and this method will return a tmp path file from Documents user's folder
     * 
     * @param extension
     * @return tmp File
     */
    protected File tmpTestFile(String extension)
    {
        return new File(System.getProperty("user.home"), "Documents/tmpTestFile." + extension);
    }
}
