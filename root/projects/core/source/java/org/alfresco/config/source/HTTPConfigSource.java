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
     * @see HTTPConfigSource#HTTPConfigSource(List<String>)
     */
    public HTTPConfigSource(String url)
    {
        this(Collections.singletonList(url));
    }
    
   /**
    * Constructs an HTTPConfigSource using the list of URLs
    * 
    * @param source List of URLs to get config from
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
