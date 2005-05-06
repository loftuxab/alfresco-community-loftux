package org.alfresco.web.ui.common.tag.property;

import javax.faces.component.UIComponent;
import org.alfresco.web.ui.common.tag.BaseComponentTag;

/**
 * Tag to represent the combination of a PropertySheet component
 * and a Grid renderer
 * 
 * @author gavinc
 */
public class PropertySheetGridTag extends BaseComponentTag
{
   private String value;
   private String var;
   private String columns;
   private String externalConfig;
   
   /**
    * @see javax.faces.webapp.UIComponentTag#getComponentType()
    */
   public String getComponentType()
   {
      return "awc.faces.PropertySheet";
   }

   /**
    * @see javax.faces.webapp.UIComponentTag#getRendererType()
    */
   public String getRendererType()
   {
      return "javax.faces.Grid";
   }
   
   /**
    * @see javax.faces.webapp.UIComponentTag#setProperties(javax.faces.component.UIComponent)
    */
   protected void setProperties(UIComponent component)
   {
      super.setProperties(component);
      
      setStringProperty(component, "value", this.value);
      setStringStaticProperty(component, "var", this.var);
      setIntProperty(component, "columns", this.columns);
      setBooleanProperty(component, "externalConfig", this.externalConfig);
   }

   /**
    * @see javax.faces.webapp.UIComponentTag#release()
    */
   public void release()
   {
      this.value = null;
      this.var = null;
      this.columns = null;
      this.externalConfig = null;
      
      super.release();
   }

   /**
    * @param value The value to set.
    */
   public void setValue(String value)
   {
      this.value = value;
   }
   
   /**
    * @param var The var to set.
    */
   public void setVar(String var)
   {
      this.var = var;
   }

   /**
    * @param columns The columns to set.
    */
   public void setColumns(String columns)
   {
      this.columns = columns;
   }

   /**
    * @param externalConfig The externalConfig to set.
    */
   public void setExternalConfig(String externalConfig)
   {
      this.externalConfig = externalConfig;
   }   
}
