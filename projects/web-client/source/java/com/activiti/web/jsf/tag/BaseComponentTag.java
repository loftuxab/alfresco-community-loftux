/*
 * Created on Mar 4, 2005
 */
package com.activiti.web.jsf.tag;

import javax.faces.component.UIComponent;
import javax.faces.el.ValueBinding;
import javax.faces.webapp.UIComponentTag;


/**
 * @author kevinr
 */
public abstract class BaseComponentTag extends UIComponentTag
{
   /**
    * Helper method to set a String property value into the component.
    * Respects the possibility that the property value is a Value Binding.
    * 
    * @param component  UIComponent
    * @param name       property string name
    * @param value      property string value
    */
   protected void setStringProperty(UIComponent component, String name, String value)
   {
      if (value != null)
      {
         if (isValueReference(value))
         {
            ValueBinding vb = getFacesContext().getApplication().createValueBinding(value);
            component.setValueBinding(name, vb);
         }
         else
         {
            component.getAttributes().put(name, value);
         }
      }
   }
   
   /**
    * Helper method to set a String value property into the component.
    * Assumes the that the property value must be a Value Binding.
    * 
    * @param component  UIComponent
    * @param name       property string name
    * @param value      property string value binding
    */
   protected void setStringBindingProperty(UIComponent component, String name, String value)
   {
      if (value != null)
      {
         ValueBinding vb = getFacesContext().getApplication().createValueBinding(value);
         component.setValueBinding(name, vb);
      }
   }
   
   /**
    * Helper method to set a static String property into the component.
    * Assumes the that the property value can only be a static string value.
    * 
    * @param component  UIComponent
    * @param name       property string name
    * @param value      property string static value
    */
   protected void setStringStaticProperty(UIComponent component, String name, String value)
   {
      if (value != null)
      {
         component.getAttributes().put(name, value);
      }
   }
   
   /**
    * Helper method to set a String property as an Integer value into the component.
    * Respects the possibility that the property value is a Value Binding.
    * 
    * @param component  UIComponent
    * @param name       property string name
    * @param value      property string value (an Integer will be created)
    */
   protected void setIntProperty(UIComponent component, String name, String value)
   {
      if (value != null)
      {
         if (isValueReference(value))
         {
            ValueBinding vb = getFacesContext().getApplication().createValueBinding(value);
            component.setValueBinding(name, vb);
         }
         else
         {
            component.getAttributes().put(name, new Integer(value));
         }
      }
   }
   
   /**
    * Helper method to set a String property as an Boolean value into the component.
    * Respects the possibility that the property value is a Value Binding.
    * 
    * @param component  UIComponent
    * @param name       property string name
    * @param value      property string value (a Boolean will be created)
    */
   protected void setBooleanProperty(UIComponent component, String name, String value)
   {
      if (value != null)
      {
         if (isValueReference(value))
         {
            ValueBinding vb = getFacesContext().getApplication().createValueBinding(value);
            component.setValueBinding(name, vb);
         }
         else
         {
            component.getAttributes().put(name, Boolean.valueOf(value));
         }
      }
   }
}
