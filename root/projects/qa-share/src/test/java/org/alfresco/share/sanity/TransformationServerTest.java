package org.alfresco.share.sanity;

import org.alfresco.po.alfresco.AlfrescoTransformationServerHistoryPage;
import org.alfresco.po.alfresco.AlfrescoTransformationServerStatusPage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.site.contentrule.FolderRulesPage;
import org.alfresco.po.share.site.contentrule.FolderRulesPageWithRules;
import org.alfresco.po.share.site.contentrule.createrules.CreateRulePage;
import org.alfresco.po.share.site.contentrule.createrules.selectors.AbstractIfSelector;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.ActionSelectorEnterpImpl;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.WhenSelectorImpl;
import org.alfresco.po.share.site.document.CopyOrMoveContentPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.systemsummary.AdminConsoleLink;
import org.alfresco.po.share.systemsummary.RepositoryServerClusteringPage;
import org.alfresco.po.share.systemsummary.SystemSummaryPage;
import org.alfresco.po.share.util.PageUtils;
import org.alfresco.share.util.*;
import org.alfresco.test.FailedTestListener;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Marina.Nenadovets
 */
@Listeners(FailedTestListener.class)
public class TransformationServerTest extends AbstractUtils
{
    private static final Log logger = LogFactory.getLog(TransformationServerTest.class);
    private static String node1Url;
    private static int transPort;
    private static final String TRANSFORMSERV_USERNAME = "alfresco";
    private static final String TRANSFORMSERV_PASS = "alfresco";

