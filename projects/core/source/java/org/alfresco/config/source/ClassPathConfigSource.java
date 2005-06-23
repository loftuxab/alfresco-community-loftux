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

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.alfresco.config.ConfigException;

/**
 * ConfigSource implementation that gets its data via the class path.
 * 
 * @author gavinc
 */
public class ClassPathConfigSource extends BaseConfigSource
{
    /**
     * Constructs a class path configuration source that uses a single file
     * 
     * @param classpath
     *            the classpath from which to get config
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
     * @param source
     *            List of classpath resources to get config from
     */
    public ClassPathConfigSource(List<String> sourceStrings)
    {
        super(sourceStrings);
    }

    /**
     * Retrieves an input stream for the given class path source
     * 
     * @param sourceString
     *            The class path resource to search for
     * @return The input stream
     */
    public InputStream getInputStream(String sourceString)
    {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(sourceString);

        if (is == null)
        {
            throw new ConfigException("Failed to obtain input stream to classpath: " + sourceString);
        }

        return is;
    }
}
