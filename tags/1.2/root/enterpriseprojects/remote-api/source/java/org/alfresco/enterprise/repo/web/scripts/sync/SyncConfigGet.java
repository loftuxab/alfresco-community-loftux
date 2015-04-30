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

import org.alfresco.enterprise.repo.sync.SyncAdminService;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * WebScript controller for /enterprise/sync/config REST API.
 * <p>
 * "syncMode": "[CLOUD|ON_PREMISE|OFF]"
 * 
 * @author mrogers
 * @since 4.1
 */
public class SyncConfigGet extends DeclarativeWebScript
{
    private SyncAdminService syncAdminService;
    
    public void setSyncAdminService(SyncAdminService syncAdminService)
    {
        this.syncAdminService = syncAdminService;
    }
    
    @Override protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> model = new HashMap<String, Object>(2);
        
        model.put("syncMode", syncAdminService.getMode().toString());
      
        return model;
    }
}