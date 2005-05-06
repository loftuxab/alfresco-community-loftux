/*
 * Created on Mar 9, 2005
 */
package org.alfresco.web.jsf.renderer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

/**
 * Base renderer class. Contains helper methods to assist most renderers. 
 * 
 * @author kevinr
 */
public abstract class BaseRenderer extends Renderer
{
   /**
    * Helper to output an attribute to the output stream
    * 
    * @param out        ResponseWriter
    * @param attr       attribute value object (cannot be null)
    * @param mapping    mapping to output as e.g. style="..."
    * 
    * @throws IOException
    */
   protected static void outputAttribute(ResponseWriter out, Object attr, String mapping)
      throws IOException
   {
      if (attr != null)
      {
         out.write(' ');
         out.write(mapping);
         out.write("=\"");
         out.write(attr.toString());
         out.write('"');
      }
   }
   
   /**
    * Helper to recursively render a component and it's child components
    * 
    * @param context    FacesContext
    * @param component  UIComponent
    * 
    * @throws IOException
    */
   protected static void encodeRecursive(FacesContext context, UIComponent component)
      throws IOException
   {
      if (component.isRendered() == true)
      {
         component.encodeBegin(context);
         
         // follow the spec for components that render their children
         if (component.getRendersChildren() == true)
         {
            component.encodeChildren(context);
         }
         else
         {
            if (component.getChildCount() != 0)
            {
               for (Iterator i=component.getChildren().iterator(); i.hasNext(); /**/)
               {
                  encodeRecursive(context, (UIComponent)i.next());
               }
            }
         }
         
         component.encodeEnd(context);
      }
   }
   
   /**
    * Ensures that the given context and component are not null. This method
    * should be called by all renderer methods that are given these parameters.
    * 
    * @param ctx Faces context
    * @param component The component
    */
   protected static void assertParmeters(FacesContext ctx, UIComponent component)
   {
      if (ctx == null)
      {
         throw new IllegalStateException("context can not be null");
      }
      
      if (component == null)
      {
         throw new IllegalStateException("component can not be null");
      }
   }
   
   /**
    * Return the map of name/value pairs for any child UIParameter components.
    * 
    * @param component to find UIParameter child values for
    * 
    * @return a Map of name/value pairs or null if none found
    */
   protected static Map<String, String> getParameterMap(UIComponent component)
   {
      Map<String, String> params = null;
      
      if (component.getChildCount() != 0)
      {
         params = new HashMap<String, String>(3, 1.0f);
         for (Iterator i=component.getChildren().iterator(); i.hasNext(); /**/)
         {
            UIComponent child = (UIComponent)i.next();
            if (child instanceof UIParameter)
            {
               UIParameter param = (UIParameter)child;
               params.put(param.getName(), (String)param.getValue());
            }
         }
      }
      
      return params;
   }
}
