package org.alfresco.config.source;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.alfresco.config.ConfigException;

/**
 * ConfigSource implementation that gets its data via a file.
 * 
 * @author gavinc
 */
public class FileConfigSource extends BaseConfigSource
{
   /**
    * Default Constructor
    */
   public FileConfigSource()
   {
      super();
   }
   
   /**
    * Constructs an HTTPConfigSource using the list of file paths
    * 
    * @param source List of file paths to get config from
    */
   public FileConfigSource(String source)
   {
      super();
      
      this.setSource(source);
   }

   /**
    * @see org.alfresco.config.source.BaseConfigSource#getInputStream(java.lang.String)
    */
   public InputStream getInputStream(String source)
   {
      InputStream is = null;
      
      try
      {
         is = new BufferedInputStream(new FileInputStream(source));
      }
      catch (IOException ioe)
      {
         throw new ConfigException("Failed to obtain input stream to file: " + source, ioe);
      }
      
      return is;
   }
}
