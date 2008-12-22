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
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.module.vti.web.VtiAction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
* <p>VtiInfAction returns the information to determine the entry point for
* the Microsoft FrontPage Server Extensions.</p>
*
* @author Michael Shavnev
*/
public class VtiInfAction implements VtiAction
{
    private static final long serialVersionUID = 429709350002602411L;

    private final static Log logger = LogFactory.getLog(VtiBaseAction.class);

    /**
     * <p>Return the information to determine the entry point for 
     * the Microsoft FrontPage Server Extensions.</p> 
     *
     * @param request HTTP request
     * @param response HTTP response
     */
    public void execute(HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            OutputStream outputStream = response.getOutputStream();
            outputStream.write("<!-- FrontPage Configuration Information\n".getBytes());
            outputStream.write(" FPVersion=\"6.0.2.9999\"\n".getBytes());
            outputStream.write("FPShtmlScriptUrl=\"_vti_bin/shtml.dll/_vti_rpc\"\n".getBytes());
            outputStream.write("FPAuthorScriptUrl=\"_vti_bin/_vti_aut/author.dll\"\n".getBytes());
            outputStream.write("FPAdminScriptUrl=\"_vti_bin/_vti_adm/admin.dll\"\n".getBytes());
            outputStream.write("TPScriptUrl=\"_vti_bin/owssvr.dll\"\n".getBytes());
            outputStream.write("-->".getBytes());
            outputStream.close();
        }
        catch (IOException e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Action IO exception", e);
            }
        }
    }

}