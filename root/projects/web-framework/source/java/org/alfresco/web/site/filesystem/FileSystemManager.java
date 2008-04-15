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
package org.alfresco.web.site.filesystem;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.alfresco.web.site.Framework;

/**
 * @author muzquiano
 */
public class FileSystemManager
{
    protected static Map fileSystems = null;

    /**
     * Generic way to get a file system
     * Performs no initialization if the instance is created
     * @param id
     * @return
     */
    protected static IFileSystem newFileSystem(String fileSystemId,
            String cacheKey)
    {
        if (fileSystems == null)
            fileSystems = new HashMap();

        IFileSystem fileSystem = (IFileSystem) fileSystems.get(cacheKey);
        if (fileSystem == null)
        {
            String className = Framework.getConfig().getFileSystemClass(
                    fileSystemId);
            try
            {
                fileSystem = (IFileSystem) Class.forName(className).newInstance();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        return fileSystem;
    }

    protected static void putFileSystem(String cacheKey, IFileSystem fileSystem)
    {
        if (fileSystems == null)
        {
            fileSystems = new HashMap();
        }
        fileSystems.put(cacheKey, fileSystem);
    }

    public static IFileSystem getFileSystem(String cacheKey)
    {
        if (fileSystems == null)
        {
            fileSystems = new HashMap();
        }

        return (IFileSystem) fileSystems.get(cacheKey);
    }

    public static IFileSystem getLocalFileSystem(ServletContext servletContext,
            String relativePath)
    {
        String realPath = servletContext.getRealPath(relativePath);
        return getLocalFileSystem(realPath);
    }

    public static IFileSystem getLocalFileSystem(String realPath)
    {
        File f = new File(realPath);
        return getLocalFileSystem(f);
    }

    public static IFileSystem getLocalFileSystem(File rootDirectory)
    {
        String cacheKey = rootDirectory.getAbsolutePath();

        IFileSystem fileSystem = getFileSystem(cacheKey);
        if (fileSystem != null)
            return fileSystem;

        // otherwise, let's create a new file system
        fileSystem = newFileSystem("local", cacheKey);
        if (fileSystem != null)
        {
            // perform instantiation
            LocalFileSystem localFileSystem = (LocalFileSystem) fileSystem;
            localFileSystem.setRootDirectory(rootDirectory);

            // flip on caching?
            if ("true".equals(Framework.getConfig().getFileSystemUseCache(
                    "local")))
                fileSystem = new CachedFileSystem(fileSystem);

            putFileSystem(cacheKey, fileSystem);
        }
        return fileSystem;
    }
}
