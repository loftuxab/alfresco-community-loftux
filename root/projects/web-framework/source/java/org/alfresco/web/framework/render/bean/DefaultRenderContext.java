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
package org.alfresco.web.framework.render.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.alfresco.web.framework.render.AbstractRenderContext;
import org.alfresco.web.framework.render.RenderContext;
import org.alfresco.web.framework.render.RenderContextProvider;
import org.alfresco.web.framework.render.RenderMode;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.WrappedRequestContext;

/**
 * A render context instance is available to all rendering engines
 * and provides a convenient grab bag of things that are useful to
 * component or template developer.
 * 
 * @author muzquiano
 */
public class DefaultRenderContext extends AbstractRenderContext
{
	protected Map<String, Serializable> ourValuesMap;
	protected Map<String, Serializable> normalizedValuesMap;
	
	public DefaultRenderContext(RenderContextProvider provider, RequestContext context)
	{
		super(provider, context);
		
		if(context instanceof RenderContext)
		{
			RenderContext renderContext = (RenderContext) context;
			
			this.setRenderMode(renderContext.getRenderMode());
			this.setObject(renderContext.getObject());
			this.setPassiveMode(renderContext.isPassiveMode());			
		}
		else
		{
			this.setRenderMode(RenderMode.VIEW);			
		}
		
		this.ourValuesMap = new HashMap<String, Serializable>(16, 1.0f);
	}            
	
    public void setValue(String key, Serializable value)
    {
    	this.ourValuesMap.put(key, value);

    	// remove from our normalized depth map as well
    	if(this.normalizedValuesMap != null)
    	{
    		this.normalizedValuesMap.remove(key);
    	}    	
    }

    public Serializable getValue(String key)
    {
    	Serializable value = (Serializable) this.ourValuesMap.get(key);
    	if(value == null)
    	{
    		// check if a wrapped context has the value
    		value = this.getOriginalContext().getValue(key);
    	}
    	return value;
    }

    public void removeValue(String key)
    {
    	this.ourValuesMap.remove(key);
    	
    	// remove from our normalized depth map as well
    	if(this.normalizedValuesMap != null)
    	{
    		this.normalizedValuesMap.remove(key);
    	}
    }
    
    public boolean hasValue(String key)
    {
    	return this.ourValuesMap.containsKey(key);
    }
    
    public synchronized Map<String, Serializable> getValuesMap()
    {
    	if(this.normalizedValuesMap == null)
    	{
    		this.normalizedValuesMap = new HashMap<String, Serializable>(16, 1.0f);
    		
    		RequestContext rc = (RequestContext) this;
    		
    		// build the stack
    		Stack<RequestContext> stack = new Stack<RequestContext>();
    		boolean build = true;
    		while(build)
    		{
    			stack.push(rc);
    			
    			if(rc instanceof WrappedRequestContext)
    			{
    				rc = ((WrappedRequestContext)rc).getOriginalContext();
    			}
    			else
    			{
    				build = false;
    			}
    		}
    		
    		// pop out the stack and populate variables
    		while(stack.size() > 0)
    		{
    			rc = (RequestContext) stack.peek();
    			this.normalizedValuesMap.putAll(rc.getValuesMap());
    			
    			stack.pop();
    		}
    	}
    	
    	return this.normalizedValuesMap;
    }
    
    // TODO: Provide for namespaced parameters
    
    /*
    public Serializable getParameter(String key)
    {
    	return this.context.getParameter(key);
    }

    public boolean hasParameter(String key)
    {
    	return this.context.hasParameter(key);
    }
    
    public Map<String, Serializable> getParameters()
    {
    	return this.context.getParameters();
    }
    */

}
	