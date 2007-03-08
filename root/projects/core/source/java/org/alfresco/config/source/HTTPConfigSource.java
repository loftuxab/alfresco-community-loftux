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

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ConfigSource implementation that gets its data via HTTP.
 * 
 * @author gavinc
 */
public class HTTPConfigSource extends BaseConfigSource
{
    private static Log logger = LogFactory.getLog(HTTPConfigSource.class);
   
    /**
     * Constructs an HTTP configuration source that uses a single URL
     * 
     * @param url the url of the file from which to get config
     * 
     * @see HTTPConfigSource#HTTPConfigSource(java.util.List)
     */
    public HTTPConfigSource(String url)
    {
        this(Collections.singletonList(url));
    }
    
   /**
    * Constructs an HTTPConfigSource using the list of URLs
    * 
    * @param sourceStrings List of URLs to get config from
    */
   public HTTPConfigSource(List<String> sourceStrings)
   {
      super(sourceStrings);
   }

   /**
    * Retrieves an input stream over HTTP for the given URL
    * 
    * @param sourceString URL to retrieve config data from
    * @return The input stream
    */
   public InputStream getInputStream(String sourceString)
   {
      InputStream is = null;
      
      try
      {
         URL url = new URL(sourceString);
         is = new BufferedInputStream(url.openStream());
      }
      catch (Throwable e)
      {
          if (logger.isDebugEnabled())
          {
              logger.debug("Failed to obtain input stream to URL: " + sourceString, e);
          }
      }
      
      return is;
   }
}
