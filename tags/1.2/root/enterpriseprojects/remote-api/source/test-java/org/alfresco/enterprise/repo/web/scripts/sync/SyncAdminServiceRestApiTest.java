/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.web.scripts.sync;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.enterprise.repo.sync.SyncAdminService;
import org.alfresco.enterprise.repo.sync.SyncAdminServiceImpl;
import org.alfresco.enterprise.repo.sync.SyncModel;
import org.alfresco.enterprise.repo.sync.SyncServiceImpl;
import org.alfresco.enterprise.repo.sync.SyncSetDefinition;
import org.alfresco.enterprise.repo.sync.transport.CloudSyncSetDefinitionTransport;
import org.alfresco.enterprise.repo.sync.transport.impl.CloudSyncSetDefinitionTransportImpl;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.archive.NodeArchiveService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.collections.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.extensions.webscripts.TestWebScriptServer.DeleteRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.GetRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.PostRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.Response;

/**
 * This class tests the ReST API of the {@link SyncAdminService}.
 * 
 * @author Neil Mc Erlean
 * @since CloudSync
 */
public class SyncAdminServiceRestApiTest extends BaseSyncServiceRestApiTest_
{
    // Miscellaneous constants used throughout this test class.
    private static final String GET_SSD_URL_FORMAT = "/enterprise/sync/syncsetdefinitions/{1}";
    private static final String POST_SSD_URL_FORMAT = "/enterprise/sync/syncsetdefinitions";
    private static final String DELETE_SSMN_URL_FORMAT = "/enterprise/sync/syncsetmembers/{nodeRef}";
    
    private static final String POST_REMOTE_SSD_URL_FORMAT = "/enterprise/sync/remotesyncsetdefinitions";
    
    
    private CloudSyncSetDefinitionTransport syncSsdTransport;
    NodeArchiveService archiveService;
    
    private SyncSetDefinition precreatedSsd;
    private List<NodeRef> syncedNodes;
    
    private List<NodeRef> unsyncedNodes;
    
    /** This is a normal, local folder 'pretending' to be a target folder on the Cloud.  */
    private NodeRef targetFolderOnCloud;
    
    @Override protected void setUp() throws Exception
    {
        super.setUp();
        
        syncSsdTransport  = getServer().getApplicationContext().getBean("cloudSyncSetDefinitionTransport", CloudSyncSetDefinitionTransport.class);
        archiveService = (NodeArchiveService) getServer().getApplicationContext().getBean("nodeArchiveService", NodeArchiveService.class);
        
        // Wire in our mock/testing cloud connector
        ((SyncAdminServiceImpl)syncAdminService).setCloudConnectorService(MOCK_CLOUD_CONNECTOR_SERVICE);
        ((CloudSyncSetDefinitionTransportImpl)syncSsdTransport).setCloudConnectorService(MOCK_CLOUD_CONNECTOR_SERVICE);
        for (AbstractCloudSyncDeclarativeWebScript webscript : getServer().getApplicationContext().getBeansOfType(AbstractCloudSyncDeclarativeWebScript.class).values())
        {
            webscript.setCloudConnectorService(MOCK_CLOUD_CONNECTOR_SERVICE);
        }
        
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        
        unsyncedNodes = createNodes();
        syncedNodes = createNodes();
        
        targetFolderOnCloud = createFolderNode();
        
        precreatedSsd = transactionHelper.doInTransaction(new RetryingTransactionCallback<SyncSetDefinition>()
        {
            public SyncSetDefinition execute() throws Throwable
            {
                SyncSetDefinition ssd = syncAdminService.createSourceSyncSet(syncedNodes, "remoteTenant", "cloud://node/Ref", false, false, true);
                nodesToDeleteAfterTest.add(ssd.getNodeRef());
                
                assertNotNull(ssd);
                assertNotNull(ssd.getId());
                return ssd;
            }
        });
    }
    
