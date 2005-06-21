package org.alfresco.web.config;

import java.util.Iterator;

import org.alfresco.config.ConfigElement;
import org.alfresco.config.ConfigException;
import org.alfresco.config.xml.elementreader.ConfigElementReader;
import org.dom4j.Element;

/**
 * Custom element reader to parse config for server details
 * 
 * @author gavinc
 */
public class ServerElementReader implements ConfigElementReader
{
   public static final String ELEMENT_SERVER = "server";
   public static final String ELEMENT_MODE = "mode";
   public static final String ELEMENT_ERROR_PAGE = "error-page";
   public static final String ELEMENT_LOGIN_PAGE = "login-page";
   
   /**
    * @see org.alfresco.config.xml.elementreader.ConfigElementReader#parse(org.dom4j.Element)
    */
   public ConfigElement parse(Element element)
   {
      ServerConfigElement configElement = null;
      
      if (element != null)
      {
         String name = element.getName();
         if (name.equals(ELEMENT_SERVER) == false)
         {
            throw new ConfigException("ServerElementReader can only parse " +
                  ELEMENT_SERVER + "elements, " + "the element passed was '" + 
                  name + "'");
         }
         
         configElement = new ServerConfigElement();
         
         // get the server mode
         Element mode = element.element(ELEMENT_MODE);
         if (mode != null)
         {
            configElement.setMode(mode.getTextTrim());
         }
         
         // get the error page
         Element errorPage = element.element(ELEMENT_ERROR_PAGE);
         if (errorPage != null)
         {
            configElement.setErrorPage(errorPage.getTextTrim());
         }
         
         // get the login page
         Element loginPage = element.element(ELEMENT_LOGIN_PAGE);
         if (loginPage != null)
         {
            configElement.setLoginPage(loginPage.getTextTrim());
         }
      }
      
      return configElement;
   }
}
