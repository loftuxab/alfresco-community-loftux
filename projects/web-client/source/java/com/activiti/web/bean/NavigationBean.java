/*
 * Created on 19-Apr-2005
 */
package com.activiti.web.bean;

import javax.faces.event.ActionEvent;

/**
 * @author kevinr
 */
public class NavigationBean
{
   /**
    * Return the expanded state of the Shelf panel wrapper component
    * 
    * @return the expanded state of the Shelf panel wrapper component
    */
   public boolean isShelfExpanded()
   {
      return this.shelfExpanded;
   }
   
   /**
    * Set the expanded state of the Shelf panel wrapper component
    * 
    * @param expanded      true to expanded the Shelf panel area, false to hide it
    */
   public void setShelfExpanded(boolean expanded)
   {
      this.shelfExpanded = expanded;
   }
   
   /**
    * Action to toggle the expanded state of the shelf. The panel component wrapping the shelf area of the UI
    * is
    * @param event
    */
   public void toggleShelf(ActionEvent event)
   {
      this.shelfExpanded = !this.shelfExpanded;
   }
   
   /** expanded state of the Shelf panel wrapper component */
   boolean shelfExpanded = true;
}
