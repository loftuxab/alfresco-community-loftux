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
import org.alfresco.web.site.RequestUtil;
import org.alfresco.web.site.exception.RequestContextException;

/**
 * Declarative Web Script implementation that adds in additional root-scoped
 * objects for Extranet
 * 
 * @author muzquiano
 */
public class DeclarativeExtranetWebScript extends DeclarativeJSONWebScript
{
    private static final String ROOT_SCOPE_EXTRANET = "extranet";
    
    public DeclarativeExtranetWebScript()
    {
        super();
    }

    protected Map<String, Object> createScriptParameters(WebScriptRequest req,
            WebScriptResponse res, Map<String, Object> customParams)
    {
        Map<String, Object> params = super.createScriptParameters(req, res,
                customParams);
        
        RequestContext context = getRequestContext(req);
        if (context != null)
        {
            ScriptExtranet scriptExtranet = new ScriptExtranet(context);
            params.put(ROOT_SCOPE_EXTRANET, scriptExtranet);
        }
        return params;
    }

    /**
     * Gets the request context.
     * 
     * @param req the req
     * 
     * @return the request context
     */
    protected RequestContext getRequestContext(WebScriptRequest req)
    {
        RequestContext context = null;
        try
        {
            context = RequestUtil.getRequestContext(req);
        }
        catch(RequestContextException rce)
        {
            rce.printStackTrace();
        }
        return context;
    }
}
