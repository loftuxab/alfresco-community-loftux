package org.alfresco.share.sanity.repository.cifs;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.alfresco.application.windows.MicorsoftOffice2010;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.systemsummary.AdminConsoleLink;
import org.alfresco.po.share.systemsummary.RepositoryServerClusteringPage;
import org.alfresco.po.share.systemsummary.SystemSummaryPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.CifsUtil;
import org.alfresco.share.util.FtpUtil;
import org.alfresco.share.util.JmxUtils;
import org.alfresco.share.util.RemoteUtil;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.TelnetUtil;
import org.alfresco.utilities.Application;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.cobra.ldtp.Ldtp;

/**
 * @author Sergey Kardash
 */
public class SanityCifsClusterTest extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(SanityCifsClusterTest.class);
    private static String node1Url;
    private static String node2Url;
    protected static String jmxGobalProperties = "Alfresco:Name=GlobalProperties";
    protected static String jmxDirLicense = "dir.license.external";
    private static final String regexUrl = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(:\\d{1,5})?";
    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
    private static final Pattern DOMAIN_PATTERN = Pattern.compile("\\w+(\\.\\w+)*(\\.\\w{2,})");
    final String fileName1GB = "File_" + getRandomString(10) + "_1.txt";
    final File file1GB = getFileWithSize(fileName1GB, 1024);

    String mapConnect;
    String mapConnect2;
    String networkDrive2;
    String networkPathNew2;
    static String folderName = getRandomString(10);
    private static String cifsPath = "\\" + folderName + "\\";
    private static String cifsPathValidate = "\\Sites\\";
    private static final String regexUrlWithPort = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(:\\d{1,5})?";
    private static final String regexUrlIP = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();

        logger.info("Starting Tests: " + testName);
        file1GB.deleteOnExit();

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

        int charValue = networkDrive.charAt(0);
        String previous = String.valueOf((char) (charValue - 1));
        networkDrive2 = previous.toUpperCase() + ":";

        String ip = getAddressNetworkPath(node1Url);
        String networkPathNew = networkPath.concat("webdav");
        networkPathNew = networkPathNew.replaceFirst(regexUrlWithPort, ip);
        if (networkPathNew.contains("alfresco\\"))
        {
            networkPathNew = networkPathNew.replace("alfresco\\", "alfresco");
        }

        String ip2 = getAddressNetworkPath(node2Url);
        networkPathNew2 = networkPathNew2.replaceFirst(regexUrlWithPort, ip2);
        if (networkPathNew2.contains("alfresco\\"))
        {
            networkPathNew2 = networkPathNew2.replace("alfresco\\", "alfresco");
        }

        mapConnect = "cmd /c start /WAIT net use" + " " + networkDrive + " " + networkPathNew + " " + "/user:" + ADMIN_USERNAME + " " + ADMIN_PASSWORD;
        Runtime.getRuntime().exec(mapConnect);

        webDriverWait(drone, 5000);

        mapConnect2 = "cmd /c start /WAIT net use" + " " + networkDrive2 + " " + networkPathNew2 + " " + "/user:" + ADMIN_USERNAME + " " + ADMIN_PASSWORD;
        Runtime.getRuntime().exec(mapConnect2);

        CifsUtil.addSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", folderName);

        if (CifsUtil.checkDirOrFileExists(10, 200, networkDrive + cifsPathValidate))
        {
            logger.info("----------Mapping succesfull " + ADMIN_USERNAME);
        }
        else
        {
            logger.error("----------Mapping was not done " + ADMIN_USERNAME);
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() throws Exception
    {
        super.setup();

    }

    @AfterMethod(alwaysRun = true)
    public void teardownMethod() throws Exception
    {

        Runtime.getRuntime().exec("taskkill /F /IM WINWORD.EXE");
        Runtime.getRuntime().exec("taskkill /F /IM CobraWinLDTP.EXE");
        super.tearDown();
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() throws IOException
    {

        Runtime.getRuntime().exec("cmd /c start /WAIT net use * /d /y");

        if (CifsUtil.checkDirOrFileNotExists(7, 200, networkDrive + cifsPathValidate))
        {
            logger.info("--------Unmapping succesfull " + ADMIN_USERNAME);
        }
        else
        {
            logger.error("--------Unmapping was not done correctly " + ADMIN_USERNAME);
        }

    }

    private static String getAddressNetworkPath(String shareUrl)
    {
        Pattern p1 = Pattern.compile(regexUrlIP);
        Matcher m1 = p1.matcher(shareUrl);
        if (m1.find())
        {
            return m1.group();
        }
        throw new PageException("Can't extract address from URL");
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
     * Test - AONE-8036:CIFS in cluster
     * <ul>
     * <li>Server A and server B are working in cluster</li>
     * <li>Add document 'A' direct to node 1 via CIFS</li>
     * <li>Add document 'B' direct to node 2 via CIFS</li>
     * <li>Check that each node can see both documents</li>
     * <li>Take node 2 down</li>
     * <li>Add document 'C' to the node 1 via CIFS</li>
     * <li>Bring node 2 up</li>
     * <li>Check that documents 'A, 'B' and 'C' can be seen on both node</li>
     * <li>Cluster works correctly without errors</li>
     * </ul>
     */
    @Test(groups = { "CIFSWindowsClient", "EnterpriseOnly" }, timeOut = 2000000)
    public void AONE_8036() throws Exception
    {

        final String fileName1 = getRandomString(5) + "_A" + ".txt";
        String fileNameRename1 = getRandomString(5) + "_AA" + ".txt";
        String fileNameRename2 = getRandomString(5) + "_BB" + ".txt";
        String fileName2 = getRandomString(5) + "_B" + ".txt";
        String fileName3 = "WordDocument.docx";
        String fileName3Name = "WordDocument";
        String fileName3Type = "docx";

        final String remotePath = "Alfresco" + "/" + folderName + "/";
        String dataPath = DATA_FOLDER + fileName3;
        File file = new File(dataPath);
        String alfrescoPath = JmxUtils.getAlfrescoServerProperty(node1Url, jmxGobalProperties, jmxDirLicense).toString();
        ExecutorService executorService = java.util.concurrent.Executors.newCachedThreadPool();

        try
        {
            dronePropertiesMap.get(drone).setShareUrl(node1Url);
            CifsUtil.addSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", folderName);

            // 1. add document 'A' direct to node 1 via CIFS
            Assert.assertTrue(CifsUtil.addContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, fileName1, fileName1),
                    "Document 'A' isn't added to node 1 via CIFS");
            logger.info("1. add document 'A' direct to node 1 via CIFS");

            // 2. add document 'B' direct to node 2 via CIFS
            Assert.assertTrue(CifsUtil.addContent(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, fileName2, fileName2),
                    "Document 'B' isn't added to node 2 via CIFS");
            logger.info("2. add document 'B' direct to node 1 via CIFS");

            // 3. check that each node can see both documents
            assertTrue(CifsUtil.checkItem(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, fileName2), "Document 'B' isn't added to node 1 via CIFS");
            assertTrue(CifsUtil.checkItem(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, fileName1), "Document 'A' isn't added to node 2 via CIFS");
            logger.info("3. check that each node can see both documents");

            setSshHost(node2Url);
            // 4. Take node 2 down
            RemoteUtil.applyIptablesAllPorts();
            assertFalse(TelnetUtil.connectServer(node2Url, ftpPort), "Check port " + ftpPort + " for node " + node2Url + " is accessible");
            logger.info("4. Take node 2 down");

            // 5. Add document 'C' to the node 1 via CIFS
            assertTrue(CifsUtil.uploadContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, file), "Document 'C' isn't uploaded to node 1 via CIFS");
            logger.info("5. Add document 'C' to the node 1 via CIFS");

            // 6. Bring node 2 up
            RemoteUtil.removeIpTables(node1Url);
            logger.info("Remove iptables");
            logger.info("6. Bring node 2 up");
            checkClusterNumbers();

            // 7. Check that documents 'A, 'B' and 'C' can be seen on both node
            assertTrue(CifsUtil.checkItem(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, fileName1), "Document 'A' isn't seen on node A");
            assertTrue(CifsUtil.checkItem(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, fileName2), "Document 'B' isn't seen on node A");
            assertTrue(CifsUtil.checkItem(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, fileName3), "Document 'C' isn't seen on node A");
            assertTrue(CifsUtil.checkItem(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, fileName1), "Document 'A' isn't seen on node B");
            assertTrue(CifsUtil.checkItem(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, fileName2), "Document 'B' isn't seen on node B");
            assertTrue(CifsUtil.checkItem(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, fileName3), "Document 'C' isn't seen on node B");

            String fullPath = networkDrive + cifsPath;
            String fullPath1 = networkDrive2 + cifsPath;
            MicorsoftOffice2010 word = new MicorsoftOffice2010(Application.WORD, "2010");

            // 23 Open document 'C.doc' for editing on node 1 via CIFS
            Ldtp ldtp;
            word.openFileFromCMD(fullPath, fileName3, ADMIN_USERNAME, ADMIN_PASSWORD, false);
            word.getAbstractUtil().waitForWindow("frm" + fileName3Name + " - Microsoft Word");
            if (word.getAbstractUtil().isWindowPresented("frm" + fileName3Name + " - Microsoft Word"))
            {
                word.getAbstractUtil().setOnWindow("frm" + fileName3Name + " - Microsoft Word");
            }
            else if (word.getAbstractUtil().isWindowPresented("frm" + fileName3 + " - Microsoft Word"))
            {
                word.getAbstractUtil().setOnWindow("frm" + fileName3 + " - Microsoft Word");
            }

            // 24 Open document 'C.doc' for editing on node 2 via CIFS
            word.openFileFromCMD(fullPath1, fileName3, ADMIN_USERNAME, ADMIN_PASSWORD, false);
            word.getAbstractUtil().waitForWindow("frmFile In Use");
            ldtp = word.getAbstractUtil().setOnWindow("frmFile In Use");

            Assert.assertTrue(word.getAbstractUtil().isObjectDisplayed(ldtp, "rbtnOpenaReadOnlycopy"), "'rbtnOpenaReadOnlycopy' isn't displayed");
            word.getAbstractUtil().clickOnObject(ldtp, "btnOK");

            logger.info("Click OK button for 'frmFile In Use' window");

            word.getAbstractUtil().waitForWindow("frm" + fileName3 + " [Read-Only] - Microsoft Word");
            if (word.getAbstractUtil().isWindowPresented("frm" + fileName3Name + " [Read-Only] - Microsoft Word"))
            {
                ldtp = word.getAbstractUtil().setOnWindow("frm" + fileName3Name + " [Read-Only] - Microsoft Word");
            }
            else if (word.getAbstractUtil().isWindowPresented("frm" + fileName3 + " [Read-Only] - Microsoft Word"))
            {
                ldtp = word.getAbstractUtil().setOnWindow("frm" + fileName3 + " [Read-Only] - Microsoft Word");
            }

            // 26 Document 'C.doc' is opened in Read only mode on node 2
            Assert.assertTrue(word.getAbstractUtil().isObjectDisplayed(ldtp, "pane" + fileName3Name + fileName3Type + "[Read-Only]")
                    || word.getAbstractUtil().isObjectDisplayed(ldtp, "pane" + fileName3Name + "[Read-Only]"), "'[Read-Only] - Microsoft Word' isn't displayed");

            Runtime.getRuntime().exec("taskkill /F /IM WINWORD.EXE");

            // 8. Rename and update document 'A' to document 'AA" on node 2 via CIFS
            assertTrue(CifsUtil.renameItem(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, fileName1, fileNameRename1),
                    "Documents 'A' isn't renamed to 'AA'.");

            // 9. check that each node can see the updates
            assertTrue(CifsUtil.checkItem(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, fileNameRename1),
                    "Document 'A' isn't renamed to 'AA'. Updates isn't seen on node A");
            assertFalse(CifsUtil.checkItem(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, fileName1),
                    "Document 'A' isn't renamed to 'AA'. Updates isn't seen on node A");
            assertTrue(CifsUtil.checkItem(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, fileNameRename1),
                    "Document 'A' isn't renamed to 'AA'. Updates isn't seen on node B");
            assertFalse(CifsUtil.checkItem(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, fileName1),
                    "Document 'A' isn't renamed to 'AA'. Updates isn't seen on node B");

            // 10. Take node 2 down
            setSshHost(node2Url);
            RemoteUtil.applyIptablesAllPorts();
            assertFalse(TelnetUtil.connectServer(node2Url, ftpPort), "Check port " + ftpPort + " for node " + node2Url + " is accessible");

            // 11. Rename and update document 'AA' to document 'BB' on node 1 via CIFS
            assertTrue(CifsUtil.renameItem(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, fileNameRename1, fileNameRename2),
                    "Document 'AA' isn't renamed to 'BB'.");

            // 12. Bring node 2 up
            RemoteUtil.removeIpTables(node1Url);
            logger.info("Remove iptables");
            logger.info("5. Check port " + ftpPort + " for node " + node2Url);
            checkClusterNumbers();

            // 13. Check that each node can see the updates
            assertTrue(CifsUtil.checkItem(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, fileNameRename2),
                    "Document 'AA' isn't renamed to 'BB'. Updates isn't seen on node A");
            assertFalse(CifsUtil.checkItem(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, fileNameRename1),
                    "Document 'AA' isn't renamed to 'BB'. Updates isn't seen on node A");
            assertTrue(CifsUtil.checkItem(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, fileNameRename2),
                    "Document 'AA' isn't renamed to 'BB'. Updates isn't seen on node B");
            assertFalse(CifsUtil.checkItem(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, fileNameRename1),
                    "Document 'AA' isn't renamed to 'BB'. Updates isn't seen on node B");

            // 14. Delete document BB from node 2 via CIFS
            assertTrue(CifsUtil.deleteContent(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, fileNameRename2), "Documents 'BB' isn't deleted.");

            // 15. Check that each node can't see deleted document
            assertFalse(CifsUtil.checkItem(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, fileNameRename2),
                    "Document 'BB' isn't deleted. Node A can see deleted document");
            assertFalse(CifsUtil.checkItem(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, fileNameRename2),
                    "Document 'BB' isn't deleted. Node B can see deleted document");

            // 16 Take node 1 down
            setSshHost(node1Url);
            RemoteUtil.applyIptablesAllPorts();
            assertFalse(TelnetUtil.connectServer(node1Url, ftpPort), "Check port " + ftpPort + " for node " + node1Url + " is accessible");

            // 17. Delete document 'B' on node 2 via CIFS
            assertTrue(CifsUtil.deleteContent(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, fileName2), "Documents 'BB' isn't deleted.");

            // 18. Bring node 1 up
            RemoteUtil.removeIpTables(node2Url);
            logger.info("18. Remove iptables");
            checkClusterNumbers();

            // 19. Check that deleted documents can't be seen on both node
            assertFalse(CifsUtil.checkItem(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, fileName2),
                    "Document 'B' isn't deleted. Node A can see deleted document");
            assertFalse(CifsUtil.checkItem(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, fileName2),
                    "Document 'B' isn't deleted. Node B can see deleted document");

            // 20. Start upload a file of 1 GB on node 1 and at the moment stop the node 2
            Thread uploadThread = new Thread(new Runnable()
            {
                public void run()
                {
                    logger.info("1. Upload file start for " + node1Url);
                    // Start upload a file of 1 GB on node 1
                    assertTrue(CifsUtil.uploadContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, file1GB), "Can't upload " + fileName1GB
                            + " content A of 1 GB direct to node 1 via FTP");
                    logger.info("4. Upload file end for " + node1Url);

                }
            });

            List<Future> futures = new ArrayList<>();

            // Start upload a file of 1 GB on node 1
            futures.add(executorService.submit(uploadThread));

            // upload
            webDriverWait(drone, 10000);

            int count = 0;
            logger.info("node1Url: " + node1Url);
            while (count < 10)
            {
                if (FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName1GB, remotePath))
                {
                    break;
                }
                checkClusterNumbers();
                count++;
                logger.info("wait upload begin count:" + count);
            }
            setSshHost(node2Url);
            // Server B is stopped
            RemoteUtil.stopAlfresco(alfrescoPath);
            logger.info("Wait alfresco stopped");
            RemoteUtil.waitForAlfrescoShutdown(node2Url, 1000);

            logger.info("Check port " + ftpPort + " for node " + node1Url);
            assertFalse(TelnetUtil.connectServer(node2Url, ftpPort), "Check port " + ftpPort + " for node " + node1Url + " is accessible");

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

            // start the server B
            RemoteUtil.startAlfresco(alfrescoPath);
            logger.info("5. Wait alfresco start");
            RemoteUtil.waitForAlfrescoStartup(node2Url, 2000);

            logger.info("6. Check port " + ftpPort + " for node " + node2Url);

            checkClusterNumbers();
            assertTrue(TelnetUtil.connectServer(node2Url, ftpPort), "Check port " + ftpPort + " for node " + node2Url + "isn't accessible");

            // Check that each node can see the document
            assertTrue(CifsUtil.checkItem(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, fileName1GB), "Document '" + fileName1GB
                    + "' isn't seen on node A");
            assertTrue(CifsUtil.checkItem(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, fileName1GB), "Document '" + fileName1GB
                    + "' isn't seen on node B");

        }
        finally
        {
            assertTrue(CifsUtil.deleteContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", folderName + "/"), "Can't delete " + folderName
                    + " folder");
            // Remove folder with documents
            setSshHost(node2Url);
            RemoteUtil.removeIpTables(node1Url);
            setSshHost(node1Url);
            RemoteUtil.removeIpTables(node2Url);
            executorService.shutdown();

        }

        ShareUser.logout(drone);
    }

}