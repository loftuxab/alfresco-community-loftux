package org.alfresco.config;

/**
 * Exception thrown by the config service
 * 
 * @author gavinc
 */
public class ConfigException extends RuntimeException
{
   private static final long serialVersionUID = 3257008761007847733L;

   public ConfigException(String msg)
   {
      super(msg);
   }
   
   public ConfigException(String msg, Throwable cause)
   {
      super(msg, cause);
   }
}
