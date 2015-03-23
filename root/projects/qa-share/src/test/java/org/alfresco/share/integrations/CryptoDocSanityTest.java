package org.alfresco.share.integrations;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.adminconsole.NodeBrowserPage;
import org.alfresco.po.share.search.SearchBox;
import org.alfresco.po.share.search.SearchResultPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditTextDocumentPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.document.InlineEditPage;
import org.alfresco.po.share.site.document.MimeType;
import org.alfresco.po.share.systemsummary.AdminConsoleLink;
import org.alfresco.po.share.systemsummary.RepositoryServerClusteringPage;
import org.alfresco.po.share.systemsummary.SystemSummaryPage;
import org.alfresco.po.share.util.PageUtils;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.CifsUtil;
import org.alfresco.share.util.CryptodocUtil;
import org.alfresco.share.util.FtpUtil;
import org.alfresco.share.util.JmxUtils;
import org.alfresco.share.util.NodeBrowserPageUtil;
import org.alfresco.share.util.RemoteUtil;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSearchPage;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.SshCommandProcessor;
import org.alfresco.share.util.WebDavUtil;
import org.alfresco.share.util.WebDroneType;
import org.alfresco.webdrone.WebDroneImpl;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.apache.commons.io.FileUtils;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.po.share.site.document.ContentType.PLAINTEXT;
import static org.alfresco.share.util.ShareUser.createContent;
import static org.alfresco.share.util.ShareUser.openDocumentLibrary;
import static org.alfresco.share.util.ShareUser.openUserDashboard;
import static org.alfresco.share.util.SiteUtil.createSite;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;


/**
 * @author Maryia Zaichanka
 */


@Listeners(FailedTestListener.class)
public class CryptoDocSanityTest extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(CryptoDocSanityTest.class);
    private static String node1Url;
    private static String node2Url;
    private static final String regexUrl = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(:\\d{1,5})?";

    private static SshCommandProcessor commandProcessor;

    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
    private static final Pattern DOMAIN_PATTERN = Pattern.compile("\\w+(\\.\\w+)*(\\.\\w{2,})");
    private static String remotePathToSites = "/" + "Alfresco" + "/" + "Sites";
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
        node1Url = PageUtils.getAddress(shareUrl).replaceAll("(:\\d{1,5})?", "");

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
                CryptodocUtil.changeKeyProperties(node1Url, key + "," + newKey, keyPassword + "," + newKeyPassword);

            }
        }

    }

    @Test(groups = { "EnterpriseOnly", "Sanity", "NonGrid"}, timeOut = 400000)
    public void AONE_15973() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String plainText = getRandomString(3) + getFileName(testName);
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        setupCustomDrone(WebDroneType.DownLoadDrone);

        // Login
        dronePropertiesMap.get(customDrone).setShareUrl(node1Url);
        ShareUser.login(customDrone, ADMIN_USERNAME, ADMIN_PASSWORD);
        createSite(customDrone, siteName, siteName);
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);
        docLibPage = docLibPage.getNavigation().selectDetailedView().render();
        String mainWindow = customDrone.getWindowHandle();

        // Create any document via Create Menu, e.g. Plain Text.
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setContent(plainText);
        contentDetails.setName(plainText);
        contentDetails.setDescription(plainText);

        createContent(customDrone, contentDetails, PLAINTEXT);

        // Upload any document via Upload action
        // Upload any document using drag'n'drop action
        ShareUser.uploadFileInFolder(customDrone, fileInfo);

        String nodeRef = ShareUserSitePage.getFileDirectoryInfo(customDrone, plainText).getContentNodeRef();

        nodeRef = nodeRef.substring(nodeRef.indexOf("workspace"));
        nodeRef = nodeRef.replaceFirst("/", "://");

        String nodeRef2 = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName).getContentNodeRef();

        nodeRef2 = nodeRef2.substring(nodeRef2.indexOf("workspace"));
        nodeRef2 = nodeRef2.replaceFirst("/", "://");

        // Verify the documents' previews and thumbnails
        String [] docs = {plainText, fileName};
        for (String doc : docs)
        {
            FileDirectoryInfo fileDirectoryInfo = docLibPage.getFileDirectoryInfo(doc);
            assertTrue(!fileDirectoryInfo.getPreViewUrl().isEmpty(), "Thumbnail isn't displayed");
            DocumentDetailsPage detailsPage = fileDirectoryInfo.selectThumbnail().render();
            assertTrue(detailsPage.isDocumentDetailsPage(), "Failed to open Document Details page");
            if (alfrescoVersion.getVersion() < 5.0)
            {
                assertTrue(detailsPage.isFlashPreviewDisplayed(), "Preview isn't correctly displayed on details page");
            }
            else
            {
                assertTrue(detailsPage.getPreviewerClassName().equals("previewer PdfJs"), "Preview isn't correctly displayed on details page");
                assertTrue(detailsPage.getDocumentBody().contains(doc), "The document isn't unencrypted");
            }
            openDocumentLibrary(customDrone);
        }

        // Verify some basic actions of the documents, i.e. Download, View in Browser, Edit Inline, Edit Offline, Upload New version
        // download
        FileDirectoryInfo fileDirectoryInfo = docLibPage.getFileDirectoryInfo(plainText);
        fileDirectoryInfo.selectDownload();
        docLibPage.waitForFile(downloadDirectory + plainText);
        List<String> extractedChildFilesOrFolders = ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory);
        assertTrue(extractedChildFilesOrFolders.contains(plainText), "File isn't downloaded");

