/*
 * Created on Mar 11, 2005
 */
package com.activiti.web.jsf.component;

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
   
   protected static void outputAttribute(ResponseWriter out, Object attr, String mapping)
      throws IOException
   {
      if (attr != null)
      {
         out.write(' ');
         out.write(mapping);
         out.write("='");
         out.write(attr.toString());
         out.write('\'');
      }
   }
}
