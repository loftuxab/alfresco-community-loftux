/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
 * http://www.alfresco.com/legal/licensing
 */
package org.springframework.extensions.webeditor;

import java.util.List;

/**
 * Interface definition of a Web Editor Framework resource
 *
 * @author Gavin Cornwell
 */
public interface WEFResource
{
    /**
     * Returns the resource name.
     * 
     * @return The name
     */
    public String getName();
    
    /**
     * Returns the resource description.
     * 
     * @return The description
     */
    public String getDescription();
    
    /**
     * Returns the resource type.
     * 
     * @return The type
     */
    public String getType();
    
    /**
     * Returns the resource path.
     * 
     * @return The path
     */
    public String getPath();
    
    /**
     * Returns the resource varaible name
     * 
     * @return The variable name
     */
    public String getVariableName();
    
    /**
     * Returns the resource container.
     * 
     * @return The container
     */
    public String getContainer();
    
    /**
     * Returns a list of dependencies this resouce has.
     * 
     * @return The resource's dependencies
     */
    public List<WEFResource> getDependencies();
}
