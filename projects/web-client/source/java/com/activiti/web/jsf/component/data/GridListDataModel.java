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
   
   public Object getRow(int index)
   {
      return m_data.get(index);
   }
   
   // TODO: allow formatting etc. of strings
   
   public int size()
   {
      return m_data.size();
   }
   
   private List m_data = null;
}
