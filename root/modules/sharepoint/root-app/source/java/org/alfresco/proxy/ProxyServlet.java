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
package org.alfresco.proxy;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
*
* @author Stas Sokolovsky
*
*/
public class ProxyServlet extends HttpServlet implements Runnable
{

    private static final long serialVersionUID = -1620920714447607434L;

    private static final String ALRESCO_CONTEXT_NAME = "alfresco-context-name";

    private static final String ALRESCO_APP_CONTEXT = "alfresco-appcontext-attribute";

    private static final String RECEIVER_NAME = "receiver-bean-name";
    
    private static final String CONTEXT_RECEIVER_NAME = "context-receiver-bean-name";

    private static final String TARGET_HTTP_METHOD = "target-http-method";
    
    private static final String TARGET_ALFRESCO_CONTEXT_METHOD = "target-alfresco-context-method";

    private static final int RECONNECT_TIME = 1000;

    private Method targetHttpMethod = null;

    private Object receiverBean = null;
    
    private String alfrescoContextName;
    
    private String alfrescoAppContextName;
    
    private String receiverBeanName;
    
    private String contextReceiverBeanName;
    
    private String httpMethodName;
    
    private String alfrescoContextMethodName;

    @SuppressWarnings("static-access")
    @Override
    public void init(ServletConfig config) throws ServletException
    {
        alfrescoContextName = config.getInitParameter(ALRESCO_CONTEXT_NAME);
        alfrescoAppContextName = config.getInitParameter(ALRESCO_APP_CONTEXT);
        receiverBeanName = config.getInitParameter(RECEIVER_NAME);
        contextReceiverBeanName = config.getInitParameter(CONTEXT_RECEIVER_NAME);
        httpMethodName = config.getInitParameter(TARGET_HTTP_METHOD);
        alfrescoContextMethodName = config.getInitParameter(TARGET_ALFRESCO_CONTEXT_METHOD);
        super.init(config);
        Thread initializationThread = new Thread(this);
        initializationThread.start();
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        if (targetHttpMethod != null)
        {
            try
            {
                targetHttpMethod.invoke(receiverBean, request, response);
            }
            catch (Exception e)
            {
                throw new ServletException(e);
            }
        }
    }

    @SuppressWarnings("static-access")
    private void findReceiver() throws ServletException {
        ServletContext servletContext = getServletConfig().getServletContext();
        Object alfrescoAppContext = null;
        Object contextReceiver = null;
        while (servletContext.getContext(alfrescoContextName) == null
                || (alfrescoAppContext = servletContext.getContext(alfrescoContextName).getAttribute(alfrescoAppContextName)) == null)
        {
            try
            {
                Thread.currentThread().sleep(RECONNECT_TIME);
            }
            catch (InterruptedException e)
            {
            }
        }

        while (receiverBean == null || contextReceiver == null)
        {
            try
            {
                Method method = alfrescoAppContext.getClass().getMethod("getBean", String.class);
                receiverBean = method.invoke(alfrescoAppContext, receiverBeanName);
            }
            catch (Exception e)
            {
            }
            
            try
            {
                Method method = alfrescoAppContext.getClass().getMethod("getBean", String.class);
                contextReceiver = method.invoke(alfrescoAppContext, contextReceiverBeanName);
            }
            catch (Exception e)
            {
            }
            
            if (receiverBean == null || contextReceiver == null) 
            {
                try
                {
                    Thread.currentThread().sleep(RECONNECT_TIME);
                }
                catch (InterruptedException e)
                {
                }
            }
        }
        
        try
        {
            Method setAlfrescoContext = contextReceiver.getClass().getMethod(alfrescoContextMethodName, String.class);
            setAlfrescoContext.invoke(contextReceiver, alfrescoContextName);
        }
        catch (Exception e)
        {
            throw new ServletException("Exception while retrieving context receiver bean", e);
        }
        try
        {
            Method setAlfrescoContext = receiverBean.getClass().getMethod(alfrescoContextMethodName, String.class);
            setAlfrescoContext.invoke(receiverBean, alfrescoContextName);
        }
        catch (Exception e)
        {
            throw new ServletException("Exception while retrieving context receiver bean", e);
        }
        try
        {
            targetHttpMethod = getMethodByName(receiverBean, httpMethodName);
        }
        catch (Exception e)
        {
            throw new ServletException("Exception while retrieving target http method", e);
        }

    }
    
    private Method getMethodByName(Object object, String methodname)
    {
        Method[] methods = object.getClass().getMethods();
        for (Method method : methods)
        {
            if (method.getName().equals(methodname))
            {
                return method;
            }
        }
        throw new RuntimeException("Method " + methodname + " is not found in target object");
    }

    public void run()
    {
        try
        {
            findReceiver();
        }
        catch (ServletException e)
        {
            throw new RuntimeException(e);
        }
    }

}