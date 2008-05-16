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

import java.net.MalformedURLException;
import java.net.URL;

import org.alfresco.error.AlfrescoRuntimeException;

/**
 * Abstract base class for use by developers who wish to provide
 * additional custom client implementations.
 * 
 * A general purpose but very useful RemoteClient implementation
 * is provided that should handle most HTTP related matters.
 * 
 * Client objects manage state between the web script layer and the
 * remote endpoint.  They are "dumb" objects in the sense that they
 * need to be set up and then fired off.
 * 
 * Connector objects tell the Client objects what to do and when.
 * They orchestrate the sequence of handshakes and so forth so that
 * the end user or web script developer doesn't need to worry about the
 * underlying mechanics of speaking to the endpoint.
 * 
 * @author muzquiano
 */
public abstract class AbstractClient implements Client
{
    /**
     * Instantiates a new abstract client.
     * 
     * @param endpoint the endpoint
     */
    public AbstractClient(String endpoint)
    {
        this.endpoint = endpoint;
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.Client#getEndpoint()
     */
    public String getEndpoint()
    {
        return this.endpoint;
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.Client#getURL()
     */
    public URL getURL()
    {
        try
        {
            return new URL(this.endpoint);
        }
        catch (MalformedURLException me)
        {
            throw new AlfrescoRuntimeException("Unable to parse endpoint as URL: " + this.endpoint);
        }
    }
    
    protected String endpoint;    
}
