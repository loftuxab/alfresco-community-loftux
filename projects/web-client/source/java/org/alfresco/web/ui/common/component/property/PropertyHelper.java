package org.alfresco.web.ui.common.component.property;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIOutput;
import javax.faces.component.UISelectBoolean;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.DateTimeConverter;
import javax.faces.el.ValueBinding;

import jsftest.repository.DataDictionary.Property;

import org.apache.log4j.Logger;

public class PropertyHelper
{
   private static Logger logger = Logger.getLogger(PropertyHelper.class);
   
   /**
    * Generates a JSF OutputText component/renderer
    * 
    * @param context
    * @param text
    * @param parent
    */
   public static void generateLabel(FacesContext context, String text,
                                    UIComponent parent)
   {
      UIOutput label = (UIOutput)context.getApplication().
                        createComponent("javax.faces.Output");
      label.setId(context.getViewRoot().createUniqueId());
      label.setRendererType("javax.faces.Text");
      label.setValue(text + ": ");
      parent.getChildren().add(label);
      
      if (logger.isDebugEnabled())
         logger.debug("Created label " + label.getClientId(context) + 
                      " and added it to component " + parent);
   }
   
   /**
    * Generates an appropriate control for the given property
    * 
    * @param context
    * @param propName
    * @param varName
    * @param property
    */
   public static void generateControl(FacesContext context, Property property, 
                                      String varName, UIComponent parent)
   {
      UIInput inputControl = null;
      ValueBinding vb = context.getApplication().
                        createValueBinding("#{" + varName + "." + 
                        property.getName() + "}");
      
      // generate the appropriate input field 
      String typeName = property.getType();
      
      if (typeName.equalsIgnoreCase("string"))
      {
         inputControl = (UIInput)context.getApplication().
                         createComponent("javax.faces.Input");
         inputControl.setRendererType("javax.faces.Text");
      }
      else if (typeName.equalsIgnoreCase("string[]"))
      {
         inputControl = (UIInput)context.getApplication().
                         createComponent("javax.faces.Input");
         
         // add a string array converter
         Converter conv = (Converter)context.getApplication().
                           createConverter("converter:StringArray");
         inputControl.setConverter(conv);
      }
      else if (typeName.equalsIgnoreCase("datetime"))
      {
         inputControl = (UIInput)context.getApplication().
                         createComponent("javax.faces.Input");
         inputControl.setRendererType("javax.faces.Text");
         
         // add a converter for datetime types
         DateTimeConverter conv = (DateTimeConverter)context.getApplication().
                                   createConverter("javax.faces.DateTime");
         conv.setDateStyle("short");
         conv.setPattern("d/MM/yyyy");
         inputControl.setConverter(conv);
      }
      else if (typeName.equalsIgnoreCase("boolean"))
      {
         inputControl = (UISelectBoolean)context.getApplication().
                         createComponent("javax.faces.SelectBoolean");
         inputControl.setRendererType("javax.faces.Checkbox");
      }
      else if (typeName.equalsIgnoreCase("enum"))
      {
         // TODO: Handle lists of values by retrieving them from DD
      }
      
      // set up the common aspects of the control
      inputControl.setId(context.getViewRoot().createUniqueId());
      inputControl.setValueBinding("value", vb);
      
      if (property.isReadOnly())
      {
         inputControl.getAttributes().put("disabled", Boolean.TRUE);
      }
      
      // add a validator if the field is required
//      if (property.isMandatory())
//      {
//         inputControl.setRequired(true);
//         LengthValidator val = (LengthValidator)context.getApplication().
//                                createValidator("javax.faces.Length");
//         val.setMinimum(1);
//         inputControl.addValidator(val);
//      }
      
      parent.getChildren().add(inputControl);
      
      if (logger.isDebugEnabled())
         logger.debug("Created control " + inputControl + "(" + 
                      inputControl.getClientId(context) + 
                      ") and added it to component " + parent);
   }
}
