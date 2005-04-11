/*
 * Created on 04-Apr-2005
 */
package com.activiti.web.jsf.renderer;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.activiti.web.jsf.Utils;
import com.activiti.web.jsf.component.UIActionLink;

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
         
         UIActionLink link = (UIActionLink)component;
         Map<String, String> destParams = link.getParameterMap();
         destParams.clear();
         if (getParameterMap(link) != null)
         {
            for (String name : getParameterMap(link).keySet())
            {
               String paramValue = (String)requestMap.get(name);
               destParams.put(name, paramValue);
            }
         }
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
         
         UIActionLink link = (UIActionLink)component;
         
         // render sort link
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
            buf.append(Utils.buildImageTag(context, link.getImage(), (String)link.getValue()));
            if (link.getShowLink() == true)
            {
               // text next to an image may need alignment
               if (attrs.get("verticalAlign") != null)
               {
                  buf.append("<span style='vertical-align:")
                     .append(attrs.get("verticalAlign"))
                     .append("'>");
                  buf.append(link.getValue());
                  buf.append("</span>");
               }
               else
               {
                  buf.append(link.getValue());
               }
            }
         }
         else
         {
            buf.append(link.getValue());
         }
         
         out.write( buf.toString() );
      }
   }
   
   /**
    * @see javax.faces.render.Renderer#encodeEnd(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
    */
   public void encodeEnd(FacesContext context, UIComponent component) throws IOException
   {
      if (component.isRendered() == true)
      {
         Writer out = context.getResponseWriter();
         
         out.write("</a>");
      }
   }
   
   
   // ------------------------------------------------------------------------------
   // Private helpers

   /**
    * Get the hidden field name for this actionlink.
    * Build a shared field name from the parent form name and the string "act".
    * 
    * @return hidden field name shared by all action links within the Form.
    */
   private String getHiddenFieldName(FacesContext context, UIComponent component)
   {
      return Utils.getParentForm(context, component).getClientId(context) + NamingContainer.SEPARATOR_CHAR + "act";
   }
}
