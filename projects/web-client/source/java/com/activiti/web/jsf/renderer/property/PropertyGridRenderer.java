package com.activiti.web.jsf.renderer.property;

import java.io.IOException;
import java.util.Iterator;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.apache.log4j.Logger;
import com.activiti.web.jsf.component.property.UIProperty;
import com.activiti.web.jsf.renderer.BaseRenderer;

/**
 * A renderer to generate the HTML for a Property sheet
 * 
 * @author gavinc
 */
public class PropertyGridRenderer extends BaseRenderer
{
   // *********************************************************
   // TODO: Try replacing this with the standard Grid renderer
   // *********************************************************
   
   private static Logger s_logger = Logger.getLogger(PropertyGridRenderer.class);
   
   /**
    * @see javax.faces.render.Renderer#getRendersChildren()
    */
   public boolean getRendersChildren()
   {
      // we are responsible for rendering our child components
      return true;
   }
   
   /**
    * @see javax.faces.render.Renderer#encodeBegin(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
    */
   public void encodeBegin(FacesContext context, UIComponent component)
            throws IOException
   {
      if (component.isRendered())
      {
         ResponseWriter out = context.getResponseWriter();
         out.write("<table border='1'>");
      }
   }

   /**
    * @see javax.faces.render.Renderer#encodeChildren(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
    */
   public void encodeChildren(FacesContext context, UIComponent component)
            throws IOException
   {
      ResponseWriter out = context.getResponseWriter();
      
      Iterator iter = component.getChildren().iterator();
      while (iter.hasNext())
      {
         UIComponent prop = (UIComponent)iter.next();
         if (prop instanceof UIProperty)
         {
            out.write("<tr>");
            Iterator propKids = prop.getChildren().iterator();
            while (propKids.hasNext())
            {
               out.write("<td>");
               UIComponent comp = (UIComponent)propKids.next();
               encodeRecursive(context, comp);
               out.write("</td>");
            }
            out.write("</tr>\n");
         }
      }
   }
   /**
    * @see javax.faces.render.Renderer#encodeEnd(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
    */
   public void encodeEnd(FacesContext context, UIComponent component)
            throws IOException
   {
      if (component.isRendered())
      {
         ResponseWriter out = context.getResponseWriter();
         out.write("</table>");
      }
   }
}
