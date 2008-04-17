/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 * As a special exception to the terms and conditions of version 2.0 of the GPL,
 * you may redistribute this Program in connection with Free/Libre and Open
 * Source Software ("FLOSS") applications as described in Alfresco's FLOSS
 * exception. You should have recieved a copy of the text describing the FLOSS
 * exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing
 */
package org.alfresco.web.scripts;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.web.site.HttpRequestContext;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.ThemeUtil;

/**
 * @author muzquiano
 */
public class LocalWebScriptRuntimeContainer extends PresentationContainer
{
    private ThreadLocal<RequestContext> requestContext = new ThreadLocal<RequestContext>();

    protected void bindRequestContext(RequestContext context)
    {
        requestContext.set(context);
    }

    protected void unbindRequestContext()
    {
        requestContext.remove();
    }

    protected RequestContext getRequestContext()
    {
        return requestContext.get();
    }

    @Override
    public Map<String, Object> getScriptParameters()
    {
        // NOTE: returns unmodifable map from super
        Map<String, Object> params = new HashMap<String, Object>(8, 1.0f);
        params.putAll(super.getScriptParameters());
        
        // Bind in Web Script Model elements
        RequestContext context = getRequestContext();
        if(context != null)
        {
            ModelHelper.populateScriptModel(context, params);
        }

        return params;
    }

    @Override
    public Map<String, Object> getTemplateParameters()
    {
        // NOTE: returns unmodifable map from super
        Map<String, Object> params = new HashMap<String, Object>(8, 1.0f);
        params.putAll(super.getTemplateParameters());
        
        // Bind in Template Model elements
        RequestContext context = getRequestContext();
        if(context != null)
        {
            ModelHelper.populateTemplateModel(context, params);
        }

        return params;
    }
}
