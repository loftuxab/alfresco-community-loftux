package com.activiti.web.data;


/**
 * IMetaData
 * 
 * Data source meta data interface
 */
public interface IMetaData
{
   /**
    * Returns an ordered list of the field names describing this data
    * 
    * @return an array of field names
    */
   public String[] getFieldNames();
   
   /**
    * Returns an ordered list of the type names describing this data
    * 
    * @return an of classes representing the result types
    */
   public Class[] getFieldTypes();
   
   /**
    * Return the index of the specified field name
    * 
    * @param field to lookup
    * 
    * @return index in dataset or -1 if not found
    */
   public int lookupFieldIndex(String field);
   
} // end interface IMetaData
