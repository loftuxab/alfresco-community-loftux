/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.web.scripts.sync;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.enterprise.repo.sync.SyncModel;
import org.alfresco.enterprise.repo.web.scripts.BaseEnterpriseWebScriptTest;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.extensions.webscripts.TestWebScriptServer.GetRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.Response;

/**
 * This class tests the webscript {@link RemoteSyncedNodeGet}.
 * 
 * @author Neil Mc Erlean
 * @since CloudSync
 */
// It may test other webscripts as we develop them.
public class RemoteSyncedNodeRestApiTest extends BaseEnterpriseWebScriptTest
{
    // Miscellaneous constants used throughout this test class.
    private static final String GET_REMOTE_SYNCED_NODE_URL = "/enterprise/sync/remotesyncednode?nodeRef={}";
    
    // Injected services
    private NodeService               nodeService;
    private Repository                repositoryHelper;
    private RetryingTransactionHelper transactionHelper;
    
    private NodeRef ssdNodeRef, unsyncedNodeRef, partiallySyncedNodeRef, fullySyncedNodeRef, indirectlySyncedNodeRef;
    private String ssdGuid, sourceRepoId, remoteTargetNetwork, remoteNodeRef, remoteTargetFolder, remoteParentFolder;
    
    @Override protected void setUp() throws Exception
    {
        // We don't need to create real SyncSetDefinitions in order to test this webscript.
        // And indeed, they introduce complexity, such as quartz jobs etc. So therefore we'll cheat and manually create nodes of the correct type.
        super.setUp();
        
        nodeService       = getServer().getApplicationContext().getBean("NodeService", NodeService.class);
        repositoryHelper  = getServer().getApplicationContext().getBean("repositoryHelper", Repository.class);
        transactionHelper = getServer().getApplicationContext().getBean("retryingTransactionHelper", RetryingTransactionHelper.class);
        
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        
        // 0. Create a node of type SyncSetDefinition. (Although it will not be in the correct folder and therefore
        //      should not function as a proper SSD, which is what we want).
        final NodeRef finalSsdNodeRef = transactionHelper.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            @Override public NodeRef execute() throws Throwable
            {
                final NodeRef companyHome = repositoryHelper.getCompanyHome();
                
                final Map<QName, Serializable> ssdProps = new HashMap<QName, Serializable>();
                
                ssdGuid = RemoteSyncedNodeRestApiTest.class.getSimpleName() + "_syncGuid";
                sourceRepoId = RemoteSyncedNodeRestApiTest.class.getSimpleName() + "_sourceRepoId";
                remoteTargetNetwork = RemoteSyncedNodeRestApiTest.class.getSimpleName() + "_targetNetwork";
                remoteTargetFolder = RemoteSyncedNodeRestApiTest.class.getSimpleName() + "_targetFolder";
                
                ssdProps.put(SyncModel.PROP_SYNC_GUID, ssdGuid);
                ssdProps.put(SyncModel.PROP_SOURCE_REPO_ID, sourceRepoId);
                ssdProps.put(SyncModel.PROP_TARGET_NETWORK_ID, remoteTargetNetwork);
                ssdProps.put(SyncModel.PROP_TARGET_ROOT_FOLDER, remoteTargetFolder);
                ssdProps.put(SyncModel.PROP_SYNC_SET_IS_LOCKED_ON_PREMISE, false);
                
                return nodeService.createNode(companyHome,
                        ContentModel.ASSOC_CONTAINS, ContentModel.ASSOC_CONTAINS,
                        SyncModel.TYPE_SYNC_SET_DEFINITION, ssdProps).getChildRef();
            }
        });
        this.ssdNodeRef = finalSsdNodeRef;
        
        // 1. Create a node representing a normal node unrelated to sync in any way.
        unsyncedNodeRef = transactionHelper.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            @Override public NodeRef execute() throws Throwable
            {
                final NodeRef companyHome = repositoryHelper.getCompanyHome();
                
                return nodeService.createNode(companyHome,
                        ContentModel.ASSOC_CONTAINS, ContentModel.ASSOC_CONTAINS,
                        ContentModel.TYPE_CONTENT, null).getChildRef();
            }
        });
        
        // 2. Create a node representing an SSMN that is as yet unsynced.
        partiallySyncedNodeRef = transactionHelper.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            @Override public NodeRef execute() throws Throwable
            {
                final NodeRef companyHome = repositoryHelper.getCompanyHome();
                
                final NodeRef result = nodeService.createNode(companyHome,
                        ContentModel.ASSOC_CONTAINS, ContentModel.ASSOC_CONTAINS,
                        ContentModel.TYPE_CONTENT, null).getChildRef();
                
                Map<QName, Serializable> ssmnProps = new HashMap<QName, Serializable>();
                ssmnProps.put(SyncModel.PROP_DIRECT_SYNC, true);
                
                nodeService.addAspect(result, SyncModel.ASPECT_SYNC_SET_MEMBER_NODE, ssmnProps);
                nodeService.createAssociation(finalSsdNodeRef, result, SyncModel.ASSOC_SYNC_MEMBERS);
                
                return result;
            }
        });
        
        // 3. Create a node representing an SSMN that is fully synced.
        fullySyncedNodeRef = transactionHelper.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            @Override public NodeRef execute() throws Throwable
            {
                final NodeRef companyHome = repositoryHelper.getCompanyHome();
                
                final NodeRef result = nodeService.createNode(companyHome,
                        ContentModel.ASSOC_CONTAINS, ContentModel.ASSOC_CONTAINS,
                        ContentModel.TYPE_CONTENT, null).getChildRef();
                
                remoteNodeRef = RemoteSyncedNodeRestApiTest.class.getSimpleName() + "_otherNodeRefString";
                
                Map<QName, Serializable> ssmnProps = new HashMap<QName, Serializable>();
                ssmnProps.put(SyncModel.PROP_OTHER_NODEREF_STRING, remoteNodeRef);
                ssmnProps.put(SyncModel.PROP_DIRECT_SYNC, true);
                
                nodeService.addAspect(result, SyncModel.ASPECT_SYNC_SET_MEMBER_NODE, ssmnProps);
                nodeService.createAssociation(finalSsdNodeRef, result, SyncModel.ASSOC_SYNC_MEMBERS);
                
                return result;
            }
        });
        
        // 4. Create a node representing an SSMN that is fully synced, albeit indirectly due to its parent folder.
        indirectlySyncedNodeRef = transactionHelper.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            @Override public NodeRef execute() throws Throwable
            {
                final NodeRef companyHome = repositoryHelper.getCompanyHome();
                
                final NodeRef folder = nodeService.createNode(companyHome,
                        ContentModel.ASSOC_CONTAINS, ContentModel.ASSOC_CONTAINS,
                        ContentModel.TYPE_FOLDER, null).getChildRef();
                final NodeRef result = nodeService.createNode(folder,
                        ContentModel.ASSOC_CONTAINS, ContentModel.ASSOC_CONTAINS,
                        ContentModel.TYPE_CONTENT, null).getChildRef();
                
                remoteNodeRef = RemoteSyncedNodeRestApiTest.class.getSimpleName() + "_otherNodeRefString";
                remoteParentFolder = RemoteSyncedNodeRestApiTest.class.getSimpleName() + "_remoteParentFolderNodeRefString";
                
                Map<QName, Serializable> ssmnFolderProps = new HashMap<QName, Serializable>();
                ssmnFolderProps.put(SyncModel.PROP_OTHER_NODEREF_STRING, remoteParentFolder);
                ssmnFolderProps.put(SyncModel.PROP_DIRECT_SYNC, true);
                
                nodeService.addAspect(folder, SyncModel.ASPECT_SYNC_SET_MEMBER_NODE, ssmnFolderProps);
                nodeService.createAssociation(finalSsdNodeRef, folder, SyncModel.ASSOC_SYNC_MEMBERS);
                
                Map<QName, Serializable> ssmnFileProps = new HashMap<QName, Serializable>();
                ssmnFileProps.put(SyncModel.PROP_OTHER_NODEREF_STRING, remoteNodeRef);
                ssmnFileProps.put(SyncModel.PROP_DIRECT_SYNC, false);
                
                nodeService.addAspect(result, SyncModel.ASPECT_SYNC_SET_MEMBER_NODE, ssmnFileProps);
                nodeService.createAssociation(finalSsdNodeRef, result, SyncModel.ASSOC_SYNC_MEMBERS);
                
                return result;
            }
        });
    }
    
    @Override protected void tearDown() throws Exception
    {
        transactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                for (NodeRef nodeRef : new NodeRef[] {ssdNodeRef, unsyncedNodeRef, partiallySyncedNodeRef, fullySyncedNodeRef})
                {
                    if (nodeService.exists(nodeRef))
                    {
                        nodeService.deleteNode(nodeRef);
                    }
                }
                return null;
            }
        });
    }
    
    public void testGetNonExistentLocalSyncSetDefinition() throws Exception
    {
        String url = GET_REMOTE_SYNCED_NODE_URL.replace("{}", "squiggle://squiggle/squiggle");
        sendRequest(new GetRequest(url), 404);
    }
    
    public void testGetRemoteSyncedNodeForUnsyncedLocalNode() throws Exception
    {
        String url = getUrlForLocalNode(unsyncedNodeRef);
        
        // REST call
        sendRequest(new GetRequest(url), 403);
    }
    
    public void testGetRemoteSyncedNodeForPartiallySyncedLocalNode() throws Exception
    {
        String url = getUrlForLocalNode(partiallySyncedNodeRef);
        
        // REST call
        Response rsp = sendRequest(new GetRequest(url), 200);
        
        String contentAsString = rsp.getContentAsString();
        
        JSONObject jsonRsp = (JSONObject) JSONValue.parseWithException(contentAsString);
        
        assertNull(jsonRsp.get(RemoteSyncedNodeGet.REMOTE_NODE_REF));
        assertEquals(remoteTargetNetwork, jsonRsp.get(RemoteSyncedNodeGet.REMOTE_NETWORK_ID));
        assertEquals(remoteTargetFolder,  jsonRsp.get(RemoteSyncedNodeGet.REMOTE_PARENT_NODE_REF));
    }
    
    public void testGetRemoteSyncedNodeForFullySyncedLocalNode() throws Exception
    {
        String url = getUrlForLocalNode(fullySyncedNodeRef);
        
        // REST call
        Response rsp = sendRequest(new GetRequest(url), 200);
        
        String contentAsString = rsp.getContentAsString();
        
        JSONObject jsonRsp = (JSONObject) JSONValue.parseWithException(contentAsString);
        
        assertEquals(remoteNodeRef,       jsonRsp.get(RemoteSyncedNodeGet.REMOTE_NODE_REF));
        assertEquals(remoteTargetNetwork, jsonRsp.get(RemoteSyncedNodeGet.REMOTE_NETWORK_ID));
        assertEquals(remoteTargetFolder,  jsonRsp.get(RemoteSyncedNodeGet.REMOTE_PARENT_NODE_REF));
        assertEquals(fullySyncedNodeRef.toString(),  jsonRsp.get(RemoteSyncedNodeGet.LOCAL_ROOT_NODE_REF));
        assertEquals(StringUtils.substringAfterLast(fullySyncedNodeRef.toString(), "/"), jsonRsp.get(RemoteSyncedNodeGet.LOCAL_ROOT_NODE_NAME));
        
        assertEquals("Administrator",  jsonRsp.get(RemoteSyncedNodeGet.SYNC_SET_OWNER_FIRST_NAME));
        assertEquals("",               jsonRsp.get(RemoteSyncedNodeGet.SYNC_SET_OWNER_LAST_NAME));
        assertEquals(AuthenticationUtil.getAdminUserName(), jsonRsp.get(RemoteSyncedNodeGet.SYNC_SET_OWNER_USER_NAME));
    }
    
    public void testGetRemoteSyncedNodeForIndirectlySyncedLocalNode() throws Exception
    {
        String url = getUrlForLocalNode(indirectlySyncedNodeRef);
        
        // REST call
        Response rsp = sendRequest(new GetRequest(url), 200);
        
        String contentAsString = rsp.getContentAsString();
        
        JSONObject jsonRsp = (JSONObject) JSONValue.parseWithException(contentAsString);
        
        assertEquals(remoteNodeRef,       jsonRsp.get(RemoteSyncedNodeGet.REMOTE_NODE_REF));
        assertEquals(remoteTargetNetwork, jsonRsp.get(RemoteSyncedNodeGet.REMOTE_NETWORK_ID));
        assertEquals(remoteParentFolder,  jsonRsp.get(RemoteSyncedNodeGet.REMOTE_PARENT_NODE_REF));
    }
    
    private String getUrlForLocalNode(NodeRef nodeRef)
    {
        String result = GET_REMOTE_SYNCED_NODE_URL.replace("{}", String.valueOf(nodeRef));
        return result;
    }
}
