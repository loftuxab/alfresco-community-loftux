/*
 * Created on 11-Apr-2005
 */
package com.activiti.web.jsf.component;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.activiti.web.jsf.Utils;

/**
 * @author kevinr
 */
public class UIMenu extends SelfRenderingComponent
{
   // ------------------------------------------------------------------------------
   // Component Impl 
   
   /**
    * @see javax.faces.component.UIComponent#getFamily()
    */
   public String getFamily()
   {
      return "awc.faces.Controls";
   }

   /**
    * @see javax.faces.component.UIComponentBase#encodeBegin(javax.faces.context.FacesContext)
    */
   public void encodeBegin(FacesContext context) throws IOException
   {
      if (isRendered() == false)
      {
         return;
      }
      
      ResponseWriter out = context.getResponseWriter();
      
      // output a textual label with an optional icon to show the menu
      out.write("<a href='#' onclick=\"javascript:_toggleMenu('");
      out.write(getClientId(context));
      out.write("');return false;\"");
      outputAttribute(out, getAttributes().get("style"), "style");
      outputAttribute(out, getAttributes().get("styleClass"), "class");
      outputAttribute(out, getAttributes().get("tooltip"), "title");
      out.write('>');
      
      // output label text
      if (getAttributes().get("label") != null)
      {
         out.write("<span>");
         out.write(Utils.encode((String)getAttributes().get("label")));
         out.write("</span>");
      }
      
      // output image
      if (getAttributes().get("image") != null)
      {
         out.write(Utils.buildImageTag(context, (String)getAttributes().get("image"), null));
      }
      
      out.write("</a>");
      
      // output the hidden DIV section to contain the menu item table
      out.write("<div id=\"");
      out.write(getClientId(context));
      out.write("\" style=\"position:absolute;display:none;padding-left:2px;\">");
      out.write("<table border=0 cellpadding=0");
      outputAttribute(out, getAttributes().get("itemSpacing"), "cellspacing");
      outputAttribute(out, getAttributes().get("menuStyle"), "style");
      outputAttribute(out, getAttributes().get("menuStyleClass"), "class");
      out.write(">");
   }

   /**
    * @see javax.faces.component.UIComponentBase#encodeEnd(javax.faces.context.FacesContext)
    */
   public void encodeEnd(FacesContext context) throws IOException
   {
      if (isRendered() == false)
      {
         return;
      }
      
      ResponseWriter out = context.getResponseWriter();
      
      // end the menu table and the hidden DIV section
      out.write("</table></div>");
   }
   
   
   // ------------------------------------------------------------------------------
   // Strongly typed component property accessors
   
   
   // ------------------------------------------------------------------------------
   // Private helpers
   
   /**
    * Get the hidden field name for this menu.
    * Build a shared field name from the parent form name and the string "menu".
    * 
    * @return hidden field name shared by all menus within the Form.
    */
   private static String getHiddenFieldName(FacesContext context, UIComponent component)
   {
      return Utils.getParentForm(context, component).getClientId(context) + NamingContainer.SEPARATOR_CHAR + "menu";
   }
   
   
   // ------------------------------------------------------------------------------
   // Private members
}
