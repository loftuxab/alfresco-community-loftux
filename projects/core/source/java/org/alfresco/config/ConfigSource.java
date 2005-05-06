package org.alfresco.config;

import java.io.InputStream;

/**
 * Definition of a configuration source
 * 
 * @author gavinc
 */
public interface ConfigSource
{
   /**
    * Sets the source of all the configuration data.
    * 
    * This maybe a single file path, a comma separated list of URLs, a query etc.
    * 
    * @param source The source of the data 
    */
   public void setSource(String source);
   
   /**
    * Determines whether the config source has another config stream available
    * 
    * @return true if there is a stream available
    */
   public boolean hasStream();
   
   /**
    * Returns the next available config stream
    * 
    * @return The next available stream
    */
   public InputStream nextStream();
}