//        view in browser
        fileDirectoryInfo.selectViewInBrowser();
        String htmlSource = ((WebDroneImpl) customDrone).getDriver().getPageSource();
        assertTrue(htmlSource.contains(plainText), "Document isn't opened in a browser");
        customDrone.closeWindow();
        customDrone.switchToWindow(mainWindow);

        // edit inline
        InlineEditPage inlineEditPage = fileDirectoryInfo.selectInlineEdit().render();
        EditTextDocumentPage editTextDocumentPage = ((EditTextDocumentPage) inlineEditPage.getInlineEditDocumentPage(MimeType.TEXT)).render(maxWaitTime);

        contentDetails = new ContentDetails();
        String newDescription = getTestName() + " description";
        contentDetails.setDescription(newDescription);
        docLibPage = editTextDocumentPage.saveWithValidation(contentDetails).render(maxWaitTime);

        assertEquals(docLibPage.getFileDirectoryInfo(plainText).getDescription(), newDescription, "Document isn't edited");

        // edit offline
        fileDirectoryInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, plainText);
        fileDirectoryInfo.selectEditOffline().render();
        openDocumentLibrary(customDrone);
        fileDirectoryInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, plainText);
        assertEquals(fileDirectoryInfo.getContentInfo(), "This document is locked by you for offline editing.");

        // upload new version
        fileDirectoryInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName);
        String actualVersion = fileDirectoryInfo.getVersionInfo();

        UpdateFilePage updatePage = fileDirectoryInfo.selectUploadNewVersion().render(maxWaitTime);
        updatePage.selectMinorVersionChange();
        String fileNameNew = fileName + getRandomString(3) + ".txt";

        File newFileName = newFile(DATA_FOLDER + (fileNameNew), fileName);
        updatePage.uploadFile(newFileName.getCanonicalPath());
        SitePage sitePage = updatePage.submit().render();
        sitePage.render();
        FileUtils.forceDelete(newFileName);

        // verify version
        docLibPage = openDocumentLibrary(customDrone);
        fileDirectoryInfo = docLibPage.getFileDirectoryInfo(fileName);
        String currentVersion = fileDirectoryInfo.getVersionInfo();
        assertNotEquals(actualVersion, currentVersion, "Version of a document isn't changed");

        // Verify the documents can be found
        if (alfrescoVersion.getVersion() < 5.0)
        {
            for (String doc : docs)
            {
                SharePage page = customDrone.getCurrentPage().render();
                DashBoardPage dashBoard = page.getNav().selectMyDashBoard().render();
                SearchBox search = dashBoard.getSearch();

                SearchResultPage resultPage = search.search(doc).render();
                assertTrue(resultPage.getResults().size()>0, "Document isn't found");
            }

        }
        else
        {
            openUserDashboard(customDrone);
            ShareUserSearchPage.basicSearch(customDrone, plainText, true);
            assertTrue(ShareUserSearchPage.isSearchItemInFacetSearchPage(customDrone, plainText), "Document isn't found");

            openUserDashboard(customDrone);
            ShareUserSearchPage.basicSearch(customDrone, fileName, false);
            assertTrue(ShareUserSearchPage.isSearchItemInFacetSearchPage(customDrone, fileName), "Document isn't found");
        }

        for (String doc : docs) {

            // Find any of the documents via Node Browser
            NodeBrowserPage nodeBrowserPage = NodeBrowserPageUtil.openNodeBrowserPage(customDrone);
            String url = customDrone.getCurrentUrl();
            if (doc.equals(docs[0]))
            {
                nodeBrowserPage = NodeBrowserPageUtil.executeQuery(customDrone, nodeRef, NodeBrowserPage.QueryType.NODE_REF, NodeBrowserPage.Store.WORKSPACE_SPACE_STORE)
                        .render();
                getCurrentPage(customDrone).render(maxWaitTime);
                assertTrue(nodeBrowserPage.isInResultsByName(doc) && nodeBrowserPage.isInResultsByNodeRef(nodeRef),
                        "Nothing was found or there was found incorrect file by nodeRef");
            }
            else
            {
                nodeBrowserPage = NodeBrowserPageUtil.executeQuery(customDrone, nodeRef2, NodeBrowserPage.QueryType.NODE_REF, NodeBrowserPage.Store.WORKSPACE_SPACE_STORE)
                        .render();
                getCurrentPage(customDrone).render(maxWaitTime);
                assertTrue(nodeBrowserPage.isInResultsByName(doc) && nodeBrowserPage.isInResultsByNodeRef(nodeRef2),
                        "Nothing was found or there was found incorrect file by nodeRef");
            }

            nodeBrowserPage.getItemDetails(doc);
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
            RemoteUtil.checkForStrings(doc, filepath, resultsPath);

            assertTrue(RemoteUtil.isFileEmpty(resultsPath), "File " + doc + " wasn't encrypted");
            customDrone.navigateTo(url);
        }

        customDrone.closeWindow();
    }

    @Test(groups = { "EnterpriseOnly", "Sanity", "NonGrid"}, timeOut = 600000)
    public void AONE_15974() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String plainText = getRandomString(3) + getFileName(testName);
        String fileName = getFileName(testName);
        String[] dots = {fileName + ".doc", fileName + ".docx", fileName + ".xls", fileName + ".xlsx", fileName + ".ppt",
                fileName +  ".pptx", fileName +  ".odt", fileName + ".ods", fileName + ".odp", fileName + ".txt", fileName + ".rtf",
                fileName + ".xml", fileName + ".html"};
        String[] contents = {plainText + 0, plainText + 1};
        String remotePathFtp = remotePathToSites + "/" + siteName + "/" + "documentLibrary";

        ArrayList<String[]> allDocs = new ArrayList<String[]>();
        allDocs.add(dots);
        allDocs.add(contents);


        setupCustomDrone(WebDroneType.DownLoadDrone);

        // Login
//        dronePropertiesMap.get(customDrone).setShareUrl(node1Url);
        ShareUser.login(customDrone, ADMIN_USERNAME, ADMIN_PASSWORD);
        createSite(customDrone, siteName, siteName);
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);
        docLibPage = docLibPage.getNavigation().selectDetailedView().render();
        String mainWindow = customDrone.getWindowHandle();
        String url = customDrone.getCurrentUrl();


        // Create any two documents with different names and same content
        // Verify the documents' previews and thumbnails
        for (int i=0; i<=1; i++)
        {
            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setContent(plainText);
            contentDetails.setName(plainText + i);
            contentDetails.setDescription(plainText);

            createContent(customDrone, contentDetails, PLAINTEXT);

        }

        // Upload several documents of the supported mimetypes
        for (String dot : dots)
        {
            String[] fileInfo = {dot, DOCLIB};
            ShareUser.uploadFileInFolder(customDrone, fileInfo);
            assertTrue(docLibPage.isFileVisible(dot), "File isn't uploaded");
        }

        // Verify the documents' previews and thumbnails
            for (String dot : dots)
            {
                FileDirectoryInfo fileDirectoryInfo = docLibPage.getFileDirectoryInfo(dot);
                assertTrue(!fileDirectoryInfo.getPreViewUrl().isEmpty(), "Thumbnail isn't displayed");
                DocumentDetailsPage detailsPage = fileDirectoryInfo.selectThumbnail().render(maxWaitTime);
                assertTrue(detailsPage.isDocumentDetailsPage(), "Failed to open Document Details page");
                if (alfrescoVersion.getVersion() < 5.0)
                {
                    assertTrue(detailsPage.isFlashPreviewDisplayed(), "Preview isn't correctly displayed on details page");
                }
                else
                {
                    assertTrue(detailsPage.getPreviewerClassName().equals("previewer PdfJs"), "Preview isn't correctly displayed on details page");
                    assertTrue(detailsPage.getDocumentBody().contains(dot), "The document isn't unencrypted");
                }
                openDocumentLibrary(customDrone).render(maxWaitTime);
            }

        for (int i=0; i<=1; i++)
        {
            FileDirectoryInfo fileDirectoryInfo = docLibPage.getFileDirectoryInfo(plainText + i);
            assertTrue(!fileDirectoryInfo.getPreViewUrl().isEmpty(), "Thumbnail isn't displayed");
            DocumentDetailsPage detailsPage = fileDirectoryInfo.selectThumbnail().render(maxWaitTime);
            assertTrue(detailsPage.isDocumentDetailsPage(), "Failed to open Document Details page");
            if (alfrescoVersion.getVersion() < 5.0)
            {
                assertTrue(detailsPage.isFlashPreviewDisplayed(), "Preview isn't correctly displayed on details page");
            }
            else
            {
                assertTrue(detailsPage.getPreviewerClassName().equals("previewer PdfJs"), "Preview isn't correctly displayed on details page");
                assertTrue(detailsPage.getDocumentBody().contains(plainText), "The document isn't unencrypted");
            }
            openDocumentLibrary(customDrone).render(maxWaitTime);
        }
        // Verify some basic actions of the documents, i.e. Download, View in Browser, Edit Inline, Edit Offline, Upload New version
        // download
        FileDirectoryInfo fileDirectoryInfo = docLibPage.getFileDirectoryInfo(plainText + 0);
        fileDirectoryInfo.selectDownload();
        docLibPage.waitForFile(downloadDirectory + plainText + 0);
        List<String> extractedChildFilesOrFolders = ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory);
        assertTrue(extractedChildFilesOrFolders.contains(plainText + 0), "File isn't downloaded");

