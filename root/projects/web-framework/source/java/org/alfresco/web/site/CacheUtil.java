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

import org.alfresco.web.site.filesystem.CachedFileSystem;
import org.alfresco.web.site.filesystem.IFileSystem;

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
     * Invalidate file system cache.
     * 
     * @param context the context
     */
    public static void invalidateFileSystemCache(RequestContext context)
    {
        IFileSystem fileSystem = context.getFileSystem();
        if (fileSystem instanceof CachedFileSystem)
        {
            CachedFileSystem cachedFileSystem = (CachedFileSystem) fileSystem;
            cachedFileSystem.refresh();
            
            Framework.getLogger().info("Invalidated File System Cache");
        }
    }

    /**
     * Invalidate ads object cache.
     * 
     * @param context the context
     */
    public static void invalidateADSObjectCache(RequestContext context)
    {
        IModel model = context.getModel();
        if (model instanceof DefaultModel)
        {
            ((DefaultModel) model).cacheInvalidateAll(context);
            
            Framework.getLogger().info("Invalidated Object Cache");
        }
    }

    /**
     * Checks if is file system cache enabled.
     * 
     * @param context the context
     * 
     * @return true, if is file system cache enabled
     */
    public static boolean isFileSystemCacheEnabled(RequestContext context)
    {
        IFileSystem fileSystem = context.getFileSystem();
        return (fileSystem instanceof CachedFileSystem);
    }

}
