package org.alfresco.web.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.alfresco.config.ConfigElement;
import org.alfresco.config.element.ConfigElementAdapter;
import org.alfresco.config.element.GenericConfigElement;

/**
 * Custom config element that represents the config data for a property sheet
 * 
 * @author gavinc
 */
public class PropertySheetConfigElement extends ConfigElementAdapter
{
   // TODO: Currently this object just deals with properties to show, in the
   //       future it will also deal with properties to hide.
   
   private List<PropertyConfig> properties = new ArrayList<PropertyConfig>();
   private Map<String, PropertyConfig> propertiesMap = new HashMap<String, PropertyConfig>();
   private List<String> propertyNames = new ArrayList<String>();
   private boolean kidsPopulated = false;
   
   /**
    * Default constructor
    */
   public PropertySheetConfigElement()
   {
      super("property-sheet");
   }
   
   /**
    * Constructor
    * 
    * @param name Name of the element this config element represents
    */
   public PropertySheetConfigElement(String name)
   {
      super(name);
   }
   
   /**
    * @see org.alfresco.config.ConfigElement#getChildren()
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
            Iterator<PropertyConfig> props = this.properties.iterator();
            while (props.hasNext())
            {
               PropertyConfig pc = props.next();
               GenericConfigElement ce = new GenericConfigElement(PropertySheetElementReader.ELEMENT_SHOW_PROPERTY);
               ce.addAttribute(PropertySheetElementReader.ATTR_NAME, pc.getName());
               ce.addAttribute(PropertySheetElementReader.ATTR_DISPLAY_LABEL, pc.getDisplayLabel());
               ce.addAttribute(PropertySheetElementReader.ATTR_READ_ONLY, Boolean.toString(pc.isReadOnly()));
               ce.addAttribute(PropertySheetElementReader.ATTR_CONVERTER, pc.getConverter());
               this.children.add(ce);
            }
            
            this.kidsPopulated = true;
         }
         
         kids = super.getChildren();
      }
      
      return kids;
   }
   
   /**
    * @see org.alfresco.config.ConfigElement#combine(org.alfresco.config.ConfigElement)
    */
   public ConfigElement combine(ConfigElement configElement)
   {
      PropertySheetConfigElement combined = new PropertySheetConfigElement();
      
      // add all the existing properties
      Iterator<PropertyConfig> props = this.getPropertiesToShow().iterator();
      while (props.hasNext())
      {
         combined.addProperty(props.next());
      }
      
      // add all the properties from the given element
      props = ((PropertySheetConfigElement)configElement).getPropertiesToShow().iterator();
      while (props.hasNext())
      {
         combined.addProperty(props.next());
      }
      
      return combined;
   }
   
   /**
    * Adds a property to show
    * 
    * @param propertyConfig A pre-configured property config object
    */
   public void addProperty(PropertyConfig propertyConfig)
   {
      if (this.propertiesMap.containsKey(propertyConfig.getName()) == false)
      {
         this.properties.add(propertyConfig);
         this.propertiesMap.put(propertyConfig.getName(), propertyConfig);
         this.propertyNames.add(propertyConfig.getName());
      }
   }
   
   /**
    * Adds a property to show
    * 
    * @param name The name of the property
    * @param displayLabel Display label to use for the property
    * @param readOnly Sets whether the property should be rendered as read only
    * @param converter The name of a converter to apply to the property control
    */
   public void addProperty(String name, String displayLabel, String readOnly, String converter)
   {
      addProperty(new PropertyConfig(name, displayLabel, Boolean.parseBoolean(readOnly), converter));
   }
   
   /**
    * @return Returns the list
    */
   public List<String> getPropertyNamesToShow()
   {
      return this.propertyNames;
   }
   
   /**
    * @return Returns the list of property config objects that represent those to display
    */
   public List<PropertyConfig> getPropertiesToShow()
   {
      return this.properties;
   }
   
   /**
    * @return Returns a map of the property names to show
    */
   public Map<String, PropertyConfig> getPropertiesMapToShow()
   {
      return this.propertiesMap;
   }
   
   /**
    * Inner class to represent a configured property 
    */
   public class PropertyConfig
   {
      private String name;
      private String displayLabel;
      private String converter;
      private boolean readOnly;
      
      public PropertyConfig(String name, String displayLabel, boolean readOnly, String converter)
      {
         this.name = name;
         this.displayLabel = displayLabel;
         this.readOnly = readOnly;
         this.converter = converter;
      }
      
      /**
       * @return The display label
       */
      public String getDisplayLabel()
      {
         return this.displayLabel;
      }
      
      /**
       * @return The property name
       */
      public String getName()
      {
         return this.name;
      }
      
      /**
       * @return Determines whether the property is configured as read only
       */
      public boolean isReadOnly()
      {
         return this.readOnly;
      }
      
      public String getConverter()
      {
         return this.converter;
      }
      
      /**
       * @see java.lang.Object#toString()
       */
      public String toString()
      {
         StringBuilder buffer = new StringBuilder(super.toString());
         buffer.append(" (name=").append(this.name);
         buffer.append(" displaylabel=").append(this.displayLabel);
         buffer.append(" converter=").append(this.converter);
         buffer.append(" readonly=").append(this.readOnly).append(")");
         return buffer.toString();
      }
   }
}
