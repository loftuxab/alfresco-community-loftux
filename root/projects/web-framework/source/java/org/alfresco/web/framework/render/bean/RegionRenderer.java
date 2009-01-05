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

import java.io.IOException;

import org.alfresco.web.framework.exception.RegionRendererExecutionException;
import org.alfresco.web.framework.exception.RendererExecutionException;
import org.alfresco.web.framework.model.Chrome;
import org.alfresco.web.framework.model.TemplateInstance;
import org.alfresco.web.framework.render.AbstractRenderer;
import org.alfresco.web.framework.render.RenderContext;
import org.alfresco.web.framework.render.RenderFocus;
import org.alfresco.web.framework.render.RenderHelper;
import org.alfresco.web.framework.render.RenderUtil;
import org.alfresco.web.framework.render.Renderer;
import org.alfresco.web.framework.render.RendererType;
import org.alfresco.web.site.Timer;
import org.alfresco.web.site.WebFrameworkConstants;

/**
 * Bean responsible for rendering a region.
 * 
 * The bean should set up render context state and then hand off to a
 * region chrome renderer.  If no chrome is present, then it call call
 * through to the RenderUtil helper method directly.
 * 
 * @author muzquiano
 */
public class RegionRenderer extends AbstractRenderer
{
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.AbstractRenderer#header(org.alfresco.web.framework.render.RenderContext)
     */
    public void header(RenderContext parentContext)
        throws RendererExecutionException
    {
        super.header(parentContext);
        
        TemplateInstance template = (TemplateInstance) parentContext.getObject();

        // values from the render context
        String regionId = (String) parentContext.getValue(WebFrameworkConstants.RENDER_DATA_REGION_ID);
        String regionScopeId = (String) parentContext.getValue(WebFrameworkConstants.RENDER_DATA_REGION_SCOPE_ID);
        String regionChromeId = (String) parentContext.getValue(WebFrameworkConstants.RENDER_DATA_REGION_CHROME_ID);
        
        // create a new render context (for the chrome)
        // execute the renderer
        RenderContext context = RenderHelper.provideRenderContext(parentContext);
        try
        {
            // start a timer
            if (Timer.isTimerEnabled())
                Timer.start(context, "RegionRenderer-" + template.getId() + "-" + regionId + "-" + regionScopeId);
            
            // call thru directly to renderRegionComponents
            // this is what the regionInclude tag in the region chrome
            // would do anyway
            RenderUtil.renderRegionComponents(context);
        }
        catch (Exception ex)
        {
            throw new RegionRendererExecutionException("Unable to render region: " + regionId, ex);
        }
        finally
        {
            // release the render context
            context.release();

            if (Timer.isTimerEnabled())
                Timer.stop(context, "RegionRenderer-" + template.getId() + "-" + regionId + "-" + regionScopeId);
        }    
    }    
    
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.AbstractRenderer#body(org.alfresco.web.framework.render.RendererContext)
     */
    public void body(RenderContext parentContext)
        throws RendererExecutionException
    {
        TemplateInstance template = (TemplateInstance) parentContext.getObject();

        // values from the render context
        String regionId = (String) parentContext.getValue(WebFrameworkConstants.RENDER_DATA_REGION_ID);
        String regionScopeId = (String) parentContext.getValue(WebFrameworkConstants.RENDER_DATA_REGION_SCOPE_ID);
        String regionChromeId = (String) parentContext.getValue(WebFrameworkConstants.RENDER_DATA_REGION_CHROME_ID);
        
        // create a new render context (for the chrome)
        // execute the renderer
        RenderContext context = RenderHelper.provideRenderContext(parentContext);
        try
        {
            // start a timer
            if (Timer.isTimerEnabled())
                Timer.start(context, "RegionRenderer-" + template.getId() + "-" + regionId + "-" + regionScopeId);
            
            // fetch the appropriate chrome instance
            Chrome chrome = RenderUtil.getRegionChrome(context, template, regionId, regionChromeId);
            
            // if we have chrome, process it
            if (chrome != null)
            {
                // bind the render context to this chrome
                RenderHelper.mergeRenderContext(context, chrome);
                          
                // loads the "chrome renderer" bean and executes it
                Renderer renderer = RenderHelper.getRenderer(RendererType.CHROME);
                renderer.render(context, RenderFocus.BODY);
            }
            else
            {
                // call thru directly to renderRegionComponents
                // this is what the regionInclude tag in the region chrome
                // would do anyway
                RenderUtil.renderRegionComponents(context);
            }
            
            // post process call
            postProcess(context);
        }
        catch (Exception ex)
        {
            throw new RegionRendererExecutionException("Unable to render region: " + regionId, ex);
        }
        finally
        {
            // release the render context
            context.release();

            if (Timer.isTimerEnabled())
                Timer.stop(context, "RegionRenderer-" + template.getId() + "-" + regionId + "-" + regionScopeId);
        }    
    }    
    
    /**
     * Post-processing of regions
     */
    public void postProcess(RenderContext context)
        throws IOException
    {
    }
}