/*
 * Created on Mar 11, 2005
 */
package com.activiti.web.jsf.tag.data;

import javax.faces.component.UIComponent;

import com.activiti.web.jsf.tag.BaseComponentTag;


/**
 * @author kevinr
 */
public class ColumnTag extends BaseComponentTag
{
   /**
    * @see javax.faces.webapp.UIComponentTag#getComponentType()
    */
   public String getComponentType()
   {
      return "awc.faces.RichListColumn";
   }

   /**
    * @see javax.faces.webapp.UIComponentTag#getRendererType()
    */
   public String getRendererType()
   {
      // the component is renderer by the parent
      return null;
   }
   
   /**
    * @see javax.servlet.jsp.tagext.Tag#release()
    */
   public void release()
   {
      super.release();
      this.value = null;
      this.label = null;
      this.primary = null;
      this.width = null;
   }
   
   /**
    * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
    */
   protected void setProperties(UIComponent component)
   {
      super.setProperties(component);
      
      setStringBindingProperty(component, "value", this.value);
      setStringProperty(component, "label", this.label);
      setBooleanProperty(component, "primary", this.primary);
      setStringProperty(component, "width", this.width);
   }
   
   
   // ------------------------------------------------------------------------------
   // Tag properties

   /**
    * Set the value
    *
    * @param value     the value
    */
   public void setValue(String value)
   {
      this.value = value;
   }

   /**
    * Set the label
    *
    * @param label     the label
    */
   public void setLabel(String label)
   {
      this.label = label;
   }

   /**
    * Set the primary
    *
    * @param primary     the primary
    */
   public void setPrimary(String primary)
   {
      this.primary = primary;
   }
   
   /**
    * Set the width
    *
    * @param width     the width
    */
   public void setWidth(String width)
   {
      this.width = width;
   }


   /** the width */
   private String width;

   /** the primary */
   private String primary;
   
   /** the value */
   private String value;
   
   /** the label */
   private String label;
}
