/*
 * Created on Mar 14, 2005
 */
package com.activiti.web.jsf.component.data;



/**
 * @author kevinr
 */
public class GridArrayDataModel implements IGridDataModel
{
   /**
    * Constructor
    * 
    * @param data    Array of Object (beans) row data 
    */
   public GridArrayDataModel(Object[] data)
   {
      m_data = data;
   }
   
   public Object getRow(int index)
   {
      return m_data[index];
   }
   
   // TODO: allow formatting etc. of strings
   
   public int size()
   {
      return m_data.length;
   }
   
   private Object[] m_data = null;
}
