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

import org.alfresco.web.framework.exception.ProcessorExecutionException;
import org.alfresco.web.framework.exception.RendererExecutionException;
import org.springframework.context.ApplicationContext;

public interface Processor 
{
	/**
	 * Initialisation hook point.
	 * 
	 * This will be called exactly once when the processor is instantiated.
	 * It does not have access to request time information.
	 * 
	 * @param applicationContext the application context
	 */
    public void init(ApplicationContext applicationContext)
    	throws ProcessorExecutionException;
    
    /**
     * Executes the given focus of the processor output using the
     * given processor context
     * 
     * @param processorContext
     * @param focus
     * 
     * @throws RendererExecutionException
     */
    public void execute(ProcessorContext processorContext, RenderFocus focus)
    	throws RendererExecutionException;
    
    /**
     * Executes the "body" of the processor output using the given
     * processor context.
     * 
     * @param processorContext processorContext
     * 
     * @throws RendererExecutionException
     */
    public void executeBody(ProcessorContext processorContext)
    	throws RendererExecutionException;

    /**
     * Executes the "header" of the processor output using the given
     * processor context.
     * 
     * @param processorContext processorContext
     * 
     * @throws RendererExecutionException
     */
    public void executeHeader(ProcessorContext processorContext)
    	throws RendererExecutionException;

    /**
     * Executes the "footer" of the processor output using the given
     * processor context.
     * 
     * @param processorContext processorContext
     * 
     * @throws RendererExecutionException
     */
    public void executeFooter(ProcessorContext processorContext)
    	throws RendererExecutionException;
    
}
