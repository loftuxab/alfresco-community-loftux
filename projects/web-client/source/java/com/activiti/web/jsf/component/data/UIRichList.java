/*
 * Created on Mar 11, 2005
 */
package com.activiti.web.jsf.component.data;

import java.util.List;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import com.activiti.web.jsf.renderer.data.IRichListRenderer;
import com.activiti.web.jsf.renderer.data.RichListRenderer;


/**
 * @author kevinr
 */
public class UIRichList extends UIComponentBase
{
   // ------------------------------------------------------------------------------
   // Construction
   
   /**
    * Default constructor
    */
   public UIRichList()
   {
      setRendererType("awc.faces.RichListRenderer");
      
      // TODO: set the default IRichListRenderer impl - could come from a config?
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
   }
   
   /**
    * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
    */
   public Object saveState(FacesContext context)
   {
      Object values[] = new Object[8];
      // standard component attributes are saved by the super class
      values[0] = super.saveState(context);
      values[1] = new Integer(m_currentPage);
      return (values);
   }
   
   
   // ------------------------------------------------------------------------------
   // UIRichList implementation
   
   /**
    * @return A new IRichListRenderer implementation for the current view mode
    */
   public IRichListRenderer getViewRenderer()
   {
      // get type from current view mode, then create an instance of the renderer
      return new RichListRenderer.ListViewRenderer();
   }
   
   /**
    * Method called to bind the RichList component state to the data model value
    */
   public void bind()
   {
      Integer pageSize = (Integer)getAttributes().get("pageSize");
      // if a page size is specified, then we use that
      if (pageSize != null)
      {
         m_rowIndex = (m_currentPage * pageSize.intValue()) - 1;
      }
      // else we are not paged so show all data from start
      else
      {
         m_rowIndex = -1;
      }
   }
   
   public boolean isDataAvailable()
   {
      return m_rowIndex < (getDataModel().size() - 1);
   }
   
   public Object nextRow()
   {
      return getDataModel().getRow(++m_rowIndex);
   }
   
   public int getCurrentPage()
   {
      return m_currentPage;
   }
   
   private IGridDataModel getDataModel()
   {
      if (m_dataModel == null)
      {
         Object val = getAttributes().get("value");
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
   
   private int m_currentPage = 0;
   private int m_rowIndex = -1;
   private IGridDataModel m_dataModel = null;
}
