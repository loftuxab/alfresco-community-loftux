/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.web.scripts.sync.audit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.enterprise.repo.sync.audit.SyncAuditService;
import org.alfresco.enterprise.repo.sync.audit.SyncChangeEvent;
import org.alfresco.enterprise.repo.web.scripts.sync.AbstractCloudSyncDeclarativeWebScript;
import org.alfresco.model.ContentModel;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the TARGET controller for the syncsetchanges.get web script.
 * 
 * GET a list of targetNodeRefs (for a given SSD ID).
 * 
 * @author janv
 * @since 4.1
 */
public class SyncSetChangesGet extends AbstractCloudSyncDeclarativeWebScript
{
    protected SyncAuditService syncAuditService;
    
    public void setSyncAuditService(SyncAuditService syncAuditService)
    {
        this.syncAuditService = syncAuditService;
    }
    
    private static final String PARAM_SSD_ID = "ssdId";
    private static final String PARAM_MAX_ITEMS   = "maxItems";
    
    private static final int DEFAULT_MAX_RESULTS = 5000;
    
    @Override protected Map<String, Object> executeSyncImpl(WebScriptRequest req, Status status, Cache cache)
    {
        final String ssdId = req.getParameter(PARAM_SSD_ID);
        if ((ssdId == null) || (ssdId.isEmpty()))
        {
            throw new WebScriptException(HttpServletResponse.SC_NOT_FOUND, "ssdId cannot be null / empty: "+ssdId);
        }
        
        final int maxItems = getIntParameter(req, PARAM_MAX_ITEMS, DEFAULT_MAX_RESULTS);
        
        List<SyncChangeEvent> syncChangeEvents = syncAuditService.queryBySsdId(ssdId, maxItems);
        
        List<String> folders = new ArrayList<String>(syncChangeEvents.size());
        List<String> files   = new ArrayList<String>(syncChangeEvents.size());
        
        for (SyncChangeEvent event : syncChangeEvents)
        {
            String targetNodeRefStr = event.getNodeRef().toString();
            
            if ((event.getNodeType() != null) && (event.getNodeType().equals(ContentModel.TYPE_FOLDER)))
            {
                if (! folders.contains(targetNodeRefStr))
                {
                    folders.add(targetNodeRefStr);
                }
            }
            else
            {
                if (! files.contains(targetNodeRefStr))
                {
                    files.add(targetNodeRefStr);
                }
            }
        }
        
        List<String> nodeRefStrings = new ArrayList<String>(folders.size()+files.size());
        nodeRefStrings.addAll(folders); // TODO return folders in parent order (at least for create events)
        nodeRefStrings.addAll(files);
        
        Map<String, Object> model = new HashMap<String, Object>(1);
        model.put("nodeRefs", nodeRefStrings);
        
        return model;
    }
}