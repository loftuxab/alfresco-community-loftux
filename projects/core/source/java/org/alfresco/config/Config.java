package org.alfresco.config;

import java.util.Map;

/**
 * Definition of a object that represents the results of a lookup. 
 * 
 * @author gavinc
 */
public interface Config
{
   /**
    * Returns the config element with the given name
    * 
    * @param name Name of the config element to retrieve
    * @return The ConfigElement object or null if it doesn't exist
    */
   public ConfigElement getConfigElement(String name);
   
   /**
    * Returns all the config elements
    * 
    * @return All the config elements 
    */
   public Map getConfigElements();
   
   // TODO: Add more methods to this interface to allow for easier client
   //       access to the results i.e. by using an XPath expression for
   //       example?
}
