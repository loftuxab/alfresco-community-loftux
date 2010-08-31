/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.wcm.client.exceptionresolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.AssetFactory;
import org.alfresco.wcm.client.WebSite;
import org.alfresco.wcm.client.exception.PageNotFoundException;
import org.alfresco.wcm.client.interceptor.ModelDecorator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.mvc.PageView;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

/**
 * This class attempts to find an error page for a http status code within the 
 * repository when an exception occurs. If not found it reverts to the behaviour of the 
 * SimpleMappingExceptionResolver and so uses a default catch-all error page.
 * @author Chris Lack
 */
public class RepositoryExceptionResolver extends SimpleMappingExceptionResolver
{
    private static Log log = LogFactory.getLog(RepositoryExceptionResolver.class);

    private AssetFactory assetFactory;	
	private String errorPageSuffix;
	private ModelDecorator modelDecorator;
	
	@Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
    {
	    // Log the exception
	    log.error(ex, ex);
	    
		// Determine the http status code from the exception
		Integer statusCode;
		if (ex instanceof PageNotFoundException) 
		{
			statusCode = HttpStatus.NOT_FOUND.value();
		}
		else 
		{
			statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
		}
		
		// Get the current website from the request
		RequestContext requestContext = ThreadLocalRequestContext.getRequestContext();		
		if (requestContext != null)
		{
			WebSite webSite = (WebSite)requestContext.getValue("webSite");			
			if (webSite != null)
			{
				// Determine the error page asset name and fetch it from the repository
				String errorPage = statusCode+errorPageSuffix+".html";		
		        Asset errorAsset = assetFactory.getSectionAsset(webSite.getRootSection().getId(), errorPage, true);
		        
		        // If there is an editorially configured error page then use it
		        if (errorAsset != null)
		        {
		        	
					// Apply HTTP status code for error views.
					// Only apply it if we're processing a top-level request.
					applyStatusCodeIfPossible(request, response, statusCode);
					
			        PageView view = new PageView(requestContext.getServiceRegistry());
			        view.setUrl("errorpage");
			        			        
		    		ModelAndView mv = new ModelAndView();
			        mv.setView(view);
				    
				    // Store website, section and asset on spring model too for use in page meta data
			        // When exceptions are encountered a new model is created by Spring so any data loaded 
			        // by the the controller interceptors is lost.  
			        modelDecorator.populate(request, mv);
			        
			        // Store error details on model
	    			mv.addObject("exception", ex);		        	
		        	mv.addObject("errorAsset", errorAsset);
			        return mv;					
		        }
			}
		}
		
		// If we couldn't determine an editorially configured error page then use a static one
    	return super.doResolveException(request, response, handler, ex);
    }

	public void setErrorPageSuffix(String errorPageSuffix) 
	{
		this.errorPageSuffix = errorPageSuffix;
	}
	
    public void setAssetFactory(AssetFactory assetFactory)
    {
        this.assetFactory = assetFactory;
    }	
    
	
	public void setModelDecorator(ModelDecorator modelDecorator) 
	{
		this.modelDecorator = modelDecorator;
	}    
}
