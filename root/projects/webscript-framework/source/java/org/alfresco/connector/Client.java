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
package org.alfresco.connector;

import java.net.URL;

/**
 * Interface for Client implementations
 * 
 * This interface is pretty lightweight at the moment.  The Client
 * implementation requirements are deliberately so since we're still
 * feeling out what kind of custom Clients might be written in the future.
 * 
 * At present, it seems that most things are solved by the root RemoteClient
 * object.
 * 
 * This interface is therefore subject to change in the future.
 * 
 * Developers seeking to write their own Clients are encouraged to extend
 * the AbstractClient base class.
 * 
 * @author muzquiano
 */
public interface Client
{
    public String getEndpoint();
    public URL getURL();
}
