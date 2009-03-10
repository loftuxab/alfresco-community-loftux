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
package org.alfresco.web.framework.render;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.tools.FakeHttpServletResponse;
import org.alfresco.tools.WrappedHttpServletRequest;
import org.alfresco.web.framework.ModelObject;
import org.alfresco.web.site.WrappedRequestContext;

/**
 * A render context instance is available to all rendering engines
 * and provides a convenient grab bag of things that are useful to
 * component or template developer.
 * 
 * @author muzquiano
 */
final public class WrappedRenderContext
    extends WrappedRequestContext
    implements RenderContext, Serializable
{
    final private RenderContext renderContext;
    final private WrappedHttpServletRequest _request;
    final private FakeHttpServletResponse _response;
    
    public WrappedRenderContext(RenderContext context)
    {
        super(context);
        
        this.renderContext = context;
        
        // wrap the request
        HttpServletRequest request = renderContext.getRequest();
        _request = new WrappedHttpServletRequest(request);
        
        // fake the response object
        _response = new FakeHttpServletResponse(renderContext.getResponse());
    }
    
    public String getContentAsString()
        throws UnsupportedEncodingException
    {
        return _response.getContentAsString();
    }

    public RenderMode getRenderMode()
    {
        return this.renderContext.getRenderMode();
    }
    
    public void setRenderMode(RenderMode renderMode)
    {
        this.renderContext.setRenderMode(renderMode);
    }
        
    public HttpServletRequest getRequest()
    {
        return this._request;
    }
    
    public void setRequest(HttpServletRequest request)
    {
    }
    
    public HttpServletResponse getResponse()
    {
        return this._response;        
    }
    
    public void setResponse(HttpServletResponse response)
    {
    }
    
    public ModelObject getObject()
    {
        return this.renderContext.getObject();
    }
    
    public void setObject(ModelObject modelObject)
    {
        this.renderContext.setObject(modelObject);
    }
    
    public String getRenderId()
    {
        return "Wrapped" + this.renderContext.getRenderId();
    }
    
    public void setPassiveMode(boolean passiveMode)
    {
        this.renderContext.setPassiveMode(passiveMode);
    }
    
    public boolean isPassiveMode()
    {
        return this.renderContext.isPassiveMode();
    }
        
    public RenderContextProvider getProvider()
    {
        return this.renderContext.getProvider();
    }
    
    public void release()
    {
        this.renderContext.release();
    }
    
    public void setValue(String key, Serializable value, int scope)
    {
        this.renderContext.setValue(key, value, scope);
    }
    
    public Serializable getValue(String key, int scope)
    {
        return this.renderContext.getValue(key, scope);
    }
    
    public void removeValue(String key, int scope)
    {
        this.renderContext.removeValue(key, scope);
    }
    
    public boolean hasValue(String key, int scope)
    {
        return this.renderContext.hasValue(key, scope);
    }
}
    