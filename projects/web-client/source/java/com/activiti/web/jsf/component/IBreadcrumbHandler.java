/*
 * Created on 22-Apr-2005
 */
package com.activiti.web.jsf.component;

/**
 * @author Kevin Roast
 */
public interface IBreadcrumbHandler
{
   /**
    * Override Object.toString()
    * 
    * @return the element display label for this handler instance.
    */
   public String toString();
   
   /**
    * Perform appropriate processing logic and then return a JSF navigation outcome.
    * This method will be called by the framework when the handler instance is selected by the user.
    * 
    * @return JSF navigation outcome
    */
   public String navigationOutcome();
}
