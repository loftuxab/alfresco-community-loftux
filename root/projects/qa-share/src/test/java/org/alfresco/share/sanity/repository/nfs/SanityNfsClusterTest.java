/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.share.sanity.repository.nfs;

import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.systemsummary.AdminConsoleLink;
import org.alfresco.po.share.systemsummary.RepositoryServerClusteringPage;
import org.alfresco.po.share.systemsummary.SystemSummaryPage;
import org.alfresco.share.util.*;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Created by Olga Lokhach
 */
public class SanityNfsClusterTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(SanityNfsClusterTest.class);
    private static String node1Url;
    private static String node2Url;
    protected static String jmxGobalProperties = "Alfresco:Name=GlobalProperties";
    protected static String jmxDirLicense = "dir.license.external";
    private static final String regexUrl = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(:\\d{1,5})?";
    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
    private static final Pattern DOMAIN_PATTERN = Pattern.compile("\\w+(\\.\\w+)*(\\.\\w{2,})");
    final String bigFileName = "File_" + getRandomString(10) + "_1.txt";
    final File bigFile = getFileWithSize(bigFileName, 10);
    private static String testUser;
    private static String folderName;
    private static String pathToNFS = "/tmp/alf/";
    private static String remotePath;

    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
        testUser = testName.toLowerCase();
        bigFile.deleteOnExit();
        folderName = "NFS_" + getRandomString(3);
        remotePath = pathToNFS + "User Homes"+ "/" + testUser + "/" + folderName;

        ShareUser.createEnterpriseUser(drone, ADMIN_USERNAME, testUser, testUser, DEFAULT_LASTNAME, DEFAULT_PASSWORD);
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

        // Configure server A
        NfsUtil.configNfsServer(node1Url, ADMIN_PASSWORD, testUser);
        webDriverWait(drone, 5000);
        NfsUtil.configNfsUser(node1Url, testUser);
        webDriverWait(drone, 5000);

        // Mount NFS on server A
        setSshHost(node1Url);
        RemoteUtil.mountNfs(node1Url, nfsPort, nfsMountPort);

        // Create user on server A
        RemoteUtil.createUserOnServer(testUser, DEFAULT_PASSWORD);

        // Mount NFS on server B
        setSshHost(node2Url);
        RemoteUtil.mountNfs(node2Url, nfsPort, nfsMountPort);

        // Create user on server B
        RemoteUtil.createUserOnServer(testUser, DEFAULT_PASSWORD);

        // Create folder
        setSshHost(node2Url);
        NfsUtil.createSpace(sshHost, testUser, DEFAULT_PASSWORD, pathToNFS + "User Homes"+ "/" + testUser + "/", folderName);
    }

    @AfterClass(alwaysRun = true)
    private void disableNFS() throws Exception
    {
        setSshHost(node1Url);
        RemoteUtil.unmountNfs(pathToNFS);
        RemoteUtil.deleteUserOnServer(testUser);
        setSshHost(node2Url);
        RemoteUtil.unmountNfs(pathToNFS);
        RemoteUtil.deleteUserOnServer(testUser);
    }

    /**
     * AONE-8049: NFS in cluster
     */

    @Test(groups = { "EnterpriseOnly" }, timeOut = 1200000)
    public void AONE_8049() throws Exception
    {

        String fileName1 = getRandomString(5) + "_A" + ".txt";
        String fileName2 = getRandomString(5) + "_B" + ".txt";
        String fileName = getRandomString(5) + "_C" + ".txt";
        String fileNameRename1 = getRandomString(5) + "_AA" + ".txt";
        String fileNameRename2 = getRandomString(5) + "_BB" + ".txt";
        File file = newFile(fileName, fileName );
        File file1 = newFile(fileName1, fileName1);
        File file2 = newFile(fileName2, fileName2);
        String alfrescoPath = JmxUtils.getAlfrescoServerProperty(node2Url, jmxGobalProperties, jmxDirLicense).toString();
        ExecutorService executorService = java.util.concurrent.Executors.newCachedThreadPool();

        try
        {
            setSshHost(node1Url);
            // Add document 'A' direct to node 1 via NFS
            assertTrue(NfsUtil.uploadContent(sshHost, testUser, DEFAULT_PASSWORD, remotePath, file1),
                "Document 'A' isn't added to node 1 via CIFS");

            setSshHost(node2Url);
            // Add document 'B' direct to node 2 via NFS
            assertTrue(NfsUtil.uploadContent(sshHost, testUser, DEFAULT_PASSWORD, remotePath, file2),
                "Document 'B' isn't added to node 2 via NFS");

            // Check that each node can see both documents
            assertTrue(NfsUtil.isObjectExists(sshHost, testUser, DEFAULT_PASSWORD, remotePath, fileName1), "Document 'A' isn't added to node 2 via NFS");
            setSshHost(node1Url);
            assertTrue(NfsUtil.isObjectExists(sshHost, testUser, DEFAULT_PASSWORD, remotePath, fileName2), "Document 'B' isn't added to node 1 via NFS");

            // Take node 2 down
            setSshHost(node2Url);
            RemoteUtil.applyIptablesAllPorts();
            assertFalse(TelnetUtil.connectServer(sshHost, nodePort), "Check port " + nodePort + " for node " + node2Url + " is accessible");
            logger.info("Take node 2 down");

            // Add document 'C' to the node 1 via NFS
            setSshHost(node1Url);
            assertTrue(NfsUtil.uploadContent(sshHost, testUser, DEFAULT_PASSWORD, remotePath, file), "Document 'C' isn't uploaded to node 1 via NFS");

            // Bring node 2 up
            setSshHost(node2Url);
            RemoteUtil.removeIpTables(node2Url);
            logger.info("Remove iptables");
            logger.info("Bring node 2 up");
            checkClusterNumbers();

            // Check that documents 'A, 'B' and 'C' can be seen on both node
            setSshHost(node1Url);
            assertTrue(NfsUtil.isObjectExists(sshHost, testUser, DEFAULT_PASSWORD, remotePath, fileName1), "Document 'A' isn't seen on node A");
            assertTrue(NfsUtil.isObjectExists(sshHost, testUser, DEFAULT_PASSWORD, remotePath, fileName2), "Document 'B' isn't seen on node A");
            assertTrue(NfsUtil.isObjectExists(sshHost, testUser, DEFAULT_PASSWORD, remotePath, fileName), "Document 'C' isn't seen on node A");
            setSshHost(node2Url);
            assertTrue(NfsUtil.isObjectExists(sshHost, testUser, DEFAULT_PASSWORD, remotePath, fileName1), "Document 'A' isn't seen on node B");
            assertTrue(NfsUtil.isObjectExists(sshHost, testUser, DEFAULT_PASSWORD, remotePath, fileName2), "Document 'B' isn't seen on node B");
            assertTrue(NfsUtil.isObjectExists(sshHost, testUser, DEFAULT_PASSWORD, remotePath, fileName), "Document 'C' isn't seen on node B");

            // Update document 'A' on node 2 via NFS
            assertTrue(NfsUtil.editContent(sshHost, testUser, DEFAULT_PASSWORD, remotePath, fileName1, "edited"), "Content wasn't edited" );
            webDriverWait(drone, 10000);

            // Check that each node can see the updates
            setSshHost(node1Url);
            assertTrue(NfsUtil.getContent(sshHost, testUser, DEFAULT_PASSWORD, remotePath, fileName1).contains("edited"), "Content wasn't edited. Updates isn't seen on node A");
            setSshHost(node2Url);
            assertTrue(NfsUtil.getContent(sshHost, testUser, DEFAULT_PASSWORD, remotePath, fileName1).contains("edited"), "Content wasn't edited. Updates isn't seen on node B.");

            // Rename document 'A' to document 'AA" on node 2 via NFS
            assertTrue(NfsUtil.renameFileOrFolder(sshHost, testUser, DEFAULT_PASSWORD, remotePath, fileName1, fileNameRename1), "Documents 'A' isn't renamed to 'AA'.");
            webDriverWait(drone, 10000);

            // Check that each node can see the updates
            assertTrue(NfsUtil.isObjectExists(sshHost, testUser, DEFAULT_PASSWORD, remotePath, fileNameRename1), "Documents 'A' isn't renamed to 'AA'. Updates isn't seen on node A");
            assertFalse(NfsUtil.isObjectExists(sshHost, testUser, DEFAULT_PASSWORD, remotePath, fileName1), "Documents 'A' isn't renamed to 'AA'. Updates isn't seen on node A");
            setSshHost(node2Url);
            assertTrue(NfsUtil.isObjectExists(sshHost, testUser, DEFAULT_PASSWORD, remotePath, fileNameRename1), "Documents 'A' isn't renamed to 'AA'. Updates isn't seen on node A");
            assertFalse(NfsUtil.isObjectExists(sshHost, testUser, DEFAULT_PASSWORD, remotePath, fileName1), "Documents 'A' isn't renamed to 'AA'. Updates isn't seen on node B.");

            // Take node 2 down
            setSshHost(node2Url);
            RemoteUtil.applyIptablesAllPorts();
            assertFalse(TelnetUtil.connectServer(node2Url, nodePort), "Check port " + nodePort + " for node " + node2Url + " is accessible");
            logger.info("Take node 2 down");

            // Update document 'AA' on node 1 via NFS
            setSshHost(node1Url);
            assertTrue(NfsUtil.editContent(sshHost, testUser, DEFAULT_PASSWORD, remotePath, fileName, fileNameRename2), "Content wasn't edited" );

            // Bring node 2 up
            setSshHost(node2Url);
            RemoteUtil.removeIpTables(node2Url);
            logger.info("Remove iptables");
            logger.info("Bring node 2 up");
            checkClusterNumbers();

            // Check that each node can see the updates
            setSshHost(node1Url);
            assertTrue(NfsUtil.getContent(sshHost, testUser, DEFAULT_PASSWORD, remotePath, fileName).contains(fileNameRename2), "Updates isn't seen on node A");
            setSshHost(node2Url);
            assertTrue(NfsUtil.getContent(sshHost, testUser, DEFAULT_PASSWORD, remotePath, fileName).contains(fileNameRename2), "Updates isn't seen on node B");

            // Take node 2 down
            setSshHost(node2Url);
            RemoteUtil.applyIptablesAllPorts();
            assertFalse(TelnetUtil.connectServer(node2Url, nodePort), "Check port " + nodePort + " for node " + node2Url + " is accessible");
            logger.info("Take node 2 down");

            // Rename document 'AA' to document 'BB' on node 1 via NFS
            setSshHost(node1Url);
            assertTrue(NfsUtil.renameFileOrFolder(sshHost, testUser, DEFAULT_PASSWORD, remotePath, fileName, fileNameRename2), "Documents 'AA' isn't renamed to 'BB'.");

            // Bring node 2 up
            setSshHost(node2Url);
            RemoteUtil.removeIpTables(node2Url);
            logger.info("Remove iptables");
            logger.info("Bring node 2 up");
            checkClusterNumbers();

            // Check that each node can see the updates
            setSshHost(node1Url);
            assertTrue(NfsUtil.isObjectExists(sshHost, testUser, DEFAULT_PASSWORD, remotePath, fileNameRename2), "Documents 'AA' isn't renamed to 'BB'.");
            assertFalse(NfsUtil.isObjectExists(sshHost, testUser, DEFAULT_PASSWORD, remotePath, fileName),
                   "Document 'AA' isn't renamed to 'BB'. Updates isn't seen on node A");
            setSshHost(node2Url);
            assertTrue(NfsUtil.isObjectExists(sshHost, testUser, DEFAULT_PASSWORD, remotePath, fileNameRename2), "Documents 'AA' isn't renamed to 'BB'.");
            assertFalse(NfsUtil.isObjectExists(sshHost, testUser, DEFAULT_PASSWORD, remotePath, fileName),
                    "Document 'AA' isn't renamed to 'BB'. Updates isn't seen on node B");

            // Delete document BB from node 2 via NFS
            assertTrue(NfsUtil.deleteContentItem(sshHost, testUser, DEFAULT_PASSWORD, remotePath, fileNameRename2), "Documents 'BB' isn't deleted.");

            // Check that each node can't see deleted document
            assertFalse(NfsUtil.isObjectExists(sshHost, testUser, DEFAULT_PASSWORD, remotePath, fileNameRename2),
                "Document 'BB' isn't deleted. Node A can see deleted document");
            setSshHost(node1Url);
            assertFalse(NfsUtil.isObjectExists(sshHost, testUser, DEFAULT_PASSWORD, remotePath, fileNameRename2),
                "Document 'BB' isn't deleted. Node B can see deleted document");

            // Take node 1 down
            setSshHost(node1Url);
            RemoteUtil.applyIptablesAllPorts();
            assertFalse(TelnetUtil.connectServer(node1Url, nodePort), "Check port " + nodePort + " for node " + node1Url + " is accessible");

            // Delete document 'B' on node 2 via NFS
            setSshHost(node2Url);
            assertTrue(NfsUtil.deleteContentItem(sshHost, testUser, DEFAULT_PASSWORD, remotePath, fileName2), "Documents 'BB' isn't deleted.");

            // Bring node 1 up
            setSshHost(node1Url);
            RemoteUtil.removeIpTables(node1Url);
            logger.info("Remove iptables");
            checkClusterNumbers();

            // Check that deleted documents can't be seen on both node
            assertFalse(NfsUtil.isObjectExists(sshHost, testUser, DEFAULT_PASSWORD, remotePath, fileName2),
                "Document 'B' isn't deleted. Node A can see deleted document");
            setSshHost(node2Url);
            assertFalse(NfsUtil.isObjectExists(sshHost, testUser, DEFAULT_PASSWORD, remotePath, fileName2),
                "Document 'B' isn't deleted. Node B can see deleted document");

            // Start upload a file of 1 GB on node 1 and at the moment stop the node 2

            Thread uploadThread = new Thread(new Runnable()
            {
                public void run()
                {
                    setSshHost(node1Url);
                    logger.info("Upload file start for " + node1Url);
                    // Start upload a file of 1 GB on node 1
                    assertTrue(NfsUtil.uploadContent(sshHost, testUser, DEFAULT_PASSWORD, remotePath, bigFile), "Can't upload " + bigFileName
                        + " content A of 1 GB direct to node 1 via NFS");
                    logger.info(" Upload file end for " + node1Url);

                }
            });


            List<Future> futures = new ArrayList<>();

            //Start Upload
            futures.add(executorService.submit(uploadThread));

            // Server B is stopped
            setSshHost(node2Url);
            RemoteUtil.stopAlfresco(alfrescoPath);
            logger.info("Wait alfresco stopped");
            RemoteUtil.waitForAlfrescoShutdown(node2Url, 1000);
            logger.info("Take node 2 down");
            assertFalse(TelnetUtil.connectServer(node2Url, nodePort), "Check port " + nodePort + " for node " + node2Url + " is accessible");

            //End Upload
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

            setSshHost(node1Url);
            int count = 0;
            logger.info("node1Url: " + node1Url);
            while (count < 20)
            {
                if (NfsUtil.isObjectExists(sshHost, testUser, DEFAULT_PASSWORD, remotePath, bigFileName))
                {
                    break;
                }
                count++;
                logger.info("wait upload begin count:" + count);
            }

            // start the server B
            setSshHost(node2Url);
            RemoteUtil.startAlfresco(alfrescoPath);
            logger.info("Wait alfresco start");
            RemoteUtil.waitForAlfrescoStartup(node2Url, 2000);
            logger.info("Bring node 2 up");
            checkClusterNumbers();
            assertTrue(TelnetUtil.connectServer(node2Url, nodePort), "Check port " + nfsMountPort + " for node " + node2Url + "isn't accessible");

            // Check that each node can see the document
            setSshHost(node1Url);
            assertTrue(NfsUtil.isObjectExists(sshHost, testUser, DEFAULT_PASSWORD, remotePath, bigFileName), "Document '" + bigFile
                + "' isn't seen on node A");
            setSshHost(node2Url);
            assertTrue(NfsUtil.isObjectExists(sshHost, testUser, DEFAULT_PASSWORD, remotePath, bigFileName), "Document '" + bigFile
                + "' isn't seen on node B");

        }
        finally
        {
            // Remove folder with documents
            assertTrue(NfsUtil.deleteFolder(sshHost, testUser, DEFAULT_PASSWORD, pathToNFS + "User Homes" + "/" + testUser + "/", folderName), "Can't delete " + folderName
                + " folder");
            setSshHost(node2Url);
            RemoteUtil.removeIpTables(node1Url);
            setSshHost(node1Url);
            RemoteUtil.removeIpTables(node2Url);
            executorService.shutdown();
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
}
