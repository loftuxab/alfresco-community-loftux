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

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.alfresco.config.ConfigException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ConfigSource that looks for a prefix to determine where to look for the config.</br>
 * Valid prefixes are:
 * <ul>
 *   <li><b>file:</b> the location provided is a path to a physical file</li>
 *   <li><b>classpath:</b> the location provided is a resource on the classpath</li>
 *   <li><b>http:</b> the location provided is a HTTP address</li>
 *   <li><b>jar:</b> the location provided is within a JAR file. The location can either
 *   be a single JAR file location or a wildcard in which case all JAR files on the classpath
 *   will be searched for the given entry. NOTE: Currently only searching in the META-INF
 *   folder is supported.</li>
 * </ul>
 * The default, if none of the above is detected, is <b>classpath</b>.  An example of
 * a URL is <code>file:/home/root/settings/config.xml</code>.
 * 
 * @author Derek Hulley
 * @author gavinc
 */
public class UrlConfigSource extends BaseConfigSource
{
    public static final String PREFIX_JAR = "jar:";
    public static final String PREFIX_FILE = "file:";
    public static final String PREFIX_HTTP = "http:";
    public static final String PREFIX_CLASSPATH = "classpath:";
    
    private static final String WILDCARD = "*";
    private static final String META_INF = "META-INF";
    
    private static final Log logger = LogFactory.getLog(UrlConfigSource.class);
    
    /**
     * Constructs a config location that figures out where to look for the config
     * 
     * @param sourceLocation The location from which to get config
     * 
     * @see ClassPathConfigSource#ClassPathConfigSource(List<String>)
     */
    public UrlConfigSource(String sourceLocation)
    {
        processSourceString(sourceLocation);
    }

    /**
     * Constructs a config location that figures out where to look for the config
     * 
     * @param source List of locations from which to get the config
     */
    public UrlConfigSource(List<String> sourceLocations)
    {
        for (String location : sourceLocations)
        {
           processSourceString(location);
        }
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
            sourceString = sourceUrl;
            configSource = new HTTPConfigSource(sourceString);
        }
        else if (sourceUrl.startsWith(PREFIX_CLASSPATH))
        {
            sourceString = sourceUrl.substring(10);
            configSource = new ClassPathConfigSource(sourceString);
        }
        else if (sourceUrl.startsWith(PREFIX_JAR))
        {
            sourceString = sourceUrl;
            configSource = new JarConfigSource(sourceString);
        }
        else if (sourceUrl.indexOf(':') > -1)
        {
            throw new ConfigException("Config source cannot be determined: " + sourceString);
        }
        else
        {
            sourceString = sourceUrl;
            configSource = new ClassPathConfigSource(sourceString);
        }
        
        return configSource.getInputStream(sourceString);
    }
    
    /**
     * Processes the given source string and adds the resulting config
     * source files to the list to be parsed.
     * <p>
     * If the sourceString contains a wildcard the appropriate resolution
     * processing is performed to obtain a list of physical locations.
     * </p> 
     * 
     * @param sourceString
     */
    protected void processSourceString(String sourceString)
    {
        // the only source type we support wildcards for at the
        // moment is for JAR files
        if (sourceString != null && sourceString.startsWith(PREFIX_JAR) && 
            sourceString.indexOf(WILDCARD + JarConfigSource.JAR_PATH_SEPARATOR) != -1)
        {
            processWildcardJarSource(sourceString);
        }
        else
        {
            super.addSourceString(sourceString);
        }
    }
    
    /**
     * Processes the given JAR file pattern source. The classpath
     * will be searched for JAR files that contain files that match
     * the given pattern.
     * 
     * NOTE: Currently only files within the META-INF folder are supported
     * i.e. patterns that look like "jar:*!/META-INF/[filename]"
     * 
     * @param sourcePattern The wildcard pattern for files to find within JARs
     */
    protected void processWildcardJarSource(String sourcePattern)
    {
        String file = sourcePattern.substring(7);
       
        if (file.startsWith(META_INF) == false)
        {
            throw new UnsupportedOperationException(
                "Only JAR file wildcard searches within the META-INF folder are currently supported");
        }
        
        try
        {
            // get a list of all the JAR files that have the META-INF folder
            Enumeration urls = this.getClass().getClassLoader().getResources(META_INF);
            while (urls.hasMoreElements())
            {
                URL url = (URL)urls.nextElement();
                // only add the item if is a reference to a JAR file
                if (url.getProtocol().equals(JarConfigSource.JAR_PROTOCOL))
                {
                    URLConnection conn = url.openConnection();
                    if (conn instanceof JarURLConnection)
                    {
                        // open the jar file and see if it contains what we're looking for
                        JarURLConnection jarConn = (JarURLConnection)conn;
                        JarFile jar = ((JarURLConnection)conn).getJarFile();
                        ZipEntry entry = jar.getEntry(file);
                        if (entry != null)
                        {
                            if (logger.isInfoEnabled())
                                logger.info("Found " + file + " in " + jarConn.getJarFileURL());
                       
                            String sourceString = JarConfigSource.JAR_PROTOCOL + ":" +
                                    jarConn.getJarFileURL().toExternalForm() + 
                                    JarConfigSource.JAR_PATH_SEPARATOR + file;
                       
                            super.addSourceString(sourceString);
                        }
                        else if (logger.isDebugEnabled())
                        {
                            logger.debug("Did not find " + file + " in " + jarConn.getJarFileURL());
                        }
                    }
                }
            }
        }
        catch (IOException ioe)
        {
            if (logger.isDebugEnabled())
                logger.debug("Failed to process JAR file wildcard: " + sourcePattern, ioe);
        }
    }
}
