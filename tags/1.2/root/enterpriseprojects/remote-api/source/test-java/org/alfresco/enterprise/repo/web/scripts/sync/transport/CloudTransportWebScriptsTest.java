/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.web.scripts.sync.transport;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.alfresco.enterprise.repo.sync.ConflictResponse;
import org.alfresco.enterprise.repo.sync.ConflictResponseImpl;
import org.alfresco.enterprise.repo.sync.NoSuchSyncSetDefinitionException;
import org.alfresco.enterprise.repo.sync.SyncAdminService;
import org.alfresco.enterprise.repo.sync.SyncAdminServiceImpl;
import org.alfresco.enterprise.repo.sync.SyncNodeException;
import org.alfresco.enterprise.repo.sync.SyncNodeException.SyncNodeExceptionType;
import org.alfresco.enterprise.repo.sync.SyncService;
import org.alfresco.enterprise.repo.sync.SyncSetDefinition;
import org.alfresco.enterprise.repo.sync.audit.SyncAuditService;
import org.alfresco.enterprise.repo.sync.audit.SyncChangeEvent;
import org.alfresco.enterprise.repo.sync.audit.SyncChangeEventImpl;
import org.alfresco.enterprise.repo.sync.audit.SyncEventHandler;
import org.alfresco.enterprise.repo.sync.connector.CloudConnectorService;
import org.alfresco.enterprise.repo.sync.connector.impl.CloudConnectorServiceImpl;
import org.alfresco.enterprise.repo.sync.transport.AuditToken;
import org.alfresco.enterprise.repo.sync.transport.CloudSyncContent;
import org.alfresco.enterprise.repo.sync.transport.CloudSyncDeclinedException;
import org.alfresco.enterprise.repo.sync.transport.CloudSyncMemberNodeTransport;
import org.alfresco.enterprise.repo.sync.transport.CloudSyncOnCloudService;
import org.alfresco.enterprise.repo.sync.transport.CloudSyncSetDefinitionTransport;
import org.alfresco.enterprise.repo.sync.transport.SyncNodeChangesInfo;
import org.alfresco.enterprise.repo.sync.transport.impl.AuditTokenImpl;
import org.alfresco.enterprise.repo.sync.transport.impl.CloudSyncContentNodeImpl;
import org.alfresco.enterprise.repo.sync.transport.impl.CloudSyncMemberNodeTransportImpl;
import org.alfresco.enterprise.repo.sync.transport.impl.CloudSyncSetDefinitionTransportImpl;
import org.alfresco.enterprise.repo.sync.transport.impl.SyncNodeChangesInfoImpl;
import org.alfresco.enterprise.repo.sync.transport.impl.TestControllableCloudSyncOnCloudService;
import org.alfresco.enterprise.repo.web.scripts.BaseEnterpriseWebScriptTest;
import org.alfresco.enterprise.repo.web.scripts.sync.audit.SyncSetChangesGet;
import org.alfresco.enterprise.repo.web.scripts.sync.audit.SyncSetManifestGet;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.ContentLimitViolationException;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.mode.ServerMode;
import org.alfresco.repo.mode.ServerModeProvider;
import org.alfresco.repo.node.archive.NodeArchiveService;
import org.alfresco.repo.remoteconnector.LocalWebScriptConnectorServiceImpl;
import org.alfresco.repo.remoteconnector.RemoteConnectorRequestImpl;
import org.alfresco.repo.remotecredentials.PasswordCredentialsInfoImpl;
import org.alfresco.repo.remoteticket.RemoteAlfrescoTicketServiceImpl;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.site.SiteDoesNotExistException;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.remoteconnector.RemoteConnectorClientException;
import org.alfresco.service.cmr.remoteconnector.RemoteConnectorRequest;
import org.alfresco.service.cmr.remoteticket.RemoteAlfrescoTicketService;
import org.alfresco.service.cmr.remoteticket.RemoteSystemUnavailableException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.MLText;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.service.cmr.usage.ContentQuotaException;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;
import org.alfresco.util.PropertyMap;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.TestWebScriptServer.Request;
import org.springframework.extensions.webscripts.TestWebScriptServer.Response;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * Tests for the various Cloud Transport WebScripts, which push
 *  and pull data between repositories.
 * 
 * These tests are done with the loop-back local remote connector,
 *  and generally performed with dummy services at the far end.
 *  
 * These tests cover both {@link CloudSyncMemberNodeTransport} and
 *  {@link CloudSyncSetDefinitionTransport}.
 * 
 * @author Nick Burch
 * @since 4.1
 */
public class CloudTransportWebScriptsTest extends BaseEnterpriseWebScriptTest
{
    private static final String URL_PULL = "/enterprise/sync/pull";
    private static final String URL_PUSH = "/enterprise/sync/push";
    private static final String URL_DELETE = "/enterprise/sync/delete";
    
    private MutableAuthenticationService authenticationService;
    private RetryingTransactionHelper retryingTransactionHelper;
    private ContentService contentService;
    private PersonService personService;
    private SiteService siteService;
    private NodeService nodeService;
    private NamespaceService namespaceService;
    private NodeArchiveService nodeArchiveService;
    
    private SyncAdminService syncAdminService;
    private CloudConnectorService cloudConnectorService;
    private RemoteAlfrescoTicketService remoteAlfrescoTicketService;
    
    private CloudSyncOnCloudService realCloudSyncOnCloudService;
    private TestControllableCloudSyncOnCloudService testCloudSyncOnCloudService;
    
    private CloudSyncMemberNodeTransport realCloudSyncNodeTransportService;
    private TestCloudSyncMemberNodeTransport testCloudSyncNodeTransportService;
    private CloudSyncSetDefinitionTransport realCloudSyncSetDefinitionTransport;
    
    private TestSyncService testSyncService;
    private SyncAuditService testSyncAuditService;
    private SyncAuditService realSyncAuditService;
    
    private AbstractCloudSyncPostWebScript webscriptPushPost;
    private AbstractCloudSyncPostWebScript webscriptDeletePost;
    private CloudSyncPullGet webscriptPullGet;
    private CloudSyncConfirmPost webscriptConfirmPost;
    private CloudSyncConflictPost webscriptConflictPost;
    private SyncSetChangesGet syncChangesGet;
    private SyncSetManifestGet syncManifestGet;

    private static final String USER_ONE = "UserOneSecondToo";
    private static final String USER_TWO = "UserTwoSecondToo";
    private static final String USER_THREE = "UserThreeStill";
    private static final String PASSWORD = "passwordTEST";
    
    private static final String SITE_ON_PREMISE_NAME = "TestOnPremiseSite";
    private static final String SITE_CLOUD_NAME = "TestCloudSite";
    private SiteInfo SITE_ON_PREMISE;
    private SiteInfo SITE_CLOUD;
    
    private List<SyncSetDefinition> syncSets = new ArrayList<SyncSetDefinition>();
    
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        
        AbstractRefreshableApplicationContext ctx = (AbstractRefreshableApplicationContext)getServer().getApplicationContext();
        this.retryingTransactionHelper = (RetryingTransactionHelper)ctx.getBean("retryingTransactionHelper");
        this.authenticationService = (MutableAuthenticationService)ctx.getBean("AuthenticationService");
        this.namespaceService = (NamespaceService)ctx.getBean("NamespaceService");
        this.contentService = (ContentService)ctx.getBean("ContentService");
        this.personService = (PersonService)ctx.getBean("PersonService");
        this.siteService = (SiteService)ctx.getBean("SiteService");
        this.nodeService = (NodeService)ctx.getBean("NodeService");
        this.nodeArchiveService = (NodeArchiveService)getServer().getApplicationContext().getBean("nodeArchiveService");
        
        this.syncAdminService = (SyncAdminService)ctx.getBean("SyncAdminService");
        SyncAdminServiceImpl syncAdminServiceImpl = (SyncAdminServiceImpl)ctx.getBean("syncAdminService");
        this.cloudConnectorService = (CloudConnectorService)ctx.getBean("cloudConnectorService");
        this.remoteAlfrescoTicketService = (RemoteAlfrescoTicketService)ctx.getBean("remoteAlfrescoTicketService");
        
        // Get the real beans
        this.realCloudSyncOnCloudService = (CloudSyncOnCloudService)ctx.getBean("CloudSyncOnCloudService");
        this.realCloudSyncNodeTransportService = (CloudSyncMemberNodeTransport)ctx.getBean("CloudSyncMemberNodeTransport");
        this.realCloudSyncSetDefinitionTransport = (CloudSyncSetDefinitionTransport)ctx.getBean("CloudSyncSetDefinitionTransport");
        
        // And the test ones
        this.testSyncService = new TestSyncService();
        this.testCloudSyncOnCloudService = new TestControllableCloudSyncOnCloudService(true,true,0);        
        this.testCloudSyncNodeTransportService = new TestCloudSyncMemberNodeTransport(realCloudSyncNodeTransportService);
        
        // Mock the sync audit service
        this.testSyncAuditService = mock(SyncAuditService.class);
        this.realSyncAuditService = (SyncAuditService)ctx.getBean("SyncAuditService");
        

        // Do the setup as admin
        AuthenticationUtil.setAdminUserAsFullyAuthenticatedUser();
        
        ServerModeProvider fakeServerModeProvider = new ServerModeProvider()
        {

			@Override
			public ServerMode getServerMode() {
				return ServerMode.PRODUCTION;
			}
        	
        };
        
        if(syncAdminServiceImpl instanceof SyncAdminServiceImpl)
        {
        	syncAdminServiceImpl.setServerModeProvider(fakeServerModeProvider);
        }
        
        
        // Wire up the loop-back connector
        ((RemoteAlfrescoTicketServiceImpl)remoteAlfrescoTicketService).setRemoteConnectorService(
                new LocalWebScriptConnectorServiceImpl(this));
        ((CloudConnectorServiceImpl)cloudConnectorService).setRemoteConnectorService(
                new LocalWebScriptConnectorServiceImpl(this));
        
        // Configure the cloud connector for loopback
        ((CloudConnectorServiceImpl)cloudConnectorService).setCloudBaseUrl(
                LocalWebScriptConnectorServiceImpl.LOCAL_SERVICE_URL);
        
        // Have the sync admin service not try to talk to the remote end for SSDs
        CloudSyncSetDefinitionTransport testSSDTransport = mock(CloudSyncSetDefinitionTransport.class);
        ((SyncAdminServiceImpl)ctx.getBean("syncAdminService")).setCloudSyncSetDefinitionTransport(testSSDTransport);
        
        // Get the SSMN transport webscripts
        final String ssmnWebscriptIdBase = "webscript.org.alfresco.cloud.sync.cloud-sync";
        webscriptPushPost = (AbstractCloudSyncPostWebScript)ctx.getBean(ssmnWebscriptIdBase+"-push.post");
        webscriptDeletePost = (AbstractCloudSyncPostWebScript)ctx.getBean(ssmnWebscriptIdBase+"-delete.post");
        webscriptPullGet = (CloudSyncPullGet)ctx.getBean(ssmnWebscriptIdBase+"-pull.get");
        webscriptConfirmPost = (CloudSyncConfirmPost)ctx.getBean(ssmnWebscriptIdBase+"-confirm.post");
        webscriptConflictPost = (CloudSyncConflictPost)ctx.getBean(ssmnWebscriptIdBase+"-conflict.post");
        
