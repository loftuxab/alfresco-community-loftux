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

/**
 * @author muzquiano
 */
public class CachedFile implements IFile
{
    public CachedFile(CachedFileSystem fileSystem, IFile file)
    {
        this.fileSystem = fileSystem;
        this.file = file;
    }

    public String getName()
    {
        if (this.name == null)
            this.name = file.getName();
        return this.name;
    }

    public IDirectory getParent()
    {
        return fileSystem.getParent(this.getPath());
    }

    public boolean delete()
    {
        return this.fileSystem.deleteFile(this.getPath());
    }

    public String getPath()
    {
        return file.getPath();
    }

    public long length()
    {
        if (length == -1)
            length = file.length();
        return length;
    }

    public boolean isFile()
    {
        return file.isFile();
    }

    public long getModificationDate()
    {
        if (modDate == -1)
            modDate = file.getModificationDate();
        return modDate;
    }

    public byte[] readBytes()
    {
        return file.readBytes();
    }

    public void writeBytes(byte[] array)
    {
        file.writeBytes(array);
    }

    public String readContents()
    {
        return new String(readBytes());
    }

    public void writeContents(String contents)
    {
        writeBytes(contents.getBytes());
    }

    public InputStream getInputStream() throws Exception
    {
        return file.getInputStream();
    }

    public OutputStream getOutputStream() throws Exception
    {
        return file.getOutputStream();
    }

    protected IFileSystem fileSystem;
    protected IFile file;

    protected long modDate = -1;
    protected long length = -1;
    protected String name = null;
}
