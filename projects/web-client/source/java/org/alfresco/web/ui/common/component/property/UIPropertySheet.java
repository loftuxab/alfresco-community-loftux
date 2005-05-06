package org.alfresco.web.ui.common.component.property;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import jsftest.repository.DataDictionary;
import jsftest.repository.NodeRef;
import jsftest.repository.NodeService;
import jsftest.repository.DataDictionary.MetaData;
import jsftest.repository.DataDictionary.Property;

import org.apache.log4j.Logger;
import org.springframework.web.jsf.FacesContextUtils;

import org.alfresco.config.Config;
import org.alfresco.config.ConfigService;
import org.alfresco.config.element.PropertiesConfigElement;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.bean.repository.Repository;

/**
 * Component that represents the properties of an object
 * 
 * @author gavinc
 */
public class UIPropertySheet extends UIPanel implements NamingContainer
{
   private static Logger logger = Logger.getLogger(UIPropertySheet.class);
   private static String DEFAULT_VAR_NAME = "node";
   
   private String variable;
   private NodeRef nodeRef;
   private Node node;
   
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
         DataDictionary dd = new DataDictionary();
         MetaData metaData = dd.getMetaData(this.node.getType());
            
         if (externalConfig != null && externalConfig.booleanValue())
         {
            // configure the component using the config service
            if (logger.isDebugEnabled())
               logger.debug("Configuring property sheet using ConfigService");
            
            //ConfigService configSvc = ConfigServiceFactory.getConfigService();
            ConfigService configSvc = (ConfigService)FacesContextUtils.getRequiredWebApplicationContext(context).getBean("configService");
            Config configProps = configSvc.getConfig(this.node);
            PropertiesConfigElement propsToDisplay = (PropertiesConfigElement)configProps.getConfigElement("properties");
            List propNames = propsToDisplay.getProperties();
            
            if (logger.isDebugEnabled())
            {
               logger.debug("ConfigElement: " + propsToDisplay);
               logger.debug("Properties to render: " + propNames);
            }
            
            createComponents(context, metaData, propNames);
         }
         else
         {
            // show all the properties for the current node
            if (logger.isDebugEnabled())
               logger.debug("Configuring property sheet using meta data");   
            
            createComponents(context, metaData);
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
   }
   
   /**
    * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
    */
   public Object saveState(FacesContext context)
   {
      Object values[] = new Object[8];
      // standard component attributes are saved by the super class
      values[0] = super.saveState(context);
      values[1] = this.nodeRef;
      values[2] = this.node;
      values[3] = this.variable;
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
         String path = (String)getAttributes().get("value");
      
         if (path == null)
         {
            ValueBinding vb = getValueBinding("value");
            if (vb != null)
            {
               path = (String)vb.getValue(getFacesContext());
            }
         }
         
         this.nodeRef = NodeService.getNodeRef(path);
         // TODO: This needs refactoring - it is still using old NodeRef - should be using new one,
         //       should pass a real NodeRef into the new Node() constructor.
         this.node = new Node(new org.alfresco.repo.ref.NodeRef(Repository.getStoreRef(), ""), NodeService.getType(this.nodeRef));
         this.node.setProperties(NodeService.getProperties(this.nodeRef));
      }
      
      return node;
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
    * Creates components to represent all the properties in the given meta data object
    * 
    * @param context
    * @param metaData
    * @throws IOException
    */
   private void createComponents(FacesContext context, MetaData metaData)
   	throws IOException
   {
      Iterator iter = metaData.getProperties().iterator();
      while (iter.hasNext())
      {
         Property property = (Property)iter.next(); 
         
         // generate the label
         PropertyHelper.generateLabel(context, property.getDisplayName(), this);
         
         // generate the input control
         PropertyHelper.generateControl(context, property, this.variable, this);
      }
   }
   
   /**
    * Creates components to represent the given properties in the given meta data object
    * 
    * @param context
    * @param metaData
    * @param propNames
    * @throws IOException
    */
   private void createComponents(FacesContext context, MetaData metaData, List propNames)
      throws IOException
   {
      Iterator iter = metaData.getProperties().iterator();
      while (iter.hasNext())
      {
         Property property = (Property)iter.next();
         
         if (propNames.contains(property.getName()))
         {
            // generate the label
            PropertyHelper.generateLabel(context, property.getDisplayName(), this);
            
            // generate the input control
            PropertyHelper.generateControl(context, property, this.variable, this);
         }
      }
   }
}