//        view in browser
        fileDirectoryInfo.selectViewInBrowser();
        String htmlSource = ((WebDroneImpl) customDrone).getDriver().getPageSource();
        assertTrue(htmlSource.contains(plainText), "Document isn't opened in a browser");
        customDrone.closeWindow();
        customDrone.switchToWindow(mainWindow);

        // edit inline
        InlineEditPage inlineEditPage = fileDirectoryInfo.selectInlineEdit().render();
        EditTextDocumentPage editTextDocumentPage = ((EditTextDocumentPage) inlineEditPage.getInlineEditDocumentPage(MimeType.TEXT)).render(maxWaitTime);

        ContentDetails contentDetails = new ContentDetails();
        String newDescription = getTestName() + " description";
        contentDetails.setDescription(newDescription);
        docLibPage = editTextDocumentPage.saveWithValidation(contentDetails).render(maxWaitTime);

        assertEquals(docLibPage.getFileDirectoryInfo(plainText + 0).getDescription(), newDescription, "Document isn't edited");

        // edit offline
        fileDirectoryInfo = docLibPage.getFileDirectoryInfo(plainText + 0);
        fileDirectoryInfo.selectEditOffline().render();
        openDocumentLibrary(customDrone);
        fileDirectoryInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, plainText + 0);
        assertEquals(fileDirectoryInfo.getContentInfo(), "This document is locked by you for offline editing.");
        docLibPage = openDocumentLibrary(customDrone);
        fileDirectoryInfo = docLibPage.getFileDirectoryInfo(plainText + 0);
        fileDirectoryInfo.selectCancelEditing();

        for (String dot : dots)
        {
            // upload new version
            docLibPage = openDocumentLibrary(customDrone);
            fileDirectoryInfo = docLibPage.getFileDirectoryInfo(dot);
            String actualVersion = fileDirectoryInfo.getVersionInfo();

            UpdateFilePage updatePage = fileDirectoryInfo.selectUploadNewVersion().render(maxWaitTime);
            updatePage.selectMinorVersionChange();
            String fileNameNew = getRandomString(3) + dot;

            File newFileName = newFile(DATA_FOLDER + (fileNameNew), dot);
            updatePage.uploadFile(newFileName.getCanonicalPath());
            updatePage.submit().render();
            FileUtils.forceDelete(newFileName);

            // verify version
            fileDirectoryInfo = docLibPage.getFileDirectoryInfo(dot);
            String currentVersion = fileDirectoryInfo.getVersionInfo();
            assertNotEquals(actualVersion, currentVersion, "Version isn't increased");
            getCurrentPage(customDrone).render(maxWaitTime);
        }

        // Verify the documents can be found
        if (alfrescoVersion.getVersion() < 5.0)
        {
            for (String docs[] : allDocs)
            {
                for (String doc : docs)
                {
                    SharePage page = customDrone.getCurrentPage().render();
                    DashBoardPage dashBoard = page.getNav().selectMyDashBoard().render();
                    SearchBox search = dashBoard.getSearch();

                    SearchResultPage resultPage = search.search(doc).render(maxWaitTime);
                    assertTrue(resultPage.getResults().size()>0, "Document isn't found");
                }
            }

        }
        else
        {
            for (String docs[] : allDocs)
            {
                for (String doc : docs)
                {
                    openUserDashboard(customDrone);
                    ShareUserSearchPage.basicSearch(customDrone, String.valueOf(doc), false);
                    assertTrue(ShareUserSearchPage.isSearchItemInFacetSearchPage(customDrone, doc), "Document isn't found");
                }
            }
        }

        // Verify all other created/uploaded document's .bin files
        for (String docs[] : allDocs)
        {
            for (String doc : docs)
            {
                // Find any of the documents via Node Browser
                customDrone.navigateTo(url);
//                docLibPage = docLibPage.getNavigation().selectDetailedView().render();
                String nodeRef = docLibPage.getFileDirectoryInfo(doc).getContentNodeRef();

                nodeRef = nodeRef.substring(nodeRef.indexOf("workspace"));
                nodeRef = nodeRef.replaceFirst("/", "://");
                NodeBrowserPage nodeBrowserPage = NodeBrowserPageUtil.openNodeBrowserPage(customDrone);
                nodeBrowserPage = NodeBrowserPageUtil.executeQuery(customDrone, nodeRef, NodeBrowserPage.QueryType.NODE_REF, NodeBrowserPage.Store.WORKSPACE_SPACE_STORE)
                            .render();
                getCurrentPage(customDrone).render(maxWaitTime);
                assertTrue(nodeBrowserPage.isInResultsByName(doc) && nodeBrowserPage.isInResultsByNodeRef(nodeRef),
                            "Nothing was found or there was found incorrect file by nodeRef");

                nodeBrowserPage.getItemDetails(doc);
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
                RemoteUtil.checkForStrings(doc, filepath, resultsPath);

                assertTrue(RemoteUtil.isFileEmpty(resultsPath), "File " + doc + " wasn't encrypted");
            }
        }

        String fileChecksum = null;
        String fileChecksum2;

        for (int i=0; i<=1; i++)
        {
            // Copy the .bin files of the two documents created in step 1 to your local machine
            customDrone.navigateTo(url);
            String nodeRef = docLibPage.getFileDirectoryInfo(plainText + i).getContentNodeRef();

            nodeRef = nodeRef.substring(nodeRef.indexOf("workspace"));
            nodeRef = nodeRef.replaceFirst("/", "://");
            NodeBrowserPage nodeBrowserPage = NodeBrowserPageUtil.openNodeBrowserPage(customDrone);
            nodeBrowserPage = NodeBrowserPageUtil.executeQuery(customDrone, nodeRef, NodeBrowserPage.QueryType.NODE_REF, NodeBrowserPage.Store.WORKSPACE_SPACE_STORE)
                    .render();
            getCurrentPage(customDrone).render(maxWaitTime);
            assertTrue(nodeBrowserPage.isInResultsByName(plainText + i) && nodeBrowserPage.isInResultsByNodeRef(nodeRef),
                    "Nothing was found or there was found incorrect file by nodeRef");

            nodeBrowserPage.getItemDetails(plainText + i);
            String binPath = nodeBrowserPage.getContentUrl();
            binPath = binPath.substring(binPath.indexOf("store"), binPath.lastIndexOf("bin"));
            binPath = binPath.replace("store://", "contentstore/");
            String binName=binPath.substring(binPath.lastIndexOf("/")) + "bin";
            binName = binName.replace("/", "");

            // On your server machine open your alf_data/contentstore directory
            // Open the a .bin file and verify its content
            setSshHost(node1Url);
            initConnection();
            String alfHome = JmxUtils.getAlfrescoServerProperty("Alfresco:Name=SystemProperties", "alfresco.home").toString();
            alfHome.replace("/", SLASH);
            String filepath = alfHome + "/alf_data/" + binPath + "bin";
            assertTrue(RemoteUtil.isFileExist(filepath), ".bin file isn't present at a contentstore");

            // Copy the .bin files of the two documents created in step 1 to your local machine
            String server = getAddress(node1Url);
            RemoteUtil.uploadFileFromRemoteFolderToLocalFtp(filepath,remotePathFtp + "/", ADMIN_USERNAME, ADMIN_PASSWORD, server);
            FtpUtil.downloadContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, binName, remotePathFtp);
            if (i==0)
            {
                fileChecksum = checksum(binName);

            }

            else
            {
                fileChecksum2 = checksum(binName);
                // Verify MD5 checksum for both files
                assertNotEquals(fileChecksum, fileChecksum2, "Files' checksums are equal");
            }

            FileUtils.forceDelete(new File(downloadDirectory + binName));
        }

