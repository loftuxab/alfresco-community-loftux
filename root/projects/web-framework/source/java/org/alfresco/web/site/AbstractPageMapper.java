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

import org.alfresco.web.site.exception.PageMapperException;

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
    /**
     * Instantiates a new abstract page mapper.
     */
    public AbstractPageMapper()
    {
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.PageMapper#execute(org.alfresco.web.site.RequestContext, javax.servlet.ServletRequest)
     */
    public abstract void execute(RequestContext context,
            ServletRequest request) throws PageMapperException;
    
    /**
     * Gets the default page id.
     * 
     * @param context the context
     * @param pageTypeId the page type id
     * 
     * @return the default page id
     */
    protected String getDefaultPageId(RequestContext context, String pageTypeId)
    {
        if(pageTypeId == null)
        {
            return null;
        }
        
        return context.getConfig().getDefaultPageTypeInstanceId(pageTypeId);
    }
    
}
