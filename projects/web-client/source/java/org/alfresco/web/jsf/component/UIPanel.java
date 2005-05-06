/*
 * Created on Mar 30, 2005
 */
package org.alfresco.web.jsf.component;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;

import org.alfresco.web.PanelGenerator;
import org.alfresco.web.jsf.Utils;

/**
 * @author kevinr
 */
public class UIPanel extends SelfRenderingComponent
{
   // ------------------------------------------------------------------------------
   // Component Impl 
   
   /**
    * @see javax.faces.component.UIComponent#getFamily()
    */
   public String getFamily()
   {
      return "awc.faces.Controls";
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
      
      String bgcolor = getBgcolor();
      if (bgcolor == null)
      {
         bgcolor = PanelGenerator.BGCOLOR_WHITE;
      }
      
      // output first part of border table
      if (getBorder() != null)
      {
         PanelGenerator.generatePanelStart(
               out,
               context.getExternalContext().getRequestContextPath(),
               getBorder(),
               bgcolor);
      }
      
      // output textual label
      String label = getLabel();
      if (label != null)
      {
         out.write("<span");
         outputAttribute(out, getAttributes().get("style"), "style");
         outputAttribute(out, getAttributes().get("styleClass"), "class");
         out.write('>');
         
         out.write(Utils.encode(label));
         
         out.write("</span>&nbsp;");
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
            out.write(Utils.buildImageTag(context, EXPANDED_IMG, 7, 6, ""));
         }
         else
         {
            out.write(Utils.buildImageTag(context, COLLAPSED_IMG, 6, 7, ""));
         }
         
         out.write("</a>");
      }
      
      // start panel contents on new line if we added any adornments
      if (label != null || isProgressive() == true)
      {
         out.write("<br>");
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
      if (getBorder() != null)
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
         // we were clicked
         // strip out the boolean value from the field contents
         setExpanded( Boolean.valueOf( value.substring(getClientId(context).length() + 1) ).booleanValue() );
         
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
   }
   
   /**
    * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
    */
   public Object saveState(FacesContext context)
   {
      Object values[] = new Object[2];
      // standard component attributes are saved by the super class
      values[0] = super.saveState(context);
      values[1] = (isExpanded() ? Boolean.TRUE : Boolean.FALSE);
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
   
   private final static String EXPANDED_IMG  = "/images/arrow_expanded.gif";
   private final static String COLLAPSED_IMG = "/images/arrow_collapsed.gif";
   
   
   // ------------------------------------------------------------------------------
   // Private members 
   
   // component settings
   private String border = null;
   private String bgcolor = null;
   private Boolean progressive = null;
   private String label = null;
   
   // component state
   private Boolean expanded = Boolean.TRUE;
}
