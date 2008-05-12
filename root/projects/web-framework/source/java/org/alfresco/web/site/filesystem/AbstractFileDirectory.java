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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.alfresco.tools.DataUtil;
import org.alfresco.web.site.FrameworkHelper;

/**
 * @author muzquiano
 */
public abstract class AbstractFileDirectory implements IFile
{
    public AbstractFileDirectory(IFileSystem fileSystem)
    {
        this.fileSystem = fileSystem;
    }

    public IDirectory getParent()
    {
        return fileSystem.getParent(this.getPath());
    }

    public boolean delete()
    {
        return this.fileSystem.deleteFile(this.getPath());
    }

    public boolean isFile()
    {
        return true;
    }

    public abstract String getPath();

    public abstract long length();

    public abstract long getModificationDate();

    public abstract String getName();

    public IFile[] getChildren()
    {
        return fileSystem.getFiles(this.getPath());
    }

    public IFile getChild(String name)
    {
        return fileSystem.getFile(this.getPath(), name);
    }

    public IFile createFile(String name)
    {
        return this.fileSystem.createFile(this.getPath(), name);
    }

    public byte[] readBytes()
    {
        byte[] array = null;

        if (this.isFile())
        {
            try
            {
                InputStream is = getInputStream();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int data = 0;
                while ((data = is.read()) != -1)
                {
                    baos.write(data);
                }
                baos.close();

                array = baos.toByteArray();
            }
            catch (Exception ex)
            {
            	FrameworkHelper.getLogger().error(ex);
            }
        }
        return array;
    }

    public void writeBytes(byte[] array)
    {
        if (this.isFile())
        {
            try
            {
                ByteArrayInputStream bais = new ByteArrayInputStream(array);
                OutputStream os = getOutputStream();
                DataUtil.copyStream(bais, os);
            }
            catch (Exception ex)
            {
            	FrameworkHelper.getLogger().error(ex);
            }
        }
    }

    public String readContents()
    {
        byte[] array = readBytes();
        return new String(array);
    }

    public void writeContents(String contents)
    {
        byte[] array = contents.getBytes();
        writeBytes(array);
    }

    public InputStream getInputStream() throws Exception
    {
        return this.fileSystem.getInputStream(this);
    }

    public OutputStream getOutputStream() throws Exception
    {
        return this.fileSystem.getOutputStream(this);
    }

    protected IFileSystem fileSystem;
}
