/*
 * Created on Mar 11, 2005
 */
package org.alfresco.web.ui.common.component.data;

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
   
   /**
    * Return the UI Component to be used as the header for this column
    * 
    * @return UIComponent
    */
   public UIComponent getHeader()
   {
      return getFacet("header");
   }
   
   /**
    * Return the UI Component to be used as the footer for this column
    * 
    * @return UIComponent
    */
   public UIComponent getFooter()
   {
      return getFacet("footer");
   }
   
   /**
    * Return the UI Component to be used as the large icon for this column
    * 
    * @return UIComponent
    */
   public UIComponent getLargeIcon()
   {
      return getFacet("large-icon");
   }
   
   /**
    * Return the UI Component to be used as the small icon for this column
    * 
    * @return UIComponent
    */
   public UIComponent getSmallIcon()
   {
      return getFacet("small-icon");
   }
   
   public boolean isPrimaryColumn()
   {
      return this.primary;
   }
   
   public void setPrimary(boolean primary)
   {
      this.primary = primary;
   }
   
   public boolean isActionsColumn()
   {
      return this.actions;
   }
   
   public void setActions(boolean actions)
   {
      this.actions = actions;
   }
   
   
   private boolean primary = false;
   private boolean actions = false;
}
