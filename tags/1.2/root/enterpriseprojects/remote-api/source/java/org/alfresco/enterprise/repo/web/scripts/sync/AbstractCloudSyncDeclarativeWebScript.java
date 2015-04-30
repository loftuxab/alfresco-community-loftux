/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.web.scripts.sync;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.enterprise.repo.sync.SyncAdminService;
import org.alfresco.enterprise.repo.sync.connector.CloudConnectorService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the parent of the Cloud Sync (Declarative) WebScripts
 * 
 * @author Nick Burch
 * @since 4.1
 */
public abstract class AbstractCloudSyncDeclarativeWebScript extends DeclarativeWebScript
{
    protected static final String MESSAGE = "message";
    
    protected NodeService           nodeService;
    protected SyncAdminService      syncAdminService;
    protected CloudConnectorService cloudConnectorService;
    
    public void setNodeService(NodeService service)
    {
        this.nodeService = service;
    }
    public void setSyncAdminService(SyncAdminService syncAdminService)
    {
        this.syncAdminService = syncAdminService;
    }
    public void setCloudConnectorService(CloudConnectorService cloudConnectorService)
    {
        this.cloudConnectorService = cloudConnectorService;
    }
    
    @Override 
    final protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        AbstractCloudSyncAbstractWebScript.ensureSyncActionPermitted(syncAdminService, getDescription());
        return executeSyncImpl(req, status, cache);
    }
    
    abstract protected Map<String, Object> executeSyncImpl(WebScriptRequest req, Status status, Cache cache);
    
    /**
     * Records the given status
     */
    protected void recordStatus(int statusCode, String message, Map<String,Object> model, Status status)
    {
        status.setCode(statusCode);
        status.setMessage(message);
        status.setRedirect(true);
        model.put(MESSAGE, message);
    }
    
    /**
     * This method parses the {@link WebScriptRequest} for a URL-templated NodeRef.
     * 
     * @param req the webscript request object
     * @return the NodeRef if valid.
     * @throws WebScriptException throws a 404 if the Node does not exist.
     */
    protected NodeRef parseRequestForNodeRef(WebScriptRequest req)
    {
        Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
        String storeType = templateVars.get("store_type");
        String storeId = templateVars.get("store_id");
        String nodeId = templateVars.get("id");

        // create the NodeRef and ensure it is valid
        StoreRef storeRef = new StoreRef(storeType, storeId);
        NodeRef nodeRef = new NodeRef(storeRef, nodeId);
        
        if (!this.nodeService.exists(nodeRef))
        {
            throw new WebScriptException(HttpServletResponse.SC_NOT_FOUND, "Unable to find node: " + nodeRef.toString());
        }
        return nodeRef;
    }
    
    protected int getIntParameter(WebScriptRequest req, String paramName, int defaultValue)
    {
        String paramString = req.getParameter(paramName);
        if (paramString != null)
        {
            try
            {
                int param = Integer.valueOf(paramString);
                if (param > 0)
                {
                    return param;
                }
            }
            catch (NumberFormatException e) 
            {
                throw new WebScriptException(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            }
        }
        
        return defaultValue;
    }
}