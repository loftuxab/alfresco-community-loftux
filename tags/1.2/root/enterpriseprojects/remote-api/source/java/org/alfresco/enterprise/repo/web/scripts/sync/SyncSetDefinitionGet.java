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
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the SOURCE controller for the syncsetdefinition.get web script.
 * 
 * @author Neil Mc Erlean
 * @since 4.1
 */
public class SyncSetDefinitionGet extends AbstractCloudSyncDeclarativeWebScript
{
    private static final String TEMPLATE_VAR_ID = "id";
    
    @Override protected Map<String, Object> executeSyncImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
        final String idString = templateVars.get(TEMPLATE_VAR_ID);
        
        SyncSetDefinition ssd = syncAdminService.getSyncSetDefinition(idString);
        
        if (ssd == null)
        {
            throw new WebScriptException(Status.STATUS_NOT_FOUND, "SyncSetDefinition not found");
        }
        
        Map<String, Object> model = new HashMap<String, Object>();
        
        model.put("ssd", ssd);
      
        return model;
    }
}