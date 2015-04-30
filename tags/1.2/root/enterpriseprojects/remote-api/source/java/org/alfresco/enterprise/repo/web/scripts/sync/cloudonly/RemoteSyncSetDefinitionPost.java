/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.web.scripts.sync.cloudonly;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.enterprise.repo.sync.SyncServiceImpl;
import org.alfresco.enterprise.repo.sync.SyncSetDefinition;
import org.alfresco.enterprise.repo.web.scripts.sync.AbstractCloudSyncDeclarativeWebScript;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.ParameterCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the TARGET controller for the remotesyncsetdefinition.post web script.
 * 
 * @author Neil Mc Erlean
 * @since 4.1
 */
public class RemoteSyncSetDefinitionPost extends AbstractCloudSyncDeclarativeWebScript
{
    private static final Log logger = LogFactory.getLog(RemoteSyncSetDefinitionPost.class);
    
    @Override protected Map<String, Object> executeSyncImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> model = new HashMap<String, Object>();
        
        try
        {
            JSONObject json = (JSONObject) JSONValue.parseWithException(req.getContent().getContent());
            
            // All JSON data here is mandatory.
            final String ssdId = (String) json.get(SyncServiceImpl.PARAM_SSD_ID);
            final String srcRepoId = (String) json.get(SyncServiceImpl.PARAM_SOURCE_REPO_ID);
            final String targetFolderNodeRefString = (String) json.get(SyncServiceImpl.PARAM_TARGET_FOLDER_NODEREF);
            
            ParameterCheck.mandatoryString(SyncServiceImpl.PARAM_SSD_ID, ssdId);
            ParameterCheck.mandatoryString(SyncServiceImpl.PARAM_SOURCE_REPO_ID, srcRepoId);
            ParameterCheck.mandatoryString(SyncServiceImpl.PARAM_TARGET_FOLDER_NODEREF, targetFolderNodeRefString);
            
            final NodeRef targetFolderNodeRef = new NodeRef(targetFolderNodeRefString);
            if ( !nodeService.exists(targetFolderNodeRef))
            {
                throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Invalid " + SyncServiceImpl.PARAM_TARGET_FOLDER_NODEREF + ": " + targetFolderNodeRefString);
            }
            
            Boolean includeSubFolders = (Boolean) json.get(SyncServiceImpl.PARAM_INCLUDE_SUBFOLDERS);
            if (includeSubFolders == null) { includeSubFolders = false; }
            
            Boolean isDeleteOnCloud = (Boolean) json.get(SyncServiceImpl.PARAM_IS_DELETE_ON_CLOUD);
            if (isDeleteOnCloud == null) { isDeleteOnCloud = true; }
            
            Boolean isDeleteOnPrem = (Boolean) json.get(SyncServiceImpl.PARAM_IS_DELETE_ON_PREM);
            if (isDeleteOnPrem == null) { isDeleteOnPrem = false; }
         
            
            if (logger.isDebugEnabled())
            {
                logger.debug("Create target SSD: "+ssdId);
            }
            
            SyncSetDefinition ssd = syncAdminService.createTargetSyncSet(ssdId, srcRepoId, targetFolderNodeRef, includeSubFolders, isDeleteOnCloud, isDeleteOnPrem);
            
            model.put("ssd", ssd);
        }
        catch (ParseException p)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not parse JSON from request.", p);
        }
        catch (IOException e)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not read content from req.", e);
        }
        return model;
    }
}