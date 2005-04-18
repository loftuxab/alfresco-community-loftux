package com.activiti.web.config.evaluator;

import javax.faces.component.UIComponentBase;

/**
 * Evaluator that tests whether an object is of a particular component type
 * 
 * @author gavinc
 */
public class ComponentTypeEvaluator implements Evaluator
{
   /**
    * Tests whether the given object has the component type defined in the condition
    * 
    * @see com.activiti.web.config.evaluator.Evaluator#applies(java.lang.Object, java.lang.String)
    */
   public boolean applies(Object obj, String condition)
   {
      boolean result = false;
      
      if (obj instanceof UIComponentBase)
      {
         String typeName = obj.getClass().getName();
         result = typeName.equalsIgnoreCase(condition);
      }
      
      return result;
   }

}
