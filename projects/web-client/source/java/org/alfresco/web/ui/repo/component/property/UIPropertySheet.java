package org.alfresco.web.ui.repo.component.property;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import org.alfresco.config.Config;
import org.alfresco.config.ConfigLookupContext;
import org.alfresco.config.ConfigService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.config.PropertySheetConfigElement;
import org.alfresco.web.config.PropertySheetConfigElement.PropertyConfig;
import org.apache.log4j.Logger;
import org.springframework.web.jsf.FacesContextUtils;

/**
 * Component that represents the properties of a Node
 * 
 * @author gavinc
 */
public class UIPropertySheet extends UIPanel implements NamingContainer
{
   public static final String VIEW_MODE = "view";
   public static final String EDIT_MODE = "edit";
   
   private static Logger logger = Logger.getLogger(UIPropertySheet.class);
   private static String DEFAULT_VAR_NAME = "node";
   
   private String variable;
   private NodeRef nodeRef;
   private Node node;
   private Boolean readOnly;
   private String mode;
   private String configArea;
   
   /**
    * Default constructor
    */
   public UIPropertySheet()
   {
      // set the default renderer for a property sheet
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
    * @see javax.faces.component.UIComponent#encodeBegin(javax.faces.context.FacesContext)
    */
   public void encodeBegin(FacesContext context) throws IOException
   {
      String var = null;
      int howManyKids = getChildren().size();
      Boolean externalConfig = (Boolean)getAttributes().get("externalConfig");
      
      // generate a variable name to use if necessary
      if (this.variable == null)
      {
         this.variable = DEFAULT_VAR_NAME;
      }
      
      // force retrieval of node info
      getNode();
      
      if (howManyKids == 0)
      {
         // get the property names the current node has
         List<String> props = node.getPropertyNames();
         
         if (externalConfig != null && externalConfig.booleanValue())
         {
            // configure the component using the config service
            if (logger.isDebugEnabled())
               logger.debug("Configuring property sheet using ConfigService");

            // get the properties to display
            ConfigService configSvc = (ConfigService)FacesContextUtils.getRequiredWebApplicationContext(
                  context).getBean("configService");
            Config configProps = null;
            if (getConfigArea() == null)
            {
               configProps = configSvc.getConfig(this.node);
            }
            else
            {
               // only look within the given area
               configProps = configSvc.getConfig(this.node, new ConfigLookupContext(getConfigArea()));
            }
            
            PropertySheetConfigElement propsToDisplay = (PropertySheetConfigElement)configProps.
               getConfigElement("property-sheet");
            
            if (propsToDisplay != null)
            {
               List<PropertyConfig> propsToRender = propsToDisplay.getPropertiesToShow();
            
               if (logger.isDebugEnabled())
                  logger.debug("Properties to render: " + propsToDisplay.getPropertyNamesToShow());
            
               createPropertyComponentsFromConfig(context, propsToRender);
            }
            else
            {
               if (logger.isDebugEnabled())
                  logger.debug("There are no properties to render!");
            }
         }
         else
         {
            // show all the properties for the current node
            if (logger.isDebugEnabled())
               logger.debug("Configuring property sheet using node's properties");   
            
            createPropertyComponentsFromNode(context, props);
         }
      }
      
      // put the node in the session if it is not there already
      Map sessionMap = getFacesContext().getExternalContext().getSessionMap();
      sessionMap.put(this.variable, this.node);

      if (logger.isDebugEnabled())
         logger.debug("Put node into session with key '" + this.variable + "': " + this.node);

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
      this.nodeRef = (NodeRef)values[1];
      this.node = (Node)values[2];
      this.variable = (String)values[3];
      this.readOnly = (Boolean)values[4];
      this.mode = (String)values[5];
      this.configArea = (String)values[6];
   }
   
   /**
    * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
    */
   public Object saveState(FacesContext context)
   {
      Object values[] = new Object[7];
      // standard component attributes are saved by the super class
      values[0] = super.saveState(context);
      values[1] = this.nodeRef;
      values[2] = this.node;
      values[3] = this.variable;
      values[4] = this.readOnly;
      values[5] = this.mode;
      values[6] = this.configArea;
      return (values);
   }
   
   /**
    * @return Returns the node
    */
   public Node getNode()
   {
      // use the value to get hold of the actual object
      if (this.node == null)
      {
         Object value = getAttributes().get("value");
         
         if (value == null)
         {
            ValueBinding vb = getValueBinding("value");
            if (vb != null)
            {
               value = vb.getValue(getFacesContext());
            }
         }
         
         // TODO: for now we presume the object is a Node, but we need to support id's too
         if (value instanceof Node)
         {
            this.node = (Node)value;
         }
      }
      
      return this.node;
   }
   
   /**
    * @param node The node
    */
   public void setNode(Node node)
   {
      this.node = node;
   }
   
   /**
    * @return Returns the variable.
    */
   public String getVar()
   {
      return this.variable;
   }

   /**
    * @param variable The variable to set.
    */
   public void setVar(String variable)
   {
      this.variable = variable;
   }
   
   /**
    * @return Returns whether the property sheet is read only
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
    * @param readOnly Sets the read only flag for the property sheet
    */
   public void setReadOnly(boolean readOnly)
   {
      this.readOnly = Boolean.valueOf(readOnly);
   }

   /**
    * @return Returns the mode
    */
   public String getMode()
   {
      if (this.mode == null)
      {
         ValueBinding vb = getValueBinding("mode");
         if (vb != null)
         {
            this.mode = (String)vb.getValue(getFacesContext());
         }
      }
      
      if (this.mode == null)
      {
         mode = EDIT_MODE;
      }
      
      return mode;
   }

   /**
    * @param mode Sets the mode
    */
   public void setMode(String mode)
   {
      this.mode = mode;
   }
   
   /**
    * @return Returns the config area to use
    */
   public String getConfigArea()
   {
      if (this.configArea == null)
      {
         ValueBinding vb = getValueBinding("configArea");
         if (vb != null)
         {
            this.configArea = (String)vb.getValue(getFacesContext());
         }
      }
      
      return configArea;
   }
   
   /**
    * @param configArea Sets the config area to use
    */
   public void setConfigArea(String configArea)
   {
      this.configArea = configArea;
   }

   /**
    * Creates all the property components required to display the properties held by the node.
    * 
    * @param context JSF context
    * @param properties List of property names held by node 
    * @throws IOException
    */
   private void createPropertyComponentsFromNode(FacesContext context, List<String> properties)
      throws IOException
   {
      for (String propertyName : properties)
      {
         // create the property component
         UIProperty propComp = (UIProperty)context.getApplication().createComponent("org.alfresco.faces.Property");
         propComp.setId(context.getViewRoot().createUniqueId());
         propComp.setName(propertyName);
         
         // if this property sheet is set as read only, set all properties to read only
         if (isReadOnly())
         {
            propComp.setReadOnly(true);
         }
         
         // NOTE: we don't know what the display label is so don't set it
         
         this.getChildren().add(propComp);
         
         if (logger.isDebugEnabled())
            logger.debug("Created property component " + propComp + "(" + 
                   propComp.getClientId(context) + 
                   ") for '" + propertyName +
                   "' and added it to property sheet " + this);
      }
   }
   
   /**
    * Creates all the property components required to display the properties specified
    * in an external config file.
    * 
    * @param context JSF context
    * @param properties List of properties to render (driven from configuration) 
    * @throws IOException
    */
   private void createPropertyComponentsFromConfig(FacesContext context, List<PropertyConfig> properties)
      throws IOException
   {
      for (PropertyConfig property : properties)
      {
         // create the property component
         UIProperty propComp = (UIProperty)context.getApplication().createComponent("org.alfresco.faces.Property");
         propComp.setId(context.getViewRoot().createUniqueId());
         propComp.setName(property.getName());
         propComp.setDisplayLabel(property.getDisplayLabel());
         propComp.setConverter(property.getConverter());
         
         // if this property sheet is set as read only or the config says the property
         // should be read only set it as such
         if (isReadOnly() || property.isReadOnly())
         {
            propComp.setReadOnly(true);
         }
         
         this.getChildren().add(propComp);
         
         if (logger.isDebugEnabled())
            logger.debug("Created property component " + propComp + "(" + 
                   propComp.getClientId(context) + 
                   ") for '" + property.getName() + 
                   "' and added it to property sheet " + this);
      }
   }
}
