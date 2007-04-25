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

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ProcessorExtension;

import com.caucho.quercus.module.AbstractQuercusModule;
import com.caucho.quercus.module.QuercusModule;

/**
 * Base class representing an extension tot he PHP rpocessor that adds new methods.
 * 
 * @author Roy Wetherall
 */
public class PHPMethodExtension extends AbstractQuercusModule implements ProcessorExtension
{
    /** The name of the extension */
    protected String extensionName;
    
    /** The PHP processor */
    protected PHPProcessor phpProcessor;
    
    /** The service registry */
    protected ServiceRegistry serviceRegistry;    
    
    /**
     * Sets the service registry
     * 
     * @param serviceRegistry   the service registry
     */
    public void setServiceRegistry(ServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
    }
    
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
     * Sets the PHP Processor
     * 
     * @param phpProcessor  the PHP processor
     */
    public void setPhpProcessor(PHPProcessor phpProcessor)
    {
        this.phpProcessor = phpProcessor;
    }
    
    /**
     * Register the method extension wiht the PHP processor.
     */
    public void register()
    {
       this.phpProcessor.registerProcessorExtension(this);
    }
    
    /**
     * Callback used to copy across state to the Quercus module.  This is needed because the Quercus
     * library creates a new instance of the module once it hsa been added.
     * 
     * @param module    the Quercus module
     */
    public void initialiseModule(QuercusModule module)
    {
        PHPMethodExtension baseModule = (PHPMethodExtension)module;
        baseModule.extensionName = this.extensionName;
        baseModule.phpProcessor = this.phpProcessor;
        baseModule.serviceRegistry = this.serviceRegistry;
    }    
}
