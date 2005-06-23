/*
 * Created on 22-May-2005
 */
package org.alfresco.web.config;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.config.ConfigElement;
import org.alfresco.config.element.ConfigElementAdapter;

/**
 * @author Kevin Roast
 */
public final class MimeTypeConfigElement extends ConfigElementAdapter
{
   /**
    * Default Constructor
    */
   public MimeTypeConfigElement()
   {
      super(MimeTypesElementReader.ELEMENT_MIMETYPES);
   }
   
   /**
    * Constructor
    * 
    * @param mappings      Map of mimetype elements to use
    */
   public MimeTypeConfigElement(Map<String, String> mappings)
   {
      super(MimeTypesElementReader.ELEMENT_MIMETYPES);
      this.mimetypes = mappings;
   }

   /**
    * @see org.alfresco.config.element.ConfigElementAdapter#combine(org.alfresco.config.ConfigElement)
    */
   public ConfigElement combine(ConfigElement configElement)
   {
      MimeTypeConfigElement combined = new MimeTypeConfigElement(this.mimetypes);
      
      if (configElement instanceof MimeTypeConfigElement)
      {
         combined.mimetypes.putAll( ((MimeTypeConfigElement)configElement).mimetypes );
      }
      
      return combined;
   }
   
   /**
    * Add a mimetype extension mapping to the config element
    * 
    * @param ext        extension to map against
    * @param mimetype   mimetype content type for the specified extension
    */
   public void addMapping(String ext, String mimetype)
   {
      this.mimetypes.put(ext, mimetype);
   }
   
   /**
    * Return the mimetype for the specified extension
    * 
    * @param ext     File
    * 
    * @return mimetype content type or null if not found
    */
   public String getMimeType(String ext)
   {
      return this.mimetypes.get(ext);
   }
   
   private Map<String, String> mimetypes = new HashMap<String, String>(89, 1.0f);
}
