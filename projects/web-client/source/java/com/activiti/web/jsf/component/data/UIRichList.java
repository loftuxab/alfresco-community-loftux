/*
 * Created on Mar 11, 2005
 */
package com.activiti.web.jsf.component.data;

import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import org.apache.log4j.Logger;

import com.activiti.web.data.IDataContainer;
import com.activiti.web.jsf.renderer.data.IRichListRenderer;
import com.activiti.web.jsf.renderer.data.RichListRenderer;

/**
 * @author kevinr
 */
public class UIRichList extends UIComponentBase implements IDataContainer
{
   // ------------------------------------------------------------------------------
   // Construction
   
   /**
    * Default constructor
    */
   public UIRichList()
   {
      setRendererType("awc.faces.RichListRenderer");
   }

   
   // ------------------------------------------------------------------------------
   // Component implementation
   
   /**
    * @see javax.faces.component.UIComponent#getFamily()
    */
   public String getFamily()
   {
      return "awc.faces.Data";
   }
   
   /**
    * @see javax.faces.component.StateHolder#restoreState(javax.faces.context.FacesContext, java.lang.Object)
    */
   public void restoreState(FacesContext context, Object state)
   {
      Object values[] = (Object[])state;
      // standard component attributes are restored by the super class
      super.restoreState(context, values[0]);
      m_currentPage = ((Integer)values[1]).intValue();
      m_sortColumn = (String)values[2];
      m_sortDirection = ((Boolean)values[3]).booleanValue();
   }
   
   /**
    * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
    */
   public Object saveState(FacesContext context)
   {
      Object values[] = new Object[4];
      // standard component attributes are saved by the super class
      values[0] = super.saveState(context);
      values[1] = new Integer(m_currentPage);
      values[2] = m_sortColumn;
      values[3] = (m_sortDirection ? Boolean.TRUE : Boolean.FALSE);
      return (values);
   }
   
   /**
    * Get the value (for this component the value is an object used as the DataModel)
    *
    * @return the value
    */
   public Object getValue()
   {
      if (m_value == null)
      {
         ValueBinding vb = getValueBinding("value");
         if (vb != null)
         {
            m_value = vb.getValue(getFacesContext());
         }
      }
      return m_value;
   }

   /**
    * Set the value (for this component the value is an object used as the DataModel)
    *
    * @param value     the value
    */
   public void setValue(Object value)
   {
      m_dataModel = null;
      m_value = value;
   }
   
   
   // ------------------------------------------------------------------------------
   // IDataContainer implementation 
   
   /**
    * Return the currently sorted column if any
    * 
    * @return current sorted column if any
    */
   public String getCurrentSortColumn()
   {
      return m_sortColumn;
   }
   
   /**
    * Returns the current sort direction. Only valid if a sort column is set.
    * True is returned for descending sort, false for accending sort.
    * 
    * @return true for descending sort, false for accending sort
    */
   public boolean getCurrentSortDirection()
   {
      return m_sortDirection;
   }
   
   /**
    * Returns the current page size used for this list, or -1 for no paging.
    */
   public int getPageSize()
   {
      return m_pageSize;
   }
   
   /**
    * Sets the current page size used for the list.
    * 
    * @param val
    */
   public void setPageSize(int val)
   {
      // TODO: value binding code
      m_pageSize = val;
   }
   
   /**
    * @see com.activiti.web.data.IDataContainer#getPageCount()
    */
   public int getPageCount()
   {
      return m_pageCount;
   }
   
   /**
    * Return the current page the list is displaying
    * 
    * @return current page zero based index
    */
   public int getCurrentPage()
   {
      return m_currentPage;
   }
   
   /**
    * @see com.activiti.web.data.IDataContainer#setCurrentPage(int)
    */
   public void setCurrentPage(int index)
   {
      m_currentPage = index;
   }

   /**
    * Returns true if a row of data is available
    * 
    * @return true if data is available, false otherwise
    */
   public boolean isDataAvailable()
   {
      return m_rowIndex < m_maxRowIndex;
   }
   
