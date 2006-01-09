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
import java.util.Collections;
import java.util.List;

import org.alfresco.config.ConfigException;

/**
 * ConfigSource that looks for a prefix to determine where to look for the config.</br>
 * Valid prefixes are:
 * <ul>
 *   <li><b>file:</b> the location provided is a path to a physical file</li>
 *   <li><b>classpath:</b> the location provided is a resource on the classpath</li>
 *   <li><b>http:</b> the location provided is a HTTP address</li>
 * </ul>
 * The default, if none of the above is detected, is <b>classpath</b>.  An example of
 * a URL is <code>file:/home/root/settings/config.xml</code>.
 * 
 * @author Derek Hulley
 */
public class UrlConfigSource extends BaseConfigSource
{
    public static final String PREFIX_FILE = "file:";
    public static final String PREFIX_HTTP = "http:";
    public static final String PREFIX_CLASSPATH = "classpath:";
    
    /**
     * Constructs a config location that figures out where to look for the config
     * 
     * @param sourceLocation
     *            the location from which to get config
     * 
     * @see ClassPathConfigSource#ClassPathConfigSource(List<String>)
     */
    public UrlConfigSource(String sourceLocation)
    {
        this(Collections.singletonList(sourceLocation));
    }

    /**
     * Constructs a config location that figures out where to look for the config
     * 
     * @param source
     *            List of locations from which to get the config
     */
    public UrlConfigSource(List<String> sourceLocations)
    {
        super(sourceLocations);
    }

    public InputStream getInputStream(String sourceUrl)
    {
        // determine the config source
        BaseConfigSource configSource = null;
        String sourceString = null;
        if (sourceUrl.startsWith(PREFIX_FILE))
        {
            sourceString = sourceUrl.substring(5);
            configSource = new FileConfigSource(sourceString);
        }
        else if (sourceUrl.startsWith(PREFIX_HTTP))
        {
            sourceString = sourceUrl.substring(5);
            configSource = new HTTPConfigSource(sourceString);
        }
        else if (sourceUrl.startsWith(PREFIX_CLASSPATH))
        {
            sourceString = sourceUrl.substring(10);
            configSource = new ClassPathConfigSource(sourceString);
        }
        else if (sourceUrl.indexOf(':') > -1)
        {
            throw new ConfigException("Config source cannot be determined: " + sourceString);
        }
        else
        {
            configSource = new ClassPathConfigSource(sourceString);
        }
        
        return configSource.getInputStream(sourceString);
    }
}
