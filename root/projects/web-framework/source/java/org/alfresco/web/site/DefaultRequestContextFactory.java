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
package org.alfresco.web.site;

import java.io.File;
import java.io.Serializable;
import java.util.Enumeration;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.alfresco.connector.User;
import org.alfresco.web.framework.ModelObjectManager;
import org.alfresco.web.framework.ModelPersistenceContext;
import org.alfresco.web.framework.exception.WebFrameworkServiceException;
import org.alfresco.web.site.exception.PageMapperException;
import org.alfresco.web.site.exception.RequestContextException;
import org.alfresco.web.site.exception.UserFactoryException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Produces HttpRequestContext instances for HttpServletRequest request
 * inputs.  The HttpRequestContext type has an additional convenience
 * accessor method to return the HttpServletRequest.
 *  
 * @author muzquiano
 */
public class DefaultRequestContextFactory implements RequestContextFactory
{
    private static Log logger = LogFactory.getLog(DefaultRequestContextFactory.class);
    
    /**
     * Produces a new RequestContext instance for a given request. Always returns
     * a RequestContext instance - or an exception is thrown.
     * 
     * @return The RequestContext instance
     * 
     * @throws RequestContextException
     */    
    public RequestContext newInstance(ServletRequest request)
        throws RequestContextException
    {
        if (!(request instanceof HttpServletRequest))
        {
            throw new RequestContextException("DefaultRequestContextFactory can only produce RequestContext instances for an HttpServletRequest request.");
        }

        RequestContext context;
        
        // Load the user and place the user onto the RequestContext
        try
        {
            // Construct the HttpRequestContext instance
            context = new DefaultRequestContext((HttpServletRequest)request);
            
            // Copy in request parameters
            Enumeration parameterNames = ((HttpServletRequest)request).getParameterNames();
            while (parameterNames.hasMoreElements())
            {
                String parameterName = (String) parameterNames.nextElement();
                Object parameterValue = ((HttpServletRequest)request).getParameter(parameterName);
                context.setValue(parameterName, (Serializable)parameterValue);
            }
            
            // Construct/load the user and place them onto the request context if required
            if (request.getAttribute(SILENT_INIT) == null)
            {
                String userEndpointId = (String)request.getAttribute(USER_ENDPOINT);
                UserFactory userFactory = FrameworkHelper.getUserFactory();
                User user = userFactory.faultUser(context, (HttpServletRequest)request, userEndpointId);
                context.setUser(user);
            }
            
            initEnvironment(context, (HttpServletRequest) request);
            
            // Bind the model into the requestcontext
            initModel(context, (HttpServletRequest) request);
            
            /**
             * Execute the configured page mapper
             * 
             * This will populate request context with information about
             * how to render, based on the incoming URL
             */
            PageMapper pageMapper = PageMapperFactory.newInstance(context);
            pageMapper.execute(context, (HttpServletRequest)request);
        }
        catch (WebFrameworkServiceException wfse)
        {
            throw new RequestContextException("Exception instantiating model in HttpRequestContextFactory", wfse);            
        }
        catch (UserFactoryException ufe)
        {
            throw new RequestContextException("Exception running UserFactory in HttpRequestContextFactory", ufe);
        }
        catch (PageMapperException pme)
        {
            throw new RequestContextException("Exception running PageMapper in HttpRequestContextFactory", pme);
        }
        
        return context;
    }

    /**
     * Initializes the environment for the request context.
     * This includes bindings for store and webapp, if applicable.
     * 
     * @param context
     * @param request
     * @throws WebFrameworkServiceException
     */
    public void initEnvironment(RequestContext context, HttpServletRequest request)
        throws WebFrameworkServiceException
    {
        // Was a store id set by request parameter?
        String repositoryStoreId = request.getParameter(WebFrameworkConstants.STORE_ID_REQUEST_PARAM_NAME);
        if (repositoryStoreId == null)
        {
            // if we didn't get an explicitly fed store id, we can attempt
            // to infer one from the virtualization server name
            String serverName = request.getServerName();
            if (serverName != null && serverName.indexOf("--") > -1)
            {
                // could be a virtual server host name...
                String realPath = request.getSession().getServletContext().getRealPath("/");
                if (realPath != null && realPath.indexOf("--") > -1)
                {
                    // let's assume it is a virtual store id since it mapped
                    // down to a likely real path (mounted disk)
                    int x1 = realPath.indexOf(File.separator);
                    if (x1 > -1)
                    {
                        int x2 = realPath.indexOf(File.separator, x1+1);
                        if (x2 > -1)
                        {
                            repositoryStoreId = (String) realPath.substring(x1, x2);
                        }
                    }
                }
            }
        }
        if (repositoryStoreId == null)
        {
            // Otherwise, check to see if we have one in session
            if (request.getSession(false) != null)
            {
                repositoryStoreId = (String)request.getSession().getAttribute(WebFrameworkConstants.STORE_ID_SESSION_ATTRIBUTE_NAME);
            }
        }
        if (repositoryStoreId != null)
        {
            context.setValue(WebFrameworkConstants.STORE_ID_REQUEST_CONTEXT_NAME, repositoryStoreId);
        }
        
        // Was a web application id set by request parameter?
        String repositoryWebappId = request.getParameter(WebFrameworkConstants.WEBAPP_ID_REQUEST_PARAM_NAME);
        if (repositoryWebappId == null)
        {
            // Otherwise, check to see if we have one in session
            if (request.getSession(false) != null)
            {
                repositoryWebappId = (String)request.getSession().getAttribute(WebFrameworkConstants.WEBAPP_ID_SESSION_ATTRIBUTE_NAME);
            }            
        }
        if (repositoryWebappId != null)
        {
            context.setValue(WebFrameworkConstants.WEBAPP_ID_REQUEST_CONTEXT_NAME, repositoryWebappId);
        }
    }
    
    /**
     * Initializes the model and places it onto the request context
     * 
     * @param context
     * @param request
     */
    public void initModel(RequestContext context, HttpServletRequest request)
        throws WebFrameworkServiceException
    {
        String userId = context.getUserId();
        
        ModelPersistenceContext mpc = new ModelPersistenceContext(userId);
        
        // Bind to a Store ID
        String storeId = (String)context.getValue(WebFrameworkConstants.STORE_ID_REQUEST_CONTEXT_NAME);
        if (storeId != null)
        {
            if (logger.isDebugEnabled())
                logger.debug("RequestContext [" + context.getId() + "] using store: " + storeId);
            
            mpc.putValue(ModelPersistenceContext.REPO_STOREID, storeId);
        }
        
        // Bind to a Webapp ID
        String webappId = (String)context.getValue(WebFrameworkConstants.WEBAPP_ID_REQUEST_CONTEXT_NAME);
        if (webappId != null)
        {
            if (logger.isDebugEnabled())
                logger.debug("RequestContext [" + context.getId() + "] using webapp: " + webappId);
            
            mpc.putValue(ModelPersistenceContext.REPO_WEBAPPID, webappId);
        }
        
        // retrieve the model object service which is scoped to this user
        // and this persistence context
        ModelObjectManager modelObjectService = FrameworkHelper.getWebFrameworkManager().getObjectManager(mpc);
        
        // create a new model
        // this model reflects the state of the objects as per the persistence context
        Model model = new Model(modelObjectService);
        
        // place onto request context
        context.setModel(model);
    }
}
