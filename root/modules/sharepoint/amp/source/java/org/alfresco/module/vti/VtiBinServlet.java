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

package org.alfresco.module.vti;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.alfresco.module.vti.method.NotImplementedVtiMethod;
import org.alfresco.module.vti.method.VtiMethod;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 *
 * @author Michael Shavnev
 *
 */
public class VtiBinServlet extends VtiServlet implements javax.servlet.Servlet {

    private static final long serialVersionUID = -4566432341846075170L;

    private static final String METHOD_DELIMETR = ":";
    
    private Map<String, VtiMethod> nameToVtiMethod = new HashMap<String, VtiMethod>();  
    
    /** Logger */
    private static Log logger = LogFactory.getLog(VtiBinServlet.class);  

    @Override
    public void init(ServletConfig config) throws ServletException
    {        
    }

    protected void doGet(VtiRequest request, VtiResponse response) throws ServletException, IOException
    {
        String vtiMethodName = request.getParameter("dialogview");
        if (vtiMethodName != null)
        {
            if (logger.isDebugEnabled())
                logger.debug("Request to VTI method '" + vtiMethodName + "'");
            
            String[] dialogViewName = request.getParameterValues("dialogview");
            if (dialogViewName != null && dialogViewName.length > 0)
            {
                if (logger.isDebugEnabled())
                    logger.debug("Process '" + dialogViewName[0] + "'");

                processVtiMethod(dialogViewName[0] + "_dialog", request, response);
            }
        }
        else
        {
            vtiMethodName = request.getParameter("Cmd");
            if (vtiMethodName == null)
            {
                if (logger.isDebugEnabled()) 
                {
                    logger.debug("Request to VTI method '" + vtiMethodName + "'");
                    logger.debug("Process '" + vtiMethodName + "'");
                }
                
                vtiMethodName = vtiMethodName + "_command";
                processVtiMethod(vtiMethodName, request, response);
            }
        }
    }


    protected void doPost(VtiRequest request, VtiResponse response) throws ServletException, IOException
    {
        String vtiMethodAndVersion = request.getParameter("method");
        if (vtiMethodAndVersion != null)
        {
            String vtiMethodName = vtiMethodAndVersion.split(METHOD_DELIMETR)[0].replaceAll(" ", "_") + "_method";
            processVtiMethod(vtiMethodName, request, response);
        }        
    }

    private void processVtiMethod(String vtiMethodName, VtiRequest request, VtiResponse response) throws IOException
    {
        VtiMethod vtiMethod = nameToVtiMethod.get(vtiMethodName);
        if (vtiMethod == null)
        {
            vtiMethod = new NotImplementedVtiMethod(vtiMethodName);
        }
        
        if (logger.isDebugEnabled())
            logger.debug("Executing vtiMethod: " + vtiMethod);
        
        vtiMethod.execute(request, response);
    }

    public Map<String, VtiMethod> getNameToVtiMethod()
    {
        return nameToVtiMethod;
    }

    public void setNameToVtiMethod(Map<String, VtiMethod> nameToVtiMethod)
    {
        this.nameToVtiMethod = nameToVtiMethod;
    }

}