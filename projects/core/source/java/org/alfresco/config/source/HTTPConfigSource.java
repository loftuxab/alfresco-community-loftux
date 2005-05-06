package org.alfresco.config.source;

import java.io.InputStream;

/**
 * ConfigSource implementation that gets its data via HTTP.
 * 
 * @author gavinc
 */
public class HTTPConfigSource extends BaseConfigSource
{
   /**
    * Default Constructor
    */
   public HTTPConfigSource()
   {
      super();
   }
   
   /**
    * Constructs an HTTPConfigSource using the list of URLs
    * 
    * @param source List of URLs to get config from
    */
   public HTTPConfigSource(String source)
   {
      super();
      
      this.setSource(source);
   }

   /**
    * @see org.alfresco.config.source.BaseConfigSource#getInputStream(java.lang.String)
    */
   public InputStream getInputStream(String source)
   {
      throw new UnsupportedOperationException();
   }
}
