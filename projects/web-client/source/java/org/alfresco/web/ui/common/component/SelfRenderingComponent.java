/*
 * Created on Mar 11, 2005
 */
package org.alfresco.web.ui.common.component;

import java.io.IOException;

import javax.faces.component.UIComponentBase;
import javax.faces.context.ResponseWriter;


/**
 * @author kevinr
 */
public abstract class SelfRenderingComponent extends UIComponentBase
{
   /**
    * Default Constructor
    */
   public SelfRenderingComponent()
   {
      // specifically set the renderer type to null to indicate to the framework
      // that this component renders itself - there is no abstract renderer class
      setRendererType(null);
   }
   
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
}
