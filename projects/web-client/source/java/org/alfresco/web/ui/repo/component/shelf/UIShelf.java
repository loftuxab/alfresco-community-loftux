/*
 * Created on 31-May-2005
 */
package org.alfresco.web.ui.repo.component.shelf;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;

import org.alfresco.web.ui.common.PanelGenerator;
import org.alfresco.web.ui.common.Utils;
import org.alfresco.web.ui.common.component.SelfRenderingComponent;

/**
 * @author Kevin Roast
 */
public class UIShelf extends SelfRenderingComponent
{
   // ------------------------------------------------------------------------------
   // Component Impl 

   /**
    * @see javax.faces.component.UIComponent#getFamily()
    */
   public String getFamily()
   {
      return "org.alfresco.faces.Shelf";
   }
   
   /**
    * @see javax.faces.component.StateHolder#restoreState(javax.faces.context.FacesContext, java.lang.Object)
    */
   public void restoreState(FacesContext context, Object state)
   {
      Object values[] = (Object[])state;
      // standard component attributes are restored by the super class
      super.restoreState(context, values[0]);
      this.groupPanel = (String)values[1];
      this.groupBgcolor = (String)values[2];
      this.selectedGroupPanel = (String)values[3];
      this.selectedGroupBgcolor = (String)values[4];
      this.innerGroupPanel = (String)values[5];
      this.innerGroupBgcolor = (String)values[6];
   }
   
   /**
    * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
    */
   public Object saveState(FacesContext context)
   {
      Object values[] = new Object[7];
      // standard component attributes are saved by the super class
      values[0] = super.saveState(context);
      values[1] = this.groupPanel;
      values[2] = this.groupBgcolor;
      values[3] = this.selectedGroupPanel;
      values[4] = this.selectedGroupBgcolor;
      values[5] = this.innerGroupPanel;
      values[6] = this.innerGroupBgcolor;
      return values;
   }
   
   /**
    * @see javax.faces.component.UIComponentBase#decode(javax.faces.context.FacesContext)
    */
   public void decode(FacesContext context)
   {
      Map requestMap = context.getExternalContext().getRequestParameterMap();
      String fieldId = getHiddenFieldName();
      String value = (String)requestMap.get(fieldId);
      
      // we encoded the value to start with our Id
      if (value != null && value.length() != 0)
      {
         int sepIndex = value.indexOf(NamingContainer.SEPARATOR_CHAR);
         int groupIndex = Integer.parseInt( value.substring(0, sepIndex) );
         boolean expanded = Boolean.parseBoolean( value.substring(sepIndex + 1) );
         
         // TODO: should we fire an event here instead of setting state directly?!
         int index = 0;
         for (Iterator i=this.getChildren().iterator(); i.hasNext(); index++)
         {
            UIComponent child = (UIComponent)i.next();
            if (index == groupIndex && child instanceof UIShelfGroup)
            {
               // found correct child - set the new state
               ((UIShelfGroup)child).setExpanded(expanded);
               break;
            }
         }
      }
   }
   
   /**
    * @see javax.faces.component.UIComponentBase#encodeBegin(javax.faces.context.FacesContext)
    */
   public void encodeBegin(FacesContext context) throws IOException
   {
      if (isRendered() == false)
      {
         return;
      }
      
      ResponseWriter out = context.getResponseWriter();
      
      // TODO: allow config of spacing between ShelfGroup components
      out.write("<table border=0 cellspacing=2 cellpadding=0 width=100%>");
   }
   
