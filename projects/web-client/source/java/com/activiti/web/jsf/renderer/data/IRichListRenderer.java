/*
 * Created on Mar 14, 2005
 */
package com.activiti.web.jsf.renderer.data;

import java.io.IOException;

import javax.faces.context.FacesContext;

import com.activiti.web.jsf.component.data.UIColumn;
import com.activiti.web.jsf.component.data.UIRichList;

/**
 * Contract for implementations capable of rendering the columns for a Rich List
 * component.
 * 
 * @author kevinr
 */
public interface IRichListRenderer
{
   public void renderListBefore(FacesContext context, UIRichList richList, UIColumn[] columns)
      throws IOException;
   
   public void renderListRow(FacesContext context, UIRichList richList, UIColumn[] columns, Object row)
      throws IOException;
   
   public void renderListAfter(FacesContext context, UIRichList richList, UIColumn[] columns)
      throws IOException;
}
