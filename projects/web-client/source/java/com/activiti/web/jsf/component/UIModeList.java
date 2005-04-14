/*
 * Created on 12-Apr-2005
 */
package com.activiti.web.jsf.component;

import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesEvent;

import com.activiti.web.jsf.component.UIBreadcrumb.BreadcrumbEvent;

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
   // Inner classes
   
   /**
    * Class representing a change in selection for a ModeList component.
    */
   public static class ModeListItemSelectedEvent extends ActionEvent
   {
      public ModeListItemSelectedEvent(UIComponent component, Object selectedValue)
      {
         super(component);
         SelectedValue = selectedValue;
      }
      
      public Object SelectedValue = null;
   }
}