//        Download both documents created in step1 via Share and verify their MD5 checksums
        for (int i=0; i<=1; i++)
        {
            customDrone.navigateTo(url);
            fileDirectoryInfo = docLibPage.getFileDirectoryInfo(plainText + i);
            fileDirectoryInfo.selectDownload();
            docLibPage.waitForFile(downloadDirectory + plainText + i);
            extractedChildFilesOrFolders = ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory);
            assertTrue(extractedChildFilesOrFolders.contains(plainText + i), "File isn't downloaded");

            if (i==0)
            {
                fileChecksum = checksum(plainText + i);

            }

            else
            {
                fileChecksum2 = checksum(plainText + i);
                // Verify MD5 checksum for both files
                assertEquals(fileChecksum, fileChecksum2, "Files' checksums aren't equal");
            }

            FileUtils.forceDelete(new File(downloadDirectory + plainText + i));
        }

        customDrone.closeWindow();
    }

    @Test(groups = { "EnterpriseOnly", "Sanity", "NonGrid"}, timeOut = 400000)
    public void AONE_15979() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String plainText = getFileName(testName);
        String fileName = getFileName(testName) + ".txt";
        File file = newFile(fileName, fileName);
        String remotePath = "Alfresco" + "/" + "Sites" + "/" + siteName + "/" + "documentLibrary" + "/";

        setupCustomDrone(WebDroneType.DownLoadDrone);

        // Login
        dronePropertiesMap.get(customDrone).setShareUrl(node1Url);
        ShareUser.login(customDrone, ADMIN_USERNAME, ADMIN_PASSWORD);
        createSite(customDrone, siteName, siteName);
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);
        docLibPage = docLibPage.getNavigation().selectDetailedView().render();
        String url = customDrone.getCurrentUrl();

        String mainWindow = customDrone.getWindowHandle();

        // Create any empty and non-empty docs
        for (int i=0; i<=1; i++)
        {
            ContentDetails contentDetails = new ContentDetails();
            if (i==0)
            {
                contentDetails.setContent(plainText + i);
            }
            contentDetails.setName(plainText + i);
            contentDetails.setDescription(plainText + i);

            createContent(customDrone, contentDetails, PLAINTEXT);
        }

        // Open the created in the pre-condition documents
        assertTrue(CifsUtil.checkContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, plainText + 0, plainText + 0), "Content isn't unencrypted");
        assertTrue(CifsUtil.checkContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, plainText+1, ""), "Content isn't unencrypted\"");

        // Download both documents
        assertTrue(CifsUtil.downloadContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, plainText + 0), "Document isn't downloaded");
        assertTrue(CifsUtil.downloadContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, plainText + 1), "Document isn't downloaded");

        // Their content is displayed correctly
        String content = getTextFromDownloadDirectoryFile(plainText+0);
        String contentEmpty = getTextFromDownloadDirectoryFile(plainText+1);

        assertTrue(content.contains(plainText + 0), "Content of the document isn't displayed correctly");
        assertTrue(contentEmpty.isEmpty(), "Content of the document isn't displayed correctly");

        // Upload the created in the pre-condition document to the site's Document Library
        CifsUtil.uploadContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD,remotePath, file);
        assertTrue(CifsUtil.checkItem(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, fileName), "File isn't uploaded");

        // Check the document was encrypted
        openDocumentLibrary(customDrone);
        String nodeRef = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName).getContentNodeRef();
        String binPath = CryptodocUtil.getBinPath(customDrone, fileName, nodeRef);

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
        RemoteUtil.checkForStrings(fileName, filepath, resultsPath);

        assertTrue(RemoteUtil.isFileEmpty(resultsPath), "File " + fileName + " wasn't encrypted");
        customDrone.navigateTo(url);

        // Check the document is available for usage in Share
        // download
        FileDirectoryInfo fileDirectoryInfo = docLibPage.getFileDirectoryInfo(fileName);
        fileDirectoryInfo.selectDownload();
        docLibPage.waitForFile(downloadDirectory + plainText);
        List<String> extractedChildFilesOrFolders = ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory);
        assertTrue(extractedChildFilesOrFolders.contains(plainText), "File isn't downloaded");

