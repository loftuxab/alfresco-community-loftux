package org.alfresco.config.element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.alfresco.config.ConfigElement;

/**
 * Implementation of a config element that represents properties configuration
 * 
 * @author gavinc
 */
public class PropertiesConfigElement extends GenericConfigElement
{
   private List<String> properties = new ArrayList<String>();
   private boolean kidsPopulated = false;
   
   /**
    * Default constructor
    */
   public PropertiesConfigElement()
   {
      super("properties");
   }
   
   /**
    * @see org.alfresco.config.element.GenericConfigElement#getChildren()
    */
   public List<ConfigElement> getChildren()
   {
      // lazily build the list of generic config elements representing
      // the properties as the caller may not even call this method
      
      List<ConfigElement> kids = null;
      
      if (this.properties.size() > 0)
      {
         if (this.kidsPopulated == false)
         {
            Iterator props = this.properties.iterator();
            while (props.hasNext())
            {
               GenericConfigElement ce = new GenericConfigElement("property");
               ce.addAttribute("name", (String)props.next());
               addChild(ce);
            }
            
            this.kidsPopulated = true;
         }
         
         kids = super.getChildren();
      }
      
      return kids;
   }

   /**
    * @see org.alfresco.web.config.ConfigElement#combine(org.alfresco.web.config.ConfigElement)
    */
   public ConfigElement combine(ConfigElement configElement)
   {
      PropertiesConfigElement combined = new PropertiesConfigElement();
      
      // add all the existing properties
      Iterator props = this.getProperties().iterator();
      while (props.hasNext())
      {
         combined.addProperty((String)props.next());
      }
      
      // add all the properties from the given element
      props = ((PropertiesConfigElement)configElement).getProperties().iterator();
      while (props.hasNext())
      {
         combined.addProperty((String)props.next());
      }
      
      return combined;
   }
   
   /**
    * Returns the list of property names
    * 
    * @return List of property names
    */
   public List getProperties()
   {
      return this.properties;
   }
   
   /**
    * Adds the given property name to the list if it is not already present
    *  
    * @param name Name of the property to add
    */
   public void addProperty(String name)
   {
      if (this.properties.contains(name) == false)
      {
         this.properties.add(name);
      }
   }
}
