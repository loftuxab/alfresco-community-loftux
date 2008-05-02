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

import org.alfresco.service.cmr.avm.AVMNodeDescriptor;

/**
 * The Class AVMFile.
 * 
 * @author muzquiano
 */
public class AVMFile extends AbstractFileDirectory implements IFile
{
    
    /**
     * Instantiates a new aVM file.
     * 
     * @param fileSystem the file system
     * @param avmNodeDescriptor the avm node descriptor
     * @param path the path
     */
    protected AVMFile(AVMFileSystem fileSystem,
            AVMNodeDescriptor avmNodeDescriptor, String path)
    {
        super(fileSystem);

        this.avmNodeDescriptor = avmNodeDescriptor;
        this.path = path;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.filesystem.AbstractFileDirectory#getName()
     */
    public String getName()
    {
        return avmNodeDescriptor.getName();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.filesystem.AbstractFileDirectory#getPath()
     */
    public String getPath()
    {
        return this.path;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.filesystem.AbstractFileDirectory#length()
     */
    public long length()
    {
        return avmNodeDescriptor.getLength();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.filesystem.AbstractFileDirectory#getModificationDate()
     */
    public long getModificationDate()
    {
        return avmNodeDescriptor.getModDate();
    }

    /** The avm node descriptor. */
    protected AVMNodeDescriptor avmNodeDescriptor;
    
    /** The path. */
    protected String path;
}
