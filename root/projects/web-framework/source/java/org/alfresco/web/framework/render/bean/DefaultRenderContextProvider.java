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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.web.framework.ModelObject;
import org.alfresco.web.framework.model.Component;
import org.alfresco.web.framework.model.Page;
import org.alfresco.web.framework.model.TemplateInstance;
import org.alfresco.web.framework.render.AbstractRenderContextProvider;
import org.alfresco.web.framework.render.RenderContext;
import org.alfresco.web.framework.render.RenderMode;
import org.alfresco.web.framework.render.RenderUtil;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.WebFrameworkConstants;

/**
 * The Class DefaultRenderContextProvider.
 * 
 * @author muzquiano
 * @author kevinr
 */
public final class DefaultRenderContextProvider extends AbstractRenderContextProvider
{
	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.render.AbstractRenderContextProvider#release(org.alfresco.web.framework.render.RenderContext)
	 */
	public void release(RenderContext renderContext)
	{
		// nothing to do
	}

	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.render.RenderContextProvider#provide(org.alfresco.web.site.RequestContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public RenderContext provide(RequestContext requestContext,  HttpServletRequest request, HttpServletResponse response)
	{
		// create a render context
		RenderContext renderContext = new DefaultRenderContext(this, requestContext);
		
		// short-circuit through to original request and response for now
		renderContext.setRequest(request);
		renderContext.setResponse(response);
		
        // set to VIEW mode
		renderContext.setRenderMode(RenderMode.VIEW);		
		
		return renderContext;		
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.render.RenderContextProvider#provide(org.alfresco.web.framework.render.RenderContext)
	 */
	public RenderContext provide(RenderContext renderContext)
	{		
		// create a render context
		RenderContext newRenderContext = new DefaultRenderContext(this, renderContext);
		
		// short-circuit through to original request and response for now
        newRenderContext.setRequest(renderContext.getRequest());
        newRenderContext.setResponse(renderContext.getResponse());
        
        // set to VIEW mode if not otherwise set
        if(newRenderContext.getRenderMode() == null)
        {
        	newRenderContext.setRenderMode(RenderMode.VIEW);
        }
        
        return newRenderContext;                				
	}

	
	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.render.AbstractRenderContextProvider#merge(org.alfresco.web.framework.render.RenderContext, org.alfresco.web.framework.ModelObject)
	 */
	public void merge(RenderContext renderContext, ModelObject modelObject)
	{
        if (modelObject != null)
        {
        	renderContext.setObject(modelObject);
            mergeObject(renderContext, modelObject);
        }		
	}
            
    /**
     * Merge object properties into a render context.
     * 
     * @param context the context to merge properties into.
     * @param object the object to merge properties from.
     */
    private void mergeObject(RenderContext context, ModelObject object)
    {
        // switch on object type
        if (object instanceof Component)
        {
            mergeComponent(context, (Component) object);
        }
        else if (object instanceof Page)
        {
            mergePage(context, (Page) object);
        }
        else if (object instanceof TemplateInstance)
        {
            mergeTemplateInstance(context, (TemplateInstance) object);
        }        
    }

    /**
     * Populates the configuration for a page.
     * 
     * @param context the context
     * @param page the page
     */
    private void mergePage(RenderContext context, Page page)
    {
        // properties about the page
        context.setValue(WebFrameworkConstants.RENDER_DATA_PAGE_ID, page.getId());
        context.setValue(WebFrameworkConstants.RENDER_DATA_PAGE_TYPE_ID, page.getPageTypeId());
        
        // set htmlid
        context.setValue(WebFrameworkConstants.RENDER_DATA_HTMLID, RenderUtil.determineValidHtmlId(page.getId()));
    }
    
    
    /**
     * Populates the configuration for a template instance.
     * 
     * @param context the context
     * @param template the template
     */
    private void mergeTemplateInstance(RenderContext context, TemplateInstance template)
    {
        // properties about the template
        context.setValue(WebFrameworkConstants.RENDER_DATA_TEMPLATE_ID, template.getId());
        context.setValue(WebFrameworkConstants.RENDER_DATA_TEMPLATE_TYPE_ID, template.getTemplateType());

        // set htmlid
        context.setValue(WebFrameworkConstants.RENDER_DATA_HTMLID, RenderUtil.determineValidHtmlId(template.getId()));        
    }

    /**
     * Populates the configuration for a component.
     * 
     * @param context the context
     * @param component the component
     */
    private void mergeComponent(RenderContext context, Component component)
    {
        context.setValue(WebFrameworkConstants.RENDER_DATA_COMPONENT_ID, component.getId());
        context.setValue(WebFrameworkConstants.RENDER_DATA_COMPONENT_TYPE_ID, component.getComponentTypeId());
        context.setValue(WebFrameworkConstants.RENDER_DATA_COMPONENT_REGION_ID, component.getRegionId());
        context.setValue(WebFrameworkConstants.RENDER_DATA_COMPONENT_SOURCE_ID, component.getSourceId());
        context.setValue(WebFrameworkConstants.RENDER_DATA_COMPONENT_SCOPE_ID, component.getScope());
        
        // set htmlid
        context.setValue(WebFrameworkConstants.RENDER_DATA_HTMLID, RenderUtil.determineValidHtmlId(component.getId()));                
    }
}
