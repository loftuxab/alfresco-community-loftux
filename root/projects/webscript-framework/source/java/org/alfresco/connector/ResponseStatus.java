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
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.connector;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.web.scripts.Status;

/**
 * Wrapper around the Status object that allows the Remote Client to
 * expose header state.
 * 
 * Records the outcome of a call
 * 
 * @author muzquiano
 */
public class ResponseStatus extends Status
{
    protected Map<String, String> headers = new HashMap<String, String>(16, 1.0f);
    
    @Override
    public String toString()
    {
        return Integer.toString(getCode());
    }
    
    /**
     * Allows for response headers to be stored onto the status
     * 
     * @param headerName name of the header
     * @param headerValue value of the header
     */
    public void setHeader(String headerName, String headerValue)
    {
        this.headers.put(headerName, headerValue);
    }
    
    /**
     * Retrieves response headers
     * 
     * @return map of response headers
     */
    public Map<String, String> getHeaders()
    {
        return this.headers;
    }
}
