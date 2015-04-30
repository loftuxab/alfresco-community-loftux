/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.web.scripts.sync.audit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.enterprise.repo.sync.audit.SyncAuditService;
import org.alfresco.enterprise.repo.web.scripts.sync.AbstractCloudSyncDeclarativeWebScript;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the TARGET controller for the syncsetmanifest.get web script.
 * 
 * GET a list of SSD IDs (for a given source Repository ID).
 * 
 * @author janv
 * @since 4.1
 */
public class SyncSetManifestGet extends AbstractCloudSyncDeclarativeWebScript
{
    protected SyncAuditService syncAuditService;
    
    public void setSyncAuditService(SyncAuditService syncAuditService)
    {
        this.syncAuditService = syncAuditService;
    }
    
    private static final String PARAM_SRC_REPO_ID = "srcRepoId";
    private static final String PARAM_MAX_ITEMS   = "maxItems";
    
    private static final int DEFAULT_MAX_RESULTS = 5000;
    
    @Override protected Map<String, Object> executeSyncImpl(WebScriptRequest req, Status status, Cache cache)
    {
        final String srcRepoId = req.getParameter(PARAM_SRC_REPO_ID);
        if ((srcRepoId == null) || (srcRepoId.isEmpty()))
        {
            throw new WebScriptException(HttpServletResponse.SC_NOT_FOUND, "srcRepoId cannot be null / empty: "+srcRepoId);
        }
        
        final int maxItems = getIntParameter(req, PARAM_MAX_ITEMS, DEFAULT_MAX_RESULTS);
        
        List<String> ssdIds = syncAuditService.querySsdManifest(srcRepoId, maxItems);
        
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("ssdIds", ssdIds);
        
        return model;
    }
}