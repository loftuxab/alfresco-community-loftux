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

import java.util.Collections;
import java.util.List;

/**
 * Implementation of a WEFResource, representing a resource that can be 
 * referenced in a WEF based application. 
 *
 * @author Gavin Cornwell
 */
public class WEFResourceImpl implements WEFResource
{
    protected String name;
    protected String path;
    protected String type;
    protected String description;
    protected String variableName;
    protected String container;
    protected List<WEFResource> dependencies;

    /*
     * @see org.alfresco.wef.WEFResource#getName()
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Sets the resource name.
     * 
     * @param name The name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /*
     * @see org.alfresco.wef.WEFResource#getPath()
     */
    public String getPath()
    {
        return this.path;
    }
    
    /**
     * Sets the resource path.
     * 
     * @param path The path
     */
    public void setPath(String path)
    {
        this.path = path;
    }

    /*
     * @see org.alfresco.wef.WEFResource#getType()
     */
    public String getType()
    {
        return this.type;
    }
    
    /**
     * Sets the resource type.
     * 
     * @param type
     */
    public void setType(String type)
    {
        this.type = type;
    }
    
    /*
     * @see org.alfresco.wef.WEFResource#getDescription()
     */
    public String getDescription()
    {
        return this.description;
    }
    
    /**
     * Sets the resource description.
     * 
     * @param description The description
     */
    public void setDescription(String description)
    {
        this.description = description;
    } 
    
    /*
     * @see org.alfresco.wef.WEFResource#getVariableName()
     */
    public String getVariableName()
    {
        return this.variableName;
    }

    /**
     * Sets the resource variable name.
     * 
     * @param variableName The variable name
     */
    public void setVariableName(String variableName)
    {
        this.variableName = variableName;
    }

    /*
     * @see org.alfresco.wef.WEFResource#getDependencies()
     */
    public List<WEFResource> getDependencies()
    {
        if (this.dependencies != null)
        {
            return this.dependencies;
        }
        else
        {
            return Collections.emptyList();
        }
    }
    
    /**
     * Sets the resource's dependencies
     * 
     * @param dependencies List of dependencies
     */
    public void setDependencies(List<WEFResource> dependencies)
    {
        this.dependencies = dependencies;
    }
    
    /*
     * @see org.alfresco.wef.WEFResource#getContainer()
     */
    public String getContainer()
    {
        return this.container;
    }
    
    /**
     * Sets the resource container.
     * 
     * @param container The container
     */
    public void setContainer(String container)
    {
        this.container = container;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(super.toString());
        
        builder.append(" (name: ");
        builder.append(this.name);
        builder.append(", path: ");
        builder.append(this.path);
        builder.append(", type: ");
        builder.append(this.type);
        builder.append(", description: ");
        builder.append(this.description);
        builder.append(", variableName: ");
        builder.append(this.variableName);
        builder.append(", container: ");
        builder.append(this.container);
        builder.append(", dependencies: ");
        builder.append(getDependencies().size());
        builder.append(")");
        
        return builder.toString();
    }
}
