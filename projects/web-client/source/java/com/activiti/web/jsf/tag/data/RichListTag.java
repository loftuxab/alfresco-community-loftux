/*
 * Created on Mar 11, 2005
 */
package com.activiti.web.jsf.tag.data;

import javax.faces.component.UIComponent;

import com.activiti.web.jsf.tag.BaseComponentTag;


/**
 * @author kevinr
 */
public class RichListTag extends BaseComponentTag
{
   // ------------------------------------------------------------------------------
   // Component methods 
   
   /**
    * @see javax.faces.webapp.UIComponentTag#getComponentType()
    */
   public String getComponentType()
   {
      return "awc.faces.RichList";
   }

   /**
    * @see javax.faces.webapp.UIComponentTag#getRendererType()
    */
   public String getRendererType()
   {
      return "awc.faces.RichListRenderer";
   }

   /**
    * @see javax.servlet.jsp.tagext.Tag#release()
    */
   public void release()
   {
      super.release();
      this.value = null;
      this.var = null;
      this.initialSortColumn = null;
      this.initialSortDirection = null;
      this.listConfig = null;
      this.viewModes = null;
      this.pageSize = null;
      this.style = null;
      this.styleClass = null;
   }
   
   /**
    * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
    */
   protected void setProperties(UIComponent component)
   {
      super.setProperties(component);
      setStringBindingProperty(component, "value", this.value);
      setStringStaticProperty(component, "var", this.var);
      setStringProperty(component, "initialSortColumn", this.initialSortColumn);
      setStringProperty(component, "initialSortDirection", this.initialSortDirection);
      setStringProperty(component, "listConfig", this.listConfig);
      setStringProperty(component, "viewModes", this.viewModes);
      setStringProperty(component, "style", this.style);
      setStringProperty(component, "styleClass", this.styleClass);
      setStringProperty(component, "rowStyleClass", this.rowStyleClass);
      setStringProperty(component, "altRowStyleClass", this.altRowStyleClass);
      setIntProperty(component, "pageSize", this.pageSize);
   }
   
   
   // ------------------------------------------------------------------------------
   // Bean implementation 
   
   /**
    * Set the viewModes
    *
    * @param viewModes     the viewModes
    */
   public void setViewModes(String viewModes)
   {
      this.viewModes = viewModes;
   }
   
   /**
    * Set the pageSize
    *
    * @param pageSize     the pageSize
    */
   public void setPageSize(String pageSize)
   {
      this.pageSize = pageSize;
   }
   
   /**
    * Set the initialSortColumn
    *
    * @param initialSortColumn     the initialSortColumn
    */
   public void setInitialSortColumn(String initialSortColumn)
   {
      this.initialSortColumn = initialSortColumn;
   }
   
   /**
    * Set the initialSortDirection
    *
    * @param initialSortDirection     the initialSortDirection
    */
   public void setInitialSortDirection(String initialSortDirection)
   {
      this.initialSortDirection = initialSortDirection;
   }
   
   /**
    * Set the listConfig
    *
    * @param listConfig     the listConfig
    */
   public void setListConfig(String listConfig)
   {
      this.listConfig = listConfig;
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
    * Set the var
    *
    * @param var     the var
    */
   public void setVar(String var)
   {
      this.var = var;
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
    * Set the styleClass
    *
    * @param styleClass     the styleClass
    */
   public void setStyleClass(String styleClass)
   {
      this.styleClass = styleClass;
   }
   
   /**
    * Set the the row CSS Class
    *
    * @param rowStyleClass     the the row CSS Class
    */
   public void setRowStyleClass(String rowStyleClass)
   {
      this.rowStyleClass = rowStyleClass;
   }

   /**
    * Set the alternate row CSS Class
    *
    * @param altRowStyleClass     the alternate row CSS Class
    */
   public void setAltRowStyleClass(String altRowStyleClass)
   {
      this.altRowStyleClass = altRowStyleClass;
   }


   // ------------------------------------------------------------------------------
   // Private data
   
   /** the row CSS Class */
   private String rowStyleClass;

   /** the alternate row CSS Class */
   private String altRowStyleClass;
   
   /** the style */
   private String style;

   /** the styleClass */
   private String styleClass;

   /** the value */
   private String value;

   /** the var */
   private String var;

   /** the viewModes */
   private String viewModes;

   /** the pageSize */
   private String pageSize;

   /** the initialSortColumn */
   private String initialSortColumn;

   /** the initialSortDirection */
   private String initialSortDirection;

   /** the listConfig */
   private String listConfig;
}
