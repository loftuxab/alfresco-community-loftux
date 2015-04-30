/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.web.scripts.sync;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.enterprise.repo.sync.SyncSetDefinition;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.ParameterCheck;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the SOURCE controller for the syncsetdefinition.post web script.
 * 
 * @author Neil Mc Erlean, janv
 * @since 4.1
 */
public class SyncSetDefinitionPost extends AbstractCloudSyncDeclarativeWebScript
{
    public static final String PARAM_REMOTE_USER_NAME      = "remoteUserName";
    public static final String PARAM_REMOTE_PASSWORD       = "remotePassword";
    public static final String PARAM_MEMBER_NODEREFS       = "memberNodeRefs";
    public static final String PARAM_REMOTE_TENANT_ID      = "remoteTenantId";
    public static final String PARAM_TARGET_FOLDER_NODEREF = "targetFolderNodeRef";
    public static final String PARAM_LOCK_SOURCE_COPY      = "lockSourceCopy";
    public static final String PARAM_INCLUDE_SUBFOLDERS    = "includeSubFolders";
    public static final String PARAM_IS_DELETE_ON_CLOUD    = "isDeleteOnCloud";
    public static final String PARAM_IS_DELETE_ON_PREM     = "isDeleteOnPrem";
    
    @SuppressWarnings("unchecked")
    @Override protected Map<String, Object> executeSyncImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> model = new HashMap<String, Object>();
        
        try
        {
            JSONObject json = (JSONObject) JSONValue.parseWithException(req.getContent().getContent());
            
            // See if they supplied new/updated username/password details
            // They must either supply some, or already have some defined
            final String remoteUserName = (String) json.get(PARAM_REMOTE_USER_NAME);
            final String remotePassword = (String) json.get(PARAM_REMOTE_PASSWORD);
            if (remoteUserName != null && remotePassword != null)
            {
                // Store the new details
                cloudConnectorService.storeCloudCredentials(remoteUserName, remotePassword);
            }
            else
            {
                if (cloudConnectorService.getCloudCredentials() != null)
                {
                    // They already have credentials, we'll re-use them
                }
                else
                {
                    throw new WebScriptException(Status.STATUS_FORBIDDEN, "No credentials supplied and no existing ones found");
                }
            }
            
            
            // All JSON data here is mandatory. Additional validation will take place in the Cloud.
            
            final JSONArray memberNodeRefsArray = (JSONArray) json.get(PARAM_MEMBER_NODEREFS);
            ParameterCheck.mandatory(PARAM_MEMBER_NODEREFS, memberNodeRefsArray);
            
            String[] memberNodeRefStrings = new String[0];
            memberNodeRefStrings= (String[]) memberNodeRefsArray.toArray(memberNodeRefStrings);
            
            
            final String remoteTenantId = (String) json.get(PARAM_REMOTE_TENANT_ID);
            ParameterCheck.mandatoryString(PARAM_REMOTE_TENANT_ID, remoteTenantId);
            
            final String targetFolderNodeRef = (String) json.get(PARAM_TARGET_FOLDER_NODEREF);
            ParameterCheck.mandatoryString(PARAM_TARGET_FOLDER_NODEREF, targetFolderNodeRef);
            
            final Boolean lockSourceCopy = (Boolean) json.get(PARAM_LOCK_SOURCE_COPY);
            
            final Boolean includeSubFolders = (Boolean) json.get(PARAM_INCLUDE_SUBFOLDERS);
            
            final Boolean isDeleteOnCloud = (Boolean) json.get(PARAM_IS_DELETE_ON_CLOUD);
            final Boolean isDeleteOnPrem = (Boolean) json.get(PARAM_IS_DELETE_ON_PREM);
            
            if (memberNodeRefStrings.length == 0)
            {
                throw new WebScriptException(Status.STATUS_BAD_REQUEST, "At least one member node required");
            }
            
            List<NodeRef> memberNodeRefs = new ArrayList<NodeRef>(memberNodeRefStrings.length);
            for (String nodeRefString : memberNodeRefStrings)
            {
                memberNodeRefs.add(new NodeRef(nodeRefString));
            }
            
            // TODO: minor - switch includeSubFolders default to false, once UI checkbox is available
            SyncSetDefinition ssd = syncAdminService.createSourceSyncSet(memberNodeRefs,
                                                                         remoteTenantId, 
                                                                         targetFolderNodeRef,
                                                                         (lockSourceCopy == null ? false : lockSourceCopy),
                                                                         (includeSubFolders == null ? true : includeSubFolders),
                                                                         (isDeleteOnCloud == null ? true : isDeleteOnCloud),
                                                                         (isDeleteOnPrem == null ? false : isDeleteOnPrem));
            
            model.put("ssd", ssd);
        }
        catch (InvalidNodeRefException inre)
        {
            throw new WebScriptException(Status.STATUS_NOT_FOUND, "Node not found: "+inre.getNodeRef(), inre);
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