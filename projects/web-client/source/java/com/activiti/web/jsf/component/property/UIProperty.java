package com.activiti.web.jsf.component.property;

import com.activiti.web.jsf.component.SelfRenderingComponent;

/**
 * Component to represent an individual property
 * 
 * @author gavinc
 */
public class UIProperty extends SelfRenderingComponent
{
   /**
    * @see javax.faces.component.UIComponent#getFamily()
    */
   public String getFamily()
   {
      return "activiti:PropertyFamily";
   }
}
