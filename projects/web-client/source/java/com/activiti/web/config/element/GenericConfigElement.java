package com.activiti.web.config.element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.activiti.web.config.ConfigElement;

/**
 * Implementation of a generic configuration element. This class
 * can handle the representation of any config element in a 
 * generic manner. 
 * 
 * @author gavinc
 */
public class GenericConfigElement implements ConfigElement
{
   private String name;
   private String value;
   private Map attributes;
   private List children;
   
   /**
    * Default constructor
    * 
    * @param name Name of the config element
    */
   public GenericConfigElement(String name)
   {
      this.name = name;
      this.attributes = new HashMap();
   }

   /**
    * @see com.activiti.web.config.ConfigElement#getAttribute(java.lang.String)
    */
   public String getAttribute(String name)
   {
      return (String)this.attributes.get(name);
   }
   
   /**
    * @see com.activiti.web.config.ConfigElement#getAttributes()
    */
   public Map getAttributes()
   {
      return this.attributes;
   }

   /**
    * @see com.activiti.web.config.ConfigElement#getChildren()
    */
   public List getChildren()
   {
      return this.children;
   }

   /**
    * @see com.activiti.web.config.ConfigElement#getName()
    */
   public String getName()
   {
      return this.name;
   }

   /**
    * @see com.activiti.web.config.ConfigElement#getValue()
    */
   public String getValue()
   {
      return this.value;
   }

   /**
    * Sets the value of this config element
    * 
    * @param value The value to set.
    */
   public void setValue(String value)
   {
      this.value = value;
   }

   /**
    * @see com.activiti.web.config.ConfigElement#hasAttrbiute(java.lang.String)
    */
   public boolean hasAttrbiute(String name)
   {
      return this.attributes.containsKey(name);
   }

   /**
    * @see com.activiti.web.config.ConfigElement#hasChildren()
    */
   public boolean hasChildren()
   {
      boolean hasKids = false;
      
      if (this.children != null)
      {
         hasKids = this.children.isEmpty();
      }
      
      return hasKids;
   }
   
   /**
    * @see com.activiti.web.config.ConfigElement#combine(com.activiti.web.config.ConfigElement)
    */
   public ConfigElement combine(ConfigElement configElement)
   {
      GenericConfigElement combined = new GenericConfigElement(this.name);
      combined.setValue(configElement.getValue());
      
      // add the existing attributes to the new instance
      Iterator attrs = this.getAttributes().keySet().iterator();
      while (attrs.hasNext())
      {
         String attrName = (String)attrs.next();
         String attrValue = configElement.getAttribute(attrName);
         combined.addAttribute(attrName, attrValue);
      }
      
      // add/combine the attributes from the given instance
      attrs = configElement.getAttributes().keySet().iterator();
      while (attrs.hasNext())
      {
         String attrName = (String)attrs.next();
         String attrValue = configElement.getAttribute(attrName);
         combined.addAttribute(attrName, attrValue);
      }
      
      // add the existing children to the new instance
      List kids = this.getChildren();
      if (kids != null)
      {
         for (int x = 0; x < kids.size(); x++)
         {
            ConfigElement ce = (ConfigElement)kids.get(x);
            combined.addChild(ce);
         }
      }
      
      // add the children from the given instance
      kids = configElement.getChildren();
      if (kids != null)
      {
         for (int x = 0; x < kids.size(); x++)
         {
            ConfigElement ce = (ConfigElement)kids.get(x);
            combined.addChild(ce);
         }
      }
      
      return combined;
   }
   
   /**
    * Adds the attribute with the given name and value
    * 
    * @param name Name of the attribute
    * @param value Value of the attribute
    */
   public void addAttribute(String name, String value)
   {
      this.attributes.put(name, value);
   }
   
   /**
    * Adds the given config element as a child of this element
    * 
    * @param configElement The child config element
    */
   public void addChild(ConfigElement configElement)
   {
      if (this.children == null)
      {
         this.children = new ArrayList();
      }
      
      this.children.add(configElement);
   }
   
   /**
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
      StringBuffer buffer = new StringBuffer(super.toString());
      buffer.append(" (name=").append(this.name).append(")");
      return buffer.toString();
   }
}
