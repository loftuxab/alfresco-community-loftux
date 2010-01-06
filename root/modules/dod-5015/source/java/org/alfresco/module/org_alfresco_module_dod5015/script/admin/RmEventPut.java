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
package org.alfresco.module.org_alfresco_module_dod5015.script.admin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_dod5015.event.RecordsManagementEvent;
import org.alfresco.module.org_alfresco_module_dod5015.event.RecordsManagementEventService;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Records management event PUT web script
 * 
 * @author Roy Wetherall
 */
public class RmEventPut extends DeclarativeWebScript
{
    @SuppressWarnings("unused")
    private static Log logger = LogFactory.getLog(RmEventPut.class);
    
    /** Reccords management event service */
    private RecordsManagementEventService rmEventService;
    
    /**
     * Set the records management event service
     * 
     * @param rmEventService
     */
    public void setRecordsManagementEventService(RecordsManagementEventService rmEventService)
    {
        this.rmEventService = rmEventService;
    }

    @Override
    public Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> model = new HashMap<String, Object>();        
        JSONObject json = null;
        try
        {
            // Event name
            Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
            String eventName = templateVars.get("eventname");
            if (eventName == null)
            {
                throw new WebScriptException(Status.STATUS_NOT_FOUND, "No event name was provided on the URL.");
            }
            
            // Check the event exists
            if (rmEventService.existsEvent(eventName) == false)
            {
                throw new WebScriptException(Status.STATUS_NOT_FOUND, "The event " + eventName + " does not exist.");
            }
            
            json = new JSONObject(new JSONTokener(req.getContent().getContent()));
           
            String eventDisplayLabel = null;
            if (json.has("eventDisplayLabel") == true)
            {
                eventDisplayLabel = json.getString("eventDisplayLabel");
            }
            if (eventDisplayLabel == null || eventDisplayLabel.length() == 0)
            {
                throw new WebScriptException(Status.STATUS_BAD_REQUEST, "No event display label provided.");
            }
            
            String eventType = null;
            if (json.has("eventType") == true)
            {
                eventType = json.getString("eventType");
            }
            if (eventType == null || eventType.length() == 0)
            {
                throw new WebScriptException(Status.STATUS_BAD_REQUEST, "No event type provided.");
            }
            
            
            RecordsManagementEvent event = rmEventService.addEvent(eventType, eventName, eventDisplayLabel);
            model.put("event", event);
            
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
        
        return model;
    }
}