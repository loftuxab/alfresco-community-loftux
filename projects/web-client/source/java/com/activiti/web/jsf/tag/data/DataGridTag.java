package com.activiti.web.jsf.tag.data;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.el.ValueBinding;
import javax.faces.webapp.UIComponentTag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import com.activiti.web.data.IDataHandler;
import com.activiti.web.jsf.tag.BaseComponentTag;

/**
 * DataGridTag
 * 
 * Datagrid Tag Handler. The data handler manages the dissemination of data from 
 * the datasrc in rows per page.
 */
public class DataGridTag extends BaseComponentTag implements IDataProviderTag
{
   // ------------------------------------------------------------------------------
   // Component methods 
   
   /**
    * @see javax.faces.webapp.UIComponentTag#getComponentType()
    */
   public String getComponentType()
   {
      return "awc.faces.DataGrid";
   }
   
   /**
    * @see javax.faces.webapp.UIComponentTag#getRendererType()
    */
   public String getRendererType()
   {
      // The datagrid component is self rendering
      return null;
   }
   
   /**
    * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
    */
   protected void setProperties(UIComponent component)
   {
      super.setProperties(component);
      
      //setStringProperty(component, "datasource", m_datasourceName);
      setStringProperty(component, "cellpadding", m_cellpadding);
      setStringProperty(component, "cellspacing", m_cellspacing);
      setStringProperty(component, "styleClass", m_styleClass);
      setStringProperty(component, "style", m_styleClass);
      setStringProperty(component, "initialSortedColumn", m_initialSortedColumn);
      setBooleanProperty(component, "initialSortedDirection", m_initialSortedDirection);
      setIntProperty(component, "pageSize", m_pageSize);
   }
   
   
   // ------------------------------------------------------------------------------
   // Tag properties
   
   /**
    * Name of the data source object to lookup
    * 
    * @param datasource
    */
   //public void setDatasource(String datasource)
   //{
   //   m_datasourceName = datasource;
   //}
   
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
    * Get the cellspacing
    *
    * @return the cellspacing
    */
   public String getCellspacing()
   {
      return m_cellspacing;
   }

   /**
    * Set the cellspacing
    *
    * @param cellspacing     the cellspacing
    */
   public void setCellspacing(String cellspacing)
   {
      m_cellspacing = cellspacing;
   }

   /**
    * Get the cellpadding
    *
    * @return the cellpadding
    */
   public String getCellpadding()
   {
      return m_cellpadding;
   }

   /**
    * Set the cellpadding
    *
    * @param cellpadding     the cellpadding
    */
   public void setCellpadding(String cellpadding)
   {
      m_cellpadding = cellpadding;
   }

   /**
    * Get the initialSortedColumn
    *
    * @return the initialSortedColumn
    */
   public String getInitialSortedColumn()
   {
      return m_initialSortedColumn;
   }

   /**
    * Set the initialSortedColumn
    *
    * @param initialSortedColumn     the initialSortedColumn
    */
   public void setInitialSortedColumn(String initialSortedColumn)
   {
      m_initialSortedColumn = initialSortedColumn;
   }

   /**
    * Get the initialSortedDirection
    *
    * @return the initialSortedDirection
    */
   public String getInitialSortedDirection()
   {
      return m_initialSortedDirection;
   }

   /**
    * Set the initialSortedDirection
    *
    * @param initialSortedDirection     the initialSortedDirection
    */
   public void setInitialSortedDirection(String initialSortedDirection)
   {
      m_initialSortedDirection = initialSortedDirection;
   }

