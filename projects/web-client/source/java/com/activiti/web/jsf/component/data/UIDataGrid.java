package com.activiti.web.jsf.component.data;

import java.io.IOException;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.activiti.web.data.IDataContainer;
import com.activiti.web.jsf.component.SelfRenderingComponent;


/**
 * @author kevinr
 */
public class UIDataGrid extends SelfRenderingComponent implements IDataContainer
{
   /**
    * @see javax.faces.component.UIComponent#getFamily()
    */
   public String getFamily()
   {
      return "awc.faces.Data";
   }
   
   /**
    * @see javax.faces.component.UIComponent#encodeBegin(javax.faces.context.FacesContext)
    */
   public void encodeBegin(FacesContext context) throws IOException
   {
      // always check for this flag - as per the spec
      if (this.isRendered() == true)
      {
         ResponseWriter out = context.getResponseWriter();
         Map attrs = this.getAttributes();
         out.write("<table");
         outputAttribute(out, attrs.get("styleClass"), "class");
         outputAttribute(out, attrs.get("style"), "style");
         outputAttribute(out, attrs.get("cellpadding"), "cellpadding");
         outputAttribute(out, attrs.get("cellspacing"), "cellspacing");
         out.write(">");
      }
   }
   
   /**
    * @see javax.faces.component.UIComponent#encodeEnd(javax.faces.context.FacesContext)
    */
   public void encodeEnd(FacesContext context) throws IOException
   {
      // always check for this flag - as per the spec
      if (this.isRendered() == true)
      {
         ResponseWriter out = context.getResponseWriter();
         out.write("</table>");
      }
   }

   /**
    * @see com.activiti.web.data.IDataContainer#getCurrentPage()
    */
   public int getCurrentPage()
   {
      // TODO Auto-generated method stub
      return 0;
   }

   /**
    * @see com.activiti.web.data.IDataContainer#getCurrentSortColumn()
    */
   public String getCurrentSortColumn()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see com.activiti.web.data.IDataContainer#getCurrentSortDirection()
    */
   public boolean getCurrentSortDirection()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see com.activiti.web.data.IDataContainer#getPageSize()
    */
   public int getPageSize()
   {
      // TODO Auto-generated method stub
      return 0;
   }

   /**
    * @see com.activiti.web.data.IDataContainer#isDataAvailable()
    */
   public boolean isDataAvailable()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see com.activiti.web.data.IDataContainer#nextRow()
    */
   public Object nextRow()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see com.activiti.web.data.IDataContainer#sort(java.lang.String, boolean, java.lang.String)
    */
   public void sort(String column, boolean bAscending, String mode)
   {
   }
}
