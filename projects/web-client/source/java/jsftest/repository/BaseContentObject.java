package jsftest.repository;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents an exampe domain object we may use in the future
 * 
 * @author gavinc
 */
public class BaseContentObject implements Serializable
{
   private String m_name;
   private Date m_created = new Date();
   private Date m_modified = new Date();
   private String m_description;
   private String[] m_keywords;
   
   /**
    * @return The type name of this object
    */
   public String getType()
   {
      return "base";
   }
   
   /**
    * @return Returns the keywords.
    */
   public String[] getKeywords()
   {
      return m_keywords;
   }
   
   /**
    * @param keywords The keywords to set.
    */
   public void setKeywords(String[] keywords)
   {
      m_keywords = keywords;
   }
   
   /**
    * @return Returns the created.
    */
   public Date getCreated()
   {
      return m_created;
   }
   
   /**
    * @param created The created to set.
    */
   public void setCreated(Date created)
   {
      m_created = created;
   }
   
   /**
    * @return Returns the description.
    */
   public String getDescription()
   {
      return m_description;
   }
   
   /**
    * @param description The description to set.
    */
   public void setDescription(String description)
   {
      m_description = description;
   }
   
   /**
    * @return Returns the modified.
    */
   public Date getModified()
   {
      return m_modified;
   }
   
   /**
    * @param modified The modified to set.
    */
   public void setModified(Date modified)
   {
      m_modified = modified;
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
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();
      
      buffer.append(super.toString());
      buffer.append("; Name: ").append(m_name);
      buffer.append("; Type: ").append(getType());
      buffer.append("; Description: ").append(m_description);
      buffer.append("; Created: ").append(m_created);
      buffer.append("; Modified: ").append(m_modified);
      buffer.append("; Keywords: ");
      if (m_keywords != null)
      {
         for (int x = 0; x < m_keywords.length; x++)
         {
            buffer.append(m_keywords[x]).append(" ");
         }
      }
      
      return buffer.toString();
   }
}
