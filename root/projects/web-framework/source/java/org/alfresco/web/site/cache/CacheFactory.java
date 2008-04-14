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
package org.alfresco.web.site.cache;

import java.io.File;

import org.alfresco.web.site.filesystem.IFileSystem;

/**
 * @author muzquiano
 */
public class CacheFactory
{
    private CacheFactory()
    {
    }

    /**
     *  Creates a basic in-memory cache
     */
    public static IContentCache createBasicCache(long timeout)
    {
        return new BasicCache(timeout);
    }

    /**
     *  Creates an ADS Object cache
     */
    public static IContentCache createADSCache(IFileSystem fileSystem,
            long timeout)
    {
        return new ModelObjectCache(fileSystem, timeout);
    }

    /**
     *  Creates a persistent disk-memory cache
     */
    public static IContentCache createPersistentCache(String cacheId,
            long timeout, String cacheDir) throws Exception
    {
        if (cacheDir == null || "".equals(cacheDir))
            throw new Exception("Null cache directory!");

        // clean up and add in the bean ID
        cacheDir = cacheDir.replace('\\', File.separatorChar);
        cacheDir = cacheDir.replace('/', File.separatorChar);
        if (!cacheDir.endsWith(File.separator))
            cacheDir = cacheDir + File.separator;

        // Check to make sure that the given cache directory is valid
        /*
         if(!isValidDirectory(cacheDir))
         throw new Exception("Invalid cache directory!");
         */

        // append the bean id and run mkdirs
        cacheDir = cacheDir + cacheId + File.separator;
        File f = new File(cacheDir);
        f.mkdirs();

        // return the cache
        IContentCache cache = new PersistentCache(timeout, cacheDir);
        return cache;
    }

    /**
     *  Creates a persistent disk-memory cache from the default properties settings.
     *  If no cache directory is available, then the assumption is that persisted
     *  caching is disabled and an exception is thrown.
     */
    public static IContentCache createPersistentCache(String cacheId,
            long timeout) throws Exception
    {
        String cacheDir = getPropertiesCacheLocation();
        if (cacheDir == null)
            throw new Exception("The cache location is invalid.");

        IContentCache cache = createPersistentCache(cacheId, timeout, cacheDir);
        cache.setReporting(isReportingEnabled());

        return cache;
    }

    /**
     *  Returns whether persistent caching is enabled
     */
    public static boolean isPersistentCache()
    {
        return (getPropertiesCacheLocation() != null);
    }

    /**
     *  Returns the cache location as in the properties.txt file.
     *  If the location is invalid, doesn't exist or empty, then NULL is returned.
     */
    private static String getPropertiesCacheLocation()
    {
        if (propertiesCacheLocation == null)
        {
            /*
             // Get the cache location from the properties file
             String cacheLocation = System.getProperty("mws_comms_service.default.proxy.cache.location");
             if(cacheLocation == null || "".equals(cacheLocation))
             return null;
             
             // Transform it into a Java-safe version and make sure it ends with '/'
             cacheLocation.replace('\\', File.separatorChar );
             cacheLocation.replace('/', File.separatorChar );
             if(!cacheLocation.endsWith( File.separator ))
             cacheLocation += File.separator;
             
             // Make sure the path exists on disk, otherwise return null
             if(!isValidDirectory( cacheLocation ))
             return null;
             
             propertiesCacheLocation = cacheLocation;
             */
        }

        // return the cache's location on disk
        return propertiesCacheLocation;
    }

    private static String propertiesCacheLocation;

    /**
     *  Returns whether reporting is enabled for the cache in the properties file
     */
    private static boolean isReportingEnabled()
    {
        /*
         String value = System.getProperty("mws_comms_service.default.proxy.cache.reporting");
         return( "true".equalsIgnoreCase(value) );
         */
        return false;
    }

    /*
     private static boolean isValidDirectory(String path)
     {
     File f = new File(path);
     return f.exists();
     }
     */
}
