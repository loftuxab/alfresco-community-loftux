/*
 * Created on Mar 14, 2005
 */
package com.activiti.web.jsf.component.data;

import java.util.List;


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
      m_data = data;
   }
   
   public Object[] getRow(int index)
   {
      return (Object[])m_data.get(index);
   }
   
   public Object getColumnForRow(int row, int column)
   {
      return ((Object[])m_data.get(row))[column];
   }
   
   // TODO: allow formatting etc. of strings
   
   public int size()
   {
      return m_data.size();
   }
   
   private List m_data = null;
}
