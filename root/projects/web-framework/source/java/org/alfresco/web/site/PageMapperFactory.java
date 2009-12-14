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

import org.springframework.extensions.surf.util.ReflectionHelper;
import org.alfresco.web.site.exception.PageMapperException;

/**
 * @author muzquiano
 */
public final class PageMapperFactory
{
    public static PageMapper newInstance(RequestContext context)
        throws PageMapperException
    {
        PageMapper pageMapper;
        
        // check if there is a configured page mapper id
        String pageMapperId = FrameworkHelper.getConfig().getDefaultPageMapperId();
        if (pageMapperId == null)
        {
            // default that we will use
            pageMapper = new DefaultPageMapper();
        }
        else
        {
            // construct a new page mapper for this request instance
            String className = FrameworkHelper.getConfig().getPageMapperDescriptor(pageMapperId).getImplementationClass();
            pageMapper = (PageMapper) ReflectionHelper.newObject(className);
            if (pageMapper == null)
            {
                throw new PageMapperException("Unable to create page mapper for class name: " + className);
            }
        }
        
        if (FrameworkHelper.getLogger().isDebugEnabled())
        {
            FrameworkHelper.getLogger().debug("Constructed page mapper: " + pageMapper.getClass().toString());
        }
        
        return pageMapper;
    }    
}
