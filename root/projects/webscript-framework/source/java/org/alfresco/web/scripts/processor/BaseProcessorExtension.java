/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
package org.alfresco.web.scripts.processor;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.processor.Processor;
import org.alfresco.processor.ProcessorExtension;

/**
 * Abstract base class for a processor extension in the presentation tier.
 * 
 * {@link org.alfresco.repo.processor.BaseProcessorExtension}
 */
public abstract class BaseProcessorExtension implements ProcessorExtension
{
	/** The list of processors */
	private List<Processor> processors = null;
	
	/** The name of the extension */
	private String extensionName;
	
	
	/**
	 * Sets the processor list.
	 * 
	 * @param processor		  The processor list
	 */
	public void setProcessors(List<Processor> processors)
	{
		this.processors = processors;
	}
	
	/**
	 * Spring bean init method - registers this extension with the appropriate processor.
	 */
	public void register()
	{
	    if (this.processors != null)
	    {
	        for (Processor processor : this.processors)
	        {
	            processor.registerProcessorExtension(this);
	        }
	    }
	}
	
	/**
	 * Sets the extension name.
	 * 
	 * @param extensionName the extension name
	 */
	public void setExtensionName(String extension)
	{
		this.extensionName = extension;
	}
    
    /**
     * @see org.alfresco.processor.ProcessorExtension#getExtensionName()
     */
    public String getExtensionName()
    {
    	return this.extensionName;
    }
}