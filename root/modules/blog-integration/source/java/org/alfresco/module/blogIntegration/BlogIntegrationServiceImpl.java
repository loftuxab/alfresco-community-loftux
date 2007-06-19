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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

/**
 * 
 * @author Roy Wetherall
 */
public class BlogIntegrationServiceImpl implements BlogIntegrationService, BlogIntegrationModel
{
    private NodeService nodeService;
    private ContentService contentService;
    
    private Map<String, BlogIntegrationImplementation> implementations = new HashMap<String, BlogIntegrationImplementation>(5); 
    private List<String> supportedMimetypes = new ArrayList<String>(5);
    
    public BlogIntegrationServiceImpl()
    {
        this.supportedMimetypes.add(MimetypeMap.MIMETYPE_TEXT_PLAIN);
        this.supportedMimetypes.add(MimetypeMap.MIMETYPE_HTML);
    }
    
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    public void setContentService(ContentService contentService)
    {
        this.contentService = contentService;
    }
    
    public void register(BlogIntegrationImplementation implementation)
    {
        if (this.implementations.containsKey(implementation.getName()) == true)
        {
            throw new BlogIntegrationRuntimeException("A blog implementation with name '" + implementation.getName() + "' has already been registered.");
        }
        this.implementations.put(implementation.getName(), implementation);
    }
    
    private BlogIntegrationImplementation getImplementation(String implementationName)
    {
        if (this.implementations.containsKey(implementationName) == false)
        {
            throw new BlogIntegrationRuntimeException("There is no blog implementation present for '" + implementationName + "'");
        }
        return this.implementations.get(implementationName);
    }
    
    public void newPost(BlogDetails blogDetails, NodeRef nodeRef, QName contentProperty, boolean publish)
    {
        // Get the blog implementation
        BlogIntegrationImplementation implementation = getImplementation(blogDetails.getImplementationName());
        
        // Check that this node has not already been posted to a blog
        if (this.nodeService.hasAspect(nodeRef, ASPECT_BLOG_POST) == true)
        {
            throw new BlogIntegrationRuntimeException("Can not create new blog post since this conten has already been posted to a blog.");
        }
        
        // Get the posts title
        String title = (String)this.nodeService.getProperty(nodeRef, ContentModel.PROP_TITLE);
        if (title == null || title.length() == 0)
        {
            throw new BlogIntegrationRuntimeException("No title available for new blog post.  Set the title property and re-try.");
        }
        
        // Get the posts body
        ContentReader contentReader = this.contentService.getReader(nodeRef, contentProperty);
        if (contentReader == null)
        {
            throw new BlogIntegrationRuntimeException("No content found for new blog entry.");
        }
        
        // Check the mimetype
        String body = null;
        if (this.supportedMimetypes.contains(contentReader.getMimetype()) == true)
        {
            // Get the content
            body = contentReader.getContentString();
        }
        else
        {
            throw new BlogIntegrationRuntimeException("The content mimetype '" + contentReader.getMimetype() + "' is not supported.");
        }
        
        // Post the new blog entry
        String postId = implementation.newPost(blogDetails, title, body, true);
        
        // Get the blog details node if the is one
        NodeRef blogDetailsNodeRef = blogDetails.getNodeRef();
        if (blogDetailsNodeRef != null)
        {
            // Add the details of the new post to the node
            Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
            props.put(PROP_POST_ID, postId);
            this.nodeService.addAspect(nodeRef, ASPECT_BLOG_POST, props);
            
            // Associate to the blog details
            this.nodeService.createAssociation(nodeRef, blogDetailsNodeRef, ASSOC_BLOG_DETAILS);
        }
    }
    
    public void updatePost(String postId, NodeRef nodeRef, QName contentProperty, boolean publish)
    {
        // TODO Auto-generated method stub
        
    }

    public void deletePost(String postId, NodeRef nodeRef)
    {
        // TODO Auto-generated method stub
        
    }



    

}
