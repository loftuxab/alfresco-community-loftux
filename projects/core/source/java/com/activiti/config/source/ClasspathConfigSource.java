package com.activiti.config.source;

import java.io.InputStream;

/**
 * ConfigSource implementation that gets its data via XML files on the classpath
 * 
 * @author gavinc
 */
public class ClasspathConfigSource extends BaseConfigSource
{
   /**
    * Default Constructor
    */
   public ClasspathConfigSource()
   {
      super();
   }
   
   /**
    * Constructs an ClasspathConfigSource using the list of classpath references
    * 
    * @param source List of classpath references to get config from
    */
   public ClasspathConfigSource(String source)
   {
      super();
      
      this.setSource(source);
   }

   /**
    * @see com.activiti.config.source.BaseConfigSource#getInputStream(java.lang.String)
    */
   public InputStream getInputStream(String source)
   {
      throw new UnsupportedOperationException();
   }
}
