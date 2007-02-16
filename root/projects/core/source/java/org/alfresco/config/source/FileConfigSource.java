/*
 * Copyright (C) 2005 Alfresco, Inc.
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
package org.alfresco.config.source;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ConfigSource implementation that gets its data via a file or files.
 * 
 * @author gavinc
 */
public class FileConfigSource extends BaseConfigSource
{
    private static Log logger = LogFactory.getLog(FileConfigSource.class);
    
    /**
     * Constructs a file configuration source that uses a single file
     * 
     * @param filename the name of the file from which to get config
     * 
     * @see FileConfigSource#FileConfigSource(List<String>)
     */
    public FileConfigSource(String filename)
    {
        this(Collections.singletonList(filename));
    }
    
    /**
     * @param sources
     *            List of file paths to get config from
     */
    public FileConfigSource(List<String> sourceStrings)
    {
        super(sourceStrings);
    }

    /**
     * @param sourceString
     *            a valid filename as accepted by the
     *            {@link java.io.File#File(java.lang.String) file constructor}
     * @return Returns a stream onto the file
     */
    protected InputStream getInputStream(String sourceString)
    {
        InputStream is = null;

        try
        {
            is = new BufferedInputStream(new FileInputStream(sourceString));
        }
        catch (IOException ioe)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Failed to obtain input stream to file: " + sourceString, ioe);
            }
        }

        return is;
    }
}