   /**
    * @see javax.faces.component.UIComponentBase#encodeChildren(javax.faces.context.FacesContext)
    */
   public void encodeChildren(FacesContext context) throws IOException
   {
      if (isRendered() == false)
      {
         return;
      }
      
      ResponseWriter out = context.getResponseWriter();
      
      // output each shelf group in turn
      int index = 0;
      for (Iterator i=this.getChildren().iterator(); i.hasNext(); index++)
      {
         UIComponent child = (UIComponent)i.next();
         if (child instanceof UIShelfGroup)
         {
            UIShelfGroup group = (UIShelfGroup)child;
            if (group.isRendered() == true)
            {
               // output the surrounding structure then call the component to render itself and children
               boolean isExpanded = group.isExpanded();      // TODO: get this from Shelf or ShelfGroup?
               out.write("<tr><td>");
               
               String contextPath = context.getExternalContext().getRequestContextPath();
               
               // output appropriate panel start section and bgcolor
               String groupPanel;
               String groupBgcolor;
               if (isExpanded == false)
               {
                  groupPanel = getGroupPanel();
                  groupBgcolor = getGroupBgcolor();
               }
               else
               {
                  groupPanel = getSelectedGroupPanel();
                  groupBgcolor = getSelectedGroupBgcolor();
               }
               if (groupBgcolor == null)
               {
                  groupBgcolor = PanelGenerator.BGCOLOR_WHITE;
               }
               if (groupPanel != null)
               {
                  PanelGenerator.generatePanelStart(out, contextPath, groupPanel, groupBgcolor);
               }
               
               // output appropriate expanded icon state
               out.write("<nobr>");
               out.write("<a href='#' onclick=\"");
               // encode value as the index of the ShelfGroup clicked and the new state
               String value = Integer.toString(index) + NamingContainer.SEPARATOR_CHAR + Boolean.toString(!isExpanded);
               out.write(Utils.generateFormSubmit(context, this, getHiddenFieldName(), value));
               out.write("\">");
               if (isExpanded == true)
               {
                  out.write(Utils.buildImageTag(context, EXPANDED_IMG, 10, 10, ""));
               }
               else
               {
                  out.write(Utils.buildImageTag(context, COLLAPSED_IMG, 10, 10, ""));
               }
               out.write("</a>&nbsp;");
               
               // output title label text
               String label = group.getLabel();
               out.write("<span");
               outputAttribute(out, group.getAttributes().get("style"), "style");
               outputAttribute(out, group.getAttributes().get("styleClass"), "class");
               out.write('>');
               out.write(Utils.encode(label));
               out.write("</span>");
               out.write("</nobr>");
               
               if (isExpanded == true)
               {
                  // if this is the expanded group, output the inner panel 
                  out.write("<br>");
                  String innerGroupPanel = getInnerGroupPanel();
                  String innerGroupBgcolor = getInnerGroupBgcolor();
                  if (innerGroupBgcolor == null)
                  {
                     innerGroupBgcolor = PanelGenerator.BGCOLOR_WHITE;
                  }
                  if (innerGroupPanel != null)
                  {
                     PanelGenerator.generatePanelStart(out, contextPath, innerGroupPanel, innerGroupBgcolor);
                  }
                  
                  // allow child components to render themselves
                  Utils.encodeRecursive(context, group);
                  
                  if (innerGroupPanel != null)
                  {
                     PanelGenerator.generatePanelEnd(out, contextPath, innerGroupPanel);
                  }
               }
               
               // output panel and group end elements
               PanelGenerator.generatePanelEnd(out, contextPath, groupPanel);
               out.write("</td></tr>");
            }
         }
      }
   }
   
   /**
    * @see javax.faces.component.UIComponentBase#encodeEnd(javax.faces.context.FacesContext)
    */
   public void encodeEnd(FacesContext context) throws IOException
   {
      if (isRendered() == false)
      {
         return;
      }
      
      ResponseWriter out = context.getResponseWriter();
      
      out.write("</table>");
   }

   /**
    * @see javax.faces.component.UIComponentBase#getRendersChildren()
    */
   public boolean getRendersChildren()
   {
      return true;
   }

   
   // ------------------------------------------------------------------------------
   // Strongly typed component property accessors 
   
   /**
    * @return Returns the group panel name.
    */
   public String getGroupPanel()
   {
      ValueBinding vb = getValueBinding("groupPanel");
      if (vb != null)
      {
         this.groupPanel = (String)vb.getValue(getFacesContext());
      }
      
      return this.groupPanel;
   }
   
