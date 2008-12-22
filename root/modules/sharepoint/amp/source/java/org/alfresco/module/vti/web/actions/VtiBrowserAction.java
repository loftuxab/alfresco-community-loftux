/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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
package org.alfresco.module.vti.web.actions;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.module.vti.handler.DwsServiceHandler;
import org.alfresco.module.vti.web.VtiAction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
* <p>VtiBrowserAction is used for redirection of specific requests to Web clients.
* It is used by browser while it is opening by client.</p>
*
* @author PavelYur
*
*/
public class VtiBrowserAction implements VtiAction
{

    private DwsServiceHandler handler;

    private static final long serialVersionUID = 5032228836777952601L;

    private static Log logger = LogFactory.getLog(VtiBrowserAction.class);

    /**
     * <p>VtiHandler setter.</p>
     *
     * @param handler {@link DwsServiceHandler}.    
     */
    public void setHandler(DwsServiceHandler handler)
    {
        this.handler = handler;
    }
    
    /**
     * <p>Handle redirection of specific requests to Web clients.</p> 
     *
     * @param request HTTP request
     * @param response HTTP response
     */
    public void execute(HttpServletRequest req, HttpServletResponse resp)
    {
        if (logger.isDebugEnabled())
            logger.debug("Handle request to browser '" + req.getRequestURI() + "'");
        try
        {
            handler.handleRedirect(req, resp);
        }
        catch (IOException e)
        {
            if (logger.isDebugEnabled()) {
                logger.debug("Action execution exception", e);
            }
        }
    }
}
