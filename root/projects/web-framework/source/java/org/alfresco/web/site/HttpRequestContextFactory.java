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
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.alfresco.connector.User;
import org.alfresco.web.framework.ModelObjectManager;
import org.alfresco.web.framework.ModelPersistenceContext;
import org.alfresco.web.framework.exception.WebFrameworkServiceException;
import org.alfresco.web.site.exception.PageMapperException;
import org.alfresco.web.site.exception.RequestContextException;
import org.alfresco.web.site.exception.UserFactoryException;
import org.alfresco.web.site.filesystem.FileSystemManager;
import org.alfresco.web.site.filesystem.IFileSystem;

/**
 * Produces HttpRequestContext instances for HttpServletRequest request
 * inputs.  The HttpRequestContext type has an additional convenience
 * accessor method to return the HttpServletRequest.
 *  
 * @author muzquiano
 */
public class HttpRequestContextFactory implements RequestContextFactory
{
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
            throw new RequestContextException("HttpRequestContextFactory can only produce HttpRequestContext instances for HttpServletRequest requests");
        }

        HttpRequestContext context;
        
        // Load the user and place the user onto the RequestContext
        try
        {
            // Construct the HttpRequestContext instance
            context = new HttpRequestContext((HttpServletRequest)request);
            
            // Copy in request parameters
            Enumeration parameterNames = ((HttpServletRequest)request).getParameterNames();
            while(parameterNames.hasMoreElements())
            {
                String parameterName = (String) parameterNames.nextElement();
                Object parameterValue = ((HttpServletRequest)request).getParameter(parameterName);
                context.setValue(parameterName, parameterValue);
            }
            
            // Construct/load the user and place them onto the request context
            UserFactory userFactory = FrameworkHelper.getUserFactory();
            User user = userFactory.faultUser(context, (HttpServletRequest)request);
            context.setUser(user);
            
            // Bind the model into the requestcontext
            initModel(context, (HttpServletRequest) request);
            
            // Initialize the file system
            String rootPath = context.getConfig().getFileSystemDescriptor("local").getRootPath();
            initFileSystem(context, (HttpServletRequest)request, rootPath);
            
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
     * Creates a FileSystem that points to the local web application root.
     * 
     * @param context
     * @param request
     * @param rootPath
     */
    public void initFileSystem(RequestContext context,
            HttpServletRequest request, String rootPath)
    {
        ServletContext servletContext = request.getSession().getServletContext();
        String realPath = servletContext.getRealPath(rootPath);

        // the file system manager takes care to make sure that if this
        // file system has already been loaded, it will be reused
        File dir = new File(realPath);
        IFileSystem fileSystem = FileSystemManager.getLocalFileSystem(dir);
        context.setFileSystem(fileSystem);
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
        
        // TODO: see if we can determine a store id that is being virtualized
        /*String repositoryStoreId = null;
        if (repositoryStoreId == null)
        {
            repositoryStoreId = (String) request.getParameter("storeId");
        }
        if (repositoryStoreId != null)
        {
            mpc.putValue(ModelPersistenceContext.REPO_STOREID, repositoryStoreId);
        }*/
        
        // retrieve the model object service which is scoped to this user
        // and this persistence context
        ModelObjectManager modelObjectService = FrameworkHelper.getWebFrameworkService().getObjectManager(mpc);
        
        // create a new model
        // this model reflects the state of the objects as per the persistence context
        Model model = new Model(modelObjectService);
        
        // place onto request context
        context.setModel(model);
    }
}
