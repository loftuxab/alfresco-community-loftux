/*
 * Created on 04-Apr-2005
 */
package org.alfresco.web.ui.common.renderer;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.alfresco.web.ui.common.Utils;
import org.alfresco.web.ui.common.component.UIActionLink;
import org.alfresco.web.ui.common.component.UIMenu;

/**
 * @author kevinr
 */
public class ActionLinkRenderer extends BaseRenderer
{
   // ------------------------------------------------------------------------------
   // Renderer implementation 
   
   /**
    * @see javax.faces.render.Renderer#decode(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
    */
   public void decode(FacesContext context, UIComponent component)
   {
      Map requestMap = context.getExternalContext().getRequestParameterMap();
      String fieldId = getHiddenFieldName(context, component);
      String value = (String)requestMap.get(fieldId);
      // we are clicked if the hidden field contained our client id
      if (value != null && value.equals(component.getClientId(context)))
      {
         ActionEvent event = new ActionEvent(component);
         component.queueEvent(event);
         
         // get all the params for this actionlink, see if any values have been set
         // on the request which match our params and set them into the component
         UIActionLink link = (UIActionLink)component;
         Map<String, String> destParams = link.getParameterMap();
         destParams.clear();
         Map<String, String> actionParams = getParameterMap(link);
         if (actionParams != null)
         {
            for (String name : actionParams.keySet())
            {
               String paramValue = (String)requestMap.get(name);
               destParams.put(name, paramValue);
            }
         }
      }
   }
   
   /**
    * @see javax.faces.render.Renderer#encodeEnd(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
    */
   public void encodeEnd(FacesContext context, UIComponent component) throws IOException
   {
      // always check for this flag - as per the spec
      if (component.isRendered() == true)
      {
         Writer out = context.getResponseWriter();
         
         UIActionLink link = (UIActionLink)component;
         
         if (isInMenu(link) == true)
         {
            // render as menu item
            out.write( renderMenuAction(context, link) );
         }
         else
         {
            // render as action link
            out.write( renderActionLink(context, link) );
         }
      }
   }
   
   /**
    * Render ActionLink as plain link and image
    * 
    * @param context
    * @param link
    * 
    * @return action link HTML
    */
   private String renderActionLink(FacesContext context, UIActionLink link)
   {
      StringBuilder buf = new StringBuilder(256);
      
      buf.append("<a href='#' onclick=\"");
      // generate JavaScript to set a hidden form field and submit
      // a form which request attributes that we can decode
      buf.append(Utils.generateFormSubmit(context, link, getHiddenFieldName(context, link), link.getClientId(context), getParameterMap(link)));
      buf.append('"');
      Map attrs = link.getAttributes();
      if (attrs.get("style") != null)
      {
         buf.append(" style=\"")
            .append(attrs.get("style"))
            .append('"');
      }
      if (attrs.get("styleClass") != null)
      {
         buf.append(" class=")
            .append(attrs.get("styleClass"));
      }
      buf.append('>');
      
      if (link.getImage() != null)
      {
         int padding = link.getPadding();
         if (padding != 0)
         {
            // need the crappy "cursor:hand" embedded style for IE support
            // TODO: make this width value a property!
            buf.append("<table cellspacing=0 cellpadding=0 style=\"cursor:hand\"><tr><td width=16>");
         }
         
         buf.append(Utils.buildImageTag(context, link.getImage(), (String)link.getValue()));
         
         if (link.getShowLink() == true)
         {
            if (padding != 0)
            {
               buf.append("</td><td style=\"padding:")
                  .append(padding)
                  .append("px\">");
            }
            else
            {
               // TODO: add horizontal spacing as component property
               buf.append("<span style='padding-left:2px");
               
               // text next to an image may need alignment
               if (attrs.get("verticalAlign") != null)
               {
                  buf.append(";vertical-align:")
                     .append(attrs.get("verticalAlign"));
               }
               
               buf.append("'>");
            }
            
            // TODO: encode label value
            buf.append(Utils.encode(link.getValue().toString()));
            
            if (padding == 0)
            {
               buf.append("</span>");
            }
         }
         
         if (padding != 0)
         {
            buf.append("</td></tr></table>");
         }
      }
      else
      {
         buf.append(Utils.encode(link.getValue().toString()));
      }
      
      buf.append("</a>");
      
      return buf.toString();
   }
   
   /**
    * Render ActionLink as menu image and item link
    * 
    * @param context
    * @param link
    * 
    * @return action link HTML
    */
   private String renderMenuAction(FacesContext context, UIActionLink link)
   {
      StringBuilder buf = new StringBuilder(256);
      
      buf.append("<tr><td>");
      
      // render image cell first for a menu
      if (link.getImage() != null)
      {
         buf.append(Utils.buildImageTag(context, link.getImage(), (String)link.getValue()));
      }
      
      buf.append("</td><td");
      int padding = link.getPadding();
      if (padding != 0)
      {
         buf.append(" style=\"padding:")
            .append(padding)
            .append("px\"");
      }
      
      // render text link cell for the menu
      buf.append("><a href='#' onclick=\"");
      buf.append(Utils.generateFormSubmit(context, link, getHiddenFieldName(context, link), link.getClientId(context), getParameterMap(link)));
      buf.append('"');
      Map attrs = link.getAttributes();
      if (attrs.get("style") != null)
      {
         buf.append(" style=\"")
            .append(attrs.get("style"))
            .append('"');
      }
      if (attrs.get("styleClass") != null)
      {
         buf.append(" class=")
            .append(attrs.get("styleClass"));
      }
      buf.append('>');
      buf.append(Utils.encode(link.getValue().toString()));
      buf.append("</a>");
      
      buf.append("</td></tr>");
      
      return buf.toString();
   }
   
   
   // ------------------------------------------------------------------------------
   // Private helpers

   /**
    * Get the hidden field name for this actionlink.
    * Build a shared field name from the parent form name and the string "act".
    * 
    * @return hidden field name shared by all action links within the Form.
    */
   private static String getHiddenFieldName(FacesContext context, UIComponent component)
   {
      return Utils.getParentForm(context, component).getClientId(context) + NamingContainer.SEPARATOR_CHAR + "act";
   }
   
   /**
    * Return true if the action link is present within a UIMenu component container
    * 
    * @param link    The ActionLink to test
    * 
    * @return true if the action link is present within a UIMenu component
    */
   private static boolean isInMenu(UIActionLink link)
   {
      UIComponent parent = link.getParent();
      while (parent != null)
      {
         if (parent instanceof UIMenu)
         {
            break;
         }
         parent = parent.getParent();
      }
      return (parent != null);
   }
}
