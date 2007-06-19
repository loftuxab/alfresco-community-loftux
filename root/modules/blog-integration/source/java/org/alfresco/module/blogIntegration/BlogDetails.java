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
 * http://www.alfresco.com/legal/licensing
 */
package org.alfresco.module.blogIntegration;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

/**
 * @author Roy Wetherall
 */
public class BlogDetails implements BlogIntegrationModel
{
    private NodeRef nodeRef;
    
    private String implementationName;
    
    private String blogId;
    
    private String connectionURL;
    
    private String userName;
    
    private String password;
    
    private String name;
    
    private String description;
    
    public static BlogDetails createBlogDetails(NodeService nodeService, NodeRef nodeRef)
    {
        // Check for the blog details aspect
        if (nodeService.hasAspect(nodeRef, ASPECT_BLOG_DETAILS) == false)
        {
            throw new BlogIntegrationRuntimeException("Can not create blog details object since node does not have blogDetails aspect.");
        }
        
        // Get the blog details
        Map<QName, Serializable> props = nodeService.getProperties(nodeRef);
        return new BlogDetails(
                (String)props.get(PROP_BLOG_IMPLEMENTATION),
                (String)props.get(PROP_ID),
                (String)props.get(PROP_CONNNECTION_URL),
                (String)props.get(PROP_USER_NAME),
                (String)props.get(PROP_PASSWORD),
                (String)props.get(PROP_NAME),
                (String)props.get(PROP_DESCRIPTION),
                nodeRef);        
    }
    
    public BlogDetails(String implementationName, String blogId, String connectionURL, String userName, String password, String name, String description)
    {
        this(implementationName, blogId, connectionURL, userName, password, name, description, null);
    }
    
    public BlogDetails(String implementationName, String blogId, String connectionURL, String userName, String password, String name, String description, NodeRef nodeRef)
    {
        this.implementationName = implementationName;
        this.blogId = blogId;
        this.connectionURL = connectionURL;
        this.userName = userName;
        this.password = password;
        this.name = name;
        this.description = description;
        this.nodeRef = nodeRef;
    }
    
    public NodeRef getNodeRef()
    {
        return nodeRef;
    }
    
    public String getImplementationName()
    {
        return this.implementationName;
    }
    
    public String getBlogId()
    {
        return this.blogId;
    }
    
    public String getConnectionURL()
    {
        return this.connectionURL;
    }
    
    public String getUserName()
    {
        return this.userName;
    }
    
    public String getPassword()
    {
        return this.password;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getDescription()
    {
        return description;
    }
}
