/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.web.scripts.sync;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.enterprise.repo.sync.NoSuchSyncSetDefinitionException;
import org.alfresco.enterprise.repo.sync.SyncAdminService;
import org.alfresco.enterprise.repo.sync.SyncNodeException;
import org.alfresco.enterprise.repo.sync.SyncNodeException.SyncNodeExceptionType;
import org.alfresco.enterprise.repo.sync.SyncService;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.Description;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.Description.RequiredAuthentication;

/**
 * This class is the parent of the Cloud Sync (Abstract) WebScripts
 * 
 * @author Nick Burch
 * @since 4.1
 */
public abstract class AbstractCloudSyncAbstractWebScript extends AbstractWebScript
{
    protected static Log logger = LogFactory.getLog(AbstractCloudSyncAbstractWebScript.class);

    protected NodeService           nodeService;
    protected SyncService           syncService;
    protected SyncAdminService      syncAdminService;
    
    public void setNodeService(NodeService service)
    {
        this.nodeService = service;
    }
    public void setSyncService(SyncService syncService)
    {
        this.syncService = syncService;
    }
    public void setSyncAdminService(SyncAdminService syncAdminService)
    {
        this.syncAdminService = syncAdminService;
    }
    
    @Override
    final public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException
    {
        ensureSyncActionPermitted(syncAdminService, getDescription());
        executeSyncImpl(req, res);
    }
    public abstract void executeSyncImpl(WebScriptRequest req, WebScriptResponse res) throws IOException;
    
    /**
     * Ensures that Sync is allowed for the current tenant
     */
    protected static void ensureSyncActionPermitted(SyncAdminService syncAdminService, Description description)
    {
        // note: requiredAuthentication none => special case (eg. SyncSetManifestGet)
        if (description.getRequiredAuthentication() != RequiredAuthentication.none)
        {
            String currentTenantDomain = TenantUtil.getCurrentDomain();
            if (! syncAdminService.isTenantEnabledForSync(currentTenantDomain))
            {
                // Unsync is always allowed, no matter the current tenant status
                if ("cloud-sync-delete".equals(description.getShortName()))
                {
                    logger.info("Permitting Un-Sync for downgraded network " + currentTenantDomain);
                }
                else
                {
                    throw new WebScriptException(HttpServletResponse.SC_FORBIDDEN, "Cannot sync to non-Enterprise Network: "+currentTenantDomain);
                }
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    protected void reportException(NoSuchSyncSetDefinitionException nsse, WebScriptResponse res) throws IOException
    {
        res.setStatus(Status.STATUS_GONE);
        res.setContentType(MimetypeMap.MIMETYPE_JSON);

        JSONObject json = new JSONObject();
        json.put("message", nsse.getMessage());
        json.put("ssId", nsse.getUnrecognisedSsdId());
        
        Writer writer = res.getWriter();
        writer.write(json.toJSONString());
        writer.close();
    }
    
    @SuppressWarnings("unchecked")
    protected void reportException(SyncNodeException sne, WebScriptResponse res) throws IOException
    {
        res.setStatus(Status.STATUS_PRECONDITION_FAILED);
        res.setContentType(MimetypeMap.MIMETYPE_JSON);

        JSONObject json = new JSONObject();
        json.put("message", sne.getMessage());
        json.put("messageId", sne.getMsgId());
        
        if (sne.getExceptionType() == SyncNodeExceptionType.UNKNOWN)
        {
            JSONObject cause = new JSONObject();
            if (sne.getCause() == null)
            {
                cause.put("message", "(no cause available)");
            }
            else
            {
                cause.put("class", sne.getCause().getClass().getName());
                cause.put("message", sne.getCause().getMessage());
                JSONArray st = new JSONArray();
                for (StackTraceElement ste : sne.getCause().getStackTrace())
                {
                    st.add(ste.toString());
                }
                cause.put("stacktrace", st);
            }
            json.put("cause", cause);
        }
        
        Writer writer = res.getWriter();
        writer.write(json.toJSONString());
        writer.close();
    }
}
