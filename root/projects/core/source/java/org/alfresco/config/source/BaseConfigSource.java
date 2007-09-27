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
package org.alfresco.config.source;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.config.ConfigDeployment;
import org.alfresco.config.ConfigException;
import org.alfresco.config.ConfigSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base class for ConfigSource implementations, provides support for parsing
 * comma separated sources and iterating around them
 * 
 * @author gavinc
 */
public abstract class BaseConfigSource implements ConfigSource
{
    private static final Log logger = LogFactory.getLog(BaseConfigSource.class);

    private List<String> sourceStrings = new ArrayList<String>();

    /**
     * Default constructor. If this contstructor is used source files
     * must be added using the addSourceString method.
     */
    protected BaseConfigSource() {}
    
    /**
     * @param sourceStrings
     *            a list of implementation-specific sources. The meaning of the
     *            source is particular to the implementation, eg. for a file config
     *            source they would be file names.
     */
    protected BaseConfigSource(List<String> sourceStrings)
    {
        for (String sourceString : sourceStrings)
        {
            addSourceString(sourceString);
        }
    }
    
    /**
     * Conditionally adds the source to the set of source strings if its
     * trimmed length is greater than 0.
     */
    protected void addSourceString(String sourceString)
    {
        if (sourceString == null || sourceString.trim().length() == 0)
        {
           throw new ConfigException("Invalid source value: " + sourceString);
        }
        
        this.sourceStrings.add(sourceString);
    }
    
    /**
     * Converts all the sources given in the constructor into a list of
     * input streams.
     * 
     * @see #getInputStream(String)
     */
    public final List<ConfigDeployment> getConfigDeployments()
    {
        // check that we have some kind of source
        int size = this.sourceStrings.size();
        if (size == 0)
        {
            throw new ConfigException("No sources provided: " + sourceStrings);
        }
        
        // build a list of input streams
        List<ConfigDeployment> configDeployments = new ArrayList<ConfigDeployment>(size);
        for (String sourceString : sourceStrings)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Retrieving input stream for source: " + sourceString);
            }
            
            InputStream is = getInputStream(sourceString);
            configDeployments.add(new ConfigDeployment(sourceString, is));
        }
        // done
        return configDeployments;
    }

    /**
     * Retrieves an InputStream to the source represented by the given
     * source location.  The meaning of the source location will depend
     * on the implementation.
     * 
     * @param sourceString the source location
     * @return Returns an InputStream to the named source location
     */
    protected abstract InputStream getInputStream(String sourceString);
}
