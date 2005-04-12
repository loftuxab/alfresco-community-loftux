/*
 * Created on 04-Apr-2005
 */
package com.activiti.web.jsf.component;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UICommand;
import javax.faces.el.ValueBinding;

/**
 * @author kevinr
 */
public class UIActionLink extends UICommand
{
   // ------------------------------------------------------------------------------
   // Construction 
   
   /**
    * Default Constructor
    */
   public UIActionLink()
   {
      setRendererType("awc.faces.ActionLinkRenderer");
   }
   
   
   // ------------------------------------------------------------------------------
   // Component implementation 
   
   /**
    * @see javax.faces.component.UIComponent#getFamily()
    */
   public String getFamily()
   {
      return "awc.faces.Controls";
   }

   
   // ------------------------------------------------------------------------------
   // Strongly typed component property accessors 
   
   /**
    * Return the current child parameter map for this action link instance.
    * This map is filled with name/value pairs from any child UIParameter components.
    * 
    * @return Map of name/value pairs
    */
   public Map<String, String> getParameterMap()
   {
      return this.params;
   }
   
   /**
    * Get whether to show the link as well as the image if specified
    * 
    * @return true to show the link as well as the image if specified
    */
   public boolean getShowLink()
   {
      if (this.showLink == null)
      {
         ValueBinding vb = getValueBinding("showLink");
         if (vb != null)
         {
            this.showLink = (Boolean)vb.getValue(getFacesContext());
         }
      }
      
      if (this.showLink != null)
      {
         return this.showLink.booleanValue();
      }
      else
      {
         return true;
      }
   }
   
   /**
    * Set whether to show the link as well as the image if specified
    * 
    * @param showLink      Whether to show the link as well as the image if specified
    */
   public void setShowLink(boolean showLink)
   {
      this.showLink = Boolean.valueOf(showLink);
   }
   
   /**
    * Return the Image path to use for this actionlink.
    * If an image is specified, it is shown in additon to the value text unless
    * the 'showLink' property is set to 'false'.
    * 
    * @return the image path to display
    */
   public String getImage()
   {
      if (this.image == null)
      {
         ValueBinding vb = getValueBinding("image");
         if (vb != null)
         {
            this.image = (String)vb.getValue(getFacesContext());
         }
      }
      
      return this.image;
   }
   
   /**
    * Set the Image path to use for this actionlink.
    * If an image is specified, it is shown in additon to the value text unless
    * the 'showLink' property is set to 'false'.
    * 
    * @param image      Image path to display
    */
   public void setImage(String image)
   {
      this.image = image;
   }
   
   
   // ------------------------------------------------------------------------------
   // Private data
   
   /** True to show the link as well as the image if specified */
   private Boolean showLink = null;
   
   /** If an image is specified, it is shown in additon to the value text */
   private String image = null;
   
   /** Map of child param name/values pairs */
   private Map<String, String> params = new HashMap<String, String>(3, 1.0f);
}
