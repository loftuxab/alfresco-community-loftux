package com.activiti.web.jsf.component.property;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIOutput;
import javax.faces.component.UISelectBoolean;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.DateTimeConverter;
import javax.faces.el.ValueBinding;
import jsftest.repository.BaseContentObject;
import jsftest.repository.DataDictionary;
import jsftest.repository.Repository;
import jsftest.repository.DataDictionary.MetaData;
import jsftest.repository.DataDictionary.Property;
import org.apache.log4j.Logger;

/**
 * Component that represents the properties of an object
 * 
 * @author gavinc
 */
public class UIPropertySheet extends UIInput implements NamingContainer
{
   // *********************************************************
   // TODO: Try extending the standard Panel then the standard
   //       javax.faces.Panel family can be used
   // *********************************************************
   
   private static Logger s_logger = Logger.getLogger(UIPropertySheet.class);
   
   private BaseContentObject m_obj;
   
   /**
    * Default constructor
    */
   public UIPropertySheet()
   {
      // set the default renderer for a property sheet
      setRendererType("awc.faces.Grid");
   }
   
   /**
    * @see javax.faces.component.UIComponent#getFamily()
    */
   public String getFamily()
   {
      return "activiti:PropertyFamily";
   }

   /**
    * @see javax.faces.component.UIComponent#encodeBegin(javax.faces.context.FacesContext)
    */
   public void encodeBegin(FacesContext context) throws IOException
   {
      s_logger.debug("*** In property sheet component encodeBegin ***");
      
      int howManyKids = getChildren().size();
      String configFile = (String)getAttributes().get("var");
      BaseContentObject contentObject = getContentObject(); 
      String var = null;
      
      if (configFile != null)
      {
         // TODO: configure the component using an external file
      }
      else
      {
         if (howManyKids == 0)
         {
            var = "obj";
	         DataDictionary dd = new DataDictionary();
	         MetaData metaData = dd.getMetaData(contentObject.getType());
	         createComponents(context, metaData);
         }
      }
      
      // get the var from the attributes if we don't know it yet
      if (var == null)
      {
         var = (String)getAttributes().get("var");
      }
      
      if (var != null)
      {
         Map sessionMap = getFacesContext().getExternalContext().getSessionMap();
         sessionMap.put(var, contentObject);
         
         if (s_logger.isDebugEnabled())
            s_logger.debug("Put object into session: " + contentObject);
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
      m_obj = (BaseContentObject)values[1];
   }
   
   /**
    * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
    */
   public Object saveState(FacesContext context)
   {
      Object values[] = new Object[8];
      // standard component attributes are saved by the super class
      values[0] = super.saveState(context);
      values[1] = m_obj;
      return (values);
   }
   
   /**
    * @return Returns the content object
    */
   public BaseContentObject getContentObject()
   {
      // use the value to get hold of the actual object
      if (m_obj == null)
      {
         String path = (String)getValue();
         m_obj = Repository.getObject(path);
      }
      
      return m_obj;
   }
   
   /**
    * @param obj The content object to set
    */
   public void setContentObject(BaseContentObject obj)
   {
      m_obj = obj;
   }
   
   private void createComponents(FacesContext context, MetaData metaData)
   	throws IOException
   {
      Iterator iter = metaData.getProperties().iterator();
      while (iter.hasNext())
      {
         Property objProp = (Property)iter.next(); 
            
         // dynamically add the string props to start with
         UIComponent prop = context.getApplication().
                            createComponent("awc.faces.Property");
         
         // generate the label
         UIOutput label = (UIOutput)context.getApplication().
                           createComponent("javax.faces.Output");
         label.setRendererType("javax.faces.Text");
         label.setValue(objProp.getDisplayName() + ": ");
         prop.getChildren().add(label);
         
         ValueBinding vb = context.getApplication().
                           createValueBinding("#{obj." + objProp.getName() + "}");
         
         // generate the appropriate input field 
         String typeName = objProp.getType();
         if (typeName.equalsIgnoreCase("string"))
         {
            UIInput input = (UIInput)context.getApplication().
                             createComponent("javax.faces.Input");
	         input.setRendererType("javax.faces.Text");
	         input.setValueBinding("value", vb);
	         
	         if (objProp.isReadOnly())
	         {
	            input.getAttributes().put("disabled", new Boolean(true));
	         }
	         
	         prop.getChildren().add(input);
         }
         else if (typeName.equalsIgnoreCase("string[]"))
         {
            UIInput input = (UIInput)context.getApplication().
                             createComponent("javax.faces.Input");
	         input.setRendererType("javax.faces.Text");
	         input.setValueBinding("value", vb);
	         
	         if (objProp.isReadOnly())
	         {
	            input.getAttributes().put("disabled", new Boolean(true));
	         }
	         
	         // add a string array converter
	         Converter conv = (Converter)context.getApplication().
	                           createConverter("converter:StringArray");
	         input.setConverter(conv);
	         
	         prop.getChildren().add(input);
         }
         else if (typeName.equalsIgnoreCase("datetime"))
         {
            UIInput input = (UIInput)context.getApplication().
                             createComponent("javax.faces.Input");
            input.setRendererType("javax.faces.Text");
            input.setValueBinding("value", vb);
            
            if (objProp.isReadOnly())
	         {
	            input.getAttributes().put("disabled", new Boolean(true));
	         }
            
            // add a converter for datetime types
            DateTimeConverter conv = (DateTimeConverter)context.getApplication().
                                      createConverter("javax.faces.DateTime");
            conv.setDateStyle("short");
            conv.setPattern("d/MM/yyyy");
            input.setConverter(conv);
            
            // add a validator if the field is required
//            if (cntrl.isRequired())
//            {
//               uiControl.setRequired(true);
//               LengthValidator val = (LengthValidator)context.getApplication().
//                                      createValidator("javax.faces.Length");
//               val.setMinimum(1);
//               uiControl.addValidator(val);
//            }

            prop.getChildren().add(input);
         }
         else if (typeName.equalsIgnoreCase("boolean"))
         {
            UISelectBoolean input = (UISelectBoolean)context.getApplication().
                                     createComponent("javax.faces.SelectBoolean");
            input.setRendererType("javax.faces.Checkbox");
            input.setValueBinding("value", vb);
            
            if (objProp.isReadOnly())
	         {
	            input.getAttributes().put("disabled", new Boolean(true));
	         }

            prop.getChildren().add(input);
         }
         else if (typeName.equalsIgnoreCase("enum"))
         {
            // TODO: Handle lists
         }
         
         // add the property definition to the list of children for this sheet
         getChildren().add(prop);
      }
   }
}