   /**
    * Get the pageSize
    *
    * @return the pageSize
    */
   public String getPageSize()
   {
      return m_pageSize;
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
   
   
   //------------------------------------------------------------------------------
   // Tag implemenation
   
   /**
    * release tag attributes
    */
   public void release()
   {
      m_dataHandler = null;
      m_pageSize = null;
      m_cellpadding = null;
      m_cellspacing = null;
      //m_datasourceName = null;
      m_initialSortedColumn = null;
      m_initialSortedDirection = "true";
   }
   
   /**
    * Process the start tag.
    *
    * @return               Continuation instruction.
    */
   /*public int doStartTag() throws JspTagException
   {
      // register the data grid hidden field with the parent form
      FormTag parent = (FormTag)findAncestorWithClass(this, FormTag.class);
      if (parent == null)
      {
         throw new IllegalStateException("DataGridTag must be contained within parent Form tag!");
      }
      parent.addHiddenField(DataConstants.DATAHANDLER_NAME_PARAM, null);
      
      // bind the datahandler to the tag attributes
      bind();
      
      try
      {
         JspWriter out = pageContext.getOut();
         
         // start the table grid
         out.print("<table border=0");
         if (m_styleClass != null && m_styleClass.length() != 0)
         {
            out.print(" class='");
            out.print(m_styleClass);
            out.print('\'');
         }
         if (m_cellpadding != null && m_cellpadding.length() != 0)
         {
            out.print(" cellpadding='");
            out.print(m_cellpadding);
            out.print('\'');
         }
         if (m_cellspacing != null && m_cellspacing.length() != 0)
         {
            out.print(" cellspacing='");
            out.print(m_cellspacing);
            out.print('\'');
         }
         out.print('>');
      }
      catch (Exception e)
      {
         throw new JspTagException(e.getMessage());
      }
      
      return EVAL_BODY_INCLUDE;
   }*/
   
   /**
    * Process the end tag.
    *
    * @return               Always return EVAL_PAGE.
    */
   /*public int doEndTag() throws JspTagException
   {
      try
      {
         JspWriter out = pageContext.getOut();
         
         out.print("</table>");
      }
      catch (Exception e)
      {
         throw new JspTagException(e.getMessage());
      }
      
      return EVAL_PAGE;
   }*/
   
   
   // ------------------------------------------------------------------------------
   // IDataProviderTag implementation
   
   /**
    * Return the object supporting the IDataHandler interface that stores the
    * current state of this data handler. This stores the state so the tag object
    * can remain stateless and be used by the app-server.
    * 
    * @return the IDataHandler supporting object
    */
   public IDataHandler getDataHandler()
   {
      if (m_dataHandler == null)
      {
         /* // Return the object supporting the IDataHandler interface for us
         IDataBean bean = (IDataBean)pageContext.getSession().getAttribute(m_beanName);
         if (bean == null)
         {
            bean = (IDataBean)pageContext.getAttribute(m_beanName);
         }
         if (bean == null)
         {
            throw new IllegalStateException("Unable to find request level state bean: " + m_beanName);
         }
         
         IDataHandler state = bean.lookupDataHandler(getParentForm().getElementId());
         if (state == null)
         {
            // register this datagrid state for the first time
            state = bean.registerDataHandler(getParentForm().getElementId(), m_datasourceName);
         }
         
         // force reset to first row for current page
         // needed as the Portlet env seems to render page twice...!
         state.resetRowIndex();
         
         m_dataHandler = state;*/
      }
      
      return m_dataHandler;
   }
   
   
   //------------------------------------------------------------------------------
   // Private methods
   
   /**
    * Return the parent FormTag instance
    * 
    * @return the parent FormTag
    */
   /*private FormTag getParentForm()
   {
      return (FormTag)findAncestorWithClass(this, FormTag.class);
   }*/
   
   /**
    * Bind tag attributes to the data handler
    */
   /*private void bind()
   {
      // set the mandatory attributes so the data handler can initialise
      IDataHandler handler = getDataHandler();
      handler.setUniqueName(m_datasourceName);
      handler.setPageSize(m_nPageSize);
      
      // apply initially sorted column if required
      if (handler.getLastSortedColumn() == -1 && m_initialSortedColumn != null)
      {
         int column = handler.getDataSource().getMetaData().lookupFieldIndex(m_initialSortedColumn);
         if (column != -1)
         {
            // TODO: add initially sorted mode tag property
            handler.sort(column, m_initialSortedDirection, IDataHandler.SORT_CASEINS);
         }
      }
   }*/
   
   
   //------------------------------------------------------------------------------
   // Private data
   
   /** form data handler providing the state of the data */
   private IDataHandler m_dataHandler = null;
   
   /** data source name to lookup */
   //private String m_datasourceName = null;
   
   /** state bean name to find in the request */
   //private String m_beanName = null;
   
   /** the style */
   private String m_style;

   /** the CSS Class */
   private String m_styleClass;

   /** the cellspacing */
   private String m_cellspacing;

   /** the cellpadding */
   private String m_cellpadding;

   /** the initialSortedColumn */
   private String m_initialSortedColumn;

   /** the initialSortedDirection */
   private String m_initialSortedDirection;

   /** the page size */
   private String m_pageSize;

} // end class DataGridTag
