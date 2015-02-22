package org.alfresco.module.vti.handler.alfresco;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.module.vti.handler.MethodHandler;
import org.alfresco.module.vti.metadata.dic.DocumentStatus;
import org.alfresco.module.vti.web.AlfrescoServletInputStream;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.repo.version.VersionModel;
import org.alfresco.repo.webdav.WebDAV;
import org.alfresco.repo.webdav.WebDAVActivityPoster;
import org.alfresco.repo.webdav.WebDAVHelper;
import org.alfresco.repo.webdav.WebDAVLockService;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.cmr.version.VersionType;
import org.alfresco.service.cmr.webdav.WebDavService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ApplicationContextHelper;
import org.alfresco.util.TempFileProvider;
import org.apache.chemistry.opencmis.server.shared.ThresholdOutputStreamFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@RunWith(MockitoJUnitRunner.class)
public class AlfrescoMethodHandlerTest
{
    // The class under test.
    private MethodHandler handler;
    private static ApplicationContext ctx;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private SiteService siteService;
    private FileFolderService fileFolderService;
    private NodeService nodeService;
    private String shortSiteId;
    private NodeRef docLib;
    private @Mock WebDAVActivityPoster mockActivityPoster;
    private @Mock WebDavService mockDavService;

    private ContentService contentService;
    private TransactionService transactionService;
    private MimetypeService mimetypeService;
    private @Mock VtiPathHelper pathHelper;
    private @Mock FileInfo fileInfo;
    private @Mock CheckOutCheckInService checkOutCheckInService;
    private @Mock VtiDocumentHelper documentHelper;
    private @Mock WebDAVLockService webDAVLockService;
    private @Mock VersionService versionService;
    private @Mock HttpServletRequest servletRequest;
    private @Mock HttpServletResponse servletResponse;
    private @Mock WebDAVHelper davHelper;

