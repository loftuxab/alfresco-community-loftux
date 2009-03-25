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

import javax.servlet.ServletRequest;

import org.alfresco.web.framework.exception.ContentLoaderException;
import org.alfresco.web.framework.model.Page;
import org.alfresco.web.framework.resource.ResourceContent;
import org.alfresco.web.site.exception.PageMapperException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract class for use in building custom page mappers.
 * 
 * This abstract class intends to make things a little easier for application
 * developers.  It also provides helper methods that may be useful in writing
 * your own page mapper instances.
 * 
 * @author muzquiano
 */
public abstract class AbstractPageMapper implements PageMapper
{
    public static Log logger = LogFactory.getLog(PageMapper.class);
    
    /**
     * Empty constructor - for instantiation via reflection 
     */
    public AbstractPageMapper()
    {
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.PageMapper#execute(org.alfresco.web.site.RequestContext, javax.servlet.ServletRequest)
     */
    public void execute(RequestContext context, ServletRequest request)
        throws PageMapperException
    {
        executeMapper(context, request);
        
        // run some additional cleanup logic
        postExecute(context, request);
    }
    
    /**
     * Execute mapper.
     * 
     * @param context the context
     * @param request the request
     * 
     * @throws PageMapperException the page mapper exception
     */
    public abstract void executeMapper(RequestContext context,
        ServletRequest request) throws PageMapperException;
    
    /**
     * Handles clean up cases
     * 
     * @param context the context
     * @param request the request
     * 
     * @throws PageMapperException the page mapper exception
     */
    public void postExecute(RequestContext context, ServletRequest request)
        throws PageMapperException
    {
        if (context.getSiteConfiguration() == null)
        {
            if (logger.isDebugEnabled())
                debug(context, "No site configuration - performing reset");

            context.setPage(null);
            context.setCurrentObject(null);
        }
        
        // if we have absolutely nothing to dispatch to, then check to
        // see if there is a root-page declared to which we can go
        if (context.getPage() == null && context.getCurrentObjectId() == null)
        {
            // if the site configuration exists...
            if (context.getSiteConfiguration() != null)
            {
                // check if a root page exists to which we can forward
                Page rootPage = context.getRootPage();
                if (rootPage != null)
                {
                    if (logger.isDebugEnabled())
                        debug(context, "Set root page as current page");
                    
                    context.setPage(rootPage);
                }            
            }
        }    
    }
    
    /**
     * Gets the page id for a given page type.
     * 
     * @param context the context
     * @param pageTypeId the page type id
     * 
     * @return the page id
     */
    protected String getPageId(RequestContext context, String pageTypeId)
    {
        if (pageTypeId == null)
        {
            return null;
        }
        
        return FrameworkHelper.getConfig().getDefaultPageTypeInstanceId(pageTypeId);
    }
    
    /**
     * Loads content from the remote content store and returns a Content 
     * object that contains the data.
     * 
     * @param context
     * @param objectId
     * @return
     */
    protected ResourceContent loadContent(RequestContext context, String objectId)
        throws ContentLoaderException
    {
        return ContentLoaderUtil.loadContent(context, objectId);
    }    
    
    /**
     * Helper method for debugging
     */
    protected void debug(RequestContext context, String value)
    {
        logger.debug("PageMapper [" + context.getId() + "] " + value);
    }   
}
