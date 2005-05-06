package org.alfresco.web.ui.common.component.property;

import java.io.IOException;

import javax.faces.application.FacesMessage;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import jsftest.repository.DataDictionary;
import jsftest.repository.DataDictionary.MetaData;
import jsftest.repository.DataDictionary.Property;

import org.apache.log4j.Logger;

import org.alfresco.web.bean.repository.Node;

/**
 * Component to represent an individual property
 * 
 * @author gavinc
 */
public class UIProperty extends UIPanel implements NamingContainer
{
   private static Logger logger = Logger.getLogger(UIProperty.class);
   
   /**
    * Default constructor
    */
   public UIProperty()
   {
      // set the default renderer for a property
      setRendererType("javax.faces.Grid");
   }
   
   /**
    * @see javax.faces.component.UIComponent#getFamily()
    */
   public String getFamily()
   {
      return "javax.faces.Panel";
   }

   /**
    * @see javax.faces.component.UIComponentBase#encodeBegin(javax.faces.context.FacesContext)
    */
   public void encodeBegin(FacesContext context) throws IOException
   {
      // get the variable being used from the parent
      UIComponent parent = this.getParent();
      if ((parent instanceof UIPropertySheet) == false)
      {
         throw new IllegalStateException("The property component must be nested within a property sheet component");
      }
      
      // only build the components if there are currently no children
      int howManyKids = getChildren().size();
      if (howManyKids == 0)
      {
         Node node = ((UIPropertySheet)parent).getNode();
         String var = ((UIPropertySheet)parent).getVar();
         String propertyName = (String)getAttributes().get("value");
      
         if (propertyName == null)
         {
            ValueBinding vb = getValueBinding("value");
            if (vb != null)
            {
               propertyName = (String)vb.getValue(context);
            }
         }
   
         DataDictionary dd = new DataDictionary();
         MetaData metaData = dd.getMetaData(node.getType());
         Property prop = (Property)metaData.getPropertiesMap().get(propertyName); 
         
         if (prop == null)
         {
            // add an error message
            String msg = "Property '"+ propertyName + "' is not available for this node";
            context.addMessage(this.getClientId(context), 
                               new FacesMessage(FacesMessage.SEVERITY_WARN, msg, msg));
         }
         else
         {
            // generate the label
            PropertyHelper.generateLabel(context, prop.getDisplayName(), this);
         
            // generate the input field
            PropertyHelper.generateControl(context, prop, var, this);
         }
      }
      
      super.encodeBegin(context);
   }
   
   /**
    * @see javax.faces.component.StateHolder#restoreState(javax.faces.context.FacesContext, java.lang.Object)
    */
   public void restoreState(FacesContext context, Object state)
   {
      Object values[] = (Object[])state;
      // standard component attributes are restored by the super class
      super.restoreState(context, values[0]);
   }
   
   /**
    * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
    */
   public Object saveState(FacesContext context)
   {
      Object values[] = new Object[8];
      // standard component attributes are saved by the super class
      values[0] = super.saveState(context);
      return (values);
   }
}
