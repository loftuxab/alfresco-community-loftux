/*
 * Created on 12-Apr-2005
 */
package org.alfresco.web.jsf.component;

import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesEvent;

/**
 * @author kevinr
 */
public class UIModeList extends UICommand
{
   // ------------------------------------------------------------------------------
   // Construction
   
   /**
    * Default constructor
    */
   public UIModeList()
   {
      setRendererType("awc.faces.ModeListRenderer");
   }
   
   
   // ------------------------------------------------------------------------------
   // Component Impl 
   
   /**
    * @see javax.faces.component.UIComponent#getFamily()
    */
   public String getFamily()
   {
      return "awc.faces.Controls";
   }
   
   /**
    * @see javax.faces.component.UICommand#broadcast(javax.faces.event.FacesEvent)
    */
   public void broadcast(FacesEvent event) throws AbortProcessingException
   {
      if (event instanceof ModeListItemSelectedEvent)
      {
         // found an event for us, update the value for this component
         setValue( ((ModeListItemSelectedEvent)event).SelectedValue );
      }
      
      // default ActionEvent processing for a UICommand
      super.broadcast(event);
   }
   
   
   // ------------------------------------------------------------------------------
   // Strongly typed property accessors 
   
   /**
    * Get the horizontal rendering flag
    *
    * @return true for horizontal rendering, false otherwise
    */
   public boolean isHorizontal()
   {
      ValueBinding vb = getValueBinding("horizontal");
      if (vb != null)
      {
         this.horizontal = (Boolean)vb.getValue(getFacesContext());
      }
      
      if (this.horizontal != null)
      {
         return this.horizontal.booleanValue();
      }
      else
      {
         // return the default
         return false;
      }
   }

   /**
    * Set true for horizontal rendering, false otherwise
    *
    * @param horizontal       the horizontal
    */
   public void setHorizontal(boolean horizontal)
   {
      this.horizontal = horizontal;
   }
   
   /**
    * Get the icon column width
    *
    * @return the icon column width
    */
   public int getIconColumnWidth()
   {
      ValueBinding vb = getValueBinding("iconColumnWidth");
      if (vb != null)
      {
         this.iconColumnWidth = (Integer)vb.getValue(getFacesContext());
      }
      
      if (this.iconColumnWidth != null)
      {
         return this.iconColumnWidth.intValue();
      }
      else
      {
         // return the default
         return 20;
      }
   }

   /**
    * Set the icon column width
    *
    * @param iconColumnWidth     the icon column width
    */
   public void setIconColumnWidth(int iconColumnWidth)
   {
      this.iconColumnWidth = Integer.valueOf(iconColumnWidth);
   }


   /** the icon column width */
   private Integer iconColumnWidth;

   /** true for horizontal rendering, false otherwise */
   private Boolean horizontal = null;
   
   
   // ------------------------------------------------------------------------------
   // Inner classes
   
   /**
    * Class representing a change in selection for a ModeList component.
    */
   public static class ModeListItemSelectedEvent extends ActionEvent
   {
      private static final long serialVersionUID = 3618135654274774322L;

      public ModeListItemSelectedEvent(UIComponent component, Object selectedValue)
      {
         super(component);
         SelectedValue = selectedValue;
      }
      
      public Object SelectedValue = null;
   }
}
