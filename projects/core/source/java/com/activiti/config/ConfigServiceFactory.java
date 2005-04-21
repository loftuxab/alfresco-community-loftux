package com.activiti.config;

import java.util.StringTokenizer;

import javax.servlet.ServletContext;

import com.activiti.config.source.FileConfigSource;
import com.activiti.config.xml.XMLConfigService;

/**
 * Factory to return ConfigService implementations
 * 
 * NOTE: This factory will be replaced with Spring soon
 * 
 * @author gavinc
 */
public class ConfigServiceFactory
{
   private static ConfigService configService;
   
   public static ConfigService getConfigService(ServletContext servletContext)
   {
      if (configService == null)
      {
         String rawSources = servletContext.getInitParameter("config.files");
         StringBuffer sources = new StringBuffer();
         
         StringTokenizer tokenizer = new StringTokenizer(rawSources, ",");
         boolean first = true;
         while (tokenizer.hasMoreTokens())
         {
            if (first == false)
            {
               sources.append(",");
            }
            
            String source = tokenizer.nextToken();
            if (source.startsWith("/WEB-INF/"))
            {
               String fullPath = servletContext.getRealPath(source);
               sources.append(fullPath);
            }
            else
            {
               sources.append(source);
            }
         }
         
         ConfigSource configSource = new FileConfigSource(sources.toString());
         configService = new XMLConfigService();
         configService.setConfigSource(configSource);
         configService.init();
      }
      
      return configService;
   }
   
   public static ConfigService getConfigService()
   {
      if (configService == null)
      {
         throw new IllegalStateException("getServletContext(ServletContext) must be called first");
      }
      
      return configService;
   }
}
