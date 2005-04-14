/*
 * Created on 12-Apr-2005
 */
package com.activiti.web.jsf.tag;

import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;

/**
 * @author kevinr
 */
public class ModeListTag extends HtmlComponentTag
{
   /**
    * @see javax.faces.webapp.UIComponentTag#getComponentType()
    */
   public String getComponentType()
   {
      return "awc.faces.ModeList";
   }

   /**
    * @see javax.faces.webapp.UIComponentTag#getRendererType()
    */
   public String getRendererType()
   {
      return "awc.faces.ModeListRenderer";
   }
   
   /**
    * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
    */
   protected void setProperties(UIComponent component)
   {
      super.setProperties(component);
      setActionProperty((UICommand)component, this.action);
      setActionListenerProperty((UICommand)component, this.actionListener);
      setStringProperty(component, "itemStyleClass", this.itemStyleClass);
      setStringProperty(component, "itemStyle", this.itemStyle);
      setStringProperty(component, "selectedStyleClass", this.selectedStyleClass);
      setStringProperty(component, "selectedStyle", this.selectedStyle);
      setIntProperty(component, "itemSpacing", this.itemSpacing);
      setIntProperty(component, "iconColumnWidth", this.iconColumnWidth);
      setIntProperty(component, "width", this.width);
      setStringProperty(component, "label", this.label);
      setStringProperty(component, "value", this.value);
   }
   
   /**
    * @see javax.servlet.jsp.tagext.Tag#release()
    */
   public void release()
   {
      super.release();
      this.itemStyleClass = null;
      this.itemStyle = null;
      this.selectedStyleClass = null;
      this.selectedStyle = null;
      this.itemSpacing = null;
      this.iconColumnWidth = null;
      this.width = null;
      this.label = null;
      this.action = null;
      this.actionListener = null;
      this.value = null;
   }
   
   /**
    * Set the itemStyleClass
    *
    * @param itemStyleClass     the itemStyleClass
    */
   public void setItemStyleClass(String itemStyleClass)
   {
      this.itemStyleClass = itemStyleClass;
   }

   /**
    * Set the itemStyle
    *
    * @param itemStyle     the itemStyle
    */
   public void setItemStyle(String itemStyle)
   {
      this.itemStyle = itemStyle;
   }

   /**
    * Set the selectedStyleClass
    *
    * @param selectedStyleClass     the selectedStyleClass
    */
   public void setSelectedStyleClass(String selectedStyleClass)
   {
      this.selectedStyleClass = selectedStyleClass;
   }

   /**
    * Set the selectedStyle
    *
    * @param selectedStyle     the selectedStyle
    */
   public void setSelectedStyle(String selectedStyle)
   {
      this.selectedStyle = selectedStyle;
   }

   /**
    * Set the itemSpacing
    *
    * @param itemSpacing     the itemSpacing
    */
   public void setItemSpacing(String itemSpacing)
   {
      this.itemSpacing = itemSpacing;
   }

   /**
    * Set the iconColumnWidth
    *
    * @param iconColumnWidth     the iconColumnWidth
    */
   public void setIconColumnWidth(String iconColumnWidth)
   {
      this.iconColumnWidth = iconColumnWidth;
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
    * Set the action
    *
    * @param action     the action
    */
   public void setAction(String action)
   {
      this.action = action;
   }

   /**
    * Set the actionListener
    *
    * @param actionListener     the actionListener
    */
   public void setActionListener(String actionListener)
   {
      this.actionListener = actionListener;
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
   
   /** the itemStyleClass */
   private String itemStyleClass;

   /** the itemStyle */
   private String itemStyle;

   /** the selectedStyleClass */
   private String selectedStyleClass;

   /** the selectedStyle */
   private String selectedStyle;

   /** the itemSpacing */
   private String itemSpacing;

   /** the iconColumnWidth */
   private String iconColumnWidth;

   /** the label */
   private String label;

   /** the action */
   private String action;

   /** the actionListener */
   private String actionListener;

   /** the value */
   private String value;
}
