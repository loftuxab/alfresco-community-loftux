/*
 * Created on 13-Apr-2005
 */
package org.alfresco.web.jsf.component;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * @author kevinr
 */
public class UIModeListItem extends SelfRenderingComponent
{
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
    * Get the value - the value is used in a equals() match against the current value in the
    * parent ModeList component to set the selected item.
    *
    * @return the value
    */
   public Object getValue()
   {
      ValueBinding vb = getValueBinding("value");
      if (vb != null)
      {
         this.value = vb.getValue(getFacesContext());
      }
      
      return this.value;
   }

   /**
    * Set the value - the value is used in a equals() match against the current value in the
    * parent ModeList component to set the selected item.
    *
    * @param value     the value
    */
   public void setValue(Object value)
   {
      this.value = value;
   }
   
   /**
    * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
    */
   public Object saveState(FacesContext context)
   {
      Object values[] = new Object[5];
      values[0] = super.saveState(context);
      values[1] = this.value;
      return ((Object) (values));
   }

   /**
    * @see javax.faces.component.StateHolder#restoreState(javax.faces.context.FacesContext, java.lang.Object)
    */
   public void restoreState(FacesContext context, Object state)
   {
      Object values[] = (Object[])state;
      super.restoreState(context, values[0]);
      this.value = values[1];
   }
   
   /** the component value */
   private Object value = null;
}
