package org.alfresco.web.ui.common.component.data;

import java.io.IOException;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.alfresco.web.data.IDataContainer;
import org.alfresco.web.ui.common.component.SelfRenderingComponent;


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
    * @see org.alfresco.web.data.IDataContainer#getCurrentPage()
    */
   public int getCurrentPage()
   {
      // TODO Auto-generated method stub
      return 0;
   }

   /**
    * @see org.alfresco.web.data.IDataContainer#setCurrentPage(int)
    */
   public void setCurrentPage(int index)
   {
      // TODO Auto-generated method stub
   }

   /**
    * @see org.alfresco.web.data.IDataContainer#getCurrentSortColumn()
    */
   public String getCurrentSortColumn()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.alfresco.web.data.IDataContainer#isCurrentSortDescending()
    */
   public boolean isCurrentSortDescending()
   {
      // TODO Auto-generated method stub
      return true;
   }

   /**
    * @see org.alfresco.web.data.IDataContainer#getPageSize()
    */
   public int getPageSize()
   {
      // TODO Auto-generated method stub
      return 0;
   }

   /**
    * @see org.alfresco.web.data.IDataContainer#getPageCount()
    */
   public int getPageCount()
   {
      // TODO Auto-generated method stub
      return 1;
   }

   /**
    * @see org.alfresco.web.data.IDataContainer#isDataAvailable()
    */
   public boolean isDataAvailable()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.alfresco.web.data.IDataContainer#nextRow()
    */
   public Object nextRow()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.alfresco.web.data.IDataContainer#sort(java.lang.String, boolean, java.lang.String)
    */
   public void sort(String column, boolean bAscending, String mode)
   {
   }
}
