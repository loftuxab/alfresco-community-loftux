package org.alfresco.web.ui.common.tag;

import javax.faces.component.UIComponent;

public class ErrorsTag extends HtmlComponentTag
{
   private String message;
   
   /**
    * @see javax.faces.webapp.UIComponentTag#getComponentType()
    */
   public String getComponentType()
   {
      return "javax.faces.Messages";
   }

   /**
    * @see javax.faces.webapp.UIComponentTag#getRendererType()
    */
   public String getRendererType()
   {
      return "org.alfresco.faces.Errors";
   }

   /**
    * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
    */
   protected void setProperties(UIComponent component)
   {
      super.setProperties(component);
      
      setStringProperty(component, "message", this.message);
   }

   /**
    * @param message Sets the message to display
    */
   public void setMessage(String message)
   {
      this.message = message;
   }
}
