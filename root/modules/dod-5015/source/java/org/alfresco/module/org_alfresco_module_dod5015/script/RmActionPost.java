/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.org_alfresco_module_dod5015.script;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementActionResult;
import org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementActionService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.springframework.extensions.surf.util.ISO8601DateFormat;
import org.alfresco.web.scripts.Cache;
import org.alfresco.web.scripts.DeclarativeWebScript;
import org.alfresco.web.scripts.Status;
import org.alfresco.web.scripts.WebScriptException;
import org.alfresco.web.scripts.WebScriptRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * This class provides the implementation for the rmaction webscript.
 * 
 * @author Neil McErlean
 */
public class RmActionPost extends DeclarativeWebScript
{
    private static Log logger = LogFactory.getLog(RmActionPost.class);
    
    private static final String PARAM_NAME = "name";
    private static final String PARAM_NODE_REF = "nodeRef";
    private static final String PARAM_NODE_REFS = "nodeRefs";
    private static final String PARAM_PARAMS = "params";
    
    private NodeService nodeService;
    private RecordsManagementActionService rmActionService;
    
    private String actionName;
    private List<NodeRef> targetNodeRefs = new ArrayList<NodeRef>();
    private Map<String, Serializable> actionParams = new HashMap<String, Serializable>();
    
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setRecordsManagementActionService(RecordsManagementActionService rmActionService)
    {
        this.rmActionService = rmActionService;
    }

    @Override
    public Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        String reqContentAsString;
        try
        {
            reqContentAsString = req.getContent().getContent();
        } catch (IOException iox)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST,
                    "Could not read content from req.", iox);
        }

        initJsonParams(reqContentAsString);
        
        // validate input: check for mandatory params.
        // Some RM actions can be posted without a nodeRef.
        if (this.actionName == null)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST,
                "A mandatory parameter has not been provided in URL");
        }

        // Check that all the nodes provided exist and build report string
        StringBuffer targetNodeRefsString = new StringBuffer(30);
        boolean firstTime = true;
        for (NodeRef targetNodeRef : this.targetNodeRefs)
        {
            if (nodeService.exists(targetNodeRef) == false)
            {
                throw new WebScriptException(Status.STATUS_NOT_FOUND,
                    "The targetNode does not exist (" + targetNodeRef.toString() + ")");
            }
            
            // Build the string
            if (firstTime == true)
            {
                firstTime = false;
            }
            else
            {
                targetNodeRefsString.append(", ");
            }
            targetNodeRefsString.append(targetNodeRef.toString());
        }

        // Proceed to execute the specified action on the specified node.
        if (logger.isDebugEnabled())
        {
            StringBuilder msg = new StringBuilder();
            msg.append("Executing Record Action ")
               .append(this.actionName)
               .append(", (")
               .append(targetNodeRefsString.toString())
               .append("), ")
               .append(this.actionParams);
            logger.debug(msg.toString());
        }
        
        Map<String, Object> model = new HashMap<String, Object>();        
        if (this.targetNodeRefs.isEmpty())
        {
            RecordsManagementActionResult result = this.rmActionService.executeRecordsManagementAction(actionName, actionParams);
            if (result.getValue() != null)
            {
                model.put("result", result.getValue().toString());
            }
        }
        else
        {
            Map<NodeRef, RecordsManagementActionResult> resultMap = this.rmActionService.executeRecordsManagementAction(targetNodeRefs, actionName, actionParams);
            Map<String, String> results = new HashMap<String, String>(resultMap.size());
            for (NodeRef nodeRef : resultMap.keySet())
            {
                Object value = resultMap.get(nodeRef).getValue();
                if (value != null)
                {
                    results.put(nodeRef.toString(), resultMap.get(nodeRef).getValue().toString());
                }
            }
            model.put("results", results);
        }
        
        model.put("message", "Successfully queued action [" + actionName + "] on " + targetNodeRefsString.toString());

        return model;
    }

    @SuppressWarnings("unchecked")
    private void initJsonParams(final String reqContentAsString)
    {
        try
        {
            JSONObject jsonObj = new JSONObject(new JSONTokener(reqContentAsString));
            
            // Get the action name
            this.actionName = jsonObj.getString(PARAM_NAME);
            
            // Get the target references
            if (jsonObj.has(PARAM_NODE_REF) == true)
            {
                NodeRef nodeRef = new NodeRef(jsonObj.getString(PARAM_NODE_REF));
                this.targetNodeRefs = new ArrayList<NodeRef>(1);
                this.targetNodeRefs.add(nodeRef);
            }
            if (jsonObj.has(PARAM_NODE_REFS) == true)
            {
                JSONArray jsonArray = jsonObj.getJSONArray(PARAM_NODE_REFS);
                if (jsonArray.length() != 0)
                {
                    this.targetNodeRefs = new ArrayList(jsonArray.length());
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        NodeRef nodeRef = new NodeRef(jsonArray.getString(i));
                        this.targetNodeRefs.add(nodeRef);
                    }
                }
            }
            
            // params are optional.
            if (jsonObj.has(PARAM_PARAMS))
            {
                JSONObject paramsObj = jsonObj.getJSONObject(PARAM_PARAMS);
                for (Iterator iter = paramsObj.keys(); iter.hasNext(); )
                {
                    Object nextKey = iter.next();
                    String nextKeyString = (String)nextKey;
                    Object nextValue = paramsObj.get(nextKeyString);
                    
                    // Check for date values
                    if (nextValue instanceof JSONObject)
                    {
                        if (((JSONObject)nextValue).has("iso8601") == true)
                        {
                            String dateStringValue = ((JSONObject)nextValue).getString("iso8601");
                            nextValue = ISO8601DateFormat.parse(dateStringValue);
                        }
                    } 
                    
                    this.actionParams.put(nextKeyString, (Serializable)nextValue);
                }
            }
        }
        catch(JSONException je)
        {
            // Intentionally empty. Missing mandatory parameters are detected in the calling method.
        }
    }
}