package org.alfresco.share.util;

import org.alfresco.po.alfresco.AlfrescoTransformationServerStatusPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class containing util methods for Transformation Server
 *
 * @author Marina.Nenadovets
 */
public class TransformServerUtil extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(TransformServerUtil.class);
    private static final String JMX_FILE_SERVERS_CONFIG = "Alfresco:Type=Configuration,Category=transformationserver,id1=default";
    private static final String TRANSFORM_STOP = "stop";
    private static final String TRANSFORM_START = "start";
    private static final String REGEX_LICENSE = "INFO  \\[(.*?)transformation.client.LicenseVerifierImpl\\] \\[(.*?)\\] Successfully validated " +
        "Transformation Server license";

    /**
     * Method to install Transformation Server remotely in quiet mode
     *
     * @param transfServNodeName String hostname of the transformation server node
     * @param msiFilePath        String path to msi installer
     * @param destPath           String path to installation destination
     * @return true if success
     */
    public static boolean launchInstallerInQuietMode(String transfServNodeName, String msiFilePath, String destPath, String clusterDB, String clusterNodeName)
    {
        if(transfServNodeName.isEmpty() || msiFilePath == null || destPath == null)
        {
            throw new IllegalArgumentException("Params must not be null.");
        }
        String cmdFilePath;
        StringBuilder installCmd = new StringBuilder("Invoke-Command -Computer " + transfServNodeName + " -ScriptBlock {& cmd /c \"msiexec.exe /i " +
            msiFilePath + " CHECKWORD=0 CHECKEXCEL=0 " + "JDKVERSION=1.6 CHECKPPNT=0 WRAPPERPORT=1 HTTPPORT=2 SSLPORT=3 JMXPORT=4 DEBUGPORT=5 TARGETDIR=" +
            destPath);
        if (!(clusterDB == null && clusterNodeName == null))
        {
            installCmd.append(" CLUSTERDB=").append(clusterDB).append(" CLUSTERNODE=").append(clusterNodeName).append(" /q\"}");
        }
        else
        {
            installCmd.append(" /q\"}");
        }
        cmdFilePath = DATA_FOLDER + "installscript.ps1";
        File file = newFile(cmdFilePath, installCmd.toString());
        CommandLine cmdLine = new CommandLine("powershell");
        cmdLine.addArgument("-ExecutionPolicy");
        cmdLine.addArgument("RemoteSigned");
        cmdLine.addArgument(cmdFilePath);
        DefaultExecutor executor = new DefaultExecutor();
        try
        {
            executor.execute(cmdLine);
        }
        catch (IOException e)
        {
            throw new PageOperationException("Unable to launch the installer", e);
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
        logger.info("Installing Transformation in Quiet Mode on " + transfServNodeName);
        return true;
    }

    /**
     * Method to stop transformation service on remote windows machine
     *
     * @param transfServNodeName Name of machine
     */
    public static void stopTransformServiceRemotely(String transfServNodeName)
    {
        if(transfServNodeName.isEmpty() || transfServNodeName == null)
        {
            throw new IllegalArgumentException("Transformation Server Node name must not be null");
        }
        String installCmd = "stop-service -inputobject $(get-service -ComputerName " + transfServNodeName + " -Name TransformationServer)";
        CommandLine cmdLine = new CommandLine("powershell");
        cmdLine.addArgument(installCmd);
        DefaultExecutor executor = new DefaultExecutor();
        try
        {
            executor.execute(cmdLine);
        }
        catch (IOException e)
        {
            throw new PageOperationException("Unable to stop Transformation service", e.getCause());
        }
        logger.info("Transformation service on " + transfServNodeName + " was stopped");
    }

    /**
     * Method to start transformation service on remote windows machine
     *
     * @param transfServNodeName Name of machine
     */
    public static void startTransformServiceRemotely(String transfServNodeName)
    {
        if(transfServNodeName == null || transfServNodeName.isEmpty())
        {
            throw new IllegalArgumentException("Transformation Server Node name must not be null");
        }
        String installCmd = "start-service -inputobject $(get-service -ComputerName " + transfServNodeName + " -Name TransformationServer)";
        CommandLine cmdLine = new CommandLine("powershell");
        cmdLine.addArgument(installCmd);
        DefaultExecutor executor = new DefaultExecutor();
        try
        {
            executor.execute(cmdLine);
        }
        catch (IOException e)
        {
            throw new PageOperationException("Unable to start Transformation service", e.getCause());
        }
        logger.info("Transformation service on " + transfServNodeName + " was started");
    }

    /**
     * Method to navigate to Transformation Server page
     *
     * @param driver             Webdrone instance
     * @param transfServNodeName String Name of the machine transformation server is installed
     * @return AlfrescoTransformationServerStatusPage
     */
    public static AlfrescoTransformationServerStatusPage navigateToTransformationServerPage(WebDrone driver, String transfServNodeName, int port)
    {
        driver.maximize();
        driver.navigateTo(String.format("http://%s:%s@" + transfServNodeName + ":" + port + "/transformation-server/home", "alfresco", "alfresco"));
        return new AlfrescoTransformationServerStatusPage(driver).render();
    }

    /**
     * Method to set Transformation Server properties via JMX
     *
     * @param nodeUrl       String url of the machine alfresco installed
     * @param transformHost String name of the transformation server host
     * @param username      String username
     * @param password      String password
     */
    public static void setPropertiesInJmx(String nodeUrl, String transformHost, String username, String password)
    {
        JmxUtils.invokeAlfrescoServerProperty(nodeUrl, JMX_FILE_SERVERS_CONFIG, TRANSFORM_STOP);
        JmxUtils.setAlfrescoServerProperty(nodeUrl, JMX_FILE_SERVERS_CONFIG, "transformserver.url", transformHost);
        JmxUtils.setAlfrescoServerProperty(nodeUrl, JMX_FILE_SERVERS_CONFIG, "transformserver.username", username);
        JmxUtils.setAlfrescoServerProperty(nodeUrl, JMX_FILE_SERVERS_CONFIG, "transformserver.password", password);
        JmxUtils.invokeAlfrescoServerProperty(nodeUrl, JMX_FILE_SERVERS_CONFIG, TRANSFORM_START);
        logger.info("Transformation server properties were changed via JMX");
    }

    /**
     * Method to verify whether alfresco license (if any) is valid for Transformation Server
     *
     * @param nodeUrl String ip address of alfresco instance
     * @return boolean
     */
    public static boolean isLicenseValid(String nodeUrl)
    {
        boolean isValid;
        int i = 0;
        Pattern p1 = Pattern.compile(REGEX_LICENSE);
        Matcher m1 = p1.matcher(RemoteUtil.getAlfrescoLog(nodeUrl));
        isValid = m1.find();
        while (!isValid)
        {
            isValid = p1.matcher(RemoteUtil.getAlfrescoLog(nodeUrl)).find();
            i++;
            if (i == 3)
            {
                break;
            }
        }
        logger.info("The license on " + sshHost + " is valid: " + Boolean.toString(isValid));
        return isValid;
    }

    /**
     * Method to uninstall the transformation server on remote windows machine
     *
     * @param transfServNodeName Name of machine
     * @param msiFileRemotePath  path to msi file
     * @return true if successful
     */
    public static boolean uninstallTheServer(String transfServNodeName, String msiFileRemotePath)
    {
        String installCmd;
        String cmdFilePath;
        if(transfServNodeName == null || transfServNodeName.isEmpty() || msiFileRemotePath == null)
        {
            throw new IllegalArgumentException("Params must not be null");
        }
        installCmd = "Invoke-Command -Computer " + transfServNodeName + " -ScriptBlock {& cmd /c \"msiexec.exe /x " + msiFileRemotePath + " /q\"}";
        cmdFilePath = DATA_FOLDER + "uninstallscript.ps1";
        File file = newFile(cmdFilePath, installCmd);
        CommandLine cmdLine = new CommandLine("powershell");
        cmdLine.addArgument("-ExecutionPolicy");
        cmdLine.addArgument("RemoteSigned");
        cmdLine.addArgument(cmdFilePath);
        DefaultExecutor executor = new DefaultExecutor();
        try
        {
            executor.execute(cmdLine);
        }
        catch (IOException e)
        {
            throw new PageOperationException("Unable to launch the installer", e);
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
        logger.info("Uninstalling Transformation in Quiet Mode on " + transfServNodeName);
        return true;
    }

    public static boolean checkTransformationService(String transNodeName) throws IOException
    {
        if(transNodeName == null || transNodeName.isEmpty())
        {
            throw new IllegalArgumentException("Transformation Node name must not be null");
        }
        CommandLine cmdLine = new CommandLine("powershell");
        cmdLine.addArgument("get-service");
        cmdLine.addArgument("-ComputerName");
        cmdLine.addArgument(transNodeName);
        cmdLine.addArgument("-Name");
        cmdLine.addArgument("TransformationServer");
        DefaultExecutor executor = new DefaultExecutor();
        try
        {
            int result = executor.execute(cmdLine);
            if(result == 0)
            {
                return true;
            }
        }
        catch (ExecuteException e)
        {
            logger.info("Transformation service isn't available on " + transNodeName, e);
        }
        return false;
    }
}
