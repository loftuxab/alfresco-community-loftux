package jsftest.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Class to represent a basic data dictionary service
 * 
 * @author gavinc
 */
public class DataDictionary
{
   private Map m_types;
   
   public DataDictionary()
   {
      m_types = new HashMap();
      
      // setup the dictionary
      Property name = new Property("name", "string", "Name", false);
      Property desc = new Property("description", "string", "Description" , false);
      Property created = new Property("created", "datetime", "Created Date", true);
      Property modified = new Property("modified", "datetime", "Modified Date", false);
      Property keywords = new Property("keywords", "string[]", "Keywords", false);
      
      Property sopid = new Property("sopId", "string", "SOP ID", true);
      Property effective = new Property("effective", "datetime", "Effective Date", false);
      Property approved = new Property("approved", "boolean", "Approved", false);
      
      MetaData base = new MetaData("base");
      base.addProperty(name);
      base.addProperty(desc);
      base.addProperty(created);
      base.addProperty(modified);
      base.addProperty(keywords);
      
      MetaData sop = new MetaData("SOP");
      sop.setProperties(base.getProperties());
      sop.addProperty(sopid);
      sop.addProperty(effective);
      sop.addProperty(approved);
      
      m_types.put(base.getTypeName(), base);
      m_types.put(sop.getTypeName(), sop);
   }
   
   public MetaData getMetaData(String type)
   {
      return (MetaData)m_types.get(type);
   }
   
   /**
    * @return Returns the types.
    */
   public Map getTypes()
   {
      return m_types;
   }
   
   
   // *********************
   // *** Inner classes ***
   // *********************
   
   /**
    * Represents the meta data of an object
    * @author gavinc
    */
	public class MetaData
	{
	   private List m_properties;
	   private String m_typeName;
	   
	   public MetaData(String typeName)
	   {
	      m_properties = new ArrayList();
	      m_typeName = typeName;
	   }
	   
	   /**
	    * Adds a property to the meta data object
	    * 
	    * @author gavinc
	    */
	   public void addProperty(Property property)
	   {
	      m_properties.add(property);
	   }
	   
      /**
       * @return Returns the properties.
       */
      public List getProperties()
      {
         return m_properties;
      }
      
      /**
       * @param properties The properties to set.
       */
      public void setProperties(List properties)
      {
         m_properties.clear();
         
         Iterator iter = properties.iterator();
         while (iter.hasNext())
         {
            Property prop = (Property)iter.next();
            m_properties.add(prop);
         }
      }
      
      /**
       * @return Returns the typeName.
       */
      public String getTypeName()
      {
         return m_typeName;
      }
	}
	
	/**
	 * Represents a property on an object
	 * @author gavinc
	 */
	public class Property
	{
	   private String m_name;
	   private String m_type;
	   private String m_displayName;
	   private boolean m_readOnly;
	   
      /**
       * @param name
       * @param type
       * @param readOnly
       */
      public Property(String name, String type, String displayName, boolean readOnly)
      {
         m_name = name;
         m_type = type;
         m_displayName = displayName;
         m_readOnly = readOnly;
      }
      
	   /**
	    * @return Returns the name.
	    */
	   public String getName()
	   {
	      return m_name;
	   }
	   
	   /**
	    * @param name The name to set.
	    */
	   public void setName(String name)
	   {
	      m_name = name;
	   }
	   
	   /**
	    * @return Returns the type.
	    */
	   public String getType()
	   {
	      return m_type;
	   }
	   
	   /**
	    * @param type The type to set.
	    */
	   public void setType(String type)
	   {
	      m_type = type;
	   }
	   
      /**
       * @return Returns the displayName.
       */
      public String getDisplayName()
      {
         return m_displayName;
      }
      
      /**
       * @param displayName The displayName to set.
       */
      public void setDisplayName(String displayName)
      {
         m_displayName = displayName;
      }
      
	   /**
	    * @return Returns the readOnly.
	    */
	   public boolean isReadOnly()
	   {
	      return m_readOnly;
	   }
	   
	   /**
	    * @param readOnly The readOnly to set.
	    */
	   public void setReadOnly(boolean readOnly)
	   {
	      m_readOnly = readOnly;
	   }
	}
	
}