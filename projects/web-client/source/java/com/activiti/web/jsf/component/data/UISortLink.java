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
import javax.faces.event.ActionEvent;

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
      out.write("<a href=\"#\" onclick=\"");
      // generate some hooky JavaScript to set a hidden form field and submit
      // a form which request attributes that we can decode
      out.write(Utils.generateFormSubmit(context, this, getHiddenFieldName(context), getClientId(context)));
      out.write("\">");
      
      out.write( (String)getAttributes().get("label") );
      
      out.write("</a>");
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
         s_logger.debug("Caught sort click using field: " + fieldId + "; for sort link Id: " + getClientId(context));
         // we were clicked - queue an event to represent the click
         String column = null;      // TODO: get column from request args!
         SortEvent actionEvent = new SortEvent(this, column);
         this.queueEvent(actionEvent);
         // TODO: where do we handle events - broadcast()???
         //       want to inform the parent IDataContainer of the sort request
         //
         //       OR: do we just findComponent() and simply call a method now!?!
         //
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
   
   private static Logger s_logger = Logger.getLogger(IDataContainer.class);
}
