package org.alfresco.wef;

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
        builder.append(", container: ");
        builder.append(this.container);
        builder.append(", dependencies: ");
        builder.append(getDependencies().size());
        builder.append(")");
        
        return builder.toString();
    }
}
