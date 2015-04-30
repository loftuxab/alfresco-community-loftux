/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.web.scripts.sync;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.enterprise.repo.sync.SyncAdminService;
import org.alfresco.enterprise.repo.sync.SyncModel;
import org.alfresco.enterprise.repo.sync.SyncSetDefinition;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PersonService;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the controller for the remotesyncednode.get web script.
 * 
 * @author Neil Mc Erlean
 * @since 4.1
 */
public class RemoteSyncedNodeGet extends DeclarativeWebScript
// TODO Rename this webscript/class to something that better reflects its current use: SyncedNodeInfoGet?
{
    public static final String REMOTE_NETWORK_ID      = "remoteNetworkId";
    public static final String REMOTE_PARENT_NODE_REF = "remoteParentNodeRef";
    public static final String REMOTE_NODE_REF        = "remoteNodeRef";
    
    /**
     * The NodeRef of the local (on source) node which is the root of the synced node's syncset.
     * For an indirectly synced node, this will be the ancestor in the primary parent path which is directly synced.
     * For a directly synced node, this will be equal to the local synced node itself.
     */
    public static final String LOCAL_ROOT_NODE_REF        = "localRootNodeRef";
    public static final String LOCAL_ROOT_NODE_NAME       = "localRootNodeName";
    
    // Personal details of the sync set owner.
    public static final String SYNC_SET_OWNER_FIRST_NAME = "syncSetOwnerFirstName";
    public static final String SYNC_SET_OWNER_LAST_NAME  = "syncSetOwnerLastName";
    public static final String SYNC_SET_OWNER_USER_NAME  = "syncSetOwnerUserName";
    
    private NodeService      nodeService;
    private PersonService    personService;
    private SyncAdminService syncAdminService;
    private CheckOutCheckInService  cociService;
    
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    public void setPersonService(PersonService personService)
    {
        this.personService = personService;
    }
    
    public void setSyncAdminService(SyncAdminService syncAdminService)
    {
        this.syncAdminService = syncAdminService;
    }
    
    public void setCheckOutCheckInService(CheckOutCheckInService cociService)
    {
        this.cociService = cociService;
    }
    
    @Override protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        String noderefString = req.getParameter("nodeRef");
        
        // create the NodeRef and ensure it is valid
        if ( !NodeRef.isNodeRef(noderefString))
        {
            throw new WebScriptException(HttpServletResponse.SC_NOT_FOUND, "Illegal nodeRef: " + noderefString);
        }
        
        NodeRef requestedLocalNodeRef = new NodeRef(noderefString);
        
        if (!this.nodeService.exists(requestedLocalNodeRef))
        {
            throw new WebScriptException(HttpServletResponse.SC_NOT_FOUND, "Unable to find node: " + requestedLocalNodeRef.toString());
        }
        
        if ( !syncAdminService.isSyncSetMemberNode(requestedLocalNodeRef))
        {
            throw new WebScriptException(Status.STATUS_FORBIDDEN, "Specified NodeRef was not a sync set member node.");
        }
        
        // In case this is a working copy - return the original checked-out (note: will return the null if not a working copy)
        NodeRef origNodeRef = cociService.getCheckedOut(requestedLocalNodeRef);
        if (origNodeRef != null)
        {
            requestedLocalNodeRef = origNodeRef;
        }
        
        SyncSetDefinition ssd = syncAdminService.getSyncSetDefinition(requestedLocalNodeRef);
        
        final String remoteNetworkId = ssd.getRemoteTenantId();
        final String remoteNodeRef = (String) nodeService.getProperty(requestedLocalNodeRef, SyncModel.PROP_OTHER_NODEREF_STRING);
        
        String remoteParentNodeRef = null;
        final NodeRef rootNodeRef = syncAdminService.getRootNodeRef(requestedLocalNodeRef);
        
        if (syncAdminService.isDirectSyncSetMemberNode(requestedLocalNodeRef))
        {
            // The remote parent NodeRef for an immediately synced file (or folder) will be the targetFolderNodeRef of its SyncSetDefinition.
            // In other words, for a top-level or root node in a sync set.
            remoteParentNodeRef = ssd.getTargetFolderNodeRef();
        }
        else
        {
            // For files that are in either a folder with immediate children synced or files that are anywhere underneath a deeply synced
            // folder, we will get the remoteParentNodeRef by getting the On Premise node's local parent and returning that node's remoteNodeRef.
            NodeRef localPrimaryParent = nodeService.getPrimaryParent(requestedLocalNodeRef).getParentRef();
            
            // We know the local node is an indirect member of a syncset, so in normal circumstances we would
            // always expect its parent to also be a member of the same syncset.
            if (syncAdminService.isSyncSetMemberNode(localPrimaryParent, ssd))
            {
                remoteParentNodeRef = (String) nodeService.getProperty(localPrimaryParent, SyncModel.PROP_OTHER_NODEREF_STRING);
            }
        }
        
        Map<String, Object> model = new HashMap<String, Object>();
        model.put(REMOTE_NETWORK_ID, remoteNetworkId);
        
        if (remoteNodeRef != null)
        {
            model.put(REMOTE_NODE_REF, remoteNodeRef);
        }
        
        if (remoteParentNodeRef != null)
        {
            model.put(REMOTE_PARENT_NODE_REF, remoteParentNodeRef);
        }
        
        if (rootNodeRef != null)
        {
            String nodeName = (String) nodeService.getProperty(rootNodeRef, ContentModel.PROP_NAME);
            
            model.put(LOCAL_ROOT_NODE_NAME, nodeName);
            model.put(LOCAL_ROOT_NODE_REF, rootNodeRef.toString());
        }
        
        final String syncSetOwner = ssd.getSyncCreator();
        if (syncSetOwner != null)
        {
            model.put(SYNC_SET_OWNER_USER_NAME, syncSetOwner);
            if (personService.personExists(syncSetOwner))
            {
                NodeRef personNode = personService.getPerson(syncSetOwner, false);
                String firstName = (String) nodeService.getProperty(personNode, ContentModel.PROP_FIRSTNAME);
                String lastName = (String) nodeService.getProperty(personNode, ContentModel.PROP_LASTNAME);
                
                model.put(SYNC_SET_OWNER_FIRST_NAME, firstName);
                model.put(SYNC_SET_OWNER_LAST_NAME, lastName);
            }
        }
        
        return model;
    }
}