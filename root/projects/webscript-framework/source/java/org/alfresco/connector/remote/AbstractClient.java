/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 * As a special exception to the terms and conditions of version 2.0 of the GPL,
 * you may redistribute this Program in connection with Free/Libre and Open
 * Source Software ("FLOSS") applications as described in Alfresco's FLOSS
 * exception. You should have recieved a copy of the text describing the FLOSS
 * exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.connector.remote;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author muzquiano
 */
public abstract class AbstractClient implements Client
{
    public AbstractClient(String endpoint)
    {
        this.endpoint = endpoint;

        // parse out some things
    }

    public String getEndpoint()
    {
        return endpoint;
    }

    public String getHost()
    {
        URL url = getURL();
        if (url != null)
            return url.getHost();
        return null;
    }

    public int getPort()
    {
        URL url = getURL();
        if (url != null)
            return url.getPort();
        return 80;
    }

    public String getProtocol()
    {
        URL url = getURL();
        if (url != null)
            return url.getProtocol();
        return null;
    }

    public URL getURL()
    {
        try
        {
            return new URL(endpoint);
        }
        catch (MalformedURLException me)
        {
        }
        return null;
    }

    protected String endpoint;
}
