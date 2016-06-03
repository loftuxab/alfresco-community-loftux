package org.alfresco.module.vti.metadata.model;

import org.alfresco.service.namespace.QName;


/**
 * <p>Bean class that store all meta-information about
 *  a SharePoint List Type (definition of a kind of List)</p>
 * 
 * @author Nick Burch
 */
public class ListTypeBean
{
   private final int id;
   private final int baseType;
   private final boolean isDataList;
   private final QName entryType;
   private final String name;
   private final String title;
   private final String description;
   
   public ListTypeBean(int id, int baseType, boolean isDataList, QName entryType, 
                       String name, String title, String description) 
   {
      this.id = id;
      this.baseType = baseType;
      this.isDataList = isDataList;
      this.entryType = entryType;
      this.name = name;
      this.title = title;
      this.description = description;
   }

   /**
    * Get the ID of the Type
    */
   public int getId() 
   {
      return id;
   }

   /**
    * Get the List Base Type 
    */
   public int getBaseType() 
   {
      return baseType;
   }
   
   /**
    * Is this a Data List (can have many) or
    *  a Site Component (limited to one)?
    */
   public boolean isDataList()
   {
      return isDataList;
   }

   /**
    * The Type of entries within the list
    */
   public QName getEntryType()
   {
       return entryType;
   }

   /**
    * Get the (short form) name
    */
   public String getName() 
   {
      return name;
   }

   /**
    * Get the Title (Display Name)
    */
   public String getTitle() 
   {
      return title;
   }

   /**
    * Get the Description
    */
   public String getDescription() 
   {
      return description;
   }
}
