package com.activiti.web.config;

import com.activiti.web.config.xml.XMLConfigService;

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
   
   public static ConfigService getConfigService()
   {
      if (configService == null)
      {
         // TODO: get the absolute path for the config file
         //String configFile = "/WEB-INF/config/web-client-config.xml";
         String configFile = "w:\\sandbox\\projects\\web-client\\source\\web\\WEB-INF\\web-client-config.xml";
         
         configService = new XMLConfigService(configFile);
         configService.init();
      }
      
      return configService;
   }
}
