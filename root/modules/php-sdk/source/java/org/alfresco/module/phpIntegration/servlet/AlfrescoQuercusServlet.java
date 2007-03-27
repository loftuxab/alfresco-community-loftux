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
 * http://www.alfresco.com/legal/licensing
 */
package org.alfresco.module.phpIntegration.servlet;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.module.phpIntegration.lib.Node;
import org.alfresco.module.phpIntegration.lib.Session;
import org.alfresco.module.phpIntegration.lib.SpacesStore;
import org.alfresco.module.phpIntegration.lib.Store;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.transaction.TransactionUtil;
import org.alfresco.repo.transaction.TransactionUtil.TransactionWork;
import org.alfresco.service.transaction.TransactionService;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.caucho.quercus.servlet.PhpClassConfig;
import com.caucho.quercus.servlet.QuercusServlet;

/**
 * @author royw
 *
 */
public class AlfrescoQuercusServlet extends QuercusServlet
{
    private static final long serialVersionUID = 3074706465787671284L;

    public AlfrescoQuercusServlet()
    {
        super();
        
        // Add the Alfresco modules and classes
        registerClass("Session", Session.class);
        registerClass("Node", Node.class);
        registerClass("Store", Store.class);
        registerClass("SpacesStore", SpacesStore.class);
    }
    
    public void service(final HttpServletRequest request, final HttpServletResponse response)
        throws ServletException, IOException
    {
        ServletContext servletContext = request.getSession().getServletContext();
        ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        
        final AuthenticationComponent authenticationComponenet = (AuthenticationComponent)applicationContext.getBean("authenticationComponent");
        TransactionService transactionService = (TransactionService)applicationContext.getBean("transactionComponent");
        
        TransactionUtil.executeInUserTransaction(transactionService, new TransactionWork<Object>()
        {
            public Object doWork() throws Throwable
            {
                authenticationComponenet.setCurrentUser("admin");
                try
                {
                    AlfrescoQuercusServlet.super.service(request, response);
                }
                finally
                {
                    authenticationComponenet.clearCurrentSecurityContext();
                }
                
                return null;
            }
        });
    }
    
    private void registerClass(String name, Class clazz)
    {
        PhpClassConfig config = new PhpClassConfig();
        config.setName(name);
        config.setType(clazz);
        addClass(config);
    }
}
