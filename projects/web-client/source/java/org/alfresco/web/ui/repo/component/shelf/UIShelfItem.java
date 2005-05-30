/*
 * Created on 31-May-2005
 */
package org.alfresco.web.ui.repo.component.shelf;

import org.alfresco.web.ui.common.component.SelfRenderingComponent;

/**
 * @author Kevin Roast
 */
public class UIShelfItem extends SelfRenderingComponent
{
   // ------------------------------------------------------------------------------
   // Component Impl 

   /**
    * @see javax.faces.component.UIComponent#getFamily()
    */
   public String getFamily()
   {
      return "org.alfresco.faces.Shelf";
   }

}
