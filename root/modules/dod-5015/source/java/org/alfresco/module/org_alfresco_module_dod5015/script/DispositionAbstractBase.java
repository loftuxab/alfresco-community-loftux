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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.module.org_alfresco_module_dod5015.DispositionActionDefinition;
import org.alfresco.module.org_alfresco_module_dod5015.DispositionSchedule;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService;
import org.alfresco.module.org_alfresco_module_dod5015.event.RecordsManagementEvent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.web.scripts.DeclarativeWebScript;
import org.alfresco.web.scripts.WebScriptException;
import org.alfresco.web.scripts.WebScriptRequest;

/**
 * Abstract base class for all disposition related java backed webscripts.
 * 
 * @author Gavin Cornwell
 */
public class DispositionAbstractBase extends DeclarativeWebScript
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
    
    /**
     * Parses the request and providing it's valid returns the NodeRef.
     * 
     * @param req The webscript request
     * @return The NodeRef passed in the request
     */
    protected NodeRef parseRequestForNodeRef(WebScriptRequest req)
    {
        // get the parameters that represent the NodeRef, we know they are present
        // otherwise this webscript would not have matched
        Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
        String storeType = templateVars.get("store_type");
        String storeId = templateVars.get("store_id");
        String nodeId = templateVars.get("id");
        
        // create the NodeRef and ensure it is valid
        StoreRef storeRef = new StoreRef(storeType, storeId);
        NodeRef nodeRef = new NodeRef(storeRef, nodeId);
        
        if (!this.nodeService.exists(nodeRef))
        {
            throw new WebScriptException(HttpServletResponse.SC_NOT_FOUND, "Unable to find node: " + 
                        nodeRef.toString());
        }
        
        return nodeRef;
    }
    
    /**
     * Parses the request and providing it's valid returns the DispositionSchedule object.
     * 
     * @param req The webscript request
     * @return The DispositionSchedule object the request is aimed at
     */
    protected DispositionSchedule parseRequestForSchedule(WebScriptRequest req)
    {
        // get the NodeRef from the request
        NodeRef nodeRef = parseRequestForNodeRef(req);
        
        // make sure the node passed in has a disposition schedule attached
        DispositionSchedule schedule = this.rmService.getDispositionSchedule(nodeRef);
        if (schedule == null)
        {
            throw new WebScriptException(HttpServletResponse.SC_NOT_FOUND, "Node " + 
                        nodeRef.toString() + " does not have a disposition schedule");
        }
        
        return schedule;
    }
    
    /**
     * Parses the request and providing it's valid returns the DispositionActionDefinition object.
     * 
     * @param req The webscript request
     * @param schedule The disposition schedule
     * @return The DispositionActionDefinition object the request is aimed at
     */
    protected DispositionActionDefinition parseRequestForActionDefinition(WebScriptRequest req,
              DispositionSchedule schedule)
    {
        // make sure the requested action definition exists
        Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
        String actionDefId = templateVars.get("action_def_id");
        DispositionActionDefinition actionDef = schedule.getDispositionActionDefinition(actionDefId);
        if (actionDef == null)
        {
            throw new WebScriptException(HttpServletResponse.SC_NOT_FOUND, 
                        "Requested disposition action definition (id:" + actionDefId + ") does not exist");
        }
        
        return actionDef;
    }
    
    /**
     * Helper to create a model to represent the given disposition action definition.
     * 
     * @param actionDef The DispositionActionDefinition instance to generate model for
     * @param url The URL for the DispositionActionDefinition
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
        model.put("label", actionDef.getLabel());
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
        
        List<RecordsManagementEvent> events = actionDef.getEvents();
        if (events != null && events.size() > 0)
        {
            List<String> eventNames = new ArrayList<String>(events.size());
            for (RecordsManagementEvent event : events)
            {
                eventNames.add(event.getName());
            }
            model.put("events", eventNames);
        }
        
        return model;
    }
}