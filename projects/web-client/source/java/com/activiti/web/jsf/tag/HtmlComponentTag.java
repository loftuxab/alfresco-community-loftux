/*
 * Created on Mar 9, 2005
 */
package com.activiti.web.jsf.tag;


/**
 * Base class for tags that represent HTML components.
 * 
 * @author kevinr
 */
public abstract class HtmlComponentTag extends BaseComponentTag
{
   /**
    * @see javax.servlet.jsp.tagext.Tag#release()
    */
   public void release()
   {
      super.release();
      m_style = null;
      m_styleClass = null;
      m_title = null;
      m_visible = null;
   }
   
   /**
    * Get the style
    *
    * @return the style
    */
   public String getStyle()
   {
      return m_style;
   }

   /**
    * Set the style
    *
    * @param style     the style
    */
   public void setStyle(String style)
   {
      m_style = style;
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

   /**
    * Get the visible
    *
    * @return the visible
    */
   public String getVisible()
   {
      return m_visible;
   }

   /**
    * Set the visible
    *
    * @param visible     the visible
    */
   public void setVisible(String visible)
   {
      m_visible = visible;
   }

   /**
    * Get the title
    *
    * @return the title
    */
   public String getTitle()
   {
      return m_title;
   }

   /**
    * Set the title
    *
    * @param title     the title
    */
   public void setTitle(String title)
   {
      m_title = title;
   }


   /** the style */
   protected String m_style;

   /** the styleClass */
   protected String m_styleClass;

   /** the visible */
   protected String m_visible;

   /** the title */
   protected String m_title;
}
