/*
 * Created on 01-Apr-2005
 */
package com.activiti.web.jsf.renderer;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.StringTokenizer;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;

import com.activiti.web.jsf.Utils;
import com.activiti.web.jsf.component.UIBreadcrumb;

/**
 * Renderer class for the UIBreadcrumb component
 * 
 * @author kevinr
 */
public class BreadcrumbRenderer extends BaseRenderer
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
      if (value != null && value.length() != 0)
      {
         // create a breadcrumb specific action event if we were clicked
         int selectedIndex = Integer.parseInt(value);
         UIBreadcrumb.BreadcrumbEvent event = new UIBreadcrumb.BreadcrumbEvent(component, selectedIndex);
         component.queueEvent(event);
      }
   }

   /**
    * @see javax.faces.render.Renderer#encodeBegin(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
    */
   public void encodeBegin(FacesContext context, UIComponent component) throws IOException
   {
      // always check for this flag - as per the spec
      if (component.isRendered() == true)
      {
         Writer out = context.getResponseWriter();
         
         UIBreadcrumb breadcrumb = (UIBreadcrumb)component;
         String path = (String)breadcrumb.getValue();
         if (path != null)
         {
            int index = 0;
            boolean first = true;
            StringTokenizer t = new StringTokenizer(path, UIBreadcrumb.SEPARATOR);
            while (t.hasMoreTokens() == true)
            {
               String element = t.nextToken();
               // handle not optionally hiding the root part
               if (index != 0 || breadcrumb.getShowRoot() == true)
               {
                  out.write( renderBreadcrumb(context, breadcrumb, element, index, first) );
                  first = false;
               }
               index++;
            }
         }
      }
   }
   
   
   // ------------------------------------------------------------------------------
   // Private helpers
   
   private String renderBreadcrumb(FacesContext context, UIBreadcrumb bc, String element, int index, boolean first)
   {
      // render breadcrumb link element
      StringBuilder buf = new StringBuilder(256);
      
      // output separator
      if (first == false)
      {
         buf.append(' ')
            .append(bc.getSeparator())
            .append(' ');
      }
      
      // generate JavaScript to set a hidden form field and submit
      // a form which request attributes that we can decode
      buf.append("<a href='#' onclick=\"");
      buf.append(Utils.generateFormSubmit(context, bc, getHiddenFieldName(context, bc), Integer.toString(index)));
      buf.append('"');
      
      if (bc.getAttributes().get("style") != null)
      {
         buf.append(" style='")
            .append(bc.getAttributes().get("style"))
            .append('\'');
      }
      if (bc.getAttributes().get("styleClass") != null)
      {
         buf.append(" class=")
            .append(bc.getAttributes().get("styleClass"));
      }
      buf.append('>');
      
      // output path element text
      // TODO: optionally crop text length with ellipses - use title attribute for all
      buf.append(element);
      
      // close tag
      buf.append("</a>");
      
      return buf.toString();
   }
   
   /**
    * Get the hidden field name for this breadcrumb.
    * Assume there will not be many breadcrumbs on a page - therefore a hidden field
    * for each is not a significant issue.
    * 
    * @return hidden field name
    */
   private String getHiddenFieldName(FacesContext context, UIComponent component)
   {
      return component.getClientId(context);
   }
}
