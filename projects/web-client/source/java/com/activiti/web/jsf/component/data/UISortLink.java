/*
 * Created on Mar 15, 2005
 */
package com.activiti.web.jsf.component.data;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.NamingContainer;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesEvent;

import org.apache.log4j.Logger;

import com.activiti.web.data.IDataContainer;
import com.activiti.web.jsf.Utils;

/**
 * @author kevinr
 */
public class UISortLink extends UICommand
{
   /**
    * @see javax.faces.component.UIComponent#encodeBegin(javax.faces.context.FacesContext)
    */
   public void encodeBegin(FacesContext context) throws IOException
   {
      ResponseWriter out = context.getResponseWriter();
      
      IDataContainer dataContainer = getDataContainer();
      if (dataContainer == null)
      {
         throw new IllegalStateException("Must nest UISortLink inside component implementing IDataContainer!"); 
      }
      
      // swap sort direction if we were last sorted column
      boolean bPreviouslySorted = false;
      boolean bAscending = true;
      String lastSortedColumn = dataContainer.getCurrentSortColumn();
      if (lastSortedColumn == (String)getValue())
      {
         bAscending = !dataContainer.getCurrentSortDirection();
         bPreviouslySorted = true;
      }
      
      // render sort link
      StringBuffer buf = new StringBuffer(256);
      buf.append("<a href='#' onclick=\"");
      // generate some JavaScript to set a hidden form field and submit
      // a form which request attributes that we can decode
      buf.append(Utils.generateFormSubmit(context, this, getHiddenFieldName(context), getClientId(context)));
      buf.append('"');
      
      if (getAttributes().get("style") != null)
      {
         buf.append(" style='")
            .append(getAttributes().get("style"))
            .append('\'');
      }
      if (getAttributes().get("styleClass") != null)
      {
         buf.append(" class=")
            .append(getAttributes().get("styleClass"));
      }
      if (getAttributes().get("title") != null)
      {
         buf.append(" title='")
            .append(getAttributes().get("title"))
            .append("' alt='")
            .append(getAttributes().get("title"))
            .append('\'');
      }
      buf.append('>');
      
      // output column label
      buf.append((String)getAttributes().get("label"));
      
      if (bPreviouslySorted == true)
      {
         if (bAscending == true)
         {
            buf.append("&nbsp;")
               .append(Utils.buildImageTag(context, IMAGE_SORTUP, 10, 6, null));
         }
         else
         {
            buf.append("&nbsp;")
               .append(Utils.buildImageTag(context, IMAGE_SORTDOWN, 10, 6, null));
         }
      }
      else
      {
         buf.append("&nbsp;")
            .append(Utils.buildImageTag(context, IMAGE_SORTNONE, 10, 7, null));
      }
      buf.append("</a>");
      
      out.write(buf.toString());
   }
   
   /**
    * @see javax.faces.component.UIComponent#encodeEnd(javax.faces.context.FacesContext)
    */
   public void encodeEnd(FacesContext context) throws IOException
   {
   }
   
   /**
    * @see javax.faces.component.UIComponent#decode(javax.faces.context.FacesContext)
    */
   public void decode(FacesContext context)
   {
      Map requestMap = context.getExternalContext().getRequestParameterMap();
      String fieldId = getHiddenFieldName(context);
      String value = (String)requestMap.get(fieldId);
      if (value != null && value.equals(getClientId(context)))
      {
         if (s_logger.isDebugEnabled())
            s_logger.debug("Caught sort click using field: " + fieldId + "; for sort link Id: " + getClientId(context));
         // we were clicked - queue an event to represent the click
         // cannot handle the event here as other components etc. have not had
         // a chance to decode() - we queue an event to be processed later
         SortEvent actionEvent = new SortEvent(this, (String)this.getValue());
         this.queueEvent(actionEvent);
      }
   }
   
   /**
    * @see javax.faces.component.UIComponent#broadcast(javax.faces.event.FacesEvent)
    */
   public void broadcast(FacesEvent event) throws AbortProcessingException
   {
      if (event instanceof SortEvent == false)
      {
         // let the super class handle events which we know nothing about
         super.broadcast(event);
      }
      else if ( ((SortEvent)event).Column.equals(getColumn()) )
      {
         // found a sort event for us!
         if (s_logger.isDebugEnabled())
            s_logger.debug("Handling sort event for column: " + ((SortEvent)event).Column);
         if (getColumn().equals(getDataContainer().getCurrentSortColumn()) == true)
         {
            // reverse sort direction
            m_bAscending = !m_bAscending;
         }
         getDataContainer().sort(getColumn(), m_bAscending, getMode());
      }  
   }
   
   /**
    * We use a hidden field name based on the parent data container component Id and
    * the string "sort" to give a field name that can be shared by all sort links
    * within a single data container component.
    * 
    * @param context    FacesContext
    * 
    * @return hidden field name
    */
   private String getHiddenFieldName(FacesContext context)
   {
      UIComponent dataContainer = (UIComponent)Utils.getParentDataContainer(context, this);
      return dataContainer.getClientId(context) + NamingContainer.SEPARATOR_CHAR + "sort";
   }
   
   public String getColumn()
   {
      return (String)getValue();
   }
   
   public String getMode()
   {
      return m_mode;
   }
   
   public void setMode(String sortMode)
   {
      m_mode = sortMode;
   }
   
   public boolean isAscending()
   {
      return m_bAscending;
   }
   
   /**
    * @see javax.faces.component.StateHolder#restoreState(javax.faces.context.FacesContext, java.lang.Object)
    */
   public void restoreState(FacesContext context, Object state)
   {
      Object values[] = (Object[])state;
      // standard component attributes are restored by the super class
      super.restoreState(context, values[0]);
      m_bAscending = ((Boolean)values[1]).booleanValue();
   }
   
   /**
    * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
    */
   public Object saveState(FacesContext context)
   {
      Object values[] = new Object[2];
      // standard component attributes are saved by the super class
      values[0] = super.saveState(context);
      values[1] = (m_bAscending ? Boolean.TRUE : Boolean.FALSE);
      return values;
   }
   
   private IDataContainer getDataContainer()
   {
      return Utils.getParentDataContainer(getFacesContext(), this);
   }
   
   
   // ------------------------------------------------------------------------------
   // Inner classes
   
   /**
    * Class representing the clicking of a sortable column.
    */
   private static class SortEvent extends ActionEvent
   {
      public SortEvent(UIComponent component, String column)
      {
         super(component);
         Column = column;
      }
      
      public String Column = null;
   }
   
   
   // ------------------------------------------------------------------------------
   // Constants
   
   /** separator between encoded sort values */
   public final static char SEPARATOR = ',';
   
   private static Logger s_logger = Logger.getLogger(IDataContainer.class);
   
   private final static String IMAGE_SORTUP     = "/images/sort_up.gif";
   private final static String IMAGE_SORTDOWN   = "/images/sort_down.gif";
   private final static String IMAGE_SORTNONE   = "/images/sort_flat.gif";
   
   private String m_mode = IDataContainer.SORT_CASEINSENSITIVE;
   private boolean m_bAscending = true;
}
