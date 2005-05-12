package org.alfresco.config.source;

import java.io.InputStream;
import java.util.List;

/**
 * ConfigSource implementation that gets its data via HTTP.
 * 
 * @author gavinc
 */
public class HTTPConfigSource extends BaseConfigSource
{
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
    * Not implemented
    */
   public InputStream getInputStream(String sourceString)
   {
      throw new UnsupportedOperationException();
   }
}
