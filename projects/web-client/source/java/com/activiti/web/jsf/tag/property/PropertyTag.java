package com.activiti.web.jsf.tag.property;

import javax.faces.webapp.UIComponentTag;

/**
 * @author gavinc
 */
public class PropertyTag extends UIComponentTag
{
   /**
    * @see javax.faces.webapp.UIComponentTag#getRendererType()
    */
   public String getRendererType()
   {
      return null;
   }
   
   /**
    * @see javax.faces.webapp.UIComponentTag#getComponentType()
    */
   public String getComponentType()
   {
      return "awc.faces.Property";
   }
}
