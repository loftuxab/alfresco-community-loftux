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
import com.activiti.web.jsf.component.data.UIRichList;
import com.activiti.web.jsf.renderer.BaseRenderer;

/**
 * @author kevinr
 */
public class RichListRenderer extends BaseRenderer
{
   // ------------------------------------------------------------------------------
   // Renderer implemenation 
   
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
      // the RichList component we are working with
      UIRichList richList = (UIRichList)component;
      
      // prepare the component current row against the current page settings
      richList.bind();
      
      // collect child column components so they can be passed to the renderer
      List columnList = new ArrayList(8);
      for (Iterator i=richList.getChildren().iterator(); i.hasNext(); /**/)
      {
         UIComponent child = (UIComponent)i.next();
         if (child instanceof UIColumn)
         {
            columnList.add(child);
         }
      }
      
      UIColumn[] columns = new UIColumn[columnList.size()];
      columnList.toArray(columns);
      
      // get the renderer instance
      IRichListRenderer renderer = (IRichListRenderer)richList.getViewRenderer();
      if (renderer == null)
      {
         throw new IllegalStateException("IRichListRenderer must be available in UIRichList!");
      }
      
      // TODO: set the row index as appropriate for the paging state?
      //       the component should be responsible for this!
      
      // TODO: how to render paging controls? e.g. prob don't delegate to the
      //       list render for this - probably a single solution ok
      
      // TODO: rendering sort links etc. - how to wire up events for sort clicks...?
      
      // call render-before to output headers if required
      ResponseWriter out = context.getResponseWriter();
      out.write("<thead>");
      renderer.renderListBefore(context, richList, columns);
      out.write("</thead>");
      out.write("<tbody>");
      while (richList.isDataAvailable() == true)
      {
         // render each row in turn
         renderer.renderListRow(context, richList, columns, richList.nextRow());
      }
      // call render-after to output footers if required
      renderer.renderListAfter(context, richList, columns);
      out.write("</tbody>");
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
   
   
   // ------------------------------------------------------------------------------
   // Inner classes
   
   /**
    * Class to implement a List view for the RichList component
    * 
    * @author kevinr
    */
   public static class ListViewRenderer implements IRichListRenderer
   {
      /**
       * @see com.activiti.web.jsf.renderer.data.IRichListRenderer#renderListBefore(javax.faces.context.FacesContext, com.activiti.web.jsf.component.data.UIColumn[])
       */
      public void renderListBefore(FacesContext context, UIRichList richList, UIColumn[] columns)
            throws IOException
      {
         ResponseWriter out = context.getResponseWriter();
         
         // render column headers as labels
         out.write("<tr>");
         for (int i=0; i<columns.length; i++)
         {
            // render column as appropriate for the list type
            out.write("<th>");
            
            // output the header facet if any
            UIComponent header = columns[i].getHeader();
            if (header != null)
            {
               header.encodeBegin(context);
               header.encodeChildren(context);
               header.encodeEnd(context);
            }
            
            // we don't render child controls for the header row
            out.write("</th>");
         }
         out.write("</tr>");
      }
      
      /**
       * @see com.activiti.web.jsf.renderer.data.IRichListRenderer#renderListRow(javax.faces.context.FacesContext, com.activiti.web.jsf.component.data.UIColumn[], java.lang.Object)
       */
      public void renderListRow(FacesContext context, UIRichList richList, UIColumn[] columns, Object row)
            throws IOException
      {
         ResponseWriter out = context.getResponseWriter();
         
         // TODO: output alt row styles here?
         out.write("<tr>");
         for (int i=0; i<columns.length; i++)
         {
            // render column as appropriate for the list type
            out.write("<td>");
            if (columns[i].getChildCount() != 0)
            {
               // allow child controls inside the columns to render themselves
               encodeRecursive(context, columns[i]);
            }
            out.write("</td>");
         }
         out.write("</tr>");
      }
      
      /**
       * @see com.activiti.web.jsf.renderer.data.IRichListRenderer#renderListAfter(javax.faces.context.FacesContext, com.activiti.web.jsf.component.data.UIColumn[])
       */
      public void renderListAfter(FacesContext context, UIRichList richList, UIColumn[] columns)
            throws IOException
      {
         ResponseWriter out = context.getResponseWriter();
         
         out.write("<tr><td colspan=10>---footer---</td></tr>");
      }
   }
   
   
   /**
    * Class to implement a Details view for the RichList component
    * 
    * @author kevinr
    */
   public static class DetailsViewRenderer implements IRichListRenderer
   {
      /**
       * @see com.activiti.web.jsf.renderer.data.IRichListRenderer#renderListBefore(javax.faces.context.FacesContext, com.activiti.web.jsf.component.data.UIColumn[])
       */
      public void renderListBefore(FacesContext context, UIRichList richList, UIColumn[] columns)
            throws IOException
      {
         // TODO Auto-generated method stub
      }
      
      /**
       * @see com.activiti.web.jsf.renderer.data.IRichListRenderer#renderListAfter(javax.faces.context.FacesContext, com.activiti.web.jsf.component.data.UIColumn[])
       */
      public void renderListAfter(FacesContext context, UIRichList richList, UIColumn[] columns)
            throws IOException
      {
         // TODO Auto-generated method stub
      }
      
      /**
       * @see com.activiti.web.jsf.renderer.data.IRichListRenderer#renderListRow(javax.faces.context.FacesContext, com.activiti.web.jsf.component.data.UIColumn[], java.lang.Object)
       */
      public void renderListRow(FacesContext context, UIRichList richList, UIColumn[] columns, Object row) throws IOException
      {
         // TODO Auto-generated method stub
      }
   }
   
   
   /**
    * Class to implement an Icon view for the RichList component
    * 
    * @author kevinr
    */
   public static class IconViewRenderer implements IRichListRenderer
   {
      /**
       * @see com.activiti.web.jsf.renderer.data.IRichListRenderer#renderListBefore(javax.faces.context.FacesContext, com.activiti.web.jsf.component.data.UIColumn[])
       */
      public void renderListBefore(FacesContext context, UIRichList richList, UIColumn[] columns)
            throws IOException
      {
         // TODO Auto-generated method stub
      }
      
      /**
       * @see com.activiti.web.jsf.renderer.data.IRichListRenderer#renderListAfter(javax.faces.context.FacesContext, com.activiti.web.jsf.component.data.UIColumn[])
       */
      public void renderListAfter(FacesContext context, UIRichList richList, UIColumn[] columns)
            throws IOException
      {
         // TODO Auto-generated method stub
      }

      /**
       * @see com.activiti.web.jsf.renderer.data.IRichListRenderer#renderListRow(javax.faces.context.FacesContext, com.activiti.web.jsf.component.data.UIColumn[], java.lang.Object)
       */
      public void renderListRow(FacesContext context, UIRichList richList, UIColumn[] columns, Object row) throws IOException
      {
         // TODO Auto-generated method stub
      }
   }
}