    /* handler for transaction tests */
    private AlfrescoMethodHandler alfrescoMethodHandler;
    private StoreRef storeRef;
    private NodeRef rootNodeRef;
    private NodeRef resourceNodeRef;
    UserTransaction txn;
    private static final String DAV_EXT_LOCK_TIMEOUT = "X-MSDAVEXTLockTimeout";
    private int maxRetries = 40;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        ctx = ApplicationContextHelper.getApplicationContext();
    }

    @Before
    public void setUp() throws Exception
    {
        File tempDirectory = TempFileProvider.getTempDir("Alfresco-Vti");
        ThresholdOutputStreamFactory streamFactory = ThresholdOutputStreamFactory.newInstance(tempDirectory, 4194304, 4294967296L, false);

        // The class under test
        //handler = (MethodHandler) ctx.getBean("vtiHandler");
        fileFolderService = ctx.getBean("FileFolderService", FileFolderService.class);
        siteService = ctx.getBean("SiteService", SiteService.class);
        nodeService = ctx.getBean("NodeService", NodeService.class);
        response = new MockHttpServletResponse();
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());

        alfrescoMethodHandler = new AlfrescoMethodHandler();
        alfrescoMethodHandler.setStreamFactory(streamFactory);
        alfrescoMethodHandler.setPathHelper(pathHelper);
        alfrescoMethodHandler.setCheckOutCheckInService(checkOutCheckInService);
        alfrescoMethodHandler.setDocumentHelper(documentHelper);
        alfrescoMethodHandler.setWebDAVLockService(webDAVLockService);
        alfrescoMethodHandler.setVersionService(versionService);

        transactionService = ctx.getBean("transactionService", TransactionService.class);
        nodeService = ctx.getBean("NodeService", NodeService.class);
        fileFolderService = ctx.getBean("FileFolderService", FileFolderService.class);
        contentService = ctx.getBean("ContentService", ContentService.class);
        mimetypeService = ctx.getBean("MimetypeService", MimetypeService.class);

        alfrescoMethodHandler.setTransactionService(transactionService);
        alfrescoMethodHandler.setNodeService(nodeService);
        alfrescoMethodHandler.setFileFolderService(fileFolderService);
        alfrescoMethodHandler.setContentService(contentService);
        alfrescoMethodHandler.setMimetypeService(mimetypeService);
        alfrescoMethodHandler.setActivityPoster(mockActivityPoster);
        alfrescoMethodHandler.setDavHelper(davHelper);

        // Create a site for use in these tests
        shortSiteId = "SharepointTest-" + UUID.randomUUID();        
        if (!siteService.hasSite(shortSiteId))
        {
            siteService.createSite("sitePreset1", shortSiteId, "Test site", "Sharepoint tests", SiteVisibility.PUBLIC);
            docLib = siteService.createContainer(shortSiteId, SiteService.DOCUMENT_LIBRARY, ContentModel.TYPE_FOLDER, null);
        }

        // Create the store and get the root node reference
        storeRef = this.nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_" + System.currentTimeMillis());
        rootNodeRef = this.nodeService.getRootNode(storeRef);

        resourceNodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, "RESOURCENODEREF");
    }

    @Test
    public void canPutFileWithResourceTag() throws Exception
    {
        when(mockDavService.activitiesEnabled()).thenReturn(true);
        alfrescoMethodHandler.setDavService(mockDavService);
        String fileName = "test_file.txt";
        String path = shortSiteId + "/documentLibrary/" + fileName;
        // VtiIfHeaderAction PUT handler expects the file to have already been created (in most cases)
        FileInfo createdFile = fileFolderService.create(docLib, fileName, ContentModel.TYPE_CONTENT);
        when(pathHelper.getAlfrescoContext()).thenReturn("/alfresco");
        when(pathHelper.getRootNodeRef()).thenReturn(rootNodeRef);
        when(pathHelper.resolvePathFileInfo(path)).thenReturn(createdFile);

        request = new MockHttpServletRequest("PUT", "/alfresco/" + path);
        String fileContent = "This is the test file's content."; 
        request.setContent(fileContent.getBytes());
        request.addHeader("If", "(<rt:792589C1-2E8F-410E-BC91-4EF42DA88D3C@00862604462>)");
        when(davHelper.determineSiteId(rootNodeRef, path)).thenReturn(shortSiteId);
        when(davHelper.determineTenantDomain()).thenReturn(TenantService.DEFAULT_DOMAIN);
        when(davHelper.getNodeForPath(rootNodeRef, path)).thenReturn(createdFile);

        alfrescoMethodHandler.putResource(request, response);
        
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        String retContent = fileFolderService.getReader(createdFile.getNodeRef()).getContentString();
        assertEquals(fileContent, retContent);
    }
    
    @Test
    public void putFileUpdateResultsInActivityPost() throws Exception
    {
        // Inject a mock WebDavService that always states activity posting is enabled.
        when(mockDavService.activitiesEnabled()).thenReturn(true);
        alfrescoMethodHandler.setDavService(mockDavService);
        when(pathHelper.getAlfrescoContext()).thenReturn("/alfresco");
        when(pathHelper.getRootNodeRef()).thenReturn(rootNodeRef);

        String fileName = "test_file.txt";
        String path = shortSiteId + "/documentLibrary/" + fileName;
        // VtiIfHeaderAction PUT handler expects the file to have already been created (in most cases)
        FileInfo createdFile = fileFolderService.create(docLib, fileName, ContentModel.TYPE_CONTENT);
        when(pathHelper.resolvePathFileInfo(path)).thenReturn(createdFile);

        request = new MockHttpServletRequest("PUT", "/alfresco/" + path);
        String fileContent = "This is the test file's content."; 
        request.setContent(fileContent.getBytes());
        request.addHeader("If", "(<rt:792589C1-2E8F-410E-BC91-4EF42DA88D3C@00862604462>)");
        when(davHelper.determineSiteId(rootNodeRef, path)).thenReturn(shortSiteId);
        when(davHelper.determineTenantDomain()).thenReturn(TenantService.DEFAULT_DOMAIN);
        when(davHelper.getNodeForPath(rootNodeRef, path)).thenReturn(createdFile);

        // PUT the file
        alfrescoMethodHandler.putResource(request, response);
        
        // Check the activity was posted
        verify(mockActivityPoster).postFileFolderUpdated(shortSiteId, TenantService.DEFAULT_DOMAIN, createdFile);
        
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        String retContent = fileFolderService.getReader(createdFile.getNodeRef()).getContentString();
        assertEquals(fileContent, retContent);
    }
    
    @Test
    public void putNewFileResultsInActivityPost() throws Exception
    {
        // Inject a mock WebDavService that always states activity posting is enabled.
        when(mockDavService.activitiesEnabled()).thenReturn(true);
        alfrescoMethodHandler.setDavService(mockDavService);
        when(pathHelper.getAlfrescoContext()).thenReturn("/alfresco");
        when(pathHelper.getRootNodeRef()).thenReturn(rootNodeRef);

        String fileName = "test_file.txt";
        String path = shortSiteId + "/documentLibrary/" + fileName;
        // VtiIfHeaderAction PUT handler expects the file to have already been created (in most cases)
        FileInfo createdFile = fileFolderService.create(docLib, fileName, ContentModel.TYPE_CONTENT);
        when(pathHelper.resolvePathFileInfo(path)).thenReturn(createdFile);

        // Flag as a new file that hasn't yet had content uploaded
        nodeService.addAspect(createdFile.getNodeRef(), ContentModel.ASPECT_WEBDAV_NO_CONTENT,
                    new HashMap<QName, Serializable>());
        
        request = new MockHttpServletRequest("PUT", "/alfresco/" + path);
        String fileContent = "This is the test file's content."; 
        request.setContent(fileContent.getBytes());
        request.addHeader("If", "(<rt:792589C1-2E8F-410E-BC91-4EF42DA88D3C@00862604462>)");
        when(davHelper.determineSiteId(rootNodeRef, shortSiteId + "/documentLibrary/" + fileName)).thenReturn(shortSiteId);
        when(davHelper.determineTenantDomain()).thenReturn(TenantService.DEFAULT_DOMAIN);
        when(davHelper.getNodeForPath(rootNodeRef, path)).thenReturn(createdFile);

        // PUT the file
        alfrescoMethodHandler.putResource(request, response);
        
        // Check the activity was posted
        verify(mockActivityPoster).postFileFolderAdded(shortSiteId, TenantService.DEFAULT_DOMAIN, null, createdFile);
        
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        String retContent = fileFolderService.getReader(createdFile.getNodeRef()).getContentString();
        assertEquals(fileContent, retContent);
    }

    /**
     * Test for MNT-12757.
     * Simulate fail in transaction during writing in transaction
     */
    @Test
    public void testPutResourceTransactionFail() throws Exception
    {
        txn = transactionService.getUserTransaction();
        txn.begin();
        AuthenticationUtil.setAdminUserAsFullyAuthenticatedUser();

        String name = "test.docx";
        Map<QName, Serializable> props = new HashMap<QName, Serializable>();
        props.put(ContentModel.PROP_NAME, name);

        NodeRef folderNodeRef = nodeService.createNode(
                rootNodeRef,
                ContentModel.ASSOC_CHILDREN,
                ContentModel.ASSOC_CHILDREN,
                ContentModel.TYPE_FOLDER).getChildRef();

        // Create node
        NodeRef node = nodeService
                .createNode(
                        folderNodeRef,
                        ContentModel.ASSOC_CONTAINS,
                        QName.createQName(name),
                        ContentModel.TYPE_CONTENT,
                        props)
                .getChildRef();

        ContentWriter writer = contentService.getWriter(node, ContentModel.PROP_CONTENT, true);
        writer.putContent(new ByteArrayInputStream("Test".getBytes()));

        props = nodeService.getProperties(node);
        Date date = (Date) props.get(ContentModel.PROP_MODIFIED);

        txn.commit();

        when(servletRequest.getHeader(DAV_EXT_LOCK_TIMEOUT)).thenReturn("1");
        when(servletRequest.getRequestURI()).thenReturn("/alfresco/test/" + name);
        when(pathHelper.getAlfrescoContext()).thenReturn("/alfresco");
        when(pathHelper.resolvePathFileInfo("test/test.docx")).thenReturn(fileInfo);
        when(fileInfo.getNodeRef()).thenReturn(node);
        when(servletRequest.getHeader(WebDAV.HEADER_IF))
                .thenReturn("[\"{" + node.getId() + "}," + VtiUtils.convertDateToVersion(date) + "\"]");

        when(servletRequest.getInputStream())
                .thenReturn(new AlfrescoServletInputStream(new ByteArrayInputStream("Test test".getBytes())));

        // Fail at the end of method with exception
        when(versionService.createVersion(
                node,
                Collections.<String, Serializable>singletonMap(VersionModel.PROP_VERSION_TYPE, VersionType.MAJOR)))
                .thenThrow(new ConcurrencyFailureException("TestMessage"));

        alfrescoMethodHandler.putResource(servletRequest, servletResponse);
        // The response should be set to 409 after all of the retries
        verify(servletResponse, times(1)).setStatus(HttpServletResponse.SC_CONFLICT);
        // The request was read 1 time
        verify(servletRequest, times(1)).getInputStream();
        // the data was written without errors 40 times due to retrying
        verify(versionService, times(maxRetries)).createVersion(
                node,
                Collections.<String, Serializable> singletonMap(VersionModel.PROP_VERSION_TYPE, VersionType.MAJOR));
    }

    /**
     * Test for MNT-12757
     */
    @Test
    public void testUncheckOutDocumentTransactionFail()
    {
        when(pathHelper.resolvePathFileInfo("test/test.docx")).thenReturn(fileInfo);
        when(fileInfo.isFolder()).thenReturn(false);
        when(fileInfo.getNodeRef()).thenReturn(resourceNodeRef);
        when(documentHelper.getDocumentStatus(resourceNodeRef)).thenReturn(DocumentStatus.LONG_CHECKOUT_OWNER);
        when(checkOutCheckInService.getWorkingCopy(resourceNodeRef)).thenThrow(new ConcurrencyFailureException("TestMessage"));
        try
        {
            alfrescoMethodHandler.uncheckOutDocument("test", "test.docx", false, null, false, false);
            fail("An exception should be thrown.");
        }
        catch(RuntimeException re)
        {
            // expected
        }
        // Check that the call was retried
        verify(checkOutCheckInService, times(maxRetries)).getWorkingCopy(resourceNodeRef);

    }
}
