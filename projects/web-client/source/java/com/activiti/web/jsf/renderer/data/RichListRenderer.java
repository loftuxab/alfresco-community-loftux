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
         out.write("<table cellspacing=0 cellpadding=0");
         outputAttribute(out, attrs.get("styleClass"), "class");
         outputAttribute(out, attrs.get("style"), "style");
         outputAttribute(out, attrs.get("width"), "width");
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
         ResponseWriter out = context.getResponseWriter();
         
         // render column headers as labels
         out.write("<tr>");
         for (int i=0; i<columns.length; i++)
         {
            UIColumn column = columns[i];
            
            // render column as appropriate for the list type
            out.write("<th");
            outputAttribute(out, column.getAttributes().get("width"), "width");
            outputAttribute(out, column.getAttributes().get("style"), "style");
            outputAttribute(out, column.getAttributes().get("styleClass"), "class");
            out.write('>');
            
            // output the header facet if any
            UIComponent header = column.getHeader();
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
         
         this.rowIndex = 0;
      }
      
      /**
       * @see com.activiti.web.jsf.renderer.data.IRichListRenderer#renderListRow(javax.faces.context.FacesContext, com.activiti.web.jsf.component.data.UIColumn[], java.lang.Object)
       */
      public void renderListRow(FacesContext context, UIRichList richList, UIColumn[] columns, Object row)
            throws IOException
      {
         ResponseWriter out = context.getResponseWriter();
         
         // output row or alt style row if set
         out.write("<tr");
         String style = (String)richList.getAttributes().get("rowStyleClass");
         String altStyle = (String)richList.getAttributes().get("altRowStyleClass");
         if (altStyle != null && this.rowIndex++ % 2 == 1)
         {
            style = altStyle;
         }         
         outputAttribute(out, style, "class");
         out.write('>');
         
         // output each column in turn and render all children
         for (int i=0; i<columns.length; i++)
         {
            UIColumn column = columns[i];
            
            out.write("<td");
            outputAttribute(out, column.getAttributes().get("style"), "style");
            outputAttribute(out, column.getAttributes().get("styleClass"), "class");
            out.write('>');
            
            // for details view, we show the small column icon for the first column
            if (i == 0)
            {
               UIComponent smallIcon = column.getSmallIcon();
               if (smallIcon != null)
               {
                  smallIcon.encodeBegin(context);
                  smallIcon.encodeChildren(context);
                  smallIcon.encodeEnd(context);
                  out.write("&nbsp;");
               }
            }
            
            if (column.getChildCount() != 0)
            {
               // allow child controls inside the columns to render themselves
               encodeRecursive(context, column);
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
         
         out.write("<tr><td colspan=99 align=right>");
         for (Iterator i=richList.getChildren().iterator(); i.hasNext(); /**/)
         {
            // output all remaining child components that are not UIColumn
            UIComponent child = (UIComponent)i.next();
            if (child instanceof UIColumn == false)
            {
               encodeRecursive(context, child);
            }
         }
         out.write("</td></tr>");
      }
      
      private int rowIndex = 0;
   }
   
   
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
      final static int COLUMNS = 3;
      final static String COLUMN_PERCENT = Integer.toString(100/COLUMNS) + "%";
      
      /**
       * @see com.activiti.web.jsf.renderer.data.IRichListRenderer#renderListBefore(javax.faces.context.FacesContext, com.activiti.web.jsf.component.data.UIColumn[])
       */
      public void renderListBefore(FacesContext context, UIRichList richList, UIColumn[] columns)
            throws IOException
      {
         // no headers for this renderer
         this.rowIndex = 0;
      }
      
      /**
       * @see com.activiti.web.jsf.renderer.data.IRichListRenderer#renderListRow(javax.faces.context.FacesContext, com.activiti.web.jsf.component.data.UIColumn[], java.lang.Object)
       */
      public void renderListRow(FacesContext context, UIRichList richList, UIColumn[] columns, Object row)
            throws IOException
      {
         ResponseWriter out = context.getResponseWriter();
         
         // start new row as per number of columns in this icon view
         if (this.rowIndex % COLUMNS == 0)
         {
            out.write("<tr");
            outputAttribute(out, richList.getAttributes().get("rowStyleClass"), "class");
            out.write('>');
         }
         
         // output first column as the icon label
         out.write("<td width=");
         out.write(COLUMN_PERCENT);
         out.write("><table cellspacing=0 cellpadding=2 border=0>");
         if (columns.length != 0)
         {
            UIColumn column = columns[0];
            
            out.write("<tr><td rowspan=99");
            outputAttribute(out, column.getAttributes().get("style"), "style");
            outputAttribute(out, column.getAttributes().get("styleClass"), "class");
            out.write('>');
            
            // output the large icon for this column
            UIComponent largeIcon = column.getLargeIcon();
            if (largeIcon != null)
            {
               largeIcon.encodeBegin(context);
               largeIcon.encodeChildren(context);
               largeIcon.encodeEnd(context);
            }
            out.write("</td>");
            
            // start the next cell which contains the first column component
            out.write("<td align=top");
            outputAttribute(out, column.getAttributes().get("style"), "style");
            outputAttribute(out, column.getAttributes().get("styleClass"), "class");
            out.write('>');
            if (column.getChildCount() != 0)
            {
               // allow child controls inside the columns to render themselves
               encodeRecursive(context, column);
            }
            out.write("</td></tr>");
         }
         
         // render remaining columns up to a max reasonable display limit
         for (int i=1; i<columns.length && i<3; i++)
         {
            UIColumn column = columns[i];
            
            out.write("<tr><td align=top");
            outputAttribute(out, column.getAttributes().get("style"), "style");
            outputAttribute(out, column.getAttributes().get("styleClass"), "class");
            out.write('>');
            if (column.getChildCount() != 0)
            {
               // allow child controls inside the columns to render themselves
               encodeRecursive(context, column);
            }
            out.write("</td></tr>");
         }
         
         out.write("</table></td>");
         
         if (this.rowIndex % COLUMNS == COLUMNS-1)
         {
            out.write("</tr>");
         }
         
         this.rowIndex++;
      }
      
      /**
       * @see com.activiti.web.jsf.renderer.data.IRichListRenderer#renderListAfter(javax.faces.context.FacesContext, com.activiti.web.jsf.component.data.UIColumn[])
       */
      public void renderListAfter(FacesContext context, UIRichList richList, UIColumn[] columns)
            throws IOException
      {
         ResponseWriter out = context.getResponseWriter();
         
         // finish last row if required (as we used open-ended column rendering)
         if ((this.rowIndex-1) % COLUMNS != COLUMNS-1)
         {
            out.write("</tr>");
         }
         
         out.write("<tr><td colspan=99 align=right>");
         for (Iterator i=richList.getChildren().iterator(); i.hasNext(); /**/)
         {
            // output all remaining child components that are not UIColumn
            UIComponent child = (UIComponent)i.next();
            if (child instanceof UIColumn == false)
            {
               encodeRecursive(context, child);
            }
         }
         out.write("</td></tr>");
      }
      
      private int rowIndex = 0;
   }
}
