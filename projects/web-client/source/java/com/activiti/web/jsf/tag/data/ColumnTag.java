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
      m_value = null;
      m_label = null;
      m_primary = null;
   }
   
   
   /**
    * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
    */
   protected void setProperties(UIComponent component)
   {
      super.setProperties(component);
      
      setStringBindingProperty(component, "value", m_value);
      setStringProperty(component, "label", m_label);
      setBooleanProperty(component, "primary", m_primary);
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
      m_value = value;
   }

   /**
    * Set the label
    *
    * @param label     the label
    */
   public void setLabel(String label)
   {
      m_label = label;
   }

   /**
    * Set the primary
    *
    * @param primary     the primary
    */
   public void setPrimary(String primary)
   {
      m_primary = primary;
   }


   /** the primary */
   private String m_primary;
   
   /** the value */
   private String m_value;

   /** the label */
   private String m_label;
}
