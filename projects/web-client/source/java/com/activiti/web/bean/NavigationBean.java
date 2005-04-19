/*
 * Created on 19-Apr-2005
 */
package com.activiti.web.bean;

import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;

import com.activiti.web.jsf.component.UIPanel;

/**
 * @author kevinr
 */
public class NavigationBean
{
   public void toggleShelf(ActionEvent event)
   {
      UIComponent component = event.getComponent().findComponent("shelfPanel");
      if (component instanceof UIPanel)
      {
         UIPanel panel = (UIPanel)component;
         panel.setExpanded( !panel.isExpanded() );
      }
   }
}
