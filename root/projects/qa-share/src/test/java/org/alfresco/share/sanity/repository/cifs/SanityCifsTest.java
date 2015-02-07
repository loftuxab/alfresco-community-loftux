package org.alfresco.share.sanity.repository.cifs;

import com.cobra.ldtp.Ldtp;
import org.alfresco.explorer.WindowsExplorer;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.CifsUtil;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.test.FailedTestListener;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Listeners(FailedTestListener.class)
public class SanityCifsTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(SanityCifsTest.class);

    private String testUser;

    WindowsExplorer explorer = new WindowsExplorer();
    String mapConnect;
    private static String cifsPath = "\\Sites\\";
    private static String docLib = "\\documentLibrary";
    private static final String regexUrlWithPort = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(:\\d{1,5})?";
    private static final String regexUrlIP = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})";
    private static final String dlgAlfDeskAction = "dlgAlfresco Desktop Action";

    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();

        String testName = this.getClass().getSimpleName();
        testUser = getUserNameFreeDomain(testName);

        String ip = getAddress(networkPath);
        networkPath = networkPath.replaceFirst(regexUrlWithPort, ip);
        if (networkPath.contains("alfresco\\"))
        {
            networkPath = networkPath.replace("alfresco\\", "alfresco");
        }

        mapConnect = "cmd /c start /WAIT net use" + " " + networkDrive + " " + networkPath + " " + "/user:" + ADMIN_USERNAME + " " + ADMIN_PASSWORD;
        Runtime.getRuntime().exec(mapConnect);

        if (CifsUtil.checkDirOrFileExists(10, 200, networkDrive + cifsPath))
        {
            logger.info("----------Mapping succesfull " + testUser);
        }
        else
        {
            logger.error("----------Mapping was not done " + testUser);
        }

        super.tearDown();

    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() throws Exception
    {
        super.setup();

    }

    @AfterMethod(alwaysRun = true)
    public void teardownMethod() throws Exception
    {

        Runtime.getRuntime().exec("taskkill /F /IM EXCEL.EXE");
        Runtime.getRuntime().exec("taskkill /F /IM CobraWinLDTP.EXE");
        super.tearDown();
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() throws IOException
    {

        Runtime.getRuntime().exec("cmd /c start /WAIT net use * /d /y");

        if (CifsUtil.checkDirOrFileNotExists(7, 200, networkDrive + cifsPath))
        {
            logger.info("--------Unmapping succesfull " + testUser);
        }
        else
        {
            logger.error("--------Unmapping was not done correctly " + testUser);
        }

    }

    private static String getAddress(String shareUrl)
    {
        Pattern p1 = Pattern.compile(regexUrlIP);
        Matcher m1 = p1.matcher(shareUrl);
        if (m1.find())
        {
            return m1.group();
        }
        throw new PageException("Can't extract address from URL");
    }

    /**
     * Test - AONE-8027:Check Out using __AlfrescoCheckInOut.exe
     * <ul>
     * <li>Alfresco CIFS is enabled</li>
     * <li>Drag content item "Test" to __AlfrescoCheckInOut.exe icon</li>
     * <li>Alfresco Desctop Action window is displayed "Run check in/out Action"</li>
     * <li>On Alfresco Desctop Action window press OK button</li>
     * <li>The following notification is displayed: "Action returned message. Checked out working copy "Test"</li>
     * <li>Press OK button and refresh window</li>
     * <li>Working copy of the content item is displayed</li>
     * </ul>
     */
    @Test(groups = { "CIFSWindowsClient", "EnterpriseOnly" }, timeOut = 600000)
    public void AONE_8027() throws Exception
    {
        String testName = getTestName() + 3;
        String siteName = getSiteName(testName).toLowerCase() + getRandomString(5);
        String filename = "test" + getRandomString(5);
        String filenameCopy = filename + "(WorkingCopy)";
        String filenameWithExt = filename + ".txt";
        String filenameCopyWithExt = filenameCopy + "txt";

        // Login
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUser.openSitesDocumentLibrary(drone, siteName);
        File file = newFile(filenameWithExt, getRandomString(5));
        file.deleteOnExit();
        ShareUserSitePage.uploadFile(drone, file).render();

        mapConnect = "cmd /c start /WAIT net use" + " " + networkDrive + " " + networkPath + " " + "/user:" + ADMIN_USERNAME + " " + ADMIN_PASSWORD;
        Runtime.getRuntime().exec(mapConnect);
        String removeSecurity = "reg add \"HKLM\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Zones\\3\" /v \"1806\" /t REG_DWORD /d 0 /f";
        Runtime.getRuntime().exec(removeSecurity);

        Ldtp ldtp = explorer.openWindowsExplorer();

        String fullPath = networkDrive + cifsPath + siteName.toLowerCase() + docLib;

        //explorer.openFolder(ldtp, fullPath);
        explorer.openFolder(fullPath);

        // Drag content item "Test" to __AlfrescoCheckInOut.exe icon
        explorer.dragAndDropFile(filename, "CheckInOut");
        logger.info("Drag content item \"Test\" to __AlfrescoCheckInOut.exe icon");

        String tempWin;

        if (explorer.getAbstractUtil().isWindowPresented("Windows Security"))
        {
            tempWin = explorer.getAbstractUtil().getAbsoluteWindowName("Windows Security");
            explorer.clickButton(tempWin, "btnOK");
            logger.info("Click OK button for 'Windows Security' window");
        }

        explorer.activateApplicationWindow(dlgAlfDeskAction);

        // Alfresco Desctop Action window is displayed "Run check in/out Action"
        if (explorer.getAbstractUtil().isWindowPresented(dlgAlfDeskAction))
        {

            Assert.assertTrue(explorer.getAbstractUtil().isObjectDisplayed(ldtp, "lblRuncheckin/outaction"), "'Run check in/out Action' isn't displayed");
            explorer.clickButton(dlgAlfDeskAction, "btnOK");
            logger.info("Click OK button for 'Alfresco Desctop Action' window");

        }

        explorer.activateApplicationWindow(dlgAlfDeskAction);

        // The following notification is displayed: "Action returned message. Checked out working copy "Test"
        if (explorer.getAbstractUtil().isWindowPresented(dlgAlfDeskAction))
        {
            Assert.assertTrue(explorer.getAbstractUtil().isObjectDisplayed(ldtp, "lblActionreturnedmessageCheckedoutworkingcopy" + filenameCopyWithExt),
                    "\"Action returned message. Checked out working copy \"Test\" isn't displayed");
            explorer.clickButton(dlgAlfDeskAction, "btnOK");
            logger.info("Click OK button for 'Alfresco Desctop Action' window");

        }

        explorer.activateApplicationWindow("frmdocumentLibrary");

        Assert.assertTrue(explorer.getAbstractUtil().isObjectDisplayed(ldtp, "lst" + filename), "File isn't displayed");

        // Working copy of the content item is displayed
        if (!explorer.getAbstractUtil().isObjectDisplayed(ldtp, "lst" + filenameCopy))
        {
            explorer.clickButton("frmdocumentLibrary", "btnRefresh\"documentLibrary\"");
            Assert.assertTrue(explorer.getAbstractUtil().isObjectDisplayed(ldtp, "lst" + filenameCopy),
                    "Working copy of the content item is displayed isn't displayed");
            logger.info("Working copy of the content item is displayed");
        }
        logger.info("Working copy of the content item is displayed");

        logger.info("Close window");
        explorer.closeExplorer();
    }

    /**
     * Test - AONE-8028:Check in using __AlfrescoCheckInOut.exe
     * <ul>
     * <li>Alfresco CIFS is enabled</li>
     * <li>Drag working copy of "Test2" to __AlfrescoCheckInOut.exe icon</li>
     * <li>Alfresco Desctop Action window is displayed "Run check in/out Action"</li>
     * <li>On Alfresco Desctop Action window press OK button and refresh window</li>
     * <li>working copy of the document disappears, content item "Test2" becomes unlocked</li>
     * </ul>
     */
    @Test(groups = { "CIFSWindowsClient", "EnterpriseOnly" }, timeOut = 600000)
    public void AONE_8028() throws Exception
    {
        String testName = getTestName() + 3;
        String siteName = getSiteName(testName).toLowerCase() + getRandomString(5);
        String filename = "test" + getRandomString(5);
        String filenameCopy = filename + "(WorkingCopy)";
        String filenameWithExt = filename + ".txt";
        String filenameCopyWithExt = filenameCopy + "txt";

        // Login
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUser.openSitesDocumentLibrary(drone, siteName);
        File file = newFile(filenameWithExt, getRandomString(5));
        file.deleteOnExit();
        ShareUserSitePage.uploadFile(drone, file).render();

        mapConnect = "cmd /c start /WAIT net use" + " " + networkDrive + " " + networkPath + " " + "/user:" + ADMIN_USERNAME + " " + ADMIN_PASSWORD;
        Runtime.getRuntime().exec(mapConnect);
        String removeSecurity = "reg add \"HKLM\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Zones\\3\" /v \"1806\" /t REG_DWORD /d 0 /f";
        Runtime.getRuntime().exec(removeSecurity);

        Ldtp ldtp = explorer.openWindowsExplorer();

        String fullPath = networkDrive + cifsPath + siteName.toLowerCase() + docLib;

        //explorer.openFolder(ldtp, fullPath);
        explorer.openFolder(fullPath);

        // Drag content item "Test" to __AlfrescoCheckInOut.exe icon
        explorer.dragAndDropFile(filename, "CheckInOut");
        logger.info("Drag content item \"Test\" to __AlfrescoCheckInOut.exe icon");

        String tempWin;

        if (explorer.getAbstractUtil().isWindowPresented("Windows Security"))
        {
            tempWin = explorer.getAbstractUtil().getAbsoluteWindowName("Windows Security");
            explorer.clickButton(tempWin, "btnOK");
            logger.info("Click OK button for 'Windows Security' window");
        }

        explorer.activateApplicationWindow(dlgAlfDeskAction);

        // Alfresco Desctop Action window is displayed "Run check in/out Action"
        if (explorer.getAbstractUtil().isWindowPresented(dlgAlfDeskAction))
        {

            Assert.assertTrue(explorer.getAbstractUtil().isObjectDisplayed(ldtp, "lblRuncheckin/outaction"), "'Run check in/out Action' isn't displayed");
            explorer.clickButton(dlgAlfDeskAction, "btnOK");
            logger.info("Click OK button for 'Alfresco Desctop Action' window");

        }

        explorer.activateApplicationWindow(dlgAlfDeskAction);

        // The following notification is displayed: "Action returned message. Checked out working copy "Test"
        if (explorer.getAbstractUtil().isWindowPresented(dlgAlfDeskAction))
        {
            Assert.assertTrue(explorer.getAbstractUtil().isObjectDisplayed(ldtp, "lblActionreturnedmessageCheckedoutworkingcopy" + filenameCopyWithExt),
                    "\"Action returned message. Checked out working copy \"Test\" isn't displayed");
            explorer.clickButton(dlgAlfDeskAction, "btnOK");
            logger.info("Click OK button for 'Alfresco Desctop Action' window");

        }

        explorer.activateApplicationWindow("frmdocumentLibrary");

        Assert.assertTrue(explorer.getAbstractUtil().isObjectDisplayed(ldtp, "lst" + filename), "File isn't displayed");

        // Working copy of the content item is displayed
        if (!explorer.getAbstractUtil().isObjectDisplayed(ldtp, "lst" + filenameCopy))
        {
            explorer.clickButton("frmdocumentLibrary", "btnRefresh\"documentLibrary\"");
            Assert.assertTrue(explorer.getAbstractUtil().isObjectDisplayed(ldtp, "lst" + filenameCopy),
                    "Working copy of the content item is displayed isn't displayed");
            logger.info("Working copy of the content item is displayed");
        }
        logger.info("Working copy of the content item is displayed");

        // Drag content item "Test" to __AlfrescoCheckInOut.exe icon
        explorer.dragAndDropFile(filenameCopy, "CheckInOut");
        logger.info("Drag content item \"Test\" to __AlfrescoCheckInOut.exe icon");

        if (explorer.getAbstractUtil().isWindowPresented("Windows Security"))
        {
            tempWin = explorer.getAbstractUtil().getAbsoluteWindowName("Windows Security");
            explorer.clickButton(tempWin, "btnOK");
            logger.info("Click OK button for 'Windows Security' window");
        }

        explorer.activateApplicationWindow(dlgAlfDeskAction);

        // Alfresco Desctop Action window is displayed "Run check in/out Action"
        if (explorer.getAbstractUtil().isWindowPresented(dlgAlfDeskAction))
        {
            Assert.assertTrue(explorer.getAbstractUtil().isObjectDisplayed(ldtp, "lblRuncheckin/outaction"), "'Run check in/out Action' isn't displayed");
            explorer.clickButton(dlgAlfDeskAction, "btnOK");
            logger.info("Click OK button for 'Alfresco Desctop Action' window");
        }

        explorer.activateApplicationWindow("frmdocumentLibrary");

        Assert.assertTrue(explorer.getAbstractUtil().isObjectDisplayed(ldtp, "lst" + filename), "File isn't displayed");

        if (explorer.getAbstractUtil().isObjectDisplayed(ldtp, "lst" + filenameCopy))
        {
            explorer.clickButton("frmdocumentLibrary", "btnRefresh\"documentLibrary\"");
            Assert.assertFalse(explorer.getAbstractUtil().isObjectDisplayed(ldtp, "lst" + filenameCopy),
                    "Working copy of the content item is displayed isn't displayed");
            logger.info("Working copy of the content item isn't displayed");
        }
        logger.info("Working copy of the content item isn't displayed");

        logger.info("Close window");
        explorer.closeExplorer();
    }

}
