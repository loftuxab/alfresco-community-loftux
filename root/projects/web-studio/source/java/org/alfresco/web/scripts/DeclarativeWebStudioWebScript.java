/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.scripts;

import java.util.Map;

import org.alfresco.web.site.RequestContext;

/**
 * Base Web Script implementation for Web Studio Web Scripts
 * 
 * This extends from DeclarativeSiteWebScript so as to provide
 * root-scoped objects directly from the Web Framework (Surf).
 * 
 * It also introduces a new root-scoped object called "webstudio"
 * 
 * @author muzquiano
 */
public class DeclarativeWebStudioWebScript extends DeclarativeSiteWebScript
{
    private static final String ROOT_SCOPE_WEB_STUDIO = "webstudio";

    /**
     * Instantiates a new declarative web studio web script.
     */
    public DeclarativeWebStudioWebScript()
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.DeclarativeSiteWebScript#createScriptParameters(org.alfresco.web.scripts.WebScriptRequest,
     *      org.alfresco.web.scripts.WebScriptResponse, java.util.Map)
     */
    protected Map<String, Object> createScriptParameters(WebScriptRequest req,
            WebScriptResponse res, Map<String, Object> customParams)
    {
        Map<String, Object> params = super.createScriptParameters(req, res,
                customParams);
        RequestContext context = getRequestContext(req);
        if (context != null)
        {
            ScriptWebStudio scriptWebStudio = new ScriptWebStudio(context);
            scriptWebStudio.setModel(params);
            params.put(ROOT_SCOPE_WEB_STUDIO, scriptWebStudio);
        }
        return params;
    }
}
