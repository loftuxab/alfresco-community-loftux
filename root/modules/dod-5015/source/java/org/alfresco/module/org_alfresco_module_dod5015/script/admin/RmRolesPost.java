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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService;
import org.alfresco.module.org_alfresco_module_dod5015.capability.Capability;
import org.alfresco.module.org_alfresco_module_dod5015.security.RecordsManagementSecurityService;
import org.alfresco.module.org_alfresco_module_dod5015.security.Role;
import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * 
 * 
 * @author Roy Wetherall
 */
public class RmRolesPost extends DeclarativeWebScript
{
    @SuppressWarnings("unused")
    private static Log logger = LogFactory.getLog(RmRolesPost.class);
    
    private RecordsManagementService rmService;
    private RecordsManagementSecurityService rmSecurityService;
    
    public void setRecordsManagementSecurityService(RecordsManagementSecurityService rmSecurityService)
    {
        this.rmSecurityService = rmSecurityService;
    }
    
    public void setRecordsManagementService(RecordsManagementService rmService)
    {
        this.rmService = rmService;
    }

    @Override
    public Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> model = new HashMap<String, Object>();        
        JSONObject json = null;
        try
        {
            json = new JSONObject(new JSONTokener(req.getContent().getContent()));
            String name = json.getString("name");
            // TODO check
            String displayString = json.getString("displayLabel");
            // TODO check
            
            JSONArray capabilitiesArray = json.getJSONArray("capabilities");
            Set<Capability> capabilites = new HashSet<Capability>(capabilitiesArray.length());
            for (int i = 0; i < capabilitiesArray.length(); i++)
            {
                Capability capability = rmSecurityService.getCapability(capabilitiesArray.getString(i));                
                capabilites.add(capability);
            }
            
            List<NodeRef> roots = rmService.getRecordsManagementRoots();
            NodeRef root = roots.get(0);
            
            Role role = rmSecurityService.createRole(root, name, displayString, capabilites);            
            
            Set<Role> roles = rmSecurityService.getRoles(root);
            model.put("role", role);
            
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