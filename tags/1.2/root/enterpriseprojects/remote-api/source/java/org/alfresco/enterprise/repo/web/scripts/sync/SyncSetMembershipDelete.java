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

import org.alfresco.enterprise.repo.sync.SyncSetDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the SOURCE controller for the syncsetmembership.delete web script => Unsync
 * 
 * @author Neil Mc Erlean, janv
 * @since 4.1
 */
public class SyncSetMembershipDelete extends AbstractCloudSyncDeclarativeWebScript
{
    @Override protected Map<String, Object> executeSyncImpl(WebScriptRequest req, Status status, Cache cache)
    {
        NodeRef nodeRef = parseRequestForNodeRef(req);
        
        boolean deleteRemote = false;
        String deleteRemoteStr = req.getParameter("requestDeleteRemote");
        if ((deleteRemoteStr != null) && (deleteRemoteStr.equalsIgnoreCase("true")))
        {
            deleteRemote = true;
        }
        
        SyncSetDefinition ssd = syncAdminService.getSyncSetDefinition(nodeRef);
        
        if (ssd != null)
        {
            if (! syncAdminService.isDirectSyncSetMemberNode(nodeRef))
            {
                throw new WebScriptException(Status.STATUS_FORBIDDEN, "Cannot unsync indirect member of a folder sync: "+nodeRef);
            }
            syncAdminService.removeSyncSetMember(ssd, nodeRef, deleteRemote);
        }
        else
        {
            // This node is NOT a member of any sync set.
            throw new WebScriptException(Status.STATUS_NOT_FOUND, "SyncSetDefinition not found");
        }
        
        Map<String, Object> model = new HashMap<String, Object>(1);
        model.put("success", true);
        return model;
    }
}