//        view in browser
        fileDirectoryInfo.selectViewInBrowser();
        String htmlSource = ((WebDroneImpl) customDrone).getDriver().getPageSource();
        assertTrue(htmlSource.contains(fileName), "Document isn't opened in a browser");
        customDrone.closeWindow();
        customDrone.switchToWindow(mainWindow);

        // edit inline
        InlineEditPage inlineEditPage = fileDirectoryInfo.selectInlineEdit().render();
        EditTextDocumentPage editTextDocumentPage = ((EditTextDocumentPage) inlineEditPage.getInlineEditDocumentPage(MimeType.TEXT)).render(maxWaitTime);

        ContentDetails contentDetails = new ContentDetails();
        String newDescription = getTestName() + " description";
        contentDetails.setDescription(newDescription);
        docLibPage = editTextDocumentPage.saveWithValidation(contentDetails).render(maxWaitTime);

        assertEquals(docLibPage.getFileDirectoryInfo(fileName).getDescription(), newDescription, "Document isn't edited");

        // edit offline
        fileDirectoryInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName);
        fileDirectoryInfo.selectEditOffline().render();
        openDocumentLibrary(customDrone);
        fileDirectoryInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName);
        assertEquals(fileDirectoryInfo.getContentInfo(), "This document is locked by you for offline editing.");

        // upload new version
        fileDirectoryInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName);
        String actualVersion = fileDirectoryInfo.getVersionInfo();

        UpdateFilePage updatePage = fileDirectoryInfo.selectUploadNewVersion().render(maxWaitTime);
        updatePage.selectMinorVersionChange();
        String fileNameNew = fileName + getRandomString(3) + ".txt";

        File newFileName = newFile(DATA_FOLDER + (fileNameNew), fileName);
        updatePage.uploadFile(newFileName.getCanonicalPath());
        SitePage sitePage = updatePage.submit().render();
        sitePage.render();
        FileUtils.forceDelete(newFileName);

        // verify version
        docLibPage = openDocumentLibrary(customDrone);
        fileDirectoryInfo = docLibPage.getFileDirectoryInfo(fileName);
        String currentVersion = fileDirectoryInfo.getVersionInfo();
        assertNotEquals(actualVersion, currentVersion, "Version isn't changed");

        // Verify the documents' previews and thumbnails
        fileDirectoryInfo = docLibPage.getFileDirectoryInfo(fileName);
        assertTrue(!fileDirectoryInfo.getPreViewUrl().isEmpty(), "Thumbnail isn't displayed");
        DocumentDetailsPage detailsPage = fileDirectoryInfo.selectThumbnail().render();
        assertTrue(detailsPage.isDocumentDetailsPage(), "Failed to open Document Details page");

        if (alfrescoVersion.getVersion() < 5.0)
        {
            assertTrue(detailsPage.isFlashPreviewDisplayed(), "Preview isn't correctly displayed on details page");
            assertTrue(CifsUtil.checkContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, fileName, fileName), "The document isn't unencrypted");
        }
        else
        {
            assertTrue(detailsPage.getPreviewerClassName().equals("previewer PdfJs"), "Preview isn't correctly displayed on details page");
            assertTrue(detailsPage.getDocumentBody().contains(fileName), "The document isn't unencrypted");
        }

        openDocumentLibrary(customDrone);

        // Create any document using context menu in the site's Document Library
        contentDetails = new ContentDetails();
        contentDetails.setContent(plainText + 2);
        contentDetails.setName(plainText + 2);
        contentDetails.setDescription(plainText + 2);
        createContent(customDrone, contentDetails, PLAINTEXT);

        // Check the document was encrypted
        nodeRef = ShareUserSitePage.getFileDirectoryInfo(customDrone, plainText + 2).getContentNodeRef();
        binPath = CryptodocUtil.getBinPath(customDrone, plainText + 2, nodeRef);

        // On your server machine open your alf_data/contentstore directory
        // Open the a .bin file and verify its content
        setSshHost(node1Url);
        initConnection();
        alfHome = JmxUtils.getAlfrescoServerProperty("Alfresco:Name=SystemProperties", "alfresco.home").toString();
        alfHome.replace("/", SLASH);
        filepath = alfHome + "/alf_data/" + binPath + "bin";
        assertTrue(RemoteUtil.isFileExist(filepath), ".bin file isn't present at a contentstore");

        resultFile = "results.txt";
        resultsPath = alfHome + "/" + resultFile;
        RemoteUtil.checkForStrings(plainText + 2, filepath, resultsPath);

        assertTrue(RemoteUtil.isFileEmpty(resultsPath), "File " + plainText + 2 + " wasn't encrypted");
        customDrone.navigateTo(url);

        // Check the document is available for usage in Share
        // download
        fileDirectoryInfo = docLibPage.getFileDirectoryInfo(plainText + 2);
        fileDirectoryInfo.selectDownload();
        docLibPage.waitForFile(downloadDirectory + plainText + 2);
        extractedChildFilesOrFolders = ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory);
        assertTrue(extractedChildFilesOrFolders.contains(plainText + 2), "File isn't downloaded");

//        view in browser
        fileDirectoryInfo.selectViewInBrowser();
        htmlSource = ((WebDroneImpl) customDrone).getDriver().getPageSource();
        assertTrue(htmlSource.contains(plainText + 2), "Document isn't opened in a browser");
        customDrone.closeWindow();
        customDrone.switchToWindow(mainWindow);

        // edit inline
        inlineEditPage = fileDirectoryInfo.selectInlineEdit().render();
        editTextDocumentPage = ((EditTextDocumentPage) inlineEditPage.getInlineEditDocumentPage(MimeType.TEXT)).render(maxWaitTime);

        contentDetails = new ContentDetails();
        newDescription = getTestName() + " description";
        contentDetails.setDescription(newDescription);
        docLibPage = editTextDocumentPage.saveWithValidation(contentDetails).render(maxWaitTime);

        assertEquals(docLibPage.getFileDirectoryInfo(plainText + 2).getDescription(), newDescription, "Document isn't edited");

        // edit offline
        fileDirectoryInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, plainText + 2);
        fileDirectoryInfo.selectEditOffline().render();
        openDocumentLibrary(customDrone);
        fileDirectoryInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, plainText + 2);
        assertEquals(fileDirectoryInfo.getContentInfo(), "This document is locked by you for offline editing.");
        docLibPage = openDocumentLibrary(customDrone);
        fileDirectoryInfo = docLibPage.getFileDirectoryInfo(plainText + 2);
        fileDirectoryInfo.selectCancelEditing();


        // Download both documents to your client machine
        String [] docs = {fileName, plainText + 2};
        for (String doc : docs)
        {
            assertTrue(CifsUtil.downloadContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, doc), doc + " isn't downloaded");

            // Their content is displayed correctly
            content = getTextFromDownloadDirectoryFile(doc);
            assertTrue(content.contains(doc), "Content isn't displayed correctly");
        }

        // Try to edit all of the documents via FTP
        String [] items = {plainText + 0, plainText + 1, plainText + 2, fileName};
        for (String item : items)
        {
            assertTrue(CifsUtil.editContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, item, ADMIN_USERNAME), "Can't edit " + item);
            assertTrue(CifsUtil.checkContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, remotePath, item, ADMIN_USERNAME), "");
        }

        customDrone.closeWindow();

    }

    @Test(groups = { "EnterpriseOnly", "Sanity", "NonGrid"}, timeOut = 400000)
    public void AONE_15980() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String plainText = getFileName(testName);
        String fileName = getFileName(testName) + ".txt";
        File file = newFile(fileName, fileName);
        String remotePath = remotePathToSites + "/" + siteName + "/" + "documentLibrary";

        setupCustomDrone(WebDroneType.DownLoadDrone);

        // Login
        dronePropertiesMap.get(customDrone).setShareUrl(node1Url);
        ShareUser.login(customDrone, ADMIN_USERNAME, ADMIN_PASSWORD);
        createSite(customDrone, siteName, siteName);
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);
        docLibPage = docLibPage.getNavigation().selectDetailedView().render();
        String url = customDrone.getCurrentUrl();

        String mainWindow = customDrone.getWindowHandle();

        // Create any empty and non-empty docs
        for (int i=0; i<=1; i++)
        {
            ContentDetails contentDetails = new ContentDetails();
            if (i==0)
            {
                contentDetails.setContent(plainText + i);
            }
            contentDetails.setName(plainText + i);
            contentDetails.setDescription(plainText + i);

            createContent(customDrone, contentDetails, PLAINTEXT);
        }

        // Open the created in the pre-condition documents
        assertTrue(FtpUtil.getContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, plainText+0, remotePath).contains(plainText), "Content isn't displayed correctly");
        assertTrue(FtpUtil.getContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, plainText+1, remotePath).isEmpty(), "Content isn't displayed correctly");

        // Download both documents
        assertTrue(FtpUtil.downloadContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, plainText + 0, remotePath), "Document isn't downloaded");
        assertTrue(FtpUtil.downloadContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, plainText + 1, remotePath), "Document isn't downloaded");

        // Their content is displayed correctly
        String content = getTextFromDownloadDirectoryFile(plainText+0);
        String contentEmpty = getTextFromDownloadDirectoryFile(plainText+1);

        assertTrue(content.contains(plainText + 0), "Content isn't displayed correctly");
        assertTrue(contentEmpty.isEmpty(), "Content isn't displayed correctly");

        // Upload the created in the pre-condition document to the site's Document Library
        FtpUtil.uploadContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, file,remotePath);
        assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName, remotePath + "/"), "File isn't uploaded");

        // Check the document was encrypted
        openDocumentLibrary(customDrone);
        String nodeRef = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName).getContentNodeRef();
        String binPath = CryptodocUtil.getBinPath(customDrone, fileName, nodeRef);

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
        RemoteUtil.checkForStrings(fileName, filepath, resultsPath);

        assertTrue(RemoteUtil.isFileEmpty(resultsPath), "File " + fileName + " wasn't encrypted");

        customDrone.navigateTo(url);

        // Check the document is available for usage in Share
        // download
        FileDirectoryInfo fileDirectoryInfo = docLibPage.getFileDirectoryInfo(fileName);
        fileDirectoryInfo.selectDownload();
        docLibPage.waitForFile(downloadDirectory + plainText);
        List<String> extractedChildFilesOrFolders = ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory);
        assertTrue(extractedChildFilesOrFolders.contains(plainText), "File isn't downloaded");

