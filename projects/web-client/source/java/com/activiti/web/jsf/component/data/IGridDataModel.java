/*
 * Created on Mar 14, 2005
 */
package com.activiti.web.jsf.component.data;

import java.util.List;


/**
 * @author kevinr
 */
public interface IGridDataModel
{
   public Object getRow(int index);
   
   public int size();
}
