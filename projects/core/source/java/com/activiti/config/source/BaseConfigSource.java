package com.activiti.config.source;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.activiti.config.ConfigSource;

/**
 * Base class for ConfigSource implementations, provides support
 * for parsing comma separated sources and iterating around them
 * 
 * @author gavinc
 */
public abstract class BaseConfigSource implements ConfigSource
{
   private List sourceList = new ArrayList();
   private Iterator sources;
   
   /**
    * @see com.activiti.web.config.ConfigSource#setSource(java.lang.String)
    */
   public void setSource(String source)
   {
      if (source.indexOf(",") != -1)
      {
         StringTokenizer tokenizer = new StringTokenizer(source, ",");
         while (tokenizer.hasMoreTokens())
         {
            this.sourceList.add(tokenizer.nextToken());
         }
      }
      else
      {
         this.sourceList.add(source);
      }
      
      // create an iterator of all the sources
      this.sources = this.sourceList.iterator(); 
   }

   /**
    * @see com.activiti.web.config.ConfigSource#hasStream()
    */
   public boolean hasStream()
   {
      if (this.sources == null)
      {
         throw new IllegalStateException("A source has not been provided");
      }
      
      return this.sources.hasNext();
   }

   /**
    * @see com.activiti.web.config.ConfigSource#nextStream()
    */
   public InputStream nextStream()
   {
      String source = (String)this.sources.next();
      return getInputStream(source);
   }

   /**
    * Retrieves an InputStream to the source represented by the given identifier
    * 
    * @param source The source to get an InputStream for
    * @return The InputStream
    */
   public abstract InputStream getInputStream(String source);
}
