/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.web.ui.repo.component.property;

import java.io.IOException;
import java.text.MessageFormat;

import javax.faces.FacesException;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.el.ValueBinding;

import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.web.app.Application;
import org.alfresco.web.bean.repository.DataDictionary;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.ui.common.Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.jsf.FacesContextUtils;

/**
 * Component to represent an individual association within a property sheet
 * 
 * @author gavinc
 */
public class UIAssociation extends UIPanel implements NamingContainer
{
   private static final String MSG_ERROR_ASSOC = "error_association";
   private static final String MSG_ERROR_NOT_ASSOC = "error_not_association";

   private static Log logger = LogFactory.getLog(UIAssociation.class);
   
   private String name;
   private String displayLabel;
   private String converter;
   private Boolean readOnly;
   
   /**
    * Default constructor
    */
   public UIAssociation()
   {
      // set the default renderer
      setRendererType("org.alfresco.faces.AssociationRenderer");
   }
   
   /**
    * @see javax.faces.component.UIComponent#getFamily()
    */
   public String getFamily()
   {
      return "org.alfresco.faces.Association";
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
         throw new IllegalStateException("The association component must be nested within a property sheet component");
      }
      
      // only build the components if there are currently no children
      int howManyKids = getChildren().size();
      if (howManyKids == 0)
      {
         Node node = ((UIPropertySheet)parent).getNode();
         String var = ((UIPropertySheet)parent).getVar();
         String associationName = (String)getName();
   
         // get details of the association
         DataDictionary dd = (DataDictionary)FacesContextUtils.getRequiredWebApplicationContext(
               context).getBean(Application.BEAN_DATA_DICTIONARY);
         AssociationDefinition assocDef = dd.getAssociationDefinition(node, associationName);
         
         if (assocDef == null)
         {
            logger.warn("Failed to find association definition for association '" + associationName + "'");
            
            // add an error message as the property is not defined in the data dictionary and 
            // not in the node's set of properties
            String msg = MessageFormat.format(Application.getMessage(context, MSG_ERROR_ASSOC), new Object[] {associationName});
            Utils.addErrorMessage(msg);
         }
         else
         {
            // we've found the association definition but we also need to check
            // that the association is a parent child one
            if (assocDef.isChild())
            {
               String msg = MessageFormat.format(Application.getMessage(context, MSG_ERROR_NOT_ASSOC), new Object[] {associationName});
               Utils.addErrorMessage(msg);
            }
            else
            {
               String displayLabel = (String)getDisplayLabel();
               if (displayLabel == null)
               {
                  // try and get the repository assigned label
                  displayLabel = assocDef.getTitle();
                  
                  // if the label is still null default to the local name of the property
                  if (displayLabel == null)
                  {
                     displayLabel = assocDef.getName().getLocalName();
                  }
               }
               
               // generate the label and type specific control
               generateLabel(context, displayLabel);
               generateControl(context, assocDef, var);
            }
         }
      }
      