        // Get the SSD transport webscripts
        final String ssdWebscriptIdBase = "webscript.org.alfresco.enterprise.repository.sync.audit.";
        syncChangesGet = (SyncSetChangesGet)ctx.getBean(ssdWebscriptIdBase+"syncsetchanges.get");
        syncManifestGet = (SyncSetManifestGet)ctx.getBean(ssdWebscriptIdBase+"syncsetmanifest.get");
        
        // Re-wire them to use the test versions
        webscriptPullGet.setCloudSyncMemberNodeTransport(testCloudSyncNodeTransportService);
        webscriptPullGet.setCloudSyncOnCloudService(testCloudSyncOnCloudService);
        webscriptPushPost.setCloudSyncMemberNodeTransport(testCloudSyncNodeTransportService);
        webscriptPushPost.setCloudSyncOnCloudService(testCloudSyncOnCloudService);
        webscriptDeletePost.setCloudSyncMemberNodeTransport(testCloudSyncNodeTransportService);
        webscriptDeletePost.setCloudSyncOnCloudService(testCloudSyncOnCloudService);
        webscriptPushPost.setDeleteTempFiles(false);
        webscriptDeletePost.setDeleteTempFiles(false);
        webscriptPullGet.setSyncService(testSyncService);
        webscriptConfirmPost.setSyncAuditService(testSyncAuditService);
        webscriptConflictPost.setSyncService(testSyncService);
        syncChangesGet.setSyncAuditService(testSyncAuditService);
        syncManifestGet.setSyncAuditService(testSyncAuditService);
        
        
        // Create a site for each one, which will hold our sync data
        SITE_ON_PREMISE = createSite(SITE_ON_PREMISE_NAME); 
        SITE_CLOUD = createSite(SITE_CLOUD_NAME);
        
        // Create users
        createUser(USER_ONE);
        createUser(USER_TWO);
        createUser(USER_THREE);

