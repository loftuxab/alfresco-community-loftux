/*
 * Created on Mar 30, 2005
 */
package org.alfresco.web.ui.common.component;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.NamingContainer;
import javax.faces.component.UICommand;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;

import org.alfresco.web.ui.common.PanelGenerator;
import org.alfresco.web.ui.common.Utils;

/**
 * @author kevinr
 */
public class UIPanel extends UICommand
{
   // ------------------------------------------------------------------------------
   // Component Impl 
   
   /**
    * Default constructor
    */
   public UIPanel()
   {
      setRendererType(null);
   }
   
   /**
    * @see javax.faces.component.UIComponent#getFamily()
    */
   public String getFamily()
   {
      return "org.alfresco.faces.Controls";
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
      
      // determine if we have a link on the header
      boolean linkPresent = false;
      String linkLabel = getLinkLabel();
      String linkIcon = getLinkIcon();
      if (linkLabel != null || linkIcon != null)
      {
         linkPresent = true;
      }
      
      // determine whether we have any adornment
      String label = getLabel();
      if (label != null || isProgressive() == true || linkPresent == true)
      {
         this.hasAdornments = true;
      }
      
      // make sure we have a default background color for the content area
      String bgcolor = getBgcolor();
      if (bgcolor == null)
      {
         bgcolor = PanelGenerator.BGCOLOR_WHITE;
      }
      
      // determine if we have a bordered title area, note, we also need to have
      // the content area border defined as well
      if ((getTitleBgcolor() != null) && (getTitleBorder() != null) && 
          (getBorder() != null) && this.hasAdornments)
      {
         this.hasBorderedTitleArea = true;
      }
      
      // output first part of border table
      if (this.hasBorderedTitleArea)
      {
         PanelGenerator.generatePanelStart(
               out,
               context.getExternalContext().getRequestContextPath(),
               getTitleBorder(),
               getTitleBgcolor());
      }
      else if (getBorder() != null)
      {
         PanelGenerator.generatePanelStart(
               out,
               context.getExternalContext().getRequestContextPath(),
               getBorder(),
               bgcolor);
      }

      if (this.hasAdornments)
      {
         // start the containing table if we have any adornments
         out.write("<table border='0' cellspacing='0' cellpadding='0' width='100%'><tr><td>");
      }

      // output progressive disclosure icon in appropriate state
      // TODO: manage state of this icon via component Id!
      if (isProgressive() == true)
      {
         out.write("<a href='#' onclick=\"");
         String value = getClientId(context) + NamingContainer.SEPARATOR_CHAR + Boolean.toString(!isExpanded());
         out.write(Utils.generateFormSubmit(context, this, getHiddenFieldName(), value));
         out.write("\">");
         
         if (isExpanded() == true)
         {
            out.write(Utils.buildImageTag(context, EXPANDED_IMG, 11, 11, ""));
         }
         else
         {
            out.write(Utils.buildImageTag(context, COLLAPSED_IMG, 11, 11, ""));
         }
         
         out.write("</a>&nbsp;&nbsp;");
      }
      
      // output textual label
      if (label != null)
      {
         out.write("<span");
         Utils.outputAttribute(out, getAttributes().get("style"), "style");
         Utils.outputAttribute(out, getAttributes().get("styleClass"), "class");
         out.write('>');
         
         out.write(Utils.encode(label));
         
         out.write("</span>");
      }

      if (this.hasAdornments)
      {
         out.write("</td>");
      }
      
      if (linkPresent)
      {
         out.write("<td align='right'>");
         
         out.write("<a href=\"#\" onclick=\"");
         out.write(Utils.generateFormSubmit(context, this, getHiddenFieldName(), 
               getClientId(context) + NamingContainer.SEPARATOR_CHAR + LINK_CLICKED));
         out.write("\"");
         Utils.outputAttribute(out, getLinkStyleClass(), "class");
         Utils.outputAttribute(out, getLinkTooltip(), "title");
         out.write(">");
         
         if (getLinkIcon() != null)
         {
            out.write(Utils.buildImageTag(context, getLinkIcon(), getLinkTooltip(), "absmiddle"));
         }
         
         if (getLinkLabel() != null)
         {
            out.write("<span style='padding-left: 6px;'>");
            out.write(getLinkLabel());
            out.write("</span>");
         }
         
         out.write("</a></td>");
      }
      
      if (this.hasAdornments)
      {
         out.write("</tr></table>");
      }
      
      // if we have the titled border area, output the middle section
      if (this.hasBorderedTitleArea && isExpanded())
      {
         PanelGenerator.generateTitledPanelMiddle(
               out,
               context.getExternalContext().getRequestContextPath(),
               getTitleBorder(),
               getBorder(),
               getBgcolor());
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
      
      // output final part of border table
      if (this.hasBorderedTitleArea && isExpanded() == false)
      {
         PanelGenerator.generatePanelEnd(
               out,
               context.getExternalContext().getRequestContextPath(),
               getTitleBorder());
      }
      else if (getBorder() != null)
      {
         PanelGenerator.generatePanelEnd(
               out,
               context.getExternalContext().getRequestContextPath(),
               getBorder());
      }
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
      if (value != null && value.startsWith(getClientId(context) + NamingContainer.SEPARATOR_CHAR))
      {
         // we were clicked, strip out the value
         String strippedValue = value.substring(getClientId(context).length() + 1);
         
         if (strippedValue.equals(LINK_CLICKED))
         {
            // the action link was clicked, so queue the action event
            ActionEvent event = new ActionEvent(this);
            queueEvent(event);
         }
         else
         {
            // the expand/collapse icon was clicked, so toggle the state 
            setExpanded( Boolean.valueOf(strippedValue).booleanValue() );
         }
         
         //
         // TODO: See http://forums.java.sun.com/thread.jspa?threadID=524925&start=15&tstart=0
         //       Bug/known issue in JSF 1.1 RI
         //       This causes a problem where the View attempts to assign duplicate Ids
         //       to components when createUniqueId() on UIViewRoot is called before the
         //       render phase. This occurs in the Panel tag as it must call getComponent()
         //       early to decide whether to allow the tag to render contents or not.
         //
         // context.getViewRoot().setTransient(true);
         //
         //       The other solution is to explicity give ALL child components of the
         //       panel a unique Id rather than a generated one! 
      }
   }
   
   /**
    * @see javax.faces.component.StateHolder#restoreState(javax.faces.context.FacesContext, java.lang.Object)
    */
   public void restoreState(FacesContext context, Object state)
   {
      Object values[] = (Object[])state;
      // standard component attributes are restored by the super class
      super.restoreState(context, values[0]);
      setExpanded( ((Boolean)values[1]).booleanValue() );
      this.progressive = (Boolean)values[2];
      this.border = (String)values[3];
      this.bgcolor = (String)values[4];
      this.label = (String)values[5];
      this.linkLabel = (String)values[6];
      this.linkIcon = (String)values[7];
      this.linkStyleClass = (String)values[8];
      this.linkTooltip = (String)values[9];
      this.titleBgcolor = (String)values[10];
      this.titleBorder = (String)values[11];
   }
   
   /**
    * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
    */
   public Object saveState(FacesContext context)
   {
      Object values[] = new Object[12];
      // standard component attributes are saved by the super class
      values[0] = super.saveState(context);
      values[1] = (isExpanded() ? Boolean.TRUE : Boolean.FALSE);
      values[2] = this.progressive;
      values[3] = this.border;
      values[4] = this.bgcolor;
      values[5] = this.label;
      values[6] = this.linkLabel;
      values[7] = this.linkIcon;
      values[8] = this.linkStyleClass;
      values[9] = this.linkTooltip;
      values[10] = this.titleBgcolor;
      values[11] = this.titleBorder;
      return values;
   }
   
   
   // ------------------------------------------------------------------------------
   // Strongly typed component property accessors 
   
   /**
    * @return Returns the bgcolor.
    */
   public String getBgcolor()
   {
      ValueBinding vb = getValueBinding("bgcolor");
      if (vb != null)
      {
         this.bgcolor = (String)vb.getValue(getFacesContext());
      }
      
      return this.bgcolor;
   }
   
   /**
    * @param bgcolor    The bgcolor to set.
    */
   public void setBgcolor(String bgcolor)
   {
      this.bgcolor = bgcolor;
   }

   /**
    * @return Returns the border name.
    */
   public String getBorder()
   {
      ValueBinding vb = getValueBinding("border");
      if (vb != null)
      {
         this.border = (String)vb.getValue(getFacesContext());
      }
      
      return this.border;
   }

   /**
    * @param border  The border name to user.
    */
   public void setBorder(String border)
   {
      this.border = border;
   }
   
   /**
    * @return Returns the bgcolor of the title area
    */
   public String getTitleBgcolor()
   {
      ValueBinding vb = getValueBinding("titleBgcolor");
      if (vb != null)
      {
         this.titleBgcolor = (String)vb.getValue(getFacesContext());
      }
      
      return this.titleBgcolor;
   }

   /**
    * @param titleBgcolor Sets the bgcolor of the title area
    */
   public void setTitleBgcolor(String titleBgcolor)
   {
      this.titleBgcolor = titleBgcolor;
   }

   /**
    * @return Returns the border style of the title area
    */
   public String getTitleBorder()
   {
      ValueBinding vb = getValueBinding("titleBorder");
      if (vb != null)
      {
         this.titleBorder = (String)vb.getValue(getFacesContext());
      }
      
      return this.titleBorder;
   }

   /**
    * @param titleBorder Sets the border style of the title area
    */
   public void setTitleBorder(String titleBorder)
   {
      this.titleBorder = titleBorder;
   }

   /**
    * @return Returns the label.
    */
   public String getLabel()
   {
      ValueBinding vb = getValueBinding("label");
      if (vb != null)
      {
         this.label = (String)vb.getValue(getFacesContext());
      }
      
      return this.label;
   }

   /**
    * @param label The label to set.
    */
   public void setLabel(String label)
   {
      this.label = label;
   }
   
   /**
    * @return Returns the icon to use for the link
    */
   public String getLinkIcon()
   {
      ValueBinding vb = getValueBinding("linkIcon");
      if (vb != null)
      {
         this.linkIcon = (String)vb.getValue(getFacesContext());
      }
      
      return this.linkIcon;
   }

   /**
    * @param linkIcon Sets the link icon
    */
   public void setLinkIcon(String linkIcon)
   {
      this.linkIcon = linkIcon;
   }

   /**
    * @return Returns the label to use for the link
    */
   public String getLinkLabel()
   {
      ValueBinding vb = getValueBinding("linkLabel");
      if (vb != null)
      {
         this.linkLabel = (String)vb.getValue(getFacesContext());
      }
      
      return this.linkLabel;
   }

   /**
    * @param linkLabel Sets the link label
    */
   public void setLinkLabel(String linkLabel)
   {
      this.linkLabel = linkLabel;
   }

   /**
    * @return Returns the link style class
    */
   public String getLinkStyleClass()
   {
      ValueBinding vb = getValueBinding("linkStyleClass");
      if (vb != null)
      {
         this.linkStyleClass = (String)vb.getValue(getFacesContext());
      }
      
      return this.linkStyleClass;
   }

   /**
    * @param linkStyleClass Sets the link style class
    */
   public void setLinkStyleClass(String linkStyleClass)
   {
      this.linkStyleClass = linkStyleClass;
   }

   /**
    * @return Returns the tooltip for the link
    */
   public String getLinkTooltip()
   {
      ValueBinding vb = getValueBinding("linkTooltip");
      if (vb != null)
      {
         this.linkTooltip = (String)vb.getValue(getFacesContext());
      }
      
      return this.linkTooltip;
   }

   /**
    * @param linkTooltip Sets the link tooltip
    */
   public void setLinkTooltip(String linkTooltip)
   {
      this.linkTooltip = linkTooltip;
   }

   /**
    * @return Returns the progressive display setting.
    */
   public boolean isProgressive()
   {
      ValueBinding vb = getValueBinding("progressive");
      if (vb != null)
      {
         this.progressive = (Boolean)vb.getValue(getFacesContext());
      }
      
      if (this.progressive != null)
      {
         return this.progressive.booleanValue();
      }
      else
      {
         // return default
         return false;
      }
   }
   
   /**
    * @param progressive   The progressive display boolean to set.
    */
   public void setProgressive(boolean progressive)
   {
      this.progressive = Boolean.valueOf(progressive);
   }
   
   /**
    * Returns whether the component show allow rendering of its child components.
    */
   public boolean isExpanded()
   {
      ValueBinding vb = getValueBinding("expanded");
      if (vb != null)
      {
         this.expanded = (Boolean)vb.getValue(getFacesContext());
      }
      
      if (this.expanded != null)
      {
         return this.expanded.booleanValue();
      }
      else
      {
         // return default
         return true;
      }
   }
   
   /**
    * Sets whether the component show allow rendering of its child components.
    * For this component we change this value if the user indicates to change the
    * hidden/visible state of the progressive panel.
    */
   public void setExpanded(boolean expanded)
   {
      this.expanded = Boolean.valueOf(expanded);
   }
   
   
   // ------------------------------------------------------------------------------
   // Private helpers
   
   /**
    * We use a hidden field name based on the parent form component Id and
    * the string "panel" to give a hidden field name that can be shared by all panels
    * within a single UIForm component.
    * 
    * @return hidden field name
    */
   private String getHiddenFieldName()
   {
      UIForm form = Utils.getParentForm(getFacesContext(), this);
      return form.getClientId(getFacesContext()) + NamingContainer.SEPARATOR_CHAR + "panel";
   }
   
   
   // ------------------------------------------------------------------------------
   // Constants 
   
   private final static String EXPANDED_IMG  = "/images/icons/expanded.gif";
   private final static String COLLAPSED_IMG = "/images/icons/collapsed.gif";
   private final static String LINK_CLICKED = "link-clicked";
   
   // ------------------------------------------------------------------------------
   // Private members 
   
   // component settings
   private String border = null;
   private String bgcolor = null;
   private String titleBorder = null;
   private String titleBgcolor = null;
   private Boolean progressive = null;
   private String label = null;
   private String linkLabel = null;
   private String linkIcon = null;
   private String linkTooltip = null;
   private String linkStyleClass = null;
   
   // component state
   private boolean hasAdornments = false;
   private boolean hasBorderedTitleArea = false;
   private Boolean expanded = Boolean.TRUE;
}
