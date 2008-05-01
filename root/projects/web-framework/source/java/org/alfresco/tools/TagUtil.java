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
package org.alfresco.tools;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import org.alfresco.web.site.exception.TagExecutionException;

/**
 * Provides a simple helper class for executing tags in wrapped
 * HTTP objects.
 * 
 * @author Uzquiano
 */
public class TagUtil
{
    
    /**
     * Executes the given tag within the context of the given servlet request.
     * 
     * @param tag the tag
     * @param request the request
     * 
     * @return the string
     */
    public static String execute(Tag tag, HttpServletRequest request)
        throws TagExecutionException
    {
        return execute(tag, request, null);
    }

    /**
     * Executes the given tag within the context of the given servlet request.
     * The supplied body content is taken to be the body tag.
     * 
     * Body content cannot contain additional tags for processing.  This method
     * will not parse the contents of the body and process those tags.  Rather,
     * it will insert the contents of the body into the output stream at the
     * appropriate point.
     * 
     * @param tag the tag
     * @param request the request
     * @param bodyContentString the body content string
     * 
     * @return the string
     */
    public static String execute(Tag tag, HttpServletRequest request, String bodyContentString)
        throws TagExecutionException
    {
        /**
         * Extract the servlet context from the request (session)
         * Then proceed into the workhorse method
         */
        ServletContext context = request.getSession().getServletContext();
        return execute(tag, context, request, bodyContentString);
    }

    /**
     * Executes the given tag within the context of the given servlet
     * context and request.
     * 
     * @param tag the tag
     * @param context the context
     * @param request the request
     * 
     * @return the string
     */
    public static String execute(Tag tag, ServletContext context,
            HttpServletRequest request)
        throws TagExecutionException
    {
        return execute(tag, context, request, null);
    }
    
    /**
     * Executes the given tag within the context of the given servlet
     * context and request.
     * 
     * The supplied body content is taken to be the body tag.
     * 
     * Body content cannot contain additional tags for processing.  This method
     * will not parse the contents of the body and process those tags.  Rather,
     * it will insert the contents of the body into the output stream at the
     * appropriate point.
     * 
     * This is the main workhorse method and is a lightweight implementation
     * of a tag runner.
     * 
     * @param tag the tag
     * @param context the context
     * @param request the request
     * @param bodyContentString the body content string
     * 
     * @return the string
     */
    public static String execute(Tag tag, ServletContext context,
            HttpServletRequest request, String bodyContentString)
        throws TagExecutionException
    {
        /**
         * Manufacture a request implementation within which the tag will run
         */
        WrappedHttpServletRequest tagRequest = new WrappedHttpServletRequest(
                request);

        /**
         * Manufacture a response implementation within which the tag will run
         */
        FakeHttpServletResponse tagResponse = new FakeHttpServletResponse();

        /**
         * Execute the tag.  This proceeds by "running" the tag as per the
         * JSP tag mechanism.
         */
        String response = null;
        try
        {
            /**
             * Manufacture a jsp writer against which the tag will run
             */
            FakeJspWriter tagJspWriter = new FakeJspWriter(
                    tagResponse.getWriter());

            /**
             * Manufacture a jsp page context against which the tag will run
             */
            FakeJspPageContext tagJspPageContext = new FakeJspPageContext(
                    context, tagRequest, tagResponse, tagJspWriter);

            /**
             * Start the tag runner
             */
            tag.setPageContext(tagJspPageContext);
            int startTagReturn = tag.doStartTag();
            if(tag instanceof BodyTagSupport)
            {
                if(startTagReturn == tag.EVAL_BODY_INCLUDE)
                {   
                    BodyTagSupport support = ((BodyTagSupport)tag);
                    
                    BodyContent bc = tagJspPageContext.pushBody();
                    support.setBodyContent(bc);
                    
                    support.doInitBody();
                    support.doAfterBody();
                    
                    tagJspPageContext.popBody();
                }
            }
            
            /**
             * If we have body content, copy it into the output stream
             */
            if(bodyContentString != null)
            {
                tagJspWriter.print(bodyContentString);
            }
            tag.doEndTag();
            tag.release();

            /**
             * Pick off the output and return it
             */
            response = tagResponse.getContentAsString();
        }
        catch (Exception ex)
        {
            throw new TagExecutionException("Unable to process tag: " + tag.toString(), ex);
        }
        
        return response;
    }
}
