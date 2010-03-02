/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
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
