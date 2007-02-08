/*
 * Copyright (C) 2005 Alfresco, Inc.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.alfresco.webservice.util;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.transport.http.HTTPConstants;

/**
 * @author Roy Wetherall
 */
public class CookieHandler extends BasicHandler 
{
    private static final long serialVersionUID = 5355053439499560511L;

    public void invoke(MessageContext context) 
        throws AxisFault 
    {
        String sessionId = AuthenticationUtils.getAuthenticationDetails().getSessionId();
        if (sessionId != null)
        {
            context.setProperty(HTTPConstants.HEADER_COOKIE, "JSESSIONID=" + sessionId);
        }
    }
 }
