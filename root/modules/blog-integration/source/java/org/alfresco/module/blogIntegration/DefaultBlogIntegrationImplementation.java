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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

/**
 * Default blog integration implementation.  Uses various standard XML PRC blogging API to satisfy the 
 * blog integration implementation interface.
 * 
 * Based on origional contribution by Sudhakar Selvaraj.
 * 
 * @author Roy Wetherall
 */
public class DefaultBlogIntegrationImplementation extends BaseBlogIntegrationImplementation
{
    /** Blog actions */
    private static final String ACTION_NEW_POST = "metaWeblog.newPost";
    private static final String ACTION_EDIT_POST = "metaWeblog.editPost";
    private static final String ACTION_GET_POST = "metaWeblog.getPost";
    private static final String ACTION_DELETE_POST = "blogger.deletePost";
    
    /**
     * @see org.alfresco.module.blogIntegration.BlogIntegrationImplementation#newPost(org.alfresco.module.blogIntegration.BlogDetails, java.lang.String, java.lang.String, boolean)
     */
    public String newPost(BlogDetails blogDetails, String title, String body, boolean publish)
    {
       // Create the hash table containing details of the post's content
        Hashtable<String, Object> content = new Hashtable<String, Object>();
        content.put("title", title);
        content.put("description", body);
        
        // Create a list of parameters
        List<Object> params = new ArrayList<Object>(5);     
        params.add(blogDetails.getBlogId());
        params.add(blogDetails.getUserName());
        params.add(blogDetails.getPassword()); 
        params.add(content); 
        params.add(publish);
        
        // Create the new post
        return (String)execute(blogDetails.getConnectionURL(), ACTION_NEW_POST, params);
    }

    /**
     * @see org.alfresco.module.blogIntegration.BlogIntegrationImplementation#updatePost(org.alfresco.module.blogIntegration.BlogDetails, java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    public boolean updatePost(BlogDetails blogDetails, String postId, String title, String body, boolean publish)
    {
        // Create the hash table containing details of the post's content
        Hashtable<String, Object> content = new Hashtable<String, Object>();
        content.put("title", title);
        content.put("description", body);
        
        // Create a list of parameters
        List<Object> params = new ArrayList<Object>(5);     
        params.add(postId);
        params.add(blogDetails.getUserName());
        params.add(blogDetails.getPassword()); 
        params.add(content); 
        params.add(publish);
        
        // Create the new post
        Boolean result = (Boolean)execute(blogDetails.getConnectionURL(), ACTION_EDIT_POST, params);        
        return result.booleanValue();
    }
    
    public Map<String, Object> getPost(BlogDetails blogDetails, String postId)
    {
        // Create a list of parameters
        List<Object> params = new ArrayList<Object>(3);     
        params.add(postId);
        params.add(blogDetails.getUserName());
        params.add(blogDetails.getPassword()); 

        // Get the post details
        return (Map<String, Object>)execute(blogDetails.getConnectionURL(), ACTION_GET_POST, params);        
    }

    /**
     * @see org.alfresco.module.blogIntegration.BlogIntegrationImplementation#deletePost(org.alfresco.module.blogIntegration.BlogDetails, java.lang.String)
     */
    public boolean deletePost(BlogDetails blogDetails, String postId)
    {
        // Create a list of parameters
        List<Object> params = new ArrayList<Object>(5);        
        // Use the blog id for the app key
        params.add(blogDetails.getBlogId()); 
        params.add(postId); 
        params.add(blogDetails.getUserName()); 
        params.add(blogDetails.getPassword());
        params.add(true); 
        
        // Delete post
        Boolean result = (Boolean)execute(blogDetails.getConnectionURL(), ACTION_DELETE_POST, params);        
        return result.booleanValue();
    }
    
    /**
     * Helper method to get the XML RPC client
     * 
     * @param url
     * @return
     */
    private XmlRpcClient getClient(String url)
    {    
        XmlRpcClient client = null;
        try
        {
            // Init the client with url
            XmlRpcClientConfigImpl xmlrpcConfig = new XmlRpcClientConfigImpl();
            xmlrpcConfig.setServerURL(new URL(url));
            
            // Create the client
            client = new XmlRpcClient();
            client.setConfig(xmlrpcConfig);
        }
        catch (MalformedURLException exception)
        {
            throw new BlogIntegrationRuntimeException("Blog url '" + url + "' is invalid.", exception);
        }
        
        return client;
    }
    
    /**
     * Executes an XML RPC method
     * 
     * @param url
     * @param method
     * @param params
     * @return
     */
    private Object execute(String url, String method, List<Object> params)
    {
        Object result = null;
        try
        {
            // Make the remote call to the blog
            XmlRpcClient client = getClient(url);
            result = client.execute(method, params);
        }
        catch (XmlRpcException exception)
        {
            throw new BlogIntegrationRuntimeException("Failed to execute blog action '" + method + "' @ url '" + url + "'", exception);
        }
        return result;
    }
}
