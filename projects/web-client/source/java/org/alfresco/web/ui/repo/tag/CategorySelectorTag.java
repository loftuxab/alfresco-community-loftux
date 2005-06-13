/*
 * Created on 25-May-2005
 */
package org.alfresco.web.ui.repo.tag;

import javax.faces.component.UIComponent;

import org.alfresco.web.ui.common.tag.HtmlComponentTag;

/**
 * Tag implementation to allow the category selector component to be placed on a JSP page
 * 
 * @author gavinc
 */
public class CategorySelectorTag extends HtmlComponentTag
{
   /**
    * @see javax.faces.webapp.UIComponentTag#getComponentType()
    */
   public String getComponentType()
   {
      return "org.alfresco.faces.CategorySelector";
   }

   /**
    * @see javax.faces.webapp.UIComponentTag#getRendererType()
    */
   public String getRendererType()
   {
      return null;
   }

   /**
    * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
    */
   protected void setProperties(UIComponent component)
   {
      super.setProperties(component);
      
      setStringBindingProperty(component, "value", this.value);
      setStringProperty(component, "label", this.label);
      setStringProperty(component, "nodeStyle", this.nodeStyle);
      setStringProperty(component, "nodeStyleClass", this.nodeStyleClass);
      setIntProperty(component, "spacing", this.spacing);
   }
   
   /**
    * @see org.alfresco.web.ui.common.tag.HtmlComponentTag#release()
    */
   public void release()
   {
      super.release();
      
      this.value = null;
      this.label = null;
      this.spacing = null;
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
    * Set the label
    *
    * @param label     the label
    */
   public void setLabel(String label)
   {
      this.label = label;
   }

   /**
    * Set the spacing
    *
    * @param spacing     the spacing
    */
   public void setSpacing(String spacing)
   {
      this.spacing = spacing;
   }

   /**
    * Set the node style
    * 
    * @param nodeStyle  the node style
    */
   public void setNodeStyle(String nodeStyle)
   {
      this.nodeStyle = nodeStyle;
   }

   /**
    * Set the node style class
    * 
    * @param nodeStyleClass   the node style class
    */
   public void setNodeStyleClass(String nodeStyleClass)
   {
      this.nodeStyleClass = nodeStyleClass;
   }

   /** the value */
   private String value;

   /** the label */
   private String label;

   /** the spacing */
   private String spacing;
   
   /** the node style */
   private String nodeStyle;
   
   /** the node style class */
   private String nodeStyleClass;
}
