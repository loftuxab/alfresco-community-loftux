package org.alfresco.config.element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.alfresco.config.ConfigElement;

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
   }

   /**
    * @see org.alfresco.web.config.ConfigElement#getAttribute(java.lang.String)
    */
   public String getAttribute(String name)
   {
      String attr = null;
      
      if (this.attributes != null)
      {
         attr = (String)this.attributes.get(name); 
      }
      
      return attr;
   }
   
   /**
    * @see org.alfresco.web.config.ConfigElement#getAttributes()
    */
   public Map getAttributes()
   {
      return this.attributes;
   }

   /**
    * @see org.alfresco.web.config.ConfigElement#getChildren()
    */
   public List getChildren()
   {
      return this.children;
   }

   /**
    * @see org.alfresco.web.config.ConfigElement#getName()
    */
   public String getName()
   {
      return this.name;
   }

   /**
    * @see org.alfresco.web.config.ConfigElement#getValue()
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
    * @see org.alfresco.web.config.ConfigElement#hasAttribute(java.lang.String)
    */
   public boolean hasAttribute(String name)
   {
      boolean hasAttr = false;
      
      if (this.attributes != null)
      {
         hasAttr = this.attributes.containsKey(name);
      }
      
      return hasAttr;
   }

   /**
    * @see org.alfresco.web.config.ConfigElement#hasChildren()
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
    * @see org.alfresco.web.config.ConfigElement#combine(org.alfresco.web.config.ConfigElement)
    */
   public ConfigElement combine(ConfigElement configElement)
   {
      GenericConfigElement combined = new GenericConfigElement(this.name);
      combined.setValue(configElement.getValue());
      
      // add the existing attributes to the new instance
      if (this.attributes != null)
      {
         Iterator attrs = this.getAttributes().keySet().iterator();
         while (attrs.hasNext())
         {
            String attrName = (String)attrs.next();
            String attrValue = configElement.getAttribute(attrName);
            combined.addAttribute(attrName, attrValue);
         }
      }
      
      // add/combine the attributes from the given instance
      if (configElement.getAttributes() != null)
      {
         Iterator attrs = configElement.getAttributes().keySet().iterator();
         while (attrs.hasNext())
         {
            String attrName = (String)attrs.next();
            String attrValue = configElement.getAttribute(attrName);
            combined.addAttribute(attrName, attrValue);
         }
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
      if (this.attributes == null)
      {
         this.attributes = new HashMap();
      }
      
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
      StringBuilder buffer = new StringBuilder(super.toString());
      buffer.append(" (name=").append(this.name).append(")");
      return buffer.toString();
   }
}
