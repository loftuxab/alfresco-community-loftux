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
      m_value = null;
      m_var = null;
      m_filtering = null;
      m_initialSortColumn = null;
      m_initialSortDirection = null;
      m_listConfig = null;
      m_sorting = null;
      m_viewModes = null;
      m_pageSize = null;
      m_style = null;
      m_styleClass = null;
   }
   
   /**
    * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
    */
   protected void setProperties(UIComponent component)
   {
      super.setProperties(component);
      setStringBindingProperty(component, "value", m_value);
      setStringStaticProperty(component, "var", m_var);
      setBooleanProperty(component, "filtering", m_filtering);
      setBooleanProperty(component, "sorting", m_sorting);
      setStringProperty(component, "initialSortColumn", m_initialSortColumn);
      setStringProperty(component, "initialSortDirection", m_initialSortDirection);
      setStringProperty(component, "listConfig", m_listConfig);
      setStringProperty(component, "viewModes", m_viewModes);
      setStringProperty(component, "style", m_style);
      setStringProperty(component, "styleClass", m_styleClass);
      setIntProperty(component, "pageSize", m_pageSize);
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
      m_viewModes = viewModes;
   }

   /**
    * Set the filtering
    *
    * @param filtering     the filtering
    */
   public void setFiltering(String filtering)
   {
      m_filtering = filtering;
   }
   
   /**
    * Set the pageSize
    *
    * @param pageSize     the pageSize
    */
   public void setPageSize(String pageSize)
   {
      m_pageSize = pageSize;
   }
   
   /**
    * Set the sorting
    *
    * @param sorting     the sorting
    */
   public void setSorting(String sorting)
   {
      m_sorting = sorting;
   }
   
   /**
    * Set the initialSortColumn
    *
    * @param initialSortColumn     the initialSortColumn
    */
   public void setInitialSortColumn(String initialSortColumn)
   {
      m_initialSortColumn = initialSortColumn;
   }
   
   /**
    * Set the initialSortDirection
    *
    * @param initialSortDirection     the initialSortDirection
    */
   public void setInitialSortDirection(String initialSortDirection)
   {
      m_initialSortDirection = initialSortDirection;
   }
   
   /**
    * Set the listConfig
    *
    * @param listConfig     the listConfig
    */
   public void setListConfig(String listConfig)
   {
      m_listConfig = listConfig;
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
    * Set the var
    *
    * @param var     the var
    */
   public void setVar(String var)
   {
      m_var = var;
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
    * Set the styleClass
    *
    * @param styleClass     the styleClass
    */
   public void setStyleClass(String styleClass)
   {
      m_styleClass = styleClass;
   }


   // ------------------------------------------------------------------------------
   // Private data
   
   /** the style */
   private String m_style;

   /** the styleClass */
   private String m_styleClass;

   /** the value */
   private String m_value;

   /** the var */
   private String m_var;

   /** the viewModes */
   private String m_viewModes;

   /** the filtering */
   private String m_filtering;

   /** the pageSize */
   private String m_pageSize;

   /** the sorting */
   private String m_sorting;

   /** the initialSortColumn */
   private String m_initialSortColumn;

   /** the initialSortDirection */
   private String m_initialSortDirection;

   /** the listConfig */
   private String m_listConfig;
}
