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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.phpIntegration.lib;

import javax.servlet.ServletContext;

import org.alfresco.module.phpIntegration.PHPEngine;
import org.alfresco.service.ServiceRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.caucho.quercus.env.Env;

/**
 * @author Roy Wetherall
 */
public class Repository implements ScriptObject
{
    private static final String SCRIPT_OBJECT_NAME = "Repository";
    
    private ServiceRegistry serviceRegistry;
    
    public Repository(Env env)
    {
        if (env.getRequest() != null)
        {
            ServletContext servletContext = env.getRequest().getSession().getServletContext();
            ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
            this.serviceRegistry = (ServiceRegistry)applicationContext.getBean("ServiceRegistry");
        }
        else
        {
            this.serviceRegistry = (ServiceRegistry)env.getQuercus().getSpecial(PHPEngine.KEY_SERVICE_REGISTRY);
        }
    }
    
    public String getScriptObjectName()
    {
        return SCRIPT_OBJECT_NAME;
    }
    
    public Session createSession()
    {
        return new Session(this.serviceRegistry);
    }
    
    // public String authenticate();
    
    // publis Session createSession(String ticket);
}
