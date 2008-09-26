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

/**
 * Utility class that allows for easy invalidation of the cache.
 * 
 * This is used primarly by the CacheServlet which receives calls
 * from the outside world to "refresh" the cache.
 * 
 * It is also invoked from within the scripting layer to force
 * cache refreshes when objects have been changed through scripting.
 * 
 * @author muzquiano
 */
public class CacheUtil
{
    /**
     * Invalidate model object service object cache.
     * 
     * @param context the context
     */
    public static void invalidateModelObjectServiceCache(RequestContext context)
    {
        // invalidate the model object service state for this user context
        context.getModel().getObjectManager().invalidateCache();

        FrameworkHelper.getLogger().info("Invalidated Object Cache");
    }
}
