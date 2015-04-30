/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.web.scripts.sync.cloudonly;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.enterprise.repo.sync.NoSuchSyncSetDefinitionException;
import org.alfresco.enterprise.repo.web.scripts.sync.AbstractCloudSyncDeclarativeWebScript;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the TARGET controller for the remotesyncsetdefinition.delete web script.
 * 
 * TODO - if network is downgraded (from Enterprise) we should still allow the sync set to be deleted ?
 * 
 * @author janv
 * @since 4.1
 */
public class RemoteSyncSetDefinitionDelete extends AbstractCloudSyncDeclarativeWebScript
{
    private static final Log logger = LogFactory.getLog(RemoteSyncSetDefinitionDelete.class);
    
    private static final String PARAM_SSD_ID = "ssdId";
    
    @Override protected Map<String, Object> executeSyncImpl(WebScriptRequest req, Status status, Cache cache)
    {
        try
        {
            final String ssdId = req.getParameter(PARAM_SSD_ID);
            if ((ssdId == null) || (ssdId.isEmpty()))
            {
                throw new WebScriptException(HttpServletResponse.SC_NOT_FOUND, "ssdId cannot be null / empty: "+ssdId);
            }
            
            if (logger.isDebugEnabled())
            {
                logger.debug("Delete target SSD: "+ssdId);
            }
            
            syncAdminService.deleteTargetSyncSet(ssdId);
            
            Map<String, Object> model = new HashMap<String, Object>(1);
            model.put("success", true);
            return model;
        }
        catch (NoSuchSyncSetDefinitionException nsse)
        {
            throw new WebScriptException(Status.STATUS_NOT_FOUND, "SyncSet could not be found", nsse);
        }
    }
}