    private NodeRef createFolderNode()
    {
        return transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {
                final NodeRef nodeRef = nodeService.createNode(repositoryHelper.getCompanyHome(),
                                                                ContentModel.ASSOC_CONTAINS, ContentModel.ASSOC_CONTAINS,
                                                                ContentModel.TYPE_FOLDER, null).getChildRef();
                
                nodesToDeleteAfterTest.add(nodeRef);
                
                return nodeRef;
            }
        });
    }
    
    public void testGetNonExistentLocalSyncSetDefinition() throws Exception
    {
        String url = GET_SSD_URL_FORMAT.replace("{1}", "squiggle");
        
        sendRequest(new GetRequest(url), 404);
    }
    
    @SuppressWarnings("unchecked")
    public void testCreateThenGetLocalSyncSetDefinition() throws Exception
    {
        JSONArray memberNodeRefs = new JSONArray();
        memberNodeRefs.addAll(CollectionUtils.toListOfStrings(unsyncedNodes));
        
        JSONObject obj = new JSONObject();
        obj.put(SyncSetDefinitionPost.PARAM_REMOTE_USER_NAME,      "clouduser@alfresco.com");
        obj.put(SyncSetDefinitionPost.PARAM_REMOTE_PASSWORD,       "password");
        obj.put(SyncSetDefinitionPost.PARAM_MEMBER_NODEREFS,       memberNodeRefs);
        obj.put(SyncSetDefinitionPost.PARAM_REMOTE_TENANT_ID,      "alfresco.com");
        obj.put(SyncSetDefinitionPost.PARAM_TARGET_FOLDER_NODEREF, "target://folder/NodeRef");
        obj.put(SyncSetDefinitionPost.PARAM_LOCK_SOURCE_COPY,      false);
        
        StringWriter stringWriter = new StringWriter();
        obj.writeJSONString(stringWriter);
        String jsonString = stringWriter.toString();
        
        // REST call to create a new SSD
        Response rsp = sendRequest(new PostRequest(POST_SSD_URL_FORMAT, jsonString, "application/json"), 200);
        
        String contentAsString = rsp.getContentAsString();
        
        JSONObject postRsp = checkSsdJsonRsp(contentAsString);
        
        // Ensure the SSD is cleaned up after the test.
        NodeRef newSsd = new NodeRef((String) postRsp.get("nodeRef"));
        nodesToDeleteAfterTest.add(newSsd);
        
        // Now get the ssd by its id & assert it matches.
        String getUrl = GET_SSD_URL_FORMAT.replace("{1}", (String) postRsp.get("id"));
        rsp = sendRequest(new GetRequest(getUrl), 200);
        
        contentAsString = rsp.getContentAsString();
        checkSsdJsonRsp(contentAsString);
    }
    
    @SuppressWarnings("unchecked")
    public void testCreateThenGetCloudSyncSetDefinition() throws Exception
    {
        final String onPremiseSsdId = "OnPremiseSsdId";
        
        JSONObject obj = new JSONObject();
        obj.put(SyncServiceImpl.PARAM_SSD_ID, onPremiseSsdId);
        obj.put(SyncServiceImpl.PARAM_SOURCE_REPO_ID, onPremiseSsdId);
        obj.put(SyncServiceImpl.PARAM_TARGET_FOLDER_NODEREF, targetFolderOnCloud.toString());
        
        StringWriter stringWriter = new StringWriter();
        obj.writeJSONString(stringWriter);
        String jsonString = stringWriter.toString();
        
        // REST call to create a new SSD
        Response rsp = sendRequest(new PostRequest(POST_REMOTE_SSD_URL_FORMAT, jsonString, "application/json"), 200);
        
        String contentAsString = rsp.getContentAsString();
        JSONObject jsonRsp = (JSONObject) JSONValue.parse(contentAsString);
        
        // Ensure the SSD is cleaned up after the test.
        NodeRef newSsd = new NodeRef((String) jsonRsp.get("nodeRef"));
        nodesToDeleteAfterTest.add(newSsd);
        
        // TODO Could do more validation here - for now we're checking it's created without exception.
    }
    
    private JSONObject checkSsdJsonRsp(String contentAsString)
    {
        JSONObject jsonRsp = (JSONObject) JSONValue.parse(contentAsString);
        assertNotNull("Problem reading JSON", jsonRsp);
        
        assertNotNull("SSD id was null.", jsonRsp.get("id"));
        final String ssdNodeRefString = (String) jsonRsp.get("nodeRef");
        assertNotNull("SSD NodeRef was null.", ssdNodeRefString);
        NodeRef reconstitutedSsdNodeRef = new NodeRef(ssdNodeRefString);
        assertTrue("SSD NodeRef didn't exist.", nodeService.exists(reconstitutedSsdNodeRef));
        assertEquals("SSD NodeRef had wrong type.", SyncModel.TYPE_SYNC_SET_DEFINITION, nodeService.getType(reconstitutedSsdNodeRef));
        assertEquals("SSD NodeRef had wrong ID.", jsonRsp.get("id"), nodeService.getProperty(reconstitutedSsdNodeRef, SyncModel.PROP_SYNC_GUID));
        assertEquals("SSD isLocked was wrong", Boolean.FALSE, jsonRsp.get("sourceCopyLocked"));
        assertNotNull("SSD remote tenant ID was null.", jsonRsp.get("remoteTenantId"));
        assertNotNull("SSD remote target Folder was null.", jsonRsp.get("remoteTargetFolderNodeRef"));
        assertEquals("SSD isDeleteOnCloud was wrong", Boolean.TRUE, jsonRsp.get("isDeleteOnCloud"));
        assertEquals("SSD isDeleteOnPrem was wrong", Boolean.FALSE, jsonRsp.get("isDeleteOnPrem"));
        
        return jsonRsp;
    }
    
    public void testRemoveLocalMemberNode() throws Exception
    {
        String url = DELETE_SSMN_URL_FORMAT.replace("{nodeRef}",
                                                    syncedNodes.get(0).toString().replace("://", "/"));
        sendRequest(new DeleteRequest(url), 200);
    }
    
    public void testMNT11574() throws Exception
    {
    	AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
    	
    	List<NodeRef> nodeRefs = new ArrayList<NodeRef>();
    	
    	final NodeRef nodeRef = transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {
                final NodeRef nodeRef = nodeService.createNode(repositoryHelper.getCompanyHome(),
                        ContentModel.ASSOC_CONTAINS, ContentModel.ASSOC_CONTAINS,
                        ContentModel.TYPE_CONTENT, null).getChildRef();
                
                return nodeRef;
            }
        });

    	nodeRefs.add(nodeRef);
    	JSONArray memberNodeRefs = new JSONArray();
        memberNodeRefs.addAll(CollectionUtils.toListOfStrings(nodeRefs));
        
        JSONObject obj = new JSONObject();
        obj.put(SyncSetDefinitionPost.PARAM_REMOTE_USER_NAME,      "clouduser@alfresco.com");
        obj.put(SyncSetDefinitionPost.PARAM_REMOTE_PASSWORD,       "password");
        obj.put(SyncSetDefinitionPost.PARAM_MEMBER_NODEREFS,       memberNodeRefs);
        obj.put(SyncSetDefinitionPost.PARAM_REMOTE_TENANT_ID,      "alfresco.com");
        obj.put(SyncSetDefinitionPost.PARAM_TARGET_FOLDER_NODEREF, "target://folder/NodeRef");
        obj.put(SyncSetDefinitionPost.PARAM_LOCK_SOURCE_COPY,      false);
        
        StringWriter stringWriter = new StringWriter();
        obj.writeJSONString(stringWriter);
        String jsonString = stringWriter.toString();
        
        // REST call to create a new SSD
        Response rsp = sendRequest(new PostRequest(POST_SSD_URL_FORMAT, jsonString, "application/json"), 200);
        
        String contentAsString = rsp.getContentAsString();
        
        JSONObject postRsp = checkSsdJsonRsp(contentAsString);
        
        // Ensure the SSD is cleaned up after the test.
        NodeRef newSsd = new NodeRef((String) postRsp.get("nodeRef"));
        
        nodeService.deleteNode(nodeRef);
        
        NodeRef archivedNode = archiveService.getArchivedNode(nodeRef);
        
        assertFalse(nodeService.hasAspect(archivedNode, SyncModel.ASPECT_SYNC_FAILED));
        assertFalse(nodeService.hasAspect(archivedNode, SyncModel.ASPECT_SYNC_SET_MEMBER_NODE));
        assertFalse(nodeService.hasAspect(archivedNode, SyncModel.ASPECT_SYNCED));
        
    }
}
