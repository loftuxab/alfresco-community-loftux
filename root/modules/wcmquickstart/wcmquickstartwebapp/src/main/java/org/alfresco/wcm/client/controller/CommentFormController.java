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
package org.alfresco.wcm.client.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.WebSite;
import org.alfresco.wcm.client.impl.VisitorFeedbackImpl;
import org.alfresco.wcm.client.validator.FeedbackValidator;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

/**
 * CommentFormController processes a user generated feedback submission such as a comment
 * on a blog or a contact form post.
 * @author Chris Lack
 */
@Controller
@RequestMapping(value={"/blog/**",  // blog comments
		               "/contact.html"}) // contact form
public class CommentFormController
{

    @ModelAttribute("feedback")
    public VisitorFeedbackImpl populateFeedback() 
    {
    	RequestContext requestContext = ThreadLocalRequestContext.getRequestContext();
    	Asset asset = (Asset) requestContext.getValue("asset");
    	WebSite webSite = (WebSite) requestContext.getValue("webSite");

    	VisitorFeedbackImpl feedback = new VisitorFeedbackImpl();
    	// If the page corresponds to an asset, eg a blog, then store it's id on the feedback object
    	if (asset != null) 
    	{
    		feedback.setAssetId(asset.getId());
    	}
    	else // else use the website id, eg for the contact form. 
    	{
    		feedback.setAssetId(webSite.getId());
    	}
    	return feedback;
    }
    
	@InitBinder
    protected void initBinder(WebDataBinder binder) 
	{
        binder.setValidator(new FeedbackValidator());
    }

	@RequestMapping(method=RequestMethod.POST)
	protected View processSubmit(@Valid @ModelAttribute("feedback") VisitorFeedbackImpl feedback, 
							     BindingResult result, Model model, HttpServletRequest request)
	{ 
    	if ( ! result.hasErrors()) 
    	{
    		// Save feedback to repository
	    	RequestContext requestContext = ThreadLocalRequestContext.getRequestContext();
	    	WebSite website = (WebSite) requestContext.getValue("webSite");
			website.getUgcService().postFeedback(feedback);
			
			// Redirect to success page to prevent refresh button causing second post.
			RedirectView redirect = new RedirectView(feedback.getSuccessPage(), false, false);             
			redirect.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
			return redirect; 			
    	}		
		processGet(feedback, request);
		return null; // Return to same page when validation errors exist
	}
    
    @RequestMapping(method=RequestMethod.GET)
    public View processGet(@ModelAttribute("feedback") VisitorFeedbackImpl feedback, HttpServletRequest request) 
    {
    	// If the report parameter is present then the site visitor has clicked on a report feedback link
    	String reportId = request.getParameter("report");    
    	if (reportId != null) 
    	{
    		// Flag the feedback in the repository
	    	RequestContext requestContext = ThreadLocalRequestContext.getRequestContext();
	    	WebSite website = (WebSite) requestContext.getValue("webSite");
			website.getUgcService().reportFeedback(reportId);
			
			// Redirect to current page to prevent refresh button causing second post
			RedirectView redirect = new RedirectView(request.getContextPath()+request.getPathInfo(), false, false);             
			redirect.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
			return redirect; 						
    	}
    	return null;
    }
}
