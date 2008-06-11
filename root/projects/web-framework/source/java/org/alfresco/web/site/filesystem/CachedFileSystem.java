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

import java.io.InputStream;
import java.io.OutputStream;

import org.alfresco.web.framework.cache.BasicCache;
import org.alfresco.web.framework.cache.ContentCache;
import org.alfresco.web.site.FrameworkHelper;

/**
 * @author muzquiano
 */
public class CachedFileSystem implements IFileSystem
{
    protected static long DEFAULT_CACHE_TIMEOUT = 30* 60 * 60;
    
    public CachedFileSystem(IFileSystem fileSystem)
    {
        this.fileSystem = fileSystem;

        // initialize the caches
        this.fileCache = new BasicCache(DEFAULT_CACHE_TIMEOUT);
        this.childrenCache = new BasicCache(DEFAULT_CACHE_TIMEOUT);
        this.parentCache = new BasicCache(DEFAULT_CACHE_TIMEOUT);

        FrameworkHelper.getLogger().info("CachedFileSystem started");
    }

    // RETURN FROM CACHE
    public IDirectory getRoot()
    {
        IFile file = getFile("/");
        if (file instanceof IDirectory)
            return ((IDirectory) file);
        return null;
    }

    public String getAbsolutePath(IFile file)
    {
        return this.fileSystem.getAbsolutePath(file);
    }

    // TODO
    // RETURN FROM CACHE
    public InputStream getInputStream(IFile file) throws Exception
    {
        return this.fileSystem.getInputStream(file);
    }

    // TODO
    // RETURN FROM CACHE	
    public OutputStream getOutputStream(IFile file) throws Exception
    {
        return this.fileSystem.getOutputStream(file);
    }

    // RETURN FROM CACHE
    public IDirectory getParent(String path)
    {
        IDirectory parent = (IDirectory) parentCache.get(path);
        if (parent != null)
            return parent;

        parent = toCachedDirectory(fileSystem.getParent(path));
        this.parentCache.put(path, parent);

        return parent;
    }

    // RETURN FROM CACHE
    public IFile getFile(String path)
    {
        IFile file = (IFile) fileCache.get(path);
        if (file != null)
            return file;

        IFile realFile = fileSystem.getFile(path);
        file = toCachedFile(realFile);
        if (file != null)
            this.fileCache.put(path, file);

        return file;
    }

    public IFile getFile(String path, String name)
    {
        return getFile(path + "/" + name);
    }

    // RETURN FROM CACHE
    public IFile[] getFiles(String path)
    {
        IFile[] files = (IFile[]) this.childrenCache.get(path);
        if (files != null)
            return files;

        files = toCachedFiles(this.fileSystem.getFiles(path));
        if (files != null)
            this.childrenCache.put(path, files);

        return files;
    }

    public IFile createFile(String path)
    {
        int i = path.lastIndexOf("/");
        String parentPath = path.substring(0, i);
        String fileName = path.substring(i + 1, path.length());
        return createFile(parentPath, fileName);
    }

    // INVALIDATE CACHE
    public IFile createFile(String directoryPath, String fileName)
    {
        IFile _file = this.fileSystem.createFile(directoryPath, fileName);
        IFile cachedFile = toCachedFile(_file);
        this.fileCache.put(cachedFile.getPath(), cachedFile);

        // add this file to the parent's list of children
        IFile[] children = (IFile[]) this.childrenCache.get(directoryPath);
        if (children != null && children.length > 0)
        {
            int ctr = 0;
            IFile[] newChildren = new IFile[children.length + 1];
            for (int i = 0; i < children.length; i++)
            {
                newChildren[ctr] = children[i];
                ctr++;
            }
            newChildren[ctr] = cachedFile;
            this.childrenCache.put(directoryPath, newChildren);
        }
        return cachedFile;
    }

    public boolean deleteFile(String path)
    {
        int i = path.lastIndexOf("/");
        String parentPath = path.substring(0, i);
        String fileName = path.substring(i + 1, path.length());
        return deleteFile(parentPath, fileName);
    }

    // INVALIDATE CACHE
    public boolean deleteFile(String directoryPath, String fileName)
    {
        IFile _file = getFile(directoryPath, fileName);
        this.fileSystem.deleteFile(directoryPath, fileName);

        // remove the file from cache as well as its relationship to parent
        this.fileCache.remove(_file.getPath());
        this.parentCache.remove(_file.getPath());

        // remove this file from parent's list of children
        IFile[] children = (IFile[]) this.childrenCache.get(directoryPath);
        if (children != null && children.length > 0)
        {
            int ctr = 0;
            IFile[] newChildren = new IFile[children.length - 1];
            for (int i = 0; i < children.length; i++)
            {
                if (!children[i].getName().equals(fileName))
                {
                    newChildren[ctr] = children[i];
                    ctr++;
                }
            }
            this.childrenCache.put(directoryPath, newChildren);
        }
        return true;
    }

    // blow away the entire cache
    public void refresh()
    {
        this.fileCache.invalidate();
        this.parentCache.invalidate();
        this.childrenCache.invalidate();

    }

    // original file system (which we are wrapping)
    protected IFileSystem fileSystem;

    // path -> 1 CachedFile or CachedDirectory object
    protected ContentCache fileCache;

    // path -> 1 CachedDirectory parent
    protected ContentCache parentCache;

    // path -> Many CachedFile and/or CachedDirectory objects
    protected ContentCache childrenCache;

    ///////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////	

    protected CachedFile toCachedFile(IFile file)
    {
        if (file == null)
            return null;
        return new CachedFile(this, file);
    }

    protected CachedFile[] toCachedFiles(IFile[] files)
    {
        if (files == null)
            return null;

        CachedFile[] array = new CachedFile[files.length];
        for (int i = 0; i < files.length; i++)
        {
            array[i] = toCachedFile(files[i]);
        }
        return array;
    }

    protected CachedDirectory toCachedDirectory(IDirectory dir)
    {
        return new CachedDirectory(this, dir);
    }
}
