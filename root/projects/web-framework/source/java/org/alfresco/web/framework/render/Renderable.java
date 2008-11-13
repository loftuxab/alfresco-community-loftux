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

import java.util.Map;

/**
 * Implemented by object types which wish to expose a set of renderer
 * configurations.  Each renderer configuration identifies a processor
 * id and provides the processor with information about how to render.
 * 
 * A renderable object is one that has renderer processors
 * defined on it for one or more render modes. 
 * 
 * @author muzquiano
 */
public interface Renderable 
{
    /*

    JSP EXAMPLE:

    <processor mode="view">
       <id>jsp</id>
       <url>/abc/view.jsp</url>
    </processor>    
    <processor mode="edit">
       <id>jsp</id>
       <url>/abc/edit.jsp</url>
    </processor>
    
    
    WEBSCRIPT:

    <processor mode="view">
       <id>webscript</id>
    </processor>
    <processor mode="edit">
       <id>webscript</id>
       <uri>${mode.view.uri}/edit</uri>
    </processor>

     */
    
    /**
     * The list of defined render modes
     * 
     * @return an array of render modes
     */
    public String[] getRenderModes();

    /**
     * Gets the default 'view' processor id
     * 
     * @return the processor id
     */
    public String getProcessorId();
    
    /**
     * Gets the processor id
     * 
     * @param renderMode
     * 
     * @return the processor id
     */
    public String getProcessorId(String renderMode);

    /**
     * Gets a default 'view' processor property
     * 
     * @param propertyName
     * 
     * @return the processor property value
     */
    public String getProcessorProperty(String propertyName);
    
    /**
     * Gets a processor property
     * 
     * @param renderMode
     * @param propertyName
     * 
     * @return the processor property value
     */
    public String getProcessorProperty(String renderMode, String propertyName);
    
    /**
     * Gets a map of default 'view' processor properties
     *  
     * @return the map
     */
    public Map<String, String> getProcessorProperties();

    /**
     * Gets a map of processor properties for the given mode
     *  
     * @param renderMode the render mode
     * 
     * @return the map
     */    
    public Map<String, String> getProcessorProperties(String renderMode);

    /**
     * Sets the processor id.
     * 
     * @param processorId the id of the processor
     */
    public void setProcessorId(String processorId);

    /**
     * Sets the processor id for a given mode
     * 
     * @param renderMode the render mode
     * @param processorId the id of the processor 
     */
    public void setProcessorId(String renderMode, String processorId);

    /**
     * Sets a property on the default 'view' processor
     * 
     * @param propertyName
     * @param propertyValue
     */
    public void setProcessorProperty(String propertyName, String propertyValue);

    /**
     * Sets a processor property for a given render mode
     * 
     * @param renderMode the render mode
     * @param propertyName
     * @param propertyValue
     */
    public void setProcessorProperty(String renderMode, String propertyName, String propertyValue);
    
    /**
     * Removes the default processor
     */
    public void removeProcessor();

    /**
     * Removes the processor for the given render mode
     * 
     * @param renderMode the renderer mode
     */
    public void removeProcessor(String renderMode);    
}