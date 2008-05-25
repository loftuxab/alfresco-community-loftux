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
package org.alfresco.web.site.cache;

import org.alfresco.web.site.filesystem.IFile;
import org.alfresco.web.site.filesystem.IFileSystem;
import org.alfresco.web.site.model.ModelObject;

/**
 * This is an enhancement on the basic cache that performs an additional
 * check to the source file within the AVM to see if timestamps have
 * changed.  This is not a persistent cache.  If objects are removed
 * from memory, they must be reloaded from disk (the AVM store)
 * 
 * @author muzquiano
 */
public class ModelObjectCache extends BasicCache
{
    
    /**
     * Instantiates a new model object cache.
     * 
     * @param fileSystem the file system
     * @param default_timeout the default_timeout
     */
    protected ModelObjectCache(IFileSystem fileSystem, long default_timeout)
    {
        super(default_timeout);
        this.fileSystem = fileSystem;
    }

    /** The file system. */
    protected IFileSystem fileSystem = null;

    /* (non-Javadoc)
     * @see org.alfresco.web.site.cache.BasicCache#get(java.lang.String)
     */
    public synchronized Object get(String key)
    {
        ModelObject obj = (ModelObject) super.get(key);
        if (obj != null)
        {
            // additional check
            // is the mod time of the cached object the same or
            // better than the one on disk?

            // if the same, it means we have a cached copy of the exact
            // one on disk

            // if newer, it means that we've made some updates

            long cachedModificationTime = obj.getModificationTime();

            // check the modification time on disk
            IFile file = this.fileSystem.getFile(key);
            if (file == null)
            {
                // file was deleted from disk
                // so remove from cache
                remove(key);
                obj = null;
            }
            if (file != null)
            {
                try
                {
                    long currentModificationTime = file.getModificationTime();
                    if (currentModificationTime > cachedModificationTime)
                    {
                        remove(key);
                        obj = null;
                    }
                }
                catch (Exception ex)
                {
                    // something messed up with the file modification date check
                    // it may have been deleted from disk
                    remove(key);
                    obj = null;
                }
            }
        }
        return obj;
    }
}
