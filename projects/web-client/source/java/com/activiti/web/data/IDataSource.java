package com.activiti.web.data;

import java.util.List;

/**
 * IDataSource.java
 * 
 * Generic Data Source Interface
 */
public interface IDataSource
{
   /**
    * Called before the datasource is accessed for the first time
    */
   public void init();
   
   /**
    * Get the total count of rows available
    * 
    * @return the total row count
    */
   public int getRowCount();
   
   /**
    * Get the row data at the specified index
    * 
    * @return the row data as an array of Object values or null if not found
    */
   public Object[] getRow(int row);
   
   /**
    * Get all available data as a list
    * 
    * @return data
    */
   public List getData();
   
   /**
    * Return the meta data describing this data
    * 
    * @return the IMetaData interface describing this data
    */
   public IMetaData getMetaData();
   
   /**
    * Sorts the dataset.
    * 
    * @param column                 the column name to sort by
    * @param forward                true for a forward sort, false otherwise
    * @param mode                   IDataHandler sorting mode constant
    */
   public void sort(int column, boolean forward, int mode);
   
} // end interface IDataSource
