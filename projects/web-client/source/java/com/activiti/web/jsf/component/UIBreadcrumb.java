/*
 * Created on 01-Apr-2005
 */
package com.activiti.web.jsf.component;

import java.util.StringTokenizer;

import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesEvent;

/**
 * @author kevinr
 */
public class UIBreadcrumb extends UICommand
{
   // ------------------------------------------------------------------------------
   // Construction 
   
   /**
    * Default Constructor
    */
   public UIBreadcrumb()
   {
      setRendererType("awc.faces.BreadcrumbRenderer");
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
    * @see javax.faces.component.UICommand#broadcast(javax.faces.event.FacesEvent)
    */
   public void broadcast(FacesEvent event) throws AbortProcessingException
   {
      if (event instanceof BreadcrumbEvent)
      {
         setSelectedPathIndex( ((BreadcrumbEvent)event).SelectedIndex );
      }
      
      // standard ActionEvent processing for a UICommand
      super.broadcast(event);
   }

   /**
    * Set the selected path index. This modifies the current path value.
    * 
    * @return last selected path index
    */
   public void setSelectedPathIndex(int index)
   {
      if (index >= 0)
      {
         String path = (String)getValue();
         if (path != null)
         {
            // rebuild the path upto the specified token index
            StringBuilder newPath = new StringBuilder(path.length());
            StringTokenizer t = new StringTokenizer(path, SEPARATOR);
            for (int i=0; i<=index && t.hasMoreTokens(); i++)
            {
               newPath.append(SEPARATOR)
                      .append(t.nextToken());
            }
            
            // save the new path up to and including the selected element
            setValue(newPath.toString());
         }
      }
   }
   
   
   // ------------------------------------------------------------------------------
   // Strongly typed component property accessors 
   
   /**
    * Get the visible separator value for outputing the breadcrumb
    * 
    * @return separator string
    */
   public String getSeparator()
   {
      if (this.separator == null)
      {
         ValueBinding vb = getValueBinding("separator");
         if (vb != null)
         {
            this.separator = (String)vb.getValue(getFacesContext());
         }
      }
      return this.separator;
   }
   
   /**
    * Set separator
    * 
    * @param separator     visible separator value for outputing the breadcrumb
    */
   public void setSeparator(String separator)
   {
      this.separator = separator;
   }
   
   /**
    * Get whether to show the root of the path
    * 
    * @return true to show the root of the path, false to hide it
    */
   public boolean getShowRoot()
   {
      if (this.showRoot == null)
      {
         ValueBinding vb = getValueBinding("showRoot");
         if (vb != null)
         {
            this.showRoot = (Boolean)vb.getValue(getFacesContext());
         }
      }
      
      if (this.showRoot != null)
      {
         return this.showRoot.booleanValue();
      }
      else
      {
         return true;
      }
   }
   
   /**
    * Set whether to show the root of the path
    * 
    * @param showRoot      Whether to show the root of the path
    */
   public void setShowRoot(boolean showRoot)
   {
      this.showRoot = Boolean.valueOf(showRoot);
   }
   
   
   // ------------------------------------------------------------------------------
   // Inner classes
   
   /**
    * Class representing the clicking of a breadcrumb element.
    */
   public static class BreadcrumbEvent extends ActionEvent
   {
      public BreadcrumbEvent(UIComponent component, int selectedIndex)
      {
         super(component);
         SelectedIndex = selectedIndex;
      }
      
      public int SelectedIndex = 0;
   }
   
   
   // ------------------------------------------------------------------------------
   // Private data
   
   // visible separator value
   private String separator = null;
   
   // true to show the root of the breadcrumb path, false otherwise
   private Boolean showRoot = null;
   
   /** the separator for a breadcrumb path value */
   public final static String SEPARATOR = "/";
}
