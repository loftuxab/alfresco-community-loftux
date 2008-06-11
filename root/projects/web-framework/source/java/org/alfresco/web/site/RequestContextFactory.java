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
package org.alfresco.web.site;

import javax.servlet.ServletRequest;

import org.alfresco.web.site.exception.RequestContextException;

/**
 * Interface for a RequestContext factory.
 * <p>
 * A request context factory is invoked by the framework at the start of the
 * request chain.  It is responsible for producing a RequestContext object
 * which is bound to the request.  The RequestContext object is a single
 * object instance with which all downstream framework elements can consult.
 * <p>
 * The RequestContext object is scoped to the request.
 * 
 * @author muzquiano
 */
public interface RequestContextFactory
{
    /**
     * Produces a new RequestContext instance for a given request. Always returns
     * a new RequestContext instance - or an exception is thrown.
     * 
     * @return The RequestContext instance
     * @throws RequestContextException
     */
    public RequestContext newInstance(ServletRequest request) throws RequestContextException;
}