    private static final String REGEX_NO_CONN = "WARN  \\[(.*?)transformation\\.client\\.ConnectionTester\\] \\[(.*?)\\] An initial Transformation Server " +
        "connection could not be established\\.";
    private static final String REGEX_OK_CONN = "INFO  \\[(.*?)transformation.client.ConnectionTester\\] \\[(.*?)\\] The connection to the Transformation Server " +
        "has been established\\.";
    private static final String REGEX_RE_CONN = "WARN  \\[(.*?)transformation\\.client\\.ConnectionTester\\] \\[(.*?)\\] The Transformation Server connection " +
        "was re-established\\.";
    private static final String REGEX_41_ERROR = "(\\d+) An error was encountered during deployment of the AMP into the WAR: (\\d+) The module " +
        "\\(Transformation Server AMP for Repository\\) cannot be installed on a war version greater than 4\\.1\\.99\\. This war is version:" +
        "\\d\\.\\d{1,2}\\.\\d+\\.";
    private static final String REGEX_UNAUTHORIZED = "INFO  \\[transformation.client.RemoteContentTransformerWorker\\] \\[(.*?)\\] received unexpected http " +
        "status: 401";
    private static String node2Url;
    private String ARTIFACT_NAME;
    private File MSI_FILE_TO_COPY;
    private String TARGET_PATH_LOCALLY = System.getProperty("java.io.tmpdir");
    private static String alfrescoPath;
    private static String docFileName = "doc.doc";
    private static String xlsFileName = "xls.xls";
    private static final String jmxSysProps = "Alfresco:Name=SystemProperties";
    private static final String jmxAlfHome = "alfresco.home";
    protected static String jmxTransformProps = "Alfresco:Type=Configuration,Category=transformationserver,id1=default";
    protected static String TRANSFORM_STARTUP_INLOGFILE = "Startup of 'transformationserver' subsystem, ID: [transformationserver, default] complete";

    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Start Tests in: " + testName);
        String logOutput;
        String TR_SERV_PBLD = "/data/bamboo/artifacts/INTEGRATIONS-TRANSFORMATIONSERVER/JOB1/";
        try
        {
            String REMOTE_DIR = TR_SERV_PBLD + RemoteUtil.getOldestFileInPbld(TR_SERV_PBLD) + "/Transformation-server-artifacts/";
            ARTIFACT_NAME = RemoteUtil.getOldestFileInPbld(REMOTE_DIR);
            transPort = 2;

            //Downloading artifacts and unzipping them
            RemoteUtil.downloadArtifactFromPbld(REMOTE_DIR, ARTIFACT_NAME, TARGET_PATH_LOCALLY);
            ZipArchiveFile.unZipIt(TARGET_PATH_LOCALLY + ARTIFACT_NAME, TARGET_PATH_LOCALLY);

            //Copying msi file to transformation host
            File dirWithMsi = new File(TARGET_PATH_LOCALLY + ARTIFACT_NAME.replace(".zip", ""));
            File[] msiFile = dirWithMsi.listFiles(new FilenameFilter()
            {
                @Override
                public boolean accept(File dir, String name)
                {
                    return name.endsWith("msi");
                }
            });
            MSI_FILE_TO_COPY = msiFile[0].getAbsoluteFile();
            RemoteUtil.copyToRemoteWin(MSI_FILE_TO_COPY, "\\\\" + transformHostName1 + "\\" + transformServInstallationTarget.replace(":", "$"));

            //Copying amps files and applying them
            SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            RepositoryServerClusteringPage clusteringPage = sysSummaryPage.openConsolePage(AdminConsoleLink.RepositoryServerClustering).render();
            if (clusteringPage.isClusterEnabled())
            {
                List<String> clusterMembers = clusteringPage.getClusterMembers();
                if (clusterMembers.size() >= 2)
                {
                    sshHost = node1Url = clusterMembers.get(0);
                    alfrescoPath = JmxUtils.getAlfrescoServerProperty(node1Url, jmxSysProps, jmxAlfHome).toString();
                    logOutput = copyAndApplyAmps(node1Url, alfrescoPath);
                    assertTrue(TransformServerUtil.isLicenseValid(node1Url), "License is invalid on " + node1Url + ", please, provide a valid Transformation " +
                        "Server lic");
                    assertTrue(logOutput.contains(TRANSFORM_STARTUP_INLOGFILE), "Transform subsystem isn't up on " + node1Url);

                    //Same on second cluster node
                    sshHost = node2Url = clusterMembers.get(1);
                    alfrescoPath = JmxUtils.getAlfrescoServerProperty(node2Url, jmxSysProps, jmxAlfHome).toString();
                    logOutput = copyAndApplyAmps(node2Url, alfrescoPath);
                    assertTrue(TransformServerUtil.isLicenseValid(node2Url), "License is invalid on " + node2Url + ", please, provide a valid Transformation " +
                        "Server lic");
                    assertTrue(logOutput.contains(TRANSFORM_STARTUP_INLOGFILE), "Transform subsystem isn't up on " + node2Url);
                }
                else
                {
                    sshHost = node1Url = clusterMembers.get(0);
                    alfrescoPath = JmxUtils.getAlfrescoServerProperty(node1Url, jmxSysProps, jmxAlfHome).toString();
                    logOutput = copyAndApplyAmps(node1Url, alfrescoPath);
                    assertTrue(TransformServerUtil.isLicenseValid(node1Url), "License is invalid on " + node1Url + ", please, provide a valid Transformation " +
                        "Server lic");
                    assertTrue(logOutput.contains(TRANSFORM_STARTUP_INLOGFILE), "Transform subsystem isn't up on " + node1Url);
                }
            }
            else
            {
                sshHost = node1Url = PageUtils.getAddress(shareUrl).replaceFirst(":\\d{1,5}", "");
                alfrescoPath = JmxUtils.getAlfrescoServerProperty(node1Url, jmxSysProps, jmxAlfHome).toString();
                logOutput = copyAndApplyAmps(node1Url, alfrescoPath);
                assertTrue(TransformServerUtil.isLicenseValid(node1Url), "License is invalid on " + node1Url + ", please, provide a valid Transformation " +
                    "Server lic");
                assertTrue(logOutput.contains(TRANSFORM_STARTUP_INLOGFILE), "Transform subsystem isn't up on " + node1Url);
            }
        }
        catch (Exception e)
        {
            throw new SkipException("Skipping as pre-condition step(s) fail: " + e.getCause());
        }
    }

    /**
     * Scripted installation
     *
     * @throws Exception
     */
    @Test(groups = {"EnterpriseOnly", "Sanity"})
    public void AONE_8135() throws Exception
    {
        //check if transformation is running on remote host
        if (TransformServerUtil.checkTransformationService(transformHostName1))
        {
            logger.info("Transformation service is up on " + transformHostName1);
            TransformServerUtil.uninstallTheServer(transformHostName1, transformServInstallationTarget + SLASH + MSI_FILE_TO_COPY.getName());
        }
        boolean isSuccess = TransformServerUtil.launchInstallerInQuietMode(transformHostName1, transformServInstallationTarget + SLASH + MSI_FILE_TO_COPY.getName(),
            transformServInstallationTarget, null, null);
        assertTrue(isSuccess, "Installation wasn't successful");
        TransformServerUtil.navigateToTransformationServerPage(drone, transformHostName1, transPort);
    }

    /**
     * Not allowed installation
     *
     * @throws Exception
     */
    @Test(groups = {"EnterpriseOnly", "Sanity"})
    public void AONE_8138() throws Exception
    {
        String TR_SERV_PBLD = "/data/bamboo/artifacts/INTEGRATIONS-TRANSFORMATIONSERVER41/JOB1/";
        String REMOTE_DIR = TR_SERV_PBLD + RemoteUtil.getOldestFileInPbld(TR_SERV_PBLD) + "/Transformation-server-artifacts";
        String ARTIFACT_NAME41 = "alfresco-4.1-transformationserver-amps-*.zip";

        //Downloading artifacts and unzipping them
        ARTIFACT_NAME41 = RemoteUtil.downloadArtifactFromPbld(REMOTE_DIR, ARTIFACT_NAME41, TARGET_PATH_LOCALLY);
        ZipArchiveFile.unZipIt(TARGET_PATH_LOCALLY + ARTIFACT_NAME41, TARGET_PATH_LOCALLY);
        sshHost = node1Url;

        File dirWithMsi = new File(TARGET_PATH_LOCALLY + ARTIFACT_NAME41.replace(".zip", ""));
        File[] shareAmp = dirWithMsi.listFiles(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String name)
            {
                return name.contains("share") && name.endsWith("amp");
            }
        });

        File[] repoAmp = dirWithMsi.listFiles(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String name)
            {
                return name.contains("repo") && name.endsWith("amp");
            }
        });

        //Copying amp files to remote alfresco server
        RemoteUtil.copyToRemoteServer(repoAmp[0].getAbsolutePath(), alfrescoPath + "/amps");
        RemoteUtil.copyToRemoteServer(shareAmp[0].getAbsolutePath(), alfrescoPath + "/amps_share");

        //Verifying the amps cannot be applied
        RemoteUtil.initJmxProps(node1Url);
        String output = RemoteUtil.applyRepoAmp(alfrescoPath + "/amps/" + repoAmp[0].getName());
        assertTrue(Pattern.compile(REGEX_41_ERROR).matcher(output).find(), "Amps were applied");
    }

    /**
     * FailOver
     *
     * @throws Exception
     */
    @Test(groups = {"EnterpriseOnly", "Sanity"})
    public void AONE_8150() throws Exception
    {
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String siteName2 = siteName + "1";
        String[] xlsInfo = { xlsFileName, DOCLIB };
        String[] files = { "AONE_8909.docx", "AONE_9677.pptx", "AONE_9678.pptx", "AONE_9679.pptx", "AONE_9680.pptx", "AONE_9681.pptx", "AONE_9682.pptx",
            "AONE_9683.pptx", "AONE_9685.pptx", "AONE_9686.pptx", "AONE_9687.pptx", "AONE_9688.pptx", "AONE_9689.pptx", "AONE_9808.docx", "AONE_9810.docx" };

        //The transformation server and 2 alfresco instances should be configured to work with each other (ServerA - TransformationNodeA, ServerB -
        // TransformationNodeB). The TransformationNodeA is overloaded, TransformationNodeB one is not
        RemoteUtil.copyToRemoteWin(MSI_FILE_TO_COPY, "\\\\" + transformHostName2 + "\\" + transformServInstallationTarget.replace(":", "$"));
        if (TransformServerUtil.checkTransformationService(transformHostName1))
        {
            logger.info("Transformation server is up on " + transformHostName1);
            TransformServerUtil.uninstallTheServer(transformHostName1, transformServInstallationTarget + SLASH + MSI_FILE_TO_COPY.getName());
        }
        if (TransformServerUtil.checkTransformationService(transformHostName2))
        {
            logger.info("Transformation server is up on " + transformHostName2);
            TransformServerUtil.uninstallTheServer(transformHostName2, transformServInstallationTarget + SLASH + MSI_FILE_TO_COPY.getName());
        }
        boolean isSuccessOnNode1 = TransformServerUtil.launchInstallerInQuietMode(transformHostName1, transformServInstallationTarget + SLASH + MSI_FILE_TO_COPY
            .getName(), transformServInstallationTarget, transformHostName1, transformHostName1);
        assertTrue(isSuccessOnNode1, "Installation wasn't successful on " + transformHostName1);

        boolean isSuccessOnNode2 = TransformServerUtil.launchInstallerInQuietMode(transformHostName2, transformServInstallationTarget + SLASH + MSI_FILE_TO_COPY
            .getName(), transformServInstallationTarget, transformHostName1, transformHostName2);
        assertTrue(isSuccessOnNode2, "Installation wasn't successful on " + transformHostName2);

        //Enable through jmx
        TransformServerUtil.setPropertiesInJmx(node1Url, "http://" + getIpFromHostName(transformHostName1) + ":" + transPort + "/transformation-server",
            TRANSFORMSERV_USERNAME, TRANSFORMSERV_PASS);

        //Verifying that transformation is working in cluster
        String currDate = new SimpleDateFormat("HH:mm").format(new Date().getTime());
        String currDatePlusMin = new SimpleDateFormat("HH:mm").format(new Date().getTime() + 60000);
        ShareUser.login(drone, ADMIN_USERNAME);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();
        ShareUser.openDocumentLibrary(drone).render();
        ShareUser.uploadFileInFolder(drone, xlsInfo).render();

        //Checking that log file contains information about successful connection to transformation server
        assertTrue(Pattern.compile(currDate + "|" + currDatePlusMin + ":\\d{2},\\d{3}  " + REGEX_OK_CONN).matcher(RemoteUtil.getAlfrescoLog(node1Url)).find(),
            "Log on " + node1Url + "doesn't contain information about successful connection.");
        if(!(node2Url == null))
        {
            assertTrue(Pattern.compile(currDate + "|" + currDatePlusMin + ":\\d{2},\\d{3}  " + REGEX_OK_CONN).matcher(RemoteUtil.getAlfrescoLog(node2Url)).find(),
                "Log on " + node2Url + "doesn't contain information about successful connection.");
        }
        //The history contains info about successful transformation of the file on node1
        AlfrescoTransformationServerStatusPage transStatusPage = TransformServerUtil.navigateToTransformationServerPage(drone, transformHostName1, transPort);
        AlfrescoTransformationServerHistoryPage transHistoryPage = transStatusPage.openServerHistoryPage(drone).render();
        assertTrue(transHistoryPage.isFileTransformed(xlsFileName), "File wasn't transformed or doesn't exists");

        //The history contains info about successful transformation of the file on node2
        transStatusPage = TransformServerUtil.navigateToTransformationServerPage(drone, transformHostName2, transPort);
        transHistoryPage = transStatusPage.openServerHistoryPage(drone).render();
        assertTrue(transHistoryPage.isFileTransformed(xlsFileName), "File wasn't transformed or doesn't exists");

        //Enable transformation server on the second alfresco instance (will be using same host as for replication jobs)
        String secondInstAddr = PageUtils.getAddress(replicationEndPointHost).replaceFirst(":\\d{1,5}", "");
        alfrescoPath = JmxUtils.getAlfrescoServerProperty(secondInstAddr, jmxSysProps, jmxAlfHome).toString();
        String logOutput = copyAndApplyAmps(secondInstAddr, alfrescoPath);
        assertTrue(logOutput.contains(TRANSFORM_STARTUP_INLOGFILE), "Transform subsystem isn't up on " + secondInstAddr);

        TransformServerUtil.setPropertiesInJmx(secondInstAddr, "http://" + getIpFromHostName(transformHostName2) + ":" + transPort + "/transformation-server",
            TRANSFORMSERV_USERNAME, TRANSFORMSERV_PASS);

        //Create a site and a folder with rule on both instances
        try
        {
            DocumentLibraryPage docLibPage = null;
            dronePropertiesMap.get(drone).setShareUrl(replicationEndPointHost);
            for (int i = 0; i < 2; i++)
            {

                ShareUser.login(drone, ADMIN_USERNAME);
                ShareUser.createSite(drone, siteName2, SITE_VISIBILITY_PUBLIC);
                dronePropertiesMap.get(drone).setShareUrl(shareUrl);
            }

            for (String theFile : files)
            {
                docLibPage = ShareUser.uploadFileInFolder(drone, new String[] { theFile, DOCLIB }).render();
            }
            assertNotNull(docLibPage);

            for (int i = 0; i < 4; i++)
            {
                docLibPage.getNavigation().selectDocuments();
                CopyOrMoveContentPage copyOrMoveContentPage = docLibPage.getNavigation().selectCopyTo().render();
                copyOrMoveContentPage.selectSite(siteName2).selectPath("Documents").render();
                copyOrMoveContentPage.selectOkButton().render();
            }

            docLibPage.getBottomPaginationForm().clickNext().render();
            docLibPage.getBottomPaginationForm().clickNext().render();
            docLibPage.getBottomPaginationForm().clickNext().render();

            AlfrescoTransformationServerStatusPage alfStatus = TransformServerUtil.navigateToTransformationServerPage(drone, transformHostName1, transPort);
            AlfrescoTransformationServerHistoryPage alfHistory = alfStatus.openServerHistoryPage(drone).render();
            alfHistory.selectRowsPerPage(50);
            webDriverWait(drone, 15000);
            alfHistory = alfHistory.openServerHistoryPage(drone).render();
            alfHistory = alfHistory.selectRowsPerPage(50).render();
            String fileThatFailed = alfHistory.fileTransformFailed();
            boolean isFailed = false;
            for (String theFile : files)
            {
                isFailed = fileThatFailed.contains(theFile);
                if(isFailed)
                {
                   break;
                }
            }

            assertTrue(isFailed, "None of the transformations has failed due to server overload");

            //Upload any document on serverB
            String[] fileInfo = { "doc.doc", DOCLIB };
            dronePropertiesMap.get(drone).setShareUrl(replicationEndPointHost);
            ShareUser.login(drone, ADMIN_USERNAME);
            ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName2).render();
            ShareUser.uploadFileInFolder(drone, fileInfo);

            alfStatus = TransformServerUtil.navigateToTransformationServerPage(drone, transformHostName2, transPort);
            alfHistory = alfStatus.openServerHistoryPage(drone).render();
            webDriverWait(drone, 15000);
            alfHistory = alfHistory.selectRowsPerPage(50).render();
            assertTrue(alfHistory.isFileTransformed("doc.doc"), "The transformation failed on serverB");
        }
        finally
        {
            dronePropertiesMap.get(drone).setShareUrl(shareUrl);
        }
    }

    /**
     * JMX Console
     *
     * @throws Exception
     */
    @Test(groups = {"EnterpriseOnly", "Sanity"})
    public void AONE_8151() throws Exception
    {
        sshHost = node1Url;
        String[] docInfo = { docFileName, DOCLIB };
        String[] xlsInfo = { xlsFileName, DOCLIB };
        String alfrescoPath = JmxUtils.getAlfrescoServerProperty(node1Url, jmxSysProps, jmxAlfHome).toString();

        //Two Transformation Servers should be installed
        if(TransformServerUtil.checkTransformationService(transformHostName2))
        {
            TransformServerUtil.startTransformServiceRemotely(transformHostName2);
        }
        else
        {
            RemoteUtil.copyToRemoteWin(MSI_FILE_TO_COPY, "\\\\" + transformHostName2 + "\\" + transformServInstallationTarget.replace(":", "$"));
            assertTrue(TransformServerUtil.launchInstallerInQuietMode(transformHostName2, transformServInstallationTarget + SLASH + MSI_FILE_TO_COPY.getName(),
                transformServInstallationTarget, null, null), "Installation wasn't successful.");
        }

        //Check Alfresco->Configuration->transformationserver bean
        assertNotNull(JmxUtils.getAlfrescoServerProperty(node1Url, jmxTransformProps, "transformserver.aliveCheckTimeout").toString(),
            "aliveCheckTimeout is missing in jmx");
        assertNotNull(JmxUtils.getAlfrescoServerProperty(node1Url, jmxTransformProps, "transformserver.disableSSLCertificateValidation").toString(),
            "disableSSLCertificateValidation is missing in jmx");
        assertNotNull(JmxUtils.getAlfrescoServerProperty(node1Url, jmxTransformProps, "transformserver.password").toString(), "password is missing in jmx");
        assertNotNull(JmxUtils.getAlfrescoServerProperty(node1Url, jmxTransformProps, "transformserver.qualityPreference").toString(),
            "qualityPreference is missing in jmx");
        assertNotNull(JmxUtils.getAlfrescoServerProperty(node1Url, jmxTransformProps, "transformserver.test.cronExpression").toString(),
            "cronExpression is missing in jmx");
        assertNotNull(JmxUtils.getAlfrescoServerProperty(node1Url, jmxTransformProps, "transformserver.transformationTimeout").toString(),
            "transformationTimeout is missing in jmx");
        assertNotNull(JmxUtils.getAlfrescoServerProperty(node1Url, jmxTransformProps, "transformserver.url").toString(), "url is missing in jmx");
        assertNotNull(JmxUtils.getAlfrescoServerProperty(node1Url, jmxTransformProps, "transformserver.username").toString(), "username is missing in jmx");
        assertNotNull(JmxUtils.getAlfrescoServerProperty(node1Url, jmxTransformProps, "transformserver.usePDF_A").toString(), "userPDF_A is missing in jmx");

        //Change password to invalid one
        TransformServerUtil.setPropertiesInJmx(node1Url, "http://" + getIpFromHostName(transformHostName1) + ":" + transPort + "/transformation-server",
            "alfresco", "invalid");
        String currDate = new SimpleDateFormat("HH:mm").format(new Date().getTime());
        String currDatePlusMin = new SimpleDateFormat("HH:mm").format(new Date().getTime() + 60000);
        ShareUser.login(drone, ADMIN_USERNAME);
        ShareUser.createSite(drone, getRandomString(5), SITE_VISIBILITY_PUBLIC);
        ShareUser.openDocumentLibrary(drone).render();
        ShareUser.uploadFileInFolder(drone, docInfo);
        assertTrue(Pattern.compile(currDate + "|" + currDatePlusMin + ":\\d{2},\\d{3}  " + REGEX_UNAUTHORIZED).matcher(RemoteUtil.getAlfrescoLog(node1Url)).find(),
            "Log on " + node1Url + " doesn't contains unauthorized error.");
        if(!(node2Url == null))
        {
            assertTrue(Pattern.compile(currDate + "|" + currDatePlusMin + ":\\d{2},\\d{3}  " + REGEX_OK_CONN).matcher(RemoteUtil.getAlfrescoLog(node2Url)).find(),
                "Log on " + node2Url + " doesn't contains unauthorized error.");
        }

        //Change password to previous and perform the transformation again
        TransformServerUtil.setPropertiesInJmx(node1Url, "http://" + getIpFromHostName(transformHostName1) + ":" + transPort + "/transformation-server",
            TRANSFORMSERV_USERNAME, TRANSFORMSERV_PASS);
        currDate = new SimpleDateFormat("HH:mm").format(new Date().getTime());
        currDatePlusMin = new SimpleDateFormat("HH:mm").format(new Date().getTime() + 60000);
        ShareUser.login(drone, ADMIN_USERNAME);
        ShareUser.createSite(drone, getRandomString(5), SITE_VISIBILITY_PUBLIC);
        ShareUser.openDocumentLibrary(drone).render();
        ShareUser.uploadFileInFolder(drone, docInfo).render();
        assertTrue(Pattern.compile(currDate + "|" + currDatePlusMin + ":\\d{2},\\d{3}  " + REGEX_OK_CONN).matcher(RemoteUtil.getAlfrescoLog(node1Url)).find(),
            "Log on " + node1Url + " doesn't contain information about Ok connection.");
        if(!(node2Url == null))
        {
            assertTrue(Pattern.compile(currDate + "|" + currDatePlusMin + ":\\d{2},\\d{3}  " + REGEX_OK_CONN).matcher(RemoteUtil.getAlfrescoLog(node2Url)).find(),
                "Log on " + node2Url + " doesn't contain information about Ok connection.");
        }

        //The history contains info about successful transformation of the file
        AlfrescoTransformationServerStatusPage transStatusPage = TransformServerUtil.navigateToTransformationServerPage(drone, transformHostName1, transPort);
        AlfrescoTransformationServerHistoryPage transHistoryPage = transStatusPage.openServerHistoryPage(drone).render();
        assertTrue(transHistoryPage.isFileTransformed(docFileName), "File wasn't transformed or doesn't exists");

        //In JMX Console changetransformserver.url - set the url of another transformation server
        TransformServerUtil.setPropertiesInJmx(node1Url, "http://" + getIpFromHostName(transformHostName2) + ":" + transPort + "/transformation-server",
            TRANSFORMSERV_USERNAME, TRANSFORMSERV_PASS);
        currDate = new SimpleDateFormat("HH:mm").format(new Date().getTime());
        currDatePlusMin = new SimpleDateFormat("HH:mm").format(new Date().getTime() + 60000);
        ShareUser.login(drone, ADMIN_USERNAME);
        ShareUser.createSite(drone, getRandomString(5), SITE_VISIBILITY_PUBLIC);
        ShareUser.openDocumentLibrary(drone).render();
        ShareUser.uploadFileInFolder(drone, xlsInfo).render();
        assertTrue(Pattern.compile(currDate + "|" + currDatePlusMin + ":\\d{2},\\d{3}  " + REGEX_OK_CONN).matcher(RemoteUtil.getAlfrescoLog(node1Url)).find(),
            "Log on " + node1Url + " doesn't contain information about Ok connection.");
        if(!(node2Url == null))
        {
            assertTrue(Pattern.compile(currDate + "|" + currDatePlusMin + ":\\d{2},\\d{3}  " + REGEX_OK_CONN).matcher(RemoteUtil.getAlfrescoLog(node2Url)).find(),
                "Log on " + node2Url + " doesn't contain information about Ok connection.");
        }

        //The history contains info about successful transformation of the file
        transStatusPage = TransformServerUtil.navigateToTransformationServerPage(drone, transformHostName2, transPort);
        transHistoryPage = transStatusPage.openServerHistoryPage(drone).render();
        assertTrue(transHistoryPage.isFileTransformed(xlsFileName), "File wasn't transformed or doesn't exists");
    }

    /**
     * Turn off polymorph server
     *
     * @throws Exception
     */
    @Test(groups = {"EnterpriseOnly", "Sanity"})
    public void AONE_8152() throws Exception
    {
        String folderName1 = getFolderName(getRandomString(6));
        String folderName2 = getFolderName(getRandomString(6));
        String siteName = getSiteName(getRandomString(6));
        String docInfo[] = { "doc.doc", folderName1 };
        String docxInfo[] = { "docx.docx", folderName1 };
        String xlsxInfo[] = { "xlsx.xlsx", folderName1 };
        String xlsInfo[] = { "xls.xls", folderName1 };
        String pptInfo[] = { "ppt.ppt", folderName1 };
        String pptxInfo[] = { "pptx.pptx", folderName1 };
        String foldersToSelect[] = { "Documents", folderName2 };

        //Setting props in jmx
        TransformServerUtil.setPropertiesInJmx(node1Url, "http://" + getIpFromHostName(transformHostName1) + ":" + transPort + "/transformation-server",
            TRANSFORMSERV_USERNAME, TRANSFORMSERV_PASS);

        //Any site is created in Share
        ShareUser.login(drone, ADMIN_USERNAME);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        //Any folder is created in the share site (e.g. FOR)
        DocumentLibraryPage docLibPage;
        ShareUserSitePage.createFolder(drone, folderName1, null).render();

        //Any target folder e.g. Transformed is created
        docLibPage = ShareUserSitePage.createFolder(drone, folderName2, null).render();

        //Rule is created for the folder (FOR)
        FolderRulesPage folderRulesPage = docLibPage.getFileDirectoryInfo(folderName1).selectManageRules().render();

        // Fill "Name" field with correct data
        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField(folderName1);

        // Select "Inbound" value from "When" drop-down select control
        WhenSelectorImpl whenSelectorIml = createRulePage.getWhenOptionObj();
        whenSelectorIml.selectInbound();

        // Select 'All items' from "If" drop-down select control
        AbstractIfSelector ifSelector = createRulePage.getIfOptionObj();
        ifSelector.selectIFOption(0);

        //Select transform to pdf and copy
        ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectTransformAndCopy("Adobe PDF Document", siteName, foldersToSelect);

        // Click "Create" button
        FolderRulesPageWithRules ffpage = createRulePage.clickCreate().render();

        //Open Transformation Server and stop Transformation service in Services
        TransformServerUtil.stopTransformServiceRemotely(transformHostName1);

        //Upload scope of documents of supported format (e.g. .doc .xls .docm .dotm .xlsm .xltm, .docx .xlsx .ppt .pptx)
        ffpage.getSiteNav().selectSiteDocumentLibrary().render();
        ShareUser.uploadFileInFolder(drone, docInfo).render();
        ShareUser.uploadFileInFolder(drone, docxInfo).render();
        ShareUser.uploadFileInFolder(drone, xlsxInfo).render();
        ShareUser.uploadFileInFolder(drone, xlsInfo).render();
        ShareUser.uploadFileInFolder(drone, pptInfo).render();
        ShareUser.uploadFileInFolder(drone, pptxInfo).render();
        docLibPage = ShareUser.openDocumentLibrary(drone).render();
        docLibPage.selectFolder(folderName2).render(maxWaitTime);
        docLibPage.render(maxWaitTime);
        assertTrue(docLibPage.isItemVisble("doc.pdf") && docLibPage.isItemVisble("docx.pdf") && docLibPage.isItemVisble("ppt.pdf") && docLibPage
            .isItemVisble("pptx.pdf") && docLibPage.isItemVisble("xls.pdf") && docLibPage.isItemVisble("xlsx.pdf"), "The items weren't transformed by OpenOffice");

        //Verify that Server is not available
        try
        {
            AlfrescoTransformationServerStatusPage alfTrStatusPage = TransformServerUtil.navigateToTransformationServerPage(drone,
                getIpFromHostName(transformHostName1), transPort).render();
            assertFalse(alfTrStatusPage != null, "Transformation page is still available.");
        }
        catch (Exception e)
        {
            assertTrue(e instanceof PageRenderTimeException, "Transformation page is still available.");
        }
        //Start transformation service on Transformation server
        TransformServerUtil.startTransformServiceRemotely(transformHostName1);
        String currDate = new SimpleDateFormat("HH:mm").format(new Date().getTime());
        String currDatePlusMin = new SimpleDateFormat("HH:mm").format(new Date().getTime() + 60000);
        Pattern p = Pattern.compile(currDate + "|" + currDatePlusMin + ":\\d{2},\\d{3}  ");
        String allLog = RemoteUtil.getAlfrescoLog(node1Url);
        Matcher m = p.matcher(allLog);
        int i = 0;
        while (!m.find())
        {
            refreshSharePage(drone);
            webDriverWait(drone, 10000);
            allLog = RemoteUtil.getAlfrescoLog(node1Url);
            m = p.matcher(allLog);
            i++;
            if (i == 5 | m.find())
            {
                break;
            }
        }
        String logAfterDate = allLog.substring(m.start());
        assertTrue(Pattern.compile(REGEX_RE_CONN).matcher(logAfterDate).find(), "Log on " + node1Url + " doesn't contain information about Re-established" +
            " connection.");
        if(!(node2Url == null))
        {
            allLog = RemoteUtil.getAlfrescoLog(node2Url);
            m = p.matcher(allLog);
            i = 0;
            while (!m.find())
            {
                refreshSharePage(drone);
                webDriverWait(drone, 10000);
                allLog = RemoteUtil.getAlfrescoLog(node2Url);
                m = p.matcher(allLog);
                i++;
                if (i == 5 | m.find())
                {
                    break;
                }
            }
            logAfterDate = allLog.substring(m.start());
            assertTrue(Pattern.compile(REGEX_RE_CONN).matcher(logAfterDate).find(), "Log on " + node2Url + " doesn't contain information about Re-established" +
                " connection.");
        }

        //Upload scope of documents of supported format (e.g. .doc .xls .docm .dotm .xlsm .xltm, .docx .xlsx .ppt .pptx);
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName).render();
        ShareUser.uploadFileInFolder(drone, docInfo).render();
        ShareUser.uploadFileInFolder(drone, docxInfo).render();
        ShareUser.uploadFileInFolder(drone, xlsxInfo).render();
        ShareUser.uploadFileInFolder(drone, xlsInfo).render();
        ShareUser.uploadFileInFolder(drone, pptInfo).render();
        ShareUser.uploadFileInFolder(drone, pptxInfo).render();
        docLibPage = ShareUser.openDocumentLibrary(drone).render();
        docLibPage.selectFolder(folderName2).render(maxWaitTime);
        docLibPage.render(maxWaitTime);
        assertTrue(docLibPage.isItemVisble("doc-1.pdf") && docLibPage.isItemVisble("docx-1.pdf") && docLibPage.isItemVisble("ppt-1.pdf") && docLibPage
            .isItemVisble("pptx-1.pdf") && docLibPage.isItemVisble("xls-1.pdf") && docLibPage.isItemVisble("xlsx-1.pdf"), "The items weren't transformed by " +
            "Transformation Server");

        //The documents are successfully uploaded and transformed by Transformation Server's service
        AlfrescoTransformationServerStatusPage transformServPage = TransformServerUtil.navigateToTransformationServerPage(drone, transformHostName1, transPort)
            .render();
        AlfrescoTransformationServerHistoryPage transformHistoryPage = transformServPage.openServerHistoryPage(drone);
        assertTrue(transformHistoryPage.isFileTransformed("doc-1.pdf") && transformHistoryPage.isFileTransformed("docx-1.pdf") &&
            transformHistoryPage.isFileTransformed("ppt-1.pdf") && transformHistoryPage.isFileTransformed("pptx-1.pdf") &&
            transformHistoryPage.isFileTransformed("xls-1.pdf") && transformHistoryPage.isFileTransformed("xlsx-1.pdf"), "Some contents were not transformed");
    }

    /**
     * Non-existent transformation server
     *
     * @throws Exception
     */
    @Test(groups = {"EnterpriseOnly", "Sanity"})
    public void AONE_8153() throws Exception
    {
        String folderName = getFolderName(getRandomString(6));
        String siteName = getSiteName(getRandomString(6));

        //Verify transformation service is up and running
        if (TransformServerUtil.checkTransformationService(transformHostName1))
        {
            TransformServerUtil.startTransformServiceRemotely(transformHostName1);
        }
        //Put invalid transformation host through jmx
        TransformServerUtil.setPropertiesInJmx(node1Url, "http://INVALID:8080/transformation-server", TRANSFORMSERV_USERNAME, TRANSFORMSERV_PASS);

        //Log contains WARN [transformation.client.ConnectionTester] [main] An initial Transformation Server connection could not be established.
        Pattern p1 = Pattern.compile(REGEX_NO_CONN);
        Matcher m1 = p1.matcher(RemoteUtil.getAlfrescoLog(node1Url));
        assertTrue(m1.find(), "Log on " + node1Url + " does not contain WARN message.");
        if(!(node2Url == null))
        {
            m1 = p1.matcher(RemoteUtil.getAlfrescoLog(node2Url));
            assertTrue(m1.find(), "Log on " + node2Url + " does not contain WARN message.");
        }

        //Create a site
        ShareUser.login(drone, ADMIN_USERNAME);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        //Create a folder with rule (trasnform and copy content to Document Library folder)
        DocumentLibraryPage docLibPage = ShareUserSitePage.createFolder(drone, folderName, null).render();

        // create the rule for folder
        FolderRulesPage folderRulesPage = docLibPage.getFileDirectoryInfo(folderName).selectManageRules().render();

        // Fill "Name" field with correct data
        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField(folderName);

        // Select "Inbound" value from "When" drop-down select control
        WhenSelectorImpl whenSelectorIml = createRulePage.getWhenOptionObj();
        whenSelectorIml.selectInbound();

        // Select 'All items' from "If" drop-down select control
        AbstractIfSelector ifSelector = createRulePage.getIfOptionObj();
        ifSelector.selectIFOption(0);

        //Select transform to pdf and copy
        ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectTransformAndCopy("Adobe PDF Document", siteName, "Documents");

        // Click "Create" button
        FolderRulesPageWithRules ffpage = createRulePage.clickCreate().render();

        String fileInfo[] = { docFileName, folderName };
        ffpage.getSiteNav().selectSiteDocumentLibrary();
        ShareUser.uploadFileInFolder(drone, fileInfo).render();
        docLibPage = ShareUser.openDocumentLibrary(drone).render();
        assertTrue(docLibPage.isItemVisble(docFileName.replace(".doc", ".pdf")), "The item wasn't transformed by OpenOffice");

        //Navigate to Transformation node1Url and verify the file wasn't transformed
        AlfrescoTransformationServerStatusPage trStatusPage = TransformServerUtil.navigateToTransformationServerPage(drone, transformHostName1, transPort);
        AlfrescoTransformationServerHistoryPage trHistoryPage = trStatusPage.openServerHistoryPage(drone);
        assertFalse(trHistoryPage.isFileTransformed(docFileName.replace(".doc", ".pdf")));
    }

    private String copyAndApplyAmps(String nodeUrl, String alfrescoPath)
    {
        sshHost = nodeUrl;
        String addrWidPort = PageUtils.getProtocol(shareUrl) + nodeUrl + ":" + nodePort;
        File dirWithMsi = new File(TARGET_PATH_LOCALLY + ARTIFACT_NAME.replace(".zip", ""));
        File[] shareAmp = dirWithMsi.listFiles(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String name)
            {
                return name.contains("share") && name.endsWith("amp");
            }
        });
        File[] repoAmp = dirWithMsi.listFiles(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String name)
            {
                return name.contains("repo") && name.endsWith("amp");
            }
        });
        RemoteUtil.copyToRemoteServer(repoAmp[0].getAbsolutePath(), alfrescoPath + "/amps");
        RemoteUtil.copyToRemoteServer(shareAmp[0].getAbsolutePath(), alfrescoPath + "/amps_share");
        RemoteUtil.initJmxProps(nodeUrl);
        RemoteUtil.stopAlfresco(alfrescoPath);
        RemoteUtil.waitForAlfrescoShutdown(PageUtils.getProtocol(shareUrl) + nodeUrl + ":" + nodePort, 100);
        RemoteUtil.applyRepoAmp(alfrescoPath + "/amps/" + repoAmp[0].getName());
        RemoteUtil.cleanAlfrescoDir();
        RemoteUtil.applyShareAmp(alfrescoPath + "/amps_share/" + shareAmp[0].getName());
        RemoteUtil.cleanShareDir();
        RemoteUtil.startAlfresco(alfrescoPath);
        RemoteUtil.waitForAlfrescoStartup(addrWidPort, 300);
        return RemoteUtil.getAlfrescoLog(nodeUrl);
    }

    private String getIpFromHostName(String hostName)
    {
        String line;
        Pattern patt = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
        try
        {
            Process p = Runtime.getRuntime().exec("ping " + hostName);
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = in.readLine()) != null)
            {
                Matcher m = patt.matcher(line);
                if (m.find())
                {
                    in.close();
                    return line.substring(m.start(0), m.end(0));
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return "";
    }
}
