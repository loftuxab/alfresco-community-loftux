/*
 * Created on Mar 11, 2005
 */
package com.activiti.web.jsf.renderer.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.activiti.web.jsf.component.data.UIColumn;
import com.activiti.web.jsf.renderer.BaseRenderer;

/**
 * @author kevinr
 */
public class RichListRenderer extends BaseRenderer
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
         out.write(">");
      }
   }
   
   /**
    * @see javax.faces.render.Renderer#encodeChildren(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
    */
   public void encodeChildren(FacesContext context, UIComponent component)
         throws IOException
   {
      for (Iterator i=component.getChildren().iterator(); i.hasNext(); /**/)
      {
         UIComponent child = (UIComponent)i.next();
         
         // get column components here
         // then render the view as appropriate
         List columnList = new ArrayList(8);
         if (child instanceof UIColumn)
         {
            columnList.add(child);
         }
         
         UIColumn[] columns = new UIColumn[columnList.size()];
         columnList.toArray(columns);
         
         IRichListRenderer renderer = (IRichListRenderer)component.getAttributes().get("viewRenderer");
         renderer.renderList(context, columns);
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
   
   /**
    * @see javax.faces.render.Renderer#getRendersChildren()
    */
   public boolean getRendersChildren()
   {
      // we are responsible for rendering our child components
      // this renderer is a valid use of this mode - it can render the various
      // column descriptors as a number of different list view types e.g.
      // details, icons, list etc.
      return true;
   }
   
   
   public static class ListViewRenderer implements IRichListRenderer
   {
      /**
       * @see com.activiti.web.jsf.renderer.data.IRichListRenderer#renderList(com.activiti.web.jsf.component.data.UIColumn[])
       */
      public void renderList(FacesContext context, UIColumn[] columns)
         throws IOException
      {
         ResponseWriter out = context.getResponseWriter();
         
         for (int i=0; i<columns.length; i++)
         {
            // render column as appropriate for the list type
            out.write( (String)columns[i].getAttributes().get("label") );
            
            if (columns[i].getChildCount() != 0)
            {
               // allow child controls inside the columns to render
               encodeRecursive(context, columns[i]);
            }
         }
      }
   }
   
   public static class DetailsViewRenderer implements IRichListRenderer
   {
      /**
       * @see com.activiti.web.jsf.renderer.data.IRichListRenderer#renderList(com.activiti.web.jsf.component.data.UIColumn[])
       */
      public void renderList(FacesContext context, UIColumn[] columns)
         throws IOException
      {
      }
   }
   
   public static class IconViewRenderer implements IRichListRenderer
   {
      /**
       * @see com.activiti.web.jsf.renderer.data.IRichListRenderer#renderList(com.activiti.web.jsf.component.data.UIColumn[])
       */
      public void renderList(FacesContext context, UIColumn[] columns)
         throws IOException
      {
      }
   }
}
