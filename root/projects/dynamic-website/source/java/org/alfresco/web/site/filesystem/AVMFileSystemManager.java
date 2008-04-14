/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 * As a special exception to the terms and conditions of version 2.0 of the GPL,
 * you may redistribute this Program in connection with Free/Libre and Open
 * Source Software ("FLOSS") applications as described in Alfresco's FLOSS
 * exception. You should have recieved a copy of the text describing the FLOSS
 * exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.site.filesystem;

import org.alfresco.service.cmr.remote.AVMRemote;
import org.alfresco.web.site.Framework;

/**
 * @author muzquiano
 */
public class AVMFileSystemManager extends FileSystemManager
{
    public static IFileSystem getAVMFileSystem(AVMRemote avmRemote,
            String avmStoreId, String avmWebappPath)
    {
        String cacheKey = "avm-" + avmStoreId;

        IFileSystem fileSystem = getFileSystem(cacheKey);
        if (fileSystem != null)
            return fileSystem;

        // otherwise, let's create a new file system
        fileSystem = newFileSystem("avm", cacheKey);
        if (fileSystem != null)
        {
            // perform instantiation
            AVMFileSystem avmFileSystem = (AVMFileSystem) fileSystem;
            avmFileSystem.setAVMRemote(avmRemote);
            avmFileSystem.setAVMStoreId(avmStoreId);
            avmFileSystem.setAVMWebappPath(avmWebappPath);

            // flip on caching?
            if ("true".equals(Framework.getConfig().getFileSystemUseCache("avm")))
                fileSystem = new CachedFileSystem(avmFileSystem);

            putFileSystem(cacheKey, fileSystem);
        }
        return fileSystem;
    }

}
