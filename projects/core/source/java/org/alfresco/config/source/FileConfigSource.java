/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.config.source;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.alfresco.config.ConfigException;

/**
 * ConfigSource implementation that gets its data via a file or files.
 * 
 * @author gavinc
 */
public class FileConfigSource extends BaseConfigSource
{
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
            throw new ConfigException("Failed to obtain input stream to file: " +
                    sourceString, ioe);
        }

        return is;
    }
}
