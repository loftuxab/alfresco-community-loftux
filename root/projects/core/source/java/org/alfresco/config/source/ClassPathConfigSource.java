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

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ConfigSource implementation that gets its data via the class path.
 * 
 * @author gavinc
 */
public class ClassPathConfigSource extends BaseConfigSource
{
    private static Log logger = LogFactory.getLog(ClassPathConfigSource.class);
    
    /**
     * Constructs a class path configuration source that uses a single file
     * 
     * @param classpath the classpath from which to get config
     * 
     * @see ClassPathConfigSource#ClassPathConfigSource(List<String>)
     */
    public ClassPathConfigSource(String classpath)
    {
        this(Collections.singletonList(classpath));
    }

    /**
     * Constructs an ClassPathConfigSource using the list of classpath elements
     * 
     * @param source List of classpath resources to get config from
     */
    public ClassPathConfigSource(List<String> sourceStrings)
    {
        super(sourceStrings);
    }

    /**
     * Retrieves an input stream for the given class path source
     * 
     * @param sourceString The class path resource to search for
     * @return The input stream
     */
    public InputStream getInputStream(String sourceString)
    {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(sourceString);

        if (is == null && logger.isDebugEnabled())
        {
            logger.debug("Failed to obtain input stream to classpath: " + sourceString);
        }

        return is;
    }
}
