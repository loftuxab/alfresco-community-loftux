/*
 * Created on Mar 11, 2005
 */
package com.activiti.web.jsf.component.data;

import javax.faces.component.UIComponentBase;

import com.activiti.web.jsf.renderer.data.IRichListRenderer;
import com.activiti.web.jsf.renderer.data.RichListRenderer;


/**
 * @author kevinr
 */
public class UIRichList extends UIComponentBase
{
   /**
    * Default constructor
    */
   public UIRichList()
   {
      setRendererType("awc.faces.RichListRenderer");
      
      // set the default view renderer impl - this could come from a config
      this.getAttributes().put("viewRenderer", new RichListRenderer.ListViewRenderer());
   }

   /**
    * @see javax.faces.component.UIComponent#getFamily()
    */
   public String getFamily()
   {
      return "awc.faces.Data";
   }
}
