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

import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;
import org.alfresco.module.vti.method.VtiPropfindMethod;
import org.alfresco.repo.webdav.WebDAV;
import org.alfresco.repo.webdav.WebDAVServlet;

/**
 * @author PavelYur
 *
 */
public class VtiPropfindServlet extends WebDAVServlet
{
    
    private static final long serialVersionUID = 8916126506309290108L;    
    
    private VtiPathHelper pathHelper;       

    public void setPathHelper(VtiPathHelper pathHelper)
    {
        this.pathHelper = pathHelper;
    }

    /**
     * @param config
     */
    public void init(ServletConfig config) throws ServletException
    {        
        super.init(new ServletConfigImpl(config.getServletContext(), config.getServletName(), pathHelper));        
        m_davMethods.put(WebDAV.METHOD_PROPFIND, VtiPropfindMethod.class);        
    }    
}

class ServletConfigImpl implements ServletConfig
{
    
    private VtiPathHelper pathHelper;
    private ServletContext servletContext;
    private String name;
    
    public ServletConfigImpl(ServletContext servletContext, String name, VtiPathHelper pathHelper)    
    {
        this.pathHelper = pathHelper;
        this.servletContext = servletContext;
        this.name = name;
    }

    public String getInitParameter(String name)
    {
        if (name.equalsIgnoreCase(WebDAVServlet.KEY_STORE))
        {
            return pathHelper.getStorePath();
        }
        else if (name.equalsIgnoreCase(WebDAVServlet.KEY_ROOT_PATH))
        {
            return pathHelper.getRootPath();
        }
        else
        {
            return null;
        }
    }

    public Enumeration<String> getInitParameterNames()
    {        
        return new Enumeration<String>(){

            public boolean hasMoreElements()
            {
                return false;
            }

            public String nextElement()
            {             
                return null;
            }
            
        };
    }

    public ServletContext getServletContext()
    {
        return servletContext;
    }

    public String getServletName()
    {        
        return name;
    }
}
