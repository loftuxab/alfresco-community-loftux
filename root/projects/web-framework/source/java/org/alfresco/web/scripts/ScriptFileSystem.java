/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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

import java.io.Serializable;

import org.alfresco.web.site.filesystem.IDirectory;
import org.alfresco.web.site.filesystem.IFile;
import org.alfresco.web.site.filesystem.IFileSystem;

/**
 * @author muzquiano
 */
public final class ScriptFileSystem extends ScriptBase
{
    protected IFileSystem fileSystem;
    
    public ScriptFileSystem(IFileSystem fileSystem)
    {
        super();
        this.fileSystem = fileSystem;        
    }
    
    protected ScriptFile wrapFile(IFile file)
    {
        if(file == null)
        {
            return null;
        }
        return new ScriptFile(this, file);
    }

    protected Object[] wrapFiles(IFile[] files)
    {
        if(files == null)
        {
            return null;
        }
        
        Object[] scriptFiles = new Object[files.length];
        for(int i = 0; i < files.length; i++)
        {
            scriptFiles[i] = wrapFile(files[i]);
        }
        
        return scriptFiles;        
    }
    
    // API
    
    public ScriptFile getRoot()
    {
        return wrapFile(fileSystem.getRoot());
    }
    
    public ScriptFile getFile(String path)
    {
        return wrapFile(fileSystem.getFile(path));        
    }

    public ScriptFile getFile(String path, String name)
    {
        return wrapFile(fileSystem.getFile(path, name));        
    }

    public Object[] getFiles(String path)
    {
        return wrapFiles(fileSystem.getFiles(path));        
    }

    public ScriptFile createFile(String relativePath)
    {
        return wrapFile(fileSystem.createFile(relativePath));        
    }

    public ScriptFile createFile(String relativeDirectoryPath, String fileName)
    {
        return wrapFile(fileSystem.createFile(relativeDirectoryPath, fileName));        
    }

    public boolean deleteFile(String relativePath)
    {
        return fileSystem.deleteFile(relativePath);        
    }

    public boolean deleteFile(String relativeDirectoryPath, String fileName)
    {
        return fileSystem.deleteFile(relativeDirectoryPath, fileName);
    }

    public ScriptFile getParent(String path)
    {
        return wrapFile(fileSystem.getParent(path));
    }
    
    
    
    // inner classes
    
    public class ScriptFile implements Serializable
    {
        public ScriptFileSystem scriptFileSystem;
        public IFile file;
        
        public ScriptFile(ScriptFileSystem scriptFileSystem, IFile file)
        {
            this.scriptFileSystem = scriptFileSystem;
            this.file = file;
        }
        
        public String getName()
        {
            return file.getName();
        }

        public ScriptFile getParent()
        {
            return scriptFileSystem.wrapFile(file.getParent());
        }

        public boolean delete()
        {
            return file.delete();
        }

        public String getPath()
        {
            return file.getPath();
        }

        public long length()
        {
            return file.length();
        }

        public boolean isFile()
        {
            return file.isFile();
        }
        
        public boolean isDirectory()
        {
            return !file.isFile();
        }

        public long getModificationDate()
        {
            return file.getModificationDate();
        }
        
        public String readContents()
        {
            return file.readContents();            
        }
        
        public void writeContents(String contents)
        {
            file.writeContents(contents);            
        }
        
        // for directories
        
        public Object[] getChildren()
        {
            if(file.isFile())
            {
                return null;
            }
            
            IFile[] files = ((IDirectory)file).getChildren();
            return scriptFileSystem.wrapFiles(files);
        }

        public ScriptFile getChild(String name)
        {
            if(file.isFile())
            {
                return null;
            }
            
            IFile child = ((IDirectory)file).getChild(name);
            return scriptFileSystem.wrapFile(child);
        }

        public ScriptFile createFile(String name)
        {
            return scriptFileSystem.createFile(file.getPath(), name);            
        }
        
    }
}
