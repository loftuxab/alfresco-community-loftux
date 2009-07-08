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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementActionService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.web.scripts.AbstractWebScript;
import org.alfresco.web.scripts.Status;
import org.alfresco.web.scripts.WebScriptException;
import org.alfresco.web.scripts.WebScriptRequest;
import org.alfresco.web.scripts.WebScriptResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * This class provides the implementation for the rmaction webscript.
 * 
 * @author Neil McErlean
 */
public class RmActionPost extends AbstractWebScript
{
    private static Log logger = LogFactory.getLog(RmActionPost.class);
    
    private static final String PARAM_NAME = "name";
    private static final String PARAM_NODE_REF = "nodeRef";
    private static final String PARAM_PARAMS = "params";
    
    private NodeService nodeService;
    private RecordsManagementActionService rmActionService;
    
    private String actionName;
    private NodeRef targetNodeRef;
    private Map<String, Serializable> actionParams = new HashMap<String, Serializable>();
    
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setRecordsManagementActionService(RecordsManagementActionService rmActionService)
    {
        this.rmActionService = rmActionService;
    }

    public void execute(WebScriptRequest req, WebScriptResponse rsp) throws IOException
    {
        final String reqContentAsString = req.getContent().getContent();

        initJsonParams(rsp, reqContentAsString);
        
        // validate input: check for mandatory params, valid nodeRef.
        if (this.actionName == null || this.targetNodeRef == null)
        {
            rsp.setStatus(Status.STATUS_BAD_REQUEST);
            return;
        }

        if (nodeService.exists(this.targetNodeRef) == false)
        {
            if (logger.isDebugEnabled())
            {
                StringBuilder msg = new StringBuilder();
                msg.append("Node does not exist ")
                .append(targetNodeRef);
                logger.debug(msg.toString());
            }
            rsp.setStatus(Status.STATUS_NOT_FOUND);
            return;
        }

        // Proceed to execute the specified action on the specified node.
        if (logger.isDebugEnabled())
        {
            StringBuilder msg = new StringBuilder();
            msg.append("Executing Record Action ")
               .append(this.actionName)
               .append(", ")
               .append(this.targetNodeRef)
               .append(", ")
               .append(this.actionParams);
            logger.debug(msg.toString());
        }
        
        this.rmActionService.executeRecordsManagementAction(targetNodeRef, actionName, actionParams);
        
        
        // Set some JSON response content.
        JSONObject jsonRsp = new JSONObject();
        try
        {
            jsonRsp.put("message", "Successfully queued action [" + actionName + "] on " + targetNodeRef);
        } catch (JSONException je)
        {
            throw new WebScriptException("JSON error", je);
        }
        
        String jsonString = jsonRsp.toString();
        rsp.getWriter().write(jsonString);
    }

    @SuppressWarnings("unchecked")
    private void initJsonParams(WebScriptResponse res, final String reqContentAsString)
    {
        try
        {
            JSONObject jsonObj = new JSONObject(new JSONTokener(reqContentAsString));
            
            // Both nodeRef and name parameters are mandatory.
            if (jsonObj.has(PARAM_NODE_REF) == false)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Mandatory nodeRef parameter is not present");
                }
            }
            if (jsonObj.has(PARAM_NAME) == false)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Mandatory name parameter is not present");
                }
            }
            
            this.actionName = jsonObj.getString(PARAM_NAME);
            this.targetNodeRef = new NodeRef(jsonObj.getString(PARAM_NODE_REF));
            
            // params are optional.
            if (jsonObj.has(PARAM_PARAMS))
            {
                JSONObject paramsObj = jsonObj.getJSONObject(PARAM_PARAMS);
                for (Iterator iter = paramsObj.keys(); iter.hasNext(); )
                {
                    Object nextKey = iter.next();
                    String nextKeyString = (String)nextKey;
                    Object nextValue = paramsObj.get(nextKeyString);
                    this.actionParams.put(nextKeyString, (Serializable)nextValue);
                }
            }
        }
        catch(JSONException je)
        {
            throw new WebScriptException("Unable to serialize JSON", je);
        }
    }
}