package org.alfresco.config.source;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;

import org.springframework.web.context.ServletContextAware;

import org.alfresco.config.ConfigException;
import org.alfresco.config.ConfigSource;

/**
 * ConfigSource implementation that gets its data via files in a web application.
 * 
 * TODO: Also deal with the source being specified as an init param i.e. param:config.files
 * 
 * @author gavinc
 */
public class WebAppConfigSource extends BaseConfigSource implements ServletContextAware
{
   private ServletContext servletCtx;
   
   /**
    * Default Constructor
    */
   public WebAppConfigSource()
   {
      super();
   }
   
   /**
    * Constructs a WebAppConfigSource using the list of file references
    * 
    * @param source List of file references to get config from
    */
   public WebAppConfigSource(String source)
   {
      super();
      
      this.setSource(source);
   }

   /**
    * @see org.springframework.web.context.ServletContextAware#setServletContext(javax.servlet.ServletContext)
    */
   public void setServletContext(ServletContext servletContext)
   {
      this.servletCtx = servletContext;
   }
   
   /**
    * @see org.alfresco.config.source.BaseConfigSource#getInputStream(java.lang.String)
    */
   public InputStream getInputStream(String source)
   {
      InputStream is = null;
      
      try
      {
         String fullPath = this.servletCtx.getRealPath(source);
         is = new BufferedInputStream(new FileInputStream(fullPath));
      }
      catch (IOException ioe)
      {
         throw new ConfigException("Failed to obtain input stream to file: " + source, ioe);
      }
      
      return is;
   }
}
