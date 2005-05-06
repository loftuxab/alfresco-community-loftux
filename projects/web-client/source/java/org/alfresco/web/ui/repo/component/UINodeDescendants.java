/*
 * Created on 03-May-2005
 */
package org.alfresco.web.ui.repo.component;

import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;

import org.alfresco.repo.ref.NodeRef;

/**
 * @author Kevin Roast
 */
public class UINodeDescendants extends UICommand
{
   // ------------------------------------------------------------------------------
   // Construction
   
   /**
    * Default constructor
    */
   public UINodeDescendants()
   {
      setRendererType("awc.repo.NodeDescendantsLinkRenderer");
   }
   
   
   // ------------------------------------------------------------------------------
   // Component implementation
   
   /**
    * @see javax.faces.component.UIComponent#getFamily()
    */
   public String getFamily()
   {
      return "awc.repo.NodeDescendants";
   }
   
   
   // ------------------------------------------------------------------------------
   // Strongly typed component property accessors
   
   /**
    * @return the maximum number of child descendants to be displayed, default maximum is 4. 
    */
   public int getMaxChildren()
   {
      ValueBinding vb = getValueBinding("maxChildren");
      if (vb != null)
      {
         this.maxChildren = (Integer)vb.getValue(getFacesContext());
      }
      
      if (this.maxChildren != null)
      {
         return this.maxChildren.intValue();
      }
      else
      {
         // return default
         return 4;
      }
   }
   
   /**
    * @param value      The maximum allowed before the no more links are shown
    */
   public void setMaxChildren(int value)
   {
      if (value > 0 && value <= 256)
      {
         this.maxChildren = Integer.valueOf(value);
      }
   }
   
   /**
    * @return whether to show ellipses "..." if more descendants than the maxChildren value are found
    */
   public boolean getShowEllipses()
   {
      ValueBinding vb = getValueBinding("showEllipses");
      if (vb != null)
      {
         this.showEllipses = (Boolean)vb.getValue(getFacesContext());
      }
      
      if (this.showEllipses != null)
      {
         return this.showEllipses.booleanValue();
      }
      else
      {
         // return default
         return true;
      }
   }
   
   /**
    * @param showLink      True to show ellipses "..." if more descendants than maxChildren are found
    */
   public void setShowEllipses(boolean showEllipses)
   {
      this.showEllipses = Boolean.valueOf(showEllipses);
   }
   
   
   // ------------------------------------------------------------------------------
   // Inner classes
   
   /**
    * Class representing the clicking of a node descendant element.
    */
   public static class NodeSelectedEvent extends ActionEvent
   {
      public NodeSelectedEvent(UIComponent component, NodeRef nodeRef, boolean isParent)
      {
         super(component);
         this.NodeReference = nodeRef;
         this.IsParent = isParent;
      }
      
      public NodeRef NodeReference;
      public boolean IsParent;
   }
   
   
   // ------------------------------------------------------------------------------
   // Private data
   
   /** maximum number of child descendants to display */
   private Integer maxChildren = null;
   
   /** whether to show ellipses if more descendants than the maxChildren are found */
   private Boolean showEllipses = null;
}
