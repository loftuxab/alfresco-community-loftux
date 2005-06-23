/*
 * Created on Mar 3, 2005
 */
package org.alfresco.web.ui.common.tag;

import javax.faces.component.UIComponent;

/**
 * @author kevinr
 * 
 * Tag handler for an Input UI Component specific for Date input.
 * 
 * This tag collects the user params needed to specify an Input component to allow
 * the user to enter a date. It specifies the renderer as below to be our Date
 * specific renderer. This renderer is configured in the faces-config.xml.  
 */
public class InputDatePickerTag extends HtmlComponentTag
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
      return "org.alfresco.faces.DatePickerRenderer";
   }
   
   /**
    * @see javax.servlet.jsp.tagext.Tag#release()
    */
   public void release()
   {
      super.release();
      this.startYear = "1990";
      this.yearCount = "10";
      this.value = null;
   }
   
   /**
    * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
    */
   protected void setProperties(UIComponent component)
   {
      super.setProperties(component);
      
      // set the properties of tag into the component
      setIntProperty(component, "startYear", this.startYear);
      setIntProperty(component, "yearCount", this.yearCount);
      setStringProperty(component, "value", this.value);
   }
   
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
    * Set the startYear
    *
    * @param startYear     the startYear
    */
   public void setStartYear(String startYear)
   {
      this.startYear = startYear;
   }

   /**
    * Set the yearCount
    *
    * @param yearCount     the yearCount
    */
   public void setYearCount(String yearCount)
   {
      this.yearCount = yearCount;
   }

   
   private String startYear = "1990";
   private String yearCount = "10";
   private String value = null;
}
