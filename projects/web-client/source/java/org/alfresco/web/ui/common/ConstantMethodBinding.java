package org.alfresco.web.jsf;

import javax.faces.component.StateHolder;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;

public class ConstantMethodBinding extends MethodBinding implements StateHolder
{
   private String outcome = null;
   private boolean transientFlag = false;

   public ConstantMethodBinding()
   {
   }

   public ConstantMethodBinding(String yourOutcome)
   {
      outcome = yourOutcome;
   }

   public Object invoke(FacesContext context, Object params[])
   {
      return outcome;
   }

   public Class getType(FacesContext context)
   {
      return String.class;
   }

   public Object saveState(FacesContext context)
   {
      return outcome;
   }

   public void restoreState(FacesContext context, Object state)
   {
      outcome = (String) state;
   }

   public boolean isTransient()
   {
      return (this.transientFlag);
   }

   public void setTransient(boolean transientFlag)
   {
      this.transientFlag = transientFlag;
   }
}
