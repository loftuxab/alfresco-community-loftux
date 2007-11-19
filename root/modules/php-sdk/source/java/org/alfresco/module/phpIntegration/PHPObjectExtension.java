/*
 * Copyright (C) 2005 Alfresco, Inc.
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
package org.alfresco.module.phpIntegration;

import org.alfresco.service.cmr.repository.ProcessorExtension;

import com.caucho.quercus.module.AbstractQuercusModule;

/**
 * 
 * 
 * @author Roy Wetherall
 */
public class PHPObjectExtension extends AbstractQuercusModule implements ProcessorExtension
{
    /** The name of the extension */
    protected String extensionName;
    
    /** The PHP processor */
    protected PHPProcessor phpProcessor;    
    
    /** The extension implementation class */
    protected String extensionClass;
    
    /**
     * Sets the extension name
     * 
     * @param extensionName     the extension name
     */
    public void setExtensionName(String extensionName)
    {
        this.extensionName = extensionName;
    }
    
    /**
     * @see org.alfresco.service.cmr.repository.ProcessorExtension#getExtensionName()
     */
    public String getExtensionName()
    {
        return this.extensionName;
    }
    
    /**
     * Sets the extension class name
     * 
     * @param extensionClass    the extension class name
     */
    public void setExtensionClass(String extensionClass)
    {
        this.extensionClass = extensionClass;
    }
    
    /**
     * Get the extension class
     * 
     * @return  String  the extension class
     */
    public String getExtensionClass()
    {
        return extensionClass;
    }
    
    /**
     * Sets the PHP Processor
     * 
     * @param phpProcessor  the PHP processor
     */
    public void setPhpProcessor(PHPProcessor phpProcessor)
    {
        this.phpProcessor = phpProcessor;
    }
    
    /**
     * Register the method extension with the PHP processor.
     */
    public void register()
    {
       this.phpProcessor.registerProcessorExtension(this);
    }      
}
