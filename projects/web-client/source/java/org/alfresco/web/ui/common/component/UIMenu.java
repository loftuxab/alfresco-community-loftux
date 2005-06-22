/*
 * Created on 11-Apr-2005
 */
package org.alfresco.web.ui.common.component;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.alfresco.web.ui.common.Utils;

/**
 * @author Kevin Roast
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
      return "org.alfresco.faces.Controls";
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
      String menuId = getNextMenuId(context);
      out.write("<a href='#' onclick=\"javascript:_toggleMenu('");
      out.write(menuId);
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
         out.write(Utils.buildImageTag(context, (String)getAttributes().get("image"), null, "absmiddle"));
      }
      
      out.write("</a>");
      
      // output the hidden DIV section to contain the menu item table
      // also output the javascript handlers used to hide the menu after a delay of non-use
      out.write("<br><div id='");
      out.write(menuId);
      out.write("' onmouseover=\"javascript:_menuIn('");
      out.write(menuId);
      out.write("');\" onmouseout=\"javascript:_menuOut('");
      out.write(menuId);
      out.write("');\"");
      out.write(" style=\"position:absolute;display:none;padding-left:2px;\">");
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
    * Return the next usable menu DIV id in a sequence
    * 
    * @param context       FacesContext
    * 
    * @return next menu ID
    */
   private String getNextMenuId(FacesContext context)
   {
      Integer val = (Integer)context.getExternalContext().getRequestMap().get(MENU_ID_KEY);
      if (val == null)
      {
         val = Integer.valueOf(0);
      }
      
      // build next id in sequence
      String id = getClientId(context) + '_' + val.toString();
      
      // save incremented value in the request ready for next menu component instance
      val = Integer.valueOf( val.intValue() + 1 );
      context.getExternalContext().getRequestMap().put(MENU_ID_KEY, val);
      
      return id;
   }
   
   
   // ------------------------------------------------------------------------------
   // Private members
   
   private final static String MENU_ID_KEY = "__awc_menu_id";
}
