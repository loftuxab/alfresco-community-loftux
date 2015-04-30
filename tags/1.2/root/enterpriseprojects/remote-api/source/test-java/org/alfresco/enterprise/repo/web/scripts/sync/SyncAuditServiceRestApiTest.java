/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.web.scripts.sync;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.alfresco.enterprise.repo.sync.SyncAdminServiceImpl;
import org.alfresco.enterprise.repo.sync.SyncSetDefinition;
import org.alfresco.enterprise.repo.sync.audit.SyncAuditService;
import org.alfresco.enterprise.repo.sync.transport.CloudSyncSetDefinitionTransport;
import org.alfresco.enterprise.repo.sync.transport.impl.CloudSyncSetDefinitionTransportImpl;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.NodeRef;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.springframework.extensions.webscripts.TestWebScriptServer.GetRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.Response;

/**
 * This class tests the ReST API of the {@link SyncAuditService}.
 * 
 * @author janv
 * @since CloudSync
 */
public class SyncAuditServiceRestApiTest extends BaseSyncServiceRestApiTest_
{
    // Miscellaneous constants
    private static final String GET_SS_MANIFEST_URL_FORMAT = "/enterprise/sync/syncsetmanifest?srcRepoId={1}";
    private static final String GET_SS_CHANGES_URL_FORMAT = "/enterprise/sync/syncsetchanges?ssdId={1}";
    
    private CloudSyncSetDefinitionTransport syncSsdTransport;
    
    private SyncSetDefinition precreatedSsd;
    private List<NodeRef> syncedNodes;
    
    @Override protected void setUp() throws Exception
    {
        super.setUp();
        
        syncSsdTransport  = getServer().getApplicationContext().getBean("cloudSyncSetDefinitionTransport", CloudSyncSetDefinitionTransport.class);
        
        // Wire in our mock/testing cloud connector
        ((SyncAdminServiceImpl)syncAdminService).setCloudConnectorService(MOCK_CLOUD_CONNECTOR_SERVICE);
        ((CloudSyncSetDefinitionTransportImpl)syncSsdTransport).setCloudConnectorService(MOCK_CLOUD_CONNECTOR_SERVICE);
        for (AbstractCloudSyncDeclarativeWebScript webscript : getServer().getApplicationContext().getBeansOfType(AbstractCloudSyncDeclarativeWebScript.class).values())
        {
            webscript.setCloudConnectorService(MOCK_CLOUD_CONNECTOR_SERVICE);
        }
        
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        
        syncedNodes = createNodes();
        
        precreatedSsd = transactionHelper.doInTransaction(new RetryingTransactionCallback<SyncSetDefinition>()
        {
            public SyncSetDefinition execute() throws Throwable
            {
                SyncSetDefinition ssd = syncAdminService.createSourceSyncSet(syncedNodes, 
                		"remoteTenant", 
                		"cloud://node/Ref", 
                		false, true, false);
                
                assertNotNull(ssd);
                assertNotNull(ssd.getId());
                return ssd;
            }
        });
    }
    
    @Override protected void tearDown() throws Exception
    {
        NodeRef nodeRef = precreatedSsd.getNodeRef();
        nodesToDeleteAfterTest.add(nodeRef);
        
        super.tearDown();
    }
    
    // note: currently equivalent response to an existing source repo id with no outstanding sync changes
    public void testGetSyncSetManifestForNonExistentSourceRepoId() throws Exception
    {
        List<String> syncSetManifest = getSyncSetManifest("squiggle");
        assertEquals("List should be empty", 0, syncSetManifest.size());
    }
    
    public void testGetSyncSetManifest() throws Exception
    {
        List<String> syncSetManifest = getSyncSetManifest(srcRepoId);
        assertTrue("List should not be empty", syncSetManifest.size() > 0);
        
        assertTrue("syncSetManifest does not include expected syncSetId", syncSetManifest.contains(precreatedSsd.getId()));
    }
    
    @SuppressWarnings("unchecked")
    private List<String> getSyncSetManifest(String srcRepoId) throws Exception
    {
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        
        String url = GET_SS_MANIFEST_URL_FORMAT.replace("{1}", srcRepoId);
        Response rsp = sendRequest(new GetRequest(url), 200);
        
        String contentAsString = rsp.getContentAsString();
        JSONArray jsonRsp = (JSONArray) JSONValue.parse(contentAsString);
        
        return (List<String>)jsonRsp;
    }
    
    public void testGetSyncSetChangesForNonExistentSsdId() throws Exception
    {
        List<NodeRef> syncSetChanges = getSyncSetChanges("squiggle");
        assertEquals("List should be empty", 0, syncSetChanges.size());
    }
    
    private List<NodeRef> getSyncSetChanges(String ssdId) throws Exception
    {
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        
        String url = GET_SS_CHANGES_URL_FORMAT.replace("{1}", ssdId);
        Response rsp = sendRequest(new GetRequest(url), 200);
        
        String contentAsString = rsp.getContentAsString();
        JSONArray jsonRsp = (JSONArray) JSONValue.parse(contentAsString);
        
        List<NodeRef> changedNodeRefs = new ArrayList<NodeRef>(jsonRsp.size());
        
        @SuppressWarnings("unchecked")
        Iterator<String> itr = jsonRsp.iterator();
        while (itr.hasNext())
        {
            changedNodeRefs.add(new NodeRef(itr.next()));
        }
        
        return (List<NodeRef>)changedNodeRefs;
    }
    
    public void testGetSyncSetChanges() throws Exception
    {
        List<String> syncSetManifest = getSyncSetManifest(srcRepoId);
        
        for (String ssdId : syncSetManifest)
        {
            List<NodeRef> syncSetChanges = getSyncSetChanges(ssdId);
            assertTrue("List should not be empty", syncSetChanges.size() > 0);
            
            if (ssdId.equals(precreatedSsd.getId()))
            {
                for (NodeRef nodeRef : nodesToDeleteAfterTest)
                {
                    assertTrue("syncSetChanges does not include expected nodeRef", syncSetChanges.contains(nodeRef));
                }
            }
        }
    }
}
