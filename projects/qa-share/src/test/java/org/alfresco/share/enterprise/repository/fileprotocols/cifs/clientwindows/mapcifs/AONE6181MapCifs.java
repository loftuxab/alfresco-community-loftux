/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
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

package org.alfresco.share.enterprise.repository.fileprotocols.cifs.clientwindows.mapcifs;

import org.alfresco.explorer.WindowsExplorer;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.CifsUtil;
import org.alfresco.share.util.JmxUtils;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.test.FailedTestListener;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sergey Kardash
 */

@Listeners(FailedTestListener.class)
public class AONE6181MapCifs extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(AONE6180MapCifs.class);

    String testName = "AONE6181MapCifs";
    WindowsExplorer explorer = new WindowsExplorer();
    private static final String regexUrlIP = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})";
    private static final String regexUrlWithPort = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(:\\d{1,5})?";
    private static String ip;
    String cifsServerNameJMX;
    String initialCifsServerName;
    String initialCifsDomainName;
    String configurationFileServers = "Alfresco:Type=Configuration,Category=fileServers,id1=default";
    String cifsServerName = "cifs.serverName";
    String cifsDomain = "cifs.domain";
    String domain = "QALAB";

    @Override
    @BeforeClass(groups = "setup")
    public void setup() throws Exception
    {
        super.setup();

        ip = getAddress(networkPath);
        networkPath = networkPath.replaceFirst(regexUrlWithPort, ip);
        if (networkPath.contains("alfresco\\"))
        {
            networkPath = networkPath.replace("alfresco\\", "alfresco");
        }
        logger.info("Network path: " + networkPath);
    }

    /**
     * Precondition Test - AONE-6181:Verify connect to CIFS using IP server
     * <ul>
     * <li>Changes: %tomcat%/shared/classes/alfresco-global.properties</li>
     * <li>cifs.serverName=my_host_name</li>
     * <li>cifs.domain=my_domain</li>
     * <li>Mentioned above Actions were released via JMX</li>
     * </ul>
     */
    @BeforeMethod(groups = "setup")
    public void precondition() throws Exception
    {
        Process process = Runtime.getRuntime().exec("cmd /c start /WAIT net use * /d /y");
        process.waitFor();

        cifsServerNameJMX = JmxUtils.getAlfrescoServerProperty(ip, "Alfresco:Name=FileServerConfig", "CIFSServerName").toString();
        initialCifsServerName = JmxUtils.getAlfrescoServerProperty(ip, configurationFileServers, cifsServerName).toString();
        initialCifsDomainName = JmxUtils.getAlfrescoServerProperty(ip, configurationFileServers, cifsDomain).toString();

        String tempCifsServerNameJMX = cifsServerNameJMX.substring(0, cifsServerNameJMX.length() - 1);
        JmxUtils.setAlfrescoServerProperty(ip, configurationFileServers, cifsServerName, tempCifsServerNameJMX);
        JmxUtils.setAlfrescoServerProperty(ip, configurationFileServers, cifsDomain, domain);
        JmxUtils.invokeAlfrescoServerProperty(ip, configurationFileServers, "stop");
        JmxUtils.invokeAlfrescoServerProperty(ip, configurationFileServers, "start");
        Assert.assertTrue(JmxUtils.getAlfrescoServerProperty(ip, configurationFileServers, cifsServerName).toString().equals(tempCifsServerNameJMX),
                "Property " + cifsServerName + " isn't changed");
        Assert.assertTrue(JmxUtils.getAlfrescoServerProperty(ip, configurationFileServers, cifsDomain).toString().equals(domain), "Property " + cifsDomain
                + " isn't changed");

    }

    /**
     * Test - AONE-6181:Verify connect to CIFS using IP server
     * <ul>
     * <li>Use commands \\184.168.80.59 OR \\184.168.80.59\alfresco</li>
     * <li>Alfresco window is opened</li>
     * <li>Verify that all files and folders are displayed</li>
     * </ul>
     */
    @AlfrescoTest(testlink = "AONE-6181")
    @Test(groups = { "CIFSWindowsClient", "EnterpriseOnly" }, timeOut = 600000)
    public void AONE_6181() throws Exception
    {
        List<String> list;
        list = executeCommandGetConsoleText("cmd /c net use " + networkPath + " /user:" + ADMIN_USERNAME + " " + ADMIN_PASSWORD);
        Assert.assertTrue(checkString(list, "The command completed successfully"), "Expected text isn't found");
        list = executeCommandGetConsoleText("net use");
        Assert.assertTrue(checkString(list, networkPath), "Expected text isn't found");

        // Use commands \\184.168.80.59 OR \\184.168.80.59\alfresco
        executeCommandGetConsoleText("cmd /c start /WAIT " + networkPath);

        // Alfresco window is opened
        explorer.openWindowsExplorer();
        explorer.getAbstractUtil().waitForWindow("alfresco");
        explorer.closeExplorer();
        Runtime.getRuntime().exec("cmd /c start /WAIT net use " + networkPath + " /DELETE /y");

        explorer.activateApplicationWindow("alfresco");
        String[] allObjectsWindow = explorer.getAbstractUtil().getLdtp().getObjectList();
        explorer.closeExplorer();

        // Verify that all files and folders are displayed
        Assert.assertTrue(Arrays.asList(allObjectsWindow).contains("lstDataDictionary"), "Expected folder isn't found");
        Assert.assertTrue(Arrays.asList(allObjectsWindow).contains("lstGuestHome"), "Expected folder isn't found");
    }

    @AfterMethod(groups = "teardown")
    public void endTest() throws Exception
    {

        Runtime.getRuntime().exec("cmd /c start /WAIT net use * /d /y");
        // Runtime.getRuntime().exec("cmd /c start /WAIT net use \\\\" + ip + "\\alfresco /DELETE");
        String cifsPath = "\\Sites\\";
        if (CifsUtil.checkDirOrFileNotExists(7, 200, networkDrive + cifsPath))
        {
            logger.info("--------Unmapping succesfull " + ADMIN_USERNAME);
        }
        else
        {
            logger.error("--------Unmapping was not done correctly " + ADMIN_USERNAME);
        }

        JmxUtils.invokeAlfrescoServerProperty(ip, configurationFileServers, "revert");
        JmxUtils.invokeAlfrescoServerProperty(ip, configurationFileServers, "start");
        logger.info("Starting Test: " + testName);

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

    private boolean checkString(List<String> list, String searchString)
    {
        for (String str : list)
        {
            if (str.trim().contains(searchString))
            {
                logger.info("String: " + str + " contains string " + searchString);
                return true;
            }
        }
        return false;
    }

    private List<String> executeCommandGetConsoleText(String command) throws IOException
    {
        List<String> list = new ArrayList<>();
        BufferedReader in = null;
        try
        {
            Process p = Runtime.getRuntime().exec(command);
            InputStream s = p.getInputStream();
            in = new BufferedReader(new InputStreamReader(s));
            String temp;
            while ((temp = in.readLine()) != null)
            {
                list.add(temp);
            }
        }
        catch (Exception e)
        {
            logger.error("Error during attempt getting message from cmd console: ", e);
        }
        finally
        {
            if (in != null)
                in.close();
        }
        return list;
    }

}
