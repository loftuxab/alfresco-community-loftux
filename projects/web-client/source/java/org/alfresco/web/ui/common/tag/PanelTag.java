/*
 * Created on Mar 30, 2005
 */
package org.alfresco.web.ui.common.tag;

import javax.faces.component.UICommand;
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
      return "org.alfresco.faces.Panel";
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
      
      setActionProperty((UICommand)component, this.action);
      setActionListenerProperty((UICommand)component, this.actionListener);
      setStringProperty(component, "label", this.label);
      setStringProperty(component, "border", this.border);
      setBooleanProperty(component, "progressive", this.progressive);
      setStringProperty(component, "bgcolor", this.bgcolor);
      setStringProperty(component, "titleBorder", this.titleBorder);
      setStringProperty(component, "titleBgcolor", this.titleBgcolor);
      setBooleanProperty(component, "expanded", this.expanded);
      setStringProperty(component, "linkLabel", this.linkLabel);
      setStringProperty(component, "linkIcon", this.linkIcon);
      setStringProperty(component, "linkStyleClass", this.linkStyleClass);
      setStringProperty(component, "linkTooltip", this.linkTooltip);
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
      this.action = null;
      this.actionListener = null;
      this.linkIcon = null;
      this.linkLabel = null;
      this.linkStyleClass = null;
      this.linkTooltip = null;
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
         if (((UIPanel)component).isExpanded() == true && component.isRendered() == true)
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
    * @param titleBgcolor The title area background color
    */
   public void setTitleBgcolor(String titleBgcolor)
   {
      this.titleBgcolor = titleBgcolor;
   }

   /**
    * @param titleBorder The title area border style
    */
   public void setTitleBorder(String titleBorder)
   {
      this.titleBorder = titleBorder;
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

   /**
    * @param action Sets the action
    */
   public void setAction(String action)
   {
      this.action = action;
   }

   /**
    * @param actionListener Sets the action listener
    */
   public void setActionListener(String actionListener)
   {
      this.actionListener = actionListener;
   }

   /**
    * @param linkIcon Sets the icon to use for the link
    */
   public void setLinkIcon(String linkIcon)
   {
      this.linkIcon = linkIcon;
   }

   /**
    * @param linkLabel Sets the label to use for the link
    */
   public void setLinkLabel(String linkLabel)
   {
      this.linkLabel = linkLabel;
   }

   /**
    * @param linkStyleClass Sets the style to use for the link
    */
   public void setLinkStyleClass(String linkStyleClass)
   {
      this.linkStyleClass = linkStyleClass;
   }

   /**
    * @param linkTooltip Sets the tooltip to use for the link
    */
   public void setLinkTooltip(String linkTooltip)
   {
      this.linkTooltip = linkTooltip;
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

   /** the title border style */
   private String titleBorder;
   
   /** the title bgcolor */
   private String titleBgcolor;
   
   /** the action */
   private String action;
   
   /** the action listener */
   private String actionListener;
   
   /** the link label */
   private String linkLabel;
   
   /** the link icon */
   private String linkIcon;
   
   /** the link tooltip */
   private String linkTooltip;
   
   /** the link style class */
   private String linkStyleClass;
}
