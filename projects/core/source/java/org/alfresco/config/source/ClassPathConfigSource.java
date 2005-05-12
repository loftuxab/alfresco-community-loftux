package org.alfresco.config.source;

import java.io.InputStream;
import java.util.List;

/**
 * ConfigSource implementation that gets its data via the class path.
 * 
 * @author gavinc
 */
public class ClassPathConfigSource extends BaseConfigSource
{
    /**
     * Constructs an ClassPathConfigSource using the list of classpath
     * elements
     * 
     * @param source List of classpath resources to get config from
     */
   public ClassPathConfigSource(List<String> sourceStrings)
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
