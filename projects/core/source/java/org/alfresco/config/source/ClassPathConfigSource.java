package org.alfresco.config.source;

import java.io.InputStream;

/**
 * ConfigSource implementation that gets its data via the class path.
 * 
 * @author gavinc
 */
public class ClassPathConfigSource extends BaseConfigSource
{
   /**
    * Default Constructor
    */
   public ClassPathConfigSource()
   {
      super();
   }
   
   /**
    * Constructs an ClassPathConfigSource using the list of files
    * 
    * @param source List of files to get config from
    */
   public ClassPathConfigSource(String source)
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
