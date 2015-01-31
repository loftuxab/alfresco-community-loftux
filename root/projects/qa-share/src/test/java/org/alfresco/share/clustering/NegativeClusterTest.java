/*
 * Copyright (C) 2005-2013s Alfresco Software Limited.
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

package org.alfresco.share.clustering;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.systemsummary.AdminConsoleLink;
import org.alfresco.po.share.systemsummary.RepositoryServerClusteringPage;
import org.alfresco.po.share.systemsummary.SystemSummaryPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.FtpUtil;
import org.alfresco.share.util.JmxUtils;
import org.alfresco.share.util.RemoteUtil;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.TelnetUtil;
import org.alfresco.test.FailedTestListener;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Sergey Kardash
 */
@Listeners(FailedTestListener.class)
public class NegativeClusterTest extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(NegativeClusterTest.class);
    private static String node1Url;
    private static String node2Url;
    protected static String jmxGobalProperties = "Alfresco:Name=GlobalProperties";
    protected static String jmxDirLicense = "dir.license.external";
    private static final String regexUrl = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(:\\d{1,5})?";
    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
    private static final Pattern DOMAIN_PATTERN = Pattern.compile("\\w+(\\.\\w+)*(\\.\\w{2,})");
    final String fileName1 = "File_" + getRandomString(10) + "_1.txt";
    final File file1 = getFileWithSize(fileName1, 1024);
    private static final String TEST_TXT_FILE = "Test2.txt";
    String fileLocation = DATA_FOLDER + TEST_TXT_FILE;
    File fileTXT = newFile(fileLocation, TEST_TXT_FILE);

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();

        // testUser = getUserNameFreeDomain(testName);
        logger.info("Starting Tests: " + testName);
        file1.deleteOnExit();

        // String[] testUserInfo = new String[] { testUser };
        // CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        /*
         * String shareJmxPort = getAlfrescoServerProperty("Alfresco:Type=Configuration,Category=sysAdmin,id1=default", "share.port").toString();
         * boolean clustering_enabled_jmx = (boolean) getAlfrescoServerProperty("Alfresco:Name=Cluster,Tool=Admin", "ClusteringEnabled");
         * if (clustering_enabled_jmx)
         * {
         * Object clustering_url = getAlfrescoServerProperty("Alfresco:Name=Cluster,Tool=Admin", "ClusterMembers");
         * try
         * {
         * CompositeDataSupport compData = (CompositeDataSupport) ((TabularDataSupport) clustering_url).values().toArray()[0];
         * String clusterIP = compData.values().toArray()[0] + ":" + shareJmxPort;
         * CompositeDataSupport compData2 = (CompositeDataSupport) ((TabularDataSupport) clustering_url).values().toArray()[1];
         * String clusterIP2 = compData2.values().toArray()[0] + ":" + shareJmxPort;
         * node1Url = shareUrl.replace(shareIP, clusterIP);
         * node2Url = shareUrl.replace(shareIP, clusterIP2);
         * }
         * catch (Throwable ex)
         * {
         * throw new SkipException("Skipping as pre-condition step(s) fail");
         * }
         * }
         */
        SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();

        RepositoryServerClusteringPage clusteringPage = sysSummaryPage.openConsolePage(AdminConsoleLink.RepositoryServerClustering).render();

        Assert.assertTrue(clusteringPage.isClusterEnabled(), "Cluster isn't enabled");

        List<String> clusterMembers = clusteringPage.getClusterMembers();
        if (clusterMembers.size() >= 2)
        {
            node1Url = shareUrl.replaceFirst(regexUrl, clusterMembers.get(0) + ":" + nodePort);
            node2Url = shareUrl.replaceFirst(regexUrl, clusterMembers.get(1) + ":" + nodePort);
        }
        else
        {
            throw new PageOperationException("Number of cluster members is less than two");
        }
    }

    private static String getAddress(String url)
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

    private void checkClusterNumbers()
    {

        List<String> clusterMembers;
        long before = System.currentTimeMillis();

        do
        {
            SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();

            RepositoryServerClusteringPage clusteringPage = sysSummaryPage.openConsolePage(AdminConsoleLink.RepositoryServerClustering).render();

            Assert.assertTrue(clusteringPage.isClusterEnabled(), "Cluster isn't enabled");

            clusterMembers = clusteringPage.getClusterMembers();
            if (clusterMembers.size() >= 2)
            {
                node1Url = shareUrl.replaceFirst(regexUrl, clusterMembers.get(0) + ":" + nodePort);
                node2Url = shareUrl.replaceFirst(regexUrl, clusterMembers.get(1) + ":" + nodePort);
                if (logger.isDebugEnabled())
                {
                    logger.debug("Number of cluster members is more than one");
                }
                break;
            }
            else
            {
                webDriverWait(drone, 5000);
                drone.refresh();
                clusterMembers = clusteringPage.getClusterMembers();
                logger.info("Number of cluster members is less than two");
            }
            logger.info((System.currentTimeMillis() - before) / 1000 + " of " + 200 + " " + " maximum seconds passed after begin check cluster hosts");
        }
        while (clusterMembers.size() < 2 || ((System.currentTimeMillis() - before) * 0.001) < 200);
    }

    private void setSshHost(String sshHostUrl)
    {
        sshHost = getAddress(sshHostUrl);
    }

    /**
     * Test - AONE-9322:Verify Clustering if the network connection is dropped
     * <ul>
     * <li>Server A and server B are working in cluster</li>
     * <li>Switch off your network connection of server B</li>
     * <li>Network connection is switched off</li>
     * <li>wait 5 minute and switch it on</li>
     * <li>Network connection is switched on again in 5 minute</li>
     * <li>Verify that cluster works correctly</li>
     * <li>Cluster works correctly without errors</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 1000000)
    public void AONE_9322() throws Exception
    {

        String mainFolder = "Alfresco";
        String path = mainFolder + "/";
        long fileSize = fileTXT.length();
        String folderName = getFolderName(testName) + getRandomString(5);
        final String folderPath = path + folderName + "/";
        ExecutorService executorService = java.util.concurrent.Executors.newCachedThreadPool();

        try
        {
            // setSshHost(node2Url);
            checkClusterNumbers();

            dronePropertiesMap.get(drone).setShareUrl(node2Url);

            if (FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path))
            {
                FtpUtil.DeleteSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, mainFolder);
            }
            assertTrue(FtpUtil.createSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), "Can't create " + folderName + " folder");
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), folderName + " folder is not exist.");

            // Set iptables rule
            Thread iptablesThread1 = new Thread(new Runnable()
            {
                public void run()
                {
                    logger.info("2. Set iptables rule");
                    RemoteUtil.applyIptablesAllPorts();

                }
            });

            List<Future> futures = new ArrayList<>();

            // Switch off your network connection of server B
            setSshHost(node2Url);
            futures.add(executorService.submit(iptablesThread1));
            futures.get(0).get();
            Assert.assertFalse(iptablesThread1.isAlive(), "iptables isn't applied");
            logger.info("3. iptables applied");

            logger.info("wait 20 sec");
            webDriverWait(drone, 20000);

            logger.info("4. Check port " + ftpPort + " for node " + node2Url);
            assertFalse(TelnetUtil.connectServer(node2Url, ftpPort), "Check port " + ftpPort + " for node " + node2Url + " is accessible");

            for (Future future : futures)
            {
                try
                {
                    if (!future.isDone())
                        future.get();
                }
                catch (InterruptedException | ExecutionException e)
                {
                    logger.error(e);
                }
            }
            executorService.shutdown();

            // wait 5 minute and switch it on
            RemoteUtil.waitForAlfrescoStartup(node2Url, 300);

            // Network connection is switched on again in 5 minute
            setSshHost(node2Url);
            RemoteUtil.removeIpTables(node1Url);
            logger.info("Remove iptables");

            // Verify that cluster works correctly
            checkClusterNumbers();
            assertTrue(TelnetUtil.connectServer(node2Url, ftpPort), "Check port " + ftpPort + " for node " + node2Url + "isn't accessible");

            assertTrue(FtpUtil.uploadContent(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileTXT, folderPath), "Can't upload " + TEST_TXT_FILE
                    + " content A direct to node 1 via FTP");

            // Cluster works correctly without errors
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, TEST_TXT_FILE, folderPath), TEST_TXT_FILE
                    + " content A is not exist. node 1");
            assertTrue(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, TEST_TXT_FILE, folderPath), TEST_TXT_FILE
                    + " content A is not exist. node 2");
            assertEquals(FtpUtil.getContentSize(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, path + folderName, TEST_TXT_FILE), fileSize, "Document "
                    + TEST_TXT_FILE + " isn't uploaded correctly");
            assertEquals(FtpUtil.getContentSize(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, path + folderName, TEST_TXT_FILE), fileSize, "Document "
                    + TEST_TXT_FILE + " isn't uploaded correctly");

        }
        finally
        {
            // Remove folder with documents
            setSshHost(node2Url);
            RemoteUtil.removeIpTables(node1Url);
            executorService.shutdown();
            assertTrue(FtpUtil.DeleteSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, mainFolder), "Can't delete " + folderName + " folder");
            assertFalse(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), folderName + " folder is exist.");

        }

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE-9323:Verify Cluster if the network connection is dropped and uploading a file on server A
     * <ul>
     * <li>Server A and server B are working in cluster</li>
     * <li>Switch off your network connection of server B</li>
     * <li>Upload a file on server A</li>
     * <li>File is uploaded</li>
     * <li>Switch it on network connection of server B</li>
     * <li>Network connection is Switched it on</li>
     * <li>Verify that cluster works correctly</li>
     * <li>Cluster works correctly without errors</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 1000000, dependsOnMethods = "AONE_9322", alwaysRun = true)
    public void AONE_9323() throws Exception
    {

        String mainFolder = "Alfresco";
        String path = mainFolder + "/";
        long fileSize = fileTXT.length();
        String folderName = getFolderName(testName) + getRandomString(5);
        final String folderPath = path + folderName + "/";
        ExecutorService executorService = java.util.concurrent.Executors.newCachedThreadPool();

        try
        {
            checkClusterNumbers();
            // setSshHost(node2Url);

            dronePropertiesMap.get(drone).setShareUrl(node2Url);

            if (FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path))
            {
                FtpUtil.DeleteSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, mainFolder);
            }
            assertTrue(FtpUtil.createSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), "Can't create " + folderName + " folder");
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), folderName + " folder is not exist.");

            // Set iptables rule
            Thread iptablesThread1 = new Thread(new Runnable()
            {
                public void run()
                {
                    logger.info("2. Set iptables rule");
                    // Take node 2 down
                    RemoteUtil.applyIptablesAllPorts();

                }
            });

            List<Future> futures = new ArrayList<>();

            // Switch off your network connection of server B
            setSshHost(node2Url);
            futures.add(executorService.submit(iptablesThread1));
            futures.get(0).get();
            Assert.assertFalse(iptablesThread1.isAlive(), "iptables isn't applied");
            logger.info("3. iptables applied");

            // logger.info("wait 20 sec");
            webDriverWait(drone, 20000);

            logger.info("4. Check port " + ftpPort + " for node " + node2Url);
            assertFalse(TelnetUtil.connectServer(node2Url, ftpPort), "Check port " + ftpPort + " for node " + node2Url + " is accessible");

            for (Future future : futures)
            {
                try
                {
                    if (!future.isDone())
                        future.get();
                }
                catch (InterruptedException | ExecutionException e)
                {
                    logger.error(e);
                }
            }
            executorService.shutdown();

            // Upload a file on server A
            assertTrue(TelnetUtil.connectServer(node1Url, ftpPort), "Check port " + ftpPort + " for node " + node2Url + "isn't accessible");

            assertTrue(FtpUtil.uploadContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileTXT, folderPath), "Can't upload " + TEST_TXT_FILE
                    + " content A direct to node 1 via FTP");

            // Network connection is switched on again in 5 minute
            setSshHost(node2Url);
            RemoteUtil.removeIpTables(node1Url);
            logger.info("Remove iptables");

            // Verify that cluster works correctly
            checkClusterNumbers();

            // Cluster works correctly without errors
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, TEST_TXT_FILE, folderPath), TEST_TXT_FILE
                    + " content A is not exist. node 1");
            assertTrue(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, TEST_TXT_FILE, folderPath), TEST_TXT_FILE
                    + " content A is not exist. node 2");
            assertEquals(FtpUtil.getContentSize(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, path + folderName, TEST_TXT_FILE), fileSize, "Document "
                    + TEST_TXT_FILE + " isn't uploaded correctly");
            assertEquals(FtpUtil.getContentSize(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, path + folderName, TEST_TXT_FILE), fileSize, "Document "
                    + TEST_TXT_FILE + " isn't uploaded correctly");

        }
        finally
        {
            // Remove folder with documents
            setSshHost(node2Url);
            RemoteUtil.removeIpTables(node1Url);
            executorService.shutdown();
            assertTrue(FtpUtil.DeleteSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, mainFolder), "Can't delete " + folderName + " folder");
            assertFalse(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), folderName + " folder is exist.");

        }

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE-9324:Verify Cluster if the network connection is dropped and uploading a file on server A
     * <ul>
     * <li>Server A and server B are working in cluster</li>
     * <li>Switch off your network connection of server B</li>
     * <li>Upload a file on server A</li>
     * <li>File is uploaded</li>
     * <li>Stop server B</li>
     * <li>Switch it on network connection of server B</li>
     * <li>Network connection is Switched it on</li>
     * <li>Start server B</li>
     * <li>Verify that cluster works correctly</li>
     * <li>Cluster works correctly without errors</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 1000000, dependsOnMethods = "AONE_9323", alwaysRun = true)
    public void AONE_9324() throws Exception
    {

        String mainFolder = "Alfresco";
        String path = mainFolder + "/";
        long fileSize = fileTXT.length();
        String folderName = getFolderName(testName) + getRandomString(5);
        final String folderPath = path + folderName + "/";
        ExecutorService executorService = java.util.concurrent.Executors.newCachedThreadPool();
        String alfrescoPath = JmxUtils.getAlfrescoServerProperty(node2Url, jmxGobalProperties, jmxDirLicense).toString();
        final String[] serverDB = new String[1];

        try
        {
            checkClusterNumbers();
            // setSshHost(node2Url);

            dronePropertiesMap.get(drone).setShareUrl(node1Url);

            if (FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path))
            {
                FtpUtil.DeleteSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, mainFolder);
            }
            assertTrue(FtpUtil.createSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), "Can't create " + folderName + " folder");
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), folderName + " folder is not exist.");

            // Set iptables rule
            Thread iptablesThread1 = new Thread(new Runnable()
            {
                public void run()
                {
                    logger.info("2. Set iptables rule");
                    // Take node 2 down
                    RemoteUtil.applyIptables(node1Url);
                    RemoteUtil.applyIptables(shareUrl);
                    try
                    {
                        String dbURL = JmxUtils.getAlfrescoServerProperty(node1Url, "Alfresco:Name=DatabaseInformation", "URL").toString();
                        serverDB[0] = JmxUtils.getAddress(dbURL);
                        RemoteUtil.applyIptables(serverDB[0]);
                    }
                    catch (Exception e)
                    {
                        logger.info("Connection failed to jmx");
                    }
                    logger.info("Set iptables rule");

                }
            });

            List<Future> futures = new ArrayList<>();

            // Switch off your network connection of server B
            setSshHost(node2Url);
            futures.add(executorService.submit(iptablesThread1));
            futures.get(0).get();
            Assert.assertFalse(iptablesThread1.isAlive(), "iptables isn't applied");
            logger.info("3. iptables applied");

            RemoteUtil.waitForAlfrescoShutdown(node2Url, 100);

            for (Future future : futures)
            {
                try
                {
                    if (!future.isDone())
                        future.get();
                }
                catch (InterruptedException | ExecutionException e)
                {
                    logger.error(e);
                }
            }
            executorService.shutdown();

            // Upload a file on server A
            assertTrue(TelnetUtil.connectServer(node1Url, ftpPort), "Check port " + ftpPort + " for node " + node2Url + "isn't accessible");

            assertTrue(FtpUtil.uploadContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileTXT, folderPath), "Can't upload " + TEST_TXT_FILE
                    + " content A direct to node 1 via FTP");

            // Stop server B
            logger.info("4. Stop alfresco");
            RemoteUtil.stopAlfresco(alfrescoPath);

            // Server B is stopped
            logger.info("5. Wait alfresco stopped");
            RemoteUtil.waitForAlfrescoShutdown(node2Url, 100);

            // Network connection is switched on again
            setSshHost(node2Url);
            RemoteUtil.removeIpTables(node1Url);
            logger.info("6. Remove iptables");

            // start the server B
            RemoteUtil.startAlfresco(alfrescoPath);

            logger.info("7. Wait alfresco start");
            RemoteUtil.waitForAlfrescoStartup(node2Url, 2000);

            // Verify that cluster works correctly
            checkClusterNumbers();

            // Cluster works correctly without errors
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, TEST_TXT_FILE, folderPath), TEST_TXT_FILE
                    + " content A is not exist. node 1");
            assertTrue(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, TEST_TXT_FILE, folderPath), TEST_TXT_FILE
                    + " content A is not exist. node 2");
            assertEquals(FtpUtil.getContentSize(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, path + folderName, TEST_TXT_FILE), fileSize, "Document "
                    + TEST_TXT_FILE + " isn't uploaded correctly");
            assertEquals(FtpUtil.getContentSize(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, path + folderName, TEST_TXT_FILE), fileSize, "Document "
                    + TEST_TXT_FILE + " isn't uploaded correctly");

        }
        finally
        {
            // Remove folder with documents
            setSshHost(node2Url);
            RemoteUtil.removeIpTables(node1Url);
            executorService.shutdown();
            assertTrue(FtpUtil.DeleteSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, mainFolder), "Can't delete " + folderName + " folder");
            assertFalse(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), folderName + " folder is exist.");

        }

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE-9325:Verify Cluster if the network connection is dropped when uploading a big-sized file
     * <ul>
     * <li>Upload a file of 1 GB on server A and at the moment switch off your network connection of server B</li>
     * <li>Network connection is switched off. The file is being uploaded.</li>
     * <li>When the file is uploaded switch it on network connection of server B</li>
     * <li>Verify that cluster works correctly</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 1000000, dependsOnMethods = "AONE_9324", alwaysRun = true)
    public void AONE_9325() throws Exception
    {

        String mainFolder = "Alfresco";
        String path = mainFolder + "/";
        long fileSize = file1.length();
        String folderName = getFolderName(testName) + getRandomString(5);
        final String folderPath = path + folderName + "/";
        ExecutorService executorService = java.util.concurrent.Executors.newCachedThreadPool();

        try
        {
            checkClusterNumbers();
            // setSshHost(node2Url);
            logger.info("Set for ssh host: " + node1Url);

            dronePropertiesMap.get(drone).setShareUrl(node1Url);

            if (FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path))
            {
                FtpUtil.DeleteSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, mainFolder);
            }
            assertTrue(FtpUtil.createSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), "Can't create " + folderName + " folder");
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), folderName + " folder is not exist.");

            Thread uploadThread1 = new Thread(new Runnable()
            {
                public void run()
                {
                    logger.info("1. Upload file start for " + node1Url);
                    // Start upload a file of 1 GB on node 1
                    assertTrue(FtpUtil.uploadContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, file1, folderPath), "Can't upload " + fileName1
                            + " content A of 1 GB direct to node 1 via FTP");
                    logger.info("4. Upload file end for " + node1Url);

                }
            });

            // Set iptables rule
            Thread iptablesThread1 = new Thread(new Runnable()
            {
                public void run()
                {
                    logger.info("2. Set iptables rule");
                    RemoteUtil.applyIptablesAllPorts();

                }
            });

            List<Future> futures = new ArrayList<>();

            // Start upload a file of 1 GB on node 1
            futures.add(executorService.submit(uploadThread1));

            // begin upload
            webDriverWait(drone, 15000);

            // Network connection is switched off
            setSshHost(node2Url);
            futures.add(executorService.submit(iptablesThread1));

            futures.get(1).get();
            Assert.assertFalse(iptablesThread1.isAlive(), "iptables isn't applied");

            logger.info("3. Check port " + ftpPort + " for node " + node2Url);
            assertFalse(TelnetUtil.connectServer(node2Url, ftpPort), "Check port " + ftpPort + " for node " + node2Url + " is accessible");

            // The file is being uploaded
            for (Future future : futures)
            {
                try
                {
                    if (!future.isDone())
                        future.get();
                }
                catch (InterruptedException | ExecutionException e)
                {
                    logger.error(e);
                }
            }
            executorService.shutdown();

            // Network connection is switched on again
            setSshHost(node2Url);
            RemoteUtil.removeIpTables(node1Url);
            logger.info("Remove iptables");

            logger.info("5. Check port " + ftpPort + " for node " + node2Url);

            checkClusterNumbers();
            assertTrue(TelnetUtil.connectServer(node2Url, ftpPort), "Check port " + ftpPort + " for node " + node2Url + "isn't accessible");
            assertTrue(TelnetUtil.connectServer(node1Url, ftpPort), "Check port " + ftpPort + " for node " + node1Url + "isn't accessible");

            // Cluster works correctly without errors
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName1, folderPath), fileName1 + " content A is not exist. node 1");
            assertTrue(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName1, folderPath), fileName1 + " content A is not exist. node 2");
            assertEquals(FtpUtil.getContentSize(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, path + folderName, fileName1), fileSize, "Document " + fileName1
                    + " isn't uploaded correctly for server:" + node1Url);
            assertEquals(FtpUtil.getContentSize(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, path + folderName, fileName1), fileSize, "Document " + fileName1
                    + " isn't uploaded correctly for server:" + node2Url);

        }
        finally
        {
            logger.info("Finally actions");
            // Remove folder with documents
            setSshHost(node2Url);
            RemoteUtil.removeIpTables(node1Url);
            executorService.shutdown();
            assertTrue(FtpUtil.DeleteSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, mainFolder), "Can't delete " + folderName + " folder");
            assertFalse(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), folderName + " folder is exist.");
        }
    }

    /**
     * Test - AONE-9326:Verify Cluster if the network connection is dropped when uploading a big-sized file
     * <ul>
     * <li>Upload a file of 1 GB on server A and at the moment switch off your network connection of server A</li>
     * <li>Network connection is switched off. The file is being uploaded.</li>
     * <li>When the file is uploaded switch it on network connection of server B</li>
     * <li>Verify that cluster works correctly</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 1000000, dependsOnMethods = "AONE_9325", alwaysRun = true)
    public void AONE_9326() throws Exception
    {

        String mainFolder = "Alfresco";
        String path = mainFolder + "/";
        String folderName = getFolderName(testName) + getRandomString(5);
        final String folderPath = path + folderName + "/";
        ExecutorService executorService = java.util.concurrent.Executors.newCachedThreadPool();

        try
        {
            checkClusterNumbers();
            // setSshHost(node1Url);
            logger.info("Set for ssh host: " + node1Url);

            dronePropertiesMap.get(drone).setShareUrl(node1Url);

            if (FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path))
            {
                FtpUtil.DeleteSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, mainFolder);
            }
            assertTrue(FtpUtil.createSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), "Can't create " + folderName + " folder");
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), folderName + " folder is not exist.");

            Thread uploadThread1 = new Thread(new Runnable()
            {
                public void run()
                {
                    logger.info("1. Upload file start for " + node1Url);
                    // Start upload a file of 1 GB on node 1
                    assertTrue(FtpUtil.uploadContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, file1, folderPath), "Can't upload " + fileName1
                            + " content A of 1 GB direct to node 1 via FTP");
                    logger.info("4. Upload file end for " + node1Url);

                }
            });

            // Set iptables rule
            Thread iptablesThread1 = new Thread(new Runnable()
            {
                public void run()
                {
                    logger.info("2. Set iptables rule");
                    RemoteUtil.applyIptablesAllPorts();

                }
            });

            List<Future> futures = new ArrayList<>();

            // Start upload a file of 1 GB on node 1
            futures.add(executorService.submit(uploadThread1));

            // begin upload
            // webDriverWait(drone, 15000);
            int count = 0;
            while (!FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName1, folderPath) && count < 10)
            {
                checkClusterNumbers();
                count++;
                logger.info("wait upload begin count:" + count);
            }

            // Network connection is switched off
            setSshHost(node1Url);
            futures.add(executorService.submit(iptablesThread1));

            futures.get(1).get();
            Assert.assertFalse(iptablesThread1.isAlive(), "iptables isn't applied");

            logger.info("3. Check port " + ftpPort + " for node " + node1Url);
            assertFalse(TelnetUtil.connectServer(node1Url, ftpPort), "Check port " + ftpPort + " for node " + node1Url + " is accessible");

            // The file is being uploaded
            for (Future future : futures)
            {
                try
                {
                    if (!future.isDone())
                        future.get();
                }
                catch (InterruptedException | ExecutionException e)
                {
                    logger.error(e);
                }
            }
            executorService.shutdown();

            // Network connection is switched on again
            setSshHost(node1Url);
            RemoteUtil.removeIpTables(node2Url);
            logger.info("Remove iptables");

            logger.info("5. Check port " + ftpPort + " for node " + node1Url);

            checkClusterNumbers();
            assertTrue(TelnetUtil.connectServer(node2Url, ftpPort), "Check port " + ftpPort + " for node " + node2Url + "isn't accessible");
            assertTrue(TelnetUtil.connectServer(node1Url, ftpPort), "Check port " + ftpPort + " for node " + node1Url + "isn't accessible");

            // Cluster works correctly without errors
            // TODO in accordance with issue MNT-12874
            // Check that each node can see the document
            assertFalse(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName1, folderPath), fileName1
                    + " content A is exist. node 1. MNT-12874");
            assertFalse(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName1, folderPath), fileName1
                    + " content A is exist. node 2. MNT-12874");

        }
        finally
        {
            logger.info("Finally actions");
            // Remove folder with documents
            setSshHost(node1Url);
            RemoteUtil.removeIpTables(node2Url);
            executorService.shutdown();
            assertTrue(FtpUtil.DeleteSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, mainFolder), "Can't delete " + folderName + " folder");
            assertFalse(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), folderName + " folder is exist.");
        }
    }
}