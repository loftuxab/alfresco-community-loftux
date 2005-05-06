/*
 * Created on 04-Apr-2005
 */
package org.alfresco.web.ui.common.component;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UICommand;
import javax.faces.context.FacesContext;
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
   
   /**
    * @see javax.faces.component.StateHolder#restoreState(javax.faces.context.FacesContext, java.lang.Object)
    */
   public void restoreState(FacesContext context, Object state)
   {
      Object values[] = (Object[])state;
      // standard component attributes are restored by the super class
      super.restoreState(context, values[0]);
      this.padding = (Integer)values[1];
      this.image = (String)values[2];
      this.showLink = (Boolean)values[3];
      this.params = (Map)values[4];
   }
   
   /**
    * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
    */
   public Object saveState(FacesContext context)
   {
      Object values[] = new Object[5];
      // standard component attributes are saved by the super class
      values[0] = super.saveState(context);
      values[1] = this.padding;
      values[2] = this.image;
      values[3] = this.showLink;
      values[4] = this.params;
      return (values);
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
      ValueBinding vb = getValueBinding("showLink");
      if (vb != null)
      {
         this.showLink = (Boolean)vb.getValue(getFacesContext());
      }
      
      if (this.showLink != null)
      {
         return this.showLink.booleanValue();
      }
      else
      {
         // return default
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
    * Get the padding value for rendering this component in a table.
    * 
    * @return the padding in pixels, if set != 0 then a table will be rendering around the items
    */
   public int getPadding()
   {
      ValueBinding vb = getValueBinding("padding");
      if (vb != null)
      {
         this.padding = (Integer)vb.getValue(getFacesContext());
      }
      
      if (this.padding != null)
      {
         return this.padding.intValue();
      }
      else
      {
         // return default
         return 0;
      }
   }
   
   /**
    * Set the padding value for rendering this component in a table.
    * 
    * @param padding       value in pixels, if set != 0 then a table will be rendering around the items
    */
   public void setPadding(int padding)
   {
      this.padding = padding;
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
      ValueBinding vb = getValueBinding("image");
      if (vb != null)
      {
         this.image = (String)vb.getValue(getFacesContext());
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
   
   /** the padding value in pixels, if set != 0 then a table will be rendered around the items */
   private Integer padding = null;
   
   /** True to show the link as well as the image if specified */
   private Boolean showLink = null;
   
   /** If an image is specified, it is shown in additon to the value text */
   private String image = null;
   
   /** Map of child param name/values pairs */
   private Map<String, String> params = new HashMap<String, String>(3, 1.0f);
}
