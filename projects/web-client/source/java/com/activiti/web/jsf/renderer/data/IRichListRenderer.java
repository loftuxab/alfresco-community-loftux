/*
 * Created on Mar 14, 2005
 */
package com.activiti.web.jsf.renderer.data;

import java.io.IOException;

import javax.faces.context.FacesContext;

import com.activiti.web.jsf.component.data.UIColumn;

/**
 * Contract for implementations capable of rendering the columns for a Rich List
 * component.
 * 
 * @author kevinr
 */
public interface IRichListRenderer
{
   public void renderList(FacesContext context, UIColumn[] columns)
      throws IOException;
}
