/*
 * Created on Mar 7, 2005
 */
package org.alfresco.web.ui.common.renderer.data;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;
import javax.faces.render.Renderer;

import org.alfresco.web.ui.common.renderer.BaseRenderer;

/**
 * @author kevinr
 */
public class TableRenderer extends BaseRenderer
{
   /**
    * @see javax.faces.render.Renderer#encodeBegin(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
    */
   public void encodeBegin(FacesContext context, UIComponent component)
      throws IOException
   {
      // always check for this flag - as per the spec
      if (component.isRendered() == true)
      {
         ResponseWriter out = context.getResponseWriter();
         Map attrs = component.getAttributes();
         out.write("<table");
         outputAttribute(out, attrs.get("styleClass"), "class");
         outputAttribute(out, attrs.get("style"), "style");
         outputAttribute(out, attrs.get("cellpadding"), "cellpadding");
         outputAttribute(out, attrs.get("cellspacing"), "cellspacing");
         out.write(">");
      }
   }
   
   /**
    * @see javax.faces.render.Renderer#encodeEnd(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
    */
   public void encodeEnd(FacesContext context, UIComponent component)
      throws IOException
   {
      // always check for this flag - as per the spec
      if (component.isRendered() == true)
      {
         ResponseWriter out = context.getResponseWriter();
         out.write("</table>");
      }
   }
}
