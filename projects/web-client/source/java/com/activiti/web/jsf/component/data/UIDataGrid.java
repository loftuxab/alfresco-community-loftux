package com.activiti.web.jsf.component.data;

import java.io.IOException;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.activiti.web.jsf.component.SelfRenderingComponent;


/**
 * @author kevinr
 */
public class UIDataGrid extends SelfRenderingComponent
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
}
