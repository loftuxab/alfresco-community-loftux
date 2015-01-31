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

import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.systemsummary.AdminConsoleLink;
import org.alfresco.po.share.systemsummary.RepositoryServerClusteringPage;
import org.alfresco.po.share.systemsummary.SystemSummaryPage;
import org.alfresco.share.util.*;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.*;

/**
 * @author Sergey Kardash
 */
@Listeners(FailedTestListener.class)
public class NegativeClusterTest2 extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(NegativeClusterTest2.class);
    private static String node1Url;
    private static String node2Url;
    protected static String jmxGobalProperties = "Alfresco:Name=GlobalProperties";
    protected static String jmxDirLicense = "dir.license.external";
    private static final String regexUrl = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(:\\d{1,5})?";
    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
    private static final Pattern DOMAIN_PATTERN = Pattern.compile("\\w+(\\.\\w+)*(\\.\\w{2,})");
    final String fileName1 = "File_" + getRandomString(10) + "_1.txt";
    final File file1 = getFileWithSize(fileName1, 1024);
    long fileSize = file1.length();
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
     * Test - AONE-9319:Stop the server B when uploading a big-sized file on server A
     * <ul>
     * <li>Upload a file of 1 GB on server A and at the moment stop the server B</li>
     * <li>Server B is stopped. The file is being uploaded</li>
     * <li>When the file is uploaded start the server B</li>
     * <li>The server B is started succesfully. A cluster works correctly</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 1000000)
    public void AONE_9319() throws Exception
    {

        String mainFolder = "Alfresco";
        String path = mainFolder + "/";

        String folderName = getFolderName(testName) + getRandomString(5);
        final String folderPath = path + folderName + "/";
        String alfrescoPath = JmxUtils.getAlfrescoServerProperty(node2Url, jmxGobalProperties, jmxDirLicense).toString();
        ExecutorService executorService = java.util.concurrent.Executors.newCachedThreadPool();

        try
        {
            checkClusterNumbers();
            // setSshHost(node2Url);
            logger.info("Set for ssh host: " + node2Url);

            dronePropertiesMap.get(drone).setShareUrl(node1Url);

            if (FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path))
            {
                FtpUtil.DeleteSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, mainFolder);
            }
            assertTrue(FtpUtil.createSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), "Can't create " + folderName + " folder");
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), folderName + " folder is not exist.");

            Thread uploadThread = new Thread(new Runnable()
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

            List<Future> futures = new ArrayList<>();

            // Start upload a file of 1 GB on node 1
            futures.add(executorService.submit(uploadThread));

            // upload
            // logger.info("wait 10 sec");
            // webDriverWait(drone, 10000);
            int count = 0;
            while (!FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName1, folderPath) && count < 10)
            {
                checkClusterNumbers();
                count++;
                logger.info("wait upload begin count:" + count);
            }

            setSshHost(node2Url);
            // Server B is stopped
            RemoteUtil.stopAlfresco(alfrescoPath);
            logger.info("2. Wait alfresco stopped");

            RemoteUtil.waitForAlfrescoShutdown(node2Url, 1000);

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

            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName1, folderPath), fileName1
                    + " content A of 1 GB is not exist. node 1 " + node1Url);

            assertEquals(FtpUtil.getContentSize(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, path + folderName, fileName1), fileSize, "Document " + fileName1
                    + " isn't uploaded correctly");

            // start the server B
            RemoteUtil.startAlfresco(alfrescoPath);
            logger.info("5. Wait alfresco start");

            RemoteUtil.waitForAlfrescoStartup(node2Url, 2000);

            logger.info("6. Check port " + ftpPort + " for node " + node2Url);

            // checkClusterNumbers();
            assertTrue(TelnetUtil.connectServer(node2Url, ftpPort), "Check port " + ftpPort + " for node " + node2Url + "isn't accessible");

            // Check that each node can see the document
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName1, folderPath), fileName1 + " content A is not exist. node 1");
            assertTrue(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName1, folderPath), fileName1 + " content A is not exist. node 2");

        }
        finally
        {
            logger.info("Finally actions");
            // Remove folder with documents
            if (!TelnetUtil.connectServer(node2Url, ftpPort))
            {
                RemoteUtil.startAlfresco(alfrescoPath);
                logger.info("Wait alfresco start");
                RemoteUtil.waitForAlfrescoStartup(node2Url, 2000);
            }
            executorService.shutdown();
            assertTrue(FtpUtil.DeleteSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, mainFolder), "Can't delete " + folderName + " folder");
            assertFalse(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), folderName + " folder is exist.");

        }
    }

    /**
     * Test - AONE-9320:Stop the server A when uploading a big-sized file on server A
     * <ul>
     * <li>Upload a file of 1 GB on server A and at the moment stop the server A</li>
     * <li>Server A is stopped. The file isn't uploaded</li>
     * <li>Start the server A</li>
     * <li>The server A is started successfully. A cluster works correctly</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 1000000, dependsOnMethods = "AONE_9319", alwaysRun = true)
    public void AONE_9320() throws Exception
    {

        String mainFolder = "Alfresco";
        String path = mainFolder + "/";
        String folderName = getFolderName(testName) + getRandomString(5);
        final String folderPath = path + folderName + "/";
        String alfrescoPath = JmxUtils.getAlfrescoServerProperty(node1Url, jmxGobalProperties, jmxDirLicense).toString();
        ExecutorService executorService = java.util.concurrent.Executors.newCachedThreadPool();

        try
        {
            checkClusterNumbers();
            logger.info("Set for ssh host: " + node1Url);

            dronePropertiesMap.get(drone).setShareUrl(node1Url);

            if (FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path))
            {
                FtpUtil.DeleteSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, mainFolder);
            }
            assertTrue(FtpUtil.createSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), "Can't create " + folderName + " folder");
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), folderName + " folder is not exist.");

            Thread uploadThread = new Thread(new Runnable()
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

            List<Future> futures = new ArrayList<>();

            // Start upload a file of 1 GB on node 1
            futures.add(executorService.submit(uploadThread));

            // upload
            // logger.info("wait 10 sec");
            // webDriverWait(drone, 10000);

            int count = 0;
            while (!FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName1, folderPath) && count < 10)
            {
                checkClusterNumbers();
                count++;
                logger.info("wait upload begin count:" + count);
            }
            setSshHost(node1Url);
            // Server A is stopped
            RemoteUtil.stopAlfresco(alfrescoPath);
            logger.info("2. Wait alfresco stopped");
            RemoteUtil.waitForAlfrescoShutdown(node1Url, 1000);

            logger.info("3. Check port " + ftpPort + " for node " + node1Url);
            assertFalse(TelnetUtil.connectServer(node1Url, ftpPort), "Check port " + ftpPort + " for node " + node1Url + " is accessible");

            // The file isn't uploaded
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

            // start the server A
            RemoteUtil.startAlfresco(alfrescoPath);
            logger.info("5. Wait alfresco start");
            RemoteUtil.waitForAlfrescoStartup(node1Url, 2000);

            logger.info("6. Check port " + ftpPort + " for node " + node2Url);

            // checkClusterNumbers();
            assertTrue(TelnetUtil.connectServer(node1Url, ftpPort), "Check port " + ftpPort + " for node " + node2Url + "isn't accessible");

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
            if (!TelnetUtil.connectServer(node1Url, ftpPort))
            {
                RemoteUtil.startAlfresco(alfrescoPath);
                logger.info("Wait alfresco start");
                RemoteUtil.waitForAlfrescoStartup(node1Url, 2000);
            }
            executorService.shutdown();
            assertTrue(FtpUtil.DeleteSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, mainFolder), "Can't delete " + folderName + " folder");
            assertFalse(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), folderName + " folder is exist.");

        }
    }

    /**
     * Test - AONE-9321:Stop both servers when uploading the big-sized files on this servers
     * <ul>
     * <li>Upload a file of 1 GB on both servers and at the moment stop both servers</li>
     * <li>Servers are stopped. The files aren't uploaded</li>
     * <li>Start both servers</li>
     * <li>Both servers are started successfully. A cluster works correctly</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 1000000, dependsOnMethods = "AONE_9320", alwaysRun = true)
    public void AONE_9321() throws Exception
    {

        String mainFolder = "Alfresco";
        String path = mainFolder + "/";
        String folderName1 = getFolderName(testName) + getRandomString(5);
        String folderName2 = getFolderName(testName) + getRandomString(5);
        final String folderPath1 = path + folderName1 + "/";
        final String folderPath2 = path + folderName2 + "/";
        String alfrescoPath1 = JmxUtils.getAlfrescoServerProperty(node1Url, jmxGobalProperties, jmxDirLicense).toString();
        String alfrescoPath2 = JmxUtils.getAlfrescoServerProperty(node2Url, jmxGobalProperties, jmxDirLicense).toString();
        ExecutorService executorService = java.util.concurrent.Executors.newCachedThreadPool();

        try
        {
            checkClusterNumbers();

            setSshHost(node1Url);
            logger.info("Set for ssh host: " + node1Url);

            dronePropertiesMap.get(drone).setShareUrl(node1Url);

            if (FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName1, path))
            {
                FtpUtil.DeleteSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName1, mainFolder);
            }
            if (FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName2, path))
            {
                FtpUtil.DeleteSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName2, mainFolder);
            }
            assertTrue(FtpUtil.createSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName1, path), "Can't create " + folderName1 + " folder");
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName1, path), folderName1 + " folder is not exist.");

            assertTrue(FtpUtil.createSpace(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName2, path), "Can't create " + folderName2 + " folder");
            assertTrue(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName2, path), folderName2 + " folder is not exist.");

            Thread uploadThread1 = new Thread(new Runnable()
            {
                public void run()
                {
                    logger.info("1. Upload file start for " + node1Url);
                    // Start upload a file of 1 GB on node 1
                    assertTrue(FtpUtil.uploadContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, file1, folderPath1), "Can't upload " + fileName1
                            + " content A of 1 GB direct to node 1 via FTP");
                    logger.info("4. Upload file end for " + node1Url);

                }
            });

            Thread uploadThread2 = new Thread(new Runnable()
            {
                public void run()
                {
                    logger.info("1. Upload file start for " + node2Url);
                    // Start upload a file of 1 GB on node 1
                    assertTrue(FtpUtil.uploadContent(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, file1, folderPath2), "Can't upload " + fileName1
                            + " content A of 1 GB direct to node 1 via FTP");
                    logger.info("4. Upload file end for " + node2Url);

                }
            });

            List<Future> futures = new ArrayList<>();

            // Start upload a file of 1 GB on node 1
            futures.add(executorService.submit(uploadThread1));
            // Start upload a file of 1 GB on node 2
            futures.add(executorService.submit(uploadThread2));

            // upload
            // logger.info("wait 10 sec");
            // webDriverWait(drone, 10000);

            // Server A is stopped
            int count = 0;
            while (!FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName1, folderPath1) && count < 10)
            {
                checkClusterNumbers();
                count++;
                logger.info("wait upload begin count:" + count);
            }

            setSshHost(node1Url);
            RemoteUtil.stopAlfresco(alfrescoPath1);
            // Server B is stopped
            setSshHost(node2Url);
            RemoteUtil.stopAlfresco(alfrescoPath2);

            logger.info("2. Wait alfresco stopped");
            RemoteUtil.waitForAlfrescoShutdown(node2Url, 2000);
            RemoteUtil.waitForAlfrescoShutdown(node1Url, 2000);

            logger.info("3. Check port " + ftpPort + " for node " + node1Url);
            logger.info("3. Check port " + ftpPort + " for node " + node2Url);
            assertFalse(TelnetUtil.connectServer(node1Url, ftpPort), "Check port " + ftpPort + " for node " + node1Url + " is accessible");
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

            // start the server B
            setSshHost(node2Url);
            RemoteUtil.startAlfresco(alfrescoPath2);
            // start the server B
            setSshHost(node1Url);
            RemoteUtil.startAlfresco(alfrescoPath1);
            logger.info("5. Wait alfresco start");
            RemoteUtil.waitForAlfrescoStartup(node2Url, 2000);
            RemoteUtil.waitForAlfrescoStartup(node1Url, 2000);

            logger.info("6. Check port " + ftpPort + " for node " + node2Url);

            // checkClusterNumbers();
            assertTrue(TelnetUtil.connectServer(node2Url, ftpPort), "Check port " + ftpPort + " for node " + node2Url + "isn't accessible");
            assertTrue(TelnetUtil.connectServer(node1Url, ftpPort), "Check port " + ftpPort + " for node " + node1Url + "isn't accessible");

            // TODO in accordance with issue MNT-12874
            // Check that each node can see the document
            assertFalse(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName1, folderPath1), fileName1
                    + " content A is exist. node 1. MNT-12874");
            assertFalse(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName1, folderPath2), fileName1
                    + " content A is exist. node 2. MNT-12874");

        }
        finally
        {
            logger.info("Finally actions");
            // Remove folder with documents
            if (!TelnetUtil.connectServer(node2Url, ftpPort))
            {
                setSshHost(node2Url);
                RemoteUtil.startAlfresco(alfrescoPath2);
                setSshHost(node1Url);
                RemoteUtil.startAlfresco(alfrescoPath1);
                logger.info("Wait alfresco start");
                RemoteUtil.waitForAlfrescoStartup(node2Url, 2000);
                RemoteUtil.waitForAlfrescoStartup(node1Url, 2000);
            }
            executorService.shutdown();
            assertTrue(FtpUtil.DeleteSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName1, mainFolder), "Can't delete " + folderName1 + " folder");
            assertFalse(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName1, path), folderName1 + " folder is exist.");
            assertTrue(FtpUtil.DeleteSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName2, mainFolder), "Can't delete " + folderName2 + " folder");
            assertFalse(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName2, path), folderName2 + " folder is exist.");
        }
    }

}