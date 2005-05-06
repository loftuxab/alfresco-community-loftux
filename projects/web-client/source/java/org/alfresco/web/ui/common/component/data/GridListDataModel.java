/*
 * Created on Mar 14, 2005
 */
package org.alfresco.web.ui.common.component.data;

import java.util.List;

import org.alfresco.web.data.QuickSort;


/**
 * @author kevinr
 */
public class GridListDataModel implements IGridDataModel
{
   /**
    * Constructor
    * 
    * @param data    List of Object[] row data
    */
   public GridListDataModel(List data)
   {
      this.data = data;
   }
   
   /**
    * Get a row object for the specified row index
    * 
    * @param index      valid row index
    * 
    * @return row object for the specified index
    */
   public Object getRow(int index)
   {
      return this.data.get(index);
   }
   
   /**
    * Return the number of rows in the data model
    * 
    * @return row count
    */
   public int size()
   {
      return this.data.size();
   }
   
   /**
    * Sort the data set using the specified sort parameters
    * 
    * @param column        Column to sort
    * @param bAscending    True for ascending sort, false for descending
    * @param mode          Sort mode to use (see IDataContainer constants)
    */
   public void sort(String column, boolean bAscending, String mode)
   {
      try
      {
         QuickSort sorter = new QuickSort(this.data, column, bAscending, mode);
         sorter.sort();
      }
      catch (Exception err)
      {
         throw new RuntimeException("Failed to sort data: " + err.getMessage(), err);
      }
   }
   
   // TODO: allow formatting etc. of strings
   
   private List data = null;
}
