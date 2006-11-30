/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.config.source;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    private List<String> sourceStrings;

    /**
     * @param sourceStrings
     *            a list of implementation-specific sources. The meaning of the
     *            source is particular to the implementation, eg. for a file config
     *            source they would be file names.
     */
    protected BaseConfigSource(List<String> sourceStrings)
    {
        this.sourceStrings = new ArrayList<String>();
        for (String sourceString : sourceStrings)
        {
            if (sourceString == null || sourceString.trim().length() == 0)
            {
                throw new ConfigException("Invalid source value: " + sourceString);
            }
            addSourceString(sourceString);
        }
        // check that we have some kind of source
        if (sourceStrings.size() == 0)
        {
            throw new ConfigException("No sources provided: " + sourceStrings);
        }
    }
    
    /**
     * Conditionally adds the source to the set of source strings if its
     * trimmed length is greater than 0.
     */
    private void addSourceString(String sourceString)
    {
        sourceString = sourceString.trim();
        if (sourceString.length() > 0)
        {
            sourceStrings.add(sourceString);
        }
    }
    
    /**
     * Converts all the sources given in the constructor into a list of
     * input streams.
     * 
     * @see #getInputStream(String)
     */
    public final Iterator<InputStream> iterator()
    {
        // build a list of input streams
        List<InputStream> inputStreams = new ArrayList<InputStream>(sourceStrings.size());
        for (String sourceString : sourceStrings)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Retrieving input stream for source: " + sourceString);
            }
            
            InputStream is = getInputStream(sourceString);
            if (is != null)
            {
               inputStreams.add(is);
            }
        }
        // done
        return inputStreams.iterator();
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
