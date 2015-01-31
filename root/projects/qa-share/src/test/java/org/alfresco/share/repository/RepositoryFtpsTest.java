package org.alfresco.share.repository;

import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.util.PageUtils;
import org.alfresco.share.util.FtpsUtil;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPCmd;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.util.TrustManagerUtils;
import org.testng.Assert;
import org.testng.annotations.*;

import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import java.io.*;
import java.net.Socket;

import static org.testng.Assert.*;

/**
 * @author Marina.Nenadovets
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "EnterpriseOnly" }, timeOut = 400000)
public class RepositoryFtpsTest extends FtpsUtil
{
    private static Log logger = LogFactory.getLog(RepositoryFtpsTest.class);
    private static String remotePathToSites = "/" + "Alfresco" + "/" + "Sites";
    private static String remotePathToRepo = "/" + "Alfresco";
    private static File file;
    private static String server;

    @Override
    @BeforeClass
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        server = PageUtils.getAddress(shareUrl).replaceAll("(:\\d{1,5})?", "");
        logger.info("Starting Tests: " + testName);
        FtpsUtil.setCustomFtpPort(drone, ftpPort);
        if(!(keystorePath == null))
        {
            FtpsUtil.enableFtps(keystorePath, truststorePath);
        }
        else
        {
            File keyStore = FtpsUtil.generateKeyStore(getRandomString(6));
            FtpsUtil.enableFtps(keyStore, null);
        }
    }

    /**
     * Configuring FTPS
     */
    @Test
    public void AONE_6454() throws Exception
    {
        String keyStoreName = getRandomString(6);
        String trustStoreName = getRandomString(6);

        //disabling FTPS
        FtpsUtil.disableFtps();

        //generating keystore and truststore
        //when alfresco is running on remote host - files must be pre-generated
        if(!(keystorePath == null) & !(truststorePath == null))
        {
            FtpsUtil.enableFtps(keystorePath, truststorePath);
        }
        else if (!(keystorePath == null) & truststorePath == null)
        {
            throw new ShareException("Please, specify truststorePath in the properties file.");
        }
        else
        {
            File keyStoreFile = FtpsUtil.generateKeyStore(keyStoreName);
            File trustStoreFile = FtpsUtil.generateTrustStore(keyStoreFile, trustStoreName);
            FtpsUtil.enableFtps(keyStoreFile, trustStoreFile);
        }

        //log in and check ftps is on
        TrustManager trustManager = TrustManagerUtils.getValidateServerCertificateTrustManager();
        FTPSClient ftpsClient = new FTPSClient(false);
        ftpsClient.setTrustManager(trustManager);
        ftpsClient.connect(server, Integer.parseInt(ftpPort));
        ftpsClient.enterLocalPassiveMode();
        assertTrue(ftpsClient.isConnected() && ftpsClient.isRemoteVerificationEnabled(), "Couldn't connect FTP TLS");
        boolean success = ftpsClient.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        assertTrue(success, "Couldn't log in");
    }

    /**
     * Configuring FTPS with keystore specified only
     */
    @Test
    public void AONE_6455() throws Exception
    {
        String keyStoreName = getRandomString(6);

        //disabling FTPS
        FtpsUtil.disableFtps();

        //generating keystore only
        //when alfresco is running on remote host - file must be pre-generated
        if(!(keystorePath == null))
        {
            FtpsUtil.enableFtps(keystorePath, null);
        }
        else
        {
            File keyStoreFile = FtpsUtil.generateKeyStore(keyStoreName);
            FtpsUtil.enableFtps(keyStoreFile, null);
        }

        //log in and check ftps is on
        TrustManager trustManager = TrustManagerUtils.getValidateServerCertificateTrustManager();
        FTPSClient ftpsClient = new FTPSClient(false);
        ftpsClient.setTrustManager(trustManager);
        ftpsClient.connect(server, Integer.parseInt(ftpPort));
        ftpsClient.enterLocalPassiveMode();
        assertTrue(ftpsClient.isConnected() && ftpsClient.isRemoteVerificationEnabled(), "Couldn't connect FTP TLS");
        boolean success = ftpsClient.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        assertTrue(success, "Couldn't log in");
    }

    /**
     * Accessing FTP doing secure logon when FTPS is not configured
     */
    @Test
    public void AONE_6456() throws Exception
    {
        //disabling FTPS
        FtpsUtil.disableFtps();
        FTPSClient ftpsClient = new FTPSClient(false);

        try
        {
            TrustManager trustManager = TrustManagerUtils.getValidateServerCertificateTrustManager();
            ftpsClient.setTrustManager(trustManager);
            ftpsClient.connect((server), Integer.parseInt(ftpPort));
            ftpsClient.enterLocalPassiveMode();
            boolean success = ftpsClient.login(ADMIN_USERNAME, ADMIN_PASSWORD);
            assertFalse(success, "Could log in");

        }
        catch (SSLException sse)
        {
            String theMssg = sse.getLocalizedMessage();
            logger.info(theMssg);
            assertTrue(ftpsClient.getReplyCode() == 534, "Incorrect reply code");
        }
    }

    /**
     * Accessing FTPS doing non-secure logon when FTPS is configured
     */
    @Test
    public void AONE_6457() throws Exception
    {
        //generating keystore only
        //when alfresco is running on remote host - file must be pre-generated
        if(!(keystorePath == null))
        {
            FtpsUtil.enableFtps(keystorePath, null);
        }
        else
        {
            File keyStoreFile = FtpsUtil.generateKeyStore(getRandomString(6));
            FtpsUtil.enableFtps(keyStoreFile, null);
        }

        //log in through FTP
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(server, Integer.parseInt(ftpPort));
        ftpClient.enterLocalPassiveMode();
        ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        ftpClient.getReplyCode();
        boolean success = ftpClient.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        assertFalse(success, "Could log in");
        assertTrue(ftpClient.getReplyCode() == 530, "Incorrect reply code");
    }

    /**
     * Switching a session from secure to normal/plaintext mode after the logon (CCC command)
     */
    @Test
    public void AONE_6458() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName);
        file = newFile(fileName, fileName);
        String remotePath = remotePathToSites + "/" + siteName + "/" + "documentLibrary";

        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        //Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        //Connect to FTP using "FTP over TLS" protocol;
        TrustManager trustManager = TrustManagerUtils.getValidateServerCertificateTrustManager();
        FTPSClient ftpsClient = new FTPSClient(false);
        ftpsClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        ftpsClient.setTrustManager(trustManager);
        ftpsClient.connect((server), Integer.parseInt(ftpPort));
        ftpsClient.login(testUser, DEFAULT_PASSWORD);
        ftpsClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpsClient.changeWorkingDirectory(remotePath);
        ftpsClient.setControlKeepAliveTimeout(600);
        try
        {
            FileInputStream inputStream = new FileInputStream(file);
            OutputStream outputStream = ftpsClient.storeFileStream(file.getName());

            if (outputStream != null)
            {

                byte[] buffer = new byte[4096];
                int l;
                while ((l = inputStream.read(buffer)) != -1)
                {
                    outputStream.write(buffer, 0, l);
                }

                inputStream.close();
                outputStream.flush();
                outputStream.close();
                ftpsClient.logout();
            }
            else
            {
                logger.error(ftpsClient.getReplyString());
            }
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex.getMessage());
        }

        ftpsClient.connect((server), Integer.parseInt(ftpPort));
        ftpsClient.login(testUser, DEFAULT_PASSWORD);
        ftpsClient.execPBSZ(0);

        //Switching a session from secure to normal/plaintext mode (CCC command)
        ftpsClient.execCCC();
        ftpsClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpsClient.changeWorkingDirectory(remotePath);
        ftpsClient.setControlKeepAliveTimeout(600);
        file = newFile(fileName + "1", fileName + "1");
        try
        {
            FileInputStream inputStream = new FileInputStream(file);
            OutputStream outputStream = ftpsClient.storeFileStream(file.getName());

            if (outputStream != null)
            {

                byte[] buffer = new byte[4096];
                int l;
                while ((l = inputStream.read(buffer)) != -1)
                {
                    outputStream.write(buffer, 0, l);
                }
                inputStream.close();
                outputStream.flush();
                outputStream.close();
                ftpsClient.logout();
            }
            else
            {
                logger.error(ftpsClient.getReplyString());
            }
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex.getMessage());
        }

        //check files are present
        ftpsClient.connect((server), Integer.parseInt(ftpPort));
        ftpsClient.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        ftpsClient.changeWorkingDirectory(remotePath);
        String allFiles[] = ftpsClient.listNames();
        assertTrue(allFiles.length == 2 && allFiles[0].equals(fileName) && allFiles[1].equals(fileName + "1"), "Not all files are available");
    }

    /**
     * Handling lost connections
     */
    @Test
    public void AONE_6459() throws Exception
    {
        String testName = getTestName();
        String fileName = getFileName(testName);
        file = getFileWithSize(DATA_FOLDER + fileName, 20);
        FtpsUtil.restrictPort(String.valueOf(55000), String.valueOf(55004));

        //Connect to FTP using "FTP over TLS" protocol;
        TrustManager trustManager = TrustManagerUtils.getValidateServerCertificateTrustManager();
        FTPSClient ftpsClient = new FTPSClient(false);
        ftpsClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        ftpsClient.setTrustManager(trustManager);
        ftpsClient.setControlEncoding("UTF-8");
        ftpsClient.setDefaultTimeout(30000);

        try
        {
            ftpsClient.connect((server), Integer.parseInt(ftpPort));
            ftpsClient.login(ADMIN_USERNAME, ADMIN_PASSWORD);
            ftpsClient.enterLocalPassiveMode();
            ftpsClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpsClient.changeWorkingDirectory(remotePathToRepo);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        uploadFileAndQuit(file, ftpsClient);

        //The server should handle lost connections and free the port automatically and quickly.
        assertFalse(isRemotePortInUse(server, 55000) && isRemotePortInUse(server, 55001) && isRemotePortInUse(server, 55002) && isRemotePortInUse(server, 55003)
            && isRemotePortInUse(server, 55004));
    }

    /**
     * Creating folder
     */
    @Test
    public void AONE_6482() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String folderName = getFolderName(testName);
        String remotePath = remotePathToSites + "/" + siteName + "/" + "documentLibrary";

        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        //Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        // Create any folder in the Document Library space in the created site by FTPS
        assertTrue(FtpsUtil.createSpace(shareUrl, testUser, DEFAULT_PASSWORD, folderName, remotePath), "Can't create " + folderName + " folder");

        // Login to Share, check that the folder is displayed
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        DocumentLibraryPage documentLibraryPage = drone.getCurrentPage().render();
        assertTrue(documentLibraryPage.isItemVisble(folderName), folderName + " folder isn't displayed");
    }

    /**
     * Creating content
     */
    @Test
    public void AONE_6483() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName);
        file = newFile(fileName, fileName);
        String remotePath = remotePathToSites + "/" + siteName + "/" + "documentLibrary";

        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        //Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        //Create any file in the Document Library space in the created site by FTPS;
        assertTrue(FtpsUtil.uploadContent(shareUrl, testUser, DEFAULT_PASSWORD, file, remotePath), "Can't create " + file);

        //Login to Share, check that the file is displayed
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        DocumentLibraryPage documentLibraryPage = drone.getCurrentPage().render();
        assertTrue(documentLibraryPage.isItemVisble(fileName), fileName + " file isn't displayed");
    }

    /**
     * Renaming folder
     */
    @Test
    public void AONE_6484() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String folderName = getFolderName(testName);
        String folderNewName = folderName + "-FTP";
        String remotePath = remotePathToSites + "/" + siteName + "/" + "documentLibrary";

        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        //Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        //Any folder is created
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUser.logout(drone);

        //Rename created folder by FTPS
        assertTrue(FtpsUtil.renameFile(shareUrl, testUser, DEFAULT_PASSWORD, remotePath, folderName, folderNewName), "Can't rename " + folderName);

        //Login to Share, check that the folder is displayed with renamed name
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        DocumentLibraryPage documentLibraryPage = drone.getCurrentPage().render();
        assertTrue(documentLibraryPage.isItemVisble(folderNewName), "The folder isn't renamed");
    }

    /**
     * Renaming content
     */
    @Test
    public void AONE_6485() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName);
        file = newFile(fileName, fileName);
        String fileNewName = fileName + "-FTP";
        String remotePath = remotePathToSites + "/" + siteName + "/" + "documentLibrary";

        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        //Create a site
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        //Upload a file
        ShareUserSitePage.uploadFile(drone, file);
        ShareUser.logout(drone);

        //Rename uploaded content by FTPS;
        assertTrue(FtpsUtil.renameFile(shareUrl, testUser, DEFAULT_PASSWORD, remotePath, fileName, fileNewName), "Can't rename " + fileName);

        //Login to Share, check that content is displayed with renamed name
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        DocumentLibraryPage documentLibraryPage = drone.getCurrentPage().render();
        assertTrue(documentLibraryPage.isItemVisble(fileNewName), "The content isn't renamed");
    }

    /**
     * Deleting folder
     */
    @Test
    public void AONE_6486() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String folderName = getFolderName(testName);
        String remotePath = remotePathToSites + "/" + siteName + "/" + "documentLibrary";

        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        //Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        //Any folder is created
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUser.logout(drone);

        //Delete the folder by FTPS
        assertTrue(FtpsUtil.deleteFolder(shareUrl, testUser, DEFAULT_PASSWORD, folderName, remotePath), "Can't delete " + folderName);

        //Login to Share, check that the folder isn't  displayed in Share client;
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        DocumentLibraryPage documentLibraryPage = drone.getCurrentPage().render();
        assertFalse(documentLibraryPage.isItemVisble(folderName), folderName + " folder is displayed, but should be not");
    }

    /**
     * Deleting content
     */
    @Test
    public void AONE_6487() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName);
        file = newFile(fileName, fileName);
        String remotePath = remotePathToSites + "/" + siteName + "/" + "documentLibrary";

        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        //Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        //Upload a file
        ShareUserSitePage.uploadFile(drone, file);
        ShareUser.logout(drone);

        //Delete uploaded file by FTPS
        assertTrue(FtpsUtil.deleteContentItem(shareUrl, testUser, DEFAULT_PASSWORD, fileName, remotePath), "Can't delete " + fileName);

        //Login to Share, check that the file isn't  displayed in Share client;
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        DocumentLibraryPage documentLibraryPage = drone.getCurrentPage().render();
        assertFalse(documentLibraryPage.isItemVisble(fileName), fileName + " file is displayed, but should be not");
    }

    /**
     * Editing content
     */
    @Test
    public void AONE_6488() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName + ".txt");
        file = newFile(fileName, fileName);
        String remotePath = remotePathToSites + "/" + siteName + "/" + "documentLibrary";

        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        //Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        //Upload a file
        ShareUserSitePage.uploadFile(drone, file);
        ShareUser.logout(drone);

        //Edit uploaded file by FTPS
        assertTrue(FtpsUtil.editContent(shareUrl, testUser, DEFAULT_PASSWORD, fileName, remotePath), "Can't edit " + fileName);
        assertTrue(FtpsUtil.getContent(shareUrl, testUser, DEFAULT_PASSWORD, fileName, remotePath).equals(testUser));

        //Login to Share, check that the file is displayed
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        DocumentLibraryPage documentLibraryPage = drone.getCurrentPage().render();
        assertTrue(documentLibraryPage.isItemVisble(fileName), fileName + " file isn't displayed");

        //Check that the changes made by FTPS are displayed;
        DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName).render();
        assertEquals(detailsPage.getDocumentBody().contains(testUser), true, "The changes are not displayed");
    }

    /**
     * Editing content. Edit offline
     */
    @Test
    public void AONE_6489() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName + ".txt");
        String editedFileName = getFileName(testName + " (" + drone.getValue("working.copy") + ").txt");
        file = newFile(fileName, fileName);
        String remotePath = remotePathToSites + "/" + siteName + "/" + "documentLibrary";

        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        //Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        //Upload a file and click "Edit Offline"
        ShareUserSitePage.uploadFile(drone, file);
        DocumentLibraryPage documentLibraryPage = drone.getCurrentPage().render();
        documentLibraryPage.getFileDirectoryInfo(fileName).selectEditOffline().render();
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isEdited(), "The file isn't blocked for editing");
        ShareUser.logout(drone);

        //Navigate to editing content by FTPS
        assertTrue(FtpsUtil.isObjectExists(shareUrl, testUser, DEFAULT_PASSWORD, fileName, remotePath), fileName + " file is not exist.");
        assertTrue(FtpsUtil.isObjectExists(shareUrl, testUser, DEFAULT_PASSWORD, editedFileName, remotePath), editedFileName + " file is not exist.");

        //Try to edit editing content
        assertFalse(FtpsUtil.editContent(shareUrl, testUser, DEFAULT_PASSWORD, fileName, remotePath), "Can edit " + fileName);
        assertTrue(FtpsUtil.editContent(shareUrl, testUser, DEFAULT_PASSWORD, editedFileName, remotePath), "Can't edit " + fileName);

        //Login to Share, check that the file is displayed
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        documentLibraryPage = drone.getCurrentPage().render();
        assertEquals(ShareUserSitePage.getFileDirectoryInfo(drone, fileName).getContentInfo(), "This document is locked by you for offline editing.");
        assertEquals(ShareUserSitePage.getContentCount(drone), 1, "Incorrect document count: " + ShareUserSitePage.getContentCount(drone));

        //Check that the changes made by FTPS are displayed;
        DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName);
        assertEquals(detailsPage.getDocumentBody().contains(testUser), true, "The changes are not displayed");
    }

    /**
     * Move non-empty folder
     */
    @Test
    public void AONE_6490() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName1 = getSiteName(testName + "-1-") + System.currentTimeMillis();
        String siteName2 = getSiteName(testName + "-2-") + System.currentTimeMillis();
        String fileName1 = getFileName(testName + "-1");
        String fileName2 = getFileName(testName + "-2");
        File file1 = newFile(fileName1, fileName1);
        File file2 = newFile(fileName2, fileName2);
        String folderName = getFolderName(testName);
        String remotePath = remotePathToSites + "/" + siteName1 + "/" + "documentLibrary";
        String destination = remotePathToSites + "/" + siteName2 + "/" + "documentLibrary";

        // Create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        //Any site is created
        ShareUser.login(drone, testUser);
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, siteName2, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName1).render();

        //Any folder is created, some  items are added to folder
        ShareUserSitePage.createFolder(drone, folderName, folderName).render();
        ShareUserSitePage.navigateToFolder(drone, folderName);
        ShareUserSitePage.uploadFile(drone, file1);
        ShareUserSitePage.uploadFile(drone, file2);

        //Copy folder from the site 1 to the site 2 (to Document Library space of site) by FTPS
        assertTrue(FtpsUtil.moveFolder(shareUrl, testUser, DEFAULT_PASSWORD, remotePath, folderName, destination), "Can't move " + folderName + " folder");

        //Log in to Share and navigate to the Document Library of the site 1, check that folder isn't present here
        ShareUser.login(drone, testUser);
        ShareUser.openSitesDocumentLibrary(drone, siteName1).render();
        DocumentLibraryPage documentLibraryPage = drone.getCurrentPage().render();
        assertFalse(documentLibraryPage.isItemVisble(folderName), folderName + " folder is displayed");

        //Navigate to the site2, check that folder is moved here.
        ShareUser.openSitesDocumentLibrary(drone, siteName2).render();
        documentLibraryPage = drone.getCurrentPage().render();
        assertTrue(documentLibraryPage.isItemVisble(folderName), folderName + " folder isn't displayed");

        //Verify that folder has all items;
        ShareUserSitePage.navigateToFolder(drone, folderName);
        assertTrue(documentLibraryPage.isItemVisble(fileName1), fileName1 + " isn't displayed");
        assertTrue(documentLibraryPage.isItemVisble(fileName2), fileName2 + " isn't displayed");
        deleteFile(file1);
        deleteFile(file2);
    }

    /**
     * Manager. Available actions
     */
    @Test
    public void AONE_6491() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain("manager-1" + System.currentTimeMillis());
        String testUser2 = getUserNameFreeDomain("manager-2" + System.currentTimeMillis());
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName);
        file = newFile(fileName, fileName);
        String folderName = getFolderName(testName);
        String folderNewName = folderName + "-FTP";
        String fileNewName = fileName + "-FTP";
        String remotePath = remotePathToSites + "/" + siteName + "/" + "documentLibrary";
        String remotePathToFolder = remotePathToSites + "/" + siteName + "/" + "documentLibrary" + "/" + folderNewName;

        // Create users
        String[] testUserInfo1 = new String[] { testUser1 };
        String[] testUserInfo2 = new String[] { testUser2 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

        //Any site is created
        ShareUser.login(drone, testUser1);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        //Any folder is created
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUserSitePage.navigateToFolder(drone, folderName);
        ShareUserSitePage.uploadFile(drone, file);

        //User1 invites the users to the site with manager role
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName, UserRole.MANAGER);

        //Navigate to created folder by FTPS as manager
        assertTrue(FtpsUtil.isObjectExists(shareUrl, testUser2, DEFAULT_PASSWORD, folderName, remotePath), folderName + " file is not exist.");

        //Verify the possibility to rename a folder by FTPS;
        assertTrue(FtpsUtil.renameFile(shareUrl, testUser2, DEFAULT_PASSWORD, remotePath, folderName, folderNewName), "Can't rename " + folderName);

        //Verify the possibility to rename a content in this folder by FTPS;
        assertTrue(FtpsUtil.renameFile(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileName, fileNewName), "Can't rename " + fileName);

        //Verify the possibility to edit a content by FTPS;
        assertTrue(FtpsUtil.editContent(shareUrl, testUser2, DEFAULT_PASSWORD, fileNewName, remotePathToFolder), "Can't edit " + fileNewName);
        assertTrue(FtpsUtil.getContent(shareUrl, testUser2, DEFAULT_PASSWORD, fileNewName, remotePathToFolder).equals(testUser2));

        //Verify the possibility to delete a content by FTPS;
        assertTrue(FtpsUtil.deleteContentItem(shareUrl, testUser2, DEFAULT_PASSWORD, fileNewName, remotePathToFolder), "Can't delete " + fileNewName);

        //Verify the possibility to create new folder by FTPS;
        assertTrue(FtpsUtil.createSpace(shareUrl, testUser2, DEFAULT_PASSWORD, folderName, remotePathToFolder), "Can't create " + folderName + " folder");

        //Verify the possibility to upload new content by FTPS;
        assertTrue(FtpsUtil.uploadContent(shareUrl, testUser2, DEFAULT_PASSWORD, file, remotePathToFolder), "Can't upload " + fileName + " content");

        //Verify the possibility to delete a folder by FTPS;
        assertTrue(FtpsUtil.deleteSpace(shareUrl, testUser2, DEFAULT_PASSWORD, folderNewName, remotePath), "Can't delete " + folderNewName + " folder");
    }

    /**
     * Collaborator. Available actions
     */
    @Test
    public void AONE_6492() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain("manager" + System.currentTimeMillis());
        String testUser2 = getUserNameFreeDomain("-collaborator" + System.currentTimeMillis());
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName);
        file = newFile(fileName, fileName);
        String folderName = getFolderName(testName);
        String folderNewName = folderName + "-FTP";
        String fileNewName = fileName + "-FTP";
        String remotePath = remotePathToSites + "/" + siteName + "/" + "documentLibrary";
        String remotePathToFolder = remotePathToSites + "/" + siteName + "/" + "documentLibrary" + "/" + folderNewName;

        // Create users
        String[] testUserInfo1 = new String[] { testUser1 };
        String[] testUserInfo2 = new String[] { testUser2 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

        //Any site is created
        ShareUser.login(drone, testUser1);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        //Any folder is created
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUserSitePage.navigateToFolder(drone, folderName);
        ShareUserSitePage.uploadFile(drone, file);

        //User1 invites the users to the site with collaborator role
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName, UserRole.COLLABORATOR);

        //Navigate to created folder by FTP as collaborator
        assertTrue(FtpsUtil.isObjectExists(shareUrl, testUser2, DEFAULT_PASSWORD, folderName, remotePath), folderName + " file is not exist.");

        //Verify the possibility to rename a folder by FTPS;
        assertTrue(FtpsUtil.renameFile(shareUrl, testUser2, DEFAULT_PASSWORD, remotePath, folderName, folderNewName), "Can't rename " + folderName);

        //Verify the possibility to  rename a content in this folder by FTPS;
        assertTrue(FtpsUtil.renameFile(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileName, fileNewName), "Can't rename " + fileName);

        //Verify the possibility to edit a content by FTPS;
        assertTrue(FtpsUtil.editContent(shareUrl, testUser2, DEFAULT_PASSWORD, fileNewName, remotePathToFolder), "Can't edit " + fileNewName);
        assertTrue(FtpsUtil.getContent(shareUrl, testUser2, DEFAULT_PASSWORD, fileNewName, remotePathToFolder).equals(testUser2));

        //Verify the possibility to delete a content by FTPS;
        assertFalse(FtpsUtil.deleteContentItem(shareUrl, testUser2, DEFAULT_PASSWORD, fileNewName, remotePathToFolder), "Can delete " + fileNewName);

        //Verify the possibility to create new folder by FTPS;
        assertTrue(FtpsUtil.createSpace(shareUrl, testUser2, DEFAULT_PASSWORD, folderName, remotePathToFolder), "Can't create " + folderName + " folder");

        //Verify the possibility to upload new content by FTPS;
        assertTrue(FtpsUtil.uploadContent(shareUrl, testUser2, DEFAULT_PASSWORD, file, remotePathToFolder), "Can't upload " + fileName + " content");

        //Verify the possibility to delete a folder by FTPS;
        assertFalse(FtpsUtil.deleteSpace(shareUrl, testUser2, DEFAULT_PASSWORD, folderNewName, remotePath), "Can delete " + folderNewName + " folder");
    }

    /**
     * Contributor. Available actions
     */
    @Test
    public void AONE_6493() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain("-manager" + System.currentTimeMillis());
        String testUser2 = getUserNameFreeDomain("-contributor" + System.currentTimeMillis());
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName);
        file = newFile(fileName, fileName);
        String folderName = getFolderName(testName);
        String folderNewName = folderName + "-FTP";
        String fileNewName = fileName + "-FTP";
        File newFile = newFile(fileNewName, fileNewName);
        String remotePath = remotePathToSites + "/" + siteName + "/" + "documentLibrary";
        String remotePathToFolder = remotePathToSites + "/" + siteName + "/" + "documentLibrary" + "/" + folderName;

        // Create users
        String[] testUserInfo1 = new String[] { testUser1 };
        String[] testUserInfo2 = new String[] { testUser2 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

        //Any site is created
        ShareUser.login(drone, testUser1);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        //Any folder is created
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUserSitePage.navigateToFolder(drone, folderName);
        ShareUserSitePage.uploadFile(drone, file);

        //User1 invites the users to the site with contributor role
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName, UserRole.CONTRIBUTOR);

        //Navigate to created folder by FTP as contributor
        assertTrue(FtpsUtil.isObjectExists(shareUrl, testUser2, DEFAULT_PASSWORD, folderName, remotePath), folderName + " file is not exist.");

        //Verify the possibility to rename a folder by FTPS;
        assertFalse(FtpsUtil.renameFile(shareUrl, testUser2, DEFAULT_PASSWORD, remotePath, folderName, folderNewName), "Can rename " + folderName);

        //Verify the possibility to  rename a content in this folder by FTPS;
        assertFalse(FtpsUtil.renameFile(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileName, fileNewName), "Can rename " + fileName);

        //Verify the possibility to edit a content by FTPS;
        assertFalse(FtpsUtil.editContent(shareUrl, testUser2, DEFAULT_PASSWORD, fileName, remotePathToFolder), "Can edit " + fileName);

        //Verify the possibility to delete a content by FTPS;
        assertFalse(FtpsUtil.deleteContentItem(shareUrl, testUser2, DEFAULT_PASSWORD, fileName, remotePathToFolder), "Can delete " + fileName);

        //Verify the possibility to create new folder by FTPS;
        assertTrue(FtpsUtil.createSpace(shareUrl, testUser2, DEFAULT_PASSWORD, folderNewName, remotePathToFolder), "Can't create " + folderNewName + " folder");

        //Verify the possibility to upload new content by FTPS;
        assertTrue(FtpsUtil.uploadContent(shareUrl, testUser2, DEFAULT_PASSWORD, newFile, remotePathToFolder), "Can't upload " + newFile + " content");

        //Verify the possibility to delete a folder by FTPS;
        assertFalse(FtpsUtil.deleteSpace(shareUrl, testUser2, DEFAULT_PASSWORD, folderName, remotePath), "Can delete " + folderName + " folder");
        deleteFile(newFile);
    }

    /**
     * Consumer. Available actions
     */
    @Test
    public void AONE_6494() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain("-manager" + System.currentTimeMillis());
        String testUser2 = getUserNameFreeDomain("-consumer" + System.currentTimeMillis());
        String siteName = getSiteName(testName + "-") + System.currentTimeMillis();
        String fileName = getFileName(testName);
        file = newFile(fileName, fileName);
        String folderName = getFolderName(testName);
        String folderNewName = folderName + "-FTP";
        String fileNewName = fileName + "-FTP";
        File newFile = newFile(fileNewName, fileNewName);
        String remotePath = remotePathToSites + "/" + siteName + "/" + "documentLibrary";
        String remotePathToFolder = remotePathToSites + "/" + siteName + "/" + "documentLibrary" + "/" + folderName;

        // Create users
        String[] testUserInfo1 = new String[] { testUser1 };
        String[] testUserInfo2 = new String[] { testUser2 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

        //Any site is created
        ShareUser.login(drone, testUser1);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        //Any folder is created
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUserSitePage.navigateToFolder(drone, folderName);
        ShareUserSitePage.uploadFile(drone, file);

        //User1 invites the users to the site with Consumer role
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName, UserRole.CONSUMER);

        //Navigate to created folder by FTPS as consumer
        assertTrue(FtpsUtil.isObjectExists(shareUrl, testUser2, DEFAULT_PASSWORD, folderName, remotePath), folderName + " file is not exist.");

        //Verify the possibility to rename a folder by FTPS;
        assertFalse(FtpsUtil.renameFile(shareUrl, testUser2, DEFAULT_PASSWORD, remotePath, folderName, folderNewName), "Can rename " + folderName);

        //Verify the possibility to  rename a content in this folder by FTPS;
        assertFalse(FtpsUtil.renameFile(shareUrl, testUser2, DEFAULT_PASSWORD, remotePathToFolder, fileName, fileNewName), "Can rename " + fileName);

        //Verify the possibility to edit a content by FTPS;
        assertFalse(FtpsUtil.editContent(shareUrl, testUser2, DEFAULT_PASSWORD, fileName, remotePathToFolder), "Can edit " + fileName);

        //Verify the possibility to delete a content by FTPS;
        assertFalse(FtpsUtil.deleteContentItem(shareUrl, testUser2, DEFAULT_PASSWORD, fileName, remotePathToFolder), "Can delete " + fileName);

        //Verify the possibility to create new folder by FTPS;
        assertFalse(FtpsUtil.createSpace(shareUrl, testUser2, DEFAULT_PASSWORD, folderNewName, remotePathToFolder), "Can create " + folderNewName + " folder");

        //Verify the possibility to upload new content by FTPS;
        assertFalse(FtpsUtil.uploadContent(shareUrl, testUser2, DEFAULT_PASSWORD, newFile, remotePathToFolder), "Can upload " + newFile + " content");

        //Verify the possibility to delete a folder by FTPS;
        assertFalse(FtpsUtil.deleteSpace(shareUrl, testUser2, DEFAULT_PASSWORD, folderName, remotePath), "Can delete " + folderName + " folder");
        deleteFile(newFile);
    }

    @AfterMethod(alwaysRun = true)
    @Parameters({ "fileToDlt" })
    private void deleteFile(@Optional() File fileToDlt)
    {
        File fileToDelete;
        if (fileToDlt == null)
        {
            if (file == null)
            {
                logger.info("Nothing to delete. Quitting.");
                return;
            }
            fileToDelete = new File(file.getAbsolutePath());
        }
        else
        {
            fileToDelete = new File(fileToDlt.getAbsolutePath());
        }
        if (fileToDelete.delete())
            logger.info("File was deleted");
        else
            logger.info("Delete operation has failed");
    }

    /**
     * method to disable ftps
     */
    @AfterClass(alwaysRun = true)
    private void disableFTPS()
    {
        FtpsUtil.disableFtps();
        deleteKeystores();
    }

    private void deleteKeystores()
    {
        File ftpsFolder = new File(DATA_FOLDER + "ftps");
        try
        {
            FileUtils.cleanDirectory(ftpsFolder);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void uploadFileAndQuit(File file, FTPSClient ftpsClient) throws IOException
    {
        try
        {
            FileInputStream inputStream = new FileInputStream(file);
            OutputStream outputStream = ftpsClient.storeFileStream(file.getName());

            if (outputStream != null)
            {
                int l;
                int i = 0;
                while ((l = inputStream.read()) != -1)
                {
                    outputStream.write(l);
                    i++;
                    if (i == 5)
                    {
                        ftpsClient.sendCommand(FTPCmd.QUIT);
                        break;
                    }
                }

                inputStream.close();
                outputStream.flush();
                outputStream.close();
            }
            else
            {
                logger.error(ftpsClient.getReplyString());
            }
        }
        catch (IOException e)
        {
            logger.info("I/O process was interrupted by FTP QUIT command");
        }
    }

    private boolean isRemotePortInUse(String hostName, int portNumber)
    {
        try
        {
            // Socket try to open a REMOTE port
            new Socket(hostName, portNumber).close();
            // remote port can be opened, this is a listening port on remote machine
            // this port is in use on the remote machine !
            return true;
        }
        catch (Exception e)
        {
            // remote port is closed, nothing is running on
            return false;
        }
    }
}
