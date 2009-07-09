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
import java.util.Arrays;
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
import org.alfresco.web.scripts.Cache;
import org.alfresco.web.scripts.DeclarativeWebScript;
import org.alfresco.web.scripts.Status;
import org.alfresco.web.scripts.WebScriptException;
import org.alfresco.web.scripts.WebScriptRequest;

/**
 * WebScript java backed bean implementation to get details of a node's
 * disposition schedule.
 * 
 * @author Gavin Cornwell
 */
public class DispositionScheduleGet extends DeclarativeWebScript
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

        // add all the schedule data to Map
        Map<String, Object> scheduleModel = new HashMap<String, Object>(8);
        scheduleModel.put("url", req.getURL());
        scheduleModel.put("authority", schedule.getDispositionAuthority());
        scheduleModel.put("instructions", schedule.getDispositionInstructions());
        scheduleModel.put("recordLevelDisposition", schedule.isRecordLevelDisposition());
        String actionsUrl = req.getURL() + "/dispositionactiondefinitions";
        scheduleModel.put("actionsUrl", actionsUrl);
        
        List<Map<String, Object>> actions = new ArrayList<Map<String, Object>>();
        for (DispositionActionDefinition actionDef : schedule.getDispositionActionDefinitions())
        {
            actions.add(createActionDefModel(actionDef, actionsUrl));
        }
        scheduleModel.put("actions", actions);
        
        // create model object with just the schedule data
        Map<String, Object> model = new HashMap<String, Object>(1);
        model.put("schedule", scheduleModel);
        return model;
    }
    
    /**
     * Creates model to represent the given disposition action definition
     * 
     * @param actionDef The DispositionActionDefinition instance to generate model for
     * @param url The URL for the dispositionactiondefinitons collection
     * @return Map representing the model
     */
    protected Map<String, Object> createActionDefModel(DispositionActionDefinition actionDef,
                String url)
    {
        Map<String, Object> model = new HashMap<String, Object>(8);
        
        model.put("id", actionDef.getId());
        model.put("index", actionDef.getIndex());
        model.put("url", url + "/" + actionDef.getId());
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