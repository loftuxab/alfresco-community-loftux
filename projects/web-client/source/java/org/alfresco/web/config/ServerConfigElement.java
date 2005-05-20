package org.alfresco.web.config;

import org.alfresco.config.ConfigElement;
import org.alfresco.config.element.ConfigElementAdapter;

/**
 * Custom config element that represents the config data for the server
 * 
 * @author gavinc
 */
public class ServerConfigElement extends ConfigElementAdapter
{
   private String mode;
   private String errorPage;
   
   /**
    * Default constructor
    */
   public ServerConfigElement()
   {
      super("server");
   }
   
   /**
    * Constructor
    * 
    * @param name Name of the element this config element represents
    */
   public ServerConfigElement(String name)
   {
      super(name);
   }
   
   public ConfigElement combine(ConfigElement configElement)
   {
      // NOTE: combining these would simply override the values so we just need
      //       to return a new instance of the given config element
      
      ServerConfigElement combined = new ServerConfigElement();
      combined.setMode(((ServerConfigElement)configElement).getMode());
      combined.setErrorPage(((ServerConfigElement)configElement).getErrorPage());
      return combined;
   }

   /**
    * @return The mode the server should run in, either <code>portlet</code>
    *         or <code>servlet</code>
    */
   public String getMode()
   {
      return this.mode;
   }
   
   /**
    * @param mode Sets the mode
    */
   public void setMode(String mode)
   {
      this.mode = mode;
   }
   
   /**
    * @return The error page the application should use
    */
   public String getErrorPage()
   {
      return this.errorPage;
   }

   /**
    * @param errorPage Sets the error page
    */
   public void setErrorPage(String errorPage)
   {
      this.errorPage = errorPage;
   }   
}
