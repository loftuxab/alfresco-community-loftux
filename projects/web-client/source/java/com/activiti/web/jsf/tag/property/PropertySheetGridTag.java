package com.activiti.web.jsf.tag.property;

import javax.faces.component.UIComponent;
import com.activiti.web.jsf.tag.BaseComponentTag;

/**
 * Tag to represent the combination of a PropertySheet component
 * and a Grid renderer
 * 
 * @author gavinc
 */
public class PropertySheetGridTag extends BaseComponentTag
{
   private String m_value;
   private String m_var;
   
   /**
    * @see javax.faces.webapp.UIComponentTag#getComponentType()
    */
   public String getComponentType()
   {
      return "awc.faces.PropertySheet";
   }

   /**
    * @see javax.faces.webapp.UIComponentTag#getRendererType()
    */
   public String getRendererType()
   {
      // TODO: replace this with the standard javax.faces.Grid
      return "awc.faces.Grid";
   }
   
   /**
    * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
    */
   protected void setProperties(UIComponent component)
   {
      super.setProperties(component);
      
      setStringProperty(component, "value", m_value);
      setStringProperty(component, "var", m_var);
   }

   /**
    * @param value The value to set.
    */
   public void setValue(String value)
   {
      m_value = value;
   }
   
   /**
    * @param var The var to set.
    */
   public void setVar(String var)
   {
      m_var = var;
   }
}
