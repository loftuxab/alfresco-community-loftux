/*
 * Created on Mar 15, 2005
 */
package com.activiti.web.jsf;

import java.util.Map;

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
      return generateFormSubmit(context, component, fieldId, fieldValue, null);
   }
   
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
    * @param params        Optional map of param name/values to output
    * 
    * @return JavaScript event code
    */
   public static String generateFormSubmit(FacesContext context, UIComponent component, String fieldId, String fieldValue, Map<String, String> params)
   {
      UIForm form = Utils.getParentForm(context, component);
      if (form == null)
      {
         throw new IllegalStateException("Must nest components inside UIForm to generate form submit!");
      }
      
      String formClientId = form.getClientId(context);
      
      StringBuilder buf = new StringBuilder(200);
      buf.append("document.forms[");
      buf.append("'");
      buf.append(formClientId);
      buf.append("'");
      buf.append("]['");
      buf.append(fieldId);
      buf.append("'].value='");
      buf.append(fieldValue);
      buf.append("';");
      
      if (params != null)
      {
         for (String name : params.keySet())
         {
            buf.append("document.forms[");
            buf.append("'");
            buf.append(formClientId);
            buf.append("'");
            buf.append("]['");
            buf.append(name);
            buf.append("'].value='");
            buf.append(params.get(name));
            buf.append("';");
            
            // weak, but this seems to be the way Sun RI do it...
            FormRenderer.addNeededHiddenField(context, name);
         }
      }
      
      buf.append("document.forms[");
      buf.append("'");
      buf.append(formClientId);
      buf.append("'");
      buf.append("].submit()");
      
      buf.append(";return false;");
      
      // weak, but this seems to be the way Sun RI do it...
      FormRenderer.addNeededHiddenField(context, fieldId);
      
      return buf.toString();
   }
   
   /**
    * Build a context path safe image tag for the supplied image path.
    * Image path should be supplied with a leading slash '/'.
    * 
    * @param context       FacesContext
    * @param image         The local image path from the web folder with leading slash '/'
    * @param width         Width in pixels
    * @param height        Height in pixels
    * @param alt           Optional alt/title text
    * 
    * @return Populated <code>img</code> tag
    */
   public static String buildImageTag(FacesContext context, String image, int width, int height, String alt)
   {
      StringBuilder buf = new StringBuilder(100);
      
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
   
   /**
    * Build a context path safe image tag for the supplied image path.
    * Image path should be supplied with a leading slash '/'.
    * 
    * @param context       FacesContext
    * @param image         The local image path from the web folder with leading slash '/'
    * @param alt           Optional alt/title text
    * 
    * @return Populated <code>img</code> tag
    */
   public static String buildImageTag(FacesContext context, String image, String alt)
   {
      StringBuilder buf = new StringBuilder(100);
      
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
