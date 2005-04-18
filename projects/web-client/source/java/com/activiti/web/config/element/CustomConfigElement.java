package com.activiti.web.config.element;

import java.util.List;
import java.util.Map;

import com.activiti.web.config.ConfigElement;


/**
 * Base class implementation of a custom config element
 * 
 * @author gavinc
 */
public abstract class CustomConfigElement implements ConfigElement
{
   private String name;
   
   /**
    * Default constructor
    * 
    * @param name Name of the element this object represents
    */
   public CustomConfigElement(String name)
   {
      this.name = name;
   }

   /**
    * @see com.activiti.web.config.ConfigElement#getName()
    */
   public String getName()
   {
      return this.name;
   }

   /**
    * @see com.activiti.web.config.ConfigElement#getAttribute(java.lang.String)
    */
   public String getAttribute(String name)
   {
      throw new UnsupportedOperationException("Use the implementation specific methods");
   }

   /**
    * @see com.activiti.web.config.ConfigElement#getAttributes()
    */
   public Map getAttributes()
   {
      throw new UnsupportedOperationException("Use the implementation specific methods");
   }

   /**
    * @see com.activiti.web.config.ConfigElement#hasAttrbiute(java.lang.String)
    */
   public boolean hasAttrbiute(String name)
   {
      throw new UnsupportedOperationException("Use the implementation specific methods");
   }

   /**
    * @see com.activiti.web.config.ConfigElement#getValue()
    */
   public String getValue()
   {
      throw new UnsupportedOperationException("Use the implementation specific methods");
   }

   /**
    * @see com.activiti.web.config.ConfigElement#getChildren()
    */
   public List getChildren()
   {
      throw new UnsupportedOperationException("Use the implementation specific methods");
   }

   /**
    * @see com.activiti.web.config.ConfigElement#hasChildren()
    */
   public boolean hasChildren()
   {
      throw new UnsupportedOperationException("Use the implementation specific methods");
   }

   /**
    * @see com.activiti.web.config.ConfigElement#combine(com.activiti.web.config.ConfigElement)
    */
   public abstract ConfigElement combine(ConfigElement configElement);
}
