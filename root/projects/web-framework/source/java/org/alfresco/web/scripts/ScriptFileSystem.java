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

import org.alfresco.web.site.filesystem.IFile;
import org.alfresco.web.site.filesystem.IFileSystem;

/**
 * The ScriptFileSystem object is a starting point for looking up
 * ScriptFile objects.
 * 
 * The following is valid:
 * 
 * var root = fileSystem.root;
 * var rootParent = root.parent; // == null
 * 
 * var file1 = root.getFile("/a/b/c/readme.txt");
 * var file2 = root.getFile("/a/b/c", "readme.txt");
 * var test = (file1 == file2); // == true
 * 
 * var folder = root.getFile("/a/b/c");
 * var children = folder.getChildren();
 * var children2 = root.getFiles("/a/b/c");
 * 
 * // children identical set to children2
 * 
 * var file3 = root.createFile("/a/b/c/test1.txt");
 * var file4 = root.createFile("/a/b/c", "test2.txt");
 * 
 * var file3parent = root.getParent("/a/b/c/test1.txt");
 * var file4parent = root.getParent("/a/b/c/test2.txt");
 * 
 * // file3parent and file4parent are same folder (/a/b/c)
 * 
 * root.deleteFile("/a/b/c/test1.txt");
 * root.deleteFile("/a/b/c", "test2.txt");
 * 
 * 
 * 
 * @author muzquiano
 */
public final class ScriptFileSystem extends ScriptBase
{
    protected IFileSystem fileSystem;

    /**
     * Instantiates a new script file system.
     * 
     * @param fileSystem the file system
     */
    public ScriptFileSystem(IFileSystem fileSystem)
    {
        this.fileSystem = fileSystem;
    }
    
    /**
     * Wrap file.
     * 
     * @param file the file
     * 
     * @return the script file
     */
    protected ScriptFile wrapFile(IFile file)
    {
        if (file == null)
        {
            return null;
        }
        return new ScriptFile(this, file);
    }

    /**
     * Wrap files.
     * 
     * @param files the files
     * 
     * @return the object[]
     */
    protected Object[] wrapFiles(IFile[] files)
    {
        if (files == null)
        {
            return null;
        }

        Object[] scriptFiles = new Object[files.length];
        for (int i = 0; i < files.length; i++)
        {
            scriptFiles[i] = wrapFile(files[i]);
        }

        return scriptFiles;
    }

    // no support for properties
    protected ScriptableMap buildProperties()
    {
        return null;
    }
    
    
    //------------------------------------------------------------
    // JavaScript Properties
    //
    
    public ScriptFile getRoot()
    {
        return wrapFile(fileSystem.getRoot());
    }
    
    
    //------------------------------------------------------------
    // JavaScript Functions
    //
    
    /**
     * Gets the file.
     * 
     * @param path the path
     * 
     * @return the file
     */
    public ScriptFile getFile(String path)
    {
        return wrapFile(fileSystem.getFile(path));
    }

    /**
     * Gets the file.
     * 
     * @param path the path
     * @param name the name
     * 
     * @return the file
     */
    public ScriptFile getFile(String path, String name)
    {
        return wrapFile(fileSystem.getFile(path, name));
    }

    /**
     * Gets the files.
     * 
     * @param path the path
     * 
     * @return the files
     */
    public Object[] getFiles(String path)
    {
        return wrapFiles(fileSystem.getFiles(path));
    }

    /**
     * Creates the file.
     * 
     * @param relativePath the relative path
     * 
     * @return the script file
     */
    public ScriptFile createFile(String relativePath)
    {
        return wrapFile(fileSystem.createFile(relativePath));
    }

    /**
     * Creates the file.
     * 
     * @param relativeDirectoryPath the relative directory path
     * @param fileName the file name
     * 
     * @return the script file
     */
    public ScriptFile createFile(String relativeDirectoryPath, String fileName)
    {
        return wrapFile(fileSystem.createFile(relativeDirectoryPath, fileName));
    }

    /**
     * Delete file.
     * 
     * @param relativePath the relative path
     * 
     * @return true, if successful
     */
    public boolean deleteFile(String relativePath)
    {
        return fileSystem.deleteFile(relativePath);
    }

    /**
     * Delete file.
     * 
     * @param relativeDirectoryPath the relative directory path
     * @param fileName the file name
     * 
     * @return true, if successful
     */
    public boolean deleteFile(String relativeDirectoryPath, String fileName)
    {
        return fileSystem.deleteFile(relativeDirectoryPath, fileName);
    }

    /**
     * Gets the parent.
     * 
     * @param path the path
     * 
     * @return the parent
     */
    public ScriptFile getParent(String path)
    {
        return wrapFile(fileSystem.getParent(path));
    }    
}
