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

import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.web.site.model.Page;

/**
 * This is a Page Mapper class which serves to interpret URLs at dispatch
 * time for the Slingshot project.
 * 
 * Requests are received in the style defined for Slingshot.  This class
 * basically lets the generic dispatcher servlet emulate the input
 * structure that PageRendererServlet had been using.
 * 
 * Requests arrive in the form:
 * 
 * 		/page
 * 		/page/<pageId>
 * 		/page/<pageId>?<objArgument>=<objectId>
 * 		/page/<pageId>?<objArgument>=<objectId>&other arguments
 * 
 * The string is tokenized and things are picked off as shown above.
 * 
 * The <pageId> identifier could be the id of the page object.
 * It could also be a relative path to the page object:
 * 
 * 		For example:  /page/collaboration/dashboard
 * 
 * Everything from the original request is available downstream to
 * all rendering components and templates.
 * 
 * @author muzquiano
 */
public class SlingshotPageMapper extends PageMapper
{
    public SlingshotPageMapper()
    {
        super();
    }

    /**
     * Requests come in with the forms:
     * 
     *		/page/pageId	
     *		/page/pageId?doc=<docId>
     *
     * This is a pretty quick and dirty way to implement the
     * interpretation
     */
    /**
     * 
     */
    public void execute(RequestContext context, HttpServletRequest request)
    {
    	/**
    	 * The request URI string.  This comes in as something like:
    	 * 		/slingshot/page/user-profile
    	 */
    	String requestURI = request.getRequestURI();
    	
    	/**
    	 * Tokenizer and walk the string to figure out what kinds of
    	 * inputs we were given in the straight up URI
    	 */
    	String webappId = null;
    	String servletId = null;
    	String pageId = null;
    	StringTokenizer tokenizer = new StringTokenizer(requestURI, "/");
    	if(tokenizer.hasMoreTokens())
    	{
    		webappId = (String) tokenizer.nextToken();
    		if(tokenizer.hasMoreTokens())
    		{
    			servletId = (String) tokenizer.nextToken();
    			if(tokenizer.hasMoreTokens())
    			{
    				// ride out the rest to build the pageId
    				pageId = "";
    				do {
    					pageId += (String) tokenizer.nextToken();
    					if(tokenizer.hasMoreTokens())
    					{
    						pageId += "/";
    					}
    				}while(tokenizer.hasMoreTokens());
    			}
    		}
    	}
    	
    	/**
    	 * Did we receive an "object" request parameter
    	 */
    	String objectId = (String) request.getParameter("doc");
    	if("".equals(objectId))
    	{
    		objectId = null;
    	}
    	
    	
    	/**
    	 * Place everything into the request context
    	 */
    	if (pageId != null && !"".equals(pageId))
    	{
    		/**
    		 * We have a page Id.  We must resolve it to a page.
    		 */
            Page _page = context.getModel().loadPage(context, pageId);
            if (_page != null)
            {
                context.setCurrentPage(_page);
            }
        }

        
        /**
         * TODO:  At present, the Slingshot project doesn't seem to do
         * much with formats, though this may change.  As such, we ignore
         * formats though the RequestContext is capable of supporting
         * them.  
         * 
         * Just for fun, we'll set the default format.
         * 
         * Note that if we didn't set it, it would still automatically
         * pick up the default format.
         */
        context.setCurrentFormatId(Framework.getConfig().getDefaultFormatId());
    	
        
        
        /**
         * If we received a "currently viewed object", then lets set it
         * onto the request as well.
         */
    	if(objectId != null)
    	{
    		context.setCurrentObjectId(objectId);    		
    	}    	
    }
}
