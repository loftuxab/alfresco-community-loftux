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
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.framework.resource;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;


/**
 * Defines a Web Framework model object resource
 * 
 * @author muzquiano
 */
public interface Resource extends Serializable
{
    public static final String ATTR_ENDPOINT = "endpoint";
    public static final String ATTR_ID = "id";
    public static final String ATTR_TYPE = "type";
    
    /**
     * Returns the id of the resource
     * 
     * @return the id
     */
    public String getId();
    
    /**
     * Returns the type of the resource
     * 
     * @return the type
     */
    public String getType();
    
    /**
     * Sets the type of the resource
     * 
     * @param type
     */
    public void setType(String type);
    
    /**
     * Returns the endpoint of the resource
     * 
     * @return the endpoint
     */
    public String getEndpoint();
    
    /**
     * Sets the endpoint of the resource
     * 
     * @param endpoint
     */
    public void setEndpoint(String endpoint);
    
    /**
     * Returns the names of attributes
     * 
     * @return
     */
    public String[] getAttributeNames();
    
    /**
     * Returns the attribute value for the given attribute name
     * 
     * @param name
     * 
     * @return value
     */
    public String getAttribute(String name);
    
    /**
     * Sets an attribute
     * 
     * @param name
     * @param value
     */
    public void setAttribute(String name, String value);
    
    /**
     * Removes an attribute
     * 
     * @param name
     */
    public void removeAttribute(String name);
    
    /**
     * Gets the resource value
     * 
     * @return the value
     */
    public String getValue();
    
    /**
     * Sets the resource value
     */
    public void setValue(String value);
    
    /**
     * Provides the URI required to retrieve the content stream
     *  
     * @return
     */
    public String getDownloadURI(HttpServletRequest request);

    /**
     * Provides the Proxied URI required to retrieve the content stream
     *  
     * @return
     */
    public String getProxiedDownloadURI(HttpServletRequest request);
    
    /**
     * Provides the URI required to access the metadata of the resource
     * 
     * @return
     */
    public String getMetadataURI(HttpServletRequest request);

    /**
     * Provides the URI required to access the metadata of the resource
     * 
     * @return
     */
    public String getProxiedMetadataURI(HttpServletRequest request);
    
    /**
     * Fetches the raw metadata from the remote storage location
     * 
     * @param request
     * 
     * @return
     */
    public String getRawMetadata(HttpServletRequest request);

    /**
     * Fetches the common-container formatted metadata from 
     * the remote storage location
     * 
     * @param request
     * 
     * @return
     */
    public String getMetadata(HttpServletRequest request);    
}
