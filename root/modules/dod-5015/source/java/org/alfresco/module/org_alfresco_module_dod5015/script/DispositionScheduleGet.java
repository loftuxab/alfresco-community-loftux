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

import org.alfresco.module.org_alfresco_module_dod5015.DispositionActionDefinition;
import org.alfresco.module.org_alfresco_module_dod5015.DispositionSchedule;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * Implementation for Java backed webscript to return full details
 * about a disposition schedule.
 * 
 * @author Gavin Cornwell
 */
public class DispositionScheduleGet extends DispositionAbstractBase
{
    /*
     * @see org.alfresco.web.scripts.DeclarativeWebScript#executeImpl(org.alfresco.web.scripts.WebScriptRequest, org.alfresco.web.scripts.Status, org.alfresco.web.scripts.Cache)
     */
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        // parse the request to retrieve the schedule object
        DispositionSchedule schedule = parseRequestForSchedule(req);

        // add all the schedule data to Map
        Map<String, Object> scheduleModel = new HashMap<String, Object>(8);
        
        // build url
        String serviceUrl = req.getServiceContextPath() + req.getPathInfo();
        scheduleModel.put("url", serviceUrl);
        String actionsUrl = serviceUrl + "/dispositionactiondefinitions";
        scheduleModel.put("actionsUrl", actionsUrl);
        scheduleModel.put("nodeRef", schedule.getNodeRef().toString());
        scheduleModel.put("recordLevelDisposition", schedule.isRecordLevelDisposition());
        scheduleModel.put("canStepsBeRemoved", 
                    this.rmService.canDispositionActionDefinitionsBeRemoved(schedule));
        
        if (schedule.getDispositionAuthority() != null)
        {
            scheduleModel.put("authority", schedule.getDispositionAuthority());
        }
        
        if (schedule.getDispositionInstructions() != null)
        {
            scheduleModel.put("instructions", schedule.getDispositionInstructions());
        }
        
        List<Map<String, Object>> actions = new ArrayList<Map<String, Object>>();
        for (DispositionActionDefinition actionDef : schedule.getDispositionActionDefinitions())
        {
            actions.add(createActionDefModel(actionDef, actionsUrl + "/" + actionDef.getId()));
        }
        scheduleModel.put("actions", actions);
        
        // create model object with just the schedule data
        Map<String, Object> model = new HashMap<String, Object>(1);
        model.put("schedule", scheduleModel);
        return model;
    }
}