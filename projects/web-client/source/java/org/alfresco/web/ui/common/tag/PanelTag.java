/*
 * Created on Mar 30, 2005
 */
package org.alfresco.web.ui.common.tag;

import java.util.Random;

import javax.faces.component.UIComponent;
import javax.servlet.jsp.JspException;

import org.alfresco.web.ui.common.component.UIPanel;

/**
 * @author kevinr
 */
public class PanelTag extends HtmlComponentTag
{
   /**
    * @see javax.faces.webapp.UIComponentTag#getComponentType()
    */
   public String getComponentType()
   {
      return "awc.faces.Panel";
   }

   /**
    * @see javax.faces.webapp.UIComponentTag#getRendererType()
    */
   public String getRendererType()
   {
      // the component is self renderering
      return null;
   }

   /**
    * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
    */
   protected void setProperties(UIComponent component)
   {
      super.setProperties(component);
      
      setStringProperty(component, "label", this.label);
      setStringProperty(component, "border", this.border);
      setBooleanProperty(component, "progressive", this.progressive);
      setStringProperty(component, "bgcolor", this.bgcolor);
      setBooleanProperty(component, "expanded", this.expanded);
   }
   
   /**
    * @see javax.servlet.jsp.tagext.Tag#release()
    */
   public void release()
   {
      super.release();
      this.label = null;
      this.border = null;
      this.progressive = null;
      this.bgcolor = null;
      this.expanded = null;
   }
   
   /**
    * Override this to allow the panel component to control whether child components
    * are rendered by the JSP tag framework. This is a nasty solution as it requires
    * a reference to the UIPanel instance and also specific knowledge of the component
    * type that is created by the framework for this tag.
    * 
    * The reason for this solution is to allow any child content (including HTML tags)
    * to be displayed inside the UIPanel component without having to resort to the
    * awful JSF Component getRendersChildren() mechanism - as this would force the use
    * of the verbatim tags for ALL non-JSF child content!
    */
   protected int getDoStartValue() throws JspException
   {
      UIComponent component = getComponentInstance();
      if (component instanceof UIPanel)
      {
         if (((UIPanel)component).isExpanded() == true)
         {
            return EVAL_BODY_INCLUDE;
         }
         else
         {
            return SKIP_BODY;
         }
      }
      return EVAL_BODY_INCLUDE;
   }

   /**
    * Set the border
    *
    * @param border     the border
    */
   public void setBorder(String border)
   {
      this.border = border;
   }

   /**
    * Set the progressive
    *
    * @param progressive     the progressive
    */
   public void setProgressive(String progressive)
   {
      this.progressive = progressive;
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
    * Set the bgcolor
    *
    * @param bgcolor     the bgcolor
    */
   public void setBgcolor(String bgcolor)
   {
      this.bgcolor = bgcolor;
   }
   
   /**
    * Set whether the panel is expanded, default is true.
    *
    * @param expanded     the expanded flag
    */
   public void setExpanded(String expanded)
   {
      this.expanded = expanded;
   }


   /** the expanded flag */
   private String expanded;

   /** the border */
   private String border;

   /** the progressive */
   private String progressive;

   /** the label */
   private String label;

   /** the bgcolor */
   private String bgcolor;
}
