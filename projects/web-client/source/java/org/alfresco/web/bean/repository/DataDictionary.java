package org.alfresco.web.bean.repository;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

/**
 * Lighweight client side representation of the repository data dictionary. 
 * This allows service calls to be kept to a minimum and for bean access, thus enabling JSF
 * value binding expressions.
 * 
 * @author gavinc
 */
public class DataDictionary
{
   private DictionaryService dictionaryService;
   private Map<QName, ClassDefinition> types = new HashMap<QName, ClassDefinition>(6, 1.0f);

   /**
    * Constructor
    * 
    * @param dictionaryService The dictionary service to use to retrieve the data 
    */
   public DataDictionary(DictionaryService dictionaryService)
   {
      this.dictionaryService = dictionaryService;
   }
   
   /**
    * @param type The ClassRef of the type to retrive data for
    * @return The class definition for the requested type
    */
   public ClassDefinition getType(QName type)
   {
      ClassDefinition classDef = types.get(type);
      
      if (classDef == null)
      {
         classDef = this.dictionaryService.getClass(type);
         
         if (classDef != null)
         {
            types.put(type, classDef);
         }
      }
      
      return classDef;
   }
   
   /**
    * @param node The node from which to get the property
    * @param property The property to find the definition for
    * @return The property definition
    */
   public PropertyDefinition getPropertyDefinition(Node node, String property)
   {
      // TODO: we need to deal with namespaces, for now presume it is the alfresco namespace
      
      PropertyDefinition propDef = null;
      
      ClassDefinition classDef = getType(node.getType());
      
      if (classDef != null)
      {
         Map<QName, PropertyDefinition> properties = classDef.getProperties();
         propDef = properties.get(QName.createQName(NamespaceService.ALFRESCO_URI, property));
      }
      
      return propDef;
   }
}
