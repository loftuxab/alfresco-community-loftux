/*
 * Created on Mar 15, 2005
 */
package com.activiti.web.jsf.tag.data;

import javax.faces.component.UIComponent;

import com.activiti.web.jsf.tag.HtmlComponentTag;

/**
 * @author kevinr
 */
public class SortLinkTag extends HtmlComponentTag
{
   // ------------------------------------------------------------------------------
   // Component methods 
   
   /**
    * @see javax.faces.webapp.UIComponentTag#getComponentType()
    */
   public String getComponentType()
   {
      return "awc.faces.SortLink";
   }

   /**
    * @see javax.faces.webapp.UIComponentTag#getRendererType()
    */
   public String getRendererType()
   {
      return null;
   }
   
   /**
    * @see javax.servlet.jsp.tagext.Tag#release()
    */
   public void release()
   {
      super.release();
      m_mode = null;
      m_value = null;
      m_label = null;
   }
   
   /**
    * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
    */
   protected void setProperties(UIComponent component)
   {
      super.setProperties(component);
      setStringProperty(component, "value", m_value);
      setStringProperty(component, "label", m_label);
      setStringProperty(component, "mode", m_mode);
   }
   
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
    * Set the sorting mode (see IDataContainer constants)
    *
    * @param mode     the sort mode
    */
   public void setMode(String mode)
   {
      m_mode = mode;
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


   /** the label */
   private String m_label;

   /** the value */
   private String m_value;

   /** the sorting mode */
   private String m_mode;
}
