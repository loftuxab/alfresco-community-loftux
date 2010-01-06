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
import java.util.HashMap;
import java.util.Map;

import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Implementation for Java backed webscript to start
 * and stop Records Management auditing.
 * 
 * @author Gavin Cornwell
 */
public class AuditLogPut extends BaseAuditAdminWebScript
{
    protected static final String PARAM_ENABLED = "enabled";
    
    /*
     * @see org.alfresco.web.scripts.DeclarativeWebScript#executeImpl(org.alfresco.web.scripts.WebScriptRequest, org.alfresco.web.scripts.Status, org.alfresco.web.scripts.Cache)
     */
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        try
        {
            // determine whether to start or stop auditing
            JSONObject json = new JSONObject(new JSONTokener(req.getContent().getContent()));
            
            // check the enabled property present
            if (!json.has(PARAM_ENABLED))
            {
                throw new WebScriptException(Status.STATUS_BAD_REQUEST,
                    "Mandatory 'enabled' parameter was not provided in request body");
            }
            
            boolean enabled = json.getBoolean(PARAM_ENABLED);
            if (enabled)
            {
                this.rmAuditService.start();
            }
            else
            {
                this.rmAuditService.stop();
            }
            
            // create model object with the audit status model
            Map<String, Object> model = new HashMap<String, Object>(1);
            model.put("auditstatus", createAuditStatusModel());
            return model;
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
    }
}