      super.encodeBegin(context);
   }
   
   /**
    * @return Returns the display label
    */
   public String getDisplayLabel()
   {
      if (this.displayLabel == null)
      {
         ValueBinding vb = getValueBinding("displayLabel");
         if (vb != null)
         {
            this.displayLabel = (String)vb.getValue(getFacesContext());
         }
      }
      
      return this.displayLabel;
   }

   /**
    * @param displayLabel Sets the display label
    */
   public void setDisplayLabel(String displayLabel)
   {
      this.displayLabel = displayLabel;
   }

   /**
    * @return Returns the name
    */
   public String getName()
   {
      if (this.name == null)
      {
         ValueBinding vb = getValueBinding("name");
         if (vb != null)
         {
            this.name = (String)vb.getValue(getFacesContext());
         }
      }
      
      return this.name;
   }

   /**
    * @param name Sets the name
    */
   public void setName(String name)
   {
      this.name = name;
   }
   
   /**
    * @return Returns the converter
    */
   public String getConverter()
   {
      if (this.converter == null)
      {
         ValueBinding vb = getValueBinding("converter");
         if (vb != null)
         {
            this.converter = (String)vb.getValue(getFacesContext());
         }
      }
      
      return this.converter;
   }

   /**
    * @param converter Sets the converter
    */
   public void setConverter(String converter)
   {
      this.converter = converter;
   }

   /**
    * @return Returns whether the property is read only
    */
   public boolean isReadOnly()
   {
      if (this.readOnly == null)
      {
         ValueBinding vb = getValueBinding("readOnly");
         if (vb != null)
         {
            this.readOnly = (Boolean)vb.getValue(getFacesContext());
         }
      }
      
      if (this.readOnly == null)
      {
         this.readOnly = Boolean.FALSE;
      }
      
      return this.readOnly;
   }

   /**
    * @param readOnly Sets the read only flag for the component
    */
   public void setReadOnly(boolean readOnly)
   {
      this.readOnly = readOnly;
   }
   
   /**
    * @see javax.faces.component.StateHolder#restoreState(javax.faces.context.FacesContext, java.lang.Object)
    */
   public void restoreState(FacesContext context, Object state)
   {
      Object values[] = (Object[])state;
      // standard component attributes are restored by the super class
      super.restoreState(context, values[0]);
      this.name = (String)values[1];
      this.displayLabel = (String)values[2];
      this.readOnly = (Boolean)values[3];
      this.converter = (String)values[4];
   }
   
   /**
    * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
    */
   public Object saveState(FacesContext context)
   {
      Object values[] = new Object[5];
      // standard component attributes are saved by the super class
      values[0] = super.saveState(context);
      values[1] = this.name;
      values[2] = this.displayLabel;
      values[3] = this.readOnly;
      values[4] = this.converter;
      return (values);
   }
   
   /**
    * Generates a JSF OutputText component/renderer
    * 
    * @param context JSF context
    * @param displayLabel The display label text
    * @param parent The parent component for the label
    */
   private void generateLabel(FacesContext context, String displayLabel)
   {
      UIOutput label = (UIOutput)context.getApplication().
                        createComponent("javax.faces.Output");
      label.setId(context.getViewRoot().createUniqueId());
      label.setRendererType("javax.faces.Text");
      label.setValue(displayLabel + ": ");
      this.getChildren().add(label);
      
      if (logger.isDebugEnabled())
         logger.debug("Created label " + label.getClientId(context) + 
                      " for '" + displayLabel + "' and added it to component " + this);
   }
   
   /**
    * Generates an appropriate control for the given property
    * 
    * @param context JSF context
    * @param propDef The definition of the association to create the control for
    * @param varName Name of the variable the node is stored in the session as 
    *                (used for value binding expression)
    * @param parent The parent component for the control
    */
   private void generateControl(FacesContext context, AssociationDefinition assocDef, 
                                String varName)
   {
      UIPropertySheet propSheet = (UIPropertySheet)this.getParent();

      if (propSheet.getMode().equalsIgnoreCase(UIPropertySheet.VIEW_MODE) || isReadOnly() || assocDef.isProtected())
      {
         ValueBinding vb = context.getApplication().
                        createValueBinding("#{" + varName + ".associations[\"" + 
                        assocDef.getName().toString() + "\"]}");
         
         // if we are in view mode simply output the text to the screen
         UIOutput control = (UIOutput)context.getApplication().createComponent("javax.faces.Output");
         control.setRendererType("javax.faces.Text");
         
         // if a converter has been specified we need to instantiate it
         // and apply it to the control otherwise add the standard one
         if (getConverter() == null)
         {
            // add the standard ChildAssociation converter that shows the current association state
            Converter conv = context.getApplication().createConverter("org.alfresco.faces.AssociationConverter");
            control.setConverter(conv);
         }
         else
         {
            // catch null pointer exception to workaround bug in myfaces
            try
            {
               Converter conv = context.getApplication().createConverter(getConverter());
               ((UIOutput)control).setConverter(conv);
            }
            catch (FacesException fe)
            {
               logger.warn("Converter " + getConverter() + " could not be applied");
            }
         }
         
         // set up the common aspects of the control
         control.setId(context.getViewRoot().createUniqueId());
         control.setValueBinding("value", vb);
         
         // add the control itself
         this.getChildren().add(control);
      }
      else
      {
         ValueBinding vb = context.getApplication().createValueBinding("#{" + varName + "}");
         
         UIAssociationEditor control = (UIAssociationEditor)context.
            getApplication().createComponent("org.alfresco.faces.AssociationEditor");
         control.setAssociationName(assocDef.getName().toString());
         
         // set up the common aspects of the control
         control.setId(context.getViewRoot().createUniqueId());
         control.setValueBinding("value", vb);
         
         // add the control itself
         this.getChildren().add(control);
      }
   }
}
