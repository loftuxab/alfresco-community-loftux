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

import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.web.config.RemoteConfigElement.ConnectorDescriptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Connector object that is used to connect to Web Helpdesk
 * 
 * This just extends the HttpConnector and is available in case we
 * choose to make extensions in the future.
 * 
 * @author muzquiano
 */
public class WebHelpdeskConnector extends HttpConnector
{
    private static Log logger = LogFactory.getLog(WebHelpdeskConnector.class);
    
    /**
     * Instantiates a new web helpdesk connector.
     * 
     * @param descriptor the descriptor
     * @param endpoint the endpoint
     */
    public WebHelpdeskConnector(ConnectorDescriptor descriptor, String endpoint)
    {
        super(descriptor, endpoint);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.HttpConnector#call(java.lang.String, org.alfresco.connector.ConnectorContext)
     */
    public Response call(String uri, ConnectorContext context)
    {
        // if Web Helpdesk has determined another URL, we will use that
        if(getConnectorSession() != null)
        {
            if("/".equals(uri))
            {
                uri = getConnectorSession().getParameter(WebHelpdeskAuthenticator.CS_PARAM_FORM_ACTION_HANDLER);
                
                if(logger.isDebugEnabled())
                    logger.debug("Rewrote '/' uri to '" + uri + "'");
            }            
        }
        
        return super.call(uri, context);
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.HttpConnector#call(java.lang.String, org.alfresco.connector.ConnectorContext, java.io.InputStream, java.io.OutputStream)
     */
    public Response call(String uri, ConnectorContext context, InputStream in, OutputStream out)
    {
        // if Web Helpdesk has determined another URL, we will use that
        if(getConnectorSession() != null)
        {
            if("/".equals(uri))
            {
                if("/".equals(uri))
                {
                    uri = getConnectorSession().getParameter(WebHelpdeskAuthenticator.CS_PARAM_FORM_ACTION_HANDLER);

                    if(logger.isDebugEnabled())
                        logger.debug("Rewrote '/' uri to '" + uri + "'");
                }
            }
        }
        
        return super.call(uri, context, in, out);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.HttpConnector#call(java.lang.String, org.alfresco.connector.ConnectorContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public Response call(String uri, ConnectorContext context, HttpServletRequest req, HttpServletResponse res)
    {
        // if Web Helpdesk has determined another URL, we will use that
        if(getConnectorSession() != null)
        {
            if("/".equals(uri))
            {
                uri = getConnectorSession().getParameter(WebHelpdeskAuthenticator.CS_PARAM_FORM_ACTION_HANDLER);
                
                if(logger.isDebugEnabled())
                    logger.debug("Rewrote '/' uri to '" + uri + "'");
            }
        }

        return super.call(uri, context, req, res);
    }
}
