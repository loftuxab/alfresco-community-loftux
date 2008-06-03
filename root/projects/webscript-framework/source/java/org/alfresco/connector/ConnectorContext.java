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
public class ConnectorContext
{
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    
    /** The parameters. */
    protected Map parameters;
    
    /** The headers. */
    protected Map headers;
    
    /** The content type. */
    protected String contentType;
    
    /** The method. */
    protected String method;
    
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
    public ConnectorContext(String method, Map parameters, Map headers)
    {
        this.method = method;
        this.parameters = new HashMap();
        this.headers = new HashMap();
        
        if(parameters != null)
        {
            this.parameters.putAll(parameters);
        }
        if(headers != null)
        {
            this.headers.putAll(headers);
        }
        
        if(this.method == null)
        {
            this.method = METHOD_GET;
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
    public String getMethod()
    {
        return this.method;
    }
    
    /**
     * Sets the method.
     * 
     * @param method the new method
     */
    public void setMethod(String method)
    {
        this.method = method;
    }
}
