/*
 * Created on 22-Apr-2005
 */
package org.alfresco.web.jsf.component;

import java.io.Serializable;

/**
 * @author Kevin Roast
 */
public interface IBreadcrumbHandler extends Serializable
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
    * @param breadcrumb    The UIBreadcrumb component that caused the navigation
    * 
    * @return JSF navigation outcome
    */
   public String navigationOutcome(UIBreadcrumb breadcrumb);
}
