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

import java.util.Iterator;
import java.util.Map;

import org.alfresco.connector.exception.AuthenticationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract implementation of an Authenticator which can be used quite
 * readily as a base class for your own custom implementations.
 * 
 * This abstract implementation provides helper methods for post-processing
 * response elements such as headers.  
 * 
 * The sole authenticate method remains unimplemented.
 * 
 * @author muzquiano
 */
public abstract class AbstractAuthenticator implements Authenticator
{
    private static Log logger = LogFactory.getLog(Authenticator.class);
    
    public abstract ConnectorSession authenticate(String endpoint, Credentials credentials, ConnectorSession connectorSession)
            throws AuthenticationException;
    
    public abstract boolean isAuthenticated(String endpoint, ConnectorSession connectorSession);
    
    /**
     * Retrieves headers from response and stores onto the Connector Session
     * 
     * @param response
     */
    protected void processResponse(Response response, ConnectorSession connectorSession)
    {
        // look for "Set" cookies and store back onto credential vault
        
        Map<String, String> headers = response.getStatus().getHeaders();
        Iterator it = headers.keySet().iterator();
        while(it.hasNext())
        {
            String headerName = (String) it.next();
            if(headerName.toLowerCase().equals("set-cookie"))
            {
                String headerValue = (String) headers.get(headerName);
                
                int z = headerValue.indexOf("=");
                if(z > -1)
                {
                    String cookieName = (String) headerValue.substring(0,z);
                    String cookieValue = (String) headerValue.substring(z+1, headerValue.length());
                    int y = cookieValue.indexOf(";");
                    if(y > -1)
                    {
                        cookieValue = cookieValue.substring(0,y);
                    }                    

                    if(logger.isDebugEnabled())
                        logger.debug("Authenticator found set-cookie: " + cookieName + " = " + cookieValue);

                    // store cookie back                    
                    if(connectorSession != null)
                    {
                        connectorSession.setCookie(cookieName, cookieValue);
                    }
                }
            }
        }
    }    
}
