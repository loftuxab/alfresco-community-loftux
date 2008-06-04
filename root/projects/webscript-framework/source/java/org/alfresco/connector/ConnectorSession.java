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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Container for Connector "session state".  Session state consists
 * of headers, cookies and parameters that need to be bound onto
 * the connector with subsequent connections.
 * 
 * This class essentially allows for the mimic of Browser-like
 * functionality in terms of subsequent Connectors reusing state
 * from previous Connector responses.
 * 
 * @author muzquiano
 */
public class ConnectorSession implements Serializable
{
    private Map<String, String> parameters = null;
    private Map<String, String> cookies = null;
    private String endpointId;
    
    
    /**
     * Instantiates a new connector session.
     * 
     * @param endpointId the endpoint id
     */
    public ConnectorSession(String endpointId)
    {
        this.endpointId = endpointId;
        this.parameters = new HashMap<String, String>(16, 1.0f);
        this.cookies = new HashMap<String, String>(16, 1.0f);
    }
    
    /**
     * Gets the endpoint id.
     * 
     * @return the endpoint id
     */
    public String getEndpointId()
    {
        return this.endpointId;
    }

    /**
     * Gets a parameter.
     * 
     * @param key the key
     * 
     * @return the parameter
     */
    public String getParameter(String key)
    {
        return this.parameters.get(key);
    }

    /**
     * Sets a given parameter.
     * 
     * @param key the key
     * @param value the value
     */
    public void setParameter(String key, String value)
    {
        this.parameters.put(key, value);
    }
    
    /**
     * Returns the parameter keys.
     * 
     * @return array of parameter keys
     */
    public String[] getParameterKeys()
    {
        return this.parameters.keySet().toArray(new String[this.parameters.size()]);
    }    

    /**
     * Gets a header.
     * 
     * @param name the name
     * 
     * @return the header
     */
    public String getCookie(String name)
    {
        return (String) this.cookies.get(name);
    }

    /**
     * Sets a given header.
     * 
     * @param name the name
     * @param value the header
     */
    public void setCookie(String name, String value)
    {
        this.cookies.put(name, value);
    }

    /**
     * Returns the cookie names.
     * 
     * @return array of cookie names
     */
    public String[] getCookieNames()
    {
        return this.cookies.keySet().toArray(new String[this.cookies.size()]);
    }    
}
