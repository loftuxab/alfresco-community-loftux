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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.web.framework.exception.RendererExecutionException;
import org.alfresco.web.framework.render.RenderContext;
import org.alfresco.web.framework.render.RenderHelper;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.RequestUtil;
import org.alfresco.web.site.exception.RequestContextException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author kevinr
 * @author muzquiano
 */
public class LocalWebScriptRuntimeContainer extends PresentationContainer
{
    private static Log logger = LogFactory.getLog(PresentationContainer.class);
    
    private ThreadLocal<RenderContext> renderContext = new ThreadLocal<RenderContext>();    
    
    protected void bindRenderContext(RenderContext context)
    {
        renderContext.set(context);
    }

    protected void unbindRenderContext()
    {
        renderContext.remove();
    }

    protected RenderContext getRenderContext()
    {
        return renderContext.get();
    }

    @Override
    public Map<String, Object> getScriptParameters()
    {
        // NOTE: returns unmodifable map from super
        Map<String, Object> params = new HashMap<String, Object>(8, 1.0f);
        params.putAll(super.getScriptParameters());
        
        // Bind in Web Script Model elements
        RenderContext context = getRenderContext();

        // populate the root script properties
        ProcessorModelHelper.populateScriptModel(context, params);

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
        WebFrameworkScriptRemote remote = new WebFrameworkScriptRemote(context);
        params.put("remote", remote);
        
        return params;
    }

    @Override
    public Map<String, Object> getTemplateParameters()
    {
        // NOTE: returns unmodifable map from super
        Map<String, Object> params = new HashMap<String, Object>(32, 1.0f);
        params.putAll(super.getTemplateParameters());
        
        // Bind in Template Model elements
        RenderContext context = getRenderContext();
        
        // populate the root template properties
        try
        {
            ProcessorModelHelper.populateTemplateModel(context, params);
        }
        catch (RendererExecutionException ree)
        {
            // This exception is only thrown when processing
            // template objects, thus it shouldn't occur for web scripts
        }
        catch (UnsupportedEncodingException uee)
        {
            // TODO: how to handle this?
        }
        
        return params;
    }
    
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.PresentationContainer#executeScript(org.alfresco.web.scripts.WebScriptRequest, org.alfresco.web.scripts.WebScriptResponse, org.alfresco.web.scripts.Authenticator)
     */
    public void executeScript(WebScriptRequest scriptReq, WebScriptResponse scriptRes, Authenticator auth)
        throws IOException
    {
        // Override this method so that we can ensure that a request context
        // is available.
        //
        // There are two ways that a web script can be executed.  First, it
        // could be executed by a Renderer (from the Web Framework).  If so,
        // the creation of a RequestContext will have been handled ahead of time.
        // In this case, we have a RendererContext instance.
        //
        // On the other hand, a web script could be executed by simply firing
        // at the WebScriptServlet.  If that is done, then a request context
        // is not created until this point.
        // In this case, we do not have a RendererContext instance.
        boolean handleBinding = false;
        
        RenderContext context = getRenderContext();
        if (context == null)
        {
            try
            {
                if (scriptReq instanceof org.alfresco.web.scripts.servlet.WebScriptServletRequest)
                {
                    if (scriptRes instanceof org.alfresco.web.scripts.servlet.WebScriptServletResponse)
                    {
                        HttpServletRequest request = ((org.alfresco.web.scripts.servlet.WebScriptServletRequest) scriptReq).getHttpServletRequest();
                        HttpServletResponse response = ((org.alfresco.web.scripts.servlet.WebScriptServletResponse) scriptRes).getHttpServletResponse();
                        
                        RequestContext requestContext = RequestUtil.getRequestContext(request);
                        context = RenderHelper.provideRenderContext(requestContext, request, response);
                        
                        // flag that we will manually handle the bindings
                        handleBinding = true;
                    }
                }
            }
            catch (RequestContextException rce)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Unable to retrieve the RequestContext instance from the current request");
                    logger.debug(rce);
                }
            }
        }
        
        // manually handle binding?
        if (handleBinding)
        {
            bindRenderContext(context);
        }
        
        try
        {
            // call through
            super.executeScript(scriptReq, scriptRes, auth);
        }
        finally
        {
            // manually handle binding?
            if (handleBinding)
            {
                unbindRenderContext();
            }
        }
    }
}
