/*
 * Created on Mar 14, 2005
 */
package com.activiti.web.jsf.component.data;

import java.util.List;


/**
 * @author kevinr
 */
public class GridArrayDataModel implements IGridDataModel
{
   /**
    * Constructor
    * 
    * @param data    Array of Object[] row data
    */
   public GridArrayDataModel(Object[] data)
   {
      m_data = data;
   }
   
   public Object[] getRow(int index)
   {
      return (Object[])m_data[index];
   }
   
   public Object getColumnForRow(int row, int column)
   {
      return ((Object[])m_data[row])[column];
   }
   
   // TODO: allow formatting etc. of strings
   
   public int size()
   {
      return m_data.length;
   }
   
   private Object[] m_data = null;
}
