package org.alfresco.share.util;

import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.po.share.systemsummary.AdminConsoleLink;
import org.alfresco.po.share.systemsummary.FileServersPage;
import org.alfresco.po.share.systemsummary.SystemSummaryPage;
import org.alfresco.po.share.util.PageUtils;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.util.TrustManagerUtils;
import org.testng.Assert;

import javax.net.ssl.TrustManager;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Runtime.getRuntime;

/**
 * @author Marina.Nenadovets
 */
public class FtpsUtil extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(FtpsUtil.class);
    private static final String JMX_FILE_SERVERS_CONFIG = "Alfresco:Type=Configuration,Category=fileServers,id1=default";
    private static final String FTP_STOP = "stop";
    private static final String FTP_START = "start";
    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
    private static final Pattern DOMAIN_PATTERN = Pattern.compile("\\w+(\\.\\w+)*(\\.\\w{2,})");

    private static String ALIAS = getRandomString(6);
    private static String ALGORITHM = "RSA";
    private static int VALIDITY = 7;
    private static String PASS = "alfresco";
    private static String KEYSTORENAME;
    private static String TRUSTSTORENAME;
    private static final String pathToKeyStoreFile = DATA_FOLDER + "ftps";


    /**
     * Method to generate a keystore file
     *
     * @param keystoreName Name of the file
     * @throws Exception
     */
    public static File generateKeyStore(String keystoreName) throws Exception
    {
        KEYSTORENAME = keystoreName;

        CommandLine cmdLine = new CommandLine("keytool");
        cmdLine.addArgument("-genkeypair");
        cmdLine.addArgument("-dname");
        cmdLine.addArgument("cn=Test Name, ou=QA, o=SomeCompany, L=Ghost Town, ST=Uruguay, c=UY");
        cmdLine.addArgument("-keyalg");
        cmdLine.addArgument(ALGORITHM);
        cmdLine.addArgument("-alias");
        cmdLine.addArgument(ALIAS);
        cmdLine.addArgument("-keypass");
        cmdLine.addArgument(PASS);
        cmdLine.addArgument("-keystore");
        cmdLine.addArgument(System.getProperty("java.io.tmpdir") + SLASH + KEYSTORENAME);
        cmdLine.addArgument("-storepass");
        cmdLine.addArgument(PASS);
        cmdLine.addArgument("-validity");
        cmdLine.addArgument(String.valueOf(VALIDITY));
        DefaultExecutor executor = new DefaultExecutor();
        int value = executor.execute(cmdLine);
        if(value == 0)
        {
            logger.info("Generated keystore " + KEYSTORENAME);
        }
        else
        {
            throw new ShareException("Exception occurred when generation keystore");
        }
        return new File(System.getProperty("java.io.tmpdir") + SLASH + KEYSTORENAME);
    }

    /**
     * Method to generate trustore based on existing keystore
     *
     * @param keyStore
     * @param trustStoreName
     * @throws Exception
     */
    public static File generateTrustStore(File keyStore, String trustStoreName) throws Exception
    {
        if (trustStoreName == null || trustStoreName.isEmpty())
        {
            TRUSTSTORENAME = "truststore";
        }
        else
        {
            TRUSTSTORENAME = trustStoreName;
        }
        String line;
        //exporting keystore to alfresco.cer file first
        String cmdline = "keytool -export" +
            " -alias " + ALIAS +
            " -keystore " + keyStore.getAbsolutePath() +
            " -storepass " + PASS +
            " -rfc -file alfresco.cer";
        Process p = getRuntime().exec(cmdline);
        p.waitFor();
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while ((line = in.readLine()) != null)
        {
            System.out.println(line);
            if (line.contains("error"))
            {
                throw new ShareException("Unable to generate keystore.");
            }
        }

        //now generating truststore
        cmdline = "keytool -import" +
            " -alias " + ALIAS +
            " -storepass " + PASS +
            "-file alfresco.cer" +
            " -keystore " + pathToKeyStoreFile + SLASH + TRUSTSTORENAME;
        p = getRuntime().exec(cmdline);
        p.waitFor();
        in = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while ((line = in.readLine()) != null)
        {
            System.out.println(line);
            if (line.contains("error"))
            {
                throw new ShareException("Unable to generate keystore.");
            }
        }
        logger.info("Generated truststore " + TRUSTSTORENAME);
        return new File(pathToKeyStoreFile + SLASH + TRUSTSTORENAME);
    }

    /**
     * Method to generate keystore and unable ftps through jmx
     */
    public static void enableFtps(File keyStore, File trustStore) throws Exception
    {
        String keyStrFilePathOnSys = keyStore.getAbsolutePath();
        JmxUtils.invokeAlfrescoServerProperty(JMX_FILE_SERVERS_CONFIG, FTP_STOP);
        JmxUtils.setAlfrescoServerProperty(JMX_FILE_SERVERS_CONFIG, "ftp.enabled", true);
        JmxUtils.setAlfrescoServerProperty(JMX_FILE_SERVERS_CONFIG, "ftp.requireSecureSession", true);
        JmxUtils.setAlfrescoServerProperty(JMX_FILE_SERVERS_CONFIG, "ftp.keyStore", keyStrFilePathOnSys);
        JmxUtils.setAlfrescoServerProperty(JMX_FILE_SERVERS_CONFIG, "ftp.keyStorePassphrase", PASS);
        if (trustStore == null)
        {
            JmxUtils.invokeAlfrescoServerProperty(JMX_FILE_SERVERS_CONFIG, FTP_START);
        }
        else
        {
            String trustStrFilePathOnSys = trustStore.getAbsolutePath();
            JmxUtils.setAlfrescoServerProperty(JMX_FILE_SERVERS_CONFIG, "ftp.trustStore", trustStrFilePathOnSys);
            JmxUtils.setAlfrescoServerProperty(JMX_FILE_SERVERS_CONFIG, "ftp.trustStorePassphrase", PASS);
            JmxUtils.invokeAlfrescoServerProperty(JMX_FILE_SERVERS_CONFIG, FTP_START);
        }
        logger.info("FTPS is on.");
    }

    /**
     * Method to disable ftps through jmx
     */
    public static void disableFtps()
    {
        JmxUtils.invokeAlfrescoServerProperty(JMX_FILE_SERVERS_CONFIG, FTP_STOP);
        JmxUtils.setAlfrescoServerProperty(JMX_FILE_SERVERS_CONFIG, "ftp.enabled", true);
        JmxUtils.setAlfrescoServerProperty(JMX_FILE_SERVERS_CONFIG, "ftp.requireSecureSession", false);
        JmxUtils.setAlfrescoServerProperty(JMX_FILE_SERVERS_CONFIG, "ftp.keyStore", "");
        JmxUtils.setAlfrescoServerProperty(JMX_FILE_SERVERS_CONFIG, "ftp.keyStorePassphrase", "");
        JmxUtils.setAlfrescoServerProperty(JMX_FILE_SERVERS_CONFIG, "ftp.trustStore", "");
        JmxUtils.setAlfrescoServerProperty(JMX_FILE_SERVERS_CONFIG, "ftp.trustStorePassphrase", "");
        JmxUtils.invokeAlfrescoServerProperty(JMX_FILE_SERVERS_CONFIG, FTP_START);
        logger.info("FTPS is off.");
    }

    /**
     * Method to init the client
     *
     * @param shareUrl
     * @param user
     * @param password
     * @return FTPSClient
     */
    private static FTPSClient connectServer(String shareUrl, String user, String password) throws IOException
    {

        String server = PageUtils.getAddress(shareUrl).replaceAll("(:\\d{1,5})?", "");
        int port = Integer.parseInt(ftpPort);

        TrustManager trustManager = TrustManagerUtils.getValidateServerCertificateTrustManager();
        FTPSClient ftpsClient = new FTPSClient(false);
        if (ftpsClient.isConnected())
        {
            ftpsClient.disconnect();
        }
        ftpsClient.setTrustManager(trustManager);
        logger.debug("using TLS");
        ftpsClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        ftpsClient.setDataTimeout(10000);
        ftpsClient.connect(server, port);
        ftpsClient.enterLocalPassiveMode();
        ftpsClient.setConnectTimeout(2000);
        boolean success = ftpsClient.login(user, password);
        if (!success)
        {
            throw new RuntimeException(ftpsClient.getReplyString());
        }
        return ftpsClient;
    }

    /**
     * Method to upload a content
     *
     * @param shareUrl
     * @param user
     * @param password
     * @param contentName
     * @param remoteFolderPath
     * @return true if content is uploaded
     */
    public static boolean uploadContent(String shareUrl, String user, String password, File contentName, String remoteFolderPath)
    {

        InputStream inputStream;
        OutputStream outputStream;
        boolean result = false;

        try
        {
            FTPSClient ftpsClient = connectServer(shareUrl, user, password);
            ftpsClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
            ftpsClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpsClient.changeWorkingDirectory(remoteFolderPath);
            ftpsClient.setControlKeepAliveTimeout(600);
            if (ftpsClient.isConnected())
                try
                {
                    inputStream = new FileInputStream(contentName);
                    outputStream = ftpsClient.storeFileStream(contentName.getName());

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
                        result = true;
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
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex.getMessage());
        }

        return result;
    }

    /**
     * Method to get list of remote objects from FTPS
     *
     * @param shareUrl
     * @param user
     * @param password
     * @param remoteObject
     * @param remoteFolderPath
     * @return true if object is exist
     */

    public static boolean isObjectExists(String shareUrl, String user, String password, String remoteObject, String remoteFolderPath)
    {

        try
        {
            FTPSClient FTPSClient = connectServer(shareUrl, user, password);
            FTPSClient.changeWorkingDirectory(remoteFolderPath);

            for (String content : FTPSClient.listNames())
            {
                if (content.equals(remoteObject))
                {
                    return true;
                }
            }
            FTPSClient.logout();
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex.getMessage());
        }
        return false;
    }

    /**
     * Method to create a remote folder
     *
     * @param shareUrl
     * @param user
     * @param password
     * @param spaceName
     * @param remoteFilePath
     * @return true if folder is created
     */

    public static boolean createSpace(String shareUrl, String user, String password, String spaceName, String remoteFilePath)
    {

        boolean result;

        try
        {
            FTPSClient ftpsClient = connectServer(shareUrl, user, password);
            ftpsClient.changeWorkingDirectory(remoteFilePath);
            result = ftpsClient.makeDirectory(spaceName);
            if (!result)
            {
                logger.error(ftpsClient.getReplyString());
            }
            ftpsClient.logout();
            ftpsClient.disconnect();
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex.getMessage());
        }
        return result;
    }

    /**
     * Method to edit a content via FTP
     *
     * @param shareUrl
     * @param user
     * @param password
     * @param remoteContentName
     * @param remoteFolderPath
     * @return true if content is edited
     */

    public static boolean editContent(String shareUrl, String user, String password, String remoteContentName, String remoteFolderPath)
    {

        OutputStream outputStream;
        boolean result = false;

        try
        {
            FTPSClient FTPSClient = connectServer(shareUrl, user, password);
            FTPSClient.changeWorkingDirectory(remoteFolderPath);
            outputStream = FTPSClient.storeFileStream(remoteContentName);
            if (outputStream != null)
            {
                outputStream.write(user.getBytes());
                outputStream.close();
                FTPSClient.logout();
                FTPSClient.disconnect();
                result = true;
            }
            else
            {
                logger.error(FTPSClient.getReplyString());
            }
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex.getMessage());
        }
        return result;
    }

    /**
     * Method to delete a remote folder
     *
     * @param shareUrl
     * @param user
     * @param password
     * @param remoteSpaceName
     * @param remoteFolderPath
     * @return true if folder is deleted
     */
    public static boolean deleteFolder(String shareUrl, String user, String password, String remoteSpaceName, String remoteFolderPath)
    {
        boolean result;

        try
        {
            FTPSClient FTPSClient = connectServer(shareUrl, user, password);
            FTPSClient.changeWorkingDirectory(remoteFolderPath);
            result = FTPSClient.removeDirectory(remoteFolderPath + "/" + remoteSpaceName);
            if (!result)
            {
                logger.error(FTPSClient.getReplyString());
            }
            FTPSClient.logout();
            FTPSClient.disconnect();
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex.getMessage());
        }
        return result;
    }

    /**
     * Method to delete a remote content
     *
     * @param shareUrl
     * @param user
     * @param password
     * @param remoteContentName
     * @param remoteFilePath
     * @return true if a content is deleted
     */

    public static boolean deleteContentItem(String shareUrl, String user, String password, String remoteContentName, String remoteFilePath)
    {
        boolean result;

        try
        {

            FTPSClient FTPSClient = connectServer(shareUrl, user, password);
            FTPSClient.changeWorkingDirectory(remoteFilePath);
            result = FTPSClient.deleteFile(remoteContentName);
            if (!result)
            {
                logger.error(FTPSClient.getReplyString());
            }
            FTPSClient.logout();
            FTPSClient.disconnect();

        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex.getMessage());
        }
        return result;
    }

    /**
     * Method to get a content from FTP
     *
     * @param shareUrl
     * @param user
     * @param password
     * @param remoteContentName
     * @param remoteFilePath
     * @return String message
     */

    public static String getContent(String shareUrl, String user, String password, String remoteContentName, String remoteFilePath)
    {
        StringBuilder content = new StringBuilder();
        BufferedReader reader;
        String inputLine;

        try
        {
            FTPSClient FTPSClient = connectServer(shareUrl, user, password);
            boolean isChanged = FTPSClient.changeWorkingDirectory(remoteFilePath);
            if (!isChanged)
            {
                throw new ShareException("No such directory " + remoteFilePath);
            }
            InputStream inputStream = FTPSClient.retrieveFileStream(remoteContentName);
            reader = new BufferedReader(new InputStreamReader(inputStream));
            while ((inputLine = reader.readLine()) != null)
            {
                content.append(inputLine);
            }
            reader.close();
            FTPSClient.logout();
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex.getMessage());
        }

        return content.toString();

    }

    /**
     * Method to set a custom ftp port through Admin Console
     */

    public static void setCustomFtpPort(WebDrone drone, String port)
    {
        SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
        FileServersPage fileServersPage = sysSummaryPage.openConsolePage(AdminConsoleLink.FileServers).render();
        fileServersPage.configFtpPort(port);
    }

    /**
     * Method to delete a space
     *
     * @param shareUrl
     * @param user
     * @param password
     * @param remoteSpaceName
     * @param remoteFolderPath
     * @return true if deleted
     * @throws IOException
     */
    public static boolean deleteSpace(String shareUrl, String user, String password, String remoteSpaceName, String remoteFolderPath) throws IOException
    {
        boolean result = false;
        FTPSClient FTPSClient = connectServer(shareUrl, user, password);

        try
        {
            boolean spaceexists = FTPSClient.changeWorkingDirectory(remoteFolderPath + "/" + remoteSpaceName);

            if (spaceexists)
            {

                if (FTPSClient.listNames().length == 0)
                {

                    FTPSClient.changeToParentDirectory();
                    result = FTPSClient.removeDirectory(remoteSpaceName);
                }
                else
                {
                    for (FTPFile file : FTPSClient.listFiles())
                    {
                        if (file.isFile())
                        {
                            FTPSClient.deleteFile(file.getName());
                        }
                        if (file.isDirectory())
                        {
                            EmptyFolderContents(FTPSClient, file.getName());
                        }

                    }
                    FTPSClient.changeToParentDirectory();
                    result = FTPSClient.removeDirectory(remoteSpaceName);

                }

            }

            FTPSClient.logout();
            FTPSClient.disconnect();
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex.getMessage());
        }
        return result;

    }

    private static void EmptyFolderContents(FTPSClient FTPSClient, String foldername)
    {
        try
        {
            boolean spaceexists = FTPSClient.changeWorkingDirectory(FTPSClient.printWorkingDirectory() + "/" + foldername);

            if (spaceexists)
            {

                if (FTPSClient.listNames().length == 0)
                {
                    FTPSClient.changeToParentDirectory();
                    FTPSClient.removeDirectory(foldername);
                    return;
                }

                for (FTPFile file : FTPSClient.listFiles())
                {
                    if (file.isFile())
                    {
                        FTPSClient.deleteFile(file.getName());
                    }
                    if (file.isDirectory())
                    {

                        EmptyFolderContents(FTPSClient, file.getName());

                    }

                }

                FTPSClient.changeToParentDirectory();
                FTPSClient.removeDirectory(foldername);
            }

        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex.getMessage());
        }

    }

    /**
     * Method to rename a file
     *
     * @param shareUrl
     * @param username
     * @param userpass
     * @param ftppath
     * @param object
     * @param newName
     * @return true if renamed
     * @throws IOException
     */
    public static boolean renameFile(String shareUrl, String username, String userpass, String ftppath, String object, String newName) throws IOException
    {
        boolean result;
        FTPSClient FTPSClient = connectServer(shareUrl, username, userpass);

        try
        {
            FTPSClient.login(username, userpass);
            FTPSClient.enterLocalPassiveMode();
            boolean isChanged = FTPSClient.changeWorkingDirectory(ftppath);
            if (!isChanged)
            {
                throw new ShareException("No such directory " + ftppath);
            }

            result = FTPSClient.rename(object, newName);
            if (!result)
            {
                FTPSClient.logout();
                FTPSClient.disconnect();
                try
                {
                    throw new IllegalAccessException("Seem access denied");
                }
                catch (Exception ex)
                {
                    System.out.println("IOException: " + ex.getMessage());
                }
            }

            FTPSClient.logout();
            FTPSClient.disconnect();
            result = true;
        }
        catch (IOException ex)
        {
            result = false;
        }
        return result;

    }

    /**
     * Method to edit a content
     *
     * @param shareUrl
     * @param username
     * @param userpass
     * @param ftppath
     * @param filename
     * @param contents
     * @return true if edited
     */
    public static boolean EditContent(String shareUrl, String username, String userpass, String ftppath, String filename, String contents)
    {
        boolean successful;

        String ftpUrl = "ftpes://%s:%s@%s/%s;type=i";
        //FTPSClient FTPSClient = connectServer(shareUrl, username, userpass);
        String serverIP = getAddress(shareUrl);

        try
        {

            ftpUrl = String.format(ftpUrl, username, userpass, serverIP, ftppath + filename);
            if (isObjectExists(shareUrl, username, userpass, filename, ftppath))
            {
                URL url = new URL(ftpUrl);
                OutputStream outputStream = null;
                try
                {
                    URLConnection conn = url.openConnection();
                    outputStream = conn.getOutputStream();
                    outputStream.write(contents.getBytes());
                    outputStream.close();
                }
                catch (IOException e)
                {
                    throw new IllegalAccessException("Seem access denied");
                }
                finally
                {
                    if (outputStream != null)
                        outputStream.close();
                }
            }

            // check editing
            if (isObjectExists(shareUrl, username, userpass, filename, ftppath))
            {
                String inputLine;
                URL url = new URL(ftpUrl);
                BufferedReader in = null;
                try
                {
                    URLConnection conn = url.openConnection();
                    in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    while ((inputLine = in.readLine()) != null)
                        Assert.assertTrue(inputLine.equals(contents), "Expected item isn't edited '" + filename);
                    in.close();

                }
                catch (IOException e)
                {
                    throw new IllegalAccessException("Seem access denied");
                }
                finally
                {
                    if (in != null)
                        in.close();
                }
            }

            successful = true;
        }
        catch (Exception ex)
        {
            successful = false;
        }
        finally
        {
            RemoveLocalFile(filename);
        }
        return successful;
    }

    /**
     * @param shareUrl
     * @param username
     * @param userpass
     * @param ftppath
     * @param filename
     * @param contents
     * @return
     */
    public static boolean checkContent(String shareUrl, String username, String userpass, String ftppath, String filename, String contents)
    {
        boolean successful;

        String ftpUrl = "ftpes://%s:%s@%s/%s;type=i";
        String serverIP = getAddress(shareUrl);

        try
        {

            ftpUrl = String.format(ftpUrl, username, userpass, serverIP, ftppath + filename);

            // check editing
            if (isObjectExists(shareUrl, username, userpass, filename, ftppath))
            {
                String inputLine;
                URL url = new URL(ftpUrl);
                BufferedReader in = null;
                try
                {
                    URLConnection conn = url.openConnection();
                    in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    while ((inputLine = in.readLine()) != null)
                        Assert.assertTrue(inputLine.equals(contents), "Expected item isn't edited '" + filename);
                    in.close();

                }
                catch (IOException e)
                {
                    throw new IllegalAccessException("Seem access denied");
                }
                finally
                {
                    if (in != null)
                        in.close();
                }
            }

            successful = true;
        }
        catch (Exception ex)
        {
            successful = false;
        }
        finally
        {
            RemoveLocalFile(filename);
        }
        return successful;
    }

    public static String getAddress(String url)
    {
        checkNotNull(url);
        Matcher m = IP_PATTERN.matcher(url);
        if (m.find())
        {
            return m.group();
        }
        else
        {
            m = DOMAIN_PATTERN.matcher(url);
            if (m.find())
            {
                return m.group();
            }
        }
        throw new PageOperationException(String.format("Can't parse address from url[%s]", url));
    }

    public static void RemoveLocalFile(String filename)
    {
        File file = new File(filename);
        if (file.exists())
        {
            file.delete();
        }
    }

    /**
     * Method to move a folder
     *
     * @param shareUrl
     * @param user
     * @param password
     * @param remoteFolderName
     * @param remoteFolderPath
     * @param destination
     */
    public static boolean moveFolder(String shareUrl, String user, String password, String remoteFolderPath, String remoteFolderName, String destination) throws IOException

    {
        boolean result;
        FTPSClient FTPSClient = connectServer(shareUrl, user, password);

        try
        {
            FTPSClient.changeWorkingDirectory(remoteFolderPath + "/" + remoteFolderName);
            FTPSClient.setControlKeepAliveTimeout(600);

            if (FTPSClient.listNames().length == 0)
            {

                FTPSClient.changeWorkingDirectory(destination);
                result = FTPSClient.makeDirectory(remoteFolderName);
                if (!result)
                {
                    logger.error(FTPSClient.getReplyString());
                    FTPSClient.logout();
                    FTPSClient.disconnect();
                    return result;
                }
                FTPSClient.changeWorkingDirectory(remoteFolderPath);
                result = FTPSClient.removeDirectory(remoteFolderPath + "/" + remoteFolderName);
                if (!result)
                {
                    logger.error(FTPSClient.getReplyString());
                }
            }

            else

            {
                for (FTPFile file : FTPSClient.listFiles())
                {
                    if (!FTPSClient.printWorkingDirectory().startsWith(remoteFolderPath))
                    {
                        FTPSClient.changeWorkingDirectory(remoteFolderPath + "/" + remoteFolderName);
                    }

                    if (file.isFile())
                    {

                        InputStream inputStream = null;
                        ByteArrayOutputStream outputStream = null;

                        try
                        {
                            outputStream = new ByteArrayOutputStream();
                            FTPSClient.retrieveFile(file.getName(), outputStream);
                            inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                            FTPSClient.setFileType(FTP.BINARY_FILE_TYPE);
                            FTPSClient.changeWorkingDirectory(destination);
                            if (FTPSClient.listDirectories().length == 0)
                            {
                                result = FTPSClient.makeDirectory(remoteFolderName);

                                if (!result)
                                {
                                    logger.error(FTPSClient.getReplyString());
                                    FTPSClient.logout();
                                    FTPSClient.disconnect();
                                    return result;
                                }
                            }
                            else
                            {
                                for (FTPFile content : FTPSClient.listDirectories())
                                {
                                    if (!content.getName().equalsIgnoreCase(remoteFolderName))
                                    {
                                        result = FTPSClient.makeDirectory(remoteFolderName);
                                        if (!result)
                                        {
                                            logger.error(FTPSClient.getReplyString());
                                            FTPSClient.logout();
                                            FTPSClient.disconnect();
                                            return result;
                                        }
                                    }
                                }
                            }

                            FTPSClient.changeWorkingDirectory(destination + "/" + remoteFolderName);
                            FTPSClient.storeFile(file.getName(), inputStream);
                        }
                        catch (IOException ex)
                        {
                            throw new RuntimeException(ex.getMessage());
                        }
                        finally
                        {
                            if (inputStream != null)
                                inputStream.close();

                            if (outputStream != null)
                                outputStream.close();
                        }

                    }

                    if (file.isDirectory())
                    {
                        copyFolderContents(FTPSClient, remoteFolderPath, file.getName(), destination);
                    }

                }
                FTPSClient.changeWorkingDirectory(remoteFolderPath + "/" + remoteFolderName);
                FTPFile[] filesToDlts = FTPSClient.listFiles();
                for (FTPFile theFiles : filesToDlts)
                {
                    FTPSClient.deleteFile(theFiles.getName());
                }

                result = FTPSClient.removeDirectory(remoteFolderPath + "/" + remoteFolderName);
                if (!result)
                {
                    logger.error(FTPSClient.getReplyString());
                }
            }

            FTPSClient.logout();
            FTPSClient.disconnect();
            result = true;
        }

        catch (IOException ex)
        {
            throw new RuntimeException(ex.getMessage());
        }

        return result;
    }

    private static void copyFolderContents(FTPSClient FTPSClient, String remoteFolderPath, String folderName, String destination)
    {
        try
        {
            String newDestination = destination + FTPSClient.printWorkingDirectory().replace(remoteFolderPath, "");
            boolean spaceexists = FTPSClient.changeWorkingDirectory(FTPSClient.printWorkingDirectory() + "/" + folderName);

            if (spaceexists)
            {

                if (FTPSClient.listNames().length == 0)
                {
                    FTPSClient.changeWorkingDirectory(newDestination);
                    FTPSClient.makeDirectory(folderName);
                }

                for (FTPFile file : FTPSClient.listFiles())
                {
                    if (!FTPSClient.printWorkingDirectory().startsWith(remoteFolderPath))
                    {
                        FTPSClient.changeWorkingDirectory(FTPSClient.printWorkingDirectory().replace(destination, remoteFolderPath));
                    }

                    if (file.isFile())
                    {
                        InputStream inputStream = null;
                        ByteArrayOutputStream outputStream = null;

                        try
                        {
                            outputStream = new ByteArrayOutputStream();
                            FTPSClient.retrieveFile(file.getName(), outputStream);
                            inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                            FTPSClient.setFileType(FTP.BINARY_FILE_TYPE);
                            FTPSClient.changeWorkingDirectory(newDestination);
                            if (FTPSClient.listDirectories().length == 0)
                            {
                                boolean result = FTPSClient.makeDirectory(folderName);

                                if (!result)
                                {
                                    logger.error(FTPSClient.getReplyString());
                                    return;
                                }
                            }
                            else
                            {
                                for (FTPFile content : FTPSClient.listDirectories())
                                {
                                    if (!content.getName().equalsIgnoreCase(folderName))
                                    {
                                        boolean result = FTPSClient.makeDirectory(folderName);

                                        if (!result)
                                        {
                                            logger.error(FTPSClient.getReplyString());
                                            return;
                                        }
                                    }
                                }
                            }
                            FTPSClient.changeWorkingDirectory(newDestination + "/" + folderName);
                            FTPSClient.storeFile(file.getName(), inputStream);
                        }
                        catch (IOException ex)
                        {
                            throw new RuntimeException(ex.getMessage());
                        }
                        finally
                        {
                            if (inputStream != null)
                                inputStream.close();

                            if (outputStream != null)
                                outputStream.close();
                        }
                    }
                    if (file.isDirectory())
                    {
                        copyFolderContents(FTPSClient, remoteFolderPath, file.getName(), destination);
                    }
                }
            }
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public static void restrictPort(String portFrom, String portTo)
    {
        JmxUtils.invokeAlfrescoServerProperty(JMX_FILE_SERVERS_CONFIG, FTP_STOP);
        JmxUtils.setAlfrescoServerProperty(JMX_FILE_SERVERS_CONFIG, "ftp.dataPortFrom", portFrom);
        JmxUtils.setAlfrescoServerProperty(JMX_FILE_SERVERS_CONFIG, "ftp.dataPortTo", portTo);
        JmxUtils.invokeAlfrescoServerProperty(JMX_FILE_SERVERS_CONFIG, FTP_START);
        logger.info("FTP port range was restricted from " + portFrom + " to " + portTo);
    }
}
