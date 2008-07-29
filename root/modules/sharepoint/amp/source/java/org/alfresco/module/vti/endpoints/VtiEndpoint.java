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

package org.alfresco.module.vti.endpoints;

import org.alfresco.module.vti.httpconnector.VtiSessionManager;
import org.springframework.ws.server.endpoint.AbstractDom4jPayloadEndpoint;

/**
 * Abstract class for all vti web services endpoints
 *
 * @author Dmitry Lazurkin
 *
 */
public abstract class VtiEndpoint extends AbstractDom4jPayloadEndpoint
{

    public static final String DWS = "VTI_DWS";
    protected String name;
    protected String namespace;
    
    protected VtiSessionManager sessionManager;

    public VtiSessionManager getSessionManager()
    {
        return sessionManager;
    }

    public void setSessionManager(VtiSessionManager sessionManager)
    {
        this.sessionManager = sessionManager;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setNamespace(String namespace)
    {
        this.namespace = namespace;
    }

    public String getName()
    {
        return name;
    }

    public String getNamespace()
    {
        return namespace;
    }

}
