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
package org.alfresco.web.scripts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.alfresco.web.site.filesystem.IDirectory;
import org.alfresco.web.site.filesystem.IFile;
import org.alfresco.web.site.filesystem.IFileSystem;

/**
 * @author muzquiano
 */
public class StoreFileSystem implements IFileSystem
{
    public StoreFileSystem(Store store)
    {
        this.store = store;
    }

    public StoreFileSystem()
    {
    }

    public IDirectory getRoot()
    {
        IFile file = getFile("/");
        if (file instanceof IDirectory)
            return ((IDirectory) file);
        return null;
    }

    public String getAbsolutePath(IFile file)
    {
        return store.getBasePath() + file.getPath();
    }

    public InputStream getInputStream(IFile file) throws Exception
    {
        return store.getDocument(file.getPath());
    }

    public OutputStream getOutputStream(IFile file) throws Exception
    {
        return new StoreFileOutputStream(this, file);
    }

    public IFile getFile(String path)
    {
        if (store.hasDocument(path))
            return new StoreFile(this, path);
        return null;
    }

    public IFile getFile(String path, String name)
    {
        return getFile(path + "/" + name);
    }

    // TODO
    public IFile[] getFiles(String path)
    {
        return null;
    }

    public IDirectory getParent(String path)
    {
        if (path == null)
            return null;
        if (path.length() > 1)
        {
            int i = path.lastIndexOf("/");
            String parentPath = path.substring(0, i);
            IDirectory dir = (IDirectory) getFile(parentPath);
            return dir;
        }
        return null;
    }

    public IFile createFile(String path)
    {
        String content = "";
        try
        {
            store.createDocument(path, content);
            return getFile(path);
        }
        catch (Exception ex)
        {
        }
        return null;
    }

    public IFile createFile(String directoryPath, String fileName)
    {
        return createFile(directoryPath + "/" + fileName);
    }

    public boolean deleteFile(String path)
    {
        int i = path.lastIndexOf("/");
        String parentPath = path.substring(0, i);
        String fileName = path.substring(i + 1, path.length());
        return deleteFile(parentPath, fileName);
    }

    // TODO
    public boolean deleteFile(String directoryPath, String fileName)
    {
        return false;
    }

    protected Store store;

    protected class StoreFileOutputStream extends ByteArrayOutputStream
    {
        protected IFile file;
        protected StoreFileSystem fileSystem;

        public StoreFileOutputStream(StoreFileSystem fileSystem, IFile file)
        {
            this.fileSystem = fileSystem;
            this.file = file;
        }

        public void close() throws IOException
        {
            String content = this.buf.toString();
            fileSystem.store.updateDocument(file.getPath(), content);
        }

    }
}
