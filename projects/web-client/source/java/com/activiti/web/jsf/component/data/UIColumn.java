/*
 * Created on Mar 11, 2005
 */
package com.activiti.web.jsf.component.data;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;


/**
 * @author kevinr
 */
public class UIColumn extends UIComponentBase
{
   /**
    * @see javax.faces.component.UIComponent#getFamily()
    */
   public String getFamily()
   {
      return "awc.faces.Data";
   }
   
   public UIComponent getHeader()
   {
      return getFacet("header");
   }
   
   public UIComponent getFooter()
   {
      return getFacet("footer");
   }
   
   // ===========================================================================
   // Strongly typed Getters for Component attributes
   
   // NOTE: Cannot be done as you might think - Be careful!
   //       The problem is that we are setting the component attributes and value
   //       bindings in the associated Tag class. This means when we call
   //       getAttributes().get("key") then the "property transparency" means the
   //       framework attempts resolve value bindings and then exec a getter called
   //       getKey() which is fine unless you call getKey() directly (which is the
   //       point!) which results in a stack overflow!
   //       It seems better for the tag to simply set the attributes on the comp
   //       and the comp to resolve the value bindings during the getter call - but
   //       then you would HAVE to use the specific getter call not getAttributes()
   //       as that would not have the attribute in it!
   //       Therefore strongly typed getters should be used carefully for JSF
   //       component impls.
}
