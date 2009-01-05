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

import org.alfresco.web.framework.exception.RendererExecutionException;
import org.alfresco.web.framework.render.ProcessorContext.ProcessorDescriptor;
import org.alfresco.web.site.FrameworkHelper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public abstract class AbstractProcessor implements Processor, ApplicationListener, ApplicationContextAware
{
    private ApplicationContext applicationContext = null;
    
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    public void onApplicationEvent(ApplicationEvent event)
    {
        if (event instanceof ContextRefreshedEvent)
        {
            ContextRefreshedEvent refreshEvent = (ContextRefreshedEvent)event;
            ApplicationContext refreshContext = refreshEvent.getApplicationContext();
            if (refreshContext != null && refreshContext.equals(applicationContext))
            {
                init(applicationContext);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext)
        throws BeansException
    {
        this.applicationContext = applicationContext;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.Processor#init(org.alfresco.web.framework.render.DispatcherContext)
     */
    public void init(ApplicationContext applicationContext)
    {
        if (FrameworkHelper.getLogger().isDebugEnabled())
        {
            FrameworkHelper.getLogger().debug(this.getClass().getName() + " init");
        }
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.Processor#execute(org.alfresco.web.framework.render.ProcessorContext, org.alfresco.web.framework.render.RenderFocus)
     */
    public void execute(ProcessorContext processorContext, RenderFocus focus)
        throws RendererExecutionException
    {    
        if (focus == null || focus == RenderFocus.BODY)
        {
            executeBody(processorContext);
        }
        else if (focus == RenderFocus.ALL)
        {
            executeHeader(processorContext);
            executeBody(processorContext);
            executeFooter(processorContext);
        }
        else if (focus == RenderFocus.HEADER)
        {
            executeHeader(processorContext);
        }
        else if (focus == RenderFocus.FOOTER)
        {
            executeFooter(processorContext);
        }        
    }
        
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.Processor#executeHeader(org.alfresco.web.framework.render.ProcessorContext)
     */
    public void executeHeader(ProcessorContext processorContext)
        throws RendererExecutionException
    {
        // nothing
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.Processor#executeBody(org.alfresco.web.framework.render.ProcessorContext)
     */
    public abstract void executeBody(ProcessorContext processorContext)
        throws RendererExecutionException;
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.Processor#executeFooter(org.alfresco.web.framework.render.ProcessorContext)
     */
    public void executeFooter(ProcessorContext processorContext)
        throws RendererExecutionException
    {
        // nothing
    }
    

    /**
     * Helper method which returns the appropriate processor descriptor
     * from the given processor descriptor for the current render mode.
     * 
     * For example, if the current render mode is "view", then this will
     * return the processor descriptor for the processor to be used during
     * "view" processing.  This descriptor contains all of the relevant
     * metadata for the processor about how to proceed.
     * 
     * @param processorContext
     * @return processor descriptor
     */
    protected ProcessorDescriptor getRenderingDescriptor(ProcessorContext processorContext)
    {
        RenderContext context = processorContext.getRenderContext();
        RenderMode renderMode = context.getRenderMode();
        
        return getRenderingDescriptor(processorContext, renderMode);
    }

    protected ProcessorDescriptor getRenderingDescriptor(ProcessorContext processorContext, RenderMode renderMode)
    {
        return processorContext.getDescriptor(renderMode);
    }
    
    /**
     * Returns a configuration property from the rendering
     * processor descriptor.
     * 
     * @param processorContext
     * @param propertyName
     * 
     * @return property value as string
     */
    protected String getProperty(ProcessorContext processorContext, String propertyName)
    {
        ProcessorDescriptor descriptor = getRenderingDescriptor(processorContext);
        String value = descriptor.get(propertyName);
        
        // allow for simple variable substitution
        // ${mode.view.uri} = uri property value where mode="view"
        if (value != null)
        {
            String modeViewUri = getScalarProperty(processorContext, "uri", RenderMode.VIEW);
            if (value.indexOf("${mode.view.uri}") != -1)
            {
                value = value.replace("${mode.view.uri}", modeViewUri);
            }
        }
        
        return value;
    }

    private String getScalarProperty(ProcessorContext processorContext, String propertyName, RenderMode renderMode)
    {
        ProcessorDescriptor descriptor = getRenderingDescriptor(processorContext, renderMode);
        return descriptor.get(propertyName);
    }    
}