   /**
    * @param groupPanel    The group panel name to set.
    */
   public void setGroupPanel(String groupPanel)
   {
      this.groupPanel = groupPanel;
   }
   
   /**
    * @return Returns the group background colour.
    */
   public String getGroupBgcolor()
   {
      ValueBinding vb = getValueBinding("groupBgcolor");
      if (vb != null)
      {
         this.groupBgcolor = (String)vb.getValue(getFacesContext());
      }
      
      return this.groupBgcolor;
   }
   
   /**
    * @param groupBgcolor    The group background colour to set.
    */
   public void setGroupBgcolor(String groupBgcolor)
   {
      this.groupBgcolor = groupBgcolor;
   }
   
   /**
    * @return Returns the selected group panel name.
    */
   public String getSelectedGroupPanel()
   {
      ValueBinding vb = getValueBinding("selectedGroupPanel");
      if (vb != null)
      {
         this.selectedGroupPanel = (String)vb.getValue(getFacesContext());
      }
      
      return this.selectedGroupPanel;
   }
   
   /**
    * @param selectedGroupPanel    The selected group panel name to set.
    */
   public void setSelectedGroupPanel(String selectedGroupPanel)
   {
      this.selectedGroupPanel = selectedGroupPanel;
   }
   
   /**
    * @return Returns the selected group background colour.
    */
   public String getSelectedGroupBgcolor()
   {
      ValueBinding vb = getValueBinding("selectedGroupBgcolor");
      if (vb != null)
      {
         this.selectedGroupBgcolor = (String)vb.getValue(getFacesContext());
      }
      
      return this.selectedGroupBgcolor;
   }
   
   /**
    * @param selectedGroupBgcolor    The selected group background colour to set.
    */
   public void setSelectedGroupBgcolor(String selectedGroupBgcolor)
   {
      this.selectedGroupBgcolor = selectedGroupBgcolor;
   }
   
   /**
    * @return Returns the inner group panel name.
    */
   public String getInnerGroupPanel()
   {
      ValueBinding vb = getValueBinding("innerGroupPanel");
      if (vb != null)
      {
         this.innerGroupPanel = (String)vb.getValue(getFacesContext());
      }
      
      return this.innerGroupPanel;
   }
   
   /**
    * @param innerGroupPanel    The inner group panel name to set.
    */
   public void setInnerGroupPanel(String innerGroupPanel)
   {
      this.innerGroupPanel = innerGroupPanel;
   }
   
   /**
    * @return Returns the inner group background colour.
    */
   public String getInnerGroupBgcolor()
   {
      ValueBinding vb = getValueBinding("innerGroupBgcolor");
      if (vb != null)
      {
         this.innerGroupBgcolor = (String)vb.getValue(getFacesContext());
      }
      
      return this.innerGroupBgcolor;
   }
   
   /**
    * @param innerGroupBgcolor    The inner group background colour to set.
    */
   public void setInnerGroupBgcolor(String innerGroupBgcolor)
   {
      this.innerGroupBgcolor = innerGroupBgcolor;
   }
   
   
   // ------------------------------------------------------------------------------
   // Private helpers
   
   /**
    * We use a hidden field name on the assumption that very few shelf instances will
    * be present on a single page.
    * 
    * @return hidden field name
    */
   private String getHiddenFieldName()
   {
      return getClientId(getFacesContext());
   }
   
   
   // ------------------------------------------------------------------------------
   // Constants 
   
   private final static String EXPANDED_IMG  = "/images/icons/expanded.gif";
   private final static String COLLAPSED_IMG = "/images/icons/collapsed.gif";
   
   
   // ------------------------------------------------------------------------------
   // Private data
   
   /** component properties */
   private String groupPanel;
   private String groupBgcolor;
   private String selectedGroupPanel;
   private String selectedGroupBgcolor;
   private String innerGroupPanel;
   private String innerGroupBgcolor;
}
