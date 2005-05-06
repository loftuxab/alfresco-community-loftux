package org.alfresco.web.jsf.component.data;

/**
 * @author kevinr
 */
public interface IGridDataModel
{
   /**
    * Get a row object for the specified row index
    * 
    * @param index      valid row index
    * 
    * @return row object for the specified index
    */
   public Object getRow(int index);
   
   /**
    * Return the number of rows in the data model
    * 
    * @return row count
    */
   public int size();
   
   /**
    * Sort the data set using the specified sort parameters
    * 
    * @param column        Column to sort
    * @param bAscending    True for ascending sort, false for descending
    * @param mode          Sort mode to use (see IDataContainer constants)
    */
   public void sort(String column, boolean bAscending, String mode);
}