//        view in browser
        fileDirectoryInfo.selectViewInBrowser();
        String htmlSource = ((WebDroneImpl) customDrone).getDriver().getPageSource();
        assertTrue(htmlSource.contains(fileName), "Document isn't opened in a browser");
        customDrone.closeWindow();
        customDrone.switchToWindow(mainWindow);

        // edit inline
        InlineEditPage inlineEditPage = fileDirectoryInfo.selectInlineEdit().render();
        EditTextDocumentPage editTextDocumentPage = ((EditTextDocumentPage) inlineEditPage.getInlineEditDocumentPage(MimeType.TEXT)).render(maxWaitTime);

        ContentDetails contentDetails = new ContentDetails();
        String newDescription = getTestName() + " description";
        contentDetails.setDescription(newDescription);
        docLibPage = editTextDocumentPage.saveWithValidation(contentDetails).render(maxWaitTime);

        assertEquals(docLibPage.getFileDirectoryInfo(fileName).getDescription(), newDescription, "Document isn't edited");

        // edit offline
        fileDirectoryInfo = docLibPage.getFileDirectoryInfo(fileName);
        fileDirectoryInfo.selectEditOffline().render();
        openDocumentLibrary(customDrone);
        fileDirectoryInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName);
        assertEquals(fileDirectoryInfo.getContentInfo(), "This document is locked by you for offline editing.");

        // upload new version
        fileDirectoryInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName);
        String actualVersion = fileDirectoryInfo.getVersionInfo();

        UpdateFilePage updatePage = fileDirectoryInfo.selectUploadNewVersion().render(maxWaitTime);
        updatePage.selectMinorVersionChange();
        String fileNameNew = fileName + getRandomString(3) + ".txt";

        File newFileName = newFile(DATA_FOLDER + (fileNameNew), fileName);
        updatePage.uploadFile(newFileName.getCanonicalPath());
        SitePage sitePage = updatePage.submit().render();
        sitePage.render();
        FileUtils.forceDelete(newFileName);

        // verify version
        docLibPage = openDocumentLibrary(customDrone);
        fileDirectoryInfo = docLibPage.getFileDirectoryInfo(fileName);
        String currentVersion = fileDirectoryInfo.getVersionInfo();
        assertNotEquals(actualVersion, currentVersion, "Version isn't changed");

        // Verify the documents' previews and thumbnails
        fileDirectoryInfo = docLibPage.getFileDirectoryInfo(fileName);
        assertTrue(!fileDirectoryInfo.getPreViewUrl().isEmpty(), "Thumbnail isn't displayed");
        DocumentDetailsPage detailsPage = fileDirectoryInfo.selectThumbnail().render();
        assertTrue(detailsPage.isDocumentDetailsPage(), "Failed to open Document Details page");

        if (alfrescoVersion.getVersion() < 5.0)
        {
            assertTrue(detailsPage.isFlashPreviewDisplayed(), "Preview isn't correctly displayed on details page");
            assertTrue(FtpUtil.getContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName, remotePath).equals(fileName), "The document isn't unencrypted");
        }
        else
        {
            assertTrue(detailsPage.getPreviewerClassName().equals("previewer PdfJs"), "Preview isn't correctly displayed on details page");
            assertTrue(detailsPage.getDocumentBody().contains(fileName), "The document isn't unencrypted");
        }
        openDocumentLibrary(customDrone);

        // Create any document using context menu in the site's Document Library
        contentDetails = new ContentDetails();
        contentDetails.setContent(plainText + 2);
        contentDetails.setName(plainText + 2);
        contentDetails.setDescription(plainText + 2);
        createContent(customDrone, contentDetails, PLAINTEXT);

        // Check the document was encrypted
        nodeRef = ShareUserSitePage.getFileDirectoryInfo(customDrone, plainText + 2).getContentNodeRef();
        binPath = CryptodocUtil.getBinPath(customDrone, plainText + 2, nodeRef);

        // On your server machine open your alf_data/contentstore directory
        // Open the a .bin file and verify its content
        setSshHost(node1Url);
        initConnection();
        alfHome = JmxUtils.getAlfrescoServerProperty("Alfresco:Name=SystemProperties", "alfresco.home").toString();
        alfHome.replace("/", SLASH);
        filepath = alfHome + "/alf_data/" + binPath + "bin";
        assertTrue(RemoteUtil.isFileExist(filepath), ".bin file isn't present at a contentstore");

        resultFile = "results.txt";
        resultsPath = alfHome + "/" + resultFile;
        RemoteUtil.checkForStrings(plainText + 2, filepath, resultsPath);

        assertTrue(RemoteUtil.isFileEmpty(resultsPath), "File " + plainText + 2 + " wasn't encrypted");
        customDrone.navigateTo(url);

        // Check the document is available for usage in Share
        // download
        fileDirectoryInfo = docLibPage.getFileDirectoryInfo(plainText + 2);
        fileDirectoryInfo.selectDownload();
        docLibPage.waitForFile(downloadDirectory + plainText + 2);
        extractedChildFilesOrFolders = ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory);
        assertTrue(extractedChildFilesOrFolders.contains(plainText + 2), "File isn't downloaded");

