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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.web.framework.ModelObject;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.WrappedRequestContext;

/**
 * An abstract base class which developers can use to extend and build
 * their own implementations of render context types.
 * 
 * This wraps the request context for you automatically and provides
 * callthrus to the underlying request context.
 * 
 * It also establishes the idea of connecting the provider to the
 * render context so that threading is possible.
 * 
 * @author muzquiano
 */
public abstract class AbstractRenderContext extends WrappedRequestContext implements RenderContext, Serializable
{
    // the provider
    protected RenderContextProvider provider;
    
    // other attributes
    protected ModelObject object;
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected RenderMode mode = null;
    protected String renderId = null;
    protected boolean passiveMode = false;
    
    /**
     * Increments every time a request id is required
     */
    protected static int idCounter = 0;
    
    
    /**
     * Constructor
     * 
     * @param provider
     * @param requestContext
     */
    public AbstractRenderContext(RenderContextProvider provider, RequestContext requestContext)
    {
        super(requestContext);
        this.provider = provider;
    }

    // methods from RenderContext
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.RenderContext#getRenderMode()
     */
    public RenderMode getRenderMode()
    {
        return this.mode;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.RenderContext#setRenderMode(org.alfresco.web.framework.render.RenderMode)
     */
    public void setRenderMode(RenderMode mode)
    {
        this.mode = mode;
    }
        
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.RenderContext#getRequest()
     */
    public HttpServletRequest getRequest()
    {
        return this.request;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.RenderContext#setRequest(javax.servlet.http.HttpServletRequest)
     */
    public void setRequest(HttpServletRequest request)
    {
        this.request = request;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.RenderContext#getResponse()
     */
    public HttpServletResponse getResponse()
    {
        return this.response;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.RenderContext#setResponse(javax.servlet.http.HttpServletResponse)
     */
    public void setResponse(HttpServletResponse response)
    {
        this.response = response;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.RenderContext#getObject()
     */
    public ModelObject getObject()
    {
        return this.object;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.RenderContext#setObject(org.alfresco.web.framework.ModelObject)
     */
    public void setObject(ModelObject object)
    {
        this.object = object;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.RenderContext#getRenderId()
     */
    public String getRenderId()
    {
        synchronized (AbstractRenderContext.class)
        {
            if (this.renderId == null)
            {
                idCounter++;
                this.renderId = Integer.toString(idCounter);
            }
        }
        return this.renderId;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.RequestContext#getId()
     */
    public String getId()
    {
        if (this.object != null)
        {
            return this.object.getTypeId() + "___" + this.object.getId();
        }
        return "unknown";        
    }
            
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.RenderContext#getProvider()
     */
    public RenderContextProvider getProvider()
    {
        return this.provider;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.RenderContext#finish()
     */
    public void release()
    {
        this.provider.release(this);
    }
        
    @Override
    public String toString()
    {
        return "RenderContext-" + getId();
    }    
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.RenderContext#setPassiveMode(boolean)
     */
    public void setPassiveMode(boolean passiveMode)
    {
        this.passiveMode = passiveMode;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.RenderContext#isPassiveMode()
     */
    public boolean isPassiveMode()
    {
        return this.passiveMode;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.RenderContext#setValue(java.lang.String, java.io.Serializable, int)
     */
    public void setValue(String key, Serializable value, int scope)
    {
        if (scope == RenderContext.SCOPE_REQUEST)
        {
            this.getOriginalContext().setValue(key, value);
        }
        else
        {
            this.setValue(key, value);
        }
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.RenderContext#getValue(java.lang.String, int)
     */
    public Serializable getValue(String key, int scope)
    {
        Serializable value = null;
        
        if (scope == RenderContext.SCOPE_REQUEST)
        {
            value = this.getOriginalContext().getValue(key);
        }
        else
        {
            value = this.getValue(key);
        }
        
        return value;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.RenderContext#removeValue(java.lang.String, int)
     */
    public void removeValue(String key, int scope)
    {
        if (scope == RenderContext.SCOPE_REQUEST)
        {
            this.getOriginalContext().removeValue(key);
        }
        else
        {
            this.removeValue(key);
        }        
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.RenderContext#hasValue(java.lang.String, int)
     */
    public boolean hasValue(String key, int scope)
    {
        boolean has = false;
        
        if (scope == RenderContext.SCOPE_REQUEST)
        {
            has = this.getOriginalContext().hasValue(key);
        }
        else
        {
            has = this.hasValue(key);
        }        
        
        return has;
    }
}
    