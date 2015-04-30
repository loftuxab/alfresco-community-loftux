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

import org.alfresco.enterprise.repo.sync.SyncService;
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
 * This class is the SOURCE controller for the syncrequest.post web script.
 * 
 * @author janv
 * @since 4.1
 */
public class SyncRequestPost extends AbstractCloudSyncDeclarativeWebScript
{
    public static final String PARAM_MEMBER_NODEREFS = "memberNodeRefs";
    
    protected SyncService syncService;
    
    public void setSyncService(SyncService syncService)
    {
        this.syncService = syncService;
    }
    
    @SuppressWarnings("unchecked")
    @Override 
    protected Map<String, Object> executeSyncImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> model = new HashMap<String, Object>();
        
        try
        {
            JSONObject json = (JSONObject) JSONValue.parseWithException(req.getContent().getContent());
            
            final JSONArray memberNodeRefsArray = (JSONArray) json.get(PARAM_MEMBER_NODEREFS);
            ParameterCheck.mandatory(PARAM_MEMBER_NODEREFS, memberNodeRefsArray);
            
            String[] memberNodeRefStrings = new String[0];
            memberNodeRefStrings= (String[]) memberNodeRefsArray.toArray(memberNodeRefStrings);
            
            if (memberNodeRefStrings.length == 0)
            {
                throw new WebScriptException(Status.STATUS_BAD_REQUEST, "At least one member node required");
            }
            
            List<NodeRef> memberNodeRefs = new ArrayList<NodeRef>(memberNodeRefStrings.length);
            for (String nodeRefString : memberNodeRefStrings)
            {
                memberNodeRefs.add(new NodeRef(nodeRefString));
            }
            
            syncService.requestSync(memberNodeRefs);
            
            model.put("success", true);
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