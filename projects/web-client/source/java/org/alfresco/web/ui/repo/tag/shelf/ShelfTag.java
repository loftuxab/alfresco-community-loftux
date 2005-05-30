/*
 * Created on 31-May-2005
 */
package org.alfresco.web.ui.repo.tag.shelf;

import javax.faces.component.UIComponent;

import org.alfresco.web.ui.common.tag.BaseComponentTag;

/**
 * @author Kevin Roast
 */
public class ShelfTag extends BaseComponentTag
{
   /**
    * @see javax.faces.webapp.UIComponentTag#getComponentType()
    */
   public String getComponentType()
   {
      return "org.alfresco.faces.Shelf";
   }

   /**
    * @see javax.faces.webapp.UIComponentTag#getRendererType()
    */
   public String getRendererType()
   {
      // self rendering component
      return null;
   }
   
   /**
    * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
    */
   protected void setProperties(UIComponent component)
   {
      super.setProperties(component);
      
      setStringProperty(component, "groupPanel", this.groupPanel);
      setStringProperty(component, "groupBgcolor", this.groupBgcolor);
      setStringProperty(component, "selectedGroupPanel", this.selectedGroupPanel);
      setStringProperty(component, "selectedGroupBgcolor", this.selectedGroupBgcolor);
      setStringProperty(component, "innerGroupPanel", this.innerGroupPanel);
      setStringProperty(component, "innerGroupBgcolor", this.innerGroupBgcolor);
   }
   
   /**
    * @see org.alfresco.web.ui.common.tag.HtmlComponentTag#release()
    */
   public void release()
   {
      super.release();
      
      this.groupPanel = null;
      this.groupBgcolor = null;
      this.selectedGroupPanel = null;
      this.selectedGroupBgcolor = null;
      this.innerGroupPanel = null;
      this.innerGroupBgcolor = null;
   }
   
   /**
    * Set the groupPanel
    *
    * @param groupPanel     the groupPanel
    */
   public void setGroupPanel(String groupPanel)
   {
      this.groupPanel = groupPanel;
   }

   /**
    * Set the groupBgcolor
    *
    * @param groupBgcolor     the groupBgcolor
    */
   public void setGroupBgcolor(String groupBgcolor)
   {
      this.groupBgcolor = groupBgcolor;
   }

   /**
    * Set the selectedGroupPanel
    *
    * @param selectedGroupPanel     the selectedGroupPanel
    */
   public void setSelectedGroupPanel(String selectedGroupPanel)
   {
      this.selectedGroupPanel = selectedGroupPanel;
   }

   /**
    * Set the selectedGroupBgcolor
    *
    * @param selectedGroupBgcolor     the selectedGroupBgcolor
    */
   public void setSelectedGroupBgcolor(String selectedGroupBgcolor)
   {
      this.selectedGroupBgcolor = selectedGroupBgcolor;
   }

   /**
    * Set the innerGroupPanel
    *
    * @param innerGroupPanel     the innerGroupPanel
    */
   public void setInnerGroupPanel(String innerGroupPanel)
   {
      this.innerGroupPanel = innerGroupPanel;
   }

   /**
    * Set the innerGroupBgcolor
    *
    * @param innerGroupBgcolor     the innerGroupBgcolor
    */
   public void setInnerGroupBgcolor(String innerGroupBgcolor)
   {
      this.innerGroupBgcolor = innerGroupBgcolor;
   }


   /** the groupPanel */
   private String groupPanel;

   /** the groupBgcolor */
   private String groupBgcolor;

   /** the selectedGroupPanel */
   private String selectedGroupPanel;

   /** the selectedGroupBgcolor */
   private String selectedGroupBgcolor;

   /** the innerGroupPanel */
   private String innerGroupPanel;

   /** the innerGroupBgcolor */
   private String innerGroupBgcolor;
}
