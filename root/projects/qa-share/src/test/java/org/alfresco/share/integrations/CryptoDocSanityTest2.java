package org.alfresco.share.integrations;

import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.adminconsole.NodeBrowserPage;

import org.alfresco.po.share.systemsummary.AdminConsoleLink;
import org.alfresco.po.share.systemsummary.RepositoryServerClusteringPage;
import org.alfresco.po.share.systemsummary.SystemSummaryPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.CryptodocUtil;
import org.alfresco.share.util.JmxUtils;
import org.alfresco.share.util.NodeBrowserPageUtil;
import org.alfresco.share.util.RemoteUtil;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserRepositoryPage;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.SshCommandProcessor;
import org.alfresco.test.FailedTestListener;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.antlr.tool.ErrorManager.assertTrue;
import static org.testng.Assert.assertEquals;


/**
 * @author Maryia Zaichanka
 */


@Listeners(FailedTestListener.class)
public class CryptoDocSanityTest2 extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(CryptoDocSanityTest2.class);
    private static String node1Url;
    private static String node2Url;
    private static final String regexUrl = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(:\\d{1,5})?";

    private static SshCommandProcessor commandProcessor;

    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
    private static final Pattern DOMAIN_PATTERN = Pattern.compile("\\w+(\\.\\w+)*(\\.\\w{2,})");
    private static final String objectName = "Alfresco:Type=Configuration,Category=ContentStore,id1=managed,id2=encrypted";

    private static String masterKeyPath;
    private static String key;
    private static String keyPassword;
    private static String newKey;
    private static String newKeyPassword;
    private static String keyStorePassword;

    private static void initConnection()
    {
        commandProcessor = new SshCommandProcessor();
        commandProcessor.connect();
    }

    private void setSshHost(String sshHostUrl)
    {
        sshHost = getAddress(sshHostUrl);
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


    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();

        logger.info("Start Tests in: " + testName);

        SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
        RepositoryServerClusteringPage clusteringPage = sysSummaryPage.openConsolePage(AdminConsoleLink.RepositoryServerClustering).render();
        if (clusteringPage.isClusterEnabled())
        {
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
        else
        {
            node1Url = shareUrl;
            node2Url = null;
        }

        masterKeyPath = JmxUtils.getAlfrescoServerProperty(node1Url, objectName, "cryptodoc.jce.keystore.path").toString();
        if (masterKeyPath.equals(""))
        {
            throw new PageOperationException("Encryption isn't enabled");
        }
        else
        {
            Object result = JmxUtils.invokeAlfrescoOperation(node1Url, objectName, "showMasterKeys");
            TabularData tabularData=(TabularDataSupport)result;
            Collection<CompositeData> values=(Collection<CompositeData>)tabularData.values();
            int numberOfKeys = values.size();
            if (numberOfKeys>1)
            {

                newKey = JmxUtils.getAlfrescoServerProperty(node1Url, objectName, "cryptodoc.jce.key.aliases").toString();
                key = newKey.substring(newKey.lastIndexOf(",")).replace(",", "");
                newKey = newKey.replace("," + key, "");
                newKeyPassword = JmxUtils.getAlfrescoServerProperty(node1Url, objectName, "cryptodoc.jce.key.passwords").toString();
                keyPassword = newKeyPassword.substring(newKeyPassword.lastIndexOf(",")).replace(",", "");
                newKeyPassword = newKeyPassword.replace("," + keyPassword, "");
                CryptodocUtil.revokeKey(node1Url, newKey);
                CryptodocUtil.reEncryptKey(node1Url,newKey);
                CryptodocUtil.changeKeyProperties(node1Url, key, keyPassword);
            }

            if (numberOfKeys==1)

            {

                key = JmxUtils.getAlfrescoServerProperty(node1Url, objectName, "cryptodoc.jce.key.aliases").toString();
                keyPassword = JmxUtils.getAlfrescoServerProperty(node1Url, objectName, "cryptodoc.jce.key.passwords").toString();
                keyStorePassword = JmxUtils.getAlfrescoServerProperty(node1Url, objectName, "cryptodoc.jce.keystore.password").toString();
                setSshHost(node1Url);
                initConnection();
                newKey = getRandomString(4);
                newKeyPassword = getRandomString(6);
                CryptodocUtil.generateKeyStore(masterKeyPath, keyStorePassword, newKey, newKeyPassword);

            }
        }


    }

    @Test(groups = { "EnterpriseOnly", "Sanity"}, dependsOnMethods = "AONE_15989", alwaysRun = true, timeOut = 400000)
    public void AONE_15985() throws Exception
    {
        String testName = getTestName();

        String fileName1 = getFileName(testName) + System.currentTimeMillis() + "_1.txt";
        String fileName2 = getFileName(testName) + System.currentTimeMillis() + "_2.txt";
        String fileName3 = getFileName(testName) + System.currentTimeMillis() + "_3.txt";
        String fileName4 = getFileName(testName) + System.currentTimeMillis() + "_4.txt";
        String fileName5 = getFileName(testName) + System.currentTimeMillis() + "_5.txt";
        String fileName6 = getFileName(testName) + System.currentTimeMillis() + "_6.txt";
        String fileName7 = getFileName(testName) + System.currentTimeMillis() + "_7.txt";
        File file1 = newFile(fileName1, fileName1);
        File file2 = newFile(fileName2, fileName2);
        File file3 = newFile(fileName3, fileName3);
        File file4 = newFile(fileName3, fileName4);
        File file5 = newFile(fileName1, fileName5);
        File file6 = newFile(fileName2, fileName6);
        File file7 = newFile(fileName3, fileName7);
        File [] files = {file1, file2, file3};
        File [] files2 = {file5, file6, file7};

        // Perform a showMasterKeys operation
        Object result = JmxUtils.invokeAlfrescoOperation(node1Url, objectName, "showMasterKeys");

        TabularData tabularData=(TabularDataSupport)result;
        Collection<CompositeData> values=(Collection<CompositeData>)tabularData.values();
        int numberOfKeys = values.size();
        assertTrue(numberOfKeys > 1, "Less then 2 master key are present");

        CompositeData[] cdArray = new CompositeData[]{};
        CompositeData[] cdArrays = values.toArray(cdArray);

        long num = 0;
        long num2 =0;
        for ( int  i = 0; i < cdArrays.length; ++i )
        {
            CompositeData cd = cdArrays[i];
            String n = cd.get("Key Alias").toString();

            if (n.equals(newKey))
            {
                num = cd.get("Number of Symmetric Keys").hashCode();
            }
            else
            {
                num2 = cd.get("Number of Symmetric Keys").hashCode();
            }
        }

        // Perform the same operation for another cluster node if you are using a cluster
        if (node2Url!=null)
        {
            result = JmxUtils.invokeAlfrescoOperation(node2Url, objectName, "showMasterKeys");

            tabularData=(TabularDataSupport)result;
            values=(Collection<CompositeData>)tabularData.values();
            numberOfKeys = values.size();
            assertTrue(numberOfKeys > 1, "Less then 2 master key are present");

            cdArray = new CompositeData[]{};
            cdArrays = values.toArray(cdArray);

            long numNode = 0;
            long numNode2 = 0;
            for ( int  i = 0; i < cdArrays.length; ++i )
            {
                CompositeData cd = cdArrays[i];
                String n = cd.get("Key Alias").toString();

                if (n.equals(newKey))
                {
                    numNode = cd.get("Number of Symmetric Keys").hashCode();
                    assertEquals(num, numNode, "Symmetric keys are different on different nodes");
                }
                else
                {
                    numNode2 = cd.get("Number of Symmetric Keys").hashCode();
                    assertEquals(num2, numNode2, "Symmetric keys are different on different nodes");
                }
            }

        }

        // Open Share (on one of the cluster members if you are using a cluster)
        // Add any content item, e.g. any txt document to the Repository root location, and verify that it was encrypted
        dronePropertiesMap.get(drone).setShareUrl(node1Url);
        for (File file : files)
        {
            ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
            RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
            ShareUserRepositoryPage.uploadFileInRepository(drone, file);
            Assert.assertTrue(repositoryPage.isFileVisible(file.getName()), "file isn't uploaded");

            String nodeRef = repositoryPage.getFileDirectoryInfo(file.getName()).getContentNodeRef();

            nodeRef = nodeRef.substring(nodeRef.indexOf("workspace"));
            nodeRef = nodeRef.replaceFirst("/", "://");
            NodeBrowserPage nodeBrowserPage = NodeBrowserPageUtil.openNodeBrowserPage(drone);
            nodeBrowserPage = NodeBrowserPageUtil.executeQuery(drone, nodeRef, NodeBrowserPage.QueryType.NODE_REF, NodeBrowserPage.Store.WORKSPACE_SPACE_STORE)
                    .render();
            getCurrentPage(drone).render(maxWaitTime);
            assertTrue(nodeBrowserPage.isInResultsByName(file.getName()) && nodeBrowserPage.isInResultsByNodeRef(nodeRef),
                    "Nothing was found or there was found incorrect file by nodeRef");

            nodeBrowserPage.getItemDetails(file.getName());
            String binPath = nodeBrowserPage.getContentUrl();
            binPath = binPath.substring(binPath.indexOf("store"), binPath.lastIndexOf("bin"));
            binPath = binPath.replace("store://", "contentstore/");

            // On your server machine open your alf_data/contentstore directory
            // Open the a .bin file and verify its content
            setSshHost(node1Url);
            initConnection();
            String alfHome = JmxUtils.getAlfrescoServerProperty("Alfresco:Name=SystemProperties", "alfresco.home").toString();
            alfHome.replace("/", SLASH);
            String filepath = alfHome + "/alf_data/" + binPath + "bin";
            assertTrue(RemoteUtil.isFileExist(filepath), ".bin file isn't present at a contentstore");

            String resultFile = "results.txt";
            String resultsPath = alfHome + "/" + resultFile;
            RemoteUtil.checkForStrings(file.getName(), filepath, resultsPath);

            assertTrue(RemoteUtil.isFileEmpty(resultsPath), "File " + file.getName() + " wasn't encrypted");
        }
        dronePropertiesMap.get(drone).setShareUrl(node1Url);


        //  Repeat steps for another cluster member
        if (node2Url!=null)
        {
            dronePropertiesMap.get(drone).setShareUrl(node2Url);
            for (File file : files2)
            {
                ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
                RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
                ShareUserRepositoryPage.uploadFileInRepository(drone, file);
                Assert.assertTrue(repositoryPage.isFileVisible(file.getName()), "file isn't uploaded");

                String nodeRef = repositoryPage.getFileDirectoryInfo(file.getName()).getContentNodeRef();

                nodeRef = nodeRef.substring(nodeRef.indexOf("workspace"));
                nodeRef = nodeRef.replaceFirst("/", "://");
                NodeBrowserPage nodeBrowserPage = NodeBrowserPageUtil.openNodeBrowserPage(drone);
                nodeBrowserPage = NodeBrowserPageUtil.executeQuery(drone, nodeRef, NodeBrowserPage.QueryType.NODE_REF, NodeBrowserPage.Store.WORKSPACE_SPACE_STORE)
                        .render();
                getCurrentPage(drone).render(maxWaitTime);
                assertTrue(nodeBrowserPage.isInResultsByName(file.getName()) && nodeBrowserPage.isInResultsByNodeRef(nodeRef),
                        "Nothing was found or there was found incorrect file by nodeRef");

                nodeBrowserPage.getItemDetails(file.getName());
                String binPath = nodeBrowserPage.getContentUrl();
                binPath = binPath.substring(binPath.indexOf("store"), binPath.lastIndexOf("bin"));
                binPath = binPath.replace("store://", "contentstore/");
                // binPath = binPath.replace("/", SLASH);

                // On your server machine open your alf_data/contentstore directory
                // Open the a .bin file and verify its content
                setSshHost(node2Url);
                initConnection();
                String alfHome = JmxUtils.getAlfrescoServerProperty("Alfresco:Name=SystemProperties", "alfresco.home").toString();
                alfHome.replace("/", SLASH);
                String filepath = alfHome + "/alf_data/" + binPath + "bin";
                assertTrue(RemoteUtil.isFileExist(filepath), ".bin file isn't present at a contentstore");

                String resultFile = "results.txt";
                String resultsPath = alfHome + "/" + resultFile;
                RemoteUtil.checkForStrings(file.getName(), filepath, resultsPath);

                assertTrue(RemoteUtil.isFileEmpty(resultsPath), "File " + file.getName() + " wasn't encrypted");
            }
            dronePropertiesMap.get(drone).setShareUrl(node2Url);
        }

        // Perform a showMasterKey operation
        result = JmxUtils.invokeAlfrescoOperation(node1Url, objectName, "showMasterKeys");
        tabularData=(TabularDataSupport)result;
        values=(Collection<CompositeData>)tabularData.values();
        cdArray = new CompositeData[]{};
        cdArrays = values.toArray(cdArray);

        long newNum;
        long newNum2 = 0;
        for ( int  i = 0; i < cdArrays.length; ++i )
        {
            CompositeData cd = cdArrays[i];
            String n = cd.get("Key Alias").toString();

            if (n.equals(newKey))
            {
                newNum = cd.get("Number of Symmetric Keys").hashCode();
                assertTrue(num < newNum, "Number of symmetric keys isn't increased");
            }
            else
            {
                newNum2 = cd.get("Number of Symmetric Keys").hashCode();
                assertTrue(num2 < newNum2, "Number of symmetric keys isn't increased");
            }
        }


        // Perform a showMasterKey operation on another cluster node if you are using a cluster
        if (node2Url!=null)
        {
            result = JmxUtils.invokeAlfrescoOperation(node2Url, objectName, "showMasterKeys");
            tabularData=(TabularDataSupport)result;
            values=(Collection<CompositeData>)tabularData.values();
            cdArray = new CompositeData[]{};
            cdArrays = values.toArray(cdArray);

            long newNumNode = 0;
            long newNumNode2 = 0;

            for ( int  i = 0; i < cdArrays.length; ++i )
            {
                CompositeData cd = cdArrays[i];
                String n = cd.get("Key Alias").toString();

                if (n.equals(newKey))
                {
                    newNumNode = cd.get("Number of Symmetric Keys").hashCode();
                    assertTrue(num < newNumNode, "Number of symmetric keys isn't increased");
                }
                else
                {
                    newNumNode2 = cd.get("Number of Symmetric Keys").hashCode();
                    assertTrue(num2 < newNumNode2, "Number of symmetric keys isn't increased");
                }
            }
        }

        // Revoke a master key (revokeMasterKey operation) against key2 master key - specify key2 value in a string field and execute operation
        CryptodocUtil.revokeKey(node1Url, newKey);

        // Perform a showMasterKey operation
        result = JmxUtils.invokeAlfrescoOperation(node1Url, objectName, "showMasterKeys");
        tabularData=(TabularDataSupport)result;
        values=(Collection<CompositeData>)tabularData.values();
        numberOfKeys = values.size();
        assertTrue(numberOfKeys > 1, "Only one master key is present");
        cdArray = new CompositeData[]{};
        cdArrays = values.toArray(cdArray);

        for ( int  i = 0; i < cdArrays.length; ++i )
        {
            CompositeData cd = cdArrays[i];
            String n = cd.get("Key Alias").toString();

            if (n.equals(newKey))
            {
                assertEquals(cd.get("Can Encrypt"), false, "Key isn't revoked");
                assertEquals(cd.get("Encryption Key Algorithm"), null, "Key isn't revoked");
                num = cd.get("Number of Symmetric Keys").hashCode();
            }
            else
            {
                num2 = cd.get("Number of Symmetric Keys").hashCode();
            }
        }

        // Perform the same operation on another cluster node if you are using a cluster
        if (node2Url!=null)
        {
            result = JmxUtils.invokeAlfrescoOperation(node1Url, objectName, "showMasterKeys");
            tabularData=(TabularDataSupport)result;
            values=(Collection<CompositeData>)tabularData.values();
            numberOfKeys = values.size();
            assertTrue(numberOfKeys > 1, "Only one master key is present");
            cdArray = new CompositeData[]{};
            cdArrays = values.toArray(cdArray);

            for ( int  i = 0; i < cdArrays.length; ++i )
            {
                CompositeData cd = cdArrays[i];
                String n = cd.get("Key Alias").toString();

                if (n.equals(newKey))
                {
                    assertEquals(cd.get("Can Encrypt"), false, "Key isn't revoked");
                    assertEquals(cd.get("Encryption Key Algorithm"), null, "Key isn't revoked");
                }
            }
        }


        // Verify the previously created document on both cluster nodes if you are using a cluster
        dronePropertiesMap.get(drone).setShareUrl(node1Url);
        for (File file : files)
        {
            ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
            RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone).render();

            String nodeRef = repositoryPage.getFileDirectoryInfo(file.getName()).getContentNodeRef();

            nodeRef = nodeRef.substring(nodeRef.indexOf("workspace"));
            nodeRef = nodeRef.replaceFirst("/", "://");
            NodeBrowserPage nodeBrowserPage = NodeBrowserPageUtil.openNodeBrowserPage(drone);
            nodeBrowserPage = NodeBrowserPageUtil.executeQuery(drone, nodeRef, NodeBrowserPage.QueryType.NODE_REF, NodeBrowserPage.Store.WORKSPACE_SPACE_STORE)
                    .render();
            getCurrentPage(drone).render(maxWaitTime);
            assertTrue(nodeBrowserPage.isInResultsByName(file.getName()) && nodeBrowserPage.isInResultsByNodeRef(nodeRef),
                    "Nothing was found or there was found incorrect file by nodeRef");

            nodeBrowserPage.getItemDetails(file.getName());
            String binPath = nodeBrowserPage.getContentUrl();
            binPath = binPath.substring(binPath.indexOf("store"), binPath.lastIndexOf("bin"));
            binPath = binPath.replace("store://", "contentstore/");
            // binPath = binPath.replace("/", SLASH);

            // On your server machine open your alf_data/contentstore directory
            // Open the a .bin file and verify its content
            setSshHost(node1Url);
            initConnection();
            String alfHome = JmxUtils.getAlfrescoServerProperty("Alfresco:Name=SystemProperties", "alfresco.home").toString();
            String filepath = alfHome + "/alf_data/" + binPath + "bin";
            assertTrue(RemoteUtil.isFileExist(filepath), ".bin file isn't present at a contentstore");

            String resultFile = "results.txt";
            String resultsPath = alfHome + "/" + resultFile;
            RemoteUtil.checkForStrings(file.getName(), filepath, resultsPath);

            assertTrue(RemoteUtil.isFileEmpty(resultsPath), "File " + file.getName() + " wasn't encrypted");
        }
        ShareUser.logout(drone);

        if (node2Url!=null)
        {
            dronePropertiesMap.get(drone).setShareUrl(node2Url);
            for (File file : files)
            {
                ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
                RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone).render();

                String nodeRef = repositoryPage.getFileDirectoryInfo(file.getName()).getContentNodeRef();

                nodeRef = nodeRef.substring(nodeRef.indexOf("workspace"));
                nodeRef = nodeRef.replaceFirst("/", "://");
                NodeBrowserPage nodeBrowserPage = NodeBrowserPageUtil.openNodeBrowserPage(drone);
                nodeBrowserPage = NodeBrowserPageUtil.executeQuery(drone, nodeRef, NodeBrowserPage.QueryType.NODE_REF, NodeBrowserPage.Store.WORKSPACE_SPACE_STORE)
                        .render();
                getCurrentPage(drone).render(maxWaitTime);
                assertTrue(nodeBrowserPage.isInResultsByName(file.getName()) && nodeBrowserPage.isInResultsByNodeRef(nodeRef),
                        "Nothing was found or there was found incorrect file by nodeRef");

                nodeBrowserPage.getItemDetails(file.getName());
                String binPath = nodeBrowserPage.getContentUrl();
                binPath = binPath.substring(binPath.indexOf("store"), binPath.lastIndexOf("bin"));
                binPath = binPath.replace("store://", "contentstore/");
                // binPath = binPath.replace("/", SLASH);

                // On your server machine open your alf_data/contentstore directory
                // Open the a .bin file and verify its content
                setSshHost(node2Url);
                initConnection();
                String alfHome = JmxUtils.getAlfrescoServerProperty("Alfresco:Name=SystemProperties", "alfresco.home").toString();
                String filepath = alfHome + "/alf_data/" + binPath + "bin";
                assertTrue(RemoteUtil.isFileExist(filepath), ".bin file isn't present at a contentstore");

                String resultFile = "results.txt";
                String resultsPath = alfHome + "/" + resultFile;
                RemoteUtil.checkForStrings(file.getName(), filepath, resultsPath);

                assertTrue(RemoteUtil.isFileEmpty(resultsPath), "File " + file.getName() + " wasn't encrypted");
            }
            ShareUser.logout(drone);

        }

        // Create any new document on any cluster node
        dronePropertiesMap.get(drone).setShareUrl(node1Url);
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        ShareUserRepositoryPage.uploadFileInRepository(drone, file4);
        Assert.assertTrue(repositoryPage.isFileVisible(file4.getName()), "File isn't uploaded");

        // Perform a showMasterKey operation
        result = JmxUtils.invokeAlfrescoOperation(node1Url, objectName, "showMasterKeys");
        tabularData=(TabularDataSupport)result;
        values=(Collection<CompositeData>)tabularData.values();
        numberOfKeys = values.size();
        assertTrue(numberOfKeys > 1, "Only one master key is present");
        cdArray = new CompositeData[]{};
        cdArrays = values.toArray(cdArray);


        for ( int  i = 0; i < cdArrays.length; ++i )
        {
            CompositeData cd = cdArrays[i];
            String n = cd.get("Key Alias").toString();

            if (n.equals(newKey))
            {
                newNum = cd.get("Number of Symmetric Keys").hashCode();
                assertEquals(num, newNum, "Number of symmetric keys increased for revoked key");
            }
            else
            {
                newNum2 = cd.get("Number of Symmetric Keys").hashCode();
                assertTrue(newNum2>num2, "Number of symmetric keys isn't increased");
            }
        }

        // Perform the same operation on another cluster node if you are using a cluster
        if (node2Url!=null)
        {
            result = JmxUtils.invokeAlfrescoOperation(node2Url, objectName, "showMasterKeys");
            tabularData=(TabularDataSupport)result;
            values=(Collection<CompositeData>)tabularData.values();
            numberOfKeys = values.size();
            assertTrue(numberOfKeys > 1, "Only one master key is present");
            cdArray = new CompositeData[]{};
            cdArrays = values.toArray(cdArray);

            long newNumNode;
            long newNumNode2;
            for ( int  i = 0; i < cdArrays.length; ++i )
            {
                CompositeData cd = cdArrays[i];
                String n = cd.get("Key Alias").toString();

                if (n.equals(newKey))
                {
                    newNumNode = cd.get("Number of Symmetric Keys").hashCode();
                    assertEquals(num, newNumNode, "Number of symmetric keys increased for revoked key");
                }
                else
                {
                    newNumNode2 = cd.get("Number of Symmetric Keys").hashCode();
                    assertTrue(newNumNode2>num2, "Number of symmetric keys isn't increased");
                }
            }
        }

        // Re-encrypt symmetric keys for that master key
        CryptodocUtil.reEncryptKey(node1Url, newKey);

        // Perform a showMasterKeys operation
        result = JmxUtils.invokeAlfrescoOperation(node1Url, objectName, "showMasterKeys");
        tabularData=(TabularDataSupport)result;
        values=(Collection<CompositeData>)tabularData.values();
        numberOfKeys = values.size();
        assertTrue(numberOfKeys > 1, "Only one master key is present");
        cdArray = new CompositeData[]{};
        cdArrays = values.toArray(cdArray);

        for ( int  i = 0; i < cdArrays.length; ++i )
        {
            CompositeData cd = cdArrays[i];
            String n = cd.get("Key Alias").toString();

            if (n.equals(newKey))
            {
                assertEquals(cd.get("Number of Symmetric Keys").hashCode(), 0, "Symmetric keys of key2 master key weren't moved to key1");
            }
            else
            {
                num2 = cd.get("Number of Symmetric Keys").hashCode();
                assertTrue(newNum2<num2, "Symmetric keys of key2 master key weren't moved to key1");
            }
        }

        // Perform the same operation on another cluster node if you are using a cluster
        if (node2Url!=null)
        {
            result = JmxUtils.invokeAlfrescoOperation(node2Url, objectName, "showMasterKeys");
            tabularData=(TabularDataSupport)result;
            values=(Collection<CompositeData>)tabularData.values();
            numberOfKeys = values.size();
            assertTrue(numberOfKeys > 1, "Only one master key is present");
            cdArray = new CompositeData[]{};
            cdArrays = values.toArray(cdArray);
            for ( int  i = 0; i < cdArrays.length; ++i )
            {
                CompositeData cd = cdArrays[i];
                String n = cd.get("Key Alias").toString();

                if (n.equals(newKey))
                {
                    assertEquals(cd.get("Number of Symmetric Keys").hashCode(), 0, "Symmetric keys of key2 master key weren't moved to key1");
                }
                else
                {
                    long numNode2 = cd.get("Number of Symmetric Keys").hashCode();
                    assertTrue(newNum2<numNode2, "Symmetric keys of key2 master key weren't moved to key1");
                }
            }
        }

    }

    @Test(groups = { "EnterpriseOnly", "Sanity"}, dependsOnMethods = "AONE_15985", alwaysRun = true, timeOut = 400000)
    public void AONE_15986() throws Exception
    {
        String testName = getTestName();
        String fileName1 = getFileName(testName) + System.currentTimeMillis() + "_1.txt";
        String fileName2 = getFileName(testName) + System.currentTimeMillis() + "_2.txt";
        String fileName3 = getFileName(testName) + System.currentTimeMillis() + "_3.txt";
        File file1 = newFile(fileName1, fileName1);
        File file2 = newFile(fileName2, fileName2);
        File file3 = newFile(fileName3, fileName3);

        File [] files = {file1, file2, file3};

        String fileName4 = getFileName(testName) + System.currentTimeMillis() + "_4.txt";
        String fileName5 = getFileName(testName) + System.currentTimeMillis() + "_5.txt";
        String fileName6 = getFileName(testName) + System.currentTimeMillis() + "_6.txt";
        File file4 = newFile(fileName1, fileName4);
        File file5 = newFile(fileName2, fileName5);
        File file6 = newFile(fileName3, fileName6);

        File [] files2 = {file4, file5, file6};


        // Cancel revocation of the master key key2 (cancelRevocation operation) - specify key2 value in the string field and execute operation
        CryptodocUtil.cancelRevocation(node1Url, newKey);

        // Perform a showMasterKey operation
        Object result = JmxUtils.invokeAlfrescoOperation(node1Url, objectName, "showMasterKeys");
        TabularData tabularData=(TabularDataSupport)result;
        Collection<CompositeData>values=(Collection<CompositeData>)tabularData.values();
        long numberOfKeys = values.size();
        assertTrue(numberOfKeys > 1, "Only one master key is present");
        CompositeData [] cdArray = new CompositeData[]{};
        CompositeData [] cdArrays = values.toArray(cdArray);


        long num = 0;
        long num2 = 0;
        for ( int  i = 0; i < cdArrays.length; ++i )
        {
            CompositeData cd = cdArrays[i];
            String n = cd.get("Key Alias").toString();

            if (n.equals(newKey))
            {
                assertEquals(cd.get("Can Encrypt"), true, "Revocation isn't canceled");
                assertEquals(cd.get("Encryption Key Algorithm"), "RSA", "Revocation isn't canceled");
                num2 = cd.get("Number of Symmetric Keys").hashCode();
            }
            else
            {
                num = cd.get("Number of Symmetric Keys").hashCode();
            }
        }

        // Perform the same operation on another cluster node if you are using a cluster
        if (node2Url!=null)
        {
            result = JmxUtils.invokeAlfrescoOperation(node1Url, objectName, "showMasterKeys");
            tabularData=(TabularDataSupport)result;
            values=(Collection<CompositeData>)tabularData.values();
            numberOfKeys = values.size();
            assertTrue(numberOfKeys > 1, "Only one master key is present");
            cdArray = new CompositeData[]{};
            cdArrays = values.toArray(cdArray);

            for ( int  i = 0; i < cdArrays.length; ++i )
            {
                CompositeData cd = cdArrays[i];
                String n = cd.get("Key Alias").toString();

                if (n.equals(newKey))
                {
                    assertEquals(cd.get("Can Encrypt"), true, "Revocation isn't canceled");
                    assertEquals(cd.get("Encryption Key Algorithm"), "RSA", "Revocation isn't canceled");
                }
            }

        }

        // Add any content item, e.g. any txt document to the Repository root location
        dronePropertiesMap.get(drone).setShareUrl(node1Url);
        for (File file : files)
        {
            ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
            RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
            ShareUserRepositoryPage.uploadFileInRepository(drone, file);
            Assert.assertTrue(repositoryPage.isFileVisible(file.getName()), "File isn't uploaded");
        }
        ShareUser.logout(drone);

        // Repeat steps for cluster node
        if (node2Url!=null)
        {
            dronePropertiesMap.get(drone).setShareUrl(node2Url);
            for (File file : files2)
            {
                ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
                RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
                ShareUserRepositoryPage.uploadFileInRepository(drone, file);
                Assert.assertTrue(repositoryPage.isFileVisible(file.getName()), "File isn't uploaded");
            }
            ShareUser.logout(drone);
        }

        // Perform a showMasterKey operation
        result = JmxUtils.invokeAlfrescoOperation(node1Url, objectName, "showMasterKeys");
        tabularData=(TabularDataSupport)result;
        values=(Collection<CompositeData>)tabularData.values();
        numberOfKeys = values.size();
        assertTrue(numberOfKeys > 1, "Only one master key is present");
        cdArray = new CompositeData[]{};
        cdArrays = values.toArray(cdArray);

        long newNum2 = 0;
        long newNum = 0;
        for ( int  i = 0; i < cdArrays.length; ++i )
        {
            CompositeData cd = cdArrays[i];
            String n = cd.get("Key Alias").toString();

            if (n.equals(newKey))
            {
                newNum2 = cd.get("Number of Symmetric Keys").hashCode();
                assertTrue(newNum2 > num2, "Number of symmetric keys isn't increased");
            }
            else
            {
                newNum = cd.get("Number of Symmetric Keys").hashCode();
                assertTrue(newNum>num, "Number of symmetric keys isn't increased");
            }
        }

        // Perform the same operation on another cluster node if you are using a cluster
        if (node2Url!=null)
        {
            result = JmxUtils.invokeAlfrescoOperation(node2Url, objectName, "showMasterKeys");
            tabularData=(TabularDataSupport)result;
            values=(Collection<CompositeData>)tabularData.values();
            numberOfKeys = values.size();
            assertTrue(numberOfKeys > 1, "Only one master key is present");
            cdArray = new CompositeData[]{};
            cdArrays = values.toArray(cdArray);

            //        long num2 =0;
            for ( int  i = 0; i < cdArrays.length; ++i )
            {
                CompositeData cd = cdArrays[i];
                String n = cd.get("Key Alias").toString();

                if (n.equals(newKey))
                {
                    long newNumNode2 = cd.get("Number of Symmetric Keys").hashCode();
                    assertTrue(newNum2 > newNumNode2, "Number of symmetric keys isn't increased");
                }
                else
                {
                    long newNumNode = cd.get("Number of Symmetric Keys").hashCode();
                    assertTrue(newNum>newNumNode, "Number of symmetric keys isn't increased");
                }
            }
        }
    }

    @Test(groups = { "EnterpriseOnly", "Sanity"}, dependsOnMethods = "AONE_15986", alwaysRun = true, timeOut = 400000)
    public void AONE_15987() throws Exception
    {
        String testName = getTestName();
        String fileName1 = getFileName(testName) + System.currentTimeMillis() + "_1.txt";
        File file1 = newFile(fileName1, fileName1);



        // Revoke a master key (revokeMasterKey operation) against key2 master key - specify key2 value in a string field and execute operation
        CryptodocUtil.revokeKey(node1Url, newKey);

        // Perform a showMasterKeys operation
        Object result = JmxUtils.invokeAlfrescoOperation(node1Url, objectName, "showMasterKeys");
        TabularData tabularData=(TabularDataSupport)result;
        Collection <CompositeData> values=(Collection<CompositeData>)tabularData.values();
        long numberOfKeys = values.size();
        assertTrue(numberOfKeys > 1, "Only one master key is present");
        CompositeData [] cdArray = new CompositeData[]{};
        CompositeData [] cdArrays = values.toArray(cdArray);

        long num = 0;
//        long num2 = 0;
        for ( int  i = 0; i < cdArrays.length; ++i )
        {
            CompositeData cd = cdArrays[i];
            String n = cd.get("Key Alias").toString();

            if (n.equals(newKey))
            {
                assertEquals(cd.get("Can Encrypt"), false, "Key isn't revoked");
                assertEquals(cd.get("Encryption Key Algorithm"), null, "Key isn't revoked");
            }
            else
            {
                num = cd.get("Number of Symmetric Keys").hashCode();
            }
        }

        // Perform the same operation for another cluster node if you are using a cluster
        long numNode = 0;
        if (node2Url!=null)
        {
            result = JmxUtils.invokeAlfrescoOperation(node2Url, objectName, "showMasterKeys");
            tabularData=(TabularDataSupport)result;
            values=(Collection<CompositeData>)tabularData.values();
            numberOfKeys = values.size();
            assertTrue(numberOfKeys > 1, "Only one master key is present");
            cdArray = new CompositeData[]{};
            cdArrays = values.toArray(cdArray);

            for ( int  i = 0; i < cdArrays.length; ++i )
            {
                CompositeData cd = cdArrays[i];
                String n = cd.get("Key Alias").toString();

                if (n.equals(newKey))
                {
                    assertEquals(cd.get("Can Encrypt"), false, "Key isn't revoked");
                    assertEquals(cd.get("Encryption Key Algorithm"), null, "Key isn't revoked");
                }
                else
                {
                    numNode = cd.get("Number of Symmetric Keys").hashCode();
                }
            }

        }

        // Re-encrypt symmetric keys for that master key
        CryptodocUtil.reEncryptKey(node1Url, newKey);

        // Perform a showMasterKeys operation
        result = JmxUtils.invokeAlfrescoOperation(node1Url, objectName, "showMasterKeys");
        tabularData=(TabularDataSupport)result;
        values=(Collection<CompositeData>)tabularData.values();
        numberOfKeys = values.size();
        assertTrue(numberOfKeys > 1, "Only one master key is present");
        cdArray = new CompositeData[]{};
        cdArrays = values.toArray(cdArray);

        for ( int  i = 0; i < cdArrays.length; ++i )
        {
            CompositeData cd = cdArrays[i];
            String n = cd.get("Key Alias").toString();

            if (n.equals(newKey))
            {
                assertEquals(cd.get("Number of Symmetric Keys").hashCode(), 0, "Symmetric keys of key2 master key weren't moved to key1");
            }
        }

        // Perform the same operation for another cluster node if you are using a cluster
        if (node2Url!=null)
        {
            result = JmxUtils.invokeAlfrescoOperation(node2Url, objectName, "showMasterKeys");
            tabularData=(TabularDataSupport)result;
            values=(Collection<CompositeData>)tabularData.values();
            numberOfKeys = values.size();
            assertTrue(numberOfKeys > 1, "Only one master key is present");
            cdArray = new CompositeData[]{};
            cdArrays = values.toArray(cdArray);

            for ( int  i = 0; i < cdArrays.length; ++i )
            {
                CompositeData cd = cdArrays[i];
                String n = cd.get("Key Alias").toString();

                if (n.equals(newKey))
                {
                    assertEquals(cd.get("Number of Symmetric Keys").hashCode(), 0, "Symmetric keys of key2 master key weren't moved to key1");
                }
            }

        }

        // Remove master key key2 - remove key2 value from the cryptodoc.jce.key.aliases property and its password from the cryptodoc.jce.key.passwords field and restart the subsystem
        CryptodocUtil.changeKeyProperties(node1Url, key, keyPassword);

        // Perform a showMasterKeys operation
        result = JmxUtils.invokeAlfrescoOperation(node1Url, objectName, "showMasterKeys");
        tabularData=(TabularDataSupport)result;
        values=(Collection<CompositeData>)tabularData.values();
        numberOfKeys = values.size();
        assertEquals(numberOfKeys, 1, "Key isn't removed");

        // Perform the same operation for another cluster node if you are using a cluster
        if (node2Url!=null)
        {
            result = JmxUtils.invokeAlfrescoOperation(node2Url, objectName, "showMasterKeys");
            tabularData=(TabularDataSupport)result;
            values=(Collection<CompositeData>)tabularData.values();
            numberOfKeys = values.size();
            assertEquals(numberOfKeys, 1, "Key isn't removed");
        }

        // Add any content item, e.g. any txt document to the Repository root location
        dronePropertiesMap.get(drone).setShareUrl(node1Url);
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        ShareUserRepositoryPage.uploadFileInRepository(drone, file1);
        Assert.assertTrue(repositoryPage.isFileVisible(file1.getName()), "File isn't uploaded");

        // Perform a showMasterKey operation
        result = JmxUtils.invokeAlfrescoOperation(node1Url, objectName, "showMasterKeys");
        tabularData=(TabularDataSupport)result;
        values=(Collection<CompositeData>)tabularData.values();
        cdArray = new CompositeData[]{};
        cdArrays = values.toArray(cdArray);

        for ( int  i = 0; i < cdArrays.length; ++i )
        {
            CompositeData cd = cdArrays[i];
            String n = cd.get("Key Alias").toString();

            if (n.equals(newKey))
            {
                long newNum = cd.get("Number of Symmetric Keys").hashCode();
                assertTrue(newNum>num, "Number of symmetric keys isn't increased");
            }
        }

        // Perform the same operation on another cluster node if you are using a cluster
        if (node2Url!=null)
        {
            result = JmxUtils.invokeAlfrescoOperation(node2Url, objectName, "showMasterKeys");
            tabularData=(TabularDataSupport)result;
            values=(Collection<CompositeData>)tabularData.values();
            cdArray = new CompositeData[]{};
            cdArrays = values.toArray(cdArray);

            for ( int  i = 0; i < cdArrays.length; ++i )
            {
                CompositeData cd = cdArrays[i];
                String n = cd.get("Key Alias").toString();

                if (n.equals(newKey))
                {
                    long newNumNode = cd.get("Number of Symmetric Keys").hashCode();
                    assertTrue(newNumNode>numNode, "Number of symmetric keys isn't increased");
                }
            }
        }
    }

    @Test(groups = { "EnterpriseOnly", "Sanity"}, timeOut = 400000)
    public void AONE_15989() throws Exception
    {
        String testName = getTestName();
        File sampleFile = SiteUtil.prepareFile();
        String fileName1 = getFileName(testName) + System.currentTimeMillis() + "_1.txt";
        String fileName2 = getFileName(testName) + System.currentTimeMillis() + "_2.txt";
        String fileName3 = getFileName(testName) + System.currentTimeMillis() + "_3.txt";
        String fileName4 = getFileName(testName) + System.currentTimeMillis() + "_4.txt";
        String fileName5 = getFileName(testName) + System.currentTimeMillis() + "_5.txt";
        String fileName6 = getFileName(testName) + System.currentTimeMillis() + "_6.txt";
        File file1 = newFile(fileName1, fileName1);
        File file2 = newFile(fileName2, fileName2);
        File file3 = newFile(fileName3, fileName3);
        File file4 = newFile(fileName1, fileName4);
        File file5 = newFile(fileName2, fileName5);
        File file6 = newFile(fileName3, fileName6);
        File [] files = {file1, file2, file3};
        File [] files2 = {file4, file5, file6};




        // Perform a showMasterKeys operation
        Object result = JmxUtils.invokeAlfrescoOperation(node1Url, objectName, "showMasterKeys");

        TabularData tabularData=(TabularDataSupport)result;
        Collection<CompositeData> values=(Collection<CompositeData>)tabularData.values();
        int numberOfKeys = values.size();
        assertEquals(numberOfKeys, 1, "Not only one master key present");

        // Perform the same operation for another cluster node if you are using a cluster
        if (node2Url!=null)
        {
            result = JmxUtils.invokeAlfrescoOperation(node2Url, objectName, "showMasterKeys");

            tabularData=(TabularDataSupport)result;
            values=(Collection<CompositeData>)tabularData.values();
            numberOfKeys = values.size();
            assertEquals(numberOfKeys, 1, "Not only one master key present");
        }

        // Add a new value for the cryptodoc.jce.key.aliases property, i.e. another key alias, e.g. key2, separated by a comma:
        // Add a new value for the cryptodoc.jce.key.passwords property, i.e. another key password, e.g. mykey2pass, separated by a comma:
        // Stop and restart the encrypted subsystem (JMX operations)
        CryptodocUtil.changeKeyProperties(node1Url, key + "," + newKey,  keyPassword + "," + newKeyPassword);

        // Perform a showMasterKeys operation
        result = JmxUtils.invokeAlfrescoOperation(node1Url, objectName, "showMasterKeys");
        tabularData=(TabularDataSupport)result;
        values=(Collection<CompositeData>)tabularData.values();

        numberOfKeys = values.size();
        assertTrue(numberOfKeys > 1, "New key isn't added");
        CompositeData[] cdArray = new CompositeData[]{};
        CompositeData[] cdArrays = values.toArray(cdArray);



        long num=0;
        for ( int  i = 0; i < cdArrays.length; ++i )
        {
            CompositeData cd = cdArrays[i];
            assertEquals(cd.get("Encryption Key Algorithm"), "RSA", "Encryption Key Algorithm isn't present");
            assertEquals(cd.get("Decryption Key Algorithm"), "RSA", "Decryption Key Algorithm isn't present");
            assertEquals(cd.get("Can Encrypt"), true, "No encrypt ability is available");
            assertEquals(cd.get("Can Decrypt"), true, "No encrypt ability is available");
            String n = cd.get("Key Alias").toString();

            if (n.equals(newKey))
            {
                assertEquals(cd.get("Number of Symmetric Keys").hashCode(), 0, "Number of symmetric keys isn;t correct");
            }
            else
            {
                num = cd.get("Number of Symmetric Keys").hashCode();
            }

        }

//        Perform the same operation for another cluster node if you are using a cluster
        long numNode = 0;
        if (node2Url!=null)
        {
            result = JmxUtils.invokeAlfrescoOperation(node1Url, objectName, "showMasterKeys");
            tabularData=(TabularDataSupport)result;
            values=(Collection<CompositeData>)tabularData.values();
            numberOfKeys = values.size();
            assertTrue(numberOfKeys > 1, "New key isn't added");
            cdArray = new CompositeData[]{};
            cdArrays = values.toArray(cdArray);

            for ( int  i = 0; i < cdArrays.length; ++i )
            {
                CompositeData cd = cdArrays[i];
                assertEquals(cd.get("Encryption Key Algorithm"), "RSA", "Encryption Key Algorithm isn't present");
                assertEquals(cd.get("Decryption Key Algorithm"), "RSA", "Decryption Key Algorithm isn't present");
                assertEquals(cd.get("Can Encrypt"), true, "No encrypt ability is available");
                assertEquals(cd.get("Can Decrypt"), true, "No encrypt ability is available");
                String n = cd.get("Key Alias").toString();
                if (n.equals(newKey))
                {
                    assertEquals(cd.get("Number of Symmetric Keys").hashCode(), 0, "Number of symmetric keys isn;t correct");
                }
                else
                {
                    numNode = cd.get("Number of Symmetric Keys").hashCode();
                }
            }
        }


         // Open Share (on one of the cluster members if you are using a cluster)
         // Add any content item, e.g. any txt document to the Repository root location, and verify that it was encrypted
        for (File file : files) {
            dronePropertiesMap.get(drone).setShareUrl(node1Url);
            ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
            ShareUserRepositoryPage.openRepositorySimpleView(drone);
            RepositoryPage repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, file);
            Assert.assertTrue(repositoryPage.isFileVisible(file.getName()), "File isn't uploaded");

            String nodeRef = repositoryPage.getFileDirectoryInfo(file.getName()).getContentNodeRef();

            // On your server machine open your alf_data/contentstore directory
            // Open the a .bin file and verify its content
            String binPath = CryptodocUtil.getBinPath(drone, file.getName(), nodeRef);

            setSshHost(node1Url);
            initConnection();
            String alfHome = JmxUtils.getAlfrescoServerProperty("Alfresco:Name=SystemProperties", "alfresco.home").toString();
            String filepath = alfHome + "/alf_data/" + binPath + "bin";
            assertTrue(RemoteUtil.isFileExist(filepath), ".bin file isn't present at a contentstore");

            String resultFile = "results.txt";
            String resultsPath = alfHome + "/" + resultFile;
            RemoteUtil.checkForStrings(sampleFile.getName(), filepath, resultsPath);

            assertTrue(RemoteUtil.isFileEmpty(resultsPath), "File " + file.getName() + " wasn't encrypted");
        }

        //  Repeat steps for another cluster member
        if (node2Url!=null)
        {
            for (File file : files2) {
                dronePropertiesMap.get(drone).setShareUrl(node2Url);
                ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
                ShareUserRepositoryPage.openRepositorySimpleView(drone);
                RepositoryPage repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, file);
                Assert.assertTrue(repositoryPage.isFileVisible(file.getName()), "File isn't uploaded");

                String nodeRef = repositoryPage.getFileDirectoryInfo(file.getName()).getContentNodeRef();

                // On your server machine open your alf_data/contentstore directory
                // Open the a .bin file and verify its content
                String binPath = CryptodocUtil.getBinPath(drone, file.getName(), nodeRef);

                setSshHost(node2Url);
                initConnection();
                String alfHome = JmxUtils.getAlfrescoServerProperty("Alfresco:Name=SystemProperties", "alfresco.home").toString();
                String filepath = alfHome + "/alf_data/" + binPath + "bin";
                assertTrue(RemoteUtil.isFileExist(filepath), ".bin file isn't present at a contentstore");

                String resultFile = "results.txt";
                String resultsPath = alfHome + "/" + resultFile;
                RemoteUtil.checkForStrings(sampleFile.getName(), filepath, resultsPath);

                assertTrue(RemoteUtil.isFileEmpty(resultsPath), "File " + file.getName() + " wasn't encrypted");
            }
            ShareUser.logout(drone);
        }

        // Perform a showMasterKey operation
        result = JmxUtils.invokeAlfrescoOperation(shareUrl, objectName, "showMasterKeys");
        tabularData=(TabularDataSupport)result;
        values=(Collection<CompositeData>)tabularData.values();
        cdArray = new CompositeData[]{};
        cdArrays = values.toArray(cdArray);

        long num2;
        for ( int  i = 0; i < cdArrays.length; ++i )
        {
            CompositeData cd = cdArrays[i];
            assertEquals(cd.get("Encryption Key Algorithm"), "RSA", "Encryption Key Algorithm isn't present");
            assertEquals(cd.get("Decryption Key Algorithm"), "RSA", "Decryption Key Algorithm isn't present");
            assertEquals(cd.get("Can Encrypt"), true, "No encrypt ability is available");
            assertEquals(cd.get("Can Decrypt"), true, "No decrypt ability is available");
            String n = cd.get("Key Alias").toString();

            if (n.equals(newKey))
            {
                Assert.assertTrue(cd.get("Number of Symmetric Keys").hashCode() > 0, "");
            }
            else
            {
                num2 = cd.get("Number of Symmetric Keys").hashCode();
                assertTrue(num<num2, "Number of symmetric keys isn't increased");
            }
        }

        // Perform a showMasterKey operation on another cluster node if you are using a cluster
        if (node2Url!=null)
        {
            result = JmxUtils.invokeAlfrescoOperation(node2Url, objectName, "showMasterKeys");
            tabularData=(TabularDataSupport)result;
            values=(Collection<CompositeData>)tabularData.values();
            cdArray = new CompositeData[]{};
            cdArrays = values.toArray(cdArray);

            long numNode2;
            for ( int  i = 0; i < cdArrays.length; ++i )
            {
                CompositeData cd = cdArrays[i];
                assertEquals(cd.get("Encryption Key Algorithm"), "RSA", "Encryption Key Algorithm isn't present");
                assertEquals(cd.get("Decryption Key Algorithm"), "RSA", "Decryption Key Algorithm isn't present");
                assertEquals(cd.get("Can Encrypt"), true, "No encrypt ability is available");
                assertEquals(cd.get("Can Decrypt"), true, "No decrypt ability is available");
                String n = cd.get("Key Alias").toString();

                if (n.equals(newKey))
                {
                    Assert.assertTrue(cd.get("Number of Symmetric Keys").hashCode() > 0, "");
                }
                else
                {
                    numNode2 = cd.get("Number of Symmetric Keys").hashCode();
                    assertTrue(numNode<numNode2, "Number of symmetric keys isn't increased");
                }
            }
        }
    }
}
