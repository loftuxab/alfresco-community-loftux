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
package org.alfresco.connector;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Describes invocation context that the connector should consider
 * when creating the connection to the remote service.
 * 
 * Invocation context consists of HTTP request state such as
 * fixed parameters and headers.
 * 
 * @author Uzquiano
 */
public final class ConnectorContext
{
    /** The parameters. */
    private Map<String, String> parameters = Collections.<String, String>emptyMap();
    
    /** The headers. */
    private Map<String, String> headers = Collections.<String, String>emptyMap();
    
    /** The content type. */
    private String contentType;
    
    /** The method. */
    private HttpMethod method = HttpMethod.GET;
    
    
    /**
     * Instantiates a new connector context.
     */
    public ConnectorContext()
    {
        this(null, null, null);
    }
    
    /**
     * Instantiates a new connector context.
     * 
     * @param parameters the parameters
     * @param headers the headers
     */
    public ConnectorContext(Map parameters, Map headers)
    {
        this(null, parameters, headers);
    }

    /**
     * Instantiates a new connector context.
     * 
     * @param method the method
     * @param parameters the parameters
     * @param headers the headers
     */
    public ConnectorContext(HttpMethod method, Map parameters, Map headers)
    {
        if (method != null)
        {
            this.method = method;
        }
        if (parameters != null)
        {
            this.parameters = new HashMap<String, String>(parameters.size());
            this.parameters.putAll(parameters);
        }
        if (headers != null)
        {
            this.headers = new HashMap<String, String>(headers.size());
            this.headers.putAll(headers);
        }
    }
    
    /**
     * Gets the parameters.
     * 
     * @return the parameters
     */
    public Map getParameters()
    {
        return this.parameters;
    }
    
    /**
     * Gets the headers.
     * 
     * @return the headers
     */
    public Map getHeaders()
    {
        return this.headers;
    }
    
    /**
     * Gets the content type.
     * 
     * @return the content type
     */
    public String getContentType()
    {
        return this.contentType;
    }
    
    /**
     * Sets the content type.
     * 
     * @param contentType the new content type
     */
    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }
    
    /**
     * Gets the method.
     * 
     * @return the method
     */
    public HttpMethod getMethod()
    {
        return this.method;
    }
    
    /**
     * Sets the method.
     * 
     * @param method the new method
     */
    public void setMethod(HttpMethod method)
    {
        if (method != null)
        {
            this.method = method;
        }
    }
}
