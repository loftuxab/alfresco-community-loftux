/*
 * Created on Mar 9, 2005
 */
package com.activiti.web.jsf.tag;

import javax.faces.component.UIComponent;


/**
 * Base class for tags that represent HTML components.
 * 
 * @author kevinr
 */
public abstract class HtmlComponentTag extends BaseComponentTag
{
   /**
    * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
    */
   protected void setProperties(UIComponent component)
   {
      super.setProperties(component);
      
      setStringProperty(component, "style", this.style);
      setStringProperty(component, "styleClass", this.styleClass);
      setStringProperty(component, "title", this.title);
   }
   
   /**
    * @see javax.servlet.jsp.tagext.Tag#release()
    */
   public void release()
   {
      super.release();
      this.style = null;
      this.styleClass = null;
      this.title = null;
   }
   
   /**
    * Get the style
    *
    * @return the style
    */
   public String getStyle()
   {
      return this.style;
   }

   /**
    * Set the style
    *
    * @param style     the style
    */
   public void setStyle(String style)
   {
      this.style = style;
   }

   /**
    * Get the styleClass
    *
    * @return the styleClass
    */
   public String getStyleClass()
   {
      return this.styleClass;
   }

   /**
    * Set the styleClass
    *
    * @param styleClass     the styleClass
    */
   public void setStyleClass(String styleClass)
   {
      this.styleClass = styleClass;
   }

   /**
    * Get the title
    *
    * @return the title
    */
   public String getTitle()
   {
      return this.title;
   }

   /**
    * Set the title
    *
    * @param title     the title
    */
   public void setTitle(String title)
   {
      this.title = title;
   }


   /** the style */
   protected String style;

   /** the styleClass */
   protected String styleClass;

   /** the title */
   protected String title;
}
