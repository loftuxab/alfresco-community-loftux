package org.alfresco.share.util;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import org.alfresco.po.share.util.PageUtils;
import org.alfresco.share.enums.OSName;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class RemoteUtil extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(RemoteUtil.class);

    private static SshCommandProcessor commandProcessor;
    private static Session session;
    private static final String jmxSysProps = "Alfresco:Name=SystemProperties";
    private static final String jmxCatalinaHome = "catalina.home";
    private static final String jmxJavaHome = "java.home";
    private static final String jmxAlfHome = "alfresco.home";
    private static String javaHome;
    private static String alfHome;
    private static String catalinaHome;
    private static OSName osName;

    private static Session initConnection()
    {
        commandProcessor = new SshCommandProcessor();
        return commandProcessor.connect();
    }

    public static void applyIptables(String dropPocketsNode)
    {
        initConnection();
        String server = JmxUtils.getAddress(dropPocketsNode);
        commandProcessor.executeCommand("service iptables start");
        commandProcessor.executeCommand("iptables -A INPUT -p tcp -s " + server + " -j DROP");
        commandProcessor.executeCommand("iptables -A OUTPUT -p tcp -s " + server + " -j DROP");
        logger.info("Temporarily apply a rule using iptables to drop all packets coming from (outcoming to) " + server + " to " + sshHost);
        commandProcessor.disconnect();
    }

    public static void applyIptablesAllPorts()
    {
        initConnection();
        getCygwinPath("C:\\Alfresco");
        commandProcessor.executeCommand("service iptables start");
        commandProcessor.executeCommand("iptables -F");
        commandProcessor.executeCommand("iptables -A INPUT -p tcp -m tcp -m multiport ! --dports " + serverShhPort + " -j DROP");
        logger.info("Set iptables for all ports except port '" + serverShhPort + "' for host " + sshHost);
        commandProcessor.disconnect();
    }

    public static void removeIpTables(String acceptPocketsNode)
    {

        initConnection();
        String server = PageUtils.getAddress(acceptPocketsNode).replaceAll("(:\\d{1,5})?", "");
        commandProcessor.executeCommand("iptables -D INPUT -s " + server + " -j DROP");
        commandProcessor.executeCommand("iptables -F");
        commandProcessor.executeCommand("service iptables stop");
        logger.info("Turn the filter off iptables to drop all packets coming from " + server + " to " + sshHost);
        commandProcessor.disconnect();
    }

    public static void stopAlfresco(String alfrescoPath)
    {
        switch (osName)
        {
            case Windows:
            {
                String installCmd = "stop-service -inputobject $(get-service -ComputerName " + sshHost + " -Name alfrescoTomcat)";
                CommandLine cmdLine = new CommandLine("powershell");
                cmdLine.addArgument(installCmd);
                DefaultExecutor executor = new DefaultExecutor();
                try
                {
                    executor.execute(cmdLine);
                }
                catch (IOException e)
                {
                    throw new PageOperationException("Unable to stop alfrescoTomcat service", e);
                }
                logger.info("alfrescoTomcat service on " + sshHost + " was stopped");
                break;
            }
            default:
            {
                initConnection();
                commandProcessor.executeCommand(alfrescoPath + "/./alfresco.sh stop");
                logger.info("Stop alfresco server " + sshHost);
                logger.info("Execute command: " + alfrescoPath + "/./alfresco.sh stop");
                commandProcessor.disconnect();
            }
        }
    }

    public static void startAlfresco(String alfrescoPath)
    {
        switch (osName)
        {
            case Windows:
            {
                String installCmd = "start-service -inputobject $(get-service -ComputerName " + sshHost + " -Name alfrescoTomcat)";
                CommandLine cmdLine = new CommandLine("powershell");
                cmdLine.addArgument(installCmd);
                DefaultExecutor executor = new DefaultExecutor();
                try
                {
                    executor.execute(cmdLine);
                }
                catch (IOException e)
                {
                    throw new PageOperationException("Unable to start alfrescoTomcat service", e);
                }
                logger.info("alfrescoTomcat service on " + sshHost + " was started");
                break;
            }
            default:
            {
                initConnection();
                commandProcessor.executeCommand(alfrescoPath + "/./alfresco.sh start");
                logger.info("Start alfresco server " + sshHost);
                logger.info("Execute command: " + alfrescoPath + "/./alfresco.sh start");
                commandProcessor.disconnect();
            }
        }
    }

    public static void waitForAlfrescoStartup(String nodeURL, long starttime)
    {
        long before = System.currentTimeMillis();
        try
        {
            while (!HttpUtil.alfrescoRunning(nodeURL) || ((System.currentTimeMillis() - before) * 0.001) < 2000 * 1000)
            {
                Thread.sleep(5000);
                logger.info("Retrying request to Alfresco login page");
                logger.info((System.currentTimeMillis() - before) / 1000 + " of " + 2000 + " " + " maximum seconds passed after sending start signal");

                if (starttime > 0)
                    if (!(((System.currentTimeMillis() - before) * 0.001) < starttime))
                    {
                        logger.info("Alfresco application isn't up during expected time: " + starttime + " seconds");
                        break;
                    }
                if (HttpUtil.alfrescoRunning(nodeURL))
                {
                    logger.info("Alfresco application is up and running");
                    break;
                }

                if (((System.currentTimeMillis() - before) * 0.001) >= 2000)
                {
                    throw new InterruptedException("Timeout on waiting for Alfresco startup for " + 2000 + " seconds." + System.getProperty("line.separator")
                        + "Try to increase timeout in \"build.properties\" file or inspect \"alfresco.log\"" + " for details");
                }
            }
        }
        catch (Throwable ex)
        {
            logger.info(ex.getMessage());
            System.exit(1);
        }
    }

    public static void waitForAlfrescoShutdown(String nodeURL, long stoptime)
    {
        try
        {
            long before = System.currentTimeMillis();
            logger.info("Waiting for Alfresco instance to shut down");
            while (HttpUtil.alfrescoRunning(nodeURL) || ((System.currentTimeMillis() - before) * 0.001) < 2000)
            {
                Thread.sleep(5000);

                if (stoptime > 0)
                    if (!(((System.currentTimeMillis() - before) * 0.001) < stoptime))
                    {
                        logger.info("Expected shut down time is ended: " + stoptime + " seconds");
                        break;
                    }

                if (!HttpUtil.alfrescoRunning(nodeURL))
                {
                    logger.info("Alfresco application is shut down");
                    break;
                }

                if ((((System.currentTimeMillis() - before) * 0.001) - stoptime) >= 2000)
                {
                    throw new InterruptedException("Timeout on waiting for Alfresco shutdown for " + 2000 + " seconds." + System.getProperty("line.separator")
                        + "Try to increase timeout in \"build.properties\" file or inspect \"alfresco.log\"" + " for details");
                }
            }

        }
        catch (Throwable ex)
        {
            logger.info(ex.getMessage());
            System.exit(1);
        }
    }


    public static String getCygwinPath(String winPath)
    {
        return String.format("`cygpath -u '%s'`", winPath);
    }

    public static boolean isFileExist(String file_path)
    {
        String output;
        initConnection();

        output = commandProcessor.executeCommand("ls -d " + file_path);
        if (output.contains(file_path) && !output.contains("No such file or directory"))
        {
            return true;
        }

        return false;
    }

    public static boolean isFileEmpty(String file_path)
    {
        String output;
        initConnection();

        output = commandProcessor.executeCommand("ls -s " + file_path);
        if (output.contains(0 + " " + file_path))
        {
            return true;
        }

        else
        {
            return false;
        }
    }

    public static void checkForStrings(String searchText, String filepath, String resultsFilePath)
    {
        initConnection();
        commandProcessor.executeCommand("strings " + filepath + " | grep " + searchText + " > " + resultsFilePath);

    }

    public static void mountNfs(String shareUrl, String nfsServerPort, String mountServerPort) throws Exception
    {
        String server = PageUtils.getAddress(shareUrl).replaceAll("(:\\d{1,5})?", "");

        initConnection();
        commandProcessor.executeCommand("mkdir /tmp/alf");
        commandProcessor
            .executeCommand("mount -o nolock,port=" + nfsServerPort + ",mountport=" + mountServerPort + ",proto=tcp " + server + ":/Alfresco /tmp/alf");
        logger.info("NFS mounted!");
        commandProcessor.disconnect();

    }

    public static void createUserOnServer(String username, String password) throws Exception
    {
        initConnection();
        commandProcessor.executeCommand("useradd -u 1000 -g 500 " + username);
        commandProcessor.executeCommand("echo -e " + "\"" + password + "\n" + password + "\"" + "| passwd " + username);
        logger.info("User created!");
        commandProcessor.disconnect();

    }

    public static void unmountNfs(String pathToNFS) throws Exception
    {
        initConnection();
        commandProcessor.executeCommand("umount " + pathToNFS);
        logger.info("NFS unmounted!");
        commandProcessor.disconnect();
    }

    public static void deleteUserOnServer(String username) throws Exception
    {
        initConnection();
        commandProcessor.executeCommand("userdel -f -r " + username);
        logger.info("User deleted!");
        commandProcessor.disconnect();
    }


    private static ChannelSftp openSftp()
    {
        Channel channel;
        try
        {
            session = initConnection();
            logger.info("Connected to " + sshHost);
            channel = session.openChannel("sftp");
            channel.connect();
            logger.info("Sftp channel is open");
            return (ChannelSftp) channel;
        }
        catch (Exception ex)
        {
            throw new PageOperationException("Unable to open sftp channel to remote server", ex);
        }
    }

    /**
     * Method to copy to remote server
     *
     * @param workingDir    String
     * @param sftpTargetDir String
     */
    public static void copyToRemoteServer(String workingDir, String sftpTargetDir)
    {
        ChannelSftp channelSftp = null;
        try
        {
            switch (osName)
            {
                case Windows:
                    copyToRemoteWin(new File(workingDir), "\\\\" + sshHost + "\\" + sftpTargetDir.replace(":", "$"));
                    break;
                default:
                {
                    channelSftp = openSftp();
                    channelSftp.cd(sftpTargetDir);
                    File f = new File(workingDir);
                    channelSftp.put(new FileInputStream(f), f.getName());
                    logger.info("Transfer to " + sshHost + " complete");
                }
            }
        }
        catch (Exception ex)
        {
            throw new PageOperationException("Unable to copy to remote server", ex);
        }
        finally
        {
            if (!(channelSftp == null))
            {
                channelSftp.disconnect();
            }
        }
    }

    /**
     * Method to get oldest item in pbld
     *
     * @param pbldPath String path to destination item
     * @return String name of the item
     */
    public static String getOldestFileInPbld(String pbldPath)
    {
        Vector list;
        List<Integer> timesList = new ArrayList<>();
        ChannelSftp channel = connectPbld();
        try
        {
            list = channel.ls(pbldPath);
            if (list.isEmpty())
            {
                throw new PageOperationException("No artifacts found in " + pbldPath + " folder");
            }
            else
            {
                for (Object theFolder : list)
                {
                    ChannelSftp.LsEntry ff = (ChannelSftp.LsEntry) theFolder;
                    if (ff.getFilename().equals(".") || ff.getFilename().equals(".."))
                    {
                        continue;
                    }
                    timesList.add(ff.getAttrs().getMTime());
                }
                int max = timesList.get(0);
                for (int i = 1; i < timesList.size(); i++)
                {
                    if (timesList.get(i) > max)
                    {
                        max = timesList.get(i);
                    }
                }
                for (Object theFolder : list)
                {
                    if (!((ChannelSftp.LsEntry) theFolder).getFilename().equals(".") && !((ChannelSftp.LsEntry) theFolder).getFilename().equals(".."))
                    {
                        if (((ChannelSftp.LsEntry) theFolder).getAttrs().getMTime() == max)
                        {
                            return ((ChannelSftp.LsEntry) theFolder).getFilename();
                        }
                    }
                }
            }
        }
        catch (SftpException | NullPointerException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if (!(channel == null))
            {
                channel.disconnect();
            }
        }
        return "";
    }

    private static ChannelSftp connectPbld()
    {
        JSch jsch = new JSch();
        Channel channel = null;
        try
        {
            String host = "pbam01.alfresco.com";
            int port = 22;
            if (!pathToRSAKey.isEmpty())
            {
                String privateKey = pathToRSAKey;
                jsch.addIdentity(privateKey);
                logger.info("Identity added ");
            }

            session = jsch.getSession(pbldUserName, host, port);
            logger.info("Session created.");

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            if (!pbldUserPassword.isEmpty())
            {
                session.setPassword(pbldUserPassword);
            }
            session.connect();
            logger.info(host + " session connected.....");

            channel = session.openChannel("sftp");
            channel.setInputStream(System.in);
            channel.setOutputStream(System.out);
            channel.connect();
            logger.info("Shell channel connected....");
        }
        catch (JSchException e)
        {
            e.printStackTrace();
        }
        return (ChannelSftp) channel;
    }

    /**
     * Method to download artifact from pbld01.alfresco.com
     *
     * @param remoteDir    String path to artifact location
     * @param artifactName String artifact name
     * @param targetPath   String target path where artifact is to be download
     * @return String artifact name
     */
    public static String downloadArtifactFromPbld(String remoteDir, String artifactName, String targetPath)
    {
        BufferedInputStream bis = null;
        BufferedOutputStream bos;
        ChannelSftp c = null;
        String artifactNm = null;
        try
        {
            c = connectPbld();
            c.cd(remoteDir);
            byte[] buffer = new byte[1024];
            Vector<ChannelSftp.LsEntry> list = c.ls(artifactName);
            for (ChannelSftp.LsEntry entry : list)
            {
                artifactNm = entry.getFilename();
                bis = new BufferedInputStream(c.get(artifactNm));
            }
            File newFile;
            newFile = new File(targetPath + artifactNm);
            OutputStream os = new FileOutputStream(newFile);
            bos = new BufferedOutputStream(os);
            int readCount;
            logger.info("Getting: " + artifactName);
            while (bis != null && (readCount = bis.read(buffer)) > 0)
            {
                bos.write(buffer, 0, readCount);
            }
            if (bis != null)
            {
                bis.close();
            }
            bos.close();
        }
        catch (Exception e)
        {
            throw new PageOperationException("The file could not be downloaded", e);
        }
        finally
        {
            if (c != null)
            {
                c.disconnect();
            }
        }
        return artifactNm;
    }

    /**
     * Method to retrieve alfresco.log contents
     *
     * @param nodeUrl String node ip address
     * @return String
     */
    public static String getAlfrescoLog(String nodeUrl)
    {
        ChannelSftp channelSftp;
        StringBuilder rv = new StringBuilder();
        RemoteUtil.initJmxProps(nodeUrl);
        if (osName == OSName.Windows)
        {
            catalinaHome = getCygwinPath(catalinaHome);
        }
        try
        {
            channelSftp = openSftp();
            channelSftp.cd(catalinaHome);
            logger.info(String.format("Reading alfresco.log file"));
            BufferedReader bis = new BufferedReader(new InputStreamReader(channelSftp.get("logs/catalina.out")));
            String line;
            while ((line = bis.readLine()) != null)
            {
                rv.append(line);
            }
            bis.close();
            channelSftp.disconnect();
            session.disconnect();
            return rv.toString();
        }
        catch (Exception ex)
        {
            throw new PageOperationException("Unable to read log file", ex);
        }
    }

    /**
     * Method to apply repo amps
     *
     * @param ampPath String amps path
     * @return String output
     */
    public static String applyRepoAmp(String ampPath)
    {
        String output;
        if (osName == OSName.Windows)
        {
            javaHome = getCygwinPath(javaHome);
            alfHome = getCygwinPath(alfHome);
            ampPath = getCygwinPath(ampPath);
            catalinaHome = getCygwinPath(catalinaHome);
        }
        initConnection();
        output = commandProcessor.executeCommand(javaHome + "/bin/java -jar " + alfHome + "/bin/alfresco-mmt.jar install " + ampPath + " "
            + catalinaHome + "/webapps/alfresco.war");
        logger.info("Applied repo amps on  " + sshHost);
        return output;
    }

    /**
     * Method to clean alfresco directory
     *
     * @return String command output
     */
    public static String cleanAlfrescoDir()
    {
        String output;
        if (osName == OSName.Windows)
        {
            catalinaHome = getCygwinPath(catalinaHome);
        }
        initConnection();
        output = commandProcessor.executeCommand("rm -rf " + catalinaHome + "/webapps/alfresco");
        logger.info("Cleaning Alfresco dir...");
        commandProcessor.disconnect();
        return output;
    }

    /**
     * Method to clean Share directory
     *
     * @return String command output
     */
    public static String cleanShareDir()
    {
        String output;
        if (osName == OSName.Windows)
        {
            catalinaHome = getCygwinPath(catalinaHome);
        }
        initConnection();
        output = commandProcessor.executeCommand("rm -rf " + catalinaHome + "/webapps/share");
        logger.info("Cleaning Share dir...");
        commandProcessor.disconnect();
        return output;
    }

    /**
     * Method to apply share amps
     *
     * @param ampPath String amps path
     * @return String output
     */
    public static String applyShareAmp(String ampPath)
    {
        String output;
        if (osName == OSName.Windows)
        {
            javaHome = getCygwinPath(javaHome);
            alfHome = getCygwinPath(alfHome);
            ampPath = getCygwinPath(ampPath);
            catalinaHome = getCygwinPath(catalinaHome);

        }
        initConnection();
        output = commandProcessor.executeCommand(javaHome + "/bin/java -jar " + alfHome + "/bin/alfresco-mmt.jar install " + ampPath + " "
            + catalinaHome + "/webapps/share.war");
        logger.info("Applied share amps on  " + sshHost);
        return output;
    }

    /**
     * Method to copy an item to remote windows machine
     *
     * @param sourceFile File source to copy
     * @param remotePath String remote path
     */
    public static void copyToRemoteWin(File sourceFile, String remotePath)
    {
        String copyCmd = "Copy-item \"" + sourceFile + "\" -Destination " + remotePath;
        String cmdFilePath = DATA_FOLDER + "copyscript.ps1";
        File file = newFile(cmdFilePath, copyCmd);
        CommandLine cmdLine = new CommandLine("powershell");
        cmdLine.addArgument("-ExecutionPolicy");
        cmdLine.addArgument("RemoteSigned");
        cmdLine.addArgument(cmdFilePath);
        DefaultExecutor executor = new DefaultExecutor();
        try
        {
            int value = executor.execute(cmdLine);
            if (value == 0)
            {
                logger.info(sourceFile.getName() + " was copied to " + remotePath);
            }
            else
            {
                throw new PageOperationException("Failed copying " + sourceFile.getName() + " to " + remotePath);
            }
        }
        catch (IOException e)
        {
            throw new PageOperationException("Failed copying " + sourceFile.getName() + " to " + remotePath, e);
        }
        finally
        {
            if (file.delete())
            {
                logger.info(file + " was deleted.");
            }
            else
            {
                logger.info(file + " failed to be deleted.");
            }
        }
    }

    /**
     * Method to initialize Jmx properties for alfresco instance
     *
     * @param nodeUrl String node ip address
     */
    public static void initJmxProps(String nodeUrl)
    {
        javaHome = JmxUtils.getAlfrescoServerProperty(nodeUrl, jmxSysProps, jmxJavaHome).toString();
        alfHome = JmxUtils.getAlfrescoServerProperty(nodeUrl, jmxSysProps, jmxAlfHome).toString();
        catalinaHome = JmxUtils.getAlfrescoServerProperty(nodeUrl, jmxSysProps, jmxCatalinaHome).toString();
        osName = getServerOS(nodeUrl);
    }

    public static void uploadFileFromRemoteFolderToLocalFtp(String filePath, String ftpFilePath, String userName, String userPassword, String serverIP) throws Exception
    {
        initConnection();
        commandProcessor.executeCommand("curl -T " + filePath + " --user " + userName + ":" + userPassword + " ftp://" + serverIP + "/Alfresco/" + ftpFilePath);
        logger.info("File uploaded!");
        commandProcessor.disconnect();
    }

    public static void removeItem(String folderPath)
    {

        initConnection();
        commandProcessor.executeCommand("rm -rf " + folderPath);
        logger.info("Item is deleted");
        commandProcessor.disconnect();

    }

    public static void executeCommand(String command)
    {

        initConnection();
        commandProcessor.executeCommand(command);
        logger.info("Command is executed");
        commandProcessor.disconnect();

    }
}
