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

import org.alfresco.web.config.RemoteConfigElement.ConnectorDescriptor;

/**
 * Connector object that is used to connect to Media Wiki.
 * 
 * This just extends the HttpConnector and is available in case we
 * choose to make extensions in the future.
 * 
 * The fact is that the MediaWikiAuthenticator does all of the magic
 * in terms of logging in and handling cookies.  The MediaWikiAuthenticator
 * is orchestrated through the AuthenticatingConnector class.
 * 
 * @author muzquiano
 */
public class MediaWikiConnector extends HttpConnector
{
    /**
     * Instantiates a new media wiki connector.
     * 
     * @param descriptor the descriptor
     * @param endpoint the endpoint
     */
    public MediaWikiConnector(ConnectorDescriptor descriptor, String endpoint)
    {
        super(descriptor, endpoint);
    }
}