//        view in browser
        fileDirectoryInfo.selectViewInBrowser();
        htmlSource = ((WebDroneImpl) customDrone).getDriver().getPageSource();
        assertTrue(htmlSource.contains(plainText + 2), "Document isn't opened in a browser");
        customDrone.closeWindow();
        customDrone.switchToWindow(mainWindow);

        // edit inline
        inlineEditPage = fileDirectoryInfo.selectInlineEdit().render();
        editTextDocumentPage = ((EditTextDocumentPage) inlineEditPage.getInlineEditDocumentPage(MimeType.TEXT)).render(maxWaitTime);

        contentDetails = new ContentDetails();
        newDescription = getTestName() + " description";
        contentDetails.setDescription(newDescription);
        docLibPage = editTextDocumentPage.saveWithValidation(contentDetails).render(maxWaitTime);

        assertEquals(docLibPage.getFileDirectoryInfo(plainText + 2).getDescription(), newDescription, "Document isn't edited");

        // edit offline
        fileDirectoryInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, plainText + 2);
        fileDirectoryInfo.selectEditOffline().render();
        openDocumentLibrary(customDrone);
        fileDirectoryInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, plainText + 2);
        assertEquals(fileDirectoryInfo.getContentInfo(), "This document is locked by you for offline editing.");
        docLibPage = openDocumentLibrary(customDrone);
        fileDirectoryInfo = docLibPage.getFileDirectoryInfo(plainText + 2);
        fileDirectoryInfo.selectCancelEditing();


        // Download both documents to your client machine
        String [] docs = {fileName, plainText + 2};
        for (String doc : docs)
        {
            assertTrue(FtpUtil.downloadContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, doc, remotePath), doc + " isn't downloaded");

            // Their content is displayed correctly
            content = getTextFromDownloadDirectoryFile(doc);
            assertTrue(content.contains(doc), "Content isn't displayed correctly");
        }

        // Try to edit all of the documents via FTP
        String [] items = {plainText + 0, plainText + 1, plainText + 2, fileName};
        for (String item : items)
        {
            assertTrue(FtpUtil.editContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, item, remotePath), "Can't edit " + item);
            assertTrue(FtpUtil.getContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, item, remotePath).equals(ADMIN_USERNAME), "");
        }

        customDrone.closeWindow();

    }

    @Test(groups = { "EnterpriseOnly", "Sanity", "NonGrid"}, timeOut = 400000)
    public void AONE_15981() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String plainText = getFileName(testName);
        String fileName = getFileName(testName) + ".txt";
        File file = newFile(fileName, fileName);
        String remotePath = "Sites/" + siteName + "/documentLibrary/";
        String path = "/alfresco/webdav/";


        setupCustomDrone(WebDroneType.DownLoadDrone);

        // Login
        dronePropertiesMap.get(customDrone).setShareUrl(node1Url);
        ShareUser.login(customDrone, ADMIN_USERNAME, ADMIN_PASSWORD);
        createSite(customDrone, siteName, siteName);
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);
        docLibPage = docLibPage.getNavigation().selectDetailedView().render();
        String url = customDrone.getCurrentUrl();

        String mainWindow = customDrone.getWindowHandle();

        // Create any empty and non-empty docs
        for (int i=0; i<=1; i++)
        {
            ContentDetails contentDetails = new ContentDetails();
            if (i==0)
            {
                contentDetails.setContent(plainText + i);
            }
            contentDetails.setName(plainText + i);
            contentDetails.setDescription(plainText + i);

            createContent(customDrone, contentDetails, PLAINTEXT);
        }

        // Open the created in the pre-condition documents
        assertTrue(WebDavUtil.getContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, plainText + 0, path + remotePath).contains(plainText), "Document isn't opened correctly");
        assertTrue(WebDavUtil.getContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, plainText+1, path + remotePath).isEmpty(), "Document isn't opened correctly");

        // Download both documents
        assertTrue(WebDavUtil.downloadContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, plainText + 0, path + remotePath), "Document isn't downloaded");
        assertTrue(WebDavUtil.downloadContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, plainText + 1, path + remotePath), "Document isn't downloaded");

        // Their content is displayed correctly
        String content = getTextFromDownloadDirectoryFile(plainText+0);
        String contentEmpty = getTextFromDownloadDirectoryFile(plainText+1);

        assertTrue(content.contains(plainText + 0), "Content isn't displayed correctly");
        assertTrue(contentEmpty.isEmpty(), "Content isn't displayed correctly");

        // Upload the created in the pre-condition document to the site's Document Library
        WebDavUtil.uploadContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, file, path + remotePath);
        assertTrue(WebDavUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName, path + remotePath), "File isn't uploaded");

        // Check the document was encrypted
        openDocumentLibrary(customDrone);
        String nodeRef = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName).getContentNodeRef();
        String binPath = CryptodocUtil.getBinPath(customDrone, fileName, nodeRef);

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
        RemoteUtil.checkForStrings(fileName, filepath, resultsPath);

        assertTrue(RemoteUtil.isFileEmpty(resultsPath), "File " + fileName + " wasn't encrypted");
        customDrone.navigateTo(url);

        // Check the document is available for usage in Share
        // download
        FileDirectoryInfo fileDirectoryInfo = docLibPage.getFileDirectoryInfo(fileName);
        fileDirectoryInfo.selectDownload();
        docLibPage.waitForFile(downloadDirectory + plainText);
        List<String> extractedChildFilesOrFolders = ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory);
        assertTrue(extractedChildFilesOrFolders.contains(plainText), "File isn't downloaded");

