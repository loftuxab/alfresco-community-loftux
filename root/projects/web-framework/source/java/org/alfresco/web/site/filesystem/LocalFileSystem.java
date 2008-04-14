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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;

/**
 * @author muzquiano
 */
public class LocalFileSystem implements IFileSystem
{
    public LocalFileSystem(File rootDirectory)
    {
        setRootDirectory(rootDirectory);
    }

    public LocalFileSystem()
    {
    }

    public void setRootDirectory(File rootDirectory)
    {
        this.rootDirectory = rootDirectory;
    }

    public IDirectory getRoot()
    {
        return new LocalDirectory(this, rootDirectory, "/");
    }

    public String getAbsolutePath(IFile file)
    {
        String absPath = rootDirectory.getAbsolutePath();
        absPath = absPath + file.getPath();
        return absPath;
    }

    public InputStream getInputStream(IFile file) throws Exception
    {
        File f = ((LocalFile) file).file;
        return new FileInputStream(f);
    }

    public OutputStream getOutputStream(IFile file) throws Exception
    {
        File f = ((LocalFile) file).file;
        return new FileOutputStream(f);
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

    // retrieves a file at a specific path
    public IFile getFile(String path)
    {
        IFile current = this.getRoot();

        StringTokenizer tokenizer = new StringTokenizer(path, "/");
        while (tokenizer.hasMoreTokens())
        {
            String childName = (String) tokenizer.nextToken();
            if ((current.isFile()) && tokenizer.hasMoreTokens())
                return null;

            if (!current.isFile())
                current = _getChild(((IDirectory) current), childName);

            if (current == null)
                return null;
        }
        return current;
    }

    public IFile getFile(String path, String name)
    {
        return getFile(path + "/" + name);
    }

    protected IFile _getChild(IDirectory dir, String name)
    {
        IFile newFile = null;

        java.io.File f = (java.io.File) ((LocalDirectory) dir).getFileObject();
        File[] fs = f.listFiles();
        if (fs != null && fs.length > 0)
        {
            for (int i = 0; i < fs.length; i++)
            {
                String _name = fs[i].getName();
                if (_name != null && _name.equals(name))
                {
                    String newPath = dir.getPath() + "/" + name;
                    if (fs[i].isDirectory())
                        newFile = new LocalDirectory(this, fs[i], newPath);
                    else
                        newFile = new LocalFile(this, fs[i], newPath);
                    return newFile;
                }
            }
        }
        return newFile;
    }

    // retrieve all of the children at a given path
    public IFile[] getFiles(String path)
    {
        IFile file = getFile(path);
        if (file instanceof IDirectory)
        {
            IFile[] array = null;

            java.io.File f = (java.io.File) ((LocalDirectory) file).getFileObject();
            File[] fs = f.listFiles();
            array = new IFile[fs.length];
            for (int i = 0; i < fs.length; i++)
            {
                IFile newFile = null;

                String newPath = path + "/" + fs[i].getName();
                if (fs[i].isDirectory())
                    newFile = new LocalDirectory(this, fs[i], newPath);
                else
                    newFile = new LocalFile(this, fs[i], newPath);

                array[i] = newFile;
            }

            return array;
        }
        return null;
    }

    public IFile createFile(String path)
    {
        int i = path.lastIndexOf("/");
        String parentPath = path.substring(0, i);
        String fileName = path.substring(i + 1, path.length());
        return createFile(parentPath, fileName);
    }

    public IFile createFile(String directoryPath, String fileName)
    {
        IDirectory parentDirectory = (IDirectory) getFile(directoryPath);
        if (parentDirectory != null)
        {
            String absDirectoryPath = this.getAbsolutePath(parentDirectory);
            File fileObject = new File(absDirectoryPath, fileName);

            String filePath = directoryPath + "/" + fileName;

            IFile file = new LocalFile(this, fileObject, filePath);
            return file;
        }
        return null;
    }

    public boolean deleteFile(String path)
    {
        int i = path.lastIndexOf("/");
        String parentPath = path.substring(0, i);
        String fileName = path.substring(i + 1, path.length());
        return deleteFile(parentPath, fileName);
    }

    public boolean deleteFile(String directoryPath, String fileName)
    {
        IDirectory parentDirectory = (IDirectory) getFile(directoryPath);
        if (parentDirectory != null)
        {
            IFile childFile = parentDirectory.getChild(fileName);
            if (childFile != null)
            {
                System.out.println("LocalFileSystem: Deleting " + childFile.getName());

                LocalFile localFile = (LocalFile) childFile;
                File f = localFile.getFileObject();

                System.out.println("LocalFileSystem 11");
                return f.delete();
            }
        }
        return false;
    }

    protected File rootDirectory;
}
