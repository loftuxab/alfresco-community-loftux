/*
 * Created on Mar 15, 2005
 */
package com.activiti.web.jsf;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;

import com.activiti.web.data.IDataContainer;

import com.sun.faces.renderkit.html_basic.FormRenderer;

/**
 * @author kevinr
 */
public final class Utils
{
   /**
    * Generate the JavaScript to submit set the specified hidden Form field to the
    * supplied value and submit the parent Form.
    * 
    * NOTE: the supplied hidden field name is added to the Form Renderer map for output.
    * 
    * @param context       FacesContext
    * @param component     UIComponent to generate JavaScript for
    * @param fieldId       Hidden field id to set value for
    * @param fieldValue    Hidden field value to set hidden field too on submit
    * 
    * @return JavaScript event code
    */
   public static String generateFormSubmit(FacesContext context, UIComponent component, String fieldId, String fieldValue)
   {
      UIForm form = Utils.getParentForm(context, component);
      if (form == null)
      {
         throw new IllegalStateException("Must nest components inside UIForm to generate form submit!");
      }
      
      String formClientId = form.getClientId(context);
      
      StringBuffer buf = new StringBuffer(200);
      buf.append("document.forms[");
      buf.append("'");
      buf.append(formClientId);
      buf.append("'");
      buf.append("]['");
      buf.append(fieldId);
      buf.append("'].value='");
      buf.append(fieldValue);     //component.getClientId(context)
      buf.append("';");
      buf.append(" document.forms[");
      buf.append("'");
      buf.append(formClientId);
      buf.append("'");
      buf.append("].submit()");
      
      buf.append(";return false;");
      
      // weak, but this seems to be the way Sun RI do it...
      FormRenderer.addNeededHiddenField(context, fieldId);
      
      return buf.toString();
   }
   
   public static String buildImageTag(FacesContext context, String image, int width, int height, String alt)
   {
      StringBuffer buf = new StringBuffer(100);
      
      buf.append("<img src='")
         .append(context.getExternalContext().getRequestContextPath())
         .append(image)
         .append("' width=")
         .append(width)
         .append(" height=")
         .append(height)
         .append(" border=0");
      
      if (alt != null)
      {
         buf.append(" alt='")
            .append(alt)
            .append("' title='")
            .append(alt)
            .append('\'');
      }
      
      buf.append('>');
      
      return buf.toString();
   }
   
   public static String buildImageTag(FacesContext context, String image, String alt)
   {
      StringBuffer buf = new StringBuffer(100);
      
      buf.append("<img src='")
         .append(context.getExternalContext().getRequestContextPath())
         .append(image)
         .append("' border=0");
      
      if (alt != null)
      {
         buf.append(" alt='")
            .append(alt)
            .append("' title='")
            .append(alt)
            .append('\'');
      }
      
      buf.append('>');
      
      return buf.toString();
   }
   
   /**
    * Return the parent UIForm component for the specified UIComponent
    * 
    * @param context       FaceContext
    * @param component     The UIComponent to find parent Form for
    * 
    * @return UIForm parent or null if none found in hiearachy
    */
   public static UIForm getParentForm(FacesContext context, UIComponent component)
   {
      UIComponent parent = component.getParent();
      while (parent != null)
      {
         if (parent instanceof UIForm)
         {
            break;
         }
         parent = parent.getParent();
      }
      return (UIForm)parent;
   }
   
   /**
    * Return the parent UIComponent implementing the NamingContainer interface for
    * the specified UIComponent.
    * 
    * @param context       FaceContext
    * @param component     The UIComponent to find parent Form for
    * 
    * @return NamingContainer parent or null if none found in hiearachy
    */
   public static UIComponent getParentNamingContainer(FacesContext context, UIComponent component)
   {
      UIComponent parent = component.getParent();
      while (parent != null)
      {
         if (parent instanceof NamingContainer)
         {
            break;
         }
         parent = parent.getParent();
      }
      return (UIComponent)parent;
   }
   
   /**
    * Return the parent UIComponent implementing the IDataContainer interface for
    * the specified UIComponent.
    * 
    * @param context       FaceContext
    * @param component     The UIComponent to find parent IDataContainer for
    * 
    * @return IDataContainer parent or null if none found in hiearachy
    */
   public static IDataContainer getParentDataContainer(FacesContext context, UIComponent component)
   {
      UIComponent parent = component.getParent();
      while (parent != null)
      {
         if (parent instanceof IDataContainer)
         {
            break;
         }
         parent = parent.getParent();
      }
      return (IDataContainer)parent;
   }
}
