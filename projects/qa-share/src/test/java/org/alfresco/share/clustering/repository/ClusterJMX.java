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

package org.alfresco.share.clustering.repository;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.site.contentrule.FolderRulesPage;
import org.alfresco.po.share.site.contentrule.FolderRulesPageWithRules;
import org.alfresco.po.share.site.contentrule.createrules.CreateRulePage;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.ActionSelectorEnterpImpl;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.systemsummary.AdminConsoleLink;
import org.alfresco.po.share.systemsummary.RepositoryServerClusteringPage;
import org.alfresco.po.share.systemsummary.SystemSummaryPage;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Sergey Kardash
 */
@Listeners(FailedTestListener.class)
public class ClusterJMX extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(ClusterJMX.class);
    private static String node1Url;
    private static String node2Url;
    private static final String regexUrl = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(:\\d{1,5})?";
    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
    private static final Pattern DOMAIN_PATTERN = Pattern.compile("\\w+(\\.\\w+)*(\\.\\w{2,})");

    protected String siteName = "";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();

        // testUser = getUserNameFreeDomain(testName);
        logger.info("Starting Tests: " + testName);

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

    private SharePage login(WebDrone drone, String userName, String password)
    {
        SharePage resultPage = null;
        try
        {
            resultPage = ShareUser.login(drone, userName, password);
        }
        catch (SkipException se)
        {
            resultPage = ShareUser.getSharePage(drone);
        }
        return resultPage;
    }

    /**
     * Test - AONE-9276:Module "Authentication->managed", change Attributes
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>Monitoring and Management Console go to MBeans</li>
     * <li>open in Authentication->managed->alfresco Attributes bookmark</li>
     * <li>Change any attribute (e.g. alfresco.authentication.authenticateFTP = false)</li>
     * <li>Verify what component is reinitialized across the cluster</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 600000)
    public void AONE_9276() throws Exception
    {

        String path = "Alfresco" + "/";
        String folderName = "Sites";
        String managedAuthentication = "Alfresco:Type=Configuration,Category=Authentication,id1=managed,id2=alfrescoNtlm1";
        String authenticateFTP = "alfresco.authentication.authenticateFTP";

        try
        {
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), authenticateFTP + " must be true");

            // Change any attribute (e.g. alfresco.authentication.authenticateFTP = false)
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), authenticateFTP + " must be true for node1");
            assertTrue(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), authenticateFTP + " must be true node2");
            JmxUtils.setAlfrescoServerProperty(node1Url, managedAuthentication, authenticateFTP, false);
            JmxUtils.invokeAlfrescoServerProperty(node1Url, managedAuthentication, "stop");
            JmxUtils.invokeAlfrescoServerProperty(node1Url, managedAuthentication, "start");

            // Attribute is change
            assertFalse(Boolean.parseBoolean(JmxUtils.getAlfrescoServerProperty(node2Url, managedAuthentication, authenticateFTP).toString()), authenticateFTP
                    + " is true (Attribute is not changed to false for node2)");

            // Verify what component is reinitialized across the cluster
            try
            {
                assertFalse(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), authenticateFTP + " must be false");
            }
            catch (RuntimeException e)
            {
                logger.info("RuntimeException is correct behaviour. " + authenticateFTP + " is false");
            }
        }
        finally
        {
            JmxUtils.setAlfrescoServerProperty(node1Url, managedAuthentication, authenticateFTP, true);
            JmxUtils.invokeAlfrescoServerProperty(node1Url, managedAuthentication, "stop");
            JmxUtils.invokeAlfrescoServerProperty(node1Url, managedAuthentication, "start");
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), authenticateFTP + " must be true for node1");
            assertTrue(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), authenticateFTP + " must be true for node2");
        }

    }

    /**
     * Test - AONE-9277:Module "Authentication->managed", change Operations
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>Monitoring and Management Console go to MBeans</li>
     * <li>open in Authentication->managed->alfresco Attributes bookmark</li>
     * <li>Change any attribute (e.g. alfresco.authentication.authenticateFTP = false)</li>
     * <li>Verify what component is reinitialized across the cluster</li>
     * <li>Click any operation (e.g. revert)</li>
     * <li>Verify what operation is reinitialized across the cluster</li>
     * <li>All values are restored to defaults on server B</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 600000)
    public void AONE_9277() throws Exception
    {

        String path = "Alfresco" + "/";
        String folderName = "Sites";
        String managedAuthentication = "Alfresco:Type=Configuration,Category=Authentication,id1=managed,id2=alfrescoNtlm1";
        String authenticateFTP = "alfresco.authentication.authenticateFTP";

        try
        {
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), authenticateFTP + " must be true");

            // Change any attribute (e.g. alfresco.authentication.authenticateFTP = false)
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), authenticateFTP + " must be true for node1");
            assertTrue(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), authenticateFTP + " must be true node2");
            JmxUtils.setAlfrescoServerProperty(node1Url, managedAuthentication, authenticateFTP, false);
            JmxUtils.invokeAlfrescoServerProperty(node1Url, managedAuthentication, "stop");
            JmxUtils.invokeAlfrescoServerProperty(node1Url, managedAuthentication, "start");

            webDriverWait(drone, 5000);
            // Attribute is change
            assertFalse(Boolean.parseBoolean(JmxUtils.getAlfrescoServerProperty(node2Url, managedAuthentication, authenticateFTP).toString()), authenticateFTP
                    + " is true (Attribute is not changed to false for node2)");

            // Click any operation (e.g. revert)
            JmxUtils.invokeAlfrescoServerProperty(node1Url, managedAuthentication, "revert");
            JmxUtils.invokeAlfrescoServerProperty(node1Url, managedAuthentication, "start");

            // All values are restored to defaults on servers
            assertTrue(Boolean.parseBoolean(JmxUtils.getAlfrescoServerProperty(node1Url, managedAuthentication, authenticateFTP).toString()), authenticateFTP
                    + " is false (Attribute is not changed to true for node1)");
            assertTrue(Boolean.parseBoolean(JmxUtils.getAlfrescoServerProperty(node2Url, managedAuthentication, authenticateFTP).toString()), authenticateFTP
                    + " is false (Attribute is not changed to true for node2)");

            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), authenticateFTP + " must be true for node1");
            assertTrue(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), authenticateFTP + " must be true for node2");

            // Verify what component is reinitialized across the cluster
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), authenticateFTP + " must be true");
        }
        finally
        {
            JmxUtils.setAlfrescoServerProperty(node1Url, managedAuthentication, authenticateFTP, true);
            JmxUtils.invokeAlfrescoServerProperty(node1Url, managedAuthentication, "stop");
            JmxUtils.invokeAlfrescoServerProperty(node1Url, managedAuthentication, "start");
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), authenticateFTP + " must be true for node1");
            assertTrue(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), authenticateFTP + " must be true for node2");
        }

    }

    /**
     * Test - AONE-9277:Module "Authentication->managed", change Operations
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>Monitoring and Management Console go to MBeans</li>
     * <li>open in OOoDirect->default Attributes bookmark</li>
     * <li>Change any attribute (e.g. ooo.enabled = false)</li>
     * <li>Click operation "Start"</li>
     * <li>Verify what component is reinitialized across the cluster</li>
     * <li>Content has transformed on server B</li>
     * <li>Content conversion failed</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 600000)
    public void AONE_9278() throws Exception
    {

        String oDirect = "Alfresco:Type=Configuration,Category=OOoDirect,id1=default";
        String directEnabled = "ooo.enabled";
        String oJodconverter = "Alfresco:Type=Configuration,Category=OOoJodconverter,id1=default";
        String jodconverterEnabled = "jodconverter.enabled";

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName) + System.currentTimeMillis();
        String siteName1 = getSiteName(testName + "-1-") + System.currentTimeMillis();
        String folderName = getFolderName(testName) + System.currentTimeMillis();

        String FILE_DOCX = "docx.docx";
        String FILE_DOCX_PDF = "docx.pdf";
        boolean defaultDirect = Boolean.parseBoolean(JmxUtils.getAlfrescoServerProperty(node1Url, oDirect, directEnabled).toString());
        boolean defaultJodconverter = Boolean.parseBoolean(JmxUtils.getAlfrescoServerProperty(node1Url, oJodconverter, jodconverterEnabled).toString());

        try
        {

            // Change any attribute (e.g. ooo.enabled = false)
            JmxUtils.setAlfrescoServerProperty(node1Url, oDirect, directEnabled, false);
            JmxUtils.invokeAlfrescoServerProperty(node1Url, oDirect, "start");

            JmxUtils.setAlfrescoServerProperty(node1Url, oJodconverter, jodconverterEnabled, false);
            JmxUtils.invokeAlfrescoServerProperty(node1Url, oJodconverter, "start");

            assertFalse(Boolean.parseBoolean(JmxUtils.getAlfrescoServerProperty(node1Url, oDirect, directEnabled).toString()),
                    " is true (Attribute is not changed to false for node1)");
            assertFalse(Boolean.parseBoolean(JmxUtils.getAlfrescoServerProperty(node1Url, oJodconverter, jodconverterEnabled).toString()),
                    " is true (Attribute is not changed to false for node1)");

            dronePropertiesMap.get(drone).setShareUrl(node2Url);

            // Create user
            String[] testUserInfo = new String[] { testUser };
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Any site is created
            ShareUser.login(drone, testUser);
            ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);

            String fileInfo[] = { FILE_DOCX };

            ShareUser.uploadFileInFolder(drone, fileInfo);

            DocumentLibraryPage documentLibraryPage;

            documentLibraryPage = ShareUserSitePage.createFolder(drone, folderName, folderName);
            // Any folder is created, some items are added to folder
            // create folder with copy rule applied to it and files in the folder
            FolderRulesPage folderRulesPage = documentLibraryPage.getFileDirectoryInfo(folderName).selectManageRules().render();
            Assert.assertTrue(folderRulesPage.isPageCorrect(folderName), "Rule page isn't correct");

            CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
            createRulePage.fillNameField("New Copy and Transform Rule Name");
            createRulePage.fillDescriptionField("New Copy and Transform Rule Description");

            ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();

            // copy rule
            actionSelectorEnterpImpl.selectTransformAndCopy("Adobe PDF Document", siteName1, "Documents");

            FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
            Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

            ShareUser.openDocumentLibrary(drone);

            // Content has transformed on server B
            documentLibraryPage = ShareUserSitePage.copyOrMoveToFolder(drone, siteName1, FILE_DOCX, new String[] { folderName }, true);

            ShareUser.openSitesDocumentLibrary(drone, siteName1);

            // Content conversion failed
            Assert.assertFalse(documentLibraryPage.isFileVisible(FILE_DOCX_PDF), "Content conversion isn't failed");

            JmxUtils.setAlfrescoServerProperty(node1Url, oDirect, directEnabled, true);
            JmxUtils.invokeAlfrescoServerProperty(node1Url, oDirect, "stop");
            JmxUtils.invokeAlfrescoServerProperty(node1Url, oDirect, "start");
            assertTrue(Boolean.parseBoolean(JmxUtils.getAlfrescoServerProperty(node1Url, oDirect, directEnabled).toString()), directEnabled
                    + " is false (Attribute is not changed to true for node1)");
            assertTrue(Boolean.parseBoolean(JmxUtils.getAlfrescoServerProperty(node2Url, oDirect, directEnabled).toString()), directEnabled
                    + " is false (Attribute is not changed to true for node2)");

            // wait 'The OpenOffice connection was re-established.'
            webDriverWait(drone, 120000);
            ShareUser.openDocumentLibrary(drone);

            // Content has transformed on server B
            documentLibraryPage = ShareUserSitePage.copyOrMoveToFolder(drone, siteName1, FILE_DOCX, new String[] { folderName }, true);
            ShareUser.openSitesDocumentLibrary(drone, siteName1);

            // Content conversion passed
            Assert.assertTrue(documentLibraryPage.isFileVisible(FILE_DOCX_PDF), "Content conversion is failed");

        }
        finally
        {
            JmxUtils.setAlfrescoServerProperty(node1Url, oDirect, directEnabled, defaultDirect);
            JmxUtils.invokeAlfrescoServerProperty(node1Url, oDirect, "start");

            JmxUtils.setAlfrescoServerProperty(node1Url, oJodconverter, jodconverterEnabled, defaultJodconverter);
            JmxUtils.invokeAlfrescoServerProperty(node1Url, oJodconverter, "start");
            assertFalse(Boolean.parseBoolean(JmxUtils.getAlfrescoServerProperty(node1Url, oDirect, directEnabled).toString()),
                    " is true (Attribute is not changed to false for node1)");
            assertTrue(Boolean.parseBoolean(JmxUtils.getAlfrescoServerProperty(node1Url, oJodconverter, jodconverterEnabled).toString()),
                    " is true (Attribute is not changed to false for node1)");

        }

    }

    /**
     * Test - AONE-9279:Module "OOodirect->default", change Operations
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>Monitoring and Management Console go to MBeans</li>
     * <li>open in OOoDirect->default Attributes bookmark</li>
     * <li>Click any operation (e.g. revert)</li>
     * <li>Click operation "Start"</li>
     * <li>Verify what component is reinitialized across the cluster</li>
     * <li>Content has transformed on server B</li>
     * <li>All values are restored to defaults successfully</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 600000)
    public void AONE_9279() throws Exception
    {

        String oDirect = "Alfresco:Type=Configuration,Category=OOoDirect,id1=default";
        String directEnabled = "ooo.enabled";
        String oJodconverter = "Alfresco:Type=Configuration,Category=OOoJodconverter,id1=default";
        String jodconverterEnabled = "jodconverter.enabled";

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName) + System.currentTimeMillis();
        String siteName1 = getSiteName(testName + "-1-") + System.currentTimeMillis();
        String folderName = getFolderName(testName) + System.currentTimeMillis();

        String FILE_DOCX = "docx.docx";
        String FILE_DOCX_PDF = "docx.pdf";

        boolean defaultDirect = Boolean.parseBoolean(JmxUtils.getAlfrescoServerProperty(node1Url, oDirect, directEnabled).toString());
        boolean defaultJodconverter = Boolean.parseBoolean(JmxUtils.getAlfrescoServerProperty(node1Url, oJodconverter, jodconverterEnabled).toString());

        try
        {

            // Check defaults values
            if (defaultDirect)
            {
                logger.info("ooo.enabled = true by default");
                JmxUtils.setAlfrescoServerProperty(node1Url, oDirect, directEnabled, false);
                JmxUtils.invokeAlfrescoServerProperty(node1Url, oDirect, "start");
            }
            else
            {
                logger.info("ooo.enabled = false by default");
                JmxUtils.setAlfrescoServerProperty(node1Url, oDirect, directEnabled, true);
                JmxUtils.invokeAlfrescoServerProperty(node1Url, oDirect, "start");
            }

            JmxUtils.setAlfrescoServerProperty(node1Url, oJodconverter, jodconverterEnabled, false);
            JmxUtils.invokeAlfrescoServerProperty(node1Url, oJodconverter, "start");

            if (defaultDirect)
            {
                logger.info("ooo.enabled changed to false");
                assertFalse(Boolean.parseBoolean(JmxUtils.getAlfrescoServerProperty(node1Url, oDirect, directEnabled).toString()),
                        " is true (Attribute is not changed to false for node1)");
            }
            else
            {
                logger.info("ooo.enabled changed to true");
                assertTrue(Boolean.parseBoolean(JmxUtils.getAlfrescoServerProperty(node1Url, oDirect, directEnabled).toString()),
                        " is false (Attribute is not changed to true for node1)");
            }
            assertFalse(Boolean.parseBoolean(JmxUtils.getAlfrescoServerProperty(node1Url, oJodconverter, jodconverterEnabled).toString()),
                    " is true (Attribute is not changed to false for node1)");

            if (defaultDirect)
            {
                logger.info("ooo.enabled revert to true");
                JmxUtils.invokeAlfrescoServerProperty(node1Url, oDirect, "revert");
                JmxUtils.invokeAlfrescoServerProperty(node1Url, oDirect, "start");
                assertTrue(Boolean.parseBoolean(JmxUtils.getAlfrescoServerProperty(node1Url, oDirect, directEnabled).toString()),
                        " is false (Attribute is not reverted to true for node1)");
            }
            else
            {
                logger.info("ooo.enabled revert to false");
                JmxUtils.invokeAlfrescoServerProperty(node1Url, oDirect, "revert");
                JmxUtils.invokeAlfrescoServerProperty(node1Url, oDirect, "start");
                assertFalse(Boolean.parseBoolean(JmxUtils.getAlfrescoServerProperty(node1Url, oDirect, directEnabled).toString()),
                        " is true (Attribute is not reverted to false for node1)");
            }

            dronePropertiesMap.get(drone).setShareUrl(node2Url);

            // Create user
            String[] testUserInfo = new String[] { testUser };
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Any site is created
            ShareUser.login(drone, testUser);
            ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);

            String fileInfo[] = { FILE_DOCX };

            ShareUser.uploadFileInFolder(drone, fileInfo);

            DocumentLibraryPage documentLibraryPage;

            documentLibraryPage = ShareUserSitePage.createFolder(drone, folderName, folderName);
            // Any folder is created, some items are added to folder
            // create folder with copy rule applied to it and files in the folder
            FolderRulesPage folderRulesPage = documentLibraryPage.getFileDirectoryInfo(folderName).selectManageRules().render();
            Assert.assertTrue(folderRulesPage.isPageCorrect(folderName), "Rule page isn't correct");

            CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
            createRulePage.fillNameField("New Copy and Transform Rule Name");
            createRulePage.fillDescriptionField("New Copy and Transform Rule Description");

            ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();

            // copy rule
            actionSelectorEnterpImpl.selectTransformAndCopy("Adobe PDF Document", siteName1, "Documents");

            FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
            Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

            ShareUser.openDocumentLibrary(drone);

            if (defaultDirect)
            {
                logger.info("Content must be successfully transformed");
                webDriverWait(drone, 120000);
                // Content has transformed on server B
                documentLibraryPage = ShareUserSitePage.copyOrMoveToFolder(drone, siteName1, FILE_DOCX, new String[] { folderName }, true);

                ShareUser.openSitesDocumentLibrary(drone, siteName1);
                // Content conversion passed
                Assert.assertTrue(documentLibraryPage.isFileVisible(FILE_DOCX_PDF), "Content conversion is failed");
            }
            else
            {
                logger.info("Content mustn't be successfully transformed");
                webDriverWait(drone, 120000);
                // Content has transformed on server B
                documentLibraryPage = ShareUserSitePage.copyOrMoveToFolder(drone, siteName1, FILE_DOCX, new String[] { folderName }, true);

                ShareUser.openSitesDocumentLibrary(drone, siteName1);
                Assert.assertFalse(documentLibraryPage.isFileVisible(FILE_DOCX_PDF), "Content conversion isn't failed");
            }
        }
        finally
        {
            JmxUtils.setAlfrescoServerProperty(node1Url, oDirect, directEnabled, defaultDirect);
            JmxUtils.invokeAlfrescoServerProperty(node1Url, oDirect, "start");

            JmxUtils.setAlfrescoServerProperty(node1Url, oJodconverter, jodconverterEnabled, defaultJodconverter);
            JmxUtils.invokeAlfrescoServerProperty(node1Url, oJodconverter, "start");

        }

    }

    /**
     * Test - AONE-9280:Module "OOoJodconverter->default", change Attributes
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>Monitoring and Management Console go to MBeans</li>
     * <li>open in OOoJodconverter->default Attributes bookmark</li>
     * <li>Change any attribute (e.g. jodconverter.taskExecutionTimeout = 1)</li>
     * <li>Click operation "Start"</li>
     * <li>Verify what component is reinitialized across the cluster</li>
     * <li>Content has transformed on server B</li>
     * <li>Content conversion failed</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 600000)
    public void AONE_9280() throws Exception
    {

        String oDirect = "Alfresco:Type=Configuration,Category=OOoDirect,id1=default";
        String directEnabled = "ooo.enabled";
        String oJodconverter = "Alfresco:Type=Configuration,Category=OOoJodconverter,id1=default";
        String taskExecutionTimeout = "jodconverter.taskExecutionTimeout";

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName) + System.currentTimeMillis();
        String siteName1 = getSiteName(testName + "-1-") + System.currentTimeMillis();
        String folderName = getFolderName(testName) + System.currentTimeMillis();

        String FILE_DOCX = "docx.docx";
        String FILE_DOCX_PDF = "docx.pdf";
        boolean defaultDirect = Boolean.parseBoolean(JmxUtils.getAlfrescoServerProperty(node1Url, oDirect, directEnabled).toString());
        String defaultExecutionTimeout = JmxUtils.getAlfrescoServerProperty(node1Url, oJodconverter, taskExecutionTimeout).toString();
        String executionTimeout = "120000";

        try
        {

            assertTrue(defaultExecutionTimeout.equals(executionTimeout), "Task execution value isn't '120000'");

            if (defaultDirect)
            {
                JmxUtils.setAlfrescoServerProperty(node1Url, oDirect, directEnabled, false);
                JmxUtils.invokeAlfrescoServerProperty(node1Url, oDirect, "start");
            }

            JmxUtils.setAlfrescoServerProperty(node1Url, oJodconverter, taskExecutionTimeout, "1");
            JmxUtils.invokeAlfrescoServerProperty(node1Url, oJodconverter, "start");
            assertFalse(Boolean.parseBoolean(JmxUtils.getAlfrescoServerProperty(node1Url, oDirect, directEnabled).toString()),
                    " is true (Attribute is not changed to false for node1)");
            assertTrue(JmxUtils.getAlfrescoServerProperty(node1Url, oJodconverter, taskExecutionTimeout).toString().equals("1"),
                    "Task execution value isn't changed(Attribute is not changed to '1' for node1)");

            dronePropertiesMap.get(drone).setShareUrl(node2Url);

            // Create user
            String[] testUserInfo = new String[] { testUser };
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Any site is created
            ShareUser.login(drone, testUser);
            ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);

            String fileInfo[] = { FILE_DOCX };

            ShareUser.uploadFileInFolder(drone, fileInfo);

            DocumentLibraryPage documentLibraryPage;

            documentLibraryPage = ShareUserSitePage.createFolder(drone, folderName, folderName);
            // Any folder is created, some items are added to folder
            // create folder with copy rule applied to it and files in the folder
            FolderRulesPage folderRulesPage = documentLibraryPage.getFileDirectoryInfo(folderName).selectManageRules().render();
            Assert.assertTrue(folderRulesPage.isPageCorrect(folderName), "Rule page isn't correct");

            CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
            createRulePage.fillNameField("New Copy and Transform Rule Name");
            createRulePage.fillDescriptionField("New Copy and Transform Rule Description");

            ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();

            // copy rule
            actionSelectorEnterpImpl.selectTransformAndCopy("Adobe PDF Document", siteName1, "Documents");

            FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
            Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

            ShareUser.openDocumentLibrary(drone);

            // Content has transformed on server B
            documentLibraryPage = ShareUserSitePage.copyOrMoveToFolder(drone, siteName1, FILE_DOCX, new String[] { folderName }, true);

            ShareUser.openSitesDocumentLibrary(drone, siteName1);

            // Content conversion failed
            Assert.assertFalse(documentLibraryPage.isFileVisible(FILE_DOCX_PDF), "Content conversion isn't failed");

            JmxUtils.setAlfrescoServerProperty(node1Url, oJodconverter, taskExecutionTimeout, defaultExecutionTimeout);
            JmxUtils.invokeAlfrescoServerProperty(node1Url, oJodconverter, "start");
            assertTrue(JmxUtils.getAlfrescoServerProperty(node1Url, oJodconverter, taskExecutionTimeout).toString().equals(executionTimeout),
                    " Task execution value isn't changed to default (Attribute is not changed to true for node1)");
            assertTrue(JmxUtils.getAlfrescoServerProperty(node2Url, oJodconverter, taskExecutionTimeout).toString().equals(executionTimeout),
                    " Task execution value isn't changed to default (Attribute is not changed to true for node1)");

            ShareUser.openDocumentLibrary(drone);

            // Content has transformed on server B
            documentLibraryPage = ShareUserSitePage.copyOrMoveToFolder(drone, siteName1, FILE_DOCX, new String[] { folderName }, true);
            ShareUser.openSitesDocumentLibrary(drone, siteName1);

            // Content conversion passed
            Assert.assertTrue(documentLibraryPage.isFileVisible(FILE_DOCX_PDF), "Content conversion is failed");

        }
        finally
        {
            JmxUtils.setAlfrescoServerProperty(node1Url, oDirect, directEnabled, defaultDirect);
            JmxUtils.invokeAlfrescoServerProperty(node1Url, oDirect, "start");
            JmxUtils.setAlfrescoServerProperty(node1Url, oJodconverter, taskExecutionTimeout, defaultExecutionTimeout);
            JmxUtils.invokeAlfrescoServerProperty(node1Url, oJodconverter, "start");
            assertFalse(Boolean.parseBoolean(JmxUtils.getAlfrescoServerProperty(node1Url, oDirect, directEnabled).toString()), directEnabled
                    + " is true (Attribute is not changed to false for node1)");
            assertTrue(JmxUtils.getAlfrescoServerProperty(node1Url, oJodconverter, taskExecutionTimeout).toString().equals(executionTimeout),
                    "Task Execution Timeout isn't '120000'");

        }

    }

    /**
     * Test - AONE-9281:Module "OOoJodconverter->default", change Operations
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>Monitoring and Management Console go to MBeans</li>
     * <li>open in OOoJodconverter->default Attributes bookmark</li>
     * <li>Change any attribute (e.g. jodconverter.taskExecutionTimeout = 1)</li>
     * <li>Click operation "Start"</li>
     * <li>Click any operation (e.g. revert)</li>
     * <li>click operation "Start"</li>
     * <li>Verify what component is reinitialized across the cluster</li>
     * <li>Content has transformed on server B</li>
     * <li>Content conversion passed</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 600000)
    public void AONE_9281() throws Exception
    {

        String oDirect = "Alfresco:Type=Configuration,Category=OOoDirect,id1=default";
        String directEnabled = "ooo.enabled";
        String oJodconverter = "Alfresco:Type=Configuration,Category=OOoJodconverter,id1=default";
        String taskExecutionTimeout = "jodconverter.taskExecutionTimeout";

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName) + System.currentTimeMillis();
        String siteName1 = getSiteName(testName + "-1-") + System.currentTimeMillis();
        String folderName = getFolderName(testName) + System.currentTimeMillis();

        String FILE_DOCX = "docx.docx";
        String FILE_DOCX_PDF = "docx.pdf";
        boolean defaultDirect = Boolean.parseBoolean(JmxUtils.getAlfrescoServerProperty(node1Url, oDirect, directEnabled).toString());
        String defaultExecutionTimeout = JmxUtils.getAlfrescoServerProperty(node1Url, oJodconverter, taskExecutionTimeout).toString();
        String executionTimeout = "120000";

        try
        {
            assertTrue(defaultExecutionTimeout.equals(executionTimeout), "Task execution value isn't '120000'");

            if (defaultDirect)
            {
                JmxUtils.setAlfrescoServerProperty(node1Url, oDirect, directEnabled, false);
                JmxUtils.invokeAlfrescoServerProperty(node1Url, oDirect, "start");
            }

            JmxUtils.setAlfrescoServerProperty(node1Url, oJodconverter, taskExecutionTimeout, "1");
            JmxUtils.invokeAlfrescoServerProperty(node1Url, oJodconverter, "start");
            assertFalse(Boolean.parseBoolean(JmxUtils.getAlfrescoServerProperty(node1Url, oDirect, directEnabled).toString()),
                    " is true (Attribute is not changed to false for node1)");
            assertTrue(JmxUtils.getAlfrescoServerProperty(node1Url, oJodconverter, taskExecutionTimeout).toString().equals("1"),
                    "Task execution value isn't changed(Attribute is not changed to '1' for node1)");

            dronePropertiesMap.get(drone).setShareUrl(node2Url);

            // Create user
            String[] testUserInfo = new String[] { testUser };
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Any site is created
            ShareUser.login(drone, testUser);
            ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);

            String fileInfo[] = { FILE_DOCX };

            ShareUser.uploadFileInFolder(drone, fileInfo);

            DocumentLibraryPage documentLibraryPage;

            documentLibraryPage = ShareUserSitePage.createFolder(drone, folderName, folderName);
            // Any folder is created, some items are added to folder
            // create folder with copy rule applied to it and files in the folder
            FolderRulesPage folderRulesPage = documentLibraryPage.getFileDirectoryInfo(folderName).selectManageRules().render();
            Assert.assertTrue(folderRulesPage.isPageCorrect(folderName), "Rule page isn't correct");

            CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
            createRulePage.fillNameField("New Copy and Transform Rule Name");
            createRulePage.fillDescriptionField("New Copy and Transform Rule Description");

            ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();

            // copy rule
            actionSelectorEnterpImpl.selectTransformAndCopy("Adobe PDF Document", siteName1, "Documents");

            FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
            Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

            // Click any operation (e.g. revert)
            JmxUtils.invokeAlfrescoServerProperty(node1Url, oJodconverter, "revert");
            JmxUtils.invokeAlfrescoServerProperty(node1Url, oJodconverter, "start");
            assertTrue(JmxUtils.getAlfrescoServerProperty(node1Url, oJodconverter, taskExecutionTimeout).toString().equals(executionTimeout),
                    " Task execution value isn't changed to default (Attribute is not changed for node1)");
            assertTrue(JmxUtils.getAlfrescoServerProperty(node2Url, oJodconverter, taskExecutionTimeout).toString().equals(executionTimeout),
                    " Task execution value isn't changed to default (Attribute is not changed for node1)");

            ShareUser.openDocumentLibrary(drone);

            // Content has transformed on server B
            documentLibraryPage = ShareUserSitePage.copyOrMoveToFolder(drone, siteName1, FILE_DOCX, new String[] { folderName }, true);
            ShareUser.openSitesDocumentLibrary(drone, siteName1);

            // Content conversion passed
            Assert.assertTrue(documentLibraryPage.isFileVisible(FILE_DOCX_PDF), "Content conversion is failed");

        }
        finally
        {
            JmxUtils.setAlfrescoServerProperty(node1Url, oDirect, directEnabled, defaultDirect);
            JmxUtils.invokeAlfrescoServerProperty(node1Url, oDirect, "start");
            JmxUtils.setAlfrescoServerProperty(node1Url, oJodconverter, taskExecutionTimeout, defaultExecutionTimeout);
            JmxUtils.invokeAlfrescoServerProperty(node1Url, oJodconverter, "start");
            assertFalse(Boolean.parseBoolean(JmxUtils.getAlfrescoServerProperty(node1Url, oDirect, directEnabled).toString()), directEnabled
                    + " is true (Attribute is not changed to false for node1)");
            assertTrue(JmxUtils.getAlfrescoServerProperty(node1Url, oJodconverter, taskExecutionTimeout).toString().equals(executionTimeout),
                    "Task Execution Timeout isn't '120000'");

        }

    }

    /**
     * Test - AONE-9282:Module "fileServers->default", change Attributes
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>Monitoring and Management Console go to MBeans</li>
     * <li>open in open in fileServers->default Attributes bookmark</li>
     * <li>Change any attribute (e.g. ftp.enabled = false)</li>
     * <li>Verify what component is reinitialized across the cluster</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 600000)
    public void AONE_9282() throws Exception
    {

        String path = "Alfresco" + "/";
        String folderName = "Sites";
        String fileServers = "Alfresco:Type=Configuration,Category=fileServers,id1=default";
        String ftpEnabled = "ftp.enabled";

        try
        {

            // Change any attribute (e.g. ftp.enabled = false)
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), ftpEnabled + " must be true for node1");
            assertTrue(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), ftpEnabled + " must be true node2");
            JmxUtils.setAlfrescoServerProperty(node1Url, fileServers, ftpEnabled, false);
            JmxUtils.invokeAlfrescoServerProperty(node1Url, fileServers, "stop");
            JmxUtils.invokeAlfrescoServerProperty(node1Url, fileServers, "start");

            // Attribute is change
            assertFalse(Boolean.parseBoolean(JmxUtils.getAlfrescoServerProperty(node2Url, fileServers, ftpEnabled).toString()), ftpEnabled
                    + " is true (Attribute is not changed to false for node2)");

            // Verify what component is reinitialized across the cluster
            try
            {
                assertFalse(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), ftpEnabled + " must be false");
            }
            catch (RuntimeException e)
            {
                logger.info("RuntimeException is correct behaviour. " + ftpEnabled + " is false");
            }
        }
        finally
        {
            JmxUtils.setAlfrescoServerProperty(node1Url, fileServers, ftpEnabled, true);
            JmxUtils.invokeAlfrescoServerProperty(node1Url, fileServers, "stop");
            JmxUtils.invokeAlfrescoServerProperty(node1Url, fileServers, "start");
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), ftpEnabled + " must be true for node1");
            assertTrue(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), ftpEnabled + " must be true for node2");
        }

    }

    /**
     * Test - AONE-9283:Module "fileServer->default", change Operations
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>Monitoring and Management Console go to MBeans</li>
     * <li>open in open in fileServers->default Attributes bookmark</li>
     * <li>Change any attribute (e.g. ftp.enabled = false)</li>
     * <li>Click any operation (e.g. revert)</li>
     * <li>Click operation "Start"</li>
     * <li>Verify what component is reinitialized across the cluster</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 600000)
    public void AONE_9283() throws Exception
    {

        String path = "Alfresco" + "/";
        String folderName = "Sites";
        String fileServers = "Alfresco:Type=Configuration,Category=fileServers,id1=default";
        String ftpEnabled = "ftp.enabled";

        try
        {

            // Change any attribute (e.g. ftp.enabled = false)
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), ftpEnabled + " must be true for node1");
            assertTrue(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), ftpEnabled + " must be true node2");
            JmxUtils.setAlfrescoServerProperty(node1Url, fileServers, ftpEnabled, false);
            JmxUtils.invokeAlfrescoServerProperty(node1Url, fileServers, "stop");
            JmxUtils.invokeAlfrescoServerProperty(node1Url, fileServers, "start");

            // Attribute is change
            assertFalse(Boolean.parseBoolean(JmxUtils.getAlfrescoServerProperty(node2Url, fileServers, ftpEnabled).toString()), ftpEnabled
                    + " is true (Attribute is not changed to false for node2)");

            // Verify what component is reinitialized across the cluster
            try
            {
                assertFalse(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), ftpEnabled + " must be false");
            }
            catch (RuntimeException e)
            {
                logger.info("RuntimeException is correct behaviour. " + ftpEnabled + " is false");
            }

            JmxUtils.setAlfrescoServerProperty(node1Url, fileServers, ftpEnabled, true);
            JmxUtils.invokeAlfrescoServerProperty(node1Url, fileServers, "revert");
            JmxUtils.invokeAlfrescoServerProperty(node1Url, fileServers, "start");

            // Attribute is change
            assertTrue(Boolean.parseBoolean(JmxUtils.getAlfrescoServerProperty(node2Url, fileServers, ftpEnabled).toString()), ftpEnabled
                    + " is false (Attribute is not changed to true for node2)");

            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), ftpEnabled + " must be true for node1");
            assertTrue(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), ftpEnabled + " must be true for node2");

        }
        finally
        {
            JmxUtils.setAlfrescoServerProperty(node1Url, fileServers, ftpEnabled, true);
            JmxUtils.invokeAlfrescoServerProperty(node1Url, fileServers, "stop");
            JmxUtils.invokeAlfrescoServerProperty(node1Url, fileServers, "start");
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), ftpEnabled + " must be true for node1");
            assertTrue(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), ftpEnabled + " must be true for node2");
        }

    }

    /**
     * Test - AONE-9284:Module "imap->default", change Attributes
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>Monitoring and Management Console go to MBeans</li>
     * <li>open in imap->default Attributes bookmark</li>
     * <li>Change any attribute (e.g. imap.enabled = false)</li>
     * <li>Click any operation (e.g. revert)</li>
     * <li>Click operation "Start"</li>
     * <li>Verify what component is reinitialized across the cluster</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 600000)
    public void AONE_9284() throws Exception
    {

        String imapObject = "Alfresco:Type=Configuration,Category=imap,id1=default";
        String imapEnabled = "imap.server.imap.enabled";
        String imapPort = "143";
        boolean imapEnabledDefault = Boolean.parseBoolean(JmxUtils.getAlfrescoServerProperty(node1Url, imapObject, imapEnabled).toString());

        try
        {
            // imap is enabled
            Assert.assertTrue(imapEnabledDefault, "imap.server.imap.enabled isn't true by default");

            JmxUtils.setAlfrescoServerProperty(node2Url, imapObject, imapEnabled, true);
            JmxUtils.invokeAlfrescoServerProperty(node2Url, imapObject, "stop");
            JmxUtils.invokeAlfrescoServerProperty(node2Url, imapObject, "start");

            assertTrue(TelnetUtil.connectServer(node2Url, imapPort), "Component isn't reinitialized across the cluster (imap is disable)");

            // Change any attribute (e.g. imap.enabled = false)
            JmxUtils.setAlfrescoServerProperty(node1Url, imapObject, imapEnabled, false);
            // click operation "Start"
            JmxUtils.invokeAlfrescoServerProperty(node1Url, imapObject, "stop");
            JmxUtils.invokeAlfrescoServerProperty(node1Url, imapObject, "start");

            // Verify what component is reinitialized across the cluster (Connected to imap on server B)
            assertFalse(TelnetUtil.connectServer(node2Url, imapPort), "Component isn't reinitialized across the cluster (imap is enable)");

            JmxUtils.setAlfrescoServerProperty(node1Url, imapObject, imapEnabled, true);
            JmxUtils.invokeAlfrescoServerProperty(node1Url, imapObject, "stop");
            JmxUtils.invokeAlfrescoServerProperty(node1Url, imapObject, "start");
            assertTrue(TelnetUtil.connectServer(node2Url, imapPort));
            assertTrue(TelnetUtil.connectServer(node2Url, imapPort), "Component isn't reinitialized across the cluster (imap is disable)");

        }
        finally
        {
            JmxUtils.setAlfrescoServerProperty(node1Url, imapObject, imapEnabled, true);
            JmxUtils.invokeAlfrescoServerProperty(node1Url, imapObject, "stop");
            JmxUtils.invokeAlfrescoServerProperty(node1Url, imapObject, "start");
        }

    }

    /**
     * Test - AONE-9285:Module "imap->default", change Operations
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>Monitoring and Management Console go to MBeans</li>
     * <li>open in imap->default Attributes bookmark</li>
     * <li>Click any operation (e.g. revert)</li>
     * <li>Click any operation (e.g. revert)</li>
     * <li>Click operation "Start"</li>
     * <li>Verify what component is reinitialized across the cluster</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 600000)
    public void AONE_9285() throws Exception
    {

        String imapObject = "Alfresco:Type=Configuration,Category=imap,id1=default";
        String imapEnabled = "imap.server.imap.enabled";
        String imapPort = "143";
        boolean imapEnabledDefault = Boolean.parseBoolean(JmxUtils.getAlfrescoServerProperty(node2Url, imapObject, imapEnabled).toString());

        try
        {
            JmxUtils.setAlfrescoServerProperty(node1Url, imapObject, imapEnabled, true);
            JmxUtils.invokeAlfrescoServerProperty(node1Url, imapObject, "stop");
            JmxUtils.invokeAlfrescoServerProperty(node1Url, imapObject, "start");
            // imap is enabled
            Assert.assertTrue(imapEnabledDefault, "imap.server.imap.enabled isn't true by default");

            JmxUtils.setAlfrescoServerProperty(node1Url, imapObject, imapEnabled, false);
            JmxUtils.invokeAlfrescoServerProperty(node1Url, imapObject, "stop");
            JmxUtils.invokeAlfrescoServerProperty(node1Url, imapObject, "start");
            assertFalse(TelnetUtil.connectServer(node2Url, imapPort), "Component isn't reinitialized across the cluster (imap is enable)");

            // Click any operation (e.g. revert)
            JmxUtils.invokeAlfrescoServerProperty(node1Url, imapObject, "revert");
            // click operation "Start"
            JmxUtils.invokeAlfrescoServerProperty(node1Url, imapObject, "stop");
            JmxUtils.invokeAlfrescoServerProperty(node1Url, imapObject, "start");
            // Verify what component is reinitialized across the cluster (Connected to imap on server B)
            assertTrue(TelnetUtil.connectServer(node2Url, imapPort), "Component isn't reinitialized across the cluster (imap is disable, imap isn't reverted)");

        }
        finally
        {

            JmxUtils.setAlfrescoServerProperty(node1Url, imapObject, imapEnabled, true);
            JmxUtils.invokeAlfrescoServerProperty(node1Url, imapObject, "stop");
            JmxUtils.invokeAlfrescoServerProperty(node1Url, imapObject, "start");
        }

    }

    /**
     * Test - AONE-9286:Module "sysAdmin->default", change Attributes
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>Monitoring and Management Console go to MBeans</li>
     * <li>open in sysAdmin->default Attributes bookmark</li>
     * <li>Change any attribute (e.g. server.allowedusers)</li>
     * <li>Click operation "Start"</li>
     * <li>Verify what component is reinitialized across the cluster</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 600000)
    public void AONE_9286() throws Exception
    {

        String sysAdminObject = "Alfresco:Type=Configuration,Category=sysAdmin,id1=default";
        String allowedUsers = "server.allowedusers";

        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + "-1");
        String testUser2 = getUserNameFreeDomain(testName + "-2");

        try
        {

            dronePropertiesMap.get(drone).setShareUrl(node1Url);

            // Create user1
            String[] testUserInfo1 = new String[] { testUser1 };
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo1);

            // Create user1
            String[] testUserInfo2 = new String[] { testUser2 };
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

            // Try login by user1
            SharePage resultPage = login(drone, testUser1, DEFAULT_PASSWORD).render();
            resultPage.render();
            Assert.assertTrue(resultPage.isLoggedIn(), testUser1 + "isn't logged");
            ShareUser.logout(drone);

            // Try login by user2
            resultPage = login(drone, testUser2, DEFAULT_PASSWORD);
            resultPage.render();
            Assert.assertTrue(resultPage.isLoggedIn(), testUser2 + "isn't logged");
            ShareUser.logout(drone);

            JmxUtils.setAlfrescoServerProperty(node1Url, sysAdminObject, allowedUsers, testUser1);
            JmxUtils.invokeAlfrescoServerProperty(node1Url, sysAdminObject, "start");
            Assert.assertTrue(JmxUtils.getAlfrescoServerProperty(node1Url, sysAdminObject, allowedUsers).toString().equals(testUser1), "Property "
                    + allowedUsers + " isn't set");

            dronePropertiesMap.get(drone).setShareUrl(node2Url);

            // Try login by user1
            resultPage = login(drone, testUser1, DEFAULT_PASSWORD).render();
            resultPage.render();
            Assert.assertTrue(resultPage.isLoggedIn(), testUser1 + "isn't logged");
            ShareUser.logout(drone);

            // Try login by user1
            resultPage = login(drone, testUser2, DEFAULT_PASSWORD).render();
            resultPage.render();
            Assert.assertFalse(resultPage.isLoggedIn(), testUser2 + "is logged");
        }
        finally
        {
            JmxUtils.setAlfrescoServerProperty(node1Url, sysAdminObject, allowedUsers, "");
            JmxUtils.invokeAlfrescoServerProperty(node1Url, sysAdminObject, "start");
            Assert.assertTrue(JmxUtils.getAlfrescoServerProperty(node1Url, sysAdminObject, allowedUsers).toString().equals(""), "Property " + allowedUsers
                    + " isn't cleaned");

        }

    }

    /**
     * Test - AONE-9287:Module "sysAdmin->default", change Operations
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>Monitoring and Management Console go to MBeans</li>
     * <li>open in sysAdmin->default Attributes bookmark</li>
     * <li>Change any attribute (e.g. server.allowedusers)</li> *
     * <li>Click operation "Start"</li>
     * <li>Click any operation (e.g. revert)</li>
     * <li>Click operation "Start"</li>
     * <li>Verify what component is reinitialized across the cluster</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 600000)
    public void AONE_9287() throws Exception
    {

        String sysAdminObject = "Alfresco:Type=Configuration,Category=sysAdmin,id1=default";
        String allowedUsers = "server.allowedusers";

        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + "-1");
        String testUser2 = getUserNameFreeDomain(testName + "-2");

        try
        {

            dronePropertiesMap.get(drone).setShareUrl(node1Url);

            // Create user1
            String[] testUserInfo1 = new String[] { testUser1 };
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo1);

            // Create user1
            String[] testUserInfo2 = new String[] { testUser2 };
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

            JmxUtils.setAlfrescoServerProperty(node1Url, sysAdminObject, allowedUsers, testUser1);
            JmxUtils.invokeAlfrescoServerProperty(node1Url, sysAdminObject, "start");
            Assert.assertTrue(JmxUtils.getAlfrescoServerProperty(node1Url, sysAdminObject, allowedUsers).toString().equals(testUser1), "Property "
                    + allowedUsers + " isn't set");

            dronePropertiesMap.get(drone).setShareUrl(node2Url);

            // Try login by user1
            SharePage resultPage = login(drone, testUser1, DEFAULT_PASSWORD).render();
            resultPage.render();
            Assert.assertTrue(resultPage.isLoggedIn(), testUser1 + "isn't logged");
            ShareUser.logout(drone);

            // Try login by user2
            resultPage = login(drone, testUser2, DEFAULT_PASSWORD).render();
            resultPage.render();
            Assert.assertFalse(resultPage.isLoggedIn(), testUser2 + "is logged");

            JmxUtils.invokeAlfrescoServerProperty(node1Url, sysAdminObject, "revert");
            JmxUtils.invokeAlfrescoServerProperty(node1Url, sysAdminObject, "start");

            // Try login by user1
            resultPage = login(drone, testUser1, DEFAULT_PASSWORD).render();
            resultPage.render();
            Assert.assertTrue(resultPage.isLoggedIn(), testUser1 + "isn't logged");
            ShareUser.logout(drone);

            // Try login by user2
            resultPage = login(drone, testUser2, DEFAULT_PASSWORD);
            resultPage.render();
            Assert.assertTrue(resultPage.isLoggedIn(), testUser2 + "isn't logged");
            ShareUser.logout(drone);
        }
        finally
        {
            JmxUtils.setAlfrescoServerProperty(node1Url, sysAdminObject, allowedUsers, "");
            JmxUtils.invokeAlfrescoServerProperty(node1Url, sysAdminObject, "start");
            Assert.assertTrue(JmxUtils.getAlfrescoServerProperty(node1Url, sysAdminObject, allowedUsers).toString().equals(""), "Property " + allowedUsers
                    + " isn't cleaned");

        }

    }

    /**
     * Test - AONE-9288:Module "thirdparty->default", change Attributes
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>Monitoring and Management Console go to MBeans</li>
     * <li>open in thirdparty->default Attributes bookmark</li>
     * <li>Change any attribute</li>
     * <li>Click operation "Start"</li>
     * <li>Verify what component is reinitialized across the cluster</li>
     * <li>Content has transformed on server B</li>
     * <li>Content conversion failed</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 600000)
    public void AONE_9288() throws Exception
    {

        String thirdpartyObject = "Alfresco:Type=Configuration,Category=thirdparty,id1=default";
        String imgExe = "img.exe";

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName) + System.currentTimeMillis();
        String siteName1 = getSiteName(testName + "-1-") + System.currentTimeMillis();
        String folderName = getFolderName(testName) + System.currentTimeMillis();

        String FILE_PNG = "channel_test_png.png";
        String FILE_GIF = "channel_test_png.gif";
        String defaultImgExe = JmxUtils.getAlfrescoServerProperty(node1Url, thirdpartyObject, imgExe).toString();

        try
        {

            // Change any attribute
            JmxUtils.setAlfrescoServerProperty(node1Url, thirdpartyObject, imgExe, defaultImgExe + 1);
            JmxUtils.invokeAlfrescoServerProperty(node1Url, thirdpartyObject, "start");

            dronePropertiesMap.get(drone).setShareUrl(node2Url);

            // Create user
            String[] testUserInfo = new String[] { testUser };
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Any site is created
            ShareUser.login(drone, testUser);
            ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);

            String fileInfo[] = { FILE_PNG };

            ShareUser.uploadFileInFolder(drone, fileInfo);

            DocumentLibraryPage documentLibraryPage;

            documentLibraryPage = ShareUserSitePage.createFolder(drone, folderName, folderName);
            // Any folder is created, some items are added to folder
            // create folder with copy rule applied to it and files in the folder
            FolderRulesPage folderRulesPage = documentLibraryPage.getFileDirectoryInfo(folderName).selectManageRules().render();
            Assert.assertTrue(folderRulesPage.isPageCorrect(folderName), "Rule page isn't correct");

            CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
            createRulePage.fillNameField("New Copy and Transform Rule Name");
            createRulePage.fillDescriptionField("New Copy and Transform Rule Description");

            ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();

            // copy rule
            actionSelectorEnterpImpl.selectTransformAndCopy("GIF Image", siteName1, "Documents");

            FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
            Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

            ShareUser.openDocumentLibrary(drone);

            // Content has transformed on server B
            documentLibraryPage = ShareUserSitePage.copyOrMoveToFolder(drone, siteName1, FILE_PNG, new String[] { folderName }, true);

            ShareUser.openSitesDocumentLibrary(drone, siteName1);

            // Content conversion failed
            Assert.assertFalse(documentLibraryPage.isFileVisible(FILE_GIF), "Content conversion isn't failed");

            JmxUtils.setAlfrescoServerProperty(node1Url, thirdpartyObject, imgExe, defaultImgExe);
            JmxUtils.invokeAlfrescoServerProperty(node1Url, thirdpartyObject, "start");

            ShareUser.openDocumentLibrary(drone);

            // Content has transformed on server B
            documentLibraryPage = ShareUserSitePage.copyOrMoveToFolder(drone, siteName1, FILE_PNG, new String[] { folderName }, true);
            ShareUser.openSitesDocumentLibrary(drone, siteName1);

            // Content conversion passed
            Assert.assertTrue(documentLibraryPage.isFileVisible(FILE_GIF), "Content conversion is failed");

        }
        finally
        {
            JmxUtils.setAlfrescoServerProperty(node1Url, thirdpartyObject, imgExe, defaultImgExe);
            JmxUtils.invokeAlfrescoServerProperty(node1Url, thirdpartyObject, "start");

        }

    }

    /**
     * Test - AONE-9289:Module "thierdparty->default", change Operations
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>Monitoring and Management Console go to MBeans</li>
     * <li>open in thirdparty->default Attributes bookmark</li>
     * <li>Change any attribute</li>
     * <li>Click operation "Start"</li>
     * <li>Verify what component is reinitialized across the cluster</li>
     * <li>Content has transformed on server B</li>
     * <li>Content conversion failed</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 600000)
    public void AONE_9289() throws Exception
    {

        String thirdpartyObject = "Alfresco:Type=Configuration,Category=thirdparty,id1=default";
        String imgExe = "img.exe";

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName) + System.currentTimeMillis();
        String siteName1 = getSiteName(testName + "-1-") + System.currentTimeMillis();
        String folderName = getFolderName(testName) + System.currentTimeMillis();

        String FILE_PNG = "channel_test_png.png";
        String FILE_GIF = "channel_test_png.gif";
        String defaultImgExe = JmxUtils.getAlfrescoServerProperty(node1Url, thirdpartyObject, imgExe).toString();

        try
        {

            // Change any attribute
            JmxUtils.setAlfrescoServerProperty(node1Url, thirdpartyObject, imgExe, defaultImgExe + 1);
            JmxUtils.invokeAlfrescoServerProperty(node1Url, thirdpartyObject, "start");

            dronePropertiesMap.get(drone).setShareUrl(node2Url);

            // Create user
            String[] testUserInfo = new String[] { testUser };
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Any site is created
            ShareUser.login(drone, testUser);
            ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);

            String fileInfo[] = { FILE_PNG };

            ShareUser.uploadFileInFolder(drone, fileInfo);

            DocumentLibraryPage documentLibraryPage;

            documentLibraryPage = ShareUserSitePage.createFolder(drone, folderName, folderName);
            // Any folder is created, some items are added to folder
            // create folder with copy rule applied to it and files in the folder
            FolderRulesPage folderRulesPage = documentLibraryPage.getFileDirectoryInfo(folderName).selectManageRules().render();
            Assert.assertTrue(folderRulesPage.isPageCorrect(folderName), "Rule page isn't correct");

            CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
            createRulePage.fillNameField("New Copy and Transform Rule Name");
            createRulePage.fillDescriptionField("New Copy and Transform Rule Description");

            ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();

            // copy rule
            actionSelectorEnterpImpl.selectTransformAndCopy("GIF Image", siteName1, "Documents");

            FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
            Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

            ShareUser.openDocumentLibrary(drone);

            // Content has transformed on server B
            documentLibraryPage = ShareUserSitePage.copyOrMoveToFolder(drone, siteName1, FILE_PNG, new String[] { folderName }, true);

            ShareUser.openSitesDocumentLibrary(drone, siteName1);

            // Content conversion failed
            Assert.assertFalse(documentLibraryPage.isFileVisible(FILE_GIF), "Content conversion isn't failed");

            JmxUtils.invokeAlfrescoServerProperty(node1Url, thirdpartyObject, "revert");
            JmxUtils.invokeAlfrescoServerProperty(node1Url, thirdpartyObject, "start");

            ShareUser.openDocumentLibrary(drone);

            // Content has transformed on server B
            documentLibraryPage = ShareUserSitePage.copyOrMoveToFolder(drone, siteName1, FILE_PNG, new String[] { folderName }, true);
            ShareUser.openSitesDocumentLibrary(drone, siteName1);

            // Content conversion passed
            Assert.assertTrue(documentLibraryPage.isFileVisible(FILE_GIF), "Content conversion is failed");

        }
        finally
        {
            JmxUtils.setAlfrescoServerProperty(node1Url, thirdpartyObject, imgExe, defaultImgExe);
            JmxUtils.invokeAlfrescoServerProperty(node1Url, thirdpartyObject, "start");
        }

    }

}