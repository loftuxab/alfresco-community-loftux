/*
 * Created on Mar 3, 2005
 */
package com.activiti.web.jsf.tag;

import javax.faces.webapp.UIComponentTag;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * @author kevinr
 * 
 * Tag handler for an Input UI Component specific for Date input.
 * 
 * This tag collects the user params needed to specify an Input component to allow
 * the user to enter a date. It specifies the renderer as below to be our Date
 * specific renderer. This renderer is configured in the faces-config.xml.  
 */
public class InputDatePickerTag extends BaseComponentTag
{
   /**
    * @see javax.faces.webapp.UIComponentTag#getComponentType()
    */
   public String getComponentType()
   {
      // we are require an Input component to manage our state
      // this is just a convention name Id - not an actual class
      return "javax.faces.Input";
   }
   
   /**
    * @see javax.faces.webapp.UIComponentTag#getRendererType()
    */
   public String getRendererType()
   {
      // the renderer type is a convention name Id - not an actual class
      // see the <render-kit> in faces-config.xml
      return "awc.faces.DatePickerRenderer";
   }
   
   /**
    * @see javax.servlet.jsp.tagext.Tag#release()
    */
   public void release()
   {
      super.release();
      m_startYear = "1990";
      m_yearCount = "10";
      m_styleClass = null;
      m_value = null;
   }
   
   /**
    * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
    */
   protected void setProperties(UIComponent component)
   {
      super.setProperties(component);
      
      // set the properties of tag into the component
      setIntProperty(component, "startYear", m_startYear);
      setIntProperty(component, "yearCount", m_yearCount);
      setStringProperty(component, "styleClass", m_styleClass);
      setStringProperty(component, "value", m_value);
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
    * Set the startYear
    *
    * @param startYear     the startYear
    */
   public void setStartYear(String startYear)
   {
      m_startYear = startYear;
   }
   
   /**
    * Get the yearCount
    *
    * @return the yearCount
    */
   public String getYearCount()
   {
      return m_yearCount;
   }

   /**
    * Set the yearCount
    *
    * @param yearCount     the yearCount
    */
   public void setYearCount(String yearCount)
   {
      m_yearCount = yearCount;
   }

   /**
    * Get the styleClass
    *
    * @return the styleClass
    */
   public String getStyleClass()
   {
      return m_styleClass;
   }

   /**
    * Set the styleClass
    *
    * @param styleClass     the styleClass
    */
   public void setStyleClass(String styleClass)
   {
      m_styleClass = styleClass;
   }

   
   private String m_startYear = "1990";
   private String m_yearCount = "10";
   private String m_value = null;
   private String m_styleClass = null;
}
