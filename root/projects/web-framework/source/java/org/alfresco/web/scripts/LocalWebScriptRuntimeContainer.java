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

import org.alfresco.web.site.exception.RendererExecutionException;
import org.alfresco.web.site.renderer.RendererContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author kevinr
 * @author muzquiano
 */
public class LocalWebScriptRuntimeContainer extends PresentationContainer
{
    private static Log logger = LogFactory.getLog(PresentationContainer.class);
    
    private ThreadLocal<RendererContext> rendererContext = new ThreadLocal<RendererContext>();
    
    
    protected void bindRendererContext(RendererContext context)
    {
        rendererContext.set(context);
    }

    protected void unbindRendererContext()
    {
        rendererContext.remove();
    }

    protected RendererContext getRendererContext()
    {
        return rendererContext.get();
    }

    @Override
    public Map<String, Object> getScriptParameters()
    {
        // NOTE: returns unmodifable map from super
        Map<String, Object> params = new HashMap<String, Object>(8, 1.0f);
        params.putAll(super.getScriptParameters());
        
        // Bind in Web Script Model elements
        RendererContext rendererContext = getRendererContext();

        // populate the root script properties
        ProcessorModelHelper.populateScriptModel(rendererContext, params);

        /**
         * Override the "remote" object with a slightly better 
         * implementation that takes into account the credential
         * vault which is under management by the web framework
         * 
         * This allows script developers to make calls like:
         * 
         * var myRemote = remote.connect("alfresco");
         * var json = myRemote.call("/content/get?nodeRef=abc");
         * 
         * All while in the context of the current user
         */
        WebFrameworkScriptRemote remote = new WebFrameworkScriptRemote(rendererContext.getRequestContext());
        params.put("remote", remote);
        
        return params;
    }

    @Override
    public Map<String, Object> getTemplateParameters()
    {
        // NOTE: returns unmodifable map from super
        Map<String, Object> params = new HashMap<String, Object>(8, 1.0f);
        params.putAll(super.getTemplateParameters());
        
        // Bind in Template Model elements
        RendererContext rendererContext = getRendererContext();
        
        // populate the root template properties
        try
        {
            ProcessorModelHelper.populateTemplateModel(rendererContext, params);
        }
        catch(RendererExecutionException ree)
        {
            // This exception is only thrown when processing
            // template objects, thus it shouldn't occur for web scripts
        }
        
        return params;
    }
}
