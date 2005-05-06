package org.alfresco.web.ui.common.tag.property;

import javax.faces.component.UIComponent;

import org.alfresco.web.ui.common.tag.BaseComponentTag;

/**
 * @author gavinc
 */
public class PropertyTag extends BaseComponentTag
{
   private String value;
   private String columns;
   
   /**
    * @see javax.faces.webapp.UIComponentTag#getRendererType()
    */
   public String getRendererType()
   {
      return "javax.faces.Grid";
   }
   
   /**
    * @see javax.faces.webapp.UIComponentTag#getComponentType()
    */
   public String getComponentType()
   {
      return "awc.faces.Property";
   }
   
   /**
    * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
    */
   protected void setProperties(UIComponent component)
   {
      super.setProperties(component);
      
      setStringProperty(component, "value", value);
      setIntProperty(component, "columns", columns);
   }
   
   /**
    * @see javax.faces.webapp.UIComponentTag#release()
    */
   public void release()
   {
      this.value = null;
      this.columns = null;
      
      super.release();
   }

   /**
    * @param value The value to set.
    */
   public void setValue(String value)
   {
      this.value = value;
   }

   /**
    * @param columns The columns to set.
    */
   public void setColumns(String columns)
   {
      this.columns = columns;
   }
}