        // Do tests as first user
        AuthenticationUtil.setFullyAuthenticatedUser(USER_ONE);
    }
    
    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        
        // Admin user required to delete users and sites
        AuthenticationUtil.setAdminUserAsFullyAuthenticatedUser();
        
        // Zap the sites, and their contents
        SiteInfo siteInfo1 = siteService.getSite(SITE_ON_PREMISE.getShortName());
        SiteInfo siteInfo2 = siteService.getSite(SITE_CLOUD.getShortName());
        if (siteInfo1 != null)
        {
            siteService.deleteSite(SITE_ON_PREMISE.getShortName());
            nodeArchiveService.purgeArchivedNode(nodeArchiveService.getArchivedNode(siteInfo1.getNodeRef()));
        }
        if (siteInfo2 != null)
        {
            siteService.deleteSite(SITE_CLOUD.getShortName());
            nodeArchiveService.purgeArchivedNode(nodeArchiveService.getArchivedNode(siteInfo2.getNodeRef()));
        }
        

        
        // Zap the sync set(s)
        for (SyncSetDefinition ssd : syncSets)
        {
            syncAdminService.deleteSourceSyncSet(ssd.getId());
        }
        
        // Delete users, and their credentials, and their tickets
        for (String user : new String[] {USER_ONE, USER_TWO, USER_THREE})
        {
            // Delete credentials, as them
            AuthenticationUtil.setFullyAuthenticatedUser(user);
            if(personService.personExists(user))
            {
                cloudConnectorService.deleteCloudCredentials();
            }
            
            // Delete the user, as admin
            AuthenticationUtil.setAdminUserAsFullyAuthenticatedUser();
            if(personService.personExists(user))
            {
               personService.deletePerson(user);
            }
            if(this.authenticationService.authenticationExists(user))
            {
               this.authenticationService.deleteAuthentication(user);
            }
        }
        
        // Tidy up the webscipts so they go back to how they used to be
        syncChangesGet.setSyncAuditService(realSyncAuditService);
        syncManifestGet.setSyncAuditService(realSyncAuditService);
    }
    
    private SiteInfo createSite(final String shortName)
    {
        SiteInfo oldSiteInfo = siteService.getSite(shortName);
        if (oldSiteInfo != null)
        {
            retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
            {
                @Override
                public Void execute() throws Throwable
                  {
                      // Tidy up after failed earlier run
                      siteService.deleteSite(shortName);
                    return null;
                  }
            }, false, true);
            // Tidy up after failed earlier run
            nodeArchiveService.purgeArchivedNode(nodeArchiveService.getArchivedNode(oldSiteInfo.getNodeRef()));
        }
        return retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<SiteInfo>()
        {
            @Override
            public SiteInfo execute() throws Throwable
            {
                  // Do the create
                  return siteService.createSite("Testing", shortName, shortName, null, SiteVisibility.PUBLIC);
            }
        }, false, true);
    }
    
    private void createUser(final String userName)
    {
        // Make sure a new user is created every time
        // This ensures there are no credentials for them already existing
        //  which might confuse things later in the test
        if(this.personService.personExists(userName))
        {
           this.personService.deletePerson(userName);
        }
        if(this.authenticationService.authenticationExists(userName))
        {
           this.authenticationService.deleteAuthentication(userName);
        }
        
        
        // Create a fresh user
        authenticationService.createAuthentication(userName, PASSWORD.toCharArray());

        // create person properties
        PropertyMap personProps = new PropertyMap();
        personProps.put(ContentModel.PROP_USERNAME, userName);
        personProps.put(ContentModel.PROP_FIRSTNAME, "First");
        personProps.put(ContentModel.PROP_LASTNAME, "Last");
        personProps.put(ContentModel.PROP_EMAIL, "FirstName123.LastName123@email.com");
        personProps.put(ContentModel.PROP_JOBTITLE, "JobTitle123");
        personProps.put(ContentModel.PROP_JOBTITLE, "Organisation123");

        // create person node for user
        personService.createPerson(personProps);

        // For now, set everyone as contributors to both sites
        siteService.setMembership(SITE_ON_PREMISE.getShortName(), userName, SiteModel.SITE_CONTRIBUTOR);
        siteService.setMembership(SITE_CLOUD.getShortName(), userName, SiteModel.SITE_CONTRIBUTOR);
    }
    
    private NodeRef createTestNode(final SiteInfo site, final String name, final String title, final String contents)
    {
        return createTestNode(site.getNodeRef(), name, title, contents);
    }
    private NodeRef createTestNode(final NodeRef parentNodeRef, final String name, final String title, final String contents)
    {
        return retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<NodeRef>() {
                @Override
                public NodeRef execute() throws Throwable
                {
                    // Basic properties
                    Map<QName,Serializable> props = new HashMap<QName, Serializable>();
                    props.put(ContentModel.PROP_NAME, name);
                    props.put(ContentModel.PROP_TITLE, title);

                    // Create
                    NodeRef node = nodeService.createNode(
                            parentNodeRef, ContentModel.ASSOC_CONTAINS,
                            QName.createQName(name), ContentModel.TYPE_CONTENT, props).getChildRef();
                    
                    // Contents
                    ContentWriter writer = contentService.getWriter(node, ContentModel.PROP_CONTENT, true);
                    writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
                    writer.setEncoding("UTF-8");
                    writer.putContent(contents);
                    
                    // Done
                    return node;
                }
            }, false, false
        );
    }
    private NodeRef createTestFolder(final SiteInfo site, final String name, final String title)
    {
        return retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<NodeRef>() {
                @Override
                public NodeRef execute() throws Throwable
                {
                    // Basic properties
                    Map<QName,Serializable> props = new HashMap<QName, Serializable>();
                    props.put(ContentModel.PROP_NAME, name);
                    props.put(ContentModel.PROP_TITLE, title);

                    // Create
                    NodeRef node = nodeService.createNode(
                            site.getNodeRef(), ContentModel.ASSOC_CONTAINS,
                            QName.createQName(name), ContentModel.TYPE_FOLDER, props).getChildRef();
                    
                    // Done
                    return node;
                }
            }, false, false
        );
    }
    
    private JSONObject asJSON(Response response) throws Exception
    {
        String json = response.getContentAsString();
        JSONParser p = new JSONParser();
        Object o = p.parse(json);
        
        if (o instanceof JSONObject)
        {
            return (JSONObject)o; 
        }
        throw new IllegalArgumentException("Expected JSONObject, got " + o + " from " + json);
    }
    
    private CloudConnectorService buildMockCloudConnectorService(CloudConnectorService initial)
    {
        CloudConnectorService mockConnectorService = initial;
        if (mockConnectorService == null) mockConnectorService = mock(CloudConnectorService.class);
        
        when(mockConnectorService.getCloudCredentials()).thenReturn(new PasswordCredentialsInfoImpl());
        when(mockConnectorService.buildCloudRequest(any(String.class), any(String.class), any(String.class))).
            thenReturn(new RemoteConnectorRequestImpl("ignored", "POST"));

        return mockConnectorService;
    }

    // ---------------------------------------------------------------------------------------------
    
    /**
     * Check that when invalid requests are sent to the webscripts,
     *  they give helpful-ish answers
     */
    public void testInvalidRequests() throws Exception
    {
        Request req;
        
        // Wrong request mimetype
        req = new Request("POST", URL_PUSH);
        req.setType("text/plain");
        sendRequest(req, Status.STATUS_BAD_REQUEST);
        
        req = new Request("POST", URL_DELETE);
        req.setType("text/plain");
        sendRequest(req, Status.STATUS_BAD_REQUEST);
        

        // Empty request
        req = new Request("POST", URL_PUSH);
        req.setType(CloudSyncMemberNodeTransportImpl.MULTIPART_MIXED_TYPE);
        req.setBody(new byte[0]);
        sendRequest(req, Status.STATUS_BAD_REQUEST);
        
        req = new Request("POST", URL_DELETE);
        req.setType(CloudSyncMemberNodeTransportImpl.MULTIPART_MIXED_TYPE);
        req.setBody(new byte[0]);
        sendRequest(req, Status.STATUS_BAD_REQUEST);

        
        // Truncated request
        String boundary = "---testing";
        byte[] trunc = (boundary + "\r\nContent-Type: text/plain\r\n\r\nThings").getBytes("utf-8");
        
        req = new Request("POST", URL_PUSH);
        req.setType(CloudSyncMemberNodeTransportImpl.MULTIPART_MIXED_TYPE + "; boundary=\"" + boundary + "\"");
        req.setBody(trunc);
        sendRequest(req, Status.STATUS_BAD_REQUEST);
        
        req = new Request("POST", URL_DELETE);
        req.setType(CloudSyncMemberNodeTransportImpl.MULTIPART_MIXED_TYPE + "; boundary=\"" + boundary + "\"");
        req.setBody(trunc);
        sendRequest(req, Status.STATUS_BAD_REQUEST);
    }
    
    /**
     * Check that a basic push operations (create, update, un-sync, delete)
     *  all result in something being sent. Details aren't checked
     */
    public void testBasicPushOperations() throws Exception
    {
        AuthenticationUtil.setFullyAuthenticatedUser(USER_ONE);

        // Create a local node
        NodeRef sourceNode = createTestNode(SITE_ON_PREMISE, "testNode", "Test Node", "Stuff!!!");
        
        // Dummy mark as a sync set, but don't worry about a real one for now
        String syncSet = "testSyncSet";
        
        // Wrap it manually, with a minimal set of data
        SyncNodeChangesInfoImpl newSync = new SyncNodeChangesInfoImpl(
                sourceNode, null, syncSet, nodeService.getType(sourceNode));
        newSync.setLocalParentNodeRef(SITE_ON_PREMISE.getNodeRef());
        newSync.setRemoteParentNodeRef(SITE_CLOUD.getNodeRef());
        
        
        // We can't sync if we don't have any credentials
        assertEquals(null, cloudConnectorService.getCloudCredentials());
        try
        {
            realCloudSyncNodeTransportService.pushSyncInitial(newSync, null);
            fail("Shouldn't be able to sync without credentials!");
        }
        catch(AuthenticationException e) {}
        
        // Set some credentials
        cloudConnectorService.storeCloudCredentials(USER_THREE, PASSWORD);
        
        
        // Now call to have it initially synced
        NodeRef remote = realCloudSyncNodeTransportService.pushSyncInitial(newSync, null);
        
        // Should have got a magic noderef back for it 
        assertEquals(TestCloudSyncMemberNodeTransport.DUMMY_NEW_NODE_NODEREF, remote);
        
        // Should have been correctly registered
        assertEquals(1, testCloudSyncNodeTransportService.toApply.size());
        assertEquals(0, testCloudSyncNodeTransportService.toDelete.size());
        assertEquals(0, testCloudSyncNodeTransportService.toUnSync.size());
        
        // Check the core details were correctly serialized/deserialized,
        // and the local/remote swapped
        SyncNodeChangesInfo asSynced = testCloudSyncNodeTransportService.toApply.get(0);
        assertEquals(null, asSynced.getLocalNodeRef());
        assertEquals(SITE_CLOUD.getNodeRef(), asSynced.getLocalParentNodeRef());
        assertEquals(null, asSynced.getLocalPath());
        assertEquals(null, asSynced.getLocalVersionLabel());
        assertEquals(null, asSynced.getLocalModifiedAt());
        
        assertEquals(sourceNode, asSynced.getRemoteNodeRef());
        assertEquals(SITE_ON_PREMISE.getNodeRef(), asSynced.getRemoteParentNodeRef());
        assertEquals(null, asSynced.getRemotePath());
        assertEquals(null, asSynced.getRemoteVersionLabel());
        
        assertEquals(null, asSynced.getAspectsAdded());
        assertEquals(null, asSynced.getAspectsRemoved());
        assertEquals(null, asSynced.getPropertyUpdates());
        assertEquals(null, asSynced.getContentUpdates());
        
        
        // Build an "updateable" version
        NodeRef targetNode = createTestNode(SITE_ON_PREMISE, "target", "Target Node", "Stuff!!!");
        SyncNodeChangesInfoImpl upSync = new SyncNodeChangesInfoImpl(
                sourceNode, targetNode, syncSet, nodeService.getType(sourceNode));
        upSync.setLocalParentNodeRef(SITE_ON_PREMISE.getNodeRef());
        upSync.setRemoteParentNodeRef(SITE_CLOUD.getNodeRef());
        
        String sourcePath = "/this/is/the/source";
        String targetPath = "/is/the/target";
        upSync.setLocalPath(sourcePath);
        upSync.setRemotePath(targetPath);
                
        // Populate some more
        upSync.setLocalVersionLabel("Updated Version");
        upSync.setLocalModifiedAt(new Date(1234));
        
        
        // Do an update
        testCloudSyncNodeTransportService.reset();
        realCloudSyncNodeTransportService.pushSyncChange(upSync, null);
        
        // Check all the details were correctly serialized/deserialized
        asSynced = testCloudSyncNodeTransportService.toApply.get(0);
        assertEquals(targetNode, asSynced.getLocalNodeRef());
        assertEquals(SITE_CLOUD.getNodeRef(), asSynced.getLocalParentNodeRef());
        assertEquals(targetPath, asSynced.getLocalPath());
        assertEquals(null, asSynced.getLocalVersionLabel());
        assertEquals(null, asSynced.getLocalModifiedAt());
        
        assertEquals(sourceNode, asSynced.getRemoteNodeRef());
        assertEquals(SITE_ON_PREMISE.getNodeRef(), asSynced.getRemoteParentNodeRef());
        assertEquals(sourcePath, asSynced.getRemotePath());
        assertEquals("Updated Version", asSynced.getRemoteVersionLabel());
        assertEquals(new Date(1234), asSynced.getRemoteModifiedAt());
        
                     
        // Ask for it to be unsynced
        testCloudSyncNodeTransportService.reset();
        realCloudSyncNodeTransportService.pushUnSync(upSync, null);
        
        assertEquals(0, testCloudSyncNodeTransportService.toApply.size());
        assertEquals(1, testCloudSyncNodeTransportService.toUnSync.size());
        assertEquals(0, testCloudSyncNodeTransportService.toDelete.size());
        
        // Check the right details turned up for it
        asSynced = testCloudSyncNodeTransportService.toUnSync.get(0);
        assertEquals(targetNode, asSynced.getLocalNodeRef());
        assertEquals(sourceNode, asSynced.getRemoteNodeRef());

        
        // Ask for it to be deleted
        testCloudSyncNodeTransportService.reset();
        realCloudSyncNodeTransportService.pushSyncDelete(upSync, null);
        
        assertEquals(0, testCloudSyncNodeTransportService.toApply.size());
        assertEquals(0, testCloudSyncNodeTransportService.toUnSync.size());
        assertEquals(1, testCloudSyncNodeTransportService.toDelete.size());
        
        // Check the right details turned up for it
        asSynced = testCloudSyncNodeTransportService.toDelete.get(0);
        assertEquals(targetNode, asSynced.getLocalNodeRef());
        assertEquals(sourceNode, asSynced.getRemoteNodeRef());

        
        // Check that we have to give one of remote noderef or parent noderef to push
        SyncNodeChangesInfo noDetails = new SyncNodeChangesInfoImpl(sourceNode, null, syncSet, nodeService.getType(sourceNode));
        try
        {
            realCloudSyncNodeTransportService.pushSyncInitial(noDetails, null);
            fail("Shouldn't be able to push initial with no parent details");
        }
        catch(IllegalArgumentException e) {}
        try
        {
            realCloudSyncNodeTransportService.pushSyncChange(noDetails, null);
            fail("Shouldn't be able to push with no remote details");
        }
        catch(IllegalArgumentException e) {}
        
        // Check that we have to give the remote noderef to delete / unsync
        try
        {
            realCloudSyncNodeTransportService.pushSyncDelete(noDetails, null);
            fail("Shouldn't be able to push initial with no parent details");
        }
        catch(IllegalArgumentException e) {}
        try
        {
            realCloudSyncNodeTransportService.pushUnSync(noDetails, null);
            fail("Shouldn't be able to push initial with no parent details");
        }
        catch(IllegalArgumentException e) {}
    }
    
    /**
     * Performs detailed checks on the sync transport, to ensure that
     *  the correct details are being passed over and decoded.
     * Covers all the core details, the aspects, non-content properties
     *  and content properties
     */
    public void testPushInitialAndChangesDetails() throws Exception
    {
        AuthenticationUtil.setFullyAuthenticatedUser(USER_ONE);
        
        // Create a local node
        String initialContent = "Stuff!!!";
        NodeRef sourceNode = createTestNode(SITE_ON_PREMISE, "testNode", "Test Node", initialContent);
        
        // Set some credentials
        cloudConnectorService.storeCloudCredentials(USER_THREE, PASSWORD);
        
        // Put it into a real sync set
        String network = "-default-";
        SyncSetDefinition ssd = syncAdminService.createSourceSyncSet(
                Arrays.asList(new NodeRef[] {sourceNode}), network, SITE_CLOUD.getNodeRef().toString(), false, true, false);
        syncSets.add(ssd);
        String syncSet = ssd.getId();
        
        
        // Wrap it as a SyncNodeChangesInfo
        SyncNodeChangesInfoImpl newSync = new SyncNodeChangesInfoImpl(
                sourceNode, null, syncSet, nodeService.getType(sourceNode));
        newSync.setLocalParentNodeRef(SITE_ON_PREMISE.getNodeRef());
        newSync.setRemoteParentNodeRef(SITE_CLOUD.getNodeRef());
        
        String sourcePath = "/this/is/the/source";
        String targetPath = "/is/the/target";
        newSync.setLocalPath(sourcePath);
        newSync.setRemotePath(targetPath);

        
        // Set lots of different things on it
        newSync.setLocalVersionLabel("Initial Version");
        newSync.setLocalModifiedAt(new Date(1234));
        
        // Populate aspects and properties
        newSync.setAspectsAdded(new HashSet<QName>(
                Arrays.asList(new QName[] { ContentModel.ASPECT_TITLED })
        ));
        newSync.setAspectsRemoved(new HashSet<QName>(
                Arrays.asList(new QName[] { ContentModel.ASPECT_AUTHOR, ContentModel.ASPECT_DUBLINCORE })
        ));
        
        Map<QName,Serializable> newProps = new HashMap<QName, Serializable>();
        newProps.put(ContentModel.PROP_TITLE, "Title Title");
        newProps.put(ContentModel.PROP_DESCRIPTION, "A description");
        newProps.put(ContentModel.PROP_AUTHOR, null);
        newSync.setPropertyUpdates(newProps);
        
        // Don't send any content on this version
        Map<QName,CloudSyncContent> newContent = new HashMap<QName, CloudSyncContent>();
        newSync.setContentUpdates(newContent);

        
        // Perform the initial sync, check they turned up
        NodeRef remote = realCloudSyncNodeTransportService.pushSyncInitial(newSync, null);
        assertEquals(TestCloudSyncMemberNodeTransport.DUMMY_NEW_NODE_NODEREF, remote);
        
        assertEquals(1, testCloudSyncNodeTransportService.toApply.size());
        SyncNodeChangesInfo asSynced = testCloudSyncNodeTransportService.toApply.get(0);
        
        // Check the details on it, and that remote/local swapped
        assertEquals(null, asSynced.getLocalNodeRef());
        assertEquals(SITE_CLOUD.getNodeRef(), asSynced.getLocalParentNodeRef());
        assertEquals(targetPath, asSynced.getLocalPath());
        assertEquals(null, asSynced.getLocalVersionLabel());
        assertEquals(null, asSynced.getLocalModifiedAt());
        
        assertEquals(sourceNode, asSynced.getRemoteNodeRef());
        assertEquals(SITE_ON_PREMISE.getNodeRef(), asSynced.getRemoteParentNodeRef());
        assertEquals(sourcePath, asSynced.getRemotePath());
        assertEquals("Initial Version", asSynced.getRemoteVersionLabel());
        assertEquals(new Date(1234), asSynced.getRemoteModifiedAt());
        
        // Check Aspects and Properties
        assertEquals(1, asSynced.getAspectsAdded().size());
        assertEquals("Not found in " + asSynced.getAspectsAdded(), true,
                     asSynced.getAspectsAdded().contains(ContentModel.ASPECT_TITLED));

        assertEquals(2, asSynced.getAspectsRemoved().size());
        assertEquals("Not found in " + asSynced.getAspectsRemoved(), true,
                     asSynced.getAspectsRemoved().contains(ContentModel.ASPECT_AUTHOR));
        assertEquals("Not found in " + asSynced.getAspectsRemoved(), true,
                     asSynced.getAspectsRemoved().contains(ContentModel.ASPECT_DUBLINCORE));
        
        assertEquals(3, asSynced.getPropertyUpdates().size());
        assertEquals("Title Title", asSynced.getPropertyUpdates().get(ContentModel.PROP_TITLE));
        assertEquals("A description", asSynced.getPropertyUpdates().get(ContentModel.PROP_DESCRIPTION));
        assertEquals(null, asSynced.getPropertyUpdates().get(ContentModel.PROP_AUTHOR));

        // Check content
        assertEquals(null, asSynced.getContentUpdates());
        
        
        // Create an update version
        String updateContent = "Different Stuff";
        sourceNode = createTestNode(SITE_ON_PREMISE, "testNode2", "Test Node Update", updateContent);

        SyncNodeChangesInfoImpl upSync = new SyncNodeChangesInfoImpl(
                sourceNode, sourceNode, syncSet, nodeService.getType(sourceNode));
        upSync.setLocalParentNodeRef(SITE_ON_PREMISE.getNodeRef());
        upSync.setRemoteParentNodeRef(SITE_CLOUD.getNodeRef());

        upSync.setLocalPath(sourcePath);
        upSync.setRemotePath(targetPath);

        
        // Populate it with some different things
        upSync.setLocalVersionLabel("New Version");
        upSync.setLocalModifiedAt(new Date(54321));
        
        // Populate aspects and properties
        upSync.setAspectsAdded(new HashSet<QName>(
                Arrays.asList(new QName[] { ContentModel.ASPECT_DUBLINCORE, ContentModel.ASPECT_AUTHOR })
        ));
        upSync.setAspectsRemoved(new HashSet<QName>(
                Arrays.asList(new QName[] { ContentModel.ASPECT_COUNTABLE, ContentModel.ASPECT_TEMPLATABLE })
        ));
        Map<QName,Serializable> upProps = new HashMap<QName, Serializable>();
        upProps.put(ContentModel.PROP_TITLE, "Updated Title");
        upProps.put(ContentModel.PROP_DESCRIPTION, "New description");
        upProps.put(ContentModel.PROP_SKYPE, null);
        upSync.setPropertyUpdates(upProps);
        
        // Set two content properties, and remove a third
        // Note - we cheat and use non-content properties here, as we 
        //  don't have to worry about validation
        Map<QName,CloudSyncContent> upContent = new HashMap<QName, CloudSyncContent>();
        upContent.put(ContentModel.PROP_TITLE, null);
        upContent.put(ContentModel.PROP_CONTENT, new CloudSyncContentNodeImpl(ContentModel.PROP_CONTENT, sourceNode,  contentService));
        
        NodeRef altNode = createTestNode(SITE_ON_PREMISE, "alt", "alt", "Alternate Content");
        upContent.put(ContentModel.PROP_DESCRIPTION, new CloudSyncContentNodeImpl(ContentModel.PROP_CONTENT, altNode, contentService));
        upSync.setContentUpdates(upContent);
        
        
        // Do the update sync
        testCloudSyncNodeTransportService.reset();
        realCloudSyncNodeTransportService.pushSyncChange(upSync, null);

        assertEquals(1, testCloudSyncNodeTransportService.toApply.size());
        asSynced = testCloudSyncNodeTransportService.toApply.get(0);

        // Check they turned up
        assertEquals(sourceNode, asSynced.getLocalNodeRef());
        assertEquals(SITE_CLOUD.getNodeRef(), asSynced.getLocalParentNodeRef());
        assertEquals(targetPath, asSynced.getLocalPath());
        assertEquals(null, asSynced.getLocalVersionLabel());
        assertEquals(null, asSynced.getLocalModifiedAt());
        
        assertEquals(sourceNode, asSynced.getRemoteNodeRef());
        assertEquals(SITE_ON_PREMISE.getNodeRef(), asSynced.getRemoteParentNodeRef());
        assertEquals(sourcePath, asSynced.getRemotePath());
        assertEquals("New Version", asSynced.getRemoteVersionLabel());
        assertEquals(new Date(54321), asSynced.getRemoteModifiedAt());
        
        // Check Aspects and Properties
        assertEquals(2, asSynced.getAspectsAdded().size());
        assertEquals("Not found in " + asSynced.getAspectsAdded(), true,
                     asSynced.getAspectsAdded().contains(ContentModel.ASPECT_DUBLINCORE));
        assertEquals("Not found in " + asSynced.getAspectsAdded(), true,
                asSynced.getAspectsAdded().contains(ContentModel.ASPECT_AUTHOR));

        assertEquals(2, asSynced.getAspectsRemoved().size());
        assertEquals("Not found in " + asSynced.getAspectsRemoved(), true,
                     asSynced.getAspectsRemoved().contains(ContentModel.ASPECT_COUNTABLE));
        assertEquals("Not found in " + asSynced.getAspectsRemoved(), true,
                     asSynced.getAspectsRemoved().contains(ContentModel.ASPECT_TEMPLATABLE));
        
        assertEquals(3, asSynced.getPropertyUpdates().size());
        assertEquals("Updated Title", asSynced.getPropertyUpdates().get(ContentModel.PROP_TITLE));
        assertEquals("New description", asSynced.getPropertyUpdates().get(ContentModel.PROP_DESCRIPTION));
        assertEquals(null, asSynced.getPropertyUpdates().get(ContentModel.PROP_SKYPE));

        // Check content
        assertNotNull(asSynced.getContentUpdates());
        assertEquals(3, asSynced.getContentUpdates().size());
        assertEquals(true, asSynced.getContentUpdates().containsKey(ContentModel.PROP_TITLE));
        assertEquals(true, asSynced.getContentUpdates().containsKey(ContentModel.PROP_CONTENT));
        assertEquals(true, asSynced.getContentUpdates().containsKey(ContentModel.PROP_DESCRIPTION));
        
        assertEquals(null, asSynced.getContentUpdates().get(ContentModel.PROP_TITLE));
        
        CloudSyncContent content = asSynced.getContentUpdates().get(ContentModel.PROP_CONTENT);
        assertNotNull(content);
        ContentReader propContent = content.openReader();
        
        assertEquals(updateContent, propContent.getContentString());
        assertEquals(MimetypeMap.MIMETYPE_TEXT_PLAIN, propContent.getMimetype());
        assertEquals("UTF-8", propContent.getEncoding());
        
        CloudSyncContent propContDesc = asSynced.getContentUpdates().get(ContentModel.PROP_DESCRIPTION);
        assertNotNull(propContDesc);
        ContentReader reader = propContDesc.openReader();
        assertEquals("Alternate Content", reader.getContentString());
        assertEquals(MimetypeMap.MIMETYPE_TEXT_PLAIN, reader.getMimetype());
        assertEquals("UTF-8", reader.getEncoding());
    }
    
    /**
     * Check that we can push a folder. (Other tests generally just work with cm:content)
     */
    public void testPushFolder() throws Exception
    {
        AuthenticationUtil.setFullyAuthenticatedUser(USER_ONE);
        
        // Create a local folder
        NodeRef sourceFolder = createTestFolder(SITE_ON_PREMISE, "testFolder", "Test Folder");
        
        // For now, child nodes make no difference
        NodeRef sourceChild1 = createTestNode(sourceFolder, "testNode1", "A child node", "Stuff");
        NodeRef sourceChild2 = createTestNode(sourceFolder, "testNode2", "B child node", "Stuff");
        
        
        // Set some credentials
        cloudConnectorService.storeCloudCredentials(USER_THREE, PASSWORD);
        
        // Put the folder into a real sync set
        String network = "-default-";
        SyncSetDefinition ssd = syncAdminService.createSourceSyncSet(
                Arrays.asList(new NodeRef[] {sourceFolder}), network, SITE_CLOUD.getNodeRef().toString(), false, true, false);
        syncSets.add(ssd);
        String syncSet = ssd.getId();
        
        
        // Wrap it as a SyncNodeChangesInfo
        SyncNodeChangesInfoImpl newSync = new SyncNodeChangesInfoImpl(
                sourceFolder, null, syncSet, nodeService.getType(sourceFolder));
        newSync.setLocalParentNodeRef(SITE_ON_PREMISE.getNodeRef());
        newSync.setRemoteParentNodeRef(SITE_CLOUD.getNodeRef());
        
        // As it's a folder, much of the transport stuff doesn't make sense
        // For example, there's no content, and no versioning
        
        // Populate aspects and properties
        newSync.setAspectsAdded(new HashSet<QName>(
                Arrays.asList(new QName[] { ContentModel.ASPECT_TITLED })
        ));
        newSync.setAspectsRemoved(new HashSet<QName>(
                Arrays.asList(new QName[] { ContentModel.ASPECT_AUTHOR, ContentModel.ASPECT_DUBLINCORE })
        ));
        
        Map<QName,Serializable> newProps = new HashMap<QName, Serializable>();
        newProps.put(ContentModel.PROP_TITLE, "Title Title");
        newProps.put(ContentModel.PROP_DESCRIPTION, "A description");
        newProps.put(ContentModel.PROP_AUTHOR, null);
        newSync.setPropertyUpdates(newProps);
        
        
        // Perform the initial sync, check they turned up
        NodeRef remote = realCloudSyncNodeTransportService.pushSyncInitial(newSync, null);
        assertEquals(TestCloudSyncMemberNodeTransport.DUMMY_NEW_NODE_NODEREF, remote);
        
        assertEquals(1, testCloudSyncNodeTransportService.toApply.size());
        SyncNodeChangesInfo asSynced = testCloudSyncNodeTransportService.toApply.get(0);
        
        // Check the details on it, and that remote/local swapped
        assertEquals(null, asSynced.getLocalNodeRef());
        assertEquals(SITE_CLOUD.getNodeRef(), asSynced.getLocalParentNodeRef());
        assertEquals(null, asSynced.getLocalVersionLabel());
        assertEquals(null, asSynced.getLocalModifiedAt());
        
        assertEquals(sourceFolder, asSynced.getRemoteNodeRef());
        assertEquals(SITE_ON_PREMISE.getNodeRef(), asSynced.getRemoteParentNodeRef());
        assertEquals(null, asSynced.getRemoteVersionLabel());
        assertEquals(null, asSynced.getRemoteModifiedAt());
        
        // Check Aspects and Properties
        assertEquals(1, asSynced.getAspectsAdded().size());
        assertEquals("Not found in " + asSynced.getAspectsAdded(), true,
                     asSynced.getAspectsAdded().contains(ContentModel.ASPECT_TITLED));

        assertEquals(2, asSynced.getAspectsRemoved().size());
        assertEquals("Not found in " + asSynced.getAspectsRemoved(), true,
                     asSynced.getAspectsRemoved().contains(ContentModel.ASPECT_AUTHOR));
        assertEquals("Not found in " + asSynced.getAspectsRemoved(), true,
                     asSynced.getAspectsRemoved().contains(ContentModel.ASPECT_DUBLINCORE));
        
        assertEquals(3, asSynced.getPropertyUpdates().size());
        assertEquals("Title Title", asSynced.getPropertyUpdates().get(ContentModel.PROP_TITLE));
        assertEquals("A description", asSynced.getPropertyUpdates().get(ContentModel.PROP_DESCRIPTION));
        assertEquals(null, asSynced.getPropertyUpdates().get(ContentModel.PROP_AUTHOR));

        // Check content isn't there
        assertEquals(null, asSynced.getContentUpdates());
    }
    
    /**
     * Check that we correctly check to see if a sync is allowed at different points 
     */
    public void testPushAllowed() throws Exception
    {
        // Create a local node
        String initialContent = "Stuff!!!";
        NodeRef sourceNode = createTestNode(SITE_ON_PREMISE, "testNode", "Test Node", initialContent);
        
        // Set some credentials
        cloudConnectorService.storeCloudCredentials(USER_THREE, PASSWORD);
        
        // Put it into a sync set
        String network = "-default-";
        SyncSetDefinition ssd = syncAdminService.createSourceSyncSet(
                Arrays.asList(new NodeRef[] {sourceNode}), network, SITE_CLOUD.getNodeRef().toString(), false, true, false);
        syncSets.add(ssd);
        String syncSet = ssd.getId();
        
        // Wrap it as a SyncNodeChangesInfo
        SyncNodeChangesInfoImpl newSync = new SyncNodeChangesInfoImpl(
                sourceNode, null, syncSet, nodeService.getType(sourceNode));
        newSync.setLocalParentNodeRef(SITE_ON_PREMISE.getNodeRef());
        newSync.setRemoteParentNodeRef(SITE_CLOUD.getNodeRef());
        
        
        // Set as all deny
        testCloudSyncOnCloudService.setCanOccur(false);
        testCloudSyncOnCloudService.setCanProceed(false);
        
        // Try, will be told it isn't permitted
        try
        {
            realCloudSyncNodeTransportService.pushSyncInitial(newSync, null);
            fail("Cloud sync should not have been permitted");
        }
        catch (CloudSyncDeclinedException e) {}
        
        
        // Only can occur will have been called
        assertEquals(true, testCloudSyncOnCloudService.wasCanOccurCalled());
        assertEquals(false, testCloudSyncOnCloudService.wasCanProceedCalled());
        
        
        // Change to deny at proceed
        testCloudSyncOnCloudService.reset();
        testCloudSyncOnCloudService.setCanOccur(true);
        testCloudSyncOnCloudService.setCanProceed(false);
        
        // Also denied
        try
        {
            realCloudSyncNodeTransportService.pushSyncInitial(newSync, null);
            fail("Cloud sync should not have been permitted");
        }
        catch (CloudSyncDeclinedException e) {}
        
        // Both were checked
        assertEquals(true, testCloudSyncOnCloudService.wasCanOccurCalled());
        assertEquals(true, testCloudSyncOnCloudService.wasCanProceedCalled());

        
        // Change to allow
        testCloudSyncOnCloudService.reset();
        testCloudSyncOnCloudService.setCanOccur(true);
        testCloudSyncOnCloudService.setCanProceed(true);

        // Will work
        NodeRef remote = realCloudSyncNodeTransportService.pushSyncInitial(newSync, null);
        assertEquals(TestCloudSyncMemberNodeTransport.DUMMY_NEW_NODE_NODEREF, remote);

        // Was checked
        assertEquals(true, testCloudSyncOnCloudService.wasCanOccurCalled());
        assertEquals(true, testCloudSyncOnCloudService.wasCanProceedCalled());
    }
    
    /**
     * Performs a pull call, and ensures that the correct data turns up for it
     */
    public void testPullChanges() throws Exception
    {
        AuthenticationUtil.setFullyAuthenticatedUser(USER_ONE);
        SyncNodeChangesInfo stub;
        
        // Create a local node
        String initialContent = "Stuff!!!";
        NodeRef sourceNode = createTestNode(SITE_ON_PREMISE, "testNode", "Test Node", initialContent);
        QName nodeType = nodeService.getType(sourceNode);
        
        // Set some credentials
        cloudConnectorService.storeCloudCredentials(USER_THREE, PASSWORD);
        
        // Put it into a real sync set
        String network = "-default-";
        SyncSetDefinition ssd = syncAdminService.createSourceSyncSet(
                Arrays.asList(new NodeRef[] {sourceNode}), network, SITE_CLOUD.getNodeRef().toString(), false, true, false);
        syncSets.add(ssd);
        String syncSet = ssd.getId();
        
        
        // Can't pull without a syncset
        stub = new SyncNodeChangesInfoImpl(sourceNode, sourceNode, null, null);
        try {
            realCloudSyncNodeTransportService.pullSyncChange(stub, network);
            fail("Shouldn't be allowed without a SyncSet");
        } catch(IllegalArgumentException e) {}
        
        // Can't pull without the remote nodeRef (local is optional)
        stub = new SyncNodeChangesInfoImpl(null, null, syncSet, null);
        try {
            realCloudSyncNodeTransportService.pullSyncChange(stub, network);
            fail("Shouldn't be allowed without a Remote NodeRef");
        } catch(IllegalArgumentException e) {}
        
        
        // Wrap our node as a stub SyncNodeChangesInfo
        stub = new SyncNodeChangesInfoImpl(null, sourceNode, syncSet, nodeType);
        
        // Try to pull it
        testSyncService.pullResult = stub;
        SyncNodeChangesInfo pull = realCloudSyncNodeTransportService.pullSyncChange(stub, network);
        
        // Check we got something back (note local and remote will switch in transport)
        assertEquals(sourceNode, pull.getLocalNodeRef());
        assertEquals(null, pull.getRemoteNodeRef());
        assertEquals(syncSet,  pull.getSyncSetGUID());
        assertEquals(nodeType, pull.getType());
        
        
        // Build up a more complex pull example
        NodeRef altNode = createTestNode(SITE_CLOUD, "Complex", "Complex Title", "Stuff");
        SyncNodeChangesInfoImpl toPull = new SyncNodeChangesInfoImpl(altNode, sourceNode, syncSet, nodeType);
        toPull.setAspectsAdded(new HashSet<QName>(
                Arrays.asList(new QName[] { ContentModel.ASPECT_TITLED })
        ));
        toPull.setAspectsRemoved(new HashSet<QName>(
                Arrays.asList(new QName[] { ContentModel.ASPECT_AUTHOR, ContentModel.ASPECT_DUBLINCORE })
        ));
        
        Map<QName,Serializable> pullProps = new HashMap<QName, Serializable>();
        pullProps.put(ContentModel.PROP_TITLE, "Test Title");
        pullProps.put(ContentModel.PROP_DESCRIPTION, "A description");
        pullProps.put(ContentModel.PROP_AUTHOR, null);
        toPull.setPropertyUpdates(pullProps);
        
        Map<QName,CloudSyncContent> pullContent = new HashMap<QName, CloudSyncContent>();
        pullContent.put(ContentModel.PROP_TITLE, null);
        pullContent.put(ContentModel.PROP_CONTENT, new CloudSyncContentNodeImpl(ContentModel.PROP_CONTENT, altNode, contentService));
        toPull.setContentUpdates(pullContent);
        
        AuditTokenImpl auditToken = new AuditTokenImpl();
        auditToken.record(1234l);
        auditToken.record(4321l);
        toPull.setAuditToken(auditToken);
        
        testSyncService.pullResult = toPull;

        
        // Try to pull it
        stub = new SyncNodeChangesInfoImpl(sourceNode, altNode, syncSet, nodeType);
        pull = realCloudSyncNodeTransportService.pullSyncChange(stub, network);
        
        
        // Check we got everything back
        // NodeRefs swap round
        assertEquals(sourceNode, pull.getLocalNodeRef());
        assertEquals(altNode, pull.getRemoteNodeRef());
        
        // Aspects sent fine
        assertEquals(1, pull.getAspectsAdded().size());
        assertEquals("Not found in " + pull.getAspectsAdded(), true,
                     pull.getAspectsAdded().contains(ContentModel.ASPECT_TITLED));

        assertEquals(2, pull.getAspectsRemoved().size());
        assertEquals("Not found in " + pull.getAspectsRemoved(), true,
                     pull.getAspectsRemoved().contains(ContentModel.ASPECT_AUTHOR));
        assertEquals("Not found in " + pull.getAspectsRemoved(), true,
                     pull.getAspectsRemoved().contains(ContentModel.ASPECT_DUBLINCORE));
        
        // Properties sent file
        assertEquals(3, pull.getPropertyUpdates().size());
        assertEquals("Test Title", pull.getPropertyUpdates().get(ContentModel.PROP_TITLE));
        assertEquals("A description", pull.getPropertyUpdates().get(ContentModel.PROP_DESCRIPTION));
        assertEquals(null, pull.getPropertyUpdates().get(ContentModel.PROP_AUTHOR));

        // Content sent fine
        assertNotNull(pull.getContentUpdates());
        assertEquals(2, pull.getContentUpdates().size());
        assertEquals(true, pull.getContentUpdates().containsKey(ContentModel.PROP_TITLE));
        assertEquals(true, pull.getContentUpdates().containsKey(ContentModel.PROP_CONTENT));
        
        assertEquals(null, pull.getContentUpdates().get(ContentModel.PROP_TITLE));
        
        CloudSyncContent content = pull.getContentUpdates().get(ContentModel.PROP_CONTENT);
        ContentReader propContent = content.openReader();
        assertNotNull(propContent);
        assertEquals("Stuff", propContent.getContentString());
        assertEquals(MimetypeMap.MIMETYPE_TEXT_PLAIN, propContent.getMimetype());
        assertEquals("UTF-8", propContent.getEncoding());
        
        // Audit Token seen/transported fine
        // (Note - this bit needs to be kept in sync with AuditToken format changes)
        assertNotNull(pull.getAuditToken());
        assertEquals("{\"auditIds\":[1234,4321]}", pull.getAuditToken().asJSON().toString());
    }
        
    /**
     * Try to do operations like push, unsync etc, having set things
     *  up for a conflict, and check it gets correctly reported.
     * 
     * Note - pull does not generate a conflict during transport, as that
     *  is checked for and handled in the job before doing the pull.
     * Neither of Confirm Pull and Push Conflict generate conflicts
     */
    public void testConflictResponseOnPushUnsync() throws Exception
    {
        // Have conflicts raised
        testCloudSyncNodeTransportService.isConflict = true;
        
        // Test node to push / un-sync / etc
        NodeRef sourceNode = createTestNode(SITE_ON_PREMISE, "testNode", "Test Node", "Stuff");
        SyncNodeChangesInfoImpl sync = new SyncNodeChangesInfoImpl(
                sourceNode, sourceNode, "ignored", nodeService.getType(sourceNode));
        cloudConnectorService.storeCloudCredentials(USER_THREE, PASSWORD);
       
        // Check the conflict on push
        try
        {
            realCloudSyncNodeTransportService.pushSyncChange(sync, null);
            fail("Should have given a conflict");
        }
        catch (ConcurrentModificationException e) {}
        
        // Check the conflict on unsync
        try
        {
            realCloudSyncNodeTransportService.pushUnSync(sync, null);
            fail("Should have given a conflict");
        }
        catch (ConcurrentModificationException e) {}
        
        // Check the conflict on delete
        try
        {
            realCloudSyncNodeTransportService.pushSyncDelete(sync, null);
            fail("Should have given a conflict");
        }
        catch (ConcurrentModificationException e) {}
        
        
        // Put things back
        testCloudSyncNodeTransportService.reset();
    }
    
    /**
     * Performs a confirmation of a pull, which sends the audit token(s)
     *  back to the cloud.
     */
    public void testConfirmPull() throws Exception
    {
        AuthenticationUtil.setFullyAuthenticatedUser(USER_ONE);
        ArgumentCaptor<AuditToken> testSyncAuditServiceDeleteCaptor;
        
        // Build a couple of fake AuditTokens
        AuditTokenImpl tokenA = new AuditTokenImpl();
        AuditTokenImpl tokenB = new AuditTokenImpl();
        tokenA.record(1234l);
        tokenB.record(12345l);
        tokenB.record(54321l);
        
        // Can't confirm without credentials
        assertEquals(null, cloudConnectorService.getCloudCredentials());
        try
        {
            realCloudSyncNodeTransportService.confirmPull(new AuditToken[] {tokenA}, null);
            fail("Shouldn't be able to confirm without credentials!");
        }
        catch(AuthenticationException e) {}
        
        
        // Setup some credentials
        AuthenticationUtil.setFullyAuthenticatedUser(USER_ONE);
        cloudConnectorService.storeCloudCredentials(USER_THREE, PASSWORD);
        
        // Confirm one
        realCloudSyncNodeTransportService.confirmPull(new AuditToken[] {tokenA}, null);
        
        // Check it was passed in
        testSyncAuditServiceDeleteCaptor = ArgumentCaptor.forClass(AuditToken.class);
        verify(testSyncAuditService, times(1)).deleteAuditEntries(testSyncAuditServiceDeleteCaptor.capture());
        
        assertEquals(1, testSyncAuditServiceDeleteCaptor.getAllValues().size());
        assertEquals(tokenA, testSyncAuditServiceDeleteCaptor.getAllValues().get(0));

        
        // Confirm both
        realCloudSyncNodeTransportService.confirmPull(new AuditToken[] {tokenA,tokenB}, null);
        
        // Check they turned up OK
        testSyncAuditServiceDeleteCaptor = ArgumentCaptor.forClass(AuditToken.class);
        verify(testSyncAuditService, times(1)).deleteAuditEntries(
                testSyncAuditServiceDeleteCaptor.capture(),
                testSyncAuditServiceDeleteCaptor.capture());

        assertEquals(2, testSyncAuditServiceDeleteCaptor.getAllValues().size());
        assertEquals(tokenA, testSyncAuditServiceDeleteCaptor.getAllValues().get(0));
        assertEquals(tokenB, testSyncAuditServiceDeleteCaptor.getAllValues().get(1));
    }
    
    public void testPushConflict() throws Exception
    {
        AuthenticationUtil.setFullyAuthenticatedUser(USER_ONE);

        // Dummy mark as a sync set, but don't worry about a real one for now
        String syncSet = "testSyncSet";
        
        // Setup a node to conflict
        NodeRef sourceNode = createTestNode(SITE_ON_PREMISE, "testNode", "Test Node", "Stuff!!!");
        NodeRef targetNode = createTestNode(SITE_ON_PREMISE, "target", "Target Node", "Stuff!!!");
        SyncNodeChangesInfoImpl stubSync = new SyncNodeChangesInfoImpl(
                sourceNode, targetNode, syncSet, nodeService.getType(sourceNode));
        stubSync.setLocalParentNodeRef(SITE_ON_PREMISE.getNodeRef());
        stubSync.setRemoteParentNodeRef(SITE_CLOUD.getNodeRef());

        
        // Can't push a conflict without credentials
        assertEquals(null, cloudConnectorService.getCloudCredentials());
        try
        {
            realCloudSyncNodeTransportService.pushConflictDetected(null, null);
            fail("Shouldn't be able to push a conflict without credentials!");
        }
        catch(AuthenticationException e) {}
        
        
        // Setup some credentials
        AuthenticationUtil.setFullyAuthenticatedUser(USER_ONE);
        cloudConnectorService.storeCloudCredentials(USER_THREE, PASSWORD);
        
        
        // Push the real conflict
        testSyncService.conflictValue = null;
        realCloudSyncNodeTransportService.pushConflictDetected(stubSync, null);

        // Check it turned up
        assertNotNull(testSyncService.conflictValue);
        assertEquals(targetNode, testSyncService.conflictValue.getLocalNodeRef());
        assertEquals(sourceNode, testSyncService.conflictValue.getRemoteNodeRef());
    }
    
    /**
     * Tests that we properly encode and decode different property values
     *  when sending them over the network
     */
    public void testPropertyValueTransport()
    {
        // Build up a list of one of each
        // QNames don't matter as long as they're unique
        Map<QName,Serializable> props = new HashMap<QName, Serializable>();
        
        props.put(ContentModel.PROP_ACCESSED, "A test String");
        props.put(ContentModel.PROP_ACCOUNT_EXPIRES, new Date(654321000));
        props.put(ContentModel.PROP_ACCOUNT_LOCKED, false);
        props.put(ContentModel.PROP_ADDRESSEE, 123);
        props.put(ContentModel.PROP_ADDRESSEES, 87654321l);
        props.put(ContentModel.PROP_ARCHIVED_BY, 1.23f);
        props.put(ContentModel.PROP_ARCHIVED_DATE, 123.456);
        props.put(ContentModel.PROP_ARCHIVED_ORIGINAL_OWNER, null);
        props.put(ContentModel.PROP_AUTHOR, new TestSerializableValue(432, 76542l, "Serialised String"));
        
        // And some multi-valued properties
        props.put(ContentModel.PROP_CATEGORIES, (Serializable)Arrays.asList(new String[] {"Str1","2Str2"}));
        props.put(ContentModel.PROP_COMPANYFAX, (Serializable)Arrays.asList(new Integer[] {123,345,567}));
        props.put(ContentModel.PROP_COMPANYPOSTCODE, (Serializable)Arrays.asList(new TestSerializableValue[] {
                new TestSerializableValue(432, 76542l, "Serialised String"),
                new TestSerializableValue(123, 19542l, "Another String"),
                new TestSerializableValue(-92, 92312l, "Final String"),
        }));
        
        // Finally a ML-Text property
        MLText mlText = new MLText();
        mlText.put(Locale.ENGLISH, "In English");
        mlText.put(Locale.FRENCH, "En Fran\u00e7ais");
        props.put(ContentModel.PROP_COUNTER, mlText);
        

        // Have it encoded / decoded
        NodeRef sourceNode = createTestNode(SITE_ON_PREMISE, "testNode", "Test Node", "Stuff");
        SyncNodeChangesInfoImpl sync = new SyncNodeChangesInfoImpl(
                sourceNode, sourceNode, "ignored", nodeService.getType(sourceNode));
        sync.setPropertyUpdates(props);
        
        cloudConnectorService.storeCloudCredentials(USER_THREE, PASSWORD);
        realCloudSyncNodeTransportService.pushSyncChange(sync, null);

        Map<QName,Serializable> recvProps = testCloudSyncNodeTransportService.toApply.get(0).getPropertyUpdates();

        
        // Check they came back properly
        assertEquals(props.size(), recvProps.size());
        for (QName key : props.keySet())
        {
            assertEquals(props.get(key), recvProps.get(key));
        }
    }
    
    /**
     * Tests that we handle things like IOExceptions, NoRouteToHost etc
     *  cleanly, when given by the low level connector.
     * Doesn't check that exceptions / problems are correctly
     *  transported, that is done in another test.
     */
    public void testTransportLevelExceptions() throws Exception
    {
        // Create two test services instances, so we can monkey with them without
        //  affecting all our other tests
        CloudSyncMemberNodeTransportImpl memberTransport = new CloudSyncMemberNodeTransportImpl();
        CloudSyncSetDefinitionTransportImpl ssdTransport = new CloudSyncSetDefinitionTransportImpl();
        
        // Wire in underlying connectors with controllable problems
        // These will be used for checking the handling of connector exceptions
        // (Exception Transport is done a bit later)
        CloudConnectorService mockConnectorService = buildMockCloudConnectorService(null);
        ssdTransport.setCloudConnectorService(mockConnectorService);
        memberTransport.setCloudConnectorService(mockConnectorService);
        memberTransport.setNamespaceService(namespaceService);
        

        // Try the SSD calls when a NoRouteToHost will be given
        when(mockConnectorService.executeCloudRequest(any(RemoteConnectorRequest.class))).
            thenThrow(new NoRouteToHostException());
        
        try
        {
            ssdTransport.pullChangedNodesForSSD("ssid", null);
            fail("Should give a RemoteSystemUnavailableException");
        }
        catch (RemoteSystemUnavailableException e) {}
        try
        {
            ssdTransport.pullChangedSSDs("repoID");
            fail("Should give a RemoteSystemUnavailableException");
        }
        catch (RemoteSystemUnavailableException e) {}
        
        
        // Try the SSD calls when a Client Exception (eg bad request, not acceptable) is given
        reset(mockConnectorService);
        buildMockCloudConnectorService(mockConnectorService);
        when(mockConnectorService.executeCloudRequest(any(RemoteConnectorRequest.class))).
            thenThrow(new RemoteConnectorClientException(Status.STATUS_BAD_REQUEST, "Bang!", null));
    
        try
        {
            ssdTransport.pullChangedNodesForSSD("ssid", null);
            fail("Should give a RemoteSystemUnavailableException");
        }
        catch (RemoteSystemUnavailableException e) {}
        try
        {
            ssdTransport.pullChangedSSDs("repoID");
            fail("Should give a RemoteSystemUnavailableException");
        }
        catch (RemoteSystemUnavailableException e) {}

        
        // Try the Member Node calls, with a UnknownHostException
        reset(mockConnectorService);
        buildMockCloudConnectorService(mockConnectorService);
        when(mockConnectorService.executeCloudRequest(any(RemoteConnectorRequest.class))).
            thenThrow(new UnknownHostException("madeup"));

        NodeRef sourceNode = createTestNode(SITE_ON_PREMISE, "testNode", "Test Node", "Stuff");
        SyncNodeChangesInfoImpl stub = new SyncNodeChangesInfoImpl(sourceNode, null, "ssid", ContentModel.TYPE_CONTENT);
        stub.setRemoteParentNodeRef(sourceNode);
        
        try
        {
            memberTransport.pushSyncInitial(stub, null);
            fail("Should give a RemoteSystemUnavailableException");
        }
        catch (RemoteSystemUnavailableException e) {}
        
        stub = new SyncNodeChangesInfoImpl(sourceNode, sourceNode, "ssid", ContentModel.TYPE_CONTENT);
        try
        {
            memberTransport.pushSyncChange(stub, null);
            fail("Should give a RemoteSystemUnavailableException");
        }
        catch (RemoteSystemUnavailableException e) {}
        try
        {
            memberTransport.pushSyncDelete(stub, null);
            fail("Should give a RemoteSystemUnavailableException");
        }
        catch (RemoteSystemUnavailableException e) {}
        try
        {
            memberTransport.pullSyncChange(stub, null);
            fail("Should give a RemoteSystemUnavailableException");
        }
        catch (RemoteSystemUnavailableException e) {}
        
        
        // Push Member Node calls need to accept the various SyncNodeExceptions
        reset(mockConnectorService);
        buildMockCloudConnectorService(mockConnectorService);
        when(mockConnectorService.executeCloudRequest(any(RemoteConnectorRequest.class))).
            thenThrow(new SyncNodeException(SyncNodeExceptionType.TARGET_FOLDER_NOT_FOUND));

        try
        {
            memberTransport.pushSyncChange(stub, null);
            fail("Should give a SyncNodeException");
        }
        catch (SyncNodeException e) {
            assertEquals(SyncNodeExceptionType.TARGET_FOLDER_NOT_FOUND, e.getExceptionType());
        }
        
        reset(mockConnectorService);
        buildMockCloudConnectorService(mockConnectorService);
        when(mockConnectorService.executeCloudRequest(any(RemoteConnectorRequest.class))).
            thenThrow(new SyncNodeException(SyncNodeExceptionType.TARGET_NODE_ALREADY_SYNCED));

        try
        {
            memberTransport.pushSyncChange(stub, null);
            fail("Should give a SyncNodeException");
        }
        catch (SyncNodeException e) {
            assertEquals(SyncNodeExceptionType.TARGET_NODE_ALREADY_SYNCED, e.getExceptionType());
        }
    }
    
    /**
     * More exception related tests, this time to check that when exceptions
     *  are raised on the remote end, they are correctly handled and encoded
     *  for transport, and then correctly decoded.
     * To do this, we check that if the remote service throws an exception,
     *  then that is correctly transported back (over the local loopback
     *  connector) and then correctly handled.
     * 
     * Certain kinds of exceptions are already tested in the relevant Push and
     *  Pull code routes, such as permissions, validation and "not now", this
     *  only covers the extra ones such as business logic failures.
     * 
     * Currently only SyncMemberNode related exception transport is checked,
     *  as this covers the main cases.
     */
    public void testExceptionTransport() throws Exception    
    {
        // Set up some nodes and a SSD
        NodeRef sourceNode = createTestNode(SITE_ON_PREMISE, "testNode", "Test Node", "Stuff!!!");
        NodeRef destNode   = createTestNode(SITE_CLOUD, "testNode", "Test Node", "Stuff!!!");
        String syncSet = "testSyncSet";
        SyncNodeChangesInfoImpl newSync = new SyncNodeChangesInfoImpl(
                sourceNode, destNode, syncSet, nodeService.getType(sourceNode));
        newSync.setLocalParentNodeRef(SITE_ON_PREMISE.getNodeRef());
        newSync.setRemoteParentNodeRef(SITE_CLOUD.getNodeRef());
        
        // Set some credentials
        cloudConnectorService.storeCloudCredentials(USER_THREE, PASSWORD);
        
        
        // ----------------------
        // Push exception checks
        // ----------------------
        
        // Test push conflict
        testCloudSyncNodeTransportService.isConflict = true;
        try
        {
            realCloudSyncNodeTransportService.pushSyncChange(newSync, null);
            fail("Should have given a conflict");
        }
        catch (ConcurrentModificationException e) {}
        testCloudSyncNodeTransportService.isConflict = false;
        
        // Test push with NoSuchSyncSetDefinitionException
        testCloudSyncNodeTransportService.fetchLocalException = new NoSuchSyncSetDefinitionException("Test", "NotKnown");
        try
        {
            realCloudSyncNodeTransportService.pushSyncChange(newSync, null);
            fail("Should have failed");
        }
        catch (NoSuchSyncSetDefinitionException e) {}
        
        // Test push with ContentLimitViolationException
        testCloudSyncNodeTransportService.fetchLocalException = new ContentLimitViolationException("Testing");
        try
        {
            realCloudSyncNodeTransportService.pushSyncChange(newSync, null);
            fail("Should have failed");
        }
        catch (SyncNodeException e)
        {
            assertEquals(SyncNodeExceptionType.CONTENT_LIMIT_VIOLATION, e.getExceptionType());
        }
        
        // Test push with Quota
        testCloudSyncNodeTransportService.fetchLocalException = new ContentQuotaException("Too Much");
        try
        {
            realCloudSyncNodeTransportService.pushSyncChange(newSync, null);
            fail("Should have failed");
        }
        catch (SyncNodeException e)
        {
            assertEquals(SyncNodeExceptionType.QUOTA_LIMIT_VIOLATION, e.getExceptionType());
        }
        
        // Test push with a few different kinds of SyncNodeException
        testCloudSyncNodeTransportService.fetchLocalException = new SyncNodeException(SyncNodeExceptionType.TARGET_NODE_ALREADY_SYNCED);
        try
        {
            realCloudSyncNodeTransportService.pushSyncChange(newSync, null);
            fail("Should have failed");
        }
        catch (SyncNodeException e)
        {
            assertEquals(SyncNodeExceptionType.TARGET_NODE_ALREADY_SYNCED, e.getExceptionType());
        }
        
        testCloudSyncNodeTransportService.fetchLocalException = new SyncNodeException(SyncNodeExceptionType.TARGET_FOLDER_NAME_CLASH);
        try
        {
            realCloudSyncNodeTransportService.pushSyncChange(newSync, null);
            fail("Should have failed");
        }
        catch (SyncNodeException e)
        {
            assertEquals(SyncNodeExceptionType.TARGET_FOLDER_NAME_CLASH, e.getExceptionType());
        }
        
        Exception unknownCause = new SiteDoesNotExistException("testing");
        testCloudSyncNodeTransportService.fetchLocalException = SyncNodeException.wrapUnhandledException(unknownCause);
        try
        {
            realCloudSyncNodeTransportService.pushSyncChange(newSync, null);
            fail("Should have failed");
        }
        catch (SyncNodeException e)
        {
            assertEquals(SyncNodeExceptionType.UNKNOWN, e.getExceptionType());
        }

        
        // ----------------------
        // Push exception checks
        // ----------------------

        // Test pull with NoSuchSyncSetDefinitionException
        testSyncService.fetchForPullException = new NoSuchSyncSetDefinitionException("Test", "NotKnown");
        try
        {
            realCloudSyncNodeTransportService.pullSyncChange(newSync, null);
            fail("Should have failed");
        }
        catch (NoSuchSyncSetDefinitionException e) {}
        
        // Test pull with a few different kinds of SyncNodeException
        testSyncService.fetchForPullException = new SyncNodeException(SyncNodeExceptionType.TARGET_NODE_ALREADY_SYNCED);
        try
        {
            realCloudSyncNodeTransportService.pullSyncChange(newSync, null);
            fail("Should have failed");
        }
        catch (SyncNodeException e)
        {
            assertEquals(SyncNodeExceptionType.TARGET_NODE_ALREADY_SYNCED, e.getExceptionType());
        }
        
        testSyncService.fetchForPullException = new SyncNodeException(SyncNodeExceptionType.TARGET_FOLDER_NAME_CLASH);
        try
        {
            realCloudSyncNodeTransportService.pullSyncChange(newSync, null);
            fail("Should have failed");
        }
        catch (SyncNodeException e)
        {
            assertEquals(SyncNodeExceptionType.TARGET_FOLDER_NAME_CLASH, e.getExceptionType());
        }
        testSyncService.reset();

        
        // -----------------------
        // Conflict Marking checks
        // -----------------------
        
        // Test conflict marking with NoSuchSyncSetDefinitionException
        testSyncService.dealWithConflictException = new NoSuchSyncSetDefinitionException("Test", "NotKnown");
        try
        {
            realCloudSyncNodeTransportService.pushConflictDetected(newSync, null);
            fail("Should have failed");
        }
        catch (NoSuchSyncSetDefinitionException e) {}
        
        // Test conflict marking with a few different kinds of SyncNodeException
        testSyncService.dealWithConflictException = new SyncNodeException(SyncNodeExceptionType.TARGET_NODE_ALREADY_SYNCED);
        try
        {
            realCloudSyncNodeTransportService.pushConflictDetected(newSync, null);
            fail("Should have failed");
        }
        catch (SyncNodeException e)
        {
            assertEquals(SyncNodeExceptionType.TARGET_NODE_ALREADY_SYNCED, e.getExceptionType());
        }
        
        testSyncService.dealWithConflictException = new SyncNodeException(SyncNodeExceptionType.TARGET_FOLDER_NAME_CLASH);
        try
        {
            realCloudSyncNodeTransportService.pushConflictDetected(newSync, null);
            fail("Should have failed");
        }
        catch (SyncNodeException e)
        {
            assertEquals(SyncNodeExceptionType.TARGET_FOLDER_NAME_CLASH, e.getExceptionType());
        }
        testSyncService.reset();
        

        // -----------------------
        // Delete/UnSync checks
        // -----------------------
        
        // Test delete hits conflict
        testCloudSyncNodeTransportService.isConflict = true;
        try
        {
            realCloudSyncNodeTransportService.pushSyncDelete(newSync, null);
            fail("Should have given a conflict");
        }
        catch (ConcurrentModificationException e) {}
        testCloudSyncNodeTransportService.isConflict = false;
        
        // Test unsync with NoSuchSyncSetDefinitionException
        testCloudSyncNodeTransportService.fetchLocalException = new NoSuchSyncSetDefinitionException("Test", "NotKnown");
        try
        {
            realCloudSyncNodeTransportService.pushUnSync(newSync, null);
            fail("Should have failed");
        }
        catch (NoSuchSyncSetDefinitionException e) {}
        
        // Test delete with ContentLimitViolationException
        testCloudSyncNodeTransportService.fetchLocalException = new ContentLimitViolationException("Testing");
        try
        {
            realCloudSyncNodeTransportService.pushSyncDelete(newSync, null);
            fail("Should have failed");
        }
        catch (SyncNodeException e)
        {
            assertEquals(SyncNodeExceptionType.CONTENT_LIMIT_VIOLATION, e.getExceptionType());
        }
        
        // Test unsync/delete with a few different kinds of SyncNodeException
        testCloudSyncNodeTransportService.fetchLocalException = new SyncNodeException(SyncNodeExceptionType.TARGET_NODE_ALREADY_SYNCED);
        try
        {
            realCloudSyncNodeTransportService.pushUnSync(newSync, null);
            fail("Should have failed");
        }
        catch (SyncNodeException e)
        {
            assertEquals(SyncNodeExceptionType.TARGET_NODE_ALREADY_SYNCED, e.getExceptionType());
        }
        
        testCloudSyncNodeTransportService.fetchLocalException = new SyncNodeException(SyncNodeExceptionType.TARGET_FOLDER_NAME_CLASH);
        try
        {
            realCloudSyncNodeTransportService.pushSyncDelete(newSync, null);
            fail("Should have failed");
        }
        catch (SyncNodeException e)
        {
            assertEquals(SyncNodeExceptionType.TARGET_FOLDER_NAME_CLASH, e.getExceptionType());
        }
        
        
        // -----------------------
        // Confirmation checks
        // -----------------------
        
        // Business logic doesn't currently support exceptions for this
        // So, no need to check about exceptions as none should be thrown
    }
    
    public void testSyncMustBeEnabledForTenant() throws Exception
    {
        // Set up some nodes and a SSD
        NodeRef sourceNode = createTestNode(SITE_ON_PREMISE, "testNode", "Test Node", "Stuff!!!");
        NodeRef destNode   = createTestNode(SITE_CLOUD, "testNode", "Test Node", "Stuff!!!");
        String syncSet = "testSyncSet";
        SyncNodeChangesInfoImpl newSync = new SyncNodeChangesInfoImpl(
                sourceNode, destNode, syncSet, nodeService.getType(sourceNode));
        newSync.setLocalParentNodeRef(SITE_ON_PREMISE.getNodeRef());
        newSync.setRemoteParentNodeRef(SITE_CLOUD.getNodeRef());
        
        SyncNodeChangesInfoImpl stub = new SyncNodeChangesInfoImpl(null, sourceNode, syncSet, ContentModel.TYPE_CONTENT);
        testSyncService.pullResult = stub;

        // Set some credentials
        cloudConnectorService.storeCloudCredentials(USER_THREE, PASSWORD);
        
        
        // Disable syncs for all
        AbstractRefreshableApplicationContext ctx = (AbstractRefreshableApplicationContext)getServer().getApplicationContext();
        SyncAdminServiceImpl realSyncAdminService = ctx.getBean("syncAdminService", SyncAdminServiceImpl.class);
        realSyncAdminService.setSyncEnabledForAllTenants(false);
        
        try
        {
            // Shouldn't be able to push
            try
            {
                realCloudSyncNodeTransportService.pushSyncChange(newSync, null);
                fail("Shouldn't be able to push if Sync is disabled for all tenants");
            }
            catch (AuthenticationException ae) {}
            
            
            // Shouldn't be able to pull
            try
            {
                realCloudSyncNodeTransportService.pullSyncChange(newSync, null);
                fail("Shouldn't be able to push if Sync is disabled for all tenants");
            }
            catch (AuthenticationException ae) {}

            
            // Shouldn't be able to confirm
            try
            {
                realCloudSyncNodeTransportService.confirmPull(new AuditToken[0], null);
                fail("Shouldn't be able to push if Sync is disabled for all tenants");
            }
            catch (AuthenticationException ae) {}
            
            
            // Can still unsync, no matter the network status
            realCloudSyncNodeTransportService.pushUnSync(newSync, null);
            
        // Restore
        } finally {
            realSyncAdminService.setSyncEnabledForAllTenants(true);
        }
    }
    
    /**
     * Test for fetching the list of SSDs which have changed on the far end
     */
    public void testPullChangedSSDs() throws Exception
    {
        // Mock what values to return
        List<String> ids = Arrays.asList(new String[] {"ssid1", "ssid2", "3ssid3"});
        when(testSyncAuditService.querySsdManifest(any(String.class), Matchers.anyInt())).
          thenReturn(ids);
        
        // Works with no credentials
        assertEquals(null, cloudConnectorService.getCloudCredentials());
        realCloudSyncSetDefinitionTransport.pullChangedSSDs("any");

        // Check we get back the right values
        List<String> fetched = realCloudSyncSetDefinitionTransport.pullChangedSSDs("anything");
        assertEquals(ids.size(), fetched.size());
        for (int i=0; i<ids.size(); i++)
        {
            assertEquals(ids.get(i), fetched.get(i));
        }
    }
    
    /**
     * Test for fetching the list of nodes which have changed in a given
     *  SSD, which will then need to be pulled.
     */
    public void testPullChangedNodesForSSD() throws Exception
    {
        // We can't fetch changes if we don't have any credentials
        assertEquals(null, cloudConnectorService.getCloudCredentials());
        try
        {
            realCloudSyncSetDefinitionTransport.pullChangedNodesForSSD("anySSID", null);
            fail("Shouldn't be able to fetch change list without credentials!");
        }
        catch(AuthenticationException e) {}
        
        // Set some credentials
        cloudConnectorService.storeCloudCredentials(USER_THREE, PASSWORD);
        
        
        // Mock the list to return
        List<SyncChangeEvent> events = new ArrayList<SyncChangeEvent>();
        Map<String, Serializable> v;
        
        v = new HashMap<String, Serializable>();
        v.put(SyncEventHandler.PATH_TO_NODEREF_KEY, createTestNode(SITE_CLOUD, "a1", "b", "c").toString());
        v.put(SyncEventHandler.PATH_TO_NODETYPE_KEY, ContentModel.TYPE_FOLDER);
        events.add(new SyncChangeEventImpl(1l, null, 1l, v));
        
        v = new HashMap<String, Serializable>();
        v.put(SyncEventHandler.PATH_TO_NODEREF_KEY, createTestNode(SITE_CLOUD, "a2", "b", "c").toString());
        events.add(new SyncChangeEventImpl(2l, null, 2l, v));
        v.put(SyncEventHandler.PATH_TO_NODETYPE_KEY, ContentModel.TYPE_CONTENT);
        
        v = new HashMap<String, Serializable>();
        v.put(SyncEventHandler.PATH_TO_NODEREF_KEY, createTestNode(SITE_CLOUD, "a3", "b", "c").toString());
        events.add(new SyncChangeEventImpl(3l, null, 3l, v));
        v.put(SyncEventHandler.PATH_TO_NODETYPE_KEY, ContentModel.TYPE_CONTENT);
        
        when(testSyncAuditService.queryBySsdId(any(String.class), Matchers.anyInt())).
           thenReturn(events);
        
        
        // Check we get the right values back
        List<NodeRef> fetched = realCloudSyncSetDefinitionTransport.pullChangedNodesForSSD("anySSD", null);
        assertEquals(events.size(), fetched.size());
        for (int i=0; i<events.size(); i++)
        {
            assertEquals(events.get(i).getNodeRef(), fetched.get(i));
        }
    }

    // ---------------------------------------------------------------------------------------------
    
    private static class TestCloudSyncMemberNodeTransport implements CloudSyncMemberNodeTransport
    {
        private static final NodeRef DUMMY_NEW_NODE_NODEREF = new NodeRef("test", "was", "added");
        private List<SyncNodeChangesInfo> toApply = new ArrayList<SyncNodeChangesInfo>();
        private List<SyncNodeChangesInfo> toUnSync = new ArrayList<SyncNodeChangesInfo>();
        private List<SyncNodeChangesInfo> toDelete = new ArrayList<SyncNodeChangesInfo>();
        private CloudSyncMemberNodeTransport realService;
        private boolean isConflict = false;
        private AlfrescoRuntimeException fetchLocalException = null;
        
        private TestCloudSyncMemberNodeTransport(CloudSyncMemberNodeTransport real)
        {
            this.realService = real;
        }
        private void reset()
        {
            toApply.clear();
            toUnSync.clear();
            toDelete.clear();
            isConflict = false;
            fetchLocalException = null;
        }

        @Override
        public NodeRef fetchLocalDetailsAndApply(SyncNodeChangesInfo syncNode, boolean isOnCloud) throws ConcurrentModificationException
        {
            if (isConflict) throw new ConcurrentModificationException("Testing Conflict");
            if (fetchLocalException != null) throw fetchLocalException;
            
            // Record
            toApply.add(syncNode);
            
            // Return a suitable noderef
            if (syncNode.getLocalNodeRef() == null)
            {
                return DUMMY_NEW_NODE_NODEREF;
            }
            return syncNode.getLocalNodeRef();
        }

        @Override
        public void fetchLocalDetailsAndUnSync(SyncNodeChangesInfo syncNode, boolean deleteOnUnSync)
                throws ConcurrentModificationException
        {
            if (isConflict) throw new ConcurrentModificationException("Testing Conflict");
            if (fetchLocalException != null) throw fetchLocalException;
            
            if (deleteOnUnSync)
                toDelete.add(syncNode);
            else
                toUnSync.add(syncNode);
        }

        @Override
        public CloudSyncContent decodeContent(FileItemStream contentPart)
                throws IOException
        {
            return realService.decodeContent(contentPart);
        }
        @Override
        public SyncNodeChangesInfo decodeMainJSON(FileItemStream jsonPart) throws IOException
        {
            return realService.decodeMainJSON(jsonPart);
        }
        @Override
        public SyncNodeChangesInfo decodePullParameters(WebScriptRequest request)
        {
            return realService.decodePullParameters(request);
        }
        @Override
        public MultipartRequestEntity encodeSyncChanges(SyncNodeChangesInfo syncNode)
        {
            return realService.encodeSyncChanges(syncNode);
        }

        @Override
        public SyncNodeChangesInfo pullSyncChange(SyncNodeChangesInfo stubLocal, String cloudNetwork)
                throws ConcurrentModificationException, AuthenticationException, RemoteSystemUnavailableException
        {
            throw new IllegalStateException("Should not be called!");
        }
        @Override
        public void pushSyncChange(SyncNodeChangesInfo syncNode, String cloudNetwork)
                throws ConcurrentModificationException, AuthenticationException, RemoteSystemUnavailableException
        {
            throw new IllegalStateException("Should not be called!");
        }
        @Override
        public void pushSyncDelete(SyncNodeChangesInfo syncNode, String cloudNetwork)
                throws ConcurrentModificationException, AuthenticationException, RemoteSystemUnavailableException
        {
            throw new IllegalStateException("Should not be called!");
        }
        @Override
        public NodeRef pushSyncInitial(SyncNodeChangesInfo syncNode, String cloudNetwork)
                throws AuthenticationException, RemoteSystemUnavailableException
        {
            throw new IllegalStateException("Should not be called!");
        }
        @Override
        public void pushUnSync(SyncNodeChangesInfo syncNode, String cloudNetwork)
                throws ConcurrentModificationException, AuthenticationException, RemoteSystemUnavailableException
        {
            throw new IllegalStateException("Should not be called!");
        }
        @Override
        public void confirmPull(AuditToken[] things, String cloudNetwork)
                throws AuthenticationException,
                RemoteSystemUnavailableException
        {
            throw new IllegalStateException("Should not be called!");
        }
        @Override
        public void pushConflictDetected(SyncNodeChangesInfo stubLocal,
                String cloudNetwork) throws AuthenticationException,
                RemoteSystemUnavailableException
        {
            throw new IllegalStateException("Should not be called!");
        }
    }
    
    private static class TestSyncService implements SyncService
    {
        private SyncNodeChangesInfo pullResult = null;
        private SyncNodeChangesInfo conflictValue = null;
        private AlfrescoRuntimeException fetchForPullException = null;
        private AlfrescoRuntimeException dealWithConflictException = null;
        
        private void reset()
        {
            pullResult = null;
            conflictValue = null;
            fetchForPullException = null;
            dealWithConflictException = null;
        }

        @Override
        public SyncNodeChangesInfo fetchForPull(SyncNodeChangesInfo stub)
        {
            if (fetchForPullException != null) throw fetchForPullException;
            return pullResult;
        }
        @Override
        public ConflictResponse dealWithConflictInAppropriateManner(SyncNodeChangesInfo conflict)
        {
            if (dealWithConflictException != null) throw dealWithConflictException;
            this.conflictValue = conflict;
            return new ConflictResponseImpl();
        }

        @Override
        public NodeRef create(SyncNodeChangesInfo newNode, boolean isOnCloud) throws FileExistsException
        {
            throw new IllegalStateException("Should not be called!");
        }
        @Override
        public void delete(SyncNodeChangesInfo changes, boolean force) throws ConcurrentModificationException
        {
            throw new IllegalStateException("Should not be called!");
        }
        @Override
        public void removeFromSyncSet(SyncNodeChangesInfo changes, boolean force)
                throws ConcurrentModificationException
        {
            throw new IllegalStateException("Should not be called!");
        }
        @Override
        public void update(SyncNodeChangesInfo change) throws ConcurrentModificationException
        {
            throw new IllegalStateException("Should not be called!");
        }
        
        @Override
        public void forceUpdate(SyncNodeChangesInfo change)
                throws SyncNodeException
        {
            throw new IllegalStateException("Should not be called!");
        }
        
        @Override
        public void requestSync(List<NodeRef> memberNodeRefs)
        {
            throw new IllegalStateException("Should not be called!");
        }
    }
    
    /**
     * Class we can use for testing the generic serializable transport
     */
    private static class TestSerializableValue implements Serializable
    {
        private static final long serialVersionUID = 1965321867996452336L;

        private int v1;
        private Long v2;
        private String v3;
        private TestSerializableValue(int v1, Long v2, String v3)
        {
            this.v1 = v1;
            this.v2 = v2;
            this.v3 = v3;
        }
        
        @Override
        public boolean equals(Object obj)
        {
            if (obj instanceof TestSerializableValue)
            {
                TestSerializableValue other = (TestSerializableValue)obj;
                if (v1 == other.v1 && v2.equals(other.v2) && v3.equals(other.v3))
                {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String toString()
        {
            return "Test: " + v1 + " - " + v2 + " - " + v3;
        }
    }
}
