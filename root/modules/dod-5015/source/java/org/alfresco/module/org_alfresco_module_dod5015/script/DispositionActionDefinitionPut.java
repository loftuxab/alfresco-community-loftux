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
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing
 */
package org.alfresco.module.org_alfresco_module_dod5015.script;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.module.org_alfresco_module_dod5015.DispositionActionDefinition;
import org.alfresco.module.org_alfresco_module_dod5015.DispositionSchedule;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.scripts.Cache;
import org.alfresco.web.scripts.DeclarativeWebScript;
import org.alfresco.web.scripts.Status;
import org.alfresco.web.scripts.WebScriptException;
import org.alfresco.web.scripts.WebScriptRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * WebScript java backed bean implementation to update an instance
 * of a dispostion action definition.
 * 
 * @author Gavin Cornwell
 */
public class DispositionActionDefinitionPut extends DeclarativeWebScript
{
    protected RecordsManagementService rmService;
    protected NodeService nodeService;
    protected NamespaceService namespaceService;
    
    /**
     * Sets the RecordsManagementService instance
     * 
     * @param rmService The RecordsManagementService instance
     */
    public void setRecordsManagementService(RecordsManagementService rmService)
    {
        this.rmService = rmService;
    }

    /**
     * Sets the NodeService instance
     * 
     * @param nodeService The NodeService instance
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    /**
     * Sets the NamespaceService instance
     * 
     * @param namespaceService The NamespaceService instance
     */
    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    /*
     * @see org.alfresco.web.scripts.DeclarativeWebScript#executeImpl(org.alfresco.web.scripts.WebScriptRequest, org.alfresco.web.scripts.Status, org.alfresco.web.scripts.Cache)
     */
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        // get the parameters that represent the NodeRef, we know they are present
        // otherwise this webscript would not have matched
        Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
        String storeType = templateVars.get("store_type");
        String storeId = templateVars.get("store_id");
        String nodeId = templateVars.get("id");
        String actionDefId = templateVars.get("action_def_id");
        
        // create the NodeRef and ensure it is valid
        StoreRef storeRef = new StoreRef(storeType, storeId);
        NodeRef nodeRef = new NodeRef(storeRef, nodeId);
        
        if (!this.nodeService.exists(nodeRef))
        {
            throw new WebScriptException(HttpServletResponse.SC_NOT_FOUND, "Unable to find node: " + 
                        nodeRef.toString());
        }
        
        // make sure the node passed in has a disposition schedule attached
        DispositionSchedule schedule = this.rmService.getDispositionSchedule(nodeRef);
        if (schedule == null)
        {
            throw new WebScriptException(HttpServletResponse.SC_NOT_FOUND, "Node " + 
                        nodeRef.toString() + " does not have a disposition schedule");
        }
        
        // make sure the requested action definition exists
        DispositionActionDefinition actionDef = schedule.getDispositionActionDefinition(actionDefId);
        if (actionDef == null)
        {
            throw new WebScriptException(HttpServletResponse.SC_NOT_FOUND, 
                        "Requested disposition action definition (id:" + actionDefId + ") does not exist");
        }

        // retrieve the rest of the post body and update the action definition 
        JSONObject json = null;
        try
        {
            json = new JSONObject(new JSONTokener(req.getContent().getContent()));
            actionDef = updateActionDefinition(actionDefId, json, nodeRef);
        } 
        catch (IOException iox)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST,
                    "Could not read content from req.", iox);
        }
        catch (JSONException je)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST,
                        "Could not parse JSON from req.", je);
        }
        
        // create model object with just the action data
        Map<String, Object> model = new HashMap<String, Object>(1);
        model.put("action", createActionDefModel(actionDef, req.getURL()));
        return model;
    }
    
    /**
     * Updates a dispositionActionDefinition node in the repo.
     * 
     * @param actionDefId The id of the action definition to update
     * @param json The JSON to use to create the action definition
     * @param scheduleParent The nodeRef of the item the with the disposition schedule the
     *        updated action definition is for
     * @return The updated DispositionActionDefinition
     */
    protected DispositionActionDefinition updateActionDefinition(String actionDefId,
              JSONObject json, NodeRef scheduleParent) throws JSONException
    {
        // create the properties for the action definition
        Map<QName, Serializable> props = new HashMap<QName, Serializable>(8);
        
        if (json.has("name"))
        {
            props.put(RecordsManagementModel.PROP_DISPOSITION_ACTION_NAME, json.getString("name"));
        }
        
        if (json.has("description"))
        {
            props.put(RecordsManagementModel.PROP_DISPOSITION_DESCRIPTION, json.getString("description"));
        }
        
        if (json.has("period"))
        {
            props.put(RecordsManagementModel.PROP_DISPOSITION_PERIOD, json.getString("period"));
        }
        
        if (json.has("periodProperty"))
        {
            QName periodProperty = QName.createQName(json.getString("periodProperty"), this.namespaceService);
            props.put(RecordsManagementModel.PROP_DISPOSITION_PERIOD_PROPERTY, periodProperty);
        }
        
        if (json.has("eligibleOnFirstCompleteEvent"))
        {
            props.put(RecordsManagementModel.PROP_DISPOSITION_EVENT_COMBINATION, 
                        json.getBoolean("eligibleOnFirstCompleteEvent") ? "or" : "and");
        }
        
        if (json.has("events"))
        {
            JSONArray events = json.getJSONArray("events");
            List<String> eventsList = new ArrayList<String>(events.length());
            for (int x = 0; x < events.length(); x++)
            {
                eventsList.add(events.getString(x));
            }
            props.put(RecordsManagementModel.PROP_DISPOSITION_EVENT, (Serializable)eventsList);
        }
        
        // update the node with properties
        NodeRef actionNodeRef = new NodeRef(scheduleParent.getStoreRef(), actionDefId);
        this.nodeService.addProperties(actionNodeRef, props);
        
        // use the RM service to fetch the dispostion schedule which will include the 
        // new action, retriev the details and return
        DispositionSchedule schedule = this.rmService.getDispositionSchedule(scheduleParent);
        return schedule.getDispositionActionDefinition(actionDefId);
    }
    
    /**
     * Creates model to represent the given disposition action definition
     * 
     * @param actionDef The DispositionActionDefinition instance to generate model for
     * @param url The URL for the dispositionactiondefiniton resource
     * @return Map representing the model
     */
    protected Map<String, Object> createActionDefModel(DispositionActionDefinition actionDef,
                String url)
    {
        Map<String, Object> model = new HashMap<String, Object>(8);
        
        model.put("id", actionDef.getId());
        model.put("index", actionDef.getIndex());
        model.put("url", url);
        model.put("name", actionDef.getName());
        model.put("eligibleOnFirstCompleteEvent", actionDef.eligibleOnFirstCompleteEvent());
        
        if (actionDef.getDescription() != null)
        {
            model.put("description", actionDef.getDescription());
        }
        
        if (actionDef.getPeriod() != null)
        {
            model.put("period", actionDef.getPeriod().toString());
        }
        
        if (actionDef.getPeriodProperty() != null)
        {
            model.put("periodProperty", actionDef.getPeriodProperty().toPrefixString(this.namespaceService));
        }
        
        /*List<RecordsManagementEvent> events = actionDef.getEvents();
        if (events != null && events.size() > 0)
        {
            List<String> eventNames = new ArrayList<String>(events.size());
            for (RecordsManagementEvent event : events)
            {
                eventNames.add(event.getName());
            }
            model.put("events", eventNames);
        }*/
        
        return model;
    }
}