/*
 * Created on 19-Apr-2005
 */
package com.activiti.web.bean;

import javax.faces.event.ActionEvent;

import com.activiti.web.jsf.component.UIModeList;

/**
 * @author kevinr
 */
public class BrowseBean
{
   /**
    * @return Returns the browse View mode. See UIRichList
    */
   public String getBrowseViewMode()
   {
      return browseViewMode;
   }
   
   /**
    * @param browseViewMode      The browse View mode to set. See UIRichList.
    */
   public void setBrowseViewMode(String browseViewMode)
   {
      this.browseViewMode = browseViewMode;
   }
   
   /**
    * Change the current view mode based on user selection
    * 
    * @param event      ActionEvent
    */
   public void viewModeChanged(ActionEvent event)
   {
      UIModeList viewList = (UIModeList)event.getComponent();
      setBrowseViewMode(viewList.getValue().toString());
   }


   private String browseViewMode = "details";
}