//        view in browser
        fileDirectoryInfo.selectViewInBrowser();
        String htmlSource = ((WebDroneImpl) customDrone).getDriver().getPageSource();
        assertTrue(htmlSource.contains(fileName), "Document isn't opened in a browser");
        customDrone.closeWindow();
        customDrone.switchToWindow(mainWindow);

        // edit inline
        String newDescription = getTestName() + " description";
        DocumentDetailsPage documentDetailsPage = docLibPage.selectFile(fileName);
        EditTextDocumentPage inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setDescription(newDescription);
        documentDetailsPage = inlineEditPage.saveWithValidation(contentDetails).render();
        documentDetailsPage.render();
        docLibPage = documentDetailsPage.getSiteNav().selectSiteDocumentLibrary().render();
        assertEquals(docLibPage.getFileDirectoryInfo(fileName).getDescription(), newDescription, "Document isn't edited");

        // edit offline
        fileDirectoryInfo = docLibPage.getFileDirectoryInfo(fileName);
        fileDirectoryInfo.selectEditOffline().render();
        openDocumentLibrary(customDrone);
        fileDirectoryInfo = docLibPage.getFileDirectoryInfo(fileName);
        assertEquals(fileDirectoryInfo.getContentInfo(), "This document is locked by you for offline editing.");

        // upload new version
        fileDirectoryInfo = docLibPage.getFileDirectoryInfo(fileName);
        String actualVersion = fileDirectoryInfo.getVersionInfo();

        UpdateFilePage updatePage = fileDirectoryInfo.selectUploadNewVersion().render(maxWaitTime);
        updatePage.selectMinorVersionChange();
        String fileNameNew = fileName + getRandomString(3) + ".txt";

        File newFileName = newFile(DATA_FOLDER + (fileNameNew), fileName);
        updatePage.uploadFile(newFileName.getCanonicalPath());
        SitePage sitePage = updatePage.submit().render();
        sitePage.render();
        FileUtils.forceDelete(newFileName);

        // verify version
        docLibPage = openDocumentLibrary(customDrone);
        fileDirectoryInfo = docLibPage.getFileDirectoryInfo(fileName);
        String currentVersion = fileDirectoryInfo.getVersionInfo();
        assertNotEquals(actualVersion, currentVersion, "Version isn't changed");

        // Verify the documents' previews and thumbnails
        fileDirectoryInfo = docLibPage.getFileDirectoryInfo(fileName);
        assertTrue(!fileDirectoryInfo.getPreViewUrl().isEmpty(), "Thumbnail isn't displayed");
        DocumentDetailsPage detailsPage = fileDirectoryInfo.selectThumbnail().render();
        assertTrue(detailsPage.isDocumentDetailsPage(), "Failed to open Document Details page");

        if (alfrescoVersion.getVersion() < 5.0)
        {
            assertTrue(detailsPage.isFlashPreviewDisplayed(), "Preview isn't correctly displayed on details page");
            assertTrue(WebDavUtil.getContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName, path + remotePath).equals(fileName), "The document isn't unencrypted");
        }
        else
        {
            assertTrue(detailsPage.getPreviewerClassName().equals("previewer PdfJs"), "Preview isn't correctly displayed on details page");
            assertTrue(detailsPage.getDocumentBody().contains(fileName), "The document isn't unencrypted");
        }

        openDocumentLibrary(customDrone);

        // Create any document using context menu in the site's Document Library
        contentDetails = new ContentDetails();
        contentDetails.setContent(plainText + 2);
        contentDetails.setName(plainText + 2);
        contentDetails.setDescription(plainText + 2);
        createContent(customDrone, contentDetails, PLAINTEXT);

        // Check the document was encrypted
        nodeRef = ShareUserSitePage.getFileDirectoryInfo(customDrone, plainText + 2).getContentNodeRef();
        binPath = CryptodocUtil.getBinPath(customDrone, plainText + 2, nodeRef);

        // On your server machine open your alf_data/contentstore directory
        // Open the a .bin file and verify its content
        setSshHost(node1Url);
        initConnection();
        alfHome = JmxUtils.getAlfrescoServerProperty("Alfresco:Name=SystemProperties", "alfresco.home").toString();
        alfHome.replace("/", SLASH);
        filepath = alfHome + "/alf_data/" + binPath + "bin";
        assertTrue(RemoteUtil.isFileExist(filepath), ".bin file isn't present at a contentstore");

        resultFile = "results.txt";
        resultsPath = alfHome + "/" + resultFile;
        RemoteUtil.checkForStrings(plainText + 2, filepath, resultsPath);

        assertTrue(RemoteUtil.isFileEmpty(resultsPath), "File " + plainText + 2 + " wasn't encrypted");
        customDrone.navigateTo(url);

        // Check the document is available for usage in Share
        // download
        fileDirectoryInfo = docLibPage.getFileDirectoryInfo(plainText + 2);
        fileDirectoryInfo.selectDownload();
        docLibPage.waitForFile(downloadDirectory + plainText + 2);
        extractedChildFilesOrFolders = ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory);
        assertTrue(extractedChildFilesOrFolders.contains(plainText + 2), "File isn't downloaded");

//        view in browser
        fileDirectoryInfo.selectViewInBrowser();
        htmlSource = ((WebDroneImpl) customDrone).getDriver().getPageSource();
        assertTrue(htmlSource.contains(plainText + 2), "Document isn't opened in a browser");
        customDrone.closeWindow();
        customDrone.switchToWindow(mainWindow);

        // edit inline
        newDescription = getTestName() + " description";
        documentDetailsPage = docLibPage.selectFile(plainText + 2);
        inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        contentDetails = new ContentDetails();
        contentDetails.setDescription(newDescription);
        documentDetailsPage = inlineEditPage.saveWithValidation(contentDetails).render();
        documentDetailsPage.render();
        docLibPage = documentDetailsPage.getSiteNav().selectSiteDocumentLibrary().render();
        assertEquals(docLibPage.getFileDirectoryInfo(plainText + 2).getDescription(), newDescription, "Document isn't edited");
        inlineEditPage = fileDirectoryInfo.selectInlineEdit().render();
        EditTextDocumentPage editTextDocumentPage = ((EditTextDocumentPage) inlineEditPage.getInlineEditDocumentPage(MimeType.TEXT)).render(maxWaitTime);

        contentDetails = new ContentDetails();
        newDescription = getTestName() + " description";
        contentDetails.setDescription(newDescription);
        docLibPage = editTextDocumentPage.saveWithValidation(contentDetails).render(maxWaitTime);

        assertEquals(docLibPage.getFileDirectoryInfo(plainText + 2).getDescription(), newDescription, "Document isn't edited");


        // edit offline
        fileDirectoryInfo = docLibPage.getFileDirectoryInfo(plainText + 2);
        fileDirectoryInfo.selectEditOffline().render();
        openDocumentLibrary(customDrone);
        fileDirectoryInfo = docLibPage.getFileDirectoryInfo(plainText + 2);
        assertEquals(fileDirectoryInfo.getContentInfo(), "This document is locked by you for offline editing.");
        docLibPage = openDocumentLibrary(customDrone);
        fileDirectoryInfo = docLibPage.getFileDirectoryInfo(plainText + 2);
        fileDirectoryInfo.selectCancelEditing();


        // Download both documents to your client machine
        String [] docs = {fileName, plainText + 2};
        for (String doc : docs)
        {
            assertTrue(WebDavUtil.downloadContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, doc, path + remotePath), doc + " isn't downloaded");

            // Their content is displayed correctly
            content = getTextFromDownloadDirectoryFile(doc);
            assertTrue(content.contains(doc), "Content isn't displayed correctly");
        }

        // Try to edit all of the documents via WebDav
        String [] items = {plainText + 0, plainText + 1, plainText + 2, fileName};
        for (String item : items)
        {
            assertTrue(WebDavUtil.editContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, item, path + remotePath), "Can't edit " + item);
            assertTrue(WebDavUtil.getContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, item, path + remotePath).equals(ADMIN_USERNAME), "Document isn't edited");
        }

        ShareUser.logout(customDrone);
        customDrone.closeWindow();
    }

}
