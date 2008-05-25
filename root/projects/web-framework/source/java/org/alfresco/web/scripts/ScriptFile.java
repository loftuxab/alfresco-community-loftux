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

import org.alfresco.web.site.filesystem.IDirectory;
import org.alfresco.web.site.filesystem.IFile;

/**
 * This provides a lightweight interface to a FileSystem file object.
 * 
 * A file object's properties are presumed to be read-only.
 * Changes to the file are performed through function calls.
 * 
 * The following is valid:
 * 
 * var name = file.name;
 * var parent = file.parent;
 * var parentName = file.parent.name;
 * var path = file.path;
 * var length = file.length;
 * var timestamp = file.timestamp;
 * var isFile = file.isFile;
 * var isDirectory = file.isDirectory;
 * 
 * var contents = file.readContents();
 * contents += "extra text";
 * file.writeContents(contents);
 * 
 * var children = file.getChildren();
 * var newChild = file.createFile("readme.txt");
 * var child = file.getChild("readme.txt");
 * 
 * child.delete();
 * 
 * 
 * 
 * 
 * @author muzquiano
 */
public final class ScriptFile extends ScriptBase
{
    protected ScriptFileSystem scriptFileSystem;
    protected IFile file;

    /**
     * Instantiates a new script file.
     * 
     * @param scriptFileSystem the script file system
     * @param file the file
     */
    public ScriptFile(ScriptFileSystem scriptFileSystem, IFile file)
    {
        super();
        
        this.scriptFileSystem = scriptFileSystem;
        this.file = file;
    }
    
    // no support for properties
    public ScriptableMap buildProperties()
    {
        return null;
    }
    

    //------------------------------------------------------------
    // JavaScript Properties
    //
    
    public String getName()
    {
        return file.getName();
    }
    
    public String getTitle()
    {
        return file.getName();
    }
    
    public ScriptFile getParent()
    {
        ScriptFile parent = null;

        IDirectory parentDirectory = file.getParent();
        if(parentDirectory != null)
        {
            parent = scriptFileSystem.wrapFile(parentDirectory);
        }
        
        return parent;
    }
    
    public String getPath()
    {
        return file.getPath();
    }
    
    public long getLength()
    {
        return file.length();
    }
    
    public long getTimestamp()
    {
        return file.getModificationTime();
    }
    
    public long getModificationTime()
    {
        return file.getModificationTime();
    }
    
    public boolean getIsFile()
    {
        return file.isFile();
    }
    
    public boolean getIsDirectory()
    {
        return !file.isFile();
    }
            
    

    // --------------------------------------------------------------
    // JavaScript Functions
    //


    /**
     * Delete.
     * 
     * @return true, if successful
     */
    public boolean delete()
    {
        return file.delete();
    }

    /**
     * Read contents.
     * 
     * @return the string
     */
    public String readContents()
    {
        return file.readContents();
    }

    /**
     * Write contents.
     * 
     * @param contents the contents
     */
    public void writeContents(String contents)
    {
        file.writeContents(contents);
    }

    // for directories

    /**
     * Gets the children.
     * 
     * @return the children
     */
    public Object[] getChildren()
    {
        if (file.isFile())
        {
            return null;
        }

        IFile[] files = ((IDirectory) file).getChildren();
        return scriptFileSystem.wrapFiles(files);
    }

    /**
     * Gets the child.
     * 
     * @param name the name
     * 
     * @return the child
     */
    public ScriptFile getChild(String name)
    {
        if (file.isFile())
        {
            return null;
        }

        IFile child = ((IDirectory) file).getChild(name);
        return scriptFileSystem.wrapFile(child);
    }

    /**
     * Creates the file.
     * 
     * @param name the name
     * 
     * @return the script file
     */
    public ScriptFile createFile(String name)
    {
        if(file.isFile())
        {
            return null;
        }
        
        return scriptFileSystem.createFile(file.getPath(), name);
    }    
}
