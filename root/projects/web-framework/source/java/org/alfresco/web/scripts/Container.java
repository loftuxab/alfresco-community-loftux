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
package org.alfresco.web.scripts;

import java.util.Map;


/**
 * Web Script Context
 * 
 * @author davidc
 */
public interface Container
{
    public ServerModel getDescription();
    
    public Map<String, Object> getScriptParameters();
    
    public Map<String, Object> getTemplateParameters();    
    
    /**
     * Gets the response format registry
     * 
     * @return  response format registry
     */
    public FormatRegistry getFormatRegistry();
    
    /**
     * Gets the Template Processor
     *  
     * @return  template processor
     */
    public TemplateProcessor getTemplateProcessor();
    
    /**
     * Gets the Template Image Resolver
     * 
     * @return  template image resolver
     */
     // TODO:
//    public TemplateImageResolver getTemplateImageResolver();
    
    /**
     * Gets the Script Processor
     * 
     * @return  script processor
     */
    public ScriptProcessor getScriptProcessor();
    
    
    public Registry getRegistry();
    
    public void reset();    

}