   /**
    * Returns the next row of data from the data model
    * 
    * @return next row of data as a Bean object
    */
   public Object nextRow()
   {
      // get next row and increment row count
      Object rowData = getDataModel().getRow(m_rowIndex + 1);
      
      // Prepare the data-binding variable "var" ready for the next cycle of
      // renderering for the child components. 
      String var = (String)getAttributes().get("var");
      if (var != null)
      {
         Map requestMap = getFacesContext().getExternalContext().getRequestMap();
         if (isDataAvailable() == true)
         {
            requestMap.put(var, rowData);
         }
         else
         {
            requestMap.remove(var);
         }
      }
      
      m_rowIndex++;
      
      return rowData;
   }
   
   /**
    * Sort the dataset using the specified sort parameters
    * 
    * @param column        Column to sort
    * @param bAscending    True for ascending sort, false for descending
    * @param mode          Sort mode to use (see IDataContainer constants)
    */
   public void sort(String column, boolean bAscending, String mode)
   {
      m_sortColumn = column;
      m_sortDirection = bAscending;
      
      // TODO: implement stable merge sort.
   }
   
   
   // ------------------------------------------------------------------------------
   // UIRichList implementation
   
   /**
    * Method called to bind the RichList component state to the data model value
    */
   public void bind()
   {
      int rowCount = getDataModel().size();
      // if a page size is specified, then we use that
      if (m_pageSize != -1)
      {
         // calc start row index based on current page index
         m_rowIndex = (m_currentPage * m_pageSize) - 1;
         
         // calc total number of pages available
         m_pageCount = (rowCount / m_pageSize) + 1;
         if (rowCount % m_pageSize == 0 && m_pageCount != 1)
         {
            m_pageCount--;
         }
         
         // calc the maximum row index that can be returned
         m_maxRowIndex = m_rowIndex + m_pageSize;
         if (m_maxRowIndex >= rowCount)
         {
            m_maxRowIndex = rowCount - 1;
         }
      }
      // else we are not paged so show all data from start
      else
      {
         m_rowIndex = -1;
         m_pageCount = 1;
         m_maxRowIndex = (rowCount - 1);
      }
      if (s_logger.isDebugEnabled())
         s_logger.debug("Bound datasource: PageSize: " + m_pageSize + "; CurrentPage: " + m_currentPage + "; RowIndex: " + m_rowIndex + "; MaxRowIndex: " + m_maxRowIndex + "; RowCount: " + rowCount);
   }
   
   /**
    * @return A new IRichListRenderer implementation for the current view mode
    */
   public IRichListRenderer getViewRenderer()
   {
      // get type from current view mode, then create an instance of the renderer
      // TODO: set the appropriate IRichListRenderer impl - could come from a config?
      return new RichListRenderer.ListViewRenderer();
   }
   
   /**
    * Return the data model wrapper
    * 
    * @return IGridDataModel 
    */
   private IGridDataModel getDataModel()
   {
      if (m_dataModel == null)
      {
         // TODO: sort first time on initially sorted column - NOTE: can we
         //       do this here or use a different hook point?
         Object val = getValue();
         if (val instanceof List)
         {
            m_dataModel = new GridListDataModel((List)val);
         }
         else if ( (java.lang.Object[].class).isAssignableFrom(val.getClass()) )
         {
            m_dataModel = new GridArrayDataModel((Object[])val);
         }
         else
         {
            throw new IllegalStateException("UIRichList 'value' attribute binding should specify data model of a supported type!"); 
         }
      }
      
      return m_dataModel;
   }
   
   
   // ------------------------------------------------------------------------------
   // Private data
   
   // component state
   private int m_currentPage = 0;
   private int m_rowIndex = -1;
   private String m_sortColumn = null;
   private boolean m_sortDirection = true;
   private int m_pageSize = -1;
   private int m_pageCount = 1;
   private int m_maxRowIndex = -1;
   
   private IGridDataModel m_dataModel = null;
   
   // component properties - NOTE: may use ValueBinding!
   private Object m_value = null;
   
   private static Logger s_logger = Logger.getLogger(IDataContainer.class);
}
